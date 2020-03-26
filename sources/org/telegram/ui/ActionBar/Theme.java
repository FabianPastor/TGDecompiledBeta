package org.telegram.ui.ActionBar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Base64;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.StateSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.time.SunDate;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$BaseTheme;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$InputFile;
import org.telegram.tgnet.TLRPC$TL_account_getMultiWallPapers;
import org.telegram.tgnet.TLRPC$TL_account_getTheme;
import org.telegram.tgnet.TLRPC$TL_account_getThemes;
import org.telegram.tgnet.TLRPC$TL_account_getWallPaper;
import org.telegram.tgnet.TLRPC$TL_baseThemeArctic;
import org.telegram.tgnet.TLRPC$TL_baseThemeClassic;
import org.telegram.tgnet.TLRPC$TL_baseThemeDay;
import org.telegram.tgnet.TLRPC$TL_baseThemeNight;
import org.telegram.tgnet.TLRPC$TL_baseThemeTinted;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputTheme;
import org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug;
import org.telegram.tgnet.TLRPC$TL_theme;
import org.telegram.tgnet.TLRPC$TL_themeSettings;
import org.telegram.tgnet.TLRPC$TL_wallPaper;
import org.telegram.tgnet.TLRPC$TL_wallPaperNoFile;
import org.telegram.tgnet.TLRPC$Theme;
import org.telegram.tgnet.TLRPC$WallPaper;
import org.telegram.tgnet.TLRPC$WallPaperSettings;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ThemesHorizontalListCell;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.ScamDrawable;
import org.telegram.ui.Components.SvgHelper;

public class Theme {
    public static int DEFALT_THEME_ACCENT_ID = 99;
    private static Method StateListDrawable_getStateDrawableMethod = null;
    private static SensorEventListener ambientSensorListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        public void onSensorChanged(SensorEvent sensorEvent) {
            float f = sensorEvent.values[0];
            if (f <= 0.0f) {
                f = 0.1f;
            }
            if (!ApplicationLoader.mainInterfacePaused && ApplicationLoader.isScreenOn) {
                if (f > 500.0f) {
                    float unused = Theme.lastBrightnessValue = 1.0f;
                } else {
                    float unused2 = Theme.lastBrightnessValue = ((float) Math.ceil((Math.log((double) f) * 9.932299613952637d) + 27.05900001525879d)) / 100.0f;
                }
                if (Theme.lastBrightnessValue > Theme.autoNightBrighnessThreshold) {
                    if (Theme.switchNightRunnableScheduled) {
                        boolean unused3 = Theme.switchNightRunnableScheduled = false;
                        AndroidUtilities.cancelRunOnUIThread(Theme.switchNightBrightnessRunnable);
                    }
                    if (!Theme.switchDayRunnableScheduled) {
                        boolean unused4 = Theme.switchDayRunnableScheduled = true;
                        AndroidUtilities.runOnUIThread(Theme.switchDayBrightnessRunnable, Theme.getAutoNightSwitchThemeDelay());
                    }
                } else if (!MediaController.getInstance().isRecordingOrListeningByProximity()) {
                    if (Theme.switchDayRunnableScheduled) {
                        boolean unused5 = Theme.switchDayRunnableScheduled = false;
                        AndroidUtilities.cancelRunOnUIThread(Theme.switchDayBrightnessRunnable);
                    }
                    if (!Theme.switchNightRunnableScheduled) {
                        boolean unused6 = Theme.switchNightRunnableScheduled = true;
                        AndroidUtilities.runOnUIThread(Theme.switchNightBrightnessRunnable, Theme.getAutoNightSwitchThemeDelay());
                    }
                }
            }
        }
    };
    private static HashMap<String, Integer> animatingColors = null;
    public static float autoNightBrighnessThreshold = 0.0f;
    public static String autoNightCityName = null;
    public static int autoNightDayEndTime = 0;
    public static int autoNightDayStartTime = 0;
    public static int autoNightLastSunCheckDay = 0;
    public static double autoNightLocationLatitude = 0.0d;
    public static double autoNightLocationLongitude = 0.0d;
    public static boolean autoNightScheduleByLocation = false;
    public static int autoNightSunriseTime = 0;
    public static int autoNightSunsetTime = 0;
    public static Drawable[] avatarDrawables = new Drawable[10];
    public static Paint avatar_backgroundPaint = null;
    private static BackgroundGradientDrawable.Disposable backgroundGradientDisposable = null;
    public static Drawable calllog_msgCallDownGreenDrawable = null;
    public static Drawable calllog_msgCallDownRedDrawable = null;
    public static Drawable calllog_msgCallUpGreenDrawable = null;
    public static Drawable calllog_msgCallUpRedDrawable = null;
    private static boolean canStartHolidayAnimation = false;
    public static Paint chat_actionBackgroundPaint = null;
    public static TextPaint chat_actionTextPaint = null;
    public static TextPaint chat_adminPaint = null;
    public static Drawable[] chat_attachButtonDrawables = new Drawable[6];
    public static Drawable chat_attachEmptyDrawable = null;
    public static TextPaint chat_audioPerformerPaint = null;
    public static TextPaint chat_audioTimePaint = null;
    public static TextPaint chat_audioTitlePaint = null;
    public static TextPaint chat_botButtonPaint = null;
    public static Drawable chat_botInlineDrawable = null;
    public static Drawable chat_botLinkDrawalbe = null;
    public static Paint chat_botProgressPaint = null;
    public static Paint chat_composeBackgroundPaint = null;
    public static Drawable chat_composeShadowDrawable = null;
    public static Drawable[] chat_contactDrawable = new Drawable[2];
    public static TextPaint chat_contactNamePaint = null;
    public static TextPaint chat_contactPhonePaint = null;
    public static TextPaint chat_contextResult_descriptionTextPaint = null;
    public static Drawable chat_contextResult_shadowUnderSwitchDrawable = null;
    public static TextPaint chat_contextResult_titleTextPaint = null;
    public static Drawable[] chat_cornerInner = new Drawable[4];
    public static Drawable[] chat_cornerOuter = new Drawable[4];
    public static Paint chat_deleteProgressPaint = null;
    public static Paint chat_docBackPaint = null;
    public static TextPaint chat_docNamePaint = null;
    public static TextPaint chat_durationPaint = null;
    public static CombinedDrawable[][] chat_fileMiniStatesDrawable = ((CombinedDrawable[][]) Array.newInstance(CombinedDrawable.class, new int[]{6, 2}));
    public static Path[] chat_filePath = new Path[2];
    public static Drawable[][] chat_fileStatesDrawable = ((Drawable[][]) Array.newInstance(Drawable.class, new int[]{10, 2}));
    public static Drawable chat_flameIcon = null;
    public static TextPaint chat_forwardNamePaint = null;
    public static TextPaint chat_gamePaint = null;
    public static Drawable chat_gifIcon = null;
    public static Drawable chat_goIconDrawable = null;
    public static TextPaint chat_infoPaint = null;
    public static Drawable chat_inlineResultAudio = null;
    public static Drawable chat_inlineResultFile = null;
    public static Drawable chat_inlineResultLocation = null;
    public static TextPaint chat_instantViewPaint = null;
    public static Paint chat_instantViewRectPaint = null;
    public static TextPaint chat_livePaint = null;
    public static TextPaint chat_locationAddressPaint = null;
    public static Drawable[] chat_locationDrawable = new Drawable[2];
    public static TextPaint chat_locationTitlePaint = null;
    public static Drawable chat_lockIconDrawable = null;
    public static Drawable chat_msgAvatarLiveLocationDrawable = null;
    public static TextPaint chat_msgBotButtonPaint = null;
    public static Drawable chat_msgBroadcastDrawable = null;
    public static Drawable chat_msgBroadcastMediaDrawable = null;
    public static Drawable chat_msgCallDownGreenDrawable = null;
    public static Drawable chat_msgCallDownRedDrawable = null;
    public static Drawable chat_msgCallUpGreenDrawable = null;
    public static Drawable chat_msgErrorDrawable = null;
    public static Paint chat_msgErrorPaint = null;
    public static TextPaint chat_msgGameTextPaint = null;
    public static Drawable chat_msgInCallDrawable = null;
    public static Drawable chat_msgInCallSelectedDrawable = null;
    public static Drawable chat_msgInClockDrawable = null;
    public static MessageDrawable chat_msgInDrawable = null;
    public static Drawable chat_msgInInstantDrawable = null;
    public static MessageDrawable chat_msgInMediaDrawable = null;
    public static MessageDrawable chat_msgInMediaSelectedDrawable = null;
    public static Drawable chat_msgInMenuDrawable = null;
    public static Drawable chat_msgInMenuSelectedDrawable = null;
    public static Drawable chat_msgInSelectedClockDrawable = null;
    public static MessageDrawable chat_msgInSelectedDrawable = null;
    public static Drawable chat_msgInViewsDrawable = null;
    public static Drawable chat_msgInViewsSelectedDrawable = null;
    public static Drawable chat_msgMediaCheckDrawable = null;
    public static Drawable chat_msgMediaClockDrawable = null;
    public static Drawable chat_msgMediaHalfCheckDrawable = null;
    public static Drawable chat_msgMediaMenuDrawable = null;
    public static Drawable chat_msgMediaViewsDrawable = null;
    public static Drawable chat_msgNoSoundDrawable = null;
    public static Drawable chat_msgOutCallDrawable = null;
    public static Drawable chat_msgOutCallSelectedDrawable = null;
    public static Drawable chat_msgOutCheckDrawable = null;
    public static Drawable chat_msgOutCheckReadDrawable = null;
    public static Drawable chat_msgOutCheckReadSelectedDrawable = null;
    public static Drawable chat_msgOutCheckSelectedDrawable = null;
    public static Drawable chat_msgOutClockDrawable = null;
    public static MessageDrawable chat_msgOutDrawable = null;
    public static Drawable chat_msgOutHalfCheckDrawable = null;
    public static Drawable chat_msgOutHalfCheckSelectedDrawable = null;
    public static Drawable chat_msgOutInstantDrawable = null;
    public static MessageDrawable chat_msgOutMediaDrawable = null;
    public static MessageDrawable chat_msgOutMediaSelectedDrawable = null;
    public static Drawable chat_msgOutMenuDrawable = null;
    public static Drawable chat_msgOutMenuSelectedDrawable = null;
    public static Drawable chat_msgOutSelectedClockDrawable = null;
    public static MessageDrawable chat_msgOutSelectedDrawable = null;
    public static Drawable chat_msgOutViewsDrawable = null;
    public static Drawable chat_msgOutViewsSelectedDrawable = null;
    public static Drawable chat_msgStickerCheckDrawable = null;
    public static Drawable chat_msgStickerClockDrawable = null;
    public static Drawable chat_msgStickerHalfCheckDrawable = null;
    public static Drawable chat_msgStickerViewsDrawable = null;
    public static TextPaint chat_msgTextPaint = null;
    public static TextPaint chat_msgTextPaintOneEmoji = null;
    public static TextPaint chat_msgTextPaintThreeEmoji = null;
    public static TextPaint chat_msgTextPaintTwoEmoji = null;
    public static Drawable chat_muteIconDrawable = null;
    public static TextPaint chat_namePaint = null;
    public static Drawable[][] chat_photoStatesDrawables = ((Drawable[][]) Array.newInstance(Drawable.class, new int[]{13, 2}));
    public static Drawable[] chat_pollCheckDrawable = new Drawable[2];
    public static Drawable[] chat_pollCrossDrawable = new Drawable[2];
    public static Paint chat_radialProgress2Paint = null;
    public static Paint chat_radialProgressPaint = null;
    public static Drawable chat_redLocationIcon = null;
    public static Drawable chat_replyIconDrawable = null;
    public static Paint chat_replyLinePaint = null;
    public static TextPaint chat_replyNamePaint = null;
    public static TextPaint chat_replyTextPaint = null;
    public static Drawable chat_roundVideoShadow = null;
    public static Drawable chat_shareDrawable = null;
    public static Drawable chat_shareIconDrawable = null;
    public static TextPaint chat_shipmentPaint = null;
    public static Paint chat_statusPaint = null;
    public static Paint chat_statusRecordPaint = null;
    public static Drawable chat_systemDrawable = null;
    public static Paint chat_textSearchSelectionPaint = null;
    public static Paint chat_timeBackgroundPaint = null;
    public static TextPaint chat_timePaint = null;
    public static Paint chat_urlPaint = null;
    public static Paint checkboxSquare_backgroundPaint = null;
    public static Paint checkboxSquare_checkPaint = null;
    public static Paint checkboxSquare_eraserPaint = null;
    public static PorterDuffColorFilter colorFilter = null;
    public static PorterDuffColorFilter colorFilter2 = null;
    public static PorterDuffColorFilter colorPressedFilter = null;
    public static PorterDuffColorFilter colorPressedFilter2 = null;
    public static int currentColor = 0;
    /* access modifiers changed from: private */
    public static HashMap<String, Integer> currentColors = new HashMap<>();
    private static HashMap<String, Integer> currentColorsNoAccent = new HashMap<>();
    private static ThemeInfo currentDayTheme = null;
    /* access modifiers changed from: private */
    public static ThemeInfo currentNightTheme = null;
    private static int currentSelectedColor = 0;
    private static ColorFilter currentShareColorFilter = null;
    private static int currentShareColorFilterColor = 0;
    private static ColorFilter currentShareSelectedColorFilter = null;
    private static int currentShareSelectedColorFilterColor = 0;
    /* access modifiers changed from: private */
    public static ThemeInfo currentTheme = null;
    /* access modifiers changed from: private */
    public static HashMap<String, Integer> defaultColors = new HashMap<>();
    private static ThemeInfo defaultTheme = null;
    public static RLottieDrawable dialogs_archiveAvatarDrawable = null;
    public static boolean dialogs_archiveAvatarDrawableRecolored = false;
    public static RLottieDrawable dialogs_archiveDrawable = null;
    public static boolean dialogs_archiveDrawableRecolored = false;
    public static TextPaint dialogs_archiveTextPaint = null;
    public static Drawable dialogs_botDrawable = null;
    public static Drawable dialogs_broadcastDrawable = null;
    public static Drawable dialogs_checkDrawable = null;
    public static Drawable dialogs_checkReadDrawable = null;
    public static Drawable dialogs_clockDrawable = null;
    public static Paint dialogs_countGrayPaint = null;
    public static Paint dialogs_countPaint = null;
    public static TextPaint dialogs_countTextPaint = null;
    public static Drawable dialogs_errorDrawable = null;
    public static Paint dialogs_errorPaint = null;
    public static Drawable dialogs_groupDrawable = null;
    public static Drawable dialogs_halfCheckDrawable = null;
    public static Drawable dialogs_holidayDrawable = null;
    private static int dialogs_holidayDrawableOffsetX = 0;
    private static int dialogs_holidayDrawableOffsetY = 0;
    public static Drawable dialogs_lockDrawable = null;
    public static Drawable dialogs_mentionDrawable = null;
    public static TextPaint dialogs_messageNamePaint = null;
    public static TextPaint[] dialogs_messagePaint = null;
    public static TextPaint[] dialogs_messagePrintingPaint = null;
    public static Drawable dialogs_muteDrawable = null;
    public static TextPaint[] dialogs_nameEncryptedPaint = null;
    public static TextPaint[] dialogs_namePaint = null;
    public static TextPaint dialogs_offlinePaint = null;
    public static Paint dialogs_onlineCirclePaint = null;
    public static TextPaint dialogs_onlinePaint = null;
    public static RLottieDrawable dialogs_pinArchiveDrawable = null;
    public static Drawable dialogs_pinnedDrawable = null;
    public static Paint dialogs_pinnedPaint = null;
    public static Drawable dialogs_reorderDrawable = null;
    public static ScamDrawable dialogs_scamDrawable = null;
    public static TextPaint dialogs_searchNameEncryptedPaint = null;
    public static TextPaint dialogs_searchNamePaint = null;
    public static Paint dialogs_tabletSeletedPaint = null;
    public static TextPaint dialogs_timePaint = null;
    public static RLottieDrawable dialogs_unarchiveDrawable = null;
    public static RLottieDrawable dialogs_unpinArchiveDrawable = null;
    public static Drawable dialogs_verifiedCheckDrawable = null;
    public static Drawable dialogs_verifiedDrawable = null;
    public static Paint dividerPaint = null;
    /* access modifiers changed from: private */
    public static HashMap<String, String> fallbackKeys = new HashMap<>();
    private static boolean hasPreviousTheme = false;
    private static ThreadLocal<float[]> hsvTemp1Local = new ThreadLocal<>();
    private static ThreadLocal<float[]> hsvTemp2Local = new ThreadLocal<>();
    private static ThreadLocal<float[]> hsvTemp3Local = new ThreadLocal<>();
    private static ThreadLocal<float[]> hsvTemp4Local = new ThreadLocal<>();
    private static ThreadLocal<float[]> hsvTemp5Local = new ThreadLocal<>();
    private static boolean isApplyingAccent = false;
    private static boolean isCustomTheme = false;
    private static boolean isInNigthMode = false;
    private static boolean isPatternWallpaper = false;
    private static boolean isWallpaperMotion = false;
    public static String[] keys_avatar_background = {"avatar_backgroundRed", "avatar_backgroundOrange", "avatar_backgroundViolet", "avatar_backgroundGreen", "avatar_backgroundCyan", "avatar_backgroundBlue", "avatar_backgroundPink"};
    public static String[] keys_avatar_nameInMessage = {"avatar_nameInMessageRed", "avatar_nameInMessageOrange", "avatar_nameInMessageViolet", "avatar_nameInMessageGreen", "avatar_nameInMessageCyan", "avatar_nameInMessageBlue", "avatar_nameInMessagePink"};
    /* access modifiers changed from: private */
    public static float lastBrightnessValue = 1.0f;
    private static long lastDelayUpdateTime;
    private static long lastHolidayCheckTime;
    private static int lastLoadingCurrentThemeTime;
    private static int[] lastLoadingThemesTime = new int[3];
    private static long lastThemeSwitchTime;
    private static Sensor lightSensor;
    private static boolean lightSensorRegistered;
    public static Paint linkSelectionPaint;
    private static int loadingCurrentTheme;
    private static boolean[] loadingRemoteThemes = new boolean[3];
    /* access modifiers changed from: private */
    public static Paint maskPaint = new Paint(1);
    public static Drawable moveUpDrawable;
    /* access modifiers changed from: private */
    public static HashSet<String> myMessagesColorKeys = new HashSet<>();
    private static ArrayList<ThemeInfo> otherThemes = new ArrayList<>();
    /* access modifiers changed from: private */
    public static ThemeInfo previousTheme;
    public static TextPaint profile_aboutTextPaint;
    public static Drawable profile_verifiedCheckDrawable;
    public static Drawable profile_verifiedDrawable;
    private static int[] remoteThemesHash = new int[3];
    public static int selectedAutoNightType;
    private static SensorManager sensorManager;
    private static int serviceMessage2Color;
    private static int serviceMessageColor;
    public static int serviceMessageColorBackup;
    private static int serviceSelectedMessage2Color;
    private static int serviceSelectedMessageColor;
    public static int serviceSelectedMessageColorBackup;
    /* access modifiers changed from: private */
    public static boolean shouldDrawGradientIcons;
    /* access modifiers changed from: private */
    public static Runnable switchDayBrightnessRunnable = new Runnable() {
        public void run() {
            boolean unused = Theme.switchDayRunnableScheduled = false;
            Theme.applyDayNightThemeMaybe(false);
        }
    };
    /* access modifiers changed from: private */
    public static boolean switchDayRunnableScheduled;
    /* access modifiers changed from: private */
    public static Runnable switchNightBrightnessRunnable = new Runnable() {
        public void run() {
            boolean unused = Theme.switchNightRunnableScheduled = false;
            Theme.applyDayNightThemeMaybe(true);
        }
    };
    /* access modifiers changed from: private */
    public static boolean switchNightRunnableScheduled;
    private static int switchNightThemeDelay;
    private static boolean switchingNightTheme;
    private static final Object sync = new Object();
    /* access modifiers changed from: private */
    public static HashSet<String> themeAccentExclusionKeys = new HashSet<>();
    private static Drawable themedWallpaper;
    private static int themedWallpaperFileOffset;
    private static String themedWallpaperLink;
    public static ArrayList<ThemeInfo> themes = new ArrayList<>();
    /* access modifiers changed from: private */
    public static HashMap<String, ThemeInfo> themesDict = new HashMap<>();
    private static Drawable wallpaper;
    private static final Object wallpaperSync = new Object();

    public static class MessageDrawable extends Drawable {
        private Drawable[][] backgroundDrawable = ((Drawable[][]) Array.newInstance(Drawable.class, new int[]{2, 4}));
        private int[][] backgroundDrawableColor = {new int[]{-1, -1, -1, -1}, new int[]{-1, -1, -1, -1}};
        private Rect backupRect = new Rect();
        private int[][] currentBackgroundDrawableRadius = {new int[]{-1, -1, -1, -1}, new int[]{-1, -1, -1, -1}};
        private int currentBackgroundHeight;
        private int currentColor;
        private int currentGradientColor;
        private int[] currentShadowDrawableRadius = {-1, -1, -1, -1};
        private int currentType;
        private LinearGradient gradientShader;
        private boolean isBottomNear;
        private boolean isOut;
        private boolean isSelected;
        private boolean isTopNear;
        private Matrix matrix = new Matrix();
        private Paint paint = new Paint(1);
        private Path path;
        private RectF rect = new RectF();
        private Paint selectedPaint;
        private Drawable[] shadowDrawable = new Drawable[4];
        private int[] shadowDrawableColor = {-1, -1, -1, -1};
        private int topY;

        public int getOpacity() {
            return -2;
        }

        public void setColorFilter(int i, PorterDuff.Mode mode) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public MessageDrawable(int i, boolean z, boolean z2) {
            this.isOut = z;
            this.currentType = i;
            this.isSelected = z2;
            this.path = new Path();
            this.selectedPaint = new Paint(1);
        }

        public boolean hasGradient() {
            return this.gradientShader != null && Theme.shouldDrawGradientIcons;
        }

        public LinearGradient getGradientShader() {
            return this.gradientShader;
        }

        public Matrix getMatrix() {
            return this.matrix;
        }

        /* access modifiers changed from: protected */
        public int getColor(String str) {
            return Theme.getColor(str);
        }

        /* access modifiers changed from: protected */
        public Integer getCurrentColor(String str) {
            return (Integer) Theme.currentColors.get(str);
        }

        public void setTop(int i, int i2, boolean z, boolean z2) {
            Integer num;
            int i3;
            String str;
            int i4 = i2;
            if (this.isOut) {
                if (this.isSelected) {
                    str = "chat_outBubbleSelected";
                } else {
                    str = "chat_outBubble";
                }
                i3 = getColor(str);
                num = getCurrentColor("chat_outBubbleGradient");
            } else {
                i3 = getColor(this.isSelected ? "chat_inBubbleSelected" : "chat_inBubble");
                num = null;
            }
            if (num != null) {
                i3 = getColor("chat_outBubble");
            }
            if (num == null) {
                num = 0;
            }
            if (num.intValue() != 0 && (this.gradientShader == null || i4 != this.currentBackgroundHeight || this.currentColor != i3 || this.currentGradientColor != num.intValue())) {
                LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, (float) i4, new int[]{num.intValue(), i3}, (float[]) null, Shader.TileMode.CLAMP);
                this.gradientShader = linearGradient;
                this.paint.setShader(linearGradient);
                this.currentColor = i3;
                this.currentGradientColor = num.intValue();
                this.paint.setColor(-1);
            } else if (num.intValue() == 0) {
                if (this.gradientShader != null) {
                    this.gradientShader = null;
                    this.paint.setShader((Shader) null);
                }
                this.paint.setColor(i3);
            }
            this.currentBackgroundHeight = i4;
            this.topY = i;
            this.isTopNear = z;
            this.isBottomNear = z2;
        }

        private int dp(float f) {
            if (this.currentType == 2) {
                return (int) Math.ceil((double) (f * 3.0f));
            }
            return AndroidUtilities.dp(f);
        }

        public Paint getPaint() {
            return this.paint;
        }

        public Drawable[] getShadowDrawables() {
            return this.shadowDrawable;
        }

        public Drawable getBackgroundDrawable() {
            char c;
            int i;
            int dp = AndroidUtilities.dp((float) SharedConfig.bubbleRadius);
            boolean z = false;
            if (this.isTopNear && this.isBottomNear) {
                c = 3;
            } else if (this.isTopNear) {
                c = 2;
            } else {
                c = this.isBottomNear ? (char) 1 : 0;
            }
            char c2 = this.isSelected;
            int[][] iArr = this.currentBackgroundDrawableRadius;
            if (iArr[c2][c] != dp) {
                iArr[c2][c] = dp;
                try {
                    Bitmap createBitmap = Bitmap.createBitmap(dp(50.0f), dp(40.0f), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(createBitmap);
                    Paint paint2 = new Paint(1);
                    paint2.setColor(-1);
                    this.backupRect.set(getBounds());
                    setBounds(0, 0, createBitmap.getWidth(), createBitmap.getHeight());
                    draw(canvas, paint2);
                    this.backgroundDrawable[c2][c] = new NinePatchDrawable(createBitmap, getByteBuffer((createBitmap.getWidth() / 2) - 1, (createBitmap.getWidth() / 2) + 1, (createBitmap.getHeight() / 2) - 1, (createBitmap.getHeight() / 2) + 1).array(), new Rect(), (String) null);
                    try {
                        setBounds(this.backupRect);
                    } catch (Throwable unused) {
                    }
                    z = true;
                } catch (Throwable unused2) {
                }
            }
            if (this.isSelected) {
                i = getColor(this.isOut ? "chat_outBubbleSelected" : "chat_inBubbleSelected");
            } else {
                i = getColor(this.isOut ? "chat_outBubble" : "chat_inBubble");
            }
            if (this.backgroundDrawable[c2][c] != null && (this.backgroundDrawableColor[c2][c] != i || z)) {
                this.backgroundDrawable[c2][c].setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                this.backgroundDrawableColor[c2][c] = i;
            }
            return this.backgroundDrawable[c2][c];
        }

        public Drawable getShadowDrawable() {
            char c;
            int dp = AndroidUtilities.dp((float) SharedConfig.bubbleRadius);
            boolean z = false;
            if (this.isTopNear && this.isBottomNear) {
                c = 3;
            } else if (this.isTopNear) {
                c = 2;
            } else {
                c = this.isBottomNear ? (char) 1 : 0;
            }
            int[] iArr = this.currentShadowDrawableRadius;
            if (iArr[c] != dp) {
                iArr[c] = dp;
                try {
                    Bitmap createBitmap = Bitmap.createBitmap(dp(50.0f), dp(40.0f), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(createBitmap);
                    Paint paint2 = new Paint(1);
                    LinearGradient linearGradient = r9;
                    LinearGradient linearGradient2 = new LinearGradient(0.0f, 0.0f, 0.0f, (float) dp(40.0f), new int[]{NUM, NUM}, (float[]) null, Shader.TileMode.CLAMP);
                    paint2.setShader(linearGradient);
                    paint2.setShadowLayer(2.0f, 0.0f, 1.0f, -1);
                    if (AndroidUtilities.density > 1.0f) {
                        setBounds(-1, -1, createBitmap.getWidth() + 1, createBitmap.getHeight() + 1);
                    } else {
                        setBounds(0, 0, createBitmap.getWidth(), createBitmap.getHeight());
                    }
                    draw(canvas, paint2);
                    if (AndroidUtilities.density > 1.0f) {
                        paint2.setColor(0);
                        paint2.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
                        paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                        setBounds(0, 0, createBitmap.getWidth(), createBitmap.getHeight());
                        draw(canvas, paint2);
                    }
                    this.shadowDrawable[c] = new NinePatchDrawable(createBitmap, getByteBuffer((createBitmap.getWidth() / 2) - 1, (createBitmap.getWidth() / 2) + 1, (createBitmap.getHeight() / 2) - 1, (createBitmap.getHeight() / 2) + 1).array(), new Rect(), (String) null);
                    z = true;
                } catch (Throwable unused) {
                }
            }
            int color = getColor(this.isOut ? "chat_outBubbleShadow" : "chat_inBubbleShadow");
            if (this.shadowDrawable[c] != null && (this.shadowDrawableColor[c] != color || z)) {
                this.shadowDrawable[c].setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                this.shadowDrawableColor[c] = color;
            }
            return this.shadowDrawable[c];
        }

        private static ByteBuffer getByteBuffer(int i, int i2, int i3, int i4) {
            ByteBuffer order = ByteBuffer.allocate(84).order(ByteOrder.nativeOrder());
            order.put((byte) 1);
            order.put((byte) 2);
            order.put((byte) 2);
            order.put((byte) 9);
            order.putInt(0);
            order.putInt(0);
            order.putInt(0);
            order.putInt(0);
            order.putInt(0);
            order.putInt(0);
            order.putInt(0);
            order.putInt(i);
            order.putInt(i2);
            order.putInt(i3);
            order.putInt(i4);
            order.putInt(1);
            order.putInt(1);
            order.putInt(1);
            order.putInt(1);
            order.putInt(1);
            order.putInt(1);
            order.putInt(1);
            order.putInt(1);
            order.putInt(1);
            return order;
        }

        public void draw(Canvas canvas) {
            draw(canvas, (Paint) null);
        }

        /* JADX WARNING: Removed duplicated region for block: B:102:0x0489  */
        /* JADX WARNING: Removed duplicated region for block: B:112:0x04de  */
        /* JADX WARNING: Removed duplicated region for block: B:54:0x0215  */
        /* JADX WARNING: Removed duplicated region for block: B:64:0x0269  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void draw(android.graphics.Canvas r20, android.graphics.Paint r21) {
            /*
                r19 = this;
                r0 = r19
                r1 = r20
                android.graphics.Rect r2 = r19.getBounds()
                if (r21 != 0) goto L_0x001b
                android.graphics.LinearGradient r3 = r0.gradientShader
                if (r3 != 0) goto L_0x001b
                android.graphics.drawable.Drawable r3 = r19.getBackgroundDrawable()
                if (r3 == 0) goto L_0x001b
                r3.setBounds(r2)
                r3.draw(r1)
                return
            L_0x001b:
                r3 = 1073741824(0x40000000, float:2.0)
                int r4 = r0.dp(r3)
                int r5 = r0.currentType
                r6 = 1086324736(0x40CLASSNAME, float:6.0)
                r7 = 2
                if (r5 != r7) goto L_0x0031
                int r5 = r0.dp(r6)
                int r8 = r0.dp(r6)
                goto L_0x0044
            L_0x0031:
                int r5 = org.telegram.messenger.SharedConfig.bubbleRadius
                float r5 = (float) r5
                int r5 = r0.dp(r5)
                r8 = 5
                int r9 = org.telegram.messenger.SharedConfig.bubbleRadius
                int r8 = java.lang.Math.min(r8, r9)
                float r8 = (float) r8
                int r8 = r0.dp(r8)
            L_0x0044:
                int r6 = r0.dp(r6)
                if (r21 != 0) goto L_0x004d
                android.graphics.Paint r9 = r0.paint
                goto L_0x004f
            L_0x004d:
                r9 = r21
            L_0x004f:
                r10 = 0
                if (r21 != 0) goto L_0x006b
                android.graphics.LinearGradient r11 = r0.gradientShader
                if (r11 == 0) goto L_0x006b
                android.graphics.Matrix r11 = r0.matrix
                r11.reset()
                android.graphics.Matrix r11 = r0.matrix
                int r12 = r0.topY
                int r12 = -r12
                float r12 = (float) r12
                r11.postTranslate(r10, r12)
                android.graphics.LinearGradient r11 = r0.gradientShader
                android.graphics.Matrix r12 = r0.matrix
                r11.setLocalMatrix(r12)
            L_0x006b:
                int r11 = r2.top
                r12 = 0
                int r11 = java.lang.Math.max(r11, r12)
                android.graphics.Path r13 = r0.path
                r13.reset()
                boolean r13 = r0.isOut
                r14 = 1076258406(0x40266666, float:2.6)
                r15 = 1119092736(0x42b40000, float:90.0)
                r10 = 1
                r3 = 1090519040(0x41000000, float:8.0)
                if (r13 == 0) goto L_0x02f0
                int r13 = r0.currentType
                if (r13 == r7) goto L_0x00bb
                if (r21 != 0) goto L_0x00bb
                int r13 = r0.topY
                int r7 = r2.bottom
                int r13 = r13 + r7
                int r13 = r13 - r5
                int r7 = r0.currentBackgroundHeight
                if (r13 >= r7) goto L_0x0094
                goto L_0x00bb
            L_0x0094:
                android.graphics.Path r7 = r0.path
                int r13 = r2.right
                int r14 = r0.dp(r3)
                int r13 = r13 - r14
                float r13 = (float) r13
                int r14 = r0.topY
                int r14 = r11 - r14
                int r12 = r0.currentBackgroundHeight
                int r14 = r14 + r12
                float r12 = (float) r14
                r7.moveTo(r13, r12)
                android.graphics.Path r7 = r0.path
                int r12 = r2.left
                int r12 = r12 + r4
                float r12 = (float) r12
                int r13 = r0.topY
                int r13 = r11 - r13
                int r14 = r0.currentBackgroundHeight
                int r13 = r13 + r14
                float r13 = (float) r13
                r7.lineTo(r12, r13)
                goto L_0x0112
            L_0x00bb:
                int r7 = r0.currentType
                if (r7 != r10) goto L_0x00d2
                android.graphics.Path r7 = r0.path
                int r12 = r2.right
                int r13 = r0.dp(r3)
                int r12 = r12 - r13
                int r12 = r12 - r5
                float r12 = (float) r12
                int r13 = r2.bottom
                int r13 = r13 - r4
                float r13 = (float) r13
                r7.moveTo(r12, r13)
                goto L_0x00e3
            L_0x00d2:
                android.graphics.Path r7 = r0.path
                int r12 = r2.right
                int r13 = r0.dp(r14)
                int r12 = r12 - r13
                float r12 = (float) r12
                int r13 = r2.bottom
                int r13 = r13 - r4
                float r13 = (float) r13
                r7.moveTo(r12, r13)
            L_0x00e3:
                android.graphics.Path r7 = r0.path
                int r12 = r2.left
                int r12 = r12 + r4
                int r12 = r12 + r5
                float r12 = (float) r12
                int r13 = r2.bottom
                int r13 = r13 - r4
                float r13 = (float) r13
                r7.lineTo(r12, r13)
                android.graphics.RectF r7 = r0.rect
                int r12 = r2.left
                int r13 = r12 + r4
                float r13 = (float) r13
                int r14 = r2.bottom
                int r17 = r14 - r4
                int r18 = r5 * 2
                int r3 = r17 - r18
                float r3 = (float) r3
                int r12 = r12 + r4
                int r12 = r12 + r18
                float r12 = (float) r12
                int r14 = r14 - r4
                float r14 = (float) r14
                r7.set(r13, r3, r12, r14)
                android.graphics.Path r3 = r0.path
                android.graphics.RectF r7 = r0.rect
                r12 = 0
                r3.arcTo(r7, r15, r15, r12)
            L_0x0112:
                int r3 = r0.currentType
                r7 = 2
                if (r3 == r7) goto L_0x016c
                if (r21 != 0) goto L_0x016c
                int r3 = r0.topY
                int r7 = r5 * 2
                int r7 = r7 + r3
                if (r7 < 0) goto L_0x0121
                goto L_0x016c
            L_0x0121:
                android.graphics.Path r7 = r0.path
                int r12 = r2.left
                int r12 = r12 + r4
                float r12 = (float) r12
                int r3 = r11 - r3
                r13 = 1073741824(0x40000000, float:2.0)
                int r14 = r0.dp(r13)
                int r3 = r3 - r14
                float r3 = (float) r3
                r7.lineTo(r12, r3)
                int r3 = r0.currentType
                if (r3 != r10) goto L_0x014f
                android.graphics.Path r3 = r0.path
                int r7 = r2.right
                int r7 = r7 - r4
                float r7 = (float) r7
                int r12 = r0.topY
                int r12 = r11 - r12
                r13 = 1073741824(0x40000000, float:2.0)
                int r13 = r0.dp(r13)
                int r12 = r12 - r13
                float r12 = (float) r12
                r3.lineTo(r7, r12)
                goto L_0x0210
            L_0x014f:
                android.graphics.Path r3 = r0.path
                int r7 = r2.right
                r12 = 1090519040(0x41000000, float:8.0)
                int r13 = r0.dp(r12)
                int r7 = r7 - r13
                float r7 = (float) r7
                int r12 = r0.topY
                int r12 = r11 - r12
                r13 = 1073741824(0x40000000, float:2.0)
                int r13 = r0.dp(r13)
                int r12 = r12 - r13
                float r12 = (float) r12
                r3.lineTo(r7, r12)
                goto L_0x0210
            L_0x016c:
                android.graphics.Path r3 = r0.path
                int r7 = r2.left
                int r7 = r7 + r4
                float r7 = (float) r7
                int r12 = r2.top
                int r12 = r12 + r4
                int r12 = r12 + r5
                float r12 = (float) r12
                r3.lineTo(r7, r12)
                android.graphics.RectF r3 = r0.rect
                int r7 = r2.left
                int r12 = r7 + r4
                float r12 = (float) r12
                int r13 = r2.top
                int r14 = r13 + r4
                float r14 = (float) r14
                int r7 = r7 + r4
                int r16 = r5 * 2
                int r7 = r7 + r16
                float r7 = (float) r7
                int r13 = r13 + r4
                int r13 = r13 + r16
                float r13 = (float) r13
                r3.set(r12, r14, r7, r13)
                android.graphics.Path r3 = r0.path
                android.graphics.RectF r7 = r0.rect
                r12 = 1127481344(0x43340000, float:180.0)
                r13 = 0
                r3.arcTo(r7, r12, r15, r13)
                boolean r3 = r0.isTopNear
                if (r3 == 0) goto L_0x01a3
                r3 = r8
                goto L_0x01a4
            L_0x01a3:
                r3 = r5
            L_0x01a4:
                int r7 = r0.currentType
                if (r7 != r10) goto L_0x01cf
                android.graphics.Path r7 = r0.path
                int r12 = r2.right
                int r12 = r12 - r4
                int r12 = r12 - r3
                float r12 = (float) r12
                int r13 = r2.top
                int r13 = r13 + r4
                float r13 = (float) r13
                r7.lineTo(r12, r13)
                android.graphics.RectF r7 = r0.rect
                int r12 = r2.right
                int r13 = r12 - r4
                r14 = 2
                int r3 = r3 * 2
                int r13 = r13 - r3
                float r13 = (float) r13
                int r14 = r2.top
                int r10 = r14 + r4
                float r10 = (float) r10
                int r12 = r12 - r4
                float r12 = (float) r12
                int r14 = r14 + r4
                int r14 = r14 + r3
                float r3 = (float) r14
                r7.set(r13, r10, r12, r3)
                goto L_0x0206
            L_0x01cf:
                android.graphics.Path r7 = r0.path
                int r10 = r2.right
                r12 = 1090519040(0x41000000, float:8.0)
                int r13 = r0.dp(r12)
                int r10 = r10 - r13
                int r10 = r10 - r3
                float r10 = (float) r10
                int r13 = r2.top
                int r13 = r13 + r4
                float r13 = (float) r13
                r7.lineTo(r10, r13)
                android.graphics.RectF r7 = r0.rect
                int r10 = r2.right
                int r13 = r0.dp(r12)
                int r10 = r10 - r13
                r13 = 2
                int r3 = r3 * 2
                int r10 = r10 - r3
                float r10 = (float) r10
                int r13 = r2.top
                int r13 = r13 + r4
                float r13 = (float) r13
                int r14 = r2.right
                int r16 = r0.dp(r12)
                int r14 = r14 - r16
                float r12 = (float) r14
                int r14 = r2.top
                int r14 = r14 + r4
                int r14 = r14 + r3
                float r3 = (float) r14
                r7.set(r10, r13, r12, r3)
            L_0x0206:
                android.graphics.Path r3 = r0.path
                android.graphics.RectF r7 = r0.rect
                r10 = 1132920832(0x43870000, float:270.0)
                r12 = 0
                r3.arcTo(r7, r10, r15, r12)
            L_0x0210:
                int r3 = r0.currentType
                r7 = 1
                if (r3 != r7) goto L_0x0269
                r7 = 2
                if (r3 == r7) goto L_0x0233
                if (r21 != 0) goto L_0x0233
                int r3 = r0.topY
                int r6 = r2.bottom
                int r6 = r6 + r3
                int r6 = r6 - r5
                int r7 = r0.currentBackgroundHeight
                if (r6 >= r7) goto L_0x0225
                goto L_0x0233
            L_0x0225:
                android.graphics.Path r5 = r0.path
                int r2 = r2.right
                int r2 = r2 - r4
                float r2 = (float) r2
                int r11 = r11 - r3
                int r11 = r11 + r7
                float r3 = (float) r11
                r5.lineTo(r2, r3)
                goto L_0x0561
            L_0x0233:
                boolean r3 = r0.isBottomNear
                if (r3 == 0) goto L_0x0238
                r5 = r8
            L_0x0238:
                android.graphics.Path r3 = r0.path
                int r6 = r2.right
                int r6 = r6 - r4
                float r6 = (float) r6
                int r7 = r2.bottom
                int r7 = r7 - r4
                int r7 = r7 - r5
                float r7 = (float) r7
                r3.lineTo(r6, r7)
                android.graphics.RectF r3 = r0.rect
                int r6 = r2.right
                int r7 = r6 - r4
                r8 = 2
                int r5 = r5 * 2
                int r7 = r7 - r5
                float r7 = (float) r7
                int r2 = r2.bottom
                int r8 = r2 - r4
                int r8 = r8 - r5
                float r5 = (float) r8
                int r6 = r6 - r4
                float r6 = (float) r6
                int r2 = r2 - r4
                float r2 = (float) r2
                r3.set(r7, r5, r6, r2)
                android.graphics.Path r2 = r0.path
                android.graphics.RectF r3 = r0.rect
                r4 = 0
                r5 = 0
                r2.arcTo(r3, r4, r15, r5)
                goto L_0x0561
            L_0x0269:
                r5 = 2
                if (r3 == r5) goto L_0x0293
                if (r21 != 0) goto L_0x0293
                int r3 = r0.topY
                int r5 = r2.bottom
                int r3 = r3 + r5
                int r5 = r6 * 2
                int r3 = r3 - r5
                int r5 = r0.currentBackgroundHeight
                if (r3 >= r5) goto L_0x027b
                goto L_0x0293
            L_0x027b:
                android.graphics.Path r3 = r0.path
                int r2 = r2.right
                r4 = 1090519040(0x41000000, float:8.0)
                int r4 = r0.dp(r4)
                int r2 = r2 - r4
                float r2 = (float) r2
                int r4 = r0.topY
                int r11 = r11 - r4
                int r4 = r0.currentBackgroundHeight
                int r11 = r11 + r4
                float r4 = (float) r11
                r3.lineTo(r2, r4)
                goto L_0x0561
            L_0x0293:
                android.graphics.Path r3 = r0.path
                int r5 = r2.right
                r7 = 1090519040(0x41000000, float:8.0)
                int r8 = r0.dp(r7)
                int r5 = r5 - r8
                float r5 = (float) r5
                int r7 = r2.bottom
                int r7 = r7 - r4
                int r7 = r7 - r6
                r8 = 1077936128(0x40400000, float:3.0)
                int r8 = r0.dp(r8)
                int r7 = r7 - r8
                float r7 = (float) r7
                r3.lineTo(r5, r7)
                android.graphics.RectF r3 = r0.rect
                int r5 = r2.right
                r7 = 1090519040(0x41000000, float:8.0)
                int r7 = r0.dp(r7)
                int r5 = r5 - r7
                float r5 = (float) r5
                int r7 = r2.bottom
                int r7 = r7 - r4
                r8 = 2
                int r6 = r6 * 2
                int r7 = r7 - r6
                r8 = 1091567616(0x41100000, float:9.0)
                int r8 = r0.dp(r8)
                int r7 = r7 - r8
                float r7 = (float) r7
                int r8 = r2.right
                r10 = 1088421888(0x40e00000, float:7.0)
                int r10 = r0.dp(r10)
                int r8 = r8 - r10
                int r8 = r8 + r6
                float r6 = (float) r8
                int r2 = r2.bottom
                int r2 = r2 - r4
                r4 = 1065353216(0x3var_, float:1.0)
                int r4 = r0.dp(r4)
                int r2 = r2 - r4
                float r2 = (float) r2
                r3.set(r5, r7, r6, r2)
                android.graphics.Path r2 = r0.path
                android.graphics.RectF r3 = r0.rect
                r4 = -1029308416(0xffffffffc2a60000, float:-83.0)
                r5 = 1127481344(0x43340000, float:180.0)
                r6 = 0
                r2.arcTo(r3, r5, r4, r6)
                goto L_0x0561
            L_0x02f0:
                int r3 = r0.currentType
                r7 = -1028390912(0xffffffffc2b40000, float:-90.0)
                r10 = 2
                if (r3 == r10) goto L_0x032d
                if (r21 != 0) goto L_0x032d
                int r3 = r0.topY
                int r10 = r2.bottom
                int r3 = r3 + r10
                int r3 = r3 - r5
                int r10 = r0.currentBackgroundHeight
                if (r3 >= r10) goto L_0x0304
                goto L_0x032d
            L_0x0304:
                android.graphics.Path r3 = r0.path
                int r10 = r2.left
                r12 = 1090519040(0x41000000, float:8.0)
                int r13 = r0.dp(r12)
                int r10 = r10 + r13
                float r10 = (float) r10
                int r12 = r0.topY
                int r12 = r11 - r12
                int r13 = r0.currentBackgroundHeight
                int r12 = r12 + r13
                float r12 = (float) r12
                r3.moveTo(r10, r12)
                android.graphics.Path r3 = r0.path
                int r10 = r2.right
                int r10 = r10 - r4
                float r10 = (float) r10
                int r12 = r0.topY
                int r12 = r11 - r12
                int r13 = r0.currentBackgroundHeight
                int r12 = r12 + r13
                float r12 = (float) r12
                r3.lineTo(r10, r12)
                goto L_0x0386
            L_0x032d:
                int r3 = r0.currentType
                r10 = 1
                if (r3 != r10) goto L_0x0347
                android.graphics.Path r3 = r0.path
                int r10 = r2.left
                r12 = 1090519040(0x41000000, float:8.0)
                int r13 = r0.dp(r12)
                int r10 = r10 + r13
                int r10 = r10 + r5
                float r10 = (float) r10
                int r12 = r2.bottom
                int r12 = r12 - r4
                float r12 = (float) r12
                r3.moveTo(r10, r12)
                goto L_0x0358
            L_0x0347:
                android.graphics.Path r3 = r0.path
                int r10 = r2.left
                int r12 = r0.dp(r14)
                int r10 = r10 + r12
                float r10 = (float) r10
                int r12 = r2.bottom
                int r12 = r12 - r4
                float r12 = (float) r12
                r3.moveTo(r10, r12)
            L_0x0358:
                android.graphics.Path r3 = r0.path
                int r10 = r2.right
                int r10 = r10 - r4
                int r10 = r10 - r5
                float r10 = (float) r10
                int r12 = r2.bottom
                int r12 = r12 - r4
                float r12 = (float) r12
                r3.lineTo(r10, r12)
                android.graphics.RectF r3 = r0.rect
                int r10 = r2.right
                int r12 = r10 - r4
                int r13 = r5 * 2
                int r12 = r12 - r13
                float r12 = (float) r12
                int r14 = r2.bottom
                int r18 = r14 - r4
                int r13 = r18 - r13
                float r13 = (float) r13
                int r10 = r10 - r4
                float r10 = (float) r10
                int r14 = r14 - r4
                float r14 = (float) r14
                r3.set(r12, r13, r10, r14)
                android.graphics.Path r3 = r0.path
                android.graphics.RectF r10 = r0.rect
                r12 = 0
                r3.arcTo(r10, r15, r7, r12)
            L_0x0386:
                int r3 = r0.currentType
                r10 = 2
                if (r3 == r10) goto L_0x03e1
                if (r21 != 0) goto L_0x03e1
                int r3 = r0.topY
                int r10 = r5 * 2
                int r10 = r10 + r3
                if (r10 < 0) goto L_0x0395
                goto L_0x03e1
            L_0x0395:
                android.graphics.Path r10 = r0.path
                int r12 = r2.right
                int r12 = r12 - r4
                float r12 = (float) r12
                int r3 = r11 - r3
                r13 = 1073741824(0x40000000, float:2.0)
                int r14 = r0.dp(r13)
                int r3 = r3 - r14
                float r3 = (float) r3
                r10.lineTo(r12, r3)
                int r3 = r0.currentType
                r10 = 1
                if (r3 != r10) goto L_0x03c4
                android.graphics.Path r3 = r0.path
                int r10 = r2.left
                int r10 = r10 + r4
                float r10 = (float) r10
                int r12 = r0.topY
                int r12 = r11 - r12
                r13 = 1073741824(0x40000000, float:2.0)
                int r13 = r0.dp(r13)
                int r12 = r12 - r13
                float r12 = (float) r12
                r3.lineTo(r10, r12)
                goto L_0x0484
            L_0x03c4:
                android.graphics.Path r3 = r0.path
                int r10 = r2.left
                r12 = 1090519040(0x41000000, float:8.0)
                int r13 = r0.dp(r12)
                int r10 = r10 + r13
                float r10 = (float) r10
                int r12 = r0.topY
                int r12 = r11 - r12
                r13 = 1073741824(0x40000000, float:2.0)
                int r13 = r0.dp(r13)
                int r12 = r12 - r13
                float r12 = (float) r12
                r3.lineTo(r10, r12)
                goto L_0x0484
            L_0x03e1:
                android.graphics.Path r3 = r0.path
                int r10 = r2.right
                int r10 = r10 - r4
                float r10 = (float) r10
                int r12 = r2.top
                int r12 = r12 + r4
                int r12 = r12 + r5
                float r12 = (float) r12
                r3.lineTo(r10, r12)
                android.graphics.RectF r3 = r0.rect
                int r10 = r2.right
                int r12 = r10 - r4
                int r13 = r5 * 2
                int r12 = r12 - r13
                float r12 = (float) r12
                int r14 = r2.top
                int r15 = r14 + r4
                float r15 = (float) r15
                int r10 = r10 - r4
                float r10 = (float) r10
                int r14 = r14 + r4
                int r14 = r14 + r13
                float r13 = (float) r14
                r3.set(r12, r15, r10, r13)
                android.graphics.Path r3 = r0.path
                android.graphics.RectF r10 = r0.rect
                r12 = 0
                r13 = 0
                r3.arcTo(r10, r12, r7, r13)
                boolean r3 = r0.isTopNear
                if (r3 == 0) goto L_0x0415
                r3 = r8
                goto L_0x0416
            L_0x0415:
                r3 = r5
            L_0x0416:
                int r10 = r0.currentType
                r12 = 1
                if (r10 != r12) goto L_0x0443
                android.graphics.Path r10 = r0.path
                int r12 = r2.left
                int r12 = r12 + r4
                int r12 = r12 + r3
                float r12 = (float) r12
                int r13 = r2.top
                int r13 = r13 + r4
                float r13 = (float) r13
                r10.lineTo(r12, r13)
                android.graphics.RectF r10 = r0.rect
                int r12 = r2.left
                int r13 = r12 + r4
                float r13 = (float) r13
                int r14 = r2.top
                int r15 = r14 + r4
                float r15 = (float) r15
                int r12 = r12 + r4
                r16 = 2
                int r3 = r3 * 2
                int r12 = r12 + r3
                float r12 = (float) r12
                int r14 = r14 + r4
                int r14 = r14 + r3
                float r3 = (float) r14
                r10.set(r13, r15, r12, r3)
                goto L_0x047a
            L_0x0443:
                android.graphics.Path r10 = r0.path
                int r12 = r2.left
                r13 = 1090519040(0x41000000, float:8.0)
                int r14 = r0.dp(r13)
                int r12 = r12 + r14
                int r12 = r12 + r3
                float r12 = (float) r12
                int r14 = r2.top
                int r14 = r14 + r4
                float r14 = (float) r14
                r10.lineTo(r12, r14)
                android.graphics.RectF r10 = r0.rect
                int r12 = r2.left
                int r14 = r0.dp(r13)
                int r12 = r12 + r14
                float r12 = (float) r12
                int r14 = r2.top
                int r14 = r14 + r4
                float r14 = (float) r14
                int r15 = r2.left
                int r16 = r0.dp(r13)
                int r15 = r15 + r16
                r13 = 2
                int r3 = r3 * 2
                int r15 = r15 + r3
                float r13 = (float) r15
                int r15 = r2.top
                int r15 = r15 + r4
                int r15 = r15 + r3
                float r3 = (float) r15
                r10.set(r12, r14, r13, r3)
            L_0x047a:
                android.graphics.Path r3 = r0.path
                android.graphics.RectF r10 = r0.rect
                r12 = 1132920832(0x43870000, float:270.0)
                r13 = 0
                r3.arcTo(r10, r12, r7, r13)
            L_0x0484:
                int r3 = r0.currentType
                r10 = 1
                if (r3 != r10) goto L_0x04de
                r10 = 2
                if (r3 == r10) goto L_0x04a7
                if (r21 != 0) goto L_0x04a7
                int r3 = r0.topY
                int r6 = r2.bottom
                int r6 = r6 + r3
                int r6 = r6 - r5
                int r10 = r0.currentBackgroundHeight
                if (r6 >= r10) goto L_0x0499
                goto L_0x04a7
            L_0x0499:
                android.graphics.Path r5 = r0.path
                int r2 = r2.left
                int r2 = r2 + r4
                float r2 = (float) r2
                int r11 = r11 - r3
                int r11 = r11 + r10
                float r3 = (float) r11
                r5.lineTo(r2, r3)
                goto L_0x0561
            L_0x04a7:
                boolean r3 = r0.isBottomNear
                if (r3 == 0) goto L_0x04ac
                r5 = r8
            L_0x04ac:
                android.graphics.Path r3 = r0.path
                int r6 = r2.left
                int r6 = r6 + r4
                float r6 = (float) r6
                int r8 = r2.bottom
                int r8 = r8 - r4
                int r8 = r8 - r5
                float r8 = (float) r8
                r3.lineTo(r6, r8)
                android.graphics.RectF r3 = r0.rect
                int r6 = r2.left
                int r8 = r6 + r4
                float r8 = (float) r8
                int r2 = r2.bottom
                int r10 = r2 - r4
                r11 = 2
                int r5 = r5 * 2
                int r10 = r10 - r5
                float r10 = (float) r10
                int r6 = r6 + r4
                int r6 = r6 + r5
                float r5 = (float) r6
                int r2 = r2 - r4
                float r2 = (float) r2
                r3.set(r8, r10, r5, r2)
                android.graphics.Path r2 = r0.path
                android.graphics.RectF r3 = r0.rect
                r4 = 1127481344(0x43340000, float:180.0)
                r5 = 0
                r2.arcTo(r3, r4, r7, r5)
                goto L_0x0561
            L_0x04de:
                r5 = 2
                if (r3 == r5) goto L_0x0507
                if (r21 != 0) goto L_0x0507
                int r3 = r0.topY
                int r5 = r2.bottom
                int r3 = r3 + r5
                int r5 = r6 * 2
                int r3 = r3 - r5
                int r5 = r0.currentBackgroundHeight
                if (r3 >= r5) goto L_0x04f0
                goto L_0x0507
            L_0x04f0:
                android.graphics.Path r3 = r0.path
                int r2 = r2.left
                r4 = 1090519040(0x41000000, float:8.0)
                int r4 = r0.dp(r4)
                int r2 = r2 + r4
                float r2 = (float) r2
                int r4 = r0.topY
                int r11 = r11 - r4
                int r4 = r0.currentBackgroundHeight
                int r11 = r11 + r4
                float r4 = (float) r11
                r3.lineTo(r2, r4)
                goto L_0x0561
            L_0x0507:
                android.graphics.Path r3 = r0.path
                int r5 = r2.left
                r7 = 1090519040(0x41000000, float:8.0)
                int r8 = r0.dp(r7)
                int r5 = r5 + r8
                float r5 = (float) r5
                int r7 = r2.bottom
                int r7 = r7 - r4
                int r7 = r7 - r6
                r8 = 1077936128(0x40400000, float:3.0)
                int r8 = r0.dp(r8)
                int r7 = r7 - r8
                float r7 = (float) r7
                r3.lineTo(r5, r7)
                android.graphics.RectF r3 = r0.rect
                int r5 = r2.left
                r7 = 1088421888(0x40e00000, float:7.0)
                int r7 = r0.dp(r7)
                int r5 = r5 + r7
                r7 = 2
                int r6 = r6 * 2
                int r5 = r5 - r6
                float r5 = (float) r5
                int r7 = r2.bottom
                int r7 = r7 - r4
                int r7 = r7 - r6
                r6 = 1091567616(0x41100000, float:9.0)
                int r6 = r0.dp(r6)
                int r7 = r7 - r6
                float r6 = (float) r7
                int r7 = r2.left
                r8 = 1090519040(0x41000000, float:8.0)
                int r8 = r0.dp(r8)
                int r7 = r7 + r8
                float r7 = (float) r7
                int r2 = r2.bottom
                int r2 = r2 - r4
                r4 = 1065353216(0x3var_, float:1.0)
                int r4 = r0.dp(r4)
                int r2 = r2 - r4
                float r2 = (float) r2
                r3.set(r5, r6, r7, r2)
                android.graphics.Path r2 = r0.path
                android.graphics.RectF r3 = r0.rect
                r4 = 1118175232(0x42a60000, float:83.0)
                r5 = 0
                r6 = 0
                r2.arcTo(r3, r5, r4, r6)
            L_0x0561:
                android.graphics.Path r2 = r0.path
                r2.close()
                android.graphics.Path r2 = r0.path
                r1.drawPath(r2, r9)
                android.graphics.LinearGradient r2 = r0.gradientShader
                if (r2 == 0) goto L_0x0585
                boolean r2 = r0.isSelected
                if (r2 == 0) goto L_0x0585
                android.graphics.Paint r2 = r0.selectedPaint
                java.lang.String r3 = "chat_outBubbleGradientSelectedOverlay"
                int r3 = r0.getColor(r3)
                r2.setColor(r3)
                android.graphics.Path r2 = r0.path
                android.graphics.Paint r3 = r0.selectedPaint
                r1.drawPath(r2, r3)
            L_0x0585:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.MessageDrawable.draw(android.graphics.Canvas, android.graphics.Paint):void");
        }

        public void setAlpha(int i) {
            this.paint.setAlpha(i);
            if (this.isOut) {
                this.selectedPaint.setAlpha((int) (((float) Color.alpha(getColor("chat_outBubbleGradientSelectedOverlay"))) * (((float) i) / 255.0f)));
            }
            if (this.gradientShader == null) {
                getBackgroundDrawable().setAlpha(i);
            }
        }
    }

    public static class PatternsLoader implements NotificationCenter.NotificationCenterDelegate {
        private static PatternsLoader loader;
        private int account = UserConfig.selectedAccount;
        private HashMap<String, LoadingPattern> watingForLoad;

        private static class LoadingPattern {
            public ArrayList<ThemeAccent> accents;
            public TLRPC$TL_wallPaper pattern;

            private LoadingPattern() {
                this.accents = new ArrayList<>();
            }
        }

        public static void createLoader(boolean z) {
            ArrayList<ThemeAccent> arrayList;
            if (loader == null || z) {
                ArrayList arrayList2 = null;
                int i = 0;
                while (i < 5) {
                    ThemeInfo themeInfo = (ThemeInfo) Theme.themesDict.get(i != 0 ? i != 1 ? i != 2 ? i != 3 ? "Night" : "Day" : "Arctic Blue" : "Dark Blue" : "Blue");
                    if (!(themeInfo == null || (arrayList = themeInfo.themeAccents) == null || arrayList.isEmpty())) {
                        int size = themeInfo.themeAccents.size();
                        for (int i2 = 0; i2 < size; i2++) {
                            ThemeAccent themeAccent = themeInfo.themeAccents.get(i2);
                            if (themeAccent.id != Theme.DEFALT_THEME_ACCENT_ID && !TextUtils.isEmpty(themeAccent.patternSlug)) {
                                if (arrayList2 == null) {
                                    arrayList2 = new ArrayList();
                                }
                                arrayList2.add(themeAccent);
                            }
                        }
                    }
                    i++;
                }
                loader = new PatternsLoader(arrayList2);
            }
        }

        private PatternsLoader(ArrayList<ThemeAccent> arrayList) {
            if (arrayList != null) {
                Utilities.globalQueue.postRunnable(new Runnable(arrayList) {
                    private final /* synthetic */ ArrayList f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        Theme.PatternsLoader.this.lambda$new$1$Theme$PatternsLoader(this.f$1);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$new$1$Theme$PatternsLoader(ArrayList arrayList) {
            int size = arrayList.size();
            ArrayList arrayList2 = null;
            int i = 0;
            while (i < size) {
                ThemeAccent themeAccent = (ThemeAccent) arrayList.get(i);
                File pathToWallpaper = themeAccent.getPathToWallpaper();
                if (pathToWallpaper == null || !pathToWallpaper.exists()) {
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList();
                    }
                    if (!arrayList2.contains(themeAccent.patternSlug)) {
                        arrayList2.add(themeAccent.patternSlug);
                    }
                } else {
                    arrayList.remove(i);
                    i--;
                    size--;
                }
                i++;
            }
            if (arrayList2 != null) {
                TLRPC$TL_account_getMultiWallPapers tLRPC$TL_account_getMultiWallPapers = new TLRPC$TL_account_getMultiWallPapers();
                int size2 = arrayList2.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    TLRPC$TL_inputWallPaperSlug tLRPC$TL_inputWallPaperSlug = new TLRPC$TL_inputWallPaperSlug();
                    tLRPC$TL_inputWallPaperSlug.slug = (String) arrayList2.get(i2);
                    tLRPC$TL_account_getMultiWallPapers.wallpapers.add(tLRPC$TL_inputWallPaperSlug);
                }
                ConnectionsManager.getInstance(this.account).sendRequest(tLRPC$TL_account_getMultiWallPapers, new RequestDelegate(arrayList) {
                    private final /* synthetic */ ArrayList f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        Theme.PatternsLoader.this.lambda$null$0$Theme$PatternsLoader(this.f$1, tLObject, tLRPC$TL_error);
                    }
                });
            }
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v0, resolved type: org.telegram.ui.ActionBar.Theme$1} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v0, resolved type: org.telegram.ui.ActionBar.Theme$1} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v1, resolved type: java.lang.Boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v1, resolved type: android.graphics.Bitmap} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v2, resolved type: java.lang.Boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v3, resolved type: java.lang.Boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v4, resolved type: java.lang.Boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v4, resolved type: android.graphics.Bitmap} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v7, resolved type: android.graphics.Bitmap} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$null$0$Theme$PatternsLoader(java.util.ArrayList r17, org.telegram.tgnet.TLObject r18, org.telegram.tgnet.TLRPC$TL_error r19) {
            /*
                r16 = this;
                r0 = r16
                r1 = r18
                boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$Vector
                if (r2 == 0) goto L_0x00b9
                org.telegram.tgnet.TLRPC$Vector r1 = (org.telegram.tgnet.TLRPC$Vector) r1
                java.util.ArrayList<java.lang.Object> r2 = r1.objects
                int r2 = r2.size()
                r4 = 0
                r6 = r4
                r5 = 0
            L_0x0013:
                r7 = 1
                if (r5 >= r2) goto L_0x00b6
                java.util.ArrayList<java.lang.Object> r8 = r1.objects
                java.lang.Object r8 = r8.get(r5)
                org.telegram.tgnet.TLRPC$WallPaper r8 = (org.telegram.tgnet.TLRPC$WallPaper) r8
                boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_wallPaper
                if (r9 != 0) goto L_0x0024
                goto L_0x00af
            L_0x0024:
                org.telegram.tgnet.TLRPC$TL_wallPaper r8 = (org.telegram.tgnet.TLRPC$TL_wallPaper) r8
                boolean r9 = r8.pattern
                if (r9 == 0) goto L_0x00af
                org.telegram.tgnet.TLRPC$Document r9 = r8.document
                java.io.File r7 = org.telegram.messenger.FileLoader.getPathToAttach(r9, r7)
                int r9 = r17.size()
                r11 = r4
                r12 = r11
                r10 = 0
            L_0x0037:
                if (r10 >= r9) goto L_0x00a7
                r13 = r17
                java.lang.Object r14 = r13.get(r10)
                org.telegram.ui.ActionBar.Theme$ThemeAccent r14 = (org.telegram.ui.ActionBar.Theme.ThemeAccent) r14
                java.lang.String r15 = r14.patternSlug
                java.lang.String r3 = r8.slug
                boolean r3 = r15.equals(r3)
                if (r3 == 0) goto L_0x00a3
                if (r12 != 0) goto L_0x0055
                boolean r3 = r7.exists()
                java.lang.Boolean r12 = java.lang.Boolean.valueOf(r3)
            L_0x0055:
                if (r11 != 0) goto L_0x008b
                boolean r3 = r12.booleanValue()
                if (r3 == 0) goto L_0x005e
                goto L_0x008b
            L_0x005e:
                org.telegram.tgnet.TLRPC$Document r3 = r8.document
                java.lang.String r3 = org.telegram.messenger.FileLoader.getAttachFileName(r3)
                java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$PatternsLoader$LoadingPattern> r15 = r0.watingForLoad
                if (r15 != 0) goto L_0x006f
                java.util.HashMap r15 = new java.util.HashMap
                r15.<init>()
                r0.watingForLoad = r15
            L_0x006f:
                java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$PatternsLoader$LoadingPattern> r15 = r0.watingForLoad
                java.lang.Object r15 = r15.get(r3)
                org.telegram.ui.ActionBar.Theme$PatternsLoader$LoadingPattern r15 = (org.telegram.ui.ActionBar.Theme.PatternsLoader.LoadingPattern) r15
                if (r15 != 0) goto L_0x0085
                org.telegram.ui.ActionBar.Theme$PatternsLoader$LoadingPattern r15 = new org.telegram.ui.ActionBar.Theme$PatternsLoader$LoadingPattern
                r15.<init>()
                r15.pattern = r8
                java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$PatternsLoader$LoadingPattern> r4 = r0.watingForLoad
                r4.put(r3, r15)
            L_0x0085:
                java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeAccent> r3 = r15.accents
                r3.add(r14)
                goto L_0x00a3
            L_0x008b:
                org.telegram.tgnet.TLRPC$Document r3 = r8.document
                java.lang.String r3 = r3.mime_type
                java.lang.String r4 = "application/x-tgwallpattern"
                boolean r3 = r4.equals(r3)
                android.graphics.Bitmap r11 = r0.createWallpaperForAccent(r11, r3, r7, r14)
                if (r6 != 0) goto L_0x00a0
                java.util.ArrayList r6 = new java.util.ArrayList
                r6.<init>()
            L_0x00a0:
                r6.add(r14)
            L_0x00a3:
                int r10 = r10 + 1
                r4 = 0
                goto L_0x0037
            L_0x00a7:
                r13 = r17
                if (r11 == 0) goto L_0x00b1
                r11.recycle()
                goto L_0x00b1
            L_0x00af:
                r13 = r17
            L_0x00b1:
                int r5 = r5 + 1
                r4 = 0
                goto L_0x0013
            L_0x00b6:
                r0.checkCurrentWallpaper(r6, r7)
            L_0x00b9:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.PatternsLoader.lambda$null$0$Theme$PatternsLoader(java.util.ArrayList, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
        }

        private void checkCurrentWallpaper(ArrayList<ThemeAccent> arrayList, boolean z) {
            AndroidUtilities.runOnUIThread(new Runnable(arrayList, z) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    Theme.PatternsLoader.this.lambda$checkCurrentWallpaper$2$Theme$PatternsLoader(this.f$1, this.f$2);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: checkCurrentWallpaperInternal */
        public void lambda$checkCurrentWallpaper$2$Theme$PatternsLoader(ArrayList<ThemeAccent> arrayList, boolean z) {
            if (arrayList != null && Theme.currentTheme.themeAccents != null && !Theme.currentTheme.themeAccents.isEmpty() && arrayList.contains(Theme.currentTheme.getAccent(false))) {
                Theme.reloadWallpaper();
            }
            if (!z) {
                HashMap<String, LoadingPattern> hashMap = this.watingForLoad;
                if (hashMap == null || hashMap.isEmpty()) {
                    NotificationCenter.getInstance(this.account).removeObserver(this, NotificationCenter.fileDidLoad);
                    NotificationCenter.getInstance(this.account).removeObserver(this, NotificationCenter.fileDidFailToLoad);
                }
            } else if (this.watingForLoad != null) {
                NotificationCenter.getInstance(this.account).addObserver(this, NotificationCenter.fileDidLoad);
                NotificationCenter.getInstance(this.account).addObserver(this, NotificationCenter.fileDidFailToLoad);
                for (Map.Entry<String, LoadingPattern> value : this.watingForLoad.entrySet()) {
                    FileLoader.getInstance(this.account).loadFile(ImageLocation.getForDocument(((LoadingPattern) value.getValue()).pattern.document), "wallpaper", (String) null, 0, 1);
                }
            }
        }

        private Bitmap createWallpaperForAccent(Bitmap bitmap, boolean z, File file, ThemeAccent themeAccent) {
            Bitmap bitmap2;
            Drawable drawable;
            int i;
            Bitmap bitmap3;
            Integer num;
            File file2 = file;
            ThemeAccent themeAccent2 = themeAccent;
            try {
                File pathToWallpaper = themeAccent.getPathToWallpaper();
                if (pathToWallpaper == null) {
                    return null;
                }
                ThemeInfo themeInfo = themeAccent2.parentTheme;
                HashMap<String, Integer> themeFileValues = Theme.getThemeFileValues((File) null, themeInfo.assetName, (String[]) null);
                int i2 = themeAccent2.accentColor;
                int i3 = (int) themeAccent2.backgroundOverrideColor;
                int i4 = (int) themeAccent2.backgroundGradientOverrideColor;
                if (i4 == 0 && themeAccent2.backgroundGradientOverrideColor == 0) {
                    if (i3 != 0) {
                        i2 = i3;
                    }
                    Integer num2 = themeFileValues.get("chat_wallpaper_gradient_to");
                    if (num2 != null) {
                        i4 = Theme.changeColorAccent(themeInfo, i2, num2.intValue());
                    }
                } else {
                    i2 = 0;
                }
                if (i3 == 0 && (num = themeFileValues.get("chat_wallpaper")) != null) {
                    i3 = Theme.changeColorAccent(themeInfo, i2, num.intValue());
                }
                if (i4 != 0) {
                    drawable = new BackgroundGradientDrawable(BackgroundGradientDrawable.getGradientOrientation(themeAccent2.backgroundRotation), new int[]{i3, i4});
                    i = AndroidUtilities.getPatternColor(AndroidUtilities.getAverageColor(i3, i4));
                } else {
                    drawable = new ColorDrawable(i3);
                    i = AndroidUtilities.getPatternColor(i3);
                }
                if (bitmap == null) {
                    if (z) {
                        bitmap3 = SvgHelper.getBitmap(file2, AndroidUtilities.dp(360.0f), AndroidUtilities.dp(640.0f), false);
                    } else {
                        bitmap3 = Theme.loadScreenSizedBitmap(new FileInputStream(file2), 0);
                    }
                    bitmap2 = bitmap3;
                } else {
                    bitmap2 = bitmap;
                }
                try {
                    Bitmap createBitmap = Bitmap.createBitmap(bitmap2.getWidth(), bitmap2.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(createBitmap);
                    drawable.setBounds(0, 0, bitmap2.getWidth(), bitmap2.getHeight());
                    drawable.draw(canvas);
                    Paint paint = new Paint(2);
                    paint.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN));
                    paint.setAlpha((int) (themeAccent2.patternIntensity * 255.0f));
                    canvas.drawBitmap(bitmap2, 0.0f, 0.0f, paint);
                    FileOutputStream fileOutputStream = new FileOutputStream(pathToWallpaper);
                    createBitmap.compress(Bitmap.CompressFormat.JPEG, 87, fileOutputStream);
                    fileOutputStream.close();
                } catch (Throwable th) {
                    th = th;
                }
                return bitmap2;
            } catch (Throwable th2) {
                th = th2;
                bitmap2 = bitmap;
                FileLog.e(th);
                return bitmap2;
            }
        }

        public void didReceivedNotification(int i, int i2, Object... objArr) {
            HashMap<String, LoadingPattern> hashMap = this.watingForLoad;
            if (hashMap != null) {
                if (i == NotificationCenter.fileDidLoad) {
                    LoadingPattern remove = hashMap.remove(objArr[0]);
                    if (remove != null) {
                        Utilities.globalQueue.postRunnable(new Runnable(remove) {
                            private final /* synthetic */ Theme.PatternsLoader.LoadingPattern f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                Theme.PatternsLoader.this.lambda$didReceivedNotification$3$Theme$PatternsLoader(this.f$1);
                            }
                        });
                    }
                } else if (i == NotificationCenter.fileDidFailToLoad && hashMap.remove(objArr[0]) != null) {
                    checkCurrentWallpaper((ArrayList<ThemeAccent>) null, false);
                }
            }
        }

        public /* synthetic */ void lambda$didReceivedNotification$3$Theme$PatternsLoader(LoadingPattern loadingPattern) {
            TLRPC$TL_wallPaper tLRPC$TL_wallPaper = loadingPattern.pattern;
            File pathToAttach = FileLoader.getPathToAttach(tLRPC$TL_wallPaper.document, true);
            int size = loadingPattern.accents.size();
            Bitmap bitmap = null;
            ArrayList arrayList = null;
            for (int i = 0; i < size; i++) {
                ThemeAccent themeAccent = loadingPattern.accents.get(i);
                if (themeAccent.patternSlug.equals(tLRPC$TL_wallPaper.slug)) {
                    bitmap = createWallpaperForAccent(bitmap, "application/x-tgwallpattern".equals(tLRPC$TL_wallPaper.document.mime_type), pathToAttach, themeAccent);
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                        arrayList.add(themeAccent);
                    }
                }
            }
            if (bitmap != null) {
                bitmap.recycle();
            }
            checkCurrentWallpaper(arrayList, false);
        }
    }

    public static class ThemeAccent {
        public int accentColor;
        public int account;
        public long backgroundGradientOverrideColor;
        public long backgroundOverrideColor;
        public int backgroundRotation = 45;
        public int id;
        public TLRPC$TL_theme info;
        public int myMessagesAccentColor;
        public int myMessagesGradientAccentColor;
        public OverrideWallpaperInfo overrideWallpaper;
        public ThemeInfo parentTheme;
        public TLRPC$TL_wallPaper pattern;
        public float patternIntensity;
        public boolean patternMotion;
        public String patternSlug = "";
        public TLRPC$InputFile uploadedFile;
        public TLRPC$InputFile uploadedThumb;
        public String uploadingFile;
        public String uploadingThumb;

        public boolean fillAccentColors(HashMap<String, Integer> hashMap, HashMap<String, Integer> hashMap2) {
            boolean z;
            int i;
            int i2;
            int i3;
            int i4;
            int i5;
            int changeColorAccent;
            String str;
            int i6;
            String str2;
            float[] access$600 = Theme.getTempHsv(1);
            float[] access$6002 = Theme.getTempHsv(2);
            Color.colorToHSV(this.parentTheme.accentBaseColor, access$600);
            Color.colorToHSV(this.accentColor, access$6002);
            boolean isDark = this.parentTheme.isDark();
            if (this.accentColor != this.parentTheme.accentBaseColor) {
                HashSet hashSet = new HashSet(hashMap.keySet());
                hashSet.addAll(Theme.defaultColors.keySet());
                hashSet.removeAll(Theme.themeAccentExclusionKeys);
                Iterator it = hashSet.iterator();
                while (it.hasNext()) {
                    String str3 = (String) it.next();
                    Integer num = hashMap.get(str3);
                    if (num != null || (str2 = (String) Theme.fallbackKeys.get(str3)) == null || hashMap.get(str2) == null) {
                        if (num == null) {
                            num = (Integer) Theme.defaultColors.get(str3);
                        }
                        int changeColorAccent2 = Theme.changeColorAccent(access$600, access$6002, num.intValue(), isDark);
                        if (changeColorAccent2 != num.intValue()) {
                            hashMap2.put(str3, Integer.valueOf(changeColorAccent2));
                        }
                    }
                }
            }
            int i7 = this.myMessagesAccentColor;
            boolean z2 = false;
            if ((i7 == 0 && this.accentColor == 0) || this.myMessagesGradientAccentColor == 0) {
                z = false;
            } else {
                int i8 = this.myMessagesAccentColor;
                if (i8 == 0) {
                    i8 = this.accentColor;
                }
                Integer num2 = hashMap.get("chat_outBubble");
                if (num2 == null) {
                    num2 = (Integer) Theme.defaultColors.get("chat_outBubble");
                }
                z = AndroidUtilities.getColorDistance(i8, Theme.changeColorAccent(access$600, access$6002, num2.intValue(), isDark)) <= 35000 && AndroidUtilities.getColorDistance(i8, this.myMessagesGradientAccentColor) <= 35000;
                i7 = Theme.getAccentColor(access$600, num2.intValue(), i8);
            }
            if (!(i7 == 0 || (((i5 = this.parentTheme.accentBaseColor) == 0 || i7 == i5) && ((i6 = this.accentColor) == 0 || i6 == i7)))) {
                Color.colorToHSV(i7, access$6002);
                Iterator it2 = Theme.myMessagesColorKeys.iterator();
                while (it2.hasNext()) {
                    String str4 = (String) it2.next();
                    Integer num3 = hashMap.get(str4);
                    if (num3 != null || (str = (String) Theme.fallbackKeys.get(str4)) == null || hashMap.get(str) == null) {
                        if (num3 == null) {
                            num3 = (Integer) Theme.defaultColors.get(str4);
                        }
                        if (!(num3 == null || (changeColorAccent = Theme.changeColorAccent(access$600, access$6002, num3.intValue(), isDark)) == num3.intValue())) {
                            hashMap2.put(str4, Integer.valueOf(changeColorAccent));
                        }
                    }
                }
            }
            if (!z && (i = this.myMessagesGradientAccentColor) != 0) {
                if (Theme.useBlackText(this.myMessagesAccentColor, i)) {
                    i4 = -16777216;
                    i3 = -11184811;
                    i2 = NUM;
                } else {
                    i3 = -1118482;
                    i2 = NUM;
                    i4 = -1;
                }
                hashMap2.put("chat_outAudioProgress", Integer.valueOf(i2));
                hashMap2.put("chat_outAudioSelectedProgress", Integer.valueOf(i2));
                hashMap2.put("chat_outAudioSeekbar", Integer.valueOf(i2));
                hashMap2.put("chat_outAudioCacheSeekbar", Integer.valueOf(i2));
                hashMap2.put("chat_outAudioSeekbarSelected", Integer.valueOf(i2));
                hashMap2.put("chat_outAudioSeekbarFill", Integer.valueOf(i4));
                hashMap2.put("chat_outVoiceSeekbar", Integer.valueOf(i2));
                hashMap2.put("chat_outVoiceSeekbarSelected", Integer.valueOf(i2));
                hashMap2.put("chat_outVoiceSeekbarFill", Integer.valueOf(i4));
                hashMap2.put("chat_messageTextOut", Integer.valueOf(i4));
                hashMap2.put("chat_messageLinkOut", Integer.valueOf(i4));
                hashMap2.put("chat_outForwardedNameText", Integer.valueOf(i4));
                hashMap2.put("chat_outViaBotNameText", Integer.valueOf(i4));
                hashMap2.put("chat_outReplyLine", Integer.valueOf(i4));
                hashMap2.put("chat_outReplyNameText", Integer.valueOf(i4));
                hashMap2.put("chat_outPreviewLine", Integer.valueOf(i4));
                hashMap2.put("chat_outSiteNameText", Integer.valueOf(i4));
                hashMap2.put("chat_outInstant", Integer.valueOf(i4));
                hashMap2.put("chat_outInstantSelected", Integer.valueOf(i4));
                hashMap2.put("chat_outPreviewInstantText", Integer.valueOf(i4));
                hashMap2.put("chat_outPreviewInstantSelectedText", Integer.valueOf(i4));
                hashMap2.put("chat_outViews", Integer.valueOf(i4));
                hashMap2.put("chat_outAudioTitleText", Integer.valueOf(i4));
                hashMap2.put("chat_outFileNameText", Integer.valueOf(i4));
                hashMap2.put("chat_outContactNameText", Integer.valueOf(i4));
                hashMap2.put("chat_outAudioPerfomerText", Integer.valueOf(i4));
                hashMap2.put("chat_outAudioPerfomerSelectedText", Integer.valueOf(i4));
                hashMap2.put("chat_outSentCheck", Integer.valueOf(i4));
                hashMap2.put("chat_outSentCheckSelected", Integer.valueOf(i4));
                hashMap2.put("chat_outSentCheckRead", Integer.valueOf(i4));
                hashMap2.put("chat_outSentCheckReadSelected", Integer.valueOf(i4));
                hashMap2.put("chat_outSentClock", Integer.valueOf(i4));
                hashMap2.put("chat_outSentClockSelected", Integer.valueOf(i4));
                hashMap2.put("chat_outMenu", Integer.valueOf(i4));
                hashMap2.put("chat_outMenuSelected", Integer.valueOf(i4));
                hashMap2.put("chat_outTimeText", Integer.valueOf(i4));
                hashMap2.put("chat_outTimeSelectedText", Integer.valueOf(i4));
                hashMap2.put("chat_outAudioDurationText", Integer.valueOf(i3));
                hashMap2.put("chat_outAudioDurationSelectedText", Integer.valueOf(i3));
                hashMap2.put("chat_outContactPhoneText", Integer.valueOf(i3));
                hashMap2.put("chat_outContactPhoneSelectedText", Integer.valueOf(i3));
                hashMap2.put("chat_outFileInfoText", Integer.valueOf(i3));
                hashMap2.put("chat_outFileInfoSelectedText", Integer.valueOf(i3));
                hashMap2.put("chat_outVenueInfoText", Integer.valueOf(i3));
                hashMap2.put("chat_outVenueInfoSelectedText", Integer.valueOf(i3));
                hashMap2.put("chat_outReplyMessageText", Integer.valueOf(i4));
                hashMap2.put("chat_outReplyMediaMessageText", Integer.valueOf(i4));
                hashMap2.put("chat_outReplyMediaMessageSelectedText", Integer.valueOf(i4));
                hashMap2.put("chat_outLoader", Integer.valueOf(i4));
                hashMap2.put("chat_outLoaderSelected", Integer.valueOf(i4));
                hashMap2.put("chat_outFileProgress", Integer.valueOf(this.myMessagesAccentColor));
                hashMap2.put("chat_outFileProgressSelected", Integer.valueOf(this.myMessagesAccentColor));
                hashMap2.put("chat_outMediaIcon", Integer.valueOf(this.myMessagesAccentColor));
                hashMap2.put("chat_outMediaIconSelected", Integer.valueOf(this.myMessagesAccentColor));
            }
            if (!z || AndroidUtilities.getColorDistance(-1, hashMap2.get("chat_outLoader").intValue()) >= 5000) {
                z2 = z;
            }
            int i9 = this.myMessagesAccentColor;
            if (!(i9 == 0 || this.myMessagesGradientAccentColor == 0)) {
                hashMap2.put("chat_outBubble", Integer.valueOf(i9));
                hashMap2.put("chat_outBubbleGradient", Integer.valueOf(this.myMessagesGradientAccentColor));
            }
            long j = this.backgroundOverrideColor;
            int i10 = (int) j;
            if (i10 != 0) {
                hashMap2.put("chat_wallpaper", Integer.valueOf(i10));
            } else if (j != 0) {
                hashMap2.remove("chat_wallpaper");
            }
            long j2 = this.backgroundGradientOverrideColor;
            int i11 = (int) j2;
            if (i11 != 0) {
                hashMap2.put("chat_wallpaper_gradient_to", Integer.valueOf(i11));
            } else if (j2 != 0) {
                hashMap2.remove("chat_wallpaper_gradient_to");
            }
            int i12 = this.backgroundRotation;
            if (i12 != 45) {
                hashMap2.put("chat_wallpaper_gradient_rotation", Integer.valueOf(i12));
            }
            return !z2;
        }

        public File getPathToWallpaper() {
            if (TextUtils.isEmpty(this.patternSlug)) {
                return null;
            }
            return new File(ApplicationLoader.getFilesDirFixed(), String.format(Locale.US, "%s_%d_%s.jpg", new Object[]{this.parentTheme.getKey(), Integer.valueOf(this.id), this.patternSlug}));
        }

        /* JADX WARNING: Removed duplicated region for block: B:51:0x01ec A[SYNTHETIC, Splitter:B:51:0x01ec] */
        /* JADX WARNING: Removed duplicated region for block: B:57:0x01f7 A[SYNTHETIC, Splitter:B:57:0x01f7] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.io.File saveToFile() {
            /*
                r15 = this;
                java.io.File r0 = org.telegram.messenger.AndroidUtilities.getSharingDirectory()
                r0.mkdirs()
                java.io.File r1 = new java.io.File
                java.util.Locale r2 = java.util.Locale.US
                r3 = 2
                java.lang.Object[] r4 = new java.lang.Object[r3]
                org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = r15.parentTheme
                java.lang.String r5 = r5.getKey()
                r6 = 0
                r4[r6] = r5
                int r5 = r15.id
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r7 = 1
                r4[r7] = r5
                java.lang.String r5 = "%s_%d.attheme"
                java.lang.String r2 = java.lang.String.format(r2, r5, r4)
                r1.<init>(r0, r2)
                org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = r15.parentTheme
                java.lang.String r0 = r0.assetName
                r2 = 0
                java.util.HashMap r0 = org.telegram.ui.ActionBar.Theme.getThemeFileValues(r2, r0, r2)
                java.util.HashMap r4 = new java.util.HashMap
                r4.<init>(r0)
                r15.fillAccentColors(r0, r4)
                java.lang.String r0 = r15.patternSlug
                boolean r0 = android.text.TextUtils.isEmpty(r0)
                java.lang.String r5 = "chat_wallpaper_gradient_to"
                java.lang.String r8 = "chat_wallpaper"
                if (r0 != 0) goto L_0x0165
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                boolean r9 = r15.patternMotion
                if (r9 == 0) goto L_0x0054
                java.lang.String r9 = "motion"
                r0.append(r9)
            L_0x0054:
                java.lang.Object r9 = r4.get(r8)
                java.lang.Integer r9 = (java.lang.Integer) r9
                if (r9 != 0) goto L_0x0061
                r9 = -1
                java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            L_0x0061:
                java.lang.Object r10 = r4.get(r5)
                java.lang.Integer r10 = (java.lang.Integer) r10
                if (r10 != 0) goto L_0x006d
                java.lang.Integer r10 = java.lang.Integer.valueOf(r6)
            L_0x006d:
                java.lang.String r11 = "chat_wallpaper_gradient_rotation"
                java.lang.Object r11 = r4.get(r11)
                java.lang.Integer r11 = (java.lang.Integer) r11
                if (r11 != 0) goto L_0x007d
                r11 = 45
                java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            L_0x007d:
                r12 = 3
                java.lang.Object[] r13 = new java.lang.Object[r12]
                int r14 = r9.intValue()
                int r14 = r14 >> 16
                byte r14 = (byte) r14
                r14 = r14 & 255(0xff, float:3.57E-43)
                java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
                r13[r6] = r14
                int r14 = r9.intValue()
                int r14 = r14 >> 8
                byte r14 = (byte) r14
                r14 = r14 & 255(0xff, float:3.57E-43)
                java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
                r13[r7] = r14
                int r9 = r9.intValue()
                r9 = r9 & 255(0xff, float:3.57E-43)
                byte r9 = (byte) r9
                java.lang.Byte r9 = java.lang.Byte.valueOf(r9)
                r13[r3] = r9
                java.lang.String r9 = "%02x%02x%02x"
                java.lang.String r13 = java.lang.String.format(r9, r13)
                java.lang.String r13 = r13.toLowerCase()
                int r14 = r10.intValue()
                if (r14 == 0) goto L_0x00f1
                java.lang.Object[] r12 = new java.lang.Object[r12]
                int r14 = r10.intValue()
                int r14 = r14 >> 16
                byte r14 = (byte) r14
                r14 = r14 & 255(0xff, float:3.57E-43)
                java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
                r12[r6] = r14
                int r6 = r10.intValue()
                int r6 = r6 >> 8
                byte r6 = (byte) r6
                r6 = r6 & 255(0xff, float:3.57E-43)
                java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
                r12[r7] = r6
                int r6 = r10.intValue()
                r6 = r6 & 255(0xff, float:3.57E-43)
                byte r6 = (byte) r6
                java.lang.Byte r6 = java.lang.Byte.valueOf(r6)
                r12[r3] = r6
                java.lang.String r3 = java.lang.String.format(r9, r12)
                java.lang.String r3 = r3.toLowerCase()
                goto L_0x00f2
            L_0x00f1:
                r3 = r2
            L_0x00f2:
                if (r3 == 0) goto L_0x011c
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                r6.append(r13)
                java.lang.String r7 = "-"
                r6.append(r7)
                r6.append(r3)
                java.lang.String r3 = r6.toString()
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                r6.append(r3)
                java.lang.String r3 = "&rotation="
                r6.append(r3)
                r6.append(r11)
                java.lang.String r13 = r6.toString()
            L_0x011c:
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r6 = "https://attheme.org?slug="
                r3.append(r6)
                java.lang.String r6 = r15.patternSlug
                r3.append(r6)
                java.lang.String r6 = "&intensity="
                r3.append(r6)
                float r6 = r15.patternIntensity
                r7 = 1120403456(0x42CLASSNAME, float:100.0)
                float r6 = r6 * r7
                int r6 = (int) r6
                r3.append(r6)
                java.lang.String r6 = "&bg_color="
                r3.append(r6)
                r3.append(r13)
                java.lang.String r3 = r3.toString()
                int r6 = r0.length()
                if (r6 <= 0) goto L_0x0166
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                r6.append(r3)
                java.lang.String r3 = "&mode="
                r6.append(r3)
                java.lang.String r0 = r0.toString()
                r6.append(r0)
                java.lang.String r3 = r6.toString()
                goto L_0x0166
            L_0x0165:
                r3 = r2
            L_0x0166:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.util.Set r4 = r4.entrySet()
                java.util.Iterator r4 = r4.iterator()
            L_0x0173:
                boolean r6 = r4.hasNext()
                java.lang.String r7 = "\n"
                if (r6 == 0) goto L_0x01a9
                java.lang.Object r6 = r4.next()
                java.util.Map$Entry r6 = (java.util.Map.Entry) r6
                java.lang.Object r9 = r6.getKey()
                java.lang.String r9 = (java.lang.String) r9
                if (r3 == 0) goto L_0x0196
                boolean r10 = r8.equals(r9)
                if (r10 != 0) goto L_0x0173
                boolean r10 = r5.equals(r9)
                if (r10 == 0) goto L_0x0196
                goto L_0x0173
            L_0x0196:
                r0.append(r9)
                java.lang.String r9 = "="
                r0.append(r9)
                java.lang.Object r6 = r6.getValue()
                r0.append(r6)
                r0.append(r7)
                goto L_0x0173
            L_0x01a9:
                java.io.FileOutputStream r4 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x01e6 }
                r4.<init>(r1)     // Catch:{ Exception -> 0x01e6 }
                java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x01e1, all -> 0x01de }
                byte[] r0 = org.telegram.messenger.AndroidUtilities.getStringBytes(r0)     // Catch:{ Exception -> 0x01e1, all -> 0x01de }
                r4.write(r0)     // Catch:{ Exception -> 0x01e1, all -> 0x01de }
                boolean r0 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x01e1, all -> 0x01de }
                if (r0 != 0) goto L_0x01da
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01e1, all -> 0x01de }
                r0.<init>()     // Catch:{ Exception -> 0x01e1, all -> 0x01de }
                java.lang.String r2 = "WLS="
                r0.append(r2)     // Catch:{ Exception -> 0x01e1, all -> 0x01de }
                r0.append(r3)     // Catch:{ Exception -> 0x01e1, all -> 0x01de }
                r0.append(r7)     // Catch:{ Exception -> 0x01e1, all -> 0x01de }
                java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x01e1, all -> 0x01de }
                byte[] r0 = org.telegram.messenger.AndroidUtilities.getStringBytes(r0)     // Catch:{ Exception -> 0x01e1, all -> 0x01de }
                r4.write(r0)     // Catch:{ Exception -> 0x01e1, all -> 0x01de }
            L_0x01da:
                r4.close()     // Catch:{ Exception -> 0x01f0 }
                goto L_0x01f4
            L_0x01de:
                r0 = move-exception
                r2 = r4
                goto L_0x01f5
            L_0x01e1:
                r0 = move-exception
                r2 = r4
                goto L_0x01e7
            L_0x01e4:
                r0 = move-exception
                goto L_0x01f5
            L_0x01e6:
                r0 = move-exception
            L_0x01e7:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x01e4 }
                if (r2 == 0) goto L_0x01f4
                r2.close()     // Catch:{ Exception -> 0x01f0 }
                goto L_0x01f4
            L_0x01f0:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x01f4:
                return r1
            L_0x01f5:
                if (r2 == 0) goto L_0x01ff
                r2.close()     // Catch:{ Exception -> 0x01fb }
                goto L_0x01ff
            L_0x01fb:
                r1 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
            L_0x01ff:
                goto L_0x0201
            L_0x0200:
                throw r0
            L_0x0201:
                goto L_0x0200
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.ThemeAccent.saveToFile():java.io.File");
        }
    }

    public static class OverrideWallpaperInfo {
        public int color;
        public String fileName = "";
        public int gradientColor;
        public float intensity;
        public boolean isBlurred;
        public boolean isMotion;
        public String originalFileName = "";
        public ThemeAccent parentAccent;
        public ThemeInfo parentTheme;
        public int rotation;
        public String slug = "";

        public OverrideWallpaperInfo() {
        }

        public OverrideWallpaperInfo(OverrideWallpaperInfo overrideWallpaperInfo, ThemeInfo themeInfo, ThemeAccent themeAccent) {
            this.slug = overrideWallpaperInfo.slug;
            this.color = overrideWallpaperInfo.color;
            this.gradientColor = overrideWallpaperInfo.gradientColor;
            this.rotation = overrideWallpaperInfo.rotation;
            this.isBlurred = overrideWallpaperInfo.isBlurred;
            this.isMotion = overrideWallpaperInfo.isMotion;
            this.intensity = overrideWallpaperInfo.intensity;
            this.parentTheme = themeInfo;
            this.parentAccent = themeAccent;
            if (!TextUtils.isEmpty(overrideWallpaperInfo.fileName)) {
                try {
                    File file = new File(ApplicationLoader.getFilesDirFixed(), overrideWallpaperInfo.fileName);
                    File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                    String generateWallpaperName = this.parentTheme.generateWallpaperName(this.parentAccent, false);
                    this.fileName = generateWallpaperName;
                    AndroidUtilities.copyFile(file, new File(filesDirFixed, generateWallpaperName));
                } catch (Exception e) {
                    this.fileName = "";
                    FileLog.e((Throwable) e);
                }
            } else {
                this.fileName = "";
            }
            if (TextUtils.isEmpty(overrideWallpaperInfo.originalFileName)) {
                this.originalFileName = "";
            } else if (!overrideWallpaperInfo.originalFileName.equals(overrideWallpaperInfo.fileName)) {
                try {
                    File file2 = new File(ApplicationLoader.getFilesDirFixed(), overrideWallpaperInfo.originalFileName);
                    File filesDirFixed2 = ApplicationLoader.getFilesDirFixed();
                    String generateWallpaperName2 = this.parentTheme.generateWallpaperName(this.parentAccent, true);
                    this.originalFileName = generateWallpaperName2;
                    AndroidUtilities.copyFile(file2, new File(filesDirFixed2, generateWallpaperName2));
                } catch (Exception e2) {
                    this.originalFileName = "";
                    FileLog.e((Throwable) e2);
                }
            } else {
                this.originalFileName = this.fileName;
            }
        }

        public boolean isDefault() {
            return "d".equals(this.slug);
        }

        public boolean isColor() {
            return "c".equals(this.slug);
        }

        public boolean isTheme() {
            return "t".equals(this.slug);
        }

        public void saveOverrideWallpaper() {
            ThemeInfo themeInfo = this.parentTheme;
            if (themeInfo == null) {
                return;
            }
            if (this.parentAccent != null || themeInfo.overrideWallpaper == this) {
                ThemeAccent themeAccent = this.parentAccent;
                if (themeAccent == null || themeAccent.overrideWallpaper == this) {
                    save();
                }
            }
        }

        private String getKey() {
            if (this.parentAccent != null) {
                return this.parentTheme.name + "_" + this.parentAccent.id + "_owp";
            }
            return this.parentTheme.name + "_owp";
        }

        /* access modifiers changed from: private */
        public void save() {
            try {
                String key = getKey();
                SharedPreferences.Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("wall", this.fileName);
                jSONObject.put("owall", this.originalFileName);
                jSONObject.put("pColor", this.color);
                jSONObject.put("pGrColor", this.gradientColor);
                jSONObject.put("pGrAngle", this.rotation);
                jSONObject.put("wallSlug", this.slug != null ? this.slug : "");
                jSONObject.put("wBlur", this.isBlurred);
                jSONObject.put("wMotion", this.isMotion);
                jSONObject.put("pIntensity", (double) this.intensity);
                edit.putString(key, jSONObject.toString());
                edit.commit();
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }

        /* access modifiers changed from: private */
        public void delete() {
            ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit().remove(getKey()).commit();
            new File(ApplicationLoader.getFilesDirFixed(), this.fileName).delete();
            new File(ApplicationLoader.getFilesDirFixed(), this.originalFileName).delete();
        }
    }

    public static class ThemeInfo implements NotificationCenter.NotificationCenterDelegate {
        public int accentBaseColor;
        public LongSparseArray<ThemeAccent> accentsByThemeId;
        public int account;
        public String assetName;
        public boolean badWallpaper;
        public int currentAccentId;
        public int defaultAccentCount;
        public boolean firstAccentIsDefault;
        public TLRPC$TL_theme info;
        public boolean isBlured;
        public boolean isMotion;
        public int lastAccentId = 100;
        public boolean loaded = true;
        private String loadingThemeWallpaperName;
        public String name;
        private String newPathToWallpaper;
        public OverrideWallpaperInfo overrideWallpaper;
        public String pathToFile;
        public String pathToWallpaper;
        public int patternBgColor;
        public int patternBgGradientColor;
        public int patternBgGradientRotation = 45;
        public int patternIntensity;
        public int prevAccentId = -1;
        /* access modifiers changed from: private */
        public int previewBackgroundColor;
        public int previewBackgroundGradientColor;
        /* access modifiers changed from: private */
        public int previewInColor;
        /* access modifiers changed from: private */
        public int previewOutColor;
        public boolean previewParsed;
        public int previewWallpaperOffset;
        public String slug;
        public int sortIndex;
        public ArrayList<ThemeAccent> themeAccents;
        public SparseArray<ThemeAccent> themeAccentsMap;
        public boolean themeLoaded = true;
        public TLRPC$InputFile uploadedFile;
        public TLRPC$InputFile uploadedThumb;
        public String uploadingFile;
        public String uploadingThumb;

        ThemeInfo() {
        }

        /* access modifiers changed from: package-private */
        public JSONObject getSaveJson() {
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("name", this.name);
                jSONObject.put("path", this.pathToFile);
                jSONObject.put("account", this.account);
                if (this.info != null) {
                    SerializedData serializedData = new SerializedData(this.info.getObjectSize());
                    this.info.serializeToStream(serializedData);
                    jSONObject.put("info", Utilities.bytesToHex(serializedData.toByteArray()));
                }
                jSONObject.put("loaded", this.loaded);
                return jSONObject;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return null;
            }
        }

        /* access modifiers changed from: private */
        public void loadWallpapers(SharedPreferences sharedPreferences) {
            ArrayList<ThemeAccent> arrayList = this.themeAccents;
            if (arrayList == null || arrayList.isEmpty()) {
                loadOverrideWallpaper(sharedPreferences, (ThemeAccent) null, this.name + "_owp");
                return;
            }
            int size = this.themeAccents.size();
            for (int i = 0; i < size; i++) {
                ThemeAccent themeAccent = this.themeAccents.get(i);
                loadOverrideWallpaper(sharedPreferences, themeAccent, this.name + "_" + themeAccent.id + "_owp");
            }
        }

        private void loadOverrideWallpaper(SharedPreferences sharedPreferences, ThemeAccent themeAccent, String str) {
            try {
                String string = sharedPreferences.getString(str, (String) null);
                if (!TextUtils.isEmpty(string)) {
                    JSONObject jSONObject = new JSONObject(string);
                    OverrideWallpaperInfo overrideWallpaperInfo = new OverrideWallpaperInfo();
                    overrideWallpaperInfo.fileName = jSONObject.getString("wall");
                    overrideWallpaperInfo.originalFileName = jSONObject.getString("owall");
                    overrideWallpaperInfo.color = jSONObject.getInt("pColor");
                    overrideWallpaperInfo.gradientColor = jSONObject.getInt("pGrColor");
                    overrideWallpaperInfo.rotation = jSONObject.getInt("pGrAngle");
                    overrideWallpaperInfo.slug = jSONObject.getString("wallSlug");
                    overrideWallpaperInfo.isBlurred = jSONObject.getBoolean("wBlur");
                    overrideWallpaperInfo.isMotion = jSONObject.getBoolean("wMotion");
                    overrideWallpaperInfo.intensity = (float) jSONObject.getDouble("pIntensity");
                    overrideWallpaperInfo.parentTheme = this;
                    overrideWallpaperInfo.parentAccent = themeAccent;
                    if (themeAccent != null) {
                        themeAccent.overrideWallpaper = overrideWallpaperInfo;
                    } else {
                        this.overrideWallpaper = overrideWallpaperInfo;
                    }
                    if (jSONObject.has("wallId") && jSONObject.getLong("wallId") == 1000001) {
                        overrideWallpaperInfo.slug = "d";
                    }
                }
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }

        public void setOverrideWallpaper(OverrideWallpaperInfo overrideWallpaperInfo) {
            if (this.overrideWallpaper != overrideWallpaperInfo) {
                ThemeAccent accent = getAccent(false);
                OverrideWallpaperInfo overrideWallpaperInfo2 = this.overrideWallpaper;
                if (overrideWallpaperInfo2 != null) {
                    overrideWallpaperInfo2.delete();
                }
                if (overrideWallpaperInfo != null) {
                    overrideWallpaperInfo.parentAccent = accent;
                    overrideWallpaperInfo.parentTheme = this;
                    overrideWallpaperInfo.save();
                }
                this.overrideWallpaper = overrideWallpaperInfo;
                if (accent != null) {
                    accent.overrideWallpaper = overrideWallpaperInfo;
                }
            }
        }

        public String getName() {
            if ("Blue".equals(this.name)) {
                return LocaleController.getString("ThemeClassic", NUM);
            }
            if ("Dark Blue".equals(this.name)) {
                return LocaleController.getString("ThemeDark", NUM);
            }
            if ("Arctic Blue".equals(this.name)) {
                return LocaleController.getString("ThemeArcticBlue", NUM);
            }
            if ("Day".equals(this.name)) {
                return LocaleController.getString("ThemeDay", NUM);
            }
            if ("Night".equals(this.name)) {
                return LocaleController.getString("ThemeNight", NUM);
            }
            TLRPC$TL_theme tLRPC$TL_theme = this.info;
            return tLRPC$TL_theme != null ? tLRPC$TL_theme.title : this.name;
        }

        public void setCurrentAccentId(int i) {
            this.currentAccentId = i;
            ThemeAccent accent = getAccent(false);
            if (accent != null) {
                this.overrideWallpaper = accent.overrideWallpaper;
            }
        }

        public String generateWallpaperName(ThemeAccent themeAccent, boolean z) {
            StringBuilder sb;
            StringBuilder sb2;
            if (themeAccent == null) {
                themeAccent = getAccent(false);
            }
            if (themeAccent != null) {
                StringBuilder sb3 = new StringBuilder();
                if (z) {
                    sb2 = new StringBuilder();
                    sb2.append(this.name);
                    sb2.append("_");
                    sb2.append(themeAccent.id);
                    sb2.append("_wp_o");
                } else {
                    sb2 = new StringBuilder();
                    sb2.append(this.name);
                    sb2.append("_");
                    sb2.append(themeAccent.id);
                    sb2.append("_wp");
                }
                sb3.append(sb2.toString());
                sb3.append(Utilities.random.nextInt());
                sb3.append(".jpg");
                return sb3.toString();
            }
            StringBuilder sb4 = new StringBuilder();
            if (z) {
                sb = new StringBuilder();
                sb.append(this.name);
                sb.append("_wp_o");
            } else {
                sb = new StringBuilder();
                sb.append(this.name);
                sb.append("_wp");
            }
            sb4.append(sb.toString());
            sb4.append(Utilities.random.nextInt());
            sb4.append(".jpg");
            return sb4.toString();
        }

        public void setPreviewInColor(int i) {
            this.previewInColor = i;
        }

        public void setPreviewOutColor(int i) {
            this.previewOutColor = i;
        }

        public void setPreviewBackgroundColor(int i) {
            this.previewBackgroundColor = i;
        }

        public int getPreviewInColor() {
            if (!this.firstAccentIsDefault || this.currentAccentId != Theme.DEFALT_THEME_ACCENT_ID) {
                return this.previewInColor;
            }
            return -1;
        }

        public int getPreviewOutColor() {
            if (!this.firstAccentIsDefault || this.currentAccentId != Theme.DEFALT_THEME_ACCENT_ID) {
                return this.previewOutColor;
            }
            return -983328;
        }

        public int getPreviewBackgroundColor() {
            if (!this.firstAccentIsDefault || this.currentAccentId != Theme.DEFALT_THEME_ACCENT_ID) {
                return this.previewBackgroundColor;
            }
            return -3155485;
        }

        /* access modifiers changed from: private */
        public boolean isDefaultMyMessages() {
            if (!this.firstAccentIsDefault) {
                return false;
            }
            int i = this.currentAccentId;
            int i2 = Theme.DEFALT_THEME_ACCENT_ID;
            if (i == i2) {
                return true;
            }
            ThemeAccent themeAccent = this.themeAccentsMap.get(i2);
            ThemeAccent themeAccent2 = this.themeAccentsMap.get(this.currentAccentId);
            if (themeAccent == null || themeAccent2 == null || themeAccent.myMessagesAccentColor != themeAccent2.myMessagesAccentColor || themeAccent.myMessagesGradientAccentColor != themeAccent2.myMessagesGradientAccentColor) {
                return false;
            }
            return true;
        }

        /* access modifiers changed from: private */
        public boolean isDefaultMainAccent() {
            if (!this.firstAccentIsDefault) {
                return false;
            }
            int i = this.currentAccentId;
            int i2 = Theme.DEFALT_THEME_ACCENT_ID;
            if (i == i2) {
                return true;
            }
            ThemeAccent themeAccent = this.themeAccentsMap.get(i2);
            ThemeAccent themeAccent2 = this.themeAccentsMap.get(this.currentAccentId);
            if (themeAccent2 == null || themeAccent.accentColor != themeAccent2.accentColor) {
                return false;
            }
            return true;
        }

        public boolean hasAccentColors() {
            return this.defaultAccentCount != 0;
        }

        public boolean isDark() {
            return "Dark Blue".equals(this.name) || "Night".equals(this.name);
        }

        public boolean isLight() {
            return this.pathToFile == null && !isDark();
        }

        public String getKey() {
            if (this.info == null) {
                return this.name;
            }
            return "remote" + this.info.id;
        }

        static ThemeInfo createWithJson(JSONObject jSONObject) {
            if (jSONObject == null) {
                return null;
            }
            try {
                ThemeInfo themeInfo = new ThemeInfo();
                themeInfo.name = jSONObject.getString("name");
                themeInfo.pathToFile = jSONObject.getString("path");
                if (jSONObject.has("account")) {
                    themeInfo.account = jSONObject.getInt("account");
                }
                if (jSONObject.has("info")) {
                    try {
                        SerializedData serializedData = new SerializedData(Utilities.hexToBytes(jSONObject.getString("info")));
                        themeInfo.info = (TLRPC$TL_theme) TLRPC$Theme.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                }
                if (jSONObject.has("loaded")) {
                    themeInfo.loaded = jSONObject.getBoolean("loaded");
                }
                return themeInfo;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return null;
            }
        }

        static ThemeInfo createWithString(String str) {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            String[] split = str.split("\\|");
            if (split.length != 2) {
                return null;
            }
            ThemeInfo themeInfo = new ThemeInfo();
            themeInfo.name = split[0];
            themeInfo.pathToFile = split[1];
            return themeInfo;
        }

        /* access modifiers changed from: private */
        public void setAccentColorOptions(int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4, int[] iArr5, int[] iArr6, String[] strArr, int[] iArr7, int[] iArr8) {
            int[] iArr9 = iArr;
            this.defaultAccentCount = iArr9.length;
            this.themeAccents = new ArrayList<>();
            this.themeAccentsMap = new SparseArray<>();
            this.accentsByThemeId = new LongSparseArray<>();
            for (int i = 0; i < iArr9.length; i++) {
                ThemeAccent themeAccent = new ThemeAccent();
                themeAccent.id = iArr6 != null ? iArr6[i] : i;
                themeAccent.accentColor = iArr9[i];
                themeAccent.parentTheme = this;
                if (iArr2 != null) {
                    themeAccent.myMessagesAccentColor = iArr2[i];
                }
                if (iArr3 != null) {
                    themeAccent.myMessagesGradientAccentColor = iArr3[i];
                }
                if (iArr4 != null) {
                    themeAccent.backgroundOverrideColor = (long) iArr4[i];
                    if (!this.firstAccentIsDefault || themeAccent.id != Theme.DEFALT_THEME_ACCENT_ID) {
                        themeAccent.backgroundOverrideColor = (long) iArr4[i];
                    } else {
                        themeAccent.backgroundOverrideColor = 4294967296L;
                    }
                }
                if (iArr5 != null) {
                    if (!this.firstAccentIsDefault || themeAccent.id != Theme.DEFALT_THEME_ACCENT_ID) {
                        themeAccent.backgroundGradientOverrideColor = (long) iArr5[i];
                    } else {
                        themeAccent.backgroundGradientOverrideColor = 4294967296L;
                    }
                }
                if (strArr != null) {
                    themeAccent.patternIntensity = ((float) iArr8[i]) / 100.0f;
                    themeAccent.backgroundRotation = iArr7[i];
                    themeAccent.patternSlug = strArr[i];
                }
                this.themeAccentsMap.put(themeAccent.id, themeAccent);
                this.themeAccents.add(themeAccent);
            }
            this.accentBaseColor = this.themeAccentsMap.get(0).accentColor;
        }

        /* access modifiers changed from: private */
        public void loadThemeDocument() {
            this.loaded = false;
            this.loadingThemeWallpaperName = null;
            this.newPathToWallpaper = null;
            addObservers();
            FileLoader instance = FileLoader.getInstance(this.account);
            TLRPC$TL_theme tLRPC$TL_theme = this.info;
            instance.loadFile(tLRPC$TL_theme.document, tLRPC$TL_theme, 1, 1);
        }

        private void addObservers() {
            NotificationCenter.getInstance(this.account).addObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(this.account).addObserver(this, NotificationCenter.fileDidFailToLoad);
        }

        /* access modifiers changed from: private */
        public void removeObservers() {
            NotificationCenter.getInstance(this.account).removeObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(this.account).removeObserver(this, NotificationCenter.fileDidFailToLoad);
        }

        /* access modifiers changed from: private */
        public void onFinishLoadingRemoteTheme() {
            this.loaded = true;
            boolean z = false;
            this.previewParsed = false;
            Theme.saveOtherThemes(true);
            if (this == Theme.currentTheme && Theme.previousTheme == null) {
                NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                int i = NotificationCenter.needSetDayNightTheme;
                Object[] objArr = new Object[4];
                objArr[0] = this;
                if (this == Theme.currentNightTheme) {
                    z = true;
                }
                objArr[1] = Boolean.valueOf(z);
                objArr[2] = null;
                objArr[3] = -1;
                globalInstance.postNotificationName(i, objArr);
            }
        }

        public static boolean accentEquals(ThemeAccent themeAccent, TLRPC$TL_themeSettings tLRPC$TL_themeSettings) {
            long j;
            int i;
            int i2;
            TLRPC$WallPaperSettings tLRPC$WallPaperSettings;
            int i3 = tLRPC$TL_themeSettings.message_top_color;
            if (tLRPC$TL_themeSettings.message_bottom_color == i3) {
                i3 = 0;
            }
            String str = null;
            float f = 0.0f;
            TLRPC$WallPaper tLRPC$WallPaper = tLRPC$TL_themeSettings.wallpaper;
            if (tLRPC$WallPaper == null || (tLRPC$WallPaperSettings = tLRPC$WallPaper.settings) == null) {
                j = 0;
                i2 = 0;
                i = 0;
            } else {
                i2 = tLRPC$WallPaperSettings.background_color;
                int i4 = tLRPC$WallPaperSettings.second_background_color;
                j = i4 == 0 ? 4294967296L : (long) i4;
                i = AndroidUtilities.getWallpaperRotation(tLRPC$TL_themeSettings.wallpaper.settings.rotation, false);
                TLRPC$WallPaper tLRPC$WallPaper2 = tLRPC$TL_themeSettings.wallpaper;
                if (!(tLRPC$WallPaper2 instanceof TLRPC$TL_wallPaperNoFile) && tLRPC$WallPaper2.pattern) {
                    str = tLRPC$WallPaper2.slug;
                    f = ((float) tLRPC$WallPaper2.settings.intensity) / 100.0f;
                }
            }
            if (tLRPC$TL_themeSettings.accent_color == themeAccent.accentColor && tLRPC$TL_themeSettings.message_bottom_color == themeAccent.myMessagesAccentColor && i3 == themeAccent.myMessagesGradientAccentColor && ((long) i2) == themeAccent.backgroundOverrideColor && j == themeAccent.backgroundGradientOverrideColor && i == themeAccent.backgroundRotation && TextUtils.equals(str, themeAccent.patternSlug) && ((double) Math.abs(f - themeAccent.patternIntensity)) < 0.001d) {
                return true;
            }
            return false;
        }

        public static void fillAccentValues(ThemeAccent themeAccent, TLRPC$TL_themeSettings tLRPC$TL_themeSettings) {
            TLRPC$WallPaperSettings tLRPC$WallPaperSettings;
            themeAccent.accentColor = tLRPC$TL_themeSettings.accent_color;
            int i = tLRPC$TL_themeSettings.message_bottom_color;
            themeAccent.myMessagesAccentColor = i;
            int i2 = tLRPC$TL_themeSettings.message_top_color;
            themeAccent.myMessagesGradientAccentColor = i2;
            if (i == i2) {
                themeAccent.myMessagesGradientAccentColor = 0;
            }
            TLRPC$WallPaper tLRPC$WallPaper = tLRPC$TL_themeSettings.wallpaper;
            if (tLRPC$WallPaper != null && (tLRPC$WallPaperSettings = tLRPC$WallPaper.settings) != null) {
                themeAccent.backgroundOverrideColor = (long) tLRPC$WallPaperSettings.background_color;
                int i3 = tLRPC$WallPaperSettings.second_background_color;
                if (i3 == 0) {
                    themeAccent.backgroundGradientOverrideColor = 4294967296L;
                } else {
                    themeAccent.backgroundGradientOverrideColor = (long) i3;
                }
                themeAccent.backgroundRotation = AndroidUtilities.getWallpaperRotation(tLRPC$TL_themeSettings.wallpaper.settings.rotation, false);
                TLRPC$WallPaper tLRPC$WallPaper2 = tLRPC$TL_themeSettings.wallpaper;
                if (!(tLRPC$WallPaper2 instanceof TLRPC$TL_wallPaperNoFile) && tLRPC$WallPaper2.pattern) {
                    themeAccent.patternSlug = tLRPC$WallPaper2.slug;
                    TLRPC$WallPaperSettings tLRPC$WallPaperSettings2 = tLRPC$WallPaper2.settings;
                    themeAccent.patternIntensity = ((float) tLRPC$WallPaperSettings2.intensity) / 100.0f;
                    themeAccent.patternMotion = tLRPC$WallPaperSettings2.motion;
                }
            }
        }

        public ThemeAccent createNewAccent(TLRPC$TL_themeSettings tLRPC$TL_themeSettings) {
            ThemeAccent themeAccent = new ThemeAccent();
            fillAccentValues(themeAccent, tLRPC$TL_themeSettings);
            themeAccent.parentTheme = this;
            return themeAccent;
        }

        public ThemeAccent createNewAccent(TLRPC$TL_theme tLRPC$TL_theme, int i) {
            if (tLRPC$TL_theme == null) {
                return null;
            }
            ThemeAccent themeAccent = this.accentsByThemeId.get(tLRPC$TL_theme.id);
            if (themeAccent != null) {
                return themeAccent;
            }
            int i2 = this.lastAccentId + 1;
            this.lastAccentId = i2;
            ThemeAccent createNewAccent = createNewAccent(tLRPC$TL_theme.settings);
            createNewAccent.id = i2;
            createNewAccent.info = tLRPC$TL_theme;
            createNewAccent.account = i;
            this.themeAccentsMap.put(i2, createNewAccent);
            this.themeAccents.add(0, createNewAccent);
            this.accentsByThemeId.put(tLRPC$TL_theme.id, createNewAccent);
            return createNewAccent;
        }

        public ThemeAccent getAccent(boolean z) {
            if (this.themeAccents == null) {
                return null;
            }
            ThemeAccent themeAccent = this.themeAccentsMap.get(this.currentAccentId);
            if (!z) {
                return themeAccent;
            }
            int i = this.lastAccentId + 1;
            this.lastAccentId = i;
            ThemeAccent themeAccent2 = new ThemeAccent();
            themeAccent2.accentColor = themeAccent.accentColor;
            themeAccent2.myMessagesAccentColor = themeAccent.myMessagesAccentColor;
            themeAccent2.myMessagesGradientAccentColor = themeAccent.myMessagesGradientAccentColor;
            themeAccent2.backgroundOverrideColor = themeAccent.backgroundOverrideColor;
            themeAccent2.backgroundGradientOverrideColor = themeAccent.backgroundGradientOverrideColor;
            themeAccent2.backgroundRotation = themeAccent.backgroundRotation;
            themeAccent2.patternSlug = themeAccent.patternSlug;
            themeAccent2.patternIntensity = themeAccent.patternIntensity;
            themeAccent2.patternMotion = themeAccent.patternMotion;
            themeAccent2.parentTheme = this;
            OverrideWallpaperInfo overrideWallpaperInfo = this.overrideWallpaper;
            if (overrideWallpaperInfo != null) {
                themeAccent2.overrideWallpaper = new OverrideWallpaperInfo(overrideWallpaperInfo, this, themeAccent2);
            }
            this.prevAccentId = this.currentAccentId;
            themeAccent2.id = i;
            this.currentAccentId = i;
            this.overrideWallpaper = themeAccent2.overrideWallpaper;
            this.themeAccentsMap.put(i, themeAccent2);
            this.themeAccents.add(0, themeAccent2);
            return themeAccent2;
        }

        public int getAccentColor(int i) {
            ThemeAccent themeAccent = this.themeAccentsMap.get(i);
            if (themeAccent != null) {
                return themeAccent.accentColor;
            }
            return 0;
        }

        public boolean createBackground(File file, String str) {
            int i;
            try {
                Bitmap scaledBitmap = ThemesHorizontalListCell.getScaledBitmap((float) AndroidUtilities.dp(640.0f), (float) AndroidUtilities.dp(360.0f), file.getAbsolutePath(), (String) null, 0);
                if (!(scaledBitmap == null || this.patternBgColor == 0)) {
                    Bitmap createBitmap = Bitmap.createBitmap(scaledBitmap.getWidth(), scaledBitmap.getHeight(), scaledBitmap.getConfig());
                    Canvas canvas = new Canvas(createBitmap);
                    if (this.patternBgGradientColor != 0) {
                        i = AndroidUtilities.getAverageColor(this.patternBgColor, this.patternBgGradientColor);
                        GradientDrawable gradientDrawable = new GradientDrawable(BackgroundGradientDrawable.getGradientOrientation(this.patternBgGradientRotation), new int[]{this.patternBgColor, this.patternBgGradientColor});
                        gradientDrawable.setBounds(0, 0, createBitmap.getWidth(), createBitmap.getHeight());
                        gradientDrawable.draw(canvas);
                    } else {
                        i = AndroidUtilities.getPatternColor(this.patternBgColor);
                        canvas.drawColor(this.patternBgColor);
                    }
                    Paint paint = new Paint(2);
                    paint.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN));
                    paint.setAlpha((int) ((((float) this.patternIntensity) / 100.0f) * 255.0f));
                    canvas.drawBitmap(scaledBitmap, 0.0f, 0.0f, paint);
                    canvas.setBitmap((Bitmap) null);
                    scaledBitmap = createBitmap;
                }
                if (this.isBlured) {
                    scaledBitmap = Utilities.blurWallpaper(scaledBitmap);
                }
                FileOutputStream fileOutputStream = new FileOutputStream(str);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 87, fileOutputStream);
                fileOutputStream.close();
                return true;
            } catch (Throwable th) {
                FileLog.e(th);
                return false;
            }
        }

        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (i == NotificationCenter.fileDidLoad || i == NotificationCenter.fileDidFailToLoad) {
                String str = objArr[0];
                TLRPC$TL_theme tLRPC$TL_theme = this.info;
                if (tLRPC$TL_theme != null && tLRPC$TL_theme.document != null) {
                    if (str.equals(this.loadingThemeWallpaperName)) {
                        this.loadingThemeWallpaperName = null;
                        Utilities.globalQueue.postRunnable(new Runnable(objArr[1]) {
                            private final /* synthetic */ File f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                Theme.ThemeInfo.this.lambda$didReceivedNotification$0$Theme$ThemeInfo(this.f$1);
                            }
                        });
                    } else if (str.equals(FileLoader.getAttachFileName(this.info.document))) {
                        removeObservers();
                        if (i == NotificationCenter.fileDidLoad) {
                            File file = new File(this.pathToFile);
                            TLRPC$TL_theme tLRPC$TL_theme2 = this.info;
                            ThemeInfo fillThemeValues = Theme.fillThemeValues(file, tLRPC$TL_theme2.title, tLRPC$TL_theme2);
                            if (fillThemeValues == null || fillThemeValues.pathToWallpaper == null || new File(fillThemeValues.pathToWallpaper).exists()) {
                                onFinishLoadingRemoteTheme();
                                return;
                            }
                            this.patternBgColor = fillThemeValues.patternBgColor;
                            this.patternBgGradientColor = fillThemeValues.patternBgGradientColor;
                            this.patternBgGradientRotation = fillThemeValues.patternBgGradientRotation;
                            this.isBlured = fillThemeValues.isBlured;
                            this.patternIntensity = fillThemeValues.patternIntensity;
                            this.newPathToWallpaper = fillThemeValues.pathToWallpaper;
                            TLRPC$TL_account_getWallPaper tLRPC$TL_account_getWallPaper = new TLRPC$TL_account_getWallPaper();
                            TLRPC$TL_inputWallPaperSlug tLRPC$TL_inputWallPaperSlug = new TLRPC$TL_inputWallPaperSlug();
                            tLRPC$TL_inputWallPaperSlug.slug = fillThemeValues.slug;
                            tLRPC$TL_account_getWallPaper.wallpaper = tLRPC$TL_inputWallPaperSlug;
                            ConnectionsManager.getInstance(fillThemeValues.account).sendRequest(tLRPC$TL_account_getWallPaper, new RequestDelegate(fillThemeValues) {
                                private final /* synthetic */ Theme.ThemeInfo f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                    Theme.ThemeInfo.this.lambda$didReceivedNotification$2$Theme$ThemeInfo(this.f$1, tLObject, tLRPC$TL_error);
                                }
                            });
                        }
                    }
                }
            }
        }

        public /* synthetic */ void lambda$didReceivedNotification$0$Theme$ThemeInfo(File file) {
            createBackground(file, this.newPathToWallpaper);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    Theme.ThemeInfo.this.onFinishLoadingRemoteTheme();
                }
            });
        }

        public /* synthetic */ void lambda$didReceivedNotification$2$Theme$ThemeInfo(ThemeInfo themeInfo, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable(tLObject, themeInfo) {
                private final /* synthetic */ TLObject f$1;
                private final /* synthetic */ Theme.ThemeInfo f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    Theme.ThemeInfo.this.lambda$null$1$Theme$ThemeInfo(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$null$1$Theme$ThemeInfo(TLObject tLObject, ThemeInfo themeInfo) {
            if (tLObject instanceof TLRPC$TL_wallPaper) {
                TLRPC$TL_wallPaper tLRPC$TL_wallPaper = (TLRPC$TL_wallPaper) tLObject;
                this.loadingThemeWallpaperName = FileLoader.getAttachFileName(tLRPC$TL_wallPaper.document);
                addObservers();
                FileLoader.getInstance(themeInfo.account).loadFile(tLRPC$TL_wallPaper.document, tLRPC$TL_wallPaper, 1, 1);
                return;
            }
            onFinishLoadingRemoteTheme();
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v319, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v324, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v242, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v328, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v329, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v332, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v282, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v338, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v341, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v344, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v345, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v348, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v366, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v367, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v368, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v369, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v375, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v376, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v377, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v378, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r5v22, types: [boolean] */
    /* JADX WARNING: type inference failed for: r5v24 */
    /* JADX WARNING: type inference failed for: r5v35 */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x2ba9, code lost:
        if (r0 == null) goto L_0x2bb7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x2bab, code lost:
        r0 = themesDict.get(r0);
        r9 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x2bb3, code lost:
        if (r0 == null) goto L_0x2bb7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x2bb5, code lost:
        currentNightTheme = r0;
        r9 = r9;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x2cdf A[Catch:{ all -> 0x2d07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x2ceb A[Catch:{ all -> 0x2d05 }] */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x2e1a A[Catch:{ Exception -> 0x2var_ }] */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x2e37 A[Catch:{ Exception -> 0x2var_ }] */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x2e3a A[Catch:{ Exception -> 0x2var_ }] */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x2e47 A[Catch:{ Exception -> 0x2var_ }] */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x2var_  */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x2var_  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x2var_  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x2f7e  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x2fbe  */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x2fdd  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x2fe1  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x2fe3  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x2e56 A[SYNTHETIC] */
    static {
        /*
            java.lang.Object r0 = new java.lang.Object
            r0.<init>()
            sync = r0
            java.lang.Object r0 = new java.lang.Object
            r0.<init>()
            wallpaperSync = r0
            org.telegram.ui.ActionBar.Theme$1 r0 = new org.telegram.ui.ActionBar.Theme$1
            r0.<init>()
            switchDayBrightnessRunnable = r0
            org.telegram.ui.ActionBar.Theme$2 r0 = new org.telegram.ui.ActionBar.Theme$2
            r0.<init>()
            switchNightBrightnessRunnable = r0
            r0 = 99
            DEFALT_THEME_ACCENT_ID = r0
            r1 = 0
            selectedAutoNightType = r1
            r0 = 1048576000(0x3e800000, float:0.25)
            autoNightBrighnessThreshold = r0
            r0 = 1320(0x528, float:1.85E-42)
            autoNightDayStartTime = r0
            r0 = 480(0x1e0, float:6.73E-43)
            autoNightDayEndTime = r0
            r0 = 1320(0x528, float:1.85E-42)
            autoNightSunsetTime = r0
            r2 = -1
            java.lang.Integer r0 = java.lang.Integer.valueOf(r2)
            autoNightLastSunCheckDay = r2
            r3 = 480(0x1e0, float:6.73E-43)
            autoNightSunriseTime = r3
            java.lang.String r3 = ""
            autoNightCityName = r3
            r4 = 4666723172467343360(0x40cNUM, double:10000.0)
            autoNightLocationLatitude = r4
            autoNightLocationLongitude = r4
            android.graphics.Paint r4 = new android.graphics.Paint
            r5 = 1
            r4.<init>(r5)
            maskPaint = r4
            r4 = 3
            boolean[] r6 = new boolean[r4]
            loadingRemoteThemes = r6
            int[] r6 = new int[r4]
            lastLoadingThemesTime = r6
            int[] r6 = new int[r4]
            remoteThemesHash = r6
            r6 = 10
            android.graphics.drawable.Drawable[] r6 = new android.graphics.drawable.Drawable[r6]
            avatarDrawables = r6
            r6 = 2
            android.graphics.drawable.Drawable[] r7 = new android.graphics.drawable.Drawable[r6]
            chat_pollCheckDrawable = r7
            android.graphics.drawable.Drawable[] r7 = new android.graphics.drawable.Drawable[r6]
            chat_pollCrossDrawable = r7
            r7 = 6
            android.graphics.drawable.Drawable[] r8 = new android.graphics.drawable.Drawable[r7]
            chat_attachButtonDrawables = r8
            android.graphics.drawable.Drawable[] r8 = new android.graphics.drawable.Drawable[r6]
            chat_locationDrawable = r8
            android.graphics.drawable.Drawable[] r8 = new android.graphics.drawable.Drawable[r6]
            chat_contactDrawable = r8
            r8 = 4
            android.graphics.drawable.Drawable[] r9 = new android.graphics.drawable.Drawable[r8]
            chat_cornerOuter = r9
            android.graphics.drawable.Drawable[] r9 = new android.graphics.drawable.Drawable[r8]
            chat_cornerInner = r9
            int[] r9 = new int[r6]
            r9 = {10, 2} // fill-array
            java.lang.Class<android.graphics.drawable.Drawable> r10 = android.graphics.drawable.Drawable.class
            java.lang.Object r9 = java.lang.reflect.Array.newInstance(r10, r9)
            android.graphics.drawable.Drawable[][] r9 = (android.graphics.drawable.Drawable[][]) r9
            chat_fileStatesDrawable = r9
            int[] r9 = new int[r6]
            r9 = {6, 2} // fill-array
            java.lang.Class<org.telegram.ui.Components.CombinedDrawable> r10 = org.telegram.ui.Components.CombinedDrawable.class
            java.lang.Object r9 = java.lang.reflect.Array.newInstance(r10, r9)
            org.telegram.ui.Components.CombinedDrawable[][] r9 = (org.telegram.ui.Components.CombinedDrawable[][]) r9
            chat_fileMiniStatesDrawable = r9
            int[] r9 = new int[r6]
            r9 = {13, 2} // fill-array
            java.lang.Class<android.graphics.drawable.Drawable> r10 = android.graphics.drawable.Drawable.class
            java.lang.Object r9 = java.lang.reflect.Array.newInstance(r10, r9)
            android.graphics.drawable.Drawable[][] r9 = (android.graphics.drawable.Drawable[][]) r9
            chat_photoStatesDrawables = r9
            android.graphics.Path[] r9 = new android.graphics.Path[r6]
            chat_filePath = r9
            r9 = 7
            java.lang.String[] r10 = new java.lang.String[r9]
            java.lang.String r11 = "avatar_backgroundRed"
            r10[r1] = r11
            java.lang.String r11 = "avatar_backgroundOrange"
            r10[r5] = r11
            java.lang.String r11 = "avatar_backgroundViolet"
            r10[r6] = r11
            java.lang.String r11 = "avatar_backgroundGreen"
            r10[r4] = r11
            java.lang.String r11 = "avatar_backgroundCyan"
            r10[r8] = r11
            java.lang.String r11 = "avatar_backgroundBlue"
            r12 = 5
            r10[r12] = r11
            java.lang.String r11 = "avatar_backgroundPink"
            r10[r7] = r11
            keys_avatar_background = r10
            java.lang.String[] r10 = new java.lang.String[r9]
            java.lang.String r11 = "avatar_nameInMessageRed"
            r10[r1] = r11
            java.lang.String r11 = "avatar_nameInMessageOrange"
            r10[r5] = r11
            java.lang.String r11 = "avatar_nameInMessageViolet"
            r10[r6] = r11
            java.lang.String r11 = "avatar_nameInMessageGreen"
            r10[r4] = r11
            java.lang.String r11 = "avatar_nameInMessageCyan"
            r10[r8] = r11
            java.lang.String r11 = "avatar_nameInMessageBlue"
            r10[r12] = r11
            java.lang.String r11 = "avatar_nameInMessagePink"
            r10[r7] = r11
            keys_avatar_nameInMessage = r10
            java.util.HashSet r10 = new java.util.HashSet
            r10.<init>()
            myMessagesColorKeys = r10
            java.util.HashMap r10 = new java.util.HashMap
            r10.<init>()
            defaultColors = r10
            java.util.HashMap r10 = new java.util.HashMap
            r10.<init>()
            fallbackKeys = r10
            java.util.HashSet r10 = new java.util.HashSet
            r10.<init>()
            themeAccentExclusionKeys = r10
            java.lang.ThreadLocal r10 = new java.lang.ThreadLocal
            r10.<init>()
            hsvTemp1Local = r10
            java.lang.ThreadLocal r10 = new java.lang.ThreadLocal
            r10.<init>()
            hsvTemp2Local = r10
            java.lang.ThreadLocal r10 = new java.lang.ThreadLocal
            r10.<init>()
            hsvTemp3Local = r10
            java.lang.ThreadLocal r10 = new java.lang.ThreadLocal
            r10.<init>()
            hsvTemp4Local = r10
            java.lang.ThreadLocal r10 = new java.lang.ThreadLocal
            r10.<init>()
            hsvTemp5Local = r10
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            java.lang.String r11 = "dialogBackground"
            r10.put(r11, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r11 = -986896(0xfffffffffff0f0f0, float:NaN)
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            java.lang.String r13 = "dialogBackgroundGray"
            r10.put(r13, r11)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r11 = -14540254(0xfffffffffvar_, float:-2.1551216E38)
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            java.lang.String r13 = "dialogTextBlack"
            r10.put(r13, r11)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -14255946(0xfffffffffvar_b6, float:-2.2127861E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogTextLink"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = 862104035(0x3362a9e3, float:5.2774237E-8)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogLinkSelection"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -3319206(0xffffffffffcd5a5a, float:NaN)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogTextRed"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -2213318(0xffffffffffde3a3a, float:NaN)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogTextRed2"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -13660983(0xffffffffff2f8cc9, float:-2.333459E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogTextBlue"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -12937771(0xffffffffff3a95d5, float:-2.4801438E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogTextBlue2"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -12664327(0xffffffffff3ec1f9, float:-2.5356048E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogTextBlue3"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -15095832(0xfffffffffvar_a7e8, float:-2.042437E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogTextBlue4"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -13333567(0xfffffffffvar_bc1, float:-2.3998668E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogTextGray"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -9079435(0xfffffffffvar_, float:-3.2627073E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogTextGray2"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -6710887(0xfffffffffvar_, float:NaN)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogTextGray3"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -5000269(0xffffffffffb3b3b3, float:NaN)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogTextGray4"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -6842473(0xfffffffffvar_, float:NaN)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogTextHint"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -9999504(0xfffffffffvar_b70, float:-3.0760951E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogIcon"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -2011827(0xffffffffffe14d4d, float:NaN)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogRedIcon"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -2960686(0xffffffffffd2d2d2, float:NaN)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogGrayLine"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -9456923(0xffffffffff6fb2e5, float:-3.1861436E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogTopBackground"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -2368549(0xffffffffffdbdbdb, float:NaN)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogInputField"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -13129232(0xfffffffffvar_a9f0, float:-2.4413109E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogInputFieldActivated"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -12345121(0xfffffffffvar_a0df, float:-2.6003475E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogCheckboxSquareBackground"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            java.lang.String r13 = "dialogCheckboxSquareCheck"
            r10.put(r13, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -9211021(0xfffffffffvar_, float:-3.2360185E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogCheckboxSquareUnchecked"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -5197648(0xffffffffffb0b0b0, float:NaN)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogCheckboxSquareDisabled"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -5000269(0xffffffffffb3b3b3, float:NaN)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogRadioBackground"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -13129232(0xfffffffffvar_a9f0, float:-2.4413109E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogRadioBackgroundChecked"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -14115349(0xfffffffffvar_deb, float:-2.2413026E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogProgressCircle"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -11371101(0xfffffffffvar_da3, float:-2.7979022E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogLineProgress"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -2368549(0xffffffffffdbdbdb, float:NaN)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogLineProgressBackground"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -11955764(0xfffffffffvar_cc, float:-2.6793185E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogButton"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = 251658240(0xvar_, float:6.3108872E-30)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogButtonSelector"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -657673(0xfffffffffff5f6f7, float:NaN)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogScrollGlow"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -11750155(0xffffffffff4cb4f5, float:-2.721021E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogRoundCheckBox"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            java.lang.String r13 = "dialogRoundCheckBoxCheck"
            r10.put(r13, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -12664327(0xffffffffff3ec1f9, float:-2.5356048E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogBadgeBackground"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            java.lang.String r13 = "dialogBadgeText"
            r10.put(r13, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            java.lang.String r13 = "dialogCameraIcon"
            r10.put(r13, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -151981323(0xfffffffff6f0f2f5, float:-2.4435137E33)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialog_inlineProgressBackground"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -9735304(0xffffffffff6b7378, float:-3.1296813E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialog_inlineProgress"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -854795(0xfffffffffff2f4f5, float:NaN)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogSearchBackground"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -6774617(0xfffffffffvar_a0a7, float:NaN)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogSearchHint"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -6182737(0xffffffffffa1a8af, float:NaN)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogSearchIcon"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            java.lang.String r13 = "dialogSearchText"
            r10.put(r13, r11)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -11750155(0xffffffffff4cb4f5, float:-2.721021E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogFloatingButton"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = 251658240(0xvar_, float:6.3108872E-30)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogFloatingButtonPressed"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            java.lang.String r13 = "dialogFloatingIcon"
            r10.put(r13, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = 301989888(0x12000000, float:4.0389678E-28)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogShadowLine"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -6314840(0xffffffffff9fa4a8, float:NaN)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogEmptyImage"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -7565164(0xffffffffff8CLASSNAME, float:NaN)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogEmptyText"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            java.lang.String r13 = "windowBackgroundWhite"
            r10.put(r13, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -6445135(0xffffffffff9da7b1, float:NaN)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "windowBackgroundUnchecked"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -11034919(0xfffffffffvar_ed9, float:-2.866088E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "windowBackgroundChecked"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            java.lang.String r13 = "windowBackgroundCheckText"
            r10.put(r13, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -14904349(0xffffffffff1CLASSNAMEe3, float:-2.0812744E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "progressCircle"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -8288629(0xfffffffffvar_b, float:NaN)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "windowBackgroundWhiteGrayIcon"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -12545331(0xfffffffffvar_cd, float:-2.55974E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "windowBackgroundWhiteBlueText"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -12937771(0xffffffffff3a95d5, float:-2.4801438E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "windowBackgroundWhiteBlueText2"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -14255946(0xfffffffffvar_b6, float:-2.2127861E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "windowBackgroundWhiteBlueText3"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -14904349(0xffffffffff1CLASSNAMEe3, float:-2.0812744E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "windowBackgroundWhiteBlueText4"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -11759926(0xffffffffff4c8eca, float:-2.7190391E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "windowBackgroundWhiteBlueText5"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -12940081(0xffffffffff3a8ccf, float:-2.4796753E38)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "windowBackgroundWhiteBlueText6"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -13141330(0xfffffffffvar_aae, float:-2.4388571E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteBlueText7"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -14776109(0xffffffffff1e88d3, float:-2.1072846E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteBlueButton"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -13132315(0xfffffffffvar_de5, float:-2.4406856E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteBlueIcon"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -14248148(0xfffffffffvar_c, float:-2.2143678E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteGreenText"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -13129704(0xfffffffffvar_a818, float:-2.4412152E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteGreenText2"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -3319206(0xffffffffffcd5a5a, float:NaN)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteRedText"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -2404015(0xffffffffffdb5151, float:NaN)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteRedText2"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -2995895(0xffffffffffd24949, float:NaN)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteRedText3"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -3198928(0xffffffffffcvar_, float:NaN)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteRedText4"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -1230535(0xffffffffffed3939, float:NaN)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteRedText5"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -39322(0xfffffffffffvar_, float:NaN)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteRedText6"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -8156010(0xfffffffffvar_CLASSNAME, float:NaN)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteGrayText"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -8223094(0xfffffffffvar_a, float:NaN)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteGrayText2"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -6710887(0xfffffffffvar_, float:NaN)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteGrayText3"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -8355712(0xfffffffffvar_, float:NaN)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteGrayText4"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -6052957(0xffffffffffa3a3a3, float:NaN)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteGrayText5"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -9079435(0xfffffffffvar_, float:-3.2627073E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteGrayText6"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -3750202(0xffffffffffc6c6c6, float:NaN)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteGrayText7"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -9605774(0xffffffffff6d6d72, float:-3.155953E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteGrayText8"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -2368549(0xffffffffffdbdbdb, float:NaN)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteGrayLine"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            java.lang.String r14 = "windowBackgroundWhiteBlackText"
            r10.put(r14, r11)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -5723992(0xffffffffffa8a8a8, float:NaN)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteHintText"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -12937771(0xffffffffff3a95d5, float:-2.4801438E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteValueText"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -14255946(0xfffffffffvar_b6, float:-2.2127861E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteLinkText"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = 862104035(0x3362a9e3, float:5.2774237E-8)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteLinkSelection"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -12937771(0xffffffffff3a95d5, float:-2.4801438E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteBlueHeader"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -2368549(0xffffffffffdbdbdb, float:NaN)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteInputField"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -13129232(0xfffffffffvar_a9f0, float:-2.4413109E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteInputFieldActivated"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -5196358(0xffffffffffb0b5ba, float:NaN)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "switchTrack"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -11358743(0xfffffffffvar_ade9, float:-2.8004087E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "switchTrackChecked"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -8221031(0xfffffffffvar_e99, float:NaN)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "switchTrackBlue"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -12810041(0xffffffffff3CLASSNAMEc7, float:-2.5060505E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "switchTrackBlueChecked"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            java.lang.String r14 = "switchTrackBlueThumb"
            r10.put(r14, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            java.lang.String r14 = "switchTrackBlueThumbChecked"
            r10.put(r14, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = 390089299(0x17404a53, float:6.2132356E-25)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "switchTrackBlueSelector"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = 553797505(0x21024781, float:4.414035E-19)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "switchTrackBlueSelectorChecked"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -688514(0xffffffffffvar_e7e, float:NaN)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "switch2Track"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -11358743(0xfffffffffvar_ade9, float:-2.8004087E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "switch2TrackChecked"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -12345121(0xfffffffffvar_a0df, float:-2.6003475E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "checkboxSquareBackground"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            java.lang.String r14 = "checkboxSquareCheck"
            r10.put(r14, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -9211021(0xfffffffffvar_, float:-3.2360185E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "checkboxSquareUnchecked"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -5197648(0xffffffffffb0b0b0, float:NaN)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "checkboxSquareDisabled"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = 251658240(0xvar_, float:6.3108872E-30)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "listSelectorSDK21"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -5000269(0xffffffffffb3b3b3, float:NaN)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "radioBackground"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -13129232(0xfffffffffvar_a9f0, float:-2.4413109E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "radioBackgroundChecked"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -986896(0xfffffffffff0f0f0, float:NaN)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundGray"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundGrayShadow"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r15 = -6974059(0xfffffffffvar_, float:NaN)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            java.lang.String r9 = "emptyListPlaceholder"
            r10.put(r9, r15)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -2500135(0xffffffffffd9d9d9, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "divider"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -657931(0xfffffffffff5f5f5, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "graySection"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -8222838(0xfffffffffvar_a, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "key_graySectionText"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -4202506(0xffffffffffbfdff6, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "contextProgressInner1"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -13920542(0xffffffffff2b96e2, float:-2.2808142E38)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "contextProgressOuter1"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -4202506(0xffffffffffbfdff6, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "contextProgressInner2"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            java.lang.String r10 = "contextProgressOuter2"
            r9.put(r10, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -5000269(0xffffffffffb3b3b3, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "contextProgressInner3"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            java.lang.String r10 = "contextProgressOuter3"
            r9.put(r10, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -3486256(0xffffffffffcacdd0, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "contextProgressInner4"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -13683656(0xffffffffff2var_, float:-2.3288603E38)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "contextProgressOuter4"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r15 = -11361317(0xfffffffffvar_a3db, float:-2.7998867E38)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            java.lang.String r7 = "fastScrollActive"
            r9.put(r7, r15)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -3551791(0xffffffffffc9cdd1, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "fastScrollInactive"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "fastScrollText"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "avatar_text"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -10043398(0xfffffffffvar_bffa, float:-3.0671924E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_backgroundSaved"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -5654847(0xffffffffffa9b6c1, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_backgroundArchived"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -10043398(0xfffffffffvar_bffa, float:-3.0671924E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_backgroundArchivedHidden"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -1743531(0xffffffffffe56555, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_backgroundRed"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -881592(0xffffffffffvar_CLASSNAME, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_backgroundOrange"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -7436818(0xffffffffff8e85ee, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_backgroundViolet"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -8992691(0xfffffffffvar_CLASSNAMEd, float:-3.280301E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_backgroundGreen"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -10502443(0xffffffffff5fbed5, float:-2.974087E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_backgroundCyan"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -11232035(0xfffffffffvar_cdd, float:-2.8261082E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_backgroundBlue"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -887654(0xffffffffffvar_a, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_backgroundPink"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -11500111(0xfffffffffvar_b1, float:-2.7717359E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_backgroundInProfileBlue"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -10907718(0xfffffffffvar_fba, float:-2.8918875E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_backgroundActionBarBlue"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -2626822(0xffffffffffd7eafa, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_subtitleInProfileBlue"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -11959891(0xfffffffffvar_ad, float:-2.6784814E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_actionBarSelectorBlue"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "avatar_actionBarIconBlue"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -3516848(0xffffffffffca5650, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_nameInMessageRed"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -2589911(0xffffffffffd87b29, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_nameInMessageOrange"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -11627828(0xffffffffff4e92cc, float:-2.7458318E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_nameInMessageViolet"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -11488718(0xfffffffffvar_b232, float:-2.7740467E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_nameInMessageGreen"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -13132104(0xfffffffffvar_eb8, float:-2.4407284E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_nameInMessageCyan"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -11627828(0xffffffffff4e92cc, float:-2.7458318E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_nameInMessageBlue"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -11627828(0xffffffffff4e92cc, float:-2.7458318E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_nameInMessagePink"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -11371101(0xfffffffffvar_da3, float:-2.7979022E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarDefault"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "actionBarDefaultIcon"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "actionBarActionModeDefault"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = 268435456(0x10000000, float:2.5243549E-29)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarActionModeDefaultTop"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -9999761(0xfffffffffvar_a6f, float:-3.076043E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarActionModeDefaultIcon"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "actionBarDefaultTitle"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -2758409(0xffffffffffd5e8f7, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarDefaultSubtitle"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -12554860(0xfffffffffvar_d94, float:-2.5578074E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarDefaultSelector"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = 486539264(0x1d000000, float:1.6940659E-21)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarWhiteSelector"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "actionBarDefaultSearch"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -1996488705(0xfffffffvar_ffffff, float:-1.5407439E-33)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarDefaultSearchPlaceholder"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "actionBarDefaultSubmenuItem"
            r7.put(r9, r11)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -9999504(0xfffffffffvar_b70, float:-3.0760951E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarDefaultSubmenuItemIcon"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "actionBarDefaultSubmenuBackground"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -1907998(0xffffffffffe2e2e2, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarActionModeDefaultSelector"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "actionBarTabActiveText"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -2758409(0xffffffffffd5e8f7, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarTabUnactiveText"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "actionBarTabLine"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -12554860(0xfffffffffvar_d94, float:-2.5578074E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarTabSelector"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "actionBarBrowser"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -9471353(0xffffffffff6f7a87, float:-3.1832169E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarDefaultArchived"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -10590350(0xffffffffff5e6772, float:-2.9562573E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarDefaultArchivedSelector"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "actionBarDefaultArchivedIcon"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "actionBarDefaultArchivedTitle"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "actionBarDefaultArchivedSearch"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -1996488705(0xfffffffvar_ffffff, float:-1.5407439E-33)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarDefaultSearchArchivedPlaceholder"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -11810020(0xffffffffff4bcb1c, float:-2.7088789E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_onlineCircle"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -11613090(0xffffffffff4ecc5e, float:-2.748821E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_unreadCounter"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -3749428(0xffffffffffc6c9cc, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_unreadCounterMuted"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chats_unreadCounterText"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -10049056(0xfffffffffvar_a9e0, float:-3.0660448E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_archiveBackground"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -6313293(0xffffffffff9faab3, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_archivePinBackground"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chats_archiveIcon"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chats_archiveText"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chats_name"
            r7.put(r9, r11)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -11382190(0xfffffffffvar_, float:-2.7956531E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_nameArchived"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -16734706(0xfffffffffvar_a60e, float:-1.7100339E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_secretName"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -15093466(0xfffffffffvar_b126, float:-2.042917E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_secretIcon"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -14408668(0xfffffffffvar_, float:-2.1818104E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_nameIcon"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -5723992(0xffffffffffa8a8a8, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_pinnedIcon"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -7631473(0xffffffffff8b8d8f, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_message"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -7237231(0xfffffffffvar_, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_messageArchived"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -7434095(0xffffffffff8e9091, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_message_threeLines"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -2274503(0xffffffffffdd4b39, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_draft"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -12812624(0xffffffffff3c7eb0, float:-2.5055266E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_nameMessage"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -7631473(0xffffffffff8b8d8f, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_nameMessageArchived"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -12434359(0xfffffffffvar_, float:-2.5822479E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_nameMessage_threeLines"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -10592674(0xffffffffff5e5e5e, float:-2.955786E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_nameMessageArchived_threeLines"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -12812624(0xffffffffff3c7eb0, float:-2.5055266E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_attachMessage"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -12812624(0xffffffffff3c7eb0, float:-2.5055266E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_actionMessage"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -6973028(0xfffffffffvar_c, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_date"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = 134217728(0x8000000, float:3.85186E-34)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_pinnedOverlay"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = 251658240(0xvar_, float:6.3108872E-30)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_tabletSelectedOverlay"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -12146122(0xfffffffffvar_aa36, float:-2.6407093E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_sentCheck"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -12146122(0xfffffffffvar_aa36, float:-2.6407093E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_sentReadCheck"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -9061026(0xfffffffffvar_bd5e, float:-3.266441E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_sentClock"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -2796974(0xffffffffffd55252, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_sentError"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chats_sentErrorIcon"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -13391642(0xfffffffffvar_a8e6, float:-2.3880878E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_verifiedBackground"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chats_verifiedCheck"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -4341308(0xffffffffffbdc1c4, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_muteIcon"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chats_mentionIcon"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chats_menuBackground"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -12303292(0xfffffffffvar_, float:-2.6088314E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_menuItemText"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -10907718(0xfffffffffvar_fba, float:-2.8918875E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_menuItemCheck"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -7827048(0xfffffffffvar_, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_menuItemIcon"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chats_menuName"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chats_menuPhone"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -4004353(0xffffffffffc2e5ff, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_menuPhoneCats"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chats_menuCloud"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -12420183(0xfffffffffvar_ba9, float:-2.5851231E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_menuCloudBackgroundCats"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chats_actionIcon"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -10114592(0xfffffffffvar_a9e0, float:-3.0527525E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_actionBackground"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -11100714(0xfffffffffvar_dd6, float:-2.8527432E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_actionPressedBackground"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -9211021(0xfffffffffvar_, float:-3.2360185E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_actionUnreadIcon"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chats_actionUnreadBackground"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -855310(0xfffffffffff2f2f2, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_actionUnreadPressedBackground"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -10907718(0xfffffffffvar_fba, float:-2.8918875E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_menuTopBackgroundCats"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -3749428(0xffffffffffc6c9cc, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_archivePullDownBackground"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -10049056(0xfffffffffvar_a9e0, float:-3.0660448E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_archivePullDownBackgroundActive"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -12171706(0xfffffffffvar_, float:-2.6355202E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachMediaBanBackground"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chat_attachMediaBanText"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chat_attachCheckBoxCheck"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -12995849(0xfffffffffvar_b2f7, float:-2.4683642E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachCheckBoxBackground"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = 201326592(0xCLASSNAME, float:9.8607613E-32)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachPhotoBackground"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -13391883(0xfffffffffvar_a7f5, float:-2.388039E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachActiveTab"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -7169634(0xfffffffffvar_e, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachUnactiveTab"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -13421773(0xfffffffffvar_, float:-2.3819765E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachPermissionImage"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -1945520(0xffffffffffe25050, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachPermissionMark"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -9472134(0xffffffffff6var_a, float:-3.1830585E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachPermissionText"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -3355444(0xffffffffffcccccc, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachEmptyImage"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -12214795(0xfffffffffvar_df5, float:-2.6267807E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachGalleryBackground"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chat_attachGalleryIcon"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -1351584(0xffffffffffeb6060, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachAudioBackground"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chat_attachAudioIcon"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -13321743(0xfffffffffvar_b9f1, float:-2.402265E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachFileBackground"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chat_attachFileIcon"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -868277(0xfffffffffff2CLASSNAMEb, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachContactBackground"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chat_attachContactIcon"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -10436011(0xfffffffffvar_CLASSNAME, float:-2.987561E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachLocationBackground"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chat_attachLocationIcon"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -868277(0xfffffffffff2CLASSNAMEb, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachPollBackground"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chat_attachPollIcon"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -10436011(0xfffffffffvar_CLASSNAME, float:-2.987561E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_inPollCorrectAnswer"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -10436011(0xfffffffffvar_CLASSNAME, float:-2.987561E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outPollCorrectAnswer"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -1351584(0xffffffffffeb6060, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_inPollWrongAnswer"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -1351584(0xffffffffffeb6060, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outPollWrongAnswer"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -2758409(0xffffffffffd5e8f7, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_status"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -16725933(0xfffffffffvar_CLASSNAME, float:-1.7118133E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_inDownCall"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -47032(0xfffffffffffvar_, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_inUpCall"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -16725933(0xfffffffffvar_CLASSNAME, float:-1.7118133E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outUpCall"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = 1718783910(0x66728fa6, float:2.8636563E23)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_shareBackground"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -1720545370(0xfffffffvar_fa6, float:-1.2540116E-23)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_shareBackgroundSelected"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chat_lockIcon"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -5124893(0xffffffffffb1cce3, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_muteIcon"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chat_inBubble"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -1247235(0xffffffffffecf7fd, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_inBubbleSelected"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -14862509(0xffffffffff1d3753, float:-2.0897606E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_inBubbleShadow"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -1048610(0xffffffffffefffde, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outBubble"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = 335544320(0x14000000, float:6.4623485E-27)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outBubbleGradientSelectedOverlay"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -2492475(0xffffffffffd9f7c5, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outBubbleSelected"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -14781172(0xffffffffff1e750c, float:-2.1062577E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outBubbleShadow"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chat_inMediaIcon"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -1050370(0xffffffffffeff8fe, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_inMediaIconSelected"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -1048610(0xffffffffffefffde, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outMediaIcon"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -1967921(0xffffffffffe1f8cf, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outMediaIconSelected"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chat_messageTextIn"
            r7.put(r9, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chat_messageTextOut"
            r7.put(r9, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -14255946(0xfffffffffvar_b6, float:-2.2127861E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_messageLinkIn"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -14255946(0xfffffffffvar_b6, float:-2.2127861E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_messageLinkOut"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chat_serviceText"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chat_serviceLink"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r9 = "chat_serviceIcon"
            r7.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = 1711276032(0x66000000, float:1.5111573E23)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_mediaTimeBackground"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -10637232(0xffffffffff5db050, float:-2.9467485E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outSentCheck"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -10637232(0xffffffffff5db050, float:-2.9467485E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outSentCheckSelected"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -10637232(0xffffffffff5db050, float:-2.9467485E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outSentCheckRead"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -10637232(0xffffffffff5db050, float:-2.9467485E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outSentCheckReadSelected"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -9061026(0xfffffffffvar_bd5e, float:-3.266441E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outSentClock"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -9061026(0xfffffffffvar_bd5e, float:-3.266441E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outSentClockSelected"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r9 = -6182221(0xffffffffffa1aab3, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_inSentClock"
            r7.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r15 = -7094838(0xfffffffffvar_bdca, float:NaN)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            java.lang.String r12 = "chat_inSentClockSelected"
            r7.put(r12, r15)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r12 = "chat_mediaSentCheck"
            r7.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r12 = "chat_mediaSentClock"
            r7.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r12 = "chat_inViews"
            r7.put(r12, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r12 = -7094838(0xfffffffffvar_bdca, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_inViewsSelected"
            r7.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r12 = -9522601(0xffffffffff6eb257, float:-3.1728226E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_outViews"
            r7.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r12 = -9522601(0xffffffffff6eb257, float:-3.1728226E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_outViewsSelected"
            r7.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r12 = "chat_mediaViews"
            r7.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r12 = -4801083(0xffffffffffb6bdc5, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_inMenu"
            r7.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r12 = -6766130(0xfffffffffvar_c1ce, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_inMenuSelected"
            r7.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r12 = -7221634(0xfffffffffvar_ce7e, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_outMenu"
            r7.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r12 = -7221634(0xfffffffffvar_ce7e, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_outMenuSelected"
            r7.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r12 = "chat_mediaMenu"
            r7.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r12 = -11162801(0xfffffffffvar_ab4f, float:-2.8401505E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_outInstant"
            r7.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r15 = -12019389(0xfffffffffvar_, float:-2.6664138E38)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            java.lang.String r8 = "chat_outInstantSelected"
            r7.put(r8, r15)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r8 = "chat_inInstant"
            r7.put(r8, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r8 = -13600331(0xfffffffffvar_b5, float:-2.3457607E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inInstantSelected"
            r7.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r8 = -2411211(0xffffffffffdb3535, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_sentError"
            r7.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r8 = "chat_sentErrorIcon"
            r7.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r8 = 671781104(0x280a90f0, float:7.691967E-15)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_selectedBackground"
            r7.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r8 = "chat_previewDurationText"
            r7.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r8 = "chat_previewGameText"
            r7.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r8 = "chat_inPreviewInstantText"
            r7.put(r8, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r8 = "chat_outPreviewInstantText"
            r7.put(r8, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r8 = -13600331(0xfffffffffvar_b5, float:-2.3457607E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inPreviewInstantSelectedText"
            r7.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r8 = -12019389(0xfffffffffvar_, float:-2.6664138E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outPreviewInstantSelectedText"
            r7.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r8 = -1776928(0xffffffffffe4e2e0, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_secretTimeText"
            r7.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r8 = "chat_stickerNameText"
            r7.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r8 = "chat_botButtonText"
            r7.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r8 = "chat_botProgress"
            r7.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r8 = -13072697(0xfffffffffvar_c7, float:-2.4527776E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inForwardedNameText"
            r7.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r8 = "chat_outForwardedNameText"
            r7.put(r8, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r8 = "chat_inViaBotNameText"
            r7.put(r8, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r8 = "chat_outViaBotNameText"
            r7.put(r8, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r8 = "chat_stickerViaBotNameText"
            r7.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r8 = -10903592(0xfffffffffvar_fd8, float:-2.8927243E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inReplyLine"
            r7.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r8 = -9520791(0xffffffffff6eb969, float:-3.1731897E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outReplyLine"
            r7.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r8 = "chat_stickerReplyLine"
            r7.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r8 = "chat_inReplyNameText"
            r7.put(r8, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r8 = "chat_outReplyNameText"
            r7.put(r8, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r8 = "chat_stickerReplyNameText"
            r7.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r8 = "chat_inReplyMessageText"
            r7.put(r8, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r8 = "chat_outReplyMessageText"
            r7.put(r8, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            java.lang.String r8 = "chat_inReplyMediaMessageText"
            r7.put(r8, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r8 = -10112933(0xfffffffffvar_b05b, float:-3.053089E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outReplyMediaMessageText"
            r7.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = defaultColors
            r15 = -7752511(0xfffffffffvar_b4c1, float:NaN)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            java.lang.String r4 = "chat_inReplyMediaMessageSelectedText"
            r7.put(r4, r15)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_outReplyMediaMessageSelectedText"
            r4.put(r7, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_stickerReplyMessageText"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -9390872(0xfffffffffvar_b4e8, float:-3.1995404E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_inPreviewLine"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -7812741(0xfffffffffvar_CLASSNAMEb, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_outPreviewLine"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_inSiteNameText"
            r4.put(r7, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_outSiteNameText"
            r4.put(r7, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11625772(0xffffffffff4e9ad4, float:-2.7462488E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_inContactNameText"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_outContactNameText"
            r4.put(r7, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_inContactPhoneText"
            r4.put(r7, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_inContactPhoneSelectedText"
            r4.put(r7, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -13286860(0xfffffffffvar_, float:-2.4093401E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_outContactPhoneText"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -13286860(0xfffffffffvar_, float:-2.4093401E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_outContactPhoneSelectedText"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_mediaProgress"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_inAudioProgress"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -1048610(0xffffffffffefffde, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_outAudioProgress"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -1050370(0xffffffffffeff8fe, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_inAudioSelectedProgress"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -1967921(0xffffffffffe1f8cf, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_outAudioSelectedProgress"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_mediaTimeText"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_inTimeText"
            r4.put(r7, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -9391780(0xfffffffffvar_b15c, float:-3.1993562E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_outTimeText"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -4143413(0xffffffffffc0c6cb, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_adminText"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -7752511(0xfffffffffvar_b4c1, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_adminSelectedText"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -7752511(0xfffffffffvar_b4c1, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_inTimeSelectedText"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -9391780(0xfffffffffvar_b15c, float:-3.1993562E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_outTimeSelectedText"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_inAudioPerfomerText"
            r4.put(r7, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_inAudioPerfomerSelectedText"
            r4.put(r7, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -13286860(0xfffffffffvar_, float:-2.4093401E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_outAudioPerfomerText"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -13286860(0xfffffffffvar_, float:-2.4093401E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_outAudioPerfomerSelectedText"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11625772(0xffffffffff4e9ad4, float:-2.7462488E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_inAudioTitleText"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_outAudioTitleText"
            r4.put(r7, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_inAudioDurationText"
            r4.put(r7, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_outAudioDurationText"
            r4.put(r7, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -7752511(0xfffffffffvar_b4c1, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_inAudioDurationSelectedText"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_outAudioDurationSelectedText"
            r4.put(r7, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -1774864(0xffffffffffe4eaf0, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_inAudioSeekbar"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = 1071966960(0x3fe4eaf0, float:1.7884197)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_inAudioCacheSeekbar"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -4463700(0xffffffffffbbe3ac, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_outAudioSeekbar"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = 1069278124(0x3fbbe3ac, float:1.4678855)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_outAudioCacheSeekbar"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -4399384(0xffffffffffbcdee8, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_inAudioSeekbarSelected"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -5644906(0xffffffffffa9dd96, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_outAudioSeekbarSelected"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -9259544(0xfffffffffvar_b5e8, float:-3.2261769E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_inAudioSeekbarFill"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -8863118(0xfffffffffvar_CLASSNAME, float:-3.3065816E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_outAudioSeekbarFill"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -2169365(0xffffffffffdee5eb, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_inVoiceSeekbar"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -4463700(0xffffffffffbbe3ac, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_outVoiceSeekbar"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -4399384(0xffffffffffbcdee8, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_inVoiceSeekbarSelected"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -5644906(0xffffffffffa9dd96, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_outVoiceSeekbarSelected"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -9259544(0xfffffffffvar_b5e8, float:-3.2261769E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_inVoiceSeekbarFill"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -8863118(0xfffffffffvar_CLASSNAME, float:-3.3065816E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_outVoiceSeekbarFill"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -1314571(0xffffffffffebf0f5, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_inFileProgress"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -2427453(0xffffffffffdaf5c3, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_outFileProgress"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -3413258(0xffffffffffcbeaf6, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_inFileProgressSelected"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -3806041(0xffffffffffc5eca7, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_outFileProgressSelected"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11625772(0xffffffffff4e9ad4, float:-2.7462488E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r15 = "chat_inFileNameText"
            r4.put(r15, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_outFileNameText"
            r4.put(r7, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_inFileInfoText"
            r4.put(r7, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_outFileInfoText"
            r4.put(r7, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -7752511(0xfffffffffvar_b4c1, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r12 = "chat_inFileInfoSelectedText"
            r4.put(r12, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_outFileInfoSelectedText"
            r4.put(r7, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -1314571(0xffffffffffebf0f5, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r12 = "chat_inFileBackground"
            r4.put(r12, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -2427453(0xffffffffffdaf5c3, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r12 = "chat_outFileBackground"
            r4.put(r12, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -3413258(0xffffffffffcbeaf6, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r12 = "chat_inFileBackgroundSelected"
            r4.put(r12, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -3806041(0xffffffffffc5eca7, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r12 = "chat_outFileBackgroundSelected"
            r4.put(r12, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_inVenueInfoText"
            r4.put(r7, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_outVenueInfoText"
            r4.put(r7, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -7752511(0xfffffffffvar_b4c1, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r12 = "chat_inVenueInfoSelectedText"
            r4.put(r12, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_outVenueInfoSelectedText"
            r4.put(r7, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_mediaInfoText"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = 862104035(0x3362a9e3, float:5.2774237E-8)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_linkSelectBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = 1717742051(0x6662a9e3, float:2.6759717E23)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_textSelectBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -986379(0xfffffffffff0f2f5, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_emojiPanelBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11688214(0xffffffffff4da6ea, float:-2.733584E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_emojiPanelBadgeBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_emojiPanelBadgeText"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -1709586(0xffffffffffe5e9ee, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_emojiSearchBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -7036497(0xfffffffffvar_a1af, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_emojiSearchIcon"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = 301989888(0x12000000, float:4.0389678E-28)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_emojiPanelShadowLine"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -7038047(0xfffffffffvar_ba1, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_emojiPanelEmptyText"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -6445909(0xffffffffff9da4ab, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_emojiPanelIcon"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -7564905(0xffffffffff8CLASSNAME, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_emojiBottomPanelIcon"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -13920286(0xffffffffff2b97e2, float:-2.280866E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_emojiPanelIconSelected"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -1907225(0xffffffffffe2e5e7, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_emojiPanelStickerPackSelector"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11097104(0xfffffffffvar_abf0, float:-2.8534754E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_emojiPanelStickerPackSelectorLine"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -7564905(0xffffffffff8CLASSNAME, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_emojiPanelBackspace"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_emojiPanelMasksIcon"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -10305560(0xfffffffffvar_bfe8, float:-3.0140196E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_emojiPanelMasksIconSelected"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_emojiPanelTrendingTitle"
            r4.put(r7, r11)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -8221804(0xfffffffffvar_b94, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_emojiPanelStickerSetName"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -14184997(0xfffffffffvar_ddb, float:-2.2271763E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_emojiPanelStickerSetNameHighlight"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -5130564(0xffffffffffb1b6bc, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_emojiPanelStickerSetNameIcon"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -7697782(0xffffffffff8a8a8a, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_emojiPanelTrendingDescription"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -13220017(0xfffffffffvar_f, float:-2.4228975E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_botKeyboardButtonText"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -1775639(0xffffffffffe4e7e9, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_botKeyboardButtonBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -3354156(0xffffffffffccd1d4, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_botKeyboardButtonBackgroundPressed"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -6113849(0xffffffffffa2b5c7, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_unreadMessagesStartArrowIcon"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11102772(0xfffffffffvar_cc, float:-2.8523258E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_unreadMessagesStartText"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_unreadMessagesStartBackground"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -6113849(0xffffffffffa2b5c7, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_inFileIcon"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -7883067(0xfffffffffvar_b6c5, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_inFileSelectedIcon"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -8011912(0xfffffffffvar_bvar_, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_outFileIcon"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -8011912(0xfffffffffvar_bvar_, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_outFileSelectedIcon"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -1314571(0xffffffffffebf0f5, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_inLocationBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -6113849(0xffffffffffa2b5c7, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_inLocationIcon"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -2427453(0xffffffffffdaf5c3, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_outLocationBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -7880840(0xfffffffffvar_bvar_, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_outLocationIcon"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -9259544(0xfffffffffvar_b5e8, float:-3.2261769E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_inContactBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_inContactIcon"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -8863118(0xfffffffffvar_CLASSNAME, float:-3.3065816E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_outContactBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -1048610(0xffffffffffefffde, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_outContactIcon"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -12146122(0xfffffffffvar_aa36, float:-2.6407093E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_outBroadcast"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_mediaBroadcast"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -9999761(0xfffffffffvar_a6f, float:-3.076043E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_searchPanelIcons"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -9999761(0xfffffffffvar_a6f, float:-3.076043E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_searchPanelText"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -8421505(0xffffffffff7f7f7f, float:-3.3961514E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_secretChatStatusText"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_fieldOverlayText"
            r4.put(r7, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_stickersHintPanel"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11032346(0xfffffffffvar_a8e6, float:-2.86661E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_replyPanelIcons"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -7432805(0xffffffffff8e959b, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_replyPanelClose"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_replyPanelName"
            r4.put(r7, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_replyPanelMessage"
            r4.put(r7, r11)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -1513240(0xffffffffffe8e8e8, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_replyPanelLine"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_messagePanelBackground"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_messagePanelText"
            r4.put(r7, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -5985101(0xffffffffffa4acb3, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_messagePanelHint"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11230757(0xfffffffffvar_a1db, float:-2.8263674E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_messagePanelCursor"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_messagePanelShadow"
            r4.put(r7, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -7432805(0xffffffffff8e959b, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_messagePanelIcons"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11817481(0xffffffffff4badf7, float:-2.7073656E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_messagePanelVideoFrame"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_recordedVoicePlayPause"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -2495749(0xffffffffffd9eafb, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_recordedVoicePlayPausePressed"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -2468275(0xffffffffffda564d, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_recordedVoiceDot"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -10637848(0xffffffffff5dade8, float:-2.9466236E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_recordedVoiceBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -5120257(0xffffffffffb1deff, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_recordedVoiceProgress"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_recordedVoiceProgressInner"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -12937772(0xffffffffff3a95d4, float:-2.4801436E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_recordVoiceCancel"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = 1694498815(0x64ffffff, float:3.777893E22)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "key_chat_recordedVoiceHighlight"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -10309397(0xfffffffffvar_b0eb, float:-3.0132414E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_messagePanelSend"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -5987164(0xffffffffffa4a4a4, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "key_chat_messagePanelVoiceLock"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "key_chat_messagePanelVoiceLockBackground"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "key_chat_messagePanelVoiceLockShadow"
            r4.put(r7, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -7432805(0xffffffffff8e959b, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_recordTime"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11688214(0xffffffffff4da6ea, float:-2.733584E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_emojiPanelNewTrending"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_gifSaveHintText"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -871296751(0xffffffffcCLASSNAME, float:-3.8028356E7)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_gifSaveHintBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_goDownButton"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_goDownButtonShadow"
            r4.put(r7, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -7432805(0xffffffffff8e959b, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_goDownButtonIcon"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_goDownButtonCounter"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11689240(0xffffffffff4da2e8, float:-2.733376E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_goDownButtonCounterBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -5395027(0xffffffffffadadad, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_messagePanelCancelInlineBot"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_messagePanelVoicePressed"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -10639650(0xffffffffff5da6de, float:-2.9462581E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_messagePanelVoiceBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = 218103808(0xd000000, float:3.9443045E-31)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_messagePanelVoiceShadow"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -9211021(0xfffffffffvar_, float:-3.2360185E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_messagePanelVoiceDelete"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_messagePanelVoiceDuration"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11037236(0xfffffffffvar_cc, float:-2.865618E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_inlineResultIcon"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_topPanelBackground"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -7563878(0xffffffffff8CLASSNAMEa, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_topPanelClose"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -9658414(0xffffffffff6c9fd2, float:-3.1452764E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_topPanelLine"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_topPanelTitle"
            r4.put(r7, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -6710887(0xfffffffffvar_, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_topPanelMessage"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -3188393(0xffffffffffcvar_, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_reportSpam"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11894091(0xffffffffff4a82b5, float:-2.6918272E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_addContact"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -9259544(0xfffffffffvar_b5e8, float:-3.2261769E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_inLoader"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -10114080(0xfffffffffvar_abe0, float:-3.0528564E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_inLoaderSelected"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -8863118(0xfffffffffvar_CLASSNAME, float:-3.3065816E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_outLoader"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -9783964(0xffffffffff6ab564, float:-3.1198118E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_outLoaderSelected"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -6113080(0xffffffffffa2b8c8, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_inLoaderPhoto"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -6113849(0xffffffffffa2b5c7, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_inLoaderPhotoSelected"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -197380(0xfffffffffffcfcfc, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_inLoaderPhotoIcon"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -1314571(0xffffffffffebf0f5, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_inLoaderPhotoIconSelected"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -8011912(0xfffffffffvar_bvar_, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_outLoaderPhoto"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -8538000(0xffffffffff7db870, float:-3.3725234E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_outLoaderPhotoSelected"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -2427453(0xffffffffffdaf5c3, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_outLoaderPhotoIcon"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -4134748(0xffffffffffc0e8a4, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_outLoaderPhotoIconSelected"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = 1711276032(0x66000000, float:1.5111573E23)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_mediaLoaderPhoto"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = 2130706432(0x7var_, float:1.7014118E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_mediaLoaderPhotoSelected"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_mediaLoaderPhotoIcon"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -2500135(0xffffffffffd9d9d9, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_mediaLoaderPhotoIconSelected"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -868326258(0xffffffffcc3e648e, float:-4.9910328E7)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_secretTimerBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "chat_secretTimerText"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -12937771(0xffffffffff3a95d5, float:-2.4801438E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "profile_creatorIcon"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -8288630(0xfffffffffvar_a, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "profile_actionIcon"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "profile_actionBackground"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -855310(0xfffffffffff2f2f2, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "profile_actionPressedBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -5056776(0xffffffffffb2d6f8, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "profile_verifiedBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11959368(0xfffffffffvar_b8, float:-2.6785875E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "profile_verifiedCheck"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "profile_title"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -2626822(0xffffffffffd7eafa, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "profile_status"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -7893872(0xfffffffffvar_CLASSNAME, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "profile_tabText"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -12937771(0xffffffffff3a95d5, float:-2.4801438E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "profile_tabSelectedText"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11557143(0xffffffffff4fa6e9, float:-2.7601684E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "profile_tabSelectedLine"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = 251658240(0xvar_, float:6.3108872E-30)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "profile_tabSelector"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "player_actionBar"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = 251658240(0xvar_, float:6.3108872E-30)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "player_actionBarSelector"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "player_actionBarTitle"
            r4.put(r7, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -1728053248(0xfffffffvar_, float:-6.617445E-24)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "player_actionBarTop"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -7697782(0xffffffffff8a8a8a, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "player_actionBarSubtitle"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -7697782(0xffffffffff8a8a8a, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "player_actionBarItems"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "player_background"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -7564650(0xffffffffff8CLASSNAME, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "player_time"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -1445899(0xffffffffffe9eff5, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "player_progressBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -1445899(0xffffffffffe9eff5, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "key_player_progressCachedBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11821085(0xffffffffff4b9fe3, float:-2.7066346E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "player_progress"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -5723992(0xffffffffffa8a8a8, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "player_placeholder"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -986896(0xfffffffffff0f0f0, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "player_placeholderBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -13421773(0xfffffffffvar_, float:-2.3819765E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "player_button"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11753238(0xffffffffff4ca8ea, float:-2.7203956E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "player_buttonActive"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -1973016(0xffffffffffe1e4e8, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "key_sheet_scrollUp"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -3551789(0xffffffffffc9cdd3, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "key_sheet_other"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "files_folderIcon"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -10637333(0xffffffffff5dafeb, float:-2.946728E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "files_folderIconBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "files_iconText"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -6908266(0xfffffffffvar_, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "sessions_devicesImage"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -12211217(0xfffffffffvar_abef, float:-2.6275065E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "passport_authorizeBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -12542501(0xfffffffffvar_ddb, float:-2.560314E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "passport_authorizeBackgroundSelected"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "passport_authorizeText"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -12149258(0xfffffffffvar_df6, float:-2.6400732E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "location_sendLocationBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "location_sendLocationIcon"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -14906664(0xffffffffff1c8ad8, float:-2.0808049E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "location_sendLocationText"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11550140(0xffffffffff4fCLASSNAME, float:-2.7615888E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "location_sendLiveLocationBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "location_sendLiveLocationIcon"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -13194460(0xfffffffffvar_ab24, float:-2.428081E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "location_sendLiveLocationText"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -13262875(0xfffffffffvar_fe5, float:-2.4142049E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "location_liveLocationProgress"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11753238(0xffffffffff4ca8ea, float:-2.7203956E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "location_placeLocationBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -12959675(0xffffffffff3a4045, float:-2.4757011E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "location_actionIcon"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -12414746(0xfffffffffvar_e6, float:-2.5862259E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "location_actionActiveIcon"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "location_actionBackground"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -855310(0xfffffffffff2f2f2, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "location_actionPressedBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -13262875(0xfffffffffvar_fe5, float:-2.4142049E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "dialog_liveLocationProgress"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -16725933(0xfffffffffvar_CLASSNAME, float:-1.7118133E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "calls_callReceivedGreenIcon"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -47032(0xfffffffffffvar_, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "calls_callReceivedRedIcon"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11491093(0xfffffffffvar_a8eb, float:-2.773565E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "featuredStickers_addedIcon"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "featuredStickers_buttonProgress"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11491093(0xfffffffffvar_a8eb, float:-2.773565E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "featuredStickers_addButton"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -12346402(0xfffffffffvar_bde, float:-2.6000877E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "featuredStickers_addButtonPressed"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11496493(0xfffffffffvar_d3, float:-2.7724697E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "featuredStickers_removeButtonText"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "featuredStickers_buttonText"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11688214(0xffffffffff4da6ea, float:-2.733584E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "featuredStickers_unread"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "inappPlayerPerformer"
            r4.put(r7, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "inappPlayerTitle"
            r4.put(r7, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "inappPlayerBackground"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -10309397(0xfffffffffvar_b0eb, float:-3.0132414E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "inappPlayerPlayPause"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -5723992(0xffffffffffa8a8a8, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "inappPlayerClose"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -12279325(0xfffffffffvar_a1e3, float:-2.6136925E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "returnToCallBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "returnToCallText"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -13196562(0xfffffffffvar_a2ee, float:-2.4276547E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "sharedMedia_startStopLoadIcon"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -986123(0xfffffffffff0f3f5, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "sharedMedia_linkPlaceholder"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -4735293(0xffffffffffb7bec3, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "sharedMedia_linkPlaceholderText"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -1182729(0xffffffffffedf3f7, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "sharedMedia_photoPlaceholder"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -12154957(0xfffffffffvar_b3, float:-2.6389173E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "sharedMedia_actionMode"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -10567099(0xffffffffff5eCLASSNAME, float:-2.9609732E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "checkbox"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "checkboxCheck"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -5195326(0xffffffffffb0b9c2, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "checkboxDisabled"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -4801083(0xffffffffffb6bdc5, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "stickers_menu"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = 251658240(0xvar_, float:6.3108872E-30)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "stickers_menuSelector"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -4669499(0xffffffffffb8bfc5, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "changephoneinfo_image"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11491350(0xfffffffffvar_a7ea, float:-2.7735128E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "changephoneinfo_image2"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "groupcreate_hintText"
            r4.put(r7, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11361317(0xfffffffffvar_a3db, float:-2.7998867E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "groupcreate_cursor"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "groupcreate_sectionShadow"
            r4.put(r7, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -8617336(0xffffffffff7CLASSNAME, float:-3.3564321E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "groupcreate_sectionText"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "groupcreate_spanText"
            r4.put(r7, r11)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -855310(0xfffffffffff2f2f2, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "groupcreate_spanBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "groupcreate_spanDelete"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -11157919(0xfffffffffvar_be61, float:-2.8411407E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "contacts_inviteBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "contacts_inviteText"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -1971470(0xffffffffffe1eaf2, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "login_progressInner"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -10313520(0xfffffffffvar_a0d0, float:-3.0124051E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "login_progressOuter"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -14043401(0xfffffffffvar_b6f7, float:-2.2558954E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "musicPicker_checkbox"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "musicPicker_checkboxCheck"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -10702870(0xffffffffff5cafea, float:-2.9334356E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "musicPicker_buttonBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "musicPicker_buttonIcon"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -15095832(0xfffffffffvar_a7e8, float:-2.042437E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "picker_enabledButton"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -6710887(0xfffffffffvar_, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "picker_disabledButton"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -14043401(0xfffffffffvar_b6f7, float:-2.2558954E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "picker_badge"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "picker_badgeText"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -12348980(0xfffffffffvar_cc, float:-2.5995648E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_botSwitchToInlineText"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -366530760(0xffffffffea272var_, float:-5.05284E25)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "undo_background"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -8008961(0xfffffffffvar_caff, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "undo_cancelColor"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "undo_infoColor"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "wallet_blackBackground"
            r4.put(r7, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -986896(0xfffffffffff0f0f0, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "wallet_graySettingsBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -14079703(0xfffffffffvar_, float:-2.2485325E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "wallet_grayBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "wallet_whiteBackground"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = 1090519039(0x40ffffff, float:7.9999995)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "wallet_blackBackgroundSelector"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "wallet_whiteText"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "wallet_blackText"
            r4.put(r7, r11)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -8355712(0xfffffffffvar_, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "wallet_statusText"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -8947849(0xfffffffffvar_, float:-3.2893961E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "wallet_grayText"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -10066330(0xfffffffffvar_, float:-3.0625412E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "wallet_grayText2"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -13129704(0xfffffffffvar_a818, float:-2.4412152E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "wallet_greenText"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -2408384(0xffffffffffdb4040, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "wallet_redText"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -6710887(0xfffffffffvar_, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "wallet_dateText"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -6710887(0xfffffffffvar_, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "wallet_commentText"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -13599557(0xfffffffffvar_cbb, float:-2.3459176E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "wallet_releaseBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -14606047(0xfffffffffvar_, float:-2.1417772E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "wallet_pullBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -12082714(0xfffffffffvar_a1e6, float:-2.65357E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "wallet_buttonBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -13923114(0xffffffffff2b8cd6, float:-2.2802925E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "wallet_buttonPressedBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "wallet_buttonText"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = 218103808(0xd000000, float:3.9443045E-31)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "wallet_addressConfirmBackground"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = 775919907(0x2e3var_, float:4.3564385E-11)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_outTextSelectionHighlight"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = 1348643299(0x5062a9e3, float:1.5211138E10)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_inTextSelectionHighlight"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -12476440(0xfffffffffvar_fe8, float:-2.5737128E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "chat_TextSelectionCursor"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = 2133140777(0x7var_, float:2.1951557E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "statisticChartSignature"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = 2133140777(0x7var_, float:2.1951557E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "statisticChartSignatureAlpha"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = 437792059(0x1a182d3b, float:3.14694E-23)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "statisticChartHintLine"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -4408132(0xffffffffffbcbcbc, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "statisticChartActiveLine"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -1713180935(0xfffffffvar_e2eef9, float:-2.3464373E-23)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "statisticChartInactivePickerChart"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -658846503(0xffffffffd8baccd9, float:-1.64311181E15)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "statisticChartActivePickerChart"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = 746495415(0x2c7e9db7, float:3.618312E-12)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "statisticChartRipple"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -15692829(0xfffffffffvar_be3, float:-1.9213516E38)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "statisticChartBackZoomColor"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -4342339(0xffffffffffbdbdbd, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "statisticChartCheckboxInactive"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -7434605(0xffffffffff8e8e93, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "statisticChartNightIconColor"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = -2959913(0xffffffffffd2d5d7, float:NaN)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "statisticChartChevronColor"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r7 = 552398060(0x20ececec, float:4.0136737E-19)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            java.lang.String r8 = "statisticChartHighlightColor"
            r4.put(r8, r7)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r7 = "statisticChartPopupBackground"
            r4.put(r7, r0)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_adminText"
            java.lang.String r7 = "chat_inTimeText"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_adminSelectedText"
            java.lang.String r7 = "chat_inTimeSelectedText"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "key_player_progressCachedBackground"
            java.lang.String r7 = "player_progressBackground"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_inAudioCacheSeekbar"
            java.lang.String r7 = "chat_inAudioSeekbar"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_outAudioCacheSeekbar"
            java.lang.String r7 = "chat_outAudioSeekbar"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_emojiSearchBackground"
            java.lang.String r7 = "chat_emojiPanelStickerPackSelector"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "location_sendLiveLocationIcon"
            java.lang.String r7 = "location_sendLocationIcon"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "changephoneinfo_image2"
            java.lang.String r7 = "featuredStickers_addButton"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "key_graySectionText"
            java.lang.String r7 = "windowBackgroundWhiteGrayText2"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_inMediaIcon"
            java.lang.String r7 = "chat_inBubble"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_outMediaIcon"
            java.lang.String r7 = "chat_outBubble"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_inMediaIconSelected"
            java.lang.String r7 = "chat_inBubbleSelected"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_outMediaIconSelected"
            java.lang.String r7 = "chat_outBubbleSelected"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_actionUnreadIcon"
            java.lang.String r7 = "profile_actionIcon"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_actionUnreadBackground"
            java.lang.String r7 = "profile_actionBackground"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_actionUnreadPressedBackground"
            java.lang.String r7 = "profile_actionPressedBackground"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialog_inlineProgressBackground"
            java.lang.String r7 = "windowBackgroundGray"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialog_inlineProgress"
            java.lang.String r7 = "chats_menuItemIcon"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "groupcreate_spanDelete"
            java.lang.String r7 = "chats_actionIcon"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "sharedMedia_photoPlaceholder"
            java.lang.String r7 = "windowBackgroundGray"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_attachPollBackground"
            java.lang.String r7 = "chat_attachAudioBackground"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_attachPollIcon"
            java.lang.String r7 = "chat_attachAudioIcon"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_onlineCircle"
            java.lang.String r7 = "windowBackgroundWhiteBlueText"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "windowBackgroundWhiteBlueButton"
            java.lang.String r7 = "windowBackgroundWhiteValueText"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "windowBackgroundWhiteBlueIcon"
            java.lang.String r7 = "windowBackgroundWhiteValueText"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "undo_background"
            java.lang.String r7 = "chat_gifSaveHintBackground"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "undo_cancelColor"
            java.lang.String r7 = "chat_gifSaveHintText"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "undo_infoColor"
            java.lang.String r7 = "chat_gifSaveHintText"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "windowBackgroundUnchecked"
            java.lang.String r7 = "windowBackgroundWhite"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "windowBackgroundChecked"
            java.lang.String r7 = "windowBackgroundWhite"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "switchTrackBlue"
            java.lang.String r7 = "switchTrack"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "switchTrackBlueChecked"
            java.lang.String r7 = "switchTrackChecked"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "switchTrackBlueThumb"
            java.lang.String r7 = "windowBackgroundWhite"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "switchTrackBlueThumbChecked"
            java.lang.String r7 = "windowBackgroundWhite"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "windowBackgroundCheckText"
            java.lang.String r7 = "windowBackgroundWhiteBlackText"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "contextProgressInner4"
            java.lang.String r7 = "contextProgressInner1"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "contextProgressOuter4"
            java.lang.String r7 = "contextProgressOuter1"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "switchTrackBlueSelector"
            java.lang.String r7 = "listSelectorSDK21"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "switchTrackBlueSelectorChecked"
            java.lang.String r7 = "listSelectorSDK21"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_emojiBottomPanelIcon"
            java.lang.String r7 = "chat_emojiPanelIcon"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_emojiSearchIcon"
            java.lang.String r7 = "chat_emojiPanelIcon"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_emojiPanelStickerSetNameHighlight"
            java.lang.String r7 = "windowBackgroundWhiteBlueText4"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_emojiPanelStickerPackSelectorLine"
            java.lang.String r7 = "chat_emojiPanelIconSelected"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "sharedMedia_actionMode"
            java.lang.String r7 = "actionBarDefault"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "key_sheet_scrollUp"
            java.lang.String r7 = "chat_emojiPanelStickerPackSelector"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "key_sheet_other"
            java.lang.String r7 = "player_actionBarItems"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialogSearchBackground"
            java.lang.String r7 = "chat_emojiPanelStickerPackSelector"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialogSearchHint"
            java.lang.String r7 = "chat_emojiPanelIcon"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialogSearchIcon"
            java.lang.String r7 = "chat_emojiPanelIcon"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialogSearchText"
            java.lang.String r7 = "windowBackgroundWhiteBlackText"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialogFloatingButton"
            java.lang.String r7 = "dialogRoundCheckBox"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialogFloatingButtonPressed"
            java.lang.String r7 = "dialogRoundCheckBox"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialogFloatingIcon"
            java.lang.String r7 = "dialogRoundCheckBoxCheck"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialogShadowLine"
            java.lang.String r7 = "chat_emojiPanelShadowLine"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "actionBarDefaultArchived"
            java.lang.String r7 = "actionBarDefault"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "actionBarDefaultArchivedSelector"
            java.lang.String r7 = "actionBarDefaultSelector"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "actionBarDefaultArchivedIcon"
            java.lang.String r7 = "actionBarDefaultIcon"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "actionBarDefaultArchivedTitle"
            java.lang.String r7 = "actionBarDefaultTitle"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "actionBarDefaultArchivedSearch"
            java.lang.String r7 = "actionBarDefaultSearch"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "actionBarDefaultSearchArchivedPlaceholder"
            java.lang.String r7 = "actionBarDefaultSearchPlaceholder"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_message_threeLines"
            java.lang.String r7 = "chats_message"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_nameMessage_threeLines"
            java.lang.String r7 = "chats_nameMessage"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_nameArchived"
            java.lang.String r7 = "chats_name"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_nameMessageArchived"
            java.lang.String r7 = "chats_nameMessage"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_nameMessageArchived_threeLines"
            java.lang.String r7 = "chats_nameMessage"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_messageArchived"
            java.lang.String r7 = "chats_message"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "avatar_backgroundArchived"
            java.lang.String r7 = "chats_unreadCounterMuted"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_archiveBackground"
            java.lang.String r7 = "chats_actionBackground"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_archivePinBackground"
            java.lang.String r7 = "chats_unreadCounterMuted"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_archiveIcon"
            java.lang.String r7 = "chats_actionIcon"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_archiveText"
            java.lang.String r7 = "chats_actionIcon"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "actionBarDefaultSubmenuItemIcon"
            java.lang.String r7 = "dialogIcon"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "checkboxDisabled"
            java.lang.String r7 = "chats_unreadCounterMuted"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_status"
            java.lang.String r7 = "actionBarDefaultSubtitle"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_inDownCall"
            java.lang.String r7 = "calls_callReceivedGreenIcon"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_inUpCall"
            java.lang.String r7 = "calls_callReceivedRedIcon"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_outUpCall"
            java.lang.String r7 = "calls_callReceivedGreenIcon"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "actionBarTabActiveText"
            java.lang.String r7 = "actionBarDefaultTitle"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "actionBarTabUnactiveText"
            java.lang.String r7 = "actionBarDefaultSubtitle"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "actionBarTabLine"
            java.lang.String r7 = "actionBarDefaultTitle"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "actionBarTabSelector"
            java.lang.String r7 = "actionBarDefaultSelector"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "profile_status"
            java.lang.String r7 = "avatar_subtitleInProfileBlue"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_menuTopBackgroundCats"
            java.lang.String r7 = "avatar_backgroundActionBarBlue"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_attachPermissionImage"
            java.lang.String r7 = "dialogTextBlack"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_attachPermissionMark"
            java.lang.String r7 = "chat_sentError"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_attachPermissionText"
            java.lang.String r7 = "dialogTextBlack"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_attachEmptyImage"
            java.lang.String r7 = "emptyListPlaceholder"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "actionBarBrowser"
            java.lang.String r7 = "actionBarDefault"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_sentReadCheck"
            java.lang.String r7 = "chats_sentCheck"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_outSentCheckRead"
            java.lang.String r7 = "chat_outSentCheck"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_outSentCheckReadSelected"
            java.lang.String r7 = "chat_outSentCheckSelected"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_archivePullDownBackground"
            java.lang.String r7 = "chats_unreadCounterMuted"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_archivePullDownBackgroundActive"
            java.lang.String r7 = "chats_actionBackground"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "avatar_backgroundArchivedHidden"
            java.lang.String r7 = "avatar_backgroundSaved"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "featuredStickers_removeButtonText"
            java.lang.String r7 = "featuredStickers_addButtonPressed"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialogEmptyImage"
            java.lang.String r7 = "player_time"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialogEmptyText"
            java.lang.String r7 = "player_time"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "location_actionIcon"
            java.lang.String r7 = "dialogTextBlack"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "location_actionActiveIcon"
            java.lang.String r7 = "windowBackgroundWhiteBlueText7"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "location_actionBackground"
            java.lang.String r7 = "dialogBackground"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "location_actionPressedBackground"
            java.lang.String r7 = "dialogBackgroundGray"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "location_sendLocationText"
            java.lang.String r7 = "windowBackgroundWhiteBlueText7"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "location_sendLiveLocationText"
            java.lang.String r7 = "windowBackgroundWhiteGreenText"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_outTextSelectionHighlight"
            java.lang.String r7 = "chat_textSelectBackground"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_inTextSelectionHighlight"
            java.lang.String r7 = "chat_textSelectBackground"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_TextSelectionCursor"
            java.lang.String r7 = "chat_messagePanelCursor"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_inPollCorrectAnswer"
            java.lang.String r7 = "chat_attachLocationBackground"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_outPollCorrectAnswer"
            java.lang.String r7 = "chat_attachLocationBackground"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_inPollWrongAnswer"
            java.lang.String r7 = "chat_attachAudioBackground"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_outPollWrongAnswer"
            java.lang.String r7 = "chat_attachAudioBackground"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "profile_tabText"
            java.lang.String r7 = "windowBackgroundWhiteGrayText"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "profile_tabSelectedText"
            java.lang.String r7 = "windowBackgroundWhiteBlueHeader"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "profile_tabSelectedLine"
            java.lang.String r7 = "windowBackgroundWhiteBlueHeader"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "profile_tabSelector"
            java.lang.String r7 = "listSelectorSDK21"
            r0.put(r4, r7)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "statisticChartPopupBackground"
            java.lang.String r7 = "dialogBackground"
            r0.put(r4, r7)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String[] r4 = keys_avatar_background
            java.util.List r4 = java.util.Arrays.asList(r4)
            r0.addAll(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String[] r4 = keys_avatar_nameInMessage
            java.util.List r4 = java.util.Arrays.asList(r4)
            r0.addAll(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "chat_attachFileBackground"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "chat_attachGalleryBackground"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "chat_shareBackground"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "chat_shareBackgroundSelected"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outUpCall"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outBubble"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outBubbleSelected"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outBubbleShadow"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outBubbleGradient"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outSentCheck"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outSentCheckSelected"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outSentCheckRead"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outSentCheckReadSelected"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outSentClock"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outSentClockSelected"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outMediaIcon"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outMediaIconSelected"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outViews"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outViewsSelected"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outMenu"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outMenuSelected"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outInstant"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outInstantSelected"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outPreviewInstantText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outPreviewInstantSelectedText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outForwardedNameText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outViaBotNameText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outReplyLine"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outReplyNameText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outReplyMessageText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outReplyMediaMessageText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outReplyMediaMessageSelectedText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outPreviewLine"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outSiteNameText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outContactNameText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outContactPhoneText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outContactPhoneSelectedText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outAudioProgress"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outAudioSelectedProgress"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outTimeText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outTimeSelectedText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outAudioPerfomerText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outAudioPerfomerSelectedText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outAudioTitleText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outAudioDurationText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outAudioDurationSelectedText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outAudioSeekbar"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outAudioCacheSeekbar"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outAudioSeekbarSelected"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outAudioSeekbarFill"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outVoiceSeekbar"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outVoiceSeekbarSelected"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outVoiceSeekbarFill"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outFileProgress"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outFileProgressSelected"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outFileNameText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outFileInfoText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outFileInfoSelectedText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outFileBackground"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outFileBackgroundSelected"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outVenueInfoText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outVenueInfoSelectedText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outLoader"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outLoaderSelected"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outLoaderPhoto"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outLoaderPhotoSelected"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outLoaderPhotoIcon"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outLoaderPhotoIconSelected"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outLocationBackground"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outLocationIcon"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outContactBackground"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outContactIcon"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outFileIcon"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outFileSelectedIcon"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outBroadcast"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_messageTextOut"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_messageLinkOut"
            r0.add(r4)
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            themes = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            otherThemes = r0
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            themesDict = r0
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            currentColorsNoAccent = r0
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            currentColors = r0
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r4 = "themeconfig"
            android.content.SharedPreferences r4 = r0.getSharedPreferences(r4, r1)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = new org.telegram.ui.ActionBar.Theme$ThemeInfo
            r0.<init>()
            java.lang.String r7 = "Blue"
            r0.name = r7
            java.lang.String r7 = "bluebubbles.attheme"
            r0.assetName = r7
            r7 = -6963476(0xfffffffffvar_beec, float:NaN)
            int unused = r0.previewBackgroundColor = r7
            int unused = r0.previewInColor = r2
            r7 = -3086593(0xffffffffffd0e6ff, float:NaN)
            int unused = r0.previewOutColor = r7
            r0.firstAccentIsDefault = r5
            int r7 = DEFALT_THEME_ACCENT_ID
            r0.currentAccentId = r7
            r0.sortIndex = r5
            r7 = 16
            int[] r8 = new int[r7]
            r8 = {-10972987, -14444461, -3252606, -8428605, -14380627, -14050257, -7842636, -13464881, -12342073, -11359164, -3317869, -2981834, -8165684, -3256745, -2904512, -8681301} // fill-array
            int[] r9 = new int[r7]
            r9 = {-4660851, -328756, -1572, -4108434, -3031781, -1335, -198952, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r10 = new int[r7]
            r10 = {0, -853047, -264993, 0, 0, -135756, -198730, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r11 = new int[r7]
            r11 = {0, -2104672, -1918575, -2637335, -2305600, -1067658, -4152623, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r12 = new int[r7]
            r12 = {0, -4071005, -1318214, -1520170, -2039866, -1251471, -2175778, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r13 = new int[r7]
            r13 = {99, 9, 10, 11, 12, 13, 14, 0, 1, 2, 3, 4, 5, 6, 7, 8} // fill-array
            java.lang.String[] r14 = new java.lang.String[r7]
            r14[r1] = r3
            java.lang.String r15 = "p-pXcflrmFIBAAAAvXYQk-mCwZU"
            r14[r5] = r15
            java.lang.String r15 = "JqSUrO0-mFIBAAAAWwTvLzoWGQI"
            r14[r6] = r15
            java.lang.String r15 = "O-wmAfBPSFADAAAA4zINVfD_bro"
            r20 = 3
            r14[r20] = r15
            java.lang.String r15 = "RepJ5uE_SVABAAAAr4d0YhgB850"
            r19 = 4
            r14[r19] = r15
            java.lang.String r15 = "-Xc-np9y2VMCAAAARKr0yNNPYW0"
            r18 = 5
            r14[r18] = r15
            java.lang.String r15 = "dhf9pceaQVACAAAAbzdVo4SCiZA"
            r17 = 6
            r14[r17] = r15
            r15 = 7
            r14[r15] = r3
            r15 = 8
            r14[r15] = r3
            r15 = 9
            r14[r15] = r3
            r15 = 10
            r14[r15] = r3
            r15 = 11
            r14[r15] = r3
            r15 = 12
            r14[r15] = r3
            r15 = 13
            r14[r15] = r3
            r15 = 14
            r14[r15] = r3
            r2 = 15
            r14[r2] = r3
            int[] r2 = new int[r7]
            r2 = {0, 180, 45, 0, 45, 180, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r15 = new int[r7]
            r15 = {0, 52, 46, 57, 45, 64, 52, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            r21 = r0
            r22 = r8
            r23 = r9
            r24 = r10
            r25 = r11
            r26 = r12
            r27 = r13
            r28 = r14
            r29 = r2
            r30 = r15
            r21.setAccentColorOptions(r22, r23, r24, r25, r26, r27, r28, r29, r30)
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r2 = themes
            defaultTheme = r0
            currentTheme = r0
            currentDayTheme = r0
            r2.add(r0)
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r2 = themesDict
            java.lang.String r8 = "Blue"
            r2.put(r8, r0)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = new org.telegram.ui.ActionBar.Theme$ThemeInfo
            r0.<init>()
            java.lang.String r2 = "Dark Blue"
            r0.name = r2
            java.lang.String r2 = "darkblue.attheme"
            r0.assetName = r2
            r2 = -10523006(0xffffffffff5f6e82, float:-2.9699163E38)
            int unused = r0.previewBackgroundColor = r2
            r2 = -9009508(0xfffffffffvar_c, float:-3.2768902E38)
            int unused = r0.previewInColor = r2
            r2 = -8214301(0xfffffffffvar_a8e3, float:NaN)
            int unused = r0.previewOutColor = r2
            r2 = 3
            r0.sortIndex = r2
            r2 = 18
            int[] r8 = new int[r2]
            r8 = {-7177260, -9860357, -14440464, -8687151, -9848491, -14053142, -9403671, -10044691, -13203974, -12138259, -11880383, -1344335, -1142742, -6127120, -2931932, -1131212, -8417365, -13270557} // fill-array
            int[] r9 = new int[r2]
            r9 = {-6464359, -10267323, -13532789, -5413850, -11898828, -13410942, -13215889, -10914461, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r10 = new int[r2]
            r10 = {-10465880, -9937588, -14983040, -6736562, -14197445, -13534568, -13144441, -10587280, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r11 = new int[r2]
            r11 = {-14147282, -15263198, -16310753, -15724781, -15459054, -16313828, -14802903, -16645117, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r12 = new int[r2]
            r12 = {-15593453, -14277074, -15459034, -14541276, -15064812, -14932432, -15461096, -15393761, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r13 = new int[r2]
            r13 = {11, 12, 13, 14, 15, 16, 17, 18, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9} // fill-array
            java.lang.String[] r14 = new java.lang.String[r2]
            java.lang.String r15 = "O-wmAfBPSFADAAAA4zINVfD_bro"
            r14[r1] = r15
            java.lang.String r15 = "RepJ5uE_SVABAAAAr4d0YhgB850"
            r14[r5] = r15
            java.lang.String r15 = "dk_wwlghOFACAAAAfz9xrxi6euw"
            r14[r6] = r15
            java.lang.String r15 = "9LW_RcoOSVACAAAAFTk3DTyXN-M"
            r20 = 3
            r14[r20] = r15
            java.lang.String r15 = "PllZ-bf_SFAEAAAA8crRfwZiDNg"
            r19 = 4
            r14[r19] = r15
            java.lang.String r15 = "-Xc-np9y2VMCAAAARKr0yNNPYW0"
            r18 = 5
            r14[r18] = r15
            java.lang.String r15 = "kO4jyq55SFABAAAA0WEpcLfahXk"
            r17 = 6
            r14[r17] = r15
            java.lang.String r15 = "CJNyxPMgSVAEAAAAvW9sMwCLASSNAMEcw"
            r16 = 7
            r14[r16] = r15
            r15 = 8
            r14[r15] = r3
            r15 = 9
            r14[r15] = r3
            r15 = 10
            r14[r15] = r3
            r15 = 11
            r14[r15] = r3
            r15 = 12
            r14[r15] = r3
            r15 = 13
            r14[r15] = r3
            r15 = 14
            r14[r15] = r3
            r15 = 15
            r14[r15] = r3
            r14[r7] = r3
            r7 = 17
            r14[r7] = r3
            int[] r7 = new int[r2]
            r7 = {225, 45, 225, 135, 45, 225, 45, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r2 = new int[r2]
            r2 = {40, 40, 31, 50, 25, 34, 35, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            r21 = r0
            r22 = r8
            r23 = r9
            r24 = r10
            r25 = r11
            r26 = r12
            r27 = r13
            r28 = r14
            r29 = r7
            r30 = r2
            r21.setAccentColorOptions(r22, r23, r24, r25, r26, r27, r28, r29, r30)
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r2 = themes
            r2.add(r0)
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r2 = themesDict
            currentNightTheme = r0
            java.lang.String r7 = "Dark Blue"
            r2.put(r7, r0)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = new org.telegram.ui.ActionBar.Theme$ThemeInfo
            r0.<init>()
            java.lang.String r2 = "Arctic Blue"
            r0.name = r2
            java.lang.String r2 = "arctic.attheme"
            r0.assetName = r2
            r2 = -1971728(0xffffffffffe1e9f0, float:NaN)
            int unused = r0.previewBackgroundColor = r2
            r2 = -1
            int unused = r0.previewInColor = r2
            r2 = -9657877(0xffffffffff6ca1eb, float:-3.1453853E38)
            int unused = r0.previewOutColor = r2
            r2 = 5
            r0.sortIndex = r2
            r2 = 15
            int[] r7 = new int[r2]
            r7 = {-12537374, -12472227, -3240928, -11033621, -2194124, -3382903, -13332245, -12342073, -11359164, -3317869, -2981834, -8165684, -3256745, -2904512, -8681301} // fill-array
            int[] r8 = new int[r2]
            r8 = {-13525046, -14113959, -7579073, -13597229, -3581840, -8883763, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r9 = new int[r2]
            r9 = {-11616542, -9716647, -6400452, -12008744, -2592697, -4297041, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r10 = new int[r2]
            r10 = {-4922384, -2236758, -2437983, -1838093, -1120848, -1712148, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r11 = new int[r2]
            r11 = {-918020, -3544650, -1908290, -3610898, -1130838, -1980692, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r12 = new int[r2]
            r12 = {9, 10, 11, 12, 13, 14, 0, 1, 2, 3, 4, 5, 6, 7, 8} // fill-array
            java.lang.String[] r13 = new java.lang.String[r2]
            java.lang.String r2 = "MIo6r0qGSFAFAAAAtL8TsDzNX60"
            r13[r1] = r2
            java.lang.String r2 = "dhf9pceaQVACAAAAbzdVo4SCiZA"
            r13[r5] = r2
            java.lang.String r2 = "fqv01SQemVIBAAAApND8LDRUhRU"
            r13[r6] = r2
            java.lang.String r2 = "p-pXcflrmFIBAAAAvXYQk-mCwZU"
            r14 = 3
            r13[r14] = r2
            java.lang.String r2 = "JqSUrO0-mFIBAAAAWwTvLzoWGQI"
            r14 = 4
            r13[r14] = r2
            java.lang.String r2 = "F5oWoCs7QFACAAAAgf2bD_mg8Bw"
            r14 = 5
            r13[r14] = r2
            r2 = 6
            r13[r2] = r3
            r2 = 7
            r13[r2] = r3
            r2 = 8
            r13[r2] = r3
            r2 = 9
            r13[r2] = r3
            r2 = 10
            r13[r2] = r3
            r2 = 11
            r13[r2] = r3
            r2 = 12
            r13[r2] = r3
            r2 = 13
            r13[r2] = r3
            r2 = 14
            r13[r2] = r3
            r2 = 15
            int[] r14 = new int[r2]
            r14 = {315, 315, 225, 315, 0, 180, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r2 = new int[r2]
            r2 = {50, 50, 58, 47, 46, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            r21 = r0
            r22 = r7
            r23 = r8
            r24 = r9
            r25 = r10
            r26 = r11
            r27 = r12
            r28 = r13
            r29 = r14
            r30 = r2
            r21.setAccentColorOptions(r22, r23, r24, r25, r26, r27, r28, r29, r30)
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r2 = themes
            r2.add(r0)
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r2 = themesDict
            java.lang.String r7 = "Arctic Blue"
            r2.put(r7, r0)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = new org.telegram.ui.ActionBar.Theme$ThemeInfo
            r0.<init>()
            java.lang.String r2 = "Day"
            r0.name = r2
            java.lang.String r2 = "day.attheme"
            r0.assetName = r2
            r2 = -1
            int unused = r0.previewBackgroundColor = r2
            r2 = -1315084(0xffffffffffebeef4, float:NaN)
            int unused = r0.previewInColor = r2
            r2 = -8604930(0xffffffffff7cb2fe, float:-3.3589484E38)
            int unused = r0.previewOutColor = r2
            r0.sortIndex = r6
            r2 = 14
            int[] r7 = new int[r2]
            r7 = {-11099447, -3379581, -3109305, -3382174, -7963438, -11759137, -11029287, -11226775, -2506945, -3382174, -3379581, -6587438, -2649788, -8681301} // fill-array
            int[] r8 = new int[r2]
            r8 = {-10125092, -9671214, -3451775, -3978678, -10711329, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r9 = new int[r2]
            r9 = {-12664362, -3642988, -2383569, -3109317, -11422261, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r10 = new int[r2]
            r10 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r11 = new int[r2]
            r11 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r12 = new int[r2]
            r12 = {9, 10, 11, 12, 13, 0, 1, 2, 3, 4, 5, 6, 7, 8} // fill-array
            java.lang.String[] r13 = new java.lang.String[r2]
            r13[r1] = r3
            r13[r5] = r3
            r13[r6] = r3
            r2 = 3
            r13[r2] = r3
            r2 = 4
            r13[r2] = r3
            r2 = 5
            r13[r2] = r3
            r2 = 6
            r13[r2] = r3
            r2 = 7
            r13[r2] = r3
            r2 = 8
            r13[r2] = r3
            r2 = 9
            r13[r2] = r3
            r2 = 10
            r13[r2] = r3
            r2 = 11
            r13[r2] = r3
            r2 = 12
            r13[r2] = r3
            r2 = 13
            r13[r2] = r3
            r2 = 14
            int[] r14 = new int[r2]
            r14 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r15 = new int[r2]
            r15 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            r21 = r0
            r22 = r7
            r23 = r8
            r24 = r9
            r25 = r10
            r26 = r11
            r27 = r12
            r28 = r13
            r29 = r14
            r30 = r15
            r21.setAccentColorOptions(r22, r23, r24, r25, r26, r27, r28, r29, r30)
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r2 = themes
            r2.add(r0)
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r2 = themesDict
            java.lang.String r7 = "Day"
            r2.put(r7, r0)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = new org.telegram.ui.ActionBar.Theme$ThemeInfo
            r0.<init>()
            java.lang.String r2 = "Night"
            r0.name = r2
            java.lang.String r2 = "night.attheme"
            r0.assetName = r2
            r2 = -11315623(0xfffffffffvar_, float:-2.8091545E38)
            int unused = r0.previewBackgroundColor = r2
            r2 = -9143676(0xfffffffffvar_a84, float:-3.2496777E38)
            int unused = r0.previewInColor = r2
            r2 = -9067802(0xfffffffffvar_a2e6, float:-3.2650668E38)
            int unused = r0.previewOutColor = r2
            r2 = 4
            r0.sortIndex = r2
            r2 = 14
            int[] r7 = new int[r2]
            r7 = {-9781697, -7505693, -2204034, -10913816, -2375398, -12678921, -11881005, -11880383, -2534026, -1934037, -7115558, -3128522, -1528292, -8812381} // fill-array
            int[] r8 = new int[r2]
            r8 = {-7712108, -4953061, -5288081, -14258547, -9154889, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r9 = new int[r2]
            r9 = {-9939525, -5948598, -10335844, -13659747, -14054507, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r10 = new int[r2]
            r10 = {-16644350, -15658220, -16514300, -16053236, -16382457, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r11 = new int[r2]
            r11 = {-15790576, -16250871, -16448251, -15856112, -15921904, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r12 = new int[r2]
            r12 = {9, 10, 11, 12, 13, 0, 1, 2, 3, 4, 5, 6, 7, 8} // fill-array
            java.lang.String[] r13 = new java.lang.String[r2]
            java.lang.String r2 = "YIxYGEALQVADAAAAA3QbEH0AowY"
            r13[r1] = r2
            java.lang.String r2 = "9LW_RcoOSVACAAAAFTk3DTyXN-M"
            r13[r5] = r2
            java.lang.String r2 = "O-wmAfBPSFADAAAA4zINVfD_bro"
            r13[r6] = r2
            java.lang.String r2 = "F5oWoCs7QFACAAAAgf2bD_mg8Bw"
            r14 = 3
            r13[r14] = r2
            java.lang.String r2 = "-Xc-np9y2VMCAAAARKr0yNNPYW0"
            r14 = 4
            r13[r14] = r2
            r2 = 5
            r13[r2] = r3
            r2 = 6
            r13[r2] = r3
            r2 = 7
            r13[r2] = r3
            r2 = 8
            r13[r2] = r3
            r2 = 9
            r13[r2] = r3
            r2 = 10
            r13[r2] = r3
            r2 = 11
            r13[r2] = r3
            r2 = 12
            r13[r2] = r3
            r2 = 13
            r13[r2] = r3
            r2 = 14
            int[] r14 = new int[r2]
            r14 = {45, 135, 0, 180, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r2 = new int[r2]
            r2 = {34, 47, 52, 48, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            r21 = r0
            r22 = r7
            r23 = r8
            r24 = r9
            r25 = r10
            r26 = r11
            r27 = r12
            r28 = r13
            r29 = r14
            r30 = r2
            r21.setAccentColorOptions(r22, r23, r24, r25, r26, r27, r28, r29, r30)
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r2 = themes
            r2.add(r0)
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r2 = themesDict
            java.lang.String r7 = "Night"
            r2.put(r7, r0)
            r2 = 0
            java.lang.String r0 = "themes2"
            java.lang.String r0 = r4.getString(r0, r2)
            r7 = 0
        L_0x2a49:
            r8 = 3
            if (r7 >= r8) goto L_0x2a91
            int[] r8 = remoteThemesHash
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "remoteThemesHash"
            r9.append(r10)
            if (r7 == 0) goto L_0x2a5f
            java.lang.Integer r10 = java.lang.Integer.valueOf(r7)
            goto L_0x2a60
        L_0x2a5f:
            r10 = r3
        L_0x2a60:
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            int r9 = r4.getInt(r9, r1)
            r8[r7] = r9
            int[] r8 = lastLoadingThemesTime
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "lastLoadingThemesTime"
            r9.append(r10)
            if (r7 == 0) goto L_0x2a80
            java.lang.Integer r10 = java.lang.Integer.valueOf(r7)
            goto L_0x2a81
        L_0x2a80:
            r10 = r3
        L_0x2a81:
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            int r9 = r4.getInt(r9, r1)
            r8[r7] = r9
            int r7 = r7 + 1
            goto L_0x2a49
        L_0x2a91:
            boolean r7 = android.text.TextUtils.isEmpty(r0)
            if (r7 != 0) goto L_0x2acb
            org.json.JSONArray r7 = new org.json.JSONArray     // Catch:{ Exception -> 0x2ac6 }
            r7.<init>(r0)     // Catch:{ Exception -> 0x2ac6 }
            r0 = 0
        L_0x2a9d:
            int r8 = r7.length()     // Catch:{ Exception -> 0x2ac6 }
            if (r0 >= r8) goto L_0x2b0f
            org.json.JSONObject r8 = r7.getJSONObject(r0)     // Catch:{ Exception -> 0x2ac6 }
            org.telegram.ui.ActionBar.Theme$ThemeInfo r8 = org.telegram.ui.ActionBar.Theme.ThemeInfo.createWithJson(r8)     // Catch:{ Exception -> 0x2ac6 }
            if (r8 == 0) goto L_0x2ac3
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r9 = otherThemes     // Catch:{ Exception -> 0x2ac6 }
            r9.add(r8)     // Catch:{ Exception -> 0x2ac6 }
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r9 = themes     // Catch:{ Exception -> 0x2ac6 }
            r9.add(r8)     // Catch:{ Exception -> 0x2ac6 }
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r9 = themesDict     // Catch:{ Exception -> 0x2ac6 }
            java.lang.String r10 = r8.getKey()     // Catch:{ Exception -> 0x2ac6 }
            r9.put(r10, r8)     // Catch:{ Exception -> 0x2ac6 }
            r8.loadWallpapers(r4)     // Catch:{ Exception -> 0x2ac6 }
        L_0x2ac3:
            int r0 = r0 + 1
            goto L_0x2a9d
        L_0x2ac6:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x2b0f
        L_0x2acb:
            java.lang.String r0 = "themes"
            java.lang.String r0 = r4.getString(r0, r2)
            boolean r7 = android.text.TextUtils.isEmpty(r0)
            if (r7 != 0) goto L_0x2b0f
            java.lang.String r7 = "&"
            java.lang.String[] r0 = r0.split(r7)
            r7 = 0
        L_0x2ade:
            int r8 = r0.length
            if (r7 >= r8) goto L_0x2aff
            r8 = r0[r7]
            org.telegram.ui.ActionBar.Theme$ThemeInfo r8 = org.telegram.ui.ActionBar.Theme.ThemeInfo.createWithString(r8)
            if (r8 == 0) goto L_0x2afc
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r9 = otherThemes
            r9.add(r8)
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r9 = themes
            r9.add(r8)
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r9 = themesDict
            java.lang.String r10 = r8.getKey()
            r9.put(r10, r8)
        L_0x2afc:
            int r7 = r7 + 1
            goto L_0x2ade
        L_0x2aff:
            saveOtherThemes(r5, r5)
            android.content.SharedPreferences$Editor r0 = r4.edit()
            java.lang.String r7 = "themes"
            android.content.SharedPreferences$Editor r0 = r0.remove(r7)
            r0.commit()
        L_0x2b0f:
            sortThemes()
            android.content.SharedPreferences r7 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r0 = themesDict     // Catch:{ Exception -> 0x2f0d }
            java.lang.String r8 = "Dark Blue"
            java.lang.Object r0 = r0.get(r8)     // Catch:{ Exception -> 0x2f0d }
            r8 = r0
            org.telegram.ui.ActionBar.Theme$ThemeInfo r8 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r8     // Catch:{ Exception -> 0x2f0d }
            java.lang.String r0 = "theme"
            java.lang.String r0 = r7.getString(r0, r2)     // Catch:{ Exception -> 0x2f0d }
            java.lang.String r9 = "Default"
            boolean r9 = r9.equals(r0)     // Catch:{ Exception -> 0x2f0d }
            if (r9 == 0) goto L_0x2b42
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r0 = themesDict     // Catch:{ Exception -> 0x2b3f }
            java.lang.String r9 = "Blue"
            java.lang.Object r0 = r0.get(r9)     // Catch:{ Exception -> 0x2b3f }
            r9 = r0
            org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r9     // Catch:{ Exception -> 0x2b3f }
            int r0 = DEFALT_THEME_ACCENT_ID     // Catch:{ Exception -> 0x2f0a }
            r9.currentAccentId = r0     // Catch:{ Exception -> 0x2f0a }
            goto L_0x2b7b
        L_0x2b3f:
            r0 = move-exception
            goto L_0x2f0f
        L_0x2b42:
            java.lang.String r9 = "Dark"
            boolean r9 = r9.equals(r0)     // Catch:{ Exception -> 0x2f0d }
            if (r9 == 0) goto L_0x2b54
            r0 = 9
            r8.currentAccentId = r0     // Catch:{ Exception -> 0x2b50 }
            r9 = r8
            goto L_0x2b7b
        L_0x2b50:
            r0 = move-exception
            r2 = r8
            goto L_0x2f0f
        L_0x2b54:
            if (r0 == 0) goto L_0x2b7a
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r9 = themesDict     // Catch:{ Exception -> 0x2b3f }
            java.lang.Object r0 = r9.get(r0)     // Catch:{ Exception -> 0x2b3f }
            r9 = r0
            org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r9     // Catch:{ Exception -> 0x2b3f }
            if (r9 == 0) goto L_0x2b7b
            java.lang.String r0 = "lastDayTheme"
            boolean r0 = r4.contains(r0)     // Catch:{ Exception -> 0x2f0a }
            if (r0 != 0) goto L_0x2b7b
            android.content.SharedPreferences$Editor r0 = r4.edit()     // Catch:{ Exception -> 0x2f0a }
            java.lang.String r10 = "lastDayTheme"
            java.lang.String r11 = r9.getKey()     // Catch:{ Exception -> 0x2f0a }
            r0.putString(r10, r11)     // Catch:{ Exception -> 0x2f0a }
            r0.commit()     // Catch:{ Exception -> 0x2f0a }
            goto L_0x2b7b
        L_0x2b7a:
            r9 = r2
        L_0x2b7b:
            java.lang.String r0 = "nighttheme"
            java.lang.String r0 = r7.getString(r0, r2)     // Catch:{ Exception -> 0x2f0a }
            java.lang.String r10 = "Default"
            boolean r10 = r10.equals(r0)     // Catch:{ Exception -> 0x2f0a }
            if (r10 == 0) goto L_0x2b9a
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r0 = themesDict     // Catch:{ Exception -> 0x2f0a }
            java.lang.String r8 = "Blue"
            java.lang.Object r0 = r0.get(r8)     // Catch:{ Exception -> 0x2f0a }
            r8 = r0
            org.telegram.ui.ActionBar.Theme$ThemeInfo r8 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r8     // Catch:{ Exception -> 0x2f0a }
            int r0 = DEFALT_THEME_ACCENT_ID     // Catch:{ Exception -> 0x2b50 }
            r8.currentAccentId = r0     // Catch:{ Exception -> 0x2b50 }
            r9 = r8
            goto L_0x2bb7
        L_0x2b9a:
            java.lang.String r10 = "Dark"
            boolean r10 = r10.equals(r0)     // Catch:{ Exception -> 0x2f0a }
            if (r10 == 0) goto L_0x2ba9
            currentNightTheme = r8     // Catch:{ Exception -> 0x2f0a }
            r0 = 9
            r8.currentAccentId = r0     // Catch:{ Exception -> 0x2f0a }
            goto L_0x2bb7
        L_0x2ba9:
            if (r0 == 0) goto L_0x2bb7
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r8 = themesDict     // Catch:{ Exception -> 0x2f0a }
            java.lang.Object r0 = r8.get(r0)     // Catch:{ Exception -> 0x2f0a }
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r0     // Catch:{ Exception -> 0x2f0a }
            if (r0 == 0) goto L_0x2bb7
            currentNightTheme = r0     // Catch:{ Exception -> 0x2f0a }
        L_0x2bb7:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = currentNightTheme     // Catch:{ Exception -> 0x2var_ }
            if (r0 == 0) goto L_0x2bd5
            java.lang.String r0 = "lastDarkTheme"
            boolean r0 = r4.contains(r0)     // Catch:{ Exception -> 0x2f0a }
            if (r0 != 0) goto L_0x2bd5
            android.content.SharedPreferences$Editor r0 = r4.edit()     // Catch:{ Exception -> 0x2f0a }
            java.lang.String r8 = "lastDarkTheme"
            org.telegram.ui.ActionBar.Theme$ThemeInfo r10 = currentNightTheme     // Catch:{ Exception -> 0x2f0a }
            java.lang.String r10 = r10.getKey()     // Catch:{ Exception -> 0x2f0a }
            r0.putString(r8, r10)     // Catch:{ Exception -> 0x2f0a }
            r0.commit()     // Catch:{ Exception -> 0x2f0a }
        L_0x2bd5:
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r0 = themesDict     // Catch:{ Exception -> 0x2var_ }
            java.util.Collection r0 = r0.values()     // Catch:{ Exception -> 0x2var_ }
            java.util.Iterator r8 = r0.iterator()     // Catch:{ Exception -> 0x2var_ }
            r10 = r2
            r11 = r10
        L_0x2be1:
            boolean r0 = r8.hasNext()     // Catch:{ Exception -> 0x2var_ }
            if (r0 == 0) goto L_0x2e5f
            java.lang.Object r0 = r8.next()     // Catch:{ Exception -> 0x2var_ }
            r12 = r0
            org.telegram.ui.ActionBar.Theme$ThemeInfo r12 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r12     // Catch:{ Exception -> 0x2var_ }
            java.lang.String r0 = r12.assetName     // Catch:{ Exception -> 0x2var_ }
            if (r0 == 0) goto L_0x2e50
            int r0 = r12.accentBaseColor     // Catch:{ Exception -> 0x2var_ }
            if (r0 == 0) goto L_0x2e50
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x2var_ }
            r0.<init>()     // Catch:{ Exception -> 0x2var_ }
            java.lang.String r13 = "accents_"
            r0.append(r13)     // Catch:{ Exception -> 0x2var_ }
            java.lang.String r13 = r12.assetName     // Catch:{ Exception -> 0x2var_ }
            r0.append(r13)     // Catch:{ Exception -> 0x2var_ }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x2var_ }
            java.lang.String r0 = r4.getString(r0, r2)     // Catch:{ Exception -> 0x2var_ }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x2var_ }
            r13.<init>()     // Catch:{ Exception -> 0x2var_ }
            java.lang.String r14 = "accent_current_"
            r13.append(r14)     // Catch:{ Exception -> 0x2var_ }
            java.lang.String r14 = r12.assetName     // Catch:{ Exception -> 0x2var_ }
            r13.append(r14)     // Catch:{ Exception -> 0x2var_ }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x2var_ }
            boolean r14 = r12.firstAccentIsDefault     // Catch:{ Exception -> 0x2var_ }
            if (r14 == 0) goto L_0x2CLASSNAME
            int r14 = DEFALT_THEME_ACCENT_ID     // Catch:{ Exception -> 0x2f0a }
            goto L_0x2CLASSNAME
        L_0x2CLASSNAME:
            r14 = 0
        L_0x2CLASSNAME:
            int r13 = r4.getInt(r13, r14)     // Catch:{ Exception -> 0x2var_ }
            r12.currentAccentId = r13     // Catch:{ Exception -> 0x2var_ }
            java.util.ArrayList r13 = new java.util.ArrayList     // Catch:{ Exception -> 0x2var_ }
            r13.<init>()     // Catch:{ Exception -> 0x2var_ }
            boolean r14 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x2var_ }
            if (r14 != 0) goto L_0x2d24
            org.telegram.tgnet.SerializedData r14 = new org.telegram.tgnet.SerializedData     // Catch:{ all -> 0x2d11 }
            r15 = 3
            byte[] r0 = android.util.Base64.decode(r0, r15)     // Catch:{ all -> 0x2d11 }
            r14.<init>((byte[]) r0)     // Catch:{ all -> 0x2d11 }
            int r0 = r14.readInt32(r5)     // Catch:{ all -> 0x2d11 }
            int r15 = r14.readInt32(r5)     // Catch:{ all -> 0x2d11 }
            r2 = 0
        L_0x2c4c:
            if (r2 >= r15) goto L_0x2d0b
            org.telegram.ui.ActionBar.Theme$ThemeAccent r1 = new org.telegram.ui.ActionBar.Theme$ThemeAccent     // Catch:{ all -> 0x2d11 }
            r1.<init>()     // Catch:{ all -> 0x2d11 }
            int r6 = r14.readInt32(r5)     // Catch:{ all -> 0x2d11 }
            r1.id = r6     // Catch:{ all -> 0x2d11 }
            int r6 = r14.readInt32(r5)     // Catch:{ all -> 0x2d11 }
            r1.accentColor = r6     // Catch:{ all -> 0x2d11 }
            r1.parentTheme = r12     // Catch:{ all -> 0x2d11 }
            int r6 = r14.readInt32(r5)     // Catch:{ all -> 0x2d11 }
            r1.myMessagesAccentColor = r6     // Catch:{ all -> 0x2d11 }
            int r6 = r14.readInt32(r5)     // Catch:{ all -> 0x2d11 }
            r1.myMessagesGradientAccentColor = r6     // Catch:{ all -> 0x2d11 }
            r6 = 3
            if (r0 < r6) goto L_0x2c7a
            r22 = r8
            r6 = r9
            long r8 = r14.readInt64(r5)     // Catch:{ all -> 0x2d07 }
            r1.backgroundOverrideColor = r8     // Catch:{ all -> 0x2d07 }
            goto L_0x2CLASSNAME
        L_0x2c7a:
            r22 = r8
            r6 = r9
            int r8 = r14.readInt32(r5)     // Catch:{ all -> 0x2d07 }
            long r8 = (long) r8     // Catch:{ all -> 0x2d07 }
            r1.backgroundOverrideColor = r8     // Catch:{ all -> 0x2d07 }
        L_0x2CLASSNAME:
            r8 = 2
            if (r0 < r8) goto L_0x2c8e
            long r8 = r14.readInt64(r5)     // Catch:{ all -> 0x2d07 }
            r1.backgroundGradientOverrideColor = r8     // Catch:{ all -> 0x2d07 }
            goto L_0x2CLASSNAME
        L_0x2c8e:
            int r8 = r14.readInt32(r5)     // Catch:{ all -> 0x2d07 }
            long r8 = (long) r8     // Catch:{ all -> 0x2d07 }
            r1.backgroundGradientOverrideColor = r8     // Catch:{ all -> 0x2d07 }
        L_0x2CLASSNAME:
            if (r0 < r5) goto L_0x2c9d
            int r8 = r14.readInt32(r5)     // Catch:{ all -> 0x2d07 }
            r1.backgroundRotation = r8     // Catch:{ all -> 0x2d07 }
        L_0x2c9d:
            r8 = 4
            if (r0 < r8) goto L_0x2cb9
            r14.readInt64(r5)     // Catch:{ all -> 0x2d07 }
            double r8 = r14.readDouble(r5)     // Catch:{ all -> 0x2d07 }
            float r8 = (float) r8     // Catch:{ all -> 0x2d07 }
            r1.patternIntensity = r8     // Catch:{ all -> 0x2d07 }
            boolean r8 = r14.readBool(r5)     // Catch:{ all -> 0x2d07 }
            r1.patternMotion = r8     // Catch:{ all -> 0x2d07 }
            r8 = 5
            if (r0 < r8) goto L_0x2cba
            java.lang.String r8 = r14.readString(r5)     // Catch:{ all -> 0x2d07 }
            r1.patternSlug = r8     // Catch:{ all -> 0x2d07 }
        L_0x2cb9:
            r8 = 5
        L_0x2cba:
            if (r0 < r8) goto L_0x2cd4
            boolean r8 = r14.readBool(r5)     // Catch:{ all -> 0x2d07 }
            if (r8 == 0) goto L_0x2cd4
            int r8 = r14.readInt32(r5)     // Catch:{ all -> 0x2d07 }
            r1.account = r8     // Catch:{ all -> 0x2d07 }
            int r8 = r14.readInt32(r5)     // Catch:{ all -> 0x2d07 }
            org.telegram.tgnet.TLRPC$Theme r8 = org.telegram.tgnet.TLRPC$Theme.TLdeserialize(r14, r8, r5)     // Catch:{ all -> 0x2d07 }
            org.telegram.tgnet.TLRPC$TL_theme r8 = (org.telegram.tgnet.TLRPC$TL_theme) r8     // Catch:{ all -> 0x2d07 }
            r1.info = r8     // Catch:{ all -> 0x2d07 }
        L_0x2cd4:
            android.util.SparseArray<org.telegram.ui.ActionBar.Theme$ThemeAccent> r8 = r12.themeAccentsMap     // Catch:{ all -> 0x2d07 }
            int r9 = r1.id     // Catch:{ all -> 0x2d07 }
            r8.put(r9, r1)     // Catch:{ all -> 0x2d07 }
            org.telegram.tgnet.TLRPC$TL_theme r8 = r1.info     // Catch:{ all -> 0x2d07 }
            if (r8 == 0) goto L_0x2ceb
            android.util.LongSparseArray<org.telegram.ui.ActionBar.Theme$ThemeAccent> r8 = r12.accentsByThemeId     // Catch:{ all -> 0x2d07 }
            org.telegram.tgnet.TLRPC$TL_theme r9 = r1.info     // Catch:{ all -> 0x2d07 }
            r24 = r6
            long r5 = r9.id     // Catch:{ all -> 0x2d05 }
            r8.put(r5, r1)     // Catch:{ all -> 0x2d05 }
            goto L_0x2ced
        L_0x2ceb:
            r24 = r6
        L_0x2ced:
            r13.add(r1)     // Catch:{ all -> 0x2d05 }
            int r5 = r12.lastAccentId     // Catch:{ all -> 0x2d05 }
            int r1 = r1.id     // Catch:{ all -> 0x2d05 }
            int r1 = java.lang.Math.max(r5, r1)     // Catch:{ all -> 0x2d05 }
            r12.lastAccentId = r1     // Catch:{ all -> 0x2d05 }
            int r2 = r2 + 1
            r8 = r22
            r9 = r24
            r1 = 0
            r5 = 1
            r6 = 2
            goto L_0x2c4c
        L_0x2d05:
            r0 = move-exception
            goto L_0x2d16
        L_0x2d07:
            r0 = move-exception
            r24 = r6
            goto L_0x2d16
        L_0x2d0b:
            r22 = r8
            r24 = r9
            r2 = 5
            goto L_0x2d1b
        L_0x2d11:
            r0 = move-exception
            r22 = r8
            r24 = r9
        L_0x2d16:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x2d1e }
        L_0x2d19:
            r2 = 5
            r5 = 1
        L_0x2d1b:
            r6 = 3
            goto L_0x2e14
        L_0x2d1e:
            r0 = move-exception
            r2 = r24
            r5 = 1
            goto L_0x2f0f
        L_0x2d24:
            r22 = r8
            r24 = r9
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x2e4c }
            r0.<init>()     // Catch:{ Exception -> 0x2e4c }
            java.lang.String r1 = "accent_for_"
            r0.append(r1)     // Catch:{ Exception -> 0x2e4c }
            java.lang.String r1 = r12.assetName     // Catch:{ Exception -> 0x2e4c }
            r0.append(r1)     // Catch:{ Exception -> 0x2e4c }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x2e4c }
            r1 = 0
            int r2 = r7.getInt(r0, r1)     // Catch:{ Exception -> 0x2e4c }
            if (r2 == 0) goto L_0x2d19
            if (r10 != 0) goto L_0x2d4c
            android.content.SharedPreferences$Editor r10 = r7.edit()     // Catch:{ Exception -> 0x2d1e }
            android.content.SharedPreferences$Editor r11 = r4.edit()     // Catch:{ Exception -> 0x2d1e }
        L_0x2d4c:
            r10.remove(r0)     // Catch:{ Exception -> 0x2e4c }
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeAccent> r0 = r12.themeAccents     // Catch:{ Exception -> 0x2e4c }
            int r0 = r0.size()     // Catch:{ Exception -> 0x2e4c }
            r1 = 0
        L_0x2d56:
            if (r1 >= r0) goto L_0x2d6d
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeAccent> r5 = r12.themeAccents     // Catch:{ Exception -> 0x2d1e }
            java.lang.Object r5 = r5.get(r1)     // Catch:{ Exception -> 0x2d1e }
            org.telegram.ui.ActionBar.Theme$ThemeAccent r5 = (org.telegram.ui.ActionBar.Theme.ThemeAccent) r5     // Catch:{ Exception -> 0x2d1e }
            int r6 = r5.accentColor     // Catch:{ Exception -> 0x2d1e }
            if (r6 != r2) goto L_0x2d6a
            int r0 = r5.id     // Catch:{ Exception -> 0x2d1e }
            r12.currentAccentId = r0     // Catch:{ Exception -> 0x2d1e }
            r0 = 1
            goto L_0x2d6e
        L_0x2d6a:
            int r1 = r1 + 1
            goto L_0x2d56
        L_0x2d6d:
            r0 = 0
        L_0x2d6e:
            if (r0 != 0) goto L_0x2df9
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = new org.telegram.ui.ActionBar.Theme$ThemeAccent     // Catch:{ Exception -> 0x2e4c }
            r0.<init>()     // Catch:{ Exception -> 0x2e4c }
            r1 = 100
            r0.id = r1     // Catch:{ Exception -> 0x2e4c }
            r0.accentColor = r2     // Catch:{ Exception -> 0x2e4c }
            r0.parentTheme = r12     // Catch:{ Exception -> 0x2e4c }
            android.util.SparseArray<org.telegram.ui.ActionBar.Theme$ThemeAccent> r2 = r12.themeAccentsMap     // Catch:{ Exception -> 0x2e4c }
            r2.put(r1, r0)     // Catch:{ Exception -> 0x2e4c }
            r1 = 0
            r13.add(r1, r0)     // Catch:{ Exception -> 0x2e4c }
            r1 = 100
            r12.currentAccentId = r1     // Catch:{ Exception -> 0x2e4c }
            r1 = 101(0x65, float:1.42E-43)
            r12.lastAccentId = r1     // Catch:{ Exception -> 0x2e4c }
            org.telegram.tgnet.SerializedData r1 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x2e4c }
            r2 = 68
            r1.<init>((int) r2)     // Catch:{ Exception -> 0x2e4c }
            r2 = 5
            r1.writeInt32(r2)     // Catch:{ Exception -> 0x2e4c }
            r5 = 1
            r1.writeInt32(r5)     // Catch:{ Exception -> 0x2var_ }
            int r6 = r0.id     // Catch:{ Exception -> 0x2var_ }
            r1.writeInt32(r6)     // Catch:{ Exception -> 0x2var_ }
            int r6 = r0.accentColor     // Catch:{ Exception -> 0x2var_ }
            r1.writeInt32(r6)     // Catch:{ Exception -> 0x2var_ }
            int r6 = r0.myMessagesAccentColor     // Catch:{ Exception -> 0x2var_ }
            r1.writeInt32(r6)     // Catch:{ Exception -> 0x2var_ }
            int r6 = r0.myMessagesGradientAccentColor     // Catch:{ Exception -> 0x2var_ }
            r1.writeInt32(r6)     // Catch:{ Exception -> 0x2var_ }
            long r8 = r0.backgroundOverrideColor     // Catch:{ Exception -> 0x2var_ }
            r1.writeInt64(r8)     // Catch:{ Exception -> 0x2var_ }
            long r8 = r0.backgroundGradientOverrideColor     // Catch:{ Exception -> 0x2var_ }
            r1.writeInt64(r8)     // Catch:{ Exception -> 0x2var_ }
            int r6 = r0.backgroundRotation     // Catch:{ Exception -> 0x2var_ }
            r1.writeInt32(r6)     // Catch:{ Exception -> 0x2var_ }
            r8 = 0
            r1.writeInt64(r8)     // Catch:{ Exception -> 0x2var_ }
            float r6 = r0.patternIntensity     // Catch:{ Exception -> 0x2var_ }
            double r8 = (double) r6     // Catch:{ Exception -> 0x2var_ }
            r1.writeDouble(r8)     // Catch:{ Exception -> 0x2var_ }
            boolean r6 = r0.patternMotion     // Catch:{ Exception -> 0x2var_ }
            r1.writeBool(r6)     // Catch:{ Exception -> 0x2var_ }
            java.lang.String r0 = r0.patternSlug     // Catch:{ Exception -> 0x2var_ }
            r1.writeString(r0)     // Catch:{ Exception -> 0x2var_ }
            r6 = 0
            r1.writeBool(r6)     // Catch:{ Exception -> 0x2var_ }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x2var_ }
            r0.<init>()     // Catch:{ Exception -> 0x2var_ }
            java.lang.String r6 = "accents_"
            r0.append(r6)     // Catch:{ Exception -> 0x2var_ }
            java.lang.String r6 = r12.assetName     // Catch:{ Exception -> 0x2var_ }
            r0.append(r6)     // Catch:{ Exception -> 0x2var_ }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x2var_ }
            byte[] r1 = r1.toByteArray()     // Catch:{ Exception -> 0x2var_ }
            r6 = 3
            java.lang.String r1 = android.util.Base64.encodeToString(r1, r6)     // Catch:{ Exception -> 0x2var_ }
            r11.putString(r0, r1)     // Catch:{ Exception -> 0x2var_ }
            goto L_0x2dfc
        L_0x2df9:
            r2 = 5
            r5 = 1
            r6 = 3
        L_0x2dfc:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x2var_ }
            r0.<init>()     // Catch:{ Exception -> 0x2var_ }
            java.lang.String r1 = "accent_current_"
            r0.append(r1)     // Catch:{ Exception -> 0x2var_ }
            java.lang.String r1 = r12.assetName     // Catch:{ Exception -> 0x2var_ }
            r0.append(r1)     // Catch:{ Exception -> 0x2var_ }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x2var_ }
            int r1 = r12.currentAccentId     // Catch:{ Exception -> 0x2var_ }
            r11.putInt(r0, r1)     // Catch:{ Exception -> 0x2var_ }
        L_0x2e14:
            boolean r0 = r13.isEmpty()     // Catch:{ Exception -> 0x2var_ }
            if (r0 != 0) goto L_0x2e25
            org.telegram.ui.ActionBar.-$$Lambda$Theme$-1eio9W5h8f4eCuCQ0q4O3hfyjg r0 = org.telegram.ui.ActionBar.$$Lambda$Theme$1eio9W5h8f4eCuCQ0q4O3hfyjg.INSTANCE     // Catch:{ Exception -> 0x2var_ }
            java.util.Collections.sort(r13, r0)     // Catch:{ Exception -> 0x2var_ }
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeAccent> r0 = r12.themeAccents     // Catch:{ Exception -> 0x2var_ }
            r1 = 0
            r0.addAll(r1, r13)     // Catch:{ Exception -> 0x2var_ }
        L_0x2e25:
            android.util.SparseArray<org.telegram.ui.ActionBar.Theme$ThemeAccent> r0 = r12.themeAccentsMap     // Catch:{ Exception -> 0x2var_ }
            if (r0 == 0) goto L_0x2e3d
            android.util.SparseArray<org.telegram.ui.ActionBar.Theme$ThemeAccent> r0 = r12.themeAccentsMap     // Catch:{ Exception -> 0x2var_ }
            int r1 = r12.currentAccentId     // Catch:{ Exception -> 0x2var_ }
            java.lang.Object r0 = r0.get(r1)     // Catch:{ Exception -> 0x2var_ }
            if (r0 != 0) goto L_0x2e3d
            boolean r0 = r12.firstAccentIsDefault     // Catch:{ Exception -> 0x2var_ }
            if (r0 == 0) goto L_0x2e3a
            int r0 = DEFALT_THEME_ACCENT_ID     // Catch:{ Exception -> 0x2var_ }
            goto L_0x2e3b
        L_0x2e3a:
            r0 = 0
        L_0x2e3b:
            r12.currentAccentId = r0     // Catch:{ Exception -> 0x2var_ }
        L_0x2e3d:
            r12.loadWallpapers(r4)     // Catch:{ Exception -> 0x2var_ }
            r1 = 0
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r12.getAccent(r1)     // Catch:{ Exception -> 0x2var_ }
            if (r0 == 0) goto L_0x2e56
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r0 = r0.overrideWallpaper     // Catch:{ Exception -> 0x2var_ }
            r12.overrideWallpaper = r0     // Catch:{ Exception -> 0x2var_ }
            goto L_0x2e56
        L_0x2e4c:
            r0 = move-exception
            r5 = 1
            goto L_0x2var_
        L_0x2e50:
            r22 = r8
            r24 = r9
            r2 = 5
            r6 = 3
        L_0x2e56:
            r8 = r22
            r9 = r24
            r1 = 0
            r2 = 0
            r6 = 2
            goto L_0x2be1
        L_0x2e5f:
            r24 = r9
            r6 = 3
            if (r10 == 0) goto L_0x2e6a
            r10.commit()     // Catch:{ Exception -> 0x2var_ }
            r11.commit()     // Catch:{ Exception -> 0x2var_ }
        L_0x2e6a:
            java.lang.String r0 = "selectedAutoNightType"
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x2var_ }
            r2 = 29
            if (r1 < r2) goto L_0x2e74
            r4 = 3
            goto L_0x2e75
        L_0x2e74:
            r4 = 0
        L_0x2e75:
            int r0 = r7.getInt(r0, r4)     // Catch:{ Exception -> 0x2var_ }
            selectedAutoNightType = r0     // Catch:{ Exception -> 0x2var_ }
            java.lang.String r0 = "autoNightScheduleByLocation"
            r1 = 0
            boolean r0 = r7.getBoolean(r0, r1)     // Catch:{ Exception -> 0x2var_ }
            autoNightScheduleByLocation = r0     // Catch:{ Exception -> 0x2var_ }
            java.lang.String r0 = "autoNightBrighnessThreshold"
            r1 = 1048576000(0x3e800000, float:0.25)
            float r0 = r7.getFloat(r0, r1)     // Catch:{ Exception -> 0x2var_ }
            autoNightBrighnessThreshold = r0     // Catch:{ Exception -> 0x2var_ }
            java.lang.String r0 = "autoNightDayStartTime"
            r1 = 1320(0x528, float:1.85E-42)
            int r0 = r7.getInt(r0, r1)     // Catch:{ Exception -> 0x2var_ }
            autoNightDayStartTime = r0     // Catch:{ Exception -> 0x2var_ }
            java.lang.String r0 = "autoNightDayEndTime"
            r1 = 480(0x1e0, float:6.73E-43)
            int r0 = r7.getInt(r0, r1)     // Catch:{ Exception -> 0x2var_ }
            autoNightDayEndTime = r0     // Catch:{ Exception -> 0x2var_ }
            java.lang.String r0 = "autoNightSunsetTime"
            r1 = 1320(0x528, float:1.85E-42)
            int r0 = r7.getInt(r0, r1)     // Catch:{ Exception -> 0x2var_ }
            autoNightSunsetTime = r0     // Catch:{ Exception -> 0x2var_ }
            java.lang.String r0 = "autoNightSunriseTime"
            r1 = 480(0x1e0, float:6.73E-43)
            int r0 = r7.getInt(r0, r1)     // Catch:{ Exception -> 0x2var_ }
            autoNightSunriseTime = r0     // Catch:{ Exception -> 0x2var_ }
            java.lang.String r0 = "autoNightCityName"
            java.lang.String r0 = r7.getString(r0, r3)     // Catch:{ Exception -> 0x2var_ }
            autoNightCityName = r0     // Catch:{ Exception -> 0x2var_ }
            java.lang.String r0 = "autoNightLocationLatitude3"
            r1 = 10000(0x2710, double:4.9407E-320)
            long r0 = r7.getLong(r0, r1)     // Catch:{ Exception -> 0x2var_ }
            r8 = 10000(0x2710, double:4.9407E-320)
            int r2 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
            if (r2 == 0) goto L_0x2ed3
            double r0 = java.lang.Double.longBitsToDouble(r0)     // Catch:{ Exception -> 0x2var_ }
            autoNightLocationLatitude = r0     // Catch:{ Exception -> 0x2var_ }
            goto L_0x2eda
        L_0x2ed3:
            r0 = 4666723172467343360(0x40cNUM, double:10000.0)
            autoNightLocationLatitude = r0     // Catch:{ Exception -> 0x2var_ }
        L_0x2eda:
            java.lang.String r0 = "autoNightLocationLongitude3"
            r1 = 10000(0x2710, double:4.9407E-320)
            long r0 = r7.getLong(r0, r1)     // Catch:{ Exception -> 0x2var_ }
            r8 = 10000(0x2710, double:4.9407E-320)
            int r2 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
            if (r2 == 0) goto L_0x2eef
            double r0 = java.lang.Double.longBitsToDouble(r0)     // Catch:{ Exception -> 0x2var_ }
            autoNightLocationLongitude = r0     // Catch:{ Exception -> 0x2var_ }
            goto L_0x2ef6
        L_0x2eef:
            r0 = 4666723172467343360(0x40cNUM, double:10000.0)
            autoNightLocationLongitude = r0     // Catch:{ Exception -> 0x2var_ }
        L_0x2ef6:
            java.lang.String r0 = "autoNightLastSunCheckDay"
            r1 = -1
            int r0 = r7.getInt(r0, r1)     // Catch:{ Exception -> 0x2var_ }
            autoNightLastSunCheckDay = r0     // Catch:{ Exception -> 0x2var_ }
            r9 = r24
            goto L_0x2var_
        L_0x2var_:
            r0 = move-exception
            goto L_0x2var_
        L_0x2var_:
            r0 = move-exception
            r24 = r9
        L_0x2var_:
            r2 = r24
            goto L_0x2f0f
        L_0x2f0a:
            r0 = move-exception
            r2 = r9
            goto L_0x2f0f
        L_0x2f0d:
            r0 = move-exception
            r2 = 0
        L_0x2f0f:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r9 = r2
        L_0x2var_:
            if (r9 != 0) goto L_0x2var_
            org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = defaultTheme
            goto L_0x2f1a
        L_0x2var_:
            currentDayTheme = r9
        L_0x2f1a:
            java.lang.String r0 = "overrideThemeWallpaper"
            boolean r0 = r7.contains(r0)
            if (r0 != 0) goto L_0x2f2a
            java.lang.String r0 = "selectedBackground2"
            boolean r0 = r7.contains(r0)
            if (r0 == 0) goto L_0x2fd6
        L_0x2f2a:
            java.lang.String r0 = "overrideThemeWallpaper"
            r1 = 0
            boolean r0 = r7.getBoolean(r0, r1)
            r1 = 1000001(0xvar_, double:4.94066E-318)
            java.lang.String r4 = "selectedBackground2"
            long r1 = r7.getLong(r4, r1)
            r10 = -1
            int r4 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r4 == 0) goto L_0x2f4f
            if (r0 == 0) goto L_0x2fc3
            r10 = -2
            int r0 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x2fc3
            r10 = 1000001(0xvar_, double:4.94066E-318)
            int r0 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x2fc3
        L_0x2f4f:
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r0 = new org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo
            r0.<init>()
            java.lang.String r4 = "selectedColor"
            r6 = 0
            int r4 = r7.getInt(r4, r6)
            r0.color = r4
            java.lang.String r4 = "selectedBackgroundSlug"
            java.lang.String r4 = r7.getString(r4, r3)
            r0.slug = r4
            r10 = -100
            int r4 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r4 < 0) goto L_0x2f7e
            r10 = -1
            int r4 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r4 > 0) goto L_0x2f7e
            int r1 = r0.color
            if (r1 == 0) goto L_0x2f7e
            java.lang.String r1 = "c"
            r0.slug = r1
            r0.fileName = r3
            r0.originalFileName = r3
            goto L_0x2var_
        L_0x2f7e:
            java.lang.String r1 = "wallpaper.jpg"
            r0.fileName = r1
            java.lang.String r1 = "wallpaper_original.jpg"
            r0.originalFileName = r1
        L_0x2var_:
            java.lang.String r1 = "selectedGradientColor"
            r2 = 0
            int r1 = r7.getInt(r1, r2)
            r0.gradientColor = r1
            r1 = 45
            java.lang.String r3 = "selectedGradientRotation"
            int r1 = r7.getInt(r3, r1)
            r0.rotation = r1
            java.lang.String r1 = "selectedBackgroundBlurred"
            boolean r1 = r7.getBoolean(r1, r2)
            r0.isBlurred = r1
            java.lang.String r1 = "selectedBackgroundMotion"
            boolean r1 = r7.getBoolean(r1, r2)
            r0.isMotion = r1
            r1 = 1056964608(0x3var_, float:0.5)
            java.lang.String r2 = "selectedIntensity"
            float r1 = r7.getFloat(r2, r1)
            r0.intensity = r1
            org.telegram.ui.ActionBar.Theme$ThemeInfo r1 = currentDayTheme
            r1.setOverrideWallpaper(r0)
            int r1 = selectedAutoNightType
            if (r1 == 0) goto L_0x2fc3
            org.telegram.ui.ActionBar.Theme$ThemeInfo r1 = currentNightTheme
            r1.setOverrideWallpaper(r0)
        L_0x2fc3:
            android.content.SharedPreferences$Editor r0 = r7.edit()
            java.lang.String r1 = "overrideThemeWallpaper"
            android.content.SharedPreferences$Editor r0 = r0.remove(r1)
            java.lang.String r1 = "selectedBackground2"
            android.content.SharedPreferences$Editor r0 = r0.remove(r1)
            r0.commit()
        L_0x2fd6:
            int r0 = needSwitchToTheme()
            r1 = 2
            if (r0 != r1) goto L_0x2fdf
            org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = currentNightTheme
        L_0x2fdf:
            if (r0 != r1) goto L_0x2fe3
            r1 = 0
            goto L_0x2fe5
        L_0x2fe3:
            r1 = 0
            r5 = 0
        L_0x2fe5:
            applyTheme(r9, r1, r1, r5)
            org.telegram.ui.ActionBar.-$$Lambda$RQB0Jwr1FTqp6hrbGUHuOs-9k1I r0 = org.telegram.ui.ActionBar.$$Lambda$RQB0Jwr1FTqp6hrbGUHuOs9k1I.INSTANCE
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            org.telegram.ui.ActionBar.Theme$7 r0 = new org.telegram.ui.ActionBar.Theme$7
            r0.<init>()
            ambientSensorListener = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.<clinit>():void");
    }

    static /* synthetic */ int lambda$static$0(ThemeAccent themeAccent, ThemeAccent themeAccent2) {
        int i = themeAccent.id;
        int i2 = themeAccent2.id;
        if (i > i2) {
            return -1;
        }
        return i < i2 ? 1 : 0;
    }

    public static void saveAutoNightThemeConfig() {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("selectedAutoNightType", selectedAutoNightType);
        edit.putBoolean("autoNightScheduleByLocation", autoNightScheduleByLocation);
        edit.putFloat("autoNightBrighnessThreshold", autoNightBrighnessThreshold);
        edit.putInt("autoNightDayStartTime", autoNightDayStartTime);
        edit.putInt("autoNightDayEndTime", autoNightDayEndTime);
        edit.putInt("autoNightSunriseTime", autoNightSunriseTime);
        edit.putString("autoNightCityName", autoNightCityName);
        edit.putInt("autoNightSunsetTime", autoNightSunsetTime);
        edit.putLong("autoNightLocationLatitude3", Double.doubleToRawLongBits(autoNightLocationLatitude));
        edit.putLong("autoNightLocationLongitude3", Double.doubleToRawLongBits(autoNightLocationLongitude));
        edit.putInt("autoNightLastSunCheckDay", autoNightLastSunCheckDay);
        ThemeInfo themeInfo = currentNightTheme;
        if (themeInfo != null) {
            edit.putString("nighttheme", themeInfo.getKey());
        } else {
            edit.remove("nighttheme");
        }
        edit.commit();
    }

    /* access modifiers changed from: private */
    @SuppressLint({"PrivateApi"})
    public static Drawable getStateDrawable(Drawable drawable, int i) {
        if (Build.VERSION.SDK_INT >= 29 && (drawable instanceof StateListDrawable)) {
            return ((StateListDrawable) drawable).getStateDrawable(i);
        }
        if (StateListDrawable_getStateDrawableMethod == null) {
            Class<StateListDrawable> cls = StateListDrawable.class;
            try {
                StateListDrawable_getStateDrawableMethod = cls.getDeclaredMethod("getStateDrawable", new Class[]{Integer.TYPE});
            } catch (Throwable unused) {
            }
        }
        Method method = StateListDrawable_getStateDrawableMethod;
        if (method == null) {
            return null;
        }
        try {
            return (Drawable) method.invoke(drawable, new Object[]{Integer.valueOf(i)});
        } catch (Exception unused2) {
            return null;
        }
    }

    public static Drawable createEmojiIconSelectorDrawable(Context context, int i, int i2, int i3) {
        Resources resources = context.getResources();
        Drawable mutate = resources.getDrawable(i).mutate();
        if (i2 != 0) {
            mutate.setColorFilter(new PorterDuffColorFilter(i2, PorterDuff.Mode.MULTIPLY));
        }
        Drawable mutate2 = resources.getDrawable(i).mutate();
        if (i3 != 0) {
            mutate2.setColorFilter(new PorterDuffColorFilter(i3, PorterDuff.Mode.MULTIPLY));
        }
        AnonymousClass3 r4 = new StateListDrawable() {
            public boolean selectDrawable(int i) {
                if (Build.VERSION.SDK_INT >= 21) {
                    return super.selectDrawable(i);
                }
                Drawable access$2600 = Theme.getStateDrawable(this, i);
                ColorFilter colorFilter = null;
                if (access$2600 instanceof BitmapDrawable) {
                    colorFilter = ((BitmapDrawable) access$2600).getPaint().getColorFilter();
                } else if (access$2600 instanceof NinePatchDrawable) {
                    colorFilter = ((NinePatchDrawable) access$2600).getPaint().getColorFilter();
                }
                boolean selectDrawable = super.selectDrawable(i);
                if (colorFilter != null) {
                    access$2600.setColorFilter(colorFilter);
                }
                return selectDrawable;
            }
        };
        r4.setEnterFadeDuration(1);
        r4.setExitFadeDuration(200);
        r4.addState(new int[]{16842913}, mutate2);
        r4.addState(new int[0], mutate);
        return r4;
    }

    public static Drawable createEditTextDrawable(Context context, boolean z) {
        Resources resources = context.getResources();
        Drawable mutate = resources.getDrawable(NUM).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(getColor(z ? "dialogInputField" : "windowBackgroundWhiteInputField"), PorterDuff.Mode.MULTIPLY));
        Drawable mutate2 = resources.getDrawable(NUM).mutate();
        mutate2.setColorFilter(new PorterDuffColorFilter(getColor(z ? "dialogInputFieldActivated" : "windowBackgroundWhiteInputFieldActivated"), PorterDuff.Mode.MULTIPLY));
        AnonymousClass4 r5 = new StateListDrawable() {
            public boolean selectDrawable(int i) {
                if (Build.VERSION.SDK_INT >= 21) {
                    return super.selectDrawable(i);
                }
                Drawable access$2600 = Theme.getStateDrawable(this, i);
                ColorFilter colorFilter = null;
                if (access$2600 instanceof BitmapDrawable) {
                    colorFilter = ((BitmapDrawable) access$2600).getPaint().getColorFilter();
                } else if (access$2600 instanceof NinePatchDrawable) {
                    colorFilter = ((NinePatchDrawable) access$2600).getPaint().getColorFilter();
                }
                boolean selectDrawable = super.selectDrawable(i);
                if (colorFilter != null) {
                    access$2600.setColorFilter(colorFilter);
                }
                return selectDrawable;
            }
        };
        r5.addState(new int[]{16842910, 16842908}, mutate2);
        r5.addState(new int[]{16842908}, mutate2);
        r5.addState(StateSet.WILD_CARD, mutate);
        return r5;
    }

    public static boolean canStartHolidayAnimation() {
        return canStartHolidayAnimation;
    }

    public static int getEventType() {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(System.currentTimeMillis());
        int i = instance.get(2);
        int i2 = instance.get(5);
        instance.get(12);
        instance.get(11);
        if ((i != 11 || i2 < 24 || i2 > 31) && (i != 0 || i2 != 1)) {
            return (i == 1 && i2 == 14) ? 1 : -1;
        }
        return 0;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0059, code lost:
        if (r2 <= 31) goto L_0x005f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x005d, code lost:
        if (r2 == 1) goto L_0x005f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x005f, code lost:
        dialogs_holidayDrawable = org.telegram.messenger.ApplicationLoader.applicationContext.getResources().getDrawable(NUM);
        dialogs_holidayDrawableOffsetX = -org.telegram.messenger.AndroidUtilities.dp(3.0f);
        dialogs_holidayDrawableOffsetY = -org.telegram.messenger.AndroidUtilities.dp(1.0f);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.drawable.Drawable getCurrentHolidayDrawable() {
        /*
            long r0 = java.lang.System.currentTimeMillis()
            long r2 = lastHolidayCheckTime
            long r0 = r0 - r2
            r2 = 60000(0xea60, double:2.9644E-319)
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 < 0) goto L_0x0080
            long r0 = java.lang.System.currentTimeMillis()
            lastHolidayCheckTime = r0
            java.util.Calendar r0 = java.util.Calendar.getInstance()
            long r1 = java.lang.System.currentTimeMillis()
            r0.setTimeInMillis(r1)
            r1 = 2
            int r1 = r0.get(r1)
            r2 = 5
            int r2 = r0.get(r2)
            r3 = 12
            int r3 = r0.get(r3)
            r4 = 11
            int r0 = r0.get(r4)
            r5 = 1
            if (r1 != 0) goto L_0x0043
            if (r2 != r5) goto L_0x0043
            r6 = 10
            if (r3 > r6) goto L_0x0043
            if (r0 != 0) goto L_0x0043
            canStartHolidayAnimation = r5
            goto L_0x0046
        L_0x0043:
            r0 = 0
            canStartHolidayAnimation = r0
        L_0x0046:
            android.graphics.drawable.Drawable r0 = dialogs_holidayDrawable
            if (r0 != 0) goto L_0x0080
            if (r1 != r4) goto L_0x005b
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            r3 = 31
            if (r0 == 0) goto L_0x0055
            r0 = 29
            goto L_0x0057
        L_0x0055:
            r0 = 31
        L_0x0057:
            if (r2 < r0) goto L_0x005b
            if (r2 <= r3) goto L_0x005f
        L_0x005b:
            if (r1 != 0) goto L_0x0080
            if (r2 != r5) goto L_0x0080
        L_0x005f:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2131165739(0x7var_b, float:1.7945704E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r1)
            dialogs_holidayDrawable = r0
            r0 = 1077936128(0x40400000, float:3.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = -r0
            dialogs_holidayDrawableOffsetX = r0
            r0 = 1065353216(0x3var_, float:1.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = -r0
            dialogs_holidayDrawableOffsetY = r0
        L_0x0080:
            android.graphics.drawable.Drawable r0 = dialogs_holidayDrawable
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.getCurrentHolidayDrawable():android.graphics.drawable.Drawable");
    }

    public static int getCurrentHolidayDrawableXOffset() {
        return dialogs_holidayDrawableOffsetX;
    }

    public static int getCurrentHolidayDrawableYOffset() {
        return dialogs_holidayDrawableOffsetY;
    }

    public static Drawable createSimpleSelectorDrawable(Context context, int i, int i2, int i3) {
        Resources resources = context.getResources();
        Drawable mutate = resources.getDrawable(i).mutate();
        if (i2 != 0) {
            mutate.setColorFilter(new PorterDuffColorFilter(i2, PorterDuff.Mode.MULTIPLY));
        }
        Drawable mutate2 = resources.getDrawable(i).mutate();
        if (i3 != 0) {
            mutate2.setColorFilter(new PorterDuffColorFilter(i3, PorterDuff.Mode.MULTIPLY));
        }
        AnonymousClass5 r4 = new StateListDrawable() {
            public boolean selectDrawable(int i) {
                if (Build.VERSION.SDK_INT >= 21) {
                    return super.selectDrawable(i);
                }
                Drawable access$2600 = Theme.getStateDrawable(this, i);
                ColorFilter colorFilter = null;
                if (access$2600 instanceof BitmapDrawable) {
                    colorFilter = ((BitmapDrawable) access$2600).getPaint().getColorFilter();
                } else if (access$2600 instanceof NinePatchDrawable) {
                    colorFilter = ((NinePatchDrawable) access$2600).getPaint().getColorFilter();
                }
                boolean selectDrawable = super.selectDrawable(i);
                if (colorFilter != null) {
                    access$2600.setColorFilter(colorFilter);
                }
                return selectDrawable;
            }
        };
        r4.addState(new int[]{16842919}, mutate2);
        r4.addState(new int[]{16842913}, mutate2);
        r4.addState(StateSet.WILD_CARD, mutate);
        return r4;
    }

    public static ShapeDrawable createCircleDrawable(int i, int i2) {
        OvalShape ovalShape = new OvalShape();
        float f = (float) i;
        ovalShape.resize(f, f);
        ShapeDrawable shapeDrawable = new ShapeDrawable(ovalShape);
        shapeDrawable.getPaint().setColor(i2);
        return shapeDrawable;
    }

    public static CombinedDrawable createCircleDrawableWithIcon(int i, int i2) {
        return createCircleDrawableWithIcon(i, i2, 0);
    }

    public static CombinedDrawable createCircleDrawableWithIcon(int i, int i2, int i3) {
        return createCircleDrawableWithIcon(i, i2 != 0 ? ApplicationLoader.applicationContext.getResources().getDrawable(i2).mutate() : null, i3);
    }

    public static CombinedDrawable createCircleDrawableWithIcon(int i, Drawable drawable, int i2) {
        OvalShape ovalShape = new OvalShape();
        float f = (float) i;
        ovalShape.resize(f, f);
        ShapeDrawable shapeDrawable = new ShapeDrawable(ovalShape);
        Paint paint = shapeDrawable.getPaint();
        paint.setColor(-1);
        if (i2 == 1) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        } else if (i2 == 2) {
            paint.setAlpha(0);
        }
        CombinedDrawable combinedDrawable = new CombinedDrawable(shapeDrawable, drawable);
        combinedDrawable.setCustomSize(i, i);
        return combinedDrawable;
    }

    public static void setCombinedDrawableColor(Drawable drawable, int i, boolean z) {
        Drawable drawable2;
        if (drawable instanceof CombinedDrawable) {
            if (z) {
                drawable2 = ((CombinedDrawable) drawable).getIcon();
            } else {
                drawable2 = ((CombinedDrawable) drawable).getBackground();
            }
            if (drawable2 instanceof ColorDrawable) {
                ((ColorDrawable) drawable2).setColor(i);
            } else {
                drawable2.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
            }
        }
    }

    public static Drawable createSimpleSelectorCircleDrawable(int i, int i2, int i3) {
        OvalShape ovalShape = new OvalShape();
        float f = (float) i;
        ovalShape.resize(f, f);
        ShapeDrawable shapeDrawable = new ShapeDrawable(ovalShape);
        shapeDrawable.getPaint().setColor(i2);
        ShapeDrawable shapeDrawable2 = new ShapeDrawable(ovalShape);
        if (Build.VERSION.SDK_INT >= 21) {
            shapeDrawable2.getPaint().setColor(-1);
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{i3}), shapeDrawable, shapeDrawable2);
        }
        shapeDrawable2.getPaint().setColor(i3);
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, shapeDrawable2);
        stateListDrawable.addState(new int[]{16842908}, shapeDrawable2);
        stateListDrawable.addState(StateSet.WILD_CARD, shapeDrawable);
        return stateListDrawable;
    }

    public static Drawable createRoundRectDrawable(int i, int i2) {
        float f = (float) i;
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(new float[]{f, f, f, f, f, f, f, f}, (RectF) null, (float[]) null));
        shapeDrawable.getPaint().setColor(i2);
        return shapeDrawable;
    }

    public static Drawable createSimpleSelectorRoundRectDrawable(int i, int i2, int i3) {
        return createSimpleSelectorRoundRectDrawable(i, i2, i3, i3);
    }

    public static Drawable createSimpleSelectorRoundRectDrawable(int i, int i2, int i3, int i4) {
        float f = (float) i;
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(new float[]{f, f, f, f, f, f, f, f}, (RectF) null, (float[]) null));
        int i5 = i2;
        shapeDrawable.getPaint().setColor(i2);
        ShapeDrawable shapeDrawable2 = new ShapeDrawable(new RoundRectShape(new float[]{f, f, f, f, f, f, f, f}, (RectF) null, (float[]) null));
        shapeDrawable2.getPaint().setColor(i4);
        if (Build.VERSION.SDK_INT >= 21) {
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{i3}), shapeDrawable, shapeDrawable2);
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, shapeDrawable2);
        stateListDrawable.addState(new int[]{16842913}, shapeDrawable2);
        stateListDrawable.addState(StateSet.WILD_CARD, shapeDrawable);
        return stateListDrawable;
    }

    public static Drawable createSelectorDrawableFromDrawables(Drawable drawable, Drawable drawable2) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, drawable2);
        stateListDrawable.addState(new int[]{16842913}, drawable2);
        stateListDrawable.addState(StateSet.WILD_CARD, drawable);
        return stateListDrawable;
    }

    public static Drawable getRoundRectSelectorDrawable(int i) {
        if (Build.VERSION.SDK_INT >= 21) {
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{(i & 16777215) | NUM}), (Drawable) null, createRoundRectDrawable(AndroidUtilities.dp(3.0f), -1));
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        int i2 = (i & 16777215) | NUM;
        stateListDrawable.addState(new int[]{16842919}, createRoundRectDrawable(AndroidUtilities.dp(3.0f), i2));
        stateListDrawable.addState(new int[]{16842913}, createRoundRectDrawable(AndroidUtilities.dp(3.0f), i2));
        stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(0));
        return stateListDrawable;
    }

    public static Drawable createSelectorWithBackgroundDrawable(int i, int i2) {
        if (Build.VERSION.SDK_INT >= 21) {
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{i2}), new ColorDrawable(i), new ColorDrawable(i));
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, new ColorDrawable(i2));
        stateListDrawable.addState(new int[]{16842913}, new ColorDrawable(i2));
        stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(i));
        return stateListDrawable;
    }

    public static Drawable getSelectorDrawable(boolean z) {
        return getSelectorDrawable(getColor("listSelectorSDK21"), z);
    }

    public static Drawable getSelectorDrawable(int i, boolean z) {
        if (!z) {
            return createSelectorDrawable(i, 2);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{i}), new ColorDrawable(getColor("windowBackgroundWhite")), new ColorDrawable(-1));
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, new ColorDrawable(i));
        stateListDrawable.addState(new int[]{16842913}, new ColorDrawable(i));
        stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(getColor("windowBackgroundWhite")));
        return stateListDrawable;
    }

    public static Drawable createSelectorDrawable(int i) {
        return createSelectorDrawable(i, 1, -1);
    }

    public static Drawable createSelectorDrawable(int i, int i2) {
        return createSelectorDrawable(i, i2, -1);
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x0053  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.drawable.Drawable createSelectorDrawable(int r10, final int r11, int r12) {
        /*
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 0
            r2 = 1
            r3 = 21
            if (r0 < r3) goto L_0x0067
            r0 = 23
            r3 = -1
            r4 = 5
            r5 = 0
            if (r11 == r2) goto L_0x0011
            if (r11 != r4) goto L_0x0017
        L_0x0011:
            int r6 = android.os.Build.VERSION.SDK_INT
            if (r6 < r0) goto L_0x0017
        L_0x0015:
            r6 = r5
            goto L_0x003b
        L_0x0017:
            if (r11 == r2) goto L_0x0031
            r6 = 3
            if (r11 == r6) goto L_0x0031
            r6 = 4
            if (r11 == r6) goto L_0x0031
            if (r11 == r4) goto L_0x0031
            r6 = 6
            if (r11 == r6) goto L_0x0031
            r6 = 7
            if (r11 != r6) goto L_0x0028
            goto L_0x0031
        L_0x0028:
            r6 = 2
            if (r11 != r6) goto L_0x0015
            android.graphics.drawable.ColorDrawable r6 = new android.graphics.drawable.ColorDrawable
            r6.<init>(r3)
            goto L_0x003b
        L_0x0031:
            android.graphics.Paint r6 = maskPaint
            r6.setColor(r3)
            org.telegram.ui.ActionBar.Theme$6 r6 = new org.telegram.ui.ActionBar.Theme$6
            r6.<init>(r11)
        L_0x003b:
            android.content.res.ColorStateList r7 = new android.content.res.ColorStateList
            int[][] r8 = new int[r2][]
            int[] r9 = android.util.StateSet.WILD_CARD
            r8[r1] = r9
            int[] r9 = new int[r2]
            r9[r1] = r10
            r7.<init>(r8, r9)
            android.graphics.drawable.RippleDrawable r10 = new android.graphics.drawable.RippleDrawable
            r10.<init>(r7, r5, r6)
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r0) goto L_0x0066
            if (r11 != r2) goto L_0x0061
            if (r12 > 0) goto L_0x005d
            r11 = 1101004800(0x41a00000, float:20.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r11)
        L_0x005d:
            r10.setRadius(r12)
            goto L_0x0066
        L_0x0061:
            if (r11 != r4) goto L_0x0066
            r10.setRadius(r3)
        L_0x0066:
            return r10
        L_0x0067:
            android.graphics.drawable.StateListDrawable r11 = new android.graphics.drawable.StateListDrawable
            r11.<init>()
            int[] r12 = new int[r2]
            r0 = 16842919(0x10100a7, float:2.3694026E-38)
            r12[r1] = r0
            android.graphics.drawable.ColorDrawable r0 = new android.graphics.drawable.ColorDrawable
            r0.<init>(r10)
            r11.addState(r12, r0)
            int[] r12 = new int[r2]
            r0 = 16842913(0x10100a1, float:2.369401E-38)
            r12[r1] = r0
            android.graphics.drawable.ColorDrawable r0 = new android.graphics.drawable.ColorDrawable
            r0.<init>(r10)
            r11.addState(r12, r0)
            int[] r10 = android.util.StateSet.WILD_CARD
            android.graphics.drawable.ColorDrawable r12 = new android.graphics.drawable.ColorDrawable
            r12.<init>(r1)
            r11.addState(r10, r12)
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.createSelectorDrawable(int, int, int):android.graphics.drawable.Drawable");
    }

    public static void applyPreviousTheme() {
        ThemeInfo themeInfo;
        if (previousTheme != null) {
            hasPreviousTheme = false;
            if (isInNigthMode && (themeInfo = currentNightTheme) != null) {
                applyTheme(themeInfo, true, false, true);
            } else if (!isApplyingAccent) {
                applyTheme(previousTheme, true, false, false);
            }
            isApplyingAccent = false;
            previousTheme = null;
            checkAutoNightThemeConditions();
        }
    }

    public static void clearPreviousTheme() {
        if (previousTheme != null) {
            hasPreviousTheme = false;
            isApplyingAccent = false;
            previousTheme = null;
        }
    }

    private static void sortThemes() {
        Collections.sort(themes, $$Lambda$Theme$9ifMr1ad9oyLiuwyK8tFm4yWNA.INSTANCE);
    }

    static /* synthetic */ int lambda$sortThemes$1(ThemeInfo themeInfo, ThemeInfo themeInfo2) {
        if (themeInfo.pathToFile == null && themeInfo.assetName == null) {
            return -1;
        }
        if (themeInfo2.pathToFile == null && themeInfo2.assetName == null) {
            return 1;
        }
        return themeInfo.name.compareTo(themeInfo2.name);
    }

    public static void applyThemeTemporary(ThemeInfo themeInfo, boolean z) {
        previousTheme = getCurrentTheme();
        hasPreviousTheme = true;
        isApplyingAccent = z;
        applyTheme(themeInfo, false, false, false);
    }

    public static boolean hasCustomWallpaper() {
        return isApplyingAccent && currentTheme.overrideWallpaper != null;
    }

    public static void resetCustomWallpaper(boolean z) {
        if (z) {
            isApplyingAccent = false;
            reloadWallpaper();
            return;
        }
        currentTheme.setOverrideWallpaper((OverrideWallpaperInfo) null);
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(11:23|(3:24|25|(2:27|(1:29)))|30|32|33|(1:35)|36|37|(1:39)|40|(1:42)) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:36:0x00de */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00e4 A[Catch:{ all -> 0x00f7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00f2 A[Catch:{ all -> 0x00f7 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.telegram.ui.ActionBar.Theme.ThemeInfo fillThemeValues(java.io.File r6, java.lang.String r7, org.telegram.tgnet.TLRPC$TL_theme r8) {
        /*
            r0 = 0
            org.telegram.ui.ActionBar.Theme$ThemeInfo r1 = new org.telegram.ui.ActionBar.Theme$ThemeInfo     // Catch:{ Exception -> 0x00ff }
            r1.<init>()     // Catch:{ Exception -> 0x00ff }
            r1.name = r7     // Catch:{ Exception -> 0x00ff }
            r1.info = r8     // Catch:{ Exception -> 0x00ff }
            java.lang.String r6 = r6.getAbsolutePath()     // Catch:{ Exception -> 0x00ff }
            r1.pathToFile = r6     // Catch:{ Exception -> 0x00ff }
            int r6 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ Exception -> 0x00ff }
            r1.account = r6     // Catch:{ Exception -> 0x00ff }
            r6 = 1
            java.lang.String[] r7 = new java.lang.String[r6]     // Catch:{ Exception -> 0x00ff }
            java.io.File r8 = new java.io.File     // Catch:{ Exception -> 0x00ff }
            java.lang.String r2 = r1.pathToFile     // Catch:{ Exception -> 0x00ff }
            r8.<init>(r2)     // Catch:{ Exception -> 0x00ff }
            getThemeFileValues(r8, r0, r7)     // Catch:{ Exception -> 0x00ff }
            r8 = 0
            r2 = r7[r8]     // Catch:{ Exception -> 0x00ff }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x00ff }
            if (r2 != 0) goto L_0x00fc
            r7 = r7[r8]     // Catch:{ Exception -> 0x00ff }
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x00ff }
            java.io.File r3 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ Exception -> 0x00ff }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00ff }
            r4.<init>()     // Catch:{ Exception -> 0x00ff }
            java.lang.String r5 = org.telegram.messenger.Utilities.MD5(r7)     // Catch:{ Exception -> 0x00ff }
            r4.append(r5)     // Catch:{ Exception -> 0x00ff }
            java.lang.String r5 = ".wp"
            r4.append(r5)     // Catch:{ Exception -> 0x00ff }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x00ff }
            r2.<init>(r3, r4)     // Catch:{ Exception -> 0x00ff }
            java.lang.String r2 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x00ff }
            r1.pathToWallpaper = r2     // Catch:{ Exception -> 0x00ff }
            android.net.Uri r7 = android.net.Uri.parse(r7)     // Catch:{ all -> 0x00f7 }
            java.lang.String r2 = "slug"
            java.lang.String r2 = r7.getQueryParameter(r2)     // Catch:{ all -> 0x00f7 }
            r1.slug = r2     // Catch:{ all -> 0x00f7 }
            java.lang.String r2 = "mode"
            java.lang.String r2 = r7.getQueryParameter(r2)     // Catch:{ all -> 0x00f7 }
            if (r2 == 0) goto L_0x0092
            java.lang.String r2 = r2.toLowerCase()     // Catch:{ all -> 0x00f7 }
            java.lang.String r3 = " "
            java.lang.String[] r2 = r2.split(r3)     // Catch:{ all -> 0x00f7 }
            if (r2 == 0) goto L_0x0092
            int r3 = r2.length     // Catch:{ all -> 0x00f7 }
            if (r3 <= 0) goto L_0x0092
        L_0x0073:
            int r3 = r2.length     // Catch:{ all -> 0x00f7 }
            if (r8 >= r3) goto L_0x0092
            java.lang.String r3 = "blur"
            r4 = r2[r8]     // Catch:{ all -> 0x00f7 }
            boolean r3 = r3.equals(r4)     // Catch:{ all -> 0x00f7 }
            if (r3 == 0) goto L_0x0083
            r1.isBlured = r6     // Catch:{ all -> 0x00f7 }
            goto L_0x008f
        L_0x0083:
            java.lang.String r3 = "motion"
            r4 = r2[r8]     // Catch:{ all -> 0x00f7 }
            boolean r3 = r3.equals(r4)     // Catch:{ all -> 0x00f7 }
            if (r3 == 0) goto L_0x008f
            r1.isMotion = r6     // Catch:{ all -> 0x00f7 }
        L_0x008f:
            int r8 = r8 + 1
            goto L_0x0073
        L_0x0092:
            java.lang.String r6 = "intensity"
            java.lang.String r6 = r7.getQueryParameter(r6)     // Catch:{ all -> 0x00f7 }
            boolean r8 = android.text.TextUtils.isEmpty(r6)     // Catch:{ all -> 0x00f7 }
            if (r8 != 0) goto L_0x00fe
            java.lang.String r8 = "bg_color"
            java.lang.String r8 = r7.getQueryParameter(r8)     // Catch:{ Exception -> 0x00c8 }
            boolean r2 = android.text.TextUtils.isEmpty(r8)     // Catch:{ Exception -> 0x00c8 }
            if (r2 != 0) goto L_0x00c8
            r2 = 16
            int r3 = java.lang.Integer.parseInt(r8, r2)     // Catch:{ Exception -> 0x00c8 }
            r4 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r3 = r3 | r4
            r1.patternBgColor = r3     // Catch:{ Exception -> 0x00c8 }
            int r3 = r8.length()     // Catch:{ Exception -> 0x00c8 }
            r5 = 6
            if (r3 <= r5) goto L_0x00c8
            r3 = 7
            java.lang.String r8 = r8.substring(r3)     // Catch:{ Exception -> 0x00c8 }
            int r8 = java.lang.Integer.parseInt(r8, r2)     // Catch:{ Exception -> 0x00c8 }
            r8 = r8 | r4
            r1.patternBgGradientColor = r8     // Catch:{ Exception -> 0x00c8 }
        L_0x00c8:
            java.lang.String r8 = "rotation"
            java.lang.String r7 = r7.getQueryParameter(r8)     // Catch:{ Exception -> 0x00de }
            boolean r8 = android.text.TextUtils.isEmpty(r7)     // Catch:{ Exception -> 0x00de }
            if (r8 != 0) goto L_0x00de
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ Exception -> 0x00de }
            int r7 = r7.intValue()     // Catch:{ Exception -> 0x00de }
            r1.patternBgGradientRotation = r7     // Catch:{ Exception -> 0x00de }
        L_0x00de:
            boolean r7 = android.text.TextUtils.isEmpty(r6)     // Catch:{ all -> 0x00f7 }
            if (r7 != 0) goto L_0x00ee
            java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r6)     // Catch:{ all -> 0x00f7 }
            int r6 = r6.intValue()     // Catch:{ all -> 0x00f7 }
            r1.patternIntensity = r6     // Catch:{ all -> 0x00f7 }
        L_0x00ee:
            int r6 = r1.patternIntensity     // Catch:{ all -> 0x00f7 }
            if (r6 != 0) goto L_0x00fe
            r6 = 50
            r1.patternIntensity = r6     // Catch:{ all -> 0x00f7 }
            goto L_0x00fe
        L_0x00f7:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)     // Catch:{ Exception -> 0x00ff }
            goto L_0x00fe
        L_0x00fc:
            themedWallpaperLink = r0     // Catch:{ Exception -> 0x00ff }
        L_0x00fe:
            return r1
        L_0x00ff:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.fillThemeValues(java.io.File, java.lang.String, org.telegram.tgnet.TLRPC$TL_theme):org.telegram.ui.ActionBar.Theme$ThemeInfo");
    }

    public static ThemeInfo applyThemeFile(File file, String str, TLRPC$TL_theme tLRPC$TL_theme, boolean z) {
        String str2;
        File file2;
        try {
            if (!str.toLowerCase().endsWith(".attheme")) {
                str = str + ".attheme";
            }
            if (z) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.goingToPreviewTheme, new Object[0]);
                ThemeInfo themeInfo = new ThemeInfo();
                themeInfo.name = str;
                themeInfo.info = tLRPC$TL_theme;
                themeInfo.pathToFile = file.getAbsolutePath();
                themeInfo.account = UserConfig.selectedAccount;
                applyThemeTemporary(themeInfo, false);
                return themeInfo;
            }
            if (tLRPC$TL_theme != null) {
                str2 = "remote" + tLRPC$TL_theme.id;
                file2 = new File(ApplicationLoader.getFilesDirFixed(), str2 + ".attheme");
            } else {
                file2 = new File(ApplicationLoader.getFilesDirFixed(), str);
                str2 = str;
            }
            if (!AndroidUtilities.copyFile(file, file2)) {
                applyPreviousTheme();
                return null;
            }
            previousTheme = null;
            hasPreviousTheme = false;
            isApplyingAccent = false;
            ThemeInfo themeInfo2 = themesDict.get(str2);
            if (themeInfo2 == null) {
                themeInfo2 = new ThemeInfo();
                themeInfo2.name = str;
                themeInfo2.account = UserConfig.selectedAccount;
                themes.add(themeInfo2);
                otherThemes.add(themeInfo2);
                sortThemes();
            } else {
                themesDict.remove(str2);
            }
            themeInfo2.info = tLRPC$TL_theme;
            themeInfo2.pathToFile = file2.getAbsolutePath();
            themesDict.put(themeInfo2.getKey(), themeInfo2);
            saveOtherThemes(true);
            applyTheme(themeInfo2, true, true, false);
            return themeInfo2;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    public static ThemeInfo getTheme(String str) {
        return themesDict.get(str);
    }

    public static void applyTheme(ThemeInfo themeInfo) {
        applyTheme(themeInfo, true, true, false);
    }

    public static void applyTheme(ThemeInfo themeInfo, boolean z) {
        applyTheme(themeInfo, true, true, z);
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(11:75|76|(1:78)|79|80|82|84|(0)|87|90|(0)(0)) */
    /* JADX WARNING: Can't wrap try/catch for region: R(24:31|32|33|(1:37)|38|39|40|41|(3:47|(4:50|(2:52|98)(2:53|(2:55|97)(1:99))|56|48)|96)|57|58|59|60|(2:62|(1:64))|65|67|68|(1:70)|82|84|(0)|87|90|(1:102)(1:101)) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:38:0x00c8 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:79:0x017a */
    /* JADX WARNING: Removed duplicated region for block: B:101:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00f3 A[Catch:{ all -> 0x0167 }] */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x012c A[Catch:{ Exception -> 0x014e }] */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x015a A[Catch:{ Exception -> 0x0165 }] */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x018c A[Catch:{ Exception -> 0x019c }] */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x01a4 A[ADDED_TO_REGION] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:38:0x00c8=Splitter:B:38:0x00c8, B:79:0x017a=Splitter:B:79:0x017a} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void applyTheme(org.telegram.ui.ActionBar.Theme.ThemeInfo r6, boolean r7, boolean r8, boolean r9) {
        /*
            if (r6 != 0) goto L_0x0003
            return
        L_0x0003:
            org.telegram.ui.Components.ThemeEditorView r8 = org.telegram.ui.Components.ThemeEditorView.getInstance()
            if (r8 == 0) goto L_0x000c
            r8.destroy()
        L_0x000c:
            r8 = 0
            java.lang.String r0 = r6.pathToFile     // Catch:{ Exception -> 0x019c }
            java.lang.String r1 = "theme"
            r2 = 0
            if (r0 != 0) goto L_0x003a
            java.lang.String r0 = r6.assetName     // Catch:{ Exception -> 0x019c }
            if (r0 == 0) goto L_0x0019
            goto L_0x003a
        L_0x0019:
            if (r9 != 0) goto L_0x002b
            if (r7 == 0) goto L_0x002b
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings()     // Catch:{ Exception -> 0x019c }
            android.content.SharedPreferences$Editor r0 = r0.edit()     // Catch:{ Exception -> 0x019c }
            r0.remove(r1)     // Catch:{ Exception -> 0x019c }
            r0.commit()     // Catch:{ Exception -> 0x019c }
        L_0x002b:
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = currentColorsNoAccent     // Catch:{ Exception -> 0x019c }
            r0.clear()     // Catch:{ Exception -> 0x019c }
            themedWallpaperFileOffset = r8     // Catch:{ Exception -> 0x019c }
            themedWallpaperLink = r2     // Catch:{ Exception -> 0x019c }
            wallpaper = r2     // Catch:{ Exception -> 0x019c }
            themedWallpaper = r2     // Catch:{ Exception -> 0x019c }
            goto L_0x017e
        L_0x003a:
            if (r9 != 0) goto L_0x0050
            if (r7 == 0) goto L_0x0050
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings()     // Catch:{ Exception -> 0x019c }
            android.content.SharedPreferences$Editor r0 = r0.edit()     // Catch:{ Exception -> 0x019c }
            java.lang.String r3 = r6.getKey()     // Catch:{ Exception -> 0x019c }
            r0.putString(r1, r3)     // Catch:{ Exception -> 0x019c }
            r0.commit()     // Catch:{ Exception -> 0x019c }
        L_0x0050:
            r0 = 1
            java.lang.String[] r1 = new java.lang.String[r0]     // Catch:{ Exception -> 0x019c }
            java.lang.String r3 = r6.assetName     // Catch:{ Exception -> 0x019c }
            if (r3 == 0) goto L_0x0060
            java.lang.String r3 = r6.assetName     // Catch:{ Exception -> 0x019c }
            java.util.HashMap r3 = getThemeFileValues(r2, r3, r2)     // Catch:{ Exception -> 0x019c }
            currentColorsNoAccent = r3     // Catch:{ Exception -> 0x019c }
            goto L_0x006d
        L_0x0060:
            java.io.File r3 = new java.io.File     // Catch:{ Exception -> 0x019c }
            java.lang.String r4 = r6.pathToFile     // Catch:{ Exception -> 0x019c }
            r3.<init>(r4)     // Catch:{ Exception -> 0x019c }
            java.util.HashMap r3 = getThemeFileValues(r3, r2, r1)     // Catch:{ Exception -> 0x019c }
            currentColorsNoAccent = r3     // Catch:{ Exception -> 0x019c }
        L_0x006d:
            java.util.HashMap<java.lang.String, java.lang.Integer> r3 = currentColorsNoAccent     // Catch:{ Exception -> 0x019c }
            java.lang.String r4 = "wallpaperFileOffset"
            java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x019c }
            java.lang.Integer r3 = (java.lang.Integer) r3     // Catch:{ Exception -> 0x019c }
            if (r3 == 0) goto L_0x007f
            int r3 = r3.intValue()     // Catch:{ Exception -> 0x019c }
            goto L_0x0080
        L_0x007f:
            r3 = -1
        L_0x0080:
            themedWallpaperFileOffset = r3     // Catch:{ Exception -> 0x019c }
            r3 = r1[r8]     // Catch:{ Exception -> 0x019c }
            boolean r3 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x019c }
            if (r3 != 0) goto L_0x016c
            r1 = r1[r8]     // Catch:{ Exception -> 0x019c }
            themedWallpaperLink = r1     // Catch:{ Exception -> 0x019c }
            java.io.File r1 = new java.io.File     // Catch:{ Exception -> 0x019c }
            java.io.File r2 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ Exception -> 0x019c }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x019c }
            r3.<init>()     // Catch:{ Exception -> 0x019c }
            java.lang.String r4 = themedWallpaperLink     // Catch:{ Exception -> 0x019c }
            java.lang.String r4 = org.telegram.messenger.Utilities.MD5(r4)     // Catch:{ Exception -> 0x019c }
            r3.append(r4)     // Catch:{ Exception -> 0x019c }
            java.lang.String r4 = ".wp"
            r3.append(r4)     // Catch:{ Exception -> 0x019c }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x019c }
            r1.<init>(r2, r3)     // Catch:{ Exception -> 0x019c }
            java.lang.String r1 = r1.getAbsolutePath()     // Catch:{ Exception -> 0x019c }
            java.lang.String r2 = r6.pathToWallpaper     // Catch:{ Exception -> 0x00c8 }
            if (r2 == 0) goto L_0x00c8
            java.lang.String r2 = r6.pathToWallpaper     // Catch:{ Exception -> 0x00c8 }
            boolean r2 = r2.equals(r1)     // Catch:{ Exception -> 0x00c8 }
            if (r2 != 0) goto L_0x00c8
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x00c8 }
            java.lang.String r3 = r6.pathToWallpaper     // Catch:{ Exception -> 0x00c8 }
            r2.<init>(r3)     // Catch:{ Exception -> 0x00c8 }
            r2.delete()     // Catch:{ Exception -> 0x00c8 }
        L_0x00c8:
            r6.pathToWallpaper = r1     // Catch:{ Exception -> 0x019c }
            java.lang.String r1 = themedWallpaperLink     // Catch:{ all -> 0x0167 }
            android.net.Uri r1 = android.net.Uri.parse(r1)     // Catch:{ all -> 0x0167 }
            java.lang.String r2 = "slug"
            java.lang.String r2 = r1.getQueryParameter(r2)     // Catch:{ all -> 0x0167 }
            r6.slug = r2     // Catch:{ all -> 0x0167 }
            java.lang.String r2 = "mode"
            java.lang.String r2 = r1.getQueryParameter(r2)     // Catch:{ all -> 0x0167 }
            if (r2 == 0) goto L_0x010f
            java.lang.String r2 = r2.toLowerCase()     // Catch:{ all -> 0x0167 }
            java.lang.String r3 = " "
            java.lang.String[] r2 = r2.split(r3)     // Catch:{ all -> 0x0167 }
            if (r2 == 0) goto L_0x010f
            int r3 = r2.length     // Catch:{ all -> 0x0167 }
            if (r3 <= 0) goto L_0x010f
            r3 = 0
        L_0x00f0:
            int r4 = r2.length     // Catch:{ all -> 0x0167 }
            if (r3 >= r4) goto L_0x010f
            java.lang.String r4 = "blur"
            r5 = r2[r3]     // Catch:{ all -> 0x0167 }
            boolean r4 = r4.equals(r5)     // Catch:{ all -> 0x0167 }
            if (r4 == 0) goto L_0x0100
            r6.isBlured = r0     // Catch:{ all -> 0x0167 }
            goto L_0x010c
        L_0x0100:
            java.lang.String r4 = "motion"
            r5 = r2[r3]     // Catch:{ all -> 0x0167 }
            boolean r4 = r4.equals(r5)     // Catch:{ all -> 0x0167 }
            if (r4 == 0) goto L_0x010c
            r6.isMotion = r0     // Catch:{ all -> 0x0167 }
        L_0x010c:
            int r3 = r3 + 1
            goto L_0x00f0
        L_0x010f:
            java.lang.String r0 = "intensity"
            java.lang.String r0 = r1.getQueryParameter(r0)     // Catch:{ all -> 0x0167 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ all -> 0x0167 }
            r0.intValue()     // Catch:{ all -> 0x0167 }
            r0 = 45
            r6.patternBgGradientRotation = r0     // Catch:{ all -> 0x0167 }
            java.lang.String r0 = "bg_color"
            java.lang.String r0 = r1.getQueryParameter(r0)     // Catch:{ Exception -> 0x014e }
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x014e }
            if (r2 != 0) goto L_0x014e
            r2 = 6
            java.lang.String r3 = r0.substring(r8, r2)     // Catch:{ Exception -> 0x014e }
            r4 = 16
            int r3 = java.lang.Integer.parseInt(r3, r4)     // Catch:{ Exception -> 0x014e }
            r5 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r3 = r3 | r5
            r6.patternBgColor = r3     // Catch:{ Exception -> 0x014e }
            int r3 = r0.length()     // Catch:{ Exception -> 0x014e }
            if (r3 <= r2) goto L_0x014e
            r2 = 7
            java.lang.String r0 = r0.substring(r2)     // Catch:{ Exception -> 0x014e }
            int r0 = java.lang.Integer.parseInt(r0, r4)     // Catch:{ Exception -> 0x014e }
            r0 = r0 | r5
            r6.patternBgGradientColor = r0     // Catch:{ Exception -> 0x014e }
        L_0x014e:
            java.lang.String r0 = "rotation"
            java.lang.String r0 = r1.getQueryParameter(r0)     // Catch:{ Exception -> 0x0165 }
            boolean r1 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0165 }
            if (r1 != 0) goto L_0x017e
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x0165 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x0165 }
            r6.patternBgGradientRotation = r0     // Catch:{ Exception -> 0x0165 }
            goto L_0x017e
        L_0x0165:
            goto L_0x017e
        L_0x0167:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x019c }
            goto L_0x017e
        L_0x016c:
            java.lang.String r0 = r6.pathToWallpaper     // Catch:{ Exception -> 0x017a }
            if (r0 == 0) goto L_0x017a
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x017a }
            java.lang.String r1 = r6.pathToWallpaper     // Catch:{ Exception -> 0x017a }
            r0.<init>(r1)     // Catch:{ Exception -> 0x017a }
            r0.delete()     // Catch:{ Exception -> 0x017a }
        L_0x017a:
            r6.pathToWallpaper = r2     // Catch:{ Exception -> 0x019c }
            themedWallpaperLink = r2     // Catch:{ Exception -> 0x019c }
        L_0x017e:
            if (r9 != 0) goto L_0x0196
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = previousTheme     // Catch:{ Exception -> 0x019c }
            if (r0 != 0) goto L_0x0196
            currentDayTheme = r6     // Catch:{ Exception -> 0x019c }
            boolean r0 = isCurrentThemeNight()     // Catch:{ Exception -> 0x019c }
            if (r0 == 0) goto L_0x0196
            r0 = 2000(0x7d0, float:2.803E-42)
            switchNightThemeDelay = r0     // Catch:{ Exception -> 0x019c }
            long r0 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x019c }
            lastDelayUpdateTime = r0     // Catch:{ Exception -> 0x019c }
        L_0x0196:
            currentTheme = r6     // Catch:{ Exception -> 0x019c }
            refreshThemeColors()     // Catch:{ Exception -> 0x019c }
            goto L_0x01a0
        L_0x019c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01a0:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = previousTheme
            if (r0 != 0) goto L_0x01b7
            if (r7 == 0) goto L_0x01b7
            boolean r7 = switchingNightTheme
            if (r7 != 0) goto L_0x01b7
            int r7 = r6.account
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r6.getAccent(r8)
            r7.saveTheme(r6, r0, r9, r8)
        L_0x01b7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.applyTheme(org.telegram.ui.ActionBar.Theme$ThemeInfo, boolean, boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    public static boolean useBlackText(int i, int i2) {
        float red = ((float) Color.red(i)) / 255.0f;
        float green = ((float) Color.green(i)) / 255.0f;
        float blue = ((float) Color.blue(i)) / 255.0f;
        return ((((red * 0.5f) + ((((float) Color.red(i2)) / 255.0f) * 0.5f)) * 0.2126f) + (((green * 0.5f) + ((((float) Color.green(i2)) / 255.0f) * 0.5f)) * 0.7152f)) + (((blue * 0.5f) + ((((float) Color.blue(i2)) / 255.0f) * 0.5f)) * 0.0722f) > 0.705f || ((red * 0.2126f) + (green * 0.7152f)) + (blue * 0.0722f) > 0.705f;
    }

    public static void refreshThemeColors() {
        currentColors.clear();
        currentColors.putAll(currentColorsNoAccent);
        shouldDrawGradientIcons = true;
        ThemeAccent accent = currentTheme.getAccent(false);
        if (accent != null) {
            shouldDrawGradientIcons = accent.fillAccentColors(currentColorsNoAccent, currentColors);
        }
        reloadWallpaper();
        applyCommonTheme();
        applyDialogsTheme();
        applyProfileTheme();
        applyChatTheme(false);
        AndroidUtilities.runOnUIThread($$Lambda$Theme$vGv1azM1iijU_YZpwqwy2UfjTKA.INSTANCE);
    }

    public static int changeColorAccent(ThemeInfo themeInfo, int i, int i2) {
        int i3;
        if (i == 0 || (i3 = themeInfo.accentBaseColor) == 0 || i == i3 || (themeInfo.firstAccentIsDefault && themeInfo.currentAccentId == DEFALT_THEME_ACCENT_ID)) {
            return i2;
        }
        float[] tempHsv = getTempHsv(3);
        float[] tempHsv2 = getTempHsv(4);
        Color.colorToHSV(themeInfo.accentBaseColor, tempHsv);
        Color.colorToHSV(i, tempHsv2);
        return changeColorAccent(tempHsv, tempHsv2, i2, themeInfo.isDark());
    }

    /* access modifiers changed from: private */
    public static float[] getTempHsv(int i) {
        ThreadLocal<float[]> threadLocal;
        if (i == 1) {
            threadLocal = hsvTemp1Local;
        } else if (i == 2) {
            threadLocal = hsvTemp2Local;
        } else if (i == 3) {
            threadLocal = hsvTemp3Local;
        } else if (i != 4) {
            threadLocal = hsvTemp5Local;
        } else {
            threadLocal = hsvTemp4Local;
        }
        float[] fArr = threadLocal.get();
        if (fArr != null) {
            return fArr;
        }
        float[] fArr2 = new float[3];
        threadLocal.set(fArr2);
        return fArr2;
    }

    /* access modifiers changed from: private */
    public static int getAccentColor(float[] fArr, int i, int i2) {
        float[] tempHsv = getTempHsv(3);
        float[] tempHsv2 = getTempHsv(4);
        Color.colorToHSV(i, tempHsv);
        Color.colorToHSV(i2, tempHsv2);
        float min = Math.min((tempHsv[1] * 1.5f) / fArr[1], 1.0f);
        tempHsv[0] = (tempHsv2[0] - tempHsv[0]) + fArr[0];
        tempHsv[1] = (tempHsv2[1] * fArr[1]) / tempHsv[1];
        tempHsv[2] = ((((tempHsv2[2] / tempHsv[2]) + min) - 1.0f) * fArr[2]) / min;
        if (tempHsv[2] < 0.3f) {
            return i2;
        }
        return Color.HSVToColor(255, tempHsv);
    }

    public static int changeColorAccent(int i) {
        int i2 = 0;
        ThemeAccent accent = currentTheme.getAccent(false);
        ThemeInfo themeInfo = currentTheme;
        if (accent != null) {
            i2 = accent.accentColor;
        }
        return changeColorAccent(themeInfo, i2, i);
    }

    public static int changeColorAccent(float[] fArr, float[] fArr2, int i, boolean z) {
        float[] tempHsv = getTempHsv(5);
        Color.colorToHSV(i, tempHsv);
        boolean z2 = false;
        if (Math.min(Math.abs(tempHsv[0] - fArr[0]), Math.abs((tempHsv[0] - fArr[0]) - 360.0f)) > 30.0f) {
            return i;
        }
        float min = Math.min((tempHsv[1] * 1.5f) / fArr[1], 1.0f);
        tempHsv[0] = (tempHsv[0] + fArr2[0]) - fArr[0];
        tempHsv[1] = (tempHsv[1] * fArr2[1]) / fArr[1];
        tempHsv[2] = tempHsv[2] * ((1.0f - min) + ((min * fArr2[2]) / fArr[2]));
        int HSVToColor = Color.HSVToColor(Color.alpha(i), tempHsv);
        float computePerceivedBrightness = AndroidUtilities.computePerceivedBrightness(i);
        float computePerceivedBrightness2 = AndroidUtilities.computePerceivedBrightness(HSVToColor);
        if (!z ? computePerceivedBrightness < computePerceivedBrightness2 : computePerceivedBrightness > computePerceivedBrightness2) {
            z2 = true;
        }
        return z2 ? changeBrightness(HSVToColor, ((0.39999998f * computePerceivedBrightness) / computePerceivedBrightness2) + 0.6f) : HSVToColor;
    }

    private static int changeBrightness(int i, float f) {
        int i2;
        int i3;
        int red = (int) (((float) Color.red(i)) * f);
        int green = (int) (((float) Color.green(i)) * f);
        int blue = (int) (((float) Color.blue(i)) * f);
        int i4 = 0;
        if (red < 0) {
            i2 = 0;
        } else {
            i2 = Math.min(red, 255);
        }
        if (green < 0) {
            i3 = 0;
        } else {
            i3 = Math.min(green, 255);
        }
        if (blue >= 0) {
            i4 = Math.min(blue, 255);
        }
        return Color.argb(Color.alpha(i), i2, i3, i4);
    }

    public static boolean deleteThemeAccent(ThemeInfo themeInfo, ThemeAccent themeAccent, boolean z) {
        boolean z2 = false;
        if (themeAccent == null || themeInfo == null || themeInfo.themeAccents == null) {
            return false;
        }
        boolean z3 = themeAccent.id == themeInfo.currentAccentId;
        File pathToWallpaper = themeAccent.getPathToWallpaper();
        if (pathToWallpaper != null) {
            pathToWallpaper.delete();
        }
        themeInfo.themeAccentsMap.remove(themeAccent.id);
        themeInfo.themeAccents.remove(themeAccent);
        TLRPC$TL_theme tLRPC$TL_theme = themeAccent.info;
        if (tLRPC$TL_theme != null) {
            themeInfo.accentsByThemeId.remove(tLRPC$TL_theme.id);
        }
        OverrideWallpaperInfo overrideWallpaperInfo = themeAccent.overrideWallpaper;
        if (overrideWallpaperInfo != null) {
            overrideWallpaperInfo.delete();
        }
        if (z3) {
            themeInfo.setCurrentAccentId(themeInfo.themeAccents.get(0).id);
        }
        if (z) {
            saveThemeAccents(themeInfo, true, false, false, false);
            if (themeAccent.info != null) {
                MessagesController instance = MessagesController.getInstance(themeAccent.account);
                if (z3 && themeInfo == currentNightTheme) {
                    z2 = true;
                }
                instance.saveTheme(themeInfo, themeAccent, z2, true);
            }
        }
        return z3;
    }

    public static void saveThemeAccents(ThemeInfo themeInfo, boolean z, boolean z2, boolean z3, boolean z4) {
        saveThemeAccents(themeInfo, z, z2, z3, z4, false);
    }

    public static void saveThemeAccents(ThemeInfo themeInfo, boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
        if (z) {
            SharedPreferences.Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
            if (!z3) {
                int size = themeInfo.themeAccents.size();
                int max = Math.max(0, size - themeInfo.defaultAccentCount);
                SerializedData serializedData = new SerializedData(((max * 15) + 2) * 4);
                serializedData.writeInt32(5);
                serializedData.writeInt32(max);
                for (int i = 0; i < size; i++) {
                    ThemeAccent themeAccent = themeInfo.themeAccents.get(i);
                    int i2 = themeAccent.id;
                    if (i2 >= 100) {
                        serializedData.writeInt32(i2);
                        serializedData.writeInt32(themeAccent.accentColor);
                        serializedData.writeInt32(themeAccent.myMessagesAccentColor);
                        serializedData.writeInt32(themeAccent.myMessagesGradientAccentColor);
                        serializedData.writeInt64(themeAccent.backgroundOverrideColor);
                        serializedData.writeInt64(themeAccent.backgroundGradientOverrideColor);
                        serializedData.writeInt32(themeAccent.backgroundRotation);
                        serializedData.writeInt64(0);
                        serializedData.writeDouble((double) themeAccent.patternIntensity);
                        serializedData.writeBool(themeAccent.patternMotion);
                        serializedData.writeString(themeAccent.patternSlug);
                        serializedData.writeBool(themeAccent.info != null);
                        if (themeAccent.info != null) {
                            serializedData.writeInt32(themeAccent.account);
                            themeAccent.info.serializeToStream(serializedData);
                        }
                    }
                }
                edit.putString("accents_" + themeInfo.assetName, Base64.encodeToString(serializedData.toByteArray(), 3));
                if (!z5) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.themeAccentListUpdated, new Object[0]);
                }
                if (z4) {
                    MessagesController.getInstance(UserConfig.selectedAccount).saveThemeToServer(themeInfo, themeInfo.getAccent(false));
                }
            }
            edit.putInt("accent_current_" + themeInfo.assetName, themeInfo.currentAccentId);
            edit.commit();
        } else {
            if (themeInfo.prevAccentId != -1) {
                if (z2) {
                    ThemeAccent themeAccent2 = themeInfo.themeAccentsMap.get(themeInfo.currentAccentId);
                    themeInfo.themeAccentsMap.remove(themeAccent2.id);
                    themeInfo.themeAccents.remove(themeAccent2);
                    TLRPC$TL_theme tLRPC$TL_theme = themeAccent2.info;
                    if (tLRPC$TL_theme != null) {
                        themeInfo.accentsByThemeId.remove(tLRPC$TL_theme.id);
                    }
                }
                themeInfo.currentAccentId = themeInfo.prevAccentId;
                ThemeAccent accent = themeInfo.getAccent(false);
                if (accent != null) {
                    themeInfo.overrideWallpaper = accent.overrideWallpaper;
                } else {
                    themeInfo.overrideWallpaper = null;
                }
            }
            if (currentTheme == themeInfo) {
                refreshThemeColors();
            }
        }
        themeInfo.prevAccentId = -1;
    }

    /* access modifiers changed from: private */
    public static void saveOtherThemes(boolean z) {
        saveOtherThemes(z, false);
    }

    private static void saveOtherThemes(boolean z, boolean z2) {
        ArrayList<ThemeAccent> arrayList;
        int i = 0;
        SharedPreferences.Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
        if (z) {
            JSONArray jSONArray = new JSONArray();
            for (int i2 = 0; i2 < otherThemes.size(); i2++) {
                JSONObject saveJson = otherThemes.get(i2).getSaveJson();
                if (saveJson != null) {
                    jSONArray.put(saveJson);
                }
            }
            edit.putString("themes2", jSONArray.toString());
        }
        int i3 = 0;
        while (i3 < 3) {
            StringBuilder sb = new StringBuilder();
            sb.append("remoteThemesHash");
            Object obj = "";
            sb.append(i3 != 0 ? Integer.valueOf(i3) : obj);
            edit.putInt(sb.toString(), remoteThemesHash[i3]);
            StringBuilder sb2 = new StringBuilder();
            sb2.append("lastLoadingThemesTime");
            if (i3 != 0) {
                obj = Integer.valueOf(i3);
            }
            sb2.append(obj);
            edit.putInt(sb2.toString(), lastLoadingThemesTime[i3]);
            i3++;
        }
        edit.putInt("lastLoadingCurrentThemeTime", lastLoadingCurrentThemeTime);
        edit.commit();
        if (z) {
            while (i < 5) {
                ThemeInfo themeInfo = themesDict.get(i != 0 ? i != 1 ? i != 2 ? i != 3 ? "Night" : "Day" : "Arctic Blue" : "Dark Blue" : "Blue");
                if (!(themeInfo == null || (arrayList = themeInfo.themeAccents) == null || arrayList.isEmpty())) {
                    saveThemeAccents(themeInfo, true, false, false, false, z2);
                }
                i++;
            }
        }
    }

    public static HashMap<String, Integer> getDefaultColors() {
        return defaultColors;
    }

    public static ThemeInfo getPreviousTheme() {
        return previousTheme;
    }

    public static String getCurrentNightThemeName() {
        ThemeInfo themeInfo = currentNightTheme;
        if (themeInfo == null) {
            return "";
        }
        String name = themeInfo.getName();
        return name.toLowerCase().endsWith(".attheme") ? name.substring(0, name.lastIndexOf(46)) : name;
    }

    public static ThemeInfo getCurrentTheme() {
        ThemeInfo themeInfo = currentDayTheme;
        return themeInfo != null ? themeInfo : defaultTheme;
    }

    public static ThemeInfo getCurrentNightTheme() {
        return currentNightTheme;
    }

    public static boolean isCurrentThemeNight() {
        return currentTheme == currentNightTheme;
    }

    public static ThemeInfo getActiveTheme() {
        return currentTheme;
    }

    /* access modifiers changed from: private */
    public static long getAutoNightSwitchThemeDelay() {
        return Math.abs(lastThemeSwitchTime - SystemClock.elapsedRealtime()) >= 12000 ? 1800 : 12000;
    }

    public static void setCurrentNightTheme(ThemeInfo themeInfo) {
        boolean z = currentTheme == currentNightTheme;
        currentNightTheme = themeInfo;
        if (z) {
            applyDayNightThemeMaybe(true);
        }
    }

    public static void checkAutoNightThemeConditions() {
        checkAutoNightThemeConditions(false);
    }

    public static void cancelAutoNightThemeCallbacks() {
        if (selectedAutoNightType != 2) {
            if (switchNightRunnableScheduled) {
                switchNightRunnableScheduled = false;
                AndroidUtilities.cancelRunOnUIThread(switchNightBrightnessRunnable);
            }
            if (switchDayRunnableScheduled) {
                switchDayRunnableScheduled = false;
                AndroidUtilities.cancelRunOnUIThread(switchDayBrightnessRunnable);
            }
            if (lightSensorRegistered) {
                lastBrightnessValue = 1.0f;
                sensorManager.unregisterListener(ambientSensorListener, lightSensor);
                lightSensorRegistered = false;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("light sensor unregistered");
                }
            }
        }
    }

    private static int needSwitchToTheme() {
        Sensor sensor;
        SensorEventListener sensorEventListener;
        int i;
        int i2;
        int i3 = selectedAutoNightType;
        if (i3 == 1) {
            Calendar instance = Calendar.getInstance();
            instance.setTimeInMillis(System.currentTimeMillis());
            int i4 = (instance.get(11) * 60) + instance.get(12);
            if (autoNightScheduleByLocation) {
                int i5 = instance.get(5);
                if (autoNightLastSunCheckDay != i5) {
                    double d = autoNightLocationLatitude;
                    if (d != 10000.0d) {
                        double d2 = autoNightLocationLongitude;
                        if (d2 != 10000.0d) {
                            int[] calculateSunriseSunset = SunDate.calculateSunriseSunset(d, d2);
                            autoNightSunriseTime = calculateSunriseSunset[0];
                            autoNightSunsetTime = calculateSunriseSunset[1];
                            autoNightLastSunCheckDay = i5;
                            saveAutoNightThemeConfig();
                        }
                    }
                }
                i2 = autoNightSunsetTime;
                i = autoNightSunriseTime;
            } else {
                i2 = autoNightDayStartTime;
                i = autoNightDayEndTime;
            }
            return i2 < i ? (i2 > i4 || i4 > i) ? 1 : 2 : ((i2 > i4 || i4 > 1440) && (i4 < 0 || i4 > i)) ? 1 : 2;
        }
        if (i3 == 2) {
            if (lightSensor == null) {
                SensorManager sensorManager2 = (SensorManager) ApplicationLoader.applicationContext.getSystemService("sensor");
                sensorManager = sensorManager2;
                lightSensor = sensorManager2.getDefaultSensor(5);
            }
            if (!(lightSensorRegistered || (sensor = lightSensor) == null || (sensorEventListener = ambientSensorListener) == null)) {
                sensorManager.registerListener(sensorEventListener, sensor, 500000);
                lightSensorRegistered = true;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("light sensor registered");
                }
            }
            if (lastBrightnessValue <= autoNightBrighnessThreshold) {
                if (!switchNightRunnableScheduled) {
                    return 2;
                }
            } else if (!switchDayRunnableScheduled) {
                return 1;
            }
        } else if (i3 == 3) {
            int i6 = ApplicationLoader.applicationContext.getResources().getConfiguration().uiMode & 48;
            if (i6 == 0 || i6 == 16) {
                return 1;
            }
            return i6 != 32 ? 0 : 2;
        } else if (i3 == 0) {
            return 1;
        }
    }

    public static void checkAutoNightThemeConditions(boolean z) {
        if (previousTheme == null) {
            if (!z && switchNightThemeDelay > 0) {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                long j = elapsedRealtime - lastDelayUpdateTime;
                lastDelayUpdateTime = elapsedRealtime;
                int i = (int) (((long) switchNightThemeDelay) - j);
                switchNightThemeDelay = i;
                if (i > 0) {
                    return;
                }
            }
            boolean z2 = false;
            if (z) {
                if (switchNightRunnableScheduled) {
                    switchNightRunnableScheduled = false;
                    AndroidUtilities.cancelRunOnUIThread(switchNightBrightnessRunnable);
                }
                if (switchDayRunnableScheduled) {
                    switchDayRunnableScheduled = false;
                    AndroidUtilities.cancelRunOnUIThread(switchDayBrightnessRunnable);
                }
            }
            cancelAutoNightThemeCallbacks();
            int needSwitchToTheme = needSwitchToTheme();
            if (needSwitchToTheme != 0) {
                if (needSwitchToTheme == 2) {
                    z2 = true;
                }
                applyDayNightThemeMaybe(z2);
            }
            if (z) {
                lastThemeSwitchTime = 0;
            }
        }
    }

    /* access modifiers changed from: private */
    public static void applyDayNightThemeMaybe(boolean z) {
        if (previousTheme == null) {
            if (z) {
                if (currentTheme != currentNightTheme) {
                    isInNigthMode = true;
                    lastThemeSwitchTime = SystemClock.elapsedRealtime();
                    switchingNightTheme = true;
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, currentNightTheme, true, null, -1);
                    switchingNightTheme = false;
                }
            } else if (currentTheme != currentDayTheme) {
                isInNigthMode = false;
                lastThemeSwitchTime = SystemClock.elapsedRealtime();
                switchingNightTheme = true;
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, currentDayTheme, true, null, -1);
                switchingNightTheme = false;
            }
        }
    }

    public static boolean deleteTheme(ThemeInfo themeInfo) {
        boolean z = false;
        if (themeInfo.pathToFile == null) {
            return false;
        }
        if (currentTheme == themeInfo) {
            applyTheme(defaultTheme, true, false, false);
            z = true;
        }
        if (themeInfo == currentNightTheme) {
            currentNightTheme = themesDict.get("Dark Blue");
        }
        themeInfo.removeObservers();
        otherThemes.remove(themeInfo);
        themesDict.remove(themeInfo.name);
        OverrideWallpaperInfo overrideWallpaperInfo = themeInfo.overrideWallpaper;
        if (overrideWallpaperInfo != null) {
            overrideWallpaperInfo.delete();
        }
        themes.remove(themeInfo);
        new File(themeInfo.pathToFile).delete();
        saveOtherThemes(true);
        return z;
    }

    public static ThemeInfo createNewTheme(String str) {
        ThemeInfo themeInfo = new ThemeInfo();
        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
        themeInfo.pathToFile = new File(filesDirFixed, "theme" + Utilities.random.nextLong() + ".attheme").getAbsolutePath();
        themeInfo.name = str;
        themedWallpaperLink = getWallpaperUrl(currentTheme.overrideWallpaper);
        themeInfo.account = UserConfig.selectedAccount;
        saveCurrentTheme(themeInfo, true, true, false);
        return themeInfo;
    }

    private static String getWallpaperUrl(OverrideWallpaperInfo overrideWallpaperInfo) {
        String str;
        String str2 = null;
        if (overrideWallpaperInfo == null || TextUtils.isEmpty(overrideWallpaperInfo.slug) || overrideWallpaperInfo.slug.equals("d")) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (overrideWallpaperInfo.isBlurred) {
            sb.append("blur");
        }
        if (overrideWallpaperInfo.isMotion) {
            if (sb.length() > 0) {
                sb.append("+");
            }
            sb.append("motion");
        }
        int i = overrideWallpaperInfo.color;
        if (i == 0) {
            str = "https://attheme.org?slug=" + overrideWallpaperInfo.slug;
        } else {
            String lowerCase = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (i >> 16)) & 255), Integer.valueOf(((byte) (overrideWallpaperInfo.color >> 8)) & 255), Byte.valueOf((byte) (overrideWallpaperInfo.color & 255))}).toLowerCase();
            int i2 = overrideWallpaperInfo.gradientColor;
            if (i2 != 0) {
                str2 = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (i2 >> 16)) & 255), Integer.valueOf(((byte) (overrideWallpaperInfo.gradientColor >> 8)) & 255), Byte.valueOf((byte) (overrideWallpaperInfo.gradientColor & 255))}).toLowerCase();
            }
            if (str2 != null) {
                lowerCase = (lowerCase + "-" + str2) + "&rotation=" + overrideWallpaperInfo.rotation;
            }
            str = "https://attheme.org?slug=" + overrideWallpaperInfo.slug + "&intensity=" + ((int) (overrideWallpaperInfo.intensity * 100.0f)) + "&bg_color=" + lowerCase;
        }
        if (sb.length() <= 0) {
            return str;
        }
        return str + "&mode=" + sb.toString();
    }

    /* JADX WARNING: Removed duplicated region for block: B:106:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x01ce A[SYNTHETIC, Splitter:B:88:0x01ce] */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x01d8  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x01e8 A[SYNTHETIC, Splitter:B:95:0x01e8] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void saveCurrentTheme(org.telegram.ui.ActionBar.Theme.ThemeInfo r12, boolean r13, boolean r14, boolean r15) {
        /*
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r0 = r12.overrideWallpaper
            if (r0 == 0) goto L_0x0009
            java.lang.String r0 = getWallpaperUrl(r0)
            goto L_0x000b
        L_0x0009:
            java.lang.String r0 = themedWallpaperLink
        L_0x000b:
            if (r14 == 0) goto L_0x0010
            android.graphics.drawable.Drawable r1 = wallpaper
            goto L_0x0012
        L_0x0010:
            android.graphics.drawable.Drawable r1 = themedWallpaper
        L_0x0012:
            if (r14 == 0) goto L_0x001a
            if (r1 == 0) goto L_0x001a
            android.graphics.drawable.Drawable r2 = wallpaper
            themedWallpaper = r2
        L_0x001a:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = currentTheme
            r3 = 0
            org.telegram.ui.ActionBar.Theme$ThemeAccent r2 = r2.getAccent(r3)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = currentTheme
            boolean r4 = r4.firstAccentIsDefault
            if (r4 == 0) goto L_0x0030
            int r4 = r2.id
            int r5 = DEFALT_THEME_ACCENT_ID
            if (r4 != r5) goto L_0x0030
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            goto L_0x0032
        L_0x0030:
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = currentColors
        L_0x0032:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            if (r4 == r6) goto L_0x005d
            if (r2 == 0) goto L_0x0040
            int r6 = r2.myMessagesAccentColor
            goto L_0x0041
        L_0x0040:
            r6 = 0
        L_0x0041:
            if (r2 == 0) goto L_0x0046
            int r2 = r2.myMessagesGradientAccentColor
            goto L_0x0047
        L_0x0046:
            r2 = 0
        L_0x0047:
            if (r6 == 0) goto L_0x005d
            if (r2 == 0) goto L_0x005d
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r7 = "chat_outBubble"
            r4.put(r7, r6)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            java.lang.String r6 = "chat_outBubbleGradient"
            r4.put(r6, r2)
        L_0x005d:
            java.util.Set r2 = r4.entrySet()
            java.util.Iterator r2 = r2.iterator()
        L_0x0065:
            boolean r6 = r2.hasNext()
            java.lang.String r7 = "\n"
            if (r6 == 0) goto L_0x00a3
            java.lang.Object r6 = r2.next()
            java.util.Map$Entry r6 = (java.util.Map.Entry) r6
            java.lang.Object r8 = r6.getKey()
            java.lang.String r8 = (java.lang.String) r8
            boolean r9 = r1 instanceof android.graphics.drawable.BitmapDrawable
            if (r9 != 0) goto L_0x007f
            if (r0 == 0) goto L_0x0090
        L_0x007f:
            java.lang.String r9 = "chat_wallpaper"
            boolean r9 = r9.equals(r8)
            if (r9 != 0) goto L_0x0065
            java.lang.String r9 = "chat_wallpaper_gradient_to"
            boolean r9 = r9.equals(r8)
            if (r9 == 0) goto L_0x0090
            goto L_0x0065
        L_0x0090:
            r5.append(r8)
            java.lang.String r8 = "="
            r5.append(r8)
            java.lang.Object r6 = r6.getValue()
            r5.append(r6)
            r5.append(r7)
            goto L_0x0065
        L_0x00a3:
            r2 = 0
            java.io.FileOutputStream r6 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x01c8 }
            java.lang.String r8 = r12.pathToFile     // Catch:{ Exception -> 0x01c8 }
            r6.<init>(r8)     // Catch:{ Exception -> 0x01c8 }
            int r2 = r5.length()     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            if (r2 != 0) goto L_0x00c0
            boolean r2 = r1 instanceof android.graphics.drawable.BitmapDrawable     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            if (r2 != 0) goto L_0x00c0
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            if (r2 == 0) goto L_0x00c0
            r2 = 32
            r5.append(r2)     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
        L_0x00c0:
            java.lang.String r2 = r5.toString()     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            byte[] r2 = org.telegram.messenger.AndroidUtilities.getStringBytes(r2)     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r6.write(r2)     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r5 = 1
            r8 = 87
            if (r2 != 0) goto L_0x0128
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r2.<init>()     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            java.lang.String r9 = "WLS="
            r2.append(r9)     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r2.append(r0)     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r2.append(r7)     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            byte[] r2 = org.telegram.messenger.AndroidUtilities.getStringBytes(r2)     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r6.write(r2)     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            if (r14 == 0) goto L_0x016b
            android.graphics.drawable.BitmapDrawable r1 = (android.graphics.drawable.BitmapDrawable) r1     // Catch:{ all -> 0x0123 }
            android.graphics.Bitmap r14 = r1.getBitmap()     // Catch:{ all -> 0x0123 }
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch:{ all -> 0x0123 }
            java.io.File r2 = new java.io.File     // Catch:{ all -> 0x0123 }
            java.io.File r7 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ all -> 0x0123 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x0123 }
            r9.<init>()     // Catch:{ all -> 0x0123 }
            java.lang.String r0 = org.telegram.messenger.Utilities.MD5(r0)     // Catch:{ all -> 0x0123 }
            r9.append(r0)     // Catch:{ all -> 0x0123 }
            java.lang.String r0 = ".wp"
            r9.append(r0)     // Catch:{ all -> 0x0123 }
            java.lang.String r0 = r9.toString()     // Catch:{ all -> 0x0123 }
            r2.<init>(r7, r0)     // Catch:{ all -> 0x0123 }
            r1.<init>(r2)     // Catch:{ all -> 0x0123 }
            android.graphics.Bitmap$CompressFormat r0 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ all -> 0x0123 }
            r14.compress(r0, r8, r1)     // Catch:{ all -> 0x0123 }
            r1.close()     // Catch:{ all -> 0x0123 }
            goto L_0x016b
        L_0x0123:
            r14 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r14)     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            goto L_0x016b
        L_0x0128:
            boolean r14 = r1 instanceof android.graphics.drawable.BitmapDrawable     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            if (r14 == 0) goto L_0x016b
            r14 = r1
            android.graphics.drawable.BitmapDrawable r14 = (android.graphics.drawable.BitmapDrawable) r14     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            android.graphics.Bitmap r14 = r14.getBitmap()     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r0 = 2
            if (r14 == 0) goto L_0x0162
            r2 = 4
            byte[] r7 = new byte[r2]     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r7[r3] = r8     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r9 = 80
            r7[r5] = r9     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r10 = 83
            r7[r0] = r10     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r10 = 3
            r11 = 10
            r7[r10] = r11     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r6.write(r7)     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            android.graphics.Bitmap$CompressFormat r7 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r14.compress(r7, r8, r6)     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r14 = 5
            byte[] r14 = new byte[r14]     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r14[r3] = r11     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r14[r5] = r8     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r14[r0] = r9     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r7 = 69
            r14[r10] = r7     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r14[r2] = r11     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r6.write(r14)     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
        L_0x0162:
            if (r13 == 0) goto L_0x016b
            if (r15 != 0) goto L_0x016b
            wallpaper = r1     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            calcBackgroundColor(r1, r0)     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
        L_0x016b:
            if (r15 != 0) goto L_0x01bc
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r14 = themesDict     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            java.lang.String r15 = r12.getKey()     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            java.lang.Object r14 = r14.get(r15)     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            if (r14 != 0) goto L_0x0192
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r14 = themes     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r14.add(r12)     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r14 = themesDict     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            java.lang.String r15 = r12.getKey()     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r14.put(r15, r12)     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r14 = otherThemes     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r14.add(r12)     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            saveOtherThemes(r5)     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            sortThemes()     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
        L_0x0192:
            currentTheme = r12     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            org.telegram.ui.ActionBar.Theme$ThemeInfo r14 = currentNightTheme     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            if (r12 == r14) goto L_0x019a
            currentDayTheme = r12     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
        L_0x019a:
            java.util.HashMap<java.lang.String, java.lang.Integer> r14 = defaultColors     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            if (r4 != r14) goto L_0x01a6
            java.util.HashMap<java.lang.String, java.lang.Integer> r14 = currentColorsNoAccent     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r14.clear()     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            refreshThemeColors()     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
        L_0x01a6:
            android.content.SharedPreferences r14 = org.telegram.messenger.MessagesController.getGlobalMainSettings()     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            android.content.SharedPreferences$Editor r14 = r14.edit()     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            java.lang.String r15 = "theme"
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = currentDayTheme     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            java.lang.String r0 = r0.getKey()     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r14.putString(r15, r0)     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
            r14.commit()     // Catch:{ Exception -> 0x01c3, all -> 0x01c0 }
        L_0x01bc:
            r6.close()     // Catch:{ Exception -> 0x01d2 }
            goto L_0x01d6
        L_0x01c0:
            r12 = move-exception
            r2 = r6
            goto L_0x01e6
        L_0x01c3:
            r14 = move-exception
            r2 = r6
            goto L_0x01c9
        L_0x01c6:
            r12 = move-exception
            goto L_0x01e6
        L_0x01c8:
            r14 = move-exception
        L_0x01c9:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r14)     // Catch:{ all -> 0x01c6 }
            if (r2 == 0) goto L_0x01d6
            r2.close()     // Catch:{ Exception -> 0x01d2 }
            goto L_0x01d6
        L_0x01d2:
            r14 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r14)
        L_0x01d6:
            if (r13 == 0) goto L_0x01e5
            int r13 = r12.account
            org.telegram.messenger.MessagesController r13 = org.telegram.messenger.MessagesController.getInstance(r13)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r14 = r12.getAccent(r3)
            r13.saveThemeToServer(r12, r14)
        L_0x01e5:
            return
        L_0x01e6:
            if (r2 == 0) goto L_0x01f0
            r2.close()     // Catch:{ Exception -> 0x01ec }
            goto L_0x01f0
        L_0x01ec:
            r13 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)
        L_0x01f0:
            goto L_0x01f2
        L_0x01f1:
            throw r12
        L_0x01f2:
            goto L_0x01f1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.saveCurrentTheme(org.telegram.ui.ActionBar.Theme$ThemeInfo, boolean, boolean, boolean):void");
    }

    public static void checkCurrentRemoteTheme(boolean z) {
        int i;
        if (loadingCurrentTheme != 0) {
            return;
        }
        if (z || Math.abs((System.currentTimeMillis() / 1000) - ((long) lastLoadingCurrentThemeTime)) >= 3600) {
            int i2 = 0;
            while (i2 < 2) {
                ThemeInfo themeInfo = i2 == 0 ? currentDayTheme : currentNightTheme;
                if (themeInfo != null && UserConfig.getInstance(themeInfo.account).isClientActivated()) {
                    ThemeAccent accent = themeInfo.getAccent(false);
                    TLRPC$TL_theme tLRPC$TL_theme = themeInfo.info;
                    if (tLRPC$TL_theme != null) {
                        i = themeInfo.account;
                    } else if (!(accent == null || (tLRPC$TL_theme = accent.info) == null)) {
                        i = UserConfig.selectedAccount;
                    }
                    if (!(tLRPC$TL_theme == null || tLRPC$TL_theme.document == null)) {
                        loadingCurrentTheme++;
                        TLRPC$TL_account_getTheme tLRPC$TL_account_getTheme = new TLRPC$TL_account_getTheme();
                        tLRPC$TL_account_getTheme.document_id = tLRPC$TL_theme.document.id;
                        tLRPC$TL_account_getTheme.format = "android";
                        TLRPC$TL_inputTheme tLRPC$TL_inputTheme = new TLRPC$TL_inputTheme();
                        tLRPC$TL_inputTheme.access_hash = tLRPC$TL_theme.access_hash;
                        tLRPC$TL_inputTheme.id = tLRPC$TL_theme.id;
                        tLRPC$TL_account_getTheme.theme = tLRPC$TL_inputTheme;
                        ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_account_getTheme, new RequestDelegate(themeInfo, tLRPC$TL_theme) {
                            private final /* synthetic */ Theme.ThemeInfo f$1;
                            private final /* synthetic */ TLRPC$TL_theme f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                AndroidUtilities.runOnUIThread(new Runnable(Theme.ThemeAccent.this, this.f$1, this.f$2) {
                                    private final /* synthetic */ Theme.ThemeAccent f$1;
                                    private final /* synthetic */ Theme.ThemeInfo f$2;
                                    private final /* synthetic */ TLRPC$TL_theme f$3;

                                    {
                                        this.f$1 = r2;
                                        this.f$2 = r3;
                                        this.f$3 = r4;
                                    }

                                    public final void run() {
                                        Theme.lambda$null$3(TLObject.this, this.f$1, this.f$2, this.f$3);
                                    }
                                });
                            }
                        });
                    }
                }
                i2++;
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:40:0x0095  */
    /* JADX WARNING: Removed duplicated region for block: B:42:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$null$3(org.telegram.tgnet.TLObject r7, org.telegram.ui.ActionBar.Theme.ThemeAccent r8, org.telegram.ui.ActionBar.Theme.ThemeInfo r9, org.telegram.tgnet.TLRPC$TL_theme r10) {
        /*
            int r0 = loadingCurrentTheme
            r1 = 1
            int r0 = r0 - r1
            loadingCurrentTheme = r0
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_theme
            r2 = 0
            if (r0 == 0) goto L_0x0090
            org.telegram.tgnet.TLRPC$TL_theme r7 = (org.telegram.tgnet.TLRPC$TL_theme) r7
            if (r8 == 0) goto L_0x0077
            org.telegram.tgnet.TLRPC$TL_themeSettings r0 = r7.settings
            if (r0 == 0) goto L_0x0077
            boolean r10 = org.telegram.ui.ActionBar.Theme.ThemeInfo.accentEquals(r8, r0)
            if (r10 != 0) goto L_0x0062
            java.io.File r10 = r8.getPathToWallpaper()
            if (r10 == 0) goto L_0x0022
            r10.delete()
        L_0x0022:
            org.telegram.tgnet.TLRPC$TL_themeSettings r10 = r7.settings
            org.telegram.ui.ActionBar.Theme.ThemeInfo.fillAccentValues(r8, r10)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r10 = currentTheme
            if (r10 != r9) goto L_0x005d
            int r9 = r10.currentAccentId
            int r10 = r8.id
            if (r9 != r10) goto L_0x005d
            refreshThemeColors()
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r10 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            r0 = 4
            java.lang.Object[] r0 = new java.lang.Object[r0]
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = currentTheme
            r0[r2] = r3
            org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = currentNightTheme
            if (r4 != r3) goto L_0x0047
            r3 = 1
            goto L_0x0048
        L_0x0047:
            r3 = 0
        L_0x0048:
            java.lang.Boolean r3 = java.lang.Boolean.valueOf(r3)
            r0[r1] = r3
            r3 = 2
            r4 = 0
            r0[r3] = r4
            r3 = 3
            r4 = -1
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r0[r3] = r4
            r9.postNotificationName(r10, r0)
        L_0x005d:
            org.telegram.ui.ActionBar.Theme.PatternsLoader.createLoader(r1)
            r9 = 1
            goto L_0x0063
        L_0x0062:
            r9 = 0
        L_0x0063:
            org.telegram.tgnet.TLRPC$TL_themeSettings r7 = r7.settings
            org.telegram.tgnet.TLRPC$WallPaper r7 = r7.wallpaper
            if (r7 == 0) goto L_0x0072
            org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r7.settings
            if (r7 == 0) goto L_0x0072
            boolean r7 = r7.motion
            if (r7 == 0) goto L_0x0072
            goto L_0x0073
        L_0x0072:
            r1 = 0
        L_0x0073:
            r8.patternMotion = r1
            r1 = r9
            goto L_0x0091
        L_0x0077:
            org.telegram.tgnet.TLRPC$Document r0 = r7.document
            if (r0 == 0) goto L_0x0090
            long r3 = r0.id
            org.telegram.tgnet.TLRPC$Document r10 = r10.document
            long r5 = r10.id
            int r10 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r10 == 0) goto L_0x0090
            if (r8 == 0) goto L_0x008a
            r8.info = r7
            goto L_0x0091
        L_0x008a:
            r9.info = r7
            r9.loadThemeDocument()
            goto L_0x0091
        L_0x0090:
            r1 = 0
        L_0x0091:
            int r7 = loadingCurrentTheme
            if (r7 != 0) goto L_0x00a2
            long r7 = java.lang.System.currentTimeMillis()
            r9 = 1000(0x3e8, double:4.94E-321)
            long r7 = r7 / r9
            int r8 = (int) r7
            lastLoadingCurrentThemeTime = r8
            saveOtherThemes(r1)
        L_0x00a2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.lambda$null$3(org.telegram.tgnet.TLObject, org.telegram.ui.ActionBar.Theme$ThemeAccent, org.telegram.ui.ActionBar.Theme$ThemeInfo, org.telegram.tgnet.TLRPC$TL_theme):void");
    }

    public static void loadRemoteThemes(int i, boolean z) {
        if (loadingRemoteThemes[i]) {
            return;
        }
        if ((z || Math.abs((System.currentTimeMillis() / 1000) - ((long) lastLoadingThemesTime[i])) >= 3600) && UserConfig.getInstance(i).isClientActivated()) {
            loadingRemoteThemes[i] = true;
            TLRPC$TL_account_getThemes tLRPC$TL_account_getThemes = new TLRPC$TL_account_getThemes();
            tLRPC$TL_account_getThemes.format = "android";
            tLRPC$TL_account_getThemes.hash = remoteThemesHash[i];
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_account_getThemes, new RequestDelegate(i) {
                private final /* synthetic */ int f$0;

                {
                    this.f$0 = r1;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable(this.f$0, tLObject) {
                        private final /* synthetic */ int f$0;
                        private final /* synthetic */ TLObject f$1;

                        {
                            this.f$0 = r1;
                            this.f$1 = r2;
                        }

                        public final void run() {
                            Theme.lambda$null$5(this.f$0, this.f$1);
                        }
                    });
                }
            });
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:84:0x01db  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$null$5(int r16, org.telegram.tgnet.TLObject r17) {
        /*
            r0 = r16
            r1 = r17
            boolean[] r2 = loadingRemoteThemes
            r3 = 0
            r2[r0] = r3
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_account_themes
            if (r2 == 0) goto L_0x0244
            org.telegram.tgnet.TLRPC$TL_account_themes r1 = (org.telegram.tgnet.TLRPC$TL_account_themes) r1
            int[] r2 = remoteThemesHash
            int r4 = r1.hash
            r2[r0] = r4
            int[] r2 = lastLoadingThemesTime
            long r4 = java.lang.System.currentTimeMillis()
            r6 = 1000(0x3e8, double:4.94E-321)
            long r4 = r4 / r6
            int r5 = (int) r4
            r2[r0] = r5
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r4 = themes
            int r4 = r4.size()
            r5 = 0
        L_0x002d:
            if (r5 >= r4) goto L_0x0069
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r6 = themes
            java.lang.Object r6 = r6.get(r5)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r6 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r6
            org.telegram.tgnet.TLRPC$TL_theme r7 = r6.info
            if (r7 == 0) goto L_0x0043
            int r7 = r6.account
            if (r7 != r0) goto L_0x0043
            r2.add(r6)
            goto L_0x0066
        L_0x0043:
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeAccent> r7 = r6.themeAccents
            if (r7 == 0) goto L_0x0066
            r7 = 0
        L_0x0048:
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeAccent> r8 = r6.themeAccents
            int r8 = r8.size()
            if (r7 >= r8) goto L_0x0066
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeAccent> r8 = r6.themeAccents
            java.lang.Object r8 = r8.get(r7)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r8 = (org.telegram.ui.ActionBar.Theme.ThemeAccent) r8
            org.telegram.tgnet.TLRPC$TL_theme r9 = r8.info
            if (r9 == 0) goto L_0x0063
            int r9 = r8.account
            if (r9 != r0) goto L_0x0063
            r2.add(r8)
        L_0x0063:
            int r7 = r7 + 1
            goto L_0x0048
        L_0x0066:
            int r5 = r5 + 1
            goto L_0x002d
        L_0x0069:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Theme> r4 = r1.themes
            int r4 = r4.size()
            r5 = 0
            r6 = 0
            r7 = 0
        L_0x0072:
            r8 = -1
            r12 = 4
            r13 = 1
            if (r5 >= r4) goto L_0x0185
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Theme> r14 = r1.themes
            java.lang.Object r14 = r14.get(r5)
            org.telegram.tgnet.TLRPC$Theme r14 = (org.telegram.tgnet.TLRPC$Theme) r14
            boolean r15 = r14 instanceof org.telegram.tgnet.TLRPC$TL_theme
            if (r15 != 0) goto L_0x0085
            goto L_0x0181
        L_0x0085:
            org.telegram.tgnet.TLRPC$TL_theme r14 = (org.telegram.tgnet.TLRPC$TL_theme) r14
            org.telegram.tgnet.TLRPC$TL_themeSettings r15 = r14.settings
            if (r15 == 0) goto L_0x011f
            java.lang.String r15 = getBaseThemeKey(r15)
            if (r15 != 0) goto L_0x0093
            goto L_0x0181
        L_0x0093:
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r9 = themesDict
            java.lang.Object r9 = r9.get(r15)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r9
            if (r9 == 0) goto L_0x0181
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeAccent> r15 = r9.themeAccents
            if (r15 != 0) goto L_0x00a3
            goto L_0x0181
        L_0x00a3:
            android.util.LongSparseArray<org.telegram.ui.ActionBar.Theme$ThemeAccent> r15 = r9.accentsByThemeId
            long r10 = r14.id
            java.lang.Object r10 = r15.get(r10)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r10 = (org.telegram.ui.ActionBar.Theme.ThemeAccent) r10
            if (r10 == 0) goto L_0x0111
            org.telegram.tgnet.TLRPC$TL_themeSettings r11 = r14.settings
            boolean r11 = org.telegram.ui.ActionBar.Theme.ThemeInfo.accentEquals(r10, r11)
            if (r11 != 0) goto L_0x00fb
            java.io.File r6 = r10.getPathToWallpaper()
            if (r6 == 0) goto L_0x00c0
            r6.delete()
        L_0x00c0:
            org.telegram.tgnet.TLRPC$TL_themeSettings r6 = r14.settings
            org.telegram.ui.ActionBar.Theme.ThemeInfo.fillAccentValues(r10, r6)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r6 = currentTheme
            if (r6 != r9) goto L_0x00f9
            int r6 = r6.currentAccentId
            int r7 = r10.id
            if (r6 != r7) goto L_0x00f9
            refreshThemeColors()
            org.telegram.messenger.NotificationCenter r6 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r7 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            java.lang.Object[] r9 = new java.lang.Object[r12]
            org.telegram.ui.ActionBar.Theme$ThemeInfo r11 = currentTheme
            r9[r3] = r11
            org.telegram.ui.ActionBar.Theme$ThemeInfo r12 = currentNightTheme
            if (r12 != r11) goto L_0x00e4
            r11 = 1
            goto L_0x00e5
        L_0x00e4:
            r11 = 0
        L_0x00e5:
            java.lang.Boolean r11 = java.lang.Boolean.valueOf(r11)
            r9[r13] = r11
            r11 = 0
            r12 = 2
            r9[r12] = r11
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r11 = 3
            r9[r11] = r8
            r6.postNotificationName(r7, r9)
        L_0x00f9:
            r6 = 1
            r7 = 1
        L_0x00fb:
            org.telegram.tgnet.TLRPC$TL_themeSettings r8 = r14.settings
            org.telegram.tgnet.TLRPC$WallPaper r8 = r8.wallpaper
            if (r8 == 0) goto L_0x010a
            org.telegram.tgnet.TLRPC$WallPaperSettings r8 = r8.settings
            if (r8 == 0) goto L_0x010a
            boolean r8 = r8.motion
            if (r8 == 0) goto L_0x010a
            goto L_0x010b
        L_0x010a:
            r13 = 0
        L_0x010b:
            r10.patternMotion = r13
            r2.remove(r10)
            goto L_0x0181
        L_0x0111:
            org.telegram.ui.ActionBar.Theme$ThemeAccent r8 = r9.createNewAccent(r14, r0)
            java.lang.String r8 = r8.patternSlug
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 != 0) goto L_0x0181
            r6 = 1
            goto L_0x0181
        L_0x011f:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "remote"
            r8.append(r9)
            long r9 = r14.id
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r9 = themesDict
            java.lang.Object r9 = r9.get(r8)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r9
            if (r9 != 0) goto L_0x016f
            org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = new org.telegram.ui.ActionBar.Theme$ThemeInfo
            r9.<init>()
            r9.account = r0
            java.io.File r7 = new java.io.File
            java.io.File r10 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r8)
            java.lang.String r8 = ".attheme"
            r11.append(r8)
            java.lang.String r8 = r11.toString()
            r7.<init>(r10, r8)
            java.lang.String r7 = r7.getAbsolutePath()
            r9.pathToFile = r7
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r7 = themes
            r7.add(r9)
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r7 = otherThemes
            r7.add(r9)
            r7 = 1
            goto L_0x0172
        L_0x016f:
            r2.remove(r9)
        L_0x0172:
            java.lang.String r8 = r14.title
            r9.name = r8
            r9.info = r14
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r8 = themesDict
            java.lang.String r10 = r9.getKey()
            r8.put(r10, r9)
        L_0x0181:
            int r5 = r5 + 1
            goto L_0x0072
        L_0x0185:
            int r0 = r2.size()
            r1 = 0
        L_0x018a:
            if (r1 >= r0) goto L_0x022c
            java.lang.Object r4 = r2.get(r1)
            boolean r5 = r4 instanceof org.telegram.ui.ActionBar.Theme.ThemeInfo
            if (r5 == 0) goto L_0x01e6
            org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r4
            r4.removeObservers()
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r5 = otherThemes
            r5.remove(r4)
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r5 = themesDict
            java.lang.String r9 = r4.name
            r5.remove(r9)
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r5 = r4.overrideWallpaper
            if (r5 == 0) goto L_0x01ac
            r5.delete()
        L_0x01ac:
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r5 = themes
            r5.remove(r4)
            java.io.File r5 = new java.io.File
            java.lang.String r9 = r4.pathToFile
            r5.<init>(r9)
            r5.delete()
            org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = currentDayTheme
            if (r5 != r4) goto L_0x01c4
            org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = defaultTheme
            currentDayTheme = r5
            goto L_0x01d6
        L_0x01c4:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = currentNightTheme
            if (r5 != r4) goto L_0x01d6
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r5 = themesDict
            java.lang.String r9 = "Dark Blue"
            java.lang.Object r5 = r5.get(r9)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r5
            currentNightTheme = r5
            r5 = 1
            goto L_0x01d7
        L_0x01d6:
            r5 = 0
        L_0x01d7:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = currentTheme
            if (r9 != r4) goto L_0x0225
            if (r5 == 0) goto L_0x01e0
            org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = currentNightTheme
            goto L_0x01e2
        L_0x01e0:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = currentDayTheme
        L_0x01e2:
            applyTheme(r4, r13, r3, r5)
            goto L_0x0225
        L_0x01e6:
            boolean r5 = r4 instanceof org.telegram.ui.ActionBar.Theme.ThemeAccent
            if (r5 == 0) goto L_0x0225
            org.telegram.ui.ActionBar.Theme$ThemeAccent r4 = (org.telegram.ui.ActionBar.Theme.ThemeAccent) r4
            org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = r4.parentTheme
            boolean r5 = deleteThemeAccent(r5, r4, r3)
            if (r5 == 0) goto L_0x0225
            org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = currentTheme
            org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r4.parentTheme
            if (r5 != r4) goto L_0x0225
            refreshThemeColors()
            org.telegram.messenger.NotificationCenter r4 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r5 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            java.lang.Object[] r9 = new java.lang.Object[r12]
            org.telegram.ui.ActionBar.Theme$ThemeInfo r10 = currentTheme
            r9[r3] = r10
            org.telegram.ui.ActionBar.Theme$ThemeInfo r11 = currentNightTheme
            if (r11 != r10) goto L_0x020f
            r10 = 1
            goto L_0x0210
        L_0x020f:
            r10 = 0
        L_0x0210:
            java.lang.Boolean r10 = java.lang.Boolean.valueOf(r10)
            r9[r13] = r10
            r10 = 0
            r11 = 2
            r9[r11] = r10
            java.lang.Integer r14 = java.lang.Integer.valueOf(r8)
            r15 = 3
            r9[r15] = r14
            r4.postNotificationName(r5, r9)
            goto L_0x0228
        L_0x0225:
            r10 = 0
            r11 = 2
            r15 = 3
        L_0x0228:
            int r1 = r1 + 1
            goto L_0x018a
        L_0x022c:
            saveOtherThemes(r13)
            sortThemes()
            if (r7 == 0) goto L_0x023f
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r1 = org.telegram.messenger.NotificationCenter.themeListUpdated
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r0.postNotificationName(r1, r2)
        L_0x023f:
            if (r6 == 0) goto L_0x0244
            org.telegram.ui.ActionBar.Theme.PatternsLoader.createLoader(r13)
        L_0x0244:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.lambda$null$5(int, org.telegram.tgnet.TLObject):void");
    }

    public static String getBaseThemeKey(TLRPC$TL_themeSettings tLRPC$TL_themeSettings) {
        TLRPC$BaseTheme tLRPC$BaseTheme = tLRPC$TL_themeSettings.base_theme;
        if (tLRPC$BaseTheme instanceof TLRPC$TL_baseThemeClassic) {
            return "Blue";
        }
        if (tLRPC$BaseTheme instanceof TLRPC$TL_baseThemeDay) {
            return "Day";
        }
        if (tLRPC$BaseTheme instanceof TLRPC$TL_baseThemeTinted) {
            return "Dark Blue";
        }
        if (tLRPC$BaseTheme instanceof TLRPC$TL_baseThemeArctic) {
            return "Arctic Blue";
        }
        if (tLRPC$BaseTheme instanceof TLRPC$TL_baseThemeNight) {
            return "Night";
        }
        return null;
    }

    public static TLRPC$BaseTheme getBaseThemeByKey(String str) {
        if ("Blue".equals(str)) {
            return new TLRPC$TL_baseThemeClassic();
        }
        if ("Day".equals(str)) {
            return new TLRPC$TL_baseThemeDay();
        }
        if ("Dark Blue".equals(str)) {
            return new TLRPC$TL_baseThemeTinted();
        }
        if ("Arctic Blue".equals(str)) {
            return new TLRPC$TL_baseThemeArctic();
        }
        if ("Night".equals(str)) {
            return new TLRPC$TL_baseThemeNight();
        }
        return null;
    }

    public static void setThemeFileReference(TLRPC$TL_theme tLRPC$TL_theme) {
        TLRPC$Document tLRPC$Document;
        int size = themes.size();
        int i = 0;
        while (i < size) {
            TLRPC$TL_theme tLRPC$TL_theme2 = themes.get(i).info;
            if (tLRPC$TL_theme2 == null || tLRPC$TL_theme2.id != tLRPC$TL_theme.id) {
                i++;
            } else {
                TLRPC$Document tLRPC$Document2 = tLRPC$TL_theme2.document;
                if (tLRPC$Document2 != null && (tLRPC$Document = tLRPC$TL_theme.document) != null) {
                    tLRPC$Document2.file_reference = tLRPC$Document.file_reference;
                    saveOtherThemes(true);
                    return;
                }
                return;
            }
        }
    }

    public static boolean isThemeInstalled(ThemeInfo themeInfo) {
        return (themeInfo == null || themesDict.get(themeInfo.getKey()) == null) ? false : true;
    }

    public static void setThemeUploadInfo(ThemeInfo themeInfo, ThemeAccent themeAccent, TLRPC$TL_theme tLRPC$TL_theme, int i, boolean z) {
        String str;
        TLRPC$WallPaperSettings tLRPC$WallPaperSettings;
        if (tLRPC$TL_theme != null) {
            TLRPC$TL_themeSettings tLRPC$TL_themeSettings = tLRPC$TL_theme.settings;
            if (tLRPC$TL_themeSettings != null) {
                if (themeInfo == null) {
                    String baseThemeKey = getBaseThemeKey(tLRPC$TL_themeSettings);
                    if (baseThemeKey != null && (themeInfo = themesDict.get(baseThemeKey)) != null) {
                        themeAccent = themeInfo.accentsByThemeId.get(tLRPC$TL_theme.id);
                    } else {
                        return;
                    }
                }
                if (themeAccent != null) {
                    TLRPC$TL_theme tLRPC$TL_theme2 = themeAccent.info;
                    if (tLRPC$TL_theme2 != null) {
                        themeInfo.accentsByThemeId.remove(tLRPC$TL_theme2.id);
                    }
                    themeAccent.info = tLRPC$TL_theme;
                    themeAccent.account = i;
                    themeInfo.accentsByThemeId.put(tLRPC$TL_theme.id, themeAccent);
                    if (!ThemeInfo.accentEquals(themeAccent, tLRPC$TL_theme.settings)) {
                        File pathToWallpaper = themeAccent.getPathToWallpaper();
                        if (pathToWallpaper != null) {
                            pathToWallpaper.delete();
                        }
                        ThemeInfo.fillAccentValues(themeAccent, tLRPC$TL_theme.settings);
                        ThemeInfo themeInfo2 = currentTheme;
                        if (themeInfo2 == themeInfo && themeInfo2.currentAccentId == themeAccent.id) {
                            refreshThemeColors();
                            NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                            int i2 = NotificationCenter.needSetDayNightTheme;
                            Object[] objArr = new Object[4];
                            ThemeInfo themeInfo3 = currentTheme;
                            objArr[0] = themeInfo3;
                            objArr[1] = Boolean.valueOf(currentNightTheme == themeInfo3);
                            objArr[2] = null;
                            objArr[3] = -1;
                            globalInstance.postNotificationName(i2, objArr);
                        }
                        PatternsLoader.createLoader(true);
                    }
                    TLRPC$WallPaper tLRPC$WallPaper = tLRPC$TL_theme.settings.wallpaper;
                    themeAccent.patternMotion = (tLRPC$WallPaper == null || (tLRPC$WallPaperSettings = tLRPC$WallPaper.settings) == null || !tLRPC$WallPaperSettings.motion) ? false : true;
                    themeInfo.previewParsed = false;
                } else {
                    return;
                }
            } else {
                if (themeInfo != null) {
                    HashMap<String, ThemeInfo> hashMap = themesDict;
                    str = themeInfo.getKey();
                    hashMap.remove(str);
                } else {
                    str = "remote" + tLRPC$TL_theme.id;
                    themeInfo = themesDict.get(str);
                }
                if (themeInfo != null) {
                    themeInfo.info = tLRPC$TL_theme;
                    themeInfo.name = tLRPC$TL_theme.title;
                    File file = new File(themeInfo.pathToFile);
                    File file2 = new File(ApplicationLoader.getFilesDirFixed(), str + ".attheme");
                    if (!file.equals(file2)) {
                        try {
                            AndroidUtilities.copyFile(file, file2);
                            themeInfo.pathToFile = file2.getAbsolutePath();
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                    if (z) {
                        themeInfo.loadThemeDocument();
                    } else {
                        themeInfo.previewParsed = false;
                    }
                    themesDict.put(themeInfo.getKey(), themeInfo);
                } else {
                    return;
                }
            }
            saveOtherThemes(true);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x004a, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x004b, code lost:
        if (r6 != null) goto L_0x004d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
        r6.close();
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:25:0x0050 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.io.File getAssetFile(java.lang.String r6) {
        /*
            java.io.File r0 = new java.io.File
            java.io.File r1 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            r0.<init>(r1, r6)
            r1 = 0
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x001e }
            android.content.res.AssetManager r3 = r3.getAssets()     // Catch:{ Exception -> 0x001e }
            java.io.InputStream r3 = r3.open(r6)     // Catch:{ Exception -> 0x001e }
            int r4 = r3.available()     // Catch:{ Exception -> 0x001e }
            long r4 = (long) r4     // Catch:{ Exception -> 0x001e }
            r3.close()     // Catch:{ Exception -> 0x001e }
            goto L_0x0023
        L_0x001e:
            r3 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            r4 = r1
        L_0x0023:
            boolean r3 = r0.exists()
            if (r3 == 0) goto L_0x0035
            int r3 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r3 == 0) goto L_0x0055
            long r1 = r0.length()
            int r3 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r3 == 0) goto L_0x0055
        L_0x0035:
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0051 }
            android.content.res.AssetManager r1 = r1.getAssets()     // Catch:{ Exception -> 0x0051 }
            java.io.InputStream r6 = r1.open(r6)     // Catch:{ Exception -> 0x0051 }
            org.telegram.messenger.AndroidUtilities.copyFile((java.io.InputStream) r6, (java.io.File) r0)     // Catch:{ all -> 0x0048 }
            if (r6 == 0) goto L_0x0055
            r6.close()     // Catch:{ Exception -> 0x0051 }
            goto L_0x0055
        L_0x0048:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x004a }
        L_0x004a:
            r1 = move-exception
            if (r6 == 0) goto L_0x0050
            r6.close()     // Catch:{ all -> 0x0050 }
        L_0x0050:
            throw r1     // Catch:{ Exception -> 0x0051 }
        L_0x0051:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
        L_0x0055:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.getAssetFile(java.lang.String):java.io.File");
    }

    public static int getPreviewColor(HashMap<String, Integer> hashMap, String str) {
        Integer num = hashMap.get(str);
        if (num == null) {
            num = defaultColors.get(str);
        }
        return num.intValue();
    }

    /* JADX WARNING: Removed duplicated region for block: B:100:0x034a A[SYNTHETIC, Splitter:B:100:0x034a] */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x035e A[SYNTHETIC, Splitter:B:112:0x035e] */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x037e A[Catch:{ all -> 0x036a, all -> 0x053a }] */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x03b6 A[Catch:{ all -> 0x036a, all -> 0x053a }] */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x03e1 A[Catch:{ all -> 0x036a, all -> 0x053a }] */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x03fd A[Catch:{ all -> 0x036a, all -> 0x053a }] */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x046c A[Catch:{ all -> 0x036a, all -> 0x053a }] */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x04ad A[Catch:{ all -> 0x036a, all -> 0x053a }] */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x04d0 A[Catch:{ all -> 0x036a, all -> 0x053a }] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:116:0x0364=Splitter:B:116:0x0364, B:38:0x01bc=Splitter:B:38:0x01bc} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String createThemePreviewImage(java.lang.String r26, java.lang.String r27) {
        /*
            r0 = r26
            r1 = r27
            r2 = 0
            r3 = 1
            java.lang.String[] r4 = new java.lang.String[r3]     // Catch:{ all -> 0x053a }
            java.io.File r5 = new java.io.File     // Catch:{ all -> 0x053a }
            r5.<init>(r0)     // Catch:{ all -> 0x053a }
            java.util.HashMap r5 = getThemeFileValues(r5, r2, r4)     // Catch:{ all -> 0x053a }
            java.lang.String r6 = "wallpaperFileOffset"
            java.lang.Object r6 = r5.get(r6)     // Catch:{ all -> 0x053a }
            java.lang.Integer r6 = (java.lang.Integer) r6     // Catch:{ all -> 0x053a }
            r7 = 560(0x230, float:7.85E-43)
            r8 = 678(0x2a6, float:9.5E-43)
            android.graphics.Bitmap$Config r9 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x053a }
            android.graphics.Bitmap r7 = org.telegram.messenger.Bitmaps.createBitmap(r7, r8, r9)     // Catch:{ all -> 0x053a }
            android.graphics.Canvas r14 = new android.graphics.Canvas     // Catch:{ all -> 0x053a }
            r14.<init>(r7)     // Catch:{ all -> 0x053a }
            android.graphics.Paint r15 = new android.graphics.Paint     // Catch:{ all -> 0x053a }
            r15.<init>()     // Catch:{ all -> 0x053a }
            java.lang.String r8 = "actionBarDefault"
            int r8 = getPreviewColor(r5, r8)     // Catch:{ all -> 0x053a }
            java.lang.String r9 = "actionBarDefaultIcon"
            int r9 = getPreviewColor(r5, r9)     // Catch:{ all -> 0x053a }
            java.lang.String r10 = "chat_messagePanelBackground"
            int r13 = getPreviewColor(r5, r10)     // Catch:{ all -> 0x053a }
            java.lang.String r10 = "chat_messagePanelIcons"
            int r10 = getPreviewColor(r5, r10)     // Catch:{ all -> 0x053a }
            java.lang.String r11 = "chat_inBubble"
            int r11 = getPreviewColor(r5, r11)     // Catch:{ all -> 0x053a }
            java.lang.String r12 = "chat_outBubble"
            int r12 = getPreviewColor(r5, r12)     // Catch:{ all -> 0x053a }
            java.lang.String r2 = "chat_outBubbleGradient"
            java.lang.Object r2 = r5.get(r2)     // Catch:{ all -> 0x053a }
            java.lang.Integer r2 = (java.lang.Integer) r2     // Catch:{ all -> 0x053a }
            java.lang.String r2 = "chat_wallpaper"
            java.lang.Object r2 = r5.get(r2)     // Catch:{ all -> 0x053a }
            java.lang.Integer r2 = (java.lang.Integer) r2     // Catch:{ all -> 0x053a }
            java.lang.String r3 = "chat_serviceBackground"
            java.lang.Object r3 = r5.get(r3)     // Catch:{ all -> 0x053a }
            java.lang.Integer r3 = (java.lang.Integer) r3     // Catch:{ all -> 0x053a }
            r16 = r11
            java.lang.String r11 = "chat_wallpaper_gradient_to"
            java.lang.Object r11 = r5.get(r11)     // Catch:{ all -> 0x053a }
            java.lang.Integer r11 = (java.lang.Integer) r11     // Catch:{ all -> 0x053a }
            android.content.Context r17 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x053a }
            r18 = r12
            android.content.res.Resources r12 = r17.getResources()     // Catch:{ all -> 0x053a }
            r17 = r13
            r13 = 2131165834(0x7var_a, float:1.7945896E38)
            android.graphics.drawable.Drawable r12 = r12.getDrawable(r13)     // Catch:{ all -> 0x053a }
            android.graphics.drawable.Drawable r13 = r12.mutate()     // Catch:{ all -> 0x053a }
            setDrawableColor(r13, r9)     // Catch:{ all -> 0x053a }
            android.content.Context r12 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x053a }
            android.content.res.Resources r12 = r12.getResources()     // Catch:{ all -> 0x053a }
            r19 = r13
            r13 = 2131165836(0x7var_c, float:1.79459E38)
            android.graphics.drawable.Drawable r12 = r12.getDrawable(r13)     // Catch:{ all -> 0x053a }
            android.graphics.drawable.Drawable r13 = r12.mutate()     // Catch:{ all -> 0x053a }
            setDrawableColor(r13, r9)     // Catch:{ all -> 0x053a }
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x053a }
            android.content.res.Resources r9 = r9.getResources()     // Catch:{ all -> 0x053a }
            r12 = 2131165839(0x7var_f, float:1.7945906E38)
            android.graphics.drawable.Drawable r9 = r9.getDrawable(r12)     // Catch:{ all -> 0x053a }
            android.graphics.drawable.Drawable r12 = r9.mutate()     // Catch:{ all -> 0x053a }
            setDrawableColor(r12, r10)     // Catch:{ all -> 0x053a }
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x053a }
            android.content.res.Resources r9 = r9.getResources()     // Catch:{ all -> 0x053a }
            r20 = r12
            r12 = 2131165837(0x7var_d, float:1.7945902E38)
            android.graphics.drawable.Drawable r9 = r9.getDrawable(r12)     // Catch:{ all -> 0x053a }
            android.graphics.drawable.Drawable r12 = r9.mutate()     // Catch:{ all -> 0x053a }
            setDrawableColor(r12, r10)     // Catch:{ all -> 0x053a }
            r10 = 2
            org.telegram.ui.ActionBar.Theme$MessageDrawable[] r9 = new org.telegram.ui.ActionBar.Theme.MessageDrawable[r10]     // Catch:{ all -> 0x053a }
            r21 = r12
            r12 = 0
        L_0x00d2:
            if (r12 >= r10) goto L_0x00ff
            org.telegram.ui.ActionBar.Theme$8 r10 = new org.telegram.ui.ActionBar.Theme$8     // Catch:{ all -> 0x053a }
            r22 = r13
            r13 = 1
            r23 = r8
            r24 = r15
            r8 = 2
            if (r12 != r13) goto L_0x00e2
            r13 = 1
            goto L_0x00e3
        L_0x00e2:
            r13 = 0
        L_0x00e3:
            r15 = 0
            r10.<init>(r8, r13, r15, r5)     // Catch:{ all -> 0x053a }
            r9[r12] = r10     // Catch:{ all -> 0x053a }
            r8 = r9[r12]     // Catch:{ all -> 0x053a }
            if (r12 != 0) goto L_0x00f0
            r10 = r16
            goto L_0x00f2
        L_0x00f0:
            r10 = r18
        L_0x00f2:
            setDrawableColor(r8, r10)     // Catch:{ all -> 0x053a }
            int r12 = r12 + 1
            r13 = r22
            r8 = r23
            r15 = r24
            r10 = 2
            goto L_0x00d2
        L_0x00ff:
            r23 = r8
            r22 = r13
            r24 = r15
            android.graphics.RectF r15 = new android.graphics.RectF     // Catch:{ all -> 0x053a }
            r15.<init>()     // Catch:{ all -> 0x053a }
            r8 = 80
            r10 = 1065353216(0x3var_, float:1.0)
            r12 = 1073741824(0x40000000, float:2.0)
            r13 = 0
            r16 = 1141637120(0x440CLASSNAME, float:560.0)
            r18 = r9
            r9 = 120(0x78, float:1.68E-43)
            if (r1 == 0) goto L_0x01c5
            android.graphics.BitmapFactory$Options r0 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x01b9 }
            r0.<init>()     // Catch:{ all -> 0x01b9 }
            r2 = 1
            r0.inJustDecodeBounds = r2     // Catch:{ all -> 0x01b9 }
            android.graphics.BitmapFactory.decodeFile(r1, r0)     // Catch:{ all -> 0x01b9 }
            int r2 = r0.outWidth     // Catch:{ all -> 0x01b9 }
            if (r2 <= 0) goto L_0x01b7
            int r2 = r0.outHeight     // Catch:{ all -> 0x01b9 }
            if (r2 <= 0) goto L_0x01b7
            int r2 = r0.outWidth     // Catch:{ all -> 0x01b9 }
            float r2 = (float) r2     // Catch:{ all -> 0x01b9 }
            float r2 = r2 / r16
            int r4 = r0.outHeight     // Catch:{ all -> 0x01b9 }
            float r4 = (float) r4     // Catch:{ all -> 0x01b9 }
            float r4 = r4 / r16
            float r2 = java.lang.Math.min(r2, r4)     // Catch:{ all -> 0x01b9 }
            r4 = 1
            r0.inSampleSize = r4     // Catch:{ all -> 0x01b9 }
            int r4 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
            if (r4 <= 0) goto L_0x014d
        L_0x0141:
            int r4 = r0.inSampleSize     // Catch:{ all -> 0x01b9 }
            r5 = 2
            int r4 = r4 * 2
            r0.inSampleSize = r4     // Catch:{ all -> 0x01b9 }
            float r4 = (float) r4     // Catch:{ all -> 0x01b9 }
            int r4 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r4 < 0) goto L_0x0141
        L_0x014d:
            r2 = 0
            r0.inJustDecodeBounds = r2     // Catch:{ all -> 0x01b9 }
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r1, r0)     // Catch:{ all -> 0x01b9 }
            if (r0 == 0) goto L_0x01b7
            android.graphics.Paint r1 = new android.graphics.Paint     // Catch:{ all -> 0x01b9 }
            r1.<init>()     // Catch:{ all -> 0x01b9 }
            r2 = 1
            r1.setFilterBitmap(r2)     // Catch:{ all -> 0x01b9 }
            int r2 = r0.getWidth()     // Catch:{ all -> 0x01b9 }
            float r2 = (float) r2     // Catch:{ all -> 0x01b9 }
            float r2 = r2 / r16
            int r4 = r0.getHeight()     // Catch:{ all -> 0x01b9 }
            float r4 = (float) r4     // Catch:{ all -> 0x01b9 }
            float r4 = r4 / r16
            float r2 = java.lang.Math.min(r2, r4)     // Catch:{ all -> 0x01b9 }
            int r4 = r0.getWidth()     // Catch:{ all -> 0x01b9 }
            float r4 = (float) r4     // Catch:{ all -> 0x01b9 }
            float r4 = r4 / r2
            int r5 = r0.getHeight()     // Catch:{ all -> 0x01b9 }
            float r5 = (float) r5     // Catch:{ all -> 0x01b9 }
            float r5 = r5 / r2
            r15.set(r13, r13, r4, r5)     // Catch:{ all -> 0x01b9 }
            int r2 = r7.getWidth()     // Catch:{ all -> 0x01b9 }
            float r2 = (float) r2     // Catch:{ all -> 0x01b9 }
            float r4 = r15.width()     // Catch:{ all -> 0x01b9 }
            float r2 = r2 - r4
            float r2 = r2 / r12
            int r4 = r7.getHeight()     // Catch:{ all -> 0x01b9 }
            float r4 = (float) r4     // Catch:{ all -> 0x01b9 }
            float r5 = r15.height()     // Catch:{ all -> 0x01b9 }
            float r4 = r4 - r5
            float r4 = r4 / r12
            r15.offset(r2, r4)     // Catch:{ all -> 0x01b9 }
            r2 = 0
            r14.drawBitmap(r0, r2, r15, r1)     // Catch:{ all -> 0x01b9 }
            if (r3 != 0) goto L_0x01b5
            android.graphics.drawable.BitmapDrawable r1 = new android.graphics.drawable.BitmapDrawable     // Catch:{ all -> 0x01b1 }
            r1.<init>(r0)     // Catch:{ all -> 0x01b1 }
            int[] r0 = org.telegram.messenger.AndroidUtilities.calcDrawableColor(r1)     // Catch:{ all -> 0x01b1 }
            r1 = 0
            r0 = r0[r1]     // Catch:{ all -> 0x01b1 }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x01b1 }
            r3 = r0
            goto L_0x01b5
        L_0x01b1:
            r0 = move-exception
            r1 = r0
            r0 = 1
            goto L_0x01bc
        L_0x01b5:
            r0 = 1
            goto L_0x01bf
        L_0x01b7:
            r0 = 0
            goto L_0x01bf
        L_0x01b9:
            r0 = move-exception
            r1 = r0
            r0 = 0
        L_0x01bc:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x053a }
        L_0x01bf:
            r1 = r0
            r0 = 80
        L_0x01c2:
            r10 = 2
            goto L_0x037c
        L_0x01c5:
            if (r2 == 0) goto L_0x0230
            if (r11 != 0) goto L_0x01d3
            android.graphics.drawable.ColorDrawable r0 = new android.graphics.drawable.ColorDrawable     // Catch:{ all -> 0x053a }
            int r1 = r2.intValue()     // Catch:{ all -> 0x053a }
            r0.<init>(r1)     // Catch:{ all -> 0x053a }
            goto L_0x0207
        L_0x01d3:
            java.lang.String r0 = "chat_wallpaper_gradient_rotation"
            java.lang.Object r0 = r5.get(r0)     // Catch:{ all -> 0x053a }
            java.lang.Integer r0 = (java.lang.Integer) r0     // Catch:{ all -> 0x053a }
            if (r0 != 0) goto L_0x01e3
            r0 = 45
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x053a }
        L_0x01e3:
            r1 = 2
            int[] r4 = new int[r1]     // Catch:{ all -> 0x053a }
            int r1 = r2.intValue()     // Catch:{ all -> 0x053a }
            r5 = 0
            r4[r5] = r1     // Catch:{ all -> 0x053a }
            int r1 = r11.intValue()     // Catch:{ all -> 0x053a }
            r5 = 1
            r4[r5] = r1     // Catch:{ all -> 0x053a }
            int r0 = r0.intValue()     // Catch:{ all -> 0x053a }
            int r1 = r7.getWidth()     // Catch:{ all -> 0x053a }
            int r5 = r7.getHeight()     // Catch:{ all -> 0x053a }
            int r5 = r5 - r9
            android.graphics.drawable.BitmapDrawable r0 = org.telegram.ui.Components.BackgroundGradientDrawable.createDitheredGradientBitmapDrawable((int) r0, (int[]) r4, (int) r1, (int) r5)     // Catch:{ all -> 0x053a }
            r8 = 90
        L_0x0207:
            int r1 = r7.getWidth()     // Catch:{ all -> 0x053a }
            int r4 = r7.getHeight()     // Catch:{ all -> 0x053a }
            int r4 = r4 - r9
            r5 = 0
            r0.setBounds(r5, r9, r1, r4)     // Catch:{ all -> 0x053a }
            r0.draw(r14)     // Catch:{ all -> 0x053a }
            if (r3 != 0) goto L_0x022d
            android.graphics.drawable.ColorDrawable r0 = new android.graphics.drawable.ColorDrawable     // Catch:{ all -> 0x053a }
            int r1 = r2.intValue()     // Catch:{ all -> 0x053a }
            r0.<init>(r1)     // Catch:{ all -> 0x053a }
            int[] r0 = org.telegram.messenger.AndroidUtilities.calcDrawableColor(r0)     // Catch:{ all -> 0x053a }
            r1 = 0
            r0 = r0[r1]     // Catch:{ all -> 0x053a }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x053a }
        L_0x022d:
            r0 = r8
            r1 = 1
            goto L_0x01c2
        L_0x0230:
            if (r6 == 0) goto L_0x0238
            int r1 = r6.intValue()     // Catch:{ all -> 0x053a }
            if (r1 >= 0) goto L_0x0241
        L_0x0238:
            r1 = 0
            r2 = r4[r1]     // Catch:{ all -> 0x053a }
            boolean r1 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x053a }
            if (r1 != 0) goto L_0x0378
        L_0x0241:
            android.graphics.BitmapFactory$Options r1 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0355 }
            r1.<init>()     // Catch:{ all -> 0x0355 }
            r2 = 1
            r1.inJustDecodeBounds = r2     // Catch:{ all -> 0x0355 }
            r2 = 0
            r5 = r4[r2]     // Catch:{ all -> 0x0355 }
            boolean r2 = android.text.TextUtils.isEmpty(r5)     // Catch:{ all -> 0x0355 }
            if (r2 != 0) goto L_0x0282
            java.io.File r0 = new java.io.File     // Catch:{ all -> 0x027c }
            java.io.File r2 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ all -> 0x027c }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x027c }
            r5.<init>()     // Catch:{ all -> 0x027c }
            r11 = 0
            r4 = r4[r11]     // Catch:{ all -> 0x027c }
            java.lang.String r4 = org.telegram.messenger.Utilities.MD5(r4)     // Catch:{ all -> 0x027c }
            r5.append(r4)     // Catch:{ all -> 0x027c }
            java.lang.String r4 = ".wp"
            r5.append(r4)     // Catch:{ all -> 0x027c }
            java.lang.String r4 = r5.toString()     // Catch:{ all -> 0x027c }
            r0.<init>(r2, r4)     // Catch:{ all -> 0x027c }
            java.lang.String r2 = r0.getAbsolutePath()     // Catch:{ all -> 0x027c }
            android.graphics.BitmapFactory.decodeFile(r2, r1)     // Catch:{ all -> 0x027c }
            r2 = 0
            goto L_0x0298
        L_0x027c:
            r0 = move-exception
            r1 = 0
            r2 = 0
            r10 = 2
            goto L_0x0359
        L_0x0282:
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ all -> 0x0355 }
            r2.<init>(r0)     // Catch:{ all -> 0x0355 }
            java.nio.channels.FileChannel r0 = r2.getChannel()     // Catch:{ all -> 0x0351 }
            int r4 = r6.intValue()     // Catch:{ all -> 0x0351 }
            long r4 = (long) r4     // Catch:{ all -> 0x0351 }
            r0.position(r4)     // Catch:{ all -> 0x0351 }
            r4 = 0
            android.graphics.BitmapFactory.decodeStream(r2, r4, r1)     // Catch:{ all -> 0x0351 }
            r0 = 0
        L_0x0298:
            int r4 = r1.outWidth     // Catch:{ all -> 0x0351 }
            if (r4 <= 0) goto L_0x0346
            int r4 = r1.outHeight     // Catch:{ all -> 0x0351 }
            if (r4 <= 0) goto L_0x0346
            int r4 = r1.outWidth     // Catch:{ all -> 0x0351 }
            float r4 = (float) r4     // Catch:{ all -> 0x0351 }
            float r4 = r4 / r16
            int r5 = r1.outHeight     // Catch:{ all -> 0x0351 }
            float r5 = (float) r5     // Catch:{ all -> 0x0351 }
            float r5 = r5 / r16
            float r4 = java.lang.Math.min(r4, r5)     // Catch:{ all -> 0x0351 }
            r5 = 1
            r1.inSampleSize = r5     // Catch:{ all -> 0x0351 }
            int r5 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r5 <= 0) goto L_0x02c2
        L_0x02b5:
            int r5 = r1.inSampleSize     // Catch:{ all -> 0x0351 }
            r10 = 2
            int r5 = r5 * 2
            r1.inSampleSize = r5     // Catch:{ all -> 0x0344 }
            float r5 = (float) r5     // Catch:{ all -> 0x0344 }
            int r5 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1))
            if (r5 < 0) goto L_0x02b5
            goto L_0x02c3
        L_0x02c2:
            r10 = 2
        L_0x02c3:
            r4 = 0
            r1.inJustDecodeBounds = r4     // Catch:{ all -> 0x0344 }
            if (r0 == 0) goto L_0x02d1
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ all -> 0x0344 }
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r0, r1)     // Catch:{ all -> 0x0344 }
            goto L_0x02e2
        L_0x02d1:
            java.nio.channels.FileChannel r0 = r2.getChannel()     // Catch:{ all -> 0x0344 }
            int r4 = r6.intValue()     // Catch:{ all -> 0x0344 }
            long r4 = (long) r4     // Catch:{ all -> 0x0344 }
            r0.position(r4)     // Catch:{ all -> 0x0344 }
            r4 = 0
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeStream(r2, r4, r1)     // Catch:{ all -> 0x0344 }
        L_0x02e2:
            if (r0 == 0) goto L_0x0347
            android.graphics.Paint r1 = new android.graphics.Paint     // Catch:{ all -> 0x0344 }
            r1.<init>()     // Catch:{ all -> 0x0344 }
            r4 = 1
            r1.setFilterBitmap(r4)     // Catch:{ all -> 0x0344 }
            int r4 = r0.getWidth()     // Catch:{ all -> 0x0344 }
            float r4 = (float) r4     // Catch:{ all -> 0x0344 }
            float r4 = r4 / r16
            int r5 = r0.getHeight()     // Catch:{ all -> 0x0344 }
            float r5 = (float) r5     // Catch:{ all -> 0x0344 }
            float r5 = r5 / r16
            float r4 = java.lang.Math.min(r4, r5)     // Catch:{ all -> 0x0344 }
            int r5 = r0.getWidth()     // Catch:{ all -> 0x0344 }
            float r5 = (float) r5     // Catch:{ all -> 0x0344 }
            float r5 = r5 / r4
            int r6 = r0.getHeight()     // Catch:{ all -> 0x0344 }
            float r6 = (float) r6     // Catch:{ all -> 0x0344 }
            float r6 = r6 / r4
            r15.set(r13, r13, r5, r6)     // Catch:{ all -> 0x0344 }
            int r4 = r7.getWidth()     // Catch:{ all -> 0x0344 }
            float r4 = (float) r4     // Catch:{ all -> 0x0344 }
            float r5 = r15.width()     // Catch:{ all -> 0x0344 }
            float r4 = r4 - r5
            float r4 = r4 / r12
            int r5 = r7.getHeight()     // Catch:{ all -> 0x0344 }
            float r5 = (float) r5     // Catch:{ all -> 0x0344 }
            float r6 = r15.height()     // Catch:{ all -> 0x0344 }
            float r5 = r5 - r6
            float r5 = r5 / r12
            r15.offset(r4, r5)     // Catch:{ all -> 0x0344 }
            r4 = 0
            r14.drawBitmap(r0, r4, r15, r1)     // Catch:{ all -> 0x0344 }
            if (r3 != 0) goto L_0x0342
            android.graphics.drawable.BitmapDrawable r1 = new android.graphics.drawable.BitmapDrawable     // Catch:{ all -> 0x033f }
            r1.<init>(r0)     // Catch:{ all -> 0x033f }
            int[] r0 = org.telegram.messenger.AndroidUtilities.calcDrawableColor(r1)     // Catch:{ all -> 0x033f }
            r1 = 0
            r0 = r0[r1]     // Catch:{ all -> 0x033f }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x033f }
            r3 = r0
            goto L_0x0342
        L_0x033f:
            r0 = move-exception
            r1 = 1
            goto L_0x0359
        L_0x0342:
            r1 = 1
            goto L_0x0348
        L_0x0344:
            r0 = move-exception
            goto L_0x0353
        L_0x0346:
            r10 = 2
        L_0x0347:
            r1 = 0
        L_0x0348:
            if (r2 == 0) goto L_0x0367
            r2.close()     // Catch:{ Exception -> 0x034e }
            goto L_0x0367
        L_0x034e:
            r0 = move-exception
            r2 = r0
            goto L_0x0364
        L_0x0351:
            r0 = move-exception
            r10 = 2
        L_0x0353:
            r1 = 0
            goto L_0x0359
        L_0x0355:
            r0 = move-exception
            r10 = 2
            r1 = 0
            r2 = 0
        L_0x0359:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x036a }
            if (r2 == 0) goto L_0x0367
            r2.close()     // Catch:{ Exception -> 0x0362 }
            goto L_0x0367
        L_0x0362:
            r0 = move-exception
            r2 = r0
        L_0x0364:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x053a }
        L_0x0367:
            r0 = 80
            goto L_0x037c
        L_0x036a:
            r0 = move-exception
            r1 = r0
            if (r2 == 0) goto L_0x0377
            r2.close()     // Catch:{ Exception -> 0x0372 }
            goto L_0x0377
        L_0x0372:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x053a }
        L_0x0377:
            throw r1     // Catch:{ all -> 0x053a }
        L_0x0378:
            r10 = 2
            r0 = 80
            r1 = 0
        L_0x037c:
            if (r1 != 0) goto L_0x03b6
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x053a }
            android.content.res.Resources r1 = r1.getResources()     // Catch:{ all -> 0x053a }
            r2 = 2131165317(0x7var_, float:1.7944848E38)
            android.graphics.drawable.Drawable r1 = r1.getDrawable(r2)     // Catch:{ all -> 0x053a }
            android.graphics.drawable.Drawable r1 = r1.mutate()     // Catch:{ all -> 0x053a }
            android.graphics.drawable.BitmapDrawable r1 = (android.graphics.drawable.BitmapDrawable) r1     // Catch:{ all -> 0x053a }
            if (r3 != 0) goto L_0x039e
            int[] r2 = org.telegram.messenger.AndroidUtilities.calcDrawableColor(r1)     // Catch:{ all -> 0x053a }
            r3 = 0
            r2 = r2[r3]     // Catch:{ all -> 0x053a }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r2)     // Catch:{ all -> 0x053a }
        L_0x039e:
            android.graphics.Shader$TileMode r2 = android.graphics.Shader.TileMode.REPEAT     // Catch:{ all -> 0x053a }
            android.graphics.Shader$TileMode r4 = android.graphics.Shader.TileMode.REPEAT     // Catch:{ all -> 0x053a }
            r1.setTileModeXY(r2, r4)     // Catch:{ all -> 0x053a }
            int r2 = r7.getWidth()     // Catch:{ all -> 0x053a }
            int r4 = r7.getHeight()     // Catch:{ all -> 0x053a }
            int r4 = r4 - r9
            r5 = 0
            r1.setBounds(r5, r9, r2, r4)     // Catch:{ all -> 0x053a }
            r1.draw(r14)     // Catch:{ all -> 0x053a }
            goto L_0x03b7
        L_0x03b6:
            r5 = 0
        L_0x03b7:
            r2 = r23
            r1 = r24
            r1.setColor(r2)     // Catch:{ all -> 0x053a }
            r2 = 0
            r4 = 0
            int r6 = r7.getWidth()     // Catch:{ all -> 0x053a }
            float r11 = (float) r6     // Catch:{ all -> 0x053a }
            r12 = 1123024896(0x42var_, float:120.0)
            r8 = r14
            r6 = r18
            r16 = 120(0x78, float:1.68E-43)
            r9 = r2
            r2 = 2
            r10 = r4
            r4 = r20
            r5 = r21
            r13 = 0
            r25 = r17
            r2 = r19
            r5 = r22
            r4 = 0
            r13 = r1
            r8.drawRect(r9, r10, r11, r12, r13)     // Catch:{ all -> 0x053a }
            if (r2 == 0) goto L_0x03fb
            r8 = 13
            int r9 = r2.getIntrinsicHeight()     // Catch:{ all -> 0x053a }
            int r9 = 120 - r9
            r10 = 2
            int r9 = r9 / r10
            int r10 = r2.getIntrinsicWidth()     // Catch:{ all -> 0x053a }
            int r10 = r10 + r8
            int r11 = r2.getIntrinsicHeight()     // Catch:{ all -> 0x053a }
            int r11 = r11 + r9
            r2.setBounds(r8, r9, r10, r11)     // Catch:{ all -> 0x053a }
            r2.draw(r14)     // Catch:{ all -> 0x053a }
        L_0x03fb:
            if (r5 == 0) goto L_0x0420
            int r2 = r7.getWidth()     // Catch:{ all -> 0x053a }
            int r8 = r5.getIntrinsicWidth()     // Catch:{ all -> 0x053a }
            int r2 = r2 - r8
            int r2 = r2 + -10
            int r8 = r5.getIntrinsicHeight()     // Catch:{ all -> 0x053a }
            int r9 = 120 - r8
            r8 = 2
            int r9 = r9 / r8
            int r8 = r5.getIntrinsicWidth()     // Catch:{ all -> 0x053a }
            int r8 = r8 + r2
            int r10 = r5.getIntrinsicHeight()     // Catch:{ all -> 0x053a }
            int r10 = r10 + r9
            r5.setBounds(r2, r9, r8, r10)     // Catch:{ all -> 0x053a }
            r5.draw(r14)     // Catch:{ all -> 0x053a }
        L_0x0420:
            r2 = 1
            r5 = r6[r2]     // Catch:{ all -> 0x053a }
            r8 = 216(0xd8, float:3.03E-43)
            int r9 = r7.getWidth()     // Catch:{ all -> 0x053a }
            r10 = 20
            int r9 = r9 - r10
            r11 = 308(0x134, float:4.32E-43)
            r12 = 161(0xa1, float:2.26E-43)
            r5.setBounds(r12, r8, r9, r11)     // Catch:{ all -> 0x053a }
            r5 = r6[r2]     // Catch:{ all -> 0x053a }
            r8 = 522(0x20a, float:7.31E-43)
            r5.setTop(r4, r8, r4, r4)     // Catch:{ all -> 0x053a }
            r5 = r6[r2]     // Catch:{ all -> 0x053a }
            r5.draw(r14)     // Catch:{ all -> 0x053a }
            r5 = r6[r2]     // Catch:{ all -> 0x053a }
            int r9 = r7.getWidth()     // Catch:{ all -> 0x053a }
            int r9 = r9 - r10
            r11 = 430(0x1ae, float:6.03E-43)
            r5.setBounds(r12, r11, r9, r8)     // Catch:{ all -> 0x053a }
            r5 = r6[r2]     // Catch:{ all -> 0x053a }
            r5.setTop(r11, r8, r4, r4)     // Catch:{ all -> 0x053a }
            r2 = r6[r2]     // Catch:{ all -> 0x053a }
            r2.draw(r14)     // Catch:{ all -> 0x053a }
            r2 = r6[r4]     // Catch:{ all -> 0x053a }
            r5 = 399(0x18f, float:5.59E-43)
            r9 = 415(0x19f, float:5.82E-43)
            r11 = 323(0x143, float:4.53E-43)
            r2.setBounds(r10, r11, r5, r9)     // Catch:{ all -> 0x053a }
            r2 = r6[r4]     // Catch:{ all -> 0x053a }
            r2.setTop(r11, r8, r4, r4)     // Catch:{ all -> 0x053a }
            r2 = r6[r4]     // Catch:{ all -> 0x053a }
            r2.draw(r14)     // Catch:{ all -> 0x053a }
            if (r3 == 0) goto L_0x048d
            int r2 = r7.getWidth()     // Catch:{ all -> 0x053a }
            int r2 = r2 + -126
            r4 = 2
            int r2 = r2 / r4
            r4 = 150(0x96, float:2.1E-43)
            float r5 = (float) r2     // Catch:{ all -> 0x053a }
            float r4 = (float) r4     // Catch:{ all -> 0x053a }
            int r2 = r2 + 126
            float r2 = (float) r2     // Catch:{ all -> 0x053a }
            r6 = 192(0xc0, float:2.69E-43)
            float r6 = (float) r6     // Catch:{ all -> 0x053a }
            r15.set(r5, r4, r2, r6)     // Catch:{ all -> 0x053a }
            int r2 = r3.intValue()     // Catch:{ all -> 0x053a }
            r1.setColor(r2)     // Catch:{ all -> 0x053a }
            r2 = 1101529088(0x41a80000, float:21.0)
            r14.drawRoundRect(r15, r2, r2, r1)     // Catch:{ all -> 0x053a }
        L_0x048d:
            r2 = r25
            r1.setColor(r2)     // Catch:{ all -> 0x053a }
            r9 = 0
            int r2 = r7.getHeight()     // Catch:{ all -> 0x053a }
            int r2 = r2 + -120
            float r10 = (float) r2     // Catch:{ all -> 0x053a }
            int r2 = r7.getWidth()     // Catch:{ all -> 0x053a }
            float r11 = (float) r2     // Catch:{ all -> 0x053a }
            int r2 = r7.getHeight()     // Catch:{ all -> 0x053a }
            float r12 = (float) r2     // Catch:{ all -> 0x053a }
            r8 = r14
            r13 = r1
            r8.drawRect(r9, r10, r11, r12, r13)     // Catch:{ all -> 0x053a }
            r1 = 22
            if (r20 == 0) goto L_0x04ce
            int r2 = r7.getHeight()     // Catch:{ all -> 0x053a }
            int r2 = r2 + -120
            int r3 = r20.getIntrinsicHeight()     // Catch:{ all -> 0x053a }
            int r9 = 120 - r3
            r3 = 2
            int r9 = r9 / r3
            int r2 = r2 + r9
            int r3 = r20.getIntrinsicWidth()     // Catch:{ all -> 0x053a }
            int r3 = r3 + r1
            int r4 = r20.getIntrinsicHeight()     // Catch:{ all -> 0x053a }
            int r4 = r4 + r2
            r5 = r20
            r5.setBounds(r1, r2, r3, r4)     // Catch:{ all -> 0x053a }
            r5.draw(r14)     // Catch:{ all -> 0x053a }
        L_0x04ce:
            if (r21 == 0) goto L_0x04fb
            int r2 = r7.getWidth()     // Catch:{ all -> 0x053a }
            int r3 = r21.getIntrinsicWidth()     // Catch:{ all -> 0x053a }
            int r2 = r2 - r3
            int r2 = r2 - r1
            int r1 = r7.getHeight()     // Catch:{ all -> 0x053a }
            int r1 = r1 + -120
            int r3 = r21.getIntrinsicHeight()     // Catch:{ all -> 0x053a }
            int r9 = 120 - r3
            r3 = 2
            int r9 = r9 / r3
            int r1 = r1 + r9
            int r3 = r21.getIntrinsicWidth()     // Catch:{ all -> 0x053a }
            int r3 = r3 + r2
            int r4 = r21.getIntrinsicHeight()     // Catch:{ all -> 0x053a }
            int r4 = r4 + r1
            r5 = r21
            r5.setBounds(r2, r1, r3, r4)     // Catch:{ all -> 0x053a }
            r5.draw(r14)     // Catch:{ all -> 0x053a }
        L_0x04fb:
            r1 = 0
            r14.setBitmap(r1)     // Catch:{ all -> 0x053a }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x053a }
            r1.<init>()     // Catch:{ all -> 0x053a }
            java.lang.String r2 = "-2147483648_"
            r1.append(r2)     // Catch:{ all -> 0x053a }
            int r2 = org.telegram.messenger.SharedConfig.getLastLocalId()     // Catch:{ all -> 0x053a }
            r1.append(r2)     // Catch:{ all -> 0x053a }
            java.lang.String r2 = ".jpg"
            r1.append(r2)     // Catch:{ all -> 0x053a }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x053a }
            java.io.File r2 = new java.io.File     // Catch:{ all -> 0x053a }
            r3 = 4
            java.io.File r3 = org.telegram.messenger.FileLoader.getDirectory(r3)     // Catch:{ all -> 0x053a }
            r2.<init>(r3, r1)     // Catch:{ all -> 0x053a }
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch:{ all -> 0x0535 }
            r1.<init>(r2)     // Catch:{ all -> 0x0535 }
            android.graphics.Bitmap$CompressFormat r3 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ all -> 0x0535 }
            r7.compress(r3, r0, r1)     // Catch:{ all -> 0x0535 }
            org.telegram.messenger.SharedConfig.saveConfig()     // Catch:{ all -> 0x0535 }
            java.lang.String r0 = r2.getAbsolutePath()     // Catch:{ all -> 0x0535 }
            return r0
        L_0x0535:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x053a }
            goto L_0x053e
        L_0x053a:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x053e:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.createThemePreviewImage(java.lang.String, java.lang.String):java.lang.String");
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(2:34|35) */
    /* JADX WARNING: Code restructure failed: missing block: B:35:?, code lost:
        r4 = org.telegram.messenger.Utilities.parseInt(r13).intValue();
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:34:0x0081 */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00ca A[SYNTHETIC, Splitter:B:55:0x00ca] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.HashMap<java.lang.String, java.lang.Integer> getThemeFileValues(java.io.File r16, java.lang.String r17, java.lang.String[] r18) {
        /*
            r0 = r18
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            r2 = 1024(0x400, float:1.435E-42)
            r3 = 0
            byte[] r2 = new byte[r2]     // Catch:{ all -> 0x00c4 }
            if (r17 == 0) goto L_0x0013
            java.io.File r4 = getAssetFile(r17)     // Catch:{ all -> 0x00c4 }
            goto L_0x0015
        L_0x0013:
            r4 = r16
        L_0x0015:
            java.io.FileInputStream r5 = new java.io.FileInputStream     // Catch:{ all -> 0x00c4 }
            r5.<init>(r4)     // Catch:{ all -> 0x00c4 }
            r3 = -1
            r4 = 0
            r6 = 0
            r7 = -1
            r8 = 0
        L_0x001f:
            int r9 = r5.read(r2)     // Catch:{ all -> 0x00c1 }
            if (r9 == r3) goto L_0x00b3
            r12 = r6
            r10 = 0
            r11 = 0
        L_0x0028:
            r13 = 1
            if (r10 >= r9) goto L_0x00a0
            byte r14 = r2[r10]     // Catch:{ all -> 0x00c1 }
            r15 = 10
            if (r14 != r15) goto L_0x009b
            int r14 = r10 - r11
            int r14 = r14 + r13
            java.lang.String r15 = new java.lang.String     // Catch:{ all -> 0x00c1 }
            int r13 = r14 + -1
            r15.<init>(r2, r11, r13)     // Catch:{ all -> 0x00c1 }
            java.lang.String r13 = "WLS="
            boolean r13 = r15.startsWith(r13)     // Catch:{ all -> 0x00c1 }
            if (r13 == 0) goto L_0x0050
            if (r0 == 0) goto L_0x0099
            int r13 = r0.length     // Catch:{ all -> 0x00c1 }
            if (r13 <= 0) goto L_0x0099
            r13 = 4
            java.lang.String r13 = r15.substring(r13)     // Catch:{ all -> 0x00c1 }
            r0[r4] = r13     // Catch:{ all -> 0x00c1 }
            goto L_0x0099
        L_0x0050:
            java.lang.String r13 = "WPS"
            boolean r13 = r15.startsWith(r13)     // Catch:{ all -> 0x00c1 }
            if (r13 == 0) goto L_0x005c
            int r14 = r14 + r12
            r7 = r14
            r8 = 1
            goto L_0x00a0
        L_0x005c:
            r13 = 61
            int r13 = r15.indexOf(r13)     // Catch:{ all -> 0x00c1 }
            if (r13 == r3) goto L_0x0099
            java.lang.String r3 = r15.substring(r4, r13)     // Catch:{ all -> 0x00c1 }
            int r13 = r13 + 1
            java.lang.String r13 = r15.substring(r13)     // Catch:{ all -> 0x00c1 }
            int r15 = r13.length()     // Catch:{ all -> 0x00c1 }
            if (r15 <= 0) goto L_0x008a
            char r15 = r13.charAt(r4)     // Catch:{ all -> 0x00c1 }
            r4 = 35
            if (r15 != r4) goto L_0x008a
            int r4 = android.graphics.Color.parseColor(r13)     // Catch:{ Exception -> 0x0081 }
            goto L_0x0092
        L_0x0081:
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r13)     // Catch:{ all -> 0x00c1 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x00c1 }
            goto L_0x0092
        L_0x008a:
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r13)     // Catch:{ all -> 0x00c1 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x00c1 }
        L_0x0092:
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x00c1 }
            r1.put(r3, r4)     // Catch:{ all -> 0x00c1 }
        L_0x0099:
            int r11 = r11 + r14
            int r12 = r12 + r14
        L_0x009b:
            int r10 = r10 + 1
            r3 = -1
            r4 = 0
            goto L_0x0028
        L_0x00a0:
            if (r6 != r12) goto L_0x00a3
            goto L_0x00b3
        L_0x00a3:
            java.nio.channels.FileChannel r3 = r5.getChannel()     // Catch:{ all -> 0x00c1 }
            long r9 = (long) r12     // Catch:{ all -> 0x00c1 }
            r3.position(r9)     // Catch:{ all -> 0x00c1 }
            if (r8 == 0) goto L_0x00ae
            goto L_0x00b3
        L_0x00ae:
            r6 = r12
            r3 = -1
            r4 = 0
            goto L_0x001f
        L_0x00b3:
            java.lang.String r0 = "wallpaperFileOffset"
            java.lang.Integer r2 = java.lang.Integer.valueOf(r7)     // Catch:{ all -> 0x00c1 }
            r1.put(r0, r2)     // Catch:{ all -> 0x00c1 }
            r5.close()     // Catch:{ Exception -> 0x00ce }
            goto L_0x00d3
        L_0x00c1:
            r0 = move-exception
            r3 = r5
            goto L_0x00c5
        L_0x00c4:
            r0 = move-exception
        L_0x00c5:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x00d4 }
            if (r3 == 0) goto L_0x00d3
            r3.close()     // Catch:{ Exception -> 0x00ce }
            goto L_0x00d3
        L_0x00ce:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x00d3:
            return r1
        L_0x00d4:
            r0 = move-exception
            r1 = r0
            if (r3 == 0) goto L_0x00e1
            r3.close()     // Catch:{ Exception -> 0x00dc }
            goto L_0x00e1
        L_0x00dc:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x00e1:
            goto L_0x00e3
        L_0x00e2:
            throw r1
        L_0x00e3:
            goto L_0x00e2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.getThemeFileValues(java.io.File, java.lang.String, java.lang.String[]):java.util.HashMap");
    }

    public static void createCommonResources(Context context) {
        if (dividerPaint == null) {
            Paint paint = new Paint();
            dividerPaint = paint;
            paint.setStrokeWidth(1.0f);
            avatar_backgroundPaint = new Paint(1);
            Paint paint2 = new Paint(1);
            checkboxSquare_checkPaint = paint2;
            paint2.setStyle(Paint.Style.STROKE);
            checkboxSquare_checkPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            checkboxSquare_checkPaint.setStrokeCap(Paint.Cap.ROUND);
            Paint paint3 = new Paint(1);
            checkboxSquare_eraserPaint = paint3;
            paint3.setColor(0);
            checkboxSquare_eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            checkboxSquare_backgroundPaint = new Paint(1);
            linkSelectionPaint = new Paint();
            Resources resources = context.getResources();
            avatarDrawables[0] = resources.getDrawable(NUM);
            avatarDrawables[1] = resources.getDrawable(NUM);
            avatarDrawables[2] = resources.getDrawable(NUM);
            avatarDrawables[3] = resources.getDrawable(NUM);
            avatarDrawables[4] = resources.getDrawable(NUM);
            avatarDrawables[5] = resources.getDrawable(NUM);
            avatarDrawables[6] = resources.getDrawable(NUM);
            avatarDrawables[7] = resources.getDrawable(NUM);
            avatarDrawables[8] = resources.getDrawable(NUM);
            avatarDrawables[9] = resources.getDrawable(NUM);
            RLottieDrawable rLottieDrawable = dialogs_archiveAvatarDrawable;
            if (rLottieDrawable != null) {
                rLottieDrawable.setCallback((Drawable.Callback) null);
                dialogs_archiveAvatarDrawable.recycle();
            }
            RLottieDrawable rLottieDrawable2 = dialogs_archiveDrawable;
            if (rLottieDrawable2 != null) {
                rLottieDrawable2.recycle();
            }
            RLottieDrawable rLottieDrawable3 = dialogs_unarchiveDrawable;
            if (rLottieDrawable3 != null) {
                rLottieDrawable3.recycle();
            }
            RLottieDrawable rLottieDrawable4 = dialogs_pinArchiveDrawable;
            if (rLottieDrawable4 != null) {
                rLottieDrawable4.recycle();
            }
            RLottieDrawable rLottieDrawable5 = dialogs_unpinArchiveDrawable;
            if (rLottieDrawable5 != null) {
                rLottieDrawable5.recycle();
            }
            dialogs_archiveAvatarDrawable = new RLottieDrawable(NUM, "chats_archiveavatar", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f), false, (int[]) null);
            dialogs_archiveDrawable = new RLottieDrawable(NUM, "chats_archive", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            dialogs_unarchiveDrawable = new RLottieDrawable(NUM, "chats_unarchive", AndroidUtilities.dp((float) AndroidUtilities.dp(36.0f)), AndroidUtilities.dp(36.0f));
            dialogs_pinArchiveDrawable = new RLottieDrawable(NUM, "chats_hide", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            dialogs_unpinArchiveDrawable = new RLottieDrawable(NUM, "chats_unhide", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            applyCommonTheme();
        }
    }

    public static void applyCommonTheme() {
        Paint paint = dividerPaint;
        if (paint != null) {
            paint.setColor(getColor("divider"));
            linkSelectionPaint.setColor(getColor("windowBackgroundWhiteLinkSelection"));
            int i = 0;
            while (true) {
                Drawable[] drawableArr = avatarDrawables;
                if (i < drawableArr.length) {
                    setDrawableColorByKey(drawableArr[i], "avatar_text");
                    i++;
                } else {
                    dialogs_archiveAvatarDrawable.beginApplyLayerColors();
                    dialogs_archiveAvatarDrawable.setLayerColor("Arrow1.**", getNonAnimatedColor("avatar_backgroundArchived"));
                    dialogs_archiveAvatarDrawable.setLayerColor("Arrow2.**", getNonAnimatedColor("avatar_backgroundArchived"));
                    dialogs_archiveAvatarDrawable.setLayerColor("Box2.**", getNonAnimatedColor("avatar_text"));
                    dialogs_archiveAvatarDrawable.setLayerColor("Box1.**", getNonAnimatedColor("avatar_text"));
                    dialogs_archiveAvatarDrawable.commitApplyLayerColors();
                    dialogs_archiveAvatarDrawableRecolored = false;
                    dialogs_archiveAvatarDrawable.setAllowDecodeSingleFrame(true);
                    dialogs_pinArchiveDrawable.beginApplyLayerColors();
                    dialogs_pinArchiveDrawable.setLayerColor("Arrow.**", getNonAnimatedColor("chats_archiveIcon"));
                    dialogs_pinArchiveDrawable.setLayerColor("Line.**", getNonAnimatedColor("chats_archiveIcon"));
                    dialogs_pinArchiveDrawable.commitApplyLayerColors();
                    dialogs_unpinArchiveDrawable.beginApplyLayerColors();
                    dialogs_unpinArchiveDrawable.setLayerColor("Arrow.**", getNonAnimatedColor("chats_archiveIcon"));
                    dialogs_unpinArchiveDrawable.setLayerColor("Line.**", getNonAnimatedColor("chats_archiveIcon"));
                    dialogs_unpinArchiveDrawable.commitApplyLayerColors();
                    dialogs_archiveDrawable.beginApplyLayerColors();
                    dialogs_archiveDrawable.setLayerColor("Arrow.**", getNonAnimatedColor("chats_archiveBackground"));
                    dialogs_archiveDrawable.setLayerColor("Box2.**", getNonAnimatedColor("chats_archiveIcon"));
                    dialogs_archiveDrawable.setLayerColor("Box1.**", getNonAnimatedColor("chats_archiveIcon"));
                    dialogs_archiveDrawable.commitApplyLayerColors();
                    dialogs_archiveDrawableRecolored = false;
                    dialogs_unarchiveDrawable.beginApplyLayerColors();
                    dialogs_unarchiveDrawable.setLayerColor("Arrow1.**", getNonAnimatedColor("chats_archiveIcon"));
                    dialogs_unarchiveDrawable.setLayerColor("Arrow2.**", getNonAnimatedColor("chats_archivePinBackground"));
                    dialogs_unarchiveDrawable.setLayerColor("Box2.**", getNonAnimatedColor("chats_archiveIcon"));
                    dialogs_unarchiveDrawable.setLayerColor("Box1.**", getNonAnimatedColor("chats_archiveIcon"));
                    dialogs_unarchiveDrawable.commitApplyLayerColors();
                    return;
                }
            }
        }
    }

    public static void createDialogsResources(Context context) {
        createCommonResources(context);
        if (dialogs_namePaint == null) {
            Resources resources = context.getResources();
            dialogs_namePaint = new TextPaint[2];
            dialogs_nameEncryptedPaint = new TextPaint[2];
            dialogs_messagePaint = new TextPaint[2];
            dialogs_messagePrintingPaint = new TextPaint[2];
            for (int i = 0; i < 2; i++) {
                dialogs_namePaint[i] = new TextPaint(1);
                dialogs_namePaint[i].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                dialogs_nameEncryptedPaint[i] = new TextPaint(1);
                dialogs_nameEncryptedPaint[i].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                dialogs_messagePaint[i] = new TextPaint(1);
                dialogs_messagePrintingPaint[i] = new TextPaint(1);
            }
            TextPaint textPaint = new TextPaint(1);
            dialogs_searchNamePaint = textPaint;
            textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            TextPaint textPaint2 = new TextPaint(1);
            dialogs_searchNameEncryptedPaint = textPaint2;
            textPaint2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            TextPaint textPaint3 = new TextPaint(1);
            dialogs_messageNamePaint = textPaint3;
            textPaint3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            dialogs_timePaint = new TextPaint(1);
            TextPaint textPaint4 = new TextPaint(1);
            dialogs_countTextPaint = textPaint4;
            textPaint4.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            TextPaint textPaint5 = new TextPaint(1);
            dialogs_archiveTextPaint = textPaint5;
            textPaint5.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            dialogs_onlinePaint = new TextPaint(1);
            dialogs_offlinePaint = new TextPaint(1);
            dialogs_tabletSeletedPaint = new Paint();
            dialogs_pinnedPaint = new Paint(1);
            dialogs_onlineCirclePaint = new Paint(1);
            dialogs_countPaint = new Paint(1);
            dialogs_countGrayPaint = new Paint(1);
            dialogs_errorPaint = new Paint(1);
            dialogs_lockDrawable = resources.getDrawable(NUM);
            dialogs_checkDrawable = resources.getDrawable(NUM).mutate();
            dialogs_checkReadDrawable = resources.getDrawable(NUM).mutate();
            dialogs_halfCheckDrawable = resources.getDrawable(NUM);
            dialogs_clockDrawable = resources.getDrawable(NUM).mutate();
            dialogs_errorDrawable = resources.getDrawable(NUM);
            dialogs_reorderDrawable = resources.getDrawable(NUM).mutate();
            dialogs_groupDrawable = resources.getDrawable(NUM);
            dialogs_broadcastDrawable = resources.getDrawable(NUM);
            dialogs_muteDrawable = resources.getDrawable(NUM).mutate();
            dialogs_verifiedDrawable = resources.getDrawable(NUM);
            dialogs_scamDrawable = new ScamDrawable(11);
            dialogs_verifiedCheckDrawable = resources.getDrawable(NUM);
            dialogs_mentionDrawable = resources.getDrawable(NUM);
            dialogs_botDrawable = resources.getDrawable(NUM);
            dialogs_pinnedDrawable = resources.getDrawable(NUM);
            moveUpDrawable = resources.getDrawable(NUM);
            applyDialogsTheme();
        }
        dialogs_messageNamePaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        dialogs_timePaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        dialogs_countTextPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        dialogs_archiveTextPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        dialogs_onlinePaint.setTextSize((float) AndroidUtilities.dp(15.0f));
        dialogs_offlinePaint.setTextSize((float) AndroidUtilities.dp(15.0f));
        dialogs_searchNamePaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        dialogs_searchNameEncryptedPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
    }

    public static void applyDialogsTheme() {
        if (dialogs_namePaint != null) {
            for (int i = 0; i < 2; i++) {
                dialogs_namePaint[i].setColor(getColor("chats_name"));
                dialogs_nameEncryptedPaint[i].setColor(getColor("chats_secretName"));
                TextPaint[] textPaintArr = dialogs_messagePaint;
                TextPaint textPaint = textPaintArr[i];
                TextPaint textPaint2 = textPaintArr[i];
                int color = getColor("chats_message");
                textPaint2.linkColor = color;
                textPaint.setColor(color);
                dialogs_messagePrintingPaint[i].setColor(getColor("chats_actionMessage"));
            }
            dialogs_searchNamePaint.setColor(getColor("chats_name"));
            dialogs_searchNameEncryptedPaint.setColor(getColor("chats_secretName"));
            TextPaint textPaint3 = dialogs_messageNamePaint;
            int color2 = getColor("chats_nameMessage_threeLines");
            textPaint3.linkColor = color2;
            textPaint3.setColor(color2);
            dialogs_tabletSeletedPaint.setColor(getColor("chats_tabletSelectedOverlay"));
            dialogs_pinnedPaint.setColor(getColor("chats_pinnedOverlay"));
            dialogs_timePaint.setColor(getColor("chats_date"));
            dialogs_countTextPaint.setColor(getColor("chats_unreadCounterText"));
            dialogs_archiveTextPaint.setColor(getColor("chats_archiveText"));
            dialogs_countPaint.setColor(getColor("chats_unreadCounter"));
            dialogs_countGrayPaint.setColor(getColor("chats_unreadCounterMuted"));
            dialogs_errorPaint.setColor(getColor("chats_sentError"));
            dialogs_onlinePaint.setColor(getColor("windowBackgroundWhiteBlueText3"));
            dialogs_offlinePaint.setColor(getColor("windowBackgroundWhiteGrayText3"));
            setDrawableColorByKey(dialogs_lockDrawable, "chats_secretIcon");
            setDrawableColorByKey(dialogs_checkDrawable, "chats_sentCheck");
            setDrawableColorByKey(dialogs_checkReadDrawable, "chats_sentReadCheck");
            setDrawableColorByKey(dialogs_halfCheckDrawable, "chats_sentReadCheck");
            setDrawableColorByKey(dialogs_clockDrawable, "chats_sentClock");
            setDrawableColorByKey(dialogs_errorDrawable, "chats_sentErrorIcon");
            setDrawableColorByKey(dialogs_groupDrawable, "chats_nameIcon");
            setDrawableColorByKey(dialogs_broadcastDrawable, "chats_nameIcon");
            setDrawableColorByKey(dialogs_botDrawable, "chats_nameIcon");
            setDrawableColorByKey(dialogs_pinnedDrawable, "chats_pinnedIcon");
            setDrawableColorByKey(dialogs_reorderDrawable, "chats_pinnedIcon");
            setDrawableColorByKey(dialogs_muteDrawable, "chats_muteIcon");
            setDrawableColorByKey(dialogs_mentionDrawable, "chats_mentionIcon");
            setDrawableColorByKey(dialogs_verifiedDrawable, "chats_verifiedBackground");
            setDrawableColorByKey(dialogs_verifiedCheckDrawable, "chats_verifiedCheck");
            setDrawableColorByKey(dialogs_holidayDrawable, "actionBarDefaultTitle");
            setDrawableColorByKey(dialogs_scamDrawable, "chats_draft");
        }
    }

    public static void destroyResources() {
        int i = 0;
        while (true) {
            Drawable[] drawableArr = chat_attachButtonDrawables;
            if (i < drawableArr.length) {
                if (drawableArr[i] != null) {
                    drawableArr[i].setCallback((Drawable.Callback) null);
                }
                i++;
            } else {
                return;
            }
        }
    }

    public static void reloadAllResources(Context context) {
        destroyResources();
        if (chat_msgInDrawable != null) {
            chat_msgInDrawable = null;
            currentColor = 0;
            currentSelectedColor = 0;
            createChatResources(context, false);
        }
        if (dialogs_namePaint != null) {
            dialogs_namePaint = null;
            createDialogsResources(context);
        }
        if (profile_verifiedDrawable != null) {
            profile_verifiedDrawable = null;
            createProfileResources(context);
        }
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(14:11|(1:13)|40|14|15|16|(4:18|(1:20)(1:21)|22|23)|41|24|25|26|27|28|29) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:27:0x0b4e */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void createChatResources(android.content.Context r17, boolean r18) {
        /*
            java.lang.Object r1 = sync
            monitor-enter(r1)
            android.text.TextPaint r0 = chat_msgTextPaint     // Catch:{ all -> 0x0cde }
            r2 = 1
            if (r0 != 0) goto L_0x003b
            android.text.TextPaint r0 = new android.text.TextPaint     // Catch:{ all -> 0x0cde }
            r0.<init>(r2)     // Catch:{ all -> 0x0cde }
            chat_msgTextPaint = r0     // Catch:{ all -> 0x0cde }
            android.text.TextPaint r0 = new android.text.TextPaint     // Catch:{ all -> 0x0cde }
            r0.<init>(r2)     // Catch:{ all -> 0x0cde }
            chat_msgGameTextPaint = r0     // Catch:{ all -> 0x0cde }
            android.text.TextPaint r0 = new android.text.TextPaint     // Catch:{ all -> 0x0cde }
            r0.<init>(r2)     // Catch:{ all -> 0x0cde }
            chat_msgTextPaintOneEmoji = r0     // Catch:{ all -> 0x0cde }
            android.text.TextPaint r0 = new android.text.TextPaint     // Catch:{ all -> 0x0cde }
            r0.<init>(r2)     // Catch:{ all -> 0x0cde }
            chat_msgTextPaintTwoEmoji = r0     // Catch:{ all -> 0x0cde }
            android.text.TextPaint r0 = new android.text.TextPaint     // Catch:{ all -> 0x0cde }
            r0.<init>(r2)     // Catch:{ all -> 0x0cde }
            chat_msgTextPaintThreeEmoji = r0     // Catch:{ all -> 0x0cde }
            android.text.TextPaint r0 = new android.text.TextPaint     // Catch:{ all -> 0x0cde }
            r0.<init>(r2)     // Catch:{ all -> 0x0cde }
            chat_msgBotButtonPaint = r0     // Catch:{ all -> 0x0cde }
            java.lang.String r3 = "fonts/rmedium.ttf"
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3)     // Catch:{ all -> 0x0cde }
            r0.setTypeface(r3)     // Catch:{ all -> 0x0cde }
        L_0x003b:
            monitor-exit(r1)     // Catch:{ all -> 0x0cde }
            r0 = 1077936128(0x40400000, float:3.0)
            r1 = 1096810496(0x41600000, float:14.0)
            r3 = 2
            if (r18 != 0) goto L_0x0b58
            org.telegram.ui.ActionBar.Theme$MessageDrawable r4 = chat_msgInDrawable
            if (r4 != 0) goto L_0x0b58
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_infoPaint = r4
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_docNamePaint = r4
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r4.setTypeface(r5)
            android.graphics.Paint r4 = new android.graphics.Paint
            r4.<init>(r2)
            chat_docBackPaint = r4
            android.graphics.Paint r4 = new android.graphics.Paint
            r4.<init>(r2)
            chat_deleteProgressPaint = r4
            android.graphics.Paint r4 = new android.graphics.Paint
            r4.<init>(r2)
            chat_botProgressPaint = r4
            android.graphics.Paint$Cap r5 = android.graphics.Paint.Cap.ROUND
            r4.setStrokeCap(r5)
            android.graphics.Paint r4 = chat_botProgressPaint
            android.graphics.Paint$Style r5 = android.graphics.Paint.Style.STROKE
            r4.setStyle(r5)
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_locationTitlePaint = r4
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r4.setTypeface(r5)
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_locationAddressPaint = r4
            android.graphics.Paint r4 = new android.graphics.Paint
            r4.<init>()
            chat_urlPaint = r4
            android.graphics.Paint r4 = new android.graphics.Paint
            r4.<init>()
            chat_textSearchSelectionPaint = r4
            android.graphics.Paint r4 = new android.graphics.Paint
            r4.<init>(r2)
            chat_radialProgressPaint = r4
            android.graphics.Paint$Cap r5 = android.graphics.Paint.Cap.ROUND
            r4.setStrokeCap(r5)
            android.graphics.Paint r4 = chat_radialProgressPaint
            android.graphics.Paint$Style r5 = android.graphics.Paint.Style.STROKE
            r4.setStyle(r5)
            android.graphics.Paint r4 = chat_radialProgressPaint
            r5 = -1610612737(0xffffffff9fffffff, float:-1.0842021E-19)
            r4.setColor(r5)
            android.graphics.Paint r4 = new android.graphics.Paint
            r4.<init>(r2)
            chat_radialProgress2Paint = r4
            android.graphics.Paint$Cap r5 = android.graphics.Paint.Cap.ROUND
            r4.setStrokeCap(r5)
            android.graphics.Paint r4 = chat_radialProgress2Paint
            android.graphics.Paint$Style r5 = android.graphics.Paint.Style.STROKE
            r4.setStyle(r5)
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_audioTimePaint = r4
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_livePaint = r4
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r4.setTypeface(r5)
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_audioTitlePaint = r4
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r4.setTypeface(r5)
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_audioPerformerPaint = r4
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_botButtonPaint = r4
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r4.setTypeface(r5)
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_contactNamePaint = r4
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r4.setTypeface(r5)
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_contactPhonePaint = r4
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_durationPaint = r4
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_gamePaint = r4
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r4.setTypeface(r5)
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_shipmentPaint = r4
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_timePaint = r4
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_adminPaint = r4
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_namePaint = r4
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r4.setTypeface(r5)
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_forwardNamePaint = r4
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_replyNamePaint = r4
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r4.setTypeface(r5)
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_replyTextPaint = r4
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_instantViewPaint = r4
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r4.setTypeface(r5)
            android.graphics.Paint r4 = new android.graphics.Paint
            r4.<init>(r2)
            chat_instantViewRectPaint = r4
            android.graphics.Paint$Style r5 = android.graphics.Paint.Style.STROKE
            r4.setStyle(r5)
            android.graphics.Paint r4 = new android.graphics.Paint
            r4.<init>(r2)
            chat_replyLinePaint = r4
            android.graphics.Paint r4 = new android.graphics.Paint
            r4.<init>(r2)
            chat_msgErrorPaint = r4
            android.graphics.Paint r4 = new android.graphics.Paint
            r4.<init>(r2)
            chat_statusPaint = r4
            android.graphics.Paint r4 = new android.graphics.Paint
            r4.<init>(r2)
            chat_statusRecordPaint = r4
            android.graphics.Paint$Style r5 = android.graphics.Paint.Style.STROKE
            r4.setStyle(r5)
            android.graphics.Paint r4 = chat_statusRecordPaint
            android.graphics.Paint$Cap r5 = android.graphics.Paint.Cap.ROUND
            r4.setStrokeCap(r5)
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_actionTextPaint = r4
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r4.setTypeface(r5)
            android.graphics.Paint r4 = new android.graphics.Paint
            r4.<init>(r2)
            chat_actionBackgroundPaint = r4
            android.graphics.Paint r4 = new android.graphics.Paint
            r4.<init>(r2)
            chat_timeBackgroundPaint = r4
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_contextResult_titleTextPaint = r4
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r4.setTypeface(r5)
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_contextResult_descriptionTextPaint = r4
            android.graphics.Paint r4 = new android.graphics.Paint
            r4.<init>()
            chat_composeBackgroundPaint = r4
            android.content.res.Resources r4 = r17.getResources()
            r5 = 2131165960(0x7var_, float:1.7946152E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_msgNoSoundDrawable = r5
            org.telegram.ui.ActionBar.Theme$MessageDrawable r5 = new org.telegram.ui.ActionBar.Theme$MessageDrawable
            r6 = 0
            r5.<init>(r6, r6, r6)
            chat_msgInDrawable = r5
            org.telegram.ui.ActionBar.Theme$MessageDrawable r5 = new org.telegram.ui.ActionBar.Theme$MessageDrawable
            r5.<init>(r6, r6, r2)
            chat_msgInSelectedDrawable = r5
            org.telegram.ui.ActionBar.Theme$MessageDrawable r5 = new org.telegram.ui.ActionBar.Theme$MessageDrawable
            r5.<init>(r6, r2, r6)
            chat_msgOutDrawable = r5
            org.telegram.ui.ActionBar.Theme$MessageDrawable r5 = new org.telegram.ui.ActionBar.Theme$MessageDrawable
            r5.<init>(r6, r2, r2)
            chat_msgOutSelectedDrawable = r5
            org.telegram.ui.ActionBar.Theme$MessageDrawable r5 = new org.telegram.ui.ActionBar.Theme$MessageDrawable
            r5.<init>(r2, r6, r6)
            chat_msgInMediaDrawable = r5
            org.telegram.ui.ActionBar.Theme$MessageDrawable r5 = new org.telegram.ui.ActionBar.Theme$MessageDrawable
            r5.<init>(r2, r6, r2)
            chat_msgInMediaSelectedDrawable = r5
            org.telegram.ui.ActionBar.Theme$MessageDrawable r5 = new org.telegram.ui.ActionBar.Theme$MessageDrawable
            r5.<init>(r2, r2, r6)
            chat_msgOutMediaDrawable = r5
            org.telegram.ui.ActionBar.Theme$MessageDrawable r5 = new org.telegram.ui.ActionBar.Theme$MessageDrawable
            r5.<init>(r2, r2, r2)
            chat_msgOutMediaSelectedDrawable = r5
            r5 = 2131165664(0x7var_e0, float:1.7945551E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgOutCheckDrawable = r5
            r5 = 2131165664(0x7var_e0, float:1.7945551E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgOutCheckSelectedDrawable = r5
            r5 = 2131165664(0x7var_e0, float:1.7945551E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgOutCheckReadDrawable = r5
            r5 = 2131165664(0x7var_e0, float:1.7945551E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgOutCheckReadSelectedDrawable = r5
            r5 = 2131165665(0x7var_e1, float:1.7945554E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgMediaCheckDrawable = r5
            r5 = 2131165665(0x7var_e1, float:1.7945554E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgStickerCheckDrawable = r5
            r5 = 2131165679(0x7var_ef, float:1.7945582E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgOutHalfCheckDrawable = r5
            r5 = 2131165679(0x7var_ef, float:1.7945582E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgOutHalfCheckSelectedDrawable = r5
            r5 = 2131165680(0x7var_f0, float:1.7945584E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgMediaHalfCheckDrawable = r5
            r5 = 2131165680(0x7var_f0, float:1.7945584E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgStickerHalfCheckDrawable = r5
            r5 = 2131165667(0x7var_e3, float:1.7945558E38)
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            chat_msgOutClockDrawable = r7
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            chat_msgOutSelectedClockDrawable = r7
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            chat_msgInClockDrawable = r7
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            chat_msgInSelectedClockDrawable = r7
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            chat_msgMediaClockDrawable = r7
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgStickerClockDrawable = r5
            r5 = 2131165731(0x7var_, float:1.7945687E38)
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            chat_msgInViewsDrawable = r7
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            chat_msgInViewsSelectedDrawable = r7
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            chat_msgOutViewsDrawable = r7
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            chat_msgOutViewsSelectedDrawable = r7
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            chat_msgMediaViewsDrawable = r7
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgStickerViewsDrawable = r5
            r5 = 2131165656(0x7var_d8, float:1.7945535E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgInMenuDrawable = r5
            r5 = 2131165656(0x7var_d8, float:1.7945535E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgInMenuSelectedDrawable = r5
            r5 = 2131165656(0x7var_d8, float:1.7945535E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgOutMenuDrawable = r5
            r5 = 2131165656(0x7var_d8, float:1.7945535E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgOutMenuSelectedDrawable = r5
            r5 = 2131165953(0x7var_, float:1.7946138E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_msgMediaMenuDrawable = r5
            r5 = 2131165683(0x7var_f3, float:1.794559E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgInInstantDrawable = r5
            r5 = 2131165683(0x7var_f3, float:1.794559E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgOutInstantDrawable = r5
            r5 = 2131165732(0x7var_, float:1.794569E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_msgErrorDrawable = r5
            r5 = 2131165553(0x7var_, float:1.7945326E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_muteIconDrawable = r5
            r5 = 2131165474(0x7var_, float:1.7945166E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_lockIconDrawable = r5
            r5 = 2131165305(0x7var_, float:1.7944823E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgBroadcastDrawable = r5
            r5 = 2131165305(0x7var_, float:1.7944823E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgBroadcastMediaDrawable = r5
            r5 = 2131165457(0x7var_, float:1.7945132E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgInCallDrawable = r5
            r5 = 2131165457(0x7var_, float:1.7945132E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgInCallSelectedDrawable = r5
            r5 = 2131165457(0x7var_, float:1.7945132E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgOutCallDrawable = r5
            r5 = 2131165457(0x7var_, float:1.7945132E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgOutCallSelectedDrawable = r5
            r5 = 2131165460(0x7var_, float:1.7945138E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgCallUpGreenDrawable = r5
            r5 = 2131165463(0x7var_, float:1.7945144E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgCallDownRedDrawable = r5
            r5 = 2131165463(0x7var_, float:1.7945144E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgCallDownGreenDrawable = r5
            r5 = 0
        L_0x0433:
            if (r5 >= r3) goto L_0x0456
            android.graphics.drawable.Drawable[] r7 = chat_pollCheckDrawable
            r8 = 2131165827(0x7var_, float:1.7945882E38)
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            r7[r5] = r8
            android.graphics.drawable.Drawable[] r7 = chat_pollCrossDrawable
            r8 = 2131165828(0x7var_, float:1.7945884E38)
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            r7[r5] = r8
            int r5 = r5 + 1
            goto L_0x0433
        L_0x0456:
            r5 = 2131165460(0x7var_, float:1.7945138E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            calllog_msgCallUpRedDrawable = r5
            r5 = 2131165460(0x7var_, float:1.7945138E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            calllog_msgCallUpGreenDrawable = r5
            r5 = 2131165463(0x7var_, float:1.7945144E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            calllog_msgCallDownRedDrawable = r5
            r5 = 2131165463(0x7var_, float:1.7945144E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            calllog_msgCallDownGreenDrawable = r5
            r5 = 2131165560(0x7var_, float:1.794534E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgAvatarLiveLocationDrawable = r5
            r5 = 2131165296(0x7var_, float:1.7944805E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_inlineResultFile = r5
            r5 = 2131165300(0x7var_, float:1.7944813E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_inlineResultAudio = r5
            r5 = 2131165299(0x7var_, float:1.7944811E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_inlineResultLocation = r5
            r5 = 2131165574(0x7var_, float:1.7945369E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_redLocationIcon = r5
            r5 = 2131165298(0x7var_, float:1.794481E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_botLinkDrawalbe = r5
            r5 = 2131165297(0x7var_, float:1.7944807E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_botInlineDrawable = r5
            r5 = 2131165927(0x7var_e7, float:1.7946085E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_systemDrawable = r5
            r5 = 2131165434(0x7var_fa, float:1.7945085E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_contextResult_shadowUnderSwitchDrawable = r5
            android.graphics.drawable.Drawable[] r5 = chat_attachButtonDrawables
            r7 = 1112014848(0x42480000, float:50.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r9 = 2131165270(0x7var_, float:1.7944752E38)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r9)
            r5[r6] = r8
            android.graphics.drawable.Drawable[] r5 = chat_attachButtonDrawables
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r9 = 2131165266(0x7var_, float:1.7944744E38)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r9)
            r5[r2] = r8
            android.graphics.drawable.Drawable[] r5 = chat_attachButtonDrawables
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r9 = 2131165269(0x7var_, float:1.794475E38)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r9)
            r5[r3] = r8
            android.graphics.drawable.Drawable[] r5 = chat_attachButtonDrawables
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r9 = 2131165268(0x7var_, float:1.7944748E38)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r9)
            r9 = 3
            r5[r9] = r8
            android.graphics.drawable.Drawable[] r5 = chat_attachButtonDrawables
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r10 = 2131165271(0x7var_, float:1.7944754E38)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r10)
            r10 = 4
            r5[r10] = r8
            android.graphics.drawable.Drawable[] r5 = chat_attachButtonDrawables
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r8 = 2131165272(0x7var_, float:1.7944756E38)
            org.telegram.ui.Components.CombinedDrawable r7 = createCircleDrawableWithIcon(r7, r8)
            r8 = 5
            r5[r8] = r7
            r5 = 2131165747(0x7var_, float:1.794572E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_attachEmptyDrawable = r5
            android.graphics.drawable.Drawable[] r5 = chat_cornerOuter
            r7 = 2131165366(0x7var_b6, float:1.7944947E38)
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r7)
            r5[r6] = r7
            android.graphics.drawable.Drawable[] r5 = chat_cornerOuter
            r7 = 2131165367(0x7var_b7, float:1.794495E38)
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r7)
            r5[r2] = r7
            android.graphics.drawable.Drawable[] r5 = chat_cornerOuter
            r7 = 2131165365(0x7var_b5, float:1.7944945E38)
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r7)
            r5[r3] = r7
            android.graphics.drawable.Drawable[] r5 = chat_cornerOuter
            r7 = 2131165364(0x7var_b4, float:1.7944943E38)
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r7)
            r5[r9] = r7
            android.graphics.drawable.Drawable[] r5 = chat_cornerInner
            r7 = 2131165363(0x7var_b3, float:1.794494E38)
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r7)
            r5[r6] = r7
            android.graphics.drawable.Drawable[] r5 = chat_cornerInner
            r7 = 2131165362(0x7var_b2, float:1.7944939E38)
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r7)
            r5[r2] = r7
            android.graphics.drawable.Drawable[] r5 = chat_cornerInner
            r7 = 2131165361(0x7var_b1, float:1.7944937E38)
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r7)
            r5[r3] = r7
            android.graphics.drawable.Drawable[] r5 = chat_cornerInner
            r7 = 2131165360(0x7var_b0, float:1.7944935E38)
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r7)
            r5[r9] = r7
            r5 = 2131165875(0x7var_b3, float:1.794598E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_shareDrawable = r5
            r5 = 2131165874(0x7var_b2, float:1.7945977E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_shareIconDrawable = r5
            r5 = 2131165374(0x7var_be, float:1.7944963E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_replyIconDrawable = r5
            r5 = 2131165649(0x7var_d1, float:1.7945521E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_goIconDrawable = r5
            org.telegram.ui.Components.CombinedDrawable[][] r5 = chat_fileMiniStatesDrawable
            r5 = r5[r6]
            r7 = 1102053376(0x41b00000, float:22.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r12 = 2131165275(0x7var_b, float:1.7944763E38)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r12)
            r5[r6] = r11
            org.telegram.ui.Components.CombinedDrawable[][] r5 = chat_fileMiniStatesDrawable
            r5 = r5[r6]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r12)
            r5[r2] = r11
            org.telegram.ui.Components.CombinedDrawable[][] r5 = chat_fileMiniStatesDrawable
            r5 = r5[r2]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r12 = 2131165276(0x7var_c, float:1.7944765E38)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r12)
            r5[r6] = r11
            org.telegram.ui.Components.CombinedDrawable[][] r5 = chat_fileMiniStatesDrawable
            r5 = r5[r2]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r12)
            r5[r2] = r11
            org.telegram.ui.Components.CombinedDrawable[][] r5 = chat_fileMiniStatesDrawable
            r5 = r5[r3]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r12 = 2131165275(0x7var_b, float:1.7944763E38)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r12)
            r5[r6] = r11
            org.telegram.ui.Components.CombinedDrawable[][] r5 = chat_fileMiniStatesDrawable
            r5 = r5[r3]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r12)
            r5[r2] = r11
            org.telegram.ui.Components.CombinedDrawable[][] r5 = chat_fileMiniStatesDrawable
            r5 = r5[r9]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r12 = 2131165276(0x7var_c, float:1.7944765E38)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r12)
            r5[r6] = r11
            org.telegram.ui.Components.CombinedDrawable[][] r5 = chat_fileMiniStatesDrawable
            r5 = r5[r9]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r12)
            r5[r2] = r11
            org.telegram.ui.Components.CombinedDrawable[][] r5 = chat_fileMiniStatesDrawable
            r5 = r5[r10]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r12 = 2131165957(0x7var_, float:1.7946146E38)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r12)
            r5[r6] = r11
            org.telegram.ui.Components.CombinedDrawable[][] r5 = chat_fileMiniStatesDrawable
            r5 = r5[r10]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r12)
            r5[r2] = r11
            org.telegram.ui.Components.CombinedDrawable[][] r5 = chat_fileMiniStatesDrawable
            r5 = r5[r8]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r12 = 2131165958(0x7var_, float:1.7946148E38)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r12)
            r5[r6] = r11
            org.telegram.ui.Components.CombinedDrawable[][] r5 = chat_fileMiniStatesDrawable
            r5 = r5[r8]
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r11 = 2131165958(0x7var_, float:1.7946148E38)
            org.telegram.ui.Components.CombinedDrawable r7 = createCircleDrawableWithIcon(r7, r11)
            r5[r2] = r7
            r5 = 1073741824(0x40000000, float:2.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.graphics.RectF r7 = new android.graphics.RectF
            r7.<init>()
            android.graphics.Path[] r11 = chat_filePath
            android.graphics.Path r12 = new android.graphics.Path
            r12.<init>()
            r11[r6] = r12
            android.graphics.Path[] r11 = chat_filePath
            r11 = r11[r6]
            r12 = 1088421888(0x40e00000, float:7.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r0)
            float r13 = (float) r13
            r11.moveTo(r12, r13)
            android.graphics.Path[] r11 = chat_filePath
            r11 = r11[r6]
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r12 = (float) r12
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r0)
            float r13 = (float) r13
            r11.lineTo(r12, r13)
            android.graphics.Path[] r11 = chat_filePath
            r11 = r11[r6]
            r12 = 1101529088(0x41a80000, float:21.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r13 = (float) r13
            r14 = 1092616192(0x41200000, float:10.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            r11.lineTo(r13, r14)
            android.graphics.Path[] r11 = chat_filePath
            r11 = r11[r6]
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r13 = (float) r13
            r14 = 1101004800(0x41a00000, float:20.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            r11.lineTo(r13, r14)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r13 = r5 * 2
            int r11 = r11 - r13
            float r11 = (float) r11
            r14 = 1100480512(0x41980000, float:19.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r14 = r14 - r5
            float r14 = (float) r14
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r15 = (float) r15
            r16 = 1100480512(0x41980000, float:19.0)
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r8 = r16 + r5
            float r8 = (float) r8
            r7.set(r11, r14, r15, r8)
            android.graphics.Path[] r8 = chat_filePath
            r8 = r8[r6]
            r11 = 0
            r14 = 1119092736(0x42b40000, float:90.0)
            r8.arcTo(r7, r11, r14, r6)
            android.graphics.Path[] r8 = chat_filePath
            r8 = r8[r6]
            r11 = 1086324736(0x40CLASSNAME, float:6.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r8.lineTo(r11, r12)
            r8 = 1084227584(0x40a00000, float:5.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r11 = (float) r11
            r12 = 1100480512(0x41980000, float:19.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r12 = r12 - r5
            float r12 = (float) r12
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r14 = r14 + r13
            float r14 = (float) r14
            r15 = 1100480512(0x41980000, float:19.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r15 = r15 + r5
            float r5 = (float) r15
            r7.set(r11, r12, r14, r5)
            android.graphics.Path[] r5 = chat_filePath
            r5 = r5[r6]
            r11 = 1119092736(0x42b40000, float:90.0)
            r12 = 1119092736(0x42b40000, float:90.0)
            r5.arcTo(r7, r11, r12, r6)
            android.graphics.Path[] r5 = chat_filePath
            r5 = r5[r6]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r11 = (float) r11
            r12 = 1082130432(0x40800000, float:4.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r5.lineTo(r11, r12)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r5 = (float) r5
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r0)
            float r11 = (float) r11
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r12 = r12 + r13
            float r12 = (float) r12
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r14 = r14 + r13
            float r13 = (float) r14
            r7.set(r5, r11, r12, r13)
            android.graphics.Path[] r5 = chat_filePath
            r5 = r5[r6]
            r11 = 1127481344(0x43340000, float:180.0)
            r12 = 1119092736(0x42b40000, float:90.0)
            r5.arcTo(r7, r11, r12, r6)
            android.graphics.Path[] r5 = chat_filePath
            r5 = r5[r6]
            r5.close()
            android.graphics.Path[] r5 = chat_filePath
            android.graphics.Path r7 = new android.graphics.Path
            r7.<init>()
            r5[r2] = r7
            android.graphics.Path[] r5 = chat_filePath
            r5 = r5[r2]
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r7 = (float) r7
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r8 = (float) r8
            r5.moveTo(r7, r8)
            android.graphics.Path[] r5 = chat_filePath
            r5 = r5[r2]
            r7 = 1100480512(0x41980000, float:19.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            r8 = 1092616192(0x41200000, float:10.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r8 = (float) r8
            r5.lineTo(r7, r8)
            android.graphics.Path[] r5 = chat_filePath
            r5 = r5[r2]
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r7 = (float) r7
            r8 = 1092616192(0x41200000, float:10.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r8 = (float) r8
            r5.lineTo(r7, r8)
            android.graphics.Path[] r5 = chat_filePath
            r5 = r5[r2]
            r5.close()
            r5 = 2131165308(0x7var_c, float:1.794483E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_flameIcon = r5
            r5 = 2131165711(0x7var_f, float:1.7945647E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_gifIcon = r5
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r6]
            r7 = 1110441984(0x42300000, float:44.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r11 = 2131165714(0x7var_, float:1.7945653E38)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r11)
            r5[r6] = r8
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r6]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r11)
            r5[r2] = r8
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r2]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r12 = 2131165713(0x7var_, float:1.794565E38)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r12)
            r5[r6] = r8
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r2]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r12)
            r5[r2] = r8
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r3]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r12 = 2131165712(0x7var_, float:1.7945649E38)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r12)
            r5[r6] = r8
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r3]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r12)
            r5[r2] = r8
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r9]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r13 = 2131165710(0x7var_e, float:1.7945645E38)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r13)
            r5[r6] = r8
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r9]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r13)
            r5[r2] = r8
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r10]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r13 = 2131165709(0x7var_d, float:1.7945643E38)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r13)
            r5[r6] = r8
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r10]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r13)
            r5[r2] = r8
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r8 = 5
            r5 = r5[r8]
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r14 = createCircleDrawableWithIcon(r14, r11)
            r5[r6] = r14
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r8]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r11)
            r5[r2] = r8
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r8 = 6
            r5 = r5[r8]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r14 = 2131165713(0x7var_, float:1.794565E38)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r14)
            r5[r6] = r8
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r8 = 6
            r5 = r5[r8]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r14)
            r5[r2] = r8
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r8 = 7
            r5 = r5[r8]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r12)
            r5[r6] = r8
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r8 = 7
            r5 = r5[r8]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r12)
            r5[r2] = r8
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r8 = 8
            r5 = r5[r8]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r14 = 2131165710(0x7var_e, float:1.7945645E38)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r14)
            r5[r6] = r8
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r8 = 8
            r5 = r5[r8]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r14)
            r5[r2] = r8
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r8 = 9
            r5 = r5[r8]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r13)
            r5[r6] = r8
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r8 = 9
            r5 = r5[r8]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r13)
            r5[r2] = r8
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r5 = r5[r6]
            r8 = 1111490560(0x42400000, float:48.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r14 = createCircleDrawableWithIcon(r14, r12)
            r5[r6] = r14
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r5 = r5[r6]
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r14 = createCircleDrawableWithIcon(r14, r12)
            r5[r2] = r14
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r5 = r5[r2]
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r14 = createCircleDrawableWithIcon(r14, r13)
            r5[r6] = r14
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r5 = r5[r2]
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r14 = createCircleDrawableWithIcon(r14, r13)
            r5[r2] = r14
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r5 = r5[r3]
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r15 = 2131165711(0x7var_f, float:1.7945647E38)
            org.telegram.ui.Components.CombinedDrawable r14 = createCircleDrawableWithIcon(r14, r15)
            r5[r6] = r14
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r5 = r5[r3]
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r14 = createCircleDrawableWithIcon(r14, r15)
            r5[r2] = r14
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r5 = r5[r9]
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r14 = createCircleDrawableWithIcon(r14, r11)
            r5[r6] = r14
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r5 = r5[r9]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r9 = createCircleDrawableWithIcon(r9, r11)
            r5[r2] = r9
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r9 = r5[r10]
            r5 = r5[r10]
            r10 = 2131165308(0x7var_c, float:1.794483E38)
            android.graphics.drawable.Drawable r10 = r4.getDrawable(r10)
            r5[r2] = r10
            r9[r6] = r10
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r9 = 5
            r10 = r5[r9]
            r5 = r5[r9]
            r9 = 2131165334(0x7var_, float:1.7944882E38)
            android.graphics.drawable.Drawable r9 = r4.getDrawable(r9)
            r5[r2] = r9
            r10[r6] = r9
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r9 = 6
            r9 = r5[r9]
            r10 = 6
            r5 = r5[r10]
            r10 = 2131165803(0x7var_b, float:1.7945833E38)
            android.graphics.drawable.Drawable r10 = r4.getDrawable(r10)
            r5[r2] = r10
            r9[r6] = r10
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r9 = 7
            r5 = r5[r9]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r9 = createCircleDrawableWithIcon(r9, r12)
            r5[r6] = r9
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r9 = 7
            r5 = r5[r9]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r9 = createCircleDrawableWithIcon(r9, r12)
            r5[r2] = r9
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r9 = 8
            r5 = r5[r9]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r9 = createCircleDrawableWithIcon(r9, r13)
            r5[r6] = r9
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r9 = 8
            r5 = r5[r9]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r9 = createCircleDrawableWithIcon(r9, r13)
            r5[r2] = r9
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r9 = 9
            r5 = r5[r9]
            r9 = 2131165370(0x7var_ba, float:1.7944955E38)
            android.graphics.drawable.Drawable r9 = r4.getDrawable(r9)
            android.graphics.drawable.Drawable r9 = r9.mutate()
            r5[r6] = r9
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r9 = 9
            r5 = r5[r9]
            r9 = 2131165370(0x7var_ba, float:1.7944955E38)
            android.graphics.drawable.Drawable r9 = r4.getDrawable(r9)
            android.graphics.drawable.Drawable r9 = r9.mutate()
            r5[r2] = r9
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r9 = 10
            r5 = r5[r9]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r9 = createCircleDrawableWithIcon(r9, r12)
            r5[r6] = r9
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r9 = 10
            r5 = r5[r9]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r9 = createCircleDrawableWithIcon(r9, r12)
            r5[r2] = r9
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r9 = 11
            r5 = r5[r9]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r9 = createCircleDrawableWithIcon(r9, r13)
            r5[r6] = r9
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r9 = 11
            r5 = r5[r9]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r13)
            r5[r2] = r8
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r8 = 12
            r5 = r5[r8]
            r8 = 2131165370(0x7var_ba, float:1.7944955E38)
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            r5[r6] = r8
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r8 = 12
            r5 = r5[r8]
            r8 = 2131165370(0x7var_ba, float:1.7944955E38)
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            r5[r2] = r8
            android.graphics.drawable.Drawable[] r5 = chat_contactDrawable
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r9 = 2131165668(0x7var_e4, float:1.794556E38)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r9)
            r5[r6] = r8
            android.graphics.drawable.Drawable[] r5 = chat_contactDrawable
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r8 = 2131165668(0x7var_e4, float:1.794556E38)
            org.telegram.ui.Components.CombinedDrawable r7 = createCircleDrawableWithIcon(r7, r8)
            r5[r2] = r7
            android.graphics.drawable.Drawable[] r5 = chat_locationDrawable
            r7 = 2131165688(0x7var_f8, float:1.79456E38)
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r7)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            r5[r6] = r7
            android.graphics.drawable.Drawable[] r5 = chat_locationDrawable
            r7 = 2131165688(0x7var_f8, float:1.79456E38)
            android.graphics.drawable.Drawable r4 = r4.getDrawable(r7)
            android.graphics.drawable.Drawable r4 = r4.mutate()
            r5[r2] = r4
            android.content.res.Resources r4 = r17.getResources()
            r5 = 2131165357(0x7var_ad, float:1.7944929E38)
            android.graphics.drawable.Drawable r4 = r4.getDrawable(r5)
            chat_composeShadowDrawable = r4
            int r4 = org.telegram.messenger.AndroidUtilities.roundMessageSize     // Catch:{ all -> 0x0b55 }
            r5 = 1086324736(0x40CLASSNAME, float:6.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)     // Catch:{ all -> 0x0b55 }
            int r4 = r4 + r5
            android.graphics.Bitmap$Config r5 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0b55 }
            android.graphics.Bitmap r5 = android.graphics.Bitmap.createBitmap(r4, r4, r5)     // Catch:{ all -> 0x0b55 }
            android.graphics.Canvas r7 = new android.graphics.Canvas     // Catch:{ all -> 0x0b55 }
            r7.<init>(r5)     // Catch:{ all -> 0x0b55 }
            android.graphics.Paint r8 = new android.graphics.Paint     // Catch:{ all -> 0x0b55 }
            r8.<init>(r2)     // Catch:{ all -> 0x0b55 }
            r8.setColor(r6)     // Catch:{ all -> 0x0b55 }
            android.graphics.Paint$Style r9 = android.graphics.Paint.Style.FILL     // Catch:{ all -> 0x0b55 }
            r8.setStyle(r9)     // Catch:{ all -> 0x0b55 }
            android.graphics.PorterDuffXfermode r9 = new android.graphics.PorterDuffXfermode     // Catch:{ all -> 0x0b55 }
            android.graphics.PorterDuff$Mode r10 = android.graphics.PorterDuff.Mode.CLEAR     // Catch:{ all -> 0x0b55 }
            r9.<init>(r10)     // Catch:{ all -> 0x0b55 }
            r8.setXfermode(r9)     // Catch:{ all -> 0x0b55 }
            android.graphics.Paint r9 = new android.graphics.Paint     // Catch:{ all -> 0x0b55 }
            r9.<init>(r2)     // Catch:{ all -> 0x0b55 }
            r2 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ all -> 0x0b55 }
            float r2 = (float) r2     // Catch:{ all -> 0x0b55 }
            r10 = 0
            r11 = 0
            r12 = 1593835520(0x5var_, float:9.223372E18)
            r9.setShadowLayer(r2, r10, r11, r12)     // Catch:{ all -> 0x0b55 }
        L_0x0b2c:
            if (r6 >= r3) goto L_0x0b4a
            int r2 = r4 / 2
            float r2 = (float) r2     // Catch:{ all -> 0x0b55 }
            int r10 = r4 / 2
            float r10 = (float) r10     // Catch:{ all -> 0x0b55 }
            int r11 = org.telegram.messenger.AndroidUtilities.roundMessageSize     // Catch:{ all -> 0x0b55 }
            int r11 = r11 / r3
            r12 = 1065353216(0x3var_, float:1.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)     // Catch:{ all -> 0x0b55 }
            int r11 = r11 - r12
            float r11 = (float) r11     // Catch:{ all -> 0x0b55 }
            if (r6 != 0) goto L_0x0b43
            r12 = r9
            goto L_0x0b44
        L_0x0b43:
            r12 = r8
        L_0x0b44:
            r7.drawCircle(r2, r10, r11, r12)     // Catch:{ all -> 0x0b55 }
            int r6 = r6 + 1
            goto L_0x0b2c
        L_0x0b4a:
            r2 = 0
            r7.setBitmap(r2)     // Catch:{ Exception -> 0x0b4e }
        L_0x0b4e:
            android.graphics.drawable.BitmapDrawable r2 = new android.graphics.drawable.BitmapDrawable     // Catch:{ all -> 0x0b55 }
            r2.<init>(r5)     // Catch:{ all -> 0x0b55 }
            chat_roundVideoShadow = r2     // Catch:{ all -> 0x0b55 }
        L_0x0b55:
            applyChatTheme(r18)
        L_0x0b58:
            android.text.TextPaint r2 = chat_msgTextPaintOneEmoji
            r4 = 1105199104(0x41e00000, float:28.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r2.setTextSize(r4)
            android.text.TextPaint r2 = chat_msgTextPaintTwoEmoji
            r4 = 1103101952(0x41CLASSNAME, float:24.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r2.setTextSize(r4)
            android.text.TextPaint r2 = chat_msgTextPaintThreeEmoji
            r4 = 1101004800(0x41a00000, float:20.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r2.setTextSize(r4)
            android.text.TextPaint r2 = chat_msgTextPaint
            int r4 = org.telegram.messenger.SharedConfig.fontSize
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r2.setTextSize(r4)
            android.text.TextPaint r2 = chat_msgGameTextPaint
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r4 = (float) r4
            r2.setTextSize(r4)
            android.text.TextPaint r2 = chat_msgBotButtonPaint
            r4 = 1097859072(0x41700000, float:15.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r5 = (float) r5
            r2.setTextSize(r5)
            if (r18 != 0) goto L_0x0cdd
            android.graphics.Paint r2 = chat_botProgressPaint
            if (r2 == 0) goto L_0x0cdd
            r5 = 1073741824(0x40000000, float:2.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r2.setStrokeWidth(r5)
            android.text.TextPaint r2 = chat_infoPaint
            r5 = 1094713344(0x41400000, float:12.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r6 = (float) r6
            r2.setTextSize(r6)
            android.text.TextPaint r2 = chat_docNamePaint
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r6 = (float) r6
            r2.setTextSize(r6)
            android.text.TextPaint r2 = chat_locationTitlePaint
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r6 = (float) r6
            r2.setTextSize(r6)
            android.text.TextPaint r2 = chat_locationAddressPaint
            r6 = 1095761920(0x41500000, float:13.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r7 = (float) r7
            r2.setTextSize(r7)
            android.text.TextPaint r2 = chat_audioTimePaint
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r7 = (float) r7
            r2.setTextSize(r7)
            android.text.TextPaint r2 = chat_livePaint
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r7 = (float) r7
            r2.setTextSize(r7)
            android.text.TextPaint r2 = chat_audioTitlePaint
            r7 = 1098907648(0x41800000, float:16.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            r2.setTextSize(r7)
            android.text.TextPaint r2 = chat_audioPerformerPaint
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r7 = (float) r7
            r2.setTextSize(r7)
            android.text.TextPaint r2 = chat_botButtonPaint
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r7 = (float) r7
            r2.setTextSize(r7)
            android.text.TextPaint r2 = chat_contactNamePaint
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r7 = (float) r7
            r2.setTextSize(r7)
            android.text.TextPaint r2 = chat_contactPhonePaint
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r7 = (float) r7
            r2.setTextSize(r7)
            android.text.TextPaint r2 = chat_durationPaint
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r7 = (float) r7
            r2.setTextSize(r7)
            android.text.TextPaint r2 = chat_timePaint
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r2.setTextSize(r5)
            android.text.TextPaint r2 = chat_adminPaint
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r5 = (float) r5
            r2.setTextSize(r5)
            android.text.TextPaint r2 = chat_namePaint
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r5 = (float) r5
            r2.setTextSize(r5)
            android.text.TextPaint r2 = chat_forwardNamePaint
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r5 = (float) r5
            r2.setTextSize(r5)
            android.text.TextPaint r2 = chat_replyNamePaint
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r5 = (float) r5
            r2.setTextSize(r5)
            android.text.TextPaint r2 = chat_replyTextPaint
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r2.setTextSize(r1)
            android.text.TextPaint r1 = chat_gamePaint
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r2 = (float) r2
            r1.setTextSize(r2)
            android.text.TextPaint r1 = chat_shipmentPaint
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r2 = (float) r2
            r1.setTextSize(r2)
            android.text.TextPaint r1 = chat_instantViewPaint
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r2 = (float) r2
            r1.setTextSize(r2)
            android.graphics.Paint r1 = chat_instantViewRectPaint
            r2 = 1065353216(0x3var_, float:1.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r1.setStrokeWidth(r2)
            android.graphics.Paint r1 = chat_statusRecordPaint
            r2 = 1073741824(0x40000000, float:2.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r1.setStrokeWidth(r2)
            android.text.TextPaint r1 = chat_actionTextPaint
            r2 = 16
            int r5 = org.telegram.messenger.SharedConfig.fontSize
            int r2 = java.lang.Math.max(r2, r5)
            int r2 = r2 - r3
            float r2 = (float) r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r1.setTextSize(r2)
            android.text.TextPaint r1 = chat_contextResult_titleTextPaint
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r2 = (float) r2
            r1.setTextSize(r2)
            android.text.TextPaint r1 = chat_contextResult_descriptionTextPaint
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r2 = (float) r2
            r1.setTextSize(r2)
            android.graphics.Paint r1 = chat_radialProgressPaint
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            float r0 = (float) r0
            r1.setStrokeWidth(r0)
            android.graphics.Paint r0 = chat_radialProgress2Paint
            r1 = 1073741824(0x40000000, float:2.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r0.setStrokeWidth(r1)
        L_0x0cdd:
            return
        L_0x0cde:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0cde }
            goto L_0x0ce2
        L_0x0ce1:
            throw r0
        L_0x0ce2:
            goto L_0x0ce1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.createChatResources(android.content.Context, boolean):void");
    }

    public static void applyChatTheme(boolean z) {
        if (chat_msgTextPaint != null && chat_msgInDrawable != null && !z) {
            chat_gamePaint.setColor(getColor("chat_previewGameText"));
            chat_durationPaint.setColor(getColor("chat_previewDurationText"));
            chat_botButtonPaint.setColor(getColor("chat_botButtonText"));
            chat_urlPaint.setColor(getColor("chat_linkSelectBackground"));
            chat_botProgressPaint.setColor(getColor("chat_botProgress"));
            chat_deleteProgressPaint.setColor(getColor("chat_secretTimeText"));
            chat_textSearchSelectionPaint.setColor(getColor("chat_textSelectBackground"));
            chat_msgErrorPaint.setColor(getColor("chat_sentError"));
            chat_statusPaint.setColor(getColor("chat_status"));
            chat_statusRecordPaint.setColor(getColor("chat_status"));
            chat_actionTextPaint.setColor(getColor("chat_serviceText"));
            chat_actionTextPaint.linkColor = getColor("chat_serviceLink");
            chat_contextResult_titleTextPaint.setColor(getColor("windowBackgroundWhiteBlackText"));
            chat_composeBackgroundPaint.setColor(getColor("chat_messagePanelBackground"));
            chat_timeBackgroundPaint.setColor(getColor("chat_mediaTimeBackground"));
            setDrawableColorByKey(chat_msgNoSoundDrawable, "chat_mediaTimeText");
            setDrawableColorByKey(chat_msgInDrawable, "chat_inBubble");
            setDrawableColorByKey(chat_msgInSelectedDrawable, "chat_inBubbleSelected");
            setDrawableColorByKey(chat_msgInMediaDrawable, "chat_inBubble");
            setDrawableColorByKey(chat_msgInMediaSelectedDrawable, "chat_inBubbleSelected");
            setDrawableColorByKey(chat_msgOutCheckDrawable, "chat_outSentCheck");
            setDrawableColorByKey(chat_msgOutCheckSelectedDrawable, "chat_outSentCheckSelected");
            setDrawableColorByKey(chat_msgOutCheckReadDrawable, "chat_outSentCheckRead");
            setDrawableColorByKey(chat_msgOutCheckReadSelectedDrawable, "chat_outSentCheckReadSelected");
            setDrawableColorByKey(chat_msgOutHalfCheckDrawable, "chat_outSentCheckRead");
            setDrawableColorByKey(chat_msgOutHalfCheckSelectedDrawable, "chat_outSentCheckReadSelected");
            setDrawableColorByKey(chat_msgOutClockDrawable, "chat_outSentClock");
            setDrawableColorByKey(chat_msgOutSelectedClockDrawable, "chat_outSentClockSelected");
            setDrawableColorByKey(chat_msgInClockDrawable, "chat_inSentClock");
            setDrawableColorByKey(chat_msgInSelectedClockDrawable, "chat_inSentClockSelected");
            setDrawableColorByKey(chat_msgMediaCheckDrawable, "chat_mediaSentCheck");
            setDrawableColorByKey(chat_msgMediaHalfCheckDrawable, "chat_mediaSentCheck");
            setDrawableColorByKey(chat_msgMediaClockDrawable, "chat_mediaSentClock");
            setDrawableColorByKey(chat_msgStickerCheckDrawable, "chat_serviceText");
            setDrawableColorByKey(chat_msgStickerHalfCheckDrawable, "chat_serviceText");
            setDrawableColorByKey(chat_msgStickerClockDrawable, "chat_serviceText");
            setDrawableColorByKey(chat_msgStickerViewsDrawable, "chat_serviceText");
            setDrawableColorByKey(chat_shareIconDrawable, "chat_serviceIcon");
            setDrawableColorByKey(chat_replyIconDrawable, "chat_serviceIcon");
            setDrawableColorByKey(chat_goIconDrawable, "chat_serviceIcon");
            setDrawableColorByKey(chat_botInlineDrawable, "chat_serviceIcon");
            setDrawableColorByKey(chat_botLinkDrawalbe, "chat_serviceIcon");
            setDrawableColorByKey(chat_msgInViewsDrawable, "chat_inViews");
            setDrawableColorByKey(chat_msgInViewsSelectedDrawable, "chat_inViewsSelected");
            setDrawableColorByKey(chat_msgOutViewsDrawable, "chat_outViews");
            setDrawableColorByKey(chat_msgOutViewsSelectedDrawable, "chat_outViewsSelected");
            setDrawableColorByKey(chat_msgMediaViewsDrawable, "chat_mediaViews");
            setDrawableColorByKey(chat_msgInMenuDrawable, "chat_inMenu");
            setDrawableColorByKey(chat_msgInMenuSelectedDrawable, "chat_inMenuSelected");
            setDrawableColorByKey(chat_msgOutMenuDrawable, "chat_outMenu");
            setDrawableColorByKey(chat_msgOutMenuSelectedDrawable, "chat_outMenuSelected");
            setDrawableColorByKey(chat_msgMediaMenuDrawable, "chat_mediaMenu");
            setDrawableColorByKey(chat_msgOutInstantDrawable, "chat_outInstant");
            setDrawableColorByKey(chat_msgInInstantDrawable, "chat_inInstant");
            setDrawableColorByKey(chat_msgErrorDrawable, "chat_sentErrorIcon");
            setDrawableColorByKey(chat_muteIconDrawable, "chat_muteIcon");
            setDrawableColorByKey(chat_lockIconDrawable, "chat_lockIcon");
            setDrawableColorByKey(chat_msgBroadcastDrawable, "chat_outBroadcast");
            setDrawableColorByKey(chat_msgBroadcastMediaDrawable, "chat_mediaBroadcast");
            setDrawableColorByKey(chat_inlineResultFile, "chat_inlineResultIcon");
            setDrawableColorByKey(chat_inlineResultAudio, "chat_inlineResultIcon");
            setDrawableColorByKey(chat_inlineResultLocation, "chat_inlineResultIcon");
            setDrawableColorByKey(chat_msgInCallDrawable, "chat_inInstant");
            setDrawableColorByKey(chat_msgInCallSelectedDrawable, "chat_inInstantSelected");
            setDrawableColorByKey(chat_msgOutCallDrawable, "chat_outInstant");
            setDrawableColorByKey(chat_msgOutCallSelectedDrawable, "chat_outInstantSelected");
            setDrawableColorByKey(chat_msgCallUpGreenDrawable, "chat_outUpCall");
            setDrawableColorByKey(chat_msgCallDownRedDrawable, "chat_inUpCall");
            setDrawableColorByKey(chat_msgCallDownGreenDrawable, "chat_inDownCall");
            setDrawableColorByKey(calllog_msgCallUpRedDrawable, "calls_callReceivedRedIcon");
            setDrawableColorByKey(calllog_msgCallUpGreenDrawable, "calls_callReceivedGreenIcon");
            setDrawableColorByKey(calllog_msgCallDownRedDrawable, "calls_callReceivedRedIcon");
            setDrawableColorByKey(calllog_msgCallDownGreenDrawable, "calls_callReceivedGreenIcon");
            for (int i = 0; i < 2; i++) {
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i][0], getColor("chat_outLoader"), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i][0], getColor("chat_outMediaIcon"), true);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i][1], getColor("chat_outLoaderSelected"), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i][1], getColor("chat_outMediaIconSelected"), true);
                int i2 = i + 2;
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i2][0], getColor("chat_inLoader"), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i2][0], getColor("chat_inMediaIcon"), true);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i2][1], getColor("chat_inLoaderSelected"), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i2][1], getColor("chat_inMediaIconSelected"), true);
                int i3 = i + 4;
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i3][0], getColor("chat_mediaLoaderPhoto"), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i3][0], getColor("chat_mediaLoaderPhotoIcon"), true);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i3][1], getColor("chat_mediaLoaderPhotoSelected"), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i3][1], getColor("chat_mediaLoaderPhotoIconSelected"), true);
            }
            for (int i4 = 0; i4 < 5; i4++) {
                setCombinedDrawableColor(chat_fileStatesDrawable[i4][0], getColor("chat_outLoader"), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[i4][0], getColor("chat_outMediaIcon"), true);
                setCombinedDrawableColor(chat_fileStatesDrawable[i4][1], getColor("chat_outLoaderSelected"), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[i4][1], getColor("chat_outMediaIconSelected"), true);
                int i5 = i4 + 5;
                setCombinedDrawableColor(chat_fileStatesDrawable[i5][0], getColor("chat_inLoader"), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[i5][0], getColor("chat_inMediaIcon"), true);
                setCombinedDrawableColor(chat_fileStatesDrawable[i5][1], getColor("chat_inLoaderSelected"), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[i5][1], getColor("chat_inMediaIconSelected"), true);
            }
            for (int i6 = 0; i6 < 4; i6++) {
                setCombinedDrawableColor(chat_photoStatesDrawables[i6][0], getColor("chat_mediaLoaderPhoto"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i6][0], getColor("chat_mediaLoaderPhotoIcon"), true);
                setCombinedDrawableColor(chat_photoStatesDrawables[i6][1], getColor("chat_mediaLoaderPhotoSelected"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i6][1], getColor("chat_mediaLoaderPhotoIconSelected"), true);
            }
            for (int i7 = 0; i7 < 2; i7++) {
                int i8 = i7 + 7;
                setCombinedDrawableColor(chat_photoStatesDrawables[i8][0], getColor("chat_outLoaderPhoto"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i8][0], getColor("chat_outLoaderPhotoIcon"), true);
                setCombinedDrawableColor(chat_photoStatesDrawables[i8][1], getColor("chat_outLoaderPhotoSelected"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i8][1], getColor("chat_outLoaderPhotoIconSelected"), true);
                int i9 = i7 + 10;
                setCombinedDrawableColor(chat_photoStatesDrawables[i9][0], getColor("chat_inLoaderPhoto"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i9][0], getColor("chat_inLoaderPhotoIcon"), true);
                setCombinedDrawableColor(chat_photoStatesDrawables[i9][1], getColor("chat_inLoaderPhotoSelected"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i9][1], getColor("chat_inLoaderPhotoIconSelected"), true);
            }
            setDrawableColorByKey(chat_photoStatesDrawables[9][0], "chat_outFileIcon");
            setDrawableColorByKey(chat_photoStatesDrawables[9][1], "chat_outFileSelectedIcon");
            setDrawableColorByKey(chat_photoStatesDrawables[12][0], "chat_inFileIcon");
            setDrawableColorByKey(chat_photoStatesDrawables[12][1], "chat_inFileSelectedIcon");
            setCombinedDrawableColor(chat_contactDrawable[0], getColor("chat_inContactBackground"), false);
            setCombinedDrawableColor(chat_contactDrawable[0], getColor("chat_inContactIcon"), true);
            setCombinedDrawableColor(chat_contactDrawable[1], getColor("chat_outContactBackground"), false);
            setCombinedDrawableColor(chat_contactDrawable[1], getColor("chat_outContactIcon"), true);
            setDrawableColor(chat_locationDrawable[0], getColor("chat_inLocationIcon"));
            setDrawableColor(chat_locationDrawable[1], getColor("chat_outLocationIcon"));
            setDrawableColorByKey(chat_composeShadowDrawable, "chat_messagePanelShadow");
            setCombinedDrawableColor(chat_attachButtonDrawables[0], getColor("chat_attachGalleryBackground"), false);
            setCombinedDrawableColor(chat_attachButtonDrawables[0], getColor("chat_attachGalleryIcon"), true);
            setCombinedDrawableColor(chat_attachButtonDrawables[1], getColor("chat_attachAudioBackground"), false);
            setCombinedDrawableColor(chat_attachButtonDrawables[1], getColor("chat_attachAudioIcon"), true);
            setCombinedDrawableColor(chat_attachButtonDrawables[2], getColor("chat_attachFileBackground"), false);
            setCombinedDrawableColor(chat_attachButtonDrawables[2], getColor("chat_attachFileIcon"), true);
            setCombinedDrawableColor(chat_attachButtonDrawables[3], getColor("chat_attachContactBackground"), false);
            setCombinedDrawableColor(chat_attachButtonDrawables[3], getColor("chat_attachContactIcon"), true);
            setCombinedDrawableColor(chat_attachButtonDrawables[4], getColor("chat_attachLocationBackground"), false);
            setCombinedDrawableColor(chat_attachButtonDrawables[4], getColor("chat_attachLocationIcon"), true);
            setCombinedDrawableColor(chat_attachButtonDrawables[5], getColor("chat_attachPollBackground"), false);
            setCombinedDrawableColor(chat_attachButtonDrawables[5], getColor("chat_attachPollIcon"), true);
            int color = getColor("chat_outAudioSeekbarFill") == -1 ? getColor("chat_outBubble") : -1;
            setDrawableColor(chat_pollCheckDrawable[1], color);
            setDrawableColor(chat_pollCrossDrawable[1], color);
            setDrawableColor(chat_attachEmptyDrawable, getColor("chat_attachEmptyImage"));
            applyChatServiceMessageColor();
        }
    }

    public static void applyChatServiceMessageColor() {
        applyChatServiceMessageColor((int[]) null);
    }

    public static void applyChatServiceMessageColor(int[] iArr) {
        Integer num;
        Integer num2;
        Integer num3;
        Integer num4;
        if (chat_actionBackgroundPaint != null) {
            serviceMessageColor = serviceMessageColorBackup;
            serviceSelectedMessageColor = serviceSelectedMessageColorBackup;
            if (iArr == null || iArr.length < 2) {
                num2 = currentColors.get("chat_serviceBackground");
                num = currentColors.get("chat_serviceBackgroundSelected");
            } else {
                num2 = Integer.valueOf(iArr[0]);
                num = Integer.valueOf(iArr[1]);
                serviceMessageColor = iArr[0];
                serviceSelectedMessageColor = iArr[1];
            }
            if (num2 == null) {
                num2 = Integer.valueOf(serviceMessageColor);
                num3 = Integer.valueOf(serviceMessage2Color);
            } else {
                num3 = num2;
            }
            if (num == null) {
                num = Integer.valueOf(serviceSelectedMessageColor);
                num4 = Integer.valueOf(serviceSelectedMessage2Color);
            } else {
                num4 = num;
            }
            if (currentColor != num2.intValue()) {
                chat_actionBackgroundPaint.setColor(num2.intValue());
                colorFilter = new PorterDuffColorFilter(num2.intValue(), PorterDuff.Mode.MULTIPLY);
                colorFilter2 = new PorterDuffColorFilter(num3.intValue(), PorterDuff.Mode.MULTIPLY);
                currentColor = num2.intValue();
                if (chat_cornerOuter[0] != null) {
                    for (int i = 0; i < 4; i++) {
                        chat_cornerOuter[i].setColorFilter(colorFilter);
                        chat_cornerInner[i].setColorFilter(colorFilter);
                    }
                }
            }
            if (currentSelectedColor != num.intValue()) {
                currentSelectedColor = num.intValue();
                colorPressedFilter = new PorterDuffColorFilter(num.intValue(), PorterDuff.Mode.MULTIPLY);
                colorPressedFilter2 = new PorterDuffColorFilter(num4.intValue(), PorterDuff.Mode.MULTIPLY);
            }
        }
    }

    public static void createProfileResources(Context context) {
        if (profile_verifiedDrawable == null) {
            profile_aboutTextPaint = new TextPaint(1);
            Resources resources = context.getResources();
            profile_verifiedDrawable = resources.getDrawable(NUM).mutate();
            profile_verifiedCheckDrawable = resources.getDrawable(NUM).mutate();
            applyProfileTheme();
        }
        profile_aboutTextPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
    }

    public static ColorFilter getShareColorFilter(int i, boolean z) {
        if (z) {
            if (currentShareSelectedColorFilter == null || currentShareSelectedColorFilterColor != i) {
                currentShareSelectedColorFilterColor = i;
                currentShareSelectedColorFilter = new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY);
            }
            return currentShareSelectedColorFilter;
        }
        if (currentShareColorFilter == null || currentShareColorFilterColor != i) {
            currentShareColorFilterColor = i;
            currentShareColorFilter = new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY);
        }
        return currentShareColorFilter;
    }

    public static void applyProfileTheme() {
        if (profile_verifiedDrawable != null) {
            profile_aboutTextPaint.setColor(getColor("windowBackgroundWhiteBlackText"));
            profile_aboutTextPaint.linkColor = getColor("windowBackgroundWhiteLinkText");
            setDrawableColorByKey(profile_verifiedDrawable, "profile_verifiedBackground");
            setDrawableColorByKey(profile_verifiedCheckDrawable, "profile_verifiedCheck");
        }
    }

    public static Drawable getThemedDrawable(Context context, int i, String str) {
        return getThemedDrawable(context, i, getColor(str));
    }

    public static Drawable getThemedDrawable(Context context, int i, int i2) {
        if (context == null) {
            return null;
        }
        Drawable mutate = context.getResources().getDrawable(i).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(i2, PorterDuff.Mode.MULTIPLY));
        return mutate;
    }

    public static int getDefaultColor(String str) {
        Integer num = defaultColors.get(str);
        if (num == null) {
            return (str.equals("chats_menuTopShadow") || str.equals("chats_menuTopBackground") || str.equals("chats_menuTopShadowCats")) ? 0 : -65536;
        }
        return num.intValue();
    }

    public static boolean hasThemeKey(String str) {
        return currentColors.containsKey(str);
    }

    public static void setAnimatingColor(boolean z) {
        animatingColors = z ? new HashMap<>() : null;
    }

    public static boolean isAnimatingColor() {
        return animatingColors != null;
    }

    public static void setAnimatedColor(String str, int i) {
        HashMap<String, Integer> hashMap = animatingColors;
        if (hashMap != null) {
            hashMap.put(str, Integer.valueOf(i));
        }
    }

    public static int getDefaultAccentColor(String str) {
        ThemeAccent accent;
        Integer num = currentColorsNoAccent.get(str);
        if (num == null || (accent = currentTheme.getAccent(false)) == null) {
            return 0;
        }
        float[] tempHsv = getTempHsv(1);
        float[] tempHsv2 = getTempHsv(2);
        Color.colorToHSV(currentTheme.accentBaseColor, tempHsv);
        Color.colorToHSV(accent.accentColor, tempHsv2);
        return changeColorAccent(tempHsv, tempHsv2, num.intValue(), currentTheme.isDark());
    }

    public static int getNonAnimatedColor(String str) {
        return getColor(str, (boolean[]) null, true);
    }

    public static int getColor(String str) {
        return getColor(str, (boolean[]) null, false);
    }

    public static int getColor(String str, boolean[] zArr) {
        return getColor(str, zArr, false);
    }

    public static int getColor(String str, boolean[] zArr, boolean z) {
        boolean z2;
        HashMap<String, Integer> hashMap;
        Integer num;
        if (!z && (hashMap = animatingColors) != null && (num = hashMap.get(str)) != null) {
            return num.intValue();
        }
        if (currentTheme == defaultTheme) {
            if (myMessagesColorKeys.contains(str)) {
                z2 = currentTheme.isDefaultMyMessages();
            } else {
                z2 = ("chat_wallpaper".equals(str) || "chat_wallpaper_gradient_to".equals(str)) ? false : currentTheme.isDefaultMainAccent();
            }
            if (z2) {
                if (str.equals("chat_serviceBackground")) {
                    return serviceMessageColor;
                }
                if (str.equals("chat_serviceBackgroundSelected")) {
                    return serviceSelectedMessageColor;
                }
                return getDefaultColor(str);
            }
        }
        Integer num2 = currentColors.get(str);
        if (num2 == null) {
            String str2 = fallbackKeys.get(str);
            if (str2 != null) {
                num2 = currentColors.get(str2);
            }
            if (num2 == null) {
                if (zArr != null) {
                    zArr[0] = true;
                }
                if (str.equals("chat_serviceBackground")) {
                    return serviceMessageColor;
                }
                if (str.equals("chat_serviceBackgroundSelected")) {
                    return serviceSelectedMessageColor;
                }
                return getDefaultColor(str);
            }
        }
        if (str.equals("windowBackgroundWhite") || str.equals("windowBackgroundGray")) {
            return -16777216 | num2.intValue();
        }
        return num2.intValue();
    }

    public static void setColor(String str, int i, boolean z) {
        if (str.equals("chat_wallpaper") || str.equals("chat_wallpaper_gradient_to") || str.equals("windowBackgroundWhite") || str.equals("windowBackgroundGray")) {
            i |= -16777216;
        }
        if (z) {
            currentColors.remove(str);
        } else {
            currentColors.put(str, Integer.valueOf(i));
        }
        char c = 65535;
        switch (str.hashCode()) {
            case -2095843767:
                if (str.equals("chat_wallpaper_gradient_rotation")) {
                    c = 4;
                    break;
                }
                break;
            case -1625862693:
                if (str.equals("chat_wallpaper")) {
                    c = 2;
                    break;
                }
                break;
            case -1397026623:
                if (str.equals("windowBackgroundGray")) {
                    c = 6;
                    break;
                }
                break;
            case -633951866:
                if (str.equals("chat_wallpaper_gradient_to")) {
                    c = 3;
                    break;
                }
                break;
            case -552118908:
                if (str.equals("actionBarDefault")) {
                    c = 5;
                    break;
                }
                break;
            case 426061980:
                if (str.equals("chat_serviceBackground")) {
                    c = 0;
                    break;
                }
                break;
            case 1573464919:
                if (str.equals("chat_serviceBackgroundSelected")) {
                    c = 1;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 1:
                applyChatServiceMessageColor();
                return;
            case 2:
            case 3:
            case 4:
                reloadWallpaper();
                return;
            case 5:
                if (Build.VERSION.SDK_INT >= 23) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, new Object[0]);
                    return;
                }
                return;
            case 6:
                if (Build.VERSION.SDK_INT >= 26) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, new Object[0]);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public static void setDefaultColor(String str, int i) {
        defaultColors.put(str, Integer.valueOf(i));
    }

    public static void setThemeWallpaper(ThemeInfo themeInfo, Bitmap bitmap, File file) {
        currentColors.remove("chat_wallpaper");
        currentColors.remove("chat_wallpaper_gradient_to");
        currentColors.remove("chat_wallpaper_gradient_rotation");
        themedWallpaperLink = null;
        themeInfo.setOverrideWallpaper((OverrideWallpaperInfo) null);
        if (bitmap != null) {
            themedWallpaper = new BitmapDrawable(bitmap);
            saveCurrentTheme(themeInfo, false, false, false);
            calcBackgroundColor(themedWallpaper, 0);
            applyChatServiceMessageColor();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewWallpapper, new Object[0]);
            return;
        }
        themedWallpaper = null;
        wallpaper = null;
        saveCurrentTheme(themeInfo, false, false, false);
        reloadWallpaper();
    }

    public static void setDrawableColor(Drawable drawable, int i) {
        if (drawable != null) {
            if (drawable instanceof ShapeDrawable) {
                ((ShapeDrawable) drawable).getPaint().setColor(i);
            } else if (drawable instanceof ScamDrawable) {
                ((ScamDrawable) drawable).setColor(i);
            } else {
                drawable.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
            }
        }
    }

    public static void setDrawableColorByKey(Drawable drawable, String str) {
        if (str != null) {
            setDrawableColor(drawable, getColor(str));
        }
    }

    public static void setEmojiDrawableColor(Drawable drawable, int i, boolean z) {
        if (!(drawable instanceof StateListDrawable)) {
            return;
        }
        if (z) {
            try {
                Drawable stateDrawable = getStateDrawable(drawable, 0);
                if (stateDrawable instanceof ShapeDrawable) {
                    ((ShapeDrawable) stateDrawable).getPaint().setColor(i);
                } else {
                    stateDrawable.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                }
            } catch (Throwable unused) {
            }
        } else {
            Drawable stateDrawable2 = getStateDrawable(drawable, 1);
            if (stateDrawable2 instanceof ShapeDrawable) {
                ((ShapeDrawable) stateDrawable2).getPaint().setColor(i);
            } else {
                stateDrawable2.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
            }
        }
    }

    @SuppressLint({"DiscouragedPrivateApi"})
    @TargetApi(21)
    public static void setRippleDrawableForceSoftware(RippleDrawable rippleDrawable) {
        if (rippleDrawable != null) {
            Class<RippleDrawable> cls = RippleDrawable.class;
            try {
                cls.getDeclaredMethod("setForceSoftware", new Class[]{Boolean.TYPE}).invoke(rippleDrawable, new Object[]{true});
            } catch (Throwable unused) {
            }
        }
    }

    public static void setSelectorDrawableColor(Drawable drawable, int i, boolean z) {
        if (drawable instanceof StateListDrawable) {
            if (z) {
                try {
                    Drawable stateDrawable = getStateDrawable(drawable, 0);
                    if (stateDrawable instanceof ShapeDrawable) {
                        ((ShapeDrawable) stateDrawable).getPaint().setColor(i);
                    } else {
                        stateDrawable.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                    }
                    Drawable stateDrawable2 = getStateDrawable(drawable, 1);
                    if (stateDrawable2 instanceof ShapeDrawable) {
                        ((ShapeDrawable) stateDrawable2).getPaint().setColor(i);
                    } else {
                        stateDrawable2.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                    }
                } catch (Throwable unused) {
                }
            } else {
                Drawable stateDrawable3 = getStateDrawable(drawable, 2);
                if (stateDrawable3 instanceof ShapeDrawable) {
                    ((ShapeDrawable) stateDrawable3).getPaint().setColor(i);
                } else {
                    stateDrawable3.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                }
            }
        } else if (Build.VERSION.SDK_INT >= 21 && (drawable instanceof RippleDrawable)) {
            RippleDrawable rippleDrawable = (RippleDrawable) drawable;
            if (z) {
                rippleDrawable.setColor(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{i}));
            } else if (rippleDrawable.getNumberOfLayers() > 0) {
                Drawable drawable2 = rippleDrawable.getDrawable(0);
                if (drawable2 instanceof ShapeDrawable) {
                    ((ShapeDrawable) drawable2).getPaint().setColor(i);
                } else {
                    drawable2.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                }
            }
        }
    }

    public static boolean isThemeWallpaperPublic() {
        return !TextUtils.isEmpty(themedWallpaperLink);
    }

    public static boolean hasWallpaperFromTheme() {
        ThemeInfo themeInfo = currentTheme;
        if (themeInfo.firstAccentIsDefault && themeInfo.currentAccentId == DEFALT_THEME_ACCENT_ID) {
            return false;
        }
        if (currentColors.containsKey("chat_wallpaper") || themedWallpaperFileOffset > 0 || !TextUtils.isEmpty(themedWallpaperLink)) {
            return true;
        }
        return false;
    }

    public static boolean isCustomTheme() {
        return isCustomTheme;
    }

    public static void reloadWallpaper() {
        BackgroundGradientDrawable.Disposable disposable = backgroundGradientDisposable;
        if (disposable != null) {
            disposable.dispose();
            backgroundGradientDisposable = null;
        }
        wallpaper = null;
        themedWallpaper = null;
        loadWallpaper();
    }

    private static void calcBackgroundColor(Drawable drawable, int i) {
        if (i != 2) {
            int[] calcDrawableColor = AndroidUtilities.calcDrawableColor(drawable);
            int i2 = calcDrawableColor[0];
            serviceMessageColorBackup = i2;
            serviceMessageColor = i2;
            int i3 = calcDrawableColor[1];
            serviceSelectedMessageColorBackup = i3;
            serviceSelectedMessageColor = i3;
            serviceMessage2Color = calcDrawableColor[2];
            serviceSelectedMessage2Color = calcDrawableColor[3];
        }
    }

    public static int getServiceMessageColor() {
        Integer num = currentColors.get("chat_serviceBackground");
        return num == null ? serviceMessageColor : num.intValue();
    }

    public static void loadWallpaper() {
        File file;
        boolean z;
        if (wallpaper == null) {
            ThemeInfo themeInfo = currentTheme;
            boolean z2 = themeInfo.firstAccentIsDefault && themeInfo.currentAccentId == DEFALT_THEME_ACCENT_ID;
            ThemeAccent accent = currentTheme.getAccent(false);
            if (accent == null || hasPreviousTheme) {
                file = null;
                z = false;
            } else {
                file = accent.getPathToWallpaper();
                z = accent.patternMotion;
            }
            Utilities.searchQueue.postRunnable(new Runnable(z2, file, z) {
                private final /* synthetic */ boolean f$1;
                private final /* synthetic */ File f$2;
                private final /* synthetic */ boolean f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    Theme.lambda$loadWallpaper$8(Theme.OverrideWallpaperInfo.this, this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(7:(1:81)(1:82)|83|(3:85|86|(1:88)(5:89|(1:(4:98|(1:102)|103|(1:105))(2:94|(1:96)(1:97)))|107|108|(2:(1:111)|112)))|106|107|108|(0)) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:107:0x01f1 */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x01f5 A[Catch:{ all -> 0x0069 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$loadWallpaper$8(org.telegram.ui.ActionBar.Theme.OverrideWallpaperInfo r7, boolean r8, java.io.File r9, boolean r10) {
        /*
            java.lang.Object r0 = wallpaperSync
            monitor-enter(r0)
            boolean r1 = hasPreviousTheme     // Catch:{ all -> 0x020d }
            r2 = 0
            r3 = 1
            if (r1 == 0) goto L_0x000d
            boolean r1 = isApplyingAccent     // Catch:{ all -> 0x020d }
            if (r1 == 0) goto L_0x0011
        L_0x000d:
            if (r7 == 0) goto L_0x0011
            r1 = 1
            goto L_0x0012
        L_0x0011:
            r1 = 0
        L_0x0012:
            if (r7 == 0) goto L_0x002b
            if (r7 == 0) goto L_0x001c
            boolean r4 = r7.isMotion     // Catch:{ all -> 0x020d }
            if (r4 == 0) goto L_0x001c
            r4 = 1
            goto L_0x001d
        L_0x001c:
            r4 = 0
        L_0x001d:
            isWallpaperMotion = r4     // Catch:{ all -> 0x020d }
            if (r7 == 0) goto L_0x0027
            int r4 = r7.color     // Catch:{ all -> 0x020d }
            if (r4 == 0) goto L_0x0027
            r4 = 1
            goto L_0x0028
        L_0x0027:
            r4 = 0
        L_0x0028:
            isPatternWallpaper = r4     // Catch:{ all -> 0x020d }
            goto L_0x003c
        L_0x002b:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = currentTheme     // Catch:{ all -> 0x020d }
            boolean r4 = r4.isMotion     // Catch:{ all -> 0x020d }
            isWallpaperMotion = r4     // Catch:{ all -> 0x020d }
            org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = currentTheme     // Catch:{ all -> 0x020d }
            int r4 = r4.patternBgColor     // Catch:{ all -> 0x020d }
            if (r4 == 0) goto L_0x0039
            r4 = 1
            goto L_0x003a
        L_0x0039:
            r4 = 0
        L_0x003a:
            isPatternWallpaper = r4     // Catch:{ all -> 0x020d }
        L_0x003c:
            r4 = 100
            r6 = 2
            if (r1 != 0) goto L_0x0157
            if (r8 == 0) goto L_0x0045
            r8 = 0
            goto L_0x004f
        L_0x0045:
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = currentColors     // Catch:{ all -> 0x020d }
            java.lang.String r1 = "chat_wallpaper"
            java.lang.Object r8 = r8.get(r1)     // Catch:{ all -> 0x020d }
            java.lang.Integer r8 = (java.lang.Integer) r8     // Catch:{ all -> 0x020d }
        L_0x004f:
            if (r9 == 0) goto L_0x006f
            boolean r1 = r9.exists()     // Catch:{ all -> 0x020d }
            if (r1 == 0) goto L_0x006f
            java.lang.String r8 = r9.getAbsolutePath()     // Catch:{ all -> 0x0069 }
            android.graphics.drawable.Drawable r8 = android.graphics.drawable.Drawable.createFromPath(r8)     // Catch:{ all -> 0x0069 }
            wallpaper = r8     // Catch:{ all -> 0x0069 }
            isWallpaperMotion = r10     // Catch:{ all -> 0x0069 }
            isCustomTheme = r3     // Catch:{ all -> 0x0069 }
            isPatternWallpaper = r3     // Catch:{ all -> 0x0069 }
            goto L_0x0157
        L_0x0069:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)     // Catch:{ all -> 0x020d }
            goto L_0x0157
        L_0x006f:
            if (r8 == 0) goto L_0x00d2
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = currentColors     // Catch:{ all -> 0x020d }
            java.lang.String r10 = "chat_wallpaper_gradient_to"
            java.lang.Object r9 = r9.get(r10)     // Catch:{ all -> 0x020d }
            java.lang.Integer r9 = (java.lang.Integer) r9     // Catch:{ all -> 0x020d }
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = currentColors     // Catch:{ all -> 0x020d }
            java.lang.String r1 = "chat_wallpaper_gradient_rotation"
            java.lang.Object r10 = r10.get(r1)     // Catch:{ all -> 0x020d }
            java.lang.Integer r10 = (java.lang.Integer) r10     // Catch:{ all -> 0x020d }
            if (r10 != 0) goto L_0x008d
            r10 = 45
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ all -> 0x020d }
        L_0x008d:
            if (r9 == 0) goto L_0x00c3
            boolean r1 = r9.equals(r8)     // Catch:{ all -> 0x020d }
            if (r1 == 0) goto L_0x0096
            goto L_0x00c3
        L_0x0096:
            int[] r1 = new int[r6]     // Catch:{ all -> 0x020d }
            int r8 = r8.intValue()     // Catch:{ all -> 0x020d }
            r1[r2] = r8     // Catch:{ all -> 0x020d }
            int r8 = r9.intValue()     // Catch:{ all -> 0x020d }
            r1[r3] = r8     // Catch:{ all -> 0x020d }
            int r8 = r10.intValue()     // Catch:{ all -> 0x020d }
            android.graphics.drawable.GradientDrawable$Orientation r8 = org.telegram.ui.Components.BackgroundGradientDrawable.getGradientOrientation(r8)     // Catch:{ all -> 0x020d }
            org.telegram.ui.Components.BackgroundGradientDrawable r9 = new org.telegram.ui.Components.BackgroundGradientDrawable     // Catch:{ all -> 0x020d }
            r9.<init>(r8, r1)     // Catch:{ all -> 0x020d }
            org.telegram.ui.ActionBar.Theme$9 r8 = new org.telegram.ui.ActionBar.Theme$9     // Catch:{ all -> 0x020d }
            r8.<init>()     // Catch:{ all -> 0x020d }
            org.telegram.ui.Components.BackgroundGradientDrawable$Sizes r10 = org.telegram.ui.Components.BackgroundGradientDrawable.Sizes.ofDeviceScreen()     // Catch:{ all -> 0x020d }
            org.telegram.ui.Components.BackgroundGradientDrawable$Disposable r8 = r9.startDithering(r10, r8, r4)     // Catch:{ all -> 0x020d }
            backgroundGradientDisposable = r8     // Catch:{ all -> 0x020d }
            wallpaper = r9     // Catch:{ all -> 0x020d }
            goto L_0x00ce
        L_0x00c3:
            android.graphics.drawable.ColorDrawable r9 = new android.graphics.drawable.ColorDrawable     // Catch:{ all -> 0x020d }
            int r8 = r8.intValue()     // Catch:{ all -> 0x020d }
            r9.<init>(r8)     // Catch:{ all -> 0x020d }
            wallpaper = r9     // Catch:{ all -> 0x020d }
        L_0x00ce:
            isCustomTheme = r3     // Catch:{ all -> 0x020d }
            goto L_0x0157
        L_0x00d2:
            java.lang.String r8 = themedWallpaperLink     // Catch:{ all -> 0x020d }
            if (r8 == 0) goto L_0x0112
            java.io.File r8 = new java.io.File     // Catch:{ Exception -> 0x010d }
            java.io.File r9 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ Exception -> 0x010d }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x010d }
            r10.<init>()     // Catch:{ Exception -> 0x010d }
            java.lang.String r1 = themedWallpaperLink     // Catch:{ Exception -> 0x010d }
            java.lang.String r1 = org.telegram.messenger.Utilities.MD5(r1)     // Catch:{ Exception -> 0x010d }
            r10.append(r1)     // Catch:{ Exception -> 0x010d }
            java.lang.String r1 = ".wp"
            r10.append(r1)     // Catch:{ Exception -> 0x010d }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x010d }
            r8.<init>(r9, r10)     // Catch:{ Exception -> 0x010d }
            java.io.FileInputStream r9 = new java.io.FileInputStream     // Catch:{ Exception -> 0x010d }
            r9.<init>(r8)     // Catch:{ Exception -> 0x010d }
            android.graphics.Bitmap r8 = loadScreenSizedBitmap(r9, r2)     // Catch:{ Exception -> 0x010d }
            if (r8 == 0) goto L_0x0157
            android.graphics.drawable.BitmapDrawable r9 = new android.graphics.drawable.BitmapDrawable     // Catch:{ Exception -> 0x010d }
            r9.<init>(r8)     // Catch:{ Exception -> 0x010d }
            wallpaper = r9     // Catch:{ Exception -> 0x010d }
            themedWallpaper = r9     // Catch:{ Exception -> 0x010d }
            isCustomTheme = r3     // Catch:{ Exception -> 0x010d }
            goto L_0x0157
        L_0x010d:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)     // Catch:{ all -> 0x020d }
            goto L_0x0157
        L_0x0112:
            int r8 = themedWallpaperFileOffset     // Catch:{ all -> 0x020d }
            if (r8 <= 0) goto L_0x0157
            org.telegram.ui.ActionBar.Theme$ThemeInfo r8 = currentTheme     // Catch:{ all -> 0x020d }
            java.lang.String r8 = r8.pathToFile     // Catch:{ all -> 0x020d }
            if (r8 != 0) goto L_0x0122
            org.telegram.ui.ActionBar.Theme$ThemeInfo r8 = currentTheme     // Catch:{ all -> 0x020d }
            java.lang.String r8 = r8.assetName     // Catch:{ all -> 0x020d }
            if (r8 == 0) goto L_0x0157
        L_0x0122:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r8 = currentTheme     // Catch:{ all -> 0x0153 }
            java.lang.String r8 = r8.assetName     // Catch:{ all -> 0x0153 }
            if (r8 == 0) goto L_0x0131
            org.telegram.ui.ActionBar.Theme$ThemeInfo r8 = currentTheme     // Catch:{ all -> 0x0153 }
            java.lang.String r8 = r8.assetName     // Catch:{ all -> 0x0153 }
            java.io.File r8 = getAssetFile(r8)     // Catch:{ all -> 0x0153 }
            goto L_0x013a
        L_0x0131:
            java.io.File r8 = new java.io.File     // Catch:{ all -> 0x0153 }
            org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = currentTheme     // Catch:{ all -> 0x0153 }
            java.lang.String r9 = r9.pathToFile     // Catch:{ all -> 0x0153 }
            r8.<init>(r9)     // Catch:{ all -> 0x0153 }
        L_0x013a:
            java.io.FileInputStream r9 = new java.io.FileInputStream     // Catch:{ all -> 0x0153 }
            r9.<init>(r8)     // Catch:{ all -> 0x0153 }
            int r8 = themedWallpaperFileOffset     // Catch:{ all -> 0x0153 }
            android.graphics.Bitmap r8 = loadScreenSizedBitmap(r9, r8)     // Catch:{ all -> 0x0153 }
            if (r8 == 0) goto L_0x0157
            android.graphics.drawable.BitmapDrawable r9 = new android.graphics.drawable.BitmapDrawable     // Catch:{ all -> 0x0153 }
            r9.<init>(r8)     // Catch:{ all -> 0x0153 }
            wallpaper = r9     // Catch:{ all -> 0x0153 }
            themedWallpaper = r9     // Catch:{ all -> 0x0153 }
            isCustomTheme = r3     // Catch:{ all -> 0x0153 }
            goto L_0x0157
        L_0x0153:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)     // Catch:{ all -> 0x020d }
        L_0x0157:
            android.graphics.drawable.Drawable r8 = wallpaper     // Catch:{ all -> 0x020d }
            if (r8 != 0) goto L_0x0201
            if (r7 == 0) goto L_0x0160
            int r8 = r7.color     // Catch:{ all -> 0x020d }
            goto L_0x0161
        L_0x0160:
            r8 = 0
        L_0x0161:
            r9 = 2131165280(0x7var_, float:1.7944773E38)
            if (r7 == 0) goto L_0x01e3
            boolean r10 = r7.isDefault()     // Catch:{ all -> 0x01f1 }
            if (r10 == 0) goto L_0x016e
            goto L_0x01e3
        L_0x016e:
            boolean r10 = r7.isColor()     // Catch:{ all -> 0x01f1 }
            if (r10 != 0) goto L_0x01f1
            if (r8 == 0) goto L_0x01ab
            boolean r10 = isPatternWallpaper     // Catch:{ all -> 0x01f1 }
            if (r10 != 0) goto L_0x01ab
            int r9 = r7.gradientColor     // Catch:{ all -> 0x01f1 }
            if (r9 == 0) goto L_0x01a3
            int[] r9 = new int[r6]     // Catch:{ all -> 0x01f1 }
            r9[r2] = r8     // Catch:{ all -> 0x01f1 }
            int r10 = r7.gradientColor     // Catch:{ all -> 0x01f1 }
            r9[r3] = r10     // Catch:{ all -> 0x01f1 }
            int r7 = r7.rotation     // Catch:{ all -> 0x01f1 }
            android.graphics.drawable.GradientDrawable$Orientation r7 = org.telegram.ui.Components.BackgroundGradientDrawable.getGradientOrientation(r7)     // Catch:{ all -> 0x01f1 }
            org.telegram.ui.Components.BackgroundGradientDrawable r10 = new org.telegram.ui.Components.BackgroundGradientDrawable     // Catch:{ all -> 0x01f1 }
            r10.<init>(r7, r9)     // Catch:{ all -> 0x01f1 }
            org.telegram.ui.ActionBar.Theme$10 r7 = new org.telegram.ui.ActionBar.Theme$10     // Catch:{ all -> 0x01f1 }
            r7.<init>()     // Catch:{ all -> 0x01f1 }
            org.telegram.ui.Components.BackgroundGradientDrawable$Sizes r9 = org.telegram.ui.Components.BackgroundGradientDrawable.Sizes.ofDeviceScreen()     // Catch:{ all -> 0x01f1 }
            org.telegram.ui.Components.BackgroundGradientDrawable$Disposable r7 = r10.startDithering(r9, r7, r4)     // Catch:{ all -> 0x01f1 }
            backgroundGradientDisposable = r7     // Catch:{ all -> 0x01f1 }
            wallpaper = r10     // Catch:{ all -> 0x01f1 }
            goto L_0x01f1
        L_0x01a3:
            android.graphics.drawable.ColorDrawable r7 = new android.graphics.drawable.ColorDrawable     // Catch:{ all -> 0x01f1 }
            r7.<init>(r8)     // Catch:{ all -> 0x01f1 }
            wallpaper = r7     // Catch:{ all -> 0x01f1 }
            goto L_0x01f1
        L_0x01ab:
            java.io.File r10 = new java.io.File     // Catch:{ all -> 0x01f1 }
            java.io.File r1 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ all -> 0x01f1 }
            java.lang.String r7 = r7.fileName     // Catch:{ all -> 0x01f1 }
            r10.<init>(r1, r7)     // Catch:{ all -> 0x01f1 }
            boolean r7 = r10.exists()     // Catch:{ all -> 0x01f1 }
            if (r7 == 0) goto L_0x01d0
            java.io.FileInputStream r7 = new java.io.FileInputStream     // Catch:{ all -> 0x01f1 }
            r7.<init>(r10)     // Catch:{ all -> 0x01f1 }
            android.graphics.Bitmap r7 = loadScreenSizedBitmap(r7, r2)     // Catch:{ all -> 0x01f1 }
            if (r7 == 0) goto L_0x01d0
            android.graphics.drawable.BitmapDrawable r10 = new android.graphics.drawable.BitmapDrawable     // Catch:{ all -> 0x01f1 }
            r10.<init>(r7)     // Catch:{ all -> 0x01f1 }
            wallpaper = r10     // Catch:{ all -> 0x01f1 }
            isCustomTheme = r3     // Catch:{ all -> 0x01f1 }
        L_0x01d0:
            android.graphics.drawable.Drawable r7 = wallpaper     // Catch:{ all -> 0x01f1 }
            if (r7 != 0) goto L_0x01f1
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x01f1 }
            android.content.res.Resources r7 = r7.getResources()     // Catch:{ all -> 0x01f1 }
            android.graphics.drawable.Drawable r7 = r7.getDrawable(r9)     // Catch:{ all -> 0x01f1 }
            wallpaper = r7     // Catch:{ all -> 0x01f1 }
            isCustomTheme = r2     // Catch:{ all -> 0x01f1 }
            goto L_0x01f1
        L_0x01e3:
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x01f1 }
            android.content.res.Resources r7 = r7.getResources()     // Catch:{ all -> 0x01f1 }
            android.graphics.drawable.Drawable r7 = r7.getDrawable(r9)     // Catch:{ all -> 0x01f1 }
            wallpaper = r7     // Catch:{ all -> 0x01f1 }
            isCustomTheme = r2     // Catch:{ all -> 0x01f1 }
        L_0x01f1:
            android.graphics.drawable.Drawable r7 = wallpaper     // Catch:{ all -> 0x020d }
            if (r7 != 0) goto L_0x0201
            if (r8 != 0) goto L_0x01fa
            r8 = -2693905(0xffffffffffd6e4ef, float:NaN)
        L_0x01fa:
            android.graphics.drawable.ColorDrawable r7 = new android.graphics.drawable.ColorDrawable     // Catch:{ all -> 0x020d }
            r7.<init>(r8)     // Catch:{ all -> 0x020d }
            wallpaper = r7     // Catch:{ all -> 0x020d }
        L_0x0201:
            android.graphics.drawable.Drawable r7 = wallpaper     // Catch:{ all -> 0x020d }
            calcBackgroundColor(r7, r3)     // Catch:{ all -> 0x020d }
            org.telegram.ui.ActionBar.-$$Lambda$Theme$rcJuxvar_iEk3SUZFDSBFoukj5k8 r7 = org.telegram.ui.ActionBar.$$Lambda$Theme$rcJuxvar_iEk3SUZFDSBFoukj5k8.INSTANCE     // Catch:{ all -> 0x020d }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r7)     // Catch:{ all -> 0x020d }
            monitor-exit(r0)     // Catch:{ all -> 0x020d }
            return
        L_0x020d:
            r7 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x020d }
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.lambda$loadWallpaper$8(org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo, boolean, java.io.File, boolean):void");
    }

    static /* synthetic */ void lambda$null$7() {
        applyChatServiceMessageColor();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewWallpapper, new Object[0]);
    }

    /* access modifiers changed from: private */
    public static Bitmap loadScreenSizedBitmap(FileInputStream fileInputStream, int i) {
        float f;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            int i2 = 1;
            options.inSampleSize = 1;
            options.inJustDecodeBounds = true;
            long j = (long) i;
            fileInputStream.getChannel().position(j);
            BitmapFactory.decodeStream(fileInputStream, (Rect) null, options);
            float f2 = (float) options.outWidth;
            float f3 = (float) options.outHeight;
            int dp = AndroidUtilities.dp(360.0f);
            int dp2 = AndroidUtilities.dp(640.0f);
            if (dp < dp2 || f2 <= f3) {
                f = Math.min(f2 / ((float) dp), f3 / ((float) dp2));
            } else {
                f = Math.max(f2 / ((float) dp), f3 / ((float) dp2));
            }
            if (f < 1.2f) {
                f = 1.0f;
            }
            options.inJustDecodeBounds = false;
            if (f <= 1.0f || (f2 <= ((float) dp) && f3 <= ((float) dp2))) {
                options.inSampleSize = (int) f;
            } else {
                do {
                    i2 *= 2;
                } while (((float) (i2 * 2)) < f);
                options.inSampleSize = i2;
            }
            fileInputStream.getChannel().position(j);
            Bitmap decodeStream = BitmapFactory.decodeStream(fileInputStream, (Rect) null, options);
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (Exception unused) {
                }
            }
            return decodeStream;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (Exception unused2) {
                }
            }
            return null;
        } catch (Throwable th) {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (Exception unused3) {
                }
            }
            throw th;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:42:0x00bb A[SYNTHETIC, Splitter:B:42:0x00bb] */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0112 A[SYNTHETIC, Splitter:B:69:0x0112] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.drawable.Drawable getThemedWallpaper(final boolean r8, final android.view.View r9) {
        /*
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = currentColors
            java.lang.String r1 = "chat_wallpaper"
            java.lang.Object r0 = r0.get(r1)
            java.lang.Integer r0 = (java.lang.Integer) r0
            r1 = 1
            r2 = 0
            r3 = 0
            if (r0 == 0) goto L_0x0091
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = currentColors
            java.lang.String r5 = "chat_wallpaper_gradient_to"
            java.lang.Object r4 = r4.get(r5)
            java.lang.Integer r4 = (java.lang.Integer) r4
            java.util.HashMap<java.lang.String, java.lang.Integer> r5 = currentColors
            java.lang.String r6 = "chat_wallpaper_gradient_rotation"
            java.lang.Object r5 = r5.get(r6)
            java.lang.Integer r5 = (java.lang.Integer) r5
            if (r5 != 0) goto L_0x002b
            r5 = 45
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
        L_0x002b:
            if (r4 != 0) goto L_0x0037
            android.graphics.drawable.ColorDrawable r8 = new android.graphics.drawable.ColorDrawable
            int r9 = r0.intValue()
            r8.<init>(r9)
            return r8
        L_0x0037:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r6 = currentTheme
            org.telegram.ui.ActionBar.Theme$ThemeAccent r6 = r6.getAccent(r2)
            if (r6 == 0) goto L_0x0058
            java.lang.String r7 = r6.patternSlug
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 != 0) goto L_0x0058
            org.telegram.ui.ActionBar.Theme$ThemeInfo r7 = previousTheme
            if (r7 != 0) goto L_0x0058
            java.io.File r6 = r6.getPathToWallpaper()
            if (r6 == 0) goto L_0x0058
            boolean r7 = r6.exists()
            if (r7 == 0) goto L_0x0058
            goto L_0x0059
        L_0x0058:
            r6 = r3
        L_0x0059:
            if (r6 != 0) goto L_0x00b8
            r6 = 2
            int[] r6 = new int[r6]
            int r0 = r0.intValue()
            r6[r2] = r0
            int r0 = r4.intValue()
            r6[r1] = r0
            int r0 = r5.intValue()
            android.graphics.drawable.GradientDrawable$Orientation r0 = org.telegram.ui.Components.BackgroundGradientDrawable.getGradientOrientation(r0)
            org.telegram.ui.Components.BackgroundGradientDrawable r1 = new org.telegram.ui.Components.BackgroundGradientDrawable
            r1.<init>(r0, r6)
            if (r8 != 0) goto L_0x007e
            org.telegram.ui.Components.BackgroundGradientDrawable$Sizes r0 = org.telegram.ui.Components.BackgroundGradientDrawable.Sizes.ofDeviceScreen()
            goto L_0x0086
        L_0x007e:
            r0 = 1040187392(0x3e000000, float:0.125)
            org.telegram.ui.Components.BackgroundGradientDrawable$Sizes$Orientation r2 = org.telegram.ui.Components.BackgroundGradientDrawable.Sizes.Orientation.PORTRAIT
            org.telegram.ui.Components.BackgroundGradientDrawable$Sizes r0 = org.telegram.ui.Components.BackgroundGradientDrawable.Sizes.ofDeviceScreen(r0, r2)
        L_0x0086:
            if (r9 == 0) goto L_0x008d
            org.telegram.ui.ActionBar.Theme$11 r3 = new org.telegram.ui.ActionBar.Theme$11
            r3.<init>(r8, r9)
        L_0x008d:
            r1.startDithering(r0, r3)
            return r1
        L_0x0091:
            int r9 = themedWallpaperFileOffset
            if (r9 <= 0) goto L_0x00b7
            org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = currentTheme
            java.lang.String r0 = r9.pathToFile
            if (r0 != 0) goto L_0x009f
            java.lang.String r9 = r9.assetName
            if (r9 == 0) goto L_0x00b7
        L_0x009f:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = currentTheme
            java.lang.String r9 = r9.assetName
            if (r9 == 0) goto L_0x00aa
            java.io.File r9 = getAssetFile(r9)
            goto L_0x00b3
        L_0x00aa:
            java.io.File r9 = new java.io.File
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = currentTheme
            java.lang.String r0 = r0.pathToFile
            r9.<init>(r0)
        L_0x00b3:
            r6 = r9
            int r9 = themedWallpaperFileOffset
            goto L_0x00b9
        L_0x00b7:
            r6 = r3
        L_0x00b8:
            r9 = 0
        L_0x00b9:
            if (r6 == 0) goto L_0x0127
            java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x010b }
            r0.<init>(r6)     // Catch:{ all -> 0x010b }
            java.nio.channels.FileChannel r4 = r0.getChannel()     // Catch:{ all -> 0x0109 }
            long r5 = (long) r9     // Catch:{ all -> 0x0109 }
            r4.position(r5)     // Catch:{ all -> 0x0109 }
            android.graphics.BitmapFactory$Options r9 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0109 }
            r9.<init>()     // Catch:{ all -> 0x0109 }
            if (r8 == 0) goto L_0x00ed
            r9.inJustDecodeBounds = r1     // Catch:{ all -> 0x0109 }
            int r8 = r9.outWidth     // Catch:{ all -> 0x0109 }
            float r8 = (float) r8     // Catch:{ all -> 0x0109 }
            int r4 = r9.outHeight     // Catch:{ all -> 0x0109 }
            float r4 = (float) r4     // Catch:{ all -> 0x0109 }
            r5 = 1120403456(0x42CLASSNAME, float:100.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)     // Catch:{ all -> 0x0109 }
        L_0x00dd:
            float r6 = (float) r5     // Catch:{ all -> 0x0109 }
            int r7 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r7 > 0) goto L_0x00e6
            int r6 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r6 <= 0) goto L_0x00ed
        L_0x00e6:
            int r1 = r1 * 2
            r6 = 1073741824(0x40000000, float:2.0)
            float r8 = r8 / r6
            float r4 = r4 / r6
            goto L_0x00dd
        L_0x00ed:
            r9.inJustDecodeBounds = r2     // Catch:{ all -> 0x0109 }
            r9.inSampleSize = r1     // Catch:{ all -> 0x0109 }
            android.graphics.Bitmap r8 = android.graphics.BitmapFactory.decodeStream(r0, r3, r9)     // Catch:{ all -> 0x0109 }
            if (r8 == 0) goto L_0x0105
            android.graphics.drawable.BitmapDrawable r9 = new android.graphics.drawable.BitmapDrawable     // Catch:{ all -> 0x0109 }
            r9.<init>(r8)     // Catch:{ all -> 0x0109 }
            r0.close()     // Catch:{ Exception -> 0x0100 }
            goto L_0x0104
        L_0x0100:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x0104:
            return r9
        L_0x0105:
            r0.close()     // Catch:{ Exception -> 0x0116 }
            goto L_0x0127
        L_0x0109:
            r8 = move-exception
            goto L_0x010d
        L_0x010b:
            r8 = move-exception
            r0 = r3
        L_0x010d:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)     // Catch:{ all -> 0x011b }
            if (r0 == 0) goto L_0x0127
            r0.close()     // Catch:{ Exception -> 0x0116 }
            goto L_0x0127
        L_0x0116:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
            goto L_0x0127
        L_0x011b:
            r8 = move-exception
            if (r0 == 0) goto L_0x0126
            r0.close()     // Catch:{ Exception -> 0x0122 }
            goto L_0x0126
        L_0x0122:
            r9 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r9)
        L_0x0126:
            throw r8
        L_0x0127:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.getThemedWallpaper(boolean, android.view.View):android.graphics.drawable.Drawable");
    }

    public static String getSelectedBackgroundSlug() {
        OverrideWallpaperInfo overrideWallpaperInfo = currentTheme.overrideWallpaper;
        if (overrideWallpaperInfo != null) {
            return overrideWallpaperInfo.slug;
        }
        return hasWallpaperFromTheme() ? "t" : "d";
    }

    public static Drawable getCachedWallpaper() {
        synchronized (wallpaperSync) {
            if (themedWallpaper != null) {
                Drawable drawable = themedWallpaper;
                return drawable;
            }
            Drawable drawable2 = wallpaper;
            return drawable2;
        }
    }

    public static Drawable getCachedWallpaperNonBlocking() {
        Drawable drawable = themedWallpaper;
        if (drawable != null) {
            return drawable;
        }
        return wallpaper;
    }

    public static boolean isWallpaperMotion() {
        return isWallpaperMotion;
    }

    public static boolean isPatternWallpaper() {
        return isPatternWallpaper;
    }
}
