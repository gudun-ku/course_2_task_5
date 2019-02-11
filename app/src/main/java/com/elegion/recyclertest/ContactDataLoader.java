package com.elegion.recyclertest;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

public class ContactDataLoader extends AsyncTaskLoader<String> {

   private String mId;
   private boolean mIsCanceled;
   private WeakReference<Context> mContext;


    public ContactDataLoader(Context context, String id) {
       super(context);
       mContext = new WeakReference<>(context);
       mId = id;
    }

    public boolean isCanceled() {
        return mIsCanceled;
    }

    @Override
    public String loadInBackground() {

        String number = null;
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Context context = mContext.get();

        if (context == null)
            return null;

        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
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

        mIsCanceled = false;
        // if id is null, do nothing and reset to keep isStarted false
        if (mId == null) {
           return;
        } else {
            forceLoad();
        }
    }

    @Override
    protected void onReset() {
        mIsCanceled = false;
        super.onReset();
    }

    @Override
    public void onCanceled(String data) {
        mIsCanceled = true;
        deliverResult(null);
        super.onCanceled(data);
    }
}
