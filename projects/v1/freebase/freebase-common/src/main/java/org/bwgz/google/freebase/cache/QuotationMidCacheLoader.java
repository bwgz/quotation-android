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
package org.bwgz.google.freebase.cache;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import org.bwgz.google.api.services.freebase.model.MqlReadResponse;
import org.bwgz.google.freebase.cache.FreebaseCursorCache.FreebaseCursor;
import org.bwgz.google.freebase.client.FreebaseHelper;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import com.google.api.client.util.Key;
import com.google.common.cache.CacheLoader;

public class QuotationMidCacheLoader extends CacheLoader<Integer, String> {
	static private final String query = 
			"[{\"type\":\"/media_common/quotation\",\"mid\":null}]";
	
	static public class Quotation {
		@Key
		private String mid;

		public String getMid() {
			return mid;
		}
		public void setMid(String mid) {
			this.mid = mid;
		}
	}
	
	private FreebaseCursorCache freebaseCursorCache;
	private FreebaseHelper freebaseHelper;

	public QuotationMidCacheLoader(InputStream in, FreebaseHelper freebaseHelper) throws IOException  {
		freebaseCursorCache = FreebaseCursorCache.getInstance(in);
		this.freebaseHelper = freebaseHelper;
	}
	
	private String getMid(String cursor, int offset) throws IOException {
		String mid = null;
		
    	Type type = ParameterizedTypeImpl.make(MqlReadResponse.class, new Type[] { Quotation[].class }, null);
    	MqlReadResponse<Quotation[]> response = freebaseHelper.mqlRead(query, MqlReadResponse.class, type, cursor);
       
        Quotation[] quotations = response.getResult();
        
        mid = quotations[offset].getMid();

	    return mid;
	}

	private String getMid(FreebaseCursor cursor, int id) throws Exception {
		return getMid(cursor.getCursor(), id - cursor.getOffset());
	}

	@Override
	public String load(Integer row) throws Exception {
		FreebaseCursor cursor = freebaseCursorCache.getCursor(row);
			
		return getMid(cursor, row);
	}
	
	public int getMaxResults() {
		return freebaseCursorCache.getMaxResults();
	}
}
