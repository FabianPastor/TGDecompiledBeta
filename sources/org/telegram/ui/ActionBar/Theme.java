package org.telegram.ui.ActionBar;

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
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Base64;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.StateSet;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.telegram.messenger.ChatThemeController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesController$$ExternalSyntheticLambda114;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.time.SunDate;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Cells.ThemesHorizontalListCell;
import org.telegram.ui.Components.AudioVisualizerDrawable;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.ChatThemeBottomSheet;
import org.telegram.ui.Components.ChoosingStickerStatusDrawable;
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
import org.telegram.ui.RoundVideoProgressShadow;

public class Theme {
    public static final int ACTION_BAR_AUDIO_SELECTOR_COLOR = NUM;
    public static final int ACTION_BAR_MEDIA_PICKER_COLOR = -13421773;
    public static final int ACTION_BAR_PHOTO_VIEWER_COLOR = NUM;
    public static final int ACTION_BAR_PICKER_SELECTOR_COLOR = -12763843;
    public static final int ACTION_BAR_PLAYER_COLOR = -1;
    public static final int ACTION_BAR_VIDEO_EDIT_COLOR = -16777216;
    public static final int ACTION_BAR_WHITE_SELECTOR_COLOR = NUM;
    public static final int ARTICLE_VIEWER_MEDIA_PROGRESS_COLOR = -1;
    public static final int AUTO_NIGHT_TYPE_AUTOMATIC = 2;
    public static final int AUTO_NIGHT_TYPE_NONE = 0;
    public static final int AUTO_NIGHT_TYPE_SCHEDULED = 1;
    public static final int AUTO_NIGHT_TYPE_SYSTEM = 3;
    private static Field BitmapDrawable_mColorFilter = null;
    public static final String COLOR_BACKGROUND_SLUG = "c";
    public static int DEFALT_THEME_ACCENT_ID = 99;
    public static final String DEFAULT_BACKGROUND_SLUG = "d";
    private static final int LIGHT_SENSOR_THEME_SWITCH_DELAY = 1800;
    private static final int LIGHT_SENSOR_THEME_SWITCH_NEAR_DELAY = 12000;
    private static final int LIGHT_SENSOR_THEME_SWITCH_NEAR_THRESHOLD = 12000;
    private static final float MAXIMUM_LUX_BREAKPOINT = 500.0f;
    public static final int MSG_OUT_COLOR_BLACK = -14606047;
    public static final int MSG_OUT_COLOR_WHITE = -1;
    private static Method StateListDrawable_getStateDrawableMethod = null;
    public static final String THEME_BACKGROUND_SLUG = "t";
    private static SensorEventListener ambientSensorListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            float lux = event.values[0];
            if (lux <= 0.0f) {
                lux = 0.1f;
            }
            if (!ApplicationLoader.mainInterfacePaused && ApplicationLoader.isScreenOn) {
                if (lux > 500.0f) {
                    float unused = Theme.lastBrightnessValue = 1.0f;
                } else {
                    float unused2 = Theme.lastBrightnessValue = ((float) Math.ceil((Math.log((double) lux) * 9.932299613952637d) + 27.05900001525879d)) / 100.0f;
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

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
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
    public static Drawable chat_msgMediaBroadcastDrawable = null;
    public static Drawable chat_msgMediaCheckDrawable = null;
    public static Drawable chat_msgMediaHalfCheckDrawable = null;
    public static Drawable chat_msgMediaMenuDrawable = null;
    public static Drawable chat_msgMediaPinnedDrawable = null;
    public static Drawable chat_msgMediaRepliesDrawable = null;
    public static Drawable chat_msgMediaViewsDrawable = null;
    public static Drawable chat_msgNoSoundDrawable = null;
    public static Drawable chat_msgOutBroadcastDrawable = null;
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
    public static Drawable chat_msgOutLocationDrawable = null;
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
    public static Drawable[][] chat_photoStatesDrawables = ((Drawable[][]) Array.newInstance(Drawable.class, new int[]{13, 2}));
    public static Drawable[] chat_pollCheckDrawable = new Drawable[2];
    public static Drawable[] chat_pollCrossDrawable = new Drawable[2];
    public static Drawable[] chat_pollHintDrawable = new Drawable[2];
    public static Paint chat_pollTimerPaint = null;
    public static Drawable[] chat_psaHelpDrawable = new Drawable[2];
    public static Paint chat_radialProgress2Paint = null;
    public static Paint chat_radialProgressPaint = null;
    public static Paint chat_radialProgressPausedPaint = null;
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
    public static int currentColor = 0;
    /* access modifiers changed from: private */
    public static HashMap<String, Integer> currentColors = new HashMap<>();
    private static HashMap<String, Integer> currentColorsNoAccent = new HashMap<>();
    private static ThemeInfo currentDayTheme = null;
    /* access modifiers changed from: private */
    public static ThemeInfo currentNightTheme = null;
    private static ColorFilter currentShareColorFilter = null;
    private static int currentShareColorFilterColor = 0;
    private static ColorFilter currentShareSelectedColorFilter = null;
    private static int currentShareSelectedColorFilterColor = 0;
    /* access modifiers changed from: private */
    public static ThemeInfo currentTheme = null;
    private static final HashMap<String, String> defaultChatDrawableColorKeys = new HashMap<>();
    private static final HashMap<String, Drawable> defaultChatDrawables = new HashMap<>();
    private static final HashMap<String, String> defaultChatPaintColors = new HashMap<>();
    private static final HashMap<String, Paint> defaultChatPaints = new HashMap<>();
    /* access modifiers changed from: private */
    public static HashMap<String, Integer> defaultColors = new HashMap<>();
    public static final ArrayList<ChatThemeBottomSheet.ChatThemeItem> defaultEmojiThemes = new ArrayList<>();
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
    public static final String key_actionBarActionModeDefault = "actionBarActionModeDefault";
    public static final String key_actionBarActionModeDefaultIcon = "actionBarActionModeDefaultIcon";
    public static final String key_actionBarActionModeDefaultSelector = "actionBarActionModeDefaultSelector";
    public static final String key_actionBarActionModeDefaultTop = "actionBarActionModeDefaultTop";
    public static final String key_actionBarBrowser = "actionBarBrowser";
    public static final String key_actionBarDefault = "actionBarDefault";
    public static final String key_actionBarDefaultArchived = "actionBarDefaultArchived";
    public static final String key_actionBarDefaultArchivedIcon = "actionBarDefaultArchivedIcon";
    public static final String key_actionBarDefaultArchivedSearch = "actionBarDefaultArchivedSearch";
    public static final String key_actionBarDefaultArchivedSearchPlaceholder = "actionBarDefaultSearchArchivedPlaceholder";
    public static final String key_actionBarDefaultArchivedSelector = "actionBarDefaultArchivedSelector";
    public static final String key_actionBarDefaultArchivedTitle = "actionBarDefaultArchivedTitle";
    public static final String key_actionBarDefaultIcon = "actionBarDefaultIcon";
    public static final String key_actionBarDefaultSearch = "actionBarDefaultSearch";
    public static final String key_actionBarDefaultSearchPlaceholder = "actionBarDefaultSearchPlaceholder";
    public static final String key_actionBarDefaultSelector = "actionBarDefaultSelector";
    public static final String key_actionBarDefaultSubmenuBackground = "actionBarDefaultSubmenuBackground";
    public static final String key_actionBarDefaultSubmenuItem = "actionBarDefaultSubmenuItem";
    public static final String key_actionBarDefaultSubmenuItemIcon = "actionBarDefaultSubmenuItemIcon";
    public static final String key_actionBarDefaultSubtitle = "actionBarDefaultSubtitle";
    public static final String key_actionBarDefaultTitle = "actionBarDefaultTitle";
    public static final String key_actionBarTabActiveText = "actionBarTabActiveText";
    public static final String key_actionBarTabLine = "actionBarTabLine";
    public static final String key_actionBarTabSelector = "actionBarTabSelector";
    public static final String key_actionBarTabUnactiveText = "actionBarTabUnactiveText";
    public static final String key_actionBarTipBackground = "actionBarTipBackground";
    public static final String key_actionBarWhiteSelector = "actionBarWhiteSelector";
    public static final String key_avatar_actionBarIconBlue = "avatar_actionBarIconBlue";
    public static final String key_avatar_actionBarSelectorBlue = "avatar_actionBarSelectorBlue";
    public static final String key_avatar_backgroundActionBarBlue = "avatar_backgroundActionBarBlue";
    public static final String key_avatar_backgroundArchived = "avatar_backgroundArchived";
    public static final String key_avatar_backgroundArchivedHidden = "avatar_backgroundArchivedHidden";
    public static final String key_avatar_backgroundBlue = "avatar_backgroundBlue";
    public static final String key_avatar_backgroundCyan = "avatar_backgroundCyan";
    public static final String key_avatar_backgroundGreen = "avatar_backgroundGreen";
    public static final String key_avatar_backgroundInProfileBlue = "avatar_backgroundInProfileBlue";
    public static final String key_avatar_backgroundOrange = "avatar_backgroundOrange";
    public static final String key_avatar_backgroundPink = "avatar_backgroundPink";
    public static final String key_avatar_backgroundRed = "avatar_backgroundRed";
    public static final String key_avatar_backgroundSaved = "avatar_backgroundSaved";
    public static final String key_avatar_backgroundViolet = "avatar_backgroundViolet";
    public static final String key_avatar_nameInMessageBlue = "avatar_nameInMessageBlue";
    public static final String key_avatar_nameInMessageCyan = "avatar_nameInMessageCyan";
    public static final String key_avatar_nameInMessageGreen = "avatar_nameInMessageGreen";
    public static final String key_avatar_nameInMessageOrange = "avatar_nameInMessageOrange";
    public static final String key_avatar_nameInMessagePink = "avatar_nameInMessagePink";
    public static final String key_avatar_nameInMessageRed = "avatar_nameInMessageRed";
    public static final String key_avatar_nameInMessageViolet = "avatar_nameInMessageViolet";
    public static final String key_avatar_subtitleInProfileBlue = "avatar_subtitleInProfileBlue";
    public static final String key_avatar_text = "avatar_text";
    public static final String key_calls_callReceivedGreenIcon = "calls_callReceivedGreenIcon";
    public static final String key_calls_callReceivedRedIcon = "calls_callReceivedRedIcon";
    public static final String key_changephoneinfo_image = "changephoneinfo_image";
    public static final String key_changephoneinfo_image2 = "changephoneinfo_image2";
    public static final String key_chat_TextSelectionCursor = "chat_TextSelectionCursor";
    public static final String key_chat_addContact = "chat_addContact";
    public static final String key_chat_attachActiveTab = "chat_attachActiveTab";
    public static final String key_chat_attachAudioBackground = "chat_attachAudioBackground";
    public static final String key_chat_attachAudioIcon = "chat_attachAudioIcon";
    public static final String key_chat_attachAudioText = "chat_attachAudioText";
    public static final String key_chat_attachCheckBoxBackground = "chat_attachCheckBoxBackground";
    public static final String key_chat_attachCheckBoxCheck = "chat_attachCheckBoxCheck";
    public static final String key_chat_attachContactBackground = "chat_attachContactBackground";
    public static final String key_chat_attachContactIcon = "chat_attachContactIcon";
    public static final String key_chat_attachContactText = "chat_attachContactText";
    public static final String key_chat_attachEmptyImage = "chat_attachEmptyImage";
    public static final String key_chat_attachFileBackground = "chat_attachFileBackground";
    public static final String key_chat_attachFileIcon = "chat_attachFileIcon";
    public static final String key_chat_attachFileText = "chat_attachFileText";
    public static final String key_chat_attachGalleryBackground = "chat_attachGalleryBackground";
    public static final String key_chat_attachGalleryIcon = "chat_attachGalleryIcon";
    public static final String key_chat_attachGalleryText = "chat_attachGalleryText";
    public static final String key_chat_attachLocationBackground = "chat_attachLocationBackground";
    public static final String key_chat_attachLocationIcon = "chat_attachLocationIcon";
    public static final String key_chat_attachLocationText = "chat_attachLocationText";
    public static final String key_chat_attachMediaBanBackground = "chat_attachMediaBanBackground";
    public static final String key_chat_attachMediaBanText = "chat_attachMediaBanText";
    public static final String key_chat_attachPermissionImage = "chat_attachPermissionImage";
    public static final String key_chat_attachPermissionMark = "chat_attachPermissionMark";
    public static final String key_chat_attachPermissionText = "chat_attachPermissionText";
    public static final String key_chat_attachPhotoBackground = "chat_attachPhotoBackground";
    public static final String key_chat_attachPollBackground = "chat_attachPollBackground";
    public static final String key_chat_attachPollIcon = "chat_attachPollIcon";
    public static final String key_chat_attachPollText = "chat_attachPollText";
    public static final String key_chat_attachUnactiveTab = "chat_attachUnactiveTab";
    public static final String key_chat_botButtonText = "chat_botButtonText";
    public static final String key_chat_botKeyboardButtonBackground = "chat_botKeyboardButtonBackground";
    public static final String key_chat_botKeyboardButtonBackgroundPressed = "chat_botKeyboardButtonBackgroundPressed";
    public static final String key_chat_botKeyboardButtonText = "chat_botKeyboardButtonText";
    public static final String key_chat_botProgress = "chat_botProgress";
    public static final String key_chat_botSwitchToInlineText = "chat_botSwitchToInlineText";
    public static final String key_chat_emojiBottomPanelIcon = "chat_emojiBottomPanelIcon";
    public static final String key_chat_emojiPanelBackground = "chat_emojiPanelBackground";
    public static final String key_chat_emojiPanelBackspace = "chat_emojiPanelBackspace";
    public static final String key_chat_emojiPanelBadgeBackground = "chat_emojiPanelBadgeBackground";
    public static final String key_chat_emojiPanelBadgeText = "chat_emojiPanelBadgeText";
    public static final String key_chat_emojiPanelEmptyText = "chat_emojiPanelEmptyText";
    public static final String key_chat_emojiPanelIcon = "chat_emojiPanelIcon";
    public static final String key_chat_emojiPanelIconSelected = "chat_emojiPanelIconSelected";
    public static final String key_chat_emojiPanelMasksIcon = "chat_emojiPanelMasksIcon";
    public static final String key_chat_emojiPanelMasksIconSelected = "chat_emojiPanelMasksIconSelected";
    public static final String key_chat_emojiPanelNewTrending = "chat_emojiPanelNewTrending";
    public static final String key_chat_emojiPanelShadowLine = "chat_emojiPanelShadowLine";
    public static final String key_chat_emojiPanelStickerPackSelector = "chat_emojiPanelStickerPackSelector";
    public static final String key_chat_emojiPanelStickerPackSelectorLine = "chat_emojiPanelStickerPackSelectorLine";
    public static final String key_chat_emojiPanelStickerSetName = "chat_emojiPanelStickerSetName";
    public static final String key_chat_emojiPanelStickerSetNameHighlight = "chat_emojiPanelStickerSetNameHighlight";
    public static final String key_chat_emojiPanelStickerSetNameIcon = "chat_emojiPanelStickerSetNameIcon";
    public static final String key_chat_emojiPanelTrendingDescription = "chat_emojiPanelTrendingDescription";
    public static final String key_chat_emojiPanelTrendingTitle = "chat_emojiPanelTrendingTitle";
    public static final String key_chat_emojiSearchBackground = "chat_emojiSearchBackground";
    public static final String key_chat_emojiSearchIcon = "chat_emojiSearchIcon";
    public static final String key_chat_fieldOverlayText = "chat_fieldOverlayText";
    public static final String key_chat_gifSaveHintBackground = "chat_gifSaveHintBackground";
    public static final String key_chat_gifSaveHintText = "chat_gifSaveHintText";
    public static final String key_chat_goDownButton = "chat_goDownButton";
    public static final String key_chat_goDownButtonCounter = "chat_goDownButtonCounter";
    public static final String key_chat_goDownButtonCounterBackground = "chat_goDownButtonCounterBackground";
    public static final String key_chat_goDownButtonIcon = "chat_goDownButtonIcon";
    public static final String key_chat_goDownButtonShadow = "chat_goDownButtonShadow";
    public static final String key_chat_inAdminSelectedText = "chat_adminSelectedText";
    public static final String key_chat_inAdminText = "chat_adminText";
    public static final String key_chat_inAudioCacheSeekbar = "chat_inAudioCacheSeekbar";
    public static final String key_chat_inAudioDurationSelectedText = "chat_inAudioDurationSelectedText";
    public static final String key_chat_inAudioDurationText = "chat_inAudioDurationText";
    public static final String key_chat_inAudioPerformerSelectedText = "chat_inAudioPerfomerSelectedText";
    public static final String key_chat_inAudioPerformerText = "chat_inAudioPerfomerText";
    public static final String key_chat_inAudioProgress = "chat_inAudioProgress";
    public static final String key_chat_inAudioSeekbar = "chat_inAudioSeekbar";
    public static final String key_chat_inAudioSeekbarFill = "chat_inAudioSeekbarFill";
    public static final String key_chat_inAudioSeekbarSelected = "chat_inAudioSeekbarSelected";
    public static final String key_chat_inAudioSelectedProgress = "chat_inAudioSelectedProgress";
    public static final String key_chat_inAudioTitleText = "chat_inAudioTitleText";
    public static final String key_chat_inBubble = "chat_inBubble";
    public static final String key_chat_inBubbleSelected = "chat_inBubbleSelected";
    public static final String key_chat_inBubbleShadow = "chat_inBubbleShadow";
    public static final String key_chat_inContactBackground = "chat_inContactBackground";
    public static final String key_chat_inContactIcon = "chat_inContactIcon";
    public static final String key_chat_inContactNameText = "chat_inContactNameText";
    public static final String key_chat_inContactPhoneSelectedText = "chat_inContactPhoneSelectedText";
    public static final String key_chat_inContactPhoneText = "chat_inContactPhoneText";
    public static final String key_chat_inFileBackground = "chat_inFileBackground";
    public static final String key_chat_inFileBackgroundSelected = "chat_inFileBackgroundSelected";
    public static final String key_chat_inFileIcon = "chat_inFileIcon";
    public static final String key_chat_inFileInfoSelectedText = "chat_inFileInfoSelectedText";
    public static final String key_chat_inFileInfoText = "chat_inFileInfoText";
    public static final String key_chat_inFileNameText = "chat_inFileNameText";
    public static final String key_chat_inFileProgress = "chat_inFileProgress";
    public static final String key_chat_inFileProgressSelected = "chat_inFileProgressSelected";
    public static final String key_chat_inFileSelectedIcon = "chat_inFileSelectedIcon";
    public static final String key_chat_inForwardedNameText = "chat_inForwardedNameText";
    public static final String key_chat_inGreenCall = "chat_inDownCall";
    public static final String key_chat_inInstant = "chat_inInstant";
    public static final String key_chat_inInstantSelected = "chat_inInstantSelected";
    public static final String key_chat_inLoader = "chat_inLoader";
    public static final String key_chat_inLoaderPhoto = "chat_inLoaderPhoto";
    public static final String key_chat_inLoaderPhotoIcon = "chat_inLoaderPhotoIcon";
    public static final String key_chat_inLoaderPhotoIconSelected = "chat_inLoaderPhotoIconSelected";
    public static final String key_chat_inLoaderPhotoSelected = "chat_inLoaderPhotoSelected";
    public static final String key_chat_inLoaderSelected = "chat_inLoaderSelected";
    public static final String key_chat_inLocationBackground = "chat_inLocationBackground";
    public static final String key_chat_inLocationIcon = "chat_inLocationIcon";
    public static final String key_chat_inMediaIcon = "chat_inMediaIcon";
    public static final String key_chat_inMediaIconSelected = "chat_inMediaIconSelected";
    public static final String key_chat_inMenu = "chat_inMenu";
    public static final String key_chat_inMenuSelected = "chat_inMenuSelected";
    public static final String key_chat_inPollCorrectAnswer = "chat_inPollCorrectAnswer";
    public static final String key_chat_inPollWrongAnswer = "chat_inPollWrongAnswer";
    public static final String key_chat_inPreviewInstantSelectedText = "chat_inPreviewInstantSelectedText";
    public static final String key_chat_inPreviewInstantText = "chat_inPreviewInstantText";
    public static final String key_chat_inPreviewLine = "chat_inPreviewLine";
    public static final String key_chat_inPsaNameText = "chat_inPsaNameText";
    public static final String key_chat_inRedCall = "chat_inUpCall";
    public static final String key_chat_inReplyLine = "chat_inReplyLine";
    public static final String key_chat_inReplyMediaMessageSelectedText = "chat_inReplyMediaMessageSelectedText";
    public static final String key_chat_inReplyMediaMessageText = "chat_inReplyMediaMessageText";
    public static final String key_chat_inReplyMessageText = "chat_inReplyMessageText";
    public static final String key_chat_inReplyNameText = "chat_inReplyNameText";
    public static final String key_chat_inSentClock = "chat_inSentClock";
    public static final String key_chat_inSentClockSelected = "chat_inSentClockSelected";
    public static final String key_chat_inSiteNameText = "chat_inSiteNameText";
    public static final String key_chat_inTextSelectionHighlight = "chat_inTextSelectionHighlight";
    public static final String key_chat_inTimeSelectedText = "chat_inTimeSelectedText";
    public static final String key_chat_inTimeText = "chat_inTimeText";
    public static final String key_chat_inVenueInfoSelectedText = "chat_inVenueInfoSelectedText";
    public static final String key_chat_inVenueInfoText = "chat_inVenueInfoText";
    public static final String key_chat_inViaBotNameText = "chat_inViaBotNameText";
    public static final String key_chat_inViews = "chat_inViews";
    public static final String key_chat_inViewsSelected = "chat_inViewsSelected";
    public static final String key_chat_inVoiceSeekbar = "chat_inVoiceSeekbar";
    public static final String key_chat_inVoiceSeekbarFill = "chat_inVoiceSeekbarFill";
    public static final String key_chat_inVoiceSeekbarSelected = "chat_inVoiceSeekbarSelected";
    public static final String key_chat_inlineResultIcon = "chat_inlineResultIcon";
    public static final String key_chat_linkSelectBackground = "chat_linkSelectBackground";
    public static final String key_chat_lockIcon = "chat_lockIcon";
    public static final String key_chat_mediaBroadcast = "chat_mediaBroadcast";
    public static final String key_chat_mediaInfoText = "chat_mediaInfoText";
    public static final String key_chat_mediaLoaderPhoto = "chat_mediaLoaderPhoto";
    public static final String key_chat_mediaLoaderPhotoIcon = "chat_mediaLoaderPhotoIcon";
    public static final String key_chat_mediaLoaderPhotoIconSelected = "chat_mediaLoaderPhotoIconSelected";
    public static final String key_chat_mediaLoaderPhotoSelected = "chat_mediaLoaderPhotoSelected";
    public static final String key_chat_mediaMenu = "chat_mediaMenu";
    public static final String key_chat_mediaProgress = "chat_mediaProgress";
    public static final String key_chat_mediaSentCheck = "chat_mediaSentCheck";
    public static final String key_chat_mediaSentClock = "chat_mediaSentClock";
    public static final String key_chat_mediaTimeBackground = "chat_mediaTimeBackground";
    public static final String key_chat_mediaTimeText = "chat_mediaTimeText";
    public static final String key_chat_mediaViews = "chat_mediaViews";
    public static final String key_chat_messageLinkIn = "chat_messageLinkIn";
    public static final String key_chat_messageLinkOut = "chat_messageLinkOut";
    public static final String key_chat_messagePanelBackground = "chat_messagePanelBackground";
    public static final String key_chat_messagePanelCancelInlineBot = "chat_messagePanelCancelInlineBot";
    public static final String key_chat_messagePanelCursor = "chat_messagePanelCursor";
    public static final String key_chat_messagePanelHint = "chat_messagePanelHint";
    public static final String key_chat_messagePanelIcons = "chat_messagePanelIcons";
    public static final String key_chat_messagePanelSend = "chat_messagePanelSend";
    public static final String key_chat_messagePanelShadow = "chat_messagePanelShadow";
    public static final String key_chat_messagePanelText = "chat_messagePanelText";
    public static final String key_chat_messagePanelVideoFrame = "chat_messagePanelVideoFrame";
    public static final String key_chat_messagePanelVoiceBackground = "chat_messagePanelVoiceBackground";
    public static final String key_chat_messagePanelVoiceDelete = "chat_messagePanelVoiceDelete";
    public static final String key_chat_messagePanelVoiceDuration = "chat_messagePanelVoiceDuration";
    public static final String key_chat_messagePanelVoiceLock = "key_chat_messagePanelVoiceLock";
    public static final String key_chat_messagePanelVoiceLockBackground = "key_chat_messagePanelVoiceLockBackground";
    public static final String key_chat_messagePanelVoiceLockShadow = "key_chat_messagePanelVoiceLockShadow";
    public static final String key_chat_messagePanelVoicePressed = "chat_messagePanelVoicePressed";
    public static final String key_chat_messageTextIn = "chat_messageTextIn";
    public static final String key_chat_messageTextOut = "chat_messageTextOut";
    public static final String key_chat_muteIcon = "chat_muteIcon";
    public static final String key_chat_outAdminSelectedText = "chat_outAdminSelectedText";
    public static final String key_chat_outAdminText = "chat_outAdminText";
    public static final String key_chat_outAudioCacheSeekbar = "chat_outAudioCacheSeekbar";
    public static final String key_chat_outAudioDurationSelectedText = "chat_outAudioDurationSelectedText";
    public static final String key_chat_outAudioDurationText = "chat_outAudioDurationText";
    public static final String key_chat_outAudioPerformerSelectedText = "chat_outAudioPerfomerSelectedText";
    public static final String key_chat_outAudioPerformerText = "chat_outAudioPerfomerText";
    public static final String key_chat_outAudioProgress = "chat_outAudioProgress";
    public static final String key_chat_outAudioSeekbar = "chat_outAudioSeekbar";
    public static final String key_chat_outAudioSeekbarFill = "chat_outAudioSeekbarFill";
    public static final String key_chat_outAudioSeekbarSelected = "chat_outAudioSeekbarSelected";
    public static final String key_chat_outAudioSelectedProgress = "chat_outAudioSelectedProgress";
    public static final String key_chat_outAudioTitleText = "chat_outAudioTitleText";
    public static final String key_chat_outBroadcast = "chat_outBroadcast";
    public static final String key_chat_outBubble = "chat_outBubble";
    public static final String key_chat_outBubbleGradient1 = "chat_outBubbleGradient";
    public static final String key_chat_outBubbleGradient2 = "chat_outBubbleGradient2";
    public static final String key_chat_outBubbleGradient3 = "chat_outBubbleGradient3";
    public static final String key_chat_outBubbleGradientAnimated = "chat_outBubbleGradientAnimated";
    public static final String key_chat_outBubbleGradientSelectedOverlay = "chat_outBubbleGradientSelectedOverlay";
    public static final String key_chat_outBubbleSelected = "chat_outBubbleSelected";
    public static final String key_chat_outBubbleShadow = "chat_outBubbleShadow";
    public static final String key_chat_outContactBackground = "chat_outContactBackground";
    public static final String key_chat_outContactIcon = "chat_outContactIcon";
    public static final String key_chat_outContactNameText = "chat_outContactNameText";
    public static final String key_chat_outContactPhoneSelectedText = "chat_outContactPhoneSelectedText";
    public static final String key_chat_outContactPhoneText = "chat_outContactPhoneText";
    public static final String key_chat_outFileBackground = "chat_outFileBackground";
    public static final String key_chat_outFileBackgroundSelected = "chat_outFileBackgroundSelected";
    public static final String key_chat_outFileIcon = "chat_outFileIcon";
    public static final String key_chat_outFileInfoSelectedText = "chat_outFileInfoSelectedText";
    public static final String key_chat_outFileInfoText = "chat_outFileInfoText";
    public static final String key_chat_outFileNameText = "chat_outFileNameText";
    public static final String key_chat_outFileProgress = "chat_outFileProgress";
    public static final String key_chat_outFileProgressSelected = "chat_outFileProgressSelected";
    public static final String key_chat_outFileSelectedIcon = "chat_outFileSelectedIcon";
    public static final String key_chat_outForwardedNameText = "chat_outForwardedNameText";
    public static final String key_chat_outGreenCall = "chat_outUpCall";
    public static final String key_chat_outInstant = "chat_outInstant";
    public static final String key_chat_outInstantSelected = "chat_outInstantSelected";
    public static final String key_chat_outLoader = "chat_outLoader";
    public static final String key_chat_outLoaderPhoto = "chat_outLoaderPhoto";
    public static final String key_chat_outLoaderPhotoIcon = "chat_outLoaderPhotoIcon";
    public static final String key_chat_outLoaderPhotoIconSelected = "chat_outLoaderPhotoIconSelected";
    public static final String key_chat_outLoaderPhotoSelected = "chat_outLoaderPhotoSelected";
    public static final String key_chat_outLoaderSelected = "chat_outLoaderSelected";
    public static final String key_chat_outLocationBackground = "chat_outLocationBackground";
    public static final String key_chat_outLocationIcon = "chat_outLocationIcon";
    public static final String key_chat_outMediaIcon = "chat_outMediaIcon";
    public static final String key_chat_outMediaIconSelected = "chat_outMediaIconSelected";
    public static final String key_chat_outMenu = "chat_outMenu";
    public static final String key_chat_outMenuSelected = "chat_outMenuSelected";
    public static final String key_chat_outPollCorrectAnswer = "chat_outPollCorrectAnswer";
    public static final String key_chat_outPollWrongAnswer = "chat_outPollWrongAnswer";
    public static final String key_chat_outPreviewInstantSelectedText = "chat_outPreviewInstantSelectedText";
    public static final String key_chat_outPreviewInstantText = "chat_outPreviewInstantText";
    public static final String key_chat_outPreviewLine = "chat_outPreviewLine";
    public static final String key_chat_outPsaNameText = "chat_outPsaNameText";
    public static final String key_chat_outReplyLine = "chat_outReplyLine";
    public static final String key_chat_outReplyMediaMessageSelectedText = "chat_outReplyMediaMessageSelectedText";
    public static final String key_chat_outReplyMediaMessageText = "chat_outReplyMediaMessageText";
    public static final String key_chat_outReplyMessageText = "chat_outReplyMessageText";
    public static final String key_chat_outReplyNameText = "chat_outReplyNameText";
    public static final String key_chat_outSentCheck = "chat_outSentCheck";
    public static final String key_chat_outSentCheckRead = "chat_outSentCheckRead";
    public static final String key_chat_outSentCheckReadSelected = "chat_outSentCheckReadSelected";
    public static final String key_chat_outSentCheckSelected = "chat_outSentCheckSelected";
    public static final String key_chat_outSentClock = "chat_outSentClock";
    public static final String key_chat_outSentClockSelected = "chat_outSentClockSelected";
    public static final String key_chat_outSiteNameText = "chat_outSiteNameText";
    public static final String key_chat_outTextSelectionHighlight = "chat_outTextSelectionHighlight";
    public static final String key_chat_outTimeSelectedText = "chat_outTimeSelectedText";
    public static final String key_chat_outTimeText = "chat_outTimeText";
    public static final String key_chat_outVenueInfoSelectedText = "chat_outVenueInfoSelectedText";
    public static final String key_chat_outVenueInfoText = "chat_outVenueInfoText";
    public static final String key_chat_outViaBotNameText = "chat_outViaBotNameText";
    public static final String key_chat_outViews = "chat_outViews";
    public static final String key_chat_outViewsSelected = "chat_outViewsSelected";
    public static final String key_chat_outVoiceSeekbar = "chat_outVoiceSeekbar";
    public static final String key_chat_outVoiceSeekbarFill = "chat_outVoiceSeekbarFill";
    public static final String key_chat_outVoiceSeekbarSelected = "chat_outVoiceSeekbarSelected";
    public static final String key_chat_previewDurationText = "chat_previewDurationText";
    public static final String key_chat_previewGameText = "chat_previewGameText";
    public static final String key_chat_recordTime = "chat_recordTime";
    public static final String key_chat_recordVoiceCancel = "chat_recordVoiceCancel";
    public static final String key_chat_recordedVoiceBackground = "chat_recordedVoiceBackground";
    public static final String key_chat_recordedVoiceDot = "chat_recordedVoiceDot";
    public static final String key_chat_recordedVoiceHighlight = "key_chat_recordedVoiceHighlight";
    public static final String key_chat_recordedVoicePlayPause = "chat_recordedVoicePlayPause";
    public static final String key_chat_recordedVoiceProgress = "chat_recordedVoiceProgress";
    public static final String key_chat_recordedVoiceProgressInner = "chat_recordedVoiceProgressInner";
    public static final String key_chat_replyPanelClose = "chat_replyPanelClose";
    public static final String key_chat_replyPanelIcons = "chat_replyPanelIcons";
    public static final String key_chat_replyPanelLine = "chat_replyPanelLine";
    public static final String key_chat_replyPanelMessage = "chat_replyPanelMessage";
    public static final String key_chat_replyPanelName = "chat_replyPanelName";
    public static final String key_chat_reportSpam = "chat_reportSpam";
    public static final String key_chat_searchPanelIcons = "chat_searchPanelIcons";
    public static final String key_chat_searchPanelText = "chat_searchPanelText";
    public static final String key_chat_secretChatStatusText = "chat_secretChatStatusText";
    public static final String key_chat_secretTimeText = "chat_secretTimeText";
    public static final String key_chat_secretTimerBackground = "chat_secretTimerBackground";
    public static final String key_chat_secretTimerText = "chat_secretTimerText";
    public static final String key_chat_selectedBackground = "chat_selectedBackground";
    public static final String key_chat_sentError = "chat_sentError";
    public static final String key_chat_sentErrorIcon = "chat_sentErrorIcon";
    public static final String key_chat_serviceBackground = "chat_serviceBackground";
    public static final String key_chat_serviceBackgroundSelected = "chat_serviceBackgroundSelected";
    public static final String key_chat_serviceIcon = "chat_serviceIcon";
    public static final String key_chat_serviceLink = "chat_serviceLink";
    public static final String key_chat_serviceText = "chat_serviceText";
    public static final String key_chat_status = "chat_status";
    public static final String key_chat_stickerNameText = "chat_stickerNameText";
    public static final String key_chat_stickerReplyLine = "chat_stickerReplyLine";
    public static final String key_chat_stickerReplyMessageText = "chat_stickerReplyMessageText";
    public static final String key_chat_stickerReplyNameText = "chat_stickerReplyNameText";
    public static final String key_chat_stickerViaBotNameText = "chat_stickerViaBotNameText";
    public static final String key_chat_stickersHintPanel = "chat_stickersHintPanel";
    public static final String key_chat_textSelectBackground = "chat_textSelectBackground";
    public static final String key_chat_topPanelBackground = "chat_topPanelBackground";
    public static final String key_chat_topPanelClose = "chat_topPanelClose";
    public static final String key_chat_topPanelLine = "chat_topPanelLine";
    public static final String key_chat_topPanelMessage = "chat_topPanelMessage";
    public static final String key_chat_topPanelTitle = "chat_topPanelTitle";
    public static final String key_chat_unreadMessagesStartArrowIcon = "chat_unreadMessagesStartArrowIcon";
    public static final String key_chat_unreadMessagesStartBackground = "chat_unreadMessagesStartBackground";
    public static final String key_chat_unreadMessagesStartText = "chat_unreadMessagesStartText";
    public static final String key_chat_wallpaper = "chat_wallpaper";
    public static final String key_chat_wallpaper_gradient_rotation = "chat_wallpaper_gradient_rotation";
    public static final String key_chat_wallpaper_gradient_to1 = "chat_wallpaper_gradient_to";
    public static final String key_chat_wallpaper_gradient_to2 = "key_chat_wallpaper_gradient_to2";
    public static final String key_chat_wallpaper_gradient_to3 = "key_chat_wallpaper_gradient_to3";
    public static final String key_chats_actionBackground = "chats_actionBackground";
    public static final String key_chats_actionIcon = "chats_actionIcon";
    public static final String key_chats_actionMessage = "chats_actionMessage";
    public static final String key_chats_actionPressedBackground = "chats_actionPressedBackground";
    public static final String key_chats_actionUnreadBackground = "chats_actionUnreadBackground";
    public static final String key_chats_actionUnreadIcon = "chats_actionUnreadIcon";
    public static final String key_chats_actionUnreadPressedBackground = "chats_actionUnreadPressedBackground";
    public static final String key_chats_archiveBackground = "chats_archiveBackground";
    public static final String key_chats_archiveIcon = "chats_archiveIcon";
    public static final String key_chats_archivePinBackground = "chats_archivePinBackground";
    public static final String key_chats_archivePullDownBackground = "chats_archivePullDownBackground";
    public static final String key_chats_archivePullDownBackgroundActive = "chats_archivePullDownBackgroundActive";
    public static final String key_chats_archiveText = "chats_archiveText";
    public static final String key_chats_attachMessage = "chats_attachMessage";
    public static final String key_chats_date = "chats_date";
    public static final String key_chats_draft = "chats_draft";
    public static final String key_chats_mentionIcon = "chats_mentionIcon";
    public static final String key_chats_menuBackground = "chats_menuBackground";
    public static final String key_chats_menuCloud = "chats_menuCloud";
    public static final String key_chats_menuCloudBackgroundCats = "chats_menuCloudBackgroundCats";
    public static final String key_chats_menuItemCheck = "chats_menuItemCheck";
    public static final String key_chats_menuItemIcon = "chats_menuItemIcon";
    public static final String key_chats_menuItemText = "chats_menuItemText";
    public static final String key_chats_menuName = "chats_menuName";
    public static final String key_chats_menuPhone = "chats_menuPhone";
    public static final String key_chats_menuPhoneCats = "chats_menuPhoneCats";
    public static final String key_chats_menuTopBackground = "chats_menuTopBackground";
    public static final String key_chats_menuTopBackgroundCats = "chats_menuTopBackgroundCats";
    public static final String key_chats_menuTopShadow = "chats_menuTopShadow";
    public static final String key_chats_menuTopShadowCats = "chats_menuTopShadowCats";
    public static final String key_chats_message = "chats_message";
    public static final String key_chats_messageArchived = "chats_messageArchived";
    public static final String key_chats_message_threeLines = "chats_message_threeLines";
    public static final String key_chats_muteIcon = "chats_muteIcon";
    public static final String key_chats_name = "chats_name";
    public static final String key_chats_nameArchived = "chats_nameArchived";
    public static final String key_chats_nameIcon = "chats_nameIcon";
    public static final String key_chats_nameMessage = "chats_nameMessage";
    public static final String key_chats_nameMessageArchived = "chats_nameMessageArchived";
    public static final String key_chats_nameMessageArchived_threeLines = "chats_nameMessageArchived_threeLines";
    public static final String key_chats_nameMessage_threeLines = "chats_nameMessage_threeLines";
    public static final String key_chats_onlineCircle = "chats_onlineCircle";
    public static final String key_chats_pinnedIcon = "chats_pinnedIcon";
    public static final String key_chats_pinnedOverlay = "chats_pinnedOverlay";
    public static final String key_chats_secretIcon = "chats_secretIcon";
    public static final String key_chats_secretName = "chats_secretName";
    public static final String key_chats_sentCheck = "chats_sentCheck";
    public static final String key_chats_sentClock = "chats_sentClock";
    public static final String key_chats_sentError = "chats_sentError";
    public static final String key_chats_sentErrorIcon = "chats_sentErrorIcon";
    public static final String key_chats_sentReadCheck = "chats_sentReadCheck";
    public static final String key_chats_tabUnreadActiveBackground = "chats_tabUnreadActiveBackground";
    public static final String key_chats_tabUnreadUnactiveBackground = "chats_tabUnreadUnactiveBackground";
    public static final String key_chats_tabletSelectedOverlay = "chats_tabletSelectedOverlay";
    public static final String key_chats_unreadCounter = "chats_unreadCounter";
    public static final String key_chats_unreadCounterMuted = "chats_unreadCounterMuted";
    public static final String key_chats_unreadCounterText = "chats_unreadCounterText";
    public static final String key_chats_verifiedBackground = "chats_verifiedBackground";
    public static final String key_chats_verifiedCheck = "chats_verifiedCheck";
    public static final String key_checkbox = "checkbox";
    public static final String key_checkboxCheck = "checkboxCheck";
    public static final String key_checkboxDisabled = "checkboxDisabled";
    public static final String key_checkboxSquareBackground = "checkboxSquareBackground";
    public static final String key_checkboxSquareCheck = "checkboxSquareCheck";
    public static final String key_checkboxSquareDisabled = "checkboxSquareDisabled";
    public static final String key_checkboxSquareUnchecked = "checkboxSquareUnchecked";
    public static final String key_contacts_inviteBackground = "contacts_inviteBackground";
    public static final String key_contacts_inviteText = "contacts_inviteText";
    public static final String key_contextProgressInner1 = "contextProgressInner1";
    public static final String key_contextProgressInner2 = "contextProgressInner2";
    public static final String key_contextProgressInner3 = "contextProgressInner3";
    public static final String key_contextProgressInner4 = "contextProgressInner4";
    public static final String key_contextProgressOuter1 = "contextProgressOuter1";
    public static final String key_contextProgressOuter2 = "contextProgressOuter2";
    public static final String key_contextProgressOuter3 = "contextProgressOuter3";
    public static final String key_contextProgressOuter4 = "contextProgressOuter4";
    public static final String key_dialogBackground = "dialogBackground";
    public static final String key_dialogBackgroundGray = "dialogBackgroundGray";
    public static final String key_dialogBadgeBackground = "dialogBadgeBackground";
    public static final String key_dialogBadgeText = "dialogBadgeText";
    public static final String key_dialogButton = "dialogButton";
    public static final String key_dialogButtonSelector = "dialogButtonSelector";
    public static final String key_dialogCameraIcon = "dialogCameraIcon";
    public static final String key_dialogCheckboxSquareBackground = "dialogCheckboxSquareBackground";
    public static final String key_dialogCheckboxSquareCheck = "dialogCheckboxSquareCheck";
    public static final String key_dialogCheckboxSquareDisabled = "dialogCheckboxSquareDisabled";
    public static final String key_dialogCheckboxSquareUnchecked = "dialogCheckboxSquareUnchecked";
    public static final String key_dialogEmptyImage = "dialogEmptyImage";
    public static final String key_dialogEmptyText = "dialogEmptyText";
    public static final String key_dialogFloatingButton = "dialogFloatingButton";
    public static final String key_dialogFloatingButtonPressed = "dialogFloatingButtonPressed";
    public static final String key_dialogFloatingIcon = "dialogFloatingIcon";
    public static final String key_dialogGrayLine = "dialogGrayLine";
    public static final String key_dialogIcon = "dialogIcon";
    public static final String key_dialogInputField = "dialogInputField";
    public static final String key_dialogInputFieldActivated = "dialogInputFieldActivated";
    public static final String key_dialogLineProgress = "dialogLineProgress";
    public static final String key_dialogLineProgressBackground = "dialogLineProgressBackground";
    public static final String key_dialogLinkSelection = "dialogLinkSelection";
    public static final String key_dialogProgressCircle = "dialogProgressCircle";
    public static final String key_dialogRadioBackground = "dialogRadioBackground";
    public static final String key_dialogRadioBackgroundChecked = "dialogRadioBackgroundChecked";
    public static final String key_dialogRedIcon = "dialogRedIcon";
    public static final String key_dialogRoundCheckBox = "dialogRoundCheckBox";
    public static final String key_dialogRoundCheckBoxCheck = "dialogRoundCheckBoxCheck";
    public static final String key_dialogScrollGlow = "dialogScrollGlow";
    public static final String key_dialogSearchBackground = "dialogSearchBackground";
    public static final String key_dialogSearchHint = "dialogSearchHint";
    public static final String key_dialogSearchIcon = "dialogSearchIcon";
    public static final String key_dialogSearchText = "dialogSearchText";
    public static final String key_dialogShadowLine = "dialogShadowLine";
    public static final String key_dialogSwipeRemove = "dialogSwipeRemove";
    public static final String key_dialogTextBlack = "dialogTextBlack";
    public static final String key_dialogTextBlue = "dialogTextBlue";
    public static final String key_dialogTextBlue2 = "dialogTextBlue2";
    public static final String key_dialogTextBlue3 = "dialogTextBlue3";
    public static final String key_dialogTextBlue4 = "dialogTextBlue4";
    public static final String key_dialogTextGray = "dialogTextGray";
    public static final String key_dialogTextGray2 = "dialogTextGray2";
    public static final String key_dialogTextGray3 = "dialogTextGray3";
    public static final String key_dialogTextGray4 = "dialogTextGray4";
    public static final String key_dialogTextHint = "dialogTextHint";
    public static final String key_dialogTextLink = "dialogTextLink";
    public static final String key_dialogTextRed = "dialogTextRed";
    public static final String key_dialogTextRed2 = "dialogTextRed2";
    public static final String key_dialogTopBackground = "dialogTopBackground";
    public static final String key_dialog_inlineProgress = "dialog_inlineProgress";
    public static final String key_dialog_inlineProgressBackground = "dialog_inlineProgressBackground";
    public static final String key_dialog_liveLocationProgress = "dialog_liveLocationProgress";
    public static final String key_divider = "divider";
    public static final String key_drawable_botInline = "drawableBotInline";
    public static final String key_drawable_botLink = "drawableBotLink";
    public static final String key_drawable_chat_pollHintDrawableIn = "drawable_chat_pollHintDrawableIn";
    public static final String key_drawable_chat_pollHintDrawableOut = "drawable_chat_pollHintDrawableOut";
    public static final String key_drawable_commentSticker = "drawableCommentSticker";
    public static final String key_drawable_goIcon = "drawableGoIcon";
    public static final String key_drawable_lockIconDrawable = "drawableLockIcon";
    public static final String key_drawable_msgError = "drawableMsgError";
    public static final String key_drawable_msgIn = "drawableMsgIn";
    public static final String key_drawable_msgInClock = "drawableMsgInClock";
    public static final String key_drawable_msgInClockSelected = "drawableMsgInClockSelected";
    public static final String key_drawable_msgInMedia = "drawableMsgInMedia";
    public static final String key_drawable_msgInMediaSelected = "drawableMsgInMediaSelected";
    public static final String key_drawable_msgInSelected = "drawableMsgInSelected";
    public static final String key_drawable_msgOut = "drawableMsgOut";
    public static final String key_drawable_msgOutCallAudio = "drawableMsgOutCallAudio";
    public static final String key_drawable_msgOutCallAudioSelected = "drawableMsgOutCallAudioSelected";
    public static final String key_drawable_msgOutCallVideo = "drawableMsgOutCallVideo";
    public static final String key_drawable_msgOutCallVideoSelected = "drawableMsgOutCallVideo";
    public static final String key_drawable_msgOutCheck = "drawableMsgOutCheck";
    public static final String key_drawable_msgOutCheckRead = "drawableMsgOutCheckRead";
    public static final String key_drawable_msgOutCheckReadSelected = "drawableMsgOutCheckReadSelected";
    public static final String key_drawable_msgOutCheckSelected = "drawableMsgOutCheckSelected";
    public static final String key_drawable_msgOutHalfCheck = "drawableMsgOutHalfCheck";
    public static final String key_drawable_msgOutHalfCheckSelected = "drawableMsgOutHalfCheckSelected";
    public static final String key_drawable_msgOutInstant = "drawableMsgOutInstant";
    public static final String key_drawable_msgOutMedia = "drawableMsgOutMedia";
    public static final String key_drawable_msgOutMediaSelected = "drawableMsgOutMediaSelected";
    public static final String key_drawable_msgOutMenu = "drawableMsgOutMenu";
    public static final String key_drawable_msgOutMenuSelected = "drawableMsgOutMenuSelected";
    public static final String key_drawable_msgOutPinned = "drawableMsgOutPinned";
    public static final String key_drawable_msgOutPinnedSelected = "drawableMsgOutPinnedSelected";
    public static final String key_drawable_msgOutReplies = "drawableMsgOutReplies";
    public static final String key_drawable_msgOutRepliesSelected = "drawableMsgOutReplies";
    public static final String key_drawable_msgOutSelected = "drawableMsgOutSelected";
    public static final String key_drawable_msgOutViews = "drawableMsgOutViews";
    public static final String key_drawable_msgOutViewsSelected = "drawableMsgOutViewsSelected";
    public static final String key_drawable_msgStickerCheck = "drawableMsgStickerCheck";
    public static final String key_drawable_msgStickerClock = "drawableMsgStickerClock";
    public static final String key_drawable_msgStickerHalfCheck = "drawableMsgStickerHalfCheck";
    public static final String key_drawable_msgStickerPinned = "drawableMsgStickerPinned";
    public static final String key_drawable_msgStickerReplies = "drawableMsgStickerReplies";
    public static final String key_drawable_msgStickerViews = "drawableMsgStickerViews";
    public static final String key_drawable_muteIconDrawable = "drawableMuteIcon";
    public static final String key_drawable_replyIcon = "drawableReplyIcon";
    public static final String key_drawable_shareIcon = "drawableShareIcon";
    public static final String key_emptyListPlaceholder = "emptyListPlaceholder";
    public static final String key_fastScrollActive = "fastScrollActive";
    public static final String key_fastScrollInactive = "fastScrollInactive";
    public static final String key_fastScrollText = "fastScrollText";
    public static final String key_featuredStickers_addButton = "featuredStickers_addButton";
    public static final String key_featuredStickers_addButtonPressed = "featuredStickers_addButtonPressed";
    public static final String key_featuredStickers_addedIcon = "featuredStickers_addedIcon";
    public static final String key_featuredStickers_buttonProgress = "featuredStickers_buttonProgress";
    public static final String key_featuredStickers_buttonText = "featuredStickers_buttonText";
    public static final String key_featuredStickers_removeButtonText = "featuredStickers_removeButtonText";
    public static final String key_featuredStickers_unread = "featuredStickers_unread";
    public static final String key_files_folderIcon = "files_folderIcon";
    public static final String key_files_folderIconBackground = "files_folderIconBackground";
    public static final String key_files_iconText = "files_iconText";
    public static final String key_graySection = "graySection";
    public static final String key_graySectionText = "key_graySectionText";
    public static final String key_groupcreate_cursor = "groupcreate_cursor";
    public static final String key_groupcreate_hintText = "groupcreate_hintText";
    public static final String key_groupcreate_sectionShadow = "groupcreate_sectionShadow";
    public static final String key_groupcreate_sectionText = "groupcreate_sectionText";
    public static final String key_groupcreate_spanBackground = "groupcreate_spanBackground";
    public static final String key_groupcreate_spanDelete = "groupcreate_spanDelete";
    public static final String key_groupcreate_spanText = "groupcreate_spanText";
    public static final String key_inappPlayerBackground = "inappPlayerBackground";
    public static final String key_inappPlayerClose = "inappPlayerClose";
    public static final String key_inappPlayerPerformer = "inappPlayerPerformer";
    public static final String key_inappPlayerPlayPause = "inappPlayerPlayPause";
    public static final String key_inappPlayerTitle = "inappPlayerTitle";
    public static final String key_listSelector = "listSelectorSDK21";
    public static final String key_location_actionActiveIcon = "location_actionActiveIcon";
    public static final String key_location_actionBackground = "location_actionBackground";
    public static final String key_location_actionIcon = "location_actionIcon";
    public static final String key_location_actionPressedBackground = "location_actionPressedBackground";
    public static final String key_location_liveLocationProgress = "location_liveLocationProgress";
    public static final String key_location_placeLocationBackground = "location_placeLocationBackground";
    public static final String key_location_sendLiveLocationBackground = "location_sendLiveLocationBackground";
    public static final String key_location_sendLiveLocationIcon = "location_sendLiveLocationIcon";
    public static final String key_location_sendLiveLocationText = "location_sendLiveLocationText";
    public static final String key_location_sendLocationBackground = "location_sendLocationBackground";
    public static final String key_location_sendLocationIcon = "location_sendLocationIcon";
    public static final String key_location_sendLocationText = "location_sendLocationText";
    public static final String key_login_progressInner = "login_progressInner";
    public static final String key_login_progressOuter = "login_progressOuter";
    public static final String key_musicPicker_buttonBackground = "musicPicker_buttonBackground";
    public static final String key_musicPicker_buttonIcon = "musicPicker_buttonIcon";
    public static final String key_musicPicker_checkbox = "musicPicker_checkbox";
    public static final String key_musicPicker_checkboxCheck = "musicPicker_checkboxCheck";
    public static final String key_paint_chatActionBackground = "paintChatActionBackground";
    public static final String key_paint_chatActionBackgroundSelected = "paintChatActionBackgroundSelected";
    public static final String key_paint_chatActionText = "paintChatActionText";
    public static final String key_paint_chatBotButton = "paintChatBotButton";
    public static final String key_paint_chatComposeBackground = "paintChatComposeBackground";
    public static final String key_paint_chatTimeBackground = "paintChatTimeBackground";
    public static final String key_passport_authorizeBackground = "passport_authorizeBackground";
    public static final String key_passport_authorizeBackgroundSelected = "passport_authorizeBackgroundSelected";
    public static final String key_passport_authorizeText = "passport_authorizeText";
    public static final String key_picker_badge = "picker_badge";
    public static final String key_picker_badgeText = "picker_badgeText";
    public static final String key_picker_disabledButton = "picker_disabledButton";
    public static final String key_picker_enabledButton = "picker_enabledButton";
    public static final String key_player_actionBar = "player_actionBar";
    public static final String key_player_actionBarItems = "player_actionBarItems";
    public static final String key_player_actionBarSelector = "player_actionBarSelector";
    public static final String key_player_actionBarSubtitle = "player_actionBarSubtitle";
    public static final String key_player_actionBarTitle = "player_actionBarTitle";
    public static final String key_player_actionBarTop = "player_actionBarTop";
    public static final String key_player_background = "player_background";
    public static final String key_player_button = "player_button";
    public static final String key_player_buttonActive = "player_buttonActive";
    public static final String key_player_progress = "player_progress";
    public static final String key_player_progressBackground = "player_progressBackground";
    public static final String key_player_progressBackground2 = "player_progressBackground2";
    public static final String key_player_progressCachedBackground = "key_player_progressCachedBackground";
    public static final String key_player_time = "player_time";
    public static final String key_profile_actionBackground = "profile_actionBackground";
    public static final String key_profile_actionIcon = "profile_actionIcon";
    public static final String key_profile_actionPressedBackground = "profile_actionPressedBackground";
    public static final String key_profile_creatorIcon = "profile_creatorIcon";
    public static final String key_profile_status = "profile_status";
    public static final String key_profile_tabSelectedLine = "profile_tabSelectedLine";
    public static final String key_profile_tabSelectedText = "profile_tabSelectedText";
    public static final String key_profile_tabSelector = "profile_tabSelector";
    public static final String key_profile_tabText = "profile_tabText";
    public static final String key_profile_title = "profile_title";
    public static final String key_profile_verifiedBackground = "profile_verifiedBackground";
    public static final String key_profile_verifiedCheck = "profile_verifiedCheck";
    public static final String key_progressCircle = "progressCircle";
    public static final String key_radioBackground = "radioBackground";
    public static final String key_radioBackgroundChecked = "radioBackgroundChecked";
    public static final String key_returnToCallBackground = "returnToCallBackground";
    public static final String key_returnToCallMutedBackground = "returnToCallMutedBackground";
    public static final String key_returnToCallText = "returnToCallText";
    public static final String key_sessions_devicesImage = "sessions_devicesImage";
    public static final String key_sharedMedia_actionMode = "sharedMedia_actionMode";
    public static final String key_sharedMedia_linkPlaceholder = "sharedMedia_linkPlaceholder";
    public static final String key_sharedMedia_linkPlaceholderText = "sharedMedia_linkPlaceholderText";
    public static final String key_sharedMedia_photoPlaceholder = "sharedMedia_photoPlaceholder";
    public static final String key_sharedMedia_startStopLoadIcon = "sharedMedia_startStopLoadIcon";
    public static final String key_sheet_other = "key_sheet_other";
    public static final String key_sheet_scrollUp = "key_sheet_scrollUp";
    public static final String key_statisticChartActiveLine = "statisticChartActiveLine";
    public static final String key_statisticChartActivePickerChart = "statisticChartActivePickerChart";
    public static final String key_statisticChartBackZoomColor = "statisticChartBackZoomColor";
    public static final String key_statisticChartCheckboxInactive = "statisticChartCheckboxInactive";
    public static final String key_statisticChartChevronColor = "statisticChartChevronColor";
    public static final String key_statisticChartHighlightColor = "statisticChartHighlightColor";
    public static final String key_statisticChartHintLine = "statisticChartHintLine";
    public static final String key_statisticChartInactivePickerChart = "statisticChartInactivePickerChart";
    public static final String key_statisticChartLineEmpty = "statisticChartLineEmpty";
    public static final String key_statisticChartLine_blue = "statisticChartLine_blue";
    public static final String key_statisticChartLine_golden = "statisticChartLine_golden";
    public static final String key_statisticChartLine_green = "statisticChartLine_green";
    public static final String key_statisticChartLine_indigo = "statisticChartLine_indigo";
    public static final String key_statisticChartLine_lightblue = "statisticChartLine_lightblue";
    public static final String key_statisticChartLine_lightgreen = "statisticChartLine_lightgreen";
    public static final String key_statisticChartLine_orange = "statisticChartLine_orange";
    public static final String key_statisticChartLine_red = "statisticChartLine_red";
    public static final String key_statisticChartNightIconColor = "statisticChartNightIconColor";
    public static final String key_statisticChartPopupBackground = "statisticChartPopupBackground";
    public static final String key_statisticChartRipple = "statisticChartRipple";
    public static final String key_statisticChartSignature = "statisticChartSignature";
    public static final String key_statisticChartSignatureAlpha = "statisticChartSignatureAlpha";
    public static final String key_stickers_menu = "stickers_menu";
    public static final String key_stickers_menuSelector = "stickers_menuSelector";
    public static final String key_switch2Track = "switch2Track";
    public static final String key_switch2TrackChecked = "switch2TrackChecked";
    public static final String key_switchTrack = "switchTrack";
    public static final String key_switchTrackBlue = "switchTrackBlue";
    public static final String key_switchTrackBlueChecked = "switchTrackBlueChecked";
    public static final String key_switchTrackBlueSelector = "switchTrackBlueSelector";
    public static final String key_switchTrackBlueSelectorChecked = "switchTrackBlueSelectorChecked";
    public static final String key_switchTrackBlueThumb = "switchTrackBlueThumb";
    public static final String key_switchTrackBlueThumbChecked = "switchTrackBlueThumbChecked";
    public static final String key_switchTrackChecked = "switchTrackChecked";
    public static final String key_undo_background = "undo_background";
    public static final String key_undo_cancelColor = "undo_cancelColor";
    public static final String key_undo_infoColor = "undo_infoColor";
    public static final String key_voipgroup_actionBar = "voipgroup_actionBar";
    public static final String key_voipgroup_actionBarItems = "voipgroup_actionBarItems";
    public static final String key_voipgroup_actionBarItemsSelector = "voipgroup_actionBarItemsSelector";
    public static final String key_voipgroup_actionBarSubtitle = "voipgroup_actionBarSubtitle";
    public static final String key_voipgroup_actionBarUnscrolled = "voipgroup_actionBarUnscrolled";
    public static final String key_voipgroup_blueText = "voipgroup_blueText";
    public static final String key_voipgroup_checkMenu = "voipgroup_checkMenu";
    public static final String key_voipgroup_connectingProgress = "voipgroup_connectingProgress";
    public static final String key_voipgroup_dialogBackground = "voipgroup_dialogBackground";
    public static final String key_voipgroup_disabledButton = "voipgroup_disabledButton";
    public static final String key_voipgroup_disabledButtonActive = "voipgroup_disabledButtonActive";
    public static final String key_voipgroup_disabledButtonActiveScrolled = "voipgroup_disabledButtonActiveScrolled";
    public static final String key_voipgroup_emptyView = "voipgroup_emptyView";
    public static final String key_voipgroup_inviteMembersBackground = "voipgroup_inviteMembersBackground";
    public static final String key_voipgroup_lastSeenText = "voipgroup_lastSeenText";
    public static final String key_voipgroup_lastSeenTextUnscrolled = "voipgroup_lastSeenTextUnscrolled";
    public static final String key_voipgroup_leaveButton = "voipgroup_leaveButton";
    public static final String key_voipgroup_leaveButtonScrolled = "voipgroup_leaveButtonScrolled";
    public static final String key_voipgroup_leaveCallMenu = "voipgroup_leaveCallMenu";
    public static final String key_voipgroup_listSelector = "voipgroup_listSelector";
    public static final String key_voipgroup_listViewBackground = "voipgroup_listViewBackground";
    public static final String key_voipgroup_listViewBackgroundUnscrolled = "voipgroup_listViewBackgroundUnscrolled";
    public static final String key_voipgroup_listeningText = "voipgroup_listeningText";
    public static final String key_voipgroup_muteButton = "voipgroup_muteButton";
    public static final String key_voipgroup_muteButton2 = "voipgroup_muteButton2";
    public static final String key_voipgroup_muteButton3 = "voipgroup_muteButton3";
    public static final String key_voipgroup_mutedByAdminGradient = "voipgroup_mutedByAdminGradient";
    public static final String key_voipgroup_mutedByAdminGradient2 = "voipgroup_mutedByAdminGradient2";
    public static final String key_voipgroup_mutedByAdminGradient3 = "voipgroup_mutedByAdminGradient3";
    public static final String key_voipgroup_mutedByAdminIcon = "voipgroup_mutedByAdminIcon";
    public static final String key_voipgroup_mutedByAdminMuteButton = "voipgroup_mutedByAdminMuteButton";
    public static final String key_voipgroup_mutedByAdminMuteButtonDisabled = "voipgroup_mutedByAdminMuteButtonDisabled";
    public static final String key_voipgroup_mutedIcon = "voipgroup_mutedIcon";
    public static final String key_voipgroup_mutedIconUnscrolled = "voipgroup_mutedIconUnscrolled";
    public static final String key_voipgroup_nameText = "voipgroup_nameText";
    public static final String key_voipgroup_overlayAlertGradientMuted = "voipgroup_overlayAlertGradientMuted";
    public static final String key_voipgroup_overlayAlertGradientMuted2 = "voipgroup_overlayAlertGradientMuted2";
    public static final String key_voipgroup_overlayAlertGradientUnmuted = "voipgroup_overlayAlertGradientUnmuted";
    public static final String key_voipgroup_overlayAlertGradientUnmuted2 = "voipgroup_overlayAlertGradientUnmuted2";
    public static final String key_voipgroup_overlayAlertMutedByAdmin = "voipgroup_overlayAlertMutedByAdmin";
    public static final String key_voipgroup_overlayAlertMutedByAdmin2 = "kvoipgroup_overlayAlertMutedByAdmin2";
    public static final String key_voipgroup_overlayBlue1 = "voipgroup_overlayBlue1";
    public static final String key_voipgroup_overlayBlue2 = "voipgroup_overlayBlue2";
    public static final String key_voipgroup_overlayGreen1 = "voipgroup_overlayGreen1";
    public static final String key_voipgroup_overlayGreen2 = "voipgroup_overlayGreen2";
    public static final String key_voipgroup_scrollUp = "voipgroup_scrollUp";
    public static final String key_voipgroup_searchBackground = "voipgroup_searchBackground";
    public static final String key_voipgroup_searchPlaceholder = "voipgroup_searchPlaceholder";
    public static final String key_voipgroup_searchText = "voipgroup_searchText";
    public static final String key_voipgroup_soundButton = "voipgroup_soundButton";
    public static final String key_voipgroup_soundButton2 = "voipgroup_soundButton2";
    public static final String key_voipgroup_soundButtonActive = "voipgroup_soundButtonActive";
    public static final String key_voipgroup_soundButtonActive2 = "voipgroup_soundButtonActive2";
    public static final String key_voipgroup_soundButtonActive2Scrolled = "voipgroup_soundButtonActive2Scrolled";
    public static final String key_voipgroup_soundButtonActiveScrolled = "voipgroup_soundButtonActiveScrolled";
    public static final String key_voipgroup_speakingText = "voipgroup_speakingText";
    public static final String key_voipgroup_topPanelBlue1 = "voipgroup_topPanelBlue1";
    public static final String key_voipgroup_topPanelBlue2 = "voipgroup_topPanelBlue2";
    public static final String key_voipgroup_topPanelGray = "voipgroup_topPanelGray";
    public static final String key_voipgroup_topPanelGreen1 = "voipgroup_topPanelGreen1";
    public static final String key_voipgroup_topPanelGreen2 = "voipgroup_topPanelGreen2";
    public static final String key_voipgroup_unmuteButton = "voipgroup_unmuteButton";
    public static final String key_voipgroup_unmuteButton2 = "voipgroup_unmuteButton2";
    public static final String key_voipgroup_windowBackgroundWhiteInputField = "voipgroup_windowBackgroundWhiteInputField";
    public static final String key_voipgroup_windowBackgroundWhiteInputFieldActivated = "voipgroup_windowBackgroundWhiteInputFieldActivated";
    public static final String key_wallet_addressConfirmBackground = "wallet_addressConfirmBackground";
    public static final String key_wallet_blackBackground = "wallet_blackBackground";
    public static final String key_wallet_blackBackgroundSelector = "wallet_blackBackgroundSelector";
    public static final String key_wallet_blackText = "wallet_blackText";
    public static final String key_wallet_buttonBackground = "wallet_buttonBackground";
    public static final String key_wallet_buttonPressedBackground = "wallet_buttonPressedBackground";
    public static final String key_wallet_buttonText = "wallet_buttonText";
    public static final String key_wallet_commentText = "wallet_commentText";
    public static final String key_wallet_dateText = "wallet_dateText";
    public static final String key_wallet_grayBackground = "wallet_grayBackground";
    public static final String key_wallet_graySettingsBackground = "wallet_graySettingsBackground";
    public static final String key_wallet_grayText = "wallet_grayText";
    public static final String key_wallet_grayText2 = "wallet_grayText2";
    public static final String key_wallet_greenText = "wallet_greenText";
    public static final String key_wallet_pullBackground = "wallet_pullBackground";
    public static final String key_wallet_redText = "wallet_redText";
    public static final String key_wallet_releaseBackground = "wallet_releaseBackground";
    public static final String key_wallet_statusText = "wallet_statusText";
    public static final String key_wallet_whiteBackground = "wallet_whiteBackground";
    public static final String key_wallet_whiteText = "wallet_whiteText";
    public static final String key_windowBackgroundCheckText = "windowBackgroundCheckText";
    public static final String key_windowBackgroundChecked = "windowBackgroundChecked";
    public static final String key_windowBackgroundGray = "windowBackgroundGray";
    public static final String key_windowBackgroundGrayShadow = "windowBackgroundGrayShadow";
    public static final String key_windowBackgroundUnchecked = "windowBackgroundUnchecked";
    public static final String key_windowBackgroundWhite = "windowBackgroundWhite";
    public static final String key_windowBackgroundWhiteBlackText = "windowBackgroundWhiteBlackText";
    public static final String key_windowBackgroundWhiteBlueButton = "windowBackgroundWhiteBlueButton";
    public static final String key_windowBackgroundWhiteBlueHeader = "windowBackgroundWhiteBlueHeader";
    public static final String key_windowBackgroundWhiteBlueIcon = "windowBackgroundWhiteBlueIcon";
    public static final String key_windowBackgroundWhiteBlueText = "windowBackgroundWhiteBlueText";
    public static final String key_windowBackgroundWhiteBlueText2 = "windowBackgroundWhiteBlueText2";
    public static final String key_windowBackgroundWhiteBlueText3 = "windowBackgroundWhiteBlueText3";
    public static final String key_windowBackgroundWhiteBlueText4 = "windowBackgroundWhiteBlueText4";
    public static final String key_windowBackgroundWhiteBlueText5 = "windowBackgroundWhiteBlueText5";
    public static final String key_windowBackgroundWhiteBlueText6 = "windowBackgroundWhiteBlueText6";
    public static final String key_windowBackgroundWhiteBlueText7 = "windowBackgroundWhiteBlueText7";
    public static final String key_windowBackgroundWhiteGrayIcon = "windowBackgroundWhiteGrayIcon";
    public static final String key_windowBackgroundWhiteGrayLine = "windowBackgroundWhiteGrayLine";
    public static final String key_windowBackgroundWhiteGrayText = "windowBackgroundWhiteGrayText";
    public static final String key_windowBackgroundWhiteGrayText2 = "windowBackgroundWhiteGrayText2";
    public static final String key_windowBackgroundWhiteGrayText3 = "windowBackgroundWhiteGrayText3";
    public static final String key_windowBackgroundWhiteGrayText4 = "windowBackgroundWhiteGrayText4";
    public static final String key_windowBackgroundWhiteGrayText5 = "windowBackgroundWhiteGrayText5";
    public static final String key_windowBackgroundWhiteGrayText6 = "windowBackgroundWhiteGrayText6";
    public static final String key_windowBackgroundWhiteGrayText7 = "windowBackgroundWhiteGrayText7";
    public static final String key_windowBackgroundWhiteGrayText8 = "windowBackgroundWhiteGrayText8";
    public static final String key_windowBackgroundWhiteGreenText = "windowBackgroundWhiteGreenText";
    public static final String key_windowBackgroundWhiteGreenText2 = "windowBackgroundWhiteGreenText2";
    public static final String key_windowBackgroundWhiteHintText = "windowBackgroundWhiteHintText";
    public static final String key_windowBackgroundWhiteInputField = "windowBackgroundWhiteInputField";
    public static final String key_windowBackgroundWhiteInputFieldActivated = "windowBackgroundWhiteInputFieldActivated";
    public static final String key_windowBackgroundWhiteLinkSelection = "windowBackgroundWhiteLinkSelection";
    public static final String key_windowBackgroundWhiteLinkText = "windowBackgroundWhiteLinkText";
    public static final String key_windowBackgroundWhiteRedText = "windowBackgroundWhiteRedText";
    public static final String key_windowBackgroundWhiteRedText2 = "windowBackgroundWhiteRedText2";
    public static final String key_windowBackgroundWhiteRedText3 = "windowBackgroundWhiteRedText3";
    public static final String key_windowBackgroundWhiteRedText4 = "windowBackgroundWhiteRedText4";
    public static final String key_windowBackgroundWhiteRedText5 = "windowBackgroundWhiteRedText5";
    public static final String key_windowBackgroundWhiteRedText6 = "windowBackgroundWhiteRedText6";
    public static final String key_windowBackgroundWhiteValueText = "windowBackgroundWhiteValueText";
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
    public static Drawable listSelector;
    private static int loadingCurrentTheme;
    private static boolean[] loadingRemoteThemes = new boolean[3];
    /* access modifiers changed from: private */
    public static Paint maskPaint = new Paint(1);
    public static Drawable moveUpDrawable;
    /* access modifiers changed from: private */
    public static HashSet<String> myMessagesBubblesColorKeys = new HashSet<>();
    /* access modifiers changed from: private */
    public static HashSet<String> myMessagesColorKeys = new HashSet<>();
    private static HashSet<String> myMessagesGradientColorsNearKeys = new HashSet<>();
    private static ArrayList<ThemeInfo> otherThemes = new ArrayList<>();
    private static int patternIntensity;
    public static PathAnimator playPauseAnimator;
    private static int previousPhase;
    /* access modifiers changed from: private */
    public static ThemeInfo previousTheme;
    public static TextPaint profile_aboutTextPaint;
    public static Drawable profile_verifiedCheckDrawable;
    public static Drawable profile_verifiedDrawable;
    private static long[] remoteThemesHash = new long[3];
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
    private static int[] viewPos = new int[2];
    private static Drawable wallpaper;
    private static Runnable wallpaperLoadTask;

    public static class BackgroundDrawableSettings {
        public Boolean isCustomTheme;
        public Boolean isPatternWallpaper;
        public Boolean isWallpaperMotion;
        public Drawable themedWallpaper;
        public Drawable wallpaper;
    }

    public static class MessageDrawable extends Drawable {
        public static final int TYPE_MEDIA = 1;
        public static final int TYPE_PREVIEW = 2;
        public static final int TYPE_TEXT = 0;
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
        private boolean isSelected;
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

        public MessageDrawable(int type, boolean out, boolean selected) {
            this(type, out, selected, (ResourcesProvider) null);
        }

        public MessageDrawable(int type, boolean out, boolean selected, ResourcesProvider resourcesProvider2) {
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
            this.isOut = out;
            this.currentType = type;
            this.isSelected = selected;
            this.path = new Path();
            this.selectedPaint = new Paint(1);
            this.alpha = 255;
        }

        public boolean hasGradient() {
            return this.gradientShader != null && Theme.shouldDrawGradientIcons;
        }

        public void applyMatrixScale() {
            int num;
            Bitmap bitmap;
            if (this.gradientShader instanceof BitmapShader) {
                int num2 = 1;
                if (!this.isCrossfadeBackground || (bitmap = this.crosfadeFromBitmap) == null) {
                    if (this.themePreview) {
                        num = 2;
                    } else {
                        if (this.currentType != 2) {
                            num2 = 0;
                        }
                        num = num2;
                    }
                    Bitmap bitmap2 = motionBackground[num].getBitmap();
                    float scale = 1.0f / Math.min(((float) bitmap2.getWidth()) / ((float) motionBackground[num].getBounds().width()), ((float) bitmap2.getHeight()) / ((float) motionBackground[num].getBounds().height()));
                    this.matrix.postScale(scale, scale);
                    return;
                }
                if (this.currentType != 2) {
                    num2 = 0;
                }
                float scale2 = 1.0f / Math.min(((float) bitmap.getWidth()) / ((float) motionBackground[num2].getBounds().width()), ((float) this.crosfadeFromBitmap.getHeight()) / ((float) motionBackground[num2].getBounds().height()));
                this.matrix.postScale(scale2, scale2);
            }
        }

        public Shader getGradientShader() {
            return this.gradientShader;
        }

        public Matrix getMatrix() {
            return this.matrix;
        }

        /* access modifiers changed from: protected */
        public int getColor(String key) {
            if (this.currentType == 2) {
                return Theme.getColor(key);
            }
            ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }

        /* access modifiers changed from: protected */
        public Integer getCurrentColor(String key) {
            if (this.currentType == 2) {
                return Integer.valueOf(Theme.getColor(key));
            }
            ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            return resourcesProvider2 != null ? resourcesProvider2.getCurrentColor(key) : (Integer) Theme.currentColors.get(key);
        }

        public void setTop(int top, int backgroundWidth, int backgroundHeight, boolean topNear, boolean bottomNear) {
            setTop(top, backgroundWidth, backgroundHeight, backgroundHeight, topNear, bottomNear);
        }

        public void setTop(int top, int backgroundWidth, int backgroundHeight, int heightOffset, boolean topNear, boolean bottomNear) {
            boolean animatedGradient;
            Integer gradientColor3;
            Integer gradientColor2;
            Integer gradientColor1;
            int color;
            int num;
            int i;
            String str;
            int i2 = backgroundHeight;
            MessageDrawable messageDrawable = this.crossfadeFromDrawable;
            if (messageDrawable != null) {
                messageDrawable.setTop(top, backgroundWidth, backgroundHeight, heightOffset, topNear, bottomNear);
            }
            int i3 = 0;
            if (this.isOut) {
                if (this.isSelected) {
                    str = "chat_outBubbleSelected";
                } else {
                    str = "chat_outBubble";
                }
                color = getColor(str);
                gradientColor1 = getCurrentColor("chat_outBubbleGradient");
                gradientColor2 = getCurrentColor("chat_outBubbleGradient2");
                gradientColor3 = getCurrentColor("chat_outBubbleGradient3");
                Integer val = getCurrentColor("chat_outBubbleGradientAnimated");
                animatedGradient = (val == null || val.intValue() == 0) ? false : true;
            } else {
                color = getColor(this.isSelected != 0 ? "chat_inBubbleSelected" : "chat_inBubble");
                gradientColor1 = null;
                gradientColor2 = null;
                gradientColor3 = null;
                animatedGradient = false;
            }
            if (gradientColor1 != null) {
                color = getColor("chat_outBubble");
            }
            if (gradientColor1 == null) {
                gradientColor1 = 0;
            }
            if (gradientColor2 == null) {
                gradientColor2 = 0;
            }
            if (gradientColor3 == null) {
                gradientColor3 = 0;
            }
            if (this.themePreview) {
                num = 2;
            } else {
                num = this.currentType == 2 ? (char) 1 : 0;
            }
            if (!this.isCrossfadeBackground && gradientColor2.intValue() != 0 && animatedGradient) {
                MotionBackgroundDrawable[] motionBackgroundDrawableArr = motionBackground;
                if (motionBackgroundDrawableArr[num] != null) {
                    int[] colors = motionBackgroundDrawableArr[num].getColors();
                    this.currentColor = colors[0];
                    this.currentGradientColor1 = colors[1];
                    this.currentGradientColor2 = colors[2];
                    this.currentGradientColor3 = colors[3];
                }
            }
            if (this.isCrossfadeBackground && gradientColor2.intValue() != 0 && animatedGradient) {
                if (i2 == this.currentBackgroundHeight && this.crosfadeFromBitmapShader != null && this.currentColor == color && this.currentGradientColor1 == gradientColor1.intValue() && this.currentGradientColor2 == gradientColor2.intValue() && this.currentGradientColor3 == gradientColor3.intValue() && this.currentAnimateGradient == animatedGradient) {
                    i = -1;
                } else {
                    if (this.crosfadeFromBitmap == null) {
                        this.crosfadeFromBitmap = Bitmap.createBitmap(60, 80, Bitmap.Config.ARGB_8888);
                        this.crosfadeFromBitmapShader = new BitmapShader(this.crosfadeFromBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    }
                    MotionBackgroundDrawable[] motionBackgroundDrawableArr2 = motionBackground;
                    if (motionBackgroundDrawableArr2[num] == null) {
                        motionBackgroundDrawableArr2[num] = new MotionBackgroundDrawable();
                        if (this.currentType != 2) {
                            motionBackground[num].setPostInvalidateParent(true);
                        }
                        motionBackground[num].setRoundRadius(AndroidUtilities.dp(1.0f));
                    }
                    i = -1;
                    motionBackground[num].setColors(color, gradientColor1.intValue(), gradientColor2.intValue(), gradientColor3.intValue(), this.crosfadeFromBitmap);
                    this.crosfadeFromBitmapShader.setLocalMatrix(this.matrix);
                }
                Shader shader = this.crosfadeFromBitmapShader;
                this.gradientShader = shader;
                this.paint.setShader(shader);
                this.paint.setColor(i);
                this.currentColor = color;
                this.currentAnimateGradient = animatedGradient;
                this.currentGradientColor1 = gradientColor1.intValue();
                this.currentGradientColor2 = gradientColor2.intValue();
                this.currentGradientColor3 = gradientColor3.intValue();
            } else if (gradientColor1.intValue() != 0 && (this.gradientShader == null || i2 != this.currentBackgroundHeight || this.currentColor != color || this.currentGradientColor1 != gradientColor1.intValue() || this.currentGradientColor2 != gradientColor2.intValue() || this.currentGradientColor3 != gradientColor3.intValue() || this.currentAnimateGradient != animatedGradient)) {
                if (gradientColor2.intValue() != 0 && animatedGradient) {
                    MotionBackgroundDrawable[] motionBackgroundDrawableArr3 = motionBackground;
                    if (motionBackgroundDrawableArr3[num] == null) {
                        motionBackgroundDrawableArr3[num] = new MotionBackgroundDrawable();
                        if (this.currentType != 2) {
                            motionBackground[num].setPostInvalidateParent(true);
                        }
                        motionBackground[num].setRoundRadius(AndroidUtilities.dp(1.0f));
                    }
                    motionBackground[num].setColors(color, gradientColor1.intValue(), gradientColor2.intValue(), gradientColor3.intValue());
                    this.gradientShader = motionBackground[num].getBitmapShader();
                } else if (gradientColor2.intValue() == 0) {
                    this.gradientShader = new LinearGradient(0.0f, 0.0f, 0.0f, (float) i2, new int[]{gradientColor1.intValue(), color}, (float[]) null, Shader.TileMode.CLAMP);
                } else if (gradientColor3.intValue() != 0) {
                    this.gradientShader = new LinearGradient(0.0f, 0.0f, 0.0f, (float) i2, new int[]{gradientColor3.intValue(), gradientColor2.intValue(), gradientColor1.intValue(), color}, (float[]) null, Shader.TileMode.CLAMP);
                } else {
                    this.gradientShader = new LinearGradient(0.0f, 0.0f, 0.0f, (float) i2, new int[]{gradientColor2.intValue(), gradientColor1.intValue(), color}, (float[]) null, Shader.TileMode.CLAMP);
                }
                this.paint.setShader(this.gradientShader);
                this.currentColor = color;
                this.currentAnimateGradient = animatedGradient;
                this.currentGradientColor1 = gradientColor1.intValue();
                this.currentGradientColor2 = gradientColor2.intValue();
                this.currentGradientColor3 = gradientColor3.intValue();
                this.paint.setColor(-1);
            } else if (gradientColor1.intValue() == 0) {
                if (this.gradientShader != null) {
                    this.gradientShader = null;
                    this.paint.setShader((Shader) null);
                }
                this.paint.setColor(color);
            }
            Shader shader2 = this.gradientShader;
            if (shader2 instanceof BitmapShader) {
                motionBackground[num].setBounds(0, 0, backgroundWidth, i2 - (shader2 instanceof BitmapShader ? heightOffset : 0));
            } else {
                int i4 = backgroundWidth;
            }
            this.currentBackgroundHeight = i2;
            if (this.gradientShader instanceof BitmapShader) {
                i3 = heightOffset;
            }
            this.topY = top - i3;
            this.isTopNear = topNear;
            this.isBottomNear = bottomNear;
        }

        public int getTopY() {
            return this.topY;
        }

        private int dp(float value) {
            if (this.currentType == 2) {
                return (int) Math.ceil((double) (3.0f * value));
            }
            return AndroidUtilities.dp(value);
        }

        public Paint getPaint() {
            return this.paint;
        }

        public Drawable[] getShadowDrawables() {
            return this.shadowDrawable;
        }

        public Drawable getBackgroundDrawable() {
            int idx;
            int color;
            int newRad = AndroidUtilities.dp((float) SharedConfig.bubbleRadius);
            int idx2 = this.isTopNear;
            if (idx2 != 0 && this.isBottomNear) {
                idx = 3;
            } else if (idx2 != 0) {
                idx = 2;
            } else if (this.isBottomNear != 0) {
                idx = 1;
            } else {
                idx = 0;
            }
            int i = this.isSelected;
            int idx22 = i;
            boolean forceSetColor = false;
            boolean drawWithShadow = this.gradientShader == null && i == false && !this.isCrossfadeBackground;
            int shadowColor = getColor(this.isOut ? "chat_outBubbleShadow" : "chat_inBubbleShadow");
            if (!(this.lastDrawWithShadow == drawWithShadow && this.currentBackgroundDrawableRadius[idx22][idx] == newRad && (!drawWithShadow || this.shadowDrawableColor[idx] == shadowColor))) {
                this.currentBackgroundDrawableRadius[idx22][idx] = newRad;
                try {
                    Bitmap bitmap = Bitmap.createBitmap(dp(50.0f), dp(40.0f), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    this.backupRect.set(getBounds());
                    if (drawWithShadow) {
                        this.shadowDrawableColor[idx] = shadowColor;
                        Paint shadowPaint = new Paint(1);
                        shadowPaint.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, (float) dp(40.0f), new int[]{NUM, NUM}, (float[]) null, Shader.TileMode.CLAMP));
                        shadowPaint.setColorFilter(new PorterDuffColorFilter(shadowColor, PorterDuff.Mode.MULTIPLY));
                        shadowPaint.setShadowLayer(2.0f, 0.0f, 1.0f, -1);
                        if (AndroidUtilities.density > 1.0f) {
                            setBounds(-1, -1, bitmap.getWidth() + 1, bitmap.getHeight() + 1);
                        } else {
                            setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                        }
                        draw(canvas, shadowPaint);
                        if (AndroidUtilities.density > 1.0f) {
                            shadowPaint.setColor(0);
                            shadowPaint.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
                            shadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                            setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                            draw(canvas, shadowPaint);
                        }
                    }
                    Paint shadowPaint2 = new Paint(1);
                    shadowPaint2.setColor(-1);
                    setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                    draw(canvas, shadowPaint2);
                    this.backgroundDrawable[idx22][idx] = new NinePatchDrawable(bitmap, getByteBuffer((bitmap.getWidth() / 2) - 1, (bitmap.getWidth() / 2) + 1, (bitmap.getHeight() / 2) - 1, (bitmap.getHeight() / 2) + 1).array(), new Rect(), (String) null);
                    forceSetColor = true;
                    setBounds(this.backupRect);
                } catch (Throwable th) {
                }
            }
            this.lastDrawWithShadow = drawWithShadow;
            if (this.isSelected) {
                color = getColor(this.isOut ? "chat_outBubbleSelected" : "chat_inBubbleSelected");
            } else {
                color = getColor(this.isOut != 0 ? "chat_outBubble" : "chat_inBubble");
            }
            Drawable[][] drawableArr = this.backgroundDrawable;
            if (drawableArr[idx22][idx] != null && (this.backgroundDrawableColor[idx22][idx] != color || forceSetColor)) {
                drawableArr[idx22][idx].setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                this.backgroundDrawableColor[idx22][idx] = color;
            }
            return this.backgroundDrawable[idx22][idx];
        }

        public Drawable getTransitionDrawable(int color) {
            if (this.transitionDrawable == null) {
                Bitmap bitmap = Bitmap.createBitmap(dp(50.0f), dp(40.0f), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                this.backupRect.set(getBounds());
                Paint shadowPaint = new Paint(1);
                shadowPaint.setColor(-1);
                setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                draw(canvas, shadowPaint);
                this.transitionDrawable = new NinePatchDrawable(bitmap, getByteBuffer((bitmap.getWidth() / 2) - 1, (bitmap.getWidth() / 2) + 1, (bitmap.getHeight() / 2) - 1, (bitmap.getHeight() / 2) + 1).array(), new Rect(), (String) null);
                setBounds(this.backupRect);
            }
            if (this.transitionDrawableColor != color) {
                this.transitionDrawableColor = color;
                this.transitionDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
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
            int idx;
            if (this.isCrossfadeBackground) {
                return null;
            }
            if (this.gradientShader == null && !this.isSelected && this.crossfadeFromDrawable == null) {
                return null;
            }
            int newRad = AndroidUtilities.dp((float) SharedConfig.bubbleRadius);
            int idx2 = this.isTopNear;
            if (idx2 != 0 && this.isBottomNear) {
                idx = 3;
            } else if (idx2 != 0) {
                idx = 2;
            } else if (this.isBottomNear != 0) {
                idx = 1;
            } else {
                idx = 0;
            }
            boolean forceSetColor = false;
            int[] iArr = this.currentShadowDrawableRadius;
            if (iArr[idx] != newRad) {
                iArr[idx] = newRad;
                try {
                    Bitmap bitmap = Bitmap.createBitmap(dp(50.0f), dp(40.0f), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    Paint shadowPaint = new Paint(1);
                    shadowPaint.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, (float) dp(40.0f), new int[]{NUM, NUM}, (float[]) null, Shader.TileMode.CLAMP));
                    shadowPaint.setShadowLayer(2.0f, 0.0f, 1.0f, -1);
                    if (AndroidUtilities.density > 1.0f) {
                        setBounds(-1, -1, bitmap.getWidth() + 1, bitmap.getHeight() + 1);
                    } else {
                        setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                    }
                    draw(canvas, shadowPaint);
                    if (AndroidUtilities.density > 1.0f) {
                        shadowPaint.setColor(0);
                        shadowPaint.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
                        shadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                        setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                        draw(canvas, shadowPaint);
                    }
                    this.shadowDrawable[idx] = new NinePatchDrawable(bitmap, getByteBuffer((bitmap.getWidth() / 2) - 1, (bitmap.getWidth() / 2) + 1, (bitmap.getHeight() / 2) - 1, (bitmap.getHeight() / 2) + 1).array(), new Rect(), (String) null);
                    forceSetColor = true;
                } catch (Throwable th) {
                }
            }
            int color = getColor(this.isOut ? "chat_outBubbleShadow" : "chat_inBubbleShadow");
            Drawable[] drawableArr = this.shadowDrawable;
            if (drawableArr[idx] != null && (this.shadowDrawableColor[idx] != color || forceSetColor)) {
                drawableArr[idx].setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                this.shadowDrawableColor[idx] = color;
            }
            return this.shadowDrawable[idx];
        }

        private static ByteBuffer getByteBuffer(int x1, int x2, int y1, int y2) {
            ByteBuffer buffer = ByteBuffer.allocate(84).order(ByteOrder.nativeOrder());
            buffer.put((byte) 1);
            buffer.put((byte) 2);
            buffer.put((byte) 2);
            buffer.put((byte) 9);
            buffer.putInt(0);
            buffer.putInt(0);
            buffer.putInt(0);
            buffer.putInt(0);
            buffer.putInt(0);
            buffer.putInt(0);
            buffer.putInt(0);
            buffer.putInt(x1);
            buffer.putInt(x2);
            buffer.putInt(y1);
            buffer.putInt(y2);
            buffer.putInt(1);
            buffer.putInt(1);
            buffer.putInt(1);
            buffer.putInt(1);
            buffer.putInt(1);
            buffer.putInt(1);
            buffer.putInt(1);
            buffer.putInt(1);
            buffer.putInt(1);
            return buffer;
        }

        public void drawCached(Canvas canvas, PathDrawParams patchDrawCacheParams) {
            this.pathDrawCacheParams = patchDrawCacheParams;
            MessageDrawable messageDrawable = this.crossfadeFromDrawable;
            if (messageDrawable != null) {
                messageDrawable.pathDrawCacheParams = patchDrawCacheParams;
            }
            draw(canvas);
            this.pathDrawCacheParams = null;
            MessageDrawable messageDrawable2 = this.crossfadeFromDrawable;
            if (messageDrawable2 != null) {
                messageDrawable2.pathDrawCacheParams = null;
            }
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

        public void draw(Canvas canvas, Paint paintToUse) {
            int nearRad;
            int rad;
            boolean drawFullTop;
            boolean drawFullBottom;
            Path path2;
            boolean invalidatePath;
            int nearRad2;
            Drawable background;
            Canvas canvas2 = canvas;
            Rect bounds = getBounds();
            if (paintToUse == null && this.gradientShader == null && (background = getBackgroundDrawable()) != null) {
                background.setBounds(bounds);
                background.draw(canvas2);
                return;
            }
            int padding = dp(2.0f);
            if (this.overrideRoundRadius != 0) {
                rad = this.overrideRoundRadius;
                nearRad = this.overrideRoundRadius;
            } else if (this.currentType == 2) {
                rad = dp(6.0f);
                nearRad = dp(6.0f);
            } else {
                rad = dp((float) SharedConfig.bubbleRadius);
                nearRad = dp((float) Math.min(5, SharedConfig.bubbleRadius));
            }
            int smallRad = dp(6.0f);
            Paint p = paintToUse == null ? this.paint : paintToUse;
            if (paintToUse == null && this.gradientShader != null) {
                this.matrix.reset();
                applyMatrixScale();
                this.matrix.postTranslate(0.0f, (float) (-this.topY));
                this.gradientShader.setLocalMatrix(this.matrix);
            }
            int top = Math.max(bounds.top, 0);
            if (this.pathDrawCacheParams == null || bounds.height() >= this.currentBackgroundHeight) {
                drawFullBottom = this.currentType != 1 ? (this.topY + bounds.bottom) - rad < this.currentBackgroundHeight : (this.topY + bounds.bottom) - (smallRad * 2) < this.currentBackgroundHeight;
                drawFullTop = this.topY + (rad * 2) >= 0;
            } else {
                drawFullBottom = true;
                drawFullTop = true;
            }
            PathDrawParams pathDrawParams = this.pathDrawCacheParams;
            if (pathDrawParams != null) {
                path2 = pathDrawParams.path;
                invalidatePath = this.pathDrawCacheParams.invalidatePath(bounds, drawFullBottom, drawFullTop);
            } else {
                path2 = this.path;
                invalidatePath = true;
            }
            if (invalidatePath) {
                path2.reset();
                if (this.isOut) {
                    if (this.drawFullBubble || this.currentType == 2 || paintToUse != null || drawFullBottom) {
                        if (this.currentType == 1) {
                            path2.moveTo((float) ((bounds.right - dp(8.0f)) - rad), (float) (bounds.bottom - padding));
                        } else {
                            path2.moveTo((float) (bounds.right - dp(2.6f)), (float) (bounds.bottom - padding));
                        }
                        path2.lineTo((float) (bounds.left + padding + rad), (float) (bounds.bottom - padding));
                        boolean z = invalidatePath;
                        nearRad2 = nearRad;
                        this.rect.set((float) (bounds.left + padding), (float) ((bounds.bottom - padding) - (rad * 2)), (float) (bounds.left + padding + (rad * 2)), (float) (bounds.bottom - padding));
                        path2.arcTo(this.rect, 90.0f, 90.0f, false);
                    } else {
                        path2.moveTo((float) (bounds.right - dp(8.0f)), (float) ((top - this.topY) + this.currentBackgroundHeight));
                        path2.lineTo((float) (bounds.left + padding), (float) ((top - this.topY) + this.currentBackgroundHeight));
                        boolean z2 = invalidatePath;
                        nearRad2 = nearRad;
                    }
                    if (this.drawFullBubble || this.currentType == 2 || paintToUse != null || drawFullTop) {
                        path2.lineTo((float) (bounds.left + padding), (float) (bounds.top + padding + rad));
                        this.rect.set((float) (bounds.left + padding), (float) (bounds.top + padding), (float) (bounds.left + padding + (rad * 2)), (float) (bounds.top + padding + (rad * 2)));
                        path2.arcTo(this.rect, 180.0f, 90.0f, false);
                        int radToUse = this.isTopNear ? nearRad2 : rad;
                        if (this.currentType == 1) {
                            path2.lineTo((float) ((bounds.right - padding) - radToUse), (float) (bounds.top + padding));
                            this.rect.set((float) ((bounds.right - padding) - (radToUse * 2)), (float) (bounds.top + padding), (float) (bounds.right - padding), (float) (bounds.top + padding + (radToUse * 2)));
                        } else {
                            path2.lineTo((float) ((bounds.right - dp(8.0f)) - radToUse), (float) (bounds.top + padding));
                            this.rect.set((float) ((bounds.right - dp(8.0f)) - (radToUse * 2)), (float) (bounds.top + padding), (float) (bounds.right - dp(8.0f)), (float) (bounds.top + padding + (radToUse * 2)));
                        }
                        path2.arcTo(this.rect, 270.0f, 90.0f, false);
                    } else {
                        path2.lineTo((float) (bounds.left + padding), (float) ((top - this.topY) - dp(2.0f)));
                        if (this.currentType == 1) {
                            path2.lineTo((float) (bounds.right - padding), (float) ((top - this.topY) - dp(2.0f)));
                        } else {
                            path2.lineTo((float) (bounds.right - dp(8.0f)), (float) ((top - this.topY) - dp(2.0f)));
                        }
                    }
                    int i = this.currentType;
                    if (i == 1) {
                        if (paintToUse != null || drawFullBottom) {
                            int radToUse2 = this.isBottomNear ? nearRad2 : rad;
                            path2.lineTo((float) (bounds.right - padding), (float) ((bounds.bottom - padding) - radToUse2));
                            this.rect.set((float) ((bounds.right - padding) - (radToUse2 * 2)), (float) ((bounds.bottom - padding) - (radToUse2 * 2)), (float) (bounds.right - padding), (float) (bounds.bottom - padding));
                            path2.arcTo(this.rect, 0.0f, 90.0f, false);
                        } else {
                            path2.lineTo((float) (bounds.right - padding), (float) ((top - this.topY) + this.currentBackgroundHeight));
                        }
                    } else if (this.drawFullBubble || i == 2 || paintToUse != null || drawFullBottom) {
                        path2.lineTo((float) (bounds.right - dp(8.0f)), (float) (((bounds.bottom - padding) - smallRad) - dp(3.0f)));
                        this.rect.set((float) (bounds.right - dp(8.0f)), (float) (((bounds.bottom - padding) - (smallRad * 2)) - dp(9.0f)), (float) ((bounds.right - dp(7.0f)) + (smallRad * 2)), (float) ((bounds.bottom - padding) - dp(1.0f)));
                        path2.arcTo(this.rect, 180.0f, -83.0f, false);
                    } else {
                        path2.lineTo((float) (bounds.right - dp(8.0f)), (float) ((top - this.topY) + this.currentBackgroundHeight));
                    }
                } else {
                    int nearRad3 = nearRad;
                    if (this.drawFullBubble || this.currentType == 2 || paintToUse != null || drawFullBottom) {
                        if (this.currentType == 1) {
                            path2.moveTo((float) (bounds.left + dp(8.0f) + rad), (float) (bounds.bottom - padding));
                        } else {
                            path2.moveTo((float) (bounds.left + dp(2.6f)), (float) (bounds.bottom - padding));
                        }
                        path2.lineTo((float) ((bounds.right - padding) - rad), (float) (bounds.bottom - padding));
                        this.rect.set((float) ((bounds.right - padding) - (rad * 2)), (float) ((bounds.bottom - padding) - (rad * 2)), (float) (bounds.right - padding), (float) (bounds.bottom - padding));
                        path2.arcTo(this.rect, 90.0f, -90.0f, false);
                    } else {
                        path2.moveTo((float) (bounds.left + dp(8.0f)), (float) ((top - this.topY) + this.currentBackgroundHeight));
                        path2.lineTo((float) (bounds.right - padding), (float) ((top - this.topY) + this.currentBackgroundHeight));
                    }
                    if (this.drawFullBubble || this.currentType == 2 || paintToUse != null || drawFullTop) {
                        path2.lineTo((float) (bounds.right - padding), (float) (bounds.top + padding + rad));
                        this.rect.set((float) ((bounds.right - padding) - (rad * 2)), (float) (bounds.top + padding), (float) (bounds.right - padding), (float) (bounds.top + padding + (rad * 2)));
                        path2.arcTo(this.rect, 0.0f, -90.0f, false);
                        int radToUse3 = this.isTopNear ? nearRad3 : rad;
                        if (this.currentType == 1) {
                            path2.lineTo((float) (bounds.left + padding + radToUse3), (float) (bounds.top + padding));
                            this.rect.set((float) (bounds.left + padding), (float) (bounds.top + padding), (float) (bounds.left + padding + (radToUse3 * 2)), (float) (bounds.top + padding + (radToUse3 * 2)));
                        } else {
                            path2.lineTo((float) (bounds.left + dp(8.0f) + radToUse3), (float) (bounds.top + padding));
                            this.rect.set((float) (bounds.left + dp(8.0f)), (float) (bounds.top + padding), (float) (bounds.left + dp(8.0f) + (radToUse3 * 2)), (float) (bounds.top + padding + (radToUse3 * 2)));
                        }
                        path2.arcTo(this.rect, 270.0f, -90.0f, false);
                    } else {
                        path2.lineTo((float) (bounds.right - padding), (float) ((top - this.topY) - dp(2.0f)));
                        if (this.currentType == 1) {
                            path2.lineTo((float) (bounds.left + padding), (float) ((top - this.topY) - dp(2.0f)));
                        } else {
                            path2.lineTo((float) (bounds.left + dp(8.0f)), (float) ((top - this.topY) - dp(2.0f)));
                        }
                    }
                    int i2 = this.currentType;
                    if (i2 == 1) {
                        if (paintToUse != null || drawFullBottom) {
                            int radToUse4 = this.isBottomNear ? nearRad3 : rad;
                            path2.lineTo((float) (bounds.left + padding), (float) ((bounds.bottom - padding) - radToUse4));
                            this.rect.set((float) (bounds.left + padding), (float) ((bounds.bottom - padding) - (radToUse4 * 2)), (float) (bounds.left + padding + (radToUse4 * 2)), (float) (bounds.bottom - padding));
                            path2.arcTo(this.rect, 180.0f, -90.0f, false);
                        } else {
                            path2.lineTo((float) (bounds.left + padding), (float) ((top - this.topY) + this.currentBackgroundHeight));
                        }
                    } else if (this.drawFullBubble || i2 == 2 || paintToUse != null || drawFullBottom) {
                        path2.lineTo((float) (bounds.left + dp(8.0f)), (float) (((bounds.bottom - padding) - smallRad) - dp(3.0f)));
                        this.rect.set((float) ((bounds.left + dp(7.0f)) - (smallRad * 2)), (float) (((bounds.bottom - padding) - (smallRad * 2)) - dp(9.0f)), (float) (bounds.left + dp(8.0f)), (float) ((bounds.bottom - padding) - dp(1.0f)));
                        path2.arcTo(this.rect, 0.0f, 83.0f, false);
                    } else {
                        path2.lineTo((float) (bounds.left + dp(8.0f)), (float) ((top - this.topY) + this.currentBackgroundHeight));
                    }
                }
                path2.close();
            } else {
                int i3 = nearRad;
            }
            Canvas canvas3 = canvas;
            canvas3.drawPath(path2, p);
            if (this.gradientShader != null && this.isSelected && paintToUse == null) {
                int color = getColor("chat_outBubbleGradientSelectedOverlay");
                this.selectedPaint.setColor(ColorUtils.setAlphaComponent(color, (int) (((float) (Color.alpha(color) * this.alpha)) / 255.0f)));
                canvas3.drawPath(path2, this.selectedPaint);
            }
        }

        public void setDrawFullBubble(boolean drawFullBuble) {
            this.drawFullBubble = drawFullBuble;
        }

        public void setAlpha(int alpha2) {
            if (this.alpha != alpha2) {
                this.alpha = alpha2;
                this.paint.setAlpha(alpha2);
                if (this.isOut) {
                    this.selectedPaint.setAlpha((int) (((float) Color.alpha(getColor("chat_outBubbleGradientSelectedOverlay"))) * (((float) alpha2) / 255.0f)));
                }
            }
            if (this.gradientShader == null) {
                Drawable background = getBackgroundDrawable();
                if (Build.VERSION.SDK_INT < 19) {
                    background.setAlpha(alpha2);
                } else if (background.getAlpha() != alpha2) {
                    background.setAlpha(alpha2);
                }
            }
        }

        public void setColorFilter(int color, PorterDuff.Mode mode) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public int getOpacity() {
            return -2;
        }

        public void setBounds(int left, int top, int right, int bottom) {
            super.setBounds(left, top, right, bottom);
            MessageDrawable messageDrawable = this.crossfadeFromDrawable;
            if (messageDrawable != null) {
                messageDrawable.setBounds(left, top, right, bottom);
            }
        }

        public void setRoundRadius(int radius) {
            this.overrideRoundRadius = radius;
        }

        public static class PathDrawParams {
            boolean lastDrawFullBottom;
            boolean lastDrawFullTop;
            Rect lastRect = new Rect();
            Path path = new Path();

            public boolean invalidatePath(Rect bounds, boolean drawFullBottom, boolean drawFullTop) {
                boolean invalidate = (!this.lastRect.isEmpty() && this.lastRect.top == bounds.top && this.lastRect.bottom == bounds.bottom && this.lastRect.right == bounds.right && this.lastRect.left == bounds.left && this.lastDrawFullTop == drawFullTop && this.lastDrawFullBottom == drawFullBottom && drawFullTop && drawFullBottom) ? false : true;
                this.lastDrawFullTop = drawFullTop;
                this.lastDrawFullBottom = drawFullBottom;
                this.lastRect.set(bounds);
                return invalidate;
            }
        }
    }

    public static class PatternsLoader implements NotificationCenter.NotificationCenterDelegate {
        private static PatternsLoader loader;
        private int account = UserConfig.selectedAccount;
        private HashMap<String, LoadingPattern> watingForLoad;

        private static class LoadingPattern {
            public ArrayList<ThemeAccent> accents;
            public TLRPC.TL_wallPaper pattern;

            private LoadingPattern() {
                this.accents = new ArrayList<>();
            }
        }

        public static void createLoader(boolean force) {
            String key;
            if (loader == null || force) {
                ArrayList<ThemeAccent> accentsToLoad = null;
                for (int b = 0; b < 5; b++) {
                    switch (b) {
                        case 0:
                            key = "Blue";
                            break;
                        case 1:
                            key = "Dark Blue";
                            break;
                        case 2:
                            key = "Arctic Blue";
                            break;
                        case 3:
                            key = "Day";
                            break;
                        default:
                            key = "Night";
                            break;
                    }
                    ThemeInfo info = (ThemeInfo) Theme.themesDict.get(key);
                    if (!(info == null || info.themeAccents == null || info.themeAccents.isEmpty())) {
                        int N = info.themeAccents.size();
                        for (int a = 0; a < N; a++) {
                            ThemeAccent accent = info.themeAccents.get(a);
                            if (accent.id != Theme.DEFALT_THEME_ACCENT_ID && !TextUtils.isEmpty(accent.patternSlug)) {
                                if (accentsToLoad == null) {
                                    accentsToLoad = new ArrayList<>();
                                }
                                accentsToLoad.add(accent);
                            }
                        }
                    }
                }
                loader = new PatternsLoader(accentsToLoad);
            }
        }

        private PatternsLoader(ArrayList<ThemeAccent> accents) {
            if (accents != null) {
                Utilities.globalQueue.postRunnable(new Theme$PatternsLoader$$ExternalSyntheticLambda0(this, accents));
            }
        }

        /* renamed from: lambda$new$1$org-telegram-ui-ActionBar-Theme$PatternsLoader  reason: not valid java name */
        public /* synthetic */ void m1335lambda$new$1$orgtelegramuiActionBarTheme$PatternsLoader(ArrayList accents) {
            ArrayList<String> slugs = null;
            int a = 0;
            int N = accents.size();
            while (a < N) {
                ThemeAccent accent = (ThemeAccent) accents.get(a);
                File wallpaper = accent.getPathToWallpaper();
                if (wallpaper == null || !wallpaper.exists()) {
                    if (slugs == null) {
                        slugs = new ArrayList<>();
                    }
                    if (!slugs.contains(accent.patternSlug)) {
                        slugs.add(accent.patternSlug);
                    }
                } else {
                    accents.remove(a);
                    a--;
                    N--;
                }
                a++;
            }
            if (slugs != null) {
                TLRPC.TL_account_getMultiWallPapers req = new TLRPC.TL_account_getMultiWallPapers();
                int N2 = slugs.size();
                for (int a2 = 0; a2 < N2; a2++) {
                    TLRPC.TL_inputWallPaperSlug slug = new TLRPC.TL_inputWallPaperSlug();
                    slug.slug = slugs.get(a2);
                    req.wallpapers.add(slug);
                }
                ConnectionsManager.getInstance(this.account).sendRequest(req, new Theme$PatternsLoader$$ExternalSyntheticLambda3(this, accents));
            }
        }

        /* renamed from: lambda$new$0$org-telegram-ui-ActionBar-Theme$PatternsLoader  reason: not valid java name */
        public /* synthetic */ void m1334lambda$new$0$orgtelegramuiActionBarTheme$PatternsLoader(ArrayList accents, TLObject response, TLRPC.TL_error error) {
            int N2;
            TLRPC.Vector res;
            int N22;
            TLRPC.Vector res2;
            TLRPC.Vector res3;
            int N23;
            TLObject tLObject = response;
            if (tLObject instanceof TLRPC.Vector) {
                TLRPC.Vector res4 = (TLRPC.Vector) tLObject;
                ArrayList<ThemeAccent> createdAccents = null;
                int b = 0;
                int N24 = res4.objects.size();
                while (b < N24) {
                    TLRPC.WallPaper object = (TLRPC.WallPaper) res4.objects.get(b);
                    if (!(object instanceof TLRPC.TL_wallPaper)) {
                        ArrayList arrayList = accents;
                        res = res4;
                        N2 = N24;
                    } else {
                        TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) object;
                        if (wallPaper.pattern) {
                            File patternPath = FileLoader.getPathToAttach(wallPaper.document, true);
                            Boolean exists = null;
                            Bitmap patternBitmap = null;
                            int a = 0;
                            int N = accents.size();
                            while (a < N) {
                                ThemeAccent accent = (ThemeAccent) accents.get(a);
                                if (accent.patternSlug.equals(wallPaper.slug)) {
                                    if (exists == null) {
                                        exists = Boolean.valueOf(patternPath.exists());
                                    }
                                    if (patternBitmap != null) {
                                        res3 = res4;
                                        N23 = N24;
                                    } else if (exists.booleanValue()) {
                                        res3 = res4;
                                        N23 = N24;
                                    } else {
                                        String key = FileLoader.getAttachFileName(wallPaper.document);
                                        if (this.watingForLoad == null) {
                                            this.watingForLoad = new HashMap<>();
                                        }
                                        LoadingPattern loadingPattern = this.watingForLoad.get(key);
                                        if (loadingPattern == null) {
                                            res2 = res4;
                                            N22 = N24;
                                            loadingPattern = new LoadingPattern();
                                            loadingPattern.pattern = wallPaper;
                                            this.watingForLoad.put(key, loadingPattern);
                                        } else {
                                            res2 = res4;
                                            N22 = N24;
                                        }
                                        loadingPattern.accents.add(accent);
                                    }
                                    Bitmap patternBitmap2 = createWallpaperForAccent(patternBitmap, "application/x-tgwallpattern".equals(wallPaper.document.mime_type), patternPath, accent);
                                    if (createdAccents == null) {
                                        createdAccents = new ArrayList<>();
                                    }
                                    createdAccents.add(accent);
                                    patternBitmap = patternBitmap2;
                                } else {
                                    res2 = res4;
                                    N22 = N24;
                                }
                                a++;
                                TLObject tLObject2 = response;
                                res4 = res2;
                                N24 = N22;
                            }
                            ArrayList arrayList2 = accents;
                            res = res4;
                            N2 = N24;
                            if (patternBitmap != null) {
                                patternBitmap.recycle();
                            }
                        } else {
                            ArrayList arrayList3 = accents;
                            res = res4;
                            N2 = N24;
                        }
                    }
                    b++;
                    TLObject tLObject3 = response;
                    res4 = res;
                    N24 = N2;
                }
                ArrayList arrayList4 = accents;
                TLRPC.Vector vector = res4;
                int i = N24;
                checkCurrentWallpaper(createdAccents, true);
                return;
            }
            ArrayList arrayList5 = accents;
        }

        private void checkCurrentWallpaper(ArrayList<ThemeAccent> accents, boolean load) {
            AndroidUtilities.runOnUIThread(new Theme$PatternsLoader$$ExternalSyntheticLambda1(this, accents, load));
        }

        /* access modifiers changed from: private */
        /* renamed from: checkCurrentWallpaperInternal */
        public void m1332x67876319(ArrayList<ThemeAccent> accents, boolean load) {
            if (accents != null && Theme.currentTheme.themeAccents != null && !Theme.currentTheme.themeAccents.isEmpty() && accents.contains(Theme.currentTheme.getAccent(false))) {
                Theme.reloadWallpaper();
            }
            if (!load) {
                HashMap<String, LoadingPattern> hashMap = this.watingForLoad;
                if (hashMap == null || hashMap.isEmpty()) {
                    NotificationCenter.getInstance(this.account).removeObserver(this, NotificationCenter.fileLoaded);
                    NotificationCenter.getInstance(this.account).removeObserver(this, NotificationCenter.fileLoadFailed);
                }
            } else if (this.watingForLoad != null) {
                NotificationCenter.getInstance(this.account).addObserver(this, NotificationCenter.fileLoaded);
                NotificationCenter.getInstance(this.account).addObserver(this, NotificationCenter.fileLoadFailed);
                for (Map.Entry<String, LoadingPattern> entry : this.watingForLoad.entrySet()) {
                    FileLoader.getInstance(this.account).loadFile(ImageLocation.getForDocument(entry.getValue().pattern.document), "wallpaper", (String) null, 0, 1);
                }
            }
        }

        private Bitmap createWallpaperForAccent(Bitmap patternBitmap, boolean svg, File patternPath, ThemeAccent accent) {
            Bitmap patternBitmap2;
            int patternColor;
            Drawable background;
            Integer color;
            Integer color2;
            Integer color3;
            File file = patternPath;
            ThemeAccent themeAccent = accent;
            try {
                File toFile = accent.getPathToWallpaper();
                if (toFile == null) {
                    return null;
                }
                ThemeInfo themeInfo = themeAccent.parentTheme;
                HashMap<String, Integer> values = Theme.getThemeFileValues((File) null, themeInfo.assetName, (String[]) null);
                Theme.checkIsDark(values, themeInfo);
                int backgroundAccent = themeAccent.accentColor;
                int backgroundColor = (int) themeAccent.backgroundOverrideColor;
                int backgroundGradientColor1 = (int) themeAccent.backgroundGradientOverrideColor1;
                if (backgroundGradientColor1 == 0 && themeAccent.backgroundGradientOverrideColor1 == 0) {
                    if (backgroundColor != 0) {
                        backgroundAccent = backgroundColor;
                    }
                    Integer color4 = values.get("chat_wallpaper_gradient_to");
                    if (color4 != null) {
                        backgroundGradientColor1 = Theme.changeColorAccent(themeInfo, backgroundAccent, color4.intValue());
                    }
                } else {
                    backgroundAccent = 0;
                }
                int backgroundGradientColor2 = (int) themeAccent.backgroundGradientOverrideColor2;
                if (backgroundGradientColor2 == 0 && themeAccent.backgroundGradientOverrideColor2 == 0 && (color3 = values.get("key_chat_wallpaper_gradient_to2")) != null) {
                    backgroundGradientColor2 = Theme.changeColorAccent(themeInfo, backgroundAccent, color3.intValue());
                }
                int backgroundGradientColor3 = (int) themeAccent.backgroundGradientOverrideColor3;
                if (backgroundGradientColor3 == 0 && themeAccent.backgroundGradientOverrideColor3 == 0 && (color2 = values.get("key_chat_wallpaper_gradient_to3")) != null) {
                    backgroundGradientColor3 = Theme.changeColorAccent(themeInfo, backgroundAccent, color2.intValue());
                }
                if (backgroundColor == 0 && (color = values.get("chat_wallpaper")) != null) {
                    backgroundColor = Theme.changeColorAccent(themeInfo, backgroundAccent, color.intValue());
                }
                if (backgroundGradientColor2 != 0) {
                    background = null;
                    patternColor = MotionBackgroundDrawable.getPatternColor(backgroundColor, backgroundGradientColor1, backgroundGradientColor2, backgroundGradientColor3);
                } else if (backgroundGradientColor1 != 0) {
                    BackgroundGradientDrawable backgroundGradientDrawable = new BackgroundGradientDrawable(BackgroundGradientDrawable.getGradientOrientation(themeAccent.backgroundRotation), new int[]{backgroundColor, backgroundGradientColor1});
                    patternColor = AndroidUtilities.getPatternColor(AndroidUtilities.getAverageColor(backgroundColor, backgroundGradientColor1));
                    background = backgroundGradientDrawable;
                } else {
                    background = new ColorDrawable(backgroundColor);
                    patternColor = AndroidUtilities.getPatternColor(backgroundColor);
                }
                if (patternBitmap != null) {
                    patternBitmap2 = patternBitmap;
                } else if (svg) {
                    patternBitmap2 = SvgHelper.getBitmap(file, AndroidUtilities.dp(360.0f), AndroidUtilities.dp(640.0f), false);
                } else {
                    patternBitmap2 = Theme.loadScreenSizedBitmap(new FileInputStream(file), 0);
                }
                if (background != null) {
                    try {
                        Bitmap dst = Bitmap.createBitmap(patternBitmap2.getWidth(), patternBitmap2.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(dst);
                        if (background != null) {
                            HashMap<String, Integer> hashMap = values;
                            background.setBounds(0, 0, patternBitmap2.getWidth(), patternBitmap2.getHeight());
                            background.draw(canvas);
                        }
                        Paint paint = new Paint(2);
                        paint.setColorFilter(new PorterDuffColorFilter(patternColor, PorterDuff.Mode.SRC_IN));
                        paint.setAlpha((int) (Math.abs(themeAccent.patternIntensity) * 255.0f));
                        canvas.drawBitmap(patternBitmap2, 0.0f, 0.0f, paint);
                        Paint paint2 = paint;
                        dst.compress(Bitmap.CompressFormat.JPEG, 87, new FileOutputStream(toFile));
                    } catch (Throwable th) {
                        e = th;
                        FileLog.e(e);
                        return patternBitmap2;
                    }
                } else {
                    FileOutputStream stream = new FileOutputStream(toFile);
                    patternBitmap2.compress(Bitmap.CompressFormat.PNG, 87, stream);
                    stream.close();
                }
                return patternBitmap2;
            } catch (Throwable th2) {
                e = th2;
                patternBitmap2 = patternBitmap;
                FileLog.e(e);
                return patternBitmap2;
            }
        }

        public void didReceivedNotification(int id, int account2, Object... args) {
            if (this.watingForLoad != null) {
                if (id == NotificationCenter.fileLoaded) {
                    LoadingPattern loadingPattern = this.watingForLoad.remove(args[0]);
                    if (loadingPattern != null) {
                        Utilities.globalQueue.postRunnable(new Theme$PatternsLoader$$ExternalSyntheticLambda2(this, loadingPattern));
                    }
                } else if (id == NotificationCenter.fileLoadFailed) {
                    if (this.watingForLoad.remove(args[0]) != null) {
                        checkCurrentWallpaper((ArrayList<ThemeAccent>) null, false);
                    }
                }
            }
        }

        /* renamed from: lambda$didReceivedNotification$3$org-telegram-ui-ActionBar-Theme$PatternsLoader  reason: not valid java name */
        public /* synthetic */ void m1333x5var_e(LoadingPattern loadingPattern) {
            ArrayList<ThemeAccent> createdAccents = null;
            TLRPC.TL_wallPaper wallPaper = loadingPattern.pattern;
            File patternPath = FileLoader.getPathToAttach(wallPaper.document, true);
            Bitmap patternBitmap = null;
            int N = loadingPattern.accents.size();
            for (int a = 0; a < N; a++) {
                ThemeAccent accent = loadingPattern.accents.get(a);
                if (accent.patternSlug.equals(wallPaper.slug)) {
                    patternBitmap = createWallpaperForAccent(patternBitmap, "application/x-tgwallpattern".equals(wallPaper.document.mime_type), patternPath, accent);
                    if (createdAccents == null) {
                        createdAccents = new ArrayList<>();
                        createdAccents.add(accent);
                    }
                }
            }
            if (patternBitmap != null) {
                patternBitmap.recycle();
            }
            checkCurrentWallpaper(createdAccents, false);
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
        public TLRPC.TL_theme info;
        public boolean isDefault;
        public int myMessagesAccentColor;
        public boolean myMessagesAnimated;
        public int myMessagesGradientAccentColor1;
        public int myMessagesGradientAccentColor2;
        public int myMessagesGradientAccentColor3;
        public OverrideWallpaperInfo overrideWallpaper;
        public ThemeInfo parentTheme;
        public String pathToFile;
        public TLRPC.TL_wallPaper pattern;
        public float patternIntensity;
        public boolean patternMotion;
        public String patternSlug = "";
        public TLRPC.InputFile uploadedFile;
        public TLRPC.InputFile uploadedThumb;
        public String uploadingFile;
        public String uploadingThumb;

        ThemeAccent() {
        }

        public ThemeAccent(ThemeAccent other) {
            this.id = other.id;
            this.parentTheme = other.parentTheme;
            this.accentColor = other.accentColor;
            this.myMessagesAccentColor = other.myMessagesAccentColor;
            this.myMessagesGradientAccentColor1 = other.myMessagesGradientAccentColor1;
            this.myMessagesGradientAccentColor2 = other.myMessagesGradientAccentColor2;
            this.myMessagesGradientAccentColor3 = other.myMessagesGradientAccentColor3;
            this.myMessagesAnimated = other.myMessagesAnimated;
            this.backgroundOverrideColor = other.backgroundOverrideColor;
            this.backgroundGradientOverrideColor1 = other.backgroundGradientOverrideColor1;
            this.backgroundGradientOverrideColor2 = other.backgroundGradientOverrideColor2;
            this.backgroundGradientOverrideColor3 = other.backgroundGradientOverrideColor3;
            this.backgroundRotation = other.backgroundRotation;
            this.patternSlug = other.patternSlug;
            this.patternIntensity = other.patternIntensity;
            this.patternMotion = other.patternMotion;
            this.info = other.info;
            this.pattern = other.pattern;
            this.account = other.account;
            this.pathToFile = other.pathToFile;
            this.uploadingThumb = other.uploadingThumb;
            this.uploadingFile = other.uploadingFile;
            this.uploadedThumb = other.uploadedThumb;
            this.uploadedFile = other.uploadedFile;
            this.overrideWallpaper = other.overrideWallpaper;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v8, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v18, resolved type: java.lang.Integer} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v15, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v23, resolved type: java.lang.Integer} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v15, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v23, resolved type: java.lang.Integer} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v43, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v6, resolved type: java.lang.Integer} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean fillAccentColors(java.util.HashMap<java.lang.String, java.lang.Integer> r20, java.util.HashMap<java.lang.String, java.lang.Integer> r21) {
            /*
                r19 = this;
                r0 = r19
                r1 = r20
                r2 = r21
                r3 = 0
                r4 = 1
                float[] r5 = org.telegram.ui.ActionBar.Theme.getTempHsv(r4)
                r6 = 2
                float[] r6 = org.telegram.ui.ActionBar.Theme.getTempHsv(r6)
                org.telegram.ui.ActionBar.Theme$ThemeInfo r7 = r0.parentTheme
                int r7 = r7.accentBaseColor
                android.graphics.Color.colorToHSV(r7, r5)
                int r7 = r0.accentColor
                android.graphics.Color.colorToHSV(r7, r6)
                org.telegram.ui.ActionBar.Theme$ThemeInfo r7 = r0.parentTheme
                boolean r7 = r7.isDark()
                int r8 = r0.accentColor
                org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = r0.parentTheme
                int r9 = r9.accentBaseColor
                if (r8 != r9) goto L_0x002f
                int r8 = r0.accentColor2
                if (r8 == 0) goto L_0x0098
            L_0x002f:
                java.util.HashSet r8 = new java.util.HashSet
                java.util.Set r9 = r20.keySet()
                r8.<init>(r9)
                java.util.HashMap r9 = org.telegram.ui.ActionBar.Theme.defaultColors
                java.util.Set r9 = r9.keySet()
                r8.addAll(r9)
                java.util.HashSet r9 = org.telegram.ui.ActionBar.Theme.themeAccentExclusionKeys
                r8.removeAll(r9)
                java.util.Iterator r9 = r8.iterator()
            L_0x004e:
                boolean r10 = r9.hasNext()
                if (r10 == 0) goto L_0x0098
                java.lang.Object r10 = r9.next()
                java.lang.String r10 = (java.lang.String) r10
                java.lang.Object r11 = r1.get(r10)
                java.lang.Integer r11 = (java.lang.Integer) r11
                if (r11 != 0) goto L_0x0075
                java.util.HashMap r12 = org.telegram.ui.ActionBar.Theme.fallbackKeys
                java.lang.Object r12 = r12.get(r10)
                java.lang.String r12 = (java.lang.String) r12
                if (r12 == 0) goto L_0x0075
                java.lang.Object r13 = r1.get(r12)
                if (r13 == 0) goto L_0x0075
                goto L_0x004e
            L_0x0075:
                if (r11 != 0) goto L_0x0082
                java.util.HashMap r12 = org.telegram.ui.ActionBar.Theme.defaultColors
                java.lang.Object r12 = r12.get(r10)
                r11 = r12
                java.lang.Integer r11 = (java.lang.Integer) r11
            L_0x0082:
                int r12 = r11.intValue()
                int r12 = org.telegram.ui.ActionBar.Theme.changeColorAccent(r5, r6, r12, r7)
                int r13 = r11.intValue()
                if (r12 == r13) goto L_0x0097
                java.lang.Integer r13 = java.lang.Integer.valueOf(r12)
                r2.put(r10, r13)
            L_0x0097:
                goto L_0x004e
            L_0x0098:
                int r8 = r0.myMessagesAccentColor
                int r9 = r0.myMessagesAccentColor
                java.lang.String r11 = "chat_outBubble"
                if (r9 != 0) goto L_0x00a4
                int r13 = r0.accentColor
                if (r13 == 0) goto L_0x011b
            L_0x00a4:
                int r13 = r0.myMessagesGradientAccentColor1
                if (r13 == 0) goto L_0x011b
                if (r9 == 0) goto L_0x00ab
                goto L_0x00ad
            L_0x00ab:
                int r9 = r0.accentColor
            L_0x00ad:
                java.lang.Object r13 = r1.get(r11)
                java.lang.Integer r13 = (java.lang.Integer) r13
                if (r13 != 0) goto L_0x00c0
                java.util.HashMap r14 = org.telegram.ui.ActionBar.Theme.defaultColors
                java.lang.Object r14 = r14.get(r11)
                r13 = r14
                java.lang.Integer r13 = (java.lang.Integer) r13
            L_0x00c0:
                int r14 = r13.intValue()
                int r14 = org.telegram.ui.ActionBar.Theme.changeColorAccent(r5, r6, r14, r7)
                int r15 = org.telegram.messenger.AndroidUtilities.getColorDistance(r9, r14)
                int r4 = r0.myMessagesGradientAccentColor1
                int r4 = org.telegram.messenger.AndroidUtilities.getColorDistance(r9, r4)
                int r12 = r0.myMessagesGradientAccentColor2
                if (r12 == 0) goto L_0x00fc
                int r12 = r0.myMessagesAccentColor
                int r10 = r0.myMessagesGradientAccentColor1
                int r10 = org.telegram.messenger.AndroidUtilities.getAverageColor(r12, r10)
                int r12 = r0.myMessagesGradientAccentColor2
                int r10 = org.telegram.messenger.AndroidUtilities.getAverageColor(r10, r12)
                int r12 = r0.myMessagesGradientAccentColor3
                if (r12 == 0) goto L_0x00ec
                int r10 = org.telegram.messenger.AndroidUtilities.getAverageColor(r10, r12)
            L_0x00ec:
                float r12 = org.telegram.messenger.AndroidUtilities.computePerceivedBrightness(r10)
                r17 = 1060403937(0x3var_ae1, float:0.705)
                int r12 = (r12 > r17 ? 1 : (r12 == r17 ? 0 : -1))
                if (r12 <= 0) goto L_0x00f9
                r12 = 1
                goto L_0x00fa
            L_0x00f9:
                r12 = 0
            L_0x00fa:
                r10 = r12
                goto L_0x0104
            L_0x00fc:
                int r10 = r0.myMessagesAccentColor
                int r12 = r0.myMessagesGradientAccentColor1
                boolean r10 = org.telegram.ui.ActionBar.Theme.useBlackText(r10, r12)
            L_0x0104:
                if (r10 == 0) goto L_0x0112
                r12 = 35000(0x88b8, float:4.9045E-41)
                if (r15 > r12) goto L_0x010f
                if (r4 > r12) goto L_0x010f
                r12 = 1
                goto L_0x0110
            L_0x010f:
                r12 = 0
            L_0x0110:
                r3 = r12
                goto L_0x0113
            L_0x0112:
                r3 = 0
            L_0x0113:
                int r12 = r13.intValue()
                int r8 = org.telegram.ui.ActionBar.Theme.getAccentColor(r5, r12, r9)
            L_0x011b:
                if (r8 == 0) goto L_0x0131
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r0.parentTheme
                int r4 = r4.accentBaseColor
                if (r4 == 0) goto L_0x0129
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r0.parentTheme
                int r4 = r4.accentBaseColor
                if (r8 != r4) goto L_0x012f
            L_0x0129:
                int r4 = r0.accentColor
                if (r4 == 0) goto L_0x0131
                if (r4 == r8) goto L_0x0131
            L_0x012f:
                r4 = 1
                goto L_0x0132
            L_0x0131:
                r4 = 0
            L_0x0132:
                if (r4 != 0) goto L_0x0138
                int r9 = r0.accentColor2
                if (r9 == 0) goto L_0x01f2
            L_0x0138:
                int r9 = r0.accentColor2
                if (r9 == 0) goto L_0x0140
                android.graphics.Color.colorToHSV(r9, r6)
                goto L_0x0143
            L_0x0140:
                android.graphics.Color.colorToHSV(r8, r6)
            L_0x0143:
                java.util.HashSet r9 = org.telegram.ui.ActionBar.Theme.myMessagesColorKeys
                java.util.Iterator r9 = r9.iterator()
            L_0x014b:
                boolean r10 = r9.hasNext()
                if (r10 == 0) goto L_0x0198
                java.lang.Object r10 = r9.next()
                java.lang.String r10 = (java.lang.String) r10
                java.lang.Object r12 = r1.get(r10)
                java.lang.Integer r12 = (java.lang.Integer) r12
                if (r12 != 0) goto L_0x0172
                java.util.HashMap r13 = org.telegram.ui.ActionBar.Theme.fallbackKeys
                java.lang.Object r13 = r13.get(r10)
                java.lang.String r13 = (java.lang.String) r13
                if (r13 == 0) goto L_0x0172
                java.lang.Object r14 = r1.get(r13)
                if (r14 == 0) goto L_0x0172
                goto L_0x014b
            L_0x0172:
                if (r12 != 0) goto L_0x017f
                java.util.HashMap r13 = org.telegram.ui.ActionBar.Theme.defaultColors
                java.lang.Object r13 = r13.get(r10)
                r12 = r13
                java.lang.Integer r12 = (java.lang.Integer) r12
            L_0x017f:
                if (r12 != 0) goto L_0x0182
                goto L_0x014b
            L_0x0182:
                int r13 = r12.intValue()
                int r13 = org.telegram.ui.ActionBar.Theme.changeColorAccent(r5, r6, r13, r7)
                int r14 = r12.intValue()
                if (r13 == r14) goto L_0x0197
                java.lang.Integer r14 = java.lang.Integer.valueOf(r13)
                r2.put(r10, r14)
            L_0x0197:
                goto L_0x014b
            L_0x0198:
                if (r4 == 0) goto L_0x01f2
                android.graphics.Color.colorToHSV(r8, r6)
                java.util.HashSet r9 = org.telegram.ui.ActionBar.Theme.myMessagesBubblesColorKeys
                java.util.Iterator r9 = r9.iterator()
            L_0x01a5:
                boolean r10 = r9.hasNext()
                if (r10 == 0) goto L_0x01f2
                java.lang.Object r10 = r9.next()
                java.lang.String r10 = (java.lang.String) r10
                java.lang.Object r12 = r1.get(r10)
                java.lang.Integer r12 = (java.lang.Integer) r12
                if (r12 != 0) goto L_0x01cc
                java.util.HashMap r13 = org.telegram.ui.ActionBar.Theme.fallbackKeys
                java.lang.Object r13 = r13.get(r10)
                java.lang.String r13 = (java.lang.String) r13
                if (r13 == 0) goto L_0x01cc
                java.lang.Object r14 = r1.get(r13)
                if (r14 == 0) goto L_0x01cc
                goto L_0x01a5
            L_0x01cc:
                if (r12 != 0) goto L_0x01d9
                java.util.HashMap r13 = org.telegram.ui.ActionBar.Theme.defaultColors
                java.lang.Object r13 = r13.get(r10)
                r12 = r13
                java.lang.Integer r12 = (java.lang.Integer) r12
            L_0x01d9:
                if (r12 != 0) goto L_0x01dc
                goto L_0x01a5
            L_0x01dc:
                int r13 = r12.intValue()
                int r13 = org.telegram.ui.ActionBar.Theme.changeColorAccent(r5, r6, r13, r7)
                int r14 = r12.intValue()
                if (r13 == r14) goto L_0x01f1
                java.lang.Integer r14 = java.lang.Integer.valueOf(r13)
                r2.put(r10, r14)
            L_0x01f1:
                goto L_0x01a5
            L_0x01f2:
                java.lang.String r9 = "chat_outLoader"
                if (r3 != 0) goto L_0x0433
                int r10 = r0.myMessagesGradientAccentColor1
                if (r10 == 0) goto L_0x0433
                int r12 = r0.myMessagesGradientAccentColor2
                if (r12 == 0) goto L_0x0222
                int r12 = r0.myMessagesAccentColor
                int r10 = org.telegram.messenger.AndroidUtilities.getAverageColor(r12, r10)
                int r12 = r0.myMessagesGradientAccentColor2
                int r10 = org.telegram.messenger.AndroidUtilities.getAverageColor(r10, r12)
                int r12 = r0.myMessagesGradientAccentColor3
                if (r12 == 0) goto L_0x0212
                int r10 = org.telegram.messenger.AndroidUtilities.getAverageColor(r10, r12)
            L_0x0212:
                float r12 = org.telegram.messenger.AndroidUtilities.computePerceivedBrightness(r10)
                r13 = 1060403937(0x3var_ae1, float:0.705)
                int r12 = (r12 > r13 ? 1 : (r12 == r13 ? 0 : -1))
                if (r12 <= 0) goto L_0x021f
                r12 = 1
                goto L_0x0220
            L_0x021f:
                r12 = 0
            L_0x0220:
                r10 = r12
                goto L_0x0228
            L_0x0222:
                int r12 = r0.myMessagesAccentColor
                boolean r10 = org.telegram.ui.ActionBar.Theme.useBlackText(r12, r10)
            L_0x0228:
                if (r10 == 0) goto L_0x0233
                r12 = -14606047(0xfffffffffvar_, float:-2.1417772E38)
                r13 = -11184811(0xfffffffffvar_, float:-2.8356863E38)
                r14 = 1291845632(0x4d000000, float:1.34217728E8)
                goto L_0x023a
            L_0x0233:
                r12 = -1
                r13 = -1118482(0xffffffffffeeeeee, float:NaN)
                r14 = 1308622847(0x4dffffff, float:5.3687088E8)
            L_0x023a:
                int r15 = r0.accentColor2
                if (r15 != 0) goto L_0x040f
                java.lang.Integer r15 = java.lang.Integer.valueOf(r14)
                java.lang.String r1 = "chat_outAudioProgress"
                r2.put(r1, r15)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r14)
                java.lang.String r15 = "chat_outAudioSelectedProgress"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r14)
                java.lang.String r15 = "chat_outAudioSeekbar"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r14)
                java.lang.String r15 = "chat_outAudioCacheSeekbar"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r14)
                java.lang.String r15 = "chat_outAudioSeekbarSelected"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outAudioSeekbarFill"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r14)
                java.lang.String r15 = "chat_outVoiceSeekbar"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r14)
                java.lang.String r15 = "chat_outVoiceSeekbarSelected"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outVoiceSeekbarFill"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_messageLinkOut"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outForwardedNameText"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outViaBotNameText"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outReplyLine"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outReplyNameText"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outPreviewLine"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outSiteNameText"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outInstant"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outInstantSelected"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outPreviewInstantText"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outPreviewInstantSelectedText"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outViews"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outViewsSelected"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outAudioTitleText"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outFileNameText"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outContactNameText"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outAudioPerfomerText"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outAudioPerfomerSelectedText"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outSentCheck"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outSentCheckSelected"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outSentCheckRead"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outSentCheckReadSelected"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outSentClock"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outSentClockSelected"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outMenu"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outMenuSelected"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outTimeText"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outTimeSelectedText"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r13)
                java.lang.String r15 = "chat_outAudioDurationText"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r13)
                java.lang.String r15 = "chat_outAudioDurationSelectedText"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r13)
                java.lang.String r15 = "chat_outContactPhoneText"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r13)
                java.lang.String r15 = "chat_outContactPhoneSelectedText"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r13)
                java.lang.String r15 = "chat_outFileInfoText"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r13)
                java.lang.String r15 = "chat_outFileInfoSelectedText"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r13)
                java.lang.String r15 = "chat_outVenueInfoText"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r13)
                java.lang.String r15 = "chat_outVenueInfoSelectedText"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                r2.put(r9, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outLoaderSelected"
                r2.put(r15, r1)
                int r1 = r0.myMessagesAccentColor
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                java.lang.String r15 = "chat_outFileProgress"
                r2.put(r15, r1)
                int r1 = r0.myMessagesAccentColor
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                java.lang.String r15 = "chat_outFileProgressSelected"
                r2.put(r15, r1)
                int r1 = r0.myMessagesAccentColor
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                java.lang.String r15 = "chat_outMediaIcon"
                r2.put(r15, r1)
                int r1 = r0.myMessagesAccentColor
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                java.lang.String r15 = "chat_outMediaIconSelected"
                r2.put(r15, r1)
            L_0x040f:
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outReplyMessageText"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outReplyMediaMessageText"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_outReplyMediaMessageSelectedText"
                r2.put(r15, r1)
                java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
                java.lang.String r15 = "chat_messageTextOut"
                r2.put(r15, r1)
            L_0x0433:
                if (r3 == 0) goto L_0x0452
                boolean r1 = r2.containsKey(r9)
                if (r1 == 0) goto L_0x0446
                java.lang.Object r1 = r2.get(r9)
                java.lang.Integer r1 = (java.lang.Integer) r1
                int r1 = r1.intValue()
                goto L_0x0447
            L_0x0446:
                r1 = 0
            L_0x0447:
                r9 = -1
                int r9 = org.telegram.messenger.AndroidUtilities.getColorDistance(r9, r1)
                r10 = 5000(0x1388, float:7.006E-42)
                if (r9 >= r10) goto L_0x0452
                r3 = 0
            L_0x0452:
                int r1 = r0.myMessagesAccentColor
                if (r1 == 0) goto L_0x0491
                int r9 = r0.myMessagesGradientAccentColor1
                if (r9 == 0) goto L_0x0491
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                r2.put(r11, r1)
                int r1 = r0.myMessagesGradientAccentColor1
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                java.lang.String r9 = "chat_outBubbleGradient"
                r2.put(r9, r1)
                int r1 = r0.myMessagesGradientAccentColor2
                if (r1 == 0) goto L_0x0486
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                java.lang.String r9 = "chat_outBubbleGradient2"
                r2.put(r9, r1)
                int r1 = r0.myMessagesGradientAccentColor3
                if (r1 == 0) goto L_0x0486
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                java.lang.String r9 = "chat_outBubbleGradient3"
                r2.put(r9, r1)
            L_0x0486:
                boolean r1 = r0.myMessagesAnimated
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                java.lang.String r9 = "chat_outBubbleGradientAnimated"
                r2.put(r9, r1)
            L_0x0491:
                long r9 = r0.backgroundOverrideColor
                int r1 = (int) r9
                java.lang.String r11 = "chat_wallpaper"
                r12 = 0
                if (r1 == 0) goto L_0x04a2
                java.lang.Integer r9 = java.lang.Integer.valueOf(r1)
                r2.put(r11, r9)
                goto L_0x04a9
            L_0x04a2:
                int r14 = (r9 > r12 ? 1 : (r9 == r12 ? 0 : -1))
                if (r14 == 0) goto L_0x04a9
                r2.remove(r11)
            L_0x04a9:
                long r9 = r0.backgroundGradientOverrideColor1
                int r11 = (int) r9
                java.lang.String r14 = "chat_wallpaper_gradient_to"
                if (r11 == 0) goto L_0x04b8
                java.lang.Integer r9 = java.lang.Integer.valueOf(r11)
                r2.put(r14, r9)
                goto L_0x04bf
            L_0x04b8:
                int r15 = (r9 > r12 ? 1 : (r9 == r12 ? 0 : -1))
                if (r15 == 0) goto L_0x04bf
                r2.remove(r14)
            L_0x04bf:
                long r9 = r0.backgroundGradientOverrideColor2
                int r14 = (int) r9
                java.lang.String r15 = "key_chat_wallpaper_gradient_to2"
                if (r14 == 0) goto L_0x04ce
                java.lang.Integer r9 = java.lang.Integer.valueOf(r14)
                r2.put(r15, r9)
                goto L_0x04d5
            L_0x04ce:
                int r17 = (r9 > r12 ? 1 : (r9 == r12 ? 0 : -1))
                if (r17 == 0) goto L_0x04d5
                r2.remove(r15)
            L_0x04d5:
                long r9 = r0.backgroundGradientOverrideColor3
                int r15 = (int) r9
                java.lang.String r12 = "key_chat_wallpaper_gradient_to3"
                if (r15 == 0) goto L_0x04e4
                java.lang.Integer r9 = java.lang.Integer.valueOf(r15)
                r2.put(r12, r9)
                goto L_0x04ed
            L_0x04e4:
                r17 = 0
                int r13 = (r9 > r17 ? 1 : (r9 == r17 ? 0 : -1))
                if (r13 == 0) goto L_0x04ed
                r2.remove(r12)
            L_0x04ed:
                int r9 = r0.backgroundRotation
                r10 = 45
                if (r9 == r10) goto L_0x04fc
                java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
                java.lang.String r10 = "chat_wallpaper_gradient_rotation"
                r2.put(r10, r9)
            L_0x04fc:
                if (r3 != 0) goto L_0x0501
                r16 = 1
                goto L_0x0503
            L_0x0501:
                r16 = 0
            L_0x0503:
                return r16
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.ThemeAccent.fillAccentColors(java.util.HashMap, java.util.HashMap):boolean");
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

        public File saveToFile() {
            String wallpaperLink;
            String color2;
            String color3;
            String str;
            File dir = AndroidUtilities.getSharingDirectory();
            dir.mkdirs();
            File path = new File(dir, String.format(Locale.US, "%s_%d.attheme", new Object[]{this.parentTheme.getKey(), Integer.valueOf(this.id)}));
            HashMap<String, Integer> currentColorsNoAccent = Theme.getThemeFileValues((File) null, this.parentTheme.assetName, (String[]) null);
            HashMap hashMap = new HashMap(currentColorsNoAccent);
            fillAccentColors(currentColorsNoAccent, hashMap);
            if (!TextUtils.isEmpty(this.patternSlug)) {
                StringBuilder modes = new StringBuilder();
                if (this.patternMotion) {
                    modes.append("motion");
                }
                Integer selectedColor = (Integer) hashMap.get("chat_wallpaper");
                if (selectedColor == null) {
                    selectedColor = -1;
                }
                Integer selectedGradientColor1 = (Integer) hashMap.get("chat_wallpaper_gradient_to");
                if (selectedGradientColor1 == null) {
                    selectedGradientColor1 = 0;
                }
                Integer selectedGradientColor2 = (Integer) hashMap.get("key_chat_wallpaper_gradient_to2");
                if (selectedGradientColor2 == null) {
                    selectedGradientColor2 = 0;
                }
                Integer selectedGradientColor3 = (Integer) hashMap.get("key_chat_wallpaper_gradient_to3");
                if (selectedGradientColor3 == null) {
                    selectedGradientColor3 = 0;
                }
                int selectedGradientRotation = (Integer) hashMap.get("chat_wallpaper_gradient_rotation");
                if (selectedGradientRotation == null) {
                    selectedGradientRotation = 45;
                }
                String color = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (selectedColor.intValue() >> 16)) & 255), Integer.valueOf(((byte) (selectedColor.intValue() >> 8)) & 255), Byte.valueOf((byte) (selectedColor.intValue() & 255))}).toLowerCase();
                if (selectedGradientColor1.intValue() != 0) {
                    color2 = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (selectedGradientColor1.intValue() >> 16)) & 255), Integer.valueOf(((byte) (selectedGradientColor1.intValue() >> 8)) & 255), Byte.valueOf((byte) (selectedGradientColor1.intValue() & 255))}).toLowerCase();
                } else {
                    color2 = null;
                }
                if (selectedGradientColor2.intValue() != 0) {
                    File file = dir;
                    color3 = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (selectedGradientColor2.intValue() >> 16)) & 255), Integer.valueOf(((byte) (selectedGradientColor2.intValue() >> 8)) & 255), Byte.valueOf((byte) (selectedGradientColor2.intValue() & 255))}).toLowerCase();
                } else {
                    color3 = null;
                }
                if (selectedGradientColor3.intValue() != 0) {
                    HashMap<String, Integer> hashMap2 = currentColorsNoAccent;
                    str = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (selectedGradientColor3.intValue() >> 16)) & 255), Integer.valueOf(((byte) (selectedGradientColor3.intValue() >> 8)) & 255), Byte.valueOf((byte) (selectedGradientColor3.intValue() & 255))}).toLowerCase();
                } else {
                    str = null;
                }
                String color4 = str;
                if (color2 == null || color3 == null) {
                    if (color2 != null) {
                        color = (color + "-" + color2) + "&rotation=" + selectedGradientRotation;
                    }
                } else if (color4 != null) {
                    color = color + "~" + color2 + "~" + color3 + "~" + color4;
                } else {
                    color = color + "~" + color2 + "~" + color3;
                }
                wallpaperLink = "https://attheme.org?slug=" + this.patternSlug + "&intensity=" + ((int) (this.patternIntensity * 100.0f)) + "&bg_color=" + color;
                if (modes.length() > 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(wallpaperLink);
                    String str2 = color2;
                    sb.append("&mode=");
                    sb.append(modes.toString());
                    wallpaperLink = sb.toString();
                }
            } else {
                File file2 = dir;
                HashMap<String, Integer> hashMap3 = currentColorsNoAccent;
                wallpaperLink = null;
            }
            StringBuilder result = new StringBuilder();
            for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
                String key = entry.getKey();
                if (wallpaperLink == null || (!"chat_wallpaper".equals(key) && !"chat_wallpaper_gradient_to".equals(key) && !"key_chat_wallpaper_gradient_to2".equals(key) && !"key_chat_wallpaper_gradient_to3".equals(key))) {
                    result.append(key);
                    result.append("=");
                    result.append(entry.getValue());
                    result.append("\n");
                }
            }
            FileOutputStream stream = null;
            try {
                FileOutputStream stream2 = new FileOutputStream(path);
                stream2.write(AndroidUtilities.getStringBytes(result.toString()));
                if (!TextUtils.isEmpty(wallpaperLink)) {
                    stream2.write(AndroidUtilities.getStringBytes("WLS=" + wallpaperLink + "\n"));
                }
                try {
                    stream2.close();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
                if (stream != null) {
                    stream.close();
                }
            } catch (Throwable th) {
                Throwable th2 = th;
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception e3) {
                        FileLog.e((Throwable) e3);
                    }
                }
                throw th2;
            }
            return path;
        }
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

        public OverrideWallpaperInfo(OverrideWallpaperInfo info, ThemeInfo themeInfo, ThemeAccent accent) {
            this.slug = info.slug;
            this.color = info.color;
            this.gradientColor1 = info.gradientColor1;
            this.gradientColor2 = info.gradientColor2;
            this.gradientColor3 = info.gradientColor3;
            this.rotation = info.rotation;
            this.isBlurred = info.isBlurred;
            this.isMotion = info.isMotion;
            this.intensity = info.intensity;
            this.parentTheme = themeInfo;
            this.parentAccent = accent;
            if (!TextUtils.isEmpty(info.fileName)) {
                try {
                    File fromFile = new File(ApplicationLoader.getFilesDirFixed(), info.fileName);
                    File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                    String generateWallpaperName = this.parentTheme.generateWallpaperName(this.parentAccent, false);
                    this.fileName = generateWallpaperName;
                    AndroidUtilities.copyFile(fromFile, new File(filesDirFixed, generateWallpaperName));
                } catch (Exception e) {
                    this.fileName = "";
                    FileLog.e((Throwable) e);
                }
            } else {
                this.fileName = "";
            }
            if (TextUtils.isEmpty(info.originalFileName)) {
                this.originalFileName = "";
            } else if (!info.originalFileName.equals(info.fileName)) {
                try {
                    File fromFile2 = new File(ApplicationLoader.getFilesDirFixed(), info.originalFileName);
                    File filesDirFixed2 = ApplicationLoader.getFilesDirFixed();
                    String generateWallpaperName2 = this.parentTheme.generateWallpaperName(this.parentAccent, true);
                    this.originalFileName = generateWallpaperName2;
                    AndroidUtilities.copyFile(fromFile2, new File(filesDirFixed2, generateWallpaperName2));
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
                SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("wall", this.fileName);
                jsonObject.put("owall", this.originalFileName);
                jsonObject.put("pColor", this.color);
                jsonObject.put("pGrColor", this.gradientColor1);
                jsonObject.put("pGrColor2", this.gradientColor2);
                jsonObject.put("pGrColor3", this.gradientColor3);
                jsonObject.put("pGrAngle", this.rotation);
                String str = this.slug;
                if (str == null) {
                    str = "";
                }
                jsonObject.put("wallSlug", str);
                jsonObject.put("wBlur", this.isBlurred);
                jsonObject.put("wMotion", this.isMotion);
                jsonObject.put("pIntensity", (double) this.intensity);
                editor.putString(key, jsonObject.toString());
                editor.commit();
            } catch (Throwable e) {
                FileLog.e(e);
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
        private static final int DARK = 1;
        private static final int LIGHT = 0;
        private static final int UNKNOWN = -1;
        public int accentBaseColor;
        public LongSparseArray<ThemeAccent> accentsByThemeId;
        public int account;
        public String assetName;
        public boolean badWallpaper;
        public LongSparseArray<ThemeAccent> chatAccentsByThemeId = new LongSparseArray<>();
        public int currentAccentId;
        public int defaultAccentCount;
        public boolean firstAccentIsDefault;
        public TLRPC.TL_theme info;
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
        public TLRPC.InputFile uploadedFile;
        public TLRPC.InputFile uploadedThumb;
        public String uploadingFile;
        public String uploadingThumb;

        ThemeInfo() {
        }

        public ThemeInfo(ThemeInfo other) {
            this.name = other.name;
            this.pathToFile = other.pathToFile;
            this.pathToWallpaper = other.pathToWallpaper;
            this.assetName = other.assetName;
            this.slug = other.slug;
            this.badWallpaper = other.badWallpaper;
            this.isBlured = other.isBlured;
            this.isMotion = other.isMotion;
            this.patternBgColor = other.patternBgColor;
            this.patternBgGradientColor1 = other.patternBgGradientColor1;
            this.patternBgGradientColor2 = other.patternBgGradientColor2;
            this.patternBgGradientColor3 = other.patternBgGradientColor3;
            this.patternBgGradientRotation = other.patternBgGradientRotation;
            this.patternIntensity = other.patternIntensity;
            this.account = other.account;
            this.info = other.info;
            this.loaded = other.loaded;
            this.uploadingThumb = other.uploadingThumb;
            this.uploadingFile = other.uploadingFile;
            this.uploadedThumb = other.uploadedThumb;
            this.uploadedFile = other.uploadedFile;
            this.previewBackgroundColor = other.previewBackgroundColor;
            this.previewBackgroundGradientColor1 = other.previewBackgroundGradientColor1;
            this.previewBackgroundGradientColor2 = other.previewBackgroundGradientColor2;
            this.previewBackgroundGradientColor3 = other.previewBackgroundGradientColor3;
            this.previewWallpaperOffset = other.previewWallpaperOffset;
            this.previewInColor = other.previewInColor;
            this.previewOutColor = other.previewOutColor;
            this.firstAccentIsDefault = other.firstAccentIsDefault;
            this.previewParsed = other.previewParsed;
            this.themeLoaded = other.themeLoaded;
            this.sortIndex = other.sortIndex;
            this.defaultAccentCount = other.defaultAccentCount;
            this.accentBaseColor = other.accentBaseColor;
            this.currentAccentId = other.currentAccentId;
            this.prevAccentId = other.prevAccentId;
            this.themeAccentsMap = other.themeAccentsMap;
            this.themeAccents = other.themeAccents;
            this.accentsByThemeId = other.accentsByThemeId;
            this.lastAccentId = other.lastAccentId;
            this.loadingThemeWallpaperName = other.loadingThemeWallpaperName;
            this.newPathToWallpaper = other.newPathToWallpaper;
            this.overrideWallpaper = other.overrideWallpaper;
        }

        /* access modifiers changed from: package-private */
        public JSONObject getSaveJson() {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", this.name);
                jsonObject.put("path", this.pathToFile);
                jsonObject.put("account", this.account);
                if (this.info != null) {
                    SerializedData data = new SerializedData(this.info.getObjectSize());
                    this.info.serializeToStream(data);
                    jsonObject.put("info", Utilities.bytesToHex(data.toByteArray()));
                }
                jsonObject.put("loaded", this.loaded);
                return jsonObject;
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
            int N = this.themeAccents.size();
            for (int a = 0; a < N; a++) {
                ThemeAccent accent = this.themeAccents.get(a);
                loadOverrideWallpaper(sharedPreferences, accent, this.name + "_" + accent.id + "_owp");
            }
        }

        private void loadOverrideWallpaper(SharedPreferences sharedPreferences, ThemeAccent accent, String key) {
            try {
                String json = sharedPreferences.getString(key, (String) null);
                if (!TextUtils.isEmpty(json)) {
                    JSONObject object = new JSONObject(json);
                    OverrideWallpaperInfo wallpaperInfo = new OverrideWallpaperInfo();
                    wallpaperInfo.fileName = object.getString("wall");
                    wallpaperInfo.originalFileName = object.getString("owall");
                    wallpaperInfo.color = object.getInt("pColor");
                    wallpaperInfo.gradientColor1 = object.getInt("pGrColor");
                    wallpaperInfo.gradientColor2 = object.optInt("pGrColor2");
                    wallpaperInfo.gradientColor3 = object.optInt("pGrColor3");
                    wallpaperInfo.rotation = object.getInt("pGrAngle");
                    wallpaperInfo.slug = object.getString("wallSlug");
                    wallpaperInfo.isBlurred = object.getBoolean("wBlur");
                    wallpaperInfo.isMotion = object.getBoolean("wMotion");
                    wallpaperInfo.intensity = (float) object.getDouble("pIntensity");
                    wallpaperInfo.parentTheme = this;
                    wallpaperInfo.parentAccent = accent;
                    if (accent != null) {
                        accent.overrideWallpaper = wallpaperInfo;
                    } else {
                        this.overrideWallpaper = wallpaperInfo;
                    }
                    if (object.has("wallId") && object.getLong("wallId") == 1000001) {
                        wallpaperInfo.slug = "d";
                    }
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }

        public void setOverrideWallpaper(OverrideWallpaperInfo info2) {
            if (this.overrideWallpaper != info2) {
                ThemeAccent accent = getAccent(false);
                OverrideWallpaperInfo overrideWallpaperInfo = this.overrideWallpaper;
                if (overrideWallpaperInfo != null) {
                    overrideWallpaperInfo.delete();
                }
                if (info2 != null) {
                    info2.parentAccent = accent;
                    info2.parentTheme = this;
                    info2.save();
                }
                this.overrideWallpaper = info2;
                if (accent != null) {
                    accent.overrideWallpaper = info2;
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
            TLRPC.TL_theme tL_theme = this.info;
            return tL_theme != null ? tL_theme.title : this.name;
        }

        public void setCurrentAccentId(int id) {
            this.currentAccentId = id;
            ThemeAccent accent = getAccent(false);
            if (accent != null) {
                this.overrideWallpaper = accent.overrideWallpaper;
            }
        }

        public String generateWallpaperName(ThemeAccent accent, boolean original) {
            String str;
            String str2;
            if (accent == null) {
                accent = getAccent(false);
            }
            if (accent != null) {
                StringBuilder sb = new StringBuilder();
                if (original) {
                    str2 = this.name + "_" + accent.id + "_wp_o";
                } else {
                    str2 = this.name + "_" + accent.id + "_wp";
                }
                sb.append(str2);
                sb.append(Utilities.random.nextInt());
                sb.append(".jpg");
                return sb.toString();
            }
            StringBuilder sb2 = new StringBuilder();
            if (original) {
                str = this.name + "_wp_o";
            } else {
                str = this.name + "_wp";
            }
            sb2.append(str);
            sb2.append(Utilities.random.nextInt());
            sb2.append(".jpg");
            return sb2.toString();
        }

        public void setPreviewInColor(int color) {
            this.previewInColor = color;
        }

        public void setPreviewOutColor(int color) {
            this.previewOutColor = color;
        }

        public void setPreviewBackgroundColor(int color) {
            this.previewBackgroundColor = color;
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
            if (this.currentAccentId == Theme.DEFALT_THEME_ACCENT_ID) {
                return true;
            }
            ThemeAccent defaultAccent = this.themeAccentsMap.get(Theme.DEFALT_THEME_ACCENT_ID);
            ThemeAccent accent = this.themeAccentsMap.get(this.currentAccentId);
            if (defaultAccent != null && accent != null && defaultAccent.myMessagesAccentColor == accent.myMessagesAccentColor && defaultAccent.myMessagesGradientAccentColor1 == accent.myMessagesGradientAccentColor1 && defaultAccent.myMessagesGradientAccentColor2 == accent.myMessagesGradientAccentColor2 && defaultAccent.myMessagesGradientAccentColor3 == accent.myMessagesGradientAccentColor3 && defaultAccent.myMessagesAnimated == accent.myMessagesAnimated) {
                return true;
            }
            return false;
        }

        /* access modifiers changed from: private */
        public boolean isDefaultMyMessages() {
            if (!this.firstAccentIsDefault) {
                return false;
            }
            if (this.currentAccentId == Theme.DEFALT_THEME_ACCENT_ID) {
                return true;
            }
            ThemeAccent defaultAccent = this.themeAccentsMap.get(Theme.DEFALT_THEME_ACCENT_ID);
            ThemeAccent accent = this.themeAccentsMap.get(this.currentAccentId);
            if (defaultAccent != null && accent != null && defaultAccent.accentColor2 == accent.accentColor2 && defaultAccent.myMessagesAccentColor == accent.myMessagesAccentColor && defaultAccent.myMessagesGradientAccentColor1 == accent.myMessagesGradientAccentColor1 && defaultAccent.myMessagesGradientAccentColor2 == accent.myMessagesGradientAccentColor2 && defaultAccent.myMessagesGradientAccentColor3 == accent.myMessagesGradientAccentColor3 && defaultAccent.myMessagesAnimated == accent.myMessagesAnimated) {
                return true;
            }
            return false;
        }

        /* access modifiers changed from: private */
        public boolean isDefaultMainAccent() {
            if (!this.firstAccentIsDefault) {
                return false;
            }
            if (this.currentAccentId == Theme.DEFALT_THEME_ACCENT_ID) {
                return true;
            }
            ThemeAccent defaultAccent = this.themeAccentsMap.get(Theme.DEFALT_THEME_ACCENT_ID);
            ThemeAccent accent = this.themeAccentsMap.get(this.currentAccentId);
            if (accent == null || defaultAccent == null || defaultAccent.accentColor != accent.accentColor) {
                return false;
            }
            return true;
        }

        public boolean hasAccentColors() {
            return this.defaultAccentCount != 0;
        }

        public boolean isDark() {
            int i = this.isDark;
            if (i == -1) {
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
            } else if (i == 1) {
                return true;
            } else {
                return false;
            }
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

        static ThemeInfo createWithJson(JSONObject object) {
            if (object == null) {
                return null;
            }
            try {
                ThemeInfo themeInfo = new ThemeInfo();
                themeInfo.name = object.getString("name");
                themeInfo.pathToFile = object.getString("path");
                if (object.has("account")) {
                    themeInfo.account = object.getInt("account");
                }
                if (object.has("info")) {
                    try {
                        SerializedData serializedData = new SerializedData(Utilities.hexToBytes(object.getString("info")));
                        themeInfo.info = TLRPC.Theme.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
                if (object.has("loaded")) {
                    themeInfo.loaded = object.getBoolean("loaded");
                }
                return themeInfo;
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
                return null;
            }
        }

        static ThemeInfo createWithString(String string) {
            if (TextUtils.isEmpty(string)) {
                return null;
            }
            String[] args = string.split("\\|");
            if (args.length != 2) {
                return null;
            }
            ThemeInfo themeInfo = new ThemeInfo();
            themeInfo.name = args[0];
            themeInfo.pathToFile = args[1];
            return themeInfo;
        }

        private void setAccentColorOptions(int[] options) {
            setAccentColorOptions(options, (int[]) null, (int[]) null, (int[]) null, (int[]) null, (int[]) null, (int[]) null, (int[]) null, (String[]) null, (int[]) null, (int[]) null);
        }

        /* access modifiers changed from: private */
        public void setAccentColorOptions(int[] accent, int[] myMessages, int[] myMessagesGradient, int[] background, int[] backgroundGradient1, int[] backgroundGradient2, int[] backgroundGradient3, int[] ids, String[] patternSlugs, int[] patternRotations, int[] patternIntensities) {
            int[] iArr = accent;
            this.defaultAccentCount = iArr.length;
            this.themeAccents = new ArrayList<>();
            this.themeAccentsMap = new SparseArray<>();
            this.accentsByThemeId = new LongSparseArray<>();
            for (int a = 0; a < iArr.length; a++) {
                ThemeAccent themeAccent = new ThemeAccent();
                themeAccent.id = ids != null ? ids[a] : a;
                if (Theme.isHome(themeAccent)) {
                    themeAccent.isDefault = true;
                }
                themeAccent.accentColor = iArr[a];
                themeAccent.parentTheme = this;
                if (myMessages != null) {
                    themeAccent.myMessagesAccentColor = myMessages[a];
                }
                if (myMessagesGradient != null) {
                    themeAccent.myMessagesGradientAccentColor1 = myMessagesGradient[a];
                }
                if (background != null) {
                    themeAccent.backgroundOverrideColor = (long) background[a];
                    if (!this.firstAccentIsDefault || themeAccent.id != Theme.DEFALT_THEME_ACCENT_ID) {
                        themeAccent.backgroundOverrideColor = (long) background[a];
                    } else {
                        themeAccent.backgroundOverrideColor = 4294967296L;
                    }
                }
                if (backgroundGradient1 != null) {
                    if (!this.firstAccentIsDefault || themeAccent.id != Theme.DEFALT_THEME_ACCENT_ID) {
                        themeAccent.backgroundGradientOverrideColor1 = (long) backgroundGradient1[a];
                    } else {
                        themeAccent.backgroundGradientOverrideColor1 = 4294967296L;
                    }
                }
                if (backgroundGradient2 != null) {
                    if (!this.firstAccentIsDefault || themeAccent.id != Theme.DEFALT_THEME_ACCENT_ID) {
                        themeAccent.backgroundGradientOverrideColor2 = (long) backgroundGradient2[a];
                    } else {
                        themeAccent.backgroundGradientOverrideColor2 = 4294967296L;
                    }
                }
                if (backgroundGradient3 != null) {
                    if (!this.firstAccentIsDefault || themeAccent.id != Theme.DEFALT_THEME_ACCENT_ID) {
                        themeAccent.backgroundGradientOverrideColor3 = (long) backgroundGradient3[a];
                    } else {
                        themeAccent.backgroundGradientOverrideColor3 = 4294967296L;
                    }
                }
                if (patternSlugs != null) {
                    themeAccent.patternIntensity = ((float) patternIntensities[a]) / 100.0f;
                    themeAccent.backgroundRotation = patternRotations[a];
                    themeAccent.patternSlug = patternSlugs[a];
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
            FileLoader.getInstance(this.account).loadFile(this.info.document, this.info, 1, 1);
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

        /* JADX WARNING: Removed duplicated region for block: B:54:0x0120  */
        /* JADX WARNING: Removed duplicated region for block: B:70:0x0162  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public static boolean accentEquals(org.telegram.ui.ActionBar.Theme.ThemeAccent r21, org.telegram.tgnet.TLRPC.ThemeSettings r22) {
            /*
                r0 = r21
                r1 = r22
                java.util.ArrayList<java.lang.Integer> r2 = r1.message_colors
                int r2 = r2.size()
                r3 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
                r4 = 0
                if (r2 <= 0) goto L_0x001d
                java.util.ArrayList<java.lang.Integer> r2 = r1.message_colors
                java.lang.Object r2 = r2.get(r4)
                java.lang.Integer r2 = (java.lang.Integer) r2
                int r2 = r2.intValue()
                r2 = r2 | r3
                goto L_0x001e
            L_0x001d:
                r2 = 0
            L_0x001e:
                java.util.ArrayList<java.lang.Integer> r5 = r1.message_colors
                int r5 = r5.size()
                r6 = 1
                if (r5 <= r6) goto L_0x0035
                java.util.ArrayList<java.lang.Integer> r5 = r1.message_colors
                java.lang.Object r5 = r5.get(r6)
                java.lang.Integer r5 = (java.lang.Integer) r5
                int r5 = r5.intValue()
                r5 = r5 | r3
                goto L_0x0036
            L_0x0035:
                r5 = 0
            L_0x0036:
                if (r2 != r5) goto L_0x0039
                r5 = 0
            L_0x0039:
                java.util.ArrayList<java.lang.Integer> r7 = r1.message_colors
                int r7 = r7.size()
                r8 = 2
                if (r7 <= r8) goto L_0x0050
                java.util.ArrayList<java.lang.Integer> r7 = r1.message_colors
                java.lang.Object r7 = r7.get(r8)
                java.lang.Integer r7 = (java.lang.Integer) r7
                int r7 = r7.intValue()
                r7 = r7 | r3
                goto L_0x0051
            L_0x0050:
                r7 = 0
            L_0x0051:
                java.util.ArrayList<java.lang.Integer> r8 = r1.message_colors
                int r8 = r8.size()
                r9 = 3
                if (r8 <= r9) goto L_0x0068
                java.util.ArrayList<java.lang.Integer> r8 = r1.message_colors
                java.lang.Object r8 = r8.get(r9)
                java.lang.Integer r8 = (java.lang.Integer) r8
                int r8 = r8.intValue()
                r3 = r3 | r8
                goto L_0x0069
            L_0x0068:
                r3 = 0
            L_0x0069:
                r8 = 0
                r9 = 0
                r11 = 0
                r13 = 0
                r15 = 0
                r16 = 0
                r17 = 0
                org.telegram.tgnet.TLRPC$WallPaper r6 = r1.wallpaper
                if (r6 == 0) goto L_0x00fa
                org.telegram.tgnet.TLRPC$WallPaper r6 = r1.wallpaper
                org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r6.settings
                if (r6 == 0) goto L_0x00fa
                org.telegram.tgnet.TLRPC$WallPaper r6 = r1.wallpaper
                org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r6.settings
                int r6 = r6.background_color
                int r8 = org.telegram.ui.ActionBar.Theme.getWallpaperColor(r6)
                org.telegram.tgnet.TLRPC$WallPaper r6 = r1.wallpaper
                org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r6.settings
                int r6 = r6.second_background_color
                if (r6 != 0) goto L_0x0097
                r9 = 4294967296(0xNUM, double:2.121995791E-314)
                goto L_0x00a2
            L_0x0097:
                org.telegram.tgnet.TLRPC$WallPaper r6 = r1.wallpaper
                org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r6.settings
                int r6 = r6.second_background_color
                int r6 = org.telegram.ui.ActionBar.Theme.getWallpaperColor(r6)
                long r9 = (long) r6
            L_0x00a2:
                org.telegram.tgnet.TLRPC$WallPaper r6 = r1.wallpaper
                org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r6.settings
                int r6 = r6.third_background_color
                if (r6 != 0) goto L_0x00b0
                r11 = 4294967296(0xNUM, double:2.121995791E-314)
                goto L_0x00bb
            L_0x00b0:
                org.telegram.tgnet.TLRPC$WallPaper r6 = r1.wallpaper
                org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r6.settings
                int r6 = r6.third_background_color
                int r6 = org.telegram.ui.ActionBar.Theme.getWallpaperColor(r6)
                long r11 = (long) r6
            L_0x00bb:
                org.telegram.tgnet.TLRPC$WallPaper r6 = r1.wallpaper
                org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r6.settings
                int r6 = r6.fourth_background_color
                if (r6 != 0) goto L_0x00c9
                r13 = 4294967296(0xNUM, double:2.121995791E-314)
                goto L_0x00d4
            L_0x00c9:
                org.telegram.tgnet.TLRPC$WallPaper r6 = r1.wallpaper
                org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r6.settings
                int r6 = r6.fourth_background_color
                int r6 = org.telegram.ui.ActionBar.Theme.getWallpaperColor(r6)
                long r13 = (long) r6
            L_0x00d4:
                org.telegram.tgnet.TLRPC$WallPaper r6 = r1.wallpaper
                org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r6.settings
                int r6 = r6.rotation
                int r15 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r6, r4)
                org.telegram.tgnet.TLRPC$WallPaper r6 = r1.wallpaper
                boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_wallPaperNoFile
                if (r6 != 0) goto L_0x00fa
                org.telegram.tgnet.TLRPC$WallPaper r6 = r1.wallpaper
                boolean r6 = r6.pattern
                if (r6 == 0) goto L_0x00fa
                org.telegram.tgnet.TLRPC$WallPaper r6 = r1.wallpaper
                java.lang.String r6 = r6.slug
                org.telegram.tgnet.TLRPC$WallPaper r4 = r1.wallpaper
                org.telegram.tgnet.TLRPC$WallPaperSettings r4 = r4.settings
                int r4 = r4.intensity
                float r4 = (float) r4
                r16 = 1120403456(0x42CLASSNAME, float:100.0)
                float r17 = r4 / r16
                goto L_0x00fc
            L_0x00fa:
                r6 = r16
            L_0x00fc:
                int r4 = r1.accent_color
                r16 = r6
                int r6 = r0.accentColor
                if (r4 != r6) goto L_0x0162
                int r4 = r1.outbox_accent_color
                int r6 = r0.accentColor2
                if (r4 != r6) goto L_0x0162
                int r4 = r0.myMessagesAccentColor
                if (r2 != r4) goto L_0x0162
                int r4 = r0.myMessagesGradientAccentColor1
                if (r5 != r4) goto L_0x0162
                int r4 = r0.myMessagesGradientAccentColor2
                if (r7 != r4) goto L_0x0162
                int r4 = r0.myMessagesGradientAccentColor3
                if (r3 != r4) goto L_0x0162
                boolean r4 = r1.message_colors_animated
                boolean r6 = r0.myMessagesAnimated
                if (r4 != r6) goto L_0x0162
                r4 = r2
                long r1 = (long) r8
                r6 = r3
                r18 = r4
                long r3 = r0.backgroundOverrideColor
                int r19 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
                if (r19 != 0) goto L_0x015f
                long r1 = r0.backgroundGradientOverrideColor1
                int r3 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
                if (r3 != 0) goto L_0x015f
                long r1 = r0.backgroundGradientOverrideColor2
                int r3 = (r11 > r1 ? 1 : (r11 == r1 ? 0 : -1))
                if (r3 != 0) goto L_0x015f
                long r1 = r0.backgroundGradientOverrideColor3
                int r3 = (r13 > r1 ? 1 : (r13 == r1 ? 0 : -1))
                if (r3 != 0) goto L_0x015f
                int r1 = r0.backgroundRotation
                if (r15 != r1) goto L_0x015f
                java.lang.String r1 = r0.patternSlug
                r2 = r16
                boolean r1 = android.text.TextUtils.equals(r2, r1)
                if (r1 == 0) goto L_0x0167
                float r1 = r0.patternIntensity
                float r1 = r17 - r1
                float r1 = java.lang.Math.abs(r1)
                double r3 = (double) r1
                r19 = 4562254508917369340(0x3var_dd2f1a9fc, double:0.001)
                int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
                if (r1 >= 0) goto L_0x0167
                r4 = 1
                goto L_0x0168
            L_0x015f:
                r2 = r16
                goto L_0x0167
            L_0x0162:
                r18 = r2
                r6 = r3
                r2 = r16
            L_0x0167:
                r4 = 0
            L_0x0168:
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.ThemeInfo.accentEquals(org.telegram.ui.ActionBar.Theme$ThemeAccent, org.telegram.tgnet.TLRPC$ThemeSettings):boolean");
        }

        public static void fillAccentValues(ThemeAccent themeAccent, TLRPC.ThemeSettings settings) {
            themeAccent.accentColor = settings.accent_color;
            themeAccent.accentColor2 = settings.outbox_accent_color;
            themeAccent.myMessagesAccentColor = settings.message_colors.size() > 0 ? settings.message_colors.get(0).intValue() | -16777216 : 0;
            themeAccent.myMessagesGradientAccentColor1 = settings.message_colors.size() > 1 ? settings.message_colors.get(1).intValue() | -16777216 : 0;
            if (themeAccent.myMessagesAccentColor == themeAccent.myMessagesGradientAccentColor1) {
                themeAccent.myMessagesGradientAccentColor1 = 0;
            }
            themeAccent.myMessagesGradientAccentColor2 = settings.message_colors.size() > 2 ? settings.message_colors.get(2).intValue() | -16777216 : 0;
            themeAccent.myMessagesGradientAccentColor3 = settings.message_colors.size() > 3 ? settings.message_colors.get(3).intValue() | -16777216 : 0;
            themeAccent.myMessagesAnimated = settings.message_colors_animated;
            if (settings.wallpaper != null && settings.wallpaper.settings != null) {
                if (settings.wallpaper.settings.background_color == 0) {
                    themeAccent.backgroundOverrideColor = 4294967296L;
                } else {
                    themeAccent.backgroundOverrideColor = (long) Theme.getWallpaperColor(settings.wallpaper.settings.background_color);
                }
                if ((settings.wallpaper.settings.flags & 16) == 0 || settings.wallpaper.settings.second_background_color != 0) {
                    themeAccent.backgroundGradientOverrideColor1 = (long) Theme.getWallpaperColor(settings.wallpaper.settings.second_background_color);
                } else {
                    themeAccent.backgroundGradientOverrideColor1 = 4294967296L;
                }
                if ((settings.wallpaper.settings.flags & 32) == 0 || settings.wallpaper.settings.third_background_color != 0) {
                    themeAccent.backgroundGradientOverrideColor2 = (long) Theme.getWallpaperColor(settings.wallpaper.settings.third_background_color);
                } else {
                    themeAccent.backgroundGradientOverrideColor2 = 4294967296L;
                }
                if ((settings.wallpaper.settings.flags & 64) == 0 || settings.wallpaper.settings.fourth_background_color != 0) {
                    themeAccent.backgroundGradientOverrideColor3 = (long) Theme.getWallpaperColor(settings.wallpaper.settings.fourth_background_color);
                } else {
                    themeAccent.backgroundGradientOverrideColor3 = 4294967296L;
                }
                themeAccent.backgroundRotation = AndroidUtilities.getWallpaperRotation(settings.wallpaper.settings.rotation, false);
                if (!(settings.wallpaper instanceof TLRPC.TL_wallPaperNoFile) && settings.wallpaper.pattern) {
                    themeAccent.patternSlug = settings.wallpaper.slug;
                    themeAccent.patternIntensity = ((float) settings.wallpaper.settings.intensity) / 100.0f;
                    themeAccent.patternMotion = settings.wallpaper.settings.motion;
                }
            }
        }

        public ThemeAccent createNewAccent(TLRPC.ThemeSettings settings) {
            ThemeAccent themeAccent = new ThemeAccent();
            fillAccentValues(themeAccent, settings);
            themeAccent.parentTheme = this;
            return themeAccent;
        }

        public ThemeAccent createNewAccent(TLRPC.TL_theme info2, int account2) {
            return createNewAccent(info2, account2, false, 0);
        }

        public ThemeAccent createNewAccent(TLRPC.TL_theme info2, int account2, boolean ignoreThemeInfoId, int settingsIndex) {
            if (info2 == null) {
                return null;
            }
            TLRPC.ThemeSettings settings = null;
            if (settingsIndex < info2.settings.size()) {
                settings = info2.settings.get(settingsIndex);
            }
            if (ignoreThemeInfoId) {
                ThemeAccent themeAccent = this.chatAccentsByThemeId.get(info2.id);
                if (themeAccent != null) {
                    return themeAccent;
                }
                int id = this.lastChatThemeId + 1;
                this.lastChatThemeId = id;
                ThemeAccent themeAccent2 = createNewAccent(settings);
                themeAccent2.id = id;
                themeAccent2.info = info2;
                themeAccent2.account = account2;
                this.chatAccentsByThemeId.put((long) id, themeAccent2);
                return themeAccent2;
            }
            ThemeAccent themeAccent3 = this.accentsByThemeId.get(info2.id);
            if (themeAccent3 != null) {
                return themeAccent3;
            }
            int id2 = this.lastAccentId + 1;
            this.lastAccentId = id2;
            ThemeAccent themeAccent4 = createNewAccent(settings);
            themeAccent4.id = id2;
            themeAccent4.info = info2;
            themeAccent4.account = account2;
            this.themeAccentsMap.put(id2, themeAccent4);
            this.themeAccents.add(0, themeAccent4);
            Theme.sortAccents(this);
            this.accentsByThemeId.put(info2.id, themeAccent4);
            return themeAccent4;
        }

        public ThemeAccent getAccent(boolean createNew) {
            if (this.themeAccents == null) {
                return null;
            }
            ThemeAccent accent = this.themeAccentsMap.get(this.currentAccentId);
            if (!createNew) {
                return accent;
            }
            int id = this.lastAccentId + 1;
            this.lastAccentId = id;
            ThemeAccent themeAccent = new ThemeAccent();
            themeAccent.accentColor = accent.accentColor;
            themeAccent.accentColor2 = accent.accentColor2;
            themeAccent.myMessagesAccentColor = accent.myMessagesAccentColor;
            themeAccent.myMessagesGradientAccentColor1 = accent.myMessagesGradientAccentColor1;
            themeAccent.myMessagesGradientAccentColor2 = accent.myMessagesGradientAccentColor2;
            themeAccent.myMessagesGradientAccentColor3 = accent.myMessagesGradientAccentColor3;
            themeAccent.myMessagesAnimated = accent.myMessagesAnimated;
            themeAccent.backgroundOverrideColor = accent.backgroundOverrideColor;
            themeAccent.backgroundGradientOverrideColor1 = accent.backgroundGradientOverrideColor1;
            themeAccent.backgroundGradientOverrideColor2 = accent.backgroundGradientOverrideColor2;
            themeAccent.backgroundGradientOverrideColor3 = accent.backgroundGradientOverrideColor3;
            themeAccent.backgroundRotation = accent.backgroundRotation;
            themeAccent.patternSlug = accent.patternSlug;
            themeAccent.patternIntensity = accent.patternIntensity;
            themeAccent.patternMotion = accent.patternMotion;
            themeAccent.parentTheme = this;
            if (this.overrideWallpaper != null) {
                themeAccent.overrideWallpaper = new OverrideWallpaperInfo(this.overrideWallpaper, this, themeAccent);
            }
            this.prevAccentId = this.currentAccentId;
            themeAccent.id = id;
            this.currentAccentId = id;
            this.overrideWallpaper = themeAccent.overrideWallpaper;
            this.themeAccentsMap.put(id, themeAccent);
            this.themeAccents.add(0, themeAccent);
            Theme.sortAccents(this);
            return themeAccent;
        }

        public int getAccentColor(int id) {
            ThemeAccent accent = this.themeAccentsMap.get(id);
            if (accent != null) {
                return accent.accentColor;
            }
            return 0;
        }

        public boolean createBackground(File file, String toPath) {
            int patternColor;
            try {
                Bitmap bitmap = ThemesHorizontalListCell.getScaledBitmap((float) AndroidUtilities.dp(640.0f), (float) AndroidUtilities.dp(360.0f), file.getAbsolutePath(), (String) null, 0);
                if (!(bitmap == null || this.patternBgColor == 0)) {
                    Bitmap finalBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
                    Canvas canvas = new Canvas(finalBitmap);
                    int i = this.patternBgGradientColor2;
                    if (i != 0) {
                        patternColor = MotionBackgroundDrawable.getPatternColor(this.patternBgColor, this.patternBgGradientColor1, i, this.patternBgGradientColor3);
                    } else {
                        int patternColor2 = this.patternBgGradientColor1;
                        if (patternColor2 != 0) {
                            patternColor = AndroidUtilities.getAverageColor(this.patternBgColor, patternColor2);
                            GradientDrawable gradientDrawable = new GradientDrawable(BackgroundGradientDrawable.getGradientOrientation(this.patternBgGradientRotation), new int[]{this.patternBgColor, this.patternBgGradientColor1});
                            gradientDrawable.setBounds(0, 0, finalBitmap.getWidth(), finalBitmap.getHeight());
                            gradientDrawable.draw(canvas);
                        } else {
                            patternColor = AndroidUtilities.getPatternColor(this.patternBgColor);
                            canvas.drawColor(this.patternBgColor);
                        }
                    }
                    Paint paint = new Paint(2);
                    paint.setColorFilter(new PorterDuffColorFilter(patternColor, PorterDuff.Mode.SRC_IN));
                    paint.setAlpha((int) ((((float) this.patternIntensity) / 100.0f) * 255.0f));
                    canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
                    bitmap = finalBitmap;
                    canvas.setBitmap((Bitmap) null);
                }
                if (this.isBlured) {
                    bitmap = Utilities.blurWallpaper(bitmap);
                }
                FileOutputStream stream = new FileOutputStream(toPath);
                bitmap.compress(this.patternBgGradientColor2 != 0 ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, 87, stream);
                stream.close();
                return true;
            } catch (Throwable e) {
                FileLog.e(e);
                return false;
            }
        }

        public void didReceivedNotification(int id, int account2, Object... args) {
            if (id == NotificationCenter.fileLoaded || id == NotificationCenter.fileLoadFailed) {
                String location = args[0];
                TLRPC.TL_theme tL_theme = this.info;
                if (tL_theme != null && tL_theme.document != null) {
                    if (location.equals(this.loadingThemeWallpaperName)) {
                        this.loadingThemeWallpaperName = null;
                        Utilities.globalQueue.postRunnable(new Theme$ThemeInfo$$ExternalSyntheticLambda1(this, args[1]));
                    } else if (location.equals(FileLoader.getAttachFileName(this.info.document))) {
                        removeObservers();
                        if (id == NotificationCenter.fileLoaded) {
                            ThemeInfo themeInfo = Theme.fillThemeValues(new File(this.pathToFile), this.info.title, this.info);
                            if (themeInfo == null || themeInfo.pathToWallpaper == null || new File(themeInfo.pathToWallpaper).exists()) {
                                onFinishLoadingRemoteTheme();
                                return;
                            }
                            this.patternBgColor = themeInfo.patternBgColor;
                            this.patternBgGradientColor1 = themeInfo.patternBgGradientColor1;
                            this.patternBgGradientColor2 = themeInfo.patternBgGradientColor2;
                            this.patternBgGradientColor3 = themeInfo.patternBgGradientColor3;
                            this.patternBgGradientRotation = themeInfo.patternBgGradientRotation;
                            this.isBlured = themeInfo.isBlured;
                            this.patternIntensity = themeInfo.patternIntensity;
                            this.newPathToWallpaper = themeInfo.pathToWallpaper;
                            TLRPC.TL_account_getWallPaper req = new TLRPC.TL_account_getWallPaper();
                            TLRPC.TL_inputWallPaperSlug inputWallPaperSlug = new TLRPC.TL_inputWallPaperSlug();
                            inputWallPaperSlug.slug = themeInfo.slug;
                            req.wallpaper = inputWallPaperSlug;
                            ConnectionsManager.getInstance(themeInfo.account).sendRequest(req, new Theme$ThemeInfo$$ExternalSyntheticLambda3(this, themeInfo));
                        }
                    }
                }
            }
        }

        /* renamed from: lambda$didReceivedNotification$0$org-telegram-ui-ActionBar-Theme$ThemeInfo  reason: not valid java name */
        public /* synthetic */ void m1336xa93038c(File file) {
            createBackground(file, this.newPathToWallpaper);
            AndroidUtilities.runOnUIThread(new Theme$ThemeInfo$$ExternalSyntheticLambda0(this));
        }

        /* renamed from: lambda$didReceivedNotification$2$org-telegram-ui-ActionBar-Theme$ThemeInfo  reason: not valid java name */
        public /* synthetic */ void m1338xd515e10e(ThemeInfo themeInfo, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Theme$ThemeInfo$$ExternalSyntheticLambda2(this, response, themeInfo));
        }

        /* renamed from: lambda$didReceivedNotification$1$org-telegram-ui-ActionBar-Theme$ThemeInfo  reason: not valid java name */
        public /* synthetic */ void m1337xefd4724d(TLObject response, ThemeInfo themeInfo) {
            if (response instanceof TLRPC.TL_wallPaper) {
                TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) response;
                this.loadingThemeWallpaperName = FileLoader.getAttachFileName(wallPaper.document);
                addObservers();
                FileLoader.getInstance(themeInfo.account).loadFile(wallPaper.document, wallPaper, 1, 1);
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
            public static int $default$getColorOrDefault(ResourcesProvider _this, String key) {
                Integer color = _this.getColor(key);
                return color != null ? color.intValue() : Theme.getColor(key);
            }

            public static Integer $default$getCurrentColor(ResourcesProvider _this, String key) {
                return _this.getColor(key);
            }

            public static void $default$setAnimatedColor(ResourcesProvider _this, String key, int color) {
            }

            public static Drawable $default$getDrawable(ResourcesProvider _this, String drawableKey) {
                return null;
            }

            public static Paint $default$getPaint(ResourcesProvider _this, String paintKey) {
                return null;
            }

            public static boolean $default$hasGradientService(ResourcesProvider _this) {
                return false;
            }

            public static void $default$applyServiceShaderMatrix(ResourcesProvider _this, int w, int h, float translationX, float translationY) {
                Theme.applyServiceShaderMatrix(w, h, translationX, translationY);
            }
        }
    }

    static {
        String theme;
        int i;
        ThemeInfo themeDarkBlue;
        int remoteVersion;
        String themesString;
        ThemeInfo themeInfo;
        int i2;
        int version;
        ThemeInfo themeDarkBlue2;
        String theme2;
        boolean z;
        boolean z2;
        int version2;
        ThemeInfo t;
        Integer num;
        Integer num2;
        selectedAutoNightType = 0;
        autoNightBrighnessThreshold = 0.25f;
        autoNightDayStartTime = 1320;
        autoNightDayEndTime = 480;
        autoNightSunsetTime = 1320;
        autoNightLastSunCheckDay = -1;
        autoNightSunriseTime = 480;
        autoNightCityName = "";
        autoNightLocationLatitude = 10000.0d;
        autoNightLocationLongitude = 10000.0d;
        defaultColors.put("dialogBackground", -1);
        defaultColors.put("dialogBackgroundGray", -986896);
        defaultColors.put("dialogTextBlack", -14540254);
        defaultColors.put("dialogTextLink", -14255946);
        defaultColors.put("dialogLinkSelection", NUM);
        defaultColors.put("dialogTextRed", -3319206);
        defaultColors.put("dialogTextRed2", -2213318);
        defaultColors.put("dialogTextBlue", -13660983);
        defaultColors.put("dialogTextBlue2", -12937771);
        defaultColors.put("dialogTextBlue3", -12664327);
        defaultColors.put("dialogTextBlue4", -15095832);
        defaultColors.put("dialogTextGray", -13333567);
        defaultColors.put("dialogTextGray2", -9079435);
        defaultColors.put("dialogTextGray3", -6710887);
        defaultColors.put("dialogTextGray4", -5000269);
        defaultColors.put("dialogTextHint", -6842473);
        defaultColors.put("dialogIcon", -9999504);
        defaultColors.put("dialogRedIcon", -2011827);
        defaultColors.put("dialogGrayLine", -2960686);
        defaultColors.put("dialogTopBackground", -9456923);
        defaultColors.put("dialogInputField", -2368549);
        defaultColors.put("dialogInputFieldActivated", -13129232);
        defaultColors.put("dialogCheckboxSquareBackground", -12345121);
        defaultColors.put("dialogCheckboxSquareCheck", -1);
        defaultColors.put("dialogCheckboxSquareUnchecked", -9211021);
        defaultColors.put("dialogCheckboxSquareDisabled", -5197648);
        defaultColors.put("dialogRadioBackground", -5000269);
        defaultColors.put("dialogRadioBackgroundChecked", -13129232);
        defaultColors.put("dialogProgressCircle", -14115349);
        defaultColors.put("dialogLineProgress", -11371101);
        defaultColors.put("dialogLineProgressBackground", -2368549);
        defaultColors.put("dialogButton", -11955764);
        defaultColors.put("dialogButtonSelector", NUM);
        defaultColors.put("dialogScrollGlow", -657673);
        defaultColors.put("dialogRoundCheckBox", -11750155);
        defaultColors.put("dialogRoundCheckBoxCheck", -1);
        defaultColors.put("dialogBadgeBackground", -12664327);
        defaultColors.put("dialogBadgeText", -1);
        defaultColors.put("dialogCameraIcon", -1);
        defaultColors.put("dialog_inlineProgressBackground", -NUM);
        defaultColors.put("dialog_inlineProgress", -9735304);
        defaultColors.put("dialogSearchBackground", -854795);
        defaultColors.put("dialogSearchHint", -6774617);
        defaultColors.put("dialogSearchIcon", -6182737);
        defaultColors.put("dialogSearchText", -14540254);
        defaultColors.put("dialogFloatingButton", -11750155);
        defaultColors.put("dialogFloatingButtonPressed", NUM);
        defaultColors.put("dialogFloatingIcon", -1);
        defaultColors.put("dialogShadowLine", NUM);
        defaultColors.put("dialogEmptyImage", -6314840);
        defaultColors.put("dialogEmptyText", -7565164);
        defaultColors.put("dialogSwipeRemove", -1743531);
        defaultColors.put("windowBackgroundWhite", -1);
        defaultColors.put("windowBackgroundUnchecked", -6445135);
        defaultColors.put("windowBackgroundChecked", -11034919);
        defaultColors.put("windowBackgroundCheckText", -1);
        defaultColors.put("progressCircle", -14904349);
        defaultColors.put("windowBackgroundWhiteGrayIcon", -8288629);
        defaultColors.put("windowBackgroundWhiteBlueText", -12545331);
        defaultColors.put("windowBackgroundWhiteBlueText2", -12937771);
        defaultColors.put("windowBackgroundWhiteBlueText3", -14255946);
        defaultColors.put("windowBackgroundWhiteBlueText4", -14904349);
        defaultColors.put("windowBackgroundWhiteBlueText5", -11759926);
        defaultColors.put("windowBackgroundWhiteBlueText6", -12940081);
        defaultColors.put("windowBackgroundWhiteBlueText7", -13141330);
        defaultColors.put("windowBackgroundWhiteBlueButton", -14776109);
        defaultColors.put("windowBackgroundWhiteBlueIcon", -13132315);
        defaultColors.put("windowBackgroundWhiteGreenText", -14248148);
        defaultColors.put("windowBackgroundWhiteGreenText2", -13129704);
        defaultColors.put("windowBackgroundWhiteRedText", -3319206);
        defaultColors.put("windowBackgroundWhiteRedText2", -2404015);
        defaultColors.put("windowBackgroundWhiteRedText3", -2995895);
        defaultColors.put("windowBackgroundWhiteRedText4", -3198928);
        defaultColors.put("windowBackgroundWhiteRedText5", -1230535);
        defaultColors.put("windowBackgroundWhiteRedText6", -39322);
        defaultColors.put("windowBackgroundWhiteGrayText", -8156010);
        defaultColors.put("windowBackgroundWhiteGrayText2", -8223094);
        defaultColors.put("windowBackgroundWhiteGrayText3", -6710887);
        defaultColors.put("windowBackgroundWhiteGrayText4", -8355712);
        defaultColors.put("windowBackgroundWhiteGrayText5", -6052957);
        defaultColors.put("windowBackgroundWhiteGrayText6", -9079435);
        defaultColors.put("windowBackgroundWhiteGrayText7", -3750202);
        defaultColors.put("windowBackgroundWhiteGrayText8", -9605774);
        defaultColors.put("windowBackgroundWhiteGrayLine", -2368549);
        defaultColors.put("windowBackgroundWhiteBlackText", -14540254);
        defaultColors.put("windowBackgroundWhiteHintText", -5723992);
        defaultColors.put("windowBackgroundWhiteValueText", -12937771);
        defaultColors.put("windowBackgroundWhiteLinkText", -14255946);
        defaultColors.put("windowBackgroundWhiteLinkSelection", NUM);
        defaultColors.put("windowBackgroundWhiteBlueHeader", -12937771);
        defaultColors.put("windowBackgroundWhiteInputField", -2368549);
        defaultColors.put("windowBackgroundWhiteInputFieldActivated", -13129232);
        defaultColors.put("switchTrack", -5196358);
        defaultColors.put("switchTrackChecked", -11358743);
        defaultColors.put("switchTrackBlue", -8221031);
        defaultColors.put("switchTrackBlueChecked", -12810041);
        defaultColors.put("switchTrackBlueThumb", -1);
        defaultColors.put("switchTrackBlueThumbChecked", -1);
        defaultColors.put("switchTrackBlueSelector", NUM);
        defaultColors.put("switchTrackBlueSelectorChecked", NUM);
        defaultColors.put("switch2Track", -688514);
        defaultColors.put("switch2TrackChecked", -11358743);
        defaultColors.put("checkboxSquareBackground", -12345121);
        defaultColors.put("checkboxSquareCheck", -1);
        defaultColors.put("checkboxSquareUnchecked", -9211021);
        defaultColors.put("checkboxSquareDisabled", -5197648);
        defaultColors.put("listSelectorSDK21", NUM);
        defaultColors.put("radioBackground", -5000269);
        defaultColors.put("radioBackgroundChecked", -13129232);
        defaultColors.put("windowBackgroundGray", -986896);
        defaultColors.put("windowBackgroundGrayShadow", -16777216);
        defaultColors.put("emptyListPlaceholder", -6974059);
        defaultColors.put("divider", -2500135);
        defaultColors.put("graySection", -657931);
        defaultColors.put("key_graySectionText", -8222838);
        defaultColors.put("contextProgressInner1", -4202506);
        defaultColors.put("contextProgressOuter1", -13920542);
        defaultColors.put("contextProgressInner2", -4202506);
        defaultColors.put("contextProgressOuter2", -1);
        defaultColors.put("contextProgressInner3", -5000269);
        defaultColors.put("contextProgressOuter3", -1);
        defaultColors.put("contextProgressInner4", -3486256);
        defaultColors.put("contextProgressOuter4", -13683656);
        defaultColors.put("fastScrollActive", -11361317);
        defaultColors.put("fastScrollInactive", -3551791);
        defaultColors.put("fastScrollText", -1);
        defaultColors.put("avatar_text", -1);
        defaultColors.put("avatar_backgroundSaved", -10043398);
        defaultColors.put("avatar_backgroundArchived", -5654847);
        defaultColors.put("avatar_backgroundArchivedHidden", -10043398);
        defaultColors.put("avatar_backgroundRed", -1743531);
        defaultColors.put("avatar_backgroundOrange", -881592);
        defaultColors.put("avatar_backgroundViolet", -7436818);
        defaultColors.put("avatar_backgroundGreen", -8992691);
        defaultColors.put("avatar_backgroundCyan", -10502443);
        defaultColors.put("avatar_backgroundBlue", -11232035);
        defaultColors.put("avatar_backgroundPink", -887654);
        defaultColors.put("avatar_backgroundInProfileBlue", -11500111);
        defaultColors.put("avatar_backgroundActionBarBlue", -10907718);
        defaultColors.put("avatar_subtitleInProfileBlue", -2626822);
        defaultColors.put("avatar_actionBarSelectorBlue", -11959891);
        defaultColors.put("avatar_actionBarIconBlue", -1);
        defaultColors.put("avatar_nameInMessageRed", -3516848);
        defaultColors.put("avatar_nameInMessageOrange", -2589911);
        defaultColors.put("avatar_nameInMessageViolet", -11627828);
        defaultColors.put("avatar_nameInMessageGreen", -11488718);
        defaultColors.put("avatar_nameInMessageCyan", -13132104);
        defaultColors.put("avatar_nameInMessageBlue", -11627828);
        defaultColors.put("avatar_nameInMessagePink", -11627828);
        defaultColors.put("actionBarDefault", -11371101);
        defaultColors.put("actionBarDefaultIcon", -1);
        defaultColors.put("actionBarActionModeDefault", -1);
        defaultColors.put("actionBarActionModeDefaultTop", NUM);
        defaultColors.put("actionBarActionModeDefaultIcon", -9999761);
        defaultColors.put("actionBarDefaultTitle", -1);
        defaultColors.put("actionBarDefaultSubtitle", -2758409);
        defaultColors.put("actionBarDefaultSelector", -12554860);
        defaultColors.put("actionBarWhiteSelector", NUM);
        defaultColors.put("actionBarDefaultSearch", -1);
        defaultColors.put("actionBarDefaultSearchPlaceholder", -NUM);
        defaultColors.put("actionBarDefaultSubmenuItem", -14540254);
        defaultColors.put("actionBarDefaultSubmenuItemIcon", -9999504);
        defaultColors.put("actionBarDefaultSubmenuBackground", -1);
        defaultColors.put("actionBarActionModeDefaultSelector", -1907998);
        defaultColors.put("actionBarTabActiveText", -1);
        defaultColors.put("actionBarTabUnactiveText", -2758409);
        defaultColors.put("actionBarTabLine", -1);
        defaultColors.put("actionBarTabSelector", -12554860);
        defaultColors.put("actionBarBrowser", -1);
        defaultColors.put("actionBarDefaultArchived", -9471353);
        defaultColors.put("actionBarDefaultArchivedSelector", -10590350);
        defaultColors.put("actionBarDefaultArchivedIcon", -1);
        defaultColors.put("actionBarDefaultArchivedTitle", -1);
        defaultColors.put("actionBarDefaultArchivedSearch", -1);
        defaultColors.put("actionBarDefaultSearchArchivedPlaceholder", -NUM);
        defaultColors.put("chats_onlineCircle", -11810020);
        defaultColors.put("chats_unreadCounter", -11613090);
        defaultColors.put("chats_unreadCounterMuted", -3749428);
        defaultColors.put("chats_unreadCounterText", -1);
        defaultColors.put("chats_archiveBackground", -10049056);
        defaultColors.put("chats_archivePinBackground", -6313293);
        defaultColors.put("chats_archiveIcon", -1);
        defaultColors.put("chats_archiveText", -1);
        defaultColors.put("chats_name", -14540254);
        defaultColors.put("chats_nameArchived", -11382190);
        defaultColors.put("chats_secretName", -16734706);
        defaultColors.put("chats_secretIcon", -15093466);
        defaultColors.put("chats_nameIcon", -14408668);
        defaultColors.put("chats_pinnedIcon", -5723992);
        defaultColors.put("chats_message", -7631473);
        defaultColors.put("chats_messageArchived", -7237231);
        defaultColors.put("chats_message_threeLines", -7434095);
        defaultColors.put("chats_draft", -2274503);
        defaultColors.put("chats_nameMessage", -12812624);
        defaultColors.put("chats_nameMessageArchived", -7631473);
        defaultColors.put("chats_nameMessage_threeLines", -12434359);
        defaultColors.put("chats_nameMessageArchived_threeLines", -10592674);
        defaultColors.put("chats_attachMessage", -12812624);
        defaultColors.put("chats_actionMessage", -12812624);
        defaultColors.put("chats_date", -6973028);
        defaultColors.put("chats_pinnedOverlay", NUM);
        defaultColors.put("chats_tabletSelectedOverlay", NUM);
        defaultColors.put("chats_sentCheck", -12146122);
        defaultColors.put("chats_sentReadCheck", -12146122);
        defaultColors.put("chats_sentClock", -9061026);
        defaultColors.put("chats_sentError", -2796974);
        defaultColors.put("chats_sentErrorIcon", -1);
        defaultColors.put("chats_verifiedBackground", -13391642);
        defaultColors.put("chats_verifiedCheck", -1);
        defaultColors.put("chats_muteIcon", -4341308);
        defaultColors.put("chats_mentionIcon", -1);
        defaultColors.put("chats_menuBackground", -1);
        defaultColors.put("chats_menuItemText", -12303292);
        defaultColors.put("chats_menuItemCheck", -10907718);
        defaultColors.put("chats_menuItemIcon", -7827048);
        defaultColors.put("chats_menuName", -1);
        defaultColors.put("chats_menuPhone", -1);
        defaultColors.put("chats_menuPhoneCats", -4004353);
        defaultColors.put("chats_menuCloud", -1);
        defaultColors.put("chats_menuCloudBackgroundCats", -12420183);
        defaultColors.put("chats_actionIcon", -1);
        defaultColors.put("chats_actionBackground", -10114592);
        defaultColors.put("chats_actionPressedBackground", -11100714);
        defaultColors.put("chats_actionUnreadIcon", -9211021);
        defaultColors.put("chats_actionUnreadBackground", -1);
        defaultColors.put("chats_actionUnreadPressedBackground", -855310);
        defaultColors.put("chats_menuTopBackgroundCats", -10907718);
        defaultColors.put("chats_archivePullDownBackground", -3749428);
        defaultColors.put("chats_archivePullDownBackgroundActive", -10049056);
        defaultColors.put("chat_attachMediaBanBackground", -12171706);
        defaultColors.put("chat_attachMediaBanText", -1);
        defaultColors.put("chat_attachCheckBoxCheck", -1);
        defaultColors.put("chat_attachCheckBoxBackground", -12995849);
        defaultColors.put("chat_attachPhotoBackground", NUM);
        defaultColors.put("chat_attachActiveTab", -13391883);
        defaultColors.put("chat_attachUnactiveTab", -7169634);
        defaultColors.put("chat_attachPermissionImage", -13421773);
        defaultColors.put("chat_attachPermissionMark", -1945520);
        defaultColors.put("chat_attachPermissionText", -9472134);
        defaultColors.put("chat_attachEmptyImage", -3355444);
        defaultColors.put("chat_attachGalleryBackground", -12214795);
        defaultColors.put("chat_attachGalleryText", -13726231);
        defaultColors.put("chat_attachGalleryIcon", -1);
        defaultColors.put("chat_attachAudioBackground", -1351584);
        defaultColors.put("chat_attachAudioText", -2209977);
        defaultColors.put("chat_attachAudioIcon", -1);
        defaultColors.put("chat_attachFileBackground", -13321743);
        defaultColors.put("chat_attachFileText", -15423260);
        defaultColors.put("chat_attachFileIcon", -1);
        defaultColors.put("chat_attachContactBackground", -868277);
        defaultColors.put("chat_attachContactText", -2121728);
        defaultColors.put("chat_attachContactIcon", -1);
        defaultColors.put("chat_attachLocationBackground", -10436011);
        defaultColors.put("chat_attachLocationText", -12801233);
        defaultColors.put("chat_attachLocationIcon", -1);
        defaultColors.put("chat_attachPollBackground", -868277);
        defaultColors.put("chat_attachPollText", -2121728);
        defaultColors.put("chat_attachPollIcon", -1);
        defaultColors.put("chat_inPollCorrectAnswer", -10436011);
        defaultColors.put("chat_outPollCorrectAnswer", -10436011);
        defaultColors.put("chat_inPollWrongAnswer", -1351584);
        defaultColors.put("chat_outPollWrongAnswer", -1351584);
        defaultColors.put("chat_status", -2758409);
        defaultColors.put("chat_inDownCall", -16725933);
        defaultColors.put("chat_inUpCall", -47032);
        defaultColors.put("chat_outUpCall", -16725933);
        defaultColors.put("chat_lockIcon", -1);
        defaultColors.put("chat_muteIcon", -5124893);
        defaultColors.put("chat_inBubble", -1);
        defaultColors.put("chat_inBubbleSelected", -1247235);
        defaultColors.put("chat_inBubbleShadow", -14862509);
        defaultColors.put("chat_outBubble", -1048610);
        defaultColors.put("chat_outBubbleGradientSelectedOverlay", NUM);
        defaultColors.put("chat_outBubbleSelected", -2492475);
        defaultColors.put("chat_outBubbleShadow", -14781172);
        defaultColors.put("chat_inMediaIcon", -1);
        defaultColors.put("chat_inMediaIconSelected", -1050370);
        defaultColors.put("chat_outMediaIcon", -1048610);
        defaultColors.put("chat_outMediaIconSelected", -1967921);
        defaultColors.put("chat_messageTextIn", -16777216);
        defaultColors.put("chat_messageTextOut", -16777216);
        defaultColors.put("chat_messageLinkIn", -14255946);
        defaultColors.put("chat_messageLinkOut", -14255946);
        defaultColors.put("chat_serviceText", -1);
        defaultColors.put("chat_serviceLink", -1);
        defaultColors.put("chat_serviceIcon", -1);
        defaultColors.put("chat_mediaTimeBackground", NUM);
        defaultColors.put("chat_outSentCheck", -10637232);
        defaultColors.put("chat_outSentCheckSelected", -10637232);
        defaultColors.put("chat_outSentCheckRead", -10637232);
        defaultColors.put("chat_outSentCheckReadSelected", -10637232);
        defaultColors.put("chat_outSentClock", -9061026);
        defaultColors.put("chat_outSentClockSelected", -9061026);
        defaultColors.put("chat_inSentClock", -6182221);
        defaultColors.put("chat_inSentClockSelected", -7094838);
        defaultColors.put("chat_mediaSentCheck", -1);
        defaultColors.put("chat_mediaSentClock", -1);
        defaultColors.put("chat_inViews", -6182221);
        defaultColors.put("chat_inViewsSelected", -7094838);
        defaultColors.put("chat_outViews", -9522601);
        defaultColors.put("chat_outViewsSelected", -9522601);
        defaultColors.put("chat_mediaViews", -1);
        defaultColors.put("chat_inMenu", -4801083);
        defaultColors.put("chat_inMenuSelected", -6766130);
        defaultColors.put("chat_outMenu", -7221634);
        defaultColors.put("chat_outMenuSelected", -7221634);
        defaultColors.put("chat_mediaMenu", -1);
        defaultColors.put("chat_outInstant", -11162801);
        defaultColors.put("chat_outInstantSelected", -12019389);
        defaultColors.put("chat_inInstant", -12940081);
        defaultColors.put("chat_inInstantSelected", -13600331);
        defaultColors.put("chat_sentError", -2411211);
        defaultColors.put("chat_sentErrorIcon", -1);
        defaultColors.put("chat_selectedBackground", NUM);
        defaultColors.put("chat_previewDurationText", -1);
        defaultColors.put("chat_previewGameText", -1);
        defaultColors.put("chat_inPreviewInstantText", -12940081);
        defaultColors.put("chat_outPreviewInstantText", -11162801);
        defaultColors.put("chat_inPreviewInstantSelectedText", -13600331);
        defaultColors.put("chat_outPreviewInstantSelectedText", -12019389);
        defaultColors.put("chat_secretTimeText", -1776928);
        defaultColors.put("chat_stickerNameText", -1);
        defaultColors.put("chat_botButtonText", -1);
        defaultColors.put("chat_botProgress", -1);
        defaultColors.put("chat_inForwardedNameText", -13072697);
        defaultColors.put("chat_outForwardedNameText", -11162801);
        defaultColors.put("chat_inPsaNameText", -10838983);
        defaultColors.put("chat_outPsaNameText", -10838983);
        defaultColors.put("chat_inViaBotNameText", -12940081);
        defaultColors.put("chat_outViaBotNameText", -11162801);
        defaultColors.put("chat_stickerViaBotNameText", -1);
        defaultColors.put("chat_inReplyLine", -10903592);
        defaultColors.put("chat_outReplyLine", -9520791);
        defaultColors.put("chat_stickerReplyLine", -1);
        defaultColors.put("chat_inReplyNameText", -12940081);
        defaultColors.put("chat_outReplyNameText", -11162801);
        defaultColors.put("chat_stickerReplyNameText", -1);
        defaultColors.put("chat_inReplyMessageText", -16777216);
        defaultColors.put("chat_outReplyMessageText", -16777216);
        defaultColors.put("chat_inReplyMediaMessageText", -6182221);
        defaultColors.put("chat_outReplyMediaMessageText", -10112933);
        defaultColors.put("chat_inReplyMediaMessageSelectedText", -7752511);
        defaultColors.put("chat_outReplyMediaMessageSelectedText", -10112933);
        defaultColors.put("chat_stickerReplyMessageText", -1);
        defaultColors.put("chat_inPreviewLine", -9390872);
        defaultColors.put("chat_outPreviewLine", -7812741);
        defaultColors.put("chat_inSiteNameText", -12940081);
        defaultColors.put("chat_outSiteNameText", -11162801);
        defaultColors.put("chat_inContactNameText", -11625772);
        defaultColors.put("chat_outContactNameText", -11162801);
        defaultColors.put("chat_inContactPhoneText", -13683656);
        defaultColors.put("chat_inContactPhoneSelectedText", -13683656);
        defaultColors.put("chat_outContactPhoneText", -13286860);
        defaultColors.put("chat_outContactPhoneSelectedText", -13286860);
        defaultColors.put("chat_mediaProgress", -1);
        defaultColors.put("chat_inAudioProgress", -1);
        defaultColors.put("chat_outAudioProgress", -1048610);
        defaultColors.put("chat_inAudioSelectedProgress", -1050370);
        defaultColors.put("chat_outAudioSelectedProgress", -1967921);
        defaultColors.put("chat_mediaTimeText", -1);
        defaultColors.put("chat_adminText", -4143413);
        defaultColors.put("chat_adminSelectedText", -7752511);
        defaultColors.put("chat_outAdminText", -9391780);
        defaultColors.put("chat_outAdminSelectedText", -9391780);
        defaultColors.put("chat_inTimeText", -6182221);
        defaultColors.put("chat_inTimeSelectedText", -7752511);
        defaultColors.put("chat_outTimeText", -9391780);
        defaultColors.put("chat_outTimeSelectedText", -9391780);
        defaultColors.put("chat_inAudioPerfomerText", -13683656);
        defaultColors.put("chat_inAudioPerfomerSelectedText", -13683656);
        defaultColors.put("chat_outAudioPerfomerText", -13286860);
        defaultColors.put("chat_outAudioPerfomerSelectedText", -13286860);
        defaultColors.put("chat_inAudioTitleText", -11625772);
        defaultColors.put("chat_outAudioTitleText", -11162801);
        defaultColors.put("chat_inAudioDurationText", -6182221);
        defaultColors.put("chat_outAudioDurationText", -10112933);
        defaultColors.put("chat_inAudioDurationSelectedText", -7752511);
        defaultColors.put("chat_outAudioDurationSelectedText", -10112933);
        defaultColors.put("chat_inAudioSeekbar", -1774864);
        defaultColors.put("chat_inAudioCacheSeekbar", NUM);
        defaultColors.put("chat_outAudioSeekbar", -4463700);
        defaultColors.put("chat_outAudioCacheSeekbar", NUM);
        defaultColors.put("chat_inAudioSeekbarSelected", -4399384);
        defaultColors.put("chat_outAudioSeekbarSelected", -5644906);
        defaultColors.put("chat_inAudioSeekbarFill", -9259544);
        defaultColors.put("chat_outAudioSeekbarFill", -8863118);
        defaultColors.put("chat_inVoiceSeekbar", -2169365);
        defaultColors.put("chat_outVoiceSeekbar", -4463700);
        defaultColors.put("chat_inVoiceSeekbarSelected", -4399384);
        defaultColors.put("chat_outVoiceSeekbarSelected", -5644906);
        defaultColors.put("chat_inVoiceSeekbarFill", -9259544);
        defaultColors.put("chat_outVoiceSeekbarFill", -8863118);
        defaultColors.put("chat_inFileProgress", -1314571);
        defaultColors.put("chat_outFileProgress", -2427453);
        defaultColors.put("chat_inFileProgressSelected", -3413258);
        defaultColors.put("chat_outFileProgressSelected", -3806041);
        defaultColors.put("chat_inFileNameText", -11625772);
        defaultColors.put("chat_outFileNameText", -11162801);
        defaultColors.put("chat_inFileInfoText", -6182221);
        defaultColors.put("chat_outFileInfoText", -10112933);
        defaultColors.put("chat_inFileInfoSelectedText", -7752511);
        defaultColors.put("chat_outFileInfoSelectedText", -10112933);
        defaultColors.put("chat_inFileBackground", -1314571);
        defaultColors.put("chat_outFileBackground", -2427453);
        defaultColors.put("chat_inFileBackgroundSelected", -3413258);
        defaultColors.put("chat_outFileBackgroundSelected", -3806041);
        defaultColors.put("chat_inVenueInfoText", -6182221);
        defaultColors.put("chat_outVenueInfoText", -10112933);
        defaultColors.put("chat_inVenueInfoSelectedText", -7752511);
        defaultColors.put("chat_outVenueInfoSelectedText", -10112933);
        defaultColors.put("chat_mediaInfoText", -1);
        defaultColors.put("chat_linkSelectBackground", NUM);
        defaultColors.put("chat_textSelectBackground", NUM);
        defaultColors.put("chat_emojiPanelBackground", -986379);
        defaultColors.put("chat_emojiPanelBadgeBackground", -11688214);
        defaultColors.put("chat_emojiPanelBadgeText", -1);
        defaultColors.put("chat_emojiSearchBackground", -1709586);
        defaultColors.put("chat_emojiSearchIcon", -7036497);
        defaultColors.put("chat_emojiPanelShadowLine", NUM);
        defaultColors.put("chat_emojiPanelEmptyText", -7038047);
        defaultColors.put("chat_emojiPanelIcon", -6445909);
        defaultColors.put("chat_emojiBottomPanelIcon", -7564905);
        defaultColors.put("chat_emojiPanelIconSelected", -13920286);
        defaultColors.put("chat_emojiPanelStickerPackSelector", -1907225);
        defaultColors.put("chat_emojiPanelStickerPackSelectorLine", -11097104);
        defaultColors.put("chat_emojiPanelBackspace", -7564905);
        defaultColors.put("chat_emojiPanelMasksIcon", -1);
        defaultColors.put("chat_emojiPanelMasksIconSelected", -10305560);
        defaultColors.put("chat_emojiPanelTrendingTitle", -14540254);
        defaultColors.put("chat_emojiPanelStickerSetName", -8221804);
        defaultColors.put("chat_emojiPanelStickerSetNameHighlight", -14184997);
        defaultColors.put("chat_emojiPanelStickerSetNameIcon", -5130564);
        defaultColors.put("chat_emojiPanelTrendingDescription", -7697782);
        defaultColors.put("chat_botKeyboardButtonText", -13220017);
        defaultColors.put("chat_botKeyboardButtonBackground", -1775639);
        defaultColors.put("chat_botKeyboardButtonBackgroundPressed", -3354156);
        defaultColors.put("chat_unreadMessagesStartArrowIcon", -6113849);
        defaultColors.put("chat_unreadMessagesStartText", -11102772);
        defaultColors.put("chat_unreadMessagesStartBackground", -1);
        defaultColors.put("chat_inFileIcon", -6113849);
        defaultColors.put("chat_inFileSelectedIcon", -7883067);
        defaultColors.put("chat_outFileIcon", -8011912);
        defaultColors.put("chat_outFileSelectedIcon", -8011912);
        defaultColors.put("chat_inLocationBackground", -1314571);
        defaultColors.put("chat_inLocationIcon", -6113849);
        defaultColors.put("chat_outLocationBackground", -2427453);
        defaultColors.put("chat_outLocationIcon", -7880840);
        defaultColors.put("chat_inContactBackground", -9259544);
        defaultColors.put("chat_inContactIcon", -1);
        defaultColors.put("chat_outContactBackground", -8863118);
        defaultColors.put("chat_outContactIcon", -1048610);
        defaultColors.put("chat_outBroadcast", -12146122);
        defaultColors.put("chat_mediaBroadcast", -1);
        defaultColors.put("chat_searchPanelIcons", -9999761);
        defaultColors.put("chat_searchPanelText", -9999761);
        defaultColors.put("chat_secretChatStatusText", -8421505);
        defaultColors.put("chat_fieldOverlayText", -12940081);
        defaultColors.put("chat_stickersHintPanel", -1);
        defaultColors.put("chat_replyPanelIcons", -11032346);
        defaultColors.put("chat_replyPanelClose", -7432805);
        defaultColors.put("chat_replyPanelName", -12940081);
        defaultColors.put("chat_replyPanelMessage", -14540254);
        defaultColors.put("chat_replyPanelLine", -1513240);
        defaultColors.put("chat_messagePanelBackground", -1);
        defaultColors.put("chat_messagePanelText", -16777216);
        defaultColors.put("chat_messagePanelHint", -5985101);
        defaultColors.put("chat_messagePanelCursor", -11230757);
        defaultColors.put("chat_messagePanelShadow", -16777216);
        defaultColors.put("chat_messagePanelIcons", -7432805);
        defaultColors.put("chat_recordedVoicePlayPause", -1);
        defaultColors.put("chat_recordedVoiceDot", -2468275);
        defaultColors.put("chat_recordedVoiceBackground", -10637848);
        defaultColors.put("chat_recordedVoiceProgress", -5120257);
        defaultColors.put("chat_recordedVoiceProgressInner", -1);
        defaultColors.put("chat_recordVoiceCancel", -12937772);
        defaultColors.put("key_chat_recordedVoiceHighlight", NUM);
        defaultColors.put("chat_messagePanelSend", -10309397);
        defaultColors.put("key_chat_messagePanelVoiceLock", -5987164);
        defaultColors.put("key_chat_messagePanelVoiceLockBackground", -1);
        defaultColors.put("key_chat_messagePanelVoiceLockShadow", -16777216);
        defaultColors.put("chat_recordTime", -7432805);
        defaultColors.put("chat_emojiPanelNewTrending", -11688214);
        defaultColors.put("chat_gifSaveHintText", -1);
        defaultColors.put("chat_gifSaveHintBackground", -NUM);
        defaultColors.put("chat_goDownButton", -1);
        defaultColors.put("chat_goDownButtonShadow", -16777216);
        defaultColors.put("chat_goDownButtonIcon", -7432805);
        defaultColors.put("chat_goDownButtonCounter", -1);
        defaultColors.put("chat_goDownButtonCounterBackground", -11689240);
        defaultColors.put("chat_messagePanelCancelInlineBot", -5395027);
        defaultColors.put("chat_messagePanelVoicePressed", -1);
        defaultColors.put("chat_messagePanelVoiceBackground", -10639650);
        defaultColors.put("chat_messagePanelVoiceDelete", -9211021);
        defaultColors.put("chat_messagePanelVoiceDuration", -1);
        defaultColors.put("chat_inlineResultIcon", -11037236);
        defaultColors.put("chat_topPanelBackground", -1);
        defaultColors.put("chat_topPanelClose", -7629157);
        defaultColors.put("chat_topPanelLine", -9658414);
        defaultColors.put("chat_topPanelTitle", -12940081);
        defaultColors.put("chat_topPanelMessage", -7893359);
        defaultColors.put("chat_reportSpam", -3188393);
        defaultColors.put("chat_addContact", -11894091);
        defaultColors.put("chat_inLoader", -9259544);
        defaultColors.put("chat_inLoaderSelected", -10114080);
        defaultColors.put("chat_outLoader", -8863118);
        defaultColors.put("chat_outLoaderSelected", -9783964);
        defaultColors.put("chat_inLoaderPhoto", -6113080);
        defaultColors.put("chat_inLoaderPhotoSelected", -6113849);
        defaultColors.put("chat_inLoaderPhotoIcon", -197380);
        defaultColors.put("chat_inLoaderPhotoIconSelected", -1314571);
        defaultColors.put("chat_outLoaderPhoto", -8011912);
        defaultColors.put("chat_outLoaderPhotoSelected", -8538000);
        defaultColors.put("chat_outLoaderPhotoIcon", -2427453);
        defaultColors.put("chat_outLoaderPhotoIconSelected", -4134748);
        defaultColors.put("chat_mediaLoaderPhoto", NUM);
        defaultColors.put("chat_mediaLoaderPhotoSelected", NUM);
        defaultColors.put("chat_mediaLoaderPhotoIcon", -1);
        defaultColors.put("chat_mediaLoaderPhotoIconSelected", -2500135);
        defaultColors.put("chat_secretTimerBackground", -NUM);
        defaultColors.put("chat_secretTimerText", -1);
        defaultColors.put("profile_creatorIcon", -12937771);
        defaultColors.put("profile_actionIcon", -8288630);
        defaultColors.put("profile_actionBackground", -1);
        defaultColors.put("profile_actionPressedBackground", -855310);
        defaultColors.put("profile_verifiedBackground", -5056776);
        defaultColors.put("profile_verifiedCheck", -11959368);
        defaultColors.put("profile_title", -1);
        defaultColors.put("profile_status", -2626822);
        defaultColors.put("profile_tabText", -7893872);
        defaultColors.put("profile_tabSelectedText", -12937771);
        defaultColors.put("profile_tabSelectedLine", -11557143);
        defaultColors.put("profile_tabSelector", NUM);
        defaultColors.put("player_actionBar", -1);
        defaultColors.put("player_actionBarSelector", NUM);
        defaultColors.put("player_actionBarTitle", -13683656);
        defaultColors.put("player_actionBarTop", -NUM);
        defaultColors.put("player_actionBarSubtitle", -7697782);
        defaultColors.put("player_actionBarItems", -7697782);
        defaultColors.put("player_background", -1);
        defaultColors.put("player_time", -7564650);
        defaultColors.put("player_progressBackground", -1315344);
        defaultColors.put("player_progressBackground2", -3353637);
        defaultColors.put("key_player_progressCachedBackground", -3810064);
        defaultColors.put("player_progress", -11228437);
        defaultColors.put("player_button", -13421773);
        defaultColors.put("player_buttonActive", -11753238);
        defaultColors.put("key_sheet_scrollUp", -1973016);
        defaultColors.put("key_sheet_other", -3551789);
        defaultColors.put("files_folderIcon", -1);
        defaultColors.put("files_folderIconBackground", -10637333);
        defaultColors.put("files_iconText", -1);
        defaultColors.put("sessions_devicesImage", -6908266);
        defaultColors.put("passport_authorizeBackground", -12211217);
        defaultColors.put("passport_authorizeBackgroundSelected", -12542501);
        defaultColors.put("passport_authorizeText", -1);
        defaultColors.put("location_sendLocationBackground", -12149258);
        defaultColors.put("location_sendLocationIcon", -1);
        defaultColors.put("location_sendLocationText", -14906664);
        defaultColors.put("location_sendLiveLocationBackground", -11550140);
        defaultColors.put("location_sendLiveLocationIcon", -1);
        defaultColors.put("location_sendLiveLocationText", -13194460);
        defaultColors.put("location_liveLocationProgress", -13262875);
        defaultColors.put("location_placeLocationBackground", -11753238);
        defaultColors.put("location_actionIcon", -12959675);
        defaultColors.put("location_actionActiveIcon", -12414746);
        defaultColors.put("location_actionBackground", -1);
        defaultColors.put("location_actionPressedBackground", -855310);
        defaultColors.put("dialog_liveLocationProgress", -13262875);
        defaultColors.put("calls_callReceivedGreenIcon", -16725933);
        defaultColors.put("calls_callReceivedRedIcon", -47032);
        defaultColors.put("featuredStickers_addedIcon", -11491093);
        defaultColors.put("featuredStickers_buttonProgress", -1);
        defaultColors.put("featuredStickers_addButton", -11491093);
        defaultColors.put("featuredStickers_addButtonPressed", -12346402);
        defaultColors.put("featuredStickers_removeButtonText", -11496493);
        defaultColors.put("featuredStickers_buttonText", -1);
        defaultColors.put("featuredStickers_unread", -11688214);
        defaultColors.put("inappPlayerPerformer", -13683656);
        defaultColors.put("inappPlayerTitle", -13683656);
        defaultColors.put("inappPlayerBackground", -1);
        defaultColors.put("inappPlayerPlayPause", -10309397);
        defaultColors.put("inappPlayerClose", -7629157);
        defaultColors.put("returnToCallBackground", -12279325);
        defaultColors.put("returnToCallMutedBackground", -6445135);
        defaultColors.put("returnToCallText", -1);
        defaultColors.put("sharedMedia_startStopLoadIcon", -13196562);
        defaultColors.put("sharedMedia_linkPlaceholder", -986123);
        defaultColors.put("sharedMedia_linkPlaceholderText", -4735293);
        defaultColors.put("sharedMedia_photoPlaceholder", -1182729);
        defaultColors.put("sharedMedia_actionMode", -12154957);
        defaultColors.put("checkbox", -10567099);
        defaultColors.put("checkboxCheck", -1);
        defaultColors.put("checkboxDisabled", -5195326);
        defaultColors.put("stickers_menu", -4801083);
        defaultColors.put("stickers_menuSelector", NUM);
        defaultColors.put("changephoneinfo_image", -4669499);
        defaultColors.put("changephoneinfo_image2", -11491350);
        defaultColors.put("groupcreate_hintText", -6182221);
        defaultColors.put("groupcreate_cursor", -11361317);
        defaultColors.put("groupcreate_sectionShadow", -16777216);
        defaultColors.put("groupcreate_sectionText", -8617336);
        defaultColors.put("groupcreate_spanText", -14540254);
        defaultColors.put("groupcreate_spanBackground", -855310);
        defaultColors.put("groupcreate_spanDelete", -1);
        defaultColors.put("contacts_inviteBackground", -11157919);
        defaultColors.put("contacts_inviteText", -1);
        defaultColors.put("login_progressInner", -1971470);
        defaultColors.put("login_progressOuter", -10313520);
        defaultColors.put("musicPicker_checkbox", -14043401);
        defaultColors.put("musicPicker_checkboxCheck", -1);
        defaultColors.put("musicPicker_buttonBackground", -10702870);
        defaultColors.put("musicPicker_buttonIcon", -1);
        defaultColors.put("picker_enabledButton", -15095832);
        defaultColors.put("picker_disabledButton", -6710887);
        defaultColors.put("picker_badge", -14043401);
        defaultColors.put("picker_badgeText", -1);
        defaultColors.put("chat_botSwitchToInlineText", -12348980);
        defaultColors.put("undo_background", -NUM);
        defaultColors.put("undo_cancelColor", -8008961);
        defaultColors.put("undo_infoColor", -1);
        defaultColors.put("wallet_blackBackground", -16777216);
        defaultColors.put("wallet_graySettingsBackground", -986896);
        defaultColors.put("wallet_grayBackground", -14079703);
        defaultColors.put("wallet_whiteBackground", -1);
        defaultColors.put("wallet_blackBackgroundSelector", NUM);
        defaultColors.put("wallet_whiteText", -1);
        defaultColors.put("wallet_blackText", -14540254);
        defaultColors.put("wallet_statusText", -8355712);
        defaultColors.put("wallet_grayText", -8947849);
        defaultColors.put("wallet_grayText2", -10066330);
        defaultColors.put("wallet_greenText", -13129704);
        defaultColors.put("wallet_redText", -2408384);
        defaultColors.put("wallet_dateText", -6710887);
        defaultColors.put("wallet_commentText", -6710887);
        defaultColors.put("wallet_releaseBackground", -13599557);
        defaultColors.put("wallet_pullBackground", -14606047);
        defaultColors.put("wallet_buttonBackground", -12082714);
        defaultColors.put("wallet_buttonPressedBackground", -13923114);
        defaultColors.put("wallet_buttonText", -1);
        defaultColors.put("wallet_addressConfirmBackground", NUM);
        defaultColors.put("chat_outTextSelectionHighlight", NUM);
        defaultColors.put("chat_inTextSelectionHighlight", NUM);
        defaultColors.put("chat_TextSelectionCursor", -12476440);
        defaultColors.put("statisticChartSignature", NUM);
        defaultColors.put("statisticChartSignatureAlpha", NUM);
        defaultColors.put("statisticChartHintLine", NUM);
        defaultColors.put("statisticChartActiveLine", NUM);
        defaultColors.put("statisticChartInactivePickerChart", -NUM);
        defaultColors.put("statisticChartActivePickerChart", -NUM);
        defaultColors.put("statisticChartRipple", NUM);
        defaultColors.put("statisticChartBackZoomColor", -15692829);
        defaultColors.put("statisticChartCheckboxInactive", -4342339);
        defaultColors.put("statisticChartNightIconColor", -7434605);
        defaultColors.put("statisticChartChevronColor", -2959913);
        defaultColors.put("statisticChartHighlightColor", NUM);
        defaultColors.put("statisticChartPopupBackground", -1);
        defaultColors.put("statisticChartLine_blue", -13467675);
        defaultColors.put("statisticChartLine_green", -10369198);
        defaultColors.put("statisticChartLine_red", -2075818);
        defaultColors.put("statisticChartLine_golden", -2180600);
        defaultColors.put("statisticChartLine_lightblue", -10966803);
        defaultColors.put("statisticChartLine_lightgreen", -7352519);
        defaultColors.put("statisticChartLine_orange", -1853657);
        defaultColors.put("statisticChartLine_indigo", -8422925);
        defaultColors.put("statisticChartLineEmpty", -1118482);
        defaultColors.put("actionBarTipBackground", -12292204);
        defaultColors.put("voipgroup_checkMenu", -9718023);
        defaultColors.put("voipgroup_muteButton", -8919716);
        defaultColors.put("voipgroup_muteButton2", -8528726);
        defaultColors.put("voipgroup_muteButton3", -11089922);
        defaultColors.put("voipgroup_searchText", -1);
        defaultColors.put("voipgroup_searchPlaceholder", -8024684);
        defaultColors.put("voipgroup_searchBackground", -13616313);
        defaultColors.put("voipgroup_leaveCallMenu", -35467);
        defaultColors.put("voipgroup_scrollUp", -13023660);
        defaultColors.put("voipgroup_soundButton", NUM);
        defaultColors.put("voipgroup_soundButtonActive", NUM);
        defaultColors.put("voipgroup_soundButtonActiveScrolled", -NUM);
        defaultColors.put("voipgroup_soundButton2", NUM);
        defaultColors.put("voipgroup_soundButtonActive2", NUM);
        defaultColors.put("voipgroup_soundButtonActive2Scrolled", -NUM);
        defaultColors.put("voipgroup_leaveButton", NUM);
        defaultColors.put("voipgroup_leaveButtonScrolled", -NUM);
        defaultColors.put("voipgroup_connectingProgress", -14107905);
        defaultColors.put("voipgroup_disabledButton", -14933463);
        defaultColors.put("voipgroup_disabledButtonActive", -13878715);
        defaultColors.put("voipgroup_disabledButtonActiveScrolled", -NUM);
        defaultColors.put("voipgroup_unmuteButton", -11297032);
        defaultColors.put("voipgroup_unmuteButton2", -10038021);
        defaultColors.put("voipgroup_actionBarUnscrolled", -15130842);
        defaultColors.put("voipgroup_listViewBackgroundUnscrolled", -14538189);
        defaultColors.put("voipgroup_lastSeenTextUnscrolled", -8024684);
        defaultColors.put("voipgroup_mutedIconUnscrolled", -8485236);
        defaultColors.put("voipgroup_actionBar", -15789289);
        defaultColors.put("voipgroup_emptyView", -15065823);
        defaultColors.put("voipgroup_actionBarItems", -1);
        defaultColors.put("voipgroup_actionBarSubtitle", -7697782);
        defaultColors.put("voipgroup_actionBarItemsSelector", NUM);
        defaultColors.put("voipgroup_mutedByAdminIcon", -36752);
        defaultColors.put("voipgroup_mutedIcon", -9471616);
        defaultColors.put("voipgroup_lastSeenText", -8813686);
        defaultColors.put("voipgroup_nameText", -1);
        defaultColors.put("voipgroup_listViewBackground", -14933463);
        defaultColors.put("voipgroup_dialogBackground", -14933463);
        defaultColors.put("voipgroup_listeningText", -11683585);
        defaultColors.put("voipgroup_speakingText", -8917379);
        defaultColors.put("voipgroup_listSelector", NUM);
        defaultColors.put("voipgroup_inviteMembersBackground", -14538189);
        defaultColors.put("voipgroup_overlayBlue1", -13906177);
        defaultColors.put("voipgroup_overlayBlue2", -16156957);
        defaultColors.put("voipgroup_overlayGreen1", -15551198);
        defaultColors.put("voipgroup_overlayGreen2", -16722239);
        defaultColors.put("voipgroup_topPanelBlue1", -10434565);
        defaultColors.put("voipgroup_topPanelBlue2", -11427847);
        defaultColors.put("voipgroup_topPanelGreen1", -11350435);
        defaultColors.put("voipgroup_topPanelGreen2", -16731712);
        defaultColors.put("voipgroup_topPanelGray", -8021590);
        defaultColors.put("voipgroup_overlayAlertGradientMuted", -14455406);
        defaultColors.put("voipgroup_overlayAlertGradientMuted2", -13873813);
        defaultColors.put("voipgroup_overlayAlertGradientUnmuted", -15955316);
        defaultColors.put("voipgroup_overlayAlertGradientUnmuted2", -14136203);
        defaultColors.put("voipgroup_mutedByAdminGradient", -11033346);
        defaultColors.put("voipgroup_mutedByAdminGradient2", -1026983);
        defaultColors.put("voipgroup_mutedByAdminGradient3", -9015575);
        defaultColors.put("voipgroup_overlayAlertMutedByAdmin", -9998178);
        defaultColors.put("kvoipgroup_overlayAlertMutedByAdmin2", -13676424);
        defaultColors.put("voipgroup_mutedByAdminMuteButton", NUM);
        defaultColors.put("voipgroup_mutedByAdminMuteButtonDisabled", NUM);
        defaultColors.put("voipgroup_windowBackgroundWhiteInputField", -2368549);
        defaultColors.put("voipgroup_windowBackgroundWhiteInputFieldActivated", -13129232);
        fallbackKeys.put("chat_adminText", "chat_inTimeText");
        fallbackKeys.put("chat_adminSelectedText", "chat_inTimeSelectedText");
        fallbackKeys.put("key_player_progressCachedBackground", "player_progressBackground");
        fallbackKeys.put("chat_inAudioCacheSeekbar", "chat_inAudioSeekbar");
        fallbackKeys.put("chat_outAudioCacheSeekbar", "chat_outAudioSeekbar");
        fallbackKeys.put("chat_emojiSearchBackground", "chat_emojiPanelStickerPackSelector");
        fallbackKeys.put("location_sendLiveLocationIcon", "location_sendLocationIcon");
        fallbackKeys.put("changephoneinfo_image2", "featuredStickers_addButton");
        fallbackKeys.put("key_graySectionText", "windowBackgroundWhiteGrayText2");
        fallbackKeys.put("chat_inMediaIcon", "chat_inBubble");
        fallbackKeys.put("chat_outMediaIcon", "chat_outBubble");
        fallbackKeys.put("chat_inMediaIconSelected", "chat_inBubbleSelected");
        fallbackKeys.put("chat_outMediaIconSelected", "chat_outBubbleSelected");
        fallbackKeys.put("chats_actionUnreadIcon", "profile_actionIcon");
        fallbackKeys.put("chats_actionUnreadBackground", "profile_actionBackground");
        fallbackKeys.put("chats_actionUnreadPressedBackground", "profile_actionPressedBackground");
        fallbackKeys.put("dialog_inlineProgressBackground", "windowBackgroundGray");
        fallbackKeys.put("dialog_inlineProgress", "chats_menuItemIcon");
        fallbackKeys.put("groupcreate_spanDelete", "chats_actionIcon");
        fallbackKeys.put("sharedMedia_photoPlaceholder", "windowBackgroundGray");
        fallbackKeys.put("chat_attachPollBackground", "chat_attachAudioBackground");
        fallbackKeys.put("chat_attachPollIcon", "chat_attachAudioIcon");
        fallbackKeys.put("chats_onlineCircle", "windowBackgroundWhiteBlueText");
        fallbackKeys.put("windowBackgroundWhiteBlueButton", "windowBackgroundWhiteValueText");
        fallbackKeys.put("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteValueText");
        fallbackKeys.put("undo_background", "chat_gifSaveHintBackground");
        fallbackKeys.put("undo_cancelColor", "chat_gifSaveHintText");
        fallbackKeys.put("undo_infoColor", "chat_gifSaveHintText");
        fallbackKeys.put("windowBackgroundUnchecked", "windowBackgroundWhite");
        fallbackKeys.put("windowBackgroundChecked", "windowBackgroundWhite");
        fallbackKeys.put("switchTrackBlue", "switchTrack");
        fallbackKeys.put("switchTrackBlueChecked", "switchTrackChecked");
        fallbackKeys.put("switchTrackBlueThumb", "windowBackgroundWhite");
        fallbackKeys.put("switchTrackBlueThumbChecked", "windowBackgroundWhite");
        fallbackKeys.put("windowBackgroundCheckText", "windowBackgroundWhiteBlackText");
        fallbackKeys.put("contextProgressInner4", "contextProgressInner1");
        fallbackKeys.put("contextProgressOuter4", "contextProgressOuter1");
        fallbackKeys.put("switchTrackBlueSelector", "listSelectorSDK21");
        fallbackKeys.put("switchTrackBlueSelectorChecked", "listSelectorSDK21");
        fallbackKeys.put("chat_emojiBottomPanelIcon", "chat_emojiPanelIcon");
        fallbackKeys.put("chat_emojiSearchIcon", "chat_emojiPanelIcon");
        fallbackKeys.put("chat_emojiPanelStickerSetNameHighlight", "windowBackgroundWhiteBlueText4");
        fallbackKeys.put("chat_emojiPanelStickerPackSelectorLine", "chat_emojiPanelIconSelected");
        fallbackKeys.put("sharedMedia_actionMode", "actionBarDefault");
        fallbackKeys.put("key_sheet_scrollUp", "chat_emojiPanelStickerPackSelector");
        fallbackKeys.put("key_sheet_other", "player_actionBarItems");
        fallbackKeys.put("dialogSearchBackground", "chat_emojiPanelStickerPackSelector");
        fallbackKeys.put("dialogSearchHint", "chat_emojiPanelIcon");
        fallbackKeys.put("dialogSearchIcon", "chat_emojiPanelIcon");
        fallbackKeys.put("dialogSearchText", "windowBackgroundWhiteBlackText");
        fallbackKeys.put("dialogFloatingButton", "dialogRoundCheckBox");
        fallbackKeys.put("dialogFloatingButtonPressed", "dialogRoundCheckBox");
        fallbackKeys.put("dialogFloatingIcon", "dialogRoundCheckBoxCheck");
        fallbackKeys.put("dialogShadowLine", "chat_emojiPanelShadowLine");
        fallbackKeys.put("actionBarDefaultArchived", "actionBarDefault");
        fallbackKeys.put("actionBarDefaultArchivedSelector", "actionBarDefaultSelector");
        fallbackKeys.put("actionBarDefaultArchivedIcon", "actionBarDefaultIcon");
        fallbackKeys.put("actionBarDefaultArchivedTitle", "actionBarDefaultTitle");
        fallbackKeys.put("actionBarDefaultArchivedSearch", "actionBarDefaultSearch");
        fallbackKeys.put("actionBarDefaultSearchArchivedPlaceholder", "actionBarDefaultSearchPlaceholder");
        fallbackKeys.put("chats_message_threeLines", "chats_message");
        fallbackKeys.put("chats_nameMessage_threeLines", "chats_nameMessage");
        fallbackKeys.put("chats_nameArchived", "chats_name");
        fallbackKeys.put("chats_nameMessageArchived", "chats_nameMessage");
        fallbackKeys.put("chats_nameMessageArchived_threeLines", "chats_nameMessage");
        fallbackKeys.put("chats_messageArchived", "chats_message");
        fallbackKeys.put("avatar_backgroundArchived", "chats_unreadCounterMuted");
        fallbackKeys.put("chats_archiveBackground", "chats_actionBackground");
        fallbackKeys.put("chats_archivePinBackground", "chats_unreadCounterMuted");
        fallbackKeys.put("chats_archiveIcon", "chats_actionIcon");
        fallbackKeys.put("chats_archiveText", "chats_actionIcon");
        fallbackKeys.put("actionBarDefaultSubmenuItemIcon", "dialogIcon");
        fallbackKeys.put("checkboxDisabled", "chats_unreadCounterMuted");
        fallbackKeys.put("chat_status", "actionBarDefaultSubtitle");
        fallbackKeys.put("chat_inDownCall", "calls_callReceivedGreenIcon");
        fallbackKeys.put("chat_inUpCall", "calls_callReceivedRedIcon");
        fallbackKeys.put("chat_outUpCall", "calls_callReceivedGreenIcon");
        fallbackKeys.put("actionBarTabActiveText", "actionBarDefaultTitle");
        fallbackKeys.put("actionBarTabUnactiveText", "actionBarDefaultSubtitle");
        fallbackKeys.put("actionBarTabLine", "actionBarDefaultTitle");
        fallbackKeys.put("actionBarTabSelector", "actionBarDefaultSelector");
        fallbackKeys.put("profile_status", "avatar_subtitleInProfileBlue");
        fallbackKeys.put("chats_menuTopBackgroundCats", "avatar_backgroundActionBarBlue");
        fallbackKeys.put("chat_attachPermissionImage", "dialogTextBlack");
        fallbackKeys.put("chat_attachPermissionMark", "chat_sentError");
        fallbackKeys.put("chat_attachPermissionText", "dialogTextBlack");
        fallbackKeys.put("chat_attachEmptyImage", "emptyListPlaceholder");
        fallbackKeys.put("actionBarBrowser", "actionBarDefault");
        fallbackKeys.put("chats_sentReadCheck", "chats_sentCheck");
        fallbackKeys.put("chat_outSentCheckRead", "chat_outSentCheck");
        fallbackKeys.put("chat_outSentCheckReadSelected", "chat_outSentCheckSelected");
        fallbackKeys.put("chats_archivePullDownBackground", "chats_unreadCounterMuted");
        fallbackKeys.put("chats_archivePullDownBackgroundActive", "chats_actionBackground");
        fallbackKeys.put("avatar_backgroundArchivedHidden", "avatar_backgroundSaved");
        fallbackKeys.put("featuredStickers_removeButtonText", "featuredStickers_addButtonPressed");
        fallbackKeys.put("dialogEmptyImage", "player_time");
        fallbackKeys.put("dialogEmptyText", "player_time");
        fallbackKeys.put("location_actionIcon", "dialogTextBlack");
        fallbackKeys.put("location_actionActiveIcon", "windowBackgroundWhiteBlueText7");
        fallbackKeys.put("location_actionBackground", "dialogBackground");
        fallbackKeys.put("location_actionPressedBackground", "dialogBackgroundGray");
        fallbackKeys.put("location_sendLocationText", "windowBackgroundWhiteBlueText7");
        fallbackKeys.put("location_sendLiveLocationText", "windowBackgroundWhiteGreenText");
        fallbackKeys.put("chat_outTextSelectionHighlight", "chat_textSelectBackground");
        fallbackKeys.put("chat_inTextSelectionHighlight", "chat_textSelectBackground");
        fallbackKeys.put("chat_TextSelectionCursor", "chat_messagePanelCursor");
        fallbackKeys.put("chat_inPollCorrectAnswer", "chat_attachLocationBackground");
        fallbackKeys.put("chat_outPollCorrectAnswer", "chat_attachLocationBackground");
        fallbackKeys.put("chat_inPollWrongAnswer", "chat_attachAudioBackground");
        fallbackKeys.put("chat_outPollWrongAnswer", "chat_attachAudioBackground");
        fallbackKeys.put("profile_tabText", "windowBackgroundWhiteGrayText");
        fallbackKeys.put("profile_tabSelectedText", "windowBackgroundWhiteBlueHeader");
        fallbackKeys.put("profile_tabSelectedLine", "windowBackgroundWhiteBlueHeader");
        fallbackKeys.put("profile_tabSelector", "listSelectorSDK21");
        fallbackKeys.put("statisticChartPopupBackground", "dialogBackground");
        fallbackKeys.put("chat_attachGalleryText", "chat_attachGalleryBackground");
        fallbackKeys.put("chat_attachAudioText", "chat_attachAudioBackground");
        fallbackKeys.put("chat_attachFileText", "chat_attachFileBackground");
        fallbackKeys.put("chat_attachContactText", "chat_attachContactBackground");
        fallbackKeys.put("chat_attachLocationText", "chat_attachLocationBackground");
        fallbackKeys.put("chat_attachPollText", "chat_attachPollBackground");
        fallbackKeys.put("chat_inPsaNameText", "avatar_nameInMessageGreen");
        fallbackKeys.put("chat_outPsaNameText", "avatar_nameInMessageGreen");
        fallbackKeys.put("chat_outAdminText", "chat_outTimeText");
        fallbackKeys.put("chat_outAdminSelectedText", "chat_outTimeSelectedText");
        fallbackKeys.put("returnToCallMutedBackground", "windowBackgroundWhite");
        fallbackKeys.put("dialogSwipeRemove", "avatar_backgroundRed");
        themeAccentExclusionKeys.addAll(Arrays.asList(keys_avatar_background));
        themeAccentExclusionKeys.addAll(Arrays.asList(keys_avatar_nameInMessage));
        themeAccentExclusionKeys.add("chat_attachFileBackground");
        themeAccentExclusionKeys.add("chat_attachGalleryBackground");
        themeAccentExclusionKeys.add("chat_attachFileText");
        themeAccentExclusionKeys.add("chat_attachGalleryText");
        themeAccentExclusionKeys.add("statisticChartLine_blue");
        themeAccentExclusionKeys.add("statisticChartLine_green");
        themeAccentExclusionKeys.add("statisticChartLine_red");
        themeAccentExclusionKeys.add("statisticChartLine_golden");
        themeAccentExclusionKeys.add("statisticChartLine_lightblue");
        themeAccentExclusionKeys.add("statisticChartLine_lightgreen");
        themeAccentExclusionKeys.add("statisticChartLine_orange");
        themeAccentExclusionKeys.add("statisticChartLine_indigo");
        themeAccentExclusionKeys.add("voipgroup_checkMenu");
        themeAccentExclusionKeys.add("voipgroup_muteButton");
        themeAccentExclusionKeys.add("voipgroup_muteButton2");
        themeAccentExclusionKeys.add("voipgroup_muteButton3");
        themeAccentExclusionKeys.add("voipgroup_searchText");
        themeAccentExclusionKeys.add("voipgroup_searchPlaceholder");
        themeAccentExclusionKeys.add("voipgroup_searchBackground");
        themeAccentExclusionKeys.add("voipgroup_leaveCallMenu");
        themeAccentExclusionKeys.add("voipgroup_scrollUp");
        themeAccentExclusionKeys.add("voipgroup_blueText");
        themeAccentExclusionKeys.add("voipgroup_soundButton");
        themeAccentExclusionKeys.add("voipgroup_soundButtonActive");
        themeAccentExclusionKeys.add("voipgroup_soundButtonActiveScrolled");
        themeAccentExclusionKeys.add("voipgroup_soundButton2");
        themeAccentExclusionKeys.add("voipgroup_soundButtonActive2");
        themeAccentExclusionKeys.add("voipgroup_soundButtonActive2Scrolled");
        themeAccentExclusionKeys.add("voipgroup_leaveButton");
        themeAccentExclusionKeys.add("voipgroup_leaveButtonScrolled");
        themeAccentExclusionKeys.add("voipgroup_connectingProgress");
        themeAccentExclusionKeys.add("voipgroup_disabledButton");
        themeAccentExclusionKeys.add("voipgroup_disabledButtonActive");
        themeAccentExclusionKeys.add("voipgroup_disabledButtonActiveScrolled");
        themeAccentExclusionKeys.add("voipgroup_unmuteButton");
        themeAccentExclusionKeys.add("voipgroup_unmuteButton2");
        themeAccentExclusionKeys.add("voipgroup_actionBarUnscrolled");
        themeAccentExclusionKeys.add("voipgroup_listViewBackgroundUnscrolled");
        themeAccentExclusionKeys.add("voipgroup_lastSeenTextUnscrolled");
        themeAccentExclusionKeys.add("voipgroup_mutedIconUnscrolled");
        themeAccentExclusionKeys.add("voipgroup_actionBar");
        themeAccentExclusionKeys.add("voipgroup_emptyView");
        themeAccentExclusionKeys.add("voipgroup_actionBarItems");
        themeAccentExclusionKeys.add("voipgroup_actionBarSubtitle");
        themeAccentExclusionKeys.add("voipgroup_actionBarItemsSelector");
        themeAccentExclusionKeys.add("voipgroup_mutedByAdminIcon");
        themeAccentExclusionKeys.add("voipgroup_mutedIcon");
        themeAccentExclusionKeys.add("voipgroup_lastSeenText");
        themeAccentExclusionKeys.add("voipgroup_nameText");
        themeAccentExclusionKeys.add("voipgroup_listViewBackground");
        themeAccentExclusionKeys.add("voipgroup_listeningText");
        themeAccentExclusionKeys.add("voipgroup_speakingText");
        themeAccentExclusionKeys.add("voipgroup_listSelector");
        themeAccentExclusionKeys.add("voipgroup_inviteMembersBackground");
        themeAccentExclusionKeys.add("voipgroup_dialogBackground");
        themeAccentExclusionKeys.add("voipgroup_overlayGreen1");
        themeAccentExclusionKeys.add("voipgroup_overlayGreen2");
        themeAccentExclusionKeys.add("voipgroup_overlayBlue1");
        themeAccentExclusionKeys.add("voipgroup_overlayBlue2");
        themeAccentExclusionKeys.add("voipgroup_topPanelGreen1");
        themeAccentExclusionKeys.add("voipgroup_topPanelGreen2");
        themeAccentExclusionKeys.add("voipgroup_topPanelBlue1");
        themeAccentExclusionKeys.add("voipgroup_topPanelBlue2");
        themeAccentExclusionKeys.add("voipgroup_topPanelGray");
        themeAccentExclusionKeys.add("voipgroup_overlayAlertGradientMuted");
        themeAccentExclusionKeys.add("voipgroup_overlayAlertGradientMuted2");
        themeAccentExclusionKeys.add("voipgroup_overlayAlertGradientUnmuted");
        themeAccentExclusionKeys.add("voipgroup_overlayAlertGradientUnmuted2");
        themeAccentExclusionKeys.add("voipgroup_overlayAlertMutedByAdmin");
        themeAccentExclusionKeys.add("kvoipgroup_overlayAlertMutedByAdmin2");
        themeAccentExclusionKeys.add("voipgroup_mutedByAdminGradient");
        themeAccentExclusionKeys.add("voipgroup_mutedByAdminGradient2");
        themeAccentExclusionKeys.add("voipgroup_mutedByAdminGradient3");
        themeAccentExclusionKeys.add("voipgroup_mutedByAdminMuteButton");
        themeAccentExclusionKeys.add("voipgroup_mutedByAdminMuteButtonDisabled");
        themeAccentExclusionKeys.add("voipgroup_windowBackgroundWhiteInputField");
        themeAccentExclusionKeys.add("voipgroup_windowBackgroundWhiteInputFieldActivated");
        myMessagesBubblesColorKeys.add("chat_outBubble");
        myMessagesBubblesColorKeys.add("chat_outBubbleSelected");
        myMessagesBubblesColorKeys.add("chat_outBubbleShadow");
        myMessagesBubblesColorKeys.add("chat_outBubbleGradient");
        myMessagesColorKeys.add("chat_outUpCall");
        myMessagesColorKeys.add("chat_outSentCheck");
        myMessagesColorKeys.add("chat_outSentCheckSelected");
        myMessagesColorKeys.add("chat_outSentCheckRead");
        myMessagesColorKeys.add("chat_outSentCheckReadSelected");
        myMessagesColorKeys.add("chat_outSentClock");
        myMessagesColorKeys.add("chat_outSentClockSelected");
        myMessagesColorKeys.add("chat_outMediaIcon");
        myMessagesColorKeys.add("chat_outMediaIconSelected");
        myMessagesColorKeys.add("chat_outViews");
        myMessagesColorKeys.add("chat_outViewsSelected");
        myMessagesColorKeys.add("chat_outMenu");
        myMessagesColorKeys.add("chat_outMenuSelected");
        myMessagesColorKeys.add("chat_outInstant");
        myMessagesColorKeys.add("chat_outInstantSelected");
        myMessagesColorKeys.add("chat_outPreviewInstantText");
        myMessagesColorKeys.add("chat_outPreviewInstantSelectedText");
        myMessagesColorKeys.add("chat_outForwardedNameText");
        myMessagesColorKeys.add("chat_outViaBotNameText");
        myMessagesColorKeys.add("chat_outReplyLine");
        myMessagesColorKeys.add("chat_outReplyNameText");
        myMessagesColorKeys.add("chat_outReplyMessageText");
        myMessagesColorKeys.add("chat_outReplyMediaMessageText");
        myMessagesColorKeys.add("chat_outReplyMediaMessageSelectedText");
        myMessagesColorKeys.add("chat_outPreviewLine");
        myMessagesColorKeys.add("chat_outSiteNameText");
        myMessagesColorKeys.add("chat_outContactNameText");
        myMessagesColorKeys.add("chat_outContactPhoneText");
        myMessagesColorKeys.add("chat_outContactPhoneSelectedText");
        myMessagesColorKeys.add("chat_outAudioProgress");
        myMessagesColorKeys.add("chat_outAudioSelectedProgress");
        myMessagesColorKeys.add("chat_outTimeText");
        myMessagesColorKeys.add("chat_outTimeSelectedText");
        myMessagesColorKeys.add("chat_outAudioPerfomerText");
        myMessagesColorKeys.add("chat_outAudioPerfomerSelectedText");
        myMessagesColorKeys.add("chat_outAudioTitleText");
        myMessagesColorKeys.add("chat_outAudioDurationText");
        myMessagesColorKeys.add("chat_outAudioDurationSelectedText");
        myMessagesColorKeys.add("chat_outAudioSeekbar");
        myMessagesColorKeys.add("chat_outAudioCacheSeekbar");
        myMessagesColorKeys.add("chat_outAudioSeekbarSelected");
        myMessagesColorKeys.add("chat_outAudioSeekbarFill");
        myMessagesColorKeys.add("chat_outVoiceSeekbar");
        myMessagesColorKeys.add("chat_outVoiceSeekbarSelected");
        myMessagesColorKeys.add("chat_outVoiceSeekbarFill");
        myMessagesColorKeys.add("chat_outFileProgress");
        myMessagesColorKeys.add("chat_outFileProgressSelected");
        myMessagesColorKeys.add("chat_outFileNameText");
        myMessagesColorKeys.add("chat_outFileInfoText");
        myMessagesColorKeys.add("chat_outFileInfoSelectedText");
        myMessagesColorKeys.add("chat_outFileBackground");
        myMessagesColorKeys.add("chat_outFileBackgroundSelected");
        myMessagesColorKeys.add("chat_outVenueInfoText");
        myMessagesColorKeys.add("chat_outVenueInfoSelectedText");
        myMessagesColorKeys.add("chat_outLoader");
        myMessagesColorKeys.add("chat_outLoaderSelected");
        myMessagesColorKeys.add("chat_outLoaderPhoto");
        myMessagesColorKeys.add("chat_outLoaderPhotoSelected");
        myMessagesColorKeys.add("chat_outLoaderPhotoIcon");
        myMessagesColorKeys.add("chat_outLoaderPhotoIconSelected");
        myMessagesColorKeys.add("chat_outLocationBackground");
        myMessagesColorKeys.add("chat_outLocationIcon");
        myMessagesColorKeys.add("chat_outContactBackground");
        myMessagesColorKeys.add("chat_outContactIcon");
        myMessagesColorKeys.add("chat_outFileIcon");
        myMessagesColorKeys.add("chat_outFileSelectedIcon");
        myMessagesColorKeys.add("chat_outBroadcast");
        myMessagesColorKeys.add("chat_messageTextOut");
        myMessagesColorKeys.add("chat_messageLinkOut");
        SharedPreferences themeConfig = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
        ThemeInfo themeInfo2 = new ThemeInfo();
        themeInfo2.name = "Blue";
        themeInfo2.assetName = "bluebubbles.attheme";
        int unused = themeInfo2.previewBackgroundColor = -6963476;
        int unused2 = themeInfo2.previewInColor = -1;
        int unused3 = themeInfo2.previewOutColor = -3086593;
        themeInfo2.firstAccentIsDefault = true;
        themeInfo2.currentAccentId = DEFALT_THEME_ACCENT_ID;
        themeInfo2.sortIndex = 1;
        themeInfo2.setAccentColorOptions(new int[]{-10972987, -14444461, -3252606, -8428605, -14380627, -14050257, -7842636, -13464881, -12342073, -11359164, -3317869, -2981834, -8165684, -3256745, -2904512, -8681301}, new int[]{-4660851, -328756, -1572, -4108434, -3031781, -1335, -198952, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{0, -853047, -264993, 0, 0, -135756, -198730, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{0, -2104672, -937328, -2637335, -2639714, -1270157, -3428124, -6570777, -7223828, -6567550, -1793599, -1855875, -4674838, -1336199, -2900876, -6247730}, new int[]{0, -4532067, -1257580, -1524266, -1646910, -1519483, -1324823, -4138509, -4202516, -2040429, -1458474, -1256030, -3814930, -1000039, -1450082, -3485987}, new int[]{0, -1909081, -1592444, -2969879, -2439762, -1137033, -2119471, -6962197, -4857383, -4270699, -3364639, -2117514, -5000734, -1598028, -2045813, -5853742}, new int[]{0, -6371440, -1319256, -1258616, -1712961, -1186647, -1193816, -4467224, -4203544, -3023977, -1061929, -1255788, -2113811, -806526, -1715305, -3485976}, new int[]{99, 9, 10, 11, 12, 13, 14, 0, 1, 2, 3, 4, 5, 6, 7, 8}, new String[]{"", "p-pXcflrmFIBAAAAvXYQk-mCwZU", "JqSUrO0-mFIBAAAAWwTvLzoWGQI", "O-wmAfBPSFADAAAA4zINVfD_bro", "RepJ5uE_SVABAAAAr4d0YhgB850", "-Xc-np9y2VMCAAAARKr0yNNPYW0", "fqv01SQemVIBAAAApND8LDRUhRU", "fqv01SQemVIBAAAApND8LDRUhRU", "RepJ5uE_SVABAAAAr4d0YhgB850", "lp0prF8ISFAEAAAA_p385_CvG0w", "heptcj-hSVACAAAAC9RrMzOa-cs", "PllZ-bf_SFAEAAAA8crRfwZiDNg", "dhf9pceaQVACAAAAbzdVo4SCiZA", "Ujx2TFcJSVACAAAARJ4vLa50MkM", "p-pXcflrmFIBAAAAvXYQk-mCwZU", "dk_wwlghOFACAAAAfz9xrxi6euw"}, new int[]{0, 180, 45, 0, 45, 180, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{0, 52, 46, 57, 45, 64, 52, 35, 36, 41, 50, 50, 35, 38, 37, 30});
        sortAccents(themeInfo2);
        ArrayList<ThemeInfo> arrayList = themes;
        defaultTheme = themeInfo2;
        currentTheme = themeInfo2;
        currentDayTheme = themeInfo2;
        arrayList.add(themeInfo2);
        themesDict.put("Blue", themeInfo2);
        ThemeInfo themeInfo3 = new ThemeInfo();
        themeInfo3.name = "Dark Blue";
        themeInfo3.assetName = "darkblue.attheme";
        int unused4 = themeInfo3.previewBackgroundColor = -10523006;
        int unused5 = themeInfo3.previewInColor = -9009508;
        int unused6 = themeInfo3.previewOutColor = -8214301;
        themeInfo3.sortIndex = 3;
        themeInfo3.setAccentColorOptions(new int[]{-7177260, -9860357, -14440464, -8687151, -9848491, -14053142, -9403671, -10044691, -13203974, -12138259, -10179489, -1344335, -1142742, -6127120, -2931932, -1131212, -8417365, -13270557}, new int[]{-6464359, -10267323, -13532789, -5413850, -11898828, -13410942, -13215889, -10914461, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{-10465880, -9937588, -14983040, -6736562, -14197445, -13534568, -13144441, -10587280, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{-14213586, -15263198, -16310753, -15724781, -15853551, -16051428, -14868183, -14668758, -15854566, -15326427, -15327979, -14411490, -14345453, -14738135, -14543346, -14212843, -15263205, -15854566}, new int[]{-15659501, -14277074, -15459034, -14542297, -14735336, -15129808, -15591910, -15459810, -15260623, -15853800, -15259879, -14477540, -14674936, -15461604, -13820650, -15067635, -14605528, -15260623}, new int[]{-13951445, -15395557, -15985382, -15855853, -16050417, -15525854, -15260627, -15327189, -15788258, -14799314, -15458796, -13952727, -13754603, -14081231, -14478324, -14081004, -15197667, -15788258}, new int[]{-15330777, -15066858, -15915220, -14213847, -15262439, -15260879, -15657695, -16443625, -15459285, -15589601, -14932454, -14740451, -15002870, -15264997, -13821660, -14805234, -14605784, -15459285}, new int[]{11, 12, 13, 14, 15, 16, 17, 18, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, new String[]{"O-wmAfBPSFADAAAA4zINVfD_bro", "RepJ5uE_SVABAAAAr4d0YhgB850", "dk_wwlghOFACAAAAfz9xrxi6euw", "9LW_RcoOSVACAAAAFTk3DTyXN-M", "PllZ-bf_SFAEAAAA8crRfwZiDNg", "-Xc-np9y2VMCAAAARKr0yNNPYW0", "kO4jyq55SFABAAAA0WEpcLfahXk", "CJNyxPMgSVAEAAAAvW9sMwCLASSNAMEcw", "fqv01SQemVIBAAAApND8LDRUhRU", "RepJ5uE_SVABAAAAr4d0YhgB850", "CJNyxPMgSVAEAAAAvW9sMwCLASSNAMEcw", "9LW_RcoOSVACAAAAFTk3DTyXN-M", "9GcNVISdSVADAAAAUcw5BYjELW4", "F5oWoCs7QFACAAAAgf2bD_mg8Bw", "9Shvar_d1MFIIAAAAjWnm8_ZMe8Q", "3rX-PaKbSFACAAAAEiHNvcEm6X4", "dk_wwlghOFACAAAAfz9xrxi6euw", "fqv01SQemVIBAAAApND8LDRUhRU"}, new int[]{225, 45, 225, 135, 45, 225, 45, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{40, 40, 31, 50, 25, 34, 35, 35, 38, 29, 24, 34, 34, 31, 29, 37, 21, 38});
        sortAccents(themeInfo3);
        themes.add(themeInfo3);
        HashMap<String, ThemeInfo> hashMap = themesDict;
        currentNightTheme = themeInfo3;
        hashMap.put("Dark Blue", themeInfo3);
        ThemeInfo themeInfo4 = new ThemeInfo();
        themeInfo4.name = "Arctic Blue";
        themeInfo4.assetName = "arctic.attheme";
        int unused7 = themeInfo4.previewBackgroundColor = -1971728;
        int unused8 = themeInfo4.previewInColor = -1;
        int unused9 = themeInfo4.previewOutColor = -9657877;
        themeInfo4.sortIndex = 5;
        themeInfo4.setAccentColorOptions(new int[]{-12537374, -12472227, -3240928, -11033621, -2194124, -3382903, -13332245, -12342073, -11359164, -3317869, -2981834, -8165684, -3256745, -2904512, -8681301}, new int[]{-13525046, -14113959, -7579073, -13597229, -3581840, -8883763, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{-11616542, -9716647, -6400452, -12008744, -2592697, -4297041, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{-3808528, -2433367, -2700891, -1838093, -1120848, -1712148, -2037779, -4202261, -4005713, -1058332, -925763, -1975316, -1189672, -1318451, -2302235}, new int[]{-1510157, -4398164, -1647697, -3610898, -1130838, -1980692, -4270093, -4202261, -3415654, -1259815, -1521765, -4341268, -1127744, -1318219, -3945761}, new int[]{-4924688, -3283031, -1523567, -2494477, -1126510, -595210, -2037517, -3478548, -4661623, -927514, -796762, -2696971, -1188403, -1319735, -1577487}, new int[]{-3149585, -5714021, -1978209, -4925720, -1134713, -1718833, -3613709, -5317397, -3218014, -999207, -2116466, -4343054, -931397, -1583186, -3815718}, new int[]{9, 10, 11, 12, 13, 14, 0, 1, 2, 3, 4, 5, 6, 7, 8}, new String[]{"MIo6r0qGSFAFAAAAtL8TsDzNX60", "dhf9pceaQVACAAAAbzdVo4SCiZA", "fqv01SQemVIBAAAApND8LDRUhRU", "p-pXcflrmFIBAAAAvXYQk-mCwZU", "JqSUrO0-mFIBAAAAWwTvLzoWGQI", "F5oWoCs7QFACAAAAgf2bD_mg8Bw", "fqv01SQemVIBAAAApND8LDRUhRU", "RepJ5uE_SVABAAAAr4d0YhgB850", "PllZ-bf_SFAEAAAA8crRfwZiDNg", "pgJfpFNRSFABAAAACDT8s5sEjfc", "ptuUd96JSFACAAAATobI23sPpz0", "dhf9pceaQVACAAAAbzdVo4SCiZA", "JqSUrO0-mFIBAAAAWwTvLzoWGQI", "9iklpvIPQVABAAAAORQXKur_Eyc", "F5oWoCs7QFACAAAAgf2bD_mg8Bw"}, new int[]{315, 315, 225, 315, 0, 180, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{50, 50, 58, 47, 46, 50, 49, 46, 51, 50, 49, 34, 54, 50, 40});
        sortAccents(themeInfo4);
        themes.add(themeInfo4);
        themesDict.put("Arctic Blue", themeInfo4);
        ThemeInfo themeInfo5 = new ThemeInfo();
        themeInfo5.name = "Day";
        themeInfo5.assetName = "day.attheme";
        int unused10 = themeInfo5.previewBackgroundColor = -1;
        int unused11 = themeInfo5.previewInColor = -1315084;
        int unused12 = themeInfo5.previewOutColor = -8604930;
        themeInfo5.sortIndex = 2;
        themeInfo5.setAccentColorOptions(new int[]{-11099447, -3379581, -3109305, -3382174, -7963438, -11759137, -11029287, -11226775, -2506945, -3382174, -3379581, -6587438, -2649788, -8681301}, new int[]{-10125092, -9671214, -3451775, -3978678, -10711329, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{-12664362, -3642988, -2383569, -3109317, -11422261, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, (int[]) null, (int[]) null, new int[]{9, 10, 11, 12, 13, 0, 1, 2, 3, 4, 5, 6, 7, 8}, new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", ""}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        sortAccents(themeInfo5);
        themes.add(themeInfo5);
        themesDict.put("Day", themeInfo5);
        ThemeInfo themeInfo6 = new ThemeInfo();
        themeInfo6.name = "Night";
        themeInfo6.assetName = "night.attheme";
        int unused13 = themeInfo6.previewBackgroundColor = -11315623;
        int unused14 = themeInfo6.previewInColor = -9143676;
        int unused15 = themeInfo6.previewOutColor = -9067802;
        themeInfo6.sortIndex = 4;
        themeInfo6.setAccentColorOptions(new int[]{-9781697, -7505693, -2204034, -10913816, -2375398, -12678921, -11881005, -11880383, -2534026, -1934037, -7115558, -3128522, -1528292, -8812381}, new int[]{-7712108, -4953061, -5288081, -14258547, -9154889, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{-9939525, -5948598, -10335844, -13659747, -14054507, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{-15330532, -14806760, -15791344, -16184308, -16313063, -15921641, -15656164, -15986420, -15856883, -14871025, -16185078, -14937584, -14869736, -15855598}, new int[]{-14673881, -15724781, -15002342, -15458526, -15987697, -16184820, -16118258, -16250616, -15067624, -15527923, -14804447, -15790836, -15987960, -16316665}, new int[]{-15856877, -14608861, -15528430, -15921391, -15722209, -15197144, -15458015, -15591406, -15528431, -15068401, -16053749, -15594229, -15395825, -15724012}, new int[]{-14804694, -15658986, -14609382, -15656421, -16118509, -15855854, -16315381, -16052981, -14544354, -15791092, -15659241, -16316922, -15988214, -16185077}, new int[]{9, 10, 11, 12, 13, 0, 1, 2, 3, 4, 5, 6, 7, 8}, new String[]{"YIxYGEALQVADAAAAA3QbEH0AowY", "9LW_RcoOSVACAAAAFTk3DTyXN-M", "O-wmAfBPSFADAAAA4zINVfD_bro", "F5oWoCs7QFACAAAAgf2bD_mg8Bw", "-Xc-np9y2VMCAAAARKr0yNNPYW0", "fqv01SQemVIBAAAApND8LDRUhRU", "F5oWoCs7QFACAAAAgf2bD_mg8Bw", "ptuUd96JSFACAAAATobI23sPpz0", "p-pXcflrmFIBAAAAvXYQk-mCwZU", "Nl8Pg2rBQVACAAAA25Lxtb8SDp0", "dhf9pceaQVACAAAAbzdVo4SCiZA", "9GcNVISdSVADAAAAUcw5BYjELW4", "9LW_RcoOSVACAAAAFTk3DTyXN-M", "dk_wwlghOFACAAAAfz9xrxi6euw"}, new int[]{45, 135, 0, 180, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new int[]{34, 47, 52, 48, 54, 50, 37, 56, 48, 49, 40, 64, 38, 48});
        sortAccents(themeInfo6);
        themes.add(themeInfo6);
        themesDict.put("Night", themeInfo6);
        String themesString2 = themeConfig.getString("themes2", (String) null);
        int remoteVersion2 = themeConfig.getInt("remote_version", 0);
        int appRemoteThemesVersion = true;
        if (remoteVersion2 == 1) {
            for (int a = 0; a < 3; a++) {
                long[] jArr = remoteThemesHash;
                StringBuilder sb = new StringBuilder();
                sb.append("2remoteThemesHash");
                if (a != 0) {
                    num = Integer.valueOf(a);
                } else {
                    num = "";
                }
                sb.append(num);
                jArr[a] = themeConfig.getLong(sb.toString(), 0);
                int[] iArr = lastLoadingThemesTime;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("lastLoadingThemesTime");
                if (a != 0) {
                    num2 = Integer.valueOf(a);
                } else {
                    num2 = "";
                }
                sb2.append(num2);
                iArr[a] = themeConfig.getInt(sb2.toString(), 0);
            }
        }
        themeConfig.edit().putInt("remote_version", 1).apply();
        if (!TextUtils.isEmpty(themesString2)) {
            try {
                JSONArray jsonArray = new JSONArray(themesString2);
                for (int a2 = 0; a2 < jsonArray.length(); a2++) {
                    themeInfo6 = ThemeInfo.createWithJson(jsonArray.getJSONObject(a2));
                    if (themeInfo6 != null) {
                        otherThemes.add(themeInfo6);
                        themes.add(themeInfo6);
                        themesDict.put(themeInfo6.getKey(), themeInfo6);
                        themeInfo6.loadWallpapers(themeConfig);
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            themesString2 = themeConfig.getString("themes", (String) null);
            if (!TextUtils.isEmpty(themesString2)) {
                String[] themesArr = themesString2.split("&");
                for (String createWithString : themesArr) {
                    themeInfo6 = ThemeInfo.createWithString(createWithString);
                    if (themeInfo6 != null) {
                        otherThemes.add(themeInfo6);
                        themes.add(themeInfo6);
                        themesDict.put(themeInfo6.getKey(), themeInfo6);
                    }
                }
                saveOtherThemes(true, true);
                themeConfig.edit().remove("themes").commit();
            }
        }
        sortThemes();
        ThemeInfo applyingTheme = null;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        try {
            ThemeInfo themeDarkBlue3 = themesDict.get("Dark Blue");
            String theme3 = preferences.getString("theme", (String) null);
            if ("Default".equals(theme3)) {
                try {
                    applyingTheme = themesDict.get("Blue");
                    applyingTheme.currentAccentId = DEFALT_THEME_ACCENT_ID;
                } catch (Exception e2) {
                    e = e2;
                    ThemeInfo themeInfo7 = themeInfo6;
                    String str = themesString2;
                    int i3 = remoteVersion2;
                }
            } else if ("Dark".equals(theme3)) {
                applyingTheme = themeDarkBlue3;
                applyingTheme.currentAccentId = 9;
            } else if (!(theme3 == null || (applyingTheme = themesDict.get(theme3)) == null || themeConfig.contains("lastDayTheme"))) {
                SharedPreferences.Editor editor = themeConfig.edit();
                editor.putString("lastDayTheme", applyingTheme.getKey());
                editor.commit();
            }
            String theme4 = preferences.getString("nighttheme", (String) null);
            if ("Default".equals(theme4)) {
                applyingTheme = themesDict.get("Blue");
                applyingTheme.currentAccentId = DEFALT_THEME_ACCENT_ID;
            } else if ("Dark".equals(theme4)) {
                currentNightTheme = themeDarkBlue3;
                themeDarkBlue3.currentAccentId = 9;
            } else if (!(theme4 == null || (t = themesDict.get(theme4)) == null)) {
                currentNightTheme = t;
            }
            if (currentNightTheme != null) {
                if (!themeConfig.contains("lastDarkTheme")) {
                    SharedPreferences.Editor editor2 = themeConfig.edit();
                    editor2.putString("lastDarkTheme", currentNightTheme.getKey());
                    editor2.commit();
                }
            }
            SharedPreferences.Editor oldEditorNew = null;
            SharedPreferences.Editor oldEditor = null;
            for (ThemeInfo info : themesDict.values()) {
                if (info.assetName == null || info.accentBaseColor == 0) {
                    themeInfo = themeInfo6;
                    themesString = themesString2;
                    remoteVersion = remoteVersion2;
                    i = appRemoteThemesVersion;
                    themeDarkBlue = themeDarkBlue3;
                    theme = theme4;
                } else {
                    String accents = themeConfig.getString("accents_" + info.assetName, (String) null);
                    StringBuilder sb3 = new StringBuilder();
                    themeInfo = themeInfo6;
                    try {
                        sb3.append("accent_current_");
                        sb3.append(info.assetName);
                        String sb4 = sb3.toString();
                        if (info.firstAccentIsDefault) {
                            try {
                                i2 = DEFALT_THEME_ACCENT_ID;
                            } catch (Exception e3) {
                                e = e3;
                                String str2 = themesString2;
                                int i4 = remoteVersion2;
                                int i5 = appRemoteThemesVersion;
                            }
                        } else {
                            i2 = 0;
                        }
                        info.currentAccentId = themeConfig.getInt(sb4, i2);
                        ArrayList arrayList2 = new ArrayList();
                        if (!TextUtils.isEmpty(accents)) {
                            try {
                                themesString = themesString2;
                                remoteVersion = remoteVersion2;
                                try {
                                    SerializedData data = new SerializedData(Base64.decode(accents, 3));
                                    int version3 = data.readInt32(true);
                                    int count = data.readInt32(true);
                                    String str3 = accents;
                                    int a3 = 0;
                                    while (a3 < count) {
                                        try {
                                            ThemeAccent accent = new ThemeAccent();
                                            int count2 = count;
                                            int appRemoteThemesVersion2 = appRemoteThemesVersion;
                                            try {
                                                accent.id = data.readInt32(true);
                                                accent.accentColor = data.readInt32(true);
                                                version = version3;
                                                if (version >= 9) {
                                                    themeDarkBlue2 = themeDarkBlue3;
                                                    try {
                                                        accent.accentColor2 = data.readInt32(true);
                                                    } catch (Throwable th) {
                                                        e = th;
                                                        int i6 = version;
                                                        String str4 = theme4;
                                                    }
                                                } else {
                                                    themeDarkBlue2 = themeDarkBlue3;
                                                }
                                            } catch (Throwable th2) {
                                                e = th2;
                                                String str5 = theme4;
                                                int i7 = version3;
                                                ThemeInfo themeInfo8 = themeDarkBlue3;
                                                try {
                                                    throw new RuntimeException(e);
                                                } catch (Throwable th3) {
                                                    e = th3;
                                                    try {
                                                        FileLog.e(e);
                                                        throw new RuntimeException(e);
                                                    } catch (Exception e4) {
                                                        e = e4;
                                                        FileLog.e((Throwable) e);
                                                        throw new RuntimeException(e);
                                                    }
                                                }
                                            }
                                            try {
                                                accent.parentTheme = info;
                                                accent.myMessagesAccentColor = data.readInt32(true);
                                                accent.myMessagesGradientAccentColor1 = data.readInt32(true);
                                                if (version >= 7) {
                                                    accent.myMessagesGradientAccentColor2 = data.readInt32(true);
                                                    accent.myMessagesGradientAccentColor3 = data.readInt32(true);
                                                }
                                                if (version >= 8) {
                                                    accent.myMessagesAnimated = data.readBool(true);
                                                }
                                                if (version >= 3) {
                                                    theme2 = theme4;
                                                    try {
                                                        accent.backgroundOverrideColor = data.readInt64(true);
                                                        z = true;
                                                    } catch (Throwable th4) {
                                                        e = th4;
                                                        int i8 = version;
                                                        throw new RuntimeException(e);
                                                    }
                                                } else {
                                                    theme2 = theme4;
                                                    z = true;
                                                    accent.backgroundOverrideColor = (long) data.readInt32(true);
                                                }
                                                if (version >= 2) {
                                                    accent.backgroundGradientOverrideColor1 = data.readInt64(z);
                                                    z2 = true;
                                                } else {
                                                    z2 = true;
                                                    accent.backgroundGradientOverrideColor1 = (long) data.readInt32(true);
                                                }
                                                if (version >= 6) {
                                                    accent.backgroundGradientOverrideColor2 = data.readInt64(z2);
                                                    accent.backgroundGradientOverrideColor3 = data.readInt64(z2);
                                                }
                                                if (version >= 1) {
                                                    accent.backgroundRotation = data.readInt32(true);
                                                }
                                                if (version >= 4) {
                                                    data.readInt64(true);
                                                    accent.patternIntensity = (float) data.readDouble(true);
                                                    accent.patternMotion = data.readBool(true);
                                                    if (version >= 5) {
                                                        accent.patternSlug = data.readString(true);
                                                    }
                                                }
                                                if (version >= 5 && data.readBool(true)) {
                                                    accent.account = data.readInt32(true);
                                                    accent.info = TLRPC.Theme.TLdeserialize(data, data.readInt32(true), true);
                                                }
                                                if (accent.info != null) {
                                                    accent.isDefault = accent.info.isDefault;
                                                }
                                                try {
                                                    info.themeAccentsMap.put(accent.id, accent);
                                                    if (accent.info != null) {
                                                        version2 = version;
                                                        try {
                                                            info.accentsByThemeId.put(accent.info.id, accent);
                                                        } catch (Throwable th5) {
                                                            e = th5;
                                                            throw new RuntimeException(e);
                                                        }
                                                    } else {
                                                        version2 = version;
                                                    }
                                                    arrayList2.add(accent);
                                                    info.lastAccentId = Math.max(info.lastAccentId, accent.id);
                                                    a3++;
                                                    themeDarkBlue3 = themeDarkBlue2;
                                                    count = count2;
                                                    appRemoteThemesVersion = appRemoteThemesVersion2;
                                                    theme4 = theme2;
                                                    version3 = version2;
                                                } catch (Throwable th6) {
                                                    e = th6;
                                                    int i9 = version;
                                                    throw new RuntimeException(e);
                                                }
                                            } catch (Throwable th7) {
                                                e = th7;
                                                int i10 = version;
                                                String str6 = theme4;
                                                throw new RuntimeException(e);
                                            }
                                        } catch (Throwable th8) {
                                            e = th8;
                                            int i11 = count;
                                            int i12 = appRemoteThemesVersion;
                                            String str7 = theme4;
                                            int i13 = version3;
                                            ThemeInfo themeInfo9 = themeDarkBlue3;
                                            throw new RuntimeException(e);
                                        }
                                    }
                                    int i14 = count;
                                    i = appRemoteThemesVersion;
                                    theme = theme4;
                                    int i15 = version3;
                                    themeDarkBlue = themeDarkBlue3;
                                } catch (Throwable th9) {
                                    e = th9;
                                    String str8 = accents;
                                    int i16 = appRemoteThemesVersion;
                                    ThemeInfo themeInfo10 = themeDarkBlue3;
                                    String str9 = theme4;
                                    FileLog.e(e);
                                    throw new RuntimeException(e);
                                }
                            } catch (Throwable th10) {
                                e = th10;
                                String str10 = accents;
                                String str11 = themesString2;
                                int i17 = remoteVersion2;
                                int i18 = appRemoteThemesVersion;
                                ThemeInfo themeInfo11 = themeDarkBlue3;
                                String str12 = theme4;
                                FileLog.e(e);
                                throw new RuntimeException(e);
                            }
                        } else {
                            themesString = themesString2;
                            remoteVersion = remoteVersion2;
                            i = appRemoteThemesVersion;
                            themeDarkBlue = themeDarkBlue3;
                            theme = theme4;
                            String key = "accent_for_" + info.assetName;
                            int oldAccentColor = preferences.getInt(key, 0);
                            if (oldAccentColor != 0) {
                                if (oldEditor == null) {
                                    oldEditor = preferences.edit();
                                    oldEditorNew = themeConfig.edit();
                                }
                                oldEditor.remove(key);
                                boolean found = false;
                                int a4 = 0;
                                int N = info.themeAccents.size();
                                while (true) {
                                    if (a4 >= N) {
                                        break;
                                    }
                                    ThemeAccent accent2 = info.themeAccents.get(a4);
                                    if (accent2.accentColor == oldAccentColor) {
                                        info.currentAccentId = accent2.id;
                                        found = true;
                                        break;
                                    }
                                    a4++;
                                }
                                if (!found) {
                                    ThemeAccent accent3 = new ThemeAccent();
                                    accent3.id = 100;
                                    accent3.accentColor = oldAccentColor;
                                    accent3.parentTheme = info;
                                    info.themeAccentsMap.put(accent3.id, accent3);
                                    arrayList2.add(0, accent3);
                                    info.currentAccentId = 100;
                                    info.lastAccentId = 101;
                                    SerializedData data2 = new SerializedData(72);
                                    data2.writeInt32(9);
                                    data2.writeInt32(1);
                                    data2.writeInt32(accent3.id);
                                    data2.writeInt32(accent3.accentColor);
                                    data2.writeInt32(accent3.myMessagesAccentColor);
                                    data2.writeInt32(accent3.myMessagesGradientAccentColor1);
                                    data2.writeInt32(accent3.myMessagesGradientAccentColor2);
                                    data2.writeInt32(accent3.myMessagesGradientAccentColor3);
                                    data2.writeBool(accent3.myMessagesAnimated);
                                    data2.writeInt64(accent3.backgroundOverrideColor);
                                    data2.writeInt64(accent3.backgroundGradientOverrideColor1);
                                    data2.writeInt64(accent3.backgroundGradientOverrideColor2);
                                    data2.writeInt64(accent3.backgroundGradientOverrideColor3);
                                    data2.writeInt32(accent3.backgroundRotation);
                                    data2.writeInt64(0);
                                    data2.writeDouble((double) accent3.patternIntensity);
                                    data2.writeBool(accent3.patternMotion);
                                    data2.writeString(accent3.patternSlug);
                                    data2.writeBool(false);
                                    int i19 = oldAccentColor;
                                    oldEditorNew.putString("accents_" + info.assetName, Base64.encodeToString(data2.toByteArray(), 3));
                                }
                                oldEditorNew.putInt("accent_current_" + info.assetName, info.currentAccentId);
                            }
                        }
                        if (!arrayList2.isEmpty()) {
                            info.themeAccents.addAll(0, arrayList2);
                            sortAccents(info);
                        }
                        if (info.themeAccentsMap != null && info.themeAccentsMap.get(info.currentAccentId) == null) {
                            info.currentAccentId = info.firstAccentIsDefault ? DEFALT_THEME_ACCENT_ID : 0;
                        }
                        info.loadWallpapers(themeConfig);
                        ThemeAccent accent4 = info.getAccent(false);
                        if (accent4 != null) {
                            info.overrideWallpaper = accent4.overrideWallpaper;
                        }
                    } catch (Exception e5) {
                        e = e5;
                        String str13 = themesString2;
                        int i20 = remoteVersion2;
                        int i21 = appRemoteThemesVersion;
                        FileLog.e((Throwable) e);
                        throw new RuntimeException(e);
                    }
                }
                themeInfo6 = themeInfo;
                themesString2 = themesString;
                remoteVersion2 = remoteVersion;
                themeDarkBlue3 = themeDarkBlue;
                appRemoteThemesVersion = i;
                theme4 = theme;
            }
            String str14 = themesString2;
            int i22 = remoteVersion2;
            int i23 = appRemoteThemesVersion;
            ThemeInfo themeInfo12 = themeDarkBlue3;
            String str15 = theme4;
            int i24 = 3;
            if (oldEditor != null) {
                oldEditor.commit();
                oldEditorNew.commit();
            }
            if (Build.VERSION.SDK_INT < 29) {
                i24 = 0;
            }
            selectedAutoNightType = preferences.getInt("selectedAutoNightType", i24);
            autoNightScheduleByLocation = preferences.getBoolean("autoNightScheduleByLocation", false);
            autoNightBrighnessThreshold = preferences.getFloat("autoNightBrighnessThreshold", 0.25f);
            autoNightDayStartTime = preferences.getInt("autoNightDayStartTime", 1320);
            autoNightDayEndTime = preferences.getInt("autoNightDayEndTime", 480);
            autoNightSunsetTime = preferences.getInt("autoNightSunsetTime", 1320);
            autoNightSunriseTime = preferences.getInt("autoNightSunriseTime", 480);
            autoNightCityName = preferences.getString("autoNightCityName", "");
            long val = preferences.getLong("autoNightLocationLatitude3", 10000);
            if (val != 10000) {
                autoNightLocationLatitude = Double.longBitsToDouble(val);
            } else {
                autoNightLocationLatitude = 10000.0d;
            }
            long val2 = preferences.getLong("autoNightLocationLongitude3", 10000);
            if (val2 != 10000) {
                autoNightLocationLongitude = Double.longBitsToDouble(val2);
            } else {
                autoNightLocationLongitude = 10000.0d;
            }
            autoNightLastSunCheckDay = preferences.getInt("autoNightLastSunCheckDay", -1);
            if (applyingTheme == null) {
                applyingTheme = defaultTheme;
            } else {
                currentDayTheme = applyingTheme;
            }
            if (preferences.contains("overrideThemeWallpaper") || preferences.contains("selectedBackground2")) {
                boolean override = preferences.getBoolean("overrideThemeWallpaper", false);
                long id = preferences.getLong("selectedBackground2", 1000001);
                if (id == -1 || !(!override || id == -2 || id == 1000001)) {
                    OverrideWallpaperInfo overrideWallpaper = new OverrideWallpaperInfo();
                    overrideWallpaper.color = preferences.getInt("selectedColor", 0);
                    overrideWallpaper.slug = preferences.getString("selectedBackgroundSlug", "");
                    if (id < -100 || id > -1 || overrideWallpaper.color == 0) {
                        overrideWallpaper.fileName = "wallpaper.jpg";
                        overrideWallpaper.originalFileName = "wallpaper_original.jpg";
                    } else {
                        overrideWallpaper.slug = "c";
                        overrideWallpaper.fileName = "";
                        overrideWallpaper.originalFileName = "";
                    }
                    overrideWallpaper.gradientColor1 = preferences.getInt("selectedGradientColor", 0);
                    overrideWallpaper.gradientColor2 = preferences.getInt("selectedGradientColor2", 0);
                    overrideWallpaper.gradientColor3 = preferences.getInt("selectedGradientColor3", 0);
                    overrideWallpaper.rotation = preferences.getInt("selectedGradientRotation", 45);
                    overrideWallpaper.isBlurred = preferences.getBoolean("selectedBackgroundBlurred", false);
                    overrideWallpaper.isMotion = preferences.getBoolean("selectedBackgroundMotion", false);
                    overrideWallpaper.intensity = preferences.getFloat("selectedIntensity", 0.5f);
                    currentDayTheme.setOverrideWallpaper(overrideWallpaper);
                    if (selectedAutoNightType != 0) {
                        currentNightTheme.setOverrideWallpaper(overrideWallpaper);
                    }
                }
                preferences.edit().remove("overrideThemeWallpaper").remove("selectedBackground2").commit();
            }
            int switchToTheme = needSwitchToTheme();
            if (switchToTheme == 2) {
                applyingTheme = currentNightTheme;
            }
            applyTheme(applyingTheme, false, false, switchToTheme == 2);
            AndroidUtilities.runOnUIThread(MessagesController$$ExternalSyntheticLambda114.INSTANCE);
            SharedPreferences preferences2 = ApplicationLoader.applicationContext.getSharedPreferences("emojithemes_config", 0);
            int count3 = preferences2.getInt("count", 0);
            final ArrayList<ChatThemeBottomSheet.ChatThemeItem> previewItems = new ArrayList<>();
            previewItems.add(new ChatThemeBottomSheet.ChatThemeItem(EmojiThemes.createHomePreviewTheme()));
            for (int i25 = 0; i25 < count3; i25++) {
                SerializedData serializedData = new SerializedData(Utilities.hexToBytes(preferences2.getString("theme_" + i25, "")));
                try {
                    EmojiThemes fullTheme = EmojiThemes.createPreviewFullTheme(TLRPC.Theme.TLdeserialize(serializedData, serializedData.readInt32(true), true));
                    if (fullTheme.items.size() >= 4) {
                        try {
                            previewItems.add(new ChatThemeBottomSheet.ChatThemeItem(fullTheme));
                        } catch (Throwable th11) {
                            e = th11;
                            FileLog.e(e);
                        }
                    }
                    ChatThemeController.chatThemeQueue.postRunnable(new Runnable() {
                        public void run() {
                            for (int i = 0; i < previewItems.size(); i++) {
                                ((ChatThemeBottomSheet.ChatThemeItem) previewItems.get(i)).chatTheme.loadPreviewColors(0);
                            }
                            AndroidUtilities.runOnUIThread(new Theme$3$$ExternalSyntheticLambda0(previewItems));
                        }

                        static /* synthetic */ void lambda$run$0(ArrayList previewItems) {
                            Theme.defaultEmojiThemes.clear();
                            Theme.defaultEmojiThemes.addAll(previewItems);
                        }
                    });
                } catch (Throwable th12) {
                    e = th12;
                    FileLog.e(e);
                }
            }
        } catch (Exception e6) {
            e = e6;
            ThemeInfo themeInfo13 = themeInfo6;
            String str16 = themesString2;
            int i26 = remoteVersion2;
            FileLog.e((Throwable) e);
            throw new RuntimeException(e);
        }
    }

    /* access modifiers changed from: private */
    public static void sortAccents(ThemeInfo info) {
        Collections.sort(info.themeAccents, Theme$$ExternalSyntheticLambda9.INSTANCE);
    }

    static /* synthetic */ int lambda$sortAccents$0(ThemeAccent o1, ThemeAccent o2) {
        if (isHome(o1)) {
            return -1;
        }
        if (isHome(o2)) {
            return 1;
        }
        int i1 = o1.isDefault;
        int i2 = o2.isDefault;
        if (i1 == i2) {
            if (o1.isDefault) {
                if (o1.id > o2.id) {
                    return 1;
                }
                if (o1.id < o2.id) {
                    return -1;
                }
                return 0;
            } else if (o1.id > o2.id) {
                return -1;
            } else {
                if (o1.id < o2.id) {
                    return 1;
                }
                return 0;
            }
        } else if (i1 > i2) {
            return -1;
        } else {
            return 1;
        }
    }

    public static void saveAutoNightThemeConfig() {
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("selectedAutoNightType", selectedAutoNightType);
        editor.putBoolean("autoNightScheduleByLocation", autoNightScheduleByLocation);
        editor.putFloat("autoNightBrighnessThreshold", autoNightBrighnessThreshold);
        editor.putInt("autoNightDayStartTime", autoNightDayStartTime);
        editor.putInt("autoNightDayEndTime", autoNightDayEndTime);
        editor.putInt("autoNightSunriseTime", autoNightSunriseTime);
        editor.putString("autoNightCityName", autoNightCityName);
        editor.putInt("autoNightSunsetTime", autoNightSunsetTime);
        editor.putLong("autoNightLocationLatitude3", Double.doubleToRawLongBits(autoNightLocationLatitude));
        editor.putLong("autoNightLocationLongitude3", Double.doubleToRawLongBits(autoNightLocationLongitude));
        editor.putInt("autoNightLastSunCheckDay", autoNightLastSunCheckDay);
        ThemeInfo themeInfo = currentNightTheme;
        if (themeInfo != null) {
            editor.putString("nighttheme", themeInfo.getKey());
        } else {
            editor.remove("nighttheme");
        }
        editor.commit();
    }

    /* access modifiers changed from: private */
    public static Drawable getStateDrawable(Drawable drawable, int index) {
        if (Build.VERSION.SDK_INT >= 29 && (drawable instanceof StateListDrawable)) {
            return ((StateListDrawable) drawable).getStateDrawable(index);
        }
        if (StateListDrawable_getStateDrawableMethod == null) {
            Class<StateListDrawable> cls = StateListDrawable.class;
            try {
                StateListDrawable_getStateDrawableMethod = cls.getDeclaredMethod("getStateDrawable", new Class[]{Integer.TYPE});
            } catch (Throwable th) {
            }
        }
        Method method = StateListDrawable_getStateDrawableMethod;
        if (method == null) {
            return null;
        }
        try {
            return (Drawable) method.invoke(drawable, new Object[]{Integer.valueOf(index)});
        } catch (Exception e) {
            return null;
        }
    }

    public static Drawable createEmojiIconSelectorDrawable(Context context, int resource, int defaultColor, int pressedColor) {
        Resources resources = context.getResources();
        Drawable defaultDrawable = resources.getDrawable(resource).mutate();
        if (defaultColor != 0) {
            defaultDrawable.setColorFilter(new PorterDuffColorFilter(defaultColor, PorterDuff.Mode.MULTIPLY));
        }
        Drawable pressedDrawable = resources.getDrawable(resource).mutate();
        if (pressedColor != 0) {
            pressedDrawable.setColorFilter(new PorterDuffColorFilter(pressedColor, PorterDuff.Mode.MULTIPLY));
        }
        StateListDrawable stateListDrawable = new StateListDrawable() {
            public boolean selectDrawable(int index) {
                if (Build.VERSION.SDK_INT >= 21) {
                    return super.selectDrawable(index);
                }
                Drawable drawable = Theme.getStateDrawable(this, index);
                ColorFilter colorFilter = null;
                if (drawable instanceof BitmapDrawable) {
                    colorFilter = ((BitmapDrawable) drawable).getPaint().getColorFilter();
                } else if (drawable instanceof NinePatchDrawable) {
                    colorFilter = ((NinePatchDrawable) drawable).getPaint().getColorFilter();
                }
                boolean result = super.selectDrawable(index);
                if (colorFilter != null) {
                    drawable.setColorFilter(colorFilter);
                }
                return result;
            }
        };
        stateListDrawable.setEnterFadeDuration(1);
        stateListDrawable.setExitFadeDuration(200);
        stateListDrawable.addState(new int[]{16842913}, pressedDrawable);
        stateListDrawable.addState(new int[0], defaultDrawable);
        return stateListDrawable;
    }

    public static Drawable createEditTextDrawable(Context context, boolean alert) {
        return createEditTextDrawable(context, getColor(alert ? "dialogInputField" : "windowBackgroundWhiteInputField"), getColor(alert ? "dialogInputFieldActivated" : "windowBackgroundWhiteInputFieldActivated"));
    }

    public static Drawable createEditTextDrawable(Context context, int color, int colorActivated) {
        Resources resources = context.getResources();
        Drawable defaultDrawable = resources.getDrawable(NUM).mutate();
        defaultDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        Drawable pressedDrawable = resources.getDrawable(NUM).mutate();
        pressedDrawable.setColorFilter(new PorterDuffColorFilter(colorActivated, PorterDuff.Mode.MULTIPLY));
        StateListDrawable stateListDrawable = new StateListDrawable() {
            public boolean selectDrawable(int index) {
                if (Build.VERSION.SDK_INT >= 21) {
                    return super.selectDrawable(index);
                }
                Drawable drawable = Theme.getStateDrawable(this, index);
                ColorFilter colorFilter = null;
                if (drawable instanceof BitmapDrawable) {
                    colorFilter = ((BitmapDrawable) drawable).getPaint().getColorFilter();
                } else if (drawable instanceof NinePatchDrawable) {
                    colorFilter = ((NinePatchDrawable) drawable).getPaint().getColorFilter();
                }
                boolean result = super.selectDrawable(index);
                if (colorFilter != null) {
                    drawable.setColorFilter(colorFilter);
                }
                return result;
            }
        };
        stateListDrawable.addState(new int[]{16842910, 16842908}, pressedDrawable);
        stateListDrawable.addState(new int[]{16842908}, pressedDrawable);
        stateListDrawable.addState(StateSet.WILD_CARD, defaultDrawable);
        return stateListDrawable;
    }

    public static boolean canStartHolidayAnimation() {
        return canStartHolidayAnimation;
    }

    public static int getEventType() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int monthOfYear = calendar.get(2);
        int dayOfMonth = calendar.get(5);
        int i = calendar.get(12);
        int hour = calendar.get(11);
        if ((monthOfYear == 11 && dayOfMonth >= 24 && dayOfMonth <= 31) || (monthOfYear == 0 && dayOfMonth == 1)) {
            return 0;
        }
        if (monthOfYear == 1 && dayOfMonth == 14) {
            return 1;
        }
        if ((monthOfYear != 9 || dayOfMonth < 30) && (monthOfYear != 10 || dayOfMonth != 1 || hour >= 12)) {
            return -1;
        }
        return 2;
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
            int r5 = r0.get(r4)
            r6 = 1
            if (r1 != 0) goto L_0x0043
            if (r2 != r6) goto L_0x0043
            r7 = 10
            if (r3 > r7) goto L_0x0043
            if (r5 != 0) goto L_0x0043
            canStartHolidayAnimation = r6
            goto L_0x0046
        L_0x0043:
            r7 = 0
            canStartHolidayAnimation = r7
        L_0x0046:
            android.graphics.drawable.Drawable r7 = dialogs_holidayDrawable
            if (r7 != 0) goto L_0x0080
            if (r1 != r4) goto L_0x005b
            boolean r4 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            r7 = 31
            if (r4 == 0) goto L_0x0055
            r4 = 29
            goto L_0x0057
        L_0x0055:
            r4 = 31
        L_0x0057:
            if (r2 < r4) goto L_0x005b
            if (r2 <= r7) goto L_0x005f
        L_0x005b:
            if (r1 != 0) goto L_0x0080
            if (r2 != r6) goto L_0x0080
        L_0x005f:
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.res.Resources r4 = r4.getResources()
            r6 = 2131165908(0x7var_d4, float:1.7946046E38)
            android.graphics.drawable.Drawable r4 = r4.getDrawable(r6)
            dialogs_holidayDrawable = r4
            r4 = 1077936128(0x40400000, float:3.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = -r4
            dialogs_holidayDrawableOffsetX = r4
            r4 = 1065353216(0x3var_, float:1.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = -r4
            dialogs_holidayDrawableOffsetY = r4
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

    public static Drawable createSimpleSelectorDrawable(Context context, int resource, int defaultColor, int pressedColor) {
        Resources resources = context.getResources();
        Drawable defaultDrawable = resources.getDrawable(resource).mutate();
        if (defaultColor != 0) {
            defaultDrawable.setColorFilter(new PorterDuffColorFilter(defaultColor, PorterDuff.Mode.MULTIPLY));
        }
        Drawable pressedDrawable = resources.getDrawable(resource).mutate();
        if (pressedColor != 0) {
            pressedDrawable.setColorFilter(new PorterDuffColorFilter(pressedColor, PorterDuff.Mode.MULTIPLY));
        }
        StateListDrawable stateListDrawable = new StateListDrawable() {
            public boolean selectDrawable(int index) {
                if (Build.VERSION.SDK_INT >= 21) {
                    return super.selectDrawable(index);
                }
                Drawable drawable = Theme.getStateDrawable(this, index);
                ColorFilter colorFilter = null;
                if (drawable instanceof BitmapDrawable) {
                    colorFilter = ((BitmapDrawable) drawable).getPaint().getColorFilter();
                } else if (drawable instanceof NinePatchDrawable) {
                    colorFilter = ((NinePatchDrawable) drawable).getPaint().getColorFilter();
                }
                boolean result = super.selectDrawable(index);
                if (colorFilter != null) {
                    drawable.setColorFilter(colorFilter);
                }
                return result;
            }
        };
        stateListDrawable.addState(new int[]{16842919}, pressedDrawable);
        stateListDrawable.addState(new int[]{16842913}, pressedDrawable);
        stateListDrawable.addState(StateSet.WILD_CARD, defaultDrawable);
        return stateListDrawable;
    }

    public static ShapeDrawable createCircleDrawable(int size, int color) {
        OvalShape ovalShape = new OvalShape();
        ovalShape.resize((float) size, (float) size);
        ShapeDrawable defaultDrawable = new ShapeDrawable(ovalShape);
        defaultDrawable.setIntrinsicWidth(size);
        defaultDrawable.setIntrinsicHeight(size);
        defaultDrawable.getPaint().setColor(color);
        return defaultDrawable;
    }

    public static CombinedDrawable createCircleDrawableWithIcon(int size, int iconRes) {
        return createCircleDrawableWithIcon(size, iconRes, 0);
    }

    public static CombinedDrawable createCircleDrawableWithIcon(int size, int iconRes, int stroke) {
        Drawable drawable;
        if (iconRes != 0) {
            drawable = ApplicationLoader.applicationContext.getResources().getDrawable(iconRes).mutate();
        } else {
            drawable = null;
        }
        return createCircleDrawableWithIcon(size, drawable, stroke);
    }

    public static CombinedDrawable createCircleDrawableWithIcon(int size, Drawable drawable, int stroke) {
        OvalShape ovalShape = new OvalShape();
        ovalShape.resize((float) size, (float) size);
        ShapeDrawable defaultDrawable = new ShapeDrawable(ovalShape);
        Paint paint = defaultDrawable.getPaint();
        paint.setColor(-1);
        if (stroke == 1) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        } else if (stroke == 2) {
            paint.setAlpha(0);
        }
        CombinedDrawable combinedDrawable = new CombinedDrawable(defaultDrawable, drawable);
        combinedDrawable.setCustomSize(size, size);
        return combinedDrawable;
    }

    public static Drawable createRoundRectDrawableWithIcon(int rad, int iconRes) {
        ShapeDrawable defaultDrawable = new ShapeDrawable(new RoundRectShape(new float[]{(float) rad, (float) rad, (float) rad, (float) rad, (float) rad, (float) rad, (float) rad, (float) rad}, (RectF) null, (float[]) null));
        defaultDrawable.getPaint().setColor(-1);
        return new CombinedDrawable(defaultDrawable, ApplicationLoader.applicationContext.getResources().getDrawable(iconRes).mutate());
    }

    public static int getWallpaperColor(int color) {
        if (color == 0) {
            return 0;
        }
        return -16777216 | color;
    }

    public static float getThemeIntensity(float value) {
        if (value >= 0.0f || getActiveTheme().isDark()) {
            return value;
        }
        return -value;
    }

    public static void setCombinedDrawableColor(Drawable combinedDrawable, int color, boolean isIcon) {
        Drawable drawable;
        if (combinedDrawable instanceof CombinedDrawable) {
            if (isIcon) {
                drawable = ((CombinedDrawable) combinedDrawable).getIcon();
            } else {
                drawable = ((CombinedDrawable) combinedDrawable).getBackground();
            }
            if (drawable instanceof ColorDrawable) {
                ((ColorDrawable) drawable).setColor(color);
            } else {
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
            }
        }
    }

    public static Drawable createSimpleSelectorCircleDrawable(int size, int defaultColor, int pressedColor) {
        OvalShape ovalShape = new OvalShape();
        ovalShape.resize((float) size, (float) size);
        ShapeDrawable defaultDrawable = new ShapeDrawable(ovalShape);
        defaultDrawable.getPaint().setColor(defaultColor);
        ShapeDrawable pressedDrawable = new ShapeDrawable(ovalShape);
        if (Build.VERSION.SDK_INT >= 21) {
            pressedDrawable.getPaint().setColor(-1);
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{pressedColor}), defaultDrawable, pressedDrawable);
        }
        pressedDrawable.getPaint().setColor(pressedColor);
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, pressedDrawable);
        stateListDrawable.addState(new int[]{16842908}, pressedDrawable);
        stateListDrawable.addState(StateSet.WILD_CARD, defaultDrawable);
        return stateListDrawable;
    }

    public static Drawable createRoundRectDrawable(int rad, int defaultColor) {
        ShapeDrawable defaultDrawable = new ShapeDrawable(new RoundRectShape(new float[]{(float) rad, (float) rad, (float) rad, (float) rad, (float) rad, (float) rad, (float) rad, (float) rad}, (RectF) null, (float[]) null));
        defaultDrawable.getPaint().setColor(defaultColor);
        return defaultDrawable;
    }

    public static Drawable createServiceDrawable(int rad, View view, View containerView) {
        return createServiceDrawable(rad, view, containerView, chat_actionBackgroundPaint);
    }

    public static Drawable createServiceDrawable(final int rad, final View view, final View containerView, final Paint backgroundPaint) {
        return new Drawable() {
            private RectF rect = new RectF();

            public void draw(Canvas canvas) {
                Rect bounds = getBounds();
                this.rect.set((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom);
                Theme.applyServiceShaderMatrixForView(view, containerView);
                RectF rectF = this.rect;
                int i = rad;
                canvas.drawRoundRect(rectF, (float) i, (float) i, backgroundPaint);
                if (Theme.hasGradientService()) {
                    RectF rectF2 = this.rect;
                    int i2 = rad;
                    canvas.drawRoundRect(rectF2, (float) i2, (float) i2, Theme.chat_actionBackgroundGradientDarkenPaint);
                }
            }

            public void setAlpha(int alpha) {
            }

            public void setColorFilter(ColorFilter colorFilter) {
            }

            public int getOpacity() {
                return -2;
            }
        };
    }

    public static Drawable createSimpleSelectorRoundRectDrawable(int rad, int defaultColor, int pressedColor) {
        return createSimpleSelectorRoundRectDrawable(rad, defaultColor, pressedColor, pressedColor);
    }

    public static Drawable createSimpleSelectorRoundRectDrawable(int rad, int defaultColor, int pressedColor, int maskColor) {
        int i = rad;
        ShapeDrawable defaultDrawable = new ShapeDrawable(new RoundRectShape(new float[]{(float) i, (float) i, (float) i, (float) i, (float) i, (float) i, (float) i, (float) i}, (RectF) null, (float[]) null));
        defaultDrawable.getPaint().setColor(defaultColor);
        ShapeDrawable pressedDrawable = new ShapeDrawable(new RoundRectShape(new float[]{(float) i, (float) i, (float) i, (float) i, (float) i, (float) i, (float) i, (float) i}, (RectF) null, (float[]) null));
        pressedDrawable.getPaint().setColor(maskColor);
        if (Build.VERSION.SDK_INT >= 21) {
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{pressedColor}), defaultDrawable, pressedDrawable);
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, pressedDrawable);
        stateListDrawable.addState(new int[]{16842913}, pressedDrawable);
        stateListDrawable.addState(StateSet.WILD_CARD, defaultDrawable);
        return stateListDrawable;
    }

    public static Drawable createSelectorDrawableFromDrawables(Drawable normal, Drawable pressed) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, pressed);
        stateListDrawable.addState(new int[]{16842913}, pressed);
        stateListDrawable.addState(StateSet.WILD_CARD, normal);
        return stateListDrawable;
    }

    public static Drawable getRoundRectSelectorDrawable(int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{NUM | (16777215 & color)}), (Drawable) null, createRoundRectDrawable(AndroidUtilities.dp(3.0f), -1));
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, createRoundRectDrawable(AndroidUtilities.dp(3.0f), (color & 16777215) | NUM));
        stateListDrawable.addState(new int[]{16842913}, createRoundRectDrawable(AndroidUtilities.dp(3.0f), NUM | (16777215 & color)));
        stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(0));
        return stateListDrawable;
    }

    public static Drawable createSelectorWithBackgroundDrawable(int backgroundColor, int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{color}), new ColorDrawable(backgroundColor), new ColorDrawable(backgroundColor));
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, new ColorDrawable(color));
        stateListDrawable.addState(new int[]{16842913}, new ColorDrawable(color));
        stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(backgroundColor));
        return stateListDrawable;
    }

    public static Drawable getSelectorDrawable(boolean whiteBackground) {
        return getSelectorDrawable(getColor("listSelectorSDK21"), whiteBackground);
    }

    public static Drawable getSelectorDrawable(int color, boolean whiteBackground) {
        if (whiteBackground) {
            return getSelectorDrawable(color, "windowBackgroundWhite");
        }
        return createSelectorDrawable(color, 2);
    }

    public static Drawable getSelectorDrawable(int color, String backgroundColor) {
        if (backgroundColor == null) {
            return createSelectorDrawable(color, 2);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{color}), new ColorDrawable(getColor(backgroundColor)), new ColorDrawable(-1));
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, new ColorDrawable(color));
        stateListDrawable.addState(new int[]{16842913}, new ColorDrawable(color));
        stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(getColor(backgroundColor)));
        return stateListDrawable;
    }

    public static Drawable createSelectorDrawable(int color) {
        return createSelectorDrawable(color, 1, -1);
    }

    public static Drawable createSelectorDrawable(int color, int maskType) {
        return createSelectorDrawable(color, maskType, -1);
    }

    public static Drawable createSelectorDrawable(int color, final int maskType, int radius) {
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable maskDrawable = null;
            if ((maskType == 1 || maskType == 5) && Build.VERSION.SDK_INT >= 23) {
                maskDrawable = null;
            } else if (maskType == 1 || maskType == 3 || maskType == 4 || maskType == 5 || maskType == 6 || maskType == 7) {
                maskPaint.setColor(-1);
                maskDrawable = new Drawable() {
                    RectF rect;

                    public void draw(Canvas canvas) {
                        int rad;
                        Rect bounds = getBounds();
                        int i = maskType;
                        if (i == 7) {
                            if (this.rect == null) {
                                this.rect = new RectF();
                            }
                            this.rect.set(bounds);
                            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), Theme.maskPaint);
                            return;
                        }
                        if (i == 1 || i == 6) {
                            rad = AndroidUtilities.dp(20.0f);
                        } else if (i == 3) {
                            rad = Math.max(bounds.width(), bounds.height()) / 2;
                        } else {
                            rad = (int) Math.ceil(Math.sqrt((double) (((bounds.left - bounds.centerX()) * (bounds.left - bounds.centerX())) + ((bounds.top - bounds.centerY()) * (bounds.top - bounds.centerY())))));
                        }
                        canvas.drawCircle((float) bounds.centerX(), (float) bounds.centerY(), (float) rad, Theme.maskPaint);
                    }

                    public void setAlpha(int alpha) {
                    }

                    public void setColorFilter(ColorFilter colorFilter) {
                    }

                    public int getOpacity() {
                        return 0;
                    }
                };
            } else if (maskType == 2) {
                maskDrawable = new ColorDrawable(-1);
            }
            RippleDrawable rippleDrawable = new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{color}), (Drawable) null, maskDrawable);
            if (Build.VERSION.SDK_INT >= 23) {
                if (maskType == 1) {
                    rippleDrawable.setRadius(radius <= 0 ? AndroidUtilities.dp(20.0f) : radius);
                } else if (maskType == 5) {
                    rippleDrawable.setRadius(-1);
                }
            }
            return rippleDrawable;
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, new ColorDrawable(color));
        stateListDrawable.addState(new int[]{16842913}, new ColorDrawable(color));
        stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(0));
        return stateListDrawable;
    }

    public static Drawable createCircleSelectorDrawable(int color, final int leftInset, final int rightInset) {
        if (Build.VERSION.SDK_INT >= 21) {
            maskPaint.setColor(-1);
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{color}), (Drawable) null, new Drawable() {
                public void draw(Canvas canvas) {
                    Rect bounds = getBounds();
                    canvas.drawCircle((float) ((bounds.centerX() - leftInset) + rightInset), (float) bounds.centerY(), (float) ((Math.max(bounds.width(), bounds.height()) / 2) + leftInset + rightInset), Theme.maskPaint);
                }

                public void setAlpha(int alpha) {
                }

                public void setColorFilter(ColorFilter colorFilter) {
                }

                public int getOpacity() {
                    return 0;
                }
            });
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, new ColorDrawable(color));
        stateListDrawable.addState(new int[]{16842913}, new ColorDrawable(color));
        stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(0));
        return stateListDrawable;
    }

    public static class RippleRadMaskDrawable extends Drawable {
        private int bottomRad;
        private Path path = new Path();
        private float[] radii = new float[8];
        private RectF rect = new RectF();
        private int topRad;

        public RippleRadMaskDrawable(int top, int bottom) {
            this.topRad = top;
            this.bottomRad = bottom;
        }

        public void setRadius(int top, int bottom) {
            this.topRad = top;
            this.bottomRad = bottom;
            invalidateSelf();
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

        public void setAlpha(int alpha) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public int getOpacity() {
            return 0;
        }
    }

    public static void setMaskDrawableRad(Drawable rippleDrawable, int top, int bottom) {
        if (Build.VERSION.SDK_INT >= 21 && (rippleDrawable instanceof RippleDrawable)) {
            RippleDrawable drawable = (RippleDrawable) rippleDrawable;
            int count = drawable.getNumberOfLayers();
            for (int a = 0; a < count; a++) {
                if (drawable.getDrawable(a) instanceof RippleRadMaskDrawable) {
                    drawable.setDrawableByLayerId(16908334, new RippleRadMaskDrawable(top, bottom));
                    return;
                }
            }
        }
    }

    public static Drawable createRadSelectorDrawable(int color, int topRad, int bottomRad) {
        if (Build.VERSION.SDK_INT >= 21) {
            maskPaint.setColor(-1);
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{color}), (Drawable) null, new RippleRadMaskDrawable(topRad, bottomRad));
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, new ColorDrawable(color));
        stateListDrawable.addState(new int[]{16842913}, new ColorDrawable(color));
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
        Collections.sort(themes, Theme$$ExternalSyntheticLambda10.INSTANCE);
    }

    static /* synthetic */ int lambda$sortThemes$1(ThemeInfo o1, ThemeInfo o2) {
        if (o1.pathToFile == null && o1.assetName == null) {
            return -1;
        }
        if (o2.pathToFile == null && o2.assetName == null) {
            return 1;
        }
        return o1.name.compareTo(o2.name);
    }

    public static void applyThemeTemporary(ThemeInfo themeInfo, boolean accent) {
        previousTheme = getCurrentTheme();
        hasPreviousTheme = true;
        isApplyingAccent = accent;
        applyTheme(themeInfo, false, false, false);
    }

    public static boolean hasCustomWallpaper() {
        return isApplyingAccent && currentTheme.overrideWallpaper != null;
    }

    public static boolean isCustomWallpaperColor() {
        return hasCustomWallpaper() && currentTheme.overrideWallpaper.color != 0;
    }

    public static void resetCustomWallpaper(boolean temporary) {
        if (temporary) {
            isApplyingAccent = false;
            reloadWallpaper();
            return;
        }
        currentTheme.setOverrideWallpaper((OverrideWallpaperInfo) null);
    }

    public static ThemeInfo fillThemeValues(File file, String themeName, TLRPC.TL_theme theme) {
        String[] modes;
        try {
            ThemeInfo themeInfo = new ThemeInfo();
            try {
                themeInfo.name = themeName;
                try {
                    themeInfo.info = theme;
                    themeInfo.pathToFile = file.getAbsolutePath();
                    themeInfo.account = UserConfig.selectedAccount;
                    String[] wallpaperLink = new String[1];
                    checkIsDark(getThemeFileValues(new File(themeInfo.pathToFile), (String) null, wallpaperLink), themeInfo);
                    if (!TextUtils.isEmpty(wallpaperLink[0])) {
                        String link = wallpaperLink[0];
                        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                        themeInfo.pathToWallpaper = new File(filesDirFixed, Utilities.MD5(link) + ".wp").getAbsolutePath();
                        try {
                            Uri data = Uri.parse(link);
                            themeInfo.slug = data.getQueryParameter("slug");
                            String mode = data.getQueryParameter("mode");
                            if (!(mode == null || (modes = mode.toLowerCase().split(" ")) == null || modes.length <= 0)) {
                                for (int a = 0; a < modes.length; a++) {
                                    if ("blur".equals(modes[a])) {
                                        themeInfo.isBlured = true;
                                    } else if ("motion".equals(modes[a])) {
                                        themeInfo.isMotion = true;
                                    }
                                }
                            }
                            String intensity = data.getQueryParameter("intensity");
                            if (!TextUtils.isEmpty(intensity)) {
                                try {
                                    String bgColor = data.getQueryParameter("bg_color");
                                    if (!TextUtils.isEmpty(bgColor)) {
                                        themeInfo.patternBgColor = Integer.parseInt(bgColor.substring(0, 6), 16) | -16777216;
                                        if (bgColor.length() >= 13 && AndroidUtilities.isValidWallChar(bgColor.charAt(6))) {
                                            themeInfo.patternBgGradientColor1 = Integer.parseInt(bgColor.substring(7, 13), 16) | -16777216;
                                        }
                                        if (bgColor.length() >= 20 && AndroidUtilities.isValidWallChar(bgColor.charAt(13))) {
                                            themeInfo.patternBgGradientColor2 = Integer.parseInt(bgColor.substring(14, 20), 16) | -16777216;
                                        }
                                        if (bgColor.length() == 27 && AndroidUtilities.isValidWallChar(bgColor.charAt(20))) {
                                            themeInfo.patternBgGradientColor3 = Integer.parseInt(bgColor.substring(21), 16) | -16777216;
                                        }
                                    }
                                } catch (Exception e) {
                                }
                                try {
                                    String rotation = data.getQueryParameter("rotation");
                                    if (!TextUtils.isEmpty(rotation)) {
                                        themeInfo.patternBgGradientRotation = Utilities.parseInt(rotation).intValue();
                                    }
                                } catch (Exception e2) {
                                }
                                if (!TextUtils.isEmpty(intensity)) {
                                    themeInfo.patternIntensity = Utilities.parseInt(intensity).intValue();
                                }
                                if (themeInfo.patternIntensity == 0) {
                                    themeInfo.patternIntensity = 50;
                                }
                            }
                        } catch (Throwable e3) {
                            FileLog.e(e3);
                        }
                    } else {
                        themedWallpaperLink = null;
                    }
                    return themeInfo;
                } catch (Exception e4) {
                    e = e4;
                    FileLog.e((Throwable) e);
                    return null;
                }
            } catch (Exception e5) {
                e = e5;
                TLRPC.TL_theme tL_theme = theme;
                FileLog.e((Throwable) e);
                return null;
            }
        } catch (Exception e6) {
            e = e6;
            String str = themeName;
            TLRPC.TL_theme tL_theme2 = theme;
            FileLog.e((Throwable) e);
            return null;
        }
    }

    public static ThemeInfo applyThemeFile(File file, String themeName, TLRPC.TL_theme theme, boolean temporary) {
        String key;
        File finalFile;
        try {
            if (!themeName.toLowerCase().endsWith(".attheme")) {
                themeName = themeName + ".attheme";
            }
            if (temporary) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.goingToPreviewTheme, new Object[0]);
                ThemeInfo themeInfo = new ThemeInfo();
                themeInfo.name = themeName;
                themeInfo.info = theme;
                themeInfo.pathToFile = file.getAbsolutePath();
                themeInfo.account = UserConfig.selectedAccount;
                applyThemeTemporary(themeInfo, false);
                return themeInfo;
            }
            if (theme != null) {
                key = "remote" + theme.id;
                finalFile = new File(ApplicationLoader.getFilesDirFixed(), key + ".attheme");
            } else {
                key = themeName;
                finalFile = new File(ApplicationLoader.getFilesDirFixed(), key);
            }
            if (!AndroidUtilities.copyFile(file, finalFile)) {
                applyPreviousTheme();
                return null;
            }
            previousTheme = null;
            hasPreviousTheme = false;
            isApplyingAccent = false;
            ThemeInfo themeInfo2 = themesDict.get(key);
            if (themeInfo2 == null) {
                themeInfo2 = new ThemeInfo();
                themeInfo2.name = themeName;
                themeInfo2.account = UserConfig.selectedAccount;
                themes.add(themeInfo2);
                otherThemes.add(themeInfo2);
                sortThemes();
            } else {
                themesDict.remove(key);
            }
            themeInfo2.info = theme;
            themeInfo2.pathToFile = finalFile.getAbsolutePath();
            themesDict.put(themeInfo2.getKey(), themeInfo2);
            saveOtherThemes(true);
            applyTheme(themeInfo2, true, true, false);
            return themeInfo2;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    public static ThemeInfo getTheme(String key) {
        return themesDict.get(key);
    }

    public static void applyTheme(ThemeInfo themeInfo) {
        applyTheme(themeInfo, true, true, false);
    }

    public static void applyTheme(ThemeInfo themeInfo, boolean nightTheme) {
        applyTheme(themeInfo, true, nightTheme);
    }

    public static void applyTheme(ThemeInfo themeInfo, boolean save, boolean nightTheme) {
        applyTheme(themeInfo, save, true, nightTheme);
    }

    /* JADX WARNING: Removed duplicated region for block: B:104:0x01ea A[Catch:{ Exception -> 0x0201 }] */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x0209 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:119:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:41:0x00d0=Splitter:B:41:0x00d0, B:97:0x01d7=Splitter:B:97:0x01d7} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void applyTheme(org.telegram.ui.ActionBar.Theme.ThemeInfo r16, boolean r17, boolean r18, boolean r19) {
        /*
            r1 = r16
            r2 = r19
            if (r1 != 0) goto L_0x0007
            return
        L_0x0007:
            org.telegram.ui.Components.ThemeEditorView r3 = org.telegram.ui.Components.ThemeEditorView.getInstance()
            if (r3 == 0) goto L_0x0010
            r3.destroy()
        L_0x0010:
            r4 = 0
            java.lang.String r0 = r1.pathToFile     // Catch:{ Exception -> 0x0201 }
            java.lang.String r5 = "theme"
            r6 = 0
            if (r0 != 0) goto L_0x003e
            java.lang.String r0 = r1.assetName     // Catch:{ Exception -> 0x0201 }
            if (r0 == 0) goto L_0x001d
            goto L_0x003e
        L_0x001d:
            if (r2 != 0) goto L_0x002f
            if (r17 == 0) goto L_0x002f
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings()     // Catch:{ Exception -> 0x0201 }
            android.content.SharedPreferences$Editor r7 = r0.edit()     // Catch:{ Exception -> 0x0201 }
            r7.remove(r5)     // Catch:{ Exception -> 0x0201 }
            r7.commit()     // Catch:{ Exception -> 0x0201 }
        L_0x002f:
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = currentColorsNoAccent     // Catch:{ Exception -> 0x0201 }
            r0.clear()     // Catch:{ Exception -> 0x0201 }
            themedWallpaperFileOffset = r4     // Catch:{ Exception -> 0x0201 }
            themedWallpaperLink = r6     // Catch:{ Exception -> 0x0201 }
            wallpaper = r6     // Catch:{ Exception -> 0x0201 }
            themedWallpaper = r6     // Catch:{ Exception -> 0x0201 }
            goto L_0x01dc
        L_0x003e:
            if (r2 != 0) goto L_0x0054
            if (r17 == 0) goto L_0x0054
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings()     // Catch:{ Exception -> 0x0201 }
            android.content.SharedPreferences$Editor r7 = r0.edit()     // Catch:{ Exception -> 0x0201 }
            java.lang.String r8 = r16.getKey()     // Catch:{ Exception -> 0x0201 }
            r7.putString(r5, r8)     // Catch:{ Exception -> 0x0201 }
            r7.commit()     // Catch:{ Exception -> 0x0201 }
        L_0x0054:
            r5 = 1
            java.lang.String[] r0 = new java.lang.String[r5]     // Catch:{ Exception -> 0x0201 }
            r7 = r0
            java.lang.String r0 = r1.assetName     // Catch:{ Exception -> 0x0201 }
            if (r0 == 0) goto L_0x0065
            java.lang.String r0 = r1.assetName     // Catch:{ Exception -> 0x0201 }
            java.util.HashMap r0 = getThemeFileValues(r6, r0, r6)     // Catch:{ Exception -> 0x0201 }
            currentColorsNoAccent = r0     // Catch:{ Exception -> 0x0201 }
            goto L_0x0072
        L_0x0065:
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x0201 }
            java.lang.String r8 = r1.pathToFile     // Catch:{ Exception -> 0x0201 }
            r0.<init>(r8)     // Catch:{ Exception -> 0x0201 }
            java.util.HashMap r0 = getThemeFileValues(r0, r6, r7)     // Catch:{ Exception -> 0x0201 }
            currentColorsNoAccent = r0     // Catch:{ Exception -> 0x0201 }
        L_0x0072:
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = currentColorsNoAccent     // Catch:{ Exception -> 0x0201 }
            java.lang.String r8 = "wallpaperFileOffset"
            java.lang.Object r0 = r0.get(r8)     // Catch:{ Exception -> 0x0201 }
            java.lang.Integer r0 = (java.lang.Integer) r0     // Catch:{ Exception -> 0x0201 }
            r8 = r0
            if (r8 == 0) goto L_0x0084
            int r0 = r8.intValue()     // Catch:{ Exception -> 0x0201 }
            goto L_0x0085
        L_0x0084:
            r0 = -1
        L_0x0085:
            themedWallpaperFileOffset = r0     // Catch:{ Exception -> 0x0201 }
            r0 = r7[r4]     // Catch:{ Exception -> 0x0201 }
            boolean r0 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0201 }
            if (r0 != 0) goto L_0x01c7
            r0 = r7[r4]     // Catch:{ Exception -> 0x0201 }
            themedWallpaperLink = r0     // Catch:{ Exception -> 0x0201 }
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x0201 }
            java.io.File r6 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ Exception -> 0x0201 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0201 }
            r9.<init>()     // Catch:{ Exception -> 0x0201 }
            java.lang.String r10 = themedWallpaperLink     // Catch:{ Exception -> 0x0201 }
            java.lang.String r10 = org.telegram.messenger.Utilities.MD5(r10)     // Catch:{ Exception -> 0x0201 }
            r9.append(r10)     // Catch:{ Exception -> 0x0201 }
            java.lang.String r10 = ".wp"
            r9.append(r10)     // Catch:{ Exception -> 0x0201 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0201 }
            r0.<init>(r6, r9)     // Catch:{ Exception -> 0x0201 }
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ Exception -> 0x0201 }
            r6 = r0
            java.lang.String r0 = r1.pathToWallpaper     // Catch:{ Exception -> 0x00cf }
            if (r0 == 0) goto L_0x00ce
            java.lang.String r0 = r1.pathToWallpaper     // Catch:{ Exception -> 0x00cf }
            boolean r0 = r0.equals(r6)     // Catch:{ Exception -> 0x00cf }
            if (r0 != 0) goto L_0x00ce
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x00cf }
            java.lang.String r9 = r1.pathToWallpaper     // Catch:{ Exception -> 0x00cf }
            r0.<init>(r9)     // Catch:{ Exception -> 0x00cf }
            r0.delete()     // Catch:{ Exception -> 0x00cf }
        L_0x00ce:
            goto L_0x00d0
        L_0x00cf:
            r0 = move-exception
        L_0x00d0:
            r1.pathToWallpaper = r6     // Catch:{ Exception -> 0x0201 }
            java.lang.String r0 = themedWallpaperLink     // Catch:{ all -> 0x01c2 }
            android.net.Uri r0 = android.net.Uri.parse(r0)     // Catch:{ all -> 0x01c2 }
            r9 = r0
            java.lang.String r0 = "slug"
            java.lang.String r0 = r9.getQueryParameter(r0)     // Catch:{ all -> 0x01c2 }
            r1.slug = r0     // Catch:{ all -> 0x01c2 }
            java.lang.String r0 = "mode"
            java.lang.String r0 = r9.getQueryParameter(r0)     // Catch:{ all -> 0x01c2 }
            if (r0 == 0) goto L_0x011b
            java.lang.String r10 = r0.toLowerCase()     // Catch:{ all -> 0x01c2 }
            r0 = r10
            java.lang.String r10 = " "
            java.lang.String[] r10 = r0.split(r10)     // Catch:{ all -> 0x01c2 }
            if (r10 == 0) goto L_0x0119
            int r11 = r10.length     // Catch:{ all -> 0x01c2 }
            if (r11 <= 0) goto L_0x0119
            r11 = 0
        L_0x00fa:
            int r12 = r10.length     // Catch:{ all -> 0x01c2 }
            if (r11 >= r12) goto L_0x0119
            java.lang.String r12 = "blur"
            r13 = r10[r11]     // Catch:{ all -> 0x01c2 }
            boolean r12 = r12.equals(r13)     // Catch:{ all -> 0x01c2 }
            if (r12 == 0) goto L_0x010a
            r1.isBlured = r5     // Catch:{ all -> 0x01c2 }
            goto L_0x0116
        L_0x010a:
            java.lang.String r12 = "motion"
            r13 = r10[r11]     // Catch:{ all -> 0x01c2 }
            boolean r12 = r12.equals(r13)     // Catch:{ all -> 0x01c2 }
            if (r12 == 0) goto L_0x0116
            r1.isMotion = r5     // Catch:{ all -> 0x01c2 }
        L_0x0116:
            int r11 = r11 + 1
            goto L_0x00fa
        L_0x0119:
            r5 = r0
            goto L_0x011c
        L_0x011b:
            r5 = r0
        L_0x011c:
            java.lang.String r0 = "intensity"
            java.lang.String r0 = r9.getQueryParameter(r0)     // Catch:{ all -> 0x01c2 }
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ all -> 0x01c2 }
            int r0 = r0.intValue()     // Catch:{ all -> 0x01c2 }
            r10 = r0
            r0 = 45
            r1.patternBgGradientRotation = r0     // Catch:{ all -> 0x01c2 }
            java.lang.String r0 = "bg_color"
            java.lang.String r0 = r9.getQueryParameter(r0)     // Catch:{ Exception -> 0x01a8 }
            boolean r11 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x01a8 }
            if (r11 != 0) goto L_0x01a7
            r11 = 6
            java.lang.String r12 = r0.substring(r4, r11)     // Catch:{ Exception -> 0x01a8 }
            r13 = 16
            int r12 = java.lang.Integer.parseInt(r12, r13)     // Catch:{ Exception -> 0x01a8 }
            r14 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r12 = r12 | r14
            r1.patternBgColor = r12     // Catch:{ Exception -> 0x01a8 }
            int r12 = r0.length()     // Catch:{ Exception -> 0x01a8 }
            r15 = 13
            if (r12 < r15) goto L_0x0169
            char r11 = r0.charAt(r11)     // Catch:{ Exception -> 0x01a8 }
            boolean r11 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r11)     // Catch:{ Exception -> 0x01a8 }
            if (r11 == 0) goto L_0x0169
            r11 = 7
            java.lang.String r11 = r0.substring(r11, r15)     // Catch:{ Exception -> 0x01a8 }
            int r11 = java.lang.Integer.parseInt(r11, r13)     // Catch:{ Exception -> 0x01a8 }
            r11 = r11 | r14
            r1.patternBgGradientColor1 = r11     // Catch:{ Exception -> 0x01a8 }
        L_0x0169:
            int r11 = r0.length()     // Catch:{ Exception -> 0x01a8 }
            r12 = 20
            if (r11 < r12) goto L_0x0188
            char r11 = r0.charAt(r15)     // Catch:{ Exception -> 0x01a8 }
            boolean r11 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r11)     // Catch:{ Exception -> 0x01a8 }
            if (r11 == 0) goto L_0x0188
            r11 = 14
            java.lang.String r11 = r0.substring(r11, r12)     // Catch:{ Exception -> 0x01a8 }
            int r11 = java.lang.Integer.parseInt(r11, r13)     // Catch:{ Exception -> 0x01a8 }
            r11 = r11 | r14
            r1.patternBgGradientColor2 = r11     // Catch:{ Exception -> 0x01a8 }
        L_0x0188:
            int r11 = r0.length()     // Catch:{ Exception -> 0x01a8 }
            r15 = 27
            if (r11 != r15) goto L_0x01a7
            char r11 = r0.charAt(r12)     // Catch:{ Exception -> 0x01a8 }
            boolean r11 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r11)     // Catch:{ Exception -> 0x01a8 }
            if (r11 == 0) goto L_0x01a7
            r11 = 21
            java.lang.String r11 = r0.substring(r11)     // Catch:{ Exception -> 0x01a8 }
            int r11 = java.lang.Integer.parseInt(r11, r13)     // Catch:{ Exception -> 0x01a8 }
            r11 = r11 | r14
            r1.patternBgGradientColor3 = r11     // Catch:{ Exception -> 0x01a8 }
        L_0x01a7:
            goto L_0x01a9
        L_0x01a8:
            r0 = move-exception
        L_0x01a9:
            java.lang.String r0 = "rotation"
            java.lang.String r0 = r9.getQueryParameter(r0)     // Catch:{ Exception -> 0x01c0 }
            boolean r11 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x01c0 }
            if (r11 != 0) goto L_0x01bf
            java.lang.Integer r11 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x01c0 }
            int r11 = r11.intValue()     // Catch:{ Exception -> 0x01c0 }
            r1.patternBgGradientRotation = r11     // Catch:{ Exception -> 0x01c0 }
        L_0x01bf:
            goto L_0x01c1
        L_0x01c0:
            r0 = move-exception
        L_0x01c1:
            goto L_0x01c6
        L_0x01c2:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x0201 }
        L_0x01c6:
            goto L_0x01db
        L_0x01c7:
            java.lang.String r0 = r1.pathToWallpaper     // Catch:{ Exception -> 0x01d6 }
            if (r0 == 0) goto L_0x01d5
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x01d6 }
            java.lang.String r5 = r1.pathToWallpaper     // Catch:{ Exception -> 0x01d6 }
            r0.<init>(r5)     // Catch:{ Exception -> 0x01d6 }
            r0.delete()     // Catch:{ Exception -> 0x01d6 }
        L_0x01d5:
            goto L_0x01d7
        L_0x01d6:
            r0 = move-exception
        L_0x01d7:
            r1.pathToWallpaper = r6     // Catch:{ Exception -> 0x0201 }
            themedWallpaperLink = r6     // Catch:{ Exception -> 0x0201 }
        L_0x01db:
        L_0x01dc:
            if (r2 != 0) goto L_0x01fb
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = previousTheme     // Catch:{ Exception -> 0x0201 }
            if (r0 != 0) goto L_0x01fb
            currentDayTheme = r1     // Catch:{ Exception -> 0x0201 }
            boolean r0 = isCurrentThemeNight()     // Catch:{ Exception -> 0x0201 }
            if (r0 == 0) goto L_0x01fb
            r0 = 2000(0x7d0, float:2.803E-42)
            switchNightThemeDelay = r0     // Catch:{ Exception -> 0x0201 }
            long r5 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0201 }
            lastDelayUpdateTime = r5     // Catch:{ Exception -> 0x0201 }
            org.telegram.messenger.MessagesController$$ExternalSyntheticLambda114 r0 = org.telegram.messenger.MessagesController$$ExternalSyntheticLambda114.INSTANCE     // Catch:{ Exception -> 0x0201 }
            r5 = 2100(0x834, double:1.0375E-320)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r5)     // Catch:{ Exception -> 0x0201 }
        L_0x01fb:
            currentTheme = r1     // Catch:{ Exception -> 0x0201 }
            refreshThemeColors()     // Catch:{ Exception -> 0x0201 }
            goto L_0x0205
        L_0x0201:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0205:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = previousTheme
            if (r0 != 0) goto L_0x021c
            if (r17 == 0) goto L_0x021c
            boolean r0 = switchingNightTheme
            if (r0 != 0) goto L_0x021c
            int r0 = r1.account
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r5 = r1.getAccent(r4)
            r0.saveTheme(r1, r5, r2, r4)
        L_0x021c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.applyTheme(org.telegram.ui.ActionBar.Theme$ThemeInfo, boolean, boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    public static boolean useBlackText(int color1, int color2) {
        float r1 = ((float) Color.red(color1)) / 255.0f;
        float g1 = ((float) Color.green(color1)) / 255.0f;
        float b1 = ((float) Color.blue(color1)) / 255.0f;
        return ((((r1 * 0.5f) + ((((float) Color.red(color2)) / 255.0f) * 0.5f)) * 0.2126f) + (((g1 * 0.5f) + ((((float) Color.green(color2)) / 255.0f) * 0.5f)) * 0.7152f)) + (((b1 * 0.5f) + (0.5f * (((float) Color.blue(color2)) / 255.0f))) * 0.0722f) > 0.705f || ((0.2126f * r1) + (0.7152f * g1)) + (0.0722f * b1) > 0.705f;
    }

    public static void refreshThemeColors() {
        refreshThemeColors(false, false);
    }

    public static void refreshThemeColors(boolean bg, boolean messages) {
        currentColors.clear();
        currentColors.putAll(currentColorsNoAccent);
        shouldDrawGradientIcons = true;
        ThemeAccent accent = currentTheme.getAccent(false);
        if (accent != null) {
            shouldDrawGradientIcons = accent.fillAccentColors(currentColorsNoAccent, currentColors);
        }
        if (!messages) {
            reloadWallpaper();
        }
        applyCommonTheme();
        applyDialogsTheme();
        applyProfileTheme();
        applyChatTheme(false, bg);
        AndroidUtilities.runOnUIThread(new Theme$$ExternalSyntheticLambda8(true ^ hasPreviousTheme));
    }

    public static int changeColorAccent(ThemeInfo themeInfo, int accent, int color) {
        if (accent == 0 || themeInfo.accentBaseColor == 0 || accent == themeInfo.accentBaseColor || (themeInfo.firstAccentIsDefault && themeInfo.currentAccentId == DEFALT_THEME_ACCENT_ID)) {
            return color;
        }
        float[] hsvTemp3 = getTempHsv(3);
        float[] hsvTemp4 = getTempHsv(4);
        Color.colorToHSV(themeInfo.accentBaseColor, hsvTemp3);
        Color.colorToHSV(accent, hsvTemp4);
        return changeColorAccent(hsvTemp3, hsvTemp4, color, themeInfo.isDark());
    }

    /* access modifiers changed from: private */
    public static float[] getTempHsv(int num) {
        ThreadLocal<float[]> local;
        switch (num) {
            case 1:
                local = hsvTemp1Local;
                break;
            case 2:
                local = hsvTemp2Local;
                break;
            case 3:
                local = hsvTemp3Local;
                break;
            case 4:
                local = hsvTemp4Local;
                break;
            default:
                local = hsvTemp5Local;
                break;
        }
        float[] hsvTemp = local.get();
        if (hsvTemp != null) {
            return hsvTemp;
        }
        float[] hsvTemp2 = new float[3];
        local.set(hsvTemp2);
        return hsvTemp2;
    }

    /* access modifiers changed from: private */
    public static int getAccentColor(float[] baseHsv, int baseColor, int elementColor) {
        float[] hsvTemp3 = getTempHsv(3);
        float[] hsvTemp4 = getTempHsv(4);
        Color.colorToHSV(baseColor, hsvTemp3);
        Color.colorToHSV(elementColor, hsvTemp4);
        float dist = Math.min((hsvTemp3[1] * 1.5f) / baseHsv[1], 1.0f);
        hsvTemp3[0] = (hsvTemp4[0] - hsvTemp3[0]) + baseHsv[0];
        hsvTemp3[1] = (hsvTemp4[1] * baseHsv[1]) / hsvTemp3[1];
        hsvTemp3[2] = ((((hsvTemp4[2] / hsvTemp3[2]) + dist) - 1.0f) * baseHsv[2]) / dist;
        if (hsvTemp3[2] < 0.3f) {
            return elementColor;
        }
        return Color.HSVToColor(255, hsvTemp3);
    }

    public static int changeColorAccent(int color) {
        int i = 0;
        ThemeAccent accent = currentTheme.getAccent(false);
        ThemeInfo themeInfo = currentTheme;
        if (accent != null) {
            i = accent.accentColor;
        }
        return changeColorAccent(themeInfo, i, color);
    }

    public static int changeColorAccent(float[] baseHsv, float[] accentHsv, int color, boolean isDarkTheme) {
        float[] colorHsv = getTempHsv(5);
        Color.colorToHSV(color, colorHsv);
        boolean needRevertBrightness = false;
        if (Math.min(Math.abs(colorHsv[0] - baseHsv[0]), Math.abs((colorHsv[0] - baseHsv[0]) - 360.0f)) > 30.0f) {
            return color;
        }
        float dist = Math.min((colorHsv[1] * 1.5f) / baseHsv[1], 1.0f);
        colorHsv[0] = (colorHsv[0] + accentHsv[0]) - baseHsv[0];
        colorHsv[1] = (colorHsv[1] * accentHsv[1]) / baseHsv[1];
        colorHsv[2] = colorHsv[2] * ((1.0f - dist) + ((accentHsv[2] * dist) / baseHsv[2]));
        int newColor = Color.HSVToColor(Color.alpha(color), colorHsv);
        float origBrightness = AndroidUtilities.computePerceivedBrightness(color);
        float newBrightness = AndroidUtilities.computePerceivedBrightness(newColor);
        if (!isDarkTheme ? origBrightness < newBrightness : origBrightness > newBrightness) {
            needRevertBrightness = true;
        }
        if (needRevertBrightness) {
            return changeBrightness(newColor, (((1.0f - 0.6f) * origBrightness) / newBrightness) + 0.6f);
        }
        return newColor;
    }

    private static int changeBrightness(int color, float amount) {
        int r = (int) (((float) Color.red(color)) * amount);
        int g = (int) (((float) Color.green(color)) * amount);
        int b = (int) (((float) Color.blue(color)) * amount);
        int b2 = 0;
        int r2 = r < 0 ? 0 : Math.min(r, 255);
        int g2 = g < 0 ? 0 : Math.min(g, 255);
        if (b >= 0) {
            b2 = Math.min(b, 255);
        }
        return Color.argb(Color.alpha(color), r2, g2, b2);
    }

    public static void onUpdateThemeAccents() {
        refreshThemeColors();
    }

    public static boolean deleteThemeAccent(ThemeInfo theme, ThemeAccent accent, boolean save) {
        boolean z = false;
        if (accent == null || theme == null || theme.themeAccents == null) {
            return false;
        }
        boolean current = accent.id == theme.currentAccentId;
        File wallpaperFile = accent.getPathToWallpaper();
        if (wallpaperFile != null) {
            wallpaperFile.delete();
        }
        theme.themeAccentsMap.remove(accent.id);
        theme.themeAccents.remove(accent);
        if (accent.info != null) {
            theme.accentsByThemeId.remove(accent.info.id);
        }
        if (accent.overrideWallpaper != null) {
            accent.overrideWallpaper.delete();
        }
        if (current) {
            theme.setCurrentAccentId(theme.themeAccents.get(0).id);
        }
        if (save) {
            saveThemeAccents(theme, true, false, false, false);
            if (accent.info != null) {
                MessagesController instance = MessagesController.getInstance(accent.account);
                if (current && theme == currentNightTheme) {
                    z = true;
                }
                instance.saveTheme(theme, accent, z, true);
            }
        }
        return current;
    }

    public static void saveThemeAccents(ThemeInfo theme, boolean save, boolean remove, boolean indexOnly, boolean upload) {
        saveThemeAccents(theme, save, remove, indexOnly, upload, false);
    }

    public static void saveThemeAccents(ThemeInfo theme, boolean save, boolean remove, boolean indexOnly, boolean upload, boolean migration) {
        ThemeInfo themeInfo = theme;
        if (save) {
            SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
            if (!indexOnly) {
                int N = themeInfo.themeAccents.size();
                int count = Math.max(0, N - themeInfo.defaultAccentCount);
                SerializedData data = new SerializedData(((count * 16) + 2) * 4);
                data.writeInt32(9);
                data.writeInt32(count);
                for (int a = 0; a < N; a++) {
                    ThemeAccent accent = themeInfo.themeAccents.get(a);
                    if (accent.id >= 100) {
                        data.writeInt32(accent.id);
                        data.writeInt32(accent.accentColor);
                        data.writeInt32(accent.accentColor2);
                        data.writeInt32(accent.myMessagesAccentColor);
                        data.writeInt32(accent.myMessagesGradientAccentColor1);
                        data.writeInt32(accent.myMessagesGradientAccentColor2);
                        data.writeInt32(accent.myMessagesGradientAccentColor3);
                        data.writeBool(accent.myMessagesAnimated);
                        data.writeInt64(accent.backgroundOverrideColor);
                        data.writeInt64(accent.backgroundGradientOverrideColor1);
                        data.writeInt64(accent.backgroundGradientOverrideColor2);
                        data.writeInt64(accent.backgroundGradientOverrideColor3);
                        data.writeInt32(accent.backgroundRotation);
                        data.writeInt64(0);
                        data.writeDouble((double) accent.patternIntensity);
                        data.writeBool(accent.patternMotion);
                        data.writeString(accent.patternSlug);
                        data.writeBool(accent.info != null);
                        if (accent.info != null) {
                            data.writeInt32(accent.account);
                            accent.info.serializeToStream(data);
                        }
                    }
                }
                editor.putString("accents_" + themeInfo.assetName, Base64.encodeToString(data.toByteArray(), 3));
                if (!migration) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.themeAccentListUpdated, new Object[0]);
                }
                if (upload) {
                    MessagesController.getInstance(UserConfig.selectedAccount).saveThemeToServer(theme, theme.getAccent(false));
                }
            }
            editor.putInt("accent_current_" + themeInfo.assetName, themeInfo.currentAccentId);
            editor.commit();
        } else {
            if (themeInfo.prevAccentId != -1) {
                if (remove) {
                    ThemeAccent accent2 = themeInfo.themeAccentsMap.get(themeInfo.currentAccentId);
                    themeInfo.themeAccentsMap.remove(accent2.id);
                    themeInfo.themeAccents.remove(accent2);
                    if (accent2.info != null) {
                        themeInfo.accentsByThemeId.remove(accent2.info.id);
                    }
                }
                themeInfo.currentAccentId = themeInfo.prevAccentId;
                ThemeAccent accent3 = theme.getAccent(false);
                if (accent3 != null) {
                    themeInfo.overrideWallpaper = accent3.overrideWallpaper;
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
    public static void saveOtherThemes(boolean full) {
        saveOtherThemes(full, false);
    }

    private static void saveOtherThemes(boolean full, boolean migration) {
        String key;
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
        if (full) {
            JSONArray array = new JSONArray();
            for (int a = 0; a < otherThemes.size(); a++) {
                JSONObject jsonObject = otherThemes.get(a).getSaveJson();
                if (jsonObject != null) {
                    array.put(jsonObject);
                }
            }
            editor.putString("themes2", array.toString());
        }
        int a2 = 0;
        while (a2 < 3) {
            StringBuilder sb = new StringBuilder();
            sb.append("2remoteThemesHash");
            Object obj = "";
            sb.append(a2 != 0 ? Integer.valueOf(a2) : obj);
            editor.putLong(sb.toString(), remoteThemesHash[a2]);
            StringBuilder sb2 = new StringBuilder();
            sb2.append("lastLoadingThemesTime");
            if (a2 != 0) {
                obj = Integer.valueOf(a2);
            }
            sb2.append(obj);
            editor.putInt(sb2.toString(), lastLoadingThemesTime[a2]);
            a2++;
        }
        editor.putInt("lastLoadingCurrentThemeTime", lastLoadingCurrentThemeTime);
        editor.commit();
        if (full) {
            for (int b = 0; b < 5; b++) {
                switch (b) {
                    case 0:
                        key = "Blue";
                        break;
                    case 1:
                        key = "Dark Blue";
                        break;
                    case 2:
                        key = "Arctic Blue";
                        break;
                    case 3:
                        key = "Day";
                        break;
                    default:
                        key = "Night";
                        break;
                }
                ThemeInfo info = themesDict.get(key);
                if (!(info == null || info.themeAccents == null || info.themeAccents.isEmpty())) {
                    saveThemeAccents(info, true, false, false, false, migration);
                }
            }
        }
    }

    public static HashMap<String, Integer> getDefaultColors() {
        return defaultColors;
    }

    public static ThemeInfo getPreviousTheme() {
        return previousTheme;
    }

    public static String getCurrentThemeName() {
        String text = currentDayTheme.getName();
        if (text.toLowerCase().endsWith(".attheme")) {
            return text.substring(0, text.lastIndexOf(46));
        }
        return text;
    }

    public static String getCurrentNightThemeName() {
        ThemeInfo themeInfo = currentNightTheme;
        if (themeInfo == null) {
            return "";
        }
        String text = themeInfo.getName();
        if (text.toLowerCase().endsWith(".attheme")) {
            return text.substring(0, text.lastIndexOf(46));
        }
        return text;
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
        if (Math.abs(lastThemeSwitchTime - SystemClock.elapsedRealtime()) >= 12000) {
            return 1800;
        }
        return 12000;
    }

    public static void setCurrentNightTheme(ThemeInfo theme) {
        boolean apply = currentTheme == currentNightTheme;
        currentNightTheme = theme;
        if (apply) {
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
        int day;
        int timeStart;
        int i = selectedAutoNightType;
        if (i == 1) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int time = (calendar.get(11) * 60) + calendar.get(12);
            if (autoNightScheduleByLocation) {
                int day2 = calendar.get(5);
                if (autoNightLastSunCheckDay != day2) {
                    double d = autoNightLocationLatitude;
                    if (d != 10000.0d) {
                        double d2 = autoNightLocationLongitude;
                        if (d2 != 10000.0d) {
                            int[] t = SunDate.calculateSunriseSunset(d, d2);
                            autoNightSunriseTime = t[0];
                            autoNightSunsetTime = t[1];
                            autoNightLastSunCheckDay = day2;
                            saveAutoNightThemeConfig();
                        }
                    }
                }
                timeStart = autoNightSunsetTime;
                day = autoNightSunriseTime;
            } else {
                timeStart = autoNightDayStartTime;
                day = autoNightDayEndTime;
            }
            return timeStart < day ? (timeStart > time || time > day) ? 1 : 2 : ((timeStart > time || time > 1440) && (time < 0 || time > day)) ? 1 : 2;
        }
        if (i == 2) {
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
        } else if (i == 3) {
            switch (ApplicationLoader.applicationContext.getResources().getConfiguration().uiMode & 48) {
                case 0:
                case 16:
                    return 1;
                case 32:
                    return 2;
            }
        } else if (i == 0) {
            return 1;
        }
        return 0;
    }

    public static void setChangingWallpaper(boolean value) {
        changingWallpaper = value;
        if (!value) {
            checkAutoNightThemeConditions(false);
        }
    }

    public static void checkAutoNightThemeConditions(boolean force) {
        if (previousTheme == null && !changingWallpaper) {
            if (!force && switchNightThemeDelay > 0) {
                long newTime = SystemClock.elapsedRealtime();
                lastDelayUpdateTime = newTime;
                int i = (int) (((long) switchNightThemeDelay) - (newTime - lastDelayUpdateTime));
                switchNightThemeDelay = i;
                if (i > 0) {
                    return;
                }
            }
            boolean z = false;
            if (force) {
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
            int switchToTheme = needSwitchToTheme();
            if (switchToTheme != 0) {
                if (switchToTheme == 2) {
                    z = true;
                }
                applyDayNightThemeMaybe(z);
            }
            if (force) {
                lastThemeSwitchTime = 0;
            }
        }
    }

    public static void applyDayNightThemeMaybe(boolean night) {
        if (previousTheme == null) {
            if (night) {
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
        if (themeInfo.pathToFile == null) {
            return false;
        }
        boolean currentThemeDeleted = false;
        if (currentTheme == themeInfo) {
            applyTheme(defaultTheme, true, false, false);
            currentThemeDeleted = true;
        }
        if (themeInfo == currentNightTheme) {
            currentNightTheme = themesDict.get("Dark Blue");
        }
        themeInfo.removeObservers();
        otherThemes.remove(themeInfo);
        themesDict.remove(themeInfo.name);
        if (themeInfo.overrideWallpaper != null) {
            themeInfo.overrideWallpaper.delete();
        }
        themes.remove(themeInfo);
        new File(themeInfo.pathToFile).delete();
        saveOtherThemes(true);
        return currentThemeDeleted;
    }

    public static ThemeInfo createNewTheme(String name) {
        ThemeInfo newTheme = new ThemeInfo();
        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
        newTheme.pathToFile = new File(filesDirFixed, "theme" + Utilities.random.nextLong() + ".attheme").getAbsolutePath();
        newTheme.name = name;
        themedWallpaperLink = getWallpaperUrl(currentTheme.overrideWallpaper);
        newTheme.account = UserConfig.selectedAccount;
        saveCurrentTheme(newTheme, true, true, false);
        return newTheme;
    }

    private static String getWallpaperUrl(OverrideWallpaperInfo wallpaperInfo) {
        String color4;
        String color2;
        String color3;
        String color42 = null;
        if (wallpaperInfo == null || TextUtils.isEmpty(wallpaperInfo.slug) || wallpaperInfo.slug.equals("d")) {
            return null;
        }
        StringBuilder modes = new StringBuilder();
        if (wallpaperInfo.isBlurred) {
            modes.append("blur");
        }
        if (wallpaperInfo.isMotion) {
            if (modes.length() > 0) {
                modes.append("+");
            }
            modes.append("motion");
        }
        if (wallpaperInfo.color == 0) {
            color4 = "https://attheme.org?slug=" + wallpaperInfo.slug;
        } else {
            String color = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (wallpaperInfo.color >> 16)) & 255), Integer.valueOf(((byte) (wallpaperInfo.color >> 8)) & 255), Byte.valueOf((byte) (wallpaperInfo.color & 255))}).toLowerCase();
            if (wallpaperInfo.gradientColor1 != 0) {
                color2 = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (wallpaperInfo.gradientColor1 >> 16)) & 255), Integer.valueOf(((byte) (wallpaperInfo.gradientColor1 >> 8)) & 255), Byte.valueOf((byte) (wallpaperInfo.gradientColor1 & 255))}).toLowerCase();
            } else {
                color2 = null;
            }
            if (wallpaperInfo.gradientColor2 != 0) {
                color3 = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (wallpaperInfo.gradientColor2 >> 16)) & 255), Integer.valueOf(((byte) (wallpaperInfo.gradientColor2 >> 8)) & 255), Byte.valueOf((byte) (wallpaperInfo.gradientColor2 & 255))}).toLowerCase();
            } else {
                color3 = null;
            }
            if (wallpaperInfo.gradientColor3 != 0) {
                color42 = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (wallpaperInfo.gradientColor3 >> 16)) & 255), Integer.valueOf(((byte) (wallpaperInfo.gradientColor3 >> 8)) & 255), Byte.valueOf((byte) (wallpaperInfo.gradientColor3 & 255))}).toLowerCase();
            }
            if (color2 == null || color3 == null) {
                if (color2 != null) {
                    color = (color + "-" + color2) + "&rotation=" + wallpaperInfo.rotation;
                }
            } else if (color42 != null) {
                color = color + "~" + color2 + "~" + color3 + "~" + color42;
            } else {
                color = color + "~" + color2 + "~" + color3;
            }
            color4 = "https://attheme.org?slug=" + wallpaperInfo.slug + "&intensity=" + ((int) (wallpaperInfo.intensity * 100.0f)) + "&bg_color=" + color;
        }
        if (modes.length() <= 0) {
            return color4;
        }
        return color4 + "&mode=" + modes.toString();
    }

    public static void saveCurrentTheme(ThemeInfo themeInfo, boolean finalSave, boolean newTheme, boolean upload) {
        String wallpaperLink;
        ThemeInfo themeInfo2 = themeInfo;
        OverrideWallpaperInfo wallpaperInfo = themeInfo2.overrideWallpaper;
        if (wallpaperInfo != null) {
            wallpaperLink = getWallpaperUrl(wallpaperInfo);
        } else {
            wallpaperLink = themedWallpaperLink;
        }
        Drawable wallpaperToSave = newTheme ? wallpaper : themedWallpaper;
        if (newTheme && wallpaperToSave != null) {
            themedWallpaper = wallpaper;
        }
        ThemeAccent accent = currentTheme.getAccent(false);
        HashMap<String, Integer> colorsMap = (!currentTheme.firstAccentIsDefault || accent.id != DEFALT_THEME_ACCENT_ID) ? currentColors : defaultColors;
        StringBuilder result = new StringBuilder();
        if (colorsMap != defaultColors) {
            int outBubbleColor = accent != null ? accent.myMessagesAccentColor : 0;
            int outBubbleGradient1 = accent != null ? accent.myMessagesGradientAccentColor1 : 0;
            int outBubbleGradient2 = accent != null ? accent.myMessagesGradientAccentColor2 : 0;
            int outBubbleGradient3 = accent != null ? accent.myMessagesGradientAccentColor3 : 0;
            if (!(outBubbleColor == 0 || outBubbleGradient1 == 0)) {
                colorsMap.put("chat_outBubble", Integer.valueOf(outBubbleColor));
                colorsMap.put("chat_outBubbleGradient", Integer.valueOf(outBubbleGradient1));
                if (outBubbleGradient2 != 0) {
                    colorsMap.put("chat_outBubbleGradient2", Integer.valueOf(outBubbleGradient2));
                    if (outBubbleGradient3 != 0) {
                        colorsMap.put("chat_outBubbleGradient3", Integer.valueOf(outBubbleGradient3));
                    }
                }
                colorsMap.put("chat_outBubbleGradientAnimated", Integer.valueOf((accent == null || !accent.myMessagesAnimated) ? 0 : 1));
            }
        }
        for (Map.Entry<String, Integer> entry : colorsMap.entrySet()) {
            String key = entry.getKey();
            if ((!(wallpaperToSave instanceof BitmapDrawable) && wallpaperLink == null) || (!"chat_wallpaper".equals(key) && !"chat_wallpaper_gradient_to".equals(key) && !"key_chat_wallpaper_gradient_to2".equals(key) && !"key_chat_wallpaper_gradient_to3".equals(key))) {
                result.append(key);
                result.append("=");
                result.append(entry.getValue());
                result.append("\n");
            }
        }
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(themeInfo2.pathToFile);
            if (result.length() == 0 && !(wallpaperToSave instanceof BitmapDrawable) && TextUtils.isEmpty(wallpaperLink)) {
                result.append(' ');
            }
            stream.write(AndroidUtilities.getStringBytes(result.toString()));
            if (!TextUtils.isEmpty(wallpaperLink)) {
                stream.write(AndroidUtilities.getStringBytes("WLS=" + wallpaperLink + "\n"));
                if (newTheme) {
                    try {
                        Bitmap bitmap = ((BitmapDrawable) wallpaperToSave).getBitmap();
                        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                        FileOutputStream wallpaperStream = new FileOutputStream(new File(filesDirFixed, Utilities.MD5(wallpaperLink) + ".wp"));
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 87, wallpaperStream);
                        wallpaperStream.close();
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
            } else if (wallpaperToSave instanceof BitmapDrawable) {
                Bitmap bitmap2 = ((BitmapDrawable) wallpaperToSave).getBitmap();
                if (bitmap2 != null) {
                    stream.write(new byte[]{87, 80, 83, 10});
                    bitmap2.compress(Bitmap.CompressFormat.JPEG, 87, stream);
                    stream.write(new byte[]{10, 87, 80, 69, 10});
                }
                if (finalSave && !upload) {
                    wallpaper = wallpaperToSave;
                    calcBackgroundColor(wallpaperToSave, 2);
                }
            }
            if (!upload) {
                if (themesDict.get(themeInfo.getKey()) == null) {
                    themes.add(themeInfo2);
                    themesDict.put(themeInfo.getKey(), themeInfo2);
                    otherThemes.add(themeInfo2);
                    saveOtherThemes(true);
                    sortThemes();
                }
                currentTheme = themeInfo2;
                if (themeInfo2 != currentNightTheme) {
                    currentDayTheme = themeInfo2;
                }
                if (colorsMap == defaultColors) {
                    currentColorsNoAccent.clear();
                    refreshThemeColors();
                }
                SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
                editor.putString("theme", currentDayTheme.getKey());
                editor.commit();
            }
            try {
                stream.close();
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        } catch (Exception e3) {
            FileLog.e((Throwable) e3);
            if (stream != null) {
                stream.close();
            }
        } catch (Throwable th) {
            Throwable th2 = th;
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e4) {
                    FileLog.e((Throwable) e4);
                }
            }
            throw th2;
        }
        if (finalSave) {
            MessagesController.getInstance(themeInfo2.account).saveThemeToServer(themeInfo2, themeInfo2.getAccent(false));
        }
    }

    public static void checkCurrentRemoteTheme(boolean force) {
        int account;
        TLRPC.TL_theme info;
        if (loadingCurrentTheme != 0) {
            return;
        }
        if (force || Math.abs((System.currentTimeMillis() / 1000) - ((long) lastLoadingCurrentThemeTime)) >= 3600) {
            int a = 0;
            while (a < 2) {
                ThemeInfo themeInfo = a == 0 ? currentDayTheme : currentNightTheme;
                if (themeInfo != null && UserConfig.getInstance(themeInfo.account).isClientActivated()) {
                    ThemeAccent accent = themeInfo.getAccent(false);
                    if (themeInfo.info != null) {
                        info = themeInfo.info;
                        account = themeInfo.account;
                    } else if (!(accent == null || accent.info == null)) {
                        info = accent.info;
                        account = UserConfig.selectedAccount;
                    }
                    if (!(info == null || info.document == null)) {
                        loadingCurrentTheme++;
                        TLRPC.TL_account_getTheme req = new TLRPC.TL_account_getTheme();
                        req.document_id = info.document.id;
                        req.format = "android";
                        TLRPC.TL_inputTheme inputTheme = new TLRPC.TL_inputTheme();
                        inputTheme.access_hash = info.access_hash;
                        inputTheme.id = info.id;
                        req.theme = inputTheme;
                        ConnectionsManager.getInstance(account).sendRequest(req, new Theme$$ExternalSyntheticLambda2(accent, themeInfo, info));
                    }
                }
                a++;
            }
        }
    }

    static /* synthetic */ void lambda$checkCurrentRemoteTheme$3(TLObject response, ThemeAccent accent, ThemeInfo themeInfo, TLRPC.TL_theme info) {
        boolean z = true;
        loadingCurrentTheme--;
        boolean changed = false;
        if (response instanceof TLRPC.TL_theme) {
            TLRPC.TL_theme theme = (TLRPC.TL_theme) response;
            TLRPC.ThemeSettings settings = null;
            if (theme.settings.size() > 0) {
                settings = theme.settings.get(0);
            }
            if (accent != null && settings != null) {
                if (!ThemeInfo.accentEquals(accent, settings)) {
                    File file = accent.getPathToWallpaper();
                    if (file != null) {
                        file.delete();
                    }
                    ThemeInfo.fillAccentValues(accent, settings);
                    ThemeInfo themeInfo2 = currentTheme;
                    if (themeInfo2 == themeInfo && themeInfo2.currentAccentId == accent.id) {
                        refreshThemeColors();
                        createChatResources(ApplicationLoader.applicationContext, false);
                        NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                        int i = NotificationCenter.needSetDayNightTheme;
                        Object[] objArr = new Object[4];
                        ThemeInfo themeInfo3 = currentTheme;
                        objArr[0] = themeInfo3;
                        objArr[1] = Boolean.valueOf(currentNightTheme == themeInfo3);
                        objArr[2] = null;
                        objArr[3] = -1;
                        globalInstance.postNotificationName(i, objArr);
                    }
                    PatternsLoader.createLoader(true);
                    changed = true;
                }
                if (settings.wallpaper == null || settings.wallpaper.settings == null || !settings.wallpaper.settings.motion) {
                    z = false;
                }
                accent.patternMotion = z;
            } else if (!(theme.document == null || theme.document.id == info.document.id)) {
                if (accent != null) {
                    accent.info = theme;
                } else {
                    themeInfo.info = theme;
                    themeInfo.loadThemeDocument();
                }
                changed = true;
            }
        }
        if (loadingCurrentTheme == 0) {
            lastLoadingCurrentThemeTime = (int) (System.currentTimeMillis() / 1000);
            saveOtherThemes(changed);
        }
    }

    public static void loadRemoteThemes(int currentAccount, boolean force) {
        if (loadingRemoteThemes[currentAccount]) {
            return;
        }
        if ((force || Math.abs((System.currentTimeMillis() / 1000) - ((long) lastLoadingThemesTime[currentAccount])) >= 3600) && UserConfig.getInstance(currentAccount).isClientActivated()) {
            loadingRemoteThemes[currentAccount] = true;
            TLRPC.TL_account_getThemes req = new TLRPC.TL_account_getThemes();
            req.format = "android";
            req.hash = remoteThemesHash[currentAccount];
            ConnectionsManager.getInstance(currentAccount).sendRequest(req, new Theme$$ExternalSyntheticLambda1(currentAccount));
        }
    }

    static /* synthetic */ void lambda$loadRemoteThemes$5(int currentAccount, TLObject response) {
        int N;
        TLRPC.TL_account_themes res;
        int N2;
        TLRPC.TL_account_themes res2;
        boolean added;
        boolean loadPatterns;
        int i = currentAccount;
        TLObject tLObject = response;
        loadingRemoteThemes[i] = false;
        if (tLObject instanceof TLRPC.TL_account_themes) {
            TLRPC.TL_account_themes res3 = (TLRPC.TL_account_themes) tLObject;
            remoteThemesHash[i] = res3.hash;
            lastLoadingThemesTime[i] = (int) (System.currentTimeMillis() / 1000);
            ArrayList<TLRPC.TL_theme> emojiPreviewThemes = new ArrayList<>();
            ArrayList<Object> oldServerThemes = new ArrayList<>();
            int N3 = themes.size();
            for (int a = 0; a < N3; a++) {
                ThemeInfo info = themes.get(a);
                if (info.info != null && info.account == i) {
                    oldServerThemes.add(info);
                } else if (info.themeAccents != null) {
                    for (int b = 0; b < info.themeAccents.size(); b++) {
                        ThemeAccent accent = info.themeAccents.get(b);
                        if (accent.info != null && accent.account == i) {
                            oldServerThemes.add(accent);
                        }
                    }
                }
            }
            boolean loadPatterns2 = false;
            boolean added2 = false;
            int a2 = 0;
            int N4 = res3.themes.size();
            while (a2 < N4) {
                TLRPC.TL_theme t = res3.themes.get(a2);
                if (!(t instanceof TLRPC.TL_theme)) {
                    res = res3;
                    N = N4;
                } else {
                    TLRPC.TL_theme theme = t;
                    if (theme.isDefault) {
                        emojiPreviewThemes.add(theme);
                    }
                    if (theme.settings == null || theme.settings.size() <= 0) {
                        res = res3;
                        N = N4;
                        String key = "remote" + theme.id;
                        ThemeInfo info2 = themesDict.get(key);
                        if (info2 == null) {
                            info2 = new ThemeInfo();
                            info2.account = i;
                            info2.pathToFile = new File(ApplicationLoader.getFilesDirFixed(), key + ".attheme").getAbsolutePath();
                            themes.add(info2);
                            otherThemes.add(info2);
                            added2 = true;
                        } else {
                            oldServerThemes.remove(info2);
                        }
                        info2.name = theme.title;
                        info2.info = theme;
                        themesDict.put(info2.getKey(), info2);
                    } else {
                        int i2 = 0;
                        while (i2 < theme.settings.size()) {
                            TLRPC.ThemeSettings settings = theme.settings.get(i2);
                            if (settings != null) {
                                String key2 = getBaseThemeKey(settings);
                                if (key2 == null) {
                                    res2 = res3;
                                    N2 = N4;
                                } else {
                                    ThemeInfo info3 = themesDict.get(key2);
                                    if (info3 == null) {
                                        res2 = res3;
                                        N2 = N4;
                                    } else if (info3.themeAccents == null) {
                                        res2 = res3;
                                        N2 = N4;
                                    } else {
                                        res2 = res3;
                                        ThemeAccent accent2 = info3.accentsByThemeId.get(theme.id);
                                        if (accent2 != null) {
                                            if (!ThemeInfo.accentEquals(accent2, settings)) {
                                                File file = accent2.getPathToWallpaper();
                                                if (file != null) {
                                                    file.delete();
                                                }
                                                ThemeInfo.fillAccentValues(accent2, settings);
                                                ThemeInfo themeInfo = currentTheme;
                                                if (themeInfo == info3) {
                                                    File file2 = file;
                                                    if (themeInfo.currentAccentId == accent2.id) {
                                                        refreshThemeColors();
                                                        NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                                                        int i3 = NotificationCenter.needSetDayNightTheme;
                                                        loadPatterns = true;
                                                        added = true;
                                                        Object[] objArr = new Object[4];
                                                        ThemeInfo themeInfo2 = currentTheme;
                                                        objArr[0] = themeInfo2;
                                                        N2 = N4;
                                                        objArr[1] = Boolean.valueOf(currentNightTheme == themeInfo2);
                                                        objArr[2] = null;
                                                        objArr[3] = -1;
                                                        globalInstance.postNotificationName(i3, objArr);
                                                    } else {
                                                        loadPatterns = true;
                                                        added = true;
                                                        N2 = N4;
                                                    }
                                                } else {
                                                    loadPatterns = true;
                                                    added = true;
                                                    N2 = N4;
                                                }
                                                loadPatterns2 = loadPatterns;
                                                added2 = added;
                                            } else {
                                                N2 = N4;
                                            }
                                            accent2.patternMotion = (settings.wallpaper == null || settings.wallpaper.settings == null || !settings.wallpaper.settings.motion) ? false : true;
                                            oldServerThemes.remove(accent2);
                                        } else {
                                            N2 = N4;
                                            accent2 = info3.createNewAccent(theme, i, false, i2);
                                            if (!TextUtils.isEmpty(accent2.patternSlug)) {
                                                loadPatterns2 = true;
                                            }
                                        }
                                        accent2.isDefault = theme.isDefault;
                                    }
                                }
                            } else {
                                res2 = res3;
                                N2 = N4;
                            }
                            i2++;
                            TLObject tLObject2 = response;
                            res3 = res2;
                            N4 = N2;
                        }
                        res = res3;
                        N = N4;
                    }
                }
                a2++;
                TLObject tLObject3 = response;
                res3 = res;
                N4 = N;
            }
            int i4 = N4;
            int N5 = oldServerThemes.size();
            for (int a3 = 0; a3 < N5; a3++) {
                Object object = oldServerThemes.get(a3);
                if (object instanceof ThemeInfo) {
                    ThemeInfo info4 = (ThemeInfo) object;
                    info4.removeObservers();
                    otherThemes.remove(info4);
                    themesDict.remove(info4.name);
                    if (info4.overrideWallpaper != null) {
                        info4.overrideWallpaper.delete();
                    }
                    themes.remove(info4);
                    new File(info4.pathToFile).delete();
                    boolean isNightTheme = false;
                    if (currentDayTheme == info4) {
                        currentDayTheme = defaultTheme;
                    } else if (currentNightTheme == info4) {
                        currentNightTheme = themesDict.get("Dark Blue");
                        isNightTheme = true;
                    }
                    if (currentTheme == info4) {
                        applyTheme(isNightTheme ? currentNightTheme : currentDayTheme, true, false, isNightTheme);
                    }
                } else if (object instanceof ThemeAccent) {
                    ThemeAccent accent3 = (ThemeAccent) object;
                    if (deleteThemeAccent(accent3.parentTheme, accent3, false) && currentTheme == accent3.parentTheme) {
                        refreshThemeColors();
                        NotificationCenter globalInstance2 = NotificationCenter.getGlobalInstance();
                        int i5 = NotificationCenter.needSetDayNightTheme;
                        Object[] objArr2 = new Object[4];
                        ThemeInfo themeInfo3 = currentTheme;
                        objArr2[0] = themeInfo3;
                        objArr2[1] = Boolean.valueOf(currentNightTheme == themeInfo3);
                        objArr2[2] = null;
                        objArr2[3] = -1;
                        globalInstance2.postNotificationName(i5, objArr2);
                    }
                }
            }
            saveOtherThemes(true);
            sortThemes();
            if (added2) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.themeListUpdated, new Object[0]);
            }
            if (loadPatterns2) {
                PatternsLoader.createLoader(true);
            }
            generateEmojiPreviewThemes(emojiPreviewThemes, i);
        }
    }

    private static void generateEmojiPreviewThemes(ArrayList<TLRPC.TL_theme> emojiPreviewThemes, final int currentAccount) {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("emojithemes_config", 0).edit();
        editor.putInt("count", emojiPreviewThemes.size());
        for (int i = 0; i < emojiPreviewThemes.size(); i++) {
            TLRPC.TL_theme tlChatTheme = emojiPreviewThemes.get(i);
            SerializedData data = new SerializedData(tlChatTheme.getObjectSize());
            tlChatTheme.serializeToStream(data);
            editor.putString("theme_" + i, Utilities.bytesToHex(data.toByteArray()));
        }
        editor.apply();
        if (!emojiPreviewThemes.isEmpty()) {
            final ArrayList<ChatThemeBottomSheet.ChatThemeItem> previewItems = new ArrayList<>();
            previewItems.add(new ChatThemeBottomSheet.ChatThemeItem(EmojiThemes.createHomePreviewTheme()));
            for (int i2 = 0; i2 < emojiPreviewThemes.size(); i2++) {
                EmojiThemes chatTheme = EmojiThemes.createPreviewFullTheme(emojiPreviewThemes.get(i2));
                ChatThemeBottomSheet.ChatThemeItem item = new ChatThemeBottomSheet.ChatThemeItem(chatTheme);
                if (chatTheme.items.size() >= 4) {
                    previewItems.add(item);
                }
            }
            ChatThemeController.chatThemeQueue.postRunnable(new Runnable() {
                public void run() {
                    for (int i = 0; i < previewItems.size(); i++) {
                        ((ChatThemeBottomSheet.ChatThemeItem) previewItems.get(i)).chatTheme.loadPreviewColors(currentAccount);
                    }
                    AndroidUtilities.runOnUIThread(new Theme$11$$ExternalSyntheticLambda0(previewItems));
                }

                static /* synthetic */ void lambda$run$0(ArrayList previewItems) {
                    Theme.defaultEmojiThemes.clear();
                    Theme.defaultEmojiThemes.addAll(previewItems);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.emojiPreviewThemesChanged, new Object[0]);
                }
            });
            return;
        }
        defaultEmojiThemes.clear();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.emojiPreviewThemesChanged, new Object[0]);
    }

    public static String getBaseThemeKey(TLRPC.ThemeSettings settings) {
        if (settings.base_theme instanceof TLRPC.TL_baseThemeClassic) {
            return "Blue";
        }
        if (settings.base_theme instanceof TLRPC.TL_baseThemeDay) {
            return "Day";
        }
        if (settings.base_theme instanceof TLRPC.TL_baseThemeTinted) {
            return "Dark Blue";
        }
        if (settings.base_theme instanceof TLRPC.TL_baseThemeArctic) {
            return "Arctic Blue";
        }
        if (settings.base_theme instanceof TLRPC.TL_baseThemeNight) {
            return "Night";
        }
        return null;
    }

    public static TLRPC.BaseTheme getBaseThemeByKey(String key) {
        if ("Blue".equals(key)) {
            return new TLRPC.TL_baseThemeClassic();
        }
        if ("Day".equals(key)) {
            return new TLRPC.TL_baseThemeDay();
        }
        if ("Dark Blue".equals(key)) {
            return new TLRPC.TL_baseThemeTinted();
        }
        if ("Arctic Blue".equals(key)) {
            return new TLRPC.TL_baseThemeArctic();
        }
        if ("Night".equals(key)) {
            return new TLRPC.TL_baseThemeNight();
        }
        return null;
    }

    public static void setThemeFileReference(TLRPC.TL_theme info) {
        int a = 0;
        int N = themes.size();
        while (a < N) {
            ThemeInfo themeInfo = themes.get(a);
            if (themeInfo.info == null || themeInfo.info.id != info.id) {
                a++;
            } else if (themeInfo.info.document != null && info.document != null) {
                themeInfo.info.document.file_reference = info.document.file_reference;
                saveOtherThemes(true);
                return;
            } else {
                return;
            }
        }
    }

    public static boolean isThemeInstalled(ThemeInfo themeInfo) {
        return (themeInfo == null || themesDict.get(themeInfo.getKey()) == null) ? false : true;
    }

    public static void setThemeUploadInfo(ThemeInfo theme, ThemeAccent accent, TLRPC.TL_theme info, int account, boolean update) {
        String key;
        if (info != null) {
            TLRPC.ThemeSettings settings = null;
            if (info.settings.size() > 0) {
                settings = info.settings.get(0);
            }
            if (settings != null) {
                if (theme == null) {
                    String key2 = getBaseThemeKey(settings);
                    if (key2 != null && (theme = themesDict.get(key2)) != null) {
                        accent = theme.accentsByThemeId.get(info.id);
                    } else {
                        return;
                    }
                }
                if (accent != null) {
                    if (accent.info != null) {
                        theme.accentsByThemeId.remove(accent.info.id);
                    }
                    accent.info = info;
                    accent.account = account;
                    theme.accentsByThemeId.put(info.id, accent);
                    if (!ThemeInfo.accentEquals(accent, settings)) {
                        File file = accent.getPathToWallpaper();
                        if (file != null) {
                            file.delete();
                        }
                        ThemeInfo.fillAccentValues(accent, settings);
                        ThemeInfo themeInfo = currentTheme;
                        if (themeInfo == theme && themeInfo.currentAccentId == accent.id) {
                            refreshThemeColors();
                            NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                            int i = NotificationCenter.needSetDayNightTheme;
                            Object[] objArr = new Object[4];
                            ThemeInfo themeInfo2 = currentTheme;
                            objArr[0] = themeInfo2;
                            objArr[1] = Boolean.valueOf(currentNightTheme == themeInfo2);
                            objArr[2] = null;
                            objArr[3] = -1;
                            globalInstance.postNotificationName(i, objArr);
                        }
                        PatternsLoader.createLoader(true);
                    }
                    accent.patternMotion = (settings.wallpaper == null || settings.wallpaper.settings == null || !settings.wallpaper.settings.motion) ? false : true;
                    theme.previewParsed = false;
                } else {
                    return;
                }
            } else {
                if (theme != null) {
                    HashMap<String, ThemeInfo> hashMap = themesDict;
                    String key3 = theme.getKey();
                    key = key3;
                    hashMap.remove(key3);
                } else {
                    String str = "remote" + info.id;
                    key = str;
                    theme = themesDict.get(str);
                }
                if (theme != null) {
                    theme.info = info;
                    theme.name = info.title;
                    File oldPath = new File(theme.pathToFile);
                    File newPath = new File(ApplicationLoader.getFilesDirFixed(), key + ".attheme");
                    if (!oldPath.equals(newPath)) {
                        try {
                            AndroidUtilities.copyFile(oldPath, newPath);
                            theme.pathToFile = newPath.getAbsolutePath();
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                    if (update) {
                        theme.loadThemeDocument();
                    } else {
                        theme.previewParsed = false;
                    }
                    themesDict.put(theme.getKey(), theme);
                } else {
                    return;
                }
            }
            saveOtherThemes(true);
        }
    }

    public static File getAssetFile(String assetName) {
        long size;
        InputStream in;
        File file = new File(ApplicationLoader.getFilesDirFixed(), assetName);
        try {
            InputStream stream = ApplicationLoader.applicationContext.getAssets().open(assetName);
            size = (long) stream.available();
            stream.close();
        } catch (Exception e) {
            size = 0;
            FileLog.e((Throwable) e);
        }
        if (!file.exists() || !(size == 0 || file.length() == size)) {
            try {
                in = ApplicationLoader.applicationContext.getAssets().open(assetName);
                AndroidUtilities.copyFile(in, file);
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            } catch (Throwable th) {
            }
        }
        return file;
        throw th;
    }

    public static int getPreviewColor(HashMap<String, Integer> colors, String key) {
        Integer color = colors.get(key);
        if (color == null) {
            color = defaultColors.get(key);
        }
        return color.intValue();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v18, resolved type: android.graphics.drawable.BitmapDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v126, resolved type: org.telegram.ui.Components.MotionBackgroundDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v129, resolved type: android.graphics.drawable.ColorDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v6, resolved type: org.telegram.ui.Components.MotionBackgroundDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v215, resolved type: org.telegram.ui.Components.MotionBackgroundDrawable} */
    /* JADX WARNING: type inference failed for: r0v121, types: [android.graphics.drawable.Drawable] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0237 A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x02a1 A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x02e1 A[SYNTHETIC, Splitter:B:139:0x02e1] */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x03d9 A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x00c6 A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x00cb A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x0583 A[SYNTHETIC, Splitter:B:246:0x0583] */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x05ae A[SYNTHETIC, Splitter:B:264:0x05ae] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00d0 A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00d6 A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:283:0x05d2 A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:284:0x05f4 A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x0626 A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x0644 A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x064a A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x0709 A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x072e A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00ea A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00ed A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00f2 A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00f7 A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00fc A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0102 A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0116 A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0119 A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x011e A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0123 A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0128 A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x012e A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0142 A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0145 A[Catch:{ all -> 0x05ba, Exception -> 0x0587, all -> 0x0799 }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0153 A[SYNTHETIC, Splitter:B:75:0x0153] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String createThemePreviewImage(java.lang.String r58, java.lang.String r59, org.telegram.ui.ActionBar.Theme.ThemeAccent r60) {
        /*
            r1 = r58
            r2 = r59
            r3 = r60
            r4 = 0
            r5 = 1
            java.lang.String[] r0 = new java.lang.String[r5]     // Catch:{ all -> 0x0799 }
            r6 = r0
            java.io.File r0 = new java.io.File     // Catch:{ all -> 0x0799 }
            r0.<init>(r1)     // Catch:{ all -> 0x0799 }
            java.util.HashMap r0 = getThemeFileValues(r0, r4, r6)     // Catch:{ all -> 0x0799 }
            r7 = r0
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = r3.parentTheme     // Catch:{ all -> 0x0799 }
            checkIsDark(r7, r0)     // Catch:{ all -> 0x0799 }
            java.lang.String r0 = "wallpaperFileOffset"
            java.lang.Object r0 = r7.get(r0)     // Catch:{ all -> 0x0799 }
            java.lang.Integer r0 = (java.lang.Integer) r0     // Catch:{ all -> 0x0799 }
            r8 = r0
            r0 = 560(0x230, float:7.85E-43)
            r9 = 678(0x2a6, float:9.5E-43)
            android.graphics.Bitmap$Config r10 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0799 }
            android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createBitmap(r0, r9, r10)     // Catch:{ all -> 0x0799 }
            r9 = r0
            android.graphics.Canvas r0 = new android.graphics.Canvas     // Catch:{ all -> 0x0799 }
            r0.<init>(r9)     // Catch:{ all -> 0x0799 }
            r15 = r0
            android.graphics.Paint r0 = new android.graphics.Paint     // Catch:{ all -> 0x0799 }
            r0.<init>()     // Catch:{ all -> 0x0799 }
            r14 = r0
            java.lang.String r0 = "actionBarDefault"
            int r0 = getPreviewColor(r7, r0)     // Catch:{ all -> 0x0799 }
            r13 = r0
            java.lang.String r0 = "actionBarDefaultIcon"
            int r0 = getPreviewColor(r7, r0)     // Catch:{ all -> 0x0799 }
            r12 = r0
            java.lang.String r0 = "chat_messagePanelBackground"
            int r0 = getPreviewColor(r7, r0)     // Catch:{ all -> 0x0799 }
            r11 = r0
            java.lang.String r0 = "chat_messagePanelIcons"
            int r0 = getPreviewColor(r7, r0)     // Catch:{ all -> 0x0799 }
            r10 = r0
            java.lang.String r0 = "chat_inBubble"
            int r0 = getPreviewColor(r7, r0)     // Catch:{ all -> 0x0799 }
            r16 = r0
            java.lang.String r0 = "chat_outBubble"
            int r0 = getPreviewColor(r7, r0)     // Catch:{ all -> 0x0799 }
            r17 = r0
            java.lang.String r0 = "chat_outBubbleGradient"
            java.lang.Object r0 = r7.get(r0)     // Catch:{ all -> 0x0799 }
            java.lang.Integer r0 = (java.lang.Integer) r0     // Catch:{ all -> 0x0799 }
            r18 = r0
            java.lang.String r0 = "chat_wallpaper"
            java.lang.Object r0 = r7.get(r0)     // Catch:{ all -> 0x0799 }
            java.lang.Integer r0 = (java.lang.Integer) r0     // Catch:{ all -> 0x0799 }
            r19 = r0
            java.lang.String r0 = "chat_wallpaper_gradient_to"
            java.lang.Object r0 = r7.get(r0)     // Catch:{ all -> 0x0799 }
            java.lang.Integer r0 = (java.lang.Integer) r0     // Catch:{ all -> 0x0799 }
            r20 = r0
            java.lang.String r0 = "key_chat_wallpaper_gradient_to2"
            java.lang.Object r0 = r7.get(r0)     // Catch:{ all -> 0x0799 }
            java.lang.Integer r0 = (java.lang.Integer) r0     // Catch:{ all -> 0x0799 }
            r21 = r0
            java.lang.String r0 = "key_chat_wallpaper_gradient_to3"
            java.lang.Object r0 = r7.get(r0)     // Catch:{ all -> 0x0799 }
            java.lang.Integer r0 = (java.lang.Integer) r0     // Catch:{ all -> 0x0799 }
            r22 = r0
            r23 = r11
            if (r19 == 0) goto L_0x00a1
            int r0 = r19.intValue()     // Catch:{ all -> 0x0799 }
            goto L_0x00a2
        L_0x00a1:
            r0 = 0
        L_0x00a2:
            r24 = r0
            if (r3 == 0) goto L_0x00aa
            long r4 = r3.backgroundOverrideColor     // Catch:{ all -> 0x0799 }
            int r0 = (int) r4     // Catch:{ all -> 0x0799 }
            goto L_0x00ab
        L_0x00aa:
            r0 = 0
        L_0x00ab:
            r4 = r0
            r26 = 0
            if (r4 != 0) goto L_0x00bc
            if (r3 == 0) goto L_0x00bc
            r5 = r12
            long r11 = r3.backgroundOverrideColor     // Catch:{ all -> 0x0799 }
            int r0 = (r11 > r26 ? 1 : (r11 == r26 ? 0 : -1))
            if (r0 == 0) goto L_0x00bd
            r0 = 0
            r11 = r0
            goto L_0x00c4
        L_0x00bc:
            r5 = r12
        L_0x00bd:
            if (r4 == 0) goto L_0x00c1
            r0 = r4
            goto L_0x00c3
        L_0x00c1:
            r0 = r24
        L_0x00c3:
            r11 = r0
        L_0x00c4:
            if (r20 == 0) goto L_0x00cb
            int r0 = r20.intValue()     // Catch:{ all -> 0x0799 }
            goto L_0x00cc
        L_0x00cb:
            r0 = 0
        L_0x00cc:
            r29 = r0
            if (r3 == 0) goto L_0x00d6
            r30 = r11
            long r11 = r3.backgroundGradientOverrideColor1     // Catch:{ all -> 0x0799 }
            int r0 = (int) r11     // Catch:{ all -> 0x0799 }
            goto L_0x00d9
        L_0x00d6:
            r30 = r11
            r0 = 0
        L_0x00d9:
            r31 = r0
            if (r31 != 0) goto L_0x00e8
            if (r3 == 0) goto L_0x00e8
            long r11 = r3.backgroundGradientOverrideColor1     // Catch:{ all -> 0x0799 }
            int r0 = (r11 > r26 ? 1 : (r11 == r26 ? 0 : -1))
            if (r0 == 0) goto L_0x00e8
            r0 = 0
            r11 = r0
            goto L_0x00f0
        L_0x00e8:
            if (r31 == 0) goto L_0x00ed
            r0 = r31
            goto L_0x00ef
        L_0x00ed:
            r0 = r29
        L_0x00ef:
            r11 = r0
        L_0x00f0:
            if (r21 == 0) goto L_0x00f7
            int r0 = r21.intValue()     // Catch:{ all -> 0x0799 }
            goto L_0x00f8
        L_0x00f7:
            r0 = 0
        L_0x00f8:
            r32 = r0
            if (r3 == 0) goto L_0x0102
            r33 = r11
            long r11 = r3.backgroundGradientOverrideColor2     // Catch:{ all -> 0x0799 }
            int r0 = (int) r11     // Catch:{ all -> 0x0799 }
            goto L_0x0105
        L_0x0102:
            r33 = r11
            r0 = 0
        L_0x0105:
            r34 = r0
            if (r34 != 0) goto L_0x0114
            if (r3 == 0) goto L_0x0114
            long r11 = r3.backgroundGradientOverrideColor2     // Catch:{ all -> 0x0799 }
            int r0 = (r11 > r26 ? 1 : (r11 == r26 ? 0 : -1))
            if (r0 == 0) goto L_0x0114
            r0 = 0
            r11 = r0
            goto L_0x011c
        L_0x0114:
            if (r34 == 0) goto L_0x0119
            r0 = r34
            goto L_0x011b
        L_0x0119:
            r0 = r32
        L_0x011b:
            r11 = r0
        L_0x011c:
            if (r22 == 0) goto L_0x0123
            int r0 = r22.intValue()     // Catch:{ all -> 0x0799 }
            goto L_0x0124
        L_0x0123:
            r0 = 0
        L_0x0124:
            r35 = r0
            if (r3 == 0) goto L_0x012e
            r36 = r11
            long r11 = r3.backgroundGradientOverrideColor3     // Catch:{ all -> 0x0799 }
            int r0 = (int) r11     // Catch:{ all -> 0x0799 }
            goto L_0x0131
        L_0x012e:
            r36 = r11
            r0 = 0
        L_0x0131:
            r37 = r0
            if (r37 != 0) goto L_0x0140
            if (r3 == 0) goto L_0x0140
            long r11 = r3.backgroundGradientOverrideColor3     // Catch:{ all -> 0x0799 }
            int r0 = (r11 > r26 ? 1 : (r11 == r26 ? 0 : -1))
            if (r0 == 0) goto L_0x0140
            r0 = 0
            r11 = r0
            goto L_0x0148
        L_0x0140:
            if (r37 == 0) goto L_0x0145
            r0 = r37
            goto L_0x0147
        L_0x0145:
            r0 = r35
        L_0x0147:
            r11 = r0
        L_0x0148:
            r12 = 0
            r0 = r6[r12]     // Catch:{ all -> 0x0799 }
            boolean r0 = android.text.TextUtils.isEmpty(r0)     // Catch:{ all -> 0x0799 }
            r26 = r11
            if (r0 != 0) goto L_0x0237
            r0 = r6[r12]     // Catch:{ Exception -> 0x022b }
            android.net.Uri r0 = android.net.Uri.parse(r0)     // Catch:{ Exception -> 0x022b }
            java.lang.String r12 = "bg_color"
            java.lang.String r12 = r0.getQueryParameter(r12)     // Catch:{ Exception -> 0x022b }
            if (r3 == 0) goto L_0x0221
            boolean r27 = android.text.TextUtils.isEmpty(r12)     // Catch:{ Exception -> 0x022b }
            if (r27 != 0) goto L_0x0221
            r11 = 6
            r39 = r0
            r38 = r4
            r4 = 0
            java.lang.String r0 = r12.substring(r4, r11)     // Catch:{ Exception -> 0x021b }
            r4 = 16
            int r0 = java.lang.Integer.parseInt(r0, r4)     // Catch:{ Exception -> 0x021b }
            r40 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r0 = r0 | r40
            r30 = r0
            r41 = r5
            long r4 = (long) r0
            r3.backgroundOverrideColor = r4     // Catch:{ Exception -> 0x0217 }
            int r0 = r12.length()     // Catch:{ Exception -> 0x0217 }
            r4 = 13
            if (r0 < r4) goto L_0x01a6
            char r0 = r12.charAt(r11)     // Catch:{ Exception -> 0x0217 }
            boolean r0 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r0)     // Catch:{ Exception -> 0x0217 }
            if (r0 == 0) goto L_0x01a6
            r0 = 7
            java.lang.String r0 = r12.substring(r0, r4)     // Catch:{ Exception -> 0x0217 }
            r5 = 16
            int r0 = java.lang.Integer.parseInt(r0, r5)     // Catch:{ Exception -> 0x0217 }
            r0 = r0 | r40
            r11 = r0
            long r4 = (long) r0
            r3.backgroundGradientOverrideColor1 = r4     // Catch:{ Exception -> 0x0211 }
            goto L_0x01a8
        L_0x01a6:
            r11 = r33
        L_0x01a8:
            int r0 = r12.length()     // Catch:{ Exception -> 0x0211 }
            r4 = 20
            if (r0 < r4) goto L_0x01d1
            r0 = 13
            char r0 = r12.charAt(r0)     // Catch:{ Exception -> 0x0211 }
            boolean r0 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r0)     // Catch:{ Exception -> 0x0211 }
            if (r0 == 0) goto L_0x01d1
            r0 = 14
            java.lang.String r0 = r12.substring(r0, r4)     // Catch:{ Exception -> 0x0211 }
            r4 = 16
            int r0 = java.lang.Integer.parseInt(r0, r4)     // Catch:{ Exception -> 0x0211 }
            r0 = r0 | r40
            r4 = r0
            r33 = r4
            long r4 = (long) r0
            r3.backgroundGradientOverrideColor2 = r4     // Catch:{ Exception -> 0x0209 }
            goto L_0x01d3
        L_0x01d1:
            r33 = r36
        L_0x01d3:
            int r0 = r12.length()     // Catch:{ Exception -> 0x0209 }
            r4 = 27
            if (r0 != r4) goto L_0x0202
            r4 = 20
            char r0 = r12.charAt(r4)     // Catch:{ Exception -> 0x0209 }
            boolean r0 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r0)     // Catch:{ Exception -> 0x0209 }
            if (r0 == 0) goto L_0x0202
            r0 = 21
            java.lang.String r0 = r12.substring(r0)     // Catch:{ Exception -> 0x0209 }
            r5 = 16
            int r0 = java.lang.Integer.parseInt(r0, r5)     // Catch:{ Exception -> 0x0209 }
            r0 = r0 | r40
            r5 = r0
            r26 = r5
            long r4 = (long) r0     // Catch:{ Exception -> 0x0209 }
            r3.backgroundGradientOverrideColor3 = r4     // Catch:{ Exception -> 0x0209 }
            r36 = r33
            r33 = r11
            r11 = r30
            goto L_0x0229
        L_0x0202:
            r36 = r33
            r33 = r11
            r11 = r30
            goto L_0x0229
        L_0x0209:
            r0 = move-exception
            r36 = r33
            r33 = r11
            r11 = r30
            goto L_0x0232
        L_0x0211:
            r0 = move-exception
            r33 = r11
            r11 = r30
            goto L_0x0232
        L_0x0217:
            r0 = move-exception
            r11 = r30
            goto L_0x0232
        L_0x021b:
            r0 = move-exception
            r41 = r5
            r11 = r30
            goto L_0x0232
        L_0x0221:
            r39 = r0
            r38 = r4
            r41 = r5
            r11 = r30
        L_0x0229:
            r4 = r11
            goto L_0x023d
        L_0x022b:
            r0 = move-exception
            r38 = r4
            r41 = r5
            r11 = r30
        L_0x0232:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0799 }
            r4 = r11
            goto L_0x023d
        L_0x0237:
            r38 = r4
            r41 = r5
            r4 = r30
        L_0x023d:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0799 }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ all -> 0x0799 }
            r5 = 2131166012(0x7var_c, float:1.7946257E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r5)     // Catch:{ all -> 0x0799 }
            android.graphics.drawable.Drawable r0 = r0.mutate()     // Catch:{ all -> 0x0799 }
            r5 = r0
            r12 = r41
            setDrawableColor(r5, r12)     // Catch:{ all -> 0x0799 }
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0799 }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ all -> 0x0799 }
            r11 = 2131166014(0x7var_e, float:1.7946261E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r11)     // Catch:{ all -> 0x0799 }
            android.graphics.drawable.Drawable r0 = r0.mutate()     // Catch:{ all -> 0x0799 }
            r11 = r0
            setDrawableColor(r11, r12)     // Catch:{ all -> 0x0799 }
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0799 }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ all -> 0x0799 }
            r30 = r11
            r11 = 2131166017(0x7var_, float:1.7946267E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r11)     // Catch:{ all -> 0x0799 }
            android.graphics.drawable.Drawable r0 = r0.mutate()     // Catch:{ all -> 0x0799 }
            r11 = r0
            setDrawableColor(r11, r10)     // Catch:{ all -> 0x0799 }
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0799 }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ all -> 0x0799 }
            r39 = r11
            r11 = 2131166015(0x7var_f, float:1.7946263E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r11)     // Catch:{ all -> 0x0799 }
            android.graphics.drawable.Drawable r0 = r0.mutate()     // Catch:{ all -> 0x0799 }
            r11 = r0
            setDrawableColor(r11, r10)     // Catch:{ all -> 0x0799 }
            r41 = r12
            r12 = 2
            org.telegram.ui.ActionBar.Theme$MessageDrawable[] r0 = new org.telegram.ui.ActionBar.Theme.MessageDrawable[r12]     // Catch:{ all -> 0x0799 }
            r40 = r0
            r0 = 0
        L_0x029f:
            if (r0 >= r12) goto L_0x02cc
            org.telegram.ui.ActionBar.Theme$12 r12 = new org.telegram.ui.ActionBar.Theme$12     // Catch:{ all -> 0x0799 }
            r49 = r10
            r10 = 1
            if (r0 != r10) goto L_0x02aa
            r10 = 1
            goto L_0x02ab
        L_0x02aa:
            r10 = 0
        L_0x02ab:
            r51 = r5
            r50 = r11
            r5 = 0
            r11 = 2
            r12.<init>(r11, r10, r5, r7)     // Catch:{ all -> 0x0799 }
            r40[r0] = r12     // Catch:{ all -> 0x0799 }
            r5 = r40[r0]     // Catch:{ all -> 0x0799 }
            if (r0 != 0) goto L_0x02bd
            r10 = r16
            goto L_0x02bf
        L_0x02bd:
            r10 = r17
        L_0x02bf:
            setDrawableColor(r5, r10)     // Catch:{ all -> 0x0799 }
            int r0 = r0 + 1
            r10 = r49
            r11 = r50
            r5 = r51
            r12 = 2
            goto L_0x029f
        L_0x02cc:
            r51 = r5
            r49 = r10
            r50 = r11
            android.graphics.RectF r0 = new android.graphics.RectF     // Catch:{ all -> 0x0799 }
            r0.<init>()     // Catch:{ all -> 0x0799 }
            r5 = r0
            r10 = 80
            r11 = 0
            r43 = 1141637120(0x440CLASSNAME, float:560.0)
            r52 = r11
            if (r2 == 0) goto L_0x03d9
            android.graphics.BitmapFactory$Options r44 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x03cf }
            r44.<init>()     // Catch:{ all -> 0x03cf }
            r53 = r44
            r11 = r53
            r12 = 1
            r11.inJustDecodeBounds = r12     // Catch:{ all -> 0x03cf }
            android.graphics.BitmapFactory.decodeFile(r2, r11)     // Catch:{ all -> 0x03cf }
            int r12 = r11.outWidth     // Catch:{ all -> 0x03cf }
            if (r12 <= 0) goto L_0x03c3
            int r12 = r11.outHeight     // Catch:{ all -> 0x03cf }
            if (r12 <= 0) goto L_0x03c3
            int r12 = r11.outWidth     // Catch:{ all -> 0x03cf }
            float r12 = (float) r12     // Catch:{ all -> 0x03cf }
            float r12 = r12 / r43
            int r0 = r11.outHeight     // Catch:{ all -> 0x03cf }
            float r0 = (float) r0     // Catch:{ all -> 0x03cf }
            float r0 = r0 / r43
            float r0 = java.lang.Math.min(r12, r0)     // Catch:{ all -> 0x03cf }
            r12 = 1
            r11.inSampleSize = r12     // Catch:{ all -> 0x03cf }
            r12 = 1065353216(0x3var_, float:1.0)
            int r12 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r12 <= 0) goto L_0x0324
        L_0x030f:
            int r12 = r11.inSampleSize     // Catch:{ all -> 0x031f }
            r45 = 2
            int r12 = r12 * 2
            r11.inSampleSize = r12     // Catch:{ all -> 0x031f }
            int r12 = r11.inSampleSize     // Catch:{ all -> 0x031f }
            float r12 = (float) r12
            int r12 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1))
            if (r12 < 0) goto L_0x030f
            goto L_0x0324
        L_0x031f:
            r0 = move-exception
            r53 = r10
            goto L_0x03d2
        L_0x0324:
            r12 = 0
            r11.inJustDecodeBounds = r12     // Catch:{ all -> 0x03cf }
            android.graphics.Bitmap r12 = android.graphics.BitmapFactory.decodeFile(r2, r11)     // Catch:{ all -> 0x03cf }
            if (r12 == 0) goto L_0x03be
            if (r36 == 0) goto L_0x0366
            if (r3 == 0) goto L_0x0366
            org.telegram.ui.Components.MotionBackgroundDrawable r53 = new org.telegram.ui.Components.MotionBackgroundDrawable     // Catch:{ all -> 0x03cf }
            r47 = 1
            r42 = r53
            r43 = r4
            r44 = r33
            r45 = r36
            r46 = r26
            r42.<init>(r43, r44, r45, r46, r47)     // Catch:{ all -> 0x03cf }
            r42 = r53
            r45 = r0
            float r0 = r3.patternIntensity     // Catch:{ all -> 0x03cf }
            r43 = 1120403456(0x42CLASSNAME, float:100.0)
            float r0 = r0 * r43
            int r0 = (int) r0     // Catch:{ all -> 0x03cf }
            r2 = r42
            r2.setPatternBitmap(r0, r12)     // Catch:{ all -> 0x03cf }
            int r0 = r9.getWidth()     // Catch:{ all -> 0x03cf }
            int r3 = r9.getHeight()     // Catch:{ all -> 0x03cf }
            r53 = r10
            r10 = 0
            r2.setBounds(r10, r10, r0, r3)     // Catch:{ all -> 0x03bc }
            r2.draw(r15)     // Catch:{ all -> 0x03bc }
            r0 = r45
            goto L_0x03b9
        L_0x0366:
            r45 = r0
            r53 = r10
            android.graphics.Paint r0 = new android.graphics.Paint     // Catch:{ all -> 0x03bc }
            r0.<init>()     // Catch:{ all -> 0x03bc }
            r2 = 1
            r0.setFilterBitmap(r2)     // Catch:{ all -> 0x03bc }
            int r2 = r12.getWidth()     // Catch:{ all -> 0x03bc }
            float r2 = (float) r2     // Catch:{ all -> 0x03bc }
            float r2 = r2 / r43
            int r3 = r12.getHeight()     // Catch:{ all -> 0x03bc }
            float r3 = (float) r3     // Catch:{ all -> 0x03bc }
            float r3 = r3 / r43
            float r2 = java.lang.Math.min(r2, r3)     // Catch:{ all -> 0x03bc }
            int r3 = r12.getWidth()     // Catch:{ all -> 0x03bc }
            float r3 = (float) r3     // Catch:{ all -> 0x03bc }
            float r3 = r3 / r2
            int r10 = r12.getHeight()     // Catch:{ all -> 0x03bc }
            float r10 = (float) r10     // Catch:{ all -> 0x03bc }
            float r10 = r10 / r2
            r43 = r2
            r2 = 0
            r5.set(r2, r2, r3, r10)     // Catch:{ all -> 0x03bc }
            int r2 = r9.getWidth()     // Catch:{ all -> 0x03bc }
            float r2 = (float) r2     // Catch:{ all -> 0x03bc }
            float r3 = r5.width()     // Catch:{ all -> 0x03bc }
            float r2 = r2 - r3
            r3 = 1073741824(0x40000000, float:2.0)
            float r2 = r2 / r3
            int r10 = r9.getHeight()     // Catch:{ all -> 0x03bc }
            float r10 = (float) r10     // Catch:{ all -> 0x03bc }
            float r42 = r5.height()     // Catch:{ all -> 0x03bc }
            float r10 = r10 - r42
            float r10 = r10 / r3
            r5.offset(r2, r10)     // Catch:{ all -> 0x03bc }
            r2 = 0
            r15.drawBitmap(r12, r2, r5, r0)     // Catch:{ all -> 0x03bc }
            r0 = r43
        L_0x03b9:
            r2 = 1
            r11 = r2
            goto L_0x03c7
        L_0x03bc:
            r0 = move-exception
            goto L_0x03d2
        L_0x03be:
            r45 = r0
            r53 = r10
            goto L_0x03c5
        L_0x03c3:
            r53 = r10
        L_0x03c5:
            r11 = r52
        L_0x03c7:
            r45 = r4
            r52 = r11
            r2 = r53
            goto L_0x05d0
        L_0x03cf:
            r0 = move-exception
            r53 = r10
        L_0x03d2:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0799 }
            r45 = r4
            goto L_0x05ce
        L_0x03d9:
            r53 = r10
            if (r4 == 0) goto L_0x044c
            if (r33 != 0) goto L_0x03e7
            android.graphics.drawable.ColorDrawable r0 = new android.graphics.drawable.ColorDrawable     // Catch:{ all -> 0x0799 }
            r0.<init>(r4)     // Catch:{ all -> 0x0799 }
            r10 = r53
            goto L_0x0432
        L_0x03e7:
            if (r36 == 0) goto L_0x03fd
            org.telegram.ui.Components.MotionBackgroundDrawable r0 = new org.telegram.ui.Components.MotionBackgroundDrawable     // Catch:{ all -> 0x0799 }
            r47 = 1
            r42 = r0
            r43 = r4
            r44 = r33
            r45 = r36
            r46 = r26
            r42.<init>(r43, r44, r45, r46, r47)     // Catch:{ all -> 0x0799 }
            r10 = r53
            goto L_0x0432
        L_0x03fd:
            java.lang.String r0 = "chat_wallpaper_gradient_rotation"
            java.lang.Object r0 = r7.get(r0)     // Catch:{ all -> 0x0799 }
            java.lang.Integer r0 = (java.lang.Integer) r0     // Catch:{ all -> 0x0799 }
            if (r0 != 0) goto L_0x040e
            r2 = 45
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ all -> 0x0799 }
            r0 = r2
        L_0x040e:
            r2 = 2
            int[] r3 = new int[r2]     // Catch:{ all -> 0x0799 }
            r2 = 0
            r3[r2] = r4     // Catch:{ all -> 0x0799 }
            int r2 = r21.intValue()     // Catch:{ all -> 0x0799 }
            r10 = 1
            r3[r10] = r2     // Catch:{ all -> 0x0799 }
            r2 = r3
            int r3 = r0.intValue()     // Catch:{ all -> 0x0799 }
            int r10 = r9.getWidth()     // Catch:{ all -> 0x0799 }
            int r11 = r9.getHeight()     // Catch:{ all -> 0x0799 }
            r12 = 120(0x78, float:1.68E-43)
            int r11 = r11 - r12
            android.graphics.drawable.BitmapDrawable r3 = org.telegram.ui.Components.BackgroundGradientDrawable.createDitheredGradientBitmapDrawable((int) r3, (int[]) r2, (int) r10, (int) r11)     // Catch:{ all -> 0x0799 }
            r10 = 90
            r0 = r3
        L_0x0432:
            int r2 = r9.getWidth()     // Catch:{ all -> 0x0799 }
            int r3 = r9.getHeight()     // Catch:{ all -> 0x0799 }
            r11 = 120(0x78, float:1.68E-43)
            int r3 = r3 - r11
            r12 = 0
            r0.setBounds(r12, r11, r2, r3)     // Catch:{ all -> 0x0799 }
            r0.draw(r15)     // Catch:{ all -> 0x0799 }
            r11 = 1
            r45 = r4
            r2 = r10
            r52 = r11
            goto L_0x05d0
        L_0x044c:
            if (r8 == 0) goto L_0x0454
            int r0 = r8.intValue()     // Catch:{ all -> 0x0799 }
            if (r0 >= 0) goto L_0x045d
        L_0x0454:
            r2 = 0
            r0 = r6[r2]     // Catch:{ all -> 0x0799 }
            boolean r0 = android.text.TextUtils.isEmpty(r0)     // Catch:{ all -> 0x0799 }
            if (r0 != 0) goto L_0x05cc
        L_0x045d:
            r2 = 0
            r3 = 0
            android.graphics.BitmapFactory$Options r0 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x05a4 }
            r0.<init>()     // Catch:{ all -> 0x05a4 }
            r10 = 1
            r0.inJustDecodeBounds = r10     // Catch:{ all -> 0x05a4 }
            r10 = 0
            r11 = r6[r10]     // Catch:{ all -> 0x05a4 }
            boolean r10 = android.text.TextUtils.isEmpty(r11)     // Catch:{ all -> 0x05a4 }
            if (r10 != 0) goto L_0x04ad
            java.io.File r10 = new java.io.File     // Catch:{ all -> 0x04a6 }
            java.io.File r11 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ all -> 0x04a6 }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x04a6 }
            r12.<init>()     // Catch:{ all -> 0x04a6 }
            r28 = 0
            r46 = r6[r28]     // Catch:{ all -> 0x04a6 }
            r47 = r2
            java.lang.String r2 = org.telegram.messenger.Utilities.MD5(r46)     // Catch:{ all -> 0x049f }
            r12.append(r2)     // Catch:{ all -> 0x049f }
            java.lang.String r2 = ".wp"
            r12.append(r2)     // Catch:{ all -> 0x049f }
            java.lang.String r2 = r12.toString()     // Catch:{ all -> 0x049f }
            r10.<init>(r11, r2)     // Catch:{ all -> 0x049f }
            r3 = r10
            java.lang.String r2 = r3.getAbsolutePath()     // Catch:{ all -> 0x049f }
            android.graphics.BitmapFactory.decodeFile(r2, r0)     // Catch:{ all -> 0x049f }
            r2 = r47
            goto L_0x04c4
        L_0x049f:
            r0 = move-exception
            r45 = r4
            r2 = r47
            goto L_0x05a9
        L_0x04a6:
            r0 = move-exception
            r47 = r2
            r45 = r4
            goto L_0x05a9
        L_0x04ad:
            r47 = r2
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ all -> 0x059e }
            r2.<init>(r1)     // Catch:{ all -> 0x059e }
            java.nio.channels.FileChannel r10 = r2.getChannel()     // Catch:{ all -> 0x059a }
            int r11 = r8.intValue()     // Catch:{ all -> 0x059a }
            long r11 = (long) r11     // Catch:{ all -> 0x059a }
            r10.position(r11)     // Catch:{ all -> 0x059a }
            r10 = 0
            android.graphics.BitmapFactory.decodeStream(r2, r10, r0)     // Catch:{ all -> 0x059a }
        L_0x04c4:
            int r10 = r0.outWidth     // Catch:{ all -> 0x0594 }
            if (r10 <= 0) goto L_0x0579
            int r10 = r0.outHeight     // Catch:{ all -> 0x0594 }
            if (r10 <= 0) goto L_0x0579
            int r10 = r0.outWidth     // Catch:{ all -> 0x0594 }
            float r10 = (float) r10     // Catch:{ all -> 0x0594 }
            float r10 = r10 / r43
            int r11 = r0.outHeight     // Catch:{ all -> 0x0594 }
            float r11 = (float) r11     // Catch:{ all -> 0x0594 }
            float r11 = r11 / r43
            float r10 = java.lang.Math.min(r10, r11)     // Catch:{ all -> 0x0594 }
            r11 = 1
            r0.inSampleSize = r11     // Catch:{ all -> 0x0594 }
            r11 = 1065353216(0x3var_, float:1.0)
            int r11 = (r10 > r11 ? 1 : (r10 == r11 ? 0 : -1))
            if (r11 <= 0) goto L_0x04f7
        L_0x04e3:
            int r11 = r0.inSampleSize     // Catch:{ all -> 0x04f2 }
            r12 = 2
            int r11 = r11 * 2
            r0.inSampleSize = r11     // Catch:{ all -> 0x04f2 }
            int r11 = r0.inSampleSize     // Catch:{ all -> 0x04f2 }
            float r11 = (float) r11
            int r11 = (r11 > r10 ? 1 : (r11 == r10 ? 0 : -1))
            if (r11 < 0) goto L_0x04e3
            goto L_0x04f8
        L_0x04f2:
            r0 = move-exception
            r45 = r4
            goto L_0x05a9
        L_0x04f7:
            r12 = 2
        L_0x04f8:
            r11 = 0
            r0.inJustDecodeBounds = r11     // Catch:{ all -> 0x0594 }
            if (r3 == 0) goto L_0x050a
            java.lang.String r11 = r3.getAbsolutePath()     // Catch:{ all -> 0x04f2 }
            android.graphics.Bitmap r11 = android.graphics.BitmapFactory.decodeFile(r11, r0)     // Catch:{ all -> 0x04f2 }
            r46 = r3
            r45 = r4
            goto L_0x0520
        L_0x050a:
            java.nio.channels.FileChannel r11 = r2.getChannel()     // Catch:{ all -> 0x0594 }
            int r12 = r8.intValue()     // Catch:{ all -> 0x0594 }
            r46 = r3
            r45 = r4
            long r3 = (long) r12
            r11.position(r3)     // Catch:{ all -> 0x0575 }
            r3 = 0
            android.graphics.Bitmap r4 = android.graphics.BitmapFactory.decodeStream(r2, r3, r0)     // Catch:{ all -> 0x0575 }
            r11 = r4
        L_0x0520:
            if (r11 == 0) goto L_0x0572
            android.graphics.Paint r3 = new android.graphics.Paint     // Catch:{ all -> 0x0575 }
            r3.<init>()     // Catch:{ all -> 0x0575 }
            r4 = 1
            r3.setFilterBitmap(r4)     // Catch:{ all -> 0x0575 }
            int r4 = r11.getWidth()     // Catch:{ all -> 0x0575 }
            float r4 = (float) r4     // Catch:{ all -> 0x0575 }
            float r4 = r4 / r43
            int r12 = r11.getHeight()     // Catch:{ all -> 0x0575 }
            float r12 = (float) r12     // Catch:{ all -> 0x0575 }
            float r12 = r12 / r43
            float r4 = java.lang.Math.min(r4, r12)     // Catch:{ all -> 0x0575 }
            int r10 = r11.getWidth()     // Catch:{ all -> 0x0575 }
            float r10 = (float) r10     // Catch:{ all -> 0x0575 }
            float r10 = r10 / r4
            int r12 = r11.getHeight()     // Catch:{ all -> 0x0575 }
            float r12 = (float) r12     // Catch:{ all -> 0x0575 }
            float r12 = r12 / r4
            r43 = r0
            r0 = 0
            r5.set(r0, r0, r10, r12)     // Catch:{ all -> 0x0575 }
            int r0 = r9.getWidth()     // Catch:{ all -> 0x0575 }
            float r0 = (float) r0     // Catch:{ all -> 0x0575 }
            float r10 = r5.width()     // Catch:{ all -> 0x0575 }
            float r0 = r0 - r10
            r10 = 1073741824(0x40000000, float:2.0)
            float r0 = r0 / r10
            int r12 = r9.getHeight()     // Catch:{ all -> 0x0575 }
            float r12 = (float) r12     // Catch:{ all -> 0x0575 }
            float r42 = r5.height()     // Catch:{ all -> 0x0575 }
            float r12 = r12 - r42
            float r12 = r12 / r10
            r5.offset(r0, r12)     // Catch:{ all -> 0x0575 }
            r10 = 0
            r15.drawBitmap(r11, r10, r5, r3)     // Catch:{ all -> 0x0575 }
            r0 = 1
            r11 = r0
            goto L_0x0581
        L_0x0572:
            r43 = r0
            goto L_0x057f
        L_0x0575:
            r0 = move-exception
            r3 = r46
            goto L_0x05a9
        L_0x0579:
            r43 = r0
            r46 = r3
            r45 = r4
        L_0x057f:
            r11 = r52
        L_0x0581:
            if (r2 == 0) goto L_0x058e
            r2.close()     // Catch:{ Exception -> 0x0587 }
            goto L_0x058e
        L_0x0587:
            r0 = move-exception
            r3 = r0
            r0 = r3
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0799 }
            goto L_0x058f
        L_0x058e:
        L_0x058f:
            r52 = r11
            r2 = r53
            goto L_0x05d0
        L_0x0594:
            r0 = move-exception
            r46 = r3
            r45 = r4
            goto L_0x05a9
        L_0x059a:
            r0 = move-exception
            r45 = r4
            goto L_0x05a9
        L_0x059e:
            r0 = move-exception
            r45 = r4
            r2 = r47
            goto L_0x05a9
        L_0x05a4:
            r0 = move-exception
            r47 = r2
            r45 = r4
        L_0x05a9:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x05ba }
            if (r2 == 0) goto L_0x05b9
            r2.close()     // Catch:{ Exception -> 0x05b2 }
            goto L_0x05b9
        L_0x05b2:
            r0 = move-exception
            r4 = r0
            r0 = r4
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0799 }
            goto L_0x05ce
        L_0x05b9:
            goto L_0x05ce
        L_0x05ba:
            r0 = move-exception
            r4 = r0
            if (r2 == 0) goto L_0x05c9
            r2.close()     // Catch:{ Exception -> 0x05c2 }
            goto L_0x05c9
        L_0x05c2:
            r0 = move-exception
            r10 = r0
            r0 = r10
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0799 }
            goto L_0x05ca
        L_0x05c9:
        L_0x05ca:
            throw r4     // Catch:{ all -> 0x0799 }
        L_0x05cc:
            r45 = r4
        L_0x05ce:
            r2 = r53
        L_0x05d0:
            if (r52 != 0) goto L_0x05f4
            int r0 = r9.getWidth()     // Catch:{ all -> 0x0799 }
            int r3 = r9.getHeight()     // Catch:{ all -> 0x0799 }
            r4 = 120(0x78, float:1.68E-43)
            int r3 = r3 - r4
            android.graphics.drawable.Drawable r0 = createDefaultWallpaper(r0, r3)     // Catch:{ all -> 0x0799 }
            int r3 = r9.getWidth()     // Catch:{ all -> 0x0799 }
            int r4 = r9.getHeight()     // Catch:{ all -> 0x0799 }
            r11 = 120(0x78, float:1.68E-43)
            int r4 = r4 - r11
            r12 = 0
            r0.setBounds(r12, r11, r3, r4)     // Catch:{ all -> 0x0799 }
            r0.draw(r15)     // Catch:{ all -> 0x0799 }
            goto L_0x05f7
        L_0x05f4:
            r11 = 120(0x78, float:1.68E-43)
            r12 = 0
        L_0x05f7:
            r14.setColor(r13)     // Catch:{ all -> 0x0799 }
            r0 = 0
            r3 = 0
            int r4 = r9.getWidth()     // Catch:{ all -> 0x0799 }
            float r4 = (float) r4     // Catch:{ all -> 0x0799 }
            r28 = 1123024896(0x42var_, float:120.0)
            r42 = r49
            r10 = r15
            r12 = r23
            r54 = r30
            r55 = r39
            r56 = r50
            r23 = 120(0x78, float:1.68E-43)
            r27 = 0
            r11 = r0
            r57 = r12
            r30 = r41
            r39 = 2
            r12 = r3
            r3 = r13
            r13 = r4
            r4 = r14
            r14 = r28
            r1 = r15
            r15 = r4
            r10.drawRect(r11, r12, r13, r14, r15)     // Catch:{ all -> 0x0799 }
            if (r51 == 0) goto L_0x0644
            r0 = 13
            int r10 = r51.getIntrinsicHeight()     // Catch:{ all -> 0x0799 }
            int r11 = 120 - r10
            int r11 = r11 / 2
            r10 = r11
            int r11 = r51.getIntrinsicWidth()     // Catch:{ all -> 0x0799 }
            int r11 = r11 + r0
            int r12 = r51.getIntrinsicHeight()     // Catch:{ all -> 0x0799 }
            int r12 = r12 + r10
            r15 = r51
            r15.setBounds(r0, r10, r11, r12)     // Catch:{ all -> 0x0799 }
            r15.draw(r1)     // Catch:{ all -> 0x0799 }
            goto L_0x0646
        L_0x0644:
            r15 = r51
        L_0x0646:
            r14 = r54
            if (r14 == 0) goto L_0x066e
            int r0 = r9.getWidth()     // Catch:{ all -> 0x0799 }
            int r10 = r14.getIntrinsicWidth()     // Catch:{ all -> 0x0799 }
            int r0 = r0 - r10
            int r0 = r0 + -10
            int r10 = r14.getIntrinsicHeight()     // Catch:{ all -> 0x0799 }
            int r11 = 120 - r10
            int r11 = r11 / 2
            r10 = r11
            int r11 = r14.getIntrinsicWidth()     // Catch:{ all -> 0x0799 }
            int r11 = r11 + r0
            int r12 = r14.getIntrinsicHeight()     // Catch:{ all -> 0x0799 }
            int r12 = r12 + r10
            r14.setBounds(r0, r10, r11, r12)     // Catch:{ all -> 0x0799 }
            r14.draw(r1)     // Catch:{ all -> 0x0799 }
        L_0x066e:
            r10 = 1
            r0 = r40[r10]     // Catch:{ all -> 0x0799 }
            r10 = 216(0xd8, float:3.03E-43)
            int r11 = r9.getWidth()     // Catch:{ all -> 0x0799 }
            r12 = 20
            int r11 = r11 - r12
            r13 = 308(0x134, float:4.32E-43)
            r12 = 161(0xa1, float:2.26E-43)
            r0.setBounds(r12, r10, r11, r13)     // Catch:{ all -> 0x0799 }
            r10 = 1
            r46 = r40[r10]     // Catch:{ all -> 0x0799 }
            r47 = 0
            r48 = 560(0x230, float:7.85E-43)
            r49 = 522(0x20a, float:7.31E-43)
            r50 = 0
            r51 = 0
            r46.setTop(r47, r48, r49, r50, r51)     // Catch:{ all -> 0x0799 }
            r10 = 1
            r0 = r40[r10]     // Catch:{ all -> 0x0799 }
            r0.draw(r1)     // Catch:{ all -> 0x0799 }
            r0 = r40[r10]     // Catch:{ all -> 0x0799 }
            r10 = 430(0x1ae, float:6.03E-43)
            int r11 = r9.getWidth()     // Catch:{ all -> 0x0799 }
            r13 = 20
            int r11 = r11 - r13
            r13 = 522(0x20a, float:7.31E-43)
            r0.setBounds(r12, r10, r11, r13)     // Catch:{ all -> 0x0799 }
            r10 = 1
            r46 = r40[r10]     // Catch:{ all -> 0x0799 }
            r47 = 430(0x1ae, float:6.03E-43)
            r48 = 560(0x230, float:7.85E-43)
            r49 = 522(0x20a, float:7.31E-43)
            r50 = 0
            r51 = 0
            r46.setTop(r47, r48, r49, r50, r51)     // Catch:{ all -> 0x0799 }
            r10 = 1
            r0 = r40[r10]     // Catch:{ all -> 0x0799 }
            r0.draw(r1)     // Catch:{ all -> 0x0799 }
            r0 = r40[r27]     // Catch:{ all -> 0x0799 }
            r10 = 323(0x143, float:4.53E-43)
            r11 = 399(0x18f, float:5.59E-43)
            r12 = 415(0x19f, float:5.82E-43)
            r13 = 20
            r0.setBounds(r13, r10, r11, r12)     // Catch:{ all -> 0x0799 }
            r46 = r40[r27]     // Catch:{ all -> 0x0799 }
            r47 = 323(0x143, float:4.53E-43)
            r48 = 560(0x230, float:7.85E-43)
            r49 = 522(0x20a, float:7.31E-43)
            r50 = 0
            r51 = 0
            r46.setTop(r47, r48, r49, r50, r51)     // Catch:{ all -> 0x0799 }
            r0 = r40[r27]     // Catch:{ all -> 0x0799 }
            r0.draw(r1)     // Catch:{ all -> 0x0799 }
            r13 = r57
            r4.setColor(r13)     // Catch:{ all -> 0x0799 }
            r11 = 0
            int r0 = r9.getHeight()     // Catch:{ all -> 0x0799 }
            int r0 = r0 + -120
            float r12 = (float) r0     // Catch:{ all -> 0x0799 }
            int r0 = r9.getWidth()     // Catch:{ all -> 0x0799 }
            float r0 = (float) r0     // Catch:{ all -> 0x0799 }
            int r10 = r9.getHeight()     // Catch:{ all -> 0x0799 }
            float r10 = (float) r10     // Catch:{ all -> 0x0799 }
            r25 = r10
            r10 = r1
            r27 = r13
            r13 = r0
            r28 = r14
            r14 = r25
            r25 = r15
            r15 = r4
            r10.drawRect(r11, r12, r13, r14, r15)     // Catch:{ all -> 0x0799 }
            r10 = r55
            if (r10 == 0) goto L_0x072a
            r0 = 22
            int r11 = r9.getHeight()     // Catch:{ all -> 0x0799 }
            int r11 = r11 + -120
            int r12 = r10.getIntrinsicHeight()     // Catch:{ all -> 0x0799 }
            int r12 = 120 - r12
            int r12 = r12 / 2
            int r11 = r11 + r12
            int r12 = r10.getIntrinsicWidth()     // Catch:{ all -> 0x0799 }
            int r12 = r12 + r0
            int r13 = r10.getIntrinsicHeight()     // Catch:{ all -> 0x0799 }
            int r13 = r13 + r11
            r10.setBounds(r0, r11, r12, r13)     // Catch:{ all -> 0x0799 }
            r10.draw(r1)     // Catch:{ all -> 0x0799 }
        L_0x072a:
            r11 = r56
            if (r11 == 0) goto L_0x0758
            int r0 = r9.getWidth()     // Catch:{ all -> 0x0799 }
            int r12 = r11.getIntrinsicWidth()     // Catch:{ all -> 0x0799 }
            int r0 = r0 - r12
            int r0 = r0 + -22
            int r12 = r9.getHeight()     // Catch:{ all -> 0x0799 }
            int r12 = r12 + -120
            int r13 = r11.getIntrinsicHeight()     // Catch:{ all -> 0x0799 }
            int r13 = 120 - r13
            int r13 = r13 / 2
            int r12 = r12 + r13
            int r13 = r11.getIntrinsicWidth()     // Catch:{ all -> 0x0799 }
            int r13 = r13 + r0
            int r14 = r11.getIntrinsicHeight()     // Catch:{ all -> 0x0799 }
            int r14 = r14 + r12
            r11.setBounds(r0, r12, r13, r14)     // Catch:{ all -> 0x0799 }
            r11.draw(r1)     // Catch:{ all -> 0x0799 }
        L_0x0758:
            r12 = 0
            r1.setBitmap(r12)     // Catch:{ all -> 0x0799 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0799 }
            r0.<init>()     // Catch:{ all -> 0x0799 }
            java.lang.String r12 = "-2147483648_"
            r0.append(r12)     // Catch:{ all -> 0x0799 }
            int r12 = org.telegram.messenger.SharedConfig.getLastLocalId()     // Catch:{ all -> 0x0799 }
            r0.append(r12)     // Catch:{ all -> 0x0799 }
            java.lang.String r12 = ".jpg"
            r0.append(r12)     // Catch:{ all -> 0x0799 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0799 }
            r12 = r0
            java.io.File r0 = new java.io.File     // Catch:{ all -> 0x0799 }
            r13 = 4
            java.io.File r13 = org.telegram.messenger.FileLoader.getDirectory(r13)     // Catch:{ all -> 0x0799 }
            r0.<init>(r13, r12)     // Catch:{ all -> 0x0799 }
            r13 = r0
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ all -> 0x0794 }
            r0.<init>(r13)     // Catch:{ all -> 0x0794 }
            android.graphics.Bitmap$CompressFormat r14 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ all -> 0x0794 }
            r9.compress(r14, r2, r0)     // Catch:{ all -> 0x0794 }
            org.telegram.messenger.SharedConfig.saveConfig()     // Catch:{ all -> 0x0794 }
            java.lang.String r14 = r13.getAbsolutePath()     // Catch:{ all -> 0x0794 }
            return r14
        L_0x0794:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0799 }
            goto L_0x079d
        L_0x0799:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x079d:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.createThemePreviewImage(java.lang.String, java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeAccent):java.lang.String");
    }

    /* access modifiers changed from: private */
    public static void checkIsDark(HashMap<String, Integer> colors, ThemeInfo info) {
        if (info != null && colors != null && info.isDark == -1) {
            if (ColorUtils.calculateLuminance(ColorUtils.blendARGB(getPreviewColor(colors, "windowBackgroundWhite"), getPreviewColor(colors, "windowBackgroundWhite"), 0.5f)) < 0.5d) {
                int unused = info.isDark = 1;
            } else {
                int unused2 = info.isDark = 0;
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:63:0x0114 A[SYNTHETIC, Splitter:B:63:0x0114] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.HashMap<java.lang.String, java.lang.Integer> getThemeFileValues(java.io.File r19, java.lang.String r20, java.lang.String[] r21) {
        /*
            r1 = r21
            r2 = 0
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r3 = r0
            r0 = 1024(0x400, float:1.435E-42)
            byte[] r0 = new byte[r0]     // Catch:{ all -> 0x010c }
            r4 = r0
            r0 = 0
            if (r20 == 0) goto L_0x0016
            java.io.File r5 = getAssetFile(r20)     // Catch:{ all -> 0x010c }
            goto L_0x0018
        L_0x0016:
            r5 = r19
        L_0x0018:
            java.io.FileInputStream r6 = new java.io.FileInputStream     // Catch:{ all -> 0x010a }
            r6.<init>(r5)     // Catch:{ all -> 0x010a }
            r2 = r6
            r6 = 0
            r7 = -1
        L_0x0020:
            int r8 = r2.read(r4)     // Catch:{ all -> 0x010a }
            r9 = r8
            r10 = -1
            if (r8 == r10) goto L_0x00f3
            r8 = r0
            r11 = 0
            r12 = 0
            r13 = r12
            r12 = r11
            r11 = r0
        L_0x002e:
            if (r13 >= r9) goto L_0x00da
            byte r0 = r4[r13]     // Catch:{ all -> 0x010a }
            r14 = 10
            if (r0 != r14) goto L_0x00cf
            int r0 = r13 - r12
            int r14 = r0 + 1
            java.lang.String r0 = new java.lang.String     // Catch:{ all -> 0x010a }
            int r15 = r14 + -1
            r0.<init>(r4, r12, r15)     // Catch:{ all -> 0x010a }
            r15 = r0
            java.lang.String r0 = "WLS="
            boolean r0 = r15.startsWith(r0)     // Catch:{ all -> 0x010a }
            r10 = 0
            if (r0 == 0) goto L_0x005f
            if (r1 == 0) goto L_0x005b
            int r0 = r1.length     // Catch:{ all -> 0x010a }
            if (r0 <= 0) goto L_0x005b
            r0 = 4
            java.lang.String r0 = r15.substring(r0)     // Catch:{ all -> 0x010a }
            r1[r10] = r0     // Catch:{ all -> 0x010a }
            r16 = r4
            goto L_0x00cc
        L_0x005b:
            r16 = r4
            goto L_0x00cc
        L_0x005f:
            java.lang.String r0 = "WPS"
            boolean r0 = r15.startsWith(r0)     // Catch:{ all -> 0x010a }
            if (r0 == 0) goto L_0x006f
            int r0 = r11 + r14
            r6 = 1
            r7 = r0
            r16 = r4
            goto L_0x00dc
        L_0x006f:
            r0 = 61
            int r0 = r15.indexOf(r0)     // Catch:{ all -> 0x010a }
            r16 = r0
            r10 = -1
            if (r0 == r10) goto L_0x00c8
            r10 = r16
            r0 = 0
            java.lang.String r16 = r15.substring(r0, r10)     // Catch:{ all -> 0x010a }
            r18 = r16
            int r0 = r10 + 1
            java.lang.String r0 = r15.substring(r0)     // Catch:{ all -> 0x010a }
            r16 = r0
            int r0 = r16.length()     // Catch:{ all -> 0x010a }
            if (r0 <= 0) goto L_0x00b0
            r1 = r16
            r0 = 0
            char r0 = r1.charAt(r0)     // Catch:{ all -> 0x010a }
            r16 = r4
            r4 = 35
            if (r0 != r4) goto L_0x00b4
            int r0 = android.graphics.Color.parseColor(r1)     // Catch:{ Exception -> 0x00a3 }
        L_0x00a2:
            goto L_0x00bc
        L_0x00a3:
            r0 = move-exception
            r4 = r0
            r0 = r4
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r1)     // Catch:{ all -> 0x010a }
            int r4 = r4.intValue()     // Catch:{ all -> 0x010a }
            r0 = r4
            goto L_0x00a2
        L_0x00b0:
            r1 = r16
            r16 = r4
        L_0x00b4:
            java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r1)     // Catch:{ all -> 0x010a }
            int r0 = r0.intValue()     // Catch:{ all -> 0x010a }
        L_0x00bc:
            java.lang.Integer r4 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x010a }
            r17 = r1
            r1 = r18
            r3.put(r1, r4)     // Catch:{ all -> 0x010a }
            goto L_0x00cc
        L_0x00c8:
            r10 = r16
            r16 = r4
        L_0x00cc:
            int r12 = r12 + r14
            int r11 = r11 + r14
            goto L_0x00d1
        L_0x00cf:
            r16 = r4
        L_0x00d1:
            int r13 = r13 + 1
            r1 = r21
            r4 = r16
            r10 = -1
            goto L_0x002e
        L_0x00da:
            r16 = r4
        L_0x00dc:
            if (r8 != r11) goto L_0x00df
            goto L_0x00ea
        L_0x00df:
            java.nio.channels.FileChannel r0 = r2.getChannel()     // Catch:{ all -> 0x010a }
            long r13 = (long) r11     // Catch:{ all -> 0x010a }
            r0.position(r13)     // Catch:{ all -> 0x010a }
            if (r6 == 0) goto L_0x00ec
        L_0x00ea:
            r0 = r11
            goto L_0x00f5
        L_0x00ec:
            r1 = r21
            r0 = r11
            r4 = r16
            goto L_0x0020
        L_0x00f3:
            r16 = r4
        L_0x00f5:
            java.lang.String r1 = "wallpaperFileOffset"
            java.lang.Integer r4 = java.lang.Integer.valueOf(r7)     // Catch:{ all -> 0x010a }
            r3.put(r1, r4)     // Catch:{ all -> 0x010a }
            r2.close()     // Catch:{ Exception -> 0x0103 }
        L_0x0102:
            goto L_0x0118
        L_0x0103:
            r0 = move-exception
            r1 = r0
            r0 = r1
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0118
        L_0x010a:
            r0 = move-exception
            goto L_0x010f
        L_0x010c:
            r0 = move-exception
            r5 = r19
        L_0x010f:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0119 }
            if (r2 == 0) goto L_0x0102
            r2.close()     // Catch:{ Exception -> 0x0103 }
            goto L_0x0102
        L_0x0118:
            return r3
        L_0x0119:
            r0 = move-exception
            r1 = r0
            if (r2 == 0) goto L_0x0128
            r2.close()     // Catch:{ Exception -> 0x0121 }
            goto L_0x0128
        L_0x0121:
            r0 = move-exception
            r4 = r0
            r0 = r4
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0129
        L_0x0128:
        L_0x0129:
            goto L_0x012b
        L_0x012a:
            throw r1
        L_0x012b:
            goto L_0x012a
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
            int a = 0;
            while (true) {
                Drawable[] drawableArr = avatarDrawables;
                if (a < drawableArr.length) {
                    setDrawableColorByKey(drawableArr[a], "avatar_text");
                    a++;
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

    public static void createCommonDialogResources(Context context) {
        if (dialogs_countTextPaint == null) {
            TextPaint textPaint = new TextPaint(1);
            dialogs_countTextPaint = textPaint;
            textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            dialogs_countPaint = new Paint(1);
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
            for (int a = 0; a < 2; a++) {
                dialogs_namePaint[a] = new TextPaint(1);
                dialogs_namePaint[a].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                dialogs_nameEncryptedPaint[a] = new TextPaint(1);
                dialogs_nameEncryptedPaint[a].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                dialogs_messagePaint[a] = new TextPaint(1);
                dialogs_messagePrintingPaint[a] = new TextPaint(1);
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
            RectF rect = new RectF();
            chat_updatePath[0] = new Path();
            chat_updatePath[2] = new Path();
            float cx = (float) AndroidUtilities.dp(12.0f);
            float cy = (float) AndroidUtilities.dp(12.0f);
            rect.set(cx - ((float) AndroidUtilities.dp(5.0f)), cy - ((float) AndroidUtilities.dp(5.0f)), ((float) AndroidUtilities.dp(5.0f)) + cx, ((float) AndroidUtilities.dp(5.0f)) + cy);
            chat_updatePath[2].arcTo(rect, -160.0f, -110.0f, true);
            chat_updatePath[2].arcTo(rect, 20.0f, -110.0f, true);
            chat_updatePath[0].moveTo(cx, ((float) AndroidUtilities.dp(8.0f)) + cy);
            chat_updatePath[0].lineTo(cx, ((float) AndroidUtilities.dp(2.0f)) + cy);
            chat_updatePath[0].lineTo(((float) AndroidUtilities.dp(3.0f)) + cx, ((float) AndroidUtilities.dp(5.0f)) + cy);
            chat_updatePath[0].close();
            chat_updatePath[0].moveTo(cx, cy - ((float) AndroidUtilities.dp(8.0f)));
            chat_updatePath[0].lineTo(cx, cy - ((float) AndroidUtilities.dp(2.0f)));
            chat_updatePath[0].lineTo(cx - ((float) AndroidUtilities.dp(3.0f)), cy - ((float) AndroidUtilities.dp(5.0f)));
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
            for (int a = 0; a < 2; a++) {
                dialogs_namePaint[a].setColor(getColor("chats_name"));
                dialogs_nameEncryptedPaint[a].setColor(getColor("chats_secretName"));
                TextPaint[] textPaintArr = dialogs_messagePaint;
                TextPaint textPaint = textPaintArr[a];
                TextPaint textPaint2 = textPaintArr[a];
                int color = getColor("chats_message");
                textPaint2.linkColor = color;
                textPaint.setColor(color);
                dialogs_messagePrintingPaint[a].setColor(getColor("chats_actionMessage"));
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

    public static void destroyResources() {
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
            chat_urlPaint = new Paint();
            chat_textSearchSelectionPaint = new Paint();
            Paint paint2 = new Paint(1);
            chat_radialProgressPaint = paint2;
            paint2.setStrokeCap(Paint.Cap.ROUND);
            chat_radialProgressPaint.setStyle(Paint.Style.STROKE);
            chat_radialProgressPaint.setColor(-NUM);
            Paint paint3 = new Paint(1);
            chat_radialProgress2Paint = paint3;
            paint3.setStrokeCap(Paint.Cap.ROUND);
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
            Paint paint4 = new Paint(1);
            chat_instantViewRectPaint = paint4;
            paint4.setStyle(Paint.Style.STROKE);
            chat_instantViewRectPaint.setStrokeCap(Paint.Cap.ROUND);
            Paint paint5 = new Paint(1);
            chat_pollTimerPaint = paint5;
            paint5.setStyle(Paint.Style.STROKE);
            chat_pollTimerPaint.setStrokeCap(Paint.Cap.ROUND);
            chat_replyLinePaint = new Paint(1);
            chat_msgErrorPaint = new Paint(1);
            chat_statusPaint = new Paint(1);
            Paint paint6 = new Paint(1);
            chat_statusRecordPaint = paint6;
            paint6.setStyle(Paint.Style.STROKE);
            chat_statusRecordPaint.setStrokeCap(Paint.Cap.ROUND);
            TextPaint textPaint12 = new TextPaint(1);
            chat_actionTextPaint = textPaint12;
            textPaint12.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            Paint paint7 = new Paint(1);
            chat_actionBackgroundGradientDarkenPaint = paint7;
            paint7.setColor(NUM);
            chat_timeBackgroundPaint = new Paint(1);
            TextPaint textPaint13 = new TextPaint(1);
            chat_contextResult_titleTextPaint = textPaint13;
            textPaint13.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_contextResult_descriptionTextPaint = new TextPaint(1);
            chat_composeBackgroundPaint = new Paint();
            chat_radialProgressPausedPaint = new Paint(1);
            chat_radialProgressPausedSeekbarPaint = new Paint(1);
            chat_actionBackgroundPaint = new Paint(1);
            chat_actionBackgroundSelectedPaint = new Paint(1);
            chat_actionBackgroundPaint2 = new Paint(1);
            chat_actionBackgroundSelectedPaint2 = new Paint(1);
            addChatPaint("paintChatActionBackground", chat_actionBackgroundPaint, "chat_serviceBackground");
            addChatPaint("paintChatActionBackgroundSelected", chat_actionBackgroundSelectedPaint, "chat_serviceBackgroundSelected");
            addChatPaint("paintChatActionText", chat_actionTextPaint, "chat_serviceText");
            addChatPaint("paintChatBotButton", chat_botButtonPaint, "chat_botButtonText");
            addChatPaint("paintChatComposeBackground", chat_composeBackgroundPaint, "chat_messagePanelBackground");
            addChatPaint("paintChatTimeBackground", chat_timeBackgroundPaint, "chat_mediaTimeBackground");
        }
    }

    public static void createChatResources(Context context, boolean fontsOnly) {
        Paint paint;
        boolean z = fontsOnly;
        createCommonChatResources();
        if (!z && chat_msgInDrawable == null) {
            Resources resources = context.getResources();
            chat_msgNoSoundDrawable = resources.getDrawable(NUM);
            chat_msgInDrawable = new MessageDrawable(0, false, false);
            chat_msgInSelectedDrawable = new MessageDrawable(0, false, true);
            chat_msgOutDrawable = new MessageDrawable(0, true, false);
            chat_msgOutSelectedDrawable = new MessageDrawable(0, true, true);
            chat_msgInMediaDrawable = new MessageDrawable(1, false, false);
            chat_msgInMediaSelectedDrawable = new MessageDrawable(1, false, true);
            chat_msgOutMediaDrawable = new MessageDrawable(1, true, false);
            chat_msgOutMediaSelectedDrawable = new MessageDrawable(1, true, true);
            PathAnimator pathAnimator = new PathAnimator(0.293f, -26.0f, -28.0f, 1.0f);
            playPauseAnimator = pathAnimator;
            pathAnimator.addSvgKeyFrame("M 34.141 16.042 C 37.384 17.921 40.886 20.001 44.211 21.965 C 46.139 23.104 49.285 24.729 49.586 25.917 C 50.289 28.687 48.484 30 46.274 30 L 6 30.021 C 3.79 30.021 2.075 30.023 2 26.021 L 2.009 3.417 C 2.009 0.417 5.326 -0.58 7.068 0.417 C 10.545 2.406 25.024 10.761 34.141 16.042 Z", 166.0f);
            playPauseAnimator.addSvgKeyFrame("M 37.843 17.769 C 41.143 19.508 44.131 21.164 47.429 23.117 C 48.542 23.775 49.623 24.561 49.761 25.993 C 50.074 28.708 48.557 30 46.347 30 L 6 30.012 C 3.79 30.012 2 28.222 2 26.012 L 2.009 4.609 C 2.009 1.626 5.276 0.664 7.074 1.541 C 10.608 3.309 28.488 12.842 37.843 17.769 Z", 200.0f);
            playPauseAnimator.addSvgKeyFrame("M 40.644 18.756 C 43.986 20.389 49.867 23.108 49.884 25.534 C 49.897 27.154 49.88 24.441 49.894 26.059 C 49.911 28.733 48.6 30 46.39 30 L 6 30.013 C 3.79 30.013 2 28.223 2 26.013 L 2.008 5.52 C 2.008 2.55 5.237 1.614 7.079 2.401 C 10.656 4 31.106 14.097 40.644 18.756 Z", 217.0f);
            playPauseAnimator.addSvgKeyFrame("M 43.782 19.218 C 47.117 20.675 50.075 21.538 50.041 24.796 C 50.022 26.606 50.038 24.309 50.039 26.104 C 50.038 28.736 48.663 30 46.453 30 L 6 29.986 C 3.79 29.986 2 28.196 2 25.986 L 2.008 6.491 C 2.008 3.535 5.196 2.627 7.085 3.316 C 10.708 4.731 33.992 14.944 43.782 19.218 Z", 234.0f);
            playPauseAnimator.addSvgKeyFrame("M 47.421 16.941 C 50.544 18.191 50.783 19.91 50.769 22.706 C 50.761 24.484 50.76 23.953 50.79 26.073 C 50.814 27.835 49.334 30 47.124 30 L 5 30.01 C 2.79 30.01 1 28.22 1 26.01 L 1.001 10.823 C 1.001 8.218 3.532 6.895 5.572 7.26 C 7.493 8.01 47.421 16.941 47.421 16.941 Z", 267.0f);
            playPauseAnimator.addSvgKeyFrame("M 47.641 17.125 C 50.641 18.207 51.09 19.935 51.078 22.653 C 51.07 24.191 51.062 21.23 51.088 23.063 C 51.109 24.886 49.587 27 47.377 27 L 5 27.009 C 2.79 27.009 1 25.219 1 23.009 L 0.983 11.459 C 0.983 8.908 3.414 7.522 5.476 7.838 C 7.138 8.486 47.641 17.125 47.641 17.125 Z", 300.0f);
            playPauseAnimator.addSvgKeyFrame("M 48 7 C 50.21 7 52 8.79 52 11 C 52 19 52 19 52 19 C 52 21.21 50.21 23 48 23 L 4 23 C 1.79 23 0 21.21 0 19 L 0 11 C 0 8.79 1.79 7 4 7 C 48 7 48 7 48 7 Z", 383.0f);
            chat_msgOutCheckDrawable = resources.getDrawable(NUM).mutate();
            chat_msgOutCheckSelectedDrawable = resources.getDrawable(NUM).mutate();
            chat_msgOutCheckReadDrawable = resources.getDrawable(NUM).mutate();
            chat_msgOutCheckReadSelectedDrawable = resources.getDrawable(NUM).mutate();
            chat_msgMediaCheckDrawable = resources.getDrawable(NUM).mutate();
            chat_msgStickerCheckDrawable = resources.getDrawable(NUM).mutate();
            chat_msgOutHalfCheckDrawable = resources.getDrawable(NUM).mutate();
            chat_msgOutHalfCheckSelectedDrawable = resources.getDrawable(NUM).mutate();
            chat_msgMediaHalfCheckDrawable = resources.getDrawable(NUM).mutate();
            chat_msgStickerHalfCheckDrawable = resources.getDrawable(NUM).mutate();
            chat_msgClockDrawable = new MsgClockDrawable();
            chat_msgInViewsDrawable = resources.getDrawable(NUM).mutate();
            chat_msgInViewsSelectedDrawable = resources.getDrawable(NUM).mutate();
            chat_msgOutViewsDrawable = resources.getDrawable(NUM).mutate();
            chat_msgOutViewsSelectedDrawable = resources.getDrawable(NUM).mutate();
            chat_msgInRepliesDrawable = resources.getDrawable(NUM).mutate();
            chat_msgInRepliesSelectedDrawable = resources.getDrawable(NUM).mutate();
            chat_msgOutRepliesDrawable = resources.getDrawable(NUM).mutate();
            chat_msgOutRepliesSelectedDrawable = resources.getDrawable(NUM).mutate();
            chat_msgInPinnedDrawable = resources.getDrawable(NUM).mutate();
            chat_msgInPinnedSelectedDrawable = resources.getDrawable(NUM).mutate();
            chat_msgOutPinnedDrawable = resources.getDrawable(NUM).mutate();
            chat_msgOutPinnedSelectedDrawable = resources.getDrawable(NUM).mutate();
            chat_msgMediaPinnedDrawable = resources.getDrawable(NUM).mutate();
            chat_msgStickerPinnedDrawable = resources.getDrawable(NUM).mutate();
            chat_msgMediaViewsDrawable = resources.getDrawable(NUM).mutate();
            chat_msgMediaRepliesDrawable = resources.getDrawable(NUM).mutate();
            chat_msgStickerViewsDrawable = resources.getDrawable(NUM).mutate();
            chat_msgStickerRepliesDrawable = resources.getDrawable(NUM).mutate();
            chat_msgInMenuDrawable = resources.getDrawable(NUM).mutate();
            chat_msgInMenuSelectedDrawable = resources.getDrawable(NUM).mutate();
            chat_msgOutMenuDrawable = resources.getDrawable(NUM).mutate();
            chat_msgOutMenuSelectedDrawable = resources.getDrawable(NUM).mutate();
            chat_msgMediaMenuDrawable = resources.getDrawable(NUM);
            chat_msgInInstantDrawable = resources.getDrawable(NUM).mutate();
            chat_msgOutInstantDrawable = resources.getDrawable(NUM).mutate();
            chat_msgErrorDrawable = resources.getDrawable(NUM);
            chat_muteIconDrawable = resources.getDrawable(NUM).mutate();
            chat_lockIconDrawable = resources.getDrawable(NUM);
            chat_msgBroadcastDrawable = resources.getDrawable(NUM).mutate();
            chat_msgBroadcastMediaDrawable = resources.getDrawable(NUM).mutate();
            chat_msgInCallDrawable[0] = resources.getDrawable(NUM).mutate();
            chat_msgInCallSelectedDrawable[0] = resources.getDrawable(NUM).mutate();
            chat_msgOutCallDrawable[0] = resources.getDrawable(NUM).mutate();
            chat_msgOutCallSelectedDrawable[0] = resources.getDrawable(NUM).mutate();
            chat_msgInCallDrawable[1] = resources.getDrawable(NUM).mutate();
            chat_msgInCallSelectedDrawable[1] = resources.getDrawable(NUM).mutate();
            chat_msgOutCallDrawable[1] = resources.getDrawable(NUM).mutate();
            chat_msgOutCallSelectedDrawable[1] = resources.getDrawable(NUM).mutate();
            chat_msgCallUpGreenDrawable = resources.getDrawable(NUM).mutate();
            chat_msgCallDownRedDrawable = resources.getDrawable(NUM).mutate();
            chat_msgCallDownGreenDrawable = resources.getDrawable(NUM).mutate();
            for (int a = 0; a < 2; a++) {
                chat_pollCheckDrawable[a] = resources.getDrawable(NUM).mutate();
                chat_pollCrossDrawable[a] = resources.getDrawable(NUM).mutate();
                chat_pollHintDrawable[a] = resources.getDrawable(NUM).mutate();
                chat_psaHelpDrawable[a] = resources.getDrawable(NUM).mutate();
            }
            calllog_msgCallUpRedDrawable = resources.getDrawable(NUM).mutate();
            calllog_msgCallUpGreenDrawable = resources.getDrawable(NUM).mutate();
            calllog_msgCallDownRedDrawable = resources.getDrawable(NUM).mutate();
            calllog_msgCallDownGreenDrawable = resources.getDrawable(NUM).mutate();
            chat_msgAvatarLiveLocationDrawable = resources.getDrawable(NUM).mutate();
            chat_inlineResultFile = resources.getDrawable(NUM);
            chat_inlineResultAudio = resources.getDrawable(NUM);
            chat_inlineResultLocation = resources.getDrawable(NUM);
            chat_redLocationIcon = resources.getDrawable(NUM).mutate();
            chat_botLinkDrawalbe = resources.getDrawable(NUM);
            chat_botInlineDrawable = resources.getDrawable(NUM);
            chat_botCardDrawalbe = resources.getDrawable(NUM);
            chat_commentDrawable = resources.getDrawable(NUM);
            chat_commentStickerDrawable = resources.getDrawable(NUM);
            chat_commentArrowDrawable = resources.getDrawable(NUM);
            chat_contextResult_shadowUnderSwitchDrawable = resources.getDrawable(NUM).mutate();
            chat_attachButtonDrawables[0] = new RLottieDrawable(NUM, "attach_gallery", AndroidUtilities.dp(26.0f), AndroidUtilities.dp(26.0f));
            chat_attachButtonDrawables[1] = new RLottieDrawable(NUM, "attach_music", AndroidUtilities.dp(26.0f), AndroidUtilities.dp(26.0f));
            chat_attachButtonDrawables[2] = new RLottieDrawable(NUM, "attach_file", AndroidUtilities.dp(26.0f), AndroidUtilities.dp(26.0f));
            chat_attachButtonDrawables[3] = new RLottieDrawable(NUM, "attach_contact", AndroidUtilities.dp(26.0f), AndroidUtilities.dp(26.0f));
            chat_attachButtonDrawables[4] = new RLottieDrawable(NUM, "attach_location", AndroidUtilities.dp(26.0f), AndroidUtilities.dp(26.0f));
            chat_attachButtonDrawables[5] = new RLottieDrawable(NUM, "attach_poll", AndroidUtilities.dp(26.0f), AndroidUtilities.dp(26.0f));
            chat_attachEmptyDrawable = resources.getDrawable(NUM);
            chat_shareIconDrawable = resources.getDrawable(NUM).mutate();
            chat_replyIconDrawable = resources.getDrawable(NUM);
            chat_goIconDrawable = resources.getDrawable(NUM);
            chat_fileMiniStatesDrawable[0][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(22.0f), NUM);
            chat_fileMiniStatesDrawable[0][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(22.0f), NUM);
            chat_fileMiniStatesDrawable[1][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(22.0f), NUM);
            chat_fileMiniStatesDrawable[1][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(22.0f), NUM);
            chat_fileMiniStatesDrawable[2][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(22.0f), NUM);
            chat_fileMiniStatesDrawable[2][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(22.0f), NUM);
            chat_fileMiniStatesDrawable[3][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(22.0f), NUM);
            chat_fileMiniStatesDrawable[3][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(22.0f), NUM);
            chat_fileMiniStatesDrawable[4][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(22.0f), NUM);
            chat_fileMiniStatesDrawable[4][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(22.0f), NUM);
            chat_fileMiniStatesDrawable[5][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(22.0f), NUM);
            chat_fileMiniStatesDrawable[5][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(22.0f), NUM);
            int rad = AndroidUtilities.dp(2.0f);
            RectF rect = new RectF();
            chat_filePath[0] = new Path();
            chat_filePath[0].moveTo((float) AndroidUtilities.dp(7.0f), (float) AndroidUtilities.dp(3.0f));
            chat_filePath[0].lineTo((float) AndroidUtilities.dp(14.0f), (float) AndroidUtilities.dp(3.0f));
            chat_filePath[0].lineTo((float) AndroidUtilities.dp(21.0f), (float) AndroidUtilities.dp(10.0f));
            chat_filePath[0].lineTo((float) AndroidUtilities.dp(21.0f), (float) AndroidUtilities.dp(20.0f));
            rect.set((float) (AndroidUtilities.dp(21.0f) - (rad * 2)), (float) (AndroidUtilities.dp(19.0f) - rad), (float) AndroidUtilities.dp(21.0f), (float) (AndroidUtilities.dp(19.0f) + rad));
            chat_filePath[0].arcTo(rect, 0.0f, 90.0f, false);
            chat_filePath[0].lineTo((float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(21.0f));
            rect.set((float) AndroidUtilities.dp(5.0f), (float) (AndroidUtilities.dp(19.0f) - rad), (float) (AndroidUtilities.dp(5.0f) + (rad * 2)), (float) (AndroidUtilities.dp(19.0f) + rad));
            chat_filePath[0].arcTo(rect, 90.0f, 90.0f, false);
            chat_filePath[0].lineTo((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(4.0f));
            rect.set((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(3.0f), (float) (AndroidUtilities.dp(5.0f) + (rad * 2)), (float) (AndroidUtilities.dp(3.0f) + (rad * 2)));
            chat_filePath[0].arcTo(rect, 180.0f, 90.0f, false);
            chat_filePath[0].close();
            chat_filePath[1] = new Path();
            chat_filePath[1].moveTo((float) AndroidUtilities.dp(14.0f), (float) AndroidUtilities.dp(5.0f));
            chat_filePath[1].lineTo((float) AndroidUtilities.dp(19.0f), (float) AndroidUtilities.dp(10.0f));
            chat_filePath[1].lineTo((float) AndroidUtilities.dp(14.0f), (float) AndroidUtilities.dp(10.0f));
            chat_filePath[1].close();
            chat_flameIcon = resources.getDrawable(NUM).mutate();
            chat_gifIcon = resources.getDrawable(NUM).mutate();
            chat_fileStatesDrawable[0][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), NUM);
            chat_fileStatesDrawable[0][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), NUM);
            chat_fileStatesDrawable[1][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), NUM);
            chat_fileStatesDrawable[1][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), NUM);
            chat_fileStatesDrawable[2][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), NUM);
            chat_fileStatesDrawable[2][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), NUM);
            chat_fileStatesDrawable[3][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), NUM);
            chat_fileStatesDrawable[3][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), NUM);
            chat_fileStatesDrawable[4][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), NUM);
            chat_fileStatesDrawable[4][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), NUM);
            chat_fileStatesDrawable[5][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), NUM);
            chat_fileStatesDrawable[5][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), NUM);
            chat_fileStatesDrawable[6][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), NUM);
            chat_fileStatesDrawable[6][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), NUM);
            chat_fileStatesDrawable[7][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), NUM);
            chat_fileStatesDrawable[7][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), NUM);
            chat_fileStatesDrawable[8][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), NUM);
            chat_fileStatesDrawable[8][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), NUM);
            chat_fileStatesDrawable[9][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), NUM);
            chat_fileStatesDrawable[9][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), NUM);
            chat_photoStatesDrawables[0][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), NUM);
            chat_photoStatesDrawables[0][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), NUM);
            chat_photoStatesDrawables[1][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), NUM);
            chat_photoStatesDrawables[1][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), NUM);
            chat_photoStatesDrawables[2][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), NUM);
            chat_photoStatesDrawables[2][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), NUM);
            chat_photoStatesDrawables[3][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), NUM);
            chat_photoStatesDrawables[3][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), NUM);
            Drawable[][] drawableArr = chat_photoStatesDrawables;
            Drawable[] drawableArr2 = drawableArr[4];
            Drawable[] drawableArr3 = drawableArr[4];
            Drawable drawable = resources.getDrawable(NUM);
            drawableArr3[1] = drawable;
            drawableArr2[0] = drawable;
            Drawable[][] drawableArr4 = chat_photoStatesDrawables;
            Drawable[] drawableArr5 = drawableArr4[5];
            Drawable[] drawableArr6 = drawableArr4[5];
            Drawable drawable2 = resources.getDrawable(NUM);
            drawableArr6[1] = drawable2;
            drawableArr5[0] = drawable2;
            Drawable[][] drawableArr7 = chat_photoStatesDrawables;
            Drawable[] drawableArr8 = drawableArr7[6];
            Drawable[] drawableArr9 = drawableArr7[6];
            Drawable drawable3 = resources.getDrawable(NUM);
            drawableArr9[1] = drawable3;
            drawableArr8[0] = drawable3;
            chat_photoStatesDrawables[7][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), NUM);
            chat_photoStatesDrawables[7][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), NUM);
            chat_photoStatesDrawables[8][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), NUM);
            chat_photoStatesDrawables[8][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), NUM);
            chat_photoStatesDrawables[9][0] = resources.getDrawable(NUM).mutate();
            chat_photoStatesDrawables[9][1] = resources.getDrawable(NUM).mutate();
            chat_photoStatesDrawables[10][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), NUM);
            chat_photoStatesDrawables[10][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), NUM);
            chat_photoStatesDrawables[11][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), NUM);
            chat_photoStatesDrawables[11][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0f), NUM);
            chat_photoStatesDrawables[12][0] = resources.getDrawable(NUM).mutate();
            chat_photoStatesDrawables[12][1] = resources.getDrawable(NUM).mutate();
            chat_contactDrawable[0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), NUM);
            chat_contactDrawable[1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0f), NUM);
            chat_locationDrawable[0] = resources.getDrawable(NUM).mutate();
            chat_locationDrawable[1] = resources.getDrawable(NUM).mutate();
            chat_composeShadowDrawable = context.getResources().getDrawable(NUM).mutate();
            chat_composeShadowRoundDrawable = context.getResources().getDrawable(NUM).mutate();
            try {
                int bitmapSize = AndroidUtilities.dp(6.0f) + AndroidUtilities.roundMessageSize;
                Bitmap bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                Paint eraserPaint = new Paint(1);
                eraserPaint.setColor(0);
                eraserPaint.setStyle(Paint.Style.FILL);
                eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                Paint paint2 = new Paint(1);
                paint2.setShadowLayer((float) AndroidUtilities.dp(4.0f), 0.0f, 0.0f, NUM);
                int a2 = 0;
                for (int i = 2; a2 < i; i = 2) {
                    canvas.drawCircle((float) (bitmapSize / 2), (float) (bitmapSize / 2), (float) ((AndroidUtilities.roundMessageSize / i) - AndroidUtilities.dp(1.0f)), a2 == 0 ? paint2 : eraserPaint);
                    a2++;
                }
                try {
                    canvas.setBitmap((Bitmap) null);
                } catch (Exception e) {
                }
                chat_roundVideoShadow = new BitmapDrawable(bitmap);
            } catch (Throwable th) {
            }
            defaultChatDrawables.clear();
            defaultChatDrawableColorKeys.clear();
            addChatDrawable("drawableBotInline", chat_botInlineDrawable, "chat_serviceIcon");
            addChatDrawable("drawableBotLink", chat_botLinkDrawalbe, "chat_serviceIcon");
            addChatDrawable("drawableGoIcon", chat_goIconDrawable, "chat_serviceIcon");
            addChatDrawable("drawableCommentSticker", chat_commentStickerDrawable, "chat_serviceIcon");
            addChatDrawable("drawableMsgError", chat_msgErrorDrawable, "chat_sentErrorIcon");
            addChatDrawable("drawableMsgIn", chat_msgInDrawable, (String) null);
            addChatDrawable("drawableMsgInSelected", chat_msgInSelectedDrawable, (String) null);
            addChatDrawable("drawableMsgInMedia", chat_msgInMediaDrawable, (String) null);
            addChatDrawable("drawableMsgInMediaSelected", chat_msgInMediaSelectedDrawable, (String) null);
            addChatDrawable("drawableMsgOut", chat_msgOutDrawable, (String) null);
            addChatDrawable("drawableMsgOutSelected", chat_msgOutSelectedDrawable, (String) null);
            addChatDrawable("drawableMsgOutMedia", chat_msgOutMediaDrawable, (String) null);
            addChatDrawable("drawableMsgOutMediaSelected", chat_msgOutMediaSelectedDrawable, (String) null);
            addChatDrawable("drawableMsgOutCallAudio", chat_msgOutCallDrawable[0], "chat_outInstant");
            addChatDrawable("drawableMsgOutCallAudioSelected", chat_msgOutCallSelectedDrawable[0], "chat_outInstantSelected");
            addChatDrawable("drawableMsgOutCallVideo", chat_msgOutCallDrawable[1], "chat_outInstant");
            addChatDrawable("drawableMsgOutCallVideo", chat_msgOutCallSelectedDrawable[1], "chat_outInstantSelected");
            addChatDrawable("drawableMsgOutCheck", chat_msgOutCheckDrawable, "chat_outSentCheck");
            addChatDrawable("drawableMsgOutCheckSelected", chat_msgOutCheckSelectedDrawable, "chat_outSentCheckSelected");
            addChatDrawable("drawableMsgOutCheckRead", chat_msgOutCheckReadDrawable, "chat_outSentCheckRead");
            addChatDrawable("drawableMsgOutCheckReadSelected", chat_msgOutCheckReadSelectedDrawable, "chat_outSentCheckReadSelected");
            addChatDrawable("drawableMsgOutHalfCheck", chat_msgOutHalfCheckDrawable, "chat_outSentCheckRead");
            addChatDrawable("drawableMsgOutHalfCheckSelected", chat_msgOutHalfCheckSelectedDrawable, "chat_outSentCheckReadSelected");
            addChatDrawable("drawableMsgOutInstant", chat_msgOutInstantDrawable, "chat_outInstant");
            addChatDrawable("drawableMsgOutMenu", chat_msgOutMenuDrawable, "chat_outMenu");
            addChatDrawable("drawableMsgOutMenuSelected", chat_msgOutMenuSelectedDrawable, "chat_outMenuSelected");
            addChatDrawable("drawableMsgOutPinned", chat_msgOutPinnedDrawable, "chat_outViews");
            addChatDrawable("drawableMsgOutPinnedSelected", chat_msgOutPinnedSelectedDrawable, "chat_outViewsSelected");
            addChatDrawable("drawableMsgOutReplies", chat_msgOutRepliesDrawable, "chat_outViews");
            addChatDrawable("drawableMsgOutReplies", chat_msgOutRepliesSelectedDrawable, "chat_outViewsSelected");
            addChatDrawable("drawableMsgOutViews", chat_msgOutViewsDrawable, "chat_outViews");
            addChatDrawable("drawableMsgOutViewsSelected", chat_msgOutViewsSelectedDrawable, "chat_outViewsSelected");
            addChatDrawable("drawableMsgStickerCheck", chat_msgStickerCheckDrawable, "chat_serviceText");
            addChatDrawable("drawableMsgStickerHalfCheck", chat_msgStickerHalfCheckDrawable, "chat_serviceText");
            addChatDrawable("drawableMsgStickerPinned", chat_msgStickerPinnedDrawable, "chat_serviceText");
            addChatDrawable("drawableMsgStickerReplies", chat_msgStickerRepliesDrawable, "chat_serviceText");
            addChatDrawable("drawableMsgStickerViews", chat_msgStickerViewsDrawable, "chat_serviceText");
            addChatDrawable("drawableReplyIcon", chat_replyIconDrawable, "chat_serviceIcon");
            addChatDrawable("drawableShareIcon", chat_shareIconDrawable, "chat_serviceIcon");
            addChatDrawable("drawableMuteIcon", chat_muteIconDrawable, "chat_muteIcon");
            addChatDrawable("drawableLockIcon", chat_lockIconDrawable, "chat_lockIcon");
            addChatDrawable("drawable_chat_pollHintDrawableOut", chat_pollHintDrawable[1], "chat_outPreviewInstantText");
            addChatDrawable("drawable_chat_pollHintDrawableIn", chat_pollHintDrawable[0], "chat_inPreviewInstantText");
            applyChatTheme(z, false);
        }
        if (!z && (paint = chat_botProgressPaint) != null) {
            paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            chat_infoPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
            chat_stickerCommentCountPaint.setTextSize((float) AndroidUtilities.dp(11.0f));
            chat_docNamePaint.setTextSize((float) AndroidUtilities.dp(15.0f));
            chat_locationTitlePaint.setTextSize((float) AndroidUtilities.dp(15.0f));
            chat_locationAddressPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
            chat_audioTimePaint.setTextSize((float) AndroidUtilities.dp(12.0f));
            chat_livePaint.setTextSize((float) AndroidUtilities.dp(12.0f));
            chat_audioTitlePaint.setTextSize((float) AndroidUtilities.dp(16.0f));
            chat_audioPerformerPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
            chat_botButtonPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
            chat_contactNamePaint.setTextSize((float) AndroidUtilities.dp(15.0f));
            chat_contactPhonePaint.setTextSize((float) AndroidUtilities.dp(13.0f));
            chat_durationPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
            chat_timePaint.setTextSize((float) AndroidUtilities.dp(12.0f));
            chat_adminPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
            chat_namePaint.setTextSize((float) AndroidUtilities.dp(14.0f));
            chat_forwardNamePaint.setTextSize((float) AndroidUtilities.dp(14.0f));
            chat_replyNamePaint.setTextSize((float) AndroidUtilities.dp(14.0f));
            chat_replyTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
            chat_gamePaint.setTextSize((float) AndroidUtilities.dp(13.0f));
            chat_shipmentPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
            chat_instantViewPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
            chat_instantViewRectPaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
            chat_pollTimerPaint.setStrokeWidth((float) AndroidUtilities.dp(1.1f));
            chat_actionTextPaint.setTextSize((float) AndroidUtilities.dp((float) (Math.max(16, SharedConfig.fontSize) - 2)));
            chat_contextResult_titleTextPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
            chat_contextResult_descriptionTextPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
            chat_radialProgressPaint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
            chat_radialProgress2Paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        }
    }

    public static void refreshAttachButtonsColors() {
        int a = 0;
        while (true) {
            RLottieDrawable[] rLottieDrawableArr = chat_attachButtonDrawables;
            if (a < rLottieDrawableArr.length) {
                if (rLottieDrawableArr[a] != null) {
                    rLottieDrawableArr[a].beginApplyLayerColors();
                    if (a == 0) {
                        chat_attachButtonDrawables[a].setLayerColor("Color_Mount.**", getNonAnimatedColor("chat_attachGalleryBackground"));
                        chat_attachButtonDrawables[a].setLayerColor("Color_PhotoShadow.**", getNonAnimatedColor("chat_attachGalleryBackground"));
                        chat_attachButtonDrawables[a].setLayerColor("White_Photo.**", getNonAnimatedColor("chat_attachGalleryIcon"));
                        chat_attachButtonDrawables[a].setLayerColor("White_BackPhoto.**", getNonAnimatedColor("chat_attachGalleryIcon"));
                    } else if (a == 1) {
                        chat_attachButtonDrawables[a].setLayerColor("White_Play1.**", getNonAnimatedColor("chat_attachAudioIcon"));
                        chat_attachButtonDrawables[a].setLayerColor("White_Play2.**", getNonAnimatedColor("chat_attachAudioIcon"));
                    } else if (a == 2) {
                        chat_attachButtonDrawables[a].setLayerColor("Color_Corner.**", getNonAnimatedColor("chat_attachFileBackground"));
                        chat_attachButtonDrawables[a].setLayerColor("White_List.**", getNonAnimatedColor("chat_attachFileIcon"));
                    } else if (a == 3) {
                        chat_attachButtonDrawables[a].setLayerColor("White_User1.**", getNonAnimatedColor("chat_attachContactIcon"));
                        chat_attachButtonDrawables[a].setLayerColor("White_User2.**", getNonAnimatedColor("chat_attachContactIcon"));
                    } else if (a == 4) {
                        chat_attachButtonDrawables[a].setLayerColor("Color_Oval.**", getNonAnimatedColor("chat_attachLocationBackground"));
                        chat_attachButtonDrawables[a].setLayerColor("White_Pin.**", getNonAnimatedColor("chat_attachLocationIcon"));
                    } else if (a == 5) {
                        chat_attachButtonDrawables[a].setLayerColor("White_Column 1.**", getNonAnimatedColor("chat_attachPollIcon"));
                        chat_attachButtonDrawables[a].setLayerColor("White_Column 2.**", getNonAnimatedColor("chat_attachPollIcon"));
                        chat_attachButtonDrawables[a].setLayerColor("White_Column 3.**", getNonAnimatedColor("chat_attachPollIcon"));
                    }
                    chat_attachButtonDrawables[a].commitApplyLayerColors();
                }
                a++;
            } else {
                return;
            }
        }
    }

    public static void applyChatTheme(boolean fontsOnly, boolean bg) {
        int color;
        if (chat_msgTextPaint != null && chat_msgInDrawable != null && !fontsOnly) {
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
            for (int a = 0; a < 2; a++) {
                setDrawableColorByKey(chat_msgInCallDrawable[a], "chat_inInstant");
                setDrawableColorByKey(chat_msgInCallSelectedDrawable[a], "chat_inInstantSelected");
                setDrawableColorByKey(chat_msgOutCallDrawable[a], "chat_outInstant");
                setDrawableColorByKey(chat_msgOutCallSelectedDrawable[a], "chat_outInstantSelected");
            }
            setDrawableColorByKey(chat_msgCallUpGreenDrawable, "chat_outUpCall");
            setDrawableColorByKey(chat_msgCallDownRedDrawable, "chat_inUpCall");
            setDrawableColorByKey(chat_msgCallDownGreenDrawable, "chat_inDownCall");
            setDrawableColorByKey(calllog_msgCallUpRedDrawable, "calls_callReceivedRedIcon");
            setDrawableColorByKey(calllog_msgCallUpGreenDrawable, "calls_callReceivedGreenIcon");
            setDrawableColorByKey(calllog_msgCallDownRedDrawable, "calls_callReceivedRedIcon");
            setDrawableColorByKey(calllog_msgCallDownGreenDrawable, "calls_callReceivedGreenIcon");
            int i = 0;
            while (true) {
                StatusDrawable[] statusDrawableArr = chat_status_drawables;
                if (i >= statusDrawableArr.length) {
                    break;
                }
                setDrawableColorByKey(statusDrawableArr[i], "chats_actionMessage");
                i++;
            }
            for (int a2 = 0; a2 < 2; a2++) {
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[a2][0], getColor("chat_outLoader"), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[a2][0], getColor("chat_outMediaIcon"), true);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[a2][1], getColor("chat_outLoaderSelected"), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[a2][1], getColor("chat_outMediaIconSelected"), true);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[a2 + 2][0], getColor("chat_inLoader"), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[a2 + 2][0], getColor("chat_inMediaIcon"), true);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[a2 + 2][1], getColor("chat_inLoaderSelected"), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[a2 + 2][1], getColor("chat_inMediaIconSelected"), true);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[a2 + 4][0], getColor("chat_mediaLoaderPhoto"), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[a2 + 4][0], getColor("chat_mediaLoaderPhotoIcon"), true);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[a2 + 4][1], getColor("chat_mediaLoaderPhotoSelected"), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[a2 + 4][1], getColor("chat_mediaLoaderPhotoIconSelected"), true);
            }
            for (int a3 = 0; a3 < 5; a3++) {
                setCombinedDrawableColor(chat_fileStatesDrawable[a3][0], getColor("chat_outLoader"), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[a3][0], getColor("chat_outMediaIcon"), true);
                setCombinedDrawableColor(chat_fileStatesDrawable[a3][1], getColor("chat_outLoaderSelected"), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[a3][1], getColor("chat_outMediaIconSelected"), true);
                setCombinedDrawableColor(chat_fileStatesDrawable[a3 + 5][0], getColor("chat_inLoader"), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[a3 + 5][0], getColor("chat_inMediaIcon"), true);
                setCombinedDrawableColor(chat_fileStatesDrawable[a3 + 5][1], getColor("chat_inLoaderSelected"), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[a3 + 5][1], getColor("chat_inMediaIconSelected"), true);
            }
            for (int a4 = 0; a4 < 4; a4++) {
                setCombinedDrawableColor(chat_photoStatesDrawables[a4][0], getColor("chat_mediaLoaderPhoto"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[a4][0], getColor("chat_mediaLoaderPhotoIcon"), true);
                setCombinedDrawableColor(chat_photoStatesDrawables[a4][1], getColor("chat_mediaLoaderPhotoSelected"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[a4][1], getColor("chat_mediaLoaderPhotoIconSelected"), true);
            }
            for (int a5 = 0; a5 < 2; a5++) {
                setCombinedDrawableColor(chat_photoStatesDrawables[a5 + 7][0], getColor("chat_outLoaderPhoto"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[a5 + 7][0], getColor("chat_outLoaderPhotoIcon"), true);
                setCombinedDrawableColor(chat_photoStatesDrawables[a5 + 7][1], getColor("chat_outLoaderPhotoSelected"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[a5 + 7][1], getColor("chat_outLoaderPhotoIconSelected"), true);
                setCombinedDrawableColor(chat_photoStatesDrawables[a5 + 10][0], getColor("chat_inLoaderPhoto"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[a5 + 10][0], getColor("chat_inLoaderPhotoIcon"), true);
                setCombinedDrawableColor(chat_photoStatesDrawables[a5 + 10][1], getColor("chat_inLoaderPhotoSelected"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[a5 + 10][1], getColor("chat_inLoaderPhotoIconSelected"), true);
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
            setDrawableColorByKey(chat_composeShadowRoundDrawable, "chat_messagePanelBackground");
            if (getColor("chat_outAudioSeekbarFill") == -1) {
                color = getColor("chat_outBubble");
            } else {
                color = -1;
            }
            setDrawableColor(chat_pollCheckDrawable[1], color);
            setDrawableColor(chat_pollCrossDrawable[1], color);
            setDrawableColor(chat_attachEmptyDrawable, getColor("chat_attachEmptyImage"));
            if (!bg) {
                applyChatServiceMessageColor();
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

    public static void applyServiceShaderMatrixForView(View view, View background) {
        if (view != null && background != null) {
            view.getLocationOnScreen(viewPos);
            int[] iArr = viewPos;
            int x = iArr[0];
            int y = iArr[1];
            background.getLocationOnScreen(iArr);
            applyServiceShaderMatrix(background.getMeasuredWidth(), background.getMeasuredHeight(), (float) x, (float) (y - viewPos[1]));
        }
    }

    public static void applyServiceShaderMatrix(int w, int h, float translationX, float translationY) {
        applyServiceShaderMatrix(serviceBitmap, serviceBitmapShader, serviceBitmapMatrix, w, h, translationX, translationY);
    }

    public static void applyServiceShaderMatrix(Bitmap bitmap, BitmapShader shader, Matrix matrix, int w, int h, float translationX, float translationY) {
        if (shader != null) {
            float bitmapWidth = (float) bitmap.getWidth();
            float bitmapHeight = (float) bitmap.getHeight();
            float maxScale = Math.max(((float) w) / bitmapWidth, ((float) h) / bitmapHeight);
            matrix.reset();
            matrix.setTranslate(((((float) w) - (bitmapWidth * maxScale)) / 2.0f) - translationX, ((((float) h) - (bitmapHeight * maxScale)) / 2.0f) - translationY);
            matrix.preScale(maxScale, maxScale);
            shader.setLocalMatrix(matrix);
        }
    }

    public static void applyChatServiceMessageColor(int[] custom, Drawable wallpaperOverride) {
        applyChatServiceMessageColor(custom, wallpaperOverride, wallpaper);
    }

    public static void applyChatServiceMessageColor(int[] custom, Drawable wallpaperOverride, Drawable currentWallpaper) {
        Integer servicePressedColor;
        Integer serviceColor;
        if (chat_actionBackgroundPaint != null) {
            serviceMessageColor = serviceMessageColorBackup;
            serviceSelectedMessageColor = serviceSelectedMessageColorBackup;
            boolean drawServiceGradient = true;
            if (custom == null || custom.length < 2) {
                serviceColor = currentColors.get("chat_serviceBackground");
                servicePressedColor = currentColors.get("chat_serviceBackgroundSelected");
            } else {
                serviceColor = Integer.valueOf(custom[0]);
                servicePressedColor = Integer.valueOf(custom[1]);
                serviceMessageColor = custom[0];
                serviceSelectedMessageColor = custom[1];
            }
            Integer serviceColor2 = serviceColor;
            Integer servicePressedColor2 = servicePressedColor;
            if (serviceColor == null) {
                serviceColor = Integer.valueOf(serviceMessageColor);
                serviceColor2 = Integer.valueOf(serviceMessage2Color);
            }
            if (servicePressedColor == null) {
                servicePressedColor = Integer.valueOf(serviceSelectedMessageColor);
            }
            if (servicePressedColor2 == null) {
                servicePressedColor2 = Integer.valueOf(serviceSelectedMessage2Color);
            }
            Drawable drawable = wallpaperOverride != null ? wallpaperOverride : currentWallpaper;
            if (!(drawable instanceof MotionBackgroundDrawable) || SharedConfig.getDevicePerformanceClass() == 0) {
                drawServiceGradient = false;
            }
            if (drawServiceGradient) {
                Bitmap newBitmap = ((MotionBackgroundDrawable) drawable).getBitmap();
                if (serviceBitmap != newBitmap) {
                    serviceBitmap = newBitmap;
                    serviceBitmapShader = new BitmapShader(serviceBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
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
                setDrawableColor(chat_botLinkDrawalbe, -1);
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
                setDrawableColorByKey(chat_botLinkDrawalbe, "chat_serviceIcon");
                chat_botButtonPaint.setColor(getColor("chat_botButtonText"));
            }
            chat_actionBackgroundPaint.setColor(serviceColor.intValue());
            chat_actionBackgroundSelectedPaint.setColor(servicePressedColor.intValue());
            chat_actionBackgroundPaint2.setColor(serviceColor2.intValue());
            chat_actionBackgroundSelectedPaint2.setColor(servicePressedColor2.intValue());
            currentColor = serviceColor.intValue();
            if (serviceBitmapShader == null || (currentColors.get("chat_serviceBackground") != null && !(drawable instanceof MotionBackgroundDrawable))) {
                chat_actionBackgroundPaint.setColorFilter((ColorFilter) null);
                chat_actionBackgroundPaint.setShader((Shader) null);
                chat_actionBackgroundSelectedPaint.setColorFilter((ColorFilter) null);
                chat_actionBackgroundSelectedPaint.setShader((Shader) null);
                return;
            }
            chat_actionBackgroundPaint.setShader(serviceBitmapShader);
            chat_actionBackgroundSelectedPaint.setShader(serviceBitmapShader);
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(((MotionBackgroundDrawable) drawable).getIntensity() >= 0 ? 1.8f : 0.5f);
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

    public static ColorFilter getShareColorFilter(int color, boolean selected) {
        if (selected) {
            if (currentShareSelectedColorFilter == null || currentShareSelectedColorFilterColor != color) {
                currentShareSelectedColorFilterColor = color;
                currentShareSelectedColorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY);
            }
            return currentShareSelectedColorFilter;
        }
        if (currentShareColorFilter == null || currentShareColorFilterColor != color) {
            currentShareColorFilterColor = color;
            currentShareColorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY);
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

    public static Drawable getThemedDrawable(Context context, int resId, String key) {
        return getThemedDrawable(context, resId, getColor(key));
    }

    public static Drawable getThemedDrawable(Context context, int resId, int color) {
        if (context == null) {
            return null;
        }
        Drawable drawable = context.getResources().getDrawable(resId).mutate();
        drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        return drawable;
    }

    public static int getDefaultColor(String key) {
        Integer value = defaultColors.get(key);
        if (value != null) {
            return value.intValue();
        }
        if (key.equals("chats_menuTopShadow") || key.equals("chats_menuTopBackground") || key.equals("chats_menuTopShadowCats") || key.equals("key_chat_wallpaper_gradient_to2") || key.equals("key_chat_wallpaper_gradient_to3")) {
            return 0;
        }
        return -65536;
    }

    public static boolean hasThemeKey(String key) {
        return currentColors.containsKey(key);
    }

    public static Integer getColorOrNull(String key) {
        Integer color = currentColors.get(key);
        if (color == null) {
            if (fallbackKeys.get(key) != null) {
                color = currentColors.get(key);
            }
            if (color == null) {
                color = defaultColors.get(key);
            }
        }
        if (color == null) {
            return color;
        }
        if ("windowBackgroundWhite".equals(key) || "windowBackgroundGray".equals(key) || "actionBarDefault".equals(key) || "actionBarDefaultArchived".equals(key)) {
            return Integer.valueOf(color.intValue() | -16777216);
        }
        return color;
    }

    public static void setAnimatingColor(boolean animating) {
        animatingColors = animating ? new HashMap<>() : null;
    }

    public static boolean isAnimatingColor() {
        return animatingColors != null;
    }

    public static void setAnimatedColor(String key, int value) {
        HashMap<String, Integer> hashMap = animatingColors;
        if (hashMap != null) {
            hashMap.put(key, Integer.valueOf(value));
        }
    }

    public static int getDefaultAccentColor(String key) {
        ThemeAccent accent;
        Integer color = currentColorsNoAccent.get(key);
        if (color == null || (accent = currentTheme.getAccent(false)) == null) {
            return 0;
        }
        float[] hsvTemp1 = getTempHsv(1);
        float[] hsvTemp2 = getTempHsv(2);
        Color.colorToHSV(currentTheme.accentBaseColor, hsvTemp1);
        Color.colorToHSV(accent.accentColor, hsvTemp2);
        return changeColorAccent(hsvTemp1, hsvTemp2, color.intValue(), currentTheme.isDark());
    }

    public static int getNonAnimatedColor(String key) {
        return getColor(key, (boolean[]) null, true);
    }

    public static int getColor(String key, ResourcesProvider provider) {
        Integer colorInteger;
        if (provider == null || (colorInteger = provider.getColor(key)) == null) {
            return getColor(key);
        }
        return colorInteger.intValue();
    }

    public static int getColor(String key) {
        return getColor(key, (boolean[]) null, false);
    }

    public static int getColor(String key, boolean[] isDefault) {
        return getColor(key, isDefault, false);
    }

    public static int getColor(String key, boolean[] isDefault, boolean ignoreAnimation) {
        boolean useDefault;
        HashMap<String, Integer> hashMap;
        Integer color;
        if (!ignoreAnimation && (hashMap = animatingColors) != null && (color = hashMap.get(key)) != null) {
            return color.intValue();
        }
        if (serviceBitmapShader != null && ("chat_serviceText".equals(key) || "chat_serviceLink".equals(key) || "chat_serviceIcon".equals(key) || "chat_stickerReplyLine".equals(key) || "chat_stickerReplyNameText".equals(key) || "chat_stickerReplyMessageText".equals(key))) {
            return -1;
        }
        if (currentTheme == defaultTheme) {
            if (myMessagesBubblesColorKeys.contains(key)) {
                useDefault = currentTheme.isDefaultMyMessagesBubbles();
            } else if (myMessagesColorKeys.contains(key)) {
                useDefault = currentTheme.isDefaultMyMessages();
            } else if ("chat_wallpaper".equals(key) || "chat_wallpaper_gradient_to".equals(key) || "key_chat_wallpaper_gradient_to2".equals(key) || "key_chat_wallpaper_gradient_to3".equals(key)) {
                useDefault = false;
            } else {
                useDefault = currentTheme.isDefaultMainAccent();
            }
            if (useDefault) {
                if (key.equals("chat_serviceBackground")) {
                    return serviceMessageColor;
                }
                if (key.equals("chat_serviceBackgroundSelected")) {
                    return serviceSelectedMessageColor;
                }
                return getDefaultColor(key);
            }
        }
        Integer color2 = currentColors.get(key);
        if (color2 == null) {
            String fallbackKey = fallbackKeys.get(key);
            if (fallbackKey != null) {
                color2 = currentColors.get(fallbackKey);
            }
            if (color2 == null) {
                if (isDefault != null) {
                    isDefault[0] = true;
                }
                if (key.equals("chat_serviceBackground")) {
                    return serviceMessageColor;
                }
                if (key.equals("chat_serviceBackgroundSelected")) {
                    return serviceSelectedMessageColor;
                }
                return getDefaultColor(key);
            }
        }
        if ("windowBackgroundWhite".equals(key) || "windowBackgroundGray".equals(key) || "actionBarDefault".equals(key) || "actionBarDefaultArchived".equals(key)) {
            color2 = Integer.valueOf(color2.intValue() | -16777216);
        }
        return color2.intValue();
    }

    public static void setColor(String key, int color, boolean useDefault) {
        if (key.equals("chat_wallpaper") || key.equals("chat_wallpaper_gradient_to") || key.equals("key_chat_wallpaper_gradient_to2") || key.equals("key_chat_wallpaper_gradient_to3") || key.equals("windowBackgroundWhite") || key.equals("windowBackgroundGray") || key.equals("actionBarDefault") || key.equals("actionBarDefaultArchived")) {
            color |= -16777216;
        }
        if (useDefault) {
            currentColors.remove(key);
        } else {
            currentColors.put(key, Integer.valueOf(color));
        }
        char c = 65535;
        switch (key.hashCode()) {
            case -2095843767:
                if (key.equals("chat_wallpaper_gradient_rotation")) {
                    c = 6;
                    break;
                }
                break;
            case -1625862693:
                if (key.equals("chat_wallpaper")) {
                    c = 2;
                    break;
                }
                break;
            case -1397026623:
                if (key.equals("windowBackgroundGray")) {
                    c = 8;
                    break;
                }
                break;
            case -633951866:
                if (key.equals("chat_wallpaper_gradient_to")) {
                    c = 3;
                    break;
                }
                break;
            case -552118908:
                if (key.equals("actionBarDefault")) {
                    c = 7;
                    break;
                }
                break;
            case 426061980:
                if (key.equals("chat_serviceBackground")) {
                    c = 0;
                    break;
                }
                break;
            case 1381936524:
                if (key.equals("key_chat_wallpaper_gradient_to2")) {
                    c = 4;
                    break;
                }
                break;
            case 1381936525:
                if (key.equals("key_chat_wallpaper_gradient_to3")) {
                    c = 5;
                    break;
                }
                break;
            case 1573464919:
                if (key.equals("chat_serviceBackgroundSelected")) {
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
            case 5:
            case 6:
                reloadWallpaper();
                return;
            case 7:
                if (Build.VERSION.SDK_INT >= 23) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, new Object[0]);
                    return;
                }
                return;
            case 8:
                if (Build.VERSION.SDK_INT >= 26) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, new Object[0]);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public static void setDefaultColor(String key, int color) {
        defaultColors.put(key, Integer.valueOf(color));
    }

    public static void setThemeWallpaper(ThemeInfo themeInfo, Bitmap bitmap, File path) {
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

    public static void setDrawableColor(Drawable drawable, int color) {
        if (drawable != null) {
            if (drawable instanceof StatusDrawable) {
                ((StatusDrawable) drawable).setColor(color);
            } else if (drawable instanceof MsgClockDrawable) {
                ((MsgClockDrawable) drawable).setColor(color);
            } else if (drawable instanceof ShapeDrawable) {
                ((ShapeDrawable) drawable).getPaint().setColor(color);
            } else if (drawable instanceof ScamDrawable) {
                ((ScamDrawable) drawable).setColor(color);
            } else {
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
            }
        }
    }

    public static void setDrawableColorByKey(Drawable drawable, String key) {
        if (key != null) {
            setDrawableColor(drawable, getColor(key));
        }
    }

    public static void setEmojiDrawableColor(Drawable drawable, int color, boolean selected) {
        Drawable state;
        if (drawable instanceof StateListDrawable) {
            if (selected) {
                try {
                    state = getStateDrawable(drawable, 0);
                } catch (Throwable th) {
                    return;
                }
            } else {
                state = getStateDrawable(drawable, 1);
            }
            if (state instanceof ShapeDrawable) {
                ((ShapeDrawable) state).getPaint().setColor(color);
            } else {
                state.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
            }
        }
    }

    public static void setRippleDrawableForceSoftware(RippleDrawable drawable) {
        if (drawable != null) {
            Class<RippleDrawable> cls = RippleDrawable.class;
            try {
                cls.getDeclaredMethod("setForceSoftware", new Class[]{Boolean.TYPE}).invoke(drawable, new Object[]{true});
            } catch (Throwable th) {
            }
        }
    }

    public static void setSelectorDrawableColor(Drawable drawable, int color, boolean selected) {
        Drawable state;
        if (drawable instanceof StateListDrawable) {
            if (selected) {
                try {
                    Drawable state2 = getStateDrawable(drawable, 0);
                    if (state2 instanceof ShapeDrawable) {
                        ((ShapeDrawable) state2).getPaint().setColor(color);
                    } else {
                        state2.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                    }
                    state = getStateDrawable(drawable, 1);
                } catch (Throwable th) {
                    return;
                }
            } else {
                state = getStateDrawable(drawable, 2);
            }
            if (state instanceof ShapeDrawable) {
                ((ShapeDrawable) state).getPaint().setColor(color);
            } else {
                state.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
            }
        } else if (Build.VERSION.SDK_INT >= 21 && (drawable instanceof RippleDrawable)) {
            RippleDrawable rippleDrawable = (RippleDrawable) drawable;
            if (selected) {
                rippleDrawable.setColor(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{color}));
            } else if (rippleDrawable.getNumberOfLayers() > 0) {
                Drawable drawable1 = rippleDrawable.getDrawable(0);
                if (drawable1 instanceof ShapeDrawable) {
                    ((ShapeDrawable) drawable1).getPaint().setColor(color);
                } else {
                    drawable1.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                }
            }
        }
    }

    public static boolean isThemeWallpaperPublic() {
        return !TextUtils.isEmpty(themedWallpaperLink);
    }

    public static boolean hasWallpaperFromTheme() {
        if (currentTheme.firstAccentIsDefault && currentTheme.currentAccentId == DEFALT_THEME_ACCENT_ID) {
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

    private static void calcBackgroundColor(Drawable drawable, int save) {
        if (save != 2) {
            int[] result = AndroidUtilities.calcDrawableColor(drawable);
            int i = result[0];
            serviceMessageColorBackup = i;
            serviceMessageColor = i;
            int i2 = result[1];
            serviceSelectedMessageColorBackup = i2;
            serviceSelectedMessageColor = i2;
            serviceMessage2Color = result[2];
            serviceSelectedMessage2Color = result[3];
        }
    }

    public static int getServiceMessageColor() {
        Integer serviceColor = currentColors.get("chat_serviceBackground");
        return serviceColor == null ? serviceMessageColor : serviceColor.intValue();
    }

    public static void loadWallpaper() {
        boolean wallpaperMotion;
        File wallpaperFile;
        TLRPC.Document wallpaperDocument;
        int intensity;
        if (wallpaper == null) {
            boolean defaultTheme2 = currentTheme.firstAccentIsDefault && currentTheme.currentAccentId == DEFALT_THEME_ACCENT_ID;
            ThemeAccent accent = currentTheme.getAccent(false);
            TLRPC.Document wallpaperDocument2 = null;
            if (accent != null) {
                File wallpaperFile2 = accent.getPathToWallpaper();
                boolean wallpaperMotion2 = accent.patternMotion;
                TLRPC.ThemeSettings settings = null;
                if (accent.info != null && accent.info.settings.size() > 0) {
                    settings = accent.info.settings.get(0);
                }
                if (!(accent.info == null || settings == null || settings.wallpaper == null)) {
                    wallpaperDocument2 = settings.wallpaper.document;
                }
                wallpaperDocument = wallpaperDocument2;
                wallpaperFile = wallpaperFile2;
                wallpaperMotion = wallpaperMotion2;
            } else {
                wallpaperDocument = null;
                wallpaperFile = null;
                wallpaperMotion = false;
            }
            OverrideWallpaperInfo overrideWallpaper = currentTheme.overrideWallpaper;
            if (overrideWallpaper != null) {
                intensity = (int) (overrideWallpaper.intensity * 100.0f);
            } else {
                intensity = (int) (accent != null ? accent.patternIntensity * 100.0f : (float) currentTheme.patternIntensity);
            }
            DispatchQueue dispatchQueue = Utilities.themeQueue;
            Theme$$ExternalSyntheticLambda7 theme$$ExternalSyntheticLambda7 = new Theme$$ExternalSyntheticLambda7(overrideWallpaper, wallpaperFile, intensity, defaultTheme2, wallpaperMotion, wallpaperDocument);
            wallpaperLoadTask = theme$$ExternalSyntheticLambda7;
            dispatchQueue.postRunnable(theme$$ExternalSyntheticLambda7);
        }
    }

    static /* synthetic */ void lambda$loadWallpaper$8(OverrideWallpaperInfo overrideWallpaper, File wallpaperFile, int intensity, boolean defaultTheme2, boolean wallpaperMotion, TLRPC.Document finalWallpaperDocument) {
        BackgroundDrawableSettings settings = createBackgroundDrawable(currentTheme, overrideWallpaper, currentColors, wallpaperFile, themedWallpaperLink, themedWallpaperFileOffset, intensity, previousPhase, defaultTheme2, hasPreviousTheme, isApplyingAccent, wallpaperMotion, finalWallpaperDocument);
        isWallpaperMotion = settings.isWallpaperMotion != null ? settings.isWallpaperMotion.booleanValue() : isWallpaperMotion;
        isPatternWallpaper = settings.isPatternWallpaper != null ? settings.isPatternWallpaper.booleanValue() : isPatternWallpaper;
        isCustomTheme = settings.isCustomTheme != null ? settings.isCustomTheme.booleanValue() : isCustomTheme;
        patternIntensity = intensity;
        wallpaper = settings.wallpaper != null ? settings.wallpaper : wallpaper;
        Drawable drawable = settings.wallpaper;
        calcBackgroundColor(drawable, 1);
        AndroidUtilities.runOnUIThread(new Theme$$ExternalSyntheticLambda3(drawable));
    }

    static /* synthetic */ void lambda$loadWallpaper$7(Drawable drawable) {
        wallpaperLoadTask = null;
        createCommonChatResources();
        applyChatServiceMessageColor((int[]) null, (Drawable) null, drawable);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewWallpapper, new Object[0]);
    }

    public static BackgroundDrawableSettings createBackgroundDrawable(ThemeInfo currentTheme2, HashMap<String, Integer> currentColors2, String wallpaperLink, int prevoiusPhase) {
        int intensity;
        ThemeInfo themeInfo = currentTheme2;
        boolean defaultTheme2 = themeInfo.firstAccentIsDefault && themeInfo.currentAccentId == DEFALT_THEME_ACCENT_ID;
        ThemeAccent accent = themeInfo.getAccent(false);
        File wallpaperFile = accent != null ? accent.getPathToWallpaper() : null;
        boolean wallpaperMotion = accent != null && accent.patternMotion;
        OverrideWallpaperInfo overrideWallpaper = themeInfo.overrideWallpaper;
        if (overrideWallpaper != null) {
            intensity = (int) (overrideWallpaper.intensity * 100.0f);
        } else {
            intensity = (int) (accent != null ? accent.patternIntensity * 100.0f : (float) themeInfo.patternIntensity);
        }
        Integer offset = currentColorsNoAccent.get("wallpaperFileOffset");
        return createBackgroundDrawable(currentTheme2, overrideWallpaper, currentColors2, wallpaperFile, wallpaperLink, offset != null ? offset.intValue() : -1, intensity, prevoiusPhase, defaultTheme2, false, false, wallpaperMotion, (TLRPC.Document) null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:112:0x027b  */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x0374  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.telegram.ui.ActionBar.Theme.BackgroundDrawableSettings createBackgroundDrawable(org.telegram.ui.ActionBar.Theme.ThemeInfo r31, org.telegram.ui.ActionBar.Theme.OverrideWallpaperInfo r32, java.util.HashMap<java.lang.String, java.lang.Integer> r33, java.io.File r34, java.lang.String r35, int r36, int r37, int r38, boolean r39, boolean r40, boolean r41, boolean r42, org.telegram.tgnet.TLRPC.Document r43) {
        /*
            r1 = r31
            r2 = r32
            r3 = r33
            r4 = r34
            r5 = r36
            r6 = r37
            r7 = r38
            r8 = r43
            org.telegram.ui.ActionBar.Theme$BackgroundDrawableSettings r0 = new org.telegram.ui.ActionBar.Theme$BackgroundDrawableSettings
            r0.<init>()
            r9 = r0
            android.graphics.drawable.Drawable r0 = wallpaper
            r9.wallpaper = r0
            r10 = 1
            java.lang.Boolean r11 = java.lang.Boolean.valueOf(r10)
            r12 = 0
            java.lang.Boolean r13 = java.lang.Boolean.valueOf(r12)
            if (r40 == 0) goto L_0x0028
            if (r41 == 0) goto L_0x002c
        L_0x0028:
            if (r2 == 0) goto L_0x002c
            r0 = 1
            goto L_0x002d
        L_0x002c:
            r0 = 0
        L_0x002d:
            r14 = r0
            if (r2 == 0) goto L_0x0052
            boolean r0 = r2.isMotion
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r0)
            r9.isWallpaperMotion = r0
            int r0 = r2.color
            if (r0 == 0) goto L_0x004a
            boolean r0 = r32.isDefault()
            if (r0 != 0) goto L_0x004a
            boolean r0 = r32.isColor()
            if (r0 != 0) goto L_0x004a
            r0 = 1
            goto L_0x004b
        L_0x004a:
            r0 = 0
        L_0x004b:
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r0)
            r9.isPatternWallpaper = r0
            goto L_0x0067
        L_0x0052:
            boolean r0 = r1.isMotion
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r0)
            r9.isWallpaperMotion = r0
            int r0 = r1.patternBgColor
            if (r0 == 0) goto L_0x0060
            r0 = 1
            goto L_0x0061
        L_0x0060:
            r0 = 0
        L_0x0061:
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r0)
            r9.isPatternWallpaper = r0
        L_0x0067:
            r16 = r11
            if (r14 != 0) goto L_0x0271
            if (r39 == 0) goto L_0x006f
            r0 = 0
            goto L_0x0077
        L_0x006f:
            java.lang.String r0 = "chat_wallpaper"
            java.lang.Object r0 = r3.get(r0)
            java.lang.Integer r0 = (java.lang.Integer) r0
        L_0x0077:
            r18 = r0
            java.lang.String r0 = "key_chat_wallpaper_gradient_to3"
            java.lang.Object r0 = r3.get(r0)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x008a
            java.lang.Integer r0 = java.lang.Integer.valueOf(r12)
            r19 = r0
            goto L_0x008c
        L_0x008a:
            r19 = r0
        L_0x008c:
            java.lang.String r0 = "key_chat_wallpaper_gradient_to2"
            java.lang.Object r20 = r3.get(r0)
            java.lang.Integer r20 = (java.lang.Integer) r20
            java.lang.Object r0 = r3.get(r0)
            r20 = r0
            java.lang.Integer r20 = (java.lang.Integer) r20
            java.lang.String r0 = "chat_wallpaper_gradient_to"
            java.lang.Object r0 = r3.get(r0)
            r10 = r0
            java.lang.Integer r10 = (java.lang.Integer) r10
            if (r4 == 0) goto L_0x0108
            boolean r0 = r34.exists()
            if (r0 == 0) goto L_0x0108
            r11 = r18
            if (r11 == 0) goto L_0x00e3
            if (r10 == 0) goto L_0x00e3
            if (r20 == 0) goto L_0x00e3
            org.telegram.ui.Components.MotionBackgroundDrawable r0 = new org.telegram.ui.Components.MotionBackgroundDrawable     // Catch:{ all -> 0x00fc }
            int r22 = r11.intValue()     // Catch:{ all -> 0x00fc }
            int r23 = r10.intValue()     // Catch:{ all -> 0x00fc }
            int r24 = r20.intValue()     // Catch:{ all -> 0x00fc }
            int r25 = r19.intValue()     // Catch:{ all -> 0x00fc }
            r26 = 0
            r21 = r0
            r21.<init>(r22, r23, r24, r25, r26)     // Catch:{ all -> 0x00fc }
            java.lang.String r18 = r34.getAbsolutePath()     // Catch:{ all -> 0x00fc }
            android.graphics.Bitmap r15 = android.graphics.BitmapFactory.decodeFile(r18)     // Catch:{ all -> 0x00fc }
            r0.setPatternBitmap(r6, r15)     // Catch:{ all -> 0x00fc }
            int r15 = r0.getPatternColor()     // Catch:{ all -> 0x00fc }
            r0.setPatternColorFilter(r15)     // Catch:{ all -> 0x00fc }
            r9.wallpaper = r0     // Catch:{ all -> 0x00fc }
            goto L_0x00ed
        L_0x00e3:
            java.lang.String r0 = r34.getAbsolutePath()     // Catch:{ all -> 0x00fc }
            android.graphics.drawable.Drawable r0 = android.graphics.drawable.Drawable.createFromPath(r0)     // Catch:{ all -> 0x00fc }
            r9.wallpaper = r0     // Catch:{ all -> 0x00fc }
        L_0x00ed:
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r42)     // Catch:{ all -> 0x00fc }
            r9.isWallpaperMotion = r0     // Catch:{ all -> 0x00fc }
            r15 = r16
            r9.isPatternWallpaper = r15     // Catch:{ all -> 0x00fa }
            r9.isCustomTheme = r15     // Catch:{ all -> 0x00fa }
            goto L_0x0102
        L_0x00fa:
            r0 = move-exception
            goto L_0x00ff
        L_0x00fc:
            r0 = move-exception
            r15 = r16
        L_0x00ff:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0102:
            r18 = r13
            r24 = r14
            goto L_0x0277
        L_0x0108:
            r15 = r16
            r11 = r18
            if (r11 == 0) goto L_0x01f4
            java.lang.String r0 = "chat_wallpaper_gradient_rotation"
            java.lang.Object r0 = r3.get(r0)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x0121
            r16 = 45
            java.lang.Integer r0 = java.lang.Integer.valueOf(r16)
            r16 = r0
            goto L_0x0123
        L_0x0121:
            r16 = r0
        L_0x0123:
            if (r10 == 0) goto L_0x0198
            if (r20 == 0) goto L_0x0198
            org.telegram.ui.Components.MotionBackgroundDrawable r0 = new org.telegram.ui.Components.MotionBackgroundDrawable
            int r23 = r11.intValue()
            int r24 = r10.intValue()
            int r25 = r20.intValue()
            int r26 = r19.intValue()
            r27 = 0
            r22 = r0
            r22.<init>(r23, r24, r25, r26, r27)
            r18 = r0
            r0 = 0
            if (r4 == 0) goto L_0x0185
            if (r8 == 0) goto L_0x0185
            r12 = 1
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToAttach(r8, r12)
            r17 = 1135869952(0x43b40000, float:360.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r17 = 1142947840(0x44200000, float:640.0)
            r24 = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r8 = 0
            android.graphics.Bitmap r12 = org.telegram.messenger.SvgHelper.getBitmap((java.io.File) r3, (int) r12, (int) r0, (boolean) r8)
            if (r12 == 0) goto L_0x0181
            r8 = 0
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0177 }
            r0.<init>(r4)     // Catch:{ Exception -> 0x0177 }
            r8 = r0
            android.graphics.Bitmap$CompressFormat r0 = android.graphics.Bitmap.CompressFormat.PNG     // Catch:{ Exception -> 0x0177 }
            r17 = r3
            r3 = 87
            r12.compress(r0, r3, r8)     // Catch:{ Exception -> 0x0175 }
            r8.close()     // Catch:{ Exception -> 0x0175 }
            goto L_0x0183
        L_0x0175:
            r0 = move-exception
            goto L_0x017a
        L_0x0177:
            r0 = move-exception
            r17 = r3
        L_0x017a:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0.printStackTrace()
            goto L_0x0183
        L_0x0181:
            r17 = r3
        L_0x0183:
            r0 = r12
            goto L_0x0189
        L_0x0185:
            r24 = r0
            r0 = r24
        L_0x0189:
            r3 = r18
            r3.setPatternBitmap(r6, r0)
            r3.setPhase(r7)
            r9.wallpaper = r3
            r18 = r13
            r24 = r14
            goto L_0x01f0
        L_0x0198:
            if (r10 == 0) goto L_0x01e1
            boolean r0 = r10.equals(r11)
            if (r0 == 0) goto L_0x01a5
            r18 = r13
            r24 = r14
            goto L_0x01e5
        L_0x01a5:
            r3 = 2
            int[] r0 = new int[r3]
            int r3 = r11.intValue()
            r8 = 0
            r0[r8] = r3
            int r3 = r10.intValue()
            r8 = 1
            r0[r8] = r3
            int r8 = r16.intValue()
            android.graphics.drawable.GradientDrawable$Orientation r8 = org.telegram.ui.Components.BackgroundGradientDrawable.getGradientOrientation(r8)
            org.telegram.ui.Components.BackgroundGradientDrawable r12 = new org.telegram.ui.Components.BackgroundGradientDrawable
            r12.<init>(r8, r0)
            org.telegram.ui.ActionBar.Theme$13 r17 = new org.telegram.ui.ActionBar.Theme$13
            r17.<init>()
            r18 = r17
            org.telegram.ui.Components.BackgroundGradientDrawable$Sizes r3 = org.telegram.ui.Components.BackgroundGradientDrawable.Sizes.ofDeviceScreen()
            r23 = r0
            r24 = r14
            r0 = r18
            r18 = r13
            r13 = 100
            org.telegram.ui.Components.BackgroundGradientDrawable$Disposable r3 = r12.startDithering(r3, r0, r13)
            backgroundGradientDisposable = r3
            r9.wallpaper = r12
            goto L_0x01f0
        L_0x01e1:
            r18 = r13
            r24 = r14
        L_0x01e5:
            android.graphics.drawable.ColorDrawable r0 = new android.graphics.drawable.ColorDrawable
            int r3 = r11.intValue()
            r0.<init>(r3)
            r9.wallpaper = r0
        L_0x01f0:
            r9.isCustomTheme = r15
            goto L_0x0277
        L_0x01f4:
            r18 = r13
            r24 = r14
            if (r35 == 0) goto L_0x0237
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x0232 }
            java.io.File r3 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ Exception -> 0x0232 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0232 }
            r8.<init>()     // Catch:{ Exception -> 0x0232 }
            java.lang.String r12 = org.telegram.messenger.Utilities.MD5(r35)     // Catch:{ Exception -> 0x0232 }
            r8.append(r12)     // Catch:{ Exception -> 0x0232 }
            java.lang.String r12 = ".wp"
            r8.append(r12)     // Catch:{ Exception -> 0x0232 }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x0232 }
            r0.<init>(r3, r8)     // Catch:{ Exception -> 0x0232 }
            java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch:{ Exception -> 0x0232 }
            r3.<init>(r0)     // Catch:{ Exception -> 0x0232 }
            r8 = 0
            android.graphics.Bitmap r3 = loadScreenSizedBitmap(r3, r8)     // Catch:{ Exception -> 0x0232 }
            if (r3 == 0) goto L_0x0236
            android.graphics.drawable.BitmapDrawable r8 = new android.graphics.drawable.BitmapDrawable     // Catch:{ Exception -> 0x0232 }
            r8.<init>(r3)     // Catch:{ Exception -> 0x0232 }
            r9.wallpaper = r8     // Catch:{ Exception -> 0x0232 }
            android.graphics.drawable.Drawable r8 = r9.wallpaper     // Catch:{ Exception -> 0x0232 }
            r9.themedWallpaper = r8     // Catch:{ Exception -> 0x0232 }
            r9.isCustomTheme = r15     // Catch:{ Exception -> 0x0232 }
            goto L_0x0236
        L_0x0232:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0236:
            goto L_0x0277
        L_0x0237:
            if (r5 <= 0) goto L_0x0277
            java.lang.String r0 = r1.pathToFile
            if (r0 != 0) goto L_0x0241
            java.lang.String r0 = r1.assetName
            if (r0 == 0) goto L_0x0277
        L_0x0241:
            java.lang.String r0 = r1.assetName     // Catch:{ all -> 0x026c }
            if (r0 == 0) goto L_0x024c
            java.lang.String r0 = r1.assetName     // Catch:{ all -> 0x026c }
            java.io.File r0 = getAssetFile(r0)     // Catch:{ all -> 0x026c }
            goto L_0x0253
        L_0x024c:
            java.io.File r0 = new java.io.File     // Catch:{ all -> 0x026c }
            java.lang.String r3 = r1.pathToFile     // Catch:{ all -> 0x026c }
            r0.<init>(r3)     // Catch:{ all -> 0x026c }
        L_0x0253:
            java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch:{ all -> 0x026c }
            r3.<init>(r0)     // Catch:{ all -> 0x026c }
            android.graphics.Bitmap r3 = loadScreenSizedBitmap(r3, r5)     // Catch:{ all -> 0x026c }
            if (r3 == 0) goto L_0x026b
            android.graphics.drawable.BitmapDrawable r8 = new android.graphics.drawable.BitmapDrawable     // Catch:{ all -> 0x026c }
            r8.<init>(r3)     // Catch:{ all -> 0x026c }
            wallpaper = r8     // Catch:{ all -> 0x026c }
            r9.themedWallpaper = r8     // Catch:{ all -> 0x026c }
            r9.wallpaper = r8     // Catch:{ all -> 0x026c }
            r9.isCustomTheme = r15     // Catch:{ all -> 0x026c }
        L_0x026b:
            goto L_0x0277
        L_0x026c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0277
        L_0x0271:
            r18 = r13
            r24 = r14
            r15 = r16
        L_0x0277:
            android.graphics.drawable.Drawable r0 = r9.wallpaper
            if (r0 != 0) goto L_0x0380
            if (r2 == 0) goto L_0x0280
            int r0 = r2.color
            goto L_0x0281
        L_0x0280:
            r0 = 0
        L_0x0281:
            r3 = r0
            if (r2 == 0) goto L_0x0364
            boolean r0 = r32.isDefault()     // Catch:{ all -> 0x036f }
            if (r0 == 0) goto L_0x028e
            r8 = r18
            goto L_0x0366
        L_0x028e:
            boolean r0 = r32.isColor()     // Catch:{ all -> 0x036f }
            if (r0 == 0) goto L_0x0298
            int r0 = r2.gradientColor1     // Catch:{ all -> 0x036f }
            if (r0 == 0) goto L_0x036e
        L_0x0298:
            if (r3 == 0) goto L_0x032f
            boolean r0 = isPatternWallpaper     // Catch:{ all -> 0x036f }
            if (r0 == 0) goto L_0x02a2
            int r0 = r2.gradientColor2     // Catch:{ all -> 0x036f }
            if (r0 == 0) goto L_0x032f
        L_0x02a2:
            int r0 = r2.gradientColor1     // Catch:{ all -> 0x036f }
            if (r0 == 0) goto L_0x02f9
            int r0 = r2.gradientColor2     // Catch:{ all -> 0x036f }
            if (r0 == 0) goto L_0x02f9
            org.telegram.ui.Components.MotionBackgroundDrawable r0 = new org.telegram.ui.Components.MotionBackgroundDrawable     // Catch:{ all -> 0x036f }
            int r8 = r2.color     // Catch:{ all -> 0x036f }
            int r10 = r2.gradientColor1     // Catch:{ all -> 0x036f }
            int r11 = r2.gradientColor2     // Catch:{ all -> 0x036f }
            int r12 = r2.gradientColor3     // Catch:{ all -> 0x036f }
            r30 = 0
            r25 = r0
            r26 = r8
            r27 = r10
            r28 = r11
            r29 = r12
            r25.<init>(r26, r27, r28, r29, r30)     // Catch:{ all -> 0x036f }
            r0.setPhase(r7)     // Catch:{ all -> 0x036f }
            java.lang.Boolean r8 = r9.isPatternWallpaper     // Catch:{ all -> 0x036f }
            boolean r8 = r8.booleanValue()     // Catch:{ all -> 0x036f }
            if (r8 == 0) goto L_0x02f5
            java.io.File r8 = new java.io.File     // Catch:{ all -> 0x036f }
            java.io.File r10 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ all -> 0x036f }
            java.lang.String r11 = r2.fileName     // Catch:{ all -> 0x036f }
            r8.<init>(r10, r11)     // Catch:{ all -> 0x036f }
            boolean r10 = r8.exists()     // Catch:{ all -> 0x036f }
            if (r10 == 0) goto L_0x02f5
            float r10 = r2.intensity     // Catch:{ all -> 0x036f }
            r11 = 1120403456(0x42CLASSNAME, float:100.0)
            float r10 = r10 * r11
            int r10 = (int) r10     // Catch:{ all -> 0x036f }
            java.io.FileInputStream r11 = new java.io.FileInputStream     // Catch:{ all -> 0x036f }
            r11.<init>(r8)     // Catch:{ all -> 0x036f }
            r12 = 0
            android.graphics.Bitmap r11 = loadScreenSizedBitmap(r11, r12)     // Catch:{ all -> 0x036f }
            r0.setPatternBitmap(r10, r11)     // Catch:{ all -> 0x036f }
            r9.isCustomTheme = r15     // Catch:{ all -> 0x036f }
        L_0x02f5:
            r9.wallpaper = r0     // Catch:{ all -> 0x036f }
            goto L_0x036e
        L_0x02f9:
            int r0 = r2.gradientColor1     // Catch:{ all -> 0x036f }
            if (r0 == 0) goto L_0x0327
            r8 = 2
            int[] r0 = new int[r8]     // Catch:{ all -> 0x036f }
            r8 = 0
            r0[r8] = r3     // Catch:{ all -> 0x036f }
            int r8 = r2.gradientColor1     // Catch:{ all -> 0x036f }
            r10 = 1
            r0[r10] = r8     // Catch:{ all -> 0x036f }
            int r8 = r2.rotation     // Catch:{ all -> 0x036f }
            android.graphics.drawable.GradientDrawable$Orientation r8 = org.telegram.ui.Components.BackgroundGradientDrawable.getGradientOrientation(r8)     // Catch:{ all -> 0x036f }
            org.telegram.ui.Components.BackgroundGradientDrawable r10 = new org.telegram.ui.Components.BackgroundGradientDrawable     // Catch:{ all -> 0x036f }
            r10.<init>(r8, r0)     // Catch:{ all -> 0x036f }
            org.telegram.ui.ActionBar.Theme$14 r11 = new org.telegram.ui.ActionBar.Theme$14     // Catch:{ all -> 0x036f }
            r11.<init>()     // Catch:{ all -> 0x036f }
            org.telegram.ui.Components.BackgroundGradientDrawable$Sizes r12 = org.telegram.ui.Components.BackgroundGradientDrawable.Sizes.ofDeviceScreen()     // Catch:{ all -> 0x036f }
            r13 = 100
            org.telegram.ui.Components.BackgroundGradientDrawable$Disposable r12 = r10.startDithering(r12, r11, r13)     // Catch:{ all -> 0x036f }
            backgroundGradientDisposable = r12     // Catch:{ all -> 0x036f }
            r9.wallpaper = r10     // Catch:{ all -> 0x036f }
            goto L_0x036e
        L_0x0327:
            android.graphics.drawable.ColorDrawable r0 = new android.graphics.drawable.ColorDrawable     // Catch:{ all -> 0x036f }
            r0.<init>(r3)     // Catch:{ all -> 0x036f }
            r9.wallpaper = r0     // Catch:{ all -> 0x036f }
            goto L_0x036e
        L_0x032f:
            java.io.File r0 = new java.io.File     // Catch:{ all -> 0x036f }
            java.io.File r8 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ all -> 0x036f }
            java.lang.String r10 = r2.fileName     // Catch:{ all -> 0x036f }
            r0.<init>(r8, r10)     // Catch:{ all -> 0x036f }
            boolean r8 = r0.exists()     // Catch:{ all -> 0x036f }
            if (r8 == 0) goto L_0x0355
            java.io.FileInputStream r8 = new java.io.FileInputStream     // Catch:{ all -> 0x036f }
            r8.<init>(r0)     // Catch:{ all -> 0x036f }
            r10 = 0
            android.graphics.Bitmap r8 = loadScreenSizedBitmap(r8, r10)     // Catch:{ all -> 0x036f }
            if (r8 == 0) goto L_0x0355
            android.graphics.drawable.BitmapDrawable r10 = new android.graphics.drawable.BitmapDrawable     // Catch:{ all -> 0x036f }
            r10.<init>(r8)     // Catch:{ all -> 0x036f }
            r9.wallpaper = r10     // Catch:{ all -> 0x036f }
            r9.isCustomTheme = r15     // Catch:{ all -> 0x036f }
        L_0x0355:
            android.graphics.drawable.Drawable r8 = r9.wallpaper     // Catch:{ all -> 0x036f }
            if (r8 != 0) goto L_0x036e
            android.graphics.drawable.Drawable r8 = createDefaultWallpaper()     // Catch:{ all -> 0x036f }
            r9.wallpaper = r8     // Catch:{ all -> 0x036f }
            r8 = r18
            r9.isCustomTheme = r8     // Catch:{ all -> 0x036f }
            goto L_0x036e
        L_0x0364:
            r8 = r18
        L_0x0366:
            android.graphics.drawable.Drawable r0 = createDefaultWallpaper()     // Catch:{ all -> 0x036f }
            r9.wallpaper = r0     // Catch:{ all -> 0x036f }
            r9.isCustomTheme = r8     // Catch:{ all -> 0x036f }
        L_0x036e:
            goto L_0x0370
        L_0x036f:
            r0 = move-exception
        L_0x0370:
            android.graphics.drawable.Drawable r0 = r9.wallpaper
            if (r0 != 0) goto L_0x0380
            if (r3 != 0) goto L_0x0379
            r3 = -2693905(0xffffffffffd6e4ef, float:NaN)
        L_0x0379:
            android.graphics.drawable.ColorDrawable r0 = new android.graphics.drawable.ColorDrawable
            r0.<init>(r3)
            r9.wallpaper = r0
        L_0x0380:
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.createBackgroundDrawable(org.telegram.ui.ActionBar.Theme$ThemeInfo, org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo, java.util.HashMap, java.io.File, java.lang.String, int, int, int, boolean, boolean, boolean, boolean, org.telegram.tgnet.TLRPC$Document):org.telegram.ui.ActionBar.Theme$BackgroundDrawableSettings");
    }

    public static Drawable createDefaultWallpaper() {
        return createDefaultWallpaper(0, 0);
    }

    public static Drawable createDefaultWallpaper(int w, int h) {
        MotionBackgroundDrawable motionBackgroundDrawable = new MotionBackgroundDrawable(-2368069, -9722489, -2762611, -7817084, w != 0);
        if (w <= 0 || h <= 0) {
            w = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
            h = Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
        }
        motionBackgroundDrawable.setPatternBitmap(34, SvgHelper.getBitmap(NUM, w, h, -16777216));
        motionBackgroundDrawable.setPatternColorFilter(motionBackgroundDrawable.getPatternColor());
        return motionBackgroundDrawable;
    }

    /* access modifiers changed from: private */
    public static Bitmap loadScreenSizedBitmap(FileInputStream stream, int offset) {
        float scaleFactor;
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 1;
            opts.inJustDecodeBounds = true;
            stream.getChannel().position((long) offset);
            BitmapFactory.decodeStream(stream, (Rect) null, opts);
            float photoW = (float) opts.outWidth;
            float photoH = (float) opts.outHeight;
            int w_filter = AndroidUtilities.dp(360.0f);
            int h_filter = AndroidUtilities.dp(640.0f);
            if (w_filter < h_filter || photoW <= photoH) {
                scaleFactor = Math.min(photoW / ((float) w_filter), photoH / ((float) h_filter));
            } else {
                scaleFactor = Math.max(photoW / ((float) w_filter), photoH / ((float) h_filter));
            }
            if (scaleFactor < 1.2f) {
                scaleFactor = 1.0f;
            }
            opts.inJustDecodeBounds = false;
            if (scaleFactor <= 1.0f || (photoW <= ((float) w_filter) && photoH <= ((float) h_filter))) {
                opts.inSampleSize = (int) scaleFactor;
            } else {
                int sample = 1;
                do {
                    sample *= 2;
                } while (((float) (sample * 2)) < scaleFactor);
                opts.inSampleSize = sample;
            }
            stream.getChannel().position((long) offset);
            Bitmap decodeStream = BitmapFactory.decodeStream(stream, (Rect) null, opts);
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                }
            }
            return decodeStream;
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e3) {
                }
            }
            return null;
        } catch (Throwable th) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e4) {
                }
            }
            throw th;
        }
    }

    public static Drawable getThemedWallpaper(boolean thumb, View ownerView) {
        int offset;
        MotionBackgroundDrawable motionBackgroundDrawable;
        File file;
        int scaleFactor;
        int intensity;
        File file2;
        BackgroundGradientDrawable.Sizes sizes;
        BackgroundGradientDrawable.Listener listener;
        File wallpaperFile;
        final boolean z = thumb;
        final View view = ownerView;
        Integer backgroundColor = currentColors.get("chat_wallpaper");
        File file3 = null;
        MotionBackgroundDrawable motionBackgroundDrawable2 = null;
        if (backgroundColor != null) {
            Integer gradientToColor1 = currentColors.get("chat_wallpaper_gradient_to");
            Integer gradientToColor2 = currentColors.get("key_chat_wallpaper_gradient_to2");
            Integer gradientToColor3 = currentColors.get("key_chat_wallpaper_gradient_to3");
            Integer rotation = currentColors.get("chat_wallpaper_gradient_rotation");
            if (rotation == null) {
                rotation = 45;
            }
            if (gradientToColor1 == null) {
                return new ColorDrawable(backgroundColor.intValue());
            }
            ThemeAccent accent = currentTheme.getAccent(false);
            if (accent != null && !TextUtils.isEmpty(accent.patternSlug) && previousTheme == null && (wallpaperFile = accent.getPathToWallpaper()) != null && wallpaperFile.exists()) {
                file3 = wallpaperFile;
            }
            if (gradientToColor2 != null) {
                motionBackgroundDrawable2 = new MotionBackgroundDrawable(backgroundColor.intValue(), gradientToColor1.intValue(), gradientToColor2.intValue(), gradientToColor3 != null ? gradientToColor3.intValue() : 0, true);
                if (file3 == null) {
                    return motionBackgroundDrawable2;
                }
            } else if (file3 == null) {
                BackgroundGradientDrawable backgroundGradientDrawable = new BackgroundGradientDrawable(BackgroundGradientDrawable.getGradientOrientation(rotation.intValue()), new int[]{backgroundColor.intValue(), gradientToColor1.intValue()});
                if (!z) {
                    sizes = BackgroundGradientDrawable.Sizes.ofDeviceScreen();
                } else {
                    sizes = BackgroundGradientDrawable.Sizes.ofDeviceScreen(0.125f, BackgroundGradientDrawable.Sizes.Orientation.PORTRAIT);
                }
                if (view != null) {
                    listener = new BackgroundGradientDrawable.ListenerAdapter() {
                        public void onSizeReady(int width, int height) {
                            if (!z) {
                                boolean isGradientPortrait = true;
                                boolean isOrientationPortrait = AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y;
                                if (width > height) {
                                    isGradientPortrait = false;
                                }
                                if (isOrientationPortrait == isGradientPortrait) {
                                    view.invalidate();
                                    return;
                                }
                                return;
                            }
                            view.invalidate();
                        }
                    };
                } else {
                    listener = null;
                }
                backgroundGradientDrawable.startDithering(sizes, listener);
                return backgroundGradientDrawable;
            }
            offset = 0;
            motionBackgroundDrawable = motionBackgroundDrawable2;
            file = file3;
        } else if (themedWallpaperFileOffset <= 0 || (currentTheme.pathToFile == null && currentTheme.assetName == null)) {
            offset = 0;
            motionBackgroundDrawable = null;
            file = null;
        } else {
            if (currentTheme.assetName != null) {
                file2 = getAssetFile(currentTheme.assetName);
            } else {
                file2 = new File(currentTheme.pathToFile);
            }
            offset = themedWallpaperFileOffset;
            motionBackgroundDrawable = null;
            file = file2;
        }
        if (file != null) {
            FileInputStream stream = null;
            try {
                stream = new FileInputStream(file);
                stream.getChannel().position((long) offset);
                BitmapFactory.Options opts = new BitmapFactory.Options();
                int scaleFactor2 = 1;
                if (z) {
                    opts.inJustDecodeBounds = true;
                    float photoW = (float) opts.outWidth;
                    float photoH = (float) opts.outHeight;
                    int maxWidth = AndroidUtilities.dp(100.0f);
                    while (true) {
                        if (photoW <= ((float) maxWidth)) {
                            if (photoH <= ((float) maxWidth)) {
                                break;
                            }
                        }
                        scaleFactor2 *= 2;
                        photoW /= 2.0f;
                        photoH /= 2.0f;
                    }
                    scaleFactor = scaleFactor2;
                } else {
                    scaleFactor = 1;
                }
                opts.inJustDecodeBounds = false;
                opts.inSampleSize = scaleFactor;
                Bitmap bitmap = BitmapFactory.decodeStream(stream, (Rect) null, opts);
                if (motionBackgroundDrawable != null) {
                    ThemeAccent accent2 = currentTheme.getAccent(false);
                    if (accent2 != null) {
                        intensity = (int) (accent2.patternIntensity * 100.0f);
                    } else {
                        intensity = 100;
                    }
                    motionBackgroundDrawable.setPatternBitmap(intensity, bitmap);
                    motionBackgroundDrawable.setPatternColorFilter(motionBackgroundDrawable.getPatternColor());
                    try {
                        stream.close();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    return motionBackgroundDrawable;
                } else if (bitmap != null) {
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
                    try {
                        stream.close();
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                    return bitmapDrawable;
                } else {
                    try {
                        stream.close();
                    } catch (Exception e3) {
                        FileLog.e((Throwable) e3);
                    }
                }
            } catch (Throwable th) {
                Throwable th2 = th;
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception e4) {
                        FileLog.e((Throwable) e4);
                    }
                }
                throw th2;
            }
        }
        return null;
    }

    public static String getSelectedBackgroundSlug() {
        if (currentTheme.overrideWallpaper != null) {
            return currentTheme.overrideWallpaper.slug;
        }
        if (hasWallpaperFromTheme()) {
            return "t";
        }
        return "d";
    }

    public static Drawable getCachedWallpaper() {
        Drawable drawable;
        if (themedWallpaper != null) {
            drawable = themedWallpaper;
        } else {
            drawable = wallpaper;
        }
        if (drawable != null || wallpaperLoadTask == null) {
            return drawable;
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        DispatchQueue dispatchQueue = Utilities.themeQueue;
        countDownLatch.getClass();
        dispatchQueue.postRunnable(new Theme$$ExternalSyntheticLambda4(countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (themedWallpaper != null) {
            return themedWallpaper;
        }
        return wallpaper;
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
        String selectedBgSlug = getSelectedBackgroundSlug();
        return isPatternWallpaper || "CJz3BZ6YGEYBAAAABboWp6SAv04".equals(selectedBgSlug) || "qeZWES8rGVIEAAAARfWlK1lnfiI".equals(selectedBgSlug);
    }

    public static BackgroundGradientDrawable getCurrentGradientWallpaper() {
        if (currentTheme.overrideWallpaper == null || currentTheme.overrideWallpaper.color == 0 || currentTheme.overrideWallpaper.gradientColor1 == 0) {
            return null;
        }
        return new BackgroundGradientDrawable(BackgroundGradientDrawable.getGradientOrientation(currentTheme.overrideWallpaper.rotation), new int[]{currentTheme.overrideWallpaper.color, currentTheme.overrideWallpaper.gradientColor1});
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
            AndroidUtilities.runOnUIThread(new Theme$$ExternalSyntheticLambda5(messageObject), 200);
            chat_msgAudioVisualizeDrawable = null;
        }
    }

    static /* synthetic */ void lambda$unrefAudioVisualizeDrawable$9(MessageObject messageObject) {
        AudioVisualizerDrawable drawable = animatedOutVisualizerDrawables.remove(messageObject);
        if (drawable != null) {
            drawable.setParentView((View) null);
        }
    }

    public static AudioVisualizerDrawable getAnimatedOutAudioVisualizerDrawable(MessageObject messageObject) {
        HashMap<MessageObject, AudioVisualizerDrawable> hashMap = animatedOutVisualizerDrawables;
        if (hashMap == null || messageObject == null) {
            return null;
        }
        return hashMap.get(messageObject);
    }

    public static StatusDrawable getChatStatusDrawable(int type) {
        if (type < 0 || type > 5) {
            return null;
        }
        StatusDrawable[] statusDrawableArr = chat_status_drawables;
        StatusDrawable statusDrawable = statusDrawableArr[type];
        if (statusDrawable != null) {
            return statusDrawable;
        }
        switch (type) {
            case 0:
                statusDrawableArr[0] = new TypingDotsDrawable(true);
                break;
            case 1:
                statusDrawableArr[1] = new RecordStatusDrawable(true);
                break;
            case 2:
                statusDrawableArr[2] = new SendingFileDrawable(true);
                break;
            case 3:
                statusDrawableArr[3] = new PlayingGameDrawable(true, (ResourcesProvider) null);
                break;
            case 4:
                statusDrawableArr[4] = new RoundStatusDrawable(true);
                break;
            case 5:
                statusDrawableArr[5] = new ChoosingStickerStatusDrawable(true);
                break;
        }
        StatusDrawable statusDrawable2 = chat_status_drawables[type];
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

    public static String getFallbackKey(String key) {
        return fallbackKeys.get(key);
    }

    public static Map<String, Drawable> getThemeDrawablesMap() {
        return defaultChatDrawables;
    }

    public static Drawable getThemeDrawable(String drawableKey) {
        return defaultChatDrawables.get(drawableKey);
    }

    public static String getThemeDrawableColorKey(String drawableKey) {
        return defaultChatDrawableColorKeys.get(drawableKey);
    }

    public static Map<String, Paint> getThemePaintsMap() {
        return defaultChatPaints;
    }

    public static Paint getThemePaint(String paintKey) {
        return defaultChatPaints.get(paintKey);
    }

    public static String getThemePaintColorKey(String paintKey) {
        return defaultChatPaintColors.get(paintKey);
    }

    private static void addChatDrawable(String key, Drawable drawable, String colorKey) {
        defaultChatDrawables.put(key, drawable);
        if (colorKey != null) {
            defaultChatDrawableColorKeys.put(key, colorKey);
        }
    }

    private static void addChatPaint(String key, Paint paint, String colorKey) {
        defaultChatPaints.put(key, paint);
        if (colorKey != null) {
            defaultChatPaintColors.put(key, colorKey);
        }
    }

    public static boolean isCurrentThemeDay() {
        return !getActiveTheme().isDark();
    }

    public static boolean isHome(ThemeAccent accent) {
        if (accent.parentTheme == null) {
            return false;
        }
        if (accent.parentTheme.getKey().equals("Blue") && accent.id == 99) {
            return true;
        }
        if (accent.parentTheme.getKey().equals("Day") && accent.id == 9) {
            return true;
        }
        if ((accent.parentTheme.getKey().equals("Night") || accent.parentTheme.getKey().equals("Dark Blue")) && accent.id == 0) {
            return true;
        }
        return false;
    }
}
