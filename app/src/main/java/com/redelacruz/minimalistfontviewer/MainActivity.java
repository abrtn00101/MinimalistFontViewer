package com.redelacruz.minimalistfontviewer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.nononsenseapps.filepicker.Utils;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int ZIP_PICK_REQUEST_CODE = 3030; //totally arbitrary number (-_-)

    //TODO: Retrieve from settings.
    private boolean isFullscreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateFullscreen();

        final Button fullscreenTestButton = (Button) findViewById(R.id.fullscreen_test_button);
        fullscreenTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFullscreen(!isFullscreen);
            }
        });

        final Button pickZipTestButton = (Button) findViewById(R.id.pickzip_test_button);
        pickZipTestButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pickFileFolder();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            updateFullscreen();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateFullscreen();
    }

    public void setFullscreen(boolean fullscreen) {
        isFullscreen = fullscreen;
        updateFullscreen();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void updateFullscreen() {
        //TODO: Fix unstable pre-KitKat layout*
        // * Applies to pre-KitKat devices that use soft navigation buttons, since Android doesn't
        //   properly calculate the size of the layout when the action bar is toggled off.
        //   Potential fix: Calculate size of action bar and adjust accordingly.
        int uiOptions = 0;
        View decorView = getWindow().getDecorView();
        boolean isUpdateNeeded = isFullscreen != ((decorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_FULLSCREEN) == View.SYSTEM_UI_FLAG_FULLSCREEN);

        if (isUpdateNeeded) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                if (isFullscreen) {
                    uiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
                    Log.d(TAG, "Fullscreen API < 19");
                } else {
                    uiOptions ^= View.SYSTEM_UI_FLAG_VISIBLE;
                    Log.d(TAG, "Not fullscreen API < 19");
                }
            } else {
                if (isFullscreen) {
                    uiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                    Log.d(TAG, "Fullscreen API >= 19");
                } else {
                    uiOptions ^= View.SYSTEM_UI_FLAG_VISIBLE;
                    Log.d(TAG, "Not fullscreen API >= 19");
                }
            }
        }

        uiOptions ^= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

        decorView.setSystemUiVisibility(uiOptions);
    }

    public void pickFileFolder() {
        Intent i = new Intent(this, PickerActivity.class);

        i.putExtra(PickerActivity.EXTRA_ALLOW_MULTIPLE, true);
        i.putExtra(PickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(PickerActivity.EXTRA_MODE, PickerActivity.MODE_FILE_AND_DIR);
        i.putExtra(PickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        i.setType("*/*");
        String[] mimeTypes = {"application/zip", "application/font-sfnt", "font/*"};
        i.putExtra(PickerActivity.EXTRA_MIME_TYPES, mimeTypes);

        startActivityForResult(i, ZIP_PICK_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(TAG, "onActivityResult");
        Log.d(TAG, "Request code: " + requestCode);
        Log.d(TAG, "Result code: " + resultCode);

        if (requestCode == ZIP_PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Use the provided utility method to parse the result
            List<Uri> files = Utils.getSelectedFilesFromResult(data);
            for (Uri uri: files) {
                String path = uri.getPath();
                Log.d(TAG, "URI path: " + path);
                // File file = Utils.getFileForUri(uri);
                // Do something with the result...
            }
        }
    }
}
