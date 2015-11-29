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
import org.bwgz.quotation.content.provider.QuotationContract.Person;
import org.bwgz.quotation.content.provider.QuotationContract.PersonQuotation;
import org.bwgz.quotation.content.provider.QuotationContract.PickPerson;
import org.bwgz.quotation.content.provider.QuotationContract.PickQuotation;
import org.bwgz.quotation.content.provider.QuotationContract.PickSubject;
import org.bwgz.quotation.content.provider.QuotationContract.Quotation;
import org.bwgz.quotation.content.provider.QuotationContract.QuotationPerson;
import org.bwgz.quotation.content.provider.QuotationContract.QuotationSubject;
import org.bwgz.quotation.content.provider.QuotationContract.Source;
import org.bwgz.quotation.content.provider.QuotationContract.Subject;
import org.bwgz.quotation.content.provider.QuotationContract.SubjectQuotation;
import org.bwgz.quotation.widget.picks.PickView;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.google.api.client.json.GenericJson;
import com.google.api.services.freebase.model.TopicLookup;
import com.google.api.services.freebase.model.TopicLookup.Property;
import com.google.api.services.freebase.model.TopicPropertyValue;
import com.google.api.services.freebase.model.TopicValue;
import com.google.api.services.freebase.model.TopicValue.Citation;

public class QuotationSyncAdapter extends AbstractThreadedSyncAdapter {
	static private final String TAG = QuotationSyncAdapter.class.getSimpleName();

	public static final String SYNC_EXTRA_URI = "sync.extra.uri";
	
	static private final String COMMON_TOPIC_DESCRIPTION					= "/common/topic/description";
	static private final String COMMON_TOPIC_IMAGE							= "/common/topic/image";
	static private final String MEDIA_COMMON_QUOTATION_AUTHOR				= "/media_common/quotation/author";
	static private final String NOTABLE_FOR									= "/common/topic/notable_for";
	static private final String PEOPLE_PERSON_QUOTATIONS					= "/people/person/quotations";
	static private final String TYPE_OBJECT_NAME							= "/type/object/name";
	static private final String TYPE_OBJECT_TYPE							= "/type/object/type";
	static private final String MEDIA_COMMON_QUOTATION_SUBJECTS				= "/media_common/quotation/subjects";
	static private final String MEDIA_COMMON_QUOTATION_SUBJECTS_QUOTATIONS	= "/media_common/quotation_subject/quotations_about_this_subject";
	static private final String MEDIA_COMMON_SOURCE							= "/media_common/quotation/source";
	static private final String MEDIA_COMMON_SPOKEN_BY_CHARACTER			= "/media_common/quotation/spoken_by_character";
	
	private static final String[] quotationFilters = { TYPE_OBJECT_NAME, MEDIA_COMMON_QUOTATION_AUTHOR, MEDIA_COMMON_SOURCE, MEDIA_COMMON_SPOKEN_BY_CHARACTER };
	static private final String[] personFilters = { TYPE_OBJECT_NAME, COMMON_TOPIC_DESCRIPTION, PEOPLE_PERSON_QUOTATIONS, COMMON_TOPIC_IMAGE, NOTABLE_FOR };
	static private final String[] subjectFilters = { TYPE_OBJECT_NAME, COMMON_TOPIC_DESCRIPTION, MEDIA_COMMON_QUOTATION_SUBJECTS_QUOTATIONS, COMMON_TOPIC_IMAGE };
	static private final String[] sourceFilters = { TYPE_OBJECT_NAME, TYPE_OBJECT_TYPE };

	private static final String[] quotationPersonFilters = { MEDIA_COMMON_QUOTATION_AUTHOR };
	private static final String[] quotationSubjectFilters = { MEDIA_COMMON_QUOTATION_SUBJECTS };

	private static final int IMAGE_WIDTH_DP		= 200;
	private static final int IMAGE_HEIGHT_DP	= 200;
	
	private FreebaseHelper freebaseHelper;
	private int imageWidth;
	private int imageHeight;

	public QuotationSyncAdapter(Context context, boolean autoInitialize, FreebaseHelper freebaseHelper) {
		super(context, autoInitialize);
		Log.d(TAG, String.format("QuotationSyncAdapter - context: %s  autoInitialize: %s", context, autoInitialize));
		
		this.freebaseHelper = freebaseHelper;
		
        imageWidth = (int) PickView.convertDpToPixel(getContext(), IMAGE_WIDTH_DP);
        imageHeight = (int) PickView.convertDpToPixel(getContext(), IMAGE_HEIGHT_DP);
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	private Builder createQuotationNewInsertBuilder(String id, String quotation, String language, long author_count, String sourceId, String spoken_by_character) {
		Log.d(TAG, String.format("createQuotationNewInsertBuilder - id: %s  quotation: %s  language: %s  source: %s  spoken_by_character: %s", id, quotation, language, sourceId, spoken_by_character));
		Builder builder = ContentProviderOperation.newInsert(Quotation.withAppendedId(id));
		builder.withValue(Quotation._ID, id);
		builder.withValue(Quotation.QUOTATION, quotation);
		builder.withValue(Quotation.LANGUAGE, language);
		builder.withValue(Quotation.AUTHOR_COUNT, author_count);
		builder.withValue(Quotation.SOURCE_ID, sourceId);
		builder.withValue(Quotation.SPOKEN_BY_CHARACTER, spoken_by_character);
		
		return builder;
	}

	private Builder createPersonNewInsertBuilder(String id, String name, String image_id, String description, String notable_for, Citation citation, String language, long quotation_count) {
		Builder builder = ContentProviderOperation.newInsert(Person.withAppendedId(id));
		builder.withValue(Person._ID, id);
		builder.withValue(Person.NAME, name);
		builder.withValue(Person.LANGUAGE, language);
		if (image_id != null) {
			builder.withValue(Person.IMAGE_ID, image_id);
		}
		if (description != null) {
			builder.withValue(Person.DESCRIPTION, description);
		}
		if (notable_for != null) {
			builder.withValue(Person.NOTABLE_FOR, notable_for);
		}
		if (citation != null) {
			builder.withValue(Person.CITATION_PROVIDER, citation.getProvider());
			builder.withValue(Person.CITATION_STATEMENT, citation.getStatement());
			builder.withValue(Person.CITATION_URI, citation.getUri());
		}
		builder.withValue(Person.QUOTATION_COUNT, quotation_count);
		
		return builder;
	}

	private Builder createPersonNewUpdateQuotationCountBuilder(String id, long quotation_count) {
		Builder builder = ContentProviderOperation.newUpdate(Person.withAppendedId(id));
		builder.withValue(Person.QUOTATION_COUNT, quotation_count);
		
		return builder;
	}

	private Builder createQuotationPersonNewInsertBuilder(String quotationId, String personId) {
		Builder builder = ContentProviderOperation.newInsert(QuotationPerson.withAppendedId(quotationId));
		builder.withValue(QuotationPerson.QUOTATION_ID, quotationId);
		builder.withValue(QuotationPerson.PERSON_ID, personId);
		
		return builder;
	}
	
	private Builder createPersonQuotationNewInsertBuilder(String personId, String quotationId) {
		Builder builder = ContentProviderOperation.newInsert(PersonQuotation.withAppendedId(personId));
		builder.withValue(QuotationPerson.PERSON_ID, personId);
		builder.withValue(QuotationPerson.QUOTATION_ID, quotationId);
		
		return builder;
	}
	
	private Builder createSubjectNewInsertBuilder(String id, String name, String image_id, String description, String language, long quotation_count) {
		Builder builder = ContentProviderOperation.newInsert(Subject.withAppendedId(id));
		builder.withValue(Subject._ID, id);
		builder.withValue(Subject.NAME, name);
		builder.withValue(Subject.LANGUAGE, language);
		if (image_id != null) {
			builder.withValue(Subject.IMAGE_ID, image_id);
		}
		if (description != null) {
			builder.withValue(Subject.DESCRIPTION, description);
		}
		builder.withValue(Subject.QUOTATION_COUNT, quotation_count);
		
		return builder;
	}

	private Builder createSubjectNewUpdateQuotationCountBuilder(String id, long quotation_count) {
		Builder builder = ContentProviderOperation.newUpdate(Subject.withAppendedId(id));
		builder.withValue(Person.QUOTATION_COUNT, quotation_count);
		
		return builder;
	}

	private Builder createQuotationSubjectNewInsertBuilder(String quotationId, String subjectId) {
		Builder builder = ContentProviderOperation.newInsert(QuotationSubject.withAppendedId(quotationId));
		builder.withValue(QuotationSubject.QUOTATION_ID, quotationId);
		builder.withValue(QuotationSubject.SUBJECT_ID, subjectId);
		
		return builder;
	}
	
	private Builder createSubjectQuotationNewInsertBuilder(String subjectId, String quotationId) {
		Builder builder = ContentProviderOperation.newInsert(SubjectQuotation.withAppendedId(subjectId));
		builder.withValue(QuotationSubject.SUBJECT_ID, subjectId);
		builder.withValue(QuotationSubject.QUOTATION_ID, quotationId);
		
		return builder;
	}

	private Builder createSourceNewInsertBuilder(String id, String name, String type) {
		Log.d(TAG, String.format("createQuotationNewInsertBuilder - id: %s  name: %s  type: %s", id, name, type));
		Builder builder = ContentProviderOperation.newInsert(Source.withAppendedId(id));
		builder.withValue(Source._ID, id);
		builder.withValue(Source.NAME, name);
		builder.withValue(Source.TYPE, type);
		
		return builder;
	}

	private Builder createPickQuotationNewInsertBuilder(String id) {
		Builder builder = ContentProviderOperation.newInsert(PickQuotation.withAppendedId(id));
		builder.withValue(PickQuotation.PICK_ID, id);
		
		return builder;
	}

	private Builder createPickPersonNewInsertBuilder(String id) {
		Builder builder = ContentProviderOperation.newInsert(PickPerson.withAppendedId(id));
		builder.withValue(PickPerson.PICK_ID, id);
		
		return builder;
	}

	private Builder createPickSubjectNewInsertBuilder(String id) {
		Builder builder = ContentProviderOperation.newInsert(PickSubject.withAppendedId(id));
		builder.withValue(PickSubject.PICK_ID, id);
		
		return builder;
	}

	/* 
	 * Freebase Requests
	 */
	
	private List<TopicValue> getPropertyValues(Property property, String name) {
		List<TopicValue> values = null;

		if (property != null) {
			TopicPropertyValue topicPropertyValue = (TopicPropertyValue) property.get(name);
			if (topicPropertyValue != null) {
				values = topicPropertyValue.getValues();
			}
		}
		
		return values;
	}
	
	private ArrayList<ContentProviderOperation> createQuotationContentProviderOperationList(Uri uri, boolean flag) throws IOException {
		Log.d(TAG, String.format("createQuotationContentProviderOperationList - uri: %s", uri));
		ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
		
		String quotationId = Quotation.getId(uri);
		TopicLookup topic = freebaseHelper.fetchTopic(quotationId, quotationFilters);
		
		Property property = topic.getProperty();
		List<TopicValue> values = getPropertyValues(property, TYPE_OBJECT_NAME);
		TopicValue value = values.get(0);
		String name = value.getValue().toString();
		String language = value.getLang();
		
		String sourceId = null;
		values = getPropertyValues(property, MEDIA_COMMON_SOURCE);
		if (values != null) {
			value = values.get(0);
			sourceId = value.getId();
			
			operationList.addAll(createSourceContentProviderOperationList(Source.withAppendedId(sourceId)));
		}
		
		String spoken_by_character = null;
		values = getPropertyValues(property, MEDIA_COMMON_SPOKEN_BY_CHARACTER);
		if (values != null) {
			value = values.get(0);
			spoken_by_character = value.getText();
		}
		
		long author_count = 0;
		values = getPropertyValues(property, MEDIA_COMMON_QUOTATION_AUTHOR);
		if (values != null) {
			author_count = values.size();
		}
		
		Builder builder = createQuotationNewInsertBuilder(quotationId, name, language, author_count, sourceId, spoken_by_character);
		operationList.add(builder.build());
		
		if (flag) {
			if (values != null) {
				operationList.addAll(createQuotationPersonContentProviderOperationList(quotationId, values));
			}
		}
		
		return operationList;
	}

	private ArrayList<ContentProviderOperation> createPersonContentProviderOperationList(Uri uri, boolean flag) throws IOException {
		ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
		
		Builder builder;
		String personId = Person.getId(uri);
		
		TopicLookup topic = freebaseHelper.fetchTopic(personId, personFilters);
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
		
		if (flag) {
			if (values != null) {
				for (TopicValue topicValue : values) {
					String quotationId = topicValue.getId();
					
					operationList.addAll(createQuotationContentProviderOperationList(Quotation.withAppendedId(quotationId), false));
					operationList.add(createPersonQuotationNewInsertBuilder(personId, quotationId).build());
				}
			}
		}
		return operationList;
	}

	private ArrayList<ContentProviderOperation> createSubjectContentProviderOperationList(Uri uri) throws IOException {
		ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
		
		Builder builder;
		String subjectId = Subject.getId(uri);
		
		TopicLookup topic = freebaseHelper.fetchTopic(subjectId, subjectFilters);
		Property property = topic.getProperty();
		
		List<TopicValue> values = getPropertyValues(property, TYPE_OBJECT_NAME);
		TopicValue value = values.get(0);
		String name = value.getValue().toString();
		
		String image_id = null;
		values = getPropertyValues(property, COMMON_TOPIC_IMAGE);
		if (values != null) {
			value = values.get(0);
			image_id = value.getId().toString();
		}
		
		String description = null;
		String language = null;
		values = getPropertyValues(property, COMMON_TOPIC_DESCRIPTION);
		if (values != null) {
			value = values.get(0);
			description = value.getValue().toString();
			language = value.getLang();
		}

		long quotation_count = 0;
		values = getPropertyValues(property, MEDIA_COMMON_QUOTATION_SUBJECTS_QUOTATIONS);
		if (values != null) {
			quotation_count = values.size();
		}

		builder = createSubjectNewInsertBuilder(subjectId, name, image_id, description, language, quotation_count);
		operationList.add(builder.build());
		
		return operationList;
	}

	private ArrayList<ContentProviderOperation> createQuotationPersonContentProviderOperationList(String quotationId, List<TopicValue> values) throws IOException {
		Log.d(TAG, String.format("createQuotationPersonContentProviderOperationList - values: %s", values));
		ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

		for (TopicValue value : values) {
			String personId = value.getId();
			
			operationList.addAll(createPersonContentProviderOperationList(Person.withAppendedId(personId), false));
			operationList.add(createQuotationPersonNewInsertBuilder(quotationId, personId).build());
		}

		return operationList;
	}
	
	private ArrayList<ContentProviderOperation> createQuotationPersonContentProviderOperationList(Uri uri) throws IOException {
		Log.d(TAG, String.format("createQuotationPersonContentProviderOperationList - uri: %s", uri));
		ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
		
		String quotationId = QuotationPerson.getId(uri);
		TopicLookup topic = freebaseHelper.fetchTopic(quotationId, quotationPersonFilters);
		
		Property property = topic.getProperty();
		if (property != null) {
			List<TopicValue> values = getPropertyValues(property, MEDIA_COMMON_QUOTATION_AUTHOR);
			if (values != null) {
				operationList.addAll(createQuotationPersonContentProviderOperationList(quotationId, values));
			}
		}
		
		return operationList;
	}

	private void dumpContentProviderResults(ContentProviderResult[] results) {
			for (ContentProviderResult result : results) {
				Log.d(TAG, String.format("result: %s", result));
			}
	}
	
	private boolean applyBatch(ArrayList<ContentProviderOperation> operationList) {
		boolean result = false;
		
		try {
			ContentProviderResult[] results = getContext().getContentResolver().applyBatch(QuotationContract.AUTHORITY, operationList);
			dumpContentProviderResults(results);
			result = true;
		} catch (RemoteException e) {
			Log.e(TAG, e.getLocalizedMessage());
		} catch (OperationApplicationException e) {
			Log.e(TAG, e.getLocalizedMessage());
		}
		
		return result;
	}
	
	private ArrayList<ContentProviderOperation> createQuotationSubjectContentProviderOperationList(Uri uri) throws IOException {
		Log.d(TAG, String.format("createQuotationSubjectContentProviderOperationList - uri: %s", uri));
		ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
		
		String quotationId = QuotationSubject.getId(uri);
		TopicLookup topic = freebaseHelper.fetchTopic(quotationId, quotationSubjectFilters);
		
		Property property = topic.getProperty();
		if (property != null) {
			TopicPropertyValue topicPropertyValue = (TopicPropertyValue) property.get(MEDIA_COMMON_QUOTATION_SUBJECTS);
			if (topicPropertyValue != null) {
				for (TopicValue topicValue : topicPropertyValue.getValues()) {
					String subjectId = topicValue.getId();
					
					operationList.addAll(createSubjectContentProviderOperationList(Subject.withAppendedId(subjectId)));
					operationList.add(createQuotationSubjectNewInsertBuilder(quotationId, subjectId).build());
				}
			}
		}
		
		return operationList;
	}

	static private final String subjectQuotationQueryTemplate = 
			"[{"											+
				"\"type\": \"/media_common/quotation\","	+ 
				"\"mid\":  null,"							+ 
				"\"/media_common/quotation/subjects\": [{"	+
					"\"mid\": \"%s\""						+
				"}]"										+
			"}]";		  
			
	private ArrayList<ContentProviderOperation> createSubjectQuotationContentProviderOperationList(Uri uri) throws IOException {
		Log.d(TAG, String.format("createSubjectQuotationContentProviderOperationList - uri: %s", uri));
		ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
		
		String subjectId = SubjectQuotation.getId(uri);
		String query = String.format(subjectQuotationQueryTemplate, subjectId);
		
		String cursor = new String();
		
		long count = 0;
		for (;;) {
			GenericJson json = freebaseHelper.fetchQuery(query, cursor);
			List<Map<String, ?>> result = (List<Map<String, ?>>) json.get("result");
			if (result != null) {
				count += result.size();
				for (Map<String, ?> map : result) {
					String quotationId = (String) map.get("mid");				
					operationList.addAll(createQuotationContentProviderOperationList(Quotation.withAppendedId(quotationId), false));
					operationList.add(createSubjectQuotationNewInsertBuilder(subjectId, quotationId).build());
				}
				
				Object object = json.get("cursor");
				if (object instanceof String) {
					cursor = (String) object;
				}
				else {
					break;
				}
			}
		}
		
		operationList.add(createSubjectNewUpdateQuotationCountBuilder(subjectId, count).build());
		
		return operationList;
	}

	private ArrayList<ContentProviderOperation> createSourceContentProviderOperationList(Uri uri) throws IOException {
		Log.d(TAG, String.format("createSourceContentProviderOperationList - uri: %s", uri));
		ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
		
		String sourceId = Source.getId(uri);
		TopicLookup topic = freebaseHelper.fetchTopic(sourceId, sourceFilters);
		
		Property property = topic.getProperty();
		List<TopicValue> values = getPropertyValues(property, TYPE_OBJECT_NAME);
		TopicValue value = values.get(0);
		String name = value.getValue().toString();
		
		String type = null;
		values = getPropertyValues(property, TYPE_OBJECT_TYPE);
		if (values != null) {
			type = values.get(0).getText();
		}
		
		Builder builder = createSourceNewInsertBuilder(sourceId, name, type);
		operationList.add(builder.build());
		
		return operationList;
	}
	
	private ArrayList<ContentProviderOperation> createPickQuotationContentProviderOperationList(Uri uri) throws IOException {
		ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
		
		String id = PickQuotation.getId(uri);
					
		operationList.addAll(createQuotationContentProviderOperationList(Quotation.withAppendedId(id), true));
		operationList.add(createPickQuotationNewInsertBuilder(id).build());

		return operationList;
	}

	private ArrayList<ContentProviderOperation> createPickPersonContentProviderOperationList(Uri uri) throws IOException {
		ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
		
		String id = PickPerson.getId(uri);
					
		operationList.addAll(createPersonContentProviderOperationList(Person.withAppendedId(id), false));
		operationList.add(createPickPersonNewInsertBuilder(id).build());

		return operationList;
	}

	private ArrayList<ContentProviderOperation> createPickSubjectContentProviderOperationList(Uri uri) throws IOException {
		ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
		
		String id = PickSubject.getId(uri);
					
		operationList.addAll(createSubjectContentProviderOperationList(Subject.withAppendedId(id)));
		operationList.add(createPickSubjectNewInsertBuilder(id).build());

		return operationList;
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
		Log.d(TAG, String.format("onPerformSync - account: %s  extras: %s  authority: %s  provider: %s  syncResult: %s", account, extras, authority, provider, syncResult));

		String string = extras.getString(SYNC_EXTRA_URI);
		if (string != null) {
			Log.d(TAG, String.format("onPerformSync - uri: %s", string));
			Uri uri = Uri.parse(string);
			
			if (uri != null) {
				int match = QuotationContentProvider.getUriMatcher().match(uri);
				
				if (match == QuotationContentProvider.PERSON_QUOTATION_ID) {
					new PersonQuotationContentSync(getContext(), freebaseHelper).createPersonQuotationContent(uri);
				}
				else if (match == QuotationContentProvider.SUBJECT_QUOTATION_ID) {
					new SubjectQuotationContentSync(getContext(), freebaseHelper).createSubjectQuotationContent(uri);
				}
				else {
					try {
						ArrayList<ContentProviderOperation> operationList = null;
						
				        switch (match)
				        {
				            case QuotationContentProvider.QUOTATION_ID:
				            	operationList = createQuotationContentProviderOperationList(uri, true);
				            	break;
				            case QuotationContentProvider.QUOTATION_PERSON_ID:
				            	operationList = createQuotationPersonContentProviderOperationList(uri);
				            	break;
				            case QuotationContentProvider.QUOTATION_SUBJECT_ID:
				            	operationList = createQuotationSubjectContentProviderOperationList(uri);
				            	break;
				            case QuotationContentProvider.PERSON_ID:
								operationList = createPersonContentProviderOperationList(uri, false);
				            	break;
				            case QuotationContentProvider.SUBJECT_ID:
								operationList = createSubjectContentProviderOperationList(uri);
				            	break;
				            case QuotationContentProvider.SUBJECT_QUOTATION_ID:
								operationList = createSubjectQuotationContentProviderOperationList(uri);
				            	break;
				            case QuotationContentProvider.PICK_QUOTATION_ID:
								operationList = createPickQuotationContentProviderOperationList(uri);
				            	break;
				            case QuotationContentProvider.PICK_PERSON_ID:
								operationList = createPickPersonContentProviderOperationList(uri);
				            	break;
				            case QuotationContentProvider.PICK_SUBJECT_ID:
								operationList = createPickSubjectContentProviderOperationList(uri);
				            	break;
				        }
		
						if (operationList != null) {
							ContentProviderResult[] results;
							results = getContext().getContentResolver().applyBatch(QuotationContract.AUTHORITY, operationList);
							for (ContentProviderResult result : results) {
								Log.d(TAG, String.format("result: %s", result));
							}
						}
					} catch (Exception e) {
						Log.e(TAG, String.format("cannot sync %s", uri), e);
					}
				}
			}
		}
	}
}
