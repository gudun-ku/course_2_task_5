package com.elegion.recyclertest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.content.Intent;

import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity
        implements ContactsAdapter.OnItemClickListener,
        LoaderManager.LoaderCallbacks<String>


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

    // Запросим разрешения, достала их установка вручную
    private final int PERMISSION_READ_CONTACTS_REQUEST_CODE = 1001;
    private final int PERMISSION_CALL_PHONE_REQUEST_CODE = 1002;

    public void requestPermissions() {
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
                PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSION_READ_CONTACTS_REQUEST_CODE);
        }

        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    PERMISSION_CALL_PHONE_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_READ_CONTACTS_REQUEST_CODE ) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestPermissions();
            }
        }else if (requestCode == PERMISSION_READ_CONTACTS_REQUEST_CODE ) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestPermissions();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    // конец запроса разрешений

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //проверим разрешения
        requestPermissions();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, RecyclerFragment.newInstance())
                    .commit();
        }
        // подберем если уже запущен
        mContactLoader = getSupportLoaderManager().getLoader(LOADER_ID);
    }

    @Override
    public void onItemClick(String id) {
        Bundle args = new Bundle();
        args.putString(CONTACT_ID, id);
        if (mContactLoader == null) {
            mContactLoader = getSupportLoaderManager().initLoader(LOADER_ID, args, this);
        } else {
            mContactLoader = getSupportLoaderManager().restartLoader(LOADER_ID, args, this);

        }
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
                if (mContactLoader != null && mContactLoader.isStarted()) {
                    mContactLoader.cancelLoad();
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public Loader<String> onCreateLoader(int i, Bundle args) {
        if (args == null) {
            showToast(getString(R.string.msg_wrong_id));
            return null;
        }
        String id = args.getString(CONTACT_ID, null);
        return new ContactDataLoader(this, id);
    }


    @Override
    public void onLoadFinished(Loader<String> loader, String number) {
        if (((ContactDataLoader) loader).isCanceled()) {
            loader.reset();
            showToast(getString(R.string.call_interrupted));
        } else {
            loader.reset();
            if (number == null || TextUtils.isEmpty(number)) {
                showToast(getString(R.string.msg_no_number));
            } else {
                startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:" + number)));
            }
        }
    }



    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
