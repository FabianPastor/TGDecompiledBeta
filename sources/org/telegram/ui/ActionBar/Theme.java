package org.telegram.ui.ActionBar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
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
import android.view.View;
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
import java.util.concurrent.CountDownLatch;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
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
import org.telegram.ui.Components.AudioVisualizerDrawable;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.FragmentContextViewWavesDrawable;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.MsgClockDrawable;
import org.telegram.ui.Components.PathAnimator;
import org.telegram.ui.Components.PlayingGameDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RecordStatusDrawable;
import org.telegram.ui.Components.RoundStatusDrawable;
import org.telegram.ui.Components.ScamDrawable;
import org.telegram.ui.Components.SendingFileDrawable;
import org.telegram.ui.Components.StatusDrawable;
import org.telegram.ui.Components.TypingDotsDrawable;

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
    private static HashMap<MessageObject, AudioVisualizerDrawable> animatedOutVisualizerDrawables = null;
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
    public static Drawable[] avatarDrawables = new Drawable[12];
    public static Paint avatar_backgroundPaint = null;
    private static BackgroundGradientDrawable.Disposable backgroundGradientDisposable = null;
    public static Drawable calllog_msgCallDownGreenDrawable = null;
    public static Drawable calllog_msgCallDownRedDrawable = null;
    public static Drawable calllog_msgCallUpGreenDrawable = null;
    public static Drawable calllog_msgCallUpRedDrawable = null;
    private static boolean canStartHolidayAnimation = false;
    private static boolean changingWallpaper = false;
    public static Paint chat_actionBackgroundGradientDarkenPaint = null;
    public static Paint chat_actionBackgroundPaint = null;
    public static Paint chat_actionBackgroundPaint2 = null;
    public static Paint chat_actionBackgroundSelectedPaint = null;
    public static Paint chat_actionBackgroundSelectedPaint2 = null;
    public static TextPaint chat_actionTextPaint = null;
    public static TextPaint chat_adminPaint = null;
    public static RLottieDrawable[] chat_attachButtonDrawables = new RLottieDrawable[6];
    public static Drawable chat_attachEmptyDrawable = null;
    public static TextPaint chat_audioPerformerPaint = null;
    public static TextPaint chat_audioTimePaint = null;
    public static TextPaint chat_audioTitlePaint = null;
    public static TextPaint chat_botButtonPaint = null;
    public static Drawable chat_botCardDrawalbe = null;
    public static Drawable chat_botInlineDrawable = null;
    public static Drawable chat_botLinkDrawalbe = null;
    public static Paint chat_botProgressPaint = null;
    public static Drawable chat_commentArrowDrawable = null;
    public static Drawable chat_commentDrawable = null;
    public static Drawable chat_commentStickerDrawable = null;
    public static Paint chat_composeBackgroundPaint = null;
    public static Drawable chat_composeShadowDrawable = null;
    public static Drawable[] chat_contactDrawable = new Drawable[2];
    public static TextPaint chat_contactNamePaint = null;
    public static TextPaint chat_contactPhonePaint = null;
    public static TextPaint chat_contextResult_descriptionTextPaint = null;
    public static Drawable chat_contextResult_shadowUnderSwitchDrawable = null;
    public static TextPaint chat_contextResult_titleTextPaint = null;
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
    private static AudioVisualizerDrawable chat_msgAudioVisualizeDrawable = null;
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
    public static Drawable[] chat_msgInCallDrawable = new Drawable[2];
    public static Drawable[] chat_msgInCallSelectedDrawable = new Drawable[2];
    public static Drawable chat_msgInClockDrawable = null;
    public static MessageDrawable chat_msgInDrawable = null;
    public static Drawable chat_msgInInstantDrawable = null;
    public static MessageDrawable chat_msgInMediaDrawable = null;
    public static MessageDrawable chat_msgInMediaSelectedDrawable = null;
    public static Drawable chat_msgInMenuDrawable = null;
    public static Drawable chat_msgInMenuSelectedDrawable = null;
    public static Drawable chat_msgInPinnedDrawable = null;
    public static Drawable chat_msgInPinnedSelectedDrawable = null;
    public static Drawable chat_msgInRepliesDrawable = null;
    public static Drawable chat_msgInRepliesSelectedDrawable = null;
    public static Drawable chat_msgInSelectedClockDrawable = null;
    public static MessageDrawable chat_msgInSelectedDrawable = null;
    public static Drawable chat_msgInViewsDrawable = null;
    public static Drawable chat_msgInViewsSelectedDrawable = null;
    public static Drawable chat_msgMediaCheckDrawable = null;
    public static Drawable chat_msgMediaClockDrawable = null;
    public static Drawable chat_msgMediaHalfCheckDrawable = null;
    public static Drawable chat_msgMediaMenuDrawable = null;
    public static Drawable chat_msgMediaPinnedDrawable = null;
    public static Drawable chat_msgMediaRepliesDrawable = null;
    public static Drawable chat_msgMediaViewsDrawable = null;
    public static Drawable chat_msgNoSoundDrawable = null;
    public static Drawable[] chat_msgOutCallDrawable = new Drawable[2];
    public static Drawable[] chat_msgOutCallSelectedDrawable = new Drawable[2];
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
    public static Drawable chat_msgOutPinnedDrawable = null;
    public static Drawable chat_msgOutPinnedSelectedDrawable = null;
    public static Drawable chat_msgOutRepliesDrawable = null;
    public static Drawable chat_msgOutRepliesSelectedDrawable = null;
    public static Drawable chat_msgOutSelectedClockDrawable = null;
    public static MessageDrawable chat_msgOutSelectedDrawable = null;
    public static Drawable chat_msgOutViewsDrawable = null;
    public static Drawable chat_msgOutViewsSelectedDrawable = null;
    public static Drawable chat_msgStickerCheckDrawable = null;
    public static Drawable chat_msgStickerClockDrawable = null;
    public static Drawable chat_msgStickerHalfCheckDrawable = null;
    public static Drawable chat_msgStickerPinnedDrawable = null;
    public static Drawable chat_msgStickerRepliesDrawable = null;
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
    public static Drawable[] chat_pollHintDrawable = new Drawable[2];
    public static Paint chat_pollTimerPaint = null;
    public static Drawable[] chat_psaHelpDrawable = new Drawable[2];
    public static Paint chat_radialProgress2Paint = null;
    public static Paint chat_radialProgressPaint = null;
    public static Drawable chat_redLocationIcon = null;
    public static Drawable chat_replyIconDrawable = null;
    public static Paint chat_replyLinePaint = null;
    public static TextPaint chat_replyNamePaint = null;
    public static TextPaint chat_replyTextPaint = null;
    public static Drawable chat_roundVideoShadow = null;
    public static Drawable chat_shareIconDrawable = null;
    public static TextPaint chat_shipmentPaint = null;
    public static Paint chat_statusPaint = null;
    public static Paint chat_statusRecordPaint = null;
    private static StatusDrawable[] chat_status_drawables = new StatusDrawable[5];
    public static TextPaint chat_stickerCommentCountPaint = null;
    public static Paint chat_textSearchSelectionPaint = null;
    public static Paint chat_timeBackgroundPaint = null;
    public static TextPaint chat_timePaint = null;
    public static Path[] chat_updatePath = new Path[3];
    public static Paint chat_urlPaint = null;
    public static Paint checkboxSquare_backgroundPaint = null;
    public static Paint checkboxSquare_checkPaint = null;
    public static Paint checkboxSquare_eraserPaint = null;
    public static int currentColor = 0;
    /* access modifiers changed from: private */
    public static HashMap<String, Integer> currentColors = new HashMap<>();
    private static HashMap<String, Integer> currentColorsNoAccent = new HashMap<>();
    private static ThemeInfo currentDayTheme = null;
    /* access modifiers changed from: private */
    public static ThemeInfo currentNightTheme = null;
    /* access modifiers changed from: private */
    public static ThemeInfo currentTheme = null;
    /* access modifiers changed from: private */
    public static HashMap<String, Integer> defaultColors = new HashMap<>();
    private static ThemeInfo defaultTheme = null;
    public static Paint dialogs_actionMessagePaint = null;
    public static RLottieDrawable dialogs_archiveAvatarDrawable = null;
    public static boolean dialogs_archiveAvatarDrawableRecolored = false;
    public static RLottieDrawable dialogs_archiveDrawable = null;
    public static boolean dialogs_archiveDrawableRecolored = false;
    public static TextPaint dialogs_archiveTextPaint = null;
    public static TextPaint dialogs_archiveTextPaintSmall = null;
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
    public static ScamDrawable dialogs_fakeDrawable = null;
    public static Drawable dialogs_groupDrawable = null;
    public static Drawable dialogs_halfCheckDrawable = null;
    public static RLottieDrawable dialogs_hidePsaDrawable = null;
    public static boolean dialogs_hidePsaDrawableRecolored = false;
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
    public static Drawable dialogs_playDrawable = null;
    public static Drawable dialogs_reorderDrawable = null;
    public static ScamDrawable dialogs_scamDrawable = null;
    public static TextPaint dialogs_searchNameEncryptedPaint = null;
    public static TextPaint dialogs_searchNamePaint = null;
    public static RLottieDrawable dialogs_swipeDeleteDrawable = null;
    public static RLottieDrawable dialogs_swipeMuteDrawable = null;
    public static RLottieDrawable dialogs_swipePinDrawable = null;
    public static RLottieDrawable dialogs_swipeReadDrawable = null;
    public static RLottieDrawable dialogs_swipeUnmuteDrawable = null;
    public static RLottieDrawable dialogs_swipeUnpinDrawable = null;
    public static RLottieDrawable dialogs_swipeUnreadDrawable = null;
    public static Paint dialogs_tabletSeletedPaint = null;
    public static TextPaint dialogs_timePaint = null;
    public static RLottieDrawable dialogs_unarchiveDrawable = null;
    public static RLottieDrawable dialogs_unpinArchiveDrawable = null;
    public static Drawable dialogs_verifiedCheckDrawable = null;
    public static Drawable dialogs_verifiedDrawable = null;
    public static Paint dividerExtraPaint = null;
    public static Paint dividerPaint = null;
    /* access modifiers changed from: private */
    public static HashMap<String, String> fallbackKeys = new HashMap<>();
    private static FragmentContextViewWavesDrawable fragmentContextViewWavesDrawable = null;
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
    private static int patternIntensity;
    public static PathAnimator playPauseAnimator;
    private static int previousPhase;
    /* access modifiers changed from: private */
    public static ThemeInfo previousTheme;
    public static TextPaint profile_aboutTextPaint;
    public static Drawable profile_verifiedCheckDrawable;
    public static Drawable profile_verifiedDrawable;
    private static int[] remoteThemesHash = new int[3];
    public static int selectedAutoNightType;
    private static SensorManager sensorManager;
    private static Bitmap serviceBitmap;
    private static Matrix serviceBitmapMatrix;
    public static BitmapShader serviceBitmapShader;
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
    private static int[] viewPos = new int[2];
    private static Drawable wallpaper;
    private static Runnable wallpaperLoadTask;

    public static void destroyResources() {
    }

    public static class MessageDrawable extends Drawable {
        private int alpha;
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
        private final boolean isOut;
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
            this.alpha = 255;
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

        public int getTopY() {
            return this.topY;
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

        /* JADX WARNING: Removed duplicated region for block: B:44:0x0151  */
        /* JADX WARNING: Removed duplicated region for block: B:49:0x015f  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public android.graphics.drawable.Drawable getBackgroundDrawable() {
            /*
                r20 = this;
                r0 = r20
                int r1 = org.telegram.messenger.SharedConfig.bubbleRadius
                float r1 = (float) r1
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                boolean r2 = r0.isTopNear
                r3 = 2
                r4 = 1
                r5 = 0
                if (r2 == 0) goto L_0x0016
                boolean r6 = r0.isBottomNear
                if (r6 == 0) goto L_0x0016
                r2 = 3
                goto L_0x0021
            L_0x0016:
                if (r2 == 0) goto L_0x001a
                r2 = 2
                goto L_0x0021
            L_0x001a:
                boolean r2 = r0.isBottomNear
                if (r2 == 0) goto L_0x0020
                r2 = 1
                goto L_0x0021
            L_0x0020:
                r2 = 0
            L_0x0021:
                boolean r6 = r0.isSelected
                android.graphics.LinearGradient r7 = r0.gradientShader
                if (r7 != 0) goto L_0x002b
                if (r6 != 0) goto L_0x002b
                r7 = 1
                goto L_0x002c
            L_0x002b:
                r7 = 0
            L_0x002c:
                boolean r8 = r0.isOut
                if (r8 == 0) goto L_0x0033
                java.lang.String r8 = "chat_outBubbleShadow"
                goto L_0x0035
            L_0x0033:
                java.lang.String r8 = "chat_inBubbleShadow"
            L_0x0035:
                int r8 = r0.getColor(r8)
                int[][] r9 = r0.currentBackgroundDrawableRadius
                r10 = r9[r6]
                r10 = r10[r2]
                if (r10 != r1) goto L_0x004d
                if (r7 == 0) goto L_0x004a
                int[] r10 = r0.shadowDrawableColor
                r10 = r10[r2]
                if (r10 == r8) goto L_0x004a
                goto L_0x004d
            L_0x004a:
                r4 = 0
                goto L_0x014d
            L_0x004d:
                r9 = r9[r6]
                r9[r2] = r1
                r1 = 1112014848(0x42480000, float:50.0)
                int r1 = r0.dp(r1)     // Catch:{ all -> 0x004a }
                r9 = 1109393408(0x42200000, float:40.0)
                int r10 = r0.dp(r9)     // Catch:{ all -> 0x004a }
                android.graphics.Bitmap$Config r11 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x004a }
                android.graphics.Bitmap r1 = android.graphics.Bitmap.createBitmap(r1, r10, r11)     // Catch:{ all -> 0x004a }
                android.graphics.Canvas r10 = new android.graphics.Canvas     // Catch:{ all -> 0x004a }
                r10.<init>(r1)     // Catch:{ all -> 0x004a }
                android.graphics.Rect r11 = r0.backupRect     // Catch:{ all -> 0x004a }
                android.graphics.Rect r12 = r20.getBounds()     // Catch:{ all -> 0x004a }
                r11.set(r12)     // Catch:{ all -> 0x004a }
                r11 = -1
                if (r7 == 0) goto L_0x00fe
                int[] r7 = r0.shadowDrawableColor     // Catch:{ all -> 0x004a }
                r7[r2] = r8     // Catch:{ all -> 0x004a }
                android.graphics.Paint r7 = new android.graphics.Paint     // Catch:{ all -> 0x004a }
                r7.<init>(r4)     // Catch:{ all -> 0x004a }
                android.graphics.LinearGradient r15 = new android.graphics.LinearGradient     // Catch:{ all -> 0x004a }
                r13 = 0
                r14 = 0
                r16 = 0
                int r9 = r0.dp(r9)     // Catch:{ all -> 0x004a }
                float r9 = (float) r9     // Catch:{ all -> 0x004a }
                int[] r12 = new int[r3]     // Catch:{ all -> 0x004a }
                r17 = 358573417(0x155var_, float:4.511449E-26)
                r12[r5] = r17     // Catch:{ all -> 0x004a }
                r17 = 694117737(0x295var_, float:4.9603906E-14)
                r12[r4] = r17     // Catch:{ all -> 0x004a }
                r18 = 0
                android.graphics.Shader$TileMode r19 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x004a }
                r17 = r12
                r12 = r15
                r3 = r15
                r15 = r16
                r16 = r9
                r12.<init>(r13, r14, r15, r16, r17, r18, r19)     // Catch:{ all -> 0x004a }
                r7.setShader(r3)     // Catch:{ all -> 0x004a }
                android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter     // Catch:{ all -> 0x004a }
                android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY     // Catch:{ all -> 0x004a }
                r3.<init>(r8, r9)     // Catch:{ all -> 0x004a }
                r7.setColorFilter(r3)     // Catch:{ all -> 0x004a }
                r3 = 1073741824(0x40000000, float:2.0)
                r8 = 1065353216(0x3var_, float:1.0)
                r9 = 0
                r7.setShadowLayer(r3, r9, r8, r11)     // Catch:{ all -> 0x004a }
                float r3 = org.telegram.messenger.AndroidUtilities.density     // Catch:{ all -> 0x004a }
                int r3 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
                if (r3 <= 0) goto L_0x00cc
                int r3 = r1.getWidth()     // Catch:{ all -> 0x004a }
                int r3 = r3 + r4
                int r12 = r1.getHeight()     // Catch:{ all -> 0x004a }
                int r12 = r12 + r4
                r0.setBounds(r11, r11, r3, r12)     // Catch:{ all -> 0x004a }
                goto L_0x00d7
            L_0x00cc:
                int r3 = r1.getWidth()     // Catch:{ all -> 0x004a }
                int r12 = r1.getHeight()     // Catch:{ all -> 0x004a }
                r0.setBounds(r5, r5, r3, r12)     // Catch:{ all -> 0x004a }
            L_0x00d7:
                r0.draw(r10, r7)     // Catch:{ all -> 0x004a }
                float r3 = org.telegram.messenger.AndroidUtilities.density     // Catch:{ all -> 0x004a }
                int r3 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
                if (r3 <= 0) goto L_0x00fe
                r7.setColor(r5)     // Catch:{ all -> 0x004a }
                r7.setShadowLayer(r9, r9, r9, r5)     // Catch:{ all -> 0x004a }
                android.graphics.PorterDuffXfermode r3 = new android.graphics.PorterDuffXfermode     // Catch:{ all -> 0x004a }
                android.graphics.PorterDuff$Mode r8 = android.graphics.PorterDuff.Mode.CLEAR     // Catch:{ all -> 0x004a }
                r3.<init>(r8)     // Catch:{ all -> 0x004a }
                r7.setXfermode(r3)     // Catch:{ all -> 0x004a }
                int r3 = r1.getWidth()     // Catch:{ all -> 0x004a }
                int r8 = r1.getHeight()     // Catch:{ all -> 0x004a }
                r0.setBounds(r5, r5, r3, r8)     // Catch:{ all -> 0x004a }
                r0.draw(r10, r7)     // Catch:{ all -> 0x004a }
            L_0x00fe:
                android.graphics.Paint r3 = new android.graphics.Paint     // Catch:{ all -> 0x004a }
                r3.<init>(r4)     // Catch:{ all -> 0x004a }
                r3.setColor(r11)     // Catch:{ all -> 0x004a }
                int r7 = r1.getWidth()     // Catch:{ all -> 0x004a }
                int r8 = r1.getHeight()     // Catch:{ all -> 0x004a }
                r0.setBounds(r5, r5, r7, r8)     // Catch:{ all -> 0x004a }
                r0.draw(r10, r3)     // Catch:{ all -> 0x004a }
                android.graphics.drawable.Drawable[][] r3 = r0.backgroundDrawable     // Catch:{ all -> 0x004a }
                r3 = r3[r6]     // Catch:{ all -> 0x004a }
                android.graphics.drawable.NinePatchDrawable r7 = new android.graphics.drawable.NinePatchDrawable     // Catch:{ all -> 0x004a }
                int r8 = r1.getWidth()     // Catch:{ all -> 0x004a }
                r9 = 2
                int r8 = r8 / r9
                int r8 = r8 - r4
                int r10 = r1.getWidth()     // Catch:{ all -> 0x004a }
                int r10 = r10 / r9
                int r10 = r10 + r4
                int r11 = r1.getHeight()     // Catch:{ all -> 0x004a }
                int r11 = r11 / r9
                int r11 = r11 - r4
                int r12 = r1.getHeight()     // Catch:{ all -> 0x004a }
                int r12 = r12 / r9
                int r12 = r12 + r4
                java.nio.ByteBuffer r8 = getByteBuffer(r8, r10, r11, r12)     // Catch:{ all -> 0x004a }
                byte[] r8 = r8.array()     // Catch:{ all -> 0x004a }
                android.graphics.Rect r9 = new android.graphics.Rect     // Catch:{ all -> 0x004a }
                r9.<init>()     // Catch:{ all -> 0x004a }
                r10 = 0
                r7.<init>(r1, r8, r9, r10)     // Catch:{ all -> 0x004a }
                r3[r2] = r7     // Catch:{ all -> 0x004a }
                android.graphics.Rect r1 = r0.backupRect     // Catch:{ all -> 0x014c }
                r0.setBounds(r1)     // Catch:{ all -> 0x014c }
                goto L_0x014d
            L_0x014c:
            L_0x014d:
                boolean r1 = r0.isSelected
                if (r1 == 0) goto L_0x015f
                boolean r1 = r0.isOut
                if (r1 == 0) goto L_0x0158
                java.lang.String r1 = "chat_outBubbleSelected"
                goto L_0x015a
            L_0x0158:
                java.lang.String r1 = "chat_inBubbleSelected"
            L_0x015a:
                int r1 = r0.getColor(r1)
                goto L_0x016c
            L_0x015f:
                boolean r1 = r0.isOut
                if (r1 == 0) goto L_0x0166
                java.lang.String r1 = "chat_outBubble"
                goto L_0x0168
            L_0x0166:
                java.lang.String r1 = "chat_inBubble"
            L_0x0168:
                int r1 = r0.getColor(r1)
            L_0x016c:
                android.graphics.drawable.Drawable[][] r3 = r0.backgroundDrawable
                r5 = r3[r6]
                r5 = r5[r2]
                if (r5 == 0) goto L_0x0192
                int[][] r5 = r0.backgroundDrawableColor
                r5 = r5[r6]
                r5 = r5[r2]
                if (r5 != r1) goto L_0x017e
                if (r4 == 0) goto L_0x0192
            L_0x017e:
                r3 = r3[r6]
                r3 = r3[r2]
                android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
                android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
                r4.<init>(r1, r5)
                r3.setColorFilter(r4)
                int[][] r3 = r0.backgroundDrawableColor
                r3 = r3[r6]
                r3[r2] = r1
            L_0x0192:
                android.graphics.drawable.Drawable[][] r1 = r0.backgroundDrawable
                r1 = r1[r6]
                r1 = r1[r2]
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.MessageDrawable.getBackgroundDrawable():android.graphics.drawable.Drawable");
        }

        public Drawable getShadowDrawable() {
            if (this.gradientShader == null && !this.isSelected) {
                return null;
            }
            int dp = AndroidUtilities.dp((float) SharedConfig.bubbleRadius);
            boolean z = this.isTopNear;
            boolean z2 = false;
            char c = (!z || !this.isBottomNear) ? z ? 2 : this.isBottomNear ? (char) 1 : 0 : 3;
            int[] iArr = this.currentShadowDrawableRadius;
            if (iArr[c] != dp) {
                iArr[c] = dp;
                try {
                    Bitmap createBitmap = Bitmap.createBitmap(dp(50.0f), dp(40.0f), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(createBitmap);
                    Paint paint2 = new Paint(1);
                    LinearGradient linearGradient = r10;
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
                    z2 = true;
                } catch (Throwable unused) {
                }
            }
            int color = getColor(this.isOut ? "chat_outBubbleShadow" : "chat_inBubbleShadow");
            Drawable[] drawableArr = this.shadowDrawable;
            if (drawableArr[c] != null && (this.shadowDrawableColor[c] != color || z2)) {
                drawableArr[c].setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
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

        /* JADX WARNING: Removed duplicated region for block: B:107:0x04d4  */
        /* JADX WARNING: Removed duplicated region for block: B:53:0x0213  */
        /* JADX WARNING: Removed duplicated region for block: B:61:0x0264  */
        /* JADX WARNING: Removed duplicated region for block: B:99:0x0482  */
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
                if (r13 == 0) goto L_0x02eb
                int r13 = r0.currentType
                if (r13 == r7) goto L_0x00bb
                if (r21 != 0) goto L_0x00bb
                int r7 = r0.topY
                int r12 = r2.bottom
                int r7 = r7 + r12
                int r7 = r7 - r5
                int r12 = r0.currentBackgroundHeight
                if (r7 >= r12) goto L_0x0094
                goto L_0x00bb
            L_0x0094:
                android.graphics.Path r7 = r0.path
                int r12 = r2.right
                int r13 = r0.dp(r3)
                int r12 = r12 - r13
                float r12 = (float) r12
                int r13 = r0.topY
                int r13 = r11 - r13
                int r14 = r0.currentBackgroundHeight
                int r13 = r13 + r14
                float r13 = (float) r13
                r7.moveTo(r12, r13)
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
                goto L_0x0110
            L_0x00bb:
                if (r13 != r10) goto L_0x00d0
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
                goto L_0x00e1
            L_0x00d0:
                android.graphics.Path r7 = r0.path
                int r12 = r2.right
                int r13 = r0.dp(r14)
                int r12 = r12 - r13
                float r12 = (float) r12
                int r13 = r2.bottom
                int r13 = r13 - r4
                float r13 = (float) r13
                r7.moveTo(r12, r13)
            L_0x00e1:
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
            L_0x0110:
                int r3 = r0.currentType
                r7 = 2
                if (r3 == r7) goto L_0x016a
                if (r21 != 0) goto L_0x016a
                int r3 = r0.topY
                int r7 = r5 * 2
                int r7 = r7 + r3
                if (r7 < 0) goto L_0x011f
                goto L_0x016a
            L_0x011f:
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
                if (r3 != r10) goto L_0x014d
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
                goto L_0x020e
            L_0x014d:
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
                goto L_0x020e
            L_0x016a:
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
                if (r3 == 0) goto L_0x01a1
                r3 = r8
                goto L_0x01a2
            L_0x01a1:
                r3 = r5
            L_0x01a2:
                int r7 = r0.currentType
                if (r7 != r10) goto L_0x01cd
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
                goto L_0x0204
            L_0x01cd:
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
            L_0x0204:
                android.graphics.Path r3 = r0.path
                android.graphics.RectF r7 = r0.rect
                r10 = 1132920832(0x43870000, float:270.0)
                r12 = 0
                r3.arcTo(r7, r10, r15, r12)
            L_0x020e:
                int r3 = r0.currentType
                r7 = 1
                if (r3 != r7) goto L_0x0264
                if (r21 != 0) goto L_0x022e
                int r3 = r0.topY
                int r6 = r2.bottom
                int r6 = r6 + r3
                int r6 = r6 - r5
                int r7 = r0.currentBackgroundHeight
                if (r6 >= r7) goto L_0x0220
                goto L_0x022e
            L_0x0220:
                android.graphics.Path r5 = r0.path
                int r2 = r2.right
                int r2 = r2 - r4
                float r2 = (float) r2
                int r11 = r11 - r3
                int r11 = r11 + r7
                float r3 = (float) r11
                r5.lineTo(r2, r3)
                goto L_0x0557
            L_0x022e:
                boolean r3 = r0.isBottomNear
                if (r3 == 0) goto L_0x0233
                r5 = r8
            L_0x0233:
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
                goto L_0x0557
            L_0x0264:
                r5 = 2
                if (r3 == r5) goto L_0x028e
                if (r21 != 0) goto L_0x028e
                int r3 = r0.topY
                int r5 = r2.bottom
                int r3 = r3 + r5
                int r5 = r6 * 2
                int r3 = r3 - r5
                int r5 = r0.currentBackgroundHeight
                if (r3 >= r5) goto L_0x0276
                goto L_0x028e
            L_0x0276:
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
                goto L_0x0557
            L_0x028e:
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
                goto L_0x0557
            L_0x02eb:
                int r3 = r0.currentType
                r7 = -1028390912(0xffffffffc2b40000, float:-90.0)
                r10 = 2
                if (r3 == r10) goto L_0x0328
                if (r21 != 0) goto L_0x0328
                int r10 = r0.topY
                int r12 = r2.bottom
                int r10 = r10 + r12
                int r10 = r10 - r5
                int r12 = r0.currentBackgroundHeight
                if (r10 >= r12) goto L_0x02ff
                goto L_0x0328
            L_0x02ff:
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
                goto L_0x037f
            L_0x0328:
                r10 = 1
                if (r3 != r10) goto L_0x0340
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
                goto L_0x0351
            L_0x0340:
                android.graphics.Path r3 = r0.path
                int r10 = r2.left
                int r12 = r0.dp(r14)
                int r10 = r10 + r12
                float r10 = (float) r10
                int r12 = r2.bottom
                int r12 = r12 - r4
                float r12 = (float) r12
                r3.moveTo(r10, r12)
            L_0x0351:
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
            L_0x037f:
                int r3 = r0.currentType
                r10 = 2
                if (r3 == r10) goto L_0x03da
                if (r21 != 0) goto L_0x03da
                int r3 = r0.topY
                int r10 = r5 * 2
                int r10 = r10 + r3
                if (r10 < 0) goto L_0x038e
                goto L_0x03da
            L_0x038e:
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
                if (r3 != r10) goto L_0x03bd
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
                goto L_0x047d
            L_0x03bd:
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
                goto L_0x047d
            L_0x03da:
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
                if (r3 == 0) goto L_0x040e
                r3 = r8
                goto L_0x040f
            L_0x040e:
                r3 = r5
            L_0x040f:
                int r10 = r0.currentType
                r12 = 1
                if (r10 != r12) goto L_0x043c
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
                goto L_0x0473
            L_0x043c:
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
            L_0x0473:
                android.graphics.Path r3 = r0.path
                android.graphics.RectF r10 = r0.rect
                r12 = 1132920832(0x43870000, float:270.0)
                r13 = 0
                r3.arcTo(r10, r12, r7, r13)
            L_0x047d:
                int r3 = r0.currentType
                r10 = 1
                if (r3 != r10) goto L_0x04d4
                if (r21 != 0) goto L_0x049d
                int r3 = r0.topY
                int r6 = r2.bottom
                int r6 = r6 + r3
                int r6 = r6 - r5
                int r10 = r0.currentBackgroundHeight
                if (r6 >= r10) goto L_0x048f
                goto L_0x049d
            L_0x048f:
                android.graphics.Path r5 = r0.path
                int r2 = r2.left
                int r2 = r2 + r4
                float r2 = (float) r2
                int r11 = r11 - r3
                int r11 = r11 + r10
                float r3 = (float) r11
                r5.lineTo(r2, r3)
                goto L_0x0557
            L_0x049d:
                boolean r3 = r0.isBottomNear
                if (r3 == 0) goto L_0x04a2
                r5 = r8
            L_0x04a2:
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
                goto L_0x0557
            L_0x04d4:
                r5 = 2
                if (r3 == r5) goto L_0x04fd
                if (r21 != 0) goto L_0x04fd
                int r3 = r0.topY
                int r5 = r2.bottom
                int r3 = r3 + r5
                int r5 = r6 * 2
                int r3 = r3 - r5
                int r5 = r0.currentBackgroundHeight
                if (r3 >= r5) goto L_0x04e6
                goto L_0x04fd
            L_0x04e6:
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
                goto L_0x0557
            L_0x04fd:
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
            L_0x0557:
                android.graphics.Path r2 = r0.path
                r2.close()
                android.graphics.Path r2 = r0.path
                r1.drawPath(r2, r9)
                android.graphics.LinearGradient r2 = r0.gradientShader
                if (r2 == 0) goto L_0x058e
                boolean r2 = r0.isSelected
                if (r2 == 0) goto L_0x058e
                if (r21 != 0) goto L_0x058e
                java.lang.String r2 = "chat_outBubbleGradientSelectedOverlay"
                int r2 = r0.getColor(r2)
                android.graphics.Paint r3 = r0.selectedPaint
                int r4 = android.graphics.Color.alpha(r2)
                int r5 = r0.alpha
                int r4 = r4 * r5
                float r4 = (float) r4
                r5 = 1132396544(0x437var_, float:255.0)
                float r4 = r4 / r5
                int r4 = (int) r4
                int r2 = androidx.core.graphics.ColorUtils.setAlphaComponent(r2, r4)
                r3.setColor(r2)
                android.graphics.Path r2 = r0.path
                android.graphics.Paint r3 = r0.selectedPaint
                r1.drawPath(r2, r3)
            L_0x058e:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.MessageDrawable.draw(android.graphics.Canvas, android.graphics.Paint):void");
        }

        public void setAlpha(int i) {
            if (this.alpha != i) {
                this.alpha = i;
                this.paint.setAlpha(i);
                if (this.isOut) {
                    this.selectedPaint.setAlpha((int) (((float) Color.alpha(getColor("chat_outBubbleGradientSelectedOverlay"))) * (((float) i) / 255.0f)));
                }
            }
            if (this.gradientShader == null) {
                Drawable backgroundDrawable2 = getBackgroundDrawable();
                if (Build.VERSION.SDK_INT < 19) {
                    backgroundDrawable2.setAlpha(i);
                } else if (backgroundDrawable2.getAlpha() != i) {
                    backgroundDrawable2.setAlpha(i);
                }
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
                    public final /* synthetic */ ArrayList f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        Theme.PatternsLoader.this.lambda$new$1$Theme$PatternsLoader(this.f$1);
                    }
                });
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$1 */
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
                    public final /* synthetic */ ArrayList f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        Theme.PatternsLoader.this.lambda$new$0$Theme$PatternsLoader(this.f$1, tLObject, tLRPC$TL_error);
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
        /* access modifiers changed from: private */
        /* JADX WARNING: Multi-variable type inference failed */
        /* renamed from: lambda$new$0 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$new$0$Theme$PatternsLoader(java.util.ArrayList r17, org.telegram.tgnet.TLObject r18, org.telegram.tgnet.TLRPC$TL_error r19) {
            /*
                r16 = this;
                r0 = r16
                r1 = r18
                boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$Vector
                if (r2 == 0) goto L_0x00bb
                org.telegram.tgnet.TLRPC$Vector r1 = (org.telegram.tgnet.TLRPC$Vector) r1
                java.util.ArrayList<java.lang.Object> r2 = r1.objects
                int r2 = r2.size()
                r4 = 0
                r6 = r4
                r5 = 0
            L_0x0013:
                r7 = 1
                if (r5 >= r2) goto L_0x00b8
                java.util.ArrayList<java.lang.Object> r8 = r1.objects
                java.lang.Object r8 = r8.get(r5)
                org.telegram.tgnet.TLRPC$WallPaper r8 = (org.telegram.tgnet.TLRPC$WallPaper) r8
                boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_wallPaper
                if (r9 != 0) goto L_0x0024
                goto L_0x00b1
            L_0x0024:
                org.telegram.tgnet.TLRPC$TL_wallPaper r8 = (org.telegram.tgnet.TLRPC$TL_wallPaper) r8
                boolean r9 = r8.pattern
                if (r9 == 0) goto L_0x00b1
                org.telegram.tgnet.TLRPC$Document r9 = r8.document
                java.io.File r7 = org.telegram.messenger.FileLoader.getPathToAttach(r9, r7)
                int r9 = r17.size()
                r11 = r4
                r12 = r11
                r10 = 0
            L_0x0037:
                if (r10 >= r9) goto L_0x00a9
                r13 = r17
                java.lang.Object r14 = r13.get(r10)
                org.telegram.ui.ActionBar.Theme$ThemeAccent r14 = (org.telegram.ui.ActionBar.Theme.ThemeAccent) r14
                java.lang.String r15 = r14.patternSlug
                java.lang.String r3 = r8.slug
                boolean r3 = r15.equals(r3)
                if (r3 == 0) goto L_0x00a5
                if (r12 != 0) goto L_0x0056
                boolean r3 = r7.exists()
                java.lang.Boolean r3 = java.lang.Boolean.valueOf(r3)
                r12 = r3
            L_0x0056:
                if (r11 != 0) goto L_0x008c
                boolean r3 = r12.booleanValue()
                if (r3 == 0) goto L_0x005f
                goto L_0x008c
            L_0x005f:
                org.telegram.tgnet.TLRPC$Document r3 = r8.document
                java.lang.String r3 = org.telegram.messenger.FileLoader.getAttachFileName(r3)
                java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$PatternsLoader$LoadingPattern> r15 = r0.watingForLoad
                if (r15 != 0) goto L_0x0070
                java.util.HashMap r15 = new java.util.HashMap
                r15.<init>()
                r0.watingForLoad = r15
            L_0x0070:
                java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$PatternsLoader$LoadingPattern> r15 = r0.watingForLoad
                java.lang.Object r15 = r15.get(r3)
                org.telegram.ui.ActionBar.Theme$PatternsLoader$LoadingPattern r15 = (org.telegram.ui.ActionBar.Theme.PatternsLoader.LoadingPattern) r15
                if (r15 != 0) goto L_0x0086
                org.telegram.ui.ActionBar.Theme$PatternsLoader$LoadingPattern r15 = new org.telegram.ui.ActionBar.Theme$PatternsLoader$LoadingPattern
                r15.<init>()
                r15.pattern = r8
                java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$PatternsLoader$LoadingPattern> r4 = r0.watingForLoad
                r4.put(r3, r15)
            L_0x0086:
                java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeAccent> r3 = r15.accents
                r3.add(r14)
                goto L_0x00a5
            L_0x008c:
                org.telegram.tgnet.TLRPC$Document r3 = r8.document
                java.lang.String r3 = r3.mime_type
                java.lang.String r4 = "application/x-tgwallpattern"
                boolean r3 = r4.equals(r3)
                android.graphics.Bitmap r11 = r0.createWallpaperForAccent(r11, r3, r7, r14)
                if (r6 != 0) goto L_0x00a2
                java.util.ArrayList r3 = new java.util.ArrayList
                r3.<init>()
                r6 = r3
            L_0x00a2:
                r6.add(r14)
            L_0x00a5:
                int r10 = r10 + 1
                r4 = 0
                goto L_0x0037
            L_0x00a9:
                r13 = r17
                if (r11 == 0) goto L_0x00b3
                r11.recycle()
                goto L_0x00b3
            L_0x00b1:
                r13 = r17
            L_0x00b3:
                int r5 = r5 + 1
                r4 = 0
                goto L_0x0013
            L_0x00b8:
                r0.checkCurrentWallpaper(r6, r7)
            L_0x00bb:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.PatternsLoader.lambda$new$0$Theme$PatternsLoader(java.util.ArrayList, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
        }

        private void checkCurrentWallpaper(ArrayList<ThemeAccent> arrayList, boolean z) {
            AndroidUtilities.runOnUIThread(new Runnable(arrayList, z) {
                public final /* synthetic */ ArrayList f$1;
                public final /* synthetic */ boolean f$2;

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
        public void lambda$checkCurrentWallpaper$2(ArrayList<ThemeAccent> arrayList, boolean z) {
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
            int i;
            Bitmap bitmap3;
            Integer num;
            Integer num2;
            Integer num3;
            File file2 = file;
            ThemeAccent themeAccent2 = themeAccent;
            try {
                File pathToWallpaper = themeAccent.getPathToWallpaper();
                Drawable drawable = null;
                if (pathToWallpaper == null) {
                    return null;
                }
                ThemeInfo themeInfo = themeAccent2.parentTheme;
                HashMap<String, Integer> themeFileValues = Theme.getThemeFileValues((File) null, themeInfo.assetName, (String[]) null);
                int i2 = themeAccent2.accentColor;
                int i3 = (int) themeAccent2.backgroundOverrideColor;
                long j = themeAccent2.backgroundGradientOverrideColor1;
                int i4 = (int) j;
                if (i4 == 0 && j == 0) {
                    if (i3 != 0) {
                        i2 = i3;
                    }
                    Integer num4 = themeFileValues.get("chat_wallpaper_gradient_to");
                    if (num4 != null) {
                        i4 = Theme.changeColorAccent(themeInfo, i2, num4.intValue());
                    }
                } else {
                    i2 = 0;
                }
                long j2 = themeAccent2.backgroundGradientOverrideColor2;
                int i5 = (int) j2;
                if (i5 == 0 && j2 == 0 && (num3 = themeFileValues.get("key_chat_wallpaper_gradient_to2")) != null) {
                    i5 = Theme.changeColorAccent(themeInfo, i2, num3.intValue());
                }
                long j3 = themeAccent2.backgroundGradientOverrideColor3;
                int i6 = (int) j3;
                if (i6 == 0 && j3 == 0 && (num2 = themeFileValues.get("key_chat_wallpaper_gradient_to3")) != null) {
                    i6 = Theme.changeColorAccent(themeInfo, i2, num2.intValue());
                }
                if (i3 == 0 && (num = themeFileValues.get("chat_wallpaper")) != null) {
                    i3 = Theme.changeColorAccent(themeInfo, i2, num.intValue());
                }
                if (i5 != 0) {
                    i = MotionBackgroundDrawable.getPatternColor(i3, i4, i5, i6);
                } else if (i4 != 0) {
                    Drawable backgroundGradientDrawable = new BackgroundGradientDrawable(BackgroundGradientDrawable.getGradientOrientation(themeAccent2.backgroundRotation), new int[]{i3, i4});
                    i = AndroidUtilities.getPatternColor(AndroidUtilities.getAverageColor(i3, i4));
                    drawable = backgroundGradientDrawable;
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
                    if (drawable != null) {
                        drawable.setBounds(0, 0, bitmap2.getWidth(), bitmap2.getHeight());
                        drawable.draw(canvas);
                    }
                    Paint paint = new Paint(2);
                    paint.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN));
                    paint.setAlpha((int) (Math.abs(themeAccent2.patternIntensity) * 255.0f));
                    canvas.drawBitmap(bitmap2, 0.0f, 0.0f, paint);
                    FileOutputStream fileOutputStream = new FileOutputStream(pathToWallpaper);
                    createBitmap.compress(drawable == null ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, 87, fileOutputStream);
                    fileOutputStream.close();
                } catch (Throwable th) {
                    th = th;
                    FileLog.e(th);
                    return bitmap2;
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
                            public final /* synthetic */ Theme.PatternsLoader.LoadingPattern f$1;

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

        /* access modifiers changed from: private */
        /* renamed from: lambda$didReceivedNotification$3 */
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
        public long backgroundGradientOverrideColor1;
        public long backgroundGradientOverrideColor2;
        public long backgroundGradientOverrideColor3;
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
                if (i7 == 0) {
                    i7 = this.accentColor;
                }
                Integer num2 = hashMap.get("chat_outBubble");
                if (num2 == null) {
                    num2 = (Integer) Theme.defaultColors.get("chat_outBubble");
                }
                z = AndroidUtilities.getColorDistance(i7, Theme.changeColorAccent(access$600, access$6002, num2.intValue(), isDark)) <= 35000 && AndroidUtilities.getColorDistance(i7, this.myMessagesGradientAccentColor) <= 35000;
                i7 = Theme.getAccentColor(access$600, num2.intValue(), i7);
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
                    i4 = -14606047;
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
                hashMap2.put("chat_outViewsSelected", Integer.valueOf(i4));
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
            int i8 = this.myMessagesAccentColor;
            if (!(i8 == 0 || this.myMessagesGradientAccentColor == 0)) {
                hashMap2.put("chat_outBubble", Integer.valueOf(i8));
                hashMap2.put("chat_outBubbleGradient", Integer.valueOf(this.myMessagesGradientAccentColor));
            }
            long j = this.backgroundOverrideColor;
            int i9 = (int) j;
            if (i9 != 0) {
                hashMap2.put("chat_wallpaper", Integer.valueOf(i9));
            } else if (j != 0) {
                hashMap2.remove("chat_wallpaper");
            }
            long j2 = this.backgroundGradientOverrideColor1;
            int i10 = (int) j2;
            if (i10 != 0) {
                hashMap2.put("chat_wallpaper_gradient_to", Integer.valueOf(i10));
            } else if (j2 != 0) {
                hashMap2.remove("chat_wallpaper_gradient_to");
            }
            long j3 = this.backgroundGradientOverrideColor2;
            int i11 = (int) j3;
            if (i11 != 0) {
                hashMap2.put("key_chat_wallpaper_gradient_to2", Integer.valueOf(i11));
            } else if (j3 != 0) {
                hashMap2.remove("key_chat_wallpaper_gradient_to2");
            }
            long j4 = this.backgroundGradientOverrideColor3;
            int i12 = (int) j4;
            if (i12 != 0) {
                hashMap2.put("key_chat_wallpaper_gradient_to3", Integer.valueOf(i12));
            } else if (j4 != 0) {
                hashMap2.remove("key_chat_wallpaper_gradient_to3");
            }
            int i13 = this.backgroundRotation;
            if (i13 != 45) {
                hashMap2.put("chat_wallpaper_gradient_rotation", Integer.valueOf(i13));
            }
            return !z2;
        }

        public File getPathToWallpaper() {
            if (TextUtils.isEmpty(this.patternSlug)) {
                return null;
            }
            return new File(ApplicationLoader.getFilesDirFixed(), String.format(Locale.US, "%s_%d_%s.jpg", new Object[]{this.parentTheme.getKey(), Integer.valueOf(this.id), this.patternSlug}));
        }

        /* JADX WARNING: Removed duplicated region for block: B:76:0x02cb A[SYNTHETIC, Splitter:B:76:0x02cb] */
        /* JADX WARNING: Removed duplicated region for block: B:84:0x02d9 A[SYNTHETIC, Splitter:B:84:0x02d9] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.io.File saveToFile() {
            /*
                r19 = this;
                r1 = r19
                java.io.File r0 = org.telegram.messenger.AndroidUtilities.getSharingDirectory()
                r0.mkdirs()
                java.io.File r2 = new java.io.File
                java.util.Locale r3 = java.util.Locale.US
                r4 = 2
                java.lang.Object[] r5 = new java.lang.Object[r4]
                org.telegram.ui.ActionBar.Theme$ThemeInfo r6 = r1.parentTheme
                java.lang.String r6 = r6.getKey()
                r7 = 0
                java.lang.Integer r8 = java.lang.Integer.valueOf(r7)
                r5[r7] = r6
                int r6 = r1.id
                java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
                r9 = 1
                r5[r9] = r6
                java.lang.String r6 = "%s_%d.attheme"
                java.lang.String r3 = java.lang.String.format(r3, r6, r5)
                r2.<init>(r0, r3)
                org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = r1.parentTheme
                java.lang.String r0 = r0.assetName
                r3 = 0
                java.util.HashMap r0 = org.telegram.ui.ActionBar.Theme.getThemeFileValues(r3, r0, r3)
                java.util.HashMap r5 = new java.util.HashMap
                r5.<init>(r0)
                r1.fillAccentColors(r0, r5)
                java.lang.String r0 = r1.patternSlug
                boolean r0 = android.text.TextUtils.isEmpty(r0)
                java.lang.String r6 = "key_chat_wallpaper_gradient_to3"
                java.lang.String r10 = "key_chat_wallpaper_gradient_to2"
                java.lang.String r11 = "chat_wallpaper_gradient_to"
                java.lang.String r12 = "chat_wallpaper"
                if (r0 != 0) goto L_0x0234
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                boolean r13 = r1.patternMotion
                if (r13 == 0) goto L_0x005e
                java.lang.String r13 = "motion"
                r0.append(r13)
            L_0x005e:
                java.lang.Object r13 = r5.get(r12)
                java.lang.Integer r13 = (java.lang.Integer) r13
                if (r13 != 0) goto L_0x006b
                r13 = -1
                java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            L_0x006b:
                java.lang.Object r14 = r5.get(r11)
                java.lang.Integer r14 = (java.lang.Integer) r14
                if (r14 != 0) goto L_0x0074
                r14 = r8
            L_0x0074:
                java.lang.Object r15 = r5.get(r10)
                java.lang.Integer r15 = (java.lang.Integer) r15
                if (r15 != 0) goto L_0x007d
                r15 = r8
            L_0x007d:
                java.lang.Object r16 = r5.get(r6)
                java.lang.Integer r16 = (java.lang.Integer) r16
                if (r16 != 0) goto L_0x0086
                goto L_0x0088
            L_0x0086:
                r8 = r16
            L_0x0088:
                java.lang.String r3 = "chat_wallpaper_gradient_rotation"
                java.lang.Object r3 = r5.get(r3)
                java.lang.Integer r3 = (java.lang.Integer) r3
                if (r3 != 0) goto L_0x0098
                r3 = 45
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            L_0x0098:
                r4 = 3
                java.lang.Object[] r9 = new java.lang.Object[r4]
                int r18 = r13.intValue()
                int r4 = r18 >> 16
                byte r4 = (byte) r4
                r4 = r4 & 255(0xff, float:3.57E-43)
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                r9[r7] = r4
                int r4 = r13.intValue()
                int r4 = r4 >> 8
                byte r4 = (byte) r4
                r4 = r4 & 255(0xff, float:3.57E-43)
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                r17 = 1
                r9[r17] = r4
                int r4 = r13.intValue()
                r4 = r4 & 255(0xff, float:3.57E-43)
                byte r4 = (byte) r4
                java.lang.Byte r4 = java.lang.Byte.valueOf(r4)
                r13 = 2
                r9[r13] = r4
                java.lang.String r4 = "%02x%02x%02x"
                java.lang.String r9 = java.lang.String.format(r4, r9)
                java.lang.String r9 = r9.toLowerCase()
                int r13 = r14.intValue()
                if (r13 == 0) goto L_0x0115
                r13 = 3
                java.lang.Object[] r7 = new java.lang.Object[r13]
                int r13 = r14.intValue()
                int r13 = r13 >> 16
                byte r13 = (byte) r13
                r13 = r13 & 255(0xff, float:3.57E-43)
                java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
                r18 = 0
                r7[r18] = r13
                int r13 = r14.intValue()
                int r13 = r13 >> 8
                byte r13 = (byte) r13
                r13 = r13 & 255(0xff, float:3.57E-43)
                java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
                r17 = 1
                r7[r17] = r13
                int r13 = r14.intValue()
                r13 = r13 & 255(0xff, float:3.57E-43)
                byte r13 = (byte) r13
                java.lang.Byte r13 = java.lang.Byte.valueOf(r13)
                r14 = 2
                r7[r14] = r13
                java.lang.String r7 = java.lang.String.format(r4, r7)
                java.lang.String r7 = r7.toLowerCase()
                goto L_0x0116
            L_0x0115:
                r7 = 0
            L_0x0116:
                int r13 = r15.intValue()
                if (r13 == 0) goto L_0x0158
                r13 = 3
                java.lang.Object[] r14 = new java.lang.Object[r13]
                int r13 = r15.intValue()
                int r13 = r13 >> 16
                byte r13 = (byte) r13
                r13 = r13 & 255(0xff, float:3.57E-43)
                java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
                r18 = 0
                r14[r18] = r13
                int r13 = r15.intValue()
                int r13 = r13 >> 8
                byte r13 = (byte) r13
                r13 = r13 & 255(0xff, float:3.57E-43)
                java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
                r17 = 1
                r14[r17] = r13
                int r13 = r15.intValue()
                r13 = r13 & 255(0xff, float:3.57E-43)
                byte r13 = (byte) r13
                java.lang.Byte r13 = java.lang.Byte.valueOf(r13)
                r15 = 2
                r14[r15] = r13
                java.lang.String r13 = java.lang.String.format(r4, r14)
                java.lang.String r13 = r13.toLowerCase()
                goto L_0x0159
            L_0x0158:
                r13 = 0
            L_0x0159:
                int r14 = r8.intValue()
                if (r14 == 0) goto L_0x019b
                r14 = 3
                java.lang.Object[] r14 = new java.lang.Object[r14]
                int r15 = r8.intValue()
                int r15 = r15 >> 16
                byte r15 = (byte) r15
                r15 = r15 & 255(0xff, float:3.57E-43)
                java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
                r18 = 0
                r14[r18] = r15
                int r15 = r8.intValue()
                int r15 = r15 >> 8
                byte r15 = (byte) r15
                r15 = r15 & 255(0xff, float:3.57E-43)
                java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
                r17 = 1
                r14[r17] = r15
                int r8 = r8.intValue()
                r8 = r8 & 255(0xff, float:3.57E-43)
                byte r8 = (byte) r8
                java.lang.Byte r8 = java.lang.Byte.valueOf(r8)
                r15 = 2
                r14[r15] = r8
                java.lang.String r4 = java.lang.String.format(r4, r14)
                java.lang.String r4 = r4.toLowerCase()
                goto L_0x019c
            L_0x019b:
                r4 = 0
            L_0x019c:
                java.lang.String r8 = "-"
                if (r7 == 0) goto L_0x01c3
                if (r13 == 0) goto L_0x01c3
                if (r4 == 0) goto L_0x01c3
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                r3.append(r9)
                r3.append(r8)
                r3.append(r7)
                r3.append(r8)
                r3.append(r13)
                r3.append(r8)
                r3.append(r4)
                java.lang.String r9 = r3.toString()
                goto L_0x01eb
            L_0x01c3:
                if (r7 == 0) goto L_0x01eb
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r9)
                r4.append(r8)
                r4.append(r7)
                java.lang.String r4 = r4.toString()
                java.lang.StringBuilder r7 = new java.lang.StringBuilder
                r7.<init>()
                r7.append(r4)
                java.lang.String r4 = "&rotation="
                r7.append(r4)
                r7.append(r3)
                java.lang.String r9 = r7.toString()
            L_0x01eb:
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "https://attheme.org?slug="
                r3.append(r4)
                java.lang.String r4 = r1.patternSlug
                r3.append(r4)
                java.lang.String r4 = "&intensity="
                r3.append(r4)
                float r4 = r1.patternIntensity
                r7 = 1120403456(0x42CLASSNAME, float:100.0)
                float r4 = r4 * r7
                int r4 = (int) r4
                r3.append(r4)
                java.lang.String r4 = "&bg_color="
                r3.append(r4)
                r3.append(r9)
                java.lang.String r3 = r3.toString()
                int r4 = r0.length()
                if (r4 <= 0) goto L_0x0235
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r3)
                java.lang.String r3 = "&mode="
                r4.append(r3)
                java.lang.String r0 = r0.toString()
                r4.append(r0)
                java.lang.String r3 = r4.toString()
                goto L_0x0235
            L_0x0234:
                r3 = 0
            L_0x0235:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.util.Set r4 = r5.entrySet()
                java.util.Iterator r4 = r4.iterator()
            L_0x0242:
                boolean r5 = r4.hasNext()
                java.lang.String r7 = "\n"
                if (r5 == 0) goto L_0x0284
                java.lang.Object r5 = r4.next()
                java.util.Map$Entry r5 = (java.util.Map.Entry) r5
                java.lang.Object r8 = r5.getKey()
                java.lang.String r8 = (java.lang.String) r8
                if (r3 == 0) goto L_0x0271
                boolean r9 = r12.equals(r8)
                if (r9 != 0) goto L_0x0242
                boolean r9 = r11.equals(r8)
                if (r9 != 0) goto L_0x0242
                boolean r9 = r10.equals(r8)
                if (r9 != 0) goto L_0x0242
                boolean r9 = r6.equals(r8)
                if (r9 == 0) goto L_0x0271
                goto L_0x0242
            L_0x0271:
                r0.append(r8)
                java.lang.String r8 = "="
                r0.append(r8)
                java.lang.Object r5 = r5.getValue()
                r0.append(r5)
                r0.append(r7)
                goto L_0x0242
            L_0x0284:
                java.io.FileOutputStream r4 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x02c4, all -> 0x02c0 }
                r4.<init>(r2)     // Catch:{ Exception -> 0x02c4, all -> 0x02c0 }
                java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x02bd, all -> 0x02b9 }
                byte[] r0 = org.telegram.messenger.AndroidUtilities.getStringBytes(r0)     // Catch:{ Exception -> 0x02bd, all -> 0x02b9 }
                r4.write(r0)     // Catch:{ Exception -> 0x02bd, all -> 0x02b9 }
                boolean r0 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x02bd, all -> 0x02b9 }
                if (r0 != 0) goto L_0x02b5
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02bd, all -> 0x02b9 }
                r0.<init>()     // Catch:{ Exception -> 0x02bd, all -> 0x02b9 }
                java.lang.String r5 = "WLS="
                r0.append(r5)     // Catch:{ Exception -> 0x02bd, all -> 0x02b9 }
                r0.append(r3)     // Catch:{ Exception -> 0x02bd, all -> 0x02b9 }
                r0.append(r7)     // Catch:{ Exception -> 0x02bd, all -> 0x02b9 }
                java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x02bd, all -> 0x02b9 }
                byte[] r0 = org.telegram.messenger.AndroidUtilities.getStringBytes(r0)     // Catch:{ Exception -> 0x02bd, all -> 0x02b9 }
                r4.write(r0)     // Catch:{ Exception -> 0x02bd, all -> 0x02b9 }
            L_0x02b5:
                r4.close()     // Catch:{ Exception -> 0x02cf }
                goto L_0x02d4
            L_0x02b9:
                r0 = move-exception
                r2 = r0
                r3 = r4
                goto L_0x02d7
            L_0x02bd:
                r0 = move-exception
                r3 = r4
                goto L_0x02c6
            L_0x02c0:
                r0 = move-exception
                r2 = r0
                r3 = 0
                goto L_0x02d7
            L_0x02c4:
                r0 = move-exception
                r3 = 0
            L_0x02c6:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x02d5 }
                if (r3 == 0) goto L_0x02d4
                r3.close()     // Catch:{ Exception -> 0x02cf }
                goto L_0x02d4
            L_0x02cf:
                r0 = move-exception
                r3 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            L_0x02d4:
                return r2
            L_0x02d5:
                r0 = move-exception
                r2 = r0
            L_0x02d7:
                if (r3 == 0) goto L_0x02e2
                r3.close()     // Catch:{ Exception -> 0x02dd }
                goto L_0x02e2
            L_0x02dd:
                r0 = move-exception
                r3 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            L_0x02e2:
                goto L_0x02e4
            L_0x02e3:
                throw r2
            L_0x02e4:
                goto L_0x02e3
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.ThemeAccent.saveToFile():java.io.File");
        }
    }

    public static class OverrideWallpaperInfo {
        public int color;
        public String fileName = "";
        public int gradientColor1;
        public int gradientColor2;
        public int gradientColor3;
        public float intensity;
        public boolean isBlurred;
        public boolean isMotion;
        public String originalFileName = "";
        public ThemeAccent parentAccent;
        public ThemeInfo parentTheme;
        public int rotation;
        public String slug = "";
        public long wallpaperId;

        public OverrideWallpaperInfo() {
        }

        public OverrideWallpaperInfo(OverrideWallpaperInfo overrideWallpaperInfo, ThemeInfo themeInfo, ThemeAccent themeAccent) {
            this.slug = overrideWallpaperInfo.slug;
            this.color = overrideWallpaperInfo.color;
            this.gradientColor1 = overrideWallpaperInfo.gradientColor1;
            this.gradientColor2 = overrideWallpaperInfo.gradientColor2;
            this.gradientColor3 = overrideWallpaperInfo.gradientColor3;
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
            if (themeInfo != null) {
                ThemeAccent themeAccent = this.parentAccent;
                if (themeAccent == null && themeInfo.overrideWallpaper != this) {
                    return;
                }
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
                jSONObject.put("pGrColor", this.gradientColor1);
                jSONObject.put("pGrColor2", this.gradientColor2);
                jSONObject.put("pGrColor3", this.gradientColor3);
                jSONObject.put("pGrAngle", this.rotation);
                String str = this.slug;
                if (str == null) {
                    str = "";
                }
                jSONObject.put("wallSlug", str);
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
        public int patternBgGradientColor1;
        public int patternBgGradientColor2;
        public int patternBgGradientColor3;
        public int patternBgGradientRotation = 45;
        public int patternIntensity;
        public int prevAccentId = -1;
        /* access modifiers changed from: private */
        public int previewBackgroundColor;
        public int previewBackgroundGradientColor1;
        public int previewBackgroundGradientColor2;
        public int previewBackgroundGradientColor3;
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
                TLRPC$TL_theme tLRPC$TL_theme = this.info;
                if (tLRPC$TL_theme != null) {
                    SerializedData serializedData = new SerializedData(tLRPC$TL_theme.getObjectSize());
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
                    overrideWallpaperInfo.gradientColor1 = jSONObject.getInt("pGrColor");
                    overrideWallpaperInfo.gradientColor2 = jSONObject.optInt("pGrColor2");
                    overrideWallpaperInfo.gradientColor3 = jSONObject.optInt("pGrColor3");
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
            if (themeAccent2 == null || themeAccent == null || themeAccent.accentColor != themeAccent2.accentColor) {
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
                int i2 = iArr6 != null ? iArr6[i] : i;
                themeAccent.id = i2;
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
                    if (!this.firstAccentIsDefault || i2 != Theme.DEFALT_THEME_ACCENT_ID) {
                        themeAccent.backgroundOverrideColor = (long) iArr4[i];
                    } else {
                        themeAccent.backgroundOverrideColor = 4294967296L;
                    }
                }
                if (iArr5 != null) {
                    if (!this.firstAccentIsDefault || i2 != Theme.DEFALT_THEME_ACCENT_ID) {
                        themeAccent.backgroundGradientOverrideColor1 = (long) iArr5[i];
                    } else {
                        themeAccent.backgroundGradientOverrideColor1 = 4294967296L;
                    }
                }
                if (strArr != null) {
                    themeAccent.patternIntensity = ((float) iArr8[i]) / 100.0f;
                    themeAccent.backgroundRotation = iArr7[i];
                    themeAccent.patternSlug = strArr[i];
                }
                this.themeAccentsMap.put(i2, themeAccent);
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
            long j2;
            long j3;
            int i;
            int i2;
            TLRPC$WallPaperSettings tLRPC$WallPaperSettings;
            ThemeAccent themeAccent2 = themeAccent;
            TLRPC$TL_themeSettings tLRPC$TL_themeSettings2 = tLRPC$TL_themeSettings;
            int i3 = tLRPC$TL_themeSettings2.message_top_color;
            if (tLRPC$TL_themeSettings2.message_bottom_color == i3) {
                i3 = 0;
            }
            String str = null;
            float f = 0.0f;
            TLRPC$WallPaper tLRPC$WallPaper = tLRPC$TL_themeSettings2.wallpaper;
            if (tLRPC$WallPaper == null || (tLRPC$WallPaperSettings = tLRPC$WallPaper.settings) == null) {
                j2 = 0;
                j = 0;
                i2 = 0;
                j3 = 0;
                i = 0;
            } else {
                i = tLRPC$WallPaperSettings.background_color;
                int i4 = tLRPC$WallPaperSettings.second_background_color;
                j2 = i4 == 0 ? 4294967296L : (long) i4;
                int i5 = tLRPC$WallPaperSettings.third_background_color;
                j = i5 == 0 ? 4294967296L : (long) i5;
                int i6 = tLRPC$WallPaperSettings.fourth_background_color;
                j3 = i6 == 0 ? 4294967296L : (long) i6;
                i2 = AndroidUtilities.getWallpaperRotation(tLRPC$WallPaperSettings.rotation, false);
                TLRPC$WallPaper tLRPC$WallPaper2 = tLRPC$TL_themeSettings2.wallpaper;
                if (!(tLRPC$WallPaper2 instanceof TLRPC$TL_wallPaperNoFile) && tLRPC$WallPaper2.pattern) {
                    str = tLRPC$WallPaper2.slug;
                    f = ((float) tLRPC$WallPaper2.settings.intensity) / 100.0f;
                }
            }
            if (tLRPC$TL_themeSettings2.accent_color == themeAccent2.accentColor && tLRPC$TL_themeSettings2.message_bottom_color == themeAccent2.myMessagesAccentColor && i3 == themeAccent2.myMessagesGradientAccentColor) {
                return ((long) i) == themeAccent2.backgroundOverrideColor && j2 == themeAccent2.backgroundGradientOverrideColor1 && j == themeAccent2.backgroundGradientOverrideColor2 && j3 == themeAccent2.backgroundGradientOverrideColor3 && i2 == themeAccent2.backgroundRotation && TextUtils.equals(str, themeAccent2.patternSlug) && ((double) Math.abs(f - themeAccent2.patternIntensity)) < 0.001d;
            }
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
                int i3 = tLRPC$WallPaperSettings.flags;
                if ((i3 & 16) == 0 || tLRPC$WallPaperSettings.second_background_color != 0) {
                    themeAccent.backgroundGradientOverrideColor1 = (long) tLRPC$WallPaperSettings.second_background_color;
                } else {
                    themeAccent.backgroundGradientOverrideColor1 = 4294967296L;
                }
                if ((i3 & 32) == 0 || tLRPC$WallPaperSettings.third_background_color != 0) {
                    themeAccent.backgroundGradientOverrideColor2 = (long) tLRPC$WallPaperSettings.third_background_color;
                } else {
                    themeAccent.backgroundGradientOverrideColor2 = 4294967296L;
                }
                if ((i3 & 64) == 0 || tLRPC$WallPaperSettings.fourth_background_color != 0) {
                    themeAccent.backgroundGradientOverrideColor3 = (long) tLRPC$WallPaperSettings.fourth_background_color;
                } else {
                    themeAccent.backgroundGradientOverrideColor3 = 4294967296L;
                }
                themeAccent.backgroundRotation = AndroidUtilities.getWallpaperRotation(tLRPC$WallPaperSettings.rotation, false);
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
            themeAccent2.backgroundGradientOverrideColor1 = themeAccent.backgroundGradientOverrideColor1;
            themeAccent2.backgroundGradientOverrideColor2 = themeAccent.backgroundGradientOverrideColor2;
            themeAccent2.backgroundGradientOverrideColor3 = themeAccent.backgroundGradientOverrideColor3;
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
                    int i2 = this.patternBgGradientColor2;
                    if (i2 != 0) {
                        i = MotionBackgroundDrawable.getPatternColor(this.patternBgColor, this.patternBgGradientColor1, i2, this.patternBgGradientColor3);
                    } else {
                        int i3 = this.patternBgGradientColor1;
                        if (i3 != 0) {
                            i = AndroidUtilities.getAverageColor(this.patternBgColor, i3);
                            GradientDrawable gradientDrawable = new GradientDrawable(BackgroundGradientDrawable.getGradientOrientation(this.patternBgGradientRotation), new int[]{this.patternBgColor, this.patternBgGradientColor1});
                            gradientDrawable.setBounds(0, 0, createBitmap.getWidth(), createBitmap.getHeight());
                            gradientDrawable.draw(canvas);
                        } else {
                            i = AndroidUtilities.getPatternColor(this.patternBgColor);
                            canvas.drawColor(this.patternBgColor);
                        }
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
                scaledBitmap.compress(this.patternBgGradientColor2 != 0 ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, 87, fileOutputStream);
                fileOutputStream.close();
                return true;
            } catch (Throwable th) {
                FileLog.e(th);
                return false;
            }
        }

        public void didReceivedNotification(int i, int i2, Object... objArr) {
            int i3 = NotificationCenter.fileDidLoad;
            if (i == i3 || i == NotificationCenter.fileDidFailToLoad) {
                String str = objArr[0];
                TLRPC$TL_theme tLRPC$TL_theme = this.info;
                if (tLRPC$TL_theme != null && tLRPC$TL_theme.document != null) {
                    if (str.equals(this.loadingThemeWallpaperName)) {
                        this.loadingThemeWallpaperName = null;
                        Utilities.globalQueue.postRunnable(new Runnable(objArr[1]) {
                            public final /* synthetic */ File f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                Theme.ThemeInfo.this.lambda$didReceivedNotification$0$Theme$ThemeInfo(this.f$1);
                            }
                        });
                    } else if (str.equals(FileLoader.getAttachFileName(this.info.document))) {
                        removeObservers();
                        if (i == i3) {
                            File file = new File(this.pathToFile);
                            TLRPC$TL_theme tLRPC$TL_theme2 = this.info;
                            ThemeInfo fillThemeValues = Theme.fillThemeValues(file, tLRPC$TL_theme2.title, tLRPC$TL_theme2);
                            if (fillThemeValues == null || fillThemeValues.pathToWallpaper == null || new File(fillThemeValues.pathToWallpaper).exists()) {
                                onFinishLoadingRemoteTheme();
                                return;
                            }
                            this.patternBgColor = fillThemeValues.patternBgColor;
                            this.patternBgGradientColor1 = fillThemeValues.patternBgGradientColor1;
                            this.patternBgGradientColor2 = fillThemeValues.patternBgGradientColor2;
                            this.patternBgGradientColor3 = fillThemeValues.patternBgGradientColor3;
                            this.patternBgGradientRotation = fillThemeValues.patternBgGradientRotation;
                            this.isBlured = fillThemeValues.isBlured;
                            this.patternIntensity = fillThemeValues.patternIntensity;
                            this.newPathToWallpaper = fillThemeValues.pathToWallpaper;
                            TLRPC$TL_account_getWallPaper tLRPC$TL_account_getWallPaper = new TLRPC$TL_account_getWallPaper();
                            TLRPC$TL_inputWallPaperSlug tLRPC$TL_inputWallPaperSlug = new TLRPC$TL_inputWallPaperSlug();
                            tLRPC$TL_inputWallPaperSlug.slug = fillThemeValues.slug;
                            tLRPC$TL_account_getWallPaper.wallpaper = tLRPC$TL_inputWallPaperSlug;
                            ConnectionsManager.getInstance(fillThemeValues.account).sendRequest(tLRPC$TL_account_getWallPaper, new RequestDelegate(fillThemeValues) {
                                public final /* synthetic */ Theme.ThemeInfo f$1;

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

        /* access modifiers changed from: private */
        /* renamed from: lambda$didReceivedNotification$0 */
        public /* synthetic */ void lambda$didReceivedNotification$0$Theme$ThemeInfo(File file) {
            createBackground(file, this.newPathToWallpaper);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    Theme.ThemeInfo.this.onFinishLoadingRemoteTheme();
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$didReceivedNotification$2 */
        public /* synthetic */ void lambda$didReceivedNotification$2$Theme$ThemeInfo(ThemeInfo themeInfo, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable(tLObject, themeInfo) {
                public final /* synthetic */ TLObject f$1;
                public final /* synthetic */ Theme.ThemeInfo f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    Theme.ThemeInfo.this.lambda$didReceivedNotification$1$Theme$ThemeInfo(this.f$1, this.f$2);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$didReceivedNotification$1 */
        public /* synthetic */ void lambda$didReceivedNotification$1$Theme$ThemeInfo(TLObject tLObject, ThemeInfo themeInfo) {
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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v329, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v330, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v331, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v332, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v333, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v118, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v334, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v119, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v445, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v338, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v120, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v339, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v340, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v341, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v130, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v342, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v152, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v350, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v351, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v367, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v368, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v369, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v370, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v371, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v372, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v373, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v374, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v375, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v376, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v380, resolved type: org.telegram.ui.ActionBar.Theme$ThemeInfo} */
    /* JADX WARNING: type inference failed for: r5v13, types: [boolean] */
    /* JADX WARNING: type inference failed for: r5v15 */
    /* JADX WARNING: type inference failed for: r2v153 */
    /* JADX WARNING: type inference failed for: r2v154 */
    /* JADX WARNING: type inference failed for: r5v17 */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x31f7, code lost:
        if (r0 == null) goto L_0x3205;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x31f9, code lost:
        r0 = themesDict.get(r0);
        r9 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x3201, code lost:
        if (r0 == null) goto L_0x3205;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x3203, code lost:
        currentNightTheme = r0;
        r9 = r9;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x333a A[Catch:{ all -> 0x3360 }] */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x3344 A[Catch:{ all -> 0x335e }] */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x3483 A[Catch:{ Exception -> 0x356b }] */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x349e A[Catch:{ Exception -> 0x356b }] */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x34a1 A[Catch:{ Exception -> 0x356b }] */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x34ae A[Catch:{ Exception -> 0x356b }] */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x357c  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x357f  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x35dc  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x35e5  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x3633  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x3652  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x3657  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x3659  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x34c1 A[SYNTHETIC] */
    static {
        /*
            java.lang.Object r0 = new java.lang.Object
            r0.<init>()
            sync = r0
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
            r6 = 12
            android.graphics.drawable.Drawable[] r6 = new android.graphics.drawable.Drawable[r6]
            avatarDrawables = r6
            r6 = 5
            org.telegram.ui.Components.StatusDrawable[] r7 = new org.telegram.ui.Components.StatusDrawable[r6]
            chat_status_drawables = r7
            r7 = 2
            android.graphics.drawable.Drawable[] r8 = new android.graphics.drawable.Drawable[r7]
            chat_msgInCallDrawable = r8
            android.graphics.drawable.Drawable[] r8 = new android.graphics.drawable.Drawable[r7]
            chat_msgInCallSelectedDrawable = r8
            android.graphics.drawable.Drawable[] r8 = new android.graphics.drawable.Drawable[r7]
            chat_msgOutCallDrawable = r8
            android.graphics.drawable.Drawable[] r8 = new android.graphics.drawable.Drawable[r7]
            chat_msgOutCallSelectedDrawable = r8
            android.graphics.drawable.Drawable[] r8 = new android.graphics.drawable.Drawable[r7]
            chat_pollCheckDrawable = r8
            android.graphics.drawable.Drawable[] r8 = new android.graphics.drawable.Drawable[r7]
            chat_pollCrossDrawable = r8
            android.graphics.drawable.Drawable[] r8 = new android.graphics.drawable.Drawable[r7]
            chat_pollHintDrawable = r8
            android.graphics.drawable.Drawable[] r8 = new android.graphics.drawable.Drawable[r7]
            chat_psaHelpDrawable = r8
            r8 = 6
            org.telegram.ui.Components.RLottieDrawable[] r9 = new org.telegram.ui.Components.RLottieDrawable[r8]
            chat_attachButtonDrawables = r9
            android.graphics.drawable.Drawable[] r9 = new android.graphics.drawable.Drawable[r7]
            chat_locationDrawable = r9
            android.graphics.drawable.Drawable[] r9 = new android.graphics.drawable.Drawable[r7]
            chat_contactDrawable = r9
            int[] r9 = new int[r7]
            r9 = {10, 2} // fill-array
            java.lang.Class<android.graphics.drawable.Drawable> r10 = android.graphics.drawable.Drawable.class
            java.lang.Object r9 = java.lang.reflect.Array.newInstance(r10, r9)
            android.graphics.drawable.Drawable[][] r9 = (android.graphics.drawable.Drawable[][]) r9
            chat_fileStatesDrawable = r9
            int[] r9 = new int[r7]
            r9 = {6, 2} // fill-array
            java.lang.Class<org.telegram.ui.Components.CombinedDrawable> r10 = org.telegram.ui.Components.CombinedDrawable.class
            java.lang.Object r9 = java.lang.reflect.Array.newInstance(r10, r9)
            org.telegram.ui.Components.CombinedDrawable[][] r9 = (org.telegram.ui.Components.CombinedDrawable[][]) r9
            chat_fileMiniStatesDrawable = r9
            int[] r9 = new int[r7]
            r9 = {13, 2} // fill-array
            java.lang.Class<android.graphics.drawable.Drawable> r10 = android.graphics.drawable.Drawable.class
            java.lang.Object r9 = java.lang.reflect.Array.newInstance(r10, r9)
            android.graphics.drawable.Drawable[][] r9 = (android.graphics.drawable.Drawable[][]) r9
            chat_photoStatesDrawables = r9
            android.graphics.Path[] r9 = new android.graphics.Path[r7]
            chat_filePath = r9
            android.graphics.Path[] r9 = new android.graphics.Path[r4]
            chat_updatePath = r9
            r9 = 7
            java.lang.String[] r10 = new java.lang.String[r9]
            java.lang.String r11 = "avatar_backgroundRed"
            r10[r1] = r11
            java.lang.String r11 = "avatar_backgroundOrange"
            r10[r5] = r11
            java.lang.String r11 = "avatar_backgroundViolet"
            r10[r7] = r11
            java.lang.String r11 = "avatar_backgroundGreen"
            r10[r4] = r11
            java.lang.String r11 = "avatar_backgroundCyan"
            r12 = 4
            r10[r12] = r11
            java.lang.String r11 = "avatar_backgroundBlue"
            r10[r6] = r11
            java.lang.String r11 = "avatar_backgroundPink"
            r10[r8] = r11
            keys_avatar_background = r10
            java.lang.String[] r10 = new java.lang.String[r9]
            java.lang.String r11 = "avatar_nameInMessageRed"
            r10[r1] = r11
            java.lang.String r11 = "avatar_nameInMessageOrange"
            r10[r5] = r11
            java.lang.String r11 = "avatar_nameInMessageViolet"
            r10[r7] = r11
            java.lang.String r11 = "avatar_nameInMessageGreen"
            r10[r4] = r11
            java.lang.String r11 = "avatar_nameInMessageCyan"
            r10[r12] = r11
            java.lang.String r11 = "avatar_nameInMessageBlue"
            r10[r6] = r11
            java.lang.String r11 = "avatar_nameInMessagePink"
            r10[r8] = r11
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
            r13 = -1743531(0xffffffffffe56555, float:NaN)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogSwipeRemove"
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
            java.lang.String r8 = "fastScrollActive"
            r9.put(r8, r15)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -3551791(0xffffffffffc9cdd1, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "fastScrollInactive"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "fastScrollText"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "avatar_text"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -10043398(0xfffffffffvar_bffa, float:-3.0671924E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_backgroundSaved"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -5654847(0xffffffffffa9b6c1, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_backgroundArchived"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -10043398(0xfffffffffvar_bffa, float:-3.0671924E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_backgroundArchivedHidden"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -1743531(0xffffffffffe56555, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_backgroundRed"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -881592(0xffffffffffvar_CLASSNAME, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_backgroundOrange"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -7436818(0xffffffffff8e85ee, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_backgroundViolet"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -8992691(0xfffffffffvar_CLASSNAMEd, float:-3.280301E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_backgroundGreen"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -10502443(0xffffffffff5fbed5, float:-2.974087E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_backgroundCyan"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -11232035(0xfffffffffvar_cdd, float:-2.8261082E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_backgroundBlue"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -887654(0xffffffffffvar_a, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_backgroundPink"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -11500111(0xfffffffffvar_b1, float:-2.7717359E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_backgroundInProfileBlue"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -10907718(0xfffffffffvar_fba, float:-2.8918875E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_backgroundActionBarBlue"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -2626822(0xffffffffffd7eafa, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_subtitleInProfileBlue"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -11959891(0xfffffffffvar_ad, float:-2.6784814E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_actionBarSelectorBlue"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "avatar_actionBarIconBlue"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -3516848(0xffffffffffca5650, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_nameInMessageRed"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -2589911(0xffffffffffd87b29, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_nameInMessageOrange"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -11627828(0xffffffffff4e92cc, float:-2.7458318E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_nameInMessageViolet"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -11488718(0xfffffffffvar_b232, float:-2.7740467E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_nameInMessageGreen"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -13132104(0xfffffffffvar_eb8, float:-2.4407284E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_nameInMessageCyan"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -11627828(0xffffffffff4e92cc, float:-2.7458318E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_nameInMessageBlue"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -11627828(0xffffffffff4e92cc, float:-2.7458318E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "avatar_nameInMessagePink"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -11371101(0xfffffffffvar_da3, float:-2.7979022E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarDefault"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "actionBarDefaultIcon"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "actionBarActionModeDefault"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = 268435456(0x10000000, float:2.5243549E-29)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarActionModeDefaultTop"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -9999761(0xfffffffffvar_a6f, float:-3.076043E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarActionModeDefaultIcon"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "actionBarDefaultTitle"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -2758409(0xffffffffffd5e8f7, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarDefaultSubtitle"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -12554860(0xfffffffffvar_d94, float:-2.5578074E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarDefaultSelector"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = 486539264(0x1d000000, float:1.6940659E-21)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarWhiteSelector"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "actionBarDefaultSearch"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -1996488705(0xfffffffvar_ffffff, float:-1.5407439E-33)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarDefaultSearchPlaceholder"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "actionBarDefaultSubmenuItem"
            r8.put(r9, r11)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -9999504(0xfffffffffvar_b70, float:-3.0760951E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarDefaultSubmenuItemIcon"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "actionBarDefaultSubmenuBackground"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -1907998(0xffffffffffe2e2e2, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarActionModeDefaultSelector"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "actionBarTabActiveText"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -2758409(0xffffffffffd5e8f7, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarTabUnactiveText"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "actionBarTabLine"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -12554860(0xfffffffffvar_d94, float:-2.5578074E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarTabSelector"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "actionBarBrowser"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -9471353(0xffffffffff6f7a87, float:-3.1832169E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarDefaultArchived"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -10590350(0xffffffffff5e6772, float:-2.9562573E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarDefaultArchivedSelector"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "actionBarDefaultArchivedIcon"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "actionBarDefaultArchivedTitle"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "actionBarDefaultArchivedSearch"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -1996488705(0xfffffffvar_ffffff, float:-1.5407439E-33)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "actionBarDefaultSearchArchivedPlaceholder"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -11810020(0xffffffffff4bcb1c, float:-2.7088789E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_onlineCircle"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -11613090(0xffffffffff4ecc5e, float:-2.748821E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_unreadCounter"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -3749428(0xffffffffffc6c9cc, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_unreadCounterMuted"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chats_unreadCounterText"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -10049056(0xfffffffffvar_a9e0, float:-3.0660448E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_archiveBackground"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -6313293(0xffffffffff9faab3, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_archivePinBackground"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chats_archiveIcon"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chats_archiveText"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chats_name"
            r8.put(r9, r11)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -11382190(0xfffffffffvar_, float:-2.7956531E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_nameArchived"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -16734706(0xfffffffffvar_a60e, float:-1.7100339E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_secretName"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -15093466(0xfffffffffvar_b126, float:-2.042917E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_secretIcon"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -14408668(0xfffffffffvar_, float:-2.1818104E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_nameIcon"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -5723992(0xffffffffffa8a8a8, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_pinnedIcon"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -7631473(0xffffffffff8b8d8f, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_message"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -7237231(0xfffffffffvar_, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_messageArchived"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -7434095(0xffffffffff8e9091, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_message_threeLines"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -2274503(0xffffffffffdd4b39, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_draft"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -12812624(0xffffffffff3c7eb0, float:-2.5055266E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_nameMessage"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -7631473(0xffffffffff8b8d8f, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_nameMessageArchived"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -12434359(0xfffffffffvar_, float:-2.5822479E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_nameMessage_threeLines"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -10592674(0xffffffffff5e5e5e, float:-2.955786E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_nameMessageArchived_threeLines"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -12812624(0xffffffffff3c7eb0, float:-2.5055266E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_attachMessage"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -12812624(0xffffffffff3c7eb0, float:-2.5055266E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_actionMessage"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -6973028(0xfffffffffvar_c, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_date"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = 134217728(0x8000000, float:3.85186E-34)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_pinnedOverlay"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = 251658240(0xvar_, float:6.3108872E-30)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_tabletSelectedOverlay"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -12146122(0xfffffffffvar_aa36, float:-2.6407093E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_sentCheck"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -12146122(0xfffffffffvar_aa36, float:-2.6407093E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_sentReadCheck"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -9061026(0xfffffffffvar_bd5e, float:-3.266441E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_sentClock"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -2796974(0xffffffffffd55252, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_sentError"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chats_sentErrorIcon"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -13391642(0xfffffffffvar_a8e6, float:-2.3880878E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_verifiedBackground"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chats_verifiedCheck"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -4341308(0xffffffffffbdc1c4, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_muteIcon"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chats_mentionIcon"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chats_menuBackground"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -12303292(0xfffffffffvar_, float:-2.6088314E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_menuItemText"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -10907718(0xfffffffffvar_fba, float:-2.8918875E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_menuItemCheck"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -7827048(0xfffffffffvar_, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_menuItemIcon"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chats_menuName"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chats_menuPhone"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -4004353(0xffffffffffc2e5ff, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_menuPhoneCats"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chats_menuCloud"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -12420183(0xfffffffffvar_ba9, float:-2.5851231E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_menuCloudBackgroundCats"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chats_actionIcon"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -10114592(0xfffffffffvar_a9e0, float:-3.0527525E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_actionBackground"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -11100714(0xfffffffffvar_dd6, float:-2.8527432E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_actionPressedBackground"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -9211021(0xfffffffffvar_, float:-3.2360185E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_actionUnreadIcon"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chats_actionUnreadBackground"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -855310(0xfffffffffff2f2f2, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_actionUnreadPressedBackground"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -10907718(0xfffffffffvar_fba, float:-2.8918875E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_menuTopBackgroundCats"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -3749428(0xffffffffffc6c9cc, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_archivePullDownBackground"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -10049056(0xfffffffffvar_a9e0, float:-3.0660448E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chats_archivePullDownBackgroundActive"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -12171706(0xfffffffffvar_, float:-2.6355202E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachMediaBanBackground"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chat_attachMediaBanText"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chat_attachCheckBoxCheck"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -12995849(0xfffffffffvar_b2f7, float:-2.4683642E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachCheckBoxBackground"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = 201326592(0xCLASSNAME, float:9.8607613E-32)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachPhotoBackground"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -13391883(0xfffffffffvar_a7f5, float:-2.388039E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachActiveTab"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -7169634(0xfffffffffvar_e, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachUnactiveTab"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -13421773(0xfffffffffvar_, float:-2.3819765E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachPermissionImage"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -1945520(0xffffffffffe25050, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachPermissionMark"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -9472134(0xffffffffff6var_a, float:-3.1830585E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachPermissionText"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -3355444(0xffffffffffcccccc, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachEmptyImage"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -12214795(0xfffffffffvar_df5, float:-2.6267807E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachGalleryBackground"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -13726231(0xffffffffff2e8de9, float:-2.3202251E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachGalleryText"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chat_attachGalleryIcon"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -1351584(0xffffffffffeb6060, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachAudioBackground"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -2209977(0xffffffffffde4747, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachAudioText"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chat_attachAudioIcon"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -13321743(0xfffffffffvar_b9f1, float:-2.402265E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachFileBackground"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -15423260(0xfffffffffvar_a8e4, float:-1.9760267E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachFileText"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chat_attachFileIcon"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -868277(0xfffffffffff2CLASSNAMEb, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachContactBackground"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -2121728(0xffffffffffdfa000, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachContactText"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chat_attachContactIcon"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -10436011(0xfffffffffvar_CLASSNAME, float:-2.987561E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachLocationBackground"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -12801233(0xffffffffff3cab2f, float:-2.507837E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachLocationText"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chat_attachLocationIcon"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -868277(0xfffffffffff2CLASSNAMEb, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachPollBackground"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -2121728(0xffffffffffdfa000, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_attachPollText"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chat_attachPollIcon"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -10436011(0xfffffffffvar_CLASSNAME, float:-2.987561E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_inPollCorrectAnswer"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -10436011(0xfffffffffvar_CLASSNAME, float:-2.987561E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outPollCorrectAnswer"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -1351584(0xffffffffffeb6060, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_inPollWrongAnswer"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -1351584(0xffffffffffeb6060, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outPollWrongAnswer"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -2758409(0xffffffffffd5e8f7, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_status"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -16725933(0xfffffffffvar_CLASSNAME, float:-1.7118133E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_inDownCall"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -47032(0xfffffffffffvar_, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_inUpCall"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -16725933(0xfffffffffvar_CLASSNAME, float:-1.7118133E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outUpCall"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chat_lockIcon"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -5124893(0xffffffffffb1cce3, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_muteIcon"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chat_inBubble"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -1247235(0xffffffffffecf7fd, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_inBubbleSelected"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -14862509(0xffffffffff1d3753, float:-2.0897606E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_inBubbleShadow"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -1048610(0xffffffffffefffde, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outBubble"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = 335544320(0x14000000, float:6.4623485E-27)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outBubbleGradientSelectedOverlay"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -2492475(0xffffffffffd9f7c5, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outBubbleSelected"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -14781172(0xffffffffff1e750c, float:-2.1062577E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outBubbleShadow"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chat_inMediaIcon"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -1050370(0xffffffffffeff8fe, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_inMediaIconSelected"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -1048610(0xffffffffffefffde, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outMediaIcon"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -1967921(0xffffffffffe1f8cf, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outMediaIconSelected"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chat_messageTextIn"
            r8.put(r9, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chat_messageTextOut"
            r8.put(r9, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -14255946(0xfffffffffvar_b6, float:-2.2127861E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_messageLinkIn"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -14255946(0xfffffffffvar_b6, float:-2.2127861E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_messageLinkOut"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chat_serviceText"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chat_serviceLink"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            java.lang.String r9 = "chat_serviceIcon"
            r8.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = 1711276032(0x66000000, float:1.5111573E23)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_mediaTimeBackground"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -10637232(0xffffffffff5db050, float:-2.9467485E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outSentCheck"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -10637232(0xffffffffff5db050, float:-2.9467485E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outSentCheckSelected"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -10637232(0xffffffffff5db050, float:-2.9467485E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outSentCheckRead"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -10637232(0xffffffffff5db050, float:-2.9467485E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outSentCheckReadSelected"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -9061026(0xfffffffffvar_bd5e, float:-3.266441E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outSentClock"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -9061026(0xfffffffffvar_bd5e, float:-3.266441E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_outSentClockSelected"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r9 = -6182221(0xffffffffffa1aab3, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "chat_inSentClock"
            r8.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = defaultColors
            r15 = -7094838(0xfffffffffvar_bdca, float:NaN)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            java.lang.String r6 = "chat_inSentClockSelected"
            r8.put(r6, r15)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r8 = "chat_mediaSentCheck"
            r6.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r8 = "chat_mediaSentClock"
            r6.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r8 = "chat_inViews"
            r6.put(r8, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r8 = -7094838(0xfffffffffvar_bdca, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inViewsSelected"
            r6.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r8 = -9522601(0xffffffffff6eb257, float:-3.1728226E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outViews"
            r6.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r8 = -9522601(0xffffffffff6eb257, float:-3.1728226E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outViewsSelected"
            r6.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r8 = "chat_mediaViews"
            r6.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r8 = -4801083(0xffffffffffb6bdc5, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inMenu"
            r6.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r8 = -6766130(0xfffffffffvar_c1ce, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inMenuSelected"
            r6.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r8 = -7221634(0xfffffffffvar_ce7e, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outMenu"
            r6.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r8 = -7221634(0xfffffffffvar_ce7e, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outMenuSelected"
            r6.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r8 = "chat_mediaMenu"
            r6.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r8 = -11162801(0xfffffffffvar_ab4f, float:-2.8401505E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outInstant"
            r6.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r15 = -12019389(0xfffffffffvar_, float:-2.6664138E38)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            java.lang.String r12 = "chat_outInstantSelected"
            r6.put(r12, r15)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_inInstant"
            r6.put(r12, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -13600331(0xfffffffffvar_b5, float:-2.3457607E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_inInstantSelected"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -2411211(0xffffffffffdb3535, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_sentError"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_sentErrorIcon"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = 671781104(0x280a90f0, float:7.691967E-15)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_selectedBackground"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_previewDurationText"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_previewGameText"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_inPreviewInstantText"
            r6.put(r12, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_outPreviewInstantText"
            r6.put(r12, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -13600331(0xfffffffffvar_b5, float:-2.3457607E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_inPreviewInstantSelectedText"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -12019389(0xfffffffffvar_, float:-2.6664138E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_outPreviewInstantSelectedText"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -1776928(0xffffffffffe4e2e0, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_secretTimeText"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_stickerNameText"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_botButtonText"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_botProgress"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -13072697(0xfffffffffvar_c7, float:-2.4527776E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_inForwardedNameText"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_outForwardedNameText"
            r6.put(r12, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -10838983(0xffffffffff5a9CLASSNAME, float:-2.9058286E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_inPsaNameText"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -10838983(0xffffffffff5a9CLASSNAME, float:-2.9058286E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_outPsaNameText"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_inViaBotNameText"
            r6.put(r12, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_outViaBotNameText"
            r6.put(r12, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_stickerViaBotNameText"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -10903592(0xfffffffffvar_fd8, float:-2.8927243E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_inReplyLine"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -9520791(0xffffffffff6eb969, float:-3.1731897E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_outReplyLine"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_stickerReplyLine"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_inReplyNameText"
            r6.put(r12, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_outReplyNameText"
            r6.put(r12, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_stickerReplyNameText"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_inReplyMessageText"
            r6.put(r12, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_outReplyMessageText"
            r6.put(r12, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_inReplyMediaMessageText"
            r6.put(r12, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -10112933(0xfffffffffvar_b05b, float:-3.053089E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_outReplyMediaMessageText"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r15 = -7752511(0xfffffffffvar_b4c1, float:NaN)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            java.lang.String r4 = "chat_inReplyMediaMessageSelectedText"
            r6.put(r4, r15)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_outReplyMediaMessageSelectedText"
            r4.put(r6, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_stickerReplyMessageText"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -9390872(0xfffffffffvar_b4e8, float:-3.1995404E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_inPreviewLine"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7812741(0xfffffffffvar_CLASSNAMEb, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_outPreviewLine"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_inSiteNameText"
            r4.put(r6, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_outSiteNameText"
            r4.put(r6, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11625772(0xffffffffff4e9ad4, float:-2.7462488E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_inContactNameText"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_outContactNameText"
            r4.put(r6, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_inContactPhoneText"
            r4.put(r6, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_inContactPhoneSelectedText"
            r4.put(r6, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -13286860(0xfffffffffvar_, float:-2.4093401E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_outContactPhoneText"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -13286860(0xfffffffffvar_, float:-2.4093401E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_outContactPhoneSelectedText"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_mediaProgress"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_inAudioProgress"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -1048610(0xffffffffffefffde, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_outAudioProgress"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -1050370(0xffffffffffeff8fe, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_inAudioSelectedProgress"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -1967921(0xffffffffffe1f8cf, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_outAudioSelectedProgress"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_mediaTimeText"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -4143413(0xffffffffffc0c6cb, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_adminText"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7752511(0xfffffffffvar_b4c1, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_adminSelectedText"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -9391780(0xfffffffffvar_b15c, float:-3.1993562E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_outAdminText"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -9391780(0xfffffffffvar_b15c, float:-3.1993562E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_outAdminSelectedText"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_inTimeText"
            r4.put(r6, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7752511(0xfffffffffvar_b4c1, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_inTimeSelectedText"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -9391780(0xfffffffffvar_b15c, float:-3.1993562E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_outTimeText"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -9391780(0xfffffffffvar_b15c, float:-3.1993562E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_outTimeSelectedText"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_inAudioPerfomerText"
            r4.put(r6, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_inAudioPerfomerSelectedText"
            r4.put(r6, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -13286860(0xfffffffffvar_, float:-2.4093401E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_outAudioPerfomerText"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -13286860(0xfffffffffvar_, float:-2.4093401E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_outAudioPerfomerSelectedText"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11625772(0xffffffffff4e9ad4, float:-2.7462488E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_inAudioTitleText"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_outAudioTitleText"
            r4.put(r6, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_inAudioDurationText"
            r4.put(r6, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_outAudioDurationText"
            r4.put(r6, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7752511(0xfffffffffvar_b4c1, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_inAudioDurationSelectedText"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_outAudioDurationSelectedText"
            r4.put(r6, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -1774864(0xffffffffffe4eaf0, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_inAudioSeekbar"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 1071966960(0x3fe4eaf0, float:1.7884197)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_inAudioCacheSeekbar"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -4463700(0xffffffffffbbe3ac, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_outAudioSeekbar"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 1069278124(0x3fbbe3ac, float:1.4678855)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_outAudioCacheSeekbar"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -4399384(0xffffffffffbcdee8, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_inAudioSeekbarSelected"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -5644906(0xffffffffffa9dd96, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_outAudioSeekbarSelected"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -9259544(0xfffffffffvar_b5e8, float:-3.2261769E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_inAudioSeekbarFill"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -8863118(0xfffffffffvar_CLASSNAME, float:-3.3065816E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_outAudioSeekbarFill"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -2169365(0xffffffffffdee5eb, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_inVoiceSeekbar"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -4463700(0xffffffffffbbe3ac, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_outVoiceSeekbar"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -4399384(0xffffffffffbcdee8, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_inVoiceSeekbarSelected"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -5644906(0xffffffffffa9dd96, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_outVoiceSeekbarSelected"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -9259544(0xfffffffffvar_b5e8, float:-3.2261769E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_inVoiceSeekbarFill"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -8863118(0xfffffffffvar_CLASSNAME, float:-3.3065816E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_outVoiceSeekbarFill"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -1314571(0xffffffffffebf0f5, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_inFileProgress"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -2427453(0xffffffffffdaf5c3, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_outFileProgress"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -3413258(0xffffffffffcbeaf6, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_inFileProgressSelected"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -3806041(0xffffffffffc5eca7, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_outFileProgressSelected"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11625772(0xffffffffff4e9ad4, float:-2.7462488E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_inFileNameText"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_outFileNameText"
            r4.put(r6, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_inFileInfoText"
            r4.put(r6, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_outFileInfoText"
            r4.put(r6, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7752511(0xfffffffffvar_b4c1, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_inFileInfoSelectedText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_outFileInfoSelectedText"
            r4.put(r6, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -1314571(0xffffffffffebf0f5, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_inFileBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -2427453(0xffffffffffdaf5c3, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_outFileBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -3413258(0xffffffffffcbeaf6, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_inFileBackgroundSelected"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -3806041(0xffffffffffc5eca7, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_outFileBackgroundSelected"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_inVenueInfoText"
            r4.put(r6, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_outVenueInfoText"
            r4.put(r6, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7752511(0xfffffffffvar_b4c1, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_inVenueInfoSelectedText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_outVenueInfoSelectedText"
            r4.put(r6, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_mediaInfoText"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 862104035(0x3362a9e3, float:5.2774237E-8)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_linkSelectBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 1717742051(0x6662a9e3, float:2.6759717E23)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_textSelectBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -986379(0xfffffffffff0f2f5, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_emojiPanelBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11688214(0xffffffffff4da6ea, float:-2.733584E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_emojiPanelBadgeBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_emojiPanelBadgeText"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -1709586(0xffffffffffe5e9ee, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_emojiSearchBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7036497(0xfffffffffvar_a1af, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_emojiSearchIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 301989888(0x12000000, float:4.0389678E-28)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_emojiPanelShadowLine"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7038047(0xfffffffffvar_ba1, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_emojiPanelEmptyText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -6445909(0xffffffffff9da4ab, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_emojiPanelIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7564905(0xffffffffff8CLASSNAME, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_emojiBottomPanelIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -13920286(0xffffffffff2b97e2, float:-2.280866E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_emojiPanelIconSelected"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -1907225(0xffffffffffe2e5e7, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_emojiPanelStickerPackSelector"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11097104(0xfffffffffvar_abf0, float:-2.8534754E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_emojiPanelStickerPackSelectorLine"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7564905(0xffffffffff8CLASSNAME, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_emojiPanelBackspace"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_emojiPanelMasksIcon"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -10305560(0xfffffffffvar_bfe8, float:-3.0140196E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_emojiPanelMasksIconSelected"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_emojiPanelTrendingTitle"
            r4.put(r6, r11)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -8221804(0xfffffffffvar_b94, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_emojiPanelStickerSetName"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -14184997(0xfffffffffvar_ddb, float:-2.2271763E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_emojiPanelStickerSetNameHighlight"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -5130564(0xffffffffffb1b6bc, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_emojiPanelStickerSetNameIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7697782(0xffffffffff8a8a8a, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_emojiPanelTrendingDescription"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -13220017(0xfffffffffvar_f, float:-2.4228975E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_botKeyboardButtonText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -1775639(0xffffffffffe4e7e9, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_botKeyboardButtonBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -3354156(0xffffffffffccd1d4, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_botKeyboardButtonBackgroundPressed"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -6113849(0xffffffffffa2b5c7, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_unreadMessagesStartArrowIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11102772(0xfffffffffvar_cc, float:-2.8523258E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_unreadMessagesStartText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_unreadMessagesStartBackground"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -6113849(0xffffffffffa2b5c7, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_inFileIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7883067(0xfffffffffvar_b6c5, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_inFileSelectedIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -8011912(0xfffffffffvar_bvar_, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_outFileIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -8011912(0xfffffffffvar_bvar_, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_outFileSelectedIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -1314571(0xffffffffffebf0f5, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_inLocationBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -6113849(0xffffffffffa2b5c7, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_inLocationIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -2427453(0xffffffffffdaf5c3, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_outLocationBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7880840(0xfffffffffvar_bvar_, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_outLocationIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -9259544(0xfffffffffvar_b5e8, float:-3.2261769E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_inContactBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_inContactIcon"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -8863118(0xfffffffffvar_CLASSNAME, float:-3.3065816E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_outContactBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -1048610(0xffffffffffefffde, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_outContactIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -12146122(0xfffffffffvar_aa36, float:-2.6407093E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_outBroadcast"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_mediaBroadcast"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -9999761(0xfffffffffvar_a6f, float:-3.076043E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_searchPanelIcons"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -9999761(0xfffffffffvar_a6f, float:-3.076043E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_searchPanelText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -8421505(0xffffffffff7f7f7f, float:-3.3961514E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_secretChatStatusText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_fieldOverlayText"
            r4.put(r6, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_stickersHintPanel"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11032346(0xfffffffffvar_a8e6, float:-2.86661E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_replyPanelIcons"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7432805(0xffffffffff8e959b, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_replyPanelClose"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_replyPanelName"
            r4.put(r6, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_replyPanelMessage"
            r4.put(r6, r11)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -1513240(0xffffffffffe8e8e8, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_replyPanelLine"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_messagePanelBackground"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_messagePanelText"
            r4.put(r6, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -5985101(0xffffffffffa4acb3, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_messagePanelHint"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11230757(0xfffffffffvar_a1db, float:-2.8263674E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_messagePanelCursor"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_messagePanelShadow"
            r4.put(r6, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7432805(0xffffffffff8e959b, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_messagePanelIcons"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_recordedVoicePlayPause"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -2468275(0xffffffffffda564d, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_recordedVoiceDot"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -10637848(0xffffffffff5dade8, float:-2.9466236E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_recordedVoiceBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -5120257(0xffffffffffb1deff, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_recordedVoiceProgress"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_recordedVoiceProgressInner"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -12937772(0xffffffffff3a95d4, float:-2.4801436E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_recordVoiceCancel"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 1694498815(0x64ffffff, float:3.777893E22)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "key_chat_recordedVoiceHighlight"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -10309397(0xfffffffffvar_b0eb, float:-3.0132414E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_messagePanelSend"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -5987164(0xffffffffffa4a4a4, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "key_chat_messagePanelVoiceLock"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "key_chat_messagePanelVoiceLockBackground"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "key_chat_messagePanelVoiceLockShadow"
            r4.put(r6, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7432805(0xffffffffff8e959b, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_recordTime"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11688214(0xffffffffff4da6ea, float:-2.733584E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_emojiPanelNewTrending"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_gifSaveHintText"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -871296751(0xffffffffcCLASSNAME, float:-3.8028356E7)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_gifSaveHintBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_goDownButton"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_goDownButtonShadow"
            r4.put(r6, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7432805(0xffffffffff8e959b, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_goDownButtonIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_goDownButtonCounter"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11689240(0xffffffffff4da2e8, float:-2.733376E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_goDownButtonCounterBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -5395027(0xffffffffffadadad, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_messagePanelCancelInlineBot"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_messagePanelVoicePressed"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -10639650(0xffffffffff5da6de, float:-2.9462581E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_messagePanelVoiceBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -9211021(0xfffffffffvar_, float:-3.2360185E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_messagePanelVoiceDelete"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_messagePanelVoiceDuration"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11037236(0xfffffffffvar_cc, float:-2.865618E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_inlineResultIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_topPanelBackground"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7629157(0xffffffffff8b969b, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_topPanelClose"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -9658414(0xffffffffff6c9fd2, float:-3.1452764E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_topPanelLine"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_topPanelTitle"
            r4.put(r6, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7893359(0xfffffffffvar_e91, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_topPanelMessage"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -3188393(0xffffffffffcvar_, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_reportSpam"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11894091(0xffffffffff4a82b5, float:-2.6918272E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_addContact"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -9259544(0xfffffffffvar_b5e8, float:-3.2261769E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_inLoader"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -10114080(0xfffffffffvar_abe0, float:-3.0528564E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_inLoaderSelected"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -8863118(0xfffffffffvar_CLASSNAME, float:-3.3065816E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_outLoader"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -9783964(0xffffffffff6ab564, float:-3.1198118E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_outLoaderSelected"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -6113080(0xffffffffffa2b8c8, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_inLoaderPhoto"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -6113849(0xffffffffffa2b5c7, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_inLoaderPhotoSelected"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -197380(0xfffffffffffcfcfc, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_inLoaderPhotoIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -1314571(0xffffffffffebf0f5, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_inLoaderPhotoIconSelected"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -8011912(0xfffffffffvar_bvar_, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_outLoaderPhoto"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -8538000(0xffffffffff7db870, float:-3.3725234E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_outLoaderPhotoSelected"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -2427453(0xffffffffffdaf5c3, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_outLoaderPhotoIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -4134748(0xffffffffffc0e8a4, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_outLoaderPhotoIconSelected"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 1711276032(0x66000000, float:1.5111573E23)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_mediaLoaderPhoto"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 2130706432(0x7var_, float:1.7014118E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_mediaLoaderPhotoSelected"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_mediaLoaderPhotoIcon"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -2500135(0xffffffffffd9d9d9, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_mediaLoaderPhotoIconSelected"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -868326258(0xffffffffcc3e648e, float:-4.9910328E7)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_secretTimerBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_secretTimerText"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -12937771(0xffffffffff3a95d5, float:-2.4801438E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "profile_creatorIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -8288630(0xfffffffffvar_a, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "profile_actionIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "profile_actionBackground"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -855310(0xfffffffffff2f2f2, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "profile_actionPressedBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -5056776(0xffffffffffb2d6f8, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "profile_verifiedBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11959368(0xfffffffffvar_b8, float:-2.6785875E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "profile_verifiedCheck"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "profile_title"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -2626822(0xffffffffffd7eafa, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "profile_status"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7893872(0xfffffffffvar_CLASSNAME, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "profile_tabText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -12937771(0xffffffffff3a95d5, float:-2.4801438E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "profile_tabSelectedText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11557143(0xffffffffff4fa6e9, float:-2.7601684E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "profile_tabSelectedLine"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 251658240(0xvar_, float:6.3108872E-30)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "profile_tabSelector"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "player_actionBar"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 251658240(0xvar_, float:6.3108872E-30)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "player_actionBarSelector"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "player_actionBarTitle"
            r4.put(r6, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -1728053248(0xfffffffvar_, float:-6.617445E-24)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "player_actionBarTop"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7697782(0xffffffffff8a8a8a, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "player_actionBarSubtitle"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7697782(0xffffffffff8a8a8a, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "player_actionBarItems"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "player_background"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7564650(0xffffffffff8CLASSNAME, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "player_time"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -1315344(0xffffffffffebedf0, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "player_progressBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -3353637(0xffffffffffccd3db, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "player_progressBackground2"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -3810064(0xffffffffffc5dcf0, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "key_player_progressCachedBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11228437(0xfffffffffvar_aaeb, float:-2.826838E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "player_progress"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -13421773(0xfffffffffvar_, float:-2.3819765E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "player_button"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11753238(0xffffffffff4ca8ea, float:-2.7203956E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "player_buttonActive"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -1973016(0xffffffffffe1e4e8, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "key_sheet_scrollUp"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -3551789(0xffffffffffc9cdd3, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "key_sheet_other"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "files_folderIcon"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -10637333(0xffffffffff5dafeb, float:-2.946728E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "files_folderIconBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "files_iconText"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -6908266(0xfffffffffvar_, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "sessions_devicesImage"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -12211217(0xfffffffffvar_abef, float:-2.6275065E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "passport_authorizeBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -12542501(0xfffffffffvar_ddb, float:-2.560314E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "passport_authorizeBackgroundSelected"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "passport_authorizeText"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -12149258(0xfffffffffvar_df6, float:-2.6400732E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "location_sendLocationBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "location_sendLocationIcon"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -14906664(0xffffffffff1c8ad8, float:-2.0808049E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "location_sendLocationText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11550140(0xffffffffff4fCLASSNAME, float:-2.7615888E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "location_sendLiveLocationBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "location_sendLiveLocationIcon"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -13194460(0xfffffffffvar_ab24, float:-2.428081E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "location_sendLiveLocationText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -13262875(0xfffffffffvar_fe5, float:-2.4142049E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "location_liveLocationProgress"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11753238(0xffffffffff4ca8ea, float:-2.7203956E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "location_placeLocationBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -12959675(0xffffffffff3a4045, float:-2.4757011E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "location_actionIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -12414746(0xfffffffffvar_e6, float:-2.5862259E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "location_actionActiveIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "location_actionBackground"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -855310(0xfffffffffff2f2f2, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "location_actionPressedBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -13262875(0xfffffffffvar_fe5, float:-2.4142049E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "dialog_liveLocationProgress"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -16725933(0xfffffffffvar_CLASSNAME, float:-1.7118133E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "calls_callReceivedGreenIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -47032(0xfffffffffffvar_, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "calls_callReceivedRedIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11491093(0xfffffffffvar_a8eb, float:-2.773565E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "featuredStickers_addedIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "featuredStickers_buttonProgress"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11491093(0xfffffffffvar_a8eb, float:-2.773565E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "featuredStickers_addButton"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -12346402(0xfffffffffvar_bde, float:-2.6000877E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "featuredStickers_addButtonPressed"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11496493(0xfffffffffvar_d3, float:-2.7724697E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "featuredStickers_removeButtonText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "featuredStickers_buttonText"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11688214(0xffffffffff4da6ea, float:-2.733584E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "featuredStickers_unread"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "inappPlayerPerformer"
            r4.put(r6, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "inappPlayerTitle"
            r4.put(r6, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "inappPlayerBackground"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -10309397(0xfffffffffvar_b0eb, float:-3.0132414E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "inappPlayerPlayPause"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7629157(0xffffffffff8b969b, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "inappPlayerClose"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -12279325(0xfffffffffvar_a1e3, float:-2.6136925E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "returnToCallBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -6445135(0xffffffffff9da7b1, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "returnToCallMutedBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "returnToCallText"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -13196562(0xfffffffffvar_a2ee, float:-2.4276547E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "sharedMedia_startStopLoadIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -986123(0xfffffffffff0f3f5, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "sharedMedia_linkPlaceholder"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -4735293(0xffffffffffb7bec3, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "sharedMedia_linkPlaceholderText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -1182729(0xffffffffffedf3f7, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "sharedMedia_photoPlaceholder"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -12154957(0xfffffffffvar_b3, float:-2.6389173E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "sharedMedia_actionMode"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -10567099(0xffffffffff5eCLASSNAME, float:-2.9609732E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "checkbox"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "checkboxCheck"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -5195326(0xffffffffffb0b9c2, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "checkboxDisabled"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -4801083(0xffffffffffb6bdc5, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "stickers_menu"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 251658240(0xvar_, float:6.3108872E-30)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "stickers_menuSelector"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -4669499(0xffffffffffb8bfc5, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "changephoneinfo_image"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11491350(0xfffffffffvar_a7ea, float:-2.7735128E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "changephoneinfo_image2"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "groupcreate_hintText"
            r4.put(r6, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11361317(0xfffffffffvar_a3db, float:-2.7998867E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "groupcreate_cursor"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "groupcreate_sectionShadow"
            r4.put(r6, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -8617336(0xffffffffff7CLASSNAME, float:-3.3564321E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "groupcreate_sectionText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "groupcreate_spanText"
            r4.put(r6, r11)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -855310(0xfffffffffff2f2f2, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "groupcreate_spanBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "groupcreate_spanDelete"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11157919(0xfffffffffvar_be61, float:-2.8411407E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "contacts_inviteBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "contacts_inviteText"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -1971470(0xffffffffffe1eaf2, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "login_progressInner"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -10313520(0xfffffffffvar_a0d0, float:-3.0124051E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "login_progressOuter"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -14043401(0xfffffffffvar_b6f7, float:-2.2558954E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "musicPicker_checkbox"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "musicPicker_checkboxCheck"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -10702870(0xffffffffff5cafea, float:-2.9334356E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "musicPicker_buttonBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "musicPicker_buttonIcon"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -15095832(0xfffffffffvar_a7e8, float:-2.042437E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "picker_enabledButton"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -6710887(0xfffffffffvar_, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "picker_disabledButton"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -14043401(0xfffffffffvar_b6f7, float:-2.2558954E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "picker_badge"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "picker_badgeText"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -12348980(0xfffffffffvar_cc, float:-2.5995648E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_botSwitchToInlineText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -366530760(0xffffffffea272var_, float:-5.05284E25)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "undo_background"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -8008961(0xfffffffffvar_caff, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "undo_cancelColor"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "undo_infoColor"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "wallet_blackBackground"
            r4.put(r6, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -986896(0xfffffffffff0f0f0, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "wallet_graySettingsBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -14079703(0xfffffffffvar_, float:-2.2485325E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "wallet_grayBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "wallet_whiteBackground"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 1090519039(0x40ffffff, float:7.9999995)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "wallet_blackBackgroundSelector"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "wallet_whiteText"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "wallet_blackText"
            r4.put(r6, r11)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -8355712(0xfffffffffvar_, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "wallet_statusText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -8947849(0xfffffffffvar_, float:-3.2893961E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "wallet_grayText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -10066330(0xfffffffffvar_, float:-3.0625412E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "wallet_grayText2"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -13129704(0xfffffffffvar_a818, float:-2.4412152E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "wallet_greenText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -2408384(0xffffffffffdb4040, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "wallet_redText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -6710887(0xfffffffffvar_, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "wallet_dateText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -6710887(0xfffffffffvar_, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "wallet_commentText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -13599557(0xfffffffffvar_cbb, float:-2.3459176E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "wallet_releaseBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -14606047(0xfffffffffvar_, float:-2.1417772E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "wallet_pullBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -12082714(0xfffffffffvar_a1e6, float:-2.65357E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "wallet_buttonBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -13923114(0xffffffffff2b8cd6, float:-2.2802925E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "wallet_buttonPressedBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "wallet_buttonText"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 218103808(0xd000000, float:3.9443045E-31)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "wallet_addressConfirmBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 775919907(0x2e3var_, float:4.3564385E-11)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_outTextSelectionHighlight"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 1348643299(0x5062a9e3, float:1.5211138E10)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_inTextSelectionHighlight"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -12476440(0xfffffffffvar_fe8, float:-2.5737128E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "chat_TextSelectionCursor"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 2133140777(0x7var_, float:2.1951557E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "statisticChartSignature"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 2133140777(0x7var_, float:2.1951557E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "statisticChartSignatureAlpha"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 437792059(0x1a182d3b, float:3.14694E-23)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "statisticChartHintLine"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 855638016(0x33000000, float:2.9802322E-8)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "statisticChartActiveLine"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -1713180935(0xfffffffvar_e2eef9, float:-2.3464373E-23)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "statisticChartInactivePickerChart"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -658846503(0xffffffffd8baccd9, float:-1.64311181E15)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "statisticChartActivePickerChart"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 746495415(0x2c7e9db7, float:3.618312E-12)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "statisticChartRipple"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -15692829(0xfffffffffvar_be3, float:-1.9213516E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "statisticChartBackZoomColor"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -4342339(0xffffffffffbdbdbd, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "statisticChartCheckboxInactive"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7434605(0xffffffffff8e8e93, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "statisticChartNightIconColor"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -2959913(0xffffffffffd2d5d7, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "statisticChartChevronColor"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 552398060(0x20ececec, float:4.0136737E-19)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "statisticChartHighlightColor"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "statisticChartPopupBackground"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -13467675(0xfffffffffvar_fe5, float:-2.3726665E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "statisticChartLine_blue"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -10369198(0xfffffffffvar_CLASSNAME, float:-3.0011123E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "statisticChartLine_green"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -2075818(0xffffffffffe05356, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "statisticChartLine_red"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -2180600(0xffffffffffdeba08, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "statisticChartLine_golden"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -10966803(0xfffffffffvar_a8ed, float:-2.8799036E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "statisticChartLine_lightblue"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7352519(0xffffffffff8fcvar_, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "statisticChartLine_lightgreen"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -1853657(0xffffffffffe3b727, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "statisticChartLine_orange"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -8422925(0xffffffffff7var_f3, float:-3.3958634E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "statisticChartLine_indigo"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -1118482(0xffffffffffeeeeee, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "statisticChartLineEmpty"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -12292204(0xfffffffffvar_var_, float:-2.6110803E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "actionBarTipBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -9718023(0xffffffffff6bb6f9, float:-3.1331863E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_checkMenu"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -8919716(0xfffffffffvar_e55c, float:-3.2951022E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_muteButton"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -8528726(0xffffffffff7ddcaa, float:-3.3744044E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_muteButton2"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11089922(0xfffffffffvar_c7fe, float:-2.854932E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_muteButton3"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "voipgroup_searchText"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -8024684(0xfffffffffvar_d94, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_searchPlaceholder"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -13616313(0xfffffffffvar_b47, float:-2.3425191E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_searchBackground"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -35467(0xfffffffffffvar_, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_leaveCallMenu"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -13023660(0xfffffffffvar_, float:-2.4627234E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_scrollUp"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 2100052301(0x7d2CLASSNAMEd, float:1.4310392E37)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_soundButton"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 2099422443(0x7d22a4eb, float:1.3511952E37)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_soundButtonActive"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -2110540545(0xfffffffvar_b4ff, float:-1.3202786E-37)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_soundButtonActiveScrolled"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 2099796282(0x7d28593a, float:1.398585E37)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_soundButton2"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 2098771793(0x7d18b751, float:1.2687156E37)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_soundButtonActive2"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -2111520954(0xfffffffvar_bvar_, float:-1.210371E-37)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_soundButtonActive2Scrolled"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 2113363036(0x7dvar_c5c, float:4.109986E37)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_leaveButton"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -2100212396(0xfffffffvar_d14d54, float:-3.0754174E-37)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_leaveButtonScrolled"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -14107905(0xfffffffffvar_baff, float:-2.2428124E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_connectingProgress"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -14933463(0xffffffffff1CLASSNAME, float:-2.0753694E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_disabledButton"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -13878715(0xffffffffff2c3a45, float:-2.2892977E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_disabledButtonActive"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -2106088964(0xfffffffvar_a1fc, float:-1.8193181E-37)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_disabledButtonActiveScrolled"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11297032(0xfffffffffvar_ef8, float:-2.8129252E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_unmuteButton"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -10038021(0xfffffffffvar_d4fb, float:-3.068283E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_unmuteButton2"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -15130842(0xfffffffffvar_var_, float:-2.0353362E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_actionBarUnscrolled"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -14538189(0xfffffffffvar_a33, float:-2.1555405E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_listViewBackgroundUnscrolled"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -8024684(0xfffffffffvar_d94, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_lastSeenTextUnscrolled"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -8485236(0xffffffffff7e868c, float:-3.3832252E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_mutedIconUnscrolled"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -15789289(0xffffffffff0var_, float:-1.9017872E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_actionBar"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -15065823(0xffffffffff1a1d21, float:-2.0485236E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_emptyView"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "voipgroup_actionBarItems"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7697782(0xffffffffff8a8a8a, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_actionBarSubtitle"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = 515562495(0x1ebadbff, float:1.9784504E-20)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_actionBarItemsSelector"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -36752(0xfffffffffffvar_, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_mutedByAdminIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -9471616(0xffffffffff6var_, float:-3.1831636E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_mutedIcon"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -8813686(0xfffffffffvar_a, float:-3.3166076E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "voipgroup_lastSeenText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "voipgroup_nameText"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -14933463(0xffffffffff1CLASSNAME, float:-2.0753694E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "voipgroup_listViewBackground"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -14933463(0xffffffffff1CLASSNAME, float:-2.0753694E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "voipgroup_dialogBackground"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -11683585(0xffffffffff4db8ff, float:-2.734523E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "voipgroup_listeningText"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -8917379(0xfffffffffvar_ee7d, float:-3.2955762E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "voipgroup_speakingText"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = 251658239(0xeffffff, float:6.310887E-30)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "voipgroup_listSelector"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -14538189(0xfffffffffvar_a33, float:-2.1555405E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "voipgroup_inviteMembersBackground"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -13906177(0xffffffffff2bceff, float:-2.2837277E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "voipgroup_overlayBlue1"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -16156957(0xfffffffffvar_e3, float:-1.8272153E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "voipgroup_overlayBlue2"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -15551198(0xfffffffffvar_b522, float:-1.9500778E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "voipgroup_overlayGreen1"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -16722239(0xfffffffffvar_d6c1, float:-1.7125625E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "voipgroup_overlayGreen2"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -10434565(0xfffffffffvar_c7fb, float:-2.9878543E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "voipgroup_topPanelBlue1"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -11427847(0xfffffffffvar_ff9, float:-2.7863928E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "voipgroup_topPanelBlue2"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -11350435(0xfffffffffvar_ce5d, float:-2.8020938E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "voipgroup_topPanelGreen1"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -16731712(0xfffffffffvar_b1c0, float:-1.7106411E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "voipgroup_topPanelGreen2"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -8021590(0xfffffffffvar_aa, float:NaN)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "voipgroup_topPanelGray"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -14455406(0xfffffffffvar_d92, float:-2.1723308E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "voipgroup_overlayAlertGradientMuted"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -13873813(0xffffffffff2c4d6b, float:-2.290292E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "voipgroup_overlayAlertGradientMuted2"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -15955316(0xffffffffff0c8a8c, float:-1.868113E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "voipgroup_overlayAlertGradientUnmuted"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -14136203(0xfffffffffvar_CLASSNAME, float:-2.237073E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "voipgroup_overlayAlertGradientUnmuted2"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -11033346(0xfffffffffvar_a4fe, float:-2.866407E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "voipgroup_mutedByAdminGradient"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -1026983(0xffffffffffvar_, float:NaN)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "voipgroup_mutedByAdminGradient2"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -9015575(0xfffffffffvar_ee9, float:-3.2756597E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "voipgroup_mutedByAdminGradient3"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -9998178(0xfffffffffvar_e, float:-3.076364E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "voipgroup_overlayAlertMutedByAdmin"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -13676424(0xffffffffff2var_, float:-2.3303272E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "kvoipgroup_overlayAlertMutedByAdmin2"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = 2138612735(0x7var_a3ff, float:3.3050006E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "voipgroup_mutedByAdminMuteButton"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = 863544319(0x3378a3ff, float:5.7891153E-8)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "voipgroup_mutedByAdminMuteButtonDisabled"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_adminText"
            java.lang.String r6 = "chat_inTimeText"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_adminSelectedText"
            java.lang.String r6 = "chat_inTimeSelectedText"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "key_player_progressCachedBackground"
            java.lang.String r6 = "player_progressBackground"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_inAudioCacheSeekbar"
            java.lang.String r6 = "chat_inAudioSeekbar"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_outAudioCacheSeekbar"
            java.lang.String r6 = "chat_outAudioSeekbar"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_emojiSearchBackground"
            java.lang.String r6 = "chat_emojiPanelStickerPackSelector"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "location_sendLiveLocationIcon"
            java.lang.String r6 = "location_sendLocationIcon"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "changephoneinfo_image2"
            java.lang.String r6 = "featuredStickers_addButton"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "key_graySectionText"
            java.lang.String r6 = "windowBackgroundWhiteGrayText2"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_inMediaIcon"
            java.lang.String r6 = "chat_inBubble"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_outMediaIcon"
            java.lang.String r6 = "chat_outBubble"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_inMediaIconSelected"
            java.lang.String r6 = "chat_inBubbleSelected"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_outMediaIconSelected"
            java.lang.String r6 = "chat_outBubbleSelected"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_actionUnreadIcon"
            java.lang.String r6 = "profile_actionIcon"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_actionUnreadBackground"
            java.lang.String r6 = "profile_actionBackground"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_actionUnreadPressedBackground"
            java.lang.String r6 = "profile_actionPressedBackground"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialog_inlineProgressBackground"
            java.lang.String r6 = "windowBackgroundGray"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialog_inlineProgress"
            java.lang.String r6 = "chats_menuItemIcon"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "groupcreate_spanDelete"
            java.lang.String r6 = "chats_actionIcon"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "sharedMedia_photoPlaceholder"
            java.lang.String r6 = "windowBackgroundGray"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_attachPollBackground"
            java.lang.String r6 = "chat_attachAudioBackground"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_attachPollIcon"
            java.lang.String r6 = "chat_attachAudioIcon"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_onlineCircle"
            java.lang.String r6 = "windowBackgroundWhiteBlueText"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "windowBackgroundWhiteBlueButton"
            java.lang.String r6 = "windowBackgroundWhiteValueText"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "windowBackgroundWhiteBlueIcon"
            java.lang.String r6 = "windowBackgroundWhiteValueText"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "undo_background"
            java.lang.String r6 = "chat_gifSaveHintBackground"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "undo_cancelColor"
            java.lang.String r6 = "chat_gifSaveHintText"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "undo_infoColor"
            java.lang.String r6 = "chat_gifSaveHintText"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "windowBackgroundUnchecked"
            java.lang.String r6 = "windowBackgroundWhite"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "windowBackgroundChecked"
            java.lang.String r6 = "windowBackgroundWhite"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "switchTrackBlue"
            java.lang.String r6 = "switchTrack"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "switchTrackBlueChecked"
            java.lang.String r6 = "switchTrackChecked"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "switchTrackBlueThumb"
            java.lang.String r6 = "windowBackgroundWhite"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "switchTrackBlueThumbChecked"
            java.lang.String r6 = "windowBackgroundWhite"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "windowBackgroundCheckText"
            java.lang.String r6 = "windowBackgroundWhiteBlackText"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "contextProgressInner4"
            java.lang.String r6 = "contextProgressInner1"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "contextProgressOuter4"
            java.lang.String r6 = "contextProgressOuter1"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "switchTrackBlueSelector"
            java.lang.String r6 = "listSelectorSDK21"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "switchTrackBlueSelectorChecked"
            java.lang.String r6 = "listSelectorSDK21"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_emojiBottomPanelIcon"
            java.lang.String r6 = "chat_emojiPanelIcon"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_emojiSearchIcon"
            java.lang.String r6 = "chat_emojiPanelIcon"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_emojiPanelStickerSetNameHighlight"
            java.lang.String r6 = "windowBackgroundWhiteBlueText4"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_emojiPanelStickerPackSelectorLine"
            java.lang.String r6 = "chat_emojiPanelIconSelected"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "sharedMedia_actionMode"
            java.lang.String r6 = "actionBarDefault"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "key_sheet_scrollUp"
            java.lang.String r6 = "chat_emojiPanelStickerPackSelector"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "key_sheet_other"
            java.lang.String r6 = "player_actionBarItems"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialogSearchBackground"
            java.lang.String r6 = "chat_emojiPanelStickerPackSelector"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialogSearchHint"
            java.lang.String r6 = "chat_emojiPanelIcon"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialogSearchIcon"
            java.lang.String r6 = "chat_emojiPanelIcon"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialogSearchText"
            java.lang.String r6 = "windowBackgroundWhiteBlackText"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialogFloatingButton"
            java.lang.String r6 = "dialogRoundCheckBox"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialogFloatingButtonPressed"
            java.lang.String r6 = "dialogRoundCheckBox"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialogFloatingIcon"
            java.lang.String r6 = "dialogRoundCheckBoxCheck"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialogShadowLine"
            java.lang.String r6 = "chat_emojiPanelShadowLine"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "actionBarDefaultArchived"
            java.lang.String r6 = "actionBarDefault"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "actionBarDefaultArchivedSelector"
            java.lang.String r6 = "actionBarDefaultSelector"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "actionBarDefaultArchivedIcon"
            java.lang.String r6 = "actionBarDefaultIcon"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "actionBarDefaultArchivedTitle"
            java.lang.String r6 = "actionBarDefaultTitle"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "actionBarDefaultArchivedSearch"
            java.lang.String r6 = "actionBarDefaultSearch"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "actionBarDefaultSearchArchivedPlaceholder"
            java.lang.String r6 = "actionBarDefaultSearchPlaceholder"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_message_threeLines"
            java.lang.String r6 = "chats_message"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_nameMessage_threeLines"
            java.lang.String r6 = "chats_nameMessage"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_nameArchived"
            java.lang.String r6 = "chats_name"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_nameMessageArchived"
            java.lang.String r6 = "chats_nameMessage"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_nameMessageArchived_threeLines"
            java.lang.String r6 = "chats_nameMessage"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_messageArchived"
            java.lang.String r6 = "chats_message"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "avatar_backgroundArchived"
            java.lang.String r6 = "chats_unreadCounterMuted"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_archiveBackground"
            java.lang.String r6 = "chats_actionBackground"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_archivePinBackground"
            java.lang.String r6 = "chats_unreadCounterMuted"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_archiveIcon"
            java.lang.String r6 = "chats_actionIcon"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_archiveText"
            java.lang.String r6 = "chats_actionIcon"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "actionBarDefaultSubmenuItemIcon"
            java.lang.String r6 = "dialogIcon"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "checkboxDisabled"
            java.lang.String r6 = "chats_unreadCounterMuted"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_status"
            java.lang.String r6 = "actionBarDefaultSubtitle"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_inDownCall"
            java.lang.String r6 = "calls_callReceivedGreenIcon"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_inUpCall"
            java.lang.String r6 = "calls_callReceivedRedIcon"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_outUpCall"
            java.lang.String r6 = "calls_callReceivedGreenIcon"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "actionBarTabActiveText"
            java.lang.String r6 = "actionBarDefaultTitle"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "actionBarTabUnactiveText"
            java.lang.String r6 = "actionBarDefaultSubtitle"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "actionBarTabLine"
            java.lang.String r6 = "actionBarDefaultTitle"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "actionBarTabSelector"
            java.lang.String r6 = "actionBarDefaultSelector"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "profile_status"
            java.lang.String r6 = "avatar_subtitleInProfileBlue"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_menuTopBackgroundCats"
            java.lang.String r6 = "avatar_backgroundActionBarBlue"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_attachPermissionImage"
            java.lang.String r6 = "dialogTextBlack"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_attachPermissionMark"
            java.lang.String r6 = "chat_sentError"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_attachPermissionText"
            java.lang.String r6 = "dialogTextBlack"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_attachEmptyImage"
            java.lang.String r6 = "emptyListPlaceholder"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "actionBarBrowser"
            java.lang.String r6 = "actionBarDefault"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_sentReadCheck"
            java.lang.String r6 = "chats_sentCheck"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_outSentCheckRead"
            java.lang.String r6 = "chat_outSentCheck"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_outSentCheckReadSelected"
            java.lang.String r6 = "chat_outSentCheckSelected"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_archivePullDownBackground"
            java.lang.String r6 = "chats_unreadCounterMuted"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chats_archivePullDownBackgroundActive"
            java.lang.String r6 = "chats_actionBackground"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "avatar_backgroundArchivedHidden"
            java.lang.String r6 = "avatar_backgroundSaved"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "featuredStickers_removeButtonText"
            java.lang.String r6 = "featuredStickers_addButtonPressed"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialogEmptyImage"
            java.lang.String r6 = "player_time"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialogEmptyText"
            java.lang.String r6 = "player_time"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "location_actionIcon"
            java.lang.String r6 = "dialogTextBlack"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "location_actionActiveIcon"
            java.lang.String r6 = "windowBackgroundWhiteBlueText7"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "location_actionBackground"
            java.lang.String r6 = "dialogBackground"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "location_actionPressedBackground"
            java.lang.String r6 = "dialogBackgroundGray"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "location_sendLocationText"
            java.lang.String r6 = "windowBackgroundWhiteBlueText7"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "location_sendLiveLocationText"
            java.lang.String r6 = "windowBackgroundWhiteGreenText"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_outTextSelectionHighlight"
            java.lang.String r6 = "chat_textSelectBackground"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_inTextSelectionHighlight"
            java.lang.String r6 = "chat_textSelectBackground"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_TextSelectionCursor"
            java.lang.String r6 = "chat_messagePanelCursor"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_inPollCorrectAnswer"
            java.lang.String r6 = "chat_attachLocationBackground"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_outPollCorrectAnswer"
            java.lang.String r6 = "chat_attachLocationBackground"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_inPollWrongAnswer"
            java.lang.String r6 = "chat_attachAudioBackground"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_outPollWrongAnswer"
            java.lang.String r6 = "chat_attachAudioBackground"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "profile_tabText"
            java.lang.String r6 = "windowBackgroundWhiteGrayText"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "profile_tabSelectedText"
            java.lang.String r6 = "windowBackgroundWhiteBlueHeader"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "profile_tabSelectedLine"
            java.lang.String r6 = "windowBackgroundWhiteBlueHeader"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "profile_tabSelector"
            java.lang.String r6 = "listSelectorSDK21"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "statisticChartPopupBackground"
            java.lang.String r6 = "dialogBackground"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_attachGalleryText"
            java.lang.String r6 = "chat_attachGalleryBackground"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_attachAudioText"
            java.lang.String r6 = "chat_attachAudioBackground"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_attachFileText"
            java.lang.String r6 = "chat_attachFileBackground"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_attachContactText"
            java.lang.String r6 = "chat_attachContactBackground"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_attachLocationText"
            java.lang.String r6 = "chat_attachLocationBackground"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_attachPollText"
            java.lang.String r6 = "chat_attachPollBackground"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_inPsaNameText"
            java.lang.String r6 = "avatar_nameInMessageGreen"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_outPsaNameText"
            java.lang.String r6 = "avatar_nameInMessageGreen"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_outAdminText"
            java.lang.String r6 = "chat_outTimeText"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_outAdminSelectedText"
            java.lang.String r6 = "chat_outTimeSelectedText"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "returnToCallMutedBackground"
            java.lang.String r6 = "windowBackgroundWhite"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialogSwipeRemove"
            java.lang.String r6 = "avatar_backgroundRed"
            r0.put(r4, r6)
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
            java.lang.String r4 = "chat_attachFileText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "chat_attachGalleryText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "statisticChartLine_blue"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "statisticChartLine_green"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "statisticChartLine_red"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "statisticChartLine_golden"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "statisticChartLine_lightblue"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "statisticChartLine_lightgreen"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "statisticChartLine_orange"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "statisticChartLine_indigo"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_checkMenu"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_muteButton"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_muteButton2"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_muteButton3"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_searchText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_searchPlaceholder"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_searchBackground"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_leaveCallMenu"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_scrollUp"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_blueText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_soundButton"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_soundButtonActive"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_soundButtonActiveScrolled"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_soundButton2"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_soundButtonActive2"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_soundButtonActive2Scrolled"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_leaveButton"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_leaveButtonScrolled"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_connectingProgress"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_disabledButton"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_disabledButtonActive"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_disabledButtonActiveScrolled"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_unmuteButton"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_unmuteButton2"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_actionBarUnscrolled"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_listViewBackgroundUnscrolled"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_lastSeenTextUnscrolled"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_mutedIconUnscrolled"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_actionBar"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_emptyView"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_actionBarItems"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_actionBarSubtitle"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_actionBarItemsSelector"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_mutedByAdminIcon"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_mutedIcon"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_lastSeenText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_nameText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_listViewBackground"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_listeningText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_speakingText"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_listSelector"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_inviteMembersBackground"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_dialogBackground"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_overlayGreen1"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_overlayGreen2"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_overlayBlue1"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_overlayBlue2"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_topPanelGreen1"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_topPanelGreen2"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_topPanelBlue1"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_topPanelBlue2"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_topPanelGray"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_overlayAlertGradientMuted"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_overlayAlertGradientMuted2"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_overlayAlertGradientUnmuted"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_overlayAlertGradientUnmuted2"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_overlayAlertMutedByAdmin"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "kvoipgroup_overlayAlertMutedByAdmin2"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_mutedByAdminGradient"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_mutedByAdminGradient2"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_mutedByAdminGradient3"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_mutedByAdminMuteButton"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_mutedByAdminMuteButtonDisabled"
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
            java.lang.String r6 = "Blue"
            r0.name = r6
            java.lang.String r6 = "bluebubbles.attheme"
            r0.assetName = r6
            r6 = -6963476(0xfffffffffvar_beec, float:NaN)
            int unused = r0.previewBackgroundColor = r6
            int unused = r0.previewInColor = r2
            r6 = -3086593(0xffffffffffd0e6ff, float:NaN)
            int unused = r0.previewOutColor = r6
            r0.firstAccentIsDefault = r5
            int r6 = DEFALT_THEME_ACCENT_ID
            r0.currentAccentId = r6
            r0.sortIndex = r5
            r6 = 16
            int[] r8 = new int[r6]
            r8 = {-10972987, -14444461, -3252606, -8428605, -14380627, -14050257, -7842636, -13464881, -12342073, -11359164, -3317869, -2981834, -8165684, -3256745, -2904512, -8681301} // fill-array
            int[] r9 = new int[r6]
            r9 = {-4660851, -328756, -1572, -4108434, -3031781, -1335, -198952, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r10 = new int[r6]
            r10 = {0, -853047, -264993, 0, 0, -135756, -198730, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r11 = new int[r6]
            r11 = {0, -2104672, -1918575, -2637335, -2305600, -1067658, -4152623, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r12 = new int[r6]
            r12 = {0, -4071005, -1318214, -1520170, -2039866, -1251471, -2175778, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r13 = new int[r6]
            r13 = {99, 9, 10, 11, 12, 13, 14, 0, 1, 2, 3, 4, 5, 6, 7, 8} // fill-array
            java.lang.String[] r14 = new java.lang.String[r6]
            r14[r1] = r3
            java.lang.String r15 = "p-pXcflrmFIBAAAAvXYQk-mCwZU"
            r14[r5] = r15
            java.lang.String r15 = "JqSUrO0-mFIBAAAAWwTvLzoWGQI"
            r14[r7] = r15
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
            int[] r2 = new int[r6]
            r2 = {0, 180, 45, 0, 45, 180, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r15 = new int[r6]
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
            r14[r7] = r15
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
            r14[r6] = r3
            r6 = 17
            r14[r6] = r3
            int[] r6 = new int[r2]
            r6 = {225, 45, 225, 135, 45, 225, 45, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
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
            r29 = r6
            r30 = r2
            r21.setAccentColorOptions(r22, r23, r24, r25, r26, r27, r28, r29, r30)
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r2 = themes
            r2.add(r0)
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r2 = themesDict
            currentNightTheme = r0
            java.lang.String r6 = "Dark Blue"
            r2.put(r6, r0)
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
            int[] r6 = new int[r2]
            r6 = {-12537374, -12472227, -3240928, -11033621, -2194124, -3382903, -13332245, -12342073, -11359164, -3317869, -2981834, -8165684, -3256745, -2904512, -8681301} // fill-array
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
            r13[r7] = r2
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
            r22 = r6
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
            java.lang.String r6 = "Arctic Blue"
            r2.put(r6, r0)
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
            r0.sortIndex = r7
            r2 = 14
            int[] r6 = new int[r2]
            r6 = {-11099447, -3379581, -3109305, -3382174, -7963438, -11759137, -11029287, -11226775, -2506945, -3382174, -3379581, -6587438, -2649788, -8681301} // fill-array
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
            r13[r7] = r3
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
            r22 = r6
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
            java.lang.String r6 = "Day"
            r2.put(r6, r0)
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
            int[] r6 = new int[r2]
            r6 = {-9781697, -7505693, -2204034, -10913816, -2375398, -12678921, -11881005, -11880383, -2534026, -1934037, -7115558, -3128522, -1528292, -8812381} // fill-array
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
            r13[r7] = r2
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
            r22 = r6
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
            java.lang.String r6 = "Night"
            r2.put(r6, r0)
            java.lang.String r0 = "themes2"
            r2 = 0
            java.lang.String r0 = r4.getString(r0, r2)
            r6 = 0
        L_0x3091:
            r8 = 3
            if (r6 >= r8) goto L_0x30d9
            int[] r8 = remoteThemesHash
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "remoteThemesHash"
            r9.append(r10)
            if (r6 == 0) goto L_0x30a7
            java.lang.Integer r10 = java.lang.Integer.valueOf(r6)
            goto L_0x30a8
        L_0x30a7:
            r10 = r3
        L_0x30a8:
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            int r9 = r4.getInt(r9, r1)
            r8[r6] = r9
            int[] r8 = lastLoadingThemesTime
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "lastLoadingThemesTime"
            r9.append(r10)
            if (r6 == 0) goto L_0x30c8
            java.lang.Integer r10 = java.lang.Integer.valueOf(r6)
            goto L_0x30c9
        L_0x30c8:
            r10 = r3
        L_0x30c9:
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            int r9 = r4.getInt(r9, r1)
            r8[r6] = r9
            int r6 = r6 + 1
            goto L_0x3091
        L_0x30d9:
            boolean r6 = android.text.TextUtils.isEmpty(r0)
            if (r6 != 0) goto L_0x3113
            org.json.JSONArray r6 = new org.json.JSONArray     // Catch:{ Exception -> 0x310e }
            r6.<init>(r0)     // Catch:{ Exception -> 0x310e }
            r0 = 0
        L_0x30e5:
            int r8 = r6.length()     // Catch:{ Exception -> 0x310e }
            if (r0 >= r8) goto L_0x3157
            org.json.JSONObject r8 = r6.getJSONObject(r0)     // Catch:{ Exception -> 0x310e }
            org.telegram.ui.ActionBar.Theme$ThemeInfo r8 = org.telegram.ui.ActionBar.Theme.ThemeInfo.createWithJson(r8)     // Catch:{ Exception -> 0x310e }
            if (r8 == 0) goto L_0x310b
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r9 = otherThemes     // Catch:{ Exception -> 0x310e }
            r9.add(r8)     // Catch:{ Exception -> 0x310e }
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r9 = themes     // Catch:{ Exception -> 0x310e }
            r9.add(r8)     // Catch:{ Exception -> 0x310e }
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r9 = themesDict     // Catch:{ Exception -> 0x310e }
            java.lang.String r10 = r8.getKey()     // Catch:{ Exception -> 0x310e }
            r9.put(r10, r8)     // Catch:{ Exception -> 0x310e }
            r8.loadWallpapers(r4)     // Catch:{ Exception -> 0x310e }
        L_0x310b:
            int r0 = r0 + 1
            goto L_0x30e5
        L_0x310e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x3157
        L_0x3113:
            java.lang.String r0 = "themes"
            java.lang.String r0 = r4.getString(r0, r2)
            boolean r6 = android.text.TextUtils.isEmpty(r0)
            if (r6 != 0) goto L_0x3157
            java.lang.String r6 = "&"
            java.lang.String[] r0 = r0.split(r6)
            r6 = 0
        L_0x3126:
            int r8 = r0.length
            if (r6 >= r8) goto L_0x3147
            r8 = r0[r6]
            org.telegram.ui.ActionBar.Theme$ThemeInfo r8 = org.telegram.ui.ActionBar.Theme.ThemeInfo.createWithString(r8)
            if (r8 == 0) goto L_0x3144
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r9 = otherThemes
            r9.add(r8)
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r9 = themes
            r9.add(r8)
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r9 = themesDict
            java.lang.String r10 = r8.getKey()
            r9.put(r10, r8)
        L_0x3144:
            int r6 = r6 + 1
            goto L_0x3126
        L_0x3147:
            saveOtherThemes(r5, r5)
            android.content.SharedPreferences$Editor r0 = r4.edit()
            java.lang.String r6 = "themes"
            android.content.SharedPreferences$Editor r0 = r0.remove(r6)
            r0.commit()
        L_0x3157:
            sortThemes()
            android.content.SharedPreferences r6 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r0 = themesDict     // Catch:{ Exception -> 0x3572 }
            java.lang.String r8 = "Dark Blue"
            java.lang.Object r0 = r0.get(r8)     // Catch:{ Exception -> 0x3572 }
            r8 = r0
            org.telegram.ui.ActionBar.Theme$ThemeInfo r8 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r8     // Catch:{ Exception -> 0x3572 }
            java.lang.String r0 = "theme"
            java.lang.String r0 = r6.getString(r0, r2)     // Catch:{ Exception -> 0x3572 }
            java.lang.String r9 = "Default"
            boolean r9 = r9.equals(r0)     // Catch:{ Exception -> 0x3572 }
            if (r9 == 0) goto L_0x3190
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r0 = themesDict     // Catch:{ Exception -> 0x318b }
            java.lang.String r9 = "Blue"
            java.lang.Object r0 = r0.get(r9)     // Catch:{ Exception -> 0x318b }
            r9 = r0
            org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r9     // Catch:{ Exception -> 0x318b }
            int r0 = DEFALT_THEME_ACCENT_ID     // Catch:{ Exception -> 0x3187 }
            r9.currentAccentId = r0     // Catch:{ Exception -> 0x3187 }
            goto L_0x31c9
        L_0x3187:
            r0 = move-exception
            r1 = r6
        L_0x3189:
            r2 = r9
            goto L_0x318d
        L_0x318b:
            r0 = move-exception
            r1 = r6
        L_0x318d:
            r6 = 1
            goto L_0x3576
        L_0x3190:
            java.lang.String r9 = "Dark"
            boolean r9 = r9.equals(r0)     // Catch:{ Exception -> 0x3572 }
            if (r9 == 0) goto L_0x31a2
            r0 = 9
            r8.currentAccentId = r0     // Catch:{ Exception -> 0x319e }
            r9 = r8
            goto L_0x31c9
        L_0x319e:
            r0 = move-exception
            r1 = r6
            r2 = r8
            goto L_0x318d
        L_0x31a2:
            if (r0 == 0) goto L_0x31c8
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r9 = themesDict     // Catch:{ Exception -> 0x318b }
            java.lang.Object r0 = r9.get(r0)     // Catch:{ Exception -> 0x318b }
            r9 = r0
            org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r9     // Catch:{ Exception -> 0x318b }
            if (r9 == 0) goto L_0x31c9
            java.lang.String r0 = "lastDayTheme"
            boolean r0 = r4.contains(r0)     // Catch:{ Exception -> 0x3187 }
            if (r0 != 0) goto L_0x31c9
            android.content.SharedPreferences$Editor r0 = r4.edit()     // Catch:{ Exception -> 0x3187 }
            java.lang.String r10 = "lastDayTheme"
            java.lang.String r11 = r9.getKey()     // Catch:{ Exception -> 0x3187 }
            r0.putString(r10, r11)     // Catch:{ Exception -> 0x3187 }
            r0.commit()     // Catch:{ Exception -> 0x3187 }
            goto L_0x31c9
        L_0x31c8:
            r9 = r2
        L_0x31c9:
            java.lang.String r0 = "nighttheme"
            java.lang.String r0 = r6.getString(r0, r2)     // Catch:{ Exception -> 0x356d }
            java.lang.String r10 = "Default"
            boolean r10 = r10.equals(r0)     // Catch:{ Exception -> 0x356d }
            if (r10 == 0) goto L_0x31e8
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r0 = themesDict     // Catch:{ Exception -> 0x3187 }
            java.lang.String r8 = "Blue"
            java.lang.Object r0 = r0.get(r8)     // Catch:{ Exception -> 0x3187 }
            r8 = r0
            org.telegram.ui.ActionBar.Theme$ThemeInfo r8 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r8     // Catch:{ Exception -> 0x3187 }
            int r0 = DEFALT_THEME_ACCENT_ID     // Catch:{ Exception -> 0x319e }
            r8.currentAccentId = r0     // Catch:{ Exception -> 0x319e }
            r9 = r8
            goto L_0x3205
        L_0x31e8:
            java.lang.String r10 = "Dark"
            boolean r10 = r10.equals(r0)     // Catch:{ Exception -> 0x356d }
            if (r10 == 0) goto L_0x31f7
            currentNightTheme = r8     // Catch:{ Exception -> 0x3187 }
            r0 = 9
            r8.currentAccentId = r0     // Catch:{ Exception -> 0x3187 }
            goto L_0x3205
        L_0x31f7:
            if (r0 == 0) goto L_0x3205
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r8 = themesDict     // Catch:{ Exception -> 0x3187 }
            java.lang.Object r0 = r8.get(r0)     // Catch:{ Exception -> 0x3187 }
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r0     // Catch:{ Exception -> 0x3187 }
            if (r0 == 0) goto L_0x3205
            currentNightTheme = r0     // Catch:{ Exception -> 0x3187 }
        L_0x3205:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = currentNightTheme     // Catch:{ Exception -> 0x356d }
            if (r0 == 0) goto L_0x3223
            java.lang.String r0 = "lastDarkTheme"
            boolean r0 = r4.contains(r0)     // Catch:{ Exception -> 0x3187 }
            if (r0 != 0) goto L_0x3223
            android.content.SharedPreferences$Editor r0 = r4.edit()     // Catch:{ Exception -> 0x3187 }
            java.lang.String r8 = "lastDarkTheme"
            org.telegram.ui.ActionBar.Theme$ThemeInfo r10 = currentNightTheme     // Catch:{ Exception -> 0x3187 }
            java.lang.String r10 = r10.getKey()     // Catch:{ Exception -> 0x3187 }
            r0.putString(r8, r10)     // Catch:{ Exception -> 0x3187 }
            r0.commit()     // Catch:{ Exception -> 0x3187 }
        L_0x3223:
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r0 = themesDict     // Catch:{ Exception -> 0x356d }
            java.util.Collection r0 = r0.values()     // Catch:{ Exception -> 0x356d }
            java.util.Iterator r8 = r0.iterator()     // Catch:{ Exception -> 0x356d }
            r10 = r2
            r11 = r10
        L_0x322f:
            boolean r0 = r8.hasNext()     // Catch:{ Exception -> 0x356d }
            if (r0 == 0) goto L_0x34ca
            java.lang.Object r0 = r8.next()     // Catch:{ Exception -> 0x356d }
            r12 = r0
            org.telegram.ui.ActionBar.Theme$ThemeInfo r12 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r12     // Catch:{ Exception -> 0x356d }
            java.lang.String r0 = r12.assetName     // Catch:{ Exception -> 0x356d }
            if (r0 == 0) goto L_0x34bb
            int r0 = r12.accentBaseColor     // Catch:{ Exception -> 0x356d }
            if (r0 == 0) goto L_0x34bb
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x356d }
            r0.<init>()     // Catch:{ Exception -> 0x356d }
            java.lang.String r13 = "accents_"
            r0.append(r13)     // Catch:{ Exception -> 0x356d }
            java.lang.String r13 = r12.assetName     // Catch:{ Exception -> 0x356d }
            r0.append(r13)     // Catch:{ Exception -> 0x356d }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x356d }
            java.lang.String r0 = r4.getString(r0, r2)     // Catch:{ Exception -> 0x356d }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x356d }
            r13.<init>()     // Catch:{ Exception -> 0x356d }
            java.lang.String r14 = "accent_current_"
            r13.append(r14)     // Catch:{ Exception -> 0x356d }
            java.lang.String r14 = r12.assetName     // Catch:{ Exception -> 0x356d }
            r13.append(r14)     // Catch:{ Exception -> 0x356d }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x356d }
            boolean r14 = r12.firstAccentIsDefault     // Catch:{ Exception -> 0x356d }
            if (r14 == 0) goto L_0x3275
            int r14 = DEFALT_THEME_ACCENT_ID     // Catch:{ Exception -> 0x3187 }
            goto L_0x3276
        L_0x3275:
            r14 = 0
        L_0x3276:
            int r13 = r4.getInt(r13, r14)     // Catch:{ Exception -> 0x356d }
            r12.currentAccentId = r13     // Catch:{ Exception -> 0x356d }
            java.util.ArrayList r13 = new java.util.ArrayList     // Catch:{ Exception -> 0x356d }
            r13.<init>()     // Catch:{ Exception -> 0x356d }
            boolean r14 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x356d }
            if (r14 != 0) goto L_0x337d
            org.telegram.tgnet.SerializedData r14 = new org.telegram.tgnet.SerializedData     // Catch:{ all -> 0x3368 }
            r15 = 3
            byte[] r0 = android.util.Base64.decode(r0, r15)     // Catch:{ all -> 0x3368 }
            r14.<init>((byte[]) r0)     // Catch:{ all -> 0x3368 }
            int r0 = r14.readInt32(r5)     // Catch:{ all -> 0x3368 }
            int r15 = r14.readInt32(r5)     // Catch:{ all -> 0x3368 }
            r2 = 0
        L_0x329a:
            if (r2 >= r15) goto L_0x3364
            org.telegram.ui.ActionBar.Theme$ThemeAccent r1 = new org.telegram.ui.ActionBar.Theme$ThemeAccent     // Catch:{ all -> 0x3368 }
            r1.<init>()     // Catch:{ all -> 0x3368 }
            int r7 = r14.readInt32(r5)     // Catch:{ all -> 0x3368 }
            r1.id = r7     // Catch:{ all -> 0x3368 }
            int r7 = r14.readInt32(r5)     // Catch:{ all -> 0x3368 }
            r1.accentColor = r7     // Catch:{ all -> 0x3368 }
            r1.parentTheme = r12     // Catch:{ all -> 0x3368 }
            int r7 = r14.readInt32(r5)     // Catch:{ all -> 0x3368 }
            r1.myMessagesAccentColor = r7     // Catch:{ all -> 0x3368 }
            int r7 = r14.readInt32(r5)     // Catch:{ all -> 0x3368 }
            r1.myMessagesGradientAccentColor = r7     // Catch:{ all -> 0x3368 }
            r7 = 3
            if (r0 < r7) goto L_0x32c7
            r23 = r8
            long r7 = r14.readInt64(r5)     // Catch:{ all -> 0x3360 }
            r1.backgroundOverrideColor = r7     // Catch:{ all -> 0x3360 }
            goto L_0x32d0
        L_0x32c7:
            r23 = r8
            int r7 = r14.readInt32(r5)     // Catch:{ all -> 0x3360 }
            long r7 = (long) r7     // Catch:{ all -> 0x3360 }
            r1.backgroundOverrideColor = r7     // Catch:{ all -> 0x3360 }
        L_0x32d0:
            r7 = 2
            if (r0 < r7) goto L_0x32da
            long r7 = r14.readInt64(r5)     // Catch:{ all -> 0x3360 }
            r1.backgroundGradientOverrideColor1 = r7     // Catch:{ all -> 0x3360 }
            goto L_0x32e1
        L_0x32da:
            int r7 = r14.readInt32(r5)     // Catch:{ all -> 0x3360 }
            long r7 = (long) r7     // Catch:{ all -> 0x3360 }
            r1.backgroundGradientOverrideColor1 = r7     // Catch:{ all -> 0x3360 }
        L_0x32e1:
            r7 = 6
            if (r0 < r7) goto L_0x32f0
            long r7 = r14.readInt64(r5)     // Catch:{ all -> 0x3360 }
            r1.backgroundGradientOverrideColor2 = r7     // Catch:{ all -> 0x3360 }
            long r7 = r14.readInt64(r5)     // Catch:{ all -> 0x3360 }
            r1.backgroundGradientOverrideColor3 = r7     // Catch:{ all -> 0x3360 }
        L_0x32f0:
            if (r0 < r5) goto L_0x32f8
            int r7 = r14.readInt32(r5)     // Catch:{ all -> 0x3360 }
            r1.backgroundRotation = r7     // Catch:{ all -> 0x3360 }
        L_0x32f8:
            r7 = 4
            if (r0 < r7) goto L_0x3314
            r14.readInt64(r5)     // Catch:{ all -> 0x3360 }
            double r7 = r14.readDouble(r5)     // Catch:{ all -> 0x3360 }
            float r7 = (float) r7     // Catch:{ all -> 0x3360 }
            r1.patternIntensity = r7     // Catch:{ all -> 0x3360 }
            boolean r7 = r14.readBool(r5)     // Catch:{ all -> 0x3360 }
            r1.patternMotion = r7     // Catch:{ all -> 0x3360 }
            r7 = 5
            if (r0 < r7) goto L_0x3315
            java.lang.String r7 = r14.readString(r5)     // Catch:{ all -> 0x3360 }
            r1.patternSlug = r7     // Catch:{ all -> 0x3360 }
        L_0x3314:
            r7 = 5
        L_0x3315:
            if (r0 < r7) goto L_0x332f
            boolean r8 = r14.readBool(r5)     // Catch:{ all -> 0x3360 }
            if (r8 == 0) goto L_0x332f
            int r8 = r14.readInt32(r5)     // Catch:{ all -> 0x3360 }
            r1.account = r8     // Catch:{ all -> 0x3360 }
            int r8 = r14.readInt32(r5)     // Catch:{ all -> 0x3360 }
            org.telegram.tgnet.TLRPC$Theme r8 = org.telegram.tgnet.TLRPC$Theme.TLdeserialize(r14, r8, r5)     // Catch:{ all -> 0x3360 }
            org.telegram.tgnet.TLRPC$TL_theme r8 = (org.telegram.tgnet.TLRPC$TL_theme) r8     // Catch:{ all -> 0x3360 }
            r1.info = r8     // Catch:{ all -> 0x3360 }
        L_0x332f:
            android.util.SparseArray<org.telegram.ui.ActionBar.Theme$ThemeAccent> r8 = r12.themeAccentsMap     // Catch:{ all -> 0x3360 }
            int r7 = r1.id     // Catch:{ all -> 0x3360 }
            r8.put(r7, r1)     // Catch:{ all -> 0x3360 }
            org.telegram.tgnet.TLRPC$TL_theme r7 = r1.info     // Catch:{ all -> 0x3360 }
            if (r7 == 0) goto L_0x3344
            android.util.LongSparseArray<org.telegram.ui.ActionBar.Theme$ThemeAccent> r8 = r12.accentsByThemeId     // Catch:{ all -> 0x3360 }
            r25 = r6
            long r5 = r7.id     // Catch:{ all -> 0x335e }
            r8.put(r5, r1)     // Catch:{ all -> 0x335e }
            goto L_0x3346
        L_0x3344:
            r25 = r6
        L_0x3346:
            r13.add(r1)     // Catch:{ all -> 0x335e }
            int r5 = r12.lastAccentId     // Catch:{ all -> 0x335e }
            int r1 = r1.id     // Catch:{ all -> 0x335e }
            int r1 = java.lang.Math.max(r5, r1)     // Catch:{ all -> 0x335e }
            r12.lastAccentId = r1     // Catch:{ all -> 0x335e }
            int r2 = r2 + 1
            r8 = r23
            r6 = r25
            r1 = 0
            r5 = 1
            r7 = 2
            goto L_0x329a
        L_0x335e:
            r0 = move-exception
            goto L_0x336d
        L_0x3360:
            r0 = move-exception
            r25 = r6
            goto L_0x336d
        L_0x3364:
            r23 = r8
            r1 = r6
            goto L_0x3372
        L_0x3368:
            r0 = move-exception
            r25 = r6
            r23 = r8
        L_0x336d:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x3377 }
            r1 = r25
        L_0x3372:
            r5 = 6
            r6 = 1
            r7 = 3
            goto L_0x347d
        L_0x3377:
            r0 = move-exception
            r2 = r9
            r1 = r25
            goto L_0x318d
        L_0x337d:
            r25 = r6
            r23 = r8
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x34b6 }
            r0.<init>()     // Catch:{ Exception -> 0x34b6 }
            java.lang.String r1 = "accent_for_"
            r0.append(r1)     // Catch:{ Exception -> 0x34b6 }
            java.lang.String r1 = r12.assetName     // Catch:{ Exception -> 0x34b6 }
            r0.append(r1)     // Catch:{ Exception -> 0x34b6 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x34b6 }
            r1 = r25
            r2 = 0
            int r5 = r1.getInt(r0, r2)     // Catch:{ Exception -> 0x34b3 }
            if (r5 == 0) goto L_0x3372
            if (r10 != 0) goto L_0x33ab
            android.content.SharedPreferences$Editor r10 = r1.edit()     // Catch:{ Exception -> 0x33a8 }
            android.content.SharedPreferences$Editor r11 = r4.edit()     // Catch:{ Exception -> 0x33a8 }
            goto L_0x33ab
        L_0x33a8:
            r0 = move-exception
            goto L_0x3189
        L_0x33ab:
            r10.remove(r0)     // Catch:{ Exception -> 0x34b3 }
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeAccent> r0 = r12.themeAccents     // Catch:{ Exception -> 0x34b3 }
            int r0 = r0.size()     // Catch:{ Exception -> 0x34b3 }
            r2 = 0
        L_0x33b5:
            if (r2 >= r0) goto L_0x33cc
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeAccent> r6 = r12.themeAccents     // Catch:{ Exception -> 0x33a8 }
            java.lang.Object r6 = r6.get(r2)     // Catch:{ Exception -> 0x33a8 }
            org.telegram.ui.ActionBar.Theme$ThemeAccent r6 = (org.telegram.ui.ActionBar.Theme.ThemeAccent) r6     // Catch:{ Exception -> 0x33a8 }
            int r7 = r6.accentColor     // Catch:{ Exception -> 0x33a8 }
            if (r7 != r5) goto L_0x33c9
            int r0 = r6.id     // Catch:{ Exception -> 0x33a8 }
            r12.currentAccentId = r0     // Catch:{ Exception -> 0x33a8 }
            r0 = 1
            goto L_0x33cd
        L_0x33c9:
            int r2 = r2 + 1
            goto L_0x33b5
        L_0x33cc:
            r0 = 0
        L_0x33cd:
            if (r0 != 0) goto L_0x3462
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = new org.telegram.ui.ActionBar.Theme$ThemeAccent     // Catch:{ Exception -> 0x34b3 }
            r0.<init>()     // Catch:{ Exception -> 0x34b3 }
            r2 = 100
            r0.id = r2     // Catch:{ Exception -> 0x34b3 }
            r0.accentColor = r5     // Catch:{ Exception -> 0x34b3 }
            r0.parentTheme = r12     // Catch:{ Exception -> 0x34b3 }
            android.util.SparseArray<org.telegram.ui.ActionBar.Theme$ThemeAccent> r5 = r12.themeAccentsMap     // Catch:{ Exception -> 0x34b3 }
            r5.put(r2, r0)     // Catch:{ Exception -> 0x34b3 }
            r2 = 0
            r13.add(r2, r0)     // Catch:{ Exception -> 0x34b3 }
            r2 = 100
            r12.currentAccentId = r2     // Catch:{ Exception -> 0x34b3 }
            r2 = 101(0x65, float:1.42E-43)
            r12.lastAccentId = r2     // Catch:{ Exception -> 0x34b3 }
            org.telegram.tgnet.SerializedData r2 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x34b3 }
            r5 = 68
            r2.<init>((int) r5)     // Catch:{ Exception -> 0x34b3 }
            r5 = 6
            r2.writeInt32(r5)     // Catch:{ Exception -> 0x34b3 }
            r6 = 1
            r2.writeInt32(r6)     // Catch:{ Exception -> 0x356b }
            int r7 = r0.id     // Catch:{ Exception -> 0x356b }
            r2.writeInt32(r7)     // Catch:{ Exception -> 0x356b }
            int r7 = r0.accentColor     // Catch:{ Exception -> 0x356b }
            r2.writeInt32(r7)     // Catch:{ Exception -> 0x356b }
            int r7 = r0.myMessagesAccentColor     // Catch:{ Exception -> 0x356b }
            r2.writeInt32(r7)     // Catch:{ Exception -> 0x356b }
            int r7 = r0.myMessagesGradientAccentColor     // Catch:{ Exception -> 0x356b }
            r2.writeInt32(r7)     // Catch:{ Exception -> 0x356b }
            long r7 = r0.backgroundOverrideColor     // Catch:{ Exception -> 0x356b }
            r2.writeInt64(r7)     // Catch:{ Exception -> 0x356b }
            long r7 = r0.backgroundGradientOverrideColor1     // Catch:{ Exception -> 0x356b }
            r2.writeInt64(r7)     // Catch:{ Exception -> 0x356b }
            long r7 = r0.backgroundGradientOverrideColor2     // Catch:{ Exception -> 0x356b }
            r2.writeInt64(r7)     // Catch:{ Exception -> 0x356b }
            long r7 = r0.backgroundGradientOverrideColor3     // Catch:{ Exception -> 0x356b }
            r2.writeInt64(r7)     // Catch:{ Exception -> 0x356b }
            int r7 = r0.backgroundRotation     // Catch:{ Exception -> 0x356b }
            r2.writeInt32(r7)     // Catch:{ Exception -> 0x356b }
            r7 = 0
            r2.writeInt64(r7)     // Catch:{ Exception -> 0x356b }
            float r7 = r0.patternIntensity     // Catch:{ Exception -> 0x356b }
            double r7 = (double) r7     // Catch:{ Exception -> 0x356b }
            r2.writeDouble(r7)     // Catch:{ Exception -> 0x356b }
            boolean r7 = r0.patternMotion     // Catch:{ Exception -> 0x356b }
            r2.writeBool(r7)     // Catch:{ Exception -> 0x356b }
            java.lang.String r0 = r0.patternSlug     // Catch:{ Exception -> 0x356b }
            r2.writeString(r0)     // Catch:{ Exception -> 0x356b }
            r7 = 0
            r2.writeBool(r7)     // Catch:{ Exception -> 0x356b }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x356b }
            r0.<init>()     // Catch:{ Exception -> 0x356b }
            java.lang.String r7 = "accents_"
            r0.append(r7)     // Catch:{ Exception -> 0x356b }
            java.lang.String r7 = r12.assetName     // Catch:{ Exception -> 0x356b }
            r0.append(r7)     // Catch:{ Exception -> 0x356b }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x356b }
            byte[] r2 = r2.toByteArray()     // Catch:{ Exception -> 0x356b }
            r7 = 3
            java.lang.String r2 = android.util.Base64.encodeToString(r2, r7)     // Catch:{ Exception -> 0x356b }
            r11.putString(r0, r2)     // Catch:{ Exception -> 0x356b }
            goto L_0x3465
        L_0x3462:
            r5 = 6
            r6 = 1
            r7 = 3
        L_0x3465:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x356b }
            r0.<init>()     // Catch:{ Exception -> 0x356b }
            java.lang.String r2 = "accent_current_"
            r0.append(r2)     // Catch:{ Exception -> 0x356b }
            java.lang.String r2 = r12.assetName     // Catch:{ Exception -> 0x356b }
            r0.append(r2)     // Catch:{ Exception -> 0x356b }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x356b }
            int r2 = r12.currentAccentId     // Catch:{ Exception -> 0x356b }
            r11.putInt(r0, r2)     // Catch:{ Exception -> 0x356b }
        L_0x347d:
            boolean r0 = r13.isEmpty()     // Catch:{ Exception -> 0x356b }
            if (r0 != 0) goto L_0x348e
            org.telegram.ui.ActionBar.-$$Lambda$Theme$dWzkJzIatDBJ3PY2FfBYKfUglpI r0 = org.telegram.ui.ActionBar.$$Lambda$Theme$dWzkJzIatDBJ3PY2FfBYKfUglpI.INSTANCE     // Catch:{ Exception -> 0x356b }
            java.util.Collections.sort(r13, r0)     // Catch:{ Exception -> 0x356b }
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeAccent> r0 = r12.themeAccents     // Catch:{ Exception -> 0x356b }
            r2 = 0
            r0.addAll(r2, r13)     // Catch:{ Exception -> 0x356b }
        L_0x348e:
            android.util.SparseArray<org.telegram.ui.ActionBar.Theme$ThemeAccent> r0 = r12.themeAccentsMap     // Catch:{ Exception -> 0x356b }
            if (r0 == 0) goto L_0x34a4
            int r2 = r12.currentAccentId     // Catch:{ Exception -> 0x356b }
            java.lang.Object r0 = r0.get(r2)     // Catch:{ Exception -> 0x356b }
            if (r0 != 0) goto L_0x34a4
            boolean r0 = r12.firstAccentIsDefault     // Catch:{ Exception -> 0x356b }
            if (r0 == 0) goto L_0x34a1
            int r0 = DEFALT_THEME_ACCENT_ID     // Catch:{ Exception -> 0x356b }
            goto L_0x34a2
        L_0x34a1:
            r0 = 0
        L_0x34a2:
            r12.currentAccentId = r0     // Catch:{ Exception -> 0x356b }
        L_0x34a4:
            r12.loadWallpapers(r4)     // Catch:{ Exception -> 0x356b }
            r2 = 0
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r12.getAccent(r2)     // Catch:{ Exception -> 0x356b }
            if (r0 == 0) goto L_0x34c1
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r0 = r0.overrideWallpaper     // Catch:{ Exception -> 0x356b }
            r12.overrideWallpaper = r0     // Catch:{ Exception -> 0x356b }
            goto L_0x34c1
        L_0x34b3:
            r0 = move-exception
            goto L_0x356f
        L_0x34b6:
            r0 = move-exception
            r1 = r25
            goto L_0x356f
        L_0x34bb:
            r1 = r6
            r23 = r8
            r5 = 6
            r6 = 1
            r7 = 3
        L_0x34c1:
            r6 = r1
            r8 = r23
            r1 = 0
            r2 = 0
            r5 = 1
            r7 = 2
            goto L_0x322f
        L_0x34ca:
            r1 = r6
            r6 = 1
            r7 = 3
            if (r10 == 0) goto L_0x34d5
            r10.commit()     // Catch:{ Exception -> 0x356b }
            r11.commit()     // Catch:{ Exception -> 0x356b }
        L_0x34d5:
            java.lang.String r0 = "selectedAutoNightType"
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x356b }
            r4 = 29
            if (r2 < r4) goto L_0x34df
            r4 = 3
            goto L_0x34e0
        L_0x34df:
            r4 = 0
        L_0x34e0:
            int r0 = r1.getInt(r0, r4)     // Catch:{ Exception -> 0x356b }
            selectedAutoNightType = r0     // Catch:{ Exception -> 0x356b }
            java.lang.String r0 = "autoNightScheduleByLocation"
            r2 = 0
            boolean r0 = r1.getBoolean(r0, r2)     // Catch:{ Exception -> 0x356b }
            autoNightScheduleByLocation = r0     // Catch:{ Exception -> 0x356b }
            java.lang.String r0 = "autoNightBrighnessThreshold"
            r2 = 1048576000(0x3e800000, float:0.25)
            float r0 = r1.getFloat(r0, r2)     // Catch:{ Exception -> 0x356b }
            autoNightBrighnessThreshold = r0     // Catch:{ Exception -> 0x356b }
            java.lang.String r0 = "autoNightDayStartTime"
            r2 = 1320(0x528, float:1.85E-42)
            int r0 = r1.getInt(r0, r2)     // Catch:{ Exception -> 0x356b }
            autoNightDayStartTime = r0     // Catch:{ Exception -> 0x356b }
            java.lang.String r0 = "autoNightDayEndTime"
            r2 = 480(0x1e0, float:6.73E-43)
            int r0 = r1.getInt(r0, r2)     // Catch:{ Exception -> 0x356b }
            autoNightDayEndTime = r0     // Catch:{ Exception -> 0x356b }
            java.lang.String r0 = "autoNightSunsetTime"
            r2 = 1320(0x528, float:1.85E-42)
            int r0 = r1.getInt(r0, r2)     // Catch:{ Exception -> 0x356b }
            autoNightSunsetTime = r0     // Catch:{ Exception -> 0x356b }
            java.lang.String r0 = "autoNightSunriseTime"
            r2 = 480(0x1e0, float:6.73E-43)
            int r0 = r1.getInt(r0, r2)     // Catch:{ Exception -> 0x356b }
            autoNightSunriseTime = r0     // Catch:{ Exception -> 0x356b }
            java.lang.String r0 = "autoNightCityName"
            java.lang.String r0 = r1.getString(r0, r3)     // Catch:{ Exception -> 0x356b }
            autoNightCityName = r0     // Catch:{ Exception -> 0x356b }
            java.lang.String r0 = "autoNightLocationLatitude3"
            r4 = 10000(0x2710, double:4.9407E-320)
            long r4 = r1.getLong(r0, r4)     // Catch:{ Exception -> 0x356b }
            r7 = 10000(0x2710, double:4.9407E-320)
            int r0 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r0 == 0) goto L_0x353e
            double r4 = java.lang.Double.longBitsToDouble(r4)     // Catch:{ Exception -> 0x356b }
            autoNightLocationLatitude = r4     // Catch:{ Exception -> 0x356b }
            goto L_0x3545
        L_0x353e:
            r4 = 4666723172467343360(0x40cNUM, double:10000.0)
            autoNightLocationLatitude = r4     // Catch:{ Exception -> 0x356b }
        L_0x3545:
            java.lang.String r0 = "autoNightLocationLongitude3"
            r4 = 10000(0x2710, double:4.9407E-320)
            long r4 = r1.getLong(r0, r4)     // Catch:{ Exception -> 0x356b }
            r7 = 10000(0x2710, double:4.9407E-320)
            int r0 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r0 == 0) goto L_0x355a
            double r4 = java.lang.Double.longBitsToDouble(r4)     // Catch:{ Exception -> 0x356b }
            autoNightLocationLongitude = r4     // Catch:{ Exception -> 0x356b }
            goto L_0x3561
        L_0x355a:
            r4 = 4666723172467343360(0x40cNUM, double:10000.0)
            autoNightLocationLongitude = r4     // Catch:{ Exception -> 0x356b }
        L_0x3561:
            java.lang.String r0 = "autoNightLastSunCheckDay"
            r2 = -1
            int r0 = r1.getInt(r0, r2)     // Catch:{ Exception -> 0x356b }
            autoNightLastSunCheckDay = r0     // Catch:{ Exception -> 0x356b }
            goto L_0x357a
        L_0x356b:
            r0 = move-exception
            goto L_0x3570
        L_0x356d:
            r0 = move-exception
            r1 = r6
        L_0x356f:
            r6 = 1
        L_0x3570:
            r2 = r9
            goto L_0x3576
        L_0x3572:
            r0 = move-exception
            r1 = r6
            r6 = 1
            r2 = 0
        L_0x3576:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r9 = r2
        L_0x357a:
            if (r9 != 0) goto L_0x357f
            org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = defaultTheme
            goto L_0x3581
        L_0x357f:
            currentDayTheme = r9
        L_0x3581:
            java.lang.String r0 = "overrideThemeWallpaper"
            boolean r0 = r1.contains(r0)
            if (r0 != 0) goto L_0x3591
            java.lang.String r0 = "selectedBackground2"
            boolean r0 = r1.contains(r0)
            if (r0 == 0) goto L_0x364b
        L_0x3591:
            java.lang.String r0 = "overrideThemeWallpaper"
            r2 = 0
            boolean r0 = r1.getBoolean(r0, r2)
            r4 = 1000001(0xvar_, double:4.94066E-318)
            java.lang.String r2 = "selectedBackground2"
            long r4 = r1.getLong(r2, r4)
            r7 = -1
            int r2 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r2 == 0) goto L_0x35b6
            if (r0 == 0) goto L_0x3638
            r7 = -2
            int r0 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r0 == 0) goto L_0x3638
            r7 = 1000001(0xvar_, double:4.94066E-318)
            int r0 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r0 == 0) goto L_0x3638
        L_0x35b6:
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r0 = new org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo
            r0.<init>()
            java.lang.String r2 = "selectedColor"
            r7 = 0
            int r2 = r1.getInt(r2, r7)
            r0.color = r2
            java.lang.String r2 = "selectedBackgroundSlug"
            java.lang.String r2 = r1.getString(r2, r3)
            r0.slug = r2
            r7 = -100
            int r2 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r2 < 0) goto L_0x35e5
            r7 = -1
            int r2 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r2 > 0) goto L_0x35e5
            int r2 = r0.color
            if (r2 == 0) goto L_0x35e5
            java.lang.String r2 = "c"
            r0.slug = r2
            r0.fileName = r3
            r0.originalFileName = r3
            goto L_0x35ed
        L_0x35e5:
            java.lang.String r2 = "wallpaper.jpg"
            r0.fileName = r2
            java.lang.String r2 = "wallpaper_original.jpg"
            r0.originalFileName = r2
        L_0x35ed:
            java.lang.String r2 = "selectedGradientColor"
            r3 = 0
            int r2 = r1.getInt(r2, r3)
            r0.gradientColor1 = r2
            java.lang.String r2 = "selectedGradientColor2"
            int r2 = r1.getInt(r2, r3)
            r0.gradientColor2 = r2
            java.lang.String r2 = "selectedGradientColor3"
            int r2 = r1.getInt(r2, r3)
            r0.gradientColor3 = r2
            r2 = 45
            java.lang.String r4 = "selectedGradientRotation"
            int r2 = r1.getInt(r4, r2)
            r0.rotation = r2
            java.lang.String r2 = "selectedBackgroundBlurred"
            boolean r2 = r1.getBoolean(r2, r3)
            r0.isBlurred = r2
            java.lang.String r2 = "selectedBackgroundMotion"
            boolean r2 = r1.getBoolean(r2, r3)
            r0.isMotion = r2
            r2 = 1056964608(0x3var_, float:0.5)
            java.lang.String r3 = "selectedIntensity"
            float r2 = r1.getFloat(r3, r2)
            r0.intensity = r2
            org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = currentDayTheme
            r2.setOverrideWallpaper(r0)
            int r2 = selectedAutoNightType
            if (r2 == 0) goto L_0x3638
            org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = currentNightTheme
            r2.setOverrideWallpaper(r0)
        L_0x3638:
            android.content.SharedPreferences$Editor r0 = r1.edit()
            java.lang.String r1 = "overrideThemeWallpaper"
            android.content.SharedPreferences$Editor r0 = r0.remove(r1)
            java.lang.String r1 = "selectedBackground2"
            android.content.SharedPreferences$Editor r0 = r0.remove(r1)
            r0.commit()
        L_0x364b:
            int r0 = needSwitchToTheme()
            r1 = 2
            if (r0 != r1) goto L_0x3654
            org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = currentNightTheme
        L_0x3654:
            r2 = 0
            if (r0 != r1) goto L_0x3659
            r5 = 1
            goto L_0x365a
        L_0x3659:
            r5 = 0
        L_0x365a:
            applyTheme(r9, r2, r2, r5)
            org.telegram.ui.ActionBar.-$$Lambda$YBrwKY5coui7kYPFT6vSXwq9GFM r0 = org.telegram.ui.ActionBar.$$Lambda$YBrwKY5coui7kYPFT6vSXwq9GFM.INSTANCE
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            org.telegram.ui.ActionBar.Theme$9 r0 = new org.telegram.ui.ActionBar.Theme$9
            r0.<init>()
            ambientSensorListener = r0
            int[] r0 = new int[r1]
            viewPos = r0
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
                Drawable access$2500 = Theme.getStateDrawable(this, i);
                ColorFilter colorFilter = null;
                if (access$2500 instanceof BitmapDrawable) {
                    colorFilter = ((BitmapDrawable) access$2500).getPaint().getColorFilter();
                } else if (access$2500 instanceof NinePatchDrawable) {
                    colorFilter = ((NinePatchDrawable) access$2500).getPaint().getColorFilter();
                }
                boolean selectDrawable = super.selectDrawable(i);
                if (colorFilter != null) {
                    access$2500.setColorFilter(colorFilter);
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
                Drawable access$2500 = Theme.getStateDrawable(this, i);
                ColorFilter colorFilter = null;
                if (access$2500 instanceof BitmapDrawable) {
                    colorFilter = ((BitmapDrawable) access$2500).getPaint().getColorFilter();
                } else if (access$2500 instanceof NinePatchDrawable) {
                    colorFilter = ((NinePatchDrawable) access$2500).getPaint().getColorFilter();
                }
                boolean selectDrawable = super.selectDrawable(i);
                if (colorFilter != null) {
                    access$2500.setColorFilter(colorFilter);
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
        int i3 = instance.get(11);
        if ((i == 11 && i2 >= 24 && i2 <= 31) || (i == 0 && i2 == 1)) {
            return 0;
        }
        if (i == 1 && i2 == 14) {
            return 1;
        }
        if (i == 9 && i2 >= 30) {
            return 2;
        }
        if (i == 10 && i2 == 1 && i3 < 12) {
            return 2;
        }
        return -1;
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
            r1 = 2131165856(0x7var_a0, float:1.794594E38)
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

    public static ShapeDrawable createCircleDrawable(int i, int i2) {
        OvalShape ovalShape = new OvalShape();
        float f = (float) i;
        ovalShape.resize(f, f);
        ShapeDrawable shapeDrawable = new ShapeDrawable(ovalShape);
        shapeDrawable.setIntrinsicWidth(i);
        shapeDrawable.setIntrinsicHeight(i);
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

    public static Drawable createServiceDrawable(final int i, final View view, final View view2) {
        return new Drawable() {
            private RectF rect = new RectF();

            public int getOpacity() {
                return -2;
            }

            public void setAlpha(int i) {
            }

            public void setColorFilter(ColorFilter colorFilter) {
            }

            public void draw(Canvas canvas) {
                Rect bounds = getBounds();
                this.rect.set((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom);
                Theme.applyServiceShaderMatrixForView(view, view2);
                RectF rectF = this.rect;
                int i = i;
                canvas.drawRoundRect(rectF, (float) i, (float) i, Theme.chat_actionBackgroundPaint);
                if (Theme.hasGradientService()) {
                    RectF rectF2 = this.rect;
                    int i2 = i;
                    canvas.drawRoundRect(rectF2, (float) i2, (float) i2, Theme.chat_actionBackgroundGradientDarkenPaint);
                }
            }
        };
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
        if (z) {
            return getSelectorDrawable(i, "windowBackgroundWhite");
        }
        return createSelectorDrawable(i, 2);
    }

    public static Drawable getSelectorDrawable(int i, String str) {
        if (str == null) {
            return createSelectorDrawable(i, 2);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{i}), new ColorDrawable(getColor(str)), new ColorDrawable(-1));
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, new ColorDrawable(i));
        stateListDrawable.addState(new int[]{16842913}, new ColorDrawable(i));
        stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(getColor(str)));
        return stateListDrawable;
    }

    public static Drawable createSelectorDrawable(int i) {
        return createSelectorDrawable(i, 1, -1);
    }

    public static Drawable createSelectorDrawable(int i, int i2) {
        return createSelectorDrawable(i, i2, -1);
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x004f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.drawable.Drawable createSelectorDrawable(int r11, final int r12, int r13) {
        /*
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 0
            r2 = 1
            r3 = 21
            if (r0 < r3) goto L_0x0063
            r3 = 23
            r4 = -1
            r5 = 5
            r6 = 0
            if (r12 == r2) goto L_0x0011
            if (r12 != r5) goto L_0x0015
        L_0x0011:
            if (r0 < r3) goto L_0x0015
        L_0x0013:
            r7 = r6
            goto L_0x0039
        L_0x0015:
            if (r12 == r2) goto L_0x002f
            r7 = 3
            if (r12 == r7) goto L_0x002f
            r7 = 4
            if (r12 == r7) goto L_0x002f
            if (r12 == r5) goto L_0x002f
            r7 = 6
            if (r12 == r7) goto L_0x002f
            r7 = 7
            if (r12 != r7) goto L_0x0026
            goto L_0x002f
        L_0x0026:
            r7 = 2
            if (r12 != r7) goto L_0x0013
            android.graphics.drawable.ColorDrawable r7 = new android.graphics.drawable.ColorDrawable
            r7.<init>(r4)
            goto L_0x0039
        L_0x002f:
            android.graphics.Paint r7 = maskPaint
            r7.setColor(r4)
            org.telegram.ui.ActionBar.Theme$7 r7 = new org.telegram.ui.ActionBar.Theme$7
            r7.<init>(r12)
        L_0x0039:
            android.content.res.ColorStateList r8 = new android.content.res.ColorStateList
            int[][] r9 = new int[r2][]
            int[] r10 = android.util.StateSet.WILD_CARD
            r9[r1] = r10
            int[] r10 = new int[r2]
            r10[r1] = r11
            r8.<init>(r9, r10)
            android.graphics.drawable.RippleDrawable r11 = new android.graphics.drawable.RippleDrawable
            r11.<init>(r8, r6, r7)
            if (r0 < r3) goto L_0x0062
            if (r12 != r2) goto L_0x005d
            if (r13 > 0) goto L_0x0059
            r12 = 1101004800(0x41a00000, float:20.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
        L_0x0059:
            r11.setRadius(r13)
            goto L_0x0062
        L_0x005d:
            if (r12 != r5) goto L_0x0062
            r11.setRadius(r4)
        L_0x0062:
            return r11
        L_0x0063:
            android.graphics.drawable.StateListDrawable r12 = new android.graphics.drawable.StateListDrawable
            r12.<init>()
            int[] r13 = new int[r2]
            r0 = 16842919(0x10100a7, float:2.3694026E-38)
            r13[r1] = r0
            android.graphics.drawable.ColorDrawable r0 = new android.graphics.drawable.ColorDrawable
            r0.<init>(r11)
            r12.addState(r13, r0)
            int[] r13 = new int[r2]
            r0 = 16842913(0x10100a1, float:2.369401E-38)
            r13[r1] = r0
            android.graphics.drawable.ColorDrawable r0 = new android.graphics.drawable.ColorDrawable
            r0.<init>(r11)
            r12.addState(r13, r0)
            int[] r11 = android.util.StateSet.WILD_CARD
            android.graphics.drawable.ColorDrawable r13 = new android.graphics.drawable.ColorDrawable
            r13.<init>(r1)
            r12.addState(r11, r13)
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.createSelectorDrawable(int, int, int):android.graphics.drawable.Drawable");
    }

    public static Drawable createCircleSelectorDrawable(int i, final int i2, final int i3) {
        if (Build.VERSION.SDK_INT >= 21) {
            maskPaint.setColor(-1);
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{i}), (Drawable) null, new Drawable() {
                public int getOpacity() {
                    return 0;
                }

                public void setAlpha(int i) {
                }

                public void setColorFilter(ColorFilter colorFilter) {
                }

                public void draw(Canvas canvas) {
                    Rect bounds = getBounds();
                    canvas.drawCircle((float) ((bounds.centerX() - i2) + i3), (float) bounds.centerY(), (float) ((Math.max(bounds.width(), bounds.height()) / 2) + i2 + i3), Theme.maskPaint);
                }
            });
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, new ColorDrawable(i));
        stateListDrawable.addState(new int[]{16842913}, new ColorDrawable(i));
        stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(0));
        return stateListDrawable;
    }

    public static class RippleRadMaskDrawable extends Drawable {
        private int bottomRad;
        private Path path = new Path();
        private float[] radii = new float[8];
        private RectF rect = new RectF();
        private int topRad;

        public int getOpacity() {
            return 0;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public RippleRadMaskDrawable(int i, int i2) {
            this.topRad = i;
            this.bottomRad = i2;
        }

        public void draw(Canvas canvas) {
            float[] fArr = this.radii;
            float dp = (float) AndroidUtilities.dp((float) this.topRad);
            fArr[3] = dp;
            fArr[2] = dp;
            fArr[1] = dp;
            fArr[0] = dp;
            float[] fArr2 = this.radii;
            float dp2 = (float) AndroidUtilities.dp((float) this.bottomRad);
            fArr2[7] = dp2;
            fArr2[6] = dp2;
            fArr2[5] = dp2;
            fArr2[4] = dp2;
            this.rect.set(getBounds());
            this.path.addRoundRect(this.rect, this.radii, Path.Direction.CW);
            canvas.drawPath(this.path, Theme.maskPaint);
        }
    }

    public static void setMaskDrawableRad(Drawable drawable, int i, int i2) {
        if (Build.VERSION.SDK_INT >= 21 && (drawable instanceof RippleDrawable)) {
            RippleDrawable rippleDrawable = (RippleDrawable) drawable;
            int numberOfLayers = rippleDrawable.getNumberOfLayers();
            for (int i3 = 0; i3 < numberOfLayers; i3++) {
                if (rippleDrawable.getDrawable(i3) instanceof RippleRadMaskDrawable) {
                    rippleDrawable.setDrawableByLayerId(16908334, new RippleRadMaskDrawable(i, i2));
                    return;
                }
            }
        }
    }

    public static Drawable createRadSelectorDrawable(int i, int i2, int i3) {
        if (Build.VERSION.SDK_INT >= 21) {
            maskPaint.setColor(-1);
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{i}), (Drawable) null, new RippleRadMaskDrawable(i2, i3));
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, new ColorDrawable(i));
        stateListDrawable.addState(new int[]{16842913}, new ColorDrawable(i));
        stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(0));
        return stateListDrawable;
    }

    public static void applyPreviousTheme() {
        ThemeInfo themeInfo;
        ThemeInfo themeInfo2 = previousTheme;
        if (themeInfo2 != null) {
            hasPreviousTheme = false;
            if (isInNigthMode && (themeInfo = currentNightTheme) != null) {
                applyTheme(themeInfo, true, false, true);
            } else if (!isApplyingAccent) {
                applyTheme(themeInfo2, true, false, false);
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
        Collections.sort(themes, $$Lambda$Theme$Ywqj9JQytc_IBGqdVuFsghzB1FQ.INSTANCE);
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

    public static boolean isCustomWallpaperColor() {
        return hasCustomWallpaper() && currentTheme.overrideWallpaper.color != 0;
    }

    public static void resetCustomWallpaper(boolean z) {
        if (z) {
            isApplyingAccent = false;
            reloadWallpaper();
            return;
        }
        currentTheme.setOverrideWallpaper((OverrideWallpaperInfo) null);
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(11:23|(3:24|25|(6:27|(1:31)|32|(1:36)|37|(1:41)))|42|44|45|(1:47)|48|49|(1:51)|52|(1:54)) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:48:0x0128 */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x012e A[Catch:{ all -> 0x0141 }] */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x013c A[Catch:{ all -> 0x0141 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.telegram.ui.ActionBar.Theme.ThemeInfo fillThemeValues(java.io.File r7, java.lang.String r8, org.telegram.tgnet.TLRPC$TL_theme r9) {
        /*
            r0 = 0
            org.telegram.ui.ActionBar.Theme$ThemeInfo r1 = new org.telegram.ui.ActionBar.Theme$ThemeInfo     // Catch:{ Exception -> 0x0149 }
            r1.<init>()     // Catch:{ Exception -> 0x0149 }
            r1.name = r8     // Catch:{ Exception -> 0x0149 }
            r1.info = r9     // Catch:{ Exception -> 0x0149 }
            java.lang.String r7 = r7.getAbsolutePath()     // Catch:{ Exception -> 0x0149 }
            r1.pathToFile = r7     // Catch:{ Exception -> 0x0149 }
            int r7 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ Exception -> 0x0149 }
            r1.account = r7     // Catch:{ Exception -> 0x0149 }
            r7 = 1
            java.lang.String[] r8 = new java.lang.String[r7]     // Catch:{ Exception -> 0x0149 }
            java.io.File r9 = new java.io.File     // Catch:{ Exception -> 0x0149 }
            java.lang.String r2 = r1.pathToFile     // Catch:{ Exception -> 0x0149 }
            r9.<init>(r2)     // Catch:{ Exception -> 0x0149 }
            getThemeFileValues(r9, r0, r8)     // Catch:{ Exception -> 0x0149 }
            r9 = 0
            r2 = r8[r9]     // Catch:{ Exception -> 0x0149 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x0149 }
            if (r2 != 0) goto L_0x0146
            r8 = r8[r9]     // Catch:{ Exception -> 0x0149 }
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x0149 }
            java.io.File r3 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ Exception -> 0x0149 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0149 }
            r4.<init>()     // Catch:{ Exception -> 0x0149 }
            java.lang.String r5 = org.telegram.messenger.Utilities.MD5(r8)     // Catch:{ Exception -> 0x0149 }
            r4.append(r5)     // Catch:{ Exception -> 0x0149 }
            java.lang.String r5 = ".wp"
            r4.append(r5)     // Catch:{ Exception -> 0x0149 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0149 }
            r2.<init>(r3, r4)     // Catch:{ Exception -> 0x0149 }
            java.lang.String r2 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x0149 }
            r1.pathToWallpaper = r2     // Catch:{ Exception -> 0x0149 }
            android.net.Uri r8 = android.net.Uri.parse(r8)     // Catch:{ all -> 0x0141 }
            java.lang.String r2 = "slug"
            java.lang.String r2 = r8.getQueryParameter(r2)     // Catch:{ all -> 0x0141 }
            r1.slug = r2     // Catch:{ all -> 0x0141 }
            java.lang.String r2 = "mode"
            java.lang.String r2 = r8.getQueryParameter(r2)     // Catch:{ all -> 0x0141 }
            if (r2 == 0) goto L_0x0092
            java.lang.String r2 = r2.toLowerCase()     // Catch:{ all -> 0x0141 }
            java.lang.String r3 = " "
            java.lang.String[] r2 = r2.split(r3)     // Catch:{ all -> 0x0141 }
            if (r2 == 0) goto L_0x0092
            int r3 = r2.length     // Catch:{ all -> 0x0141 }
            if (r3 <= 0) goto L_0x0092
        L_0x0073:
            int r3 = r2.length     // Catch:{ all -> 0x0141 }
            if (r9 >= r3) goto L_0x0092
            java.lang.String r3 = "blur"
            r4 = r2[r9]     // Catch:{ all -> 0x0141 }
            boolean r3 = r3.equals(r4)     // Catch:{ all -> 0x0141 }
            if (r3 == 0) goto L_0x0083
            r1.isBlured = r7     // Catch:{ all -> 0x0141 }
            goto L_0x008f
        L_0x0083:
            java.lang.String r3 = "motion"
            r4 = r2[r9]     // Catch:{ all -> 0x0141 }
            boolean r3 = r3.equals(r4)     // Catch:{ all -> 0x0141 }
            if (r3 == 0) goto L_0x008f
            r1.isMotion = r7     // Catch:{ all -> 0x0141 }
        L_0x008f:
            int r9 = r9 + 1
            goto L_0x0073
        L_0x0092:
            java.lang.String r7 = "intensity"
            java.lang.String r7 = r8.getQueryParameter(r7)     // Catch:{ all -> 0x0141 }
            boolean r9 = android.text.TextUtils.isEmpty(r7)     // Catch:{ all -> 0x0141 }
            if (r9 != 0) goto L_0x0148
            java.lang.String r9 = "bg_color"
            java.lang.String r9 = r8.getQueryParameter(r9)     // Catch:{ Exception -> 0x0112 }
            boolean r2 = android.text.TextUtils.isEmpty(r9)     // Catch:{ Exception -> 0x0112 }
            if (r2 != 0) goto L_0x0112
            r2 = 16
            int r3 = java.lang.Integer.parseInt(r9, r2)     // Catch:{ Exception -> 0x0112 }
            r4 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r3 = r3 | r4
            r1.patternBgColor = r3     // Catch:{ Exception -> 0x0112 }
            int r3 = r9.length()     // Catch:{ Exception -> 0x0112 }
            r5 = 13
            if (r3 < r5) goto L_0x00d4
            r3 = 6
            char r3 = r9.charAt(r3)     // Catch:{ Exception -> 0x0112 }
            boolean r3 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r3)     // Catch:{ Exception -> 0x0112 }
            if (r3 == 0) goto L_0x00d4
            r3 = 7
            java.lang.String r3 = r9.substring(r3, r5)     // Catch:{ Exception -> 0x0112 }
            int r3 = java.lang.Integer.parseInt(r3, r2)     // Catch:{ Exception -> 0x0112 }
            r3 = r3 | r4
            r1.patternBgGradientColor1 = r3     // Catch:{ Exception -> 0x0112 }
        L_0x00d4:
            int r3 = r9.length()     // Catch:{ Exception -> 0x0112 }
            r6 = 20
            if (r3 < r6) goto L_0x00f3
            char r3 = r9.charAt(r5)     // Catch:{ Exception -> 0x0112 }
            boolean r3 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r3)     // Catch:{ Exception -> 0x0112 }
            if (r3 == 0) goto L_0x00f3
            r3 = 14
            java.lang.String r3 = r9.substring(r3, r6)     // Catch:{ Exception -> 0x0112 }
            int r3 = java.lang.Integer.parseInt(r3, r2)     // Catch:{ Exception -> 0x0112 }
            r3 = r3 | r4
            r1.patternBgGradientColor2 = r3     // Catch:{ Exception -> 0x0112 }
        L_0x00f3:
            int r3 = r9.length()     // Catch:{ Exception -> 0x0112 }
            r5 = 27
            if (r3 != r5) goto L_0x0112
            char r3 = r9.charAt(r6)     // Catch:{ Exception -> 0x0112 }
            boolean r3 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r3)     // Catch:{ Exception -> 0x0112 }
            if (r3 == 0) goto L_0x0112
            r3 = 21
            java.lang.String r9 = r9.substring(r3)     // Catch:{ Exception -> 0x0112 }
            int r9 = java.lang.Integer.parseInt(r9, r2)     // Catch:{ Exception -> 0x0112 }
            r9 = r9 | r4
            r1.patternBgGradientColor3 = r9     // Catch:{ Exception -> 0x0112 }
        L_0x0112:
            java.lang.String r9 = "rotation"
            java.lang.String r8 = r8.getQueryParameter(r9)     // Catch:{ Exception -> 0x0128 }
            boolean r9 = android.text.TextUtils.isEmpty(r8)     // Catch:{ Exception -> 0x0128 }
            if (r9 != 0) goto L_0x0128
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt(r8)     // Catch:{ Exception -> 0x0128 }
            int r8 = r8.intValue()     // Catch:{ Exception -> 0x0128 }
            r1.patternBgGradientRotation = r8     // Catch:{ Exception -> 0x0128 }
        L_0x0128:
            boolean r8 = android.text.TextUtils.isEmpty(r7)     // Catch:{ all -> 0x0141 }
            if (r8 != 0) goto L_0x0138
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r7)     // Catch:{ all -> 0x0141 }
            int r7 = r7.intValue()     // Catch:{ all -> 0x0141 }
            r1.patternIntensity = r7     // Catch:{ all -> 0x0141 }
        L_0x0138:
            int r7 = r1.patternIntensity     // Catch:{ all -> 0x0141 }
            if (r7 != 0) goto L_0x0148
            r7 = 50
            r1.patternIntensity = r7     // Catch:{ all -> 0x0141 }
            goto L_0x0148
        L_0x0141:
            r7 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)     // Catch:{ Exception -> 0x0149 }
            goto L_0x0148
        L_0x0146:
            themedWallpaperLink = r0     // Catch:{ Exception -> 0x0149 }
        L_0x0148:
            return r1
        L_0x0149:
            r7 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)
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

    /* JADX WARNING: Can't wrap try/catch for region: R(11:87|88|(1:90)|91|92|94|96|(0)|99|102|(0)(0)) */
    /* JADX WARNING: Can't wrap try/catch for region: R(24:31|32|33|(1:37)|38|39|40|41|(3:47|(4:50|(2:52|110)(2:53|(2:55|109)(1:111))|56|48)|108)|57|58|59|60|(6:62|(1:66)|67|(1:71)|72|(1:76))|77|79|80|(1:82)|94|96|(0)|99|102|(1:114)(1:113)) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:38:0x00c3 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:91:0x01bf */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x01f0 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:113:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00ee A[Catch:{ all -> 0x01ac }] */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0127 A[Catch:{ Exception -> 0x0193 }] */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x019f A[Catch:{ Exception -> 0x01aa }] */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x01d1 A[Catch:{ Exception -> 0x01e8 }] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:38:0x00c3=Splitter:B:38:0x00c3, B:91:0x01bf=Splitter:B:91:0x01bf} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void applyTheme(org.telegram.ui.ActionBar.Theme.ThemeInfo r7, boolean r8, boolean r9, boolean r10) {
        /*
            if (r7 != 0) goto L_0x0003
            return
        L_0x0003:
            org.telegram.ui.Components.ThemeEditorView r9 = org.telegram.ui.Components.ThemeEditorView.getInstance()
            if (r9 == 0) goto L_0x000c
            r9.destroy()
        L_0x000c:
            r9 = 0
            java.lang.String r0 = r7.pathToFile     // Catch:{ Exception -> 0x01e8 }
            java.lang.String r1 = "theme"
            r2 = 0
            if (r0 != 0) goto L_0x003a
            java.lang.String r0 = r7.assetName     // Catch:{ Exception -> 0x01e8 }
            if (r0 == 0) goto L_0x0019
            goto L_0x003a
        L_0x0019:
            if (r10 != 0) goto L_0x002b
            if (r8 == 0) goto L_0x002b
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings()     // Catch:{ Exception -> 0x01e8 }
            android.content.SharedPreferences$Editor r0 = r0.edit()     // Catch:{ Exception -> 0x01e8 }
            r0.remove(r1)     // Catch:{ Exception -> 0x01e8 }
            r0.commit()     // Catch:{ Exception -> 0x01e8 }
        L_0x002b:
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = currentColorsNoAccent     // Catch:{ Exception -> 0x01e8 }
            r0.clear()     // Catch:{ Exception -> 0x01e8 }
            themedWallpaperFileOffset = r9     // Catch:{ Exception -> 0x01e8 }
            themedWallpaperLink = r2     // Catch:{ Exception -> 0x01e8 }
            wallpaper = r2     // Catch:{ Exception -> 0x01e8 }
            themedWallpaper = r2     // Catch:{ Exception -> 0x01e8 }
            goto L_0x01c3
        L_0x003a:
            if (r10 != 0) goto L_0x0050
            if (r8 == 0) goto L_0x0050
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings()     // Catch:{ Exception -> 0x01e8 }
            android.content.SharedPreferences$Editor r0 = r0.edit()     // Catch:{ Exception -> 0x01e8 }
            java.lang.String r3 = r7.getKey()     // Catch:{ Exception -> 0x01e8 }
            r0.putString(r1, r3)     // Catch:{ Exception -> 0x01e8 }
            r0.commit()     // Catch:{ Exception -> 0x01e8 }
        L_0x0050:
            r0 = 1
            java.lang.String[] r1 = new java.lang.String[r0]     // Catch:{ Exception -> 0x01e8 }
            java.lang.String r3 = r7.assetName     // Catch:{ Exception -> 0x01e8 }
            if (r3 == 0) goto L_0x005e
            java.util.HashMap r3 = getThemeFileValues(r2, r3, r2)     // Catch:{ Exception -> 0x01e8 }
            currentColorsNoAccent = r3     // Catch:{ Exception -> 0x01e8 }
            goto L_0x006b
        L_0x005e:
            java.io.File r3 = new java.io.File     // Catch:{ Exception -> 0x01e8 }
            java.lang.String r4 = r7.pathToFile     // Catch:{ Exception -> 0x01e8 }
            r3.<init>(r4)     // Catch:{ Exception -> 0x01e8 }
            java.util.HashMap r3 = getThemeFileValues(r3, r2, r1)     // Catch:{ Exception -> 0x01e8 }
            currentColorsNoAccent = r3     // Catch:{ Exception -> 0x01e8 }
        L_0x006b:
            java.util.HashMap<java.lang.String, java.lang.Integer> r3 = currentColorsNoAccent     // Catch:{ Exception -> 0x01e8 }
            java.lang.String r4 = "wallpaperFileOffset"
            java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x01e8 }
            java.lang.Integer r3 = (java.lang.Integer) r3     // Catch:{ Exception -> 0x01e8 }
            if (r3 == 0) goto L_0x007c
            int r3 = r3.intValue()     // Catch:{ Exception -> 0x01e8 }
            goto L_0x007d
        L_0x007c:
            r3 = -1
        L_0x007d:
            themedWallpaperFileOffset = r3     // Catch:{ Exception -> 0x01e8 }
            r3 = r1[r9]     // Catch:{ Exception -> 0x01e8 }
            boolean r3 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x01e8 }
            if (r3 != 0) goto L_0x01b1
            r1 = r1[r9]     // Catch:{ Exception -> 0x01e8 }
            themedWallpaperLink = r1     // Catch:{ Exception -> 0x01e8 }
            java.io.File r1 = new java.io.File     // Catch:{ Exception -> 0x01e8 }
            java.io.File r2 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ Exception -> 0x01e8 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01e8 }
            r3.<init>()     // Catch:{ Exception -> 0x01e8 }
            java.lang.String r4 = themedWallpaperLink     // Catch:{ Exception -> 0x01e8 }
            java.lang.String r4 = org.telegram.messenger.Utilities.MD5(r4)     // Catch:{ Exception -> 0x01e8 }
            r3.append(r4)     // Catch:{ Exception -> 0x01e8 }
            java.lang.String r4 = ".wp"
            r3.append(r4)     // Catch:{ Exception -> 0x01e8 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x01e8 }
            r1.<init>(r2, r3)     // Catch:{ Exception -> 0x01e8 }
            java.lang.String r1 = r1.getAbsolutePath()     // Catch:{ Exception -> 0x01e8 }
            java.lang.String r2 = r7.pathToWallpaper     // Catch:{ Exception -> 0x00c3 }
            if (r2 == 0) goto L_0x00c3
            boolean r2 = r2.equals(r1)     // Catch:{ Exception -> 0x00c3 }
            if (r2 != 0) goto L_0x00c3
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x00c3 }
            java.lang.String r3 = r7.pathToWallpaper     // Catch:{ Exception -> 0x00c3 }
            r2.<init>(r3)     // Catch:{ Exception -> 0x00c3 }
            r2.delete()     // Catch:{ Exception -> 0x00c3 }
        L_0x00c3:
            r7.pathToWallpaper = r1     // Catch:{ Exception -> 0x01e8 }
            java.lang.String r1 = themedWallpaperLink     // Catch:{ all -> 0x01ac }
            android.net.Uri r1 = android.net.Uri.parse(r1)     // Catch:{ all -> 0x01ac }
            java.lang.String r2 = "slug"
            java.lang.String r2 = r1.getQueryParameter(r2)     // Catch:{ all -> 0x01ac }
            r7.slug = r2     // Catch:{ all -> 0x01ac }
            java.lang.String r2 = "mode"
            java.lang.String r2 = r1.getQueryParameter(r2)     // Catch:{ all -> 0x01ac }
            if (r2 == 0) goto L_0x010a
            java.lang.String r2 = r2.toLowerCase()     // Catch:{ all -> 0x01ac }
            java.lang.String r3 = " "
            java.lang.String[] r2 = r2.split(r3)     // Catch:{ all -> 0x01ac }
            if (r2 == 0) goto L_0x010a
            int r3 = r2.length     // Catch:{ all -> 0x01ac }
            if (r3 <= 0) goto L_0x010a
            r3 = 0
        L_0x00eb:
            int r4 = r2.length     // Catch:{ all -> 0x01ac }
            if (r3 >= r4) goto L_0x010a
            java.lang.String r4 = "blur"
            r5 = r2[r3]     // Catch:{ all -> 0x01ac }
            boolean r4 = r4.equals(r5)     // Catch:{ all -> 0x01ac }
            if (r4 == 0) goto L_0x00fb
            r7.isBlured = r0     // Catch:{ all -> 0x01ac }
            goto L_0x0107
        L_0x00fb:
            java.lang.String r4 = "motion"
            r5 = r2[r3]     // Catch:{ all -> 0x01ac }
            boolean r4 = r4.equals(r5)     // Catch:{ all -> 0x01ac }
            if (r4 == 0) goto L_0x0107
            r7.isMotion = r0     // Catch:{ all -> 0x01ac }
        L_0x0107:
            int r3 = r3 + 1
            goto L_0x00eb
        L_0x010a:
            java.lang.String r0 = "intensity"
            java.lang.String r0 = r1.getQueryParameter(r0)     // Catch:{ all -> 0x01ac }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ all -> 0x01ac }
            r0.intValue()     // Catch:{ all -> 0x01ac }
            r0 = 45
            r7.patternBgGradientRotation = r0     // Catch:{ all -> 0x01ac }
            java.lang.String r0 = "bg_color"
            java.lang.String r0 = r1.getQueryParameter(r0)     // Catch:{ Exception -> 0x0193 }
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0193 }
            if (r2 != 0) goto L_0x0193
            r2 = 6
            java.lang.String r3 = r0.substring(r9, r2)     // Catch:{ Exception -> 0x0193 }
            r4 = 16
            int r3 = java.lang.Integer.parseInt(r3, r4)     // Catch:{ Exception -> 0x0193 }
            r5 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r3 = r3 | r5
            r7.patternBgColor = r3     // Catch:{ Exception -> 0x0193 }
            int r3 = r0.length()     // Catch:{ Exception -> 0x0193 }
            r6 = 13
            if (r3 < r6) goto L_0x0155
            char r2 = r0.charAt(r2)     // Catch:{ Exception -> 0x0193 }
            boolean r2 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r2)     // Catch:{ Exception -> 0x0193 }
            if (r2 == 0) goto L_0x0155
            r2 = 7
            java.lang.String r2 = r0.substring(r2, r6)     // Catch:{ Exception -> 0x0193 }
            int r2 = java.lang.Integer.parseInt(r2, r4)     // Catch:{ Exception -> 0x0193 }
            r2 = r2 | r5
            r7.patternBgGradientColor1 = r2     // Catch:{ Exception -> 0x0193 }
        L_0x0155:
            int r2 = r0.length()     // Catch:{ Exception -> 0x0193 }
            r3 = 20
            if (r2 < r3) goto L_0x0174
            char r2 = r0.charAt(r6)     // Catch:{ Exception -> 0x0193 }
            boolean r2 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r2)     // Catch:{ Exception -> 0x0193 }
            if (r2 == 0) goto L_0x0174
            r2 = 14
            java.lang.String r2 = r0.substring(r2, r3)     // Catch:{ Exception -> 0x0193 }
            int r2 = java.lang.Integer.parseInt(r2, r4)     // Catch:{ Exception -> 0x0193 }
            r2 = r2 | r5
            r7.patternBgGradientColor2 = r2     // Catch:{ Exception -> 0x0193 }
        L_0x0174:
            int r2 = r0.length()     // Catch:{ Exception -> 0x0193 }
            r6 = 27
            if (r2 != r6) goto L_0x0193
            char r2 = r0.charAt(r3)     // Catch:{ Exception -> 0x0193 }
            boolean r2 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r2)     // Catch:{ Exception -> 0x0193 }
            if (r2 == 0) goto L_0x0193
            r2 = 21
            java.lang.String r0 = r0.substring(r2)     // Catch:{ Exception -> 0x0193 }
            int r0 = java.lang.Integer.parseInt(r0, r4)     // Catch:{ Exception -> 0x0193 }
            r0 = r0 | r5
            r7.patternBgGradientColor3 = r0     // Catch:{ Exception -> 0x0193 }
        L_0x0193:
            java.lang.String r0 = "rotation"
            java.lang.String r0 = r1.getQueryParameter(r0)     // Catch:{ Exception -> 0x01aa }
            boolean r1 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x01aa }
            if (r1 != 0) goto L_0x01c3
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x01aa }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x01aa }
            r7.patternBgGradientRotation = r0     // Catch:{ Exception -> 0x01aa }
            goto L_0x01c3
        L_0x01aa:
            goto L_0x01c3
        L_0x01ac:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x01e8 }
            goto L_0x01c3
        L_0x01b1:
            java.lang.String r0 = r7.pathToWallpaper     // Catch:{ Exception -> 0x01bf }
            if (r0 == 0) goto L_0x01bf
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x01bf }
            java.lang.String r1 = r7.pathToWallpaper     // Catch:{ Exception -> 0x01bf }
            r0.<init>(r1)     // Catch:{ Exception -> 0x01bf }
            r0.delete()     // Catch:{ Exception -> 0x01bf }
        L_0x01bf:
            r7.pathToWallpaper = r2     // Catch:{ Exception -> 0x01e8 }
            themedWallpaperLink = r2     // Catch:{ Exception -> 0x01e8 }
        L_0x01c3:
            if (r10 != 0) goto L_0x01e2
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = previousTheme     // Catch:{ Exception -> 0x01e8 }
            if (r0 != 0) goto L_0x01e2
            currentDayTheme = r7     // Catch:{ Exception -> 0x01e8 }
            boolean r0 = isCurrentThemeNight()     // Catch:{ Exception -> 0x01e8 }
            if (r0 == 0) goto L_0x01e2
            r0 = 2000(0x7d0, float:2.803E-42)
            switchNightThemeDelay = r0     // Catch:{ Exception -> 0x01e8 }
            long r0 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x01e8 }
            lastDelayUpdateTime = r0     // Catch:{ Exception -> 0x01e8 }
            org.telegram.ui.ActionBar.-$$Lambda$YBrwKY5coui7kYPFT6vSXwq9GFM r0 = org.telegram.ui.ActionBar.$$Lambda$YBrwKY5coui7kYPFT6vSXwq9GFM.INSTANCE     // Catch:{ Exception -> 0x01e8 }
            r1 = 2100(0x834, double:1.0375E-320)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r1)     // Catch:{ Exception -> 0x01e8 }
        L_0x01e2:
            currentTheme = r7     // Catch:{ Exception -> 0x01e8 }
            refreshThemeColors()     // Catch:{ Exception -> 0x01e8 }
            goto L_0x01ec
        L_0x01e8:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01ec:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = previousTheme
            if (r0 != 0) goto L_0x0203
            if (r8 == 0) goto L_0x0203
            boolean r8 = switchingNightTheme
            if (r8 != 0) goto L_0x0203
            int r8 = r7.account
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r7.getAccent(r9)
            r8.saveTheme(r7, r0, r10, r9)
        L_0x0203:
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
        AndroidUtilities.runOnUIThread($$Lambda$Theme$lrkQF6bgOeO7YNCFIsow0lQQno.INSTANCE);
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
                serializedData.writeInt32(6);
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
                        serializedData.writeInt64(themeAccent.backgroundGradientOverrideColor1);
                        serializedData.writeInt64(themeAccent.backgroundGradientOverrideColor2);
                        serializedData.writeInt64(themeAccent.backgroundGradientOverrideColor3);
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

    public static void setChangingWallpaper(boolean z) {
        changingWallpaper = z;
        if (!z) {
            checkAutoNightThemeConditions(false);
        }
    }

    public static void checkAutoNightThemeConditions(boolean z) {
        if (previousTheme == null && !changingWallpaper) {
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

    public static void applyDayNightThemeMaybe(boolean z) {
        if (previousTheme == null) {
            if (z) {
                if (currentTheme != currentNightTheme) {
                    isInNigthMode = true;
                    lastThemeSwitchTime = SystemClock.elapsedRealtime();
                    switchingNightTheme = true;
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, currentNightTheme, Boolean.TRUE, null, -1);
                    switchingNightTheme = false;
                }
            } else if (currentTheme != currentDayTheme) {
                isInNigthMode = false;
                lastThemeSwitchTime = SystemClock.elapsedRealtime();
                switchingNightTheme = true;
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, currentDayTheme, Boolean.TRUE, null, -1);
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
        String str2;
        String str3;
        String str4 = null;
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
            int i2 = overrideWallpaperInfo.gradientColor1;
            if (i2 != 0) {
                str2 = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (i2 >> 16)) & 255), Integer.valueOf(((byte) (overrideWallpaperInfo.gradientColor1 >> 8)) & 255), Byte.valueOf((byte) (overrideWallpaperInfo.gradientColor1 & 255))}).toLowerCase();
            } else {
                str2 = null;
            }
            int i3 = overrideWallpaperInfo.gradientColor2;
            if (i3 != 0) {
                str3 = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (i3 >> 16)) & 255), Integer.valueOf(((byte) (overrideWallpaperInfo.gradientColor2 >> 8)) & 255), Byte.valueOf((byte) (overrideWallpaperInfo.gradientColor2 & 255))}).toLowerCase();
            } else {
                str3 = null;
            }
            int i4 = overrideWallpaperInfo.gradientColor3;
            if (i4 != 0) {
                str4 = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (i4 >> 16)) & 255), Integer.valueOf(((byte) (overrideWallpaperInfo.gradientColor3 >> 8)) & 255), Byte.valueOf((byte) (overrideWallpaperInfo.gradientColor3 & 255))}).toLowerCase();
            }
            if (str2 == null || str3 == null) {
                if (str2 != null) {
                    lowerCase = (lowerCase + "-" + str2) + "&rotation=" + overrideWallpaperInfo.rotation;
                }
            } else if (str4 != null) {
                lowerCase = lowerCase + "~" + str2 + "~" + str3 + "~" + str4;
            } else {
                lowerCase = lowerCase + "~" + str2 + "~" + str3;
            }
            str = "https://attheme.org?slug=" + overrideWallpaperInfo.slug + "&intensity=" + ((int) (overrideWallpaperInfo.intensity * 100.0f)) + "&bg_color=" + lowerCase;
        }
        if (sb.length() <= 0) {
            return str;
        }
        return str + "&mode=" + sb.toString();
    }

    /* JADX WARNING: Removed duplicated region for block: B:112:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x01de A[SYNTHETIC, Splitter:B:92:0x01de] */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x01e8  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01f8 A[SYNTHETIC, Splitter:B:99:0x01f8] */
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
            if (r6 == 0) goto L_0x00b3
            java.lang.Object r6 = r2.next()
            java.util.Map$Entry r6 = (java.util.Map.Entry) r6
            java.lang.Object r8 = r6.getKey()
            java.lang.String r8 = (java.lang.String) r8
            boolean r9 = r1 instanceof android.graphics.drawable.BitmapDrawable
            if (r9 != 0) goto L_0x007f
            if (r0 == 0) goto L_0x00a0
        L_0x007f:
            java.lang.String r9 = "chat_wallpaper"
            boolean r9 = r9.equals(r8)
            if (r9 != 0) goto L_0x0065
            java.lang.String r9 = "chat_wallpaper_gradient_to"
            boolean r9 = r9.equals(r8)
            if (r9 != 0) goto L_0x0065
            java.lang.String r9 = "key_chat_wallpaper_gradient_to2"
            boolean r9 = r9.equals(r8)
            if (r9 != 0) goto L_0x0065
            java.lang.String r9 = "key_chat_wallpaper_gradient_to3"
            boolean r9 = r9.equals(r8)
            if (r9 == 0) goto L_0x00a0
            goto L_0x0065
        L_0x00a0:
            r5.append(r8)
            java.lang.String r8 = "="
            r5.append(r8)
            java.lang.Object r6 = r6.getValue()
            r5.append(r6)
            r5.append(r7)
            goto L_0x0065
        L_0x00b3:
            r2 = 0
            java.io.FileOutputStream r6 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x01d8 }
            java.lang.String r8 = r12.pathToFile     // Catch:{ Exception -> 0x01d8 }
            r6.<init>(r8)     // Catch:{ Exception -> 0x01d8 }
            int r2 = r5.length()     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            if (r2 != 0) goto L_0x00d0
            boolean r2 = r1 instanceof android.graphics.drawable.BitmapDrawable     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            if (r2 != 0) goto L_0x00d0
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            if (r2 == 0) goto L_0x00d0
            r2 = 32
            r5.append(r2)     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
        L_0x00d0:
            java.lang.String r2 = r5.toString()     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            byte[] r2 = org.telegram.messenger.AndroidUtilities.getStringBytes(r2)     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r6.write(r2)     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r5 = 1
            r8 = 87
            if (r2 != 0) goto L_0x0138
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r2.<init>()     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            java.lang.String r9 = "WLS="
            r2.append(r9)     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r2.append(r0)     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r2.append(r7)     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            byte[] r2 = org.telegram.messenger.AndroidUtilities.getStringBytes(r2)     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r6.write(r2)     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            if (r14 == 0) goto L_0x017b
            android.graphics.drawable.BitmapDrawable r1 = (android.graphics.drawable.BitmapDrawable) r1     // Catch:{ all -> 0x0133 }
            android.graphics.Bitmap r14 = r1.getBitmap()     // Catch:{ all -> 0x0133 }
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch:{ all -> 0x0133 }
            java.io.File r2 = new java.io.File     // Catch:{ all -> 0x0133 }
            java.io.File r7 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ all -> 0x0133 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x0133 }
            r9.<init>()     // Catch:{ all -> 0x0133 }
            java.lang.String r0 = org.telegram.messenger.Utilities.MD5(r0)     // Catch:{ all -> 0x0133 }
            r9.append(r0)     // Catch:{ all -> 0x0133 }
            java.lang.String r0 = ".wp"
            r9.append(r0)     // Catch:{ all -> 0x0133 }
            java.lang.String r0 = r9.toString()     // Catch:{ all -> 0x0133 }
            r2.<init>(r7, r0)     // Catch:{ all -> 0x0133 }
            r1.<init>(r2)     // Catch:{ all -> 0x0133 }
            android.graphics.Bitmap$CompressFormat r0 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ all -> 0x0133 }
            r14.compress(r0, r8, r1)     // Catch:{ all -> 0x0133 }
            r1.close()     // Catch:{ all -> 0x0133 }
            goto L_0x017b
        L_0x0133:
            r14 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r14)     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            goto L_0x017b
        L_0x0138:
            boolean r14 = r1 instanceof android.graphics.drawable.BitmapDrawable     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            if (r14 == 0) goto L_0x017b
            r14 = r1
            android.graphics.drawable.BitmapDrawable r14 = (android.graphics.drawable.BitmapDrawable) r14     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            android.graphics.Bitmap r14 = r14.getBitmap()     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r0 = 2
            if (r14 == 0) goto L_0x0172
            r2 = 4
            byte[] r7 = new byte[r2]     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r7[r3] = r8     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r9 = 80
            r7[r5] = r9     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r10 = 83
            r7[r0] = r10     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r10 = 3
            r11 = 10
            r7[r10] = r11     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r6.write(r7)     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            android.graphics.Bitmap$CompressFormat r7 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r14.compress(r7, r8, r6)     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r14 = 5
            byte[] r14 = new byte[r14]     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r14[r3] = r11     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r14[r5] = r8     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r14[r0] = r9     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r7 = 69
            r14[r10] = r7     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r14[r2] = r11     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r6.write(r14)     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
        L_0x0172:
            if (r13 == 0) goto L_0x017b
            if (r15 != 0) goto L_0x017b
            wallpaper = r1     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            calcBackgroundColor(r1, r0)     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
        L_0x017b:
            if (r15 != 0) goto L_0x01cc
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r14 = themesDict     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            java.lang.String r15 = r12.getKey()     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            java.lang.Object r14 = r14.get(r15)     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            if (r14 != 0) goto L_0x01a2
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r14 = themes     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r14.add(r12)     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r14 = themesDict     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            java.lang.String r15 = r12.getKey()     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r14.put(r15, r12)     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r14 = otherThemes     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r14.add(r12)     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            saveOtherThemes(r5)     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            sortThemes()     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
        L_0x01a2:
            currentTheme = r12     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            org.telegram.ui.ActionBar.Theme$ThemeInfo r14 = currentNightTheme     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            if (r12 == r14) goto L_0x01aa
            currentDayTheme = r12     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
        L_0x01aa:
            java.util.HashMap<java.lang.String, java.lang.Integer> r14 = defaultColors     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            if (r4 != r14) goto L_0x01b6
            java.util.HashMap<java.lang.String, java.lang.Integer> r14 = currentColorsNoAccent     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r14.clear()     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            refreshThemeColors()     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
        L_0x01b6:
            android.content.SharedPreferences r14 = org.telegram.messenger.MessagesController.getGlobalMainSettings()     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            android.content.SharedPreferences$Editor r14 = r14.edit()     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            java.lang.String r15 = "theme"
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = currentDayTheme     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            java.lang.String r0 = r0.getKey()     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r14.putString(r15, r0)     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
            r14.commit()     // Catch:{ Exception -> 0x01d3, all -> 0x01d0 }
        L_0x01cc:
            r6.close()     // Catch:{ Exception -> 0x01e2 }
            goto L_0x01e6
        L_0x01d0:
            r12 = move-exception
            r2 = r6
            goto L_0x01f6
        L_0x01d3:
            r14 = move-exception
            r2 = r6
            goto L_0x01d9
        L_0x01d6:
            r12 = move-exception
            goto L_0x01f6
        L_0x01d8:
            r14 = move-exception
        L_0x01d9:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r14)     // Catch:{ all -> 0x01d6 }
            if (r2 == 0) goto L_0x01e6
            r2.close()     // Catch:{ Exception -> 0x01e2 }
            goto L_0x01e6
        L_0x01e2:
            r14 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r14)
        L_0x01e6:
            if (r13 == 0) goto L_0x01f5
            int r13 = r12.account
            org.telegram.messenger.MessagesController r13 = org.telegram.messenger.MessagesController.getInstance(r13)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r14 = r12.getAccent(r3)
            r13.saveThemeToServer(r12, r14)
        L_0x01f5:
            return
        L_0x01f6:
            if (r2 == 0) goto L_0x0200
            r2.close()     // Catch:{ Exception -> 0x01fc }
            goto L_0x0200
        L_0x01fc:
            r13 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)
        L_0x0200:
            goto L_0x0202
        L_0x0201:
            throw r12
        L_0x0202:
            goto L_0x0201
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
                            public final /* synthetic */ Theme.ThemeInfo f$1;
                            public final /* synthetic */ TLRPC$TL_theme f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                AndroidUtilities.runOnUIThread(new Runnable(Theme.ThemeAccent.this, this.f$1, this.f$2) {
                                    public final /* synthetic */ Theme.ThemeAccent f$1;
                                    public final /* synthetic */ Theme.ThemeInfo f$2;
                                    public final /* synthetic */ TLRPC$TL_theme f$3;

                                    {
                                        this.f$1 = r2;
                                        this.f$2 = r3;
                                        this.f$3 = r4;
                                    }

                                    public final void run() {
                                        Theme.lambda$checkCurrentRemoteTheme$3(TLObject.this, this.f$1, this.f$2, this.f$3);
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
    static /* synthetic */ void lambda$checkCurrentRemoteTheme$3(org.telegram.tgnet.TLObject r7, org.telegram.ui.ActionBar.Theme.ThemeAccent r8, org.telegram.ui.ActionBar.Theme.ThemeInfo r9, org.telegram.tgnet.TLRPC$TL_theme r10) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.lambda$checkCurrentRemoteTheme$3(org.telegram.tgnet.TLObject, org.telegram.ui.ActionBar.Theme$ThemeAccent, org.telegram.ui.ActionBar.Theme$ThemeInfo, org.telegram.tgnet.TLRPC$TL_theme):void");
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
                public final /* synthetic */ int f$0;

                {
                    this.f$0 = r1;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable(this.f$0, tLObject) {
                        public final /* synthetic */ int f$0;
                        public final /* synthetic */ TLObject f$1;

                        {
                            this.f$0 = r1;
                            this.f$1 = r2;
                        }

                        public final void run() {
                            Theme.lambda$loadRemoteThemes$5(this.f$0, this.f$1);
                        }
                    });
                }
            });
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:84:0x01db  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$loadRemoteThemes$5(int r16, org.telegram.tgnet.TLObject r17) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.lambda$loadRemoteThemes$5(int, org.telegram.tgnet.TLObject):void");
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

    /* JADX WARNING: Missing exception handler attribute for start block: B:22:0x004e */
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
            if (r3 == 0) goto L_0x0053
            long r1 = r0.length()
            int r3 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r3 == 0) goto L_0x0053
        L_0x0035:
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x004f }
            android.content.res.AssetManager r1 = r1.getAssets()     // Catch:{ Exception -> 0x004f }
            java.io.InputStream r6 = r1.open(r6)     // Catch:{ Exception -> 0x004f }
            org.telegram.messenger.AndroidUtilities.copyFile((java.io.InputStream) r6, (java.io.File) r0)     // Catch:{ all -> 0x0048 }
            if (r6 == 0) goto L_0x0053
            r6.close()     // Catch:{ Exception -> 0x004f }
            goto L_0x0053
        L_0x0048:
            r1 = move-exception
            if (r6 == 0) goto L_0x004e
            r6.close()     // Catch:{ all -> 0x004e }
        L_0x004e:
            throw r1     // Catch:{ Exception -> 0x004f }
        L_0x004f:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
        L_0x0053:
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v5, resolved type: org.telegram.ui.Components.MotionBackgroundDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v63, resolved type: org.telegram.ui.Components.MotionBackgroundDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v64, resolved type: org.telegram.ui.Components.MotionBackgroundDrawable} */
    /* JADX WARNING: type inference failed for: r0v43 */
    /* JADX WARNING: type inference failed for: r0v44, types: [android.graphics.drawable.ColorDrawable] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x02d7 A[Catch:{ all -> 0x041c, all -> 0x05e9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x03fc A[SYNTHETIC, Splitter:B:155:0x03fc] */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x0410 A[SYNTHETIC, Splitter:B:167:0x0410] */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x042f A[Catch:{ all -> 0x041c, all -> 0x05e9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x0465 A[Catch:{ all -> 0x041c, all -> 0x05e9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x048d A[Catch:{ all -> 0x041c, all -> 0x05e9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x04ac A[Catch:{ all -> 0x041c, all -> 0x05e9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x051f A[Catch:{ all -> 0x041c, all -> 0x05e9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x055e A[Catch:{ all -> 0x041c, all -> 0x05e9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x0581 A[Catch:{ all -> 0x041c, all -> 0x05e9 }] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:90:0x0271=Splitter:B:90:0x0271, B:171:0x0416=Splitter:B:171:0x0416} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String createThemePreviewImage(java.lang.String r36, java.lang.String r37, org.telegram.ui.ActionBar.Theme.ThemeAccent r38) {
        /*
            r0 = r36
            r1 = r37
            r2 = r38
            r3 = 0
            r4 = 1
            java.lang.String[] r5 = new java.lang.String[r4]     // Catch:{ all -> 0x05e9 }
            java.io.File r6 = new java.io.File     // Catch:{ all -> 0x05e9 }
            r6.<init>(r0)     // Catch:{ all -> 0x05e9 }
            java.util.HashMap r6 = getThemeFileValues(r6, r3, r5)     // Catch:{ all -> 0x05e9 }
            java.lang.String r7 = "wallpaperFileOffset"
            java.lang.Object r7 = r6.get(r7)     // Catch:{ all -> 0x05e9 }
            java.lang.Integer r7 = (java.lang.Integer) r7     // Catch:{ all -> 0x05e9 }
            r8 = 560(0x230, float:7.85E-43)
            r9 = 678(0x2a6, float:9.5E-43)
            android.graphics.Bitmap$Config r10 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x05e9 }
            android.graphics.Bitmap r8 = org.telegram.messenger.Bitmaps.createBitmap(r8, r9, r10)     // Catch:{ all -> 0x05e9 }
            android.graphics.Canvas r15 = new android.graphics.Canvas     // Catch:{ all -> 0x05e9 }
            r15.<init>(r8)     // Catch:{ all -> 0x05e9 }
            android.graphics.Paint r14 = new android.graphics.Paint     // Catch:{ all -> 0x05e9 }
            r14.<init>()     // Catch:{ all -> 0x05e9 }
            java.lang.String r9 = "actionBarDefault"
            int r9 = getPreviewColor(r6, r9)     // Catch:{ all -> 0x05e9 }
            java.lang.String r10 = "actionBarDefaultIcon"
            int r10 = getPreviewColor(r6, r10)     // Catch:{ all -> 0x05e9 }
            java.lang.String r11 = "chat_messagePanelBackground"
            int r13 = getPreviewColor(r6, r11)     // Catch:{ all -> 0x05e9 }
            java.lang.String r11 = "chat_messagePanelIcons"
            int r11 = getPreviewColor(r6, r11)     // Catch:{ all -> 0x05e9 }
            java.lang.String r12 = "chat_inBubble"
            int r12 = getPreviewColor(r6, r12)     // Catch:{ all -> 0x05e9 }
            java.lang.String r3 = "chat_outBubble"
            int r3 = getPreviewColor(r6, r3)     // Catch:{ all -> 0x05e9 }
            java.lang.String r4 = "chat_outBubbleGradient"
            java.lang.Object r4 = r6.get(r4)     // Catch:{ all -> 0x05e9 }
            java.lang.Integer r4 = (java.lang.Integer) r4     // Catch:{ all -> 0x05e9 }
            java.lang.String r4 = "chat_wallpaper"
            java.lang.Object r4 = r6.get(r4)     // Catch:{ all -> 0x05e9 }
            java.lang.Integer r4 = (java.lang.Integer) r4     // Catch:{ all -> 0x05e9 }
            r17 = r3
            java.lang.String r3 = "chat_serviceBackground"
            java.lang.Object r3 = r6.get(r3)     // Catch:{ all -> 0x05e9 }
            java.lang.Integer r3 = (java.lang.Integer) r3     // Catch:{ all -> 0x05e9 }
            r18 = r12
            java.lang.String r12 = "chat_wallpaper_gradient_to"
            java.lang.Object r12 = r6.get(r12)     // Catch:{ all -> 0x05e9 }
            java.lang.Integer r12 = (java.lang.Integer) r12     // Catch:{ all -> 0x05e9 }
            r19 = r13
            java.lang.String r13 = "key_chat_wallpaper_gradient_to2"
            java.lang.Object r13 = r6.get(r13)     // Catch:{ all -> 0x05e9 }
            java.lang.Integer r13 = (java.lang.Integer) r13     // Catch:{ all -> 0x05e9 }
            r20 = r9
            java.lang.String r9 = "key_chat_wallpaper_gradient_to3"
            java.lang.Object r9 = r6.get(r9)     // Catch:{ all -> 0x05e9 }
            java.lang.Integer r9 = (java.lang.Integer) r9     // Catch:{ all -> 0x05e9 }
            r21 = r14
            if (r4 == 0) goto L_0x0096
            int r4 = r4.intValue()     // Catch:{ all -> 0x05e9 }
            r22 = r15
            goto L_0x0099
        L_0x0096:
            r22 = r15
            r4 = 0
        L_0x0099:
            long r14 = r2.backgroundOverrideColor     // Catch:{ all -> 0x05e9 }
            r23 = r4
            int r4 = (int) r14     // Catch:{ all -> 0x05e9 }
            r24 = 0
            if (r4 != 0) goto L_0x00a8
            int r26 = (r14 > r24 ? 1 : (r14 == r24 ? 0 : -1))
            if (r26 == 0) goto L_0x00a8
            r4 = 0
            goto L_0x00ad
        L_0x00a8:
            if (r4 == 0) goto L_0x00ab
            goto L_0x00ad
        L_0x00ab:
            r4 = r23
        L_0x00ad:
            if (r12 == 0) goto L_0x00b4
            int r12 = r12.intValue()     // Catch:{ all -> 0x05e9 }
            goto L_0x00b5
        L_0x00b4:
            r12 = 0
        L_0x00b5:
            long r14 = r2.backgroundGradientOverrideColor1     // Catch:{ all -> 0x05e9 }
            r23 = r12
            int r12 = (int) r14     // Catch:{ all -> 0x05e9 }
            if (r12 != 0) goto L_0x00c3
            int r26 = (r14 > r24 ? 1 : (r14 == r24 ? 0 : -1))
            if (r26 == 0) goto L_0x00c3
            r28 = 0
            goto L_0x00c9
        L_0x00c3:
            if (r12 == 0) goto L_0x00c7
            r23 = r12
        L_0x00c7:
            r28 = r23
        L_0x00c9:
            if (r13 == 0) goto L_0x00d0
            int r12 = r13.intValue()     // Catch:{ all -> 0x05e9 }
            goto L_0x00d1
        L_0x00d0:
            r12 = 0
        L_0x00d1:
            long r14 = r2.backgroundGradientOverrideColor2     // Catch:{ all -> 0x05e9 }
            r23 = r12
            int r12 = (int) r14     // Catch:{ all -> 0x05e9 }
            if (r12 != 0) goto L_0x00df
            int r26 = (r14 > r24 ? 1 : (r14 == r24 ? 0 : -1))
            if (r26 == 0) goto L_0x00df
            r29 = 0
            goto L_0x00e5
        L_0x00df:
            if (r12 == 0) goto L_0x00e3
            r23 = r12
        L_0x00e3:
            r29 = r23
        L_0x00e5:
            if (r9 == 0) goto L_0x00ec
            int r9 = r9.intValue()     // Catch:{ all -> 0x05e9 }
            goto L_0x00ed
        L_0x00ec:
            r9 = 0
        L_0x00ed:
            long r14 = r2.backgroundGradientOverrideColor3     // Catch:{ all -> 0x05e9 }
            int r12 = (int) r14     // Catch:{ all -> 0x05e9 }
            if (r12 != 0) goto L_0x00f9
            int r23 = (r14 > r24 ? 1 : (r14 == r24 ? 0 : -1))
            if (r23 == 0) goto L_0x00f9
            r30 = 0
            goto L_0x00fe
        L_0x00f9:
            if (r12 == 0) goto L_0x00fc
            r9 = r12
        L_0x00fc:
            r30 = r9
        L_0x00fe:
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x05e9 }
            android.content.res.Resources r9 = r9.getResources()     // Catch:{ all -> 0x05e9 }
            r12 = 2131165960(0x7var_, float:1.7946152E38)
            android.graphics.drawable.Drawable r9 = r9.getDrawable(r12)     // Catch:{ all -> 0x05e9 }
            android.graphics.drawable.Drawable r15 = r9.mutate()     // Catch:{ all -> 0x05e9 }
            setDrawableColor(r15, r10)     // Catch:{ all -> 0x05e9 }
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x05e9 }
            android.content.res.Resources r9 = r9.getResources()     // Catch:{ all -> 0x05e9 }
            r12 = 2131165962(0x7var_a, float:1.7946156E38)
            android.graphics.drawable.Drawable r9 = r9.getDrawable(r12)     // Catch:{ all -> 0x05e9 }
            android.graphics.drawable.Drawable r14 = r9.mutate()     // Catch:{ all -> 0x05e9 }
            setDrawableColor(r14, r10)     // Catch:{ all -> 0x05e9 }
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x05e9 }
            android.content.res.Resources r9 = r9.getResources()     // Catch:{ all -> 0x05e9 }
            r10 = 2131165965(0x7var_d, float:1.7946162E38)
            android.graphics.drawable.Drawable r9 = r9.getDrawable(r10)     // Catch:{ all -> 0x05e9 }
            android.graphics.drawable.Drawable r12 = r9.mutate()     // Catch:{ all -> 0x05e9 }
            setDrawableColor(r12, r11)     // Catch:{ all -> 0x05e9 }
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x05e9 }
            android.content.res.Resources r9 = r9.getResources()     // Catch:{ all -> 0x05e9 }
            r10 = 2131165963(0x7var_b, float:1.7946158E38)
            android.graphics.drawable.Drawable r9 = r9.getDrawable(r10)     // Catch:{ all -> 0x05e9 }
            android.graphics.drawable.Drawable r10 = r9.mutate()     // Catch:{ all -> 0x05e9 }
            setDrawableColor(r10, r11)     // Catch:{ all -> 0x05e9 }
            r11 = 2
            org.telegram.ui.ActionBar.Theme$MessageDrawable[] r9 = new org.telegram.ui.ActionBar.Theme.MessageDrawable[r11]     // Catch:{ all -> 0x05e9 }
            r23 = r10
            r10 = 0
        L_0x0154:
            if (r10 >= r11) goto L_0x0181
            org.telegram.ui.ActionBar.Theme$10 r11 = new org.telegram.ui.ActionBar.Theme$10     // Catch:{ all -> 0x05e9 }
            r25 = r12
            r12 = 1
            r32 = r14
            r33 = r15
            if (r10 != r12) goto L_0x0163
            r12 = 1
            goto L_0x0164
        L_0x0163:
            r12 = 0
        L_0x0164:
            r14 = 2
            r15 = 0
            r11.<init>(r14, r12, r15, r6)     // Catch:{ all -> 0x05e9 }
            r9[r10] = r11     // Catch:{ all -> 0x05e9 }
            r11 = r9[r10]     // Catch:{ all -> 0x05e9 }
            if (r10 != 0) goto L_0x0172
            r12 = r18
            goto L_0x0174
        L_0x0172:
            r12 = r17
        L_0x0174:
            setDrawableColor(r11, r12)     // Catch:{ all -> 0x05e9 }
            int r10 = r10 + 1
            r12 = r25
            r14 = r32
            r15 = r33
            r11 = 2
            goto L_0x0154
        L_0x0181:
            r25 = r12
            r32 = r14
            r33 = r15
            android.graphics.RectF r15 = new android.graphics.RectF     // Catch:{ all -> 0x05e9 }
            r15.<init>()     // Catch:{ all -> 0x05e9 }
            r11 = 1065353216(0x3var_, float:1.0)
            r12 = 1073741824(0x40000000, float:2.0)
            r14 = 0
            r17 = 1141637120(0x440CLASSNAME, float:560.0)
            r10 = 120(0x78, float:1.68E-43)
            if (r1 == 0) goto L_0x0277
            android.graphics.BitmapFactory$Options r0 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x026e }
            r0.<init>()     // Catch:{ all -> 0x026e }
            r5 = 1
            r0.inJustDecodeBounds = r5     // Catch:{ all -> 0x026e }
            android.graphics.BitmapFactory.decodeFile(r1, r0)     // Catch:{ all -> 0x026e }
            int r5 = r0.outWidth     // Catch:{ all -> 0x026e }
            if (r5 <= 0) goto L_0x0265
            int r6 = r0.outHeight     // Catch:{ all -> 0x026e }
            if (r6 <= 0) goto L_0x0265
            float r5 = (float) r5     // Catch:{ all -> 0x026e }
            float r5 = r5 / r17
            float r6 = (float) r6     // Catch:{ all -> 0x026e }
            float r6 = r6 / r17
            float r5 = java.lang.Math.min(r5, r6)     // Catch:{ all -> 0x026e }
            r6 = 1
            r0.inSampleSize = r6     // Catch:{ all -> 0x026e }
            int r6 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1))
            if (r6 <= 0) goto L_0x01c7
        L_0x01bb:
            int r6 = r0.inSampleSize     // Catch:{ all -> 0x026e }
            r7 = 2
            int r6 = r6 * 2
            r0.inSampleSize = r6     // Catch:{ all -> 0x026e }
            float r6 = (float) r6     // Catch:{ all -> 0x026e }
            int r6 = (r6 > r5 ? 1 : (r6 == r5 ? 0 : -1))
            if (r6 < 0) goto L_0x01bb
        L_0x01c7:
            r5 = 0
            r0.inJustDecodeBounds = r5     // Catch:{ all -> 0x026e }
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r1, r0)     // Catch:{ all -> 0x026e }
            if (r0 == 0) goto L_0x0265
            if (r29 == 0) goto L_0x0205
            org.telegram.ui.Components.MotionBackgroundDrawable r1 = new org.telegram.ui.Components.MotionBackgroundDrawable     // Catch:{ all -> 0x026e }
            r31 = 1
            r26 = r1
            r27 = r4
            r26.<init>(r27, r28, r29, r30, r31)     // Catch:{ all -> 0x026e }
            float r2 = r2.patternIntensity     // Catch:{ all -> 0x026e }
            r4 = 1120403456(0x42CLASSNAME, float:100.0)
            float r2 = r2 * r4
            int r2 = (int) r2     // Catch:{ all -> 0x026e }
            r1.setPatternBitmap(r2, r0)     // Catch:{ all -> 0x026e }
            int r0 = r8.getWidth()     // Catch:{ all -> 0x026e }
            int r2 = r8.getHeight()     // Catch:{ all -> 0x026e }
            r4 = 0
            r1.setBounds(r4, r4, r0, r2)     // Catch:{ all -> 0x026e }
            r2 = r22
            r1.draw(r2)     // Catch:{ all -> 0x0263 }
            if (r3 != 0) goto L_0x0261
            int[] r0 = org.telegram.messenger.AndroidUtilities.calcDrawableColor(r1)     // Catch:{ all -> 0x0263 }
            r0 = r0[r4]     // Catch:{ all -> 0x0263 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x0263 }
            goto L_0x0261
        L_0x0205:
            r2 = r22
            android.graphics.Paint r1 = new android.graphics.Paint     // Catch:{ all -> 0x0263 }
            r1.<init>()     // Catch:{ all -> 0x0263 }
            r4 = 1
            r1.setFilterBitmap(r4)     // Catch:{ all -> 0x0263 }
            int r4 = r0.getWidth()     // Catch:{ all -> 0x0263 }
            float r4 = (float) r4     // Catch:{ all -> 0x0263 }
            float r4 = r4 / r17
            int r5 = r0.getHeight()     // Catch:{ all -> 0x0263 }
            float r5 = (float) r5     // Catch:{ all -> 0x0263 }
            float r5 = r5 / r17
            float r4 = java.lang.Math.min(r4, r5)     // Catch:{ all -> 0x0263 }
            int r5 = r0.getWidth()     // Catch:{ all -> 0x0263 }
            float r5 = (float) r5     // Catch:{ all -> 0x0263 }
            float r5 = r5 / r4
            int r6 = r0.getHeight()     // Catch:{ all -> 0x0263 }
            float r6 = (float) r6     // Catch:{ all -> 0x0263 }
            float r6 = r6 / r4
            r15.set(r14, r14, r5, r6)     // Catch:{ all -> 0x0263 }
            int r4 = r8.getWidth()     // Catch:{ all -> 0x0263 }
            float r4 = (float) r4     // Catch:{ all -> 0x0263 }
            float r5 = r15.width()     // Catch:{ all -> 0x0263 }
            float r4 = r4 - r5
            float r4 = r4 / r12
            int r5 = r8.getHeight()     // Catch:{ all -> 0x0263 }
            float r5 = (float) r5     // Catch:{ all -> 0x0263 }
            float r6 = r15.height()     // Catch:{ all -> 0x0263 }
            float r5 = r5 - r6
            float r5 = r5 / r12
            r15.offset(r4, r5)     // Catch:{ all -> 0x0263 }
            r4 = 0
            r2.drawBitmap(r0, r4, r15, r1)     // Catch:{ all -> 0x0263 }
            if (r3 != 0) goto L_0x0261
            android.graphics.drawable.BitmapDrawable r1 = new android.graphics.drawable.BitmapDrawable     // Catch:{ all -> 0x0263 }
            r1.<init>(r0)     // Catch:{ all -> 0x0263 }
            int[] r0 = org.telegram.messenger.AndroidUtilities.calcDrawableColor(r1)     // Catch:{ all -> 0x0263 }
            r1 = 0
            r0 = r0[r1]     // Catch:{ all -> 0x0263 }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x0263 }
            r3 = r0
        L_0x0261:
            r0 = 1
            goto L_0x0268
        L_0x0263:
            r0 = move-exception
            goto L_0x0271
        L_0x0265:
            r2 = r22
            r0 = 0
        L_0x0268:
            r1 = r0
            r0 = 80
        L_0x026b:
            r11 = 2
            goto L_0x042d
        L_0x026e:
            r0 = move-exception
            r2 = r22
        L_0x0271:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x05e9 }
        L_0x0274:
            r11 = 2
            goto L_0x042a
        L_0x0277:
            r2 = r22
            if (r4 == 0) goto L_0x02e6
            if (r28 != 0) goto L_0x0285
            android.graphics.drawable.ColorDrawable r0 = new android.graphics.drawable.ColorDrawable     // Catch:{ all -> 0x05e9 }
            r0.<init>(r4)     // Catch:{ all -> 0x05e9 }
        L_0x0282:
            r18 = 80
            goto L_0x02c5
        L_0x0285:
            if (r29 == 0) goto L_0x0293
            org.telegram.ui.Components.MotionBackgroundDrawable r0 = new org.telegram.ui.Components.MotionBackgroundDrawable     // Catch:{ all -> 0x05e9 }
            r31 = 1
            r26 = r0
            r27 = r4
            r26.<init>(r27, r28, r29, r30, r31)     // Catch:{ all -> 0x05e9 }
            goto L_0x0282
        L_0x0293:
            java.lang.String r0 = "chat_wallpaper_gradient_rotation"
            java.lang.Object r0 = r6.get(r0)     // Catch:{ all -> 0x05e9 }
            java.lang.Integer r0 = (java.lang.Integer) r0     // Catch:{ all -> 0x05e9 }
            if (r0 != 0) goto L_0x02a3
            r0 = 45
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x05e9 }
        L_0x02a3:
            r1 = 2
            int[] r5 = new int[r1]     // Catch:{ all -> 0x05e9 }
            r1 = 0
            r5[r1] = r4     // Catch:{ all -> 0x05e9 }
            int r1 = r13.intValue()     // Catch:{ all -> 0x05e9 }
            r4 = 1
            r5[r4] = r1     // Catch:{ all -> 0x05e9 }
            int r0 = r0.intValue()     // Catch:{ all -> 0x05e9 }
            int r1 = r8.getWidth()     // Catch:{ all -> 0x05e9 }
            int r4 = r8.getHeight()     // Catch:{ all -> 0x05e9 }
            int r4 = r4 - r10
            android.graphics.drawable.BitmapDrawable r0 = org.telegram.ui.Components.BackgroundGradientDrawable.createDitheredGradientBitmapDrawable((int) r0, (int[]) r5, (int) r1, (int) r4)     // Catch:{ all -> 0x05e9 }
            r1 = 90
            r18 = 90
        L_0x02c5:
            int r1 = r8.getWidth()     // Catch:{ all -> 0x05e9 }
            int r4 = r8.getHeight()     // Catch:{ all -> 0x05e9 }
            int r4 = r4 - r10
            r5 = 0
            r0.setBounds(r5, r10, r1, r4)     // Catch:{ all -> 0x05e9 }
            r0.draw(r2)     // Catch:{ all -> 0x05e9 }
            if (r3 != 0) goto L_0x02e2
            int[] r0 = org.telegram.messenger.AndroidUtilities.calcDrawableColor(r0)     // Catch:{ all -> 0x05e9 }
            r0 = r0[r5]     // Catch:{ all -> 0x05e9 }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x05e9 }
            r3 = r0
        L_0x02e2:
            r0 = r18
            r1 = 1
            goto L_0x026b
        L_0x02e6:
            if (r7 == 0) goto L_0x02ee
            int r1 = r7.intValue()     // Catch:{ all -> 0x05e9 }
            if (r1 >= 0) goto L_0x02f7
        L_0x02ee:
            r1 = 0
            r4 = r5[r1]     // Catch:{ all -> 0x05e9 }
            boolean r1 = android.text.TextUtils.isEmpty(r4)     // Catch:{ all -> 0x05e9 }
            if (r1 != 0) goto L_0x0274
        L_0x02f7:
            android.graphics.BitmapFactory$Options r1 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0407 }
            r1.<init>()     // Catch:{ all -> 0x0407 }
            r4 = 1
            r1.inJustDecodeBounds = r4     // Catch:{ all -> 0x0407 }
            r4 = 0
            r6 = r5[r4]     // Catch:{ all -> 0x0407 }
            boolean r4 = android.text.TextUtils.isEmpty(r6)     // Catch:{ all -> 0x0407 }
            if (r4 != 0) goto L_0x0338
            java.io.File r0 = new java.io.File     // Catch:{ all -> 0x0332 }
            java.io.File r4 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ all -> 0x0332 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0332 }
            r6.<init>()     // Catch:{ all -> 0x0332 }
            r13 = 0
            r5 = r5[r13]     // Catch:{ all -> 0x0332 }
            java.lang.String r5 = org.telegram.messenger.Utilities.MD5(r5)     // Catch:{ all -> 0x0332 }
            r6.append(r5)     // Catch:{ all -> 0x0332 }
            java.lang.String r5 = ".wp"
            r6.append(r5)     // Catch:{ all -> 0x0332 }
            java.lang.String r5 = r6.toString()     // Catch:{ all -> 0x0332 }
            r0.<init>(r4, r5)     // Catch:{ all -> 0x0332 }
            java.lang.String r4 = r0.getAbsolutePath()     // Catch:{ all -> 0x0332 }
            android.graphics.BitmapFactory.decodeFile(r4, r1)     // Catch:{ all -> 0x0332 }
            r4 = 0
            goto L_0x034e
        L_0x0332:
            r0 = move-exception
            r1 = 0
            r4 = 0
            r11 = 2
            goto L_0x040b
        L_0x0338:
            java.io.FileInputStream r4 = new java.io.FileInputStream     // Catch:{ all -> 0x0407 }
            r4.<init>(r0)     // Catch:{ all -> 0x0407 }
            java.nio.channels.FileChannel r0 = r4.getChannel()     // Catch:{ all -> 0x0403 }
            int r5 = r7.intValue()     // Catch:{ all -> 0x0403 }
            long r5 = (long) r5     // Catch:{ all -> 0x0403 }
            r0.position(r5)     // Catch:{ all -> 0x0403 }
            r5 = 0
            android.graphics.BitmapFactory.decodeStream(r4, r5, r1)     // Catch:{ all -> 0x0403 }
            r0 = 0
        L_0x034e:
            int r5 = r1.outWidth     // Catch:{ all -> 0x0403 }
            if (r5 <= 0) goto L_0x03f8
            int r6 = r1.outHeight     // Catch:{ all -> 0x0403 }
            if (r6 <= 0) goto L_0x03f8
            float r5 = (float) r5     // Catch:{ all -> 0x0403 }
            float r5 = r5 / r17
            float r6 = (float) r6     // Catch:{ all -> 0x0403 }
            float r6 = r6 / r17
            float r5 = java.lang.Math.min(r5, r6)     // Catch:{ all -> 0x0403 }
            r6 = 1
            r1.inSampleSize = r6     // Catch:{ all -> 0x0403 }
            int r6 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1))
            if (r6 <= 0) goto L_0x0374
        L_0x0367:
            int r6 = r1.inSampleSize     // Catch:{ all -> 0x0403 }
            r11 = 2
            int r6 = r6 * 2
            r1.inSampleSize = r6     // Catch:{ all -> 0x03f6 }
            float r6 = (float) r6     // Catch:{ all -> 0x03f6 }
            int r6 = (r6 > r5 ? 1 : (r6 == r5 ? 0 : -1))
            if (r6 < 0) goto L_0x0367
            goto L_0x0375
        L_0x0374:
            r11 = 2
        L_0x0375:
            r5 = 0
            r1.inJustDecodeBounds = r5     // Catch:{ all -> 0x03f6 }
            if (r0 == 0) goto L_0x0383
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ all -> 0x03f6 }
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r0, r1)     // Catch:{ all -> 0x03f6 }
            goto L_0x0394
        L_0x0383:
            java.nio.channels.FileChannel r0 = r4.getChannel()     // Catch:{ all -> 0x03f6 }
            int r5 = r7.intValue()     // Catch:{ all -> 0x03f6 }
            long r5 = (long) r5     // Catch:{ all -> 0x03f6 }
            r0.position(r5)     // Catch:{ all -> 0x03f6 }
            r5 = 0
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeStream(r4, r5, r1)     // Catch:{ all -> 0x03f6 }
        L_0x0394:
            if (r0 == 0) goto L_0x03f9
            android.graphics.Paint r1 = new android.graphics.Paint     // Catch:{ all -> 0x03f6 }
            r1.<init>()     // Catch:{ all -> 0x03f6 }
            r5 = 1
            r1.setFilterBitmap(r5)     // Catch:{ all -> 0x03f6 }
            int r5 = r0.getWidth()     // Catch:{ all -> 0x03f6 }
            float r5 = (float) r5     // Catch:{ all -> 0x03f6 }
            float r5 = r5 / r17
            int r6 = r0.getHeight()     // Catch:{ all -> 0x03f6 }
            float r6 = (float) r6     // Catch:{ all -> 0x03f6 }
            float r6 = r6 / r17
            float r5 = java.lang.Math.min(r5, r6)     // Catch:{ all -> 0x03f6 }
            int r6 = r0.getWidth()     // Catch:{ all -> 0x03f6 }
            float r6 = (float) r6     // Catch:{ all -> 0x03f6 }
            float r6 = r6 / r5
            int r7 = r0.getHeight()     // Catch:{ all -> 0x03f6 }
            float r7 = (float) r7     // Catch:{ all -> 0x03f6 }
            float r7 = r7 / r5
            r15.set(r14, r14, r6, r7)     // Catch:{ all -> 0x03f6 }
            int r5 = r8.getWidth()     // Catch:{ all -> 0x03f6 }
            float r5 = (float) r5     // Catch:{ all -> 0x03f6 }
            float r6 = r15.width()     // Catch:{ all -> 0x03f6 }
            float r5 = r5 - r6
            float r5 = r5 / r12
            int r6 = r8.getHeight()     // Catch:{ all -> 0x03f6 }
            float r6 = (float) r6     // Catch:{ all -> 0x03f6 }
            float r7 = r15.height()     // Catch:{ all -> 0x03f6 }
            float r6 = r6 - r7
            float r6 = r6 / r12
            r15.offset(r5, r6)     // Catch:{ all -> 0x03f6 }
            r5 = 0
            r2.drawBitmap(r0, r5, r15, r1)     // Catch:{ all -> 0x03f6 }
            if (r3 != 0) goto L_0x03f4
            android.graphics.drawable.BitmapDrawable r1 = new android.graphics.drawable.BitmapDrawable     // Catch:{ all -> 0x03f1 }
            r1.<init>(r0)     // Catch:{ all -> 0x03f1 }
            int[] r0 = org.telegram.messenger.AndroidUtilities.calcDrawableColor(r1)     // Catch:{ all -> 0x03f1 }
            r1 = 0
            r0 = r0[r1]     // Catch:{ all -> 0x03f1 }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x03f1 }
            r3 = r0
            goto L_0x03f4
        L_0x03f1:
            r0 = move-exception
            r1 = 1
            goto L_0x040b
        L_0x03f4:
            r1 = 1
            goto L_0x03fa
        L_0x03f6:
            r0 = move-exception
            goto L_0x0405
        L_0x03f8:
            r11 = 2
        L_0x03f9:
            r1 = 0
        L_0x03fa:
            if (r4 == 0) goto L_0x0419
            r4.close()     // Catch:{ Exception -> 0x0400 }
            goto L_0x0419
        L_0x0400:
            r0 = move-exception
            r4 = r0
            goto L_0x0416
        L_0x0403:
            r0 = move-exception
            r11 = 2
        L_0x0405:
            r1 = 0
            goto L_0x040b
        L_0x0407:
            r0 = move-exception
            r11 = 2
            r1 = 0
            r4 = 0
        L_0x040b:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x041c }
            if (r4 == 0) goto L_0x0419
            r4.close()     // Catch:{ Exception -> 0x0414 }
            goto L_0x0419
        L_0x0414:
            r0 = move-exception
            r4 = r0
        L_0x0416:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ all -> 0x05e9 }
        L_0x0419:
            r0 = 80
            goto L_0x042d
        L_0x041c:
            r0 = move-exception
            r1 = r0
            if (r4 == 0) goto L_0x0429
            r4.close()     // Catch:{ Exception -> 0x0424 }
            goto L_0x0429
        L_0x0424:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x05e9 }
        L_0x0429:
            throw r1     // Catch:{ all -> 0x05e9 }
        L_0x042a:
            r0 = 80
            r1 = 0
        L_0x042d:
            if (r1 != 0) goto L_0x0465
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x05e9 }
            android.content.res.Resources r1 = r1.getResources()     // Catch:{ all -> 0x05e9 }
            r4 = 2131165337(0x7var_, float:1.7944888E38)
            android.graphics.drawable.Drawable r1 = r1.getDrawable(r4)     // Catch:{ all -> 0x05e9 }
            android.graphics.drawable.Drawable r1 = r1.mutate()     // Catch:{ all -> 0x05e9 }
            android.graphics.drawable.BitmapDrawable r1 = (android.graphics.drawable.BitmapDrawable) r1     // Catch:{ all -> 0x05e9 }
            if (r3 != 0) goto L_0x044f
            int[] r3 = org.telegram.messenger.AndroidUtilities.calcDrawableColor(r1)     // Catch:{ all -> 0x05e9 }
            r4 = 0
            r3 = r3[r4]     // Catch:{ all -> 0x05e9 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ all -> 0x05e9 }
        L_0x044f:
            android.graphics.Shader$TileMode r4 = android.graphics.Shader.TileMode.REPEAT     // Catch:{ all -> 0x05e9 }
            r1.setTileModeXY(r4, r4)     // Catch:{ all -> 0x05e9 }
            int r4 = r8.getWidth()     // Catch:{ all -> 0x05e9 }
            int r5 = r8.getHeight()     // Catch:{ all -> 0x05e9 }
            int r5 = r5 - r10
            r6 = 0
            r1.setBounds(r6, r10, r4, r5)     // Catch:{ all -> 0x05e9 }
            r1.draw(r2)     // Catch:{ all -> 0x05e9 }
            goto L_0x0466
        L_0x0465:
            r6 = 0
        L_0x0466:
            r4 = r20
            r1 = r21
            r1.setColor(r4)     // Catch:{ all -> 0x05e9 }
            r4 = 0
            r5 = 0
            int r7 = r8.getWidth()     // Catch:{ all -> 0x05e9 }
            float r12 = (float) r7     // Catch:{ all -> 0x05e9 }
            r13 = 1123024896(0x42var_, float:120.0)
            r7 = r9
            r9 = r2
            r14 = r23
            r17 = 120(0x78, float:1.68E-43)
            r10 = r4
            r4 = 2
            r11 = r5
            r5 = r25
            r6 = r19
            r35 = r14
            r34 = r32
            r14 = r1
            r9.drawRect(r10, r11, r12, r13, r14)     // Catch:{ all -> 0x05e9 }
            if (r33 == 0) goto L_0x04a8
            r9 = 13
            int r10 = r33.getIntrinsicHeight()     // Catch:{ all -> 0x05e9 }
            int r10 = 120 - r10
            int r10 = r10 / r4
            int r11 = r33.getIntrinsicWidth()     // Catch:{ all -> 0x05e9 }
            int r11 = r11 + r9
            int r12 = r33.getIntrinsicHeight()     // Catch:{ all -> 0x05e9 }
            int r12 = r12 + r10
            r13 = r33
            r13.setBounds(r9, r10, r11, r12)     // Catch:{ all -> 0x05e9 }
            r13.draw(r2)     // Catch:{ all -> 0x05e9 }
        L_0x04a8:
            r9 = r34
            if (r9 == 0) goto L_0x04ce
            int r10 = r8.getWidth()     // Catch:{ all -> 0x05e9 }
            int r11 = r9.getIntrinsicWidth()     // Catch:{ all -> 0x05e9 }
            int r10 = r10 - r11
            int r10 = r10 + -10
            int r11 = r9.getIntrinsicHeight()     // Catch:{ all -> 0x05e9 }
            int r11 = 120 - r11
            int r11 = r11 / r4
            int r12 = r9.getIntrinsicWidth()     // Catch:{ all -> 0x05e9 }
            int r12 = r12 + r10
            int r13 = r9.getIntrinsicHeight()     // Catch:{ all -> 0x05e9 }
            int r13 = r13 + r11
            r9.setBounds(r10, r11, r12, r13)     // Catch:{ all -> 0x05e9 }
            r9.draw(r2)     // Catch:{ all -> 0x05e9 }
        L_0x04ce:
            r9 = 1
            r10 = r7[r9]     // Catch:{ all -> 0x05e9 }
            r9 = 216(0xd8, float:3.03E-43)
            int r11 = r8.getWidth()     // Catch:{ all -> 0x05e9 }
            r12 = 20
            int r11 = r11 - r12
            r13 = 308(0x134, float:4.32E-43)
            r14 = 161(0xa1, float:2.26E-43)
            r10.setBounds(r14, r9, r11, r13)     // Catch:{ all -> 0x05e9 }
            r9 = 1
            r10 = r7[r9]     // Catch:{ all -> 0x05e9 }
            r11 = 522(0x20a, float:7.31E-43)
            r13 = 0
            r10.setTop(r13, r11, r13, r13)     // Catch:{ all -> 0x05e9 }
            r10 = r7[r9]     // Catch:{ all -> 0x05e9 }
            r10.draw(r2)     // Catch:{ all -> 0x05e9 }
            r10 = r7[r9]     // Catch:{ all -> 0x05e9 }
            int r16 = r8.getWidth()     // Catch:{ all -> 0x05e9 }
            int r4 = r16 + -20
            r12 = 430(0x1ae, float:6.03E-43)
            r10.setBounds(r14, r12, r4, r11)     // Catch:{ all -> 0x05e9 }
            r4 = r7[r9]     // Catch:{ all -> 0x05e9 }
            r4.setTop(r12, r11, r13, r13)     // Catch:{ all -> 0x05e9 }
            r4 = r7[r9]     // Catch:{ all -> 0x05e9 }
            r4.draw(r2)     // Catch:{ all -> 0x05e9 }
            r4 = r7[r13]     // Catch:{ all -> 0x05e9 }
            r9 = 399(0x18f, float:5.59E-43)
            r10 = 415(0x19f, float:5.82E-43)
            r12 = 323(0x143, float:4.53E-43)
            r14 = 20
            r4.setBounds(r14, r12, r9, r10)     // Catch:{ all -> 0x05e9 }
            r4 = r7[r13]     // Catch:{ all -> 0x05e9 }
            r4.setTop(r12, r11, r13, r13)     // Catch:{ all -> 0x05e9 }
            r4 = r7[r13]     // Catch:{ all -> 0x05e9 }
            r4.draw(r2)     // Catch:{ all -> 0x05e9 }
            if (r3 == 0) goto L_0x0540
            int r4 = r8.getWidth()     // Catch:{ all -> 0x05e9 }
            int r4 = r4 + -126
            r7 = 2
            int r4 = r4 / r7
            r7 = 150(0x96, float:2.1E-43)
            float r9 = (float) r4     // Catch:{ all -> 0x05e9 }
            float r7 = (float) r7     // Catch:{ all -> 0x05e9 }
            int r4 = r4 + 126
            float r4 = (float) r4     // Catch:{ all -> 0x05e9 }
            r10 = 192(0xc0, float:2.69E-43)
            float r10 = (float) r10     // Catch:{ all -> 0x05e9 }
            r15.set(r9, r7, r4, r10)     // Catch:{ all -> 0x05e9 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x05e9 }
            r1.setColor(r3)     // Catch:{ all -> 0x05e9 }
            r3 = 1101529088(0x41a80000, float:21.0)
            r2.drawRoundRect(r15, r3, r3, r1)     // Catch:{ all -> 0x05e9 }
        L_0x0540:
            r1.setColor(r6)     // Catch:{ all -> 0x05e9 }
            r10 = 0
            int r3 = r8.getHeight()     // Catch:{ all -> 0x05e9 }
            int r3 = r3 + -120
            float r11 = (float) r3     // Catch:{ all -> 0x05e9 }
            int r3 = r8.getWidth()     // Catch:{ all -> 0x05e9 }
            float r12 = (float) r3     // Catch:{ all -> 0x05e9 }
            int r3 = r8.getHeight()     // Catch:{ all -> 0x05e9 }
            float r13 = (float) r3     // Catch:{ all -> 0x05e9 }
            r9 = r2
            r14 = r1
            r9.drawRect(r10, r11, r12, r13, r14)     // Catch:{ all -> 0x05e9 }
            r1 = 22
            if (r5 == 0) goto L_0x057d
            int r3 = r8.getHeight()     // Catch:{ all -> 0x05e9 }
            int r3 = r3 + -120
            int r4 = r5.getIntrinsicHeight()     // Catch:{ all -> 0x05e9 }
            int r10 = 120 - r4
            r4 = 2
            int r10 = r10 / r4
            int r3 = r3 + r10
            int r4 = r5.getIntrinsicWidth()     // Catch:{ all -> 0x05e9 }
            int r4 = r4 + r1
            int r6 = r5.getIntrinsicHeight()     // Catch:{ all -> 0x05e9 }
            int r6 = r6 + r3
            r5.setBounds(r1, r3, r4, r6)     // Catch:{ all -> 0x05e9 }
            r5.draw(r2)     // Catch:{ all -> 0x05e9 }
        L_0x057d:
            r3 = r35
            if (r3 == 0) goto L_0x05aa
            int r4 = r8.getWidth()     // Catch:{ all -> 0x05e9 }
            int r5 = r3.getIntrinsicWidth()     // Catch:{ all -> 0x05e9 }
            int r4 = r4 - r5
            int r4 = r4 - r1
            int r1 = r8.getHeight()     // Catch:{ all -> 0x05e9 }
            int r1 = r1 + -120
            int r5 = r3.getIntrinsicHeight()     // Catch:{ all -> 0x05e9 }
            int r10 = 120 - r5
            r5 = 2
            int r10 = r10 / r5
            int r1 = r1 + r10
            int r5 = r3.getIntrinsicWidth()     // Catch:{ all -> 0x05e9 }
            int r5 = r5 + r4
            int r6 = r3.getIntrinsicHeight()     // Catch:{ all -> 0x05e9 }
            int r6 = r6 + r1
            r3.setBounds(r4, r1, r5, r6)     // Catch:{ all -> 0x05e9 }
            r3.draw(r2)     // Catch:{ all -> 0x05e9 }
        L_0x05aa:
            r1 = 0
            r2.setBitmap(r1)     // Catch:{ all -> 0x05e9 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x05e9 }
            r1.<init>()     // Catch:{ all -> 0x05e9 }
            java.lang.String r2 = "-2147483648_"
            r1.append(r2)     // Catch:{ all -> 0x05e9 }
            int r2 = org.telegram.messenger.SharedConfig.getLastLocalId()     // Catch:{ all -> 0x05e9 }
            r1.append(r2)     // Catch:{ all -> 0x05e9 }
            java.lang.String r2 = ".jpg"
            r1.append(r2)     // Catch:{ all -> 0x05e9 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x05e9 }
            java.io.File r2 = new java.io.File     // Catch:{ all -> 0x05e9 }
            r3 = 4
            java.io.File r3 = org.telegram.messenger.FileLoader.getDirectory(r3)     // Catch:{ all -> 0x05e9 }
            r2.<init>(r3, r1)     // Catch:{ all -> 0x05e9 }
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch:{ all -> 0x05e4 }
            r1.<init>(r2)     // Catch:{ all -> 0x05e4 }
            android.graphics.Bitmap$CompressFormat r3 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ all -> 0x05e4 }
            r8.compress(r3, r0, r1)     // Catch:{ all -> 0x05e4 }
            org.telegram.messenger.SharedConfig.saveConfig()     // Catch:{ all -> 0x05e4 }
            java.lang.String r0 = r2.getAbsolutePath()     // Catch:{ all -> 0x05e4 }
            return r0
        L_0x05e4:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x05e9 }
            goto L_0x05ed
        L_0x05e9:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x05ed:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.createThemePreviewImage(java.lang.String, java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeAccent):java.lang.String");
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(2:34|35) */
    /* JADX WARNING: Code restructure failed: missing block: B:35:?, code lost:
        r4 = org.telegram.messenger.Utilities.parseInt(r13).intValue();
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:34:0x0081 */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00c9 A[SYNTHETIC, Splitter:B:55:0x00c9] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.HashMap<java.lang.String, java.lang.Integer> getThemeFileValues(java.io.File r16, java.lang.String r17, java.lang.String[] r18) {
        /*
            r0 = r18
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            r2 = 1024(0x400, float:1.435E-42)
            r3 = 0
            byte[] r2 = new byte[r2]     // Catch:{ all -> 0x00c3 }
            if (r17 == 0) goto L_0x0013
            java.io.File r4 = getAssetFile(r17)     // Catch:{ all -> 0x00c3 }
            goto L_0x0015
        L_0x0013:
            r4 = r16
        L_0x0015:
            java.io.FileInputStream r5 = new java.io.FileInputStream     // Catch:{ all -> 0x00c3 }
            r5.<init>(r4)     // Catch:{ all -> 0x00c3 }
            r3 = -1
            r4 = 0
            r6 = 0
            r7 = -1
            r8 = 0
        L_0x001f:
            int r9 = r5.read(r2)     // Catch:{ all -> 0x00c0 }
            if (r9 == r3) goto L_0x00b3
            r12 = r6
            r10 = 0
            r11 = 0
        L_0x0028:
            r13 = 1
            if (r10 >= r9) goto L_0x00a0
            byte r14 = r2[r10]     // Catch:{ all -> 0x00c0 }
            r15 = 10
            if (r14 != r15) goto L_0x009b
            int r14 = r10 - r11
            int r14 = r14 + r13
            java.lang.String r15 = new java.lang.String     // Catch:{ all -> 0x00c0 }
            int r13 = r14 + -1
            r15.<init>(r2, r11, r13)     // Catch:{ all -> 0x00c0 }
            java.lang.String r13 = "WLS="
            boolean r13 = r15.startsWith(r13)     // Catch:{ all -> 0x00c0 }
            if (r13 == 0) goto L_0x0050
            if (r0 == 0) goto L_0x0099
            int r13 = r0.length     // Catch:{ all -> 0x00c0 }
            if (r13 <= 0) goto L_0x0099
            r13 = 4
            java.lang.String r13 = r15.substring(r13)     // Catch:{ all -> 0x00c0 }
            r0[r4] = r13     // Catch:{ all -> 0x00c0 }
            goto L_0x0099
        L_0x0050:
            java.lang.String r13 = "WPS"
            boolean r13 = r15.startsWith(r13)     // Catch:{ all -> 0x00c0 }
            if (r13 == 0) goto L_0x005c
            int r14 = r14 + r12
            r7 = r14
            r8 = 1
            goto L_0x00a0
        L_0x005c:
            r13 = 61
            int r13 = r15.indexOf(r13)     // Catch:{ all -> 0x00c0 }
            if (r13 == r3) goto L_0x0099
            java.lang.String r3 = r15.substring(r4, r13)     // Catch:{ all -> 0x00c0 }
            int r13 = r13 + 1
            java.lang.String r13 = r15.substring(r13)     // Catch:{ all -> 0x00c0 }
            int r15 = r13.length()     // Catch:{ all -> 0x00c0 }
            if (r15 <= 0) goto L_0x008a
            char r15 = r13.charAt(r4)     // Catch:{ all -> 0x00c0 }
            r4 = 35
            if (r15 != r4) goto L_0x008a
            int r4 = android.graphics.Color.parseColor(r13)     // Catch:{ Exception -> 0x0081 }
            goto L_0x0092
        L_0x0081:
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r13)     // Catch:{ all -> 0x00c0 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x00c0 }
            goto L_0x0092
        L_0x008a:
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r13)     // Catch:{ all -> 0x00c0 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x00c0 }
        L_0x0092:
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x00c0 }
            r1.put(r3, r4)     // Catch:{ all -> 0x00c0 }
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
            java.nio.channels.FileChannel r3 = r5.getChannel()     // Catch:{ all -> 0x00c0 }
            long r9 = (long) r12     // Catch:{ all -> 0x00c0 }
            r3.position(r9)     // Catch:{ all -> 0x00c0 }
            if (r8 == 0) goto L_0x00ae
            goto L_0x00b3
        L_0x00ae:
            r6 = r12
            r3 = -1
            r4 = 0
            goto L_0x001f
        L_0x00b3:
            java.lang.String r0 = "wallpaperFileOffset"
            java.lang.Integer r2 = java.lang.Integer.valueOf(r7)     // Catch:{ all -> 0x00c0 }
            r1.put(r0, r2)     // Catch:{ all -> 0x00c0 }
            r5.close()     // Catch:{ Exception -> 0x00cd }
            goto L_0x00d2
        L_0x00c0:
            r0 = move-exception
            r3 = r5
            goto L_0x00c4
        L_0x00c3:
            r0 = move-exception
        L_0x00c4:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x00d3 }
            if (r3 == 0) goto L_0x00d2
            r3.close()     // Catch:{ Exception -> 0x00cd }
            goto L_0x00d2
        L_0x00cd:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x00d2:
            return r1
        L_0x00d3:
            r0 = move-exception
            r1 = r0
            if (r3 == 0) goto L_0x00e0
            r3.close()     // Catch:{ Exception -> 0x00db }
            goto L_0x00e0
        L_0x00db:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x00e0:
            goto L_0x00e2
        L_0x00e1:
            throw r1
        L_0x00e2:
            goto L_0x00e1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.getThemeFileValues(java.io.File, java.lang.String, java.lang.String[]):java.util.HashMap");
    }

    public static void createCommonResources(Context context) {
        if (dividerPaint == null) {
            Paint paint = new Paint();
            dividerPaint = paint;
            paint.setStrokeWidth(1.0f);
            Paint paint2 = new Paint();
            dividerExtraPaint = paint2;
            paint2.setStrokeWidth(1.0f);
            avatar_backgroundPaint = new Paint(1);
            Paint paint3 = new Paint(1);
            checkboxSquare_checkPaint = paint3;
            paint3.setStyle(Paint.Style.STROKE);
            checkboxSquare_checkPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            checkboxSquare_checkPaint.setStrokeCap(Paint.Cap.ROUND);
            Paint paint4 = new Paint(1);
            checkboxSquare_eraserPaint = paint4;
            paint4.setColor(0);
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
            avatarDrawables[10] = resources.getDrawable(NUM);
            avatarDrawables[11] = resources.getDrawable(NUM);
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
            RLottieDrawable rLottieDrawable6 = dialogs_hidePsaDrawable;
            if (rLottieDrawable6 != null) {
                rLottieDrawable6.recycle();
            }
            dialogs_archiveAvatarDrawable = new RLottieDrawable(NUM, "chats_archiveavatar", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f), false, (int[]) null);
            dialogs_archiveDrawable = new RLottieDrawable(NUM, "chats_archive", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            dialogs_unarchiveDrawable = new RLottieDrawable(NUM, "chats_unarchive", AndroidUtilities.dp((float) AndroidUtilities.dp(36.0f)), AndroidUtilities.dp(36.0f));
            dialogs_pinArchiveDrawable = new RLottieDrawable(NUM, "chats_hide", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            dialogs_unpinArchiveDrawable = new RLottieDrawable(NUM, "chats_unhide", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            dialogs_hidePsaDrawable = new RLottieDrawable(NUM, "chats_psahide", AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f));
            dialogs_swipeMuteDrawable = new RLottieDrawable(NUM, "swipe_mute", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            dialogs_swipeUnmuteDrawable = new RLottieDrawable(NUM, "swipe_unmute", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            dialogs_swipeReadDrawable = new RLottieDrawable(NUM, "swipe_read", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            dialogs_swipeUnreadDrawable = new RLottieDrawable(NUM, "swipe_unread", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            dialogs_swipeDeleteDrawable = new RLottieDrawable(NUM, "swipe_delete", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            dialogs_swipeUnpinDrawable = new RLottieDrawable(NUM, "swipe_unpin", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            dialogs_swipePinDrawable = new RLottieDrawable(NUM, "swipe_pin", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
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
                    dialogs_hidePsaDrawable.beginApplyLayerColors();
                    dialogs_hidePsaDrawable.setLayerColor("Line 1.**", getNonAnimatedColor("chats_archiveBackground"));
                    dialogs_hidePsaDrawable.setLayerColor("Line 2.**", getNonAnimatedColor("chats_archiveBackground"));
                    dialogs_hidePsaDrawable.setLayerColor("Line 3.**", getNonAnimatedColor("chats_archiveBackground"));
                    dialogs_hidePsaDrawable.setLayerColor("Cup Red.**", getNonAnimatedColor("chats_archiveIcon"));
                    dialogs_hidePsaDrawable.setLayerColor("Box.**", getNonAnimatedColor("chats_archiveIcon"));
                    dialogs_hidePsaDrawable.commitApplyLayerColors();
                    dialogs_hidePsaDrawableRecolored = false;
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
            TextPaint textPaint6 = new TextPaint(1);
            dialogs_archiveTextPaintSmall = textPaint6;
            textPaint6.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            dialogs_onlinePaint = new TextPaint(1);
            dialogs_offlinePaint = new TextPaint(1);
            dialogs_tabletSeletedPaint = new Paint();
            dialogs_pinnedPaint = new Paint(1);
            dialogs_onlineCirclePaint = new Paint(1);
            dialogs_countPaint = new Paint(1);
            dialogs_countGrayPaint = new Paint(1);
            dialogs_errorPaint = new Paint(1);
            dialogs_actionMessagePaint = new Paint(1);
            dialogs_lockDrawable = resources.getDrawable(NUM);
            dialogs_checkDrawable = resources.getDrawable(NUM).mutate();
            dialogs_playDrawable = resources.getDrawable(NUM).mutate();
            dialogs_checkReadDrawable = resources.getDrawable(NUM).mutate();
            dialogs_halfCheckDrawable = resources.getDrawable(NUM);
            dialogs_clockDrawable = new MsgClockDrawable();
            dialogs_errorDrawable = resources.getDrawable(NUM);
            dialogs_reorderDrawable = resources.getDrawable(NUM).mutate();
            dialogs_groupDrawable = resources.getDrawable(NUM);
            dialogs_broadcastDrawable = resources.getDrawable(NUM);
            dialogs_muteDrawable = resources.getDrawable(NUM).mutate();
            dialogs_verifiedDrawable = resources.getDrawable(NUM).mutate();
            dialogs_scamDrawable = new ScamDrawable(11, 0);
            dialogs_fakeDrawable = new ScamDrawable(11, 1);
            dialogs_verifiedCheckDrawable = resources.getDrawable(NUM).mutate();
            dialogs_mentionDrawable = resources.getDrawable(NUM);
            dialogs_botDrawable = resources.getDrawable(NUM);
            dialogs_pinnedDrawable = resources.getDrawable(NUM);
            moveUpDrawable = resources.getDrawable(NUM);
            RectF rectF = new RectF();
            chat_updatePath[0] = new Path();
            chat_updatePath[2] = new Path();
            float dp = (float) AndroidUtilities.dp(12.0f);
            float dp2 = (float) AndroidUtilities.dp(12.0f);
            rectF.set(dp - ((float) AndroidUtilities.dp(5.0f)), dp2 - ((float) AndroidUtilities.dp(5.0f)), ((float) AndroidUtilities.dp(5.0f)) + dp, ((float) AndroidUtilities.dp(5.0f)) + dp2);
            chat_updatePath[2].arcTo(rectF, -160.0f, -110.0f, true);
            chat_updatePath[2].arcTo(rectF, 20.0f, -110.0f, true);
            chat_updatePath[0].moveTo(dp, ((float) AndroidUtilities.dp(8.0f)) + dp2);
            chat_updatePath[0].lineTo(dp, ((float) AndroidUtilities.dp(2.0f)) + dp2);
            chat_updatePath[0].lineTo(((float) AndroidUtilities.dp(3.0f)) + dp, ((float) AndroidUtilities.dp(5.0f)) + dp2);
            chat_updatePath[0].close();
            chat_updatePath[0].moveTo(dp, dp2 - ((float) AndroidUtilities.dp(8.0f)));
            chat_updatePath[0].lineTo(dp, dp2 - ((float) AndroidUtilities.dp(2.0f)));
            chat_updatePath[0].lineTo(dp - ((float) AndroidUtilities.dp(3.0f)), dp2 - ((float) AndroidUtilities.dp(5.0f)));
            chat_updatePath[0].close();
            applyDialogsTheme();
        }
        dialogs_messageNamePaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        dialogs_timePaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        dialogs_countTextPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        dialogs_archiveTextPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        dialogs_archiveTextPaintSmall.setTextSize((float) AndroidUtilities.dp(11.0f));
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
            dialogs_archiveTextPaintSmall.setColor(getColor("chats_archiveText"));
            dialogs_countPaint.setColor(getColor("chats_unreadCounter"));
            dialogs_countGrayPaint.setColor(getColor("chats_unreadCounterMuted"));
            dialogs_actionMessagePaint.setColor(getColor("chats_actionMessage"));
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
            setDrawableColorByKey(dialogs_fakeDrawable, "chats_draft");
        }
    }

    public static void reloadAllResources(Context context) {
        destroyResources();
        if (chat_msgInDrawable != null) {
            chat_msgInDrawable = null;
            currentColor = 0;
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

    /* JADX WARNING: Can't wrap try/catch for region: R(9:15|16|(4:18|(1:20)(1:21)|22|23)|41|24|25|26|27|28) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:27:0x0ca3 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void createChatResources(android.content.Context r17, boolean r18) {
        /*
            java.lang.Object r1 = sync
            monitor-enter(r1)
            android.text.TextPaint r0 = chat_msgTextPaint     // Catch:{ all -> 0x0e4e }
            r2 = 1
            if (r0 != 0) goto L_0x003b
            android.text.TextPaint r0 = new android.text.TextPaint     // Catch:{ all -> 0x0e4e }
            r0.<init>(r2)     // Catch:{ all -> 0x0e4e }
            chat_msgTextPaint = r0     // Catch:{ all -> 0x0e4e }
            android.text.TextPaint r0 = new android.text.TextPaint     // Catch:{ all -> 0x0e4e }
            r0.<init>(r2)     // Catch:{ all -> 0x0e4e }
            chat_msgGameTextPaint = r0     // Catch:{ all -> 0x0e4e }
            android.text.TextPaint r0 = new android.text.TextPaint     // Catch:{ all -> 0x0e4e }
            r0.<init>(r2)     // Catch:{ all -> 0x0e4e }
            chat_msgTextPaintOneEmoji = r0     // Catch:{ all -> 0x0e4e }
            android.text.TextPaint r0 = new android.text.TextPaint     // Catch:{ all -> 0x0e4e }
            r0.<init>(r2)     // Catch:{ all -> 0x0e4e }
            chat_msgTextPaintTwoEmoji = r0     // Catch:{ all -> 0x0e4e }
            android.text.TextPaint r0 = new android.text.TextPaint     // Catch:{ all -> 0x0e4e }
            r0.<init>(r2)     // Catch:{ all -> 0x0e4e }
            chat_msgTextPaintThreeEmoji = r0     // Catch:{ all -> 0x0e4e }
            android.text.TextPaint r0 = new android.text.TextPaint     // Catch:{ all -> 0x0e4e }
            r0.<init>(r2)     // Catch:{ all -> 0x0e4e }
            chat_msgBotButtonPaint = r0     // Catch:{ all -> 0x0e4e }
            java.lang.String r3 = "fonts/rmedium.ttf"
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3)     // Catch:{ all -> 0x0e4e }
            r0.setTypeface(r3)     // Catch:{ all -> 0x0e4e }
        L_0x003b:
            monitor-exit(r1)     // Catch:{ all -> 0x0e4e }
            r0 = 1077936128(0x40400000, float:3.0)
            r1 = 1096810496(0x41600000, float:14.0)
            r3 = 2
            if (r18 != 0) goto L_0x0cad
            org.telegram.ui.ActionBar.Theme$MessageDrawable r4 = chat_msgInDrawable
            if (r4 != 0) goto L_0x0cad
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_infoPaint = r4
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r2)
            chat_stickerCommentCountPaint = r4
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r4.setTypeface(r5)
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
            android.graphics.Paint r4 = chat_instantViewRectPaint
            android.graphics.Paint$Cap r5 = android.graphics.Paint.Cap.ROUND
            r4.setStrokeCap(r5)
            android.graphics.Paint r4 = new android.graphics.Paint
            r4.<init>(r2)
            chat_pollTimerPaint = r4
            android.graphics.Paint$Style r5 = android.graphics.Paint.Style.STROKE
            r4.setStyle(r5)
            android.graphics.Paint r4 = chat_pollTimerPaint
            android.graphics.Paint$Cap r5 = android.graphics.Paint.Cap.ROUND
            r4.setStrokeCap(r5)
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
            chat_actionBackgroundSelectedPaint = r4
            android.graphics.Paint r4 = new android.graphics.Paint
            r4.<init>(r2)
            chat_actionBackgroundPaint2 = r4
            android.graphics.Paint r4 = new android.graphics.Paint
            r4.<init>(r2)
            chat_actionBackgroundSelectedPaint2 = r4
            android.graphics.Paint r4 = new android.graphics.Paint
            r4.<init>(r2)
            chat_actionBackgroundGradientDarkenPaint = r4
            r5 = 704643072(0x2a000000, float:1.1368684E-13)
            r4.setColor(r5)
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
            r5 = 2131166112(0x7var_a0, float:1.794646E38)
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
            org.telegram.ui.Components.PathAnimator r5 = new org.telegram.ui.Components.PathAnimator
            r7 = 1050018841(0x3e960419, float:0.293)
            r8 = -1043333120(0xffffffffc1d00000, float:-26.0)
            r9 = -1042284544(0xffffffffc1e00000, float:-28.0)
            r10 = 1065353216(0x3var_, float:1.0)
            r5.<init>(r7, r8, r9, r10)
            playPauseAnimator = r5
            java.lang.String r7 = "M 34.141 16.042 C 37.384 17.921 40.886 20.001 44.211 21.965 C 46.139 23.104 49.285 24.729 49.586 25.917 C 50.289 28.687 48.484 30 46.274 30 L 6 30.021 C 3.79 30.021 2.075 30.023 2 26.021 L 2.009 3.417 C 2.009 0.417 5.326 -0.58 7.068 0.417 C 10.545 2.406 25.024 10.761 34.141 16.042 Z"
            r8 = 1126563840(0x43260000, float:166.0)
            r5.addSvgKeyFrame(r7, r8)
            org.telegram.ui.Components.PathAnimator r5 = playPauseAnimator
            java.lang.String r7 = "M 37.843 17.769 C 41.143 19.508 44.131 21.164 47.429 23.117 C 48.542 23.775 49.623 24.561 49.761 25.993 C 50.074 28.708 48.557 30 46.347 30 L 6 30.012 C 3.79 30.012 2 28.222 2 26.012 L 2.009 4.609 C 2.009 1.626 5.276 0.664 7.074 1.541 C 10.608 3.309 28.488 12.842 37.843 17.769 Z"
            r8 = 1128792064(0x43480000, float:200.0)
            r5.addSvgKeyFrame(r7, r8)
            org.telegram.ui.Components.PathAnimator r5 = playPauseAnimator
            java.lang.String r7 = "M 40.644 18.756 C 43.986 20.389 49.867 23.108 49.884 25.534 C 49.897 27.154 49.88 24.441 49.894 26.059 C 49.911 28.733 48.6 30 46.39 30 L 6 30.013 C 3.79 30.013 2 28.223 2 26.013 L 2.008 5.52 C 2.008 2.55 5.237 1.614 7.079 2.401 C 10.656 4 31.106 14.097 40.644 18.756 Z"
            r8 = 1129906176(0x43590000, float:217.0)
            r5.addSvgKeyFrame(r7, r8)
            org.telegram.ui.Components.PathAnimator r5 = playPauseAnimator
            java.lang.String r7 = "M 43.782 19.218 C 47.117 20.675 50.075 21.538 50.041 24.796 C 50.022 26.606 50.038 24.309 50.039 26.104 C 50.038 28.736 48.663 30 46.453 30 L 6 29.986 C 3.79 29.986 2 28.196 2 25.986 L 2.008 6.491 C 2.008 3.535 5.196 2.627 7.085 3.316 C 10.708 4.731 33.992 14.944 43.782 19.218 Z"
            r8 = 1131020288(0x436a0000, float:234.0)
            r5.addSvgKeyFrame(r7, r8)
            org.telegram.ui.Components.PathAnimator r5 = playPauseAnimator
            java.lang.String r7 = "M 47.421 16.941 C 50.544 18.191 50.783 19.91 50.769 22.706 C 50.761 24.484 50.76 23.953 50.79 26.073 C 50.814 27.835 49.334 30 47.124 30 L 5 30.01 C 2.79 30.01 1 28.22 1 26.01 L 1.001 10.823 C 1.001 8.218 3.532 6.895 5.572 7.26 C 7.493 8.01 47.421 16.941 47.421 16.941 Z"
            r8 = 1132822528(0x43858000, float:267.0)
            r5.addSvgKeyFrame(r7, r8)
            org.telegram.ui.Components.PathAnimator r5 = playPauseAnimator
            java.lang.String r7 = "M 47.641 17.125 C 50.641 18.207 51.09 19.935 51.078 22.653 C 51.07 24.191 51.062 21.23 51.088 23.063 C 51.109 24.886 49.587 27 47.377 27 L 5 27.009 C 2.79 27.009 1 25.219 1 23.009 L 0.983 11.459 C 0.983 8.908 3.414 7.522 5.476 7.838 C 7.138 8.486 47.641 17.125 47.641 17.125 Z"
            r8 = 1133903872(0x43960000, float:300.0)
            r5.addSvgKeyFrame(r7, r8)
            org.telegram.ui.Components.PathAnimator r5 = playPauseAnimator
            java.lang.String r7 = "M 48 7 C 50.21 7 52 8.79 52 11 C 52 19 52 19 52 19 C 52 21.21 50.21 23 48 23 L 4 23 C 1.79 23 0 21.21 0 19 L 0 11 C 0 8.79 1.79 7 4 7 C 48 7 48 7 48 7 Z"
            r8 = 1136623616(0x43bvar_, float:383.0)
            r5.addSvgKeyFrame(r7, r8)
            r5 = 2131165721(0x7var_, float:1.7945667E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgOutCheckDrawable = r5
            r5 = 2131165721(0x7var_, float:1.7945667E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgOutCheckSelectedDrawable = r5
            r5 = 2131165721(0x7var_, float:1.7945667E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgOutCheckReadDrawable = r5
            r5 = 2131165721(0x7var_, float:1.7945667E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgOutCheckReadSelectedDrawable = r5
            r5 = 2131165722(0x7var_a, float:1.794567E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgMediaCheckDrawable = r5
            r5 = 2131165722(0x7var_a, float:1.794567E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgStickerCheckDrawable = r5
            r5 = 2131165745(0x7var_, float:1.7945716E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgOutHalfCheckDrawable = r5
            r5 = 2131165745(0x7var_, float:1.7945716E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgOutHalfCheckSelectedDrawable = r5
            r5 = 2131165746(0x7var_, float:1.7945718E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgMediaHalfCheckDrawable = r5
            r5 = 2131165746(0x7var_, float:1.7945718E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgStickerHalfCheckDrawable = r5
            org.telegram.ui.Components.MsgClockDrawable r5 = new org.telegram.ui.Components.MsgClockDrawable
            r5.<init>()
            chat_msgOutClockDrawable = r5
            org.telegram.ui.Components.MsgClockDrawable r5 = new org.telegram.ui.Components.MsgClockDrawable
            r5.<init>()
            chat_msgOutSelectedClockDrawable = r5
            org.telegram.ui.Components.MsgClockDrawable r5 = new org.telegram.ui.Components.MsgClockDrawable
            r5.<init>()
            chat_msgInClockDrawable = r5
            org.telegram.ui.Components.MsgClockDrawable r5 = new org.telegram.ui.Components.MsgClockDrawable
            r5.<init>()
            chat_msgInSelectedClockDrawable = r5
            org.telegram.ui.Components.MsgClockDrawable r5 = new org.telegram.ui.Components.MsgClockDrawable
            r5.<init>()
            chat_msgMediaClockDrawable = r5
            org.telegram.ui.Components.MsgClockDrawable r5 = new org.telegram.ui.Components.MsgClockDrawable
            r5.<init>()
            chat_msgStickerClockDrawable = r5
            r5 = 2131165839(0x7var_f, float:1.7945906E38)
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
            r7 = 2131165793(0x7var_, float:1.7945813E38)
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r7)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            chat_msgInRepliesDrawable = r8
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r7)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            chat_msgInRepliesSelectedDrawable = r8
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r7)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            chat_msgOutRepliesDrawable = r8
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r7)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            chat_msgOutRepliesSelectedDrawable = r8
            r8 = 2131165785(0x7var_, float:1.7945797E38)
            android.graphics.drawable.Drawable r9 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r9 = r9.mutate()
            chat_msgInPinnedDrawable = r9
            android.graphics.drawable.Drawable r9 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r9 = r9.mutate()
            chat_msgInPinnedSelectedDrawable = r9
            android.graphics.drawable.Drawable r9 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r9 = r9.mutate()
            chat_msgOutPinnedDrawable = r9
            android.graphics.drawable.Drawable r9 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r9 = r9.mutate()
            chat_msgOutPinnedSelectedDrawable = r9
            android.graphics.drawable.Drawable r9 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r9 = r9.mutate()
            chat_msgMediaPinnedDrawable = r9
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            chat_msgStickerPinnedDrawable = r8
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            chat_msgMediaViewsDrawable = r8
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r7)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            chat_msgMediaRepliesDrawable = r8
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgStickerViewsDrawable = r5
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r7)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgStickerRepliesDrawable = r5
            r5 = 2131165701(0x7var_, float:1.7945627E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgInMenuDrawable = r5
            r5 = 2131165701(0x7var_, float:1.7945627E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgInMenuSelectedDrawable = r5
            r5 = 2131165701(0x7var_, float:1.7945627E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgOutMenuDrawable = r5
            r5 = 2131165701(0x7var_, float:1.7945627E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgOutMenuSelectedDrawable = r5
            r5 = 2131166105(0x7var_, float:1.7946446E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_msgMediaMenuDrawable = r5
            r5 = 2131165750(0x7var_, float:1.7945726E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgInInstantDrawable = r5
            r5 = 2131165750(0x7var_, float:1.7945726E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgOutInstantDrawable = r5
            r5 = 2131165850(0x7var_a, float:1.7945929E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_msgErrorDrawable = r5
            r5 = 2131165577(0x7var_, float:1.7945375E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_muteIconDrawable = r5
            r5 = 2131165498(0x7var_a, float:1.7945215E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_lockIconDrawable = r5
            r5 = 2131165303(0x7var_, float:1.794482E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgBroadcastDrawable = r5
            r5 = 2131165303(0x7var_, float:1.794482E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgBroadcastMediaDrawable = r5
            android.graphics.drawable.Drawable[] r5 = chat_msgInCallDrawable
            r7 = 2131165341(0x7var_d, float:1.7944896E38)
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r7)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            r5[r6] = r7
            android.graphics.drawable.Drawable[] r5 = chat_msgInCallSelectedDrawable
            r7 = 2131165341(0x7var_d, float:1.7944896E38)
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r7)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            r5[r6] = r7
            android.graphics.drawable.Drawable[] r5 = chat_msgOutCallDrawable
            r7 = 2131165341(0x7var_d, float:1.7944896E38)
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r7)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            r5[r6] = r7
            android.graphics.drawable.Drawable[] r5 = chat_msgOutCallSelectedDrawable
            r7 = 2131165341(0x7var_d, float:1.7944896E38)
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r7)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            r5[r6] = r7
            android.graphics.drawable.Drawable[] r5 = chat_msgInCallDrawable
            r7 = 2131165340(0x7var_c, float:1.7944894E38)
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r7)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            r5[r2] = r7
            android.graphics.drawable.Drawable[] r5 = chat_msgInCallSelectedDrawable
            r7 = 2131165340(0x7var_c, float:1.7944894E38)
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r7)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            r5[r2] = r7
            android.graphics.drawable.Drawable[] r5 = chat_msgOutCallDrawable
            r7 = 2131165340(0x7var_c, float:1.7944894E38)
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r7)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            r5[r2] = r7
            android.graphics.drawable.Drawable[] r5 = chat_msgOutCallSelectedDrawable
            r7 = 2131165340(0x7var_c, float:1.7944894E38)
            android.graphics.drawable.Drawable r7 = r4.getDrawable(r7)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            r5[r2] = r7
            r5 = 2131165339(0x7var_b, float:1.7944892E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgCallUpGreenDrawable = r5
            r5 = 2131165338(0x7var_a, float:1.794489E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgCallDownRedDrawable = r5
            r5 = 2131165338(0x7var_a, float:1.794489E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgCallDownGreenDrawable = r5
            r5 = 0
        L_0x057a:
            if (r5 >= r3) goto L_0x05bb
            android.graphics.drawable.Drawable[] r7 = chat_pollCheckDrawable
            r8 = 2131165953(0x7var_, float:1.7946138E38)
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            r7[r5] = r8
            android.graphics.drawable.Drawable[] r7 = chat_pollCrossDrawable
            r8 = 2131165954(0x7var_, float:1.794614E38)
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            r7[r5] = r8
            android.graphics.drawable.Drawable[] r7 = chat_pollHintDrawable
            r8 = 2131166039(0x7var_, float:1.7946312E38)
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            r7[r5] = r8
            android.graphics.drawable.Drawable[] r7 = chat_psaHelpDrawable
            r8 = 2131165787(0x7var_b, float:1.79458E38)
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            r7[r5] = r8
            int r5 = r5 + 1
            goto L_0x057a
        L_0x05bb:
            r5 = 2131165484(0x7var_c, float:1.7945186E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            calllog_msgCallUpRedDrawable = r5
            r5 = 2131165484(0x7var_c, float:1.7945186E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            calllog_msgCallUpGreenDrawable = r5
            r5 = 2131165487(0x7var_f, float:1.7945193E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            calllog_msgCallDownRedDrawable = r5
            r5 = 2131165487(0x7var_f, float:1.7945193E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            calllog_msgCallDownGreenDrawable = r5
            r5 = 2131165584(0x7var_, float:1.794539E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgAvatarLiveLocationDrawable = r5
            r5 = 2131165294(0x7var_e, float:1.7944801E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_inlineResultFile = r5
            r5 = 2131165298(0x7var_, float:1.794481E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_inlineResultAudio = r5
            r5 = 2131165297(0x7var_, float:1.7944807E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_inlineResultLocation = r5
            r5 = 2131165598(0x7var_e, float:1.7945418E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_redLocationIcon = r5
            r5 = 2131165296(0x7var_, float:1.7944805E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_botLinkDrawalbe = r5
            r5 = 2131165295(0x7var_f, float:1.7944803E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_botInlineDrawable = r5
            r5 = 2131165293(0x7var_d, float:1.79448E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_botCardDrawalbe = r5
            r5 = 2131165770(0x7var_a, float:1.7945766E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_commentDrawable = r5
            r5 = 2131165771(0x7var_b, float:1.7945769E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_commentStickerDrawable = r5
            r5 = 2131165709(0x7var_d, float:1.7945643E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_commentArrowDrawable = r5
            r5 = 2131165458(0x7var_, float:1.7945134E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_contextResult_shadowUnderSwitchDrawable = r5
            org.telegram.ui.Components.RLottieDrawable[] r5 = chat_attachButtonDrawables
            org.telegram.ui.Components.RLottieDrawable r7 = new org.telegram.ui.Components.RLottieDrawable
            r8 = 2131558402(0x7f0d0002, float:1.8742119E38)
            java.lang.String r9 = "attach_gallery"
            r10 = 1104150528(0x41d00000, float:26.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r7.<init>(r8, r9, r11, r12)
            r5[r6] = r7
            org.telegram.ui.Components.RLottieDrawable[] r5 = chat_attachButtonDrawables
            org.telegram.ui.Components.RLottieDrawable r7 = new org.telegram.ui.Components.RLottieDrawable
            r8 = 2131558404(0x7f0d0004, float:1.8742123E38)
            java.lang.String r9 = "attach_music"
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r7.<init>(r8, r9, r11, r12)
            r5[r2] = r7
            org.telegram.ui.Components.RLottieDrawable[] r5 = chat_attachButtonDrawables
            org.telegram.ui.Components.RLottieDrawable r7 = new org.telegram.ui.Components.RLottieDrawable
            r8 = 2131558401(0x7f0d0001, float:1.8742117E38)
            java.lang.String r9 = "attach_file"
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r7.<init>(r8, r9, r11, r12)
            r5[r3] = r7
            org.telegram.ui.Components.RLottieDrawable[] r5 = chat_attachButtonDrawables
            org.telegram.ui.Components.RLottieDrawable r7 = new org.telegram.ui.Components.RLottieDrawable
            r8 = 2131558400(0x7f0d0000, float:1.8742115E38)
            java.lang.String r9 = "attach_contact"
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r7.<init>(r8, r9, r11, r12)
            r8 = 3
            r5[r8] = r7
            org.telegram.ui.Components.RLottieDrawable[] r5 = chat_attachButtonDrawables
            org.telegram.ui.Components.RLottieDrawable r7 = new org.telegram.ui.Components.RLottieDrawable
            r9 = 2131558403(0x7f0d0003, float:1.874212E38)
            java.lang.String r11 = "attach_location"
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r7.<init>(r9, r11, r12, r13)
            r9 = 4
            r5[r9] = r7
            org.telegram.ui.Components.RLottieDrawable[] r5 = chat_attachButtonDrawables
            org.telegram.ui.Components.RLottieDrawable r7 = new org.telegram.ui.Components.RLottieDrawable
            r11 = 2131558405(0x7f0d0005, float:1.8742125E38)
            java.lang.String r12 = "attach_poll"
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r7.<init>(r11, r12, r13, r10)
            r10 = 5
            r5[r10] = r7
            r5 = 2131165863(0x7var_a7, float:1.7945955E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_attachEmptyDrawable = r5
            r5 = 2131166020(0x7var_, float:1.7946274E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_shareIconDrawable = r5
            r5 = 2131165393(0x7var_d1, float:1.7945002E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_replyIconDrawable = r5
            r5 = 2131165693(0x7var_fd, float:1.794561E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_goIconDrawable = r5
            org.telegram.ui.Components.CombinedDrawable[][] r5 = chat_fileMiniStatesDrawable
            r5 = r5[r6]
            r7 = 1102053376(0x41b00000, float:22.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r12 = 2131165271(0x7var_, float:1.7944754E38)
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
            r12 = 2131165272(0x7var_, float:1.7944756E38)
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
            r12 = 2131165271(0x7var_, float:1.7944754E38)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r12)
            r5[r6] = r11
            org.telegram.ui.Components.CombinedDrawable[][] r5 = chat_fileMiniStatesDrawable
            r5 = r5[r3]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r12)
            r5[r2] = r11
            org.telegram.ui.Components.CombinedDrawable[][] r5 = chat_fileMiniStatesDrawable
            r5 = r5[r8]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r12 = 2131165272(0x7var_, float:1.7944756E38)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r12)
            r5[r6] = r11
            org.telegram.ui.Components.CombinedDrawable[][] r5 = chat_fileMiniStatesDrawable
            r5 = r5[r8]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r12)
            r5[r2] = r11
            org.telegram.ui.Components.CombinedDrawable[][] r5 = chat_fileMiniStatesDrawable
            r5 = r5[r9]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r12 = 2131166109(0x7var_d, float:1.7946454E38)
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
            r12 = 2131166110(0x7var_e, float:1.7946456E38)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r12)
            r5[r6] = r11
            org.telegram.ui.Components.CombinedDrawable[][] r5 = chat_fileMiniStatesDrawable
            r5 = r5[r10]
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r11 = 2131166110(0x7var_e, float:1.7946456E38)
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
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r13 = 1092616192(0x41200000, float:10.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            r11.lineTo(r12, r13)
            android.graphics.Path[] r11 = chat_filePath
            r11 = r11[r6]
            r12 = 1101529088(0x41a80000, float:21.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r13 = 1101004800(0x41a00000, float:20.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            r11.lineTo(r12, r13)
            r11 = 1101529088(0x41a80000, float:21.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r12 = r5 * 2
            int r11 = r11 - r12
            float r11 = (float) r11
            r13 = 1100480512(0x41980000, float:19.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r13 = r13 - r5
            float r13 = (float) r13
            r14 = 1101529088(0x41a80000, float:21.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            r15 = 1100480512(0x41980000, float:19.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r15 = r15 + r5
            float r15 = (float) r15
            r7.set(r11, r13, r14, r15)
            android.graphics.Path[] r11 = chat_filePath
            r11 = r11[r6]
            r13 = 0
            r14 = 1119092736(0x42b40000, float:90.0)
            r11.arcTo(r7, r13, r14, r6)
            android.graphics.Path[] r11 = chat_filePath
            r11 = r11[r6]
            r13 = 1086324736(0x40CLASSNAME, float:6.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            r14 = 1101529088(0x41a80000, float:21.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            r11.lineTo(r13, r14)
            r11 = 1084227584(0x40a00000, float:5.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r13 = (float) r13
            r14 = 1100480512(0x41980000, float:19.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r14 = r14 - r5
            float r14 = (float) r14
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r15 = r15 + r12
            float r15 = (float) r15
            r16 = 1100480512(0x41980000, float:19.0)
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r5 = r16 + r5
            float r5 = (float) r5
            r7.set(r13, r14, r15, r5)
            android.graphics.Path[] r5 = chat_filePath
            r5 = r5[r6]
            r13 = 1119092736(0x42b40000, float:90.0)
            r14 = 1119092736(0x42b40000, float:90.0)
            r5.arcTo(r7, r13, r14, r6)
            android.graphics.Path[] r5 = chat_filePath
            r5 = r5[r6]
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r13 = (float) r13
            r14 = 1082130432(0x40800000, float:4.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            r5.lineTo(r13, r14)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r5 = (float) r5
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r0)
            float r13 = (float) r13
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r14 = r14 + r12
            float r14 = (float) r14
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r15 = r15 + r12
            float r12 = (float) r15
            r7.set(r5, r13, r14, r12)
            android.graphics.Path[] r5 = chat_filePath
            r5 = r5[r6]
            r12 = 1127481344(0x43340000, float:180.0)
            r13 = 1119092736(0x42b40000, float:90.0)
            r5.arcTo(r7, r12, r13, r6)
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
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            r5.moveTo(r7, r11)
            android.graphics.Path[] r5 = chat_filePath
            r5 = r5[r2]
            r7 = 1100480512(0x41980000, float:19.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            r11 = 1092616192(0x41200000, float:10.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            r5.lineTo(r7, r11)
            android.graphics.Path[] r5 = chat_filePath
            r5 = r5[r2]
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r7 = (float) r7
            r11 = 1092616192(0x41200000, float:10.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            r5.lineTo(r7, r11)
            android.graphics.Path[] r5 = chat_filePath
            r5 = r5[r2]
            r5.close()
            r5 = 2131165306(0x7var_a, float:1.7944825E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_flameIcon = r5
            r5 = 2131165805(0x7var_d, float:1.7945837E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_gifIcon = r5
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r6]
            r7 = 1110441984(0x42300000, float:44.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r12 = 2131165808(0x7var_, float:1.7945844E38)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r12)
            r5[r6] = r11
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r6]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r12)
            r5[r2] = r11
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r2]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r13 = 2131165807(0x7var_f, float:1.7945842E38)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r13)
            r5[r6] = r11
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r2]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r13)
            r5[r2] = r11
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r3]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r13 = 2131165806(0x7var_e, float:1.794584E38)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r13)
            r5[r6] = r11
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r3]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r13)
            r5[r2] = r11
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r8]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r14 = 2131165804(0x7var_c, float:1.7945835E38)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r14)
            r5[r6] = r11
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r8]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r14)
            r5[r2] = r11
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r9]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r14 = 2131165803(0x7var_b, float:1.7945833E38)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r14)
            r5[r6] = r11
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r9]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r14)
            r5[r2] = r11
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r10]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r12)
            r5[r6] = r11
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r10]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r12)
            r5[r2] = r11
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r11 = 6
            r5 = r5[r11]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r15 = 2131165807(0x7var_f, float:1.7945842E38)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r15)
            r5[r6] = r11
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r11 = 6
            r5 = r5[r11]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r15)
            r5[r2] = r11
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r11 = 7
            r5 = r5[r11]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r13)
            r5[r6] = r11
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r11 = 7
            r5 = r5[r11]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r13)
            r5[r2] = r11
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r11 = 8
            r5 = r5[r11]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r15 = 2131165804(0x7var_c, float:1.7945835E38)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r15)
            r5[r6] = r11
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r11 = 8
            r5 = r5[r11]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r15)
            r5[r2] = r11
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r11 = 9
            r5 = r5[r11]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r14)
            r5[r6] = r11
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r11 = 9
            r5 = r5[r11]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            org.telegram.ui.Components.CombinedDrawable r11 = createCircleDrawableWithIcon(r11, r14)
            r5[r2] = r11
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r5 = r5[r6]
            r11 = 1111490560(0x42400000, float:48.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r11)
            org.telegram.ui.Components.CombinedDrawable r15 = createCircleDrawableWithIcon(r15, r13)
            r5[r6] = r15
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r5 = r5[r6]
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r11)
            org.telegram.ui.Components.CombinedDrawable r15 = createCircleDrawableWithIcon(r15, r13)
            r5[r2] = r15
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r5 = r5[r2]
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r11)
            org.telegram.ui.Components.CombinedDrawable r15 = createCircleDrawableWithIcon(r15, r14)
            r5[r6] = r15
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r5 = r5[r2]
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r11)
            org.telegram.ui.Components.CombinedDrawable r15 = createCircleDrawableWithIcon(r15, r14)
            r5[r2] = r15
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r5 = r5[r3]
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r0 = 2131165805(0x7var_d, float:1.7945837E38)
            org.telegram.ui.Components.CombinedDrawable r0 = createCircleDrawableWithIcon(r15, r0)
            r5[r6] = r0
            android.graphics.drawable.Drawable[][] r0 = chat_photoStatesDrawables
            r0 = r0[r3]
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r15 = 2131165805(0x7var_d, float:1.7945837E38)
            org.telegram.ui.Components.CombinedDrawable r5 = createCircleDrawableWithIcon(r5, r15)
            r0[r2] = r5
            android.graphics.drawable.Drawable[][] r0 = chat_photoStatesDrawables
            r0 = r0[r8]
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            org.telegram.ui.Components.CombinedDrawable r5 = createCircleDrawableWithIcon(r5, r12)
            r0[r6] = r5
            android.graphics.drawable.Drawable[][] r0 = chat_photoStatesDrawables
            r0 = r0[r8]
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            org.telegram.ui.Components.CombinedDrawable r5 = createCircleDrawableWithIcon(r5, r12)
            r0[r2] = r5
            android.graphics.drawable.Drawable[][] r0 = chat_photoStatesDrawables
            r5 = r0[r9]
            r0 = r0[r9]
            r8 = 2131165306(0x7var_a, float:1.7944825E38)
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r8)
            r0[r2] = r8
            r5[r6] = r8
            android.graphics.drawable.Drawable[][] r0 = chat_photoStatesDrawables
            r5 = r0[r10]
            r0 = r0[r10]
            r8 = 2131165359(0x7var_af, float:1.7944933E38)
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r8)
            r0[r2] = r8
            r5[r6] = r8
            android.graphics.drawable.Drawable[][] r0 = chat_photoStatesDrawables
            r5 = 6
            r5 = r0[r5]
            r8 = 6
            r0 = r0[r8]
            r8 = 2131165924(0x7var_e4, float:1.7946079E38)
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r8)
            r0[r2] = r8
            r5[r6] = r8
            android.graphics.drawable.Drawable[][] r0 = chat_photoStatesDrawables
            r5 = 7
            r0 = r0[r5]
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            org.telegram.ui.Components.CombinedDrawable r5 = createCircleDrawableWithIcon(r5, r13)
            r0[r6] = r5
            android.graphics.drawable.Drawable[][] r0 = chat_photoStatesDrawables
            r5 = 7
            r0 = r0[r5]
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            org.telegram.ui.Components.CombinedDrawable r5 = createCircleDrawableWithIcon(r5, r13)
            r0[r2] = r5
            android.graphics.drawable.Drawable[][] r0 = chat_photoStatesDrawables
            r5 = 8
            r0 = r0[r5]
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            org.telegram.ui.Components.CombinedDrawable r5 = createCircleDrawableWithIcon(r5, r14)
            r0[r6] = r5
            android.graphics.drawable.Drawable[][] r0 = chat_photoStatesDrawables
            r5 = 8
            r0 = r0[r5]
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            org.telegram.ui.Components.CombinedDrawable r5 = createCircleDrawableWithIcon(r5, r14)
            r0[r2] = r5
            android.graphics.drawable.Drawable[][] r0 = chat_photoStatesDrawables
            r5 = 9
            r0 = r0[r5]
            r5 = 2131165389(0x7var_cd, float:1.7944994E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            r0[r6] = r5
            android.graphics.drawable.Drawable[][] r0 = chat_photoStatesDrawables
            r5 = 9
            r0 = r0[r5]
            r5 = 2131165389(0x7var_cd, float:1.7944994E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            r0[r2] = r5
            android.graphics.drawable.Drawable[][] r0 = chat_photoStatesDrawables
            r5 = 10
            r0 = r0[r5]
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            org.telegram.ui.Components.CombinedDrawable r5 = createCircleDrawableWithIcon(r5, r13)
            r0[r6] = r5
            android.graphics.drawable.Drawable[][] r0 = chat_photoStatesDrawables
            r5 = 10
            r0 = r0[r5]
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            org.telegram.ui.Components.CombinedDrawable r5 = createCircleDrawableWithIcon(r5, r13)
            r0[r2] = r5
            android.graphics.drawable.Drawable[][] r0 = chat_photoStatesDrawables
            r5 = 11
            r0 = r0[r5]
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            org.telegram.ui.Components.CombinedDrawable r5 = createCircleDrawableWithIcon(r5, r14)
            r0[r6] = r5
            android.graphics.drawable.Drawable[][] r0 = chat_photoStatesDrawables
            r5 = 11
            r0 = r0[r5]
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            org.telegram.ui.Components.CombinedDrawable r5 = createCircleDrawableWithIcon(r5, r14)
            r0[r2] = r5
            android.graphics.drawable.Drawable[][] r0 = chat_photoStatesDrawables
            r5 = 12
            r0 = r0[r5]
            r5 = 2131165389(0x7var_cd, float:1.7944994E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            r0[r6] = r5
            android.graphics.drawable.Drawable[][] r0 = chat_photoStatesDrawables
            r5 = 12
            r0 = r0[r5]
            r5 = 2131165389(0x7var_cd, float:1.7944994E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            r0[r2] = r5
            android.graphics.drawable.Drawable[] r0 = chat_contactDrawable
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r8 = 2131165725(0x7var_d, float:1.7945675E38)
            org.telegram.ui.Components.CombinedDrawable r5 = createCircleDrawableWithIcon(r5, r8)
            r0[r6] = r5
            android.graphics.drawable.Drawable[] r0 = chat_contactDrawable
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r7 = 2131165725(0x7var_d, float:1.7945675E38)
            org.telegram.ui.Components.CombinedDrawable r5 = createCircleDrawableWithIcon(r5, r7)
            r0[r2] = r5
            android.graphics.drawable.Drawable[] r0 = chat_locationDrawable
            r5 = 2131165758(0x7var_e, float:1.7945742E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            r0[r6] = r5
            android.graphics.drawable.Drawable[] r0 = chat_locationDrawable
            r5 = 2131165758(0x7var_e, float:1.7945742E38)
            android.graphics.drawable.Drawable r4 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r4 = r4.mutate()
            r0[r2] = r4
            android.content.res.Resources r0 = r17.getResources()
            r4 = 2131165382(0x7var_c6, float:1.794498E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r4)
            chat_composeShadowDrawable = r0
            int r0 = org.telegram.messenger.AndroidUtilities.roundMessageSize     // Catch:{ all -> 0x0caa }
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ all -> 0x0caa }
            int r0 = r0 + r4
            android.graphics.Bitmap$Config r4 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0caa }
            android.graphics.Bitmap r4 = android.graphics.Bitmap.createBitmap(r0, r0, r4)     // Catch:{ all -> 0x0caa }
            android.graphics.Canvas r5 = new android.graphics.Canvas     // Catch:{ all -> 0x0caa }
            r5.<init>(r4)     // Catch:{ all -> 0x0caa }
            android.graphics.Paint r7 = new android.graphics.Paint     // Catch:{ all -> 0x0caa }
            r7.<init>(r2)     // Catch:{ all -> 0x0caa }
            r7.setColor(r6)     // Catch:{ all -> 0x0caa }
            android.graphics.Paint$Style r8 = android.graphics.Paint.Style.FILL     // Catch:{ all -> 0x0caa }
            r7.setStyle(r8)     // Catch:{ all -> 0x0caa }
            android.graphics.PorterDuffXfermode r8 = new android.graphics.PorterDuffXfermode     // Catch:{ all -> 0x0caa }
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.CLEAR     // Catch:{ all -> 0x0caa }
            r8.<init>(r9)     // Catch:{ all -> 0x0caa }
            r7.setXfermode(r8)     // Catch:{ all -> 0x0caa }
            android.graphics.Paint r8 = new android.graphics.Paint     // Catch:{ all -> 0x0caa }
            r8.<init>(r2)     // Catch:{ all -> 0x0caa }
            r2 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ all -> 0x0caa }
            float r2 = (float) r2     // Catch:{ all -> 0x0caa }
            r9 = 0
            r10 = 0
            r11 = 1593835520(0x5var_, float:9.223372E18)
            r8.setShadowLayer(r2, r9, r10, r11)     // Catch:{ all -> 0x0caa }
        L_0x0CLASSNAME:
            if (r6 >= r3) goto L_0x0c9f
            int r2 = r0 / 2
            float r2 = (float) r2     // Catch:{ all -> 0x0caa }
            int r9 = r0 / 2
            float r9 = (float) r9     // Catch:{ all -> 0x0caa }
            int r10 = org.telegram.messenger.AndroidUtilities.roundMessageSize     // Catch:{ all -> 0x0caa }
            int r10 = r10 / r3
            r11 = 1065353216(0x3var_, float:1.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)     // Catch:{ all -> 0x0caa }
            int r10 = r10 - r11
            float r10 = (float) r10     // Catch:{ all -> 0x0caa }
            if (r6 != 0) goto L_0x0CLASSNAME
            r11 = r8
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r11 = r7
        L_0x0CLASSNAME:
            r5.drawCircle(r2, r9, r10, r11)     // Catch:{ all -> 0x0caa }
            int r6 = r6 + 1
            goto L_0x0CLASSNAME
        L_0x0c9f:
            r0 = 0
            r5.setBitmap(r0)     // Catch:{ Exception -> 0x0ca3 }
        L_0x0ca3:
            android.graphics.drawable.BitmapDrawable r0 = new android.graphics.drawable.BitmapDrawable     // Catch:{ all -> 0x0caa }
            r0.<init>(r4)     // Catch:{ all -> 0x0caa }
            chat_roundVideoShadow = r0     // Catch:{ all -> 0x0caa }
        L_0x0caa:
            applyChatTheme(r18)
        L_0x0cad:
            android.text.TextPaint r0 = chat_msgTextPaintOneEmoji
            r2 = 1105199104(0x41e00000, float:28.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r0.setTextSize(r2)
            android.text.TextPaint r0 = chat_msgTextPaintTwoEmoji
            r2 = 1103101952(0x41CLASSNAME, float:24.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r0.setTextSize(r2)
            android.text.TextPaint r0 = chat_msgTextPaintThreeEmoji
            r2 = 1101004800(0x41a00000, float:20.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r0.setTextSize(r2)
            android.text.TextPaint r0 = chat_msgTextPaint
            int r2 = org.telegram.messenger.SharedConfig.fontSize
            float r2 = (float) r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r0.setTextSize(r2)
            android.text.TextPaint r0 = chat_msgGameTextPaint
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r2 = (float) r2
            r0.setTextSize(r2)
            android.text.TextPaint r0 = chat_msgBotButtonPaint
            r2 = 1097859072(0x41700000, float:15.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r4 = (float) r4
            r0.setTextSize(r4)
            if (r18 != 0) goto L_0x0e4d
            android.graphics.Paint r0 = chat_botProgressPaint
            if (r0 == 0) goto L_0x0e4d
            r4 = 1073741824(0x40000000, float:2.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r0.setStrokeWidth(r4)
            android.text.TextPaint r0 = chat_infoPaint
            r4 = 1094713344(0x41400000, float:12.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r5 = (float) r5
            r0.setTextSize(r5)
            android.text.TextPaint r0 = chat_stickerCommentCountPaint
            r5 = 1093664768(0x41300000, float:11.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r0.setTextSize(r5)
            android.text.TextPaint r0 = chat_docNamePaint
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r5 = (float) r5
            r0.setTextSize(r5)
            android.text.TextPaint r0 = chat_locationTitlePaint
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r5 = (float) r5
            r0.setTextSize(r5)
            android.text.TextPaint r0 = chat_locationAddressPaint
            r5 = 1095761920(0x41500000, float:13.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r6 = (float) r6
            r0.setTextSize(r6)
            android.text.TextPaint r0 = chat_audioTimePaint
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r6 = (float) r6
            r0.setTextSize(r6)
            android.text.TextPaint r0 = chat_livePaint
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r6 = (float) r6
            r0.setTextSize(r6)
            android.text.TextPaint r0 = chat_audioTitlePaint
            r6 = 1098907648(0x41800000, float:16.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            r0.setTextSize(r6)
            android.text.TextPaint r0 = chat_audioPerformerPaint
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r6 = (float) r6
            r0.setTextSize(r6)
            android.text.TextPaint r0 = chat_botButtonPaint
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r6 = (float) r6
            r0.setTextSize(r6)
            android.text.TextPaint r0 = chat_contactNamePaint
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r6 = (float) r6
            r0.setTextSize(r6)
            android.text.TextPaint r0 = chat_contactPhonePaint
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r6 = (float) r6
            r0.setTextSize(r6)
            android.text.TextPaint r0 = chat_durationPaint
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r6 = (float) r6
            r0.setTextSize(r6)
            android.text.TextPaint r0 = chat_timePaint
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r0.setTextSize(r4)
            android.text.TextPaint r0 = chat_adminPaint
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r4 = (float) r4
            r0.setTextSize(r4)
            android.text.TextPaint r0 = chat_namePaint
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r4 = (float) r4
            r0.setTextSize(r4)
            android.text.TextPaint r0 = chat_forwardNamePaint
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r4 = (float) r4
            r0.setTextSize(r4)
            android.text.TextPaint r0 = chat_replyNamePaint
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r4 = (float) r4
            r0.setTextSize(r4)
            android.text.TextPaint r0 = chat_replyTextPaint
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r0.setTextSize(r1)
            android.text.TextPaint r0 = chat_gamePaint
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r1 = (float) r1
            r0.setTextSize(r1)
            android.text.TextPaint r0 = chat_shipmentPaint
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r1 = (float) r1
            r0.setTextSize(r1)
            android.text.TextPaint r0 = chat_instantViewPaint
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r1 = (float) r1
            r0.setTextSize(r1)
            android.graphics.Paint r0 = chat_instantViewRectPaint
            r1 = 1065353216(0x3var_, float:1.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r0.setStrokeWidth(r1)
            android.graphics.Paint r0 = chat_pollTimerPaint
            r1 = 1066192077(0x3f8ccccd, float:1.1)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r0.setStrokeWidth(r1)
            android.graphics.Paint r0 = chat_statusRecordPaint
            r1 = 1073741824(0x40000000, float:2.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r0.setStrokeWidth(r1)
            android.text.TextPaint r0 = chat_actionTextPaint
            r1 = 16
            int r4 = org.telegram.messenger.SharedConfig.fontSize
            int r1 = java.lang.Math.max(r1, r4)
            int r1 = r1 - r3
            float r1 = (float) r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r0.setTextSize(r1)
            android.text.TextPaint r0 = chat_contextResult_titleTextPaint
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r1 = (float) r1
            r0.setTextSize(r1)
            android.text.TextPaint r0 = chat_contextResult_descriptionTextPaint
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r1 = (float) r1
            r0.setTextSize(r1)
            android.graphics.Paint r0 = chat_radialProgressPaint
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r0.setStrokeWidth(r1)
            android.graphics.Paint r0 = chat_radialProgress2Paint
            r1 = 1073741824(0x40000000, float:2.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r0.setStrokeWidth(r1)
        L_0x0e4d:
            return
        L_0x0e4e:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0e4e }
            goto L_0x0e52
        L_0x0e51:
            throw r0
        L_0x0e52:
            goto L_0x0e51
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.createChatResources(android.content.Context, boolean):void");
    }

    public static void refreshAttachButtonsColors() {
        int i = 0;
        while (true) {
            RLottieDrawable[] rLottieDrawableArr = chat_attachButtonDrawables;
            if (i < rLottieDrawableArr.length) {
                if (rLottieDrawableArr[i] != null) {
                    rLottieDrawableArr[i].beginApplyLayerColors();
                    if (i == 0) {
                        chat_attachButtonDrawables[i].setLayerColor("Color_Mount.**", getNonAnimatedColor("chat_attachGalleryBackground"));
                        chat_attachButtonDrawables[i].setLayerColor("Color_PhotoShadow.**", getNonAnimatedColor("chat_attachGalleryBackground"));
                        chat_attachButtonDrawables[i].setLayerColor("White_Photo.**", getNonAnimatedColor("chat_attachGalleryIcon"));
                        chat_attachButtonDrawables[i].setLayerColor("White_BackPhoto.**", getNonAnimatedColor("chat_attachGalleryIcon"));
                    } else if (i == 1) {
                        chat_attachButtonDrawables[i].setLayerColor("White_Play1.**", getNonAnimatedColor("chat_attachAudioIcon"));
                        chat_attachButtonDrawables[i].setLayerColor("White_Play2.**", getNonAnimatedColor("chat_attachAudioIcon"));
                    } else if (i == 2) {
                        chat_attachButtonDrawables[i].setLayerColor("Color_Corner.**", getNonAnimatedColor("chat_attachFileBackground"));
                        chat_attachButtonDrawables[i].setLayerColor("White_List.**", getNonAnimatedColor("chat_attachFileIcon"));
                    } else if (i == 3) {
                        chat_attachButtonDrawables[i].setLayerColor("White_User1.**", getNonAnimatedColor("chat_attachContactIcon"));
                        chat_attachButtonDrawables[i].setLayerColor("White_User2.**", getNonAnimatedColor("chat_attachContactIcon"));
                    } else if (i == 4) {
                        chat_attachButtonDrawables[i].setLayerColor("Color_Oval.**", getNonAnimatedColor("chat_attachLocationBackground"));
                        chat_attachButtonDrawables[i].setLayerColor("White_Pin.**", getNonAnimatedColor("chat_attachLocationIcon"));
                    } else if (i == 5) {
                        chat_attachButtonDrawables[i].setLayerColor("White_Column 1.**", getNonAnimatedColor("chat_attachPollIcon"));
                        chat_attachButtonDrawables[i].setLayerColor("White_Column 2.**", getNonAnimatedColor("chat_attachPollIcon"));
                        chat_attachButtonDrawables[i].setLayerColor("White_Column 3.**", getNonAnimatedColor("chat_attachPollIcon"));
                    }
                    chat_attachButtonDrawables[i].commitApplyLayerColors();
                }
                i++;
            } else {
                return;
            }
        }
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
            setDrawableColorByKey(chat_msgStickerRepliesDrawable, "chat_serviceText");
            setDrawableColorByKey(chat_shareIconDrawable, "chat_serviceIcon");
            setDrawableColorByKey(chat_replyIconDrawable, "chat_serviceIcon");
            setDrawableColorByKey(chat_goIconDrawable, "chat_serviceIcon");
            setDrawableColorByKey(chat_botInlineDrawable, "chat_serviceIcon");
            setDrawableColorByKey(chat_botLinkDrawalbe, "chat_serviceIcon");
            setDrawableColorByKey(chat_msgInViewsDrawable, "chat_inViews");
            setDrawableColorByKey(chat_msgInViewsSelectedDrawable, "chat_inViewsSelected");
            setDrawableColorByKey(chat_msgOutViewsDrawable, "chat_outViews");
            setDrawableColorByKey(chat_msgOutViewsSelectedDrawable, "chat_outViewsSelected");
            setDrawableColorByKey(chat_msgInRepliesDrawable, "chat_inViews");
            setDrawableColorByKey(chat_msgInRepliesSelectedDrawable, "chat_inViewsSelected");
            setDrawableColorByKey(chat_msgOutRepliesDrawable, "chat_outViews");
            setDrawableColorByKey(chat_msgOutRepliesSelectedDrawable, "chat_outViewsSelected");
            setDrawableColorByKey(chat_msgInPinnedDrawable, "chat_inViews");
            setDrawableColorByKey(chat_msgInPinnedSelectedDrawable, "chat_inViewsSelected");
            setDrawableColorByKey(chat_msgOutPinnedDrawable, "chat_outViews");
            setDrawableColorByKey(chat_msgOutPinnedSelectedDrawable, "chat_outViewsSelected");
            setDrawableColorByKey(chat_msgMediaPinnedDrawable, "chat_mediaViews");
            setDrawableColorByKey(chat_msgStickerPinnedDrawable, "chat_serviceText");
            setDrawableColorByKey(chat_msgMediaViewsDrawable, "chat_mediaViews");
            setDrawableColorByKey(chat_msgMediaRepliesDrawable, "chat_mediaViews");
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
            setDrawableColorByKey(chat_commentDrawable, "chat_inInstant");
            setDrawableColorByKey(chat_commentStickerDrawable, "chat_serviceIcon");
            setDrawableColorByKey(chat_commentArrowDrawable, "chat_inInstant");
            for (int i = 0; i < 2; i++) {
                setDrawableColorByKey(chat_msgInCallDrawable[i], "chat_inInstant");
                setDrawableColorByKey(chat_msgInCallSelectedDrawable[i], "chat_inInstantSelected");
                setDrawableColorByKey(chat_msgOutCallDrawable[i], "chat_outInstant");
                setDrawableColorByKey(chat_msgOutCallSelectedDrawable[i], "chat_outInstantSelected");
            }
            setDrawableColorByKey(chat_msgCallUpGreenDrawable, "chat_outUpCall");
            setDrawableColorByKey(chat_msgCallDownRedDrawable, "chat_inUpCall");
            setDrawableColorByKey(chat_msgCallDownGreenDrawable, "chat_inDownCall");
            setDrawableColorByKey(calllog_msgCallUpRedDrawable, "calls_callReceivedRedIcon");
            setDrawableColorByKey(calllog_msgCallUpGreenDrawable, "calls_callReceivedGreenIcon");
            setDrawableColorByKey(calllog_msgCallDownRedDrawable, "calls_callReceivedRedIcon");
            setDrawableColorByKey(calllog_msgCallDownGreenDrawable, "calls_callReceivedGreenIcon");
            int i2 = 0;
            while (true) {
                StatusDrawable[] statusDrawableArr = chat_status_drawables;
                if (i2 >= statusDrawableArr.length) {
                    break;
                }
                setDrawableColorByKey(statusDrawableArr[i2], "chats_actionMessage");
                i2++;
            }
            for (int i3 = 0; i3 < 2; i3++) {
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i3][0], getColor("chat_outLoader"), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i3][0], getColor("chat_outMediaIcon"), true);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i3][1], getColor("chat_outLoaderSelected"), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i3][1], getColor("chat_outMediaIconSelected"), true);
                int i4 = i3 + 2;
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i4][0], getColor("chat_inLoader"), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i4][0], getColor("chat_inMediaIcon"), true);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i4][1], getColor("chat_inLoaderSelected"), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i4][1], getColor("chat_inMediaIconSelected"), true);
                int i5 = i3 + 4;
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i5][0], getColor("chat_mediaLoaderPhoto"), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i5][0], getColor("chat_mediaLoaderPhotoIcon"), true);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i5][1], getColor("chat_mediaLoaderPhotoSelected"), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i5][1], getColor("chat_mediaLoaderPhotoIconSelected"), true);
            }
            for (int i6 = 0; i6 < 5; i6++) {
                setCombinedDrawableColor(chat_fileStatesDrawable[i6][0], getColor("chat_outLoader"), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[i6][0], getColor("chat_outMediaIcon"), true);
                setCombinedDrawableColor(chat_fileStatesDrawable[i6][1], getColor("chat_outLoaderSelected"), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[i6][1], getColor("chat_outMediaIconSelected"), true);
                int i7 = i6 + 5;
                setCombinedDrawableColor(chat_fileStatesDrawable[i7][0], getColor("chat_inLoader"), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[i7][0], getColor("chat_inMediaIcon"), true);
                setCombinedDrawableColor(chat_fileStatesDrawable[i7][1], getColor("chat_inLoaderSelected"), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[i7][1], getColor("chat_inMediaIconSelected"), true);
            }
            for (int i8 = 0; i8 < 4; i8++) {
                setCombinedDrawableColor(chat_photoStatesDrawables[i8][0], getColor("chat_mediaLoaderPhoto"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i8][0], getColor("chat_mediaLoaderPhotoIcon"), true);
                setCombinedDrawableColor(chat_photoStatesDrawables[i8][1], getColor("chat_mediaLoaderPhotoSelected"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i8][1], getColor("chat_mediaLoaderPhotoIconSelected"), true);
            }
            for (int i9 = 0; i9 < 2; i9++) {
                int i10 = i9 + 7;
                setCombinedDrawableColor(chat_photoStatesDrawables[i10][0], getColor("chat_outLoaderPhoto"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i10][0], getColor("chat_outLoaderPhotoIcon"), true);
                setCombinedDrawableColor(chat_photoStatesDrawables[i10][1], getColor("chat_outLoaderPhotoSelected"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i10][1], getColor("chat_outLoaderPhotoIconSelected"), true);
                int i11 = i9 + 10;
                setCombinedDrawableColor(chat_photoStatesDrawables[i11][0], getColor("chat_inLoaderPhoto"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i11][0], getColor("chat_inLoaderPhotoIcon"), true);
                setCombinedDrawableColor(chat_photoStatesDrawables[i11][1], getColor("chat_inLoaderPhotoSelected"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i11][1], getColor("chat_inLoaderPhotoIconSelected"), true);
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
            setDrawableColor(chat_pollHintDrawable[0], getColor("chat_inPreviewInstantText"));
            setDrawableColor(chat_pollHintDrawable[1], getColor("chat_outPreviewInstantText"));
            setDrawableColor(chat_psaHelpDrawable[0], getColor("chat_inViews"));
            setDrawableColor(chat_psaHelpDrawable[1], getColor("chat_outViews"));
            setDrawableColorByKey(chat_composeShadowDrawable, "chat_messagePanelShadow");
            int color = getColor("chat_outAudioSeekbarFill") == -1 ? getColor("chat_outBubble") : -1;
            setDrawableColor(chat_pollCheckDrawable[1], color);
            setDrawableColor(chat_pollCrossDrawable[1], color);
            setDrawableColor(chat_attachEmptyDrawable, getColor("chat_attachEmptyImage"));
            applyChatServiceMessageColor();
            refreshAttachButtonsColors();
        }
    }

    public static void applyChatServiceMessageColor() {
        applyChatServiceMessageColor((int[]) null, (Drawable) null);
    }

    public static boolean hasGradientService() {
        return serviceBitmapShader != null;
    }

    public static void applyServiceShaderMatrixForView(View view, View view2) {
        if (view != null && view2 != null) {
            view.getLocationOnScreen(viewPos);
            int[] iArr = viewPos;
            int i = iArr[0];
            int i2 = iArr[1];
            view2.getLocationOnScreen(iArr);
            applyServiceShaderMatrix(view2.getMeasuredWidth(), view2.getMeasuredHeight(), (float) i, (float) (i2 - viewPos[1]));
        }
    }

    public static void applyServiceShaderMatrix(int i, int i2, float f, float f2) {
        if (serviceBitmapShader != null) {
            float width = (float) serviceBitmap.getWidth();
            float height = (float) serviceBitmap.getHeight();
            float f3 = (float) i;
            float f4 = (float) i2;
            float max = Math.max(f3 / width, f4 / height);
            serviceBitmapMatrix.reset();
            serviceBitmapMatrix.setTranslate(((f3 - (width * max)) / 2.0f) - f, ((f4 - (height * max)) / 2.0f) - f2);
            serviceBitmapMatrix.preScale(max, max);
            serviceBitmapShader.setLocalMatrix(serviceBitmapMatrix);
        }
    }

    public static void applyChatServiceMessageColor(int[] iArr, Drawable drawable) {
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
            if (drawable == null) {
                drawable = wallpaper;
            }
            if (drawable instanceof MotionBackgroundDrawable) {
                Bitmap bitmap = ((MotionBackgroundDrawable) drawable).getBitmap();
                if (serviceBitmap != bitmap) {
                    serviceBitmap = bitmap;
                    Bitmap bitmap2 = serviceBitmap;
                    Shader.TileMode tileMode = Shader.TileMode.CLAMP;
                    serviceBitmapShader = new BitmapShader(bitmap2, tileMode, tileMode);
                    if (serviceBitmapMatrix == null) {
                        serviceBitmapMatrix = new Matrix();
                    }
                }
            } else {
                serviceBitmap = null;
                serviceBitmapShader = null;
            }
            chat_actionBackgroundPaint.setColor(num2.intValue());
            chat_actionBackgroundSelectedPaint.setColor(num.intValue());
            chat_actionBackgroundPaint2.setColor(num3.intValue());
            chat_actionBackgroundSelectedPaint2.setColor(num4.intValue());
            currentColor = num2.intValue();
            if (serviceBitmapShader == null || currentColors.get("chat_serviceBackground") != null) {
                chat_actionBackgroundPaint.setColorFilter((ColorFilter) null);
                chat_actionBackgroundPaint.setShader((Shader) null);
                chat_actionBackgroundSelectedPaint.setColorFilter((ColorFilter) null);
                chat_actionBackgroundSelectedPaint.setShader((Shader) null);
                return;
            }
            chat_actionBackgroundPaint.setShader(serviceBitmapShader);
            chat_actionBackgroundSelectedPaint.setShader(serviceBitmapShader);
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(1.8f);
            chat_actionBackgroundPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            chat_actionBackgroundPaint.setAlpha(127);
            chat_actionBackgroundSelectedPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            chat_actionBackgroundSelectedPaint.setAlpha(200);
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
                z2 = ("chat_wallpaper".equals(str) || "chat_wallpaper_gradient_to".equals(str) || "key_chat_wallpaper_gradient_to2".equals(str) || "key_chat_wallpaper_gradient_to3".equals(str)) ? false : currentTheme.isDefaultMainAccent();
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
        if ("windowBackgroundWhite".equals(str) || "windowBackgroundGray".equals(str) || "actionBarDefault".equals(str) || "actionBarDefaultArchived".equals(str)) {
            num2 = Integer.valueOf(num2.intValue() | -16777216);
        }
        return num2.intValue();
    }

    public static void setColor(String str, int i, boolean z) {
        if (str.equals("chat_wallpaper") || str.equals("chat_wallpaper_gradient_to") || str.equals("key_chat_wallpaper_gradient_to2") || str.equals("key_chat_wallpaper_gradient_to3") || str.equals("windowBackgroundWhite") || str.equals("windowBackgroundGray") || str.equals("actionBarDefault") || str.equals("actionBarDefaultArchived")) {
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
                    c = 0;
                    break;
                }
                break;
            case -1625862693:
                if (str.equals("chat_wallpaper")) {
                    c = 1;
                    break;
                }
                break;
            case -1397026623:
                if (str.equals("windowBackgroundGray")) {
                    c = 2;
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
                    c = 4;
                    break;
                }
                break;
            case 426061980:
                if (str.equals("chat_serviceBackground")) {
                    c = 5;
                    break;
                }
                break;
            case 1381936524:
                if (str.equals("key_chat_wallpaper_gradient_to2")) {
                    c = 6;
                    break;
                }
                break;
            case 1381936525:
                if (str.equals("key_chat_wallpaper_gradient_to3")) {
                    c = 7;
                    break;
                }
                break;
            case 1573464919:
                if (str.equals("chat_serviceBackgroundSelected")) {
                    c = 8;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 1:
            case 3:
            case 6:
            case 7:
                reloadWallpaper();
                return;
            case 2:
                if (Build.VERSION.SDK_INT >= 26) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, new Object[0]);
                    return;
                }
                return;
            case 4:
                if (Build.VERSION.SDK_INT >= 23) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, new Object[0]);
                    return;
                }
                return;
            case 5:
            case 8:
                applyChatServiceMessageColor();
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
        currentColors.remove("key_chat_wallpaper_gradient_to2");
        currentColors.remove("key_chat_wallpaper_gradient_to3");
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
            if (drawable instanceof StatusDrawable) {
                ((StatusDrawable) drawable).setColor(i);
            } else if (drawable instanceof MsgClockDrawable) {
                ((MsgClockDrawable) drawable).setColor(i);
            } else if (drawable instanceof ShapeDrawable) {
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
        Drawable drawable2;
        if (drawable instanceof StateListDrawable) {
            if (z) {
                try {
                    drawable2 = getStateDrawable(drawable, 0);
                } catch (Throwable unused) {
                    return;
                }
            } else {
                drawable2 = getStateDrawable(drawable, 1);
            }
            if (drawable2 instanceof ShapeDrawable) {
                ((ShapeDrawable) drawable2).getPaint().setColor(i);
            } else {
                drawable2.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
            }
        }
    }

    @SuppressLint({"DiscouragedPrivateApi"})
    @TargetApi(21)
    public static void setRippleDrawableForceSoftware(RippleDrawable rippleDrawable) {
        if (rippleDrawable != null) {
            Class<RippleDrawable> cls = RippleDrawable.class;
            try {
                cls.getDeclaredMethod("setForceSoftware", new Class[]{Boolean.TYPE}).invoke(rippleDrawable, new Object[]{Boolean.TRUE});
            } catch (Throwable unused) {
            }
        }
    }

    public static void setSelectorDrawableColor(Drawable drawable, int i, boolean z) {
        Drawable drawable2;
        if (drawable instanceof StateListDrawable) {
            if (z) {
                try {
                    Drawable stateDrawable = getStateDrawable(drawable, 0);
                    if (stateDrawable instanceof ShapeDrawable) {
                        ((ShapeDrawable) stateDrawable).getPaint().setColor(i);
                    } else {
                        stateDrawable.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                    }
                    drawable2 = getStateDrawable(drawable, 1);
                } catch (Throwable unused) {
                    return;
                }
            } else {
                drawable2 = getStateDrawable(drawable, 2);
            }
            if (drawable2 instanceof ShapeDrawable) {
                ((ShapeDrawable) drawable2).getPaint().setColor(i);
            } else {
                drawable2.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
            }
        } else if (Build.VERSION.SDK_INT >= 21 && (drawable instanceof RippleDrawable)) {
            RippleDrawable rippleDrawable = (RippleDrawable) drawable;
            if (z) {
                rippleDrawable.setColor(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{i}));
            } else if (rippleDrawable.getNumberOfLayers() > 0) {
                Drawable drawable3 = rippleDrawable.getDrawable(0);
                if (drawable3 instanceof ShapeDrawable) {
                    ((ShapeDrawable) drawable3).getPaint().setColor(i);
                } else {
                    drawable3.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
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
        Drawable drawable = wallpaper;
        if (drawable instanceof MotionBackgroundDrawable) {
            previousPhase = ((MotionBackgroundDrawable) drawable).getPhase();
        } else {
            previousPhase = 0;
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
        boolean z;
        File file;
        float f;
        if (wallpaper == null) {
            ThemeInfo themeInfo = currentTheme;
            boolean z2 = themeInfo.firstAccentIsDefault && themeInfo.currentAccentId == DEFALT_THEME_ACCENT_ID;
            ThemeAccent accent = themeInfo.getAccent(false);
            if (accent != null) {
                file = accent.getPathToWallpaper();
                z = accent.patternMotion;
            } else {
                file = null;
                z = false;
            }
            ThemeInfo themeInfo2 = currentTheme;
            OverrideWallpaperInfo overrideWallpaperInfo = themeInfo2.overrideWallpaper;
            if (overrideWallpaperInfo != null) {
                f = overrideWallpaperInfo.intensity * 100.0f;
            } else {
                f = accent != null ? accent.patternIntensity * 100.0f : (float) themeInfo2.patternIntensity;
            }
            int i = (int) f;
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            $$Lambda$Theme$TShpCD64dbtXQVgC8jVs0EdBb_w r3 = new Runnable(i, z2, file, z) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ boolean f$2;
                public final /* synthetic */ File f$3;
                public final /* synthetic */ boolean f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    Theme.lambda$loadWallpaper$8(Theme.OverrideWallpaperInfo.this, this.f$1, this.f$2, this.f$3, this.f$4);
                }
            };
            wallpaperLoadTask = r3;
            dispatchQueue.postRunnable(r3);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:128:0x02a0  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$loadWallpaper$8(org.telegram.ui.ActionBar.Theme.OverrideWallpaperInfo r17, int r18, boolean r19, java.io.File r20, boolean r21) {
        /*
            r1 = r17
            boolean r0 = hasPreviousTheme
            r2 = 1
            r3 = 0
            if (r0 == 0) goto L_0x000c
            boolean r0 = isApplyingAccent
            if (r0 == 0) goto L_0x0010
        L_0x000c:
            if (r1 == 0) goto L_0x0010
            r0 = 1
            goto L_0x0011
        L_0x0010:
            r0 = 0
        L_0x0011:
            if (r1 == 0) goto L_0x0032
            boolean r4 = r1.isMotion
            if (r4 == 0) goto L_0x0019
            r4 = 1
            goto L_0x001a
        L_0x0019:
            r4 = 0
        L_0x001a:
            isWallpaperMotion = r4
            int r4 = r1.color
            if (r4 == 0) goto L_0x002e
            boolean r4 = r17.isDefault()
            if (r4 != 0) goto L_0x002e
            boolean r4 = r17.isColor()
            if (r4 != 0) goto L_0x002e
            r4 = 1
            goto L_0x002f
        L_0x002e:
            r4 = 0
        L_0x002f:
            isPatternWallpaper = r4
            goto L_0x0041
        L_0x0032:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = currentTheme
            boolean r5 = r4.isMotion
            isWallpaperMotion = r5
            int r4 = r4.patternBgColor
            if (r4 == 0) goto L_0x003e
            r4 = 1
            goto L_0x003f
        L_0x003e:
            r4 = 0
        L_0x003f:
            isPatternWallpaper = r4
        L_0x0041:
            patternIntensity = r18
            r4 = 100
            r6 = 2
            if (r0 != 0) goto L_0x01c2
            if (r19 == 0) goto L_0x004c
            r0 = 0
            goto L_0x0056
        L_0x004c:
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = currentColors
            java.lang.String r7 = "chat_wallpaper"
            java.lang.Object r0 = r0.get(r7)
            java.lang.Integer r0 = (java.lang.Integer) r0
        L_0x0056:
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = currentColors
            java.lang.String r8 = "key_chat_wallpaper_gradient_to3"
            java.lang.Object r7 = r7.get(r8)
            java.lang.Integer r7 = (java.lang.Integer) r7
            if (r7 != 0) goto L_0x0066
            java.lang.Integer r7 = java.lang.Integer.valueOf(r3)
        L_0x0066:
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = currentColors
            java.lang.String r9 = "key_chat_wallpaper_gradient_to2"
            java.lang.Object r8 = r8.get(r9)
            java.lang.Integer r8 = (java.lang.Integer) r8
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = currentColors
            java.lang.String r10 = "chat_wallpaper_gradient_to"
            java.lang.Object r9 = r9.get(r10)
            java.lang.Integer r9 = (java.lang.Integer) r9
            if (r20 == 0) goto L_0x00c8
            boolean r10 = r20.exists()
            if (r10 == 0) goto L_0x00c8
            if (r0 == 0) goto L_0x00b0
            if (r9 == 0) goto L_0x00b0
            if (r8 == 0) goto L_0x00b0
            org.telegram.ui.Components.MotionBackgroundDrawable r10 = new org.telegram.ui.Components.MotionBackgroundDrawable     // Catch:{ all -> 0x00c2 }
            int r12 = r0.intValue()     // Catch:{ all -> 0x00c2 }
            int r13 = r9.intValue()     // Catch:{ all -> 0x00c2 }
            int r14 = r8.intValue()     // Catch:{ all -> 0x00c2 }
            int r15 = r7.intValue()     // Catch:{ all -> 0x00c2 }
            r16 = 0
            r11 = r10
            r11.<init>(r12, r13, r14, r15, r16)     // Catch:{ all -> 0x00c2 }
            int r0 = patternIntensity     // Catch:{ all -> 0x00c2 }
            java.lang.String r7 = r20.getAbsolutePath()     // Catch:{ all -> 0x00c2 }
            android.graphics.Bitmap r7 = android.graphics.BitmapFactory.decodeFile(r7)     // Catch:{ all -> 0x00c2 }
            r10.setPatternBitmap(r0, r7)     // Catch:{ all -> 0x00c2 }
            wallpaper = r10     // Catch:{ all -> 0x00c2 }
            goto L_0x00ba
        L_0x00b0:
            java.lang.String r0 = r20.getAbsolutePath()     // Catch:{ all -> 0x00c2 }
            android.graphics.drawable.Drawable r0 = android.graphics.drawable.Drawable.createFromPath(r0)     // Catch:{ all -> 0x00c2 }
            wallpaper = r0     // Catch:{ all -> 0x00c2 }
        L_0x00ba:
            isWallpaperMotion = r21     // Catch:{ all -> 0x00c2 }
            isCustomTheme = r2     // Catch:{ all -> 0x00c2 }
            isPatternWallpaper = r2     // Catch:{ all -> 0x00c2 }
            goto L_0x01c2
        L_0x00c2:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x01c2
        L_0x00c8:
            if (r0 == 0) goto L_0x0145
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = currentColors
            java.lang.String r11 = "chat_wallpaper_gradient_rotation"
            java.lang.Object r10 = r10.get(r11)
            java.lang.Integer r10 = (java.lang.Integer) r10
            if (r10 != 0) goto L_0x00dc
            r10 = 45
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
        L_0x00dc:
            if (r9 == 0) goto L_0x0100
            if (r8 == 0) goto L_0x0100
            org.telegram.ui.Components.MotionBackgroundDrawable r10 = new org.telegram.ui.Components.MotionBackgroundDrawable
            int r12 = r0.intValue()
            int r13 = r9.intValue()
            int r14 = r8.intValue()
            int r15 = r7.intValue()
            r16 = 0
            r11 = r10
            r11.<init>(r12, r13, r14, r15, r16)
            int r0 = previousPhase
            r10.setPhase(r0)
            wallpaper = r10
            goto L_0x0141
        L_0x0100:
            if (r9 == 0) goto L_0x0136
            boolean r7 = r9.equals(r0)
            if (r7 == 0) goto L_0x0109
            goto L_0x0136
        L_0x0109:
            int[] r7 = new int[r6]
            int r0 = r0.intValue()
            r7[r3] = r0
            int r0 = r9.intValue()
            r7[r2] = r0
            int r0 = r10.intValue()
            android.graphics.drawable.GradientDrawable$Orientation r0 = org.telegram.ui.Components.BackgroundGradientDrawable.getGradientOrientation(r0)
            org.telegram.ui.Components.BackgroundGradientDrawable r8 = new org.telegram.ui.Components.BackgroundGradientDrawable
            r8.<init>(r0, r7)
            org.telegram.ui.ActionBar.Theme$11 r0 = new org.telegram.ui.ActionBar.Theme$11
            r0.<init>()
            org.telegram.ui.Components.BackgroundGradientDrawable$Sizes r7 = org.telegram.ui.Components.BackgroundGradientDrawable.Sizes.ofDeviceScreen()
            org.telegram.ui.Components.BackgroundGradientDrawable$Disposable r0 = r8.startDithering(r7, r0, r4)
            backgroundGradientDisposable = r0
            wallpaper = r8
            goto L_0x0141
        L_0x0136:
            android.graphics.drawable.ColorDrawable r7 = new android.graphics.drawable.ColorDrawable
            int r0 = r0.intValue()
            r7.<init>(r0)
            wallpaper = r7
        L_0x0141:
            isCustomTheme = r2
            goto L_0x01c2
        L_0x0145:
            java.lang.String r0 = themedWallpaperLink
            if (r0 == 0) goto L_0x0185
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x0180 }
            java.io.File r7 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ Exception -> 0x0180 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0180 }
            r8.<init>()     // Catch:{ Exception -> 0x0180 }
            java.lang.String r9 = themedWallpaperLink     // Catch:{ Exception -> 0x0180 }
            java.lang.String r9 = org.telegram.messenger.Utilities.MD5(r9)     // Catch:{ Exception -> 0x0180 }
            r8.append(r9)     // Catch:{ Exception -> 0x0180 }
            java.lang.String r9 = ".wp"
            r8.append(r9)     // Catch:{ Exception -> 0x0180 }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x0180 }
            r0.<init>(r7, r8)     // Catch:{ Exception -> 0x0180 }
            java.io.FileInputStream r7 = new java.io.FileInputStream     // Catch:{ Exception -> 0x0180 }
            r7.<init>(r0)     // Catch:{ Exception -> 0x0180 }
            android.graphics.Bitmap r0 = loadScreenSizedBitmap(r7, r3)     // Catch:{ Exception -> 0x0180 }
            if (r0 == 0) goto L_0x01c2
            android.graphics.drawable.BitmapDrawable r7 = new android.graphics.drawable.BitmapDrawable     // Catch:{ Exception -> 0x0180 }
            r7.<init>(r0)     // Catch:{ Exception -> 0x0180 }
            wallpaper = r7     // Catch:{ Exception -> 0x0180 }
            themedWallpaper = r7     // Catch:{ Exception -> 0x0180 }
            isCustomTheme = r2     // Catch:{ Exception -> 0x0180 }
            goto L_0x01c2
        L_0x0180:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x01c2
        L_0x0185:
            int r0 = themedWallpaperFileOffset
            if (r0 <= 0) goto L_0x01c2
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = currentTheme
            java.lang.String r7 = r0.pathToFile
            if (r7 != 0) goto L_0x0193
            java.lang.String r7 = r0.assetName
            if (r7 == 0) goto L_0x01c2
        L_0x0193:
            java.lang.String r0 = r0.assetName     // Catch:{ all -> 0x01be }
            if (r0 == 0) goto L_0x019c
            java.io.File r0 = getAssetFile(r0)     // Catch:{ all -> 0x01be }
            goto L_0x01a5
        L_0x019c:
            java.io.File r0 = new java.io.File     // Catch:{ all -> 0x01be }
            org.telegram.ui.ActionBar.Theme$ThemeInfo r7 = currentTheme     // Catch:{ all -> 0x01be }
            java.lang.String r7 = r7.pathToFile     // Catch:{ all -> 0x01be }
            r0.<init>(r7)     // Catch:{ all -> 0x01be }
        L_0x01a5:
            java.io.FileInputStream r7 = new java.io.FileInputStream     // Catch:{ all -> 0x01be }
            r7.<init>(r0)     // Catch:{ all -> 0x01be }
            int r0 = themedWallpaperFileOffset     // Catch:{ all -> 0x01be }
            android.graphics.Bitmap r0 = loadScreenSizedBitmap(r7, r0)     // Catch:{ all -> 0x01be }
            if (r0 == 0) goto L_0x01c2
            android.graphics.drawable.BitmapDrawable r7 = new android.graphics.drawable.BitmapDrawable     // Catch:{ all -> 0x01be }
            r7.<init>(r0)     // Catch:{ all -> 0x01be }
            wallpaper = r7     // Catch:{ all -> 0x01be }
            themedWallpaper = r7     // Catch:{ all -> 0x01be }
            isCustomTheme = r2     // Catch:{ all -> 0x01be }
            goto L_0x01c2
        L_0x01be:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01c2:
            android.graphics.drawable.Drawable r0 = wallpaper
            if (r0 != 0) goto L_0x02ac
            if (r1 == 0) goto L_0x01cb
            int r0 = r1.color
            goto L_0x01cc
        L_0x01cb:
            r0 = 0
        L_0x01cc:
            if (r1 == 0) goto L_0x0292
            boolean r7 = r17.isDefault()     // Catch:{ all -> 0x029b }
            if (r7 == 0) goto L_0x01d6
            goto L_0x0292
        L_0x01d6:
            boolean r7 = r17.isColor()     // Catch:{ all -> 0x029b }
            if (r7 == 0) goto L_0x01e0
            int r7 = r1.gradientColor1     // Catch:{ all -> 0x029b }
            if (r7 == 0) goto L_0x029c
        L_0x01e0:
            if (r0 == 0) goto L_0x0260
            boolean r7 = isPatternWallpaper     // Catch:{ all -> 0x029b }
            if (r7 == 0) goto L_0x01ea
            int r7 = r1.gradientColor2     // Catch:{ all -> 0x029b }
            if (r7 == 0) goto L_0x0260
        L_0x01ea:
            int r7 = r1.gradientColor1     // Catch:{ all -> 0x029b }
            if (r7 == 0) goto L_0x0233
            int r8 = r1.gradientColor2     // Catch:{ all -> 0x029b }
            if (r8 == 0) goto L_0x0233
            org.telegram.ui.Components.MotionBackgroundDrawable r4 = new org.telegram.ui.Components.MotionBackgroundDrawable     // Catch:{ all -> 0x029b }
            int r10 = r1.color     // Catch:{ all -> 0x029b }
            int r11 = r1.gradientColor1     // Catch:{ all -> 0x029b }
            int r12 = r1.gradientColor2     // Catch:{ all -> 0x029b }
            int r13 = r1.gradientColor3     // Catch:{ all -> 0x029b }
            r14 = 0
            r9 = r4
            r9.<init>(r10, r11, r12, r13, r14)     // Catch:{ all -> 0x029b }
            int r5 = previousPhase     // Catch:{ all -> 0x029b }
            r4.setPhase(r5)     // Catch:{ all -> 0x029b }
            boolean r5 = isPatternWallpaper     // Catch:{ all -> 0x029b }
            if (r5 == 0) goto L_0x0230
            java.io.File r5 = new java.io.File     // Catch:{ all -> 0x029b }
            java.io.File r6 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ all -> 0x029b }
            java.lang.String r7 = r1.fileName     // Catch:{ all -> 0x029b }
            r5.<init>(r6, r7)     // Catch:{ all -> 0x029b }
            boolean r6 = r5.exists()     // Catch:{ all -> 0x029b }
            if (r6 == 0) goto L_0x0230
            float r1 = r1.intensity     // Catch:{ all -> 0x029b }
            r6 = 1120403456(0x42CLASSNAME, float:100.0)
            float r1 = r1 * r6
            int r1 = (int) r1     // Catch:{ all -> 0x029b }
            java.io.FileInputStream r6 = new java.io.FileInputStream     // Catch:{ all -> 0x029b }
            r6.<init>(r5)     // Catch:{ all -> 0x029b }
            android.graphics.Bitmap r3 = loadScreenSizedBitmap(r6, r3)     // Catch:{ all -> 0x029b }
            r4.setPatternBitmap(r1, r3)     // Catch:{ all -> 0x029b }
            isCustomTheme = r2     // Catch:{ all -> 0x029b }
        L_0x0230:
            wallpaper = r4     // Catch:{ all -> 0x029b }
            goto L_0x029c
        L_0x0233:
            if (r7 == 0) goto L_0x0258
            int[] r6 = new int[r6]     // Catch:{ all -> 0x029b }
            r6[r3] = r0     // Catch:{ all -> 0x029b }
            r6[r2] = r7     // Catch:{ all -> 0x029b }
            int r1 = r1.rotation     // Catch:{ all -> 0x029b }
            android.graphics.drawable.GradientDrawable$Orientation r1 = org.telegram.ui.Components.BackgroundGradientDrawable.getGradientOrientation(r1)     // Catch:{ all -> 0x029b }
            org.telegram.ui.Components.BackgroundGradientDrawable r3 = new org.telegram.ui.Components.BackgroundGradientDrawable     // Catch:{ all -> 0x029b }
            r3.<init>(r1, r6)     // Catch:{ all -> 0x029b }
            org.telegram.ui.ActionBar.Theme$12 r1 = new org.telegram.ui.ActionBar.Theme$12     // Catch:{ all -> 0x029b }
            r1.<init>()     // Catch:{ all -> 0x029b }
            org.telegram.ui.Components.BackgroundGradientDrawable$Sizes r6 = org.telegram.ui.Components.BackgroundGradientDrawable.Sizes.ofDeviceScreen()     // Catch:{ all -> 0x029b }
            org.telegram.ui.Components.BackgroundGradientDrawable$Disposable r1 = r3.startDithering(r6, r1, r4)     // Catch:{ all -> 0x029b }
            backgroundGradientDisposable = r1     // Catch:{ all -> 0x029b }
            wallpaper = r3     // Catch:{ all -> 0x029b }
            goto L_0x029c
        L_0x0258:
            android.graphics.drawable.ColorDrawable r1 = new android.graphics.drawable.ColorDrawable     // Catch:{ all -> 0x029b }
            r1.<init>(r0)     // Catch:{ all -> 0x029b }
            wallpaper = r1     // Catch:{ all -> 0x029b }
            goto L_0x029c
        L_0x0260:
            java.io.File r4 = new java.io.File     // Catch:{ all -> 0x029b }
            java.io.File r5 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ all -> 0x029b }
            java.lang.String r1 = r1.fileName     // Catch:{ all -> 0x029b }
            r4.<init>(r5, r1)     // Catch:{ all -> 0x029b }
            boolean r1 = r4.exists()     // Catch:{ all -> 0x029b }
            if (r1 == 0) goto L_0x0285
            java.io.FileInputStream r1 = new java.io.FileInputStream     // Catch:{ all -> 0x029b }
            r1.<init>(r4)     // Catch:{ all -> 0x029b }
            android.graphics.Bitmap r1 = loadScreenSizedBitmap(r1, r3)     // Catch:{ all -> 0x029b }
            if (r1 == 0) goto L_0x0285
            android.graphics.drawable.BitmapDrawable r4 = new android.graphics.drawable.BitmapDrawable     // Catch:{ all -> 0x029b }
            r4.<init>(r1)     // Catch:{ all -> 0x029b }
            wallpaper = r4     // Catch:{ all -> 0x029b }
            isCustomTheme = r2     // Catch:{ all -> 0x029b }
        L_0x0285:
            android.graphics.drawable.Drawable r1 = wallpaper     // Catch:{ all -> 0x029b }
            if (r1 != 0) goto L_0x029c
            android.graphics.drawable.Drawable r1 = createDefaultWallpaper(r3)     // Catch:{ all -> 0x029b }
            wallpaper = r1     // Catch:{ all -> 0x029b }
            isCustomTheme = r3     // Catch:{ all -> 0x029b }
            goto L_0x029c
        L_0x0292:
            android.graphics.drawable.Drawable r1 = createDefaultWallpaper(r3)     // Catch:{ all -> 0x029b }
            wallpaper = r1     // Catch:{ all -> 0x029b }
            isCustomTheme = r3     // Catch:{ all -> 0x029b }
            goto L_0x029c
        L_0x029b:
        L_0x029c:
            android.graphics.drawable.Drawable r1 = wallpaper
            if (r1 != 0) goto L_0x02ac
            if (r0 != 0) goto L_0x02a5
            r0 = -2693905(0xffffffffffd6e4ef, float:NaN)
        L_0x02a5:
            android.graphics.drawable.ColorDrawable r1 = new android.graphics.drawable.ColorDrawable
            r1.<init>(r0)
            wallpaper = r1
        L_0x02ac:
            android.graphics.drawable.Drawable r0 = wallpaper
            calcBackgroundColor(r0, r2)
            org.telegram.ui.ActionBar.-$$Lambda$Theme$xtPvl-WavJtS_3Vro5zDci24Fuo r0 = org.telegram.ui.ActionBar.$$Lambda$Theme$xtPvlWavJtS_3Vro5zDci24Fuo.INSTANCE
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.lambda$loadWallpaper$8(org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo, int, boolean, java.io.File, boolean):void");
    }

    static /* synthetic */ void lambda$loadWallpaper$7() {
        wallpaperLoadTask = null;
        applyChatServiceMessageColor();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewWallpapper, new Object[0]);
    }

    public static Drawable createDefaultWallpaper(boolean z) {
        int i;
        MotionBackgroundDrawable motionBackgroundDrawable = new MotionBackgroundDrawable(-2368069, -9722489, -2762611, -7817084, false);
        Point point = AndroidUtilities.displaySize;
        int min = Math.min(point.x, point.y);
        Point point2 = AndroidUtilities.displaySize;
        int max = Math.max(point2.x, point2.y);
        if (Build.VERSION.SDK_INT >= 29) {
            i = NUM;
        } else {
            i = motionBackgroundDrawable.getPatternColor();
        }
        motionBackgroundDrawable.setPatternBitmap(34, SvgHelper.getBitmap(NUM, min, max, i));
        return motionBackgroundDrawable;
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
            try {
                fileInputStream.close();
            } catch (Exception unused) {
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

    /* JADX WARNING: Removed duplicated region for block: B:52:0x00f8 A[SYNTHETIC, Splitter:B:52:0x00f8] */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x016f A[SYNTHETIC, Splitter:B:91:0x016f] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.drawable.Drawable getThemedWallpaper(boolean r17, android.view.View r18) {
        /*
            r0 = r17
            r1 = r18
            java.util.HashMap<java.lang.String, java.lang.Integer> r2 = currentColors
            java.lang.String r3 = "chat_wallpaper"
            java.lang.Object r2 = r2.get(r3)
            java.lang.Integer r2 = (java.lang.Integer) r2
            r3 = 1
            r4 = 0
            r5 = 0
            if (r2 == 0) goto L_0x00ce
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = currentColors
            java.lang.String r7 = "chat_wallpaper_gradient_to"
            java.lang.Object r6 = r6.get(r7)
            java.lang.Integer r6 = (java.lang.Integer) r6
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = currentColors
            java.lang.String r8 = "key_chat_wallpaper_gradient_to2"
            java.lang.Object r7 = r7.get(r8)
            java.lang.Integer r7 = (java.lang.Integer) r7
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = currentColors
            java.lang.String r9 = "key_chat_wallpaper_gradient_to3"
            java.lang.Object r8 = r8.get(r9)
            java.lang.Integer r8 = (java.lang.Integer) r8
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = currentColors
            java.lang.String r10 = "chat_wallpaper_gradient_rotation"
            java.lang.Object r9 = r9.get(r10)
            java.lang.Integer r9 = (java.lang.Integer) r9
            if (r9 != 0) goto L_0x0043
            r9 = 45
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
        L_0x0043:
            if (r6 != 0) goto L_0x004f
            android.graphics.drawable.ColorDrawable r0 = new android.graphics.drawable.ColorDrawable
            int r1 = r2.intValue()
            r0.<init>(r1)
            return r0
        L_0x004f:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r10 = currentTheme
            org.telegram.ui.ActionBar.Theme$ThemeAccent r10 = r10.getAccent(r4)
            if (r10 == 0) goto L_0x0070
            java.lang.String r11 = r10.patternSlug
            boolean r11 = android.text.TextUtils.isEmpty(r11)
            if (r11 != 0) goto L_0x0070
            org.telegram.ui.ActionBar.Theme$ThemeInfo r11 = previousTheme
            if (r11 != 0) goto L_0x0070
            java.io.File r10 = r10.getPathToWallpaper()
            if (r10 == 0) goto L_0x0070
            boolean r11 = r10.exists()
            if (r11 == 0) goto L_0x0070
            goto L_0x0071
        L_0x0070:
            r10 = r5
        L_0x0071:
            if (r7 == 0) goto L_0x0093
            org.telegram.ui.Components.MotionBackgroundDrawable r1 = new org.telegram.ui.Components.MotionBackgroundDrawable
            int r12 = r2.intValue()
            int r13 = r6.intValue()
            int r14 = r7.intValue()
            if (r8 == 0) goto L_0x0089
            int r2 = r8.intValue()
            r15 = r2
            goto L_0x008a
        L_0x0089:
            r15 = 0
        L_0x008a:
            r16 = 1
            r11 = r1
            r11.<init>(r12, r13, r14, r15, r16)
            if (r10 != 0) goto L_0x00cc
            return r1
        L_0x0093:
            if (r10 != 0) goto L_0x00cb
            r7 = 2
            int[] r7 = new int[r7]
            int r2 = r2.intValue()
            r7[r4] = r2
            int r2 = r6.intValue()
            r7[r3] = r2
            int r2 = r9.intValue()
            android.graphics.drawable.GradientDrawable$Orientation r2 = org.telegram.ui.Components.BackgroundGradientDrawable.getGradientOrientation(r2)
            org.telegram.ui.Components.BackgroundGradientDrawable r3 = new org.telegram.ui.Components.BackgroundGradientDrawable
            r3.<init>(r2, r7)
            if (r0 != 0) goto L_0x00b8
            org.telegram.ui.Components.BackgroundGradientDrawable$Sizes r2 = org.telegram.ui.Components.BackgroundGradientDrawable.Sizes.ofDeviceScreen()
            goto L_0x00c0
        L_0x00b8:
            r2 = 1040187392(0x3e000000, float:0.125)
            org.telegram.ui.Components.BackgroundGradientDrawable$Sizes$Orientation r4 = org.telegram.ui.Components.BackgroundGradientDrawable.Sizes.Orientation.PORTRAIT
            org.telegram.ui.Components.BackgroundGradientDrawable$Sizes r2 = org.telegram.ui.Components.BackgroundGradientDrawable.Sizes.ofDeviceScreen(r2, r4)
        L_0x00c0:
            if (r1 == 0) goto L_0x00c7
            org.telegram.ui.ActionBar.Theme$13 r5 = new org.telegram.ui.ActionBar.Theme$13
            r5.<init>(r0, r1)
        L_0x00c7:
            r3.startDithering(r2, r5)
            return r3
        L_0x00cb:
            r1 = r5
        L_0x00cc:
            r2 = r1
            goto L_0x00f5
        L_0x00ce:
            int r1 = themedWallpaperFileOffset
            if (r1 <= 0) goto L_0x00f3
            org.telegram.ui.ActionBar.Theme$ThemeInfo r1 = currentTheme
            java.lang.String r2 = r1.pathToFile
            if (r2 != 0) goto L_0x00dc
            java.lang.String r2 = r1.assetName
            if (r2 == 0) goto L_0x00f3
        L_0x00dc:
            java.lang.String r1 = r1.assetName
            if (r1 == 0) goto L_0x00e5
            java.io.File r1 = getAssetFile(r1)
            goto L_0x00ee
        L_0x00e5:
            java.io.File r1 = new java.io.File
            org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = currentTheme
            java.lang.String r2 = r2.pathToFile
            r1.<init>(r2)
        L_0x00ee:
            r10 = r1
            int r1 = themedWallpaperFileOffset
            r2 = r5
            goto L_0x00f6
        L_0x00f3:
            r2 = r5
            r10 = r2
        L_0x00f5:
            r1 = 0
        L_0x00f6:
            if (r10 == 0) goto L_0x0187
            java.io.FileInputStream r6 = new java.io.FileInputStream     // Catch:{ all -> 0x0168 }
            r6.<init>(r10)     // Catch:{ all -> 0x0168 }
            java.nio.channels.FileChannel r7 = r6.getChannel()     // Catch:{ all -> 0x0166 }
            long r8 = (long) r1     // Catch:{ all -> 0x0166 }
            r7.position(r8)     // Catch:{ all -> 0x0166 }
            android.graphics.BitmapFactory$Options r1 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0166 }
            r1.<init>()     // Catch:{ all -> 0x0166 }
            r7 = 1120403456(0x42CLASSNAME, float:100.0)
            if (r0 == 0) goto L_0x012a
            r1.inJustDecodeBounds = r3     // Catch:{ all -> 0x0166 }
            int r0 = r1.outWidth     // Catch:{ all -> 0x0166 }
            float r0 = (float) r0     // Catch:{ all -> 0x0166 }
            int r8 = r1.outHeight     // Catch:{ all -> 0x0166 }
            float r8 = (float) r8     // Catch:{ all -> 0x0166 }
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r7)     // Catch:{ all -> 0x0166 }
        L_0x011a:
            float r10 = (float) r9     // Catch:{ all -> 0x0166 }
            int r11 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r11 > 0) goto L_0x0123
            int r10 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r10 <= 0) goto L_0x012a
        L_0x0123:
            int r3 = r3 * 2
            r10 = 1073741824(0x40000000, float:2.0)
            float r0 = r0 / r10
            float r8 = r8 / r10
            goto L_0x011a
        L_0x012a:
            r1.inJustDecodeBounds = r4     // Catch:{ all -> 0x0166 }
            r1.inSampleSize = r3     // Catch:{ all -> 0x0166 }
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeStream(r6, r5, r1)     // Catch:{ all -> 0x0166 }
            if (r2 == 0) goto L_0x0151
            org.telegram.ui.ActionBar.Theme$ThemeInfo r1 = currentTheme     // Catch:{ all -> 0x0166 }
            org.telegram.ui.ActionBar.Theme$ThemeAccent r1 = r1.getAccent(r4)     // Catch:{ all -> 0x0166 }
            if (r1 == 0) goto L_0x0142
            float r1 = r1.patternIntensity     // Catch:{ all -> 0x0166 }
            float r1 = r1 * r7
            int r1 = (int) r1     // Catch:{ all -> 0x0166 }
            goto L_0x0144
        L_0x0142:
            r1 = 100
        L_0x0144:
            r2.setPatternBitmap(r1, r0)     // Catch:{ all -> 0x0166 }
            r6.close()     // Catch:{ Exception -> 0x014b }
            goto L_0x0150
        L_0x014b:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0150:
            return r2
        L_0x0151:
            if (r0 == 0) goto L_0x0162
            android.graphics.drawable.BitmapDrawable r1 = new android.graphics.drawable.BitmapDrawable     // Catch:{ all -> 0x0166 }
            r1.<init>(r0)     // Catch:{ all -> 0x0166 }
            r6.close()     // Catch:{ Exception -> 0x015c }
            goto L_0x0161
        L_0x015c:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x0161:
            return r1
        L_0x0162:
            r6.close()     // Catch:{ Exception -> 0x0173 }
            goto L_0x0187
        L_0x0166:
            r0 = move-exception
            goto L_0x016a
        L_0x0168:
            r0 = move-exception
            r6 = r5
        L_0x016a:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0179 }
            if (r6 == 0) goto L_0x0187
            r6.close()     // Catch:{ Exception -> 0x0173 }
            goto L_0x0187
        L_0x0173:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
            goto L_0x0187
        L_0x0179:
            r0 = move-exception
            r1 = r0
            if (r6 == 0) goto L_0x0186
            r6.close()     // Catch:{ Exception -> 0x0181 }
            goto L_0x0186
        L_0x0181:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x0186:
            throw r1
        L_0x0187:
            return r5
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
        Drawable drawable = themedWallpaper;
        if (drawable == null) {
            drawable = wallpaper;
        }
        if (drawable != null || wallpaperLoadTask == null) {
            return drawable;
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Utilities.searchQueue.postRunnable(new Runnable(countDownLatch) {
            public final /* synthetic */ CountDownLatch f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        Drawable drawable2 = themedWallpaper;
        return drawable2 != null ? drawable2 : wallpaper;
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

    public static AudioVisualizerDrawable getCurrentAudiVisualizerDrawable() {
        if (chat_msgAudioVisualizeDrawable == null) {
            chat_msgAudioVisualizeDrawable = new AudioVisualizerDrawable();
        }
        return chat_msgAudioVisualizeDrawable;
    }

    public static void unrefAudioVisualizeDrawable(MessageObject messageObject) {
        AudioVisualizerDrawable audioVisualizerDrawable = chat_msgAudioVisualizeDrawable;
        if (audioVisualizerDrawable != null) {
            if (audioVisualizerDrawable.getParentView() == null || messageObject == null) {
                chat_msgAudioVisualizeDrawable.setParentView((View) null);
                return;
            }
            if (animatedOutVisualizerDrawables == null) {
                animatedOutVisualizerDrawables = new HashMap<>();
            }
            animatedOutVisualizerDrawables.put(messageObject, chat_msgAudioVisualizeDrawable);
            chat_msgAudioVisualizeDrawable.setWaveform(false, true, (float[]) null);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    Theme.lambda$unrefAudioVisualizeDrawable$9(MessageObject.this);
                }
            }, 200);
            chat_msgAudioVisualizeDrawable = null;
        }
    }

    static /* synthetic */ void lambda$unrefAudioVisualizeDrawable$9(MessageObject messageObject) {
        AudioVisualizerDrawable remove = animatedOutVisualizerDrawables.remove(messageObject);
        if (remove != null) {
            remove.setParentView((View) null);
        }
    }

    public static AudioVisualizerDrawable getAnimatedOutAudioVisualizerDrawable(MessageObject messageObject) {
        HashMap<MessageObject, AudioVisualizerDrawable> hashMap = animatedOutVisualizerDrawables;
        if (hashMap == null || messageObject == null) {
            return null;
        }
        return hashMap.get(messageObject);
    }

    public static StatusDrawable getChatStatusDrawable(int i) {
        if (i < 0 || i > 4) {
            return null;
        }
        StatusDrawable[] statusDrawableArr = chat_status_drawables;
        StatusDrawable statusDrawable = statusDrawableArr[i];
        if (statusDrawable != null) {
            return statusDrawable;
        }
        if (i == 0) {
            statusDrawableArr[0] = new TypingDotsDrawable(true);
        } else if (i == 1) {
            statusDrawableArr[1] = new RecordStatusDrawable(true);
        } else if (i == 2) {
            statusDrawableArr[2] = new SendingFileDrawable(true);
        } else if (i == 3) {
            statusDrawableArr[3] = new PlayingGameDrawable(true);
        } else if (i == 4) {
            statusDrawableArr[4] = new RoundStatusDrawable(true);
        }
        StatusDrawable statusDrawable2 = chat_status_drawables[i];
        statusDrawable2.start();
        statusDrawable2.setColor(getColor("chats_actionMessage"));
        return statusDrawable2;
    }

    public static FragmentContextViewWavesDrawable getFragmentContextViewWavesDrawable() {
        if (fragmentContextViewWavesDrawable == null) {
            fragmentContextViewWavesDrawable = new FragmentContextViewWavesDrawable();
        }
        return fragmentContextViewWavesDrawable;
    }
}
