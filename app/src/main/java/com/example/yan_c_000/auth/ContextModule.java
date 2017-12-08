package com.example.yan_c_000.auth;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yan-c_000 on 10.11.2017.
 */
@Module
public class ContextModule {

    private final Context context;

    public ContextModule(Context context) {
        this.context = context;
    }

    @Provides
    public Context context(){
        return context;
    }

}
