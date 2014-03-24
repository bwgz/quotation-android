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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bwgz.quotation.R;
import org.bwgz.quotation.model.picks.Pick;

import android.content.Context;
import android.util.Log;

public class FreebaseIdLoader {
	static public final String TAG = FreebaseIdLoader.class.getSimpleName();

    private static final String MID_NAMESPACE	= "/m";
    private static final int RANDOM_PICK_SIZE	= 100;
    
    private Context context;
    private final Random random = new Random();
    private final List<String> quotations = new ArrayList<String>();
    private final List<String> authors = new ArrayList<String>();
    private final List<String> subjects = new ArrayList<String>();

    static private FreebaseIdLoader instance;
    
    public FreebaseIdLoader(Context context) {
    	this.context = context;
	}

	static public synchronized FreebaseIdLoader getInstance(Context context) {
    	if (instance == null) {
    		instance = new FreebaseIdLoader(context);
	 		try {
	 			instance.load(instance.quotations, R.raw.quotations_demo);
	 			instance.load(instance.authors, R.raw.authors_demo);
	 			instance.load(instance.subjects, R.raw.subjects_demo);
	 			//instance.load(instance.quotations, R.raw.quotations);
	 			//instance.load(instance.authors, R.raw.authors);
	 			//instance.load(instance.subjects, R.raw.subjects);
			} catch (IOException e) {
				Log.e(TAG, e.getLocalizedMessage());
			}
    	}
    	
    	return instance;
    }
    
    private void load(List<String> list, int resId) throws IOException {
		Log.d(TAG, String.format("load - list: %s  resId: %d", list, resId));
		
		BufferedReader in = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(resId)));
		String mid;
		
		while ((mid = in.readLine()) != null) {
			list.add(mid);
		}
		Log.d(TAG, String.format("load - list size: %d", list.size()));
		
		in.close();
    }
    
    public void initialize() throws IOException {
		Log.d(TAG, String.format("initialize"));
    }
    
    private String toMid(String string) {
    	return MID_NAMESPACE + "/" + string;
    }
    
    private List<Pick> getRandomPicks(List<String> source, int size) {
		//Log.d(TAG, String.format("getRandomPicks - source: %s (%d)  size: %d  clazz: %s", source, source.size(), size, clazz));
        List<Pick> picks = new ArrayList<Pick>();

        for (int i = 0; i < Math.min(source.size(),  size); i++) {
        	for (;;) {
        		String mid = toMid(source.get(random.nextInt(source.size())));
        		
        		Pick pick = new Pick(mid);
        		if (!picks.contains(pick)) {
	        		picks.add(pick);
	        		break;
        		}
        	}
        }
        
		//Log.d(TAG, String.format("getRandomPicks - picks: %s  size: %d", picks, picks.size()));

    	return picks;
    }
    
	public List<Pick> getRandomQuotationPicks(int size) {
	    return getRandomPicks(quotations, size);
    }
	
	public List<Pick> getRandomQuotationPicks() {
	    return getRandomQuotationPicks(RANDOM_PICK_SIZE);
    }
	
	public List<Pick> getRandomAuthorPicks(int size) {
	    return getRandomPicks(authors, size);
	}

	public List<Pick> getRandomAuthorPicks() {
	    return getRandomAuthorPicks(RANDOM_PICK_SIZE);
	}
	
	public List<Pick> getRandomSubjectPicks(int size) {
	    return getRandomPicks(subjects, size);
	}

	public List<Pick> getRandomSubjectPicks() {
	    return getRandomSubjectPicks(RANDOM_PICK_SIZE);
	}

	public Pick getRandomQuotationPick() {
	    return getRandomPicks(quotations, 1).get(0);
    }

}
