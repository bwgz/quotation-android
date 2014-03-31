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
package org.bwgz.quotation.core;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

public interface CursorLoaderManager extends LoaderManager.LoaderCallbacks<Cursor> {
	public static final String	LOADER_BUNDLE_URI				= "loader.bundle.uri";
	public static final String	LOADER_BUNDLE_PROJECTION		= "loader.bundle.projection";
	public static final String	LOADER_BUNDLE_SELECTION			= "loader.bundle.selection";
	public static final String	LOADER_BUNDLE_SELECTION_ARGS	= "loader.bundle.selection.args";
	public static final String	LOADER_BUNDLE_SORT_ORDER		= "loader.bundle.sort.order";

    public interface CursorLoaderListener {
        public void onCursorLoadFinished(Loader<Cursor> loader, Cursor cursor);
    }

	public int initLoader(CursorLoaderListener listener, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder);
	public int initLoader(CursorLoaderListener listener, Bundle bundle);
	public void destroyLoader(int id);
}
