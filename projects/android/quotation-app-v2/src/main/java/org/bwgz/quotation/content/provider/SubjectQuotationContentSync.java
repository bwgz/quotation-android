/*
 * Copyright (C) 2014 bwgz.org
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as 
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bwgz.quotation.content.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bwgz.google.freebase.client.FreebaseHelper;
import org.bwgz.quotation.content.provider.QuotationContract.Source;
import org.bwgz.quotation.content.provider.QuotationContract.SubjectQuotation;

import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.api.client.json.GenericJson;
import com.google.api.services.freebase.model.TopicLookup;
import com.google.api.services.freebase.model.TopicLookup.Property;
import com.google.api.services.freebase.model.TopicValue;
import com.google.api.services.freebase.model.TopicValue.Citation;

public class SubjectQuotationContentSync extends ContentSync {
	static private final String TAG = SubjectQuotationContentSync.class.getSimpleName();

	static private final String MEDIA_COMMON_QUOTATION_AUTHOR				= "/media_common/quotation/author";
	static private final String TYPE_OBJECT_NAME							= "/type/object/name";
	static private final String MEDIA_COMMON_SOURCE							= "/media_common/quotation/source";
	static private final String MEDIA_COMMON_SPOKEN_BY_CHARACTER			= "/media_common/quotation/spoken_by_character";
	
	static private final String subjectQuotationQueryTemplate = 
			"[{ \"type\": \"/media_common/quotation\", \"mid\": null, \"name\": null, \"/media_common/quotation/spoken_by_character\": [{}], " +
			"\"/media_common/quotation/author\": [{\"mid\": null}]," +
			"\"/media_common/quotation/source\": { }, \"/media_common/quotation/subjects\": [{ \"mid\": \"%s\" }] }]";
				
	static private final String COMMON_TOPIC_DESCRIPTION					= "/common/topic/description";
	static private final String COMMON_TOPIC_IMAGE							= "/common/topic/image";
	static private final String NOTABLE_FOR									= "/common/topic/notable_for";
	static private final String PEOPLE_PERSON_QUOTATIONS					= "/people/person/quotations";
	
	static private final String[] personFilters = { TYPE_OBJECT_NAME, COMMON_TOPIC_DESCRIPTION, PEOPLE_PERSON_QUOTATIONS, COMMON_TOPIC_IMAGE, NOTABLE_FOR };

	SubjectQuotationContentSync(Context context, FreebaseHelper freebaseHelper) {
		super(context, freebaseHelper);
	}
	
	private String getSpokenByCharacter(List<Map<String, Object>> results) throws IOException {
		Log.d(TAG, String.format("getSpokenByCharacter - id: %s", results));
		String spoken_by_character = null;
		
		if (results.size() != 0) {
			Map<String, Object> result = results.get(0);
			
			spoken_by_character = getString(result, "name");
		}
		
		return spoken_by_character;
	}

	private ArrayList<ContentProviderOperation> createPersonContentProviderOperationList(String personId) throws IOException {
		ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
		
		Builder builder;
		
		TopicLookup topic = getFreebaseHelper().fetchTopic(personId, personFilters);
		Property property = topic.getProperty();
		
		List<TopicValue> values = getPropertyValues(property, TYPE_OBJECT_NAME);
		TopicValue value = values.get(0);
		String name = value.getValue().toString();

		String notable_for = null;
		values = getPropertyValues(property, NOTABLE_FOR);
		if (values != null) {
			value = values.get(0);
			notable_for = value.getText().toString();
		}

		String image_id = null;
		values = getPropertyValues(property, COMMON_TOPIC_IMAGE);
		if (values != null) {
			value = values.get(0);
			image_id = value.getId().toString();
		}
		
		String description = null;
		String language = null;
		Citation citation = null;
		
		values = getPropertyValues(property, COMMON_TOPIC_DESCRIPTION);
		if (values != null) {
			value = values.get(0);
			description = value.getValue().toString();
			language = value.getLang();
			citation = value.getCitation();
		}
		
		long quotation_count = 0;
		values = getPropertyValues(property, PEOPLE_PERSON_QUOTATIONS);
		if (values != null) {
			quotation_count = values.size();
		}

		builder = createPersonNewInsertBuilder(personId, name, image_id, description, notable_for, citation, language, quotation_count);
		operationList.add(builder.build());
		
		return operationList;
	}

	public void createSubjectQuotationContent(Uri uri) {
		Log.d(TAG, String.format("createSubjectQuotationContent - uri: %s", uri));
		
		String subjectId = SubjectQuotation.getId(uri);
		String query = String.format(subjectQuotationQueryTemplate, subjectId);
		String cursor = new String();
		
		long count = 0;
		for (;;) {
			try {
				GenericJson json = getFreebaseHelper().fetchQuery(query, cursor);
				List<Map<String, Object>> result = (List<Map<String, Object>>) json.get("result");
				
				if (result == null) {
					break;
				}
				else {
					ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
					count += result.size();
					Log.d(TAG, String.format("createSubjectQuotationContent - result.size(): %d  count: %d", result.size(), count));
					operationList.add(createSubjectNewUpdateQuotationCountBuilder(subjectId, count).build());
					
					int chunkSize = 10;
					int items = 0;
					
					for (Map<String, Object> map : result) {
						String quotationId = getString(map, "mid");	
						String name = getString(map, "name");
						String language = "en";
						long author_count = 0;
						String sourceId = null;
						String spoken_by_character = null;
						
						Log.d(TAG, String.format("createPersonQuotationContent - quotationId: %s", quotationId));
						
						Object object = map.get(MEDIA_COMMON_SPOKEN_BY_CHARACTER);
						if (object instanceof List) {
							spoken_by_character = getSpokenByCharacter((List<Map<String, Object>>) object);
						}
						
						Object source = map.get(MEDIA_COMMON_SOURCE);
						if (source instanceof Map) {
							String id = getString((Map<String, Object>) source, "id");
							sourceId = new SourceContentSync(getContext(), getFreebaseHelper()).createSourceContent(Source.withAppendedId(id));
						}
						
						operationList.add(createQuotationNewInsertBuilder(quotationId, name, language, author_count, sourceId, spoken_by_character).build());
						operationList.add(createSubjectQuotationNewInsertBuilder(subjectId, quotationId).build());
						
						Object authors = map.get(MEDIA_COMMON_QUOTATION_AUTHOR);
						if (authors instanceof List) {
							for (Map<String, Object> author : (List<Map<String, Object>>) authors) {
								 String personId = (String) author.get("mid");
								 ArrayList<ContentProviderOperation> list = createPersonContentProviderOperationList(personId);
								 operationList.addAll(list);
								 operationList.add(createPersonQuotationNewInsertBuilder(personId, quotationId).build());
							}
						}

						items++;
						if (items == chunkSize) {
							applyBatch(getContext(), operationList);
							chunkSize = 25;
							items = 0;
						}
					}
				
					if (operationList.size() != 0) {
						applyBatch(getContext(), operationList);
					}
				
					Object object = json.get("cursor");
					if (object instanceof String) {
						cursor = (String) object;
					}
					else {
						break;
					}
				}
			} catch (IOException e) {
				Log.e(TAG, e.getLocalizedMessage());
				break;
			}
		}
	}
}
