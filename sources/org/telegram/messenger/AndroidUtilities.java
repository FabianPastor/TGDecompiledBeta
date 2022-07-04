package org.telegram.messenger;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
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
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.CallLog;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.StateSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.android.internal.telephony.ITelephony;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.crashes.Crashes;
import com.microsoft.appcenter.distribute.Distribute;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.ForegroundColorSpanThemable;
import org.telegram.ui.Components.ForegroundDetector;
import org.telegram.ui.Components.HideViewAfterAnimation;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.PickerBottomLayout;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.ThemePreviewActivity;
import org.telegram.ui.WallpapersListActivity;

public class AndroidUtilities {
    public static Pattern BAD_CHARS_MESSAGE_LONG_PATTERN = null;
    public static Pattern BAD_CHARS_MESSAGE_PATTERN = null;
    public static Pattern BAD_CHARS_PATTERN = null;
    public static final int DARK_STATUS_BAR_OVERLAY = NUM;
    public static final int FLAG_TAG_ALL = 11;
    public static final int FLAG_TAG_BOLD = 2;
    public static final int FLAG_TAG_BR = 1;
    public static final int FLAG_TAG_COLOR = 4;
    public static final int FLAG_TAG_URL = 8;
    public static final int LIGHT_STATUS_BAR_OVERLAY = NUM;
    public static final String STICKERS_PLACEHOLDER_PACK_NAME = "tg_placeholders_android";
    public static final String TYPEFACE_ROBOTO_MEDIUM = "fonts/rmedium.ttf";
    public static Pattern WEB_URL;
    public static AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
    private static AccessibilityManager accessibilityManager;
    private static int adjustOwnerClassGuid = 0;
    private static int altFocusableClassGuid = 0;
    private static RectF bitmapRect;
    private static final Object callLock = new Object();
    private static CallReceiver callReceiver;
    private static char[] characters = {160, ' ', '!', '\"', '#', '%', '&', '\'', '(', ')', '*', ',', '-', '.', '/', ':', ';', '?', '@', '[', '\\', ']', '_', '{', '}', 161, 167, 171, 182, 183, 187, 191, 894, 903, 1370, 1371, 1372, 1373, 1374, 1375, 1417, 1418, 1470, 1472, 1475, 1478, 1523, 1524, 1545, 1546, 1548, 1549, 1563, 1566, 1567, 1642, 1643, 1644, 1645, 1748, 1792, 1793, 1794, 1795, 1796, 1797, 1798, 1799, 1800, 1801, 1802, 1803, 1804, 1805, 2039, 2040, 2041, 2096, 2097, 2098, 2099, 2100, 2101, 2102, 2103, 2104, 2105, 2106, 2107, 2108, 2109, 2110, 2142, 2404, 2405, 2416, 2557, 2678, 2800, 3191, 3204, 3572, 3663, 3674, 3675, 3844, 3845, 3846, 3847, 3848, 3849, 3850, 3851, 3852, 3853, 3854, 3855, 3856, 3857, 3858, 3860, 3898, 3899, 3900, 3901, 3973, 4048, 4049, 4050, 4051, 4052, 4057, 4058, 4170, 4171, 4172, 4173, 4174, 4175, 4347, 4960, 4961, 4962, 4963, 4964, 4965, 4966, 4967, 4968, 5120, 5742, 5787, 5788, 5867, 5868, 5869, 5941, 5942, 6100, 6101, 6102, 6104, 6105, 6106, 6144, 6145, 6146, 6147, 6148, 6149, 6150, 6151, 6152, 6153, 6154, 6468, 6469, 6686, 6687, 6816, 6817, 6818, 6819, 6820, 6821, 6822, 6824, 6825, 6826, 6827, 6828, 6829, 7002, 7003, 7004, 7005, 7006, 7007, 7008, 7164, 7165, 7166, 7167, 7227, 7228, 7229, 7230, 7231, 7294, 7295, 7360, 7361, 7362, 7363, 7364, 7365, 7366, 7367, 7379, 8208, 8209, 8210, 8211, 8212, 8213, 8214, 8215, 8216, 8217, 8218, 8219, 8220, 8221, 8222, 8223, 8224, 8225, 8226, 8227, 8228, 8229, 8230, 8231, 8240, 8241, 8242, 8243, 8244, 8245, 8246, 8247, 8248, 8249, 8250, 8251, 8252, 8253, 8254, 8255, 8256, 8257, 8258, 8259, 8261, 8262, 8263, 8264, 8265, 8266, 8267, 8268, 8269, 8270, 8271, 8272, 8273, 8275, 8276, 8277, 8278, 8279, 8280, 8281, 8282, 8283, 8284, 8285, 8286, 8317, 8318, 8333, 8334, 8968, 8969, 8970, 8971, 9001, 9002, 10088, 10089, 10090, 10091, 10092, 10093, 10094, 10095, 10096, 10097, 10098, 10099, 10100, 10101, 10181, 10182, 10214, 10215, 10216, 10217, 10218, 10219, 10220, 10221, 10222, 10223, 10627, 10628, 10629, 10630, 10631, 10632, 10633, 10634, 10635, 10636, 10637, 10638, 10639, 10640, 10641, 10642, 10643, 10644, 10645, 10646, 10647, 10648, 10712, 10713, 10714, 10715, 10748, 10749, 11513, 11514, 11515, 11516, 11518, 11519, 11632, 11776, 11777, 11778, 11779, 11780, 11781, 11782, 11783, 11784, 11785, 11786, 11787, 11788, 11789, 11790, 11791, 11792, 11793, 11794, 11795, 11796, 11797, 11798, 11799, 11800, 11801, 11802, 11803, 11804, 11805, 11806, 11807, 11808, 11809, 11810, 11811, 11812, 11813, 11814, 11815, 11816, 11817, 11818, 11819, 11820, 11821, 11822, 11824, 11825, 11826, 11827, 11828, 11829, 11830, 11831, 11832, 11833, 11834, 11835, 11836, 11837, 11838, 11839, 11840, 11841, 11842, 11843, 11844, 11845, 11846, 11847, 11848, 11849, 11850, 11851, 11852, 11853, 11854, 11855, 12289, 12290, 12291, 12296, 12297, 12298, 12299, 12300, 12301, 12302, 12303, 12304, 12305, 12308, 12309, 12310, 12311, 12312, 12313, 12314, 12315, 12316, 12317, 12318, 12319, 12336, 12349, 12448, 12539, 42238, 42239, 42509, 42510, 42511, 42611, 42622, 42738, 42739, 42740, 42741, 42742, 42743, 43124, 43125, 43126, 43127, 43214, 43215, 43256, 43257, 43258, 43260, 43310, 43311, 43359, 43457, 43458, 43459, 43460, 43461, 43462, 43463, 43464, 43465, 43466, 43467, 43468, 43469, 43486, 43487, 43612, 43613, 43614, 43615, 43742, 43743, 43760, 43761, 44011, 64830, 64831, 65040, 65041, 65042, 65043, 65044, 65045, 65046, 65047, 65048, 65049, 65072, 65073, 65074, 65075, 65076, 65077, 65078, 65079, 65080, 65081, 65082, 65083, 65084, 65085, 65086, 65087, 65088, 65089, 65090, 65091, 65092, 65093, 65094, 65095, 65096, 65097, 65098, 65099, 65100, 65101, 65102, 65103, 65104, 65105, 65106, 65108, 65109, 65110, 65111, 65112, 65113, 65114, 65115, 65116, 65117, 65118, 65119, 65120, 65121, 65123, 65128, 65130, 65131, 65281, 65282, 65283, 65285, 65286, 65287, 65288, 65289, 65290, 65292, 65293, 65294, 65295, 65306, 65307, 65311, 65312, 65339, 65340, 65341, 65343, 65371, 65373, 65375, 65376, 65377, 65378, 65379, 65380, 65381};
    private static HashSet<Character> charactersMap;
    public static DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    public static float density = 1.0f;
    public static DisplayMetrics displayMetrics = new DisplayMetrics();
    public static Point displaySize = new Point();
    private static int[] documentIcons = {NUM, NUM, NUM, NUM};
    private static int[] documentMediaIcons = {NUM, NUM, NUM, NUM};
    public static boolean firstConfigurationWas;
    private static WeakReference<BaseFragment> flagSecureFragment;
    private static final HashMap<Window, ArrayList<Long>> flagSecureReasons = new HashMap<>();
    private static SimpleDateFormat generatingVideoPathFormat;
    private static boolean hasCallPermissions = (Build.VERSION.SDK_INT >= 23);
    public static boolean incorrectDisplaySizeFix;
    public static boolean isInMultiwindow;
    private static Boolean isSmallScreen = null;
    private static Boolean isTablet = null;
    private static long lastUpdateCheckTime;
    public static int leftBaseline = (isTablet() ? 80 : 72);
    private static Field mAttachInfoField;
    private static Field mStableInsetsField;
    /* access modifiers changed from: private */
    public static HashMap<Window, ValueAnimator> navigationBarColorAnimators;
    public static int navigationBarHeight = 0;
    public static final String[] numbersSignatureArray = {"", "K", "M", "G", "T", "P"};
    public static OvershootInterpolator overshootInterpolator = new OvershootInterpolator();
    public static Integer photoSize = null;
    private static int prevOrientation = -10;
    public static final RectF rectTmp = new RectF();
    public static final Rect rectTmp2 = new Rect();
    public static int roundMessageInset;
    public static int roundMessageSize;
    private static Paint roundPaint;
    public static int roundPlayingMessageSize;
    public static final Linkify.MatchFilter sUrlMatchFilter = AndroidUtilities$$ExternalSyntheticLambda8.INSTANCE;
    public static float screenRefreshRate = 60.0f;
    private static Pattern singleTagPatter = null;
    private static final Object smsLock = new Object();
    public static int statusBarHeight = 0;
    public static float touchSlop;
    private static final Hashtable<String, Typeface> typefaceCache = new Hashtable<>();
    private static Runnable unregisterRunnable;
    public static boolean usingHardwareInput;
    private static Vibrator vibrator;
    private static boolean waitingForCall = false;
    private static boolean waitingForSms = false;

    public interface IntColorCallback {
        void run(int i);
    }

    static {
        WEB_URL = null;
        BAD_CHARS_PATTERN = null;
        BAD_CHARS_MESSAGE_PATTERN = null;
        BAD_CHARS_MESSAGE_LONG_PATTERN = null;
        Object obj = "a-zA-Z0-9 -퟿豈-﷏ﷰ-￯";
        try {
            BAD_CHARS_PATTERN = Pattern.compile("[─-◿]");
            BAD_CHARS_MESSAGE_LONG_PATTERN = Pattern.compile("[̀-ͯ⁦-⁧]+");
            BAD_CHARS_MESSAGE_PATTERN = Pattern.compile("[⁦-⁧]+");
            Pattern IP_ADDRESS = Pattern.compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9]))");
            Pattern DOMAIN_NAME = Pattern.compile("(([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯\\-]{0,61}[a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]){0,1}\\.)+[a-zA-Z -퟿豈-﷏ﷰ-￯]{2,63}|" + IP_ADDRESS + ")");
            WEB_URL = Pattern.compile("((?:(http|https|Http|Https|ton|tg):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?(?:" + DOMAIN_NAME + ")(?:\\:\\d{1,5})?)(\\/(?:(?:[" + "a-zA-Z0-9 -퟿豈-﷏ﷰ-￯" + "\\;\\/\\?\\:\\@\\&\\=\\#\\~\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?(?:\\b|$)");
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        checkDisplaySize(ApplicationLoader.applicationContext, (Configuration) null);
    }

    private static boolean containsUnsupportedCharacters(String text) {
        if (text.contains("‬") || text.contains("‭") || text.contains("‮")) {
            return true;
        }
        try {
            if (BAD_CHARS_PATTERN.matcher(text).find()) {
                return true;
            }
            return false;
        } catch (Throwable th) {
            return true;
        }
    }

    public static String getSafeString(String str) {
        try {
            return BAD_CHARS_MESSAGE_PATTERN.matcher(str).replaceAll("‌");
        } catch (Throwable th) {
            return str;
        }
    }

    public static CharSequence ellipsizeCenterEnd(CharSequence str, String query, int availableWidth, TextPaint textPaint, int maxSymbols) {
        CharSequence str2;
        CharSequence charSequence;
        int startHighlightedIndex;
        CharSequence sub;
        CharSequence str3;
        int i = availableWidth;
        TextPaint textPaint2 = textPaint;
        int i2 = maxSymbols;
        try {
            int lastIndex = str.length();
            try {
                int startHighlightedIndex2 = str.toString().toLowerCase().indexOf(query);
                if (lastIndex > i2) {
                    charSequence = str;
                    try {
                        str3 = charSequence.subSequence(Math.max(0, startHighlightedIndex2 - (i2 / 2)), Math.min(lastIndex, (i2 / 2) + startHighlightedIndex2));
                    } catch (Exception e) {
                        e = e;
                        str2 = charSequence;
                        FileLog.e((Throwable) e);
                        return str2;
                    }
                    try {
                        int startHighlightedIndex3 = startHighlightedIndex2 - Math.max(0, startHighlightedIndex2 - (i2 / 2));
                        int length = str3.length();
                        startHighlightedIndex = startHighlightedIndex3;
                        str2 = str3;
                    } catch (Exception e2) {
                        e = e2;
                        str2 = str3;
                        FileLog.e((Throwable) e);
                        return str2;
                    }
                } else {
                    int i3 = lastIndex;
                    startHighlightedIndex = startHighlightedIndex2;
                    str2 = str;
                }
            } catch (Exception e3) {
                e = e3;
                charSequence = str;
                str2 = charSequence;
                FileLog.e((Throwable) e);
                return str2;
            }
            try {
                StaticLayout staticLayout = new StaticLayout(str2, textPaint, Integer.MAX_VALUE, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                float endOfTextX = staticLayout.getLineWidth(0);
                if (textPaint2.measureText("...") + endOfTextX < ((float) i)) {
                    return str2;
                }
                int i4 = startHighlightedIndex + 1;
                while (i4 < str2.length() - 1 && !Character.isWhitespace(str2.charAt(i4))) {
                    i4++;
                }
                int endHighlightedIndex = i4;
                float endOfHighlight = staticLayout.getPrimaryHorizontal(endHighlightedIndex);
                if (staticLayout.isRtlCharAt(endHighlightedIndex)) {
                    endOfHighlight = endOfTextX - endOfHighlight;
                }
                if (endOfHighlight < ((float) i)) {
                    return str2;
                }
                float x = (endOfHighlight - ((float) i)) + (textPaint2.measureText("...") * 2.0f) + (((float) i) * 0.1f);
                if (str2.length() - endHighlightedIndex > 20) {
                    x += ((float) i) * 0.1f;
                }
                if (x > 0.0f) {
                    int charOf = staticLayout.getOffsetForHorizontal(0, x);
                    int k = 0;
                    if (charOf > str2.length() - 1) {
                        charOf = str2.length() - 1;
                    }
                    while (true) {
                        float endOfTextX2 = endOfTextX;
                        if (Character.isWhitespace(str2.charAt(charOf)) || k >= 10) {
                            break;
                        }
                        k++;
                        charOf++;
                        if (charOf > str2.length() - 1) {
                            charOf = staticLayout.getOffsetForHorizontal(0, x);
                            break;
                        }
                        endOfTextX = endOfTextX2;
                    }
                    if (k >= 10) {
                        sub = str2.subSequence(staticLayout.getOffsetForHorizontal(0, staticLayout.getPrimaryHorizontal(startHighlightedIndex + 1) - (((float) i) * 0.3f)), str2.length());
                    } else {
                        if (charOf > 0 && charOf < str2.length() - 2 && Character.isWhitespace(str2.charAt(charOf))) {
                            charOf++;
                        }
                        sub = str2.subSequence(charOf, str2.length());
                    }
                    return SpannableStringBuilder.valueOf("...").append(sub);
                }
                return str2;
            } catch (Exception e4) {
                e = e4;
                FileLog.e((Throwable) e);
                return str2;
            }
        } catch (Exception e5) {
            e = e5;
            charSequence = str;
            String str4 = query;
            str2 = charSequence;
            FileLog.e((Throwable) e);
            return str2;
        }
    }

    public static CharSequence highlightText(CharSequence str, ArrayList<String> query, Theme.ResourcesProvider resourcesProvider) {
        if (query == null) {
            return null;
        }
        int emptyCount = 0;
        for (int i = 0; i < query.size(); i++) {
            CharSequence strTmp = highlightText(str, query.get(i), resourcesProvider);
            if (strTmp != null) {
                str = strTmp;
            } else {
                emptyCount++;
            }
        }
        if (emptyCount == query.size()) {
            return null;
        }
        return str;
    }

    public static CharSequence highlightText(CharSequence str, String query, Theme.ResourcesProvider resourcesProvider) {
        if (TextUtils.isEmpty(query) || TextUtils.isEmpty(str)) {
            return null;
        }
        String s = str.toString().toLowerCase();
        SpannableStringBuilder spannableStringBuilder = SpannableStringBuilder.valueOf(str);
        int i = s.indexOf(query);
        while (i >= 0) {
            try {
                spannableStringBuilder.setSpan(new ForegroundColorSpanThemable("windowBackgroundWhiteBlueText4", resourcesProvider), i, Math.min(query.length() + i, str.length()), 0);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            i = s.indexOf(query, i + 1);
        }
        return spannableStringBuilder;
    }

    public static Activity findActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            return findActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    public static CharSequence replaceSingleTag(String str, final Runnable runnable) {
        int startIndex = str.indexOf("**");
        int endIndex = str.indexOf("**", startIndex + 1);
        String str2 = str.replace("**", "");
        int index = -1;
        int len = 0;
        if (startIndex >= 0 && endIndex >= 0 && endIndex - startIndex > 2) {
            len = (endIndex - startIndex) - 2;
            index = startIndex;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str2);
        if (index >= 0) {
            spannableStringBuilder.setSpan(new ClickableSpan() {
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }

                public void onClick(View view) {
                    runnable.run();
                }
            }, index, index + len, 0);
        }
        return spannableStringBuilder;
    }

    public static void recycleBitmaps(ArrayList<Bitmap> bitmapToRecycle) {
        if (bitmapToRecycle != null && !bitmapToRecycle.isEmpty()) {
            runOnUIThread(new AndroidUtilities$$ExternalSyntheticLambda14(bitmapToRecycle), 36);
        }
    }

    static /* synthetic */ void lambda$recycleBitmaps$0(ArrayList bitmapToRecycle) {
        for (int i = 0; i < bitmapToRecycle.size(); i++) {
            Bitmap bitmap = (Bitmap) bitmapToRecycle.get(i);
            if (bitmap != null && !bitmap.isRecycled()) {
                try {
                    bitmap.recycle();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }
    }

    private static class LinkSpec {
        int end;
        int start;
        String url;

        private LinkSpec() {
        }
    }

    private static String makeUrl(String url, String[] prefixes, Matcher matcher) {
        boolean hasPrefix = false;
        int i = 0;
        while (true) {
            if (i >= prefixes.length) {
                break;
            }
            if (url.regionMatches(true, 0, prefixes[i], 0, prefixes[i].length())) {
                hasPrefix = true;
                if (!url.regionMatches(false, 0, prefixes[i], 0, prefixes[i].length())) {
                    url = prefixes[i] + url.substring(prefixes[i].length());
                }
            } else {
                i++;
            }
        }
        if (hasPrefix || prefixes.length <= 0) {
            return url;
        }
        return prefixes[0] + url;
    }

    private static void gatherLinks(ArrayList<LinkSpec> links, Spannable s, Pattern pattern, String[] schemes, Linkify.MatchFilter matchFilter, boolean internalOnly) {
        if (TextUtils.indexOf(s, 9472) >= 0) {
            s = new SpannableStringBuilder(s.toString().replace(9472, ' '));
        }
        Matcher m = pattern.matcher(s);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            if (matchFilter == null || matchFilter.acceptMatch(s, start, end)) {
                LinkSpec spec = new LinkSpec();
                String url = makeUrl(m.group(0), schemes, m);
                if (!internalOnly || Browser.isInternalUrl(url, true, (boolean[]) null)) {
                    spec.url = url;
                    spec.start = start;
                    spec.end = end;
                    links.add(spec);
                }
            }
        }
    }

    static /* synthetic */ boolean lambda$static$2(CharSequence s, int start, int end) {
        if (start != 0 && s.charAt(start - 1) == '@') {
            return false;
        }
        return true;
    }

    public static boolean addLinks(Spannable text, int mask) {
        return addLinks(text, mask, false);
    }

    public static boolean addLinks(Spannable text, int mask, boolean internalOnly) {
        if (text == null || containsUnsupportedCharacters(text.toString()) || mask == 0) {
            return false;
        }
        URLSpan[] old = (URLSpan[]) text.getSpans(0, text.length(), URLSpan.class);
        for (int i = old.length - 1; i >= 0; i--) {
            text.removeSpan(old[i]);
        }
        ArrayList<LinkSpec> links = new ArrayList<>();
        if (!internalOnly && (mask & 4) != 0) {
            Linkify.addLinks(text, 4);
        }
        if ((mask & 1) != 0) {
            gatherLinks(links, text, LinkifyPort.WEB_URL, new String[]{"http://", "https://", "tg://"}, sUrlMatchFilter, internalOnly);
        }
        pruneOverlaps(links);
        if (links.size() == 0) {
            return false;
        }
        int N = links.size();
        for (int a = 0; a < N; a++) {
            LinkSpec link = links.get(a);
            URLSpan[] oldSpans = (URLSpan[]) text.getSpans(link.start, link.end, URLSpan.class);
            if (oldSpans != null && oldSpans.length > 0) {
                for (URLSpan removeSpan : oldSpans) {
                    text.removeSpan(removeSpan);
                }
            }
            text.setSpan(new URLSpan(link.url), link.start, link.end, 33);
        }
        return true;
    }

    private static void pruneOverlaps(ArrayList<LinkSpec> links) {
        Collections.sort(links, AndroidUtilities$$ExternalSyntheticLambda2.INSTANCE);
        int len = links.size();
        int i = 0;
        while (i < len - 1) {
            LinkSpec a = links.get(i);
            LinkSpec b = links.get(i + 1);
            int remove = -1;
            if (a.start <= b.start && a.end > b.start) {
                if (b.end <= a.end) {
                    remove = i + 1;
                } else if (a.end - a.start > b.end - b.start) {
                    remove = i + 1;
                } else if (a.end - a.start < b.end - b.start) {
                    remove = i;
                }
                if (remove != -1) {
                    links.remove(remove);
                    len--;
                }
            }
            i++;
        }
    }

    static /* synthetic */ int lambda$pruneOverlaps$3(LinkSpec a, LinkSpec b) {
        if (a.start < b.start) {
            return -1;
        }
        if (a.start > b.start || a.end < b.end) {
            return 1;
        }
        if (a.end > b.end) {
            return -1;
        }
        return 0;
    }

    public static void fillStatusBarHeight(Context context) {
        if (context != null && statusBarHeight <= 0) {
            statusBarHeight = getStatusBarHeight(context);
            navigationBarHeight = getNavigationBarHeight(context);
        }
    }

    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private static int getNavigationBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static int getThumbForNameOrMime(String name, String mime, boolean media) {
        if (name == null || name.length() == 0) {
            return media ? documentMediaIcons[0] : documentIcons[0];
        }
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

    public static int calcBitmapColor(Bitmap bitmap) {
        try {
            Bitmap b = Bitmaps.createScaledBitmap(bitmap, 1, 1, true);
            if (b != null) {
                int bitmapColor = b.getPixel(0, 0);
                if (bitmap != b) {
                    b.recycle();
                }
                return bitmapColor;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return 0;
    }

    public static int[] calcDrawableColor(Drawable drawable) {
        Drawable drawable2 = drawable;
        int bitmapColor = -16777216;
        int[] result = new int[4];
        try {
            if (drawable2 instanceof BitmapDrawable) {
                bitmapColor = calcBitmapColor(((BitmapDrawable) drawable2).getBitmap());
            } else if (drawable2 instanceof ColorDrawable) {
                bitmapColor = ((ColorDrawable) drawable2).getColor();
            } else if (drawable2 instanceof BackgroundGradientDrawable) {
                int[] colors = ((BackgroundGradientDrawable) drawable2).getColorsList();
                if (colors != null) {
                    if (colors.length > 1) {
                        bitmapColor = getAverageColor(colors[0], colors[1]);
                    } else if (colors.length > 0) {
                        bitmapColor = colors[0];
                    }
                }
            } else if (drawable2 instanceof MotionBackgroundDrawable) {
                int argb = Color.argb(45, 0, 0, 0);
                result[2] = argb;
                result[0] = argb;
                int argb2 = Color.argb(61, 0, 0, 0);
                result[3] = argb2;
                result[1] = argb2;
                return result;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        double[] hsv = rgbToHsv((bitmapColor >> 16) & 255, (bitmapColor >> 8) & 255, bitmapColor & 255);
        hsv[1] = Math.min(1.0d, hsv[1] + 0.05d + ((1.0d - hsv[1]) * 0.1d));
        int[] rgb = hsvToRgb(hsv[0], hsv[1], Math.max(0.0d, hsv[2] * 0.65d));
        result[0] = Color.argb(102, rgb[0], rgb[1], rgb[2]);
        result[1] = Color.argb(136, rgb[0], rgb[1], rgb[2]);
        int[] rgb2 = hsvToRgb(hsv[0], hsv[1], Math.max(0.0d, hsv[2] * 0.72d));
        result[2] = Color.argb(102, rgb2[0], rgb2[1], rgb2[2]);
        result[3] = Color.argb(136, rgb2[0], rgb2[1], rgb2[2]);
        return result;
    }

    public static double[] rgbToHsv(int color) {
        return rgbToHsv(Color.red(color), Color.green(color), Color.blue(color));
    }

    public static double[] rgbToHsv(int r, int g, int b) {
        double h;
        double h2;
        double d = (double) r;
        Double.isNaN(d);
        double rf = d / 255.0d;
        double d2 = (double) g;
        Double.isNaN(d2);
        double gf = d2 / 255.0d;
        double d3 = (double) b;
        Double.isNaN(d3);
        double bf = d3 / 255.0d;
        double max = (rf <= gf || rf <= bf) ? Math.max(gf, bf) : rf;
        double min = (rf >= gf || rf >= bf) ? Math.min(gf, bf) : rf;
        double d4 = max - min;
        double s = 0.0d;
        if (max != 0.0d) {
            s = d4 / max;
        }
        if (max == min) {
            h = 0.0d;
            double d5 = min;
        } else {
            if (rf <= gf || rf <= bf) {
                if (gf > bf) {
                    h2 = ((bf - rf) / d4) + 2.0d;
                } else {
                    h2 = ((rf - gf) / d4) + 4.0d;
                }
            } else {
                double d6 = (gf - bf) / d4;
                double d7 = min;
                double min2 = (double) (gf < bf ? 6 : 0);
                Double.isNaN(min2);
                h2 = d6 + min2;
            }
            h = h2 / 6.0d;
        }
        return new double[]{h, s, max};
    }

    public static int hsvToColor(double h, double s, double v) {
        int[] rgb = hsvToRgb(h, s, v);
        return Color.argb(255, rgb[0], rgb[1], rgb[2]);
    }

    public static int[] hsvToRgb(double h, double s, double v) {
        double r;
        double g = 0.0d;
        double b = 0.0d;
        double i = (double) ((int) Math.floor(h * 6.0d));
        Double.isNaN(i);
        double f = (6.0d * h) - i;
        double p = (1.0d - s) * v;
        double q = (1.0d - (f * s)) * v;
        double t = (1.0d - ((1.0d - f) * s)) * v;
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
            default:
                r = 0.0d;
                break;
        }
        double d = f;
        double d2 = i;
        return new int[]{(int) (r * 255.0d), (int) (g * 255.0d), (int) (b * 255.0d)};
    }

    public static void adjustSaturationColorMatrix(ColorMatrix colorMatrix, float saturation) {
        if (colorMatrix != null) {
            float x = saturation + 1.0f;
            colorMatrix.postConcat(new ColorMatrix(new float[]{((1.0f - x) * 0.3086f) + x, (1.0f - x) * 0.6094f, (1.0f - x) * 0.082f, 0.0f, 0.0f, (1.0f - x) * 0.3086f, ((1.0f - x) * 0.6094f) + x, (1.0f - x) * 0.082f, 0.0f, 0.0f, (1.0f - x) * 0.3086f, (1.0f - x) * 0.6094f, ((1.0f - x) * 0.082f) + x, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}));
        }
    }

    public static void adjustBrightnessColorMatrix(ColorMatrix colorMatrix, float brightness) {
        if (colorMatrix != null) {
            float brightness2 = brightness * 255.0f;
            colorMatrix.postConcat(new ColorMatrix(new float[]{1.0f, 0.0f, 0.0f, 0.0f, brightness2, 0.0f, 1.0f, 0.0f, 0.0f, brightness2, 0.0f, 0.0f, 1.0f, 0.0f, brightness2, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}));
        }
    }

    public static void multiplyBrightnessColorMatrix(ColorMatrix colorMatrix, float v) {
        if (colorMatrix != null) {
            colorMatrix.postConcat(new ColorMatrix(new float[]{v, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, v, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, v, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}));
        }
    }

    public static Bitmap snapshotView(View v) {
        Bitmap bm = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        v.draw(canvas);
        int[] loc = new int[2];
        v.getLocationInWindow(loc);
        snapshotTextureViews(loc[0], loc[1], loc, canvas, v);
        return bm;
    }

    private static void snapshotTextureViews(int rootX, int rootY, int[] loc, Canvas canvas, View v) {
        if (v instanceof TextureView) {
            TextureView tv = (TextureView) v;
            tv.getLocationInWindow(loc);
            Bitmap textureSnapshot = tv.getBitmap();
            if (textureSnapshot != null) {
                canvas.save();
                canvas.drawBitmap(textureSnapshot, (float) (loc[0] - rootX), (float) (loc[1] - rootY), (Paint) null);
                canvas.restore();
                textureSnapshot.recycle();
            }
        }
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                snapshotTextureViews(rootX, rootY, loc, canvas, vg.getChildAt(i));
            }
        }
    }

    public static void requestAltFocusable(Activity activity, int classGuid) {
        if (activity != null) {
            activity.getWindow().setFlags(131072, 131072);
            altFocusableClassGuid = classGuid;
        }
    }

    public static void removeAltFocusable(Activity activity, int classGuid) {
        if (activity != null && altFocusableClassGuid == classGuid) {
            activity.getWindow().clearFlags(131072);
        }
    }

    public static void requestAdjustResize(Activity activity, int classGuid) {
        if (activity != null) {
            requestAdjustResize(activity.getWindow(), classGuid);
        }
    }

    public static void requestAdjustResize(Window window, int classGuid) {
        if (window != null && !isTablet()) {
            window.setSoftInputMode(16);
            adjustOwnerClassGuid = classGuid;
        }
    }

    public static void requestAdjustNothing(Activity activity, int classGuid) {
        if (activity != null && !isTablet()) {
            activity.getWindow().setSoftInputMode(48);
            adjustOwnerClassGuid = classGuid;
        }
    }

    public static void setAdjustResizeToNothing(Activity activity, int classGuid) {
        if (activity != null && !isTablet()) {
            int i = adjustOwnerClassGuid;
            if (i == 0 || i == classGuid) {
                activity.getWindow().setSoftInputMode(48);
            }
        }
    }

    public static void removeAdjustResize(Activity activity, int classGuid) {
        if (activity != null && !isTablet() && adjustOwnerClassGuid == classGuid) {
            activity.getWindow().setSoftInputMode(32);
        }
    }

    public static void createEmptyFile(File f) {
        try {
            if (!f.exists()) {
                FileWriter writer = new FileWriter(f);
                writer.flush();
                writer.close();
            }
        } catch (Throwable e) {
            FileLog.e(e, false);
        }
    }

    public static boolean isGoogleMapsInstalled(BaseFragment fragment) {
        try {
            ApplicationLoader.applicationContext.getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            if (fragment.getParentActivity() == null) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) fragment.getParentActivity());
            builder.setMessage(LocaleController.getString("InstallGoogleMaps", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new AndroidUtilities$$ExternalSyntheticLambda7(fragment));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            fragment.showDialog(builder.create());
            return false;
        }
    }

    static /* synthetic */ void lambda$isGoogleMapsInstalled$4(BaseFragment fragment, DialogInterface dialogInterface, int i) {
        try {
            fragment.getParentActivity().startActivityForResult(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.google.android.apps.maps")), 500);
        } catch (Exception e1) {
            FileLog.e((Throwable) e1);
        }
    }

    public static int[] toIntArray(List<Integer> integers) {
        int[] ret = new int[integers.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }

    public static boolean isInternalUri(Uri uri) {
        return isInternalUri(uri, 0);
    }

    public static boolean isInternalUri(int fd) {
        return isInternalUri((Uri) null, fd);
    }

    private static boolean isInternalUri(Uri uri, int fd) {
        String pathString;
        if (uri != null) {
            pathString = uri.getPath();
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
                try {
                    String newPath = Utilities.readlink(pathString);
                    if (newPath != null && !newPath.equals(pathString)) {
                        pathString = newPath;
                        tries++;
                    }
                } catch (Throwable th) {
                    return true;
                }
            } while (tries < 10);
            return true;
        }
        String pathString2 = "";
        int tries2 = 0;
        do {
            if (pathString != null && pathString.length() > 4096) {
                return true;
            }
            try {
                String newPath2 = Utilities.readlinkFd(fd);
                if (newPath2 != null && !newPath2.equals(pathString)) {
                    pathString2 = newPath2;
                    tries2++;
                }
            } catch (Throwable th2) {
                return true;
            }
        } while (tries2 < 10);
        return true;
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
        if (pathString.endsWith(".attheme") || pathString == null) {
            return false;
        }
        String lowerCase = pathString.toLowerCase();
        if (lowerCase.contains("/data/data/" + ApplicationLoader.applicationContext.getPackageName())) {
            return true;
        }
        return false;
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
                FileLog.e((Throwable) e);
            }
        }
    }

    public static void unlockOrientation(Activity activity) {
        if (activity != null) {
            try {
                int i = prevOrientation;
                if (i != -10) {
                    activity.setRequestedOrientation(i);
                    prevOrientation = -10;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    private static class VcardData {
        String name;
        ArrayList<String> phones;
        StringBuilder vcard;

        private VcardData() {
            this.phones = new ArrayList<>();
            this.vcard = new StringBuilder();
        }
    }

    public static class VcardItem {
        public boolean checked = true;
        public String fullData = "";
        public int type;
        public ArrayList<String> vcardData = new ArrayList<>();

        public String[] getRawValue() {
            byte[] bytes;
            int idx = this.fullData.indexOf(58);
            if (idx < 0) {
                return new String[0];
            }
            String valueType = this.fullData.substring(0, idx);
            String value = this.fullData.substring(idx + 1);
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
            for (int a = 0; a < args.length; a++) {
                if (!(TextUtils.isEmpty(args[a]) || nameEncoding == null || !nameEncoding.equalsIgnoreCase("QUOTED-PRINTABLE") || (bytes = AndroidUtilities.decodeQuotedPrintable(AndroidUtilities.getStringBytes(args[a]))) == null || bytes.length == 0)) {
                    try {
                        args[a] = new String(bytes, nameCharset);
                    } catch (Exception e) {
                    }
                }
            }
            return args;
        }

        public String getValue(boolean format) {
            byte[] bytes;
            StringBuilder result = new StringBuilder();
            int idx = this.fullData.indexOf(58);
            if (idx < 0) {
                return "";
            }
            if (result.length() > 0) {
                result.append(", ");
            }
            String valueType = this.fullData.substring(0, idx);
            String value = this.fullData.substring(idx + 1);
            String[] params = valueType.split(";");
            String nameEncoding = null;
            String nameCharset = "UTF-8";
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
            for (int a = 0; a < args.length; a++) {
                if (!TextUtils.isEmpty(args[a])) {
                    if (!(nameEncoding == null || !nameEncoding.equalsIgnoreCase("QUOTED-PRINTABLE") || (bytes = AndroidUtilities.decodeQuotedPrintable(AndroidUtilities.getStringBytes(args[a]))) == null || bytes.length == 0)) {
                        try {
                            args[a] = new String(bytes, nameCharset);
                        } catch (Exception e) {
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
                int i = this.type;
                if (i == 0) {
                    return PhoneFormat.getInstance().format(result.toString());
                }
                if (i == 5) {
                    String[] date = result.toString().split("T");
                    if (date.length > 0) {
                        String[] date2 = date[0].split("-");
                        if (date2.length == 3) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(1, Utilities.parseInt((CharSequence) date2[0]).intValue());
                            calendar.set(2, Utilities.parseInt((CharSequence) date2[1]).intValue() - 1);
                            calendar.set(5, Utilities.parseInt((CharSequence) date2[2]).intValue());
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
            if (this.type == 20) {
                String[] args = value.substring(2).split(";");
                if (first) {
                    return args[0];
                }
                if (args.length > 1) {
                    return args[args.length - 1];
                }
                return "";
            }
            String[] args2 = value.split(";");
            for (int a = 0; a < args2.length; a++) {
                if (args2[a].indexOf(61) < 0) {
                    value = args2[a];
                }
            }
            return value;
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* JADX WARNING: Code restructure failed: missing block: B:34:0x0099, code lost:
            if (r2.equals("WORK") != false) goto L_0x00c5;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.String getType() {
            /*
                r10 = this;
                int r0 = r10.type
                r1 = 5
                if (r0 != r1) goto L_0x000f
                r0 = 2131625230(0x7f0e050e, float:1.8877662E38)
                java.lang.String r1 = "ContactBirthday"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                return r0
            L_0x000f:
                r2 = 6
                r3 = 1
                if (r0 != r2) goto L_0x0033
                java.lang.String r0 = r10.getRawType(r3)
                java.lang.String r1 = "ORG"
                boolean r0 = r1.equalsIgnoreCase(r0)
                if (r0 == 0) goto L_0x0029
                r0 = 2131625231(0x7f0e050f, float:1.8877664E38)
                java.lang.String r1 = "ContactJob"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                return r0
            L_0x0029:
                r0 = 2131625232(0x7f0e0510, float:1.8877666E38)
                java.lang.String r1 = "ContactJobTitle"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                return r0
            L_0x0033:
                java.lang.String r0 = r10.fullData
                r2 = 58
                int r0 = r0.indexOf(r2)
                if (r0 >= 0) goto L_0x0040
                java.lang.String r1 = ""
                return r1
            L_0x0040:
                java.lang.String r2 = r10.fullData
                r4 = 0
                java.lang.String r2 = r2.substring(r4, r0)
                int r5 = r10.type
                r6 = 20
                java.lang.String r7 = ";"
                r8 = 2
                if (r5 != r6) goto L_0x005c
                java.lang.String r1 = r2.substring(r8)
                java.lang.String[] r2 = r1.split(r7)
                r1 = r2[r4]
                goto L_0x00fc
            L_0x005c:
                java.lang.String[] r5 = r2.split(r7)
                r6 = 0
            L_0x0061:
                int r7 = r5.length
                if (r6 >= r7) goto L_0x0074
                r7 = r5[r6]
                r9 = 61
                int r7 = r7.indexOf(r9)
                if (r7 < 0) goto L_0x006f
                goto L_0x0071
            L_0x006f:
                r2 = r5[r6]
            L_0x0071:
                int r6 = r6 + 1
                goto L_0x0061
            L_0x0074:
                java.lang.String r6 = "X-"
                boolean r6 = r2.startsWith(r6)
                if (r6 == 0) goto L_0x0080
                java.lang.String r2 = r2.substring(r8)
            L_0x0080:
                r6 = -1
                int r7 = r2.hashCode()
                switch(r7) {
                    case -2015525726: goto L_0x00ba;
                    case 2064738: goto L_0x00b0;
                    case 2223327: goto L_0x00a6;
                    case 2464291: goto L_0x009c;
                    case 2670353: goto L_0x0093;
                    case 75532016: goto L_0x0089;
                    default: goto L_0x0088;
                }
            L_0x0088:
                goto L_0x00c4
            L_0x0089:
                java.lang.String r1 = "OTHER"
                boolean r1 = r2.equals(r1)
                if (r1 == 0) goto L_0x0088
                r1 = 4
                goto L_0x00c5
            L_0x0093:
                java.lang.String r7 = "WORK"
                boolean r7 = r2.equals(r7)
                if (r7 == 0) goto L_0x0088
                goto L_0x00c5
            L_0x009c:
                java.lang.String r1 = "PREF"
                boolean r1 = r2.equals(r1)
                if (r1 == 0) goto L_0x0088
                r1 = 0
                goto L_0x00c5
            L_0x00a6:
                java.lang.String r1 = "HOME"
                boolean r1 = r2.equals(r1)
                if (r1 == 0) goto L_0x0088
                r1 = 1
                goto L_0x00c5
            L_0x00b0:
                java.lang.String r1 = "CELL"
                boolean r1 = r2.equals(r1)
                if (r1 == 0) goto L_0x0088
                r1 = 3
                goto L_0x00c5
            L_0x00ba:
                java.lang.String r1 = "MOBILE"
                boolean r1 = r2.equals(r1)
                if (r1 == 0) goto L_0x0088
                r1 = 2
                goto L_0x00c5
            L_0x00c4:
                r1 = -1
            L_0x00c5:
                switch(r1) {
                    case 0: goto L_0x00f2;
                    case 1: goto L_0x00e8;
                    case 2: goto L_0x00de;
                    case 3: goto L_0x00de;
                    case 4: goto L_0x00d4;
                    case 5: goto L_0x00ca;
                    default: goto L_0x00c8;
                }
            L_0x00c8:
                r1 = r2
                goto L_0x00fc
            L_0x00ca:
                r1 = 2131627502(0x7f0e0dee, float:1.888227E38)
                java.lang.String r6 = "PhoneWork"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r6, r1)
                goto L_0x00fc
            L_0x00d4:
                r1 = 2131627501(0x7f0e0ded, float:1.8882268E38)
                java.lang.String r6 = "PhoneOther"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r6, r1)
                goto L_0x00fc
            L_0x00de:
                r1 = 2131627492(0x7f0e0de4, float:1.888225E38)
                java.lang.String r6 = "PhoneMobile"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r6, r1)
                goto L_0x00fc
            L_0x00e8:
                r1 = 2131627490(0x7f0e0de2, float:1.8882246E38)
                java.lang.String r6 = "PhoneHome"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r6, r1)
                goto L_0x00fc
            L_0x00f2:
                r1 = 2131627491(0x7f0e0de3, float:1.8882248E38)
                java.lang.String r6 = "PhoneMain"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r6, r1)
            L_0x00fc:
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r4 = r1.substring(r4, r3)
                java.lang.String r4 = r4.toUpperCase()
                r2.append(r4)
                java.lang.String r3 = r1.substring(r3)
                java.lang.String r3 = r3.toLowerCase()
                r2.append(r3)
                java.lang.String r1 = r2.toString()
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.VcardItem.getType():java.lang.String");
        }
    }

    public static byte[] getStringBytes(String src) {
        try {
            return src.getBytes("UTF-8");
        } catch (Exception e) {
            return new byte[0];
        }
    }

    public static ArrayList<TLRPC.User> loadVCardFromStream(Uri uri, int currentAccount, boolean asset, ArrayList<VcardItem> items, String name) {
        InputStream stream;
        ArrayList<TLRPC.User> result;
        InputStream stream2;
        String[] args;
        VcardItem currentItem;
        boolean currentIsPhoto;
        String pendingLine;
        byte[] bytes;
        Uri uri2 = uri;
        ArrayList<VcardItem> arrayList = items;
        ArrayList<TLRPC.User> result2 = null;
        if (asset) {
            try {
                stream = ApplicationLoader.applicationContext.getContentResolver().openAssetFileDescriptor(uri2, "r").createInputStream();
                InputStream inputStream = stream;
            } catch (Throwable th) {
                e = th;
                FileLog.e(e);
                return result2;
            }
        } else {
            try {
                stream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri2);
                InputStream inputStream2 = stream;
            } catch (Throwable th2) {
                e = th2;
                ArrayList<TLRPC.User> arrayList2 = result2;
                FileLog.e(e);
                return result2;
            }
        }
        ArrayList arrayList3 = new ArrayList();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        VcardItem currentItem2 = null;
        boolean currentIsPhoto2 = false;
        String pendingLine2 = null;
        VcardData currentData = null;
        while (true) {
            String readLine = bufferedReader.readLine();
            String line = readLine;
            String originalLine = readLine;
            if (readLine == null) {
                break;
            } else if (originalLine.startsWith("PHOTO")) {
                currentIsPhoto2 = true;
            } else {
                if (originalLine.indexOf(58) >= 0) {
                    VcardItem currentItem3 = null;
                    currentIsPhoto2 = false;
                    if (originalLine.startsWith("BEGIN:VCARD")) {
                        VcardData vcardData = new VcardData();
                        VcardData currentData2 = vcardData;
                        arrayList3.add(vcardData);
                        currentData2.name = name;
                        currentItem2 = null;
                        currentData = currentData2;
                    } else {
                        String str = name;
                        if (!originalLine.startsWith("END:VCARD")) {
                            if (arrayList != null) {
                                if (originalLine.startsWith("TEL")) {
                                    currentItem3 = new VcardItem();
                                    currentItem3.type = 0;
                                } else if (originalLine.startsWith("EMAIL")) {
                                    currentItem3 = new VcardItem();
                                    currentItem3.type = 1;
                                } else {
                                    if (!originalLine.startsWith("ADR") && !originalLine.startsWith("LABEL")) {
                                        if (!originalLine.startsWith("GEO")) {
                                            if (originalLine.startsWith("URL")) {
                                                currentItem3 = new VcardItem();
                                                currentItem3.type = 3;
                                            } else if (originalLine.startsWith("NOTE")) {
                                                currentItem3 = new VcardItem();
                                                currentItem3.type = 4;
                                            } else if (originalLine.startsWith("BDAY")) {
                                                currentItem3 = new VcardItem();
                                                currentItem3.type = 5;
                                            } else {
                                                if (!originalLine.startsWith("ORG") && !originalLine.startsWith("TITLE")) {
                                                    if (!originalLine.startsWith("ROLE")) {
                                                        if (originalLine.startsWith("X-ANDROID")) {
                                                            currentItem3 = new VcardItem();
                                                            currentItem3.type = -1;
                                                        } else if (originalLine.startsWith("X-PHONETIC")) {
                                                            currentItem3 = null;
                                                        } else if (originalLine.startsWith("X-")) {
                                                            currentItem3 = new VcardItem();
                                                            currentItem3.type = 20;
                                                        }
                                                    }
                                                }
                                                if (0 == 0) {
                                                    currentItem3 = new VcardItem();
                                                    currentItem3.type = 6;
                                                }
                                            }
                                        }
                                    }
                                    currentItem3 = new VcardItem();
                                    currentItem3.type = 2;
                                }
                                if (currentItem3 != null && currentItem3.type >= 0) {
                                    arrayList.add(currentItem3);
                                }
                            }
                        }
                        currentItem2 = currentItem3;
                    }
                }
                if (!currentIsPhoto2 && currentData != null) {
                    if (currentItem2 == null) {
                        if (currentData.vcard.length() > 0) {
                            currentData.vcard.append(10);
                        }
                        currentData.vcard.append(originalLine);
                    } else {
                        currentItem2.vcardData.add(originalLine);
                    }
                }
                if (pendingLine2 != null) {
                    line = pendingLine2 + line;
                    pendingLine2 = null;
                }
                String str2 = "=";
                if (line.contains("=QUOTED-PRINTABLE")) {
                    if (line.endsWith(str2)) {
                        pendingLine2 = line.substring(0, line.length() - 1);
                        Uri uri3 = uri;
                    }
                }
                if (!(currentIsPhoto2 || currentData == null || currentItem2 == null)) {
                    currentItem2.fullData = line;
                }
                int idx = line.indexOf(":");
                if (idx >= 0) {
                    result = result2;
                    try {
                        args = new String[]{line.substring(0, idx), line.substring(idx + 1).trim()};
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    } catch (Throwable th3) {
                        e = th3;
                        result2 = result;
                        FileLog.e(e);
                        return result2;
                    }
                } else {
                    result = result2;
                    args = new String[]{line.trim()};
                }
                int i = idx;
                if (args.length < 2) {
                    pendingLine = pendingLine2;
                    currentIsPhoto = currentIsPhoto2;
                    currentItem = currentItem2;
                } else if (currentData == null) {
                    pendingLine = pendingLine2;
                    currentIsPhoto = currentIsPhoto2;
                    currentItem = currentItem2;
                } else {
                    if (!args[0].startsWith("FN")) {
                        if (!args[0].startsWith("N")) {
                            if (!args[0].startsWith("ORG") || !TextUtils.isEmpty(currentData.name)) {
                                if (args[0].startsWith("TEL")) {
                                    currentData.phones.add(args[1]);
                                    pendingLine = pendingLine2;
                                    currentIsPhoto = currentIsPhoto2;
                                    currentItem = currentItem2;
                                } else {
                                    pendingLine = pendingLine2;
                                    currentIsPhoto = currentIsPhoto2;
                                    currentItem = currentItem2;
                                }
                            }
                        }
                    }
                    String[] params = args[0].split(";");
                    int length = params.length;
                    currentIsPhoto = currentIsPhoto2;
                    int i2 = 0;
                    pendingLine = pendingLine2;
                    String nameCharset = null;
                    String nameEncoding = null;
                    while (i2 < length) {
                        String[] params2 = params;
                        String param = params[i2];
                        String str3 = param;
                        String[] args2 = param.split(str2);
                        String str4 = str2;
                        VcardItem currentItem4 = currentItem2;
                        if (args2.length == 2) {
                            if (args2[0].equals("CHARSET")) {
                                nameCharset = args2[1];
                            } else if (args2[0].equals("ENCODING")) {
                                nameEncoding = args2[1];
                            }
                        }
                        i2++;
                        params = params2;
                        str2 = str4;
                        currentItem2 = currentItem4;
                    }
                    currentItem = currentItem2;
                    if (args[0].startsWith("N")) {
                        currentData.name = args[1].replace(';', ' ').trim();
                    } else {
                        currentData.name = args[1];
                    }
                    if (!(nameEncoding == null || !nameEncoding.equalsIgnoreCase("QUOTED-PRINTABLE") || (bytes = decodeQuotedPrintable(getStringBytes(currentData.name))) == null || bytes.length == 0)) {
                        currentData.name = new String(bytes, nameCharset);
                    }
                }
                Uri uri4 = uri;
                arrayList = items;
                result2 = result;
                pendingLine2 = pendingLine;
                currentIsPhoto2 = currentIsPhoto;
                currentItem2 = currentItem;
            }
        }
        result = result2;
        bufferedReader.close();
        stream.close();
        int a = 0;
        result2 = result;
        while (a < arrayList3.size()) {
            VcardData vcardData2 = (VcardData) arrayList3.get(a);
            if (vcardData2.name == null || vcardData2.phones.isEmpty()) {
                stream2 = stream;
            } else {
                if (result2 == null) {
                    result2 = new ArrayList<>();
                }
                String phoneToUse = vcardData2.phones.get(0);
                int b = 0;
                while (true) {
                    if (b >= vcardData2.phones.size()) {
                        stream2 = stream;
                        break;
                    }
                    String phone = vcardData2.phones.get(b);
                    String phoneToUse2 = phoneToUse;
                    stream2 = stream;
                    if (ContactsController.getInstance(currentAccount).contactsByShortPhone.get(phone.substring(Math.max(0, phone.length() - 7))) != null) {
                        phoneToUse = phone;
                        break;
                    }
                    b++;
                    stream = stream2;
                    phoneToUse = phoneToUse2;
                }
                TLRPC.User user = new TLRPC.TL_userContact_old2();
                user.phone = phoneToUse;
                user.first_name = vcardData2.name;
                user.last_name = "";
                user.id = 0;
                TLRPC.TL_restrictionReason reason = new TLRPC.TL_restrictionReason();
                reason.text = vcardData2.vcard.toString();
                reason.platform = "";
                reason.reason = "";
                user.restriction_reason.add(reason);
                result2.add(user);
            }
            a++;
            stream = stream2;
        }
        return result2;
    }

    public static Typeface getTypeface(String assetPath) {
        Typeface typeface;
        Typeface t;
        Hashtable<String, Typeface> hashtable = typefaceCache;
        synchronized (hashtable) {
            if (!hashtable.containsKey(assetPath)) {
                try {
                    if (Build.VERSION.SDK_INT >= 26) {
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
                    hashtable.put(assetPath, t);
                } catch (Exception e) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("Could not get typeface '" + assetPath + "' because " + e.getMessage());
                    }
                    return null;
                }
            }
            typeface = hashtable.get(assetPath);
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
            if (value) {
                try {
                    SmsRetriever.getClient(ApplicationLoader.applicationContext).startSmsRetriever().addOnSuccessListener(AndroidUtilities$$ExternalSyntheticLambda12.INSTANCE);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        }
    }

    static /* synthetic */ void lambda$setWaitingForSms$5(Void aVoid) {
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("sms listener registered");
        }
    }

    public static int getShadowHeight() {
        float f = density;
        if (f >= 4.0f) {
            return 3;
        }
        if (f >= 2.0f) {
            return 2;
        }
        return 1;
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
                        CallReceiver callReceiver2 = new CallReceiver();
                        callReceiver = callReceiver2;
                        context.registerReceiver(callReceiver2, filter);
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
        if (view == null) {
            return false;
        }
        try {
            return ((InputMethodManager) view.getContext().getSystemService("input_method")).showSoftInput(view, 1);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    public static String[] getCurrentKeyboardLanguage() {
        try {
            InputMethodManager inputManager = (InputMethodManager) ApplicationLoader.applicationContext.getSystemService("input_method");
            InputMethodSubtype inputMethodSubtype = inputManager.getCurrentInputMethodSubtype();
            String locale = null;
            if (inputMethodSubtype != null) {
                if (Build.VERSION.SDK_INT >= 24) {
                    locale = inputMethodSubtype.getLanguageTag();
                }
                if (TextUtils.isEmpty(locale)) {
                    locale = inputMethodSubtype.getLocale();
                }
            } else {
                InputMethodSubtype inputMethodSubtype2 = inputManager.getLastInputMethodSubtype();
                if (inputMethodSubtype2 != null) {
                    if (Build.VERSION.SDK_INT >= 24) {
                        locale = inputMethodSubtype2.getLanguageTag();
                    }
                    if (TextUtils.isEmpty(locale)) {
                        locale = inputMethodSubtype2.getLocale();
                    }
                }
            }
            if (TextUtils.isEmpty(locale)) {
                String locale2 = LocaleController.getSystemLocaleStringIso639();
                LocaleController.LocaleInfo localeInfo = LocaleController.getInstance().getCurrentLocaleInfo();
                String locale22 = localeInfo.getBaseLangCode();
                if (TextUtils.isEmpty(locale22)) {
                    locale22 = localeInfo.getLangCode();
                }
                if (locale2.contains(locale22) || locale22.contains(locale2)) {
                    if (!locale2.contains("en")) {
                        locale22 = "en";
                    } else {
                        locale22 = null;
                    }
                }
                if (!TextUtils.isEmpty(locale22)) {
                    return new String[]{locale2.replace('_', '-'), locale22};
                }
                return new String[]{locale2.replace('_', '-')};
            }
            return new String[]{locale.replace('_', '-')};
        } catch (Exception e) {
            return new String[]{"en"};
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
                FileLog.e((Throwable) e);
            }
        }
    }

    public static ArrayList<File> getDataDirs() {
        File[] dirs;
        ArrayList<File> result = null;
        if (Build.VERSION.SDK_INT >= 19 && (dirs = ApplicationLoader.applicationContext.getExternalFilesDirs((String) null)) != null) {
            for (int a = 0; a < dirs.length; a++) {
                if (dirs[a] != null) {
                    String absolutePath = dirs[a].getAbsolutePath();
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(dirs[a]);
                }
            }
        }
        if (result == null) {
            result = new ArrayList<>();
        }
        if (result.isEmpty()) {
            result.add(Environment.getExternalStorageDirectory());
        }
        return result;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0019, code lost:
        r3 = r1[r2].getAbsolutePath();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.ArrayList<java.io.File> getRootDirs() {
        /*
            r0 = 0
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x003f
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            r2 = 0
            java.io.File[] r1 = r1.getExternalFilesDirs(r2)
            if (r1 == 0) goto L_0x003f
            r2 = 0
        L_0x0011:
            int r3 = r1.length
            if (r2 >= r3) goto L_0x003f
            r3 = r1[r2]
            if (r3 != 0) goto L_0x0019
            goto L_0x003c
        L_0x0019:
            r3 = r1[r2]
            java.lang.String r3 = r3.getAbsolutePath()
            java.lang.String r4 = "/Android"
            int r4 = r3.indexOf(r4)
            if (r4 < 0) goto L_0x003c
            if (r0 != 0) goto L_0x002f
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r0 = r5
        L_0x002f:
            java.io.File r5 = new java.io.File
            r6 = 0
            java.lang.String r6 = r3.substring(r6, r4)
            r5.<init>(r6)
            r0.add(r5)
        L_0x003c:
            int r2 = r2 + 1
            goto L_0x0011
        L_0x003f:
            if (r0 != 0) goto L_0x0047
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r0 = r1
        L_0x0047:
            boolean r1 = r0.isEmpty()
            if (r1 == 0) goto L_0x0054
            java.io.File r1 = android.os.Environment.getExternalStorageDirectory()
            r0.add(r1)
        L_0x0054:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.getRootDirs():java.util.ArrayList");
    }

    public static File getCacheDir() {
        File file;
        String state = null;
        try {
            state = Environment.getExternalStorageState();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (state == null || state.startsWith("mounted")) {
            try {
                if (Build.VERSION.SDK_INT >= 19) {
                    File[] dirs = ApplicationLoader.applicationContext.getExternalCacheDirs();
                    file = dirs[0];
                    if (!TextUtils.isEmpty(SharedConfig.storageCacheDir)) {
                        int a = 0;
                        while (true) {
                            if (a < dirs.length) {
                                if (dirs[a] != null && dirs[a].getAbsolutePath().startsWith(SharedConfig.storageCacheDir)) {
                                    file = dirs[a];
                                    break;
                                }
                                a++;
                            } else {
                                break;
                            }
                        }
                    }
                } else {
                    file = ApplicationLoader.applicationContext.getExternalCacheDir();
                }
                if (file != null) {
                    return file;
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
        try {
            File file2 = ApplicationLoader.applicationContext.getCacheDir();
            if (file2 != null) {
                return file2;
            }
        } catch (Exception e3) {
            FileLog.e((Throwable) e3);
        }
        return new File("");
    }

    public static int dp(float value) {
        if (value == 0.0f) {
            return 0;
        }
        return (int) Math.ceil((double) (density * value));
    }

    public static int dpr(float value) {
        if (value == 0.0f) {
            return 0;
        }
        return Math.round(density * value);
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

    public static int compare(long lhs, long rhs) {
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
        Display display;
        try {
            float oldDensity = density;
            float newDensity = context.getResources().getDisplayMetrics().density;
            density = newDensity;
            if (firstConfigurationWas && ((double) Math.abs(oldDensity - newDensity)) > 0.001d) {
                Theme.reloadAllResources(context);
            }
            boolean z = true;
            firstConfigurationWas = true;
            Configuration configuration = newConfiguration;
            if (configuration == null) {
                configuration = context.getResources().getConfiguration();
            }
            if (configuration.keyboard == 1 || configuration.hardKeyboardHidden != 1) {
                z = false;
            }
            usingHardwareInput = z;
            WindowManager manager = (WindowManager) context.getSystemService("window");
            if (!(manager == null || (display = manager.getDefaultDisplay()) == null)) {
                display.getMetrics(displayMetrics);
                display.getSize(displaySize);
                screenRefreshRate = display.getRefreshRate();
            }
            if (configuration.screenWidthDp != 0) {
                int newSize = (int) Math.ceil((double) (((float) configuration.screenWidthDp) * density));
                if (Math.abs(displaySize.x - newSize) > 3) {
                    displaySize.x = newSize;
                }
            }
            if (configuration.screenHeightDp != 0) {
                int newSize2 = (int) Math.ceil((double) (((float) configuration.screenHeightDp) * density));
                if (Math.abs(displaySize.y - newSize2) > 3) {
                    displaySize.y = newSize2;
                }
            }
            if (roundMessageSize == 0) {
                if (isTablet()) {
                    roundMessageSize = (int) (((float) getMinTabletSide()) * 0.6f);
                    roundPlayingMessageSize = getMinTabletSide() - dp(28.0f);
                } else {
                    roundMessageSize = (int) (((float) Math.min(displaySize.x, displaySize.y)) * 0.6f);
                    roundPlayingMessageSize = Math.min(displaySize.x, displaySize.y) - dp(28.0f);
                }
                roundMessageInset = dp(2.0f);
            }
            if (BuildVars.LOGS_ENABLED) {
                if (statusBarHeight == 0) {
                    fillStatusBarHeight(context);
                }
                FileLog.e("density = " + density + " display size = " + displaySize.x + " " + displaySize.y + " " + displayMetrics.xdpi + "x" + displayMetrics.ydpi + ", screen layout: " + configuration.screenLayout + ", statusbar height: " + statusBarHeight + ", navbar height: " + navigationBarHeight);
            }
            touchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static double fixLocationCoord(double value) {
        double d = (double) ((long) (value * 1000000.0d));
        Double.isNaN(d);
        return d / 1000000.0d;
    }

    public static String formapMapUrl(int account, double lat, double lon, int width, int height, boolean marker, int zoom, int provider) {
        int provider2;
        int scale = Math.min(2, (int) Math.ceil((double) density));
        int i = provider;
        if (i == -1) {
            provider2 = MessagesController.getInstance(account).mapProvider;
        } else {
            provider2 = i;
        }
        if (provider2 == 1 || provider2 == 3) {
            String lang = null;
            String[] availableLangs = {"ru_RU", "tr_TR"};
            LocaleController.LocaleInfo localeInfo = LocaleController.getInstance().getCurrentLocaleInfo();
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
        String k = MessagesController.getInstance(account).mapKey;
        if (!TextUtils.isEmpty(k)) {
            if (marker) {
                return String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%.6f,%.6f&zoom=%d&size=%dx%d&maptype=roadmap&scale=%d&markers=color:red%%7Csize:mid%%7C%.6f,%.6f&sensor=false&key=%s", new Object[]{Double.valueOf(lat), Double.valueOf(lon), Integer.valueOf(zoom), Integer.valueOf(width), Integer.valueOf(height), Integer.valueOf(scale), Double.valueOf(lat), Double.valueOf(lon), k});
            }
            return String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%.6f,%.6f&zoom=%d&size=%dx%d&maptype=roadmap&scale=%d&key=%s", new Object[]{Double.valueOf(lat), Double.valueOf(lon), Integer.valueOf(zoom), Integer.valueOf(width), Integer.valueOf(height), Integer.valueOf(scale), k});
        } else if (marker) {
            return String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%.6f,%.6f&zoom=%d&size=%dx%d&maptype=roadmap&scale=%d&markers=color:red%%7Csize:mid%%7C%.6f,%.6f&sensor=false", new Object[]{Double.valueOf(lat), Double.valueOf(lon), Integer.valueOf(zoom), Integer.valueOf(width), Integer.valueOf(height), Integer.valueOf(scale), Double.valueOf(lat), Double.valueOf(lon)});
        } else {
            return String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%.6f,%.6f&zoom=%d&size=%dx%d&maptype=roadmap&scale=%d", new Object[]{Double.valueOf(lat), Double.valueOf(lon), Integer.valueOf(zoom), Integer.valueOf(width), Integer.valueOf(height), Integer.valueOf(scale)});
        }
    }

    public static float getPixelsInCM(float cm, boolean isX) {
        float f = cm / 2.54f;
        DisplayMetrics displayMetrics2 = displayMetrics;
        return f * (isX ? displayMetrics2.xdpi : displayMetrics2.ydpi);
    }

    public static int getMyLayerVersion(int layer) {
        return 65535 & layer;
    }

    public static int getPeerLayerVersion(int layer) {
        return Math.max(73, (layer >> 16) & 65535);
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
        if (ApplicationLoader.applicationHandler != null) {
            if (delay == 0) {
                ApplicationLoader.applicationHandler.post(runnable);
            } else {
                ApplicationLoader.applicationHandler.postDelayed(runnable, delay);
            }
        }
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        if (ApplicationLoader.applicationHandler != null) {
            ApplicationLoader.applicationHandler.removeCallbacks(runnable);
        }
    }

    public static boolean isValidWallChar(char ch) {
        return ch == '-' || ch == '~';
    }

    public static boolean isTablet() {
        if (isTablet == null) {
            isTablet = Boolean.valueOf(ApplicationLoader.applicationContext != null && ApplicationLoader.applicationContext.getResources().getBoolean(NUM));
        }
        return isTablet.booleanValue();
    }

    public static boolean isSmallScreen() {
        if (isSmallScreen == null) {
            isSmallScreen = Boolean.valueOf(((float) ((Math.max(displaySize.x, displaySize.y) - statusBarHeight) - navigationBarHeight)) / density <= 650.0f);
        }
        return isSmallScreen.booleanValue();
    }

    public static boolean isSmallTablet() {
        return ((float) Math.min(displaySize.x, displaySize.y)) / density <= 690.0f;
    }

    public static int getMinTabletSide() {
        if (!isSmallTablet()) {
            int smallSide = Math.min(displaySize.x, displaySize.y);
            int leftSide = (smallSide * 35) / 100;
            if (leftSide < dp(320.0f)) {
                leftSide = dp(320.0f);
            }
            return smallSide - leftSide;
        }
        int smallSide2 = Math.min(displaySize.x, displaySize.y);
        int maxSide = Math.max(displaySize.x, displaySize.y);
        int leftSide2 = (maxSide * 35) / 100;
        if (leftSide2 < dp(320.0f)) {
            leftSide2 = dp(320.0f);
        }
        return Math.min(smallSide2, maxSide - leftSide2);
    }

    public static int getPhotoSize() {
        if (photoSize == null) {
            photoSize = 1280;
        }
        return photoSize.intValue();
    }

    public static void endIncomingCall() {
        if (hasCallPermissions) {
            try {
                TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                Method m = Class.forName(tm.getClass().getName()).getDeclaredMethod("getITelephony", new Class[0]);
                m.setAccessible(true);
                ITelephony iTelephony = (ITelephony) m.invoke(tm, new Object[0]);
                ITelephony telephonyService = (ITelephony) m.invoke(tm, new Object[0]);
                telephonyService.silenceRinger();
                telephonyService.endCall();
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    public static String obtainLoginPhoneCall(String pattern) {
        Cursor cursor;
        if (!hasCallPermissions) {
            return null;
        }
        try {
            cursor = ApplicationLoader.applicationContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{"number", "date"}, "type IN (3,1,5)", (String[]) null, "date DESC LIMIT 5");
            while (cursor.moveToNext()) {
                String number = cursor.getString(0);
                long date = cursor.getLong(1);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("number = " + number);
                }
                if (Math.abs(System.currentTimeMillis() - date) < 3600000) {
                    if (checkPhonePattern(pattern, number)) {
                        if (cursor != null) {
                            cursor.close();
                        }
                        return number;
                    }
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        } catch (Throwable th) {
        }
        return null;
        throw th;
    }

    public static boolean checkPhonePattern(String pattern, String phone) {
        if (TextUtils.isEmpty(pattern) || pattern.equals("*")) {
            return true;
        }
        String[] args = pattern.split("\\*");
        String phone2 = PhoneFormat.stripExceptNumbers(phone);
        int checkStart = 0;
        for (String arg : args) {
            if (!TextUtils.isEmpty(arg)) {
                int indexOf = phone2.indexOf(arg, checkStart);
                int index = indexOf;
                if (indexOf == -1) {
                    return false;
                }
                checkStart = arg.length() + index;
            }
        }
        return true;
    }

    public static int getViewInset(View view) {
        if (view == null || Build.VERSION.SDK_INT < 21 || view.getHeight() == displaySize.y || view.getHeight() == displaySize.y - statusBarHeight) {
            return 0;
        }
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                WindowInsets insets = view.getRootWindowInsets();
                if (insets != null) {
                    return insets.getStableInsetBottom();
                }
                return 0;
            }
            if (mAttachInfoField == null) {
                Field declaredField = View.class.getDeclaredField("mAttachInfo");
                mAttachInfoField = declaredField;
                declaredField.setAccessible(true);
            }
            Object mAttachInfo = mAttachInfoField.get(view);
            if (mAttachInfo != null) {
                if (mStableInsetsField == null) {
                    Field declaredField2 = mAttachInfo.getClass().getDeclaredField("mStableInsets");
                    mStableInsetsField = declaredField2;
                    declaredField2.setAccessible(true);
                }
                return ((Rect) mStableInsetsField.get(mAttachInfo)).bottom;
            }
            return 0;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static Point getRealScreenSize() {
        Point size = new Point();
        try {
            WindowManager windowManager = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
            if (Build.VERSION.SDK_INT >= 17) {
                windowManager.getDefaultDisplay().getRealSize(size);
            } else {
                try {
                    size.set(((Integer) Display.class.getMethod("getRawWidth", new Class[0]).invoke(windowManager.getDefaultDisplay(), new Object[0])).intValue(), ((Integer) Display.class.getMethod("getRawHeight", new Class[0]).invoke(windowManager.getDefaultDisplay(), new Object[0])).intValue());
                } catch (Exception e) {
                    size.set(windowManager.getDefaultDisplay().getWidth(), windowManager.getDefaultDisplay().getHeight());
                    FileLog.e((Throwable) e);
                }
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
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

    public static int charSequenceIndexOf(CharSequence cs, CharSequence needle, int fromIndex) {
        for (int i = fromIndex; i < cs.length() - needle.length(); i++) {
            boolean eq = true;
            int j = 0;
            while (true) {
                if (j >= needle.length()) {
                    break;
                } else if (needle.charAt(j) != cs.charAt(i + j)) {
                    eq = false;
                    break;
                } else {
                    j++;
                }
            }
            if (eq) {
                return i;
            }
        }
        return -1;
    }

    public static int charSequenceIndexOf(CharSequence cs, CharSequence needle) {
        return charSequenceIndexOf(cs, needle, 0);
    }

    public static boolean charSequenceContains(CharSequence cs, CharSequence needle) {
        return charSequenceIndexOf(cs, needle) != -1;
    }

    public static CharSequence getTrimmedString(CharSequence src) {
        if (src == null || src.length() == 0) {
            return src;
        }
        while (src.length() > 0 && (src.charAt(0) == 10 || src.charAt(0) == ' ')) {
            src = src.subSequence(1, src.length());
        }
        while (src.length() > 0 && (src.charAt(src.length() - 1) == 10 || src.charAt(src.length() - 1) == ' ')) {
            src = src.subSequence(0, src.length() - 1);
        }
        return src;
    }

    public static void setViewPagerEdgeEffectColor(ViewPager viewPager, int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                Field field = ViewPager.class.getDeclaredField("mLeftEdge");
                field.setAccessible(true);
                EdgeEffect mLeftEdge = (EdgeEffect) field.get(viewPager);
                if (mLeftEdge != null) {
                    mLeftEdge.setColor(color);
                }
                Field field2 = ViewPager.class.getDeclaredField("mRightEdge");
                field2.setAccessible(true);
                EdgeEffect mRightEdge = (EdgeEffect) field2.get(viewPager);
                if (mRightEdge != null) {
                    mRightEdge.setColor(color);
                }
            } catch (Exception e) {
            }
        }
    }

    public static void setScrollViewEdgeEffectColor(HorizontalScrollView scrollView, int color) {
        if (Build.VERSION.SDK_INT >= 29) {
            scrollView.setEdgeEffectColor(color);
        } else if (Build.VERSION.SDK_INT >= 21) {
            try {
                Field field = HorizontalScrollView.class.getDeclaredField("mEdgeGlowLeft");
                field.setAccessible(true);
                EdgeEffect mEdgeGlowTop = (EdgeEffect) field.get(scrollView);
                if (mEdgeGlowTop != null) {
                    mEdgeGlowTop.setColor(color);
                }
                Field field2 = HorizontalScrollView.class.getDeclaredField("mEdgeGlowRight");
                field2.setAccessible(true);
                EdgeEffect mEdgeGlowBottom = (EdgeEffect) field2.get(scrollView);
                if (mEdgeGlowBottom != null) {
                    mEdgeGlowBottom.setColor(color);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public static void setScrollViewEdgeEffectColor(ScrollView scrollView, int color) {
        if (Build.VERSION.SDK_INT >= 29) {
            scrollView.setTopEdgeEffectColor(color);
            scrollView.setBottomEdgeEffectColor(color);
        } else if (Build.VERSION.SDK_INT >= 21) {
            try {
                Field field = ScrollView.class.getDeclaredField("mEdgeGlowTop");
                field.setAccessible(true);
                EdgeEffect mEdgeGlowTop = (EdgeEffect) field.get(scrollView);
                if (mEdgeGlowTop != null) {
                    mEdgeGlowTop.setColor(color);
                }
                Field field2 = ScrollView.class.getDeclaredField("mEdgeGlowBottom");
                field2.setAccessible(true);
                EdgeEffect mEdgeGlowBottom = (EdgeEffect) field2.get(scrollView);
                if (mEdgeGlowBottom != null) {
                    mEdgeGlowBottom.setColor(color);
                }
            } catch (Exception e) {
            }
        }
    }

    public static void clearDrawableAnimation(View view) {
        if (Build.VERSION.SDK_INT >= 21 && view != null) {
            if (view instanceof ListView) {
                Drawable drawable = ((ListView) view).getSelector();
                if (drawable != null) {
                    drawable.setState(StateSet.NOTHING);
                    return;
                }
                return;
            }
            Drawable drawable2 = view.getBackground();
            if (drawable2 != null) {
                drawable2.setState(StateSet.NOTHING);
                drawable2.jumpToCurrentState();
            }
        }
    }

    public static SpannableStringBuilder replaceTags(String str) {
        return replaceTags(str, 11, new Object[0]);
    }

    public static SpannableStringBuilder replaceTags(String str, int flag, Object... args) {
        try {
            StringBuilder stringBuilder = new StringBuilder(str);
            if ((flag & 1) != 0) {
                while (true) {
                    int indexOf = stringBuilder.indexOf("<br>");
                    int start = indexOf;
                    if (indexOf == -1) {
                        break;
                    }
                    stringBuilder.replace(start, start + 4, "\n");
                }
                while (true) {
                    int indexOf2 = stringBuilder.indexOf("<br/>");
                    int start2 = indexOf2;
                    if (indexOf2 == -1) {
                        break;
                    }
                    stringBuilder.replace(start2, start2 + 5, "\n");
                }
            }
            ArrayList<Integer> bolds = new ArrayList<>();
            if ((flag & 2) != 0) {
                while (true) {
                    int indexOf3 = stringBuilder.indexOf("<b>");
                    int start3 = indexOf3;
                    if (indexOf3 == -1) {
                        break;
                    }
                    stringBuilder.replace(start3, start3 + 3, "");
                    int end = stringBuilder.indexOf("</b>");
                    if (end == -1) {
                        end = stringBuilder.indexOf("<b>");
                    }
                    stringBuilder.replace(end, end + 4, "");
                    bolds.add(Integer.valueOf(start3));
                    bolds.add(Integer.valueOf(end));
                }
                while (true) {
                    int indexOf4 = stringBuilder.indexOf("**");
                    int start4 = indexOf4;
                    if (indexOf4 == -1) {
                        break;
                    }
                    stringBuilder.replace(start4, start4 + 2, "");
                    int end2 = stringBuilder.indexOf("**");
                    if (end2 >= 0) {
                        stringBuilder.replace(end2, end2 + 2, "");
                        bolds.add(Integer.valueOf(start4));
                        bolds.add(Integer.valueOf(end2));
                    }
                }
            }
            if ((flag & 8) != 0) {
                while (true) {
                    int indexOf5 = stringBuilder.indexOf("**");
                    int start5 = indexOf5;
                    if (indexOf5 == -1) {
                        break;
                    }
                    stringBuilder.replace(start5, start5 + 2, "");
                    int end3 = stringBuilder.indexOf("**");
                    if (end3 >= 0) {
                        stringBuilder.replace(end3, end3 + 2, "");
                        bolds.add(Integer.valueOf(start5));
                        bolds.add(Integer.valueOf(end3));
                    }
                }
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringBuilder);
            for (int a = 0; a < bolds.size() / 2; a++) {
                spannableStringBuilder.setSpan(new TypefaceSpan(getTypeface("fonts/rmedium.ttf")), bolds.get(a * 2).intValue(), bolds.get((a * 2) + 1).intValue(), 33);
            }
            return spannableStringBuilder;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return new SpannableStringBuilder(str);
        }
    }

    public static class LinkMovementMethodMy extends LinkMovementMethod {
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                boolean result = super.onTouchEvent(widget, buffer, event);
                if (event.getAction() == 1 || event.getAction() == 3) {
                    Selection.removeSelection(buffer);
                }
                return result;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return false;
            }
        }
    }

    public static boolean needShowPasscode() {
        return needShowPasscode(false);
    }

    public static boolean needShowPasscode(boolean reset) {
        boolean wasInBackground = ForegroundDetector.getInstance().isWasInBackground(reset);
        if (reset) {
            ForegroundDetector.getInstance().resetBackgroundVar();
        }
        int uptime = (int) (SystemClock.elapsedRealtime() / 1000);
        if (BuildVars.LOGS_ENABLED && reset && SharedConfig.passcodeHash.length() > 0) {
            FileLog.d("wasInBackground = " + wasInBackground + " appLocked = " + SharedConfig.appLocked + " autoLockIn = " + SharedConfig.autoLockIn + " lastPauseTime = " + SharedConfig.lastPauseTime + " uptime = " + uptime);
        }
        return SharedConfig.passcodeHash.length() > 0 && wasInBackground && (SharedConfig.appLocked || ((SharedConfig.autoLockIn != 0 && SharedConfig.lastPauseTime != 0 && !SharedConfig.appLocked && SharedConfig.lastPauseTime + SharedConfig.autoLockIn <= uptime) || uptime + 5 < SharedConfig.lastPauseTime));
    }

    public static void shakeView(final View view, final float x, final int num) {
        if (view != null) {
            if (num == 6) {
                view.setTranslationX(0.0f);
                return;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, "translationX", new float[]{(float) dp(x)})});
            animatorSet.setDuration(50);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    View view = view;
                    int i = num;
                    AndroidUtilities.shakeView(view, i == 5 ? 0.0f : -x, i + 1);
                }
            });
            animatorSet.start();
        }
    }

    public static void shakeViewSpring(View view) {
        shakeViewSpring(view, 10.0f, (Runnable) null);
    }

    public static void shakeViewSpring(View view, float shiftDp) {
        shakeViewSpring(view, shiftDp, (Runnable) null);
    }

    public static void shakeViewSpring(View view, Runnable endCallback) {
        shakeViewSpring(view, 10.0f, endCallback);
    }

    public static void shakeViewSpring(View view, float shiftDp, Runnable endCallback) {
        ((SpringAnimation) ((SpringAnimation) new SpringAnimation(view, DynamicAnimation.TRANSLATION_X, 0.0f).setSpring(new SpringForce(0.0f).setStiffness(600.0f)).setStartVelocity((float) ((-dp(shiftDp)) * 100))).addEndListener(new AndroidUtilities$$ExternalSyntheticLambda11(endCallback))).start();
    }

    static /* synthetic */ void lambda$shakeViewSpring$6(Runnable endCallback, DynamicAnimation animation, boolean canceled, float value, float velocity) {
        if (endCallback != null) {
            endCallback.run();
        }
    }

    public static void startAppCenter(Activity context) {
        if (!BuildConfig.DEBUG) {
            try {
                if (BuildVars.DEBUG_VERSION) {
                    Distribute.setEnabledForDebuggableBuild(true);
                    AppCenter.start(context.getApplication(), BuildVars.APPCENTER_HASH, Distribute.class, Crashes.class);
                    AppCenter.setUserId("uid=" + UserConfig.getInstance(UserConfig.selectedAccount).clientUserId);
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    public static void checkForUpdates() {
        try {
            if (BuildVars.DEBUG_VERSION && SystemClock.elapsedRealtime() - lastUpdateCheckTime >= 3600000) {
                lastUpdateCheckTime = SystemClock.elapsedRealtime();
                Distribute.checkForUpdate();
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public static void appCenterLog(Throwable e) {
        try {
            Crashes.trackError(e);
        } catch (Throwable th) {
        }
    }

    public static boolean shouldShowClipboardToast() {
        return Build.VERSION.SDK_INT < 31 || !OneUIUtilities.hasBuiltInClipboardToasts();
    }

    public static void addToClipboard(CharSequence str) {
        try {
            ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", str));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void addMediaToGallery(String fromPath) {
        if (fromPath != null) {
            addMediaToGallery(new File(fromPath));
        }
    }

    public static void addMediaToGallery(File file) {
        Uri uri = Uri.fromFile(file);
        if (uri != null) {
            try {
                Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                mediaScanIntent.setData(uri);
                ApplicationLoader.applicationContext.sendBroadcast(mediaScanIntent);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    private static File getAlbumDir(boolean secretChat) {
        if (secretChat || !BuildVars.NO_SCOPED_STORAGE || (Build.VERSION.SDK_INT >= 23 && ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0)) {
            return FileLoader.getDirectory(0);
        }
        File storageDir = null;
        if ("mounted".equals(Environment.getExternalStorageState())) {
            storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Telegram");
            if (!storageDir.mkdirs() && !storageDir.exists()) {
                if (!BuildVars.LOGS_ENABLED) {
                    return null;
                }
                FileLog.d("failed to create directory");
                return null;
            }
        } else if (BuildVars.LOGS_ENABLED) {
            FileLog.d("External storage is not mounted READ/WRITE.");
        }
        return storageDir;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getPath(android.net.Uri r11) {
        /*
            java.lang.String r0 = "_id=?"
            r1 = 0
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x00ed }
            r3 = 19
            r4 = 1
            r5 = 0
            if (r2 < r3) goto L_0x000d
            r2 = 1
            goto L_0x000e
        L_0x000d:
            r2 = 0
        L_0x000e:
            if (r2 == 0) goto L_0x00c8
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00ed }
            boolean r3 = android.provider.DocumentsContract.isDocumentUri(r3, r11)     // Catch:{ Exception -> 0x00ed }
            if (r3 == 0) goto L_0x00c8
            boolean r3 = isExternalStorageDocument(r11)     // Catch:{ Exception -> 0x00ed }
            java.lang.String r6 = ":"
            if (r3 == 0) goto L_0x004f
            java.lang.String r0 = android.provider.DocumentsContract.getDocumentId(r11)     // Catch:{ Exception -> 0x00ed }
            java.lang.String[] r3 = r0.split(r6)     // Catch:{ Exception -> 0x00ed }
            r5 = r3[r5]     // Catch:{ Exception -> 0x00ed }
            java.lang.String r6 = "primary"
            boolean r6 = r6.equalsIgnoreCase(r5)     // Catch:{ Exception -> 0x00ed }
            if (r6 == 0) goto L_0x004d
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00ed }
            r6.<init>()     // Catch:{ Exception -> 0x00ed }
            java.io.File r7 = android.os.Environment.getExternalStorageDirectory()     // Catch:{ Exception -> 0x00ed }
            r6.append(r7)     // Catch:{ Exception -> 0x00ed }
            java.lang.String r7 = "/"
            r6.append(r7)     // Catch:{ Exception -> 0x00ed }
            r4 = r3[r4]     // Catch:{ Exception -> 0x00ed }
            r6.append(r4)     // Catch:{ Exception -> 0x00ed }
            java.lang.String r1 = r6.toString()     // Catch:{ Exception -> 0x00ed }
            return r1
        L_0x004d:
            goto L_0x00ec
        L_0x004f:
            boolean r3 = isDownloadsDocument(r11)     // Catch:{ Exception -> 0x00ed }
            if (r3 == 0) goto L_0x0072
            java.lang.String r0 = android.provider.DocumentsContract.getDocumentId(r11)     // Catch:{ Exception -> 0x00ed }
            java.lang.String r3 = "content://downloads/public_downloads"
            android.net.Uri r3 = android.net.Uri.parse(r3)     // Catch:{ Exception -> 0x00ed }
            java.lang.Long r4 = java.lang.Long.valueOf(r0)     // Catch:{ Exception -> 0x00ed }
            long r4 = r4.longValue()     // Catch:{ Exception -> 0x00ed }
            android.net.Uri r3 = android.content.ContentUris.withAppendedId(r3, r4)     // Catch:{ Exception -> 0x00ed }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00ed }
            java.lang.String r1 = getDataColumn(r4, r3, r1, r1)     // Catch:{ Exception -> 0x00ed }
            return r1
        L_0x0072:
            boolean r3 = isMediaDocument(r11)     // Catch:{ Exception -> 0x00ed }
            if (r3 == 0) goto L_0x00ec
            java.lang.String r3 = android.provider.DocumentsContract.getDocumentId(r11)     // Catch:{ Exception -> 0x00ed }
            java.lang.String[] r6 = r3.split(r6)     // Catch:{ Exception -> 0x00ed }
            r7 = r6[r5]     // Catch:{ Exception -> 0x00ed }
            r8 = 0
            r9 = -1
            int r10 = r7.hashCode()     // Catch:{ Exception -> 0x00ed }
            switch(r10) {
                case 93166550: goto L_0x00a0;
                case 100313435: goto L_0x0096;
                case 112202875: goto L_0x008c;
                default: goto L_0x008b;
            }     // Catch:{ Exception -> 0x00ed }
        L_0x008b:
            goto L_0x00a9
        L_0x008c:
            java.lang.String r10 = "video"
            boolean r10 = r7.equals(r10)     // Catch:{ Exception -> 0x00ed }
            if (r10 == 0) goto L_0x008b
            r9 = 1
            goto L_0x00a9
        L_0x0096:
            java.lang.String r10 = "image"
            boolean r10 = r7.equals(r10)     // Catch:{ Exception -> 0x00ed }
            if (r10 == 0) goto L_0x008b
            r9 = 0
            goto L_0x00a9
        L_0x00a0:
            java.lang.String r10 = "audio"
            boolean r10 = r7.equals(r10)     // Catch:{ Exception -> 0x00ed }
            if (r10 == 0) goto L_0x008b
            r9 = 2
        L_0x00a9:
            switch(r9) {
                case 0: goto L_0x00b5;
                case 1: goto L_0x00b1;
                case 2: goto L_0x00ad;
                default: goto L_0x00ac;
            }     // Catch:{ Exception -> 0x00ed }
        L_0x00ac:
            goto L_0x00b9
        L_0x00ad:
            android.net.Uri r9 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x00ed }
            r8 = r9
            goto L_0x00b9
        L_0x00b1:
            android.net.Uri r9 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x00ed }
            r8 = r9
            goto L_0x00b9
        L_0x00b5:
            android.net.Uri r9 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x00ed }
            r8 = r9
        L_0x00b9:
            r9 = r0
            java.lang.String[] r10 = new java.lang.String[r4]     // Catch:{ Exception -> 0x00ed }
            r4 = r6[r4]     // Catch:{ Exception -> 0x00ed }
            r10[r5] = r4     // Catch:{ Exception -> 0x00ed }
            r4 = r10
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00ed }
            java.lang.String r0 = getDataColumn(r5, r8, r0, r4)     // Catch:{ Exception -> 0x00ed }
            return r0
        L_0x00c8:
            java.lang.String r0 = "content"
            java.lang.String r3 = r11.getScheme()     // Catch:{ Exception -> 0x00ed }
            boolean r0 = r0.equalsIgnoreCase(r3)     // Catch:{ Exception -> 0x00ed }
            if (r0 == 0) goto L_0x00db
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00ed }
            java.lang.String r0 = getDataColumn(r0, r11, r1, r1)     // Catch:{ Exception -> 0x00ed }
            return r0
        L_0x00db:
            java.lang.String r0 = "file"
            java.lang.String r3 = r11.getScheme()     // Catch:{ Exception -> 0x00ed }
            boolean r0 = r0.equalsIgnoreCase(r3)     // Catch:{ Exception -> 0x00ed }
            if (r0 == 0) goto L_0x00ec
            java.lang.String r0 = r11.getPath()     // Catch:{ Exception -> 0x00ed }
            return r0
        L_0x00ec:
            goto L_0x00f1
        L_0x00ed:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00f1:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.getPath(android.net.Uri):java.lang.String");
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor;
        try {
            cursor = context.getContentResolver().query(uri, new String[]{"_data"}, selection, selectionArgs, (String) null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    String value = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
                    if (value.startsWith("content://") || (!value.startsWith("/") && !value.startsWith("file://"))) {
                        if (cursor != null) {
                            cursor.close();
                        }
                        return null;
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                    return value;
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
        } catch (Throwable th) {
        }
        return null;
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
        return generatePicturePath(false, (String) null);
    }

    public static File generatePicturePath(boolean secretChat, String ext) {
        try {
            File publicDir = FileLoader.getDirectory(100);
            if (!secretChat) {
                if (publicDir != null) {
                    return new File(publicDir, generateFileName(0, ext));
                }
            }
            return new File(ApplicationLoader.applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), generateFileName(0, ext));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    public static String generateFileName(int type, String ext) {
        Date date = new Date();
        date.setTime(System.currentTimeMillis() + ((long) Utilities.random.nextInt(1000)) + 1);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(date);
        if (type == 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("IMG_");
            sb.append(timeStamp);
            sb.append(".");
            sb.append(TextUtils.isEmpty(ext) ? "jpg" : ext);
            return sb.toString();
        }
        return "VID_" + timeStamp + ".mp4";
    }

    public static CharSequence generateSearchName(String name, String name2, String q) {
        if ((name == null && name2 == null) || TextUtils.isEmpty(q)) {
            return "";
        }
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String wholeString = name;
        if (wholeString == null || wholeString.length() == 0) {
            wholeString = name2;
        } else if (!(name2 == null || name2.length() == 0)) {
            wholeString = wholeString + " " + name2;
        }
        String wholeString2 = wholeString.trim();
        String lower = " " + wholeString2.toLowerCase();
        int lastIndex = 0;
        while (true) {
            int indexOf = lower.indexOf(" " + q, lastIndex);
            int index = indexOf;
            if (indexOf == -1) {
                break;
            }
            int i = 1;
            int idx = index - (index == 0 ? 0 : 1);
            int length = q.length();
            if (index == 0) {
                i = 0;
            }
            int end = length + i + idx;
            if (lastIndex != 0 && lastIndex != idx + 1) {
                builder.append(wholeString2.substring(lastIndex, idx));
            } else if (lastIndex == 0 && idx != 0) {
                builder.append(wholeString2.substring(0, idx));
            }
            String query = wholeString2.substring(idx, Math.min(wholeString2.length(), end));
            if (query.startsWith(" ")) {
                builder.append(" ");
            }
            String query2 = query.trim();
            int start = builder.length();
            builder.append(query2);
            builder.setSpan(new ForegroundColorSpanThemable("windowBackgroundWhiteBlueText4"), start, query2.length() + start, 33);
            lastIndex = end;
        }
        if (lastIndex != -1 && lastIndex < wholeString2.length()) {
            builder.append(wholeString2.substring(lastIndex));
        }
        return builder;
    }

    public static boolean isKeyguardSecure() {
        return ((KeyguardManager) ApplicationLoader.applicationContext.getSystemService("keyguard")).isKeyguardSecure();
    }

    public static boolean isSimAvailable() {
        TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
        int state = tm.getSimState();
        return (state == 1 || state == 0 || tm.getPhoneType() == 0 || isAirplaneModeOn()) ? false : true;
    }

    public static boolean isAirplaneModeOn() {
        if (Build.VERSION.SDK_INT < 17) {
            if (Settings.System.getInt(ApplicationLoader.applicationContext.getContentResolver(), "airplane_mode_on", 0) != 0) {
                return true;
            }
            return false;
        } else if (Settings.Global.getInt(ApplicationLoader.applicationContext.getContentResolver(), "airplane_mode_on", 0) != 0) {
            return true;
        } else {
            return false;
        }
    }

    public static File generateVideoPath() {
        return generateVideoPath(false);
    }

    public static File generateVideoPath(boolean secretChat) {
        try {
            File storageDir = getAlbumDir(secretChat);
            Date date = new Date();
            date.setTime(System.currentTimeMillis() + ((long) Utilities.random.nextInt(1000)) + 1);
            if (generatingVideoPathFormat == null) {
                generatingVideoPathFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US);
            }
            String timeStamp = generatingVideoPathFormat.format(date);
            return new File(storageDir, "VID_" + timeStamp + ".mp4");
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    public static String formatFileSize(long size) {
        return formatFileSize(size, false);
    }

    public static String formatFileSize(long size, boolean removeZero) {
        if (size < 1024) {
            return String.format("%d B", new Object[]{Long.valueOf(size)});
        } else if (size < 1048576) {
            float value = ((float) size) / 1024.0f;
            if (!removeZero || (value - ((float) ((int) value))) * 10.0f != 0.0f) {
                return String.format("%.1f KB", new Object[]{Float.valueOf(value)});
            }
            return String.format("%d KB", new Object[]{Integer.valueOf((int) value)});
        } else if (size < NUM) {
            float value2 = (((float) size) / 1024.0f) / 1024.0f;
            if (!removeZero || (value2 - ((float) ((int) value2))) * 10.0f != 0.0f) {
                return String.format("%.1f MB", new Object[]{Float.valueOf(value2)});
            }
            return String.format("%d MB", new Object[]{Integer.valueOf((int) value2)});
        } else {
            float value3 = ((float) ((int) ((size / 1024) / 1024))) / 1000.0f;
            if (!removeZero || (value3 - ((float) ((int) value3))) * 10.0f != 0.0f) {
                return String.format("%.2f GB", new Object[]{Float.valueOf(value3)});
            }
            return String.format("%d GB", new Object[]{Integer.valueOf((int) value3)});
        }
    }

    public static String formatShortDuration(int duration) {
        return formatDuration(duration, false);
    }

    public static String formatLongDuration(int duration) {
        return formatDuration(duration, true);
    }

    public static String formatDuration(int duration, boolean isLong) {
        int h = duration / 3600;
        int m = (duration / 60) % 60;
        int s = duration % 60;
        if (h != 0) {
            return String.format(Locale.US, "%d:%02d:%02d", new Object[]{Integer.valueOf(h), Integer.valueOf(m), Integer.valueOf(s)});
        } else if (isLong) {
            return String.format(Locale.US, "%02d:%02d", new Object[]{Integer.valueOf(m), Integer.valueOf(s)});
        } else {
            return String.format(Locale.US, "%d:%02d", new Object[]{Integer.valueOf(m), Integer.valueOf(s)});
        }
    }

    public static String formatFullDuration(int duration) {
        int h = duration / 3600;
        int m = (duration / 60) % 60;
        int s = duration % 60;
        if (duration < 0) {
            return String.format(Locale.US, "-%02d:%02d:%02d", new Object[]{Integer.valueOf(Math.abs(h)), Integer.valueOf(Math.abs(m)), Integer.valueOf(Math.abs(s))});
        }
        return String.format(Locale.US, "%02d:%02d:%02d", new Object[]{Integer.valueOf(h), Integer.valueOf(m), Integer.valueOf(s)});
    }

    public static String formatDurationNoHours(int duration, boolean isLong) {
        int m = duration / 60;
        int s = duration % 60;
        if (isLong) {
            return String.format(Locale.US, "%02d:%02d", new Object[]{Integer.valueOf(m), Integer.valueOf(s)});
        }
        return String.format(Locale.US, "%d:%02d", new Object[]{Integer.valueOf(m), Integer.valueOf(s)});
    }

    public static String formatShortDuration(int progress, int duration) {
        return formatDuration(progress, duration, false);
    }

    public static String formatLongDuration(int progress, int duration) {
        return formatDuration(progress, duration, true);
    }

    public static String formatDuration(int progress, int duration, boolean isLong) {
        int i = progress;
        int i2 = duration;
        int h = i2 / 3600;
        int m = (i2 / 60) % 60;
        int s = i2 % 60;
        int ph = i / 3600;
        int pm = (i / 60) % 60;
        int ps = i % 60;
        if (i2 == 0) {
            if (ph != 0) {
                return String.format(Locale.US, "%d:%02d:%02d / -:--", new Object[]{Integer.valueOf(ph), Integer.valueOf(pm), Integer.valueOf(ps)});
            } else if (isLong) {
                return String.format(Locale.US, "%02d:%02d / -:--", new Object[]{Integer.valueOf(pm), Integer.valueOf(ps)});
            } else {
                return String.format(Locale.US, "%d:%02d / -:--", new Object[]{Integer.valueOf(pm), Integer.valueOf(ps)});
            }
        } else if (ph != 0 || h != 0) {
            return String.format(Locale.US, "%d:%02d:%02d / %d:%02d:%02d", new Object[]{Integer.valueOf(ph), Integer.valueOf(pm), Integer.valueOf(ps), Integer.valueOf(h), Integer.valueOf(m), Integer.valueOf(s)});
        } else if (isLong) {
            return String.format(Locale.US, "%02d:%02d / %02d:%02d", new Object[]{Integer.valueOf(pm), Integer.valueOf(ps), Integer.valueOf(m), Integer.valueOf(s)});
        } else {
            return String.format(Locale.US, "%d:%02d / %d:%02d", new Object[]{Integer.valueOf(pm), Integer.valueOf(ps), Integer.valueOf(m), Integer.valueOf(s)});
        }
    }

    public static String formatVideoDuration(int progress, int duration) {
        int i = progress;
        int i2 = duration;
        int h = i2 / 3600;
        int m = (i2 / 60) % 60;
        int s = i2 % 60;
        int ph = i / 3600;
        int pm = (i / 60) % 60;
        int ps = i % 60;
        if (ph == 0 && h == 0) {
            return String.format(Locale.US, "%02d:%02d / %02d:%02d", new Object[]{Integer.valueOf(pm), Integer.valueOf(ps), Integer.valueOf(m), Integer.valueOf(s)});
        } else if (h == 0) {
            return String.format(Locale.US, "%d:%02d:%02d / %02d:%02d", new Object[]{Integer.valueOf(ph), Integer.valueOf(pm), Integer.valueOf(ps), Integer.valueOf(m), Integer.valueOf(s)});
        } else if (ph == 0) {
            return String.format(Locale.US, "%02d:%02d / %d:%02d:%02d", new Object[]{Integer.valueOf(pm), Integer.valueOf(ps), Integer.valueOf(h), Integer.valueOf(m), Integer.valueOf(s)});
        } else {
            return String.format(Locale.US, "%d:%02d:%02d / %d:%02d:%02d", new Object[]{Integer.valueOf(ph), Integer.valueOf(pm), Integer.valueOf(ps), Integer.valueOf(h), Integer.valueOf(m), Integer.valueOf(s)});
        }
    }

    public static String formatCount(int count) {
        if (count < 1000) {
            return Integer.toString(count);
        }
        ArrayList<String> strings = new ArrayList<>();
        while (count != 0) {
            int mod = count % 1000;
            count /= 1000;
            if (count > 0) {
                strings.add(String.format(Locale.ENGLISH, "%03d", new Object[]{Integer.valueOf(mod)}));
            } else {
                strings.add(Integer.toString(mod));
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = strings.size() - 1; i >= 0; i--) {
            stringBuilder.append(strings.get(i));
            if (i != 0) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    public static String formatWholeNumber(int v, int dif) {
        if (v == 0) {
            return "0";
        }
        float num_ = (float) v;
        int count = 0;
        if (dif == 0) {
            dif = v;
        }
        if (dif < 1000) {
            return formatCount(v);
        }
        while (dif >= 1000 && count < numbersSignatureArray.length - 1) {
            dif /= 1000;
            num_ /= 1000.0f;
            count++;
        }
        if (((double) num_) < 0.1d) {
            return "0";
        }
        if (num_ * 10.0f == ((float) ((int) (num_ * 10.0f)))) {
            return String.format(Locale.ENGLISH, "%s%s", new Object[]{formatCount((int) num_), numbersSignatureArray[count]});
        }
        return String.format(Locale.ENGLISH, "%.1f%s", new Object[]{Float.valueOf(((float) ((int) (num_ * 10.0f))) / 10.0f), numbersSignatureArray[count]});
    }

    public static byte[] decodeQuotedPrintable(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int i = 0;
        while (i < bytes.length) {
            byte b = bytes[i];
            if (b == 61) {
                int i2 = i + 1;
                try {
                    int u = Character.digit((char) bytes[i2], 16);
                    i = i2 + 1;
                    buffer.write((char) ((u << 4) + Character.digit((char) bytes[i], 16)));
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                    return null;
                }
            } else {
                buffer.write(b);
            }
            i++;
        }
        byte[] array = buffer.toByteArray();
        try {
            buffer.close();
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        return array;
    }

    public static boolean copyFile(InputStream sourceFile, File destFile) throws IOException {
        return copyFile(sourceFile, (OutputStream) new FileOutputStream(destFile));
    }

    public static boolean copyFile(InputStream sourceFile, OutputStream out) throws IOException {
        byte[] buf = new byte[4096];
        while (true) {
            int read = sourceFile.read(buf);
            int len = read;
            if (read > 0) {
                Thread.yield();
                out.write(buf, 0, len);
            } else {
                out.close();
                return true;
            }
        }
    }

    public static boolean copyFile(File sourceFile, File destFile) throws IOException {
        FileOutputStream destination;
        if (sourceFile.equals(destFile)) {
            return true;
        }
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        try {
            FileInputStream source = new FileInputStream(sourceFile);
            try {
                destination = new FileOutputStream(destFile);
                destination.getChannel().transferFrom(source.getChannel(), 0, source.getChannel().size());
                destination.close();
                source.close();
                return true;
            } catch (Throwable th) {
                source.close();
                throw th;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        } catch (Throwable th2) {
        }
        throw th;
    }

    public static byte[] calcAuthKeyHash(byte[] auth_key) {
        byte[] key_hash = new byte[16];
        System.arraycopy(Utilities.computeSHA1(auth_key), 0, key_hash, 0, 16);
        return key_hash;
    }

    public static void openDocument(MessageObject message, Activity activity, BaseFragment parentFragment) {
        TLRPC.Document document;
        File f;
        String str;
        String str2;
        MessageObject messageObject = message;
        Activity activity2 = activity;
        BaseFragment baseFragment = parentFragment;
        if (messageObject != null && (document = message.getDocument()) != null) {
            File f2 = null;
            String fileName = messageObject.messageOwner.media != null ? FileLoader.getAttachFileName(document) : "";
            if (!(messageObject.messageOwner.attachPath == null || messageObject.messageOwner.attachPath.length() == 0)) {
                f2 = new File(messageObject.messageOwner.attachPath);
            }
            if (f2 == null || (f2 != null && !f2.exists())) {
                f = FileLoader.getInstance(UserConfig.selectedAccount).getPathToMessage(messageObject.messageOwner);
            } else {
                f = f2;
            }
            if (f != null && f.exists()) {
                if (baseFragment == null || !f.getName().toLowerCase().endsWith("attheme")) {
                    String realMimeType = null;
                    try {
                        Intent intent = new Intent("android.intent.action.VIEW");
                        intent.setFlags(1);
                        MimeTypeMap myMime = MimeTypeMap.getSingleton();
                        int idx = fileName.lastIndexOf(46);
                        if (idx != -1 && (realMimeType = myMime.getMimeTypeFromExtension(fileName.substring(idx + 1).toLowerCase())) == null && ((realMimeType = document.mime_type) == null || realMimeType.length() == 0)) {
                            realMimeType = null;
                        }
                        if (Build.VERSION.SDK_INT >= 24) {
                            Uri uriForFile = FileProvider.getUriForFile(activity2, "org.telegram.messenger.beta.provider", f);
                            if (realMimeType != null) {
                                str2 = realMimeType;
                            } else {
                                str2 = "text/plain";
                            }
                            intent.setDataAndType(uriForFile, str2);
                        } else {
                            Uri fromFile = Uri.fromFile(f);
                            if (realMimeType != null) {
                                str = realMimeType;
                            } else {
                                str = "text/plain";
                            }
                            intent.setDataAndType(fromFile, str);
                        }
                        if (realMimeType != null) {
                            try {
                                activity2.startActivityForResult(intent, 500);
                            } catch (Exception e) {
                                Exception exc = e;
                                if (Build.VERSION.SDK_INT >= 24) {
                                    intent.setDataAndType(FileProvider.getUriForFile(activity2, "org.telegram.messenger.beta.provider", f), "text/plain");
                                } else {
                                    intent.setDataAndType(Uri.fromFile(f), "text/plain");
                                }
                                activity2.startActivityForResult(intent, 500);
                            }
                        } else {
                            activity2.startActivityForResult(intent, 500);
                        }
                    } catch (Exception e2) {
                        if (activity2 != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity2);
                            builder.setTitle(LocaleController.getString("AppName", NUM));
                            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                            builder.setMessage(LocaleController.formatString("NoHandleAppInstalled", NUM, message.getDocument().mime_type));
                            if (baseFragment != null) {
                                baseFragment.showDialog(builder.create());
                            } else {
                                builder.show();
                            }
                        }
                    }
                } else {
                    Theme.ThemeInfo themeInfo = Theme.applyThemeFile(f, message.getDocumentName(), (TLRPC.TL_theme) null, true);
                    if (themeInfo != null) {
                        baseFragment.presentFragment(new ThemePreviewActivity(themeInfo));
                        return;
                    }
                    AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) activity2);
                    builder2.setTitle(LocaleController.getString("AppName", NUM));
                    builder2.setMessage(LocaleController.getString("IncorrectTheme", NUM));
                    builder2.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                    baseFragment.showDialog(builder2.create());
                }
            }
        }
    }

    /*  JADX ERROR: JadxRuntimeException in pass: CodeShrinkVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Don't wrap MOVE or CONST insns: 0x0031: MOVE  (r0v6 'realMimeType' java.lang.String) = (r13v0 'mimeType' java.lang.String)
        	at jadx.core.dex.instructions.args.InsnArg.wrapArg(InsnArg.java:164)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.assignInline(CodeShrinkVisitor.java:133)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.checkInline(CodeShrinkVisitor.java:118)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkBlock(CodeShrinkVisitor.java:65)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkMethod(CodeShrinkVisitor.java:43)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.visit(CodeShrinkVisitor.java:35)
        */
    public static boolean openForView(java.io.File r11, java.lang.String r12, java.lang.String r13, android.app.Activity r14, org.telegram.ui.ActionBar.Theme.ResourcesProvider r15) {
        /*
            if (r11 == 0) goto L_0x00a6
            boolean r0 = r11.exists()
            if (r0 == 0) goto L_0x00a6
            r0 = 0
            android.content.Intent r1 = new android.content.Intent
            java.lang.String r2 = "android.intent.action.VIEW"
            r1.<init>(r2)
            r2 = 1
            r1.setFlags(r2)
            android.webkit.MimeTypeMap r3 = android.webkit.MimeTypeMap.getSingleton()
            r4 = 46
            int r4 = r12.lastIndexOf(r4)
            r5 = -1
            if (r4 == r5) goto L_0x003b
            int r5 = r4 + 1
            java.lang.String r5 = r12.substring(r5)
            java.lang.String r6 = r5.toLowerCase()
            java.lang.String r0 = r3.getMimeTypeFromExtension(r6)
            if (r0 != 0) goto L_0x003b
            r0 = r13
            if (r0 == 0) goto L_0x003a
            int r6 = r0.length()
            if (r6 != 0) goto L_0x003b
        L_0x003a:
            r0 = 0
        L_0x003b:
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 26
            if (r5 < r6) goto L_0x005f
            if (r0 == 0) goto L_0x005f
            java.lang.String r5 = "application/vnd.android.package-archive"
            boolean r5 = r0.equals(r5)
            if (r5 == 0) goto L_0x005f
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.pm.PackageManager r5 = r5.getPackageManager()
            boolean r5 = r5.canRequestPackageInstalls()
            if (r5 != 0) goto L_0x005f
            android.app.Dialog r5 = org.telegram.ui.Components.AlertsCreator.createApkRestrictedDialog(r14, r15)
            r5.show()
            return r2
        L_0x005f:
            int r5 = android.os.Build.VERSION.SDK_INT
            java.lang.String r6 = "org.telegram.messenger.beta.provider"
            r7 = 24
            java.lang.String r8 = "text/plain"
            if (r5 < r7) goto L_0x0076
            android.net.Uri r5 = androidx.core.content.FileProvider.getUriForFile(r14, r6, r11)
            if (r0 == 0) goto L_0x0071
            r9 = r0
            goto L_0x0072
        L_0x0071:
            r9 = r8
        L_0x0072:
            r1.setDataAndType(r5, r9)
            goto L_0x0082
        L_0x0076:
            android.net.Uri r5 = android.net.Uri.fromFile(r11)
            if (r0 == 0) goto L_0x007e
            r9 = r0
            goto L_0x007f
        L_0x007e:
            r9 = r8
        L_0x007f:
            r1.setDataAndType(r5, r9)
        L_0x0082:
            r5 = 500(0x1f4, float:7.0E-43)
            if (r0 == 0) goto L_0x00a2
            r14.startActivityForResult(r1, r5)     // Catch:{ Exception -> 0x008a }
        L_0x0089:
            goto L_0x00a5
        L_0x008a:
            r9 = move-exception
            int r10 = android.os.Build.VERSION.SDK_INT
            if (r10 < r7) goto L_0x0097
            android.net.Uri r6 = androidx.core.content.FileProvider.getUriForFile(r14, r6, r11)
            r1.setDataAndType(r6, r8)
            goto L_0x009e
        L_0x0097:
            android.net.Uri r6 = android.net.Uri.fromFile(r11)
            r1.setDataAndType(r6, r8)
        L_0x009e:
            r14.startActivityForResult(r1, r5)
            goto L_0x0089
        L_0x00a2:
            r14.startActivityForResult(r1, r5)
        L_0x00a5:
            return r2
        L_0x00a6:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.openForView(java.io.File, java.lang.String, java.lang.String, android.app.Activity, org.telegram.ui.ActionBar.Theme$ResourcesProvider):boolean");
    }

    public static boolean openForView(MessageObject message, Activity activity, Theme.ResourcesProvider resourcesProvider) {
        File f = null;
        if (!(message.messageOwner.attachPath == null || message.messageOwner.attachPath.length() == 0)) {
            f = new File(message.messageOwner.attachPath);
        }
        if (f == null || !f.exists()) {
            f = FileLoader.getInstance(message.currentAccount).getPathToMessage(message.messageOwner);
        }
        return openForView(f, message.getFileName(), (message.type == 9 || message.type == 0) ? message.getMimeType() : null, activity, resourcesProvider);
    }

    public static boolean openForView(TLRPC.Document document, boolean forceCache, Activity activity) {
        return openForView(FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(document, true), FileLoader.getAttachFileName(document), document.mime_type, activity, (Theme.ResourcesProvider) null);
    }

    public static SpannableStringBuilder formatSpannableSimple(String format, CharSequence... cs) {
        return formatSpannable(format, AndroidUtilities$$ExternalSyntheticLambda4.INSTANCE, cs);
    }

    static /* synthetic */ String lambda$formatSpannableSimple$7(Integer i) {
        return "%s";
    }

    public static SpannableStringBuilder formatSpannable(String format, CharSequence... cs) {
        if (format.contains("%s")) {
            return formatSpannableSimple(format, cs);
        }
        return formatSpannable(format, AndroidUtilities$$ExternalSyntheticLambda3.INSTANCE, cs);
    }

    static /* synthetic */ String lambda$formatSpannable$8(Integer i) {
        return "%" + (i.intValue() + 1) + "$s";
    }

    public static SpannableStringBuilder formatSpannable(String format, GenericProvider<Integer, String> keysProvider, CharSequence... cs) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(format);
        for (int i = 0; i < cs.length; i++) {
            String key = keysProvider.provide(Integer.valueOf(i));
            int j = format.indexOf(key);
            if (j != -1) {
                stringBuilder.replace(j, key.length() + j, cs[i]);
                format = format.substring(0, j) + cs[i].toString() + format.substring(key.length() + j);
            }
        }
        return stringBuilder;
    }

    public static CharSequence replaceTwoNewLinesToOne(CharSequence original) {
        char[] buf = new char[2];
        if (original instanceof StringBuilder) {
            StringBuilder stringBuilder = (StringBuilder) original;
            int a = 0;
            int N = original.length();
            while (a < N - 2) {
                stringBuilder.getChars(a, a + 2, buf, 0);
                if (buf[0] == 10 && buf[1] == 10) {
                    stringBuilder = stringBuilder.replace(a, a + 2, "\n");
                    a--;
                    N--;
                }
                a++;
            }
            return original;
        } else if (!(original instanceof SpannableStringBuilder)) {
            return original.toString().replace("\n\n", "\n");
        } else {
            SpannableStringBuilder stringBuilder2 = (SpannableStringBuilder) original;
            int a2 = 0;
            int N2 = original.length();
            while (a2 < N2 - 2) {
                stringBuilder2.getChars(a2, a2 + 2, buf, 0);
                if (buf[0] == 10 && buf[1] == 10) {
                    stringBuilder2 = stringBuilder2.replace(a2, a2 + 2, "\n");
                    a2--;
                    N2--;
                }
                a2++;
            }
            return original;
        }
    }

    public static CharSequence replaceNewLines(CharSequence original) {
        if (original instanceof StringBuilder) {
            StringBuilder stringBuilder = (StringBuilder) original;
            int N = original.length();
            for (int a = 0; a < N; a++) {
                if (original.charAt(a) == 10) {
                    stringBuilder.setCharAt(a, ' ');
                }
            }
            return original;
        } else if (!(original instanceof SpannableStringBuilder)) {
            return original.toString().replace(10, ' ');
        } else {
            SpannableStringBuilder stringBuilder2 = (SpannableStringBuilder) original;
            int N2 = original.length();
            for (int a2 = 0; a2 < N2; a2++) {
                if (original.charAt(a2) == 10) {
                    stringBuilder2.replace(a2, a2 + 1, " ");
                }
            }
            return original;
        }
    }

    public static boolean openForView(TLObject media, Activity activity) {
        String str;
        String str2;
        if (media == null || activity == null) {
            return false;
        }
        String fileName = FileLoader.getAttachFileName(media);
        File f = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(media, true);
        if (f == null || !f.exists()) {
            return false;
        }
        String realMimeType = null;
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setFlags(1);
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        int idx = fileName.lastIndexOf(46);
        if (idx != -1 && (realMimeType = myMime.getMimeTypeFromExtension(fileName.substring(idx + 1).toLowerCase())) == null) {
            if (media instanceof TLRPC.TL_document) {
                realMimeType = ((TLRPC.TL_document) media).mime_type;
            }
            if (realMimeType == null || realMimeType.length() == 0) {
                realMimeType = null;
            }
        }
        if (Build.VERSION.SDK_INT >= 24) {
            Uri uriForFile = FileProvider.getUriForFile(activity, "org.telegram.messenger.beta.provider", f);
            if (realMimeType != null) {
                str2 = realMimeType;
            } else {
                str2 = "text/plain";
            }
            intent.setDataAndType(uriForFile, str2);
        } else {
            Uri fromFile = Uri.fromFile(f);
            if (realMimeType != null) {
                str = realMimeType;
            } else {
                str = "text/plain";
            }
            intent.setDataAndType(fromFile, str);
        }
        if (realMimeType != null) {
            try {
                activity.startActivityForResult(intent, 500);
            } catch (Exception e) {
                if (Build.VERSION.SDK_INT >= 24) {
                    intent.setDataAndType(FileProvider.getUriForFile(activity, "org.telegram.messenger.beta.provider", f), "text/plain");
                } else {
                    intent.setDataAndType(Uri.fromFile(f), "text/plain");
                }
                activity.startActivityForResult(intent, 500);
            }
        } else {
            activity.startActivityForResult(intent, 500);
        }
        return true;
    }

    public static boolean isBannedForever(TLRPC.TL_chatBannedRights rights) {
        return rights == null || Math.abs(((long) rights.until_date) - (System.currentTimeMillis() / 1000)) > NUM;
    }

    public static void setRectToRect(Matrix matrix, RectF src, RectF dst, int rotation, boolean translate) {
        float sy;
        float sx;
        float ty;
        float tx;
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
        float diff2 = diff / 2.0f;
        if (xLarger) {
            tx += diff2;
        } else {
            ty += diff2;
        }
        matrix.preScale(sx, sy);
        if (translate) {
            matrix.preTranslate(tx, ty);
        }
    }

    public static Vibrator getVibrator() {
        if (vibrator == null) {
            vibrator = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
        }
        return vibrator;
    }

    public static boolean isAccessibilityTouchExplorationEnabled() {
        if (accessibilityManager == null) {
            accessibilityManager = (AccessibilityManager) ApplicationLoader.applicationContext.getSystemService("accessibility");
        }
        return accessibilityManager.isEnabled() && accessibilityManager.isTouchExplorationEnabled();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x006f, code lost:
        if (r11.startsWith("tg://socks") == false) goto L_0x0120;
     */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0130  */
    /* JADX WARNING: Removed duplicated region for block: B:78:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean handleProxyIntent(android.app.Activity r19, android.content.Intent r20) {
        /*
            java.lang.String r0 = "tg:proxy"
            java.lang.String r1 = "tg://telegram.org"
            r2 = 0
            if (r20 != 0) goto L_0x0008
            return r2
        L_0x0008:
            int r3 = r20.getFlags()     // Catch:{ Exception -> 0x0148 }
            r4 = 1048576(0x100000, float:1.469368E-39)
            r3 = r3 & r4
            if (r3 == 0) goto L_0x0012
            return r2
        L_0x0012:
            android.net.Uri r3 = r20.getData()     // Catch:{ Exception -> 0x0148 }
            if (r3 == 0) goto L_0x0147
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            java.lang.String r9 = r3.getScheme()     // Catch:{ Exception -> 0x0148 }
            if (r9 == 0) goto L_0x011c
            java.lang.String r11 = "http"
            boolean r11 = r9.equals(r11)     // Catch:{ Exception -> 0x0148 }
            java.lang.String r12 = "secret"
            java.lang.String r13 = "pass"
            java.lang.String r14 = "user"
            java.lang.String r15 = "port"
            java.lang.String r2 = "server"
            if (r11 != 0) goto L_0x00b5
            java.lang.String r11 = "https"
            boolean r11 = r9.equals(r11)     // Catch:{ Exception -> 0x0148 }
            if (r11 == 0) goto L_0x0043
            r17 = r4
            r18 = r5
            goto L_0x00b9
        L_0x0043:
            java.lang.String r11 = "tg"
            boolean r11 = r9.equals(r11)     // Catch:{ Exception -> 0x0148 }
            if (r11 == 0) goto L_0x00af
            java.lang.String r11 = r3.toString()     // Catch:{ Exception -> 0x0148 }
            boolean r16 = r11.startsWith(r0)     // Catch:{ Exception -> 0x0148 }
            java.lang.String r10 = "tg://socks"
            r17 = r4
            java.lang.String r4 = "tg:socks"
            r18 = r5
            java.lang.String r5 = "tg://proxy"
            if (r16 != 0) goto L_0x0071
            boolean r16 = r11.startsWith(r5)     // Catch:{ Exception -> 0x0148 }
            if (r16 != 0) goto L_0x0071
            boolean r16 = r11.startsWith(r4)     // Catch:{ Exception -> 0x0148 }
            if (r16 != 0) goto L_0x0071
            boolean r16 = r11.startsWith(r10)     // Catch:{ Exception -> 0x0148 }
            if (r16 == 0) goto L_0x0120
        L_0x0071:
            java.lang.String r0 = r11.replace(r0, r1)     // Catch:{ Exception -> 0x0148 }
            java.lang.String r0 = r0.replace(r5, r1)     // Catch:{ Exception -> 0x0148 }
            java.lang.String r0 = r0.replace(r10, r1)     // Catch:{ Exception -> 0x0148 }
            java.lang.String r0 = r0.replace(r4, r1)     // Catch:{ Exception -> 0x0148 }
            android.net.Uri r1 = android.net.Uri.parse(r0)     // Catch:{ Exception -> 0x0148 }
            r3 = r1
            java.lang.String r1 = r3.getQueryParameter(r2)     // Catch:{ Exception -> 0x0148 }
            boolean r2 = checkHostForPunycode(r1)     // Catch:{ Exception -> 0x0148 }
            if (r2 == 0) goto L_0x0098
            r2 = 1
            java.lang.String r4 = java.net.IDN.toASCII(r1, r2)     // Catch:{ Exception -> 0x0148 }
            r1 = r4
            r7 = r1
            goto L_0x0099
        L_0x0098:
            r7 = r1
        L_0x0099:
            java.lang.String r1 = r3.getQueryParameter(r15)     // Catch:{ Exception -> 0x0148 }
            r6 = r1
            java.lang.String r1 = r3.getQueryParameter(r14)     // Catch:{ Exception -> 0x0148 }
            r4 = r1
            java.lang.String r1 = r3.getQueryParameter(r13)     // Catch:{ Exception -> 0x0148 }
            r5 = r1
            java.lang.String r1 = r3.getQueryParameter(r12)     // Catch:{ Exception -> 0x0148 }
            r8 = r1
            goto L_0x0124
        L_0x00af:
            r17 = r4
            r18 = r5
            goto L_0x0120
        L_0x00b5:
            r17 = r4
            r18 = r5
        L_0x00b9:
            java.lang.String r0 = r3.getHost()     // Catch:{ Exception -> 0x0148 }
            java.lang.String r0 = r0.toLowerCase()     // Catch:{ Exception -> 0x0148 }
            java.lang.String r1 = "telegram.me"
            boolean r1 = r0.equals(r1)     // Catch:{ Exception -> 0x0148 }
            if (r1 != 0) goto L_0x00d9
            java.lang.String r1 = "t.me"
            boolean r1 = r0.equals(r1)     // Catch:{ Exception -> 0x0148 }
            if (r1 != 0) goto L_0x00d9
            java.lang.String r1 = "telegram.dog"
            boolean r1 = r0.equals(r1)     // Catch:{ Exception -> 0x0148 }
            if (r1 == 0) goto L_0x0117
        L_0x00d9:
            java.lang.String r1 = r3.getPath()     // Catch:{ Exception -> 0x0148 }
            if (r1 == 0) goto L_0x0117
            java.lang.String r4 = "/socks"
            boolean r4 = r1.startsWith(r4)     // Catch:{ Exception -> 0x0148 }
            if (r4 != 0) goto L_0x00ef
            java.lang.String r4 = "/proxy"
            boolean r4 = r1.startsWith(r4)     // Catch:{ Exception -> 0x0148 }
            if (r4 == 0) goto L_0x0117
        L_0x00ef:
            java.lang.String r2 = r3.getQueryParameter(r2)     // Catch:{ Exception -> 0x0148 }
            boolean r4 = checkHostForPunycode(r2)     // Catch:{ Exception -> 0x0148 }
            if (r4 == 0) goto L_0x0101
            r4 = 1
            java.lang.String r5 = java.net.IDN.toASCII(r2, r4)     // Catch:{ Exception -> 0x0148 }
            r2 = r5
            r7 = r2
            goto L_0x0102
        L_0x0101:
            r7 = r2
        L_0x0102:
            java.lang.String r2 = r3.getQueryParameter(r15)     // Catch:{ Exception -> 0x0148 }
            r6 = r2
            java.lang.String r2 = r3.getQueryParameter(r14)     // Catch:{ Exception -> 0x0148 }
            r4 = r2
            java.lang.String r2 = r3.getQueryParameter(r13)     // Catch:{ Exception -> 0x0148 }
            r5 = r2
            java.lang.String r2 = r3.getQueryParameter(r12)     // Catch:{ Exception -> 0x0148 }
            r8 = r2
            goto L_0x011b
        L_0x0117:
            r4 = r17
            r5 = r18
        L_0x011b:
            goto L_0x0124
        L_0x011c:
            r17 = r4
            r18 = r5
        L_0x0120:
            r4 = r17
            r5 = r18
        L_0x0124:
            boolean r0 = android.text.TextUtils.isEmpty(r7)     // Catch:{ Exception -> 0x0148 }
            if (r0 != 0) goto L_0x0147
            boolean r0 = android.text.TextUtils.isEmpty(r6)     // Catch:{ Exception -> 0x0148 }
            if (r0 != 0) goto L_0x0147
            java.lang.String r0 = ""
            if (r4 != 0) goto L_0x0135
            r4 = r0
        L_0x0135:
            if (r5 != 0) goto L_0x0138
            r5 = r0
        L_0x0138:
            if (r8 != 0) goto L_0x013b
            r8 = r0
        L_0x013b:
            r10 = r19
            r11 = r7
            r12 = r6
            r13 = r4
            r14 = r5
            r15 = r8
            showProxyAlert(r10, r11, r12, r13, r14, r15)     // Catch:{ Exception -> 0x0148 }
            r0 = 1
            return r0
        L_0x0147:
            goto L_0x0149
        L_0x0148:
            r0 = move-exception
        L_0x0149:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.handleProxyIntent(android.app.Activity, android.content.Intent):boolean");
    }

    public static boolean shouldEnableAnimation() {
        if (Build.VERSION.SDK_INT < 26 || Build.VERSION.SDK_INT >= 28) {
            return true;
        }
        if (!((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).isPowerSaveMode() && Settings.Global.getFloat(ApplicationLoader.applicationContext.getContentResolver(), "animator_duration_scale", 1.0f) > 0.0f) {
            return true;
        }
        return false;
    }

    public static void showProxyAlert(Activity activity, String address, String port, String user, String password, String secret) {
        Activity activity2 = activity;
        BottomSheet.Builder builder = new BottomSheet.Builder(activity2);
        Runnable dismissRunnable = builder.getDismissRunnable();
        builder.setApplyTopPadding(false);
        builder.setApplyBottomPadding(false);
        LinearLayout linearLayout = new LinearLayout(activity2);
        builder.setCustomView(linearLayout);
        boolean z = true;
        linearLayout.setOrientation(1);
        int i = 5;
        if (!TextUtils.isEmpty(secret)) {
            TextView titleTextView = new TextView(activity2);
            titleTextView.setText(LocaleController.getString("UseProxyTelegramInfo2", NUM));
            titleTextView.setTextColor(Theme.getColor("dialogTextGray4"));
            titleTextView.setTextSize(1, 14.0f);
            titleTextView.setGravity(49);
            linearLayout.addView(titleTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 17, 8, 17, 8));
            View lineView = new View(activity2);
            lineView.setBackgroundColor(Theme.getColor("divider"));
            linearLayout.addView(lineView, new LinearLayout.LayoutParams(-1, 1));
        }
        int a = 0;
        while (true) {
            if (a >= i) {
                String str = port;
                break;
            }
            String text = null;
            String detail = null;
            if (a == 0) {
                text = address;
                detail = LocaleController.getString("UseProxyAddress", NUM);
                String str2 = port;
            } else if (a == z) {
                text = "" + port;
                detail = LocaleController.getString("UseProxyPort", NUM);
            } else {
                String str3 = port;
                if (a == 2) {
                    text = secret;
                    detail = LocaleController.getString("UseProxySecret", NUM);
                } else if (a == 3) {
                    text = user;
                    detail = LocaleController.getString("UseProxyUsername", NUM);
                } else if (a == 4) {
                    text = password;
                    detail = LocaleController.getString("UseProxyPassword", NUM);
                }
            }
            if (!TextUtils.isEmpty(text)) {
                TextDetailSettingsCell cell = new TextDetailSettingsCell(activity2);
                cell.setTextAndValue(text, detail, z);
                cell.getTextView().setTextColor(Theme.getColor("dialogTextBlack"));
                cell.getValueTextView().setTextColor(Theme.getColor("dialogTextGray3"));
                linearLayout.addView(cell, LayoutHelper.createLinear(-1, -2));
                if (a == 2) {
                    break;
                }
            }
            a++;
            z = true;
            i = 5;
        }
        PickerBottomLayout pickerBottomLayout = new PickerBottomLayout(activity2, false);
        pickerBottomLayout.setBackgroundColor(Theme.getColor("dialogBackground"));
        linearLayout.addView(pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        pickerBottomLayout.cancelButton.setPadding(dp(18.0f), 0, dp(18.0f), 0);
        pickerBottomLayout.cancelButton.setTextColor(Theme.getColor("dialogTextBlue2"));
        pickerBottomLayout.cancelButton.setText(LocaleController.getString("Cancel", NUM).toUpperCase());
        pickerBottomLayout.cancelButton.setOnClickListener(new AndroidUtilities$$ExternalSyntheticLambda9(dismissRunnable));
        pickerBottomLayout.doneButtonTextView.setTextColor(Theme.getColor("dialogTextBlue2"));
        pickerBottomLayout.doneButton.setPadding(dp(18.0f), 0, dp(18.0f), 0);
        pickerBottomLayout.doneButtonBadgeTextView.setVisibility(8);
        pickerBottomLayout.doneButtonTextView.setText(LocaleController.getString("ConnectingConnectProxy", NUM).toUpperCase());
        pickerBottomLayout.doneButton.setOnClickListener(new AndroidUtilities$$ExternalSyntheticLambda10(address, port, secret, password, user, dismissRunnable));
        builder.show();
    }

    static /* synthetic */ void lambda$showProxyAlert$10(String address, String port, String secret, String password, String user, Runnable dismissRunnable, View v) {
        SharedConfig.ProxyInfo info;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("proxy_enabled", true);
        editor.putString("proxy_ip", address);
        int p = Utilities.parseInt((CharSequence) port).intValue();
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
            info = new SharedConfig.ProxyInfo(address, p, user, password, "");
        } else {
            editor.remove("proxy_pass");
            editor.remove("proxy_user");
            editor.putString("proxy_secret", secret);
            info = new SharedConfig.ProxyInfo(address, p, "", "", secret);
        }
        editor.commit();
        SharedConfig.currentProxy = SharedConfig.addProxy(info);
        ConnectionsManager.setProxySettings(true, address, p, user, password, secret);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
        dismissRunnable.run();
    }

    public static String getSystemProperty(String key) {
        try {
            return (String) Class.forName("android.os.SystemProperties").getMethod("get", new Class[]{String.class}).invoke((Object) null, new Object[]{key});
        } catch (Exception e) {
            return null;
        }
    }

    public static void fixGoogleMapsBug() {
        SharedPreferences googleBug = ApplicationLoader.applicationContext.getSharedPreferences("google_bug_NUM", 0);
        if (!googleBug.contains("fixed")) {
            new File(ApplicationLoader.getFilesDirFixed(), "ZoomTables.data").delete();
            googleBug.edit().putBoolean("fixed", true).apply();
        }
    }

    public static CharSequence concat(CharSequence... text) {
        if (text.length == 0) {
            return "";
        }
        int i = 0;
        if (text.length == 1) {
            return text[0];
        }
        boolean spanned = false;
        int length = text.length;
        int i2 = 0;
        while (true) {
            if (i2 >= length) {
                break;
            } else if (text[i2] instanceof Spanned) {
                spanned = true;
                break;
            } else {
                i2++;
            }
        }
        if (spanned) {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            int length2 = text.length;
            while (i < length2) {
                CharSequence piece = text[i];
                ssb.append(piece == null ? "null" : piece);
                i++;
            }
            return new SpannedString(ssb);
        }
        StringBuilder sb = new StringBuilder();
        int length3 = text.length;
        while (i < length3) {
            sb.append(text[i]);
            i++;
        }
        return sb.toString();
    }

    public static float[] RGBtoHSB(int r, int g, int b) {
        float saturation;
        float hue;
        float hue2;
        float[] hsbvals = new float[3];
        int cmax = Math.max(r, g);
        if (b > cmax) {
            cmax = b;
        }
        int cmin = Math.min(r, g);
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
                hue2 = bluec - greenc;
            } else if (g == cmax) {
                hue2 = (2.0f + redc) - bluec;
            } else {
                hue2 = (4.0f + greenc) - redc;
            }
            float hue3 = hue2 / 6.0f;
            if (hue3 < 0.0f) {
                hue = 1.0f + hue3;
            } else {
                hue = hue3;
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
            float p = (1.0f - saturation) * brightness;
            float q = (1.0f - (saturation * f)) * brightness;
            float t = (1.0f - ((1.0f - f) * saturation)) * brightness;
            switch ((int) h) {
                case 0:
                    r = (int) ((brightness * 255.0f) + 0.5f);
                    g = (int) ((t * 255.0f) + 0.5f);
                    b = (int) ((255.0f * p) + 0.5f);
                    break;
                case 1:
                    r = (int) ((q * 255.0f) + 0.5f);
                    g = (int) ((brightness * 255.0f) + 0.5f);
                    b = (int) ((255.0f * p) + 0.5f);
                    break;
                case 2:
                    r = (int) ((p * 255.0f) + 0.5f);
                    g = (int) ((brightness * 255.0f) + 0.5f);
                    b = (int) ((255.0f * t) + 0.5f);
                    break;
                case 3:
                    r = (int) ((p * 255.0f) + 0.5f);
                    g = (int) ((q * 255.0f) + 0.5f);
                    b = (int) ((255.0f * brightness) + 0.5f);
                    break;
                case 4:
                    r = (int) ((t * 255.0f) + 0.5f);
                    g = (int) ((p * 255.0f) + 0.5f);
                    b = (int) ((255.0f * brightness) + 0.5f);
                    break;
                case 5:
                    r = (int) ((brightness * 255.0f) + 0.5f);
                    g = (int) ((p * 255.0f) + 0.5f);
                    b = (int) ((255.0f * q) + 0.5f);
                    break;
            }
        } else {
            int i = (int) ((255.0f * brightness) + 0.5f);
            b = i;
            g = i;
            r = i;
        }
        return -16777216 | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
    }

    public static float computePerceivedBrightness(int color) {
        return (((((float) Color.red(color)) * 0.2126f) + (((float) Color.green(color)) * 0.7152f)) + (((float) Color.blue(color)) * 0.0722f)) / 255.0f;
    }

    public static int getPatternColor(int color) {
        return getPatternColor(color, false);
    }

    public static int getPatternColor(int color, boolean alwaysDark) {
        float[] hsb = RGBtoHSB(Color.red(color), Color.green(color), Color.blue(color));
        if (hsb[1] > 0.0f || (hsb[2] < 1.0f && hsb[2] > 0.0f)) {
            hsb[1] = Math.min(1.0f, hsb[1] + (alwaysDark ? 0.15f : 0.05f) + ((1.0f - hsb[1]) * 0.1f));
        }
        if (alwaysDark || hsb[2] > 0.5f) {
            hsb[2] = Math.max(0.0f, hsb[2] * 0.65f);
        } else {
            hsb[2] = Math.max(0.0f, Math.min(1.0f, 1.0f - (hsb[2] * 0.65f)));
        }
        return HSBtoRGB(hsb[0], hsb[1], hsb[2]) & (alwaysDark ? -NUM : NUM);
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

    public static int getWallpaperRotation(int angle, boolean iOS) {
        int angle2;
        if (iOS) {
            angle2 = angle + 180;
        } else {
            angle2 = angle - 180;
        }
        while (angle2 >= 360) {
            angle2 -= 360;
        }
        while (angle2 < 0) {
            angle2 += 360;
        }
        return angle2;
    }

    public static String getWallPaperUrl(Object object) {
        if (object instanceof TLRPC.TL_wallPaper) {
            TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) object;
            String link = "https://" + MessagesController.getInstance(UserConfig.selectedAccount).linkPrefix + "/bg/" + wallPaper.slug;
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
            if (modes.length() <= 0) {
                return link;
            }
            return link + "?mode=" + modes.toString();
        } else if (object instanceof WallpapersListActivity.ColorWallpaper) {
            return ((WallpapersListActivity.ColorWallpaper) object).getUrl();
        } else {
            return null;
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

    public static int getOffsetColor(int color1, int color2, float offset, float alpha) {
        int rF = Color.red(color2);
        int gF = Color.green(color2);
        int bF = Color.blue(color2);
        int aF = Color.alpha(color2);
        int rS = Color.red(color1);
        int gS = Color.green(color1);
        int bS = Color.blue(color1);
        int aS = Color.alpha(color1);
        return Color.argb((int) ((((float) aS) + (((float) (aF - aS)) * offset)) * alpha), (int) (((float) rS) + (((float) (rF - rS)) * offset)), (int) (((float) gS) + (((float) (gF - gS)) * offset)), (int) (((float) bS) + (((float) (bF - bS)) * offset)));
    }

    public static int indexOfIgnoreCase(String origin, String searchStr) {
        if (searchStr.isEmpty() || origin.isEmpty()) {
            return origin.indexOf(searchStr);
        }
        int i = 0;
        while (i < origin.length() && searchStr.length() + i <= origin.length()) {
            int j = 0;
            int ii = i;
            while (ii < origin.length() && j < searchStr.length() && Character.toLowerCase(origin.charAt(ii)) == Character.toLowerCase(searchStr.charAt(j))) {
                j++;
                ii++;
            }
            if (j == searchStr.length()) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static int lerp(int a, int b, float f) {
        return (int) (((float) a) + (((float) (b - a)) * f));
    }

    public static float lerp(float a, float b, float f) {
        return ((b - a) * f) + a;
    }

    public static float lerp(float[] ab, float f) {
        return lerp(ab[0], ab[1], f);
    }

    public static int lerpColor(int a, int b, float f) {
        return Color.argb(lerp(Color.alpha(a), Color.alpha(b), f), lerp(Color.red(a), Color.red(b), f), lerp(Color.green(a), Color.green(b), f), lerp(Color.blue(a), Color.blue(b), f));
    }

    public static void lerp(RectF a, RectF b, float f, RectF to) {
        if (to != null) {
            to.set(lerp(a.left, b.left, f), lerp(a.top, b.top, f), lerp(a.right, b.right, f), lerp(a.bottom, b.bottom, f));
        }
    }

    public static void lerp(Rect a, Rect b, float f, Rect to) {
        if (to != null) {
            to.set(lerp(a.left, b.left, f), lerp(a.top, b.top, f), lerp(a.right, b.right, f), lerp(a.bottom, b.bottom, f));
        }
    }

    public static float computeDampingRatio(float tension, float friction, float mass) {
        return friction / (((float) Math.sqrt((double) (mass * tension))) * 2.0f);
    }

    public static boolean hasFlagSecureFragment() {
        return flagSecureFragment != null;
    }

    public static void setFlagSecure(BaseFragment parentFragment, boolean set) {
        if (parentFragment != null && parentFragment.getParentActivity() != null) {
            if (set) {
                try {
                    parentFragment.getParentActivity().getWindow().setFlags(8192, 8192);
                    flagSecureFragment = new WeakReference<>(parentFragment);
                } catch (Exception e) {
                }
            } else {
                WeakReference<BaseFragment> weakReference = flagSecureFragment;
                if (weakReference != null && weakReference.get() == parentFragment) {
                    try {
                        parentFragment.getParentActivity().getWindow().clearFlags(8192);
                    } catch (Exception e2) {
                    }
                    flagSecureFragment = null;
                }
            }
        }
    }

    public static Runnable registerFlagSecure(Window window) {
        ArrayList<Long> reasonIds;
        long reasonId = (long) (Math.random() * 9.99999999E8d);
        HashMap<Window, ArrayList<Long>> hashMap = flagSecureReasons;
        if (hashMap.containsKey(window)) {
            reasonIds = hashMap.get(window);
        } else {
            ArrayList<Long> reasonIds2 = new ArrayList<>();
            hashMap.put(window, reasonIds2);
            reasonIds = reasonIds2;
        }
        reasonIds.add(Long.valueOf(reasonId));
        updateFlagSecure(window);
        return new AndroidUtilities$$ExternalSyntheticLambda1(reasonIds, reasonId, window);
    }

    static /* synthetic */ void lambda$registerFlagSecure$11(ArrayList reasonIds, long reasonId, Window window) {
        reasonIds.remove(Long.valueOf(reasonId));
        updateFlagSecure(window);
    }

    private static void updateFlagSecure(Window window) {
        if (Build.VERSION.SDK_INT >= 23 && window != null) {
            HashMap<Window, ArrayList<Long>> hashMap = flagSecureReasons;
            if (hashMap.containsKey(window) && hashMap.get(window).size() > 0) {
                try {
                    window.addFlags(8192);
                } catch (Exception e) {
                }
            } else {
                window.clearFlags(8192);
            }
        }
    }

    public static void openSharing(BaseFragment fragment, String url) {
        if (fragment != null && fragment.getParentActivity() != null) {
            fragment.showDialog(new ShareAlert(fragment.getParentActivity(), (ArrayList<MessageObject>) null, url, false, url, false));
        }
    }

    public static boolean allowScreenCapture() {
        return SharedConfig.passcodeHash.length() == 0 || SharedConfig.allowScreenCapture;
    }

    public static File getSharingDirectory() {
        return new File(FileLoader.getDirectory(4), "sharing/");
    }

    public static String getCertificateSHA256Fingerprint() {
        try {
            return Utilities.bytesToHex(Utilities.computeSHA256(((X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(new ByteArrayInputStream(ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 64).signatures[0].toByteArray()))).getEncoded()));
        } catch (Throwable th) {
            return "";
        }
    }

    public static boolean isPunctuationCharacter(char ch) {
        if (charactersMap == null) {
            charactersMap = new HashSet<>();
            int a = 0;
            while (true) {
                char[] cArr = characters;
                if (a >= cArr.length) {
                    break;
                }
                charactersMap.add(Character.valueOf(cArr[a]));
                a++;
            }
        }
        return charactersMap.contains(Character.valueOf(ch));
    }

    public static int getColorDistance(int color1, int color2) {
        int r1 = Color.red(color1);
        int g1 = Color.green(color1);
        int b1 = Color.blue(color1);
        int r2 = Color.red(color2);
        int rMean = (r1 + r2) / 2;
        int r = r1 - r2;
        int g = g1 - Color.green(color2);
        int b = b1 - Color.blue(color2);
        return ((((rMean + 512) * r) * r) >> 8) + (g * 4 * g) + ((((767 - rMean) * b) * b) >> 8);
    }

    public static int getAverageColor(int color1, int color2) {
        return Color.argb(255, (Color.red(color1) / 2) + (Color.red(color2) / 2), (Color.green(color1) / 2) + (Color.green(color2) / 2), (Color.blue(color1) / 2) + (Color.blue(color2) / 2));
    }

    public static void setLightStatusBar(Window window, boolean enable) {
        setLightStatusBar(window, enable, false);
    }

    public static void setLightStatusBar(Window window, boolean enable, boolean forceTransparentStatusbar) {
        if (Build.VERSION.SDK_INT >= 23) {
            View decorView = window.getDecorView();
            int flags = decorView.getSystemUiVisibility();
            if (enable) {
                if ((flags & 8192) == 0) {
                    decorView.setSystemUiVisibility(flags | 8192);
                }
                if (SharedConfig.noStatusBar || forceTransparentStatusbar) {
                    window.setStatusBarColor(0);
                } else {
                    window.setStatusBarColor(NUM);
                }
            } else {
                if ((flags & 8192) != 0) {
                    decorView.setSystemUiVisibility(flags & -8193);
                }
                if (SharedConfig.noStatusBar || forceTransparentStatusbar) {
                    window.setStatusBarColor(0);
                } else {
                    window.setStatusBarColor(NUM);
                }
            }
        }
    }

    public static boolean getLightNavigationBar(Window window) {
        if (Build.VERSION.SDK_INT < 26 || (window.getDecorView().getSystemUiVisibility() & 16) <= 0) {
            return false;
        }
        return true;
    }

    public static void setLightNavigationBar(View view, boolean enable) {
        int flags;
        if (view != null && Build.VERSION.SDK_INT >= 26) {
            int flags2 = view.getSystemUiVisibility();
            if (enable) {
                flags = flags2 | 16;
            } else {
                flags = flags2 & -17;
            }
            view.setSystemUiVisibility(flags);
        }
    }

    public static void setLightNavigationBar(Window window, boolean enable) {
        if (window != null) {
            setLightNavigationBar(window.getDecorView(), enable);
        }
    }

    public static void setNavigationBarColor(Window window, int color) {
        setNavigationBarColor(window, color, true);
    }

    public static void setNavigationBarColor(Window window, int color, boolean animated) {
        setNavigationBarColor(window, color, animated, (IntColorCallback) null);
    }

    public static void setNavigationBarColor(final Window window, int color, boolean animated, IntColorCallback onUpdate) {
        ValueAnimator animator;
        if (Build.VERSION.SDK_INT >= 21) {
            HashMap<Window, ValueAnimator> hashMap = navigationBarColorAnimators;
            if (!(hashMap == null || (animator = hashMap.get(window)) == null)) {
                animator.cancel();
                navigationBarColorAnimators.remove(window);
            }
            if (!animated) {
                if (onUpdate != null) {
                    onUpdate.run(color);
                }
                try {
                    window.setNavigationBarColor(color);
                } catch (Exception e) {
                }
            } else {
                ValueAnimator animator2 = ValueAnimator.ofArgb(new int[]{window.getNavigationBarColor(), color});
                animator2.addUpdateListener(new AndroidUtilities$$ExternalSyntheticLambda6(onUpdate, window));
                animator2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (AndroidUtilities.navigationBarColorAnimators != null) {
                            AndroidUtilities.navigationBarColorAnimators.remove(window);
                        }
                    }
                });
                animator2.setDuration(200);
                animator2.setInterpolator(CubicBezierInterpolator.DEFAULT);
                animator2.start();
                if (navigationBarColorAnimators == null) {
                    navigationBarColorAnimators = new HashMap<>();
                }
                navigationBarColorAnimators.put(window, animator2);
            }
        }
    }

    static /* synthetic */ void lambda$setNavigationBarColor$12(IntColorCallback onUpdate, Window window, ValueAnimator a) {
        int tcolor = ((Integer) a.getAnimatedValue()).intValue();
        if (onUpdate != null) {
            onUpdate.run(tcolor);
        }
        try {
            window.setNavigationBarColor(tcolor);
        } catch (Exception e) {
        }
    }

    public static boolean checkHostForPunycode(String url) {
        if (url == null) {
            return false;
        }
        boolean hasLatin = false;
        boolean hasNonLatin = false;
        try {
            int N = url.length();
            for (int a = 0; a < N; a++) {
                char ch = url.charAt(a);
                if (!(ch == '.' || ch == '-' || ch == '/' || ch == '+' || (ch >= '0' && ch <= '9'))) {
                    if ((ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z')) {
                        hasNonLatin = true;
                    } else {
                        hasLatin = true;
                    }
                    if (hasLatin && hasNonLatin) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (!hasLatin || !hasNonLatin) {
            return false;
        }
        return true;
    }

    public static boolean shouldShowUrlInAlert(String url) {
        try {
            return checkHostForPunycode(Uri.parse(url).getHost());
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    public static void scrollToFragmentRow(ActionBarLayout parentLayout, String rowName) {
        if (parentLayout != null && rowName != null) {
            BaseFragment openingFragment = parentLayout.fragmentsStack.get(parentLayout.fragmentsStack.size() - 1);
            try {
                Field listViewField = openingFragment.getClass().getDeclaredField("listView");
                listViewField.setAccessible(true);
                RecyclerListView listView = (RecyclerListView) listViewField.get(openingFragment);
                listView.highlightRow(new AndroidUtilities$$ExternalSyntheticLambda5(openingFragment, rowName, listView));
                listViewField.setAccessible(false);
            } catch (Throwable th) {
            }
        }
    }

    static /* synthetic */ int lambda$scrollToFragmentRow$13(BaseFragment openingFragment, String rowName, RecyclerListView listView) {
        int position = -1;
        try {
            Field rowField = openingFragment.getClass().getDeclaredField(rowName);
            rowField.setAccessible(true);
            position = rowField.getInt(openingFragment);
            ((LinearLayoutManager) listView.getLayoutManager()).scrollToPositionWithOffset(position, dp(60.0f));
            rowField.setAccessible(false);
            return position;
        } catch (Throwable th) {
            return position;
        }
    }

    public static boolean checkInlinePermissions(Context context) {
        return Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(context);
    }

    public static void updateVisibleRows(RecyclerListView listView) {
        RecyclerView.Adapter adapter;
        RecyclerView.ViewHolder holder;
        if (listView != null && (adapter = listView.getAdapter()) != null) {
            for (int i = 0; i < listView.getChildCount(); i++) {
                View child = listView.getChildAt(i);
                int p = listView.getChildAdapterPosition(child);
                if (p >= 0 && (holder = listView.getChildViewHolder(child)) != null && !holder.shouldIgnore()) {
                    adapter.onBindViewHolder(holder, p);
                }
            }
        }
    }

    public static void updateImageViewImageAnimated(ImageView imageView, int newIcon) {
        updateImageViewImageAnimated(imageView, ContextCompat.getDrawable(imageView.getContext(), newIcon));
    }

    public static void updateImageViewImageAnimated(ImageView imageView, Drawable newIcon) {
        ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(150);
        animator.addUpdateListener(new AndroidUtilities$$ExternalSyntheticLambda0(imageView, new AtomicBoolean(), newIcon));
        animator.start();
    }

    static /* synthetic */ void lambda$updateImageViewImageAnimated$14(ImageView imageView, AtomicBoolean changed, Drawable newIcon, ValueAnimator animation) {
        float val = ((Float) animation.getAnimatedValue()).floatValue();
        float scale = Math.abs(val - 0.5f) + 0.5f;
        imageView.setScaleX(scale);
        imageView.setScaleY(scale);
        if (val >= 0.5f && !changed.get()) {
            changed.set(true);
            imageView.setImageDrawable(newIcon);
        }
    }

    public static void updateViewVisibilityAnimated(View view, boolean show) {
        updateViewVisibilityAnimated(view, show, 1.0f, true);
    }

    public static void updateViewVisibilityAnimated(View view, boolean show, float scaleFactor, boolean animated) {
        if (view != null) {
            if (view.getParent() == null) {
                animated = false;
            }
            int i = 0;
            int i2 = null;
            if (!animated) {
                view.animate().setListener((Animator.AnimatorListener) null).cancel();
                if (!show) {
                    i = 8;
                }
                view.setVisibility(i);
                if (show) {
                    i2 = 1;
                }
                view.setTag(i2);
                view.setAlpha(1.0f);
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
            } else if (show && view.getTag() == null) {
                view.animate().setListener((Animator.AnimatorListener) null).cancel();
                if (view.getVisibility() != 0) {
                    view.setVisibility(0);
                    view.setAlpha(0.0f);
                    view.setScaleX(scaleFactor);
                    view.setScaleY(scaleFactor);
                }
                view.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).setDuration(150).start();
                view.setTag(1);
            } else if (!show && view.getTag() != null) {
                view.animate().setListener((Animator.AnimatorListener) null).cancel();
                view.animate().alpha(0.0f).scaleY(scaleFactor).scaleX(scaleFactor).setListener(new HideViewAfterAnimation(view)).setDuration(150).start();
                view.setTag((Object) null);
            }
        }
    }

    public static long getPrefIntOrLong(SharedPreferences preferences, String key, long defaultValue) {
        try {
            return preferences.getLong(key, defaultValue);
        } catch (Exception e) {
            return (long) preferences.getInt(key, (int) defaultValue);
        }
    }

    public static Bitmap getScaledBitmap(float w, float h, String path, String streamPath, int streamOffset) {
        Bitmap wallpaper;
        FileInputStream stream = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            if (path != null) {
                BitmapFactory.decodeFile(path, options);
            } else {
                stream = new FileInputStream(streamPath);
                stream.getChannel().position((long) streamOffset);
                BitmapFactory.decodeStream(stream, (Rect) null, options);
            }
            if (options.outWidth <= 0 || options.outHeight <= 0) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                }
                return null;
            }
            if (w > h && options.outWidth < options.outHeight) {
                float temp = w;
                w = h;
                h = temp;
            }
            float scale = Math.min(((float) options.outWidth) / w, ((float) options.outHeight) / h);
            options.inSampleSize = 1;
            if (scale > 1.0f) {
                do {
                    options.inSampleSize *= 2;
                } while (((float) options.inSampleSize) < scale);
            }
            options.inJustDecodeBounds = false;
            if (path != null) {
                wallpaper = BitmapFactory.decodeFile(path, options);
            } else {
                stream.getChannel().position((long) streamOffset);
                wallpaper = BitmapFactory.decodeStream(stream, (Rect) null, options);
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e22) {
                    FileLog.e((Throwable) e22);
                }
            }
            return wallpaper;
        } catch (Throwable th) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e23) {
                    FileLog.e((Throwable) e23);
                }
            }
            throw th;
        }
    }

    public static Uri getBitmapShareUri(Bitmap bitmap, String fileName, Bitmap.CompressFormat format) {
        FileOutputStream out;
        File cachePath = getCacheDir();
        if (!cachePath.isDirectory()) {
            try {
                cachePath.mkdirs();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return null;
            }
        }
        File file = new File(cachePath, fileName);
        try {
            out = new FileOutputStream(file);
            bitmap.compress(format, 100, out);
            out.close();
            Uri uriForFile = FileProvider.getUriForFile(ApplicationLoader.applicationContext, "org.telegram.messenger.beta.provider", file);
            out.close();
            return uriForFile;
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
            return null;
        } catch (Throwable th) {
        }
        throw th;
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isAccessibilityScreenReaderEnabled() {
        return false;
    }
}
