package ru.fmparty;

import android.app.Application;
import android.content.Context;

public class FMPartyApp extends Application {
    private static FMPartyApp  instance;

    public FMPartyApp()
    {
        instance = this;
    }
    public static Context getContext()
    {
        return instance;
    }
}
