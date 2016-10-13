package com.abdallaadelessa.core.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.abdallaadelessa.core.app.BaseCoreApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Unbinder;

public class Utilities {
    /**
     * Check if Application is connected to the Internet
     *
     * @param context
     * @return
     */

    public static Boolean CheckIfApplicationIsConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if(activeNetInfo != null) {
            return activeNetInfo != null && activeNetInfo.isAvailable() && activeNetInfo.isConnected();
        }
        else {
            return false;
        }
    }

    /**
     * Encode URl UTF-8
     *
     * @param originalUrl
     * @return
     * @throws UnsupportedEncodingException
     */

    public static String EncodeURL(String originalUrl) throws UnsupportedEncodingException {
        int lastSlashIndexLarge = originalUrl.lastIndexOf('/');
        String encodedUrl = originalUrl.substring(0, lastSlashIndexLarge + 1) + URLEncoder.encode(originalUrl.substring(lastSlashIndexLarge + 1, originalUrl.length()), "UTF-8");
        String completeUrl = encodedUrl.replace("+", "%20");
        return completeUrl;
    }

    /**
     * Checks Email format
     *
     * @param email
     * @return
     */

    public static boolean isEmailValid(String email) {
        if(TextUtils.isEmpty(email)) {
            return false;
        }
        else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    /**
     * Convert from pixels to DP
     *
     * @param dp
     * @param context
     * @return
     */

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * Convert from Dp to pixels
     *
     * @param px
     * @param context
     * @return
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    /**
     * Hides keyboard
     *
     * @param ctx
     */
    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if(v == null) return;
        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    public static boolean hasArabicCharactersOnly(String characters) {
        String regex = "^[\\p{Arabic}\\s\\p{N}0-9]+$";

        CharSequence inputStr = characters;

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches()) return true;
        else return false;
    }

    public static boolean hasEnglishCharactersOnly(String characters) {
        String regex = "^[\\sa-zA-Z0-9]+$";

        CharSequence inputStr = characters;

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches()) return true;
        else {
            return false;
        }
    }

    public static void openMarkerLocationOnGoogleMapsApp(Context context, String latitude, String longitude, String title) {
        try {
            Double myLatitude = Double.valueOf(latitude);
            Double myLongitude = Double.valueOf(longitude);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + myLatitude + ">,<" + myLongitude + ">?q=<" + myLatitude + ">,<" + myLongitude + ">(" + title + ")"));
            if(intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the file name from URL
     *
     * @param URL
     * @return
     */
    public static String getFileNameFromURl(String URL) {
        String fileName = "";

        int index = URL.lastIndexOf("/");
        fileName = URL.substring(index + 1, URL.length());

        return fileName;
    }

    public static String removeQuotes(String text) {
        return text.replaceAll("^\"|\"$", "");
    }

    public static void openBrowser(Context context, String url) {
        if(!TextUtils.isEmpty(url)) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(browserIntent);
        }
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for(ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if(processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for(String activeProcess : processInfo.pkgList) {
                        if(activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        }
        else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if(componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    // -------------------------------------------

    public static String capitalizeFirstLetter(String text) {
        text = text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
        return text;
    }

    public static String computeHash(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();

        byte[] byteData = digest.digest(input.getBytes());
        return bytesToHex(byteData);

    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2];
        for(int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static void restartActivity(Activity activity, Bundle bundle) {
        Intent intent = activity.getIntent();
        if(bundle != null) intent.putExtras(bundle);
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
        if(upIntent != null) {
            if(NavUtils.shouldUpRecreateTask(activity, upIntent)) {
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
            }
            else {
                // This activity is part of this app's task, so simply
                // navigate up to the logical parent activity.
                NavUtils.navigateUpTo(activity, upIntent);
            }
        }
        else {
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
            while((str = in.readLine()) != null) {
                if(isFirst) isFirst = false;
                else buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        }
        catch(IOException e) {
            BaseCoreApp.getAppComponent().getLogger().log("Error opening asset " + fileName);
        }
        finally {
            if(in != null) {
                try {
                    in.close();
                }
                catch(IOException e) {
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
        if(extras != null) intent.putExtras(extras);
        cxt.startActivity(intent);
    }

    public static boolean isMyServiceRunning(Activity activity, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if(serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------> Resources Utils
    private static final String DRAWABLE_TYPE = "drawable";

    private static final String STRING_TYPE = "string";

    public static int getResIdByName(Context context, String defType, String name) {
        int resource = context.getResources().getIdentifier(name, defType, context.getPackageName());
        return resource;
    }

    public static String getResNameById(Context context, int id) {
        String name = getStringResourceById(context, id);
        String[] imagePath = name.split("/");
        name = imagePath[imagePath.length - 1];
        return name;
    }

    public static int getDrawableIdByName(Context cxt, String name) {
        return getResIdByName(cxt, DRAWABLE_TYPE, name);
    }

    public static int getStringIdByName(Context cxt, String name) {
        return getResIdByName(cxt, STRING_TYPE, name);
    }

    private static String getStringResourceById(Context context, int id) {
        String name;
        name = context.getResources().getString(id);
        return name;
    }

    public static int[] toArray(List<Integer> list) {
        int[] ret = new int[list.size()];
        int i = 0;
        for(Iterator<Integer> it = list.iterator(); it.hasNext(); ret[i++] = it.next()) ;
        return ret;
    }

    // ------------------------------------------------>

    public static void call(Context context, String number) {
        if(context == null || ValidationUtils.isStringEmpty(number)) return;
        try {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + number));
            context.startActivity(callIntent);
        }
        catch(Exception e) {
            UIUtils.showToast(context, "No apps found to call this number");
        }
    }

    public static void sendEmailTo(Activity activity, String email, String subject, String body, String chooserTitle) {
        if(activity == null) return;
        ShareCompat.IntentBuilder.from(activity).setType("message/rfc822").addEmailTo(email).setSubject(subject).setText(body).setChooserTitle(chooserTitle).startChooser();
    }

    public static double round(double value, int places) {
        if(places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    // ------------------------------------------------>

    public static void unBindButterKnife(Unbinder unbinder) {
        if(unbinder != null) {
            unbinder.unbind();
        }
    }

    public static Object genericInvokMethod(Object obj, String methodName, int paramCount, Object... params) {
        Method method;
        Object requiredObj = null;
        Object[] parameters = new Object[paramCount];
        Class<?>[] classArray = new Class<?>[paramCount];
        for(int i = 0; i < paramCount; i++) {
            parameters[i] = params[i];
            classArray[i] = params[i].getClass();
        }
        try {
            method = obj.getClass().getDeclaredMethod(methodName, classArray);
            method.setAccessible(true);
            requiredObj = method.invoke(obj, params);
        }
        catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
        catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch(IllegalAccessException e) {
            e.printStackTrace();
        }
        catch(InvocationTargetException e) {
            e.printStackTrace();
        }

        return requiredObj;
    }

    /**
     * Determine if the device is a tablet (i.e. it has a large screen).
     *
     * @param context The calling context.
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

}
