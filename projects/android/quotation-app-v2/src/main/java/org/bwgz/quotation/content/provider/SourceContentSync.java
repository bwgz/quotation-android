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

import org.bwgz.google.freebase.client.FreebaseHelper;
import org.bwgz.quotation.content.provider.QuotationContract.Source;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.api.services.freebase.model.TopicLookup;
import com.google.api.services.freebase.model.TopicLookup.Property;

public class SourceContentSync extends ContentSync {
	static private final String TAG = SourceContentSync.class.getSimpleName();

	static private final String TYPE_OBJECT_MID		= "/type/object/mid";
	static private final String TYPE_OBJECT_NAME	= "/type/object/name";
	static private final String NOTABLE_FOR			= "/common/topic/notable_for";

	static private final String[] sourceFilters = { TYPE_OBJECT_MID, TYPE_OBJECT_NAME, NOTABLE_FOR };

	SourceContentSync(Context context, FreebaseHelper freebaseHelper) {
		super(context, freebaseHelper);
	}
	
	public String createSourceContent(Uri uri) {
		Log.d(TAG, String.format("createSourceContent - uri: %s", uri));
		String mid = null;
		
		String id = Source.getId(uri);
		try {
			TopicLookup topic = getFreebaseHelper().fetchTopic(id, sourceFilters);
			Property property = topic.getProperty();
			mid = getFirstPropertyValue(property, TYPE_OBJECT_MID);
			String name = getFirstPropertyValue(property, TYPE_OBJECT_NAME);
			String type = getFirstPropertyText(property, NOTABLE_FOR);
			
			ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
			operationList.add(createSourceNewInsertBuilder(mid, name, type).build());
			applyBatch(getContext(), operationList);
		} catch (IOException e) {
			Log.e(TAG, e.getLocalizedMessage());
		}
		
		return mid;
	}
}
