package org.telegram.ui.ActionBar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
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
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Base64;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.StateSet;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map.Entry;
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
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.time.SunDate;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BaseTheme;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.TL_account_getMultiWallPapers;
import org.telegram.tgnet.TLRPC.TL_account_getTheme;
import org.telegram.tgnet.TLRPC.TL_account_getThemes;
import org.telegram.tgnet.TLRPC.TL_account_getWallPaper;
import org.telegram.tgnet.TLRPC.TL_baseThemeArctic;
import org.telegram.tgnet.TLRPC.TL_baseThemeClassic;
import org.telegram.tgnet.TLRPC.TL_baseThemeDay;
import org.telegram.tgnet.TLRPC.TL_baseThemeNight;
import org.telegram.tgnet.TLRPC.TL_baseThemeTinted;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputTheme;
import org.telegram.tgnet.TLRPC.TL_inputWallPaperSlug;
import org.telegram.tgnet.TLRPC.TL_theme;
import org.telegram.tgnet.TLRPC.TL_themeSettings;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.tgnet.TLRPC.TL_wallPaperNoFile;
import org.telegram.tgnet.TLRPC.Vector;
import org.telegram.tgnet.TLRPC.WallPaper;
import org.telegram.tgnet.TLRPC.WallPaperSettings;
import org.telegram.ui.Cells.ThemesHorizontalListCell;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.BackgroundGradientDrawable.Disposable;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.ScamDrawable;
import org.telegram.ui.Components.SvgHelper;

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
    private static Method StateListDrawable_getStateDrawableMethod = null;
    public static final String THEME_BACKGROUND_SLUG = "t";
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
                    Theme.lastBrightnessValue = 1.0f;
                } else {
                    Theme.lastBrightnessValue = ((float) Math.ceil((Math.log((double) f) * 9.932299613952637d) + 27.05900001525879d)) / 100.0f;
                }
                if (Theme.lastBrightnessValue > Theme.autoNightBrighnessThreshold) {
                    if (Theme.switchNightRunnableScheduled) {
                        Theme.switchNightRunnableScheduled = false;
                        AndroidUtilities.cancelRunOnUIThread(Theme.switchNightBrightnessRunnable);
                    }
                    if (!Theme.switchDayRunnableScheduled) {
                        Theme.switchDayRunnableScheduled = true;
                        AndroidUtilities.runOnUIThread(Theme.switchDayBrightnessRunnable, Theme.getAutoNightSwitchThemeDelay());
                    }
                } else if (!MediaController.getInstance().isRecordingOrListeningByProximity()) {
                    if (Theme.switchDayRunnableScheduled) {
                        Theme.switchDayRunnableScheduled = false;
                        AndroidUtilities.cancelRunOnUIThread(Theme.switchDayBrightnessRunnable);
                    }
                    if (!Theme.switchNightRunnableScheduled) {
                        Theme.switchNightRunnableScheduled = true;
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
    public static Paint avatar_backgroundPaint = null;
    public static Drawable avatar_ghostDrawable = null;
    public static Drawable avatar_savedDrawable = null;
    private static Disposable backgroundGradientDisposable = null;
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
    public static Drawable chat_msgInDrawable = null;
    public static Drawable chat_msgInInstantDrawable = null;
    public static Drawable chat_msgInMediaDrawable = null;
    public static Drawable chat_msgInMediaSelectedDrawable = null;
    public static Drawable chat_msgInMediaShadowDrawable = null;
    public static Drawable chat_msgInMenuDrawable = null;
    public static Drawable chat_msgInMenuSelectedDrawable = null;
    public static Drawable chat_msgInSelectedClockDrawable = null;
    public static Drawable chat_msgInSelectedDrawable = null;
    public static Drawable chat_msgInShadowDrawable = null;
    public static Drawable chat_msgInViewsDrawable = null;
    public static Drawable chat_msgInViewsSelectedDrawable = null;
    public static Drawable chat_msgMediaBroadcastDrawable = null;
    public static Drawable chat_msgMediaCheckDrawable = null;
    public static Drawable chat_msgMediaClockDrawable = null;
    public static Drawable chat_msgMediaHalfCheckDrawable = null;
    public static Drawable chat_msgMediaMenuDrawable = null;
    public static Drawable chat_msgMediaViewsDrawable = null;
    public static Drawable chat_msgNoSoundDrawable = null;
    public static Drawable chat_msgOutBroadcastDrawable = null;
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
    public static Drawable chat_msgOutLocationDrawable = null;
    public static MessageDrawable chat_msgOutMediaDrawable = null;
    public static MessageDrawable chat_msgOutMediaSelectedDrawable = null;
    public static Drawable chat_msgOutMediaShadowDrawable = null;
    public static Drawable chat_msgOutMenuDrawable = null;
    public static Drawable chat_msgOutMenuSelectedDrawable = null;
    public static Drawable chat_msgOutSelectedClockDrawable = null;
    public static MessageDrawable chat_msgOutSelectedDrawable = null;
    public static Drawable chat_msgOutShadowDrawable = null;
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
    private static int currentColor = 0;
    private static HashMap<String, Integer> currentColors = new HashMap();
    private static HashMap<String, Integer> currentColorsNoAccent = new HashMap();
    private static ThemeInfo currentDayTheme = null;
    private static ThemeInfo currentNightTheme = null;
    private static int currentSelectedColor = 0;
    private static ColorFilter currentShareColorFilter = null;
    private static int currentShareColorFilterColor = 0;
    private static ColorFilter currentShareSelectedColorFilter = null;
    private static int currentShareSelectedColorFilterColor = 0;
    private static ThemeInfo currentTheme = null;
    private static HashMap<String, Integer> defaultColors = new HashMap();
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
    private static HashMap<String, String> fallbackKeys = new HashMap();
    private static boolean hasPreviousTheme = false;
    private static ThreadLocal<float[]> hsvTemp1Local = new ThreadLocal();
    private static ThreadLocal<float[]> hsvTemp2Local = new ThreadLocal();
    private static ThreadLocal<float[]> hsvTemp3Local = new ThreadLocal();
    private static ThreadLocal<float[]> hsvTemp4Local = new ThreadLocal();
    private static ThreadLocal<float[]> hsvTemp5Local = new ThreadLocal();
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
    public static final String key_actionBarWhiteSelector = "actionBarWhiteSelector";
    public static final String key_avatar_actionBarIconBlue = "avatar_actionBarIconBlue";
    public static final String key_avatar_actionBarSelectorBlue = "avatar_actionBarSelectorBlue";
    public static final String key_avatar_backgroundActionBarBlue = "avatar_backgroundActionBarBlue";
    public static final String key_avatar_backgroundArchived = "avatar_backgroundArchived";
    public static final String key_avatar_backgroundArchivedHidden = "avatar_backgroundArchivedHidden";
    public static final String key_avatar_backgroundBlue = "avatar_backgroundBlue";
    public static final String key_avatar_backgroundCyan = "avatar_backgroundCyan";
    public static final String key_avatar_backgroundGreen = "avatar_backgroundGreen";
    public static final String key_avatar_backgroundGroupCreateSpanBlue = "avatar_backgroundGroupCreateSpanBlue";
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
    public static final String key_chat_adminSelectedText = "chat_adminSelectedText";
    public static final String key_chat_adminText = "chat_adminText";
    public static final String key_chat_attachActiveTab = "chat_attachActiveTab";
    public static final String key_chat_attachAudioBackground = "chat_attachAudioBackground";
    public static final String key_chat_attachAudioIcon = "chat_attachAudioIcon";
    public static final String key_chat_attachCheckBoxBackground = "chat_attachCheckBoxBackground";
    public static final String key_chat_attachCheckBoxCheck = "chat_attachCheckBoxCheck";
    public static final String key_chat_attachContactBackground = "chat_attachContactBackground";
    public static final String key_chat_attachContactIcon = "chat_attachContactIcon";
    public static final String key_chat_attachEmptyImage = "chat_attachEmptyImage";
    public static final String key_chat_attachFileBackground = "chat_attachFileBackground";
    public static final String key_chat_attachFileIcon = "chat_attachFileIcon";
    public static final String key_chat_attachGalleryBackground = "chat_attachGalleryBackground";
    public static final String key_chat_attachGalleryIcon = "chat_attachGalleryIcon";
    public static final String key_chat_attachLocationBackground = "chat_attachLocationBackground";
    public static final String key_chat_attachLocationIcon = "chat_attachLocationIcon";
    public static final String key_chat_attachMediaBanBackground = "chat_attachMediaBanBackground";
    public static final String key_chat_attachMediaBanText = "chat_attachMediaBanText";
    public static final String key_chat_attachPermissionImage = "chat_attachPermissionImage";
    public static final String key_chat_attachPermissionMark = "chat_attachPermissionMark";
    public static final String key_chat_attachPermissionText = "chat_attachPermissionText";
    public static final String key_chat_attachPhotoBackground = "chat_attachPhotoBackground";
    public static final String key_chat_attachPollBackground = "chat_attachPollBackground";
    public static final String key_chat_attachPollIcon = "chat_attachPollIcon";
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
    public static final String key_chat_inPreviewInstantSelectedText = "chat_inPreviewInstantSelectedText";
    public static final String key_chat_inPreviewInstantText = "chat_inPreviewInstantText";
    public static final String key_chat_inPreviewLine = "chat_inPreviewLine";
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
    public static final String key_chat_messagePanelVoiceShadow = "chat_messagePanelVoiceShadow";
    public static final String key_chat_messageTextIn = "chat_messageTextIn";
    public static final String key_chat_messageTextOut = "chat_messageTextOut";
    public static final String key_chat_muteIcon = "chat_muteIcon";
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
    public static final String key_chat_outBubbleGradient = "chat_outBubbleGradient";
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
    public static final String key_chat_outPreviewInstantSelectedText = "chat_outPreviewInstantSelectedText";
    public static final String key_chat_outPreviewInstantText = "chat_outPreviewInstantText";
    public static final String key_chat_outPreviewLine = "chat_outPreviewLine";
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
    public static final String key_chat_recordedVoicePlayPause = "chat_recordedVoicePlayPause";
    public static final String key_chat_recordedVoicePlayPausePressed = "chat_recordedVoicePlayPausePressed";
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
    public static final String key_chat_shareBackground = "chat_shareBackground";
    public static final String key_chat_shareBackgroundSelected = "chat_shareBackgroundSelected";
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
    public static final String key_chat_wallpaper_gradient_to = "chat_wallpaper_gradient_to";
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
    public static final String key_player_placeholder = "player_placeholder";
    public static final String key_player_placeholderBackground = "player_placeholderBackground";
    public static final String key_player_progress = "player_progress";
    public static final String key_player_progressBackground = "player_progressBackground";
    public static final String key_player_progressCachedBackground = "key_player_progressCachedBackground";
    public static final String key_player_time = "player_time";
    public static final String key_profile_actionBackground = "profile_actionBackground";
    public static final String key_profile_actionIcon = "profile_actionIcon";
    public static final String key_profile_actionPressedBackground = "profile_actionPressedBackground";
    public static final String key_profile_creatorIcon = "profile_creatorIcon";
    public static final String key_profile_status = "profile_status";
    public static final String key_profile_title = "profile_title";
    public static final String key_profile_verifiedBackground = "profile_verifiedBackground";
    public static final String key_profile_verifiedCheck = "profile_verifiedCheck";
    public static final String key_progressCircle = "progressCircle";
    public static final String key_radioBackground = "radioBackground";
    public static final String key_radioBackgroundChecked = "radioBackgroundChecked";
    public static final String key_returnToCallBackground = "returnToCallBackground";
    public static final String key_returnToCallText = "returnToCallText";
    public static final String key_sessions_devicesImage = "sessions_devicesImage";
    public static final String key_sharedMedia_actionMode = "sharedMedia_actionMode";
    public static final String key_sharedMedia_linkPlaceholder = "sharedMedia_linkPlaceholder";
    public static final String key_sharedMedia_linkPlaceholderText = "sharedMedia_linkPlaceholderText";
    public static final String key_sharedMedia_photoPlaceholder = "sharedMedia_photoPlaceholder";
    public static final String key_sharedMedia_startStopLoadIcon = "sharedMedia_startStopLoadIcon";
    public static final String key_sheet_other = "key_sheet_other";
    public static final String key_sheet_scrollUp = "key_sheet_scrollUp";
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
    public static String[] keys_avatar_background = new String[]{"avatar_backgroundRed", "avatar_backgroundOrange", "avatar_backgroundViolet", "avatar_backgroundGreen", "avatar_backgroundCyan", "avatar_backgroundBlue", "avatar_backgroundPink"};
    public static String[] keys_avatar_nameInMessage = new String[]{"avatar_nameInMessageRed", "avatar_nameInMessageOrange", "avatar_nameInMessageViolet", "avatar_nameInMessageGreen", "avatar_nameInMessageCyan", "avatar_nameInMessageBlue", "avatar_nameInMessagePink"};
    private static float lastBrightnessValue = 1.0f;
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
    private static Paint maskPaint = new Paint(1);
    public static Drawable moveUpDrawable;
    private static HashSet<String> myMessagesColorKeys = new HashSet();
    private static ArrayList<ThemeInfo> otherThemes = new ArrayList();
    private static ThemeInfo previousTheme;
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
    private static boolean shouldDrawGradientIcons;
    private static Runnable switchDayBrightnessRunnable = new Runnable() {
        public void run() {
            Theme.switchDayRunnableScheduled = false;
            Theme.applyDayNightThemeMaybe(false);
        }
    };
    private static boolean switchDayRunnableScheduled;
    private static Runnable switchNightBrightnessRunnable = new Runnable() {
        public void run() {
            Theme.switchNightRunnableScheduled = false;
            Theme.applyDayNightThemeMaybe(true);
        }
    };
    private static boolean switchNightRunnableScheduled;
    private static int switchNightThemeDelay;
    private static boolean switchingNightTheme;
    private static final Object sync = new Object();
    private static HashSet<String> themeAccentExclusionKeys = new HashSet();
    private static Drawable themedWallpaper;
    private static int themedWallpaperFileOffset;
    private static String themedWallpaperLink;
    public static ArrayList<ThemeInfo> themes = new ArrayList();
    private static HashMap<String, ThemeInfo> themesDict = new HashMap();
    private static Drawable wallpaper;
    private static final Object wallpaperSync = new Object();

    public static class MessageDrawable extends Drawable {
        public static final int TYPE_MEDIA = 1;
        public static final int TYPE_PREVIEW = 2;
        public static final int TYPE_TEXT = 0;
        private int currentBackgroundHeight;
        private int currentColor;
        private int currentGradientColor;
        private int currentType;
        private LinearGradient gradientShader;
        private boolean isSelected;
        private Matrix matrix = new Matrix();
        private Paint paint = new Paint(1);
        private Path path;
        private RectF rect = new RectF();
        private Paint selectedPaint;
        private int topY;

        public int getOpacity() {
            return -2;
        }

        public void setColorFilter(int i, Mode mode) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public MessageDrawable(int i, boolean z) {
            this.currentType = i;
            this.isSelected = z;
            if (this.currentType != 1) {
                this.path = new Path();
            }
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

        /* Access modifiers changed, original: protected */
        public int getColor(String str) {
            return Theme.getColor(str);
        }

        /* Access modifiers changed, original: protected */
        public Integer getCurrentColor(String str) {
            return (Integer) Theme.currentColors.get(str);
        }

        public void setTop(int i, int i2) {
            String str = "chat_outBubble";
            int color = getColor(this.isSelected ? "chat_outBubbleSelected" : str);
            Integer currentColor = getCurrentColor("chat_outBubbleGradient");
            if (currentColor != null) {
                color = getColor(str);
            }
            if (currentColor == null) {
                currentColor = Integer.valueOf(0);
            }
            if (currentColor.intValue() != 0 && (this.gradientShader == null || i2 != this.currentBackgroundHeight || this.currentColor != color || this.currentGradientColor != currentColor.intValue())) {
                this.gradientShader = new LinearGradient(0.0f, 0.0f, 0.0f, (float) i2, new int[]{currentColor.intValue(), color}, null, TileMode.CLAMP);
                this.paint.setShader(this.gradientShader);
                this.currentBackgroundHeight = i2;
                this.currentColor = color;
                this.currentGradientColor = currentColor.intValue();
                this.paint.setColor(-1);
            } else if (currentColor.intValue() == 0) {
                if (this.gradientShader != null) {
                    this.gradientShader = null;
                    this.paint.setShader(null);
                }
                this.paint.setColor(color);
            }
            this.topY = i;
        }

        private int dp(float f) {
            if (this.currentType == 2) {
                return (int) Math.ceil((double) (f * 3.0f));
            }
            return AndroidUtilities.dp(f);
        }

        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            int dp = dp(2.0f);
            int dp2 = dp(6.0f);
            if (this.gradientShader != null) {
                this.matrix.reset();
                this.matrix.postTranslate(0.0f, (float) (-this.topY));
                this.gradientShader.setLocalMatrix(this.matrix);
            }
            String str = "chat_outBubbleGradientSelectedOverlay";
            if (this.currentType == 1) {
                this.rect.set((float) (bounds.left + dp), (float) (bounds.top + dp), (float) (bounds.right - dp), (float) (bounds.bottom - dp));
                float f = (float) dp2;
                canvas.drawRoundRect(this.rect, f, f, this.paint);
                if (this.gradientShader != null && this.isSelected) {
                    this.selectedPaint.setColor(getColor(str));
                    canvas.drawRoundRect(this.rect, f, f, this.selectedPaint);
                    return;
                }
                return;
            }
            this.path.reset();
            this.path.moveTo((float) (bounds.right - dp(2.6f)), (float) (bounds.bottom - dp));
            this.path.lineTo((float) ((bounds.left + dp) + dp2), (float) (bounds.bottom - dp));
            RectF rectF = this.rect;
            int i = bounds.left;
            float f2 = (float) (i + dp);
            int i2 = bounds.bottom;
            int i3 = dp2 * 2;
            rectF.set(f2, (float) ((i2 - dp) - i3), (float) ((i + dp) + i3), (float) (i2 - dp));
            this.path.arcTo(this.rect, 90.0f, 90.0f, false);
            this.path.lineTo((float) (bounds.left + dp), (float) ((bounds.top + dp) + dp2));
            rectF = this.rect;
            i = bounds.left;
            float f3 = (float) (i + dp);
            int i4 = bounds.top;
            rectF.set(f3, (float) (i4 + dp), (float) ((i + dp) + i3), (float) ((i4 + dp) + i3));
            this.path.arcTo(this.rect, 180.0f, 90.0f, false);
            this.path.lineTo((float) ((bounds.right - dp(8.0f)) - dp2), (float) (bounds.top + dp));
            this.rect.set((float) ((bounds.right - dp(8.0f)) - i3), (float) (bounds.top + dp), (float) (bounds.right - dp(8.0f)), (float) ((bounds.top + dp) + i3));
            this.path.arcTo(this.rect, 270.0f, 90.0f, false);
            this.path.lineTo((float) (bounds.right - dp(8.0f)), (float) (((bounds.bottom - dp) - dp2) - dp(1.0f)));
            this.rect.set((float) (bounds.right - dp(8.0f)), (float) (((bounds.bottom - dp) - i3) - dp(9.0f)), (float) ((bounds.right - dp(7.0f)) + i3), (float) ((bounds.bottom - dp) - dp(1.0f)));
            this.path.arcTo(this.rect, 180.0f, -83.0f, false);
            this.path.close();
            canvas.drawPath(this.path, this.paint);
            if (this.gradientShader != null && this.isSelected) {
                this.selectedPaint.setColor(getColor(str));
                canvas.drawPath(this.path, this.selectedPaint);
            }
        }

        public void setAlpha(int i) {
            this.paint.setAlpha(i);
            this.selectedPaint.setAlpha((int) (((float) Color.alpha(getColor("chat_outBubbleGradientSelectedOverlay"))) * (((float) i) / 255.0f)));
        }
    }

    public static class OverrideWallpaperInfo {
        public int color;
        public String fileName;
        public int gradientColor;
        public float intensity;
        public boolean isBlurred;
        public boolean isMotion;
        public String originalFileName;
        public ThemeAccent parentAccent;
        public ThemeInfo parentTheme;
        public int rotation;
        public String slug;

        public OverrideWallpaperInfo() {
            String str = "";
            this.fileName = str;
            this.originalFileName = str;
            this.slug = str;
        }

        public OverrideWallpaperInfo(OverrideWallpaperInfo overrideWallpaperInfo, ThemeInfo themeInfo, ThemeAccent themeAccent) {
            File file;
            String str = "";
            this.fileName = str;
            this.originalFileName = str;
            this.slug = str;
            this.slug = overrideWallpaperInfo.slug;
            this.color = overrideWallpaperInfo.color;
            this.gradientColor = overrideWallpaperInfo.gradientColor;
            this.rotation = overrideWallpaperInfo.rotation;
            this.isBlurred = overrideWallpaperInfo.isBlurred;
            this.isMotion = overrideWallpaperInfo.isMotion;
            this.intensity = overrideWallpaperInfo.intensity;
            this.parentTheme = themeInfo;
            this.parentAccent = themeAccent;
            if (TextUtils.isEmpty(overrideWallpaperInfo.fileName)) {
                this.fileName = str;
            } else {
                try {
                    file = new File(ApplicationLoader.getFilesDirFixed(), overrideWallpaperInfo.fileName);
                    File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                    String generateWallpaperName = this.parentTheme.generateWallpaperName(this.parentAccent, false);
                    this.fileName = generateWallpaperName;
                    AndroidUtilities.copyFile(file, new File(filesDirFixed, generateWallpaperName));
                } catch (Exception e) {
                    this.fileName = str;
                    FileLog.e(e);
                }
            }
            if (TextUtils.isEmpty(overrideWallpaperInfo.originalFileName)) {
                this.originalFileName = str;
            } else if (overrideWallpaperInfo.originalFileName.equals(overrideWallpaperInfo.fileName)) {
                this.originalFileName = this.fileName;
            } else {
                try {
                    file = new File(ApplicationLoader.getFilesDirFixed(), overrideWallpaperInfo.originalFileName);
                    File filesDirFixed2 = ApplicationLoader.getFilesDirFixed();
                    String generateWallpaperName2 = this.parentTheme.generateWallpaperName(this.parentAccent, true);
                    this.originalFileName = generateWallpaperName2;
                    AndroidUtilities.copyFile(file, new File(filesDirFixed2, generateWallpaperName2));
                } catch (Exception e2) {
                    this.originalFileName = str;
                    FileLog.e(e2);
                }
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
            String str = "_owp";
            StringBuilder stringBuilder;
            if (this.parentAccent != null) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(this.parentTheme.name);
                stringBuilder.append("_");
                stringBuilder.append(this.parentAccent.id);
                stringBuilder.append(str);
                return stringBuilder.toString();
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append(this.parentTheme.name);
            stringBuilder.append(str);
            return stringBuilder.toString();
        }

        private void save() {
            try {
                String key = getKey();
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
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

        private void delete() {
            ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit().remove(getKey()).commit();
            new File(ApplicationLoader.getFilesDirFixed(), this.fileName).delete();
            new File(ApplicationLoader.getFilesDirFixed(), this.originalFileName).delete();
        }
    }

    public static class ThemeAccent {
        public int accentColor;
        public int account;
        public long backgroundGradientOverrideColor;
        public long backgroundOverrideColor;
        public int backgroundRotation = 45;
        public int id;
        public TL_theme info;
        public int myMessagesAccentColor;
        public int myMessagesGradientAccentColor;
        public OverrideWallpaperInfo overrideWallpaper;
        public ThemeInfo parentTheme;
        public String pathToFile;
        public TL_wallPaper pattern;
        public float patternIntensity;
        public boolean patternMotion;
        public String patternSlug = "";
        public InputFile uploadedFile;
        public InputFile uploadedThumb;
        public String uploadingFile;
        public String uploadingThumb;

        /* JADX WARNING: Missing block: B:42:0x00e8, code skipped:
            if (r7 != r4) goto L_0x00ea;
     */
        public boolean fillAccentColors(java.util.HashMap<java.lang.String, java.lang.Integer> r12, java.util.HashMap<java.lang.String, java.lang.Integer> r13) {
            /*
            r11 = this;
            r0 = 1;
            r1 = org.telegram.ui.ActionBar.Theme.getTempHsv(r0);
            r2 = 2;
            r2 = org.telegram.ui.ActionBar.Theme.getTempHsv(r2);
            r3 = r11.parentTheme;
            r3 = r3.accentBaseColor;
            android.graphics.Color.colorToHSV(r3, r1);
            r3 = r11.accentColor;
            android.graphics.Color.colorToHSV(r3, r2);
            r3 = r11.parentTheme;
            r3 = r3.isDark();
            r4 = r11.accentColor;
            r5 = r11.parentTheme;
            r5 = r5.accentBaseColor;
            if (r4 == r5) goto L_0x008c;
        L_0x0024:
            r4 = new java.util.HashSet;
            r5 = r12.keySet();
            r4.<init>(r5);
            r5 = org.telegram.ui.ActionBar.Theme.defaultColors;
            r5 = r5.keySet();
            r4.addAll(r5);
            r5 = org.telegram.ui.ActionBar.Theme.themeAccentExclusionKeys;
            r4.removeAll(r5);
            r4 = r4.iterator();
        L_0x0043:
            r5 = r4.hasNext();
            if (r5 == 0) goto L_0x008c;
        L_0x0049:
            r5 = r4.next();
            r5 = (java.lang.String) r5;
            r6 = r12.get(r5);
            r6 = (java.lang.Integer) r6;
            if (r6 != 0) goto L_0x006a;
        L_0x0057:
            r7 = org.telegram.ui.ActionBar.Theme.fallbackKeys;
            r7 = r7.get(r5);
            r7 = (java.lang.String) r7;
            if (r7 == 0) goto L_0x006a;
        L_0x0063:
            r7 = r12.get(r7);
            if (r7 == 0) goto L_0x006a;
        L_0x0069:
            goto L_0x0043;
        L_0x006a:
            if (r6 != 0) goto L_0x0076;
        L_0x006c:
            r6 = org.telegram.ui.ActionBar.Theme.defaultColors;
            r6 = r6.get(r5);
            r6 = (java.lang.Integer) r6;
        L_0x0076:
            r7 = r6.intValue();
            r7 = org.telegram.ui.ActionBar.Theme.changeColorAccent(r1, r2, r7, r3);
            r6 = r6.intValue();
            if (r7 == r6) goto L_0x0043;
        L_0x0084:
            r6 = java.lang.Integer.valueOf(r7);
            r13.put(r5, r6);
            goto L_0x0043;
        L_0x008c:
            r4 = r11.myMessagesAccentColor;
            r5 = "chat_outBubble";
            r6 = 0;
            if (r4 != 0) goto L_0x0097;
        L_0x0093:
            r7 = r11.accentColor;
            if (r7 == 0) goto L_0x00d9;
        L_0x0097:
            r7 = r11.myMessagesGradientAccentColor;
            if (r7 == 0) goto L_0x00d9;
        L_0x009b:
            r4 = r11.myMessagesAccentColor;
            if (r4 == 0) goto L_0x00a0;
        L_0x009f:
            goto L_0x00a2;
        L_0x00a0:
            r4 = r11.accentColor;
        L_0x00a2:
            r7 = r12.get(r5);
            r7 = (java.lang.Integer) r7;
            if (r7 != 0) goto L_0x00b4;
        L_0x00aa:
            r7 = org.telegram.ui.ActionBar.Theme.defaultColors;
            r7 = r7.get(r5);
            r7 = (java.lang.Integer) r7;
        L_0x00b4:
            r8 = r7.intValue();
            r8 = org.telegram.ui.ActionBar.Theme.changeColorAccent(r1, r2, r8, r3);
            r8 = org.telegram.messenger.AndroidUtilities.getColorDistance(r4, r8);
            r9 = r11.myMessagesGradientAccentColor;
            r9 = org.telegram.messenger.AndroidUtilities.getColorDistance(r4, r9);
            r10 = 35000; // 0x88b8 float:4.9045E-41 double:1.72923E-319;
            if (r8 > r10) goto L_0x00cf;
        L_0x00cb:
            if (r9 > r10) goto L_0x00cf;
        L_0x00cd:
            r8 = 1;
            goto L_0x00d0;
        L_0x00cf:
            r8 = 0;
        L_0x00d0:
            r7 = r7.intValue();
            r4 = org.telegram.ui.ActionBar.Theme.getAccentColor(r1, r7, r4);
            goto L_0x00da;
        L_0x00d9:
            r8 = 0;
        L_0x00da:
            if (r4 == 0) goto L_0x0141;
        L_0x00dc:
            r7 = r11.parentTheme;
            r7 = r7.accentBaseColor;
            if (r7 == 0) goto L_0x00e4;
        L_0x00e2:
            if (r4 != r7) goto L_0x00ea;
        L_0x00e4:
            r7 = r11.accentColor;
            if (r7 == 0) goto L_0x0141;
        L_0x00e8:
            if (r7 == r4) goto L_0x0141;
        L_0x00ea:
            android.graphics.Color.colorToHSV(r4, r2);
            r4 = org.telegram.ui.ActionBar.Theme.myMessagesColorKeys;
            r4 = r4.iterator();
        L_0x00f5:
            r7 = r4.hasNext();
            if (r7 == 0) goto L_0x0141;
        L_0x00fb:
            r7 = r4.next();
            r7 = (java.lang.String) r7;
            r9 = r12.get(r7);
            r9 = (java.lang.Integer) r9;
            if (r9 != 0) goto L_0x011c;
        L_0x0109:
            r10 = org.telegram.ui.ActionBar.Theme.fallbackKeys;
            r10 = r10.get(r7);
            r10 = (java.lang.String) r10;
            if (r10 == 0) goto L_0x011c;
        L_0x0115:
            r10 = r12.get(r10);
            if (r10 == 0) goto L_0x011c;
        L_0x011b:
            goto L_0x00f5;
        L_0x011c:
            if (r9 != 0) goto L_0x0128;
        L_0x011e:
            r9 = org.telegram.ui.ActionBar.Theme.defaultColors;
            r9 = r9.get(r7);
            r9 = (java.lang.Integer) r9;
        L_0x0128:
            if (r9 != 0) goto L_0x012b;
        L_0x012a:
            goto L_0x00f5;
        L_0x012b:
            r10 = r9.intValue();
            r10 = org.telegram.ui.ActionBar.Theme.changeColorAccent(r1, r2, r10, r3);
            r9 = r9.intValue();
            if (r10 == r9) goto L_0x00f5;
        L_0x0139:
            r9 = java.lang.Integer.valueOf(r10);
            r13.put(r7, r9);
            goto L_0x00f5;
        L_0x0141:
            r12 = -1;
            r1 = "chat_outLoader";
            if (r8 != 0) goto L_0x034d;
        L_0x0146:
            r2 = r11.myMessagesGradientAccentColor;
            if (r2 == 0) goto L_0x034d;
        L_0x014a:
            r3 = r11.myMessagesAccentColor;
            r2 = org.telegram.ui.ActionBar.Theme.useBlackText(r3, r2);
            if (r2 == 0) goto L_0x015a;
        L_0x0152:
            r2 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
            r3 = -11184811; // 0xfffffffffvar_ float:-2.8356863E38 double:NaN;
            r4 = NUM; // 0x4d000000 float:1.34217728E8 double:6.382565465E-315;
            goto L_0x0161;
        L_0x015a:
            r3 = -1118482; // 0xffffffffffeeeeee float:NaN double:NaN;
            r4 = NUM; // 0x4dffffff float:5.3687088E8 double:6.46545592E-315;
            r2 = -1;
        L_0x0161:
            r7 = java.lang.Integer.valueOf(r4);
            r9 = "chat_outAudioProgress";
            r13.put(r9, r7);
            r7 = java.lang.Integer.valueOf(r4);
            r9 = "chat_outAudioSelectedProgress";
            r13.put(r9, r7);
            r7 = java.lang.Integer.valueOf(r4);
            r9 = "chat_outAudioSeekbar";
            r13.put(r9, r7);
            r7 = java.lang.Integer.valueOf(r4);
            r9 = "chat_outAudioCacheSeekbar";
            r13.put(r9, r7);
            r7 = java.lang.Integer.valueOf(r4);
            r9 = "chat_outAudioSeekbarSelected";
            r13.put(r9, r7);
            r7 = java.lang.Integer.valueOf(r2);
            r9 = "chat_outAudioSeekbarFill";
            r13.put(r9, r7);
            r7 = java.lang.Integer.valueOf(r4);
            r9 = "chat_outVoiceSeekbar";
            r13.put(r9, r7);
            r4 = java.lang.Integer.valueOf(r4);
            r7 = "chat_outVoiceSeekbarSelected";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outVoiceSeekbarFill";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_messageTextOut";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_messageLinkOut";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outForwardedNameText";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outViaBotNameText";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outReplyLine";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outReplyNameText";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outPreviewLine";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outSiteNameText";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outInstant";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outInstantSelected";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outPreviewInstantText";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outPreviewInstantSelectedText";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outViews";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outAudioTitleText";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outFileNameText";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outContactNameText";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outAudioPerfomerText";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outAudioPerfomerSelectedText";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outSentCheck";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outSentCheckSelected";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outSentCheckRead";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outSentCheckReadSelected";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outSentClock";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outSentClockSelected";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outMenu";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outMenuSelected";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outTimeText";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r2);
            r7 = "chat_outTimeSelectedText";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r3);
            r7 = "chat_outAudioDurationText";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r3);
            r7 = "chat_outAudioDurationSelectedText";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r3);
            r7 = "chat_outContactPhoneText";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r3);
            r7 = "chat_outContactPhoneSelectedText";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r3);
            r7 = "chat_outFileInfoText";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r3);
            r7 = "chat_outFileInfoSelectedText";
            r13.put(r7, r4);
            r4 = java.lang.Integer.valueOf(r3);
            r7 = "chat_outVenueInfoText";
            r13.put(r7, r4);
            r3 = java.lang.Integer.valueOf(r3);
            r4 = "chat_outVenueInfoSelectedText";
            r13.put(r4, r3);
            r3 = java.lang.Integer.valueOf(r2);
            r4 = "chat_outReplyMessageText";
            r13.put(r4, r3);
            r3 = java.lang.Integer.valueOf(r2);
            r4 = "chat_outReplyMediaMessageText";
            r13.put(r4, r3);
            r3 = java.lang.Integer.valueOf(r2);
            r4 = "chat_outReplyMediaMessageSelectedText";
            r13.put(r4, r3);
            r3 = java.lang.Integer.valueOf(r2);
            r13.put(r1, r3);
            r2 = java.lang.Integer.valueOf(r2);
            r3 = "chat_outLoaderSelected";
            r13.put(r3, r2);
            r2 = r11.myMessagesAccentColor;
            r2 = java.lang.Integer.valueOf(r2);
            r3 = "chat_outFileProgress";
            r13.put(r3, r2);
            r2 = r11.myMessagesAccentColor;
            r2 = java.lang.Integer.valueOf(r2);
            r3 = "chat_outFileProgressSelected";
            r13.put(r3, r2);
            r2 = r11.myMessagesAccentColor;
            r2 = java.lang.Integer.valueOf(r2);
            r3 = "chat_outMediaIcon";
            r13.put(r3, r2);
            r2 = r11.myMessagesAccentColor;
            r2 = java.lang.Integer.valueOf(r2);
            r3 = "chat_outMediaIconSelected";
            r13.put(r3, r2);
        L_0x034d:
            if (r8 == 0) goto L_0x0362;
        L_0x034f:
            r1 = r13.get(r1);
            r1 = (java.lang.Integer) r1;
            r1 = r1.intValue();
            r12 = org.telegram.messenger.AndroidUtilities.getColorDistance(r12, r1);
            r1 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
            if (r12 >= r1) goto L_0x0362;
        L_0x0361:
            goto L_0x0363;
        L_0x0362:
            r6 = r8;
        L_0x0363:
            r12 = r11.myMessagesAccentColor;
            if (r12 == 0) goto L_0x037d;
        L_0x0367:
            r1 = r11.myMessagesGradientAccentColor;
            if (r1 == 0) goto L_0x037d;
        L_0x036b:
            r12 = java.lang.Integer.valueOf(r12);
            r13.put(r5, r12);
            r12 = r11.myMessagesGradientAccentColor;
            r12 = java.lang.Integer.valueOf(r12);
            r1 = "chat_outBubbleGradient";
            r13.put(r1, r12);
        L_0x037d:
            r1 = r11.backgroundOverrideColor;
            r12 = (int) r1;
            r3 = "chat_wallpaper";
            r4 = 0;
            if (r12 == 0) goto L_0x038e;
        L_0x0386:
            r12 = java.lang.Integer.valueOf(r12);
            r13.put(r3, r12);
            goto L_0x0395;
        L_0x038e:
            r12 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1));
            if (r12 == 0) goto L_0x0395;
        L_0x0392:
            r13.remove(r3);
        L_0x0395:
            r1 = r11.backgroundGradientOverrideColor;
            r12 = (int) r1;
            r3 = "chat_wallpaper_gradient_to";
            if (r12 == 0) goto L_0x03a4;
        L_0x039c:
            r12 = java.lang.Integer.valueOf(r12);
            r13.put(r3, r12);
            goto L_0x03ab;
        L_0x03a4:
            r12 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1));
            if (r12 == 0) goto L_0x03ab;
        L_0x03a8:
            r13.remove(r3);
        L_0x03ab:
            r12 = r11.backgroundRotation;
            r1 = 45;
            if (r12 == r1) goto L_0x03ba;
        L_0x03b1:
            r12 = java.lang.Integer.valueOf(r12);
            r1 = "chat_wallpaper_gradient_rotation";
            r13.put(r1, r12);
        L_0x03ba:
            r12 = r6 ^ 1;
            return r12;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme$ThemeAccent.fillAccentColors(java.util.HashMap, java.util.HashMap):boolean");
        }

        public File getPathToWallpaper() {
            if (TextUtils.isEmpty(this.patternSlug)) {
                return null;
            }
            return new File(ApplicationLoader.getFilesDirFixed(), String.format(Locale.US, "%s_%d_%s.jpg", new Object[]{this.parentTheme.getKey(), Integer.valueOf(this.id), this.patternSlug}));
        }

        /* JADX WARNING: Removed duplicated region for block: B:53:0x01ee A:{SYNTHETIC, Splitter:B:53:0x01ee} */
        /* JADX WARNING: Removed duplicated region for block: B:59:0x01f9 A:{SYNTHETIC, Splitter:B:59:0x01f9} */
        public java.io.File saveToFile() {
            /*
            r15 = this;
            r0 = org.telegram.messenger.AndroidUtilities.getSharingDirectory();
            r0.mkdirs();
            r1 = new java.io.File;
            r2 = java.util.Locale.US;
            r3 = 2;
            r4 = new java.lang.Object[r3];
            r5 = r15.parentTheme;
            r5 = r5.getKey();
            r6 = 0;
            r4[r6] = r5;
            r5 = r15.id;
            r5 = java.lang.Integer.valueOf(r5);
            r7 = 1;
            r4[r7] = r5;
            r5 = "%s_%d.attheme";
            r2 = java.lang.String.format(r2, r5, r4);
            r1.<init>(r0, r2);
            r0 = r15.parentTheme;
            r0 = r0.assetName;
            r2 = 0;
            r0 = org.telegram.ui.ActionBar.Theme.getThemeFileValues(r2, r0, r2);
            r4 = new java.util.HashMap;
            r4.<init>(r0);
            r15.fillAccentColors(r0, r4);
            r0 = r15.patternSlug;
            r0 = android.text.TextUtils.isEmpty(r0);
            r5 = "chat_wallpaper_gradient_to";
            r8 = "chat_wallpaper";
            if (r0 != 0) goto L_0x0167;
        L_0x0046:
            r0 = new java.lang.StringBuilder;
            r0.<init>();
            r9 = r15.patternMotion;
            if (r9 == 0) goto L_0x0054;
        L_0x004f:
            r9 = "motion";
            r0.append(r9);
        L_0x0054:
            r9 = r4.get(r8);
            r9 = (java.lang.Integer) r9;
            if (r9 != 0) goto L_0x0061;
        L_0x005c:
            r9 = -1;
            r9 = java.lang.Integer.valueOf(r9);
        L_0x0061:
            r10 = r4.get(r5);
            r10 = (java.lang.Integer) r10;
            if (r10 != 0) goto L_0x006d;
        L_0x0069:
            r10 = java.lang.Integer.valueOf(r6);
        L_0x006d:
            r11 = "chat_wallpaper_gradient_rotation";
            r11 = r4.get(r11);
            r11 = (java.lang.Integer) r11;
            if (r11 != 0) goto L_0x007d;
        L_0x0077:
            r11 = 45;
            r11 = java.lang.Integer.valueOf(r11);
        L_0x007d:
            r12 = 3;
            r13 = new java.lang.Object[r12];
            r14 = r9.intValue();
            r14 = r14 >> 16;
            r14 = (byte) r14;
            r14 = r14 & 255;
            r14 = java.lang.Integer.valueOf(r14);
            r13[r6] = r14;
            r14 = r9.intValue();
            r14 = r14 >> 8;
            r14 = (byte) r14;
            r14 = r14 & 255;
            r14 = java.lang.Integer.valueOf(r14);
            r13[r7] = r14;
            r9 = r9.intValue();
            r9 = r9 & 255;
            r9 = (byte) r9;
            r9 = java.lang.Byte.valueOf(r9);
            r13[r3] = r9;
            r9 = "%02x%02x%02x";
            r13 = java.lang.String.format(r9, r13);
            r13 = r13.toLowerCase();
            r14 = r10.intValue();
            if (r14 == 0) goto L_0x00f1;
        L_0x00bb:
            r12 = new java.lang.Object[r12];
            r14 = r10.intValue();
            r14 = r14 >> 16;
            r14 = (byte) r14;
            r14 = r14 & 255;
            r14 = java.lang.Integer.valueOf(r14);
            r12[r6] = r14;
            r6 = r10.intValue();
            r6 = r6 >> 8;
            r6 = (byte) r6;
            r6 = r6 & 255;
            r6 = java.lang.Integer.valueOf(r6);
            r12[r7] = r6;
            r6 = r10.intValue();
            r6 = r6 & 255;
            r6 = (byte) r6;
            r6 = java.lang.Byte.valueOf(r6);
            r12[r3] = r6;
            r3 = java.lang.String.format(r9, r12);
            r3 = r3.toLowerCase();
            goto L_0x00f2;
        L_0x00f1:
            r3 = r2;
        L_0x00f2:
            if (r3 == 0) goto L_0x011c;
        L_0x00f4:
            r6 = new java.lang.StringBuilder;
            r6.<init>();
            r6.append(r13);
            r7 = "-";
            r6.append(r7);
            r6.append(r3);
            r3 = r6.toString();
            r6 = new java.lang.StringBuilder;
            r6.<init>();
            r6.append(r3);
            r3 = "&rotation=";
            r6.append(r3);
            r6.append(r11);
            r13 = r6.toString();
        L_0x011c:
            r3 = new java.lang.StringBuilder;
            r3.<init>();
            r6 = "https://attheme.org?slug=";
            r3.append(r6);
            r6 = r15.patternSlug;
            r3.append(r6);
            r6 = "&intensity=";
            r3.append(r6);
            r6 = r15.patternIntensity;
            r7 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
            r6 = r6 * r7;
            r6 = (int) r6;
            r3.append(r6);
            r6 = "&bg_color=";
            r3.append(r6);
            r3.append(r13);
            r3 = r3.toString();
            r6 = r0.length();
            if (r6 <= 0) goto L_0x0165;
        L_0x014c:
            r6 = new java.lang.StringBuilder;
            r6.<init>();
            r6.append(r3);
            r3 = "&mode=";
            r6.append(r3);
            r0 = r0.toString();
            r6.append(r0);
            r0 = r6.toString();
            goto L_0x0168;
        L_0x0165:
            r0 = r3;
            goto L_0x0168;
        L_0x0167:
            r0 = r2;
        L_0x0168:
            r3 = new java.lang.StringBuilder;
            r3.<init>();
            r4 = r4.entrySet();
            r4 = r4.iterator();
        L_0x0175:
            r6 = r4.hasNext();
            r7 = "\n";
            if (r6 == 0) goto L_0x01ab;
        L_0x017d:
            r6 = r4.next();
            r6 = (java.util.Map.Entry) r6;
            r9 = r6.getKey();
            r9 = (java.lang.String) r9;
            if (r0 == 0) goto L_0x0198;
        L_0x018b:
            r10 = r8.equals(r9);
            if (r10 != 0) goto L_0x0175;
        L_0x0191:
            r10 = r5.equals(r9);
            if (r10 == 0) goto L_0x0198;
        L_0x0197:
            goto L_0x0175;
        L_0x0198:
            r3.append(r9);
            r9 = "=";
            r3.append(r9);
            r6 = r6.getValue();
            r3.append(r6);
            r3.append(r7);
            goto L_0x0175;
        L_0x01ab:
            r4 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x01e8 }
            r4.<init>(r1);	 Catch:{ Exception -> 0x01e8 }
            r2 = r3.toString();	 Catch:{ Exception -> 0x01e2, all -> 0x01e0 }
            r2 = org.telegram.messenger.AndroidUtilities.getStringBytes(r2);	 Catch:{ Exception -> 0x01e2, all -> 0x01e0 }
            r4.write(r2);	 Catch:{ Exception -> 0x01e2, all -> 0x01e0 }
            r2 = android.text.TextUtils.isEmpty(r0);	 Catch:{ Exception -> 0x01e2, all -> 0x01e0 }
            if (r2 != 0) goto L_0x01dc;
        L_0x01c1:
            r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01e2, all -> 0x01e0 }
            r2.<init>();	 Catch:{ Exception -> 0x01e2, all -> 0x01e0 }
            r3 = "WLS=";
            r2.append(r3);	 Catch:{ Exception -> 0x01e2, all -> 0x01e0 }
            r2.append(r0);	 Catch:{ Exception -> 0x01e2, all -> 0x01e0 }
            r2.append(r7);	 Catch:{ Exception -> 0x01e2, all -> 0x01e0 }
            r0 = r2.toString();	 Catch:{ Exception -> 0x01e2, all -> 0x01e0 }
            r0 = org.telegram.messenger.AndroidUtilities.getStringBytes(r0);	 Catch:{ Exception -> 0x01e2, all -> 0x01e0 }
            r4.write(r0);	 Catch:{ Exception -> 0x01e2, all -> 0x01e0 }
        L_0x01dc:
            r4.close();	 Catch:{ Exception -> 0x01f2 }
            goto L_0x01f6;
        L_0x01e0:
            r0 = move-exception;
            goto L_0x01f7;
        L_0x01e2:
            r0 = move-exception;
            r2 = r4;
            goto L_0x01e9;
        L_0x01e5:
            r0 = move-exception;
            r4 = r2;
            goto L_0x01f7;
        L_0x01e8:
            r0 = move-exception;
        L_0x01e9:
            org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x01e5 }
            if (r2 == 0) goto L_0x01f6;
        L_0x01ee:
            r2.close();	 Catch:{ Exception -> 0x01f2 }
            goto L_0x01f6;
        L_0x01f2:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x01f6:
            return r1;
        L_0x01f7:
            if (r4 == 0) goto L_0x0201;
        L_0x01f9:
            r4.close();	 Catch:{ Exception -> 0x01fd }
            goto L_0x0201;
        L_0x01fd:
            r1 = move-exception;
            org.telegram.messenger.FileLog.e(r1);
        L_0x0201:
            goto L_0x0203;
        L_0x0202:
            throw r0;
        L_0x0203:
            goto L_0x0202;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme$ThemeAccent.saveToFile():java.io.File");
        }
    }

    public static class PatternsLoader implements NotificationCenterDelegate {
        private static PatternsLoader loader;
        private int account = UserConfig.selectedAccount;
        private HashMap<String, LoadingPattern> watingForLoad;

        private class LoadingPattern {
            public ArrayList<ThemeAccent> accents;
            public TL_wallPaper pattern;

            private LoadingPattern() {
                this.accents = new ArrayList();
            }

            /* synthetic */ LoadingPattern(PatternsLoader patternsLoader, AnonymousClass1 anonymousClass1) {
                this();
            }
        }

        public static void createLoader(boolean z) {
            if (loader == null || z) {
                ArrayList arrayList = null;
                int i = 0;
                while (i < 5) {
                    Object obj = i != 0 ? i != 1 ? i != 2 ? i != 3 ? "Night" : "Day" : "Arctic Blue" : "Dark Blue" : "Blue";
                    ThemeInfo themeInfo = (ThemeInfo) Theme.themesDict.get(obj);
                    if (themeInfo != null) {
                        ArrayList arrayList2 = themeInfo.themeAccents;
                        if (!(arrayList2 == null || arrayList2.isEmpty())) {
                            int size = themeInfo.themeAccents.size();
                            ArrayList arrayList3 = arrayList;
                            for (int i2 = 0; i2 < size; i2++) {
                                ThemeAccent themeAccent = (ThemeAccent) themeInfo.themeAccents.get(i2);
                                if (!(themeAccent.id == Theme.DEFALT_THEME_ACCENT_ID || TextUtils.isEmpty(themeAccent.patternSlug))) {
                                    if (arrayList3 == null) {
                                        arrayList3 = new ArrayList();
                                    }
                                    arrayList3.add(themeAccent);
                                }
                            }
                            arrayList = arrayList3;
                        }
                    }
                    i++;
                }
                loader = new PatternsLoader(arrayList);
            }
        }

        private PatternsLoader(ArrayList<ThemeAccent> arrayList) {
            if (arrayList != null) {
                Utilities.globalQueue.postRunnable(new -$$Lambda$Theme$PatternsLoader$HCCEdWFXtTST0bTku-tleeEi2Js(this, arrayList));
            }
        }

        public /* synthetic */ void lambda$new$1$Theme$PatternsLoader(ArrayList arrayList) {
            ArrayList arrayList2 = null;
            int size = arrayList.size();
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
                TL_account_getMultiWallPapers tL_account_getMultiWallPapers = new TL_account_getMultiWallPapers();
                size = arrayList2.size();
                for (int i2 = 0; i2 < size; i2++) {
                    TL_inputWallPaperSlug tL_inputWallPaperSlug = new TL_inputWallPaperSlug();
                    tL_inputWallPaperSlug.slug = (String) arrayList2.get(i2);
                    tL_account_getMultiWallPapers.wallpapers.add(tL_inputWallPaperSlug);
                }
                ConnectionsManager.getInstance(this.account).sendRequest(tL_account_getMultiWallPapers, new -$$Lambda$Theme$PatternsLoader$N4of1nB2IYmfL6rQuv9aChkyycI(this, arrayList));
            }
        }

        public /* synthetic */ void lambda$null$0$Theme$PatternsLoader(ArrayList arrayList, TLObject tLObject, TL_error tL_error) {
            TLObject tLObject2 = tLObject;
            if (tLObject2 instanceof Vector) {
                Vector vector = (Vector) tLObject2;
                int size = vector.objects.size();
                AnonymousClass1 anonymousClass1 = null;
                ArrayList arrayList2 = null;
                int i = 0;
                while (i < size) {
                    ArrayList arrayList3;
                    WallPaper wallPaper = (WallPaper) vector.objects.get(i);
                    if (wallPaper instanceof TL_wallPaper) {
                        TL_wallPaper tL_wallPaper = (TL_wallPaper) wallPaper;
                        if (tL_wallPaper.pattern) {
                            File pathToAttach = FileLoader.getPathToAttach(tL_wallPaper.document, true);
                            int size2 = arrayList.size();
                            Bitmap bitmap = anonymousClass1;
                            Boolean bool = bitmap;
                            ArrayList arrayList4 = arrayList2;
                            int i2 = 0;
                            while (i2 < size2) {
                                ThemeAccent themeAccent = (ThemeAccent) arrayList.get(i2);
                                if (themeAccent.patternSlug.equals(tL_wallPaper.slug)) {
                                    if (bool == null) {
                                        bool = Boolean.valueOf(pathToAttach.exists());
                                    }
                                    if (bitmap != null || bool.booleanValue()) {
                                        Bitmap createWallpaperForAccent = createWallpaperForAccent(bitmap, "application/x-tgwallpattern".equals(tL_wallPaper.document.mime_type), pathToAttach, themeAccent);
                                        if (arrayList4 == null) {
                                            arrayList4 = new ArrayList();
                                        }
                                        arrayList4.add(themeAccent);
                                        bitmap = createWallpaperForAccent;
                                    } else {
                                        String attachFileName = FileLoader.getAttachFileName(tL_wallPaper.document);
                                        if (this.watingForLoad == null) {
                                            this.watingForLoad = new HashMap();
                                        }
                                        LoadingPattern loadingPattern = (LoadingPattern) this.watingForLoad.get(attachFileName);
                                        if (loadingPattern == null) {
                                            loadingPattern = new LoadingPattern(this, anonymousClass1);
                                            loadingPattern.pattern = tL_wallPaper;
                                            this.watingForLoad.put(attachFileName, loadingPattern);
                                        }
                                        loadingPattern.accents.add(themeAccent);
                                    }
                                }
                                i2++;
                                anonymousClass1 = null;
                            }
                            arrayList3 = arrayList;
                            if (bitmap != null) {
                                bitmap.recycle();
                            }
                            arrayList2 = arrayList4;
                            i++;
                            anonymousClass1 = null;
                        }
                    }
                    arrayList3 = arrayList;
                    i++;
                    anonymousClass1 = null;
                }
                checkCurrentWallpaper(arrayList2, true);
            }
        }

        private void checkCurrentWallpaper(ArrayList<ThemeAccent> arrayList, boolean z) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$Theme$PatternsLoader$JFa1Tj4tjELn2_WOuHD3CT_fNLs(this, arrayList, z));
        }

        public /* synthetic */ void lambda$checkCurrentWallpaper$2$Theme$PatternsLoader(ArrayList arrayList, boolean z) {
            checkCurrentWallpaperInternal(arrayList, z);
        }

        private void checkCurrentWallpaperInternal(ArrayList<ThemeAccent> arrayList, boolean z) {
            if (!(arrayList == null || Theme.currentTheme.themeAccents == null || Theme.currentTheme.themeAccents.isEmpty() || !arrayList.contains(Theme.currentTheme.getAccent(false)))) {
                Theme.reloadWallpaper();
            }
            if (!z) {
                HashMap hashMap = this.watingForLoad;
                if (hashMap == null || hashMap.isEmpty()) {
                    NotificationCenter.getInstance(this.account).removeObserver(this, NotificationCenter.fileDidLoad);
                    NotificationCenter.getInstance(this.account).removeObserver(this, NotificationCenter.fileDidFailToLoad);
                }
            } else if (this.watingForLoad != null) {
                NotificationCenter.getInstance(this.account).addObserver(this, NotificationCenter.fileDidLoad);
                NotificationCenter.getInstance(this.account).addObserver(this, NotificationCenter.fileDidFailToLoad);
                for (Entry value : this.watingForLoad.entrySet()) {
                    FileLoader.getInstance(this.account).loadFile(ImageLocation.getForDocument(((LoadingPattern) value.getValue()).pattern.document), "wallpaper", null, 0, 1);
                }
            }
        }

        private Bitmap createWallpaperForAccent(Bitmap bitmap, boolean z, File file, ThemeAccent themeAccent) {
            Throwable th;
            ThemeAccent themeAccent2 = themeAccent;
            Bitmap bitmap2;
            try {
                File pathToWallpaper = themeAccent.getPathToWallpaper();
                if (pathToWallpaper == null) {
                    return null;
                }
                Drawable backgroundGradientDrawable;
                ThemeInfo themeInfo = themeAccent2.parentTheme;
                HashMap themeFileValues = Theme.getThemeFileValues(null, themeInfo.assetName, null);
                int i = themeAccent2.accentColor;
                int i2 = (int) themeAccent2.backgroundOverrideColor;
                int i3 = (int) themeAccent2.backgroundGradientOverrideColor;
                if (i3 == 0 && themeAccent2.backgroundGradientOverrideColor == 0) {
                    if (i2 != 0) {
                        i = i2;
                    }
                    Integer num = (Integer) themeFileValues.get("chat_wallpaper_gradient_to");
                    if (num != null) {
                        i3 = Theme.changeColorAccent(themeInfo, i, num.intValue());
                    }
                } else {
                    i = 0;
                }
                if (i2 == 0) {
                    Integer num2 = (Integer) themeFileValues.get("chat_wallpaper");
                    if (num2 != null) {
                        i2 = Theme.changeColorAccent(themeInfo, i, num2.intValue());
                    }
                }
                int i4 = 1;
                if (i3 != 0) {
                    backgroundGradientDrawable = new BackgroundGradientDrawable(BackgroundGradientDrawable.getGradientOrientation(themeAccent2.backgroundRotation), new int[]{i2, i3});
                    i = AndroidUtilities.getPatternColor(AndroidUtilities.getAverageColor(i2, i3));
                } else {
                    backgroundGradientDrawable = new ColorDrawable(i2);
                    i = AndroidUtilities.getPatternColor(i2);
                }
                if (bitmap != null) {
                    bitmap2 = bitmap;
                } else if (z) {
                    bitmap2 = SvgHelper.getBitmap(file, AndroidUtilities.dp(360.0f), AndroidUtilities.dp(640.0f), false);
                } else {
                    float min;
                    File file2 = file;
                    Options options = new Options();
                    options.inSampleSize = 1;
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                    float f = (float) options.outWidth;
                    float f2 = (float) options.outHeight;
                    i2 = AndroidUtilities.dp(360.0f);
                    i3 = AndroidUtilities.dp(640.0f);
                    if (i2 < i3 || f <= f2) {
                        min = Math.min(f / ((float) i2), f2 / ((float) i3));
                    } else {
                        min = Math.max(f / ((float) i2), f2 / ((float) i3));
                    }
                    if (min < 1.2f) {
                        min = 1.0f;
                    }
                    options.inJustDecodeBounds = false;
                    if (min <= 1.0f || (f <= ((float) i2) && f2 <= ((float) i3))) {
                        options.inSampleSize = (int) min;
                    } else {
                        do {
                            i4 *= 2;
                        } while (((float) (i4 * 2)) < min);
                        options.inSampleSize = i4;
                    }
                    bitmap2 = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                }
                try {
                    Bitmap createBitmap = Bitmap.createBitmap(bitmap2.getWidth(), bitmap2.getHeight(), Config.ARGB_8888);
                    Canvas canvas = new Canvas(createBitmap);
                    backgroundGradientDrawable.setBounds(0, 0, bitmap2.getWidth(), bitmap2.getHeight());
                    backgroundGradientDrawable.draw(canvas);
                    Paint paint = new Paint(2);
                    paint.setColorFilter(new PorterDuffColorFilter(i, Mode.SRC_IN));
                    paint.setAlpha((int) (themeAccent2.patternIntensity * 255.0f));
                    canvas.drawBitmap(bitmap2, 0.0f, 0.0f, paint);
                    FileOutputStream fileOutputStream = new FileOutputStream(pathToWallpaper);
                    createBitmap.compress(CompressFormat.JPEG, 87, fileOutputStream);
                    fileOutputStream.close();
                } catch (Throwable th2) {
                    th = th2;
                }
                return bitmap2;
            } catch (Throwable th3) {
                th = th3;
                bitmap2 = bitmap;
                FileLog.e(th);
                return bitmap2;
            }
        }

        public void didReceivedNotification(int i, int i2, Object... objArr) {
            HashMap hashMap = this.watingForLoad;
            if (hashMap != null) {
                if (i == NotificationCenter.fileDidLoad) {
                    LoadingPattern loadingPattern = (LoadingPattern) hashMap.remove((String) objArr[0]);
                    if (loadingPattern != null) {
                        Utilities.globalQueue.postRunnable(new -$$Lambda$Theme$PatternsLoader$tV_RrPhS-fCdYNPUU4q_kKMxnDg(this, loadingPattern));
                    }
                } else if (i == NotificationCenter.fileDidFailToLoad && hashMap.remove((String) objArr[0]) != null) {
                    checkCurrentWallpaper(null, false);
                }
            }
        }

        public /* synthetic */ void lambda$didReceivedNotification$3$Theme$PatternsLoader(LoadingPattern loadingPattern) {
            TL_wallPaper tL_wallPaper = loadingPattern.pattern;
            File pathToAttach = FileLoader.getPathToAttach(tL_wallPaper.document, true);
            int size = loadingPattern.accents.size();
            Bitmap bitmap = null;
            ArrayList arrayList = bitmap;
            for (int i = 0; i < size; i++) {
                ThemeAccent themeAccent = (ThemeAccent) loadingPattern.accents.get(i);
                if (themeAccent.patternSlug.equals(tL_wallPaper.slug)) {
                    bitmap = createWallpaperForAccent(bitmap, "application/x-tgwallpattern".equals(tL_wallPaper.document.mime_type), pathToAttach, themeAccent);
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

    public static class ThemeInfo implements NotificationCenterDelegate {
        public int accentBaseColor;
        public LongSparseArray<ThemeAccent> accentsByThemeId;
        public int account;
        public String assetName;
        public boolean badWallpaper;
        public int currentAccentId;
        public int defaultAccentCount;
        public boolean firstAccentIsDefault;
        public TL_theme info;
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
        private int previewBackgroundColor;
        public int previewBackgroundGradientColor;
        private int previewInColor;
        private int previewOutColor;
        public boolean previewParsed;
        public int previewWallpaperOffset;
        public String slug;
        public int sortIndex;
        public ArrayList<ThemeAccent> themeAccents;
        public SparseArray<ThemeAccent> themeAccentsMap;
        public boolean themeLoaded = true;
        public InputFile uploadedFile;
        public InputFile uploadedThumb;
        public String uploadingFile;
        public String uploadingThumb;

        ThemeInfo() {
        }

        /* Access modifiers changed, original: 0000 */
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
                FileLog.e(e);
                return null;
            }
        }

        private void loadWallpapers(SharedPreferences sharedPreferences) {
            ArrayList arrayList = this.themeAccents;
            String str = "_owp";
            if (arrayList == null || arrayList.isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(this.name);
                stringBuilder.append(str);
                loadOverrideWallpaper(sharedPreferences, null, stringBuilder.toString());
                return;
            }
            int size = this.themeAccents.size();
            for (int i = 0; i < size; i++) {
                ThemeAccent themeAccent = (ThemeAccent) this.themeAccents.get(i);
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(this.name);
                stringBuilder2.append("_");
                stringBuilder2.append(themeAccent.id);
                stringBuilder2.append(str);
                loadOverrideWallpaper(sharedPreferences, themeAccent, stringBuilder2.toString());
            }
        }

        private void loadOverrideWallpaper(SharedPreferences sharedPreferences, ThemeAccent themeAccent, String str) {
            String str2 = "wallId";
            try {
                String string = sharedPreferences.getString(str, null);
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
                    if (jSONObject.has(str2) && jSONObject.getLong(str2) == 1000001) {
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
            TL_theme tL_theme = this.info;
            return tL_theme != null ? tL_theme.title : this.name;
        }

        public void setCurrentAccentId(int i) {
            this.currentAccentId = i;
            ThemeAccent accent = getAccent(false);
            if (accent != null) {
                this.overrideWallpaper = accent.overrideWallpaper;
            }
        }

        public String generateWallpaperName(ThemeAccent themeAccent, boolean z) {
            if (themeAccent == null) {
                themeAccent = getAccent(false);
            }
            String str = ".jpg";
            String str2 = "_wp_o";
            String str3 = "_wp";
            StringBuilder stringBuilder;
            if (themeAccent != null) {
                StringBuilder stringBuilder2 = new StringBuilder();
                String str4 = "_";
                if (z) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(this.name);
                    stringBuilder.append(str4);
                    stringBuilder.append(themeAccent.id);
                    stringBuilder.append(str2);
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(this.name);
                    stringBuilder.append(str4);
                    stringBuilder.append(themeAccent.id);
                    stringBuilder.append(str3);
                }
                stringBuilder2.append(stringBuilder.toString());
                stringBuilder2.append(Utilities.random.nextInt());
                stringBuilder2.append(str);
                return stringBuilder2.toString();
            }
            StringBuilder stringBuilder3 = new StringBuilder();
            if (z) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(this.name);
                stringBuilder.append(str2);
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append(this.name);
                stringBuilder.append(str3);
            }
            stringBuilder3.append(stringBuilder.toString());
            stringBuilder3.append(Utilities.random.nextInt());
            stringBuilder3.append(str);
            return stringBuilder3.toString();
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
            if (this.firstAccentIsDefault && this.currentAccentId == Theme.DEFALT_THEME_ACCENT_ID) {
                return -1;
            }
            return this.previewInColor;
        }

        public int getPreviewOutColor() {
            if (this.firstAccentIsDefault && this.currentAccentId == Theme.DEFALT_THEME_ACCENT_ID) {
                return -983328;
            }
            return this.previewOutColor;
        }

        public int getPreviewBackgroundColor() {
            if (this.firstAccentIsDefault && this.currentAccentId == Theme.DEFALT_THEME_ACCENT_ID) {
                return -3155485;
            }
            return this.previewBackgroundColor;
        }

        private boolean isDefaultMyMessages() {
            boolean z = false;
            if (!this.firstAccentIsDefault) {
                return false;
            }
            int i = this.currentAccentId;
            int i2 = Theme.DEFALT_THEME_ACCENT_ID;
            if (i == i2) {
                return true;
            }
            ThemeAccent themeAccent = (ThemeAccent) this.themeAccentsMap.get(i2);
            ThemeAccent themeAccent2 = (ThemeAccent) this.themeAccentsMap.get(this.currentAccentId);
            if (themeAccent.myMessagesAccentColor == themeAccent2.myMessagesAccentColor && themeAccent.myMessagesGradientAccentColor == themeAccent2.myMessagesGradientAccentColor) {
                z = true;
            }
            return z;
        }

        private boolean isDefaultMainAccent() {
            boolean z = false;
            if (!this.firstAccentIsDefault) {
                return false;
            }
            int i = this.currentAccentId;
            int i2 = Theme.DEFALT_THEME_ACCENT_ID;
            if (i == i2) {
                return true;
            }
            ThemeAccent themeAccent = (ThemeAccent) this.themeAccentsMap.get(i2);
            ThemeAccent themeAccent2 = (ThemeAccent) this.themeAccentsMap.get(this.currentAccentId);
            if (themeAccent2 != null && themeAccent.accentColor == themeAccent2.accentColor) {
                z = true;
            }
            return z;
        }

        public boolean hasAccentColors() {
            return this.defaultAccentCount != 0;
        }

        public boolean isDark() {
            if (!"Dark Blue".equals(this.name)) {
                if (!"Night".equals(this.name)) {
                    return false;
                }
            }
            return true;
        }

        public boolean isLight() {
            return this.pathToFile == null && !isDark();
        }

        public String getKey() {
            if (this.info == null) {
                return this.name;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("remote");
            stringBuilder.append(this.info.id);
            return stringBuilder.toString();
        }

        static ThemeInfo createWithJson(JSONObject jSONObject) {
            String str = "loaded";
            String str2 = "info";
            String str3 = "account";
            if (jSONObject == null) {
                return null;
            }
            try {
                ThemeInfo themeInfo = new ThemeInfo();
                themeInfo.name = jSONObject.getString("name");
                themeInfo.pathToFile = jSONObject.getString("path");
                if (jSONObject.has(str3)) {
                    themeInfo.account = jSONObject.getInt(str3);
                }
                if (jSONObject.has(str2)) {
                    try {
                        SerializedData serializedData = new SerializedData(Utilities.hexToBytes(jSONObject.getString(str2)));
                        themeInfo.info = (TL_theme) org.telegram.tgnet.TLRPC.Theme.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                }
                if (jSONObject.has(str)) {
                    themeInfo.loaded = jSONObject.getBoolean(str);
                }
                return themeInfo;
            } catch (Exception e) {
                FileLog.e(e);
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

        private void setAccentColorOptions(int[] iArr) {
            setAccentColorOptions(iArr, null, null, null, null, null, null, null, null);
        }

        private void setAccentColorOptions(int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4, int[] iArr5, int[] iArr6, String[] strArr, int[] iArr7, int[] iArr8) {
            int[] iArr9 = iArr;
            this.defaultAccentCount = iArr9.length;
            this.themeAccents = new ArrayList();
            this.themeAccentsMap = new SparseArray();
            this.accentsByThemeId = new LongSparseArray();
            int i = 0;
            while (i < iArr9.length) {
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
                    if (this.firstAccentIsDefault && themeAccent.id == Theme.DEFALT_THEME_ACCENT_ID) {
                        themeAccent.backgroundOverrideColor = 4294967296L;
                    } else {
                        themeAccent.backgroundOverrideColor = (long) iArr4[i];
                    }
                }
                if (iArr5 != null) {
                    if (this.firstAccentIsDefault && themeAccent.id == Theme.DEFALT_THEME_ACCENT_ID) {
                        themeAccent.backgroundGradientOverrideColor = 4294967296L;
                    } else {
                        themeAccent.backgroundGradientOverrideColor = (long) iArr5[i];
                    }
                }
                if (strArr != null) {
                    themeAccent.patternIntensity = ((float) iArr8[i]) / 100.0f;
                    themeAccent.backgroundRotation = iArr7[i];
                    themeAccent.patternSlug = strArr[i];
                }
                this.themeAccentsMap.put(themeAccent.id, themeAccent);
                this.themeAccents.add(themeAccent);
                i++;
            }
            this.accentBaseColor = ((ThemeAccent) this.themeAccentsMap.get(0)).accentColor;
        }

        private void loadThemeDocument() {
            this.loaded = false;
            this.loadingThemeWallpaperName = null;
            this.newPathToWallpaper = null;
            addObservers();
            FileLoader instance = FileLoader.getInstance(this.account);
            TL_theme tL_theme = this.info;
            instance.loadFile(tL_theme.document, tL_theme, 1, 1);
        }

        private void addObservers() {
            NotificationCenter.getInstance(this.account).addObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(this.account).addObserver(this, NotificationCenter.fileDidFailToLoad);
        }

        private void removeObservers() {
            NotificationCenter.getInstance(this.account).removeObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(this.account).removeObserver(this, NotificationCenter.fileDidFailToLoad);
        }

        private void onFinishLoadingRemoteTheme() {
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
                objArr[3] = Integer.valueOf(-1);
                globalInstance.postNotificationName(i, objArr);
            }
        }

        public static boolean accentEquals(ThemeAccent themeAccent, TL_themeSettings tL_themeSettings) {
            int i;
            int i2;
            long j;
            int i3 = tL_themeSettings.message_top_color;
            if (tL_themeSettings.message_bottom_color == i3) {
                i3 = 0;
            }
            CharSequence charSequence = null;
            float f = 0.0f;
            WallPaper wallPaper = tL_themeSettings.wallpaper;
            if (wallPaper != null) {
                WallPaperSettings wallPaperSettings = wallPaper.settings;
                if (wallPaperSettings != null) {
                    i = wallPaperSettings.background_color;
                    i2 = wallPaperSettings.second_background_color;
                    j = i2 == 0 ? 4294967296L : (long) i2;
                    i2 = AndroidUtilities.getWallpaperRotation(tL_themeSettings.wallpaper.settings.rotation, false);
                    WallPaper wallPaper2 = tL_themeSettings.wallpaper;
                    if (!(wallPaper2 instanceof TL_wallPaperNoFile) && wallPaper2.pattern) {
                        charSequence = wallPaper2.slug;
                        f = ((float) wallPaper2.settings.intensity) / 100.0f;
                    }
                    if (tL_themeSettings.accent_color != themeAccent.accentColor && tL_themeSettings.message_bottom_color == themeAccent.myMessagesAccentColor && i3 == themeAccent.myMessagesGradientAccentColor && ((long) i) == themeAccent.backgroundOverrideColor && j == themeAccent.backgroundGradientOverrideColor && r4 == themeAccent.backgroundRotation && TextUtils.equals(charSequence, themeAccent.patternSlug) && ((double) Math.abs(f - themeAccent.patternIntensity)) < 0.001d) {
                        return true;
                    }
                    return false;
                }
            }
            j = 0;
            i = 0;
            i2 = 0;
            return tL_themeSettings.accent_color != themeAccent.accentColor ? false : false;
        }

        public static void fillAccentValues(ThemeAccent themeAccent, TL_themeSettings tL_themeSettings) {
            themeAccent.accentColor = tL_themeSettings.accent_color;
            themeAccent.myMessagesAccentColor = tL_themeSettings.message_bottom_color;
            themeAccent.myMessagesGradientAccentColor = tL_themeSettings.message_top_color;
            if (themeAccent.myMessagesAccentColor == themeAccent.myMessagesGradientAccentColor) {
                themeAccent.myMessagesGradientAccentColor = 0;
            }
            WallPaper wallPaper = tL_themeSettings.wallpaper;
            if (wallPaper != null) {
                WallPaperSettings wallPaperSettings = wallPaper.settings;
                if (wallPaperSettings != null) {
                    themeAccent.backgroundOverrideColor = (long) wallPaperSettings.background_color;
                    int i = wallPaperSettings.second_background_color;
                    if (i == 0) {
                        themeAccent.backgroundGradientOverrideColor = 4294967296L;
                    } else {
                        themeAccent.backgroundGradientOverrideColor = (long) i;
                    }
                    themeAccent.backgroundRotation = AndroidUtilities.getWallpaperRotation(tL_themeSettings.wallpaper.settings.rotation, false);
                    WallPaper wallPaper2 = tL_themeSettings.wallpaper;
                    if (!(wallPaper2 instanceof TL_wallPaperNoFile) && wallPaper2.pattern) {
                        themeAccent.patternSlug = wallPaper2.slug;
                        WallPaperSettings wallPaperSettings2 = wallPaper2.settings;
                        themeAccent.patternIntensity = ((float) wallPaperSettings2.intensity) / 100.0f;
                        themeAccent.patternMotion = wallPaperSettings2.motion;
                    }
                }
            }
        }

        public ThemeAccent createNewAccent(TL_themeSettings tL_themeSettings) {
            ThemeAccent themeAccent = new ThemeAccent();
            fillAccentValues(themeAccent, tL_themeSettings);
            themeAccent.parentTheme = this;
            return themeAccent;
        }

        public ThemeAccent createNewAccent(TL_theme tL_theme, int i) {
            if (tL_theme == null) {
                return null;
            }
            ThemeAccent themeAccent = (ThemeAccent) this.accentsByThemeId.get(tL_theme.id);
            if (themeAccent != null) {
                return themeAccent;
            }
            int i2 = this.lastAccentId + 1;
            this.lastAccentId = i2;
            ThemeAccent createNewAccent = createNewAccent(tL_theme.settings);
            createNewAccent.id = i2;
            createNewAccent.info = tL_theme;
            createNewAccent.account = i;
            this.themeAccentsMap.put(i2, createNewAccent);
            this.themeAccents.add(0, createNewAccent);
            this.accentsByThemeId.put(tL_theme.id, createNewAccent);
            return createNewAccent;
        }

        public ThemeAccent getAccent(boolean z) {
            if (this.themeAccents == null) {
                return null;
            }
            ThemeAccent themeAccent = (ThemeAccent) this.themeAccentsMap.get(this.currentAccentId);
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
            ThemeAccent themeAccent = (ThemeAccent) this.themeAccentsMap.get(i);
            return themeAccent != null ? themeAccent.accentColor : 0;
        }

        public boolean createBackground(File file, String str) {
            try {
                Bitmap scaledBitmap = ThemesHorizontalListCell.getScaledBitmap((float) AndroidUtilities.dp(640.0f), (float) AndroidUtilities.dp(360.0f), file.getAbsolutePath(), null, 0);
                if (!(scaledBitmap == null || this.patternBgColor == 0)) {
                    int averageColor;
                    Bitmap createBitmap = Bitmap.createBitmap(scaledBitmap.getWidth(), scaledBitmap.getHeight(), scaledBitmap.getConfig());
                    Canvas canvas = new Canvas(createBitmap);
                    if (this.patternBgGradientColor != 0) {
                        averageColor = AndroidUtilities.getAverageColor(this.patternBgColor, this.patternBgGradientColor);
                        GradientDrawable gradientDrawable = new GradientDrawable(BackgroundGradientDrawable.getGradientOrientation(this.patternBgGradientRotation), new int[]{this.patternBgColor, this.patternBgGradientColor});
                        gradientDrawable.setBounds(0, 0, createBitmap.getWidth(), createBitmap.getHeight());
                        gradientDrawable.draw(canvas);
                    } else {
                        averageColor = AndroidUtilities.getPatternColor(this.patternBgColor);
                        canvas.drawColor(this.patternBgColor);
                    }
                    Paint paint = new Paint(2);
                    paint.setColorFilter(new PorterDuffColorFilter(averageColor, Mode.SRC_IN));
                    paint.setAlpha((int) ((((float) this.patternIntensity) / 100.0f) * 255.0f));
                    canvas.drawBitmap(scaledBitmap, 0.0f, 0.0f, paint);
                    canvas.setBitmap(null);
                    scaledBitmap = createBitmap;
                }
                if (this.isBlured) {
                    scaledBitmap = Utilities.blurWallpaper(scaledBitmap);
                }
                FileOutputStream fileOutputStream = new FileOutputStream(str);
                scaledBitmap.compress(CompressFormat.JPEG, 87, fileOutputStream);
                fileOutputStream.close();
                return true;
            } catch (Throwable th) {
                FileLog.e(th);
                return false;
            }
        }

        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (i == NotificationCenter.fileDidLoad || i == NotificationCenter.fileDidFailToLoad) {
                String str = (String) objArr[0];
                TL_theme tL_theme = this.info;
                if (!(tL_theme == null || tL_theme.document == null)) {
                    if (str.equals(this.loadingThemeWallpaperName)) {
                        this.loadingThemeWallpaperName = null;
                        Utilities.globalQueue.postRunnable(new -$$Lambda$Theme$ThemeInfo$6uoKlY90X4JDy2IdFv_Jxyk64R4(this, (File) objArr[1]));
                    } else if (str.equals(FileLoader.getAttachFileName(this.info.document))) {
                        removeObservers();
                        if (i == NotificationCenter.fileDidLoad) {
                            File file = new File(this.pathToFile);
                            TL_theme tL_theme2 = this.info;
                            ThemeInfo fillThemeValues = Theme.fillThemeValues(file, tL_theme2.title, tL_theme2);
                            if (fillThemeValues != null) {
                                str = fillThemeValues.pathToWallpaper;
                                if (!(str == null || new File(str).exists())) {
                                    this.patternBgColor = fillThemeValues.patternBgColor;
                                    this.patternBgGradientColor = fillThemeValues.patternBgGradientColor;
                                    this.patternBgGradientRotation = fillThemeValues.patternBgGradientRotation;
                                    this.isBlured = fillThemeValues.isBlured;
                                    this.patternIntensity = fillThemeValues.patternIntensity;
                                    this.newPathToWallpaper = fillThemeValues.pathToWallpaper;
                                    TL_account_getWallPaper tL_account_getWallPaper = new TL_account_getWallPaper();
                                    TL_inputWallPaperSlug tL_inputWallPaperSlug = new TL_inputWallPaperSlug();
                                    tL_inputWallPaperSlug.slug = fillThemeValues.slug;
                                    tL_account_getWallPaper.wallpaper = tL_inputWallPaperSlug;
                                    ConnectionsManager.getInstance(fillThemeValues.account).sendRequest(tL_account_getWallPaper, new -$$Lambda$Theme$ThemeInfo$6ReCOOMwd1PiyJLpcgj-hZxezI8(this, fillThemeValues));
                                    return;
                                }
                            }
                            onFinishLoadingRemoteTheme();
                        }
                    }
                }
            }
        }

        public /* synthetic */ void lambda$didReceivedNotification$0$Theme$ThemeInfo(File file) {
            createBackground(file, this.newPathToWallpaper);
            AndroidUtilities.runOnUIThread(new -$$Lambda$Theme$ThemeInfo$R_IXO6EFVHCidGMczoUlNZ2P2Wg(this));
        }

        public /* synthetic */ void lambda$didReceivedNotification$2$Theme$ThemeInfo(ThemeInfo themeInfo, TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$Theme$ThemeInfo$YkO5Te8GVmyGazWPzwgIwQAbUu4(this, tLObject, themeInfo));
        }

        public /* synthetic */ void lambda$null$1$Theme$ThemeInfo(TLObject tLObject, ThemeInfo themeInfo) {
            if (tLObject instanceof TL_wallPaper) {
                TL_wallPaper tL_wallPaper = (TL_wallPaper) tLObject;
                this.loadingThemeWallpaperName = FileLoader.getAttachFileName(tL_wallPaper.document);
                addObservers();
                FileLoader.getInstance(themeInfo.account).loadFile(tL_wallPaper.document, tL_wallPaper, 1, 1);
                return;
            }
            onFinishLoadingRemoteTheme();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:131:0x2c1f A:{Catch:{ all -> 0x2c3a }} */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x2CLASSNAME A:{Catch:{ all -> 0x2c3c }} */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x2d5d A:{Catch:{ Exception -> 0x2e40 }} */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x2d7d A:{Catch:{ Exception -> 0x2e40 }} */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x2d7a A:{Catch:{ Exception -> 0x2e40 }} */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x2d9d A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x2d8a A:{Catch:{ Exception -> 0x2e40 }} */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x2acc A:{SYNTHETIC, Splitter:B:67:0x2acc} */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x2abb A:{Catch:{ Exception -> 0x2e48 }} */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x2aed A:{SYNTHETIC, Splitter:B:78:0x2aed} */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x2b1b A:{Catch:{ Exception -> 0x2e44 }} */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x2dab A:{Catch:{ Exception -> 0x2e40 }} */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x2e11 A:{Catch:{ Exception -> 0x2e40 }} */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x2e0a A:{Catch:{ Exception -> 0x2e40 }} */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x2e2d A:{Catch:{ Exception -> 0x2e40 }} */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x2e26 A:{Catch:{ Exception -> 0x2e40 }} */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x2e53  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x2e50  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x2ef7  */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x2var_  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x2f1c  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x2f1a  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x2e50  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x2e53  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x2ea0  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x2ef7  */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x2var_  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x2f1a  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x2f1c  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x2e53  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x2e50  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x2ea0  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x2ef7  */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x2var_  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x2f1c  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x2f1a  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x2d5d A:{Catch:{ Exception -> 0x2e40 }} */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x2d7a A:{Catch:{ Exception -> 0x2e40 }} */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x2d7d A:{Catch:{ Exception -> 0x2e40 }} */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x2d8a A:{Catch:{ Exception -> 0x2e40 }} */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x2d9d A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x2e50  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x2e53  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x2ea0  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x2ef7  */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x2var_  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x2f1a  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x2f1c  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x2e53  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x2e50  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x2ea0  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x2ef7  */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x2var_  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x2f1c  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x2f1a  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x2e50  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x2e53  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x2ea0  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x2ef7  */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x2var_  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x2f1a  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x2f1c  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x2e53  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x2e50  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x2ea0  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x2ef7  */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x2var_  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x2f1c  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x2f1a  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x2e50  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x2e53  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x2ea0  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x2ef7  */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x2var_  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x2f1a  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x2f1c  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x2d5d A:{Catch:{ Exception -> 0x2e40 }} */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x2d7d A:{Catch:{ Exception -> 0x2e40 }} */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x2d7a A:{Catch:{ Exception -> 0x2e40 }} */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x2d9d A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x2d8a A:{Catch:{ Exception -> 0x2e40 }} */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x2d5d A:{Catch:{ Exception -> 0x2e40 }} */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x2d7a A:{Catch:{ Exception -> 0x2e40 }} */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x2d7d A:{Catch:{ Exception -> 0x2e40 }} */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x2d8a A:{Catch:{ Exception -> 0x2e40 }} */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x2d9d A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x2e53  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x2e50  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x2ea0  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x2ef7  */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x2var_  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x2f1c  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x2f1a  */
    static {
        /*
        r0 = new java.lang.Object;
        r0.<init>();
        sync = r0;
        r0 = new java.lang.Object;
        r0.<init>();
        wallpaperSync = r0;
        r0 = new org.telegram.ui.ActionBar.Theme$1;
        r0.<init>();
        switchDayBrightnessRunnable = r0;
        r0 = new org.telegram.ui.ActionBar.Theme$2;
        r0.<init>();
        switchNightBrightnessRunnable = r0;
        r0 = 99;
        DEFALT_THEME_ACCENT_ID = r0;
        r1 = 0;
        selectedAutoNightType = r1;
        r0 = NUM; // 0x3e800000 float:0.25 double:5.180653787E-315;
        autoNightBrighnessThreshold = r0;
        r0 = 1320; // 0x528 float:1.85E-42 double:6.52E-321;
        autoNightDayStartTime = r0;
        r0 = 480; // 0x1e0 float:6.73E-43 double:2.37E-321;
        autoNightDayEndTime = r0;
        r0 = 1320; // 0x528 float:1.85E-42 double:6.52E-321;
        autoNightSunsetTime = r0;
        r2 = -1;
        r0 = java.lang.Integer.valueOf(r2);
        autoNightLastSunCheckDay = r2;
        r3 = 480; // 0x1e0 float:6.73E-43 double:2.37E-321;
        autoNightSunriseTime = r3;
        r3 = "";
        autoNightCityName = r3;
        r4 = NUM; // 0x40cNUM float:0.0 double:10000.0;
        autoNightLocationLatitude = r4;
        autoNightLocationLongitude = r4;
        r4 = new android.graphics.Paint;
        r5 = 1;
        r4.<init>(r5);
        maskPaint = r4;
        r4 = 3;
        r6 = new boolean[r4];
        loadingRemoteThemes = r6;
        r6 = new int[r4];
        lastLoadingThemesTime = r6;
        r6 = new int[r4];
        remoteThemesHash = r6;
        r6 = 6;
        r7 = new android.graphics.drawable.Drawable[r6];
        chat_attachButtonDrawables = r7;
        r7 = 2;
        r8 = new android.graphics.drawable.Drawable[r7];
        chat_locationDrawable = r8;
        r8 = new android.graphics.drawable.Drawable[r7];
        chat_contactDrawable = r8;
        r8 = 4;
        r9 = new android.graphics.drawable.Drawable[r8];
        chat_cornerOuter = r9;
        r9 = new android.graphics.drawable.Drawable[r8];
        chat_cornerInner = r9;
        r9 = 10;
        r9 = new int[]{r9, r7};
        r10 = android.graphics.drawable.Drawable.class;
        r9 = java.lang.reflect.Array.newInstance(r10, r9);
        r9 = (android.graphics.drawable.Drawable[][]) r9;
        chat_fileStatesDrawable = r9;
        r9 = new int[]{r6, r7};
        r10 = org.telegram.ui.Components.CombinedDrawable.class;
        r9 = java.lang.reflect.Array.newInstance(r10, r9);
        r9 = (org.telegram.ui.Components.CombinedDrawable[][]) r9;
        chat_fileMiniStatesDrawable = r9;
        r9 = 13;
        r9 = new int[]{r9, r7};
        r10 = android.graphics.drawable.Drawable.class;
        r9 = java.lang.reflect.Array.newInstance(r10, r9);
        r9 = (android.graphics.drawable.Drawable[][]) r9;
        chat_photoStatesDrawables = r9;
        r9 = new android.graphics.Path[r7];
        chat_filePath = r9;
        r9 = 7;
        r9 = new java.lang.String[r9];
        r10 = "avatar_backgroundRed";
        r9[r1] = r10;
        r10 = "avatar_backgroundOrange";
        r9[r5] = r10;
        r10 = "avatar_backgroundViolet";
        r9[r7] = r10;
        r10 = "avatar_backgroundGreen";
        r9[r4] = r10;
        r10 = "avatar_backgroundCyan";
        r9[r8] = r10;
        r10 = 5;
        r11 = "avatar_backgroundBlue";
        r9[r10] = r11;
        r11 = "avatar_backgroundPink";
        r9[r6] = r11;
        keys_avatar_background = r9;
        r9 = 7;
        r9 = new java.lang.String[r9];
        r11 = "avatar_nameInMessageRed";
        r9[r1] = r11;
        r11 = "avatar_nameInMessageOrange";
        r9[r5] = r11;
        r11 = "avatar_nameInMessageViolet";
        r9[r7] = r11;
        r11 = "avatar_nameInMessageGreen";
        r9[r4] = r11;
        r11 = "avatar_nameInMessageCyan";
        r9[r8] = r11;
        r11 = "avatar_nameInMessageBlue";
        r9[r10] = r11;
        r11 = "avatar_nameInMessagePink";
        r9[r6] = r11;
        keys_avatar_nameInMessage = r9;
        r9 = new java.util.HashSet;
        r9.<init>();
        myMessagesColorKeys = r9;
        r9 = new java.util.HashMap;
        r9.<init>();
        defaultColors = r9;
        r9 = new java.util.HashMap;
        r9.<init>();
        fallbackKeys = r9;
        r9 = new java.util.HashSet;
        r9.<init>();
        themeAccentExclusionKeys = r9;
        r9 = new java.lang.ThreadLocal;
        r9.<init>();
        hsvTemp1Local = r9;
        r9 = new java.lang.ThreadLocal;
        r9.<init>();
        hsvTemp2Local = r9;
        r9 = new java.lang.ThreadLocal;
        r9.<init>();
        hsvTemp3Local = r9;
        r9 = new java.lang.ThreadLocal;
        r9.<init>();
        hsvTemp4Local = r9;
        r9 = new java.lang.ThreadLocal;
        r9.<init>();
        hsvTemp5Local = r9;
        r9 = defaultColors;
        r11 = "dialogBackground";
        r9.put(r11, r0);
        r9 = defaultColors;
        r11 = -986896; // 0xfffffffffff0f0f0 float:NaN double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "dialogBackgroundGray";
        r9.put(r12, r11);
        r9 = defaultColors;
        r11 = -14540254; // 0xfffffffffvar_ float:-2.1551216E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "dialogTextBlack";
        r9.put(r12, r11);
        r9 = defaultColors;
        r12 = -14255946; // 0xfffffffffvar_b6 float:-2.2127861E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogTextLink";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = NUM; // 0x3362a9e3 float:5.2774237E-8 double:4.25935987E-315;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogLinkSelection";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -3319206; // 0xffffffffffcd5a5a float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogTextRed";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -2213318; // 0xffffffffffde3a3a float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogTextRed2";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -13660983; // 0xffffffffff2f8cc9 float:-2.333459E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogTextBlue";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -12937771; // 0xffffffffff3a95d5 float:-2.4801438E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogTextBlue2";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -12664327; // 0xffffffffff3ec1f9 float:-2.5356048E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogTextBlue3";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -15095832; // 0xfffffffffvar_a7e8 float:-2.042437E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogTextBlue4";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -13333567; // 0xfffffffffvar_bc1 float:-2.3998668E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogTextGray";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -9079435; // 0xfffffffffvar_ float:-3.2627073E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogTextGray2";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -6710887; // 0xfffffffffvar_ float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogTextGray3";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -5000269; // 0xffffffffffb3b3b3 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogTextGray4";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -6842473; // 0xfffffffffvar_ float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogTextHint";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -9999504; // 0xfffffffffvar_b70 float:-3.0760951E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogIcon";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -2011827; // 0xffffffffffe14d4d float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogRedIcon";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -2960686; // 0xffffffffffd2d2d2 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogGrayLine";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -9456923; // 0xffffffffff6fb2e5 float:-3.1861436E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogTopBackground";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -2368549; // 0xffffffffffdbdbdb float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogInputField";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -13129232; // 0xfffffffffvar_a9f0 float:-2.4413109E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogInputFieldActivated";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -12345121; // 0xfffffffffvar_a0df float:-2.6003475E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogCheckboxSquareBackground";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = "dialogCheckboxSquareCheck";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -9211021; // 0xfffffffffvar_ float:-3.2360185E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogCheckboxSquareUnchecked";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -5197648; // 0xffffffffffb0b0b0 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogCheckboxSquareDisabled";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -5000269; // 0xffffffffffb3b3b3 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogRadioBackground";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -13129232; // 0xfffffffffvar_a9f0 float:-2.4413109E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogRadioBackgroundChecked";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -14115349; // 0xfffffffffvar_deb float:-2.2413026E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogProgressCircle";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -11371101; // 0xfffffffffvar_da3 float:-2.7979022E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogLineProgress";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -2368549; // 0xffffffffffdbdbdb float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogLineProgressBackground";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -11955764; // 0xfffffffffvar_cc float:-2.6793185E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogButton";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = NUM; // 0xvar_ float:6.3108872E-30 double:1.24335691E-315;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogButtonSelector";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -657673; // 0xfffffffffff5f6f7 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogScrollGlow";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -11750155; // 0xffffffffff4cb4f5 float:-2.721021E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogRoundCheckBox";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = "dialogRoundCheckBoxCheck";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -12664327; // 0xffffffffff3ec1f9 float:-2.5356048E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogBadgeBackground";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = "dialogBadgeText";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = "dialogCameraIcon";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -NUM; // 0xfffffffff6f0f2f5 float:-2.4435137E33 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialog_inlineProgressBackground";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -9735304; // 0xffffffffff6b7378 float:-3.1296813E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialog_inlineProgress";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -854795; // 0xfffffffffff2f4f5 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogSearchBackground";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -6774617; // 0xfffffffffvar_a0a7 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogSearchHint";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -6182737; // 0xffffffffffa1a8af float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogSearchIcon";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = "dialogSearchText";
        r9.put(r12, r11);
        r9 = defaultColors;
        r12 = -11750155; // 0xffffffffff4cb4f5 float:-2.721021E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogFloatingButton";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = NUM; // 0xvar_ float:6.3108872E-30 double:1.24335691E-315;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogFloatingButtonPressed";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = "dialogFloatingIcon";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = NUM; // 0x12000000 float:4.0389678E-28 double:1.49202829E-315;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogShadowLine";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -6314840; // 0xffffffffff9fa4a8 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogEmptyImage";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -7565164; // 0xffffffffff8CLASSNAME float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "dialogEmptyText";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = "windowBackgroundWhite";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -6445135; // 0xffffffffff9da7b1 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "windowBackgroundUnchecked";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -11034919; // 0xfffffffffvar_ed9 float:-2.866088E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "windowBackgroundChecked";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = "windowBackgroundCheckText";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -14904349; // 0xffffffffff1CLASSNAMEe3 float:-2.0812744E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "progressCircle";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -8288629; // 0xfffffffffvar_b float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "windowBackgroundWhiteGrayIcon";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -12545331; // 0xfffffffffvar_cd float:-2.55974E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "windowBackgroundWhiteBlueText";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -12937771; // 0xffffffffff3a95d5 float:-2.4801438E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "windowBackgroundWhiteBlueText2";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -14255946; // 0xfffffffffvar_b6 float:-2.2127861E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "windowBackgroundWhiteBlueText3";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -14904349; // 0xffffffffff1CLASSNAMEe3 float:-2.0812744E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "windowBackgroundWhiteBlueText4";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -11759926; // 0xffffffffff4c8eca float:-2.7190391E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "windowBackgroundWhiteBlueText5";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -12940081; // 0xffffffffff3a8ccf float:-2.4796753E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "windowBackgroundWhiteBlueText6";
        r9.put(r13, r12);
        r9 = defaultColors;
        r13 = -13141330; // 0xfffffffffvar_aae float:-2.4388571E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteBlueText7";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -14776109; // 0xffffffffff1e88d3 float:-2.1072846E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteBlueButton";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -13132315; // 0xfffffffffvar_de5 float:-2.4406856E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteBlueIcon";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -14248148; // 0xfffffffffvar_c float:-2.2143678E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteGreenText";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -13129704; // 0xfffffffffvar_a818 float:-2.4412152E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteGreenText2";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -3319206; // 0xffffffffffcd5a5a float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteRedText";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -2404015; // 0xffffffffffdb5151 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteRedText2";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -2995895; // 0xffffffffffd24949 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteRedText3";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -3198928; // 0xffffffffffcvar_ float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteRedText4";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -1230535; // 0xffffffffffed3939 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteRedText5";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -39322; // 0xfffffffffffvar_ float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteRedText6";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -8156010; // 0xfffffffffvar_CLASSNAME float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteGrayText";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -7697782; // 0xffffffffff8a8a8a float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteGrayText2";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -6710887; // 0xfffffffffvar_ float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteGrayText3";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -8355712; // 0xfffffffffvar_ float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteGrayText4";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -6052957; // 0xffffffffffa3a3a3 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteGrayText5";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -9079435; // 0xfffffffffvar_ float:-3.2627073E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteGrayText6";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -3750202; // 0xffffffffffc6c6c6 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteGrayText7";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -9605774; // 0xffffffffff6d6d72 float:-3.155953E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteGrayText8";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -2368549; // 0xffffffffffdbdbdb float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteGrayLine";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = "windowBackgroundWhiteBlackText";
        r9.put(r13, r11);
        r9 = defaultColors;
        r13 = -5723992; // 0xffffffffffa8a8a8 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteHintText";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -12937771; // 0xffffffffff3a95d5 float:-2.4801438E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteValueText";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -14255946; // 0xfffffffffvar_b6 float:-2.2127861E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteLinkText";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = NUM; // 0x3362a9e3 float:5.2774237E-8 double:4.25935987E-315;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteLinkSelection";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -12937771; // 0xffffffffff3a95d5 float:-2.4801438E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteBlueHeader";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -2368549; // 0xffffffffffdbdbdb float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteInputField";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -13129232; // 0xfffffffffvar_a9f0 float:-2.4413109E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundWhiteInputFieldActivated";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -5196358; // 0xffffffffffb0b5ba float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "switchTrack";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -11358743; // 0xfffffffffvar_ade9 float:-2.8004087E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "switchTrackChecked";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -8221031; // 0xfffffffffvar_e99 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "switchTrackBlue";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -12810041; // 0xffffffffff3CLASSNAMEc7 float:-2.5060505E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "switchTrackBlueChecked";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = "switchTrackBlueThumb";
        r9.put(r13, r0);
        r9 = defaultColors;
        r13 = "switchTrackBlueThumbChecked";
        r9.put(r13, r0);
        r9 = defaultColors;
        r13 = NUM; // 0x17404a53 float:6.2132356E-25 double:1.927297214E-315;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "switchTrackBlueSelector";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = NUM; // 0x21024781 float:4.414035E-19 double:2.73612322E-315;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "switchTrackBlueSelectorChecked";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -688514; // 0xffffffffffvar_e7e float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "switch2Track";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -11358743; // 0xfffffffffvar_ade9 float:-2.8004087E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "switch2TrackChecked";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -12345121; // 0xfffffffffvar_a0df float:-2.6003475E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "checkboxSquareBackground";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = "checkboxSquareCheck";
        r9.put(r13, r0);
        r9 = defaultColors;
        r13 = -9211021; // 0xfffffffffvar_ float:-3.2360185E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "checkboxSquareUnchecked";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -5197648; // 0xffffffffffb0b0b0 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "checkboxSquareDisabled";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = NUM; // 0xvar_ float:6.3108872E-30 double:1.24335691E-315;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "listSelectorSDK21";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -5000269; // 0xffffffffffb3b3b3 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "radioBackground";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -13129232; // 0xfffffffffvar_a9f0 float:-2.4413109E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "radioBackgroundChecked";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -986896; // 0xfffffffffff0f0f0 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundGray";
        r9.put(r14, r13);
        r9 = defaultColors;
        r13 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "windowBackgroundGrayShadow";
        r9.put(r14, r13);
        r9 = defaultColors;
        r14 = -6974059; // 0xfffffffffvar_ float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "emptyListPlaceholder";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -2500135; // 0xffffffffffd9d9d9 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "divider";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -1117195; // 0xffffffffffeef3f5 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "graySection";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -8418927; // 0xffffffffff7var_ float:-3.3966742E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "key_graySectionText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -4202506; // 0xffffffffffbfdff6 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "contextProgressInner1";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -13920542; // 0xffffffffff2b96e2 float:-2.2808142E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "contextProgressOuter1";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -4202506; // 0xffffffffffbfdff6 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "contextProgressInner2";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "contextProgressOuter2";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -5000269; // 0xffffffffffb3b3b3 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "contextProgressInner3";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "contextProgressOuter3";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -3486256; // 0xffffffffffcacdd0 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "contextProgressInner4";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -13683656; // 0xffffffffff2var_ float:-2.3288603E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "contextProgressOuter4";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -11361317; // 0xfffffffffvar_a3db float:-2.7998867E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "fastScrollActive";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -3551791; // 0xffffffffffc9cdd1 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "fastScrollInactive";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "fastScrollText";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = "avatar_text";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -10043398; // 0xfffffffffvar_bffa float:-3.0671924E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "avatar_backgroundSaved";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -5654847; // 0xffffffffffa9b6c1 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "avatar_backgroundArchived";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -10043398; // 0xfffffffffvar_bffa float:-3.0671924E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "avatar_backgroundArchivedHidden";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -1743531; // 0xffffffffffe56555 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "avatar_backgroundRed";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -881592; // 0xffffffffffvar_CLASSNAME float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "avatar_backgroundOrange";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7436818; // 0xffffffffff8e85ee float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "avatar_backgroundViolet";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -8992691; // 0xfffffffffvar_CLASSNAMEd float:-3.280301E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "avatar_backgroundGreen";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -10502443; // 0xffffffffff5fbed5 float:-2.974087E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "avatar_backgroundCyan";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -11232035; // 0xfffffffffvar_cdd float:-2.8261082E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "avatar_backgroundBlue";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -887654; // 0xffffffffffvar_a float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "avatar_backgroundPink";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -1642505; // 0xffffffffffe6eff7 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "avatar_backgroundGroupCreateSpanBlue";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -11500111; // 0xfffffffffvar_b1 float:-2.7717359E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "avatar_backgroundInProfileBlue";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -10907718; // 0xfffffffffvar_fba float:-2.8918875E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "avatar_backgroundActionBarBlue";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -2626822; // 0xffffffffffd7eafa float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "avatar_subtitleInProfileBlue";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -11959891; // 0xfffffffffvar_ad float:-2.6784814E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "avatar_actionBarSelectorBlue";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "avatar_actionBarIconBlue";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -3516848; // 0xffffffffffca5650 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "avatar_nameInMessageRed";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -2589911; // 0xffffffffffd87b29 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "avatar_nameInMessageOrange";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -11627828; // 0xffffffffff4e92cc float:-2.7458318E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "avatar_nameInMessageViolet";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -11488718; // 0xfffffffffvar_b232 float:-2.7740467E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "avatar_nameInMessageGreen";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -13132104; // 0xfffffffffvar_eb8 float:-2.4407284E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "avatar_nameInMessageCyan";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -11627828; // 0xffffffffff4e92cc float:-2.7458318E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "avatar_nameInMessageBlue";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -11627828; // 0xffffffffff4e92cc float:-2.7458318E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "avatar_nameInMessagePink";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -11371101; // 0xfffffffffvar_da3 float:-2.7979022E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "actionBarDefault";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "actionBarDefaultIcon";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = "actionBarActionModeDefault";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = NUM; // 0x10000000 float:2.5243549E-29 double:1.32624737E-315;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "actionBarActionModeDefaultTop";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -9999761; // 0xfffffffffvar_a6f float:-3.076043E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "actionBarActionModeDefaultIcon";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "actionBarDefaultTitle";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -2758409; // 0xffffffffffd5e8f7 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "actionBarDefaultSubtitle";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -12554860; // 0xfffffffffvar_d94 float:-2.5578074E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "actionBarDefaultSelector";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = NUM; // 0x2var_ float:1.1641532E-10 double:3.895851647E-315;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "actionBarWhiteSelector";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "actionBarDefaultSearch";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -NUM; // 0xfffffffvar_ffffff float:-1.5407439E-33 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "actionBarDefaultSearchPlaceholder";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "actionBarDefaultSubmenuItem";
        r9.put(r14, r11);
        r9 = defaultColors;
        r14 = -9999504; // 0xfffffffffvar_b70 float:-3.0760951E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "actionBarDefaultSubmenuItemIcon";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "actionBarDefaultSubmenuBackground";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -1907998; // 0xffffffffffe2e2e2 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "actionBarActionModeDefaultSelector";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "actionBarTabActiveText";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -2758409; // 0xffffffffffd5e8f7 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "actionBarTabUnactiveText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "actionBarTabLine";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -12554860; // 0xfffffffffvar_d94 float:-2.5578074E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "actionBarTabSelector";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "actionBarBrowser";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -9471353; // 0xffffffffff6f7a87 float:-3.1832169E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "actionBarDefaultArchived";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -10590350; // 0xffffffffff5e6772 float:-2.9562573E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "actionBarDefaultArchivedSelector";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "actionBarDefaultArchivedIcon";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = "actionBarDefaultArchivedTitle";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = "actionBarDefaultArchivedSearch";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -NUM; // 0xfffffffvar_ffffff float:-1.5407439E-33 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "actionBarDefaultSearchArchivedPlaceholder";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -11810020; // 0xffffffffff4bcb1c float:-2.7088789E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_onlineCircle";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -11613090; // 0xffffffffff4ecc5e float:-2.748821E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_unreadCounter";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -3749428; // 0xffffffffffc6c9cc float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_unreadCounterMuted";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chats_unreadCounterText";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -10049056; // 0xfffffffffvar_a9e0 float:-3.0660448E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_archiveBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -6313293; // 0xffffffffff9faab3 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_archivePinBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chats_archiveIcon";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = "chats_archiveText";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = "chats_name";
        r9.put(r14, r11);
        r9 = defaultColors;
        r14 = -11382190; // 0xfffffffffvar_ float:-2.7956531E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_nameArchived";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -16734706; // 0xfffffffffvar_a60e float:-1.7100339E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_secretName";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -15093466; // 0xfffffffffvar_b126 float:-2.042917E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_secretIcon";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -14408668; // 0xfffffffffvar_ float:-2.1818104E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_nameIcon";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -5723992; // 0xffffffffffa8a8a8 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_pinnedIcon";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7631473; // 0xffffffffff8b8d8f float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_message";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7237231; // 0xfffffffffvar_ float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_messageArchived";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7434095; // 0xffffffffff8e9091 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_message_threeLines";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -2274503; // 0xffffffffffdd4b39 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_draft";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -12812624; // 0xffffffffff3c7eb0 float:-2.5055266E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_nameMessage";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7631473; // 0xffffffffff8b8d8f float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_nameMessageArchived";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -12434359; // 0xfffffffffvar_ float:-2.5822479E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_nameMessage_threeLines";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -10592674; // 0xffffffffff5e5e5e float:-2.955786E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_nameMessageArchived_threeLines";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -12812624; // 0xffffffffff3c7eb0 float:-2.5055266E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_attachMessage";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -12812624; // 0xffffffffff3c7eb0 float:-2.5055266E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_actionMessage";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -6973028; // 0xfffffffffvar_c float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_date";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_pinnedOverlay";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = NUM; // 0xvar_ float:6.3108872E-30 double:1.24335691E-315;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_tabletSelectedOverlay";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -12146122; // 0xfffffffffvar_aa36 float:-2.6407093E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_sentCheck";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -12146122; // 0xfffffffffvar_aa36 float:-2.6407093E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_sentReadCheck";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -9061026; // 0xfffffffffvar_bd5e float:-3.266441E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_sentClock";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -2796974; // 0xffffffffffd55252 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_sentError";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chats_sentErrorIcon";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -13391642; // 0xfffffffffvar_a8e6 float:-2.3880878E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_verifiedBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chats_verifiedCheck";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -4341308; // 0xffffffffffbdc1c4 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_muteIcon";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chats_mentionIcon";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = "chats_menuBackground";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -12303292; // 0xfffffffffvar_ float:-2.6088314E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_menuItemText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -10907718; // 0xfffffffffvar_fba float:-2.8918875E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_menuItemCheck";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7827048; // 0xfffffffffvar_ float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_menuItemIcon";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chats_menuName";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = "chats_menuPhone";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -4004353; // 0xffffffffffc2e5ff float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_menuPhoneCats";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chats_menuCloud";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -12420183; // 0xfffffffffvar_ba9 float:-2.5851231E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_menuCloudBackgroundCats";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chats_actionIcon";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -10114592; // 0xfffffffffvar_a9e0 float:-3.0527525E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_actionBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -11100714; // 0xfffffffffvar_dd6 float:-2.8527432E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_actionPressedBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -9211021; // 0xfffffffffvar_ float:-3.2360185E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_actionUnreadIcon";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chats_actionUnreadBackground";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -855310; // 0xfffffffffff2f2f2 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_actionUnreadPressedBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -10907718; // 0xfffffffffvar_fba float:-2.8918875E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_menuTopBackgroundCats";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -3749428; // 0xffffffffffc6c9cc float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_archivePullDownBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -10049056; // 0xfffffffffvar_a9e0 float:-3.0660448E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chats_archivePullDownBackgroundActive";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -12171706; // 0xfffffffffvar_ float:-2.6355202E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_attachMediaBanBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_attachMediaBanText";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = "chat_attachCheckBoxCheck";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -12995849; // 0xfffffffffvar_b2f7 float:-2.4683642E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_attachCheckBoxBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_attachPhotoBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -13391883; // 0xfffffffffvar_a7f5 float:-2.388039E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_attachActiveTab";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7169634; // 0xfffffffffvar_e float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_attachUnactiveTab";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -13421773; // 0xfffffffffvar_ float:-2.3819765E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_attachPermissionImage";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -1945520; // 0xffffffffffe25050 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_attachPermissionMark";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -9472134; // 0xffffffffff6var_a float:-3.1830585E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_attachPermissionText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -3355444; // 0xffffffffffcccccc float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_attachEmptyImage";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -12214795; // 0xfffffffffvar_df5 float:-2.6267807E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_attachGalleryBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_attachGalleryIcon";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -1351584; // 0xffffffffffeb6060 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_attachAudioBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_attachAudioIcon";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -13321743; // 0xfffffffffvar_b9f1 float:-2.402265E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_attachFileBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_attachFileIcon";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -868277; // 0xfffffffffff2CLASSNAMEb float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_attachContactBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_attachContactIcon";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -13187226; // 0xfffffffffvar_CLASSNAME float:-2.4295483E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_attachLocationBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_attachLocationIcon";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -868277; // 0xfffffffffff2CLASSNAMEb float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_attachPollBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_attachPollIcon";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -2758409; // 0xffffffffffd5e8f7 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_status";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -16725933; // 0xfffffffffvar_CLASSNAME float:-1.7118133E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inDownCall";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -47032; // 0xfffffffffffvar_ float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inUpCall";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -16725933; // 0xfffffffffvar_CLASSNAME float:-1.7118133E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outUpCall";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = NUM; // 0x66728fa6 float:2.8636563E23 double:8.491920826E-315;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_shareBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -NUM; // 0xfffffffvar_fa6 float:-1.2540116E-23 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_shareBackgroundSelected";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_lockIcon";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -5124893; // 0xffffffffffb1cce3 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_muteIcon";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_inBubble";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -1247235; // 0xffffffffffecf7fd float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inBubbleSelected";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -14862509; // 0xffffffffff1d3753 float:-2.0897606E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inBubbleShadow";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -1048610; // 0xffffffffffefffde float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outBubble";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = NUM; // 0x14000000 float:6.4623485E-27 double:1.65780921E-315;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outBubbleGradientSelectedOverlay";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -2492475; // 0xffffffffffd9f7c5 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outBubbleSelected";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -14781172; // 0xffffffffff1e750c float:-2.1062577E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outBubbleShadow";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_inMediaIcon";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -1050370; // 0xffffffffffeff8fe float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inMediaIconSelected";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -1048610; // 0xffffffffffefffde float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outMediaIcon";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -1967921; // 0xffffffffffe1f8cf float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outMediaIconSelected";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_messageTextIn";
        r9.put(r14, r13);
        r9 = defaultColors;
        r14 = "chat_messageTextOut";
        r9.put(r14, r13);
        r9 = defaultColors;
        r14 = -14255946; // 0xfffffffffvar_b6 float:-2.2127861E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_messageLinkIn";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -14255946; // 0xfffffffffvar_b6 float:-2.2127861E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_messageLinkOut";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_serviceText";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = "chat_serviceLink";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = "chat_serviceIcon";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = NUM; // 0x66000000 float:1.5111573E23 double:8.45482698E-315;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_mediaTimeBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -10637232; // 0xffffffffff5db050 float:-2.9467485E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outSentCheck";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -10637232; // 0xffffffffff5db050 float:-2.9467485E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outSentCheckSelected";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -10637232; // 0xffffffffff5db050 float:-2.9467485E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outSentCheckRead";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -10637232; // 0xffffffffff5db050 float:-2.9467485E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outSentCheckReadSelected";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -9061026; // 0xfffffffffvar_bd5e float:-3.266441E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outSentClock";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -9061026; // 0xfffffffffvar_bd5e float:-3.266441E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outSentClockSelected";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -6182221; // 0xffffffffffa1aab3 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inSentClock";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7094838; // 0xfffffffffvar_bdca float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inSentClockSelected";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_mediaSentCheck";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = "chat_mediaSentClock";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -6182221; // 0xffffffffffa1aab3 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inViews";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7094838; // 0xfffffffffvar_bdca float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inViewsSelected";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -9522601; // 0xffffffffff6eb257 float:-3.1728226E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outViews";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -9522601; // 0xffffffffff6eb257 float:-3.1728226E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outViewsSelected";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_mediaViews";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -4801083; // 0xffffffffffb6bdc5 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inMenu";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -6766130; // 0xfffffffffvar_c1ce float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inMenuSelected";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7221634; // 0xfffffffffvar_ce7e float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outMenu";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7221634; // 0xfffffffffvar_ce7e float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outMenuSelected";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_mediaMenu";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -11162801; // 0xfffffffffvar_ab4f float:-2.8401505E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outInstant";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -12019389; // 0xfffffffffvar_ float:-2.6664138E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outInstantSelected";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_inInstant";
        r9.put(r14, r12);
        r9 = defaultColors;
        r14 = -13600331; // 0xfffffffffvar_b5 float:-2.3457607E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inInstantSelected";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -2411211; // 0xffffffffffdb3535 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_sentError";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_sentErrorIcon";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = NUM; // 0x280a90f0 float:7.691967E-15 double:3.31903965E-315;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_selectedBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_previewDurationText";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = "chat_previewGameText";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = "chat_inPreviewInstantText";
        r9.put(r14, r12);
        r9 = defaultColors;
        r14 = -11162801; // 0xfffffffffvar_ab4f float:-2.8401505E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outPreviewInstantText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -13600331; // 0xfffffffffvar_b5 float:-2.3457607E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inPreviewInstantSelectedText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -12019389; // 0xfffffffffvar_ float:-2.6664138E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outPreviewInstantSelectedText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -1776928; // 0xffffffffffe4e2e0 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_secretTimeText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_stickerNameText";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = "chat_botButtonText";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = "chat_botProgress";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -13072697; // 0xfffffffffvar_c7 float:-2.4527776E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inForwardedNameText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -11162801; // 0xfffffffffvar_ab4f float:-2.8401505E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outForwardedNameText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_inViaBotNameText";
        r9.put(r14, r12);
        r9 = defaultColors;
        r14 = -11162801; // 0xfffffffffvar_ab4f float:-2.8401505E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outViaBotNameText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_stickerViaBotNameText";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -10903592; // 0xfffffffffvar_fd8 float:-2.8927243E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inReplyLine";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -9520791; // 0xffffffffff6eb969 float:-3.1731897E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outReplyLine";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_stickerReplyLine";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = "chat_inReplyNameText";
        r9.put(r14, r12);
        r9 = defaultColors;
        r14 = -11162801; // 0xfffffffffvar_ab4f float:-2.8401505E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outReplyNameText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_stickerReplyNameText";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = "chat_inReplyMessageText";
        r9.put(r14, r13);
        r9 = defaultColors;
        r14 = "chat_outReplyMessageText";
        r9.put(r14, r13);
        r9 = defaultColors;
        r14 = -6182221; // 0xffffffffffa1aab3 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inReplyMediaMessageText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -10112933; // 0xfffffffffvar_b05b float:-3.053089E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outReplyMediaMessageText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7752511; // 0xfffffffffvar_b4c1 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inReplyMediaMessageSelectedText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -10112933; // 0xfffffffffvar_b05b float:-3.053089E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outReplyMediaMessageSelectedText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_stickerReplyMessageText";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -9390872; // 0xfffffffffvar_b4e8 float:-3.1995404E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inPreviewLine";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7812741; // 0xfffffffffvar_CLASSNAMEb float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outPreviewLine";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_inSiteNameText";
        r9.put(r14, r12);
        r9 = defaultColors;
        r14 = -11162801; // 0xfffffffffvar_ab4f float:-2.8401505E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outSiteNameText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -11625772; // 0xffffffffff4e9ad4 float:-2.7462488E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inContactNameText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -11162801; // 0xfffffffffvar_ab4f float:-2.8401505E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outContactNameText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -13683656; // 0xffffffffff2var_ float:-2.3288603E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inContactPhoneText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -13683656; // 0xffffffffff2var_ float:-2.3288603E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inContactPhoneSelectedText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -13286860; // 0xfffffffffvar_ float:-2.4093401E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outContactPhoneText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -13286860; // 0xfffffffffvar_ float:-2.4093401E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outContactPhoneSelectedText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_mediaProgress";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = "chat_inAudioProgress";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -1048610; // 0xffffffffffefffde float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outAudioProgress";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -1050370; // 0xffffffffffeff8fe float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inAudioSelectedProgress";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -1967921; // 0xffffffffffe1f8cf float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outAudioSelectedProgress";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_mediaTimeText";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -6182221; // 0xffffffffffa1aab3 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inTimeText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -9391780; // 0xfffffffffvar_b15c float:-3.1993562E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outTimeText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -4143413; // 0xffffffffffc0c6cb float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_adminText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7752511; // 0xfffffffffvar_b4c1 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_adminSelectedText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7752511; // 0xfffffffffvar_b4c1 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inTimeSelectedText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -9391780; // 0xfffffffffvar_b15c float:-3.1993562E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outTimeSelectedText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -13683656; // 0xffffffffff2var_ float:-2.3288603E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inAudioPerfomerText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -13683656; // 0xffffffffff2var_ float:-2.3288603E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inAudioPerfomerSelectedText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -13286860; // 0xfffffffffvar_ float:-2.4093401E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outAudioPerfomerText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -13286860; // 0xfffffffffvar_ float:-2.4093401E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outAudioPerfomerSelectedText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -11625772; // 0xffffffffff4e9ad4 float:-2.7462488E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inAudioTitleText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -11162801; // 0xfffffffffvar_ab4f float:-2.8401505E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outAudioTitleText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -6182221; // 0xffffffffffa1aab3 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inAudioDurationText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -10112933; // 0xfffffffffvar_b05b float:-3.053089E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outAudioDurationText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7752511; // 0xfffffffffvar_b4c1 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inAudioDurationSelectedText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -10112933; // 0xfffffffffvar_b05b float:-3.053089E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outAudioDurationSelectedText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -1774864; // 0xffffffffffe4eaf0 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inAudioSeekbar";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = NUM; // 0x3fe4eaf0 float:1.7884197 double:5.296220484E-315;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inAudioCacheSeekbar";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -4463700; // 0xffffffffffbbe3ac float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outAudioSeekbar";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = NUM; // 0x3fbbe3ac float:1.4678855 double:5.28293587E-315;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outAudioCacheSeekbar";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -4399384; // 0xffffffffffbcdee8 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inAudioSeekbarSelected";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -5644906; // 0xffffffffffa9dd96 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outAudioSeekbarSelected";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -9259544; // 0xfffffffffvar_b5e8 float:-3.2261769E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inAudioSeekbarFill";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -8863118; // 0xfffffffffvar_CLASSNAME float:-3.3065816E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outAudioSeekbarFill";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -2169365; // 0xffffffffffdee5eb float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inVoiceSeekbar";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -4463700; // 0xffffffffffbbe3ac float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outVoiceSeekbar";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -4399384; // 0xffffffffffbcdee8 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inVoiceSeekbarSelected";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -5644906; // 0xffffffffffa9dd96 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outVoiceSeekbarSelected";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -9259544; // 0xfffffffffvar_b5e8 float:-3.2261769E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inVoiceSeekbarFill";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -8863118; // 0xfffffffffvar_CLASSNAME float:-3.3065816E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outVoiceSeekbarFill";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -1314571; // 0xffffffffffebf0f5 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inFileProgress";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -2427453; // 0xffffffffffdaf5c3 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outFileProgress";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -3413258; // 0xffffffffffcbeaf6 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inFileProgressSelected";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -3806041; // 0xffffffffffc5eca7 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outFileProgressSelected";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -11625772; // 0xffffffffff4e9ad4 float:-2.7462488E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inFileNameText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -11162801; // 0xfffffffffvar_ab4f float:-2.8401505E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outFileNameText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -6182221; // 0xffffffffffa1aab3 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inFileInfoText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -10112933; // 0xfffffffffvar_b05b float:-3.053089E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outFileInfoText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7752511; // 0xfffffffffvar_b4c1 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inFileInfoSelectedText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -10112933; // 0xfffffffffvar_b05b float:-3.053089E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outFileInfoSelectedText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -1314571; // 0xffffffffffebf0f5 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inFileBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -2427453; // 0xffffffffffdaf5c3 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outFileBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -3413258; // 0xffffffffffcbeaf6 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inFileBackgroundSelected";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -3806041; // 0xffffffffffc5eca7 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outFileBackgroundSelected";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -6182221; // 0xffffffffffa1aab3 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inVenueInfoText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -10112933; // 0xfffffffffvar_b05b float:-3.053089E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outVenueInfoText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7752511; // 0xfffffffffvar_b4c1 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inVenueInfoSelectedText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -10112933; // 0xfffffffffvar_b05b float:-3.053089E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outVenueInfoSelectedText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_mediaInfoText";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = NUM; // 0x3362a9e3 float:5.2774237E-8 double:4.25935987E-315;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_linkSelectBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = NUM; // 0x6662a9e3 float:2.6759717E23 double:8.48677336E-315;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_textSelectBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -986379; // 0xfffffffffff0f2f5 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_emojiPanelBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -11688214; // 0xffffffffff4da6ea float:-2.733584E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_emojiPanelBadgeBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_emojiPanelBadgeText";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -1709586; // 0xffffffffffe5e9ee float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_emojiSearchBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7036497; // 0xfffffffffvar_a1af float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_emojiSearchIcon";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = NUM; // 0x12000000 float:4.0389678E-28 double:1.49202829E-315;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_emojiPanelShadowLine";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7038047; // 0xfffffffffvar_ba1 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_emojiPanelEmptyText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -6445909; // 0xffffffffff9da4ab float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_emojiPanelIcon";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7564905; // 0xffffffffff8CLASSNAME float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_emojiBottomPanelIcon";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -13920286; // 0xffffffffff2b97e2 float:-2.280866E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_emojiPanelIconSelected";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -1907225; // 0xffffffffffe2e5e7 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_emojiPanelStickerPackSelector";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -11097104; // 0xfffffffffvar_abf0 float:-2.8534754E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_emojiPanelStickerPackSelectorLine";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7564905; // 0xffffffffff8CLASSNAME float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_emojiPanelBackspace";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_emojiPanelMasksIcon";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -10305560; // 0xfffffffffvar_bfe8 float:-3.0140196E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_emojiPanelMasksIconSelected";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_emojiPanelTrendingTitle";
        r9.put(r14, r11);
        r9 = defaultColors;
        r14 = -8221804; // 0xfffffffffvar_b94 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_emojiPanelStickerSetName";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -14184997; // 0xfffffffffvar_ddb float:-2.2271763E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_emojiPanelStickerSetNameHighlight";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -5130564; // 0xffffffffffb1b6bc float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_emojiPanelStickerSetNameIcon";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7697782; // 0xffffffffff8a8a8a float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_emojiPanelTrendingDescription";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -13220017; // 0xfffffffffvar_f float:-2.4228975E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_botKeyboardButtonText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -1775639; // 0xffffffffffe4e7e9 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_botKeyboardButtonBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -3354156; // 0xffffffffffccd1d4 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_botKeyboardButtonBackgroundPressed";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -6113849; // 0xffffffffffa2b5c7 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_unreadMessagesStartArrowIcon";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -11102772; // 0xfffffffffvar_cc float:-2.8523258E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_unreadMessagesStartText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_unreadMessagesStartBackground";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -6113849; // 0xffffffffffa2b5c7 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inFileIcon";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7883067; // 0xfffffffffvar_b6c5 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inFileSelectedIcon";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -8011912; // 0xfffffffffvar_bvar_ float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outFileIcon";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -8011912; // 0xfffffffffvar_bvar_ float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outFileSelectedIcon";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -1314571; // 0xffffffffffebf0f5 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inLocationBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -6113849; // 0xffffffffffa2b5c7 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inLocationIcon";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -2427453; // 0xffffffffffdaf5c3 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outLocationBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7880840; // 0xfffffffffvar_bvar_ float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outLocationIcon";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -9259544; // 0xfffffffffvar_b5e8 float:-3.2261769E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inContactBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_inContactIcon";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -8863118; // 0xfffffffffvar_CLASSNAME float:-3.3065816E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outContactBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -1048610; // 0xffffffffffefffde float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outContactIcon";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -12146122; // 0xfffffffffvar_aa36 float:-2.6407093E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outBroadcast";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_mediaBroadcast";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -9999761; // 0xfffffffffvar_a6f float:-3.076043E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_searchPanelIcons";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -9999761; // 0xfffffffffvar_a6f float:-3.076043E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_searchPanelText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -8421505; // 0xffffffffff7f7f7f float:-3.3961514E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_secretChatStatusText";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_fieldOverlayText";
        r9.put(r14, r12);
        r9 = defaultColors;
        r14 = "chat_stickersHintPanel";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -11032346; // 0xfffffffffvar_a8e6 float:-2.86661E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_replyPanelIcons";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -7432805; // 0xffffffffff8e959b float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_replyPanelClose";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_replyPanelName";
        r9.put(r14, r12);
        r9 = defaultColors;
        r14 = "chat_replyPanelMessage";
        r9.put(r14, r11);
        r9 = defaultColors;
        r14 = -1513240; // 0xffffffffffe8e8e8 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_replyPanelLine";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_messagePanelBackground";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = "chat_messagePanelText";
        r9.put(r14, r13);
        r9 = defaultColors;
        r14 = -5985101; // 0xffffffffffa4acb3 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_messagePanelHint";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -11230757; // 0xfffffffffvar_a1db float:-2.8263674E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_messagePanelCursor";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_messagePanelShadow";
        r9.put(r14, r13);
        r9 = defaultColors;
        r14 = -7432805; // 0xffffffffff8e959b float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_messagePanelIcons";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -11817481; // 0xffffffffff4badf7 float:-2.7073656E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_messagePanelVideoFrame";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_recordedVoicePlayPause";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -2495749; // 0xffffffffffd9eafb float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_recordedVoicePlayPausePressed";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -2468275; // 0xffffffffffda564d float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_recordedVoiceDot";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -9981205; // 0xfffffffffvar_b2eb float:-3.0798066E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_recordedVoiceBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -6107400; // 0xffffffffffa2cef8 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_recordedVoiceProgress";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_recordedVoiceProgressInner";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -6710887; // 0xfffffffffvar_ float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_recordVoiceCancel";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -10309397; // 0xfffffffffvar_b0eb float:-3.0132414E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_messagePanelSend";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -5987164; // 0xffffffffffa4a4a4 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "key_chat_messagePanelVoiceLock";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "key_chat_messagePanelVoiceLockBackground";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = "key_chat_messagePanelVoiceLockShadow";
        r9.put(r14, r13);
        r9 = defaultColors;
        r14 = -11711413; // 0xffffffffff4d4c4b float:-2.7288787E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_recordTime";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -11688214; // 0xffffffffff4da6ea float:-2.733584E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_emojiPanelNewTrending";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_gifSaveHintText";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -NUM; // 0xffffffffcCLASSNAME float:-3.8028356E7 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_gifSaveHintBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_goDownButton";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = "chat_goDownButtonShadow";
        r9.put(r14, r13);
        r9 = defaultColors;
        r14 = -7432805; // 0xffffffffff8e959b float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_goDownButtonIcon";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_goDownButtonCounter";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -11689240; // 0xffffffffff4da2e8 float:-2.733376E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_goDownButtonCounterBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -5395027; // 0xffffffffffadadad float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_messagePanelCancelInlineBot";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_messagePanelVoicePressed";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -11037236; // 0xfffffffffvar_cc float:-2.865618E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_messagePanelVoiceBackground";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = NUM; // 0xd000000 float:3.9443045E-31 double:1.07757599E-315;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_messagePanelVoiceShadow";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -9211021; // 0xfffffffffvar_ float:-3.2360185E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_messagePanelVoiceDelete";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_messagePanelVoiceDuration";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -11037236; // 0xfffffffffvar_cc float:-2.865618E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inlineResultIcon";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_topPanelBackground";
        r9.put(r14, r0);
        r9 = defaultColors;
        r14 = -7563878; // 0xffffffffff8CLASSNAMEa float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_topPanelClose";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = -9658414; // 0xffffffffff6c9fd2 float:-3.1452764E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_topPanelLine";
        r9.put(r15, r14);
        r9 = defaultColors;
        r14 = "chat_topPanelTitle";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -6710887; // 0xfffffffffvar_ float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "chat_topPanelMessage";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -3188393; // 0xffffffffffcvar_ float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "chat_reportSpam";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -11894091; // 0xffffffffff4a82b5 float:-2.6918272E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "chat_addContact";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -9259544; // 0xfffffffffvar_b5e8 float:-3.2261769E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "chat_inLoader";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -10114080; // 0xfffffffffvar_abe0 float:-3.0528564E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "chat_inLoaderSelected";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -8863118; // 0xfffffffffvar_CLASSNAME float:-3.3065816E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "chat_outLoader";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -9783964; // 0xffffffffff6ab564 float:-3.1198118E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "chat_outLoaderSelected";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -6113080; // 0xffffffffffa2b8c8 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "chat_inLoaderPhoto";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -6113849; // 0xffffffffffa2b5c7 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "chat_inLoaderPhotoSelected";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -197380; // 0xfffffffffffcfcfc float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "chat_inLoaderPhotoIcon";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -1314571; // 0xffffffffffebf0f5 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "chat_inLoaderPhotoIconSelected";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -8011912; // 0xfffffffffvar_bvar_ float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "chat_outLoaderPhoto";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -8538000; // 0xffffffffff7db870 float:-3.3725234E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "chat_outLoaderPhotoSelected";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -2427453; // 0xffffffffffdaf5c3 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "chat_outLoaderPhotoIcon";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -4134748; // 0xffffffffffc0e8a4 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "chat_outLoaderPhotoIconSelected";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = NUM; // 0x66000000 float:1.5111573E23 double:8.45482698E-315;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "chat_mediaLoaderPhoto";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = NUM; // 0x7var_ float:1.7014118E38 double:1.0527088494E-314;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "chat_mediaLoaderPhotoSelected";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = "chat_mediaLoaderPhotoIcon";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -2500135; // 0xffffffffffd9d9d9 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "chat_mediaLoaderPhotoIconSelected";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -NUM; // 0xffffffffcc3e648e float:-4.9910328E7 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "chat_secretTimerBackground";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = "chat_secretTimerText";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -12937771; // 0xffffffffff3a95d5 float:-2.4801438E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "profile_creatorIcon";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -8288630; // 0xfffffffffvar_a float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "profile_actionIcon";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = "profile_actionBackground";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -855310; // 0xfffffffffff2f2f2 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "profile_actionPressedBackground";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -5056776; // 0xffffffffffb2d6f8 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "profile_verifiedBackground";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -11959368; // 0xfffffffffvar_b8 float:-2.6785875E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "profile_verifiedCheck";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = "profile_title";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -2626822; // 0xffffffffffd7eafa float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "profile_status";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = "player_actionBar";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = NUM; // 0xvar_ float:6.3108872E-30 double:1.24335691E-315;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "player_actionBarSelector";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -13683656; // 0xffffffffff2var_ float:-2.3288603E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "player_actionBarTitle";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -NUM; // 0xfffffffvar_ float:-6.617445E-24 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "player_actionBarTop";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -7697782; // 0xffffffffff8a8a8a float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "player_actionBarSubtitle";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -7697782; // 0xffffffffff8a8a8a float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "player_actionBarItems";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = "player_background";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -7564650; // 0xffffffffff8CLASSNAME float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "player_time";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -1445899; // 0xffffffffffe9eff5 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "player_progressBackground";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -1445899; // 0xffffffffffe9eff5 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "key_player_progressCachedBackground";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -11821085; // 0xffffffffff4b9fe3 float:-2.7066346E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "player_progress";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -5723992; // 0xffffffffffa8a8a8 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "player_placeholder";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -986896; // 0xfffffffffff0f0f0 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "player_placeholderBackground";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -13421773; // 0xfffffffffvar_ float:-2.3819765E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "player_button";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -11753238; // 0xffffffffff4ca8ea float:-2.7203956E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "player_buttonActive";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -1973016; // 0xffffffffffe1e4e8 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "key_sheet_scrollUp";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -3551789; // 0xffffffffffc9cdd3 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "key_sheet_other";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = "files_folderIcon";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -10637333; // 0xffffffffff5dafeb float:-2.946728E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "files_folderIconBackground";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = "files_iconText";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -6908266; // 0xfffffffffvar_ float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "sessions_devicesImage";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -12211217; // 0xfffffffffvar_abef float:-2.6275065E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "passport_authorizeBackground";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -12542501; // 0xfffffffffvar_ddb float:-2.560314E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "passport_authorizeBackgroundSelected";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = "passport_authorizeText";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -12149258; // 0xfffffffffvar_df6 float:-2.6400732E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "location_sendLocationBackground";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = "location_sendLocationIcon";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -14906664; // 0xffffffffff1c8ad8 float:-2.0808049E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "location_sendLocationText";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -11550140; // 0xffffffffff4fCLASSNAME float:-2.7615888E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "location_sendLiveLocationBackground";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = "location_sendLiveLocationIcon";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -13194460; // 0xfffffffffvar_ab24 float:-2.428081E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "location_sendLiveLocationText";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -13262875; // 0xfffffffffvar_fe5 float:-2.4142049E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "location_liveLocationProgress";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -11753238; // 0xffffffffff4ca8ea float:-2.7203956E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "location_placeLocationBackground";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -12959675; // 0xffffffffff3a4045 float:-2.4757011E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "location_actionIcon";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -12414746; // 0xfffffffffvar_e6 float:-2.5862259E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "location_actionActiveIcon";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = "location_actionBackground";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -855310; // 0xfffffffffff2f2f2 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "location_actionPressedBackground";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -13262875; // 0xfffffffffvar_fe5 float:-2.4142049E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "dialog_liveLocationProgress";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -16725933; // 0xfffffffffvar_CLASSNAME float:-1.7118133E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "calls_callReceivedGreenIcon";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -47032; // 0xfffffffffffvar_ float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "calls_callReceivedRedIcon";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -11491093; // 0xfffffffffvar_a8eb float:-2.773565E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "featuredStickers_addedIcon";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = "featuredStickers_buttonProgress";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -11491093; // 0xfffffffffvar_a8eb float:-2.773565E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "featuredStickers_addButton";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -12346402; // 0xfffffffffvar_bde float:-2.6000877E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "featuredStickers_addButtonPressed";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -11496493; // 0xfffffffffvar_d3 float:-2.7724697E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "featuredStickers_removeButtonText";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = "featuredStickers_buttonText";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -11688214; // 0xffffffffff4da6ea float:-2.733584E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "featuredStickers_unread";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -13683656; // 0xffffffffff2var_ float:-2.3288603E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "inappPlayerPerformer";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -13683656; // 0xffffffffff2var_ float:-2.3288603E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "inappPlayerTitle";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = "inappPlayerBackground";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -10309397; // 0xfffffffffvar_b0eb float:-3.0132414E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "inappPlayerPlayPause";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -5723992; // 0xffffffffffa8a8a8 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "inappPlayerClose";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -12279325; // 0xfffffffffvar_a1e3 float:-2.6136925E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "returnToCallBackground";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = "returnToCallText";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -13196562; // 0xfffffffffvar_a2ee float:-2.4276547E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "sharedMedia_startStopLoadIcon";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -986123; // 0xfffffffffff0f3f5 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "sharedMedia_linkPlaceholder";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -4735293; // 0xffffffffffb7bec3 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "sharedMedia_linkPlaceholderText";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -1182729; // 0xffffffffffedf3f7 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "sharedMedia_photoPlaceholder";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -12154957; // 0xfffffffffvar_b3 float:-2.6389173E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "sharedMedia_actionMode";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -10567099; // 0xffffffffff5eCLASSNAME float:-2.9609732E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "checkbox";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = "checkboxCheck";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -5195326; // 0xffffffffffb0b9c2 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "checkboxDisabled";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -4801083; // 0xffffffffffb6bdc5 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "stickers_menu";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = NUM; // 0xvar_ float:6.3108872E-30 double:1.24335691E-315;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "stickers_menuSelector";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -4669499; // 0xffffffffffb8bfc5 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "changephoneinfo_image";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -11491350; // 0xfffffffffvar_a7ea float:-2.7735128E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "changephoneinfo_image2";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -6182221; // 0xffffffffffa1aab3 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "groupcreate_hintText";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -11361317; // 0xfffffffffvar_a3db float:-2.7998867E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "groupcreate_cursor";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = "groupcreate_sectionShadow";
        r9.put(r12, r13);
        r9 = defaultColors;
        r12 = -8617336; // 0xffffffffff7CLASSNAME float:-3.3564321E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "groupcreate_sectionText";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = "groupcreate_spanText";
        r9.put(r12, r11);
        r9 = defaultColors;
        r12 = -855310; // 0xfffffffffff2f2f2 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "groupcreate_spanBackground";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = "groupcreate_spanDelete";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -11157919; // 0xfffffffffvar_be61 float:-2.8411407E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "contacts_inviteBackground";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = "contacts_inviteText";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -1971470; // 0xffffffffffe1eaf2 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "login_progressInner";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -10313520; // 0xfffffffffvar_a0d0 float:-3.0124051E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "login_progressOuter";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -14043401; // 0xfffffffffvar_b6f7 float:-2.2558954E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "musicPicker_checkbox";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = "musicPicker_checkboxCheck";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -10702870; // 0xffffffffff5cafea float:-2.9334356E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "musicPicker_buttonBackground";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = "musicPicker_buttonIcon";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -15095832; // 0xfffffffffvar_a7e8 float:-2.042437E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "picker_enabledButton";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -6710887; // 0xfffffffffvar_ float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "picker_disabledButton";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -14043401; // 0xfffffffffvar_b6f7 float:-2.2558954E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "picker_badge";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = "picker_badgeText";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = -12348980; // 0xfffffffffvar_cc float:-2.5995648E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "chat_botSwitchToInlineText";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -NUM; // 0xffffffffea272var_ float:-5.05284E25 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "undo_background";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = -8008961; // 0xfffffffffvar_caff float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r14 = "undo_cancelColor";
        r9.put(r14, r12);
        r9 = defaultColors;
        r12 = "undo_infoColor";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = "wallet_blackBackground";
        r9.put(r12, r13);
        r9 = defaultColors;
        r12 = -986896; // 0xfffffffffff0f0f0 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "wallet_graySettingsBackground";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = -14079703; // 0xfffffffffvar_ float:-2.2485325E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "wallet_grayBackground";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = "wallet_whiteBackground";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = NUM; // 0x40ffffff float:7.9999995 double:5.387879933E-315;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "wallet_blackBackgroundSelector";
        r9.put(r13, r12);
        r9 = defaultColors;
        r12 = "wallet_whiteText";
        r9.put(r12, r0);
        r9 = defaultColors;
        r12 = "wallet_blackText";
        r9.put(r12, r11);
        r9 = defaultColors;
        r11 = -8355712; // 0xfffffffffvar_ float:NaN double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "wallet_statusText";
        r9.put(r12, r11);
        r9 = defaultColors;
        r11 = -8947849; // 0xfffffffffvar_ float:-3.2893961E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "wallet_grayText";
        r9.put(r12, r11);
        r9 = defaultColors;
        r11 = -10066330; // 0xfffffffffvar_ float:-3.0625412E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "wallet_grayText2";
        r9.put(r12, r11);
        r9 = defaultColors;
        r11 = -13129704; // 0xfffffffffvar_a818 float:-2.4412152E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "wallet_greenText";
        r9.put(r12, r11);
        r9 = defaultColors;
        r11 = -2408384; // 0xffffffffffdb4040 float:NaN double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "wallet_redText";
        r9.put(r12, r11);
        r9 = defaultColors;
        r11 = -6710887; // 0xfffffffffvar_ float:NaN double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "wallet_dateText";
        r9.put(r12, r11);
        r9 = defaultColors;
        r11 = -6710887; // 0xfffffffffvar_ float:NaN double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "wallet_commentText";
        r9.put(r12, r11);
        r9 = defaultColors;
        r11 = -13599557; // 0xfffffffffvar_cbb float:-2.3459176E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "wallet_releaseBackground";
        r9.put(r12, r11);
        r9 = defaultColors;
        r11 = -14606047; // 0xfffffffffvar_ float:-2.1417772E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "wallet_pullBackground";
        r9.put(r12, r11);
        r9 = defaultColors;
        r11 = -12082714; // 0xfffffffffvar_a1e6 float:-2.65357E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "wallet_buttonBackground";
        r9.put(r12, r11);
        r9 = defaultColors;
        r11 = -13923114; // 0xffffffffff2b8cd6 float:-2.2802925E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "wallet_buttonPressedBackground";
        r9.put(r12, r11);
        r9 = defaultColors;
        r11 = "wallet_buttonText";
        r9.put(r11, r0);
        r0 = defaultColors;
        r9 = NUM; // 0xd000000 float:3.9443045E-31 double:1.07757599E-315;
        r9 = java.lang.Integer.valueOf(r9);
        r11 = "wallet_addressConfirmBackground";
        r0.put(r11, r9);
        r0 = defaultColors;
        r9 = NUM; // 0x2e3var_ float:4.3564385E-11 double:3.8335537E-315;
        r9 = java.lang.Integer.valueOf(r9);
        r11 = "chat_outTextSelectionHighlight";
        r0.put(r11, r9);
        r0 = defaultColors;
        r9 = NUM; // 0x5062a9e3 float:1.5211138E10 double:6.663183225E-315;
        r9 = java.lang.Integer.valueOf(r9);
        r11 = "chat_inTextSelectionHighlight";
        r0.put(r11, r9);
        r0 = defaultColors;
        r9 = -12476440; // 0xfffffffffvar_fe8 float:-2.5737128E38 double:NaN;
        r9 = java.lang.Integer.valueOf(r9);
        r11 = "chat_TextSelectionCursor";
        r0.put(r11, r9);
        r0 = fallbackKeys;
        r9 = "chat_adminText";
        r11 = "chat_inTimeText";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_adminSelectedText";
        r11 = "chat_inTimeSelectedText";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "key_player_progressCachedBackground";
        r11 = "player_progressBackground";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_inAudioCacheSeekbar";
        r11 = "chat_inAudioSeekbar";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_outAudioCacheSeekbar";
        r11 = "chat_outAudioSeekbar";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_emojiSearchBackground";
        r11 = "chat_emojiPanelStickerPackSelector";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "location_sendLiveLocationIcon";
        r11 = "location_sendLocationIcon";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "changephoneinfo_image2";
        r11 = "featuredStickers_addButton";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "key_graySectionText";
        r11 = "windowBackgroundWhiteGrayText2";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_inMediaIcon";
        r11 = "chat_inBubble";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_outMediaIcon";
        r11 = "chat_outBubble";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_inMediaIconSelected";
        r11 = "chat_inBubbleSelected";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_outMediaIconSelected";
        r11 = "chat_outBubbleSelected";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chats_actionUnreadIcon";
        r11 = "profile_actionIcon";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chats_actionUnreadBackground";
        r11 = "profile_actionBackground";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chats_actionUnreadPressedBackground";
        r11 = "profile_actionPressedBackground";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "dialog_inlineProgressBackground";
        r11 = "windowBackgroundGray";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "dialog_inlineProgress";
        r11 = "chats_menuItemIcon";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "groupcreate_spanDelete";
        r11 = "chats_actionIcon";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "sharedMedia_photoPlaceholder";
        r11 = "windowBackgroundGray";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_attachPollBackground";
        r11 = "chat_attachAudioBackground";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_attachPollIcon";
        r11 = "chat_attachAudioIcon";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chats_onlineCircle";
        r11 = "windowBackgroundWhiteBlueText";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "windowBackgroundWhiteBlueButton";
        r11 = "windowBackgroundWhiteValueText";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "windowBackgroundWhiteBlueIcon";
        r11 = "windowBackgroundWhiteValueText";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "undo_background";
        r11 = "chat_gifSaveHintBackground";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "undo_cancelColor";
        r11 = "chat_gifSaveHintText";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "undo_infoColor";
        r11 = "chat_gifSaveHintText";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "windowBackgroundUnchecked";
        r11 = "windowBackgroundWhite";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "windowBackgroundChecked";
        r11 = "windowBackgroundWhite";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "switchTrackBlue";
        r11 = "switchTrack";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "switchTrackBlueChecked";
        r11 = "switchTrackChecked";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "switchTrackBlueThumb";
        r11 = "windowBackgroundWhite";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "switchTrackBlueThumbChecked";
        r11 = "windowBackgroundWhite";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "windowBackgroundCheckText";
        r11 = "windowBackgroundWhiteBlackText";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "contextProgressInner4";
        r11 = "contextProgressInner1";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "contextProgressOuter4";
        r11 = "contextProgressOuter1";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "switchTrackBlueSelector";
        r11 = "listSelectorSDK21";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "switchTrackBlueSelectorChecked";
        r11 = "listSelectorSDK21";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_emojiBottomPanelIcon";
        r11 = "chat_emojiPanelIcon";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_emojiSearchIcon";
        r11 = "chat_emojiPanelIcon";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_emojiPanelStickerSetNameHighlight";
        r11 = "windowBackgroundWhiteBlueText4";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_emojiPanelStickerPackSelectorLine";
        r11 = "chat_emojiPanelIconSelected";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "sharedMedia_actionMode";
        r11 = "actionBarDefault";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "key_sheet_scrollUp";
        r11 = "chat_emojiPanelStickerPackSelector";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "key_sheet_other";
        r11 = "player_actionBarItems";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "dialogSearchBackground";
        r11 = "chat_emojiPanelStickerPackSelector";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "dialogSearchHint";
        r11 = "chat_emojiPanelIcon";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "dialogSearchIcon";
        r11 = "chat_emojiPanelIcon";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "dialogSearchText";
        r11 = "windowBackgroundWhiteBlackText";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "dialogFloatingButton";
        r11 = "dialogRoundCheckBox";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "dialogFloatingButtonPressed";
        r11 = "dialogRoundCheckBox";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "dialogFloatingIcon";
        r11 = "dialogRoundCheckBoxCheck";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "dialogShadowLine";
        r11 = "chat_emojiPanelShadowLine";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "actionBarDefaultArchived";
        r11 = "actionBarDefault";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "actionBarDefaultArchivedSelector";
        r11 = "actionBarDefaultSelector";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "actionBarDefaultArchivedIcon";
        r11 = "actionBarDefaultIcon";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "actionBarDefaultArchivedTitle";
        r11 = "actionBarDefaultTitle";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "actionBarDefaultArchivedSearch";
        r11 = "actionBarDefaultSearch";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "actionBarDefaultSearchArchivedPlaceholder";
        r11 = "actionBarDefaultSearchPlaceholder";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chats_message_threeLines";
        r11 = "chats_message";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chats_nameMessage_threeLines";
        r11 = "chats_nameMessage";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chats_nameArchived";
        r11 = "chats_name";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chats_nameMessageArchived";
        r11 = "chats_nameMessage";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chats_nameMessageArchived_threeLines";
        r11 = "chats_nameMessage";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chats_messageArchived";
        r11 = "chats_message";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "avatar_backgroundArchived";
        r11 = "chats_unreadCounterMuted";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chats_archiveBackground";
        r11 = "chats_actionBackground";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chats_archivePinBackground";
        r11 = "chats_unreadCounterMuted";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chats_archiveIcon";
        r11 = "chats_actionIcon";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chats_archiveText";
        r11 = "chats_actionIcon";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "actionBarDefaultSubmenuItemIcon";
        r11 = "dialogIcon";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "checkboxDisabled";
        r11 = "chats_unreadCounterMuted";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_status";
        r11 = "actionBarDefaultSubtitle";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_inDownCall";
        r11 = "calls_callReceivedGreenIcon";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_inUpCall";
        r11 = "calls_callReceivedRedIcon";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_outUpCall";
        r11 = "calls_callReceivedGreenIcon";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "actionBarTabActiveText";
        r11 = "actionBarDefaultTitle";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "actionBarTabUnactiveText";
        r11 = "actionBarDefaultSubtitle";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "actionBarTabLine";
        r11 = "actionBarDefaultTitle";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "actionBarTabSelector";
        r11 = "actionBarDefaultSelector";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "profile_status";
        r11 = "avatar_subtitleInProfileBlue";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chats_menuTopBackgroundCats";
        r11 = "avatar_backgroundActionBarBlue";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_attachPermissionImage";
        r11 = "dialogTextBlack";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_attachPermissionMark";
        r11 = "chat_sentError";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_attachPermissionText";
        r11 = "dialogTextBlack";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_attachEmptyImage";
        r11 = "emptyListPlaceholder";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "actionBarBrowser";
        r11 = "actionBarDefault";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chats_sentReadCheck";
        r11 = "chats_sentCheck";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_outSentCheckRead";
        r11 = "chat_outSentCheck";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_outSentCheckReadSelected";
        r11 = "chat_outSentCheckSelected";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chats_archivePullDownBackground";
        r11 = "chats_unreadCounterMuted";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chats_archivePullDownBackgroundActive";
        r11 = "chats_actionBackground";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "avatar_backgroundArchivedHidden";
        r11 = "avatar_backgroundSaved";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "featuredStickers_removeButtonText";
        r11 = "featuredStickers_addButtonPressed";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "dialogEmptyImage";
        r11 = "player_time";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "dialogEmptyText";
        r11 = "player_time";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "location_actionIcon";
        r11 = "dialogTextBlack";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "location_actionActiveIcon";
        r11 = "windowBackgroundWhiteBlueText7";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "location_actionBackground";
        r11 = "dialogBackground";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "location_actionPressedBackground";
        r11 = "dialogBackgroundGray";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "location_sendLocationText";
        r11 = "windowBackgroundWhiteBlueText7";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "location_sendLiveLocationText";
        r11 = "windowBackgroundWhiteGreenText";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_outTextSelectionHighlight";
        r11 = "chat_textSelectBackground";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_inTextSelectionHighlight";
        r11 = "chat_textSelectBackground";
        r0.put(r9, r11);
        r0 = fallbackKeys;
        r9 = "chat_TextSelectionCursor";
        r11 = "chat_messagePanelCursor";
        r0.put(r9, r11);
        r0 = themeAccentExclusionKeys;
        r9 = keys_avatar_background;
        r9 = java.util.Arrays.asList(r9);
        r0.addAll(r9);
        r0 = themeAccentExclusionKeys;
        r9 = keys_avatar_nameInMessage;
        r9 = java.util.Arrays.asList(r9);
        r0.addAll(r9);
        r0 = themeAccentExclusionKeys;
        r9 = "chat_attachFileBackground";
        r0.add(r9);
        r0 = themeAccentExclusionKeys;
        r9 = "chat_attachGalleryBackground";
        r0.add(r9);
        r0 = themeAccentExclusionKeys;
        r9 = "chat_shareBackground";
        r0.add(r9);
        r0 = themeAccentExclusionKeys;
        r9 = "chat_shareBackgroundSelected";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outUpCall";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outBubble";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outBubbleSelected";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outBubbleShadow";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outBubbleGradient";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outSentCheck";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outSentCheckSelected";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outSentCheckRead";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outSentCheckReadSelected";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outSentClock";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outSentClockSelected";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outMediaIcon";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outMediaIconSelected";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outViews";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outViewsSelected";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outMenu";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outMenuSelected";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outInstant";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outInstantSelected";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outPreviewInstantText";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outPreviewInstantSelectedText";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outForwardedNameText";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outViaBotNameText";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outReplyLine";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outReplyNameText";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outReplyMessageText";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outReplyMediaMessageText";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outReplyMediaMessageSelectedText";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outPreviewLine";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outSiteNameText";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outContactNameText";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outContactPhoneText";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outContactPhoneSelectedText";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outAudioProgress";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outAudioSelectedProgress";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outTimeText";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outTimeSelectedText";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outAudioPerfomerText";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outAudioPerfomerSelectedText";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outAudioTitleText";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outAudioDurationText";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outAudioDurationSelectedText";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outAudioSeekbar";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outAudioCacheSeekbar";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outAudioSeekbarSelected";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outAudioSeekbarFill";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outVoiceSeekbar";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outVoiceSeekbarSelected";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outVoiceSeekbarFill";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outFileProgress";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outFileProgressSelected";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outFileNameText";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outFileInfoText";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outFileInfoSelectedText";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outFileBackground";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outFileBackgroundSelected";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outVenueInfoText";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outVenueInfoSelectedText";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outLoader";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outLoaderSelected";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outLoaderPhoto";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outLoaderPhotoSelected";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outLoaderPhotoIcon";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outLoaderPhotoIconSelected";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outLocationBackground";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outLocationIcon";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outContactBackground";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outContactIcon";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outFileIcon";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outFileSelectedIcon";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_outBroadcast";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_messageTextOut";
        r0.add(r9);
        r0 = myMessagesColorKeys;
        r9 = "chat_messageLinkOut";
        r0.add(r9);
        r0 = new java.util.ArrayList;
        r0.<init>();
        themes = r0;
        r0 = new java.util.ArrayList;
        r0.<init>();
        otherThemes = r0;
        r0 = new java.util.HashMap;
        r0.<init>();
        themesDict = r0;
        r0 = new java.util.HashMap;
        r0.<init>();
        currentColorsNoAccent = r0;
        r0 = new java.util.HashMap;
        r0.<init>();
        currentColors = r0;
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r9 = "themeconfig";
        r9 = r0.getSharedPreferences(r9, r1);
        r0 = new org.telegram.ui.ActionBar.Theme$ThemeInfo;
        r0.<init>();
        r11 = "Blue";
        r0.name = r11;
        r11 = "bluebubbles.attheme";
        r0.assetName = r11;
        r11 = -6963476; // 0xfffffffffvar_beec float:NaN double:NaN;
        r0.previewBackgroundColor = r11;
        r0.previewInColor = r2;
        r11 = -3086593; // 0xffffffffffd0e6ff float:NaN double:NaN;
        r0.previewOutColor = r11;
        r0.firstAccentIsDefault = r5;
        r11 = DEFALT_THEME_ACCENT_ID;
        r0.currentAccentId = r11;
        r0.sortIndex = r5;
        r15 = 16;
        r12 = new int[r15];
        r12 = {-10972987, -14444461, -3252606, -8428605, -14380627, -14050257, -7842636, -13464881, -12342073, -11359164, -3317869, -2981834, -8165684, -3256745, -2904512, -8681301};
        r13 = new int[r15];
        r13 = {-4660851, -328756, -1572, -4108434, -3031781, -1335, -198952, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r14 = new int[r15];
        r14 = {0, -853047, -264993, 0, 0, -135756, -198730, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r11 = new int[r15];
        r11 = {0, -2104672, -1918575, -2637335, -2305600, -1067658, -4152623, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r2 = new int[r15];
        r2 = {0, -4071005, -1318214, -1520170, -2039866, -1251471, -2175778, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r6 = new int[r15];
        r6 = {99, 9, 10, 11, 12, 13, 14, 0, 1, 2, 3, 4, 5, 6, 7, 8};
        r10 = new java.lang.String[r15];
        r10[r1] = r3;
        r16 = "p-pXcflrmFIBAAAAvXYQk-mCwZU";
        r10[r5] = r16;
        r16 = "JqSUrO0-mFIBAAAAWwTvLzoWGQI";
        r10[r7] = r16;
        r16 = "O-wmAfBPSFADAAAA4zINVfD_bro";
        r10[r4] = r16;
        r16 = "RepJ5uE_SVABAAAAr4d0YhgB850";
        r10[r8] = r16;
        r16 = "-Xc-np9y2VMCAAAARKr0yNNPYW0";
        r17 = 5;
        r10[r17] = r16;
        r16 = "dhf9pceaQVACAAAAbzdVo4SCiZA";
        r17 = 6;
        r10[r17] = r16;
        r16 = 7;
        r10[r16] = r3;
        r16 = 8;
        r10[r16] = r3;
        r16 = 9;
        r10[r16] = r3;
        r16 = 10;
        r10[r16] = r3;
        r16 = 11;
        r10[r16] = r3;
        r16 = 12;
        r10[r16] = r3;
        r16 = 13;
        r10[r16] = r3;
        r8 = 14;
        r10[r8] = r3;
        r8 = 15;
        r10[r8] = r3;
        r8 = new int[r15];
        r8 = {0, 180, 45, 0, 45, 180, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r7 = new int[r15];
        r7 = {0, 52, 46, 57, 45, 64, 52, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r16 = r11;
        r11 = r0;
        r21 = 16;
        r15 = r16;
        r16 = r2;
        r17 = r6;
        r18 = r10;
        r19 = r8;
        r20 = r7;
        r11.setAccentColorOptions(r12, r13, r14, r15, r16, r17, r18, r19, r20);
        r2 = themes;
        defaultTheme = r0;
        currentTheme = r0;
        currentDayTheme = r0;
        r2.add(r0);
        r2 = themesDict;
        r6 = "Blue";
        r2.put(r6, r0);
        r0 = new org.telegram.ui.ActionBar.Theme$ThemeInfo;
        r0.<init>();
        r2 = "Dark Blue";
        r0.name = r2;
        r2 = "darkblue.attheme";
        r0.assetName = r2;
        r2 = -10523006; // 0xffffffffff5f6e82 float:-2.9699163E38 double:NaN;
        r0.previewBackgroundColor = r2;
        r2 = -9009508; // 0xfffffffffvar_c float:-3.2768902E38 double:NaN;
        r0.previewInColor = r2;
        r2 = -8214301; // 0xfffffffffvar_a8e3 float:NaN double:NaN;
        r0.previewOutColor = r2;
        r0.sortIndex = r4;
        r2 = 18;
        r11 = new int[r2];
        r11 = {-7177260, -9860357, -14440464, -8687151, -9848491, -14053142, -9403671, -10044691, -13203974, -12138259, -11880383, -1344335, -1142742, -6127120, -2931932, -1131212, -8417365, -13270557};
        r12 = new int[r2];
        r12 = {-6464359, -10267323, -13532789, -5413850, -11898828, -13410942, -13215889, -10914461, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r13 = new int[r2];
        r13 = {-10465880, -9937588, -14983040, -6736562, -14197445, -13534568, -13144441, -10587280, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r14 = new int[r2];
        r14 = {-14147282, -15263198, -16310753, -15724781, -15459054, -16313828, -14802903, -16645117, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r15 = new int[r2];
        r15 = {-15593453, -14277074, -15459034, -14541276, -15064812, -14932432, -15461096, -15393761, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r2 = new int[r2];
        r2 = {11, 12, 13, 14, 15, 16, 17, 18, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        r6 = 18;
        r6 = new java.lang.String[r6];
        r7 = "O-wmAfBPSFADAAAA4zINVfD_bro";
        r6[r1] = r7;
        r7 = "RepJ5uE_SVABAAAAr4d0YhgB850";
        r6[r5] = r7;
        r7 = "dk_wwlghOFACAAAAfz9xrxi6euw";
        r8 = 2;
        r6[r8] = r7;
        r7 = "9LW_RcoOSVACAAAAFTk3DTyXN-M";
        r6[r4] = r7;
        r7 = "PllZ-bf_SFAEAAAA8crRfwZiDNg";
        r8 = 4;
        r6[r8] = r7;
        r7 = "-Xc-np9y2VMCAAAARKr0yNNPYW0";
        r8 = 5;
        r6[r8] = r7;
        r7 = "kO4jyq55SFABAAAA0WEpcLfahXk";
        r8 = 6;
        r6[r8] = r7;
        r7 = 7;
        r8 = "CJNyxPMgSVAEAAAAvW9sMwCLASSNAMEcw";
        r6[r7] = r8;
        r7 = 8;
        r6[r7] = r3;
        r7 = 9;
        r6[r7] = r3;
        r7 = 10;
        r6[r7] = r3;
        r7 = 11;
        r6[r7] = r3;
        r7 = 12;
        r6[r7] = r3;
        r7 = 13;
        r6[r7] = r3;
        r7 = 14;
        r6[r7] = r3;
        r7 = 15;
        r6[r7] = r3;
        r6[r21] = r3;
        r7 = 17;
        r6[r7] = r3;
        r7 = 18;
        r7 = new int[r7];
        r7 = {225, 45, 225, 135, 45, 225, 45, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r8 = 18;
        r8 = new int[r8];
        r8 = {40, 40, 31, 50, 25, 34, 35, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r10 = r0;
        r16 = r2;
        r17 = r6;
        r18 = r7;
        r19 = r8;
        r10.setAccentColorOptions(r11, r12, r13, r14, r15, r16, r17, r18, r19);
        r2 = themes;
        r2.add(r0);
        r2 = themesDict;
        currentNightTheme = r0;
        r6 = "Dark Blue";
        r2.put(r6, r0);
        r0 = new org.telegram.ui.ActionBar.Theme$ThemeInfo;
        r0.<init>();
        r2 = "Arctic Blue";
        r0.name = r2;
        r2 = "arctic.attheme";
        r0.assetName = r2;
        r2 = -1971728; // 0xffffffffffe1e9f0 float:NaN double:NaN;
        r0.previewBackgroundColor = r2;
        r2 = -1;
        r0.previewInColor = r2;
        r2 = -9657877; // 0xffffffffff6ca1eb float:-3.1453853E38 double:NaN;
        r0.previewOutColor = r2;
        r2 = 5;
        r0.sortIndex = r2;
        r2 = 15;
        r11 = new int[r2];
        r11 = {-12537374, -12472227, -3240928, -11033621, -2194124, -3382903, -13332245, -12342073, -11359164, -3317869, -2981834, -8165684, -3256745, -2904512, -8681301};
        r12 = new int[r2];
        r12 = {-13525046, -14113959, -7579073, -13597229, -3581840, -8883763, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r13 = new int[r2];
        r13 = {-11616542, -9716647, -6400452, -12008744, -2592697, -4297041, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r14 = new int[r2];
        r14 = {-4922384, -2236758, -2437983, -1838093, -1120848, -1712148, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r15 = new int[r2];
        r15 = {-918020, -3544650, -1908290, -3610898, -1130838, -1980692, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r6 = new int[r2];
        r6 = {9, 10, 11, 12, 13, 14, 0, 1, 2, 3, 4, 5, 6, 7, 8};
        r7 = new java.lang.String[r2];
        r2 = "MIo6r0qGSFAFAAAAtL8TsDzNX60";
        r7[r1] = r2;
        r2 = "dhf9pceaQVACAAAAbzdVo4SCiZA";
        r7[r5] = r2;
        r2 = "fqv01SQemVIBAAAApND8LDRUhRU";
        r8 = 2;
        r7[r8] = r2;
        r2 = "p-pXcflrmFIBAAAAvXYQk-mCwZU";
        r7[r4] = r2;
        r2 = "JqSUrO0-mFIBAAAAWwTvLzoWGQI";
        r8 = 4;
        r7[r8] = r2;
        r2 = "F5oWoCs7QFACAAAAgf2bD_mg8Bw";
        r8 = 5;
        r7[r8] = r2;
        r2 = 6;
        r7[r2] = r3;
        r2 = 7;
        r7[r2] = r3;
        r2 = 8;
        r7[r2] = r3;
        r2 = 9;
        r7[r2] = r3;
        r2 = 10;
        r7[r2] = r3;
        r2 = 11;
        r7[r2] = r3;
        r2 = 12;
        r7[r2] = r3;
        r2 = 13;
        r7[r2] = r3;
        r2 = 14;
        r7[r2] = r3;
        r2 = 15;
        r8 = new int[r2];
        r8 = {315, 315, 225, 315, 0, 180, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r2 = new int[r2];
        r2 = {50, 50, 58, 47, 46, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r10 = r0;
        r16 = r6;
        r17 = r7;
        r18 = r8;
        r19 = r2;
        r10.setAccentColorOptions(r11, r12, r13, r14, r15, r16, r17, r18, r19);
        r2 = themes;
        r2.add(r0);
        r2 = themesDict;
        r6 = "Arctic Blue";
        r2.put(r6, r0);
        r0 = new org.telegram.ui.ActionBar.Theme$ThemeInfo;
        r0.<init>();
        r2 = "Day";
        r0.name = r2;
        r2 = "day.attheme";
        r0.assetName = r2;
        r2 = -1;
        r0.previewBackgroundColor = r2;
        r2 = -1315084; // 0xffffffffffebeef4 float:NaN double:NaN;
        r0.previewInColor = r2;
        r2 = -8604930; // 0xffffffffff7cb2fe float:-3.3589484E38 double:NaN;
        r0.previewOutColor = r2;
        r2 = 2;
        r0.sortIndex = r2;
        r2 = 14;
        r11 = new int[r2];
        r11 = {-11099447, -3379581, -3109305, -3382174, -7963438, -11759137, -11029287, -11226775, -2506945, -3382174, -3379581, -6587438, -2649788, -8681301};
        r12 = new int[r2];
        r12 = {-10125092, -9671214, -3451775, -3978678, -10711329, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r13 = new int[r2];
        r13 = {-12664362, -3642988, -2383569, -3109317, -11422261, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r14 = new int[r2];
        r14 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r15 = new int[r2];
        r15 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r6 = new int[r2];
        r6 = {9, 10, 11, 12, 13, 0, 1, 2, 3, 4, 5, 6, 7, 8};
        r7 = new java.lang.String[r2];
        r7[r1] = r3;
        r7[r5] = r3;
        r2 = 2;
        r7[r2] = r3;
        r7[r4] = r3;
        r2 = 4;
        r7[r2] = r3;
        r2 = 5;
        r7[r2] = r3;
        r2 = 6;
        r7[r2] = r3;
        r2 = 7;
        r7[r2] = r3;
        r2 = 8;
        r7[r2] = r3;
        r2 = 9;
        r7[r2] = r3;
        r2 = 10;
        r7[r2] = r3;
        r2 = 11;
        r7[r2] = r3;
        r2 = 12;
        r7[r2] = r3;
        r2 = 13;
        r7[r2] = r3;
        r2 = 14;
        r8 = new int[r2];
        r8 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r10 = new int[r2];
        r10 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r2 = r10;
        r10 = r0;
        r16 = r6;
        r17 = r7;
        r18 = r8;
        r19 = r2;
        r10.setAccentColorOptions(r11, r12, r13, r14, r15, r16, r17, r18, r19);
        r2 = themes;
        r2.add(r0);
        r2 = themesDict;
        r6 = "Day";
        r2.put(r6, r0);
        r0 = new org.telegram.ui.ActionBar.Theme$ThemeInfo;
        r0.<init>();
        r2 = "Night";
        r0.name = r2;
        r2 = "night.attheme";
        r0.assetName = r2;
        r2 = -11315623; // 0xfffffffffvar_ float:-2.8091545E38 double:NaN;
        r0.previewBackgroundColor = r2;
        r2 = -9143676; // 0xfffffffffvar_a84 float:-3.2496777E38 double:NaN;
        r0.previewInColor = r2;
        r2 = -9067802; // 0xfffffffffvar_a2e6 float:-3.2650668E38 double:NaN;
        r0.previewOutColor = r2;
        r2 = 4;
        r0.sortIndex = r2;
        r2 = 14;
        r11 = new int[r2];
        r11 = {-9781697, -7505693, -2204034, -10913816, -2375398, -12678921, -11881005, -11880383, -2534026, -1934037, -7115558, -3128522, -1528292, -8812381};
        r12 = new int[r2];
        r12 = {-7712108, -4953061, -5288081, -14258547, -9154889, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r13 = new int[r2];
        r13 = {-9939525, -5948598, -10335844, -13659747, -14054507, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r14 = new int[r2];
        r14 = {-16644350, -15658220, -16514300, -16053236, -16382457, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r15 = new int[r2];
        r15 = {-15790576, -16250871, -16448251, -15856112, -15921904, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r6 = new int[r2];
        r6 = {9, 10, 11, 12, 13, 0, 1, 2, 3, 4, 5, 6, 7, 8};
        r7 = new java.lang.String[r2];
        r2 = "YIxYGEALQVADAAAAA3QbEH0AowY";
        r7[r1] = r2;
        r2 = "9LW_RcoOSVACAAAAFTk3DTyXN-M";
        r7[r5] = r2;
        r2 = "O-wmAfBPSFADAAAA4zINVfD_bro";
        r8 = 2;
        r7[r8] = r2;
        r2 = "F5oWoCs7QFACAAAAgf2bD_mg8Bw";
        r7[r4] = r2;
        r2 = "-Xc-np9y2VMCAAAARKr0yNNPYW0";
        r8 = 4;
        r7[r8] = r2;
        r2 = 5;
        r7[r2] = r3;
        r2 = 6;
        r7[r2] = r3;
        r2 = 7;
        r7[r2] = r3;
        r2 = 8;
        r7[r2] = r3;
        r2 = 9;
        r7[r2] = r3;
        r2 = 10;
        r7[r2] = r3;
        r2 = 11;
        r7[r2] = r3;
        r2 = 12;
        r7[r2] = r3;
        r2 = 13;
        r7[r2] = r3;
        r2 = 14;
        r8 = new int[r2];
        r8 = {45, 135, 0, 180, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r2 = new int[r2];
        r2 = {34, 47, 52, 48, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        r10 = r0;
        r16 = r6;
        r17 = r7;
        r18 = r8;
        r19 = r2;
        r10.setAccentColorOptions(r11, r12, r13, r14, r15, r16, r17, r18, r19);
        r2 = themes;
        r2.add(r0);
        r2 = themesDict;
        currentNightTheme = r0;
        r6 = "Night";
        r2.put(r6, r0);
        r0 = "themes2";
        r2 = 0;
        r0 = r9.getString(r0, r2);
        r2 = 0;
    L_0x2970:
        if (r2 >= r4) goto L_0x29b7;
    L_0x2972:
        r6 = remoteThemesHash;
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "remoteThemesHash";
        r7.append(r8);
        if (r2 == 0) goto L_0x2985;
    L_0x2980:
        r8 = java.lang.Integer.valueOf(r2);
        goto L_0x2986;
    L_0x2985:
        r8 = r3;
    L_0x2986:
        r7.append(r8);
        r7 = r7.toString();
        r7 = r9.getInt(r7, r1);
        r6[r2] = r7;
        r6 = lastLoadingThemesTime;
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "lastLoadingThemesTime";
        r7.append(r8);
        if (r2 == 0) goto L_0x29a6;
    L_0x29a1:
        r8 = java.lang.Integer.valueOf(r2);
        goto L_0x29a7;
    L_0x29a6:
        r8 = r3;
    L_0x29a7:
        r7.append(r8);
        r7 = r7.toString();
        r7 = r9.getInt(r7, r1);
        r6[r2] = r7;
        r2 = r2 + 1;
        goto L_0x2970;
    L_0x29b7:
        r2 = android.text.TextUtils.isEmpty(r0);
        if (r2 != 0) goto L_0x29f1;
    L_0x29bd:
        r2 = new org.json.JSONArray;	 Catch:{ Exception -> 0x29ec }
        r2.<init>(r0);	 Catch:{ Exception -> 0x29ec }
        r0 = 0;
    L_0x29c3:
        r6 = r2.length();	 Catch:{ Exception -> 0x29ec }
        if (r0 >= r6) goto L_0x2a38;
    L_0x29c9:
        r6 = r2.getJSONObject(r0);	 Catch:{ Exception -> 0x29ec }
        r6 = org.telegram.ui.ActionBar.Theme.ThemeInfo.createWithJson(r6);	 Catch:{ Exception -> 0x29ec }
        if (r6 == 0) goto L_0x29e9;
    L_0x29d3:
        r7 = otherThemes;	 Catch:{ Exception -> 0x29ec }
        r7.add(r6);	 Catch:{ Exception -> 0x29ec }
        r7 = themes;	 Catch:{ Exception -> 0x29ec }
        r7.add(r6);	 Catch:{ Exception -> 0x29ec }
        r7 = themesDict;	 Catch:{ Exception -> 0x29ec }
        r8 = r6.getKey();	 Catch:{ Exception -> 0x29ec }
        r7.put(r8, r6);	 Catch:{ Exception -> 0x29ec }
        r6.loadWallpapers(r9);	 Catch:{ Exception -> 0x29ec }
    L_0x29e9:
        r0 = r0 + 1;
        goto L_0x29c3;
    L_0x29ec:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x2a38;
    L_0x29f1:
        r0 = 0;
        r2 = "themes";
        r0 = r9.getString(r2, r0);
        r2 = android.text.TextUtils.isEmpty(r0);
        if (r2 != 0) goto L_0x2a38;
    L_0x29ff:
        r2 = "&";
        r0 = r0.split(r2);
        r2 = 0;
    L_0x2a06:
        r6 = r0.length;
        if (r2 >= r6) goto L_0x2a27;
    L_0x2a09:
        r6 = r0[r2];
        r6 = org.telegram.ui.ActionBar.Theme.ThemeInfo.createWithString(r6);
        if (r6 == 0) goto L_0x2a24;
    L_0x2a11:
        r7 = otherThemes;
        r7.add(r6);
        r7 = themes;
        r7.add(r6);
        r7 = themesDict;
        r8 = r6.getKey();
        r7.put(r8, r6);
    L_0x2a24:
        r2 = r2 + 1;
        goto L_0x2a06;
    L_0x2a27:
        saveOtherThemes(r5, r5);
        r0 = r9.edit();
        r2 = "themes";
        r0 = r0.remove(r2);
        r0.commit();
    L_0x2a38:
        sortThemes();
        r2 = 0;
        r6 = org.telegram.messenger.MessagesController.getGlobalMainSettings();
        r0 = themesDict;	 Catch:{ Exception -> 0x2e48 }
        r7 = "Dark Blue";
        r0 = r0.get(r7);	 Catch:{ Exception -> 0x2e48 }
        r7 = r0;
        r7 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r7;	 Catch:{ Exception -> 0x2e48 }
        r0 = "theme";
        r8 = 0;
        r0 = r6.getString(r0, r8);	 Catch:{ Exception -> 0x2e48 }
        r8 = "Default";
        r8 = r8.equals(r0);	 Catch:{ Exception -> 0x2e48 }
        if (r8 == 0) goto L_0x2a72;
    L_0x2a5b:
        r0 = themesDict;	 Catch:{ Exception -> 0x2e48 }
        r8 = "Blue";
        r0 = r0.get(r8);	 Catch:{ Exception -> 0x2e48 }
        r8 = r0;
        r8 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r8;	 Catch:{ Exception -> 0x2e48 }
        r0 = DEFALT_THEME_ACCENT_ID;	 Catch:{ Exception -> 0x2a6c }
        r8.currentAccentId = r0;	 Catch:{ Exception -> 0x2a6c }
    L_0x2a6a:
        r2 = r8;
        goto L_0x2aac;
    L_0x2a6c:
        r0 = move-exception;
        r4 = r3;
        r1 = r6;
        r2 = r8;
        goto L_0x2e4b;
    L_0x2a72:
        r8 = "Dark";
        r8 = r8.equals(r0);	 Catch:{ Exception -> 0x2e48 }
        if (r8 == 0) goto L_0x2a86;
    L_0x2a7a:
        r0 = 9;
        r7.currentAccentId = r0;	 Catch:{ Exception -> 0x2a80 }
        r2 = r7;
        goto L_0x2aac;
    L_0x2a80:
        r0 = move-exception;
        r4 = r3;
        r1 = r6;
        r2 = r7;
        goto L_0x2e4b;
    L_0x2a86:
        if (r0 == 0) goto L_0x2aac;
    L_0x2a88:
        r8 = themesDict;	 Catch:{ Exception -> 0x2e48 }
        r0 = r8.get(r0);	 Catch:{ Exception -> 0x2e48 }
        r8 = r0;
        r8 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r8;	 Catch:{ Exception -> 0x2e48 }
        if (r8 == 0) goto L_0x2a6a;
    L_0x2a93:
        r0 = "lastDayTheme";
        r0 = r9.contains(r0);	 Catch:{ Exception -> 0x2a6c }
        if (r0 != 0) goto L_0x2a6a;
    L_0x2a9b:
        r0 = r9.edit();	 Catch:{ Exception -> 0x2a6c }
        r2 = "lastDayTheme";
        r10 = r8.getKey();	 Catch:{ Exception -> 0x2a6c }
        r0.putString(r2, r10);	 Catch:{ Exception -> 0x2a6c }
        r0.commit();	 Catch:{ Exception -> 0x2a6c }
        goto L_0x2a6a;
    L_0x2aac:
        r0 = "nighttheme";
        r8 = 0;
        r0 = r6.getString(r0, r8);	 Catch:{ Exception -> 0x2e48 }
        r8 = "Default";
        r8 = r8.equals(r0);	 Catch:{ Exception -> 0x2e48 }
        if (r8 == 0) goto L_0x2acc;
    L_0x2abb:
        r0 = themesDict;	 Catch:{ Exception -> 0x2e48 }
        r7 = "Blue";
        r0 = r0.get(r7);	 Catch:{ Exception -> 0x2e48 }
        r7 = r0;
        r7 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r7;	 Catch:{ Exception -> 0x2e48 }
        r0 = DEFALT_THEME_ACCENT_ID;	 Catch:{ Exception -> 0x2a80 }
        r7.currentAccentId = r0;	 Catch:{ Exception -> 0x2a80 }
        r2 = r7;
        goto L_0x2ae9;
    L_0x2acc:
        r8 = "Dark";
        r8 = r8.equals(r0);	 Catch:{ Exception -> 0x2e48 }
        if (r8 == 0) goto L_0x2adb;
    L_0x2ad4:
        currentNightTheme = r7;	 Catch:{ Exception -> 0x2e48 }
        r0 = 9;
        r7.currentAccentId = r0;	 Catch:{ Exception -> 0x2e48 }
        goto L_0x2ae9;
    L_0x2adb:
        if (r0 == 0) goto L_0x2ae9;
    L_0x2add:
        r7 = themesDict;	 Catch:{ Exception -> 0x2e48 }
        r0 = r7.get(r0);	 Catch:{ Exception -> 0x2e48 }
        r0 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r0;	 Catch:{ Exception -> 0x2e48 }
        if (r0 == 0) goto L_0x2ae9;
    L_0x2ae7:
        currentNightTheme = r0;	 Catch:{ Exception -> 0x2e48 }
    L_0x2ae9:
        r0 = currentNightTheme;	 Catch:{ Exception -> 0x2e44 }
        if (r0 == 0) goto L_0x2b07;
    L_0x2aed:
        r0 = "lastDarkTheme";
        r0 = r9.contains(r0);	 Catch:{ Exception -> 0x2e48 }
        if (r0 != 0) goto L_0x2b07;
    L_0x2af5:
        r0 = r9.edit();	 Catch:{ Exception -> 0x2e48 }
        r7 = "lastDarkTheme";
        r8 = currentNightTheme;	 Catch:{ Exception -> 0x2e48 }
        r8 = r8.getKey();	 Catch:{ Exception -> 0x2e48 }
        r0.putString(r7, r8);	 Catch:{ Exception -> 0x2e48 }
        r0.commit();	 Catch:{ Exception -> 0x2e48 }
    L_0x2b07:
        r0 = 0;
        r7 = 0;
        r8 = themesDict;	 Catch:{ Exception -> 0x2e44 }
        r8 = r8.values();	 Catch:{ Exception -> 0x2e44 }
        r8 = r8.iterator();	 Catch:{ Exception -> 0x2e44 }
        r10 = r7;
        r7 = r0;
    L_0x2b15:
        r0 = r8.hasNext();	 Catch:{ Exception -> 0x2e44 }
        if (r0 == 0) goto L_0x2da5;
    L_0x2b1b:
        r0 = r8.next();	 Catch:{ Exception -> 0x2e44 }
        r11 = r0;
        r11 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r11;	 Catch:{ Exception -> 0x2e44 }
        r0 = r11.assetName;	 Catch:{ Exception -> 0x2e44 }
        if (r0 == 0) goto L_0x2d97;
    L_0x2b26:
        r0 = r11.accentBaseColor;	 Catch:{ Exception -> 0x2e44 }
        if (r0 == 0) goto L_0x2d97;
    L_0x2b2a:
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x2e44 }
        r0.<init>();	 Catch:{ Exception -> 0x2e44 }
        r12 = "accents_";
        r0.append(r12);	 Catch:{ Exception -> 0x2e44 }
        r12 = r11.assetName;	 Catch:{ Exception -> 0x2e44 }
        r0.append(r12);	 Catch:{ Exception -> 0x2e44 }
        r0 = r0.toString();	 Catch:{ Exception -> 0x2e44 }
        r12 = 0;
        r0 = r9.getString(r0, r12);	 Catch:{ Exception -> 0x2e44 }
        r12 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x2e44 }
        r12.<init>();	 Catch:{ Exception -> 0x2e44 }
        r13 = "accent_current_";
        r12.append(r13);	 Catch:{ Exception -> 0x2e44 }
        r13 = r11.assetName;	 Catch:{ Exception -> 0x2e44 }
        r12.append(r13);	 Catch:{ Exception -> 0x2e44 }
        r12 = r12.toString();	 Catch:{ Exception -> 0x2e44 }
        r13 = r11.firstAccentIsDefault;	 Catch:{ Exception -> 0x2e44 }
        if (r13 == 0) goto L_0x2b5c;
    L_0x2b59:
        r13 = DEFALT_THEME_ACCENT_ID;	 Catch:{ Exception -> 0x2e48 }
        goto L_0x2b5d;
    L_0x2b5c:
        r13 = 0;
    L_0x2b5d:
        r12 = r9.getInt(r12, r13);	 Catch:{ Exception -> 0x2e44 }
        r11.currentAccentId = r12;	 Catch:{ Exception -> 0x2e44 }
        r12 = new java.util.ArrayList;	 Catch:{ Exception -> 0x2e44 }
        r12.<init>();	 Catch:{ Exception -> 0x2e44 }
        r13 = android.text.TextUtils.isEmpty(r0);	 Catch:{ Exception -> 0x2e44 }
        if (r13 != 0) goto L_0x2c5c;
    L_0x2b6e:
        r13 = new org.telegram.tgnet.SerializedData;	 Catch:{ all -> 0x2CLASSNAME }
        r0 = android.util.Base64.decode(r0, r4);	 Catch:{ all -> 0x2CLASSNAME }
        r13.<init>(r0);	 Catch:{ all -> 0x2CLASSNAME }
        r0 = r13.readInt32(r5);	 Catch:{ all -> 0x2CLASSNAME }
        r14 = r13.readInt32(r5);	 Catch:{ all -> 0x2CLASSNAME }
        r15 = 0;
    L_0x2b80:
        if (r15 >= r14) goto L_0x2c3e;
    L_0x2b82:
        r1 = new org.telegram.ui.ActionBar.Theme$ThemeAccent;	 Catch:{ all -> 0x2CLASSNAME }
        r1.<init>();	 Catch:{ all -> 0x2CLASSNAME }
        r4 = r13.readInt32(r5);	 Catch:{ all -> 0x2CLASSNAME }
        r1.id = r4;	 Catch:{ all -> 0x2CLASSNAME }
        r4 = r13.readInt32(r5);	 Catch:{ all -> 0x2CLASSNAME }
        r1.accentColor = r4;	 Catch:{ all -> 0x2CLASSNAME }
        r1.parentTheme = r11;	 Catch:{ all -> 0x2CLASSNAME }
        r4 = r13.readInt32(r5);	 Catch:{ all -> 0x2CLASSNAME }
        r1.myMessagesAccentColor = r4;	 Catch:{ all -> 0x2CLASSNAME }
        r4 = r13.readInt32(r5);	 Catch:{ all -> 0x2CLASSNAME }
        r1.myMessagesGradientAccentColor = r4;	 Catch:{ all -> 0x2CLASSNAME }
        r4 = 3;
        if (r0 < r4) goto L_0x2bae;
    L_0x2ba4:
        r18 = r2;
        r4 = r3;
        r2 = r13.readInt64(r5);	 Catch:{ all -> 0x2c3c }
        r1.backgroundOverrideColor = r2;	 Catch:{ all -> 0x2c3c }
        goto L_0x2bb8;
    L_0x2bae:
        r18 = r2;
        r4 = r3;
        r2 = r13.readInt32(r5);	 Catch:{ all -> 0x2c3c }
        r2 = (long) r2;	 Catch:{ all -> 0x2c3c }
        r1.backgroundOverrideColor = r2;	 Catch:{ all -> 0x2c3c }
    L_0x2bb8:
        r2 = 2;
        if (r0 < r2) goto L_0x2bc2;
    L_0x2bbb:
        r2 = r13.readInt64(r5);	 Catch:{ all -> 0x2c3c }
        r1.backgroundGradientOverrideColor = r2;	 Catch:{ all -> 0x2c3c }
        goto L_0x2bc9;
    L_0x2bc2:
        r2 = r13.readInt32(r5);	 Catch:{ all -> 0x2c3c }
        r2 = (long) r2;	 Catch:{ all -> 0x2c3c }
        r1.backgroundGradientOverrideColor = r2;	 Catch:{ all -> 0x2c3c }
    L_0x2bc9:
        if (r0 < r5) goto L_0x2bd1;
    L_0x2bcb:
        r2 = r13.readInt32(r5);	 Catch:{ all -> 0x2c3c }
        r1.backgroundRotation = r2;	 Catch:{ all -> 0x2c3c }
    L_0x2bd1:
        r2 = 4;
        if (r0 < r2) goto L_0x2bed;
    L_0x2bd4:
        r13.readInt64(r5);	 Catch:{ all -> 0x2c3c }
        r2 = r13.readDouble(r5);	 Catch:{ all -> 0x2c3c }
        r2 = (float) r2;	 Catch:{ all -> 0x2c3c }
        r1.patternIntensity = r2;	 Catch:{ all -> 0x2c3c }
        r2 = r13.readBool(r5);	 Catch:{ all -> 0x2c3c }
        r1.patternMotion = r2;	 Catch:{ all -> 0x2c3c }
        r2 = 5;
        if (r0 < r2) goto L_0x2bee;
    L_0x2be7:
        r2 = r13.readString(r5);	 Catch:{ all -> 0x2c3c }
        r1.patternSlug = r2;	 Catch:{ all -> 0x2c3c }
    L_0x2bed:
        r2 = 5;
    L_0x2bee:
        if (r0 < r2) goto L_0x2CLASSNAME;
    L_0x2bf0:
        r2 = r13.readBool(r5);	 Catch:{ all -> 0x2c3c }
        if (r2 == 0) goto L_0x2CLASSNAME;
    L_0x2bf6:
        r2 = r13.readInt32(r5);	 Catch:{ all -> 0x2c3c }
        r1.account = r2;	 Catch:{ all -> 0x2c3c }
        r2 = r13.readInt32(r5);	 Catch:{ all -> 0x2c3c }
        r2 = org.telegram.tgnet.TLRPC.Theme.TLdeserialize(r13, r2, r5);	 Catch:{ all -> 0x2c3c }
        r2 = (org.telegram.tgnet.TLRPC.TL_theme) r2;	 Catch:{ all -> 0x2c3c }
        r1.info = r2;	 Catch:{ all -> 0x2c3c }
    L_0x2CLASSNAME:
        r2 = r11.themeAccentsMap;	 Catch:{ all -> 0x2c3c }
        r3 = r1.id;	 Catch:{ all -> 0x2c3c }
        r2.put(r3, r1);	 Catch:{ all -> 0x2c3c }
        r2 = r1.info;	 Catch:{ all -> 0x2c3c }
        if (r2 == 0) goto L_0x2c1f;
    L_0x2CLASSNAME:
        r2 = r11.accentsByThemeId;	 Catch:{ all -> 0x2c3c }
        r3 = r1.info;	 Catch:{ all -> 0x2c3c }
        r20 = r6;
        r5 = r3.id;	 Catch:{ all -> 0x2c3a }
        r2.put(r5, r1);	 Catch:{ all -> 0x2c3a }
        goto L_0x2CLASSNAME;
    L_0x2c1f:
        r20 = r6;
    L_0x2CLASSNAME:
        r12.add(r1);	 Catch:{ all -> 0x2c3a }
        r2 = r11.lastAccentId;	 Catch:{ all -> 0x2c3a }
        r1 = r1.id;	 Catch:{ all -> 0x2c3a }
        r1 = java.lang.Math.max(r2, r1);	 Catch:{ all -> 0x2c3a }
        r11.lastAccentId = r1;	 Catch:{ all -> 0x2c3a }
        r15 = r15 + 1;
        r3 = r4;
        r2 = r18;
        r6 = r20;
        r1 = 0;
        r4 = 3;
        r5 = 1;
        goto L_0x2b80;
    L_0x2c3a:
        r0 = move-exception;
        goto L_0x2c4a;
    L_0x2c3c:
        r0 = move-exception;
        goto L_0x2CLASSNAME;
    L_0x2c3e:
        r18 = r2;
        r4 = r3;
        r1 = r6;
        r3 = 5;
        goto L_0x2CLASSNAME;
    L_0x2CLASSNAME:
        r0 = move-exception;
        r18 = r2;
        r4 = r3;
    L_0x2CLASSNAME:
        r20 = r6;
    L_0x2c4a:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x2CLASSNAME }
        r1 = r20;
    L_0x2c4f:
        r3 = 5;
        r5 = 1;
    L_0x2CLASSNAME:
        r6 = 3;
        goto L_0x2d57;
    L_0x2CLASSNAME:
        r0 = move-exception;
        r2 = r18;
        r1 = r20;
    L_0x2CLASSNAME:
        r5 = 1;
        goto L_0x2e4b;
    L_0x2c5c:
        r18 = r2;
        r4 = r3;
        r20 = r6;
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x2d91 }
        r0.<init>();	 Catch:{ Exception -> 0x2d91 }
        r1 = "accent_for_";
        r0.append(r1);	 Catch:{ Exception -> 0x2d91 }
        r1 = r11.assetName;	 Catch:{ Exception -> 0x2d91 }
        r0.append(r1);	 Catch:{ Exception -> 0x2d91 }
        r0 = r0.toString();	 Catch:{ Exception -> 0x2d91 }
        r1 = r20;
        r2 = 0;
        r3 = r1.getInt(r0, r2);	 Catch:{ Exception -> 0x2d8f }
        if (r3 == 0) goto L_0x2c4f;
    L_0x2c7d:
        if (r7 != 0) goto L_0x2c8d;
    L_0x2c7f:
        r7 = r1.edit();	 Catch:{ Exception -> 0x2CLASSNAME }
        r2 = r9.edit();	 Catch:{ Exception -> 0x2CLASSNAME }
        r10 = r2;
        goto L_0x2c8d;
    L_0x2CLASSNAME:
        r0 = move-exception;
        r2 = r18;
        goto L_0x2CLASSNAME;
    L_0x2c8d:
        r7.remove(r0);	 Catch:{ Exception -> 0x2d8f }
        r0 = r11.themeAccents;	 Catch:{ Exception -> 0x2d8f }
        r0 = r0.size();	 Catch:{ Exception -> 0x2d8f }
        r2 = 0;
    L_0x2CLASSNAME:
        if (r2 >= r0) goto L_0x2cae;
    L_0x2CLASSNAME:
        r5 = r11.themeAccents;	 Catch:{ Exception -> 0x2CLASSNAME }
        r5 = r5.get(r2);	 Catch:{ Exception -> 0x2CLASSNAME }
        r5 = (org.telegram.ui.ActionBar.Theme.ThemeAccent) r5;	 Catch:{ Exception -> 0x2CLASSNAME }
        r6 = r5.accentColor;	 Catch:{ Exception -> 0x2CLASSNAME }
        if (r6 != r3) goto L_0x2cab;
    L_0x2ca5:
        r0 = r5.id;	 Catch:{ Exception -> 0x2CLASSNAME }
        r11.currentAccentId = r0;	 Catch:{ Exception -> 0x2CLASSNAME }
        r0 = 1;
        goto L_0x2caf;
    L_0x2cab:
        r2 = r2 + 1;
        goto L_0x2CLASSNAME;
    L_0x2cae:
        r0 = 0;
    L_0x2caf:
        if (r0 != 0) goto L_0x2d3c;
    L_0x2cb1:
        r0 = new org.telegram.ui.ActionBar.Theme$ThemeAccent;	 Catch:{ Exception -> 0x2d8f }
        r0.<init>();	 Catch:{ Exception -> 0x2d8f }
        r2 = 100;
        r0.id = r2;	 Catch:{ Exception -> 0x2d8f }
        r0.accentColor = r3;	 Catch:{ Exception -> 0x2d8f }
        r0.parentTheme = r11;	 Catch:{ Exception -> 0x2d8f }
        r2 = r11.themeAccentsMap;	 Catch:{ Exception -> 0x2d8f }
        r3 = r0.id;	 Catch:{ Exception -> 0x2d8f }
        r2.put(r3, r0);	 Catch:{ Exception -> 0x2d8f }
        r2 = 0;
        r12.add(r2, r0);	 Catch:{ Exception -> 0x2d8f }
        r2 = 100;
        r11.currentAccentId = r2;	 Catch:{ Exception -> 0x2d8f }
        r2 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        r11.lastAccentId = r2;	 Catch:{ Exception -> 0x2d8f }
        r2 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x2d8f }
        r3 = 68;
        r2.<init>(r3);	 Catch:{ Exception -> 0x2d8f }
        r3 = 5;
        r2.writeInt32(r3);	 Catch:{ Exception -> 0x2d8f }
        r5 = 1;
        r2.writeInt32(r5);	 Catch:{ Exception -> 0x2e40 }
        r6 = r0.id;	 Catch:{ Exception -> 0x2e40 }
        r2.writeInt32(r6);	 Catch:{ Exception -> 0x2e40 }
        r6 = r0.accentColor;	 Catch:{ Exception -> 0x2e40 }
        r2.writeInt32(r6);	 Catch:{ Exception -> 0x2e40 }
        r6 = r0.myMessagesAccentColor;	 Catch:{ Exception -> 0x2e40 }
        r2.writeInt32(r6);	 Catch:{ Exception -> 0x2e40 }
        r6 = r0.myMessagesGradientAccentColor;	 Catch:{ Exception -> 0x2e40 }
        r2.writeInt32(r6);	 Catch:{ Exception -> 0x2e40 }
        r13 = r0.backgroundOverrideColor;	 Catch:{ Exception -> 0x2e40 }
        r2.writeInt64(r13);	 Catch:{ Exception -> 0x2e40 }
        r13 = r0.backgroundGradientOverrideColor;	 Catch:{ Exception -> 0x2e40 }
        r2.writeInt64(r13);	 Catch:{ Exception -> 0x2e40 }
        r6 = r0.backgroundRotation;	 Catch:{ Exception -> 0x2e40 }
        r2.writeInt32(r6);	 Catch:{ Exception -> 0x2e40 }
        r13 = 0;
        r2.writeInt64(r13);	 Catch:{ Exception -> 0x2e40 }
        r6 = r0.patternIntensity;	 Catch:{ Exception -> 0x2e40 }
        r13 = (double) r6;	 Catch:{ Exception -> 0x2e40 }
        r2.writeDouble(r13);	 Catch:{ Exception -> 0x2e40 }
        r6 = r0.patternMotion;	 Catch:{ Exception -> 0x2e40 }
        r2.writeBool(r6);	 Catch:{ Exception -> 0x2e40 }
        r0 = r0.patternSlug;	 Catch:{ Exception -> 0x2e40 }
        r2.writeString(r0);	 Catch:{ Exception -> 0x2e40 }
        r6 = 0;
        r2.writeBool(r6);	 Catch:{ Exception -> 0x2e40 }
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x2e40 }
        r0.<init>();	 Catch:{ Exception -> 0x2e40 }
        r6 = "accents_";
        r0.append(r6);	 Catch:{ Exception -> 0x2e40 }
        r6 = r11.assetName;	 Catch:{ Exception -> 0x2e40 }
        r0.append(r6);	 Catch:{ Exception -> 0x2e40 }
        r0 = r0.toString();	 Catch:{ Exception -> 0x2e40 }
        r2 = r2.toByteArray();	 Catch:{ Exception -> 0x2e40 }
        r6 = 3;
        r2 = android.util.Base64.encodeToString(r2, r6);	 Catch:{ Exception -> 0x2e40 }
        r10.putString(r0, r2);	 Catch:{ Exception -> 0x2e40 }
        goto L_0x2d3f;
    L_0x2d3c:
        r3 = 5;
        r5 = 1;
        r6 = 3;
    L_0x2d3f:
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x2e40 }
        r0.<init>();	 Catch:{ Exception -> 0x2e40 }
        r2 = "accent_current_";
        r0.append(r2);	 Catch:{ Exception -> 0x2e40 }
        r2 = r11.assetName;	 Catch:{ Exception -> 0x2e40 }
        r0.append(r2);	 Catch:{ Exception -> 0x2e40 }
        r0 = r0.toString();	 Catch:{ Exception -> 0x2e40 }
        r2 = r11.currentAccentId;	 Catch:{ Exception -> 0x2e40 }
        r10.putInt(r0, r2);	 Catch:{ Exception -> 0x2e40 }
    L_0x2d57:
        r0 = r12.isEmpty();	 Catch:{ Exception -> 0x2e40 }
        if (r0 != 0) goto L_0x2d68;
    L_0x2d5d:
        r0 = org.telegram.ui.ActionBar.-$$Lambda$Theme$-1eio9W5h8f4eCuCQ0q4O3hfyjg.INSTANCE;	 Catch:{ Exception -> 0x2e40 }
        java.util.Collections.sort(r12, r0);	 Catch:{ Exception -> 0x2e40 }
        r0 = r11.themeAccents;	 Catch:{ Exception -> 0x2e40 }
        r2 = 0;
        r0.addAll(r2, r12);	 Catch:{ Exception -> 0x2e40 }
    L_0x2d68:
        r0 = r11.themeAccentsMap;	 Catch:{ Exception -> 0x2e40 }
        if (r0 == 0) goto L_0x2d80;
    L_0x2d6c:
        r0 = r11.themeAccentsMap;	 Catch:{ Exception -> 0x2e40 }
        r2 = r11.currentAccentId;	 Catch:{ Exception -> 0x2e40 }
        r0 = r0.get(r2);	 Catch:{ Exception -> 0x2e40 }
        if (r0 != 0) goto L_0x2d80;
    L_0x2d76:
        r0 = r11.firstAccentIsDefault;	 Catch:{ Exception -> 0x2e40 }
        if (r0 == 0) goto L_0x2d7d;
    L_0x2d7a:
        r0 = DEFALT_THEME_ACCENT_ID;	 Catch:{ Exception -> 0x2e40 }
        goto L_0x2d7e;
    L_0x2d7d:
        r0 = 0;
    L_0x2d7e:
        r11.currentAccentId = r0;	 Catch:{ Exception -> 0x2e40 }
    L_0x2d80:
        r11.loadWallpapers(r9);	 Catch:{ Exception -> 0x2e40 }
        r2 = 0;
        r0 = r11.getAccent(r2);	 Catch:{ Exception -> 0x2e40 }
        if (r0 == 0) goto L_0x2d9d;
    L_0x2d8a:
        r0 = r0.overrideWallpaper;	 Catch:{ Exception -> 0x2e40 }
        r11.overrideWallpaper = r0;	 Catch:{ Exception -> 0x2e40 }
        goto L_0x2d9d;
    L_0x2d8f:
        r0 = move-exception;
        goto L_0x2d94;
    L_0x2d91:
        r0 = move-exception;
        r1 = r20;
    L_0x2d94:
        r5 = 1;
        goto L_0x2e41;
    L_0x2d97:
        r18 = r2;
        r4 = r3;
        r1 = r6;
        r3 = 5;
        r6 = 3;
    L_0x2d9d:
        r6 = r1;
        r3 = r4;
        r2 = r18;
        r1 = 0;
        r4 = 3;
        goto L_0x2b15;
    L_0x2da5:
        r18 = r2;
        r4 = r3;
        r1 = r6;
        if (r7 == 0) goto L_0x2db1;
    L_0x2dab:
        r7.commit();	 Catch:{ Exception -> 0x2e40 }
        r10.commit();	 Catch:{ Exception -> 0x2e40 }
    L_0x2db1:
        r0 = "selectedAutoNightType";
        r2 = 0;
        r0 = r1.getInt(r0, r2);	 Catch:{ Exception -> 0x2e40 }
        selectedAutoNightType = r0;	 Catch:{ Exception -> 0x2e40 }
        r0 = "autoNightScheduleByLocation";
        r0 = r1.getBoolean(r0, r2);	 Catch:{ Exception -> 0x2e40 }
        autoNightScheduleByLocation = r0;	 Catch:{ Exception -> 0x2e40 }
        r0 = "autoNightBrighnessThreshold";
        r2 = NUM; // 0x3e800000 float:0.25 double:5.180653787E-315;
        r0 = r1.getFloat(r0, r2);	 Catch:{ Exception -> 0x2e40 }
        autoNightBrighnessThreshold = r0;	 Catch:{ Exception -> 0x2e40 }
        r0 = "autoNightDayStartTime";
        r2 = 1320; // 0x528 float:1.85E-42 double:6.52E-321;
        r0 = r1.getInt(r0, r2);	 Catch:{ Exception -> 0x2e40 }
        autoNightDayStartTime = r0;	 Catch:{ Exception -> 0x2e40 }
        r0 = "autoNightDayEndTime";
        r2 = 480; // 0x1e0 float:6.73E-43 double:2.37E-321;
        r0 = r1.getInt(r0, r2);	 Catch:{ Exception -> 0x2e40 }
        autoNightDayEndTime = r0;	 Catch:{ Exception -> 0x2e40 }
        r0 = "autoNightSunsetTime";
        r2 = 1320; // 0x528 float:1.85E-42 double:6.52E-321;
        r0 = r1.getInt(r0, r2);	 Catch:{ Exception -> 0x2e40 }
        autoNightSunsetTime = r0;	 Catch:{ Exception -> 0x2e40 }
        r0 = "autoNightSunriseTime";
        r2 = 480; // 0x1e0 float:6.73E-43 double:2.37E-321;
        r0 = r1.getInt(r0, r2);	 Catch:{ Exception -> 0x2e40 }
        autoNightSunriseTime = r0;	 Catch:{ Exception -> 0x2e40 }
        r0 = "autoNightCityName";
        r0 = r1.getString(r0, r4);	 Catch:{ Exception -> 0x2e40 }
        autoNightCityName = r0;	 Catch:{ Exception -> 0x2e40 }
        r0 = "autoNightLocationLatitude3";
        r2 = 10000; // 0x2710 float:1.4013E-41 double:4.9407E-320;
        r2 = r1.getLong(r0, r2);	 Catch:{ Exception -> 0x2e40 }
        r6 = 10000; // 0x2710 float:1.4013E-41 double:4.9407E-320;
        r0 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
        if (r0 == 0) goto L_0x2e11;
    L_0x2e0a:
        r2 = java.lang.Double.longBitsToDouble(r2);	 Catch:{ Exception -> 0x2e40 }
        autoNightLocationLatitude = r2;	 Catch:{ Exception -> 0x2e40 }
        goto L_0x2e18;
    L_0x2e11:
        r2 = NUM; // 0x40cNUM float:0.0 double:10000.0;
        autoNightLocationLatitude = r2;	 Catch:{ Exception -> 0x2e40 }
    L_0x2e18:
        r0 = "autoNightLocationLongitude3";
        r2 = 10000; // 0x2710 float:1.4013E-41 double:4.9407E-320;
        r2 = r1.getLong(r0, r2);	 Catch:{ Exception -> 0x2e40 }
        r6 = 10000; // 0x2710 float:1.4013E-41 double:4.9407E-320;
        r0 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
        if (r0 == 0) goto L_0x2e2d;
    L_0x2e26:
        r2 = java.lang.Double.longBitsToDouble(r2);	 Catch:{ Exception -> 0x2e40 }
        autoNightLocationLongitude = r2;	 Catch:{ Exception -> 0x2e40 }
        goto L_0x2e34;
    L_0x2e2d:
        r2 = NUM; // 0x40cNUM float:0.0 double:10000.0;
        autoNightLocationLongitude = r2;	 Catch:{ Exception -> 0x2e40 }
    L_0x2e34:
        r0 = "autoNightLastSunCheckDay";
        r2 = -1;
        r0 = r1.getInt(r0, r2);	 Catch:{ Exception -> 0x2e40 }
        autoNightLastSunCheckDay = r0;	 Catch:{ Exception -> 0x2e40 }
        r2 = r18;
        goto L_0x2e4e;
    L_0x2e40:
        r0 = move-exception;
    L_0x2e41:
        r2 = r18;
        goto L_0x2e4b;
    L_0x2e44:
        r0 = move-exception;
        r18 = r2;
        goto L_0x2e49;
    L_0x2e48:
        r0 = move-exception;
    L_0x2e49:
        r4 = r3;
        r1 = r6;
    L_0x2e4b:
        org.telegram.messenger.FileLog.e(r0);
    L_0x2e4e:
        if (r2 != 0) goto L_0x2e53;
    L_0x2e50:
        r2 = defaultTheme;
        goto L_0x2e55;
    L_0x2e53:
        currentDayTheme = r2;
    L_0x2e55:
        r0 = "overrideThemeWallpaper";
        r0 = r1.contains(r0);
        if (r0 != 0) goto L_0x2e65;
    L_0x2e5d:
        r0 = "selectedBackground2";
        r0 = r1.contains(r0);
        if (r0 == 0) goto L_0x2f0f;
    L_0x2e65:
        r0 = "overrideThemeWallpaper";
        r3 = 0;
        r0 = r1.getBoolean(r0, r3);
        r6 = 1000001; // 0xvar_ float:1.4013E-39 double:4.94066E-318;
        r3 = "selectedBackground2";
        r6 = r1.getLong(r3, r6);
        r8 = -2;
        r3 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r3 == 0) goto L_0x2efc;
    L_0x2e7b:
        if (r0 != 0) goto L_0x2e84;
    L_0x2e7d:
        r8 = 1000001; // 0xvar_ float:1.4013E-39 double:4.94066E-318;
        r0 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r0 == 0) goto L_0x2efc;
    L_0x2e84:
        r0 = new org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo;
        r0.<init>();
        r3 = "selectedColor";
        r8 = 0;
        r3 = r1.getInt(r3, r8);
        r0.color = r3;
        r3 = "selectedBackgroundSlug";
        r3 = r1.getString(r3, r4);
        r0.slug = r3;
        r8 = -100;
        r3 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r3 < 0) goto L_0x2eb7;
    L_0x2ea0:
        r8 = -1;
        r3 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r3 > 0) goto L_0x2eb7;
    L_0x2ea6:
        r3 = r0.slug;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 == 0) goto L_0x2eb7;
    L_0x2eae:
        r3 = "c";
        r0.slug = r3;
        r0.fileName = r4;
        r0.originalFileName = r4;
        goto L_0x2ec1;
    L_0x2eb7:
        r3 = "wallpaper.jpg";
        r0.fileName = r3;
        r3 = "wallpaper_original.jpg";
        r0.originalFileName = r3;
    L_0x2ec1:
        r3 = "selectedGradientColor";
        r4 = 0;
        r3 = r1.getInt(r3, r4);
        r0.gradientColor = r3;
        r3 = 45;
        r6 = "selectedGradientRotation";
        r3 = r1.getInt(r6, r3);
        r0.rotation = r3;
        r3 = "selectedBackgroundBlurred";
        r3 = r1.getBoolean(r3, r4);
        r0.isBlurred = r3;
        r3 = "selectedBackgroundMotion";
        r3 = r1.getBoolean(r3, r4);
        r0.isMotion = r3;
        r3 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
        r4 = "selectedIntensity";
        r3 = r1.getFloat(r4, r3);
        r0.intensity = r3;
        r3 = currentDayTheme;
        r3.setOverrideWallpaper(r0);
        r3 = selectedAutoNightType;
        if (r3 == 0) goto L_0x2efc;
    L_0x2ef7:
        r3 = currentNightTheme;
        r3.setOverrideWallpaper(r0);
    L_0x2efc:
        r0 = r1.edit();
        r1 = "overrideThemeWallpaper";
        r0 = r0.remove(r1);
        r1 = "selectedBackground2";
        r0 = r0.remove(r1);
        r0.commit();
    L_0x2f0f:
        r0 = needSwitchToTheme();
        r1 = 2;
        if (r0 != r1) goto L_0x2var_;
    L_0x2var_:
        r2 = currentNightTheme;
    L_0x2var_:
        if (r0 != r1) goto L_0x2f1c;
    L_0x2f1a:
        r1 = 0;
        goto L_0x2f1e;
    L_0x2f1c:
        r1 = 0;
        r5 = 0;
    L_0x2f1e:
        applyTheme(r2, r1, r1, r5);
        r0 = org.telegram.ui.ActionBar.-$$Lambda$RQB0Jwr1FTqp6hrbGUHuOs-9k1I.INSTANCE;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        r0 = new org.telegram.ui.ActionBar.Theme$7;
        r0.<init>();
        ambientSensorListener = r0;
        return;
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
        Editor edit = MessagesController.getGlobalMainSettings().edit();
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
        String str = "nighttheme";
        if (themeInfo != null) {
            edit.putString(str, themeInfo.getKey());
        } else {
            edit.remove(str);
        }
        edit.commit();
    }

    @SuppressLint({"PrivateApi"})
    private static Drawable getStateDrawable(Drawable drawable, int i) {
        if (VERSION.SDK_INT >= 29 && (drawable instanceof StateListDrawable)) {
            return ((StateListDrawable) drawable).getStateDrawable(i);
        }
        if (StateListDrawable_getStateDrawableMethod == null) {
            try {
                StateListDrawable_getStateDrawableMethod = StateListDrawable.class.getDeclaredMethod("getStateDrawable", new Class[]{Integer.TYPE});
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
            mutate.setColorFilter(new PorterDuffColorFilter(i2, Mode.MULTIPLY));
        }
        Drawable mutate2 = resources.getDrawable(i).mutate();
        if (i3 != 0) {
            mutate2.setColorFilter(new PorterDuffColorFilter(i3, Mode.MULTIPLY));
        }
        AnonymousClass3 anonymousClass3 = new StateListDrawable() {
            public boolean selectDrawable(int i) {
                if (VERSION.SDK_INT >= 21) {
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
        anonymousClass3.setEnterFadeDuration(1);
        anonymousClass3.setExitFadeDuration(200);
        anonymousClass3.addState(new int[]{16842913}, mutate2);
        anonymousClass3.addState(new int[0], mutate);
        return anonymousClass3;
    }

    public static Drawable createEditTextDrawable(Context context, boolean z) {
        Resources resources = context.getResources();
        Drawable mutate = resources.getDrawable(NUM).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(getColor(z ? "dialogInputField" : "windowBackgroundWhiteInputField"), Mode.MULTIPLY));
        Drawable mutate2 = resources.getDrawable(NUM).mutate();
        mutate2.setColorFilter(new PorterDuffColorFilter(getColor(z ? "dialogInputFieldActivated" : "windowBackgroundWhiteInputFieldActivated"), Mode.MULTIPLY));
        AnonymousClass4 anonymousClass4 = new StateListDrawable() {
            public boolean selectDrawable(int i) {
                if (VERSION.SDK_INT >= 21) {
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
        anonymousClass4.addState(new int[]{16842910, 16842908}, mutate2);
        anonymousClass4.addState(new int[]{16842908}, mutate2);
        anonymousClass4.addState(StateSet.WILD_CARD, mutate);
        return anonymousClass4;
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
        return ((i != 11 || i2 < 24 || i2 > 31) && !(i == 0 && i2 == 1)) ? -1 : 0;
    }

    /* JADX WARNING: Missing block: B:18:0x0059, code skipped:
            if (r2 <= 31) goto L_0x005f;
     */
    /* JADX WARNING: Missing block: B:20:0x005d, code skipped:
            if (r2 == 1) goto L_0x005f;
     */
    /* JADX WARNING: Missing block: B:21:0x005f, code skipped:
            dialogs_holidayDrawable = org.telegram.messenger.ApplicationLoader.applicationContext.getResources().getDrawable(NUM);
            dialogs_holidayDrawableOffsetX = -org.telegram.messenger.AndroidUtilities.dp(3.0f);
            dialogs_holidayDrawableOffsetY = -org.telegram.messenger.AndroidUtilities.dp(1.0f);
     */
    public static android.graphics.drawable.Drawable getCurrentHolidayDrawable() {
        /*
        r0 = java.lang.System.currentTimeMillis();
        r2 = lastHolidayCheckTime;
        r0 = r0 - r2;
        r2 = 60000; // 0xea60 float:8.4078E-41 double:2.9644E-319;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 < 0) goto L_0x0080;
    L_0x000e:
        r0 = java.lang.System.currentTimeMillis();
        lastHolidayCheckTime = r0;
        r0 = java.util.Calendar.getInstance();
        r1 = java.lang.System.currentTimeMillis();
        r0.setTimeInMillis(r1);
        r1 = 2;
        r1 = r0.get(r1);
        r2 = 5;
        r2 = r0.get(r2);
        r3 = 12;
        r3 = r0.get(r3);
        r4 = 11;
        r0 = r0.get(r4);
        r5 = 1;
        if (r1 != 0) goto L_0x0043;
    L_0x0038:
        if (r2 != r5) goto L_0x0043;
    L_0x003a:
        r6 = 10;
        if (r3 > r6) goto L_0x0043;
    L_0x003e:
        if (r0 != 0) goto L_0x0043;
    L_0x0040:
        canStartHolidayAnimation = r5;
        goto L_0x0046;
    L_0x0043:
        r0 = 0;
        canStartHolidayAnimation = r0;
    L_0x0046:
        r0 = dialogs_holidayDrawable;
        if (r0 != 0) goto L_0x0080;
    L_0x004a:
        if (r1 != r4) goto L_0x005b;
    L_0x004c:
        r0 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION;
        r3 = 31;
        if (r0 == 0) goto L_0x0055;
    L_0x0052:
        r0 = 29;
        goto L_0x0057;
    L_0x0055:
        r0 = 31;
    L_0x0057:
        if (r2 < r0) goto L_0x005b;
    L_0x0059:
        if (r2 <= r3) goto L_0x005f;
    L_0x005b:
        if (r1 != 0) goto L_0x0080;
    L_0x005d:
        if (r2 != r5) goto L_0x0080;
    L_0x005f:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r0 = r0.getResources();
        r1 = NUM; // 0x7var_ float:1.7945657E38 double:1.052935766E-314;
        r0 = r0.getDrawable(r1);
        dialogs_holidayDrawable = r0;
        r0 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r0 = -r0;
        dialogs_holidayDrawableOffsetX = r0;
        r0 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r0 = -r0;
        dialogs_holidayDrawableOffsetY = r0;
    L_0x0080:
        r0 = dialogs_holidayDrawable;
        return r0;
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
            mutate.setColorFilter(new PorterDuffColorFilter(i2, Mode.MULTIPLY));
        }
        Drawable mutate2 = resources.getDrawable(i).mutate();
        if (i3 != 0) {
            mutate2.setColorFilter(new PorterDuffColorFilter(i3, Mode.MULTIPLY));
        }
        AnonymousClass5 anonymousClass5 = new StateListDrawable() {
            public boolean selectDrawable(int i) {
                if (VERSION.SDK_INT >= 21) {
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
        anonymousClass5.addState(new int[]{16842919}, mutate2);
        anonymousClass5.addState(new int[]{16842913}, mutate2);
        anonymousClass5.addState(StateSet.WILD_CARD, mutate);
        return anonymousClass5;
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
            paint.setStyle(Style.STROKE);
            paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        } else if (i2 == 2) {
            paint.setAlpha(0);
        }
        CombinedDrawable combinedDrawable = new CombinedDrawable(shapeDrawable, drawable);
        combinedDrawable.setCustomSize(i, i);
        return combinedDrawable;
    }

    public static Drawable createRoundRectDrawableWithIcon(int i, int i2) {
        r2 = new float[8];
        float f = (float) i;
        r2[0] = f;
        r2[1] = f;
        r2[2] = f;
        r2[3] = f;
        r2[4] = f;
        r2[5] = f;
        r2[6] = f;
        r2[7] = f;
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(r2, null, null));
        shapeDrawable.getPaint().setColor(-1);
        return new CombinedDrawable(shapeDrawable, ApplicationLoader.applicationContext.getResources().getDrawable(i2).mutate());
    }

    public static void setCombinedDrawableColor(Drawable drawable, int i, boolean z) {
        if (drawable instanceof CombinedDrawable) {
            if (z) {
                drawable = ((CombinedDrawable) drawable).getIcon();
            } else {
                drawable = ((CombinedDrawable) drawable).getBackground();
            }
            if (drawable instanceof ColorDrawable) {
                ((ColorDrawable) drawable).setColor(i);
            } else {
                drawable.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
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
        if (VERSION.SDK_INT >= 21) {
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
        r2 = new float[8];
        float f = (float) i;
        r2[0] = f;
        r2[1] = f;
        r2[2] = f;
        r2[3] = f;
        r2[4] = f;
        r2[5] = f;
        r2[6] = f;
        r2[7] = f;
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(r2, null, null));
        shapeDrawable.getPaint().setColor(i2);
        return shapeDrawable;
    }

    public static Drawable createSimpleSelectorRoundRectDrawable(int i, int i2, int i3) {
        r3 = new float[8];
        float f = (float) i;
        r3[0] = f;
        r3[1] = f;
        r3[2] = f;
        r3[3] = f;
        r3[4] = f;
        r3[5] = f;
        r3[6] = f;
        r3[7] = f;
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(r3, null, null));
        shapeDrawable.getPaint().setColor(i2);
        ShapeDrawable shapeDrawable2 = new ShapeDrawable(new RoundRectShape(new float[]{f, f, f, f, f, f, f, f}, null, null));
        shapeDrawable2.getPaint().setColor(i3);
        if (VERSION.SDK_INT >= 21) {
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
        if (VERSION.SDK_INT >= 21) {
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{(i & 16777215) | NUM}), null, createRoundRectDrawable(AndroidUtilities.dp(3.0f), -1));
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        i = (i & 16777215) | NUM;
        stateListDrawable.addState(new int[]{16842919}, createRoundRectDrawable(AndroidUtilities.dp(3.0f), i));
        stateListDrawable.addState(new int[]{16842913}, createRoundRectDrawable(AndroidUtilities.dp(3.0f), i));
        stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(0));
        return stateListDrawable;
    }

    public static Drawable createSelectorWithBackgroundDrawable(int i, int i2) {
        if (VERSION.SDK_INT >= 21) {
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
        String str = "windowBackgroundWhite";
        if (VERSION.SDK_INT >= 21) {
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

    /* JADX WARNING: Removed duplicated region for block: B:24:0x0053  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0053  */
    public static android.graphics.drawable.Drawable createSelectorDrawable(int r10, final int r11, int r12) {
        /*
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 0;
        r2 = 1;
        r3 = 21;
        if (r0 < r3) goto L_0x0067;
    L_0x0008:
        r0 = 23;
        r3 = -1;
        r4 = 5;
        r5 = 0;
        if (r11 == r2) goto L_0x0011;
    L_0x000f:
        if (r11 != r4) goto L_0x0017;
    L_0x0011:
        r6 = android.os.Build.VERSION.SDK_INT;
        if (r6 < r0) goto L_0x0017;
    L_0x0015:
        r6 = r5;
        goto L_0x003b;
    L_0x0017:
        if (r11 == r2) goto L_0x0031;
    L_0x0019:
        r6 = 3;
        if (r11 == r6) goto L_0x0031;
    L_0x001c:
        r6 = 4;
        if (r11 == r6) goto L_0x0031;
    L_0x001f:
        if (r11 == r4) goto L_0x0031;
    L_0x0021:
        r6 = 6;
        if (r11 == r6) goto L_0x0031;
    L_0x0024:
        r6 = 7;
        if (r11 != r6) goto L_0x0028;
    L_0x0027:
        goto L_0x0031;
    L_0x0028:
        r6 = 2;
        if (r11 != r6) goto L_0x0015;
    L_0x002b:
        r6 = new android.graphics.drawable.ColorDrawable;
        r6.<init>(r3);
        goto L_0x003b;
    L_0x0031:
        r6 = maskPaint;
        r6.setColor(r3);
        r6 = new org.telegram.ui.ActionBar.Theme$6;
        r6.<init>(r11);
    L_0x003b:
        r7 = new android.content.res.ColorStateList;
        r8 = new int[r2][];
        r9 = android.util.StateSet.WILD_CARD;
        r8[r1] = r9;
        r9 = new int[r2];
        r9[r1] = r10;
        r7.<init>(r8, r9);
        r10 = new android.graphics.drawable.RippleDrawable;
        r10.<init>(r7, r5, r6);
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 < r0) goto L_0x0066;
    L_0x0053:
        if (r11 != r2) goto L_0x0061;
    L_0x0055:
        if (r12 > 0) goto L_0x005d;
    L_0x0057:
        r11 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r11);
    L_0x005d:
        r10.setRadius(r12);
        goto L_0x0066;
    L_0x0061:
        if (r11 != r4) goto L_0x0066;
    L_0x0063:
        r10.setRadius(r3);
    L_0x0066:
        return r10;
    L_0x0067:
        r11 = new android.graphics.drawable.StateListDrawable;
        r11.<init>();
        r12 = new int[r2];
        r0 = 16842919; // 0x10100a7 float:2.3694026E-38 double:8.3215077E-317;
        r12[r1] = r0;
        r0 = new android.graphics.drawable.ColorDrawable;
        r0.<init>(r10);
        r11.addState(r12, r0);
        r12 = new int[r2];
        r0 = 16842913; // 0x10100a1 float:2.369401E-38 double:8.3215047E-317;
        r12[r1] = r0;
        r0 = new android.graphics.drawable.ColorDrawable;
        r0.<init>(r10);
        r11.addState(r12, r0);
        r10 = android.util.StateSet.WILD_CARD;
        r12 = new android.graphics.drawable.ColorDrawable;
        r12.<init>(r1);
        r11.addState(r10, r12);
        return r11;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.createSelectorDrawable(int, int, int):android.graphics.drawable.Drawable");
    }

    public static void applyPreviousTheme() {
        if (previousTheme != null) {
            hasPreviousTheme = false;
            if (isInNigthMode) {
                ThemeInfo themeInfo = currentNightTheme;
                if (themeInfo != null) {
                    applyTheme(themeInfo, true, false, true);
                    isApplyingAccent = false;
                    previousTheme = null;
                    checkAutoNightThemeConditions();
                }
            }
            if (!isApplyingAccent) {
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
        Collections.sort(themes, -$$Lambda$Theme$9ifMr-1ad9oyLiuwyK8tFm4yWNA.INSTANCE);
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
        currentTheme.setOverrideWallpaper(null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:36:0x00e4 A:{Catch:{ all -> 0x00f7 }} */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00f2 A:{Catch:{ all -> 0x00f7 }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:33:0x00de */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00d4 A:{Catch:{ Exception -> 0x00de }} */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00e4 A:{Catch:{ all -> 0x00f7 }} */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00f2 A:{Catch:{ all -> 0x00f7 }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:29:0x00c8 */
    /* JADX WARNING: Can't wrap try/catch for region: R(11:23|24|(2:26|(1:28))|29|30|(1:32)|33|34|(1:36)|37|(1:39)) */
    /* JADX WARNING: Can't wrap try/catch for region: R(11:23|24|(2:26|(1:28))|29|30|(1:32)|33|34|(1:36)|37|(1:39)) */
    public static org.telegram.ui.ActionBar.Theme.ThemeInfo fillThemeValues(java.io.File r6, java.lang.String r7, org.telegram.tgnet.TLRPC.TL_theme r8) {
        /*
        r0 = 0;
        r1 = new org.telegram.ui.ActionBar.Theme$ThemeInfo;	 Catch:{ Exception -> 0x00ff }
        r1.<init>();	 Catch:{ Exception -> 0x00ff }
        r1.name = r7;	 Catch:{ Exception -> 0x00ff }
        r1.info = r8;	 Catch:{ Exception -> 0x00ff }
        r6 = r6.getAbsolutePath();	 Catch:{ Exception -> 0x00ff }
        r1.pathToFile = r6;	 Catch:{ Exception -> 0x00ff }
        r6 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ Exception -> 0x00ff }
        r1.account = r6;	 Catch:{ Exception -> 0x00ff }
        r6 = 1;
        r7 = new java.lang.String[r6];	 Catch:{ Exception -> 0x00ff }
        r8 = new java.io.File;	 Catch:{ Exception -> 0x00ff }
        r2 = r1.pathToFile;	 Catch:{ Exception -> 0x00ff }
        r8.<init>(r2);	 Catch:{ Exception -> 0x00ff }
        getThemeFileValues(r8, r0, r7);	 Catch:{ Exception -> 0x00ff }
        r8 = 0;
        r2 = r7[r8];	 Catch:{ Exception -> 0x00ff }
        r2 = android.text.TextUtils.isEmpty(r2);	 Catch:{ Exception -> 0x00ff }
        if (r2 != 0) goto L_0x00fc;
    L_0x002a:
        r7 = r7[r8];	 Catch:{ Exception -> 0x00ff }
        r2 = new java.io.File;	 Catch:{ Exception -> 0x00ff }
        r3 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();	 Catch:{ Exception -> 0x00ff }
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00ff }
        r4.<init>();	 Catch:{ Exception -> 0x00ff }
        r5 = org.telegram.messenger.Utilities.MD5(r7);	 Catch:{ Exception -> 0x00ff }
        r4.append(r5);	 Catch:{ Exception -> 0x00ff }
        r5 = ".wp";
        r4.append(r5);	 Catch:{ Exception -> 0x00ff }
        r4 = r4.toString();	 Catch:{ Exception -> 0x00ff }
        r2.<init>(r3, r4);	 Catch:{ Exception -> 0x00ff }
        r2 = r2.getAbsolutePath();	 Catch:{ Exception -> 0x00ff }
        r1.pathToWallpaper = r2;	 Catch:{ Exception -> 0x00ff }
        r7 = android.net.Uri.parse(r7);	 Catch:{ all -> 0x00f7 }
        r2 = "slug";
        r2 = r7.getQueryParameter(r2);	 Catch:{ all -> 0x00f7 }
        r1.slug = r2;	 Catch:{ all -> 0x00f7 }
        r2 = "mode";
        r2 = r7.getQueryParameter(r2);	 Catch:{ all -> 0x00f7 }
        if (r2 == 0) goto L_0x0092;
    L_0x0064:
        r2 = r2.toLowerCase();	 Catch:{ all -> 0x00f7 }
        r3 = " ";
        r2 = r2.split(r3);	 Catch:{ all -> 0x00f7 }
        if (r2 == 0) goto L_0x0092;
    L_0x0070:
        r3 = r2.length;	 Catch:{ all -> 0x00f7 }
        if (r3 <= 0) goto L_0x0092;
    L_0x0073:
        r3 = r2.length;	 Catch:{ all -> 0x00f7 }
        if (r8 >= r3) goto L_0x0092;
    L_0x0076:
        r3 = "blur";
        r4 = r2[r8];	 Catch:{ all -> 0x00f7 }
        r3 = r3.equals(r4);	 Catch:{ all -> 0x00f7 }
        if (r3 == 0) goto L_0x0083;
    L_0x0080:
        r1.isBlured = r6;	 Catch:{ all -> 0x00f7 }
        goto L_0x008f;
    L_0x0083:
        r3 = "motion";
        r4 = r2[r8];	 Catch:{ all -> 0x00f7 }
        r3 = r3.equals(r4);	 Catch:{ all -> 0x00f7 }
        if (r3 == 0) goto L_0x008f;
    L_0x008d:
        r1.isMotion = r6;	 Catch:{ all -> 0x00f7 }
    L_0x008f:
        r8 = r8 + 1;
        goto L_0x0073;
    L_0x0092:
        r6 = "intensity";
        r6 = r7.getQueryParameter(r6);	 Catch:{ all -> 0x00f7 }
        r8 = android.text.TextUtils.isEmpty(r6);	 Catch:{ all -> 0x00f7 }
        if (r8 != 0) goto L_0x00fe;
    L_0x009e:
        r8 = "bg_color";
        r8 = r7.getQueryParameter(r8);	 Catch:{ Exception -> 0x00c8 }
        r2 = android.text.TextUtils.isEmpty(r8);	 Catch:{ Exception -> 0x00c8 }
        if (r2 != 0) goto L_0x00c8;
    L_0x00aa:
        r2 = 16;
        r3 = java.lang.Integer.parseInt(r8, r2);	 Catch:{ Exception -> 0x00c8 }
        r4 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r3 = r3 | r4;
        r1.patternBgColor = r3;	 Catch:{ Exception -> 0x00c8 }
        r3 = r8.length();	 Catch:{ Exception -> 0x00c8 }
        r5 = 6;
        if (r3 <= r5) goto L_0x00c8;
    L_0x00bc:
        r3 = 7;
        r8 = r8.substring(r3);	 Catch:{ Exception -> 0x00c8 }
        r8 = java.lang.Integer.parseInt(r8, r2);	 Catch:{ Exception -> 0x00c8 }
        r8 = r8 | r4;
        r1.patternBgGradientColor = r8;	 Catch:{ Exception -> 0x00c8 }
    L_0x00c8:
        r8 = "rotation";
        r7 = r7.getQueryParameter(r8);	 Catch:{ Exception -> 0x00de }
        r8 = android.text.TextUtils.isEmpty(r7);	 Catch:{ Exception -> 0x00de }
        if (r8 != 0) goto L_0x00de;
    L_0x00d4:
        r7 = org.telegram.messenger.Utilities.parseInt(r7);	 Catch:{ Exception -> 0x00de }
        r7 = r7.intValue();	 Catch:{ Exception -> 0x00de }
        r1.patternBgGradientRotation = r7;	 Catch:{ Exception -> 0x00de }
    L_0x00de:
        r7 = android.text.TextUtils.isEmpty(r6);	 Catch:{ all -> 0x00f7 }
        if (r7 != 0) goto L_0x00ee;
    L_0x00e4:
        r6 = org.telegram.messenger.Utilities.parseInt(r6);	 Catch:{ all -> 0x00f7 }
        r6 = r6.intValue();	 Catch:{ all -> 0x00f7 }
        r1.patternIntensity = r6;	 Catch:{ all -> 0x00f7 }
    L_0x00ee:
        r6 = r1.patternIntensity;	 Catch:{ all -> 0x00f7 }
        if (r6 != 0) goto L_0x00fe;
    L_0x00f2:
        r6 = 50;
        r1.patternIntensity = r6;	 Catch:{ all -> 0x00f7 }
        goto L_0x00fe;
    L_0x00f7:
        r6 = move-exception;
        org.telegram.messenger.FileLog.e(r6);	 Catch:{ Exception -> 0x00ff }
        goto L_0x00fe;
    L_0x00fc:
        themedWallpaperLink = r0;	 Catch:{ Exception -> 0x00ff }
    L_0x00fe:
        return r1;
    L_0x00ff:
        r6 = move-exception;
        org.telegram.messenger.FileLog.e(r6);
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.fillThemeValues(java.io.File, java.lang.String, org.telegram.tgnet.TLRPC$TL_theme):org.telegram.ui.ActionBar.Theme$ThemeInfo");
    }

    public static ThemeInfo applyThemeFile(File file, String str, TL_theme tL_theme, boolean z) {
        String str2 = ".attheme";
        try {
            if (!str.toLowerCase().endsWith(str2)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(str2);
                str = stringBuilder.toString();
            }
            if (z) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.goingToPreviewTheme, new Object[0]);
                ThemeInfo themeInfo = new ThemeInfo();
                themeInfo.name = str;
                themeInfo.info = tL_theme;
                themeInfo.pathToFile = file.getAbsolutePath();
                themeInfo.account = UserConfig.selectedAccount;
                applyThemeTemporary(themeInfo, false);
                return themeInfo;
            }
            Object stringBuilder2;
            File file2;
            if (tL_theme != null) {
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append("remote");
                stringBuilder3.append(tL_theme.id);
                stringBuilder2 = stringBuilder3.toString();
                File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                StringBuilder stringBuilder4 = new StringBuilder();
                stringBuilder4.append(stringBuilder2);
                stringBuilder4.append(str2);
                file2 = new File(filesDirFixed, stringBuilder4.toString());
            } else {
                file2 = new File(ApplicationLoader.getFilesDirFixed(), str);
                stringBuilder2 = str;
            }
            if (AndroidUtilities.copyFile(file, file2)) {
                previousTheme = null;
                hasPreviousTheme = false;
                isApplyingAccent = false;
                ThemeInfo themeInfo2 = (ThemeInfo) themesDict.get(stringBuilder2);
                if (themeInfo2 == null) {
                    themeInfo2 = new ThemeInfo();
                    themeInfo2.name = str;
                    themeInfo2.account = UserConfig.selectedAccount;
                    themes.add(themeInfo2);
                    otherThemes.add(themeInfo2);
                    sortThemes();
                } else {
                    themesDict.remove(stringBuilder2);
                }
                themeInfo2.info = tL_theme;
                themeInfo2.pathToFile = file2.getAbsolutePath();
                themesDict.put(themeInfo2.getKey(), themeInfo2);
                saveOtherThemes(true);
                applyTheme(themeInfo2, true, true, false);
                return themeInfo2;
            }
            applyPreviousTheme();
            return null;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static ThemeInfo getTheme(String str) {
        return (ThemeInfo) themesDict.get(str);
    }

    public static void applyTheme(ThemeInfo themeInfo) {
        applyTheme(themeInfo, true, true, false);
    }

    public static void applyTheme(ThemeInfo themeInfo, boolean z) {
        applyTheme(themeInfo, true, true, z);
    }

    /* JADX WARNING: Unknown top exception splitter block from list: {B:38:0x00c9=Splitter:B:38:0x00c9, B:76:0x017b=Splitter:B:76:0x017b} */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x018d A:{Catch:{ Exception -> 0x019d }} */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x018d A:{Catch:{ Exception -> 0x019d }} */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x015b A:{Catch:{ Exception -> 0x0166 }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:64:0x014f */
    /* JADX WARNING: Missing exception handler attribute for start block: B:76:0x017b */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00e1 A:{Catch:{ all -> 0x0168 }} */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x012d A:{Catch:{ Exception -> 0x014f }} */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x015b A:{Catch:{ Exception -> 0x0166 }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:38:0x00c9 */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Can't wrap try/catch for region: R(24:31|32|33|(1:37)|38|39|40|41|(2:43|(3:47|(4:50|(2:52|96)(2:53|(2:55|95)(1:97))|56|48)|94))|57|58|59|(2:61|(1:63))|64|65|(1:67)|79|81|(0)|84|87|90|92|93) */
    /* JADX WARNING: Can't wrap try/catch for region: R(13:72|73|(1:75)|76|77|79|81|(0)|84|87|90|92|93) */
    private static void applyTheme(org.telegram.ui.ActionBar.Theme.ThemeInfo r6, boolean r7, boolean r8, boolean r9) {
        /*
        if (r6 != 0) goto L_0x0003;
    L_0x0002:
        return;
    L_0x0003:
        r8 = org.telegram.ui.Components.ThemeEditorView.getInstance();
        if (r8 == 0) goto L_0x000c;
    L_0x0009:
        r8.destroy();
    L_0x000c:
        r8 = 0;
        r0 = r6.pathToFile;	 Catch:{ Exception -> 0x019d }
        r1 = "theme";
        r2 = 0;
        if (r0 != 0) goto L_0x003b;
    L_0x0015:
        r0 = r6.assetName;	 Catch:{ Exception -> 0x019d }
        if (r0 == 0) goto L_0x001a;
    L_0x0019:
        goto L_0x003b;
    L_0x001a:
        if (r9 != 0) goto L_0x002c;
    L_0x001c:
        if (r7 == 0) goto L_0x002c;
    L_0x001e:
        r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings();	 Catch:{ Exception -> 0x019d }
        r0 = r0.edit();	 Catch:{ Exception -> 0x019d }
        r0.remove(r1);	 Catch:{ Exception -> 0x019d }
        r0.commit();	 Catch:{ Exception -> 0x019d }
    L_0x002c:
        r0 = currentColorsNoAccent;	 Catch:{ Exception -> 0x019d }
        r0.clear();	 Catch:{ Exception -> 0x019d }
        themedWallpaperFileOffset = r8;	 Catch:{ Exception -> 0x019d }
        themedWallpaperLink = r2;	 Catch:{ Exception -> 0x019d }
        wallpaper = r2;	 Catch:{ Exception -> 0x019d }
        themedWallpaper = r2;	 Catch:{ Exception -> 0x019d }
        goto L_0x017f;
    L_0x003b:
        if (r9 != 0) goto L_0x0051;
    L_0x003d:
        if (r7 == 0) goto L_0x0051;
    L_0x003f:
        r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings();	 Catch:{ Exception -> 0x019d }
        r0 = r0.edit();	 Catch:{ Exception -> 0x019d }
        r3 = r6.getKey();	 Catch:{ Exception -> 0x019d }
        r0.putString(r1, r3);	 Catch:{ Exception -> 0x019d }
        r0.commit();	 Catch:{ Exception -> 0x019d }
    L_0x0051:
        r0 = 1;
        r1 = new java.lang.String[r0];	 Catch:{ Exception -> 0x019d }
        r3 = r6.assetName;	 Catch:{ Exception -> 0x019d }
        if (r3 == 0) goto L_0x0061;
    L_0x0058:
        r3 = r6.assetName;	 Catch:{ Exception -> 0x019d }
        r3 = getThemeFileValues(r2, r3, r2);	 Catch:{ Exception -> 0x019d }
        currentColorsNoAccent = r3;	 Catch:{ Exception -> 0x019d }
        goto L_0x006e;
    L_0x0061:
        r3 = new java.io.File;	 Catch:{ Exception -> 0x019d }
        r4 = r6.pathToFile;	 Catch:{ Exception -> 0x019d }
        r3.<init>(r4);	 Catch:{ Exception -> 0x019d }
        r3 = getThemeFileValues(r3, r2, r1);	 Catch:{ Exception -> 0x019d }
        currentColorsNoAccent = r3;	 Catch:{ Exception -> 0x019d }
    L_0x006e:
        r3 = currentColorsNoAccent;	 Catch:{ Exception -> 0x019d }
        r4 = "wallpaperFileOffset";
        r3 = r3.get(r4);	 Catch:{ Exception -> 0x019d }
        r3 = (java.lang.Integer) r3;	 Catch:{ Exception -> 0x019d }
        if (r3 == 0) goto L_0x0080;
    L_0x007b:
        r3 = r3.intValue();	 Catch:{ Exception -> 0x019d }
        goto L_0x0081;
    L_0x0080:
        r3 = -1;
    L_0x0081:
        themedWallpaperFileOffset = r3;	 Catch:{ Exception -> 0x019d }
        r3 = r1[r8];	 Catch:{ Exception -> 0x019d }
        r3 = android.text.TextUtils.isEmpty(r3);	 Catch:{ Exception -> 0x019d }
        if (r3 != 0) goto L_0x016d;
    L_0x008b:
        r1 = r1[r8];	 Catch:{ Exception -> 0x019d }
        themedWallpaperLink = r1;	 Catch:{ Exception -> 0x019d }
        r1 = new java.io.File;	 Catch:{ Exception -> 0x019d }
        r2 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();	 Catch:{ Exception -> 0x019d }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x019d }
        r3.<init>();	 Catch:{ Exception -> 0x019d }
        r4 = themedWallpaperLink;	 Catch:{ Exception -> 0x019d }
        r4 = org.telegram.messenger.Utilities.MD5(r4);	 Catch:{ Exception -> 0x019d }
        r3.append(r4);	 Catch:{ Exception -> 0x019d }
        r4 = ".wp";
        r3.append(r4);	 Catch:{ Exception -> 0x019d }
        r3 = r3.toString();	 Catch:{ Exception -> 0x019d }
        r1.<init>(r2, r3);	 Catch:{ Exception -> 0x019d }
        r1 = r1.getAbsolutePath();	 Catch:{ Exception -> 0x019d }
        r2 = r6.pathToWallpaper;	 Catch:{ Exception -> 0x00c9 }
        if (r2 == 0) goto L_0x00c9;
    L_0x00b7:
        r2 = r6.pathToWallpaper;	 Catch:{ Exception -> 0x00c9 }
        r2 = r2.equals(r1);	 Catch:{ Exception -> 0x00c9 }
        if (r2 != 0) goto L_0x00c9;
    L_0x00bf:
        r2 = new java.io.File;	 Catch:{ Exception -> 0x00c9 }
        r3 = r6.pathToWallpaper;	 Catch:{ Exception -> 0x00c9 }
        r2.<init>(r3);	 Catch:{ Exception -> 0x00c9 }
        r2.delete();	 Catch:{ Exception -> 0x00c9 }
    L_0x00c9:
        r6.pathToWallpaper = r1;	 Catch:{ Exception -> 0x019d }
        r1 = themedWallpaperLink;	 Catch:{ all -> 0x0168 }
        r1 = android.net.Uri.parse(r1);	 Catch:{ all -> 0x0168 }
        r2 = "slug";
        r2 = r1.getQueryParameter(r2);	 Catch:{ all -> 0x0168 }
        r6.slug = r2;	 Catch:{ all -> 0x0168 }
        r2 = "mode";
        r2 = r1.getQueryParameter(r2);	 Catch:{ all -> 0x0168 }
        if (r2 == 0) goto L_0x0110;
    L_0x00e1:
        r2 = r2.toLowerCase();	 Catch:{ all -> 0x0168 }
        r3 = " ";
        r2 = r2.split(r3);	 Catch:{ all -> 0x0168 }
        if (r2 == 0) goto L_0x0110;
    L_0x00ed:
        r3 = r2.length;	 Catch:{ all -> 0x0168 }
        if (r3 <= 0) goto L_0x0110;
    L_0x00f0:
        r3 = 0;
    L_0x00f1:
        r4 = r2.length;	 Catch:{ all -> 0x0168 }
        if (r3 >= r4) goto L_0x0110;
    L_0x00f4:
        r4 = "blur";
        r5 = r2[r3];	 Catch:{ all -> 0x0168 }
        r4 = r4.equals(r5);	 Catch:{ all -> 0x0168 }
        if (r4 == 0) goto L_0x0101;
    L_0x00fe:
        r6.isBlured = r0;	 Catch:{ all -> 0x0168 }
        goto L_0x010d;
    L_0x0101:
        r4 = "motion";
        r5 = r2[r3];	 Catch:{ all -> 0x0168 }
        r4 = r4.equals(r5);	 Catch:{ all -> 0x0168 }
        if (r4 == 0) goto L_0x010d;
    L_0x010b:
        r6.isMotion = r0;	 Catch:{ all -> 0x0168 }
    L_0x010d:
        r3 = r3 + 1;
        goto L_0x00f1;
    L_0x0110:
        r0 = "intensity";
        r0 = r1.getQueryParameter(r0);	 Catch:{ all -> 0x0168 }
        r0 = org.telegram.messenger.Utilities.parseInt(r0);	 Catch:{ all -> 0x0168 }
        r0.intValue();	 Catch:{ all -> 0x0168 }
        r0 = 45;
        r6.patternBgGradientRotation = r0;	 Catch:{ all -> 0x0168 }
        r0 = "bg_color";
        r0 = r1.getQueryParameter(r0);	 Catch:{ Exception -> 0x014f }
        r2 = android.text.TextUtils.isEmpty(r0);	 Catch:{ Exception -> 0x014f }
        if (r2 != 0) goto L_0x014f;
    L_0x012d:
        r2 = 6;
        r3 = r0.substring(r8, r2);	 Catch:{ Exception -> 0x014f }
        r4 = 16;
        r3 = java.lang.Integer.parseInt(r3, r4);	 Catch:{ Exception -> 0x014f }
        r5 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r3 = r3 | r5;
        r6.patternBgColor = r3;	 Catch:{ Exception -> 0x014f }
        r3 = r0.length();	 Catch:{ Exception -> 0x014f }
        if (r3 <= r2) goto L_0x014f;
    L_0x0143:
        r2 = 7;
        r0 = r0.substring(r2);	 Catch:{ Exception -> 0x014f }
        r0 = java.lang.Integer.parseInt(r0, r4);	 Catch:{ Exception -> 0x014f }
        r0 = r0 | r5;
        r6.patternBgGradientColor = r0;	 Catch:{ Exception -> 0x014f }
    L_0x014f:
        r0 = "rotation";
        r0 = r1.getQueryParameter(r0);	 Catch:{ Exception -> 0x0166 }
        r1 = android.text.TextUtils.isEmpty(r0);	 Catch:{ Exception -> 0x0166 }
        if (r1 != 0) goto L_0x017f;
    L_0x015b:
        r0 = org.telegram.messenger.Utilities.parseInt(r0);	 Catch:{ Exception -> 0x0166 }
        r0 = r0.intValue();	 Catch:{ Exception -> 0x0166 }
        r6.patternBgGradientRotation = r0;	 Catch:{ Exception -> 0x0166 }
        goto L_0x017f;
        goto L_0x017f;
    L_0x0168:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x019d }
        goto L_0x017f;
    L_0x016d:
        r0 = r6.pathToWallpaper;	 Catch:{ Exception -> 0x017b }
        if (r0 == 0) goto L_0x017b;
    L_0x0171:
        r0 = new java.io.File;	 Catch:{ Exception -> 0x017b }
        r1 = r6.pathToWallpaper;	 Catch:{ Exception -> 0x017b }
        r0.<init>(r1);	 Catch:{ Exception -> 0x017b }
        r0.delete();	 Catch:{ Exception -> 0x017b }
    L_0x017b:
        r6.pathToWallpaper = r2;	 Catch:{ Exception -> 0x019d }
        themedWallpaperLink = r2;	 Catch:{ Exception -> 0x019d }
    L_0x017f:
        if (r9 != 0) goto L_0x0197;
    L_0x0181:
        r0 = previousTheme;	 Catch:{ Exception -> 0x019d }
        if (r0 != 0) goto L_0x0197;
    L_0x0185:
        currentDayTheme = r6;	 Catch:{ Exception -> 0x019d }
        r0 = isCurrentThemeNight();	 Catch:{ Exception -> 0x019d }
        if (r0 == 0) goto L_0x0197;
    L_0x018d:
        r0 = 2000; // 0x7d0 float:2.803E-42 double:9.88E-321;
        switchNightThemeDelay = r0;	 Catch:{ Exception -> 0x019d }
        r0 = android.os.SystemClock.uptimeMillis();	 Catch:{ Exception -> 0x019d }
        lastDelayUpdateTime = r0;	 Catch:{ Exception -> 0x019d }
    L_0x0197:
        currentTheme = r6;	 Catch:{ Exception -> 0x019d }
        refreshThemeColors();	 Catch:{ Exception -> 0x019d }
        goto L_0x01a1;
    L_0x019d:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x01a1:
        r0 = previousTheme;
        if (r0 != 0) goto L_0x01b8;
    L_0x01a5:
        if (r7 == 0) goto L_0x01b8;
    L_0x01a7:
        r7 = switchingNightTheme;
        if (r7 != 0) goto L_0x01b8;
    L_0x01ab:
        r7 = r6.account;
        r7 = org.telegram.messenger.MessagesController.getInstance(r7);
        r0 = r6.getAccent(r8);
        r7.saveTheme(r6, r0, r9, r8);
    L_0x01b8:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.applyTheme(org.telegram.ui.ActionBar.Theme$ThemeInfo, boolean, boolean, boolean):void");
    }

    private static boolean useBlackText(int i, int i2) {
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
        AndroidUtilities.runOnUIThread(-$$Lambda$Theme$vGv1azM1iijU_YZpwqwy2UfjTKA.INSTANCE);
    }

    public static int changeColorAccent(ThemeInfo themeInfo, int i, int i2) {
        if (i != 0) {
            int i3 = themeInfo.accentBaseColor;
            if (!(i3 == 0 || i == i3 || (themeInfo.firstAccentIsDefault && themeInfo.currentAccentId == DEFALT_THEME_ACCENT_ID))) {
                float[] tempHsv = getTempHsv(3);
                float[] tempHsv2 = getTempHsv(4);
                Color.colorToHSV(themeInfo.accentBaseColor, tempHsv);
                Color.colorToHSV(i, tempHsv2);
                return changeColorAccent(tempHsv, tempHsv2, i2, themeInfo.isDark());
            }
        }
        return i2;
    }

    private static float[] getTempHsv(int i) {
        ThreadLocal threadLocal;
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
        float[] fArr = (float[]) threadLocal.get();
        if (fArr != null) {
            return fArr;
        }
        fArr = new float[3];
        threadLocal.set(fArr);
        return fArr;
    }

    private static int getAccentColor(float[] fArr, int i, int i2) {
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
        int i2 = 0;
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
        if (z ? computePerceivedBrightness <= computePerceivedBrightness2 : computePerceivedBrightness >= computePerceivedBrightness2) {
            i2 = 1;
        }
        if (i2 != 0) {
            HSVToColor = changeBrightness(HSVToColor, ((0.39999998f * computePerceivedBrightness) / computePerceivedBrightness2) + 0.6f);
        }
        return HSVToColor;
    }

    private static int changeBrightness(int i, float f) {
        int red = (int) (((float) Color.red(i)) * f);
        int green = (int) (((float) Color.green(i)) * f);
        int blue = (int) (((float) Color.blue(i)) * f);
        if (red < 0) {
            red = 0;
        } else if (red > 255) {
            red = 255;
        }
        if (green < 0) {
            green = 0;
        } else if (green > 255) {
            green = 255;
        }
        if (blue < 0) {
            blue = 0;
        } else if (blue > 255) {
            blue = 255;
        }
        return Color.argb(Color.alpha(i), red, green, blue);
    }

    public static void onUpdateThemeAccents() {
        refreshThemeColors();
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
        TL_theme tL_theme = themeAccent.info;
        if (tL_theme != null) {
            themeInfo.accentsByThemeId.remove(tL_theme.id);
        }
        OverrideWallpaperInfo overrideWallpaperInfo = themeAccent.overrideWallpaper;
        if (overrideWallpaperInfo != null) {
            overrideWallpaperInfo.delete();
        }
        if (z3) {
            themeInfo.setCurrentAccentId(((ThemeAccent) themeInfo.themeAccents.get(0)).id);
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
            StringBuilder stringBuilder;
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
            if (!z3) {
                int size = themeInfo.themeAccents.size();
                int i = size - themeInfo.defaultAccentCount;
                SerializedData serializedData = new SerializedData(((i * 15) + 2) * 4);
                serializedData.writeInt32(5);
                serializedData.writeInt32(i);
                for (i = 0; i < size; i++) {
                    ThemeAccent themeAccent = (ThemeAccent) themeInfo.themeAccents.get(i);
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
                stringBuilder = new StringBuilder();
                stringBuilder.append("accents_");
                stringBuilder.append(themeInfo.assetName);
                edit.putString(stringBuilder.toString(), Base64.encodeToString(serializedData.toByteArray(), 3));
                if (!z5) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.themeAccentListUpdated, new Object[0]);
                }
                if (z4) {
                    MessagesController.getInstance(UserConfig.selectedAccount).saveThemeToServer(themeInfo, themeInfo.getAccent(false));
                }
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append("accent_current_");
            stringBuilder.append(themeInfo.assetName);
            edit.putInt(stringBuilder.toString(), themeInfo.currentAccentId);
            edit.commit();
        } else {
            if (themeInfo.prevAccentId != -1) {
                ThemeAccent themeAccent2;
                if (z2) {
                    themeAccent2 = (ThemeAccent) themeInfo.themeAccentsMap.get(themeInfo.currentAccentId);
                    themeInfo.themeAccentsMap.remove(themeAccent2.id);
                    themeInfo.themeAccents.remove(themeAccent2);
                    TL_theme tL_theme = themeAccent2.info;
                    if (tL_theme != null) {
                        themeInfo.accentsByThemeId.remove(tL_theme.id);
                    }
                }
                themeInfo.currentAccentId = themeInfo.prevAccentId;
                themeAccent2 = themeInfo.getAccent(false);
                if (themeAccent2 != null) {
                    themeInfo.overrideWallpaper = themeAccent2.overrideWallpaper;
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

    private static void saveOtherThemes(boolean z) {
        saveOtherThemes(z, false);
    }

    private static void saveOtherThemes(boolean z, boolean z2) {
        int i = 0;
        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
        if (z) {
            JSONArray jSONArray = new JSONArray();
            for (int i2 = 0; i2 < otherThemes.size(); i2++) {
                JSONObject saveJson = ((ThemeInfo) otherThemes.get(i2)).getSaveJson();
                if (saveJson != null) {
                    jSONArray.put(saveJson);
                }
            }
            edit.putString("themes2", jSONArray.toString());
        }
        int i3 = 0;
        while (i3 < 3) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("remoteThemesHash");
            Object obj = "";
            stringBuilder.append(i3 != 0 ? Integer.valueOf(i3) : obj);
            edit.putInt(stringBuilder.toString(), remoteThemesHash[i3]);
            stringBuilder = new StringBuilder();
            stringBuilder.append("lastLoadingThemesTime");
            if (i3 != 0) {
                obj = Integer.valueOf(i3);
            }
            stringBuilder.append(obj);
            edit.putInt(stringBuilder.toString(), lastLoadingThemesTime[i3]);
            i3++;
        }
        edit.putInt("lastLoadingCurrentThemeTime", lastLoadingCurrentThemeTime);
        edit.commit();
        if (z) {
            while (i < 5) {
                Object obj2 = i != 0 ? i != 1 ? i != 2 ? i != 3 ? "Night" : "Day" : "Arctic Blue" : "Dark Blue" : "Blue";
                ThemeInfo themeInfo = (ThemeInfo) themesDict.get(obj2);
                if (themeInfo != null) {
                    ArrayList arrayList = themeInfo.themeAccents;
                    if (!(arrayList == null || arrayList.isEmpty())) {
                        saveThemeAccents(themeInfo, true, false, false, false, z2);
                    }
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

    public static String getCurrentThemeName() {
        String name = currentDayTheme.getName();
        return name.toLowerCase().endsWith(".attheme") ? name.substring(0, name.lastIndexOf(46)) : name;
    }

    public static String getCurrentNightThemeName() {
        ThemeInfo themeInfo = currentNightTheme;
        if (themeInfo == null) {
            return "";
        }
        String name = themeInfo.getName();
        if (name.toLowerCase().endsWith(".attheme")) {
            name = name.substring(0, name.lastIndexOf(46));
        }
        return name;
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

    private static long getAutoNightSwitchThemeDelay() {
        return Math.abs(lastThemeSwitchTime - SystemClock.elapsedRealtime()) >= 12000 ? 1800 : 12000;
    }

    public static void setCurrentNightTheme(ThemeInfo themeInfo) {
        Object obj = currentTheme == currentNightTheme ? 1 : null;
        currentNightTheme = themeInfo;
        if (obj != null) {
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
        int i = selectedAutoNightType;
        if (i == 1) {
            int i2;
            Calendar instance = Calendar.getInstance();
            instance.setTimeInMillis(System.currentTimeMillis());
            int i3 = (instance.get(11) * 60) + instance.get(12);
            if (autoNightScheduleByLocation) {
                i = instance.get(5);
                if (autoNightLastSunCheckDay != i) {
                    double d = autoNightLocationLatitude;
                    if (d != 10000.0d) {
                        double d2 = autoNightLocationLongitude;
                        if (d2 != 10000.0d) {
                            int[] calculateSunriseSunset = SunDate.calculateSunriseSunset(d, d2);
                            autoNightSunriseTime = calculateSunriseSunset[0];
                            autoNightSunsetTime = calculateSunriseSunset[1];
                            autoNightLastSunCheckDay = i;
                            saveAutoNightThemeConfig();
                        }
                    }
                }
                i = autoNightSunsetTime;
                i2 = autoNightSunriseTime;
            } else {
                i = autoNightDayStartTime;
                i2 = autoNightDayEndTime;
            }
            return i < i2 ? (i > i3 || i3 > i2) ? 1 : 2 : ((i > i3 || i3 > 1440) && (i3 < 0 || i3 > i2)) ? 1 : 2;
        } else {
            if (i == 2) {
                if (lightSensor == null) {
                    sensorManager = (SensorManager) ApplicationLoader.applicationContext.getSystemService("sensor");
                    lightSensor = sensorManager.getDefaultSensor(5);
                }
                if (!lightSensorRegistered) {
                    Sensor sensor = lightSensor;
                    if (sensor != null) {
                        SensorEventListener sensorEventListener = ambientSensorListener;
                        if (sensorEventListener != null) {
                            sensorManager.registerListener(sensorEventListener, sensor, 500000);
                            lightSensorRegistered = true;
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("light sensor registered");
                            }
                        }
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
                i = ApplicationLoader.applicationContext.getResources().getConfiguration().uiMode & 48;
                if (i == 0 || i == 16) {
                    return 1;
                }
                return i != 32 ? 0 : 2;
            } else if (i == 0) {
                return 1;
            }
        }
    }

    public static void checkAutoNightThemeConditions(boolean z) {
        if (previousTheme == null) {
            if (!z && switchNightThemeDelay > 0) {
                long uptimeMillis = SystemClock.uptimeMillis();
                long j = uptimeMillis - lastDelayUpdateTime;
                lastDelayUpdateTime = uptimeMillis;
                switchNightThemeDelay = (int) (((long) switchNightThemeDelay) - j);
                if (switchNightThemeDelay > 0) {
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

    private static void applyDayNightThemeMaybe(boolean z) {
        if (previousTheme == null) {
            if (z) {
                if (currentTheme != currentNightTheme) {
                    isInNigthMode = true;
                    lastThemeSwitchTime = SystemClock.elapsedRealtime();
                    switchingNightTheme = true;
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, currentNightTheme, Boolean.valueOf(true), null, Integer.valueOf(-1));
                    switchingNightTheme = false;
                }
            } else if (currentTheme != currentDayTheme) {
                isInNigthMode = false;
                lastThemeSwitchTime = SystemClock.elapsedRealtime();
                switchingNightTheme = true;
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, currentDayTheme, Boolean.valueOf(true), null, Integer.valueOf(-1));
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
            currentNightTheme = (ThemeInfo) themesDict.get("Dark Blue");
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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("theme");
        stringBuilder.append(Utilities.random.nextLong());
        stringBuilder.append(".attheme");
        themeInfo.pathToFile = new File(filesDirFixed, stringBuilder.toString()).getAbsolutePath();
        themeInfo.name = str;
        themedWallpaperLink = getWallpaperUrl(currentTheme.overrideWallpaper);
        themeInfo.account = UserConfig.selectedAccount;
        saveCurrentTheme(themeInfo, true, true, false);
        return themeInfo;
    }

    private static String getWallpaperUrl(OverrideWallpaperInfo overrideWallpaperInfo) {
        String str = null;
        if (overrideWallpaperInfo == null || TextUtils.isEmpty(overrideWallpaperInfo.slug) || overrideWallpaperInfo.slug.equals("d")) {
            return null;
        }
        StringBuilder stringBuilder;
        String stringBuilder2;
        StringBuilder stringBuilder3 = new StringBuilder();
        if (overrideWallpaperInfo.isBlurred) {
            stringBuilder3.append("blur");
        }
        if (overrideWallpaperInfo.isMotion) {
            if (stringBuilder3.length() > 0) {
                stringBuilder3.append("+");
            }
            stringBuilder3.append("motion");
        }
        String str2 = "https://attheme.org?slug=";
        if (overrideWallpaperInfo.color == 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(str2);
            stringBuilder.append(overrideWallpaperInfo.slug);
            stringBuilder2 = stringBuilder.toString();
        } else {
            Object[] objArr = new Object[]{Integer.valueOf(((byte) (overrideWallpaperInfo.color >> 16)) & 255), Integer.valueOf(((byte) (overrideWallpaperInfo.color >> 8)) & 255), Byte.valueOf((byte) (overrideWallpaperInfo.color & 255))};
            String str3 = "%02x%02x%02x";
            String toLowerCase = String.format(str3, objArr).toLowerCase();
            if (overrideWallpaperInfo.gradientColor != 0) {
                str = String.format(str3, new Object[]{Integer.valueOf(((byte) (overrideWallpaperInfo.gradientColor >> 16)) & 255), Integer.valueOf(((byte) (overrideWallpaperInfo.gradientColor >> 8)) & 255), Byte.valueOf((byte) (overrideWallpaperInfo.gradientColor & 255))}).toLowerCase();
            }
            if (str != null) {
                StringBuilder stringBuilder4 = new StringBuilder();
                stringBuilder4.append(toLowerCase);
                stringBuilder4.append("-");
                stringBuilder4.append(str);
                str = stringBuilder4.toString();
                stringBuilder4 = new StringBuilder();
                stringBuilder4.append(str);
                stringBuilder4.append("&rotation=");
                stringBuilder4.append(overrideWallpaperInfo.rotation);
                toLowerCase = stringBuilder4.toString();
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append(str2);
            stringBuilder.append(overrideWallpaperInfo.slug);
            stringBuilder.append("&intensity=");
            stringBuilder.append((int) (overrideWallpaperInfo.intensity * 100.0f));
            stringBuilder.append("&bg_color=");
            stringBuilder.append(toLowerCase);
            stringBuilder2 = stringBuilder.toString();
        }
        if (stringBuilder3.length() > 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(stringBuilder2);
            stringBuilder.append("&mode=");
            stringBuilder.append(stringBuilder3.toString());
            stringBuilder2 = stringBuilder.toString();
        }
        return stringBuilder2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:107:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x01dd  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x01d3 A:{SYNTHETIC, Splitter:B:89:0x01d3} */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x01dd  */
    /* JADX WARNING: Removed duplicated region for block: B:107:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x01ed A:{SYNTHETIC, Splitter:B:96:0x01ed} */
    public static void saveCurrentTheme(org.telegram.ui.ActionBar.Theme.ThemeInfo r12, boolean r13, boolean r14, boolean r15) {
        /*
        r0 = r12.overrideWallpaper;
        if (r0 == 0) goto L_0x0009;
    L_0x0004:
        r0 = getWallpaperUrl(r0);
        goto L_0x000b;
    L_0x0009:
        r0 = themedWallpaperLink;
    L_0x000b:
        if (r14 == 0) goto L_0x0010;
    L_0x000d:
        r1 = wallpaper;
        goto L_0x0012;
    L_0x0010:
        r1 = themedWallpaper;
    L_0x0012:
        if (r14 == 0) goto L_0x001a;
    L_0x0014:
        if (r1 == 0) goto L_0x001a;
    L_0x0016:
        r2 = wallpaper;
        themedWallpaper = r2;
    L_0x001a:
        r2 = currentTheme;
        r3 = 0;
        r2 = r2.getAccent(r3);
        r4 = currentTheme;
        r4 = r4.firstAccentIsDefault;
        if (r4 == 0) goto L_0x0030;
    L_0x0027:
        r4 = r2.id;
        r5 = DEFALT_THEME_ACCENT_ID;
        if (r4 != r5) goto L_0x0030;
    L_0x002d:
        r4 = defaultColors;
        goto L_0x0032;
    L_0x0030:
        r4 = currentColors;
    L_0x0032:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = defaultColors;
        if (r4 == r6) goto L_0x005d;
    L_0x003b:
        if (r2 == 0) goto L_0x0040;
    L_0x003d:
        r6 = r2.myMessagesAccentColor;
        goto L_0x0041;
    L_0x0040:
        r6 = 0;
    L_0x0041:
        if (r2 == 0) goto L_0x0046;
    L_0x0043:
        r2 = r2.myMessagesGradientAccentColor;
        goto L_0x0047;
    L_0x0046:
        r2 = 0;
    L_0x0047:
        if (r6 == 0) goto L_0x005d;
    L_0x0049:
        if (r2 == 0) goto L_0x005d;
    L_0x004b:
        r6 = java.lang.Integer.valueOf(r6);
        r7 = "chat_outBubble";
        r4.put(r7, r6);
        r2 = java.lang.Integer.valueOf(r2);
        r6 = "chat_outBubbleGradient";
        r4.put(r6, r2);
    L_0x005d:
        r2 = r4.entrySet();
        r2 = r2.iterator();
    L_0x0065:
        r6 = r2.hasNext();
        r7 = "\n";
        if (r6 == 0) goto L_0x00a3;
    L_0x006d:
        r6 = r2.next();
        r6 = (java.util.Map.Entry) r6;
        r8 = r6.getKey();
        r8 = (java.lang.String) r8;
        r9 = r1 instanceof android.graphics.drawable.BitmapDrawable;
        if (r9 != 0) goto L_0x007f;
    L_0x007d:
        if (r0 == 0) goto L_0x0090;
    L_0x007f:
        r9 = "chat_wallpaper";
        r9 = r9.equals(r8);
        if (r9 != 0) goto L_0x0065;
    L_0x0087:
        r9 = "chat_wallpaper_gradient_to";
        r9 = r9.equals(r8);
        if (r9 == 0) goto L_0x0090;
    L_0x008f:
        goto L_0x0065;
    L_0x0090:
        r5.append(r8);
        r8 = "=";
        r5.append(r8);
        r6 = r6.getValue();
        r5.append(r6);
        r5.append(r7);
        goto L_0x0065;
    L_0x00a3:
        r2 = 0;
        r6 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x01cd }
        r8 = r12.pathToFile;	 Catch:{ Exception -> 0x01cd }
        r6.<init>(r8);	 Catch:{ Exception -> 0x01cd }
        r2 = r5.length();	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        if (r2 != 0) goto L_0x00c0;
    L_0x00b1:
        r2 = r1 instanceof android.graphics.drawable.BitmapDrawable;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        if (r2 != 0) goto L_0x00c0;
    L_0x00b5:
        r2 = android.text.TextUtils.isEmpty(r0);	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        if (r2 == 0) goto L_0x00c0;
    L_0x00bb:
        r2 = 32;
        r5.append(r2);	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
    L_0x00c0:
        r2 = r5.toString();	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r2 = org.telegram.messenger.AndroidUtilities.getStringBytes(r2);	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r6.write(r2);	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r2 = android.text.TextUtils.isEmpty(r0);	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r5 = 1;
        r8 = 87;
        if (r2 != 0) goto L_0x0128;
    L_0x00d4:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r2.<init>();	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r9 = "WLS=";
        r2.append(r9);	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r2.append(r0);	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r2.append(r7);	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r2 = org.telegram.messenger.AndroidUtilities.getStringBytes(r2);	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r6.write(r2);	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        if (r14 == 0) goto L_0x016b;
    L_0x00f1:
        r1 = (android.graphics.drawable.BitmapDrawable) r1;	 Catch:{ all -> 0x0123 }
        r14 = r1.getBitmap();	 Catch:{ all -> 0x0123 }
        r1 = new java.io.FileOutputStream;	 Catch:{ all -> 0x0123 }
        r2 = new java.io.File;	 Catch:{ all -> 0x0123 }
        r7 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();	 Catch:{ all -> 0x0123 }
        r9 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0123 }
        r9.<init>();	 Catch:{ all -> 0x0123 }
        r0 = org.telegram.messenger.Utilities.MD5(r0);	 Catch:{ all -> 0x0123 }
        r9.append(r0);	 Catch:{ all -> 0x0123 }
        r0 = ".wp";
        r9.append(r0);	 Catch:{ all -> 0x0123 }
        r0 = r9.toString();	 Catch:{ all -> 0x0123 }
        r2.<init>(r7, r0);	 Catch:{ all -> 0x0123 }
        r1.<init>(r2);	 Catch:{ all -> 0x0123 }
        r0 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ all -> 0x0123 }
        r14.compress(r0, r8, r1);	 Catch:{ all -> 0x0123 }
        r1.close();	 Catch:{ all -> 0x0123 }
        goto L_0x016b;
    L_0x0123:
        r14 = move-exception;
        org.telegram.messenger.FileLog.e(r14);	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        goto L_0x016b;
    L_0x0128:
        r14 = r1 instanceof android.graphics.drawable.BitmapDrawable;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        if (r14 == 0) goto L_0x016b;
    L_0x012c:
        r14 = r1;
        r14 = (android.graphics.drawable.BitmapDrawable) r14;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r14 = r14.getBitmap();	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r0 = 2;
        if (r14 == 0) goto L_0x0162;
    L_0x0136:
        r2 = 4;
        r7 = new byte[r2];	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r7[r3] = r8;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r9 = 80;
        r7[r5] = r9;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r10 = 83;
        r7[r0] = r10;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r10 = 3;
        r11 = 10;
        r7[r10] = r11;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r6.write(r7);	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r7 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r14.compress(r7, r8, r6);	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r14 = 5;
        r14 = new byte[r14];	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r14[r3] = r11;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r14[r5] = r8;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r14[r0] = r9;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r7 = 69;
        r14[r10] = r7;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r14[r2] = r11;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r6.write(r14);	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
    L_0x0162:
        if (r13 == 0) goto L_0x016b;
    L_0x0164:
        if (r15 != 0) goto L_0x016b;
    L_0x0166:
        wallpaper = r1;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        calcBackgroundColor(r1, r0);	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
    L_0x016b:
        if (r15 != 0) goto L_0x01c1;
    L_0x016d:
        r14 = themesDict;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r15 = r12.getKey();	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r14 = r14.get(r15);	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        if (r14 != 0) goto L_0x0192;
    L_0x0179:
        r14 = themes;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r14.add(r12);	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r14 = themesDict;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r15 = r12.getKey();	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r14.put(r15, r12);	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r14 = otherThemes;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r14.add(r12);	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        saveOtherThemes(r5);	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        sortThemes();	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
    L_0x0192:
        currentTheme = r12;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r14 = currentTheme;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r15 = currentNightTheme;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        if (r14 == r15) goto L_0x019e;
    L_0x019a:
        r14 = currentTheme;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        currentDayTheme = r14;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
    L_0x019e:
        r14 = defaultColors;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        if (r4 != r14) goto L_0x01aa;
    L_0x01a2:
        r14 = currentColorsNoAccent;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r14.clear();	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        refreshThemeColors();	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
    L_0x01aa:
        r14 = org.telegram.messenger.MessagesController.getGlobalMainSettings();	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r14 = r14.edit();	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r15 = "theme";
        r0 = currentDayTheme;	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r0 = r0.getKey();	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r14.putString(r15, r0);	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
        r14.commit();	 Catch:{ Exception -> 0x01c7, all -> 0x01c5 }
    L_0x01c1:
        r6.close();	 Catch:{ Exception -> 0x01d7 }
        goto L_0x01db;
    L_0x01c5:
        r12 = move-exception;
        goto L_0x01eb;
    L_0x01c7:
        r14 = move-exception;
        r2 = r6;
        goto L_0x01ce;
    L_0x01ca:
        r12 = move-exception;
        r6 = r2;
        goto L_0x01eb;
    L_0x01cd:
        r14 = move-exception;
    L_0x01ce:
        org.telegram.messenger.FileLog.e(r14);	 Catch:{ all -> 0x01ca }
        if (r2 == 0) goto L_0x01db;
    L_0x01d3:
        r2.close();	 Catch:{ Exception -> 0x01d7 }
        goto L_0x01db;
    L_0x01d7:
        r14 = move-exception;
        org.telegram.messenger.FileLog.e(r14);
    L_0x01db:
        if (r13 == 0) goto L_0x01ea;
    L_0x01dd:
        r13 = r12.account;
        r13 = org.telegram.messenger.MessagesController.getInstance(r13);
        r14 = r12.getAccent(r3);
        r13.saveThemeToServer(r12, r14);
    L_0x01ea:
        return;
    L_0x01eb:
        if (r6 == 0) goto L_0x01f5;
    L_0x01ed:
        r6.close();	 Catch:{ Exception -> 0x01f1 }
        goto L_0x01f5;
    L_0x01f1:
        r13 = move-exception;
        org.telegram.messenger.FileLog.e(r13);
    L_0x01f5:
        goto L_0x01f7;
    L_0x01f6:
        throw r12;
    L_0x01f7:
        goto L_0x01f6;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.saveCurrentTheme(org.telegram.ui.ActionBar.Theme$ThemeInfo, boolean, boolean, boolean):void");
    }

    public static void checkCurrentRemoteTheme(boolean z) {
        if (loadingCurrentTheme != 0) {
            return;
        }
        if (z || Math.abs((System.currentTimeMillis() / 1000) - ((long) lastLoadingCurrentThemeTime)) >= 3600) {
            int i = 0;
            while (i < 2) {
                ThemeInfo themeInfo = i == 0 ? currentDayTheme : currentNightTheme;
                if (themeInfo != null && UserConfig.getInstance(themeInfo.account).isClientActivated()) {
                    int i2;
                    ThemeAccent accent = themeInfo.getAccent(false);
                    TL_theme tL_theme = themeInfo.info;
                    if (tL_theme != null) {
                        i2 = themeInfo.account;
                    } else if (accent != null) {
                        tL_theme = accent.info;
                        if (tL_theme != null) {
                            i2 = UserConfig.selectedAccount;
                        }
                    }
                    if (!(tL_theme == null || tL_theme.document == null)) {
                        loadingCurrentTheme++;
                        TL_account_getTheme tL_account_getTheme = new TL_account_getTheme();
                        tL_account_getTheme.document_id = tL_theme.document.id;
                        tL_account_getTheme.format = "android";
                        TL_inputTheme tL_inputTheme = new TL_inputTheme();
                        tL_inputTheme.access_hash = tL_theme.access_hash;
                        tL_inputTheme.id = tL_theme.id;
                        tL_account_getTheme.theme = tL_inputTheme;
                        ConnectionsManager.getInstance(i2).sendRequest(tL_account_getTheme, new -$$Lambda$Theme$bHZHOZdIgRjDGDBY1nruG_xjQ3A(accent, themeInfo, tL_theme));
                    }
                }
                i++;
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:43:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0095  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0095  */
    /* JADX WARNING: Removed duplicated region for block: B:43:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Missing block: B:27:0x006f, code skipped:
            if (r7.motion != false) goto L_0x0073;
     */
    static /* synthetic */ void lambda$null$3(org.telegram.tgnet.TLObject r7, org.telegram.ui.ActionBar.Theme.ThemeAccent r8, org.telegram.ui.ActionBar.Theme.ThemeInfo r9, org.telegram.tgnet.TLRPC.TL_theme r10) {
        /*
        r0 = loadingCurrentTheme;
        r1 = 1;
        r0 = r0 - r1;
        loadingCurrentTheme = r0;
        r0 = r7 instanceof org.telegram.tgnet.TLRPC.TL_theme;
        r2 = 0;
        if (r0 == 0) goto L_0x0090;
    L_0x000b:
        r7 = (org.telegram.tgnet.TLRPC.TL_theme) r7;
        if (r8 == 0) goto L_0x0076;
    L_0x000f:
        r0 = r7.settings;
        if (r0 == 0) goto L_0x0076;
    L_0x0013:
        r10 = org.telegram.ui.ActionBar.Theme.ThemeInfo.accentEquals(r8, r0);
        if (r10 != 0) goto L_0x0062;
    L_0x0019:
        r10 = r8.getPathToWallpaper();
        if (r10 == 0) goto L_0x0022;
    L_0x001f:
        r10.delete();
    L_0x0022:
        r10 = r7.settings;
        org.telegram.ui.ActionBar.Theme.ThemeInfo.fillAccentValues(r8, r10);
        r10 = currentTheme;
        if (r10 != r9) goto L_0x005d;
    L_0x002b:
        r9 = r10.currentAccentId;
        r10 = r8.id;
        if (r9 != r10) goto L_0x005d;
    L_0x0031:
        refreshThemeColors();
        r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r10 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme;
        r0 = 4;
        r0 = new java.lang.Object[r0];
        r3 = currentTheme;
        r0[r2] = r3;
        r4 = currentNightTheme;
        if (r4 != r3) goto L_0x0047;
    L_0x0045:
        r3 = 1;
        goto L_0x0048;
    L_0x0047:
        r3 = 0;
    L_0x0048:
        r3 = java.lang.Boolean.valueOf(r3);
        r0[r1] = r3;
        r3 = 2;
        r4 = 0;
        r0[r3] = r4;
        r3 = 3;
        r4 = -1;
        r4 = java.lang.Integer.valueOf(r4);
        r0[r3] = r4;
        r9.postNotificationName(r10, r0);
    L_0x005d:
        org.telegram.ui.ActionBar.Theme.PatternsLoader.createLoader(r1);
        r9 = 1;
        goto L_0x0063;
    L_0x0062:
        r9 = 0;
    L_0x0063:
        r7 = r7.settings;
        r7 = r7.wallpaper;
        if (r7 == 0) goto L_0x0072;
    L_0x0069:
        r7 = r7.settings;
        if (r7 == 0) goto L_0x0072;
    L_0x006d:
        r7 = r7.motion;
        if (r7 == 0) goto L_0x0072;
    L_0x0071:
        goto L_0x0073;
    L_0x0072:
        r1 = 0;
    L_0x0073:
        r8.patternMotion = r1;
        goto L_0x0091;
    L_0x0076:
        r0 = r7.document;
        if (r0 == 0) goto L_0x0090;
    L_0x007a:
        r3 = r0.id;
        r10 = r10.document;
        r5 = r10.id;
        r10 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r10 == 0) goto L_0x0090;
    L_0x0084:
        if (r8 == 0) goto L_0x0089;
    L_0x0086:
        r8.info = r7;
        goto L_0x008e;
    L_0x0089:
        r9.info = r7;
        r9.loadThemeDocument();
    L_0x008e:
        r9 = 1;
        goto L_0x0091;
    L_0x0090:
        r9 = 0;
    L_0x0091:
        r7 = loadingCurrentTheme;
        if (r7 != 0) goto L_0x00a2;
    L_0x0095:
        r7 = java.lang.System.currentTimeMillis();
        r0 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r7 = r7 / r0;
        r8 = (int) r7;
        lastLoadingCurrentThemeTime = r8;
        saveOtherThemes(r9);
    L_0x00a2:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.lambda$null$3(org.telegram.tgnet.TLObject, org.telegram.ui.ActionBar.Theme$ThemeAccent, org.telegram.ui.ActionBar.Theme$ThemeInfo, org.telegram.tgnet.TLRPC$TL_theme):void");
    }

    public static void loadRemoteThemes(int i, boolean z) {
        if (!loadingRemoteThemes[i]) {
            if ((z || Math.abs((System.currentTimeMillis() / 1000) - ((long) lastLoadingThemesTime[i])) >= 3600) && UserConfig.getInstance(i).isClientActivated()) {
                loadingRemoteThemes[i] = true;
                TL_account_getThemes tL_account_getThemes = new TL_account_getThemes();
                tL_account_getThemes.format = "android";
                tL_account_getThemes.hash = remoteThemesHash[i];
                ConnectionsManager.getInstance(i).sendRequest(tL_account_getThemes, new -$$Lambda$Theme$axfOWeRl7QketJxdsxdMlUbAw4w(i));
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:84:0x01db  */
    /* JADX WARNING: Missing block: B:56:0x0107, code skipped:
            if (r8.motion != false) goto L_0x010b;
     */
    static /* synthetic */ void lambda$null$5(int r16, org.telegram.tgnet.TLObject r17) {
        /*
        r0 = r16;
        r1 = r17;
        r2 = loadingRemoteThemes;
        r3 = 0;
        r2[r0] = r3;
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_account_themes;
        if (r2 == 0) goto L_0x0244;
    L_0x000d:
        r1 = (org.telegram.tgnet.TLRPC.TL_account_themes) r1;
        r2 = remoteThemesHash;
        r4 = r1.hash;
        r2[r0] = r4;
        r2 = lastLoadingThemesTime;
        r4 = java.lang.System.currentTimeMillis();
        r6 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r4 = r4 / r6;
        r5 = (int) r4;
        r2[r0] = r5;
        r2 = new java.util.ArrayList;
        r2.<init>();
        r4 = themes;
        r4 = r4.size();
        r5 = 0;
    L_0x002d:
        if (r5 >= r4) goto L_0x0069;
    L_0x002f:
        r6 = themes;
        r6 = r6.get(r5);
        r6 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r6;
        r7 = r6.info;
        if (r7 == 0) goto L_0x0043;
    L_0x003b:
        r7 = r6.account;
        if (r7 != r0) goto L_0x0043;
    L_0x003f:
        r2.add(r6);
        goto L_0x0066;
    L_0x0043:
        r7 = r6.themeAccents;
        if (r7 == 0) goto L_0x0066;
    L_0x0047:
        r7 = 0;
    L_0x0048:
        r8 = r6.themeAccents;
        r8 = r8.size();
        if (r7 >= r8) goto L_0x0066;
    L_0x0050:
        r8 = r6.themeAccents;
        r8 = r8.get(r7);
        r8 = (org.telegram.ui.ActionBar.Theme.ThemeAccent) r8;
        r9 = r8.info;
        if (r9 == 0) goto L_0x0063;
    L_0x005c:
        r9 = r8.account;
        if (r9 != r0) goto L_0x0063;
    L_0x0060:
        r2.add(r8);
    L_0x0063:
        r7 = r7 + 1;
        goto L_0x0048;
    L_0x0066:
        r5 = r5 + 1;
        goto L_0x002d;
    L_0x0069:
        r4 = r1.themes;
        r4 = r4.size();
        r5 = 0;
        r6 = 0;
        r7 = 0;
    L_0x0072:
        r8 = -1;
        r12 = 4;
        r13 = 1;
        if (r5 >= r4) goto L_0x0185;
    L_0x0077:
        r14 = r1.themes;
        r14 = r14.get(r5);
        r14 = (org.telegram.tgnet.TLRPC.Theme) r14;
        r15 = r14 instanceof org.telegram.tgnet.TLRPC.TL_theme;
        if (r15 != 0) goto L_0x0085;
    L_0x0083:
        goto L_0x0181;
    L_0x0085:
        r14 = (org.telegram.tgnet.TLRPC.TL_theme) r14;
        r15 = r14.settings;
        if (r15 == 0) goto L_0x011f;
    L_0x008b:
        r15 = getBaseThemeKey(r15);
        if (r15 != 0) goto L_0x0093;
    L_0x0091:
        goto L_0x0181;
    L_0x0093:
        r9 = themesDict;
        r9 = r9.get(r15);
        r9 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r9;
        if (r9 == 0) goto L_0x0181;
    L_0x009d:
        r15 = r9.themeAccents;
        if (r15 != 0) goto L_0x00a3;
    L_0x00a1:
        goto L_0x0181;
    L_0x00a3:
        r15 = r9.accentsByThemeId;
        r10 = r14.id;
        r10 = r15.get(r10);
        r10 = (org.telegram.ui.ActionBar.Theme.ThemeAccent) r10;
        if (r10 == 0) goto L_0x0111;
    L_0x00af:
        r11 = r14.settings;
        r11 = org.telegram.ui.ActionBar.Theme.ThemeInfo.accentEquals(r10, r11);
        if (r11 != 0) goto L_0x00fb;
    L_0x00b7:
        r6 = r10.getPathToWallpaper();
        if (r6 == 0) goto L_0x00c0;
    L_0x00bd:
        r6.delete();
    L_0x00c0:
        r6 = r14.settings;
        org.telegram.ui.ActionBar.Theme.ThemeInfo.fillAccentValues(r10, r6);
        r6 = currentTheme;
        if (r6 != r9) goto L_0x00f9;
    L_0x00c9:
        r6 = r6.currentAccentId;
        r7 = r10.id;
        if (r6 != r7) goto L_0x00f9;
    L_0x00cf:
        refreshThemeColors();
        r6 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r7 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme;
        r9 = new java.lang.Object[r12];
        r11 = currentTheme;
        r9[r3] = r11;
        r12 = currentNightTheme;
        if (r12 != r11) goto L_0x00e4;
    L_0x00e2:
        r11 = 1;
        goto L_0x00e5;
    L_0x00e4:
        r11 = 0;
    L_0x00e5:
        r11 = java.lang.Boolean.valueOf(r11);
        r9[r13] = r11;
        r11 = 0;
        r12 = 2;
        r9[r12] = r11;
        r8 = java.lang.Integer.valueOf(r8);
        r11 = 3;
        r9[r11] = r8;
        r6.postNotificationName(r7, r9);
    L_0x00f9:
        r6 = 1;
        r7 = 1;
    L_0x00fb:
        r8 = r14.settings;
        r8 = r8.wallpaper;
        if (r8 == 0) goto L_0x010a;
    L_0x0101:
        r8 = r8.settings;
        if (r8 == 0) goto L_0x010a;
    L_0x0105:
        r8 = r8.motion;
        if (r8 == 0) goto L_0x010a;
    L_0x0109:
        goto L_0x010b;
    L_0x010a:
        r13 = 0;
    L_0x010b:
        r10.patternMotion = r13;
        r2.remove(r10);
        goto L_0x0181;
    L_0x0111:
        r8 = r9.createNewAccent(r14, r0);
        r8 = r8.patternSlug;
        r8 = android.text.TextUtils.isEmpty(r8);
        if (r8 != 0) goto L_0x0181;
    L_0x011d:
        r6 = 1;
        goto L_0x0181;
    L_0x011f:
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r9 = "remote";
        r8.append(r9);
        r9 = r14.id;
        r8.append(r9);
        r8 = r8.toString();
        r9 = themesDict;
        r9 = r9.get(r8);
        r9 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r9;
        if (r9 != 0) goto L_0x016f;
    L_0x013c:
        r9 = new org.telegram.ui.ActionBar.Theme$ThemeInfo;
        r9.<init>();
        r9.account = r0;
        r7 = new java.io.File;
        r10 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();
        r11 = new java.lang.StringBuilder;
        r11.<init>();
        r11.append(r8);
        r8 = ".attheme";
        r11.append(r8);
        r8 = r11.toString();
        r7.<init>(r10, r8);
        r7 = r7.getAbsolutePath();
        r9.pathToFile = r7;
        r7 = themes;
        r7.add(r9);
        r7 = otherThemes;
        r7.add(r9);
        r7 = 1;
        goto L_0x0172;
    L_0x016f:
        r2.remove(r9);
    L_0x0172:
        r8 = r14.title;
        r9.name = r8;
        r9.info = r14;
        r8 = themesDict;
        r10 = r9.getKey();
        r8.put(r10, r9);
    L_0x0181:
        r5 = r5 + 1;
        goto L_0x0072;
    L_0x0185:
        r0 = r2.size();
        r1 = 0;
    L_0x018a:
        if (r1 >= r0) goto L_0x022c;
    L_0x018c:
        r4 = r2.get(r1);
        r5 = r4 instanceof org.telegram.ui.ActionBar.Theme.ThemeInfo;
        if (r5 == 0) goto L_0x01e6;
    L_0x0194:
        r4 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r4;
        r4.removeObservers();
        r5 = otherThemes;
        r5.remove(r4);
        r5 = themesDict;
        r9 = r4.name;
        r5.remove(r9);
        r5 = r4.overrideWallpaper;
        if (r5 == 0) goto L_0x01ac;
    L_0x01a9:
        r5.delete();
    L_0x01ac:
        r5 = themes;
        r5.remove(r4);
        r5 = new java.io.File;
        r9 = r4.pathToFile;
        r5.<init>(r9);
        r5.delete();
        r5 = currentDayTheme;
        if (r5 != r4) goto L_0x01c4;
    L_0x01bf:
        r5 = defaultTheme;
        currentDayTheme = r5;
        goto L_0x01d6;
    L_0x01c4:
        r5 = currentNightTheme;
        if (r5 != r4) goto L_0x01d6;
    L_0x01c8:
        r5 = themesDict;
        r9 = "Dark Blue";
        r5 = r5.get(r9);
        r5 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r5;
        currentNightTheme = r5;
        r5 = 1;
        goto L_0x01d7;
    L_0x01d6:
        r5 = 0;
    L_0x01d7:
        r9 = currentTheme;
        if (r9 != r4) goto L_0x0225;
    L_0x01db:
        if (r5 == 0) goto L_0x01e0;
    L_0x01dd:
        r4 = currentNightTheme;
        goto L_0x01e2;
    L_0x01e0:
        r4 = currentDayTheme;
    L_0x01e2:
        applyTheme(r4, r13, r3, r5);
        goto L_0x0225;
    L_0x01e6:
        r5 = r4 instanceof org.telegram.ui.ActionBar.Theme.ThemeAccent;
        if (r5 == 0) goto L_0x0225;
    L_0x01ea:
        r4 = (org.telegram.ui.ActionBar.Theme.ThemeAccent) r4;
        r5 = r4.parentTheme;
        r5 = deleteThemeAccent(r5, r4, r3);
        if (r5 == 0) goto L_0x0225;
    L_0x01f4:
        r5 = currentTheme;
        r4 = r4.parentTheme;
        if (r5 != r4) goto L_0x0225;
    L_0x01fa:
        refreshThemeColors();
        r4 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r5 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme;
        r9 = new java.lang.Object[r12];
        r10 = currentTheme;
        r9[r3] = r10;
        r11 = currentNightTheme;
        if (r11 != r10) goto L_0x020f;
    L_0x020d:
        r10 = 1;
        goto L_0x0210;
    L_0x020f:
        r10 = 0;
    L_0x0210:
        r10 = java.lang.Boolean.valueOf(r10);
        r9[r13] = r10;
        r10 = 0;
        r11 = 2;
        r9[r11] = r10;
        r14 = java.lang.Integer.valueOf(r8);
        r15 = 3;
        r9[r15] = r14;
        r4.postNotificationName(r5, r9);
        goto L_0x0228;
    L_0x0225:
        r10 = 0;
        r11 = 2;
        r15 = 3;
    L_0x0228:
        r1 = r1 + 1;
        goto L_0x018a;
    L_0x022c:
        saveOtherThemes(r13);
        sortThemes();
        if (r7 == 0) goto L_0x023f;
    L_0x0234:
        r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r1 = org.telegram.messenger.NotificationCenter.themeListUpdated;
        r2 = new java.lang.Object[r3];
        r0.postNotificationName(r1, r2);
    L_0x023f:
        if (r6 == 0) goto L_0x0244;
    L_0x0241:
        org.telegram.ui.ActionBar.Theme.PatternsLoader.createLoader(r13);
    L_0x0244:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.lambda$null$5(int, org.telegram.tgnet.TLObject):void");
    }

    public static String getBaseThemeKey(TL_themeSettings tL_themeSettings) {
        BaseTheme baseTheme = tL_themeSettings.base_theme;
        if (baseTheme instanceof TL_baseThemeClassic) {
            return "Blue";
        }
        if (baseTheme instanceof TL_baseThemeDay) {
            return "Day";
        }
        if (baseTheme instanceof TL_baseThemeTinted) {
            return "Dark Blue";
        }
        if (baseTheme instanceof TL_baseThemeArctic) {
            return "Arctic Blue";
        }
        return baseTheme instanceof TL_baseThemeNight ? "Night" : null;
    }

    public static BaseTheme getBaseThemeByKey(String str) {
        if ("Blue".equals(str)) {
            return new TL_baseThemeClassic();
        }
        if ("Day".equals(str)) {
            return new TL_baseThemeDay();
        }
        if ("Dark Blue".equals(str)) {
            return new TL_baseThemeTinted();
        }
        if ("Arctic Blue".equals(str)) {
            return new TL_baseThemeArctic();
        }
        return "Night".equals(str) ? new TL_baseThemeNight() : null;
    }

    public static void setThemeFileReference(TL_theme tL_theme) {
        int size = themes.size();
        int i = 0;
        while (i < size) {
            TL_theme tL_theme2 = ((ThemeInfo) themes.get(i)).info;
            if (tL_theme2 == null || tL_theme2.id != tL_theme.id) {
                i++;
            } else {
                Document document = tL_theme2.document;
                if (document != null) {
                    Document document2 = tL_theme.document;
                    if (document2 != null) {
                        document.file_reference = document2.file_reference;
                        saveOtherThemes(true);
                        return;
                    }
                    return;
                }
                return;
            }
        }
    }

    public static boolean isThemeInstalled(ThemeInfo themeInfo) {
        return (themeInfo == null || themesDict.get(themeInfo.getKey()) == null) ? false : true;
    }

    public static void setThemeUploadInfo(ThemeInfo themeInfo, ThemeAccent themeAccent, TL_theme tL_theme, int i, boolean z) {
        if (tL_theme != null) {
            TL_themeSettings tL_themeSettings = tL_theme.settings;
            if (tL_themeSettings != null) {
                if (themeInfo == null) {
                    String baseThemeKey = getBaseThemeKey(tL_themeSettings);
                    if (baseThemeKey != null) {
                        themeInfo = (ThemeInfo) themesDict.get(baseThemeKey);
                        if (themeInfo != null) {
                            themeAccent = (ThemeAccent) themeInfo.accentsByThemeId.get(tL_theme.id);
                        } else {
                            return;
                        }
                    }
                    return;
                }
                if (themeAccent != null) {
                    boolean z2;
                    TL_theme tL_theme2 = themeAccent.info;
                    if (tL_theme2 != null) {
                        themeInfo.accentsByThemeId.remove(tL_theme2.id);
                    }
                    themeAccent.info = tL_theme;
                    themeAccent.account = i;
                    themeInfo.accentsByThemeId.put(tL_theme.id, themeAccent);
                    if (!ThemeInfo.accentEquals(themeAccent, tL_theme.settings)) {
                        File pathToWallpaper = themeAccent.getPathToWallpaper();
                        if (pathToWallpaper != null) {
                            pathToWallpaper.delete();
                        }
                        ThemeInfo.fillAccentValues(themeAccent, tL_theme.settings);
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
                            objArr[3] = Integer.valueOf(-1);
                            globalInstance.postNotificationName(i2, objArr);
                        }
                        PatternsLoader.createLoader(true);
                    }
                    WallPaper wallPaper = tL_theme.settings.wallpaper;
                    if (wallPaper != null) {
                        WallPaperSettings wallPaperSettings = wallPaper.settings;
                        if (wallPaperSettings != null && wallPaperSettings.motion) {
                            z2 = true;
                            themeAccent.patternMotion = z2;
                            themeInfo.previewParsed = false;
                        }
                    }
                    z2 = false;
                    themeAccent.patternMotion = z2;
                    themeInfo.previewParsed = false;
                } else {
                    return;
                }
            }
            String key;
            if (themeInfo != null) {
                HashMap hashMap = themesDict;
                key = themeInfo.getKey();
                hashMap.remove(key);
            } else {
                HashMap hashMap2 = themesDict;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("remote");
                stringBuilder.append(tL_theme.id);
                key = stringBuilder.toString();
                themeInfo = (ThemeInfo) hashMap2.get(key);
            }
            if (themeInfo != null) {
                themeInfo.info = tL_theme;
                themeInfo.name = tL_theme.title;
                File file = new File(themeInfo.pathToFile);
                File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(key);
                stringBuilder2.append(".attheme");
                File file2 = new File(filesDirFixed, stringBuilder2.toString());
                if (!file.equals(file2)) {
                    try {
                        AndroidUtilities.copyFile(file, file2);
                        themeInfo.pathToFile = file2.getAbsolutePath();
                    } catch (Exception e) {
                        FileLog.e(e);
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
            saveOtherThemes(true);
        }
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:25:0x0050 */
    /* JADX WARNING: Missing block: B:22:0x004b, code skipped:
            if (r6 != null) goto L_0x004d;
     */
    /* JADX WARNING: Missing block: B:24:?, code skipped:
            r6.close();
     */
    public static java.io.File getAssetFile(java.lang.String r6) {
        /*
        r0 = new java.io.File;
        r1 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();
        r0.<init>(r1, r6);
        r1 = 0;
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x001e }
        r3 = r3.getAssets();	 Catch:{ Exception -> 0x001e }
        r3 = r3.open(r6);	 Catch:{ Exception -> 0x001e }
        r4 = r3.available();	 Catch:{ Exception -> 0x001e }
        r4 = (long) r4;	 Catch:{ Exception -> 0x001e }
        r3.close();	 Catch:{ Exception -> 0x001e }
        goto L_0x0023;
    L_0x001e:
        r3 = move-exception;
        org.telegram.messenger.FileLog.e(r3);
        r4 = r1;
    L_0x0023:
        r3 = r0.exists();
        if (r3 == 0) goto L_0x0035;
    L_0x0029:
        r3 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1));
        if (r3 == 0) goto L_0x0055;
    L_0x002d:
        r1 = r0.length();
        r3 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1));
        if (r3 == 0) goto L_0x0055;
    L_0x0035:
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0051 }
        r1 = r1.getAssets();	 Catch:{ Exception -> 0x0051 }
        r6 = r1.open(r6);	 Catch:{ Exception -> 0x0051 }
        org.telegram.messenger.AndroidUtilities.copyFile(r6, r0);	 Catch:{ all -> 0x0048 }
        if (r6 == 0) goto L_0x0055;
    L_0x0044:
        r6.close();	 Catch:{ Exception -> 0x0051 }
        goto L_0x0055;
    L_0x0048:
        r1 = move-exception;
        throw r1;	 Catch:{ all -> 0x004a }
    L_0x004a:
        r1 = move-exception;
        if (r6 == 0) goto L_0x0050;
    L_0x004d:
        r6.close();	 Catch:{ all -> 0x0050 }
    L_0x0050:
        throw r1;	 Catch:{ Exception -> 0x0051 }
    L_0x0051:
        r6 = move-exception;
        org.telegram.messenger.FileLog.e(r6);
    L_0x0055:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.getAssetFile(java.lang.String):java.io.File");
    }

    public static int getPreviewColor(HashMap<String, Integer> hashMap, String str) {
        Integer num = (Integer) hashMap.get(str);
        if (num == null) {
            num = (Integer) defaultColors.get(str);
        }
        return num.intValue();
    }

    /* JADX WARNING: Unknown top exception splitter block from list: {B:89:0x0343=Splitter:B:89:0x0343, B:27:0x01a8=Splitter:B:27:0x01a8} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x032c A:{SYNTHETIC, Splitter:B:74:0x032c} */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x0396 A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x035e A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x03c1 A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x03df A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x042f A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x043d A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x047a A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x049d A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x035e A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x0396 A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x03c1 A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x03df A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x042f A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x043d A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x047a A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x049d A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x0396 A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x035e A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x03c1 A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x03df A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x042f A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x043d A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x047a A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x049d A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x033d A:{SYNTHETIC, Splitter:B:85:0x033d} */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x035e A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x0396 A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x03c1 A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x03df A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x042f A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x043d A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x047a A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x049d A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x033d A:{SYNTHETIC, Splitter:B:85:0x033d} */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x0396 A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x035e A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x03c1 A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x03df A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x042f A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x043d A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x047a A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x049d A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x035e A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x0396 A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x03c1 A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x03df A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x042f A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x043d A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x047a A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x049d A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x0396 A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x035e A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x03c1 A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x03df A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x042f A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x043d A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x047a A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x049d A:{Catch:{ all -> 0x034a, all -> 0x0508 }} */
    public static java.lang.String createThemePreviewImage(java.lang.String r29, java.lang.String r30) {
        /*
        r0 = r29;
        r1 = r30;
        r2 = 0;
        r3 = 1;
        r4 = new java.lang.String[r3];	 Catch:{ all -> 0x0508 }
        r5 = new java.io.File;	 Catch:{ all -> 0x0508 }
        r5.<init>(r0);	 Catch:{ all -> 0x0508 }
        r5 = getThemeFileValues(r5, r2, r4);	 Catch:{ all -> 0x0508 }
        r6 = "wallpaperFileOffset";
        r6 = r5.get(r6);	 Catch:{ all -> 0x0508 }
        r6 = (java.lang.Integer) r6;	 Catch:{ all -> 0x0508 }
        r7 = 560; // 0x230 float:7.85E-43 double:2.767E-321;
        r8 = 678; // 0x2a6 float:9.5E-43 double:3.35E-321;
        r9 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ all -> 0x0508 }
        r7 = org.telegram.messenger.Bitmaps.createBitmap(r7, r8, r9);	 Catch:{ all -> 0x0508 }
        r14 = new android.graphics.Canvas;	 Catch:{ all -> 0x0508 }
        r14.<init>(r7);	 Catch:{ all -> 0x0508 }
        r15 = new android.graphics.Paint;	 Catch:{ all -> 0x0508 }
        r15.<init>();	 Catch:{ all -> 0x0508 }
        r8 = "actionBarDefault";
        r8 = getPreviewColor(r5, r8);	 Catch:{ all -> 0x0508 }
        r9 = "actionBarDefaultIcon";
        r9 = getPreviewColor(r5, r9);	 Catch:{ all -> 0x0508 }
        r10 = "chat_messagePanelBackground";
        r13 = getPreviewColor(r5, r10);	 Catch:{ all -> 0x0508 }
        r10 = "chat_messagePanelIcons";
        r10 = getPreviewColor(r5, r10);	 Catch:{ all -> 0x0508 }
        r11 = "chat_inBubble";
        r11 = getPreviewColor(r5, r11);	 Catch:{ all -> 0x0508 }
        r12 = "chat_outBubble";
        r12 = getPreviewColor(r5, r12);	 Catch:{ all -> 0x0508 }
        r2 = "chat_outBubbleGradient";
        r2 = r5.get(r2);	 Catch:{ all -> 0x0508 }
        r2 = (java.lang.Integer) r2;	 Catch:{ all -> 0x0508 }
        r2 = "chat_wallpaper";
        r2 = r5.get(r2);	 Catch:{ all -> 0x0508 }
        r2 = (java.lang.Integer) r2;	 Catch:{ all -> 0x0508 }
        r3 = "chat_serviceBackground";
        r3 = r5.get(r3);	 Catch:{ all -> 0x0508 }
        r3 = (java.lang.Integer) r3;	 Catch:{ all -> 0x0508 }
        r17 = r13;
        r13 = "chat_wallpaper_gradient_to";
        r13 = r5.get(r13);	 Catch:{ all -> 0x0508 }
        r13 = (java.lang.Integer) r13;	 Catch:{ all -> 0x0508 }
        r18 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0508 }
        r19 = r8;
        r8 = r18.getResources();	 Catch:{ all -> 0x0508 }
        r18 = r15;
        r15 = NUM; // 0x7var_d float:1.7945837E38 double:1.05293581E-314;
        r8 = r8.getDrawable(r15);	 Catch:{ all -> 0x0508 }
        r15 = r8.mutate();	 Catch:{ all -> 0x0508 }
        setDrawableColor(r15, r9);	 Catch:{ all -> 0x0508 }
        r8 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0508 }
        r8 = r8.getResources();	 Catch:{ all -> 0x0508 }
        r20 = r15;
        r15 = NUM; // 0x7var_f float:1.7945842E38 double:1.052935811E-314;
        r8 = r8.getDrawable(r15);	 Catch:{ all -> 0x0508 }
        r15 = r8.mutate();	 Catch:{ all -> 0x0508 }
        setDrawableColor(r15, r9);	 Catch:{ all -> 0x0508 }
        r8 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0508 }
        r8 = r8.getResources();	 Catch:{ all -> 0x0508 }
        r9 = NUM; // 0x7var_ float:1.794585E38 double:1.052935813E-314;
        r8 = r8.getDrawable(r9);	 Catch:{ all -> 0x0508 }
        r9 = r8.mutate();	 Catch:{ all -> 0x0508 }
        setDrawableColor(r9, r10);	 Catch:{ all -> 0x0508 }
        r8 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0508 }
        r8 = r8.getResources();	 Catch:{ all -> 0x0508 }
        r21 = r9;
        r9 = NUM; // 0x7var_ float:1.7945844E38 double:1.0529358113E-314;
        r8 = r8.getDrawable(r9);	 Catch:{ all -> 0x0508 }
        r9 = r8.mutate();	 Catch:{ all -> 0x0508 }
        setDrawableColor(r9, r10);	 Catch:{ all -> 0x0508 }
        r8 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0508 }
        r8 = r8.getResources();	 Catch:{ all -> 0x0508 }
        r10 = NUM; // 0x7var_ float:1.7945846E38 double:1.052935812E-314;
        r8 = r8.getDrawable(r10);	 Catch:{ all -> 0x0508 }
        r10 = r8.mutate();	 Catch:{ all -> 0x0508 }
        setDrawableColor(r10, r11);	 Catch:{ all -> 0x0508 }
        r11 = new org.telegram.ui.ActionBar.Theme$8;	 Catch:{ all -> 0x0508 }
        r8 = 2;
        r22 = r10;
        r10 = 0;
        r11.<init>(r8, r10, r5);	 Catch:{ all -> 0x0508 }
        setDrawableColor(r11, r12);	 Catch:{ all -> 0x0508 }
        r12 = new android.graphics.RectF;	 Catch:{ all -> 0x0508 }
        r12.<init>();	 Catch:{ all -> 0x0508 }
        r23 = 80;
        r24 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r25 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r10 = 0;
        r26 = NUM; // 0x440CLASSNAME float:560.0 double:5.64043681E-315;
        r27 = r9;
        r9 = 120; // 0x78 float:1.68E-43 double:5.93E-322;
        if (r1 == 0) goto L_0x01af;
    L_0x0100:
        r0 = new android.graphics.BitmapFactory$Options;	 Catch:{ all -> 0x01a5 }
        r0.<init>();	 Catch:{ all -> 0x01a5 }
        r2 = 1;
        r0.inJustDecodeBounds = r2;	 Catch:{ all -> 0x01a5 }
        android.graphics.BitmapFactory.decodeFile(r1, r0);	 Catch:{ all -> 0x01a5 }
        r2 = r0.outWidth;	 Catch:{ all -> 0x01a5 }
        if (r2 <= 0) goto L_0x01a2;
    L_0x010f:
        r2 = r0.outHeight;	 Catch:{ all -> 0x01a5 }
        if (r2 <= 0) goto L_0x01a2;
    L_0x0113:
        r2 = r0.outWidth;	 Catch:{ all -> 0x01a5 }
        r2 = (float) r2;	 Catch:{ all -> 0x01a5 }
        r2 = r2 / r26;
        r4 = r0.outHeight;	 Catch:{ all -> 0x01a5 }
        r4 = (float) r4;	 Catch:{ all -> 0x01a5 }
        r4 = r4 / r26;
        r2 = java.lang.Math.min(r2, r4);	 Catch:{ all -> 0x01a5 }
        r4 = 1;
        r0.inSampleSize = r4;	 Catch:{ all -> 0x01a5 }
        r4 = (r2 > r24 ? 1 : (r2 == r24 ? 0 : -1));
        if (r4 <= 0) goto L_0x0135;
    L_0x0128:
        r4 = r0.inSampleSize;	 Catch:{ all -> 0x01a5 }
        r4 = r4 * 2;
        r0.inSampleSize = r4;	 Catch:{ all -> 0x01a5 }
        r4 = r0.inSampleSize;	 Catch:{ all -> 0x01a5 }
        r4 = (float) r4;	 Catch:{ all -> 0x01a5 }
        r4 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1));
        if (r4 < 0) goto L_0x0128;
    L_0x0135:
        r2 = 0;
        r0.inJustDecodeBounds = r2;	 Catch:{ all -> 0x01a5 }
        r0 = android.graphics.BitmapFactory.decodeFile(r1, r0);	 Catch:{ all -> 0x01a5 }
        if (r0 == 0) goto L_0x01a2;
    L_0x013e:
        r1 = new android.graphics.Paint;	 Catch:{ all -> 0x01a5 }
        r1.<init>();	 Catch:{ all -> 0x01a5 }
        r2 = 1;
        r1.setFilterBitmap(r2);	 Catch:{ all -> 0x01a5 }
        r2 = r0.getWidth();	 Catch:{ all -> 0x01a5 }
        r2 = (float) r2;	 Catch:{ all -> 0x01a5 }
        r2 = r2 / r26;
        r4 = r0.getHeight();	 Catch:{ all -> 0x01a5 }
        r4 = (float) r4;	 Catch:{ all -> 0x01a5 }
        r4 = r4 / r26;
        r2 = java.lang.Math.min(r2, r4);	 Catch:{ all -> 0x01a5 }
        r4 = r0.getWidth();	 Catch:{ all -> 0x01a5 }
        r4 = (float) r4;	 Catch:{ all -> 0x01a5 }
        r4 = r4 / r2;
        r5 = r0.getHeight();	 Catch:{ all -> 0x01a5 }
        r5 = (float) r5;	 Catch:{ all -> 0x01a5 }
        r5 = r5 / r2;
        r12.set(r10, r10, r4, r5);	 Catch:{ all -> 0x01a5 }
        r2 = r7.getWidth();	 Catch:{ all -> 0x01a5 }
        r2 = (float) r2;	 Catch:{ all -> 0x01a5 }
        r4 = r12.width();	 Catch:{ all -> 0x01a5 }
        r2 = r2 - r4;
        r2 = r2 / r25;
        r4 = r7.getHeight();	 Catch:{ all -> 0x01a5 }
        r4 = (float) r4;	 Catch:{ all -> 0x01a5 }
        r5 = r12.height();	 Catch:{ all -> 0x01a5 }
        r4 = r4 - r5;
        r4 = r4 / r25;
        r12.offset(r2, r4);	 Catch:{ all -> 0x01a5 }
        r2 = 0;
        r14.drawBitmap(r0, r2, r12, r1);	 Catch:{ all -> 0x01a5 }
        if (r3 != 0) goto L_0x019f;
    L_0x0189:
        r1 = new android.graphics.drawable.BitmapDrawable;	 Catch:{ all -> 0x019b }
        r1.<init>(r0);	 Catch:{ all -> 0x019b }
        r0 = org.telegram.messenger.AndroidUtilities.calcDrawableColor(r1);	 Catch:{ all -> 0x019b }
        r1 = 0;
        r0 = r0[r1];	 Catch:{ all -> 0x019b }
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ all -> 0x019b }
        r3 = r0;
        goto L_0x019f;
    L_0x019b:
        r0 = move-exception;
        r16 = 1;
        goto L_0x01a8;
    L_0x019f:
        r16 = 1;
        goto L_0x01ab;
    L_0x01a2:
        r16 = 0;
        goto L_0x01ab;
    L_0x01a5:
        r0 = move-exception;
        r16 = 0;
    L_0x01a8:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0508 }
    L_0x01ab:
        r0 = 80;
        goto L_0x035c;
    L_0x01af:
        if (r2 == 0) goto L_0x021c;
    L_0x01b1:
        if (r13 != 0) goto L_0x01bd;
    L_0x01b3:
        r0 = new android.graphics.drawable.ColorDrawable;	 Catch:{ all -> 0x0508 }
        r1 = r2.intValue();	 Catch:{ all -> 0x0508 }
        r0.<init>(r1);	 Catch:{ all -> 0x0508 }
        goto L_0x01f0;
    L_0x01bd:
        r0 = "chat_wallpaper_gradient_rotation";
        r0 = r5.get(r0);	 Catch:{ all -> 0x0508 }
        r0 = (java.lang.Integer) r0;	 Catch:{ all -> 0x0508 }
        if (r0 != 0) goto L_0x01cd;
    L_0x01c7:
        r0 = 45;
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ all -> 0x0508 }
    L_0x01cd:
        r1 = new int[r8];	 Catch:{ all -> 0x0508 }
        r4 = r2.intValue();	 Catch:{ all -> 0x0508 }
        r5 = 0;
        r1[r5] = r4;	 Catch:{ all -> 0x0508 }
        r4 = r13.intValue();	 Catch:{ all -> 0x0508 }
        r5 = 1;
        r1[r5] = r4;	 Catch:{ all -> 0x0508 }
        r0 = r0.intValue();	 Catch:{ all -> 0x0508 }
        r4 = r7.getWidth();	 Catch:{ all -> 0x0508 }
        r5 = r7.getHeight();	 Catch:{ all -> 0x0508 }
        r5 = r5 - r9;
        r0 = org.telegram.ui.Components.BackgroundGradientDrawable.createDitheredGradientBitmapDrawable(r0, r1, r4, r5);	 Catch:{ all -> 0x0508 }
        r23 = 90;
    L_0x01f0:
        r1 = r7.getWidth();	 Catch:{ all -> 0x0508 }
        r4 = r7.getHeight();	 Catch:{ all -> 0x0508 }
        r4 = r4 - r9;
        r5 = 0;
        r0.setBounds(r5, r9, r1, r4);	 Catch:{ all -> 0x0508 }
        r0.draw(r14);	 Catch:{ all -> 0x0508 }
        if (r3 != 0) goto L_0x0216;
    L_0x0202:
        r0 = new android.graphics.drawable.ColorDrawable;	 Catch:{ all -> 0x0508 }
        r1 = r2.intValue();	 Catch:{ all -> 0x0508 }
        r0.<init>(r1);	 Catch:{ all -> 0x0508 }
        r0 = org.telegram.messenger.AndroidUtilities.calcDrawableColor(r0);	 Catch:{ all -> 0x0508 }
        r1 = 0;
        r0 = r0[r1];	 Catch:{ all -> 0x0508 }
        r3 = java.lang.Integer.valueOf(r0);	 Catch:{ all -> 0x0508 }
    L_0x0216:
        r0 = r23;
        r16 = 1;
        goto L_0x035c;
    L_0x021c:
        if (r6 == 0) goto L_0x0224;
    L_0x021e:
        r1 = r6.intValue();	 Catch:{ all -> 0x0508 }
        if (r1 >= 0) goto L_0x022d;
    L_0x0224:
        r1 = 0;
        r2 = r4[r1];	 Catch:{ all -> 0x0508 }
        r1 = android.text.TextUtils.isEmpty(r2);	 Catch:{ all -> 0x0508 }
        if (r1 != 0) goto L_0x0358;
    L_0x022d:
        r1 = new android.graphics.BitmapFactory$Options;	 Catch:{ all -> 0x0335 }
        r1.<init>();	 Catch:{ all -> 0x0335 }
        r2 = 1;
        r1.inJustDecodeBounds = r2;	 Catch:{ all -> 0x0335 }
        r2 = 0;
        r5 = r4[r2];	 Catch:{ all -> 0x0335 }
        r2 = android.text.TextUtils.isEmpty(r5);	 Catch:{ all -> 0x0335 }
        if (r2 != 0) goto L_0x0269;
    L_0x023e:
        r2 = new java.io.File;	 Catch:{ all -> 0x0335 }
        r0 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();	 Catch:{ all -> 0x0335 }
        r5 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0335 }
        r5.<init>();	 Catch:{ all -> 0x0335 }
        r13 = 0;
        r4 = r4[r13];	 Catch:{ all -> 0x0335 }
        r4 = org.telegram.messenger.Utilities.MD5(r4);	 Catch:{ all -> 0x0335 }
        r5.append(r4);	 Catch:{ all -> 0x0335 }
        r4 = ".wp";
        r5.append(r4);	 Catch:{ all -> 0x0335 }
        r4 = r5.toString();	 Catch:{ all -> 0x0335 }
        r2.<init>(r0, r4);	 Catch:{ all -> 0x0335 }
        r0 = r2.getAbsolutePath();	 Catch:{ all -> 0x0335 }
        android.graphics.BitmapFactory.decodeFile(r0, r1);	 Catch:{ all -> 0x0335 }
        r0 = r2;
        r2 = 0;
        goto L_0x027f;
    L_0x0269:
        r2 = new java.io.FileInputStream;	 Catch:{ all -> 0x0335 }
        r2.<init>(r0);	 Catch:{ all -> 0x0335 }
        r0 = r2.getChannel();	 Catch:{ all -> 0x0333 }
        r4 = r6.intValue();	 Catch:{ all -> 0x0333 }
        r4 = (long) r4;	 Catch:{ all -> 0x0333 }
        r0.position(r4);	 Catch:{ all -> 0x0333 }
        r4 = 0;
        android.graphics.BitmapFactory.decodeStream(r2, r4, r1);	 Catch:{ all -> 0x0333 }
        r0 = 0;
    L_0x027f:
        r4 = r1.outWidth;	 Catch:{ all -> 0x0333 }
        if (r4 <= 0) goto L_0x0329;
    L_0x0283:
        r4 = r1.outHeight;	 Catch:{ all -> 0x0333 }
        if (r4 <= 0) goto L_0x0329;
    L_0x0287:
        r4 = r1.outWidth;	 Catch:{ all -> 0x0333 }
        r4 = (float) r4;	 Catch:{ all -> 0x0333 }
        r4 = r4 / r26;
        r5 = r1.outHeight;	 Catch:{ all -> 0x0333 }
        r5 = (float) r5;	 Catch:{ all -> 0x0333 }
        r5 = r5 / r26;
        r4 = java.lang.Math.min(r4, r5);	 Catch:{ all -> 0x0333 }
        r5 = 1;
        r1.inSampleSize = r5;	 Catch:{ all -> 0x0333 }
        r5 = (r4 > r24 ? 1 : (r4 == r24 ? 0 : -1));
        if (r5 <= 0) goto L_0x02a9;
    L_0x029c:
        r5 = r1.inSampleSize;	 Catch:{ all -> 0x0333 }
        r5 = r5 * 2;
        r1.inSampleSize = r5;	 Catch:{ all -> 0x0333 }
        r5 = r1.inSampleSize;	 Catch:{ all -> 0x0333 }
        r5 = (float) r5;	 Catch:{ all -> 0x0333 }
        r5 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1));
        if (r5 < 0) goto L_0x029c;
    L_0x02a9:
        r4 = 0;
        r1.inJustDecodeBounds = r4;	 Catch:{ all -> 0x0333 }
        if (r0 == 0) goto L_0x02b7;
    L_0x02ae:
        r0 = r0.getAbsolutePath();	 Catch:{ all -> 0x0333 }
        r0 = android.graphics.BitmapFactory.decodeFile(r0, r1);	 Catch:{ all -> 0x0333 }
        goto L_0x02c8;
    L_0x02b7:
        r0 = r2.getChannel();	 Catch:{ all -> 0x0333 }
        r4 = r6.intValue();	 Catch:{ all -> 0x0333 }
        r4 = (long) r4;	 Catch:{ all -> 0x0333 }
        r0.position(r4);	 Catch:{ all -> 0x0333 }
        r4 = 0;
        r0 = android.graphics.BitmapFactory.decodeStream(r2, r4, r1);	 Catch:{ all -> 0x0333 }
    L_0x02c8:
        if (r0 == 0) goto L_0x0329;
    L_0x02ca:
        r1 = new android.graphics.Paint;	 Catch:{ all -> 0x0333 }
        r1.<init>();	 Catch:{ all -> 0x0333 }
        r4 = 1;
        r1.setFilterBitmap(r4);	 Catch:{ all -> 0x0333 }
        r5 = r0.getWidth();	 Catch:{ all -> 0x0333 }
        r5 = (float) r5;	 Catch:{ all -> 0x0333 }
        r5 = r5 / r26;
        r6 = r0.getHeight();	 Catch:{ all -> 0x0333 }
        r6 = (float) r6;	 Catch:{ all -> 0x0333 }
        r6 = r6 / r26;
        r5 = java.lang.Math.min(r5, r6);	 Catch:{ all -> 0x0333 }
        r6 = r0.getWidth();	 Catch:{ all -> 0x0333 }
        r6 = (float) r6;	 Catch:{ all -> 0x0333 }
        r6 = r6 / r5;
        r13 = r0.getHeight();	 Catch:{ all -> 0x0333 }
        r13 = (float) r13;	 Catch:{ all -> 0x0333 }
        r13 = r13 / r5;
        r12.set(r10, r10, r6, r13);	 Catch:{ all -> 0x0333 }
        r5 = r7.getWidth();	 Catch:{ all -> 0x0333 }
        r5 = (float) r5;	 Catch:{ all -> 0x0333 }
        r6 = r12.width();	 Catch:{ all -> 0x0333 }
        r5 = r5 - r6;
        r5 = r5 / r25;
        r6 = r7.getHeight();	 Catch:{ all -> 0x0333 }
        r6 = (float) r6;	 Catch:{ all -> 0x0333 }
        r10 = r12.height();	 Catch:{ all -> 0x0333 }
        r6 = r6 - r10;
        r6 = r6 / r25;
        r12.offset(r5, r6);	 Catch:{ all -> 0x0333 }
        r5 = 0;
        r14.drawBitmap(r0, r5, r12, r1);	 Catch:{ all -> 0x0333 }
        if (r3 != 0) goto L_0x032a;
    L_0x0315:
        r1 = new android.graphics.drawable.BitmapDrawable;	 Catch:{ all -> 0x0327 }
        r1.<init>(r0);	 Catch:{ all -> 0x0327 }
        r0 = org.telegram.messenger.AndroidUtilities.calcDrawableColor(r1);	 Catch:{ all -> 0x0327 }
        r1 = 0;
        r0 = r0[r1];	 Catch:{ all -> 0x0327 }
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ all -> 0x0327 }
        r3 = r0;
        goto L_0x032a;
    L_0x0327:
        r0 = move-exception;
        goto L_0x0338;
    L_0x0329:
        r4 = 0;
    L_0x032a:
        if (r2 == 0) goto L_0x0346;
    L_0x032c:
        r2.close();	 Catch:{ Exception -> 0x0330 }
        goto L_0x0346;
    L_0x0330:
        r0 = move-exception;
        r1 = r0;
        goto L_0x0343;
    L_0x0333:
        r0 = move-exception;
        goto L_0x0337;
    L_0x0335:
        r0 = move-exception;
        r2 = 0;
    L_0x0337:
        r4 = 0;
    L_0x0338:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x034a }
        if (r2 == 0) goto L_0x0346;
    L_0x033d:
        r2.close();	 Catch:{ Exception -> 0x0341 }
        goto L_0x0346;
    L_0x0341:
        r0 = move-exception;
        r1 = r0;
    L_0x0343:
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ all -> 0x0508 }
    L_0x0346:
        r16 = r4;
        goto L_0x01ab;
    L_0x034a:
        r0 = move-exception;
        r1 = r0;
        if (r2 == 0) goto L_0x0357;
    L_0x034e:
        r2.close();	 Catch:{ Exception -> 0x0352 }
        goto L_0x0357;
    L_0x0352:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ all -> 0x0508 }
    L_0x0357:
        throw r1;	 Catch:{ all -> 0x0508 }
    L_0x0358:
        r0 = 80;
        r16 = 0;
    L_0x035c:
        if (r16 != 0) goto L_0x0396;
    L_0x035e:
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0508 }
        r1 = r1.getResources();	 Catch:{ all -> 0x0508 }
        r2 = NUM; // 0x7var_ float:1.794484E38 double:1.052935567E-314;
        r1 = r1.getDrawable(r2);	 Catch:{ all -> 0x0508 }
        r1 = r1.mutate();	 Catch:{ all -> 0x0508 }
        r1 = (android.graphics.drawable.BitmapDrawable) r1;	 Catch:{ all -> 0x0508 }
        if (r3 != 0) goto L_0x037e;
    L_0x0373:
        r2 = org.telegram.messenger.AndroidUtilities.calcDrawableColor(r1);	 Catch:{ all -> 0x0508 }
        r3 = 0;
        r2 = r2[r3];	 Catch:{ all -> 0x0508 }
        r3 = java.lang.Integer.valueOf(r2);	 Catch:{ all -> 0x0508 }
    L_0x037e:
        r2 = android.graphics.Shader.TileMode.REPEAT;	 Catch:{ all -> 0x0508 }
        r4 = android.graphics.Shader.TileMode.REPEAT;	 Catch:{ all -> 0x0508 }
        r1.setTileModeXY(r2, r4);	 Catch:{ all -> 0x0508 }
        r2 = r7.getWidth();	 Catch:{ all -> 0x0508 }
        r4 = r7.getHeight();	 Catch:{ all -> 0x0508 }
        r4 = r4 - r9;
        r5 = 0;
        r1.setBounds(r5, r9, r2, r4);	 Catch:{ all -> 0x0508 }
        r1.draw(r14);	 Catch:{ all -> 0x0508 }
        goto L_0x0397;
    L_0x0396:
        r5 = 0;
    L_0x0397:
        r1 = r18;
        r2 = r19;
        r1.setColor(r2);	 Catch:{ all -> 0x0508 }
        r2 = 0;
        r10 = 0;
        r4 = r7.getWidth();	 Catch:{ all -> 0x0508 }
        r4 = (float) r4;	 Catch:{ all -> 0x0508 }
        r6 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
        r16 = 2;
        r8 = r14;
        r13 = r21;
        r5 = r27;
        r18 = 120; // 0x78 float:1.68E-43 double:5.93E-322;
        r9 = r2;
        r2 = r22;
        r28 = r11;
        r11 = r4;
        r4 = r12;
        r12 = r6;
        r5 = r13;
        r6 = r17;
        r13 = r1;
        r8.drawRect(r9, r10, r11, r12, r13);	 Catch:{ all -> 0x0508 }
        if (r20 == 0) goto L_0x03dd;
    L_0x03c1:
        r8 = 13;
        r9 = r20.getIntrinsicHeight();	 Catch:{ all -> 0x0508 }
        r9 = 120 - r9;
        r9 = r9 / 2;
        r10 = r20.getIntrinsicWidth();	 Catch:{ all -> 0x0508 }
        r10 = r10 + r8;
        r11 = r20.getIntrinsicHeight();	 Catch:{ all -> 0x0508 }
        r11 = r11 + r9;
        r12 = r20;
        r12.setBounds(r8, r9, r10, r11);	 Catch:{ all -> 0x0508 }
        r12.draw(r14);	 Catch:{ all -> 0x0508 }
    L_0x03dd:
        if (r15 == 0) goto L_0x0402;
    L_0x03df:
        r8 = r7.getWidth();	 Catch:{ all -> 0x0508 }
        r9 = r15.getIntrinsicWidth();	 Catch:{ all -> 0x0508 }
        r8 = r8 - r9;
        r8 = r8 + -10;
        r9 = r15.getIntrinsicHeight();	 Catch:{ all -> 0x0508 }
        r9 = 120 - r9;
        r9 = r9 / 2;
        r10 = r15.getIntrinsicWidth();	 Catch:{ all -> 0x0508 }
        r10 = r10 + r8;
        r11 = r15.getIntrinsicHeight();	 Catch:{ all -> 0x0508 }
        r11 = r11 + r9;
        r15.setBounds(r8, r9, r10, r11);	 Catch:{ all -> 0x0508 }
        r15.draw(r14);	 Catch:{ all -> 0x0508 }
    L_0x0402:
        r8 = 216; // 0xd8 float:3.03E-43 double:1.067E-321;
        r9 = r7.getWidth();	 Catch:{ all -> 0x0508 }
        r10 = 20;
        r9 = r9 - r10;
        r11 = 308; // 0x134 float:4.32E-43 double:1.52E-321;
        r12 = 161; // 0xa1 float:2.26E-43 double:7.95E-322;
        r13 = r28;
        r13.setBounds(r12, r8, r9, r11);	 Catch:{ all -> 0x0508 }
        r8 = 522; // 0x20a float:7.31E-43 double:2.58E-321;
        r9 = 0;
        r13.setTop(r9, r8);	 Catch:{ all -> 0x0508 }
        r13.draw(r14);	 Catch:{ all -> 0x0508 }
        r9 = r7.getWidth();	 Catch:{ all -> 0x0508 }
        r9 = r9 - r10;
        r11 = 430; // 0x1ae float:6.03E-43 double:2.124E-321;
        r13.setBounds(r12, r11, r9, r8);	 Catch:{ all -> 0x0508 }
        r13.setTop(r11, r8);	 Catch:{ all -> 0x0508 }
        r13.draw(r14);	 Catch:{ all -> 0x0508 }
        if (r2 == 0) goto L_0x043b;
    L_0x042f:
        r8 = 323; // 0x143 float:4.53E-43 double:1.596E-321;
        r9 = 399; // 0x18f float:5.59E-43 double:1.97E-321;
        r11 = 415; // 0x19f float:5.82E-43 double:2.05E-321;
        r2.setBounds(r10, r8, r9, r11);	 Catch:{ all -> 0x0508 }
        r2.draw(r14);	 Catch:{ all -> 0x0508 }
    L_0x043b:
        if (r3 == 0) goto L_0x045e;
    L_0x043d:
        r2 = r7.getWidth();	 Catch:{ all -> 0x0508 }
        r2 = r2 + -126;
        r2 = r2 / 2;
        r8 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
        r9 = (float) r2;	 Catch:{ all -> 0x0508 }
        r8 = (float) r8;	 Catch:{ all -> 0x0508 }
        r2 = r2 + 126;
        r2 = (float) r2;	 Catch:{ all -> 0x0508 }
        r10 = 192; // 0xc0 float:2.69E-43 double:9.5E-322;
        r10 = (float) r10;	 Catch:{ all -> 0x0508 }
        r4.set(r9, r8, r2, r10);	 Catch:{ all -> 0x0508 }
        r2 = r3.intValue();	 Catch:{ all -> 0x0508 }
        r1.setColor(r2);	 Catch:{ all -> 0x0508 }
        r2 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r14.drawRoundRect(r4, r2, r2, r1);	 Catch:{ all -> 0x0508 }
    L_0x045e:
        r1.setColor(r6);	 Catch:{ all -> 0x0508 }
        r9 = 0;
        r2 = r7.getHeight();	 Catch:{ all -> 0x0508 }
        r2 = r2 + -120;
        r10 = (float) r2;	 Catch:{ all -> 0x0508 }
        r2 = r7.getWidth();	 Catch:{ all -> 0x0508 }
        r11 = (float) r2;	 Catch:{ all -> 0x0508 }
        r2 = r7.getHeight();	 Catch:{ all -> 0x0508 }
        r12 = (float) r2;	 Catch:{ all -> 0x0508 }
        r8 = r14;
        r13 = r1;
        r8.drawRect(r9, r10, r11, r12, r13);	 Catch:{ all -> 0x0508 }
        if (r5 == 0) goto L_0x049b;
    L_0x047a:
        r1 = 22;
        r2 = r7.getHeight();	 Catch:{ all -> 0x0508 }
        r2 = r2 + -120;
        r3 = r5.getIntrinsicHeight();	 Catch:{ all -> 0x0508 }
        r9 = 120 - r3;
        r9 = r9 / 2;
        r2 = r2 + r9;
        r3 = r5.getIntrinsicWidth();	 Catch:{ all -> 0x0508 }
        r3 = r3 + r1;
        r4 = r5.getIntrinsicHeight();	 Catch:{ all -> 0x0508 }
        r4 = r4 + r2;
        r5.setBounds(r1, r2, r3, r4);	 Catch:{ all -> 0x0508 }
        r5.draw(r14);	 Catch:{ all -> 0x0508 }
    L_0x049b:
        if (r27 == 0) goto L_0x04c9;
    L_0x049d:
        r1 = r7.getWidth();	 Catch:{ all -> 0x0508 }
        r2 = r27.getIntrinsicWidth();	 Catch:{ all -> 0x0508 }
        r1 = r1 - r2;
        r1 = r1 + -22;
        r2 = r7.getHeight();	 Catch:{ all -> 0x0508 }
        r2 = r2 + -120;
        r3 = r27.getIntrinsicHeight();	 Catch:{ all -> 0x0508 }
        r9 = 120 - r3;
        r9 = r9 / 2;
        r2 = r2 + r9;
        r3 = r27.getIntrinsicWidth();	 Catch:{ all -> 0x0508 }
        r3 = r3 + r1;
        r4 = r27.getIntrinsicHeight();	 Catch:{ all -> 0x0508 }
        r4 = r4 + r2;
        r5 = r27;
        r5.setBounds(r1, r2, r3, r4);	 Catch:{ all -> 0x0508 }
        r5.draw(r14);	 Catch:{ all -> 0x0508 }
    L_0x04c9:
        r1 = 0;
        r14.setBitmap(r1);	 Catch:{ all -> 0x0508 }
        r1 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0508 }
        r1.<init>();	 Catch:{ all -> 0x0508 }
        r2 = "-2147483648_";
        r1.append(r2);	 Catch:{ all -> 0x0508 }
        r2 = org.telegram.messenger.SharedConfig.getLastLocalId();	 Catch:{ all -> 0x0508 }
        r1.append(r2);	 Catch:{ all -> 0x0508 }
        r2 = ".jpg";
        r1.append(r2);	 Catch:{ all -> 0x0508 }
        r1 = r1.toString();	 Catch:{ all -> 0x0508 }
        r2 = new java.io.File;	 Catch:{ all -> 0x0508 }
        r3 = 4;
        r3 = org.telegram.messenger.FileLoader.getDirectory(r3);	 Catch:{ all -> 0x0508 }
        r2.<init>(r3, r1);	 Catch:{ all -> 0x0508 }
        r1 = new java.io.FileOutputStream;	 Catch:{ all -> 0x0503 }
        r1.<init>(r2);	 Catch:{ all -> 0x0503 }
        r3 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ all -> 0x0503 }
        r7.compress(r3, r0, r1);	 Catch:{ all -> 0x0503 }
        org.telegram.messenger.SharedConfig.saveConfig();	 Catch:{ all -> 0x0503 }
        r0 = r2.getAbsolutePath();	 Catch:{ all -> 0x0503 }
        return r0;
    L_0x0503:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0508 }
        goto L_0x050c;
    L_0x0508:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x050c:
        r1 = 0;
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.createThemePreviewImage(java.lang.String, java.lang.String):java.lang.String");
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:34:0x0081 */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00ca A:{SYNTHETIC, Splitter:B:55:0x00ca} */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Can't wrap try/catch for region: R(4:32|33|34|35) */
    /* JADX WARNING: Missing block: B:49:0x00c1, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:56:?, code skipped:
            r5.close();
     */
    public static java.util.HashMap<java.lang.String, java.lang.Integer> getThemeFileValues(java.io.File r16, java.lang.String r17, java.lang.String[] r18) {
        /*
        r0 = r18;
        r1 = new java.util.HashMap;
        r1.<init>();
        r2 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r3 = 0;
        r2 = new byte[r2];	 Catch:{ all -> 0x00c3 }
        if (r17 == 0) goto L_0x0013;
    L_0x000e:
        r4 = getAssetFile(r17);	 Catch:{ all -> 0x00c3 }
        goto L_0x0015;
    L_0x0013:
        r4 = r16;
    L_0x0015:
        r5 = new java.io.FileInputStream;	 Catch:{ all -> 0x00c3 }
        r5.<init>(r4);	 Catch:{ all -> 0x00c3 }
        r3 = -1;
        r4 = 0;
        r6 = 0;
        r7 = -1;
        r8 = 0;
    L_0x001f:
        r9 = r5.read(r2);	 Catch:{ all -> 0x00c1 }
        if (r9 == r3) goto L_0x00b3;
    L_0x0025:
        r12 = r6;
        r10 = 0;
        r11 = 0;
    L_0x0028:
        r13 = 1;
        if (r10 >= r9) goto L_0x00a0;
    L_0x002b:
        r14 = r2[r10];	 Catch:{ all -> 0x00c1 }
        r15 = 10;
        if (r14 != r15) goto L_0x009b;
    L_0x0031:
        r14 = r10 - r11;
        r14 = r14 + r13;
        r15 = new java.lang.String;	 Catch:{ all -> 0x00c1 }
        r13 = r14 + -1;
        r15.<init>(r2, r11, r13);	 Catch:{ all -> 0x00c1 }
        r13 = "WLS=";
        r13 = r15.startsWith(r13);	 Catch:{ all -> 0x00c1 }
        if (r13 == 0) goto L_0x0050;
    L_0x0043:
        if (r0 == 0) goto L_0x0099;
    L_0x0045:
        r13 = r0.length;	 Catch:{ all -> 0x00c1 }
        if (r13 <= 0) goto L_0x0099;
    L_0x0048:
        r13 = 4;
        r13 = r15.substring(r13);	 Catch:{ all -> 0x00c1 }
        r0[r4] = r13;	 Catch:{ all -> 0x00c1 }
        goto L_0x0099;
    L_0x0050:
        r13 = "WPS";
        r13 = r15.startsWith(r13);	 Catch:{ all -> 0x00c1 }
        if (r13 == 0) goto L_0x005c;
    L_0x0058:
        r14 = r14 + r12;
        r7 = r14;
        r8 = 1;
        goto L_0x00a0;
    L_0x005c:
        r13 = 61;
        r13 = r15.indexOf(r13);	 Catch:{ all -> 0x00c1 }
        if (r13 == r3) goto L_0x0099;
    L_0x0064:
        r3 = r15.substring(r4, r13);	 Catch:{ all -> 0x00c1 }
        r13 = r13 + 1;
        r13 = r15.substring(r13);	 Catch:{ all -> 0x00c1 }
        r15 = r13.length();	 Catch:{ all -> 0x00c1 }
        if (r15 <= 0) goto L_0x008a;
    L_0x0074:
        r15 = r13.charAt(r4);	 Catch:{ all -> 0x00c1 }
        r4 = 35;
        if (r15 != r4) goto L_0x008a;
    L_0x007c:
        r4 = android.graphics.Color.parseColor(r13);	 Catch:{ Exception -> 0x0081 }
        goto L_0x0092;
    L_0x0081:
        r4 = org.telegram.messenger.Utilities.parseInt(r13);	 Catch:{ all -> 0x00c1 }
        r4 = r4.intValue();	 Catch:{ all -> 0x00c1 }
        goto L_0x0092;
    L_0x008a:
        r4 = org.telegram.messenger.Utilities.parseInt(r13);	 Catch:{ all -> 0x00c1 }
        r4 = r4.intValue();	 Catch:{ all -> 0x00c1 }
    L_0x0092:
        r4 = java.lang.Integer.valueOf(r4);	 Catch:{ all -> 0x00c1 }
        r1.put(r3, r4);	 Catch:{ all -> 0x00c1 }
    L_0x0099:
        r11 = r11 + r14;
        r12 = r12 + r14;
    L_0x009b:
        r10 = r10 + 1;
        r3 = -1;
        r4 = 0;
        goto L_0x0028;
    L_0x00a0:
        if (r6 != r12) goto L_0x00a3;
    L_0x00a2:
        goto L_0x00b3;
    L_0x00a3:
        r3 = r5.getChannel();	 Catch:{ all -> 0x00c1 }
        r9 = (long) r12;	 Catch:{ all -> 0x00c1 }
        r3.position(r9);	 Catch:{ all -> 0x00c1 }
        if (r8 == 0) goto L_0x00ae;
    L_0x00ad:
        goto L_0x00b3;
    L_0x00ae:
        r6 = r12;
        r3 = -1;
        r4 = 0;
        goto L_0x001f;
    L_0x00b3:
        r0 = "wallpaperFileOffset";
        r2 = java.lang.Integer.valueOf(r7);	 Catch:{ all -> 0x00c1 }
        r1.put(r0, r2);	 Catch:{ all -> 0x00c1 }
        r5.close();	 Catch:{ Exception -> 0x00ce }
        goto L_0x00d3;
    L_0x00c1:
        r0 = move-exception;
        goto L_0x00c5;
    L_0x00c3:
        r0 = move-exception;
        r5 = r3;
    L_0x00c5:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x00d4 }
        if (r5 == 0) goto L_0x00d3;
    L_0x00ca:
        r5.close();	 Catch:{ Exception -> 0x00ce }
        goto L_0x00d3;
    L_0x00ce:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
    L_0x00d3:
        return r1;
    L_0x00d4:
        r0 = move-exception;
        r1 = r0;
        if (r5 == 0) goto L_0x00e1;
    L_0x00d8:
        r5.close();	 Catch:{ Exception -> 0x00dc }
        goto L_0x00e1;
    L_0x00dc:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
    L_0x00e1:
        goto L_0x00e3;
    L_0x00e2:
        throw r1;
    L_0x00e3:
        goto L_0x00e2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.getThemeFileValues(java.io.File, java.lang.String, java.lang.String[]):java.util.HashMap");
    }

    public static void createCommonResources(Context context) {
        if (dividerPaint == null) {
            dividerPaint = new Paint();
            dividerPaint.setStrokeWidth(1.0f);
            avatar_backgroundPaint = new Paint(1);
            checkboxSquare_checkPaint = new Paint(1);
            checkboxSquare_checkPaint.setStyle(Style.STROKE);
            checkboxSquare_checkPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            checkboxSquare_checkPaint.setStrokeCap(Cap.ROUND);
            checkboxSquare_eraserPaint = new Paint(1);
            checkboxSquare_eraserPaint.setColor(0);
            checkboxSquare_eraserPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            checkboxSquare_backgroundPaint = new Paint(1);
            linkSelectionPaint = new Paint();
            Resources resources = context.getResources();
            avatar_savedDrawable = resources.getDrawable(NUM);
            avatar_ghostDrawable = resources.getDrawable(NUM);
            RLottieDrawable rLottieDrawable = dialogs_archiveAvatarDrawable;
            if (rLottieDrawable != null) {
                rLottieDrawable.setCallback(null);
                dialogs_archiveAvatarDrawable.recycle();
            }
            rLottieDrawable = dialogs_archiveDrawable;
            if (rLottieDrawable != null) {
                rLottieDrawable.recycle();
            }
            rLottieDrawable = dialogs_unarchiveDrawable;
            if (rLottieDrawable != null) {
                rLottieDrawable.recycle();
            }
            rLottieDrawable = dialogs_pinArchiveDrawable;
            if (rLottieDrawable != null) {
                rLottieDrawable.recycle();
            }
            rLottieDrawable = dialogs_unpinArchiveDrawable;
            if (rLottieDrawable != null) {
                rLottieDrawable.recycle();
            }
            dialogs_archiveAvatarDrawable = new RLottieDrawable(NUM, "chats_archiveavatar", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f), false, null);
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
            String str = "avatar_text";
            setDrawableColorByKey(avatar_savedDrawable, str);
            String str2 = "avatar_backgroundArchived";
            String str3 = "Arrow1.**";
            dialogs_archiveAvatarDrawable.setLayerColor(str3, getColor(str2));
            String str4 = "Arrow2.**";
            dialogs_archiveAvatarDrawable.setLayerColor(str4, getColor(str2));
            String str5 = "Box2.**";
            dialogs_archiveAvatarDrawable.setLayerColor(str5, getColor(str));
            str2 = "Box1.**";
            dialogs_archiveAvatarDrawable.setLayerColor(str2, getColor(str));
            dialogs_archiveAvatarDrawableRecolored = false;
            dialogs_archiveAvatarDrawable.setAllowDecodeSingleFrame(true);
            String str6 = "chats_archiveIcon";
            String str7 = "Arrow.**";
            dialogs_pinArchiveDrawable.setLayerColor(str7, getColor(str6));
            String str8 = "Line.**";
            dialogs_pinArchiveDrawable.setLayerColor(str8, getColor(str6));
            dialogs_unpinArchiveDrawable.setLayerColor(str7, getColor(str6));
            dialogs_unpinArchiveDrawable.setLayerColor(str8, getColor(str6));
            dialogs_archiveDrawable.setLayerColor(str7, getColor("chats_archiveBackground"));
            dialogs_archiveDrawable.setLayerColor(str5, getColor(str6));
            dialogs_archiveDrawable.setLayerColor(str2, getColor(str6));
            dialogs_archiveDrawableRecolored = false;
            dialogs_unarchiveDrawable.setLayerColor(str3, getColor(str6));
            dialogs_unarchiveDrawable.setLayerColor(str4, getColor("chats_archivePinBackground"));
            dialogs_unarchiveDrawable.setLayerColor(str5, getColor(str6));
            dialogs_unarchiveDrawable.setLayerColor(str2, getColor(str6));
        }
    }

    public static void createDialogsResources(Context context) {
        createCommonResources(context);
        if (dialogs_namePaint == null) {
            String str;
            Resources resources = context.getResources();
            dialogs_namePaint = new TextPaint[2];
            dialogs_nameEncryptedPaint = new TextPaint[2];
            dialogs_messagePaint = new TextPaint[2];
            dialogs_messagePrintingPaint = new TextPaint[2];
            int i = 0;
            while (true) {
                str = "fonts/rmedium.ttf";
                if (i >= 2) {
                    break;
                }
                dialogs_namePaint[i] = new TextPaint(1);
                dialogs_namePaint[i].setTypeface(AndroidUtilities.getTypeface(str));
                dialogs_nameEncryptedPaint[i] = new TextPaint(1);
                dialogs_nameEncryptedPaint[i].setTypeface(AndroidUtilities.getTypeface(str));
                dialogs_messagePaint[i] = new TextPaint(1);
                dialogs_messagePrintingPaint[i] = new TextPaint(1);
                i++;
            }
            dialogs_searchNamePaint = new TextPaint(1);
            dialogs_searchNamePaint.setTypeface(AndroidUtilities.getTypeface(str));
            dialogs_searchNameEncryptedPaint = new TextPaint(1);
            dialogs_searchNameEncryptedPaint.setTypeface(AndroidUtilities.getTypeface(str));
            dialogs_messageNamePaint = new TextPaint(1);
            dialogs_messageNamePaint.setTypeface(AndroidUtilities.getTypeface(str));
            dialogs_timePaint = new TextPaint(1);
            dialogs_countTextPaint = new TextPaint(1);
            dialogs_countTextPaint.setTypeface(AndroidUtilities.getTypeface(str));
            dialogs_archiveTextPaint = new TextPaint(1);
            dialogs_archiveTextPaint.setTypeface(AndroidUtilities.getTypeface(str));
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
            dialogs_reorderDrawable = resources.getDrawable(NUM);
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
            int i = 0;
            while (true) {
                String str = "chats_secretName";
                String str2 = "chats_name";
                if (i < 2) {
                    dialogs_namePaint[i].setColor(getColor(str2));
                    dialogs_nameEncryptedPaint[i].setColor(getColor(str));
                    TextPaint[] textPaintArr = dialogs_messagePaint;
                    TextPaint textPaint = textPaintArr[i];
                    TextPaint textPaint2 = textPaintArr[i];
                    int color = getColor("chats_message");
                    textPaint2.linkColor = color;
                    textPaint.setColor(color);
                    dialogs_messagePrintingPaint[i].setColor(getColor("chats_actionMessage"));
                    i++;
                } else {
                    dialogs_searchNamePaint.setColor(getColor(str2));
                    dialogs_searchNameEncryptedPaint.setColor(getColor(str));
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
                    String str3 = "chats_sentReadCheck";
                    setDrawableColorByKey(dialogs_checkReadDrawable, str3);
                    setDrawableColorByKey(dialogs_halfCheckDrawable, str3);
                    setDrawableColorByKey(dialogs_clockDrawable, "chats_sentClock");
                    setDrawableColorByKey(dialogs_errorDrawable, "chats_sentErrorIcon");
                    str3 = "chats_nameIcon";
                    setDrawableColorByKey(dialogs_groupDrawable, str3);
                    setDrawableColorByKey(dialogs_broadcastDrawable, str3);
                    setDrawableColorByKey(dialogs_botDrawable, str3);
                    str3 = "chats_pinnedIcon";
                    setDrawableColorByKey(dialogs_pinnedDrawable, str3);
                    setDrawableColorByKey(dialogs_reorderDrawable, str3);
                    setDrawableColorByKey(dialogs_muteDrawable, "chats_muteIcon");
                    setDrawableColorByKey(dialogs_mentionDrawable, "chats_mentionIcon");
                    setDrawableColorByKey(dialogs_verifiedDrawable, "chats_verifiedBackground");
                    setDrawableColorByKey(dialogs_verifiedCheckDrawable, "chats_verifiedCheck");
                    setDrawableColorByKey(dialogs_holidayDrawable, "actionBarDefaultTitle");
                    setDrawableColorByKey(dialogs_scamDrawable, "chats_draft");
                    return;
                }
            }
        }
    }

    public static void destroyResources() {
        int i = 0;
        while (true) {
            Drawable[] drawableArr = chat_attachButtonDrawables;
            if (i < drawableArr.length) {
                if (drawableArr[i] != null) {
                    drawableArr[i].setCallback(null);
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

    /* JADX WARNING: Missing exception handler attribute for start block: B:24:0x0bc2 */
    /* JADX WARNING: Can't wrap try/catch for region: R(11:11|12|13|(4:15|(1:17)(1:18)|19|20)|37|21|22|23|24|25|26) */
    public static void createChatResources(android.content.Context r15, boolean r16) {
        /*
        r1 = sync;
        monitor-enter(r1);
        r0 = chat_msgTextPaint;	 Catch:{ all -> 0x0d5c }
        r2 = 1;
        if (r0 != 0) goto L_0x003d;
    L_0x0008:
        r0 = new android.text.TextPaint;	 Catch:{ all -> 0x0d5c }
        r0.<init>(r2);	 Catch:{ all -> 0x0d5c }
        chat_msgTextPaint = r0;	 Catch:{ all -> 0x0d5c }
        r0 = new android.text.TextPaint;	 Catch:{ all -> 0x0d5c }
        r0.<init>(r2);	 Catch:{ all -> 0x0d5c }
        chat_msgGameTextPaint = r0;	 Catch:{ all -> 0x0d5c }
        r0 = new android.text.TextPaint;	 Catch:{ all -> 0x0d5c }
        r0.<init>(r2);	 Catch:{ all -> 0x0d5c }
        chat_msgTextPaintOneEmoji = r0;	 Catch:{ all -> 0x0d5c }
        r0 = new android.text.TextPaint;	 Catch:{ all -> 0x0d5c }
        r0.<init>(r2);	 Catch:{ all -> 0x0d5c }
        chat_msgTextPaintTwoEmoji = r0;	 Catch:{ all -> 0x0d5c }
        r0 = new android.text.TextPaint;	 Catch:{ all -> 0x0d5c }
        r0.<init>(r2);	 Catch:{ all -> 0x0d5c }
        chat_msgTextPaintThreeEmoji = r0;	 Catch:{ all -> 0x0d5c }
        r0 = new android.text.TextPaint;	 Catch:{ all -> 0x0d5c }
        r0.<init>(r2);	 Catch:{ all -> 0x0d5c }
        chat_msgBotButtonPaint = r0;	 Catch:{ all -> 0x0d5c }
        r0 = chat_msgBotButtonPaint;	 Catch:{ all -> 0x0d5c }
        r3 = "fonts/rmedium.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);	 Catch:{ all -> 0x0d5c }
        r0.setTypeface(r3);	 Catch:{ all -> 0x0d5c }
    L_0x003d:
        monitor-exit(r1);	 Catch:{ all -> 0x0d5c }
        r0 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r1 = 2;
        if (r16 != 0) goto L_0x0bcc;
    L_0x0043:
        r3 = chat_msgInDrawable;
        if (r3 != 0) goto L_0x0bcc;
    L_0x0047:
        r3 = new android.text.TextPaint;
        r3.<init>(r2);
        chat_infoPaint = r3;
        r3 = new android.text.TextPaint;
        r3.<init>(r2);
        chat_docNamePaint = r3;
        r3 = chat_docNamePaint;
        r4 = "fonts/rmedium.ttf";
        r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r4);
        r3.setTypeface(r4);
        r3 = new android.graphics.Paint;
        r3.<init>(r2);
        chat_docBackPaint = r3;
        r3 = new android.graphics.Paint;
        r3.<init>(r2);
        chat_deleteProgressPaint = r3;
        r3 = new android.graphics.Paint;
        r3.<init>(r2);
        chat_botProgressPaint = r3;
        r3 = chat_botProgressPaint;
        r4 = android.graphics.Paint.Cap.ROUND;
        r3.setStrokeCap(r4);
        r3 = chat_botProgressPaint;
        r4 = android.graphics.Paint.Style.STROKE;
        r3.setStyle(r4);
        r3 = new android.text.TextPaint;
        r3.<init>(r2);
        chat_locationTitlePaint = r3;
        r3 = chat_locationTitlePaint;
        r4 = "fonts/rmedium.ttf";
        r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r4);
        r3.setTypeface(r4);
        r3 = new android.text.TextPaint;
        r3.<init>(r2);
        chat_locationAddressPaint = r3;
        r3 = new android.graphics.Paint;
        r3.<init>();
        chat_urlPaint = r3;
        r3 = new android.graphics.Paint;
        r3.<init>();
        chat_textSearchSelectionPaint = r3;
        r3 = new android.graphics.Paint;
        r3.<init>(r2);
        chat_radialProgressPaint = r3;
        r3 = chat_radialProgressPaint;
        r4 = android.graphics.Paint.Cap.ROUND;
        r3.setStrokeCap(r4);
        r3 = chat_radialProgressPaint;
        r4 = android.graphics.Paint.Style.STROKE;
        r3.setStyle(r4);
        r3 = chat_radialProgressPaint;
        r4 = -NUM; // 0xffffffff9fffffff float:-1.0842021E-19 double:NaN;
        r3.setColor(r4);
        r3 = new android.graphics.Paint;
        r3.<init>(r2);
        chat_radialProgress2Paint = r3;
        r3 = chat_radialProgress2Paint;
        r4 = android.graphics.Paint.Cap.ROUND;
        r3.setStrokeCap(r4);
        r3 = chat_radialProgress2Paint;
        r4 = android.graphics.Paint.Style.STROKE;
        r3.setStyle(r4);
        r3 = new android.text.TextPaint;
        r3.<init>(r2);
        chat_audioTimePaint = r3;
        r3 = new android.text.TextPaint;
        r3.<init>(r2);
        chat_livePaint = r3;
        r3 = chat_livePaint;
        r4 = "fonts/rmedium.ttf";
        r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r4);
        r3.setTypeface(r4);
        r3 = new android.text.TextPaint;
        r3.<init>(r2);
        chat_audioTitlePaint = r3;
        r3 = chat_audioTitlePaint;
        r4 = "fonts/rmedium.ttf";
        r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r4);
        r3.setTypeface(r4);
        r3 = new android.text.TextPaint;
        r3.<init>(r2);
        chat_audioPerformerPaint = r3;
        r3 = new android.text.TextPaint;
        r3.<init>(r2);
        chat_botButtonPaint = r3;
        r3 = chat_botButtonPaint;
        r4 = "fonts/rmedium.ttf";
        r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r4);
        r3.setTypeface(r4);
        r3 = new android.text.TextPaint;
        r3.<init>(r2);
        chat_contactNamePaint = r3;
        r3 = chat_contactNamePaint;
        r4 = "fonts/rmedium.ttf";
        r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r4);
        r3.setTypeface(r4);
        r3 = new android.text.TextPaint;
        r3.<init>(r2);
        chat_contactPhonePaint = r3;
        r3 = new android.text.TextPaint;
        r3.<init>(r2);
        chat_durationPaint = r3;
        r3 = new android.text.TextPaint;
        r3.<init>(r2);
        chat_gamePaint = r3;
        r3 = chat_gamePaint;
        r4 = "fonts/rmedium.ttf";
        r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r4);
        r3.setTypeface(r4);
        r3 = new android.text.TextPaint;
        r3.<init>(r2);
        chat_shipmentPaint = r3;
        r3 = new android.text.TextPaint;
        r3.<init>(r2);
        chat_timePaint = r3;
        r3 = new android.text.TextPaint;
        r3.<init>(r2);
        chat_adminPaint = r3;
        r3 = new android.text.TextPaint;
        r3.<init>(r2);
        chat_namePaint = r3;
        r3 = chat_namePaint;
        r4 = "fonts/rmedium.ttf";
        r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r4);
        r3.setTypeface(r4);
        r3 = new android.text.TextPaint;
        r3.<init>(r2);
        chat_forwardNamePaint = r3;
        r3 = new android.text.TextPaint;
        r3.<init>(r2);
        chat_replyNamePaint = r3;
        r3 = chat_replyNamePaint;
        r4 = "fonts/rmedium.ttf";
        r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r4);
        r3.setTypeface(r4);
        r3 = new android.text.TextPaint;
        r3.<init>(r2);
        chat_replyTextPaint = r3;
        r3 = new android.text.TextPaint;
        r3.<init>(r2);
        chat_instantViewPaint = r3;
        r3 = chat_instantViewPaint;
        r4 = "fonts/rmedium.ttf";
        r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r4);
        r3.setTypeface(r4);
        r3 = new android.graphics.Paint;
        r3.<init>(r2);
        chat_instantViewRectPaint = r3;
        r3 = chat_instantViewRectPaint;
        r4 = android.graphics.Paint.Style.STROKE;
        r3.setStyle(r4);
        r3 = new android.graphics.Paint;
        r3.<init>(r2);
        chat_replyLinePaint = r3;
        r3 = new android.graphics.Paint;
        r3.<init>(r2);
        chat_msgErrorPaint = r3;
        r3 = new android.graphics.Paint;
        r3.<init>(r2);
        chat_statusPaint = r3;
        r3 = new android.graphics.Paint;
        r3.<init>(r2);
        chat_statusRecordPaint = r3;
        r3 = chat_statusRecordPaint;
        r4 = android.graphics.Paint.Style.STROKE;
        r3.setStyle(r4);
        r3 = chat_statusRecordPaint;
        r4 = android.graphics.Paint.Cap.ROUND;
        r3.setStrokeCap(r4);
        r3 = new android.text.TextPaint;
        r3.<init>(r2);
        chat_actionTextPaint = r3;
        r3 = chat_actionTextPaint;
        r4 = "fonts/rmedium.ttf";
        r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r4);
        r3.setTypeface(r4);
        r3 = new android.graphics.Paint;
        r3.<init>(r2);
        chat_actionBackgroundPaint = r3;
        r3 = new android.graphics.Paint;
        r3.<init>(r2);
        chat_timeBackgroundPaint = r3;
        r3 = new android.text.TextPaint;
        r3.<init>(r2);
        chat_contextResult_titleTextPaint = r3;
        r3 = chat_contextResult_titleTextPaint;
        r4 = "fonts/rmedium.ttf";
        r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r4);
        r3.setTypeface(r4);
        r3 = new android.text.TextPaint;
        r3.<init>(r2);
        chat_contextResult_descriptionTextPaint = r3;
        r3 = new android.graphics.Paint;
        r3.<init>();
        chat_composeBackgroundPaint = r3;
        r3 = r15.getResources();
        r4 = NUM; // 0x7var_da float:1.794554E38 double:1.052935737E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgInDrawable = r4;
        r4 = NUM; // 0x7var_da float:1.794554E38 double:1.052935737E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgInSelectedDrawable = r4;
        r4 = NUM; // 0x7var_e9 float:1.7946089E38 double:1.052935871E-314;
        r4 = r3.getDrawable(r4);
        chat_msgNoSoundDrawable = r4;
        r4 = new org.telegram.ui.ActionBar.Theme$MessageDrawable;
        r5 = 0;
        r4.<init>(r5, r5);
        chat_msgOutDrawable = r4;
        r4 = new org.telegram.ui.ActionBar.Theme$MessageDrawable;
        r4.<init>(r5, r2);
        chat_msgOutSelectedDrawable = r4;
        r4 = NUM; // 0x7var_ef float:1.7945582E38 double:1.0529357476E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgInMediaDrawable = r4;
        r4 = NUM; // 0x7var_ef float:1.7945582E38 double:1.0529357476E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgInMediaSelectedDrawable = r4;
        r4 = new org.telegram.ui.ActionBar.Theme$MessageDrawable;
        r4.<init>(r2, r5);
        chat_msgOutMediaDrawable = r4;
        r4 = new org.telegram.ui.ActionBar.Theme$MessageDrawable;
        r4.<init>(r2, r2);
        chat_msgOutMediaSelectedDrawable = r4;
        r4 = NUM; // 0x7var_c9 float:1.7945505E38 double:1.052935729E-314;
        r6 = r3.getDrawable(r4);
        r6 = r6.mutate();
        chat_msgOutCheckDrawable = r6;
        r6 = r3.getDrawable(r4);
        r6 = r6.mutate();
        chat_msgOutCheckSelectedDrawable = r6;
        r6 = r3.getDrawable(r4);
        r6 = r6.mutate();
        chat_msgOutCheckReadDrawable = r6;
        r6 = r3.getDrawable(r4);
        r6 = r6.mutate();
        chat_msgOutCheckReadSelectedDrawable = r6;
        r6 = r3.getDrawable(r4);
        r6 = r6.mutate();
        chat_msgMediaCheckDrawable = r6;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgStickerCheckDrawable = r4;
        r4 = NUM; // 0x7var_d7 float:1.7945533E38 double:1.0529357357E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgOutHalfCheckDrawable = r4;
        r4 = NUM; // 0x7var_d7 float:1.7945533E38 double:1.0529357357E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgOutHalfCheckSelectedDrawable = r4;
        r4 = NUM; // 0x7var_d7 float:1.7945533E38 double:1.0529357357E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgMediaHalfCheckDrawable = r4;
        r4 = NUM; // 0x7var_d7 float:1.7945533E38 double:1.0529357357E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgStickerHalfCheckDrawable = r4;
        r4 = NUM; // 0x7var_cb float:1.7945509E38 double:1.05293573E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgOutClockDrawable = r4;
        r4 = NUM; // 0x7var_cb float:1.7945509E38 double:1.05293573E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgOutSelectedClockDrawable = r4;
        r4 = NUM; // 0x7var_cb float:1.7945509E38 double:1.05293573E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgInClockDrawable = r4;
        r4 = NUM; // 0x7var_cb float:1.7945509E38 double:1.05293573E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgInSelectedClockDrawable = r4;
        r4 = NUM; // 0x7var_cb float:1.7945509E38 double:1.05293573E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgMediaClockDrawable = r4;
        r4 = NUM; // 0x7var_cb float:1.7945509E38 double:1.05293573E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgStickerClockDrawable = r4;
        r4 = NUM; // 0x7var_c float:1.794564E38 double:1.052935762E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgInViewsDrawable = r4;
        r4 = NUM; // 0x7var_c float:1.794564E38 double:1.052935762E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgInViewsSelectedDrawable = r4;
        r4 = NUM; // 0x7var_c float:1.794564E38 double:1.052935762E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgOutViewsDrawable = r4;
        r4 = NUM; // 0x7var_c float:1.794564E38 double:1.052935762E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgOutViewsSelectedDrawable = r4;
        r4 = NUM; // 0x7var_c float:1.794564E38 double:1.052935762E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgMediaViewsDrawable = r4;
        r4 = NUM; // 0x7var_c float:1.794564E38 double:1.052935762E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgStickerViewsDrawable = r4;
        r4 = NUM; // 0x7var_c1 float:1.7945489E38 double:1.052935725E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgInMenuDrawable = r4;
        r4 = NUM; // 0x7var_c1 float:1.7945489E38 double:1.052935725E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgInMenuSelectedDrawable = r4;
        r4 = NUM; // 0x7var_c1 float:1.7945489E38 double:1.052935725E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgOutMenuDrawable = r4;
        r4 = NUM; // 0x7var_c1 float:1.7945489E38 double:1.052935725E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgOutMenuSelectedDrawable = r4;
        r4 = NUM; // 0x7var_e2 float:1.7946075E38 double:1.0529358676E-314;
        r4 = r3.getDrawable(r4);
        chat_msgMediaMenuDrawable = r4;
        r4 = NUM; // 0x7var_dc float:1.7945543E38 double:1.052935738E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgInInstantDrawable = r4;
        r4 = NUM; // 0x7var_dc float:1.7945543E38 double:1.052935738E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgOutInstantDrawable = r4;
        r4 = NUM; // 0x7var_d float:1.7945643E38 double:1.0529357624E-314;
        r4 = r3.getDrawable(r4);
        chat_msgErrorDrawable = r4;
        r4 = NUM; // 0x7var_ float:1.7945298E38 double:1.0529356784E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_muteIconDrawable = r4;
        r4 = NUM; // 0x7var_ float:1.7945142E38 double:1.0529356404E-314;
        r4 = r3.getDrawable(r4);
        chat_lockIconDrawable = r4;
        r4 = NUM; // 0x7var_ float:1.7944815E38 double:1.052935561E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgBroadcastDrawable = r4;
        r4 = NUM; // 0x7var_ float:1.7944815E38 double:1.052935561E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgBroadcastMediaDrawable = r4;
        r4 = NUM; // 0x7var_ float:1.794511E38 double:1.0529356325E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgInCallDrawable = r4;
        r4 = NUM; // 0x7var_ float:1.794511E38 double:1.0529356325E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgInCallSelectedDrawable = r4;
        r4 = NUM; // 0x7var_ float:1.794511E38 double:1.0529356325E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgOutCallDrawable = r4;
        r4 = NUM; // 0x7var_ float:1.794511E38 double:1.0529356325E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgOutCallSelectedDrawable = r4;
        r4 = NUM; // 0x7var_ float:1.7945115E38 double:1.052935634E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgCallUpGreenDrawable = r4;
        r4 = NUM; // 0x7var_c float:1.7945122E38 double:1.0529356354E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgCallDownRedDrawable = r4;
        r4 = NUM; // 0x7var_c float:1.7945122E38 double:1.0529356354E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgCallDownGreenDrawable = r4;
        r4 = NUM; // 0x7var_ float:1.7945115E38 double:1.052935634E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        calllog_msgCallUpRedDrawable = r4;
        r4 = NUM; // 0x7var_ float:1.7945115E38 double:1.052935634E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        calllog_msgCallUpGreenDrawable = r4;
        r4 = NUM; // 0x7var_c float:1.7945122E38 double:1.0529356354E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        calllog_msgCallDownRedDrawable = r4;
        r4 = NUM; // 0x7var_c float:1.7945122E38 double:1.0529356354E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        calllog_msgCallDownGreenDrawable = r4;
        r4 = NUM; // 0x7var_a float:1.7945312E38 double:1.052935682E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_msgAvatarLiveLocationDrawable = r4;
        r4 = NUM; // 0x7var_c float:1.7944797E38 double:1.0529355564E-314;
        r4 = r3.getDrawable(r4);
        chat_inlineResultFile = r4;
        r4 = NUM; // 0x7var_ float:1.7944805E38 double:1.0529355584E-314;
        r4 = r3.getDrawable(r4);
        chat_inlineResultAudio = r4;
        r4 = NUM; // 0x7var_f float:1.7944803E38 double:1.052935558E-314;
        r4 = r3.getDrawable(r4);
        chat_inlineResultLocation = r4;
        r4 = NUM; // 0x7var_c float:1.7945349E38 double:1.052935691E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_redLocationIcon = r4;
        r4 = NUM; // 0x7var_db float:1.7945541E38 double:1.0529357377E-314;
        r4 = r3.getDrawable(r4);
        chat_msgInShadowDrawable = r4;
        r4 = NUM; // 0x7var_eb float:1.7945574E38 double:1.0529357456E-314;
        r4 = r3.getDrawable(r4);
        chat_msgOutShadowDrawable = r4;
        r4 = NUM; // 0x7var_f0 float:1.7945584E38 double:1.052935748E-314;
        r4 = r3.getDrawable(r4);
        chat_msgInMediaShadowDrawable = r4;
        r4 = NUM; // 0x7var_f0 float:1.7945584E38 double:1.052935748E-314;
        r4 = r3.getDrawable(r4);
        chat_msgOutMediaShadowDrawable = r4;
        r4 = NUM; // 0x7var_e float:1.7944801E38 double:1.0529355574E-314;
        r4 = r3.getDrawable(r4);
        chat_botLinkDrawalbe = r4;
        r4 = NUM; // 0x7var_d float:1.79448E38 double:1.052935557E-314;
        r4 = r3.getDrawable(r4);
        chat_botInlineDrawable = r4;
        r4 = NUM; // 0x7var_c9 float:1.7946024E38 double:1.0529358553E-314;
        r4 = r3.getDrawable(r4);
        chat_systemDrawable = r4;
        r4 = NUM; // 0x7var_ef float:1.7945063E38 double:1.052935621E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_contextResult_shadowUnderSwitchDrawable = r4;
        r4 = chat_attachButtonDrawables;
        r6 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r7 = NUM; // 0x7var_ float:1.7944744E38 double:1.0529355435E-314;
        r6 = createCircleDrawableWithIcon(r6, r7);
        r4[r5] = r6;
        r4 = chat_attachButtonDrawables;
        r6 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r7 = NUM; // 0x7var_e float:1.7944736E38 double:1.0529355416E-314;
        r6 = createCircleDrawableWithIcon(r6, r7);
        r4[r2] = r6;
        r4 = chat_attachButtonDrawables;
        r6 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r7 = NUM; // 0x7var_ float:1.7944742E38 double:1.052935543E-314;
        r6 = createCircleDrawableWithIcon(r6, r7);
        r4[r1] = r6;
        r4 = chat_attachButtonDrawables;
        r6 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r7 = NUM; // 0x7var_ float:1.794474E38 double:1.0529355426E-314;
        r6 = createCircleDrawableWithIcon(r6, r7);
        r7 = 3;
        r4[r7] = r6;
        r4 = chat_attachButtonDrawables;
        r6 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r8 = NUM; // 0x7var_ float:1.7944746E38 double:1.052935544E-314;
        r6 = createCircleDrawableWithIcon(r6, r8);
        r8 = 4;
        r4[r8] = r6;
        r4 = chat_attachButtonDrawables;
        r6 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r9 = NUM; // 0x7var_ float:1.7944748E38 double:1.0529355445E-314;
        r6 = createCircleDrawableWithIcon(r6, r9);
        r9 = 5;
        r4[r9] = r6;
        r4 = NUM; // 0x7var_c float:1.7945673E38 double:1.05293577E-314;
        r4 = r3.getDrawable(r4);
        chat_attachEmptyDrawable = r4;
        r4 = chat_cornerOuter;
        r6 = NUM; // 0x7var_b3 float:1.794494E38 double:1.0529355915E-314;
        r6 = r3.getDrawable(r6);
        r4[r5] = r6;
        r4 = chat_cornerOuter;
        r6 = NUM; // 0x7var_b4 float:1.7944943E38 double:1.052935592E-314;
        r6 = r3.getDrawable(r6);
        r4[r2] = r6;
        r4 = chat_cornerOuter;
        r6 = NUM; // 0x7var_b2 float:1.7944939E38 double:1.052935591E-314;
        r6 = r3.getDrawable(r6);
        r4[r1] = r6;
        r4 = chat_cornerOuter;
        r6 = NUM; // 0x7var_b1 float:1.7944937E38 double:1.0529355905E-314;
        r6 = r3.getDrawable(r6);
        r4[r7] = r6;
        r4 = chat_cornerInner;
        r6 = NUM; // 0x7var_b0 float:1.7944935E38 double:1.05293559E-314;
        r6 = r3.getDrawable(r6);
        r4[r5] = r6;
        r4 = chat_cornerInner;
        r6 = NUM; // 0x7var_af float:1.7944933E38 double:1.0529355895E-314;
        r6 = r3.getDrawable(r6);
        r4[r2] = r6;
        r4 = chat_cornerInner;
        r6 = NUM; // 0x7var_ae float:1.794493E38 double:1.052935589E-314;
        r6 = r3.getDrawable(r6);
        r4[r1] = r6;
        r4 = chat_cornerInner;
        r6 = NUM; // 0x7var_ad float:1.7944929E38 double:1.0529355885E-314;
        r6 = r3.getDrawable(r6);
        r4[r7] = r6;
        r4 = NUM; // 0x7var_ float:1.7945923E38 double:1.0529358306E-314;
        r4 = r3.getDrawable(r4);
        chat_shareDrawable = r4;
        r4 = NUM; // 0x7var_ float:1.794592E38 double:1.05293583E-314;
        r4 = r3.getDrawable(r4);
        chat_shareIconDrawable = r4;
        r4 = NUM; // 0x7var_bb float:1.7944957E38 double:1.0529355954E-314;
        r4 = r3.getDrawable(r4);
        chat_replyIconDrawable = r4;
        r4 = NUM; // 0x7var_ba float:1.7945474E38 double:1.0529357214E-314;
        r4 = r3.getDrawable(r4);
        chat_goIconDrawable = r4;
        r4 = chat_fileMiniStatesDrawable;
        r4 = r4[r5];
        r6 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = NUM; // 0x7var_ float:1.7944754E38 double:1.052935546E-314;
        r10 = createCircleDrawableWithIcon(r10, r11);
        r4[r5] = r10;
        r4 = chat_fileMiniStatesDrawable;
        r4 = r4[r5];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r10 = createCircleDrawableWithIcon(r10, r11);
        r4[r2] = r10;
        r4 = chat_fileMiniStatesDrawable;
        r4 = r4[r2];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = NUM; // 0x7var_ float:1.7944756E38 double:1.0529355465E-314;
        r10 = createCircleDrawableWithIcon(r10, r11);
        r4[r5] = r10;
        r4 = chat_fileMiniStatesDrawable;
        r4 = r4[r2];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r10 = createCircleDrawableWithIcon(r10, r11);
        r4[r2] = r10;
        r4 = chat_fileMiniStatesDrawable;
        r4 = r4[r1];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = NUM; // 0x7var_ float:1.7944754E38 double:1.052935546E-314;
        r10 = createCircleDrawableWithIcon(r10, r11);
        r4[r5] = r10;
        r4 = chat_fileMiniStatesDrawable;
        r4 = r4[r1];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r10 = createCircleDrawableWithIcon(r10, r11);
        r4[r2] = r10;
        r4 = chat_fileMiniStatesDrawable;
        r4 = r4[r7];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = NUM; // 0x7var_ float:1.7944756E38 double:1.0529355465E-314;
        r10 = createCircleDrawableWithIcon(r10, r11);
        r4[r5] = r10;
        r4 = chat_fileMiniStatesDrawable;
        r4 = r4[r7];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r10 = createCircleDrawableWithIcon(r10, r11);
        r4[r2] = r10;
        r4 = chat_fileMiniStatesDrawable;
        r4 = r4[r8];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = NUM; // 0x7var_e6 float:1.7946083E38 double:1.0529358696E-314;
        r10 = createCircleDrawableWithIcon(r10, r11);
        r4[r5] = r10;
        r4 = chat_fileMiniStatesDrawable;
        r4 = r4[r8];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r10 = createCircleDrawableWithIcon(r10, r11);
        r4[r2] = r10;
        r4 = chat_fileMiniStatesDrawable;
        r4 = r4[r9];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = NUM; // 0x7var_e7 float:1.7946085E38 double:1.05293587E-314;
        r10 = createCircleDrawableWithIcon(r10, r11);
        r4[r5] = r10;
        r4 = chat_fileMiniStatesDrawable;
        r4 = r4[r9];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r10 = NUM; // 0x7var_e7 float:1.7946085E38 double:1.05293587E-314;
        r6 = createCircleDrawableWithIcon(r6, r10);
        r4[r2] = r6;
        r4 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r6 = new android.graphics.RectF;
        r6.<init>();
        r10 = chat_filePath;
        r11 = new android.graphics.Path;
        r11.<init>();
        r10[r5] = r11;
        r10 = chat_filePath;
        r10 = r10[r5];
        r11 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r11;
        r12 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r12 = (float) r12;
        r10.moveTo(r11, r12);
        r10 = chat_filePath;
        r10 = r10[r5];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r11 = (float) r11;
        r12 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r12 = (float) r12;
        r10.lineTo(r11, r12);
        r10 = chat_filePath;
        r10 = r10[r5];
        r11 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r11;
        r12 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r12 = (float) r12;
        r10.lineTo(r11, r12);
        r10 = chat_filePath;
        r10 = r10[r5];
        r11 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r11;
        r12 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r12 = (float) r12;
        r10.lineTo(r11, r12);
        r10 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r11 = r4 * 2;
        r10 = r10 - r11;
        r10 = (float) r10;
        r12 = NUM; // 0x41980000 float:19.0 double:5.43709615E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r12 = r12 - r4;
        r12 = (float) r12;
        r13 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r13 = (float) r13;
        r14 = NUM; // 0x41980000 float:19.0 double:5.43709615E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r14 = r14 + r4;
        r14 = (float) r14;
        r6.set(r10, r12, r13, r14);
        r10 = chat_filePath;
        r10 = r10[r5];
        r12 = 0;
        r13 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;
        r10.arcTo(r6, r12, r13, r5);
        r10 = chat_filePath;
        r10 = r10[r5];
        r12 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r12 = (float) r12;
        r13 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r13 = (float) r13;
        r10.lineTo(r12, r13);
        r10 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r10 = (float) r10;
        r12 = NUM; // 0x41980000 float:19.0 double:5.43709615E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r12 = r12 - r4;
        r12 = (float) r12;
        r13 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r13 = r13 + r11;
        r13 = (float) r13;
        r14 = NUM; // 0x41980000 float:19.0 double:5.43709615E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r14 = r14 + r4;
        r4 = (float) r14;
        r6.set(r10, r12, r13, r4);
        r4 = chat_filePath;
        r4 = r4[r5];
        r10 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;
        r12 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;
        r4.arcTo(r6, r10, r12, r5);
        r4 = chat_filePath;
        r4 = r4[r5];
        r10 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r10 = (float) r10;
        r12 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r12 = (float) r12;
        r4.lineTo(r10, r12);
        r4 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r10 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r10 = (float) r10;
        r12 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r12 = r12 + r11;
        r12 = (float) r12;
        r13 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r13 = r13 + r11;
        r11 = (float) r13;
        r6.set(r4, r10, r12, r11);
        r4 = chat_filePath;
        r4 = r4[r5];
        r10 = NUM; // 0x43340000 float:180.0 double:5.570497984E-315;
        r11 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;
        r4.arcTo(r6, r10, r11, r5);
        r4 = chat_filePath;
        r4 = r4[r5];
        r4.close();
        r4 = chat_filePath;
        r6 = new android.graphics.Path;
        r6.<init>();
        r4[r2] = r6;
        r4 = chat_filePath;
        r4 = r4[r2];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r6 = (float) r6;
        r10 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r10 = (float) r10;
        r4.moveTo(r6, r10);
        r4 = chat_filePath;
        r4 = r4[r2];
        r6 = NUM; // 0x41980000 float:19.0 double:5.43709615E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = (float) r6;
        r10 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r10 = (float) r10;
        r4.lineTo(r6, r10);
        r4 = chat_filePath;
        r4 = r4[r2];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r6 = (float) r6;
        r10 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r10 = (float) r10;
        r4.lineTo(r6, r10);
        r4 = chat_filePath;
        r4 = r4[r2];
        r4.close();
        r4 = NUM; // 0x7var_ float:1.7944821E38 double:1.0529355623E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_flameIcon = r4;
        r4 = NUM; // 0x7var_f9 float:1.7945602E38 double:1.0529357525E-314;
        r4 = r3.getDrawable(r4);
        r4 = r4.mutate();
        chat_gifIcon = r4;
        r4 = chat_fileStatesDrawable;
        r4 = r4[r5];
        r6 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = NUM; // 0x7var_fc float:1.7945608E38 double:1.052935754E-314;
        r10 = createCircleDrawableWithIcon(r10, r11);
        r4[r5] = r10;
        r4 = chat_fileStatesDrawable;
        r4 = r4[r5];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r10 = createCircleDrawableWithIcon(r10, r11);
        r4[r2] = r10;
        r4 = chat_fileStatesDrawable;
        r4 = r4[r2];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = NUM; // 0x7var_fb float:1.7945606E38 double:1.0529357535E-314;
        r10 = createCircleDrawableWithIcon(r10, r11);
        r4[r5] = r10;
        r4 = chat_fileStatesDrawable;
        r4 = r4[r2];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r10 = createCircleDrawableWithIcon(r10, r11);
        r4[r2] = r10;
        r4 = chat_fileStatesDrawable;
        r4 = r4[r1];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = NUM; // 0x7var_fa float:1.7945604E38 double:1.052935753E-314;
        r10 = createCircleDrawableWithIcon(r10, r11);
        r4[r5] = r10;
        r4 = chat_fileStatesDrawable;
        r4 = r4[r1];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r10 = createCircleDrawableWithIcon(r10, r11);
        r4[r2] = r10;
        r4 = chat_fileStatesDrawable;
        r4 = r4[r7];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r12 = NUM; // 0x7var_f8 float:1.79456E38 double:1.052935752E-314;
        r10 = createCircleDrawableWithIcon(r10, r12);
        r4[r5] = r10;
        r4 = chat_fileStatesDrawable;
        r4 = r4[r7];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r10 = createCircleDrawableWithIcon(r10, r12);
        r4[r2] = r10;
        r4 = chat_fileStatesDrawable;
        r4 = r4[r8];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r12 = NUM; // 0x7var_f7 float:1.7945598E38 double:1.0529357515E-314;
        r10 = createCircleDrawableWithIcon(r10, r12);
        r4[r5] = r10;
        r4 = chat_fileStatesDrawable;
        r4 = r4[r8];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r10 = createCircleDrawableWithIcon(r10, r12);
        r4[r2] = r10;
        r4 = chat_fileStatesDrawable;
        r4 = r4[r9];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r13 = NUM; // 0x7var_fc float:1.7945608E38 double:1.052935754E-314;
        r10 = createCircleDrawableWithIcon(r10, r13);
        r4[r5] = r10;
        r4 = chat_fileStatesDrawable;
        r4 = r4[r9];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r10 = createCircleDrawableWithIcon(r10, r13);
        r4[r2] = r10;
        r4 = chat_fileStatesDrawable;
        r10 = 6;
        r4 = r4[r10];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r13 = NUM; // 0x7var_fb float:1.7945606E38 double:1.0529357535E-314;
        r10 = createCircleDrawableWithIcon(r10, r13);
        r4[r5] = r10;
        r4 = chat_fileStatesDrawable;
        r10 = 6;
        r4 = r4[r10];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r10 = createCircleDrawableWithIcon(r10, r13);
        r4[r2] = r10;
        r4 = chat_fileStatesDrawable;
        r10 = 7;
        r4 = r4[r10];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r10 = createCircleDrawableWithIcon(r10, r11);
        r4[r5] = r10;
        r4 = chat_fileStatesDrawable;
        r10 = 7;
        r4 = r4[r10];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r10 = createCircleDrawableWithIcon(r10, r11);
        r4[r2] = r10;
        r4 = chat_fileStatesDrawable;
        r10 = 8;
        r4 = r4[r10];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r13 = NUM; // 0x7var_f8 float:1.79456E38 double:1.052935752E-314;
        r10 = createCircleDrawableWithIcon(r10, r13);
        r4[r5] = r10;
        r4 = chat_fileStatesDrawable;
        r10 = 8;
        r4 = r4[r10];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r10 = createCircleDrawableWithIcon(r10, r13);
        r4[r2] = r10;
        r4 = chat_fileStatesDrawable;
        r10 = 9;
        r4 = r4[r10];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r10 = createCircleDrawableWithIcon(r10, r12);
        r4[r5] = r10;
        r4 = chat_fileStatesDrawable;
        r10 = 9;
        r4 = r4[r10];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r10 = createCircleDrawableWithIcon(r10, r12);
        r4[r2] = r10;
        r4 = chat_photoStatesDrawables;
        r4 = r4[r5];
        r10 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r13 = createCircleDrawableWithIcon(r13, r11);
        r4[r5] = r13;
        r4 = chat_photoStatesDrawables;
        r4 = r4[r5];
        r13 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r13 = createCircleDrawableWithIcon(r13, r11);
        r4[r2] = r13;
        r4 = chat_photoStatesDrawables;
        r4 = r4[r2];
        r13 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r13 = createCircleDrawableWithIcon(r13, r12);
        r4[r5] = r13;
        r4 = chat_photoStatesDrawables;
        r4 = r4[r2];
        r13 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r13 = createCircleDrawableWithIcon(r13, r12);
        r4[r2] = r13;
        r4 = chat_photoStatesDrawables;
        r4 = r4[r1];
        r13 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r14 = NUM; // 0x7var_f9 float:1.7945602E38 double:1.0529357525E-314;
        r13 = createCircleDrawableWithIcon(r13, r14);
        r4[r5] = r13;
        r4 = chat_photoStatesDrawables;
        r4 = r4[r1];
        r13 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r13 = createCircleDrawableWithIcon(r13, r14);
        r4[r2] = r13;
        r4 = chat_photoStatesDrawables;
        r4 = r4[r7];
        r13 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r14 = NUM; // 0x7var_fc float:1.7945608E38 double:1.052935754E-314;
        r13 = createCircleDrawableWithIcon(r13, r14);
        r4[r5] = r13;
        r4 = chat_photoStatesDrawables;
        r4 = r4[r7];
        r7 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r13 = NUM; // 0x7var_fc float:1.7945608E38 double:1.052935754E-314;
        r7 = createCircleDrawableWithIcon(r7, r13);
        r4[r2] = r7;
        r4 = chat_photoStatesDrawables;
        r7 = r4[r8];
        r4 = r4[r8];
        r8 = NUM; // 0x7var_ float:1.7944821E38 double:1.0529355623E-314;
        r8 = r3.getDrawable(r8);
        r4[r2] = r8;
        r7[r5] = r8;
        r4 = chat_photoStatesDrawables;
        r7 = r4[r9];
        r4 = r4[r9];
        r8 = NUM; // 0x7var_ float:1.7944874E38 double:1.052935575E-314;
        r8 = r3.getDrawable(r8);
        r4[r2] = r8;
        r7[r5] = r8;
        r4 = chat_photoStatesDrawables;
        r7 = 6;
        r7 = r4[r7];
        r8 = 6;
        r4 = r4[r8];
        r8 = NUM; // 0x7var_ float:1.7945787E38 double:1.0529357975E-314;
        r8 = r3.getDrawable(r8);
        r4[r2] = r8;
        r7[r5] = r8;
        r4 = chat_photoStatesDrawables;
        r7 = 7;
        r4 = r4[r7];
        r7 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r7 = createCircleDrawableWithIcon(r7, r11);
        r4[r5] = r7;
        r4 = chat_photoStatesDrawables;
        r7 = 7;
        r4 = r4[r7];
        r7 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r7 = createCircleDrawableWithIcon(r7, r11);
        r4[r2] = r7;
        r4 = chat_photoStatesDrawables;
        r7 = 8;
        r4 = r4[r7];
        r7 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r7 = createCircleDrawableWithIcon(r7, r12);
        r4[r5] = r7;
        r4 = chat_photoStatesDrawables;
        r7 = 8;
        r4 = r4[r7];
        r7 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r7 = createCircleDrawableWithIcon(r7, r12);
        r4[r2] = r7;
        r4 = chat_photoStatesDrawables;
        r7 = 9;
        r4 = r4[r7];
        r7 = NUM; // 0x7var_b7 float:1.794495E38 double:1.0529355934E-314;
        r7 = r3.getDrawable(r7);
        r7 = r7.mutate();
        r4[r5] = r7;
        r4 = chat_photoStatesDrawables;
        r7 = 9;
        r4 = r4[r7];
        r7 = NUM; // 0x7var_b7 float:1.794495E38 double:1.0529355934E-314;
        r7 = r3.getDrawable(r7);
        r7 = r7.mutate();
        r4[r2] = r7;
        r4 = chat_photoStatesDrawables;
        r7 = 10;
        r4 = r4[r7];
        r7 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r7 = createCircleDrawableWithIcon(r7, r11);
        r4[r5] = r7;
        r4 = chat_photoStatesDrawables;
        r7 = 10;
        r4 = r4[r7];
        r7 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r7 = createCircleDrawableWithIcon(r7, r11);
        r4[r2] = r7;
        r4 = chat_photoStatesDrawables;
        r7 = 11;
        r4 = r4[r7];
        r7 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r7 = createCircleDrawableWithIcon(r7, r12);
        r4[r5] = r7;
        r4 = chat_photoStatesDrawables;
        r7 = 11;
        r4 = r4[r7];
        r7 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r7 = createCircleDrawableWithIcon(r7, r12);
        r4[r2] = r7;
        r4 = chat_photoStatesDrawables;
        r7 = 12;
        r4 = r4[r7];
        r7 = NUM; // 0x7var_b7 float:1.794495E38 double:1.0529355934E-314;
        r7 = r3.getDrawable(r7);
        r7 = r7.mutate();
        r4[r5] = r7;
        r4 = chat_photoStatesDrawables;
        r7 = 12;
        r4 = r4[r7];
        r7 = NUM; // 0x7var_b7 float:1.794495E38 double:1.0529355934E-314;
        r3 = r3.getDrawable(r7);
        r3 = r3.mutate();
        r4[r2] = r3;
        r3 = chat_contactDrawable;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r7 = NUM; // 0x7var_cc float:1.794551E38 double:1.0529357303E-314;
        r4 = createCircleDrawableWithIcon(r4, r7);
        r3[r5] = r4;
        r3 = chat_contactDrawable;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = NUM; // 0x7var_cc float:1.794551E38 double:1.0529357303E-314;
        r4 = createCircleDrawableWithIcon(r4, r6);
        r3[r2] = r4;
        r3 = chat_locationDrawable;
        r4 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r6 = NUM; // 0x7var_e1 float:1.7945554E38 double:1.0529357407E-314;
        r4 = createRoundRectDrawableWithIcon(r4, r6);
        r3[r5] = r4;
        r3 = chat_locationDrawable;
        r4 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = createRoundRectDrawableWithIcon(r4, r6);
        r3[r2] = r4;
        r3 = r15.getResources();
        r4 = NUM; // 0x7var_aa float:1.7944923E38 double:1.052935587E-314;
        r3 = r3.getDrawable(r4);
        chat_composeShadowDrawable = r3;
        r3 = org.telegram.messenger.AndroidUtilities.roundMessageSize;	 Catch:{ all -> 0x0bc9 }
        r4 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ all -> 0x0bc9 }
        r3 = r3 + r4;
        r4 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ all -> 0x0bc9 }
        r4 = android.graphics.Bitmap.createBitmap(r3, r3, r4);	 Catch:{ all -> 0x0bc9 }
        r6 = new android.graphics.Canvas;	 Catch:{ all -> 0x0bc9 }
        r6.<init>(r4);	 Catch:{ all -> 0x0bc9 }
        r7 = new android.graphics.Paint;	 Catch:{ all -> 0x0bc9 }
        r7.<init>(r2);	 Catch:{ all -> 0x0bc9 }
        r7.setColor(r5);	 Catch:{ all -> 0x0bc9 }
        r8 = android.graphics.Paint.Style.FILL;	 Catch:{ all -> 0x0bc9 }
        r7.setStyle(r8);	 Catch:{ all -> 0x0bc9 }
        r8 = new android.graphics.PorterDuffXfermode;	 Catch:{ all -> 0x0bc9 }
        r9 = android.graphics.PorterDuff.Mode.CLEAR;	 Catch:{ all -> 0x0bc9 }
        r8.<init>(r9);	 Catch:{ all -> 0x0bc9 }
        r7.setXfermode(r8);	 Catch:{ all -> 0x0bc9 }
        r8 = new android.graphics.Paint;	 Catch:{ all -> 0x0bc9 }
        r8.<init>(r2);	 Catch:{ all -> 0x0bc9 }
        r2 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);	 Catch:{ all -> 0x0bc9 }
        r2 = (float) r2;	 Catch:{ all -> 0x0bc9 }
        r9 = 0;
        r10 = 0;
        r11 = NUM; // 0x5var_ float:9.223372E18 double:7.874593756E-315;
        r8.setShadowLayer(r2, r9, r10, r11);	 Catch:{ all -> 0x0bc9 }
    L_0x0ba0:
        if (r5 >= r1) goto L_0x0bbe;
    L_0x0ba2:
        r2 = r3 / 2;
        r2 = (float) r2;	 Catch:{ all -> 0x0bc9 }
        r9 = r3 / 2;
        r9 = (float) r9;	 Catch:{ all -> 0x0bc9 }
        r10 = org.telegram.messenger.AndroidUtilities.roundMessageSize;	 Catch:{ all -> 0x0bc9 }
        r10 = r10 / r1;
        r11 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);	 Catch:{ all -> 0x0bc9 }
        r10 = r10 - r11;
        r10 = (float) r10;	 Catch:{ all -> 0x0bc9 }
        if (r5 != 0) goto L_0x0bb7;
    L_0x0bb5:
        r11 = r8;
        goto L_0x0bb8;
    L_0x0bb7:
        r11 = r7;
    L_0x0bb8:
        r6.drawCircle(r2, r9, r10, r11);	 Catch:{ all -> 0x0bc9 }
        r5 = r5 + 1;
        goto L_0x0ba0;
    L_0x0bbe:
        r2 = 0;
        r6.setBitmap(r2);	 Catch:{ Exception -> 0x0bc2 }
    L_0x0bc2:
        r2 = new android.graphics.drawable.BitmapDrawable;	 Catch:{ all -> 0x0bc9 }
        r2.<init>(r4);	 Catch:{ all -> 0x0bc9 }
        chat_roundVideoShadow = r2;	 Catch:{ all -> 0x0bc9 }
    L_0x0bc9:
        applyChatTheme(r16);
    L_0x0bcc:
        r2 = chat_msgTextPaintOneEmoji;
        r3 = NUM; // 0x41e00000 float:28.0 double:5.46040909E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r2.setTextSize(r3);
        r2 = chat_msgTextPaintTwoEmoji;
        r3 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r2.setTextSize(r3);
        r2 = chat_msgTextPaintThreeEmoji;
        r3 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r2.setTextSize(r3);
        r2 = chat_msgTextPaint;
        r3 = org.telegram.messenger.SharedConfig.fontSize;
        r3 = (float) r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r2.setTextSize(r3);
        r2 = chat_msgGameTextPaint;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r3 = (float) r3;
        r2.setTextSize(r3);
        r2 = chat_msgBotButtonPaint;
        r3 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = (float) r4;
        r2.setTextSize(r4);
        if (r16 != 0) goto L_0x0d5b;
    L_0x0CLASSNAME:
        r2 = chat_botProgressPaint;
        if (r2 == 0) goto L_0x0d5b;
    L_0x0CLASSNAME:
        r4 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r2.setStrokeWidth(r4);
        r2 = chat_infoPaint;
        r4 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r2.setTextSize(r4);
        r2 = chat_docNamePaint;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = (float) r4;
        r2.setTextSize(r4);
        r2 = chat_locationTitlePaint;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = (float) r4;
        r2.setTextSize(r4);
        r2 = chat_locationAddressPaint;
        r4 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = (float) r5;
        r2.setTextSize(r5);
        r2 = chat_audioTimePaint;
        r5 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r2.setTextSize(r5);
        r2 = chat_livePaint;
        r5 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r2.setTextSize(r5);
        r2 = chat_audioTitlePaint;
        r5 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r2.setTextSize(r5);
        r2 = chat_audioPerformerPaint;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r5 = (float) r5;
        r2.setTextSize(r5);
        r2 = chat_botButtonPaint;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r5 = (float) r5;
        r2.setTextSize(r5);
        r2 = chat_contactNamePaint;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r5 = (float) r5;
        r2.setTextSize(r5);
        r2 = chat_contactPhonePaint;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = (float) r5;
        r2.setTextSize(r5);
        r2 = chat_durationPaint;
        r5 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r2.setTextSize(r5);
        r2 = chat_timePaint;
        r5 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r2.setTextSize(r5);
        r2 = chat_adminPaint;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = (float) r5;
        r2.setTextSize(r5);
        r2 = chat_namePaint;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r5 = (float) r5;
        r2.setTextSize(r5);
        r2 = chat_forwardNamePaint;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r5 = (float) r5;
        r2.setTextSize(r5);
        r2 = chat_replyNamePaint;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r5 = (float) r5;
        r2.setTextSize(r5);
        r2 = chat_replyTextPaint;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r0 = (float) r0;
        r2.setTextSize(r0);
        r0 = chat_gamePaint;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = (float) r2;
        r0.setTextSize(r2);
        r0 = chat_shipmentPaint;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = (float) r2;
        r0.setTextSize(r2);
        r0 = chat_instantViewPaint;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = (float) r2;
        r0.setTextSize(r2);
        r0 = chat_instantViewRectPaint;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r0.setStrokeWidth(r2);
        r0 = chat_statusRecordPaint;
        r2 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r0.setStrokeWidth(r2);
        r0 = chat_actionTextPaint;
        r2 = 16;
        r5 = org.telegram.messenger.SharedConfig.fontSize;
        r2 = java.lang.Math.max(r2, r5);
        r2 = r2 - r1;
        r1 = (float) r2;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = (float) r1;
        r0.setTextSize(r1);
        r0 = chat_contextResult_titleTextPaint;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r1 = (float) r1;
        r0.setTextSize(r1);
        r0 = chat_contextResult_descriptionTextPaint;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r1 = (float) r1;
        r0.setTextSize(r1);
        r0 = chat_radialProgressPaint;
        r1 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = (float) r1;
        r0.setStrokeWidth(r1);
        r0 = chat_radialProgress2Paint;
        r1 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = (float) r1;
        r0.setStrokeWidth(r1);
    L_0x0d5b:
        return;
    L_0x0d5c:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0d5c }
        goto L_0x0d60;
    L_0x0d5f:
        throw r0;
    L_0x0d60:
        goto L_0x0d5f;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.createChatResources(android.content.Context, boolean):void");
    }

    public static void applyChatTheme(boolean z) {
        if (!(chat_msgTextPaint == null || chat_msgInDrawable == null || z)) {
            int i;
            chat_gamePaint.setColor(getColor("chat_previewGameText"));
            chat_durationPaint.setColor(getColor("chat_previewDurationText"));
            chat_botButtonPaint.setColor(getColor("chat_botButtonText"));
            chat_urlPaint.setColor(getColor("chat_linkSelectBackground"));
            chat_botProgressPaint.setColor(getColor("chat_botProgress"));
            chat_deleteProgressPaint.setColor(getColor("chat_secretTimeText"));
            chat_textSearchSelectionPaint.setColor(getColor("chat_textSelectBackground"));
            chat_msgErrorPaint.setColor(getColor("chat_sentError"));
            String str = "chat_status";
            chat_statusPaint.setColor(getColor(str));
            chat_statusRecordPaint.setColor(getColor(str));
            str = "chat_serviceText";
            chat_actionTextPaint.setColor(getColor(str));
            chat_actionTextPaint.linkColor = getColor("chat_serviceLink");
            chat_contextResult_titleTextPaint.setColor(getColor("windowBackgroundWhiteBlackText"));
            chat_composeBackgroundPaint.setColor(getColor("chat_messagePanelBackground"));
            chat_timeBackgroundPaint.setColor(getColor("chat_mediaTimeBackground"));
            setDrawableColorByKey(chat_msgNoSoundDrawable, "chat_mediaTimeText");
            String str2 = "chat_inBubble";
            setDrawableColorByKey(chat_msgInDrawable, str2);
            String str3 = "chat_inBubbleSelected";
            setDrawableColorByKey(chat_msgInSelectedDrawable, str3);
            String str4 = "chat_inBubbleShadow";
            setDrawableColorByKey(chat_msgInShadowDrawable, str4);
            String str5 = "chat_outBubbleShadow";
            setDrawableColorByKey(chat_msgOutShadowDrawable, str5);
            setDrawableColorByKey(chat_msgInMediaDrawable, str2);
            setDrawableColorByKey(chat_msgInMediaSelectedDrawable, str3);
            setDrawableColorByKey(chat_msgInMediaShadowDrawable, str4);
            setDrawableColorByKey(chat_msgOutMediaShadowDrawable, str5);
            setDrawableColorByKey(chat_msgOutCheckDrawable, "chat_outSentCheck");
            setDrawableColorByKey(chat_msgOutCheckSelectedDrawable, "chat_outSentCheckSelected");
            str2 = "chat_outSentCheckRead";
            setDrawableColorByKey(chat_msgOutCheckReadDrawable, str2);
            str3 = "chat_outSentCheckReadSelected";
            setDrawableColorByKey(chat_msgOutCheckReadSelectedDrawable, str3);
            setDrawableColorByKey(chat_msgOutHalfCheckDrawable, str2);
            setDrawableColorByKey(chat_msgOutHalfCheckSelectedDrawable, str3);
            setDrawableColorByKey(chat_msgOutClockDrawable, "chat_outSentClock");
            setDrawableColorByKey(chat_msgOutSelectedClockDrawable, "chat_outSentClockSelected");
            setDrawableColorByKey(chat_msgInClockDrawable, "chat_inSentClock");
            setDrawableColorByKey(chat_msgInSelectedClockDrawable, "chat_inSentClockSelected");
            setDrawableColorByKey(chat_msgMediaCheckDrawable, "chat_mediaSentCheck");
            setDrawableColorByKey(chat_msgMediaHalfCheckDrawable, "chat_mediaSentCheck");
            setDrawableColorByKey(chat_msgMediaClockDrawable, "chat_mediaSentClock");
            setDrawableColorByKey(chat_msgStickerCheckDrawable, str);
            setDrawableColorByKey(chat_msgStickerHalfCheckDrawable, str);
            setDrawableColorByKey(chat_msgStickerClockDrawable, str);
            setDrawableColorByKey(chat_msgStickerViewsDrawable, str);
            str = "chat_serviceIcon";
            setDrawableColorByKey(chat_shareIconDrawable, str);
            setDrawableColorByKey(chat_replyIconDrawable, str);
            setDrawableColorByKey(chat_goIconDrawable, str);
            setDrawableColorByKey(chat_botInlineDrawable, str);
            setDrawableColorByKey(chat_botLinkDrawalbe, str);
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
            str = "chat_inlineResultIcon";
            setDrawableColorByKey(chat_inlineResultFile, str);
            setDrawableColorByKey(chat_inlineResultAudio, str);
            setDrawableColorByKey(chat_inlineResultLocation, str);
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
            for (i = 0; i < 2; i++) {
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i][0], getColor("chat_outLoader"), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i][0], getColor("chat_outMediaIcon"), true);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i][1], getColor("chat_outLoaderSelected"), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i][1], getColor("chat_outMediaIconSelected"), true);
                int i2 = i + 2;
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i2][0], getColor("chat_inLoader"), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i2][0], getColor("chat_inMediaIcon"), true);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i2][1], getColor("chat_inLoaderSelected"), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i2][1], getColor("chat_inMediaIconSelected"), true);
                i2 = i + 4;
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i2][0], getColor("chat_mediaLoaderPhoto"), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i2][0], getColor("chat_mediaLoaderPhotoIcon"), true);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i2][1], getColor("chat_mediaLoaderPhotoSelected"), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i2][1], getColor("chat_mediaLoaderPhotoIconSelected"), true);
            }
            for (i = 0; i < 5; i++) {
                setCombinedDrawableColor(chat_fileStatesDrawable[i][0], getColor("chat_outLoader"), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[i][0], getColor("chat_outMediaIcon"), true);
                setCombinedDrawableColor(chat_fileStatesDrawable[i][1], getColor("chat_outLoaderSelected"), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[i][1], getColor("chat_outMediaIconSelected"), true);
                int i3 = i + 5;
                setCombinedDrawableColor(chat_fileStatesDrawable[i3][0], getColor("chat_inLoader"), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[i3][0], getColor("chat_inMediaIcon"), true);
                setCombinedDrawableColor(chat_fileStatesDrawable[i3][1], getColor("chat_inLoaderSelected"), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[i3][1], getColor("chat_inMediaIconSelected"), true);
            }
            for (i = 0; i < 4; i++) {
                setCombinedDrawableColor(chat_photoStatesDrawables[i][0], getColor("chat_mediaLoaderPhoto"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i][0], getColor("chat_mediaLoaderPhotoIcon"), true);
                setCombinedDrawableColor(chat_photoStatesDrawables[i][1], getColor("chat_mediaLoaderPhotoSelected"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i][1], getColor("chat_mediaLoaderPhotoIconSelected"), true);
            }
            for (i = 0; i < 2; i++) {
                int i4 = i + 7;
                setCombinedDrawableColor(chat_photoStatesDrawables[i4][0], getColor("chat_outLoaderPhoto"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i4][0], getColor("chat_outLoaderPhotoIcon"), true);
                setCombinedDrawableColor(chat_photoStatesDrawables[i4][1], getColor("chat_outLoaderPhotoSelected"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i4][1], getColor("chat_outLoaderPhotoIconSelected"), true);
                i4 = i + 10;
                setCombinedDrawableColor(chat_photoStatesDrawables[i4][0], getColor("chat_inLoaderPhoto"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i4][0], getColor("chat_inLoaderPhotoIcon"), true);
                setCombinedDrawableColor(chat_photoStatesDrawables[i4][1], getColor("chat_inLoaderPhotoSelected"), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i4][1], getColor("chat_inLoaderPhotoIconSelected"), true);
            }
            setDrawableColorByKey(chat_photoStatesDrawables[9][0], "chat_outFileIcon");
            setDrawableColorByKey(chat_photoStatesDrawables[9][1], "chat_outFileSelectedIcon");
            setDrawableColorByKey(chat_photoStatesDrawables[12][0], "chat_inFileIcon");
            setDrawableColorByKey(chat_photoStatesDrawables[12][1], "chat_inFileSelectedIcon");
            setCombinedDrawableColor(chat_contactDrawable[0], getColor("chat_inContactBackground"), false);
            setCombinedDrawableColor(chat_contactDrawable[0], getColor("chat_inContactIcon"), true);
            setCombinedDrawableColor(chat_contactDrawable[1], getColor("chat_outContactBackground"), false);
            setCombinedDrawableColor(chat_contactDrawable[1], getColor("chat_outContactIcon"), true);
            setCombinedDrawableColor(chat_locationDrawable[0], getColor("chat_inLocationBackground"), false);
            setCombinedDrawableColor(chat_locationDrawable[0], getColor("chat_inLocationIcon"), true);
            setCombinedDrawableColor(chat_locationDrawable[1], getColor("chat_outLocationBackground"), false);
            setCombinedDrawableColor(chat_locationDrawable[1], getColor("chat_outLocationIcon"), true);
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
            setDrawableColor(chat_attachEmptyDrawable, getColor("chat_attachEmptyImage"));
            applyChatServiceMessageColor();
        }
    }

    public static void applyChatServiceMessageColor() {
        applyChatServiceMessageColor(null);
    }

    public static void applyChatServiceMessageColor(int[] iArr) {
        if (chat_actionBackgroundPaint != null) {
            Integer num;
            Integer num2;
            Integer valueOf;
            Integer valueOf2;
            serviceMessageColor = serviceMessageColorBackup;
            serviceSelectedMessageColor = serviceSelectedMessageColorBackup;
            int i = 0;
            if (iArr == null || iArr.length < 2) {
                num = (Integer) currentColors.get("chat_serviceBackground");
                num2 = (Integer) currentColors.get("chat_serviceBackgroundSelected");
            } else {
                num = Integer.valueOf(iArr[0]);
                num2 = Integer.valueOf(iArr[1]);
                serviceMessageColor = iArr[0];
                serviceSelectedMessageColor = iArr[1];
            }
            if (num == null) {
                num = Integer.valueOf(serviceMessageColor);
                valueOf = Integer.valueOf(serviceMessage2Color);
            } else {
                valueOf = num;
            }
            if (num2 == null) {
                num2 = Integer.valueOf(serviceSelectedMessageColor);
                valueOf2 = Integer.valueOf(serviceSelectedMessage2Color);
            } else {
                valueOf2 = num2;
            }
            if (currentColor != num.intValue()) {
                chat_actionBackgroundPaint.setColor(num.intValue());
                colorFilter = new PorterDuffColorFilter(num.intValue(), Mode.MULTIPLY);
                colorFilter2 = new PorterDuffColorFilter(valueOf.intValue(), Mode.MULTIPLY);
                currentColor = num.intValue();
                if (chat_cornerOuter[0] != null) {
                    while (i < 4) {
                        chat_cornerOuter[i].setColorFilter(colorFilter);
                        chat_cornerInner[i].setColorFilter(colorFilter);
                        i++;
                    }
                }
            }
            if (currentSelectedColor != num2.intValue()) {
                currentSelectedColor = num2.intValue();
                colorPressedFilter = new PorterDuffColorFilter(num2.intValue(), Mode.MULTIPLY);
                colorPressedFilter2 = new PorterDuffColorFilter(valueOf2.intValue(), Mode.MULTIPLY);
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
                currentShareSelectedColorFilter = new PorterDuffColorFilter(i, Mode.MULTIPLY);
            }
            return currentShareSelectedColorFilter;
        }
        if (currentShareColorFilter == null || currentShareColorFilterColor != i) {
            currentShareColorFilterColor = i;
            currentShareColorFilter = new PorterDuffColorFilter(i, Mode.MULTIPLY);
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
        mutate.setColorFilter(new PorterDuffColorFilter(i2, Mode.MULTIPLY));
        return mutate;
    }

    public static int getDefaultColor(String str) {
        Integer num = (Integer) defaultColors.get(str);
        if (num == null) {
            return (str.equals("chats_menuTopShadow") || str.equals("chats_menuTopBackground") || str.equals("chats_menuTopShadowCats")) ? 0 : -65536;
        } else {
            return num.intValue();
        }
    }

    public static boolean hasThemeKey(String str) {
        return currentColors.containsKey(str);
    }

    public static Integer getColorOrNull(String str) {
        Integer num = (Integer) currentColors.get(str);
        if (num != null) {
            return num;
        }
        if (((String) fallbackKeys.get(str)) != null) {
            num = (Integer) currentColors.get(str);
        }
        return num == null ? (Integer) defaultColors.get(str) : num;
    }

    public static void setAnimatingColor(boolean z) {
        animatingColors = z ? new HashMap() : null;
    }

    public static boolean isAnimatingColor() {
        return animatingColors != null;
    }

    public static void setAnimatedColor(String str, int i) {
        HashMap hashMap = animatingColors;
        if (hashMap != null) {
            hashMap.put(str, Integer.valueOf(i));
        }
    }

    public static int getDefaultAccentColor(String str) {
        Integer num = (Integer) currentColorsNoAccent.get(str);
        if (num == null) {
            return 0;
        }
        ThemeAccent accent = currentTheme.getAccent(false);
        if (accent == null) {
            return 0;
        }
        float[] tempHsv = getTempHsv(1);
        float[] tempHsv2 = getTempHsv(2);
        Color.colorToHSV(currentTheme.accentBaseColor, tempHsv);
        Color.colorToHSV(accent.accentColor, tempHsv2);
        return changeColorAccent(tempHsv, tempHsv2, num.intValue(), currentTheme.isDark());
    }

    public static int getColor(String str) {
        return getColor(str, null, false);
    }

    public static int getColor(String str, boolean[] zArr) {
        return getColor(str, zArr, false);
    }

    public static int getColor(String str, boolean[] zArr, boolean z) {
        Integer num;
        if (!z) {
            HashMap hashMap = animatingColors;
            if (hashMap != null) {
                num = (Integer) hashMap.get(str);
                if (num != null) {
                    return num.intValue();
                }
            }
        }
        String str2 = "chat_serviceBackgroundSelected";
        String str3 = "chat_serviceBackground";
        if (currentTheme == defaultTheme) {
            z = myMessagesColorKeys.contains(str) ? currentTheme.isDefaultMyMessages() : ("chat_wallpaper".equals(str) || "chat_wallpaper_gradient_to".equals(str)) ? false : currentTheme.isDefaultMainAccent();
            if (z) {
                if (str.equals(str3)) {
                    return serviceMessageColor;
                }
                if (str.equals(str2)) {
                    return serviceSelectedMessageColor;
                }
                return getDefaultColor(str);
            }
        }
        num = (Integer) currentColors.get(str);
        if (num == null) {
            String str4 = (String) fallbackKeys.get(str);
            if (str4 != null) {
                num = (Integer) currentColors.get(str4);
            }
            if (num == null) {
                if (zArr != null) {
                    zArr[0] = true;
                }
                if (str.equals(str3)) {
                    return serviceMessageColor;
                }
                if (str.equals(str2)) {
                    return serviceSelectedMessageColor;
                }
                return getDefaultColor(str);
            }
        }
        return num.intValue();
    }

    public static void setColor(String str, int i, boolean z) {
        String str2 = "chat_wallpaper";
        String str3 = "chat_wallpaper_gradient_to";
        if (str.equals(str2) || str.equals(str3)) {
            i |= -16777216;
        }
        if (z) {
            currentColors.remove(str);
        } else {
            currentColors.put(str, Integer.valueOf(i));
        }
        Object obj = -1;
        switch (str.hashCode()) {
            case -2095843767:
                if (str.equals("chat_wallpaper_gradient_rotation")) {
                    obj = 4;
                    break;
                }
                break;
            case -1625862693:
                if (str.equals(str2)) {
                    obj = 2;
                    break;
                }
                break;
            case -1397026623:
                if (str.equals("windowBackgroundGray")) {
                    obj = 6;
                    break;
                }
                break;
            case -633951866:
                if (str.equals(str3)) {
                    obj = 3;
                    break;
                }
                break;
            case -552118908:
                if (str.equals("actionBarDefault")) {
                    obj = 5;
                    break;
                }
                break;
            case 426061980:
                if (str.equals("chat_serviceBackground")) {
                    obj = null;
                    break;
                }
                break;
            case 1573464919:
                if (str.equals("chat_serviceBackgroundSelected")) {
                    obj = 1;
                    break;
                }
                break;
        }
        switch (obj) {
            case null:
            case 1:
                applyChatServiceMessageColor();
                return;
            case 2:
            case 3:
            case 4:
                reloadWallpaper();
                return;
            case 5:
                if (VERSION.SDK_INT >= 23) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, new Object[0]);
                    return;
                }
                return;
            case 6:
                if (VERSION.SDK_INT >= 26) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, new Object[0]);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public static void setThemeWallpaper(ThemeInfo themeInfo, Bitmap bitmap, File file) {
        currentColors.remove("chat_wallpaper");
        currentColors.remove("chat_wallpaper_gradient_to");
        currentColors.remove("chat_wallpaper_gradient_rotation");
        themedWallpaperLink = null;
        themeInfo.setOverrideWallpaper(null);
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
                drawable.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
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
                drawable = getStateDrawable(drawable, 0);
                if (drawable instanceof ShapeDrawable) {
                    ((ShapeDrawable) drawable).getPaint().setColor(i);
                    return;
                } else {
                    drawable.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                    return;
                }
            } catch (Throwable unused) {
                return;
            }
        }
        drawable = getStateDrawable(drawable, 1);
        if (drawable instanceof ShapeDrawable) {
            ((ShapeDrawable) drawable).getPaint().setColor(i);
        } else {
            drawable.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
        }
    }

    @SuppressLint({"DiscouragedPrivateApi"})
    @TargetApi(21)
    public static void setRippleDrawableForceSoftware(RippleDrawable rippleDrawable) {
        if (rippleDrawable != null) {
            try {
                RippleDrawable.class.getDeclaredMethod("setForceSoftware", new Class[]{Boolean.TYPE}).invoke(rippleDrawable, new Object[]{Boolean.valueOf(true)});
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
                        stateDrawable.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                    }
                    drawable = getStateDrawable(drawable, 1);
                    if (drawable instanceof ShapeDrawable) {
                        ((ShapeDrawable) drawable).getPaint().setColor(i);
                        return;
                    } else {
                        drawable.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                        return;
                    }
                } catch (Throwable unused) {
                    return;
                }
            }
            drawable = getStateDrawable(drawable, 2);
            if (drawable instanceof ShapeDrawable) {
                ((ShapeDrawable) drawable).getPaint().setColor(i);
            } else {
                drawable.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
            }
        } else if (VERSION.SDK_INT >= 21 && (drawable instanceof RippleDrawable)) {
            RippleDrawable rippleDrawable = (RippleDrawable) drawable;
            if (z) {
                rippleDrawable.setColor(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{i}));
            } else if (rippleDrawable.getNumberOfLayers() > 0) {
                drawable = rippleDrawable.getDrawable(0);
                if (drawable instanceof ShapeDrawable) {
                    ((ShapeDrawable) drawable).getPaint().setColor(i);
                } else {
                    drawable.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
                }
            }
        }
    }

    public static boolean isThemeWallpaperPublic() {
        return TextUtils.isEmpty(themedWallpaperLink) ^ 1;
    }

    public static boolean hasWallpaperFromTheme() {
        ThemeInfo themeInfo = currentTheme;
        boolean z = false;
        if (themeInfo.firstAccentIsDefault && themeInfo.currentAccentId == DEFALT_THEME_ACCENT_ID) {
            return false;
        }
        if (currentColors.containsKey("chat_wallpaper") || themedWallpaperFileOffset > 0 || !TextUtils.isEmpty(themedWallpaperLink)) {
            z = true;
        }
        return z;
    }

    public static boolean isCustomTheme() {
        return isCustomTheme;
    }

    public static void reloadWallpaper() {
        Disposable disposable = backgroundGradientDisposable;
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
            i = calcDrawableColor[0];
            serviceMessageColorBackup = i;
            serviceMessageColor = i;
            i = calcDrawableColor[1];
            serviceSelectedMessageColorBackup = i;
            serviceSelectedMessageColor = i;
            serviceMessage2Color = calcDrawableColor[2];
            serviceSelectedMessage2Color = calcDrawableColor[3];
        }
    }

    public static int getServiceMessageColor() {
        Integer num = (Integer) currentColors.get("chat_serviceBackground");
        return num == null ? serviceMessageColor : num.intValue();
    }

    public static void loadWallpaper() {
        if (wallpaper == null) {
            File file;
            ThemeInfo themeInfo = currentTheme;
            boolean z = false;
            boolean z2 = themeInfo.firstAccentIsDefault && themeInfo.currentAccentId == DEFALT_THEME_ACCENT_ID;
            ThemeAccent accent = currentTheme.getAccent(false);
            if (accent == null || previousTheme != null) {
                file = null;
            } else {
                File pathToWallpaper = accent.getPathToWallpaper();
                z = accent.patternMotion;
                file = pathToWallpaper;
            }
            Utilities.searchQueue.postRunnable(new -$$Lambda$Theme$2f8NbctloYmhmpPWZuMMuEu_eSE(currentTheme.overrideWallpaper, z2, file, z));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:124:0x020f A:{Catch:{ all -> 0x016e, all -> 0x006a }} */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x017e A:{Catch:{ all -> 0x016e, all -> 0x006a }} */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x020f A:{Catch:{ all -> 0x016e, all -> 0x006a }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:121:0x020b */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0168 A:{SYNTHETIC, Splitter:B:85:0x0168} */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x017e A:{Catch:{ all -> 0x016e, all -> 0x006a }} */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x017e A:{Catch:{ all -> 0x016e, all -> 0x006a }} */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:37:0x0058, B:82:0x0163] */
    /* JADX WARNING: Can't wrap try/catch for region: R(7:(1:99)(1:100)|101|(3:103|104|(1:106)(5:107|(1:(2:116|(1:118)(1:119))(2:112|(1:114)(1:115)))|121|122|(2:(1:125)|126)))|120|121|122|(0)) */
    /* JADX WARNING: Missing block: B:39:0x006a, code skipped:
            r10 = move-exception;
     */
    /* JADX WARNING: Missing block: B:41:?, code skipped:
            org.telegram.messenger.FileLog.e(r10);
     */
    /* JADX WARNING: Missing block: B:89:0x016f, code skipped:
            if (r1 != null) goto L_0x0171;
     */
    /* JADX WARNING: Missing block: B:91:?, code skipped:
            r1.close();
     */
    /* JADX WARNING: Missing block: B:92:0x0175, code skipped:
            r10 = move-exception;
     */
    /* JADX WARNING: Missing block: B:94:?, code skipped:
            org.telegram.messenger.FileLog.e(r10);
     */
    static /* synthetic */ void lambda$loadWallpaper$8(org.telegram.ui.ActionBar.Theme.OverrideWallpaperInfo r9, boolean r10, java.io.File r11, boolean r12) {
        /*
        r0 = wallpaperSync;
        monitor-enter(r0);
        r1 = hasPreviousTheme;	 Catch:{ all -> 0x0227 }
        r2 = 0;
        r3 = 1;
        if (r1 == 0) goto L_0x000d;
    L_0x0009:
        r1 = isApplyingAccent;	 Catch:{ all -> 0x0227 }
        if (r1 == 0) goto L_0x0011;
    L_0x000d:
        if (r9 == 0) goto L_0x0011;
    L_0x000f:
        r1 = 1;
        goto L_0x0012;
    L_0x0011:
        r1 = 0;
    L_0x0012:
        if (r9 == 0) goto L_0x002b;
    L_0x0014:
        if (r9 == 0) goto L_0x001c;
    L_0x0016:
        r4 = r9.isMotion;	 Catch:{ all -> 0x0227 }
        if (r4 == 0) goto L_0x001c;
    L_0x001a:
        r4 = 1;
        goto L_0x001d;
    L_0x001c:
        r4 = 0;
    L_0x001d:
        isWallpaperMotion = r4;	 Catch:{ all -> 0x0227 }
        if (r9 == 0) goto L_0x0027;
    L_0x0021:
        r4 = r9.color;	 Catch:{ all -> 0x0227 }
        if (r4 == 0) goto L_0x0027;
    L_0x0025:
        r4 = 1;
        goto L_0x0028;
    L_0x0027:
        r4 = 0;
    L_0x0028:
        isPatternWallpaper = r4;	 Catch:{ all -> 0x0227 }
        goto L_0x003c;
    L_0x002b:
        r4 = currentTheme;	 Catch:{ all -> 0x0227 }
        r4 = r4.isMotion;	 Catch:{ all -> 0x0227 }
        isWallpaperMotion = r4;	 Catch:{ all -> 0x0227 }
        r4 = currentTheme;	 Catch:{ all -> 0x0227 }
        r4 = r4.patternBgColor;	 Catch:{ all -> 0x0227 }
        if (r4 == 0) goto L_0x0039;
    L_0x0037:
        r4 = 1;
        goto L_0x003a;
    L_0x0039:
        r4 = 0;
    L_0x003a:
        isPatternWallpaper = r4;	 Catch:{ all -> 0x0227 }
    L_0x003c:
        r4 = 100;
        r6 = 2;
        if (r1 != 0) goto L_0x017a;
    L_0x0041:
        r1 = 0;
        if (r10 == 0) goto L_0x0046;
    L_0x0044:
        r10 = r1;
        goto L_0x0050;
    L_0x0046:
        r10 = currentColors;	 Catch:{ all -> 0x0227 }
        r7 = "chat_wallpaper";
        r10 = r10.get(r7);	 Catch:{ all -> 0x0227 }
        r10 = (java.lang.Integer) r10;	 Catch:{ all -> 0x0227 }
    L_0x0050:
        if (r11 == 0) goto L_0x0070;
    L_0x0052:
        r7 = r11.exists();	 Catch:{ all -> 0x0227 }
        if (r7 == 0) goto L_0x0070;
    L_0x0058:
        r10 = r11.getAbsolutePath();	 Catch:{ all -> 0x006a }
        r10 = android.graphics.drawable.Drawable.createFromPath(r10);	 Catch:{ all -> 0x006a }
        wallpaper = r10;	 Catch:{ all -> 0x006a }
        isWallpaperMotion = r12;	 Catch:{ all -> 0x006a }
        isCustomTheme = r3;	 Catch:{ all -> 0x006a }
        isPatternWallpaper = r3;	 Catch:{ all -> 0x006a }
        goto L_0x017a;
    L_0x006a:
        r10 = move-exception;
        org.telegram.messenger.FileLog.e(r10);	 Catch:{ all -> 0x0227 }
        goto L_0x017a;
    L_0x0070:
        if (r10 == 0) goto L_0x00d3;
    L_0x0072:
        r11 = currentColors;	 Catch:{ all -> 0x0227 }
        r12 = "chat_wallpaper_gradient_to";
        r11 = r11.get(r12);	 Catch:{ all -> 0x0227 }
        r11 = (java.lang.Integer) r11;	 Catch:{ all -> 0x0227 }
        r12 = currentColors;	 Catch:{ all -> 0x0227 }
        r1 = "chat_wallpaper_gradient_rotation";
        r12 = r12.get(r1);	 Catch:{ all -> 0x0227 }
        r12 = (java.lang.Integer) r12;	 Catch:{ all -> 0x0227 }
        if (r12 != 0) goto L_0x008e;
    L_0x0088:
        r12 = 45;
        r12 = java.lang.Integer.valueOf(r12);	 Catch:{ all -> 0x0227 }
    L_0x008e:
        if (r11 == 0) goto L_0x00c4;
    L_0x0090:
        r1 = r11.equals(r10);	 Catch:{ all -> 0x0227 }
        if (r1 == 0) goto L_0x0097;
    L_0x0096:
        goto L_0x00c4;
    L_0x0097:
        r1 = new int[r6];	 Catch:{ all -> 0x0227 }
        r10 = r10.intValue();	 Catch:{ all -> 0x0227 }
        r1[r2] = r10;	 Catch:{ all -> 0x0227 }
        r10 = r11.intValue();	 Catch:{ all -> 0x0227 }
        r1[r3] = r10;	 Catch:{ all -> 0x0227 }
        r10 = r12.intValue();	 Catch:{ all -> 0x0227 }
        r10 = org.telegram.ui.Components.BackgroundGradientDrawable.getGradientOrientation(r10);	 Catch:{ all -> 0x0227 }
        r11 = new org.telegram.ui.Components.BackgroundGradientDrawable;	 Catch:{ all -> 0x0227 }
        r11.<init>(r10, r1);	 Catch:{ all -> 0x0227 }
        r10 = new org.telegram.ui.ActionBar.Theme$9;	 Catch:{ all -> 0x0227 }
        r10.<init>();	 Catch:{ all -> 0x0227 }
        r12 = org.telegram.ui.Components.BackgroundGradientDrawable.Sizes.ofDeviceScreen();	 Catch:{ all -> 0x0227 }
        r10 = r11.startDithering(r12, r10, r4);	 Catch:{ all -> 0x0227 }
        backgroundGradientDisposable = r10;	 Catch:{ all -> 0x0227 }
        wallpaper = r11;	 Catch:{ all -> 0x0227 }
        goto L_0x00cf;
    L_0x00c4:
        r11 = new android.graphics.drawable.ColorDrawable;	 Catch:{ all -> 0x0227 }
        r10 = r10.intValue();	 Catch:{ all -> 0x0227 }
        r11.<init>(r10);	 Catch:{ all -> 0x0227 }
        wallpaper = r11;	 Catch:{ all -> 0x0227 }
    L_0x00cf:
        isCustomTheme = r3;	 Catch:{ all -> 0x0227 }
        goto L_0x017a;
    L_0x00d3:
        r10 = themedWallpaperLink;	 Catch:{ all -> 0x0227 }
        if (r10 == 0) goto L_0x010e;
    L_0x00d7:
        r10 = new java.io.File;	 Catch:{ all -> 0x0227 }
        r11 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();	 Catch:{ all -> 0x0227 }
        r12 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0227 }
        r12.<init>();	 Catch:{ all -> 0x0227 }
        r1 = themedWallpaperLink;	 Catch:{ all -> 0x0227 }
        r1 = org.telegram.messenger.Utilities.MD5(r1);	 Catch:{ all -> 0x0227 }
        r12.append(r1);	 Catch:{ all -> 0x0227 }
        r1 = ".wp";
        r12.append(r1);	 Catch:{ all -> 0x0227 }
        r12 = r12.toString();	 Catch:{ all -> 0x0227 }
        r10.<init>(r11, r12);	 Catch:{ all -> 0x0227 }
        r10 = r10.getAbsolutePath();	 Catch:{ all -> 0x0227 }
        r10 = android.graphics.BitmapFactory.decodeFile(r10);	 Catch:{ all -> 0x0227 }
        if (r10 == 0) goto L_0x017a;
    L_0x0101:
        r11 = new android.graphics.drawable.BitmapDrawable;	 Catch:{ all -> 0x0227 }
        r11.<init>(r10);	 Catch:{ all -> 0x0227 }
        wallpaper = r11;	 Catch:{ all -> 0x0227 }
        themedWallpaper = r11;	 Catch:{ all -> 0x0227 }
        isCustomTheme = r3;	 Catch:{ all -> 0x0227 }
        goto L_0x017a;
    L_0x010e:
        r10 = themedWallpaperFileOffset;	 Catch:{ all -> 0x0227 }
        if (r10 <= 0) goto L_0x017a;
    L_0x0112:
        r10 = currentTheme;	 Catch:{ all -> 0x0227 }
        r10 = r10.pathToFile;	 Catch:{ all -> 0x0227 }
        if (r10 != 0) goto L_0x011e;
    L_0x0118:
        r10 = currentTheme;	 Catch:{ all -> 0x0227 }
        r10 = r10.assetName;	 Catch:{ all -> 0x0227 }
        if (r10 == 0) goto L_0x017a;
    L_0x011e:
        r10 = currentTheme;	 Catch:{ all -> 0x0162 }
        r10 = r10.assetName;	 Catch:{ all -> 0x0162 }
        if (r10 == 0) goto L_0x012d;
    L_0x0124:
        r10 = currentTheme;	 Catch:{ all -> 0x0162 }
        r10 = r10.assetName;	 Catch:{ all -> 0x0162 }
        r10 = getAssetFile(r10);	 Catch:{ all -> 0x0162 }
        goto L_0x0136;
    L_0x012d:
        r10 = new java.io.File;	 Catch:{ all -> 0x0162 }
        r11 = currentTheme;	 Catch:{ all -> 0x0162 }
        r11 = r11.pathToFile;	 Catch:{ all -> 0x0162 }
        r10.<init>(r11);	 Catch:{ all -> 0x0162 }
    L_0x0136:
        r11 = new java.io.FileInputStream;	 Catch:{ all -> 0x0162 }
        r11.<init>(r10);	 Catch:{ all -> 0x0162 }
        r10 = r11.getChannel();	 Catch:{ all -> 0x015f }
        r12 = themedWallpaperFileOffset;	 Catch:{ all -> 0x015f }
        r7 = (long) r12;	 Catch:{ all -> 0x015f }
        r10.position(r7);	 Catch:{ all -> 0x015f }
        r10 = android.graphics.BitmapFactory.decodeStream(r11);	 Catch:{ all -> 0x015f }
        if (r10 == 0) goto L_0x0156;
    L_0x014b:
        r12 = new android.graphics.drawable.BitmapDrawable;	 Catch:{ all -> 0x015f }
        r12.<init>(r10);	 Catch:{ all -> 0x015f }
        wallpaper = r12;	 Catch:{ all -> 0x015f }
        themedWallpaper = r12;	 Catch:{ all -> 0x015f }
        isCustomTheme = r3;	 Catch:{ all -> 0x015f }
    L_0x0156:
        r11.close();	 Catch:{ Exception -> 0x015a }
        goto L_0x017a;
    L_0x015a:
        r10 = move-exception;
    L_0x015b:
        org.telegram.messenger.FileLog.e(r10);	 Catch:{ all -> 0x0227 }
        goto L_0x017a;
    L_0x015f:
        r10 = move-exception;
        r1 = r11;
        goto L_0x0163;
    L_0x0162:
        r10 = move-exception;
    L_0x0163:
        org.telegram.messenger.FileLog.e(r10);	 Catch:{ all -> 0x016e }
        if (r1 == 0) goto L_0x017a;
    L_0x0168:
        r1.close();	 Catch:{ Exception -> 0x016c }
        goto L_0x017a;
    L_0x016c:
        r10 = move-exception;
        goto L_0x015b;
    L_0x016e:
        r9 = move-exception;
        if (r1 == 0) goto L_0x0179;
    L_0x0171:
        r1.close();	 Catch:{ Exception -> 0x0175 }
        goto L_0x0179;
    L_0x0175:
        r10 = move-exception;
        org.telegram.messenger.FileLog.e(r10);	 Catch:{ all -> 0x0227 }
    L_0x0179:
        throw r9;	 Catch:{ all -> 0x0227 }
    L_0x017a:
        r10 = wallpaper;	 Catch:{ all -> 0x0227 }
        if (r10 != 0) goto L_0x021b;
    L_0x017e:
        if (r9 == 0) goto L_0x0183;
    L_0x0180:
        r10 = r9.color;	 Catch:{ all -> 0x0227 }
        goto L_0x0184;
    L_0x0183:
        r10 = 0;
    L_0x0184:
        r11 = NUM; // 0x7var_c float:1.7944765E38 double:1.0529355485E-314;
        if (r9 == 0) goto L_0x01fd;
    L_0x0189:
        r12 = r9.isDefault();	 Catch:{ all -> 0x020b }
        if (r12 == 0) goto L_0x0190;
    L_0x018f:
        goto L_0x01fd;
    L_0x0190:
        r12 = r9.isColor();	 Catch:{ all -> 0x020b }
        if (r12 != 0) goto L_0x020b;
    L_0x0196:
        if (r10 == 0) goto L_0x01cd;
    L_0x0198:
        r12 = isPatternWallpaper;	 Catch:{ all -> 0x020b }
        if (r12 != 0) goto L_0x01cd;
    L_0x019c:
        r11 = r9.gradientColor;	 Catch:{ all -> 0x020b }
        if (r11 == 0) goto L_0x01c5;
    L_0x01a0:
        r11 = new int[r6];	 Catch:{ all -> 0x020b }
        r11[r2] = r10;	 Catch:{ all -> 0x020b }
        r12 = r9.gradientColor;	 Catch:{ all -> 0x020b }
        r11[r3] = r12;	 Catch:{ all -> 0x020b }
        r9 = r9.rotation;	 Catch:{ all -> 0x020b }
        r9 = org.telegram.ui.Components.BackgroundGradientDrawable.getGradientOrientation(r9);	 Catch:{ all -> 0x020b }
        r12 = new org.telegram.ui.Components.BackgroundGradientDrawable;	 Catch:{ all -> 0x020b }
        r12.<init>(r9, r11);	 Catch:{ all -> 0x020b }
        r9 = new org.telegram.ui.ActionBar.Theme$10;	 Catch:{ all -> 0x020b }
        r9.<init>();	 Catch:{ all -> 0x020b }
        r11 = org.telegram.ui.Components.BackgroundGradientDrawable.Sizes.ofDeviceScreen();	 Catch:{ all -> 0x020b }
        r9 = r12.startDithering(r11, r9, r4);	 Catch:{ all -> 0x020b }
        backgroundGradientDisposable = r9;	 Catch:{ all -> 0x020b }
        wallpaper = r12;	 Catch:{ all -> 0x020b }
        goto L_0x020b;
    L_0x01c5:
        r9 = new android.graphics.drawable.ColorDrawable;	 Catch:{ all -> 0x020b }
        r9.<init>(r10);	 Catch:{ all -> 0x020b }
        wallpaper = r9;	 Catch:{ all -> 0x020b }
        goto L_0x020b;
    L_0x01cd:
        r12 = new java.io.File;	 Catch:{ all -> 0x020b }
        r1 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();	 Catch:{ all -> 0x020b }
        r9 = r9.fileName;	 Catch:{ all -> 0x020b }
        r12.<init>(r1, r9);	 Catch:{ all -> 0x020b }
        r12.length();	 Catch:{ all -> 0x020b }
        r9 = r12.exists();	 Catch:{ all -> 0x020b }
        if (r9 == 0) goto L_0x01ee;
    L_0x01e1:
        r9 = r12.getAbsolutePath();	 Catch:{ all -> 0x020b }
        r9 = android.graphics.drawable.Drawable.createFromPath(r9);	 Catch:{ all -> 0x020b }
        wallpaper = r9;	 Catch:{ all -> 0x020b }
        isCustomTheme = r3;	 Catch:{ all -> 0x020b }
        goto L_0x020b;
    L_0x01ee:
        r9 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x020b }
        r9 = r9.getResources();	 Catch:{ all -> 0x020b }
        r9 = r9.getDrawable(r11);	 Catch:{ all -> 0x020b }
        wallpaper = r9;	 Catch:{ all -> 0x020b }
        isCustomTheme = r2;	 Catch:{ all -> 0x020b }
        goto L_0x020b;
    L_0x01fd:
        r9 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x020b }
        r9 = r9.getResources();	 Catch:{ all -> 0x020b }
        r9 = r9.getDrawable(r11);	 Catch:{ all -> 0x020b }
        wallpaper = r9;	 Catch:{ all -> 0x020b }
        isCustomTheme = r2;	 Catch:{ all -> 0x020b }
    L_0x020b:
        r9 = wallpaper;	 Catch:{ all -> 0x0227 }
        if (r9 != 0) goto L_0x021b;
    L_0x020f:
        if (r10 != 0) goto L_0x0214;
    L_0x0211:
        r10 = -2693905; // 0xffffffffffd6e4ef float:NaN double:NaN;
    L_0x0214:
        r9 = new android.graphics.drawable.ColorDrawable;	 Catch:{ all -> 0x0227 }
        r9.<init>(r10);	 Catch:{ all -> 0x0227 }
        wallpaper = r9;	 Catch:{ all -> 0x0227 }
    L_0x021b:
        r9 = wallpaper;	 Catch:{ all -> 0x0227 }
        calcBackgroundColor(r9, r3);	 Catch:{ all -> 0x0227 }
        r9 = org.telegram.ui.ActionBar.-$$Lambda$Theme$rcJuxvar_iEk3SUZFDSBFoukj5k8.INSTANCE;	 Catch:{ all -> 0x0227 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r9);	 Catch:{ all -> 0x0227 }
        monitor-exit(r0);	 Catch:{ all -> 0x0227 }
        return;
    L_0x0227:
        r9 = move-exception;
        monitor-exit(r0);	 Catch:{ all -> 0x0227 }
        goto L_0x022b;
    L_0x022a:
        throw r9;
    L_0x022b:
        goto L_0x022a;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.lambda$loadWallpaper$8(org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo, boolean, java.io.File, boolean):void");
    }

    static /* synthetic */ void lambda$null$7() {
        applyChatServiceMessageColor();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewWallpapper, new Object[0]);
    }

    /* JADX WARNING: Removed duplicated region for block: B:43:0x00bb A:{SYNTHETIC, Splitter:B:43:0x00bb} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0112 A:{SYNTHETIC, Splitter:B:70:0x0112} */
    /* JADX WARNING: Missing block: B:17:0x0055, code skipped:
            if (r6.exists() != false) goto L_0x0059;
     */
    public static android.graphics.drawable.Drawable getThemedWallpaper(final boolean r8, final android.view.View r9) {
        /*
        r0 = currentColors;
        r1 = "chat_wallpaper";
        r0 = r0.get(r1);
        r0 = (java.lang.Integer) r0;
        r1 = 1;
        r2 = 0;
        r3 = 0;
        if (r0 == 0) goto L_0x0093;
    L_0x000f:
        r4 = currentColors;
        r5 = "chat_wallpaper_gradient_to";
        r4 = r4.get(r5);
        r4 = (java.lang.Integer) r4;
        r5 = currentColors;
        r6 = "chat_wallpaper_gradient_rotation";
        r5 = r5.get(r6);
        r5 = (java.lang.Integer) r5;
        if (r5 != 0) goto L_0x002b;
    L_0x0025:
        r5 = 45;
        r5 = java.lang.Integer.valueOf(r5);
    L_0x002b:
        if (r4 != 0) goto L_0x0037;
    L_0x002d:
        r8 = new android.graphics.drawable.ColorDrawable;
        r9 = r0.intValue();
        r8.<init>(r9);
        return r8;
    L_0x0037:
        r6 = currentTheme;
        r6 = r6.getAccent(r2);
        if (r6 == 0) goto L_0x0058;
    L_0x003f:
        r7 = r6.patternSlug;
        r7 = android.text.TextUtils.isEmpty(r7);
        if (r7 != 0) goto L_0x0058;
    L_0x0047:
        r7 = previousTheme;
        if (r7 != 0) goto L_0x0058;
    L_0x004b:
        r6 = r6.getPathToWallpaper();
        if (r6 == 0) goto L_0x0058;
    L_0x0051:
        r7 = r6.exists();
        if (r7 == 0) goto L_0x0058;
    L_0x0057:
        goto L_0x0059;
    L_0x0058:
        r6 = r3;
    L_0x0059:
        if (r6 != 0) goto L_0x0091;
    L_0x005b:
        r6 = 2;
        r6 = new int[r6];
        r0 = r0.intValue();
        r6[r2] = r0;
        r0 = r4.intValue();
        r6[r1] = r0;
        r0 = r5.intValue();
        r0 = org.telegram.ui.Components.BackgroundGradientDrawable.getGradientOrientation(r0);
        r1 = new org.telegram.ui.Components.BackgroundGradientDrawable;
        r1.<init>(r0, r6);
        if (r8 != 0) goto L_0x007e;
    L_0x0079:
        r0 = org.telegram.ui.Components.BackgroundGradientDrawable.Sizes.ofDeviceScreen();
        goto L_0x0086;
    L_0x007e:
        r0 = NUM; // 0x3e000000 float:0.125 double:5.139208556E-315;
        r2 = org.telegram.ui.Components.BackgroundGradientDrawable.Sizes.Orientation.PORTRAIT;
        r0 = org.telegram.ui.Components.BackgroundGradientDrawable.Sizes.ofDeviceScreen(r0, r2);
    L_0x0086:
        if (r9 == 0) goto L_0x008d;
    L_0x0088:
        r3 = new org.telegram.ui.ActionBar.Theme$11;
        r3.<init>(r8, r9);
    L_0x008d:
        r1.startDithering(r0, r3);
        return r1;
    L_0x0091:
        r9 = r6;
        goto L_0x00b8;
    L_0x0093:
        r9 = themedWallpaperFileOffset;
        if (r9 <= 0) goto L_0x00b7;
    L_0x0097:
        r9 = currentTheme;
        r0 = r9.pathToFile;
        if (r0 != 0) goto L_0x00a1;
    L_0x009d:
        r9 = r9.assetName;
        if (r9 == 0) goto L_0x00b7;
    L_0x00a1:
        r9 = currentTheme;
        r0 = r9.assetName;
        if (r0 == 0) goto L_0x00ac;
    L_0x00a7:
        r9 = getAssetFile(r0);
        goto L_0x00b4;
    L_0x00ac:
        r0 = new java.io.File;
        r9 = r9.pathToFile;
        r0.<init>(r9);
        r9 = r0;
    L_0x00b4:
        r0 = themedWallpaperFileOffset;
        goto L_0x00b9;
    L_0x00b7:
        r9 = r3;
    L_0x00b8:
        r0 = 0;
    L_0x00b9:
        if (r9 == 0) goto L_0x0127;
    L_0x00bb:
        r4 = new java.io.FileInputStream;	 Catch:{ all -> 0x010b }
        r4.<init>(r9);	 Catch:{ all -> 0x010b }
        r9 = r4.getChannel();	 Catch:{ all -> 0x0109 }
        r5 = (long) r0;	 Catch:{ all -> 0x0109 }
        r9.position(r5);	 Catch:{ all -> 0x0109 }
        r9 = new android.graphics.BitmapFactory$Options;	 Catch:{ all -> 0x0109 }
        r9.<init>();	 Catch:{ all -> 0x0109 }
        if (r8 == 0) goto L_0x00ed;
    L_0x00cf:
        r9.inJustDecodeBounds = r1;	 Catch:{ all -> 0x0109 }
        r8 = r9.outWidth;	 Catch:{ all -> 0x0109 }
        r8 = (float) r8;	 Catch:{ all -> 0x0109 }
        r0 = r9.outHeight;	 Catch:{ all -> 0x0109 }
        r0 = (float) r0;	 Catch:{ all -> 0x0109 }
        r5 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);	 Catch:{ all -> 0x0109 }
    L_0x00dd:
        r6 = (float) r5;	 Catch:{ all -> 0x0109 }
        r7 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1));
        if (r7 > 0) goto L_0x00e6;
    L_0x00e2:
        r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1));
        if (r6 <= 0) goto L_0x00ed;
    L_0x00e6:
        r1 = r1 * 2;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r8 = r8 / r6;
        r0 = r0 / r6;
        goto L_0x00dd;
    L_0x00ed:
        r9.inJustDecodeBounds = r2;	 Catch:{ all -> 0x0109 }
        r9.inSampleSize = r1;	 Catch:{ all -> 0x0109 }
        r8 = android.graphics.BitmapFactory.decodeStream(r4, r3, r9);	 Catch:{ all -> 0x0109 }
        if (r8 == 0) goto L_0x0105;
    L_0x00f7:
        r9 = new android.graphics.drawable.BitmapDrawable;	 Catch:{ all -> 0x0109 }
        r9.<init>(r8);	 Catch:{ all -> 0x0109 }
        r4.close();	 Catch:{ Exception -> 0x0100 }
        goto L_0x0104;
    L_0x0100:
        r8 = move-exception;
        org.telegram.messenger.FileLog.e(r8);
    L_0x0104:
        return r9;
    L_0x0105:
        r4.close();	 Catch:{ Exception -> 0x0116 }
        goto L_0x0127;
    L_0x0109:
        r8 = move-exception;
        goto L_0x010d;
    L_0x010b:
        r8 = move-exception;
        r4 = r3;
    L_0x010d:
        org.telegram.messenger.FileLog.e(r8);	 Catch:{ all -> 0x011b }
        if (r4 == 0) goto L_0x0127;
    L_0x0112:
        r4.close();	 Catch:{ Exception -> 0x0116 }
        goto L_0x0127;
    L_0x0116:
        r8 = move-exception;
        org.telegram.messenger.FileLog.e(r8);
        goto L_0x0127;
    L_0x011b:
        r8 = move-exception;
        if (r4 == 0) goto L_0x0126;
    L_0x011e:
        r4.close();	 Catch:{ Exception -> 0x0122 }
        goto L_0x0126;
    L_0x0122:
        r9 = move-exception;
        org.telegram.messenger.FileLog.e(r9);
    L_0x0126:
        throw r8;
    L_0x0127:
        return r3;
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
            Drawable drawable;
            if (themedWallpaper != null) {
                drawable = themedWallpaper;
                return drawable;
            }
            drawable = wallpaper;
            return drawable;
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
