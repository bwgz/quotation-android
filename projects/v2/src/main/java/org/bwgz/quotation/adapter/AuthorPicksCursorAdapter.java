package org.bwgz.quotation.adapter;

import org.bwgz.quotation.R;
import org.bwgz.quotation.content.provider.QuotationContract;
import org.bwgz.quotation.content.provider.QuotationContract.BookmarkPerson;
import org.bwgz.quotation.content.provider.QuotationContract.Person;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class AuthorPicksCursorAdapter extends PicksCursorAdapter {
	static public final String TAG = AuthorPicksCursorAdapter.class.getSimpleName();

	static private class ViewHolder {
		public TextView author_name;
		public TextView author_description;
		public TextView author_notable_for;
		public NetworkImageView author_image;
		public TextView quotation_count;
		public CheckBox bookmark;
	}

	public AuthorPicksCursorAdapter(Context context, Cursor cursor, int resId, ImageLoader imageLoader) {
		super(context, cursor, resId, imageLoader);
	}

	public AuthorPicksCursorAdapter(Context context, Cursor cursor, int resId, ImageLoader imageLoader, int maxCount) {
		super(context, cursor, resId, imageLoader, maxCount);
		Log.d(TAG, String.format("AuthorPicksCursorAdapter - context: %s  cursor: %s (%d)  resId: %d  imageLoader: %s  maxCount: %d", context, cursor, cursor.getCount(), resId, imageLoader, maxCount));
	}

	@Override
	public View newView(final Context context, Cursor cursor, ViewGroup parent) {
		Log.d(TAG, String.format("newView - context: %s  cursor: %s (%d)   parent: %s", context, cursor, cursor.getCount(), parent));
		LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(getResId(), parent, false);

		ViewHolder viewHolder = new ViewHolder();
		viewHolder.author_name = (TextView) view.findViewById(R.id.author_name);
		viewHolder.author_description = (TextView) view.findViewById(R.id.author_description);
		viewHolder.author_notable_for = (TextView) view.findViewById(R.id.author_notable_for);
		viewHolder.author_image = (NetworkImageView) view.findViewById(R.id.author_image);
		viewHolder.quotation_count = (TextView) view.findViewById(R.id.quotation_count);
		viewHolder.bookmark = (CheckBox) view.findViewById(R.id.bookmark);
		view.setTag(viewHolder);

        if (viewHolder.author_image != null) {
            viewHolder.author_image.setDefaultImageResId(R.drawable.pick_image_holder);
        }

        return view;
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
        Log.d(TAG, String.format("bindView - view: %s  context: %s  cursor: %s (%d)", view, context, cursor, cursor.getCount()));
        Log.d(TAG, String.format("bindView - id: %s  checkBox: %s  name: %s", cursor.getString(cursor.getColumnIndex(Person._ID)), cursor.getString(cursor.getColumnIndex(BookmarkPerson.BOOKMARK_ID)), cursor.getString(cursor.getColumnIndex(Person.NAME))));

		ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (viewHolder.bookmark != null) {
            String id = cursor.getString(cursor.getColumnIndex(Person._ID));
            viewHolder.bookmark.setOnClickListener(new BookmarkOnClickListener(context, BookmarkPerson.withAppendedId(id), BookmarkPerson.BOOKMARK_ID, id));
        }

        setTextView(viewHolder.author_name, cursor.getString(cursor.getColumnIndex(Person.NAME)));
		setTextView(viewHolder.author_description, cursor.getString(cursor.getColumnIndex(Person.DESCRIPTION)));
		setTextView(viewHolder.author_notable_for, cursor.getString(cursor.getColumnIndex(Person.NOTABLE_FOR)));
		setNetworkImageView(viewHolder.author_image, cursor.getString(cursor.getColumnIndex(Person.IMAGE_ID)));
		setTextView(viewHolder.quotation_count, cursor.getString(cursor.getColumnIndex(Person.QUOTATION_COUNT)));
		setCheckBox(viewHolder.bookmark, cursor.getString(cursor.getColumnIndex(BookmarkPerson.BOOKMARK_ID)) != null);
	}
}
