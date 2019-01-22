package com.ittianyu.dynamicupdater.loader;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.view.View;

public interface Loader {
    View render(Context context, Lifecycle lifecycle);
    int version();
    String name();
}
