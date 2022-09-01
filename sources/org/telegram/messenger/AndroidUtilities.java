package org.telegram.messenger;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
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
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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
import android.widget.EdgeEffect;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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
import org.telegram.messenger.utils.CustomHtml;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
import org.telegram.tgnet.TLRPC$TL_wallPaper;
import org.telegram.tgnet.TLRPC$WallPaperSettings;
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
import org.telegram.ui.Components.URLSpanReplacement;
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
    private static int[] documentIcons = {R.drawable.media_doc_blue, R.drawable.media_doc_green, R.drawable.media_doc_red, R.drawable.media_doc_yellow};
    private static int[] documentMediaIcons = {R.drawable.media_doc_blue_b, R.drawable.media_doc_green_b, R.drawable.media_doc_red_b, R.drawable.media_doc_yellow_b};
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
    public static final Linkify.MatchFilter sUrlMatchFilter = AndroidUtilities$$ExternalSyntheticLambda3.INSTANCE;
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

    public static int compare(int i, int i2) {
        if (i == i2) {
            return 0;
        }
        return i > i2 ? 1 : -1;
    }

    public static int compare(long j, long j2) {
        if (j == j2) {
            return 0;
        }
        return j > j2 ? 1 : -1;
    }

    public static int getMyLayerVersion(int i) {
        return i & 65535;
    }

    public static int getWallpaperRotation(int i, boolean z) {
        int i2 = z ? i + 180 : i - 180;
        while (i2 >= 360) {
            i2 -= 360;
        }
        while (i2 < 0) {
            i2 += 360;
        }
        return i2;
    }

    public static boolean isAccessibilityScreenReaderEnabled() {
        return false;
    }

    public static boolean isValidWallChar(char c) {
        return c == '-' || c == '~';
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ String lambda$formatSpannableSimple$7(Integer num) {
        return "%s";
    }

    public static float lerp(float f, float f2, float f3) {
        return f + (f3 * (f2 - f));
    }

    public static int lerp(int i, int i2, float f) {
        return (int) (((float) i) + (f * ((float) (i2 - i))));
    }

    public static int setMyLayerVersion(int i, int i2) {
        return (i & -65536) | i2;
    }

    public static int setPeerLayerVersion(int i, int i2) {
        return (i & 65535) | (i2 << 16);
    }

    static {
        WEB_URL = null;
        BAD_CHARS_PATTERN = null;
        BAD_CHARS_MESSAGE_PATTERN = null;
        BAD_CHARS_MESSAGE_LONG_PATTERN = null;
        try {
            BAD_CHARS_PATTERN = Pattern.compile("[─-◿]");
            BAD_CHARS_MESSAGE_LONG_PATTERN = Pattern.compile("[̀-ͯ⁦-⁧]+");
            BAD_CHARS_MESSAGE_PATTERN = Pattern.compile("[⁦-⁧]+");
            Pattern compile = Pattern.compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9]))");
            Pattern compile2 = Pattern.compile("(([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯\\-]{0,61}[a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]){0,1}\\.)+[a-zA-Z -퟿豈-﷏ﷰ-￯]{2,63}|" + compile + ")");
            WEB_URL = Pattern.compile("((?:(http|https|Http|Https|ton|tg):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?(?:" + compile2 + ")(?:\\:\\d{1,5})?)(\\/(?:(?:[" + "a-zA-Z0-9 -퟿豈-﷏ﷰ-￯" + "\\;\\/\\?\\:\\@\\&\\=\\#\\~\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?(?:\\b|$)");
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        checkDisplaySize(ApplicationLoader.applicationContext, (Configuration) null);
    }

    private static boolean containsUnsupportedCharacters(String str) {
        if (str.contains("‬") || str.contains("‭") || str.contains("‮")) {
            return true;
        }
        try {
            if (BAD_CHARS_PATTERN.matcher(str).find()) {
                return true;
            }
            return false;
        } catch (Throwable unused) {
            return true;
        }
    }

    public static String getSafeString(String str) {
        try {
            return BAD_CHARS_MESSAGE_PATTERN.matcher(str).replaceAll("‌");
        } catch (Throwable unused) {
            return str;
        }
    }

    public static CharSequence ellipsizeCenterEnd(CharSequence charSequence, String str, int i, TextPaint textPaint, int i2) {
        CharSequence charSequence2;
        try {
            int length = charSequence.length();
            int indexOf = charSequence.toString().toLowerCase().indexOf(str);
            if (length > i2) {
                charSequence = charSequence.subSequence(Math.max(0, indexOf - (i2 / 2)), Math.min(length, (i2 / 2) + indexOf));
                indexOf -= Math.max(0, indexOf - (i2 / 2));
                charSequence.length();
            }
            StaticLayout staticLayout = new StaticLayout(charSequence, textPaint, Integer.MAX_VALUE, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            float lineWidth = staticLayout.getLineWidth(0);
            float f = (float) i;
            if (textPaint.measureText("...") + lineWidth < f) {
                return charSequence;
            }
            int i3 = indexOf + 1;
            int i4 = i3;
            while (i4 < charSequence.length() - 1 && !Character.isWhitespace(charSequence.charAt(i4))) {
                i4++;
            }
            float primaryHorizontal = staticLayout.getPrimaryHorizontal(i4);
            if (staticLayout.isRtlCharAt(i4)) {
                primaryHorizontal = lineWidth - primaryHorizontal;
            }
            if (primaryHorizontal < f) {
                return charSequence;
            }
            float measureText = (primaryHorizontal - f) + (textPaint.measureText("...") * 2.0f);
            float f2 = 0.1f * f;
            float f3 = measureText + f2;
            if (charSequence.length() - i4 > 20) {
                f3 += f2;
            }
            if (f3 > 0.0f) {
                int offsetForHorizontal = staticLayout.getOffsetForHorizontal(0, f3);
                if (offsetForHorizontal > charSequence.length() - 1) {
                    offsetForHorizontal = charSequence.length() - 1;
                }
                int i5 = 0;
                while (true) {
                    if (Character.isWhitespace(charSequence.charAt(offsetForHorizontal)) || i5 >= 10) {
                        break;
                    }
                    i5++;
                    offsetForHorizontal++;
                    if (offsetForHorizontal > charSequence.length() - 1) {
                        offsetForHorizontal = staticLayout.getOffsetForHorizontal(0, f3);
                        break;
                    }
                }
                if (i5 >= 10) {
                    charSequence2 = charSequence.subSequence(staticLayout.getOffsetForHorizontal(0, staticLayout.getPrimaryHorizontal(i3) - (f * 0.3f)), charSequence.length());
                } else {
                    if (offsetForHorizontal > 0 && offsetForHorizontal < charSequence.length() - 2 && Character.isWhitespace(charSequence.charAt(offsetForHorizontal))) {
                        offsetForHorizontal++;
                    }
                    charSequence2 = charSequence.subSequence(offsetForHorizontal, charSequence.length());
                }
                return SpannableStringBuilder.valueOf("...").append(charSequence2);
            }
            return charSequence;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static CharSequence highlightText(CharSequence charSequence, ArrayList<String> arrayList, Theme.ResourcesProvider resourcesProvider) {
        if (arrayList == null) {
            return null;
        }
        int i = 0;
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            CharSequence highlightText = highlightText(charSequence, arrayList.get(i2), resourcesProvider);
            if (highlightText != null) {
                charSequence = highlightText;
            } else {
                i++;
            }
        }
        if (i == arrayList.size()) {
            return null;
        }
        return charSequence;
    }

    public static CharSequence highlightText(CharSequence charSequence, String str, Theme.ResourcesProvider resourcesProvider) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(charSequence)) {
            return null;
        }
        String lowerCase = charSequence.toString().toLowerCase();
        SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(charSequence);
        int indexOf = lowerCase.indexOf(str);
        while (indexOf >= 0) {
            try {
                valueOf.setSpan(new ForegroundColorSpanThemable("windowBackgroundWhiteBlueText4", resourcesProvider), indexOf, Math.min(str.length() + indexOf, charSequence.length()), 0);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            indexOf = lowerCase.indexOf(str, indexOf + 1);
        }
        return valueOf;
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

    public static CharSequence replaceSingleTag(String str, Runnable runnable) {
        return replaceSingleTag(str, (String) null, runnable);
    }

    public static CharSequence replaceSingleTag(String str, final String str2, final Runnable runnable) {
        int i;
        int i2;
        int indexOf = str.indexOf("**");
        int indexOf2 = str.indexOf("**", indexOf + 1);
        String replace = str.replace("**", "");
        if (indexOf < 0 || indexOf2 < 0 || (i2 = indexOf2 - indexOf) <= 2) {
            indexOf = -1;
            i = 0;
        } else {
            i = i2 - 2;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(replace);
        if (indexOf >= 0) {
            spannableStringBuilder.setSpan(new ClickableSpan() {
                public void updateDrawState(TextPaint textPaint) {
                    super.updateDrawState(textPaint);
                    textPaint.setUnderlineText(false);
                    String str = str2;
                    if (str != null) {
                        textPaint.setColor(Theme.getColor(str));
                    }
                }

                public void onClick(View view) {
                    Runnable runnable = runnable;
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            }, indexOf, i + indexOf, 0);
        }
        return spannableStringBuilder;
    }

    public static void recycleBitmaps(ArrayList<Bitmap> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            runOnUIThread(new AndroidUtilities$$ExternalSyntheticLambda9(arrayList), 36);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$recycleBitmaps$0(ArrayList arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            Bitmap bitmap = (Bitmap) arrayList.get(i);
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

    private static String makeUrl(String str, String[] strArr, Matcher matcher) {
        boolean z;
        int i = 0;
        while (true) {
            z = true;
            if (i >= strArr.length) {
                z = false;
                break;
            }
            if (str.regionMatches(true, 0, strArr[i], 0, strArr[i].length())) {
                if (!str.regionMatches(false, 0, strArr[i], 0, strArr[i].length())) {
                    str = strArr[i] + str.substring(strArr[i].length());
                }
            } else {
                i++;
            }
        }
        if (z || strArr.length <= 0) {
            return str;
        }
        return strArr[0] + str;
    }

    private static void gatherLinks(ArrayList<LinkSpec> arrayList, Spannable spannable, Pattern pattern, String[] strArr, Linkify.MatchFilter matchFilter, boolean z) {
        if (TextUtils.indexOf(spannable, 9472) >= 0) {
            spannable = new SpannableStringBuilder(spannable.toString().replace(9472, ' '));
        }
        Matcher matcher = pattern.matcher(spannable);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if (matchFilter == null || matchFilter.acceptMatch(spannable, start, end)) {
                LinkSpec linkSpec = new LinkSpec();
                String makeUrl = makeUrl(matcher.group(0), strArr, matcher);
                if (!z || Browser.isInternalUrl(makeUrl, true, (boolean[]) null)) {
                    linkSpec.url = makeUrl;
                    linkSpec.start = start;
                    linkSpec.end = end;
                    arrayList.add(linkSpec);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$static$2(CharSequence charSequence, int i, int i2) {
        return i == 0 || charSequence.charAt(i - 1) != '@';
    }

    public static boolean addLinks(Spannable spannable, int i) {
        return addLinks(spannable, i, false);
    }

    public static boolean addLinks(Spannable spannable, int i, boolean z) {
        return addLinks(spannable, i, z, true);
    }

    public static boolean addLinks(Spannable spannable, int i, boolean z, boolean z2) {
        if (spannable == null || containsUnsupportedCharacters(spannable.toString()) || i == 0) {
            return false;
        }
        URLSpan[] uRLSpanArr = (URLSpan[]) spannable.getSpans(0, spannable.length(), URLSpan.class);
        for (int length = uRLSpanArr.length - 1; length >= 0; length--) {
            URLSpan uRLSpan = uRLSpanArr[length];
            if (!(uRLSpan instanceof URLSpanReplacement) || z2) {
                spannable.removeSpan(uRLSpan);
            }
        }
        ArrayList arrayList = new ArrayList();
        if (!z && (i & 4) != 0) {
            Linkify.addLinks(spannable, 4);
        }
        if ((i & 1) != 0) {
            gatherLinks(arrayList, spannable, LinkifyPort.WEB_URL, new String[]{"http://", "https://", "tg://"}, sUrlMatchFilter, z);
        }
        pruneOverlaps(arrayList);
        if (arrayList.size() == 0) {
            return false;
        }
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            LinkSpec linkSpec = (LinkSpec) arrayList.get(i2);
            URLSpan[] uRLSpanArr2 = (URLSpan[]) spannable.getSpans(linkSpec.start, linkSpec.end, URLSpan.class);
            if (uRLSpanArr2 != null && uRLSpanArr2.length > 0) {
                for (URLSpan uRLSpan2 : uRLSpanArr2) {
                    spannable.removeSpan(uRLSpan2);
                    if (!(uRLSpan2 instanceof URLSpanReplacement) || z2) {
                        spannable.removeSpan(uRLSpan2);
                    }
                }
            }
            spannable.setSpan(new URLSpan(linkSpec.url), linkSpec.start, linkSpec.end, 33);
        }
        return true;
    }

    private static void pruneOverlaps(ArrayList<LinkSpec> arrayList) {
        int i;
        Collections.sort(arrayList, AndroidUtilities$$ExternalSyntheticLambda11.INSTANCE);
        int size = arrayList.size();
        int i2 = 0;
        while (i2 < size - 1) {
            LinkSpec linkSpec = arrayList.get(i2);
            int i3 = i2 + 1;
            LinkSpec linkSpec2 = arrayList.get(i3);
            int i4 = linkSpec.start;
            int i5 = linkSpec2.start;
            if (i4 <= i5 && (i = linkSpec.end) > i5) {
                int i6 = linkSpec2.end;
                int i7 = (i6 > i && i - i4 <= i6 - i5) ? i - i4 < i6 - i5 ? i2 : -1 : i3;
                if (i7 != -1) {
                    arrayList.remove(i7);
                    size--;
                }
            }
            i2 = i3;
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$pruneOverlaps$3(LinkSpec linkSpec, LinkSpec linkSpec2) {
        int i;
        int i2;
        int i3 = linkSpec.start;
        int i4 = linkSpec2.start;
        if (i3 < i4) {
            return -1;
        }
        if (i3 > i4 || (i = linkSpec.end) < (i2 = linkSpec2.end)) {
            return 1;
        }
        if (i > i2) {
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
        int identifier = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (identifier > 0) {
            return context.getResources().getDimensionPixelSize(identifier);
        }
        return 0;
    }

    private static int getNavigationBarHeight(Context context) {
        int identifier = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (identifier > 0) {
            return context.getResources().getDimensionPixelSize(identifier);
        }
        return 0;
    }

    public static int getThumbForNameOrMime(String str, String str2, boolean z) {
        if (str == null || str.length() == 0) {
            return z ? documentMediaIcons[0] : documentIcons[0];
        }
        int i = (str.contains(".doc") || str.contains(".txt") || str.contains(".psd")) ? 0 : (str.contains(".xls") || str.contains(".csv")) ? 1 : (str.contains(".pdf") || str.contains(".ppt") || str.contains(".key")) ? 2 : (str.contains(".zip") || str.contains(".rar") || str.contains(".ai") || str.contains(".mp3") || str.contains(".mov") || str.contains(".avi")) ? 3 : -1;
        if (i == -1) {
            int lastIndexOf = str.lastIndexOf(46);
            String substring = lastIndexOf == -1 ? "" : str.substring(lastIndexOf + 1);
            if (substring.length() != 0) {
                i = substring.charAt(0) % documentIcons.length;
            } else {
                i = str.charAt(0) % documentIcons.length;
            }
        }
        return z ? documentMediaIcons[i] : documentIcons[i];
    }

    public static int calcBitmapColor(Bitmap bitmap) {
        try {
            Bitmap createScaledBitmap = Bitmaps.createScaledBitmap(bitmap, 1, 1, true);
            if (createScaledBitmap != null) {
                int pixel = createScaledBitmap.getPixel(0, 0);
                if (bitmap != createScaledBitmap) {
                    createScaledBitmap.recycle();
                }
                return pixel;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return 0;
    }

    public static int[] calcDrawableColor(Drawable drawable) {
        int i;
        Drawable drawable2 = drawable;
        int[] iArr = new int[4];
        int i2 = -16777216;
        try {
            if (drawable2 instanceof BitmapDrawable) {
                i2 = calcBitmapColor(((BitmapDrawable) drawable2).getBitmap());
            } else if (drawable2 instanceof ColorDrawable) {
                i2 = ((ColorDrawable) drawable2).getColor();
            } else if (drawable2 instanceof BackgroundGradientDrawable) {
                int[] colorsList = ((BackgroundGradientDrawable) drawable2).getColorsList();
                if (colorsList != null) {
                    if (colorsList.length > 1) {
                        i = getAverageColor(colorsList[0], colorsList[1]);
                    } else if (colorsList.length > 0) {
                        i = colorsList[0];
                    }
                    i2 = i;
                }
            } else if (drawable2 instanceof MotionBackgroundDrawable) {
                int argb = Color.argb(45, 0, 0, 0);
                iArr[2] = argb;
                iArr[0] = argb;
                int argb2 = Color.argb(61, 0, 0, 0);
                iArr[3] = argb2;
                iArr[1] = argb2;
                return iArr;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        double[] rgbToHsv = rgbToHsv((i2 >> 16) & 255, (i2 >> 8) & 255, i2 & 255);
        rgbToHsv[1] = Math.min(1.0d, rgbToHsv[1] + 0.05d + ((1.0d - rgbToHsv[1]) * 0.1d));
        int[] hsvToRgb = hsvToRgb(rgbToHsv[0], rgbToHsv[1], Math.max(0.0d, rgbToHsv[2] * 0.65d));
        iArr[0] = Color.argb(102, hsvToRgb[0], hsvToRgb[1], hsvToRgb[2]);
        iArr[1] = Color.argb(136, hsvToRgb[0], hsvToRgb[1], hsvToRgb[2]);
        int[] hsvToRgb2 = hsvToRgb(rgbToHsv[0], rgbToHsv[1], Math.max(0.0d, rgbToHsv[2] * 0.72d));
        iArr[2] = Color.argb(102, hsvToRgb2[0], hsvToRgb2[1], hsvToRgb2[2]);
        iArr[3] = Color.argb(136, hsvToRgb2[0], hsvToRgb2[1], hsvToRgb2[2]);
        return iArr;
    }

    public static double[] rgbToHsv(int i) {
        return rgbToHsv(Color.red(i), Color.green(i), Color.blue(i));
    }

    public static double[] rgbToHsv(int i, int i2, int i3) {
        double d;
        double d2;
        double d3;
        double d4;
        double d5;
        double d6 = (double) i;
        Double.isNaN(d6);
        double d7 = d6 / 255.0d;
        double d8 = (double) i2;
        Double.isNaN(d8);
        double d9 = d8 / 255.0d;
        double d10 = (double) i3;
        Double.isNaN(d10);
        double d11 = d10 / 255.0d;
        if (d7 <= d9 || d7 <= d11) {
            d = Math.max(d9, d11);
        } else {
            d = d7;
        }
        if (d7 >= d9 || d7 >= d11) {
            d2 = Math.min(d9, d11);
        } else {
            d2 = d7;
        }
        double d12 = d - d2;
        double d13 = 0.0d;
        double d14 = d == 0.0d ? 0.0d : d12 / d;
        if (d != d2) {
            if (d7 > d9 && d7 > d11) {
                d5 = (d9 - d11) / d12;
                d4 = (double) (d9 < d11 ? 6 : 0);
                Double.isNaN(d4);
            } else if (d9 > d11) {
                d3 = 2.0d + ((d11 - d7) / d12);
                d13 = d3 / 6.0d;
            } else {
                d5 = (d7 - d9) / d12;
                d4 = 4.0d;
            }
            d3 = d5 + d4;
            d13 = d3 / 6.0d;
        }
        return new double[]{d13, d14, d};
    }

    public static int hsvToColor(double d, double d2, double d3) {
        int[] hsvToRgb = hsvToRgb(d, d2, d3);
        return Color.argb(255, hsvToRgb[0], hsvToRgb[1], hsvToRgb[2]);
    }

    public static int[] hsvToRgb(double d, double d2, double d3) {
        double d4 = 6.0d * d;
        double floor = (double) ((int) Math.floor(d4));
        Double.isNaN(floor);
        double d5 = d4 - floor;
        double d6 = (1.0d - d2) * d3;
        double d7 = (1.0d - (d5 * d2)) * d3;
        double d8 = d3 * (1.0d - ((1.0d - d5) * d2));
        int i = ((int) floor) % 6;
        double d9 = 0.0d;
        if (i != 0) {
            if (i == 1) {
                d9 = d3;
                d8 = d6;
                d6 = d7;
            } else if (i == 2) {
                d9 = d3;
            } else if (i == 3) {
                d8 = d3;
                d9 = d7;
            } else if (i == 4) {
                d9 = d6;
                d6 = d8;
                d8 = d3;
            } else if (i != 5) {
                d8 = 0.0d;
                d6 = 0.0d;
            } else {
                d9 = d6;
                d8 = d7;
            }
            return new int[]{(int) (d6 * 255.0d), (int) (d9 * 255.0d), (int) (d8 * 255.0d)};
        }
        d9 = d8;
        d8 = d6;
        d6 = d3;
        return new int[]{(int) (d6 * 255.0d), (int) (d9 * 255.0d), (int) (d8 * 255.0d)};
    }

    public static void adjustSaturationColorMatrix(ColorMatrix colorMatrix, float f) {
        if (colorMatrix != null) {
            float f2 = f + 1.0f;
            float f3 = 1.0f - f2;
            float f4 = 0.3086f * f3;
            float f5 = 0.6094f * f3;
            float f6 = f3 * 0.082f;
            colorMatrix.postConcat(new ColorMatrix(new float[]{f4 + f2, f5, f6, 0.0f, 0.0f, f4, f5 + f2, f6, 0.0f, 0.0f, f4, f5, f6 + f2, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}));
        }
    }

    public static void adjustBrightnessColorMatrix(ColorMatrix colorMatrix, float f) {
        if (colorMatrix != null) {
            float f2 = f * 255.0f;
            colorMatrix.postConcat(new ColorMatrix(new float[]{1.0f, 0.0f, 0.0f, 0.0f, f2, 0.0f, 1.0f, 0.0f, 0.0f, f2, 0.0f, 0.0f, 1.0f, 0.0f, f2, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}));
        }
    }

    public static void multiplyBrightnessColorMatrix(ColorMatrix colorMatrix, float f) {
        if (colorMatrix != null) {
            colorMatrix.postConcat(new ColorMatrix(new float[]{f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}));
        }
    }

    public static Bitmap snapshotView(View view) {
        Bitmap createBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        view.draw(canvas);
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        snapshotTextureViews(iArr[0], iArr[1], iArr, canvas, view);
        return createBitmap;
    }

    private static void snapshotTextureViews(int i, int i2, int[] iArr, Canvas canvas, View view) {
        if (view instanceof TextureView) {
            TextureView textureView = (TextureView) view;
            textureView.getLocationInWindow(iArr);
            Bitmap bitmap = textureView.getBitmap();
            if (bitmap != null) {
                canvas.save();
                canvas.drawBitmap(bitmap, (float) (iArr[0] - i), (float) (iArr[1] - i2), (Paint) null);
                canvas.restore();
                bitmap.recycle();
            }
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i3 = 0; i3 < viewGroup.getChildCount(); i3++) {
                snapshotTextureViews(i, i2, iArr, canvas, viewGroup.getChildAt(i3));
            }
        }
    }

    public static void requestAltFocusable(Activity activity, int i) {
        if (activity != null) {
            activity.getWindow().setFlags(131072, 131072);
            altFocusableClassGuid = i;
        }
    }

    public static void removeAltFocusable(Activity activity, int i) {
        if (activity != null && altFocusableClassGuid == i) {
            activity.getWindow().clearFlags(131072);
        }
    }

    public static void requestAdjustResize(Activity activity, int i) {
        if (activity != null) {
            requestAdjustResize(activity.getWindow(), i);
        }
    }

    public static void requestAdjustResize(Window window, int i) {
        if (window != null && !isTablet()) {
            window.setSoftInputMode(16);
            adjustOwnerClassGuid = i;
        }
    }

    public static void requestAdjustNothing(Activity activity, int i) {
        if (activity != null && !isTablet()) {
            activity.getWindow().setSoftInputMode(48);
            adjustOwnerClassGuid = i;
        }
    }

    public static void setAdjustResizeToNothing(Activity activity, int i) {
        if (activity != null && !isTablet()) {
            int i2 = adjustOwnerClassGuid;
            if (i2 == 0 || i2 == i) {
                activity.getWindow().setSoftInputMode(48);
            }
        }
    }

    public static void removeAdjustResize(Activity activity, int i) {
        if (activity != null && !isTablet() && adjustOwnerClassGuid == i) {
            activity.getWindow().setSoftInputMode(32);
        }
    }

    public static void createEmptyFile(File file) {
        try {
            if (!file.exists()) {
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.flush();
                fileWriter.close();
            }
        } catch (Throwable th) {
            FileLog.e(th, false);
        }
    }

    public static boolean isMapsInstalled(BaseFragment baseFragment) {
        String mapsAppPackageName = ApplicationLoader.getMapsProvider().getMapsAppPackageName();
        try {
            ApplicationLoader.applicationContext.getPackageManager().getApplicationInfo(mapsAppPackageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            if (baseFragment.getParentActivity() == null) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) baseFragment.getParentActivity());
            builder.setMessage(LocaleController.getString(ApplicationLoader.getMapsProvider().getInstallMapsString()));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new AndroidUtilities$$ExternalSyntheticLambda2(mapsAppPackageName, baseFragment));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            baseFragment.showDialog(builder.create());
            return false;
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$isMapsInstalled$4(String str, BaseFragment baseFragment, DialogInterface dialogInterface, int i) {
        try {
            baseFragment.getParentActivity().startActivityForResult(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + str)), 500);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static int[] toIntArray(List<Integer> list) {
        int size = list.size();
        int[] iArr = new int[size];
        for (int i = 0; i < size; i++) {
            iArr[i] = list.get(i).intValue();
        }
        return iArr;
    }

    public static boolean isInternalUri(Uri uri) {
        return isInternalUri(uri, 0);
    }

    public static boolean isInternalUri(int i) {
        return isInternalUri((Uri) null, i);
    }

    private static boolean isInternalUri(Uri uri, int i) {
        String str;
        if (uri != null) {
            str = uri.getPath();
            if (str == null) {
                return false;
            }
            if (str.matches(Pattern.quote(new File(ApplicationLoader.applicationContext.getCacheDir(), "voip_logs").getAbsolutePath()) + "/\\d+\\.log")) {
                return false;
            }
            int i2 = 0;
            while (str.length() <= 4096) {
                try {
                    String readlink = Utilities.readlink(str);
                    if (readlink != null && !readlink.equals(str)) {
                        i2++;
                        if (i2 >= 10) {
                            return true;
                        }
                        str = readlink;
                    }
                } catch (Throwable unused) {
                    return true;
                }
            }
            return true;
        }
        String str2 = "";
        int i3 = 0;
        while (str.length() <= 4096) {
            try {
                String readlinkFd = Utilities.readlinkFd(i);
                if (readlinkFd != null && !readlinkFd.equals(str)) {
                    i3++;
                    if (i3 >= 10) {
                        return true;
                    }
                    str2 = readlinkFd;
                }
            } catch (Throwable unused2) {
                return true;
            }
        }
        return true;
        try {
            String canonicalPath = new File(str).getCanonicalPath();
            if (canonicalPath != null) {
                str = canonicalPath;
            }
        } catch (Exception unused3) {
            str.replace("/./", "/");
        }
        if (str.endsWith(".attheme")) {
            return false;
        }
        String lowerCase = str.toLowerCase();
        if (lowerCase.contains("/data/data/" + ApplicationLoader.applicationContext.getPackageName())) {
            return true;
        }
        return false;
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
                FileLog.e((Throwable) e);
            }
        }
    }

    @SuppressLint({"WrongConstant"})
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
            byte[] decodeQuotedPrintable;
            int indexOf = this.fullData.indexOf(58);
            if (indexOf < 0) {
                return new String[0];
            }
            String substring = this.fullData.substring(0, indexOf);
            String substring2 = this.fullData.substring(indexOf + 1);
            String str = null;
            String[] split = substring.split(";");
            String str2 = "UTF-8";
            for (String split2 : split) {
                String[] split3 = split2.split("=");
                if (split3.length == 2) {
                    if (split3[0].equals("CHARSET")) {
                        str2 = split3[1];
                    } else if (split3[0].equals("ENCODING")) {
                        str = split3[1];
                    }
                }
            }
            String[] split4 = substring2.split(";");
            for (int i = 0; i < split4.length; i++) {
                if (!(TextUtils.isEmpty(split4[i]) || str == null || !str.equalsIgnoreCase("QUOTED-PRINTABLE") || (decodeQuotedPrintable = AndroidUtilities.decodeQuotedPrintable(AndroidUtilities.getStringBytes(split4[i]))) == null || decodeQuotedPrintable.length == 0)) {
                    try {
                        split4[i] = new String(decodeQuotedPrintable, str2);
                    } catch (Exception unused) {
                    }
                }
            }
            return split4;
        }

        public String getValue(boolean z) {
            byte[] decodeQuotedPrintable;
            StringBuilder sb = new StringBuilder();
            int indexOf = this.fullData.indexOf(58);
            if (indexOf < 0) {
                return "";
            }
            if (sb.length() > 0) {
                sb.append(", ");
            }
            String substring = this.fullData.substring(0, indexOf);
            String substring2 = this.fullData.substring(indexOf + 1);
            String str = null;
            String[] split = substring.split(";");
            String str2 = "UTF-8";
            for (String split2 : split) {
                String[] split3 = split2.split("=");
                if (split3.length == 2) {
                    if (split3[0].equals("CHARSET")) {
                        str2 = split3[1];
                    } else if (split3[0].equals("ENCODING")) {
                        str = split3[1];
                    }
                }
            }
            String[] split4 = substring2.split(";");
            boolean z2 = false;
            for (int i = 0; i < split4.length; i++) {
                if (!TextUtils.isEmpty(split4[i])) {
                    if (!(str == null || !str.equalsIgnoreCase("QUOTED-PRINTABLE") || (decodeQuotedPrintable = AndroidUtilities.decodeQuotedPrintable(AndroidUtilities.getStringBytes(split4[i]))) == null || decodeQuotedPrintable.length == 0)) {
                        try {
                            split4[i] = new String(decodeQuotedPrintable, str2);
                        } catch (Exception unused) {
                        }
                    }
                    if (z2 && sb.length() > 0) {
                        sb.append(" ");
                    }
                    sb.append(split4[i]);
                    if (!z2) {
                        z2 = split4[i].length() > 0;
                    }
                }
            }
            if (z) {
                int i2 = this.type;
                if (i2 == 0) {
                    return PhoneFormat.getInstance().format(sb.toString());
                }
                if (i2 == 5) {
                    String[] split5 = sb.toString().split("T");
                    if (split5.length > 0) {
                        String[] split6 = split5[0].split("-");
                        if (split6.length == 3) {
                            Calendar instance = Calendar.getInstance();
                            instance.set(1, Utilities.parseInt((CharSequence) split6[0]).intValue());
                            instance.set(2, Utilities.parseInt((CharSequence) split6[1]).intValue() - 1);
                            instance.set(5, Utilities.parseInt((CharSequence) split6[2]).intValue());
                            return LocaleController.getInstance().formatterYearMax.format(instance.getTime());
                        }
                    }
                }
            }
            return sb.toString();
        }

        public String getRawType(boolean z) {
            int indexOf = this.fullData.indexOf(58);
            if (indexOf < 0) {
                return "";
            }
            String substring = this.fullData.substring(0, indexOf);
            if (this.type == 20) {
                String[] split = substring.substring(2).split(";");
                if (z) {
                    return split[0];
                }
                if (split.length > 1) {
                    return split[split.length - 1];
                }
                return "";
            }
            String[] split2 = substring.split(";");
            for (int i = 0; i < split2.length; i++) {
                if (split2[i].indexOf(61) < 0) {
                    substring = split2[i];
                }
            }
            return substring;
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* JADX WARNING: Code restructure failed: missing block: B:32:0x0090, code lost:
            if (r0.equals("OTHER") == false) goto L_0x0088;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.String getType() {
            /*
                r9 = this;
                int r0 = r9.type
                r1 = 5
                if (r0 != r1) goto L_0x000e
                int r0 = org.telegram.messenger.R.string.ContactBirthday
                java.lang.String r1 = "ContactBirthday"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                return r0
            L_0x000e:
                r2 = 6
                r3 = 1
                if (r0 != r2) goto L_0x0030
                java.lang.String r0 = r9.getRawType(r3)
                java.lang.String r1 = "ORG"
                boolean r0 = r1.equalsIgnoreCase(r0)
                if (r0 == 0) goto L_0x0027
                int r0 = org.telegram.messenger.R.string.ContactJob
                java.lang.String r1 = "ContactJob"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                return r0
            L_0x0027:
                int r0 = org.telegram.messenger.R.string.ContactJobTitle
                java.lang.String r1 = "ContactJobTitle"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                return r0
            L_0x0030:
                java.lang.String r0 = r9.fullData
                r2 = 58
                int r0 = r0.indexOf(r2)
                if (r0 >= 0) goto L_0x003d
                java.lang.String r0 = ""
                return r0
            L_0x003d:
                java.lang.String r2 = r9.fullData
                r4 = 0
                java.lang.String r0 = r2.substring(r4, r0)
                int r2 = r9.type
                r5 = 20
                java.lang.String r6 = ";"
                r7 = 2
                if (r2 != r5) goto L_0x0059
                java.lang.String r0 = r0.substring(r7)
                java.lang.String[] r0 = r0.split(r6)
                r0 = r0[r4]
                goto L_0x00f9
            L_0x0059:
                java.lang.String[] r2 = r0.split(r6)
                r5 = 0
            L_0x005e:
                int r6 = r2.length
                if (r5 >= r6) goto L_0x0071
                r6 = r2[r5]
                r8 = 61
                int r6 = r6.indexOf(r8)
                if (r6 < 0) goto L_0x006c
                goto L_0x006e
            L_0x006c:
                r0 = r2[r5]
            L_0x006e:
                int r5 = r5 + 1
                goto L_0x005e
            L_0x0071:
                java.lang.String r2 = "X-"
                boolean r2 = r0.startsWith(r2)
                if (r2 == 0) goto L_0x007d
                java.lang.String r0 = r0.substring(r7)
            L_0x007d:
                r0.hashCode()
                r2 = -1
                int r5 = r0.hashCode()
                switch(r5) {
                    case -2015525726: goto L_0x00bf;
                    case 2064738: goto L_0x00b4;
                    case 2223327: goto L_0x00a9;
                    case 2464291: goto L_0x009e;
                    case 2670353: goto L_0x0093;
                    case 75532016: goto L_0x008a;
                    default: goto L_0x0088;
                }
            L_0x0088:
                r1 = -1
                goto L_0x00c9
            L_0x008a:
                java.lang.String r5 = "OTHER"
                boolean r5 = r0.equals(r5)
                if (r5 != 0) goto L_0x00c9
                goto L_0x0088
            L_0x0093:
                java.lang.String r1 = "WORK"
                boolean r1 = r0.equals(r1)
                if (r1 != 0) goto L_0x009c
                goto L_0x0088
            L_0x009c:
                r1 = 4
                goto L_0x00c9
            L_0x009e:
                java.lang.String r1 = "PREF"
                boolean r1 = r0.equals(r1)
                if (r1 != 0) goto L_0x00a7
                goto L_0x0088
            L_0x00a7:
                r1 = 3
                goto L_0x00c9
            L_0x00a9:
                java.lang.String r1 = "HOME"
                boolean r1 = r0.equals(r1)
                if (r1 != 0) goto L_0x00b2
                goto L_0x0088
            L_0x00b2:
                r1 = 2
                goto L_0x00c9
            L_0x00b4:
                java.lang.String r1 = "CELL"
                boolean r1 = r0.equals(r1)
                if (r1 != 0) goto L_0x00bd
                goto L_0x0088
            L_0x00bd:
                r1 = 1
                goto L_0x00c9
            L_0x00bf:
                java.lang.String r1 = "MOBILE"
                boolean r1 = r0.equals(r1)
                if (r1 != 0) goto L_0x00c8
                goto L_0x0088
            L_0x00c8:
                r1 = 0
            L_0x00c9:
                switch(r1) {
                    case 0: goto L_0x00f1;
                    case 1: goto L_0x00f1;
                    case 2: goto L_0x00e8;
                    case 3: goto L_0x00df;
                    case 4: goto L_0x00d6;
                    case 5: goto L_0x00cd;
                    default: goto L_0x00cc;
                }
            L_0x00cc:
                goto L_0x00f9
            L_0x00cd:
                int r0 = org.telegram.messenger.R.string.PhoneOther
                java.lang.String r1 = "PhoneOther"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                goto L_0x00f9
            L_0x00d6:
                int r0 = org.telegram.messenger.R.string.PhoneWork
                java.lang.String r1 = "PhoneWork"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                goto L_0x00f9
            L_0x00df:
                int r0 = org.telegram.messenger.R.string.PhoneMain
                java.lang.String r1 = "PhoneMain"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                goto L_0x00f9
            L_0x00e8:
                int r0 = org.telegram.messenger.R.string.PhoneHome
                java.lang.String r1 = "PhoneHome"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                goto L_0x00f9
            L_0x00f1:
                int r0 = org.telegram.messenger.R.string.PhoneMobile
                java.lang.String r1 = "PhoneMobile"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            L_0x00f9:
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = r0.substring(r4, r3)
                java.lang.String r2 = r2.toUpperCase()
                r1.append(r2)
                java.lang.String r0 = r0.substring(r3)
                java.lang.String r0 = r0.toLowerCase()
                r1.append(r0)
                java.lang.String r0 = r1.toString()
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.VcardItem.getType():java.lang.String");
        }
    }

    public static byte[] getStringBytes(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (Exception unused) {
            return new byte[0];
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v0, resolved type: org.telegram.messenger.AndroidUtilities$1} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v1, resolved type: org.telegram.messenger.AndroidUtilities$VcardItem} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: org.telegram.messenger.AndroidUtilities$1} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v6, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v9, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v2, resolved type: org.telegram.messenger.AndroidUtilities$VcardItem} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v3, resolved type: org.telegram.messenger.AndroidUtilities$VcardItem} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v4, resolved type: org.telegram.messenger.AndroidUtilities$VcardItem} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v5, resolved type: org.telegram.messenger.AndroidUtilities$VcardItem} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v6, resolved type: org.telegram.messenger.AndroidUtilities$VcardItem} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v8, resolved type: org.telegram.messenger.AndroidUtilities$VcardItem} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v15, resolved type: org.telegram.messenger.AndroidUtilities$VcardItem} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v63, resolved type: org.telegram.messenger.AndroidUtilities$1} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v65, resolved type: org.telegram.messenger.AndroidUtilities$1} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v67, resolved type: org.telegram.messenger.AndroidUtilities$1} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v73, resolved type: org.telegram.messenger.AndroidUtilities$1} */
    /* JADX WARNING: type inference failed for: r3v1, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$User>] */
    /* JADX WARNING: type inference failed for: r11v0 */
    /* JADX WARNING: type inference failed for: r3v8, types: [java.util.ArrayList] */
    /* JADX WARNING: type inference failed for: r3v64 */
    /* JADX WARNING: type inference failed for: r3v68 */
    /* JADX WARNING: type inference failed for: r3v70 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.ArrayList<org.telegram.tgnet.TLRPC$User> loadVCardFromStream(android.net.Uri r21, int r22, boolean r23, java.util.ArrayList<org.telegram.messenger.AndroidUtilities.VcardItem> r24, java.lang.String r25) {
        /*
            r0 = r21
            r1 = r24
            java.lang.String r2 = ""
            r3 = 0
            if (r23 == 0) goto L_0x001d
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x001a }
            android.content.ContentResolver r4 = r4.getContentResolver()     // Catch:{ all -> 0x001a }
            java.lang.String r5 = "r"
            android.content.res.AssetFileDescriptor r0 = r4.openAssetFileDescriptor(r0, r5)     // Catch:{ all -> 0x001a }
            java.io.FileInputStream r0 = r0.createInputStream()     // Catch:{ all -> 0x001a }
            goto L_0x0027
        L_0x001a:
            r0 = move-exception
            goto L_0x0347
        L_0x001d:
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0345 }
            android.content.ContentResolver r4 = r4.getContentResolver()     // Catch:{ all -> 0x0345 }
            java.io.InputStream r0 = r4.openInputStream(r0)     // Catch:{ all -> 0x0345 }
        L_0x0027:
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ all -> 0x0345 }
            r4.<init>()     // Catch:{ all -> 0x0345 }
            java.io.BufferedReader r5 = new java.io.BufferedReader     // Catch:{ all -> 0x0345 }
            java.io.InputStreamReader r6 = new java.io.InputStreamReader     // Catch:{ all -> 0x0345 }
            java.lang.String r7 = "UTF-8"
            r6.<init>(r0, r7)     // Catch:{ all -> 0x0345 }
            r5.<init>(r6)     // Catch:{ all -> 0x0345 }
            r7 = 0
            r8 = r3
            r9 = r8
            r11 = r9
            r10 = 0
        L_0x003d:
            java.lang.String r12 = r5.readLine()     // Catch:{ all -> 0x0345 }
            if (r12 == 0) goto L_0x02ad
            java.lang.String r13 = "PHOTO"
            boolean r13 = r12.startsWith(r13)     // Catch:{ all -> 0x0345 }
            if (r13 == 0) goto L_0x004d
            r10 = 1
            goto L_0x003d
        L_0x004d:
            r13 = 58
            int r13 = r12.indexOf(r13)     // Catch:{ all -> 0x0345 }
            java.lang.String r14 = "ORG"
            java.lang.String r15 = "TEL"
            r6 = 2
            if (r13 < 0) goto L_0x0153
            java.lang.String r10 = "BEGIN:VCARD"
            boolean r10 = r12.startsWith(r10)     // Catch:{ all -> 0x001a }
            if (r10 == 0) goto L_0x0070
            org.telegram.messenger.AndroidUtilities$VcardData r8 = new org.telegram.messenger.AndroidUtilities$VcardData     // Catch:{ all -> 0x001a }
            r8.<init>()     // Catch:{ all -> 0x001a }
            r4.add(r8)     // Catch:{ all -> 0x001a }
            r13 = r25
            r8.name = r13     // Catch:{ all -> 0x001a }
            goto L_0x0150
        L_0x0070:
            r13 = r25
            java.lang.String r10 = "END:VCARD"
            boolean r10 = r12.startsWith(r10)     // Catch:{ all -> 0x001a }
            if (r10 == 0) goto L_0x007c
            goto L_0x0150
        L_0x007c:
            if (r1 == 0) goto L_0x0150
            boolean r10 = r12.startsWith(r15)     // Catch:{ all -> 0x001a }
            if (r10 == 0) goto L_0x008e
            org.telegram.messenger.AndroidUtilities$VcardItem r10 = new org.telegram.messenger.AndroidUtilities$VcardItem     // Catch:{ all -> 0x001a }
            r10.<init>()     // Catch:{ all -> 0x001a }
            r10.type = r7     // Catch:{ all -> 0x001a }
        L_0x008b:
            r11 = r10
            goto L_0x0146
        L_0x008e:
            java.lang.String r10 = "EMAIL"
            boolean r10 = r12.startsWith(r10)     // Catch:{ all -> 0x001a }
            if (r10 == 0) goto L_0x009f
            org.telegram.messenger.AndroidUtilities$VcardItem r10 = new org.telegram.messenger.AndroidUtilities$VcardItem     // Catch:{ all -> 0x001a }
            r10.<init>()     // Catch:{ all -> 0x001a }
            r11 = 1
            r10.type = r11     // Catch:{ all -> 0x001a }
            goto L_0x008b
        L_0x009f:
            java.lang.String r10 = "ADR"
            boolean r10 = r12.startsWith(r10)     // Catch:{ all -> 0x001a }
            if (r10 != 0) goto L_0x013d
            java.lang.String r10 = "LABEL"
            boolean r10 = r12.startsWith(r10)     // Catch:{ all -> 0x001a }
            if (r10 != 0) goto L_0x013d
            java.lang.String r10 = "GEO"
            boolean r10 = r12.startsWith(r10)     // Catch:{ all -> 0x001a }
            if (r10 == 0) goto L_0x00b9
            goto L_0x013d
        L_0x00b9:
            java.lang.String r10 = "URL"
            boolean r10 = r12.startsWith(r10)     // Catch:{ all -> 0x001a }
            if (r10 == 0) goto L_0x00ca
            org.telegram.messenger.AndroidUtilities$VcardItem r10 = new org.telegram.messenger.AndroidUtilities$VcardItem     // Catch:{ all -> 0x001a }
            r10.<init>()     // Catch:{ all -> 0x001a }
            r11 = 3
            r10.type = r11     // Catch:{ all -> 0x001a }
            goto L_0x008b
        L_0x00ca:
            java.lang.String r10 = "NOTE"
            boolean r10 = r12.startsWith(r10)     // Catch:{ all -> 0x001a }
            if (r10 == 0) goto L_0x00db
            org.telegram.messenger.AndroidUtilities$VcardItem r10 = new org.telegram.messenger.AndroidUtilities$VcardItem     // Catch:{ all -> 0x001a }
            r10.<init>()     // Catch:{ all -> 0x001a }
            r11 = 4
            r10.type = r11     // Catch:{ all -> 0x001a }
            goto L_0x008b
        L_0x00db:
            java.lang.String r10 = "BDAY"
            boolean r10 = r12.startsWith(r10)     // Catch:{ all -> 0x001a }
            if (r10 == 0) goto L_0x00ec
            org.telegram.messenger.AndroidUtilities$VcardItem r10 = new org.telegram.messenger.AndroidUtilities$VcardItem     // Catch:{ all -> 0x001a }
            r10.<init>()     // Catch:{ all -> 0x001a }
            r11 = 5
            r10.type = r11     // Catch:{ all -> 0x001a }
            goto L_0x008b
        L_0x00ec:
            boolean r10 = r12.startsWith(r14)     // Catch:{ all -> 0x001a }
            if (r10 != 0) goto L_0x0133
            java.lang.String r10 = "TITLE"
            boolean r10 = r12.startsWith(r10)     // Catch:{ all -> 0x001a }
            if (r10 != 0) goto L_0x0133
            java.lang.String r10 = "ROLE"
            boolean r10 = r12.startsWith(r10)     // Catch:{ all -> 0x001a }
            if (r10 == 0) goto L_0x0103
            goto L_0x0133
        L_0x0103:
            java.lang.String r10 = "X-ANDROID"
            boolean r10 = r12.startsWith(r10)     // Catch:{ all -> 0x001a }
            if (r10 == 0) goto L_0x0115
            org.telegram.messenger.AndroidUtilities$VcardItem r10 = new org.telegram.messenger.AndroidUtilities$VcardItem     // Catch:{ all -> 0x001a }
            r10.<init>()     // Catch:{ all -> 0x001a }
            r11 = -1
            r10.type = r11     // Catch:{ all -> 0x001a }
            goto L_0x008b
        L_0x0115:
            java.lang.String r10 = "X-PHONETIC"
            boolean r10 = r12.startsWith(r10)     // Catch:{ all -> 0x001a }
            if (r10 == 0) goto L_0x011e
            goto L_0x0131
        L_0x011e:
            java.lang.String r10 = "X-"
            boolean r10 = r12.startsWith(r10)     // Catch:{ all -> 0x001a }
            if (r10 == 0) goto L_0x0131
            org.telegram.messenger.AndroidUtilities$VcardItem r10 = new org.telegram.messenger.AndroidUtilities$VcardItem     // Catch:{ all -> 0x001a }
            r10.<init>()     // Catch:{ all -> 0x001a }
            r11 = 20
            r10.type = r11     // Catch:{ all -> 0x001a }
            goto L_0x008b
        L_0x0131:
            r11 = r3
            goto L_0x0146
        L_0x0133:
            org.telegram.messenger.AndroidUtilities$VcardItem r10 = new org.telegram.messenger.AndroidUtilities$VcardItem     // Catch:{ all -> 0x001a }
            r10.<init>()     // Catch:{ all -> 0x001a }
            r11 = 6
            r10.type = r11     // Catch:{ all -> 0x001a }
            goto L_0x008b
        L_0x013d:
            org.telegram.messenger.AndroidUtilities$VcardItem r10 = new org.telegram.messenger.AndroidUtilities$VcardItem     // Catch:{ all -> 0x001a }
            r10.<init>()     // Catch:{ all -> 0x001a }
            r10.type = r6     // Catch:{ all -> 0x001a }
            goto L_0x008b
        L_0x0146:
            if (r11 == 0) goto L_0x0151
            int r10 = r11.type     // Catch:{ all -> 0x001a }
            if (r10 < 0) goto L_0x0151
            r1.add(r11)     // Catch:{ all -> 0x001a }
            goto L_0x0151
        L_0x0150:
            r11 = r3
        L_0x0151:
            r10 = 0
            goto L_0x0155
        L_0x0153:
            r13 = r25
        L_0x0155:
            if (r10 != 0) goto L_0x0175
            if (r8 == 0) goto L_0x0175
            if (r11 != 0) goto L_0x0170
            java.lang.StringBuilder r3 = r8.vcard     // Catch:{ all -> 0x0345 }
            int r3 = r3.length()     // Catch:{ all -> 0x0345 }
            if (r3 <= 0) goto L_0x016a
            java.lang.StringBuilder r3 = r8.vcard     // Catch:{ all -> 0x0345 }
            r6 = 10
            r3.append(r6)     // Catch:{ all -> 0x0345 }
        L_0x016a:
            java.lang.StringBuilder r3 = r8.vcard     // Catch:{ all -> 0x0345 }
            r3.append(r12)     // Catch:{ all -> 0x0345 }
            goto L_0x0175
        L_0x0170:
            java.util.ArrayList<java.lang.String> r3 = r11.vcardData     // Catch:{ all -> 0x0345 }
            r3.add(r12)     // Catch:{ all -> 0x0345 }
        L_0x0175:
            if (r9 == 0) goto L_0x0187
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0345 }
            r3.<init>()     // Catch:{ all -> 0x0345 }
            r3.append(r9)     // Catch:{ all -> 0x0345 }
            r3.append(r12)     // Catch:{ all -> 0x0345 }
            java.lang.String r12 = r3.toString()     // Catch:{ all -> 0x0345 }
            r9 = 0
        L_0x0187:
            java.lang.String r3 = "=QUOTED-PRINTABLE"
            boolean r3 = r12.contains(r3)     // Catch:{ all -> 0x0345 }
            java.lang.String r6 = "="
            if (r3 == 0) goto L_0x01a4
            boolean r3 = r12.endsWith(r6)     // Catch:{ all -> 0x0345 }
            if (r3 == 0) goto L_0x01a4
            int r3 = r12.length()     // Catch:{ all -> 0x0345 }
            r6 = 1
            int r3 = r3 - r6
            java.lang.String r9 = r12.substring(r7, r3)     // Catch:{ all -> 0x0345 }
            r3 = 0
            goto L_0x003d
        L_0x01a4:
            if (r10 != 0) goto L_0x01ac
            if (r8 == 0) goto L_0x01ac
            if (r11 == 0) goto L_0x01ac
            r11.fullData = r12     // Catch:{ all -> 0x0345 }
        L_0x01ac:
            java.lang.String r3 = ":"
            int r3 = r12.indexOf(r3)     // Catch:{ all -> 0x0345 }
            if (r3 < 0) goto L_0x01cd
            r7 = 2
            java.lang.String[] r1 = new java.lang.String[r7]     // Catch:{ all -> 0x0345 }
            r7 = 0
            java.lang.String r16 = r12.substring(r7, r3)     // Catch:{ all -> 0x0345 }
            r1[r7] = r16     // Catch:{ all -> 0x0345 }
            int r3 = r3 + 1
            java.lang.String r3 = r12.substring(r3)     // Catch:{ all -> 0x0345 }
            java.lang.String r3 = r3.trim()     // Catch:{ all -> 0x0345 }
            r7 = 1
            r1[r7] = r3     // Catch:{ all -> 0x0345 }
            r7 = 0
            goto L_0x01d8
        L_0x01cd:
            r1 = 1
            java.lang.String[] r3 = new java.lang.String[r1]     // Catch:{ all -> 0x0345 }
            java.lang.String r1 = r12.trim()     // Catch:{ all -> 0x0345 }
            r7 = 0
            r3[r7] = r1     // Catch:{ all -> 0x0345 }
            r1 = r3
        L_0x01d8:
            int r3 = r1.length     // Catch:{ all -> 0x0345 }
            r12 = 2
            if (r3 < r12) goto L_0x02a2
            if (r8 != 0) goto L_0x01e0
            goto L_0x02a2
        L_0x01e0:
            r3 = r1[r7]     // Catch:{ all -> 0x0345 }
            java.lang.String r12 = "FN"
            boolean r3 = r3.startsWith(r12)     // Catch:{ all -> 0x0345 }
            java.lang.String r12 = "N"
            if (r3 != 0) goto L_0x0218
            r3 = r1[r7]     // Catch:{ all -> 0x0345 }
            boolean r3 = r3.startsWith(r12)     // Catch:{ all -> 0x0345 }
            if (r3 != 0) goto L_0x0218
            r3 = r1[r7]     // Catch:{ all -> 0x0345 }
            boolean r3 = r3.startsWith(r14)     // Catch:{ all -> 0x0345 }
            if (r3 == 0) goto L_0x0205
            java.lang.String r3 = r8.name     // Catch:{ all -> 0x0345 }
            boolean r3 = android.text.TextUtils.isEmpty(r3)     // Catch:{ all -> 0x0345 }
            if (r3 == 0) goto L_0x0205
            goto L_0x0218
        L_0x0205:
            r3 = 0
            r6 = r1[r3]     // Catch:{ all -> 0x0345 }
            boolean r3 = r6.startsWith(r15)     // Catch:{ all -> 0x0345 }
            if (r3 == 0) goto L_0x02a2
            java.util.ArrayList<java.lang.String> r3 = r8.phones     // Catch:{ all -> 0x0345 }
            r6 = 1
            r1 = r1[r6]     // Catch:{ all -> 0x0345 }
            r3.add(r1)     // Catch:{ all -> 0x0345 }
            goto L_0x02a2
        L_0x0218:
            r3 = 0
            r7 = r1[r3]     // Catch:{ all -> 0x0345 }
            java.lang.String r3 = ";"
            java.lang.String[] r3 = r7.split(r3)     // Catch:{ all -> 0x0345 }
            int r7 = r3.length     // Catch:{ all -> 0x0345 }
            r17 = r9
            r9 = 0
            r14 = 0
            r15 = 0
        L_0x0227:
            if (r14 >= r7) goto L_0x0260
            r18 = r7
            r7 = r3[r14]     // Catch:{ all -> 0x0345 }
            java.lang.String[] r7 = r7.split(r6)     // Catch:{ all -> 0x0345 }
            r19 = r3
            int r3 = r7.length     // Catch:{ all -> 0x0345 }
            r20 = r6
            r6 = 2
            if (r3 == r6) goto L_0x023a
            goto L_0x0257
        L_0x023a:
            r3 = 0
            r6 = r7[r3]     // Catch:{ all -> 0x0345 }
            java.lang.String r3 = "CHARSET"
            boolean r3 = r6.equals(r3)     // Catch:{ all -> 0x0345 }
            if (r3 == 0) goto L_0x0249
            r3 = 1
            r9 = r7[r3]     // Catch:{ all -> 0x0345 }
            goto L_0x0257
        L_0x0249:
            r3 = 0
            r6 = r7[r3]     // Catch:{ all -> 0x0345 }
            java.lang.String r3 = "ENCODING"
            boolean r3 = r6.equals(r3)     // Catch:{ all -> 0x0345 }
            if (r3 == 0) goto L_0x0257
            r3 = 1
            r15 = r7[r3]     // Catch:{ all -> 0x0345 }
        L_0x0257:
            int r14 = r14 + 1
            r7 = r18
            r3 = r19
            r6 = r20
            goto L_0x0227
        L_0x0260:
            r3 = 0
            r6 = r1[r3]     // Catch:{ all -> 0x0345 }
            boolean r3 = r6.startsWith(r12)     // Catch:{ all -> 0x0345 }
            if (r3 == 0) goto L_0x027c
            r3 = 1
            r1 = r1[r3]     // Catch:{ all -> 0x0345 }
            r3 = 59
            r6 = 32
            java.lang.String r1 = r1.replace(r3, r6)     // Catch:{ all -> 0x0345 }
            java.lang.String r1 = r1.trim()     // Catch:{ all -> 0x0345 }
            r8.name = r1     // Catch:{ all -> 0x0345 }
            r3 = 1
            goto L_0x0281
        L_0x027c:
            r3 = 1
            r1 = r1[r3]     // Catch:{ all -> 0x0345 }
            r8.name = r1     // Catch:{ all -> 0x0345 }
        L_0x0281:
            if (r15 == 0) goto L_0x02a5
            java.lang.String r1 = "QUOTED-PRINTABLE"
            boolean r1 = r15.equalsIgnoreCase(r1)     // Catch:{ all -> 0x0345 }
            if (r1 == 0) goto L_0x02a5
            java.lang.String r1 = r8.name     // Catch:{ all -> 0x0345 }
            byte[] r1 = getStringBytes(r1)     // Catch:{ all -> 0x0345 }
            byte[] r1 = decodeQuotedPrintable(r1)     // Catch:{ all -> 0x0345 }
            if (r1 == 0) goto L_0x02a5
            int r6 = r1.length     // Catch:{ all -> 0x0345 }
            if (r6 == 0) goto L_0x02a5
            java.lang.String r6 = new java.lang.String     // Catch:{ all -> 0x0345 }
            r6.<init>(r1, r9)     // Catch:{ all -> 0x0345 }
            r8.name = r6     // Catch:{ all -> 0x0345 }
            goto L_0x02a5
        L_0x02a2:
            r17 = r9
            r3 = 1
        L_0x02a5:
            r1 = r24
            r9 = r17
            r3 = 0
            r7 = 0
            goto L_0x003d
        L_0x02ad:
            r5.close()     // Catch:{ Exception -> 0x02b4 }
            r0.close()     // Catch:{ Exception -> 0x02b4 }
            goto L_0x02b8
        L_0x02b4:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0345 }
        L_0x02b8:
            r3 = 0
            r7 = 0
        L_0x02ba:
            int r0 = r4.size()     // Catch:{ all -> 0x001a }
            if (r7 >= r0) goto L_0x034a
            java.lang.Object r0 = r4.get(r7)     // Catch:{ all -> 0x001a }
            org.telegram.messenger.AndroidUtilities$VcardData r0 = (org.telegram.messenger.AndroidUtilities.VcardData) r0     // Catch:{ all -> 0x001a }
            java.lang.String r1 = r0.name     // Catch:{ all -> 0x001a }
            if (r1 == 0) goto L_0x0340
            java.util.ArrayList<java.lang.String> r1 = r0.phones     // Catch:{ all -> 0x001a }
            boolean r1 = r1.isEmpty()     // Catch:{ all -> 0x001a }
            if (r1 != 0) goto L_0x0340
            if (r3 != 0) goto L_0x02da
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x001a }
            r1.<init>()     // Catch:{ all -> 0x001a }
            r3 = r1
        L_0x02da:
            java.util.ArrayList<java.lang.String> r1 = r0.phones     // Catch:{ all -> 0x001a }
            r5 = 0
            java.lang.Object r1 = r1.get(r5)     // Catch:{ all -> 0x001a }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ all -> 0x001a }
            r5 = 0
        L_0x02e4:
            java.util.ArrayList<java.lang.String> r6 = r0.phones     // Catch:{ all -> 0x001a }
            int r6 = r6.size()     // Catch:{ all -> 0x001a }
            if (r5 >= r6) goto L_0x0314
            java.util.ArrayList<java.lang.String> r6 = r0.phones     // Catch:{ all -> 0x001a }
            java.lang.Object r6 = r6.get(r5)     // Catch:{ all -> 0x001a }
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ all -> 0x001a }
            int r8 = r6.length()     // Catch:{ all -> 0x001a }
            int r8 = r8 + -7
            r9 = 0
            int r8 = java.lang.Math.max(r9, r8)     // Catch:{ all -> 0x001a }
            java.lang.String r8 = r6.substring(r8)     // Catch:{ all -> 0x001a }
            org.telegram.messenger.ContactsController r10 = org.telegram.messenger.ContactsController.getInstance(r22)     // Catch:{ all -> 0x001a }
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_contact> r10 = r10.contactsByShortPhone     // Catch:{ all -> 0x001a }
            java.lang.Object r8 = r10.get(r8)     // Catch:{ all -> 0x001a }
            if (r8 == 0) goto L_0x0311
            r1 = r6
            goto L_0x0315
        L_0x0311:
            int r5 = r5 + 1
            goto L_0x02e4
        L_0x0314:
            r9 = 0
        L_0x0315:
            org.telegram.tgnet.TLRPC$TL_userContact_old2 r5 = new org.telegram.tgnet.TLRPC$TL_userContact_old2     // Catch:{ all -> 0x001a }
            r5.<init>()     // Catch:{ all -> 0x001a }
            r5.phone = r1     // Catch:{ all -> 0x001a }
            java.lang.String r1 = r0.name     // Catch:{ all -> 0x001a }
            r5.first_name = r1     // Catch:{ all -> 0x001a }
            r5.last_name = r2     // Catch:{ all -> 0x001a }
            r10 = 0
            r5.id = r10     // Catch:{ all -> 0x001a }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r1 = new org.telegram.tgnet.TLRPC$TL_restrictionReason     // Catch:{ all -> 0x001a }
            r1.<init>()     // Catch:{ all -> 0x001a }
            java.lang.StringBuilder r0 = r0.vcard     // Catch:{ all -> 0x001a }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x001a }
            r1.text = r0     // Catch:{ all -> 0x001a }
            r1.platform = r2     // Catch:{ all -> 0x001a }
            r1.reason = r2     // Catch:{ all -> 0x001a }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r0 = r5.restriction_reason     // Catch:{ all -> 0x001a }
            r0.add(r1)     // Catch:{ all -> 0x001a }
            r3.add(r5)     // Catch:{ all -> 0x001a }
            goto L_0x0341
        L_0x0340:
            r9 = 0
        L_0x0341:
            int r7 = r7 + 1
            goto L_0x02ba
        L_0x0345:
            r0 = move-exception
            r3 = 0
        L_0x0347:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x034a:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.loadVCardFromStream(android.net.Uri, int, boolean, java.util.ArrayList, java.lang.String):java.util.ArrayList");
    }

    public static Typeface getTypeface(String str) {
        Typeface typeface;
        Typeface typeface2;
        Hashtable<String, Typeface> hashtable = typefaceCache;
        synchronized (hashtable) {
            if (!hashtable.containsKey(str)) {
                try {
                    if (Build.VERSION.SDK_INT >= 26) {
                        Typeface.Builder builder = new Typeface.Builder(ApplicationLoader.applicationContext.getAssets(), str);
                        if (str.contains("medium")) {
                            builder.setWeight(700);
                        }
                        if (str.contains("italic")) {
                            builder.setItalic(true);
                        }
                        typeface2 = builder.build();
                    } else {
                        typeface2 = Typeface.createFromAsset(ApplicationLoader.applicationContext.getAssets(), str);
                    }
                    hashtable.put(str, typeface2);
                } catch (Exception e) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("Could not get typeface '" + str + "' because " + e.getMessage());
                    }
                    return null;
                }
            }
            typeface = hashtable.get(str);
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
            if (z) {
                try {
                    SmsRetriever.getClient(ApplicationLoader.applicationContext).startSmsRetriever().addOnSuccessListener(AndroidUtilities$$ExternalSyntheticLambda7.INSTANCE);
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$setWaitingForSms$5(Void voidR) {
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

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x002e */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void setWaitingForCall(boolean r4) {
        /*
            java.lang.Object r0 = callLock
            monitor-enter(r0)
            if (r4 == 0) goto L_0x001d
            org.telegram.messenger.CallReceiver r1 = callReceiver     // Catch:{ Exception -> 0x002e }
            if (r1 != 0) goto L_0x002e
            android.content.IntentFilter r1 = new android.content.IntentFilter     // Catch:{ Exception -> 0x002e }
            java.lang.String r2 = "android.intent.action.PHONE_STATE"
            r1.<init>(r2)     // Catch:{ Exception -> 0x002e }
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x002e }
            org.telegram.messenger.CallReceiver r3 = new org.telegram.messenger.CallReceiver     // Catch:{ Exception -> 0x002e }
            r3.<init>()     // Catch:{ Exception -> 0x002e }
            callReceiver = r3     // Catch:{ Exception -> 0x002e }
            r2.registerReceiver(r3, r1)     // Catch:{ Exception -> 0x002e }
            goto L_0x002e
        L_0x001d:
            org.telegram.messenger.CallReceiver r1 = callReceiver     // Catch:{ Exception -> 0x002e }
            if (r1 == 0) goto L_0x002e
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x002e }
            org.telegram.messenger.CallReceiver r2 = callReceiver     // Catch:{ Exception -> 0x002e }
            r1.unregisterReceiver(r2)     // Catch:{ Exception -> 0x002e }
            r1 = 0
            callReceiver = r1     // Catch:{ Exception -> 0x002e }
            goto L_0x002e
        L_0x002c:
            r4 = move-exception
            goto L_0x0032
        L_0x002e:
            waitingForCall = r4     // Catch:{ all -> 0x002c }
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            return
        L_0x0032:
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            throw r4
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
            FileLog.e((Throwable) e);
            return false;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:0x008a A[Catch:{ Exception -> 0x00a8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0096 A[Catch:{ Exception -> 0x00a8 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String[] getCurrentKeyboardLanguage() {
        /*
            java.lang.String r0 = "en"
            r1 = 0
            r2 = 1
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00a8 }
            java.lang.String r4 = "input_method"
            java.lang.Object r3 = r3.getSystemService(r4)     // Catch:{ Exception -> 0x00a8 }
            android.view.inputmethod.InputMethodManager r3 = (android.view.inputmethod.InputMethodManager) r3     // Catch:{ Exception -> 0x00a8 }
            android.view.inputmethod.InputMethodSubtype r4 = r3.getCurrentInputMethodSubtype()     // Catch:{ Exception -> 0x00a8 }
            r5 = 24
            r6 = 0
            if (r4 == 0) goto L_0x002c
            int r3 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x00a8 }
            if (r3 < r5) goto L_0x0020
            java.lang.String r3 = r4.getLanguageTag()     // Catch:{ Exception -> 0x00a8 }
            goto L_0x0021
        L_0x0020:
            r3 = r6
        L_0x0021:
            boolean r5 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x00a8 }
            if (r5 == 0) goto L_0x004a
            java.lang.String r3 = r4.getLocale()     // Catch:{ Exception -> 0x00a8 }
            goto L_0x004a
        L_0x002c:
            android.view.inputmethod.InputMethodSubtype r3 = r3.getLastInputMethodSubtype()     // Catch:{ Exception -> 0x00a8 }
            if (r3 == 0) goto L_0x0049
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x00a8 }
            if (r4 < r5) goto L_0x003b
            java.lang.String r4 = r3.getLanguageTag()     // Catch:{ Exception -> 0x00a8 }
            goto L_0x003c
        L_0x003b:
            r4 = r6
        L_0x003c:
            boolean r5 = android.text.TextUtils.isEmpty(r4)     // Catch:{ Exception -> 0x00a8 }
            if (r5 == 0) goto L_0x0047
            java.lang.String r3 = r3.getLocale()     // Catch:{ Exception -> 0x00a8 }
            goto L_0x004a
        L_0x0047:
            r3 = r4
            goto L_0x004a
        L_0x0049:
            r3 = r6
        L_0x004a:
            boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x00a8 }
            r5 = 45
            r7 = 95
            if (r4 == 0) goto L_0x009f
            java.lang.String r3 = org.telegram.messenger.LocaleController.getSystemLocaleStringIso639()     // Catch:{ Exception -> 0x00a8 }
            org.telegram.messenger.LocaleController r4 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x00a8 }
            org.telegram.messenger.LocaleController$LocaleInfo r4 = r4.getCurrentLocaleInfo()     // Catch:{ Exception -> 0x00a8 }
            java.lang.String r8 = r4.getBaseLangCode()     // Catch:{ Exception -> 0x00a8 }
            boolean r9 = android.text.TextUtils.isEmpty(r8)     // Catch:{ Exception -> 0x00a8 }
            if (r9 == 0) goto L_0x006e
            java.lang.String r8 = r4.getLangCode()     // Catch:{ Exception -> 0x00a8 }
        L_0x006e:
            boolean r4 = r3.contains(r8)     // Catch:{ Exception -> 0x00a8 }
            if (r4 != 0) goto L_0x007d
            boolean r4 = r8.contains(r3)     // Catch:{ Exception -> 0x00a8 }
            if (r4 == 0) goto L_0x007b
            goto L_0x007d
        L_0x007b:
            r6 = r8
            goto L_0x0084
        L_0x007d:
            boolean r4 = r3.contains(r0)     // Catch:{ Exception -> 0x00a8 }
            if (r4 != 0) goto L_0x0084
            r6 = r0
        L_0x0084:
            boolean r4 = android.text.TextUtils.isEmpty(r6)     // Catch:{ Exception -> 0x00a8 }
            if (r4 != 0) goto L_0x0096
            r4 = 2
            java.lang.String[] r4 = new java.lang.String[r4]     // Catch:{ Exception -> 0x00a8 }
            java.lang.String r3 = r3.replace(r7, r5)     // Catch:{ Exception -> 0x00a8 }
            r4[r1] = r3     // Catch:{ Exception -> 0x00a8 }
            r4[r2] = r6     // Catch:{ Exception -> 0x00a8 }
            return r4
        L_0x0096:
            java.lang.String[] r4 = new java.lang.String[r2]     // Catch:{ Exception -> 0x00a8 }
            java.lang.String r3 = r3.replace(r7, r5)     // Catch:{ Exception -> 0x00a8 }
            r4[r1] = r3     // Catch:{ Exception -> 0x00a8 }
            return r4
        L_0x009f:
            java.lang.String[] r4 = new java.lang.String[r2]     // Catch:{ Exception -> 0x00a8 }
            java.lang.String r3 = r3.replace(r7, r5)     // Catch:{ Exception -> 0x00a8 }
            r4[r1] = r3     // Catch:{ Exception -> 0x00a8 }
            return r4
        L_0x00a8:
            java.lang.String[] r2 = new java.lang.String[r2]
            r2[r1] = r0
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.getCurrentKeyboardLanguage():java.lang.String[]");
    }

    public static void hideKeyboard(View view) {
        if (view != null) {
            try {
                InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService("input_method");
                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public static ArrayList<File> getDataDirs() {
        File[] externalFilesDirs;
        ArrayList<File> arrayList = null;
        if (Build.VERSION.SDK_INT >= 19 && (externalFilesDirs = ApplicationLoader.applicationContext.getExternalFilesDirs((String) null)) != null) {
            for (int i = 0; i < externalFilesDirs.length; i++) {
                if (externalFilesDirs[i] != null) {
                    externalFilesDirs[i].getAbsolutePath();
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                    }
                    arrayList.add(externalFilesDirs[i]);
                }
            }
        }
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
        if (arrayList.isEmpty()) {
            arrayList.add(Environment.getExternalStorageDirectory());
        }
        return arrayList;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0019, code lost:
        r4 = r0[r3].getAbsolutePath();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.ArrayList<java.io.File> getRootDirs() {
        /*
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 0
            r2 = 19
            if (r0 < r2) goto L_0x003d
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.io.File[] r0 = r0.getExternalFilesDirs(r1)
            if (r0 == 0) goto L_0x003d
            r2 = 0
            r3 = 0
        L_0x0011:
            int r4 = r0.length
            if (r3 >= r4) goto L_0x003d
            r4 = r0[r3]
            if (r4 != 0) goto L_0x0019
            goto L_0x003a
        L_0x0019:
            r4 = r0[r3]
            java.lang.String r4 = r4.getAbsolutePath()
            java.lang.String r5 = "/Android"
            int r5 = r4.indexOf(r5)
            if (r5 < 0) goto L_0x003a
            if (r1 != 0) goto L_0x002e
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
        L_0x002e:
            java.io.File r6 = new java.io.File
            java.lang.String r4 = r4.substring(r2, r5)
            r6.<init>(r4)
            r1.add(r6)
        L_0x003a:
            int r3 = r3 + 1
            goto L_0x0011
        L_0x003d:
            if (r1 != 0) goto L_0x0044
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
        L_0x0044:
            boolean r0 = r1.isEmpty()
            if (r0 == 0) goto L_0x0051
            java.io.File r0 = android.os.Environment.getExternalStorageDirectory()
            r1.add(r0)
        L_0x0051:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.getRootDirs():java.util.ArrayList");
    }

    public static File getCacheDir() {
        String str;
        File file;
        try {
            str = Environment.getExternalStorageState();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            str = null;
        }
        if (str == null || str.startsWith("mounted")) {
            try {
                if (Build.VERSION.SDK_INT >= 19) {
                    File[] externalCacheDirs = ApplicationLoader.applicationContext.getExternalCacheDirs();
                    int i = 0;
                    file = externalCacheDirs[0];
                    if (!TextUtils.isEmpty(SharedConfig.storageCacheDir)) {
                        while (true) {
                            if (i < externalCacheDirs.length) {
                                if (externalCacheDirs[i] != null && externalCacheDirs[i].getAbsolutePath().startsWith(SharedConfig.storageCacheDir)) {
                                    file = externalCacheDirs[i];
                                    break;
                                }
                                i++;
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
            File cacheDir = ApplicationLoader.applicationContext.getCacheDir();
            if (cacheDir != null) {
                return cacheDir;
            }
        } catch (Exception e3) {
            FileLog.e((Throwable) e3);
        }
        return new File("");
    }

    public static int dp(float f) {
        if (f == 0.0f) {
            return 0;
        }
        return (int) Math.ceil((double) (density * f));
    }

    public static int dpr(float f) {
        if (f == 0.0f) {
            return 0;
        }
        return Math.round(density * f);
    }

    public static int dp2(float f) {
        if (f == 0.0f) {
            return 0;
        }
        return (int) Math.floor((double) (density * f));
    }

    public static float dpf2(float f) {
        if (f == 0.0f) {
            return 0.0f;
        }
        return density * f;
    }

    public static void checkDisplaySize(Context context, Configuration configuration) {
        Display defaultDisplay;
        try {
            float f = density;
            float f2 = context.getResources().getDisplayMetrics().density;
            density = f2;
            if (firstConfigurationWas && ((double) Math.abs(f - f2)) > 0.001d) {
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
            if (!(windowManager == null || (defaultDisplay = windowManager.getDefaultDisplay()) == null)) {
                defaultDisplay.getMetrics(displayMetrics);
                defaultDisplay.getSize(displaySize);
                screenRefreshRate = defaultDisplay.getRefreshRate();
            }
            int i = configuration.screenWidthDp;
            if (i != 0) {
                int ceil = (int) Math.ceil((double) (((float) i) * density));
                if (Math.abs(displaySize.x - ceil) > 3) {
                    displaySize.x = ceil;
                }
            }
            int i2 = configuration.screenHeightDp;
            if (i2 != 0) {
                int ceil2 = (int) Math.ceil((double) (((float) i2) * density));
                if (Math.abs(displaySize.y - ceil2) > 3) {
                    displaySize.y = ceil2;
                }
            }
            if (roundMessageSize == 0) {
                if (isTablet()) {
                    roundMessageSize = (int) (((float) getMinTabletSide()) * 0.6f);
                    roundPlayingMessageSize = getMinTabletSide() - dp(28.0f);
                } else {
                    Point point = displaySize;
                    roundMessageSize = (int) (((float) Math.min(point.x, point.y)) * 0.6f);
                    Point point2 = displaySize;
                    roundPlayingMessageSize = Math.min(point2.x, point2.y) - dp(28.0f);
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

    public static double fixLocationCoord(double d) {
        double d2 = (double) ((long) (d * 1000000.0d));
        Double.isNaN(d2);
        return d2 / 1000000.0d;
    }

    public static String formapMapUrl(int i, double d, double d2, int i2, int i3, boolean z, int i4, int i5) {
        int min = Math.min(2, (int) Math.ceil((double) density));
        int i6 = i5;
        int i7 = i6 == -1 ? MessagesController.getInstance(i).mapProvider : i6;
        if (i7 == 1 || i7 == 3) {
            String str = null;
            String[] strArr = {"ru_RU", "tr_TR"};
            LocaleController.LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
            for (int i8 = 0; i8 < 2; i8++) {
                if (strArr[i8].toLowerCase().contains(currentLocaleInfo.shortName)) {
                    str = strArr[i8];
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
        String str2 = MessagesController.getInstance(i).mapKey;
        if (!TextUtils.isEmpty(str2)) {
            if (z) {
                return String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%.6f,%.6f&zoom=%d&size=%dx%d&maptype=roadmap&scale=%d&markers=color:red%%7Csize:mid%%7C%.6f,%.6f&sensor=false&key=%s", new Object[]{Double.valueOf(d), Double.valueOf(d2), Integer.valueOf(i4), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(min), Double.valueOf(d), Double.valueOf(d2), str2});
            }
            return String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%.6f,%.6f&zoom=%d&size=%dx%d&maptype=roadmap&scale=%d&key=%s", new Object[]{Double.valueOf(d), Double.valueOf(d2), Integer.valueOf(i4), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(min), str2});
        } else if (z) {
            return String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%.6f,%.6f&zoom=%d&size=%dx%d&maptype=roadmap&scale=%d&markers=color:red%%7Csize:mid%%7C%.6f,%.6f&sensor=false", new Object[]{Double.valueOf(d), Double.valueOf(d2), Integer.valueOf(i4), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(min), Double.valueOf(d), Double.valueOf(d2)});
        } else {
            return String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%.6f,%.6f&zoom=%d&size=%dx%d&maptype=roadmap&scale=%d", new Object[]{Double.valueOf(d), Double.valueOf(d2), Integer.valueOf(i4), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(min)});
        }
    }

    public static float getPixelsInCM(float f, boolean z) {
        return (f / 2.54f) * (z ? displayMetrics.xdpi : displayMetrics.ydpi);
    }

    public static int getPeerLayerVersion(int i) {
        return Math.max(73, (i >> 16) & 65535);
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long j) {
        if (ApplicationLoader.applicationHandler != null) {
            if (j == 0) {
                ApplicationLoader.applicationHandler.post(runnable);
            } else {
                ApplicationLoader.applicationHandler.postDelayed(runnable, j);
            }
        }
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        if (ApplicationLoader.applicationHandler != null) {
            ApplicationLoader.applicationHandler.removeCallbacks(runnable);
        }
    }

    public static boolean isTabletInternal() {
        if (isTablet == null) {
            isTablet = Boolean.valueOf(ApplicationLoader.applicationContext != null && ApplicationLoader.applicationContext.getResources().getBoolean(R.bool.isTablet));
        }
        return isTablet.booleanValue();
    }

    public static boolean isTablet() {
        return isTabletInternal() && !SharedConfig.forceDisableTabletMode;
    }

    public static boolean isSmallScreen() {
        if (isSmallScreen == null) {
            Point point = displaySize;
            isSmallScreen = Boolean.valueOf(((float) ((Math.max(point.x, point.y) - statusBarHeight) - navigationBarHeight)) / density <= 650.0f);
        }
        return isSmallScreen.booleanValue();
    }

    public static boolean isSmallTablet() {
        Point point = displaySize;
        return ((float) Math.min(point.x, point.y)) / density <= 690.0f;
    }

    public static int getMinTabletSide() {
        if (!isSmallTablet()) {
            Point point = displaySize;
            int min = Math.min(point.x, point.y);
            int i = (min * 35) / 100;
            if (i < dp(320.0f)) {
                i = dp(320.0f);
            }
            return min - i;
        }
        Point point2 = displaySize;
        int min2 = Math.min(point2.x, point2.y);
        Point point3 = displaySize;
        int max = Math.max(point3.x, point3.y);
        int i2 = (max * 35) / 100;
        if (i2 < dp(320.0f)) {
            i2 = dp(320.0f);
        }
        return Math.min(min2, max - i2);
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
                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                Method declaredMethod = Class.forName(telephonyManager.getClass().getName()).getDeclaredMethod("getITelephony", new Class[0]);
                declaredMethod.setAccessible(true);
                ITelephony iTelephony = (ITelephony) declaredMethod.invoke(telephonyManager, new Object[0]);
                ITelephony iTelephony2 = (ITelephony) declaredMethod.invoke(telephonyManager, new Object[0]);
                iTelephony2.silenceRinger();
                iTelephony2.endCall();
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:24:0x006f */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String obtainLoginPhoneCall(java.lang.String r10) {
        /*
            boolean r0 = hasCallPermissions
            r1 = 0
            if (r0 != 0) goto L_0x0006
            return r1
        L_0x0006:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0070 }
            android.content.ContentResolver r2 = r0.getContentResolver()     // Catch:{ Exception -> 0x0070 }
            android.net.Uri r3 = android.provider.CallLog.Calls.CONTENT_URI     // Catch:{ Exception -> 0x0070 }
            r0 = 2
            java.lang.String[] r4 = new java.lang.String[r0]     // Catch:{ Exception -> 0x0070 }
            java.lang.String r0 = "number"
            r8 = 0
            r4[r8] = r0     // Catch:{ Exception -> 0x0070 }
            java.lang.String r0 = "date"
            r9 = 1
            r4[r9] = r0     // Catch:{ Exception -> 0x0070 }
            java.lang.String r5 = "type IN (3,1,5)"
            r6 = 0
            java.lang.String r7 = "date DESC LIMIT 5"
            android.database.Cursor r0 = r2.query(r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0070 }
        L_0x0024:
            boolean r2 = r0.moveToNext()     // Catch:{ all -> 0x0069 }
            if (r2 == 0) goto L_0x0065
            java.lang.String r2 = r0.getString(r8)     // Catch:{ all -> 0x0069 }
            long r3 = r0.getLong(r9)     // Catch:{ all -> 0x0069 }
            boolean r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0069 }
            if (r5 == 0) goto L_0x004a
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0069 }
            r5.<init>()     // Catch:{ all -> 0x0069 }
            java.lang.String r6 = "number = "
            r5.append(r6)     // Catch:{ all -> 0x0069 }
            r5.append(r2)     // Catch:{ all -> 0x0069 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0069 }
            org.telegram.messenger.FileLog.e((java.lang.String) r5)     // Catch:{ all -> 0x0069 }
        L_0x004a:
            long r5 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0069 }
            long r5 = r5 - r3
            long r3 = java.lang.Math.abs(r5)     // Catch:{ all -> 0x0069 }
            r5 = 3600000(0x36ee80, double:1.7786363E-317)
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 < 0) goto L_0x005b
            goto L_0x0024
        L_0x005b:
            boolean r3 = checkPhonePattern(r10, r2)     // Catch:{ all -> 0x0069 }
            if (r3 == 0) goto L_0x0024
            r0.close()     // Catch:{ Exception -> 0x0070 }
            return r2
        L_0x0065:
            r0.close()     // Catch:{ Exception -> 0x0070 }
            goto L_0x0074
        L_0x0069:
            r10 = move-exception
            if (r0 == 0) goto L_0x006f
            r0.close()     // Catch:{ all -> 0x006f }
        L_0x006f:
            throw r10     // Catch:{ Exception -> 0x0070 }
        L_0x0070:
            r10 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r10)
        L_0x0074:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.obtainLoginPhoneCall(java.lang.String):java.lang.String");
    }

    public static boolean checkPhonePattern(String str, String str2) {
        if (!TextUtils.isEmpty(str) && !str.equals("*")) {
            String[] split = str.split("\\*");
            String stripExceptNumbers = PhoneFormat.stripExceptNumbers(str2);
            int i = 0;
            for (String str3 : split) {
                if (!TextUtils.isEmpty(str3)) {
                    int indexOf = stripExceptNumbers.indexOf(str3, i);
                    if (indexOf == -1) {
                        return false;
                    }
                    i = indexOf + str3.length();
                }
            }
        }
        return true;
    }

    public static int getViewInset(View view) {
        int i;
        if (!(view == null || (i = Build.VERSION.SDK_INT) < 21 || view.getHeight() == displaySize.y || view.getHeight() == displaySize.y - statusBarHeight)) {
            if (i >= 23) {
                try {
                    WindowInsets rootWindowInsets = view.getRootWindowInsets();
                    if (rootWindowInsets != null) {
                        return rootWindowInsets.getStableInsetBottom();
                    }
                    return 0;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else {
                if (mAttachInfoField == null) {
                    Field declaredField = View.class.getDeclaredField("mAttachInfo");
                    mAttachInfoField = declaredField;
                    declaredField.setAccessible(true);
                }
                Object obj = mAttachInfoField.get(view);
                if (obj != null) {
                    if (mStableInsetsField == null) {
                        Field declaredField2 = obj.getClass().getDeclaredField("mStableInsets");
                        mStableInsetsField = declaredField2;
                        declaredField2.setAccessible(true);
                    }
                    return ((Rect) mStableInsetsField.get(obj)).bottom;
                }
            }
        }
        return 0;
    }

    public static Point getRealScreenSize() {
        Point point = new Point();
        try {
            WindowManager windowManager = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
            if (Build.VERSION.SDK_INT >= 17) {
                windowManager.getDefaultDisplay().getRealSize(point);
            } else {
                try {
                    point.set(((Integer) Display.class.getMethod("getRawWidth", new Class[0]).invoke(windowManager.getDefaultDisplay(), new Object[0])).intValue(), ((Integer) Display.class.getMethod("getRawHeight", new Class[0]).invoke(windowManager.getDefaultDisplay(), new Object[0])).intValue());
                } catch (Exception e) {
                    point.set(windowManager.getDefaultDisplay().getWidth(), windowManager.getDefaultDisplay().getHeight());
                    FileLog.e((Throwable) e);
                }
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
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

    public static int charSequenceIndexOf(CharSequence charSequence, CharSequence charSequence2, int i) {
        while (i < charSequence.length() - charSequence2.length()) {
            boolean z = false;
            int i2 = 0;
            while (true) {
                if (i2 >= charSequence2.length()) {
                    z = true;
                    break;
                } else if (charSequence2.charAt(i2) != charSequence.charAt(i + i2)) {
                    break;
                } else {
                    i2++;
                }
            }
            if (z) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static int charSequenceIndexOf(CharSequence charSequence, CharSequence charSequence2) {
        return charSequenceIndexOf(charSequence, charSequence2, 0);
    }

    public static boolean charSequenceContains(CharSequence charSequence, CharSequence charSequence2) {
        return charSequenceIndexOf(charSequence, charSequence2) != -1;
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
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                Field declaredField = ViewPager.class.getDeclaredField("mLeftEdge");
                declaredField.setAccessible(true);
                EdgeEffect edgeEffect = (EdgeEffect) declaredField.get(viewPager);
                if (edgeEffect != null) {
                    edgeEffect.setColor(i);
                }
                Field declaredField2 = ViewPager.class.getDeclaredField("mRightEdge");
                declaredField2.setAccessible(true);
                EdgeEffect edgeEffect2 = (EdgeEffect) declaredField2.get(viewPager);
                if (edgeEffect2 != null) {
                    edgeEffect2.setColor(i);
                }
            } catch (Exception unused) {
            }
        }
    }

    public static void setScrollViewEdgeEffectColor(HorizontalScrollView horizontalScrollView, int i) {
        int i2 = Build.VERSION.SDK_INT;
        if (i2 >= 29) {
            horizontalScrollView.setEdgeEffectColor(i);
        } else if (i2 >= 21) {
            try {
                Field declaredField = HorizontalScrollView.class.getDeclaredField("mEdgeGlowLeft");
                declaredField.setAccessible(true);
                EdgeEffect edgeEffect = (EdgeEffect) declaredField.get(horizontalScrollView);
                if (edgeEffect != null) {
                    edgeEffect.setColor(i);
                }
                Field declaredField2 = HorizontalScrollView.class.getDeclaredField("mEdgeGlowRight");
                declaredField2.setAccessible(true);
                EdgeEffect edgeEffect2 = (EdgeEffect) declaredField2.get(horizontalScrollView);
                if (edgeEffect2 != null) {
                    edgeEffect2.setColor(i);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public static void setScrollViewEdgeEffectColor(ScrollView scrollView, int i) {
        int i2 = Build.VERSION.SDK_INT;
        if (i2 >= 29) {
            scrollView.setTopEdgeEffectColor(i);
            scrollView.setBottomEdgeEffectColor(i);
        } else if (i2 >= 21) {
            try {
                Field declaredField = ScrollView.class.getDeclaredField("mEdgeGlowTop");
                declaredField.setAccessible(true);
                EdgeEffect edgeEffect = (EdgeEffect) declaredField.get(scrollView);
                if (edgeEffect != null) {
                    edgeEffect.setColor(i);
                }
                Field declaredField2 = ScrollView.class.getDeclaredField("mEdgeGlowBottom");
                declaredField2.setAccessible(true);
                EdgeEffect edgeEffect2 = (EdgeEffect) declaredField2.get(scrollView);
                if (edgeEffect2 != null) {
                    edgeEffect2.setColor(i);
                }
            } catch (Exception unused) {
            }
        }
    }

    @SuppressLint({"NewApi"})
    public static void clearDrawableAnimation(View view) {
        if (Build.VERSION.SDK_INT >= 21 && view != null) {
            if (view instanceof ListView) {
                Drawable selector = ((ListView) view).getSelector();
                if (selector != null) {
                    selector.setState(StateSet.NOTHING);
                    return;
                }
                return;
            }
            Drawable background = view.getBackground();
            if (background != null) {
                background.setState(StateSet.NOTHING);
                background.jumpToCurrentState();
            }
        }
    }

    public static SpannableStringBuilder replaceTags(String str) {
        return replaceTags(str, 11, new Object[0]);
    }

    public static SpannableStringBuilder replaceTags(String str, int i, Object... objArr) {
        try {
            StringBuilder sb = new StringBuilder(str);
            if ((i & 1) != 0) {
                while (true) {
                    int indexOf = sb.indexOf("<br>");
                    if (indexOf == -1) {
                        break;
                    }
                    sb.replace(indexOf, indexOf + 4, "\n");
                }
                while (true) {
                    int indexOf2 = sb.indexOf("<br/>");
                    if (indexOf2 == -1) {
                        break;
                    }
                    sb.replace(indexOf2, indexOf2 + 5, "\n");
                }
            }
            ArrayList arrayList = new ArrayList();
            if ((i & 2) != 0) {
                while (true) {
                    int indexOf3 = sb.indexOf("<b>");
                    if (indexOf3 == -1) {
                        break;
                    }
                    sb.replace(indexOf3, indexOf3 + 3, "");
                    int indexOf4 = sb.indexOf("</b>");
                    if (indexOf4 == -1) {
                        indexOf4 = sb.indexOf("<b>");
                    }
                    sb.replace(indexOf4, indexOf4 + 4, "");
                    arrayList.add(Integer.valueOf(indexOf3));
                    arrayList.add(Integer.valueOf(indexOf4));
                }
                while (true) {
                    int indexOf5 = sb.indexOf("**");
                    if (indexOf5 == -1) {
                        break;
                    }
                    sb.replace(indexOf5, indexOf5 + 2, "");
                    int indexOf6 = sb.indexOf("**");
                    if (indexOf6 >= 0) {
                        sb.replace(indexOf6, indexOf6 + 2, "");
                        arrayList.add(Integer.valueOf(indexOf5));
                        arrayList.add(Integer.valueOf(indexOf6));
                    }
                }
            }
            if ((i & 8) != 0) {
                while (true) {
                    int indexOf7 = sb.indexOf("**");
                    if (indexOf7 == -1) {
                        break;
                    }
                    sb.replace(indexOf7, indexOf7 + 2, "");
                    int indexOf8 = sb.indexOf("**");
                    if (indexOf8 >= 0) {
                        sb.replace(indexOf8, indexOf8 + 2, "");
                        arrayList.add(Integer.valueOf(indexOf7));
                        arrayList.add(Integer.valueOf(indexOf8));
                    }
                }
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(sb);
            for (int i2 = 0; i2 < arrayList.size() / 2; i2++) {
                int i3 = i2 * 2;
                spannableStringBuilder.setSpan(new TypefaceSpan(getTypeface("fonts/rmedium.ttf")), ((Integer) arrayList.get(i3)).intValue(), ((Integer) arrayList.get(i3 + 1)).intValue(), 33);
            }
            return spannableStringBuilder;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return new SpannableStringBuilder(str);
        }
    }

    public static class LinkMovementMethodMy extends LinkMovementMethod {
        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                boolean onTouchEvent = super.onTouchEvent(textView, spannable, motionEvent);
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    Selection.removeSelection(spannable);
                }
                return onTouchEvent;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return false;
            }
        }
    }

    public static boolean needShowPasscode() {
        return needShowPasscode(false);
    }

    public static boolean needShowPasscode(boolean z) {
        boolean isWasInBackground = ForegroundDetector.getInstance().isWasInBackground(z);
        if (z) {
            ForegroundDetector.getInstance().resetBackgroundVar();
        }
        int elapsedRealtime = (int) (SystemClock.elapsedRealtime() / 1000);
        if (BuildVars.LOGS_ENABLED && z && SharedConfig.passcodeHash.length() > 0) {
            FileLog.d("wasInBackground = " + isWasInBackground + " appLocked = " + SharedConfig.appLocked + " autoLockIn = " + SharedConfig.autoLockIn + " lastPauseTime = " + SharedConfig.lastPauseTime + " uptime = " + elapsedRealtime);
        }
        return SharedConfig.passcodeHash.length() > 0 && isWasInBackground && (SharedConfig.appLocked || ((SharedConfig.autoLockIn != 0 && SharedConfig.lastPauseTime != 0 && !SharedConfig.appLocked && SharedConfig.lastPauseTime + SharedConfig.autoLockIn <= elapsedRealtime) || elapsedRealtime + 5 < SharedConfig.lastPauseTime));
    }

    public static void shakeView(final View view, final float f, final int i) {
        if (view != null) {
            if (i == 6) {
                view.setTranslationX(0.0f);
                return;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, "translationX", new float[]{(float) dp(f)})});
            animatorSet.setDuration(50);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    View view = view;
                    int i = i;
                    AndroidUtilities.shakeView(view, i == 5 ? 0.0f : -f, i + 1);
                }
            });
            animatorSet.start();
        }
    }

    public static void shakeViewSpring(View view) {
        shakeViewSpring(view, 10.0f, (Runnable) null);
    }

    public static void shakeViewSpring(View view, float f) {
        shakeViewSpring(view, f, (Runnable) null);
    }

    public static void shakeViewSpring(View view, Runnable runnable) {
        shakeViewSpring(view, 10.0f, runnable);
    }

    public static void shakeViewSpring(View view, float f, Runnable runnable) {
        ((SpringAnimation) ((SpringAnimation) new SpringAnimation(view, DynamicAnimation.TRANSLATION_X, 0.0f).setSpring(new SpringForce(0.0f).setStiffness(600.0f)).setStartVelocity((float) ((-dp(f)) * 100))).addEndListener(new AndroidUtilities$$ExternalSyntheticLambda6(runnable))).start();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$shakeViewSpring$6(Runnable runnable, DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void startAppCenter(Activity activity) {
        try {
            if (BuildVars.DEBUG_VERSION) {
                Distribute.setEnabledForDebuggableBuild(true);
                AppCenter.start(activity.getApplication(), BuildVars.APPCENTER_HASH, Distribute.class, Crashes.class);
                AppCenter.setUserId("uid=" + UserConfig.getInstance(UserConfig.selectedAccount).clientUserId);
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public static void checkForUpdates() {
        try {
            if (BuildVars.DEBUG_VERSION && SystemClock.elapsedRealtime() - lastUpdateCheckTime >= 3600000) {
                lastUpdateCheckTime = SystemClock.elapsedRealtime();
                Distribute.checkForUpdate();
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public static void appCenterLog(Throwable th) {
        try {
            Crashes.trackError(th);
        } catch (Throwable unused) {
        }
    }

    public static boolean shouldShowClipboardToast() {
        return Build.VERSION.SDK_INT < 31 || !OneUIUtilities.hasBuiltInClipboardToasts();
    }

    public static boolean addToClipboard(CharSequence charSequence) {
        try {
            ClipboardManager clipboardManager = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
            if (charSequence instanceof Spanned) {
                clipboardManager.setPrimaryClip(ClipData.newHtmlText("label", charSequence, CustomHtml.toHtml((Spanned) charSequence)));
                return true;
            }
            clipboardManager.setPrimaryClip(ClipData.newPlainText("label", charSequence));
            return true;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    public static void addMediaToGallery(String str) {
        if (str != null) {
            addMediaToGallery(new File(str));
        }
    }

    public static void addMediaToGallery(File file) {
        Uri fromFile = Uri.fromFile(file);
        if (fromFile != null) {
            try {
                Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                intent.setData(fromFile);
                ApplicationLoader.applicationContext.sendBroadcast(intent);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    private static File getAlbumDir(boolean z) {
        if (z || !BuildVars.NO_SCOPED_STORAGE || (Build.VERSION.SDK_INT >= 23 && ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0)) {
            return FileLoader.getDirectory(0);
        }
        if ("mounted".equals(Environment.getExternalStorageState())) {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Telegram");
            if (file.mkdirs() || file.exists()) {
                return file;
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

    @SuppressLint({"NewApi"})
    public static String getPath(Uri uri) {
        Uri uri2;
        try {
            if ((Build.VERSION.SDK_INT >= 19) && DocumentsContract.isDocumentUri(ApplicationLoader.applicationContext, uri)) {
                if (isExternalStorageDocument(uri)) {
                    String[] split = DocumentsContract.getDocumentId(uri).split(":");
                    if ("primary".equalsIgnoreCase(split[0])) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                } else if (isDownloadsDocument(uri)) {
                    return getDataColumn(ApplicationLoader.applicationContext, ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(DocumentsContract.getDocumentId(uri)).longValue()), (String) null, (String[]) null);
                } else if (isMediaDocument(uri)) {
                    String[] split2 = DocumentsContract.getDocumentId(uri).split(":");
                    String str = split2[0];
                    char c = 65535;
                    int hashCode = str.hashCode();
                    if (hashCode != 93166550) {
                        if (hashCode != NUM) {
                            if (hashCode == NUM) {
                                if (str.equals("video")) {
                                    c = 1;
                                }
                            }
                        } else if (str.equals("image")) {
                            c = 0;
                        }
                    } else if (str.equals("audio")) {
                        c = 2;
                    }
                    if (c == 0) {
                        uri2 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if (c == 1) {
                        uri2 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if (c != 2) {
                        uri2 = null;
                    } else {
                        uri2 = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    return getDataColumn(ApplicationLoader.applicationContext, uri2, "_id=?", new String[]{split2[1]});
                }
                return null;
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(ApplicationLoader.applicationContext, uri, (String) null, (String[]) null);
            } else {
                if ("file".equalsIgnoreCase(uri.getScheme())) {
                    return uri.getPath();
                }
                return null;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x004a */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:21:0x004a=Splitter:B:21:0x004a, B:13:0x003e=Splitter:B:13:0x003e} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getDataColumn(android.content.Context r8, android.net.Uri r9, java.lang.String r10, java.lang.String[] r11) {
        /*
            r0 = 1
            java.lang.String[] r3 = new java.lang.String[r0]
            r0 = 0
            java.lang.String r7 = "_data"
            r3[r0] = r7
            r0 = 0
            android.content.ContentResolver r1 = r8.getContentResolver()     // Catch:{ Exception -> 0x0050 }
            r6 = 0
            r2 = r9
            r4 = r10
            r5 = r11
            android.database.Cursor r8 = r1.query(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x0050 }
            if (r8 == 0) goto L_0x004b
            boolean r9 = r8.moveToFirst()     // Catch:{ all -> 0x0046 }
            if (r9 == 0) goto L_0x004b
            int r9 = r8.getColumnIndexOrThrow(r7)     // Catch:{ all -> 0x0046 }
            java.lang.String r9 = r8.getString(r9)     // Catch:{ all -> 0x0046 }
            java.lang.String r10 = "content://"
            boolean r10 = r9.startsWith(r10)     // Catch:{ all -> 0x0046 }
            if (r10 != 0) goto L_0x0042
            java.lang.String r10 = "/"
            boolean r10 = r9.startsWith(r10)     // Catch:{ all -> 0x0046 }
            if (r10 != 0) goto L_0x003e
            java.lang.String r10 = "file://"
            boolean r10 = r9.startsWith(r10)     // Catch:{ all -> 0x0046 }
            if (r10 != 0) goto L_0x003e
            goto L_0x0042
        L_0x003e:
            r8.close()     // Catch:{ Exception -> 0x0050 }
            return r9
        L_0x0042:
            r8.close()     // Catch:{ Exception -> 0x0050 }
            return r0
        L_0x0046:
            r9 = move-exception
            r8.close()     // Catch:{ all -> 0x004a }
        L_0x004a:
            throw r9     // Catch:{ Exception -> 0x0050 }
        L_0x004b:
            if (r8 == 0) goto L_0x0050
            r8.close()     // Catch:{ Exception -> 0x0050 }
        L_0x0050:
            return r0
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
        return generatePicturePath(false, (String) null);
    }

    public static File generatePicturePath(boolean z, String str) {
        try {
            File directory = FileLoader.getDirectory(100);
            if (!z) {
                if (directory != null) {
                    return new File(directory, generateFileName(0, str));
                }
            }
            return new File(ApplicationLoader.applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), generateFileName(0, str));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    public static String generateFileName(int i, String str) {
        Date date = new Date();
        date.setTime(System.currentTimeMillis() + ((long) Utilities.random.nextInt(1000)) + 1);
        String format = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(date);
        if (i == 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("IMG_");
            sb.append(format);
            sb.append(".");
            if (TextUtils.isEmpty(str)) {
                str = "jpg";
            }
            sb.append(str);
            return sb.toString();
        }
        return "VID_" + format + ".mp4";
    }

    public static CharSequence generateSearchName(String str, String str2, String str3) {
        if ((str == null && str2 == null) || TextUtils.isEmpty(str3)) {
            return "";
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (str == null || str.length() == 0) {
            str = str2;
        } else if (!(str2 == null || str2.length() == 0)) {
            str = str + " " + str2;
        }
        String trim = str.trim();
        String str4 = " " + trim.toLowerCase();
        int i = 0;
        while (true) {
            int indexOf = str4.indexOf(" " + str3, i);
            if (indexOf == -1) {
                break;
            }
            int i2 = 1;
            int i3 = indexOf - (indexOf == 0 ? 0 : 1);
            int length = str3.length();
            if (indexOf == 0) {
                i2 = 0;
            }
            int i4 = length + i2 + i3;
            if (i != 0 && i != i3 + 1) {
                spannableStringBuilder.append(trim.substring(i, i3));
            } else if (i == 0 && i3 != 0) {
                spannableStringBuilder.append(trim.substring(0, i3));
            }
            String substring = trim.substring(i3, Math.min(trim.length(), i4));
            if (substring.startsWith(" ")) {
                spannableStringBuilder.append(" ");
            }
            String trim2 = substring.trim();
            int length2 = spannableStringBuilder.length();
            spannableStringBuilder.append(trim2);
            spannableStringBuilder.setSpan(new ForegroundColorSpanThemable("windowBackgroundWhiteBlueText4"), length2, trim2.length() + length2, 33);
            i = i4;
        }
        if (i != -1 && i < trim.length()) {
            spannableStringBuilder.append(trim.substring(i));
        }
        return spannableStringBuilder;
    }

    public static boolean isKeyguardSecure() {
        return ((KeyguardManager) ApplicationLoader.applicationContext.getSystemService("keyguard")).isKeyguardSecure();
    }

    public static boolean isSimAvailable() {
        TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
        int simState = telephonyManager.getSimState();
        if (simState == 1 || simState == 0 || telephonyManager.getPhoneType() == 0 || isAirplaneModeOn()) {
            return false;
        }
        return true;
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

    public static File generateVideoPath(boolean z) {
        try {
            File albumDir = getAlbumDir(z);
            Date date = new Date();
            date.setTime(System.currentTimeMillis() + ((long) Utilities.random.nextInt(1000)) + 1);
            if (generatingVideoPathFormat == null) {
                generatingVideoPathFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US);
            }
            String format = generatingVideoPathFormat.format(date);
            return new File(albumDir, "VID_" + format + ".mp4");
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    public static String formatFileSize(long j) {
        return formatFileSize(j, false);
    }

    public static String formatFileSize(long j, boolean z) {
        if (j < 1024) {
            return String.format("%d B", new Object[]{Long.valueOf(j)});
        } else if (j < 1048576) {
            float f = ((float) j) / 1024.0f;
            if (z) {
                int i = (int) f;
                if ((f - ((float) i)) * 10.0f == 0.0f) {
                    return String.format("%d KB", new Object[]{Integer.valueOf(i)});
                }
            }
            return String.format("%.1f KB", new Object[]{Float.valueOf(f)});
        } else if (j < NUM) {
            float f2 = (((float) j) / 1024.0f) / 1024.0f;
            if (z) {
                int i2 = (int) f2;
                if ((f2 - ((float) i2)) * 10.0f == 0.0f) {
                    return String.format("%d MB", new Object[]{Integer.valueOf(i2)});
                }
            }
            return String.format("%.1f MB", new Object[]{Float.valueOf(f2)});
        } else {
            float f3 = ((float) ((int) ((j / 1024) / 1024))) / 1000.0f;
            if (z) {
                int i3 = (int) f3;
                if ((f3 - ((float) i3)) * 10.0f == 0.0f) {
                    return String.format("%d GB", new Object[]{Integer.valueOf(i3)});
                }
            }
            return String.format("%.2f GB", new Object[]{Float.valueOf(f3)});
        }
    }

    public static String formatShortDuration(int i) {
        return formatDuration(i, false);
    }

    public static String formatLongDuration(int i) {
        return formatDuration(i, true);
    }

    public static String formatDuration(int i, boolean z) {
        int i2 = i / 3600;
        int i3 = (i / 60) % 60;
        int i4 = i % 60;
        if (i2 != 0) {
            return String.format(Locale.US, "%d:%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4)});
        } else if (z) {
            return String.format(Locale.US, "%02d:%02d", new Object[]{Integer.valueOf(i3), Integer.valueOf(i4)});
        } else {
            return String.format(Locale.US, "%d:%02d", new Object[]{Integer.valueOf(i3), Integer.valueOf(i4)});
        }
    }

    public static String formatFullDuration(int i) {
        int i2 = i / 3600;
        int i3 = (i / 60) % 60;
        int i4 = i % 60;
        if (i < 0) {
            return String.format(Locale.US, "-%02d:%02d:%02d", new Object[]{Integer.valueOf(Math.abs(i2)), Integer.valueOf(Math.abs(i3)), Integer.valueOf(Math.abs(i4))});
        }
        return String.format(Locale.US, "%02d:%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4)});
    }

    public static String formatDurationNoHours(int i, boolean z) {
        int i2 = i / 60;
        int i3 = i % 60;
        if (z) {
            return String.format(Locale.US, "%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(i3)});
        }
        return String.format(Locale.US, "%d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(i3)});
    }

    public static String formatShortDuration(int i, int i2) {
        return formatDuration(i, i2, false);
    }

    public static String formatLongDuration(int i, int i2) {
        return formatDuration(i, i2, true);
    }

    public static String formatDuration(int i, int i2, boolean z) {
        int i3 = i2 / 3600;
        int i4 = (i2 / 60) % 60;
        int i5 = i2 % 60;
        int i6 = i / 3600;
        int i7 = (i / 60) % 60;
        int i8 = i % 60;
        if (i2 == 0) {
            if (i6 != 0) {
                return String.format(Locale.US, "%d:%02d:%02d / -:--", new Object[]{Integer.valueOf(i6), Integer.valueOf(i7), Integer.valueOf(i8)});
            } else if (z) {
                return String.format(Locale.US, "%02d:%02d / -:--", new Object[]{Integer.valueOf(i7), Integer.valueOf(i8)});
            } else {
                return String.format(Locale.US, "%d:%02d / -:--", new Object[]{Integer.valueOf(i7), Integer.valueOf(i8)});
            }
        } else if (i6 != 0 || i3 != 0) {
            return String.format(Locale.US, "%d:%02d:%02d / %d:%02d:%02d", new Object[]{Integer.valueOf(i6), Integer.valueOf(i7), Integer.valueOf(i8), Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5)});
        } else if (z) {
            return String.format(Locale.US, "%02d:%02d / %02d:%02d", new Object[]{Integer.valueOf(i7), Integer.valueOf(i8), Integer.valueOf(i4), Integer.valueOf(i5)});
        } else {
            return String.format(Locale.US, "%d:%02d / %d:%02d", new Object[]{Integer.valueOf(i7), Integer.valueOf(i8), Integer.valueOf(i4), Integer.valueOf(i5)});
        }
    }

    public static String formatVideoDuration(int i, int i2) {
        int i3 = i2 / 3600;
        int i4 = (i2 / 60) % 60;
        int i5 = i2 % 60;
        int i6 = i / 3600;
        int i7 = (i / 60) % 60;
        int i8 = i % 60;
        if (i6 == 0 && i3 == 0) {
            return String.format(Locale.US, "%02d:%02d / %02d:%02d", new Object[]{Integer.valueOf(i7), Integer.valueOf(i8), Integer.valueOf(i4), Integer.valueOf(i5)});
        } else if (i3 == 0) {
            return String.format(Locale.US, "%d:%02d:%02d / %02d:%02d", new Object[]{Integer.valueOf(i6), Integer.valueOf(i7), Integer.valueOf(i8), Integer.valueOf(i4), Integer.valueOf(i5)});
        } else if (i6 == 0) {
            return String.format(Locale.US, "%02d:%02d / %d:%02d:%02d", new Object[]{Integer.valueOf(i7), Integer.valueOf(i8), Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5)});
        } else {
            return String.format(Locale.US, "%d:%02d:%02d / %d:%02d:%02d", new Object[]{Integer.valueOf(i6), Integer.valueOf(i7), Integer.valueOf(i8), Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5)});
        }
    }

    public static String formatCount(int i) {
        if (i < 1000) {
            return Integer.toString(i);
        }
        ArrayList arrayList = new ArrayList();
        while (i != 0) {
            int i2 = i % 1000;
            i /= 1000;
            if (i > 0) {
                arrayList.add(String.format(Locale.ENGLISH, "%03d", new Object[]{Integer.valueOf(i2)}));
            } else {
                arrayList.add(Integer.toString(i2));
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            sb.append((String) arrayList.get(size));
            if (size != 0) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public static String formatWholeNumber(int i, int i2) {
        if (i == 0) {
            return "0";
        }
        float f = (float) i;
        if (i2 == 0) {
            i2 = i;
        }
        if (i2 < 1000) {
            return formatCount(i);
        }
        int i3 = 0;
        while (i2 >= 1000 && i3 < numbersSignatureArray.length - 1) {
            i2 /= 1000;
            f /= 1000.0f;
            i3++;
        }
        if (((double) f) < 0.1d) {
            return "0";
        }
        float f2 = f * 10.0f;
        float f3 = (float) ((int) f2);
        if (f2 == f3) {
            return String.format(Locale.ENGLISH, "%s%s", new Object[]{formatCount((int) f), numbersSignatureArray[i3]});
        }
        return String.format(Locale.ENGLISH, "%.1f%s", new Object[]{Float.valueOf(f3 / 10.0f), numbersSignatureArray[i3]});
    }

    public static byte[] decodeQuotedPrintable(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i = 0;
        while (i < bArr.length) {
            byte b = bArr[i];
            if (b == 61) {
                int i2 = i + 1;
                try {
                    int digit = Character.digit((char) bArr[i2], 16);
                    i = i2 + 1;
                    byteArrayOutputStream.write((char) ((digit << 4) + Character.digit((char) bArr[i], 16)));
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                    return null;
                }
            } else {
                byteArrayOutputStream.write(b);
            }
            i++;
        }
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        try {
            byteArrayOutputStream.close();
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        return byteArray;
    }

    public static boolean copyFile(InputStream inputStream, File file) throws IOException {
        return copyFile(inputStream, (OutputStream) new FileOutputStream(file));
    }

    public static boolean copyFile(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bArr = new byte[4096];
        while (true) {
            int read = inputStream.read(bArr);
            if (read > 0) {
                Thread.yield();
                outputStream.write(bArr, 0, read);
            } else {
                outputStream.close();
                return true;
            }
        }
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:20:0x003b */
    /* JADX WARNING: Missing exception handler attribute for start block: B:25:0x0040 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean copyFile(java.io.File r8, java.io.File r9) throws java.io.IOException {
        /*
            boolean r0 = r8.equals(r9)
            r1 = 1
            if (r0 == 0) goto L_0x0008
            return r1
        L_0x0008:
            boolean r0 = r9.exists()
            if (r0 != 0) goto L_0x0011
            r9.createNewFile()
        L_0x0011:
            java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ Exception -> 0x0041 }
            r0.<init>(r8)     // Catch:{ Exception -> 0x0041 }
            java.io.FileOutputStream r8 = new java.io.FileOutputStream     // Catch:{ all -> 0x003c }
            r8.<init>(r9)     // Catch:{ all -> 0x003c }
            java.nio.channels.FileChannel r2 = r8.getChannel()     // Catch:{ all -> 0x0037 }
            java.nio.channels.FileChannel r3 = r0.getChannel()     // Catch:{ all -> 0x0037 }
            r4 = 0
            java.nio.channels.FileChannel r9 = r0.getChannel()     // Catch:{ all -> 0x0037 }
            long r6 = r9.size()     // Catch:{ all -> 0x0037 }
            r2.transferFrom(r3, r4, r6)     // Catch:{ all -> 0x0037 }
            r8.close()     // Catch:{ all -> 0x003c }
            r0.close()     // Catch:{ Exception -> 0x0041 }
            return r1
        L_0x0037:
            r9 = move-exception
            r8.close()     // Catch:{ all -> 0x003b }
        L_0x003b:
            throw r9     // Catch:{ all -> 0x003c }
        L_0x003c:
            r8 = move-exception
            r0.close()     // Catch:{ all -> 0x0040 }
        L_0x0040:
            throw r8     // Catch:{ Exception -> 0x0041 }
        L_0x0041:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
            r8 = 0
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.copyFile(java.io.File, java.io.File):boolean");
    }

    public static byte[] calcAuthKeyHash(byte[] bArr) {
        byte[] bArr2 = new byte[16];
        System.arraycopy(Utilities.computeSHA1(bArr), 0, bArr2, 0, 16);
        return bArr2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:79:?, code lost:
        return;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:58:0x0110 */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0114 A[Catch:{ Exception -> 0x013e }] */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x012f A[Catch:{ Exception -> 0x013e }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void openDocument(org.telegram.messenger.MessageObject r12, android.app.Activity r13, org.telegram.ui.ActionBar.BaseFragment r14) {
        /*
            if (r12 != 0) goto L_0x0003
            return
        L_0x0003:
            org.telegram.tgnet.TLRPC$Document r0 = r12.getDocument()
            if (r0 != 0) goto L_0x000a
            return
        L_0x000a:
            org.telegram.tgnet.TLRPC$Message r1 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            if (r1 == 0) goto L_0x0015
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r0)
            goto L_0x0017
        L_0x0015:
            java.lang.String r1 = ""
        L_0x0017:
            org.telegram.tgnet.TLRPC$Message r2 = r12.messageOwner
            java.lang.String r2 = r2.attachPath
            r3 = 0
            if (r2 == 0) goto L_0x002e
            int r2 = r2.length()
            if (r2 == 0) goto L_0x002e
            java.io.File r2 = new java.io.File
            org.telegram.tgnet.TLRPC$Message r4 = r12.messageOwner
            java.lang.String r4 = r4.attachPath
            r2.<init>(r4)
            goto L_0x002f
        L_0x002e:
            r2 = r3
        L_0x002f:
            if (r2 == 0) goto L_0x0037
            boolean r4 = r2.exists()
            if (r4 != 0) goto L_0x0043
        L_0x0037:
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.FileLoader r2 = org.telegram.messenger.FileLoader.getInstance(r2)
            org.telegram.tgnet.TLRPC$Message r4 = r12.messageOwner
            java.io.File r2 = r2.getPathToMessage(r4)
        L_0x0043:
            if (r2 == 0) goto L_0x017c
            boolean r4 = r2.exists()
            if (r4 == 0) goto L_0x017c
            java.lang.String r4 = "OK"
            java.lang.String r5 = "AppName"
            r6 = 1
            if (r14 == 0) goto L_0x00a1
            java.lang.String r7 = r2.getName()
            java.lang.String r7 = r7.toLowerCase()
            java.lang.String r8 = "attheme"
            boolean r7 = r7.endsWith(r8)
            if (r7 == 0) goto L_0x00a1
            java.lang.String r12 = r12.getDocumentName()
            org.telegram.ui.ActionBar.Theme$ThemeInfo r12 = org.telegram.ui.ActionBar.Theme.applyThemeFile(r2, r12, r3, r6)
            if (r12 == 0) goto L_0x0076
            org.telegram.ui.ThemePreviewActivity r13 = new org.telegram.ui.ThemePreviewActivity
            r13.<init>(r12)
            r14.presentFragment(r13)
            goto L_0x017c
        L_0x0076:
            org.telegram.ui.ActionBar.AlertDialog$Builder r12 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r12.<init>((android.content.Context) r13)
            int r13 = org.telegram.messenger.R.string.AppName
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r5, r13)
            r12.setTitle(r13)
            int r13 = org.telegram.messenger.R.string.IncorrectTheme
            java.lang.String r0 = "IncorrectTheme"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
            r12.setMessage(r13)
            int r13 = org.telegram.messenger.R.string.OK
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r4, r13)
            r12.setPositiveButton(r13, r3)
            org.telegram.ui.ActionBar.AlertDialog r12 = r12.create()
            r14.showDialog(r12)
            goto L_0x017c
        L_0x00a1:
            android.content.Intent r7 = new android.content.Intent     // Catch:{ Exception -> 0x013e }
            java.lang.String r8 = "android.intent.action.VIEW"
            r7.<init>(r8)     // Catch:{ Exception -> 0x013e }
            r7.setFlags(r6)     // Catch:{ Exception -> 0x013e }
            android.webkit.MimeTypeMap r8 = android.webkit.MimeTypeMap.getSingleton()     // Catch:{ Exception -> 0x013e }
            r9 = 46
            int r9 = r1.lastIndexOf(r9)     // Catch:{ Exception -> 0x013e }
            r10 = -1
            if (r9 == r10) goto L_0x00d1
            int r9 = r9 + r6
            java.lang.String r1 = r1.substring(r9)     // Catch:{ Exception -> 0x013e }
            java.lang.String r1 = r1.toLowerCase()     // Catch:{ Exception -> 0x013e }
            java.lang.String r1 = r8.getMimeTypeFromExtension(r1)     // Catch:{ Exception -> 0x013e }
            if (r1 != 0) goto L_0x00d2
            java.lang.String r1 = r0.mime_type     // Catch:{ Exception -> 0x013e }
            if (r1 == 0) goto L_0x00d1
            int r0 = r1.length()     // Catch:{ Exception -> 0x013e }
            if (r0 != 0) goto L_0x00d2
        L_0x00d1:
            r1 = r3
        L_0x00d2:
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x013e }
            java.lang.String r8 = ".provider"
            r9 = 24
            java.lang.String r10 = "text/plain"
            if (r0 < r9) goto L_0x00fc
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x013e }
            r0.<init>()     // Catch:{ Exception -> 0x013e }
            java.lang.String r11 = org.telegram.messenger.ApplicationLoader.getApplicationId()     // Catch:{ Exception -> 0x013e }
            r0.append(r11)     // Catch:{ Exception -> 0x013e }
            r0.append(r8)     // Catch:{ Exception -> 0x013e }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x013e }
            android.net.Uri r0 = androidx.core.content.FileProvider.getUriForFile(r13, r0, r2)     // Catch:{ Exception -> 0x013e }
            if (r1 == 0) goto L_0x00f7
            r11 = r1
            goto L_0x00f8
        L_0x00f7:
            r11 = r10
        L_0x00f8:
            r7.setDataAndType(r0, r11)     // Catch:{ Exception -> 0x013e }
            goto L_0x0108
        L_0x00fc:
            android.net.Uri r0 = android.net.Uri.fromFile(r2)     // Catch:{ Exception -> 0x013e }
            if (r1 == 0) goto L_0x0104
            r11 = r1
            goto L_0x0105
        L_0x0104:
            r11 = r10
        L_0x0105:
            r7.setDataAndType(r0, r11)     // Catch:{ Exception -> 0x013e }
        L_0x0108:
            r0 = 500(0x1f4, float:7.0E-43)
            if (r1 == 0) goto L_0x013a
            r13.startActivityForResult(r7, r0)     // Catch:{ Exception -> 0x0110 }
            goto L_0x017c
        L_0x0110:
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x013e }
            if (r1 < r9) goto L_0x012f
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x013e }
            r1.<init>()     // Catch:{ Exception -> 0x013e }
            java.lang.String r9 = org.telegram.messenger.ApplicationLoader.getApplicationId()     // Catch:{ Exception -> 0x013e }
            r1.append(r9)     // Catch:{ Exception -> 0x013e }
            r1.append(r8)     // Catch:{ Exception -> 0x013e }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x013e }
            android.net.Uri r1 = androidx.core.content.FileProvider.getUriForFile(r13, r1, r2)     // Catch:{ Exception -> 0x013e }
            r7.setDataAndType(r1, r10)     // Catch:{ Exception -> 0x013e }
            goto L_0x0136
        L_0x012f:
            android.net.Uri r1 = android.net.Uri.fromFile(r2)     // Catch:{ Exception -> 0x013e }
            r7.setDataAndType(r1, r10)     // Catch:{ Exception -> 0x013e }
        L_0x0136:
            r13.startActivityForResult(r7, r0)     // Catch:{ Exception -> 0x013e }
            goto L_0x017c
        L_0x013a:
            r13.startActivityForResult(r7, r0)     // Catch:{ Exception -> 0x013e }
            goto L_0x017c
        L_0x013e:
            if (r13 != 0) goto L_0x0142
            return
        L_0x0142:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r0.<init>((android.content.Context) r13)
            int r13 = org.telegram.messenger.R.string.AppName
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r5, r13)
            r0.setTitle(r13)
            int r13 = org.telegram.messenger.R.string.OK
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r4, r13)
            r0.setPositiveButton(r13, r3)
            int r13 = org.telegram.messenger.R.string.NoHandleAppInstalled
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r2 = 0
            org.telegram.tgnet.TLRPC$Document r12 = r12.getDocument()
            java.lang.String r12 = r12.mime_type
            r1[r2] = r12
            java.lang.String r12 = "NoHandleAppInstalled"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r12, r13, r1)
            r0.setMessage(r12)
            if (r14 == 0) goto L_0x0179
            org.telegram.ui.ActionBar.AlertDialog r12 = r0.create()
            r14.showDialog(r12)
            goto L_0x017c
        L_0x0179:
            r0.show()
        L_0x017c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.openDocument(org.telegram.messenger.MessageObject, android.app.Activity, org.telegram.ui.ActionBar.BaseFragment):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0036, code lost:
        if (r8.length() != 0) goto L_0x003c;
     */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0068  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0098 A[SYNTHETIC, Splitter:B:35:0x0098] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00c6  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean openForView(java.io.File r6, java.lang.String r7, java.lang.String r8, android.app.Activity r9, org.telegram.ui.ActionBar.Theme.ResourcesProvider r10) {
        /*
            if (r6 == 0) goto L_0x00ca
            boolean r0 = r6.exists()
            if (r0 == 0) goto L_0x00ca
            android.content.Intent r0 = new android.content.Intent
            java.lang.String r1 = "android.intent.action.VIEW"
            r0.<init>(r1)
            r1 = 1
            r0.setFlags(r1)
            android.webkit.MimeTypeMap r2 = android.webkit.MimeTypeMap.getSingleton()
            r3 = 46
            int r3 = r7.lastIndexOf(r3)
            r4 = -1
            r5 = 0
            if (r3 == r4) goto L_0x003b
            int r3 = r3 + r1
            java.lang.String r7 = r7.substring(r3)
            java.lang.String r7 = r7.toLowerCase()
            java.lang.String r7 = r2.getMimeTypeFromExtension(r7)
            if (r7 != 0) goto L_0x0039
            if (r8 == 0) goto L_0x003b
            int r7 = r8.length()
            if (r7 != 0) goto L_0x003c
            goto L_0x003b
        L_0x0039:
            r8 = r7
            goto L_0x003c
        L_0x003b:
            r8 = r5
        L_0x003c:
            int r7 = android.os.Build.VERSION.SDK_INT
            r2 = 26
            if (r7 < r2) goto L_0x0060
            if (r8 == 0) goto L_0x0060
            java.lang.String r2 = "application/vnd.android.package-archive"
            boolean r2 = r8.equals(r2)
            if (r2 == 0) goto L_0x0060
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.pm.PackageManager r2 = r2.getPackageManager()
            boolean r2 = r2.canRequestPackageInstalls()
            if (r2 != 0) goto L_0x0060
            android.app.Dialog r6 = org.telegram.ui.Components.AlertsCreator.createApkRestrictedDialog(r9, r10)
            r6.show()
            return r1
        L_0x0060:
            java.lang.String r10 = ".provider"
            r2 = 24
            java.lang.String r3 = "text/plain"
            if (r7 < r2) goto L_0x0088
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r4 = org.telegram.messenger.ApplicationLoader.getApplicationId()
            r7.append(r4)
            r7.append(r10)
            java.lang.String r7 = r7.toString()
            android.net.Uri r7 = androidx.core.content.FileProvider.getUriForFile(r9, r7, r6)
            if (r8 == 0) goto L_0x0083
            r4 = r8
            goto L_0x0084
        L_0x0083:
            r4 = r3
        L_0x0084:
            r0.setDataAndType(r7, r4)
            goto L_0x0094
        L_0x0088:
            android.net.Uri r7 = android.net.Uri.fromFile(r6)
            if (r8 == 0) goto L_0x0090
            r4 = r8
            goto L_0x0091
        L_0x0090:
            r4 = r3
        L_0x0091:
            r0.setDataAndType(r7, r4)
        L_0x0094:
            r7 = 500(0x1f4, float:7.0E-43)
            if (r8 == 0) goto L_0x00c6
            r9.startActivityForResult(r0, r7)     // Catch:{ Exception -> 0x009c }
            goto L_0x00c9
        L_0x009c:
            int r8 = android.os.Build.VERSION.SDK_INT
            if (r8 < r2) goto L_0x00bb
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r2 = org.telegram.messenger.ApplicationLoader.getApplicationId()
            r8.append(r2)
            r8.append(r10)
            java.lang.String r8 = r8.toString()
            android.net.Uri r6 = androidx.core.content.FileProvider.getUriForFile(r9, r8, r6)
            r0.setDataAndType(r6, r3)
            goto L_0x00c2
        L_0x00bb:
            android.net.Uri r6 = android.net.Uri.fromFile(r6)
            r0.setDataAndType(r6, r3)
        L_0x00c2:
            r9.startActivityForResult(r0, r7)
            goto L_0x00c9
        L_0x00c6:
            r9.startActivityForResult(r0, r7)
        L_0x00c9:
            return r1
        L_0x00ca:
            r6 = 0
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.openForView(java.io.File, java.lang.String, java.lang.String, android.app.Activity, org.telegram.ui.ActionBar.Theme$ResourcesProvider):boolean");
    }

    public static boolean openForView(MessageObject messageObject, Activity activity, Theme.ResourcesProvider resourcesProvider) {
        String str = messageObject.messageOwner.attachPath;
        String str2 = null;
        File file = (str == null || str.length() == 0) ? null : new File(messageObject.messageOwner.attachPath);
        if (file == null || !file.exists()) {
            file = FileLoader.getInstance(messageObject.currentAccount).getPathToMessage(messageObject.messageOwner);
        }
        int i = messageObject.type;
        if (i == 9 || i == 0) {
            str2 = messageObject.getMimeType();
        }
        return openForView(file, messageObject.getFileName(), str2, activity, resourcesProvider);
    }

    public static boolean openForView(TLRPC$Document tLRPC$Document, boolean z, Activity activity) {
        return openForView(FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(tLRPC$Document, true), FileLoader.getAttachFileName(tLRPC$Document), tLRPC$Document.mime_type, activity, (Theme.ResourcesProvider) null);
    }

    public static SpannableStringBuilder formatSpannableSimple(String str, CharSequence... charSequenceArr) {
        return formatSpannable(str, AndroidUtilities$$ExternalSyntheticLambda13.INSTANCE, charSequenceArr);
    }

    public static SpannableStringBuilder formatSpannable(String str, CharSequence... charSequenceArr) {
        if (str.contains("%s")) {
            return formatSpannableSimple(str, charSequenceArr);
        }
        return formatSpannable(str, AndroidUtilities$$ExternalSyntheticLambda12.INSTANCE, charSequenceArr);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ String lambda$formatSpannable$8(Integer num) {
        return "%" + (num.intValue() + 1) + "$s";
    }

    public static SpannableStringBuilder formatSpannable(String str, GenericProvider<Integer, String> genericProvider, CharSequence... charSequenceArr) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
        for (int i = 0; i < charSequenceArr.length; i++) {
            String provide = genericProvider.provide(Integer.valueOf(i));
            int indexOf = str.indexOf(provide);
            if (indexOf != -1) {
                spannableStringBuilder.replace(indexOf, provide.length() + indexOf, charSequenceArr[i]);
                str = str.substring(0, indexOf) + charSequenceArr[i].toString() + str.substring(indexOf + provide.length());
            }
        }
        return spannableStringBuilder;
    }

    public static CharSequence replaceTwoNewLinesToOne(CharSequence charSequence) {
        char[] cArr = new char[2];
        if (charSequence instanceof StringBuilder) {
            StringBuilder sb = (StringBuilder) charSequence;
            int length = charSequence.length();
            int i = 0;
            while (i < length - 2) {
                int i2 = i + 2;
                sb.getChars(i, i2, cArr, 0);
                if (cArr[0] == 10 && cArr[1] == 10) {
                    sb = sb.replace(i, i2, "\n");
                    i--;
                    length--;
                }
                i++;
            }
            return charSequence;
        } else if (!(charSequence instanceof SpannableStringBuilder)) {
            return charSequence.toString().replace("\n\n", "\n");
        } else {
            SpannableStringBuilder spannableStringBuilder = (SpannableStringBuilder) charSequence;
            int length2 = charSequence.length();
            int i3 = 0;
            while (i3 < length2 - 2) {
                int i4 = i3 + 2;
                spannableStringBuilder.getChars(i3, i4, cArr, 0);
                if (cArr[0] == 10 && cArr[1] == 10) {
                    spannableStringBuilder = spannableStringBuilder.replace(i3, i4, "\n");
                    i3--;
                    length2--;
                }
                i3++;
            }
            return charSequence;
        }
    }

    public static CharSequence replaceNewLines(CharSequence charSequence) {
        int i = 0;
        if (charSequence instanceof StringBuilder) {
            StringBuilder sb = (StringBuilder) charSequence;
            int length = charSequence.length();
            while (i < length) {
                if (charSequence.charAt(i) == 10) {
                    sb.setCharAt(i, ' ');
                }
                i++;
            }
            return charSequence;
        } else if (!(charSequence instanceof SpannableStringBuilder)) {
            return charSequence.toString().replace(10, ' ');
        } else {
            SpannableStringBuilder spannableStringBuilder = (SpannableStringBuilder) charSequence;
            int length2 = charSequence.length();
            while (i < length2) {
                if (charSequence.charAt(i) == 10) {
                    spannableStringBuilder.replace(i, i + 1, " ");
                }
                i++;
            }
            return charSequence;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0053, code lost:
        if (r1.length() != 0) goto L_0x0056;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean openForView(org.telegram.tgnet.TLObject r8, android.app.Activity r9) {
        /*
            r0 = 0
            if (r8 == 0) goto L_0x00c3
            if (r9 != 0) goto L_0x0007
            goto L_0x00c3
        L_0x0007:
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r8)
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.FileLoader r2 = org.telegram.messenger.FileLoader.getInstance(r2)
            r3 = 1
            java.io.File r2 = r2.getPathToAttach(r8, r3)
            if (r2 == 0) goto L_0x00c3
            boolean r4 = r2.exists()
            if (r4 == 0) goto L_0x00c3
            android.content.Intent r0 = new android.content.Intent
            java.lang.String r4 = "android.intent.action.VIEW"
            r0.<init>(r4)
            r0.setFlags(r3)
            android.webkit.MimeTypeMap r4 = android.webkit.MimeTypeMap.getSingleton()
            r5 = 46
            int r5 = r1.lastIndexOf(r5)
            r6 = -1
            r7 = 0
            if (r5 == r6) goto L_0x0057
            int r5 = r5 + r3
            java.lang.String r1 = r1.substring(r5)
            java.lang.String r1 = r1.toLowerCase()
            java.lang.String r1 = r4.getMimeTypeFromExtension(r1)
            if (r1 != 0) goto L_0x0056
            boolean r4 = r8 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r4 == 0) goto L_0x004d
            org.telegram.tgnet.TLRPC$TL_document r8 = (org.telegram.tgnet.TLRPC$TL_document) r8
            java.lang.String r1 = r8.mime_type
        L_0x004d:
            if (r1 == 0) goto L_0x0057
            int r8 = r1.length()
            if (r8 != 0) goto L_0x0056
            goto L_0x0057
        L_0x0056:
            r7 = r1
        L_0x0057:
            int r8 = android.os.Build.VERSION.SDK_INT
            java.lang.String r1 = ".provider"
            r4 = 24
            java.lang.String r5 = "text/plain"
            if (r8 < r4) goto L_0x0081
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r6 = org.telegram.messenger.ApplicationLoader.getApplicationId()
            r8.append(r6)
            r8.append(r1)
            java.lang.String r8 = r8.toString()
            android.net.Uri r8 = androidx.core.content.FileProvider.getUriForFile(r9, r8, r2)
            if (r7 == 0) goto L_0x007c
            r6 = r7
            goto L_0x007d
        L_0x007c:
            r6 = r5
        L_0x007d:
            r0.setDataAndType(r8, r6)
            goto L_0x008d
        L_0x0081:
            android.net.Uri r8 = android.net.Uri.fromFile(r2)
            if (r7 == 0) goto L_0x0089
            r6 = r7
            goto L_0x008a
        L_0x0089:
            r6 = r5
        L_0x008a:
            r0.setDataAndType(r8, r6)
        L_0x008d:
            r8 = 500(0x1f4, float:7.0E-43)
            if (r7 == 0) goto L_0x00bf
            r9.startActivityForResult(r0, r8)     // Catch:{ Exception -> 0x0095 }
            goto L_0x00c2
        L_0x0095:
            int r6 = android.os.Build.VERSION.SDK_INT
            if (r6 < r4) goto L_0x00b4
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r6 = org.telegram.messenger.ApplicationLoader.getApplicationId()
            r4.append(r6)
            r4.append(r1)
            java.lang.String r1 = r4.toString()
            android.net.Uri r1 = androidx.core.content.FileProvider.getUriForFile(r9, r1, r2)
            r0.setDataAndType(r1, r5)
            goto L_0x00bb
        L_0x00b4:
            android.net.Uri r1 = android.net.Uri.fromFile(r2)
            r0.setDataAndType(r1, r5)
        L_0x00bb:
            r9.startActivityForResult(r0, r8)
            goto L_0x00c2
        L_0x00bf:
            r9.startActivityForResult(r0, r8)
        L_0x00c2:
            return r3
        L_0x00c3:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.openForView(org.telegram.tgnet.TLObject, android.app.Activity):boolean");
    }

    public static boolean isBannedForever(TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights) {
        return tLRPC$TL_chatBannedRights == null || Math.abs(((long) tLRPC$TL_chatBannedRights.until_date) - (System.currentTimeMillis() / 1000)) > NUM;
    }

    public static void setRectToRect(Matrix matrix, RectF rectF, RectF rectF2, int i, boolean z) {
        float f;
        float f2;
        float f3;
        boolean z2;
        float f4;
        float f5;
        float f6;
        float f7;
        if (i == 90 || i == 270) {
            f3 = rectF2.height() / rectF.width();
            f2 = rectF2.width();
            f = rectF.height();
        } else {
            f3 = rectF2.width() / rectF.width();
            f2 = rectF2.height();
            f = rectF.height();
        }
        float f8 = f2 / f;
        if (f3 < f8) {
            f3 = f8;
            z2 = true;
        } else {
            z2 = false;
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
            f4 = (-rectF.left) * f3;
            f5 = (-rectF.top) * f3;
        } else {
            f4 = rectF2.left - (rectF.left * f3);
            f5 = rectF2.top - (rectF.top * f3);
        }
        if (z2) {
            f6 = rectF2.width();
            f7 = rectF.width();
        } else {
            f6 = rectF2.height();
            f7 = rectF.height();
        }
        float f9 = (f6 - (f7 * f3)) / 2.0f;
        if (z2) {
            f4 += f9;
        } else {
            f5 += f9;
        }
        matrix.preScale(f3, f3);
        if (z) {
            matrix.preTranslate(f4, f5);
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

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0063, code lost:
        if (r3.startsWith("tg://socks") == false) goto L_0x00fe;
     */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0112  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0114  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0119  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x011c  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x011e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean handleProxyIntent(android.app.Activity r16, android.content.Intent r17) {
        /*
            java.lang.String r0 = "tg:proxy"
            java.lang.String r1 = "tg://telegram.org"
            r2 = 0
            if (r17 != 0) goto L_0x0008
            return r2
        L_0x0008:
            int r3 = r17.getFlags()     // Catch:{ Exception -> 0x0125 }
            r4 = 1048576(0x100000, float:1.469368E-39)
            r3 = r3 & r4
            if (r3 == 0) goto L_0x0012
            return r2
        L_0x0012:
            android.net.Uri r3 = r17.getData()     // Catch:{ Exception -> 0x0125 }
            if (r3 == 0) goto L_0x0125
            java.lang.String r4 = r3.getScheme()     // Catch:{ Exception -> 0x0125 }
            r5 = 1
            r6 = 0
            if (r4 == 0) goto L_0x00fe
            java.lang.String r7 = "http"
            boolean r7 = r4.equals(r7)     // Catch:{ Exception -> 0x0125 }
            java.lang.String r8 = "secret"
            java.lang.String r9 = "pass"
            java.lang.String r10 = "user"
            java.lang.String r11 = "port"
            java.lang.String r12 = "server"
            if (r7 != 0) goto L_0x009d
            java.lang.String r7 = "https"
            boolean r7 = r4.equals(r7)     // Catch:{ Exception -> 0x0125 }
            if (r7 == 0) goto L_0x003b
            goto L_0x009d
        L_0x003b:
            java.lang.String r7 = "tg"
            boolean r4 = r4.equals(r7)     // Catch:{ Exception -> 0x0125 }
            if (r4 == 0) goto L_0x00fe
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0125 }
            boolean r4 = r3.startsWith(r0)     // Catch:{ Exception -> 0x0125 }
            java.lang.String r7 = "tg://socks"
            java.lang.String r13 = "tg:socks"
            java.lang.String r14 = "tg://proxy"
            if (r4 != 0) goto L_0x0065
            boolean r4 = r3.startsWith(r14)     // Catch:{ Exception -> 0x0125 }
            if (r4 != 0) goto L_0x0065
            boolean r4 = r3.startsWith(r13)     // Catch:{ Exception -> 0x0125 }
            if (r4 != 0) goto L_0x0065
            boolean r4 = r3.startsWith(r7)     // Catch:{ Exception -> 0x0125 }
            if (r4 == 0) goto L_0x00fe
        L_0x0065:
            java.lang.String r0 = r3.replace(r0, r1)     // Catch:{ Exception -> 0x0125 }
            java.lang.String r0 = r0.replace(r14, r1)     // Catch:{ Exception -> 0x0125 }
            java.lang.String r0 = r0.replace(r7, r1)     // Catch:{ Exception -> 0x0125 }
            java.lang.String r0 = r0.replace(r13, r1)     // Catch:{ Exception -> 0x0125 }
            android.net.Uri r0 = android.net.Uri.parse(r0)     // Catch:{ Exception -> 0x0125 }
            java.lang.String r1 = r0.getQueryParameter(r12)     // Catch:{ Exception -> 0x0125 }
            boolean r3 = checkHostForPunycode(r1)     // Catch:{ Exception -> 0x0125 }
            if (r3 == 0) goto L_0x0087
            java.lang.String r1 = java.net.IDN.toASCII(r1, r5)     // Catch:{ Exception -> 0x0125 }
        L_0x0087:
            r6 = r1
            java.lang.String r1 = r0.getQueryParameter(r11)     // Catch:{ Exception -> 0x0125 }
            java.lang.String r3 = r0.getQueryParameter(r10)     // Catch:{ Exception -> 0x0125 }
            java.lang.String r4 = r0.getQueryParameter(r9)     // Catch:{ Exception -> 0x0125 }
            java.lang.String r0 = r0.getQueryParameter(r8)     // Catch:{ Exception -> 0x0125 }
            r9 = r1
            r8 = r6
            r6 = r3
            goto L_0x0102
        L_0x009d:
            java.lang.String r0 = r3.getHost()     // Catch:{ Exception -> 0x0125 }
            java.lang.String r0 = r0.toLowerCase()     // Catch:{ Exception -> 0x0125 }
            java.lang.String r1 = "telegram.me"
            boolean r1 = r0.equals(r1)     // Catch:{ Exception -> 0x0125 }
            if (r1 != 0) goto L_0x00bd
            java.lang.String r1 = "t.me"
            boolean r1 = r0.equals(r1)     // Catch:{ Exception -> 0x0125 }
            if (r1 != 0) goto L_0x00bd
            java.lang.String r1 = "telegram.dog"
            boolean r0 = r0.equals(r1)     // Catch:{ Exception -> 0x0125 }
            if (r0 == 0) goto L_0x00f6
        L_0x00bd:
            java.lang.String r0 = r3.getPath()     // Catch:{ Exception -> 0x0125 }
            if (r0 == 0) goto L_0x00f6
            java.lang.String r1 = "/socks"
            boolean r1 = r0.startsWith(r1)     // Catch:{ Exception -> 0x0125 }
            if (r1 != 0) goto L_0x00d3
            java.lang.String r1 = "/proxy"
            boolean r0 = r0.startsWith(r1)     // Catch:{ Exception -> 0x0125 }
            if (r0 == 0) goto L_0x00f6
        L_0x00d3:
            java.lang.String r0 = r3.getQueryParameter(r12)     // Catch:{ Exception -> 0x0125 }
            boolean r1 = checkHostForPunycode(r0)     // Catch:{ Exception -> 0x0125 }
            if (r1 == 0) goto L_0x00e1
            java.lang.String r0 = java.net.IDN.toASCII(r0, r5)     // Catch:{ Exception -> 0x0125 }
        L_0x00e1:
            r6 = r0
            java.lang.String r0 = r3.getQueryParameter(r11)     // Catch:{ Exception -> 0x0125 }
            java.lang.String r1 = r3.getQueryParameter(r10)     // Catch:{ Exception -> 0x0125 }
            java.lang.String r4 = r3.getQueryParameter(r9)     // Catch:{ Exception -> 0x0125 }
            java.lang.String r3 = r3.getQueryParameter(r8)     // Catch:{ Exception -> 0x0125 }
            r15 = r6
            r6 = r1
            r1 = r15
            goto L_0x00fa
        L_0x00f6:
            r0 = r6
            r1 = r0
            r3 = r1
            r4 = r3
        L_0x00fa:
            r9 = r0
            r8 = r1
            r0 = r3
            goto L_0x0102
        L_0x00fe:
            r0 = r6
            r4 = r0
            r8 = r4
            r9 = r8
        L_0x0102:
            boolean r1 = android.text.TextUtils.isEmpty(r8)     // Catch:{ Exception -> 0x0125 }
            if (r1 != 0) goto L_0x0125
            boolean r1 = android.text.TextUtils.isEmpty(r9)     // Catch:{ Exception -> 0x0125 }
            if (r1 != 0) goto L_0x0125
            java.lang.String r1 = ""
            if (r6 != 0) goto L_0x0114
            r10 = r1
            goto L_0x0115
        L_0x0114:
            r10 = r6
        L_0x0115:
            if (r4 != 0) goto L_0x0119
            r11 = r1
            goto L_0x011a
        L_0x0119:
            r11 = r4
        L_0x011a:
            if (r0 != 0) goto L_0x011e
            r12 = r1
            goto L_0x011f
        L_0x011e:
            r12 = r0
        L_0x011f:
            r7 = r16
            showProxyAlert(r7, r8, r9, r10, r11, r12)     // Catch:{ Exception -> 0x0125 }
            return r5
        L_0x0125:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.handleProxyIntent(android.app.Activity, android.content.Intent):boolean");
    }

    public static boolean shouldEnableAnimation() {
        int i = Build.VERSION.SDK_INT;
        if (i < 26 || i >= 28 || (!((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).isPowerSaveMode() && Settings.Global.getFloat(ApplicationLoader.applicationContext.getContentResolver(), "animator_duration_scale", 1.0f) > 0.0f)) {
            return true;
        }
        return false;
    }

    public static void showProxyAlert(Activity activity, String str, String str2, String str3, String str4, String str5) {
        String str6;
        Activity activity2 = activity;
        BottomSheet.Builder builder = new BottomSheet.Builder(activity2);
        Runnable dismissRunnable = builder.getDismissRunnable();
        builder.setApplyTopPadding(false);
        builder.setApplyBottomPadding(false);
        LinearLayout linearLayout = new LinearLayout(activity2);
        builder.setCustomView(linearLayout);
        linearLayout.setOrientation(1);
        if (!TextUtils.isEmpty(str5)) {
            TextView textView = new TextView(activity2);
            textView.setText(LocaleController.getString("UseProxyTelegramInfo2", R.string.UseProxyTelegramInfo2));
            textView.setTextColor(Theme.getColor("dialogTextGray4"));
            textView.setTextSize(1, 14.0f);
            textView.setGravity(49);
            linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 17, 8, 17, 8));
            View view = new View(activity2);
            view.setBackgroundColor(Theme.getColor("divider"));
            linearLayout.addView(view, new LinearLayout.LayoutParams(-1, 1));
        }
        int i = 0;
        while (true) {
            if (i >= 5) {
                String str7 = str2;
                break;
            }
            String str8 = null;
            if (i == 0) {
                String str9 = str2;
                str6 = LocaleController.getString("UseProxyAddress", R.string.UseProxyAddress);
                str8 = str;
            } else if (i == 1) {
                str8 = "" + str2;
                str6 = LocaleController.getString("UseProxyPort", R.string.UseProxyPort);
            } else {
                String str10 = str2;
                if (i == 2) {
                    str6 = LocaleController.getString("UseProxySecret", R.string.UseProxySecret);
                    str8 = str5;
                } else if (i == 3) {
                    str6 = LocaleController.getString("UseProxyUsername", R.string.UseProxyUsername);
                    str8 = str3;
                } else if (i == 4) {
                    str6 = LocaleController.getString("UseProxyPassword", R.string.UseProxyPassword);
                    str8 = str4;
                } else {
                    str6 = null;
                }
            }
            if (!TextUtils.isEmpty(str8)) {
                TextDetailSettingsCell textDetailSettingsCell = new TextDetailSettingsCell(activity2);
                textDetailSettingsCell.setTextAndValue(str8, str6, true);
                textDetailSettingsCell.getTextView().setTextColor(Theme.getColor("dialogTextBlack"));
                textDetailSettingsCell.getValueTextView().setTextColor(Theme.getColor("dialogTextGray3"));
                linearLayout.addView(textDetailSettingsCell, LayoutHelper.createLinear(-1, -2));
                if (i == 2) {
                    break;
                }
            }
            i++;
        }
        PickerBottomLayout pickerBottomLayout = new PickerBottomLayout(activity2, false);
        pickerBottomLayout.setBackgroundColor(Theme.getColor("dialogBackground"));
        linearLayout.addView(pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        pickerBottomLayout.cancelButton.setPadding(dp(18.0f), 0, dp(18.0f), 0);
        pickerBottomLayout.cancelButton.setTextColor(Theme.getColor("dialogTextBlue2"));
        pickerBottomLayout.cancelButton.setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
        pickerBottomLayout.cancelButton.setOnClickListener(new AndroidUtilities$$ExternalSyntheticLambda4(dismissRunnable));
        pickerBottomLayout.doneButtonTextView.setTextColor(Theme.getColor("dialogTextBlue2"));
        pickerBottomLayout.doneButton.setPadding(dp(18.0f), 0, dp(18.0f), 0);
        pickerBottomLayout.doneButtonBadgeTextView.setVisibility(8);
        pickerBottomLayout.doneButtonTextView.setText(LocaleController.getString("ConnectingConnectProxy", R.string.ConnectingConnectProxy).toUpperCase());
        pickerBottomLayout.doneButton.setOnClickListener(new AndroidUtilities$$ExternalSyntheticLambda5(str, str2, str5, str4, str3, dismissRunnable));
        builder.show();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$showProxyAlert$10(String str, String str2, String str3, String str4, String str5, Runnable runnable, View view) {
        SharedConfig.ProxyInfo proxyInfo;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("proxy_enabled", true);
        edit.putString("proxy_ip", str);
        int intValue = Utilities.parseInt((CharSequence) str2).intValue();
        edit.putInt("proxy_port", intValue);
        if (TextUtils.isEmpty(str3)) {
            edit.remove("proxy_secret");
            if (TextUtils.isEmpty(str4)) {
                edit.remove("proxy_pass");
            } else {
                edit.putString("proxy_pass", str4);
            }
            if (TextUtils.isEmpty(str5)) {
                edit.remove("proxy_user");
            } else {
                edit.putString("proxy_user", str5);
            }
            proxyInfo = new SharedConfig.ProxyInfo(str, intValue, str5, str4, "");
        } else {
            edit.remove("proxy_pass");
            edit.remove("proxy_user");
            edit.putString("proxy_secret", str3);
            proxyInfo = new SharedConfig.ProxyInfo(str, intValue, "", "", str3);
        }
        edit.commit();
        SharedConfig.currentProxy = SharedConfig.addProxy(proxyInfo);
        ConnectionsManager.setProxySettings(true, str, intValue, str5, str4, str3);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
        runnable.run();
    }

    @SuppressLint({"PrivateApi"})
    public static String getSystemProperty(String str) {
        try {
            return (String) Class.forName("android.os.SystemProperties").getMethod("get", new Class[]{String.class}).invoke((Object) null, new Object[]{str});
        } catch (Exception unused) {
            return null;
        }
    }

    public static void fixGoogleMapsBug() {
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("google_bug_NUM", 0);
        if (!sharedPreferences.contains("fixed")) {
            new File(ApplicationLoader.getFilesDirFixed(), "ZoomTables.data").delete();
            sharedPreferences.edit().putBoolean("fixed", true).apply();
        }
    }

    public static CharSequence concat(CharSequence... charSequenceArr) {
        if (charSequenceArr.length == 0) {
            return "";
        }
        int i = 0;
        boolean z = true;
        if (charSequenceArr.length == 1) {
            return charSequenceArr[0];
        }
        int length = charSequenceArr.length;
        int i2 = 0;
        while (true) {
            if (i2 >= length) {
                z = false;
                break;
            } else if (charSequenceArr[i2] instanceof Spanned) {
                break;
            } else {
                i2++;
            }
        }
        if (z) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            int length2 = charSequenceArr.length;
            while (i < length2) {
                String str = charSequenceArr[i];
                if (str == null) {
                    str = "null";
                }
                spannableStringBuilder.append(str);
                i++;
            }
            return new SpannedString(spannableStringBuilder);
        }
        StringBuilder sb = new StringBuilder();
        int length3 = charSequenceArr.length;
        while (i < length3) {
            sb.append(charSequenceArr[i]);
            i++;
        }
        return sb.toString();
    }

    public static float[] RGBtoHSB(int i, int i2, int i3) {
        float[] fArr = new float[3];
        int max = Math.max(i, i2);
        if (i3 > max) {
            max = i3;
        }
        int min = Math.min(i, i2);
        if (i3 < min) {
            min = i3;
        }
        float f = (float) max;
        float f2 = f / 255.0f;
        float f3 = 0.0f;
        float f4 = max != 0 ? ((float) (max - min)) / f : 0.0f;
        if (f4 != 0.0f) {
            float f5 = (float) (max - min);
            float f6 = ((float) (max - i)) / f5;
            float f7 = ((float) (max - i2)) / f5;
            float f8 = ((float) (max - i3)) / f5;
            float f9 = (i == max ? f8 - f7 : i2 == max ? (f6 + 2.0f) - f8 : (f7 + 4.0f) - f6) / 6.0f;
            f3 = f9 < 0.0f ? f9 + 1.0f : f9;
        }
        fArr[0] = f3;
        fArr[1] = f4;
        fArr[2] = f2;
        return fArr;
    }

    public static int HSBtoRGB(float f, float f2, float f3) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5 = 0;
        if (f2 == 0.0f) {
            i5 = (int) ((f3 * 255.0f) + 0.5f);
            i2 = i5;
            i = i2;
        } else {
            float floor = (f - ((float) Math.floor((double) f))) * 6.0f;
            float floor2 = floor - ((float) Math.floor((double) floor));
            float f4 = (1.0f - f2) * f3;
            float f5 = (1.0f - (f2 * floor2)) * f3;
            float f6 = (1.0f - (f2 * (1.0f - floor2))) * f3;
            int i6 = (int) floor;
            if (i6 == 0) {
                i3 = (int) ((f3 * 255.0f) + 0.5f);
                i4 = (int) ((f6 * 255.0f) + 0.5f);
            } else if (i6 == 1) {
                i3 = (int) ((f5 * 255.0f) + 0.5f);
                i4 = (int) ((f3 * 255.0f) + 0.5f);
            } else if (i6 != 2) {
                if (i6 == 3) {
                    i5 = (int) ((f4 * 255.0f) + 0.5f);
                    i2 = (int) ((f5 * 255.0f) + 0.5f);
                } else if (i6 == 4) {
                    i5 = (int) ((f6 * 255.0f) + 0.5f);
                    i2 = (int) ((f4 * 255.0f) + 0.5f);
                } else if (i6 != 5) {
                    i2 = 0;
                    i = 0;
                } else {
                    i5 = (int) ((f3 * 255.0f) + 0.5f);
                    i2 = (int) ((f4 * 255.0f) + 0.5f);
                    i = (int) ((f5 * 255.0f) + 0.5f);
                }
                i = (int) ((f3 * 255.0f) + 0.5f);
            } else {
                i5 = (int) ((f4 * 255.0f) + 0.5f);
                i2 = (int) ((f3 * 255.0f) + 0.5f);
                i = (int) ((f6 * 255.0f) + 0.5f);
            }
            i = (int) ((f4 * 255.0f) + 0.5f);
        }
        return ((i2 & 255) << 8) | -16777216 | ((i5 & 255) << 16) | (i & 255);
    }

    public static float computePerceivedBrightness(int i) {
        return (((((float) Color.red(i)) * 0.2126f) + (((float) Color.green(i)) * 0.7152f)) + (((float) Color.blue(i)) * 0.0722f)) / 255.0f;
    }

    public static int getPatternColor(int i) {
        return getPatternColor(i, false);
    }

    public static int getPatternColor(int i, boolean z) {
        float[] RGBtoHSB = RGBtoHSB(Color.red(i), Color.green(i), Color.blue(i));
        if (RGBtoHSB[1] > 0.0f || (RGBtoHSB[2] < 1.0f && RGBtoHSB[2] > 0.0f)) {
            RGBtoHSB[1] = Math.min(1.0f, RGBtoHSB[1] + (z ? 0.15f : 0.05f) + ((1.0f - RGBtoHSB[1]) * 0.1f));
        }
        if (z || RGBtoHSB[2] > 0.5f) {
            RGBtoHSB[2] = Math.max(0.0f, RGBtoHSB[2] * 0.65f);
        } else {
            RGBtoHSB[2] = Math.max(0.0f, Math.min(1.0f, 1.0f - (RGBtoHSB[2] * 0.65f)));
        }
        return HSBtoRGB(RGBtoHSB[0], RGBtoHSB[1], RGBtoHSB[2]) & (z ? -NUM : NUM);
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

    public static String getWallPaperUrl(Object obj) {
        if (obj instanceof TLRPC$TL_wallPaper) {
            TLRPC$TL_wallPaper tLRPC$TL_wallPaper = (TLRPC$TL_wallPaper) obj;
            String str = "https://" + MessagesController.getInstance(UserConfig.selectedAccount).linkPrefix + "/bg/" + tLRPC$TL_wallPaper.slug;
            StringBuilder sb = new StringBuilder();
            TLRPC$WallPaperSettings tLRPC$WallPaperSettings = tLRPC$TL_wallPaper.settings;
            if (tLRPC$WallPaperSettings != null) {
                if (tLRPC$WallPaperSettings.blur) {
                    sb.append("blur");
                }
                if (tLRPC$TL_wallPaper.settings.motion) {
                    if (sb.length() > 0) {
                        sb.append("+");
                    }
                    sb.append("motion");
                }
            }
            if (sb.length() <= 0) {
                return str;
            }
            return str + "?mode=" + sb.toString();
        } else if (obj instanceof WallpapersListActivity.ColorWallpaper) {
            return ((WallpapersListActivity.ColorWallpaper) obj).getUrl();
        } else {
            return null;
        }
    }

    public static float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((double) ((f - 0.5f) * 0.47123894f));
    }

    public static void makeAccessibilityAnnouncement(CharSequence charSequence) {
        AccessibilityManager accessibilityManager2 = (AccessibilityManager) ApplicationLoader.applicationContext.getSystemService("accessibility");
        if (accessibilityManager2.isEnabled()) {
            AccessibilityEvent obtain = AccessibilityEvent.obtain();
            obtain.setEventType(16384);
            obtain.getText().add(charSequence);
            accessibilityManager2.sendAccessibilityEvent(obtain);
        }
    }

    public static int getOffsetColor(int i, int i2, float f, float f2) {
        int red = Color.red(i2);
        int green = Color.green(i2);
        int blue = Color.blue(i2);
        int alpha = Color.alpha(i2);
        int red2 = Color.red(i);
        int green2 = Color.green(i);
        int blue2 = Color.blue(i);
        int alpha2 = Color.alpha(i);
        return Color.argb((int) ((((float) alpha2) + (((float) (alpha - alpha2)) * f)) * f2), (int) (((float) red2) + (((float) (red - red2)) * f)), (int) (((float) green2) + (((float) (green - green2)) * f)), (int) (((float) blue2) + (((float) (blue - blue2)) * f)));
    }

    public static int indexOfIgnoreCase(String str, String str2) {
        if (str2.isEmpty() || str.isEmpty()) {
            return str.indexOf(str2);
        }
        int i = 0;
        while (i < str.length() && str2.length() + i <= str.length()) {
            int i2 = i;
            int i3 = 0;
            while (i2 < str.length() && i3 < str2.length() && Character.toLowerCase(str.charAt(i2)) == Character.toLowerCase(str2.charAt(i3))) {
                i3++;
                i2++;
            }
            if (i3 == str2.length()) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static float lerp(float[] fArr, float f) {
        return lerp(fArr[0], fArr[1], f);
    }

    public static void lerp(RectF rectF, RectF rectF2, float f, RectF rectF3) {
        if (rectF3 != null) {
            rectF3.set(lerp(rectF.left, rectF2.left, f), lerp(rectF.top, rectF2.top, f), lerp(rectF.right, rectF2.right, f), lerp(rectF.bottom, rectF2.bottom, f));
        }
    }

    public static void lerp(Rect rect, Rect rect2, float f, Rect rect3) {
        if (rect3 != null) {
            rect3.set(lerp(rect.left, rect2.left, f), lerp(rect.top, rect2.top, f), lerp(rect.right, rect2.right, f), lerp(rect.bottom, rect2.bottom, f));
        }
    }

    public static float cascade(float f, float f2, float f3, float f4) {
        float f5 = (1.0f / f3) * f4;
        return MathUtils.clamp((f - ((f2 / f3) * (1.0f - f5))) / f5, 0.0f, 1.0f);
    }

    public static int multiplyAlphaComponent(int i, float f) {
        return ColorUtils.setAlphaComponent(i, (int) (((float) Color.alpha(i)) * f));
    }

    public static float computeDampingRatio(float f, float f2, float f3) {
        return f2 / (((float) Math.sqrt((double) (f3 * f))) * 2.0f);
    }

    public static boolean hasFlagSecureFragment() {
        return flagSecureFragment != null;
    }

    public static void setFlagSecure(BaseFragment baseFragment, boolean z) {
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            if (z) {
                try {
                    baseFragment.getParentActivity().getWindow().setFlags(8192, 8192);
                    flagSecureFragment = new WeakReference<>(baseFragment);
                } catch (Exception unused) {
                }
            } else {
                WeakReference<BaseFragment> weakReference = flagSecureFragment;
                if (weakReference != null && weakReference.get() == baseFragment) {
                    try {
                        baseFragment.getParentActivity().getWindow().clearFlags(8192);
                    } catch (Exception unused2) {
                    }
                    flagSecureFragment = null;
                }
            }
        }
    }

    public static Runnable registerFlagSecure(Window window) {
        ArrayList arrayList;
        long random = (long) (Math.random() * 9.99999999E8d);
        HashMap<Window, ArrayList<Long>> hashMap = flagSecureReasons;
        if (hashMap.containsKey(window)) {
            arrayList = hashMap.get(window);
        } else {
            ArrayList arrayList2 = new ArrayList();
            hashMap.put(window, arrayList2);
            arrayList = arrayList2;
        }
        arrayList.add(Long.valueOf(random));
        updateFlagSecure(window);
        return new AndroidUtilities$$ExternalSyntheticLambda10(arrayList, random, window);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$registerFlagSecure$11(ArrayList arrayList, long j, Window window) {
        arrayList.remove(Long.valueOf(j));
        updateFlagSecure(window);
    }

    private static void updateFlagSecure(Window window) {
        if (Build.VERSION.SDK_INT >= 23 && window != null) {
            HashMap<Window, ArrayList<Long>> hashMap = flagSecureReasons;
            if (hashMap.containsKey(window) && hashMap.get(window).size() > 0) {
                try {
                    window.addFlags(8192);
                } catch (Exception unused) {
                }
            } else {
                window.clearFlags(8192);
            }
        }
    }

    public static void openSharing(BaseFragment baseFragment, String str) {
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            baseFragment.showDialog(new ShareAlert(baseFragment.getParentActivity(), (ArrayList<MessageObject>) null, str, false, str, false));
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
        } catch (Throwable unused) {
            return "";
        }
    }

    public static boolean isPunctuationCharacter(char c) {
        if (charactersMap == null) {
            charactersMap = new HashSet<>();
            int i = 0;
            while (true) {
                char[] cArr = characters;
                if (i >= cArr.length) {
                    break;
                }
                charactersMap.add(Character.valueOf(cArr[i]));
                i++;
            }
        }
        return charactersMap.contains(Character.valueOf(c));
    }

    public static int getColorDistance(int i, int i2) {
        int red = Color.red(i);
        int green = Color.green(i);
        int blue = Color.blue(i);
        int red2 = Color.red(i2);
        int i3 = (red + red2) / 2;
        int i4 = red - red2;
        int green2 = green - Color.green(i2);
        int blue2 = blue - Color.blue(i2);
        return ((((i3 + 512) * i4) * i4) >> 8) + (green2 * 4 * green2) + ((((767 - i3) * blue2) * blue2) >> 8);
    }

    public static int getAverageColor(int i, int i2) {
        return Color.argb(255, (Color.red(i) / 2) + (Color.red(i2) / 2), (Color.green(i) / 2) + (Color.green(i2) / 2), (Color.blue(i) / 2) + (Color.blue(i2) / 2));
    }

    public static void setLightStatusBar(Window window, boolean z) {
        setLightStatusBar(window, z, false);
    }

    public static void setLightStatusBar(Window window, boolean z, boolean z2) {
        if (Build.VERSION.SDK_INT >= 23) {
            View decorView = window.getDecorView();
            int systemUiVisibility = decorView.getSystemUiVisibility();
            if (z) {
                if ((systemUiVisibility & 8192) == 0) {
                    decorView.setSystemUiVisibility(systemUiVisibility | 8192);
                }
                if (SharedConfig.noStatusBar || z2) {
                    window.setStatusBarColor(0);
                } else {
                    window.setStatusBarColor(NUM);
                }
            } else {
                if ((systemUiVisibility & 8192) != 0) {
                    decorView.setSystemUiVisibility(systemUiVisibility & -8193);
                }
                if (SharedConfig.noStatusBar || z2) {
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

    public static void setLightNavigationBar(View view, boolean z) {
        if (view != null && Build.VERSION.SDK_INT >= 26) {
            int systemUiVisibility = view.getSystemUiVisibility();
            view.setSystemUiVisibility(z ? systemUiVisibility | 16 : systemUiVisibility & -17);
        }
    }

    public static void setLightNavigationBar(Window window, boolean z) {
        if (window != null) {
            setLightNavigationBar(window.getDecorView(), z);
        }
    }

    public static void setNavigationBarColor(Window window, int i) {
        setNavigationBarColor(window, i, true);
    }

    public static void setNavigationBarColor(Window window, int i, boolean z) {
        setNavigationBarColor(window, i, z, (IntColorCallback) null);
    }

    public static void setNavigationBarColor(final Window window, int i, boolean z, IntColorCallback intColorCallback) {
        ValueAnimator valueAnimator;
        if (Build.VERSION.SDK_INT >= 21) {
            HashMap<Window, ValueAnimator> hashMap = navigationBarColorAnimators;
            if (!(hashMap == null || (valueAnimator = hashMap.get(window)) == null)) {
                valueAnimator.cancel();
                navigationBarColorAnimators.remove(window);
            }
            if (!z) {
                if (intColorCallback != null) {
                    intColorCallback.run(i);
                }
                try {
                    window.setNavigationBarColor(i);
                } catch (Exception unused) {
                }
            } else {
                ValueAnimator ofArgb = ValueAnimator.ofArgb(new int[]{window.getNavigationBarColor(), i});
                ofArgb.addUpdateListener(new AndroidUtilities$$ExternalSyntheticLambda1(intColorCallback, window));
                ofArgb.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (AndroidUtilities.navigationBarColorAnimators != null) {
                            AndroidUtilities.navigationBarColorAnimators.remove(window);
                        }
                    }
                });
                ofArgb.setDuration(200);
                ofArgb.setInterpolator(CubicBezierInterpolator.DEFAULT);
                ofArgb.start();
                if (navigationBarColorAnimators == null) {
                    navigationBarColorAnimators = new HashMap<>();
                }
                navigationBarColorAnimators.put(window, ofArgb);
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$setNavigationBarColor$12(IntColorCallback intColorCallback, Window window, ValueAnimator valueAnimator) {
        int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        if (intColorCallback != null) {
            intColorCallback.run(intValue);
        }
        try {
            window.setNavigationBarColor(intValue);
        } catch (Exception unused) {
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:40:0x0050 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:51:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean checkHostForPunycode(java.lang.String r8) {
        /*
            r0 = 0
            if (r8 != 0) goto L_0x0004
            return r0
        L_0x0004:
            r1 = 1
            int r2 = r8.length()     // Catch:{ Exception -> 0x0048 }
            r3 = 0
            r4 = 0
            r5 = 0
        L_0x000c:
            if (r3 >= r2) goto L_0x004e
            char r6 = r8.charAt(r3)     // Catch:{ Exception -> 0x0046 }
            r7 = 46
            if (r6 == r7) goto L_0x0043
            r7 = 45
            if (r6 == r7) goto L_0x0043
            r7 = 47
            if (r6 == r7) goto L_0x0043
            r7 = 43
            if (r6 == r7) goto L_0x0043
            r7 = 48
            if (r6 < r7) goto L_0x002b
            r7 = 57
            if (r6 > r7) goto L_0x002b
            goto L_0x0043
        L_0x002b:
            r7 = 97
            if (r6 < r7) goto L_0x0033
            r7 = 122(0x7a, float:1.71E-43)
            if (r6 <= r7) goto L_0x003b
        L_0x0033:
            r7 = 65
            if (r6 < r7) goto L_0x003d
            r7 = 90
            if (r6 > r7) goto L_0x003d
        L_0x003b:
            r4 = 1
            goto L_0x003e
        L_0x003d:
            r5 = 1
        L_0x003e:
            if (r4 == 0) goto L_0x0043
            if (r5 == 0) goto L_0x0043
            goto L_0x004e
        L_0x0043:
            int r3 = r3 + 1
            goto L_0x000c
        L_0x0046:
            r8 = move-exception
            goto L_0x004b
        L_0x0048:
            r8 = move-exception
            r4 = 0
            r5 = 0
        L_0x004b:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x004e:
            if (r4 == 0) goto L_0x0053
            if (r5 == 0) goto L_0x0053
            r0 = 1
        L_0x0053:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.checkHostForPunycode(java.lang.String):boolean");
    }

    public static boolean shouldShowUrlInAlert(String str) {
        try {
            return checkHostForPunycode(Uri.parse(str).getHost());
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    public static void scrollToFragmentRow(ActionBarLayout actionBarLayout, String str) {
        if (actionBarLayout != null && str != null) {
            ArrayList<BaseFragment> arrayList = actionBarLayout.fragmentsStack;
            BaseFragment baseFragment = arrayList.get(arrayList.size() - 1);
            try {
                Field declaredField = baseFragment.getClass().getDeclaredField("listView");
                declaredField.setAccessible(true);
                RecyclerListView recyclerListView = (RecyclerListView) declaredField.get(baseFragment);
                recyclerListView.highlightRow(new AndroidUtilities$$ExternalSyntheticLambda14(baseFragment, str, recyclerListView));
                declaredField.setAccessible(false);
            } catch (Throwable unused) {
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$scrollToFragmentRow$13(BaseFragment baseFragment, String str, RecyclerListView recyclerListView) {
        int i = -1;
        try {
            Field declaredField = baseFragment.getClass().getDeclaredField(str);
            declaredField.setAccessible(true);
            i = declaredField.getInt(baseFragment);
            ((LinearLayoutManager) recyclerListView.getLayoutManager()).scrollToPositionWithOffset(i, dp(60.0f));
            declaredField.setAccessible(false);
            return i;
        } catch (Throwable unused) {
            return i;
        }
    }

    public static boolean checkInlinePermissions(Context context) {
        return Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(context);
    }

    public static void updateVisibleRows(RecyclerListView recyclerListView) {
        RecyclerView.Adapter adapter;
        RecyclerView.ViewHolder childViewHolder;
        if (recyclerListView != null && (adapter = recyclerListView.getAdapter()) != null) {
            for (int i = 0; i < recyclerListView.getChildCount(); i++) {
                View childAt = recyclerListView.getChildAt(i);
                int childAdapterPosition = recyclerListView.getChildAdapterPosition(childAt);
                if (childAdapterPosition >= 0 && (childViewHolder = recyclerListView.getChildViewHolder(childAt)) != null && !childViewHolder.shouldIgnore()) {
                    adapter.onBindViewHolder(childViewHolder, childAdapterPosition);
                }
            }
        }
    }

    public static void updateImageViewImageAnimated(ImageView imageView, int i) {
        updateImageViewImageAnimated(imageView, ContextCompat.getDrawable(imageView.getContext(), i));
    }

    public static void updateImageViewImageAnimated(ImageView imageView, Drawable drawable) {
        ValueAnimator duration = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(150);
        duration.addUpdateListener(new AndroidUtilities$$ExternalSyntheticLambda0(imageView, new AtomicBoolean(), drawable));
        duration.start();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateImageViewImageAnimated$14(ImageView imageView, AtomicBoolean atomicBoolean, Drawable drawable, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float abs = Math.abs(floatValue - 0.5f) + 0.5f;
        imageView.setScaleX(abs);
        imageView.setScaleY(abs);
        if (floatValue >= 0.5f && !atomicBoolean.get()) {
            atomicBoolean.set(true);
            imageView.setImageDrawable(drawable);
        }
    }

    public static void updateViewVisibilityAnimated(View view, boolean z) {
        updateViewVisibilityAnimated(view, z, 1.0f, true);
    }

    public static void updateViewVisibilityAnimated(View view, boolean z, float f, boolean z2) {
        if (view != null) {
            int i = 0;
            if (view.getParent() == null) {
                z2 = false;
            }
            int i2 = null;
            if (!z2) {
                view.animate().setListener((Animator.AnimatorListener) null).cancel();
                if (!z) {
                    i = 8;
                }
                view.setVisibility(i);
                if (z) {
                    i2 = 1;
                }
                view.setTag(i2);
                view.setAlpha(1.0f);
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
            } else if (z && view.getTag() == null) {
                view.animate().setListener((Animator.AnimatorListener) null).cancel();
                if (view.getVisibility() != 0) {
                    view.setVisibility(0);
                    view.setAlpha(0.0f);
                    view.setScaleX(f);
                    view.setScaleY(f);
                }
                view.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).setDuration(150).start();
                view.setTag(1);
            } else if (!z && view.getTag() != null) {
                view.animate().setListener((Animator.AnimatorListener) null).cancel();
                view.animate().alpha(0.0f).scaleY(f).scaleX(f).setListener(new HideViewAfterAnimation(view)).setDuration(150).start();
                view.setTag((Object) null);
            }
        }
    }

    public static long getPrefIntOrLong(SharedPreferences sharedPreferences, String str, long j) {
        try {
            return sharedPreferences.getLong(str, j);
        } catch (Exception unused) {
            return (long) sharedPreferences.getInt(str, (int) j);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:39:0x007c A[SYNTHETIC, Splitter:B:39:0x007c] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Bitmap getScaledBitmap(float r7, float r8, java.lang.String r9, java.lang.String r10, int r11) {
        /*
            r0 = 0
            android.graphics.BitmapFactory$Options r1 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0075 }
            r1.<init>()     // Catch:{ all -> 0x0075 }
            r2 = 1
            r1.inJustDecodeBounds = r2     // Catch:{ all -> 0x0075 }
            if (r9 == 0) goto L_0x0010
            android.graphics.BitmapFactory.decodeFile(r9, r1)     // Catch:{ all -> 0x0075 }
            r3 = r0
            goto L_0x0020
        L_0x0010:
            java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch:{ all -> 0x0075 }
            r3.<init>(r10)     // Catch:{ all -> 0x0075 }
            java.nio.channels.FileChannel r10 = r3.getChannel()     // Catch:{ all -> 0x0073 }
            long r4 = (long) r11     // Catch:{ all -> 0x0073 }
            r10.position(r4)     // Catch:{ all -> 0x0073 }
            android.graphics.BitmapFactory.decodeStream(r3, r0, r1)     // Catch:{ all -> 0x0073 }
        L_0x0020:
            int r10 = r1.outWidth     // Catch:{ all -> 0x0073 }
            if (r10 <= 0) goto L_0x006d
            int r4 = r1.outHeight     // Catch:{ all -> 0x0073 }
            if (r4 <= 0) goto L_0x006d
            int r5 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r5 <= 0) goto L_0x0031
            if (r10 >= r4) goto L_0x0031
            r6 = r8
            r8 = r7
            r7 = r6
        L_0x0031:
            float r10 = (float) r10     // Catch:{ all -> 0x0073 }
            float r10 = r10 / r7
            float r7 = (float) r4     // Catch:{ all -> 0x0073 }
            float r7 = r7 / r8
            float r7 = java.lang.Math.min(r10, r7)     // Catch:{ all -> 0x0073 }
            r1.inSampleSize = r2     // Catch:{ all -> 0x0073 }
            r8 = 1065353216(0x3var_, float:1.0)
            int r8 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r8 <= 0) goto L_0x004c
        L_0x0041:
            int r8 = r1.inSampleSize     // Catch:{ all -> 0x0073 }
            int r8 = r8 * 2
            r1.inSampleSize = r8     // Catch:{ all -> 0x0073 }
            float r8 = (float) r8     // Catch:{ all -> 0x0073 }
            int r8 = (r8 > r7 ? 1 : (r8 == r7 ? 0 : -1))
            if (r8 < 0) goto L_0x0041
        L_0x004c:
            r7 = 0
            r1.inJustDecodeBounds = r7     // Catch:{ all -> 0x0073 }
            if (r9 == 0) goto L_0x0056
            android.graphics.Bitmap r7 = android.graphics.BitmapFactory.decodeFile(r9, r1)     // Catch:{ all -> 0x0073 }
            goto L_0x0062
        L_0x0056:
            java.nio.channels.FileChannel r7 = r3.getChannel()     // Catch:{ all -> 0x0073 }
            long r8 = (long) r11     // Catch:{ all -> 0x0073 }
            r7.position(r8)     // Catch:{ all -> 0x0073 }
            android.graphics.Bitmap r7 = android.graphics.BitmapFactory.decodeStream(r3, r0, r1)     // Catch:{ all -> 0x0073 }
        L_0x0062:
            if (r3 == 0) goto L_0x006c
            r3.close()     // Catch:{ Exception -> 0x0068 }
            goto L_0x006c
        L_0x0068:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x006c:
            return r7
        L_0x006d:
            if (r3 == 0) goto L_0x0084
            r3.close()     // Catch:{ Exception -> 0x0080 }
            goto L_0x0084
        L_0x0073:
            r7 = move-exception
            goto L_0x0077
        L_0x0075:
            r7 = move-exception
            r3 = r0
        L_0x0077:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)     // Catch:{ all -> 0x0085 }
            if (r3 == 0) goto L_0x0084
            r3.close()     // Catch:{ Exception -> 0x0080 }
            goto L_0x0084
        L_0x0080:
            r7 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)
        L_0x0084:
            return r0
        L_0x0085:
            r7 = move-exception
            if (r3 == 0) goto L_0x0090
            r3.close()     // Catch:{ Exception -> 0x008c }
            goto L_0x0090
        L_0x008c:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x0090:
            goto L_0x0092
        L_0x0091:
            throw r7
        L_0x0092:
            goto L_0x0091
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.getScaledBitmap(float, float, java.lang.String, java.lang.String, int):android.graphics.Bitmap");
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x0049 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.net.Uri getBitmapShareUri(android.graphics.Bitmap r3, java.lang.String r4, android.graphics.Bitmap.CompressFormat r5) {
        /*
            java.io.File r0 = getCacheDir()
            boolean r1 = r0.isDirectory()
            r2 = 0
            if (r1 != 0) goto L_0x0014
            r0.mkdirs()     // Catch:{ Exception -> 0x000f }
            goto L_0x0014
        L_0x000f:
            r3 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            return r2
        L_0x0014:
            java.io.File r1 = new java.io.File
            r1.<init>(r0, r4)
            java.io.FileOutputStream r4 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x004a }
            r4.<init>(r1)     // Catch:{ Exception -> 0x004a }
            r0 = 100
            r3.compress(r5, r0, r4)     // Catch:{ all -> 0x0045 }
            r4.close()     // Catch:{ all -> 0x0045 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0045 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0045 }
            r5.<init>()     // Catch:{ all -> 0x0045 }
            java.lang.String r0 = org.telegram.messenger.ApplicationLoader.getApplicationId()     // Catch:{ all -> 0x0045 }
            r5.append(r0)     // Catch:{ all -> 0x0045 }
            java.lang.String r0 = ".provider"
            r5.append(r0)     // Catch:{ all -> 0x0045 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0045 }
            android.net.Uri r3 = androidx.core.content.FileProvider.getUriForFile(r3, r5, r1)     // Catch:{ all -> 0x0045 }
            r4.close()     // Catch:{ Exception -> 0x004a }
            return r3
        L_0x0045:
            r3 = move-exception
            r4.close()     // Catch:{ all -> 0x0049 }
        L_0x0049:
            throw r3     // Catch:{ Exception -> 0x004a }
        L_0x004a:
            r3 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AndroidUtilities.getBitmapShareUri(android.graphics.Bitmap, java.lang.String, android.graphics.Bitmap$CompressFormat):android.net.Uri");
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException unused) {
            return false;
        }
    }

    public static CharSequence trim(CharSequence charSequence, int[] iArr) {
        if (charSequence == null) {
            return null;
        }
        int length = charSequence.length();
        int i = 0;
        while (i < length && charSequence.charAt(i) <= ' ') {
            i++;
        }
        while (i < length && charSequence.charAt(length - 1) <= ' ') {
            length--;
        }
        if (iArr != null) {
            iArr[0] = i;
        }
        return (i > 0 || length < charSequence.length()) ? charSequence.subSequence(i, length) : charSequence;
    }
}
