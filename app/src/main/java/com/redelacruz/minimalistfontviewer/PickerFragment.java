package com.redelacruz.minimalistfontviewer;

import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.nononsenseapps.filepicker.FilePickerFragment;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * An extension of NoNonsense-FilePicker (https://github.com/spacecowboy/NoNonsense-FilePicker)
 * that adds the ability to limit visible files by MIME type.
 */
public class PickerFragment extends FilePickerFragment {

    private static final String TAG = "PickerFragment";

    private Set<String> mimeTypes = new HashSet<>();

    @Override
    protected boolean isItemVisible(final File file) {
        if (!isDir(file)) {
            String fileMime = getMimeType(file);

            for (String mimeType: mimeTypes) {
                if (super.isItemVisible(file) && (fileMime.contains(mimeType))) return true;
            }

            return false;
        } else {
            return super.isItemVisible(file);
        }
    }

    /**
     * Return the MIME type of a given file based on its file extension.
     *
     * @param file  The file to get MIME type of.
     * @return  A String containing the MIME type of the given file. Empty ("") when the MIME type
     *          cannot be determined from the file's extension alone.
     */
    private String getMimeType(File file) {
        // TODO: Maybe? Find a cheap way to get MIME type from stream.
        String type = null;
        Uri uri = Uri.fromFile(file);
        String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type == null ? "" : type;
    }

    /**
     * Registers a list of MIME types that will be visible in the file picker.
     *
     * @param mimes An array of strings containing full or partial MIME types.
     */
    protected void registerMimeTypes(String[] mimes) {
        Log.d(TAG, "Mime array: " + Arrays.toString(mimes));

        for (String mimeType: mimes) {
            if (!mimeType.contains("/")) continue;

            String modifiedMime = mimeType.trim();
            if (modifiedMime.matches("(?s).*[\\\\()<>@,;:\"\\[\\]=?.\\s].*")) continue;
            if (modifiedMime.endsWith("/*")) modifiedMime = modifiedMime.substring(0, modifiedMime.length() - 1);
            if (modifiedMime.startsWith("*/")) modifiedMime = modifiedMime.substring(1, modifiedMime.length());
            if (mimeType.length() - mimeType.replace("/", "").length() > 1 || modifiedMime.contains("*")) continue;
            if (modifiedMime.equals("/")) {
                mimeTypes.clear();
                mimeTypes.add("");
                return;
            }

            mimeTypes.add(modifiedMime);
        }

        Log.d(TAG, "Mime types: " + mimeTypes.toString());
    }
}
