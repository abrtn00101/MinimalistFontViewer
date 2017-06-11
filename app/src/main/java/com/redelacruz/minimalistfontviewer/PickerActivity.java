package com.redelacruz.minimalistfontviewer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.nononsenseapps.filepicker.AbstractFilePickerFragment;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;

/**
 * An extension of NoNonsense-FilePicker (https://github.com/spacecowboy/NoNonsense-FilePicker)
 * that adds the ability to limit visible files by MIME type.
 */
public class PickerActivity extends FilePickerActivity {

    private static final String TAG = "PickerActivity";

    /**
     * An String array of full or partial MIME types that will be visible in the picker.
     * Requires setType("&#42;&#47;&#42;").
     */
    public static final String EXTRA_MIME_TYPES =
            "android.intent.extra" + "ADD_MIME_TYPE";

    PickerFragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String[] mimeTypes = {};

        if (intent != null && intent.getType() != null) {
            switch (intent.getType()) {
                case "*/*":
                    if (intent.getStringArrayExtra(EXTRA_MIME_TYPES) != null) {
                        mimeTypes = intent.getStringArrayExtra(EXTRA_MIME_TYPES);
                        break;
                    }
                default:
                    mimeTypes = new String[]{intent.getType()};
            }
        }

        currentFragment.registerMimeTypes(mimeTypes);
    }

    /**
     * Return a copy of the new fragment
     */
    @Override
    protected AbstractFilePickerFragment<File> getFragment(
            final String startPath, final int mode, final boolean allowMultiple,
            final boolean allowDirCreate, final boolean allowExistingFile,
            final boolean singleClick) {

        // startPath is allowed to be null.
        // In that case, default folder should be SD-card and not "/"
        String path = (startPath != null ? startPath
                : Environment.getExternalStorageDirectory().getPath());

        currentFragment = new PickerFragment();
        currentFragment.setArgs(path, mode, allowMultiple, allowDirCreate,
                allowExistingFile, singleClick);
        return currentFragment;
    }

}
