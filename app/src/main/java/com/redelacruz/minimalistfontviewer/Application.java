package com.redelacruz.minimalistfontviewer;

import android.content.Context;

public class Application extends android.app.Application {

    private static final String TAG = "Application";
    private static Application context;

    public void onCreate() {
        super.onCreate();
        context = this;
    }

    /**
     * Get the application {@link Context}.
     *
     * @return The application {@link Context}.
     */
    public static Context getContext() {
        return context;
    }
}
