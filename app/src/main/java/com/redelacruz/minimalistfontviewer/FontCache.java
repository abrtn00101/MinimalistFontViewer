package com.redelacruz.minimalistfontviewer;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple font cache that makes a font once when it's first asked for and keeps it for the
 * life of the application.
 *
 * To use it, put your fonts in /assets/fonts.  You can access them in XML by their filename, minus
 * the extension (e.g. "Roboto-BoldItalic" or "roboto-bolditalic" for Roboto-BoldItalic.ttf).
 *
 * To set custom names for fonts other than their filenames, call addFont().
 *
 * Source: https://github.com/lisawray/fontbinding
 *
 *
 * Copyright 2015 Lisa Wray
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

// TODO: Adapt for use with user folders/ZIP files
// TODO: Add cache limit
// TODO: Method for bulk adding fonts to cache
// TODO: Method for bulk removing fonts from cache
// TODO: Method(s) for managing cache when cache limit is reached
public class FontCache
{
    private static final String TAG = "FontCache";
    private static final String FONT_DIR = "fonts";
    private static Map<String, Typeface> cache = new HashMap<>();
    private static Map<String, String> fontMapping = new HashMap<>();
    private static FontCache instance;

    public static FontCache getInstance() {
        if (instance == null) {
            instance = new FontCache();
        }
        return instance;
    }

    public void addFont(String name, String fontFilename) {
        fontMapping.put(name, fontFilename);
    }

    private FontCache() {
        AssetManager am = Application.getContext().getResources().getAssets();
        String fileList[];
        try {
            fileList = am.list(FONT_DIR);
        } catch (IOException e) {
            Log.e(TAG, "Error loading fonts from assets/fonts.");
            return;
        }

        for (String filename : fileList) {
            String alias = filename.substring(0, filename.lastIndexOf('.'));
            fontMapping.put(alias, filename);
            fontMapping.put(alias.toLowerCase(), filename);
        }
    }

    public Typeface get(String fontName) {
        String fontFilename = fontMapping.get(fontName);
        if (fontFilename == null) {
            Log.e(TAG, "Couldn't find font " + fontName + ". Maybe you need to call addFont() first?");
            return null;
        }
        if (cache.containsKey(fontName)) {
            return cache.get(fontName);
        } else {
            Typeface typeface = Typeface.createFromAsset(Application.getContext().getAssets(), FONT_DIR + "/" + fontFilename);
            cache.put(fontFilename, typeface);
            return typeface;
        }
    }

}