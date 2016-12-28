package com.abdallaadelessa.core.utils;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.CycleInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.abdallaadelessa.core.R;
import com.abdallaadelessa.core.app.BaseCoreApp;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by abdalla on 28/07/15.
 */
public class UIUtils {

    // ------------------------>

    public static void showToast(Context context, String msg, int... time) {
        if (context == null) return;
        int toastTime = Toast.LENGTH_LONG;
        if (time != null && time.length > 0) {
            toastTime = time[0];
        }
        Toast appToast = BaseCoreApp.getInstance().getAppComponent().getToast();
        if (appToast != null) {
            appToast.setDuration(toastTime);
            appToast.setText(msg);
            appToast.show();
        }
    }

    public static void showToast(Context context, int rid, int... time) {
        showToast(context, context.getString(rid), time);
    }

    // ------------------------>

    public static Bitmap getNotificationBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
            return bm;
        } catch (Exception e) {
            BaseCoreApp.getInstance().getLoggerComponent().getLogger().logError(e);
        }
        return bm;
    }

    public static int getColorWithAlpha(float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        return a + rgb;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    @NonNull
    public static Point getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    @SuppressLint("NewApi")
    public static void setCompactBackgroundDrawable(View view, Drawable drawable) {
        int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(drawable);
        } else {
            view.setBackground(drawable);
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
        Bitmap bitmap = null;
        try {
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if (bitmapDrawable.getBitmap() != null) {
                    return bitmapDrawable.getBitmap();
                }
            }
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        } catch (Throwable e) {
            BaseCoreApp.getInstance().getLoggerComponent().getLogger().logError(e);
        }
        return bitmap;
    }

    public static String convertMillisToMinAndSec(long milliseconds) {
        int seconds = (int) ((milliseconds / 1000) % 60);
        int minutes = (int) ((milliseconds / 1000) / 60);
        if (seconds >= 0 && minutes >= 0) {
            return (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds);
        } else {
            return "00:00";
        }
    }

    public static void setCollapsingEnabled(final boolean enabled, AppBarLayout appBarLayout, RecyclerView rvList) {
        appBarLayout.setExpanded(enabled);
        rvList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return !enabled;
            }
        });
    }

    /**
     * Returns darker version of specified <code>color</code>.
     */
    public static int darker(int color, float factor) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        return Color.argb(a, Math.max((int) (r * factor), 0), Math.max((int) (g * factor), 0), Math.max((int) (b * factor), 0));
    }

    /**
     * Lightens a color by a given factor.
     *
     * @param color  The color to lighten
     * @param factor The factor to lighten the color. 0 will make the color unchanged. 1 will make the
     *               color white.
     * @return lighter version of the specified color.
     */

    public static int lighter(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }

    // ------------------------>

    public static Drawable colorDrawable(Context context, Drawable drawable, int colorRes) {
        if (drawable != null) {
            if (colorRes > 0) {
                drawable.mutate().setColorFilter(ContextCompat.getColor(context, colorRes), PorterDuff.Mode.SRC_ATOP);
            } else {
                drawable.mutate().setColorFilter(null);
            }
        }
        return drawable;
    }

    public static void setEnabled(Context context, View view, boolean enabled) {
        try {
            if (enabled) {
                view.setBackgroundDrawable(UIUtils.colorDrawable(context, view.getBackground(), -1));
            } else {
                view.setBackgroundDrawable(UIUtils.colorDrawable(context, view.getBackground(), R.color.colorBrightGrey));
            }
            view.setEnabled(enabled);
        } catch (Exception e) {
            BaseCoreApp.getInstance().getLoggerComponent().getLogger().logError(e);
        }
    }

    public static void setEnabled(Context context, ImageView imageView, boolean enabled) {
        try {
            if (enabled) {
                imageView.setImageDrawable(UIUtils.colorDrawable(context, imageView.getDrawable(), -1));
            } else {
                imageView.setImageDrawable(UIUtils.colorDrawable(context, imageView.getDrawable(), R.color.colorBrightGrey));
            }
            imageView.setEnabled(enabled);
        } catch (Exception e) {
            BaseCoreApp.getInstance().getLoggerComponent().getLogger().logError(e);
        }
    }

    // ------------------------>

    public static ColorStateList getThemeTint(int disabledColor, int enabledColor) {
        ColorStateList colorStateList = new ColorStateList(new int[][]{

                new int[]{-android.R.attr.state_enabled}, //disabled
                new int[]{android.R.attr.state_enabled} //enabled
        }, new int[]{

                disabledColor //disabled
                , enabledColor //enabled

        });
        return colorStateList;
    }

    public static void setDrawerEnabled(ActionBarDrawerToggle drawerToggle, DrawerLayout drawerLayout, boolean isEnabled) {
        if (isEnabled) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            drawerToggle.onDrawerStateChanged(DrawerLayout.STATE_IDLE);
            drawerToggle.setDrawerIndicatorEnabled(true);
            drawerToggle.syncState();

        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            drawerToggle.onDrawerStateChanged(DrawerLayout.STATE_IDLE);
            drawerToggle.setDrawerIndicatorEnabled(false);
            drawerToggle.syncState();
        }
    }

    public static void shakeView(View view) {
        ObjectAnimator translateAnimator = ObjectAnimator.ofFloat(view, "translationX", 0, 10);
        translateAnimator.setInterpolator(new CycleInterpolator(7));
        translateAnimator.setDuration(700);
        translateAnimator.start();
    }
}
