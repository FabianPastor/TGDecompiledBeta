package org.telegram.messenger;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.EdgeEffectCompat;
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
import com.android.internal.telephony.ITelephony;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_userContact_old2;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Components.ForegroundDetector;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PickerBottomLayout;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.ThemePreviewActivity;
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
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                boolean result = super.onTouchEvent(widget, buffer, event);
                if (event.getAction() != 1 && event.getAction() != 3) {
                    return result;
                }
                Selection.removeSelection(buffer);
                return result;
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

        /* synthetic */ VcardData(AnonymousClass1 x0) {
            this();
        }
    }

    public static class VcardItem {
        public boolean checked = true;
        public String fullData = "";
        public int type;
        public ArrayList<String> vcardData = new ArrayList();

        public String[] getRawValue() {
            int idx = this.fullData.indexOf(58);
            if (idx < 0) {
                return new String[0];
            }
            int a;
            String valueType = this.fullData.substring(0, idx);
            String value = this.fullData.substring(idx + 1, this.fullData.length());
            String nameEncoding = null;
            String nameCharset = "UTF-8";
            String[] params = valueType.split(";");
            for (String split : params) {
                String[] args2 = split.split("=");
                if (args2.length == 2) {
                    if (args2[0].equals("CHARSET")) {
                        nameCharset = args2[1];
                    } else if (args2[0].equals("ENCODING")) {
                        nameEncoding = args2[1];
                    }
                }
            }
            String[] args = value.split(";");
            for (a = 0; a < args.length; a++) {
                if (!(TextUtils.isEmpty(args[a]) || nameEncoding == null || !nameEncoding.equalsIgnoreCase("QUOTED-PRINTABLE"))) {
                    byte[] bytes = AndroidUtilities.decodeQuotedPrintable(AndroidUtilities.getStringBytes(args[a]));
                    if (!(bytes == null || bytes.length == 0)) {
                        try {
                            args[a] = new String(bytes, nameCharset);
                        } catch (Exception e) {
                        }
                    }
                }
            }
            return args;
        }

        public String getValue(boolean format) {
            StringBuilder result = new StringBuilder();
            int idx = this.fullData.indexOf(58);
            if (idx < 0) {
                return "";
            }
            int a;
            if (result.length() > 0) {
                result.append(", ");
            }
            String valueType = this.fullData.substring(0, idx);
            String value = this.fullData.substring(idx + 1, this.fullData.length());
            String nameEncoding = null;
            String nameCharset = "UTF-8";
            String[] params = valueType.split(";");
            for (String split : params) {
                String[] args2 = split.split("=");
                if (args2.length == 2) {
                    if (args2[0].equals("CHARSET")) {
                        nameCharset = args2[1];
                    } else if (args2[0].equals("ENCODING")) {
                        nameEncoding = args2[1];
                    }
                }
            }
            String[] args = value.split(";");
            boolean added = false;
            for (a = 0; a < args.length; a++) {
                if (!TextUtils.isEmpty(args[a])) {
                    if (nameEncoding != null && nameEncoding.equalsIgnoreCase("QUOTED-PRINTABLE")) {
                        byte[] bytes = AndroidUtilities.decodeQuotedPrintable(AndroidUtilities.getStringBytes(args[a]));
                        if (!(bytes == null || bytes.length == 0)) {
                            try {
                                args[a] = new String(bytes, nameCharset);
                            } catch (Exception e) {
                            }
                        }
                    }
                    if (added && result.length() > 0) {
                        result.append(" ");
                    }
                    result.append(args[a]);
                    if (!added) {
                        added = args[a].length() > 0;
                    }
                }
            }
            if (format) {
                if (this.type == 0) {
                    return PhoneFormat.getInstance().format(result.toString());
                }
                if (this.type == 5) {
                    String[] date = result.toString().split("T");
                    if (date.length > 0) {
                        date = date[0].split("-");
                        if (date.length == 3) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(1, Utilities.parseInt(date[0]).intValue());
                            calendar.set(2, Utilities.parseInt(date[1]).intValue() - 1);
                            calendar.set(5, Utilities.parseInt(date[2]).intValue());
                            return LocaleController.getInstance().formatterYearMax.format(calendar.getTime());
                        }
                    }
                }
            }
            return result.toString();
        }

        public String getRawType(boolean first) {
            int idx = this.fullData.indexOf(58);
            if (idx < 0) {
                return "";
            }
            String value = this.fullData.substring(0, idx);
            String[] args;
            if (this.type == 20) {
                args = value.substring(2).split(";");
                if (first) {
                    return args[0];
                }
                if (args.length > 1) {
                    return args[args.length - 1];
                }
                return "";
            }
            args = value.split(";");
            for (int a = 0; a < args.length; a++) {
                if (args[a].indexOf(61) < 0) {
                    value = args[a];
                }
            }
            return value;
        }

        public String getType() {
            if (this.type == 5) {
                return LocaleController.getString("ContactBirthday", NUM);
            }
            if (this.type != 6) {
                int idx = this.fullData.indexOf(58);
                if (idx < 0) {
                    return "";
                }
                String value = this.fullData.substring(0, idx);
                if (this.type == 20) {
                    value = value.substring(2).split(";")[0];
                } else {
                    String[] args = value.split(";");
                    for (int a = 0; a < args.length; a++) {
                        if (args[a].indexOf(61) < 0) {
                            value = args[a];
                        }
                    }
                    if (value.startsWith("X-")) {
                        value = value.substring(2);
                    }
                    if ("PREF".equals(value)) {
                        value = LocaleController.getString("PhoneMain", NUM);
                    } else if ("HOME".equals(value)) {
                        value = LocaleController.getString("PhoneHome", NUM);
                    } else if ("MOBILE".equals(value) || "CELL".equals(value)) {
                        value = LocaleController.getString("PhoneMobile", NUM);
                    } else if ("OTHER".equals(value)) {
                        value = LocaleController.getString("PhoneOther", NUM);
                    } else if ("WORK".equals(value)) {
                        value = LocaleController.getString("PhoneWork", NUM);
                    }
                }
                return value.substring(0, 1).toUpperCase() + value.substring(1, value.length()).toLowerCase();
            } else if ("ORG".equalsIgnoreCase(getRawType(true))) {
                return LocaleController.getString("ContactJob", NUM);
            } else {
                return LocaleController.getString("ContactJobTitle", NUM);
            }
        }
    }

    static {
        boolean z;
        WEB_URL = null;
        try {
            String GOOD_IRI_CHAR = "a-zA-Z0-9 -퟿豈-﷏ﷰ-￯";
            String IRI = "[a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯\\-]{0,61}[a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]){0,1}";
            String GOOD_GTLD_CHAR = "a-zA-Z -퟿豈-﷏ﷰ-￯";
            String GTLD = "[a-zA-Z -퟿豈-﷏ﷰ-￯]{2,63}";
            String HOST_NAME = "([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯\\-]{0,61}[a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]){0,1}\\.)+[a-zA-Z -퟿豈-﷏ﷰ-￯]{2,63}";
            WEB_URL = Pattern.compile("((?:(http|https|Http|Https):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?(?:" + Pattern.compile("(([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯\\-]{0,61}[a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]){0,1}\\.)+[a-zA-Z -퟿豈-﷏ﷰ-￯]{2,63}|" + Pattern.compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9]))") + ")") + ")(?:\\:\\d{1,5})?)(\\/(?:(?:[" + "a-zA-Z0-9 -퟿豈-﷏ﷰ-￯" + "\\;\\/\\?\\:\\@\\&\\=\\#\\~\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?(?:\\b|$)");
        } catch (Exception e) {
            FileLog.e(e);
        }
        checkDisplaySize(ApplicationLoader.applicationContext, null);
        if (VERSION.SDK_INT >= 23) {
            z = true;
        } else {
            z = false;
        }
        hasCallPermissions = z;
    }

    public static int getThumbForNameOrMime(String name, String mime, boolean media) {
        if (name == null || name.length() == 0) {
            return media ? documentMediaIcons[0] : documentIcons[0];
        } else {
            int color = -1;
            if (name.contains(".doc") || name.contains(".txt") || name.contains(".psd")) {
                color = 0;
            } else if (name.contains(".xls") || name.contains(".csv")) {
                color = 1;
            } else if (name.contains(".pdf") || name.contains(".ppt") || name.contains(".key")) {
                color = 2;
            } else if (name.contains(".zip") || name.contains(".rar") || name.contains(".ai") || name.contains(".mp3") || name.contains(".mov") || name.contains(".avi")) {
                color = 3;
            }
            if (color == -1) {
                int idx = name.lastIndexOf(46);
                String ext = idx == -1 ? "" : name.substring(idx + 1);
                if (ext.length() != 0) {
                    color = ext.charAt(0) % documentIcons.length;
                } else {
                    color = name.charAt(0) % documentIcons.length;
                }
            }
            return media ? documentMediaIcons[color] : documentIcons[color];
        }
    }

    public static int[] calcDrawableColor(Drawable drawable) {
        int bitmapColor = -16777216;
        int[] result = new int[4];
        try {
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                if (bitmap != null) {
                    Bitmap b = Bitmaps.createScaledBitmap(bitmap, 1, 1, true);
                    if (b != null) {
                        bitmapColor = b.getPixel(0, 0);
                        if (bitmap != b) {
                            b.recycle();
                        }
                    }
                }
            } else if (drawable instanceof ColorDrawable) {
                bitmapColor = ((ColorDrawable) drawable).getColor();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        double[] hsv = rgbToHsv((bitmapColor >> 16) & 255, (bitmapColor >> 8) & 255, bitmapColor & 255);
        hsv[1] = Math.min(1.0d, (hsv[1] + 0.05d) + (0.1d * (1.0d - hsv[1])));
        int[] rgb = hsvToRgb(hsv[0], hsv[1], Math.max(0.0d, hsv[2] * 0.65d));
        result[0] = Color.argb(102, rgb[0], rgb[1], rgb[2]);
        result[1] = Color.argb(136, rgb[0], rgb[1], rgb[2]);
        rgb = hsvToRgb(hsv[0], hsv[1], Math.max(0.0d, hsv[2] * 0.72d));
        result[2] = Color.argb(102, rgb[0], rgb[1], rgb[2]);
        result[3] = Color.argb(136, rgb[0], rgb[1], rgb[2]);
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

    public static boolean isGoogleMapsInstalled(BaseFragment fragment) {
        try {
            ApplicationLoader.applicationContext.getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
            return true;
        } catch (NameNotFoundException e) {
            if (fragment.getParentActivity() == null) {
                return false;
            }
            Builder builder = new Builder(fragment.getParentActivity());
            builder.setMessage(LocaleController.getString("InstallGoogleMaps", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new AndroidUtilities$$Lambda$0(fragment));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            fragment.showDialog(builder.create());
            return false;
        }
    }

    static final /* synthetic */ void lambda$isGoogleMapsInstalled$0$AndroidUtilities(BaseFragment fragment, DialogInterface dialogInterface, int i) {
        try {
            fragment.getParentActivity().startActivityForResult(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.google.android.apps.maps")), 500);
        } catch (Exception e1) {
            FileLog.e(e1);
        }
    }

    public static int[] toIntArray(List<Integer> integers) {
        int[] ret = new int[integers.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = ((Integer) integers.get(i)).intValue();
        }
        return ret;
    }

    public static boolean isInternalUri(Uri uri) {
        String pathString = uri.getPath();
        if (pathString == null) {
            return false;
        }
        if (pathString.matches(Pattern.quote(new File(ApplicationLoader.applicationContext.getCacheDir(), "voip_logs").getAbsolutePath()) + "/\\d+\\.log")) {
            return false;
        }
        int tries = 0;
        do {
            if (pathString != null && pathString.length() > 4096) {
                return true;
            }
            String newPath = Utilities.readlink(pathString);
            if (newPath == null || newPath.equals(pathString)) {
                if (pathString != null) {
                    try {
                        String path = new File(pathString).getCanonicalPath();
                        if (path != null) {
                            pathString = path;
                        }
                    } catch (Exception e) {
                        pathString.replace("/./", "/");
                    }
                }
                if (pathString == null || !pathString.toLowerCase().contains("/data/data/" + ApplicationLoader.applicationContext.getPackageName())) {
                    return false;
                }
                return true;
            }
            pathString = newPath;
            tries++;
        } while (tries < 10);
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
            } catch (Exception e) {
                FileLog.e(e);
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
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static byte[] getStringBytes(String src) {
        try {
            return src.getBytes("UTF-8");
        } catch (Exception e) {
            return new byte[0];
        }
    }

    public static ArrayList<User> loadVCardFromStream(Uri uri, int currentAccount, boolean asset, ArrayList<VcardItem> items, String name) {
        InputStream stream;
        Throwable e;
        ArrayList<User> result = null;
        if (asset) {
            try {
                stream = ApplicationLoader.applicationContext.getContentResolver().openAssetFileDescriptor(uri, "r").createInputStream();
            } catch (Exception e2) {
                FileLog.e(e2);
            } catch (Throwable th) {
                e2 = th;
                FileLog.e(e2);
                return result;
            }
        }
        stream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
        ArrayList<VcardData> vcardDatas = new ArrayList();
        VcardData currentData = null;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        String pendingLine = null;
        boolean currentIsPhoto = false;
        VcardItem currentItem = null;
        while (true) {
            String line = bufferedReader.readLine();
            String originalLine = line;
            if (line == null) {
                break;
            } else if (originalLine.startsWith("PHOTO")) {
                currentIsPhoto = true;
            } else {
                if (originalLine.indexOf(58) >= 0) {
                    currentItem = null;
                    currentIsPhoto = false;
                    if (originalLine.startsWith("BEGIN:VCARD")) {
                        currentData = new VcardData(null);
                        vcardDatas.add(currentData);
                        currentData.name = name;
                    } else if (!(originalLine.startsWith("END:VCARD") || items == null)) {
                        if (originalLine.startsWith("TEL")) {
                            currentItem = new VcardItem();
                            currentItem.type = 0;
                        } else if (originalLine.startsWith("EMAIL")) {
                            currentItem = new VcardItem();
                            currentItem.type = 1;
                        } else if (originalLine.startsWith("ADR") || originalLine.startsWith("LABEL") || originalLine.startsWith("GEO")) {
                            currentItem = new VcardItem();
                            currentItem.type = 2;
                        } else if (originalLine.startsWith("URL")) {
                            currentItem = new VcardItem();
                            currentItem.type = 3;
                        } else if (originalLine.startsWith("NOTE")) {
                            currentItem = new VcardItem();
                            currentItem.type = 4;
                        } else if (originalLine.startsWith("BDAY")) {
                            currentItem = new VcardItem();
                            currentItem.type = 5;
                        } else if (originalLine.startsWith("ORG") || originalLine.startsWith("TITLE") || originalLine.startsWith("ROLE")) {
                            if (null == null) {
                                currentItem = new VcardItem();
                                currentItem.type = 6;
                            }
                        } else if (originalLine.startsWith("X-ANDROID")) {
                            currentItem = new VcardItem();
                            currentItem.type = -1;
                        } else if (originalLine.startsWith("X-PHONETIC")) {
                            currentItem = null;
                        } else if (originalLine.startsWith("X-")) {
                            currentItem = new VcardItem();
                            currentItem.type = 20;
                        }
                        if (currentItem != null && currentItem.type >= 0) {
                            items.add(currentItem);
                        }
                    }
                }
                if (!(currentIsPhoto || currentData == null)) {
                    if (currentItem == null) {
                        if (currentData.vcard.length() > 0) {
                            currentData.vcard.append(10);
                        }
                        currentData.vcard.append(originalLine);
                    } else {
                        currentItem.vcardData.add(originalLine);
                    }
                }
                if (pendingLine != null) {
                    line = pendingLine + line;
                    pendingLine = null;
                }
                if (line.contains("=QUOTED-PRINTABLE") && line.endsWith("=")) {
                    pendingLine = line.substring(0, line.length() - 1);
                } else {
                    String[] args;
                    if (!(currentIsPhoto || currentData == null || currentItem == null)) {
                        currentItem.fullData = line;
                    }
                    int idx = line.indexOf(":");
                    if (idx >= 0) {
                        args = new String[2];
                        args[0] = line.substring(0, idx);
                        args[1] = line.substring(idx + 1, line.length()).trim();
                    } else {
                        args = new String[]{line.trim()};
                    }
                    if (args.length >= 2 && currentData != null) {
                        if (args[0].startsWith("FN") || (args[0].startsWith("ORG") && TextUtils.isEmpty(currentData.name))) {
                            String nameEncoding = null;
                            String nameCharset = null;
                            for (String param : args[0].split(";")) {
                                String[] args2 = param.split("=");
                                if (args2.length == 2) {
                                    if (args2[0].equals("CHARSET")) {
                                        nameCharset = args2[1];
                                    } else if (args2[0].equals("ENCODING")) {
                                        nameEncoding = args2[1];
                                    }
                                }
                            }
                            currentData.name = args[1];
                            if (nameEncoding != null && nameEncoding.equalsIgnoreCase("QUOTED-PRINTABLE")) {
                                byte[] bytes = decodeQuotedPrintable(getStringBytes(currentData.name));
                                if (!(bytes == null || bytes.length == 0)) {
                                    String decodedName = new String(bytes, nameCharset);
                                    if (decodedName != null) {
                                        currentData.name = decodedName;
                                    }
                                }
                            }
                        } else if (args[0].startsWith("TEL")) {
                            currentData.phones.add(args[1]);
                        }
                    }
                }
            }
        }
        bufferedReader.close();
        stream.close();
        int a = 0;
        ArrayList<User> result2 = null;
        while (a < vcardDatas.size()) {
            try {
                VcardData vcardData = (VcardData) vcardDatas.get(a);
                if (vcardData.name == null || vcardData.phones.isEmpty()) {
                    result = result2;
                } else {
                    if (result2 == null) {
                        result = new ArrayList();
                    } else {
                        result = result2;
                    }
                    String phoneToUse = (String) vcardData.phones.get(0);
                    for (int b = 0; b < vcardData.phones.size(); b++) {
                        String phone = (String) vcardData.phones.get(b);
                        if (ContactsController.getInstance(currentAccount).contactsByShortPhone.get(phone.substring(Math.max(0, phone.length() - 7))) != null) {
                            phoneToUse = phone;
                            break;
                        }
                    }
                    User user = new TL_userContact_old2();
                    user.phone = phoneToUse;
                    user.first_name = vcardData.name;
                    user.last_name = "";
                    user.id = 0;
                    user.restriction_reason = vcardData.vcard.toString();
                    result.add(user);
                }
                a++;
                result2 = result;
            } catch (Throwable th2) {
                e2 = th2;
                result = result2;
                FileLog.e(e2);
                return result;
            }
        }
        return result2;
    }

    public static Typeface getTypeface(String assetPath) {
        Typeface typeface;
        synchronized (typefaceCache) {
            if (!typefaceCache.containsKey(assetPath)) {
                try {
                    Typeface t;
                    if (VERSION.SDK_INT >= 26) {
                        Typeface.Builder builder = new Typeface.Builder(ApplicationLoader.applicationContext.getAssets(), assetPath);
                        if (assetPath.contains("medium")) {
                            builder.setWeight(700);
                        }
                        if (assetPath.contains("italic")) {
                            builder.setItalic(true);
                        }
                        t = builder.build();
                    } else {
                        t = Typeface.createFromAsset(ApplicationLoader.applicationContext.getAssets(), assetPath);
                    }
                    typefaceCache.put(assetPath, t);
                } catch (Exception e) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("Could not get typeface '" + assetPath + "' because " + e.getMessage());
                    }
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
            try {
                if (waitingForSms) {
                    SmsRetriever.getClient(ApplicationLoader.applicationContext).startSmsRetriever().addOnSuccessListener(AndroidUtilities$$Lambda$1.$instance);
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    static final /* synthetic */ void lambda$setWaitingForSms$1$AndroidUtilities(Void aVoid) {
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("sms listener registered");
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
            if (value) {
                try {
                    if (callReceiver == null) {
                        IntentFilter filter = new IntentFilter("android.intent.action.PHONE_STATE");
                        Context context = ApplicationLoader.applicationContext;
                        CallReceiver callReceiver = new CallReceiver();
                        callReceiver = callReceiver;
                        context.registerReceiver(callReceiver, filter);
                    }
                } catch (Exception e) {
                }
            } else if (callReceiver != null) {
                ApplicationLoader.applicationContext.unregisterReceiver(callReceiver);
                callReceiver = null;
            }
            waitingForCall = value;
        }
    }

    public static boolean showKeyboard(View view) {
        boolean z = false;
        if (view == null) {
            return z;
        }
        try {
            return ((InputMethodManager) view.getContext().getSystemService("input_method")).showSoftInput(view, 1);
        } catch (Exception e) {
            FileLog.e(e);
            return z;
        }
    }

    public static boolean isKeyboardShowed(View view) {
        boolean z = false;
        if (view == null) {
            return z;
        }
        try {
            return ((InputMethodManager) view.getContext().getSystemService("input_method")).isActive(view);
        } catch (Exception e) {
            FileLog.e(e);
            return z;
        }
    }

    public static String getCurrentKeyboardLanguage() {
        try {
            InputMethodManager inputManager = (InputMethodManager) ApplicationLoader.applicationContext.getSystemService("input_method");
            InputMethodSubtype inputMethodSubtype = inputManager.getCurrentInputMethodSubtype();
            String locale = null;
            if (inputMethodSubtype != null) {
                if (VERSION.SDK_INT >= 24) {
                    locale = inputMethodSubtype.getLanguageTag();
                }
                if (TextUtils.isEmpty(locale)) {
                    locale = inputMethodSubtype.getLocale();
                }
            } else {
                inputMethodSubtype = inputManager.getLastInputMethodSubtype();
                if (inputMethodSubtype != null) {
                    if (VERSION.SDK_INT >= 24) {
                        locale = inputMethodSubtype.getLanguageTag();
                    }
                    if (TextUtils.isEmpty(locale)) {
                        locale = inputMethodSubtype.getLocale();
                    }
                }
            }
            if (TextUtils.isEmpty(locale)) {
                locale = LocaleController.getSystemLocaleStringIso639();
            }
            return locale.replace('_', '-');
        } catch (Exception e) {
            return "en";
        }
    }

    public static void hideKeyboard(View view) {
        if (view != null) {
            try {
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService("input_method");
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static File getCacheDir() {
        File file;
        String state = null;
        try {
            state = Environment.getExternalStorageState();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (state == null || state.startsWith("mounted")) {
            try {
                file = ApplicationLoader.applicationContext.getExternalCacheDir();
                if (file != null) {
                    return file;
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        try {
            file = ApplicationLoader.applicationContext.getCacheDir();
            if (file != null) {
                return file;
            }
        } catch (Exception e22) {
            FileLog.e(e22);
        }
        return new File("");
    }

    public static int dp(float value) {
        if (value == 0.0f) {
            return 0;
        }
        return (int) Math.ceil((double) (density * value));
    }

    public static int dp2(float value) {
        if (value == 0.0f) {
            return 0;
        }
        return (int) Math.floor((double) (density * value));
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
            if (roundMessageSize == 0) {
                if (isTablet()) {
                    roundMessageSize = (int) (((float) getMinTabletSide()) * 0.6f);
                } else {
                    roundMessageSize = (int) (((float) Math.min(displaySize.x, displaySize.y)) * 0.6f);
                }
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("display size = " + displaySize.x + " " + displaySize.y + " " + displayMetrics.xdpi + "x" + displayMetrics.ydpi);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static double fixLocationCoord(double value) {
        return ((double) ((long) (value * 1000000.0d))) / 1000000.0d;
    }

    public static String formapMapUrl(int account, double lat, double lon, int width, int height, boolean marker, int zoom) {
        int scale = Math.min(2, (int) Math.ceil((double) density));
        int provider = MessagesController.getInstance(account).mapProvider;
        if (provider == 1 || provider == 3) {
            String lang = null;
            String[] availableLangs = new String[]{"ru_RU", "tr_TR"};
            LocaleInfo localeInfo = LocaleController.getInstance().getCurrentLocaleInfo();
            for (int a = 0; a < availableLangs.length; a++) {
                if (availableLangs[a].toLowerCase().contains(localeInfo.shortName)) {
                    lang = availableLangs[a];
                }
            }
            if (lang == null) {
                lang = "en_US";
            }
            if (marker) {
                return String.format(Locale.US, "https://static-maps.yandex.ru/1.x/?ll=%.6f,%.6f&z=%d&size=%d,%d&l=map&scale=%d&pt=%.6f,%.6f,vkbkm&lang=%s", new Object[]{Double.valueOf(lon), Double.valueOf(lat), Integer.valueOf(zoom), Integer.valueOf(width * scale), Integer.valueOf(height * scale), Integer.valueOf(scale), Double.valueOf(lon), Double.valueOf(lat), lang});
            }
            return String.format(Locale.US, "https://static-maps.yandex.ru/1.x/?ll=%.6f,%.6f&z=%d&size=%d,%d&l=map&scale=%d&lang=%s", new Object[]{Double.valueOf(lon), Double.valueOf(lat), Integer.valueOf(zoom), Integer.valueOf(width * scale), Integer.valueOf(height * scale), Integer.valueOf(scale), lang});
        }
        if (TextUtils.isEmpty(MessagesController.getInstance(account).mapKey)) {
            if (marker) {
                return String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%.6f,%.6f&zoom=%d&size=%dx%d&maptype=roadmap&scale=%d&markers=color:red%%7Csize:mid%%7C%.6f,%.6f&sensor=false", new Object[]{Double.valueOf(lat), Double.valueOf(lon), Integer.valueOf(zoom), Integer.valueOf(width), Integer.valueOf(height), Integer.valueOf(scale), Double.valueOf(lat), Double.valueOf(lon)});
            }
            return String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%.6f,%.6f&zoom=%d&size=%dx%d&maptype=roadmap&scale=%d", new Object[]{Double.valueOf(lat), Double.valueOf(lon), Integer.valueOf(zoom), Integer.valueOf(width), Integer.valueOf(height), Integer.valueOf(scale)});
        } else if (marker) {
            return String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%.6f,%.6f&zoom=%d&size=%dx%d&maptype=roadmap&scale=%d&markers=color:red%%7Csize:mid%%7C%.6f,%.6f&sensor=false&key=%s", new Object[]{Double.valueOf(lat), Double.valueOf(lon), Integer.valueOf(zoom), Integer.valueOf(width), Integer.valueOf(height), Integer.valueOf(scale), Double.valueOf(lat), Double.valueOf(lon), k});
        } else {
            return String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%.6f,%.6f&zoom=%d&size=%dx%d&maptype=roadmap&scale=%d&key=%s", new Object[]{Double.valueOf(lat), Double.valueOf(lon), Integer.valueOf(zoom), Integer.valueOf(width), Integer.valueOf(height), Integer.valueOf(scale), k});
        }
    }

    public static float getPixelsInCM(float cm, boolean isX) {
        return (isX ? displayMetrics.xdpi : displayMetrics.ydpi) * (cm / 2.54f);
    }

    public static long makeBroadcastId(int id) {
        return 4294967296L | (((long) id) & 4294967295L);
    }

    public static int getMyLayerVersion(int layer) {
        return 65535 & layer;
    }

    public static int getPeerLayerVersion(int layer) {
        return (layer >> 16) & 65535;
    }

    public static int setMyLayerVersion(int layer, int version) {
        return (-65536 & layer) | version;
    }

    public static int setPeerLayerVersion(int layer, int version) {
        return (65535 & layer) | (version << 16);
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
            isTablet = Boolean.valueOf(ApplicationLoader.applicationContext.getResources().getBoolean(NUM));
        }
        return isTablet.booleanValue();
    }

    public static boolean isSmallTablet() {
        return ((float) Math.min(displaySize.x, displaySize.y)) / density <= 700.0f;
    }

    public static int getMinTabletSide() {
        int smallSide;
        int leftSide;
        if (isSmallTablet()) {
            smallSide = Math.min(displaySize.x, displaySize.y);
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
            photoSize = Integer.valueOf(1280);
        }
        return photoSize.intValue();
    }

    public static void endIncomingCall() {
        if (hasCallPermissions) {
            try {
                TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                Method m = Class.forName(tm.getClass().getName()).getDeclaredMethod("getITelephony", new Class[0]);
                m.setAccessible(true);
                ITelephony telephonyService = (ITelephony) m.invoke(tm, new Object[0]);
                telephonyService = (ITelephony) m.invoke(tm, new Object[0]);
                telephonyService.silenceRinger();
                telephonyService.endCall();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static boolean checkPhonePattern(String pattern, String phone) {
        if (TextUtils.isEmpty(pattern) || pattern.equals("*")) {
            return true;
        }
        String[] args = pattern.split("\\*");
        phone = PhoneFormat.stripExceptNumbers(phone);
        int checkStart = 0;
        for (String arg : args) {
            if (!TextUtils.isEmpty(arg)) {
                int index = phone.indexOf(arg, checkStart);
                if (index == -1) {
                    return false;
                }
                checkStart = index + arg.length();
            }
        }
        return true;
    }

    public static int getViewInset(View view) {
        if (view == null || VERSION.SDK_INT < 21 || view.getHeight() == displaySize.y || view.getHeight() == displaySize.y - statusBarHeight) {
            return 0;
        }
        try {
            if (mAttachInfoField == null) {
                mAttachInfoField = View.class.getDeclaredField("mAttachInfo");
                mAttachInfoField.setAccessible(true);
            }
            Object mAttachInfo = mAttachInfoField.get(view);
            if (mAttachInfo == null) {
                return 0;
            }
            if (mStableInsetsField == null) {
                mStableInsetsField = mAttachInfo.getClass().getDeclaredField("mStableInsets");
                mStableInsetsField.setAccessible(true);
            }
            return ((Rect) mStableInsetsField.get(mAttachInfo)).bottom;
        } catch (Exception e) {
            FileLog.e(e);
            return 0;
        }
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
                } catch (Exception e) {
                    size.set(windowManager.getDefaultDisplay().getWidth(), windowManager.getDefaultDisplay().getHeight());
                    FileLog.e(e);
                }
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        return size;
    }

    public static void setEnabled(View view, boolean enabled) {
        if (view != null) {
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    setEnabled(viewGroup.getChildAt(i), enabled);
                }
            }
        }
    }

    public static CharSequence getTrimmedString(CharSequence src) {
        if (!(src == null || src.length() == 0)) {
            while (src.length() > 0 && (src.charAt(0) == 10 || src.charAt(0) == ' ')) {
                src = src.subSequence(1, src.length());
            }
            while (src.length() > 0 && (src.charAt(src.length() - 1) == 10 || src.charAt(src.length() - 1) == ' ')) {
                src = src.subSequence(0, src.length() - 1);
            }
        }
        return src;
    }

    public static void setViewPagerEdgeEffectColor(ViewPager viewPager, int color) {
        if (VERSION.SDK_INT >= 21) {
            try {
                EdgeEffect mEdgeEffect;
                Field field = ViewPager.class.getDeclaredField("mLeftEdge");
                field.setAccessible(true);
                EdgeEffectCompat mLeftEdge = (EdgeEffectCompat) field.get(viewPager);
                if (mLeftEdge != null) {
                    field = EdgeEffectCompat.class.getDeclaredField("mEdgeEffect");
                    field.setAccessible(true);
                    mEdgeEffect = (EdgeEffect) field.get(mLeftEdge);
                    if (mEdgeEffect != null) {
                        mEdgeEffect.setColor(color);
                    }
                }
                field = ViewPager.class.getDeclaredField("mRightEdge");
                field.setAccessible(true);
                EdgeEffectCompat mRightEdge = (EdgeEffectCompat) field.get(viewPager);
                if (mRightEdge != null) {
                    field = EdgeEffectCompat.class.getDeclaredField("mEdgeEffect");
                    field.setAccessible(true);
                    mEdgeEffect = (EdgeEffect) field.get(mRightEdge);
                    if (mEdgeEffect != null) {
                        mEdgeEffect.setColor(color);
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    public static void setScrollViewEdgeEffectColor(HorizontalScrollView scrollView, int color) {
        if (VERSION.SDK_INT >= 21) {
            try {
                Field field = HorizontalScrollView.class.getDeclaredField("mEdgeGlowLeft");
                field.setAccessible(true);
                EdgeEffect mEdgeGlowTop = (EdgeEffect) field.get(scrollView);
                if (mEdgeGlowTop != null) {
                    mEdgeGlowTop.setColor(color);
                }
                field = HorizontalScrollView.class.getDeclaredField("mEdgeGlowRight");
                field.setAccessible(true);
                EdgeEffect mEdgeGlowBottom = (EdgeEffect) field.get(scrollView);
                if (mEdgeGlowBottom != null) {
                    mEdgeGlowBottom.setColor(color);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static void setScrollViewEdgeEffectColor(ScrollView scrollView, int color) {
        if (VERSION.SDK_INT >= 21) {
            try {
                Field field = ScrollView.class.getDeclaredField("mEdgeGlowTop");
                field.setAccessible(true);
                EdgeEffect mEdgeGlowTop = (EdgeEffect) field.get(scrollView);
                if (mEdgeGlowTop != null) {
                    mEdgeGlowTop.setColor(color);
                }
                field = ScrollView.class.getDeclaredField("mEdgeGlowBottom");
                field.setAccessible(true);
                EdgeEffect mEdgeGlowBottom = (EdgeEffect) field.get(scrollView);
                if (mEdgeGlowBottom != null) {
                    mEdgeGlowBottom.setColor(color);
                }
            } catch (Exception e) {
                FileLog.e(e);
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
        return replaceTags(str, 11, new Object[0]);
    }

    public static SpannableStringBuilder replaceTags(String str, int flag, Object... args) {
        try {
            int start;
            int end;
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
                while (true) {
                    start = stringBuilder.indexOf("**");
                    if (start == -1) {
                        break;
                    }
                    stringBuilder.replace(start, start + 2, "");
                    end = stringBuilder.indexOf("**");
                    if (end >= 0) {
                        stringBuilder.replace(end, end + 2, "");
                        bolds.add(Integer.valueOf(start));
                        bolds.add(Integer.valueOf(end));
                    }
                }
            }
            if ((flag & 8) != 0) {
                while (true) {
                    start = stringBuilder.indexOf("**");
                    if (start == -1) {
                        break;
                    }
                    stringBuilder.replace(start, start + 2, "");
                    end = stringBuilder.indexOf("**");
                    if (end >= 0) {
                        stringBuilder.replace(end, end + 2, "");
                        bolds.add(Integer.valueOf(start));
                        bolds.add(Integer.valueOf(end));
                    }
                }
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringBuilder);
            for (int a = 0; a < bolds.size() / 2; a++) {
                spannableStringBuilder.setSpan(new TypefaceSpan(getTypeface("fonts/rmedium.ttf")), ((Integer) bolds.get(a * 2)).intValue(), ((Integer) bolds.get((a * 2) + 1)).intValue(), 33);
            }
            return spannableStringBuilder;
        } catch (Exception e) {
            FileLog.e(e);
            return new SpannableStringBuilder(str);
        }
    }

    public static boolean needShowPasscode(boolean reset) {
        boolean wasInBackground = ForegroundDetector.getInstance().isWasInBackground(reset);
        if (reset) {
            ForegroundDetector.getInstance().resetBackgroundVar();
        }
        return SharedConfig.passcodeHash.length() > 0 && wasInBackground && (SharedConfig.appLocked || (!(SharedConfig.autoLockIn == 0 || SharedConfig.lastPauseTime == 0 || SharedConfig.appLocked || SharedConfig.lastPauseTime + SharedConfig.autoLockIn > ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime()) || ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime() + 5 < SharedConfig.lastPauseTime));
    }

    public static void shakeView(final View view, final float x, final int num) {
        if (view != null) {
            if (num == 6) {
                view.setTranslationX(0.0f);
                return;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(view, "translationX", new float[]{(float) dp(x)});
            animatorSet.playTogether(animatorArr);
            animatorSet.setDuration(50);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AndroidUtilities.shakeView(view, num == 5 ? 0.0f : -x, num + 1);
                }
            });
            animatorSet.start();
        }
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
        } catch (Exception e) {
            FileLog.e(e);
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
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    private static File getAlbumDir(boolean secretChat) {
        if (secretChat || (VERSION.SDK_INT >= 23 && ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0)) {
            return FileLoader.getDirectory(4);
        }
        if ("mounted".equals(Environment.getExternalStorageState())) {
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Telegram");
            if (storageDir.mkdirs() || storageDir.exists()) {
                return storageDir;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("failed to create directory");
            }
            return null;
        } else if (!BuildVars.LOGS_ENABLED) {
            return null;
        } else {
            FileLog.d("External storage is not mounted READ/WRITE.");
            return null;
        }
    }

    @android.annotation.SuppressLint({"NewApi"})
    public static java.lang.String getPath(android.net.Uri r14) {
        /*
        r9 = 0;
        r12 = 1;
        r10 = 0;
        r11 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0103 }
        r13 = 19;
        if (r11 < r13) goto L_0x0051;
    L_0x0009:
        r4 = r12;
    L_0x000a:
        if (r4 == 0) goto L_0x00d9;
    L_0x000c:
        r11 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0103 }
        r11 = android.provider.DocumentsContract.isDocumentUri(r11, r14);	 Catch:{ Exception -> 0x0103 }
        if (r11 == 0) goto L_0x00d9;
    L_0x0014:
        r11 = isExternalStorageDocument(r14);	 Catch:{ Exception -> 0x0103 }
        if (r11 == 0) goto L_0x0053;
    L_0x001a:
        r1 = android.provider.DocumentsContract.getDocumentId(r14);	 Catch:{ Exception -> 0x0103 }
        r10 = ":";
        r7 = r1.split(r10);	 Catch:{ Exception -> 0x0103 }
        r10 = 0;
        r8 = r7[r10];	 Catch:{ Exception -> 0x0103 }
        r10 = "primary";
        r10 = r10.equalsIgnoreCase(r8);	 Catch:{ Exception -> 0x0103 }
        if (r10 == 0) goto L_0x0050;
    L_0x0031:
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0103 }
        r10.<init>();	 Catch:{ Exception -> 0x0103 }
        r11 = android.os.Environment.getExternalStorageDirectory();	 Catch:{ Exception -> 0x0103 }
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x0103 }
        r11 = "/";
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x0103 }
        r11 = 1;
        r11 = r7[r11];	 Catch:{ Exception -> 0x0103 }
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x0103 }
        r9 = r10.toString();	 Catch:{ Exception -> 0x0103 }
    L_0x0050:
        return r9;
    L_0x0051:
        r4 = r10;
        goto L_0x000a;
    L_0x0053:
        r11 = isDownloadsDocument(r14);	 Catch:{ Exception -> 0x0103 }
        if (r11 == 0) goto L_0x0079;
    L_0x0059:
        r3 = android.provider.DocumentsContract.getDocumentId(r14);	 Catch:{ Exception -> 0x0103 }
        r10 = "content://downloads/public_downloads";
        r10 = android.net.Uri.parse(r10);	 Catch:{ Exception -> 0x0103 }
        r11 = java.lang.Long.valueOf(r3);	 Catch:{ Exception -> 0x0103 }
        r12 = r11.longValue();	 Catch:{ Exception -> 0x0103 }
        r0 = android.content.ContentUris.withAppendedId(r10, r12);	 Catch:{ Exception -> 0x0103 }
        r10 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0103 }
        r11 = 0;
        r12 = 0;
        r9 = getDataColumn(r10, r0, r11, r12);	 Catch:{ Exception -> 0x0103 }
        goto L_0x0050;
    L_0x0079:
        r11 = isMediaDocument(r14);	 Catch:{ Exception -> 0x0103 }
        if (r11 == 0) goto L_0x0050;
    L_0x007f:
        r1 = android.provider.DocumentsContract.getDocumentId(r14);	 Catch:{ Exception -> 0x0103 }
        r11 = ":";
        r7 = r1.split(r11);	 Catch:{ Exception -> 0x0103 }
        r11 = 0;
        r8 = r7[r11];	 Catch:{ Exception -> 0x0103 }
        r0 = 0;
        r11 = -1;
        r13 = r8.hashCode();	 Catch:{ Exception -> 0x0103 }
        switch(r13) {
            case 93166550: goto L_0x00c5;
            case 100313435: goto L_0x00b0;
            case 112202875: goto L_0x00ba;
            default: goto L_0x0096;
        };	 Catch:{ Exception -> 0x0103 }
    L_0x0096:
        r10 = r11;
    L_0x0097:
        switch(r10) {
            case 0: goto L_0x00d0;
            case 1: goto L_0x00d3;
            case 2: goto L_0x00d6;
            default: goto L_0x009a;
        };	 Catch:{ Exception -> 0x0103 }
    L_0x009a:
        r5 = "_id=?";
        r10 = 1;
        r6 = new java.lang.String[r10];	 Catch:{ Exception -> 0x0103 }
        r10 = 0;
        r11 = 1;
        r11 = r7[r11];	 Catch:{ Exception -> 0x0103 }
        r6[r10] = r11;	 Catch:{ Exception -> 0x0103 }
        r10 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0103 }
        r11 = "_id=?";
        r9 = getDataColumn(r10, r0, r11, r6);	 Catch:{ Exception -> 0x0103 }
        goto L_0x0050;
    L_0x00b0:
        r12 = "image";
        r12 = r8.equals(r12);	 Catch:{ Exception -> 0x0103 }
        if (r12 == 0) goto L_0x0096;
    L_0x00b9:
        goto L_0x0097;
    L_0x00ba:
        r10 = "video";
        r10 = r8.equals(r10);	 Catch:{ Exception -> 0x0103 }
        if (r10 == 0) goto L_0x0096;
    L_0x00c3:
        r10 = r12;
        goto L_0x0097;
    L_0x00c5:
        r10 = "audio";
        r10 = r8.equals(r10);	 Catch:{ Exception -> 0x0103 }
        if (r10 == 0) goto L_0x0096;
    L_0x00ce:
        r10 = 2;
        goto L_0x0097;
    L_0x00d0:
        r0 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;	 Catch:{ Exception -> 0x0103 }
        goto L_0x009a;
    L_0x00d3:
        r0 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;	 Catch:{ Exception -> 0x0103 }
        goto L_0x009a;
    L_0x00d6:
        r0 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;	 Catch:{ Exception -> 0x0103 }
        goto L_0x009a;
    L_0x00d9:
        r10 = "content";
        r11 = r14.getScheme();	 Catch:{ Exception -> 0x0103 }
        r10 = r10.equalsIgnoreCase(r11);	 Catch:{ Exception -> 0x0103 }
        if (r10 == 0) goto L_0x00f0;
    L_0x00e6:
        r10 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0103 }
        r11 = 0;
        r12 = 0;
        r9 = getDataColumn(r10, r14, r11, r12);	 Catch:{ Exception -> 0x0103 }
        goto L_0x0050;
    L_0x00f0:
        r10 = "file";
        r11 = r14.getScheme();	 Catch:{ Exception -> 0x0103 }
        r10 = r10.equalsIgnoreCase(r11);	 Catch:{ Exception -> 0x0103 }
        if (r10 == 0) goto L_0x0050;
    L_0x00fd:
        r9 = r14.getPath();	 Catch:{ Exception -> 0x0103 }
        goto L_0x0050;
    L_0x0103:
        r2 = move-exception;
        org.telegram.messenger.FileLog.e(r2);
        goto L_0x0050;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.getPath(android.net.Uri):java.lang.String");
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
        } catch (Exception e) {
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
        return generatePicturePath(false);
    }

    public static File generatePicturePath(boolean secretChat) {
        try {
            File storageDir = getAlbumDir(secretChat);
            Date date = new Date();
            date.setTime((System.currentTimeMillis() + ((long) Utilities.random.nextInt(1000))) + 1);
            return new File(storageDir, "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(date) + ".jpg");
        } catch (Exception e) {
            FileLog.e(e);
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
            int idx = index - (index == 0 ? 0 : 1);
            int end = ((index == 0 ? 0 : 1) + q.length()) + idx;
            if (lastIndex != 0 && lastIndex != idx + 1) {
                builder.append(wholeString.substring(lastIndex, idx));
            } else if (lastIndex == 0 && idx != 0) {
                builder.append(wholeString.substring(0, idx));
            }
            String query = wholeString.substring(idx, Math.min(wholeString.length(), end));
            if (query.startsWith(" ")) {
                builder.append(" ");
            }
            query = query.trim();
            int start = builder.length();
            builder.append(query);
            builder.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteBlueText4")), start, query.length() + start, 33);
            lastIndex = end;
        }
        if (lastIndex == -1 || lastIndex >= wholeString.length()) {
            return builder;
        }
        builder.append(wholeString.substring(lastIndex, wholeString.length()));
        return builder;
    }

    public static boolean isAirplaneModeOn() {
        if (VERSION.SDK_INT < 17) {
            if (System.getInt(ApplicationLoader.applicationContext.getContentResolver(), "airplane_mode_on", 0) != 0) {
                return true;
            }
            return false;
        } else if (Global.getInt(ApplicationLoader.applicationContext.getContentResolver(), "airplane_mode_on", 0) == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static File generateVideoPath() {
        return generateVideoPath(false);
    }

    public static File generateVideoPath(boolean secretChat) {
        try {
            File storageDir = getAlbumDir(secretChat);
            Date date = new Date();
            date.setTime((System.currentTimeMillis() + ((long) Utilities.random.nextInt(1000))) + 1);
            return new File(storageDir, "VID_" + new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(date) + ".mp4");
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static String formatFileSize(long size) {
        return formatFileSize(size, false);
    }

    public static String formatFileSize(long size, boolean removeZero) {
        float value;
        if (size < 1024) {
            return String.format("%d B", new Object[]{Long.valueOf(size)});
        } else if (size < 1048576) {
            value = ((float) size) / 1024.0f;
            if (removeZero && (value - ((float) ((int) value))) * 10.0f == 0.0f) {
                return String.format("%d KB", new Object[]{Integer.valueOf((int) value)});
            }
            return String.format("%.1f KB", new Object[]{Float.valueOf(value)});
        } else if (size < NUM) {
            value = (((float) size) / 1024.0f) / 1024.0f;
            if (removeZero && (value - ((float) ((int) value))) * 10.0f == 0.0f) {
                return String.format("%d MB", new Object[]{Integer.valueOf((int) value)});
            }
            return String.format("%.1f MB", new Object[]{Float.valueOf(value)});
        } else {
            value = ((((float) size) / 1024.0f) / 1024.0f) / 1024.0f;
            if (removeZero && (value - ((float) ((int) value))) * 10.0f == 0.0f) {
                return String.format("%d GB", new Object[]{Integer.valueOf((int) value)});
            }
            return String.format("%.1f GB", new Object[]{Float.valueOf(value)});
        }
    }

    public static byte[] decodeQuotedPrintable(byte[] bytes) {
        byte[] array = null;
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
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                } else {
                    buffer.write(b);
                }
                i++;
            }
            array = buffer.toByteArray();
            try {
                buffer.close();
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        return array;
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

    /* JADX WARNING: Removed duplicated region for block: B:20:0x003f  */
    /* JADX WARNING: Removed duplicated region for block: B:38:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0044  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x004b  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0050  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x004b  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0050  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x003f  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0044  */
    /* JADX WARNING: Removed duplicated region for block: B:38:? A:{SYNTHETIC, RETURN} */
    public static boolean copyFile(java.io.File r11, java.io.File r12) throws java.io.IOException {
        /*
        r0 = r12.exists();
        if (r0 != 0) goto L_0x0009;
    L_0x0006:
        r12.createNewFile();
    L_0x0009:
        r9 = 0;
        r6 = 0;
        r10 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x0038 }
        r10.<init>(r11);	 Catch:{ Exception -> 0x0038 }
        r7 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x005b, all -> 0x0054 }
        r7.<init>(r12);	 Catch:{ Exception -> 0x005b, all -> 0x0054 }
        r0 = r7.getChannel();	 Catch:{ Exception -> 0x005e, all -> 0x0057 }
        r1 = r10.getChannel();	 Catch:{ Exception -> 0x005e, all -> 0x0057 }
        r2 = 0;
        r4 = r10.getChannel();	 Catch:{ Exception -> 0x005e, all -> 0x0057 }
        r4 = r4.size();	 Catch:{ Exception -> 0x005e, all -> 0x0057 }
        r0.transferFrom(r1, r2, r4);	 Catch:{ Exception -> 0x005e, all -> 0x0057 }
        if (r10 == 0) goto L_0x002f;
    L_0x002c:
        r10.close();
    L_0x002f:
        if (r7 == 0) goto L_0x0034;
    L_0x0031:
        r7.close();
    L_0x0034:
        r0 = 1;
        r6 = r7;
        r9 = r10;
    L_0x0037:
        return r0;
    L_0x0038:
        r8 = move-exception;
    L_0x0039:
        org.telegram.messenger.FileLog.e(r8);	 Catch:{ all -> 0x0048 }
        r0 = 0;
        if (r9 == 0) goto L_0x0042;
    L_0x003f:
        r9.close();
    L_0x0042:
        if (r6 == 0) goto L_0x0037;
    L_0x0044:
        r6.close();
        goto L_0x0037;
    L_0x0048:
        r0 = move-exception;
    L_0x0049:
        if (r9 == 0) goto L_0x004e;
    L_0x004b:
        r9.close();
    L_0x004e:
        if (r6 == 0) goto L_0x0053;
    L_0x0050:
        r6.close();
    L_0x0053:
        throw r0;
    L_0x0054:
        r0 = move-exception;
        r9 = r10;
        goto L_0x0049;
    L_0x0057:
        r0 = move-exception;
        r6 = r7;
        r9 = r10;
        goto L_0x0049;
    L_0x005b:
        r8 = move-exception;
        r9 = r10;
        goto L_0x0039;
    L_0x005e:
        r8 = move-exception;
        r6 = r7;
        r9 = r10;
        goto L_0x0039;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.copyFile(java.io.File, java.io.File):boolean");
    }

    public static byte[] calcAuthKeyHash(byte[] auth_key) {
        byte[] key_hash = new byte[16];
        System.arraycopy(Utilities.computeSHA1(auth_key), 0, key_hash, 0, 16);
        return key_hash;
    }

    public static void openDocument(MessageObject message, Activity activity, BaseFragment parentFragment) {
        Builder builder;
        if (message != null) {
            Document document = message.getDocument();
            if (document != null) {
                File f = null;
                String fileName = message.messageOwner.media != null ? FileLoader.getAttachFileName(document) : "";
                if (!(message.messageOwner.attachPath == null || message.messageOwner.attachPath.length() == 0)) {
                    f = new File(message.messageOwner.attachPath);
                }
                if (f == null || !(f == null || f.exists())) {
                    f = FileLoader.getPathToMessage(message.messageOwner);
                }
                if (f != null && f.exists()) {
                    if (parentFragment == null || !f.getName().toLowerCase().endsWith("attheme")) {
                        String realMimeType = null;
                        try {
                            Intent intent = new Intent("android.intent.action.VIEW");
                            intent.setFlags(1);
                            MimeTypeMap myMime = MimeTypeMap.getSingleton();
                            int idx = fileName.lastIndexOf(46);
                            if (idx != -1) {
                                realMimeType = myMime.getMimeTypeFromExtension(fileName.substring(idx + 1).toLowerCase());
                                if (realMimeType == null) {
                                    realMimeType = document.mime_type;
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
                            return;
                        } catch (Exception e2) {
                            if (activity != null) {
                                builder = new Builder((Context) activity);
                                builder.setTitle(LocaleController.getString("AppName", NUM));
                                builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                                builder.setMessage(LocaleController.formatString("NoHandleAppInstalled", NUM, message.getDocument().mime_type));
                                if (parentFragment != null) {
                                    parentFragment.showDialog(builder.create());
                                    return;
                                } else {
                                    builder.show();
                                    return;
                                }
                            }
                            return;
                        }
                    }
                    ThemeInfo themeInfo = Theme.applyThemeFile(f, message.getDocumentName(), true);
                    if (themeInfo != null) {
                        parentFragment.presentFragment(new ThemePreviewActivity(f, themeInfo));
                        return;
                    }
                    builder = new Builder((Context) activity);
                    builder.setTitle(LocaleController.getString("AppName", NUM));
                    builder.setMessage(LocaleController.getString("IncorrectTheme", NUM));
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                    parentFragment.showDialog(builder.create());
                }
            }
        }
    }

    public static void openForView(MessageObject message, Activity activity) {
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
            if (VERSION.SDK_INT < 26 || realMimeType == null || !realMimeType.equals("application/vnd.android.package-archive") || ApplicationLoader.applicationContext.getPackageManager().canRequestPackageInstalls()) {
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
                return;
            }
            Builder builder = new Builder((Context) activity);
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.getString("ApkRestricted", NUM));
            builder.setPositiveButton(LocaleController.getString("PermissionOpenSettings", NUM), new AndroidUtilities$$Lambda$2(activity));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            builder.show();
        }
    }

    static final /* synthetic */ void lambda$openForView$2$AndroidUtilities(Activity activity, DialogInterface dialogInterface, int i) {
        try {
            activity.startActivity(new Intent("android.settings.MANAGE_UNKNOWN_APP_SOURCES", Uri.parse("package:" + activity.getPackageName())));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void openForView(TLObject media, Activity activity) {
        if (media != null && activity != null) {
            String fileName = FileLoader.getAttachFileName(media);
            File f = FileLoader.getPathToAttach(media, true);
            if (f != null && f.exists()) {
                String realMimeType = null;
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setFlags(1);
                MimeTypeMap myMime = MimeTypeMap.getSingleton();
                int idx = fileName.lastIndexOf(46);
                if (idx != -1) {
                    realMimeType = myMime.getMimeTypeFromExtension(fileName.substring(idx + 1).toLowerCase());
                    if (realMimeType == null) {
                        if (media instanceof TL_document) {
                            realMimeType = ((TL_document) media).mime_type;
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
    }

    public static boolean isBannedForever(TL_chatBannedRights rights) {
        return rights == null || Math.abs(((long) rights.until_date) - (System.currentTimeMillis() / 1000)) > NUM;
    }

    public static void setRectToRect(Matrix matrix, RectF src, RectF dst, int rotation, boolean translate) {
        float sx;
        float sy;
        float tx;
        float ty;
        float diff;
        boolean xLarger = false;
        if (rotation == 90 || rotation == 270) {
            sx = dst.height() / src.width();
            sy = dst.width() / src.height();
        } else {
            sx = dst.width() / src.width();
            sy = dst.height() / src.height();
        }
        if (sx < sy) {
            sx = sy;
            xLarger = true;
        } else {
            sy = sx;
        }
        if (translate) {
            matrix.setTranslate(dst.left, dst.top);
        }
        if (rotation == 90) {
            matrix.preRotate(90.0f);
            matrix.preTranslate(0.0f, -dst.width());
        } else if (rotation == 180) {
            matrix.preRotate(180.0f);
            matrix.preTranslate(-dst.width(), -dst.height());
        } else if (rotation == 270) {
            matrix.preRotate(270.0f);
            matrix.preTranslate(-dst.height(), 0.0f);
        }
        if (translate) {
            tx = (-src.left) * sx;
            ty = (-src.top) * sy;
        } else {
            tx = dst.left - (src.left * sx);
            ty = dst.top - (src.top * sy);
        }
        if (xLarger) {
            diff = dst.width() - (src.width() * sy);
        } else {
            diff = dst.height() - (src.height() * sy);
        }
        diff /= 2.0f;
        if (xLarger) {
            tx += diff;
        } else {
            ty += diff;
        }
        matrix.preScale(sx, sy);
        if (translate) {
            matrix.preTranslate(tx, ty);
        }
    }

    public static boolean handleProxyIntent(Activity activity, Intent intent) {
        if (intent == null) {
            return false;
        }
        try {
            if ((intent.getFlags() & 1048576) != 0) {
                return false;
            }
            Uri data = intent.getData();
            if (data != null) {
                String user = null;
                String password = null;
                String port = null;
                String address = null;
                String secret = null;
                String scheme = data.getScheme();
                if (scheme != null) {
                    if (scheme.equals("http") || scheme.equals("https")) {
                        String host = data.getHost().toLowerCase();
                        if (host.equals("telegram.me") || host.equals("t.me") || host.equals("telegram.dog")) {
                            String path = data.getPath();
                            if (path != null && (path.startsWith("/socks") || path.startsWith("/proxy"))) {
                                address = data.getQueryParameter("server");
                                port = data.getQueryParameter("port");
                                user = data.getQueryParameter("user");
                                password = data.getQueryParameter("pass");
                                secret = data.getQueryParameter("secret");
                            }
                        }
                    } else if (scheme.equals("tg")) {
                        String url = data.toString();
                        if (url.startsWith("tg:proxy") || url.startsWith("tg://proxy") || url.startsWith("tg:socks") || url.startsWith("tg://socks")) {
                            data = Uri.parse(url.replace("tg:proxy", "tg://telegram.org").replace("tg://proxy", "tg://telegram.org").replace("tg://socks", "tg://telegram.org").replace("tg:socks", "tg://telegram.org"));
                            address = data.getQueryParameter("server");
                            port = data.getQueryParameter("port");
                            user = data.getQueryParameter("user");
                            password = data.getQueryParameter("pass");
                            secret = data.getQueryParameter("secret");
                        }
                    }
                }
                if (!(TextUtils.isEmpty(address) || TextUtils.isEmpty(port))) {
                    if (user == null) {
                        user = "";
                    }
                    if (password == null) {
                        password = "";
                    }
                    if (secret == null) {
                        secret = "";
                    }
                    showProxyAlert(activity, address, port, user, password, secret);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
        }
    }

    public static boolean shouldEnableAnimation() {
        if (VERSION.SDK_INT < 26 || VERSION.SDK_INT >= 28) {
            return true;
        }
        if (((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).isPowerSaveMode() || Global.getFloat(ApplicationLoader.applicationContext.getContentResolver(), "animator_duration_scale", 1.0f) <= 0.0f) {
            return false;
        }
        return true;
    }

    public static void showProxyAlert(Activity activity, String address, String port, String user, String password, String secret) {
        View textView;
        BottomSheet.Builder builder = new BottomSheet.Builder(activity);
        Runnable dismissRunnable = builder.getDismissRunnable();
        builder.setApplyTopPadding(false);
        builder.setApplyBottomPadding(false);
        LinearLayout linearLayout = new LinearLayout(activity);
        builder.setCustomView(linearLayout);
        linearLayout.setOrientation(1);
        if (!TextUtils.isEmpty(secret)) {
            textView = new TextView(activity);
            textView.setText(LocaleController.getString("UseProxyTelegramInfo2", NUM));
            textView.setTextColor(Theme.getColor("dialogTextGray4"));
            textView.setTextSize(1, 14.0f);
            textView.setGravity(49);
            linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 17, 8, 17, 8));
            View lineView = new View(activity);
            lineView.setBackgroundColor(Theme.getColor("divider"));
            linearLayout.addView(lineView, new LayoutParams(-1, 1));
        }
        for (int a = 0; a < 5; a++) {
            String text = null;
            String detail = null;
            if (a == 0) {
                text = address;
                detail = LocaleController.getString("UseProxyAddress", NUM);
            } else if (a == 1) {
                text = "" + port;
                detail = LocaleController.getString("UseProxyPort", NUM);
            } else if (a == 2) {
                text = secret;
                detail = LocaleController.getString("UseProxySecret", NUM);
            } else if (a == 3) {
                text = user;
                detail = LocaleController.getString("UseProxyUsername", NUM);
            } else if (a == 4) {
                text = password;
                detail = LocaleController.getString("UseProxyPassword", NUM);
            }
            if (!TextUtils.isEmpty(text)) {
                TextDetailSettingsCell cell = new TextDetailSettingsCell(activity);
                cell.setTextAndValue(text, detail, true);
                cell.getTextView().setTextColor(Theme.getColor("dialogTextBlack"));
                cell.getValueTextView().setTextColor(Theme.getColor("dialogTextGray3"));
                linearLayout.addView(cell, LayoutHelper.createLinear(-1, -2));
                if (a == 2) {
                    break;
                }
            }
        }
        textView = new PickerBottomLayout(activity, false);
        textView.setBackgroundColor(Theme.getColor("dialogBackground"));
        linearLayout.addView(textView, LayoutHelper.createFrame(-1, 48, 83));
        textView.cancelButton.setPadding(dp(18.0f), 0, dp(18.0f), 0);
        textView.cancelButton.setTextColor(Theme.getColor("dialogTextBlue2"));
        textView.cancelButton.setText(LocaleController.getString("Cancel", NUM).toUpperCase());
        textView.cancelButton.setOnClickListener(new AndroidUtilities$$Lambda$3(dismissRunnable));
        textView.doneButtonTextView.setTextColor(Theme.getColor("dialogTextBlue2"));
        textView.doneButton.setPadding(dp(18.0f), 0, dp(18.0f), 0);
        textView.doneButtonBadgeTextView.setVisibility(8);
        textView.doneButtonTextView.setText(LocaleController.getString("ConnectingConnectProxy", NUM).toUpperCase());
        textView.doneButton.setOnClickListener(new AndroidUtilities$$Lambda$4(address, port, secret, password, user, dismissRunnable));
        builder.show();
    }

    static final /* synthetic */ void lambda$showProxyAlert$4$AndroidUtilities(String address, String port, String secret, String password, String user, Runnable dismissRunnable, View v) {
        ProxyInfo info;
        Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("proxy_enabled", true);
        editor.putString("proxy_ip", address);
        int p = Utilities.parseInt(port).intValue();
        editor.putInt("proxy_port", p);
        if (TextUtils.isEmpty(secret)) {
            editor.remove("proxy_secret");
            if (TextUtils.isEmpty(password)) {
                editor.remove("proxy_pass");
            } else {
                editor.putString("proxy_pass", password);
            }
            if (TextUtils.isEmpty(user)) {
                editor.remove("proxy_user");
            } else {
                editor.putString("proxy_user", user);
            }
            info = new ProxyInfo(address, p, user, password, "");
        } else {
            editor.remove("proxy_pass");
            editor.remove("proxy_user");
            editor.putString("proxy_secret", secret);
            info = new ProxyInfo(address, p, "", "", secret);
        }
        editor.commit();
        SharedConfig.currentProxy = SharedConfig.addProxy(info);
        ConnectionsManager.setProxySettings(true, address, p, user, password, secret);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
        dismissRunnable.run();
    }

    public static String getSystemProperty(String key) {
        try {
            return (String) Class.forName("android.os.SystemProperties").getMethod("get", new Class[]{String.class}).invoke(null, new Object[]{key});
        } catch (Exception e) {
            return null;
        }
    }

    public static CharSequence concat(CharSequence... text) {
        int i = 0;
        if (text.length == 0) {
            return "";
        }
        if (text.length == 1) {
            return text[0];
        }
        int length;
        CharSequence piece;
        boolean spanned = false;
        for (CharSequence piece2 : text) {
            if (piece2 instanceof Spanned) {
                spanned = true;
                break;
            }
        }
        if (spanned) {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            length = text.length;
            while (i < length) {
                piece2 = text[i];
                if (piece2 == null) {
                    piece2 = "null";
                }
                ssb.append(piece2);
                i++;
            }
            return new SpannedString(ssb);
        }
        StringBuilder sb = new StringBuilder();
        length = text.length;
        while (i < length) {
            sb.append(text[i]);
            i++;
        }
        return sb.toString();
    }

    public static float[] RGBtoHSB(int r, int g, int b) {
        int cmax;
        int cmin;
        float saturation;
        float hue;
        float[] hsbvals = new float[3];
        if (r > g) {
            cmax = r;
        } else {
            cmax = g;
        }
        if (b > cmax) {
            cmax = b;
        }
        if (r < g) {
            cmin = r;
        } else {
            cmin = g;
        }
        if (b < cmin) {
            cmin = b;
        }
        float brightness = ((float) cmax) / 255.0f;
        if (cmax != 0) {
            saturation = ((float) (cmax - cmin)) / ((float) cmax);
        } else {
            saturation = 0.0f;
        }
        if (saturation == 0.0f) {
            hue = 0.0f;
        } else {
            float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
            float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
            float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
            if (r == cmax) {
                hue = bluec - greenc;
            } else if (g == cmax) {
                hue = (2.0f + redc) - bluec;
            } else {
                hue = (4.0f + greenc) - redc;
            }
            hue /= 6.0f;
            if (hue < 0.0f) {
                hue += 1.0f;
            }
        }
        hsbvals[0] = hue;
        hsbvals[1] = saturation;
        hsbvals[2] = brightness;
        return hsbvals;
    }

    public static int HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0;
        int g = 0;
        int b = 0;
        if (saturation != 0.0f) {
            float h = (hue - ((float) Math.floor((double) hue))) * 6.0f;
            float f = h - ((float) Math.floor((double) h));
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - (saturation * f));
            float t = brightness * (1.0f - ((1.0f - f) * saturation));
            switch ((int) h) {
                case 0:
                    r = (int) ((brightness * 255.0f) + 0.5f);
                    g = (int) ((t * 255.0f) + 0.5f);
                    b = (int) ((p * 255.0f) + 0.5f);
                    break;
                case 1:
                    r = (int) ((q * 255.0f) + 0.5f);
                    g = (int) ((brightness * 255.0f) + 0.5f);
                    b = (int) ((p * 255.0f) + 0.5f);
                    break;
                case 2:
                    r = (int) ((p * 255.0f) + 0.5f);
                    g = (int) ((brightness * 255.0f) + 0.5f);
                    b = (int) ((t * 255.0f) + 0.5f);
                    break;
                case 3:
                    r = (int) ((p * 255.0f) + 0.5f);
                    g = (int) ((q * 255.0f) + 0.5f);
                    b = (int) ((brightness * 255.0f) + 0.5f);
                    break;
                case 4:
                    r = (int) ((t * 255.0f) + 0.5f);
                    g = (int) ((p * 255.0f) + 0.5f);
                    b = (int) ((brightness * 255.0f) + 0.5f);
                    break;
                case 5:
                    r = (int) ((brightness * 255.0f) + 0.5f);
                    g = (int) ((p * 255.0f) + 0.5f);
                    b = (int) ((q * 255.0f) + 0.5f);
                    break;
            }
        }
        b = (int) ((brightness * 255.0f) + 0.5f);
        g = b;
        r = b;
        return ((-16777216 | ((r & 255) << 16)) | ((g & 255) << 8)) | (b & 255);
    }

    public static int getPatternColor(int color) {
        float[] hsb = RGBtoHSB(Color.red(color), Color.green(color), Color.blue(color));
        if (hsb[1] > 0.0f || (hsb[2] < 1.0f && hsb[2] > 0.0f)) {
            hsb[1] = Math.min(1.0f, (hsb[1] + 0.05f) + (0.1f * (1.0f - hsb[1])));
        }
        if (hsb[2] > 0.5f) {
            hsb[2] = Math.max(0.0f, hsb[2] * 0.65f);
        } else {
            hsb[2] = Math.max(0.0f, Math.min(1.0f, 1.0f - (hsb[2] * 0.65f)));
        }
        return HSBtoRGB(hsb[0], hsb[1], hsb[2]) & NUM;
    }

    public static int getPatternSideColor(int color) {
        float[] hsb = RGBtoHSB(Color.red(color), Color.green(color), Color.blue(color));
        hsb[1] = Math.min(1.0f, hsb[1] + 0.05f);
        if (hsb[2] > 0.5f) {
            hsb[2] = Math.max(0.0f, hsb[2] * 0.9f);
        } else {
            hsb[2] = Math.max(0.0f, hsb[2] * 0.9f);
        }
        return HSBtoRGB(hsb[0], hsb[1], hsb[2]) | -16777216;
    }

    public static String getWallPaperUrl(Object object, int currentAccount) {
        if (object instanceof TL_wallPaper) {
            TL_wallPaper wallPaper = (TL_wallPaper) object;
            String link = "https://" + MessagesController.getInstance(currentAccount).linkPrefix + "/bg/" + wallPaper.slug;
            StringBuilder modes = new StringBuilder();
            if (wallPaper.settings != null) {
                if (wallPaper.settings.blur) {
                    modes.append("blur");
                }
                if (wallPaper.settings.motion) {
                    if (modes.length() > 0) {
                        modes.append("+");
                    }
                    modes.append("motion");
                }
            }
            return modes.length() > 0 ? link + "?mode=" + modes.toString() : link;
        } else if (!(object instanceof ColorWallpaper)) {
            return null;
        } else {
            ColorWallpaper wallPaper2 = (ColorWallpaper) object;
            String color = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (wallPaper2.color >> 16)) & 255), Integer.valueOf(((byte) (wallPaper2.color >> 8)) & 255), Byte.valueOf((byte) (wallPaper2.color & 255))}).toLowerCase();
            if (wallPaper2.pattern != null) {
                return "https://" + MessagesController.getInstance(currentAccount).linkPrefix + "/bg/" + wallPaper2.pattern.slug + "?intensity=" + ((int) (wallPaper2.intensity * 100.0f)) + "&bg_color=" + color;
            }
            return "https://" + MessagesController.getInstance(currentAccount).linkPrefix + "/bg/" + color;
        }
    }

    public static float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((double) ((f - 0.5f) * 0.47123894f));
    }

    public static void makeAccessibilityAnnouncement(CharSequence what) {
        AccessibilityManager am = (AccessibilityManager) ApplicationLoader.applicationContext.getSystemService("accessibility");
        if (am.isEnabled()) {
            AccessibilityEvent ev = AccessibilityEvent.obtain();
            ev.setEventType(16384);
            ev.getText().add(what);
            am.sendAccessibilityEvent(ev);
        }
    }
}
