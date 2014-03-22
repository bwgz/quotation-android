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
	public static final String DATABASE = "quotation";
	
	protected interface StatusColumns {
		public static final String MODIFIED		= "modified";
	}

	protected interface CacheColumns {
		public static final String EVICTABLE	= "evictable";
	}

	protected interface LanguageColumns {
		public static final String LANGUAGE		= "language";
	}

	protected interface QuotationColumns {
		public static final String QUOTATION			= "quotation";
		public static final String SOURCE_ID			= "source_id";
		public static final String SPOKEN_BY_CHARACTER	= "spoken_by_character";
		public static final String AUTHOR_COUNT			= "author_count";
	}

	public static class Quotation implements BaseColumns, StatusColumns, CacheColumns, LanguageColumns, QuotationColumns {
		public static final String TABLE = "quotation";
		public static final String SEGMENT = "quotation";
		public static final String AUTHOR_IDS = "author_ids";
		public static final String AUTHOR_NAMES = "author_names";
		public static final String AUTHOR_IMAGE_IDS = "author_image_ids";

		public static final String FULL_ID = TABLE + "." + Quotation._ID; 

		private Quotation() {
		}

		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, SEGMENT);

		public static Uri withAppendedId(String id) {
			return Uri.parse(CONTENT_URI + id);
		}

		public static String getId(String uri) {
	    	return uri.substring(Quotation.CONTENT_URI.toString().length(), uri.length());
		}
		
		public static String getId(Uri uri) {
	    	return getId(uri.toString());
		}
		
		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of
		 * quotations.
		 */
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/quotation";

		/**
		 * The MIME type of a {@link #CONTENT_URI} a single quotation.
		 */
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.org.bwgz.quotation.quotation";

	}
	
	public static class QuotationQuery {
		static public final String AUTHOR_IDS = "(SELECT GROUP_CONCAT(" + Person.FULL_ID + ", ';')"									+
													" FROM " + QuotationPerson.TABLE										+
													" JOIN " + Person.TABLE													+
													" ON " + QuotationPerson.PERSON_ID + " = " + Person.FULL_ID				+
									                " WHERE " + QuotationPerson.QUOTATION_ID + " = " + Quotation.FULL_ID	+
									               	" GROUP BY " + Person.FULL_ID											+
									               	" ) AS " + Quotation.AUTHOR_IDS;
		static public final String AUTHOR_NAMES = "(SELECT GROUP_CONCAT(" + Person.FULL_NAME + ", ';')"									+
													" FROM " + QuotationPerson.TABLE										+
													" JOIN " + Person.TABLE													+
													" ON " + QuotationPerson.PERSON_ID + " = " + Person.FULL_ID				+
									                " WHERE " + QuotationPerson.QUOTATION_ID + " = " + Quotation.FULL_ID	+
									               	" GROUP BY " + Person.FULL_ID											+
									               	" ) AS " + Quotation.AUTHOR_NAMES;
		static public final String AUTHOR_IMAGE_IDS = "(SELECT GROUP_CONCAT(" + Person.FULL_IMAGE_ID + ", ';')"									+
													" FROM " + QuotationPerson.TABLE										+
													" JOIN " + Person.TABLE													+
													" ON " + QuotationPerson.PERSON_ID + " = " + Person.FULL_ID				+
									                " WHERE " + QuotationPerson.QUOTATION_ID + " = " + Quotation.FULL_ID	+
									               	" GROUP BY " + Person.FULL_ID											+
									               	" ) AS " + Quotation.AUTHOR_IMAGE_IDS;
		static public final String[] PROJECTION = { Quotation.FULL_ID, Quotation.QUOTATION, Source.FULL_NAME, Source.TYPE, BookmarkQuotation.BOOKMARK_ID, AUTHOR_IDS, AUTHOR_NAMES, AUTHOR_IMAGE_IDS };
	}
	
	protected interface PersonColumns {
		public static final String NAME				= "name";
		public static final String DESCRIPTION		= "description";
		public static final String NOTABLE_FOR		= "notable_for";
		public static final String IMAGE_ID			= "image_id";
		public static final String QUOTATION_COUNT	= "quotation_count";
	}

	protected interface CitationColumns {
		public static final String CITATION_PROVIDER	= "citation_provider";
		public static final String CITATION_STATEMENT	= "citation_statement";
		public static final String CITATION_URI			= "citation_uri";
	}
	
	public static class Person implements BaseColumns, StatusColumns, CacheColumns, LanguageColumns, PersonColumns, CitationColumns {
		public static final String TABLE = "person";
		public static final String SEGMENT = "person";

		public static final String FULL_ID = TABLE + "." + Person._ID; 
		public static final String FULL_NAME = TABLE + "." + Person.NAME; 
		public static final String FULL_IMAGE_ID = TABLE + "." + Person.IMAGE_ID; 

		private Person() {
		}

		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, SEGMENT);

		public static Uri withAppendedId(String id) {
			return Uri.parse(CONTENT_URI + id);
		}

		public static String getId(String uri) {
	    	return uri.substring(Person.CONTENT_URI.toString().length(), uri.length());
		}
		
		public static String getId(Uri uri) {
	    	return getId(uri.toString());
		}
	}
	
	protected interface QuotationPersonColumns {
		public static final String QUOTATION_ID	= "quotation_id";
		public static final String PERSON_ID	= "person_id";
	}

	public static class QuotationPerson implements StatusColumns, QuotationPersonColumns {
		public static final String TABLE = "quotation_person";
		public static final String SEGMENT = "quotation/person";

		public static final String FULL_MODIFIED = TABLE + "." + QuotationPerson.MODIFIED; 

		private QuotationPerson() {
		}

		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, SEGMENT);

		public static Uri withAppendedId(String id) {
			return Uri.parse(CONTENT_URI + id);
		}

		public static String getId(String uri) {
	    	return uri.substring(QuotationPerson.CONTENT_URI.toString().length(), uri.length());
		}
		
		public static String getId(Uri uri) {
	    	return getId(uri.toString());
		}
	}

	public static class PersonQuotation extends QuotationPerson {
		public static final String SEGMENT = "person/quotation";
		
		private PersonQuotation() {
		}

		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, SEGMENT);

		public static Uri withAppendedId(String id) {
			return Uri.parse(CONTENT_URI + id);
		}

		public static String getId(String uri) {
	    	return uri.substring(PersonQuotation.CONTENT_URI.toString().length(), uri.length());
		}
		
		public static String getId(Uri uri) {
	    	return getId(uri.toString());
		}
	}

	protected interface SubjectColumns {
		public static final String NAME				= "name";
		public static final String DESCRIPTION		= "description";
		public static final String IMAGE_ID			= "image_id";
		public static final String QUOTATION_COUNT	= "quotation_count";
	}

	public static class Subject implements BaseColumns, StatusColumns, CacheColumns, LanguageColumns, SubjectColumns {
		public static final String TABLE = "subject";
		public static final String SEGMENT = "subject";

		public static final String FULL_ID = TABLE + "." + Subject._ID; 

		private Subject() {
		}

		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, SEGMENT);

		public static Uri withAppendedId(String id) {
			return Uri.parse(CONTENT_URI + id);
		}

		public static String getId(String uri) {
	    	return uri.substring(Subject.CONTENT_URI.toString().length(), uri.length());
		}
		
		public static String getId(Uri uri) {
	    	return getId(uri.toString());
		}
	}
	
	protected interface QuotationSubjectColumns {
		public static final String QUOTATION_ID	= "quotation_id";
		public static final String SUBJECT_ID	= "subject_id";
	}

	public static class QuotationSubject implements StatusColumns, QuotationSubjectColumns {
		public static final String TABLE = "quotation_subject";
		public static final String SEGMENT = "quotation/subject";
	
		private QuotationSubject() {
		}

		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, SEGMENT);

		public static Uri withAppendedId(String id) {
			return Uri.parse(CONTENT_URI + id);
		}

		public static String getId(String uri) {
	    	return uri.substring(Subject.CONTENT_URI.toString().length(), uri.length());
		}
		
		public static String getId(Uri uri) {
	    	return getId(uri.toString());
		}
	}
	
	public static class SubjectQuotation extends QuotationSubject {
		public static final String SEGMENT = "subject/quotation";
		
		private SubjectQuotation() {
		}

		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, SEGMENT);

		public static Uri withAppendedId(String id) {
			return Uri.parse(CONTENT_URI + id);
		}

		public static String getId(String uri) {
	    	return uri.substring(SubjectQuotation.CONTENT_URI.toString().length(), uri.length());
		}
		
		public static String getId(Uri uri) {
	    	return getId(uri.toString());
		}
	}

	protected interface BookmarkColumns {
		public static final String BOOKMARK_ID	= "bookmark_id";
	}

	public static class BookmarkQuotation implements BookmarkColumns, StatusColumns {
		public static final String TABLE = "bookmark_quotation";
		public static final String SEGMENT = "bookmark/quotation";

		private BookmarkQuotation() {
		}

		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, SEGMENT);

		public static Uri withAppendedId(String id) {
			return Uri.parse(CONTENT_URI + id);
		}

		public static String getId(String uri) {
	    	return uri.substring(BookmarkQuotation.CONTENT_URI.toString().length(), uri.length());
		}
		
		public static String getId(Uri uri) {
	    	return getId(uri.toString());
		}
	}
	
	public static class BookmarkPerson implements BookmarkColumns, StatusColumns {
		public static final String TABLE = "bookmark_person";
		public static final String SEGMENT = "bookmark/person";

		private BookmarkPerson() {
		}

		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, SEGMENT);

		public static Uri withAppendedId(String id) {
			return Uri.parse(CONTENT_URI + id);
		}

		public static String getId(String uri) {
	    	return uri.substring(BookmarkPerson.CONTENT_URI.toString().length(), uri.length());
		}
		
		public static String getId(Uri uri) {
	    	return getId(uri.toString());
		}
	}
	
	public static class BookmarkSubject implements BookmarkColumns, StatusColumns {
		public static final String TABLE = "bookmark_subject";
		public static final String SEGMENT = "bookmark/subject";

		private BookmarkSubject() {
		}

		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, SEGMENT);

		public static Uri withAppendedId(String id) {
			return Uri.parse(CONTENT_URI + id);
		}

		public static String getId(String uri) {
	    	return uri.substring(BookmarkSubject.CONTENT_URI.toString().length(), uri.length());
		}
		
		public static String getId(Uri uri) {
	    	return getId(uri.toString());
		}
	}
	
	protected interface SourceColumns {
		public static final String NAME					= "name";
		public static final String TYPE					= "type";
	}

	public static class Source implements BaseColumns, StatusColumns, CacheColumns, SourceColumns {
		public static final String TABLE = "source";
		public static final String SEGMENT = "source";

		public static final String FULL_ID = TABLE + "." + Source._ID; 
		public static final String FULL_NAME = TABLE + "." + Source.NAME; 

		private Source() {
		}

		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, SEGMENT);

		public static Uri withAppendedId(String id) {
			return Uri.parse(CONTENT_URI + id);
		}

		public static String getId(String uri) {
	    	return uri.substring(Source.CONTENT_URI.toString().length(), uri.length());
		}
		
		public static String getId(Uri uri) {
	    	return getId(uri.toString());
		}
	}
	
	protected interface PickColumns {
		public static final String PICK_ID	= "pick_id";
	}

	public static class PickQuotation implements StatusColumns, PickColumns {
		public static final String TABLE = "pick_quotation";
		public static final String SEGMENT = "pick/quotation";

		private PickQuotation() {
		}

		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, SEGMENT);

		public static Uri withAppendedId(String id) {
			return Uri.parse(CONTENT_URI + id);
		}

		public static String getId(String uri) {
	    	return uri.substring(PickQuotation.CONTENT_URI.toString().length(), uri.length());
		}
		
		public static String getId(Uri uri) {
	    	return getId(uri.toString());
		}
	}

	public static class PickPerson implements StatusColumns, PickColumns {
		public static final String TABLE = "pick_person";
		public static final String SEGMENT = "pick/person";

		private PickPerson() {
		}

		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, SEGMENT);

		public static Uri withAppendedId(String id) {
			return Uri.parse(CONTENT_URI + id);
		}

		public static String getId(String uri) {
	    	return uri.substring(PickPerson.CONTENT_URI.toString().length(), uri.length());
		}
		
		public static String getId(Uri uri) {
	    	return getId(uri.toString());
		}
	}

	public static class PickSubject implements StatusColumns, PickColumns {
		public static final String TABLE = "pick_subject";
		public static final String SEGMENT = "pick/subject";

		private PickSubject() {
		}

		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, SEGMENT);

		public static Uri withAppendedId(String id) {
			return Uri.parse(CONTENT_URI + id);
		}

		public static String getId(String uri) {
	    	return uri.substring(PickSubject.CONTENT_URI.toString().length(), uri.length());
		}
		
		public static String getId(Uri uri) {
	    	return getId(uri.toString());
		}
	}
}
