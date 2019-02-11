package com.elegion.recyclertest;

import android.support.v4.app.LoaderManager;
import android.content.Intent;

import android.support.v4.content.Loader;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity
        implements ContactsAdapter.OnItemClickListener, LoaderManager.LoaderCallbacks<String>,
        Loader.OnLoadCanceledListener<String>

{

    public static final String CONTACT_ID = "CONTACT_ID";
    private static final int LOADER_ID = 304;

    // добавить фрагмент с recyclerView ---
    // добавить адаптер, холдер и генератор заглушечных данных ---
    // добавить обновление данных и состояние ошибки ---
    // добавить загрузку данных с телефонной книги ---
    // добавить обработку нажатий ---
    // добавить декораторы ---


    private Toast mToast;
    private Loader<String> mContactLoader;

    private void showToast(String msg) {
        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        mToast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, RecyclerFragment.newInstance())
                    .commit();
            mContactLoader = getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        } else {
            mContactLoader = getSupportLoaderManager().getLoader(LOADER_ID);
        }
        //register cancelled listener
        //mContactLoader.registerOnLoadCanceledListener(this);
    }

    @Override
    public void onItemClick(String id) {
        mContactLoader = getSupportLoaderManager().getLoader(LOADER_ID);
        // set contact id
        ((ContactDataLoader) mContactLoader).setId(id);
        mContactLoader.startLoading();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_stop_calling:
                // cancel calling process
                if (mContactLoader.isStarted()) {
                    mContactLoader.cancelLoad();
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public Loader<String> onCreateLoader(int i, Bundle bundle) {
        String id = null;
        if (bundle != null) {
          id = bundle.getString(CONTACT_ID, null);
        }
        return new ContactDataLoader(this);
    }


    @Override
    public void onLoadFinished(Loader<String> loader, String number) {
        loader.reset();
        if (number == null || TextUtils.isEmpty(number)) {
            showToast(getString(R.string.msg_wrong_number));
        } else {
            startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:" + number)));
        }

    }

    @Override
    public void onLoadCanceled(Loader<String> loader) {
        showToast(getString(R.string.call_interrupted));
        loader.reset();
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
