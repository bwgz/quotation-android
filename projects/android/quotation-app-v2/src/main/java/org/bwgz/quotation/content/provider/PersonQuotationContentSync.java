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
import org.bwgz.quotation.content.provider.QuotationContract.PersonQuotation;
import org.bwgz.quotation.content.provider.QuotationContract.Source;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.api.client.json.GenericJson;

public class PersonQuotationContentSync extends ContentSync {
	static private final String TAG = PersonQuotationContentSync.class.getSimpleName();

	static private final String MEDIA_COMMON_QUOTATION_AUTHOR				= "/media_common/quotation/author";
	static private final String TYPE_OBJECT_NAME							= "/type/object/name";
	static private final String TYPE_OBJECT_TYPE							= "/type/object/type";
	static private final String MEDIA_COMMON_SOURCE							= "/media_common/quotation/source";
	static private final String MEDIA_COMMON_SPOKEN_BY_CHARACTER			= "/media_common/quotation/spoken_by_character";
	
	static private final String peopleQuotationQueryTemplate = 
			"[{ \"type\": \"/media_common/quotation\", \"mid\": null, \"name\": null, \"/media_common/quotation/spoken_by_character\": [{}], \"/media_common/quotation/source\": { }, \"/media_common/quotation/author\": [{ \"mid\": \"%s\" }] }]";
				
	static private final String sourceQuotationQueryTemplate = "[{ \"id\": \"%s\", \"mid\": null, \"name\": null, \"type\": [{}] }]";

	PersonQuotationContentSync(Context context, FreebaseHelper freebaseHelper) {
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
	
	public void createPersonQuotationContent(Uri uri) {
		Log.d(TAG, String.format("createPersonQuotationContent - uri: %s", uri));
		
		String personId = PersonQuotation.getId(uri);
		String query = String.format(peopleQuotationQueryTemplate, personId);
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
					Log.d(TAG, String.format("createPersonQuotationContent - result.size(): %d  count: %d", result.size(), count));
					operationList.add(createPersonNewUpdateQuotationCountBuilder(personId, count).build());
					
					int chunkSize = 10;
					int items = 0;
					
					for (Map<String, Object> map : result) {
						String quotationId = getString(map, "mid");	
						String name = getString(map, "name");
						String language = "en";
						long author_count = 1;
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
						operationList.add(createPersonQuotationNewInsertBuilder(personId, quotationId).build());
						
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
