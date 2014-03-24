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
package org.bwgz.quotation.activity;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.bwgz.quotation.R;
import org.bwgz.quotation.content.provider.QuotationContract.Person;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class AuthorFragment extends SherlockFragment {
	static public final String TAG = AuthorFragment.class.getSimpleName();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 View view = inflater.inflate(R.layout.author_fragment, container, false);
		 
		 return view;
	}

    private String generateCitation(String provider, String statement, String uri) {
    	String citation;
    	
    	if (uri != null) {
    		citation = String.format("<a href=\"%s\" target=\"_new\" title=\"%s\">%s</a>", uri, statement != null ? statement : provider, provider);
    	}
    	else {
    		citation = String.format("[%s]", provider);
    	}
    	
    	return citation;
    }

    public void setAuthors(Cursor cursor) {
		Log.d(TAG, String.format("updateAuthors - cursor: %s", cursor));
        int authorCount = 0;
        int descriptionCount = 0;
        
        StringBuilder authorBuffer = new StringBuilder();
        StringBuilder descriptionBuffer = new StringBuilder();
	    Bitmap image = null;
	    
        for (int i = 0; cursor.moveToPosition(i); i++) {
        	String name = cursor.getString(cursor.getColumnIndex(Person.NAME));
    		if (name != null) {
				Log.d(TAG, String.format("person name: %s",name));
        		if (authorCount != 0) {
        			authorBuffer.append(", ");
        		}
    			authorBuffer.append(name);
				authorCount++;
    		}
    		
    		String description = cursor.getString(cursor.getColumnIndex(Person.DESCRIPTION));
			if (description != null) {
				Log.d(TAG, String.format("person description: %s", description));
        		if (descriptionCount != 0) {
        			descriptionBuffer.append("\n");
        		}
        		descriptionBuffer.append(description);
        		
        		String citation_provider = cursor.getString(cursor.getColumnIndex(Person.CITATION_PROVIDER));
        		
        		if (citation_provider != null) {
            		String citation_statement = cursor.getString(cursor.getColumnIndex(Person.CITATION_STATEMENT));
            		String citation_uri = cursor.getString(cursor.getColumnIndex(Person.CITATION_URI));
        			descriptionBuffer.append(" ");
        			descriptionBuffer.append(generateCitation(citation_provider, citation_statement, citation_uri));
        		}
				descriptionCount++;
			}
			
			if (image == null) {
				byte[] bytes = cursor.getBlob(cursor.getColumnIndex(Person.IMAGE));
				if (bytes != null && bytes.length != 0) {
			        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			        image = BitmapFactory.decodeStream(in);
			        
					DisplayMetrics metrics = new DisplayMetrics();
					getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
					
			        Matrix matrix = new Matrix();
			        matrix.postScale(metrics.scaledDensity, metrics.scaledDensity);

			        image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
			        try {
						in.close();
					} catch (IOException e) {
					}
				}
			}
        }
        
        TextView textView;
        
		textView = (TextView) getActivity().findViewById(R.id.author);
        if (authorBuffer.length() != 0 || descriptionBuffer.length() != 0) {
			textView.setText(authorBuffer.toString());
			textView.setVisibility(View.VISIBLE);
        }
        else {
			textView.setVisibility(View.GONE);
        }
		
		textView = (TextView) getActivity().findViewById(R.id.description);
        if (descriptionBuffer.length() != 0) {
			textView.setText(Html.fromHtml(descriptionBuffer.toString()));
			textView.setMovementMethod(LinkMovementMethod.getInstance());
			textView.setVisibility(View.VISIBLE);
        }
        else {
			textView.setVisibility(View.GONE);
        }
		
		ImageView imageView = (ImageView) getActivity().findViewById(R.id.image);
		if (image != null) {
			imageView.setImageBitmap(image);
			imageView.setVisibility(View.VISIBLE);
		}
		else {
			imageView.setVisibility(View.GONE);
		}
    }
}
