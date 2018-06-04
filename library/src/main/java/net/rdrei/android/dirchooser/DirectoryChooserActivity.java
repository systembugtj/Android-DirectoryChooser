package net.rdrei.android.dirchooser;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.tbruyelle.rxpermissions2.RxPermissions;

/**
 * Let's the user choose a directory on the storage device. The selected folder
 * will be sent back to the starting activity as an activity result.
 */
public class DirectoryChooserActivity extends AppCompatActivity implements
        DirectoryChooserFragment.OnFragmentInteractionListener {
    public static final String EXTRA_CONFIG = "config";
    public static final String RESULT_SELECTED_DIR = "selected_dir";
    public static final int RESULT_CODE_DIR_SELECTED = 1;

    RxPermissions rxPermissions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        setContentView(R.layout.directory_chooser_activity);

        rxPermissions = new RxPermissions(this);

        final DirectoryChooserConfig config = getIntent().getParcelableExtra(EXTRA_CONFIG);

        if (config == null) {
            throw new IllegalArgumentException(
                    "You must provide EXTRA_CONFIG when starting the DirectoryChooserActivity.");
        }

        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        if (savedInstanceState == null) {
                            final FragmentManager fragmentManager = getFragmentManager();
                            final DirectoryChooserFragment fragment = DirectoryChooserFragment.newInstance(config);
                            fragmentManager.beginTransaction()
                                    .add(R.id.main, fragment)
                                    .commit();
                        }
                    } else {
                        finish();
                    }
                });
    }

    /* package */void setupActionBar() {
        // there might not be an ActionBar, for example when started in Theme.Holo.Dialog.NoActionBar theme
        @SuppressLint("AppCompatMethod") final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSelectDirectory(@NonNull String path) {
        final Intent intent = new Intent();
        intent.putExtra(RESULT_SELECTED_DIR, path);
        setResult(RESULT_CODE_DIR_SELECTED, intent);
        finish();
    }

    @Override
    public void onCancelChooser() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
