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
package org.bwgz.quotation.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.bwgz.quotation.app.QuotationApplication;
import org.bwgz.quotation.content.provider.QuotationContract.PickPerson;
import org.bwgz.quotation.content.provider.QuotationContract.PickQuotation;
import org.bwgz.quotation.content.provider.QuotationContract.PickSubject;
import org.bwgz.quotation.core.FreebaseIdLoader;
import org.bwgz.quotation.model.picks.Pick;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class QuotationService extends IntentService {
	static private final String TAG = QuotationService.class.getSimpleName();

    public static final String BROADCAST_ACTION		= "org.bwgz.quotation.service.BROADCAST";
	public static final String EXTENDED_DATA_STATUS	= "org.bwgz.quotation.service.STATUS";

    private static final int PICK_SIZE	= 10;
    private static final long PICKS_PERIOD	= TimeUnit.DAYS.toMillis(1);

	private static final Random random = new Random();

	public QuotationService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, String.format("onHandleIntent - intent: %s (%s)", intent, new Date().toString()));
		
		SharedPreferences preferences = getSharedPreferences(QuotationApplication.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
    	long modified = preferences.getLong(QuotationApplication.APPLICATION_PREFERENCE_QUOTATION_PICKS_MODIFIED, 0);
    	
    	if (modified + PICKS_PERIOD  < System.currentTimeMillis()) {
    		getContentResolver().delete(PickQuotation.CONTENT_URI, null, null);
      		getContentResolver().delete(PickPerson.CONTENT_URI, null, null);
     		getContentResolver().delete(PickSubject.CONTENT_URI, null, null);
 		
            FreebaseIdLoader freebaseIdLoader = FreebaseIdLoader.getInstance(getApplicationContext());
            List<Pick> quotationPicks = freebaseIdLoader.getRandomQuotationPicks(PICK_SIZE);
            List<Pick> authorPicks = freebaseIdLoader.getRandomAuthorPicks(PICK_SIZE);
            List<Pick> subjectPicks = freebaseIdLoader.getRandomSubjectPicks(PICK_SIZE);
          
            for (int i = 0; i < PICK_SIZE; i++) {
		 		getContentResolver().query(PickQuotation.withAppendedId(quotationPicks.get(i).getId()), null, null, null, null).close();
		 		getContentResolver().query(PickPerson.withAppendedId(authorPicks.get(i).getId()), null, null, null, null).close();
		 		getContentResolver().query(PickSubject.withAppendedId(subjectPicks.get(i).getId()), null, null, null, null).close();
            }
    		
 			SharedPreferences.Editor editor = preferences.edit();
 			modified = System.currentTimeMillis();
			editor.putLong(QuotationApplication.APPLICATION_PREFERENCE_QUOTATION_PICKS_MODIFIED, modified);
			editor.putLong(QuotationApplication.APPLICATION_PREFERENCE_PERSON_PICKS_MODIFIED, modified);
			editor.putLong(QuotationApplication.APPLICATION_PREFERENCE_SUBJECT_PICKS_MODIFIED, modified);
			editor.commit();
    	}
    	
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, new Intent(this, QuotationAlarmReceiver.class), 0);
		alarmManager.cancel(alarmIntent);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 2);
		calendar.set(Calendar.MINUTE,random.nextInt(60));
		calendar.set(Calendar.SECOND, random.nextInt(60));
		alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis() + PICKS_PERIOD, alarmIntent);

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(BROADCAST_ACTION).putExtra(EXTENDED_DATA_STATUS, true));
	}

	/*
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, String.format("onHandleIntent - intent: %s", intent));
		
		SharedPreferences preferences = getSharedPreferences(QuotationApplication.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
    	long modified = preferences.getLong(QuotationApplication.APPLICATION_PREFERENCE_QUOTATION_PICKS_MODIFIED, 0);
    	
    	if (modified + PICKS_PERIOD  < System.currentTimeMillis()) {
    		getContentResolver().delete(PickQuotation.CONTENT_URI, null, null);
    		
            FreebaseIdLoader freebaseIdLoader = FreebaseIdLoader.getInstance(getApplicationContext());
            List<Pick> picks = freebaseIdLoader.getQuotationPicks().getPicks();
            
            for (Pick pick : picks) {
        		Log.d(TAG, String.format("onHandleIntent - querying quotation pick: %s", pick));

		 		getContentResolver().query(PickQuotation.withAppendedId(pick.getId()), null, null, null, null).close();
            }
    		
 			SharedPreferences.Editor editor = preferences.edit();
			editor.putLong(QuotationApplication.APPLICATION_PREFERENCE_QUOTATION_PICKS_MODIFIED, System.currentTimeMillis());
			editor.commit();
    	}
    	
    	modified = preferences.getLong(QuotationApplication.APPLICATION_PREFERENCE_PERSON_PICKS_MODIFIED, 0);
    	
    	if (modified + PICKS_PERIOD  < System.currentTimeMillis()) {
    		getContentResolver().delete(PickPerson.CONTENT_URI, null, null);
    		
            FreebaseIdLoader freebaseIdLoader = FreebaseIdLoader.getInstance(getApplicationContext());
            List<Pick> picks = freebaseIdLoader.getAuthorPicks().getPicks();
            
            for (Pick pick : picks) {
        		Log.d(TAG, String.format("onHandleIntent - querying author pick: %s", pick));

		 		getContentResolver().query(PickPerson.withAppendedId(pick.getId()), null, null, null, null).close();
            }
    		
 			SharedPreferences.Editor editor = preferences.edit();
			editor.putLong(QuotationApplication.APPLICATION_PREFERENCE_PERSON_PICKS_MODIFIED, System.currentTimeMillis());
			editor.commit();
    	}

    	modified = preferences.getLong(QuotationApplication.APPLICATION_PREFERENCE_SUBJECT_PICKS_MODIFIED, 0);
    	
    	if (modified + PICKS_PERIOD  < System.currentTimeMillis()) {
    		getContentResolver().delete(PickSubject.CONTENT_URI, null, null);
    		
            FreebaseIdLoader freebaseIdLoader = FreebaseIdLoader.getInstance(getApplicationContext());
            List<Pick> picks = freebaseIdLoader.getSubjectPicks().getPicks();
            
            for (Pick pick : picks) {
        		Log.d(TAG, String.format("onHandleIntent - querying subject pick: %s", pick));

		 		getContentResolver().query(PickSubject.withAppendedId(pick.getId()), null, null, null, null).close();
            }
    		
 			SharedPreferences.Editor editor = preferences.edit();
			editor.putLong(QuotationApplication.APPLICATION_PREFERENCE_SUBJECT_PICKS_MODIFIED, System.currentTimeMillis());
			editor.commit();
    	}
   	

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(BROADCAST_ACTION).putExtra(EXTENDED_DATA_STATUS, true));
	}
	*/
}
