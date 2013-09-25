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

import org.bwgz.quotation.content.provider.QuotationContentProvider;
import org.bwgz.quotation.content.provider.QuotationContract;
import org.bwgz.quotation.content.provider.QuotationContract.Quotation;

import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

public class ContentProviderTests extends ProviderTestCase2<QuotationContentProvider> {
    private static final Uri INVALID_URI = Uri.withAppendedPath(Quotation.CONTENT_URI, "invalid");

    private MockContentResolver resolver;

	public ContentProviderTests(Class<QuotationContentProvider> providerClass, String providerAuthority) {
		super(providerClass, providerAuthority);
	}

    public ContentProviderTests() {
        super(QuotationContentProvider.class, QuotationContract.AUTHORITY);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        resolver = getMockContentResolver();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testIllegalArgumentException() {
    	String mimeType = resolver.getType(INVALID_URI);
    	assertNull(mimeType);
    }
    
    public void testGetType() {
        String mimeType = resolver.getType(Quotation.CONTENT_URI);
        assertEquals(Quotation.CONTENT_TYPE, mimeType);
    }
    
    public void testQuotationQuery() {
        Cursor cursor = resolver.query(QuotationContract.Quotation.CONTENT_URI, 
						        		new String[] { QuotationContract.Quotation._ID },
						        		null, null, null);
        assertNotNull(cursor);

        if (cursor.getCount() != 0) {
        	cursor.moveToFirst();
        	assertNotNull(cursor.getString(0));
        }
        cursor.close();
    }
}
