package com.elegion.recyclertest;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

public class ContactDataLoader extends AsyncTaskLoader<String> {

   private String mId;
   private WeakReference<Context> mContext;


   public ContactDataLoader(Context context) {
       super(context);
       mContext = new WeakReference<>(context);
   }

    public void setId(String id) {
        mId = id;
    }

    @Override
    public String loadInBackground() {

        String number = null;
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Cursor cursor = mContext.get().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND "
                        + ContactsContract.CommonDataKinds.Phone.TYPE + " = ?",
                new String[]{mId, String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)},
                null);

        if (cursor != null && cursor.moveToFirst()) {
            number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        return number;
    }

    @Override
    protected void onStartLoading() {
        // if id is null, do nothing and reset to keep isStarted false
        if (mId == null) {
            reset();
        } else {
            forceLoad();
        }
    }

    @Override
    protected void onReset() {
        mId = null;
        super.onReset();
    }

    @Override
    protected boolean onCancelLoad() {
        return super.onCancelLoad();
    }
}
