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
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
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
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.StateSet;
import android.view.View;
import androidx.core.graphics.ColorUtils;
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
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.time.SunDate;
import org.telegram.tgnet.ConnectionsManager;
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
import org.telegram.tgnet.TLRPC$TL_wallPaper;
import org.telegram.tgnet.TLRPC$TL_wallPaperNoFile;
import org.telegram.tgnet.TLRPC$Theme;
import org.telegram.tgnet.TLRPC$ThemeSettings;
import org.telegram.tgnet.TLRPC$WallPaper;
import org.telegram.tgnet.TLRPC$WallPaperSettings;
import org.telegram.ui.Components.AudioVisualizerDrawable;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.ChoosingStickerStatusDrawable;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.FragmentContextViewWavesDrawable;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.MsgClockDrawable;
import org.telegram.ui.Components.PathAnimator;
import org.telegram.ui.Components.PlayingGameDrawable;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RecordStatusDrawable;
import org.telegram.ui.Components.RoundStatusDrawable;
import org.telegram.ui.Components.ScamDrawable;
import org.telegram.ui.Components.SendingFileDrawable;
import org.telegram.ui.Components.StatusDrawable;
import org.telegram.ui.Components.TypingDotsDrawable;
import org.telegram.ui.RoundVideoProgressShadow;

public class Theme {
    public static Paint DEBUG_BLUE = null;
    public static Paint DEBUG_RED = null;
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
    public static TextPaint chat_actionTextPaint = null;
    public static TextPaint chat_adminPaint = null;
    public static RLottieDrawable[] chat_attachButtonDrawables = new RLottieDrawable[6];
    public static Drawable chat_attachEmptyDrawable = null;
    public static TextPaint chat_audioPerformerPaint = null;
    public static TextPaint chat_audioTimePaint = null;
    public static TextPaint chat_audioTitlePaint = null;
    public static TextPaint chat_botButtonPaint = null;
    public static Drawable chat_botCardDrawable = null;
    public static Drawable chat_botInlineDrawable = null;
    public static Drawable chat_botInviteDrawable = null;
    public static Drawable chat_botLinkDrawable = null;
    public static Paint chat_botProgressPaint = null;
    public static Drawable chat_botWebViewDrawable = null;
    public static Drawable chat_commentArrowDrawable = null;
    public static Drawable chat_commentDrawable = null;
    public static Drawable chat_commentStickerDrawable = null;
    public static Paint chat_composeBackgroundPaint = null;
    public static Drawable chat_composeShadowDrawable = null;
    public static Drawable chat_composeShadowRoundDrawable = null;
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
    public static Paint chat_messageBackgroundSelectedPaint = null;
    private static AudioVisualizerDrawable chat_msgAudioVisualizeDrawable = null;
    public static Drawable chat_msgAvatarLiveLocationDrawable = null;
    public static TextPaint chat_msgBotButtonPaint = null;
    public static Drawable chat_msgCallDownGreenDrawable = null;
    public static Drawable chat_msgCallDownRedDrawable = null;
    public static Drawable chat_msgCallUpGreenDrawable = null;
    public static MsgClockDrawable chat_msgClockDrawable = null;
    public static Drawable chat_msgErrorDrawable = null;
    public static Paint chat_msgErrorPaint = null;
    public static TextPaint chat_msgGameTextPaint = null;
    public static Drawable[] chat_msgInCallDrawable = new Drawable[2];
    public static Drawable[] chat_msgInCallSelectedDrawable = new Drawable[2];
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
    public static MessageDrawable chat_msgInSelectedDrawable = null;
    public static Drawable chat_msgInViewsDrawable = null;
    public static Drawable chat_msgInViewsSelectedDrawable = null;
    public static Drawable chat_msgMediaCheckDrawable = null;
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
    public static MessageDrawable chat_msgOutSelectedDrawable = null;
    public static Drawable chat_msgOutViewsDrawable = null;
    public static Drawable chat_msgOutViewsSelectedDrawable = null;
    public static Drawable chat_msgStickerCheckDrawable = null;
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
    public static Paint chat_outUrlPaint = null;
    public static Drawable[][] chat_photoStatesDrawables = ((Drawable[][]) Array.newInstance(Drawable.class, new int[]{13, 2}));
    public static Drawable[] chat_pollCheckDrawable = new Drawable[2];
    public static Drawable[] chat_pollCrossDrawable = new Drawable[2];
    public static Drawable[] chat_pollHintDrawable = new Drawable[2];
    public static Paint chat_pollTimerPaint = null;
    public static Drawable[] chat_psaHelpDrawable = new Drawable[2];
    public static Paint chat_radialProgress2Paint = null;
    public static Paint chat_radialProgressPaint = null;
    public static Paint chat_radialProgressPausedSeekbarPaint = null;
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
    private static StatusDrawable[] chat_status_drawables = new StatusDrawable[6];
    public static TextPaint chat_stickerCommentCountPaint = null;
    public static Paint chat_textSearchSelectionPaint = null;
    public static Paint chat_timeBackgroundPaint = null;
    public static TextPaint chat_timePaint = null;
    public static Path[] chat_updatePath = new Path[3];
    public static Paint chat_urlPaint = null;
    public static Paint checkboxSquare_backgroundPaint = null;
    public static Paint checkboxSquare_checkPaint = null;
    public static Paint checkboxSquare_eraserPaint = null;
    /* access modifiers changed from: private */
    public static HashMap<String, Integer> currentColors = new HashMap<>();
    private static HashMap<String, Integer> currentColorsNoAccent = new HashMap<>();
    private static ThemeInfo currentDayTheme = null;
    /* access modifiers changed from: private */
    public static ThemeInfo currentNightTheme = null;
    /* access modifiers changed from: private */
    public static ThemeInfo currentTheme = null;
    private static final HashMap<String, String> defaultChatDrawableColorKeys = new HashMap<>();
    private static final HashMap<String, Drawable> defaultChatDrawables = new HashMap<>();
    private static final HashMap<String, String> defaultChatPaintColors = new HashMap<>();
    private static final HashMap<String, Paint> defaultChatPaints = new HashMap<>();
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
    public static Drawable dialogs_checkDrawable = null;
    public static Drawable dialogs_checkReadDrawable = null;
    public static Drawable dialogs_clockDrawable = null;
    public static Paint dialogs_countGrayPaint = null;
    public static Paint dialogs_countPaint = null;
    public static TextPaint dialogs_countTextPaint = null;
    public static Drawable dialogs_errorDrawable = null;
    public static Paint dialogs_errorPaint = null;
    public static ScamDrawable dialogs_fakeDrawable = null;
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
    public static Paint dialogs_reactionsCountPaint = null;
    public static Drawable dialogs_reactionsMentionDrawable = null;
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
    private static int[] lastLoadingThemesTime = new int[4];
    private static long lastThemeSwitchTime;
    private static Sensor lightSensor;
    private static boolean lightSensorRegistered;
    public static Paint linkSelectionPaint;
    private static int loadingCurrentTheme;
    private static boolean[] loadingRemoteThemes = new boolean[4];
    /* access modifiers changed from: private */
    public static Paint maskPaint = new Paint(1);
    public static Drawable moveUpDrawable;
    /* access modifiers changed from: private */
    public static HashSet<String> myMessagesBubblesColorKeys = new HashSet<>();
    /* access modifiers changed from: private */
    public static HashSet<String> myMessagesColorKeys = new HashSet<>();
    private static ArrayList<ThemeInfo> otherThemes = new ArrayList<>();
    public static PathAnimator playPauseAnimator;
    private static int previousPhase;
    /* access modifiers changed from: private */
    public static ThemeInfo previousTheme;
    public static TextPaint profile_aboutTextPaint;
    public static Drawable profile_verifiedCheckDrawable;
    public static Drawable profile_verifiedDrawable;
    private static long[] remoteThemesHash = new long[4];
    private static RoundVideoProgressShadow roundPlayDrawable;
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
    private static float[] tmpHSV5;
    private static int[] viewPos = new int[2];
    private static Drawable wallpaper;
    public static Runnable wallpaperLoadTask;

    public static class BackgroundDrawableSettings {
        public Boolean isCustomTheme;
        public Boolean isPatternWallpaper;
        public Boolean isWallpaperMotion;
        public Drawable wallpaper;
    }

    private static float abs(float f) {
        return f > 0.0f ? f : -f;
    }

    public static void destroyResources() {
    }

    public static int getWallpaperColor(int i) {
        if (i == 0) {
            return 0;
        }
        return i | -16777216;
    }

    public static class MessageDrawable extends Drawable {
        public static MotionBackgroundDrawable[] motionBackground = new MotionBackgroundDrawable[3];
        private int alpha;
        private Drawable[][] backgroundDrawable;
        private int[][] backgroundDrawableColor;
        private Rect backupRect;
        private Bitmap crosfadeFromBitmap;
        private Shader crosfadeFromBitmapShader;
        public MessageDrawable crossfadeFromDrawable;
        public float crossfadeProgress;
        private boolean currentAnimateGradient;
        private int[][] currentBackgroundDrawableRadius;
        private int currentBackgroundHeight;
        private int currentColor;
        private int currentGradientColor1;
        private int currentGradientColor2;
        private int currentGradientColor3;
        private int[] currentShadowDrawableRadius;
        private int currentType;
        private boolean drawFullBubble;
        private Shader gradientShader;
        private boolean isBottomNear;
        public boolean isCrossfadeBackground;
        private final boolean isOut;
        public boolean isSelected;
        private boolean isTopNear;
        public boolean lastDrawWithShadow;
        private Matrix matrix;
        private int overrideRoundRadius;
        private Paint paint;
        private Path path;
        PathDrawParams pathDrawCacheParams;
        private RectF rect;
        private final ResourcesProvider resourcesProvider;
        private Paint selectedPaint;
        private Drawable[] shadowDrawable;
        private int[] shadowDrawableColor;
        public boolean themePreview;
        private int topY;
        Drawable transitionDrawable;
        int transitionDrawableColor;

        public int getOpacity() {
            return -2;
        }

        public void setColorFilter(int i, PorterDuff.Mode mode) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public MessageDrawable(int i, boolean z, boolean z2) {
            this(i, z, z2, (ResourcesProvider) null);
        }

        public MessageDrawable(int i, boolean z, boolean z2, ResourcesProvider resourcesProvider2) {
            this.paint = new Paint(1);
            this.rect = new RectF();
            this.matrix = new Matrix();
            this.backupRect = new Rect();
            this.currentShadowDrawableRadius = new int[]{-1, -1, -1, -1};
            this.shadowDrawable = new Drawable[4];
            this.shadowDrawableColor = new int[]{-1, -1, -1, -1};
            this.currentBackgroundDrawableRadius = new int[][]{new int[]{-1, -1, -1, -1}, new int[]{-1, -1, -1, -1}};
            this.backgroundDrawable = (Drawable[][]) Array.newInstance(Drawable.class, new int[]{2, 4});
            this.backgroundDrawableColor = new int[][]{new int[]{-1, -1, -1, -1}, new int[]{-1, -1, -1, -1}};
            this.resourcesProvider = resourcesProvider2;
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

        public void applyMatrixScale() {
            Bitmap bitmap;
            if (this.gradientShader instanceof BitmapShader) {
                char c = 1;
                char c2 = 2;
                if (!this.isCrossfadeBackground || (bitmap = this.crosfadeFromBitmap) == null) {
                    if (!this.themePreview) {
                        if (this.currentType != 2) {
                            c = 0;
                        }
                        c2 = c;
                    }
                    Bitmap bitmap2 = motionBackground[c2].getBitmap();
                    float min = 1.0f / Math.min(((float) bitmap2.getWidth()) / ((float) motionBackground[c2].getBounds().width()), ((float) bitmap2.getHeight()) / ((float) motionBackground[c2].getBounds().height()));
                    this.matrix.postScale(min, min);
                    return;
                }
                if (this.currentType != 2) {
                    c = 0;
                }
                float min2 = 1.0f / Math.min(((float) bitmap.getWidth()) / ((float) motionBackground[c].getBounds().width()), ((float) this.crosfadeFromBitmap.getHeight()) / ((float) motionBackground[c].getBounds().height()));
                this.matrix.postScale(min2, min2);
            }
        }

        public Shader getGradientShader() {
            return this.gradientShader;
        }

        public Matrix getMatrix() {
            return this.matrix;
        }

        /* access modifiers changed from: protected */
        public int getColor(String str) {
            if (this.currentType == 2) {
                return Theme.getColor(str);
            }
            ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
            return color != null ? color.intValue() : Theme.getColor(str);
        }

        /* access modifiers changed from: protected */
        public Integer getCurrentColor(String str) {
            if (this.currentType == 2) {
                return Integer.valueOf(Theme.getColor(str));
            }
            ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            return resourcesProvider2 != null ? resourcesProvider2.getCurrentColor(str) : (Integer) Theme.currentColors.get(str);
        }

        public void setTop(int i, int i2, int i3, boolean z, boolean z2) {
            setTop(i, i2, i3, i3, 0, 0, z, z2);
        }

        /* JADX WARNING: Removed duplicated region for block: B:107:0x0295  */
        /* JADX WARNING: Removed duplicated region for block: B:21:0x0066  */
        /* JADX WARNING: Removed duplicated region for block: B:23:0x006c  */
        /* JADX WARNING: Removed duplicated region for block: B:25:0x0072  */
        /* JADX WARNING: Removed duplicated region for block: B:27:0x0078  */
        /* JADX WARNING: Removed duplicated region for block: B:30:0x0081  */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x0083  */
        /* JADX WARNING: Removed duplicated region for block: B:42:0x009d  */
        /* JADX WARNING: Removed duplicated region for block: B:89:0x019b  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void setTop(int r35, int r36, int r37, int r38, int r39, int r40, boolean r41, boolean r42) {
            /*
                r34 = this;
                r0 = r34
                r10 = r37
                r11 = r39
                org.telegram.ui.ActionBar.Theme$MessageDrawable r1 = r0.crossfadeFromDrawable
                if (r1 == 0) goto L_0x001d
                r2 = r35
                r3 = r36
                r4 = r37
                r5 = r38
                r6 = r39
                r7 = r40
                r8 = r41
                r9 = r42
                r1.setTop(r2, r3, r4, r5, r6, r7, r8, r9)
            L_0x001d:
                boolean r1 = r0.isOut
                java.lang.String r2 = "chat_outBubble"
                r4 = 1
                r5 = 0
                if (r1 == 0) goto L_0x0053
                boolean r1 = r0.isSelected
                if (r1 == 0) goto L_0x002c
                java.lang.String r1 = "chat_outBubbleSelected"
                goto L_0x002d
            L_0x002c:
                r1 = r2
            L_0x002d:
                int r1 = r0.getColor(r1)
                java.lang.String r6 = "chat_outBubbleGradient"
                java.lang.Integer r6 = r0.getCurrentColor(r6)
                java.lang.String r7 = "chat_outBubbleGradient2"
                java.lang.Integer r7 = r0.getCurrentColor(r7)
                java.lang.String r8 = "chat_outBubbleGradient3"
                java.lang.Integer r8 = r0.getCurrentColor(r8)
                java.lang.String r9 = "chat_outBubbleGradientAnimated"
                java.lang.Integer r9 = r0.getCurrentColor(r9)
                if (r9 == 0) goto L_0x0063
                int r9 = r9.intValue()
                if (r9 == 0) goto L_0x0063
                r9 = 1
                goto L_0x0064
            L_0x0053:
                boolean r1 = r0.isSelected
                if (r1 == 0) goto L_0x005a
                java.lang.String r1 = "chat_inBubbleSelected"
                goto L_0x005c
            L_0x005a:
                java.lang.String r1 = "chat_inBubble"
            L_0x005c:
                int r1 = r0.getColor(r1)
                r6 = 0
                r7 = 0
                r8 = 0
            L_0x0063:
                r9 = 0
            L_0x0064:
                if (r6 == 0) goto L_0x006a
                int r1 = r0.getColor(r2)
            L_0x006a:
                if (r6 != 0) goto L_0x0070
                java.lang.Integer r6 = java.lang.Integer.valueOf(r5)
            L_0x0070:
                if (r7 != 0) goto L_0x0076
                java.lang.Integer r7 = java.lang.Integer.valueOf(r5)
            L_0x0076:
                if (r8 != 0) goto L_0x007c
                java.lang.Integer r8 = java.lang.Integer.valueOf(r5)
            L_0x007c:
                boolean r2 = r0.themePreview
                r12 = 2
                if (r2 == 0) goto L_0x0083
                r2 = 2
                goto L_0x008a
            L_0x0083:
                int r2 = r0.currentType
                if (r2 != r12) goto L_0x0089
                r2 = 1
                goto L_0x008a
            L_0x0089:
                r2 = 0
            L_0x008a:
                boolean r13 = r0.isCrossfadeBackground
                r14 = 3
                if (r13 != 0) goto L_0x00b3
                int r13 = r7.intValue()
                if (r13 == 0) goto L_0x00b3
                if (r9 == 0) goto L_0x00b3
                org.telegram.ui.Components.MotionBackgroundDrawable[] r13 = motionBackground
                r15 = r13[r2]
                if (r15 == 0) goto L_0x00b3
                r13 = r13[r2]
                int[] r13 = r13.getColors()
                r15 = r13[r5]
                r0.currentColor = r15
                r15 = r13[r4]
                r0.currentGradientColor1 = r15
                r15 = r13[r12]
                r0.currentGradientColor2 = r15
                r13 = r13[r14]
                r0.currentGradientColor3 = r13
            L_0x00b3:
                boolean r13 = r0.isCrossfadeBackground
                r15 = 1065353216(0x3var_, float:1.0)
                r3 = -1
                if (r13 == 0) goto L_0x016d
                int r13 = r7.intValue()
                if (r13 == 0) goto L_0x016d
                if (r9 == 0) goto L_0x016d
                int r13 = r0.currentBackgroundHeight
                if (r10 != r13) goto L_0x00ea
                android.graphics.Shader r13 = r0.crosfadeFromBitmapShader
                if (r13 == 0) goto L_0x00ea
                int r13 = r0.currentColor
                if (r13 != r1) goto L_0x00ea
                int r13 = r0.currentGradientColor1
                int r14 = r6.intValue()
                if (r13 != r14) goto L_0x00ea
                int r13 = r0.currentGradientColor2
                int r14 = r7.intValue()
                if (r13 != r14) goto L_0x00ea
                int r13 = r0.currentGradientColor3
                int r14 = r8.intValue()
                if (r13 != r14) goto L_0x00ea
                boolean r13 = r0.currentAnimateGradient
                if (r13 == r9) goto L_0x0147
            L_0x00ea:
                android.graphics.Bitmap r13 = r0.crosfadeFromBitmap
                if (r13 != 0) goto L_0x0105
                r13 = 60
                r14 = 80
                android.graphics.Bitmap$Config r5 = android.graphics.Bitmap.Config.ARGB_8888
                android.graphics.Bitmap r5 = android.graphics.Bitmap.createBitmap(r13, r14, r5)
                r0.crosfadeFromBitmap = r5
                android.graphics.BitmapShader r5 = new android.graphics.BitmapShader
                android.graphics.Bitmap r13 = r0.crosfadeFromBitmap
                android.graphics.Shader$TileMode r14 = android.graphics.Shader.TileMode.CLAMP
                r5.<init>(r13, r14, r14)
                r0.crosfadeFromBitmapShader = r5
            L_0x0105:
                org.telegram.ui.Components.MotionBackgroundDrawable[] r5 = motionBackground
                r13 = r5[r2]
                if (r13 != 0) goto L_0x0128
                org.telegram.ui.Components.MotionBackgroundDrawable r13 = new org.telegram.ui.Components.MotionBackgroundDrawable
                r13.<init>()
                r5[r2] = r13
                int r5 = r0.currentType
                if (r5 == r12) goto L_0x011d
                org.telegram.ui.Components.MotionBackgroundDrawable[] r5 = motionBackground
                r5 = r5[r2]
                r5.setPostInvalidateParent(r4)
            L_0x011d:
                org.telegram.ui.Components.MotionBackgroundDrawable[] r4 = motionBackground
                r4 = r4[r2]
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r15)
                r4.setRoundRadius(r5)
            L_0x0128:
                org.telegram.ui.Components.MotionBackgroundDrawable[] r4 = motionBackground
                r12 = r4[r2]
                int r14 = r6.intValue()
                int r15 = r7.intValue()
                int r16 = r8.intValue()
                android.graphics.Bitmap r4 = r0.crosfadeFromBitmap
                r13 = r1
                r17 = r4
                r12.setColors(r13, r14, r15, r16, r17)
                android.graphics.Shader r4 = r0.crosfadeFromBitmapShader
                android.graphics.Matrix r5 = r0.matrix
                r4.setLocalMatrix(r5)
            L_0x0147:
                android.graphics.Shader r4 = r0.crosfadeFromBitmapShader
                r0.gradientShader = r4
                android.graphics.Paint r5 = r0.paint
                r5.setShader(r4)
                android.graphics.Paint r4 = r0.paint
                r4.setColor(r3)
                r0.currentColor = r1
                r0.currentAnimateGradient = r9
                int r1 = r6.intValue()
                r0.currentGradientColor1 = r1
                int r1 = r7.intValue()
                r0.currentGradientColor2 = r1
                int r1 = r8.intValue()
                r0.currentGradientColor3 = r1
                goto L_0x02ac
            L_0x016d:
                int r5 = r6.intValue()
                if (r5 == 0) goto L_0x0295
                android.graphics.Shader r5 = r0.gradientShader
                if (r5 == 0) goto L_0x019b
                int r5 = r0.currentBackgroundHeight
                if (r10 != r5) goto L_0x019b
                int r5 = r0.currentColor
                if (r5 != r1) goto L_0x019b
                int r5 = r0.currentGradientColor1
                int r13 = r6.intValue()
                if (r5 != r13) goto L_0x019b
                int r5 = r0.currentGradientColor2
                int r13 = r7.intValue()
                if (r5 != r13) goto L_0x019b
                int r5 = r0.currentGradientColor3
                int r13 = r8.intValue()
                if (r5 != r13) goto L_0x019b
                boolean r5 = r0.currentAnimateGradient
                if (r5 == r9) goto L_0x0295
            L_0x019b:
                int r5 = r7.intValue()
                if (r5 == 0) goto L_0x01e5
                if (r9 == 0) goto L_0x01e5
                org.telegram.ui.Components.MotionBackgroundDrawable[] r5 = motionBackground
                r13 = r5[r2]
                if (r13 != 0) goto L_0x01c6
                org.telegram.ui.Components.MotionBackgroundDrawable r13 = new org.telegram.ui.Components.MotionBackgroundDrawable
                r13.<init>()
                r5[r2] = r13
                int r5 = r0.currentType
                if (r5 == r12) goto L_0x01bb
                org.telegram.ui.Components.MotionBackgroundDrawable[] r5 = motionBackground
                r5 = r5[r2]
                r5.setPostInvalidateParent(r4)
            L_0x01bb:
                org.telegram.ui.Components.MotionBackgroundDrawable[] r4 = motionBackground
                r4 = r4[r2]
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r15)
                r4.setRoundRadius(r5)
            L_0x01c6:
                org.telegram.ui.Components.MotionBackgroundDrawable[] r4 = motionBackground
                r4 = r4[r2]
                int r5 = r6.intValue()
                int r12 = r7.intValue()
                int r13 = r8.intValue()
                r4.setColors(r1, r5, r12, r13)
                org.telegram.ui.Components.MotionBackgroundDrawable[] r4 = motionBackground
                r4 = r4[r2]
                android.graphics.BitmapShader r4 = r4.getBitmapShader()
                r0.gradientShader = r4
                goto L_0x0272
            L_0x01e5:
                int r5 = r7.intValue()
                if (r5 == 0) goto L_0x024e
                int r5 = r8.intValue()
                if (r5 == 0) goto L_0x0223
                r5 = 4
                int[] r5 = new int[r5]
                int r13 = r8.intValue()
                r15 = 0
                r5[r15] = r13
                int r13 = r7.intValue()
                r5[r4] = r13
                int r4 = r6.intValue()
                r5[r12] = r4
                r5[r14] = r1
                android.graphics.LinearGradient r4 = new android.graphics.LinearGradient
                r19 = 0
                float r12 = (float) r11
                r21 = 0
                float r13 = (float) r10
                r24 = 0
                android.graphics.Shader$TileMode r25 = android.graphics.Shader.TileMode.CLAMP
                r18 = r4
                r20 = r12
                r22 = r13
                r23 = r5
                r18.<init>(r19, r20, r21, r22, r23, r24, r25)
                r0.gradientShader = r4
                goto L_0x0272
            L_0x0223:
                int[] r5 = new int[r14]
                int r13 = r7.intValue()
                r14 = 0
                r5[r14] = r13
                int r13 = r6.intValue()
                r5[r4] = r13
                r5[r12] = r1
                android.graphics.LinearGradient r4 = new android.graphics.LinearGradient
                r27 = 0
                float r12 = (float) r11
                r29 = 0
                float r13 = (float) r10
                r32 = 0
                android.graphics.Shader$TileMode r33 = android.graphics.Shader.TileMode.CLAMP
                r26 = r4
                r28 = r12
                r30 = r13
                r31 = r5
                r26.<init>(r27, r28, r29, r30, r31, r32, r33)
                r0.gradientShader = r4
                goto L_0x0272
            L_0x024e:
                int[] r5 = new int[r12]
                int r12 = r6.intValue()
                r13 = 0
                r5[r13] = r12
                r5[r4] = r1
                android.graphics.LinearGradient r4 = new android.graphics.LinearGradient
                r19 = 0
                float r12 = (float) r11
                r21 = 0
                float r13 = (float) r10
                r24 = 0
                android.graphics.Shader$TileMode r25 = android.graphics.Shader.TileMode.CLAMP
                r18 = r4
                r20 = r12
                r22 = r13
                r23 = r5
                r18.<init>(r19, r20, r21, r22, r23, r24, r25)
                r0.gradientShader = r4
            L_0x0272:
                android.graphics.Paint r4 = r0.paint
                android.graphics.Shader r5 = r0.gradientShader
                r4.setShader(r5)
                r0.currentColor = r1
                r0.currentAnimateGradient = r9
                int r1 = r6.intValue()
                r0.currentGradientColor1 = r1
                int r1 = r7.intValue()
                r0.currentGradientColor2 = r1
                int r1 = r8.intValue()
                r0.currentGradientColor3 = r1
                android.graphics.Paint r1 = r0.paint
                r1.setColor(r3)
                goto L_0x02ac
            L_0x0295:
                int r3 = r6.intValue()
                if (r3 != 0) goto L_0x02ac
                android.graphics.Shader r3 = r0.gradientShader
                if (r3 == 0) goto L_0x02a7
                r3 = 0
                r0.gradientShader = r3
                android.graphics.Paint r4 = r0.paint
                r4.setShader(r3)
            L_0x02a7:
                android.graphics.Paint r3 = r0.paint
                r3.setColor(r1)
            L_0x02ac:
                android.graphics.Shader r1 = r0.gradientShader
                boolean r1 = r1 instanceof android.graphics.BitmapShader
                if (r1 == 0) goto L_0x02bf
                org.telegram.ui.Components.MotionBackgroundDrawable[] r1 = motionBackground
                r1 = r1[r2]
                int r2 = r10 - r38
                r3 = r36
                r4 = 0
                r1.setBounds(r4, r11, r3, r2)
                goto L_0x02c0
            L_0x02bf:
                r4 = 0
            L_0x02c0:
                r0.currentBackgroundHeight = r10
                android.graphics.Shader r1 = r0.gradientShader
                boolean r1 = r1 instanceof android.graphics.BitmapShader
                if (r1 == 0) goto L_0x02ca
                r4 = r38
            L_0x02ca:
                int r1 = r35 - r4
                r0.topY = r1
                r1 = r41
                r0.isTopNear = r1
                r1 = r42
                r0.isBottomNear = r1
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.MessageDrawable.setTop(int, int, int, int, int, int, boolean, boolean):void");
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

        /* JADX WARNING: Removed duplicated region for block: B:48:0x0160  */
        /* JADX WARNING: Removed duplicated region for block: B:53:0x016e  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public android.graphics.drawable.Drawable getBackgroundDrawable() {
            /*
                r21 = this;
                r0 = r21
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
                android.graphics.Shader r7 = r0.gradientShader
                if (r7 != 0) goto L_0x002f
                if (r6 != 0) goto L_0x002f
                boolean r7 = r0.isCrossfadeBackground
                if (r7 != 0) goto L_0x002f
                r7 = 1
                goto L_0x0030
            L_0x002f:
                r7 = 0
            L_0x0030:
                boolean r8 = r0.isOut
                if (r8 == 0) goto L_0x0037
                java.lang.String r8 = "chat_outBubbleShadow"
                goto L_0x0039
            L_0x0037:
                java.lang.String r8 = "chat_inBubbleShadow"
            L_0x0039:
                int r8 = r0.getColor(r8)
                boolean r9 = r0.lastDrawWithShadow
                if (r9 != r7) goto L_0x0055
                int[][] r9 = r0.currentBackgroundDrawableRadius
                r9 = r9[r6]
                r9 = r9[r2]
                if (r9 != r1) goto L_0x0055
                if (r7 == 0) goto L_0x0052
                int[] r9 = r0.shadowDrawableColor
                r9 = r9[r2]
                if (r9 == r8) goto L_0x0052
                goto L_0x0055
            L_0x0052:
                r4 = 0
                goto L_0x015a
            L_0x0055:
                int[][] r9 = r0.currentBackgroundDrawableRadius
                r9 = r9[r6]
                r9[r2] = r1
                r1 = 1112014848(0x42480000, float:50.0)
                int r1 = r0.dp(r1)     // Catch:{ all -> 0x0052 }
                r9 = 1109393408(0x42200000, float:40.0)
                int r10 = r0.dp(r9)     // Catch:{ all -> 0x0052 }
                android.graphics.Bitmap$Config r11 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0052 }
                android.graphics.Bitmap r1 = android.graphics.Bitmap.createBitmap(r1, r10, r11)     // Catch:{ all -> 0x0052 }
                android.graphics.Canvas r10 = new android.graphics.Canvas     // Catch:{ all -> 0x0052 }
                r10.<init>(r1)     // Catch:{ all -> 0x0052 }
                android.graphics.Rect r11 = r0.backupRect     // Catch:{ all -> 0x0052 }
                android.graphics.Rect r12 = r21.getBounds()     // Catch:{ all -> 0x0052 }
                r11.set(r12)     // Catch:{ all -> 0x0052 }
                r11 = -1
                if (r7 == 0) goto L_0x010b
                int[] r12 = r0.shadowDrawableColor     // Catch:{ all -> 0x0052 }
                r12[r2] = r8     // Catch:{ all -> 0x0052 }
                android.graphics.Paint r12 = new android.graphics.Paint     // Catch:{ all -> 0x0052 }
                r12.<init>(r4)     // Catch:{ all -> 0x0052 }
                android.graphics.LinearGradient r15 = new android.graphics.LinearGradient     // Catch:{ all -> 0x0052 }
                r14 = 0
                r16 = 0
                r17 = 0
                int r9 = r0.dp(r9)     // Catch:{ all -> 0x0052 }
                float r9 = (float) r9     // Catch:{ all -> 0x0052 }
                int[] r13 = new int[r3]     // Catch:{ all -> 0x0052 }
                r18 = 358573417(0x155var_, float:4.511449E-26)
                r13[r5] = r18     // Catch:{ all -> 0x0052 }
                r18 = 694117737(0x295var_, float:4.9603906E-14)
                r13[r4] = r18     // Catch:{ all -> 0x0052 }
                r19 = 0
                android.graphics.Shader$TileMode r20 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x0052 }
                r18 = r13
                r13 = r15
                r3 = r15
                r15 = r16
                r16 = r17
                r17 = r9
                r13.<init>(r14, r15, r16, r17, r18, r19, r20)     // Catch:{ all -> 0x0052 }
                r12.setShader(r3)     // Catch:{ all -> 0x0052 }
                android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter     // Catch:{ all -> 0x0052 }
                android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY     // Catch:{ all -> 0x0052 }
                r3.<init>(r8, r9)     // Catch:{ all -> 0x0052 }
                r12.setColorFilter(r3)     // Catch:{ all -> 0x0052 }
                r3 = 1073741824(0x40000000, float:2.0)
                r8 = 1065353216(0x3var_, float:1.0)
                r9 = 0
                r12.setShadowLayer(r3, r9, r8, r11)     // Catch:{ all -> 0x0052 }
                float r3 = org.telegram.messenger.AndroidUtilities.density     // Catch:{ all -> 0x0052 }
                int r3 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
                if (r3 <= 0) goto L_0x00d9
                int r3 = r1.getWidth()     // Catch:{ all -> 0x0052 }
                int r3 = r3 + r4
                int r13 = r1.getHeight()     // Catch:{ all -> 0x0052 }
                int r13 = r13 + r4
                r0.setBounds(r11, r11, r3, r13)     // Catch:{ all -> 0x0052 }
                goto L_0x00e4
            L_0x00d9:
                int r3 = r1.getWidth()     // Catch:{ all -> 0x0052 }
                int r13 = r1.getHeight()     // Catch:{ all -> 0x0052 }
                r0.setBounds(r5, r5, r3, r13)     // Catch:{ all -> 0x0052 }
            L_0x00e4:
                r0.draw(r10, r12)     // Catch:{ all -> 0x0052 }
                float r3 = org.telegram.messenger.AndroidUtilities.density     // Catch:{ all -> 0x0052 }
                int r3 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
                if (r3 <= 0) goto L_0x010b
                r12.setColor(r5)     // Catch:{ all -> 0x0052 }
                r12.setShadowLayer(r9, r9, r9, r5)     // Catch:{ all -> 0x0052 }
                android.graphics.PorterDuffXfermode r3 = new android.graphics.PorterDuffXfermode     // Catch:{ all -> 0x0052 }
                android.graphics.PorterDuff$Mode r8 = android.graphics.PorterDuff.Mode.CLEAR     // Catch:{ all -> 0x0052 }
                r3.<init>(r8)     // Catch:{ all -> 0x0052 }
                r12.setXfermode(r3)     // Catch:{ all -> 0x0052 }
                int r3 = r1.getWidth()     // Catch:{ all -> 0x0052 }
                int r8 = r1.getHeight()     // Catch:{ all -> 0x0052 }
                r0.setBounds(r5, r5, r3, r8)     // Catch:{ all -> 0x0052 }
                r0.draw(r10, r12)     // Catch:{ all -> 0x0052 }
            L_0x010b:
                android.graphics.Paint r3 = new android.graphics.Paint     // Catch:{ all -> 0x0052 }
                r3.<init>(r4)     // Catch:{ all -> 0x0052 }
                r3.setColor(r11)     // Catch:{ all -> 0x0052 }
                int r8 = r1.getWidth()     // Catch:{ all -> 0x0052 }
                int r9 = r1.getHeight()     // Catch:{ all -> 0x0052 }
                r0.setBounds(r5, r5, r8, r9)     // Catch:{ all -> 0x0052 }
                r0.draw(r10, r3)     // Catch:{ all -> 0x0052 }
                android.graphics.drawable.Drawable[][] r3 = r0.backgroundDrawable     // Catch:{ all -> 0x0052 }
                r3 = r3[r6]     // Catch:{ all -> 0x0052 }
                android.graphics.drawable.NinePatchDrawable r8 = new android.graphics.drawable.NinePatchDrawable     // Catch:{ all -> 0x0052 }
                int r9 = r1.getWidth()     // Catch:{ all -> 0x0052 }
                r10 = 2
                int r9 = r9 / r10
                int r9 = r9 - r4
                int r11 = r1.getWidth()     // Catch:{ all -> 0x0052 }
                int r11 = r11 / r10
                int r11 = r11 + r4
                int r12 = r1.getHeight()     // Catch:{ all -> 0x0052 }
                int r12 = r12 / r10
                int r12 = r12 - r4
                int r13 = r1.getHeight()     // Catch:{ all -> 0x0052 }
                int r13 = r13 / r10
                int r13 = r13 + r4
                java.nio.ByteBuffer r9 = getByteBuffer(r9, r11, r12, r13)     // Catch:{ all -> 0x0052 }
                byte[] r9 = r9.array()     // Catch:{ all -> 0x0052 }
                android.graphics.Rect r10 = new android.graphics.Rect     // Catch:{ all -> 0x0052 }
                r10.<init>()     // Catch:{ all -> 0x0052 }
                r11 = 0
                r8.<init>(r1, r9, r10, r11)     // Catch:{ all -> 0x0052 }
                r3[r2] = r8     // Catch:{ all -> 0x0052 }
                android.graphics.Rect r1 = r0.backupRect     // Catch:{ all -> 0x0159 }
                r0.setBounds(r1)     // Catch:{ all -> 0x0159 }
                goto L_0x015a
            L_0x0159:
            L_0x015a:
                r0.lastDrawWithShadow = r7
                boolean r1 = r0.isSelected
                if (r1 == 0) goto L_0x016e
                boolean r1 = r0.isOut
                if (r1 == 0) goto L_0x0167
                java.lang.String r1 = "chat_outBubbleSelected"
                goto L_0x0169
            L_0x0167:
                java.lang.String r1 = "chat_inBubbleSelected"
            L_0x0169:
                int r1 = r0.getColor(r1)
                goto L_0x017b
            L_0x016e:
                boolean r1 = r0.isOut
                if (r1 == 0) goto L_0x0175
                java.lang.String r1 = "chat_outBubble"
                goto L_0x0177
            L_0x0175:
                java.lang.String r1 = "chat_inBubble"
            L_0x0177:
                int r1 = r0.getColor(r1)
            L_0x017b:
                android.graphics.drawable.Drawable[][] r3 = r0.backgroundDrawable
                r5 = r3[r6]
                r5 = r5[r2]
                if (r5 == 0) goto L_0x01a1
                int[][] r5 = r0.backgroundDrawableColor
                r5 = r5[r6]
                r5 = r5[r2]
                if (r5 != r1) goto L_0x018d
                if (r4 == 0) goto L_0x01a1
            L_0x018d:
                r3 = r3[r6]
                r3 = r3[r2]
                android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
                android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
                r4.<init>(r1, r5)
                r3.setColorFilter(r4)
                int[][] r3 = r0.backgroundDrawableColor
                r3 = r3[r6]
                r3[r2] = r1
            L_0x01a1:
                android.graphics.drawable.Drawable[][] r1 = r0.backgroundDrawable
                r1 = r1[r6]
                r1 = r1[r2]
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.MessageDrawable.getBackgroundDrawable():android.graphics.drawable.Drawable");
        }

        public Drawable getTransitionDrawable(int i) {
            if (this.transitionDrawable == null) {
                Bitmap createBitmap = Bitmap.createBitmap(dp(50.0f), dp(40.0f), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                this.backupRect.set(getBounds());
                Paint paint2 = new Paint(1);
                paint2.setColor(-1);
                setBounds(0, 0, createBitmap.getWidth(), createBitmap.getHeight());
                draw(canvas, paint2);
                this.transitionDrawable = new NinePatchDrawable(createBitmap, getByteBuffer((createBitmap.getWidth() / 2) - 1, (createBitmap.getWidth() / 2) + 1, (createBitmap.getHeight() / 2) - 1, (createBitmap.getHeight() / 2) + 1).array(), new Rect(), (String) null);
                setBounds(this.backupRect);
            }
            if (this.transitionDrawableColor != i) {
                this.transitionDrawableColor = i;
                this.transitionDrawable.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
            }
            return this.transitionDrawable;
        }

        public MotionBackgroundDrawable getMotionBackgroundDrawable() {
            if (this.themePreview) {
                return motionBackground[2];
            }
            return motionBackground[this.currentType == 2 ? (char) 1 : 0];
        }

        public Drawable getShadowDrawable() {
            if (this.isCrossfadeBackground) {
                return null;
            }
            if (this.gradientShader == null && !this.isSelected && this.crossfadeFromDrawable == null) {
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

        public void drawCached(Canvas canvas, PathDrawParams pathDrawParams, Paint paint2) {
            this.pathDrawCacheParams = pathDrawParams;
            MessageDrawable messageDrawable = this.crossfadeFromDrawable;
            if (messageDrawable != null) {
                messageDrawable.pathDrawCacheParams = pathDrawParams;
            }
            draw(canvas, paint2);
            this.pathDrawCacheParams = null;
            MessageDrawable messageDrawable2 = this.crossfadeFromDrawable;
            if (messageDrawable2 != null) {
                messageDrawable2.pathDrawCacheParams = null;
            }
        }

        public void drawCached(Canvas canvas, PathDrawParams pathDrawParams) {
            drawCached(canvas, pathDrawParams, (Paint) null);
        }

        public void draw(Canvas canvas) {
            MessageDrawable messageDrawable = this.crossfadeFromDrawable;
            if (messageDrawable != null) {
                messageDrawable.draw(canvas);
                setAlpha((int) (this.crossfadeProgress * 255.0f));
                draw(canvas, (Paint) null);
                setAlpha(255);
                return;
            }
            draw(canvas, (Paint) null);
        }

        /* JADX WARNING: Removed duplicated region for block: B:43:0x00b7  */
        /* JADX WARNING: Removed duplicated region for block: B:44:0x00be  */
        /* JADX WARNING: Removed duplicated region for block: B:46:0x00c3  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void draw(android.graphics.Canvas r22, android.graphics.Paint r23) {
            /*
                r21 = this;
                r0 = r21
                r1 = r22
                android.graphics.Rect r2 = r21.getBounds()
                if (r23 != 0) goto L_0x001b
                android.graphics.Shader r3 = r0.gradientShader
                if (r3 != 0) goto L_0x001b
                android.graphics.drawable.Drawable r3 = r21.getBackgroundDrawable()
                if (r3 == 0) goto L_0x001b
                r3.setBounds(r2)
                r3.draw(r1)
                return
            L_0x001b:
                r3 = 1073741824(0x40000000, float:2.0)
                int r4 = r0.dp(r3)
                int r5 = r0.overrideRoundRadius
                r6 = 1086324736(0x40CLASSNAME, float:6.0)
                r7 = 2
                if (r5 == 0) goto L_0x002a
                r8 = r5
                goto L_0x004a
            L_0x002a:
                int r5 = r0.currentType
                if (r5 != r7) goto L_0x0037
                int r5 = r0.dp(r6)
                int r8 = r0.dp(r6)
                goto L_0x004a
            L_0x0037:
                int r5 = org.telegram.messenger.SharedConfig.bubbleRadius
                float r5 = (float) r5
                int r5 = r0.dp(r5)
                r8 = 5
                int r9 = org.telegram.messenger.SharedConfig.bubbleRadius
                int r8 = java.lang.Math.min(r8, r9)
                float r8 = (float) r8
                int r8 = r0.dp(r8)
            L_0x004a:
                int r6 = r0.dp(r6)
                if (r23 != 0) goto L_0x0053
                android.graphics.Paint r9 = r0.paint
                goto L_0x0055
            L_0x0053:
                r9 = r23
            L_0x0055:
                r10 = 0
                if (r23 != 0) goto L_0x0074
                android.graphics.Shader r11 = r0.gradientShader
                if (r11 == 0) goto L_0x0074
                android.graphics.Matrix r11 = r0.matrix
                r11.reset()
                r21.applyMatrixScale()
                android.graphics.Matrix r11 = r0.matrix
                int r12 = r0.topY
                int r12 = -r12
                float r12 = (float) r12
                r11.postTranslate(r10, r12)
                android.graphics.Shader r11 = r0.gradientShader
                android.graphics.Matrix r12 = r0.matrix
                r11.setLocalMatrix(r12)
            L_0x0074:
                int r11 = r2.top
                r12 = 0
                int r11 = java.lang.Math.max(r11, r12)
                org.telegram.ui.ActionBar.Theme$MessageDrawable$PathDrawParams r13 = r0.pathDrawCacheParams
                r14 = 1
                if (r13 == 0) goto L_0x008b
                int r13 = r2.height()
                int r15 = r0.currentBackgroundHeight
                if (r13 >= r15) goto L_0x008b
                r13 = 1
            L_0x0089:
                r15 = 1
                goto L_0x00b3
            L_0x008b:
                int r13 = r0.currentType
                if (r13 != r14) goto L_0x009c
                int r13 = r0.topY
                int r15 = r2.bottom
                int r13 = r13 + r15
                int r15 = r6 * 2
                int r13 = r13 - r15
                int r15 = r0.currentBackgroundHeight
                if (r13 >= r15) goto L_0x00a8
                goto L_0x00a6
            L_0x009c:
                int r13 = r0.topY
                int r15 = r2.bottom
                int r13 = r13 + r15
                int r13 = r13 - r5
                int r15 = r0.currentBackgroundHeight
                if (r13 >= r15) goto L_0x00a8
            L_0x00a6:
                r13 = 1
                goto L_0x00a9
            L_0x00a8:
                r13 = 0
            L_0x00a9:
                int r15 = r0.topY
                int r16 = r5 * 2
                int r15 = r15 + r16
                if (r15 < 0) goto L_0x00b2
                goto L_0x0089
            L_0x00b2:
                r15 = 0
            L_0x00b3:
                org.telegram.ui.ActionBar.Theme$MessageDrawable$PathDrawParams r10 = r0.pathDrawCacheParams
                if (r10 == 0) goto L_0x00be
                android.graphics.Path r3 = r10.path
                boolean r10 = r10.invalidatePath(r2, r13, r15)
                goto L_0x00c1
            L_0x00be:
                android.graphics.Path r3 = r0.path
                r10 = 1
            L_0x00c1:
                if (r10 == 0) goto L_0x0558
                r3.reset()
                boolean r10 = r0.isOut
                r12 = 1090519040(0x41000000, float:8.0)
                if (r10 == 0) goto L_0x0311
                boolean r10 = r0.drawFullBubble
                if (r10 != 0) goto L_0x00ff
                int r10 = r0.currentType
                if (r10 == r7) goto L_0x00ff
                if (r23 != 0) goto L_0x00ff
                if (r13 == 0) goto L_0x00d9
                goto L_0x00ff
            L_0x00d9:
                int r10 = r2.right
                int r18 = r0.dp(r12)
                int r10 = r10 - r18
                float r10 = (float) r10
                int r7 = r0.topY
                int r7 = r11 - r7
                int r12 = r0.currentBackgroundHeight
                int r7 = r7 + r12
                float r7 = (float) r7
                r3.moveTo(r10, r7)
                int r7 = r2.left
                int r7 = r7 + r4
                float r7 = (float) r7
                int r10 = r0.topY
                int r10 = r11 - r10
                int r12 = r0.currentBackgroundHeight
                int r10 = r10 + r12
                float r10 = (float) r10
                r3.lineTo(r7, r10)
                r20 = r8
                goto L_0x0157
            L_0x00ff:
                int r7 = r0.currentType
                if (r7 != r14) goto L_0x0116
                int r7 = r2.right
                r10 = 1090519040(0x41000000, float:8.0)
                int r12 = r0.dp(r10)
                int r7 = r7 - r12
                int r7 = r7 - r5
                float r7 = (float) r7
                int r10 = r2.bottom
                int r10 = r10 - r4
                float r10 = (float) r10
                r3.moveTo(r7, r10)
                goto L_0x0128
            L_0x0116:
                int r7 = r2.right
                r10 = 1076258406(0x40266666, float:2.6)
                int r10 = r0.dp(r10)
                int r7 = r7 - r10
                float r7 = (float) r7
                int r10 = r2.bottom
                int r10 = r10 - r4
                float r10 = (float) r10
                r3.moveTo(r7, r10)
            L_0x0128:
                int r7 = r2.left
                int r7 = r7 + r4
                int r7 = r7 + r5
                float r7 = (float) r7
                int r10 = r2.bottom
                int r10 = r10 - r4
                float r10 = (float) r10
                r3.lineTo(r7, r10)
                android.graphics.RectF r7 = r0.rect
                int r10 = r2.left
                int r12 = r10 + r4
                float r12 = (float) r12
                int r14 = r2.bottom
                int r18 = r14 - r4
                int r19 = r5 * 2
                r20 = r8
                int r8 = r18 - r19
                float r8 = (float) r8
                int r10 = r10 + r4
                int r10 = r10 + r19
                float r10 = (float) r10
                int r14 = r14 - r4
                float r14 = (float) r14
                r7.set(r12, r8, r10, r14)
                android.graphics.RectF r7 = r0.rect
                r8 = 1119092736(0x42b40000, float:90.0)
                r10 = 0
                r3.arcTo(r7, r8, r8, r10)
            L_0x0157:
                boolean r7 = r0.drawFullBubble
                if (r7 != 0) goto L_0x01a9
                int r7 = r0.currentType
                r8 = 2
                if (r7 == r8) goto L_0x01a9
                if (r23 != 0) goto L_0x01a9
                if (r15 == 0) goto L_0x0165
                goto L_0x01a9
            L_0x0165:
                int r7 = r2.left
                int r7 = r7 + r4
                float r7 = (float) r7
                int r8 = r0.topY
                int r8 = r11 - r8
                r10 = 1073741824(0x40000000, float:2.0)
                int r12 = r0.dp(r10)
                int r8 = r8 - r12
                float r8 = (float) r8
                r3.lineTo(r7, r8)
                int r7 = r0.currentType
                r8 = 1
                if (r7 != r8) goto L_0x0190
                int r7 = r2.right
                int r7 = r7 - r4
                float r7 = (float) r7
                int r8 = r0.topY
                int r8 = r11 - r8
                int r10 = r0.dp(r10)
                int r8 = r8 - r10
                float r8 = (float) r8
                r3.lineTo(r7, r8)
                goto L_0x0247
            L_0x0190:
                int r7 = r2.right
                r8 = 1090519040(0x41000000, float:8.0)
                int r12 = r0.dp(r8)
                int r7 = r7 - r12
                float r7 = (float) r7
                int r8 = r0.topY
                int r8 = r11 - r8
                int r10 = r0.dp(r10)
                int r8 = r8 - r10
                float r8 = (float) r8
                r3.lineTo(r7, r8)
                goto L_0x0247
            L_0x01a9:
                int r7 = r2.left
                int r7 = r7 + r4
                float r7 = (float) r7
                int r8 = r2.top
                int r8 = r8 + r4
                int r8 = r8 + r5
                float r8 = (float) r8
                r3.lineTo(r7, r8)
                android.graphics.RectF r7 = r0.rect
                int r8 = r2.left
                int r10 = r8 + r4
                float r10 = (float) r10
                int r12 = r2.top
                int r14 = r12 + r4
                float r14 = (float) r14
                int r8 = r8 + r4
                int r15 = r5 * 2
                int r8 = r8 + r15
                float r8 = (float) r8
                int r12 = r12 + r4
                int r12 = r12 + r15
                float r12 = (float) r12
                r7.set(r10, r14, r8, r12)
                android.graphics.RectF r7 = r0.rect
                r8 = 1127481344(0x43340000, float:180.0)
                r10 = 1119092736(0x42b40000, float:90.0)
                r12 = 0
                r3.arcTo(r7, r8, r10, r12)
                boolean r7 = r0.isTopNear
                if (r7 == 0) goto L_0x01dd
                r7 = r20
                goto L_0x01de
            L_0x01dd:
                r7 = r5
            L_0x01de:
                int r8 = r0.currentType
                r10 = 1
                if (r8 != r10) goto L_0x0208
                int r8 = r2.right
                int r8 = r8 - r4
                int r8 = r8 - r7
                float r8 = (float) r8
                int r10 = r2.top
                int r10 = r10 + r4
                float r10 = (float) r10
                r3.lineTo(r8, r10)
                android.graphics.RectF r8 = r0.rect
                int r10 = r2.right
                int r12 = r10 - r4
                r14 = 2
                int r7 = r7 * 2
                int r12 = r12 - r7
                float r12 = (float) r12
                int r14 = r2.top
                int r15 = r14 + r4
                float r15 = (float) r15
                int r10 = r10 - r4
                float r10 = (float) r10
                int r14 = r14 + r4
                int r14 = r14 + r7
                float r7 = (float) r14
                r8.set(r12, r15, r10, r7)
                goto L_0x023d
            L_0x0208:
                int r8 = r2.right
                r10 = 1090519040(0x41000000, float:8.0)
                int r12 = r0.dp(r10)
                int r8 = r8 - r12
                int r8 = r8 - r7
                float r8 = (float) r8
                int r12 = r2.top
                int r12 = r12 + r4
                float r12 = (float) r12
                r3.lineTo(r8, r12)
                android.graphics.RectF r8 = r0.rect
                int r12 = r2.right
                int r14 = r0.dp(r10)
                int r12 = r12 - r14
                r14 = 2
                int r7 = r7 * 2
                int r12 = r12 - r7
                float r12 = (float) r12
                int r14 = r2.top
                int r14 = r14 + r4
                float r14 = (float) r14
                int r15 = r2.right
                int r17 = r0.dp(r10)
                int r15 = r15 - r17
                float r10 = (float) r15
                int r15 = r2.top
                int r15 = r15 + r4
                int r15 = r15 + r7
                float r7 = (float) r15
                r8.set(r12, r14, r10, r7)
            L_0x023d:
                android.graphics.RectF r7 = r0.rect
                r8 = 1132920832(0x43870000, float:270.0)
                r10 = 1119092736(0x42b40000, float:90.0)
                r12 = 0
                r3.arcTo(r7, r8, r10, r12)
            L_0x0247:
                int r7 = r0.currentType
                r8 = 1
                if (r7 != r8) goto L_0x0296
                if (r23 != 0) goto L_0x0261
                if (r13 == 0) goto L_0x0251
                goto L_0x0261
            L_0x0251:
                int r2 = r2.right
                int r2 = r2 - r4
                float r2 = (float) r2
                int r4 = r0.topY
                int r11 = r11 - r4
                int r4 = r0.currentBackgroundHeight
                int r11 = r11 + r4
                float r4 = (float) r11
                r3.lineTo(r2, r4)
                goto L_0x0555
            L_0x0261:
                boolean r6 = r0.isBottomNear
                if (r6 == 0) goto L_0x0267
                r5 = r20
            L_0x0267:
                int r6 = r2.right
                int r6 = r6 - r4
                float r6 = (float) r6
                int r7 = r2.bottom
                int r7 = r7 - r4
                int r7 = r7 - r5
                float r7 = (float) r7
                r3.lineTo(r6, r7)
                android.graphics.RectF r6 = r0.rect
                int r7 = r2.right
                int r8 = r7 - r4
                r10 = 2
                int r5 = r5 * 2
                int r8 = r8 - r5
                float r8 = (float) r8
                int r2 = r2.bottom
                int r10 = r2 - r4
                int r10 = r10 - r5
                float r5 = (float) r10
                int r7 = r7 - r4
                float r7 = (float) r7
                int r2 = r2 - r4
                float r2 = (float) r2
                r6.set(r8, r5, r7, r2)
                android.graphics.RectF r2 = r0.rect
                r4 = 0
                r5 = 1119092736(0x42b40000, float:90.0)
                r6 = 0
                r3.arcTo(r2, r4, r5, r6)
                goto L_0x0555
            L_0x0296:
                boolean r5 = r0.drawFullBubble
                if (r5 != 0) goto L_0x02b8
                r5 = 2
                if (r7 == r5) goto L_0x02b8
                if (r23 != 0) goto L_0x02b8
                if (r13 == 0) goto L_0x02a2
                goto L_0x02b8
            L_0x02a2:
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
                goto L_0x0555
            L_0x02b8:
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
                android.graphics.RectF r5 = r0.rect
                int r7 = r2.right
                r8 = 1090519040(0x41000000, float:8.0)
                int r8 = r0.dp(r8)
                int r7 = r7 - r8
                float r7 = (float) r7
                int r8 = r2.bottom
                int r8 = r8 - r4
                r10 = 2
                int r6 = r6 * 2
                int r8 = r8 - r6
                r10 = 1091567616(0x41100000, float:9.0)
                int r10 = r0.dp(r10)
                int r8 = r8 - r10
                float r8 = (float) r8
                int r10 = r2.right
                r11 = 1088421888(0x40e00000, float:7.0)
                int r11 = r0.dp(r11)
                int r10 = r10 - r11
                int r10 = r10 + r6
                float r6 = (float) r10
                int r2 = r2.bottom
                int r2 = r2 - r4
                r4 = 1065353216(0x3var_, float:1.0)
                int r4 = r0.dp(r4)
                int r2 = r2 - r4
                float r2 = (float) r2
                r5.set(r7, r8, r6, r2)
                android.graphics.RectF r2 = r0.rect
                r4 = -1029308416(0xffffffffc2a60000, float:-83.0)
                r5 = 1127481344(0x43340000, float:180.0)
                r6 = 0
                r3.arcTo(r2, r5, r4, r6)
                goto L_0x0555
            L_0x0311:
                r20 = r8
                boolean r7 = r0.drawFullBubble
                if (r7 != 0) goto L_0x0346
                int r7 = r0.currentType
                r10 = 2
                if (r7 == r10) goto L_0x0346
                if (r23 != 0) goto L_0x0346
                if (r13 == 0) goto L_0x0321
                goto L_0x0346
            L_0x0321:
                int r7 = r2.left
                r10 = 1090519040(0x41000000, float:8.0)
                int r12 = r0.dp(r10)
                int r7 = r7 + r12
                float r7 = (float) r7
                int r10 = r0.topY
                int r10 = r11 - r10
                int r12 = r0.currentBackgroundHeight
                int r10 = r10 + r12
                float r10 = (float) r10
                r3.moveTo(r7, r10)
                int r7 = r2.right
                int r7 = r7 - r4
                float r7 = (float) r7
                int r10 = r0.topY
                int r10 = r11 - r10
                int r12 = r0.currentBackgroundHeight
                int r10 = r10 + r12
                float r10 = (float) r10
                r3.lineTo(r7, r10)
                goto L_0x039e
            L_0x0346:
                int r7 = r0.currentType
                r10 = 1
                if (r7 != r10) goto L_0x035e
                int r7 = r2.left
                r10 = 1090519040(0x41000000, float:8.0)
                int r12 = r0.dp(r10)
                int r7 = r7 + r12
                int r7 = r7 + r5
                float r7 = (float) r7
                int r10 = r2.bottom
                int r10 = r10 - r4
                float r10 = (float) r10
                r3.moveTo(r7, r10)
                goto L_0x0370
            L_0x035e:
                int r7 = r2.left
                r10 = 1076258406(0x40266666, float:2.6)
                int r10 = r0.dp(r10)
                int r7 = r7 + r10
                float r7 = (float) r7
                int r10 = r2.bottom
                int r10 = r10 - r4
                float r10 = (float) r10
                r3.moveTo(r7, r10)
            L_0x0370:
                int r7 = r2.right
                int r7 = r7 - r4
                int r7 = r7 - r5
                float r7 = (float) r7
                int r10 = r2.bottom
                int r10 = r10 - r4
                float r10 = (float) r10
                r3.lineTo(r7, r10)
                android.graphics.RectF r7 = r0.rect
                int r10 = r2.right
                int r12 = r10 - r4
                int r14 = r5 * 2
                int r12 = r12 - r14
                float r12 = (float) r12
                int r8 = r2.bottom
                int r19 = r8 - r4
                int r14 = r19 - r14
                float r14 = (float) r14
                int r10 = r10 - r4
                float r10 = (float) r10
                int r8 = r8 - r4
                float r8 = (float) r8
                r7.set(r12, r14, r10, r8)
                android.graphics.RectF r7 = r0.rect
                r8 = -1028390912(0xffffffffc2b40000, float:-90.0)
                r10 = 1119092736(0x42b40000, float:90.0)
                r12 = 0
                r3.arcTo(r7, r10, r8, r12)
            L_0x039e:
                boolean r7 = r0.drawFullBubble
                if (r7 != 0) goto L_0x03f0
                int r7 = r0.currentType
                r8 = 2
                if (r7 == r8) goto L_0x03f0
                if (r23 != 0) goto L_0x03f0
                if (r15 == 0) goto L_0x03ac
                goto L_0x03f0
            L_0x03ac:
                int r7 = r2.right
                int r7 = r7 - r4
                float r7 = (float) r7
                int r8 = r0.topY
                int r8 = r11 - r8
                r10 = 1073741824(0x40000000, float:2.0)
                int r12 = r0.dp(r10)
                int r8 = r8 - r12
                float r8 = (float) r8
                r3.lineTo(r7, r8)
                int r7 = r0.currentType
                r8 = 1
                if (r7 != r8) goto L_0x03d7
                int r7 = r2.left
                int r7 = r7 + r4
                float r7 = (float) r7
                int r8 = r0.topY
                int r8 = r11 - r8
                int r10 = r0.dp(r10)
                int r8 = r8 - r10
                float r8 = (float) r8
                r3.lineTo(r7, r8)
                goto L_0x048e
            L_0x03d7:
                int r7 = r2.left
                r8 = 1090519040(0x41000000, float:8.0)
                int r12 = r0.dp(r8)
                int r7 = r7 + r12
                float r7 = (float) r7
                int r8 = r0.topY
                int r8 = r11 - r8
                int r10 = r0.dp(r10)
                int r8 = r8 - r10
                float r8 = (float) r8
                r3.lineTo(r7, r8)
                goto L_0x048e
            L_0x03f0:
                int r7 = r2.right
                int r7 = r7 - r4
                float r7 = (float) r7
                int r8 = r2.top
                int r8 = r8 + r4
                int r8 = r8 + r5
                float r8 = (float) r8
                r3.lineTo(r7, r8)
                android.graphics.RectF r7 = r0.rect
                int r8 = r2.right
                int r10 = r8 - r4
                int r12 = r5 * 2
                int r10 = r10 - r12
                float r10 = (float) r10
                int r14 = r2.top
                int r15 = r14 + r4
                float r15 = (float) r15
                int r8 = r8 - r4
                float r8 = (float) r8
                int r14 = r14 + r4
                int r14 = r14 + r12
                float r12 = (float) r14
                r7.set(r10, r15, r8, r12)
                android.graphics.RectF r7 = r0.rect
                r8 = -1028390912(0xffffffffc2b40000, float:-90.0)
                r10 = 0
                r12 = 0
                r3.arcTo(r7, r10, r8, r12)
                boolean r7 = r0.isTopNear
                if (r7 == 0) goto L_0x0423
                r7 = r20
                goto L_0x0424
            L_0x0423:
                r7 = r5
            L_0x0424:
                int r8 = r0.currentType
                r10 = 1
                if (r8 != r10) goto L_0x044f
                int r8 = r2.left
                int r8 = r8 + r4
                int r8 = r8 + r7
                float r8 = (float) r8
                int r10 = r2.top
                int r10 = r10 + r4
                float r10 = (float) r10
                r3.lineTo(r8, r10)
                android.graphics.RectF r8 = r0.rect
                int r10 = r2.left
                int r12 = r10 + r4
                float r12 = (float) r12
                int r14 = r2.top
                int r15 = r14 + r4
                float r15 = (float) r15
                int r10 = r10 + r4
                r17 = 2
                int r7 = r7 * 2
                int r10 = r10 + r7
                float r10 = (float) r10
                int r14 = r14 + r4
                int r14 = r14 + r7
                float r7 = (float) r14
                r8.set(r12, r15, r10, r7)
                goto L_0x0484
            L_0x044f:
                int r8 = r2.left
                r10 = 1090519040(0x41000000, float:8.0)
                int r12 = r0.dp(r10)
                int r8 = r8 + r12
                int r8 = r8 + r7
                float r8 = (float) r8
                int r12 = r2.top
                int r12 = r12 + r4
                float r12 = (float) r12
                r3.lineTo(r8, r12)
                android.graphics.RectF r8 = r0.rect
                int r12 = r2.left
                int r14 = r0.dp(r10)
                int r12 = r12 + r14
                float r12 = (float) r12
                int r14 = r2.top
                int r14 = r14 + r4
                float r14 = (float) r14
                int r15 = r2.left
                int r17 = r0.dp(r10)
                int r15 = r15 + r17
                r10 = 2
                int r7 = r7 * 2
                int r15 = r15 + r7
                float r10 = (float) r15
                int r15 = r2.top
                int r15 = r15 + r4
                int r15 = r15 + r7
                float r7 = (float) r15
                r8.set(r12, r14, r10, r7)
            L_0x0484:
                android.graphics.RectF r7 = r0.rect
                r8 = 1132920832(0x43870000, float:270.0)
                r10 = -1028390912(0xffffffffc2b40000, float:-90.0)
                r12 = 0
                r3.arcTo(r7, r8, r10, r12)
            L_0x048e:
                int r7 = r0.currentType
                r8 = 1
                if (r7 != r8) goto L_0x04de
                if (r23 != 0) goto L_0x04a8
                if (r13 == 0) goto L_0x0498
                goto L_0x04a8
            L_0x0498:
                int r2 = r2.left
                int r2 = r2 + r4
                float r2 = (float) r2
                int r4 = r0.topY
                int r11 = r11 - r4
                int r4 = r0.currentBackgroundHeight
                int r11 = r11 + r4
                float r4 = (float) r11
                r3.lineTo(r2, r4)
                goto L_0x0555
            L_0x04a8:
                boolean r6 = r0.isBottomNear
                if (r6 == 0) goto L_0x04ae
                r5 = r20
            L_0x04ae:
                int r6 = r2.left
                int r6 = r6 + r4
                float r6 = (float) r6
                int r7 = r2.bottom
                int r7 = r7 - r4
                int r7 = r7 - r5
                float r7 = (float) r7
                r3.lineTo(r6, r7)
                android.graphics.RectF r6 = r0.rect
                int r7 = r2.left
                int r8 = r7 + r4
                float r8 = (float) r8
                int r2 = r2.bottom
                int r10 = r2 - r4
                r11 = 2
                int r5 = r5 * 2
                int r10 = r10 - r5
                float r10 = (float) r10
                int r7 = r7 + r4
                int r7 = r7 + r5
                float r5 = (float) r7
                int r2 = r2 - r4
                float r2 = (float) r2
                r6.set(r8, r10, r5, r2)
                android.graphics.RectF r2 = r0.rect
                r4 = 1127481344(0x43340000, float:180.0)
                r5 = -1028390912(0xffffffffc2b40000, float:-90.0)
                r6 = 0
                r3.arcTo(r2, r4, r5, r6)
                goto L_0x0555
            L_0x04de:
                boolean r5 = r0.drawFullBubble
                if (r5 != 0) goto L_0x04ff
                r5 = 2
                if (r7 == r5) goto L_0x04ff
                if (r23 != 0) goto L_0x04ff
                if (r13 == 0) goto L_0x04ea
                goto L_0x04ff
            L_0x04ea:
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
                goto L_0x0555
            L_0x04ff:
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
                android.graphics.RectF r5 = r0.rect
                int r7 = r2.left
                r8 = 1088421888(0x40e00000, float:7.0)
                int r8 = r0.dp(r8)
                int r7 = r7 + r8
                r8 = 2
                int r6 = r6 * 2
                int r7 = r7 - r6
                float r7 = (float) r7
                int r8 = r2.bottom
                int r8 = r8 - r4
                int r8 = r8 - r6
                r6 = 1091567616(0x41100000, float:9.0)
                int r6 = r0.dp(r6)
                int r8 = r8 - r6
                float r6 = (float) r8
                int r8 = r2.left
                r10 = 1090519040(0x41000000, float:8.0)
                int r10 = r0.dp(r10)
                int r8 = r8 + r10
                float r8 = (float) r8
                int r2 = r2.bottom
                int r2 = r2 - r4
                r4 = 1065353216(0x3var_, float:1.0)
                int r4 = r0.dp(r4)
                int r2 = r2 - r4
                float r2 = (float) r2
                r5.set(r7, r6, r8, r2)
                android.graphics.RectF r2 = r0.rect
                r4 = 1118175232(0x42a60000, float:83.0)
                r5 = 0
                r6 = 0
                r3.arcTo(r2, r5, r4, r6)
            L_0x0555:
                r3.close()
            L_0x0558:
                r1.drawPath(r3, r9)
                android.graphics.Shader r2 = r0.gradientShader
                if (r2 == 0) goto L_0x0586
                boolean r2 = r0.isSelected
                if (r2 == 0) goto L_0x0586
                if (r23 != 0) goto L_0x0586
                java.lang.String r2 = "chat_outBubbleGradientSelectedOverlay"
                int r2 = r0.getColor(r2)
                android.graphics.Paint r4 = r0.selectedPaint
                int r5 = android.graphics.Color.alpha(r2)
                int r6 = r0.alpha
                int r5 = r5 * r6
                float r5 = (float) r5
                r6 = 1132396544(0x437var_, float:255.0)
                float r5 = r5 / r6
                int r5 = (int) r5
                int r2 = androidx.core.graphics.ColorUtils.setAlphaComponent(r2, r5)
                r4.setColor(r2)
                android.graphics.Paint r2 = r0.selectedPaint
                r1.drawPath(r3, r2)
            L_0x0586:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.MessageDrawable.draw(android.graphics.Canvas, android.graphics.Paint):void");
        }

        public void setDrawFullBubble(boolean z) {
            this.drawFullBubble = z;
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

        public void setBounds(int i, int i2, int i3, int i4) {
            super.setBounds(i, i2, i3, i4);
            MessageDrawable messageDrawable = this.crossfadeFromDrawable;
            if (messageDrawable != null) {
                messageDrawable.setBounds(i, i2, i3, i4);
            }
        }

        public void setRoundRadius(int i) {
            this.overrideRoundRadius = i;
        }

        public static class PathDrawParams {
            boolean lastDrawFullBottom;
            boolean lastDrawFullTop;
            Rect lastRect = new Rect();
            Path path = new Path();

            public boolean invalidatePath(Rect rect, boolean z, boolean z2) {
                boolean z3;
                if (!this.lastRect.isEmpty()) {
                    Rect rect2 = this.lastRect;
                    if (rect2.top == rect.top && rect2.bottom == rect.bottom && rect2.right == rect.right && rect2.left == rect.left && this.lastDrawFullTop == z2 && this.lastDrawFullBottom == z && z2 && z) {
                        z3 = false;
                        this.lastDrawFullTop = z2;
                        this.lastDrawFullBottom = z;
                        this.lastRect.set(rect);
                        return z3;
                    }
                }
                z3 = true;
                this.lastDrawFullTop = z2;
                this.lastDrawFullBottom = z;
                this.lastRect.set(rect);
                return z3;
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
                Utilities.globalQueue.postRunnable(new Theme$PatternsLoader$$ExternalSyntheticLambda0(this, arrayList));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(ArrayList arrayList) {
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
                ConnectionsManager.getInstance(this.account).sendRequest(tLRPC$TL_account_getMultiWallPapers, new Theme$PatternsLoader$$ExternalSyntheticLambda3(this, arrayList));
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
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$new$0(java.util.ArrayList r17, org.telegram.tgnet.TLObject r18, org.telegram.tgnet.TLRPC$TL_error r19) {
            /*
                r16 = this;
                r0 = r16
                r1 = r18
                boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$Vector
                if (r2 == 0) goto L_0x00c1
                org.telegram.tgnet.TLRPC$Vector r1 = (org.telegram.tgnet.TLRPC$Vector) r1
                java.util.ArrayList<java.lang.Object> r2 = r1.objects
                int r2 = r2.size()
                r4 = 0
                r6 = r4
                r5 = 0
            L_0x0013:
                r7 = 1
                if (r5 >= r2) goto L_0x00be
                java.util.ArrayList<java.lang.Object> r8 = r1.objects
                java.lang.Object r8 = r8.get(r5)
                org.telegram.tgnet.TLRPC$WallPaper r8 = (org.telegram.tgnet.TLRPC$WallPaper) r8
                boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_wallPaper
                if (r9 != 0) goto L_0x0024
                goto L_0x00b7
            L_0x0024:
                org.telegram.tgnet.TLRPC$TL_wallPaper r8 = (org.telegram.tgnet.TLRPC$TL_wallPaper) r8
                boolean r9 = r8.pattern
                if (r9 == 0) goto L_0x00b7
                int r9 = org.telegram.messenger.UserConfig.selectedAccount
                org.telegram.messenger.FileLoader r9 = org.telegram.messenger.FileLoader.getInstance(r9)
                org.telegram.tgnet.TLRPC$Document r10 = r8.document
                java.io.File r7 = r9.getPathToAttach(r10, r7)
                int r9 = r17.size()
                r11 = r4
                r12 = r11
                r10 = 0
            L_0x003d:
                if (r10 >= r9) goto L_0x00af
                r13 = r17
                java.lang.Object r14 = r13.get(r10)
                org.telegram.ui.ActionBar.Theme$ThemeAccent r14 = (org.telegram.ui.ActionBar.Theme.ThemeAccent) r14
                java.lang.String r15 = r14.patternSlug
                java.lang.String r3 = r8.slug
                boolean r3 = r15.equals(r3)
                if (r3 == 0) goto L_0x00ab
                if (r12 != 0) goto L_0x005c
                boolean r3 = r7.exists()
                java.lang.Boolean r3 = java.lang.Boolean.valueOf(r3)
                r12 = r3
            L_0x005c:
                if (r11 != 0) goto L_0x0092
                boolean r3 = r12.booleanValue()
                if (r3 == 0) goto L_0x0065
                goto L_0x0092
            L_0x0065:
                org.telegram.tgnet.TLRPC$Document r3 = r8.document
                java.lang.String r3 = org.telegram.messenger.FileLoader.getAttachFileName(r3)
                java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$PatternsLoader$LoadingPattern> r15 = r0.watingForLoad
                if (r15 != 0) goto L_0x0076
                java.util.HashMap r15 = new java.util.HashMap
                r15.<init>()
                r0.watingForLoad = r15
            L_0x0076:
                java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$PatternsLoader$LoadingPattern> r15 = r0.watingForLoad
                java.lang.Object r15 = r15.get(r3)
                org.telegram.ui.ActionBar.Theme$PatternsLoader$LoadingPattern r15 = (org.telegram.ui.ActionBar.Theme.PatternsLoader.LoadingPattern) r15
                if (r15 != 0) goto L_0x008c
                org.telegram.ui.ActionBar.Theme$PatternsLoader$LoadingPattern r15 = new org.telegram.ui.ActionBar.Theme$PatternsLoader$LoadingPattern
                r15.<init>()
                r15.pattern = r8
                java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$PatternsLoader$LoadingPattern> r4 = r0.watingForLoad
                r4.put(r3, r15)
            L_0x008c:
                java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeAccent> r3 = r15.accents
                r3.add(r14)
                goto L_0x00ab
            L_0x0092:
                org.telegram.tgnet.TLRPC$Document r3 = r8.document
                java.lang.String r3 = r3.mime_type
                java.lang.String r4 = "application/x-tgwallpattern"
                boolean r3 = r4.equals(r3)
                android.graphics.Bitmap r11 = r0.createWallpaperForAccent(r11, r3, r7, r14)
                if (r6 != 0) goto L_0x00a8
                java.util.ArrayList r3 = new java.util.ArrayList
                r3.<init>()
                r6 = r3
            L_0x00a8:
                r6.add(r14)
            L_0x00ab:
                int r10 = r10 + 1
                r4 = 0
                goto L_0x003d
            L_0x00af:
                r13 = r17
                if (r11 == 0) goto L_0x00b9
                r11.recycle()
                goto L_0x00b9
            L_0x00b7:
                r13 = r17
            L_0x00b9:
                int r5 = r5 + 1
                r4 = 0
                goto L_0x0013
            L_0x00be:
                r0.checkCurrentWallpaper(r6, r7)
            L_0x00c1:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.PatternsLoader.lambda$new$0(java.util.ArrayList, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
        }

        private void checkCurrentWallpaper(ArrayList<ThemeAccent> arrayList, boolean z) {
            AndroidUtilities.runOnUIThread(new Theme$PatternsLoader$$ExternalSyntheticLambda1(this, arrayList, z));
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
                    NotificationCenter.getInstance(this.account).removeObserver(this, NotificationCenter.fileLoaded);
                    NotificationCenter.getInstance(this.account).removeObserver(this, NotificationCenter.fileLoadFailed);
                }
            } else if (this.watingForLoad != null) {
                NotificationCenter.getInstance(this.account).addObserver(this, NotificationCenter.fileLoaded);
                NotificationCenter.getInstance(this.account).addObserver(this, NotificationCenter.fileLoadFailed);
                for (Map.Entry<String, LoadingPattern> value : this.watingForLoad.entrySet()) {
                    FileLoader.getInstance(this.account).loadFile(ImageLocation.getForDocument(((LoadingPattern) value.getValue()).pattern.document), "wallpaper", (String) null, 0, 1);
                }
            }
        }

        private Bitmap createWallpaperForAccent(Bitmap bitmap, boolean z, File file, ThemeAccent themeAccent) {
            Bitmap bitmap2;
            int i;
            Bitmap access$500;
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
                Theme.checkIsDark(themeFileValues, themeInfo);
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
                    Point point = AndroidUtilities.displaySize;
                    int min = Math.min(point.x, point.y);
                    Point point2 = AndroidUtilities.displaySize;
                    int max = Math.max(point2.x, point2.y);
                    if (z) {
                        access$500 = SvgHelper.getBitmap(file2, min, max, false);
                    } else {
                        access$500 = Theme.loadScreenSizedBitmap(new FileInputStream(file2), 0);
                    }
                    bitmap2 = access$500;
                } else {
                    bitmap2 = bitmap;
                }
                if (drawable != null) {
                    try {
                        Bitmap createBitmap = Bitmap.createBitmap(bitmap2.getWidth(), bitmap2.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(createBitmap);
                        drawable.setBounds(0, 0, bitmap2.getWidth(), bitmap2.getHeight());
                        drawable.draw(canvas);
                        Paint paint = new Paint(2);
                        paint.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN));
                        paint.setAlpha((int) (Math.abs(themeAccent2.patternIntensity) * 255.0f));
                        canvas.drawBitmap(bitmap2, 0.0f, 0.0f, paint);
                        createBitmap.compress(Bitmap.CompressFormat.JPEG, 87, new FileOutputStream(pathToWallpaper));
                    } catch (Throwable th) {
                        th = th;
                        FileLog.e(th);
                        return bitmap2;
                    }
                } else {
                    FileOutputStream fileOutputStream = new FileOutputStream(pathToWallpaper);
                    bitmap2.compress(Bitmap.CompressFormat.PNG, 87, fileOutputStream);
                    fileOutputStream.close();
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
                if (i == NotificationCenter.fileLoaded) {
                    LoadingPattern remove = hashMap.remove(objArr[0]);
                    if (remove != null) {
                        Utilities.globalQueue.postRunnable(new Theme$PatternsLoader$$ExternalSyntheticLambda2(this, remove));
                    }
                } else if (i == NotificationCenter.fileLoadFailed && hashMap.remove(objArr[0]) != null) {
                    checkCurrentWallpaper((ArrayList<ThemeAccent>) null, false);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$didReceivedNotification$3(LoadingPattern loadingPattern) {
            TLRPC$TL_wallPaper tLRPC$TL_wallPaper = loadingPattern.pattern;
            File pathToAttach = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(tLRPC$TL_wallPaper.document, true);
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
        public int accentColor2;
        public int account;
        public long backgroundGradientOverrideColor1;
        public long backgroundGradientOverrideColor2;
        public long backgroundGradientOverrideColor3;
        public long backgroundOverrideColor;
        public int backgroundRotation = 45;
        public int id;
        public TLRPC$TL_theme info;
        public boolean isDefault;
        public int myMessagesAccentColor;
        public boolean myMessagesAnimated;
        public int myMessagesGradientAccentColor1;
        public int myMessagesGradientAccentColor2;
        public int myMessagesGradientAccentColor3;
        public OverrideWallpaperInfo overrideWallpaper;
        public ThemeInfo parentTheme;
        public TLRPC$TL_wallPaper pattern;
        public float patternIntensity;
        public boolean patternMotion;
        public String patternSlug = "";
        private float[] tempHSV = new float[3];
        public TLRPC$InputFile uploadedFile;
        public TLRPC$InputFile uploadedThumb;
        public String uploadingFile;
        public String uploadingThumb;

        ThemeAccent() {
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v24, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v5, resolved type: java.lang.Integer} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean fillAccentColors(java.util.HashMap<java.lang.String, java.lang.Integer> r17, java.util.HashMap<java.lang.String, java.lang.Integer> r18) {
            /*
                r16 = this;
                r0 = r16
                r1 = r17
                r2 = r18
                r3 = 1
                float[] r4 = org.telegram.ui.ActionBar.Theme.getTempHsv(r3)
                r5 = 2
                float[] r6 = org.telegram.ui.ActionBar.Theme.getTempHsv(r5)
                org.telegram.ui.ActionBar.Theme$ThemeInfo r7 = r0.parentTheme
                int r7 = r7.accentBaseColor
                android.graphics.Color.colorToHSV(r7, r4)
                int r7 = r0.accentColor
                android.graphics.Color.colorToHSV(r7, r6)
                org.telegram.ui.ActionBar.Theme$ThemeInfo r7 = r0.parentTheme
                boolean r7 = r7.isDark()
                int r8 = r0.accentColor
                org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = r0.parentTheme
                int r9 = r9.accentBaseColor
                if (r8 != r9) goto L_0x002e
                int r8 = r0.accentColor2
                if (r8 == 0) goto L_0x00a2
            L_0x002e:
                java.util.Set r8 = r17.keySet()
                java.util.HashMap r9 = org.telegram.ui.ActionBar.Theme.defaultColors
                java.util.Set r9 = r9.keySet()
                java.util.HashSet r10 = new java.util.HashSet
                int r11 = r8.size()
                int r12 = r9.size()
                int r11 = r11 + r12
                r10.<init>(r11)
                r10.addAll(r8)
                r10.addAll(r9)
                java.util.HashSet r8 = org.telegram.ui.ActionBar.Theme.themeAccentExclusionKeys
                r10.removeAll(r8)
                java.util.Iterator r8 = r10.iterator()
            L_0x0059:
                boolean r9 = r8.hasNext()
                if (r9 == 0) goto L_0x00a2
                java.lang.Object r9 = r8.next()
                java.lang.String r9 = (java.lang.String) r9
                java.lang.Object r10 = r1.get(r9)
                java.lang.Integer r10 = (java.lang.Integer) r10
                if (r10 != 0) goto L_0x0080
                java.util.HashMap r11 = org.telegram.ui.ActionBar.Theme.fallbackKeys
                java.lang.Object r11 = r11.get(r9)
                java.lang.String r11 = (java.lang.String) r11
                if (r11 == 0) goto L_0x0080
                java.lang.Object r11 = r1.get(r11)
                if (r11 == 0) goto L_0x0080
                goto L_0x0059
            L_0x0080:
                if (r10 != 0) goto L_0x008c
                java.util.HashMap r10 = org.telegram.ui.ActionBar.Theme.defaultColors
                java.lang.Object r10 = r10.get(r9)
                java.lang.Integer r10 = (java.lang.Integer) r10
            L_0x008c:
                int r11 = r10.intValue()
                int r11 = org.telegram.ui.ActionBar.Theme.changeColorAccent(r4, r6, r11, r7)
                int r10 = r10.intValue()
                if (r11 == r10) goto L_0x0059
                java.lang.Integer r10 = java.lang.Integer.valueOf(r11)
                r2.put(r9, r10)
                goto L_0x0059
            L_0x00a2:
                int r8 = r0.myMessagesAccentColor
                r9 = 1060403937(0x3var_ae1, float:0.705)
                java.lang.String r10 = "chat_outBubble"
                r11 = 0
                if (r8 != 0) goto L_0x00b0
                int r12 = r0.accentColor
                if (r12 == 0) goto L_0x0120
            L_0x00b0:
                int r12 = r0.myMessagesGradientAccentColor1
                if (r12 == 0) goto L_0x0120
                if (r8 == 0) goto L_0x00b7
                goto L_0x00b9
            L_0x00b7:
                int r8 = r0.accentColor
            L_0x00b9:
                java.lang.Object r12 = r1.get(r10)
                java.lang.Integer r12 = (java.lang.Integer) r12
                if (r12 != 0) goto L_0x00cb
                java.util.HashMap r12 = org.telegram.ui.ActionBar.Theme.defaultColors
                java.lang.Object r12 = r12.get(r10)
                java.lang.Integer r12 = (java.lang.Integer) r12
            L_0x00cb:
                int r13 = r12.intValue()
                int r13 = org.telegram.ui.ActionBar.Theme.changeColorAccent(r4, r6, r13, r7)
                int r13 = org.telegram.messenger.AndroidUtilities.getColorDistance(r8, r13)
                int r14 = r0.myMessagesGradientAccentColor1
                int r14 = org.telegram.messenger.AndroidUtilities.getColorDistance(r8, r14)
                int r15 = r0.myMessagesGradientAccentColor2
                if (r15 == 0) goto L_0x0103
                int r15 = r0.myMessagesAccentColor
                int r5 = r0.myMessagesGradientAccentColor1
                int r5 = org.telegram.messenger.AndroidUtilities.getAverageColor(r15, r5)
                int r15 = r0.myMessagesGradientAccentColor2
                int r5 = org.telegram.messenger.AndroidUtilities.getAverageColor(r5, r15)
                int r15 = r0.myMessagesGradientAccentColor3
                if (r15 == 0) goto L_0x00f7
                int r5 = org.telegram.messenger.AndroidUtilities.getAverageColor(r5, r15)
            L_0x00f7:
                float r5 = org.telegram.messenger.AndroidUtilities.computePerceivedBrightness(r5)
                int r5 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1))
                if (r5 <= 0) goto L_0x0101
                r5 = 1
                goto L_0x010b
            L_0x0101:
                r5 = 0
                goto L_0x010b
            L_0x0103:
                int r5 = r0.myMessagesAccentColor
                int r15 = r0.myMessagesGradientAccentColor1
                boolean r5 = org.telegram.ui.ActionBar.Theme.useBlackText(r5, r15)
            L_0x010b:
                if (r5 == 0) goto L_0x0116
                r5 = 35000(0x88b8, float:4.9045E-41)
                if (r13 > r5) goto L_0x0116
                if (r14 > r5) goto L_0x0116
                r5 = 1
                goto L_0x0117
            L_0x0116:
                r5 = 0
            L_0x0117:
                int r12 = r12.intValue()
                int r8 = org.telegram.ui.ActionBar.Theme.getAccentColor(r4, r12, r8)
                goto L_0x0121
            L_0x0120:
                r5 = 0
            L_0x0121:
                if (r8 == 0) goto L_0x0133
                org.telegram.ui.ActionBar.Theme$ThemeInfo r12 = r0.parentTheme
                int r12 = r12.accentBaseColor
                if (r12 == 0) goto L_0x012b
                if (r8 != r12) goto L_0x0131
            L_0x012b:
                int r12 = r0.accentColor
                if (r12 == 0) goto L_0x0133
                if (r12 == r8) goto L_0x0133
            L_0x0131:
                r12 = 1
                goto L_0x0134
            L_0x0133:
                r12 = 0
            L_0x0134:
                if (r12 != 0) goto L_0x013a
                int r13 = r0.accentColor2
                if (r13 == 0) goto L_0x01f4
            L_0x013a:
                int r13 = r0.accentColor2
                if (r13 == 0) goto L_0x0142
                android.graphics.Color.colorToHSV(r13, r6)
                goto L_0x0145
            L_0x0142:
                android.graphics.Color.colorToHSV(r8, r6)
            L_0x0145:
                java.util.HashSet r13 = org.telegram.ui.ActionBar.Theme.myMessagesColorKeys
                java.util.Iterator r13 = r13.iterator()
            L_0x014d:
                boolean r14 = r13.hasNext()
                if (r14 == 0) goto L_0x019b
                java.lang.Object r14 = r13.next()
                java.lang.String r14 = (java.lang.String) r14
                java.lang.Object r15 = r1.get(r14)
                java.lang.Integer r15 = (java.lang.Integer) r15
                if (r15 != 0) goto L_0x0174
                java.util.HashMap r3 = org.telegram.ui.ActionBar.Theme.fallbackKeys
                java.lang.Object r3 = r3.get(r14)
                java.lang.String r3 = (java.lang.String) r3
                if (r3 == 0) goto L_0x0174
                java.lang.Object r3 = r1.get(r3)
                if (r3 == 0) goto L_0x0174
                goto L_0x0183
            L_0x0174:
                if (r15 != 0) goto L_0x0181
                java.util.HashMap r3 = org.telegram.ui.ActionBar.Theme.defaultColors
                java.lang.Object r3 = r3.get(r14)
                r15 = r3
                java.lang.Integer r15 = (java.lang.Integer) r15
            L_0x0181:
                if (r15 != 0) goto L_0x0185
            L_0x0183:
                r3 = 1
                goto L_0x014d
            L_0x0185:
                int r3 = r15.intValue()
                int r3 = org.telegram.ui.ActionBar.Theme.changeColorAccent(r4, r6, r3, r7)
                int r15 = r15.intValue()
                if (r3 == r15) goto L_0x0183
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                r2.put(r14, r3)
                goto L_0x0183
            L_0x019b:
                if (r12 == 0) goto L_0x01f4
                android.graphics.Color.colorToHSV(r8, r6)
                java.util.HashSet r3 = org.telegram.ui.ActionBar.Theme.myMessagesBubblesColorKeys
                java.util.Iterator r3 = r3.iterator()
            L_0x01a8:
                boolean r8 = r3.hasNext()
                if (r8 == 0) goto L_0x01f4
                java.lang.Object r8 = r3.next()
                java.lang.String r8 = (java.lang.String) r8
                java.lang.Object r12 = r1.get(r8)
                java.lang.Integer r12 = (java.lang.Integer) r12
                if (r12 != 0) goto L_0x01cf
                java.util.HashMap r13 = org.telegram.ui.ActionBar.Theme.fallbackKeys
                java.lang.Object r13 = r13.get(r8)
                java.lang.String r13 = (java.lang.String) r13
                if (r13 == 0) goto L_0x01cf
                java.lang.Object r13 = r1.get(r13)
                if (r13 == 0) goto L_0x01cf
                goto L_0x01a8
            L_0x01cf:
                if (r12 != 0) goto L_0x01db
                java.util.HashMap r12 = org.telegram.ui.ActionBar.Theme.defaultColors
                java.lang.Object r12 = r12.get(r8)
                java.lang.Integer r12 = (java.lang.Integer) r12
            L_0x01db:
                if (r12 != 0) goto L_0x01de
                goto L_0x01a8
            L_0x01de:
                int r13 = r12.intValue()
                int r13 = org.telegram.ui.ActionBar.Theme.changeColorAccent(r4, r6, r13, r7)
                int r12 = r12.intValue()
                if (r13 == r12) goto L_0x01a8
                java.lang.Integer r12 = java.lang.Integer.valueOf(r13)
                r2.put(r8, r12)
                goto L_0x01a8
            L_0x01f4:
                r1 = -1
                java.lang.String r3 = "chat_messageLinkOut"
                java.lang.String r4 = "chat_outLoader"
                if (r5 != 0) goto L_0x0432
                int r6 = r0.myMessagesGradientAccentColor1
                if (r6 == 0) goto L_0x0432
                int r8 = r0.myMessagesGradientAccentColor2
                if (r8 == 0) goto L_0x0223
                int r8 = r0.myMessagesAccentColor
                int r6 = org.telegram.messenger.AndroidUtilities.getAverageColor(r8, r6)
                int r8 = r0.myMessagesGradientAccentColor2
                int r6 = org.telegram.messenger.AndroidUtilities.getAverageColor(r6, r8)
                int r8 = r0.myMessagesGradientAccentColor3
                if (r8 == 0) goto L_0x0217
                int r6 = org.telegram.messenger.AndroidUtilities.getAverageColor(r6, r8)
            L_0x0217:
                float r6 = org.telegram.messenger.AndroidUtilities.computePerceivedBrightness(r6)
                int r6 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
                if (r6 <= 0) goto L_0x0221
                r6 = 1
                goto L_0x0229
            L_0x0221:
                r6 = 0
                goto L_0x0229
            L_0x0223:
                int r8 = r0.myMessagesAccentColor
                boolean r6 = org.telegram.ui.ActionBar.Theme.useBlackText(r8, r6)
            L_0x0229:
                if (r6 == 0) goto L_0x0234
                r6 = -14606047(0xfffffffffvar_, float:-2.1417772E38)
                r8 = -11184811(0xfffffffffvar_, float:-2.8356863E38)
                r9 = 1291845632(0x4d000000, float:1.34217728E8)
                goto L_0x023b
            L_0x0234:
                r8 = -1118482(0xffffffffffeeeeee, float:NaN)
                r9 = 1308622847(0x4dffffff, float:5.3687088E8)
                r6 = -1
            L_0x023b:
                int r12 = r0.accentColor2
                if (r12 != 0) goto L_0x040e
                java.lang.Integer r12 = java.lang.Integer.valueOf(r9)
                java.lang.String r13 = "chat_outAudioProgress"
                r2.put(r13, r12)
                java.lang.Integer r12 = java.lang.Integer.valueOf(r9)
                java.lang.String r13 = "chat_outAudioSelectedProgress"
                r2.put(r13, r12)
                java.lang.Integer r12 = java.lang.Integer.valueOf(r9)
                java.lang.String r13 = "chat_outAudioSeekbar"
                r2.put(r13, r12)
                java.lang.Integer r12 = java.lang.Integer.valueOf(r9)
                java.lang.String r13 = "chat_outAudioCacheSeekbar"
                r2.put(r13, r12)
                java.lang.Integer r12 = java.lang.Integer.valueOf(r9)
                java.lang.String r13 = "chat_outAudioSeekbarSelected"
                r2.put(r13, r12)
                java.lang.Integer r12 = java.lang.Integer.valueOf(r6)
                java.lang.String r13 = "chat_outAudioSeekbarFill"
                r2.put(r13, r12)
                java.lang.Integer r12 = java.lang.Integer.valueOf(r9)
                java.lang.String r13 = "chat_outVoiceSeekbar"
                r2.put(r13, r12)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
                java.lang.String r12 = "chat_outVoiceSeekbarSelected"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outVoiceSeekbarFill"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                r2.put(r3, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outForwardedNameText"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outViaBotNameText"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outReplyLine"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outReplyNameText"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outPreviewLine"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outSiteNameText"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outInstant"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outInstantSelected"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outPreviewInstantText"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outPreviewInstantSelectedText"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outViews"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outViewsSelected"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outAudioTitleText"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outFileNameText"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outContactNameText"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outAudioPerfomerText"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outAudioPerfomerSelectedText"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outSentCheck"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outSentCheckSelected"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outSentCheckRead"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outSentCheckReadSelected"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outSentClock"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outSentClockSelected"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outMenu"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outMenuSelected"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outTimeText"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
                java.lang.String r12 = "chat_outTimeSelectedText"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r8)
                java.lang.String r12 = "chat_outAudioDurationText"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r8)
                java.lang.String r12 = "chat_outAudioDurationSelectedText"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r8)
                java.lang.String r12 = "chat_outContactPhoneText"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r8)
                java.lang.String r12 = "chat_outContactPhoneSelectedText"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r8)
                java.lang.String r12 = "chat_outFileInfoText"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r8)
                java.lang.String r12 = "chat_outFileInfoSelectedText"
                r2.put(r12, r9)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r8)
                java.lang.String r12 = "chat_outVenueInfoText"
                r2.put(r12, r9)
                java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
                java.lang.String r9 = "chat_outVenueInfoSelectedText"
                r2.put(r9, r8)
                java.lang.Integer r8 = java.lang.Integer.valueOf(r6)
                r2.put(r4, r8)
                java.lang.Integer r8 = java.lang.Integer.valueOf(r6)
                java.lang.String r9 = "chat_outLoaderSelected"
                r2.put(r9, r8)
                int r8 = r0.myMessagesAccentColor
                java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
                java.lang.String r9 = "chat_outFileProgress"
                r2.put(r9, r8)
                int r8 = r0.myMessagesAccentColor
                java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
                java.lang.String r9 = "chat_outFileProgressSelected"
                r2.put(r9, r8)
                int r8 = r0.myMessagesAccentColor
                java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
                java.lang.String r9 = "chat_outMediaIcon"
                r2.put(r9, r8)
                int r8 = r0.myMessagesAccentColor
                java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
                java.lang.String r9 = "chat_outMediaIconSelected"
                r2.put(r9, r8)
            L_0x040e:
                java.lang.Integer r8 = java.lang.Integer.valueOf(r6)
                java.lang.String r9 = "chat_outReplyMessageText"
                r2.put(r9, r8)
                java.lang.Integer r8 = java.lang.Integer.valueOf(r6)
                java.lang.String r9 = "chat_outReplyMediaMessageText"
                r2.put(r9, r8)
                java.lang.Integer r8 = java.lang.Integer.valueOf(r6)
                java.lang.String r9 = "chat_outReplyMediaMessageSelectedText"
                r2.put(r9, r8)
                java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
                java.lang.String r8 = "chat_messageTextOut"
                r2.put(r8, r6)
            L_0x0432:
                if (r5 == 0) goto L_0x044f
                boolean r6 = r2.containsKey(r4)
                if (r6 == 0) goto L_0x0445
                java.lang.Object r4 = r2.get(r4)
                java.lang.Integer r4 = (java.lang.Integer) r4
                int r4 = r4.intValue()
                goto L_0x0446
            L_0x0445:
                r4 = 0
            L_0x0446:
                int r1 = org.telegram.messenger.AndroidUtilities.getColorDistance(r1, r4)
                r4 = 5000(0x1388, float:7.006E-42)
                if (r1 >= r4) goto L_0x044f
                r5 = 0
            L_0x044f:
                int r1 = r0.myMessagesAccentColor
                if (r1 == 0) goto L_0x048e
                int r4 = r0.myMessagesGradientAccentColor1
                if (r4 == 0) goto L_0x048e
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                r2.put(r10, r1)
                int r1 = r0.myMessagesGradientAccentColor1
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                java.lang.String r4 = "chat_outBubbleGradient"
                r2.put(r4, r1)
                int r1 = r0.myMessagesGradientAccentColor2
                if (r1 == 0) goto L_0x0483
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                java.lang.String r4 = "chat_outBubbleGradient2"
                r2.put(r4, r1)
                int r1 = r0.myMessagesGradientAccentColor3
                if (r1 == 0) goto L_0x0483
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                java.lang.String r4 = "chat_outBubbleGradient3"
                r2.put(r4, r1)
            L_0x0483:
                boolean r1 = r0.myMessagesAnimated
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                java.lang.String r4 = "chat_outBubbleGradientAnimated"
                r2.put(r4, r1)
            L_0x048e:
                long r8 = r0.backgroundOverrideColor
                int r1 = (int) r8
                java.lang.String r4 = "chat_wallpaper"
                r12 = 0
                if (r1 == 0) goto L_0x049f
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                r2.put(r4, r1)
                goto L_0x04a6
            L_0x049f:
                int r1 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
                if (r1 == 0) goto L_0x04a6
                r2.remove(r4)
            L_0x04a6:
                long r8 = r0.backgroundGradientOverrideColor1
                int r1 = (int) r8
                java.lang.String r6 = "chat_wallpaper_gradient_to"
                if (r1 == 0) goto L_0x04b5
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                r2.put(r6, r1)
                goto L_0x04bc
            L_0x04b5:
                int r1 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
                if (r1 == 0) goto L_0x04bc
                r2.remove(r6)
            L_0x04bc:
                long r8 = r0.backgroundGradientOverrideColor2
                int r1 = (int) r8
                java.lang.String r14 = "key_chat_wallpaper_gradient_to2"
                if (r1 == 0) goto L_0x04cb
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                r2.put(r14, r1)
                goto L_0x04d2
            L_0x04cb:
                int r1 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
                if (r1 == 0) goto L_0x04d2
                r2.remove(r14)
            L_0x04d2:
                long r8 = r0.backgroundGradientOverrideColor3
                int r1 = (int) r8
                java.lang.String r15 = "key_chat_wallpaper_gradient_to3"
                if (r1 == 0) goto L_0x04e1
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                r2.put(r15, r1)
                goto L_0x04e8
            L_0x04e1:
                int r1 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
                if (r1 == 0) goto L_0x04e8
                r2.remove(r15)
            L_0x04e8:
                int r1 = r0.backgroundRotation
                r8 = 45
                if (r1 == r8) goto L_0x04f7
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                java.lang.String r8 = "chat_wallpaper_gradient_rotation"
                r2.put(r8, r1)
            L_0x04f7:
                java.lang.Object r1 = r2.get(r10)
                java.lang.Integer r1 = (java.lang.Integer) r1
                if (r1 != 0) goto L_0x0507
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r10)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            L_0x0507:
                java.lang.String r8 = "chat_inBubble"
                java.lang.Object r9 = r2.get(r8)
                java.lang.Integer r9 = (java.lang.Integer) r9
                if (r9 != 0) goto L_0x0519
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                java.lang.Integer r9 = java.lang.Integer.valueOf(r8)
            L_0x0519:
                r1.intValue()
                org.telegram.tgnet.TLRPC$TL_theme r8 = r0.info
                if (r8 == 0) goto L_0x0597
                java.lang.String r8 = r8.emoticon
                if (r8 == 0) goto L_0x0597
                if (r7 != 0) goto L_0x0597
                java.lang.String r8 = "chat_selectedBackground"
                r2.remove(r8)
                r8 = 3
                java.lang.String[] r8 = new java.lang.String[r8]
                r8[r11] = r6
                r6 = 1
                r8[r6] = r14
                r10 = 2
                r8[r10] = r15
                int r8 = r0.averageColor(r2, r8)
                if (r8 != 0) goto L_0x0544
                java.lang.String[] r8 = new java.lang.String[r6]
                r8[r11] = r4
                int r8 = r0.averageColor(r2, r8)
            L_0x0544:
                if (r8 != 0) goto L_0x0548
                int r8 = r0.accentColor
            L_0x0548:
                int r4 = r1.intValue()
                int r4 = r0.bubbleSelectedOverlay(r4, r8)
                java.lang.Integer r6 = java.lang.Integer.valueOf(r4)
                java.lang.String r8 = "chat_outBubbleSelectedOverlay"
                r2.put(r8, r6)
                java.lang.Integer r6 = java.lang.Integer.valueOf(r4)
                java.lang.String r8 = "chat_outBubbleGradientSelectedOverlay"
                r2.put(r8, r6)
                int r6 = r1.intValue()
                int r4 = org.telegram.ui.ActionBar.Theme.blendOver(r6, r4)
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                java.lang.String r6 = "chat_outBubbleSelected"
                r2.put(r6, r4)
                int r4 = r9.intValue()
                int r6 = r0.accentColor
                int r4 = r0.bubbleSelectedOverlay(r4, r6)
                java.lang.Integer r6 = java.lang.Integer.valueOf(r4)
                java.lang.String r8 = "chat_inBubbleSelectedOverlay"
                r2.put(r8, r6)
                int r6 = r9.intValue()
                int r4 = org.telegram.ui.ActionBar.Theme.blendOver(r6, r4)
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                java.lang.String r6 = "chat_inBubbleSelected"
                r2.put(r6, r4)
            L_0x0597:
                if (r7 != 0) goto L_0x05d3
                int r4 = r9.intValue()
                int r6 = r0.accentColor
                int r4 = r0.textSelectionBackground(r11, r4, r6)
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                java.lang.String r6 = "chat_inTextSelectionHighlight"
                r2.put(r6, r4)
                int r4 = r1.intValue()
                int r6 = r0.accentColor
                r8 = 1
                int r4 = r0.textSelectionBackground(r8, r4, r6)
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                java.lang.String r6 = "chat_outTextSelectionHighlight"
                r2.put(r6, r4)
                int r4 = r1.intValue()
                int r6 = r0.accentColor
                int r4 = r0.textSelectionHandle(r4, r6)
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                java.lang.String r6 = "chat_outTextSelectionCursor"
                r2.put(r6, r4)
            L_0x05d3:
                java.lang.String r4 = "chat_messageLinkIn"
                java.lang.Object r6 = r2.get(r4)
                java.lang.Integer r6 = (java.lang.Integer) r6
                if (r6 != 0) goto L_0x05e5
                int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
                java.lang.Integer r6 = java.lang.Integer.valueOf(r4)
            L_0x05e5:
                java.lang.Object r4 = r2.get(r3)
                java.lang.Integer r4 = (java.lang.Integer) r4
                if (r4 != 0) goto L_0x05f5
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                java.lang.Integer r4 = java.lang.Integer.valueOf(r3)
            L_0x05f5:
                int r3 = r6.intValue()
                int r6 = r9.intValue()
                int r3 = r0.linkSelectionBackground(r3, r6, r7)
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                java.lang.String r6 = "chat_linkSelectBackground"
                r2.put(r6, r3)
                int r3 = r4.intValue()
                int r1 = r1.intValue()
                int r1 = r0.linkSelectionBackground(r3, r1, r7)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                java.lang.String r3 = "chat_outLinkSelectBackground"
                r2.put(r3, r1)
                java.lang.String r1 = "actionBarDefaultSubmenuBackground"
                java.lang.Object r3 = r2.get(r1)
                java.lang.Integer r3 = (java.lang.Integer) r3
                if (r3 != 0) goto L_0x0631
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                java.lang.Integer r3 = java.lang.Integer.valueOf(r1)
            L_0x0631:
                int r1 = r3.intValue()
                int r1 = android.graphics.Color.alpha(r1)
                int r4 = r3.intValue()
                int r4 = android.graphics.Color.red(r4)
                int r4 = r4 + -10
                int r4 = java.lang.Math.max(r11, r4)
                int r6 = r3.intValue()
                int r6 = android.graphics.Color.green(r6)
                int r6 = r6 + -10
                int r6 = java.lang.Math.max(r11, r6)
                int r3 = r3.intValue()
                int r3 = android.graphics.Color.blue(r3)
                int r3 = r3 + -10
                int r3 = java.lang.Math.max(r11, r3)
                int r1 = android.graphics.Color.argb(r1, r4, r6, r3)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                java.lang.String r3 = "actionBarDefaultSubmenuSeparator"
                r2.put(r3, r1)
                r1 = 1
                r1 = r1 ^ r5
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.ThemeAccent.fillAccentColors(java.util.HashMap, java.util.HashMap):boolean");
        }

        private int bubbleSelectedOverlay(int i, int i2) {
            Color.colorToHSV(i2, this.tempHSV);
            float[] fArr = this.tempHSV;
            float f = fArr[0];
            Color.colorToHSV(i, fArr);
            float[] fArr2 = this.tempHSV;
            if (fArr2[1] <= 0.0f) {
                fArr2[0] = f;
            }
            fArr2[1] = Math.max(0.0f, Math.min(1.0f, fArr2[1] + 0.6f));
            float[] fArr3 = this.tempHSV;
            fArr3[2] = Math.max(0.0f, Math.min(1.0f, fArr3[2] - 0.05f));
            return Color.HSVToColor(30, this.tempHSV);
        }

        private int textSelectionBackground(boolean z, int i, int i2) {
            Color.colorToHSV(i2, this.tempHSV);
            float[] fArr = this.tempHSV;
            float f = fArr[0];
            Color.colorToHSV(i, fArr);
            float[] fArr2 = this.tempHSV;
            if (fArr2[1] <= 0.0f || (fArr2[0] > 45.0f && fArr2[0] < 85.0f)) {
                fArr2[0] = f;
            }
            fArr2[1] = Math.max(0.0f, Math.min(1.0f, fArr2[1] + (fArr2[2] > 0.85f ? 0.25f : 0.45f)));
            float[] fArr3 = this.tempHSV;
            fArr3[2] = Math.max(0.0f, Math.min(1.0f, fArr3[2] - 0.15f));
            return Color.HSVToColor(80, this.tempHSV);
        }

        private int textSelectionHandle(int i, int i2) {
            Color.colorToHSV(i2, this.tempHSV);
            float[] fArr = this.tempHSV;
            float f = fArr[0];
            Color.colorToHSV(i, fArr);
            float[] fArr2 = this.tempHSV;
            if (fArr2[1] <= 0.0f || (fArr2[0] > 45.0f && fArr2[0] < 85.0f)) {
                fArr2[0] = f;
            }
            fArr2[1] = Math.max(0.0f, Math.min(1.0f, fArr2[1] + 0.6f));
            float[] fArr3 = this.tempHSV;
            fArr3[2] = Math.max(0.0f, Math.min(1.0f, fArr3[2] - (fArr3[2] > 0.7f ? 0.25f : 0.125f)));
            return Theme.blendOver(i, Color.HSVToColor(255, this.tempHSV));
        }

        private int linkSelectionBackground(int i, int i2, boolean z) {
            Color.colorToHSV(ColorUtils.blendARGB(i, i2, 0.25f), this.tempHSV);
            float[] fArr = this.tempHSV;
            float f = 0.1f;
            fArr[1] = Math.max(0.0f, Math.min(1.0f, fArr[1] - 0.1f));
            float[] fArr2 = this.tempHSV;
            float f2 = fArr2[2];
            if (!z) {
                f = 0.0f;
            }
            fArr2[2] = Math.max(0.0f, Math.min(1.0f, f2 + f));
            return Color.HSVToColor(51, this.tempHSV);
        }

        private int averageColor(HashMap<String, Integer> hashMap, String... strArr) {
            int i = 0;
            int i2 = 0;
            int i3 = 0;
            int i4 = 0;
            for (int i5 = 0; i5 < strArr.length; i5++) {
                if (hashMap.containsKey(strArr[i5])) {
                    try {
                        int intValue = hashMap.get(strArr[i5]).intValue();
                        i2 += Color.red(intValue);
                        i3 += Color.green(intValue);
                        i4 += Color.blue(intValue);
                        i++;
                    } catch (Exception unused) {
                    }
                }
            }
            if (i == 0) {
                return 0;
            }
            return Color.argb(255, i2 / i, i3 / i, i4 / i);
        }

        public File getPathToWallpaper() {
            if (this.id < 100) {
                if (TextUtils.isEmpty(this.patternSlug)) {
                    return null;
                }
                return new File(ApplicationLoader.getFilesDirFixed(), String.format(Locale.US, "%s_%d_%s_v5.jpg", new Object[]{this.parentTheme.getKey(), Integer.valueOf(this.id), this.patternSlug}));
            } else if (TextUtils.isEmpty(this.patternSlug)) {
                return null;
            } else {
                return new File(ApplicationLoader.getFilesDirFixed(), String.format(Locale.US, "%s_%d_%s_v8_debug.jpg", new Object[]{this.parentTheme.getKey(), Integer.valueOf(this.id), this.patternSlug}));
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:77:0x02e6 A[SYNTHETIC, Splitter:B:77:0x02e6] */
        /* JADX WARNING: Removed duplicated region for block: B:85:0x02f4 A[SYNTHETIC, Splitter:B:85:0x02f4] */
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
                if (r0 != 0) goto L_0x024f
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
                if (r7 == 0) goto L_0x01dc
                if (r13 == 0) goto L_0x01dc
                java.lang.String r3 = "~"
                if (r4 == 0) goto L_0x01c3
                java.lang.StringBuilder r8 = new java.lang.StringBuilder
                r8.<init>()
                r8.append(r9)
                r8.append(r3)
                r8.append(r7)
                r8.append(r3)
                r8.append(r13)
                r8.append(r3)
                r8.append(r4)
                java.lang.String r9 = r8.toString()
                goto L_0x0206
            L_0x01c3:
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r9)
                r4.append(r3)
                r4.append(r7)
                r4.append(r3)
                r4.append(r13)
                java.lang.String r9 = r4.toString()
                goto L_0x0206
            L_0x01dc:
                if (r7 == 0) goto L_0x0206
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r9)
                java.lang.String r8 = "-"
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
            L_0x0206:
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
                if (r4 <= 0) goto L_0x0250
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r3)
                java.lang.String r3 = "&mode="
                r4.append(r3)
                java.lang.String r0 = r0.toString()
                r4.append(r0)
                java.lang.String r3 = r4.toString()
                goto L_0x0250
            L_0x024f:
                r3 = 0
            L_0x0250:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.util.Set r4 = r5.entrySet()
                java.util.Iterator r4 = r4.iterator()
            L_0x025d:
                boolean r5 = r4.hasNext()
                java.lang.String r7 = "\n"
                if (r5 == 0) goto L_0x029f
                java.lang.Object r5 = r4.next()
                java.util.Map$Entry r5 = (java.util.Map.Entry) r5
                java.lang.Object r8 = r5.getKey()
                java.lang.String r8 = (java.lang.String) r8
                if (r3 == 0) goto L_0x028c
                boolean r9 = r12.equals(r8)
                if (r9 != 0) goto L_0x025d
                boolean r9 = r11.equals(r8)
                if (r9 != 0) goto L_0x025d
                boolean r9 = r10.equals(r8)
                if (r9 != 0) goto L_0x025d
                boolean r9 = r6.equals(r8)
                if (r9 == 0) goto L_0x028c
                goto L_0x025d
            L_0x028c:
                r0.append(r8)
                java.lang.String r8 = "="
                r0.append(r8)
                java.lang.Object r5 = r5.getValue()
                r0.append(r5)
                r0.append(r7)
                goto L_0x025d
            L_0x029f:
                java.io.FileOutputStream r4 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x02df, all -> 0x02db }
                r4.<init>(r2)     // Catch:{ Exception -> 0x02df, all -> 0x02db }
                java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x02d8, all -> 0x02d4 }
                byte[] r0 = org.telegram.messenger.AndroidUtilities.getStringBytes(r0)     // Catch:{ Exception -> 0x02d8, all -> 0x02d4 }
                r4.write(r0)     // Catch:{ Exception -> 0x02d8, all -> 0x02d4 }
                boolean r0 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x02d8, all -> 0x02d4 }
                if (r0 != 0) goto L_0x02d0
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02d8, all -> 0x02d4 }
                r0.<init>()     // Catch:{ Exception -> 0x02d8, all -> 0x02d4 }
                java.lang.String r5 = "WLS="
                r0.append(r5)     // Catch:{ Exception -> 0x02d8, all -> 0x02d4 }
                r0.append(r3)     // Catch:{ Exception -> 0x02d8, all -> 0x02d4 }
                r0.append(r7)     // Catch:{ Exception -> 0x02d8, all -> 0x02d4 }
                java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x02d8, all -> 0x02d4 }
                byte[] r0 = org.telegram.messenger.AndroidUtilities.getStringBytes(r0)     // Catch:{ Exception -> 0x02d8, all -> 0x02d4 }
                r4.write(r0)     // Catch:{ Exception -> 0x02d8, all -> 0x02d4 }
            L_0x02d0:
                r4.close()     // Catch:{ Exception -> 0x02ea }
                goto L_0x02ef
            L_0x02d4:
                r0 = move-exception
                r2 = r0
                r3 = r4
                goto L_0x02f2
            L_0x02d8:
                r0 = move-exception
                r3 = r4
                goto L_0x02e1
            L_0x02db:
                r0 = move-exception
                r2 = r0
                r3 = 0
                goto L_0x02f2
            L_0x02df:
                r0 = move-exception
                r3 = 0
            L_0x02e1:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x02f0 }
                if (r3 == 0) goto L_0x02ef
                r3.close()     // Catch:{ Exception -> 0x02ea }
                goto L_0x02ef
            L_0x02ea:
                r0 = move-exception
                r3 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            L_0x02ef:
                return r2
            L_0x02f0:
                r0 = move-exception
                r2 = r0
            L_0x02f2:
                if (r3 == 0) goto L_0x02fd
                r3.close()     // Catch:{ Exception -> 0x02f8 }
                goto L_0x02fd
            L_0x02f8:
                r0 = move-exception
                r3 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            L_0x02fd:
                goto L_0x02ff
            L_0x02fe:
                throw r2
            L_0x02ff:
                goto L_0x02fe
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.ThemeAccent.saveToFile():java.io.File");
        }
    }

    public static int blendOver(int i, int i2) {
        float alpha = ((float) Color.alpha(i2)) / 255.0f;
        float alpha2 = ((float) Color.alpha(i)) / 255.0f;
        float f = 1.0f - alpha;
        float f2 = (alpha2 * f) + alpha;
        if (f2 == 0.0f) {
            return 0;
        }
        return Color.argb((int) (255.0f * f2), (int) (((((float) Color.red(i2)) * alpha) + ((((float) Color.red(i)) * alpha2) * f)) / f2), (int) (((((float) Color.green(i2)) * alpha) + ((((float) Color.green(i)) * alpha2) * f)) / f2), (int) (((((float) Color.blue(i2)) * alpha) + ((((float) Color.blue(i)) * alpha2) * f)) / f2));
    }

    public static class OverrideWallpaperInfo {
        public long accessHash;
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
        public LongSparseArray<ThemeAccent> chatAccentsByThemeId = new LongSparseArray<>();
        public int currentAccentId;
        public int defaultAccentCount;
        public boolean firstAccentIsDefault;
        public TLRPC$TL_theme info;
        public boolean isBlured;
        /* access modifiers changed from: private */
        public int isDark = -1;
        public boolean isMotion;
        public int lastAccentId = 100;
        public int lastChatThemeId = 0;
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

        public ThemeInfo(ThemeInfo themeInfo) {
            this.name = themeInfo.name;
            this.pathToFile = themeInfo.pathToFile;
            this.pathToWallpaper = themeInfo.pathToWallpaper;
            this.assetName = themeInfo.assetName;
            this.slug = themeInfo.slug;
            this.badWallpaper = themeInfo.badWallpaper;
            this.isBlured = themeInfo.isBlured;
            this.isMotion = themeInfo.isMotion;
            this.patternBgColor = themeInfo.patternBgColor;
            this.patternBgGradientColor1 = themeInfo.patternBgGradientColor1;
            this.patternBgGradientColor2 = themeInfo.patternBgGradientColor2;
            this.patternBgGradientColor3 = themeInfo.patternBgGradientColor3;
            this.patternBgGradientRotation = themeInfo.patternBgGradientRotation;
            this.patternIntensity = themeInfo.patternIntensity;
            this.account = themeInfo.account;
            this.info = themeInfo.info;
            this.loaded = themeInfo.loaded;
            this.uploadingThumb = themeInfo.uploadingThumb;
            this.uploadingFile = themeInfo.uploadingFile;
            this.uploadedThumb = themeInfo.uploadedThumb;
            this.uploadedFile = themeInfo.uploadedFile;
            this.previewBackgroundColor = themeInfo.previewBackgroundColor;
            this.previewBackgroundGradientColor1 = themeInfo.previewBackgroundGradientColor1;
            this.previewBackgroundGradientColor2 = themeInfo.previewBackgroundGradientColor2;
            this.previewBackgroundGradientColor3 = themeInfo.previewBackgroundGradientColor3;
            this.previewWallpaperOffset = themeInfo.previewWallpaperOffset;
            this.previewInColor = themeInfo.previewInColor;
            this.previewOutColor = themeInfo.previewOutColor;
            this.firstAccentIsDefault = themeInfo.firstAccentIsDefault;
            this.previewParsed = themeInfo.previewParsed;
            this.themeLoaded = themeInfo.themeLoaded;
            this.sortIndex = themeInfo.sortIndex;
            this.defaultAccentCount = themeInfo.defaultAccentCount;
            this.accentBaseColor = themeInfo.accentBaseColor;
            this.currentAccentId = themeInfo.currentAccentId;
            this.prevAccentId = themeInfo.prevAccentId;
            this.themeAccentsMap = themeInfo.themeAccentsMap;
            this.themeAccents = themeInfo.themeAccents;
            this.accentsByThemeId = themeInfo.accentsByThemeId;
            this.lastAccentId = themeInfo.lastAccentId;
            this.loadingThemeWallpaperName = themeInfo.loadingThemeWallpaperName;
            this.newPathToWallpaper = themeInfo.newPathToWallpaper;
            this.overrideWallpaper = themeInfo.overrideWallpaper;
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
        public boolean isDefaultMyMessagesBubbles() {
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
            if (themeAccent != null && themeAccent2 != null && themeAccent.myMessagesAccentColor == themeAccent2.myMessagesAccentColor && themeAccent.myMessagesGradientAccentColor1 == themeAccent2.myMessagesGradientAccentColor1 && themeAccent.myMessagesGradientAccentColor2 == themeAccent2.myMessagesGradientAccentColor2 && themeAccent.myMessagesGradientAccentColor3 == themeAccent2.myMessagesGradientAccentColor3 && themeAccent.myMessagesAnimated == themeAccent2.myMessagesAnimated) {
                return true;
            }
            return false;
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
            if (themeAccent != null && themeAccent2 != null && themeAccent.accentColor2 == themeAccent2.accentColor2 && themeAccent.myMessagesAccentColor == themeAccent2.myMessagesAccentColor && themeAccent.myMessagesGradientAccentColor1 == themeAccent2.myMessagesGradientAccentColor1 && themeAccent.myMessagesGradientAccentColor2 == themeAccent2.myMessagesGradientAccentColor2 && themeAccent.myMessagesGradientAccentColor3 == themeAccent2.myMessagesGradientAccentColor3 && themeAccent.myMessagesAnimated == themeAccent2.myMessagesAnimated) {
                return true;
            }
            return false;
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
            int i = this.isDark;
            if (i != -1) {
                return i == 1;
            }
            if ("Dark Blue".equals(this.name) || "Night".equals(this.name)) {
                this.isDark = 1;
            } else if ("Blue".equals(this.name) || "Arctic Blue".equals(this.name) || "Day".equals(this.name)) {
                this.isDark = 0;
            }
            if (this.isDark == -1) {
                Theme.checkIsDark(Theme.getThemeFileValues(new File(this.pathToFile), (String) null, new String[1]), this);
            }
            if (this.isDark == 1) {
                return true;
            }
            return false;
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
                        themeInfo.info = TLRPC$Theme.TLdeserialize(serializedData, serializedData.readInt32(true), true);
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
        public void setAccentColorOptions(int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4, int[] iArr5, int[] iArr6, int[] iArr7, int[] iArr8, String[] strArr, int[] iArr9, int[] iArr10) {
            int[] iArr11 = iArr;
            this.defaultAccentCount = iArr11.length;
            this.themeAccents = new ArrayList<>();
            this.themeAccentsMap = new SparseArray<>();
            this.accentsByThemeId = new LongSparseArray<>();
            for (int i = 0; i < iArr11.length; i++) {
                ThemeAccent themeAccent = new ThemeAccent();
                themeAccent.id = iArr8 != null ? iArr8[i] : i;
                if (Theme.isHome(themeAccent)) {
                    themeAccent.isDefault = true;
                }
                themeAccent.accentColor = iArr11[i];
                themeAccent.parentTheme = this;
                if (iArr2 != null) {
                    themeAccent.myMessagesAccentColor = iArr2[i];
                }
                if (iArr3 != null) {
                    themeAccent.myMessagesGradientAccentColor1 = iArr3[i];
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
                        themeAccent.backgroundGradientOverrideColor1 = (long) iArr5[i];
                    } else {
                        themeAccent.backgroundGradientOverrideColor1 = 4294967296L;
                    }
                }
                if (iArr6 != null) {
                    if (!this.firstAccentIsDefault || themeAccent.id != Theme.DEFALT_THEME_ACCENT_ID) {
                        themeAccent.backgroundGradientOverrideColor2 = (long) iArr6[i];
                    } else {
                        themeAccent.backgroundGradientOverrideColor2 = 4294967296L;
                    }
                }
                if (iArr7 != null) {
                    if (!this.firstAccentIsDefault || themeAccent.id != Theme.DEFALT_THEME_ACCENT_ID) {
                        themeAccent.backgroundGradientOverrideColor3 = (long) iArr7[i];
                    } else {
                        themeAccent.backgroundGradientOverrideColor3 = 4294967296L;
                    }
                }
                if (strArr != null) {
                    themeAccent.patternIntensity = ((float) iArr10[i]) / 100.0f;
                    themeAccent.backgroundRotation = iArr9[i];
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
            NotificationCenter.getInstance(this.account).addObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(this.account).addObserver(this, NotificationCenter.fileLoadFailed);
        }

        /* access modifiers changed from: private */
        public void removeObservers() {
            NotificationCenter.getInstance(this.account).removeObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(this.account).removeObserver(this, NotificationCenter.fileLoadFailed);
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
                Object[] objArr = new Object[5];
                objArr[0] = this;
                if (this == Theme.currentNightTheme) {
                    z = true;
                }
                objArr[1] = Boolean.valueOf(z);
                objArr[2] = null;
                objArr[3] = -1;
                objArr[4] = Theme.fallbackKeys;
                globalInstance.postNotificationName(i, objArr);
            }
        }

        public static boolean accentEquals(ThemeAccent themeAccent, TLRPC$ThemeSettings tLRPC$ThemeSettings) {
            long j;
            long j2;
            int i;
            float f;
            String str;
            int i2;
            TLRPC$WallPaperSettings tLRPC$WallPaperSettings;
            long j3;
            long j4;
            long j5;
            long j6;
            ThemeAccent themeAccent2 = themeAccent;
            TLRPC$ThemeSettings tLRPC$ThemeSettings2 = tLRPC$ThemeSettings;
            int intValue = tLRPC$ThemeSettings2.message_colors.size() > 0 ? tLRPC$ThemeSettings2.message_colors.get(0).intValue() | -16777216 : 0;
            int intValue2 = tLRPC$ThemeSettings2.message_colors.size() > 1 ? tLRPC$ThemeSettings2.message_colors.get(1).intValue() | -16777216 : 0;
            if (intValue == intValue2) {
                intValue2 = 0;
            }
            int intValue3 = tLRPC$ThemeSettings2.message_colors.size() > 2 ? tLRPC$ThemeSettings2.message_colors.get(2).intValue() | -16777216 : 0;
            int intValue4 = tLRPC$ThemeSettings2.message_colors.size() > 3 ? -16777216 | tLRPC$ThemeSettings2.message_colors.get(3).intValue() : 0;
            TLRPC$WallPaper tLRPC$WallPaper = tLRPC$ThemeSettings2.wallpaper;
            long j7 = 0;
            if (tLRPC$WallPaper == null || (tLRPC$WallPaperSettings = tLRPC$WallPaper.settings) == null) {
                j2 = 0;
                j = 0;
                i2 = 0;
                str = null;
                f = 0.0f;
                i = 0;
            } else {
                i = Theme.getWallpaperColor(tLRPC$WallPaperSettings.background_color);
                int i3 = tLRPC$ThemeSettings2.wallpaper.settings.second_background_color;
                if (i3 == 0) {
                    j3 = 4294967296L;
                } else {
                    j3 = (long) Theme.getWallpaperColor(i3);
                }
                int i4 = tLRPC$ThemeSettings2.wallpaper.settings.third_background_color;
                if (i4 == 0) {
                    j4 = 4294967296L;
                } else {
                    j4 = (long) Theme.getWallpaperColor(i4);
                }
                int i5 = tLRPC$ThemeSettings2.wallpaper.settings.fourth_background_color;
                if (i5 == 0) {
                    j5 = 4294967296L;
                } else {
                    j5 = (long) Theme.getWallpaperColor(i5);
                }
                int wallpaperRotation = AndroidUtilities.getWallpaperRotation(tLRPC$ThemeSettings2.wallpaper.settings.rotation, false);
                TLRPC$WallPaper tLRPC$WallPaper2 = tLRPC$ThemeSettings2.wallpaper;
                if ((tLRPC$WallPaper2 instanceof TLRPC$TL_wallPaperNoFile) || !tLRPC$WallPaper2.pattern) {
                    i2 = wallpaperRotation;
                    j6 = j4;
                    str = null;
                    f = 0.0f;
                } else {
                    String str2 = tLRPC$WallPaper2.slug;
                    j6 = j4;
                    f = ((float) tLRPC$WallPaper2.settings.intensity) / 100.0f;
                    str = str2;
                    i2 = wallpaperRotation;
                }
                long j8 = j5;
                j7 = j3;
                j2 = j6;
                j = j8;
            }
            return tLRPC$ThemeSettings2.accent_color == themeAccent2.accentColor && tLRPC$ThemeSettings2.outbox_accent_color == themeAccent2.accentColor2 && intValue == themeAccent2.myMessagesAccentColor && intValue2 == themeAccent2.myMessagesGradientAccentColor1 && intValue3 == themeAccent2.myMessagesGradientAccentColor2 && intValue4 == themeAccent2.myMessagesGradientAccentColor3 && tLRPC$ThemeSettings2.message_colors_animated == themeAccent2.myMessagesAnimated && ((long) i) == themeAccent2.backgroundOverrideColor && j7 == themeAccent2.backgroundGradientOverrideColor1 && j2 == themeAccent2.backgroundGradientOverrideColor2 && j == themeAccent2.backgroundGradientOverrideColor3 && i2 == themeAccent2.backgroundRotation && TextUtils.equals(str, themeAccent2.patternSlug) && ((double) Math.abs(f - themeAccent2.patternIntensity)) < 0.001d;
        }

        public static void fillAccentValues(ThemeAccent themeAccent, TLRPC$ThemeSettings tLRPC$ThemeSettings) {
            TLRPC$WallPaperSettings tLRPC$WallPaperSettings;
            themeAccent.accentColor = tLRPC$ThemeSettings.accent_color;
            themeAccent.accentColor2 = tLRPC$ThemeSettings.outbox_accent_color;
            themeAccent.myMessagesAccentColor = tLRPC$ThemeSettings.message_colors.size() > 0 ? tLRPC$ThemeSettings.message_colors.get(0).intValue() | -16777216 : 0;
            int intValue = tLRPC$ThemeSettings.message_colors.size() > 1 ? tLRPC$ThemeSettings.message_colors.get(1).intValue() | -16777216 : 0;
            themeAccent.myMessagesGradientAccentColor1 = intValue;
            if (themeAccent.myMessagesAccentColor == intValue) {
                themeAccent.myMessagesGradientAccentColor1 = 0;
            }
            themeAccent.myMessagesGradientAccentColor2 = tLRPC$ThemeSettings.message_colors.size() > 2 ? tLRPC$ThemeSettings.message_colors.get(2).intValue() | -16777216 : 0;
            themeAccent.myMessagesGradientAccentColor3 = tLRPC$ThemeSettings.message_colors.size() > 3 ? tLRPC$ThemeSettings.message_colors.get(3).intValue() | -16777216 : 0;
            themeAccent.myMessagesAnimated = tLRPC$ThemeSettings.message_colors_animated;
            TLRPC$WallPaper tLRPC$WallPaper = tLRPC$ThemeSettings.wallpaper;
            if (tLRPC$WallPaper != null && (tLRPC$WallPaperSettings = tLRPC$WallPaper.settings) != null) {
                int i = tLRPC$WallPaperSettings.background_color;
                if (i == 0) {
                    themeAccent.backgroundOverrideColor = 4294967296L;
                } else {
                    themeAccent.backgroundOverrideColor = (long) Theme.getWallpaperColor(i);
                }
                TLRPC$WallPaperSettings tLRPC$WallPaperSettings2 = tLRPC$ThemeSettings.wallpaper.settings;
                if ((tLRPC$WallPaperSettings2.flags & 16) == 0 || tLRPC$WallPaperSettings2.second_background_color != 0) {
                    themeAccent.backgroundGradientOverrideColor1 = (long) Theme.getWallpaperColor(tLRPC$WallPaperSettings2.second_background_color);
                } else {
                    themeAccent.backgroundGradientOverrideColor1 = 4294967296L;
                }
                TLRPC$WallPaperSettings tLRPC$WallPaperSettings3 = tLRPC$ThemeSettings.wallpaper.settings;
                if ((tLRPC$WallPaperSettings3.flags & 32) == 0 || tLRPC$WallPaperSettings3.third_background_color != 0) {
                    themeAccent.backgroundGradientOverrideColor2 = (long) Theme.getWallpaperColor(tLRPC$WallPaperSettings3.third_background_color);
                } else {
                    themeAccent.backgroundGradientOverrideColor2 = 4294967296L;
                }
                TLRPC$WallPaperSettings tLRPC$WallPaperSettings4 = tLRPC$ThemeSettings.wallpaper.settings;
                if ((tLRPC$WallPaperSettings4.flags & 64) == 0 || tLRPC$WallPaperSettings4.fourth_background_color != 0) {
                    themeAccent.backgroundGradientOverrideColor3 = (long) Theme.getWallpaperColor(tLRPC$WallPaperSettings4.fourth_background_color);
                } else {
                    themeAccent.backgroundGradientOverrideColor3 = 4294967296L;
                }
                themeAccent.backgroundRotation = AndroidUtilities.getWallpaperRotation(tLRPC$ThemeSettings.wallpaper.settings.rotation, false);
                TLRPC$WallPaper tLRPC$WallPaper2 = tLRPC$ThemeSettings.wallpaper;
                if (!(tLRPC$WallPaper2 instanceof TLRPC$TL_wallPaperNoFile) && tLRPC$WallPaper2.pattern) {
                    themeAccent.patternSlug = tLRPC$WallPaper2.slug;
                    TLRPC$WallPaperSettings tLRPC$WallPaperSettings5 = tLRPC$WallPaper2.settings;
                    themeAccent.patternIntensity = ((float) tLRPC$WallPaperSettings5.intensity) / 100.0f;
                    themeAccent.patternMotion = tLRPC$WallPaperSettings5.motion;
                }
            }
        }

        public ThemeAccent createNewAccent(TLRPC$ThemeSettings tLRPC$ThemeSettings) {
            ThemeAccent themeAccent = new ThemeAccent();
            fillAccentValues(themeAccent, tLRPC$ThemeSettings);
            themeAccent.parentTheme = this;
            return themeAccent;
        }

        public ThemeAccent createNewAccent(TLRPC$TL_theme tLRPC$TL_theme, int i) {
            return createNewAccent(tLRPC$TL_theme, i, false, 0);
        }

        public ThemeAccent createNewAccent(TLRPC$TL_theme tLRPC$TL_theme, int i, boolean z, int i2) {
            TLRPC$ThemeSettings tLRPC$ThemeSettings = null;
            if (tLRPC$TL_theme == null) {
                return null;
            }
            if (i2 < tLRPC$TL_theme.settings.size()) {
                tLRPC$ThemeSettings = tLRPC$TL_theme.settings.get(i2);
            }
            if (z) {
                ThemeAccent themeAccent = this.chatAccentsByThemeId.get(tLRPC$TL_theme.id);
                if (themeAccent != null) {
                    return themeAccent;
                }
                int i3 = this.lastChatThemeId + 1;
                this.lastChatThemeId = i3;
                ThemeAccent createNewAccent = createNewAccent(tLRPC$ThemeSettings);
                createNewAccent.id = i3;
                createNewAccent.info = tLRPC$TL_theme;
                createNewAccent.account = i;
                this.chatAccentsByThemeId.put((long) i3, createNewAccent);
                return createNewAccent;
            }
            ThemeAccent themeAccent2 = this.accentsByThemeId.get(tLRPC$TL_theme.id);
            if (themeAccent2 != null) {
                return themeAccent2;
            }
            int i4 = this.lastAccentId + 1;
            this.lastAccentId = i4;
            ThemeAccent createNewAccent2 = createNewAccent(tLRPC$ThemeSettings);
            createNewAccent2.id = i4;
            createNewAccent2.info = tLRPC$TL_theme;
            createNewAccent2.account = i;
            this.themeAccentsMap.put(i4, createNewAccent2);
            this.themeAccents.add(0, createNewAccent2);
            Theme.sortAccents(this);
            this.accentsByThemeId.put(tLRPC$TL_theme.id, createNewAccent2);
            return createNewAccent2;
        }

        public ThemeAccent getAccent(boolean z) {
            ThemeAccent themeAccent;
            if (this.themeAccents == null || (themeAccent = this.themeAccentsMap.get(this.currentAccentId)) == null) {
                return null;
            }
            if (!z) {
                return themeAccent;
            }
            int i = this.lastAccentId + 1;
            this.lastAccentId = i;
            ThemeAccent themeAccent2 = new ThemeAccent();
            themeAccent2.accentColor = themeAccent.accentColor;
            themeAccent2.accentColor2 = themeAccent.accentColor2;
            themeAccent2.myMessagesAccentColor = themeAccent.myMessagesAccentColor;
            themeAccent2.myMessagesGradientAccentColor1 = themeAccent.myMessagesGradientAccentColor1;
            themeAccent2.myMessagesGradientAccentColor2 = themeAccent.myMessagesGradientAccentColor2;
            themeAccent2.myMessagesGradientAccentColor3 = themeAccent.myMessagesGradientAccentColor3;
            themeAccent2.myMessagesAnimated = themeAccent.myMessagesAnimated;
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
            Theme.sortAccents(this);
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
                Bitmap scaledBitmap = AndroidUtilities.getScaledBitmap((float) AndroidUtilities.dp(640.0f), (float) AndroidUtilities.dp(360.0f), file.getAbsolutePath(), (String) null, 0);
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
            int i3 = NotificationCenter.fileLoaded;
            if (i == i3 || i == NotificationCenter.fileLoadFailed) {
                String str = objArr[0];
                TLRPC$TL_theme tLRPC$TL_theme = this.info;
                if (tLRPC$TL_theme != null && tLRPC$TL_theme.document != null) {
                    if (str.equals(this.loadingThemeWallpaperName)) {
                        this.loadingThemeWallpaperName = null;
                        Utilities.globalQueue.postRunnable(new Theme$ThemeInfo$$ExternalSyntheticLambda1(this, objArr[1]));
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
                            ConnectionsManager.getInstance(fillThemeValues.account).sendRequest(tLRPC$TL_account_getWallPaper, new Theme$ThemeInfo$$ExternalSyntheticLambda3(this, fillThemeValues));
                        }
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$didReceivedNotification$0(File file) {
            createBackground(file, this.newPathToWallpaper);
            AndroidUtilities.runOnUIThread(new Theme$ThemeInfo$$ExternalSyntheticLambda0(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$didReceivedNotification$2(ThemeInfo themeInfo, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Theme$ThemeInfo$$ExternalSyntheticLambda2(this, tLObject, themeInfo));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$didReceivedNotification$1(TLObject tLObject, ThemeInfo themeInfo) {
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

    public interface ResourcesProvider {
        void applyServiceShaderMatrix(int i, int i2, float f, float f2);

        Integer getColor(String str);

        int getColorOrDefault(String str);

        Integer getCurrentColor(String str);

        Drawable getDrawable(String str);

        Paint getPaint(String str);

        boolean hasGradientService();

        void setAnimatedColor(String str, int i);

        /* renamed from: org.telegram.ui.ActionBar.Theme$ResourcesProvider$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static Drawable $default$getDrawable(ResourcesProvider resourcesProvider, String str) {
                return null;
            }

            public static Paint $default$getPaint(ResourcesProvider resourcesProvider, String str) {
                return null;
            }

            public static boolean $default$hasGradientService(ResourcesProvider resourcesProvider) {
                return false;
            }

            public static void $default$setAnimatedColor(ResourcesProvider resourcesProvider, String str, int i) {
            }

            public static int $default$getColorOrDefault(ResourcesProvider _this, String str) {
                Integer color = _this.getColor(str);
                return color != null ? color.intValue() : Theme.getColor(str);
            }

            public static Integer $default$getCurrentColor(ResourcesProvider _this, String str) {
                return _this.getColor(str);
            }

            public static void $default$applyServiceShaderMatrix(ResourcesProvider _this, int i, int i2, float f, float f2) {
                Theme.applyServiceShaderMatrix(i, i2, f, f2);
            }
        }
    }

    /* JADX WARNING: type inference failed for: r1v76, types: [boolean] */
    /* JADX WARNING: type inference failed for: r1v82 */
    /* JADX WARNING: type inference failed for: r1v86 */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x37ca A[Catch:{ Exception -> 0x39b4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x37e3 A[Catch:{ Exception -> 0x39b4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x37e6 A[Catch:{ Exception -> 0x39b4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x37f3 A[Catch:{ Exception -> 0x39b4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:233:0x3801 A[SYNTHETIC] */
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
            r4 = 4
            boolean[] r6 = new boolean[r4]
            loadingRemoteThemes = r6
            int[] r6 = new int[r4]
            lastLoadingThemesTime = r6
            long[] r6 = new long[r4]
            remoteThemesHash = r6
            r6 = 12
            android.graphics.drawable.Drawable[] r6 = new android.graphics.drawable.Drawable[r6]
            avatarDrawables = r6
            r6 = 6
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
            org.telegram.ui.Components.RLottieDrawable[] r8 = new org.telegram.ui.Components.RLottieDrawable[r6]
            chat_attachButtonDrawables = r8
            android.graphics.drawable.Drawable[] r8 = new android.graphics.drawable.Drawable[r7]
            chat_locationDrawable = r8
            android.graphics.drawable.Drawable[] r8 = new android.graphics.drawable.Drawable[r7]
            chat_contactDrawable = r8
            int[] r8 = new int[r7]
            r8 = {10, 2} // fill-array
            java.lang.Class<android.graphics.drawable.Drawable> r9 = android.graphics.drawable.Drawable.class
            java.lang.Object r8 = java.lang.reflect.Array.newInstance(r9, r8)
            android.graphics.drawable.Drawable[][] r8 = (android.graphics.drawable.Drawable[][]) r8
            chat_fileStatesDrawable = r8
            int[] r8 = new int[r7]
            r8 = {13, 2} // fill-array
            java.lang.Class<android.graphics.drawable.Drawable> r9 = android.graphics.drawable.Drawable.class
            java.lang.Object r8 = java.lang.reflect.Array.newInstance(r9, r8)
            android.graphics.drawable.Drawable[][] r8 = (android.graphics.drawable.Drawable[][]) r8
            chat_photoStatesDrawables = r8
            android.graphics.Path[] r8 = new android.graphics.Path[r7]
            chat_filePath = r8
            r8 = 3
            android.graphics.Path[] r9 = new android.graphics.Path[r8]
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
            r10[r8] = r11
            java.lang.String r11 = "avatar_backgroundCyan"
            r10[r4] = r11
            java.lang.String r11 = "avatar_backgroundBlue"
            r12 = 5
            r10[r12] = r11
            java.lang.String r11 = "avatar_backgroundPink"
            r10[r6] = r11
            keys_avatar_background = r10
            java.lang.String[] r10 = new java.lang.String[r9]
            java.lang.String r11 = "avatar_nameInMessageRed"
            r10[r1] = r11
            java.lang.String r11 = "avatar_nameInMessageOrange"
            r10[r5] = r11
            java.lang.String r11 = "avatar_nameInMessageViolet"
            r10[r7] = r11
            java.lang.String r11 = "avatar_nameInMessageGreen"
            r10[r8] = r11
            java.lang.String r11 = "avatar_nameInMessageCyan"
            r10[r4] = r11
            java.lang.String r11 = "avatar_nameInMessageBlue"
            r10[r12] = r11
            java.lang.String r11 = "avatar_nameInMessagePink"
            r10[r6] = r11
            keys_avatar_nameInMessage = r10
            java.util.HashMap r10 = new java.util.HashMap
            r10.<init>()
            defaultChatDrawables = r10
            java.util.HashMap r10 = new java.util.HashMap
            r10.<init>()
            defaultChatDrawableColorKeys = r10
            java.util.HashMap r10 = new java.util.HashMap
            r10.<init>()
            defaultChatPaints = r10
            java.util.HashMap r10 = new java.util.HashMap
            r10.<init>()
            defaultChatPaintColors = r10
            java.util.HashSet r10 = new java.util.HashSet
            r10.<init>()
            myMessagesColorKeys = r10
            java.util.HashSet r10 = new java.util.HashSet
            r10.<init>()
            myMessagesBubblesColorKeys = r10
            java.util.HashSet r10 = new java.util.HashSet
            r10.<init>()
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
            r13 = -1743531(0xffffffffffe56555, float:NaN)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogSwipeRemove"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r13 = -1026983(0xffffffffffvar_, float:NaN)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.String r14 = "dialogReactionMentionBackground"
            r10.put(r14, r13)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            java.lang.String r13 = "windowBackgroundWhite"
            r10.put(r13, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -6445135(0xffffffffff9da7b1, float:NaN)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundUnchecked"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -11034919(0xfffffffffvar_ed9, float:-2.866088E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundChecked"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            java.lang.String r14 = "windowBackgroundCheckText"
            r10.put(r14, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -14904349(0xffffffffff1CLASSNAMEe3, float:-2.0812744E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "progressCircle"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -8288629(0xfffffffffvar_b, float:NaN)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteGrayIcon"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -12545331(0xfffffffffvar_cd, float:-2.55974E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteBlueText"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -12937771(0xffffffffff3a95d5, float:-2.4801438E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteBlueText2"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -14255946(0xfffffffffvar_b6, float:-2.2127861E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteBlueText3"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -14904349(0xffffffffff1CLASSNAMEe3, float:-2.0812744E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteBlueText4"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -11759926(0xffffffffff4c8eca, float:-2.7190391E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteBlueText5"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r14 = -12940081(0xffffffffff3a8ccf, float:-2.4796753E38)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            java.lang.String r15 = "windowBackgroundWhiteBlueText6"
            r10.put(r15, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r10 = defaultColors
            r15 = -13141330(0xfffffffffvar_aae, float:-2.4388571E38)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            java.lang.String r9 = "windowBackgroundWhiteBlueText7"
            r10.put(r9, r15)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -14776109(0xffffffffff1e88d3, float:-2.1072846E38)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteBlueButton"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -13132315(0xfffffffffvar_de5, float:-2.4406856E38)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteBlueIcon"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -14248148(0xfffffffffvar_c, float:-2.2143678E38)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteGreenText"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -13129704(0xfffffffffvar_a818, float:-2.4412152E38)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteGreenText2"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -3319206(0xffffffffffcd5a5a, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteRedText"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -2404015(0xffffffffffdb5151, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteRedText2"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -2995895(0xffffffffffd24949, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteRedText3"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -3198928(0xffffffffffcvar_, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteRedText4"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -1230535(0xffffffffffed3939, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteRedText5"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -39322(0xfffffffffffvar_, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteRedText6"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -8156010(0xfffffffffvar_CLASSNAME, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteGrayText"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -8223094(0xfffffffffvar_a, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteGrayText2"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -6710887(0xfffffffffvar_, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteGrayText3"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -8355712(0xfffffffffvar_, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteGrayText4"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -6052957(0xffffffffffa3a3a3, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteGrayText5"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -9079435(0xfffffffffvar_, float:-3.2627073E38)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteGrayText6"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -3750202(0xffffffffffc6c6c6, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteGrayText7"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -9605774(0xffffffffff6d6d72, float:-3.155953E38)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteGrayText8"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -2368549(0xffffffffffdbdbdb, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteGrayLine"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            java.lang.String r10 = "windowBackgroundWhiteBlackText"
            r9.put(r10, r11)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -5723992(0xffffffffffa8a8a8, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteHintText"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -12937771(0xffffffffff3a95d5, float:-2.4801438E38)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteValueText"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -14255946(0xfffffffffvar_b6, float:-2.2127861E38)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteLinkText"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = 862104035(0x3362a9e3, float:5.2774237E-8)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteLinkSelection"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -12937771(0xffffffffff3a95d5, float:-2.4801438E38)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteBlueHeader"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -2368549(0xffffffffffdbdbdb, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteInputField"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -13129232(0xfffffffffvar_a9f0, float:-2.4413109E38)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundWhiteInputFieldActivated"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -5196358(0xffffffffffb0b5ba, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "switchTrack"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -11358743(0xfffffffffvar_ade9, float:-2.8004087E38)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "switchTrackChecked"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -8221031(0xfffffffffvar_e99, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "switchTrackBlue"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -12810041(0xffffffffff3CLASSNAMEc7, float:-2.5060505E38)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "switchTrackBlueChecked"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            java.lang.String r10 = "switchTrackBlueThumb"
            r9.put(r10, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            java.lang.String r10 = "switchTrackBlueThumbChecked"
            r9.put(r10, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = 390089299(0x17404a53, float:6.2132356E-25)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "switchTrackBlueSelector"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = 553797505(0x21024781, float:4.414035E-19)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "switchTrackBlueSelectorChecked"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -688514(0xffffffffffvar_e7e, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "switch2Track"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -11358743(0xfffffffffvar_ade9, float:-2.8004087E38)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "switch2TrackChecked"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -12345121(0xfffffffffvar_a0df, float:-2.6003475E38)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "checkboxSquareBackground"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            java.lang.String r10 = "checkboxSquareCheck"
            r9.put(r10, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -9211021(0xfffffffffvar_, float:-3.2360185E38)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "checkboxSquareUnchecked"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -5197648(0xffffffffffb0b0b0, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "checkboxSquareDisabled"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = 251658240(0xvar_, float:6.3108872E-30)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "listSelectorSDK21"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -5000269(0xffffffffffb3b3b3, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "radioBackground"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -13129232(0xfffffffffvar_a9f0, float:-2.4413109E38)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "radioBackgroundChecked"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -986896(0xfffffffffff0f0f0, float:NaN)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundGray"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r10 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r15 = "windowBackgroundGrayShadow"
            r9.put(r15, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = defaultColors
            r15 = -6974059(0xfffffffffvar_, float:NaN)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            java.lang.String r6 = "emptyListPlaceholder"
            r9.put(r6, r15)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r9 = -2500135(0xffffffffffd9d9d9, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "divider"
            r6.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r9 = -657931(0xfffffffffff5f5f5, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "graySection"
            r6.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r9 = -8222838(0xfffffffffvar_a, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "key_graySectionText"
            r6.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r9 = -4202506(0xffffffffffbfdff6, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "contextProgressInner1"
            r6.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r9 = -13920542(0xffffffffff2b96e2, float:-2.2808142E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "contextProgressOuter1"
            r6.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r9 = -4202506(0xffffffffffbfdff6, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "contextProgressInner2"
            r6.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r9 = "contextProgressOuter2"
            r6.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r9 = -5000269(0xffffffffffb3b3b3, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "contextProgressInner3"
            r6.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r9 = "contextProgressOuter3"
            r6.put(r9, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r9 = -3486256(0xffffffffffcacdd0, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "contextProgressInner4"
            r6.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r9 = -13683656(0xffffffffff2var_, float:-2.3288603E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.String r15 = "contextProgressOuter4"
            r6.put(r15, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r15 = -11361317(0xfffffffffvar_a3db, float:-2.7998867E38)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            java.lang.String r12 = "fastScrollActive"
            r6.put(r12, r15)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -3551791(0xffffffffffc9cdd1, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "fastScrollInactive"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "fastScrollText"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "avatar_text"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -10043398(0xfffffffffvar_bffa, float:-3.0671924E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "avatar_backgroundSaved"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -5654847(0xffffffffffa9b6c1, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "avatar_backgroundArchived"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -10043398(0xfffffffffvar_bffa, float:-3.0671924E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "avatar_backgroundArchivedHidden"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -1743531(0xffffffffffe56555, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "avatar_backgroundRed"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -881592(0xffffffffffvar_CLASSNAME, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "avatar_backgroundOrange"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -7436818(0xffffffffff8e85ee, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "avatar_backgroundViolet"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -8992691(0xfffffffffvar_CLASSNAMEd, float:-3.280301E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "avatar_backgroundGreen"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -10502443(0xffffffffff5fbed5, float:-2.974087E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "avatar_backgroundCyan"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -11232035(0xfffffffffvar_cdd, float:-2.8261082E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "avatar_backgroundBlue"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -887654(0xffffffffffvar_a, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "avatar_backgroundPink"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -11500111(0xfffffffffvar_b1, float:-2.7717359E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "avatar_backgroundInProfileBlue"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -10907718(0xfffffffffvar_fba, float:-2.8918875E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "avatar_backgroundActionBarBlue"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -2626822(0xffffffffffd7eafa, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "avatar_subtitleInProfileBlue"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -11959891(0xfffffffffvar_ad, float:-2.6784814E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "avatar_actionBarSelectorBlue"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "avatar_actionBarIconBlue"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -3516848(0xffffffffffca5650, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "avatar_nameInMessageRed"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -2589911(0xffffffffffd87b29, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "avatar_nameInMessageOrange"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -11627828(0xffffffffff4e92cc, float:-2.7458318E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "avatar_nameInMessageViolet"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -11488718(0xfffffffffvar_b232, float:-2.7740467E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "avatar_nameInMessageGreen"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -13132104(0xfffffffffvar_eb8, float:-2.4407284E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "avatar_nameInMessageCyan"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -11627828(0xffffffffff4e92cc, float:-2.7458318E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "avatar_nameInMessageBlue"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -11627828(0xffffffffff4e92cc, float:-2.7458318E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "avatar_nameInMessagePink"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -11371101(0xfffffffffvar_da3, float:-2.7979022E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "actionBarDefault"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "actionBarDefaultIcon"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "actionBarActionModeDefault"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = 268435456(0x10000000, float:2.5243549E-29)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "actionBarActionModeDefaultTop"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -9999761(0xfffffffffvar_a6f, float:-3.076043E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "actionBarActionModeDefaultIcon"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "actionBarDefaultTitle"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -2758409(0xffffffffffd5e8f7, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "actionBarDefaultSubtitle"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -12554860(0xfffffffffvar_d94, float:-2.5578074E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "actionBarDefaultSelector"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = 486539264(0x1d000000, float:1.6940659E-21)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "actionBarWhiteSelector"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "actionBarDefaultSearch"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -1996488705(0xfffffffvar_ffffff, float:-1.5407439E-33)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "actionBarDefaultSearchPlaceholder"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "actionBarDefaultSubmenuItem"
            r6.put(r12, r11)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -9999504(0xfffffffffvar_b70, float:-3.0760951E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "actionBarDefaultSubmenuItemIcon"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "actionBarDefaultSubmenuBackground"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -657931(0xfffffffffff5f5f5, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "actionBarDefaultSubmenuSeparator"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -1907998(0xffffffffffe2e2e2, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "actionBarActionModeDefaultSelector"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "actionBarTabActiveText"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -2758409(0xffffffffffd5e8f7, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "actionBarTabUnactiveText"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "actionBarTabLine"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -12554860(0xfffffffffvar_d94, float:-2.5578074E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "actionBarTabSelector"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "actionBarBrowser"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -9471353(0xffffffffff6f7a87, float:-3.1832169E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "actionBarDefaultArchived"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -10590350(0xffffffffff5e6772, float:-2.9562573E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "actionBarDefaultArchivedSelector"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "actionBarDefaultArchivedIcon"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "actionBarDefaultArchivedTitle"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "actionBarDefaultArchivedSearch"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -1996488705(0xfffffffvar_ffffff, float:-1.5407439E-33)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "actionBarDefaultSearchArchivedPlaceholder"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -11810020(0xffffffffff4bcb1c, float:-2.7088789E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_onlineCircle"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -11613090(0xffffffffff4ecc5e, float:-2.748821E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_unreadCounter"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -3749428(0xffffffffffc6c9cc, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_unreadCounterMuted"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chats_unreadCounterText"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -10049056(0xfffffffffvar_a9e0, float:-3.0660448E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_archiveBackground"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -6313293(0xffffffffff9faab3, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_archivePinBackground"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chats_archiveIcon"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chats_archiveText"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chats_name"
            r6.put(r12, r11)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -11382190(0xfffffffffvar_, float:-2.7956531E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_nameArchived"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -16734706(0xfffffffffvar_a60e, float:-1.7100339E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_secretName"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -15093466(0xfffffffffvar_b126, float:-2.042917E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_secretIcon"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -14408668(0xfffffffffvar_, float:-2.1818104E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_nameIcon"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -5723992(0xffffffffffa8a8a8, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_pinnedIcon"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -7631473(0xffffffffff8b8d8f, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_message"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -7237231(0xfffffffffvar_, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_messageArchived"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -7434095(0xffffffffff8e9091, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_message_threeLines"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -2274503(0xffffffffffdd4b39, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_draft"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -12812624(0xffffffffff3c7eb0, float:-2.5055266E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_nameMessage"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -7631473(0xffffffffff8b8d8f, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_nameMessageArchived"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -12434359(0xfffffffffvar_, float:-2.5822479E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_nameMessage_threeLines"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -10592674(0xffffffffff5e5e5e, float:-2.955786E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_nameMessageArchived_threeLines"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -12812624(0xffffffffff3c7eb0, float:-2.5055266E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_attachMessage"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -12812624(0xffffffffff3c7eb0, float:-2.5055266E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_actionMessage"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -6973028(0xfffffffffvar_c, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_date"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = 134217728(0x8000000, float:3.85186E-34)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_pinnedOverlay"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = 251658240(0xvar_, float:6.3108872E-30)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_tabletSelectedOverlay"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -12146122(0xfffffffffvar_aa36, float:-2.6407093E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_sentCheck"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -12146122(0xfffffffffvar_aa36, float:-2.6407093E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_sentReadCheck"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -9061026(0xfffffffffvar_bd5e, float:-3.266441E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_sentClock"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -2796974(0xffffffffffd55252, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_sentError"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chats_sentErrorIcon"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -13391642(0xfffffffffvar_a8e6, float:-2.3880878E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_verifiedBackground"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chats_verifiedCheck"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -4341308(0xffffffffffbdc1c4, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_muteIcon"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chats_mentionIcon"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chats_menuBackground"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -12303292(0xfffffffffvar_, float:-2.6088314E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_menuItemText"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -10907718(0xfffffffffvar_fba, float:-2.8918875E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_menuItemCheck"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -7827048(0xfffffffffvar_, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_menuItemIcon"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chats_menuName"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chats_menuPhone"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -4004353(0xffffffffffc2e5ff, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_menuPhoneCats"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chats_menuCloud"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -12420183(0xfffffffffvar_ba9, float:-2.5851231E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_menuCloudBackgroundCats"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chats_actionIcon"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -10114592(0xfffffffffvar_a9e0, float:-3.0527525E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_actionBackground"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -11100714(0xfffffffffvar_dd6, float:-2.8527432E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_actionPressedBackground"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -9211021(0xfffffffffvar_, float:-3.2360185E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_actionUnreadIcon"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chats_actionUnreadBackground"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -855310(0xfffffffffff2f2f2, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_actionUnreadPressedBackground"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -10907718(0xfffffffffvar_fba, float:-2.8918875E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_menuTopBackgroundCats"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -3749428(0xffffffffffc6c9cc, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_archivePullDownBackground"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -10049056(0xfffffffffvar_a9e0, float:-3.0660448E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chats_archivePullDownBackgroundActive"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -12171706(0xfffffffffvar_, float:-2.6355202E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_attachMediaBanBackground"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_attachMediaBanText"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_attachCheckBoxCheck"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -12995849(0xfffffffffvar_b2f7, float:-2.4683642E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_attachCheckBoxBackground"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = 201326592(0xCLASSNAME, float:9.8607613E-32)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_attachPhotoBackground"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -13391883(0xfffffffffvar_a7f5, float:-2.388039E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_attachActiveTab"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -7169634(0xfffffffffvar_e, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_attachUnactiveTab"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -13421773(0xfffffffffvar_, float:-2.3819765E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_attachPermissionImage"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -1945520(0xffffffffffe25050, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_attachPermissionMark"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -9472134(0xffffffffff6var_a, float:-3.1830585E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_attachPermissionText"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -3355444(0xffffffffffcccccc, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_attachEmptyImage"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -12214795(0xfffffffffvar_df5, float:-2.6267807E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_attachGalleryBackground"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -13726231(0xffffffffff2e8de9, float:-2.3202251E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_attachGalleryText"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_attachGalleryIcon"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -1351584(0xffffffffffeb6060, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_attachAudioBackground"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -2209977(0xffffffffffde4747, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_attachAudioText"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_attachAudioIcon"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -13321743(0xfffffffffvar_b9f1, float:-2.402265E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_attachFileBackground"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -15423260(0xfffffffffvar_a8e4, float:-1.9760267E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_attachFileText"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_attachFileIcon"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -868277(0xfffffffffff2CLASSNAMEb, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_attachContactBackground"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -2121728(0xffffffffffdfa000, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_attachContactText"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_attachContactIcon"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -10436011(0xfffffffffvar_CLASSNAME, float:-2.987561E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_attachLocationBackground"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -12801233(0xffffffffff3cab2f, float:-2.507837E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_attachLocationText"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_attachLocationIcon"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -868277(0xfffffffffff2CLASSNAMEb, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_attachPollBackground"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -2121728(0xffffffffffdfa000, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_attachPollText"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_attachPollIcon"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -10436011(0xfffffffffvar_CLASSNAME, float:-2.987561E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_inPollCorrectAnswer"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -10436011(0xfffffffffvar_CLASSNAME, float:-2.987561E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_outPollCorrectAnswer"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -1351584(0xffffffffffeb6060, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_inPollWrongAnswer"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -1351584(0xffffffffffeb6060, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_outPollWrongAnswer"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -2758409(0xffffffffffd5e8f7, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_status"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -16725933(0xfffffffffvar_CLASSNAME, float:-1.7118133E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_inDownCall"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -47032(0xfffffffffffvar_, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_inUpCall"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -16725933(0xfffffffffvar_CLASSNAME, float:-1.7118133E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_outUpCall"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_lockIcon"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -5124893(0xffffffffffb1cce3, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_muteIcon"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_inBubble"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -1247235(0xffffffffffecf7fd, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_inBubbleSelected"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -14862509(0xffffffffff1d3753, float:-2.0897606E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_inBubbleShadow"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -1048610(0xffffffffffefffde, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_outBubble"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = 335544320(0x14000000, float:6.4623485E-27)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_outBubbleGradientSelectedOverlay"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -2492475(0xffffffffffd9f7c5, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_outBubbleSelected"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -14781172(0xffffffffff1e750c, float:-2.1062577E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_outBubbleShadow"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_inMediaIcon"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -1050370(0xffffffffffeff8fe, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_inMediaIconSelected"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -1048610(0xffffffffffefffde, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_outMediaIcon"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -1967921(0xffffffffffe1f8cf, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_outMediaIconSelected"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_messageTextIn"
            r6.put(r12, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_messageTextOut"
            r6.put(r12, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -14255946(0xfffffffffvar_b6, float:-2.2127861E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_messageLinkIn"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -14255946(0xfffffffffvar_b6, float:-2.2127861E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_messageLinkOut"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_serviceText"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_serviceLink"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            java.lang.String r12 = "chat_serviceIcon"
            r6.put(r12, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = 1711276032(0x66000000, float:1.5111573E23)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_mediaTimeBackground"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -10637232(0xffffffffff5db050, float:-2.9467485E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_outSentCheck"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -10637232(0xffffffffff5db050, float:-2.9467485E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_outSentCheckSelected"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -10637232(0xffffffffff5db050, float:-2.9467485E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_outSentCheckRead"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -10637232(0xffffffffff5db050, float:-2.9467485E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_outSentCheckReadSelected"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -9061026(0xfffffffffvar_bd5e, float:-3.266441E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_outSentClock"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -9061026(0xfffffffffvar_bd5e, float:-3.266441E38)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_outSentClockSelected"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r12 = -6182221(0xffffffffffa1aab3, float:NaN)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r15 = "chat_inSentClock"
            r6.put(r15, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r6 = defaultColors
            r15 = -7094838(0xfffffffffvar_bdca, float:NaN)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            java.lang.String r4 = "chat_inSentClockSelected"
            r6.put(r4, r15)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_mediaSentCheck"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_mediaSentClock"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_inViews"
            r4.put(r6, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7094838(0xfffffffffvar_bdca, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_inViewsSelected"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -9522601(0xffffffffff6eb257, float:-3.1728226E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_outViews"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -9522601(0xffffffffff6eb257, float:-3.1728226E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_outViewsSelected"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_mediaViews"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -4801083(0xffffffffffb6bdc5, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_inMenu"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -6766130(0xfffffffffvar_c1ce, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_inMenuSelected"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7221634(0xfffffffffvar_ce7e, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_outMenu"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -7221634(0xfffffffffvar_ce7e, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_outMenuSelected"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_mediaMenu"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11162801(0xfffffffffvar_ab4f, float:-2.8401505E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r15 = "chat_outInstant"
            r4.put(r15, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r15 = -12019389(0xfffffffffvar_, float:-2.6664138E38)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            java.lang.String r8 = "chat_outInstantSelected"
            r4.put(r8, r15)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_inInstant"
            r4.put(r8, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -13600331(0xfffffffffvar_b5, float:-2.3457607E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inInstantSelected"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -2411211(0xffffffffffdb3535, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_sentError"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_sentErrorIcon"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 671781104(0x280a90f0, float:7.691967E-15)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_selectedBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_previewDurationText"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_previewGameText"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_inPreviewInstantText"
            r4.put(r8, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_outPreviewInstantText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -13600331(0xfffffffffvar_b5, float:-2.3457607E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inPreviewInstantSelectedText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -12019389(0xfffffffffvar_, float:-2.6664138E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outPreviewInstantSelectedText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -1776928(0xffffffffffe4e2e0, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_secretTimeText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_stickerNameText"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_botButtonText"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_botProgress"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -13072697(0xfffffffffvar_c7, float:-2.4527776E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inForwardedNameText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_outForwardedNameText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10838983(0xffffffffff5a9CLASSNAME, float:-2.9058286E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inPsaNameText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10838983(0xffffffffff5a9CLASSNAME, float:-2.9058286E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outPsaNameText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_inViaBotNameText"
            r4.put(r8, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_outViaBotNameText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_stickerViaBotNameText"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10903592(0xfffffffffvar_fd8, float:-2.8927243E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inReplyLine"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -9520791(0xffffffffff6eb969, float:-3.1731897E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outReplyLine"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_stickerReplyLine"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_inReplyNameText"
            r4.put(r8, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_outReplyNameText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_stickerReplyNameText"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_inReplyMessageText"
            r4.put(r8, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_outReplyMessageText"
            r4.put(r8, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_inReplyMediaMessageText"
            r4.put(r8, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10112933(0xfffffffffvar_b05b, float:-3.053089E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outReplyMediaMessageText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7752511(0xfffffffffvar_b4c1, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inReplyMediaMessageSelectedText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10112933(0xfffffffffvar_b05b, float:-3.053089E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outReplyMediaMessageSelectedText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_stickerReplyMessageText"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -9390872(0xfffffffffvar_b4e8, float:-3.1995404E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inPreviewLine"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7812741(0xfffffffffvar_CLASSNAMEb, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outPreviewLine"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_inSiteNameText"
            r4.put(r8, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_outSiteNameText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11625772(0xffffffffff4e9ad4, float:-2.7462488E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inContactNameText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_outContactNameText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_inContactPhoneText"
            r4.put(r8, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_inContactPhoneSelectedText"
            r4.put(r8, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -13286860(0xfffffffffvar_, float:-2.4093401E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outContactPhoneText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -13286860(0xfffffffffvar_, float:-2.4093401E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outContactPhoneSelectedText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_mediaProgress"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_inAudioProgress"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -1048610(0xffffffffffefffde, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outAudioProgress"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -1050370(0xffffffffffeff8fe, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inAudioSelectedProgress"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -1967921(0xffffffffffe1f8cf, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outAudioSelectedProgress"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_mediaTimeText"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -4143413(0xffffffffffc0c6cb, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_adminText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7752511(0xfffffffffvar_b4c1, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_adminSelectedText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -9391780(0xfffffffffvar_b15c, float:-3.1993562E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outAdminText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -9391780(0xfffffffffvar_b15c, float:-3.1993562E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outAdminSelectedText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_inTimeText"
            r4.put(r8, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7752511(0xfffffffffvar_b4c1, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inTimeSelectedText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -9391780(0xfffffffffvar_b15c, float:-3.1993562E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outTimeText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -9391780(0xfffffffffvar_b15c, float:-3.1993562E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outTimeSelectedText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_inAudioPerfomerText"
            r4.put(r8, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_inAudioPerfomerSelectedText"
            r4.put(r8, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -13286860(0xfffffffffvar_, float:-2.4093401E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outAudioPerfomerText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -13286860(0xfffffffffvar_, float:-2.4093401E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outAudioPerfomerSelectedText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11625772(0xffffffffff4e9ad4, float:-2.7462488E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inAudioTitleText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_outAudioTitleText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_inAudioDurationText"
            r4.put(r8, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10112933(0xfffffffffvar_b05b, float:-3.053089E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outAudioDurationText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7752511(0xfffffffffvar_b4c1, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inAudioDurationSelectedText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10112933(0xfffffffffvar_b05b, float:-3.053089E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outAudioDurationSelectedText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -1774864(0xffffffffffe4eaf0, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inAudioSeekbar"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 1071966960(0x3fe4eaf0, float:1.7884197)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inAudioCacheSeekbar"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -4463700(0xffffffffffbbe3ac, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outAudioSeekbar"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 1069278124(0x3fbbe3ac, float:1.4678855)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outAudioCacheSeekbar"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -4399384(0xffffffffffbcdee8, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inAudioSeekbarSelected"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -5644906(0xffffffffffa9dd96, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outAudioSeekbarSelected"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -9259544(0xfffffffffvar_b5e8, float:-3.2261769E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inAudioSeekbarFill"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -8863118(0xfffffffffvar_CLASSNAME, float:-3.3065816E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outAudioSeekbarFill"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -2169365(0xffffffffffdee5eb, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inVoiceSeekbar"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -4463700(0xffffffffffbbe3ac, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outVoiceSeekbar"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -4399384(0xffffffffffbcdee8, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inVoiceSeekbarSelected"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -5644906(0xffffffffffa9dd96, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outVoiceSeekbarSelected"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -9259544(0xfffffffffvar_b5e8, float:-3.2261769E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inVoiceSeekbarFill"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -8863118(0xfffffffffvar_CLASSNAME, float:-3.3065816E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outVoiceSeekbarFill"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -1314571(0xffffffffffebf0f5, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inFileProgress"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -2427453(0xffffffffffdaf5c3, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outFileProgress"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -3413258(0xffffffffffcbeaf6, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inFileProgressSelected"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -3806041(0xffffffffffc5eca7, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outFileProgressSelected"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11625772(0xffffffffff4e9ad4, float:-2.7462488E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inFileNameText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_outFileNameText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_inFileInfoText"
            r4.put(r8, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10112933(0xfffffffffvar_b05b, float:-3.053089E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outFileInfoText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7752511(0xfffffffffvar_b4c1, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inFileInfoSelectedText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10112933(0xfffffffffvar_b05b, float:-3.053089E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outFileInfoSelectedText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -1314571(0xffffffffffebf0f5, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inFileBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -2427453(0xffffffffffdaf5c3, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outFileBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -3413258(0xffffffffffcbeaf6, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inFileBackgroundSelected"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -3806041(0xffffffffffc5eca7, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outFileBackgroundSelected"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_inVenueInfoText"
            r4.put(r8, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10112933(0xfffffffffvar_b05b, float:-3.053089E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outVenueInfoText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7752511(0xfffffffffvar_b4c1, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inVenueInfoSelectedText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10112933(0xfffffffffvar_b05b, float:-3.053089E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outVenueInfoSelectedText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_mediaInfoText"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 862104035(0x3362a9e3, float:5.2774237E-8)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_linkSelectBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 862104035(0x3362a9e3, float:5.2774237E-8)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outLinkSelectBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 1717742051(0x6662a9e3, float:2.6759717E23)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_textSelectBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -986379(0xfffffffffff0f2f5, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_emojiPanelBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11688214(0xffffffffff4da6ea, float:-2.733584E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_emojiPanelBadgeBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_emojiPanelBadgeText"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -1709586(0xffffffffffe5e9ee, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_emojiSearchBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7036497(0xfffffffffvar_a1af, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_emojiSearchIcon"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 301989888(0x12000000, float:4.0389678E-28)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_emojiPanelShadowLine"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7038047(0xfffffffffvar_ba1, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_emojiPanelEmptyText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -6445909(0xffffffffff9da4ab, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_emojiPanelIcon"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 184549376(0xb000000, float:2.4651903E-32)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_emojiPanelIconSelector"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7564905(0xffffffffff8CLASSNAME, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_emojiBottomPanelIcon"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10589834(0xffffffffff5e6976, float:-2.956362E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_emojiPanelIconSelected"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -1907225(0xffffffffffe2e5e7, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_emojiPanelStickerPackSelector"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11097104(0xfffffffffvar_abf0, float:-2.8534754E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_emojiPanelStickerPackSelectorLine"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7564905(0xffffffffff8CLASSNAME, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_emojiPanelBackspace"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_emojiPanelMasksIcon"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10305560(0xfffffffffvar_bfe8, float:-3.0140196E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_emojiPanelMasksIconSelected"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_emojiPanelTrendingTitle"
            r4.put(r8, r11)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -8221804(0xfffffffffvar_b94, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_emojiPanelStickerSetName"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -14184997(0xfffffffffvar_ddb, float:-2.2271763E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_emojiPanelStickerSetNameHighlight"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -5130564(0xffffffffffb1b6bc, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_emojiPanelStickerSetNameIcon"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7697782(0xffffffffff8a8a8a, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_emojiPanelTrendingDescription"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -13220017(0xfffffffffvar_f, float:-2.4228975E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_botKeyboardButtonText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -1775639(0xffffffffffe4e7e9, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_botKeyboardButtonBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -3354156(0xffffffffffccd1d4, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_botKeyboardButtonBackgroundPressed"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -6113849(0xffffffffffa2b5c7, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_unreadMessagesStartArrowIcon"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11102772(0xfffffffffvar_cc, float:-2.8523258E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_unreadMessagesStartText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_unreadMessagesStartBackground"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -6113849(0xffffffffffa2b5c7, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inFileIcon"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7883067(0xfffffffffvar_b6c5, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inFileSelectedIcon"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -8011912(0xfffffffffvar_bvar_, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outFileIcon"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -8011912(0xfffffffffvar_bvar_, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outFileSelectedIcon"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -1314571(0xffffffffffebf0f5, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inLocationBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -6113849(0xffffffffffa2b5c7, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inLocationIcon"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -2427453(0xffffffffffdaf5c3, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outLocationBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7880840(0xfffffffffvar_bvar_, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outLocationIcon"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -9259544(0xfffffffffvar_b5e8, float:-3.2261769E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inContactBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_inContactIcon"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -8863118(0xfffffffffvar_CLASSNAME, float:-3.3065816E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outContactBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -1048610(0xffffffffffefffde, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outContactIcon"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -12146122(0xfffffffffvar_aa36, float:-2.6407093E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outBroadcast"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_mediaBroadcast"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -9999761(0xfffffffffvar_a6f, float:-3.076043E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_searchPanelIcons"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -9999761(0xfffffffffvar_a6f, float:-3.076043E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_searchPanelText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -8421505(0xffffffffff7f7f7f, float:-3.3961514E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_secretChatStatusText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_fieldOverlayText"
            r4.put(r8, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_stickersHintPanel"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11032346(0xfffffffffvar_a8e6, float:-2.86661E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_replyPanelIcons"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7432805(0xffffffffff8e959b, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_replyPanelClose"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_replyPanelName"
            r4.put(r8, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_replyPanelMessage"
            r4.put(r8, r11)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -1513240(0xffffffffffe8e8e8, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_replyPanelLine"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_messagePanelBackground"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_messagePanelText"
            r4.put(r8, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -5985101(0xffffffffffa4acb3, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_messagePanelHint"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11230757(0xfffffffffvar_a1db, float:-2.8263674E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_messagePanelCursor"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_messagePanelShadow"
            r4.put(r8, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7432805(0xffffffffff8e959b, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_messagePanelIcons"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_recordedVoicePlayPause"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -2468275(0xffffffffffda564d, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_recordedVoiceDot"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10637848(0xffffffffff5dade8, float:-2.9466236E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_recordedVoiceBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -5120257(0xffffffffffb1deff, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_recordedVoiceProgress"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_recordedVoiceProgressInner"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -12937772(0xffffffffff3a95d4, float:-2.4801436E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_recordVoiceCancel"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 1694498815(0x64ffffff, float:3.777893E22)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "key_chat_recordedVoiceHighlight"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10309397(0xfffffffffvar_b0eb, float:-3.0132414E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_messagePanelSend"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -5987164(0xffffffffffa4a4a4, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "key_chat_messagePanelVoiceLock"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "key_chat_messagePanelVoiceLockBackground"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "key_chat_messagePanelVoiceLockShadow"
            r4.put(r8, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7432805(0xffffffffff8e959b, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_recordTime"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11688214(0xffffffffff4da6ea, float:-2.733584E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_emojiPanelNewTrending"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_gifSaveHintText"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -871296751(0xffffffffcCLASSNAME, float:-3.8028356E7)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_gifSaveHintBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_goDownButton"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_goDownButtonShadow"
            r4.put(r8, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7432805(0xffffffffff8e959b, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_goDownButtonIcon"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_goDownButtonCounter"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11689240(0xffffffffff4da2e8, float:-2.733376E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_goDownButtonCounterBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -5395027(0xffffffffffadadad, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_messagePanelCancelInlineBot"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_messagePanelVoicePressed"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10639650(0xffffffffff5da6de, float:-2.9462581E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_messagePanelVoiceBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -9211021(0xfffffffffvar_, float:-3.2360185E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_messagePanelVoiceDelete"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_messagePanelVoiceDuration"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11037236(0xfffffffffvar_cc, float:-2.865618E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inlineResultIcon"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_topPanelBackground"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7629157(0xffffffffff8b969b, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_topPanelClose"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -9658414(0xffffffffff6c9fd2, float:-3.1452764E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_topPanelLine"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_topPanelTitle"
            r4.put(r8, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7893359(0xfffffffffvar_e91, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_topPanelMessage"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -3188393(0xffffffffffcvar_, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_reportSpam"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11894091(0xffffffffff4a82b5, float:-2.6918272E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_addContact"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -9259544(0xfffffffffvar_b5e8, float:-3.2261769E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inLoader"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10114080(0xfffffffffvar_abe0, float:-3.0528564E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inLoaderSelected"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -8863118(0xfffffffffvar_CLASSNAME, float:-3.3065816E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outLoader"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -9783964(0xffffffffff6ab564, float:-3.1198118E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outLoaderSelected"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -6113080(0xffffffffffa2b8c8, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inLoaderPhoto"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -6113849(0xffffffffffa2b5c7, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inLoaderPhotoSelected"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -197380(0xfffffffffffcfcfc, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inLoaderPhotoIcon"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -1314571(0xffffffffffebf0f5, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_inLoaderPhotoIconSelected"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -8011912(0xfffffffffvar_bvar_, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outLoaderPhoto"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -8538000(0xffffffffff7db870, float:-3.3725234E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outLoaderPhotoSelected"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -2427453(0xffffffffffdaf5c3, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outLoaderPhotoIcon"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -4134748(0xffffffffffc0e8a4, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_outLoaderPhotoIconSelected"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 1711276032(0x66000000, float:1.5111573E23)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_mediaLoaderPhoto"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 2130706432(0x7var_, float:1.7014118E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_mediaLoaderPhotoSelected"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_mediaLoaderPhotoIcon"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -2500135(0xffffffffffd9d9d9, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_mediaLoaderPhotoIconSelected"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -868326258(0xffffffffcc3e648e, float:-4.9910328E7)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "chat_secretTimerBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_secretTimerText"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -12937771(0xffffffffff3a95d5, float:-2.4801438E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "profile_creatorIcon"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -8288630(0xfffffffffvar_a, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "profile_actionIcon"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "profile_actionBackground"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -855310(0xfffffffffff2f2f2, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "profile_actionPressedBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -5056776(0xffffffffffb2d6f8, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "profile_verifiedBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11959368(0xfffffffffvar_b8, float:-2.6785875E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "profile_verifiedCheck"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "profile_title"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -2626822(0xffffffffffd7eafa, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "profile_status"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7893872(0xfffffffffvar_CLASSNAME, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "profile_tabText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -12937771(0xffffffffff3a95d5, float:-2.4801438E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "profile_tabSelectedText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11557143(0xffffffffff4fa6e9, float:-2.7601684E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "profile_tabSelectedLine"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 251658240(0xvar_, float:6.3108872E-30)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "profile_tabSelector"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "player_actionBar"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 251658240(0xvar_, float:6.3108872E-30)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "player_actionBarSelector"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "player_actionBarTitle"
            r4.put(r8, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -1728053248(0xfffffffvar_, float:-6.617445E-24)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "player_actionBarTop"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7697782(0xffffffffff8a8a8a, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "player_actionBarSubtitle"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7697782(0xffffffffff8a8a8a, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "player_actionBarItems"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "player_background"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7564650(0xffffffffff8CLASSNAME, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "player_time"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -1315344(0xffffffffffebedf0, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "player_progressBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -3353637(0xffffffffffccd3db, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "player_progressBackground2"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -3810064(0xffffffffffc5dcf0, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "key_player_progressCachedBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11228437(0xfffffffffvar_aaeb, float:-2.826838E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "player_progress"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -13421773(0xfffffffffvar_, float:-2.3819765E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "player_button"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11753238(0xffffffffff4ca8ea, float:-2.7203956E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "player_buttonActive"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -1973016(0xffffffffffe1e4e8, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "key_sheet_scrollUp"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -3551789(0xffffffffffc9cdd3, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "key_sheet_other"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "files_folderIcon"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10637333(0xffffffffff5dafeb, float:-2.946728E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "files_folderIconBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "files_iconText"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -6908266(0xfffffffffvar_, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "sessions_devicesImage"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -12211217(0xfffffffffvar_abef, float:-2.6275065E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "passport_authorizeBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -12542501(0xfffffffffvar_ddb, float:-2.560314E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "passport_authorizeBackgroundSelected"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "passport_authorizeText"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -12149258(0xfffffffffvar_df6, float:-2.6400732E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "location_sendLocationBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "location_sendLocationIcon"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -14906664(0xffffffffff1c8ad8, float:-2.0808049E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "location_sendLocationText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11550140(0xffffffffff4fCLASSNAME, float:-2.7615888E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "location_sendLiveLocationBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "location_sendLiveLocationIcon"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -13194460(0xfffffffffvar_ab24, float:-2.428081E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "location_sendLiveLocationText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -13262875(0xfffffffffvar_fe5, float:-2.4142049E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "location_liveLocationProgress"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11753238(0xffffffffff4ca8ea, float:-2.7203956E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "location_placeLocationBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -12959675(0xffffffffff3a4045, float:-2.4757011E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "location_actionIcon"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -12414746(0xfffffffffvar_e6, float:-2.5862259E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "location_actionActiveIcon"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "location_actionBackground"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -855310(0xfffffffffff2f2f2, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "location_actionPressedBackground"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -13262875(0xfffffffffvar_fe5, float:-2.4142049E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "dialog_liveLocationProgress"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -16725933(0xfffffffffvar_CLASSNAME, float:-1.7118133E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "calls_callReceivedGreenIcon"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -47032(0xfffffffffffvar_, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "calls_callReceivedRedIcon"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11491093(0xfffffffffvar_a8eb, float:-2.773565E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "featuredStickers_addedIcon"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "featuredStickers_buttonProgress"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11491093(0xfffffffffvar_a8eb, float:-2.773565E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "featuredStickers_addButton"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -12346402(0xfffffffffvar_bde, float:-2.6000877E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "featuredStickers_addButtonPressed"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11496493(0xfffffffffvar_d3, float:-2.7724697E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "featuredStickers_removeButtonText"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "featuredStickers_buttonText"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11688214(0xffffffffff4da6ea, float:-2.733584E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r15 = "featuredStickers_unread"
            r4.put(r15, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "inappPlayerPerformer"
            r4.put(r8, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "inappPlayerTitle"
            r4.put(r8, r9)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "inappPlayerBackground"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10309397(0xfffffffffvar_b0eb, float:-3.0132414E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "inappPlayerPlayPause"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7629157(0xffffffffff8b969b, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "inappPlayerClose"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -12279325(0xfffffffffvar_a1e3, float:-2.6136925E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "returnToCallBackground"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -6445135(0xffffffffff9da7b1, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "returnToCallMutedBackground"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "returnToCallText"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -13196562(0xfffffffffvar_a2ee, float:-2.4276547E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "sharedMedia_startStopLoadIcon"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -986123(0xfffffffffff0f3f5, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "sharedMedia_linkPlaceholder"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -4735293(0xffffffffffb7bec3, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "sharedMedia_linkPlaceholderText"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -1182729(0xffffffffffedf3f7, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "sharedMedia_photoPlaceholder"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -12154957(0xfffffffffvar_b3, float:-2.6389173E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "sharedMedia_actionMode"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10567099(0xffffffffff5eCLASSNAME, float:-2.9609732E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "checkbox"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "checkboxCheck"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -5195326(0xffffffffffb0b9c2, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "checkboxDisabled"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -4801083(0xffffffffffb6bdc5, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "stickers_menu"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 251658240(0xvar_, float:6.3108872E-30)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "stickers_menuSelector"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -4669499(0xffffffffffb8bfc5, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "changephoneinfo_image"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11491350(0xfffffffffvar_a7ea, float:-2.7735128E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "changephoneinfo_image2"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "groupcreate_hintText"
            r4.put(r8, r12)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11361317(0xfffffffffvar_a3db, float:-2.7998867E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "groupcreate_cursor"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "groupcreate_sectionShadow"
            r4.put(r8, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -8617336(0xffffffffff7CLASSNAME, float:-3.3564321E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "groupcreate_sectionText"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "groupcreate_spanText"
            r4.put(r8, r11)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -855310(0xfffffffffff2f2f2, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "groupcreate_spanBackground"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "groupcreate_spanDelete"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11157919(0xfffffffffvar_be61, float:-2.8411407E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "contacts_inviteBackground"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "contacts_inviteText"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -1971470(0xffffffffffe1eaf2, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "login_progressInner"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10313520(0xfffffffffvar_a0d0, float:-3.0124051E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "login_progressOuter"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -14043401(0xfffffffffvar_b6f7, float:-2.2558954E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "musicPicker_checkbox"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "musicPicker_checkboxCheck"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10702870(0xffffffffff5cafea, float:-2.9334356E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "musicPicker_buttonBackground"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "musicPicker_buttonIcon"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -15095832(0xfffffffffvar_a7e8, float:-2.042437E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "picker_enabledButton"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -6710887(0xfffffffffvar_, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "picker_disabledButton"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -14043401(0xfffffffffvar_b6f7, float:-2.2558954E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "picker_badge"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "picker_badgeText"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -12348980(0xfffffffffvar_cc, float:-2.5995648E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "chat_botSwitchToInlineText"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -366530760(0xffffffffea272var_, float:-5.05284E25)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "undo_background"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -8008961(0xfffffffffvar_caff, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "undo_cancelColor"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "undo_infoColor"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "wallet_blackBackground"
            r4.put(r8, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -986896(0xfffffffffff0f0f0, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "wallet_graySettingsBackground"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -14079703(0xfffffffffvar_, float:-2.2485325E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "wallet_grayBackground"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "wallet_whiteBackground"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 1090519039(0x40ffffff, float:7.9999995)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "wallet_blackBackgroundSelector"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "wallet_whiteText"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "wallet_blackText"
            r4.put(r8, r11)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -8355712(0xfffffffffvar_, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "wallet_statusText"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -8947849(0xfffffffffvar_, float:-3.2893961E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "wallet_grayText"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10066330(0xfffffffffvar_, float:-3.0625412E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "wallet_grayText2"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -13129704(0xfffffffffvar_a818, float:-2.4412152E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "wallet_greenText"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -2408384(0xffffffffffdb4040, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "wallet_redText"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -6710887(0xfffffffffvar_, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "wallet_dateText"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -6710887(0xfffffffffvar_, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "wallet_commentText"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -13599557(0xfffffffffvar_cbb, float:-2.3459176E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "wallet_releaseBackground"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -14606047(0xfffffffffvar_, float:-2.1417772E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "wallet_pullBackground"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -12082714(0xfffffffffvar_a1e6, float:-2.65357E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "wallet_buttonBackground"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -13923114(0xffffffffff2b8cd6, float:-2.2802925E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "wallet_buttonPressedBackground"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "wallet_buttonText"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 218103808(0xd000000, float:3.9443045E-31)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "wallet_addressConfirmBackground"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 775919907(0x2e3var_, float:4.3564385E-11)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "chat_outTextSelectionHighlight"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 1348643299(0x5062a9e3, float:1.5211138E10)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "chat_inTextSelectionHighlight"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -12476440(0xfffffffffvar_fe8, float:-2.5737128E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "chat_TextSelectionCursor"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -12476440(0xfffffffffvar_fe8, float:-2.5737128E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "chat_outTextSelectionCursor"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_BlurAlpha"
            r4.put(r8, r10)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 2133140777(0x7var_, float:2.1951557E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "statisticChartSignature"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 2133140777(0x7var_, float:2.1951557E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "statisticChartSignatureAlpha"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 437792059(0x1a182d3b, float:3.14694E-23)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "statisticChartHintLine"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 855638016(0x33000000, float:2.9802322E-8)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "statisticChartActiveLine"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -1713180935(0xfffffffvar_e2eef9, float:-2.3464373E-23)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "statisticChartInactivePickerChart"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -658846503(0xffffffffd8baccd9, float:-1.64311181E15)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "statisticChartActivePickerChart"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 746495415(0x2c7e9db7, float:3.618312E-12)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "statisticChartRipple"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -15692829(0xfffffffffvar_be3, float:-1.9213516E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "statisticChartBackZoomColor"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -4342339(0xffffffffffbdbdbd, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "statisticChartCheckboxInactive"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7434605(0xffffffffff8e8e93, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "statisticChartNightIconColor"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -2959913(0xffffffffffd2d5d7, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "statisticChartChevronColor"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 552398060(0x20ececec, float:4.0136737E-19)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "statisticChartHighlightColor"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "statisticChartPopupBackground"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -13467675(0xfffffffffvar_fe5, float:-2.3726665E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "statisticChartLine_blue"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10369198(0xfffffffffvar_CLASSNAME, float:-3.0011123E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "statisticChartLine_green"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -2075818(0xffffffffffe05356, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "statisticChartLine_red"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -2180600(0xffffffffffdeba08, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "statisticChartLine_golden"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10966803(0xfffffffffvar_a8ed, float:-2.8799036E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "statisticChartLine_lightblue"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7352519(0xffffffffff8fcvar_, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "statisticChartLine_lightgreen"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -1853657(0xffffffffffe3b727, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "statisticChartLine_orange"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -8422925(0xffffffffff7var_f3, float:-3.3958634E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "statisticChartLine_indigo"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -1118482(0xffffffffffeeeeee, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "statisticChartLineEmpty"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -12292204(0xfffffffffvar_var_, float:-2.6110803E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "actionBarTipBackground"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -9718023(0xffffffffff6bb6f9, float:-3.1331863E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_checkMenu"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -8919716(0xfffffffffvar_e55c, float:-3.2951022E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_muteButton"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -8528726(0xffffffffff7ddcaa, float:-3.3744044E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_muteButton2"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11089922(0xfffffffffvar_c7fe, float:-2.854932E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_muteButton3"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "voipgroup_searchText"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -8024684(0xfffffffffvar_d94, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_searchPlaceholder"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -13616313(0xfffffffffvar_b47, float:-2.3425191E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_searchBackground"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -35467(0xfffffffffffvar_, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_leaveCallMenu"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -13023660(0xfffffffffvar_, float:-2.4627234E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_scrollUp"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 2100052301(0x7d2CLASSNAMEd, float:1.4310392E37)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_soundButton"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 2099422443(0x7d22a4eb, float:1.3511952E37)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_soundButtonActive"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -2110540545(0xfffffffvar_b4ff, float:-1.3202786E-37)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_soundButtonActiveScrolled"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 2099796282(0x7d28593a, float:1.398585E37)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_soundButton2"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 2098771793(0x7d18b751, float:1.2687156E37)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_soundButtonActive2"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -2111520954(0xfffffffvar_bvar_, float:-1.210371E-37)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_soundButtonActive2Scrolled"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 2113363036(0x7dvar_c5c, float:4.109986E37)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_leaveButton"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -2100212396(0xfffffffvar_d14d54, float:-3.0754174E-37)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_leaveButtonScrolled"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -14107905(0xfffffffffvar_baff, float:-2.2428124E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_connectingProgress"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -14933463(0xffffffffff1CLASSNAME, float:-2.0753694E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_disabledButton"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -13878715(0xffffffffff2c3a45, float:-2.2892977E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_disabledButtonActive"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -2106088964(0xfffffffvar_a1fc, float:-1.8193181E-37)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_disabledButtonActiveScrolled"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11297032(0xfffffffffvar_ef8, float:-2.8129252E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_unmuteButton"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10038021(0xfffffffffvar_d4fb, float:-3.068283E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_unmuteButton2"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -15130842(0xfffffffffvar_var_, float:-2.0353362E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_actionBarUnscrolled"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -14538189(0xfffffffffvar_a33, float:-2.1555405E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_listViewBackgroundUnscrolled"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -8024684(0xfffffffffvar_d94, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_lastSeenTextUnscrolled"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -8485236(0xffffffffff7e868c, float:-3.3832252E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_mutedIconUnscrolled"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -15789289(0xffffffffff0var_, float:-1.9017872E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_actionBar"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -15065823(0xffffffffff1a1d21, float:-2.0485236E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_emptyView"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "voipgroup_actionBarItems"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -7697782(0xffffffffff8a8a8a, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_actionBarSubtitle"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 515562495(0x1ebadbff, float:1.9784504E-20)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_actionBarItemsSelector"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -36752(0xfffffffffffvar_, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_mutedByAdminIcon"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -9471616(0xffffffffff6var_, float:-3.1831636E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_mutedIcon"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -8813686(0xfffffffffvar_a, float:-3.3166076E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_lastSeenText"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "voipgroup_nameText"
            r4.put(r8, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -14933463(0xffffffffff1CLASSNAME, float:-2.0753694E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_listViewBackground"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -14933463(0xffffffffff1CLASSNAME, float:-2.0753694E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_dialogBackground"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11683585(0xffffffffff4db8ff, float:-2.734523E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_listeningText"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -8917379(0xfffffffffvar_ee7d, float:-3.2955762E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_speakingText"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 251658239(0xeffffff, float:6.310887E-30)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_listSelector"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -14538189(0xfffffffffvar_a33, float:-2.1555405E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_inviteMembersBackground"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -13906177(0xffffffffff2bceff, float:-2.2837277E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_overlayBlue1"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -16156957(0xfffffffffvar_e3, float:-1.8272153E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_overlayBlue2"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -15551198(0xfffffffffvar_b522, float:-1.9500778E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_overlayGreen1"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -16722239(0xfffffffffvar_d6c1, float:-1.7125625E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_overlayGreen2"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -10434565(0xfffffffffvar_c7fb, float:-2.9878543E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_topPanelBlue1"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11427847(0xfffffffffvar_ff9, float:-2.7863928E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_topPanelBlue2"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11350435(0xfffffffffvar_ce5d, float:-2.8020938E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_topPanelGreen1"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -16731712(0xfffffffffvar_b1c0, float:-1.7106411E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_topPanelGreen2"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -8021590(0xfffffffffvar_aa, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_topPanelGray"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -14455406(0xfffffffffvar_d92, float:-2.1723308E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_overlayAlertGradientMuted"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -13873813(0xffffffffff2c4d6b, float:-2.290292E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_overlayAlertGradientMuted2"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -15955316(0xffffffffff0c8a8c, float:-1.868113E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_overlayAlertGradientUnmuted"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -14136203(0xfffffffffvar_CLASSNAME, float:-2.237073E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_overlayAlertGradientUnmuted2"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -11033346(0xfffffffffvar_a4fe, float:-2.866407E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_mutedByAdminGradient"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -1026983(0xffffffffffvar_, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_mutedByAdminGradient2"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -9015575(0xfffffffffvar_ee9, float:-3.2756597E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_mutedByAdminGradient3"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -9998178(0xfffffffffvar_e, float:-3.076364E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_overlayAlertMutedByAdmin"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -13676424(0xffffffffff2var_, float:-2.3303272E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "kvoipgroup_overlayAlertMutedByAdmin2"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 2138612735(0x7var_a3ff, float:3.3050006E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_mutedByAdminMuteButton"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = 863544319(0x3378a3ff, float:5.7891153E-8)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_mutedByAdminMuteButtonDisabled"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -2368549(0xffffffffffdbdbdb, float:NaN)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_windowBackgroundWhiteInputField"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -13129232(0xfffffffffvar_a9f0, float:-2.4413109E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "voipgroup_windowBackgroundWhiteInputFieldActivated"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -8863118(0xfffffffffvar_CLASSNAME, float:-3.3065816E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "chat_outReactionButtonBackground"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r8 = -9259544(0xfffffffffvar_b5e8, float:-3.2261769E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            java.lang.String r9 = "chat_inReactionButtonBackground"
            r4.put(r9, r8)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_inReactionButtonText"
            r4.put(r8, r14)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r8 = "chat_outReactionButtonText"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_inReactionButtonTextSelected"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "chat_outReactionButtonTextSelected"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11875005(0xffffffffff4acd43, float:-2.6956983E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "premiumGradient0"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11164161(0xfffffffffvar_a5ff, float:-2.8398746E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "premiumGradient1"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -5806081(0xffffffffffa767ff, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "premiumGradient2"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -2401123(0xffffffffffdb5c9d, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "premiumGradient3"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -816858(0xffffffffffvar_, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "premiumGradient4"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -11164161(0xfffffffffvar_a5ff, float:-2.8398746E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "premiumGradientBackground1"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -5806081(0xffffffffffa767ff, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "premiumGradientBackground2"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -2401123(0xffffffffffdb5c9d, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "premiumGradientBackground3"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            r6 = -816858(0xffffffffffvar_, float:NaN)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r8 = "premiumGradientBackground4"
            r4.put(r8, r6)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "premiumGradientBackgroundOverlay"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = defaultColors
            java.lang.String r6 = "premiumStarGradient1"
            r4.put(r6, r0)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -1839878(0xffffffffffe3ecfa, float:NaN)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "premiumStarGradient2"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = 90
            int r4 = androidx.core.graphics.ColorUtils.setAlphaComponent(r2, r4)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "premiumStartSmallStarsColor"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = 90
            int r4 = androidx.core.graphics.ColorUtils.setAlphaComponent(r2, r4)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "premiumStartSmallStarsColor2"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -10773017(0xffffffffff5b9de7, float:-2.919208E38)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "premiumGradientBottomSheet1"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -5535779(0xffffffffffab87dd, float:NaN)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "premiumGradientBottomSheet2"
            r0.put(r6, r4)
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = defaultColors
            r4 = -1600322(0xffffffffffe794be, float:NaN)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r6 = "premiumGradientBottomSheet3"
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
            r0.put(r4, r13)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "windowBackgroundChecked"
            r0.put(r4, r13)
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
            r0.put(r4, r13)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "switchTrackBlueThumbChecked"
            r0.put(r4, r13)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "windowBackgroundCheckText"
            r0.put(r4, r13)
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
            java.lang.String r4 = "chat_emojiPanelIconSelector"
            java.lang.String r6 = "listSelectorSDK21"
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
            java.lang.String r4 = "chat_outLinkSelectBackground"
            java.lang.String r6 = "chat_linkSelectBackground"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "actionBarDefaultSubmenuSeparator"
            java.lang.String r6 = "windowBackgroundGray"
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
            java.lang.String r4 = "chat_outTextSelectionCursor"
            java.lang.String r6 = "chat_TextSelectionCursor"
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
            r0.put(r4, r13)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialogSwipeRemove"
            java.lang.String r6 = "avatar_backgroundRed"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_inReactionButtonBackground"
            java.lang.String r6 = "chat_inLoader"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_outReactionButtonBackground"
            java.lang.String r6 = "chat_outLoader"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_inReactionButtonText"
            java.lang.String r6 = "chat_inPreviewInstantText"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_outReactionButtonText"
            java.lang.String r6 = "chat_outPreviewInstantText"
            r0.put(r4, r6)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_inReactionButtonTextSelected"
            r0.put(r4, r13)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "chat_outReactionButtonTextSelected"
            r0.put(r4, r13)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = fallbackKeys
            java.lang.String r4 = "dialogReactionMentionBackground"
            java.lang.String r6 = "voipgroup_mutedByAdminGradient2"
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
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_windowBackgroundWhiteInputField"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "voipgroup_windowBackgroundWhiteInputFieldActivated"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "premiumGradient1"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "premiumGradient2"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "premiumGradient3"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = themeAccentExclusionKeys
            java.lang.String r4 = "premiumGradient4"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesBubblesColorKeys
            java.lang.String r4 = "chat_outBubble"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesBubblesColorKeys
            java.lang.String r4 = "chat_outBubbleSelected"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesBubblesColorKeys
            java.lang.String r4 = "chat_outBubbleShadow"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesBubblesColorKeys
            java.lang.String r4 = "chat_outBubbleGradient"
            r0.add(r4)
            java.util.HashSet<java.lang.String> r0 = myMessagesColorKeys
            java.lang.String r4 = "chat_outUpCall"
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
            r11 = {0, -2104672, -937328, -2637335, -2639714, -1270157, -3428124, -6570777, -7223828, -6567550, -1793599, -1855875, -4674838, -1336199, -2900876, -6247730} // fill-array
            int[] r12 = new int[r6]
            r12 = {0, -4532067, -1257580, -1524266, -1646910, -1519483, -1324823, -4138509, -4202516, -2040429, -1458474, -1256030, -3814930, -1000039, -1450082, -3485987} // fill-array
            int[] r13 = new int[r6]
            r13 = {0, -1909081, -1592444, -2969879, -2439762, -1137033, -2119471, -6962197, -4857383, -4270699, -3364639, -2117514, -5000734, -1598028, -2045813, -5853742} // fill-array
            int[] r14 = new int[r6]
            r14 = {0, -6371440, -1319256, -1258616, -1712961, -1186647, -1193816, -4467224, -4203544, -3023977, -1061929, -1255788, -2113811, -806526, -1715305, -3485976} // fill-array
            int[] r15 = new int[r6]
            r15 = {99, 9, 10, 11, 12, 13, 14, 0, 1, 2, 3, 4, 5, 6, 7, 8} // fill-array
            java.lang.String[] r2 = new java.lang.String[r6]
            r2[r1] = r3
            java.lang.String r21 = "p-pXcflrmFIBAAAAvXYQk-mCwZU"
            r2[r5] = r21
            java.lang.String r21 = "JqSUrO0-mFIBAAAAWwTvLzoWGQI"
            r2[r7] = r21
            java.lang.String r21 = "O-wmAfBPSFADAAAA4zINVfD_bro"
            r20 = 3
            r2[r20] = r21
            java.lang.String r21 = "RepJ5uE_SVABAAAAr4d0YhgB850"
            r19 = 4
            r2[r19] = r21
            java.lang.String r21 = "-Xc-np9y2VMCAAAARKr0yNNPYW0"
            r18 = 5
            r2[r18] = r21
            java.lang.String r21 = "fqv01SQemVIBAAAApND8LDRUhRU"
            r17 = 6
            r2[r17] = r21
            java.lang.String r21 = "fqv01SQemVIBAAAApND8LDRUhRU"
            r16 = 7
            r2[r16] = r21
            r21 = 8
            java.lang.String r22 = "RepJ5uE_SVABAAAAr4d0YhgB850"
            r2[r21] = r22
            java.lang.String r21 = "lp0prF8ISFAEAAAA_p385_CvG0w"
            r7 = 9
            r2[r7] = r21
            r21 = 10
            java.lang.String r22 = "heptcj-hSVACAAAAC9RrMzOa-cs"
            r2[r21] = r22
            r21 = 11
            java.lang.String r22 = "PllZ-bf_SFAEAAAA8crRfwZiDNg"
            r2[r21] = r22
            r21 = 12
            java.lang.String r22 = "dhf9pceaQVACAAAAbzdVo4SCiZA"
            r2[r21] = r22
            r21 = 13
            java.lang.String r22 = "Ujx2TFcJSVACAAAARJ4vLa50MkM"
            r2[r21] = r22
            java.lang.String r21 = "p-pXcflrmFIBAAAAvXYQk-mCwZU"
            r7 = 14
            r2[r7] = r21
            java.lang.String r21 = "dk_wwlghOFACAAAAfz9xrxi6euw"
            r7 = 15
            r2[r7] = r21
            int[] r7 = new int[r6]
            r7 = {0, 180, 45, 0, 45, 180, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r5 = new int[r6]
            r5 = {0, 52, 46, 57, 45, 64, 52, 35, 36, 41, 50, 50, 35, 38, 37, 30} // fill-array
            r21 = r0
            r22 = r8
            r23 = r9
            r24 = r10
            r25 = r11
            r26 = r12
            r27 = r13
            r28 = r14
            r29 = r15
            r30 = r2
            r31 = r7
            r32 = r5
            r21.setAccentColorOptions(r22, r23, r24, r25, r26, r27, r28, r29, r30, r31, r32)
            sortAccents(r0)
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r2 = themes
            defaultTheme = r0
            currentTheme = r0
            currentDayTheme = r0
            r2.add(r0)
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r2 = themesDict
            java.lang.String r5 = "Blue"
            r2.put(r5, r0)
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
            int[] r5 = new int[r2]
            r5 = {-7177260, -9860357, -14440464, -8687151, -9848491, -14053142, -9403671, -10044691, -13203974, -12138259, -10179489, -1344335, -1142742, -6127120, -2931932, -1131212, -8417365, -13270557} // fill-array
            int[] r7 = new int[r2]
            r7 = {-6464359, -10267323, -13532789, -5413850, -11898828, -13410942, -13215889, -10914461, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r8 = new int[r2]
            r8 = {-10465880, -9937588, -14983040, -6736562, -14197445, -13534568, -13144441, -10587280, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r9 = new int[r2]
            r9 = {-14213586, -15263198, -16310753, -15724781, -15853551, -16051428, -14868183, -14668758, -15854566, -15326427, -15327979, -14411490, -14345453, -14738135, -14543346, -14212843, -15263205, -15854566} // fill-array
            int[] r10 = new int[r2]
            r10 = {-15659501, -14277074, -15459034, -14542297, -14735336, -15129808, -15591910, -15459810, -15260623, -15853800, -15259879, -14477540, -14674936, -15461604, -13820650, -15067635, -14605528, -15260623} // fill-array
            int[] r11 = new int[r2]
            r11 = {-13951445, -15395557, -15985382, -15855853, -16050417, -15525854, -15260627, -15327189, -15788258, -14799314, -15458796, -13952727, -13754603, -14081231, -14478324, -14081004, -15197667, -15788258} // fill-array
            int[] r12 = new int[r2]
            r12 = {-15330777, -15066858, -15915220, -14213847, -15262439, -15260879, -15657695, -16443625, -15459285, -15589601, -14932454, -14740451, -15002870, -15264997, -13821660, -14805234, -14605784, -15459285} // fill-array
            int[] r13 = new int[r2]
            r13 = {11, 12, 13, 14, 15, 16, 17, 18, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9} // fill-array
            java.lang.String[] r14 = new java.lang.String[r2]
            java.lang.String r15 = "O-wmAfBPSFADAAAA4zINVfD_bro"
            r14[r1] = r15
            java.lang.String r15 = "RepJ5uE_SVABAAAAr4d0YhgB850"
            r21 = 1
            r14[r21] = r15
            java.lang.String r15 = "dk_wwlghOFACAAAAfz9xrxi6euw"
            r21 = 2
            r14[r21] = r15
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
            java.lang.String r21 = "fqv01SQemVIBAAAApND8LDRUhRU"
            r14[r15] = r21
            java.lang.String r15 = "RepJ5uE_SVABAAAAr4d0YhgB850"
            r21 = 9
            r14[r21] = r15
            r15 = 10
            java.lang.String r21 = "CJNyxPMgSVAEAAAAvW9sMwCLASSNAMEcw"
            r14[r15] = r21
            r15 = 11
            java.lang.String r21 = "9LW_RcoOSVACAAAAFTk3DTyXN-M"
            r14[r15] = r21
            r15 = 12
            java.lang.String r21 = "9GcNVISdSVADAAAAUcw5BYjELW4"
            r14[r15] = r21
            r15 = 13
            java.lang.String r21 = "F5oWoCs7QFACAAAAgf2bD_mg8Bw"
            r14[r15] = r21
            java.lang.String r15 = "9Shvar_d1MFIIAAAAjWnm8_ZMe8Q"
            r21 = 14
            r14[r21] = r15
            java.lang.String r15 = "3rX-PaKbSFACAAAAEiHNvcEm6X4"
            r21 = 15
            r14[r21] = r15
            java.lang.String r15 = "dk_wwlghOFACAAAAfz9xrxi6euw"
            r14[r6] = r15
            r6 = 17
            java.lang.String r15 = "fqv01SQemVIBAAAApND8LDRUhRU"
            r14[r6] = r15
            int[] r6 = new int[r2]
            r6 = {225, 45, 225, 135, 45, 225, 45, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r2 = new int[r2]
            r2 = {40, 40, 31, 50, 25, 34, 35, 35, 38, 29, 24, 34, 34, 31, 29, 37, 21, 38} // fill-array
            r21 = r0
            r22 = r5
            r23 = r7
            r24 = r8
            r25 = r9
            r26 = r10
            r27 = r11
            r28 = r12
            r29 = r13
            r30 = r14
            r31 = r6
            r32 = r2
            r21.setAccentColorOptions(r22, r23, r24, r25, r26, r27, r28, r29, r30, r31, r32)
            sortAccents(r0)
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r2 = themes
            r2.add(r0)
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r2 = themesDict
            currentNightTheme = r0
            java.lang.String r5 = "Dark Blue"
            r2.put(r5, r0)
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
            int[] r5 = new int[r2]
            r5 = {-12537374, -12472227, -3240928, -11033621, -2194124, -3382903, -13332245, -12342073, -11359164, -3317869, -2981834, -8165684, -3256745, -2904512, -8681301} // fill-array
            int[] r6 = new int[r2]
            r6 = {-13525046, -14113959, -7579073, -13597229, -3581840, -8883763, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r7 = new int[r2]
            r7 = {-11616542, -9716647, -6400452, -12008744, -2592697, -4297041, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r8 = new int[r2]
            r8 = {-3808528, -2433367, -2700891, -1838093, -1120848, -1712148, -2037779, -4202261, -4005713, -1058332, -925763, -1975316, -1189672, -1318451, -2302235} // fill-array
            int[] r9 = new int[r2]
            r9 = {-1510157, -4398164, -1647697, -3610898, -1130838, -1980692, -4270093, -4202261, -3415654, -1259815, -1521765, -4341268, -1127744, -1318219, -3945761} // fill-array
            int[] r10 = new int[r2]
            r10 = {-4924688, -3283031, -1523567, -2494477, -1126510, -595210, -2037517, -3478548, -4661623, -927514, -796762, -2696971, -1188403, -1319735, -1577487} // fill-array
            int[] r11 = new int[r2]
            r11 = {-3149585, -5714021, -1978209, -4925720, -1134713, -1718833, -3613709, -5317397, -3218014, -999207, -2116466, -4343054, -931397, -1583186, -3815718} // fill-array
            int[] r12 = new int[r2]
            r12 = {9, 10, 11, 12, 13, 14, 0, 1, 2, 3, 4, 5, 6, 7, 8} // fill-array
            java.lang.String[] r13 = new java.lang.String[r2]
            java.lang.String r2 = "MIo6r0qGSFAFAAAAtL8TsDzNX60"
            r13[r1] = r2
            java.lang.String r2 = "dhf9pceaQVACAAAAbzdVo4SCiZA"
            r14 = 1
            r13[r14] = r2
            java.lang.String r2 = "fqv01SQemVIBAAAApND8LDRUhRU"
            r14 = 2
            r13[r14] = r2
            java.lang.String r2 = "p-pXcflrmFIBAAAAvXYQk-mCwZU"
            r14 = 3
            r13[r14] = r2
            java.lang.String r2 = "JqSUrO0-mFIBAAAAWwTvLzoWGQI"
            r14 = 4
            r13[r14] = r2
            java.lang.String r2 = "F5oWoCs7QFACAAAAgf2bD_mg8Bw"
            r14 = 5
            r13[r14] = r2
            java.lang.String r2 = "fqv01SQemVIBAAAApND8LDRUhRU"
            r14 = 6
            r13[r14] = r2
            java.lang.String r2 = "RepJ5uE_SVABAAAAr4d0YhgB850"
            r14 = 7
            r13[r14] = r2
            r2 = 8
            java.lang.String r14 = "PllZ-bf_SFAEAAAA8crRfwZiDNg"
            r13[r2] = r14
            java.lang.String r2 = "pgJfpFNRSFABAAAACDT8s5sEjfc"
            r14 = 9
            r13[r14] = r2
            r2 = 10
            java.lang.String r14 = "ptuUd96JSFACAAAATobI23sPpz0"
            r13[r2] = r14
            r2 = 11
            java.lang.String r14 = "dhf9pceaQVACAAAAbzdVo4SCiZA"
            r13[r2] = r14
            r2 = 12
            java.lang.String r14 = "JqSUrO0-mFIBAAAAWwTvLzoWGQI"
            r13[r2] = r14
            r2 = 13
            java.lang.String r14 = "9iklpvIPQVABAAAAORQXKur_Eyc"
            r13[r2] = r14
            java.lang.String r2 = "F5oWoCs7QFACAAAAgf2bD_mg8Bw"
            r14 = 14
            r13[r14] = r2
            r2 = 15
            int[] r14 = new int[r2]
            r14 = {315, 315, 225, 315, 0, 180, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r2 = new int[r2]
            r2 = {50, 50, 58, 47, 46, 50, 49, 46, 51, 50, 49, 34, 54, 50, 40} // fill-array
            r21 = r0
            r22 = r5
            r23 = r6
            r24 = r7
            r25 = r8
            r26 = r9
            r27 = r10
            r28 = r11
            r29 = r12
            r30 = r13
            r31 = r14
            r32 = r2
            r21.setAccentColorOptions(r22, r23, r24, r25, r26, r27, r28, r29, r30, r31, r32)
            sortAccents(r0)
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r2 = themes
            r2.add(r0)
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r2 = themesDict
            java.lang.String r5 = "Arctic Blue"
            r2.put(r5, r0)
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
            r2 = 2
            r0.sortIndex = r2
            r2 = 14
            int[] r5 = new int[r2]
            r5 = {-11099447, -3379581, -3109305, -3382174, -7963438, -11759137, -11029287, -11226775, -2506945, -3382174, -3379581, -6587438, -2649788, -8681301} // fill-array
            int[] r6 = new int[r2]
            r6 = {-10125092, -9671214, -3451775, -3978678, -10711329, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r7 = new int[r2]
            r7 = {-12664362, -3642988, -2383569, -3109317, -11422261, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r8 = new int[r2]
            r8 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r9 = new int[r2]
            r9 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r10 = new int[r2]
            r10 = {9, 10, 11, 12, 13, 0, 1, 2, 3, 4, 5, 6, 7, 8} // fill-array
            java.lang.String[] r11 = new java.lang.String[r2]
            r11[r1] = r3
            r2 = 1
            r11[r2] = r3
            r2 = 2
            r11[r2] = r3
            r2 = 3
            r11[r2] = r3
            r2 = 4
            r11[r2] = r3
            r2 = 5
            r11[r2] = r3
            r2 = 6
            r11[r2] = r3
            r2 = 7
            r11[r2] = r3
            r2 = 8
            r11[r2] = r3
            r2 = 9
            r11[r2] = r3
            r2 = 10
            r11[r2] = r3
            r2 = 11
            r11[r2] = r3
            r2 = 12
            r11[r2] = r3
            r2 = 13
            r11[r2] = r3
            r2 = 14
            int[] r12 = new int[r2]
            r12 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r13 = new int[r2]
            r13 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            r27 = 0
            r28 = 0
            r21 = r0
            r22 = r5
            r23 = r6
            r24 = r7
            r25 = r8
            r26 = r9
            r29 = r10
            r30 = r11
            r31 = r12
            r32 = r13
            r21.setAccentColorOptions(r22, r23, r24, r25, r26, r27, r28, r29, r30, r31, r32)
            sortAccents(r0)
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r2 = themes
            r2.add(r0)
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r2 = themesDict
            java.lang.String r5 = "Day"
            r2.put(r5, r0)
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
            int[] r5 = new int[r2]
            r5 = {-9781697, -7505693, -2204034, -10913816, -2375398, -12678921, -11881005, -11880383, -2534026, -1934037, -7115558, -3128522, -1528292, -8812381} // fill-array
            int[] r6 = new int[r2]
            r6 = {-7712108, -4953061, -5288081, -14258547, -9154889, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r7 = new int[r2]
            r7 = {-9939525, -5948598, -10335844, -13659747, -14054507, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r8 = new int[r2]
            r8 = {-15330532, -14806760, -15791344, -16184308, -16313063, -15921641, -15656164, -15986420, -15856883, -14871025, -16185078, -14937584, -14869736, -15855598} // fill-array
            int[] r9 = new int[r2]
            r9 = {-14673881, -15724781, -15002342, -15458526, -15987697, -16184820, -16118258, -16250616, -15067624, -15527923, -14804447, -15790836, -15987960, -16316665} // fill-array
            int[] r10 = new int[r2]
            r10 = {-15856877, -14608861, -15528430, -15921391, -15722209, -15197144, -15458015, -15591406, -15528431, -15068401, -16053749, -15594229, -15395825, -15724012} // fill-array
            int[] r11 = new int[r2]
            r11 = {-14804694, -15658986, -14609382, -15656421, -16118509, -15855854, -16315381, -16052981, -14544354, -15791092, -15659241, -16316922, -15988214, -16185077} // fill-array
            int[] r12 = new int[r2]
            r12 = {9, 10, 11, 12, 13, 0, 1, 2, 3, 4, 5, 6, 7, 8} // fill-array
            java.lang.String[] r13 = new java.lang.String[r2]
            java.lang.String r2 = "YIxYGEALQVADAAAAA3QbEH0AowY"
            r13[r1] = r2
            java.lang.String r2 = "9LW_RcoOSVACAAAAFTk3DTyXN-M"
            r14 = 1
            r13[r14] = r2
            java.lang.String r2 = "O-wmAfBPSFADAAAA4zINVfD_bro"
            r14 = 2
            r13[r14] = r2
            java.lang.String r2 = "F5oWoCs7QFACAAAAgf2bD_mg8Bw"
            r14 = 3
            r13[r14] = r2
            java.lang.String r2 = "-Xc-np9y2VMCAAAARKr0yNNPYW0"
            r14 = 4
            r13[r14] = r2
            java.lang.String r2 = "fqv01SQemVIBAAAApND8LDRUhRU"
            r14 = 5
            r13[r14] = r2
            java.lang.String r2 = "F5oWoCs7QFACAAAAgf2bD_mg8Bw"
            r14 = 6
            r13[r14] = r2
            java.lang.String r2 = "ptuUd96JSFACAAAATobI23sPpz0"
            r14 = 7
            r13[r14] = r2
            r2 = 8
            java.lang.String r14 = "p-pXcflrmFIBAAAAvXYQk-mCwZU"
            r13[r2] = r14
            java.lang.String r2 = "Nl8Pg2rBQVACAAAA25Lxtb8SDp0"
            r14 = 9
            r13[r14] = r2
            r2 = 10
            java.lang.String r14 = "dhf9pceaQVACAAAAbzdVo4SCiZA"
            r13[r2] = r14
            r2 = 11
            java.lang.String r14 = "9GcNVISdSVADAAAAUcw5BYjELW4"
            r13[r2] = r14
            r2 = 12
            java.lang.String r14 = "9LW_RcoOSVACAAAAFTk3DTyXN-M"
            r13[r2] = r14
            r2 = 13
            java.lang.String r14 = "dk_wwlghOFACAAAAfz9xrxi6euw"
            r13[r2] = r14
            r2 = 14
            int[] r14 = new int[r2]
            r14 = {45, 135, 0, 180, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} // fill-array
            int[] r2 = new int[r2]
            r2 = {34, 47, 52, 48, 54, 50, 37, 56, 48, 49, 40, 64, 38, 48} // fill-array
            r21 = r0
            r22 = r5
            r23 = r6
            r24 = r7
            r25 = r8
            r26 = r9
            r27 = r10
            r28 = r11
            r29 = r12
            r30 = r13
            r31 = r14
            r32 = r2
            r21.setAccentColorOptions(r22, r23, r24, r25, r26, r27, r28, r29, r30, r31, r32)
            sortAccents(r0)
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r2 = themes
            r2.add(r0)
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r2 = themesDict
            java.lang.String r5 = "Night"
            r2.put(r5, r0)
            java.lang.String r0 = "themes2"
            r2 = 0
            java.lang.String r0 = r4.getString(r0, r2)
            java.lang.String r2 = "remote_version"
            int r2 = r4.getInt(r2, r1)
            r5 = 1
            if (r2 != r5) goto L_0x33e0
            r2 = 0
        L_0x3396:
            r5 = 4
            if (r2 >= r5) goto L_0x33e0
            long[] r5 = remoteThemesHash
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "2remoteThemesHash"
            r6.append(r7)
            if (r2 == 0) goto L_0x33ac
            java.lang.Integer r7 = java.lang.Integer.valueOf(r2)
            goto L_0x33ad
        L_0x33ac:
            r7 = r3
        L_0x33ad:
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r7 = 0
            long r6 = r4.getLong(r6, r7)
            r5[r2] = r6
            int[] r5 = lastLoadingThemesTime
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "lastLoadingThemesTime"
            r6.append(r7)
            if (r2 == 0) goto L_0x33cf
            java.lang.Integer r7 = java.lang.Integer.valueOf(r2)
            goto L_0x33d0
        L_0x33cf:
            r7 = r3
        L_0x33d0:
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            int r6 = r4.getInt(r6, r1)
            r5[r2] = r6
            int r2 = r2 + 1
            goto L_0x3396
        L_0x33e0:
            android.content.SharedPreferences$Editor r2 = r4.edit()
            java.lang.String r5 = "remote_version"
            r6 = 1
            android.content.SharedPreferences$Editor r2 = r2.putInt(r5, r6)
            r2.apply()
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 != 0) goto L_0x3428
            org.json.JSONArray r2 = new org.json.JSONArray     // Catch:{ Exception -> 0x3423 }
            r2.<init>(r0)     // Catch:{ Exception -> 0x3423 }
            r0 = 0
        L_0x33fa:
            int r5 = r2.length()     // Catch:{ Exception -> 0x3423 }
            if (r0 >= r5) goto L_0x346e
            org.json.JSONObject r5 = r2.getJSONObject(r0)     // Catch:{ Exception -> 0x3423 }
            org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = org.telegram.ui.ActionBar.Theme.ThemeInfo.createWithJson(r5)     // Catch:{ Exception -> 0x3423 }
            if (r5 == 0) goto L_0x3420
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r6 = otherThemes     // Catch:{ Exception -> 0x3423 }
            r6.add(r5)     // Catch:{ Exception -> 0x3423 }
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r6 = themes     // Catch:{ Exception -> 0x3423 }
            r6.add(r5)     // Catch:{ Exception -> 0x3423 }
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r6 = themesDict     // Catch:{ Exception -> 0x3423 }
            java.lang.String r7 = r5.getKey()     // Catch:{ Exception -> 0x3423 }
            r6.put(r7, r5)     // Catch:{ Exception -> 0x3423 }
            r5.loadWallpapers(r4)     // Catch:{ Exception -> 0x3423 }
        L_0x3420:
            int r0 = r0 + 1
            goto L_0x33fa
        L_0x3423:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x346e
        L_0x3428:
            r0 = 0
            java.lang.String r2 = "themes"
            java.lang.String r0 = r4.getString(r2, r0)
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 != 0) goto L_0x346e
            java.lang.String r2 = "&"
            java.lang.String[] r0 = r0.split(r2)
            r2 = 0
        L_0x343c:
            int r5 = r0.length
            if (r2 >= r5) goto L_0x345d
            r5 = r0[r2]
            org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = org.telegram.ui.ActionBar.Theme.ThemeInfo.createWithString(r5)
            if (r5 == 0) goto L_0x345a
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r6 = otherThemes
            r6.add(r5)
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r6 = themes
            r6.add(r5)
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r6 = themesDict
            java.lang.String r7 = r5.getKey()
            r6.put(r7, r5)
        L_0x345a:
            int r2 = r2 + 1
            goto L_0x343c
        L_0x345d:
            r2 = 1
            saveOtherThemes(r2, r2)
            android.content.SharedPreferences$Editor r0 = r4.edit()
            java.lang.String r2 = "themes"
            android.content.SharedPreferences$Editor r0 = r0.remove(r2)
            r0.commit()
        L_0x346e:
            sortThemes()
            r0 = 0
            android.content.SharedPreferences r2 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r5 = themesDict     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r6 = "Dark Blue"
            java.lang.Object r5 = r5.get(r6)     // Catch:{ Exception -> 0x39b4 }
            org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r5     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r6 = "theme"
            r7 = 0
            java.lang.String r6 = r2.getString(r6, r7)     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r7 = "Default"
            boolean r7 = r7.equals(r6)     // Catch:{ Exception -> 0x39b4 }
            if (r7 == 0) goto L_0x349e
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r0 = themesDict     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r6 = "Blue"
            java.lang.Object r0 = r0.get(r6)     // Catch:{ Exception -> 0x39b4 }
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r0     // Catch:{ Exception -> 0x39b4 }
            int r6 = DEFALT_THEME_ACCENT_ID     // Catch:{ Exception -> 0x39b4 }
            r0.currentAccentId = r6     // Catch:{ Exception -> 0x39b4 }
            goto L_0x34d0
        L_0x349e:
            java.lang.String r7 = "Dark"
            boolean r7 = r7.equals(r6)     // Catch:{ Exception -> 0x39b4 }
            if (r7 == 0) goto L_0x34ac
            r7 = 9
            r5.currentAccentId = r7     // Catch:{ Exception -> 0x39b4 }
            r0 = r5
            goto L_0x34d0
        L_0x34ac:
            if (r6 == 0) goto L_0x34d0
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r0 = themesDict     // Catch:{ Exception -> 0x39b4 }
            java.lang.Object r0 = r0.get(r6)     // Catch:{ Exception -> 0x39b4 }
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r0     // Catch:{ Exception -> 0x39b4 }
            if (r0 == 0) goto L_0x34d0
            java.lang.String r6 = "lastDayTheme"
            boolean r6 = r4.contains(r6)     // Catch:{ Exception -> 0x39b4 }
            if (r6 != 0) goto L_0x34d0
            android.content.SharedPreferences$Editor r6 = r4.edit()     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r7 = "lastDayTheme"
            java.lang.String r8 = r0.getKey()     // Catch:{ Exception -> 0x39b4 }
            r6.putString(r7, r8)     // Catch:{ Exception -> 0x39b4 }
            r6.commit()     // Catch:{ Exception -> 0x39b4 }
        L_0x34d0:
            java.lang.String r6 = "nighttheme"
            r7 = 0
            java.lang.String r6 = r2.getString(r6, r7)     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r7 = "Default"
            boolean r7 = r7.equals(r6)     // Catch:{ Exception -> 0x39b4 }
            if (r7 == 0) goto L_0x34ee
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r0 = themesDict     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r5 = "Blue"
            java.lang.Object r0 = r0.get(r5)     // Catch:{ Exception -> 0x39b4 }
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r0     // Catch:{ Exception -> 0x39b4 }
            int r5 = DEFALT_THEME_ACCENT_ID     // Catch:{ Exception -> 0x39b4 }
            r0.currentAccentId = r5     // Catch:{ Exception -> 0x39b4 }
            goto L_0x350b
        L_0x34ee:
            java.lang.String r7 = "Dark"
            boolean r7 = r7.equals(r6)     // Catch:{ Exception -> 0x39b4 }
            if (r7 == 0) goto L_0x34fd
            currentNightTheme = r5     // Catch:{ Exception -> 0x39b4 }
            r6 = 9
            r5.currentAccentId = r6     // Catch:{ Exception -> 0x39b4 }
            goto L_0x350b
        L_0x34fd:
            if (r6 == 0) goto L_0x350b
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r5 = themesDict     // Catch:{ Exception -> 0x39b4 }
            java.lang.Object r5 = r5.get(r6)     // Catch:{ Exception -> 0x39b4 }
            org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r5     // Catch:{ Exception -> 0x39b4 }
            if (r5 == 0) goto L_0x350b
            currentNightTheme = r5     // Catch:{ Exception -> 0x39b4 }
        L_0x350b:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = currentNightTheme     // Catch:{ Exception -> 0x39b4 }
            if (r5 == 0) goto L_0x3529
            java.lang.String r5 = "lastDarkTheme"
            boolean r5 = r4.contains(r5)     // Catch:{ Exception -> 0x39b4 }
            if (r5 != 0) goto L_0x3529
            android.content.SharedPreferences$Editor r5 = r4.edit()     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r6 = "lastDarkTheme"
            org.telegram.ui.ActionBar.Theme$ThemeInfo r7 = currentNightTheme     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r7 = r7.getKey()     // Catch:{ Exception -> 0x39b4 }
            r5.putString(r6, r7)     // Catch:{ Exception -> 0x39b4 }
            r5.commit()     // Catch:{ Exception -> 0x39b4 }
        L_0x3529:
            r5 = 0
            r6 = 0
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r7 = themesDict     // Catch:{ Exception -> 0x39b4 }
            java.util.Collection r7 = r7.values()     // Catch:{ Exception -> 0x39b4 }
            java.util.Iterator r7 = r7.iterator()     // Catch:{ Exception -> 0x39b4 }
        L_0x3535:
            boolean r8 = r7.hasNext()     // Catch:{ Exception -> 0x39b4 }
            if (r8 == 0) goto L_0x3805
            java.lang.Object r8 = r7.next()     // Catch:{ Exception -> 0x39b4 }
            org.telegram.ui.ActionBar.Theme$ThemeInfo r8 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r8     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r9 = r8.assetName     // Catch:{ Exception -> 0x39b4 }
            if (r9 == 0) goto L_0x37f8
            int r9 = r8.accentBaseColor     // Catch:{ Exception -> 0x39b4 }
            if (r9 == 0) goto L_0x37f8
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x39b4 }
            r9.<init>()     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r10 = "accents_"
            r9.append(r10)     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r10 = r8.assetName     // Catch:{ Exception -> 0x39b4 }
            r9.append(r10)     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x39b4 }
            r10 = 0
            java.lang.String r9 = r4.getString(r9, r10)     // Catch:{ Exception -> 0x39b4 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x39b4 }
            r10.<init>()     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r11 = "accent_current_"
            r10.append(r11)     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r11 = r8.assetName     // Catch:{ Exception -> 0x39b4 }
            r10.append(r11)     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x39b4 }
            boolean r11 = r8.firstAccentIsDefault     // Catch:{ Exception -> 0x39b4 }
            if (r11 == 0) goto L_0x357b
            int r11 = DEFALT_THEME_ACCENT_ID     // Catch:{ Exception -> 0x39b4 }
            goto L_0x357c
        L_0x357b:
            r11 = 0
        L_0x357c:
            int r10 = r4.getInt(r10, r11)     // Catch:{ Exception -> 0x39b4 }
            r8.currentAccentId = r10     // Catch:{ Exception -> 0x39b4 }
            java.util.ArrayList r10 = new java.util.ArrayList     // Catch:{ Exception -> 0x39b4 }
            r10.<init>()     // Catch:{ Exception -> 0x39b4 }
            boolean r11 = android.text.TextUtils.isEmpty(r9)     // Catch:{ Exception -> 0x39b4 }
            if (r11 != 0) goto L_0x36b0
            org.telegram.tgnet.SerializedData r11 = new org.telegram.tgnet.SerializedData     // Catch:{ all -> 0x36a6 }
            r12 = 3
            byte[] r9 = android.util.Base64.decode(r9, r12)     // Catch:{ all -> 0x36a6 }
            r11.<init>((byte[]) r9)     // Catch:{ all -> 0x36a6 }
            r9 = 1
            int r12 = r11.readInt32(r9)     // Catch:{ all -> 0x36a6 }
            int r13 = r11.readInt32(r9)     // Catch:{ all -> 0x36a6 }
            r14 = 0
        L_0x35a1:
            if (r14 >= r13) goto L_0x369d
            org.telegram.ui.ActionBar.Theme$ThemeAccent r15 = new org.telegram.ui.ActionBar.Theme$ThemeAccent     // Catch:{ all -> 0x3696 }
            r15.<init>()     // Catch:{ all -> 0x3696 }
            int r1 = r11.readInt32(r9)     // Catch:{ all -> 0x3696 }
            r15.id = r1     // Catch:{ all -> 0x3696 }
            int r1 = r11.readInt32(r9)     // Catch:{ all -> 0x3696 }
            r15.accentColor = r1     // Catch:{ all -> 0x3696 }
            r1 = 9
            if (r12 < r1) goto L_0x35be
            int r1 = r11.readInt32(r9)     // Catch:{ all -> 0x3696 }
            r15.accentColor2 = r1     // Catch:{ all -> 0x3696 }
        L_0x35be:
            r15.parentTheme = r8     // Catch:{ all -> 0x3696 }
            r1 = 1
            int r9 = r11.readInt32(r1)     // Catch:{ all -> 0x3696 }
            r15.myMessagesAccentColor = r9     // Catch:{ all -> 0x3696 }
            int r9 = r11.readInt32(r1)     // Catch:{ all -> 0x3696 }
            r15.myMessagesGradientAccentColor1 = r9     // Catch:{ all -> 0x3696 }
            r9 = 7
            if (r12 < r9) goto L_0x35dc
            int r9 = r11.readInt32(r1)     // Catch:{ all -> 0x3696 }
            r15.myMessagesGradientAccentColor2 = r9     // Catch:{ all -> 0x3696 }
            int r9 = r11.readInt32(r1)     // Catch:{ all -> 0x3696 }
            r15.myMessagesGradientAccentColor3 = r9     // Catch:{ all -> 0x3696 }
        L_0x35dc:
            r1 = 8
            if (r12 < r1) goto L_0x35e8
            r1 = 1
            boolean r9 = r11.readBool(r1)     // Catch:{ all -> 0x3696 }
            r15.myMessagesAnimated = r9     // Catch:{ all -> 0x3696 }
            goto L_0x35e9
        L_0x35e8:
            r1 = 1
        L_0x35e9:
            r9 = 3
            if (r12 < r9) goto L_0x35f6
            r22 = r6
            r9 = r7
            long r6 = r11.readInt64(r1)     // Catch:{ all -> 0x3696 }
            r15.backgroundOverrideColor = r6     // Catch:{ all -> 0x3696 }
            goto L_0x3600
        L_0x35f6:
            r22 = r6
            r9 = r7
            int r6 = r11.readInt32(r1)     // Catch:{ all -> 0x3696 }
            long r6 = (long) r6     // Catch:{ all -> 0x3696 }
            r15.backgroundOverrideColor = r6     // Catch:{ all -> 0x3696 }
        L_0x3600:
            r6 = 2
            if (r12 < r6) goto L_0x360a
            long r6 = r11.readInt64(r1)     // Catch:{ all -> 0x3696 }
            r15.backgroundGradientOverrideColor1 = r6     // Catch:{ all -> 0x3696 }
            goto L_0x3611
        L_0x360a:
            int r6 = r11.readInt32(r1)     // Catch:{ all -> 0x3696 }
            long r6 = (long) r6     // Catch:{ all -> 0x3696 }
            r15.backgroundGradientOverrideColor1 = r6     // Catch:{ all -> 0x3696 }
        L_0x3611:
            r6 = 6
            if (r12 < r6) goto L_0x3621
            long r6 = r11.readInt64(r1)     // Catch:{ all -> 0x3696 }
            r15.backgroundGradientOverrideColor2 = r6     // Catch:{ all -> 0x3696 }
            long r6 = r11.readInt64(r1)     // Catch:{ all -> 0x3696 }
            r15.backgroundGradientOverrideColor3 = r6     // Catch:{ all -> 0x3696 }
            r1 = 1
        L_0x3621:
            if (r12 < r1) goto L_0x3629
            int r6 = r11.readInt32(r1)     // Catch:{ all -> 0x3696 }
            r15.backgroundRotation = r6     // Catch:{ all -> 0x3696 }
        L_0x3629:
            r6 = 4
            if (r12 < r6) goto L_0x3645
            r11.readInt64(r1)     // Catch:{ all -> 0x3696 }
            double r6 = r11.readDouble(r1)     // Catch:{ all -> 0x3696 }
            float r6 = (float) r6     // Catch:{ all -> 0x3696 }
            r15.patternIntensity = r6     // Catch:{ all -> 0x3696 }
            boolean r6 = r11.readBool(r1)     // Catch:{ all -> 0x3696 }
            r15.patternMotion = r6     // Catch:{ all -> 0x3696 }
            r6 = 5
            if (r12 < r6) goto L_0x3645
            java.lang.String r6 = r11.readString(r1)     // Catch:{ all -> 0x3696 }
            r15.patternSlug = r6     // Catch:{ all -> 0x3696 }
        L_0x3645:
            r1 = 5
            if (r12 < r1) goto L_0x365f
            r6 = 1
            boolean r7 = r11.readBool(r6)     // Catch:{ all -> 0x3696 }
            if (r7 == 0) goto L_0x365f
            int r7 = r11.readInt32(r6)     // Catch:{ all -> 0x3696 }
            r15.account = r7     // Catch:{ all -> 0x3696 }
            int r7 = r11.readInt32(r6)     // Catch:{ all -> 0x3696 }
            org.telegram.tgnet.TLRPC$TL_theme r7 = org.telegram.tgnet.TLRPC$Theme.TLdeserialize(r11, r7, r6)     // Catch:{ all -> 0x3696 }
            r15.info = r7     // Catch:{ all -> 0x3696 }
        L_0x365f:
            org.telegram.tgnet.TLRPC$TL_theme r6 = r15.info     // Catch:{ all -> 0x3696 }
            if (r6 == 0) goto L_0x3667
            boolean r6 = r6.isDefault     // Catch:{ all -> 0x3696 }
            r15.isDefault = r6     // Catch:{ all -> 0x3696 }
        L_0x3667:
            android.util.SparseArray<org.telegram.ui.ActionBar.Theme$ThemeAccent> r6 = r8.themeAccentsMap     // Catch:{ all -> 0x3696 }
            int r7 = r15.id     // Catch:{ all -> 0x3696 }
            r6.put(r7, r15)     // Catch:{ all -> 0x3696 }
            org.telegram.tgnet.TLRPC$TL_theme r6 = r15.info     // Catch:{ all -> 0x3696 }
            if (r6 == 0) goto L_0x367c
            android.util.LongSparseArray<org.telegram.ui.ActionBar.Theme$ThemeAccent> r7 = r8.accentsByThemeId     // Catch:{ all -> 0x3696 }
            r18 = r2
            long r1 = r6.id     // Catch:{ all -> 0x3696 }
            r7.put(r1, r15)     // Catch:{ all -> 0x3696 }
            goto L_0x367e
        L_0x367c:
            r18 = r2
        L_0x367e:
            r10.add(r15)     // Catch:{ all -> 0x3696 }
            int r1 = r8.lastAccentId     // Catch:{ all -> 0x3696 }
            int r2 = r15.id     // Catch:{ all -> 0x3696 }
            int r1 = java.lang.Math.max(r1, r2)     // Catch:{ all -> 0x3696 }
            r8.lastAccentId = r1     // Catch:{ all -> 0x3696 }
            int r14 = r14 + 1
            r7 = r9
            r2 = r18
            r6 = r22
            r1 = 0
            r9 = 1
            goto L_0x35a1
        L_0x3696:
            r0 = move-exception
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ all -> 0x36a6 }
            r1.<init>(r0)     // Catch:{ all -> 0x36a6 }
            throw r1     // Catch:{ all -> 0x36a6 }
        L_0x369d:
            r22 = r6
            r9 = r7
        L_0x36a0:
            r11 = 9
            r12 = 1
            r13 = 3
            goto L_0x37c2
        L_0x36a6:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x39b4 }
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x39b4 }
            r1.<init>(r0)     // Catch:{ Exception -> 0x39b4 }
            throw r1     // Catch:{ Exception -> 0x39b4 }
        L_0x36b0:
            r18 = r2
            r22 = r6
            r9 = r7
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x39b4 }
            r1.<init>()     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r2 = "accent_for_"
            r1.append(r2)     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r2 = r8.assetName     // Catch:{ Exception -> 0x39b4 }
            r1.append(r2)     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x39b4 }
            r2 = r18
            r6 = 0
            int r7 = r2.getInt(r1, r6)     // Catch:{ Exception -> 0x39b4 }
            if (r7 == 0) goto L_0x36a0
            if (r5 != 0) goto L_0x36dc
            android.content.SharedPreferences$Editor r5 = r2.edit()     // Catch:{ Exception -> 0x39b4 }
            android.content.SharedPreferences$Editor r6 = r4.edit()     // Catch:{ Exception -> 0x39b4 }
            goto L_0x36de
        L_0x36dc:
            r6 = r22
        L_0x36de:
            r5.remove(r1)     // Catch:{ Exception -> 0x39b4 }
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeAccent> r1 = r8.themeAccents     // Catch:{ Exception -> 0x39b4 }
            int r1 = r1.size()     // Catch:{ Exception -> 0x39b4 }
            r11 = 0
        L_0x36e8:
            if (r11 >= r1) goto L_0x36ff
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeAccent> r12 = r8.themeAccents     // Catch:{ Exception -> 0x39b4 }
            java.lang.Object r12 = r12.get(r11)     // Catch:{ Exception -> 0x39b4 }
            org.telegram.ui.ActionBar.Theme$ThemeAccent r12 = (org.telegram.ui.ActionBar.Theme.ThemeAccent) r12     // Catch:{ Exception -> 0x39b4 }
            int r13 = r12.accentColor     // Catch:{ Exception -> 0x39b4 }
            if (r13 != r7) goto L_0x36fc
            int r1 = r12.id     // Catch:{ Exception -> 0x39b4 }
            r8.currentAccentId = r1     // Catch:{ Exception -> 0x39b4 }
            r1 = 1
            goto L_0x3700
        L_0x36fc:
            int r11 = r11 + 1
            goto L_0x36e8
        L_0x36ff:
            r1 = 0
        L_0x3700:
            if (r1 != 0) goto L_0x37a5
            org.telegram.ui.ActionBar.Theme$ThemeAccent r1 = new org.telegram.ui.ActionBar.Theme$ThemeAccent     // Catch:{ Exception -> 0x39b4 }
            r1.<init>()     // Catch:{ Exception -> 0x39b4 }
            r11 = 100
            r1.id = r11     // Catch:{ Exception -> 0x39b4 }
            r1.accentColor = r7     // Catch:{ Exception -> 0x39b4 }
            r1.parentTheme = r8     // Catch:{ Exception -> 0x39b4 }
            android.util.SparseArray<org.telegram.ui.ActionBar.Theme$ThemeAccent> r7 = r8.themeAccentsMap     // Catch:{ Exception -> 0x39b4 }
            r7.put(r11, r1)     // Catch:{ Exception -> 0x39b4 }
            r7 = 0
            r10.add(r7, r1)     // Catch:{ Exception -> 0x39b4 }
            r7 = 100
            r8.currentAccentId = r7     // Catch:{ Exception -> 0x39b4 }
            r7 = 101(0x65, float:1.42E-43)
            r8.lastAccentId = r7     // Catch:{ Exception -> 0x39b4 }
            org.telegram.tgnet.SerializedData r7 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x39b4 }
            r11 = 72
            r7.<init>((int) r11)     // Catch:{ Exception -> 0x39b4 }
            r11 = 9
            r7.writeInt32(r11)     // Catch:{ Exception -> 0x39b4 }
            r12 = 1
            r7.writeInt32(r12)     // Catch:{ Exception -> 0x39b4 }
            int r13 = r1.id     // Catch:{ Exception -> 0x39b4 }
            r7.writeInt32(r13)     // Catch:{ Exception -> 0x39b4 }
            int r13 = r1.accentColor     // Catch:{ Exception -> 0x39b4 }
            r7.writeInt32(r13)     // Catch:{ Exception -> 0x39b4 }
            int r13 = r1.myMessagesAccentColor     // Catch:{ Exception -> 0x39b4 }
            r7.writeInt32(r13)     // Catch:{ Exception -> 0x39b4 }
            int r13 = r1.myMessagesGradientAccentColor1     // Catch:{ Exception -> 0x39b4 }
            r7.writeInt32(r13)     // Catch:{ Exception -> 0x39b4 }
            int r13 = r1.myMessagesGradientAccentColor2     // Catch:{ Exception -> 0x39b4 }
            r7.writeInt32(r13)     // Catch:{ Exception -> 0x39b4 }
            int r13 = r1.myMessagesGradientAccentColor3     // Catch:{ Exception -> 0x39b4 }
            r7.writeInt32(r13)     // Catch:{ Exception -> 0x39b4 }
            boolean r13 = r1.myMessagesAnimated     // Catch:{ Exception -> 0x39b4 }
            r7.writeBool(r13)     // Catch:{ Exception -> 0x39b4 }
            long r13 = r1.backgroundOverrideColor     // Catch:{ Exception -> 0x39b4 }
            r7.writeInt64(r13)     // Catch:{ Exception -> 0x39b4 }
            long r13 = r1.backgroundGradientOverrideColor1     // Catch:{ Exception -> 0x39b4 }
            r7.writeInt64(r13)     // Catch:{ Exception -> 0x39b4 }
            long r13 = r1.backgroundGradientOverrideColor2     // Catch:{ Exception -> 0x39b4 }
            r7.writeInt64(r13)     // Catch:{ Exception -> 0x39b4 }
            long r13 = r1.backgroundGradientOverrideColor3     // Catch:{ Exception -> 0x39b4 }
            r7.writeInt64(r13)     // Catch:{ Exception -> 0x39b4 }
            int r13 = r1.backgroundRotation     // Catch:{ Exception -> 0x39b4 }
            r7.writeInt32(r13)     // Catch:{ Exception -> 0x39b4 }
            r13 = 0
            r7.writeInt64(r13)     // Catch:{ Exception -> 0x39b4 }
            float r13 = r1.patternIntensity     // Catch:{ Exception -> 0x39b4 }
            double r13 = (double) r13     // Catch:{ Exception -> 0x39b4 }
            r7.writeDouble(r13)     // Catch:{ Exception -> 0x39b4 }
            boolean r13 = r1.patternMotion     // Catch:{ Exception -> 0x39b4 }
            r7.writeBool(r13)     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r1 = r1.patternSlug     // Catch:{ Exception -> 0x39b4 }
            r7.writeString(r1)     // Catch:{ Exception -> 0x39b4 }
            r1 = 0
            r7.writeBool(r1)     // Catch:{ Exception -> 0x39b4 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x39b4 }
            r1.<init>()     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r13 = "accents_"
            r1.append(r13)     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r13 = r8.assetName     // Catch:{ Exception -> 0x39b4 }
            r1.append(r13)     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x39b4 }
            byte[] r7 = r7.toByteArray()     // Catch:{ Exception -> 0x39b4 }
            r13 = 3
            java.lang.String r7 = android.util.Base64.encodeToString(r7, r13)     // Catch:{ Exception -> 0x39b4 }
            r6.putString(r1, r7)     // Catch:{ Exception -> 0x39b4 }
            goto L_0x37a9
        L_0x37a5:
            r11 = 9
            r12 = 1
            r13 = 3
        L_0x37a9:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x39b4 }
            r1.<init>()     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r7 = "accent_current_"
            r1.append(r7)     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r7 = r8.assetName     // Catch:{ Exception -> 0x39b4 }
            r1.append(r7)     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x39b4 }
            int r7 = r8.currentAccentId     // Catch:{ Exception -> 0x39b4 }
            r6.putInt(r1, r7)     // Catch:{ Exception -> 0x39b4 }
            goto L_0x37c4
        L_0x37c2:
            r6 = r22
        L_0x37c4:
            boolean r1 = r10.isEmpty()     // Catch:{ Exception -> 0x39b4 }
            if (r1 != 0) goto L_0x37d3
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeAccent> r1 = r8.themeAccents     // Catch:{ Exception -> 0x39b4 }
            r7 = 0
            r1.addAll(r7, r10)     // Catch:{ Exception -> 0x39b4 }
            sortAccents(r8)     // Catch:{ Exception -> 0x39b4 }
        L_0x37d3:
            android.util.SparseArray<org.telegram.ui.ActionBar.Theme$ThemeAccent> r1 = r8.themeAccentsMap     // Catch:{ Exception -> 0x39b4 }
            if (r1 == 0) goto L_0x37e9
            int r7 = r8.currentAccentId     // Catch:{ Exception -> 0x39b4 }
            java.lang.Object r1 = r1.get(r7)     // Catch:{ Exception -> 0x39b4 }
            if (r1 != 0) goto L_0x37e9
            boolean r1 = r8.firstAccentIsDefault     // Catch:{ Exception -> 0x39b4 }
            if (r1 == 0) goto L_0x37e6
            int r1 = DEFALT_THEME_ACCENT_ID     // Catch:{ Exception -> 0x39b4 }
            goto L_0x37e7
        L_0x37e6:
            r1 = 0
        L_0x37e7:
            r8.currentAccentId = r1     // Catch:{ Exception -> 0x39b4 }
        L_0x37e9:
            r8.loadWallpapers(r4)     // Catch:{ Exception -> 0x39b4 }
            r1 = 0
            org.telegram.ui.ActionBar.Theme$ThemeAccent r7 = r8.getAccent(r1)     // Catch:{ Exception -> 0x39b4 }
            if (r7 == 0) goto L_0x3801
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r1 = r7.overrideWallpaper     // Catch:{ Exception -> 0x39b4 }
            r8.overrideWallpaper = r1     // Catch:{ Exception -> 0x39b4 }
            goto L_0x3801
        L_0x37f8:
            r22 = r6
            r9 = r7
            r11 = 9
            r12 = 1
            r13 = 3
            r6 = r22
        L_0x3801:
            r7 = r9
            r1 = 0
            goto L_0x3535
        L_0x3805:
            r22 = r6
            r12 = 1
            r13 = 3
            if (r5 == 0) goto L_0x3811
            r5.commit()     // Catch:{ Exception -> 0x39b4 }
            r22.commit()     // Catch:{ Exception -> 0x39b4 }
        L_0x3811:
            java.lang.String r1 = "selectedAutoNightType"
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x39b4 }
            r5 = 29
            if (r4 < r5) goto L_0x381b
            r8 = 3
            goto L_0x381c
        L_0x381b:
            r8 = 0
        L_0x381c:
            int r1 = r2.getInt(r1, r8)     // Catch:{ Exception -> 0x39b4 }
            selectedAutoNightType = r1     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r1 = "autoNightScheduleByLocation"
            r4 = 0
            boolean r1 = r2.getBoolean(r1, r4)     // Catch:{ Exception -> 0x39b4 }
            autoNightScheduleByLocation = r1     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r1 = "autoNightBrighnessThreshold"
            r4 = 1048576000(0x3e800000, float:0.25)
            float r1 = r2.getFloat(r1, r4)     // Catch:{ Exception -> 0x39b4 }
            autoNightBrighnessThreshold = r1     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r1 = "autoNightDayStartTime"
            r4 = 1320(0x528, float:1.85E-42)
            int r1 = r2.getInt(r1, r4)     // Catch:{ Exception -> 0x39b4 }
            autoNightDayStartTime = r1     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r1 = "autoNightDayEndTime"
            r4 = 480(0x1e0, float:6.73E-43)
            int r1 = r2.getInt(r1, r4)     // Catch:{ Exception -> 0x39b4 }
            autoNightDayEndTime = r1     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r1 = "autoNightSunsetTime"
            r4 = 1320(0x528, float:1.85E-42)
            int r1 = r2.getInt(r1, r4)     // Catch:{ Exception -> 0x39b4 }
            autoNightSunsetTime = r1     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r1 = "autoNightSunriseTime"
            r4 = 480(0x1e0, float:6.73E-43)
            int r1 = r2.getInt(r1, r4)     // Catch:{ Exception -> 0x39b4 }
            autoNightSunriseTime = r1     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r1 = "autoNightCityName"
            java.lang.String r1 = r2.getString(r1, r3)     // Catch:{ Exception -> 0x39b4 }
            autoNightCityName = r1     // Catch:{ Exception -> 0x39b4 }
            java.lang.String r1 = "autoNightLocationLatitude3"
            r4 = 10000(0x2710, double:4.9407E-320)
            long r4 = r2.getLong(r1, r4)     // Catch:{ Exception -> 0x39b4 }
            r6 = 10000(0x2710, double:4.9407E-320)
            int r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r1 == 0) goto L_0x387a
            double r4 = java.lang.Double.longBitsToDouble(r4)     // Catch:{ Exception -> 0x39b4 }
            autoNightLocationLatitude = r4     // Catch:{ Exception -> 0x39b4 }
            goto L_0x3881
        L_0x387a:
            r4 = 4666723172467343360(0x40cNUM, double:10000.0)
            autoNightLocationLatitude = r4     // Catch:{ Exception -> 0x39b4 }
        L_0x3881:
            java.lang.String r1 = "autoNightLocationLongitude3"
            r4 = 10000(0x2710, double:4.9407E-320)
            long r4 = r2.getLong(r1, r4)     // Catch:{ Exception -> 0x39b4 }
            r6 = 10000(0x2710, double:4.9407E-320)
            int r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r1 == 0) goto L_0x3896
            double r4 = java.lang.Double.longBitsToDouble(r4)     // Catch:{ Exception -> 0x39b4 }
            autoNightLocationLongitude = r4     // Catch:{ Exception -> 0x39b4 }
            goto L_0x389d
        L_0x3896:
            r4 = 4666723172467343360(0x40cNUM, double:10000.0)
            autoNightLocationLongitude = r4     // Catch:{ Exception -> 0x39b4 }
        L_0x389d:
            java.lang.String r1 = "autoNightLastSunCheckDay"
            r4 = -1
            int r1 = r2.getInt(r1, r4)     // Catch:{ Exception -> 0x39b4 }
            autoNightLastSunCheckDay = r1     // Catch:{ Exception -> 0x39b4 }
            if (r0 != 0) goto L_0x38ab
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = defaultTheme
            goto L_0x38ad
        L_0x38ab:
            currentDayTheme = r0
        L_0x38ad:
            java.lang.String r1 = "overrideThemeWallpaper"
            boolean r1 = r2.contains(r1)
            if (r1 != 0) goto L_0x38bd
            java.lang.String r1 = "selectedBackground2"
            boolean r1 = r2.contains(r1)
            if (r1 == 0) goto L_0x3977
        L_0x38bd:
            java.lang.String r1 = "overrideThemeWallpaper"
            r4 = 0
            boolean r1 = r2.getBoolean(r1, r4)
            r4 = 1000001(0xvar_, double:4.94066E-318)
            java.lang.String r6 = "selectedBackground2"
            long r4 = r2.getLong(r6, r4)
            r6 = -1
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 == 0) goto L_0x38e2
            if (r1 == 0) goto L_0x3964
            r6 = -2
            int r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r1 == 0) goto L_0x3964
            r6 = 1000001(0xvar_, double:4.94066E-318)
            int r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r1 == 0) goto L_0x3964
        L_0x38e2:
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r1 = new org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo
            r1.<init>()
            java.lang.String r6 = "selectedColor"
            r7 = 0
            int r6 = r2.getInt(r6, r7)
            r1.color = r6
            java.lang.String r6 = "selectedBackgroundSlug"
            java.lang.String r6 = r2.getString(r6, r3)
            r1.slug = r6
            r6 = -100
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 < 0) goto L_0x3911
            r6 = -1
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 > 0) goto L_0x3911
            int r4 = r1.color
            if (r4 == 0) goto L_0x3911
            java.lang.String r4 = "c"
            r1.slug = r4
            r1.fileName = r3
            r1.originalFileName = r3
            goto L_0x3919
        L_0x3911:
            java.lang.String r3 = "wallpaper.jpg"
            r1.fileName = r3
            java.lang.String r3 = "wallpaper_original.jpg"
            r1.originalFileName = r3
        L_0x3919:
            java.lang.String r3 = "selectedGradientColor"
            r4 = 0
            int r3 = r2.getInt(r3, r4)
            r1.gradientColor1 = r3
            java.lang.String r3 = "selectedGradientColor2"
            int r3 = r2.getInt(r3, r4)
            r1.gradientColor2 = r3
            java.lang.String r3 = "selectedGradientColor3"
            int r3 = r2.getInt(r3, r4)
            r1.gradientColor3 = r3
            r3 = 45
            java.lang.String r5 = "selectedGradientRotation"
            int r3 = r2.getInt(r5, r3)
            r1.rotation = r3
            java.lang.String r3 = "selectedBackgroundBlurred"
            boolean r3 = r2.getBoolean(r3, r4)
            r1.isBlurred = r3
            java.lang.String r3 = "selectedBackgroundMotion"
            boolean r3 = r2.getBoolean(r3, r4)
            r1.isMotion = r3
            r3 = 1056964608(0x3var_, float:0.5)
            java.lang.String r4 = "selectedIntensity"
            float r3 = r2.getFloat(r4, r3)
            r1.intensity = r3
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = currentDayTheme
            r3.setOverrideWallpaper(r1)
            int r3 = selectedAutoNightType
            if (r3 == 0) goto L_0x3964
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = currentNightTheme
            r3.setOverrideWallpaper(r1)
        L_0x3964:
            android.content.SharedPreferences$Editor r1 = r2.edit()
            java.lang.String r2 = "overrideThemeWallpaper"
            android.content.SharedPreferences$Editor r1 = r1.remove(r2)
            java.lang.String r2 = "selectedBackground2"
            android.content.SharedPreferences$Editor r1 = r1.remove(r2)
            r1.commit()
        L_0x3977:
            int r1 = needSwitchToTheme()
            r2 = 2
            if (r1 != r2) goto L_0x3980
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = currentNightTheme
        L_0x3980:
            if (r1 != r2) goto L_0x3985
            r1 = 0
            r5 = 1
            goto L_0x3987
        L_0x3985:
            r1 = 0
            r5 = 0
        L_0x3987:
            applyTheme(r0, r1, r1, r5)
            org.telegram.messenger.MessagesController$$ExternalSyntheticLambda219 r0 = org.telegram.messenger.MessagesController$$ExternalSyntheticLambda219.INSTANCE
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            org.telegram.ui.ActionBar.Theme$9 r0 = new org.telegram.ui.ActionBar.Theme$9
            r0.<init>()
            ambientSensorListener = r0
            int[] r0 = new int[r2]
            viewPos = r0
            android.graphics.Paint r0 = new android.graphics.Paint
            r0.<init>()
            DEBUG_RED = r0
            r1 = -65536(0xfffffffffffvar_, float:NaN)
            r0.setColor(r1)
            android.graphics.Paint r0 = new android.graphics.Paint
            r0.<init>()
            DEBUG_BLUE = r0
            r1 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            r0.setColor(r1)
            return
        L_0x39b4:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            java.lang.RuntimeException r1 = new java.lang.RuntimeException
            r1.<init>(r0)
            goto L_0x39bf
        L_0x39be:
            throw r1
        L_0x39bf:
            goto L_0x39be
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.<clinit>():void");
    }

    /* access modifiers changed from: private */
    public static void sortAccents(ThemeInfo themeInfo) {
        Collections.sort(themeInfo.themeAccents, Theme$$ExternalSyntheticLambda7.INSTANCE);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$sortAccents$0(ThemeAccent themeAccent, ThemeAccent themeAccent2) {
        if (isHome(themeAccent)) {
            return -1;
        }
        if (isHome(themeAccent2)) {
            return 1;
        }
        char c = themeAccent.isDefault;
        char c2 = themeAccent2.isDefault;
        if (c == c2) {
            if (c != 0) {
                int i = themeAccent.id;
                int i2 = themeAccent2.id;
                if (i > i2) {
                    return 1;
                }
                if (i < i2) {
                    return -1;
                }
                return 0;
            }
            int i3 = themeAccent.id;
            int i4 = themeAccent2.id;
            if (i3 > i4) {
                return -1;
            }
            if (i3 < i4) {
                return 1;
            }
            return 0;
        } else if (c > c2) {
            return -1;
        } else {
            return 1;
        }
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
                Drawable access$2800 = Theme.getStateDrawable(this, i);
                ColorFilter colorFilter = null;
                if (access$2800 instanceof BitmapDrawable) {
                    colorFilter = ((BitmapDrawable) access$2800).getPaint().getColorFilter();
                } else if (access$2800 instanceof NinePatchDrawable) {
                    colorFilter = ((NinePatchDrawable) access$2800).getPaint().getColorFilter();
                }
                boolean selectDrawable = super.selectDrawable(i);
                if (colorFilter != null) {
                    access$2800.setColorFilter(colorFilter);
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
        return createEditTextDrawable(context, getColor(z ? "dialogInputField" : "windowBackgroundWhiteInputField"), getColor(z ? "dialogInputFieldActivated" : "windowBackgroundWhiteInputFieldActivated"));
    }

    public static Drawable createEditTextDrawable(Context context, int i, int i2) {
        Resources resources = context.getResources();
        Drawable mutate = resources.getDrawable(NUM).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
        Drawable mutate2 = resources.getDrawable(NUM).mutate();
        mutate2.setColorFilter(new PorterDuffColorFilter(i2, PorterDuff.Mode.MULTIPLY));
        AnonymousClass4 r4 = new StateListDrawable() {
            public boolean selectDrawable(int i) {
                if (Build.VERSION.SDK_INT >= 21) {
                    return super.selectDrawable(i);
                }
                Drawable access$2800 = Theme.getStateDrawable(this, i);
                ColorFilter colorFilter = null;
                if (access$2800 instanceof BitmapDrawable) {
                    colorFilter = ((BitmapDrawable) access$2800).getPaint().getColorFilter();
                } else if (access$2800 instanceof NinePatchDrawable) {
                    colorFilter = ((NinePatchDrawable) access$2800).getPaint().getColorFilter();
                }
                boolean selectDrawable = super.selectDrawable(i);
                if (colorFilter != null) {
                    access$2800.setColorFilter(colorFilter);
                }
                return selectDrawable;
            }
        };
        r4.addState(new int[]{16842910, 16842908}, mutate2);
        r4.addState(new int[]{16842908}, mutate2);
        r4.addState(StateSet.WILD_CARD, mutate);
        return r4;
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

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0056, code lost:
        if (r2 <= 31) goto L_0x005c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x005a, code lost:
        if (r2 == 1) goto L_0x005c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x005c, code lost:
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
            if (r4 < 0) goto L_0x007d
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
            r0.get(r3)
            r3 = 11
            int r0 = r0.get(r3)
            r4 = 1
            if (r1 != 0) goto L_0x0040
            if (r2 != r4) goto L_0x0040
            r5 = 23
            if (r0 > r5) goto L_0x0040
            canStartHolidayAnimation = r4
            goto L_0x0043
        L_0x0040:
            r0 = 0
            canStartHolidayAnimation = r0
        L_0x0043:
            android.graphics.drawable.Drawable r0 = dialogs_holidayDrawable
            if (r0 != 0) goto L_0x007d
            if (r1 != r3) goto L_0x0058
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            r3 = 31
            if (r0 == 0) goto L_0x0052
            r0 = 29
            goto L_0x0054
        L_0x0052:
            r0 = 31
        L_0x0054:
            if (r2 < r0) goto L_0x0058
            if (r2 <= r3) goto L_0x005c
        L_0x0058:
            if (r1 != 0) goto L_0x007d
            if (r2 != r4) goto L_0x007d
        L_0x005c:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2131166003(0x7var_, float:1.794624E38)
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
        L_0x007d:
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

    public static float getThemeIntensity(float f) {
        return (f >= 0.0f || getActiveTheme().isDark()) ? f : -f;
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

    public static Drawable createRoundRectDrawable(int i, int i2, int i3) {
        float f = (float) i;
        float f2 = (float) i2;
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(new float[]{f, f, f, f, f2, f2, f2, f2}, (RectF) null, (float[]) null));
        shapeDrawable.getPaint().setColor(i3);
        return shapeDrawable;
    }

    public static Drawable createServiceDrawable(int i, View view, View view2) {
        return createServiceDrawable(i, view, view2, chat_actionBackgroundPaint);
    }

    public static Drawable createServiceDrawable(final int i, final View view, final View view2, final Paint paint) {
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
                canvas.drawRoundRect(rectF, (float) i, (float) i, paint);
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
        return getRoundRectSelectorDrawable(AndroidUtilities.dp(3.0f), i);
    }

    public static Drawable getRoundRectSelectorDrawable(int i, int i2) {
        if (Build.VERSION.SDK_INT >= 21) {
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{(i2 & 16777215) | NUM}), (Drawable) null, createRoundRectDrawable(i, -1));
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        int i3 = (i2 & 16777215) | NUM;
        stateListDrawable.addState(new int[]{16842919}, createRoundRectDrawable(i, i3));
        stateListDrawable.addState(new int[]{16842913}, createRoundRectDrawable(i, i3));
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
    public static android.graphics.drawable.Drawable createSelectorDrawable(int r11, final int r12, final int r13) {
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
            r7.<init>(r12, r13)
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

    public static class AdaptiveRipple {
        private static float[] tempHSV;

        public static Drawable rect() {
            return rect(Theme.getColor("windowBackgroundWhite"));
        }

        public static Drawable rect(int i) {
            return rect(i, 0.0f);
        }

        public static Drawable rect(int i, float... fArr) {
            return createRect(0, calcRippleColor(i), fArr);
        }

        public static Drawable filledRect(String str, float... fArr) {
            return filledRect(Theme.getColor(str), fArr);
        }

        public static Drawable filledRect(int i) {
            return createRect(i, calcRippleColor(i), new float[0]);
        }

        public static Drawable filledRect(int i, float... fArr) {
            return createRect(i, calcRippleColor(i), fArr);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v0, resolved type: android.graphics.drawable.ColorDrawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: android.graphics.drawable.ColorDrawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: android.graphics.drawable.ColorDrawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: android.graphics.drawable.ShapeDrawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: android.graphics.drawable.ColorDrawable} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public static android.graphics.drawable.Drawable createRect(int r4, int r5, float... r6) {
            /*
                r0 = 0
                if (r4 == 0) goto L_0x0025
                boolean r1 = hasNonzeroRadii(r6)
                if (r1 == 0) goto L_0x0020
                android.graphics.drawable.ShapeDrawable r1 = new android.graphics.drawable.ShapeDrawable
                android.graphics.drawable.shapes.RoundRectShape r2 = new android.graphics.drawable.shapes.RoundRectShape
                float[] r3 = calcRadii(r6)
                r2.<init>(r3, r0, r0)
                r1.<init>(r2)
                android.graphics.Paint r0 = r1.getPaint()
                r0.setColor(r4)
                r0 = r1
                goto L_0x0025
            L_0x0020:
                android.graphics.drawable.ColorDrawable r0 = new android.graphics.drawable.ColorDrawable
                r0.<init>(r4)
            L_0x0025:
                android.graphics.drawable.Drawable r4 = createRect((android.graphics.drawable.Drawable) r0, (int) r5, (float[]) r6)
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.AdaptiveRipple.createRect(int, int, float[]):android.graphics.drawable.Drawable");
        }

        private static Drawable createRect(Drawable drawable, int i, float... fArr) {
            ShapeDrawable shapeDrawable;
            ShapeDrawable shapeDrawable2;
            if (Build.VERSION.SDK_INT >= 21) {
                if (hasNonzeroRadii(fArr)) {
                    shapeDrawable2 = new ShapeDrawable(new RoundRectShape(calcRadii(fArr), (RectF) null, (float[]) null));
                    shapeDrawable2.getPaint().setColor(-1);
                } else {
                    shapeDrawable2 = new ShapeDrawable(new RectShape());
                    shapeDrawable2.getPaint().setColor(-1);
                }
                return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{i}), drawable, shapeDrawable2);
            }
            StateListDrawable stateListDrawable = new StateListDrawable();
            if (hasNonzeroRadii(fArr)) {
                shapeDrawable = new ShapeDrawable(new RoundRectShape(calcRadii(fArr), (RectF) null, (float[]) null));
                shapeDrawable.getPaint().setColor(i);
            } else {
                shapeDrawable = new ShapeDrawable(new RectShape());
                shapeDrawable.getPaint().setColor(i);
            }
            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{drawable, shapeDrawable});
            stateListDrawable.addState(new int[]{16842919}, layerDrawable);
            stateListDrawable.addState(new int[]{16842913}, layerDrawable);
            stateListDrawable.addState(StateSet.WILD_CARD, drawable);
            return stateListDrawable;
        }

        private static float[] calcRadii(float... fArr) {
            if (fArr.length == 0) {
                return new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
            }
            if (fArr.length == 1) {
                return new float[]{(float) AndroidUtilities.dp(fArr[0]), (float) AndroidUtilities.dp(fArr[0]), (float) AndroidUtilities.dp(fArr[0]), (float) AndroidUtilities.dp(fArr[0]), (float) AndroidUtilities.dp(fArr[0]), (float) AndroidUtilities.dp(fArr[0]), (float) AndroidUtilities.dp(fArr[0]), (float) AndroidUtilities.dp(fArr[0])};
            } else if (fArr.length == 2) {
                return new float[]{(float) AndroidUtilities.dp(fArr[0]), (float) AndroidUtilities.dp(fArr[0]), (float) AndroidUtilities.dp(fArr[0]), (float) AndroidUtilities.dp(fArr[0]), (float) AndroidUtilities.dp(fArr[1]), (float) AndroidUtilities.dp(fArr[1]), (float) AndroidUtilities.dp(fArr[1]), (float) AndroidUtilities.dp(fArr[1])};
            } else if (fArr.length == 3) {
                return new float[]{(float) AndroidUtilities.dp(fArr[0]), (float) AndroidUtilities.dp(fArr[0]), (float) AndroidUtilities.dp(fArr[1]), (float) AndroidUtilities.dp(fArr[1]), (float) AndroidUtilities.dp(fArr[2]), (float) AndroidUtilities.dp(fArr[2]), (float) AndroidUtilities.dp(fArr[2]), (float) AndroidUtilities.dp(fArr[2])};
            } else if (fArr.length < 8) {
                return new float[]{(float) AndroidUtilities.dp(fArr[0]), (float) AndroidUtilities.dp(fArr[0]), (float) AndroidUtilities.dp(fArr[1]), (float) AndroidUtilities.dp(fArr[1]), (float) AndroidUtilities.dp(fArr[2]), (float) AndroidUtilities.dp(fArr[2]), (float) AndroidUtilities.dp(fArr[3]), (float) AndroidUtilities.dp(fArr[3])};
            } else {
                return new float[]{(float) AndroidUtilities.dp(fArr[0]), (float) AndroidUtilities.dp(fArr[1]), (float) AndroidUtilities.dp(fArr[2]), (float) AndroidUtilities.dp(fArr[3]), (float) AndroidUtilities.dp(fArr[4]), (float) AndroidUtilities.dp(fArr[5]), (float) AndroidUtilities.dp(fArr[6]), (float) AndroidUtilities.dp(fArr[7])};
            }
        }

        private static boolean hasNonzeroRadii(float... fArr) {
            for (int i = 0; i < Math.min(8, fArr.length); i++) {
                if (fArr[i] > 0.0f) {
                    return true;
                }
            }
            return false;
        }

        public static int calcRippleColor(int i) {
            if (tempHSV == null) {
                tempHSV = new float[3];
            }
            Color.colorToHSV(i, tempHSV);
            float[] fArr = tempHSV;
            if (fArr[1] > 0.01f) {
                fArr[1] = Math.min(1.0f, Math.max(0.0f, fArr[1] + (Theme.isCurrentThemeDark() ? 0.25f : -0.25f)));
                float[] fArr2 = tempHSV;
                fArr2[2] = Math.min(1.0f, Math.max(0.0f, fArr2[2] + (Theme.isCurrentThemeDark() ? 0.05f : -0.05f)));
            } else {
                fArr[2] = Math.min(1.0f, Math.max(0.0f, fArr[2] + (Theme.isCurrentThemeDark() ? 0.1f : -0.1f)));
            }
            return Color.HSVToColor(127, tempHSV);
        }
    }

    public static class RippleRadMaskDrawable extends Drawable {
        boolean invalidatePath;
        private Path path = new Path();
        private float[] radii;

        public int getOpacity() {
            return 0;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public RippleRadMaskDrawable(float f, float f2) {
            float[] fArr = new float[8];
            this.radii = fArr;
            this.invalidatePath = true;
            float dp = (float) AndroidUtilities.dp(f);
            fArr[3] = dp;
            fArr[2] = dp;
            fArr[1] = dp;
            fArr[0] = dp;
            float[] fArr2 = this.radii;
            float dp2 = (float) AndroidUtilities.dp(f2);
            fArr2[7] = dp2;
            fArr2[6] = dp2;
            fArr2[5] = dp2;
            fArr2[4] = dp2;
        }

        public RippleRadMaskDrawable(float f, float f2, float f3, float f4) {
            float[] fArr = new float[8];
            this.radii = fArr;
            this.invalidatePath = true;
            float dp = (float) AndroidUtilities.dp(f);
            fArr[1] = dp;
            fArr[0] = dp;
            float[] fArr2 = this.radii;
            float dp2 = (float) AndroidUtilities.dp(f2);
            fArr2[3] = dp2;
            fArr2[2] = dp2;
            float[] fArr3 = this.radii;
            float dp3 = (float) AndroidUtilities.dp(f3);
            fArr3[5] = dp3;
            fArr3[4] = dp3;
            float[] fArr4 = this.radii;
            float dp4 = (float) AndroidUtilities.dp(f4);
            fArr4[7] = dp4;
            fArr4[6] = dp4;
        }

        /* access modifiers changed from: protected */
        public void onBoundsChange(Rect rect) {
            this.invalidatePath = true;
        }

        public void draw(Canvas canvas) {
            if (this.invalidatePath) {
                this.invalidatePath = false;
                this.path.reset();
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(getBounds());
                this.path.addRoundRect(rectF, this.radii, Path.Direction.CW);
            }
            canvas.drawPath(this.path, Theme.maskPaint);
        }
    }

    public static void setMaskDrawableRad(Drawable drawable, int i, int i2) {
        if (Build.VERSION.SDK_INT >= 21 && (drawable instanceof RippleDrawable)) {
            RippleDrawable rippleDrawable = (RippleDrawable) drawable;
            int numberOfLayers = rippleDrawable.getNumberOfLayers();
            for (int i3 = 0; i3 < numberOfLayers; i3++) {
                if (rippleDrawable.getDrawable(i3) instanceof RippleRadMaskDrawable) {
                    rippleDrawable.setDrawableByLayerId(16908334, new RippleRadMaskDrawable((float) i, (float) i2));
                    return;
                }
            }
        }
    }

    public static Drawable createRadSelectorDrawable(int i, int i2, int i3) {
        if (Build.VERSION.SDK_INT >= 21) {
            maskPaint.setColor(-1);
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{i}), (Drawable) null, new RippleRadMaskDrawable((float) i2, (float) i3));
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, new ColorDrawable(i));
        stateListDrawable.addState(new int[]{16842913}, new ColorDrawable(i));
        stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(0));
        return stateListDrawable;
    }

    public static Drawable createRadSelectorDrawable(int i, int i2, int i3, int i4, int i5) {
        if (Build.VERSION.SDK_INT >= 21) {
            maskPaint.setColor(-1);
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{i}), (Drawable) null, new RippleRadMaskDrawable((float) i2, (float) i3, (float) i4, (float) i5));
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
        Collections.sort(themes, Theme$$ExternalSyntheticLambda8.INSTANCE);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$sortThemes$1(ThemeInfo themeInfo, ThemeInfo themeInfo2) {
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

    /* JADX WARNING: Can't wrap try/catch for region: R(13:24|25|26|(6:28|(1:32)|33|(1:37)|38|(1:42))|43|45|46|(1:48)|49|50|(1:52)|53|(1:55)) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:49:0x0131 */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0137 A[Catch:{ all -> 0x014a }] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0145 A[Catch:{ all -> 0x014a }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.telegram.ui.ActionBar.Theme.ThemeInfo fillThemeValues(java.io.File r7, java.lang.String r8, org.telegram.tgnet.TLRPC$TL_theme r9) {
        /*
            r0 = 0
            org.telegram.ui.ActionBar.Theme$ThemeInfo r1 = new org.telegram.ui.ActionBar.Theme$ThemeInfo     // Catch:{ Exception -> 0x0152 }
            r1.<init>()     // Catch:{ Exception -> 0x0152 }
            r1.name = r8     // Catch:{ Exception -> 0x0152 }
            r1.info = r9     // Catch:{ Exception -> 0x0152 }
            java.lang.String r7 = r7.getAbsolutePath()     // Catch:{ Exception -> 0x0152 }
            r1.pathToFile = r7     // Catch:{ Exception -> 0x0152 }
            int r7 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ Exception -> 0x0152 }
            r1.account = r7     // Catch:{ Exception -> 0x0152 }
            r7 = 1
            java.lang.String[] r8 = new java.lang.String[r7]     // Catch:{ Exception -> 0x0152 }
            java.io.File r9 = new java.io.File     // Catch:{ Exception -> 0x0152 }
            java.lang.String r2 = r1.pathToFile     // Catch:{ Exception -> 0x0152 }
            r9.<init>(r2)     // Catch:{ Exception -> 0x0152 }
            java.util.HashMap r9 = getThemeFileValues(r9, r0, r8)     // Catch:{ Exception -> 0x0152 }
            checkIsDark(r9, r1)     // Catch:{ Exception -> 0x0152 }
            r9 = 0
            r2 = r8[r9]     // Catch:{ Exception -> 0x0152 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x0152 }
            if (r2 != 0) goto L_0x014f
            r8 = r8[r9]     // Catch:{ Exception -> 0x0152 }
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x0152 }
            java.io.File r3 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ Exception -> 0x0152 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0152 }
            r4.<init>()     // Catch:{ Exception -> 0x0152 }
            java.lang.String r5 = org.telegram.messenger.Utilities.MD5(r8)     // Catch:{ Exception -> 0x0152 }
            r4.append(r5)     // Catch:{ Exception -> 0x0152 }
            java.lang.String r5 = ".wp"
            r4.append(r5)     // Catch:{ Exception -> 0x0152 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0152 }
            r2.<init>(r3, r4)     // Catch:{ Exception -> 0x0152 }
            java.lang.String r2 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x0152 }
            r1.pathToWallpaper = r2     // Catch:{ Exception -> 0x0152 }
            android.net.Uri r8 = android.net.Uri.parse(r8)     // Catch:{ all -> 0x014a }
            java.lang.String r2 = "slug"
            java.lang.String r2 = r8.getQueryParameter(r2)     // Catch:{ all -> 0x014a }
            r1.slug = r2     // Catch:{ all -> 0x014a }
            java.lang.String r2 = "mode"
            java.lang.String r2 = r8.getQueryParameter(r2)     // Catch:{ all -> 0x014a }
            if (r2 == 0) goto L_0x0097
            java.lang.String r2 = r2.toLowerCase()     // Catch:{ all -> 0x014a }
            java.lang.String r3 = " "
            java.lang.String[] r2 = r2.split(r3)     // Catch:{ all -> 0x014a }
            if (r2 == 0) goto L_0x0097
            int r3 = r2.length     // Catch:{ all -> 0x014a }
            if (r3 <= 0) goto L_0x0097
            r3 = 0
        L_0x0078:
            int r4 = r2.length     // Catch:{ all -> 0x014a }
            if (r3 >= r4) goto L_0x0097
            java.lang.String r4 = "blur"
            r5 = r2[r3]     // Catch:{ all -> 0x014a }
            boolean r4 = r4.equals(r5)     // Catch:{ all -> 0x014a }
            if (r4 == 0) goto L_0x0088
            r1.isBlured = r7     // Catch:{ all -> 0x014a }
            goto L_0x0094
        L_0x0088:
            java.lang.String r4 = "motion"
            r5 = r2[r3]     // Catch:{ all -> 0x014a }
            boolean r4 = r4.equals(r5)     // Catch:{ all -> 0x014a }
            if (r4 == 0) goto L_0x0094
            r1.isMotion = r7     // Catch:{ all -> 0x014a }
        L_0x0094:
            int r3 = r3 + 1
            goto L_0x0078
        L_0x0097:
            java.lang.String r7 = "intensity"
            java.lang.String r7 = r8.getQueryParameter(r7)     // Catch:{ all -> 0x014a }
            boolean r2 = android.text.TextUtils.isEmpty(r7)     // Catch:{ all -> 0x014a }
            if (r2 != 0) goto L_0x0151
            java.lang.String r2 = "bg_color"
            java.lang.String r2 = r8.getQueryParameter(r2)     // Catch:{ Exception -> 0x011b }
            boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x011b }
            if (r3 != 0) goto L_0x011b
            r3 = 6
            java.lang.String r9 = r2.substring(r9, r3)     // Catch:{ Exception -> 0x011b }
            r4 = 16
            int r9 = java.lang.Integer.parseInt(r9, r4)     // Catch:{ Exception -> 0x011b }
            r5 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r9 = r9 | r5
            r1.patternBgColor = r9     // Catch:{ Exception -> 0x011b }
            int r9 = r2.length()     // Catch:{ Exception -> 0x011b }
            r6 = 13
            if (r9 < r6) goto L_0x00dd
            char r9 = r2.charAt(r3)     // Catch:{ Exception -> 0x011b }
            boolean r9 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r9)     // Catch:{ Exception -> 0x011b }
            if (r9 == 0) goto L_0x00dd
            r9 = 7
            java.lang.String r9 = r2.substring(r9, r6)     // Catch:{ Exception -> 0x011b }
            int r9 = java.lang.Integer.parseInt(r9, r4)     // Catch:{ Exception -> 0x011b }
            r9 = r9 | r5
            r1.patternBgGradientColor1 = r9     // Catch:{ Exception -> 0x011b }
        L_0x00dd:
            int r9 = r2.length()     // Catch:{ Exception -> 0x011b }
            r3 = 20
            if (r9 < r3) goto L_0x00fc
            char r9 = r2.charAt(r6)     // Catch:{ Exception -> 0x011b }
            boolean r9 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r9)     // Catch:{ Exception -> 0x011b }
            if (r9 == 0) goto L_0x00fc
            r9 = 14
            java.lang.String r9 = r2.substring(r9, r3)     // Catch:{ Exception -> 0x011b }
            int r9 = java.lang.Integer.parseInt(r9, r4)     // Catch:{ Exception -> 0x011b }
            r9 = r9 | r5
            r1.patternBgGradientColor2 = r9     // Catch:{ Exception -> 0x011b }
        L_0x00fc:
            int r9 = r2.length()     // Catch:{ Exception -> 0x011b }
            r6 = 27
            if (r9 != r6) goto L_0x011b
            char r9 = r2.charAt(r3)     // Catch:{ Exception -> 0x011b }
            boolean r9 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r9)     // Catch:{ Exception -> 0x011b }
            if (r9 == 0) goto L_0x011b
            r9 = 21
            java.lang.String r9 = r2.substring(r9)     // Catch:{ Exception -> 0x011b }
            int r9 = java.lang.Integer.parseInt(r9, r4)     // Catch:{ Exception -> 0x011b }
            r9 = r9 | r5
            r1.patternBgGradientColor3 = r9     // Catch:{ Exception -> 0x011b }
        L_0x011b:
            java.lang.String r9 = "rotation"
            java.lang.String r8 = r8.getQueryParameter(r9)     // Catch:{ Exception -> 0x0131 }
            boolean r9 = android.text.TextUtils.isEmpty(r8)     // Catch:{ Exception -> 0x0131 }
            if (r9 != 0) goto L_0x0131
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r8)     // Catch:{ Exception -> 0x0131 }
            int r8 = r8.intValue()     // Catch:{ Exception -> 0x0131 }
            r1.patternBgGradientRotation = r8     // Catch:{ Exception -> 0x0131 }
        L_0x0131:
            boolean r8 = android.text.TextUtils.isEmpty(r7)     // Catch:{ all -> 0x014a }
            if (r8 != 0) goto L_0x0141
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r7)     // Catch:{ all -> 0x014a }
            int r7 = r7.intValue()     // Catch:{ all -> 0x014a }
            r1.patternIntensity = r7     // Catch:{ all -> 0x014a }
        L_0x0141:
            int r7 = r1.patternIntensity     // Catch:{ all -> 0x014a }
            if (r7 != 0) goto L_0x0151
            r7 = 50
            r1.patternIntensity = r7     // Catch:{ all -> 0x014a }
            goto L_0x0151
        L_0x014a:
            r7 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)     // Catch:{ Exception -> 0x0152 }
            goto L_0x0151
        L_0x014f:
            themedWallpaperLink = r0     // Catch:{ Exception -> 0x0152 }
        L_0x0151:
            return r1
        L_0x0152:
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
        applyTheme(themeInfo, true, z);
    }

    public static void applyTheme(ThemeInfo themeInfo, boolean z, boolean z2) {
        applyTheme(themeInfo, z, true, z2);
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
            r0.apply()     // Catch:{ Exception -> 0x01e8 }
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
            r0.apply()     // Catch:{ Exception -> 0x01e8 }
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
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)     // Catch:{ all -> 0x01ac }
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
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)     // Catch:{ Exception -> 0x01aa }
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
            org.telegram.messenger.MessagesController$$ExternalSyntheticLambda219 r0 = org.telegram.messenger.MessagesController$$ExternalSyntheticLambda219.INSTANCE     // Catch:{ Exception -> 0x01e8 }
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
        refreshThemeColors(false, false);
    }

    public static void refreshThemeColors(boolean z, boolean z2) {
        currentColors.clear();
        currentColors.putAll(currentColorsNoAccent);
        shouldDrawGradientIcons = true;
        ThemeAccent accent = currentTheme.getAccent(false);
        if (accent != null) {
            shouldDrawGradientIcons = accent.fillAccentColors(currentColorsNoAccent, currentColors);
        }
        if (!z2) {
            reloadWallpaper();
        }
        applyCommonTheme();
        applyDialogsTheme();
        applyProfileTheme();
        applyChatTheme(false, z);
        AndroidUtilities.runOnUIThread(new Theme$$ExternalSyntheticLambda6(!hasPreviousTheme));
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
        if (tmpHSV5 == null) {
            tmpHSV5 = new float[3];
        }
        float[] fArr3 = tmpHSV5;
        Color.colorToHSV(i, fArr3);
        boolean z2 = false;
        if (Math.min(abs(fArr3[0] - fArr[0]), abs((fArr3[0] - fArr[0]) - 360.0f)) > 30.0f) {
            return i;
        }
        float min = Math.min((fArr3[1] * 1.5f) / fArr[1], 1.0f);
        fArr3[0] = (fArr3[0] + fArr2[0]) - fArr[0];
        fArr3[1] = (fArr3[1] * fArr2[1]) / fArr[1];
        fArr3[2] = fArr3[2] * ((1.0f - min) + ((min * fArr2[2]) / fArr[2]));
        int HSVToColor = Color.HSVToColor(Color.alpha(i), fArr3);
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
                SerializedData serializedData = new SerializedData(((max * 16) + 2) * 4);
                serializedData.writeInt32(9);
                serializedData.writeInt32(max);
                for (int i = 0; i < size; i++) {
                    ThemeAccent themeAccent = themeInfo.themeAccents.get(i);
                    int i2 = themeAccent.id;
                    if (i2 >= 100) {
                        serializedData.writeInt32(i2);
                        serializedData.writeInt32(themeAccent.accentColor);
                        serializedData.writeInt32(themeAccent.accentColor2);
                        serializedData.writeInt32(themeAccent.myMessagesAccentColor);
                        serializedData.writeInt32(themeAccent.myMessagesGradientAccentColor1);
                        serializedData.writeInt32(themeAccent.myMessagesGradientAccentColor2);
                        serializedData.writeInt32(themeAccent.myMessagesGradientAccentColor3);
                        serializedData.writeBool(themeAccent.myMessagesAnimated);
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
        while (i3 < 4) {
            StringBuilder sb = new StringBuilder();
            sb.append("2remoteThemesHash");
            Object obj = "";
            sb.append(i3 != 0 ? Integer.valueOf(i3) : obj);
            edit.putLong(sb.toString(), remoteThemesHash[i3]);
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

    public static boolean isCurrentThemeDark() {
        return currentTheme.isDark();
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

    /* JADX WARNING: Removed duplicated region for block: B:108:0x0212 A[SYNTHETIC, Splitter:B:108:0x0212] */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x021c  */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x022c A[SYNTHETIC, Splitter:B:115:0x022c] */
    /* JADX WARNING: Removed duplicated region for block: B:128:? A[RETURN, SYNTHETIC] */
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
            r7 = 1
            if (r4 == r6) goto L_0x0092
            if (r2 == 0) goto L_0x0041
            int r6 = r2.myMessagesAccentColor
            goto L_0x0042
        L_0x0041:
            r6 = 0
        L_0x0042:
            if (r2 == 0) goto L_0x0047
            int r8 = r2.myMessagesGradientAccentColor1
            goto L_0x0048
        L_0x0047:
            r8 = 0
        L_0x0048:
            if (r2 == 0) goto L_0x004d
            int r9 = r2.myMessagesGradientAccentColor2
            goto L_0x004e
        L_0x004d:
            r9 = 0
        L_0x004e:
            if (r2 == 0) goto L_0x0053
            int r10 = r2.myMessagesGradientAccentColor3
            goto L_0x0054
        L_0x0053:
            r10 = 0
        L_0x0054:
            if (r6 == 0) goto L_0x0092
            if (r8 == 0) goto L_0x0092
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.lang.String r11 = "chat_outBubble"
            r4.put(r11, r6)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r8)
            java.lang.String r8 = "chat_outBubbleGradient"
            r4.put(r8, r6)
            if (r9 == 0) goto L_0x0080
            java.lang.Integer r6 = java.lang.Integer.valueOf(r9)
            java.lang.String r8 = "chat_outBubbleGradient2"
            r4.put(r8, r6)
            if (r10 == 0) goto L_0x0080
            java.lang.Integer r6 = java.lang.Integer.valueOf(r10)
            java.lang.String r8 = "chat_outBubbleGradient3"
            r4.put(r8, r6)
        L_0x0080:
            if (r2 == 0) goto L_0x0088
            boolean r2 = r2.myMessagesAnimated
            if (r2 == 0) goto L_0x0088
            r2 = 1
            goto L_0x0089
        L_0x0088:
            r2 = 0
        L_0x0089:
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            java.lang.String r6 = "chat_outBubbleGradientAnimated"
            r4.put(r6, r2)
        L_0x0092:
            java.util.Set r2 = r4.entrySet()
            java.util.Iterator r2 = r2.iterator()
        L_0x009a:
            boolean r6 = r2.hasNext()
            java.lang.String r8 = "\n"
            if (r6 == 0) goto L_0x00e8
            java.lang.Object r6 = r2.next()
            java.util.Map$Entry r6 = (java.util.Map.Entry) r6
            java.lang.Object r9 = r6.getKey()
            java.lang.String r9 = (java.lang.String) r9
            boolean r10 = r1 instanceof android.graphics.drawable.BitmapDrawable
            if (r10 != 0) goto L_0x00b4
            if (r0 == 0) goto L_0x00d5
        L_0x00b4:
            java.lang.String r10 = "chat_wallpaper"
            boolean r10 = r10.equals(r9)
            if (r10 != 0) goto L_0x009a
            java.lang.String r10 = "chat_wallpaper_gradient_to"
            boolean r10 = r10.equals(r9)
            if (r10 != 0) goto L_0x009a
            java.lang.String r10 = "key_chat_wallpaper_gradient_to2"
            boolean r10 = r10.equals(r9)
            if (r10 != 0) goto L_0x009a
            java.lang.String r10 = "key_chat_wallpaper_gradient_to3"
            boolean r10 = r10.equals(r9)
            if (r10 == 0) goto L_0x00d5
            goto L_0x009a
        L_0x00d5:
            r5.append(r9)
            java.lang.String r9 = "="
            r5.append(r9)
            java.lang.Object r6 = r6.getValue()
            r5.append(r6)
            r5.append(r8)
            goto L_0x009a
        L_0x00e8:
            r2 = 0
            java.io.FileOutputStream r6 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x020c }
            java.lang.String r9 = r12.pathToFile     // Catch:{ Exception -> 0x020c }
            r6.<init>(r9)     // Catch:{ Exception -> 0x020c }
            int r2 = r5.length()     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            if (r2 != 0) goto L_0x0105
            boolean r2 = r1 instanceof android.graphics.drawable.BitmapDrawable     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            if (r2 != 0) goto L_0x0105
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            if (r2 == 0) goto L_0x0105
            r2 = 32
            r5.append(r2)     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
        L_0x0105:
            java.lang.String r2 = r5.toString()     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            byte[] r2 = org.telegram.messenger.AndroidUtilities.getStringBytes(r2)     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r6.write(r2)     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r5 = 87
            if (r2 != 0) goto L_0x016c
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r2.<init>()     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            java.lang.String r9 = "WLS="
            r2.append(r9)     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r2.append(r0)     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r2.append(r8)     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            byte[] r2 = org.telegram.messenger.AndroidUtilities.getStringBytes(r2)     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r6.write(r2)     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            if (r14 == 0) goto L_0x01af
            android.graphics.drawable.BitmapDrawable r1 = (android.graphics.drawable.BitmapDrawable) r1     // Catch:{ all -> 0x0167 }
            android.graphics.Bitmap r14 = r1.getBitmap()     // Catch:{ all -> 0x0167 }
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch:{ all -> 0x0167 }
            java.io.File r2 = new java.io.File     // Catch:{ all -> 0x0167 }
            java.io.File r8 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ all -> 0x0167 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x0167 }
            r9.<init>()     // Catch:{ all -> 0x0167 }
            java.lang.String r0 = org.telegram.messenger.Utilities.MD5(r0)     // Catch:{ all -> 0x0167 }
            r9.append(r0)     // Catch:{ all -> 0x0167 }
            java.lang.String r0 = ".wp"
            r9.append(r0)     // Catch:{ all -> 0x0167 }
            java.lang.String r0 = r9.toString()     // Catch:{ all -> 0x0167 }
            r2.<init>(r8, r0)     // Catch:{ all -> 0x0167 }
            r1.<init>(r2)     // Catch:{ all -> 0x0167 }
            android.graphics.Bitmap$CompressFormat r0 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ all -> 0x0167 }
            r14.compress(r0, r5, r1)     // Catch:{ all -> 0x0167 }
            r1.close()     // Catch:{ all -> 0x0167 }
            goto L_0x01af
        L_0x0167:
            r14 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r14)     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            goto L_0x01af
        L_0x016c:
            boolean r14 = r1 instanceof android.graphics.drawable.BitmapDrawable     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            if (r14 == 0) goto L_0x01af
            r14 = r1
            android.graphics.drawable.BitmapDrawable r14 = (android.graphics.drawable.BitmapDrawable) r14     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            android.graphics.Bitmap r14 = r14.getBitmap()     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r0 = 2
            if (r14 == 0) goto L_0x01a6
            r2 = 4
            byte[] r8 = new byte[r2]     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r8[r3] = r5     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r9 = 80
            r8[r7] = r9     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r10 = 83
            r8[r0] = r10     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r10 = 3
            r11 = 10
            r8[r10] = r11     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r6.write(r8)     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            android.graphics.Bitmap$CompressFormat r8 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r14.compress(r8, r5, r6)     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r14 = 5
            byte[] r14 = new byte[r14]     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r14[r3] = r11     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r14[r7] = r5     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r14[r0] = r9     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r5 = 69
            r14[r10] = r5     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r14[r2] = r11     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r6.write(r14)     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
        L_0x01a6:
            if (r13 == 0) goto L_0x01af
            if (r15 != 0) goto L_0x01af
            wallpaper = r1     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            calcBackgroundColor(r1, r0)     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
        L_0x01af:
            if (r15 != 0) goto L_0x0200
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r14 = themesDict     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            java.lang.String r15 = r12.getKey()     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            java.lang.Object r14 = r14.get(r15)     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            if (r14 != 0) goto L_0x01d6
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r14 = themes     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r14.add(r12)     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r14 = themesDict     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            java.lang.String r15 = r12.getKey()     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r14.put(r15, r12)     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r14 = otherThemes     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r14.add(r12)     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            saveOtherThemes(r7)     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            sortThemes()     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
        L_0x01d6:
            currentTheme = r12     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            org.telegram.ui.ActionBar.Theme$ThemeInfo r14 = currentNightTheme     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            if (r12 == r14) goto L_0x01de
            currentDayTheme = r12     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
        L_0x01de:
            java.util.HashMap<java.lang.String, java.lang.Integer> r14 = defaultColors     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            if (r4 != r14) goto L_0x01ea
            java.util.HashMap<java.lang.String, java.lang.Integer> r14 = currentColorsNoAccent     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r14.clear()     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            refreshThemeColors()     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
        L_0x01ea:
            android.content.SharedPreferences r14 = org.telegram.messenger.MessagesController.getGlobalMainSettings()     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            android.content.SharedPreferences$Editor r14 = r14.edit()     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            java.lang.String r15 = "theme"
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = currentDayTheme     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            java.lang.String r0 = r0.getKey()     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r14.putString(r15, r0)     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
            r14.commit()     // Catch:{ Exception -> 0x0207, all -> 0x0204 }
        L_0x0200:
            r6.close()     // Catch:{ Exception -> 0x0216 }
            goto L_0x021a
        L_0x0204:
            r12 = move-exception
            r2 = r6
            goto L_0x022a
        L_0x0207:
            r14 = move-exception
            r2 = r6
            goto L_0x020d
        L_0x020a:
            r12 = move-exception
            goto L_0x022a
        L_0x020c:
            r14 = move-exception
        L_0x020d:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r14)     // Catch:{ all -> 0x020a }
            if (r2 == 0) goto L_0x021a
            r2.close()     // Catch:{ Exception -> 0x0216 }
            goto L_0x021a
        L_0x0216:
            r14 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r14)
        L_0x021a:
            if (r13 == 0) goto L_0x0229
            int r13 = r12.account
            org.telegram.messenger.MessagesController r13 = org.telegram.messenger.MessagesController.getInstance(r13)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r14 = r12.getAccent(r3)
            r13.saveThemeToServer(r12, r14)
        L_0x0229:
            return
        L_0x022a:
            if (r2 == 0) goto L_0x0234
            r2.close()     // Catch:{ Exception -> 0x0230 }
            goto L_0x0234
        L_0x0230:
            r13 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)
        L_0x0234:
            goto L_0x0236
        L_0x0235:
            throw r12
        L_0x0236:
            goto L_0x0235
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
                        ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_account_getTheme, new Theme$$ExternalSyntheticLambda10(accent, themeInfo, tLRPC$TL_theme));
                    }
                }
                i2++;
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00a6  */
    /* JADX WARNING: Removed duplicated region for block: B:44:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$checkCurrentRemoteTheme$3(org.telegram.tgnet.TLObject r7, org.telegram.ui.ActionBar.Theme.ThemeAccent r8, org.telegram.ui.ActionBar.Theme.ThemeInfo r9, org.telegram.tgnet.TLRPC$TL_theme r10) {
        /*
            int r0 = loadingCurrentTheme
            r1 = 1
            int r0 = r0 - r1
            loadingCurrentTheme = r0
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_theme
            r2 = 0
            if (r0 == 0) goto L_0x00a1
            org.telegram.tgnet.TLRPC$TL_theme r7 = (org.telegram.tgnet.TLRPC$TL_theme) r7
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ThemeSettings> r0 = r7.settings
            int r0 = r0.size()
            r3 = 0
            if (r0 <= 0) goto L_0x001f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ThemeSettings> r0 = r7.settings
            java.lang.Object r0 = r0.get(r2)
            org.telegram.tgnet.TLRPC$ThemeSettings r0 = (org.telegram.tgnet.TLRPC$ThemeSettings) r0
            goto L_0x0020
        L_0x001f:
            r0 = r3
        L_0x0020:
            if (r8 == 0) goto L_0x0088
            if (r0 == 0) goto L_0x0088
            boolean r7 = org.telegram.ui.ActionBar.Theme.ThemeInfo.accentEquals(r8, r0)
            if (r7 != 0) goto L_0x0075
            java.io.File r7 = r8.getPathToWallpaper()
            if (r7 == 0) goto L_0x0033
            r7.delete()
        L_0x0033:
            org.telegram.ui.ActionBar.Theme.ThemeInfo.fillAccentValues(r8, r0)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r7 = currentTheme
            if (r7 != r9) goto L_0x0070
            int r7 = r7.currentAccentId
            int r9 = r8.id
            if (r7 != r9) goto L_0x0070
            refreshThemeColors()
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext
            createChatResources(r7, r2)
            org.telegram.messenger.NotificationCenter r7 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r9 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            r10 = 4
            java.lang.Object[] r10 = new java.lang.Object[r10]
            org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = currentTheme
            r10[r2] = r4
            org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = currentNightTheme
            if (r5 != r4) goto L_0x005b
            r4 = 1
            goto L_0x005c
        L_0x005b:
            r4 = 0
        L_0x005c:
            java.lang.Boolean r4 = java.lang.Boolean.valueOf(r4)
            r10[r1] = r4
            r4 = 2
            r10[r4] = r3
            r3 = 3
            r4 = -1
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r10[r3] = r4
            r7.postNotificationName(r9, r10)
        L_0x0070:
            org.telegram.ui.ActionBar.Theme.PatternsLoader.createLoader(r1)
            r7 = 1
            goto L_0x0076
        L_0x0075:
            r7 = 0
        L_0x0076:
            org.telegram.tgnet.TLRPC$WallPaper r9 = r0.wallpaper
            if (r9 == 0) goto L_0x0083
            org.telegram.tgnet.TLRPC$WallPaperSettings r9 = r9.settings
            if (r9 == 0) goto L_0x0083
            boolean r9 = r9.motion
            if (r9 == 0) goto L_0x0083
            goto L_0x0084
        L_0x0083:
            r1 = 0
        L_0x0084:
            r8.patternMotion = r1
            r1 = r7
            goto L_0x00a2
        L_0x0088:
            org.telegram.tgnet.TLRPC$Document r0 = r7.document
            if (r0 == 0) goto L_0x00a1
            long r3 = r0.id
            org.telegram.tgnet.TLRPC$Document r10 = r10.document
            long r5 = r10.id
            int r10 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r10 == 0) goto L_0x00a1
            if (r8 == 0) goto L_0x009b
            r8.info = r7
            goto L_0x00a2
        L_0x009b:
            r9.info = r7
            r9.loadThemeDocument()
            goto L_0x00a2
        L_0x00a1:
            r1 = 0
        L_0x00a2:
            int r7 = loadingCurrentTheme
            if (r7 != 0) goto L_0x00b3
            long r7 = java.lang.System.currentTimeMillis()
            r9 = 1000(0x3e8, double:4.94E-321)
            long r7 = r7 / r9
            int r8 = (int) r7
            lastLoadingCurrentThemeTime = r8
            saveOtherThemes(r1)
        L_0x00b3:
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
            if (!MediaDataController.getInstance(i).defaultEmojiThemes.isEmpty()) {
                tLRPC$TL_account_getThemes.hash = remoteThemesHash[i];
            }
            if (BuildVars.LOGS_ENABLED) {
                Log.i("theme", "loading remote themes, hash " + tLRPC$TL_account_getThemes.hash);
            }
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_account_getThemes, new Theme$$ExternalSyntheticLambda9(i));
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0219  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x020d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$loadRemoteThemes$5(int r18, org.telegram.tgnet.TLObject r19) {
        /*
            r0 = r18
            r1 = r19
            boolean[] r2 = loadingRemoteThemes
            r3 = 0
            r2[r0] = r3
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_account_themes
            if (r2 == 0) goto L_0x0286
            org.telegram.tgnet.TLRPC$TL_account_themes r1 = (org.telegram.tgnet.TLRPC$TL_account_themes) r1
            long[] r2 = remoteThemesHash
            long r4 = r1.hash
            r2[r0] = r4
            int[] r2 = lastLoadingThemesTime
            long r4 = java.lang.System.currentTimeMillis()
            r6 = 1000(0x3e8, double:4.94E-321)
            long r4 = r4 / r6
            int r5 = (int) r4
            r2[r0] = r5
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r5 = themes
            int r5 = r5.size()
            r6 = 0
        L_0x0032:
            if (r6 >= r5) goto L_0x006e
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r7 = themes
            java.lang.Object r7 = r7.get(r6)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r7 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r7
            org.telegram.tgnet.TLRPC$TL_theme r8 = r7.info
            if (r8 == 0) goto L_0x0048
            int r8 = r7.account
            if (r8 != r0) goto L_0x0048
            r4.add(r7)
            goto L_0x006b
        L_0x0048:
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeAccent> r8 = r7.themeAccents
            if (r8 == 0) goto L_0x006b
            r8 = 0
        L_0x004d:
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeAccent> r9 = r7.themeAccents
            int r9 = r9.size()
            if (r8 >= r9) goto L_0x006b
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeAccent> r9 = r7.themeAccents
            java.lang.Object r9 = r9.get(r8)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r9 = (org.telegram.ui.ActionBar.Theme.ThemeAccent) r9
            org.telegram.tgnet.TLRPC$TL_theme r10 = r9.info
            if (r10 == 0) goto L_0x0068
            int r10 = r9.account
            if (r10 != r0) goto L_0x0068
            r4.add(r9)
        L_0x0068:
            int r8 = r8 + 1
            goto L_0x004d
        L_0x006b:
            int r6 = r6 + 1
            goto L_0x0032
        L_0x006e:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_theme> r5 = r1.themes
            int r5 = r5.size()
            r6 = 0
            r7 = 0
            r8 = 0
        L_0x0077:
            r13 = 4
            r14 = 1
            if (r6 >= r5) goto L_0x01b7
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_theme> r15 = r1.themes
            java.lang.Object r15 = r15.get(r6)
            org.telegram.tgnet.TLRPC$TL_theme r15 = (org.telegram.tgnet.TLRPC$TL_theme) r15
            boolean r10 = r15 instanceof org.telegram.tgnet.TLRPC$TL_theme
            if (r10 != 0) goto L_0x0089
            goto L_0x01b2
        L_0x0089:
            boolean r10 = r15.isDefault
            if (r10 == 0) goto L_0x0090
            r2.add(r15)
        L_0x0090:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ThemeSettings> r10 = r15.settings
            if (r10 == 0) goto L_0x0150
            int r10 = r10.size()
            if (r10 <= 0) goto L_0x0150
            r10 = 0
        L_0x009b:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ThemeSettings> r9 = r15.settings
            int r9 = r9.size()
            if (r10 >= r9) goto L_0x01b2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ThemeSettings> r9 = r15.settings
            java.lang.Object r9 = r9.get(r10)
            org.telegram.tgnet.TLRPC$ThemeSettings r9 = (org.telegram.tgnet.TLRPC$ThemeSettings) r9
            if (r9 == 0) goto L_0x014b
            java.lang.String r11 = getBaseThemeKey(r9)
            if (r11 != 0) goto L_0x00b5
            goto L_0x014b
        L_0x00b5:
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r12 = themesDict
            java.lang.Object r11 = r12.get(r11)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r11 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r11
            if (r11 == 0) goto L_0x014b
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeAccent> r12 = r11.themeAccents
            if (r12 != 0) goto L_0x00c5
            goto L_0x014b
        L_0x00c5:
            android.util.LongSparseArray<org.telegram.ui.ActionBar.Theme$ThemeAccent> r12 = r11.accentsByThemeId
            r17 = r4
            long r3 = r15.id
            java.lang.Object r3 = r12.get(r3)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r3 = (org.telegram.ui.ActionBar.Theme.ThemeAccent) r3
            if (r3 == 0) goto L_0x0134
            boolean r4 = org.telegram.ui.ActionBar.Theme.ThemeInfo.accentEquals(r3, r9)
            if (r4 != 0) goto L_0x011d
            java.io.File r4 = r3.getPathToWallpaper()
            if (r4 == 0) goto L_0x00e2
            r4.delete()
        L_0x00e2:
            org.telegram.ui.ActionBar.Theme.ThemeInfo.fillAccentValues(r3, r9)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = currentTheme
            if (r4 != r11) goto L_0x011b
            int r4 = r4.currentAccentId
            int r7 = r3.id
            if (r4 != r7) goto L_0x011b
            refreshThemeColors()
            org.telegram.messenger.NotificationCenter r4 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r7 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            java.lang.Object[] r8 = new java.lang.Object[r13]
            org.telegram.ui.ActionBar.Theme$ThemeInfo r11 = currentTheme
            r12 = 0
            r8[r12] = r11
            org.telegram.ui.ActionBar.Theme$ThemeInfo r12 = currentNightTheme
            if (r12 != r11) goto L_0x0105
            r11 = 1
            goto L_0x0106
        L_0x0105:
            r11 = 0
        L_0x0106:
            java.lang.Boolean r11 = java.lang.Boolean.valueOf(r11)
            r8[r14] = r11
            r11 = 0
            r12 = 2
            r8[r12] = r11
            r11 = -1
            java.lang.Integer r12 = java.lang.Integer.valueOf(r11)
            r11 = 3
            r8[r11] = r12
            r4.postNotificationName(r7, r8)
        L_0x011b:
            r7 = 1
            r8 = 1
        L_0x011d:
            org.telegram.tgnet.TLRPC$WallPaper r4 = r9.wallpaper
            if (r4 == 0) goto L_0x012b
            org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r4.settings
            if (r4 == 0) goto L_0x012b
            boolean r4 = r4.motion
            if (r4 == 0) goto L_0x012b
            r4 = 1
            goto L_0x012c
        L_0x012b:
            r4 = 0
        L_0x012c:
            r3.patternMotion = r4
            r4 = r17
            r4.remove(r3)
            goto L_0x0147
        L_0x0134:
            r4 = r17
            r3 = 0
            org.telegram.ui.ActionBar.Theme$ThemeAccent r9 = r11.createNewAccent(r15, r0, r3, r10)
            java.lang.String r3 = r9.patternSlug
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0146
            r3 = r9
            r7 = 1
            goto L_0x0147
        L_0x0146:
            r3 = r9
        L_0x0147:
            boolean r9 = r15.isDefault
            r3.isDefault = r9
        L_0x014b:
            int r10 = r10 + 1
            r3 = 0
            goto L_0x009b
        L_0x0150:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r9 = "remote"
            r3.append(r9)
            long r9 = r15.id
            r3.append(r9)
            java.lang.String r3 = r3.toString()
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r9 = themesDict
            java.lang.Object r9 = r9.get(r3)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r9
            if (r9 != 0) goto L_0x01a0
            org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = new org.telegram.ui.ActionBar.Theme$ThemeInfo
            r9.<init>()
            r9.account = r0
            java.io.File r8 = new java.io.File
            java.io.File r10 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r3)
            java.lang.String r3 = ".attheme"
            r11.append(r3)
            java.lang.String r3 = r11.toString()
            r8.<init>(r10, r3)
            java.lang.String r3 = r8.getAbsolutePath()
            r9.pathToFile = r3
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r3 = themes
            r3.add(r9)
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r3 = otherThemes
            r3.add(r9)
            r8 = 1
            goto L_0x01a3
        L_0x01a0:
            r4.remove(r9)
        L_0x01a3:
            java.lang.String r3 = r15.title
            r9.name = r3
            r9.info = r15
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r3 = themesDict
            java.lang.String r10 = r9.getKey()
            r3.put(r10, r9)
        L_0x01b2:
            int r6 = r6 + 1
            r3 = 0
            goto L_0x0077
        L_0x01b7:
            int r1 = r4.size()
            r12 = 0
        L_0x01bc:
            if (r12 >= r1) goto L_0x0266
            java.lang.Object r3 = r4.get(r12)
            boolean r5 = r3 instanceof org.telegram.ui.ActionBar.Theme.ThemeInfo
            if (r5 == 0) goto L_0x021b
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r3
            r3.removeObservers()
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r5 = otherThemes
            r5.remove(r3)
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r5 = themesDict
            java.lang.String r6 = r3.name
            r5.remove(r6)
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r5 = r3.overrideWallpaper
            if (r5 == 0) goto L_0x01de
            r5.delete()
        L_0x01de:
            java.util.ArrayList<org.telegram.ui.ActionBar.Theme$ThemeInfo> r5 = themes
            r5.remove(r3)
            java.io.File r5 = new java.io.File
            java.lang.String r6 = r3.pathToFile
            r5.<init>(r6)
            r5.delete()
            org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = currentDayTheme
            if (r5 != r3) goto L_0x01f6
            org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = defaultTheme
            currentDayTheme = r5
            goto L_0x0208
        L_0x01f6:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = currentNightTheme
            if (r5 != r3) goto L_0x0208
            java.util.HashMap<java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeInfo> r5 = themesDict
            java.lang.String r6 = "Dark Blue"
            java.lang.Object r5 = r5.get(r6)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r5
            currentNightTheme = r5
            r5 = 1
            goto L_0x0209
        L_0x0208:
            r5 = 0
        L_0x0209:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r6 = currentTheme
            if (r6 != r3) goto L_0x0219
            if (r5 == 0) goto L_0x0212
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = currentNightTheme
            goto L_0x0214
        L_0x0212:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = currentDayTheme
        L_0x0214:
            r6 = 0
            applyTheme(r3, r14, r6, r5)
            goto L_0x025d
        L_0x0219:
            r6 = 0
            goto L_0x025d
        L_0x021b:
            r6 = 0
            boolean r5 = r3 instanceof org.telegram.ui.ActionBar.Theme.ThemeAccent
            if (r5 == 0) goto L_0x025d
            org.telegram.ui.ActionBar.Theme$ThemeAccent r3 = (org.telegram.ui.ActionBar.Theme.ThemeAccent) r3
            org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = r3.parentTheme
            boolean r5 = deleteThemeAccent(r5, r3, r6)
            if (r5 == 0) goto L_0x025d
            org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = currentTheme
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = r3.parentTheme
            if (r5 != r3) goto L_0x025d
            refreshThemeColors()
            org.telegram.messenger.NotificationCenter r3 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r5 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            java.lang.Object[] r9 = new java.lang.Object[r13]
            org.telegram.ui.ActionBar.Theme$ThemeInfo r10 = currentTheme
            r9[r6] = r10
            org.telegram.ui.ActionBar.Theme$ThemeInfo r6 = currentNightTheme
            if (r6 != r10) goto L_0x0245
            r6 = 1
            goto L_0x0246
        L_0x0245:
            r6 = 0
        L_0x0246:
            java.lang.Boolean r6 = java.lang.Boolean.valueOf(r6)
            r9[r14] = r6
            r6 = 0
            r10 = 2
            r9[r10] = r6
            r11 = -1
            java.lang.Integer r15 = java.lang.Integer.valueOf(r11)
            r16 = 3
            r9[r16] = r15
            r3.postNotificationName(r5, r9)
            goto L_0x0262
        L_0x025d:
            r6 = 0
            r10 = 2
            r11 = -1
            r16 = 3
        L_0x0262:
            int r12 = r12 + 1
            goto L_0x01bc
        L_0x0266:
            saveOtherThemes(r14)
            sortThemes()
            if (r8 == 0) goto L_0x027a
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r3 = org.telegram.messenger.NotificationCenter.themeListUpdated
            r4 = 0
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r1.postNotificationName(r3, r4)
        L_0x027a:
            if (r7 == 0) goto L_0x027f
            org.telegram.ui.ActionBar.Theme.PatternsLoader.createLoader(r14)
        L_0x027f:
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r18)
            r1.generateEmojiPreviewThemes(r2, r0)
        L_0x0286:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.lambda$loadRemoteThemes$5(int, org.telegram.tgnet.TLObject):void");
    }

    public static String getBaseThemeKey(TLRPC$ThemeSettings tLRPC$ThemeSettings) {
        TLRPC$BaseTheme tLRPC$BaseTheme = tLRPC$ThemeSettings.base_theme;
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
            TLRPC$ThemeSettings tLRPC$ThemeSettings = tLRPC$TL_theme.settings.size() > 0 ? tLRPC$TL_theme.settings.get(0) : null;
            if (tLRPC$ThemeSettings != null) {
                if (themeInfo == null) {
                    String baseThemeKey = getBaseThemeKey(tLRPC$ThemeSettings);
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
                    if (!ThemeInfo.accentEquals(themeAccent, tLRPC$ThemeSettings)) {
                        File pathToWallpaper = themeAccent.getPathToWallpaper();
                        if (pathToWallpaper != null) {
                            pathToWallpaper.delete();
                        }
                        ThemeInfo.fillAccentValues(themeAccent, tLRPC$ThemeSettings);
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
                    TLRPC$WallPaper tLRPC$WallPaper = tLRPC$ThemeSettings.wallpaper;
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v60, resolved type: android.graphics.drawable.BitmapDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v64, resolved type: android.graphics.drawable.ColorDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v110, resolved type: org.telegram.ui.Components.MotionBackgroundDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v4, resolved type: org.telegram.ui.Components.MotionBackgroundDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v111, resolved type: org.telegram.ui.Components.MotionBackgroundDrawable} */
    /* JADX WARNING: type inference failed for: r0v53, types: [android.graphics.drawable.Drawable] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x01e6 A[Catch:{ all -> 0x04d1, Exception -> 0x04b4, all -> 0x0680 }] */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x024b A[Catch:{ all -> 0x04d1, Exception -> 0x04b4, all -> 0x0680 }] */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x028a A[SYNTHETIC, Splitter:B:133:0x028a] */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x034d A[Catch:{ all -> 0x04d1, Exception -> 0x04b4, all -> 0x0680 }] */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x04b0 A[SYNTHETIC, Splitter:B:217:0x04b0] */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x04c7 A[SYNTHETIC, Splitter:B:230:0x04c7] */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x04e4 A[Catch:{ all -> 0x04d1, Exception -> 0x04b4, all -> 0x0680 }] */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x0502 A[Catch:{ all -> 0x04d1, Exception -> 0x04b4, all -> 0x0680 }] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00b2 A[Catch:{ all -> 0x04d1, Exception -> 0x04b4, all -> 0x0680 }] */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x052c A[Catch:{ all -> 0x04d1, Exception -> 0x04b4, all -> 0x0680 }] */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0545 A[Catch:{ all -> 0x04d1, Exception -> 0x04b4, all -> 0x0680 }] */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x05f7 A[Catch:{ all -> 0x04d1, Exception -> 0x04b4, all -> 0x0680 }] */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x00b7 A[Catch:{ all -> 0x04d1, Exception -> 0x04b4, all -> 0x0680 }] */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x0619 A[Catch:{ all -> 0x04d1, Exception -> 0x04b4, all -> 0x0680 }] */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00ba A[Catch:{ all -> 0x04d1, Exception -> 0x04b4, all -> 0x0680 }] */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00be A[Catch:{ all -> 0x04d1, Exception -> 0x04b4, all -> 0x0680 }] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00cd A[Catch:{ all -> 0x04d1, Exception -> 0x04b4, all -> 0x0680 }] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00d1 A[Catch:{ all -> 0x04d1, Exception -> 0x04b4, all -> 0x0680 }] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00d6 A[Catch:{ all -> 0x04d1, Exception -> 0x04b4, all -> 0x0680 }] */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00d9 A[Catch:{ all -> 0x04d1, Exception -> 0x04b4, all -> 0x0680 }] */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00dd A[Catch:{ all -> 0x04d1, Exception -> 0x04b4, all -> 0x0680 }] */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00f3 A[Catch:{ all -> 0x04d1, Exception -> 0x04b4, all -> 0x0680 }] */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00f7 A[Catch:{ all -> 0x04d1, Exception -> 0x04b4, all -> 0x0680 }] */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00fc A[Catch:{ all -> 0x04d1, Exception -> 0x04b4, all -> 0x0680 }] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00ff A[Catch:{ all -> 0x04d1, Exception -> 0x04b4, all -> 0x0680 }] */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0104 A[Catch:{ all -> 0x04d1, Exception -> 0x04b4, all -> 0x0680 }] */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0108 A[ADDED_TO_REGION, Catch:{ all -> 0x04d1, Exception -> 0x04b4, all -> 0x0680 }] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0118 A[Catch:{ all -> 0x04d1, Exception -> 0x04b4, all -> 0x0680 }] */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0123 A[SYNTHETIC, Splitter:B:68:0x0123] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String createThemePreviewImage(java.lang.String r37, java.lang.String r38, org.telegram.ui.ActionBar.Theme.ThemeAccent r39) {
        /*
            r1 = r37
            r2 = r38
            r3 = r39
            r4 = 0
            r5 = 1
            java.lang.String[] r6 = new java.lang.String[r5]     // Catch:{ all -> 0x0680 }
            java.io.File r0 = new java.io.File     // Catch:{ all -> 0x0680 }
            r0.<init>(r1)     // Catch:{ all -> 0x0680 }
            java.util.HashMap r7 = getThemeFileValues(r0, r4, r6)     // Catch:{ all -> 0x0680 }
            if (r3 == 0) goto L_0x001a
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = r3.parentTheme     // Catch:{ all -> 0x0680 }
            checkIsDark(r7, r0)     // Catch:{ all -> 0x0680 }
        L_0x001a:
            java.lang.String r0 = "wallpaperFileOffset"
            java.lang.Object r0 = r7.get(r0)     // Catch:{ all -> 0x0680 }
            r8 = r0
            java.lang.Integer r8 = (java.lang.Integer) r8     // Catch:{ all -> 0x0680 }
            r0 = 560(0x230, float:7.85E-43)
            r9 = 678(0x2a6, float:9.5E-43)
            android.graphics.Bitmap$Config r10 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0680 }
            android.graphics.Bitmap r9 = org.telegram.messenger.Bitmaps.createBitmap(r0, r9, r10)     // Catch:{ all -> 0x0680 }
            android.graphics.Canvas r15 = new android.graphics.Canvas     // Catch:{ all -> 0x0680 }
            r15.<init>(r9)     // Catch:{ all -> 0x0680 }
            android.graphics.Paint r14 = new android.graphics.Paint     // Catch:{ all -> 0x0680 }
            r14.<init>()     // Catch:{ all -> 0x0680 }
            java.lang.String r0 = "actionBarDefault"
            int r10 = getPreviewColor(r7, r0)     // Catch:{ all -> 0x0680 }
            java.lang.String r0 = "actionBarDefaultIcon"
            int r11 = getPreviewColor(r7, r0)     // Catch:{ all -> 0x0680 }
            java.lang.String r0 = "chat_messagePanelBackground"
            int r13 = getPreviewColor(r7, r0)     // Catch:{ all -> 0x0680 }
            java.lang.String r0 = "chat_messagePanelIcons"
            int r12 = getPreviewColor(r7, r0)     // Catch:{ all -> 0x0680 }
            java.lang.String r0 = "chat_inBubble"
            int r16 = getPreviewColor(r7, r0)     // Catch:{ all -> 0x0680 }
            java.lang.String r0 = "chat_outBubble"
            int r17 = getPreviewColor(r7, r0)     // Catch:{ all -> 0x0680 }
            java.lang.String r0 = "chat_outBubbleGradient"
            java.lang.Object r0 = r7.get(r0)     // Catch:{ all -> 0x0680 }
            java.lang.Integer r0 = (java.lang.Integer) r0     // Catch:{ all -> 0x0680 }
            java.lang.String r0 = "chat_wallpaper"
            java.lang.Object r0 = r7.get(r0)     // Catch:{ all -> 0x0680 }
            java.lang.Integer r0 = (java.lang.Integer) r0     // Catch:{ all -> 0x0680 }
            java.lang.String r4 = "chat_wallpaper_gradient_to"
            java.lang.Object r4 = r7.get(r4)     // Catch:{ all -> 0x0680 }
            java.lang.Integer r4 = (java.lang.Integer) r4     // Catch:{ all -> 0x0680 }
            java.lang.String r5 = "key_chat_wallpaper_gradient_to2"
            java.lang.Object r5 = r7.get(r5)     // Catch:{ all -> 0x0680 }
            java.lang.Integer r5 = (java.lang.Integer) r5     // Catch:{ all -> 0x0680 }
            r18 = r13
            java.lang.String r13 = "key_chat_wallpaper_gradient_to3"
            java.lang.Object r13 = r7.get(r13)     // Catch:{ all -> 0x0680 }
            java.lang.Integer r13 = (java.lang.Integer) r13     // Catch:{ all -> 0x0680 }
            r19 = r10
            if (r0 == 0) goto L_0x008e
            int r0 = r0.intValue()     // Catch:{ all -> 0x0680 }
            goto L_0x008f
        L_0x008e:
            r0 = 0
        L_0x008f:
            if (r3 == 0) goto L_0x0097
            r20 = r11
            long r10 = r3.backgroundOverrideColor     // Catch:{ all -> 0x0680 }
            int r11 = (int) r10     // Catch:{ all -> 0x0680 }
            goto L_0x009a
        L_0x0097:
            r20 = r11
            r11 = 0
        L_0x009a:
            r22 = 0
            if (r11 != 0) goto L_0x00a9
            if (r3 == 0) goto L_0x00a9
            r10 = r0
            long r0 = r3.backgroundOverrideColor     // Catch:{ all -> 0x0680 }
            int r24 = (r0 > r22 ? 1 : (r0 == r22 ? 0 : -1))
            if (r24 == 0) goto L_0x00aa
            r1 = 0
            goto L_0x00b0
        L_0x00a9:
            r10 = r0
        L_0x00aa:
            if (r11 == 0) goto L_0x00ae
            r0 = r11
            goto L_0x00af
        L_0x00ae:
            r0 = r10
        L_0x00af:
            r1 = r0
        L_0x00b0:
            if (r4 == 0) goto L_0x00b7
            int r0 = r4.intValue()     // Catch:{ all -> 0x0680 }
            goto L_0x00b8
        L_0x00b7:
            r0 = 0
        L_0x00b8:
            if (r3 == 0) goto L_0x00be
            long r10 = r3.backgroundGradientOverrideColor1     // Catch:{ all -> 0x0680 }
            int r4 = (int) r10     // Catch:{ all -> 0x0680 }
            goto L_0x00bf
        L_0x00be:
            r4 = 0
        L_0x00bf:
            if (r4 != 0) goto L_0x00cb
            if (r3 == 0) goto L_0x00cb
            long r10 = r3.backgroundGradientOverrideColor1     // Catch:{ all -> 0x0680 }
            int r24 = (r10 > r22 ? 1 : (r10 == r22 ? 0 : -1))
            if (r24 == 0) goto L_0x00cb
            r4 = 0
            goto L_0x00cf
        L_0x00cb:
            if (r4 == 0) goto L_0x00ce
            r0 = r4
        L_0x00ce:
            r4 = r0
        L_0x00cf:
            if (r5 == 0) goto L_0x00d6
            int r0 = r5.intValue()     // Catch:{ all -> 0x0680 }
            goto L_0x00d7
        L_0x00d6:
            r0 = 0
        L_0x00d7:
            if (r3 == 0) goto L_0x00dd
            long r10 = r3.backgroundGradientOverrideColor2     // Catch:{ all -> 0x0680 }
            int r11 = (int) r10     // Catch:{ all -> 0x0680 }
            goto L_0x00de
        L_0x00dd:
            r11 = 0
        L_0x00de:
            if (r11 != 0) goto L_0x00ee
            if (r3 == 0) goto L_0x00ee
            r24 = r0
            r10 = r1
            long r0 = r3.backgroundGradientOverrideColor2     // Catch:{ all -> 0x0680 }
            int r25 = (r0 > r22 ? 1 : (r0 == r22 ? 0 : -1))
            if (r25 == 0) goto L_0x00f1
            r24 = 0
            goto L_0x00f5
        L_0x00ee:
            r24 = r0
            r10 = r1
        L_0x00f1:
            if (r11 == 0) goto L_0x00f5
            r24 = r11
        L_0x00f5:
            if (r13 == 0) goto L_0x00fc
            int r0 = r13.intValue()     // Catch:{ all -> 0x0680 }
            goto L_0x00fd
        L_0x00fc:
            r0 = 0
        L_0x00fd:
            if (r3 == 0) goto L_0x0104
            r11 = r0
            long r0 = r3.backgroundGradientOverrideColor3     // Catch:{ all -> 0x0680 }
            int r1 = (int) r0     // Catch:{ all -> 0x0680 }
            goto L_0x0106
        L_0x0104:
            r11 = r0
            r1 = 0
        L_0x0106:
            if (r1 != 0) goto L_0x0114
            if (r3 == 0) goto L_0x0114
            r13 = r10
            r0 = r11
            long r10 = r3.backgroundGradientOverrideColor3     // Catch:{ all -> 0x0680 }
            int r25 = (r10 > r22 ? 1 : (r10 == r22 ? 0 : -1))
            if (r25 == 0) goto L_0x0116
            r1 = 0
            goto L_0x011a
        L_0x0114:
            r13 = r10
            r0 = r11
        L_0x0116:
            if (r1 == 0) goto L_0x0119
            r0 = r1
        L_0x0119:
            r1 = r0
        L_0x011a:
            r10 = 0
            r0 = r6[r10]     // Catch:{ all -> 0x0680 }
            boolean r0 = android.text.TextUtils.isEmpty(r0)     // Catch:{ all -> 0x0680 }
            if (r0 != 0) goto L_0x01e6
            r0 = r6[r10]     // Catch:{ Exception -> 0x01d8 }
            android.net.Uri r0 = android.net.Uri.parse(r0)     // Catch:{ Exception -> 0x01d8 }
            java.lang.String r10 = "bg_color"
            java.lang.String r0 = r0.getQueryParameter(r10)     // Catch:{ Exception -> 0x01d8 }
            if (r3 == 0) goto L_0x01d0
            boolean r10 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x01d8 }
            if (r10 != 0) goto L_0x01d0
            r10 = 6
            r25 = r1
            r11 = 0
            java.lang.String r1 = r0.substring(r11, r10)     // Catch:{ Exception -> 0x01ce }
            r11 = 16
            int r1 = java.lang.Integer.parseInt(r1, r11)     // Catch:{ Exception -> 0x01ce }
            r13 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r1 = r1 | r13
            r26 = r14
            long r13 = (long) r1
            r3.backgroundOverrideColor = r13     // Catch:{ Exception -> 0x01ca }
            int r13 = r0.length()     // Catch:{ Exception -> 0x01ca }
            r14 = 13
            if (r13 < r14) goto L_0x016e
            char r10 = r0.charAt(r10)     // Catch:{ Exception -> 0x01ca }
            boolean r10 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r10)     // Catch:{ Exception -> 0x01ca }
            if (r10 == 0) goto L_0x016e
            r10 = 7
            java.lang.String r10 = r0.substring(r10, r14)     // Catch:{ Exception -> 0x01ca }
            int r4 = java.lang.Integer.parseInt(r10, r11)     // Catch:{ Exception -> 0x01ca }
            r10 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r4 = r4 | r10
            long r13 = (long) r4     // Catch:{ Exception -> 0x01ca }
            r3.backgroundGradientOverrideColor1 = r13     // Catch:{ Exception -> 0x01ca }
        L_0x016e:
            int r10 = r0.length()     // Catch:{ Exception -> 0x01ca }
            r13 = 20
            if (r10 < r13) goto L_0x019b
            r14 = 13
            char r10 = r0.charAt(r14)     // Catch:{ Exception -> 0x01ca }
            boolean r10 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r10)     // Catch:{ Exception -> 0x01ca }
            if (r10 == 0) goto L_0x019b
            r10 = 14
            java.lang.String r10 = r0.substring(r10, r13)     // Catch:{ Exception -> 0x01ca }
            int r10 = java.lang.Integer.parseInt(r10, r11)     // Catch:{ Exception -> 0x01ca }
            r13 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r10 = r10 | r13
            r22 = r15
            long r14 = (long) r10
            r3.backgroundGradientOverrideColor2 = r14     // Catch:{ Exception -> 0x0197 }
            r24 = r10
            goto L_0x019d
        L_0x0197:
            r0 = move-exception
            r24 = r10
            goto L_0x01e0
        L_0x019b:
            r22 = r15
        L_0x019d:
            int r10 = r0.length()     // Catch:{ Exception -> 0x01c8 }
            r13 = 27
            if (r10 != r13) goto L_0x01e3
            r14 = 20
            char r10 = r0.charAt(r14)     // Catch:{ Exception -> 0x01c8 }
            boolean r10 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r10)     // Catch:{ Exception -> 0x01c8 }
            if (r10 == 0) goto L_0x01e3
            r10 = 21
            java.lang.String r0 = r0.substring(r10)     // Catch:{ Exception -> 0x01c8 }
            int r0 = java.lang.Integer.parseInt(r0, r11)     // Catch:{ Exception -> 0x01c8 }
            r10 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r10 = r10 | r0
            long r14 = (long) r10
            r3.backgroundGradientOverrideColor3 = r14     // Catch:{ Exception -> 0x01c4 }
            r25 = r10
            goto L_0x01e3
        L_0x01c4:
            r0 = move-exception
            r25 = r10
            goto L_0x01e0
        L_0x01c8:
            r0 = move-exception
            goto L_0x01e0
        L_0x01ca:
            r0 = move-exception
            r22 = r15
            goto L_0x01e0
        L_0x01ce:
            r0 = move-exception
            goto L_0x01db
        L_0x01d0:
            r25 = r1
            r26 = r14
            r22 = r15
            r1 = r13
            goto L_0x01e3
        L_0x01d8:
            r0 = move-exception
            r25 = r1
        L_0x01db:
            r26 = r14
            r22 = r15
            r1 = r13
        L_0x01e0:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0680 }
        L_0x01e3:
            r31 = r4
            goto L_0x01ef
        L_0x01e6:
            r25 = r1
            r26 = r14
            r22 = r15
            r31 = r4
            r1 = r13
        L_0x01ef:
            r32 = r24
            r33 = r25
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0680 }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ all -> 0x0680 }
            r4 = 2131166094(0x7var_e, float:1.7946424E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r4)     // Catch:{ all -> 0x0680 }
            android.graphics.drawable.Drawable r4 = r0.mutate()     // Catch:{ all -> 0x0680 }
            r10 = r20
            setDrawableColor(r4, r10)     // Catch:{ all -> 0x0680 }
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0680 }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ all -> 0x0680 }
            r11 = 2131166096(0x7var_, float:1.7946428E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r11)     // Catch:{ all -> 0x0680 }
            android.graphics.drawable.Drawable r15 = r0.mutate()     // Catch:{ all -> 0x0680 }
            setDrawableColor(r15, r10)     // Catch:{ all -> 0x0680 }
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0680 }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ all -> 0x0680 }
            r10 = 2131166098(0x7var_, float:1.7946432E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r10)     // Catch:{ all -> 0x0680 }
            android.graphics.drawable.Drawable r14 = r0.mutate()     // Catch:{ all -> 0x0680 }
            setDrawableColor(r14, r12)     // Catch:{ all -> 0x0680 }
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0680 }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ all -> 0x0680 }
            r10 = 2131166097(0x7var_, float:1.794643E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r10)     // Catch:{ all -> 0x0680 }
            android.graphics.drawable.Drawable r13 = r0.mutate()     // Catch:{ all -> 0x0680 }
            setDrawableColor(r13, r12)     // Catch:{ all -> 0x0680 }
            r12 = 2
            org.telegram.ui.ActionBar.Theme$MessageDrawable[] r11 = new org.telegram.ui.ActionBar.Theme.MessageDrawable[r12]     // Catch:{ all -> 0x0680 }
            r0 = 0
        L_0x0249:
            if (r0 >= r12) goto L_0x0272
            org.telegram.ui.ActionBar.Theme$10 r10 = new org.telegram.ui.ActionBar.Theme$10     // Catch:{ all -> 0x0680 }
            r12 = 1
            r24 = r13
            r25 = r14
            if (r0 != r12) goto L_0x0256
            r12 = 1
            goto L_0x0257
        L_0x0256:
            r12 = 0
        L_0x0257:
            r13 = 2
            r14 = 0
            r10.<init>(r13, r12, r14, r7)     // Catch:{ all -> 0x0680 }
            r11[r0] = r10     // Catch:{ all -> 0x0680 }
            r10 = r11[r0]     // Catch:{ all -> 0x0680 }
            if (r0 != 0) goto L_0x0265
            r12 = r16
            goto L_0x0267
        L_0x0265:
            r12 = r17
        L_0x0267:
            setDrawableColor(r10, r12)     // Catch:{ all -> 0x0680 }
            int r0 = r0 + 1
            r13 = r24
            r14 = r25
            r12 = 2
            goto L_0x0249
        L_0x0272:
            r24 = r13
            r25 = r14
            android.graphics.RectF r0 = new android.graphics.RectF     // Catch:{ all -> 0x0680 }
            r0.<init>()     // Catch:{ all -> 0x0680 }
            r10 = 80
            r12 = 1065353216(0x3var_, float:1.0)
            r13 = 1073741824(0x40000000, float:2.0)
            r14 = 0
            r16 = 1141637120(0x440CLASSNAME, float:560.0)
            r17 = r11
            r11 = 120(0x78, float:1.68E-43)
            if (r2 == 0) goto L_0x034d
            android.graphics.BitmapFactory$Options r5 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0344 }
            r5.<init>()     // Catch:{ all -> 0x0344 }
            r6 = 1
            r5.inJustDecodeBounds = r6     // Catch:{ all -> 0x0344 }
            android.graphics.BitmapFactory.decodeFile(r2, r5)     // Catch:{ all -> 0x0344 }
            int r6 = r5.outWidth     // Catch:{ all -> 0x0344 }
            if (r6 <= 0) goto L_0x033b
            int r7 = r5.outHeight     // Catch:{ all -> 0x0344 }
            if (r7 <= 0) goto L_0x033b
            float r6 = (float) r6     // Catch:{ all -> 0x0344 }
            float r6 = r6 / r16
            float r7 = (float) r7     // Catch:{ all -> 0x0344 }
            float r7 = r7 / r16
            float r6 = java.lang.Math.min(r6, r7)     // Catch:{ all -> 0x0344 }
            r7 = 1
            r5.inSampleSize = r7     // Catch:{ all -> 0x0344 }
            int r7 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1))
            if (r7 <= 0) goto L_0x02ba
        L_0x02ae:
            int r7 = r5.inSampleSize     // Catch:{ all -> 0x0344 }
            r8 = 2
            int r7 = r7 * 2
            r5.inSampleSize = r7     // Catch:{ all -> 0x0344 }
            float r7 = (float) r7     // Catch:{ all -> 0x0344 }
            int r7 = (r7 > r6 ? 1 : (r7 == r6 ? 0 : -1))
            if (r7 < 0) goto L_0x02ae
        L_0x02ba:
            r6 = 0
            r5.inJustDecodeBounds = r6     // Catch:{ all -> 0x0344 }
            android.graphics.Bitmap r2 = android.graphics.BitmapFactory.decodeFile(r2, r5)     // Catch:{ all -> 0x0344 }
            if (r2 == 0) goto L_0x033b
            if (r32 == 0) goto L_0x02ee
            if (r3 == 0) goto L_0x02ee
            org.telegram.ui.Components.MotionBackgroundDrawable r0 = new org.telegram.ui.Components.MotionBackgroundDrawable     // Catch:{ all -> 0x0344 }
            r34 = 1
            r29 = r0
            r30 = r1
            r29.<init>(r30, r31, r32, r33, r34)     // Catch:{ all -> 0x0344 }
            float r1 = r3.patternIntensity     // Catch:{ all -> 0x0344 }
            r3 = 1120403456(0x42CLASSNAME, float:100.0)
            float r1 = r1 * r3
            int r1 = (int) r1     // Catch:{ all -> 0x0344 }
            r0.setPatternBitmap(r1, r2)     // Catch:{ all -> 0x0344 }
            int r1 = r9.getWidth()     // Catch:{ all -> 0x0344 }
            int r2 = r9.getHeight()     // Catch:{ all -> 0x0344 }
            r3 = 0
            r0.setBounds(r3, r3, r1, r2)     // Catch:{ all -> 0x0344 }
            r3 = r22
            r0.draw(r3)     // Catch:{ all -> 0x0339 }
            goto L_0x0337
        L_0x02ee:
            r3 = r22
            android.graphics.Paint r1 = new android.graphics.Paint     // Catch:{ all -> 0x0339 }
            r1.<init>()     // Catch:{ all -> 0x0339 }
            r5 = 1
            r1.setFilterBitmap(r5)     // Catch:{ all -> 0x0339 }
            int r5 = r2.getWidth()     // Catch:{ all -> 0x0339 }
            float r5 = (float) r5     // Catch:{ all -> 0x0339 }
            float r5 = r5 / r16
            int r6 = r2.getHeight()     // Catch:{ all -> 0x0339 }
            float r6 = (float) r6     // Catch:{ all -> 0x0339 }
            float r6 = r6 / r16
            float r5 = java.lang.Math.min(r5, r6)     // Catch:{ all -> 0x0339 }
            int r6 = r2.getWidth()     // Catch:{ all -> 0x0339 }
            float r6 = (float) r6     // Catch:{ all -> 0x0339 }
            float r6 = r6 / r5
            int r7 = r2.getHeight()     // Catch:{ all -> 0x0339 }
            float r7 = (float) r7     // Catch:{ all -> 0x0339 }
            float r7 = r7 / r5
            r0.set(r14, r14, r6, r7)     // Catch:{ all -> 0x0339 }
            int r5 = r9.getWidth()     // Catch:{ all -> 0x0339 }
            float r5 = (float) r5     // Catch:{ all -> 0x0339 }
            float r6 = r0.width()     // Catch:{ all -> 0x0339 }
            float r5 = r5 - r6
            float r5 = r5 / r13
            int r6 = r9.getHeight()     // Catch:{ all -> 0x0339 }
            float r6 = (float) r6     // Catch:{ all -> 0x0339 }
            float r7 = r0.height()     // Catch:{ all -> 0x0339 }
            float r6 = r6 - r7
            float r6 = r6 / r13
            r0.offset(r5, r6)     // Catch:{ all -> 0x0339 }
            r5 = 0
            r3.drawBitmap(r2, r5, r0, r1)     // Catch:{ all -> 0x0339 }
        L_0x0337:
            r0 = 1
            goto L_0x033e
        L_0x0339:
            r0 = move-exception
            goto L_0x0347
        L_0x033b:
            r3 = r22
            r0 = 0
        L_0x033e:
            r1 = r0
            r0 = 80
        L_0x0341:
            r12 = 2
            goto L_0x04e2
        L_0x0344:
            r0 = move-exception
            r3 = r22
        L_0x0347:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0680 }
        L_0x034a:
            r12 = 2
            goto L_0x04df
        L_0x034d:
            r3 = r22
            if (r1 == 0) goto L_0x03ae
            if (r31 != 0) goto L_0x0359
            android.graphics.drawable.ColorDrawable r0 = new android.graphics.drawable.ColorDrawable     // Catch:{ all -> 0x0680 }
            r0.<init>(r1)     // Catch:{ all -> 0x0680 }
            goto L_0x039b
        L_0x0359:
            if (r32 == 0) goto L_0x0367
            org.telegram.ui.Components.MotionBackgroundDrawable r0 = new org.telegram.ui.Components.MotionBackgroundDrawable     // Catch:{ all -> 0x0680 }
            r34 = 1
            r29 = r0
            r30 = r1
            r29.<init>(r30, r31, r32, r33, r34)     // Catch:{ all -> 0x0680 }
            goto L_0x039b
        L_0x0367:
            java.lang.String r0 = "chat_wallpaper_gradient_rotation"
            java.lang.Object r0 = r7.get(r0)     // Catch:{ all -> 0x0680 }
            java.lang.Integer r0 = (java.lang.Integer) r0     // Catch:{ all -> 0x0680 }
            if (r0 != 0) goto L_0x0377
            r0 = 45
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x0680 }
        L_0x0377:
            if (r5 != 0) goto L_0x037b
            r2 = 0
            goto L_0x037f
        L_0x037b:
            int r2 = r5.intValue()     // Catch:{ all -> 0x0680 }
        L_0x037f:
            r5 = 2
            int[] r6 = new int[r5]     // Catch:{ all -> 0x0680 }
            r5 = 0
            r6[r5] = r1     // Catch:{ all -> 0x0680 }
            r1 = 1
            r6[r1] = r2     // Catch:{ all -> 0x0680 }
            int r0 = r0.intValue()     // Catch:{ all -> 0x0680 }
            int r1 = r9.getWidth()     // Catch:{ all -> 0x0680 }
            int r2 = r9.getHeight()     // Catch:{ all -> 0x0680 }
            int r2 = r2 - r11
            android.graphics.drawable.BitmapDrawable r0 = org.telegram.ui.Components.BackgroundGradientDrawable.createDitheredGradientBitmapDrawable((int) r0, (int[]) r6, (int) r1, (int) r2)     // Catch:{ all -> 0x0680 }
            r10 = 90
        L_0x039b:
            int r1 = r9.getWidth()     // Catch:{ all -> 0x0680 }
            int r2 = r9.getHeight()     // Catch:{ all -> 0x0680 }
            int r2 = r2 - r11
            r5 = 0
            r0.setBounds(r5, r11, r1, r2)     // Catch:{ all -> 0x0680 }
            r0.draw(r3)     // Catch:{ all -> 0x0680 }
            r0 = r10
            r1 = 1
            goto L_0x0341
        L_0x03ae:
            if (r8 == 0) goto L_0x03b6
            int r1 = r8.intValue()     // Catch:{ all -> 0x0680 }
            if (r1 >= 0) goto L_0x03bf
        L_0x03b6:
            r1 = 0
            r2 = r6[r1]     // Catch:{ all -> 0x0680 }
            boolean r1 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0680 }
            if (r1 != 0) goto L_0x034a
        L_0x03bf:
            android.graphics.BitmapFactory$Options r1 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x04bf }
            r1.<init>()     // Catch:{ all -> 0x04bf }
            r2 = 1
            r1.inJustDecodeBounds = r2     // Catch:{ all -> 0x04bf }
            r2 = 0
            r5 = r6[r2]     // Catch:{ all -> 0x04bf }
            boolean r2 = android.text.TextUtils.isEmpty(r5)     // Catch:{ all -> 0x04bf }
            if (r2 != 0) goto L_0x0401
            java.io.File r2 = new java.io.File     // Catch:{ all -> 0x03fc }
            java.io.File r5 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ all -> 0x03fc }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x03fc }
            r7.<init>()     // Catch:{ all -> 0x03fc }
            r21 = 0
            r6 = r6[r21]     // Catch:{ all -> 0x03fc }
            java.lang.String r6 = org.telegram.messenger.Utilities.MD5(r6)     // Catch:{ all -> 0x03fc }
            r7.append(r6)     // Catch:{ all -> 0x03fc }
            java.lang.String r6 = ".wp"
            r7.append(r6)     // Catch:{ all -> 0x03fc }
            java.lang.String r6 = r7.toString()     // Catch:{ all -> 0x03fc }
            r2.<init>(r5, r6)     // Catch:{ all -> 0x03fc }
            java.lang.String r5 = r2.getAbsolutePath()     // Catch:{ all -> 0x03fc }
            android.graphics.BitmapFactory.decodeFile(r5, r1)     // Catch:{ all -> 0x03fc }
            r5 = r2
            r2 = 0
            goto L_0x0419
        L_0x03fc:
            r0 = move-exception
            r2 = 0
        L_0x03fe:
            r12 = 2
            goto L_0x04c2
        L_0x0401:
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ all -> 0x04bf }
            r5 = r37
            r2.<init>(r5)     // Catch:{ all -> 0x04bf }
            java.nio.channels.FileChannel r5 = r2.getChannel()     // Catch:{ all -> 0x04bc }
            int r6 = r8.intValue()     // Catch:{ all -> 0x04bc }
            long r6 = (long) r6     // Catch:{ all -> 0x04bc }
            r5.position(r6)     // Catch:{ all -> 0x04bc }
            r5 = 0
            android.graphics.BitmapFactory.decodeStream(r2, r5, r1)     // Catch:{ all -> 0x04bc }
            r5 = 0
        L_0x0419:
            int r6 = r1.outWidth     // Catch:{ all -> 0x04bc }
            if (r6 <= 0) goto L_0x04ac
            int r7 = r1.outHeight     // Catch:{ all -> 0x04bc }
            if (r7 <= 0) goto L_0x04ac
            float r6 = (float) r6     // Catch:{ all -> 0x04bc }
            float r6 = r6 / r16
            float r7 = (float) r7     // Catch:{ all -> 0x04bc }
            float r7 = r7 / r16
            float r6 = java.lang.Math.min(r6, r7)     // Catch:{ all -> 0x04bc }
            r7 = 1
            r1.inSampleSize = r7     // Catch:{ all -> 0x04bc }
            int r7 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1))
            if (r7 <= 0) goto L_0x043f
        L_0x0432:
            int r7 = r1.inSampleSize     // Catch:{ all -> 0x04bc }
            r12 = 2
            int r7 = r7 * 2
            r1.inSampleSize = r7     // Catch:{ all -> 0x04aa }
            float r7 = (float) r7     // Catch:{ all -> 0x04aa }
            int r7 = (r7 > r6 ? 1 : (r7 == r6 ? 0 : -1))
            if (r7 < 0) goto L_0x0432
            goto L_0x0440
        L_0x043f:
            r12 = 2
        L_0x0440:
            r6 = 0
            r1.inJustDecodeBounds = r6     // Catch:{ all -> 0x04aa }
            if (r5 == 0) goto L_0x044e
            java.lang.String r5 = r5.getAbsolutePath()     // Catch:{ all -> 0x04aa }
            android.graphics.Bitmap r1 = android.graphics.BitmapFactory.decodeFile(r5, r1)     // Catch:{ all -> 0x04aa }
            goto L_0x045f
        L_0x044e:
            java.nio.channels.FileChannel r5 = r2.getChannel()     // Catch:{ all -> 0x04aa }
            int r6 = r8.intValue()     // Catch:{ all -> 0x04aa }
            long r6 = (long) r6     // Catch:{ all -> 0x04aa }
            r5.position(r6)     // Catch:{ all -> 0x04aa }
            r5 = 0
            android.graphics.Bitmap r1 = android.graphics.BitmapFactory.decodeStream(r2, r5, r1)     // Catch:{ all -> 0x04aa }
        L_0x045f:
            if (r1 == 0) goto L_0x04ad
            android.graphics.Paint r5 = new android.graphics.Paint     // Catch:{ all -> 0x04aa }
            r5.<init>()     // Catch:{ all -> 0x04aa }
            r6 = 1
            r5.setFilterBitmap(r6)     // Catch:{ all -> 0x04aa }
            int r6 = r1.getWidth()     // Catch:{ all -> 0x04aa }
            float r6 = (float) r6     // Catch:{ all -> 0x04aa }
            float r6 = r6 / r16
            int r7 = r1.getHeight()     // Catch:{ all -> 0x04aa }
            float r7 = (float) r7     // Catch:{ all -> 0x04aa }
            float r7 = r7 / r16
            float r6 = java.lang.Math.min(r6, r7)     // Catch:{ all -> 0x04aa }
            int r7 = r1.getWidth()     // Catch:{ all -> 0x04aa }
            float r7 = (float) r7     // Catch:{ all -> 0x04aa }
            float r7 = r7 / r6
            int r8 = r1.getHeight()     // Catch:{ all -> 0x04aa }
            float r8 = (float) r8     // Catch:{ all -> 0x04aa }
            float r8 = r8 / r6
            r0.set(r14, r14, r7, r8)     // Catch:{ all -> 0x04aa }
            int r6 = r9.getWidth()     // Catch:{ all -> 0x04aa }
            float r6 = (float) r6     // Catch:{ all -> 0x04aa }
            float r7 = r0.width()     // Catch:{ all -> 0x04aa }
            float r6 = r6 - r7
            float r6 = r6 / r13
            int r7 = r9.getHeight()     // Catch:{ all -> 0x04aa }
            float r7 = (float) r7     // Catch:{ all -> 0x04aa }
            float r8 = r0.height()     // Catch:{ all -> 0x04aa }
            float r7 = r7 - r8
            float r7 = r7 / r13
            r0.offset(r6, r7)     // Catch:{ all -> 0x04aa }
            r6 = 0
            r3.drawBitmap(r1, r6, r0, r5)     // Catch:{ all -> 0x04aa }
            r1 = 1
            goto L_0x04ae
        L_0x04aa:
            r0 = move-exception
            goto L_0x04c2
        L_0x04ac:
            r12 = 2
        L_0x04ad:
            r1 = 0
        L_0x04ae:
            if (r2 == 0) goto L_0x04b9
            r2.close()     // Catch:{ Exception -> 0x04b4 }
            goto L_0x04b9
        L_0x04b4:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x0680 }
        L_0x04b9:
            r0 = 80
            goto L_0x04e2
        L_0x04bc:
            r0 = move-exception
            goto L_0x03fe
        L_0x04bf:
            r0 = move-exception
            r12 = 2
            r2 = 0
        L_0x04c2:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x04d1 }
            if (r2 == 0) goto L_0x04df
            r2.close()     // Catch:{ Exception -> 0x04cb }
            goto L_0x04df
        L_0x04cb:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x0680 }
            goto L_0x04df
        L_0x04d1:
            r0 = move-exception
            r1 = r0
            if (r2 == 0) goto L_0x04de
            r2.close()     // Catch:{ Exception -> 0x04d9 }
            goto L_0x04de
        L_0x04d9:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x0680 }
        L_0x04de:
            throw r1     // Catch:{ all -> 0x0680 }
        L_0x04df:
            r0 = 80
            r1 = 0
        L_0x04e2:
            if (r1 != 0) goto L_0x0502
            int r1 = r9.getWidth()     // Catch:{ all -> 0x0680 }
            int r2 = r9.getHeight()     // Catch:{ all -> 0x0680 }
            int r2 = r2 - r11
            android.graphics.drawable.Drawable r1 = createDefaultWallpaper(r1, r2)     // Catch:{ all -> 0x0680 }
            int r2 = r9.getWidth()     // Catch:{ all -> 0x0680 }
            int r5 = r9.getHeight()     // Catch:{ all -> 0x0680 }
            int r5 = r5 - r11
            r6 = 0
            r1.setBounds(r6, r11, r2, r5)     // Catch:{ all -> 0x0680 }
            r1.draw(r3)     // Catch:{ all -> 0x0680 }
            goto L_0x0503
        L_0x0502:
            r6 = 0
        L_0x0503:
            r2 = r19
            r1 = r26
            r1.setColor(r2)     // Catch:{ all -> 0x0680 }
            r2 = 0
            r5 = 0
            int r7 = r9.getWidth()     // Catch:{ all -> 0x0680 }
            float r13 = (float) r7     // Catch:{ all -> 0x0680 }
            r14 = 1123024896(0x42var_, float:120.0)
            r10 = r3
            r7 = r17
            r6 = 20
            r8 = 13
            r16 = 120(0x78, float:1.68E-43)
            r11 = r2
            r2 = 2
            r12 = r5
            r5 = r18
            r35 = r24
            r36 = r25
            r6 = r15
            r15 = r1
            r10.drawRect(r11, r12, r13, r14, r15)     // Catch:{ all -> 0x0680 }
            if (r4 == 0) goto L_0x0543
            int r10 = r4.getIntrinsicHeight()     // Catch:{ all -> 0x0680 }
            int r11 = 120 - r10
            int r11 = r11 / r2
            int r10 = r4.getIntrinsicWidth()     // Catch:{ all -> 0x0680 }
            int r10 = r10 + r8
            int r12 = r4.getIntrinsicHeight()     // Catch:{ all -> 0x0680 }
            int r12 = r12 + r11
            r4.setBounds(r8, r11, r10, r12)     // Catch:{ all -> 0x0680 }
            r4.draw(r3)     // Catch:{ all -> 0x0680 }
        L_0x0543:
            if (r6 == 0) goto L_0x0567
            int r4 = r9.getWidth()     // Catch:{ all -> 0x0680 }
            int r8 = r6.getIntrinsicWidth()     // Catch:{ all -> 0x0680 }
            int r4 = r4 - r8
            int r4 = r4 + -10
            int r8 = r6.getIntrinsicHeight()     // Catch:{ all -> 0x0680 }
            int r11 = 120 - r8
            int r11 = r11 / r2
            int r8 = r6.getIntrinsicWidth()     // Catch:{ all -> 0x0680 }
            int r8 = r8 + r4
            int r10 = r6.getIntrinsicHeight()     // Catch:{ all -> 0x0680 }
            int r10 = r10 + r11
            r6.setBounds(r4, r11, r8, r10)     // Catch:{ all -> 0x0680 }
            r6.draw(r3)     // Catch:{ all -> 0x0680 }
        L_0x0567:
            r4 = 1
            r6 = r7[r4]     // Catch:{ all -> 0x0680 }
            r8 = 216(0xd8, float:3.03E-43)
            int r10 = r9.getWidth()     // Catch:{ all -> 0x0680 }
            r11 = 20
            int r10 = r10 - r11
            r11 = 308(0x134, float:4.32E-43)
            r12 = 161(0xa1, float:2.26E-43)
            r6.setBounds(r12, r8, r10, r11)     // Catch:{ all -> 0x0680 }
            r24 = r7[r4]     // Catch:{ all -> 0x0680 }
            r25 = 0
            r26 = 560(0x230, float:7.85E-43)
            r27 = 522(0x20a, float:7.31E-43)
            r28 = 0
            r29 = 0
            r24.setTop(r25, r26, r27, r28, r29)     // Catch:{ all -> 0x0680 }
            r4 = 1
            r6 = r7[r4]     // Catch:{ all -> 0x0680 }
            r6.draw(r3)     // Catch:{ all -> 0x0680 }
            r6 = r7[r4]     // Catch:{ all -> 0x0680 }
            r8 = 430(0x1ae, float:6.03E-43)
            int r10 = r9.getWidth()     // Catch:{ all -> 0x0680 }
            r11 = 20
            int r10 = r10 - r11
            r11 = 522(0x20a, float:7.31E-43)
            r6.setBounds(r12, r8, r10, r11)     // Catch:{ all -> 0x0680 }
            r24 = r7[r4]     // Catch:{ all -> 0x0680 }
            r25 = 430(0x1ae, float:6.03E-43)
            r26 = 560(0x230, float:7.85E-43)
            r27 = 522(0x20a, float:7.31E-43)
            r28 = 0
            r29 = 0
            r24.setTop(r25, r26, r27, r28, r29)     // Catch:{ all -> 0x0680 }
            r4 = 1
            r4 = r7[r4]     // Catch:{ all -> 0x0680 }
            r4.draw(r3)     // Catch:{ all -> 0x0680 }
            r4 = 0
            r6 = r7[r4]     // Catch:{ all -> 0x0680 }
            r8 = 323(0x143, float:4.53E-43)
            r10 = 399(0x18f, float:5.59E-43)
            r11 = 415(0x19f, float:5.82E-43)
            r12 = 20
            r6.setBounds(r12, r8, r10, r11)     // Catch:{ all -> 0x0680 }
            r22 = r7[r4]     // Catch:{ all -> 0x0680 }
            r23 = 323(0x143, float:4.53E-43)
            r24 = 560(0x230, float:7.85E-43)
            r25 = 522(0x20a, float:7.31E-43)
            r26 = 0
            r27 = 0
            r22.setTop(r23, r24, r25, r26, r27)     // Catch:{ all -> 0x0680 }
            r4 = 0
            r4 = r7[r4]     // Catch:{ all -> 0x0680 }
            r4.draw(r3)     // Catch:{ all -> 0x0680 }
            r1.setColor(r5)     // Catch:{ all -> 0x0680 }
            r11 = 0
            int r4 = r9.getHeight()     // Catch:{ all -> 0x0680 }
            int r4 = r4 + -120
            float r12 = (float) r4     // Catch:{ all -> 0x0680 }
            int r4 = r9.getWidth()     // Catch:{ all -> 0x0680 }
            float r13 = (float) r4     // Catch:{ all -> 0x0680 }
            int r4 = r9.getHeight()     // Catch:{ all -> 0x0680 }
            float r14 = (float) r4     // Catch:{ all -> 0x0680 }
            r10 = r3
            r15 = r1
            r10.drawRect(r11, r12, r13, r14, r15)     // Catch:{ all -> 0x0680 }
            r1 = 22
            r4 = r36
            if (r4 == 0) goto L_0x0615
            int r5 = r9.getHeight()     // Catch:{ all -> 0x0680 }
            int r5 = r5 + -120
            int r6 = r4.getIntrinsicHeight()     // Catch:{ all -> 0x0680 }
            int r11 = 120 - r6
            int r11 = r11 / r2
            int r5 = r5 + r11
            int r6 = r4.getIntrinsicWidth()     // Catch:{ all -> 0x0680 }
            int r6 = r6 + r1
            int r7 = r4.getIntrinsicHeight()     // Catch:{ all -> 0x0680 }
            int r7 = r7 + r5
            r4.setBounds(r1, r5, r6, r7)     // Catch:{ all -> 0x0680 }
            r4.draw(r3)     // Catch:{ all -> 0x0680 }
        L_0x0615:
            r4 = r35
            if (r4 == 0) goto L_0x0641
            int r5 = r9.getWidth()     // Catch:{ all -> 0x0680 }
            int r6 = r4.getIntrinsicWidth()     // Catch:{ all -> 0x0680 }
            int r5 = r5 - r6
            int r5 = r5 - r1
            int r1 = r9.getHeight()     // Catch:{ all -> 0x0680 }
            int r1 = r1 + -120
            int r6 = r4.getIntrinsicHeight()     // Catch:{ all -> 0x0680 }
            int r11 = 120 - r6
            int r11 = r11 / r2
            int r1 = r1 + r11
            int r2 = r4.getIntrinsicWidth()     // Catch:{ all -> 0x0680 }
            int r2 = r2 + r5
            int r6 = r4.getIntrinsicHeight()     // Catch:{ all -> 0x0680 }
            int r6 = r6 + r1
            r4.setBounds(r5, r1, r2, r6)     // Catch:{ all -> 0x0680 }
            r4.draw(r3)     // Catch:{ all -> 0x0680 }
        L_0x0641:
            r1 = 0
            r3.setBitmap(r1)     // Catch:{ all -> 0x0680 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0680 }
            r1.<init>()     // Catch:{ all -> 0x0680 }
            java.lang.String r2 = "-2147483648_"
            r1.append(r2)     // Catch:{ all -> 0x0680 }
            int r2 = org.telegram.messenger.SharedConfig.getLastLocalId()     // Catch:{ all -> 0x0680 }
            r1.append(r2)     // Catch:{ all -> 0x0680 }
            java.lang.String r2 = ".jpg"
            r1.append(r2)     // Catch:{ all -> 0x0680 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0680 }
            java.io.File r2 = new java.io.File     // Catch:{ all -> 0x0680 }
            r3 = 4
            java.io.File r3 = org.telegram.messenger.FileLoader.getDirectory(r3)     // Catch:{ all -> 0x0680 }
            r2.<init>(r3, r1)     // Catch:{ all -> 0x0680 }
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch:{ all -> 0x067b }
            r1.<init>(r2)     // Catch:{ all -> 0x067b }
            android.graphics.Bitmap$CompressFormat r3 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ all -> 0x067b }
            r9.compress(r3, r0, r1)     // Catch:{ all -> 0x067b }
            org.telegram.messenger.SharedConfig.saveConfig()     // Catch:{ all -> 0x067b }
            java.lang.String r0 = r2.getAbsolutePath()     // Catch:{ all -> 0x067b }
            return r0
        L_0x067b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0680 }
            goto L_0x0684
        L_0x0680:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0684:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.createThemePreviewImage(java.lang.String, java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeAccent):java.lang.String");
    }

    /* access modifiers changed from: private */
    public static void checkIsDark(HashMap<String, Integer> hashMap, ThemeInfo themeInfo) {
        if (themeInfo != null && hashMap != null && themeInfo.isDark == -1) {
            if (ColorUtils.calculateLuminance(ColorUtils.blendARGB(getPreviewColor(hashMap, "windowBackgroundWhite"), getPreviewColor(hashMap, "windowBackgroundWhite"), 0.5f)) < 0.5d) {
                int unused = themeInfo.isDark = 1;
            } else {
                int unused2 = themeInfo.isDark = 0;
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:?, code lost:
        r4 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r13).intValue();
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:34:0x0083 */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00cb A[SYNTHETIC, Splitter:B:55:0x00cb] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.HashMap<java.lang.String, java.lang.Integer> getThemeFileValues(java.io.File r16, java.lang.String r17, java.lang.String[] r18) {
        /*
            r0 = r18
            java.util.HashMap r1 = new java.util.HashMap
            r2 = 500(0x1f4, float:7.0E-43)
            r1.<init>(r2)
            r2 = 1024(0x400, float:1.435E-42)
            r3 = 0
            byte[] r2 = new byte[r2]     // Catch:{ all -> 0x00c5 }
            if (r17 == 0) goto L_0x0015
            java.io.File r4 = getAssetFile(r17)     // Catch:{ all -> 0x00c5 }
            goto L_0x0017
        L_0x0015:
            r4 = r16
        L_0x0017:
            java.io.FileInputStream r5 = new java.io.FileInputStream     // Catch:{ all -> 0x00c5 }
            r5.<init>(r4)     // Catch:{ all -> 0x00c5 }
            r3 = -1
            r4 = 0
            r6 = 0
            r7 = -1
            r8 = 0
        L_0x0021:
            int r9 = r5.read(r2)     // Catch:{ all -> 0x00c2 }
            if (r9 == r3) goto L_0x00b5
            r12 = r6
            r10 = 0
            r11 = 0
        L_0x002a:
            r13 = 1
            if (r10 >= r9) goto L_0x00a2
            byte r14 = r2[r10]     // Catch:{ all -> 0x00c2 }
            r15 = 10
            if (r14 != r15) goto L_0x009d
            int r14 = r10 - r11
            int r14 = r14 + r13
            java.lang.String r15 = new java.lang.String     // Catch:{ all -> 0x00c2 }
            int r13 = r14 + -1
            r15.<init>(r2, r11, r13)     // Catch:{ all -> 0x00c2 }
            java.lang.String r13 = "WLS="
            boolean r13 = r15.startsWith(r13)     // Catch:{ all -> 0x00c2 }
            if (r13 == 0) goto L_0x0052
            if (r0 == 0) goto L_0x009b
            int r13 = r0.length     // Catch:{ all -> 0x00c2 }
            if (r13 <= 0) goto L_0x009b
            r13 = 4
            java.lang.String r13 = r15.substring(r13)     // Catch:{ all -> 0x00c2 }
            r0[r4] = r13     // Catch:{ all -> 0x00c2 }
            goto L_0x009b
        L_0x0052:
            java.lang.String r13 = "WPS"
            boolean r13 = r15.startsWith(r13)     // Catch:{ all -> 0x00c2 }
            if (r13 == 0) goto L_0x005e
            int r14 = r14 + r12
            r7 = r14
            r8 = 1
            goto L_0x00a2
        L_0x005e:
            r13 = 61
            int r13 = r15.indexOf(r13)     // Catch:{ all -> 0x00c2 }
            if (r13 == r3) goto L_0x009b
            java.lang.String r3 = r15.substring(r4, r13)     // Catch:{ all -> 0x00c2 }
            int r13 = r13 + 1
            java.lang.String r13 = r15.substring(r13)     // Catch:{ all -> 0x00c2 }
            int r15 = r13.length()     // Catch:{ all -> 0x00c2 }
            if (r15 <= 0) goto L_0x008c
            char r15 = r13.charAt(r4)     // Catch:{ all -> 0x00c2 }
            r4 = 35
            if (r15 != r4) goto L_0x008c
            int r4 = android.graphics.Color.parseColor(r13)     // Catch:{ Exception -> 0x0083 }
            goto L_0x0094
        L_0x0083:
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r13)     // Catch:{ all -> 0x00c2 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x00c2 }
            goto L_0x0094
        L_0x008c:
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r13)     // Catch:{ all -> 0x00c2 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x00c2 }
        L_0x0094:
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x00c2 }
            r1.put(r3, r4)     // Catch:{ all -> 0x00c2 }
        L_0x009b:
            int r11 = r11 + r14
            int r12 = r12 + r14
        L_0x009d:
            int r10 = r10 + 1
            r3 = -1
            r4 = 0
            goto L_0x002a
        L_0x00a2:
            if (r6 != r12) goto L_0x00a5
            goto L_0x00b5
        L_0x00a5:
            java.nio.channels.FileChannel r3 = r5.getChannel()     // Catch:{ all -> 0x00c2 }
            long r9 = (long) r12     // Catch:{ all -> 0x00c2 }
            r3.position(r9)     // Catch:{ all -> 0x00c2 }
            if (r8 == 0) goto L_0x00b0
            goto L_0x00b5
        L_0x00b0:
            r6 = r12
            r3 = -1
            r4 = 0
            goto L_0x0021
        L_0x00b5:
            java.lang.String r0 = "wallpaperFileOffset"
            java.lang.Integer r2 = java.lang.Integer.valueOf(r7)     // Catch:{ all -> 0x00c2 }
            r1.put(r0, r2)     // Catch:{ all -> 0x00c2 }
            r5.close()     // Catch:{ Exception -> 0x00cf }
            goto L_0x00d4
        L_0x00c2:
            r0 = move-exception
            r3 = r5
            goto L_0x00c6
        L_0x00c5:
            r0 = move-exception
        L_0x00c6:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x00d5 }
            if (r3 == 0) goto L_0x00d4
            r3.close()     // Catch:{ Exception -> 0x00cf }
            goto L_0x00d4
        L_0x00cf:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x00d4:
            return r1
        L_0x00d5:
            r0 = move-exception
            r1 = r0
            if (r3 == 0) goto L_0x00e2
            r3.close()     // Catch:{ Exception -> 0x00dd }
            goto L_0x00e2
        L_0x00dd:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x00e2:
            goto L_0x00e4
        L_0x00e3:
            throw r1
        L_0x00e4:
            goto L_0x00e3
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
            Paint paint5 = new Paint();
            linkSelectionPaint = paint5;
            paint5.setPathEffect(LinkPath.getRoundedEffect());
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
                    PremiumGradient.getInstance().checkIconColors();
                    return;
                }
            }
        }
    }

    public static void createCommonDialogResources(Context context) {
        if (dialogs_countTextPaint == null) {
            TextPaint textPaint = new TextPaint(1);
            dialogs_countTextPaint = textPaint;
            textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            dialogs_countPaint = new Paint(1);
            dialogs_reactionsCountPaint = new Paint(1);
            dialogs_onlineCirclePaint = new Paint(1);
        }
        dialogs_countTextPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
    }

    public static void createDialogsResources(Context context) {
        createCommonResources(context);
        createCommonDialogResources(context);
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
            dialogs_archiveTextPaint = textPaint4;
            textPaint4.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            TextPaint textPaint5 = new TextPaint(1);
            dialogs_archiveTextPaintSmall = textPaint5;
            textPaint5.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            dialogs_onlinePaint = new TextPaint(1);
            dialogs_offlinePaint = new TextPaint(1);
            dialogs_tabletSeletedPaint = new Paint();
            dialogs_pinnedPaint = new Paint(1);
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
            dialogs_muteDrawable = resources.getDrawable(NUM).mutate();
            dialogs_verifiedDrawable = resources.getDrawable(NUM).mutate();
            dialogs_scamDrawable = new ScamDrawable(11, 0);
            dialogs_fakeDrawable = new ScamDrawable(11, 1);
            dialogs_verifiedCheckDrawable = resources.getDrawable(NUM).mutate();
            dialogs_mentionDrawable = resources.getDrawable(NUM);
            dialogs_reactionsMentionDrawable = resources.getDrawable(NUM);
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
            dialogs_reactionsCountPaint.setColor(getColor("dialogReactionMentionBackground"));
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
            setDrawableColorByKey(dialogs_pinnedDrawable, "chats_pinnedIcon");
            setDrawableColorByKey(dialogs_reorderDrawable, "chats_pinnedIcon");
            setDrawableColorByKey(dialogs_muteDrawable, "chats_muteIcon");
            setDrawableColorByKey(dialogs_mentionDrawable, "chats_mentionIcon");
            setDrawableColorByKey(dialogs_reactionsMentionDrawable, "chats_mentionIcon");
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

    public static void createCommonMessageResources() {
        synchronized (sync) {
            if (chat_msgTextPaint == null) {
                chat_msgTextPaint = new TextPaint(1);
                chat_msgGameTextPaint = new TextPaint(1);
                chat_msgTextPaintOneEmoji = new TextPaint(1);
                chat_msgTextPaintTwoEmoji = new TextPaint(1);
                chat_msgTextPaintThreeEmoji = new TextPaint(1);
                TextPaint textPaint = new TextPaint(1);
                chat_msgBotButtonPaint = textPaint;
                textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            }
            chat_msgTextPaintOneEmoji.setTextSize((float) AndroidUtilities.dp(28.0f));
            chat_msgTextPaintTwoEmoji.setTextSize((float) AndroidUtilities.dp(24.0f));
            chat_msgTextPaintThreeEmoji.setTextSize((float) AndroidUtilities.dp(20.0f));
            chat_msgTextPaint.setTextSize((float) AndroidUtilities.dp((float) SharedConfig.fontSize));
            chat_msgGameTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
            chat_msgBotButtonPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
        }
    }

    public static void createCommonChatResources() {
        createCommonMessageResources();
        if (chat_infoPaint == null) {
            chat_infoPaint = new TextPaint(1);
            TextPaint textPaint = new TextPaint(1);
            chat_stickerCommentCountPaint = textPaint;
            textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            TextPaint textPaint2 = new TextPaint(1);
            chat_docNamePaint = textPaint2;
            textPaint2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_docBackPaint = new Paint(1);
            chat_deleteProgressPaint = new Paint(1);
            Paint paint = new Paint(1);
            chat_botProgressPaint = paint;
            paint.setStrokeCap(Paint.Cap.ROUND);
            chat_botProgressPaint.setStyle(Paint.Style.STROKE);
            TextPaint textPaint3 = new TextPaint(1);
            chat_locationTitlePaint = textPaint3;
            textPaint3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_locationAddressPaint = new TextPaint(1);
            Paint paint2 = new Paint();
            chat_urlPaint = paint2;
            paint2.setPathEffect(LinkPath.getRoundedEffect());
            Paint paint3 = new Paint();
            chat_outUrlPaint = paint3;
            paint3.setPathEffect(LinkPath.getRoundedEffect());
            Paint paint4 = new Paint();
            chat_textSearchSelectionPaint = paint4;
            paint4.setPathEffect(LinkPath.getRoundedEffect());
            Paint paint5 = new Paint(1);
            chat_radialProgressPaint = paint5;
            paint5.setStrokeCap(Paint.Cap.ROUND);
            chat_radialProgressPaint.setStyle(Paint.Style.STROKE);
            chat_radialProgressPaint.setColor(-NUM);
            Paint paint6 = new Paint(1);
            chat_radialProgress2Paint = paint6;
            paint6.setStrokeCap(Paint.Cap.ROUND);
            chat_radialProgress2Paint.setStyle(Paint.Style.STROKE);
            chat_audioTimePaint = new TextPaint(1);
            TextPaint textPaint4 = new TextPaint(1);
            chat_livePaint = textPaint4;
            textPaint4.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            TextPaint textPaint5 = new TextPaint(1);
            chat_audioTitlePaint = textPaint5;
            textPaint5.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_audioPerformerPaint = new TextPaint(1);
            TextPaint textPaint6 = new TextPaint(1);
            chat_botButtonPaint = textPaint6;
            textPaint6.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            TextPaint textPaint7 = new TextPaint(1);
            chat_contactNamePaint = textPaint7;
            textPaint7.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_contactPhonePaint = new TextPaint(1);
            chat_durationPaint = new TextPaint(1);
            TextPaint textPaint8 = new TextPaint(1);
            chat_gamePaint = textPaint8;
            textPaint8.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_shipmentPaint = new TextPaint(1);
            chat_timePaint = new TextPaint(1);
            chat_adminPaint = new TextPaint(1);
            TextPaint textPaint9 = new TextPaint(1);
            chat_namePaint = textPaint9;
            textPaint9.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_forwardNamePaint = new TextPaint(1);
            TextPaint textPaint10 = new TextPaint(1);
            chat_replyNamePaint = textPaint10;
            textPaint10.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_replyTextPaint = new TextPaint(1);
            TextPaint textPaint11 = new TextPaint(1);
            chat_instantViewPaint = textPaint11;
            textPaint11.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            Paint paint7 = new Paint(1);
            chat_instantViewRectPaint = paint7;
            paint7.setStyle(Paint.Style.STROKE);
            chat_instantViewRectPaint.setStrokeCap(Paint.Cap.ROUND);
            Paint paint8 = new Paint(1);
            chat_pollTimerPaint = paint8;
            paint8.setStyle(Paint.Style.STROKE);
            chat_pollTimerPaint.setStrokeCap(Paint.Cap.ROUND);
            chat_replyLinePaint = new Paint(1);
            chat_msgErrorPaint = new Paint(1);
            chat_statusPaint = new Paint(1);
            Paint paint9 = new Paint(1);
            chat_statusRecordPaint = paint9;
            paint9.setStyle(Paint.Style.STROKE);
            chat_statusRecordPaint.setStrokeCap(Paint.Cap.ROUND);
            TextPaint textPaint12 = new TextPaint(1);
            chat_actionTextPaint = textPaint12;
            textPaint12.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            Paint paint10 = new Paint(1);
            chat_actionBackgroundGradientDarkenPaint = paint10;
            paint10.setColor(NUM);
            chat_timeBackgroundPaint = new Paint(1);
            TextPaint textPaint13 = new TextPaint(1);
            chat_contextResult_titleTextPaint = textPaint13;
            textPaint13.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_contextResult_descriptionTextPaint = new TextPaint(1);
            chat_composeBackgroundPaint = new Paint();
            new Paint(1);
            chat_radialProgressPausedSeekbarPaint = new Paint(1);
            chat_messageBackgroundSelectedPaint = new Paint(1);
            chat_actionBackgroundPaint = new Paint(1);
            chat_actionBackgroundSelectedPaint = new Paint(1);
            chat_actionBackgroundPaint2 = new Paint(1);
            new Paint(1);
            addChatPaint("paintChatMessageBackgroundSelected", chat_messageBackgroundSelectedPaint, "chat_selectedBackground");
            addChatPaint("paintChatActionBackground", chat_actionBackgroundPaint, "chat_serviceBackground");
            addChatPaint("paintChatActionBackgroundSelected", chat_actionBackgroundSelectedPaint, "chat_serviceBackgroundSelected");
            addChatPaint("paintChatActionText", chat_actionTextPaint, "chat_serviceText");
            addChatPaint("paintChatBotButton", chat_botButtonPaint, "chat_botButtonText");
            addChatPaint("paintChatComposeBackground", chat_composeBackgroundPaint, "chat_messagePanelBackground");
            addChatPaint("paintChatTimeBackground", chat_timeBackgroundPaint, "chat_mediaTimeBackground");
        }
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(12:4|(1:6)|27|7|8|9|(4:11|(1:13)(1:14)|15|16)|17|18|19|20|21) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x0941 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void createChatResources(android.content.Context r17, boolean r18) {
        /*
            r0 = r18
            createCommonChatResources()
            r1 = 1077936128(0x40400000, float:3.0)
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 2
            if (r0 != 0) goto L_0x0ad8
            org.telegram.ui.ActionBar.Theme$MessageDrawable r4 = chat_msgInDrawable
            if (r4 != 0) goto L_0x0ad8
            android.content.res.Resources r4 = r17.getResources()
            r5 = 2131166202(0x7var_fa, float:1.7946643E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_msgNoSoundDrawable = r5
            org.telegram.ui.ActionBar.Theme$MessageDrawable r5 = new org.telegram.ui.ActionBar.Theme$MessageDrawable
            r6 = 0
            r5.<init>(r6, r6, r6)
            chat_msgInDrawable = r5
            org.telegram.ui.ActionBar.Theme$MessageDrawable r5 = new org.telegram.ui.ActionBar.Theme$MessageDrawable
            r7 = 1
            r5.<init>(r6, r6, r7)
            chat_msgInSelectedDrawable = r5
            org.telegram.ui.ActionBar.Theme$MessageDrawable r5 = new org.telegram.ui.ActionBar.Theme$MessageDrawable
            r5.<init>(r6, r7, r6)
            chat_msgOutDrawable = r5
            org.telegram.ui.ActionBar.Theme$MessageDrawable r5 = new org.telegram.ui.ActionBar.Theme$MessageDrawable
            r5.<init>(r6, r7, r7)
            chat_msgOutSelectedDrawable = r5
            org.telegram.ui.ActionBar.Theme$MessageDrawable r5 = new org.telegram.ui.ActionBar.Theme$MessageDrawable
            r5.<init>(r7, r6, r6)
            chat_msgInMediaDrawable = r5
            org.telegram.ui.ActionBar.Theme$MessageDrawable r5 = new org.telegram.ui.ActionBar.Theme$MessageDrawable
            r5.<init>(r7, r6, r7)
            chat_msgInMediaSelectedDrawable = r5
            org.telegram.ui.ActionBar.Theme$MessageDrawable r5 = new org.telegram.ui.ActionBar.Theme$MessageDrawable
            r5.<init>(r7, r7, r6)
            chat_msgOutMediaDrawable = r5
            org.telegram.ui.ActionBar.Theme$MessageDrawable r5 = new org.telegram.ui.ActionBar.Theme$MessageDrawable
            r5.<init>(r7, r7, r7)
            chat_msgOutMediaSelectedDrawable = r5
            org.telegram.ui.Components.PathAnimator r5 = new org.telegram.ui.Components.PathAnimator
            r8 = 1050018841(0x3e960419, float:0.293)
            r9 = -1043333120(0xffffffffc1d00000, float:-26.0)
            r10 = -1042284544(0xffffffffc1e00000, float:-28.0)
            r11 = 1065353216(0x3var_, float:1.0)
            r5.<init>(r8, r9, r10, r11)
            playPauseAnimator = r5
            r8 = 1126563840(0x43260000, float:166.0)
            java.lang.String r9 = "M 34.141 16.042 C 37.384 17.921 40.886 20.001 44.211 21.965 C 46.139 23.104 49.285 24.729 49.586 25.917 C 50.289 28.687 48.484 30 46.274 30 L 6 30.021 C 3.79 30.021 2.075 30.023 2 26.021 L 2.009 3.417 C 2.009 0.417 5.326 -0.58 7.068 0.417 C 10.545 2.406 25.024 10.761 34.141 16.042 Z"
            r5.addSvgKeyFrame(r9, r8)
            org.telegram.ui.Components.PathAnimator r5 = playPauseAnimator
            r8 = 1128792064(0x43480000, float:200.0)
            java.lang.String r9 = "M 37.843 17.769 C 41.143 19.508 44.131 21.164 47.429 23.117 C 48.542 23.775 49.623 24.561 49.761 25.993 C 50.074 28.708 48.557 30 46.347 30 L 6 30.012 C 3.79 30.012 2 28.222 2 26.012 L 2.009 4.609 C 2.009 1.626 5.276 0.664 7.074 1.541 C 10.608 3.309 28.488 12.842 37.843 17.769 Z"
            r5.addSvgKeyFrame(r9, r8)
            org.telegram.ui.Components.PathAnimator r5 = playPauseAnimator
            r8 = 1129906176(0x43590000, float:217.0)
            java.lang.String r9 = "M 40.644 18.756 C 43.986 20.389 49.867 23.108 49.884 25.534 C 49.897 27.154 49.88 24.441 49.894 26.059 C 49.911 28.733 48.6 30 46.39 30 L 6 30.013 C 3.79 30.013 2 28.223 2 26.013 L 2.008 5.52 C 2.008 2.55 5.237 1.614 7.079 2.401 C 10.656 4 31.106 14.097 40.644 18.756 Z"
            r5.addSvgKeyFrame(r9, r8)
            org.telegram.ui.Components.PathAnimator r5 = playPauseAnimator
            r8 = 1131020288(0x436a0000, float:234.0)
            java.lang.String r9 = "M 43.782 19.218 C 47.117 20.675 50.075 21.538 50.041 24.796 C 50.022 26.606 50.038 24.309 50.039 26.104 C 50.038 28.736 48.663 30 46.453 30 L 6 29.986 C 3.79 29.986 2 28.196 2 25.986 L 2.008 6.491 C 2.008 3.535 5.196 2.627 7.085 3.316 C 10.708 4.731 33.992 14.944 43.782 19.218 Z"
            r5.addSvgKeyFrame(r9, r8)
            org.telegram.ui.Components.PathAnimator r5 = playPauseAnimator
            r8 = 1132822528(0x43858000, float:267.0)
            java.lang.String r9 = "M 47.421 16.941 C 50.544 18.191 50.783 19.91 50.769 22.706 C 50.761 24.484 50.76 23.953 50.79 26.073 C 50.814 27.835 49.334 30 47.124 30 L 5 30.01 C 2.79 30.01 1 28.22 1 26.01 L 1.001 10.823 C 1.001 8.218 3.532 6.895 5.572 7.26 C 7.493 8.01 47.421 16.941 47.421 16.941 Z"
            r5.addSvgKeyFrame(r9, r8)
            org.telegram.ui.Components.PathAnimator r5 = playPauseAnimator
            r8 = 1133903872(0x43960000, float:300.0)
            java.lang.String r9 = "M 47.641 17.125 C 50.641 18.207 51.09 19.935 51.078 22.653 C 51.07 24.191 51.062 21.23 51.088 23.063 C 51.109 24.886 49.587 27 47.377 27 L 5 27.009 C 2.79 27.009 1 25.219 1 23.009 L 0.983 11.459 C 0.983 8.908 3.414 7.522 5.476 7.838 C 7.138 8.486 47.641 17.125 47.641 17.125 Z"
            r5.addSvgKeyFrame(r9, r8)
            org.telegram.ui.Components.PathAnimator r5 = playPauseAnimator
            r8 = 1136623616(0x43bvar_, float:383.0)
            java.lang.String r9 = "M 48 7 C 50.21 7 52 8.79 52 11 C 52 19 52 19 52 19 C 52 21.21 50.21 23 48 23 L 4 23 C 1.79 23 0 21.21 0 19 L 0 11 C 0 8.79 1.79 7 4 7 C 48 7 48 7 48 7 Z"
            r5.addSvgKeyFrame(r9, r8)
            r5 = 2131165680(0x7var_f0, float:1.7945584E38)
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            chat_msgOutCheckDrawable = r8
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            chat_msgOutCheckSelectedDrawable = r8
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            chat_msgOutCheckReadDrawable = r8
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            chat_msgOutCheckReadSelectedDrawable = r8
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            chat_msgMediaCheckDrawable = r8
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgStickerCheckDrawable = r5
            r5 = 2131165755(0x7var_b, float:1.7945736E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgOutHalfCheckDrawable = r5
            r5 = 2131165755(0x7var_b, float:1.7945736E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgOutHalfCheckSelectedDrawable = r5
            r5 = 2131165756(0x7var_c, float:1.7945738E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgMediaHalfCheckDrawable = r5
            r5 = 2131165756(0x7var_c, float:1.7945738E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgStickerHalfCheckDrawable = r5
            org.telegram.ui.Components.MsgClockDrawable r5 = new org.telegram.ui.Components.MsgClockDrawable
            r5.<init>()
            chat_msgClockDrawable = r5
            r5 = 2131165981(0x7var_d, float:1.7946194E38)
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            chat_msgInViewsDrawable = r8
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            chat_msgInViewsSelectedDrawable = r8
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            chat_msgOutViewsDrawable = r8
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            chat_msgOutViewsSelectedDrawable = r8
            r8 = 2131165902(0x7var_ce, float:1.7946034E38)
            android.graphics.drawable.Drawable r9 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r9 = r9.mutate()
            chat_msgInRepliesDrawable = r9
            android.graphics.drawable.Drawable r9 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r9 = r9.mutate()
            chat_msgInRepliesSelectedDrawable = r9
            android.graphics.drawable.Drawable r9 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r9 = r9.mutate()
            chat_msgOutRepliesDrawable = r9
            android.graphics.drawable.Drawable r9 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r9 = r9.mutate()
            chat_msgOutRepliesSelectedDrawable = r9
            r9 = 2131165866(0x7var_aa, float:1.7945961E38)
            android.graphics.drawable.Drawable r10 = r4.getDrawable(r9)
            android.graphics.drawable.Drawable r10 = r10.mutate()
            chat_msgInPinnedDrawable = r10
            android.graphics.drawable.Drawable r10 = r4.getDrawable(r9)
            android.graphics.drawable.Drawable r10 = r10.mutate()
            chat_msgInPinnedSelectedDrawable = r10
            android.graphics.drawable.Drawable r10 = r4.getDrawable(r9)
            android.graphics.drawable.Drawable r10 = r10.mutate()
            chat_msgOutPinnedDrawable = r10
            android.graphics.drawable.Drawable r10 = r4.getDrawable(r9)
            android.graphics.drawable.Drawable r10 = r10.mutate()
            chat_msgOutPinnedSelectedDrawable = r10
            android.graphics.drawable.Drawable r10 = r4.getDrawable(r9)
            android.graphics.drawable.Drawable r10 = r10.mutate()
            chat_msgMediaPinnedDrawable = r10
            android.graphics.drawable.Drawable r9 = r4.getDrawable(r9)
            android.graphics.drawable.Drawable r9 = r9.mutate()
            chat_msgStickerPinnedDrawable = r9
            android.graphics.drawable.Drawable r9 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r9 = r9.mutate()
            chat_msgMediaViewsDrawable = r9
            android.graphics.drawable.Drawable r9 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r9 = r9.mutate()
            chat_msgMediaRepliesDrawable = r9
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgStickerViewsDrawable = r5
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgStickerRepliesDrawable = r5
            r5 = 2131165624(0x7var_b8, float:1.794547E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgInMenuDrawable = r5
            r5 = 2131165624(0x7var_b8, float:1.794547E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgInMenuSelectedDrawable = r5
            r5 = 2131165624(0x7var_b8, float:1.794547E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgOutMenuDrawable = r5
            r5 = 2131165624(0x7var_b8, float:1.794547E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgOutMenuSelectedDrawable = r5
            r5 = 2131166198(0x7var_f6, float:1.7946635E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_msgMediaMenuDrawable = r5
            r5 = 2131165766(0x7var_, float:1.7945758E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgInInstantDrawable = r5
            r5 = 2131165766(0x7var_, float:1.7945758E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgOutInstantDrawable = r5
            r5 = 2131165993(0x7var_, float:1.7946219E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_msgErrorDrawable = r5
            r5 = 2131165575(0x7var_, float:1.794537E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_muteIconDrawable = r5
            r5 = 2131165481(0x7var_, float:1.794518E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_lockIconDrawable = r5
            android.graphics.drawable.Drawable[] r5 = chat_msgInCallDrawable
            r8 = 2131165330(0x7var_, float:1.7944874E38)
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            r5[r6] = r8
            android.graphics.drawable.Drawable[] r5 = chat_msgInCallSelectedDrawable
            r8 = 2131165330(0x7var_, float:1.7944874E38)
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            r5[r6] = r8
            android.graphics.drawable.Drawable[] r5 = chat_msgOutCallDrawable
            r8 = 2131165330(0x7var_, float:1.7944874E38)
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            r5[r6] = r8
            android.graphics.drawable.Drawable[] r5 = chat_msgOutCallSelectedDrawable
            r8 = 2131165330(0x7var_, float:1.7944874E38)
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            r5[r6] = r8
            android.graphics.drawable.Drawable[] r5 = chat_msgInCallDrawable
            r8 = 2131165329(0x7var_, float:1.7944872E38)
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            r5[r7] = r8
            android.graphics.drawable.Drawable[] r5 = chat_msgInCallSelectedDrawable
            r8 = 2131165329(0x7var_, float:1.7944872E38)
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            r5[r7] = r8
            android.graphics.drawable.Drawable[] r5 = chat_msgOutCallDrawable
            r8 = 2131165329(0x7var_, float:1.7944872E38)
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            r5[r7] = r8
            android.graphics.drawable.Drawable[] r5 = chat_msgOutCallSelectedDrawable
            r8 = 2131165329(0x7var_, float:1.7944872E38)
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            r5[r7] = r8
            r5 = 2131165328(0x7var_, float:1.794487E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgCallUpGreenDrawable = r5
            r5 = 2131165327(0x7var_f, float:1.7944868E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgCallDownRedDrawable = r5
            r5 = 2131165327(0x7var_f, float:1.7944868E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgCallDownGreenDrawable = r5
            r5 = 0
        L_0x02f3:
            if (r5 >= r3) goto L_0x0334
            android.graphics.drawable.Drawable[] r8 = chat_pollCheckDrawable
            r9 = 2131166087(0x7var_, float:1.794641E38)
            android.graphics.drawable.Drawable r9 = r4.getDrawable(r9)
            android.graphics.drawable.Drawable r9 = r9.mutate()
            r8[r5] = r9
            android.graphics.drawable.Drawable[] r8 = chat_pollCrossDrawable
            r9 = 2131166088(0x7var_, float:1.7946411E38)
            android.graphics.drawable.Drawable r9 = r4.getDrawable(r9)
            android.graphics.drawable.Drawable r9 = r9.mutate()
            r8[r5] = r9
            android.graphics.drawable.Drawable[] r8 = chat_pollHintDrawable
            r9 = 2131165719(0x7var_, float:1.7945663E38)
            android.graphics.drawable.Drawable r9 = r4.getDrawable(r9)
            android.graphics.drawable.Drawable r9 = r9.mutate()
            r8[r5] = r9
            android.graphics.drawable.Drawable[] r8 = chat_psaHelpDrawable
            r9 = 2131165888(0x7var_c0, float:1.7946006E38)
            android.graphics.drawable.Drawable r9 = r4.getDrawable(r9)
            android.graphics.drawable.Drawable r9 = r9.mutate()
            r8[r5] = r9
            int r5 = r5 + 1
            goto L_0x02f3
        L_0x0334:
            r5 = 2131165467(0x7var_b, float:1.7945152E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            calllog_msgCallUpRedDrawable = r5
            r5 = 2131165467(0x7var_b, float:1.7945152E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            calllog_msgCallUpGreenDrawable = r5
            r5 = 2131165470(0x7var_e, float:1.7945158E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            calllog_msgCallDownRedDrawable = r5
            r5 = 2131165470(0x7var_e, float:1.7945158E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            calllog_msgCallDownGreenDrawable = r5
            r5 = 2131165582(0x7var_e, float:1.7945385E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_msgAvatarLiveLocationDrawable = r5
            r5 = 2131165283(0x7var_, float:1.7944779E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_inlineResultFile = r5
            r5 = 2131165288(0x7var_, float:1.7944789E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_inlineResultAudio = r5
            r5 = 2131165287(0x7var_, float:1.7944787E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_inlineResultLocation = r5
            r5 = 2131165594(0x7var_a, float:1.794541E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_redLocationIcon = r5
            r5 = 2131165286(0x7var_, float:1.7944785E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_botLinkDrawable = r5
            r5 = 2131165285(0x7var_, float:1.7944783E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_botInlineDrawable = r5
            r5 = 2131165282(0x7var_, float:1.7944777E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_botCardDrawable = r5
            r5 = 2131165289(0x7var_, float:1.794479E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_botWebViewDrawable = r5
            r5 = 2131165284(0x7var_, float:1.794478E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_botInviteDrawable = r5
            r5 = 2131165817(0x7var_, float:1.7945862E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_commentDrawable = r5
            r5 = 2131165818(0x7var_a, float:1.7945864E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_commentStickerDrawable = r5
            r5 = 2131165641(0x7var_c9, float:1.7945505E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_commentArrowDrawable = r5
            r5 = 2131165446(0x7var_, float:1.794511E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_contextResult_shadowUnderSwitchDrawable = r5
            org.telegram.ui.Components.RLottieDrawable[] r5 = chat_attachButtonDrawables
            org.telegram.ui.Components.RLottieDrawable r8 = new org.telegram.ui.Components.RLottieDrawable
            r9 = 2131558404(0x7f0d0004, float:1.8742123E38)
            r10 = 1104150528(0x41d00000, float:26.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r10)
            java.lang.String r13 = "attach_gallery"
            r8.<init>(r9, r13, r11, r12)
            r5[r6] = r8
            org.telegram.ui.Components.RLottieDrawable[] r5 = chat_attachButtonDrawables
            org.telegram.ui.Components.RLottieDrawable r8 = new org.telegram.ui.Components.RLottieDrawable
            r9 = 2131558406(0x7f0d0006, float:1.8742127E38)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r10)
            java.lang.String r13 = "attach_music"
            r8.<init>(r9, r13, r11, r12)
            r5[r7] = r8
            org.telegram.ui.Components.RLottieDrawable[] r5 = chat_attachButtonDrawables
            org.telegram.ui.Components.RLottieDrawable r8 = new org.telegram.ui.Components.RLottieDrawable
            r9 = 2131558403(0x7f0d0003, float:1.874212E38)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r10)
            java.lang.String r13 = "attach_file"
            r8.<init>(r9, r13, r11, r12)
            r5[r3] = r8
            org.telegram.ui.Components.RLottieDrawable[] r5 = chat_attachButtonDrawables
            org.telegram.ui.Components.RLottieDrawable r8 = new org.telegram.ui.Components.RLottieDrawable
            r9 = 2131558402(0x7f0d0002, float:1.8742119E38)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r10)
            java.lang.String r13 = "attach_contact"
            r8.<init>(r9, r13, r11, r12)
            r9 = 3
            r5[r9] = r8
            org.telegram.ui.Components.RLottieDrawable[] r5 = chat_attachButtonDrawables
            r8 = 4
            org.telegram.ui.Components.RLottieDrawable r11 = new org.telegram.ui.Components.RLottieDrawable
            r12 = 2131558405(0x7f0d0005, float:1.8742125E38)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r10)
            java.lang.String r15 = "attach_location"
            r11.<init>(r12, r15, r13, r14)
            r5[r8] = r11
            org.telegram.ui.Components.RLottieDrawable[] r5 = chat_attachButtonDrawables
            r8 = 5
            org.telegram.ui.Components.RLottieDrawable r11 = new org.telegram.ui.Components.RLottieDrawable
            r12 = 2131558407(0x7f0d0007, float:1.8742129E38)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            java.lang.String r14 = "attach_poll"
            r11.<init>(r12, r14, r13, r10)
            r5[r8] = r11
            r5 = 2131166009(0x7var_, float:1.7946251E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_attachEmptyDrawable = r5
            r5 = 2131166141(0x7var_bd, float:1.7946519E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_shareIconDrawable = r5
            r5 = 2131165396(0x7var_d4, float:1.7945008E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_replyIconDrawable = r5
            r5 = 2131165616(0x7var_b0, float:1.7945454E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            chat_goIconDrawable = r5
            r5 = 1073741824(0x40000000, float:2.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.graphics.RectF r8 = new android.graphics.RectF
            r8.<init>()
            android.graphics.Path[] r10 = chat_filePath
            android.graphics.Path r11 = new android.graphics.Path
            r11.<init>()
            r10[r6] = r11
            android.graphics.Path[] r10 = chat_filePath
            r10 = r10[r6]
            r11 = 1088421888(0x40e00000, float:7.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r12 = (float) r12
            r10.moveTo(r11, r12)
            android.graphics.Path[] r10 = chat_filePath
            r10 = r10[r6]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r11 = (float) r11
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r12 = (float) r12
            r10.lineTo(r11, r12)
            android.graphics.Path[] r10 = chat_filePath
            r10 = r10[r6]
            r11 = 1101529088(0x41a80000, float:21.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            r12 = 1092616192(0x41200000, float:10.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r10.lineTo(r11, r12)
            android.graphics.Path[] r10 = chat_filePath
            r10 = r10[r6]
            r11 = 1101529088(0x41a80000, float:21.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            r12 = 1101004800(0x41a00000, float:20.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r10.lineTo(r11, r12)
            r10 = 1101529088(0x41a80000, float:21.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r11 = r5 * 2
            int r10 = r10 - r11
            float r10 = (float) r10
            r12 = 1100480512(0x41980000, float:19.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r12 = r12 - r5
            float r12 = (float) r12
            r13 = 1101529088(0x41a80000, float:21.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            r14 = 1100480512(0x41980000, float:19.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r14 = r14 + r5
            float r14 = (float) r14
            r8.set(r10, r12, r13, r14)
            android.graphics.Path[] r10 = chat_filePath
            r10 = r10[r6]
            r12 = 0
            r13 = 1119092736(0x42b40000, float:90.0)
            r10.arcTo(r8, r12, r13, r6)
            android.graphics.Path[] r10 = chat_filePath
            r10 = r10[r6]
            r12 = 1086324736(0x40CLASSNAME, float:6.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r13 = 1101529088(0x41a80000, float:21.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            r10.lineTo(r12, r13)
            r10 = 1084227584(0x40a00000, float:5.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r12 = (float) r12
            r13 = 1100480512(0x41980000, float:19.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r13 = r13 - r5
            float r13 = (float) r13
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r14 = r14 + r11
            float r14 = (float) r14
            r15 = 1100480512(0x41980000, float:19.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r15 = r15 + r5
            float r5 = (float) r15
            r8.set(r12, r13, r14, r5)
            android.graphics.Path[] r5 = chat_filePath
            r5 = r5[r6]
            r12 = 1119092736(0x42b40000, float:90.0)
            r13 = 1119092736(0x42b40000, float:90.0)
            r5.arcTo(r8, r12, r13, r6)
            android.graphics.Path[] r5 = chat_filePath
            r5 = r5[r6]
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r12 = (float) r12
            r13 = 1082130432(0x40800000, float:4.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            r5.lineTo(r12, r13)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r5 = (float) r5
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r12 = (float) r12
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r13 = r13 + r11
            float r13 = (float) r13
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r14 = r14 + r11
            float r11 = (float) r14
            r8.set(r5, r12, r13, r11)
            android.graphics.Path[] r5 = chat_filePath
            r5 = r5[r6]
            r11 = 1127481344(0x43340000, float:180.0)
            r12 = 1119092736(0x42b40000, float:90.0)
            r5.arcTo(r8, r11, r12, r6)
            android.graphics.Path[] r5 = chat_filePath
            r5 = r5[r6]
            r5.close()
            android.graphics.Path[] r5 = chat_filePath
            android.graphics.Path r8 = new android.graphics.Path
            r8.<init>()
            r5[r7] = r8
            android.graphics.Path[] r5 = chat_filePath
            r5 = r5[r7]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r8 = (float) r8
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            r5.moveTo(r8, r10)
            android.graphics.Path[] r5 = chat_filePath
            r5 = r5[r7]
            r8 = 1100480512(0x41980000, float:19.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r8 = (float) r8
            r10 = 1092616192(0x41200000, float:10.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            r5.lineTo(r8, r10)
            android.graphics.Path[] r5 = chat_filePath
            r5 = r5[r7]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r8 = (float) r8
            r10 = 1092616192(0x41200000, float:10.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            r5.lineTo(r8, r10)
            android.graphics.Path[] r5 = chat_filePath
            r5 = r5[r7]
            r5.close()
            r5 = 2131165293(0x7var_d, float:1.79448E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_flameIcon = r5
            r5 = 2131165915(0x7var_db, float:1.794606E38)
            android.graphics.drawable.Drawable r5 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            chat_gifIcon = r5
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r6]
            r8 = 1110441984(0x42300000, float:44.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r11 = 2131165918(0x7var_de, float:1.7946067E38)
            org.telegram.ui.Components.CombinedDrawable r10 = createCircleDrawableWithIcon(r10, r11)
            r5[r6] = r10
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r6]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r10 = createCircleDrawableWithIcon(r10, r11)
            r5[r7] = r10
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r7]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r12 = 2131165917(0x7var_dd, float:1.7946065E38)
            org.telegram.ui.Components.CombinedDrawable r10 = createCircleDrawableWithIcon(r10, r12)
            r5[r6] = r10
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r7]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r10 = createCircleDrawableWithIcon(r10, r12)
            r5[r7] = r10
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r3]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r12 = 2131165916(0x7var_dc, float:1.7946063E38)
            org.telegram.ui.Components.CombinedDrawable r10 = createCircleDrawableWithIcon(r10, r12)
            r5[r6] = r10
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r3]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r10 = createCircleDrawableWithIcon(r10, r12)
            r5[r7] = r10
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r9]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r13 = 2131165914(0x7var_da, float:1.7946059E38)
            org.telegram.ui.Components.CombinedDrawable r10 = createCircleDrawableWithIcon(r10, r13)
            r5[r6] = r10
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r5 = r5[r9]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r10 = createCircleDrawableWithIcon(r10, r13)
            r5[r7] = r10
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r10 = 4
            r5 = r5[r10]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r13 = 2131165913(0x7var_d9, float:1.7946057E38)
            org.telegram.ui.Components.CombinedDrawable r10 = createCircleDrawableWithIcon(r10, r13)
            r5[r6] = r10
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r10 = 4
            r5 = r5[r10]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r10 = createCircleDrawableWithIcon(r10, r13)
            r5[r7] = r10
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r10 = 5
            r5 = r5[r10]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r10 = createCircleDrawableWithIcon(r10, r11)
            r5[r6] = r10
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r10 = 5
            r5 = r5[r10]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r10 = createCircleDrawableWithIcon(r10, r11)
            r5[r7] = r10
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r10 = 6
            r5 = r5[r10]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r14 = 2131165917(0x7var_dd, float:1.7946065E38)
            org.telegram.ui.Components.CombinedDrawable r10 = createCircleDrawableWithIcon(r10, r14)
            r5[r6] = r10
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r10 = 6
            r5 = r5[r10]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r10 = createCircleDrawableWithIcon(r10, r14)
            r5[r7] = r10
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r10 = 7
            r5 = r5[r10]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r10 = createCircleDrawableWithIcon(r10, r12)
            r5[r6] = r10
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r10 = 7
            r5 = r5[r10]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r10 = createCircleDrawableWithIcon(r10, r12)
            r5[r7] = r10
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r10 = 8
            r5 = r5[r10]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r14 = 2131165914(0x7var_da, float:1.7946059E38)
            org.telegram.ui.Components.CombinedDrawable r10 = createCircleDrawableWithIcon(r10, r14)
            r5[r6] = r10
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r10 = 8
            r5 = r5[r10]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r10 = createCircleDrawableWithIcon(r10, r14)
            r5[r7] = r10
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r10 = 9
            r5 = r5[r10]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r10 = createCircleDrawableWithIcon(r10, r13)
            r5[r6] = r10
            android.graphics.drawable.Drawable[][] r5 = chat_fileStatesDrawable
            r10 = 9
            r5 = r5[r10]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            org.telegram.ui.Components.CombinedDrawable r10 = createCircleDrawableWithIcon(r10, r13)
            r5[r7] = r10
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r5 = r5[r6]
            r10 = 1111490560(0x42400000, float:48.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r10)
            org.telegram.ui.Components.CombinedDrawable r14 = createCircleDrawableWithIcon(r14, r12)
            r5[r6] = r14
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r5 = r5[r6]
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r10)
            org.telegram.ui.Components.CombinedDrawable r14 = createCircleDrawableWithIcon(r14, r12)
            r5[r7] = r14
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r5 = r5[r7]
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r10)
            org.telegram.ui.Components.CombinedDrawable r14 = createCircleDrawableWithIcon(r14, r13)
            r5[r6] = r14
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r5 = r5[r7]
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r10)
            org.telegram.ui.Components.CombinedDrawable r14 = createCircleDrawableWithIcon(r14, r13)
            r5[r7] = r14
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r5 = r5[r3]
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r15 = 2131165915(0x7var_db, float:1.794606E38)
            org.telegram.ui.Components.CombinedDrawable r14 = createCircleDrawableWithIcon(r14, r15)
            r5[r6] = r14
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r5 = r5[r3]
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r10)
            org.telegram.ui.Components.CombinedDrawable r14 = createCircleDrawableWithIcon(r14, r15)
            r5[r7] = r14
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r5 = r5[r9]
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r10)
            org.telegram.ui.Components.CombinedDrawable r14 = createCircleDrawableWithIcon(r14, r11)
            r5[r6] = r14
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r5 = r5[r9]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r10)
            org.telegram.ui.Components.CombinedDrawable r9 = createCircleDrawableWithIcon(r9, r11)
            r5[r7] = r9
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r9 = 4
            r9 = r5[r9]
            r11 = 4
            r5 = r5[r11]
            r11 = 2131165293(0x7var_d, float:1.79448E38)
            android.graphics.drawable.Drawable r11 = r4.getDrawable(r11)
            r5[r7] = r11
            r9[r6] = r11
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r9 = 5
            r9 = r5[r9]
            r11 = 5
            r5 = r5[r11]
            r11 = 2131165346(0x7var_a2, float:1.7944907E38)
            android.graphics.drawable.Drawable r11 = r4.getDrawable(r11)
            r5[r7] = r11
            r9[r6] = r11
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r9 = 6
            r9 = r5[r9]
            r11 = 6
            r5 = r5[r11]
            r11 = 2131166053(0x7var_, float:1.794634E38)
            android.graphics.drawable.Drawable r11 = r4.getDrawable(r11)
            r5[r7] = r11
            r9[r6] = r11
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r9 = 7
            r5 = r5[r9]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r10)
            org.telegram.ui.Components.CombinedDrawable r9 = createCircleDrawableWithIcon(r9, r12)
            r5[r6] = r9
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r9 = 7
            r5 = r5[r9]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r10)
            org.telegram.ui.Components.CombinedDrawable r9 = createCircleDrawableWithIcon(r9, r12)
            r5[r7] = r9
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r9 = 8
            r5 = r5[r9]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r10)
            org.telegram.ui.Components.CombinedDrawable r9 = createCircleDrawableWithIcon(r9, r13)
            r5[r6] = r9
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r9 = 8
            r5 = r5[r9]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r10)
            org.telegram.ui.Components.CombinedDrawable r9 = createCircleDrawableWithIcon(r9, r13)
            r5[r7] = r9
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r9 = 10
            r5 = r5[r9]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r10)
            org.telegram.ui.Components.CombinedDrawable r9 = createCircleDrawableWithIcon(r9, r12)
            r5[r6] = r9
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r9 = 10
            r5 = r5[r9]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r10)
            org.telegram.ui.Components.CombinedDrawable r9 = createCircleDrawableWithIcon(r9, r12)
            r5[r7] = r9
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r9 = 11
            r5 = r5[r9]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r10)
            org.telegram.ui.Components.CombinedDrawable r9 = createCircleDrawableWithIcon(r9, r13)
            r5[r6] = r9
            android.graphics.drawable.Drawable[][] r5 = chat_photoStatesDrawables
            r9 = 11
            r5 = r5[r9]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r10)
            org.telegram.ui.Components.CombinedDrawable r9 = createCircleDrawableWithIcon(r9, r13)
            r5[r7] = r9
            android.graphics.drawable.Drawable[] r5 = chat_contactDrawable
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r10 = 2131165689(0x7var_f9, float:1.7945602E38)
            org.telegram.ui.Components.CombinedDrawable r9 = createCircleDrawableWithIcon(r9, r10)
            r5[r6] = r9
            android.graphics.drawable.Drawable[] r5 = chat_contactDrawable
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r9 = 2131165689(0x7var_f9, float:1.7945602E38)
            org.telegram.ui.Components.CombinedDrawable r8 = createCircleDrawableWithIcon(r8, r9)
            r5[r7] = r8
            android.graphics.drawable.Drawable[] r5 = chat_locationDrawable
            r8 = 2131165789(0x7var_d, float:1.7945805E38)
            android.graphics.drawable.Drawable r8 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            r5[r6] = r8
            android.graphics.drawable.Drawable[] r5 = chat_locationDrawable
            r8 = 2131165789(0x7var_d, float:1.7945805E38)
            android.graphics.drawable.Drawable r4 = r4.getDrawable(r8)
            android.graphics.drawable.Drawable r4 = r4.mutate()
            r5[r7] = r4
            android.content.res.Resources r4 = r17.getResources()
            r5 = 2131165369(0x7var_b9, float:1.7944953E38)
            android.graphics.drawable.Drawable r4 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r4 = r4.mutate()
            chat_composeShadowDrawable = r4
            android.content.res.Resources r4 = r17.getResources()
            r5 = 2131166143(0x7var_bf, float:1.7946523E38)
            android.graphics.drawable.Drawable r4 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r4 = r4.mutate()
            chat_composeShadowRoundDrawable = r4
            r4 = 0
            int r5 = org.telegram.messenger.AndroidUtilities.roundMessageSize     // Catch:{ all -> 0x0948 }
            r8 = 1086324736(0x40CLASSNAME, float:6.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)     // Catch:{ all -> 0x0948 }
            int r5 = r5 + r8
            android.graphics.Bitmap$Config r8 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0948 }
            android.graphics.Bitmap r8 = android.graphics.Bitmap.createBitmap(r5, r5, r8)     // Catch:{ all -> 0x0948 }
            android.graphics.Canvas r9 = new android.graphics.Canvas     // Catch:{ all -> 0x0948 }
            r9.<init>(r8)     // Catch:{ all -> 0x0948 }
            android.graphics.Paint r10 = new android.graphics.Paint     // Catch:{ all -> 0x0948 }
            r10.<init>(r7)     // Catch:{ all -> 0x0948 }
            r10.setColor(r6)     // Catch:{ all -> 0x0948 }
            android.graphics.Paint$Style r11 = android.graphics.Paint.Style.FILL     // Catch:{ all -> 0x0948 }
            r10.setStyle(r11)     // Catch:{ all -> 0x0948 }
            android.graphics.PorterDuffXfermode r11 = new android.graphics.PorterDuffXfermode     // Catch:{ all -> 0x0948 }
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.CLEAR     // Catch:{ all -> 0x0948 }
            r11.<init>(r12)     // Catch:{ all -> 0x0948 }
            r10.setXfermode(r11)     // Catch:{ all -> 0x0948 }
            android.graphics.Paint r11 = new android.graphics.Paint     // Catch:{ all -> 0x0948 }
            r11.<init>(r7)     // Catch:{ all -> 0x0948 }
            r12 = 1082130432(0x40800000, float:4.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)     // Catch:{ all -> 0x0948 }
            float r12 = (float) r12     // Catch:{ all -> 0x0948 }
            r13 = 0
            r14 = 0
            r15 = 1593835520(0x5var_, float:9.223372E18)
            r11.setShadowLayer(r12, r13, r14, r15)     // Catch:{ all -> 0x0948 }
            r12 = 0
        L_0x091d:
            if (r12 >= r3) goto L_0x093e
            int r13 = r5 / 2
            float r13 = (float) r13     // Catch:{ all -> 0x0948 }
            int r14 = r5 / 2
            float r14 = (float) r14     // Catch:{ all -> 0x0948 }
            int r15 = org.telegram.messenger.AndroidUtilities.roundMessageSize     // Catch:{ all -> 0x0948 }
            int r15 = r15 / r3
            r16 = 1065353216(0x3var_, float:1.0)
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r16)     // Catch:{ all -> 0x0948 }
            int r15 = r15 - r16
            float r15 = (float) r15     // Catch:{ all -> 0x0948 }
            if (r12 != 0) goto L_0x0935
            r1 = r11
            goto L_0x0936
        L_0x0935:
            r1 = r10
        L_0x0936:
            r9.drawCircle(r13, r14, r15, r1)     // Catch:{ all -> 0x0948 }
            int r12 = r12 + 1
            r1 = 1077936128(0x40400000, float:3.0)
            goto L_0x091d
        L_0x093e:
            r9.setBitmap(r4)     // Catch:{ Exception -> 0x0941 }
        L_0x0941:
            android.graphics.drawable.BitmapDrawable r1 = new android.graphics.drawable.BitmapDrawable     // Catch:{ all -> 0x0948 }
            r1.<init>(r8)     // Catch:{ all -> 0x0948 }
            chat_roundVideoShadow = r1     // Catch:{ all -> 0x0948 }
        L_0x0948:
            java.util.HashMap<java.lang.String, android.graphics.drawable.Drawable> r1 = defaultChatDrawables
            r1.clear()
            java.util.HashMap<java.lang.String, java.lang.String> r1 = defaultChatDrawableColorKeys
            r1.clear()
            android.graphics.drawable.Drawable r1 = chat_botInlineDrawable
            java.lang.String r5 = "drawableBotInline"
            java.lang.String r8 = "chat_serviceIcon"
            addChatDrawable(r5, r1, r8)
            android.graphics.drawable.Drawable r1 = chat_botWebViewDrawable
            java.lang.String r5 = "drawableBotWebView"
            addChatDrawable(r5, r1, r8)
            android.graphics.drawable.Drawable r1 = chat_botLinkDrawable
            java.lang.String r5 = "drawableBotLink"
            addChatDrawable(r5, r1, r8)
            android.graphics.drawable.Drawable r1 = chat_botInviteDrawable
            java.lang.String r5 = "drawable_botInvite"
            addChatDrawable(r5, r1, r8)
            android.graphics.drawable.Drawable r1 = chat_goIconDrawable
            java.lang.String r5 = "drawableGoIcon"
            addChatDrawable(r5, r1, r8)
            android.graphics.drawable.Drawable r1 = chat_commentStickerDrawable
            java.lang.String r5 = "drawableCommentSticker"
            addChatDrawable(r5, r1, r8)
            android.graphics.drawable.Drawable r1 = chat_msgErrorDrawable
            java.lang.String r5 = "drawableMsgError"
            java.lang.String r9 = "chat_sentErrorIcon"
            addChatDrawable(r5, r1, r9)
            org.telegram.ui.ActionBar.Theme$MessageDrawable r1 = chat_msgInDrawable
            java.lang.String r5 = "drawableMsgIn"
            addChatDrawable(r5, r1, r4)
            org.telegram.ui.ActionBar.Theme$MessageDrawable r1 = chat_msgInSelectedDrawable
            java.lang.String r5 = "drawableMsgInSelected"
            addChatDrawable(r5, r1, r4)
            org.telegram.ui.ActionBar.Theme$MessageDrawable r1 = chat_msgInMediaDrawable
            java.lang.String r5 = "drawableMsgInMedia"
            addChatDrawable(r5, r1, r4)
            org.telegram.ui.ActionBar.Theme$MessageDrawable r1 = chat_msgInMediaSelectedDrawable
            java.lang.String r5 = "drawableMsgInMediaSelected"
            addChatDrawable(r5, r1, r4)
            org.telegram.ui.ActionBar.Theme$MessageDrawable r1 = chat_msgOutDrawable
            java.lang.String r5 = "drawableMsgOut"
            addChatDrawable(r5, r1, r4)
            org.telegram.ui.ActionBar.Theme$MessageDrawable r1 = chat_msgOutSelectedDrawable
            java.lang.String r5 = "drawableMsgOutSelected"
            addChatDrawable(r5, r1, r4)
            org.telegram.ui.ActionBar.Theme$MessageDrawable r1 = chat_msgOutMediaDrawable
            java.lang.String r5 = "drawableMsgOutMedia"
            addChatDrawable(r5, r1, r4)
            org.telegram.ui.ActionBar.Theme$MessageDrawable r1 = chat_msgOutMediaSelectedDrawable
            java.lang.String r5 = "drawableMsgOutMediaSelected"
            addChatDrawable(r5, r1, r4)
            android.graphics.drawable.Drawable[] r1 = chat_msgOutCallDrawable
            r1 = r1[r6]
            java.lang.String r4 = "drawableMsgOutCallAudio"
            java.lang.String r5 = "chat_outInstant"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable[] r1 = chat_msgOutCallSelectedDrawable
            r1 = r1[r6]
            java.lang.String r4 = "drawableMsgOutCallAudioSelected"
            java.lang.String r5 = "chat_outInstantSelected"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable[] r1 = chat_msgOutCallDrawable
            r1 = r1[r7]
            java.lang.String r4 = "drawableMsgOutCallVideo"
            java.lang.String r5 = "chat_outInstant"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable[] r1 = chat_msgOutCallSelectedDrawable
            r1 = r1[r7]
            java.lang.String r4 = "drawableMsgOutCallVideo"
            java.lang.String r5 = "chat_outInstantSelected"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable r1 = chat_msgOutCheckDrawable
            java.lang.String r4 = "drawableMsgOutCheck"
            java.lang.String r5 = "chat_outSentCheck"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable r1 = chat_msgOutCheckSelectedDrawable
            java.lang.String r4 = "drawableMsgOutCheckSelected"
            java.lang.String r5 = "chat_outSentCheckSelected"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable r1 = chat_msgOutCheckReadDrawable
            java.lang.String r4 = "drawableMsgOutCheckRead"
            java.lang.String r5 = "chat_outSentCheckRead"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable r1 = chat_msgOutCheckReadSelectedDrawable
            java.lang.String r4 = "drawableMsgOutCheckReadSelected"
            java.lang.String r5 = "chat_outSentCheckReadSelected"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable r1 = chat_msgOutHalfCheckDrawable
            java.lang.String r4 = "drawableMsgOutHalfCheck"
            java.lang.String r5 = "chat_outSentCheckRead"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable r1 = chat_msgOutHalfCheckSelectedDrawable
            java.lang.String r4 = "drawableMsgOutHalfCheckSelected"
            java.lang.String r5 = "chat_outSentCheckReadSelected"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable r1 = chat_msgOutInstantDrawable
            java.lang.String r4 = "drawableMsgOutInstant"
            java.lang.String r5 = "chat_outInstant"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable r1 = chat_msgOutMenuDrawable
            java.lang.String r4 = "drawableMsgOutMenu"
            java.lang.String r5 = "chat_outMenu"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable r1 = chat_msgOutMenuSelectedDrawable
            java.lang.String r4 = "drawableMsgOutMenuSelected"
            java.lang.String r5 = "chat_outMenuSelected"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable r1 = chat_msgOutPinnedDrawable
            java.lang.String r4 = "drawableMsgOutPinned"
            java.lang.String r5 = "chat_outViews"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable r1 = chat_msgOutPinnedSelectedDrawable
            java.lang.String r4 = "drawableMsgOutPinnedSelected"
            java.lang.String r5 = "chat_outViewsSelected"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable r1 = chat_msgOutRepliesDrawable
            java.lang.String r4 = "drawableMsgOutReplies"
            java.lang.String r5 = "chat_outViews"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable r1 = chat_msgOutRepliesSelectedDrawable
            java.lang.String r4 = "drawableMsgOutReplies"
            java.lang.String r5 = "chat_outViewsSelected"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable r1 = chat_msgOutViewsDrawable
            java.lang.String r4 = "drawableMsgOutViews"
            java.lang.String r5 = "chat_outViews"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable r1 = chat_msgOutViewsSelectedDrawable
            java.lang.String r4 = "drawableMsgOutViewsSelected"
            java.lang.String r5 = "chat_outViewsSelected"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable r1 = chat_msgStickerCheckDrawable
            java.lang.String r4 = "drawableMsgStickerCheck"
            java.lang.String r5 = "chat_serviceText"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable r1 = chat_msgStickerHalfCheckDrawable
            java.lang.String r4 = "drawableMsgStickerHalfCheck"
            java.lang.String r5 = "chat_serviceText"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable r1 = chat_msgStickerPinnedDrawable
            java.lang.String r4 = "drawableMsgStickerPinned"
            java.lang.String r5 = "chat_serviceText"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable r1 = chat_msgStickerRepliesDrawable
            java.lang.String r4 = "drawableMsgStickerReplies"
            java.lang.String r5 = "chat_serviceText"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable r1 = chat_msgStickerViewsDrawable
            java.lang.String r4 = "drawableMsgStickerViews"
            java.lang.String r5 = "chat_serviceText"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable r1 = chat_replyIconDrawable
            java.lang.String r4 = "drawableReplyIcon"
            addChatDrawable(r4, r1, r8)
            android.graphics.drawable.Drawable r1 = chat_shareIconDrawable
            java.lang.String r4 = "drawableShareIcon"
            addChatDrawable(r4, r1, r8)
            android.graphics.drawable.Drawable r1 = chat_muteIconDrawable
            java.lang.String r4 = "drawableMuteIcon"
            java.lang.String r5 = "chat_muteIcon"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable r1 = chat_lockIconDrawable
            java.lang.String r4 = "drawableLockIcon"
            java.lang.String r5 = "chat_lockIcon"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable[] r1 = chat_pollHintDrawable
            r1 = r1[r7]
            java.lang.String r4 = "drawable_chat_pollHintDrawableOut"
            java.lang.String r5 = "chat_outPreviewInstantText"
            addChatDrawable(r4, r1, r5)
            android.graphics.drawable.Drawable[] r1 = chat_pollHintDrawable
            r1 = r1[r6]
            java.lang.String r4 = "drawable_chat_pollHintDrawableIn"
            java.lang.String r5 = "chat_inPreviewInstantText"
            addChatDrawable(r4, r1, r5)
            applyChatTheme(r0, r6)
        L_0x0ad8:
            if (r0 != 0) goto L_0x0CLASSNAME
            android.graphics.Paint r0 = chat_botProgressPaint
            if (r0 == 0) goto L_0x0CLASSNAME
            r1 = 1073741824(0x40000000, float:2.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r0.setStrokeWidth(r1)
            android.text.TextPaint r0 = chat_infoPaint
            r1 = 1094713344(0x41400000, float:12.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r4 = (float) r4
            r0.setTextSize(r4)
            android.text.TextPaint r0 = chat_stickerCommentCountPaint
            r4 = 1093664768(0x41300000, float:11.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r0.setTextSize(r4)
            android.text.TextPaint r0 = chat_docNamePaint
            r4 = 1097859072(0x41700000, float:15.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r5 = (float) r5
            r0.setTextSize(r5)
            android.text.TextPaint r0 = chat_locationTitlePaint
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r5 = (float) r5
            r0.setTextSize(r5)
            android.text.TextPaint r0 = chat_locationAddressPaint
            r5 = 1095761920(0x41500000, float:13.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r6 = (float) r6
            r0.setTextSize(r6)
            android.text.TextPaint r0 = chat_audioTimePaint
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r6 = (float) r6
            r0.setTextSize(r6)
            android.text.TextPaint r0 = chat_livePaint
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r6 = (float) r6
            r0.setTextSize(r6)
            android.text.TextPaint r0 = chat_audioTitlePaint
            r6 = 1098907648(0x41800000, float:16.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            r0.setTextSize(r6)
            android.text.TextPaint r0 = chat_audioPerformerPaint
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r6 = (float) r6
            r0.setTextSize(r6)
            android.text.TextPaint r0 = chat_botButtonPaint
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r6 = (float) r6
            r0.setTextSize(r6)
            android.text.TextPaint r0 = chat_contactNamePaint
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r6 = (float) r6
            r0.setTextSize(r6)
            android.text.TextPaint r0 = chat_contactPhonePaint
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r6 = (float) r6
            r0.setTextSize(r6)
            android.text.TextPaint r0 = chat_durationPaint
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r6 = (float) r6
            r0.setTextSize(r6)
            android.text.TextPaint r0 = chat_timePaint
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r0.setTextSize(r1)
            android.text.TextPaint r0 = chat_adminPaint
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r1 = (float) r1
            r0.setTextSize(r1)
            android.text.TextPaint r0 = chat_namePaint
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r1 = (float) r1
            r0.setTextSize(r1)
            android.text.TextPaint r0 = chat_forwardNamePaint
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r1 = (float) r1
            r0.setTextSize(r1)
            android.text.TextPaint r0 = chat_replyNamePaint
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r1 = (float) r1
            r0.setTextSize(r1)
            android.text.TextPaint r0 = chat_replyTextPaint
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
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
            android.text.TextPaint r0 = chat_actionTextPaint
            r1 = 16
            int r2 = org.telegram.messenger.SharedConfig.fontSize
            int r1 = java.lang.Math.max(r1, r2)
            int r1 = r1 - r3
            float r1 = (float) r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r0.setTextSize(r1)
            android.text.TextPaint r0 = chat_contextResult_titleTextPaint
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r4)
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
        L_0x0CLASSNAME:
            return
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

    public static void applyChatTheme(boolean z, boolean z2) {
        if (chat_msgTextPaint != null && chat_msgInDrawable != null && !z) {
            chat_gamePaint.setColor(getColor("chat_previewGameText"));
            chat_durationPaint.setColor(getColor("chat_previewDurationText"));
            chat_botButtonPaint.setColor(getColor("chat_botButtonText"));
            chat_urlPaint.setColor(getColor("chat_linkSelectBackground"));
            chat_outUrlPaint.setColor(getColor("chat_outLinkSelectBackground"));
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
            setDrawableColorByKey(chat_msgMediaCheckDrawable, "chat_mediaSentCheck");
            setDrawableColorByKey(chat_msgMediaHalfCheckDrawable, "chat_mediaSentCheck");
            setDrawableColorByKey(chat_msgStickerCheckDrawable, "chat_serviceText");
            setDrawableColorByKey(chat_msgStickerHalfCheckDrawable, "chat_serviceText");
            setDrawableColorByKey(chat_msgStickerViewsDrawable, "chat_serviceText");
            setDrawableColorByKey(chat_msgStickerRepliesDrawable, "chat_serviceText");
            setDrawableColorByKey(chat_shareIconDrawable, "chat_serviceIcon");
            setDrawableColorByKey(chat_replyIconDrawable, "chat_serviceIcon");
            setDrawableColorByKey(chat_goIconDrawable, "chat_serviceIcon");
            setDrawableColorByKey(chat_botInlineDrawable, "chat_serviceIcon");
            setDrawableColorByKey(chat_botWebViewDrawable, "chat_serviceIcon");
            setDrawableColorByKey(chat_botInviteDrawable, "chat_serviceIcon");
            setDrawableColorByKey(chat_botLinkDrawable, "chat_serviceIcon");
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
            for (int i3 = 0; i3 < 5; i3++) {
                setCombinedDrawableColor(chat_fileStatesDrawable[i3][0], getColor("chat_outLoader"), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[i3][0], getColor("chat_outMediaIcon"), true);
                setCombinedDrawableColor(chat_fileStatesDrawable[i3][1], getColor("chat_outLoaderSelected"), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[i3][1], getColor("chat_outMediaIconSelected"), true);
                int i4 = i3 + 5;
                setCombinedDrawableColor(chat_fileStatesDrawable[i4][0], getColor("chat_inLoader"), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[i4][0], getColor("chat_inMediaIcon"), true);
                setCombinedDrawableColor(chat_fileStatesDrawable[i4][1], getColor("chat_inLoaderSelected"), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[i4][1], getColor("chat_inMediaIconSelected"), true);
            }
            for (int i5 = 0; i5 < 4; i5++) {
                setCombinedDrawableColor(chat_photoStatesDrawables[i5][0], getColor("chat_mediaLoaderPhoto"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i5][0], getColor("chat_mediaLoaderPhotoIcon"), true);
                setCombinedDrawableColor(chat_photoStatesDrawables[i5][1], getColor("chat_mediaLoaderPhotoSelected"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i5][1], getColor("chat_mediaLoaderPhotoIconSelected"), true);
            }
            for (int i6 = 0; i6 < 2; i6++) {
                int i7 = i6 + 7;
                setCombinedDrawableColor(chat_photoStatesDrawables[i7][0], getColor("chat_outLoaderPhoto"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i7][0], getColor("chat_outLoaderPhotoIcon"), true);
                setCombinedDrawableColor(chat_photoStatesDrawables[i7][1], getColor("chat_outLoaderPhotoSelected"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i7][1], getColor("chat_outLoaderPhotoIconSelected"), true);
                int i8 = i6 + 10;
                setCombinedDrawableColor(chat_photoStatesDrawables[i8][0], getColor("chat_inLoaderPhoto"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i8][0], getColor("chat_inLoaderPhotoIcon"), true);
                setCombinedDrawableColor(chat_photoStatesDrawables[i8][1], getColor("chat_inLoaderPhotoSelected"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i8][1], getColor("chat_inLoaderPhotoIconSelected"), true);
            }
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
            setDrawableColorByKey(chat_composeShadowRoundDrawable, "chat_messagePanelBackground");
            int color = getColor("chat_outAudioSeekbarFill") == -1 ? getColor("chat_outBubble") : -1;
            setDrawableColor(chat_pollCheckDrawable[1], color);
            setDrawableColor(chat_pollCrossDrawable[1], color);
            setDrawableColor(chat_attachEmptyDrawable, getColor("chat_attachEmptyImage"));
            if (!z2) {
                applyChatServiceMessageColor();
                applyChatMessageSelectedBackgroundColor();
            }
            refreshAttachButtonsColors();
        }
    }

    public static void applyChatServiceMessageColor() {
        applyChatServiceMessageColor((int[]) null, (Drawable) null, wallpaper);
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
        applyServiceShaderMatrix(serviceBitmap, serviceBitmapShader, serviceBitmapMatrix, i, i2, f, f2);
    }

    public static void applyServiceShaderMatrix(Bitmap bitmap, BitmapShader bitmapShader, Matrix matrix, int i, int i2, float f, float f2) {
        if (bitmapShader != null) {
            float width = (float) bitmap.getWidth();
            float height = (float) bitmap.getHeight();
            float f3 = (float) i;
            float f4 = (float) i2;
            float max = Math.max(f3 / width, f4 / height);
            matrix.reset();
            matrix.setTranslate(((f3 - (width * max)) / 2.0f) - f, ((f4 - (height * max)) / 2.0f) - f2);
            matrix.preScale(max, max);
            bitmapShader.setLocalMatrix(matrix);
        }
    }

    public static void applyChatServiceMessageColor(int[] iArr, Drawable drawable) {
        applyChatServiceMessageColor(iArr, drawable, wallpaper);
    }

    public static void applyChatServiceMessageColor(int[] iArr, Drawable drawable, Drawable drawable2) {
        Integer num;
        Integer num2;
        Integer num3;
        if (chat_actionBackgroundPaint != null) {
            serviceMessageColor = serviceMessageColorBackup;
            serviceSelectedMessageColor = serviceSelectedMessageColorBackup;
            boolean z = true;
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
            }
            if (drawable == null) {
                drawable = drawable2;
            }
            boolean z2 = drawable instanceof MotionBackgroundDrawable;
            if (!z2 || SharedConfig.getDevicePerformanceClass() == 0) {
                z = false;
            }
            if (z) {
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
                setDrawableColor(chat_msgStickerPinnedDrawable, -1);
                setDrawableColor(chat_msgStickerCheckDrawable, -1);
                setDrawableColor(chat_msgStickerHalfCheckDrawable, -1);
                setDrawableColor(chat_msgStickerViewsDrawable, -1);
                setDrawableColor(chat_msgStickerRepliesDrawable, -1);
                chat_actionTextPaint.setColor(-1);
                chat_actionTextPaint.linkColor = -1;
                chat_botButtonPaint.setColor(-1);
                setDrawableColor(chat_commentStickerDrawable, -1);
                setDrawableColor(chat_shareIconDrawable, -1);
                setDrawableColor(chat_replyIconDrawable, -1);
                setDrawableColor(chat_goIconDrawable, -1);
                setDrawableColor(chat_botInlineDrawable, -1);
                setDrawableColor(chat_botWebViewDrawable, -1);
                setDrawableColor(chat_botInviteDrawable, -1);
                setDrawableColor(chat_botLinkDrawable, -1);
            } else {
                serviceBitmap = null;
                serviceBitmapShader = null;
                setDrawableColorByKey(chat_msgStickerPinnedDrawable, "chat_serviceText");
                setDrawableColorByKey(chat_msgStickerCheckDrawable, "chat_serviceText");
                setDrawableColorByKey(chat_msgStickerHalfCheckDrawable, "chat_serviceText");
                setDrawableColorByKey(chat_msgStickerViewsDrawable, "chat_serviceText");
                setDrawableColorByKey(chat_msgStickerRepliesDrawable, "chat_serviceText");
                chat_actionTextPaint.setColor(getColor("chat_serviceText"));
                chat_actionTextPaint.linkColor = getColor("chat_serviceLink");
                setDrawableColorByKey(chat_commentStickerDrawable, "chat_serviceIcon");
                setDrawableColorByKey(chat_shareIconDrawable, "chat_serviceIcon");
                setDrawableColorByKey(chat_replyIconDrawable, "chat_serviceIcon");
                setDrawableColorByKey(chat_goIconDrawable, "chat_serviceIcon");
                setDrawableColorByKey(chat_botInlineDrawable, "chat_serviceIcon");
                setDrawableColorByKey(chat_botWebViewDrawable, "chat_serviceIcon");
                setDrawableColorByKey(chat_botInviteDrawable, "chat_serviceIcon");
                setDrawableColorByKey(chat_botLinkDrawable, "chat_serviceIcon");
                chat_botButtonPaint.setColor(getColor("chat_botButtonText"));
            }
            chat_actionBackgroundPaint.setColor(num2.intValue());
            chat_actionBackgroundSelectedPaint.setColor(num.intValue());
            chat_actionBackgroundPaint2.setColor(num3.intValue());
            num2.intValue();
            if (serviceBitmapShader == null || (currentColors.get("chat_serviceBackground") != null && !z2)) {
                chat_actionBackgroundPaint.setColorFilter((ColorFilter) null);
                chat_actionBackgroundPaint.setShader((Shader) null);
                chat_actionBackgroundSelectedPaint.setColorFilter((ColorFilter) null);
                chat_actionBackgroundSelectedPaint.setShader((Shader) null);
                return;
            }
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(((MotionBackgroundDrawable) drawable).getIntensity() >= 0 ? 1.8f : 0.5f);
            chat_actionBackgroundPaint.setShader(serviceBitmapShader);
            chat_actionBackgroundPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            chat_actionBackgroundPaint.setAlpha(127);
            chat_actionBackgroundSelectedPaint.setShader(serviceBitmapShader);
            chat_actionBackgroundSelectedPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            chat_actionBackgroundSelectedPaint.setAlpha(200);
        }
    }

    public static void applyChatMessageSelectedBackgroundColor() {
        applyChatMessageSelectedBackgroundColor((Drawable) null, wallpaper);
    }

    public static void applyChatMessageSelectedBackgroundColor(Drawable drawable, Drawable drawable2) {
        Bitmap bitmap;
        if (chat_messageBackgroundSelectedPaint != null) {
            Integer num = currentColors.get("chat_selectedBackground");
            if (drawable == null) {
                drawable = drawable2;
            }
            boolean z = (drawable instanceof MotionBackgroundDrawable) && SharedConfig.getDevicePerformanceClass() != 0 && num == null;
            if (z && serviceBitmap != (bitmap = ((MotionBackgroundDrawable) drawable).getBitmap())) {
                serviceBitmap = bitmap;
                Bitmap bitmap2 = serviceBitmap;
                Shader.TileMode tileMode = Shader.TileMode.CLAMP;
                serviceBitmapShader = new BitmapShader(bitmap2, tileMode, tileMode);
                if (serviceBitmapMatrix == null) {
                    serviceBitmapMatrix = new Matrix();
                }
            }
            if (serviceBitmapShader == null || num != null || !z) {
                chat_messageBackgroundSelectedPaint.setColor(num == null ? NUM : num.intValue());
                chat_messageBackgroundSelectedPaint.setColorFilter((ColorFilter) null);
                chat_messageBackgroundSelectedPaint.setShader((Shader) null);
                return;
            }
            ColorMatrix colorMatrix = new ColorMatrix();
            AndroidUtilities.adjustSaturationColorMatrix(colorMatrix, 2.5f);
            AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, 0.75f);
            chat_messageBackgroundSelectedPaint.setShader(serviceBitmapShader);
            chat_messageBackgroundSelectedPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            chat_messageBackgroundSelectedPaint.setAlpha(64);
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
            return (str.equals("chats_menuTopShadow") || str.equals("chats_menuTopBackground") || str.equals("chats_menuTopShadowCats") || str.equals("key_chat_wallpaper_gradient_to2") || str.equals("key_chat_wallpaper_gradient_to3")) ? 0 : -65536;
        }
        return num.intValue();
    }

    public static boolean hasThemeKey(String str) {
        return currentColors.containsKey(str);
    }

    public static Integer getColorOrNull(String str) {
        Integer num = currentColors.get(str);
        if (num == null) {
            if (fallbackKeys.get(str) != null) {
                num = currentColors.get(str);
            }
            if (num == null) {
                num = defaultColors.get(str);
            }
        }
        if (num != null) {
            return ("windowBackgroundWhite".equals(str) || "windowBackgroundGray".equals(str) || "actionBarDefault".equals(str) || "actionBarDefaultArchived".equals(str)) ? Integer.valueOf(num.intValue() | -16777216) : num;
        }
        return num;
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

    public static int getColor(String str, ResourcesProvider resourcesProvider) {
        Integer color;
        if (resourcesProvider == null || (color = resourcesProvider.getColor(str)) == null) {
            return getColor(str);
        }
        return color.intValue();
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
        if (serviceBitmapShader != null && ("chat_serviceText".equals(str) || "chat_serviceLink".equals(str) || "chat_serviceIcon".equals(str) || "chat_stickerReplyLine".equals(str) || "chat_stickerReplyNameText".equals(str) || "chat_stickerReplyMessageText".equals(str))) {
            return -1;
        }
        if (currentTheme == defaultTheme) {
            if (myMessagesBubblesColorKeys.contains(str)) {
                z2 = currentTheme.isDefaultMyMessagesBubbles();
            } else if (myMessagesColorKeys.contains(str)) {
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
            case -391617936:
                if (str.equals("chat_selectedBackground")) {
                    c = 5;
                    break;
                }
                break;
            case 426061980:
                if (str.equals("chat_serviceBackground")) {
                    c = 6;
                    break;
                }
                break;
            case 1381936524:
                if (str.equals("key_chat_wallpaper_gradient_to2")) {
                    c = 7;
                    break;
                }
                break;
            case 1381936525:
                if (str.equals("key_chat_wallpaper_gradient_to3")) {
                    c = 8;
                    break;
                }
                break;
            case 1573464919:
                if (str.equals("chat_serviceBackgroundSelected")) {
                    c = 9;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 1:
            case 3:
            case 7:
            case 8:
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
                applyChatMessageSelectedBackgroundColor();
                return;
            case 6:
            case 9:
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
            applyChatMessageSelectedBackgroundColor();
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
        TLRPC$Document tLRPC$Document;
        boolean z;
        File file;
        float f;
        float f2;
        TLRPC$WallPaper tLRPC$WallPaper;
        if (wallpaper == null) {
            ThemeInfo themeInfo = currentTheme;
            boolean z2 = themeInfo.firstAccentIsDefault && themeInfo.currentAccentId == DEFALT_THEME_ACCENT_ID;
            ThemeAccent accent = themeInfo.getAccent(false);
            TLRPC$Document tLRPC$Document2 = null;
            if (accent != null) {
                File pathToWallpaper = accent.getPathToWallpaper();
                boolean z3 = accent.patternMotion;
                TLRPC$TL_theme tLRPC$TL_theme = accent.info;
                TLRPC$ThemeSettings tLRPC$ThemeSettings = (tLRPC$TL_theme == null || tLRPC$TL_theme.settings.size() <= 0) ? null : accent.info.settings.get(0);
                if (!(accent.info == null || tLRPC$ThemeSettings == null || (tLRPC$WallPaper = tLRPC$ThemeSettings.wallpaper) == null)) {
                    tLRPC$Document2 = tLRPC$WallPaper.document;
                }
                tLRPC$Document = tLRPC$Document2;
                file = pathToWallpaper;
                z = z3;
            } else {
                file = null;
                tLRPC$Document = null;
                z = false;
            }
            ThemeInfo themeInfo2 = currentTheme;
            OverrideWallpaperInfo overrideWallpaperInfo = themeInfo2.overrideWallpaper;
            if (overrideWallpaperInfo != null) {
                f2 = overrideWallpaperInfo.intensity;
            } else if (accent != null) {
                f2 = accent.patternIntensity;
            } else {
                f = (float) themeInfo2.patternIntensity;
                int i = (int) f;
                DispatchQueue dispatchQueue = Utilities.themeQueue;
                Theme$$ExternalSyntheticLambda5 theme$$ExternalSyntheticLambda5 = new Theme$$ExternalSyntheticLambda5(overrideWallpaperInfo, file, i, z2, z, tLRPC$Document);
                wallpaperLoadTask = theme$$ExternalSyntheticLambda5;
                dispatchQueue.postRunnable(theme$$ExternalSyntheticLambda5);
            }
            f = f2 * 100.0f;
            int i2 = (int) f;
            DispatchQueue dispatchQueue2 = Utilities.themeQueue;
            Theme$$ExternalSyntheticLambda5 theme$$ExternalSyntheticLambda52 = new Theme$$ExternalSyntheticLambda5(overrideWallpaperInfo, file, i2, z2, z, tLRPC$Document);
            wallpaperLoadTask = theme$$ExternalSyntheticLambda52;
            dispatchQueue2.postRunnable(theme$$ExternalSyntheticLambda52);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$loadWallpaper$8(OverrideWallpaperInfo overrideWallpaperInfo, File file, int i, boolean z, boolean z2, TLRPC$Document tLRPC$Document) {
        BackgroundDrawableSettings createBackgroundDrawable = createBackgroundDrawable(currentTheme, overrideWallpaperInfo, currentColors, file, themedWallpaperLink, themedWallpaperFileOffset, i, previousPhase, z, hasPreviousTheme, isApplyingAccent, z2, tLRPC$Document);
        Boolean bool = createBackgroundDrawable.isWallpaperMotion;
        isWallpaperMotion = bool != null ? bool.booleanValue() : isWallpaperMotion;
        Boolean bool2 = createBackgroundDrawable.isPatternWallpaper;
        isPatternWallpaper = bool2 != null ? bool2.booleanValue() : isPatternWallpaper;
        Boolean bool3 = createBackgroundDrawable.isCustomTheme;
        isCustomTheme = bool3 != null ? bool3.booleanValue() : isCustomTheme;
        Drawable drawable = createBackgroundDrawable.wallpaper;
        wallpaper = drawable != null ? drawable : wallpaper;
        calcBackgroundColor(drawable, 1);
        AndroidUtilities.runOnUIThread(new Theme$$ExternalSyntheticLambda1(drawable));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$loadWallpaper$7(Drawable drawable) {
        wallpaperLoadTask = null;
        createCommonChatResources();
        applyChatServiceMessageColor((int[]) null, (Drawable) null, drawable);
        applyChatMessageSelectedBackgroundColor((Drawable) null, drawable);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewWallpapper, new Object[0]);
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x0047  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x004d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.telegram.ui.ActionBar.Theme.BackgroundDrawableSettings createBackgroundDrawable(org.telegram.ui.ActionBar.Theme.ThemeInfo r13, java.util.HashMap<java.lang.String, java.lang.Integer> r14, java.lang.String r15, int r16) {
        /*
            r0 = r13
            boolean r1 = r0.firstAccentIsDefault
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L_0x000f
            int r1 = r0.currentAccentId
            int r4 = DEFALT_THEME_ACCENT_ID
            if (r1 != r4) goto L_0x000f
            r8 = 1
            goto L_0x0010
        L_0x000f:
            r8 = 0
        L_0x0010:
            org.telegram.ui.ActionBar.Theme$ThemeAccent r1 = r13.getAccent(r3)
            if (r1 == 0) goto L_0x001b
            java.io.File r4 = r1.getPathToWallpaper()
            goto L_0x001c
        L_0x001b:
            r4 = 0
        L_0x001c:
            if (r1 == 0) goto L_0x0024
            boolean r5 = r1.patternMotion
            if (r5 == 0) goto L_0x0024
            r11 = 1
            goto L_0x0025
        L_0x0024:
            r11 = 0
        L_0x0025:
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r2 = r0.overrideWallpaper
            r3 = 1120403456(0x42CLASSNAME, float:100.0)
            if (r2 == 0) goto L_0x0032
            float r1 = r2.intensity
        L_0x002d:
            float r1 = r1 * r3
        L_0x002f:
            int r1 = (int) r1
            r6 = r1
            goto L_0x003b
        L_0x0032:
            if (r1 == 0) goto L_0x0037
            float r1 = r1.patternIntensity
            goto L_0x002d
        L_0x0037:
            int r1 = r0.patternIntensity
            float r1 = (float) r1
            goto L_0x002f
        L_0x003b:
            java.util.HashMap<java.lang.String, java.lang.Integer> r1 = currentColorsNoAccent
            java.lang.String r3 = "wallpaperFileOffset"
            java.lang.Object r1 = r1.get(r3)
            java.lang.Integer r1 = (java.lang.Integer) r1
            if (r1 == 0) goto L_0x004d
            int r1 = r1.intValue()
            r5 = r1
            goto L_0x004f
        L_0x004d:
            r1 = -1
            r5 = -1
        L_0x004f:
            r9 = 0
            r10 = 0
            r12 = 0
            r0 = r13
            r1 = r2
            r2 = r14
            r3 = r4
            r4 = r15
            r7 = r16
            org.telegram.ui.ActionBar.Theme$BackgroundDrawableSettings r0 = createBackgroundDrawable(r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.createBackgroundDrawable(org.telegram.ui.ActionBar.Theme$ThemeInfo, java.util.HashMap, java.lang.String, int):org.telegram.ui.ActionBar.Theme$BackgroundDrawableSettings");
    }

    /* JADX WARNING: Removed duplicated region for block: B:131:0x0313  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.telegram.ui.ActionBar.Theme.BackgroundDrawableSettings createBackgroundDrawable(org.telegram.ui.ActionBar.Theme.ThemeInfo r23, org.telegram.ui.ActionBar.Theme.OverrideWallpaperInfo r24, java.util.HashMap<java.lang.String, java.lang.Integer> r25, java.io.File r26, java.lang.String r27, int r28, int r29, int r30, boolean r31, boolean r32, boolean r33, boolean r34, org.telegram.tgnet.TLRPC$Document r35) {
        /*
            r0 = r23
            r1 = r24
            r2 = r25
            r3 = r26
            r4 = r28
            r5 = r29
            r6 = r30
            r7 = r35
            org.telegram.ui.ActionBar.Theme$BackgroundDrawableSettings r8 = new org.telegram.ui.ActionBar.Theme$BackgroundDrawableSettings
            r8.<init>()
            android.graphics.drawable.Drawable r9 = wallpaper
            r8.wallpaper = r9
            r9 = 1
            r10 = 0
            if (r32 == 0) goto L_0x001f
            if (r33 == 0) goto L_0x0023
        L_0x001f:
            if (r1 == 0) goto L_0x0023
            r11 = 1
            goto L_0x0024
        L_0x0023:
            r11 = 0
        L_0x0024:
            if (r1 == 0) goto L_0x0048
            boolean r12 = r1.isMotion
            java.lang.Boolean r12 = java.lang.Boolean.valueOf(r12)
            r8.isWallpaperMotion = r12
            int r12 = r1.color
            if (r12 == 0) goto L_0x0040
            boolean r12 = r24.isDefault()
            if (r12 != 0) goto L_0x0040
            boolean r12 = r24.isColor()
            if (r12 != 0) goto L_0x0040
            r12 = 1
            goto L_0x0041
        L_0x0040:
            r12 = 0
        L_0x0041:
            java.lang.Boolean r12 = java.lang.Boolean.valueOf(r12)
            r8.isPatternWallpaper = r12
            goto L_0x005d
        L_0x0048:
            boolean r12 = r0.isMotion
            java.lang.Boolean r12 = java.lang.Boolean.valueOf(r12)
            r8.isWallpaperMotion = r12
            int r12 = r0.patternBgColor
            if (r12 == 0) goto L_0x0056
            r12 = 1
            goto L_0x0057
        L_0x0056:
            r12 = 0
        L_0x0057:
            java.lang.Boolean r12 = java.lang.Boolean.valueOf(r12)
            r8.isPatternWallpaper = r12
        L_0x005d:
            r14 = 2
            if (r11 != 0) goto L_0x021b
            if (r31 == 0) goto L_0x0064
            r15 = 0
            goto L_0x006c
        L_0x0064:
            java.lang.String r15 = "chat_wallpaper"
            java.lang.Object r15 = r2.get(r15)
            java.lang.Integer r15 = (java.lang.Integer) r15
        L_0x006c:
            java.lang.String r11 = "key_chat_wallpaper_gradient_to3"
            java.lang.Object r11 = r2.get(r11)
            java.lang.Integer r11 = (java.lang.Integer) r11
            if (r11 != 0) goto L_0x007a
            java.lang.Integer r11 = java.lang.Integer.valueOf(r10)
        L_0x007a:
            java.lang.String r12 = "key_chat_wallpaper_gradient_to2"
            java.lang.Object r13 = r2.get(r12)
            java.lang.Integer r13 = (java.lang.Integer) r13
            java.lang.Object r12 = r2.get(r12)
            java.lang.Integer r12 = (java.lang.Integer) r12
            java.lang.String r13 = "chat_wallpaper_gradient_to"
            java.lang.Object r13 = r2.get(r13)
            java.lang.Integer r13 = (java.lang.Integer) r13
            if (r3 == 0) goto L_0x00ea
            boolean r16 = r26.exists()
            if (r16 == 0) goto L_0x00ea
            if (r15 == 0) goto L_0x00cc
            if (r13 == 0) goto L_0x00cc
            if (r12 == 0) goto L_0x00cc
            org.telegram.ui.Components.MotionBackgroundDrawable r0 = new org.telegram.ui.Components.MotionBackgroundDrawable     // Catch:{ all -> 0x00e4 }
            int r18 = r15.intValue()     // Catch:{ all -> 0x00e4 }
            int r19 = r13.intValue()     // Catch:{ all -> 0x00e4 }
            int r20 = r12.intValue()     // Catch:{ all -> 0x00e4 }
            int r21 = r11.intValue()     // Catch:{ all -> 0x00e4 }
            r22 = 0
            r17 = r0
            r17.<init>(r18, r19, r20, r21, r22)     // Catch:{ all -> 0x00e4 }
            java.lang.String r2 = r26.getAbsolutePath()     // Catch:{ all -> 0x00e4 }
            android.graphics.Bitmap r2 = android.graphics.BitmapFactory.decodeFile(r2)     // Catch:{ all -> 0x00e4 }
            r0.setPatternBitmap(r5, r2)     // Catch:{ all -> 0x00e4 }
            int r2 = r0.getPatternColor()     // Catch:{ all -> 0x00e4 }
            r0.setPatternColorFilter(r2)     // Catch:{ all -> 0x00e4 }
            r8.wallpaper = r0     // Catch:{ all -> 0x00e4 }
            goto L_0x00d6
        L_0x00cc:
            java.lang.String r0 = r26.getAbsolutePath()     // Catch:{ all -> 0x00e4 }
            android.graphics.drawable.Drawable r0 = android.graphics.drawable.Drawable.createFromPath(r0)     // Catch:{ all -> 0x00e4 }
            r8.wallpaper = r0     // Catch:{ all -> 0x00e4 }
        L_0x00d6:
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r34)     // Catch:{ all -> 0x00e4 }
            r8.isWallpaperMotion = r0     // Catch:{ all -> 0x00e4 }
            java.lang.Boolean r0 = java.lang.Boolean.TRUE     // Catch:{ all -> 0x00e4 }
            r8.isPatternWallpaper = r0     // Catch:{ all -> 0x00e4 }
            r8.isCustomTheme = r0     // Catch:{ all -> 0x00e4 }
            goto L_0x021b
        L_0x00e4:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x021b
        L_0x00ea:
            if (r15 == 0) goto L_0x01a4
            java.lang.String r0 = "chat_wallpaper_gradient_rotation"
            java.lang.Object r0 = r2.get(r0)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x00fc
            r0 = 45
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
        L_0x00fc:
            if (r13 == 0) goto L_0x015b
            if (r12 == 0) goto L_0x015b
            org.telegram.ui.Components.MotionBackgroundDrawable r2 = new org.telegram.ui.Components.MotionBackgroundDrawable
            int r17 = r15.intValue()
            int r18 = r13.intValue()
            int r19 = r12.intValue()
            int r20 = r11.intValue()
            r21 = 0
            r16 = r2
            r16.<init>(r17, r18, r19, r20, r21)
            if (r3 == 0) goto L_0x0151
            if (r7 == 0) goto L_0x0151
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.FileLoader r0 = org.telegram.messenger.FileLoader.getInstance(r0)
            java.io.File r0 = r0.getPathToAttach(r7, r9)
            r4 = 1135869952(0x43b40000, float:360.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r7 = 1142947840(0x44200000, float:640.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            android.graphics.Bitmap r11 = org.telegram.messenger.SvgHelper.getBitmap((java.io.File) r0, (int) r4, (int) r7, (boolean) r10)
            if (r11 == 0) goto L_0x0152
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0149 }
            r0.<init>(r3)     // Catch:{ Exception -> 0x0149 }
            android.graphics.Bitmap$CompressFormat r3 = android.graphics.Bitmap.CompressFormat.PNG     // Catch:{ Exception -> 0x0149 }
            r4 = 90
            r11.compress(r3, r4, r0)     // Catch:{ Exception -> 0x0149 }
            r0.close()     // Catch:{ Exception -> 0x0149 }
            goto L_0x0152
        L_0x0149:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0.printStackTrace()
            goto L_0x0152
        L_0x0151:
            r11 = 0
        L_0x0152:
            r2.setPatternBitmap(r5, r11)
            r2.setPhase(r6)
            r8.wallpaper = r2
            goto L_0x019e
        L_0x015b:
            if (r13 == 0) goto L_0x0193
            boolean r2 = r13.equals(r15)
            if (r2 == 0) goto L_0x0164
            goto L_0x0193
        L_0x0164:
            int[] r2 = new int[r14]
            int r3 = r15.intValue()
            r2[r10] = r3
            int r3 = r13.intValue()
            r2[r9] = r3
            int r0 = r0.intValue()
            android.graphics.drawable.GradientDrawable$Orientation r0 = org.telegram.ui.Components.BackgroundGradientDrawable.getGradientOrientation(r0)
            org.telegram.ui.Components.BackgroundGradientDrawable r3 = new org.telegram.ui.Components.BackgroundGradientDrawable
            r3.<init>(r0, r2)
            org.telegram.ui.ActionBar.Theme$11 r0 = new org.telegram.ui.ActionBar.Theme$11
            r0.<init>()
            org.telegram.ui.Components.BackgroundGradientDrawable$Sizes r2 = org.telegram.ui.Components.BackgroundGradientDrawable.Sizes.ofDeviceScreen()
            r4 = 100
            org.telegram.ui.Components.BackgroundGradientDrawable$Disposable r0 = r3.startDithering(r2, r0, r4)
            backgroundGradientDisposable = r0
            r8.wallpaper = r3
            goto L_0x019e
        L_0x0193:
            android.graphics.drawable.ColorDrawable r0 = new android.graphics.drawable.ColorDrawable
            int r2 = r15.intValue()
            r0.<init>(r2)
            r8.wallpaper = r0
        L_0x019e:
            java.lang.Boolean r0 = java.lang.Boolean.TRUE
            r8.isCustomTheme = r0
            goto L_0x021b
        L_0x01a4:
            if (r27 == 0) goto L_0x01e0
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x01db }
            java.io.File r2 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ Exception -> 0x01db }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01db }
            r3.<init>()     // Catch:{ Exception -> 0x01db }
            java.lang.String r4 = org.telegram.messenger.Utilities.MD5(r27)     // Catch:{ Exception -> 0x01db }
            r3.append(r4)     // Catch:{ Exception -> 0x01db }
            java.lang.String r4 = ".wp"
            r3.append(r4)     // Catch:{ Exception -> 0x01db }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x01db }
            r0.<init>(r2, r3)     // Catch:{ Exception -> 0x01db }
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ Exception -> 0x01db }
            r2.<init>(r0)     // Catch:{ Exception -> 0x01db }
            android.graphics.Bitmap r0 = loadScreenSizedBitmap(r2, r10)     // Catch:{ Exception -> 0x01db }
            if (r0 == 0) goto L_0x021b
            android.graphics.drawable.BitmapDrawable r2 = new android.graphics.drawable.BitmapDrawable     // Catch:{ Exception -> 0x01db }
            r2.<init>(r0)     // Catch:{ Exception -> 0x01db }
            r8.wallpaper = r2     // Catch:{ Exception -> 0x01db }
            java.lang.Boolean r0 = java.lang.Boolean.TRUE     // Catch:{ Exception -> 0x01db }
            r8.isCustomTheme = r0     // Catch:{ Exception -> 0x01db }
            goto L_0x021b
        L_0x01db:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x021b
        L_0x01e0:
            if (r4 <= 0) goto L_0x021b
            java.lang.String r2 = r0.pathToFile
            if (r2 != 0) goto L_0x01ea
            java.lang.String r2 = r0.assetName
            if (r2 == 0) goto L_0x021b
        L_0x01ea:
            java.lang.String r2 = r0.assetName     // Catch:{ all -> 0x0217 }
            if (r2 == 0) goto L_0x01f3
            java.io.File r0 = getAssetFile(r2)     // Catch:{ all -> 0x0217 }
            goto L_0x01fb
        L_0x01f3:
            java.io.File r2 = new java.io.File     // Catch:{ all -> 0x0217 }
            java.lang.String r0 = r0.pathToFile     // Catch:{ all -> 0x0217 }
            r2.<init>(r0)     // Catch:{ all -> 0x0217 }
            r0 = r2
        L_0x01fb:
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ all -> 0x0217 }
            r2.<init>(r0)     // Catch:{ all -> 0x0217 }
            android.graphics.Bitmap r0 = loadScreenSizedBitmap(r2, r4)     // Catch:{ all -> 0x0217 }
            if (r0 == 0) goto L_0x021b
            android.graphics.drawable.BitmapDrawable r2 = new android.graphics.drawable.BitmapDrawable     // Catch:{ all -> 0x0217 }
            r2.<init>(r0)     // Catch:{ all -> 0x0217 }
            wallpaper = r2     // Catch:{ all -> 0x0217 }
            r8.wallpaper = r2     // Catch:{ all -> 0x0217 }
            r2.setFilterBitmap(r9)     // Catch:{ all -> 0x0217 }
            java.lang.Boolean r0 = java.lang.Boolean.TRUE     // Catch:{ all -> 0x0217 }
            r8.isCustomTheme = r0     // Catch:{ all -> 0x0217 }
            goto L_0x021b
        L_0x0217:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x021b:
            android.graphics.drawable.Drawable r0 = r8.wallpaper
            if (r0 != 0) goto L_0x031f
            if (r1 == 0) goto L_0x0224
            int r0 = r1.color
            goto L_0x0225
        L_0x0224:
            r0 = 0
        L_0x0225:
            if (r1 == 0) goto L_0x0303
            boolean r2 = r24.isDefault()     // Catch:{ all -> 0x030e }
            if (r2 == 0) goto L_0x022f
            goto L_0x0303
        L_0x022f:
            boolean r2 = r24.isColor()     // Catch:{ all -> 0x030e }
            if (r2 == 0) goto L_0x0239
            int r2 = r1.gradientColor1     // Catch:{ all -> 0x030e }
            if (r2 == 0) goto L_0x030f
        L_0x0239:
            if (r0 == 0) goto L_0x02ca
            boolean r2 = isPatternWallpaper     // Catch:{ all -> 0x030e }
            if (r2 == 0) goto L_0x0243
            int r2 = r1.gradientColor2     // Catch:{ all -> 0x030e }
            if (r2 == 0) goto L_0x02ca
        L_0x0243:
            int r2 = r1.gradientColor1     // Catch:{ all -> 0x030e }
            if (r2 == 0) goto L_0x029b
            int r3 = r1.gradientColor2     // Catch:{ all -> 0x030e }
            if (r3 == 0) goto L_0x029b
            org.telegram.ui.Components.MotionBackgroundDrawable r2 = new org.telegram.ui.Components.MotionBackgroundDrawable     // Catch:{ all -> 0x030e }
            int r3 = r1.color     // Catch:{ all -> 0x030e }
            int r4 = r1.gradientColor1     // Catch:{ all -> 0x030e }
            int r5 = r1.gradientColor2     // Catch:{ all -> 0x030e }
            int r7 = r1.gradientColor3     // Catch:{ all -> 0x030e }
            r21 = 0
            r16 = r2
            r17 = r3
            r18 = r4
            r19 = r5
            r20 = r7
            r16.<init>(r17, r18, r19, r20, r21)     // Catch:{ all -> 0x030e }
            r2.setPhase(r6)     // Catch:{ all -> 0x030e }
            java.lang.Boolean r3 = r8.isPatternWallpaper     // Catch:{ all -> 0x030e }
            boolean r3 = r3.booleanValue()     // Catch:{ all -> 0x030e }
            if (r3 == 0) goto L_0x0297
            java.io.File r3 = new java.io.File     // Catch:{ all -> 0x030e }
            java.io.File r4 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ all -> 0x030e }
            java.lang.String r5 = r1.fileName     // Catch:{ all -> 0x030e }
            r3.<init>(r4, r5)     // Catch:{ all -> 0x030e }
            boolean r4 = r3.exists()     // Catch:{ all -> 0x030e }
            if (r4 == 0) goto L_0x0297
            float r1 = r1.intensity     // Catch:{ all -> 0x030e }
            r4 = 1120403456(0x42CLASSNAME, float:100.0)
            float r1 = r1 * r4
            int r1 = (int) r1     // Catch:{ all -> 0x030e }
            java.io.FileInputStream r4 = new java.io.FileInputStream     // Catch:{ all -> 0x030e }
            r4.<init>(r3)     // Catch:{ all -> 0x030e }
            android.graphics.Bitmap r3 = loadScreenSizedBitmap(r4, r10)     // Catch:{ all -> 0x030e }
            r2.setPatternBitmap(r1, r3)     // Catch:{ all -> 0x030e }
            java.lang.Boolean r1 = java.lang.Boolean.TRUE     // Catch:{ all -> 0x030e }
            r8.isCustomTheme = r1     // Catch:{ all -> 0x030e }
        L_0x0297:
            r8.wallpaper = r2     // Catch:{ all -> 0x030e }
            goto L_0x030f
        L_0x029b:
            if (r2 == 0) goto L_0x02c2
            int[] r3 = new int[r14]     // Catch:{ all -> 0x030e }
            r3[r10] = r0     // Catch:{ all -> 0x030e }
            r3[r9] = r2     // Catch:{ all -> 0x030e }
            int r1 = r1.rotation     // Catch:{ all -> 0x030e }
            android.graphics.drawable.GradientDrawable$Orientation r1 = org.telegram.ui.Components.BackgroundGradientDrawable.getGradientOrientation(r1)     // Catch:{ all -> 0x030e }
            org.telegram.ui.Components.BackgroundGradientDrawable r2 = new org.telegram.ui.Components.BackgroundGradientDrawable     // Catch:{ all -> 0x030e }
            r2.<init>(r1, r3)     // Catch:{ all -> 0x030e }
            org.telegram.ui.ActionBar.Theme$12 r1 = new org.telegram.ui.ActionBar.Theme$12     // Catch:{ all -> 0x030e }
            r1.<init>()     // Catch:{ all -> 0x030e }
            org.telegram.ui.Components.BackgroundGradientDrawable$Sizes r3 = org.telegram.ui.Components.BackgroundGradientDrawable.Sizes.ofDeviceScreen()     // Catch:{ all -> 0x030e }
            r4 = 100
            org.telegram.ui.Components.BackgroundGradientDrawable$Disposable r1 = r2.startDithering(r3, r1, r4)     // Catch:{ all -> 0x030e }
            backgroundGradientDisposable = r1     // Catch:{ all -> 0x030e }
            r8.wallpaper = r2     // Catch:{ all -> 0x030e }
            goto L_0x030f
        L_0x02c2:
            android.graphics.drawable.ColorDrawable r1 = new android.graphics.drawable.ColorDrawable     // Catch:{ all -> 0x030e }
            r1.<init>(r0)     // Catch:{ all -> 0x030e }
            r8.wallpaper = r1     // Catch:{ all -> 0x030e }
            goto L_0x030f
        L_0x02ca:
            java.io.File r2 = new java.io.File     // Catch:{ all -> 0x030e }
            java.io.File r3 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ all -> 0x030e }
            java.lang.String r1 = r1.fileName     // Catch:{ all -> 0x030e }
            r2.<init>(r3, r1)     // Catch:{ all -> 0x030e }
            boolean r1 = r2.exists()     // Catch:{ all -> 0x030e }
            if (r1 == 0) goto L_0x02f4
            java.io.FileInputStream r1 = new java.io.FileInputStream     // Catch:{ all -> 0x030e }
            r1.<init>(r2)     // Catch:{ all -> 0x030e }
            android.graphics.Bitmap r1 = loadScreenSizedBitmap(r1, r10)     // Catch:{ all -> 0x030e }
            if (r1 == 0) goto L_0x02f4
            android.graphics.drawable.BitmapDrawable r2 = new android.graphics.drawable.BitmapDrawable     // Catch:{ all -> 0x030e }
            r2.<init>(r1)     // Catch:{ all -> 0x030e }
            r8.wallpaper = r2     // Catch:{ all -> 0x030e }
            r2.setFilterBitmap(r9)     // Catch:{ all -> 0x030e }
            java.lang.Boolean r1 = java.lang.Boolean.TRUE     // Catch:{ all -> 0x030e }
            r8.isCustomTheme = r1     // Catch:{ all -> 0x030e }
        L_0x02f4:
            android.graphics.drawable.Drawable r1 = r8.wallpaper     // Catch:{ all -> 0x030e }
            if (r1 != 0) goto L_0x030f
            android.graphics.drawable.Drawable r1 = createDefaultWallpaper()     // Catch:{ all -> 0x030e }
            r8.wallpaper = r1     // Catch:{ all -> 0x030e }
            java.lang.Boolean r1 = java.lang.Boolean.FALSE     // Catch:{ all -> 0x030e }
            r8.isCustomTheme = r1     // Catch:{ all -> 0x030e }
            goto L_0x030f
        L_0x0303:
            android.graphics.drawable.Drawable r1 = createDefaultWallpaper()     // Catch:{ all -> 0x030e }
            r8.wallpaper = r1     // Catch:{ all -> 0x030e }
            java.lang.Boolean r1 = java.lang.Boolean.FALSE     // Catch:{ all -> 0x030e }
            r8.isCustomTheme = r1     // Catch:{ all -> 0x030e }
            goto L_0x030f
        L_0x030e:
        L_0x030f:
            android.graphics.drawable.Drawable r1 = r8.wallpaper
            if (r1 != 0) goto L_0x031f
            if (r0 != 0) goto L_0x0318
            r0 = -2693905(0xffffffffffd6e4ef, float:NaN)
        L_0x0318:
            android.graphics.drawable.ColorDrawable r1 = new android.graphics.drawable.ColorDrawable
            r1.<init>(r0)
            r8.wallpaper = r1
        L_0x031f:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.createBackgroundDrawable(org.telegram.ui.ActionBar.Theme$ThemeInfo, org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo, java.util.HashMap, java.io.File, java.lang.String, int, int, int, boolean, boolean, boolean, boolean, org.telegram.tgnet.TLRPC$Document):org.telegram.ui.ActionBar.Theme$BackgroundDrawableSettings");
    }

    public static Drawable createDefaultWallpaper() {
        return createDefaultWallpaper(0, 0);
    }

    public static Drawable createDefaultWallpaper(int i, int i2) {
        MotionBackgroundDrawable motionBackgroundDrawable = new MotionBackgroundDrawable(-2368069, -9722489, -2762611, -7817084, i != 0);
        if (i <= 0 || i2 <= 0) {
            Point point = AndroidUtilities.displaySize;
            i = Math.min(point.x, point.y);
            Point point2 = AndroidUtilities.displaySize;
            i2 = Math.max(point2.x, point2.y);
        }
        motionBackgroundDrawable.setPatternBitmap(34, SvgHelper.getBitmap(NUM, i, i2, -16777216));
        motionBackgroundDrawable.setPatternColorFilter(motionBackgroundDrawable.getPatternColor());
        return motionBackgroundDrawable;
    }

    /* access modifiers changed from: private */
    public static Bitmap loadScreenSizedBitmap(FileInputStream fileInputStream, int i) {
        float f;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            options.inJustDecodeBounds = true;
            long j = (long) i;
            fileInputStream.getChannel().position(j);
            BitmapFactory.decodeStream(fileInputStream, (Rect) null, options);
            float f2 = (float) options.outWidth;
            float f3 = (float) options.outHeight;
            Point point = AndroidUtilities.displaySize;
            int min = Math.min(point.x, point.y);
            Point point2 = AndroidUtilities.displaySize;
            int max = Math.max(point2.x, point2.y);
            if (min < max || f2 <= f3) {
                f = Math.min(f2 / ((float) min), f3 / ((float) max));
            } else {
                f = Math.max(f2 / ((float) min), f3 / ((float) max));
            }
            if (f < 1.2f) {
                f = 1.0f;
            }
            options.inJustDecodeBounds = false;
            if (f <= 1.0f || (f2 <= ((float) min) && f3 <= ((float) max))) {
                options.inSampleSize = (int) f;
            } else {
                int i2 = 1;
                do {
                    i2 *= 2;
                } while (((float) (i2 * 2)) < f);
                options.inSampleSize = i2;
            }
            fileInputStream.getChannel().position(j);
            Bitmap decodeStream = BitmapFactory.decodeStream(fileInputStream, (Rect) null, options);
            if (decodeStream.getWidth() < min || decodeStream.getHeight() < max) {
                float max2 = Math.max(((float) min) / ((float) decodeStream.getWidth()), ((float) max) / ((float) decodeStream.getHeight()));
                if (max2 >= 1.02f) {
                    Bitmap createScaledBitmap = Bitmap.createScaledBitmap(decodeStream, (int) (((float) decodeStream.getWidth()) * max2), (int) (((float) decodeStream.getHeight()) * max2), true);
                    decodeStream.recycle();
                    try {
                        fileInputStream.close();
                    } catch (Exception unused) {
                    }
                    return createScaledBitmap;
                }
            }
            try {
                fileInputStream.close();
            } catch (Exception unused2) {
            }
            return decodeStream;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (Exception unused3) {
                }
            }
            return null;
        } catch (Throwable th) {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (Exception unused4) {
                }
            }
            throw th;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:52:0x00f8 A[SYNTHETIC, Splitter:B:52:0x00f8] */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x0176 A[SYNTHETIC, Splitter:B:91:0x0176] */
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
            if (r10 == 0) goto L_0x018e
            java.io.FileInputStream r6 = new java.io.FileInputStream     // Catch:{ all -> 0x016f }
            r6.<init>(r10)     // Catch:{ all -> 0x016f }
            java.nio.channels.FileChannel r7 = r6.getChannel()     // Catch:{ all -> 0x016d }
            long r8 = (long) r1     // Catch:{ all -> 0x016d }
            r7.position(r8)     // Catch:{ all -> 0x016d }
            android.graphics.BitmapFactory$Options r1 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x016d }
            r1.<init>()     // Catch:{ all -> 0x016d }
            r7 = 1120403456(0x42CLASSNAME, float:100.0)
            if (r0 == 0) goto L_0x012a
            r1.inJustDecodeBounds = r3     // Catch:{ all -> 0x016d }
            int r0 = r1.outWidth     // Catch:{ all -> 0x016d }
            float r0 = (float) r0     // Catch:{ all -> 0x016d }
            int r8 = r1.outHeight     // Catch:{ all -> 0x016d }
            float r8 = (float) r8     // Catch:{ all -> 0x016d }
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r7)     // Catch:{ all -> 0x016d }
        L_0x011a:
            float r10 = (float) r9     // Catch:{ all -> 0x016d }
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
            r1.inJustDecodeBounds = r4     // Catch:{ all -> 0x016d }
            r1.inSampleSize = r3     // Catch:{ all -> 0x016d }
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeStream(r6, r5, r1)     // Catch:{ all -> 0x016d }
            if (r2 == 0) goto L_0x0158
            org.telegram.ui.ActionBar.Theme$ThemeInfo r1 = currentTheme     // Catch:{ all -> 0x016d }
            org.telegram.ui.ActionBar.Theme$ThemeAccent r1 = r1.getAccent(r4)     // Catch:{ all -> 0x016d }
            if (r1 == 0) goto L_0x0142
            float r1 = r1.patternIntensity     // Catch:{ all -> 0x016d }
            float r1 = r1 * r7
            int r1 = (int) r1     // Catch:{ all -> 0x016d }
            goto L_0x0144
        L_0x0142:
            r1 = 100
        L_0x0144:
            r2.setPatternBitmap(r1, r0)     // Catch:{ all -> 0x016d }
            int r0 = r2.getPatternColor()     // Catch:{ all -> 0x016d }
            r2.setPatternColorFilter(r0)     // Catch:{ all -> 0x016d }
            r6.close()     // Catch:{ Exception -> 0x0152 }
            goto L_0x0157
        L_0x0152:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0157:
            return r2
        L_0x0158:
            if (r0 == 0) goto L_0x0169
            android.graphics.drawable.BitmapDrawable r1 = new android.graphics.drawable.BitmapDrawable     // Catch:{ all -> 0x016d }
            r1.<init>(r0)     // Catch:{ all -> 0x016d }
            r6.close()     // Catch:{ Exception -> 0x0163 }
            goto L_0x0168
        L_0x0163:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x0168:
            return r1
        L_0x0169:
            r6.close()     // Catch:{ Exception -> 0x017a }
            goto L_0x018e
        L_0x016d:
            r0 = move-exception
            goto L_0x0171
        L_0x016f:
            r0 = move-exception
            r6 = r5
        L_0x0171:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0180 }
            if (r6 == 0) goto L_0x018e
            r6.close()     // Catch:{ Exception -> 0x017a }
            goto L_0x018e
        L_0x017a:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
            goto L_0x018e
        L_0x0180:
            r0 = move-exception
            r1 = r0
            if (r6 == 0) goto L_0x018d
            r6.close()     // Catch:{ Exception -> 0x0188 }
            goto L_0x018d
        L_0x0188:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x018d:
            throw r1
        L_0x018e:
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
        Drawable cachedWallpaperNonBlocking = getCachedWallpaperNonBlocking();
        if (cachedWallpaperNonBlocking != null || wallpaperLoadTask == null) {
            return cachedWallpaperNonBlocking;
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Utilities.themeQueue.postRunnable(new Theme$$ExternalSyntheticLambda2(countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return getCachedWallpaperNonBlocking();
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
        String selectedBackgroundSlug = getSelectedBackgroundSlug();
        return isPatternWallpaper || "CJz3BZ6YGEYBAAAABboWp6SAv04".equals(selectedBackgroundSlug) || "qeZWES8rGVIEAAAARfWlK1lnfiI".equals(selectedBackgroundSlug);
    }

    public static BackgroundGradientDrawable getCurrentGradientWallpaper() {
        int i;
        int i2;
        OverrideWallpaperInfo overrideWallpaperInfo = currentTheme.overrideWallpaper;
        if (overrideWallpaperInfo == null || (i = overrideWallpaperInfo.color) == 0 || (i2 = overrideWallpaperInfo.gradientColor1) == 0) {
            return null;
        }
        return new BackgroundGradientDrawable(BackgroundGradientDrawable.getGradientOrientation(overrideWallpaperInfo.rotation), new int[]{i, i2});
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
            AndroidUtilities.runOnUIThread(new Theme$$ExternalSyntheticLambda3(messageObject), 200);
            chat_msgAudioVisualizeDrawable = null;
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$unrefAudioVisualizeDrawable$9(MessageObject messageObject) {
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
        if (i < 0 || i > 5) {
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
            statusDrawableArr[3] = new PlayingGameDrawable(true, (ResourcesProvider) null);
        } else if (i == 4) {
            statusDrawableArr[4] = new RoundStatusDrawable(true);
        } else if (i == 5) {
            statusDrawableArr[5] = new ChoosingStickerStatusDrawable(true);
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

    public static RoundVideoProgressShadow getRadialSeekbarShadowDrawable() {
        if (roundPlayDrawable == null) {
            roundPlayDrawable = new RoundVideoProgressShadow();
        }
        return roundPlayDrawable;
    }

    public static HashMap<String, String> getFallbackKeys() {
        return fallbackKeys;
    }

    public static String getFallbackKey(String str) {
        return fallbackKeys.get(str);
    }

    public static Map<String, Drawable> getThemeDrawablesMap() {
        return defaultChatDrawables;
    }

    public static Drawable getThemeDrawable(String str) {
        return defaultChatDrawables.get(str);
    }

    public static String getThemeDrawableColorKey(String str) {
        return defaultChatDrawableColorKeys.get(str);
    }

    public static Map<String, Paint> getThemePaintsMap() {
        return defaultChatPaints;
    }

    public static Paint getThemePaint(String str) {
        return defaultChatPaints.get(str);
    }

    public static String getThemePaintColorKey(String str) {
        return defaultChatPaintColors.get(str);
    }

    private static void addChatDrawable(String str, Drawable drawable, String str2) {
        defaultChatDrawables.put(str, drawable);
        if (str2 != null) {
            defaultChatDrawableColorKeys.put(str, str2);
        }
    }

    private static void addChatPaint(String str, Paint paint, String str2) {
        defaultChatPaints.put(str, paint);
        if (str2 != null) {
            defaultChatPaintColors.put(str, str2);
        }
    }

    public static boolean isCurrentThemeDay() {
        return !getActiveTheme().isDark();
    }

    public static boolean isHome(ThemeAccent themeAccent) {
        ThemeInfo themeInfo = themeAccent.parentTheme;
        if (themeInfo == null) {
            return false;
        }
        if (themeInfo.getKey().equals("Blue") && themeAccent.id == 99) {
            return true;
        }
        if (themeAccent.parentTheme.getKey().equals("Day") && themeAccent.id == 9) {
            return true;
        }
        if ((themeAccent.parentTheme.getKey().equals("Night") || themeAccent.parentTheme.getKey().equals("Dark Blue")) && themeAccent.id == 0) {
            return true;
        }
        return false;
    }
}
