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

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.ContentProviderOperation.Builder;
import android.os.RemoteException;
import android.util.Log;

import com.google.api.services.freebase.model.TopicPropertyValue;
import com.google.api.services.freebase.model.TopicValue;
import com.google.api.services.freebase.model.TopicLookup.Property;
import com.google.api.services.freebase.model.TopicValue.Citation;

public class ContentSync {
	static private final String TAG = ContentSync.class.getSimpleName();

	private Context context;
	private FreebaseHelper freebaseHelper;		  
	
	ContentSync(Context context, FreebaseHelper freebaseHelper) {
		this.context = context;
		this.freebaseHelper = freebaseHelper;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public FreebaseHelper getFreebaseHelper() {
		return freebaseHelper;
	}

	public void setFreebaseHelper(FreebaseHelper freebaseHelper) {
		this.freebaseHelper = freebaseHelper;
	}

	protected List<TopicValue> getPropertyValues(Property property, String name) {
		List<TopicValue> values = null;
		
		TopicPropertyValue topicPropertyValue = (TopicPropertyValue) property.get(name);
		if (topicPropertyValue != null) {
			values = topicPropertyValue.getValues();
		}
		
		return values;
	}
	
	protected String getFirstPropertyValue(Property property, String name) {
		String string = null;
		
		List<TopicValue> values = getPropertyValues(property, name);
		if (values != null && values.size() != 0) {
			string = values.get(0).getValue().toString();
		}
		
		return string;
	}

	protected String getFirstPropertyText(Property property, String name) {
		String string = null;
		
		List<TopicValue> values = getPropertyValues(property, name);
		if (values != null && values.size() != 0) {
			string = values.get(0).getText().toString();
		}
		
		return string;
	}

	protected String getString(Map<String, Object> map, String key) {
		Object object = map.get(key);
		
		return (object != null && object instanceof String) ? object.toString() : null;
	}

	private void dumpContentProviderResults(ContentProviderResult[] results) {
		for (ContentProviderResult result : results) {
			Log.d(TAG, String.format("result: %s", result));
		}
	}

	protected boolean applyBatch(Context context, ArrayList<ContentProviderOperation> operationList) {
		boolean result = false;
		
		try {
			ContentProviderResult[] results = context.getContentResolver().applyBatch(QuotationContract.AUTHORITY, operationList);
			dumpContentProviderResults(results);
			result = true;
		} catch (RemoteException e) {
			Log.e(TAG, e.getLocalizedMessage());
		} catch (OperationApplicationException e) {
			Log.e(TAG, e.getLocalizedMessage());
		}
		
		return result;
	}


	protected Builder createQuotationNewInsertBuilder(String id, String quotation, String language, long author_count, String sourceId, String spoken_by_character) {
		Builder builder = ContentProviderOperation.newInsert(Quotation.withAppendedId(id));
		builder.withValue(Quotation._ID, id);
		builder.withValue(Quotation.QUOTATION, quotation);
		builder.withValue(Quotation.LANGUAGE, language);
		builder.withValue(Quotation.AUTHOR_COUNT, author_count);
		builder.withValue(Quotation.SOURCE_ID, sourceId);
		builder.withValue(Quotation.SPOKEN_BY_CHARACTER, spoken_by_character);
		
		return builder;
	}

	protected Builder createPersonNewInsertBuilder(String id, String name, String image_id, String description, String notable_for, Citation citation, String language, long quotation_count) {
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

	protected Builder createPersonNewUpdateQuotationCountBuilder(String id, long quotation_count) {
		Builder builder = ContentProviderOperation.newUpdate(Person.withAppendedId(id));
		builder.withValue(Person.QUOTATION_COUNT, quotation_count);
		
		return builder;
	}

	protected Builder createQuotationPersonNewInsertBuilder(String quotationId, String personId) {
		Builder builder = ContentProviderOperation.newInsert(QuotationPerson.withAppendedId(quotationId));
		builder.withValue(QuotationPerson.QUOTATION_ID, quotationId);
		builder.withValue(QuotationPerson.PERSON_ID, personId);
		
		return builder;
	}
	
	protected Builder createPersonQuotationNewInsertBuilder(String personId, String quotationId) {
		Builder builder = ContentProviderOperation.newInsert(PersonQuotation.withAppendedId(personId));
		builder.withValue(QuotationPerson.PERSON_ID, personId);
		builder.withValue(QuotationPerson.QUOTATION_ID, quotationId);
		
		return builder;
	}
	
	protected Builder createSubjectNewInsertBuilder(String id, String name, byte[] image, String description, String language, long quotation_count) {
		Builder builder = ContentProviderOperation.newInsert(Subject.withAppendedId(id));
		builder.withValue(Subject._ID, id);
		builder.withValue(Subject.NAME, name);
		builder.withValue(Subject.LANGUAGE, language);
		if (image != null) {
			builder.withValue(Subject.IMAGE_ID, image);
		}
		if (description != null) {
			builder.withValue(Subject.DESCRIPTION, description);
		}
		builder.withValue(Subject.QUOTATION_COUNT, quotation_count);
		
		return builder;
	}

	protected Builder createSubjectNewUpdateQuotationCountBuilder(String id, long quotation_count) {
		Builder builder = ContentProviderOperation.newUpdate(Subject.withAppendedId(id));
		builder.withValue(Person.QUOTATION_COUNT, quotation_count);
		
		return builder;
	}

	protected Builder createQuotationSubjectNewInsertBuilder(String quotationId, String subjectId) {
		Builder builder = ContentProviderOperation.newInsert(QuotationSubject.withAppendedId(quotationId));
		builder.withValue(QuotationSubject.QUOTATION_ID, quotationId);
		builder.withValue(QuotationSubject.SUBJECT_ID, subjectId);
		
		return builder;
	}
	
	protected Builder createSubjectQuotationNewInsertBuilder(String subjectId, String quotationId) {
		Builder builder = ContentProviderOperation.newInsert(SubjectQuotation.withAppendedId(subjectId));
		builder.withValue(QuotationSubject.SUBJECT_ID, subjectId);
		builder.withValue(QuotationSubject.QUOTATION_ID, quotationId);
		
		return builder;
	}

	protected Builder createSourceNewInsertBuilder(String id, String name, String type) {
		Builder builder = ContentProviderOperation.newInsert(Source.withAppendedId(id));
		builder.withValue(Source._ID, id);
		builder.withValue(Source.NAME, name);
		builder.withValue(Source.TYPE, type);
		
		return builder;
	}

	protected Builder createPickQuotationNewInsertBuilder(String id) {
		Builder builder = ContentProviderOperation.newInsert(PickQuotation.withAppendedId(id));
		builder.withValue(PickQuotation.PICK_ID, id);
		
		return builder;
	}

	protected Builder createPickPersonNewInsertBuilder(String id) {
		Builder builder = ContentProviderOperation.newInsert(PickPerson.withAppendedId(id));
		builder.withValue(PickPerson.PICK_ID, id);
		
		return builder;
	}

	protected Builder createPickSubjectNewInsertBuilder(String id) {
		Builder builder = ContentProviderOperation.newInsert(PickSubject.withAppendedId(id));
		builder.withValue(PickSubject.PICK_ID, id);
		
		return builder;
	}
}
