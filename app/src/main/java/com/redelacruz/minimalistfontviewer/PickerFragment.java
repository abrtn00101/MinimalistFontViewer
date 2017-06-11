package com.redelacruz.minimalistfontviewer;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;

import com.nononsenseapps.filepicker.FilePickerFragment;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An extension of NoNonsense-FilePicker (https://github.com/spacecowboy/NoNonsense-FilePicker)
 * that adds the ability to limit visible files by MIME type. Also adds the ability to limit access
 * above the starting path.
 */
public class PickerFragment extends FilePickerFragment {

    private static final String TAG = "PickerFragment";

    private File startPath;
    private boolean isLimited = false;
    private boolean isUpOff = false;
    private int initialHeight = 0;
    private Set<String> mimeTypes = new HashSet<>();
    private static final Map<String, String> customTypes = new HashMap<>();

    static {
        /*
         * Add custom MIME types to check for here if MimeTypeMap does not return MIME types for a
         * given extension.
         */
        addCustomType("font/collection", "collection");
        addCustomType("font/otf", "otf");
        addCustomType("font/ttf", "ttf");
        addCustomType("font/sfnt", "sfnt");
        addCustomType("font/woff", "woff");
        addCustomType("font/woff2", "woff2");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        startPath = isLimited ? mCurrentPath : new File("/");
    }

    @Override
    public void onBindHeaderViewHolder(@NonNull HeaderViewHolder viewHolder) {
        ViewGroup layout = (ViewGroup) viewHolder.itemView.findViewById(android.R.id.text1).getParent();

        if (startPath.equals(mCurrentPath)) {
            setIsUpOff(true, layout);
        } else {
            setIsUpOff(false, layout);
            super.onBindHeaderViewHolder(viewHolder);
        }
    }

    @Override
    protected boolean isItemVisible(final File file) {
        if (!isDir(file)) {
            if (mimeTypes.size() == 0) return super.isItemVisible(file);

            String fileMime = getMimeType(file);

            for (String mimeType: mimeTypes) {
                if (super.isItemVisible(file) && (fileMime.contains(mimeType))) return true;
            }

            return false;
        } else {
            return super.isItemVisible(file);
        }
    }

    protected void setIsLimited(boolean bool) {
        isLimited = bool;
    }

    /**
     * Registers a list of MIME types that will be visible in the file picker.
     *
     * @param mimes An array of strings containing full or partial MIME types.
     */
    protected void registerMimeTypes(String[] mimes) {
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
    }

    /**
     * Add a custom MIME type to be checked for.
     *
     * @param mimeType  A custom MIME type.
     * @param extension The extension associated with the MIME type.
     */
    private static void addCustomType(String mimeType, String extension){
        if (!customTypes.containsKey(mimeType)) customTypes.put(extension, mimeType);
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
            if (type == null && customTypes.containsKey(extension)) type = customTypes.get(extension);
        }
        return type == null ? "" : type;
    }

    private void setIsUpOff(boolean bool, ViewGroup layout) {
        if (isUpOff != bool) {

            if (isUpOff) {
                ViewGroup.LayoutParams params = layout.getLayoutParams();
                params.height = initialHeight;
                layout.setLayoutParams(params);
                layout.setVisibility(View.VISIBLE);
            } else {
                ViewGroup.LayoutParams params = layout.getLayoutParams();
                initialHeight = params.height;
                params.height = 0;
                layout.setLayoutParams(params);
                layout.setVisibility(View.GONE);
            }

            isUpOff = !isUpOff;
        }
    }
}
