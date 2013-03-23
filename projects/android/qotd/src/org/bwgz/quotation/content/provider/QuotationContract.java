package org.bwgz.quotation.content.provider;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.BaseColumns;

@SuppressWarnings("unused")
public final class QuotationContract {
	public static final String AUTHORITY = "org.bwgz.quotation";
	public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
	
	protected interface StatusColumns {
		public enum State {
			UNINITIALIZED(0), INITIALIZED(1);
			
		    private final int value;
		    State(int value) {
		        this.value = value;
		    }
		    public int getValue() {
		    	return value;
		    }
		}
		
		public static final String STATE		= "state";
		public static final String MODIFIED		= "modified";
	}

	protected interface QuotationColumns {
		public static final String QUOTATION	= "quotation";
		public static final String AUTHOR_NAME	= "author_name";
		public static final String AUTHOR_IMAGE	= "author_image";
	}

	public static class Quotation implements BaseColumns, StatusColumns, QuotationColumns {
		public static final String TABLE = "quotation";
		
		private Quotation() {
		}

		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE);

		public static Uri getUri(String id) {
			return Uri.parse(CONTENT_URI + id);
		}

		public static String getId(Uri uri) {
	    	return uri.getPath().substring(Quotation.CONTENT_URI.getPath().length(), uri.getPath().length());
		}
		
		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of
		 * quotations.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/quotation";

		/**
		 * The MIME type of a {@link #CONTENT_URI} a single quotation.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/quotation";

	}
}
