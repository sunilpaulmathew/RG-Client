package com.sunilpaulmathew.researchgateclient.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.snackbar.Snackbar;
import com.sunilpaulmathew.researchgateclient.BuildConfig;
import com.sunilpaulmathew.researchgateclient.R;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 12, 2021
 */

public class Utils {

    public static boolean isDonated(Context context) {
        try {
            context.getPackageManager().getApplicationInfo("com.smartpack.donate", 0);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
            return false;
        }
    }

    public static boolean isDarkTheme(Context context) {
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    public static void initializeAppTheme(Context context) {
        if (isDarkTheme(context)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return (cm.getActiveNetworkInfo() != null) && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public static boolean getBoolean(String name, boolean defaults, Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(name, defaults);
    }

    public static void saveBoolean(String name, boolean value, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(name, value).apply();
    }

    public static void showSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.dismiss, v -> snackbar.dismiss());
        snackbar.show();
    }

    public static void goToSettings(Activity activity) {
        Intent settings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        settings.setData(uri);
        activity.startActivity(settings);
        activity.finish();
    }

    public static void launchURL(View view, String url, Activity activity) {
        if (Utils.isNetworkAvailable(activity)) {
            try {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                activity.startActivity(i);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Utils.showSnackbar(view, activity.getString(R.string.network_unavailable));
        }
    }

}