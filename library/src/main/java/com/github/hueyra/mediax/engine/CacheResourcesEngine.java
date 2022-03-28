package com.github.hueyra.mediax.engine;

import android.content.Context;

@Deprecated
public interface CacheResourcesEngine {
    /**
     * Get the cache path
     *
     * @param context
     * @param url
     */
    String onCachePath(Context context, String url);
}
