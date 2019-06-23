package org.telegram.messenger;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
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
import android.os.PowerManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.telephony.TelephonyManager;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.StateSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.webkit.MimeTypeMap;
import android.widget.EdgeEffect;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;
import com.android.internal.telephony.ITelephony;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;
import net.hockeyapp.android.UpdateManager;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.messenger.SharedConfig.ProxyInfo;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_userContact_old2;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.tgnet.TLRPC.TL_wallPaperSettings;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Components.ForegroundDetector;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PickerBottomLayout;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.WallpapersListActivity.ColorWallpaper;

public class AndroidUtilities {
    public static final int FLAG_TAG_ALL = 11;
    public static final int FLAG_TAG_BOLD = 2;
    public static final int FLAG_TAG_BR = 1;
    public static final int FLAG_TAG_COLOR = 4;
    public static final int FLAG_TAG_URL = 8;
    public static Pattern WEB_URL;
    public static AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
    private static int adjustOwnerClassGuid = 0;
    private static RectF bitmapRect;
    private static final Object callLock = new Object();
    private static CallReceiver callReceiver;
    public static DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    public static float density = 1.0f;
    public static DisplayMetrics displayMetrics = new DisplayMetrics();
    public static Point displaySize = new Point();
    private static int[] documentIcons = new int[]{NUM, NUM, NUM, NUM};
    private static int[] documentMediaIcons = new int[]{NUM, NUM, NUM, NUM};
    public static boolean firstConfigurationWas;
    private static boolean hasCallPermissions;
    public static boolean incorrectDisplaySizeFix;
    public static boolean isInMultiwindow;
    private static Boolean isTablet = null;
    public static int leftBaseline = (isTablet() ? 80 : 72);
    private static Field mAttachInfoField;
    private static Field mStableInsetsField;
    public static OvershootInterpolator overshootInterpolator = new OvershootInterpolator();
    public static Integer photoSize = null;
    private static int prevOrientation = -10;
    public static int roundMessageSize;
    private static Paint roundPaint;
    private static final Object smsLock = new Object();
    public static int statusBarHeight = 0;
    private static final Hashtable<String, Typeface> typefaceCache = new Hashtable();
    public static boolean usingHardwareInput;
    private static boolean waitingForCall = false;
    private static boolean waitingForSms = false;

    public static class LinkMovementMethodMy extends LinkMovementMethod {
        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                boolean onTouchEvent = super.onTouchEvent(textView, spannable, motionEvent);
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    Selection.removeSelection(spannable);
                }
                return onTouchEvent;
            } catch (Exception e) {
                FileLog.e(e);
                return false;
            }
        }
    }

    private static class VcardData {
        String name;
        ArrayList<String> phones;
        StringBuilder vcard;

        private VcardData() {
            this.phones = new ArrayList();
            this.vcard = new StringBuilder();
        }

        /* synthetic */ VcardData(AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    public static class VcardItem {
        public boolean checked = true;
        public String fullData = "";
        public int type;
        public ArrayList<String> vcardData = new ArrayList();

        public String[] getRawValue() {
            int indexOf = this.fullData.indexOf(58);
            int i = 0;
            if (indexOf < 0) {
                return new String[0];
            }
            String substring = this.fullData.substring(0, indexOf);
            String substring2 = this.fullData.substring(indexOf + 1);
            String str = ";";
            String[] split = substring.split(str);
            String str2 = "UTF-8";
            String str3 = null;
            for (String split2 : split) {
                String[] split3 = split2.split("=");
                if (split3.length == 2) {
                    if (split3[0].equals("CHARSET")) {
                        str2 = split3[1];
                    } else if (split3[0].equals("ENCODING")) {
                        str3 = split3[1];
                    }
                }
            }
            String[] split4 = substring2.split(str);
            while (i < split4.length) {
                if (!(TextUtils.isEmpty(split4[i]) || str3 == null || !str3.equalsIgnoreCase("QUOTED-PRINTABLE"))) {
                    byte[] decodeQuotedPrintable = AndroidUtilities.decodeQuotedPrintable(AndroidUtilities.getStringBytes(split4[i]));
                    if (!(decodeQuotedPrintable == null || decodeQuotedPrintable.length == 0)) {
                        try {
                            split4[i] = new String(decodeQuotedPrintable, str2);
                        } catch (Exception unused) {
                        }
                    }
                }
                i++;
            }
            return split4;
        }

        public String getValue(boolean z) {
            StringBuilder stringBuilder = new StringBuilder();
            int indexOf = this.fullData.indexOf(58);
            if (indexOf < 0) {
                return "";
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.append(", ");
            }
            String substring = this.fullData.substring(0, indexOf);
            String substring2 = this.fullData.substring(indexOf + 1);
            String str = ";";
            String[] split = substring.split(str);
            String str2 = "UTF-8";
            String str3 = null;
            for (String split2 : split) {
                String[] split3 = split2.split("=");
                if (split3.length == 2) {
                    if (split3[0].equals("CHARSET")) {
                        str2 = split3[1];
                    } else if (split3[0].equals("ENCODING")) {
                        str3 = split3[1];
                    }
                }
            }
            String[] split4 = substring2.split(str);
            Object obj = null;
            for (int i = 0; i < split4.length; i++) {
                if (!TextUtils.isEmpty(split4[i])) {
                    if (str3 != null && str3.equalsIgnoreCase("QUOTED-PRINTABLE")) {
                        byte[] decodeQuotedPrintable = AndroidUtilities.decodeQuotedPrintable(AndroidUtilities.getStringBytes(split4[i]));
                        if (!(decodeQuotedPrintable == null || decodeQuotedPrintable.length == 0)) {
                            try {
                                split4[i] = new String(decodeQuotedPrintable, str2);
                            } catch (Exception unused) {
                            }
                        }
                    }
                    if (obj != null && stringBuilder.length() > 0) {
                        stringBuilder.append(" ");
                    }
                    stringBuilder.append(split4[i]);
                    if (obj == null) {
                        obj = split4[i].length() > 0 ? 1 : null;
                    }
                }
            }
            if (z) {
                int i2 = this.type;
                if (i2 == 0) {
                    return PhoneFormat.getInstance().format(stringBuilder.toString());
                }
                if (i2 == 5) {
                    String[] split5 = stringBuilder.toString().split("T");
                    if (split5.length > 0) {
                        split5 = split5[0].split("-");
                        if (split5.length == 3) {
                            Calendar instance = Calendar.getInstance();
                            instance.set(1, Utilities.parseInt(split5[0]).intValue());
                            instance.set(2, Utilities.parseInt(split5[1]).intValue() - 1);
                            instance.set(5, Utilities.parseInt(split5[2]).intValue());
                            return LocaleController.getInstance().formatterYearMax.format(instance.getTime());
                        }
                    }
                }
            }
            return stringBuilder.toString();
        }

        public String getRawType(boolean z) {
            int indexOf = this.fullData.indexOf(58);
            String str = "";
            if (indexOf < 0) {
                return str;
            }
            int i = 0;
            String substring = this.fullData.substring(0, indexOf);
            String str2 = ";";
            if (this.type == 20) {
                String[] split = substring.substring(2).split(str2);
                if (z) {
                    str = split[0];
                } else if (split.length > 1) {
                    str = split[split.length - 1];
                }
                return str;
            }
            String[] split2 = substring.split(str2);
            while (i < split2.length) {
                if (split2[i].indexOf(61) < 0) {
                    substring = split2[i];
                }
                i++;
            }
            return substring;
        }

        public String getType() {
            int i = this.type;
            if (i == 5) {
                return LocaleController.getString("ContactBirthday", NUM);
            }
            if (i == 6) {
                if ("ORG".equalsIgnoreCase(getRawType(true))) {
                    return LocaleController.getString("ContactJob", NUM);
                }
                return LocaleController.getString("ContactJobTitle", NUM);
            }
            i = this.fullData.indexOf(58);
            if (i < 0) {
                return "";
            }
            String substring = this.fullData.substring(0, i);
            String str = ";";
            if (this.type == 20) {
                substring = substring.substring(2).split(str)[0];
            } else {
                String[] split = substring.split(str);
                String str2 = substring;
                for (i = 0; i < split.length; i++) {
                    if (split[i].indexOf(61) < 0) {
                        str2 = split[i];
                    }
                }
                substring = str2.startsWith("X-") ? str2.substring(2) : str2;
                int i2 = -1;
                switch (substring.hashCode()) {
                    case -2015525726:
                        if (substring.equals("MOBILE")) {
                            i2 = 2;
                            break;
                        }
                        break;
                    case 2064738:
                        if (substring.equals("CELL")) {
                            i2 = 3;
                            break;
                        }
                        break;
                    case 2223327:
                        if (substring.equals("HOME")) {
                            i2 = 1;
                            break;
                        }
                        break;
                    case 2464291:
                        if (substring.equals("PREF")) {
                            i2 = 0;
                            break;
                        }
                        break;
                    case 2670353:
                        if (substring.equals("WORK")) {
                            i2 = 5;
                            break;
                        }
                        break;
                    case 75532016:
                        if (substring.equals("OTHER")) {
                            i2 = 4;
                            break;
                        }
                        break;
                }
                if (i2 == 0) {
                    substring = LocaleController.getString("PhoneMain", NUM);
                } else if (i2 == 1) {
                    substring = LocaleController.getString("PhoneHome", NUM);
                } else if (i2 == 2 || i2 == 3) {
                    substring = LocaleController.getString("PhoneMobile", NUM);
                } else if (i2 == 4) {
                    substring = LocaleController.getString("PhoneOther", NUM);
                } else if (i2 == 5) {
                    substring = LocaleController.getString("PhoneWork", NUM);
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(substring.substring(0, 1).toUpperCase());
            stringBuilder.append(substring.substring(1).toLowerCase());
            return stringBuilder.toString();
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
        return (((long) i) & 4294967295L) | 4294967296L;
    }

    public static int setMyLayerVersion(int i, int i2) {
        return (i & -65536) | i2;
    }

    public static int setPeerLayerVersion(int i, int i2) {
        return (i & 65535) | (i2 << 16);
    }

    static {
        boolean z = false;
        WEB_URL = null;
        try {
            Pattern compile = Pattern.compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9]))");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("(([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯\\-]{0,61}[a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]){0,1}\\.)+[a-zA-Z -퟿豈-﷏ﷰ-￯]{2,63}|");
            stringBuilder.append(compile);
            stringBuilder.append(")");
            compile = Pattern.compile(stringBuilder.toString());
            stringBuilder = new StringBuilder();
            stringBuilder.append("((?:(http|https|Http|Https):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?(?:");
            stringBuilder.append(compile);
            stringBuilder.append(")(?:\\:\\d{1,5})?)(\\/(?:(?:[");
            stringBuilder.append("a-zA-Z0-9 -퟿豈-﷏ﷰ-￯");
            stringBuilder.append("\\;\\/\\?\\:\\@\\&\\=\\#\\~\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?(?:\\b|$)");
            WEB_URL = Pattern.compile(stringBuilder.toString());
        } catch (Exception e) {
            FileLog.e(e);
        }
        checkDisplaySize(ApplicationLoader.applicationContext, null);
        if (VERSION.SDK_INT >= 23) {
            z = true;
        }
        hasCallPermissions = z;
    }

    public static int getThumbForNameOrMime(String str, String str2, boolean z) {
        if (str == null || str.length() == 0) {
            return z ? documentMediaIcons[0] : documentIcons[0];
        }
        int i = (str.contains(".doc") || str.contains(".txt") || str.contains(".psd")) ? 0 : (str.contains(".xls") || str.contains(".csv")) ? 1 : (str.contains(".pdf") || str.contains(".ppt") || str.contains(".key")) ? 2 : (str.contains(".zip") || str.contains(".rar") || str.contains(".ai") || str.contains(".mp3") || str.contains(".mov") || str.contains(".avi")) ? 3 : -1;
        if (i == -1) {
            i = str.lastIndexOf(46);
            String substring = i == -1 ? "" : str.substring(i + 1);
            if (substring.length() != 0) {
                i = substring.charAt(0) % documentIcons.length;
            } else {
                i = str.charAt(0) % documentIcons.length;
            }
        }
        return z ? documentMediaIcons[i] : documentIcons[i];
    }

    public static int[] calcDrawableColor(Drawable drawable) {
        Drawable drawable2 = drawable;
        int[] iArr = new int[4];
        int i = -16777216;
        try {
            if (drawable2 instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable2).getBitmap();
                if (bitmap != null) {
                    Bitmap createScaledBitmap = Bitmaps.createScaledBitmap(bitmap, 1, 1, true);
                    if (createScaledBitmap != null) {
                        i = createScaledBitmap.getPixel(0, 0);
                        if (bitmap != createScaledBitmap) {
                            createScaledBitmap.recycle();
                        }
                    }
                }
            } else if (drawable2 instanceof ColorDrawable) {
                i = ((ColorDrawable) drawable2).getColor();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        double[] rgbToHsv = rgbToHsv((i >> 16) & 255, (i >> 8) & 255, i & 255);
        rgbToHsv[1] = Math.min(1.0d, (rgbToHsv[1] + 0.05d) + ((1.0d - rgbToHsv[1]) * 0.1d));
        int[] hsvToRgb = hsvToRgb(rgbToHsv[0], rgbToHsv[1], Math.max(0.0d, rgbToHsv[2] * 0.65d));
        iArr[0] = Color.argb(102, hsvToRgb[0], hsvToRgb[1], hsvToRgb[2]);
        iArr[1] = Color.argb(136, hsvToRgb[0], hsvToRgb[1], hsvToRgb[2]);
        int[] hsvToRgb2 = hsvToRgb(rgbToHsv[0], rgbToHsv[1], Math.max(0.0d, rgbToHsv[2] * 0.72d));
        iArr[2] = Color.argb(102, hsvToRgb2[0], hsvToRgb2[1], hsvToRgb2[2]);
        iArr[3] = Color.argb(136, hsvToRgb2[0], hsvToRgb2[1], hsvToRgb2[2]);
        return iArr;
    }

    private static double[] rgbToHsv(int i, int i2, int i3) {
        double d = (double) i;
        Double.isNaN(d);
        d /= 255.0d;
        double d2 = (double) i2;
        Double.isNaN(d2);
        d2 /= 255.0d;
        double d3 = (double) i3;
        Double.isNaN(d3);
        double d4 = d3 / 255.0d;
        d3 = (d <= d2 || d <= d4) ? d2 > d4 ? d2 : d4 : d;
        double d5 = (d >= d2 || d >= d4) ? d2 < d4 ? d2 : d4 : d;
        double d6 = d3 - d5;
        double d7 = 0.0d;
        double d8 = d3 == 0.0d ? 0.0d : d6 / d3;
        if (d3 != d5) {
            if (d > d2 && d > d4) {
                d = (d2 - d4) / d6;
                d4 = (double) (d2 < d4 ? 6 : 0);
                Double.isNaN(d4);
            } else if (d2 > d4) {
                d = 2.0d + ((d4 - d) / d6);
                d7 = d / 6.0d;
            } else {
                d = (d - d2) / d6;
                d4 = 4.0d;
            }
            d += d4;
            d7 = d / 6.0d;
        }
        return new double[]{d7, d8, d3};
    }

    private static int[] hsvToRgb(double d, double d2, double d3) {
        double d4 = 6.0d * d;
        double floor = (double) ((int) Math.floor(d4));
        Double.isNaN(floor);
        d4 -= floor;
        double d5 = (1.0d - d2) * d3;
        double d6 = (1.0d - (d4 * d2)) * d3;
        d4 = d3 * (1.0d - ((1.0d - d4) * d2));
        int i = ((int) floor) % 6;
        double d7 = 0.0d;
        if (i != 0) {
            if (i == 1) {
                d7 = d3;
                d4 = d5;
                d5 = d6;
            } else if (i == 2) {
                d7 = d3;
            } else if (i == 3) {
                d4 = d3;
                d7 = d6;
            } else if (i == 4) {
                d7 = d5;
                d5 = d4;
                d4 = d3;
            } else if (i != 5) {
                d4 = 0.0d;
                d5 = d4;
            } else {
                d7 = d5;
                d4 = d6;
            }
            return new int[]{(int) (d5 * 255.0d), (int) (d7 * 255.0d), (int) (d4 * 255.0d)};
        }
        d7 = d4;
        d4 = d5;
        d5 = d3;
        return new int[]{(int) (d5 * 255.0d), (int) (d7 * 255.0d), (int) (d4 * 255.0d)};
    }

    public static void requestAdjustResize(Activity activity, int i) {
        if (activity != null && !isTablet()) {
            activity.getWindow().setSoftInputMode(16);
            adjustOwnerClassGuid = i;
        }
    }

    public static void setAdjustResizeToNothing(Activity activity, int i) {
        if (activity != null && !isTablet() && adjustOwnerClassGuid == i) {
            activity.getWindow().setSoftInputMode(48);
        }
    }

    public static void removeAdjustResize(Activity activity, int i) {
        if (activity != null && !isTablet() && adjustOwnerClassGuid == i) {
            activity.getWindow().setSoftInputMode(32);
        }
    }

    public static boolean isGoogleMapsInstalled(BaseFragment baseFragment) {
        try {
            ApplicationLoader.applicationContext.getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
            return true;
        } catch (NameNotFoundException unused) {
            if (baseFragment.getParentActivity() == null) {
                return false;
            }
            Builder builder = new Builder(baseFragment.getParentActivity());
            builder.setMessage(LocaleController.getString("InstallGoogleMaps", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$AndroidUtilities$9EW49D3frdKbOrTr4GJNg1xI0yM(baseFragment));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            baseFragment.showDialog(builder.create());
            return false;
        }
    }

    static /* synthetic */ void lambda$isGoogleMapsInstalled$0(BaseFragment baseFragment, DialogInterface dialogInterface, int i) {
        try {
            baseFragment.getParentActivity().startActivityForResult(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.google.android.apps.maps")), 500);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static int[] toIntArray(List<Integer> list) {
        int[] iArr = new int[list.size()];
        for (int i = 0; i < iArr.length; i++) {
            iArr[i] = ((Integer) list.get(i)).intValue();
        }
        return iArr;
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x005a A:{SYNTHETIC, Splitter:B:21:0x005a} */
    public static boolean isInternalUri(android.net.Uri r5) {
        /*
        r5 = r5.getPath();
        r0 = 0;
        if (r5 != 0) goto L_0x0008;
    L_0x0007:
        return r0;
    L_0x0008:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = new java.io.File;
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r3 = r3.getCacheDir();
        r4 = "voip_logs";
        r2.<init>(r3, r4);
        r2 = r2.getAbsolutePath();
        r2 = java.util.regex.Pattern.quote(r2);
        r1.append(r2);
        r2 = "/\\d+\\.log";
        r1.append(r2);
        r1 = r1.toString();
        r1 = r5.matches(r1);
        if (r1 == 0) goto L_0x0036;
    L_0x0035:
        return r0;
    L_0x0036:
        r1 = 0;
    L_0x0037:
        r2 = 1;
        if (r5 == 0) goto L_0x0043;
    L_0x003a:
        r3 = r5.length();
        r4 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        if (r3 <= r4) goto L_0x0043;
    L_0x0042:
        return r2;
    L_0x0043:
        r3 = org.telegram.messenger.Utilities.readlink(r5);
        if (r3 == 0) goto L_0x0058;
    L_0x0049:
        r4 = r3.equals(r5);
        if (r4 == 0) goto L_0x0050;
    L_0x004f:
        goto L_0x0058;
    L_0x0050:
        r1 = r1 + r2;
        r5 = 10;
        if (r1 < r5) goto L_0x0056;
    L_0x0055:
        return r2;
    L_0x0056:
        r5 = r3;
        goto L_0x0037;
    L_0x0058:
        if (r5 == 0) goto L_0x006e;
    L_0x005a:
        r1 = new java.io.File;	 Catch:{ Exception -> 0x0067 }
        r1.<init>(r5);	 Catch:{ Exception -> 0x0067 }
        r1 = r1.getCanonicalPath();	 Catch:{ Exception -> 0x0067 }
        if (r1 == 0) goto L_0x006e;
    L_0x0065:
        r5 = r1;
        goto L_0x006e;
    L_0x0067:
        r1 = "/./";
        r3 = "/";
        r5.replace(r1, r3);
    L_0x006e:
        if (r5 == 0) goto L_0x0092;
    L_0x0070:
        r5 = r5.toLowerCase();
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r3 = "/data/data/";
        r1.append(r3);
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r3 = r3.getPackageName();
        r1.append(r3);
        r1 = r1.toString();
        r5 = r5.contains(r1);
        if (r5 == 0) goto L_0x0092;
    L_0x0091:
        r0 = 1;
    L_0x0092:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.isInternalUri(android.net.Uri):boolean");
    }

    @SuppressLint({"WrongConstant"})
    public static void lockOrientation(Activity activity) {
        if (activity != null && prevOrientation == -10) {
            try {
                prevOrientation = activity.getRequestedOrientation();
                WindowManager windowManager = (WindowManager) activity.getSystemService("window");
                if (windowManager != null && windowManager.getDefaultDisplay() != null) {
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
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    @SuppressLint({"WrongConstant"})
    public static void unlockOrientation(Activity activity) {
        if (activity != null) {
            try {
                if (prevOrientation != -10) {
                    activity.setRequestedOrientation(prevOrientation);
                    prevOrientation = -10;
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static byte[] getStringBytes(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (Exception unused) {
            return new byte[0];
        }
    }

    public static ArrayList<User> loadVCardFromStream(Uri uri, int i, boolean z, ArrayList<VcardItem> arrayList, String str) {
        InputStream createInputStream;
        Throwable th;
        Uri uri2 = uri;
        ArrayList arrayList2 = arrayList;
        ArrayList<User> arrayList3 = null;
        if (z) {
            try {
                createInputStream = ApplicationLoader.applicationContext.getContentResolver().openAssetFileDescriptor(uri2, "r").createInputStream();
            } catch (Throwable th2) {
                th = th2;
            }
        } else {
            try {
                createInputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri2);
            } catch (Exception th3) {
                FileLog.e(th3);
            } catch (Throwable th4) {
                th3 = th4;
                arrayList3 = null;
                FileLog.e(th3);
                return arrayList3;
            }
        }
        ArrayList arrayList4 = new ArrayList();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(createInputStream, "UTF-8"));
        int i2 = 1;
        int i3 = 0;
        VcardData vcardData = null;
        String str2 = vcardData;
        VcardItem vcardItem = str2;
        Object obj = null;
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine == null) {
                break;
            } else if (readLine.startsWith("PHOTO")) {
                obj = 1;
            } else {
                String[] strArr;
                String str3 = "ORG";
                String str4 = "TEL";
                String str5;
                if (readLine.indexOf(58) >= 0) {
                    if (readLine.startsWith("BEGIN:VCARD")) {
                        VcardData vcardData2 = new VcardData(arrayList3);
                        arrayList4.add(vcardData2);
                        vcardData2.name = str;
                        vcardItem = arrayList3;
                        vcardData = vcardData2;
                    } else {
                        str5 = str;
                        if (!readLine.startsWith("END:VCARD")) {
                            if (arrayList2 != null) {
                                VcardItem vcardItem2;
                                if (readLine.startsWith(str4)) {
                                    vcardItem2 = new VcardItem();
                                    vcardItem2.type = i3;
                                } else if (readLine.startsWith("EMAIL")) {
                                    vcardItem2 = new VcardItem();
                                    vcardItem2.type = i2;
                                } else {
                                    if (!(readLine.startsWith("ADR") || readLine.startsWith("LABEL"))) {
                                        if (!readLine.startsWith("GEO")) {
                                            if (readLine.startsWith("URL")) {
                                                vcardItem2 = new VcardItem();
                                                vcardItem2.type = 3;
                                            } else if (readLine.startsWith("NOTE")) {
                                                vcardItem2 = new VcardItem();
                                                vcardItem2.type = 4;
                                            } else if (readLine.startsWith("BDAY")) {
                                                vcardItem2 = new VcardItem();
                                                vcardItem2.type = 5;
                                            } else {
                                                if (!(readLine.startsWith(str3) || readLine.startsWith("TITLE"))) {
                                                    if (!readLine.startsWith("ROLE")) {
                                                        if (readLine.startsWith("X-ANDROID")) {
                                                            vcardItem2 = new VcardItem();
                                                            vcardItem2.type = -1;
                                                        } else {
                                                            if (!readLine.startsWith("X-PHONETIC")) {
                                                                if (readLine.startsWith("X-")) {
                                                                    vcardItem2 = new VcardItem();
                                                                    vcardItem2.type = 20;
                                                                }
                                                            }
                                                            vcardItem2 = arrayList3;
                                                        }
                                                    }
                                                }
                                                vcardItem2 = new VcardItem();
                                                vcardItem2.type = 6;
                                            }
                                        }
                                    }
                                    vcardItem2 = new VcardItem();
                                    vcardItem2.type = 2;
                                }
                                if (vcardItem2 != null && vcardItem2.type >= 0) {
                                    arrayList2.add(vcardItem2);
                                }
                                vcardItem = vcardItem2;
                            }
                        }
                        vcardItem = arrayList3;
                    }
                    obj = null;
                } else {
                    str5 = str;
                }
                if (obj == null && vcardData != null) {
                    if (vcardItem == null) {
                        if (vcardData.vcard.length() > 0) {
                            vcardData.vcard.append(10);
                        }
                        vcardData.vcard.append(readLine);
                    } else {
                        vcardItem.vcardData.add(readLine);
                    }
                }
                if (str2 != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(str2);
                    stringBuilder.append(readLine);
                    readLine = stringBuilder.toString();
                    str2 = null;
                }
                String str6 = "=";
                if (readLine.contains("=QUOTED-PRINTABLE")) {
                    if (readLine.endsWith(str6)) {
                        str2 = readLine.substring(i3, readLine.length() - i2);
                        arrayList3 = null;
                    }
                }
                if (!(obj != null || vcardData == null || vcardItem == null)) {
                    vcardItem.fullData = readLine;
                }
                if (readLine.indexOf(":") >= 0) {
                    strArr = new String[]{readLine.substring(i3, readLine.indexOf(":")), readLine.substring(readLine.indexOf(":") + 1).trim()};
                } else {
                    strArr = new String[]{readLine.trim()};
                }
                if (strArr.length >= 2) {
                    if (vcardData != null) {
                        if (!strArr[i3].startsWith("FN")) {
                            if (!strArr[i3].startsWith(str3) || !TextUtils.isEmpty(vcardData.name)) {
                                if (strArr[i3].startsWith(str4)) {
                                    vcardData.phones.add(strArr[1]);
                                }
                            }
                        }
                        String[] split = strArr[i3].split(";");
                        i2 = split.length;
                        int i4 = 0;
                        str3 = null;
                        str4 = null;
                        while (i4 < i2) {
                            String[] split2 = split[i4].split(str6);
                            String[] strArr2 = split;
                            int i5 = i2;
                            if (split2.length == 2) {
                                if (split2[0].equals("CHARSET")) {
                                    str4 = split2[1];
                                } else if (split2[0].equals("ENCODING")) {
                                    str3 = split2[1];
                                }
                            }
                            i4++;
                            split = strArr2;
                            i2 = i5;
                        }
                        vcardData.name = strArr[1];
                        if (str3 != null && str3.equalsIgnoreCase("QUOTED-PRINTABLE")) {
                            byte[] decodeQuotedPrintable = decodeQuotedPrintable(getStringBytes(vcardData.name));
                            if (!(decodeQuotedPrintable == null || decodeQuotedPrintable.length == 0)) {
                                vcardData.name = new String(decodeQuotedPrintable, str4);
                            }
                        }
                        arrayList2 = arrayList;
                        arrayList3 = null;
                        i2 = 1;
                        i3 = 0;
                    }
                }
                arrayList2 = arrayList;
                arrayList3 = null;
                i2 = 1;
                i3 = 0;
            }
        }
        bufferedReader.close();
        createInputStream.close();
        arrayList3 = null;
        for (int i6 = 0; i6 < arrayList4.size(); i6++) {
            VcardData vcardData3 = (VcardData) arrayList4.get(i6);
            if (vcardData3.name != null && !vcardData3.phones.isEmpty()) {
                if (arrayList3 == null) {
                    arrayList3 = new ArrayList();
                }
                String str7 = (String) vcardData3.phones.get(0);
                for (i2 = 0; i2 < vcardData3.phones.size(); i2++) {
                    String str8 = (String) vcardData3.phones.get(i2);
                    if (ContactsController.getInstance(i).contactsByShortPhone.get(str8.substring(Math.max(0, str8.length() - 7))) != null) {
                        str7 = str8;
                        break;
                    }
                }
                TL_userContact_old2 tL_userContact_old2 = new TL_userContact_old2();
                tL_userContact_old2.phone = str7;
                tL_userContact_old2.first_name = vcardData3.name;
                tL_userContact_old2.last_name = "";
                tL_userContact_old2.id = 0;
                tL_userContact_old2.restriction_reason = vcardData3.vcard.toString();
                arrayList3.add(tL_userContact_old2);
            }
        }
        return arrayList3;
    }

    public static Typeface getTypeface(String str) {
        Typeface typeface;
        synchronized (typefaceCache) {
            if (!typefaceCache.containsKey(str)) {
                try {
                    Object build;
                    if (VERSION.SDK_INT >= 26) {
                        Typeface.Builder builder = new Typeface.Builder(ApplicationLoader.applicationContext.getAssets(), str);
                        if (str.contains("medium")) {
                            builder.setWeight(700);
                        }
                        if (str.contains("italic")) {
                            builder.setItalic(true);
                        }
                        build = builder.build();
                    } else {
                        build = Typeface.createFromAsset(ApplicationLoader.applicationContext.getAssets(), str);
                    }
                    typefaceCache.put(str, build);
                } catch (Exception e) {
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Could not get typeface '");
                        stringBuilder.append(str);
                        stringBuilder.append("' because ");
                        stringBuilder.append(e.getMessage());
                        FileLog.e(stringBuilder.toString());
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
            try {
                if (waitingForSms) {
                    SmsRetriever.getClient(ApplicationLoader.applicationContext).startSmsRetriever().addOnSuccessListener(-$$Lambda$AndroidUtilities$PIS3tB-hc0vfcfJPQ8BqBwJb5lo.INSTANCE);
                }
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
    }

    static /* synthetic */ void lambda$setWaitingForSms$1(Void voidR) {
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("sms listener registered");
        }
    }

    public static int getShadowHeight() {
        float f = density;
        if (f >= 4.0f) {
            return 3;
        }
        return f >= 2.0f ? 2 : 1;
    }

    public static boolean isWaitingForCall() {
        boolean z;
        synchronized (callLock) {
            z = waitingForCall;
        }
        return z;
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x002e */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    public static void setWaitingForCall(boolean r4) {
        /*
        r0 = callLock;
        monitor-enter(r0);
        if (r4 == 0) goto L_0x001d;
    L_0x0005:
        r1 = callReceiver;	 Catch:{ Exception -> 0x002e }
        if (r1 != 0) goto L_0x002e;
    L_0x0009:
        r1 = new android.content.IntentFilter;	 Catch:{ Exception -> 0x002e }
        r2 = "android.intent.action.PHONE_STATE";
        r1.<init>(r2);	 Catch:{ Exception -> 0x002e }
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x002e }
        r3 = new org.telegram.messenger.CallReceiver;	 Catch:{ Exception -> 0x002e }
        r3.<init>();	 Catch:{ Exception -> 0x002e }
        callReceiver = r3;	 Catch:{ Exception -> 0x002e }
        r2.registerReceiver(r3, r1);	 Catch:{ Exception -> 0x002e }
        goto L_0x002e;
    L_0x001d:
        r1 = callReceiver;	 Catch:{ Exception -> 0x002e }
        if (r1 == 0) goto L_0x002e;
    L_0x0021:
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x002e }
        r2 = callReceiver;	 Catch:{ Exception -> 0x002e }
        r1.unregisterReceiver(r2);	 Catch:{ Exception -> 0x002e }
        r1 = 0;
        callReceiver = r1;	 Catch:{ Exception -> 0x002e }
        goto L_0x002e;
    L_0x002c:
        r4 = move-exception;
        goto L_0x0032;
    L_0x002e:
        waitingForCall = r4;	 Catch:{ all -> 0x002c }
        monitor-exit(r0);	 Catch:{ all -> 0x002c }
        return;
    L_0x0032:
        monitor-exit(r0);	 Catch:{ all -> 0x002c }
        throw r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.setWaitingForCall(boolean):void");
    }

    public static boolean showKeyboard(View view) {
        if (view == null) {
            return false;
        }
        try {
            return ((InputMethodManager) view.getContext().getSystemService("input_method")).showSoftInput(view, 1);
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    public static boolean isKeyboardShowed(View view) {
        if (view == null) {
            return false;
        }
        try {
            return ((InputMethodManager) view.getContext().getSystemService("input_method")).isActive(view);
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    public static String[] getCurrentKeyboardLanguage() {
        String str = "en";
        try {
            CharSequence languageTag;
            CharSequence languageTag2;
            InputMethodManager inputMethodManager = (InputMethodManager) ApplicationLoader.applicationContext.getSystemService("input_method");
            InputMethodSubtype currentInputMethodSubtype = inputMethodManager.getCurrentInputMethodSubtype();
            if (currentInputMethodSubtype != null) {
                languageTag = VERSION.SDK_INT >= 24 ? currentInputMethodSubtype.getLanguageTag() : null;
                if (TextUtils.isEmpty(languageTag)) {
                    languageTag = currentInputMethodSubtype.getLocale();
                }
            } else {
                InputMethodSubtype lastInputMethodSubtype = inputMethodManager.getLastInputMethodSubtype();
                if (lastInputMethodSubtype != null) {
                    languageTag2 = VERSION.SDK_INT >= 24 ? lastInputMethodSubtype.getLanguageTag() : null;
                    languageTag = TextUtils.isEmpty(languageTag2) ? lastInputMethodSubtype.getLocale() : languageTag2;
                } else {
                    languageTag = null;
                }
            }
            if (TextUtils.isEmpty(languageTag)) {
                String systemLocaleStringIso639 = LocaleController.getSystemLocaleStringIso639();
                LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
                String baseLangCode = currentLocaleInfo.getBaseLangCode();
                languageTag2 = TextUtils.isEmpty(baseLangCode) ? currentLocaleInfo.getLangCode() : baseLangCode;
                if (systemLocaleStringIso639.contains(languageTag2) || languageTag2.contains(systemLocaleStringIso639)) {
                    languageTag2 = !systemLocaleStringIso639.contains(str) ? str : null;
                }
                if (TextUtils.isEmpty(languageTag2)) {
                    return new String[]{systemLocaleStringIso639.replace('_', '-')};
                }
                return new String[]{systemLocaleStringIso639.replace('_', '-'), languageTag2};
            }
            return new String[]{languageTag.replace('_', '-')};
        } catch (Exception unused) {
            return new String[]{str};
        }
    }

    public static void hideKeyboard(View view) {
        if (view != null) {
            try {
                InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService("input_method");
                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static File getCacheDir() {
        String externalStorageState;
        File externalCacheDir;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (Exception e) {
            FileLog.e(e);
            externalStorageState = null;
        }
        if (externalStorageState == null || externalStorageState.startsWith("mounted")) {
            try {
                externalCacheDir = ApplicationLoader.applicationContext.getExternalCacheDir();
                if (externalCacheDir != null) {
                    return externalCacheDir;
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        try {
            externalCacheDir = ApplicationLoader.applicationContext.getCacheDir();
            if (externalCacheDir != null) {
                return externalCacheDir;
            }
        } catch (Exception e22) {
            FileLog.e(e22);
        }
        return new File("");
    }

    public static int dp(float f) {
        return f == 0.0f ? 0 : (int) Math.ceil((double) (density * f));
    }

    public static int dpr(float f) {
        return f == 0.0f ? 0 : Math.round(density * f);
    }

    public static int dp2(float f) {
        return f == 0.0f ? 0 : (int) Math.floor((double) (density * f));
    }

    public static float dpf2(float f) {
        return f == 0.0f ? 0.0f : density * f;
    }

    public static void checkDisplaySize(Context context, Configuration configuration) {
        String str = " ";
        try {
            int ceil;
            int i = (int) density;
            density = context.getResources().getDisplayMetrics().density;
            int i2 = (int) density;
            if (firstConfigurationWas && i != i2) {
                Theme.reloadAllResources(context);
            }
            boolean z = true;
            firstConfigurationWas = true;
            if (configuration == null) {
                configuration = context.getResources().getConfiguration();
            }
            if (configuration.keyboard == 1 || configuration.hardKeyboardHidden != 1) {
                z = false;
            }
            usingHardwareInput = z;
            WindowManager windowManager = (WindowManager) context.getSystemService("window");
            if (windowManager != null) {
                Display defaultDisplay = windowManager.getDefaultDisplay();
                if (defaultDisplay != null) {
                    defaultDisplay.getMetrics(displayMetrics);
                    defaultDisplay.getSize(displaySize);
                }
            }
            if (configuration.screenWidthDp != 0) {
                ceil = (int) Math.ceil((double) (((float) configuration.screenWidthDp) * density));
                if (Math.abs(displaySize.x - ceil) > 3) {
                    displaySize.x = ceil;
                }
            }
            if (configuration.screenHeightDp != 0) {
                ceil = (int) Math.ceil((double) (((float) configuration.screenHeightDp) * density));
                if (Math.abs(displaySize.y - ceil) > 3) {
                    displaySize.y = ceil;
                }
            }
            if (roundMessageSize == 0) {
                if (isTablet()) {
                    roundMessageSize = (int) (((float) getMinTabletSide()) * 0.6f);
                } else {
                    roundMessageSize = (int) (((float) Math.min(displaySize.x, displaySize.y)) * 0.6f);
                }
            }
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("display size = ");
                stringBuilder.append(displaySize.x);
                stringBuilder.append(str);
                stringBuilder.append(displaySize.y);
                stringBuilder.append(str);
                stringBuilder.append(displayMetrics.xdpi);
                stringBuilder.append("x");
                stringBuilder.append(displayMetrics.ydpi);
                FileLog.e(stringBuilder.toString());
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static double fixLocationCoord(double d) {
        d = (double) ((long) (d * 1000000.0d));
        Double.isNaN(d);
        return d / 1000000.0d;
    }

    public static String formapMapUrl(int i, double d, double d2, int i2, int i3, boolean z, int i4) {
        int min = Math.min(2, (int) Math.ceil((double) density));
        int i5 = MessagesController.getInstance(i).mapProvider;
        if (i5 == 1 || i5 == 3) {
            String[] strArr = new String[]{"ru_RU", "tr_TR"};
            LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
            String str = null;
            for (i5 = 0; i5 < strArr.length; i5++) {
                if (strArr[i5].toLowerCase().contains(currentLocaleInfo.shortName)) {
                    str = strArr[i5];
                }
            }
            if (str == null) {
                str = "en_US";
            }
            if (z) {
                return String.format(Locale.US, "https://static-maps.yandex.ru/1.x/?ll=%.6f,%.6f&z=%d&size=%d,%d&l=map&scale=%d&pt=%.6f,%.6f,vkbkm&lang=%s", new Object[]{Double.valueOf(d2), Double.valueOf(d), Integer.valueOf(i4), Integer.valueOf(i2 * min), Integer.valueOf(i3 * min), Integer.valueOf(min), Double.valueOf(d2), Double.valueOf(d), str});
            }
            return String.format(Locale.US, "https://static-maps.yandex.ru/1.x/?ll=%.6f,%.6f&z=%d&size=%d,%d&l=map&scale=%d&lang=%s", new Object[]{Double.valueOf(d2), Double.valueOf(d), Integer.valueOf(i4), Integer.valueOf(i2 * min), Integer.valueOf(i3 * min), Integer.valueOf(min), str});
        }
        if (TextUtils.isEmpty(MessagesController.getInstance(i).mapKey)) {
            if (z) {
                return String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%.6f,%.6f&zoom=%d&size=%dx%d&maptype=roadmap&scale=%d&markers=color:red%%7Csize:mid%%7C%.6f,%.6f&sensor=false", new Object[]{Double.valueOf(d), Double.valueOf(d2), Integer.valueOf(i4), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(min), Double.valueOf(d), Double.valueOf(d2)});
            }
            return String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%.6f,%.6f&zoom=%d&size=%dx%d&maptype=roadmap&scale=%d", new Object[]{Double.valueOf(d), Double.valueOf(d2), Integer.valueOf(i4), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(min)});
        } else if (z) {
            return String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%.6f,%.6f&zoom=%d&size=%dx%d&maptype=roadmap&scale=%d&markers=color:red%%7Csize:mid%%7C%.6f,%.6f&sensor=false&key=%s", new Object[]{Double.valueOf(d), Double.valueOf(d2), Integer.valueOf(i4), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(min), Double.valueOf(d), Double.valueOf(d2), r2});
        } else {
            return String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%.6f,%.6f&zoom=%d&size=%dx%d&maptype=roadmap&scale=%d&key=%s", new Object[]{Double.valueOf(d), Double.valueOf(d2), Integer.valueOf(i4), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(min), r2});
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
            isTablet = Boolean.valueOf(ApplicationLoader.applicationContext.getResources().getBoolean(NUM));
        }
        return isTablet.booleanValue();
    }

    public static boolean isSmallTablet() {
        Point point = displaySize;
        return ((float) Math.min(point.x, point.y)) / density <= 700.0f;
    }

    public static int getMinTabletSide() {
        Point point;
        int min;
        int max;
        if (isSmallTablet()) {
            point = displaySize;
            min = Math.min(point.x, point.y);
            Point point2 = displaySize;
            max = Math.max(point2.x, point2.y);
            int i = (max * 35) / 100;
            if (i < dp(320.0f)) {
                i = dp(320.0f);
            }
            return Math.min(min, max - i);
        }
        point = displaySize;
        min = Math.min(point.x, point.y);
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
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static boolean checkPhonePattern(String str, String str2) {
        if (!(TextUtils.isEmpty(str) || str.equals("*"))) {
            String[] split = str.split("\\*");
            str2 = PhoneFormat.stripExceptNumbers(str2);
            int i = 0;
            for (CharSequence charSequence : split) {
                if (!TextUtils.isEmpty(charSequence)) {
                    i = str2.indexOf(charSequence, i);
                    if (i == -1) {
                        return false;
                    }
                    i += charSequence.length();
                }
            }
        }
        return true;
    }

    public static int getViewInset(View view) {
        if (!(view == null || VERSION.SDK_INT < 21 || view.getHeight() == displaySize.y || view.getHeight() == displaySize.y - statusBarHeight)) {
            try {
                if (mAttachInfoField == null) {
                    mAttachInfoField = View.class.getDeclaredField("mAttachInfo");
                    mAttachInfoField.setAccessible(true);
                }
                Object obj = mAttachInfoField.get(view);
                if (obj != null) {
                    if (mStableInsetsField == null) {
                        mStableInsetsField = obj.getClass().getDeclaredField("mStableInsets");
                        mStableInsetsField.setAccessible(true);
                    }
                    return ((Rect) mStableInsetsField.get(obj)).bottom;
                }
            } catch (Exception e) {
                FileLog.e(e);
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
                } catch (Exception e) {
                    point.set(windowManager.getDefaultDisplay().getWidth(), windowManager.getDefaultDisplay().getHeight());
                    FileLog.e(e);
                }
            }
        } catch (Exception e2) {
            FileLog.e(e2);
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
        if (!(charSequence == null || charSequence.length() == 0)) {
            while (charSequence.length() > 0 && (charSequence.charAt(0) == 10 || charSequence.charAt(0) == ' ')) {
                charSequence = charSequence.subSequence(1, charSequence.length());
            }
            while (charSequence.length() > 0 && (charSequence.charAt(charSequence.length() - 1) == 10 || charSequence.charAt(charSequence.length() - 1) == ' ')) {
                charSequence = charSequence.subSequence(0, charSequence.length() - 1);
            }
        }
        return charSequence;
    }

    public static void setViewPagerEdgeEffectColor(ViewPager viewPager, int i) {
        if (VERSION.SDK_INT >= 21) {
            try {
                Field declaredField = ViewPager.class.getDeclaredField("mLeftEdge");
                declaredField.setAccessible(true);
                EdgeEffect edgeEffect = (EdgeEffect) declaredField.get(viewPager);
                if (edgeEffect != null) {
                    edgeEffect.setColor(i);
                }
                declaredField = ViewPager.class.getDeclaredField("mRightEdge");
                declaredField.setAccessible(true);
                EdgeEffect edgeEffect2 = (EdgeEffect) declaredField.get(viewPager);
                if (edgeEffect2 != null) {
                    edgeEffect2.setColor(i);
                }
            } catch (Exception unused) {
            }
        }
    }

    public static void setScrollViewEdgeEffectColor(HorizontalScrollView horizontalScrollView, int i) {
        if (VERSION.SDK_INT >= 21) {
            try {
                Field declaredField = HorizontalScrollView.class.getDeclaredField("mEdgeGlowLeft");
                declaredField.setAccessible(true);
                EdgeEffect edgeEffect = (EdgeEffect) declaredField.get(horizontalScrollView);
                if (edgeEffect != null) {
                    edgeEffect.setColor(i);
                }
                declaredField = HorizontalScrollView.class.getDeclaredField("mEdgeGlowRight");
                declaredField.setAccessible(true);
                EdgeEffect edgeEffect2 = (EdgeEffect) declaredField.get(horizontalScrollView);
                if (edgeEffect2 != null) {
                    edgeEffect2.setColor(i);
                }
            } catch (Exception e) {
                FileLog.e(e);
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
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    @SuppressLint({"NewApi"})
    public static void clearDrawableAnimation(View view) {
        if (VERSION.SDK_INT >= 21 && view != null) {
            Drawable selector;
            if (view instanceof ListView) {
                selector = ((ListView) view).getSelector();
                if (selector != null) {
                    selector.setState(StateSet.NOTHING);
                    return;
                }
                return;
            }
            selector = view.getBackground();
            if (selector != null) {
                selector.setState(StateSet.NOTHING);
                selector.jumpToCurrentState();
            }
        }
    }

    public static SpannableStringBuilder replaceTags(String str) {
        return replaceTags(str, 11, new Object[0]);
    }

    public static SpannableStringBuilder replaceTags(String str, int i, Object... objArr) {
        String str2 = "<b>";
        try {
            int indexOf;
            StringBuilder stringBuilder = new StringBuilder(str);
            if ((i & 1) != 0) {
                int indexOf2;
                String str3;
                while (true) {
                    indexOf2 = stringBuilder.indexOf("<br>");
                    str3 = "\n";
                    if (indexOf2 == -1) {
                        break;
                    }
                    stringBuilder.replace(indexOf2, indexOf2 + 4, str3);
                }
                while (true) {
                    indexOf2 = stringBuilder.indexOf("<br/>");
                    if (indexOf2 == -1) {
                        break;
                    }
                    stringBuilder.replace(indexOf2, indexOf2 + 5, str3);
                }
            }
            ArrayList arrayList = new ArrayList();
            String str4 = "**";
            String str5 = "";
            if ((i & 2) != 0) {
                int indexOf3;
                while (true) {
                    indexOf3 = stringBuilder.indexOf(str2);
                    if (indexOf3 == -1) {
                        break;
                    }
                    stringBuilder.replace(indexOf3, indexOf3 + 3, str5);
                    int indexOf4 = stringBuilder.indexOf("</b>");
                    if (indexOf4 == -1) {
                        indexOf4 = stringBuilder.indexOf(str2);
                    }
                    stringBuilder.replace(indexOf4, indexOf4 + 4, str5);
                    arrayList.add(Integer.valueOf(indexOf3));
                    arrayList.add(Integer.valueOf(indexOf4));
                }
                while (true) {
                    indexOf = stringBuilder.indexOf(str4);
                    if (indexOf == -1) {
                        break;
                    }
                    stringBuilder.replace(indexOf, indexOf + 2, str5);
                    indexOf3 = stringBuilder.indexOf(str4);
                    if (indexOf3 >= 0) {
                        stringBuilder.replace(indexOf3, indexOf3 + 2, str5);
                        arrayList.add(Integer.valueOf(indexOf));
                        arrayList.add(Integer.valueOf(indexOf3));
                    }
                }
            }
            if ((i & 8) != 0) {
                while (true) {
                    i = stringBuilder.indexOf(str4);
                    if (i == -1) {
                        break;
                    }
                    stringBuilder.replace(i, i + 2, str5);
                    indexOf = stringBuilder.indexOf(str4);
                    if (indexOf >= 0) {
                        stringBuilder.replace(indexOf, indexOf + 2, str5);
                        arrayList.add(Integer.valueOf(i));
                        arrayList.add(Integer.valueOf(indexOf));
                    }
                }
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringBuilder);
            for (indexOf = 0; indexOf < arrayList.size() / 2; indexOf++) {
                int i2 = indexOf * 2;
                spannableStringBuilder.setSpan(new TypefaceSpan(getTypeface("fonts/rmedium.ttf")), ((Integer) arrayList.get(i2)).intValue(), ((Integer) arrayList.get(i2 + 1)).intValue(), 33);
            }
            return spannableStringBuilder;
        } catch (Exception e) {
            FileLog.e(e);
            return new SpannableStringBuilder(str);
        }
    }

    public static boolean needShowPasscode(boolean z) {
        boolean isWasInBackground = ForegroundDetector.getInstance().isWasInBackground(z);
        if (z) {
            ForegroundDetector.getInstance().resetBackgroundVar();
        }
        return SharedConfig.passcodeHash.length() > 0 && isWasInBackground && (SharedConfig.appLocked || (!(SharedConfig.autoLockIn == 0 || SharedConfig.lastPauseTime == 0 || SharedConfig.appLocked || SharedConfig.lastPauseTime + SharedConfig.autoLockIn > ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime()) || ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime() + 5 < SharedConfig.lastPauseTime));
    }

    public static void shakeView(final View view, final float f, final int i) {
        if (view != null) {
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
    }

    public static void checkForCrashes(Activity activity) {
        try {
            CrashManager.register(activity, BuildVars.DEBUG_VERSION ? BuildVars.HOCKEY_APP_HASH_DEBUG : BuildVars.HOCKEY_APP_HASH, new CrashManagerListener() {
                public boolean includeDeviceData() {
                    return true;
                }
            });
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public static void checkForUpdates(Activity activity) {
        boolean z = BuildVars.DEBUG_VERSION;
        if (z) {
            UpdateManager.register(activity, z ? BuildVars.HOCKEY_APP_HASH_DEBUG : BuildVars.HOCKEY_APP_HASH);
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
        } catch (Exception e) {
            FileLog.e(e);
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
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    private static File getAlbumDir(boolean z) {
        if (z || (VERSION.SDK_INT >= 23 && ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0)) {
            return FileLoader.getDirectory(4);
        }
        File file;
        if ("mounted".equals(Environment.getExternalStorageState())) {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Telegram");
            if (!(file.mkdirs() || file.exists())) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("failed to create directory");
                }
                return null;
            }
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("External storage is not mounted READ/WRITE.");
        }
        file = null;
        return file;
    }

    @SuppressLint({"NewApi"})
    public static String getPath(Uri uri) {
        try {
            if ((VERSION.SDK_INT >= 19 ? 1 : null) != null && DocumentsContract.isDocumentUri(ApplicationLoader.applicationContext, uri)) {
                String str = ":";
                String[] split;
                if (isExternalStorageDocument(uri)) {
                    split = DocumentsContract.getDocumentId(uri).split(str);
                    if ("primary".equalsIgnoreCase(split[0])) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(Environment.getExternalStorageDirectory());
                        stringBuilder.append("/");
                        stringBuilder.append(split[1]);
                        return stringBuilder.toString();
                    }
                } else if (isDownloadsDocument(uri)) {
                    return getDataColumn(ApplicationLoader.applicationContext, ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(DocumentsContract.getDocumentId(uri)).longValue()), null, null);
                } else if (isMediaDocument(uri)) {
                    Uri uri2;
                    String str2 = DocumentsContract.getDocumentId(uri).split(str)[0];
                    int i = -1;
                    int hashCode = str2.hashCode();
                    if (hashCode != 93166550) {
                        if (hashCode != NUM) {
                            if (hashCode == NUM) {
                                if (str2.equals("video")) {
                                    i = 1;
                                }
                            }
                        } else if (str2.equals("image")) {
                            i = 0;
                        }
                    } else if (str2.equals("audio")) {
                        i = 2;
                    }
                    if (i == 0) {
                        uri2 = Media.EXTERNAL_CONTENT_URI;
                    } else if (i == 1) {
                        uri2 = Video.Media.EXTERNAL_CONTENT_URI;
                    } else if (i != 2) {
                        uri2 = null;
                    } else {
                        uri2 = Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    return getDataColumn(ApplicationLoader.applicationContext, uri2, "_id=?", new String[]{split[1]});
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
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static String getDataColumn(Context context, Uri uri, String str, String[] strArr) {
        Cursor query;
        Throwable th;
        String[] strArr2 = new String[1];
        String str2 = "_data";
        strArr2[0] = str2;
        try {
            query = context.getContentResolver().query(uri, strArr2, str, strArr, null);
            if (query != null) {
                try {
                    if (query.moveToFirst()) {
                        String string = query.getString(query.getColumnIndexOrThrow(str2));
                        if (string.startsWith("content://") || !(string.startsWith("/") || string.startsWith("file://"))) {
                            if (query != null) {
                                query.close();
                            }
                            return null;
                        }
                        if (query != null) {
                            query.close();
                        }
                        return string;
                    }
                } catch (Throwable th2) {
                    Throwable th3 = th2;
                    str = th;
                    th = th3;
                }
            }
            if (query != null) {
                query.close();
            }
        } catch (Exception unused) {
        }
        return null;
        if (query != null) {
            if (str != null) {
                try {
                    query.close();
                } catch (Throwable unused2) {
                }
            } else {
                query.close();
            }
        }
        throw th;
        throw th;
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
        return generatePicturePath(false);
    }

    public static File generatePicturePath(boolean z) {
        try {
            File albumDir = getAlbumDir(z);
            Date date = new Date();
            date.setTime((System.currentTimeMillis() + ((long) Utilities.random.nextInt(1000))) + 1);
            String format = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(date);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("IMG_");
            stringBuilder.append(format);
            stringBuilder.append(".jpg");
            return new File(albumDir, stringBuilder.toString());
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static CharSequence generateSearchName(String str, String str2, String str3) {
        if (str == null && str2 == null) {
            return "";
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        String str4 = " ";
        if (str == null || str.length() == 0) {
            str = str2;
        } else if (!(str2 == null || str2.length() == 0)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(str4);
            stringBuilder.append(str2);
            str = stringBuilder.toString();
        }
        str = str.trim();
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(str4);
        stringBuilder2.append(str.toLowerCase());
        str2 = stringBuilder2.toString();
        int i = 0;
        while (true) {
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(str4);
            stringBuilder3.append(str3);
            int indexOf = str2.indexOf(stringBuilder3.toString(), i);
            if (indexOf == -1) {
                break;
            }
            int i2 = 1;
            int i3 = indexOf - (indexOf == 0 ? 0 : 1);
            int length = str3.length();
            if (indexOf == 0) {
                i2 = 0;
            }
            indexOf = (length + i2) + i3;
            if (i != 0 && i != i3 + 1) {
                spannableStringBuilder.append(str.substring(i, i3));
            } else if (i == 0 && i3 != 0) {
                spannableStringBuilder.append(str.substring(0, i3));
            }
            String substring = str.substring(i3, Math.min(str.length(), indexOf));
            if (substring.startsWith(str4)) {
                spannableStringBuilder.append(str4);
            }
            substring = substring.trim();
            i2 = spannableStringBuilder.length();
            spannableStringBuilder.append(substring);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteBlueText4")), i2, substring.length() + i2, 33);
            i = indexOf;
        }
        if (i != -1 && i < str.length()) {
            spannableStringBuilder.append(str.substring(i));
        }
        return spannableStringBuilder;
    }

    public static boolean isAirplaneModeOn() {
        boolean z = true;
        String str = "airplane_mode_on";
        if (VERSION.SDK_INT < 17) {
            if (System.getInt(ApplicationLoader.applicationContext.getContentResolver(), str, 0) == 0) {
                z = false;
            }
            return z;
        }
        if (Global.getInt(ApplicationLoader.applicationContext.getContentResolver(), str, 0) == 0) {
            z = false;
        }
        return z;
    }

    public static File generateVideoPath() {
        return generateVideoPath(false);
    }

    public static File generateVideoPath(boolean z) {
        try {
            File albumDir = getAlbumDir(z);
            Date date = new Date();
            date.setTime((System.currentTimeMillis() + ((long) Utilities.random.nextInt(1000))) + 1);
            String format = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(date);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("VID_");
            stringBuilder.append(format);
            stringBuilder.append(".mp4");
            return new File(albumDir, stringBuilder.toString());
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static String formatFileSize(long j) {
        return formatFileSize(j, false);
    }

    public static String formatFileSize(long j, boolean z) {
        float f;
        if (j < 1024) {
            return String.format("%d B", new Object[]{Long.valueOf(j)});
        } else if (j < 1048576) {
            f = ((float) j) / 1024.0f;
            if (z) {
                if ((f - ((float) ((int) f))) * 10.0f == 0.0f) {
                    return String.format("%d KB", new Object[]{Integer.valueOf((int) f)});
                }
            }
            return String.format("%.1f KB", new Object[]{Float.valueOf(f)});
        } else if (j < NUM) {
            f = (((float) j) / 1024.0f) / 1024.0f;
            if (z) {
                if ((f - ((float) ((int) f))) * 10.0f == 0.0f) {
                    return String.format("%d MB", new Object[]{Integer.valueOf((int) f)});
                }
            }
            return String.format("%.1f MB", new Object[]{Float.valueOf(f)});
        } else {
            f = ((((float) j) / 1024.0f) / 1024.0f) / 1024.0f;
            if (z) {
                if ((f - ((float) ((int) f))) * 10.0f == 0.0f) {
                    return String.format("%d GB", new Object[]{Integer.valueOf((int) f)});
                }
            }
            return String.format("%.1f GB", new Object[]{Float.valueOf(f)});
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
                } catch (Exception e) {
                    FileLog.e(e);
                    return null;
                }
            }
            byteArrayOutputStream.write(b);
            i++;
        }
        bArr = byteArrayOutputStream.toByteArray();
        try {
            byteArrayOutputStream.close();
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        return bArr;
    }

    public static boolean copyFile(InputStream inputStream, File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte[] bArr = new byte[4096];
        while (true) {
            int read = inputStream.read(bArr);
            if (read > 0) {
                Thread.yield();
                fileOutputStream.write(bArr, 0, read);
            } else {
                fileOutputStream.close();
                return true;
            }
        }
    }

    public static boolean copyFile(File file, File file2) throws IOException {
        Throwable th;
        Throwable th2;
        if (!file2.exists()) {
            file2.createNewFile();
        }
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            try {
                fileOutputStream.getChannel().transferFrom(fileInputStream.getChannel(), 0, fileInputStream.getChannel().size());
                fileOutputStream.close();
                fileInputStream.close();
                return true;
            } catch (Throwable th22) {
                Throwable th3 = th22;
                th22 = th;
                th = th3;
            }
            throw th;
            if (th22 != null) {
                try {
                    fileOutputStream.close();
                } catch (Throwable unused) {
                }
            } else {
                fileOutputStream.close();
            }
            throw th;
        } catch (Exception th4) {
            FileLog.e(th4);
            return false;
        } catch (Throwable unused2) {
        }
    }

    public static byte[] calcAuthKeyHash(byte[] bArr) {
        byte[] bArr2 = new byte[16];
        System.arraycopy(Utilities.computeSHA1(bArr), 0, bArr2, 0, 16);
        return bArr2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:53:0x00f1 A:{Catch:{ Exception -> 0x0120 }} */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00e4 A:{SYNTHETIC, Splitter:B:47:0x00e4} */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x011c A:{Catch:{ Exception -> 0x0120 }} */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0101 A:{SYNTHETIC, Splitter:B:60:0x0101} */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0111 A:{Catch:{ Exception -> 0x0120 }} */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0109 A:{Catch:{ Exception -> 0x0120 }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:62:0x0105 */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    public static void openDocument(org.telegram.messenger.MessageObject r16, android.app.Activity r17, org.telegram.ui.ActionBar.BaseFragment r18) {
        /*
        r0 = r16;
        r1 = r17;
        r2 = r18;
        if (r0 != 0) goto L_0x0009;
    L_0x0008:
        return;
    L_0x0009:
        r3 = r16.getDocument();
        if (r3 != 0) goto L_0x0010;
    L_0x000f:
        return;
    L_0x0010:
        r4 = r0.messageOwner;
        r4 = r4.media;
        if (r4 == 0) goto L_0x001b;
    L_0x0016:
        r4 = org.telegram.messenger.FileLoader.getAttachFileName(r3);
        goto L_0x001d;
    L_0x001b:
        r4 = "";
    L_0x001d:
        r5 = r0.messageOwner;
        r5 = r5.attachPath;
        r6 = 0;
        if (r5 == 0) goto L_0x0034;
    L_0x0024:
        r5 = r5.length();
        if (r5 == 0) goto L_0x0034;
    L_0x002a:
        r5 = new java.io.File;
        r7 = r0.messageOwner;
        r7 = r7.attachPath;
        r5.<init>(r7);
        goto L_0x0035;
    L_0x0034:
        r5 = r6;
    L_0x0035:
        if (r5 == 0) goto L_0x003f;
    L_0x0037:
        if (r5 == 0) goto L_0x0045;
    L_0x0039:
        r7 = r5.exists();
        if (r7 != 0) goto L_0x0045;
    L_0x003f:
        r5 = r0.messageOwner;
        r5 = org.telegram.messenger.FileLoader.getPathToMessage(r5);
    L_0x0045:
        if (r5 == 0) goto L_0x015c;
    L_0x0047:
        r7 = r5.exists();
        if (r7 == 0) goto L_0x015c;
    L_0x004d:
        r7 = NUM; // 0x7f0d06d1 float:1.8745654E38 double:1.0531306397E-314;
        r8 = "OK";
        r9 = NUM; // 0x7f0d00ef float:1.87426E38 double:1.0531298956E-314;
        r10 = "AppName";
        r11 = 1;
        if (r2 == 0) goto L_0x00a6;
    L_0x005a:
        r12 = r5.getName();
        r12 = r12.toLowerCase();
        r13 = "attheme";
        r12 = r12.endsWith(r13);
        if (r12 == 0) goto L_0x00a6;
    L_0x006a:
        r0 = r16.getDocumentName();
        r0 = org.telegram.ui.ActionBar.Theme.applyThemeFile(r5, r0, r11);
        if (r0 == 0) goto L_0x007e;
    L_0x0074:
        r1 = new org.telegram.ui.ThemePreviewActivity;
        r1.<init>(r5, r0);
        r2.presentFragment(r1);
        goto L_0x015c;
    L_0x007e:
        r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r0.<init>(r1);
        r1 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r0.setTitle(r1);
        r1 = NUM; // 0x7f0d0512 float:1.8744747E38 double:1.053130419E-314;
        r3 = "IncorrectTheme";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r0.setMessage(r1);
        r1 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r0.setPositiveButton(r1, r6);
        r0 = r0.create();
        r2.showDialog(r0);
        goto L_0x015c;
    L_0x00a6:
        r12 = new android.content.Intent;	 Catch:{ Exception -> 0x0120 }
        r13 = "android.intent.action.VIEW";
        r12.<init>(r13);	 Catch:{ Exception -> 0x0120 }
        r12.setFlags(r11);	 Catch:{ Exception -> 0x0120 }
        r13 = android.webkit.MimeTypeMap.getSingleton();	 Catch:{ Exception -> 0x0120 }
        r14 = 46;
        r14 = r4.lastIndexOf(r14);	 Catch:{ Exception -> 0x0120 }
        r15 = -1;
        if (r14 == r15) goto L_0x00d9;
    L_0x00bd:
        r14 = r14 + r11;
        r4 = r4.substring(r14);	 Catch:{ Exception -> 0x0120 }
        r4 = r4.toLowerCase();	 Catch:{ Exception -> 0x0120 }
        r4 = r13.getMimeTypeFromExtension(r4);	 Catch:{ Exception -> 0x0120 }
        if (r4 != 0) goto L_0x00d7;
    L_0x00cc:
        r3 = r3.mime_type;	 Catch:{ Exception -> 0x0120 }
        if (r3 == 0) goto L_0x00d9;
    L_0x00d0:
        r4 = r3.length();	 Catch:{ Exception -> 0x0120 }
        if (r4 != 0) goto L_0x00da;
    L_0x00d6:
        goto L_0x00d9;
    L_0x00d7:
        r3 = r4;
        goto L_0x00da;
    L_0x00d9:
        r3 = r6;
    L_0x00da:
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0120 }
        r13 = "org.telegram.messenger.beta.provider";
        r14 = 24;
        r15 = "text/plain";
        if (r4 < r14) goto L_0x00f1;
    L_0x00e4:
        r4 = androidx.core.content.FileProvider.getUriForFile(r1, r13, r5);	 Catch:{ Exception -> 0x0120 }
        if (r3 == 0) goto L_0x00ec;
    L_0x00ea:
        r11 = r3;
        goto L_0x00ed;
    L_0x00ec:
        r11 = r15;
    L_0x00ed:
        r12.setDataAndType(r4, r11);	 Catch:{ Exception -> 0x0120 }
        goto L_0x00fd;
    L_0x00f1:
        r4 = android.net.Uri.fromFile(r5);	 Catch:{ Exception -> 0x0120 }
        if (r3 == 0) goto L_0x00f9;
    L_0x00f7:
        r11 = r3;
        goto L_0x00fa;
    L_0x00f9:
        r11 = r15;
    L_0x00fa:
        r12.setDataAndType(r4, r11);	 Catch:{ Exception -> 0x0120 }
    L_0x00fd:
        r4 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        if (r3 == 0) goto L_0x011c;
    L_0x0101:
        r1.startActivityForResult(r12, r4);	 Catch:{ Exception -> 0x0105 }
        goto L_0x015c;
    L_0x0105:
        r3 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0120 }
        if (r3 < r14) goto L_0x0111;
    L_0x0109:
        r3 = androidx.core.content.FileProvider.getUriForFile(r1, r13, r5);	 Catch:{ Exception -> 0x0120 }
        r12.setDataAndType(r3, r15);	 Catch:{ Exception -> 0x0120 }
        goto L_0x0118;
    L_0x0111:
        r3 = android.net.Uri.fromFile(r5);	 Catch:{ Exception -> 0x0120 }
        r12.setDataAndType(r3, r15);	 Catch:{ Exception -> 0x0120 }
    L_0x0118:
        r1.startActivityForResult(r12, r4);	 Catch:{ Exception -> 0x0120 }
        goto L_0x015c;
    L_0x011c:
        r1.startActivityForResult(r12, r4);	 Catch:{ Exception -> 0x0120 }
        goto L_0x015c;
        if (r1 != 0) goto L_0x0124;
    L_0x0123:
        return;
    L_0x0124:
        r3 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r3.<init>(r1);
        r1 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r3.setTitle(r1);
        r1 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r3.setPositiveButton(r1, r6);
        r1 = NUM; // 0x7f0d0622 float:1.87453E38 double:1.053130553E-314;
        r4 = 1;
        r4 = new java.lang.Object[r4];
        r5 = 0;
        r0 = r16.getDocument();
        r0 = r0.mime_type;
        r4[r5] = r0;
        r0 = "NoHandleAppInstalled";
        r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r4);
        r3.setMessage(r0);
        if (r2 == 0) goto L_0x0159;
    L_0x0151:
        r0 = r3.create();
        r2.showDialog(r0);
        goto L_0x015c;
    L_0x0159:
        r3.show();
    L_0x015c:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.openDocument(org.telegram.messenger.MessageObject, android.app.Activity, org.telegram.ui.ActionBar.BaseFragment):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:43:0x00e7  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00da  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0112  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00f7 A:{SYNTHETIC, Splitter:B:50:0x00f7} */
    /* JADX WARNING: Missing block: B:24:0x0070, code skipped:
            if (r8.length() != 0) goto L_0x0076;
     */
    public static void openForView(org.telegram.messenger.MessageObject r8, android.app.Activity r9) {
        /*
        r0 = r8.getFileName();
        r1 = r8.messageOwner;
        r1 = r1.attachPath;
        r2 = 0;
        if (r1 == 0) goto L_0x001b;
    L_0x000b:
        r1 = r1.length();
        if (r1 == 0) goto L_0x001b;
    L_0x0011:
        r1 = new java.io.File;
        r3 = r8.messageOwner;
        r3 = r3.attachPath;
        r1.<init>(r3);
        goto L_0x001c;
    L_0x001b:
        r1 = r2;
    L_0x001c:
        if (r1 == 0) goto L_0x0024;
    L_0x001e:
        r3 = r1.exists();
        if (r3 != 0) goto L_0x002a;
    L_0x0024:
        r1 = r8.messageOwner;
        r1 = org.telegram.messenger.FileLoader.getPathToMessage(r1);
    L_0x002a:
        if (r1 == 0) goto L_0x0115;
    L_0x002c:
        r3 = r1.exists();
        if (r3 == 0) goto L_0x0115;
    L_0x0032:
        r3 = new android.content.Intent;
        r4 = "android.intent.action.VIEW";
        r3.<init>(r4);
        r4 = 1;
        r3.setFlags(r4);
        r5 = android.webkit.MimeTypeMap.getSingleton();
        r6 = 46;
        r6 = r0.lastIndexOf(r6);
        r7 = -1;
        if (r6 == r7) goto L_0x0075;
    L_0x004a:
        r6 = r6 + r4;
        r0 = r0.substring(r6);
        r0 = r0.toLowerCase();
        r0 = r5.getMimeTypeFromExtension(r0);
        if (r0 != 0) goto L_0x0073;
    L_0x0059:
        r4 = r8.type;
        r5 = 9;
        if (r4 == r5) goto L_0x0064;
    L_0x005f:
        if (r4 != 0) goto L_0x0062;
    L_0x0061:
        goto L_0x0064;
    L_0x0062:
        r8 = r0;
        goto L_0x006a;
    L_0x0064:
        r8 = r8.getDocument();
        r8 = r8.mime_type;
    L_0x006a:
        if (r8 == 0) goto L_0x0075;
    L_0x006c:
        r0 = r8.length();
        if (r0 != 0) goto L_0x0076;
    L_0x0072:
        goto L_0x0075;
    L_0x0073:
        r8 = r0;
        goto L_0x0076;
    L_0x0075:
        r8 = r2;
    L_0x0076:
        r0 = android.os.Build.VERSION.SDK_INT;
        r4 = 26;
        if (r0 < r4) goto L_0x00d0;
    L_0x007c:
        if (r8 == 0) goto L_0x00d0;
    L_0x007e:
        r0 = "application/vnd.android.package-archive";
        r0 = r8.equals(r0);
        if (r0 == 0) goto L_0x00d0;
    L_0x0086:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r0 = r0.getPackageManager();
        r0 = r0.canRequestPackageInstalls();
        if (r0 != 0) goto L_0x00d0;
    L_0x0092:
        r8 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r8.<init>(r9);
        r0 = NUM; // 0x7f0d00ef float:1.87426E38 double:1.0531298956E-314;
        r1 = "AppName";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        r8.setTitle(r0);
        r0 = NUM; // 0x7f0d00ee float:1.8742597E38 double:1.053129895E-314;
        r1 = "ApkRestricted";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        r8.setMessage(r0);
        r0 = NUM; // 0x7f0d0819 float:1.874632E38 double:1.0531308017E-314;
        r1 = "PermissionOpenSettings";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        r1 = new org.telegram.messenger.-$$Lambda$AndroidUtilities$WTDNtmPdy5yTCpHQSYXokc0QdfY;
        r1.<init>(r9);
        r8.setPositiveButton(r0, r1);
        r9 = NUM; // 0x7f0d01f6 float:1.8743133E38 double:1.0531300256E-314;
        r0 = "Cancel";
        r9 = org.telegram.messenger.LocaleController.getString(r0, r9);
        r8.setNegativeButton(r9, r2);
        r8.show();
        return;
    L_0x00d0:
        r0 = android.os.Build.VERSION.SDK_INT;
        r2 = "org.telegram.messenger.beta.provider";
        r4 = 24;
        r5 = "text/plain";
        if (r0 < r4) goto L_0x00e7;
    L_0x00da:
        r0 = androidx.core.content.FileProvider.getUriForFile(r9, r2, r1);
        if (r8 == 0) goto L_0x00e2;
    L_0x00e0:
        r6 = r8;
        goto L_0x00e3;
    L_0x00e2:
        r6 = r5;
    L_0x00e3:
        r3.setDataAndType(r0, r6);
        goto L_0x00f3;
    L_0x00e7:
        r0 = android.net.Uri.fromFile(r1);
        if (r8 == 0) goto L_0x00ef;
    L_0x00ed:
        r6 = r8;
        goto L_0x00f0;
    L_0x00ef:
        r6 = r5;
    L_0x00f0:
        r3.setDataAndType(r0, r6);
    L_0x00f3:
        r0 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        if (r8 == 0) goto L_0x0112;
    L_0x00f7:
        r9.startActivityForResult(r3, r0);	 Catch:{ Exception -> 0x00fb }
        goto L_0x0115;
    L_0x00fb:
        r8 = android.os.Build.VERSION.SDK_INT;
        if (r8 < r4) goto L_0x0107;
    L_0x00ff:
        r8 = androidx.core.content.FileProvider.getUriForFile(r9, r2, r1);
        r3.setDataAndType(r8, r5);
        goto L_0x010e;
    L_0x0107:
        r8 = android.net.Uri.fromFile(r1);
        r3.setDataAndType(r8, r5);
    L_0x010e:
        r9.startActivityForResult(r3, r0);
        goto L_0x0115;
    L_0x0112:
        r9.startActivityForResult(r3, r0);
    L_0x0115:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.openForView(org.telegram.messenger.MessageObject, android.app.Activity):void");
    }

    static /* synthetic */ void lambda$openForView$2(Activity activity, DialogInterface dialogInterface, int i) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("package:");
            stringBuilder.append(activity.getPackageName());
            activity.startActivity(new Intent("android.settings.MANAGE_UNKNOWN_APP_SOURCES", Uri.parse(stringBuilder.toString())));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void openForView(TLObject tLObject, Activity activity) {
        if (tLObject != null && activity != null) {
            String attachFileName = FileLoader.getAttachFileName(tLObject);
            File pathToAttach = FileLoader.getPathToAttach(tLObject, true);
            if (pathToAttach != null && pathToAttach.exists()) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setFlags(1);
                MimeTypeMap singleton = MimeTypeMap.getSingleton();
                int lastIndexOf = attachFileName.lastIndexOf(46);
                String str = null;
                if (lastIndexOf != -1) {
                    attachFileName = singleton.getMimeTypeFromExtension(attachFileName.substring(lastIndexOf + 1).toLowerCase());
                    if (attachFileName == null) {
                        String str2 = tLObject instanceof TL_document ? ((TL_document) tLObject).mime_type : attachFileName;
                        if (!(str2 == null || str2.length() == 0)) {
                            str = str2;
                        }
                    } else {
                        str = attachFileName;
                    }
                }
                attachFileName = "org.telegram.messenger.beta.provider";
                String str3 = "text/plain";
                if (VERSION.SDK_INT >= 24) {
                    intent.setDataAndType(FileProvider.getUriForFile(activity, attachFileName, pathToAttach), str != null ? str : str3);
                } else {
                    intent.setDataAndType(Uri.fromFile(pathToAttach), str != null ? str : str3);
                }
                if (str != null) {
                    try {
                        activity.startActivityForResult(intent, 500);
                        return;
                    } catch (Exception unused) {
                        if (VERSION.SDK_INT >= 24) {
                            intent.setDataAndType(FileProvider.getUriForFile(activity, attachFileName, pathToAttach), str3);
                        } else {
                            intent.setDataAndType(Uri.fromFile(pathToAttach), str3);
                        }
                        activity.startActivityForResult(intent, 500);
                        return;
                    }
                }
                activity.startActivityForResult(intent, 500);
            }
        }
    }

    public static boolean isBannedForever(TL_chatBannedRights tL_chatBannedRights) {
        return tL_chatBannedRights == null || Math.abs(((long) tL_chatBannedRights.until_date) - (System.currentTimeMillis() / 1000)) > NUM;
    }

    public static void setRectToRect(Matrix matrix, RectF rectF, RectF rectF2, int i, boolean z) {
        float height;
        float width;
        float height2;
        Object obj;
        float f;
        float f2;
        float width2;
        float width3;
        if (i == 90 || i == 270) {
            height = rectF2.height() / rectF.width();
            width = rectF2.width();
            height2 = rectF.height();
        } else {
            height = rectF2.width() / rectF.width();
            width = rectF2.height();
            height2 = rectF.height();
        }
        width /= height2;
        if (height < width) {
            height = width;
            obj = 1;
        } else {
            obj = null;
        }
        if (z) {
            matrix.setTranslate(rectF2.left, rectF2.top);
        }
        if (i == 90) {
            matrix.preRotate(90.0f);
            matrix.preTranslate(0.0f, -rectF2.width());
        } else if (i == 180) {
            matrix.preRotate(180.0f);
            matrix.preTranslate(-rectF2.width(), -rectF2.height());
        } else if (i == 270) {
            matrix.preRotate(270.0f);
            matrix.preTranslate(-rectF2.height(), 0.0f);
        }
        if (z) {
            f = (-rectF.left) * height;
            f2 = (-rectF.top) * height;
        } else {
            f = rectF2.left - (rectF.left * height);
            f2 = rectF2.top - (rectF.top * height);
        }
        if (obj != null) {
            width2 = rectF2.width();
            width3 = rectF.width();
        } else {
            width2 = rectF2.height();
            width3 = rectF.height();
        }
        width2 = (width2 - (width3 * height)) / 2.0f;
        if (obj != null) {
            f += width2;
        } else {
            f2 += width2;
        }
        matrix.preScale(height, height);
        if (z) {
            matrix.preTranslate(f, f2);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:54:0x00fe  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00fc  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0103  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0101  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0108  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0106  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00fc  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00fe  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0101  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0103  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0106  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0108  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00fe  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00fc  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0103  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0101  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0108  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0106  */
    /* JADX WARNING: Missing block: B:29:0x0062, code skipped:
            if (r15.startsWith(r5) == false) goto L_0x00e8;
     */
    public static boolean handleProxyIntent(android.app.Activity r14, android.content.Intent r15) {
        /*
        r0 = "tg:proxy";
        r1 = "tg://telegram.org";
        r2 = 0;
        if (r15 != 0) goto L_0x0008;
    L_0x0007:
        return r2;
    L_0x0008:
        r3 = r15.getFlags();	 Catch:{ Exception -> 0x010f }
        r4 = 1048576; // 0x100000 float:1.469368E-39 double:5.180654E-318;
        r3 = r3 & r4;
        if (r3 == 0) goto L_0x0012;
    L_0x0011:
        return r2;
    L_0x0012:
        r15 = r15.getData();	 Catch:{ Exception -> 0x010f }
        if (r15 == 0) goto L_0x010f;
    L_0x0018:
        r3 = r15.getScheme();	 Catch:{ Exception -> 0x010f }
        r4 = 0;
        if (r3 == 0) goto L_0x00e8;
    L_0x001f:
        r5 = "http";
        r5 = r3.equals(r5);	 Catch:{ Exception -> 0x010f }
        r6 = "secret";
        r7 = "pass";
        r8 = "user";
        r9 = "port";
        r10 = "server";
        if (r5 != 0) goto L_0x0091;
    L_0x0031:
        r5 = "https";
        r5 = r3.equals(r5);	 Catch:{ Exception -> 0x010f }
        if (r5 == 0) goto L_0x003a;
    L_0x0039:
        goto L_0x0091;
    L_0x003a:
        r5 = "tg";
        r3 = r3.equals(r5);	 Catch:{ Exception -> 0x010f }
        if (r3 == 0) goto L_0x00e8;
    L_0x0042:
        r15 = r15.toString();	 Catch:{ Exception -> 0x010f }
        r3 = r15.startsWith(r0);	 Catch:{ Exception -> 0x010f }
        r5 = "tg://socks";
        r11 = "tg:socks";
        r12 = "tg://proxy";
        if (r3 != 0) goto L_0x0064;
    L_0x0052:
        r3 = r15.startsWith(r12);	 Catch:{ Exception -> 0x010f }
        if (r3 != 0) goto L_0x0064;
    L_0x0058:
        r3 = r15.startsWith(r11);	 Catch:{ Exception -> 0x010f }
        if (r3 != 0) goto L_0x0064;
    L_0x005e:
        r3 = r15.startsWith(r5);	 Catch:{ Exception -> 0x010f }
        if (r3 == 0) goto L_0x00e8;
    L_0x0064:
        r15 = r15.replace(r0, r1);	 Catch:{ Exception -> 0x010f }
        r15 = r15.replace(r12, r1);	 Catch:{ Exception -> 0x010f }
        r15 = r15.replace(r5, r1);	 Catch:{ Exception -> 0x010f }
        r15 = r15.replace(r11, r1);	 Catch:{ Exception -> 0x010f }
        r15 = android.net.Uri.parse(r15);	 Catch:{ Exception -> 0x010f }
        r4 = r15.getQueryParameter(r10);	 Catch:{ Exception -> 0x010f }
        r0 = r15.getQueryParameter(r9);	 Catch:{ Exception -> 0x010f }
        r1 = r15.getQueryParameter(r8);	 Catch:{ Exception -> 0x010f }
        r3 = r15.getQueryParameter(r7);	 Catch:{ Exception -> 0x010f }
        r15 = r15.getQueryParameter(r6);	 Catch:{ Exception -> 0x010f }
        r7 = r0;
        r6 = r4;
        r4 = r1;
        goto L_0x00ec;
    L_0x0091:
        r0 = r15.getHost();	 Catch:{ Exception -> 0x010f }
        r0 = r0.toLowerCase();	 Catch:{ Exception -> 0x010f }
        r1 = "telegram.me";
        r1 = r0.equals(r1);	 Catch:{ Exception -> 0x010f }
        if (r1 != 0) goto L_0x00b1;
    L_0x00a1:
        r1 = "t.me";
        r1 = r0.equals(r1);	 Catch:{ Exception -> 0x010f }
        if (r1 != 0) goto L_0x00b1;
    L_0x00a9:
        r1 = "telegram.dog";
        r0 = r0.equals(r1);	 Catch:{ Exception -> 0x010f }
        if (r0 == 0) goto L_0x00e0;
    L_0x00b1:
        r0 = r15.getPath();	 Catch:{ Exception -> 0x010f }
        if (r0 == 0) goto L_0x00e0;
    L_0x00b7:
        r1 = "/socks";
        r1 = r0.startsWith(r1);	 Catch:{ Exception -> 0x010f }
        if (r1 != 0) goto L_0x00c7;
    L_0x00bf:
        r1 = "/proxy";
        r0 = r0.startsWith(r1);	 Catch:{ Exception -> 0x010f }
        if (r0 == 0) goto L_0x00e0;
    L_0x00c7:
        r4 = r15.getQueryParameter(r10);	 Catch:{ Exception -> 0x010f }
        r0 = r15.getQueryParameter(r9);	 Catch:{ Exception -> 0x010f }
        r1 = r15.getQueryParameter(r8);	 Catch:{ Exception -> 0x010f }
        r3 = r15.getQueryParameter(r7);	 Catch:{ Exception -> 0x010f }
        r15 = r15.getQueryParameter(r6);	 Catch:{ Exception -> 0x010f }
        r13 = r1;
        r1 = r15;
        r15 = r4;
        r4 = r13;
        goto L_0x00e4;
    L_0x00e0:
        r15 = r4;
        r0 = r15;
        r1 = r0;
        r3 = r1;
    L_0x00e4:
        r6 = r15;
        r7 = r0;
        r15 = r1;
        goto L_0x00ec;
    L_0x00e8:
        r15 = r4;
        r3 = r15;
        r6 = r3;
        r7 = r6;
    L_0x00ec:
        r0 = android.text.TextUtils.isEmpty(r6);	 Catch:{ Exception -> 0x010f }
        if (r0 != 0) goto L_0x010f;
    L_0x00f2:
        r0 = android.text.TextUtils.isEmpty(r7);	 Catch:{ Exception -> 0x010f }
        if (r0 != 0) goto L_0x010f;
    L_0x00f8:
        r0 = "";
        if (r4 != 0) goto L_0x00fe;
    L_0x00fc:
        r8 = r0;
        goto L_0x00ff;
    L_0x00fe:
        r8 = r4;
    L_0x00ff:
        if (r3 != 0) goto L_0x0103;
    L_0x0101:
        r9 = r0;
        goto L_0x0104;
    L_0x0103:
        r9 = r3;
    L_0x0104:
        if (r15 != 0) goto L_0x0108;
    L_0x0106:
        r10 = r0;
        goto L_0x0109;
    L_0x0108:
        r10 = r15;
    L_0x0109:
        r5 = r14;
        showProxyAlert(r5, r6, r7, r8, r9, r10);	 Catch:{ Exception -> 0x010f }
        r14 = 1;
        return r14;
    L_0x010f:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.handleProxyIntent(android.app.Activity, android.content.Intent):boolean");
    }

    public static boolean shouldEnableAnimation() {
        int i = VERSION.SDK_INT;
        if (i < 26 || i >= 28 || (!((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).isPowerSaveMode() && Global.getFloat(ApplicationLoader.applicationContext.getContentResolver(), "animator_duration_scale", 1.0f) > 0.0f)) {
            return true;
        }
        return false;
    }

    public static void showProxyAlert(Activity activity, String str, String str2, String str3, String str4, String str5) {
        String str6;
        Context context = activity;
        BottomSheet.Builder builder = new BottomSheet.Builder(context);
        Runnable dismissRunnable = builder.getDismissRunnable();
        builder.setApplyTopPadding(false);
        builder.setApplyBottomPadding(false);
        LinearLayout linearLayout = new LinearLayout(context);
        builder.setCustomView(linearLayout);
        linearLayout.setOrientation(1);
        if (!TextUtils.isEmpty(str5)) {
            TextView textView = new TextView(context);
            textView.setText(LocaleController.getString("UseProxyTelegramInfo2", NUM));
            textView.setTextColor(Theme.getColor("dialogTextGray4"));
            textView.setTextSize(1, 14.0f);
            textView.setGravity(49);
            linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 17, 8, 17, 8));
            View view = new View(context);
            view.setBackgroundColor(Theme.getColor("divider"));
            linearLayout.addView(view, new LayoutParams(-1, 1));
        }
        for (int i = 0; i < 5; i++) {
            CharSequence string;
            CharSequence charSequence = null;
            if (i == 0) {
                str6 = str2;
                string = LocaleController.getString("UseProxyAddress", NUM);
                charSequence = str;
            } else if (i == 1) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("");
                stringBuilder.append(str2);
                charSequence = stringBuilder.toString();
                string = LocaleController.getString("UseProxyPort", NUM);
            } else {
                str6 = str2;
                if (i == 2) {
                    string = LocaleController.getString("UseProxySecret", NUM);
                    charSequence = str5;
                } else if (i == 3) {
                    string = LocaleController.getString("UseProxyUsername", NUM);
                    charSequence = str3;
                } else if (i == 4) {
                    string = LocaleController.getString("UseProxyPassword", NUM);
                    charSequence = str4;
                } else {
                    string = null;
                }
            }
            if (!TextUtils.isEmpty(charSequence)) {
                TextDetailSettingsCell textDetailSettingsCell = new TextDetailSettingsCell(context);
                textDetailSettingsCell.setTextAndValue(charSequence, string, true);
                textDetailSettingsCell.getTextView().setTextColor(Theme.getColor("dialogTextBlack"));
                textDetailSettingsCell.getValueTextView().setTextColor(Theme.getColor("dialogTextGray3"));
                linearLayout.addView(textDetailSettingsCell, LayoutHelper.createLinear(-1, -2));
                if (i == 2) {
                    break;
                }
            }
        }
        str6 = str2;
        PickerBottomLayout pickerBottomLayout = new PickerBottomLayout(context, false);
        pickerBottomLayout.setBackgroundColor(Theme.getColor("dialogBackground"));
        linearLayout.addView(pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        pickerBottomLayout.cancelButton.setPadding(dp(18.0f), 0, dp(18.0f), 0);
        String str7 = "dialogTextBlue2";
        pickerBottomLayout.cancelButton.setTextColor(Theme.getColor(str7));
        pickerBottomLayout.cancelButton.setText(LocaleController.getString("Cancel", NUM).toUpperCase());
        pickerBottomLayout.cancelButton.setOnClickListener(new -$$Lambda$AndroidUtilities$jUNDa_ygB19u08pi8wOlTDRHeBo(dismissRunnable));
        pickerBottomLayout.doneButtonTextView.setTextColor(Theme.getColor(str7));
        pickerBottomLayout.doneButton.setPadding(dp(18.0f), 0, dp(18.0f), 0);
        pickerBottomLayout.doneButtonBadgeTextView.setVisibility(8);
        pickerBottomLayout.doneButtonTextView.setText(LocaleController.getString("ConnectingConnectProxy", NUM).toUpperCase());
        pickerBottomLayout.doneButton.setOnClickListener(new -$$Lambda$AndroidUtilities$3sGpS5JwiCuQR9NgP1Xk7usREfk(str, str2, str5, str4, str3, dismissRunnable));
        builder.show();
    }

    static /* synthetic */ void lambda$showProxyAlert$4(String str, String str2, String str3, String str4, String str5, Runnable runnable, View view) {
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("proxy_enabled", true);
        edit.putString("proxy_ip", str);
        int intValue = Utilities.parseInt(str2).intValue();
        edit.putInt("proxy_port", intValue);
        String str6 = "proxy_secret";
        String str7 = "proxy_user";
        String str8 = "proxy_pass";
        ProxyInfo proxyInfo;
        if (TextUtils.isEmpty(str3)) {
            edit.remove(str6);
            if (TextUtils.isEmpty(str4)) {
                edit.remove(str8);
            } else {
                edit.putString(str8, str4);
            }
            if (TextUtils.isEmpty(str5)) {
                edit.remove(str7);
            } else {
                edit.putString(str7, str5);
            }
            proxyInfo = new ProxyInfo(str, intValue, str5, str4, "");
        } else {
            edit.remove(str8);
            edit.remove(str7);
            edit.putString(str6, str3);
            proxyInfo = new ProxyInfo(str, intValue, "", "", str3);
        }
        edit.commit();
        SharedConfig.currentProxy = SharedConfig.addProxy(r6);
        ConnectionsManager.setProxySettings(true, str, intValue, str5, str4, str3);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
        runnable.run();
    }

    @SuppressLint({"PrivateApi"})
    public static String getSystemProperty(String str) {
        try {
            return (String) Class.forName("android.os.SystemProperties").getMethod("get", new Class[]{String.class}).invoke(null, new Object[]{str});
        } catch (Exception unused) {
            return null;
        }
    }

    public static CharSequence concat(CharSequence... charSequenceArr) {
        if (charSequenceArr.length == 0) {
            return "";
        }
        int i = 0;
        Object obj = 1;
        if (charSequenceArr.length == 1) {
            return charSequenceArr[0];
        }
        for (CharSequence charSequence : charSequenceArr) {
            if (charSequence instanceof Spanned) {
                break;
            }
        }
        obj = null;
        int length;
        if (obj != null) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            length = charSequenceArr.length;
            while (i < length) {
                CharSequence charSequence2 = charSequenceArr[i];
                if (charSequence2 == null) {
                    charSequence2 = "null";
                }
                spannableStringBuilder.append(charSequence2);
                i++;
            }
            return new SpannedString(spannableStringBuilder);
        }
        StringBuilder stringBuilder = new StringBuilder();
        length = charSequenceArr.length;
        while (i < length) {
            stringBuilder.append(charSequenceArr[i]);
            i++;
        }
        return stringBuilder.toString();
    }

    public static float[] RGBtoHSB(int i, int i2, int i3) {
        float[] fArr = new float[3];
        int i4 = i > i2 ? i : i2;
        if (i3 > i4) {
            i4 = i3;
        }
        int i5 = i < i2 ? i : i2;
        if (i3 < i5) {
            i5 = i3;
        }
        float f = (float) i4;
        float f2 = f / 255.0f;
        float f3 = 0.0f;
        f = i4 != 0 ? ((float) (i4 - i5)) / f : 0.0f;
        if (f != 0.0f) {
            float f4 = (float) (i4 - i5);
            float f5 = ((float) (i4 - i)) / f4;
            float f6 = ((float) (i4 - i2)) / f4;
            float f7 = ((float) (i4 - i3)) / f4;
            f7 = i == i4 ? f7 - f6 : i2 == i4 ? (f5 + 2.0f) - f7 : (f6 + 4.0f) - f5;
            float f8 = f7 / 6.0f;
            f3 = f8 < 0.0f ? f8 + 1.0f : f8;
        }
        fArr[0] = f3;
        fArr[1] = f;
        fArr[2] = f2;
        return fArr;
    }

    public static int HSBtoRGB(float f, float f2, float f3) {
        int i;
        int i2;
        int i3 = 0;
        if (f2 == 0.0f) {
            i3 = (int) ((f3 * 255.0f) + 0.5f);
            i = i3;
            i2 = i;
        } else {
            f = (f - ((float) Math.floor((double) f))) * 6.0f;
            float floor = f - ((float) Math.floor((double) f));
            float f4 = (1.0f - f2) * f3;
            float f5 = (1.0f - (f2 * floor)) * f3;
            float f6 = (1.0f - (f2 * (1.0f - floor))) * f3;
            i = (int) f;
            if (i == 0) {
                i3 = (int) ((f3 * 255.0f) + 0.5f);
                i = (int) ((f6 * 255.0f) + 0.5f);
            } else if (i == 1) {
                i3 = (int) ((f5 * 255.0f) + 0.5f);
                i = (int) ((f3 * 255.0f) + 0.5f);
            } else if (i != 2) {
                if (i == 3) {
                    i3 = (int) ((f4 * 255.0f) + 0.5f);
                    i = (int) ((f5 * 255.0f) + 0.5f);
                } else if (i == 4) {
                    i3 = (int) ((f6 * 255.0f) + 0.5f);
                    i = (int) ((f4 * 255.0f) + 0.5f);
                } else if (i != 5) {
                    i = 0;
                    i2 = 0;
                } else {
                    i3 = (int) ((f3 * 255.0f) + 0.5f);
                    i = (int) ((f4 * 255.0f) + 0.5f);
                    i2 = (int) ((f5 * 255.0f) + 0.5f);
                }
                i2 = (int) ((f3 * 255.0f) + 0.5f);
            } else {
                i3 = (int) ((f4 * 255.0f) + 0.5f);
                i = (int) ((f3 * 255.0f) + 0.5f);
                i2 = (int) ((f6 * 255.0f) + 0.5f);
            }
            i2 = (int) ((f4 * 255.0f) + 0.5f);
        }
        return (((i & 255) << 8) | (-16777216 | ((i3 & 255) << 16))) | (i2 & 255);
    }

    public static int getPatternColor(int i) {
        float[] RGBtoHSB = RGBtoHSB(Color.red(i), Color.green(i), Color.blue(i));
        if (RGBtoHSB[1] > 0.0f || (RGBtoHSB[2] < 1.0f && RGBtoHSB[2] > 0.0f)) {
            RGBtoHSB[1] = Math.min(1.0f, (RGBtoHSB[1] + 0.05f) + ((1.0f - RGBtoHSB[1]) * 0.1f));
        }
        if (RGBtoHSB[2] > 0.5f) {
            RGBtoHSB[2] = Math.max(0.0f, RGBtoHSB[2] * 0.65f);
        } else {
            RGBtoHSB[2] = Math.max(0.0f, Math.min(1.0f, 1.0f - (RGBtoHSB[2] * 0.65f)));
        }
        return HSBtoRGB(RGBtoHSB[0], RGBtoHSB[1], RGBtoHSB[2]) & NUM;
    }

    public static int getPatternSideColor(int i) {
        float[] RGBtoHSB = RGBtoHSB(Color.red(i), Color.green(i), Color.blue(i));
        RGBtoHSB[1] = Math.min(1.0f, RGBtoHSB[1] + 0.05f);
        if (RGBtoHSB[2] > 0.5f) {
            RGBtoHSB[2] = Math.max(0.0f, RGBtoHSB[2] * 0.9f);
        } else {
            RGBtoHSB[2] = Math.max(0.0f, RGBtoHSB[2] * 0.9f);
        }
        return HSBtoRGB(RGBtoHSB[0], RGBtoHSB[1], RGBtoHSB[2]) | -16777216;
    }

    public static String getWallPaperUrl(Object obj, int i) {
        String str = "/bg/";
        String str2 = "https://";
        StringBuilder stringBuilder;
        if (obj instanceof TL_wallPaper) {
            TL_wallPaper tL_wallPaper = (TL_wallPaper) obj;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(str2);
            stringBuilder2.append(MessagesController.getInstance(i).linkPrefix);
            stringBuilder2.append(str);
            stringBuilder2.append(tL_wallPaper.slug);
            String stringBuilder3 = stringBuilder2.toString();
            stringBuilder2 = new StringBuilder();
            TL_wallPaperSettings tL_wallPaperSettings = tL_wallPaper.settings;
            if (tL_wallPaperSettings != null) {
                if (tL_wallPaperSettings.blur) {
                    stringBuilder2.append("blur");
                }
                if (tL_wallPaper.settings.motion) {
                    if (stringBuilder2.length() > 0) {
                        stringBuilder2.append("+");
                    }
                    stringBuilder2.append("motion");
                }
            }
            if (stringBuilder2.length() <= 0) {
                return stringBuilder3;
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append(stringBuilder3);
            stringBuilder.append("?mode=");
            stringBuilder.append(stringBuilder2.toString());
            return stringBuilder.toString();
        } else if (!(obj instanceof ColorWallpaper)) {
            return null;
        } else {
            String stringBuilder4;
            ColorWallpaper colorWallpaper = (ColorWallpaper) obj;
            String toLowerCase = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (colorWallpaper.color >> 16)) & 255), Integer.valueOf(((byte) (colorWallpaper.color >> 8)) & 255), Byte.valueOf((byte) (colorWallpaper.color & 255))}).toLowerCase();
            if (colorWallpaper.pattern != null) {
                StringBuilder stringBuilder5 = new StringBuilder();
                stringBuilder5.append(str2);
                stringBuilder5.append(MessagesController.getInstance(i).linkPrefix);
                stringBuilder5.append(str);
                stringBuilder5.append(colorWallpaper.pattern.slug);
                stringBuilder5.append("?intensity=");
                stringBuilder5.append((int) (colorWallpaper.intensity * 100.0f));
                stringBuilder5.append("&bg_color=");
                stringBuilder5.append(toLowerCase);
                stringBuilder4 = stringBuilder5.toString();
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str2);
                stringBuilder.append(MessagesController.getInstance(i).linkPrefix);
                stringBuilder.append(str);
                stringBuilder.append(toLowerCase);
                stringBuilder4 = stringBuilder.toString();
            }
            return stringBuilder4;
        }
    }

    public static float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((double) ((f - 0.5f) * 0.47123894f));
    }

    public static void makeAccessibilityAnnouncement(CharSequence charSequence) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) ApplicationLoader.applicationContext.getSystemService("accessibility");
        if (accessibilityManager.isEnabled()) {
            AccessibilityEvent obtain = AccessibilityEvent.obtain();
            obtain.setEventType(16384);
            obtain.getText().add(charSequence);
            accessibilityManager.sendAccessibilityEvent(obtain);
        }
    }

    public static int getOffsetColor(int i, int i2, float f, float f2) {
        int red = Color.red(i2);
        int green = Color.green(i2);
        int blue = Color.blue(i2);
        i2 = Color.alpha(i2);
        int red2 = Color.red(i);
        int green2 = Color.green(i);
        int blue2 = Color.blue(i);
        i = Color.alpha(i);
        return Color.argb((int) ((((float) i) + (((float) (i2 - i)) * f)) * f2), (int) (((float) red2) + (((float) (red - red2)) * f)), (int) (((float) green2) + (((float) (green - green2)) * f)), (int) (((float) blue2) + (((float) (blue - blue2)) * f)));
    }
}
