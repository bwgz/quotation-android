/*
 * Copyright (C) 2013 bwgz.org
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

import org.bwgz.google.api.services.freebase.model.ValueType;
import org.bwgz.google.api.services.freebase.util.TopicUtil;
import org.bwgz.google.freebase.client.FreebaseHelper;
import org.bwgz.quotation.content.provider.QuotationContract.Person;
import org.bwgz.quotation.content.provider.QuotationContract.Quotation;
import org.bwgz.quotation.content.provider.QuotationContract.QuotationPerson;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.api.services.freebase.model.TopicLookup;
import com.google.api.services.freebase.model.TopicValue;
import com.google.api.services.freebase.model.TopicValue.Citation;

public class QuotationSyncAdapter extends AbstractThreadedSyncAdapter {
	static private final String TAG = QuotationSyncAdapter.class.getSimpleName();

	static private final String TYPE_OBJECT_NAME				= "/type/object/name";
	static private final String COMMON_TOPIC_DESCRIPTION		= "/common/topic/description";
	static private final String COMMON_TOPIC_IMAGE				= "/common/topic/image";
	static private final String PEOPLE_PERSON_QUOTATIONS		= "/people/person/quotations";
	static private final String MEDIA_COMMON_QUOTATION_AUTHOR	= "/media_common/quotation/author";
	
	static private final String[] personFilters = { TYPE_OBJECT_NAME, COMMON_TOPIC_DESCRIPTION, PEOPLE_PERSON_QUOTATIONS, COMMON_TOPIC_IMAGE };
	private static final String[] quotationFilters = { TYPE_OBJECT_NAME, MEDIA_COMMON_QUOTATION_AUTHOR };

	public static final String SYNC_EXTRAS_URI = "uri";

	private FreebaseHelper freebaseHelper;

	public QuotationSyncAdapter(Context context, boolean autoInitialize, FreebaseHelper freebaseHelper) {
		super(context, autoInitialize);
		Log.d(TAG, String.format("QuotationSyncAdapter - context: %s  autoInitialize: %s  freebaseHelper: %s", context, autoInitialize, freebaseHelper));
		
		this.freebaseHelper = freebaseHelper;
	}
	
	private Builder createQuotationNewInsertBuilder(String id, String quotation, String language) {
		Builder builder = ContentProviderOperation.newInsert(Quotation.withAppendedId(id));
		builder.withValue(Quotation._ID, id);
		builder.withValue(Quotation.QUOTATION, quotation);
		builder.withValue(Quotation.LANGUAGE, language);
		
		return builder;
	}
	
	private Builder createPersonNewInsertBuilder(String id, String name, byte[] image, String description, Citation citation, String language) {
		Builder builder = ContentProviderOperation.newInsert(Person.withAppendedId(id));
		builder.withValue(Person._ID, id);
		builder.withValue(Person.NAME, name);
		builder.withValue(Person.LANGUAGE, language);
		if (image != null) {
			builder.withValue(Person.IMAGE, image);
		}
		if (description != null) {
			builder.withValue(Person.DESCRIPTION, description);
		}
		if (citation != null) {
			builder.withValue(Person.CITATION_PROVIDER, citation.getProvider());
			builder.withValue(Person.CITATION_STATEMENT, citation.getStatement());
			builder.withValue(Person.CITATION_URI, citation.getUri());
		}
		
		return builder;
	}

	private Builder createQuotationPersonNewInsertBuilder(String quotationId, String personId) {
		Builder builder = ContentProviderOperation.newInsert(QuotationContract.QuotationPerson.CONTENT_URI);
		builder.withValue(QuotationPerson.QUOTATION_ID, quotationId);
		builder.withValue(QuotationPerson.PERSON_ID, personId);
		
		return builder;
	}
	
	private ArrayList<ContentProviderOperation> createQuotationContentProviderOperationList(Uri uri) throws IOException {
		ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
		
		Builder builder;
		String quotationId = Quotation.getId(uri);
		
		TopicLookup topic = freebaseHelper.fetchTopic(quotationId, quotationFilters);
		
		String name = (String) TopicUtil.getFirstPropertyValue(topic, TYPE_OBJECT_NAME);
		String language = (String) TopicUtil.getPropertyValue(topic, TYPE_OBJECT_NAME, 0, "lang");
		builder = createQuotationNewInsertBuilder(quotationId, name, language);
		
		List<TopicValue> authors = TopicUtil.getPropertyValues(topic, MEDIA_COMMON_QUOTATION_AUTHOR);
		if (authors != null) {
			String valueType = TopicUtil.getPropertyValueType(topic, MEDIA_COMMON_QUOTATION_AUTHOR);
			String key = ValueType.valueOf(valueType.toUpperCase()).getKey();
			
			for (TopicValue author : authors) {
				String personId = (String) author.get(key);
				
				operationList.addAll(createPersonContentProviderOperationList(Person.withAppendedId(personId)));
			}
			
			operationList.add(builder.build());
			for (TopicValue author : authors) {
				String personId = (String) author.get(key);
				operationList.add(createQuotationPersonNewInsertBuilder(quotationId, personId).build());
			}

		}
		else {
			operationList.add(builder.build());
		}
		
		return operationList;
	}

	private ArrayList<ContentProviderOperation> createPersonContentProviderOperationList(Uri uri) throws IOException {
		ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
		
		Builder builder;
		String personId = Person.getId(uri);
		
		TopicLookup topic = freebaseHelper.fetchTopic(personId, personFilters);
		
		String name = (String) TopicUtil.getFirstPropertyValue(topic, TYPE_OBJECT_NAME);
		
		byte[] image= null;
		String description = null;
		String language = null;
		Citation citation = null;
		
		if (TopicUtil.getFirstPropertyValue(topic, COMMON_TOPIC_IMAGE) != null) {
			image = freebaseHelper.fetchImage(personId, 200, 200);
		}
		
		List<TopicValue> values = TopicUtil.getPropertyValues(topic, COMMON_TOPIC_DESCRIPTION);
		if (values != null) {
			String valueType = TopicUtil.getPropertyValueType(topic, COMMON_TOPIC_DESCRIPTION);
		
			for (TopicValue value : values) {
				String key = ValueType.valueOf(valueType.toUpperCase()).getKey();
				description = (String) value.get(key);
				language = (String) value.get("lang");
				citation = value.getCitation();
				
				break; // just want one
			}
		}
		
		builder = createPersonNewInsertBuilder(personId, name, image, description, citation, language);
		operationList.add(builder.build());
		
		values = TopicUtil.getPropertyValues(topic, PEOPLE_PERSON_QUOTATIONS);
		if (values != null) {
			String valueType = TopicUtil.getPropertyValueType(topic, PEOPLE_PERSON_QUOTATIONS);
			
			for (TopicValue value : values) {
				String key = ValueType.valueOf(valueType.toUpperCase()).getKey();
				String quotationId = (String) value.get(key);
				String text = value.getText();
				language = (String) value.get("lang");
				
				builder = createQuotationNewInsertBuilder(quotationId, text, language);
				operationList.add(builder.build());
				
				builder = createQuotationPersonNewInsertBuilder(quotationId, personId);
				operationList.add(builder.build());
			}
		}

		return operationList;
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
		Log.d(TAG, String.format("onPerformSync - account: %s  extras: %s  authority: %s  provider: %s  syncResult: %s", account, extras, authority, provider, syncResult));

		/*
		 * Debugging only.
		for (String key : extras.keySet()) {
			Object value = extras.get(key);
			Log.d(TAG, String.format("extra - %s: %s", key, value));
		}
		 */
		
		String uri = extras.getString(SYNC_EXTRAS_URI);
		Log.d(TAG, String.format("uri: %s", uri));
		
		if (uri != null) {
			try {
				ArrayList<ContentProviderOperation> operationList = createQuotationContentProviderOperationList(Uri.parse(uri));
			
				ContentProviderResult[] results;
				results = getContext().getContentResolver().applyBatch(QuotationContract.AUTHORITY, operationList);
				for (ContentProviderResult result : results) {
					Log.d(TAG, String.format("result: %s", result));
				}
			} catch (Exception e) {
				Log.e(TAG, String.format("cannot sync %s", uri), e);
			}
		}
	}
}
