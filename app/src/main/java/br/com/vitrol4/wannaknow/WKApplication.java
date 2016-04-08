package br.com.vitrol4.wannaknow;

import android.app.Application;
import android.content.Context;

/**
 * Created by vitrol4 on 07/04/16.
 */
public class WKApplication extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        WKApplication.context = this;
    }
}
