package com.abdallaadelessa.core.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;

import com.abdallaadelessa.core.app.BaseCoreApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class AndroidUtils {
    public static Boolean checkIfApplicationIsConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) BaseCoreApp.getAppComponent().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null) {
            return activeNetInfo != null && activeNetInfo.isAvailable() && activeNetInfo.isConnected();
        } else {
            return false;
        }
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static void restartActivity(Activity activity, Bundle bundle) {
        Intent intent = activity.getIntent();
        if (bundle != null) intent.putExtras(bundle);
        activity.overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.finish();
        activity.overridePendingTransition(0, 0);
        activity.startActivity(intent);
    }

    public static void restartActivity(Activity activity) {
        restartActivity(activity, null);
    }

    public static void finishToParent(Activity activity) {

        Intent upIntent = NavUtils.getParentActivityIntent(activity);
        if (upIntent != null) {
            if (NavUtils.shouldUpRecreateTask(activity, upIntent)) {
                // This activity is NOT part of this app's task, so
                // create a
                // new task
                // when navigating up, with a synthesized back stack.
                TaskStackBuilder.create(activity)
                        // Add all of this activity's parents to the back stack
                        .addNextIntentWithParentStack(upIntent)
                        // Navigate up to the closest parent
                        .startActivities();
                activity.finish();
            } else {
                // This activity is part of this app's task, so simply
                // navigate up to the logical parent activity.
                NavUtils.navigateUpTo(activity, upIntent);
            }
        } else {
            activity.finish();
        }
    }

    public static String loadAssetTextAsString(Context context, String fileName) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = context.getAssets().open(fileName);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ((str = in.readLine()) != null) {
                if (isFirst) isFirst = false;
                else buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            BaseCoreApp.getAppComponent().getLogger().log("Error opening asset " + fileName);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    BaseCoreApp.getAppComponent().getLogger().log("Error closing asset " + fileName);
                }
            }
        }
        return null;
    }

    public static void clearStackAndStartNewActivity(Context cxt, Class<?> cls) {
        clearStackAndStartNewActivity(cxt, cls, null);
    }

    public static void clearStackAndStartNewActivity(Context cxt, Class<?> cls, Bundle extras) {
        Intent intent = new Intent(cxt, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (extras != null) intent.putExtras(extras);
        cxt.startActivity(intent);
    }

    public static boolean isMyServiceRunning(Activity activity, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    // -------------------->

    public static void call(Context context, String number) {
        if (context == null || ValidationUtils.isStringEmpty(number)) return;
        try {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + number));
            context.startActivity(callIntent);
        } catch (Exception e) {
            UIUtils.showToast(context, "No apps found to call this number");
        }
    }

    public static void sendEmailTo(Activity activity, String email, String subject, String body, String chooserTitle) {
        if (activity == null) return;
        ShareCompat.IntentBuilder.from(activity).setType("message/rfc822").addEmailTo(email).setSubject(subject).setText(body).setChooserTitle(chooserTitle).startChooser();
    }

    public static void openMarkerLocationOnGoogleMapsApp(Context context, String latitude, String longitude, String title) {
        try {
            Double myLatitude = Double.valueOf(latitude);
            Double myLongitude = Double.valueOf(longitude);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + myLatitude + ">,<" + myLongitude + ">?q=<" + myLatitude + ">,<" + myLongitude + ">(" + title + ")"));
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openBrowser(Context context, String url) {
        if (!TextUtils.isEmpty(url)) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(browserIntent);
        }
    }

}
