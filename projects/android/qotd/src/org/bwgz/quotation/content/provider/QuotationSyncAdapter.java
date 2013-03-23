package org.bwgz.quotation.content.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.bwgz.freebase.model.Quotation;
import org.bwgz.freebase.query.FreebaseQuery;
import org.bwgz.freebase.query.MQLQueryBuilder;
import org.bwgz.qotd.R;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

public class QuotationSyncAdapter extends AbstractThreadedSyncAdapter {
	static private String TAG = QuotationSyncAdapter.class.getSimpleName();
	
	public static final String SYNC_EXTRAS_QUOTATION_UPDATE =  "quotation.update";
	
	public QuotationSyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		Log.d(TAG, String.format("context: %s  autoInitialize: %s", context, autoInitialize));
	}

	private String findQuotationFromFortune(String id) {
		String quotation = null;
		InputStream ins = getContext().getResources().openRawResource(R.raw.quotations);

		CSVReader listReader = new CSVReader(new InputStreamReader(ins));

		String[] list;
		try {
			while((list = listReader.readNext()) != null ) {
				if (list[0].equals(id)) {
					quotation = list[1];
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return quotation;
	}

	private MQLQueryBuilder builder = new MQLQueryBuilder();
	
	private Quotation findQuotationFromFreebase(String id) {
		Quotation quotation = new Quotation();
		
		quotation.setId(id);
		String query = builder.createQuery(Quotation.class, null, quotation);
		Log.d(TAG, String.format("query: %s", query));
		
		FreebaseQuery<Quotation> fbQuery = new FreebaseQuery<Quotation>();
		org.bwgz.freebase.model.Quotation result = fbQuery.getResult(query, Quotation.class);
		Log.d(TAG, String.format("result: %s\n", result));

		return result;
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
		Log.d(TAG, String.format("onPerformSync - account: %s  extras: %s  authority: %s  provider: %s  syncResult: %s", account, extras, authority, provider, syncResult));

		for (String key : extras.keySet()) {
			Log.d(TAG, String.format("%s: %s", key, extras.get(key)));
		}
		
		String string = extras.getString(SYNC_EXTRAS_QUOTATION_UPDATE);
		if (string != null) {
			Uri uri = Uri.parse(string);
	    	String _id = org.bwgz.quotation.content.provider.QuotationContract.Quotation.getId(uri);
			Log.d(TAG, String.format("_id: %s", _id));
			
			Quotation quotation = findQuotationFromFreebase(_id);
			if (quotation != null) {
				ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
	
				ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(uri);
				builder.withValue(org.bwgz.quotation.content.provider.QuotationContract.Quotation.QUOTATION, quotation.getName());
				builder.withValue(org.bwgz.quotation.content.provider.QuotationContract.Quotation.AUTHOR_NAME, quotation.getAuthor().getName());
				builder.withValue(org.bwgz.quotation.content.provider.QuotationContract.Quotation.AUTHOR_IMAGE, quotation.getAuthor().getId());
				builder.withSelection("_id = ?", new String[] { _id });
				operationList.add(builder.build());
		
				try {
					getContext().getContentResolver().applyBatch(QuotationContract.AUTHORITY, operationList);
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (OperationApplicationException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
