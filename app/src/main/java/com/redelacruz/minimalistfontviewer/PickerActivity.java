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
 * that adds the ability to limit visible files by MIME type. Also adds the ability to limit access
 * above the starting path.
 */
public class PickerActivity extends FilePickerActivity {

    private static final String TAG = "PickerActivity";

    /**
     * A String array of full or partial MIME types that will be visible in the picker.
     * Requires setType("&#42;&#47;&#42;").
     */
    public static final String EXTRA_MIME_TYPES =
            "android.intent.extra" + ".MIME_TYPES";
    /**
     * A boolean value that determines whether or not the picker will limit access above the
     * starting path.
     */
    public static final String EXTRA_LIMIT_ROOT_TO_START =
            "pickeractivity.intent" + ".LIMIT_ROOT_TO_START";
    private String[] mimeTypes = {};
    private boolean isLimitedToStart = false;

    PickerFragment currentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();

        if (intent != null) {
            if (intent.getType() != null) {
                switch (intent.getType()) {
                    case "*/*":
                        if (intent.getStringArrayExtra(EXTRA_MIME_TYPES) != null) {
                            mimeTypes = intent.getStringArrayExtra(EXTRA_MIME_TYPES);
                        }
                        break;
                    default:
                        mimeTypes = new String[]{intent.getType()};
                }
            }

            isLimitedToStart = intent.getBooleanExtra(EXTRA_LIMIT_ROOT_TO_START, isLimitedToStart);
        }

        super.onCreate(savedInstanceState);
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
        currentFragment.registerMimeTypes(mimeTypes);
        currentFragment.setIsLimited(isLimitedToStart);
        return currentFragment;
    }

}
