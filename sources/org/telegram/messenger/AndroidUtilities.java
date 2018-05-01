package org.telegram.messenger;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.provider.CallLog.Calls;
import android.provider.DocumentsContract;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.EdgeEffectCompat;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.StateSet;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EdgeEffect;
import android.widget.ListView;
import android.widget.ScrollView;
import com.android.internal.telephony.ITelephony;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.regex.Pattern;
import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;
import net.hockeyapp.android.UpdateManager;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ForegroundDetector;
import org.telegram.ui.Components.TypefaceSpan;

public class AndroidUtilities {
    public static final int FLAG_TAG_ALL = 3;
    public static final int FLAG_TAG_BOLD = 2;
    public static final int FLAG_TAG_BR = 1;
    public static final int FLAG_TAG_COLOR = 4;
    public static Pattern WEB_URL = null;
    public static AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
    private static int adjustOwnerClassGuid = 0;
    private static RectF bitmapRect = null;
    private static final Object callLock = new Object();
    private static ContentObserver callLogContentObserver = null;
    public static DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    public static float density = 1.0f;
    public static DisplayMetrics displayMetrics = new DisplayMetrics();
    public static Point displaySize = new Point();
    private static boolean hasCallPermissions = (VERSION.SDK_INT >= 23);
    public static boolean incorrectDisplaySizeFix = false;
    public static boolean isInMultiwindow = false;
    private static Boolean isTablet = null;
    public static int leftBaseline = (isTablet() ? 80 : 72);
    private static Field mAttachInfoField = null;
    private static Field mStableInsetsField = null;
    public static OvershootInterpolator overshootInterpolator = new OvershootInterpolator();
    public static Integer photoSize = null;
    private static int prevOrientation = -10;
    public static int roundMessageSize = 0;
    private static Paint roundPaint = null;
    private static final Object smsLock = new Object();
    public static int statusBarHeight = 0;
    private static final Hashtable<String, Typeface> typefaceCache = new Hashtable();
    private static Runnable unregisterRunnable = null;
    public static boolean usingHardwareInput = false;
    private static boolean waitingForCall = false;
    private static boolean waitingForSms = false;

    /* renamed from: org.telegram.messenger.AndroidUtilities$5 */
    static class C17885 extends CrashManagerListener {
        public boolean includeDeviceData() {
            return true;
        }

        C17885() {
        }
    }

    public static int compare(int i, int i2) {
        return i == i2 ? 0 : i > i2 ? 1 : -1;
    }

    public static int getMyLayerVersion(int i) {
        return i & 65535;
    }

    public static int getPeerLayerVersion(int i) {
        return (i >> 16) & 65535;
    }

    public static long makeBroadcastId(int i) {
        return 4294967296L | (((long) i) & 4294967295L);
    }

    public static int setMyLayerVersion(int i, int i2) {
        return (i & -65536) | i2;
    }

    public static int setPeerLayerVersion(int i, int i2) {
        return (i & 65535) | (i2 << 16);
    }

    static {
        try {
            Pattern compile = Pattern.compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9]))");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("(([a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef]([a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef\\-]{0,61}[a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef]){0,1}\\.)+[a-zA-Z\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef]{2,63}|");
            stringBuilder.append(compile);
            stringBuilder.append(")");
            compile = Pattern.compile(stringBuilder.toString());
            stringBuilder = new StringBuilder();
            stringBuilder.append("((?:(http|https|Http|Https):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?(?:");
            stringBuilder.append(compile);
            stringBuilder.append(")(?:\\:\\d{1,5})?)(\\/(?:(?:[");
            stringBuilder.append("a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef");
            stringBuilder.append("\\;\\/\\?\\:\\@\\&\\=\\#\\~\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?(?:\\b|$)");
            WEB_URL = Pattern.compile(stringBuilder.toString());
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        checkDisplaySize(ApplicationLoader.applicationContext, null);
    }

    public static int[] calcDrawableColor(Drawable drawable) {
        Throwable e;
        int[] iArr = new int[2];
        int i = Theme.ACTION_BAR_VIDEO_EDIT_COLOR;
        try {
            if (drawable instanceof BitmapDrawable) {
                drawable = ((BitmapDrawable) drawable).getBitmap();
                if (drawable != null) {
                    Object createScaledBitmap = Bitmaps.createScaledBitmap(drawable, 1, 1, true);
                    if (createScaledBitmap != null) {
                        int pixel = createScaledBitmap.getPixel(0, 0);
                        if (drawable != createScaledBitmap) {
                            try {
                                createScaledBitmap.recycle();
                            } catch (Exception e2) {
                                e = e2;
                                i = pixel;
                                FileLog.m3e(e);
                                drawable = rgbToHsv((i >> 16) & 255, (i >> 8) & 255, i & 255);
                                drawable[1] = Math.min(1.0d, (drawable[1] + 0.05d) + (0.1d * (1.0d - drawable[1])));
                                drawable[2] = Math.max(0.0d, drawable[2] * 0.65d);
                                drawable = hsvToRgb(drawable[0], drawable[1], drawable[2]);
                                iArr[0] = Color.argb(102, drawable[0], drawable[1], drawable[2]);
                                iArr[1] = Color.argb(136, drawable[0], drawable[1], drawable[2]);
                                return iArr;
                            }
                        }
                        i = pixel;
                    }
                }
            } else if (drawable instanceof ColorDrawable) {
                i = ((ColorDrawable) drawable).getColor();
            }
        } catch (Exception e3) {
            e = e3;
            FileLog.m3e(e);
            drawable = rgbToHsv((i >> 16) & 255, (i >> 8) & 255, i & 255);
            drawable[1] = Math.min(1.0d, (drawable[1] + 0.05d) + (0.1d * (1.0d - drawable[1])));
            drawable[2] = Math.max(0.0d, drawable[2] * 0.65d);
            drawable = hsvToRgb(drawable[0], drawable[1], drawable[2]);
            iArr[0] = Color.argb(102, drawable[0], drawable[1], drawable[2]);
            iArr[1] = Color.argb(136, drawable[0], drawable[1], drawable[2]);
            return iArr;
        }
        drawable = rgbToHsv((i >> 16) & 255, (i >> 8) & 255, i & 255);
        drawable[1] = Math.min(1.0d, (drawable[1] + 0.05d) + (0.1d * (1.0d - drawable[1])));
        drawable[2] = Math.max(0.0d, drawable[2] * 0.65d);
        drawable = hsvToRgb(drawable[0], drawable[1], drawable[2]);
        iArr[0] = Color.argb(102, drawable[0], drawable[1], drawable[2]);
        iArr[1] = Color.argb(136, drawable[0], drawable[1], drawable[2]);
        return iArr;
    }

    private static double[] rgbToHsv(int i, int i2, int i3) {
        double d = ((double) i) / 255.0d;
        double d2 = ((double) i2) / 255.0d;
        double d3 = ((double) i3) / 255.0d;
        double d4 = (d <= d2 || d <= d3) ? d2 > d3 ? d2 : d3 : d;
        double d5 = (d >= d2 || d >= d3) ? d2 < d3 ? d2 : d3 : d;
        double d6 = d4 - d5;
        double d7 = 0.0d;
        double d8 = d4 == 0.0d ? 0.0d : d6 / d4;
        if (d4 != d5) {
            if (d <= d2 || d <= d3) {
                d = d2 > d3 ? 2.0d + ((d3 - d) / d6) : ((d - d2) / d6) + 4.0d;
            } else {
                d = ((d2 - d3) / d6) + ((double) (d2 < d3 ? 6 : 0));
            }
            d7 = d / 6.0d;
        }
        return new double[]{d7, d8, d4};
    }

    private static int[] hsvToRgb(double d, double d2, double d3) {
        double d4;
        d *= 6.0d;
        double floor = (double) ((int) Math.floor(d));
        d -= floor;
        double d5 = (1.0d - d2) * d3;
        double d6 = (1.0d - (d * d2)) * d3;
        d = d3 * (1.0d - ((1.0d - d) * d2));
        switch (((int) floor) % 6) {
            case 0.0d:
                d4 = d;
                d = d5;
                break;
            case Double.MIN_VALUE:
                d = d5;
                d5 = d3;
                d3 = d6;
                break;
            case 1.0E-323d:
                d4 = d3;
                d3 = d5;
                break;
            case 1.5E-323d:
                d = d3;
                d3 = d5;
                d5 = d6;
                break;
            case 2.0E-323d:
                d4 = d;
                d = d3;
                d3 = d4;
                break;
            case 2.5E-323d:
                d = d6;
                break;
            default:
                d = 0.0d;
                d3 = d;
                d5 = d3;
                break;
        }
        d5 = d4;
        return new int[]{(int) (d3 * 255.0d), (int) (d5 * 255.0d), (int) (d * 255.0d)};
    }

    public static void requestAdjustResize(Activity activity, int i) {
        if (activity != null) {
            if (!isTablet()) {
                activity.getWindow().setSoftInputMode(16);
                adjustOwnerClassGuid = i;
            }
        }
    }

    public static void removeAdjustResize(Activity activity, int i) {
        if (activity != null) {
            if (!isTablet()) {
                if (adjustOwnerClassGuid == i) {
                    activity.getWindow().setSoftInputMode(32);
                }
            }
        }
    }

    public static boolean isGoogleMapsInstalled(final org.telegram.ui.ActionBar.BaseFragment r4) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r0 = 0;
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ NameNotFoundException -> 0x000e }
        r1 = r1.getPackageManager();	 Catch:{ NameNotFoundException -> 0x000e }
        r2 = "com.google.android.apps.maps";	 Catch:{ NameNotFoundException -> 0x000e }
        r1.getApplicationInfo(r2, r0);	 Catch:{ NameNotFoundException -> 0x000e }
        r4 = 1;
        return r4;
    L_0x000e:
        r1 = r4.getParentActivity();
        if (r1 != 0) goto L_0x0015;
    L_0x0014:
        return r0;
    L_0x0015:
        r1 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r2 = r4.getParentActivity();
        r1.<init>(r2);
        r2 = "Install Google Maps?";
        r1.setMessage(r2);
        r2 = "OK";
        r3 = NUM; // 0x7f0c048c float:1.8611553E38 double:1.0530979736E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);
        r3 = new org.telegram.messenger.AndroidUtilities$1;
        r3.<init>(r4);
        r1.setPositiveButton(r2, r3);
        r2 = "Cancel";
        r3 = NUM; // 0x7f0c0107 float:1.8609725E38 double:1.0530975284E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);
        r3 = 0;
        r1.setNegativeButton(r2, r3);
        r1 = r1.create();
        r4.showDialog(r1);
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.isGoogleMapsInstalled(org.telegram.ui.ActionBar.BaseFragment):boolean");
    }

    public static boolean isInternalUri(android.net.Uri r3) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r3 = r3.getPath();
        r0 = 0;
        if (r3 != 0) goto L_0x0008;
    L_0x0007:
        return r0;
    L_0x0008:
        r1 = org.telegram.messenger.Utilities.readlink(r3);
        if (r1 == 0) goto L_0x0017;
    L_0x000e:
        r2 = r1.equals(r3);
        if (r2 == 0) goto L_0x0015;
    L_0x0014:
        goto L_0x0017;
    L_0x0015:
        r3 = r1;
        goto L_0x0008;
    L_0x0017:
        if (r3 == 0) goto L_0x002d;
    L_0x0019:
        r1 = new java.io.File;	 Catch:{ Exception -> 0x0026 }
        r1.<init>(r3);	 Catch:{ Exception -> 0x0026 }
        r1 = r1.getCanonicalPath();	 Catch:{ Exception -> 0x0026 }
        if (r1 == 0) goto L_0x002d;
    L_0x0024:
        r3 = r1;
        goto L_0x002d;
    L_0x0026:
        r1 = "/./";
        r2 = "/";
        r3.replace(r1, r2);
    L_0x002d:
        if (r3 == 0) goto L_0x0056;
    L_0x002f:
        r3 = r3.toLowerCase();
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "/data/data/";
        r1.append(r2);
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r2 = r2.getPackageName();
        r1.append(r2);
        r2 = "/files";
        r1.append(r2);
        r1 = r1.toString();
        r3 = r3.contains(r1);
        if (r3 == 0) goto L_0x0056;
    L_0x0055:
        r0 = 1;
    L_0x0056:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.isInternalUri(android.net.Uri):boolean");
    }

    public static void lockOrientation(Activity activity) {
        if (activity != null) {
            if (prevOrientation == -10) {
                try {
                    prevOrientation = activity.getRequestedOrientation();
                    WindowManager windowManager = (WindowManager) activity.getSystemService("window");
                    if (!(windowManager == null || windowManager.getDefaultDisplay() == null)) {
                        int rotation = windowManager.getDefaultDisplay().getRotation();
                        int i = activity.getResources().getConfiguration().orientation;
                        if (rotation == 3) {
                            if (i == 1) {
                                activity.setRequestedOrientation(1);
                            } else {
                                activity.setRequestedOrientation(8);
                            }
                        } else if (rotation == 1) {
                            if (i == 1) {
                                activity.setRequestedOrientation(9);
                            } else {
                                activity.setRequestedOrientation(0);
                            }
                        } else if (rotation == 0) {
                            if (i == 2) {
                                activity.setRequestedOrientation(0);
                            } else {
                                activity.setRequestedOrientation(1);
                            }
                        } else if (i == 2) {
                            activity.setRequestedOrientation(8);
                        } else {
                            activity.setRequestedOrientation(9);
                        }
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
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
                FileLog.m3e(e);
            }
        }
    }

    public static Typeface getTypeface(String str) {
        Typeface typeface;
        synchronized (typefaceCache) {
            if (!typefaceCache.containsKey(str)) {
                try {
                    typefaceCache.put(str, Typeface.createFromAsset(ApplicationLoader.applicationContext.getAssets(), str));
                } catch (Exception e) {
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Could not get typeface '");
                        stringBuilder.append(str);
                        stringBuilder.append("' because ");
                        stringBuilder.append(e.getMessage());
                        FileLog.m1e(stringBuilder.toString());
                    }
                    return null;
                }
            }
            typeface = (Typeface) typefaceCache.get(str);
        }
        return typeface;
    }

    public static boolean isWaitingForSms() {
        boolean z;
        synchronized (smsLock) {
            z = waitingForSms;
        }
        return z;
    }

    public static void setWaitingForSms(boolean z) {
        synchronized (smsLock) {
            waitingForSms = z;
        }
    }

    public static boolean isWaitingForCall() {
        boolean z;
        synchronized (callLock) {
            z = waitingForCall;
        }
        return z;
    }

    public static void setWaitingForCall(boolean z) {
        synchronized (callLock) {
            waitingForCall = z;
        }
    }

    public static void showKeyboard(View view) {
        if (view != null) {
            try {
                ((InputMethodManager) view.getContext().getSystemService("input_method")).showSoftInput(view, 1);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    public static boolean isKeyboardShowed(View view) {
        if (view == null) {
            return false;
        }
        try {
            return ((InputMethodManager) view.getContext().getSystemService("input_method")).isActive(view);
        } catch (Throwable e) {
            FileLog.m3e(e);
            return false;
        }
    }

    public static void hideKeyboard(View view) {
        if (view != null) {
            try {
                InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService("input_method");
                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    public static File getCacheDir() {
        String externalStorageState;
        File externalCacheDir;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (Throwable e) {
            FileLog.m3e(e);
            externalStorageState = null;
        }
        if (externalStorageState == null || externalStorageState.startsWith("mounted")) {
            try {
                externalCacheDir = ApplicationLoader.applicationContext.getExternalCacheDir();
                if (externalCacheDir != null) {
                    return externalCacheDir;
                }
            } catch (Throwable e2) {
                FileLog.m3e(e2);
            }
        }
        try {
            externalCacheDir = ApplicationLoader.applicationContext.getCacheDir();
            if (externalCacheDir != null) {
                return externalCacheDir;
            }
        } catch (Throwable e22) {
            FileLog.m3e(e22);
        }
        return new File(TtmlNode.ANONYMOUS_REGION_ID);
    }

    public static int dp(float f) {
        return f == 0.0f ? 0 : (int) Math.ceil((double) (density * f));
    }

    public static int dp2(float f) {
        return f == 0.0f ? 0 : (int) Math.floor((double) (density * f));
    }

    public static float dpf2(float f) {
        return f == 0.0f ? 0.0f : density * f;
    }

    public static void checkDisplaySize(Context context, Configuration configuration) {
        try {
            density = context.getResources().getDisplayMetrics().density;
            if (configuration == null) {
                configuration = context.getResources().getConfiguration();
            }
            boolean z = true;
            if (configuration.keyboard == 1 || configuration.hardKeyboardHidden != 1) {
                z = false;
            }
            usingHardwareInput = z;
            WindowManager windowManager = (WindowManager) context.getSystemService("window");
            if (windowManager != null) {
                context = windowManager.getDefaultDisplay();
                if (context != null) {
                    context.getMetrics(displayMetrics);
                    context.getSize(displaySize);
                }
            }
            if (configuration.screenWidthDp != null) {
                context = (int) Math.ceil((double) (((float) configuration.screenWidthDp) * density));
                if (Math.abs(displaySize.x - context) > 3) {
                    displaySize.x = context;
                }
            }
            if (configuration.screenHeightDp != null) {
                context = (int) Math.ceil((double) (((float) configuration.screenHeightDp) * density));
                if (Math.abs(displaySize.y - context) > 3) {
                    displaySize.y = context;
                }
            }
            if (roundMessageSize == null) {
                if (isTablet() != null) {
                    roundMessageSize = (int) (((float) getMinTabletSide()) * NUM);
                } else {
                    roundMessageSize = (int) (((float) Math.min(displaySize.x, displaySize.y)) * NUM);
                }
            }
            if (BuildVars.LOGS_ENABLED != null) {
                context = new StringBuilder();
                context.append("display size = ");
                context.append(displaySize.x);
                context.append(" ");
                context.append(displaySize.y);
                context.append(" ");
                context.append(displayMetrics.xdpi);
                context.append("x");
                context.append(displayMetrics.ydpi);
                FileLog.m1e(context.toString());
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public static float getPixelsInCM(float f, boolean z) {
        return (f / 2.54f) * (z ? displayMetrics.xdpi : displayMetrics.ydpi);
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long j) {
        if (j == 0) {
            ApplicationLoader.applicationHandler.post(runnable);
        } else {
            ApplicationLoader.applicationHandler.postDelayed(runnable, j);
        }
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        ApplicationLoader.applicationHandler.removeCallbacks(runnable);
    }

    public static boolean isTablet() {
        if (isTablet == null) {
            isTablet = Boolean.valueOf(ApplicationLoader.applicationContext.getResources().getBoolean(C0446R.bool.isTablet));
        }
        return isTablet.booleanValue();
    }

    public static boolean isSmallTablet() {
        return ((float) Math.min(displaySize.x, displaySize.y)) / density <= 700.0f;
    }

    public static int getMinTabletSide() {
        if (isSmallTablet()) {
            int min = Math.min(displaySize.x, displaySize.y);
            int max = Math.max(displaySize.x, displaySize.y);
            int i = (max * 35) / 100;
            if (i < dp(320.0f)) {
                i = dp(320.0f);
            }
            return Math.min(min, max - i);
        }
        min = Math.min(displaySize.x, displaySize.y);
        max = (min * 35) / 100;
        if (max < dp(320.0f)) {
            max = dp(320.0f);
        }
        return min - max;
    }

    public static int getPhotoSize() {
        if (photoSize == null) {
            photoSize = Integer.valueOf(1280);
        }
        return photoSize.intValue();
    }

    public static void endIncomingCall() {
        if (hasCallPermissions) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                Method declaredMethod = Class.forName(telephonyManager.getClass().getName()).getDeclaredMethod("getITelephony", new Class[0]);
                declaredMethod.setAccessible(true);
                ITelephony iTelephony = (ITelephony) declaredMethod.invoke(telephonyManager, new Object[0]);
                ITelephony iTelephony2 = (ITelephony) declaredMethod.invoke(telephonyManager, new Object[0]);
                iTelephony2.silenceRinger();
                iTelephony2.endCall();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    public static boolean checkPhonePattern(String str, String str2) {
        if (!TextUtils.isEmpty(str)) {
            if (!str.equals("*")) {
                str = str.split("\\*");
                str2 = PhoneFormat.stripExceptNumbers(str2);
                int i = 0;
                int i2 = i;
                while (i < str.length) {
                    String str3 = str[i];
                    if (!TextUtils.isEmpty(str3)) {
                        i2 = str2.indexOf(str3, i2);
                        if (i2 == -1) {
                            return false;
                        }
                        i2 += str3.length();
                    }
                    i++;
                }
                return true;
            }
        }
        return true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static String obtainLoginPhoneCall(String str) {
        Throwable e;
        if (!hasCallPermissions) {
            return null;
        }
        Cursor query;
        try {
            query = ApplicationLoader.applicationContext.getContentResolver().query(Calls.CONTENT_URI, new String[]{"number", "date"}, "type IN (3,1,5)", null, "date DESC LIMIT 5");
            while (query.moveToNext()) {
                try {
                    String string = query.getString(0);
                    long j = query.getLong(1);
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("number = ");
                        stringBuilder.append(string);
                        FileLog.m1e(stringBuilder.toString());
                    }
                    if (Math.abs(System.currentTimeMillis() - j) < 3600000) {
                        if (checkPhonePattern(str, string)) {
                            if (query != null) {
                                query.close();
                            }
                            return string;
                        }
                    }
                } catch (Exception e2) {
                    e = e2;
                }
            }
            if (query != null) {
                query.close();
            }
        } catch (Exception e3) {
            e = e3;
            query = null;
            try {
                FileLog.m3e(e);
            } catch (Throwable th) {
                str = th;
                if (query != null) {
                    query.close();
                }
                throw str;
            }
        } catch (Throwable th2) {
            str = th2;
            query = null;
            if (query != null) {
                query.close();
            }
            throw str;
        }
        return null;
    }

    private static void registerLoginContentObserver(boolean r4, final java.lang.String r5) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        if (r4 == 0) goto L_0x002c;
    L_0x0002:
        r4 = callLogContentObserver;
        if (r4 == 0) goto L_0x0007;
    L_0x0006:
        return;
    L_0x0007:
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r4 = r4.getContentResolver();
        r0 = android.provider.CallLog.Calls.CONTENT_URI;
        r1 = 1;
        r2 = new org.telegram.messenger.AndroidUtilities$2;
        r3 = new android.os.Handler;
        r3.<init>();
        r2.<init>(r3, r5);
        callLogContentObserver = r2;
        r4.registerContentObserver(r0, r1, r2);
        r4 = new org.telegram.messenger.AndroidUtilities$3;
        r4.<init>(r5);
        unregisterRunnable = r4;
        r0 = 10000; // 0x2710 float:1.4013E-41 double:4.9407E-320;
        runOnUIThread(r4, r0);
        goto L_0x004f;
    L_0x002c:
        r4 = callLogContentObserver;
        if (r4 != 0) goto L_0x0031;
    L_0x0030:
        return;
    L_0x0031:
        r4 = unregisterRunnable;
        r5 = 0;
        if (r4 == 0) goto L_0x003d;
    L_0x0036:
        r4 = unregisterRunnable;
        cancelRunOnUIThread(r4);
        unregisterRunnable = r5;
    L_0x003d:
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x004d, all -> 0x0049 }
        r4 = r4.getContentResolver();	 Catch:{ Exception -> 0x004d, all -> 0x0049 }
        r0 = callLogContentObserver;	 Catch:{ Exception -> 0x004d, all -> 0x0049 }
        r4.unregisterContentObserver(r0);	 Catch:{ Exception -> 0x004d, all -> 0x0049 }
        goto L_0x004d;
    L_0x0049:
        r4 = move-exception;
        callLogContentObserver = r5;
        throw r4;
    L_0x004d:
        callLogContentObserver = r5;
    L_0x004f:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.registerLoginContentObserver(boolean, java.lang.String):void");
    }

    public static void removeLoginPhoneCall(String str, boolean z) {
        Throwable e;
        if (hasCallPermissions) {
            Cursor cursor = null;
            Cursor query;
            try {
                ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
                Uri uri = Calls.CONTENT_URI;
                r4 = new String[2];
                int i = 0;
                r4[0] = "_id";
                r4[1] = "number";
                query = contentResolver.query(uri, r4, "type IN (3,1,5)", null, "date DESC LIMIT 5");
                CharSequence string;
                do {
                    try {
                        if (!query.moveToNext()) {
                            break;
                        }
                        string = query.getString(1);
                        if (string.contains(str)) {
                            break;
                        }
                    } catch (Exception e2) {
                        e = e2;
                        cursor = query;
                    } catch (Throwable th) {
                        str = th;
                    }
                } while (!str.contains(string));
                ApplicationLoader.applicationContext.getContentResolver().delete(Calls.CONTENT_URI, "_id = ? ", new String[]{String.valueOf(query.getInt(0))});
                i = 1;
                if (i == 0 && z) {
                    registerLoginContentObserver(true, str);
                }
                if (query != null) {
                    query.close();
                }
            } catch (Exception e3) {
                e = e3;
                try {
                    FileLog.m3e(e);
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (Throwable th2) {
                    str = th2;
                    query = cursor;
                    if (query != null) {
                        query.close();
                    }
                    throw str;
                }
            }
        }
    }

    public static int getViewInset(View view) {
        if (!(view == null || VERSION.SDK_INT < 21 || view.getHeight() == displaySize.y)) {
            if (view.getHeight() != displaySize.y - statusBarHeight) {
                try {
                    if (mAttachInfoField == null) {
                        mAttachInfoField = View.class.getDeclaredField("mAttachInfo");
                        mAttachInfoField.setAccessible(true);
                    }
                    view = mAttachInfoField.get(view);
                    if (view != null) {
                        if (mStableInsetsField == null) {
                            mStableInsetsField = view.getClass().getDeclaredField("mStableInsets");
                            mStableInsetsField.setAccessible(true);
                        }
                        return ((Rect) mStableInsetsField.get(view)).bottom;
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                return 0;
            }
        }
        return 0;
    }

    public static Point getRealScreenSize() {
        Point point = new Point();
        try {
            WindowManager windowManager = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
            if (VERSION.SDK_INT >= 17) {
                windowManager.getDefaultDisplay().getRealSize(point);
            } else {
                try {
                    point.set(((Integer) Display.class.getMethod("getRawWidth", new Class[0]).invoke(windowManager.getDefaultDisplay(), new Object[0])).intValue(), ((Integer) Display.class.getMethod("getRawHeight", new Class[0]).invoke(windowManager.getDefaultDisplay(), new Object[0])).intValue());
                } catch (Throwable e) {
                    point.set(windowManager.getDefaultDisplay().getWidth(), windowManager.getDefaultDisplay().getHeight());
                    FileLog.m3e(e);
                }
            }
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
        return point;
    }

    public static void setEnabled(View view, boolean z) {
        if (view != null) {
            view.setEnabled(z);
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    setEnabled(viewGroup.getChildAt(i), z);
                }
            }
        }
    }

    public static CharSequence getTrimmedString(CharSequence charSequence) {
        if (charSequence != null) {
            if (charSequence.length() != 0) {
                while (charSequence.length() > 0 && (charSequence.charAt(0) == '\n' || charSequence.charAt(0) == ' ')) {
                    charSequence = charSequence.subSequence(1, charSequence.length());
                }
                while (charSequence.length() > 0 && (charSequence.charAt(charSequence.length() - 1) == '\n' || charSequence.charAt(charSequence.length() - 1) == ' ')) {
                    charSequence = charSequence.subSequence(0, charSequence.length() - 1);
                }
                return charSequence;
            }
        }
        return charSequence;
    }

    public static void setViewPagerEdgeEffectColor(ViewPager viewPager, int i) {
        if (VERSION.SDK_INT >= 21) {
            try {
                Field declaredField = ViewPager.class.getDeclaredField("mLeftEdge");
                declaredField.setAccessible(true);
                EdgeEffectCompat edgeEffectCompat = (EdgeEffectCompat) declaredField.get(viewPager);
                if (edgeEffectCompat != null) {
                    Field declaredField2 = EdgeEffectCompat.class.getDeclaredField("mEdgeEffect");
                    declaredField2.setAccessible(true);
                    EdgeEffect edgeEffect = (EdgeEffect) declaredField2.get(edgeEffectCompat);
                    if (edgeEffect != null) {
                        edgeEffect.setColor(i);
                    }
                }
                declaredField = ViewPager.class.getDeclaredField("mRightEdge");
                declaredField.setAccessible(true);
                EdgeEffectCompat edgeEffectCompat2 = (EdgeEffectCompat) declaredField.get(viewPager);
                if (edgeEffectCompat2 != null) {
                    declaredField = EdgeEffectCompat.class.getDeclaredField("mEdgeEffect");
                    declaredField.setAccessible(true);
                    EdgeEffect edgeEffect2 = (EdgeEffect) declaredField.get(edgeEffectCompat2);
                    if (edgeEffect2 != null) {
                        edgeEffect2.setColor(i);
                    }
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    public static void setScrollViewEdgeEffectColor(ScrollView scrollView, int i) {
        if (VERSION.SDK_INT >= 21) {
            try {
                Field declaredField = ScrollView.class.getDeclaredField("mEdgeGlowTop");
                declaredField.setAccessible(true);
                EdgeEffect edgeEffect = (EdgeEffect) declaredField.get(scrollView);
                if (edgeEffect != null) {
                    edgeEffect.setColor(i);
                }
                declaredField = ScrollView.class.getDeclaredField("mEdgeGlowBottom");
                declaredField.setAccessible(true);
                EdgeEffect edgeEffect2 = (EdgeEffect) declaredField.get(scrollView);
                if (edgeEffect2 != null) {
                    edgeEffect2.setColor(i);
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    @SuppressLint({"NewApi"})
    public static void clearDrawableAnimation(View view) {
        if (VERSION.SDK_INT >= 21) {
            if (view != null) {
                if (view instanceof ListView) {
                    view = ((ListView) view).getSelector();
                    if (view != null) {
                        view.setState(StateSet.NOTHING);
                    }
                } else {
                    view = view.getBackground();
                    if (view != null) {
                        view.setState(StateSet.NOTHING);
                        view.jumpToCurrentState();
                    }
                }
            }
        }
    }

    public static SpannableStringBuilder replaceTags(String str) {
        return replaceTags(str, 3);
    }

    public static SpannableStringBuilder replaceTags(String str, int i) {
        try {
            int indexOf;
            CharSequence stringBuilder = new StringBuilder(str);
            if ((i & 1) != 0) {
                int indexOf2;
                while (true) {
                    indexOf2 = stringBuilder.indexOf("<br>");
                    if (indexOf2 == -1) {
                        break;
                    }
                    stringBuilder.replace(indexOf2, indexOf2 + 4, "\n");
                }
                while (true) {
                    indexOf2 = stringBuilder.indexOf("<br/>");
                    if (indexOf2 == -1) {
                        break;
                    }
                    stringBuilder.replace(indexOf2, indexOf2 + 5, "\n");
                }
            }
            ArrayList arrayList = new ArrayList();
            if ((i & 2) != 0) {
                while (true) {
                    i = stringBuilder.indexOf("<b>");
                    if (i == -1) {
                        break;
                    }
                    stringBuilder.replace(i, i + 3, TtmlNode.ANONYMOUS_REGION_ID);
                    indexOf = stringBuilder.indexOf("</b>");
                    if (indexOf == -1) {
                        indexOf = stringBuilder.indexOf("<b>");
                    }
                    stringBuilder.replace(indexOf, indexOf + 4, TtmlNode.ANONYMOUS_REGION_ID);
                    arrayList.add(Integer.valueOf(i));
                    arrayList.add(Integer.valueOf(indexOf));
                }
                while (true) {
                    i = stringBuilder.indexOf("**");
                    if (i == -1) {
                        break;
                    }
                    stringBuilder.replace(i, i + 2, TtmlNode.ANONYMOUS_REGION_ID);
                    indexOf = stringBuilder.indexOf("**");
                    if (indexOf >= 0) {
                        stringBuilder.replace(indexOf, indexOf + 2, TtmlNode.ANONYMOUS_REGION_ID);
                        arrayList.add(Integer.valueOf(i));
                        arrayList.add(Integer.valueOf(indexOf));
                    }
                }
            }
            i = new SpannableStringBuilder(stringBuilder);
            for (int i2 = 0; i2 < arrayList.size() / 2; i2++) {
                indexOf = i2 * 2;
                i.setSpan(new TypefaceSpan(getTypeface("fonts/rmedium.ttf")), ((Integer) arrayList.get(indexOf)).intValue(), ((Integer) arrayList.get(indexOf + 1)).intValue(), 33);
            }
            return i;
        } catch (Throwable e) {
            FileLog.m3e(e);
            return new SpannableStringBuilder(str);
        }
    }

    public static boolean needShowPasscode(boolean z) {
        boolean isWasInBackground = ForegroundDetector.getInstance().isWasInBackground(z);
        if (z) {
            ForegroundDetector.getInstance().resetBackgroundVar();
        }
        return SharedConfig.passcodeHash.length() <= false && isWasInBackground && (SharedConfig.appLocked || ((SharedConfig.autoLockIn && SharedConfig.lastPauseTime && !SharedConfig.appLocked && SharedConfig.lastPauseTime + SharedConfig.autoLockIn <= ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime()) || ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime() + 5 < SharedConfig.lastPauseTime));
    }

    public static void shakeView(final View view, final float f, final int i) {
        if (i == 6) {
            view.setTranslationX(0.0f);
            return;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[1];
        animatorArr[0] = ObjectAnimator.ofFloat(view, "translationX", new float[]{(float) dp(f)});
        animatorSet.playTogether(animatorArr);
        animatorSet.setDuration(50);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                AndroidUtilities.shakeView(view, i == 5 ? 0.0f : -f, i + 1);
            }
        });
        animatorSet.start();
    }

    public static void checkForCrashes(Activity activity) {
        CrashManager.register(activity, BuildVars.DEBUG_VERSION ? BuildVars.HOCKEY_APP_HASH_DEBUG : BuildVars.HOCKEY_APP_HASH, new C17885());
    }

    public static void checkForUpdates(Activity activity) {
        if (BuildVars.DEBUG_VERSION) {
            UpdateManager.register(activity, BuildVars.DEBUG_VERSION ? BuildVars.HOCKEY_APP_HASH_DEBUG : BuildVars.HOCKEY_APP_HASH);
        }
    }

    public static void unregisterUpdates() {
        if (BuildVars.DEBUG_VERSION) {
            UpdateManager.unregister();
        }
    }

    public static void addToClipboard(CharSequence charSequence) {
        try {
            ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", charSequence));
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public static void addMediaToGallery(String str) {
        if (str != null) {
            addMediaToGallery(Uri.fromFile(new File(str)));
        }
    }

    public static void addMediaToGallery(Uri uri) {
        if (uri != null) {
            try {
                Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                intent.setData(uri);
                ApplicationLoader.applicationContext.sendBroadcast(intent);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    private static File getAlbumDir() {
        if (VERSION.SDK_INT >= 23 && ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
            return FileLoader.getDirectory(4);
        }
        File file;
        if ("mounted".equals(Environment.getExternalStorageState())) {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Telegram");
            if (!(file.mkdirs() || file.exists())) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("failed to create directory");
                }
                return null;
            }
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m0d("External storage is not mounted READ/WRITE.");
        }
        file = null;
        return file;
    }

    @SuppressLint({"NewApi"})
    public static String getPath(Uri uri) {
        try {
            if ((VERSION.SDK_INT >= 19 ? 1 : 0) != 0 && DocumentsContract.isDocumentUri(ApplicationLoader.applicationContext, uri)) {
                if (isExternalStorageDocument(uri)) {
                    uri = DocumentsContract.getDocumentId(uri).split(":");
                    if ("primary".equalsIgnoreCase(uri[0])) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(Environment.getExternalStorageDirectory());
                        stringBuilder.append("/");
                        stringBuilder.append(uri[1]);
                        return stringBuilder.toString();
                    }
                } else if (isDownloadsDocument(uri)) {
                    return getDataColumn(ApplicationLoader.applicationContext, ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(DocumentsContract.getDocumentId(uri)).longValue()), null, null);
                } else if (isMediaDocument(uri)) {
                    int i;
                    Uri uri2;
                    String str = DocumentsContract.getDocumentId(uri).split(":")[0];
                    int hashCode = str.hashCode();
                    if (hashCode != 93166550) {
                        if (hashCode != 100313435) {
                            if (hashCode == 112202875) {
                                if (str.equals(MimeTypes.BASE_TYPE_VIDEO)) {
                                    i = 1;
                                    switch (i) {
                                        case 0:
                                            uri2 = Media.EXTERNAL_CONTENT_URI;
                                            break;
                                        case 1:
                                            uri2 = Video.Media.EXTERNAL_CONTENT_URI;
                                            break;
                                        case 2:
                                            uri2 = Audio.Media.EXTERNAL_CONTENT_URI;
                                            break;
                                        default:
                                            uri2 = null;
                                            break;
                                    }
                                    return getDataColumn(ApplicationLoader.applicationContext, uri2, "_id=?", new String[]{uri[1]});
                                }
                            }
                        } else if (str.equals("image")) {
                            i = 0;
                            switch (i) {
                                case 0:
                                    uri2 = Media.EXTERNAL_CONTENT_URI;
                                    break;
                                case 1:
                                    uri2 = Video.Media.EXTERNAL_CONTENT_URI;
                                    break;
                                case 2:
                                    uri2 = Audio.Media.EXTERNAL_CONTENT_URI;
                                    break;
                                default:
                                    uri2 = null;
                                    break;
                            }
                            return getDataColumn(ApplicationLoader.applicationContext, uri2, "_id=?", new String[]{uri[1]});
                        }
                    } else if (str.equals(MimeTypes.BASE_TYPE_AUDIO)) {
                        i = 2;
                        switch (i) {
                            case 0:
                                uri2 = Media.EXTERNAL_CONTENT_URI;
                                break;
                            case 1:
                                uri2 = Video.Media.EXTERNAL_CONTENT_URI;
                                break;
                            case 2:
                                uri2 = Audio.Media.EXTERNAL_CONTENT_URI;
                                break;
                            default:
                                uri2 = null;
                                break;
                        }
                        return getDataColumn(ApplicationLoader.applicationContext, uri2, "_id=?", new String[]{uri[1]});
                    }
                    i = -1;
                    switch (i) {
                        case 0:
                            uri2 = Media.EXTERNAL_CONTENT_URI;
                            break;
                        case 1:
                            uri2 = Video.Media.EXTERNAL_CONTENT_URI;
                            break;
                        case 2:
                            uri2 = Audio.Media.EXTERNAL_CONTENT_URI;
                            break;
                        default:
                            uri2 = null;
                            break;
                    }
                    return getDataColumn(ApplicationLoader.applicationContext, uri2, "_id=?", new String[]{uri[1]});
                }
                return null;
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(ApplicationLoader.applicationContext, uri, null, null);
            } else {
                if ("file".equalsIgnoreCase(uri.getScheme())) {
                    return uri.getPath();
                }
                return null;
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public static java.lang.String getDataColumn(android.content.Context r7, android.net.Uri r8, java.lang.String r9, java.lang.String[] r10) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r0 = 1;
        r3 = new java.lang.String[r0];
        r0 = "_data";
        r1 = 0;
        r3[r1] = r0;
        r0 = 0;
        r1 = r7.getContentResolver();	 Catch:{ Exception -> 0x0059, all -> 0x0051 }
        r6 = 0;	 Catch:{ Exception -> 0x0059, all -> 0x0051 }
        r2 = r8;	 Catch:{ Exception -> 0x0059, all -> 0x0051 }
        r4 = r9;	 Catch:{ Exception -> 0x0059, all -> 0x0051 }
        r5 = r10;	 Catch:{ Exception -> 0x0059, all -> 0x0051 }
        r7 = r1.query(r2, r3, r4, r5, r6);	 Catch:{ Exception -> 0x0059, all -> 0x0051 }
        if (r7 == 0) goto L_0x004e;
    L_0x0017:
        r8 = r7.moveToFirst();	 Catch:{ Exception -> 0x005a, all -> 0x004c }
        if (r8 == 0) goto L_0x004e;	 Catch:{ Exception -> 0x005a, all -> 0x004c }
    L_0x001d:
        r8 = "_data";	 Catch:{ Exception -> 0x005a, all -> 0x004c }
        r8 = r7.getColumnIndexOrThrow(r8);	 Catch:{ Exception -> 0x005a, all -> 0x004c }
        r8 = r7.getString(r8);	 Catch:{ Exception -> 0x005a, all -> 0x004c }
        r9 = "content://";	 Catch:{ Exception -> 0x005a, all -> 0x004c }
        r9 = r8.startsWith(r9);	 Catch:{ Exception -> 0x005a, all -> 0x004c }
        if (r9 != 0) goto L_0x0046;	 Catch:{ Exception -> 0x005a, all -> 0x004c }
    L_0x002f:
        r9 = "/";	 Catch:{ Exception -> 0x005a, all -> 0x004c }
        r9 = r8.startsWith(r9);	 Catch:{ Exception -> 0x005a, all -> 0x004c }
        if (r9 != 0) goto L_0x0040;	 Catch:{ Exception -> 0x005a, all -> 0x004c }
    L_0x0037:
        r9 = "file://";	 Catch:{ Exception -> 0x005a, all -> 0x004c }
        r9 = r8.startsWith(r9);	 Catch:{ Exception -> 0x005a, all -> 0x004c }
        if (r9 != 0) goto L_0x0040;
    L_0x003f:
        goto L_0x0046;
    L_0x0040:
        if (r7 == 0) goto L_0x0045;
    L_0x0042:
        r7.close();
    L_0x0045:
        return r8;
    L_0x0046:
        if (r7 == 0) goto L_0x004b;
    L_0x0048:
        r7.close();
    L_0x004b:
        return r0;
    L_0x004c:
        r8 = move-exception;
        goto L_0x0053;
    L_0x004e:
        if (r7 == 0) goto L_0x005f;
    L_0x0050:
        goto L_0x005c;
    L_0x0051:
        r8 = move-exception;
        r7 = r0;
    L_0x0053:
        if (r7 == 0) goto L_0x0058;
    L_0x0055:
        r7.close();
    L_0x0058:
        throw r8;
    L_0x0059:
        r7 = r0;
    L_0x005a:
        if (r7 == 0) goto L_0x005f;
    L_0x005c:
        r7.close();
    L_0x005f:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.getDataColumn(android.content.Context, android.net.Uri, java.lang.String, java.lang.String[]):java.lang.String");
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
            File albumDir = getAlbumDir();
            Date date = new Date();
            date.setTime((System.currentTimeMillis() + ((long) Utilities.random.nextInt(1000))) + 1);
            String format = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(date);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("IMG_");
            stringBuilder.append(format);
            stringBuilder.append(".jpg");
            return new File(albumDir, stringBuilder.toString());
        } catch (Throwable e) {
            FileLog.m3e(e);
            return null;
        }
    }

    public static CharSequence generateSearchName(String str, String str2, String str3) {
        if (str == null && str2 == null) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        int i;
        int indexOf;
        int i2;
        int i3;
        CharSequence spannableStringBuilder = new SpannableStringBuilder();
        if (str != null) {
            if (str.length() != 0) {
                if (!(str2 == null || str2.length() == 0)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(str);
                    stringBuilder.append(" ");
                    stringBuilder.append(str2);
                    str = stringBuilder.toString();
                }
                str = str.trim();
                str2 = new StringBuilder();
                str2.append(" ");
                str2.append(str.toLowerCase());
                str2 = str2.toString();
                i = 0;
                while (true) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(" ");
                    stringBuilder2.append(str3);
                    indexOf = str2.indexOf(stringBuilder2.toString(), i);
                    if (indexOf != -1) {
                        break;
                    }
                    String substring;
                    i2 = 1;
                    i3 = indexOf - (indexOf != 0 ? 0 : 1);
                    int length = str3.length();
                    if (indexOf == 0) {
                        i2 = 0;
                    }
                    indexOf = (length + i2) + i3;
                    if (i == 0 && i != i3 + 1) {
                        spannableStringBuilder.append(str.substring(i, i3));
                    } else if (i == 0 && i3 != 0) {
                        spannableStringBuilder.append(str.substring(0, i3));
                    }
                    substring = str.substring(i3, Math.min(str.length(), indexOf));
                    if (substring.startsWith(" ")) {
                        spannableStringBuilder.append(" ");
                    }
                    Object trim = substring.trim();
                    i2 = spannableStringBuilder.length();
                    spannableStringBuilder.append(trim);
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), i2, trim.length() + i2, 33);
                    i = indexOf;
                }
                if (i != -1 && i < str.length()) {
                    spannableStringBuilder.append(str.substring(i, str.length()));
                }
                return spannableStringBuilder;
            }
        }
        str = str2;
        str = str.trim();
        str2 = new StringBuilder();
        str2.append(" ");
        str2.append(str.toLowerCase());
        str2 = str2.toString();
        i = 0;
        while (true) {
            StringBuilder stringBuilder22 = new StringBuilder();
            stringBuilder22.append(" ");
            stringBuilder22.append(str3);
            indexOf = str2.indexOf(stringBuilder22.toString(), i);
            if (indexOf != -1) {
                break;
            }
            i2 = 1;
            if (indexOf != 0) {
            }
            i3 = indexOf - (indexOf != 0 ? 0 : 1);
            int length2 = str3.length();
            if (indexOf == 0) {
                i2 = 0;
            }
            indexOf = (length2 + i2) + i3;
            if (i == 0) {
            }
            spannableStringBuilder.append(str.substring(0, i3));
            substring = str.substring(i3, Math.min(str.length(), indexOf));
            if (substring.startsWith(" ")) {
                spannableStringBuilder.append(" ");
            }
            Object trim2 = substring.trim();
            i2 = spannableStringBuilder.length();
            spannableStringBuilder.append(trim2);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), i2, trim2.length() + i2, 33);
            i = indexOf;
        }
        spannableStringBuilder.append(str.substring(i, str.length()));
        return spannableStringBuilder;
    }

    public static File generateVideoPath() {
        try {
            File albumDir = getAlbumDir();
            Date date = new Date();
            date.setTime((System.currentTimeMillis() + ((long) Utilities.random.nextInt(1000))) + 1);
            String format = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(date);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("VID_");
            stringBuilder.append(format);
            stringBuilder.append(".mp4");
            return new File(albumDir, stringBuilder.toString());
        } catch (Throwable e) {
            FileLog.m3e(e);
            return null;
        }
    }

    public static String formatFileSize(long j) {
        if (j < 1024) {
            return String.format("%d B", new Object[]{Long.valueOf(j)});
        } else if (j < 1048576) {
            return String.format("%.1f KB", new Object[]{Float.valueOf(((float) j) / NUM)});
        } else if (j < NUM) {
            return String.format("%.1f MB", new Object[]{Float.valueOf((((float) j) / NUM) / NUM)});
        } else {
            return String.format("%.1f GB", new Object[]{Float.valueOf(((((float) j) / NUM) / NUM) / NUM)});
        }
    }

    public static byte[] decodeQuotedPrintable(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i = 0;
        while (i < bArr.length) {
            byte b = bArr[i];
            if (b == (byte) 61) {
                i++;
                try {
                    int digit = Character.digit((char) bArr[i], 16);
                    i++;
                    byteArrayOutputStream.write((char) ((digit << 4) + Character.digit((char) bArr[i], 16)));
                } catch (Throwable e) {
                    FileLog.m3e(e);
                    return null;
                }
            }
            byteArrayOutputStream.write(b);
            i++;
        }
        bArr = byteArrayOutputStream.toByteArray();
        try {
            byteArrayOutputStream.close();
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
        return bArr;
    }

    public static boolean copyFile(InputStream inputStream, File file) throws IOException {
        OutputStream fileOutputStream = new FileOutputStream(file);
        file = new byte[4096];
        while (true) {
            int read = inputStream.read(file);
            if (read > 0) {
                Thread.yield();
                fileOutputStream.write(file, 0, read);
            } else {
                fileOutputStream.close();
                return true;
            }
        }
    }

    public static boolean copyFile(File file, File file2) throws IOException {
        FileInputStream fileInputStream;
        Throwable e;
        if (!file2.exists()) {
            file2.createNewFile();
        }
        FileInputStream fileInputStream2 = null;
        try {
            fileInputStream = new FileInputStream(file);
            try {
                file = new FileOutputStream(file2);
            } catch (Exception e2) {
                e = e2;
                file = null;
                fileInputStream2 = fileInputStream;
                try {
                    FileLog.m3e(e);
                    if (fileInputStream2 != null) {
                        fileInputStream2.close();
                    }
                    if (file != null) {
                        file.close();
                    }
                    return null;
                } catch (Throwable th) {
                    file2 = th;
                    fileInputStream = fileInputStream2;
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                    if (file != null) {
                        file.close();
                    }
                    throw file2;
                }
            } catch (Throwable th2) {
                file2 = th2;
                file = null;
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (file != null) {
                    file.close();
                }
                throw file2;
            }
            try {
                file.getChannel().transferFrom(fileInputStream.getChannel(), 0, fileInputStream.getChannel().size());
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (file != null) {
                    file.close();
                }
                return true;
            } catch (Exception e3) {
                e = e3;
                fileInputStream2 = fileInputStream;
                FileLog.m3e(e);
                if (fileInputStream2 != null) {
                    fileInputStream2.close();
                }
                if (file != null) {
                    file.close();
                }
                return null;
            } catch (Throwable th3) {
                file2 = th3;
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (file != null) {
                    file.close();
                }
                throw file2;
            }
        } catch (Exception e4) {
            e = e4;
            file = null;
            FileLog.m3e(e);
            if (fileInputStream2 != null) {
                fileInputStream2.close();
            }
            if (file != null) {
                file.close();
            }
            return null;
        } catch (Throwable th4) {
            file2 = th4;
            file = null;
            fileInputStream = file;
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            if (file != null) {
                file.close();
            }
            throw file2;
        }
    }

    public static byte[] calcAuthKeyHash(byte[] bArr) {
        Object obj = new byte[16];
        System.arraycopy(Utilities.computeSHA1(bArr), 0, obj, 0, 16);
        return obj;
    }

    public static void openForView(org.telegram.messenger.MessageObject r8, final android.app.Activity r9) throws java.lang.Exception {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r0 = r8.getFileName();
        r1 = r8.messageOwner;
        r1 = r1.attachPath;
        r2 = 0;
        if (r1 == 0) goto L_0x001f;
    L_0x000b:
        r1 = r8.messageOwner;
        r1 = r1.attachPath;
        r1 = r1.length();
        if (r1 == 0) goto L_0x001f;
    L_0x0015:
        r1 = new java.io.File;
        r3 = r8.messageOwner;
        r3 = r3.attachPath;
        r1.<init>(r3);
        goto L_0x0020;
    L_0x001f:
        r1 = r2;
    L_0x0020:
        if (r1 == 0) goto L_0x0028;
    L_0x0022:
        r3 = r1.exists();
        if (r3 != 0) goto L_0x002e;
    L_0x0028:
        r1 = r8.messageOwner;
        r1 = org.telegram.messenger.FileLoader.getPathToMessage(r1);
    L_0x002e:
        if (r1 == 0) goto L_0x0121;
    L_0x0030:
        r3 = r1.exists();
        if (r3 == 0) goto L_0x0121;
    L_0x0036:
        r3 = new android.content.Intent;
        r4 = "android.intent.action.VIEW";
        r3.<init>(r4);
        r4 = 1;
        r3.setFlags(r4);
        r5 = android.webkit.MimeTypeMap.getSingleton();
        r6 = 46;
        r6 = r0.lastIndexOf(r6);
        r7 = -1;
        if (r6 == r7) goto L_0x007b;
    L_0x004e:
        r6 = r6 + r4;
        r0 = r0.substring(r6);
        r0 = r0.toLowerCase();
        r0 = r5.getMimeTypeFromExtension(r0);
        if (r0 != 0) goto L_0x0079;
    L_0x005d:
        r4 = r8.type;
        r5 = 9;
        if (r4 == r5) goto L_0x006a;
    L_0x0063:
        r4 = r8.type;
        if (r4 != 0) goto L_0x0068;
    L_0x0067:
        goto L_0x006a;
    L_0x0068:
        r8 = r0;
        goto L_0x0070;
    L_0x006a:
        r8 = r8.getDocument();
        r8 = r8.mime_type;
    L_0x0070:
        if (r8 == 0) goto L_0x007b;
    L_0x0072:
        r0 = r8.length();
        if (r0 != 0) goto L_0x007c;
    L_0x0078:
        goto L_0x007b;
    L_0x0079:
        r8 = r0;
        goto L_0x007c;
    L_0x007b:
        r8 = r2;
    L_0x007c:
        r0 = android.os.Build.VERSION.SDK_INT;
        r4 = 26;
        if (r0 < r4) goto L_0x00d6;
    L_0x0082:
        if (r8 == 0) goto L_0x00d6;
    L_0x0084:
        r0 = "application/vnd.android.package-archive";
        r0 = r8.equals(r0);
        if (r0 == 0) goto L_0x00d6;
    L_0x008c:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r0 = r0.getPackageManager();
        r0 = r0.canRequestPackageInstalls();
        if (r0 != 0) goto L_0x00d6;
    L_0x0098:
        r8 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r8.<init>(r9);
        r0 = "AppName";
        r1 = NUM; // 0x7f0c0075 float:1.860943E38 double:1.0530974563E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
        r8.setTitle(r0);
        r0 = "ApkRestricted";
        r1 = NUM; // 0x7f0c0074 float:1.8609427E38 double:1.053097456E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
        r8.setMessage(r0);
        r0 = "PermissionOpenSettings";
        r1 = NUM; // 0x7f0c0503 float:1.8611794E38 double:1.0530980323E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
        r1 = new org.telegram.messenger.AndroidUtilities$6;
        r1.<init>(r9);
        r8.setPositiveButton(r0, r1);
        r9 = "Cancel";
        r0 = NUM; // 0x7f0c0107 float:1.8609725E38 double:1.0530975284E-314;
        r9 = org.telegram.messenger.LocaleController.getString(r9, r0);
        r8.setNegativeButton(r9, r2);
        r8.show();
        return;
    L_0x00d6:
        r0 = android.os.Build.VERSION.SDK_INT;
        r2 = 24;
        if (r0 < r2) goto L_0x00ec;
    L_0x00dc:
        r0 = "org.telegram.messenger.provider";
        r0 = android.support.v4.content.FileProvider.getUriForFile(r9, r0, r1);
        if (r8 == 0) goto L_0x00e6;
    L_0x00e4:
        r4 = r8;
        goto L_0x00e8;
    L_0x00e6:
        r4 = "text/plain";
    L_0x00e8:
        r3.setDataAndType(r0, r4);
        goto L_0x00f9;
    L_0x00ec:
        r0 = android.net.Uri.fromFile(r1);
        if (r8 == 0) goto L_0x00f4;
    L_0x00f2:
        r4 = r8;
        goto L_0x00f6;
    L_0x00f4:
        r4 = "text/plain";
    L_0x00f6:
        r3.setDataAndType(r0, r4);
    L_0x00f9:
        r0 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        if (r8 == 0) goto L_0x011e;
    L_0x00fd:
        r9.startActivityForResult(r3, r0);	 Catch:{ Exception -> 0x0101 }
        goto L_0x0121;
    L_0x0101:
        r8 = android.os.Build.VERSION.SDK_INT;
        if (r8 < r2) goto L_0x0111;
    L_0x0105:
        r8 = "org.telegram.messenger.provider";
        r8 = android.support.v4.content.FileProvider.getUriForFile(r9, r8, r1);
        r1 = "text/plain";
        r3.setDataAndType(r8, r1);
        goto L_0x011a;
    L_0x0111:
        r8 = android.net.Uri.fromFile(r1);
        r1 = "text/plain";
        r3.setDataAndType(r8, r1);
    L_0x011a:
        r9.startActivityForResult(r3, r0);
        goto L_0x0121;
    L_0x011e:
        r9.startActivityForResult(r3, r0);
    L_0x0121:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.openForView(org.telegram.messenger.MessageObject, android.app.Activity):void");
    }

    public static void openForView(org.telegram.tgnet.TLObject r8, android.app.Activity r9) throws java.lang.Exception {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        if (r8 == 0) goto L_0x00a0;
    L_0x0002:
        if (r9 != 0) goto L_0x0006;
    L_0x0004:
        goto L_0x00a0;
    L_0x0006:
        r0 = org.telegram.messenger.FileLoader.getAttachFileName(r8);
        r1 = 1;
        r2 = org.telegram.messenger.FileLoader.getPathToAttach(r8, r1);
        if (r2 == 0) goto L_0x009f;
    L_0x0011:
        r3 = r2.exists();
        if (r3 == 0) goto L_0x009f;
    L_0x0017:
        r3 = new android.content.Intent;
        r4 = "android.intent.action.VIEW";
        r3.<init>(r4);
        r3.setFlags(r1);
        r4 = android.webkit.MimeTypeMap.getSingleton();
        r5 = 46;
        r5 = r0.lastIndexOf(r5);
        r6 = -1;
        r7 = 0;
        if (r5 == r6) goto L_0x0054;
    L_0x002f:
        r5 = r5 + r1;
        r0 = r0.substring(r5);
        r0 = r0.toLowerCase();
        r0 = r4.getMimeTypeFromExtension(r0);
        if (r0 != 0) goto L_0x0053;
    L_0x003e:
        r1 = r8 instanceof org.telegram.tgnet.TLRPC.TL_document;
        if (r1 == 0) goto L_0x0047;
    L_0x0042:
        r8 = (org.telegram.tgnet.TLRPC.TL_document) r8;
        r8 = r8.mime_type;
        goto L_0x0048;
    L_0x0047:
        r8 = r0;
    L_0x0048:
        if (r8 == 0) goto L_0x0054;
    L_0x004a:
        r0 = r8.length();
        if (r0 != 0) goto L_0x0051;
    L_0x0050:
        goto L_0x0054;
    L_0x0051:
        r7 = r8;
        goto L_0x0054;
    L_0x0053:
        r7 = r0;
    L_0x0054:
        r8 = android.os.Build.VERSION.SDK_INT;
        r0 = 24;
        if (r8 < r0) goto L_0x006a;
    L_0x005a:
        r8 = "org.telegram.messenger.provider";
        r8 = android.support.v4.content.FileProvider.getUriForFile(r9, r8, r2);
        if (r7 == 0) goto L_0x0064;
    L_0x0062:
        r1 = r7;
        goto L_0x0066;
    L_0x0064:
        r1 = "text/plain";
    L_0x0066:
        r3.setDataAndType(r8, r1);
        goto L_0x0077;
    L_0x006a:
        r8 = android.net.Uri.fromFile(r2);
        if (r7 == 0) goto L_0x0072;
    L_0x0070:
        r1 = r7;
        goto L_0x0074;
    L_0x0072:
        r1 = "text/plain";
    L_0x0074:
        r3.setDataAndType(r8, r1);
    L_0x0077:
        r8 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        if (r7 == 0) goto L_0x009c;
    L_0x007b:
        r9.startActivityForResult(r3, r8);	 Catch:{ Exception -> 0x007f }
        goto L_0x009f;
    L_0x007f:
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 < r0) goto L_0x008f;
    L_0x0083:
        r0 = "org.telegram.messenger.provider";
        r0 = android.support.v4.content.FileProvider.getUriForFile(r9, r0, r2);
        r1 = "text/plain";
        r3.setDataAndType(r0, r1);
        goto L_0x0098;
    L_0x008f:
        r0 = android.net.Uri.fromFile(r2);
        r1 = "text/plain";
        r3.setDataAndType(r0, r1);
    L_0x0098:
        r9.startActivityForResult(r3, r8);
        goto L_0x009f;
    L_0x009c:
        r9.startActivityForResult(r3, r8);
    L_0x009f:
        return;
    L_0x00a0:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.openForView(org.telegram.tgnet.TLObject, android.app.Activity):void");
    }

    public static boolean isBannedForever(int i) {
        return Math.abs(((long) i) - (System.currentTimeMillis() / 1000)) > 157680000;
    }

    public static void setRectToRect(Matrix matrix, RectF rectF, RectF rectF2, int i, ScaleToFit scaleToFit) {
        float width;
        float height;
        if (i != 90) {
            if (i != 270) {
                width = rectF2.width() / rectF.width();
                height = rectF2.height() / rectF.height();
                if (scaleToFit != ScaleToFit.FILL) {
                    if (width <= height) {
                        width = height;
                    } else {
                        height = width;
                    }
                }
                scaleToFit = (-rectF.left) * width;
                rectF = (-rectF.top) * height;
                matrix.setTranslate(rectF2.left, rectF2.top);
                if (i == 90) {
                    matrix.preRotate(NUM);
                    matrix.preTranslate(0.0f, -rectF2.width());
                } else if (i == 180) {
                    matrix.preRotate(NUM);
                    matrix.preTranslate(-rectF2.width(), -rectF2.height());
                } else if (i == 270) {
                    matrix.preRotate(NUM);
                    matrix.preTranslate(-rectF2.height(), 0.0f);
                }
                matrix.preScale(width, height);
                matrix.preTranslate(scaleToFit, rectF);
            }
        }
        width = rectF2.height() / rectF.width();
        height = rectF2.width() / rectF.height();
        if (scaleToFit != ScaleToFit.FILL) {
            if (width <= height) {
                height = width;
            } else {
                width = height;
            }
        }
        scaleToFit = (-rectF.left) * width;
        rectF = (-rectF.top) * height;
        matrix.setTranslate(rectF2.left, rectF2.top);
        if (i == 90) {
            matrix.preRotate(NUM);
            matrix.preTranslate(0.0f, -rectF2.width());
        } else if (i == 180) {
            matrix.preRotate(NUM);
            matrix.preTranslate(-rectF2.width(), -rectF2.height());
        } else if (i == 270) {
            matrix.preRotate(NUM);
            matrix.preTranslate(-rectF2.height(), 0.0f);
        }
        matrix.preScale(width, height);
        matrix.preTranslate(scaleToFit, rectF);
    }

    public static boolean handleProxyIntent(android.app.Activity r6, android.content.Intent r7) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r0 = 0;
        if (r7 != 0) goto L_0x0004;
    L_0x0003:
        return r0;
    L_0x0004:
        r1 = r7.getFlags();	 Catch:{ Exception -> 0x00eb }
        r2 = 1048576; // 0x100000 float:1.469368E-39 double:5.180654E-318;	 Catch:{ Exception -> 0x00eb }
        r1 = r1 & r2;	 Catch:{ Exception -> 0x00eb }
        if (r1 == 0) goto L_0x000e;	 Catch:{ Exception -> 0x00eb }
    L_0x000d:
        return r0;	 Catch:{ Exception -> 0x00eb }
    L_0x000e:
        r7 = r7.getData();	 Catch:{ Exception -> 0x00eb }
        if (r7 == 0) goto L_0x00eb;	 Catch:{ Exception -> 0x00eb }
    L_0x0014:
        r1 = r7.getScheme();	 Catch:{ Exception -> 0x00eb }
        r2 = 0;	 Catch:{ Exception -> 0x00eb }
        if (r1 == 0) goto L_0x00cf;	 Catch:{ Exception -> 0x00eb }
    L_0x001b:
        r3 = "http";	 Catch:{ Exception -> 0x00eb }
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x00eb }
        if (r3 != 0) goto L_0x0076;	 Catch:{ Exception -> 0x00eb }
    L_0x0023:
        r3 = "https";	 Catch:{ Exception -> 0x00eb }
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x00eb }
        if (r3 == 0) goto L_0x002c;	 Catch:{ Exception -> 0x00eb }
    L_0x002b:
        goto L_0x0076;	 Catch:{ Exception -> 0x00eb }
    L_0x002c:
        r3 = "tg";	 Catch:{ Exception -> 0x00eb }
        r1 = r1.equals(r3);	 Catch:{ Exception -> 0x00eb }
        if (r1 == 0) goto L_0x00cf;	 Catch:{ Exception -> 0x00eb }
    L_0x0034:
        r7 = r7.toString();	 Catch:{ Exception -> 0x00eb }
        r1 = "tg:socks";	 Catch:{ Exception -> 0x00eb }
        r1 = r7.startsWith(r1);	 Catch:{ Exception -> 0x00eb }
        if (r1 != 0) goto L_0x0048;	 Catch:{ Exception -> 0x00eb }
    L_0x0040:
        r1 = "tg://socks";	 Catch:{ Exception -> 0x00eb }
        r1 = r7.startsWith(r1);	 Catch:{ Exception -> 0x00eb }
        if (r1 == 0) goto L_0x00cf;	 Catch:{ Exception -> 0x00eb }
    L_0x0048:
        r1 = "tg:proxy";	 Catch:{ Exception -> 0x00eb }
        r2 = "tg://telegram.org";	 Catch:{ Exception -> 0x00eb }
        r7 = r7.replace(r1, r2);	 Catch:{ Exception -> 0x00eb }
        r1 = "tg://proxy";	 Catch:{ Exception -> 0x00eb }
        r2 = "tg://telegram.org";	 Catch:{ Exception -> 0x00eb }
        r7 = r7.replace(r1, r2);	 Catch:{ Exception -> 0x00eb }
        r7 = android.net.Uri.parse(r7);	 Catch:{ Exception -> 0x00eb }
        r1 = "server";	 Catch:{ Exception -> 0x00eb }
        r2 = r7.getQueryParameter(r1);	 Catch:{ Exception -> 0x00eb }
        r1 = "port";	 Catch:{ Exception -> 0x00eb }
        r1 = r7.getQueryParameter(r1);	 Catch:{ Exception -> 0x00eb }
        r3 = "user";	 Catch:{ Exception -> 0x00eb }
        r3 = r7.getQueryParameter(r3);	 Catch:{ Exception -> 0x00eb }
        r4 = "pass";	 Catch:{ Exception -> 0x00eb }
        r7 = r7.getQueryParameter(r4);	 Catch:{ Exception -> 0x00eb }
        goto L_0x00d2;	 Catch:{ Exception -> 0x00eb }
    L_0x0076:
        r1 = r7.getHost();	 Catch:{ Exception -> 0x00eb }
        r1 = r1.toLowerCase();	 Catch:{ Exception -> 0x00eb }
        r3 = "telegram.me";	 Catch:{ Exception -> 0x00eb }
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x00eb }
        if (r3 != 0) goto L_0x009e;	 Catch:{ Exception -> 0x00eb }
    L_0x0086:
        r3 = "t.me";	 Catch:{ Exception -> 0x00eb }
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x00eb }
        if (r3 != 0) goto L_0x009e;	 Catch:{ Exception -> 0x00eb }
    L_0x008e:
        r3 = "telegram.dog";	 Catch:{ Exception -> 0x00eb }
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x00eb }
        if (r3 != 0) goto L_0x009e;	 Catch:{ Exception -> 0x00eb }
    L_0x0096:
        r3 = "telesco.pe";	 Catch:{ Exception -> 0x00eb }
        r1 = r1.equals(r3);	 Catch:{ Exception -> 0x00eb }
        if (r1 == 0) goto L_0x00c8;	 Catch:{ Exception -> 0x00eb }
    L_0x009e:
        r1 = r7.getPath();	 Catch:{ Exception -> 0x00eb }
        if (r1 == 0) goto L_0x00c8;	 Catch:{ Exception -> 0x00eb }
    L_0x00a4:
        r3 = "/socks";	 Catch:{ Exception -> 0x00eb }
        r1 = r1.startsWith(r3);	 Catch:{ Exception -> 0x00eb }
        if (r1 == 0) goto L_0x00c8;	 Catch:{ Exception -> 0x00eb }
    L_0x00ac:
        r1 = "server";	 Catch:{ Exception -> 0x00eb }
        r2 = r7.getQueryParameter(r1);	 Catch:{ Exception -> 0x00eb }
        r1 = "port";	 Catch:{ Exception -> 0x00eb }
        r1 = r7.getQueryParameter(r1);	 Catch:{ Exception -> 0x00eb }
        r3 = "user";	 Catch:{ Exception -> 0x00eb }
        r3 = r7.getQueryParameter(r3);	 Catch:{ Exception -> 0x00eb }
        r4 = "pass";	 Catch:{ Exception -> 0x00eb }
        r7 = r7.getQueryParameter(r4);	 Catch:{ Exception -> 0x00eb }
        r5 = r3;	 Catch:{ Exception -> 0x00eb }
        r3 = r2;	 Catch:{ Exception -> 0x00eb }
        r2 = r5;	 Catch:{ Exception -> 0x00eb }
        goto L_0x00cb;	 Catch:{ Exception -> 0x00eb }
    L_0x00c8:
        r7 = r2;	 Catch:{ Exception -> 0x00eb }
        r1 = r7;	 Catch:{ Exception -> 0x00eb }
        r3 = r1;	 Catch:{ Exception -> 0x00eb }
    L_0x00cb:
        r5 = r3;	 Catch:{ Exception -> 0x00eb }
        r3 = r2;	 Catch:{ Exception -> 0x00eb }
        r2 = r5;	 Catch:{ Exception -> 0x00eb }
        goto L_0x00d2;	 Catch:{ Exception -> 0x00eb }
    L_0x00cf:
        r7 = r2;	 Catch:{ Exception -> 0x00eb }
        r1 = r7;	 Catch:{ Exception -> 0x00eb }
        r3 = r1;	 Catch:{ Exception -> 0x00eb }
    L_0x00d2:
        r4 = android.text.TextUtils.isEmpty(r2);	 Catch:{ Exception -> 0x00eb }
        if (r4 != 0) goto L_0x00eb;	 Catch:{ Exception -> 0x00eb }
    L_0x00d8:
        r4 = android.text.TextUtils.isEmpty(r1);	 Catch:{ Exception -> 0x00eb }
        if (r4 != 0) goto L_0x00eb;	 Catch:{ Exception -> 0x00eb }
    L_0x00de:
        if (r3 != 0) goto L_0x00e2;	 Catch:{ Exception -> 0x00eb }
    L_0x00e0:
        r3 = "";	 Catch:{ Exception -> 0x00eb }
    L_0x00e2:
        if (r7 != 0) goto L_0x00e6;	 Catch:{ Exception -> 0x00eb }
    L_0x00e4:
        r7 = "";	 Catch:{ Exception -> 0x00eb }
    L_0x00e6:
        showProxyAlert(r6, r2, r1, r3, r7);	 Catch:{ Exception -> 0x00eb }
        r6 = 1;
        return r6;
    L_0x00eb:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.handleProxyIntent(android.app.Activity, android.content.Intent):boolean");
    }

    public static void showProxyAlert(Activity activity, final String str, final String str2, final String str3, final String str4) {
        Builder builder = new Builder((Context) activity);
        builder.setTitle(LocaleController.getString("Proxy", C0446R.string.Proxy));
        activity = new StringBuilder(LocaleController.getString("EnableProxyAlert", C0446R.string.EnableProxyAlert));
        activity.append("\n\n");
        activity.append(LocaleController.getString("UseProxyAddress", C0446R.string.UseProxyAddress));
        activity.append(": ");
        activity.append(str);
        activity.append("\n");
        activity.append(LocaleController.getString("UseProxyPort", C0446R.string.UseProxyPort));
        activity.append(": ");
        activity.append(str2);
        activity.append("\n");
        if (!TextUtils.isEmpty(str3)) {
            activity.append(LocaleController.getString("UseProxyUsername", C0446R.string.UseProxyUsername));
            activity.append(": ");
            activity.append(str3);
            activity.append("\n");
        }
        if (!TextUtils.isEmpty(str4)) {
            activity.append(LocaleController.getString("UseProxyPassword", C0446R.string.UseProxyPassword));
            activity.append(": ");
            activity.append(str4);
            activity.append("\n");
        }
        activity.append("\n");
        activity.append(LocaleController.getString("EnableProxyAlert2", C0446R.string.EnableProxyAlert2));
        builder.setMessage(activity.toString());
        builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", C0446R.string.ConnectingToProxyEnable), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface = MessagesController.getGlobalMainSettings().edit();
                dialogInterface.putBoolean("proxy_enabled", true);
                dialogInterface.putString("proxy_ip", str);
                i = Utilities.parseInt(str2).intValue();
                dialogInterface.putInt("proxy_port", i);
                if (TextUtils.isEmpty(str4)) {
                    dialogInterface.remove("proxy_pass");
                } else {
                    dialogInterface.putString("proxy_pass", str4);
                }
                if (TextUtils.isEmpty(str3)) {
                    dialogInterface.remove("proxy_user");
                } else {
                    dialogInterface.putString("proxy_user", str3);
                }
                dialogInterface.commit();
                for (int i2 = 0; i2 < 3; i2++) {
                    ConnectionsManager.native_setProxySettings(i2, str, i, str3, str4);
                }
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
        builder.show().setCanceledOnTouchOutside(true);
    }
}
