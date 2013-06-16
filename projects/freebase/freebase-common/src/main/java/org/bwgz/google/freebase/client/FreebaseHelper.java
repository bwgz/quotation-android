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
package org.bwgz.google.freebase.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.bwgz.google.api.services.freebase.model.MqlReadResponse;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.freebase.Freebase;
import com.google.api.services.freebase.model.TopicLookup;

public class FreebaseHelper {
	private static String[] reasons = { "userRateLimitExceededUnreg" };
	private static final long KEY_PERIOD = TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES);
	
	private long keyLastUsed = 0;

	private Freebase freebase;
	private String key;

	public FreebaseHelper(String applicationName, String key) {
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        HttpRequestInitializer httpRequestInitializer = new HttpRequestInitializer() {

    		@Override
    		public void initialize(HttpRequest request) throws IOException {
    		}
        };
        
        Freebase.Builder fbb = new  Freebase.Builder(httpTransport, jsonFactory, httpRequestInitializer);
        fbb.setApplicationName(applicationName);
        freebase = fbb.build();
        
        this.key = key;
	}

	public FreebaseHelper(String applicationName) {
		this(applicationName, null);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	private boolean useKey() {
		return System.currentTimeMillis() < (keyLastUsed + KEY_PERIOD);
	}
	
	private boolean shouldRetryWithKey(GoogleJsonResponseException exception) {
		boolean result = false;
		
		GoogleJsonError error = exception.getDetails();
		HTTPStatusCode code = HTTPStatusCode.fromStatusCode(error.getCode());
		String reason = error.getErrors().get(0).getReason();
		
		if (code == HTTPStatusCode.FORBIDDEN) {
			for (String _reason : reasons) {
				if (reason.equals(_reason)) {
					result = true;
					break;
				}
			}
		}
			
		return result;
	}
	
	public TopicLookup fetchTopic(String mid, String lang, String[] filters) throws IOException {
		TopicLookup topic = null;
		int retries = 2;
	
		Freebase.Topic.Lookup lookup = freebase.topic().lookup(Arrays.asList(mid)).setLang(lang).setFilter(Arrays.asList(filters));
		if (useKey()) {
			lookup.setKey(key);
			retries = 1;
		}
		
        for (int n = 0; n < retries; n++) {
        	try {
		        topic = lookup.execute();
	        } 
        	catch (GoogleJsonResponseException e) {
        		if (lookup.getKey() == null && shouldRetryWithKey(e)) {
        			lookup.setKey(key);
            		keyLastUsed = System.currentTimeMillis();
        		}
        		else {
        			throw e;
        		}
        	}
        }
	    
	    return topic;
	}
	
	public TopicLookup fetchTopic(String mid, String[] filters) throws IOException {
		return fetchTopic(mid, Locale.getDefault().toString(), filters);
	}

	public byte[] fetchImage(String mid, long height, long width) throws IOException {
		byte bytes[] = null;
		int retries = 2;
		
		Freebase.Image image = freebase.image(Arrays.asList(mid));
		if (useKey()) {
			image.setKey(key);
			retries = 1;
		}
		image.setMaxheight(Long.valueOf(height));
		image.setMaxwidth(Long.valueOf(width));
		
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int n = 0; n < retries; n++) {
        	try {
				image.executeAndDownloadTo(out);
				bytes = out.toByteArray();
	        } 
        	catch (GoogleJsonResponseException e) {
        		if (image.getKey() == null && shouldRetryWithKey(e)) {
        			image.setKey(key);
        			keyLastUsed = System.currentTimeMillis();
        		}
        		else {
        			out.close();
        			throw e;
        		}
        	}
        }
		out.close();
		

		return bytes;
	}

	public <T> MqlReadResponse<T> mqlRead(String query, Class<?> clazz, Type type, String cursor) throws java.io.IOException {
     	MqlReadResponse<T> response = null;
		int retries = 2;
		
        Freebase.Mqlread<MqlReadResponse<T>> mqlRead = freebase.mqlread(query, clazz, type);
		if (useKey()) {
			mqlRead.setKey(key);
			retries = 1;
		}
     	mqlRead.setCursor(cursor);
     	
        for (int n = 0; n < retries; n++) {
        	try {
        		response = mqlRead.execute();
	        } 
        	catch (GoogleJsonResponseException e) {
        		if (mqlRead.getKey() == null && shouldRetryWithKey(e)) {
        			mqlRead.setKey(key);
        			keyLastUsed = System.currentTimeMillis();
        		}
        		else {
        			throw e;
        		}
        	}
        }

    	return response;
	}

	public Freebase getFreebase() {
		return freebase;
	}

	public void setFreebase(Freebase freebase) {
		this.freebase = freebase;
	}
}
