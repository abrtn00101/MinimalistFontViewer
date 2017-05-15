package com.redelacruz.minimalistfontviewer;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";

    //TODO: Retrieve from settings.
    private boolean isFullscreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateFullscreen();

        final Button test_button = (Button) findViewById(R.id.test_button);
        test_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setFullscreen(!isFullscreen);
            }
        });

        Log.d(TAG, "onCreate");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            updateFullscreen();
        }

        Log.d(TAG, "onWindowFocusChanged");
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateFullscreen();

        Log.d(TAG, "onResume");
    }

    public void setFullscreen(boolean fullscreen) {
        isFullscreen = fullscreen;
        updateFullscreen();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void updateFullscreen() {
        Log.d(TAG, "updateFullscreen");

        View decorView = getWindow().getDecorView();
        boolean isUpdateNeeded = isFullscreen != ((decorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_FULLSCREEN) == View.SYSTEM_UI_FLAG_FULLSCREEN);

        Log.d(TAG, "isUpdatedNeeded = " + isUpdateNeeded);
        Log.d(TAG, "isFullscreen = " + isFullscreen);
        Log.d(TAG, "system flag is not set = " + ((decorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_FULLSCREEN) != View.SYSTEM_UI_FLAG_FULLSCREEN));

        if (isUpdateNeeded) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                ActionBar actionBar = getSupportActionBar();

                if (isFullscreen) {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
                    actionBar.hide();
                    Log.d(TAG, "Fullscreen API < 19");
                } else {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    actionBar.show();
                    Log.d(TAG, "Not fullscreen API < 19");
                }
            } else {
                if (isFullscreen) {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    Log.d(TAG, "Fullscreen API => 19");
                } else {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                    Log.d(TAG, "Not fullscreen API => 19");
                }
            }
        }
    }
}
