package org.telegram.messenger;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video;
import android.support.v4.content.FileProvider;
import android.support.v4.internal.view.SupportMenu;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.StateSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.regex.Pattern;
import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;
import net.hockeyapp.android.UpdateManager;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.volley.DefaultRetryPolicy;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.ForegroundDetector;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.NumberPicker.Formatter;
import org.telegram.ui.Components.TypefaceSpan;

public class AndroidUtilities {
    public static final int FLAG_TAG_ALL = 7;
    public static final int FLAG_TAG_BOLD = 2;
    public static final int FLAG_TAG_BR = 1;
    public static final int FLAG_TAG_COLOR = 4;
    public static Pattern WEB_URL;
    private static int adjustOwnerClassGuid = 0;
    private static RectF bitmapRect;
    private static final Object callLock = new Object();
    public static float density = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
    public static DisplayMetrics displayMetrics = new DisplayMetrics();
    public static Point displaySize = new Point();
    public static boolean incorrectDisplaySizeFix;
    public static boolean isInMultiwindow;
    private static Boolean isTablet = null;
    public static int leftBaseline = (isTablet() ? 80 : 72);
    private static Field mAttachInfoField;
    private static Field mStableInsetsField;
    public static Integer photoSize = null;
    private static int prevOrientation = -10;
    private static Paint roundPaint;
    private static final Object smsLock = new Object();
    public static int statusBarHeight = 0;
    private static final Hashtable<String, Typeface> typefaceCache = new Hashtable();
    public static boolean usingHardwareInput;
    private static boolean waitingForCall = false;
    private static boolean waitingForSms = false;

    static {
        WEB_URL = null;
        try {
            String GOOD_IRI_CHAR = "a-zA-Z0-9 -퟿豈-﷏ﷰ-￯";
            String IRI = "[a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯\\-]{0,61}[a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]){0,1}";
            String GOOD_GTLD_CHAR = "a-zA-Z -퟿豈-﷏ﷰ-￯";
            String GTLD = "[a-zA-Z -퟿豈-﷏ﷰ-￯]{2,63}";
            String HOST_NAME = "([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯\\-]{0,61}[a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]){0,1}\\.)+[a-zA-Z -퟿豈-﷏ﷰ-￯]{2,63}";
            WEB_URL = Pattern.compile("((?:(http|https|Http|Https):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?(?:" + Pattern.compile("(([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯\\-]{0,61}[a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]){0,1}\\.)+[a-zA-Z -퟿豈-﷏ﷰ-￯]{2,63}|" + Pattern.compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9]))") + ")") + ")(?:\\:\\d{1,5})?)(\\/(?:(?:[" + "a-zA-Z0-9 -퟿豈-﷏ﷰ-￯" + "\\;\\/\\?\\:\\@\\&\\=\\#\\~\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?(?:\\b|$)");
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
        }
        checkDisplaySize(ApplicationLoader.applicationContext, null);
    }

    public static int[] calcDrawableColor(Drawable drawable) {
        int bitmapColor = -16777216;
        int[] result = new int[2];
        try {
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                if (bitmap != null) {
                    Bitmap b = Bitmaps.createScaledBitmap(bitmap, 1, 1, true);
                    if (b != null) {
                        bitmapColor = b.getPixel(0, 0);
                        b.recycle();
                    }
                }
            } else if (drawable instanceof ColorDrawable) {
                bitmapColor = ((ColorDrawable) drawable).getColor();
            }
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
        }
        double[] hsv = rgbToHsv((bitmapColor >> 16) & 255, (bitmapColor >> 8) & 255, bitmapColor & 255);
        hsv[1] = Math.min(1.0d, (hsv[1] + 0.05d) + (0.1d * (1.0d - hsv[1])));
        hsv[2] = Math.max(0.0d, hsv[2] * 0.65d);
        int[] rgb = hsvToRgb(hsv[0], hsv[1], hsv[2]);
        result[0] = Color.argb(102, rgb[0], rgb[1], rgb[2]);
        result[1] = Color.argb(136, rgb[0], rgb[1], rgb[2]);
        return result;
    }

    private static double[] rgbToHsv(int r, int g, int b) {
        double h;
        double rf = ((double) r) / 255.0d;
        double gf = ((double) g) / 255.0d;
        double bf = ((double) b) / 255.0d;
        double max = (rf <= gf || rf <= bf) ? gf > bf ? gf : bf : rf;
        double min = (rf >= gf || rf >= bf) ? gf < bf ? gf : bf : rf;
        double d = max - min;
        double s = max == 0.0d ? 0.0d : d / max;
        if (max == min) {
            h = 0.0d;
        } else {
            if (rf > gf && rf > bf) {
                h = ((gf - bf) / d) + ((double) (gf < bf ? 6 : 0));
            } else if (gf > bf) {
                h = ((bf - rf) / d) + 2.0d;
            } else {
                h = ((rf - gf) / d) + 4.0d;
            }
            h /= 6.0d;
        }
        return new double[]{h, s, max};
    }

    private static int[] hsvToRgb(double h, double s, double v) {
        double r = 0.0d;
        double g = 0.0d;
        double b = 0.0d;
        double i = (double) ((int) Math.floor(6.0d * h));
        double f = (6.0d * h) - i;
        double p = v * (1.0d - s);
        double q = v * (1.0d - (f * s));
        double t = v * (1.0d - ((1.0d - f) * s));
        switch (((int) i) % 6) {
            case 0:
                r = v;
                g = t;
                b = p;
                break;
            case 1:
                r = q;
                g = v;
                b = p;
                break;
            case 2:
                r = p;
                g = v;
                b = t;
                break;
            case 3:
                r = p;
                g = q;
                b = v;
                break;
            case 4:
                r = t;
                g = p;
                b = v;
                break;
            case 5:
                r = v;
                g = p;
                b = q;
                break;
        }
        return new int[]{(int) (255.0d * r), (int) (255.0d * g), (int) (255.0d * b)};
    }

    public static void requestAdjustResize(Activity activity, int classGuid) {
        if (activity != null && !isTablet()) {
            activity.getWindow().setSoftInputMode(16);
            adjustOwnerClassGuid = classGuid;
        }
    }

    public static void removeAdjustResize(Activity activity, int classGuid) {
        if (activity != null && !isTablet() && adjustOwnerClassGuid == classGuid) {
            activity.getWindow().setSoftInputMode(32);
        }
    }

    public static boolean isGoogleMapsInstalled(final BaseFragment fragment) {
        try {
            ApplicationLoader.applicationContext.getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
            return true;
        } catch (NameNotFoundException e) {
            if (fragment.getParentActivity() == null) {
                return false;
            }
            Builder builder = new Builder(fragment.getParentActivity());
            builder.setMessage("Install Google Maps?");
            builder.setCancelable(true);
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        fragment.getParentActivity().startActivityForResult(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.google.android.apps.maps")), 500);
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            fragment.showDialog(builder.create());
            return false;
        }
    }

    public static boolean isInternalUri(Uri uri) {
        String pathString = uri.getPath();
        if (pathString == null) {
            return false;
        }
        while (true) {
            String path;
            String newPath = Utilities.readlink(pathString);
            if (newPath != null && !newPath.equals(pathString)) {
                pathString = newPath;
            } else if (pathString != null) {
                try {
                    path = new File(pathString).getCanonicalPath();
                    if (path != null) {
                        pathString = path;
                    }
                } catch (Exception e) {
                    pathString.replace("/./", "/");
                }
            }
        }
        if (pathString != null) {
            path = new File(pathString).getCanonicalPath();
            if (path != null) {
                pathString = path;
            }
        }
        if (pathString == null || !pathString.toLowerCase().contains("/data/data/" + ApplicationLoader.applicationContext.getPackageName() + "/files")) {
            return false;
        }
        return true;
    }

    public static void lockOrientation(Activity activity) {
        if (activity != null && prevOrientation == -10) {
            try {
                prevOrientation = activity.getRequestedOrientation();
                WindowManager manager = (WindowManager) activity.getSystemService("window");
                if (manager != null && manager.getDefaultDisplay() != null) {
                    int rotation = manager.getDefaultDisplay().getRotation();
                    int orientation = activity.getResources().getConfiguration().orientation;
                    if (rotation == 3) {
                        if (orientation == 1) {
                            activity.setRequestedOrientation(1);
                        } else {
                            activity.setRequestedOrientation(8);
                        }
                    } else if (rotation == 1) {
                        if (orientation == 1) {
                            activity.setRequestedOrientation(9);
                        } else {
                            activity.setRequestedOrientation(0);
                        }
                    } else if (rotation == 0) {
                        if (orientation == 2) {
                            activity.setRequestedOrientation(0);
                        } else {
                            activity.setRequestedOrientation(1);
                        }
                    } else if (orientation == 2) {
                        activity.setRequestedOrientation(8);
                    } else {
                        activity.setRequestedOrientation(9);
                    }
                }
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
        }
    }

    public static void unlockOrientation(Activity activity) {
        if (activity != null) {
            try {
                if (prevOrientation != -10) {
                    activity.setRequestedOrientation(prevOrientation);
                    prevOrientation = -10;
                }
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
        }
    }

    public static Typeface getTypeface(String assetPath) {
        Typeface typeface;
        synchronized (typefaceCache) {
            if (!typefaceCache.containsKey(assetPath)) {
                try {
                    typefaceCache.put(assetPath, Typeface.createFromAsset(ApplicationLoader.applicationContext.getAssets(), assetPath));
                } catch (Exception e) {
                    FileLog.e("Typefaces", "Could not get typeface '" + assetPath + "' because " + e.getMessage());
                    typeface = null;
                }
            }
            typeface = (Typeface) typefaceCache.get(assetPath);
        }
        return typeface;
    }

    public static boolean isWaitingForSms() {
        boolean value;
        synchronized (smsLock) {
            value = waitingForSms;
        }
        return value;
    }

    public static void setWaitingForSms(boolean value) {
        synchronized (smsLock) {
            waitingForSms = value;
        }
    }

    public static boolean isWaitingForCall() {
        boolean value;
        synchronized (callLock) {
            value = waitingForCall;
        }
        return value;
    }

    public static void setWaitingForCall(boolean value) {
        synchronized (callLock) {
            waitingForCall = value;
        }
    }

    public static void showKeyboard(View view) {
        if (view != null) {
            try {
                ((InputMethodManager) view.getContext().getSystemService("input_method")).showSoftInput(view, 1);
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
        }
    }

    public static boolean isKeyboardShowed(View view) {
        boolean z = false;
        if (view != null) {
            try {
                z = ((InputMethodManager) view.getContext().getSystemService("input_method")).isActive(view);
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
        }
        return z;
    }

    public static void hideKeyboard(View view) {
        if (view != null) {
            try {
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService("input_method");
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
        }
    }

    public static File getCacheDir() {
        File file;
        String state = null;
        try {
            state = Environment.getExternalStorageState();
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
        }
        if (state == null || state.startsWith("mounted")) {
            try {
                file = ApplicationLoader.applicationContext.getExternalCacheDir();
                if (file != null) {
                    return file;
                }
            } catch (Throwable e2) {
                FileLog.e("tmessages", e2);
            }
        }
        try {
            file = ApplicationLoader.applicationContext.getCacheDir();
            if (file != null) {
                return file;
            }
        } catch (Throwable e22) {
            FileLog.e("tmessages", e22);
        }
        return new File("");
    }

    public static int dp(float value) {
        if (value == 0.0f) {
            return 0;
        }
        return (int) Math.ceil((double) (density * value));
    }

    public static int compare(int lhs, int rhs) {
        if (lhs == rhs) {
            return 0;
        }
        if (lhs > rhs) {
            return 1;
        }
        return -1;
    }

    public static float dpf2(float value) {
        if (value == 0.0f) {
            return 0.0f;
        }
        return density * value;
    }

    public static void checkDisplaySize(Context context, Configuration newConfiguration) {
        boolean z = true;
        try {
            int newSize;
            density = context.getResources().getDisplayMetrics().density;
            Configuration configuration = newConfiguration;
            if (configuration == null) {
                configuration = context.getResources().getConfiguration();
            }
            if (configuration.keyboard == 1 || configuration.hardKeyboardHidden != 1) {
                z = false;
            }
            usingHardwareInput = z;
            WindowManager manager = (WindowManager) context.getSystemService("window");
            if (manager != null) {
                Display display = manager.getDefaultDisplay();
                if (display != null) {
                    display.getMetrics(displayMetrics);
                    display.getSize(displaySize);
                }
            }
            if (configuration.screenWidthDp != 0) {
                newSize = (int) Math.ceil((double) (((float) configuration.screenWidthDp) * density));
                if (Math.abs(displaySize.x - newSize) > 3) {
                    displaySize.x = newSize;
                }
            }
            if (configuration.screenHeightDp != 0) {
                newSize = (int) Math.ceil((double) (((float) configuration.screenHeightDp) * density));
                if (Math.abs(displaySize.y - newSize) > 3) {
                    displaySize.y = newSize;
                }
            }
            FileLog.e("tmessages", "display size = " + displaySize.x + " " + displaySize.y + " " + displayMetrics.xdpi + "x" + displayMetrics.ydpi);
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
        }
    }

    public static float getPixelsInCM(float cm, boolean isX) {
        return (isX ? displayMetrics.xdpi : displayMetrics.ydpi) * (cm / 2.54f);
    }

    public static long makeBroadcastId(int id) {
        return 4294967296L | (((long) id) & 4294967295L);
    }

    public static int getMyLayerVersion(int layer) {
        return SupportMenu.USER_MASK & layer;
    }

    public static int getPeerLayerVersion(int layer) {
        return (layer >> 16) & SupportMenu.USER_MASK;
    }

    public static int setMyLayerVersion(int layer, int version) {
        return (SupportMenu.CATEGORY_MASK & layer) | version;
    }

    public static int setPeerLayerVersion(int layer, int version) {
        return (SupportMenu.USER_MASK & layer) | (version << 16);
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            ApplicationLoader.applicationHandler.post(runnable);
        } else {
            ApplicationLoader.applicationHandler.postDelayed(runnable, delay);
        }
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        ApplicationLoader.applicationHandler.removeCallbacks(runnable);
    }

    public static boolean isTablet() {
        if (isTablet == null) {
            isTablet = Boolean.valueOf(ApplicationLoader.applicationContext.getResources().getBoolean(R.bool.isTablet));
        }
        return isTablet.booleanValue();
    }

    public static boolean isSmallTablet() {
        return ((float) Math.min(displaySize.x, displaySize.y)) / density <= 700.0f;
    }

    public static int getMinTabletSide() {
        int leftSide;
        if (isSmallTablet()) {
            int smallSide = Math.min(displaySize.x, displaySize.y);
            int maxSide = Math.max(displaySize.x, displaySize.y);
            leftSide = (maxSide * 35) / 100;
            if (leftSide < dp(320.0f)) {
                leftSide = dp(320.0f);
            }
            return Math.min(smallSide, maxSide - leftSide);
        }
        smallSide = Math.min(displaySize.x, displaySize.y);
        leftSide = (smallSide * 35) / 100;
        if (leftSide < dp(320.0f)) {
            leftSide = dp(320.0f);
        }
        return smallSide - leftSide;
    }

    public static int getPhotoSize() {
        if (photoSize == null) {
            if (VERSION.SDK_INT >= 16) {
                photoSize = Integer.valueOf(1280);
            } else {
                photoSize = Integer.valueOf(800);
            }
        }
        return photoSize.intValue();
    }

    public static String formatTTLString(int ttl) {
        if (ttl < 60) {
            return LocaleController.formatPluralString("Seconds", ttl);
        }
        if (ttl < 3600) {
            return LocaleController.formatPluralString("Minutes", ttl / 60);
        }
        if (ttl < 86400) {
            return LocaleController.formatPluralString("Hours", (ttl / 60) / 60);
        }
        if (ttl < 604800) {
            return LocaleController.formatPluralString("Days", ((ttl / 60) / 60) / 24);
        }
        int days = ((ttl / 60) / 60) / 24;
        if (ttl % 7 == 0) {
            return LocaleController.formatPluralString("Weeks", days / 7);
        }
        return String.format("%s %s", new Object[]{LocaleController.formatPluralString("Weeks", days / 7), LocaleController.formatPluralString("Days", days % 7)});
    }

    public static Builder buildTTLAlert(Context context, final EncryptedChat encryptedChat) {
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("MessageLifetime", R.string.MessageLifetime));
        final NumberPicker numberPicker = new NumberPicker(context);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(20);
        if (encryptedChat.ttl > 0 && encryptedChat.ttl < 16) {
            numberPicker.setValue(encryptedChat.ttl);
        } else if (encryptedChat.ttl == 30) {
            numberPicker.setValue(16);
        } else if (encryptedChat.ttl == 60) {
            numberPicker.setValue(17);
        } else if (encryptedChat.ttl == 3600) {
            numberPicker.setValue(18);
        } else if (encryptedChat.ttl == 86400) {
            numberPicker.setValue(19);
        } else if (encryptedChat.ttl == 604800) {
            numberPicker.setValue(20);
        } else if (encryptedChat.ttl == 0) {
            numberPicker.setValue(0);
        }
        numberPicker.setFormatter(new Formatter() {
            public String format(int value) {
                if (value == 0) {
                    return LocaleController.getString("ShortMessageLifetimeForever", R.string.ShortMessageLifetimeForever);
                }
                if (value >= 1 && value < 16) {
                    return AndroidUtilities.formatTTLString(value);
                }
                if (value == 16) {
                    return AndroidUtilities.formatTTLString(30);
                }
                if (value == 17) {
                    return AndroidUtilities.formatTTLString(60);
                }
                if (value == 18) {
                    return AndroidUtilities.formatTTLString(3600);
                }
                if (value == 19) {
                    return AndroidUtilities.formatTTLString(86400);
                }
                if (value == 20) {
                    return AndroidUtilities.formatTTLString(604800);
                }
                return "";
            }
        });
        builder.setView(numberPicker);
        builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int oldValue = encryptedChat.ttl;
                which = numberPicker.getValue();
                if (which >= 0 && which < 16) {
                    encryptedChat.ttl = which;
                } else if (which == 16) {
                    encryptedChat.ttl = 30;
                } else if (which == 17) {
                    encryptedChat.ttl = 60;
                } else if (which == 18) {
                    encryptedChat.ttl = 3600;
                } else if (which == 19) {
                    encryptedChat.ttl = 86400;
                } else if (which == 20) {
                    encryptedChat.ttl = 604800;
                }
                if (oldValue != encryptedChat.ttl) {
                    SecretChatHelper.getInstance().sendTTLMessage(encryptedChat, null);
                    MessagesStorage.getInstance().updateEncryptedChatTTL(encryptedChat);
                }
            }
        });
        return builder;
    }

    public static void clearCursorDrawable(EditText editText) {
        if (editText != null) {
            try {
                Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
                mCursorDrawableRes.setAccessible(true);
                mCursorDrawableRes.setInt(editText, 0);
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
        }
    }

    public static void setProgressBarAnimationDuration(ProgressBar progressBar, int duration) {
        if (progressBar != null) {
            try {
                Field mCursorDrawableRes = ProgressBar.class.getDeclaredField("mDuration");
                mCursorDrawableRes.setAccessible(true);
                mCursorDrawableRes.setInt(progressBar, duration);
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
        }
    }

    private static Intent createShortcutIntent(long did, boolean forDelete) {
        Intent intent = new Intent(ApplicationLoader.applicationContext, OpenChatReceiver.class);
        int lower_id = (int) did;
        int high_id = (int) (did >> 32);
        User user = null;
        Chat chat = null;
        if (lower_id == 0) {
            intent.putExtra("encId", high_id);
            EncryptedChat encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(high_id));
            if (encryptedChat == null) {
                return null;
            }
            user = MessagesController.getInstance().getUser(Integer.valueOf(encryptedChat.user_id));
        } else if (lower_id > 0) {
            intent.putExtra("userId", lower_id);
            user = MessagesController.getInstance().getUser(Integer.valueOf(lower_id));
        } else if (lower_id >= 0) {
            return null;
        } else {
            chat = MessagesController.getInstance().getChat(Integer.valueOf(-lower_id));
            intent.putExtra("chatId", -lower_id);
        }
        if (user == null && chat == null) {
            return null;
        }
        String name;
        TLObject photo = null;
        if (user != null) {
            name = ContactsController.formatName(user.first_name, user.last_name);
            if (user.photo != null) {
                photo = user.photo.photo_small;
            }
        } else {
            name = chat.title;
            if (chat.photo != null) {
                photo = chat.photo.photo_small;
            }
        }
        intent.setAction("com.tmessages.openchat" + did);
        intent.addFlags(67108864);
        Intent addIntent = new Intent();
        addIntent.putExtra("android.intent.extra.shortcut.INTENT", intent);
        addIntent.putExtra("android.intent.extra.shortcut.NAME", name);
        addIntent.putExtra("duplicate", false);
        if (forDelete) {
            return addIntent;
        }
        Bitmap bitmap = null;
        if (photo != null) {
            try {
                bitmap = BitmapFactory.decodeFile(FileLoader.getPathToAttach(photo, true).toString());
                if (bitmap != null) {
                    int size = dp(58.0f);
                    Bitmap result = Bitmap.createBitmap(size, size, Config.ARGB_8888);
                    result.eraseColor(0);
                    Canvas canvas = new Canvas(result);
                    Shader bitmapShader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
                    if (roundPaint == null) {
                        roundPaint = new Paint(1);
                        bitmapRect = new RectF();
                    }
                    float scale = ((float) size) / ((float) bitmap.getWidth());
                    canvas.save();
                    canvas.scale(scale, scale);
                    roundPaint.setShader(bitmapShader);
                    bitmapRect.set(0.0f, 0.0f, (float) bitmap.getWidth(), (float) bitmap.getHeight());
                    canvas.drawRoundRect(bitmapRect, (float) bitmap.getWidth(), (float) bitmap.getHeight(), roundPaint);
                    canvas.restore();
                    Drawable drawable = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.book_logo);
                    int w = dp(15.0f);
                    int left = (size - w) - dp(2.0f);
                    int top = (size - w) - dp(2.0f);
                    drawable.setBounds(left, top, left + w, top + w);
                    drawable.draw(canvas);
                    try {
                        canvas.setBitmap(null);
                    } catch (Exception e) {
                    }
                    bitmap = result;
                }
            } catch (Throwable e2) {
                FileLog.e("tmessages", e2);
            }
        }
        if (bitmap != null) {
            addIntent.putExtra("android.intent.extra.shortcut.ICON", bitmap);
            return addIntent;
        } else if (user != null) {
            if (user.bot) {
                addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_bot));
                return addIntent;
            }
            addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_user));
            return addIntent;
        } else if (chat == null) {
            return addIntent;
        } else {
            if (!ChatObject.isChannel(chat) || chat.megagroup) {
                addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_group));
                return addIntent;
            }
            addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_channel));
            return addIntent;
        }
    }

    public static void installShortcut(long did) {
        try {
            Intent addIntent = createShortcutIntent(did, false);
            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            ApplicationLoader.applicationContext.sendBroadcast(addIntent);
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
        }
    }

    public static void uninstallShortcut(long did) {
        try {
            Intent addIntent = createShortcutIntent(did, true);
            addIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
            ApplicationLoader.applicationContext.sendBroadcast(addIntent);
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
        }
    }

    public static int getViewInset(View view) {
        int i = 0;
        if (!(view == null || VERSION.SDK_INT < 21 || view.getHeight() == displaySize.y || view.getHeight() == displaySize.y - statusBarHeight)) {
            try {
                if (mAttachInfoField == null) {
                    mAttachInfoField = View.class.getDeclaredField("mAttachInfo");
                    mAttachInfoField.setAccessible(true);
                }
                Object mAttachInfo = mAttachInfoField.get(view);
                if (mAttachInfo != null) {
                    if (mStableInsetsField == null) {
                        mStableInsetsField = mAttachInfo.getClass().getDeclaredField("mStableInsets");
                        mStableInsetsField.setAccessible(true);
                    }
                    i = ((Rect) mStableInsetsField.get(mAttachInfo)).bottom;
                }
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
        }
        return i;
    }

    public static Point getRealScreenSize() {
        Point size = new Point();
        try {
            WindowManager windowManager = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
            if (VERSION.SDK_INT >= 17) {
                windowManager.getDefaultDisplay().getRealSize(size);
            } else {
                try {
                    size.set(((Integer) Display.class.getMethod("getRawWidth", new Class[0]).invoke(windowManager.getDefaultDisplay(), new Object[0])).intValue(), ((Integer) Display.class.getMethod("getRawHeight", new Class[0]).invoke(windowManager.getDefaultDisplay(), new Object[0])).intValue());
                } catch (Throwable e) {
                    size.set(windowManager.getDefaultDisplay().getWidth(), windowManager.getDefaultDisplay().getHeight());
                    FileLog.e("tmessages", e);
                }
            }
        } catch (Throwable e2) {
            FileLog.e("tmessages", e2);
        }
        return size;
    }

    public static CharSequence getTrimmedString(CharSequence src) {
        if (!(src == null || src.length() == 0)) {
            while (src.length() > 0 && (src.charAt(0) == '\n' || src.charAt(0) == ' ')) {
                src = src.subSequence(1, src.length());
            }
            while (src.length() > 0 && (src.charAt(src.length() - 1) == '\n' || src.charAt(src.length() - 1) == ' ')) {
                src = src.subSequence(0, src.length() - 1);
            }
        }
        return src;
    }

    public static void setListViewEdgeEffectColor(AbsListView listView, int color) {
        if (VERSION.SDK_INT >= 21) {
            try {
                Field field = AbsListView.class.getDeclaredField("mEdgeGlowTop");
                field.setAccessible(true);
                EdgeEffect mEdgeGlowTop = (EdgeEffect) field.get(listView);
                if (mEdgeGlowTop != null) {
                    mEdgeGlowTop.setColor(color);
                }
                field = AbsListView.class.getDeclaredField("mEdgeGlowBottom");
                field.setAccessible(true);
                EdgeEffect mEdgeGlowBottom = (EdgeEffect) field.get(listView);
                if (mEdgeGlowBottom != null) {
                    mEdgeGlowBottom.setColor(color);
                }
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
        }
    }

    @SuppressLint({"NewApi"})
    public static void clearDrawableAnimation(View view) {
        if (VERSION.SDK_INT >= 21 && view != null) {
            Drawable drawable;
            if (view instanceof ListView) {
                drawable = ((ListView) view).getSelector();
                if (drawable != null) {
                    drawable.setState(StateSet.NOTHING);
                    return;
                }
                return;
            }
            drawable = view.getBackground();
            if (drawable != null) {
                drawable.setState(StateSet.NOTHING);
                drawable.jumpToCurrentState();
            }
        }
    }

    public static SpannableStringBuilder replaceTags(String str) {
        return replaceTags(str, 7);
    }

    public static SpannableStringBuilder replaceTags(String str, int flag) {
        try {
            int start;
            int end;
            int a;
            StringBuilder stringBuilder = new StringBuilder(str);
            if ((flag & 1) != 0) {
                while (true) {
                    start = stringBuilder.indexOf("<br>");
                    if (start != -1) {
                        stringBuilder.replace(start, start + 4, "\n");
                    } else {
                        while (true) {
                            stringBuilder.replace(start, start + 5, "\n");
                        }
                    }
                }
                start = stringBuilder.indexOf("<br/>");
                if (start == -1) {
                    break;
                }
                stringBuilder.replace(start, start + 5, "\n");
            }
            ArrayList<Integer> bolds = new ArrayList();
            if ((flag & 2) != 0) {
                while (true) {
                    start = stringBuilder.indexOf("<b>");
                    if (start == -1) {
                        break;
                    }
                    stringBuilder.replace(start, start + 3, "");
                    end = stringBuilder.indexOf("</b>");
                    if (end == -1) {
                        end = stringBuilder.indexOf("<b>");
                    }
                    stringBuilder.replace(end, end + 4, "");
                    bolds.add(Integer.valueOf(start));
                    bolds.add(Integer.valueOf(end));
                }
            }
            ArrayList<Integer> colors = new ArrayList();
            if ((flag & 4) != 0) {
                while (true) {
                    start = stringBuilder.indexOf("<c#");
                    if (start == -1) {
                        break;
                    }
                    stringBuilder.replace(start, start + 2, "");
                    end = stringBuilder.indexOf(">", start);
                    int color = Color.parseColor(stringBuilder.substring(start, end));
                    stringBuilder.replace(start, end + 1, "");
                    end = stringBuilder.indexOf("</c>");
                    stringBuilder.replace(end, end + 4, "");
                    colors.add(Integer.valueOf(start));
                    colors.add(Integer.valueOf(end));
                    colors.add(Integer.valueOf(color));
                }
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringBuilder);
            for (a = 0; a < bolds.size() / 2; a++) {
                spannableStringBuilder.setSpan(new TypefaceSpan(getTypeface("fonts/rmedium.ttf")), ((Integer) bolds.get(a * 2)).intValue(), ((Integer) bolds.get((a * 2) + 1)).intValue(), 33);
            }
            for (a = 0; a < colors.size() / 3; a++) {
                spannableStringBuilder.setSpan(new ForegroundColorSpan(((Integer) colors.get((a * 3) + 2)).intValue()), ((Integer) colors.get(a * 3)).intValue(), ((Integer) colors.get((a * 3) + 1)).intValue(), 33);
            }
            return spannableStringBuilder;
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
            return new SpannableStringBuilder(str);
        }
    }

    public static boolean needShowPasscode(boolean reset) {
        boolean wasInBackground = ForegroundDetector.getInstance().isWasInBackground(reset);
        if (reset) {
            ForegroundDetector.getInstance().resetBackgroundVar();
        }
        return UserConfig.passcodeHash.length() > 0 && wasInBackground && (UserConfig.appLocked || !(UserConfig.autoLockIn == 0 || UserConfig.lastPauseTime == 0 || UserConfig.appLocked || UserConfig.lastPauseTime + UserConfig.autoLockIn > ConnectionsManager.getInstance().getCurrentTime()));
    }

    public static void shakeView(final View view, final float x, final int num) {
        if (num == 6) {
            view.setTranslationX(0.0f);
            return;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[1];
        animatorArr[0] = ObjectAnimator.ofFloat(view, "translationX", new float[]{(float) dp(x)});
        animatorSet.playTogether(animatorArr);
        animatorSet.setDuration(50);
        animatorSet.addListener(new AnimatorListenerAdapterProxy() {
            public void onAnimationEnd(Animator animation) {
                AndroidUtilities.shakeView(view, num == 5 ? 0.0f : -x, num + 1);
            }
        });
        animatorSet.start();
    }

    public static void checkForCrashes(Activity context) {
        CrashManager.register(context, BuildVars.DEBUG_VERSION ? BuildVars.HOCKEY_APP_HASH_DEBUG : BuildVars.HOCKEY_APP_HASH, new CrashManagerListener() {
            public boolean includeDeviceData() {
                return true;
            }
        });
    }

    public static void checkForUpdates(Activity context) {
        if (BuildVars.DEBUG_VERSION) {
            UpdateManager.register(context, BuildVars.DEBUG_VERSION ? BuildVars.HOCKEY_APP_HASH_DEBUG : BuildVars.HOCKEY_APP_HASH);
        }
    }

    public static void unregisterUpdates() {
        if (BuildVars.DEBUG_VERSION) {
            UpdateManager.unregister();
        }
    }

    public static void addToClipboard(CharSequence str) {
        try {
            ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", str));
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
        }
    }

    public static void addMediaToGallery(String fromPath) {
        if (fromPath != null) {
            addMediaToGallery(Uri.fromFile(new File(fromPath)));
        }
    }

    public static void addMediaToGallery(Uri uri) {
        if (uri != null) {
            try {
                Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                mediaScanIntent.setData(uri);
                ApplicationLoader.applicationContext.sendBroadcast(mediaScanIntent);
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
        }
    }

    private static File getAlbumDir() {
        if (VERSION.SDK_INT >= 23 && ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
            return FileLoader.getInstance().getDirectory(4);
        }
        if ("mounted".equals(Environment.getExternalStorageState())) {
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Telegram");
            if (storageDir.mkdirs() || storageDir.exists()) {
                return storageDir;
            }
            FileLog.d("tmessages", "failed to create directory");
            return null;
        }
        FileLog.d("tmessages", "External storage is not mounted READ/WRITE.");
        return null;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @SuppressLint({"NewApi"})
    public static String getPath(Uri uri) {
        Object obj = null;
        try {
            if ((VERSION.SDK_INT >= 19) && DocumentsContract.isDocumentUri(ApplicationLoader.applicationContext, uri)) {
                String[] split;
                if (isExternalStorageDocument(uri)) {
                    split = DocumentsContract.getDocumentId(uri).split(":");
                    if ("primary".equalsIgnoreCase(split[0])) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                    return null;
                } else if (isDownloadsDocument(uri)) {
                    return getDataColumn(ApplicationLoader.applicationContext, ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(DocumentsContract.getDocumentId(uri)).longValue()), null, null);
                } else if (!isMediaDocument(uri)) {
                    return null;
                } else {
                    String type = DocumentsContract.getDocumentId(uri).split(":")[0];
                    Uri contentUri = null;
                    switch (type.hashCode()) {
                        case 93166550:
                            if (type.equals(MimeTypes.BASE_TYPE_AUDIO)) {
                                obj = 2;
                                break;
                            }
                        case 100313435:
                            if (type.equals("image")) {
                                break;
                            }
                        case 112202875:
                            if (type.equals(MimeTypes.BASE_TYPE_VIDEO)) {
                                int i = 1;
                                break;
                            }
                        default:
                            obj = -1;
                            break;
                    }
                    switch (obj) {
                        case null:
                            contentUri = Media.EXTERNAL_CONTENT_URI;
                            break;
                        case 1:
                            contentUri = Video.Media.EXTERNAL_CONTENT_URI;
                            break;
                        case 2:
                            contentUri = Audio.Media.EXTERNAL_CONTENT_URI;
                            break;
                    }
                    String selection = "_id=?";
                    return getDataColumn(ApplicationLoader.applicationContext, contentUri, "_id=?", new String[]{split[1]});
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(ApplicationLoader.applicationContext, uri, null, null);
            } else {
                if ("file".equalsIgnoreCase(uri.getScheme())) {
                    return uri.getPath();
                }
                return null;
            }
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
            return null;
        }
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = "_data";
        try {
            cursor = context.getContentResolver().query(uri, new String[]{"_data"}, selection, selectionArgs, null);
            if (cursor == null || !cursor.moveToFirst()) {
                if (cursor != null) {
                    cursor.close();
                }
                return null;
            }
            String value = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
            if (value.startsWith("content://") || !(value.startsWith("/") || value.startsWith("file://"))) {
                if (cursor != null) {
                    cursor.close();
                }
                return null;
            } else if (cursor == null) {
                return value;
            } else {
                cursor.close();
                return value;
            }
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static File generatePicturePath() {
        try {
            return new File(getAlbumDir(), "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".jpg");
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
            return null;
        }
    }

    public static CharSequence generateSearchName(String name, String name2, String q) {
        if (name == null && name2 == null) {
            return "";
        }
        CharSequence builder = new SpannableStringBuilder();
        String wholeString = name;
        if (wholeString == null || wholeString.length() == 0) {
            wholeString = name2;
        } else if (!(name2 == null || name2.length() == 0)) {
            wholeString = wholeString + " " + name2;
        }
        wholeString = wholeString.trim();
        String lower = " " + wholeString.toLowerCase();
        int lastIndex = 0;
        while (true) {
            int index = lower.indexOf(" " + q, lastIndex);
            if (index == -1) {
                break;
            }
            int i;
            if (index == 0) {
                i = 0;
            } else {
                i = 1;
            }
            int idx = index - i;
            int length = q.length();
            if (index == 0) {
                i = 0;
            } else {
                i = 1;
            }
            int end = (i + length) + idx;
            if (lastIndex != 0 && lastIndex != idx + 1) {
                builder.append(wholeString.substring(lastIndex, idx));
            } else if (lastIndex == 0 && idx != 0) {
                builder.append(wholeString.substring(0, idx));
            }
            String query = wholeString.substring(idx, end);
            if (query.startsWith(" ")) {
                builder.append(" ");
            }
            builder.append(replaceTags("<c#ff4d83b3>" + query.trim() + "</c>"));
            lastIndex = end;
        }
        if (lastIndex == -1 || lastIndex == wholeString.length()) {
            return builder;
        }
        builder.append(wholeString.substring(lastIndex, wholeString.length()));
        return builder;
    }

    public static File generateVideoPath() {
        try {
            return new File(getAlbumDir(), "VID_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4");
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
            return null;
        }
    }

    public static String formatFileSize(long size) {
        if (size < 1024) {
            return String.format("%d B", new Object[]{Long.valueOf(size)});
        } else if (size < 1048576) {
            return String.format("%.1f KB", new Object[]{Float.valueOf(((float) size) / 1024.0f)});
        } else if (size < NUM) {
            return String.format("%.1f MB", new Object[]{Float.valueOf((((float) size) / 1024.0f) / 1024.0f)});
        } else {
            return String.format("%.1f GB", new Object[]{Float.valueOf(((((float) size) / 1024.0f) / 1024.0f) / 1024.0f)});
        }
    }

    public static byte[] decodeQuotedPrintable(byte[] bytes) {
        byte[] bArr = null;
        if (bytes != null) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int i = 0;
            while (i < bytes.length) {
                int b = bytes[i];
                if (b == 61) {
                    i++;
                    try {
                        int u = Character.digit((char) bytes[i], 16);
                        i++;
                        buffer.write((char) ((u << 4) + Character.digit((char) bytes[i], 16)));
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                } else {
                    buffer.write(b);
                }
                i++;
            }
            bArr = buffer.toByteArray();
            try {
                buffer.close();
            } catch (Throwable e2) {
                FileLog.e("tmessages", e2);
            }
        }
        return bArr;
    }

    public static boolean copyFile(InputStream sourceFile, File destFile) throws IOException {
        OutputStream out = new FileOutputStream(destFile);
        byte[] buf = new byte[4096];
        while (true) {
            int len = sourceFile.read(buf);
            if (len > 0) {
                Thread.yield();
                out.write(buf, 0, len);
            } else {
                out.close();
                return true;
            }
        }
    }

    public static boolean copyFile(File sourceFile, File destFile) throws IOException {
        Throwable e;
        Throwable th;
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileInputStream source = null;
        FileOutputStream destination = null;
        try {
            FileInputStream source2 = new FileInputStream(sourceFile);
            try {
                FileOutputStream destination2 = new FileOutputStream(destFile);
                try {
                    destination2.getChannel().transferFrom(source2.getChannel(), 0, source2.getChannel().size());
                    if (source2 != null) {
                        source2.close();
                    }
                    if (destination2 != null) {
                        destination2.close();
                    }
                    destination = destination2;
                    source = source2;
                    return true;
                } catch (Exception e2) {
                    e = e2;
                    destination = destination2;
                    source = source2;
                    try {
                        FileLog.e("tmessages", e);
                        if (source != null) {
                            source.close();
                        }
                        if (destination != null) {
                            return false;
                        }
                        destination.close();
                        return false;
                    } catch (Throwable th2) {
                        th = th2;
                        if (source != null) {
                            source.close();
                        }
                        if (destination != null) {
                            destination.close();
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    destination = destination2;
                    source = source2;
                    if (source != null) {
                        source.close();
                    }
                    if (destination != null) {
                        destination.close();
                    }
                    throw th;
                }
            } catch (Exception e3) {
                e = e3;
                source = source2;
                FileLog.e("tmessages", e);
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    return false;
                }
                destination.close();
                return false;
            } catch (Throwable th4) {
                th = th4;
                source = source2;
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    destination.close();
                }
                throw th;
            }
        } catch (Exception e4) {
            e = e4;
            FileLog.e("tmessages", e);
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                return false;
            }
            destination.close();
            return false;
        }
    }

    public static byte[] calcAuthKeyHash(byte[] auth_key) {
        byte[] key_hash = new byte[16];
        System.arraycopy(Utilities.computeSHA1(auth_key), 0, key_hash, 0, 16);
        return key_hash;
    }

    public static void openForView(MessageObject message, Activity activity) throws Exception {
        File f = null;
        String fileName = message.getFileName();
        if (!(message.messageOwner.attachPath == null || message.messageOwner.attachPath.length() == 0)) {
            f = new File(message.messageOwner.attachPath);
        }
        if (f == null || !f.exists()) {
            f = FileLoader.getPathToMessage(message.messageOwner);
        }
        if (f != null && f.exists()) {
            String realMimeType = null;
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setFlags(1);
            MimeTypeMap myMime = MimeTypeMap.getSingleton();
            int idx = fileName.lastIndexOf(46);
            if (idx != -1) {
                realMimeType = myMime.getMimeTypeFromExtension(fileName.substring(idx + 1).toLowerCase());
                if (realMimeType == null) {
                    if (message.type == 9 || message.type == 0) {
                        realMimeType = message.getDocument().mime_type;
                    }
                    if (realMimeType == null || realMimeType.length() == 0) {
                        realMimeType = null;
                    }
                }
            }
            if (VERSION.SDK_INT >= 24) {
                intent.setDataAndType(FileProvider.getUriForFile(activity, "org.telegram.messenger.beta.provider", f), realMimeType != null ? realMimeType : "text/plain");
            } else {
                intent.setDataAndType(Uri.fromFile(f), realMimeType != null ? realMimeType : "text/plain");
            }
            if (realMimeType != null) {
                try {
                    activity.startActivityForResult(intent, 500);
                    return;
                } catch (Exception e) {
                    if (VERSION.SDK_INT >= 24) {
                        intent.setDataAndType(FileProvider.getUriForFile(activity, "org.telegram.messenger.beta.provider", f), "text/plain");
                    } else {
                        intent.setDataAndType(Uri.fromFile(f), "text/plain");
                    }
                    activity.startActivityForResult(intent, 500);
                    return;
                }
            }
            activity.startActivityForResult(intent, 500);
        }
    }

    public static void setRectToRect(Matrix matrix, RectF src, RectF dst, int rotation, ScaleToFit align) {
        float sx;
        float sy;
        if (rotation == 90 || rotation == 270) {
            sx = dst.height() / src.width();
            sy = dst.width() / src.height();
        } else {
            sx = dst.width() / src.width();
            sy = dst.height() / src.height();
        }
        if (align != ScaleToFit.FILL) {
            if (sx > sy) {
                sx = sy;
            } else {
                sy = sx;
            }
        }
        float tx = (-src.left) * sx;
        float ty = (-src.top) * sy;
        matrix.setTranslate(dst.left, dst.top);
        if (rotation == 90) {
            matrix.preRotate(90.0f);
            matrix.preTranslate(0.0f, -dst.width());
        } else if (rotation == 180) {
            matrix.preRotate(BitmapDescriptorFactory.HUE_CYAN);
            matrix.preTranslate(-dst.width(), -dst.height());
        } else if (rotation == 270) {
            matrix.preRotate(BitmapDescriptorFactory.HUE_VIOLET);
            matrix.preTranslate(-dst.height(), 0.0f);
        }
        matrix.preScale(sx, sy);
        matrix.preTranslate(tx, ty);
    }
}
