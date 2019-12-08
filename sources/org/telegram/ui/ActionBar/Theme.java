package org.telegram.ui.ActionBar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.StateSet;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.TL_account_getTheme;
import org.telegram.tgnet.TLRPC.TL_account_getThemes;
import org.telegram.tgnet.TLRPC.TL_inputTheme;
import org.telegram.tgnet.TLRPC.TL_theme;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.ScamDrawable;

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
    private static Field BitmapDrawable_mColorFilter = null;
    public static final long DEFAULT_BACKGROUND_ID = 1000001;
    private static final int LIGHT_SENSOR_THEME_SWITCH_DELAY = 1800;
    private static final int LIGHT_SENSOR_THEME_SWITCH_NEAR_DELAY = 12000;
    private static final int LIGHT_SENSOR_THEME_SWITCH_NEAR_THRESHOLD = 12000;
    private static final float MAXIMUM_LUX_BREAKPOINT = 500.0f;
    private static Method StateListDrawable_getStateDrawableMethod = null;
    public static final long THEME_BACKGROUND_ID = -2;
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
    public static Drawable chat_fileIcon = null;
    public static CombinedDrawable[][] chat_fileMiniStatesDrawable = ((CombinedDrawable[][]) Array.newInstance(CombinedDrawable.class, new int[]{6, 2}));
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
    public static Drawable chat_msgOutDrawable = null;
    public static Drawable chat_msgOutHalfCheckDrawable = null;
    public static Drawable chat_msgOutHalfCheckSelectedDrawable = null;
    public static Drawable chat_msgOutInstantDrawable = null;
    public static Drawable chat_msgOutLocationDrawable = null;
    public static Drawable chat_msgOutMediaDrawable = null;
    public static Drawable chat_msgOutMediaSelectedDrawable = null;
    public static Drawable chat_msgOutMediaShadowDrawable = null;
    public static Drawable chat_msgOutMenuDrawable = null;
    public static Drawable chat_msgOutMenuSelectedDrawable = null;
    public static Drawable chat_msgOutSelectedClockDrawable = null;
    public static Drawable chat_msgOutSelectedDrawable = null;
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
    public static TextPaint dialogs_messagePaint = null;
    public static TextPaint dialogs_messagePrintingPaint = null;
    public static Drawable dialogs_muteDrawable = null;
    public static TextPaint dialogs_nameEncryptedPaint = null;
    public static TextPaint dialogs_namePaint = null;
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
    private static float[] hsv = new float[3];
    private static boolean isCustomTheme = false;
    private static boolean isPatternWallpaper = false;
    private static boolean isWallpaperMotion = false;
    private static Boolean isWallpaperMotionPrev = null;
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
    public static final String key_chat_messagePanelSendPressed = "chat_messagePanelPressedSend";
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
    public static final String key_featuredStickers_delButton = "featuredStickers_delButton";
    public static final String key_featuredStickers_delButtonPressed = "featuredStickers_delButtonPressed";
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
    public static final String key_location_liveLocationProgress = "location_liveLocationProgress";
    public static final String key_location_placeLocationBackground = "location_placeLocationBackground";
    public static final String key_location_sendLiveLocationBackground = "location_sendLiveLocationBackground";
    public static final String key_location_sendLiveLocationIcon = "location_sendLiveLocationIcon";
    public static final String key_location_sendLocationBackground = "location_sendLocationBackground";
    public static final String key_location_sendLocationIcon = "location_sendLocationIcon";
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
    private static long lastHolidayCheckTime;
    private static int lastLoadingCurrentThemeTime;
    private static int lastLoadingThemesTime;
    private static long lastThemeSwitchTime;
    private static Sensor lightSensor;
    private static boolean lightSensorRegistered;
    public static Paint linkSelectionPaint;
    public static Drawable listSelector;
    private static int loadingCurrentTheme;
    private static boolean loadingRemoteThemes;
    private static Paint maskPaint = new Paint(1);
    public static Drawable moveUpDrawable;
    private static ArrayList<ThemeInfo> otherThemes = new ArrayList();
    private static ThemeInfo previousTheme;
    public static TextPaint profile_aboutTextPaint;
    public static Drawable profile_verifiedCheckDrawable;
    public static Drawable profile_verifiedDrawable;
    private static int remoteThemesHash;
    public static int selectedAutoNightType;
    private static int selectedColor;
    private static SensorManager sensorManager;
    private static int serviceMessage2Color;
    private static int serviceMessageColor;
    public static int serviceMessageColorBackup;
    private static int serviceSelectedMessage2Color;
    private static int serviceSelectedMessageColor;
    public static int serviceSelectedMessageColorBackup;
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
    private static final Object sync = new Object();
    private static HashSet<String> themeAccentExclusionKeys = new HashSet();
    private static Drawable themedWallpaper;
    private static int themedWallpaperFileOffset;
    private static String themedWallpaperLink;
    public static ArrayList<ThemeInfo> themes = new ArrayList();
    private static HashMap<String, ThemeInfo> themesDict = new HashMap();
    private static Drawable wallpaper;
    private static final Object wallpaperSync = new Object();

    public static class ThemeInfo implements NotificationCenterDelegate {
        public int accentBaseColor;
        final float[] accentBaseColorHsv;
        public int accentColor;
        final float[] accentColorHsv;
        public int[] accentColorOptions;
        public int account;
        public String assetName;
        public boolean badWallpaper;
        public TL_theme info;
        public boolean isBlured;
        public boolean isMotion;
        public boolean loaded;
        public String name;
        public String pathToFile;
        public String pathToWallpaper;
        public int previewBackgroundColor;
        public int previewBackgroundGradientColor;
        public int previewInColor;
        public int previewOutColor;
        public boolean previewParsed;
        public int previewWallpaperOffset;
        public String slug;
        public int sortIndex;
        public boolean themeLoaded;
        public InputFile uploadedFile;
        public InputFile uploadedThumb;
        public String uploadingFile;
        public String uploadingThumb;

        ThemeInfo() {
            this.loaded = true;
            this.themeLoaded = true;
            this.accentBaseColorHsv = new float[3];
            this.accentColorHsv = new float[3];
        }

        public ThemeInfo(ThemeInfo themeInfo) {
            this.loaded = true;
            this.themeLoaded = true;
            this.accentBaseColorHsv = new float[3];
            this.accentColorHsv = new float[3];
            this.name = themeInfo.name;
            this.pathToFile = themeInfo.pathToFile;
            this.assetName = themeInfo.assetName;
            this.sortIndex = themeInfo.sortIndex;
            this.accentColorOptions = themeInfo.accentColorOptions;
            this.accentBaseColor = themeInfo.accentBaseColor;
            this.accentColor = themeInfo.accentColor;
            this.info = themeInfo.info;
            this.loaded = themeInfo.loaded;
            this.uploadingThumb = themeInfo.uploadingThumb;
            this.uploadingFile = themeInfo.uploadingFile;
            this.uploadedThumb = themeInfo.uploadedThumb;
            this.uploadedFile = themeInfo.uploadedFile;
            this.account = themeInfo.account;
            this.pathToWallpaper = themeInfo.pathToWallpaper;
            this.slug = themeInfo.slug;
            this.badWallpaper = themeInfo.badWallpaper;
            this.isBlured = themeInfo.isBlured;
            this.isMotion = themeInfo.isMotion;
            this.previewBackgroundColor = themeInfo.previewBackgroundColor;
            this.previewBackgroundGradientColor = themeInfo.previewBackgroundGradientColor;
            this.previewWallpaperOffset = themeInfo.previewWallpaperOffset;
            this.previewInColor = themeInfo.previewInColor;
            this.previewOutColor = themeInfo.previewOutColor;
            this.previewParsed = themeInfo.previewParsed;
            this.themeLoaded = themeInfo.themeLoaded;
            Color.colorToHSV(this.accentBaseColor, this.accentBaseColorHsv);
            Color.colorToHSV(this.accentColor, this.accentColorHsv);
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

        public String getName() {
            String str = "Default";
            if (str.equals(this.name)) {
                return LocaleController.getString(str, NUM);
            }
            if ("Blue".equals(this.name)) {
                return LocaleController.getString("ThemeBlue", NUM);
            }
            if ("Dark Blue".equals(this.name)) {
                return LocaleController.getString("ThemeDark", NUM);
            }
            if ("Graphite".equals(this.name)) {
                return LocaleController.getString("ThemeGraphite", NUM);
            }
            if ("Arctic Blue".equals(this.name)) {
                return LocaleController.getString("ThemeArcticBlue", NUM);
            }
            TL_theme tL_theme = this.info;
            return tL_theme != null ? tL_theme.title : this.name;
        }

        public boolean isDark() {
            if (!"Dark Blue".equals(this.name)) {
                if (!"Graphite".equals(this.name)) {
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

        /* Access modifiers changed, original: 0000 */
        public void setAccentColorOptions(int[] iArr) {
            this.accentColorOptions = iArr;
            this.accentBaseColor = iArr[0];
            Color.colorToHSV(this.accentBaseColor, this.accentBaseColorHsv);
            setAccentColor(this.accentBaseColor);
        }

        /* Access modifiers changed, original: 0000 */
        public void setAccentColor(int i) {
            this.accentColor = i;
            Color.colorToHSV(this.accentColor, this.accentColorHsv);
        }

        private void loadThemeDocument() {
            this.loaded = false;
            NotificationCenter.getInstance(this.account).addObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(this.account).addObserver(this, NotificationCenter.fileDidFailToLoad);
            FileLoader instance = FileLoader.getInstance(this.account);
            TL_theme tL_theme = this.info;
            instance.loadFile(tL_theme.document, tL_theme, 1, 1);
        }

        private void removeObservers() {
            NotificationCenter.getInstance(this.account).removeObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(this.account).removeObserver(this, NotificationCenter.fileDidFailToLoad);
        }

        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (i == NotificationCenter.fileDidLoad || i == NotificationCenter.fileDidFailToLoad) {
                String str = (String) objArr[0];
                TL_theme tL_theme = this.info;
                if (tL_theme != null) {
                    Document document = tL_theme.document;
                    if (document != null && str.equals(FileLoader.getAttachFileName(document))) {
                        removeObservers();
                        if (i == NotificationCenter.fileDidLoad) {
                            boolean z = true;
                            this.loaded = true;
                            this.previewParsed = false;
                            Theme.saveOtherThemes(true);
                            if (this == Theme.currentTheme && Theme.previousTheme == null) {
                                if (this != Theme.currentNightTheme) {
                                    z = false;
                                }
                                Theme.applyTheme(this, z);
                            }
                        }
                    }
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:66:0x235d  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x235a  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x235a  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x235d  */
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
        r3 = NUM; // 0x40cNUM float:0.0 double:10000.0;
        autoNightLocationLatitude = r3;
        autoNightLocationLongitude = r3;
        r3 = new android.graphics.Paint;
        r4 = 1;
        r3.<init>(r4);
        maskPaint = r3;
        r3 = 6;
        r3 = new android.graphics.drawable.Drawable[r3];
        chat_attachButtonDrawables = r3;
        r3 = 2;
        r5 = new android.graphics.drawable.Drawable[r3];
        chat_locationDrawable = r5;
        r5 = new android.graphics.drawable.Drawable[r3];
        chat_contactDrawable = r5;
        r5 = 4;
        r6 = new android.graphics.drawable.Drawable[r5];
        chat_cornerOuter = r6;
        r6 = new android.graphics.drawable.Drawable[r5];
        chat_cornerInner = r6;
        r6 = 10;
        r6 = new int[]{r6, r3};
        r7 = android.graphics.drawable.Drawable.class;
        r6 = java.lang.reflect.Array.newInstance(r7, r6);
        r6 = (android.graphics.drawable.Drawable[][]) r6;
        chat_fileStatesDrawable = r6;
        r6 = 6;
        r6 = new int[]{r6, r3};
        r7 = org.telegram.ui.Components.CombinedDrawable.class;
        r6 = java.lang.reflect.Array.newInstance(r7, r6);
        r6 = (org.telegram.ui.Components.CombinedDrawable[][]) r6;
        chat_fileMiniStatesDrawable = r6;
        r6 = 13;
        r6 = new int[]{r6, r3};
        r7 = android.graphics.drawable.Drawable.class;
        r6 = java.lang.reflect.Array.newInstance(r7, r6);
        r6 = (android.graphics.drawable.Drawable[][]) r6;
        chat_photoStatesDrawables = r6;
        r6 = 7;
        r6 = new java.lang.String[r6];
        r7 = "avatar_backgroundRed";
        r6[r1] = r7;
        r7 = "avatar_backgroundOrange";
        r6[r4] = r7;
        r7 = "avatar_backgroundViolet";
        r6[r3] = r7;
        r7 = 3;
        r8 = "avatar_backgroundGreen";
        r6[r7] = r8;
        r7 = "avatar_backgroundCyan";
        r6[r5] = r7;
        r7 = 5;
        r8 = "avatar_backgroundBlue";
        r6[r7] = r8;
        r7 = 6;
        r8 = "avatar_backgroundPink";
        r6[r7] = r8;
        keys_avatar_background = r6;
        r6 = 7;
        r6 = new java.lang.String[r6];
        r7 = "avatar_nameInMessageRed";
        r6[r1] = r7;
        r7 = "avatar_nameInMessageOrange";
        r6[r4] = r7;
        r7 = "avatar_nameInMessageViolet";
        r6[r3] = r7;
        r7 = 3;
        r8 = "avatar_nameInMessageGreen";
        r6[r7] = r8;
        r7 = "avatar_nameInMessageCyan";
        r6[r5] = r7;
        r7 = 5;
        r8 = "avatar_nameInMessageBlue";
        r6[r7] = r8;
        r7 = 6;
        r8 = "avatar_nameInMessagePink";
        r6[r7] = r8;
        keys_avatar_nameInMessage = r6;
        r6 = new java.util.HashMap;
        r6.<init>();
        defaultColors = r6;
        r6 = new java.util.HashMap;
        r6.<init>();
        fallbackKeys = r6;
        r6 = new java.util.HashSet;
        r6.<init>();
        themeAccentExclusionKeys = r6;
        r6 = 3;
        r6 = new float[r6];
        hsv = r6;
        r6 = defaultColors;
        r7 = "dialogBackground";
        r6.put(r7, r0);
        r6 = defaultColors;
        r7 = -986896; // 0xfffffffffff0f0f0 float:NaN double:NaN;
        r8 = java.lang.Integer.valueOf(r7);
        r9 = "dialogBackgroundGray";
        r6.put(r9, r8);
        r6 = defaultColors;
        r8 = -14540254; // 0xfffffffffvar_ float:-2.1551216E38 double:NaN;
        r8 = java.lang.Integer.valueOf(r8);
        r9 = "dialogTextBlack";
        r6.put(r9, r8);
        r6 = defaultColors;
        r9 = -14255946; // 0xfffffffffvar_b6 float:-2.2127861E38 double:NaN;
        r9 = java.lang.Integer.valueOf(r9);
        r10 = "dialogTextLink";
        r6.put(r10, r9);
        r6 = defaultColors;
        r9 = NUM; // 0x3362a9e3 float:5.2774237E-8 double:4.25935987E-315;
        r9 = java.lang.Integer.valueOf(r9);
        r10 = "dialogLinkSelection";
        r6.put(r10, r9);
        r6 = defaultColors;
        r9 = -3319206; // 0xffffffffffcd5a5a float:NaN double:NaN;
        r9 = java.lang.Integer.valueOf(r9);
        r10 = "dialogTextRed";
        r6.put(r10, r9);
        r6 = defaultColors;
        r9 = -2213318; // 0xffffffffffde3a3a float:NaN double:NaN;
        r9 = java.lang.Integer.valueOf(r9);
        r10 = "dialogTextRed2";
        r6.put(r10, r9);
        r6 = defaultColors;
        r9 = -13660983; // 0xffffffffff2f8cc9 float:-2.333459E38 double:NaN;
        r9 = java.lang.Integer.valueOf(r9);
        r10 = "dialogTextBlue";
        r6.put(r10, r9);
        r6 = defaultColors;
        r9 = -12937771; // 0xffffffffff3a95d5 float:-2.4801438E38 double:NaN;
        r9 = java.lang.Integer.valueOf(r9);
        r10 = "dialogTextBlue2";
        r6.put(r10, r9);
        r6 = defaultColors;
        r9 = -12664327; // 0xffffffffff3ec1f9 float:-2.5356048E38 double:NaN;
        r9 = java.lang.Integer.valueOf(r9);
        r10 = "dialogTextBlue3";
        r6.put(r10, r9);
        r6 = defaultColors;
        r9 = -15095832; // 0xfffffffffvar_a7e8 float:-2.042437E38 double:NaN;
        r9 = java.lang.Integer.valueOf(r9);
        r10 = "dialogTextBlue4";
        r6.put(r10, r9);
        r6 = defaultColors;
        r9 = -13333567; // 0xfffffffffvar_bc1 float:-2.3998668E38 double:NaN;
        r9 = java.lang.Integer.valueOf(r9);
        r10 = "dialogTextGray";
        r6.put(r10, r9);
        r6 = defaultColors;
        r9 = -9079435; // 0xfffffffffvar_ float:-3.2627073E38 double:NaN;
        r9 = java.lang.Integer.valueOf(r9);
        r10 = "dialogTextGray2";
        r6.put(r10, r9);
        r6 = defaultColors;
        r9 = -6710887; // 0xfffffffffvar_ float:NaN double:NaN;
        r10 = java.lang.Integer.valueOf(r9);
        r11 = "dialogTextGray3";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -5000269; // 0xffffffffffb3b3b3 float:NaN double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogTextGray4";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -6842473; // 0xfffffffffvar_ float:NaN double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogTextHint";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -9999504; // 0xfffffffffvar_b70 float:-3.0760951E38 double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogIcon";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -2011827; // 0xffffffffffe14d4d float:NaN double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogRedIcon";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -2960686; // 0xffffffffffd2d2d2 float:NaN double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogGrayLine";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -9456923; // 0xffffffffff6fb2e5 float:-3.1861436E38 double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogTopBackground";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -2368549; // 0xffffffffffdbdbdb float:NaN double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogInputField";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -13129232; // 0xfffffffffvar_a9f0 float:-2.4413109E38 double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogInputFieldActivated";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -12345121; // 0xfffffffffvar_a0df float:-2.6003475E38 double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogCheckboxSquareBackground";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = "dialogCheckboxSquareCheck";
        r6.put(r10, r0);
        r6 = defaultColors;
        r10 = -9211021; // 0xfffffffffvar_ float:-3.2360185E38 double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogCheckboxSquareUnchecked";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -5197648; // 0xffffffffffb0b0b0 float:NaN double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogCheckboxSquareDisabled";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -5000269; // 0xffffffffffb3b3b3 float:NaN double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogRadioBackground";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -13129232; // 0xfffffffffvar_a9f0 float:-2.4413109E38 double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogRadioBackgroundChecked";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -11371101; // 0xfffffffffvar_da3 float:-2.7979022E38 double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogProgressCircle";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -11371101; // 0xfffffffffvar_da3 float:-2.7979022E38 double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogLineProgress";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -2368549; // 0xffffffffffdbdbdb float:NaN double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogLineProgressBackground";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -11955764; // 0xfffffffffvar_cc float:-2.6793185E38 double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogButton";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = NUM; // 0xvar_ float:6.3108872E-30 double:1.24335691E-315;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogButtonSelector";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -657673; // 0xfffffffffff5f6f7 float:NaN double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogScrollGlow";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -11750155; // 0xffffffffff4cb4f5 float:-2.721021E38 double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogRoundCheckBox";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = "dialogRoundCheckBoxCheck";
        r6.put(r10, r0);
        r6 = defaultColors;
        r10 = -12664327; // 0xffffffffff3ec1f9 float:-2.5356048E38 double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogBadgeBackground";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = "dialogBadgeText";
        r6.put(r10, r0);
        r6 = defaultColors;
        r10 = "dialogCameraIcon";
        r6.put(r10, r0);
        r6 = defaultColors;
        r10 = -NUM; // 0xfffffffff6f0f2f5 float:-2.4435137E33 double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialog_inlineProgressBackground";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -9735304; // 0xffffffffff6b7378 float:-3.1296813E38 double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialog_inlineProgress";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -854795; // 0xfffffffffff2f4f5 float:NaN double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogSearchBackground";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -6774617; // 0xfffffffffvar_a0a7 float:NaN double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogSearchHint";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -6182737; // 0xffffffffffa1a8af float:NaN double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogSearchIcon";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = "dialogSearchText";
        r6.put(r10, r8);
        r6 = defaultColors;
        r10 = -11750155; // 0xffffffffff4cb4f5 float:-2.721021E38 double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogFloatingButton";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -11750155; // 0xffffffffff4cb4f5 float:-2.721021E38 double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogFloatingButtonPressed";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = "dialogFloatingIcon";
        r6.put(r10, r0);
        r6 = defaultColors;
        r10 = NUM; // 0x12000000 float:4.0389678E-28 double:1.49202829E-315;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "dialogShadowLine";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = "windowBackgroundWhite";
        r6.put(r10, r0);
        r6 = defaultColors;
        r10 = -6445135; // 0xffffffffff9da7b1 float:NaN double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "windowBackgroundUnchecked";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -11034919; // 0xfffffffffvar_ed9 float:-2.866088E38 double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "windowBackgroundChecked";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = "windowBackgroundCheckText";
        r6.put(r10, r0);
        r6 = defaultColors;
        r10 = -11371101; // 0xfffffffffvar_da3 float:-2.7979022E38 double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "progressCircle";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -8288629; // 0xfffffffffvar_b float:NaN double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "windowBackgroundWhiteGrayIcon";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -12545331; // 0xfffffffffvar_cd float:-2.55974E38 double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "windowBackgroundWhiteBlueText";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -12937771; // 0xffffffffff3a95d5 float:-2.4801438E38 double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "windowBackgroundWhiteBlueText2";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -14255946; // 0xfffffffffvar_b6 float:-2.2127861E38 double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "windowBackgroundWhiteBlueText3";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -11697229; // 0xffffffffff4d83b3 float:-2.7317556E38 double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "windowBackgroundWhiteBlueText4";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -11759926; // 0xffffffffff4c8eca float:-2.7190391E38 double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "windowBackgroundWhiteBlueText5";
        r6.put(r11, r10);
        r6 = defaultColors;
        r10 = -12940081; // 0xffffffffff3a8ccf float:-2.4796753E38 double:NaN;
        r10 = java.lang.Integer.valueOf(r10);
        r11 = "windowBackgroundWhiteBlueText6";
        r6.put(r11, r10);
        r6 = defaultColors;
        r11 = -13141330; // 0xfffffffffvar_aae float:-2.4388571E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteBlueText7";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -14776109; // 0xffffffffff1e88d3 float:-2.1072846E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteBlueButton";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -13132315; // 0xfffffffffvar_de5 float:-2.4406856E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteBlueIcon";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -14248148; // 0xfffffffffvar_c float:-2.2143678E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteGreenText";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -13129704; // 0xfffffffffvar_a818 float:-2.4412152E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteGreenText2";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -3319206; // 0xffffffffffcd5a5a float:NaN double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteRedText";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -2404015; // 0xffffffffffdb5151 float:NaN double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteRedText2";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -2995895; // 0xffffffffffd24949 float:NaN double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteRedText3";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -3198928; // 0xffffffffffcvar_ float:NaN double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteRedText4";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -1230535; // 0xffffffffffed3939 float:NaN double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteRedText5";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -39322; // 0xfffffffffffvar_ float:NaN double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteRedText6";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -8156010; // 0xfffffffffvar_CLASSNAME float:NaN double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteGrayText";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -7697782; // 0xffffffffff8a8a8a float:NaN double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteGrayText2";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = java.lang.Integer.valueOf(r9);
        r12 = "windowBackgroundWhiteGrayText3";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -8355712; // 0xfffffffffvar_ float:NaN double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteGrayText4";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -6052957; // 0xffffffffffa3a3a3 float:NaN double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteGrayText5";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -9079435; // 0xfffffffffvar_ float:-3.2627073E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteGrayText6";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -3750202; // 0xffffffffffc6c6c6 float:NaN double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteGrayText7";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -9605774; // 0xffffffffff6d6d72 float:-3.155953E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteGrayText8";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -2368549; // 0xffffffffffdbdbdb float:NaN double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteGrayLine";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = "windowBackgroundWhiteBlackText";
        r6.put(r11, r8);
        r6 = defaultColors;
        r11 = -5723992; // 0xffffffffffa8a8a8 float:NaN double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteHintText";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -12937771; // 0xffffffffff3a95d5 float:-2.4801438E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteValueText";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -14255946; // 0xfffffffffvar_b6 float:-2.2127861E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteLinkText";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = NUM; // 0x3362a9e3 float:5.2774237E-8 double:4.25935987E-315;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteLinkSelection";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -12937771; // 0xffffffffff3a95d5 float:-2.4801438E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteBlueHeader";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -2368549; // 0xffffffffffdbdbdb float:NaN double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteInputField";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -13129232; // 0xfffffffffvar_a9f0 float:-2.4413109E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundWhiteInputFieldActivated";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -5196358; // 0xffffffffffb0b5ba float:NaN double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "switchTrack";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -11358743; // 0xfffffffffvar_ade9 float:-2.8004087E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "switchTrackChecked";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -8221031; // 0xfffffffffvar_e99 float:NaN double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "switchTrackBlue";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -12810041; // 0xffffffffff3CLASSNAMEc7 float:-2.5060505E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "switchTrackBlueChecked";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = "switchTrackBlueThumb";
        r6.put(r11, r0);
        r6 = defaultColors;
        r11 = "switchTrackBlueThumbChecked";
        r6.put(r11, r0);
        r6 = defaultColors;
        r11 = NUM; // 0x17404a53 float:6.2132356E-25 double:1.927297214E-315;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "switchTrackBlueSelector";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = NUM; // 0x21024781 float:4.414035E-19 double:2.73612322E-315;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "switchTrackBlueSelectorChecked";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -688514; // 0xffffffffffvar_e7e float:NaN double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "switch2Track";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -11358743; // 0xfffffffffvar_ade9 float:-2.8004087E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "switch2TrackChecked";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -12345121; // 0xfffffffffvar_a0df float:-2.6003475E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "checkboxSquareBackground";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = "checkboxSquareCheck";
        r6.put(r11, r0);
        r6 = defaultColors;
        r11 = -9211021; // 0xfffffffffvar_ float:-3.2360185E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "checkboxSquareUnchecked";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -5197648; // 0xffffffffffb0b0b0 float:NaN double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "checkboxSquareDisabled";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = NUM; // 0xvar_ float:6.3108872E-30 double:1.24335691E-315;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "listSelectorSDK21";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -5000269; // 0xffffffffffb3b3b3 float:NaN double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "radioBackground";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -13129232; // 0xfffffffffvar_a9f0 float:-2.4413109E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "radioBackgroundChecked";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = java.lang.Integer.valueOf(r7);
        r12 = "windowBackgroundGray";
        r6.put(r12, r11);
        r6 = defaultColors;
        r11 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r11 = java.lang.Integer.valueOf(r11);
        r12 = "windowBackgroundGrayShadow";
        r6.put(r12, r11);
        r6 = defaultColors;
        r12 = -6974059; // 0xfffffffffvar_ float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "emptyListPlaceholder";
        r6.put(r13, r12);
        r6 = defaultColors;
        r12 = -2500135; // 0xffffffffffd9d9d9 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "divider";
        r6.put(r13, r12);
        r6 = defaultColors;
        r12 = -1117195; // 0xffffffffffeef3f5 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "graySection";
        r6.put(r13, r12);
        r6 = defaultColors;
        r12 = -8418927; // 0xffffffffff7var_ float:-3.3966742E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "key_graySectionText";
        r6.put(r13, r12);
        r6 = defaultColors;
        r12 = -4202506; // 0xffffffffffbfdff6 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "contextProgressInner1";
        r6.put(r13, r12);
        r6 = defaultColors;
        r12 = -13920542; // 0xffffffffff2b96e2 float:-2.2808142E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "contextProgressOuter1";
        r6.put(r13, r12);
        r6 = defaultColors;
        r12 = -4202506; // 0xffffffffffbfdff6 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "contextProgressInner2";
        r6.put(r13, r12);
        r6 = defaultColors;
        r12 = "contextProgressOuter2";
        r6.put(r12, r0);
        r6 = defaultColors;
        r12 = -5000269; // 0xffffffffffb3b3b3 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "contextProgressInner3";
        r6.put(r13, r12);
        r6 = defaultColors;
        r12 = "contextProgressOuter3";
        r6.put(r12, r0);
        r6 = defaultColors;
        r12 = -3486256; // 0xffffffffffcacdd0 float:NaN double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "contextProgressInner4";
        r6.put(r13, r12);
        r6 = defaultColors;
        r12 = -13683656; // 0xffffffffff2var_ float:-2.3288603E38 double:NaN;
        r12 = java.lang.Integer.valueOf(r12);
        r13 = "contextProgressOuter4";
        r6.put(r13, r12);
        r6 = defaultColors;
        r13 = -11361317; // 0xfffffffffvar_a3db float:-2.7998867E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "fastScrollActive";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -3551791; // 0xffffffffffc9cdd1 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "fastScrollInactive";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "fastScrollText";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = "avatar_text";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -10043398; // 0xfffffffffvar_bffa float:-3.0671924E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "avatar_backgroundSaved";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -5654847; // 0xffffffffffa9b6c1 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "avatar_backgroundArchived";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -3749428; // 0xffffffffffc6c9cc float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "avatar_backgroundArchivedHidden";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -1743531; // 0xffffffffffe56555 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "avatar_backgroundRed";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -881592; // 0xffffffffffvar_CLASSNAME float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "avatar_backgroundOrange";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -7436818; // 0xffffffffff8e85ee float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "avatar_backgroundViolet";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -8992691; // 0xfffffffffvar_CLASSNAMEd float:-3.280301E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "avatar_backgroundGreen";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -10502443; // 0xffffffffff5fbed5 float:-2.974087E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "avatar_backgroundCyan";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -11232035; // 0xfffffffffvar_cdd float:-2.8261082E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "avatar_backgroundBlue";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -887654; // 0xffffffffffvar_a float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "avatar_backgroundPink";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -1642505; // 0xffffffffffe6eff7 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "avatar_backgroundGroupCreateSpanBlue";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -11500111; // 0xfffffffffvar_b1 float:-2.7717359E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "avatar_backgroundInProfileBlue";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -10907718; // 0xfffffffffvar_fba float:-2.8918875E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "avatar_backgroundActionBarBlue";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -2626822; // 0xffffffffffd7eafa float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "avatar_subtitleInProfileBlue";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -11959891; // 0xfffffffffvar_ad float:-2.6784814E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "avatar_actionBarSelectorBlue";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "avatar_actionBarIconBlue";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -3516848; // 0xffffffffffca5650 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "avatar_nameInMessageRed";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -2589911; // 0xffffffffffd87b29 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "avatar_nameInMessageOrange";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -11627828; // 0xffffffffff4e92cc float:-2.7458318E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "avatar_nameInMessageViolet";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -11488718; // 0xfffffffffvar_b232 float:-2.7740467E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "avatar_nameInMessageGreen";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -13132104; // 0xfffffffffvar_eb8 float:-2.4407284E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "avatar_nameInMessageCyan";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -11627828; // 0xffffffffff4e92cc float:-2.7458318E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "avatar_nameInMessageBlue";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -11627828; // 0xffffffffff4e92cc float:-2.7458318E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "avatar_nameInMessagePink";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -11371101; // 0xfffffffffvar_da3 float:-2.7979022E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "actionBarDefault";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "actionBarDefaultIcon";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = "actionBarActionModeDefault";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = NUM; // 0x10000000 float:2.5243549E-29 double:1.32624737E-315;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "actionBarActionModeDefaultTop";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -9999761; // 0xfffffffffvar_a6f float:-3.076043E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "actionBarActionModeDefaultIcon";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "actionBarDefaultTitle";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -2758409; // 0xffffffffffd5e8f7 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "actionBarDefaultSubtitle";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -12554860; // 0xfffffffffvar_d94 float:-2.5578074E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "actionBarDefaultSelector";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = NUM; // 0x2var_ float:1.1641532E-10 double:3.895851647E-315;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "actionBarWhiteSelector";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "actionBarDefaultSearch";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -NUM; // 0xfffffffvar_ffffff float:-1.5407439E-33 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "actionBarDefaultSearchPlaceholder";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "actionBarDefaultSubmenuItem";
        r6.put(r13, r8);
        r6 = defaultColors;
        r13 = -9999504; // 0xfffffffffvar_b70 float:-3.0760951E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "actionBarDefaultSubmenuItemIcon";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "actionBarDefaultSubmenuBackground";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = java.lang.Integer.valueOf(r7);
        r14 = "actionBarActionModeDefaultSelector";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "actionBarTabActiveText";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -2758409; // 0xffffffffffd5e8f7 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "actionBarTabUnactiveText";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "actionBarTabLine";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -12554860; // 0xfffffffffvar_d94 float:-2.5578074E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "actionBarTabSelector";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "actionBarBrowser";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -9471353; // 0xffffffffff6f7a87 float:-3.1832169E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "actionBarDefaultArchived";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -10590350; // 0xffffffffff5e6772 float:-2.9562573E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "actionBarDefaultArchivedSelector";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "actionBarDefaultArchivedIcon";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = "actionBarDefaultArchivedTitle";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = "actionBarDefaultArchivedSearch";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -NUM; // 0xfffffffvar_ffffff float:-1.5407439E-33 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "actionBarDefaultSearchArchivedPlaceholder";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -11810020; // 0xffffffffff4bcb1c float:-2.7088789E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_onlineCircle";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -11613090; // 0xffffffffff4ecc5e float:-2.748821E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_unreadCounter";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -3749428; // 0xffffffffffc6c9cc float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_unreadCounterMuted";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "chats_unreadCounterText";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -10049056; // 0xfffffffffvar_a9e0 float:-3.0660448E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_archiveBackground";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -6313293; // 0xffffffffff9faab3 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_archivePinBackground";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "chats_archiveIcon";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = "chats_archiveText";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = "chats_name";
        r6.put(r13, r8);
        r6 = defaultColors;
        r13 = -11382190; // 0xfffffffffvar_ float:-2.7956531E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_nameArchived";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -16734706; // 0xfffffffffvar_a60e float:-1.7100339E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_secretName";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -15093466; // 0xfffffffffvar_b126 float:-2.042917E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_secretIcon";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -14408668; // 0xfffffffffvar_ float:-2.1818104E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_nameIcon";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -5723992; // 0xffffffffffa8a8a8 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_pinnedIcon";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -7631473; // 0xffffffffff8b8d8f float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_message";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -7237231; // 0xfffffffffvar_ float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_messageArchived";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -7434095; // 0xffffffffff8e9091 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_message_threeLines";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -2274503; // 0xffffffffffdd4b39 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_draft";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -12812624; // 0xffffffffff3c7eb0 float:-2.5055266E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_nameMessage";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -7631473; // 0xffffffffff8b8d8f float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_nameMessageArchived";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -12434359; // 0xfffffffffvar_ float:-2.5822479E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_nameMessage_threeLines";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -10592674; // 0xffffffffff5e5e5e float:-2.955786E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_nameMessageArchived_threeLines";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -12812624; // 0xffffffffff3c7eb0 float:-2.5055266E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_attachMessage";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -12812624; // 0xffffffffff3c7eb0 float:-2.5055266E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_actionMessage";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -6973028; // 0xfffffffffvar_c float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_date";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_pinnedOverlay";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = NUM; // 0xvar_ float:6.3108872E-30 double:1.24335691E-315;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_tabletSelectedOverlay";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -12146122; // 0xfffffffffvar_aa36 float:-2.6407093E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_sentCheck";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -12146122; // 0xfffffffffvar_aa36 float:-2.6407093E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_sentReadCheck";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -9061026; // 0xfffffffffvar_bd5e float:-3.266441E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_sentClock";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -2796974; // 0xffffffffffd55252 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_sentError";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "chats_sentErrorIcon";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -13391642; // 0xfffffffffvar_a8e6 float:-2.3880878E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_verifiedBackground";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "chats_verifiedCheck";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -4341308; // 0xffffffffffbdc1c4 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_muteIcon";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "chats_mentionIcon";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = "chats_menuBackground";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -12303292; // 0xfffffffffvar_ float:-2.6088314E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_menuItemText";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -10907718; // 0xfffffffffvar_fba float:-2.8918875E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_menuItemCheck";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -7827048; // 0xfffffffffvar_ float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_menuItemIcon";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "chats_menuName";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = "chats_menuPhone";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -4004353; // 0xffffffffffc2e5ff float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_menuPhoneCats";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "chats_menuCloud";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -12420183; // 0xfffffffffvar_ba9 float:-2.5851231E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_menuCloudBackgroundCats";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "chats_actionIcon";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -10114592; // 0xfffffffffvar_a9e0 float:-3.0527525E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_actionBackground";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -11100714; // 0xfffffffffvar_dd6 float:-2.8527432E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_actionPressedBackground";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -9211021; // 0xfffffffffvar_ float:-3.2360185E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_actionUnreadIcon";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "chats_actionUnreadBackground";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -855310; // 0xfffffffffff2f2f2 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_actionUnreadPressedBackground";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -10907718; // 0xfffffffffvar_fba float:-2.8918875E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chats_menuTopBackgroundCats";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -12171706; // 0xfffffffffvar_ float:-2.6355202E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_attachMediaBanBackground";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "chat_attachMediaBanText";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = "chat_attachCheckBoxCheck";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -12995849; // 0xfffffffffvar_b2f7 float:-2.4683642E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_attachCheckBoxBackground";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = NUM; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_attachPhotoBackground";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -13391883; // 0xfffffffffvar_a7f5 float:-2.388039E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_attachActiveTab";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -7169634; // 0xfffffffffvar_e float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_attachUnactiveTab";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -13421773; // 0xfffffffffvar_ float:-2.3819765E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_attachPermissionImage";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -1945520; // 0xffffffffffe25050 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_attachPermissionMark";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -9472134; // 0xffffffffff6var_a float:-3.1830585E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_attachPermissionText";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -3355444; // 0xffffffffffcccccc float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_attachEmptyImage";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -12214795; // 0xfffffffffvar_df5 float:-2.6267807E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_attachGalleryBackground";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "chat_attachGalleryIcon";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -1351584; // 0xffffffffffeb6060 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_attachAudioBackground";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "chat_attachAudioIcon";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -13321743; // 0xfffffffffvar_b9f1 float:-2.402265E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_attachFileBackground";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "chat_attachFileIcon";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -868277; // 0xfffffffffff2CLASSNAMEb float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_attachContactBackground";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "chat_attachContactIcon";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -13187226; // 0xfffffffffvar_CLASSNAME float:-2.4295483E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_attachLocationBackground";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "chat_attachLocationIcon";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -868277; // 0xfffffffffff2CLASSNAMEb float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_attachPollBackground";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "chat_attachPollIcon";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -2758409; // 0xffffffffffd5e8f7 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_status";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -16725933; // 0xfffffffffvar_CLASSNAME float:-1.7118133E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_inDownCall";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -47032; // 0xfffffffffffvar_ float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_inUpCall";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -16725933; // 0xfffffffffvar_CLASSNAME float:-1.7118133E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_outUpCall";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = NUM; // 0x66728fa6 float:2.8636563E23 double:8.491920826E-315;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_shareBackground";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -NUM; // 0xfffffffvar_fa6 float:-1.2540116E-23 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_shareBackgroundSelected";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "chat_lockIcon";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -5124893; // 0xffffffffffb1cce3 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_muteIcon";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "chat_inBubble";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -1247235; // 0xffffffffffecf7fd float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_inBubbleSelected";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -14862509; // 0xffffffffff1d3753 float:-2.0897606E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_inBubbleShadow";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -1048610; // 0xffffffffffefffde float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_outBubble";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -2492475; // 0xffffffffffd9f7c5 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_outBubbleSelected";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -14781172; // 0xffffffffff1e750c float:-2.1062577E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_outBubbleShadow";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "chat_inMediaIcon";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = -1050370; // 0xffffffffffeff8fe float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_inMediaIconSelected";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -1048610; // 0xffffffffffefffde float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_outMediaIcon";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -1967921; // 0xffffffffffe1f8cf float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_outMediaIconSelected";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "chat_messageTextIn";
        r6.put(r13, r11);
        r6 = defaultColors;
        r13 = "chat_messageTextOut";
        r6.put(r13, r11);
        r6 = defaultColors;
        r13 = -14255946; // 0xfffffffffvar_b6 float:-2.2127861E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_messageLinkIn";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -14255946; // 0xfffffffffvar_b6 float:-2.2127861E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_messageLinkOut";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = "chat_serviceText";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = "chat_serviceLink";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = "chat_serviceIcon";
        r6.put(r13, r0);
        r6 = defaultColors;
        r13 = NUM; // 0x66000000 float:1.5111573E23 double:8.45482698E-315;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_mediaTimeBackground";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -10637232; // 0xffffffffff5db050 float:-2.9467485E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_outSentCheck";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -10637232; // 0xffffffffff5db050 float:-2.9467485E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_outSentCheckSelected";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -10637232; // 0xffffffffff5db050 float:-2.9467485E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_outSentCheckRead";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -10637232; // 0xffffffffff5db050 float:-2.9467485E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_outSentCheckReadSelected";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -9061026; // 0xfffffffffvar_bd5e float:-3.266441E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_outSentClock";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -9061026; // 0xfffffffffvar_bd5e float:-3.266441E38 double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_outSentClockSelected";
        r6.put(r14, r13);
        r6 = defaultColors;
        r13 = -6182221; // 0xffffffffffa1aab3 float:NaN double:NaN;
        r13 = java.lang.Integer.valueOf(r13);
        r14 = "chat_inSentClock";
        r6.put(r14, r13);
        r6 = defaultColors;
        r14 = -7094838; // 0xfffffffffvar_bdca float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inSentClockSelected";
        r6.put(r15, r14);
        r6 = defaultColors;
        r14 = "chat_mediaSentCheck";
        r6.put(r14, r0);
        r6 = defaultColors;
        r14 = "chat_mediaSentClock";
        r6.put(r14, r0);
        r6 = defaultColors;
        r14 = "chat_inViews";
        r6.put(r14, r13);
        r6 = defaultColors;
        r14 = -7094838; // 0xfffffffffvar_bdca float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inViewsSelected";
        r6.put(r15, r14);
        r6 = defaultColors;
        r14 = -9522601; // 0xffffffffff6eb257 float:-3.1728226E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outViews";
        r6.put(r15, r14);
        r6 = defaultColors;
        r14 = -9522601; // 0xffffffffff6eb257 float:-3.1728226E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outViewsSelected";
        r6.put(r15, r14);
        r6 = defaultColors;
        r14 = "chat_mediaViews";
        r6.put(r14, r0);
        r6 = defaultColors;
        r14 = -4801083; // 0xffffffffffb6bdc5 float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inMenu";
        r6.put(r15, r14);
        r6 = defaultColors;
        r14 = -6766130; // 0xfffffffffvar_c1ce float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_inMenuSelected";
        r6.put(r15, r14);
        r6 = defaultColors;
        r14 = -7221634; // 0xfffffffffvar_ce7e float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outMenu";
        r6.put(r15, r14);
        r6 = defaultColors;
        r14 = -7221634; // 0xfffffffffvar_ce7e float:NaN double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outMenuSelected";
        r6.put(r15, r14);
        r6 = defaultColors;
        r14 = "chat_mediaMenu";
        r6.put(r14, r0);
        r6 = defaultColors;
        r14 = -11162801; // 0xfffffffffvar_ab4f float:-2.8401505E38 double:NaN;
        r14 = java.lang.Integer.valueOf(r14);
        r15 = "chat_outInstant";
        r6.put(r15, r14);
        r6 = defaultColors;
        r15 = -12019389; // 0xfffffffffvar_ float:-2.6664138E38 double:NaN;
        r15 = java.lang.Integer.valueOf(r15);
        r5 = "chat_outInstantSelected";
        r6.put(r5, r15);
        r5 = defaultColors;
        r6 = "chat_inInstant";
        r5.put(r6, r10);
        r5 = defaultColors;
        r6 = -13600331; // 0xfffffffffvar_b5 float:-2.3457607E38 double:NaN;
        r6 = java.lang.Integer.valueOf(r6);
        r15 = "chat_inInstantSelected";
        r5.put(r15, r6);
        r5 = defaultColors;
        r6 = -2411211; // 0xffffffffffdb3535 float:NaN double:NaN;
        r6 = java.lang.Integer.valueOf(r6);
        r15 = "chat_sentError";
        r5.put(r15, r6);
        r5 = defaultColors;
        r6 = "chat_sentErrorIcon";
        r5.put(r6, r0);
        r5 = defaultColors;
        r6 = NUM; // 0x280a90f0 float:7.691967E-15 double:3.31903965E-315;
        r6 = java.lang.Integer.valueOf(r6);
        r15 = "chat_selectedBackground";
        r5.put(r15, r6);
        r5 = defaultColors;
        r6 = "chat_previewDurationText";
        r5.put(r6, r0);
        r5 = defaultColors;
        r6 = "chat_previewGameText";
        r5.put(r6, r0);
        r5 = defaultColors;
        r6 = "chat_inPreviewInstantText";
        r5.put(r6, r10);
        r5 = defaultColors;
        r6 = "chat_outPreviewInstantText";
        r5.put(r6, r14);
        r5 = defaultColors;
        r6 = -13600331; // 0xfffffffffvar_b5 float:-2.3457607E38 double:NaN;
        r6 = java.lang.Integer.valueOf(r6);
        r15 = "chat_inPreviewInstantSelectedText";
        r5.put(r15, r6);
        r5 = defaultColors;
        r6 = -12019389; // 0xfffffffffvar_ float:-2.6664138E38 double:NaN;
        r6 = java.lang.Integer.valueOf(r6);
        r15 = "chat_outPreviewInstantSelectedText";
        r5.put(r15, r6);
        r5 = defaultColors;
        r6 = -1776928; // 0xffffffffffe4e2e0 float:NaN double:NaN;
        r6 = java.lang.Integer.valueOf(r6);
        r15 = "chat_secretTimeText";
        r5.put(r15, r6);
        r5 = defaultColors;
        r6 = "chat_stickerNameText";
        r5.put(r6, r0);
        r5 = defaultColors;
        r6 = "chat_botButtonText";
        r5.put(r6, r0);
        r5 = defaultColors;
        r6 = "chat_botProgress";
        r5.put(r6, r0);
        r5 = defaultColors;
        r6 = -13072697; // 0xfffffffffvar_c7 float:-2.4527776E38 double:NaN;
        r6 = java.lang.Integer.valueOf(r6);
        r15 = "chat_inForwardedNameText";
        r5.put(r15, r6);
        r5 = defaultColors;
        r6 = "chat_outForwardedNameText";
        r5.put(r6, r14);
        r5 = defaultColors;
        r6 = "chat_inViaBotNameText";
        r5.put(r6, r10);
        r5 = defaultColors;
        r6 = "chat_outViaBotNameText";
        r5.put(r6, r14);
        r5 = defaultColors;
        r6 = "chat_stickerViaBotNameText";
        r5.put(r6, r0);
        r5 = defaultColors;
        r6 = -10903592; // 0xfffffffffvar_fd8 float:-2.8927243E38 double:NaN;
        r6 = java.lang.Integer.valueOf(r6);
        r15 = "chat_inReplyLine";
        r5.put(r15, r6);
        r5 = defaultColors;
        r6 = -9520791; // 0xffffffffff6eb969 float:-3.1731897E38 double:NaN;
        r6 = java.lang.Integer.valueOf(r6);
        r15 = "chat_outReplyLine";
        r5.put(r15, r6);
        r5 = defaultColors;
        r6 = "chat_stickerReplyLine";
        r5.put(r6, r0);
        r5 = defaultColors;
        r6 = "chat_inReplyNameText";
        r5.put(r6, r10);
        r5 = defaultColors;
        r6 = "chat_outReplyNameText";
        r5.put(r6, r14);
        r5 = defaultColors;
        r6 = "chat_stickerReplyNameText";
        r5.put(r6, r0);
        r5 = defaultColors;
        r6 = "chat_inReplyMessageText";
        r5.put(r6, r11);
        r5 = defaultColors;
        r6 = "chat_outReplyMessageText";
        r5.put(r6, r11);
        r5 = defaultColors;
        r6 = "chat_inReplyMediaMessageText";
        r5.put(r6, r13);
        r5 = defaultColors;
        r6 = -10112933; // 0xfffffffffvar_b05b float:-3.053089E38 double:NaN;
        r15 = java.lang.Integer.valueOf(r6);
        r3 = "chat_outReplyMediaMessageText";
        r5.put(r3, r15);
        r3 = defaultColors;
        r5 = -7752511; // 0xfffffffffvar_b4c1 float:NaN double:NaN;
        r15 = java.lang.Integer.valueOf(r5);
        r4 = "chat_inReplyMediaMessageSelectedText";
        r3.put(r4, r15);
        r3 = defaultColors;
        r4 = java.lang.Integer.valueOf(r6);
        r15 = "chat_outReplyMediaMessageSelectedText";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = "chat_stickerReplyMessageText";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -9390872; // 0xfffffffffvar_b4e8 float:-3.1995404E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_inPreviewLine";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = -7812741; // 0xfffffffffvar_CLASSNAMEb float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_outPreviewLine";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = "chat_inSiteNameText";
        r3.put(r4, r10);
        r3 = defaultColors;
        r4 = "chat_outSiteNameText";
        r3.put(r4, r14);
        r3 = defaultColors;
        r4 = -11625772; // 0xffffffffff4e9ad4 float:-2.7462488E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_inContactNameText";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = "chat_outContactNameText";
        r3.put(r4, r14);
        r3 = defaultColors;
        r4 = "chat_inContactPhoneText";
        r3.put(r4, r12);
        r3 = defaultColors;
        r4 = "chat_inContactPhoneSelectedText";
        r3.put(r4, r12);
        r3 = defaultColors;
        r4 = -13286860; // 0xfffffffffvar_ float:-2.4093401E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_outContactPhoneText";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = -13286860; // 0xfffffffffvar_ float:-2.4093401E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_outContactPhoneSelectedText";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = "chat_mediaProgress";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = "chat_inAudioProgress";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -1048610; // 0xffffffffffefffde float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_outAudioProgress";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = -1050370; // 0xffffffffffeff8fe float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_inAudioSelectedProgress";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = -1967921; // 0xffffffffffe1f8cf float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_outAudioSelectedProgress";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = "chat_mediaTimeText";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = "chat_inTimeText";
        r3.put(r4, r13);
        r3 = defaultColors;
        r4 = -9391780; // 0xfffffffffvar_b15c float:-3.1993562E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_outTimeText";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = -4143413; // 0xffffffffffc0c6cb float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_adminText";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = java.lang.Integer.valueOf(r5);
        r15 = "chat_adminSelectedText";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = java.lang.Integer.valueOf(r5);
        r15 = "chat_inTimeSelectedText";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = -9391780; // 0xfffffffffvar_b15c float:-3.1993562E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_outTimeSelectedText";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = "chat_inAudioPerfomerText";
        r3.put(r4, r12);
        r3 = defaultColors;
        r4 = "chat_inAudioPerfomerSelectedText";
        r3.put(r4, r12);
        r3 = defaultColors;
        r4 = -13286860; // 0xfffffffffvar_ float:-2.4093401E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_outAudioPerfomerText";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = -13286860; // 0xfffffffffvar_ float:-2.4093401E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_outAudioPerfomerSelectedText";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = -11625772; // 0xffffffffff4e9ad4 float:-2.7462488E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_inAudioTitleText";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = "chat_outAudioTitleText";
        r3.put(r4, r14);
        r3 = defaultColors;
        r4 = "chat_inAudioDurationText";
        r3.put(r4, r13);
        r3 = defaultColors;
        r4 = java.lang.Integer.valueOf(r6);
        r15 = "chat_outAudioDurationText";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = java.lang.Integer.valueOf(r5);
        r15 = "chat_inAudioDurationSelectedText";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = java.lang.Integer.valueOf(r6);
        r15 = "chat_outAudioDurationSelectedText";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = -1774864; // 0xffffffffffe4eaf0 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_inAudioSeekbar";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = NUM; // 0x3fe4eaf0 float:1.7884197 double:5.296220484E-315;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_inAudioCacheSeekbar";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = -4463700; // 0xffffffffffbbe3ac float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_outAudioSeekbar";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = NUM; // 0x3fbbe3ac float:1.4678855 double:5.28293587E-315;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_outAudioCacheSeekbar";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = -4399384; // 0xffffffffffbcdee8 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_inAudioSeekbarSelected";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = -5644906; // 0xffffffffffa9dd96 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_outAudioSeekbarSelected";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = -9259544; // 0xfffffffffvar_b5e8 float:-3.2261769E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_inAudioSeekbarFill";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = -8863118; // 0xfffffffffvar_CLASSNAME float:-3.3065816E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_outAudioSeekbarFill";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = -2169365; // 0xffffffffffdee5eb float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_inVoiceSeekbar";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = -4463700; // 0xffffffffffbbe3ac float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_outVoiceSeekbar";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = -4399384; // 0xffffffffffbcdee8 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_inVoiceSeekbarSelected";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = -5644906; // 0xffffffffffa9dd96 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_outVoiceSeekbarSelected";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = -9259544; // 0xfffffffffvar_b5e8 float:-3.2261769E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_inVoiceSeekbarFill";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = -8863118; // 0xfffffffffvar_CLASSNAME float:-3.3065816E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_outVoiceSeekbarFill";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = -1314571; // 0xffffffffffebf0f5 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_inFileProgress";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = -2427453; // 0xffffffffffdaf5c3 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_outFileProgress";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = -3413258; // 0xffffffffffcbeaf6 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_inFileProgressSelected";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = -3806041; // 0xffffffffffc5eca7 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_outFileProgressSelected";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = -11625772; // 0xffffffffff4e9ad4 float:-2.7462488E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = "chat_inFileNameText";
        r3.put(r15, r4);
        r3 = defaultColors;
        r4 = "chat_outFileNameText";
        r3.put(r4, r14);
        r3 = defaultColors;
        r4 = "chat_inFileInfoText";
        r3.put(r4, r13);
        r3 = defaultColors;
        r4 = java.lang.Integer.valueOf(r6);
        r14 = "chat_outFileInfoText";
        r3.put(r14, r4);
        r3 = defaultColors;
        r4 = java.lang.Integer.valueOf(r5);
        r14 = "chat_inFileInfoSelectedText";
        r3.put(r14, r4);
        r3 = defaultColors;
        r4 = java.lang.Integer.valueOf(r6);
        r14 = "chat_outFileInfoSelectedText";
        r3.put(r14, r4);
        r3 = defaultColors;
        r4 = -1314571; // 0xffffffffffebf0f5 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r14 = "chat_inFileBackground";
        r3.put(r14, r4);
        r3 = defaultColors;
        r4 = -2427453; // 0xffffffffffdaf5c3 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r14 = "chat_outFileBackground";
        r3.put(r14, r4);
        r3 = defaultColors;
        r4 = -3413258; // 0xffffffffffcbeaf6 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r14 = "chat_inFileBackgroundSelected";
        r3.put(r14, r4);
        r3 = defaultColors;
        r4 = -3806041; // 0xffffffffffc5eca7 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r14 = "chat_outFileBackgroundSelected";
        r3.put(r14, r4);
        r3 = defaultColors;
        r4 = "chat_inVenueInfoText";
        r3.put(r4, r13);
        r3 = defaultColors;
        r4 = java.lang.Integer.valueOf(r6);
        r14 = "chat_outVenueInfoText";
        r3.put(r14, r4);
        r3 = defaultColors;
        r4 = java.lang.Integer.valueOf(r5);
        r5 = "chat_inVenueInfoSelectedText";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = java.lang.Integer.valueOf(r6);
        r5 = "chat_outVenueInfoSelectedText";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "chat_mediaInfoText";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = NUM; // 0x3362a9e3 float:5.2774237E-8 double:4.25935987E-315;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_linkSelectBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = NUM; // 0x6662a9e3 float:2.6759717E23 double:8.48677336E-315;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_textSelectBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -986379; // 0xfffffffffff0f2f5 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_emojiPanelBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -11688214; // 0xffffffffff4da6ea float:-2.733584E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_emojiPanelBadgeBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "chat_emojiPanelBadgeText";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -1709586; // 0xffffffffffe5e9ee float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_emojiSearchBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -7036497; // 0xfffffffffvar_a1af float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_emojiSearchIcon";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = NUM; // 0x12000000 float:4.0389678E-28 double:1.49202829E-315;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_emojiPanelShadowLine";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -7038047; // 0xfffffffffvar_ba1 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_emojiPanelEmptyText";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -6445909; // 0xffffffffff9da4ab float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_emojiPanelIcon";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -7564905; // 0xffffffffff8CLASSNAME float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_emojiBottomPanelIcon";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -13920286; // 0xffffffffff2b97e2 float:-2.280866E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_emojiPanelIconSelected";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -1907225; // 0xffffffffffe2e5e7 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_emojiPanelStickerPackSelector";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -11097104; // 0xfffffffffvar_abf0 float:-2.8534754E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_emojiPanelStickerPackSelectorLine";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -7564905; // 0xffffffffff8CLASSNAME float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_emojiPanelBackspace";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "chat_emojiPanelMasksIcon";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -10305560; // 0xfffffffffvar_bfe8 float:-3.0140196E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_emojiPanelMasksIconSelected";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "chat_emojiPanelTrendingTitle";
        r3.put(r4, r8);
        r3 = defaultColors;
        r4 = -8221804; // 0xfffffffffvar_b94 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_emojiPanelStickerSetName";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -14184997; // 0xfffffffffvar_ddb float:-2.2271763E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_emojiPanelStickerSetNameHighlight";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -5130564; // 0xffffffffffb1b6bc float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_emojiPanelStickerSetNameIcon";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -7697782; // 0xffffffffff8a8a8a float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_emojiPanelTrendingDescription";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -13220017; // 0xfffffffffvar_f float:-2.4228975E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_botKeyboardButtonText";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -1775639; // 0xffffffffffe4e7e9 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_botKeyboardButtonBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -3354156; // 0xffffffffffccd1d4 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_botKeyboardButtonBackgroundPressed";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -6113849; // 0xffffffffffa2b5c7 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_unreadMessagesStartArrowIcon";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -11102772; // 0xfffffffffvar_cc float:-2.8523258E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_unreadMessagesStartText";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "chat_unreadMessagesStartBackground";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -6113849; // 0xffffffffffa2b5c7 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_inFileIcon";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -7883067; // 0xfffffffffvar_b6c5 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_inFileSelectedIcon";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -8011912; // 0xfffffffffvar_bvar_ float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_outFileIcon";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -8011912; // 0xfffffffffvar_bvar_ float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_outFileSelectedIcon";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -1314571; // 0xffffffffffebf0f5 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_inLocationBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -6113849; // 0xffffffffffa2b5c7 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_inLocationIcon";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -2427453; // 0xffffffffffdaf5c3 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_outLocationBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -7880840; // 0xfffffffffvar_bvar_ float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_outLocationIcon";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -9259544; // 0xfffffffffvar_b5e8 float:-3.2261769E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_inContactBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "chat_inContactIcon";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -8863118; // 0xfffffffffvar_CLASSNAME float:-3.3065816E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_outContactBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -1048610; // 0xffffffffffefffde float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_outContactIcon";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -12146122; // 0xfffffffffvar_aa36 float:-2.6407093E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_outBroadcast";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "chat_mediaBroadcast";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -9999761; // 0xfffffffffvar_a6f float:-3.076043E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_searchPanelIcons";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -9999761; // 0xfffffffffvar_a6f float:-3.076043E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_searchPanelText";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -8421505; // 0xffffffffff7f7f7f float:-3.3961514E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_secretChatStatusText";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "chat_fieldOverlayText";
        r3.put(r4, r10);
        r3 = defaultColors;
        r4 = "chat_stickersHintPanel";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -11032346; // 0xfffffffffvar_a8e6 float:-2.86661E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_replyPanelIcons";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -7432805; // 0xffffffffff8e959b float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_replyPanelClose";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "chat_replyPanelName";
        r3.put(r4, r10);
        r3 = defaultColors;
        r4 = "chat_replyPanelMessage";
        r3.put(r4, r8);
        r3 = defaultColors;
        r4 = -1513240; // 0xffffffffffe8e8e8 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_replyPanelLine";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "chat_messagePanelBackground";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = "chat_messagePanelText";
        r3.put(r4, r11);
        r3 = defaultColors;
        r4 = -5985101; // 0xffffffffffa4acb3 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_messagePanelHint";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -11230757; // 0xfffffffffvar_a1db float:-2.8263674E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_messagePanelCursor";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "chat_messagePanelShadow";
        r3.put(r4, r11);
        r3 = defaultColors;
        r4 = -7432805; // 0xffffffffff8e959b float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_messagePanelIcons";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -11817481; // 0xffffffffff4badf7 float:-2.7073656E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_messagePanelVideoFrame";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "chat_recordedVoicePlayPause";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -2495749; // 0xffffffffffd9eafb float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_recordedVoicePlayPausePressed";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -2468275; // 0xffffffffffda564d float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_recordedVoiceDot";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -11165981; // 0xfffffffffvar_ee3 float:-2.8395055E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_recordedVoiceBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -6107400; // 0xffffffffffa2cef8 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_recordedVoiceProgress";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "chat_recordedVoiceProgressInner";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = java.lang.Integer.valueOf(r9);
        r5 = "chat_recordVoiceCancel";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -10309397; // 0xfffffffffvar_b0eb float:-3.0132414E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_messagePanelSend";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "chat_messagePanelPressedSend";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -5987164; // 0xffffffffffa4a4a4 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "key_chat_messagePanelVoiceLock";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "key_chat_messagePanelVoiceLockBackground";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = "key_chat_messagePanelVoiceLockShadow";
        r3.put(r4, r11);
        r3 = defaultColors;
        r4 = -11711413; // 0xffffffffff4d4c4b float:-2.7288787E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_recordTime";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -11688214; // 0xffffffffff4da6ea float:-2.733584E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_emojiPanelNewTrending";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "chat_gifSaveHintText";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -NUM; // 0xffffffffcCLASSNAME float:-3.8028356E7 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_gifSaveHintBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "chat_goDownButton";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = "chat_goDownButtonShadow";
        r3.put(r4, r11);
        r3 = defaultColors;
        r4 = -7432805; // 0xffffffffff8e959b float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_goDownButtonIcon";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "chat_goDownButtonCounter";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -11689240; // 0xffffffffff4da2e8 float:-2.733376E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_goDownButtonCounterBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -5395027; // 0xffffffffffadadad float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_messagePanelCancelInlineBot";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "chat_messagePanelVoicePressed";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -11037236; // 0xfffffffffvar_cc float:-2.865618E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_messagePanelVoiceBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = NUM; // 0xd000000 float:3.9443045E-31 double:1.07757599E-315;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_messagePanelVoiceShadow";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -9211021; // 0xfffffffffvar_ float:-3.2360185E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_messagePanelVoiceDelete";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "chat_messagePanelVoiceDuration";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -11037236; // 0xfffffffffvar_cc float:-2.865618E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_inlineResultIcon";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "chat_topPanelBackground";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -7563878; // 0xffffffffff8CLASSNAMEa float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_topPanelClose";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -9658414; // 0xffffffffff6c9fd2 float:-3.1452764E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_topPanelLine";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "chat_topPanelTitle";
        r3.put(r4, r10);
        r3 = defaultColors;
        r4 = java.lang.Integer.valueOf(r9);
        r5 = "chat_topPanelMessage";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -3188393; // 0xffffffffffcvar_ float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_reportSpam";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -11894091; // 0xffffffffff4a82b5 float:-2.6918272E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_addContact";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -9259544; // 0xfffffffffvar_b5e8 float:-3.2261769E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_inLoader";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -10114080; // 0xfffffffffvar_abe0 float:-3.0528564E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_inLoaderSelected";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -8863118; // 0xfffffffffvar_CLASSNAME float:-3.3065816E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_outLoader";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -9783964; // 0xffffffffff6ab564 float:-3.1198118E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_outLoaderSelected";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -6113080; // 0xffffffffffa2b8c8 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_inLoaderPhoto";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -6113849; // 0xffffffffffa2b5c7 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_inLoaderPhotoSelected";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -197380; // 0xfffffffffffcfcfc float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_inLoaderPhotoIcon";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -1314571; // 0xffffffffffebf0f5 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_inLoaderPhotoIconSelected";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -8011912; // 0xfffffffffvar_bvar_ float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_outLoaderPhoto";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -8538000; // 0xffffffffff7db870 float:-3.3725234E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_outLoaderPhotoSelected";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -2427453; // 0xffffffffffdaf5c3 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_outLoaderPhotoIcon";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -4134748; // 0xffffffffffc0e8a4 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_outLoaderPhotoIconSelected";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = NUM; // 0x66000000 float:1.5111573E23 double:8.45482698E-315;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_mediaLoaderPhoto";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = NUM; // 0x7var_ float:1.7014118E38 double:1.0527088494E-314;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_mediaLoaderPhotoSelected";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "chat_mediaLoaderPhotoIcon";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -2500135; // 0xffffffffffd9d9d9 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_mediaLoaderPhotoIconSelected";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -NUM; // 0xffffffffcc3e648e float:-4.9910328E7 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_secretTimerBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "chat_secretTimerText";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -12937771; // 0xffffffffff3a95d5 float:-2.4801438E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "profile_creatorIcon";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -8288630; // 0xfffffffffvar_a float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "profile_actionIcon";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "profile_actionBackground";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -855310; // 0xfffffffffff2f2f2 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "profile_actionPressedBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -5056776; // 0xffffffffffb2d6f8 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "profile_verifiedBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -11959368; // 0xfffffffffvar_b8 float:-2.6785875E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "profile_verifiedCheck";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "profile_title";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -2626822; // 0xffffffffffd7eafa float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "profile_status";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "player_actionBar";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = NUM; // 0xvar_ float:6.3108872E-30 double:1.24335691E-315;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "player_actionBarSelector";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "player_actionBarTitle";
        r3.put(r4, r12);
        r3 = defaultColors;
        r4 = -NUM; // 0xfffffffvar_ float:-6.617445E-24 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "player_actionBarTop";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -7697782; // 0xffffffffff8a8a8a float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "player_actionBarSubtitle";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -7697782; // 0xffffffffff8a8a8a float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "player_actionBarItems";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "player_background";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -7564650; // 0xffffffffff8CLASSNAME float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "player_time";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -1445899; // 0xffffffffffe9eff5 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "player_progressBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -1445899; // 0xffffffffffe9eff5 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "key_player_progressCachedBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -11821085; // 0xffffffffff4b9fe3 float:-2.7066346E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "player_progress";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -5723992; // 0xffffffffffa8a8a8 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "player_placeholder";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = java.lang.Integer.valueOf(r7);
        r5 = "player_placeholderBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -13421773; // 0xfffffffffvar_ float:-2.3819765E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "player_button";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -11753238; // 0xffffffffff4ca8ea float:-2.7203956E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "player_buttonActive";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -1973016; // 0xffffffffffe1e4e8 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "key_sheet_scrollUp";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -3551789; // 0xffffffffffc9cdd3 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "key_sheet_other";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = java.lang.Integer.valueOf(r9);
        r5 = "files_folderIcon";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = java.lang.Integer.valueOf(r7);
        r5 = "files_folderIconBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "files_iconText";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -6908266; // 0xfffffffffvar_ float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "sessions_devicesImage";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -12211217; // 0xfffffffffvar_abef float:-2.6275065E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "passport_authorizeBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -12542501; // 0xfffffffffvar_ddb float:-2.560314E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "passport_authorizeBackgroundSelected";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "passport_authorizeText";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -9592620; // 0xffffffffff6da0d4 float:-3.158621E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "location_sendLocationBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -39836; // 0xfffffffffffvar_ float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "location_sendLiveLocationBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "location_sendLocationIcon";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = "location_sendLiveLocationIcon";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -13262875; // 0xfffffffffvar_fe5 float:-2.4142049E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "location_liveLocationProgress";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -11753238; // 0xffffffffff4ca8ea float:-2.7203956E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "location_placeLocationBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -13262875; // 0xfffffffffvar_fe5 float:-2.4142049E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "dialog_liveLocationProgress";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -16725933; // 0xfffffffffvar_CLASSNAME float:-1.7118133E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "calls_callReceivedGreenIcon";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -47032; // 0xfffffffffffvar_ float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "calls_callReceivedRedIcon";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -11491093; // 0xfffffffffvar_a8eb float:-2.773565E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "featuredStickers_addedIcon";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "featuredStickers_buttonProgress";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -11491093; // 0xfffffffffvar_a8eb float:-2.773565E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "featuredStickers_addButton";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -12346402; // 0xfffffffffvar_bde float:-2.6000877E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "featuredStickers_addButtonPressed";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -2533545; // 0xffffffffffd95757 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "featuredStickers_delButton";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -3782327; // 0xffffffffffCLASSNAME float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "featuredStickers_delButtonPressed";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "featuredStickers_buttonText";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -11688214; // 0xffffffffff4da6ea float:-2.733584E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "featuredStickers_unread";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "inappPlayerPerformer";
        r3.put(r4, r12);
        r3 = defaultColors;
        r4 = "inappPlayerTitle";
        r3.put(r4, r12);
        r3 = defaultColors;
        r4 = "inappPlayerBackground";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -10309397; // 0xfffffffffvar_b0eb float:-3.0132414E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "inappPlayerPlayPause";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -5723992; // 0xffffffffffa8a8a8 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "inappPlayerClose";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -12279325; // 0xfffffffffvar_a1e3 float:-2.6136925E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "returnToCallBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "returnToCallText";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -13196562; // 0xfffffffffvar_a2ee float:-2.4276547E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "sharedMedia_startStopLoadIcon";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -986123; // 0xfffffffffff0f3f5 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "sharedMedia_linkPlaceholder";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -4735293; // 0xffffffffffb7bec3 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "sharedMedia_linkPlaceholderText";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -1182729; // 0xffffffffffedf3f7 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "sharedMedia_photoPlaceholder";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -12154957; // 0xfffffffffvar_b3 float:-2.6389173E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "sharedMedia_actionMode";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -10567099; // 0xffffffffff5eCLASSNAME float:-2.9609732E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "checkbox";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "checkboxCheck";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -5195326; // 0xffffffffffb0b9c2 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "checkboxDisabled";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -4801083; // 0xffffffffffb6bdc5 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "stickers_menu";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = NUM; // 0xvar_ float:6.3108872E-30 double:1.24335691E-315;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "stickers_menuSelector";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -4669499; // 0xffffffffffb8bfc5 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "changephoneinfo_image";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -11491350; // 0xfffffffffvar_a7ea float:-2.7735128E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "changephoneinfo_image2";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "groupcreate_hintText";
        r3.put(r4, r13);
        r3 = defaultColors;
        r4 = -11361317; // 0xfffffffffvar_a3db float:-2.7998867E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "groupcreate_cursor";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "groupcreate_sectionShadow";
        r3.put(r4, r11);
        r3 = defaultColors;
        r4 = -8617336; // 0xffffffffff7CLASSNAME float:-3.3564321E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "groupcreate_sectionText";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "groupcreate_spanText";
        r3.put(r4, r8);
        r3 = defaultColors;
        r4 = -855310; // 0xfffffffffff2f2f2 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "groupcreate_spanBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "groupcreate_spanDelete";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -11157919; // 0xfffffffffvar_be61 float:-2.8411407E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "contacts_inviteBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "contacts_inviteText";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -1971470; // 0xffffffffffe1eaf2 float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "login_progressInner";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -10313520; // 0xfffffffffvar_a0d0 float:-3.0124051E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "login_progressOuter";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -14043401; // 0xfffffffffvar_b6f7 float:-2.2558954E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "musicPicker_checkbox";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "musicPicker_checkboxCheck";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -10702870; // 0xffffffffff5cafea float:-2.9334356E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "musicPicker_buttonBackground";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "musicPicker_buttonIcon";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -15095832; // 0xfffffffffvar_a7e8 float:-2.042437E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "picker_enabledButton";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = java.lang.Integer.valueOf(r9);
        r5 = "picker_disabledButton";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -14043401; // 0xfffffffffvar_b6f7 float:-2.2558954E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "picker_badge";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "picker_badgeText";
        r3.put(r4, r0);
        r3 = defaultColors;
        r4 = -12348980; // 0xfffffffffvar_cc float:-2.5995648E38 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "chat_botSwitchToInlineText";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -NUM; // 0xffffffffea272var_ float:-5.05284E25 double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "undo_background";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = -8008961; // 0xfffffffffvar_caff float:NaN double:NaN;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = "undo_cancelColor";
        r3.put(r5, r4);
        r3 = defaultColors;
        r4 = "undo_infoColor";
        r3.put(r4, r0);
        r0 = fallbackKeys;
        r3 = "chat_adminText";
        r4 = "chat_inTimeText";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chat_adminSelectedText";
        r4 = "chat_inTimeSelectedText";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "key_player_progressCachedBackground";
        r4 = "player_progressBackground";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chat_inAudioCacheSeekbar";
        r4 = "chat_inAudioSeekbar";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chat_outAudioCacheSeekbar";
        r4 = "chat_outAudioSeekbar";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chat_emojiSearchBackground";
        r4 = "chat_emojiPanelStickerPackSelector";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "location_sendLiveLocationIcon";
        r4 = "location_sendLocationIcon";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "changephoneinfo_image2";
        r4 = "featuredStickers_addButton";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "key_graySectionText";
        r4 = "windowBackgroundWhiteGrayText2";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chat_inMediaIcon";
        r4 = "chat_inBubble";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chat_outMediaIcon";
        r4 = "chat_outBubble";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chat_inMediaIconSelected";
        r4 = "chat_inBubbleSelected";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chat_outMediaIconSelected";
        r4 = "chat_outBubbleSelected";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chats_actionUnreadIcon";
        r4 = "profile_actionIcon";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chats_actionUnreadBackground";
        r4 = "profile_actionBackground";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chats_actionUnreadPressedBackground";
        r4 = "profile_actionPressedBackground";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "dialog_inlineProgressBackground";
        r4 = "windowBackgroundGray";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "dialog_inlineProgress";
        r4 = "chats_menuItemIcon";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "groupcreate_spanDelete";
        r4 = "chats_actionIcon";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "sharedMedia_photoPlaceholder";
        r4 = "windowBackgroundGray";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chat_attachPollBackground";
        r4 = "chat_attachAudioBackground";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chat_attachPollIcon";
        r4 = "chat_attachAudioIcon";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chats_onlineCircle";
        r4 = "windowBackgroundWhiteBlueText";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "windowBackgroundWhiteBlueButton";
        r4 = "windowBackgroundWhiteValueText";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "windowBackgroundWhiteBlueIcon";
        r4 = "windowBackgroundWhiteValueText";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "undo_background";
        r4 = "chat_gifSaveHintBackground";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "undo_cancelColor";
        r4 = "chat_gifSaveHintText";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "undo_infoColor";
        r4 = "chat_gifSaveHintText";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "windowBackgroundUnchecked";
        r4 = "windowBackgroundWhite";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "windowBackgroundChecked";
        r4 = "windowBackgroundWhite";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "switchTrackBlue";
        r4 = "switchTrack";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "switchTrackBlueChecked";
        r4 = "switchTrackChecked";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "switchTrackBlueThumb";
        r4 = "windowBackgroundWhite";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "switchTrackBlueThumbChecked";
        r4 = "windowBackgroundWhite";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "windowBackgroundCheckText";
        r4 = "windowBackgroundWhiteBlackText";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "contextProgressInner4";
        r4 = "contextProgressInner1";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "contextProgressOuter4";
        r4 = "contextProgressOuter1";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "switchTrackBlueSelector";
        r4 = "listSelectorSDK21";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "switchTrackBlueSelectorChecked";
        r4 = "listSelectorSDK21";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chat_emojiBottomPanelIcon";
        r4 = "chat_emojiPanelIcon";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chat_emojiSearchIcon";
        r4 = "chat_emojiPanelIcon";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chat_emojiPanelStickerSetNameHighlight";
        r4 = "windowBackgroundWhiteBlueText4";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chat_emojiPanelStickerPackSelectorLine";
        r4 = "chat_emojiPanelIconSelected";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "sharedMedia_actionMode";
        r4 = "actionBarDefault";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "key_sheet_scrollUp";
        r4 = "chat_emojiPanelStickerPackSelector";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "key_sheet_other";
        r4 = "player_actionBarItems";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "dialogSearchBackground";
        r4 = "chat_emojiPanelStickerPackSelector";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "dialogSearchHint";
        r4 = "chat_emojiPanelIcon";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "dialogSearchIcon";
        r4 = "chat_emojiPanelIcon";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "dialogSearchText";
        r4 = "windowBackgroundWhiteBlackText";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "dialogFloatingButton";
        r4 = "dialogRoundCheckBox";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "dialogFloatingButtonPressed";
        r4 = "dialogRoundCheckBox";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "dialogFloatingIcon";
        r4 = "dialogRoundCheckBoxCheck";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "dialogShadowLine";
        r4 = "chat_emojiPanelShadowLine";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "actionBarDefaultArchived";
        r4 = "actionBarDefault";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "actionBarDefaultArchivedSelector";
        r4 = "actionBarDefaultSelector";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "actionBarDefaultArchivedIcon";
        r4 = "actionBarDefaultIcon";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "actionBarDefaultArchivedTitle";
        r4 = "actionBarDefaultTitle";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "actionBarDefaultArchivedSearch";
        r4 = "actionBarDefaultSearch";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "actionBarDefaultSearchArchivedPlaceholder";
        r4 = "actionBarDefaultSearchPlaceholder";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chats_message_threeLines";
        r4 = "chats_message";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chats_nameMessage_threeLines";
        r4 = "chats_nameMessage";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chats_nameArchived";
        r4 = "chats_name";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chats_nameMessageArchived";
        r4 = "chats_nameMessage";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chats_nameMessageArchived_threeLines";
        r4 = "chats_nameMessage";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chats_messageArchived";
        r4 = "chats_message";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "avatar_backgroundArchived";
        r4 = "chats_unreadCounterMuted";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "avatar_backgroundArchivedHidden";
        r4 = "chats_unreadCounterMuted";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chats_archiveBackground";
        r4 = "chats_actionBackground";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chats_archivePinBackground";
        r4 = "chats_unreadCounterMuted";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chats_archiveIcon";
        r4 = "chats_actionIcon";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chats_archiveText";
        r4 = "chats_actionIcon";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "actionBarDefaultSubmenuItemIcon";
        r4 = "dialogIcon";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "checkboxDisabled";
        r4 = "chats_unreadCounterMuted";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chat_status";
        r4 = "actionBarDefaultSubtitle";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chat_inDownCall";
        r4 = "calls_callReceivedGreenIcon";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chat_inUpCall";
        r4 = "calls_callReceivedRedIcon";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chat_outUpCall";
        r4 = "calls_callReceivedGreenIcon";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "actionBarTabActiveText";
        r4 = "actionBarDefaultTitle";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "actionBarTabUnactiveText";
        r4 = "actionBarDefaultSubtitle";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "actionBarTabLine";
        r4 = "actionBarDefaultTitle";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "actionBarTabSelector";
        r4 = "actionBarDefaultSelector";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "profile_status";
        r4 = "avatar_subtitleInProfileBlue";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chats_menuTopBackgroundCats";
        r4 = "avatar_backgroundActionBarBlue";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chat_messagePanelPressedSend";
        r4 = "chat_messagePanelVoicePressed";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chat_attachPermissionImage";
        r4 = "dialogTextBlack";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chat_attachPermissionMark";
        r4 = "chat_sentError";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chat_attachPermissionText";
        r4 = "dialogTextBlack";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chat_attachEmptyImage";
        r4 = "emptyListPlaceholder";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "actionBarBrowser";
        r4 = "actionBarDefault";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chats_sentReadCheck";
        r4 = "chats_sentCheck";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chat_outSentCheckRead";
        r4 = "chat_outSentCheck";
        r0.put(r3, r4);
        r0 = fallbackKeys;
        r3 = "chat_outSentCheckReadSelected";
        r4 = "chat_outSentCheckSelected";
        r0.put(r3, r4);
        r0 = themeAccentExclusionKeys;
        r3 = keys_avatar_background;
        r3 = java.util.Arrays.asList(r3);
        r0.addAll(r3);
        r0 = themeAccentExclusionKeys;
        r3 = keys_avatar_nameInMessage;
        r3 = java.util.Arrays.asList(r3);
        r0.addAll(r3);
        r0 = themeAccentExclusionKeys;
        r3 = "chat_attachFileBackground";
        r0.add(r3);
        r0 = themeAccentExclusionKeys;
        r3 = "chat_attachGalleryBackground";
        r0.add(r3);
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
        r0 = new org.telegram.ui.ActionBar.Theme$ThemeInfo;
        r0.<init>();
        r3 = "Default";
        r0.name = r3;
        r3 = -3155485; // 0xffffffffffcfd9e3 float:NaN double:NaN;
        r0.previewBackgroundColor = r3;
        r0.previewInColor = r2;
        r3 = -983328; // 0xfffffffffff0fee0 float:NaN double:NaN;
        r0.previewOutColor = r3;
        r0.sortIndex = r1;
        r3 = themes;
        defaultTheme = r0;
        currentTheme = r0;
        currentDayTheme = r0;
        r3.add(r0);
        r0 = themesDict;
        r3 = defaultTheme;
        r4 = "Default";
        r0.put(r4, r3);
        r0 = new org.telegram.ui.ActionBar.Theme$ThemeInfo;
        r0.<init>();
        r3 = "Blue";
        r0.name = r3;
        r3 = "bluebubbles.attheme";
        r0.assetName = r3;
        r3 = -6963476; // 0xfffffffffvar_beec float:NaN double:NaN;
        r0.previewBackgroundColor = r3;
        r0.previewInColor = r2;
        r3 = -3086593; // 0xffffffffffd0e6ff float:NaN double:NaN;
        r0.previewOutColor = r3;
        r3 = 1;
        r0.sortIndex = r3;
        r3 = 9;
        r3 = new int[r3];
        r3 = {-13464881, -12342073, -11359164, -3317869, -2981834, -8165684, -3256745, -2904512, -8681301};
        r0.setAccentColorOptions(r3);
        r3 = themes;
        r3.add(r0);
        r3 = themesDict;
        r4 = "Blue";
        r3.put(r4, r0);
        r0 = new org.telegram.ui.ActionBar.Theme$ThemeInfo;
        r0.<init>();
        r3 = "Dark Blue";
        r0.name = r3;
        r3 = "darkblue.attheme";
        r0.assetName = r3;
        r3 = -10523006; // 0xffffffffff5f6e82 float:-2.9699163E38 double:NaN;
        r0.previewBackgroundColor = r3;
        r3 = -9009508; // 0xfffffffffvar_c float:-3.2768902E38 double:NaN;
        r0.previewInColor = r3;
        r3 = -8214301; // 0xfffffffffvar_a8e3 float:NaN double:NaN;
        r0.previewOutColor = r3;
        r3 = 2;
        r0.sortIndex = r3;
        r3 = 9;
        r3 = new int[r3];
        r3 = {-13203974, -12138259, -11880383, -1344335, -1142742, -6127120, -2931932, -1131212, -8417365};
        r0.setAccentColorOptions(r3);
        r3 = themes;
        r3.add(r0);
        r3 = themesDict;
        currentNightTheme = r0;
        r4 = "Dark Blue";
        r3.put(r4, r0);
        r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION;
        if (r0 == 0) goto L_0x215f;
    L_0x2134:
        r0 = new org.telegram.ui.ActionBar.Theme$ThemeInfo;
        r0.<init>();
        r3 = "Graphite";
        r0.name = r3;
        r3 = "graphite.attheme";
        r0.assetName = r3;
        r3 = -8749431; // 0xffffffffff7a7e89 float:-3.32964E38 double:NaN;
        r0.previewBackgroundColor = r3;
        r3 = -6775901; // 0xfffffffffvar_ba3 float:NaN double:NaN;
        r0.previewInColor = r3;
        r3 = -5980167; // 0xffffffffffa4bff9 float:NaN double:NaN;
        r0.previewOutColor = r3;
        r3 = 3;
        r0.sortIndex = r3;
        r3 = themes;
        r3.add(r0);
        r3 = themesDict;
        r4 = "Graphite";
        r3.put(r4, r0);
    L_0x215f:
        r0 = new org.telegram.ui.ActionBar.Theme$ThemeInfo;
        r0.<init>();
        r3 = "Arctic Blue";
        r0.name = r3;
        r3 = "arctic.attheme";
        r0.assetName = r3;
        r0.previewBackgroundColor = r2;
        r3 = -1315084; // 0xffffffffffebeef4 float:NaN double:NaN;
        r0.previewInColor = r3;
        r3 = -8604930; // 0xffffffffff7cb2fe float:-3.3589484E38 double:NaN;
        r0.previewOutColor = r3;
        r3 = 4;
        r0.sortIndex = r3;
        r3 = 9;
        r3 = new int[r3];
        r3 = {-13332245, -12342073, -11359164, -3317869, -2981834, -8165684, -3256745, -2904512, -8681301};
        r0.setAccentColorOptions(r3);
        r3 = themes;
        r3.add(r0);
        r3 = themesDict;
        r4 = "Arctic Blue";
        r3.put(r4, r0);
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r3 = "themeconfig";
        r0 = r0.getSharedPreferences(r3, r1);
        r3 = 0;
        r4 = "themes2";
        r3 = r0.getString(r4, r3);
        r4 = "remoteThemesHash";
        r4 = r0.getInt(r4, r1);
        remoteThemesHash = r4;
        r4 = "lastLoadingThemesTime";
        r4 = r0.getInt(r4, r1);
        lastLoadingThemesTime = r4;
        r4 = android.text.TextUtils.isEmpty(r3);
        if (r4 != 0) goto L_0x21e7;
    L_0x21b6:
        r0 = new org.json.JSONArray;	 Catch:{ Exception -> 0x21e2 }
        r0.<init>(r3);	 Catch:{ Exception -> 0x21e2 }
        r3 = 0;
    L_0x21bc:
        r4 = r0.length();	 Catch:{ Exception -> 0x21e2 }
        if (r3 >= r4) goto L_0x222d;
    L_0x21c2:
        r4 = r0.getJSONObject(r3);	 Catch:{ Exception -> 0x21e2 }
        r4 = org.telegram.ui.ActionBar.Theme.ThemeInfo.createWithJson(r4);	 Catch:{ Exception -> 0x21e2 }
        if (r4 == 0) goto L_0x21df;
    L_0x21cc:
        r5 = otherThemes;	 Catch:{ Exception -> 0x21e2 }
        r5.add(r4);	 Catch:{ Exception -> 0x21e2 }
        r5 = themes;	 Catch:{ Exception -> 0x21e2 }
        r5.add(r4);	 Catch:{ Exception -> 0x21e2 }
        r5 = themesDict;	 Catch:{ Exception -> 0x21e2 }
        r6 = r4.getKey();	 Catch:{ Exception -> 0x21e2 }
        r5.put(r6, r4);	 Catch:{ Exception -> 0x21e2 }
    L_0x21df:
        r3 = r3 + 1;
        goto L_0x21bc;
    L_0x21e2:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x222d;
    L_0x21e7:
        r3 = 0;
        r4 = "themes";
        r3 = r0.getString(r4, r3);
        r4 = android.text.TextUtils.isEmpty(r3);
        if (r4 != 0) goto L_0x221c;
    L_0x21f4:
        r4 = "&";
        r3 = r3.split(r4);
        r4 = 0;
    L_0x21fb:
        r5 = r3.length;
        if (r4 >= r5) goto L_0x221c;
    L_0x21fe:
        r5 = r3[r4];
        r5 = org.telegram.ui.ActionBar.Theme.ThemeInfo.createWithString(r5);
        if (r5 == 0) goto L_0x2219;
    L_0x2206:
        r6 = otherThemes;
        r6.add(r5);
        r6 = themes;
        r6.add(r5);
        r6 = themesDict;
        r7 = r5.getKey();
        r6.put(r7, r5);
    L_0x2219:
        r4 = r4 + 1;
        goto L_0x21fb;
    L_0x221c:
        r3 = 1;
        saveOtherThemes(r3);
        r0 = r0.edit();
        r3 = "themes";
        r0 = r0.remove(r3);
        r0.commit();
    L_0x222d:
        sortThemes();
        r3 = 0;
        r0 = themesDict;	 Catch:{ Exception -> 0x2354 }
        r4 = "Dark Blue";
        r0 = r0.get(r4);	 Catch:{ Exception -> 0x2354 }
        r4 = r0;
        r4 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r4;	 Catch:{ Exception -> 0x2354 }
        r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings();	 Catch:{ Exception -> 0x2354 }
        r5 = "theme";
        r6 = 0;
        r5 = r0.getString(r5, r6);	 Catch:{ Exception -> 0x2354 }
        r6 = "Dark";
        r6 = r6.equals(r5);	 Catch:{ Exception -> 0x2354 }
        if (r6 == 0) goto L_0x225b;
    L_0x224f:
        r3 = -8417365; // 0xffffffffff7f8fab float:-3.396991E38 double:NaN;
        r4.setAccentColor(r3);	 Catch:{ Exception -> 0x2257 }
        r3 = r4;
        goto L_0x2266;
    L_0x2257:
        r0 = move-exception;
        r3 = r4;
        goto L_0x2355;
    L_0x225b:
        if (r5 == 0) goto L_0x2266;
    L_0x225d:
        r6 = themesDict;	 Catch:{ Exception -> 0x2354 }
        r5 = r6.get(r5);	 Catch:{ Exception -> 0x2354 }
        r5 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r5;	 Catch:{ Exception -> 0x2354 }
        r3 = r5;
    L_0x2266:
        r5 = "nighttheme";
        r6 = 0;
        r5 = r0.getString(r5, r6);	 Catch:{ Exception -> 0x2354 }
        r6 = "Dark";
        r6 = r6.equals(r5);	 Catch:{ Exception -> 0x2354 }
        if (r6 == 0) goto L_0x227e;
    L_0x2275:
        currentNightTheme = r4;	 Catch:{ Exception -> 0x2354 }
        r5 = -8417365; // 0xffffffffff7f8fab float:-3.396991E38 double:NaN;
        r4.setAccentColor(r5);	 Catch:{ Exception -> 0x2354 }
        goto L_0x228c;
    L_0x227e:
        if (r5 == 0) goto L_0x228c;
    L_0x2280:
        r4 = themesDict;	 Catch:{ Exception -> 0x2354 }
        r4 = r4.get(r5);	 Catch:{ Exception -> 0x2354 }
        r4 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r4;	 Catch:{ Exception -> 0x2354 }
        if (r4 == 0) goto L_0x228c;
    L_0x228a:
        currentNightTheme = r4;	 Catch:{ Exception -> 0x2354 }
    L_0x228c:
        r4 = themesDict;	 Catch:{ Exception -> 0x2354 }
        r4 = r4.values();	 Catch:{ Exception -> 0x2354 }
        r4 = r4.iterator();	 Catch:{ Exception -> 0x2354 }
    L_0x2296:
        r5 = r4.hasNext();	 Catch:{ Exception -> 0x2354 }
        if (r5 == 0) goto L_0x22c7;
    L_0x229c:
        r5 = r4.next();	 Catch:{ Exception -> 0x2354 }
        r5 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r5;	 Catch:{ Exception -> 0x2354 }
        r6 = r5.assetName;	 Catch:{ Exception -> 0x2354 }
        if (r6 == 0) goto L_0x2296;
    L_0x22a6:
        r6 = r5.accentBaseColor;	 Catch:{ Exception -> 0x2354 }
        if (r6 == 0) goto L_0x2296;
    L_0x22aa:
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x2354 }
        r6.<init>();	 Catch:{ Exception -> 0x2354 }
        r7 = "accent_for_";
        r6.append(r7);	 Catch:{ Exception -> 0x2354 }
        r7 = r5.assetName;	 Catch:{ Exception -> 0x2354 }
        r6.append(r7);	 Catch:{ Exception -> 0x2354 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x2354 }
        r7 = r5.accentColor;	 Catch:{ Exception -> 0x2354 }
        r6 = r0.getInt(r6, r7);	 Catch:{ Exception -> 0x2354 }
        r5.setAccentColor(r6);	 Catch:{ Exception -> 0x2354 }
        goto L_0x2296;
    L_0x22c7:
        r4 = "selectedAutoNightType";
        r4 = r0.getInt(r4, r1);	 Catch:{ Exception -> 0x2354 }
        selectedAutoNightType = r4;	 Catch:{ Exception -> 0x2354 }
        r4 = "autoNightScheduleByLocation";
        r4 = r0.getBoolean(r4, r1);	 Catch:{ Exception -> 0x2354 }
        autoNightScheduleByLocation = r4;	 Catch:{ Exception -> 0x2354 }
        r4 = "autoNightBrighnessThreshold";
        r5 = NUM; // 0x3e800000 float:0.25 double:5.180653787E-315;
        r4 = r0.getFloat(r4, r5);	 Catch:{ Exception -> 0x2354 }
        autoNightBrighnessThreshold = r4;	 Catch:{ Exception -> 0x2354 }
        r4 = "autoNightDayStartTime";
        r5 = 1320; // 0x528 float:1.85E-42 double:6.52E-321;
        r4 = r0.getInt(r4, r5);	 Catch:{ Exception -> 0x2354 }
        autoNightDayStartTime = r4;	 Catch:{ Exception -> 0x2354 }
        r4 = "autoNightDayEndTime";
        r5 = 480; // 0x1e0 float:6.73E-43 double:2.37E-321;
        r4 = r0.getInt(r4, r5);	 Catch:{ Exception -> 0x2354 }
        autoNightDayEndTime = r4;	 Catch:{ Exception -> 0x2354 }
        r4 = "autoNightSunsetTime";
        r5 = 1320; // 0x528 float:1.85E-42 double:6.52E-321;
        r4 = r0.getInt(r4, r5);	 Catch:{ Exception -> 0x2354 }
        autoNightSunsetTime = r4;	 Catch:{ Exception -> 0x2354 }
        r4 = "autoNightSunriseTime";
        r5 = 480; // 0x1e0 float:6.73E-43 double:2.37E-321;
        r4 = r0.getInt(r4, r5);	 Catch:{ Exception -> 0x2354 }
        autoNightSunriseTime = r4;	 Catch:{ Exception -> 0x2354 }
        r4 = "autoNightCityName";
        r5 = "";
        r4 = r0.getString(r4, r5);	 Catch:{ Exception -> 0x2354 }
        autoNightCityName = r4;	 Catch:{ Exception -> 0x2354 }
        r4 = "autoNightLocationLatitude3";
        r5 = 10000; // 0x2710 float:1.4013E-41 double:4.9407E-320;
        r4 = r0.getLong(r4, r5);	 Catch:{ Exception -> 0x2354 }
        r6 = 10000; // 0x2710 float:1.4013E-41 double:4.9407E-320;
        r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r8 == 0) goto L_0x2328;
    L_0x2321:
        r4 = java.lang.Double.longBitsToDouble(r4);	 Catch:{ Exception -> 0x2354 }
        autoNightLocationLatitude = r4;	 Catch:{ Exception -> 0x2354 }
        goto L_0x232f;
    L_0x2328:
        r4 = NUM; // 0x40cNUM float:0.0 double:10000.0;
        autoNightLocationLatitude = r4;	 Catch:{ Exception -> 0x2354 }
    L_0x232f:
        r4 = "autoNightLocationLongitude3";
        r5 = 10000; // 0x2710 float:1.4013E-41 double:4.9407E-320;
        r4 = r0.getLong(r4, r5);	 Catch:{ Exception -> 0x2354 }
        r6 = 10000; // 0x2710 float:1.4013E-41 double:4.9407E-320;
        r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r8 == 0) goto L_0x2344;
    L_0x233d:
        r4 = java.lang.Double.longBitsToDouble(r4);	 Catch:{ Exception -> 0x2354 }
        autoNightLocationLongitude = r4;	 Catch:{ Exception -> 0x2354 }
        goto L_0x234b;
    L_0x2344:
        r4 = NUM; // 0x40cNUM float:0.0 double:10000.0;
        autoNightLocationLongitude = r4;	 Catch:{ Exception -> 0x2354 }
    L_0x234b:
        r4 = "autoNightLastSunCheckDay";
        r0 = r0.getInt(r4, r2);	 Catch:{ Exception -> 0x2354 }
        autoNightLastSunCheckDay = r0;	 Catch:{ Exception -> 0x2354 }
        goto L_0x2358;
    L_0x2354:
        r0 = move-exception;
    L_0x2355:
        org.telegram.messenger.FileLog.e(r0);
    L_0x2358:
        if (r3 != 0) goto L_0x235d;
    L_0x235a:
        r3 = defaultTheme;
        goto L_0x235f;
    L_0x235d:
        currentDayTheme = r3;
    L_0x235f:
        applyTheme(r3, r1, r1, r1);
        r0 = org.telegram.ui.ActionBar.-$$Lambda$RQB0Jwr1FTqp6hrbGUHuOs-9k1I.INSTANCE;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        r0 = new org.telegram.ui.ActionBar.Theme$7;
        r0.<init>();
        ambientSensorListener = r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.<clinit>():void");
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
                Drawable access$700 = Theme.getStateDrawable(this, i);
                ColorFilter colorFilter = null;
                if (access$700 instanceof BitmapDrawable) {
                    colorFilter = ((BitmapDrawable) access$700).getPaint().getColorFilter();
                } else if (access$700 instanceof NinePatchDrawable) {
                    colorFilter = ((NinePatchDrawable) access$700).getPaint().getColorFilter();
                }
                boolean selectDrawable = super.selectDrawable(i);
                if (colorFilter != null) {
                    access$700.setColorFilter(colorFilter);
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
                Drawable access$700 = Theme.getStateDrawable(this, i);
                ColorFilter colorFilter = null;
                if (access$700 instanceof BitmapDrawable) {
                    colorFilter = ((BitmapDrawable) access$700).getPaint().getColorFilter();
                } else if (access$700 instanceof NinePatchDrawable) {
                    colorFilter = ((NinePatchDrawable) access$700).getPaint().getColorFilter();
                }
                boolean selectDrawable = super.selectDrawable(i);
                if (colorFilter != null) {
                    access$700.setColorFilter(colorFilter);
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
        r1 = NUM; // 0x7var_bc float:1.7945478E38 double:1.0529357224E-314;
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
                Drawable access$700 = Theme.getStateDrawable(this, i);
                ColorFilter colorFilter = null;
                if (access$700 instanceof BitmapDrawable) {
                    colorFilter = ((BitmapDrawable) access$700).getPaint().getColorFilter();
                } else if (access$700 instanceof NinePatchDrawable) {
                    colorFilter = ((NinePatchDrawable) access$700).getPaint().getColorFilter();
                }
                boolean selectDrawable = super.selectDrawable(i);
                if (colorFilter != null) {
                    access$700.setColorFilter(colorFilter);
                }
                return selectDrawable;
            }
        };
        anonymousClass5.addState(new int[]{16842919}, mutate2);
        anonymousClass5.addState(new int[]{16842913}, mutate2);
        anonymousClass5.addState(StateSet.WILD_CARD, mutate);
        return anonymousClass5;
    }

    public static Drawable createCircleDrawable(int i, int i2) {
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
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, shapeDrawable2);
        stateListDrawable.addState(new int[]{16842913}, shapeDrawable2);
        stateListDrawable.addState(StateSet.WILD_CARD, shapeDrawable);
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
            if (isWallpaperMotionPrev != null) {
                Editor edit = MessagesController.getGlobalMainSettings().edit();
                boolean booleanValue = isWallpaperMotionPrev.booleanValue();
                isWallpaperMotion = booleanValue;
                edit.putBoolean("selectedBackgroundMotion", booleanValue);
                edit.commit();
            }
            applyTheme(previousTheme, true, false, false);
            previousTheme = null;
            checkAutoNightThemeConditions();
        }
    }

    private static void sortThemes() {
        Collections.sort(themes, -$$Lambda$Theme$CDAxGNnEyNa6tkvecQwKSXq77-I.INSTANCE);
    }

    static /* synthetic */ int lambda$sortThemes$0(ThemeInfo themeInfo, ThemeInfo themeInfo2) {
        if (themeInfo.pathToFile == null && themeInfo.assetName == null) {
            return -1;
        }
        if (themeInfo2.pathToFile == null && themeInfo2.assetName == null) {
            return 1;
        }
        return themeInfo.name.compareTo(themeInfo2.name);
    }

    public static void applyThemeTemporary(ThemeInfo themeInfo) {
        previousTheme = getCurrentTheme();
        applyTheme(themeInfo, false, false, false);
    }

    public static ThemeInfo fillThemeValues(File file, String str, TL_theme tL_theme) {
        try {
            ThemeInfo themeInfo = new ThemeInfo();
            themeInfo.name = str;
            themeInfo.info = tL_theme;
            themeInfo.pathToFile = file.getAbsolutePath();
            themeInfo.account = UserConfig.selectedAccount;
            String[] strArr = new String[1];
            getThemeFileValues(new File(themeInfo.pathToFile), null, strArr);
            int i = 0;
            if (TextUtils.isEmpty(strArr[0])) {
                themedWallpaperLink = null;
            } else {
                str = strArr[0];
                File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(Utilities.MD5(str));
                stringBuilder.append(".wp");
                themeInfo.pathToWallpaper = new File(filesDirFixed, stringBuilder.toString()).getAbsolutePath();
                try {
                    Uri parse = Uri.parse(str);
                    themeInfo.slug = parse.getQueryParameter("slug");
                    str = parse.getQueryParameter("mode");
                    if (str != null) {
                        strArr = str.toLowerCase().split(" ");
                        if (strArr != null && strArr.length > 0) {
                            while (i < strArr.length) {
                                if ("blur".equals(strArr[i])) {
                                    themeInfo.isBlured = true;
                                } else if ("motion".equals(strArr[i])) {
                                    themeInfo.isMotion = true;
                                }
                                i++;
                            }
                        }
                    }
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
            return themeInfo;
        } catch (Exception th2) {
            FileLog.e(th2);
            return null;
        }
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
                applyThemeTemporary(themeInfo);
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

    public static void applyTheme(ThemeInfo themeInfo) {
        applyTheme(themeInfo, true, true, false);
    }

    public static void applyTheme(ThemeInfo themeInfo, boolean z) {
        applyTheme(themeInfo, true, true, z);
    }

    /* JADX WARNING: Removed duplicated region for block: B:67:0x0151 A:{Catch:{ all -> 0x01ac }} */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x01b7 A:{Catch:{ Exception -> 0x01bf }} */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x01c7  */
    private static void applyTheme(org.telegram.ui.ActionBar.Theme.ThemeInfo r17, boolean r18, boolean r19, boolean r20) {
        /*
        r1 = r17;
        r2 = r20;
        if (r1 != 0) goto L_0x0007;
    L_0x0006:
        return;
    L_0x0007:
        r0 = org.telegram.ui.Components.ThemeEditorView.getInstance();
        if (r0 == 0) goto L_0x0010;
    L_0x000d:
        r0.destroy();
    L_0x0010:
        r3 = 0;
        r0 = r1.pathToFile;	 Catch:{ Exception -> 0x01bf }
        r4 = "overrideThemeWallpaper";
        r5 = "theme";
        r6 = 0;
        if (r0 != 0) goto L_0x0045;
    L_0x001a:
        r0 = r1.assetName;	 Catch:{ Exception -> 0x01bf }
        if (r0 == 0) goto L_0x001f;
    L_0x001e:
        goto L_0x0045;
    L_0x001f:
        if (r2 != 0) goto L_0x0036;
    L_0x0021:
        if (r18 == 0) goto L_0x0036;
    L_0x0023:
        r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings();	 Catch:{ Exception -> 0x01bf }
        r0 = r0.edit();	 Catch:{ Exception -> 0x01bf }
        r0.remove(r5);	 Catch:{ Exception -> 0x01bf }
        if (r19 == 0) goto L_0x0033;
    L_0x0030:
        r0.remove(r4);	 Catch:{ Exception -> 0x01bf }
    L_0x0033:
        r0.commit();	 Catch:{ Exception -> 0x01bf }
    L_0x0036:
        r0 = currentColorsNoAccent;	 Catch:{ Exception -> 0x01bf }
        r0.clear();	 Catch:{ Exception -> 0x01bf }
        themedWallpaperFileOffset = r3;	 Catch:{ Exception -> 0x01bf }
        themedWallpaperLink = r6;	 Catch:{ Exception -> 0x01bf }
        wallpaper = r6;	 Catch:{ Exception -> 0x01bf }
        themedWallpaper = r6;	 Catch:{ Exception -> 0x01bf }
        goto L_0x01b3;
    L_0x0045:
        if (r2 != 0) goto L_0x0060;
    L_0x0047:
        if (r18 == 0) goto L_0x0060;
    L_0x0049:
        r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings();	 Catch:{ Exception -> 0x01bf }
        r0 = r0.edit();	 Catch:{ Exception -> 0x01bf }
        r7 = r17.getKey();	 Catch:{ Exception -> 0x01bf }
        r0.putString(r5, r7);	 Catch:{ Exception -> 0x01bf }
        if (r19 == 0) goto L_0x005d;
    L_0x005a:
        r0.remove(r4);	 Catch:{ Exception -> 0x01bf }
    L_0x005d:
        r0.commit();	 Catch:{ Exception -> 0x01bf }
    L_0x0060:
        r0 = 1;
        r4 = new java.lang.String[r0];	 Catch:{ Exception -> 0x01bf }
        r5 = r1.assetName;	 Catch:{ Exception -> 0x01bf }
        if (r5 == 0) goto L_0x0070;
    L_0x0067:
        r5 = r1.assetName;	 Catch:{ Exception -> 0x01bf }
        r5 = getThemeFileValues(r6, r5, r6);	 Catch:{ Exception -> 0x01bf }
        currentColorsNoAccent = r5;	 Catch:{ Exception -> 0x01bf }
        goto L_0x007d;
    L_0x0070:
        r5 = new java.io.File;	 Catch:{ Exception -> 0x01bf }
        r7 = r1.pathToFile;	 Catch:{ Exception -> 0x01bf }
        r5.<init>(r7);	 Catch:{ Exception -> 0x01bf }
        r5 = getThemeFileValues(r5, r6, r4);	 Catch:{ Exception -> 0x01bf }
        currentColorsNoAccent = r5;	 Catch:{ Exception -> 0x01bf }
    L_0x007d:
        r5 = currentColorsNoAccent;	 Catch:{ Exception -> 0x01bf }
        r7 = "wallpaperFileOffset";
        r5 = r5.get(r7);	 Catch:{ Exception -> 0x01bf }
        r5 = (java.lang.Integer) r5;	 Catch:{ Exception -> 0x01bf }
        if (r5 == 0) goto L_0x008e;
    L_0x0089:
        r5 = r5.intValue();	 Catch:{ Exception -> 0x01bf }
        goto L_0x008f;
    L_0x008e:
        r5 = -1;
    L_0x008f:
        themedWallpaperFileOffset = r5;	 Catch:{ Exception -> 0x01bf }
        r5 = r4[r3];	 Catch:{ Exception -> 0x01bf }
        r5 = android.text.TextUtils.isEmpty(r5);	 Catch:{ Exception -> 0x01bf }
        if (r5 != 0) goto L_0x01b1;
    L_0x0099:
        r4 = r4[r3];	 Catch:{ Exception -> 0x01bf }
        themedWallpaperLink = r4;	 Catch:{ Exception -> 0x01bf }
        r4 = new java.io.File;	 Catch:{ Exception -> 0x01bf }
        r5 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();	 Catch:{ Exception -> 0x01bf }
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01bf }
        r7.<init>();	 Catch:{ Exception -> 0x01bf }
        r8 = themedWallpaperLink;	 Catch:{ Exception -> 0x01bf }
        r8 = org.telegram.messenger.Utilities.MD5(r8);	 Catch:{ Exception -> 0x01bf }
        r7.append(r8);	 Catch:{ Exception -> 0x01bf }
        r8 = ".wp";
        r7.append(r8);	 Catch:{ Exception -> 0x01bf }
        r7 = r7.toString();	 Catch:{ Exception -> 0x01bf }
        r4.<init>(r5, r7);	 Catch:{ Exception -> 0x01bf }
        r4 = r4.getAbsolutePath();	 Catch:{ Exception -> 0x01bf }
        r1.pathToWallpaper = r4;	 Catch:{ Exception -> 0x01bf }
        r4 = themedWallpaperLink;	 Catch:{ all -> 0x01ac }
        r4 = android.net.Uri.parse(r4);	 Catch:{ all -> 0x01ac }
        r5 = "slug";
        r5 = r4.getQueryParameter(r5);	 Catch:{ all -> 0x01ac }
        r1.slug = r5;	 Catch:{ all -> 0x01ac }
        r5 = "id";
        r5 = r4.getQueryParameter(r5);	 Catch:{ all -> 0x01ac }
        r5 = org.telegram.messenger.Utilities.parseLong(r5);	 Catch:{ all -> 0x01ac }
        r7 = r5.longValue();	 Catch:{ all -> 0x01ac }
        r5 = "pattern";
        r5 = r4.getQueryParameter(r5);	 Catch:{ all -> 0x01ac }
        r5 = org.telegram.messenger.Utilities.parseLong(r5);	 Catch:{ all -> 0x01ac }
        r9 = r5.longValue();	 Catch:{ all -> 0x01ac }
        r5 = "mode";
        r5 = r4.getQueryParameter(r5);	 Catch:{ all -> 0x01ac }
        if (r5 == 0) goto L_0x0124;
    L_0x00f5:
        r5 = r5.toLowerCase();	 Catch:{ all -> 0x01ac }
        r11 = " ";
        r5 = r5.split(r11);	 Catch:{ all -> 0x01ac }
        if (r5 == 0) goto L_0x0124;
    L_0x0101:
        r11 = r5.length;	 Catch:{ all -> 0x01ac }
        if (r11 <= 0) goto L_0x0124;
    L_0x0104:
        r11 = 0;
    L_0x0105:
        r12 = r5.length;	 Catch:{ all -> 0x01ac }
        if (r11 >= r12) goto L_0x0124;
    L_0x0108:
        r12 = "blur";
        r13 = r5[r11];	 Catch:{ all -> 0x01ac }
        r12 = r12.equals(r13);	 Catch:{ all -> 0x01ac }
        if (r12 == 0) goto L_0x0115;
    L_0x0112:
        r1.isBlured = r0;	 Catch:{ all -> 0x01ac }
        goto L_0x0121;
    L_0x0115:
        r12 = "motion";
        r13 = r5[r11];	 Catch:{ all -> 0x01ac }
        r12 = r12.equals(r13);	 Catch:{ all -> 0x01ac }
        if (r12 == 0) goto L_0x0121;
    L_0x011f:
        r1.isMotion = r0;	 Catch:{ all -> 0x01ac }
    L_0x0121:
        r11 = r11 + 1;
        goto L_0x0105;
    L_0x0124:
        r5 = "intensity";
        r5 = r4.getQueryParameter(r5);	 Catch:{ all -> 0x01ac }
        r5 = org.telegram.messenger.Utilities.parseInt(r5);	 Catch:{ all -> 0x01ac }
        r5 = r5.intValue();	 Catch:{ all -> 0x01ac }
        r11 = "bg_color";
        r4 = r4.getQueryParameter(r11);	 Catch:{ Exception -> 0x0148 }
        r11 = android.text.TextUtils.isEmpty(r4);	 Catch:{ Exception -> 0x0148 }
        if (r11 != 0) goto L_0x0148;
    L_0x013e:
        r11 = 16;
        r4 = java.lang.Integer.parseInt(r4, r11);	 Catch:{ Exception -> 0x0148 }
        r11 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r4 = r4 | r11;
        goto L_0x0149;
    L_0x0148:
        r4 = 0;
    L_0x0149:
        r11 = r1.slug;	 Catch:{ all -> 0x01ac }
        r11 = android.text.TextUtils.isEmpty(r11);	 Catch:{ all -> 0x01ac }
        if (r11 != 0) goto L_0x01b3;
    L_0x0151:
        r11 = org.telegram.messenger.MessagesController.getGlobalMainSettings();	 Catch:{ all -> 0x01ac }
        r11 = r11.edit();	 Catch:{ all -> 0x01ac }
        if (r18 == 0) goto L_0x0197;
    L_0x015b:
        r12 = "selectedBackgroundSlug";
        r13 = r1.slug;	 Catch:{ all -> 0x01ac }
        r11.putString(r12, r13);	 Catch:{ all -> 0x01ac }
        r12 = "selectedPattern";
        r13 = "selectedBackground2";
        r14 = 0;
        r16 = (r9 > r14 ? 1 : (r9 == r14 ? 0 : -1));
        if (r16 == 0) goto L_0x0177;
    L_0x016c:
        r7 = -1;
        r11.putLong(r13, r7);	 Catch:{ all -> 0x01ac }
        r11.putLong(r12, r9);	 Catch:{ all -> 0x01ac }
        isPatternWallpaper = r0;	 Catch:{ all -> 0x01ac }
        goto L_0x017f;
    L_0x0177:
        r11.putLong(r13, r7);	 Catch:{ all -> 0x01ac }
        r11.putLong(r12, r14);	 Catch:{ all -> 0x01ac }
        isPatternWallpaper = r3;	 Catch:{ all -> 0x01ac }
    L_0x017f:
        r0 = "selectedBackgroundBlurred";
        r7 = r1.isBlured;	 Catch:{ all -> 0x01ac }
        r11.putBoolean(r0, r7);	 Catch:{ all -> 0x01ac }
        r0 = "selectedColor";
        r11.putInt(r0, r4);	 Catch:{ all -> 0x01ac }
        r0 = "selectedIntensity";
        r4 = (float) r5;	 Catch:{ all -> 0x01ac }
        r5 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r4 = r4 / r5;
        r11.putFloat(r0, r4);	 Catch:{ all -> 0x01ac }
        isWallpaperMotionPrev = r6;	 Catch:{ all -> 0x01ac }
        goto L_0x019f;
    L_0x0197:
        r0 = isWallpaperMotion;	 Catch:{ all -> 0x01ac }
        r0 = java.lang.Boolean.valueOf(r0);	 Catch:{ all -> 0x01ac }
        isWallpaperMotionPrev = r0;	 Catch:{ all -> 0x01ac }
    L_0x019f:
        r0 = "selectedBackgroundMotion";
        r4 = r1.isMotion;	 Catch:{ all -> 0x01ac }
        isWallpaperMotion = r4;	 Catch:{ all -> 0x01ac }
        r11.putBoolean(r0, r4);	 Catch:{ all -> 0x01ac }
        r11.commit();	 Catch:{ all -> 0x01ac }
        goto L_0x01b3;
    L_0x01ac:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x01bf }
        goto L_0x01b3;
    L_0x01b1:
        themedWallpaperLink = r6;	 Catch:{ Exception -> 0x01bf }
    L_0x01b3:
        currentTheme = r1;	 Catch:{ Exception -> 0x01bf }
        if (r2 != 0) goto L_0x01bb;
    L_0x01b7:
        r0 = currentTheme;	 Catch:{ Exception -> 0x01bf }
        currentDayTheme = r0;	 Catch:{ Exception -> 0x01bf }
    L_0x01bb:
        refreshThemeColors();	 Catch:{ Exception -> 0x01bf }
        goto L_0x01c3;
    L_0x01bf:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x01c3:
        r0 = previousTheme;
        if (r0 != 0) goto L_0x01d0;
    L_0x01c7:
        r0 = r1.account;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r0.saveTheme(r1, r2, r3);
    L_0x01d0:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.applyTheme(org.telegram.ui.ActionBar.Theme$ThemeInfo, boolean, boolean, boolean):void");
    }

    private static void refreshThemeColors() {
        currentColors.clear();
        currentColors.putAll(currentColorsNoAccent);
        ThemeInfo themeInfo = currentTheme;
        int i = themeInfo.accentColor;
        if (i != 0) {
            int i2 = themeInfo.accentBaseColor;
            if (!(i2 == 0 || i == i2)) {
                HashSet hashSet = new HashSet(currentColorsNoAccent.keySet());
                hashSet.addAll(defaultColors.keySet());
                hashSet.removeAll(themeAccentExclusionKeys);
                Iterator it = hashSet.iterator();
                while (it.hasNext()) {
                    String str = (String) it.next();
                    Integer num = (Integer) currentColorsNoAccent.get(str);
                    if (num == null) {
                        String str2 = (String) fallbackKeys.get(str);
                        if (!(str2 == null || currentColorsNoAccent.get(str2) == null)) {
                        }
                    }
                    if (num == null) {
                        num = (Integer) defaultColors.get(str);
                    }
                    int changeColorAccent = changeColorAccent(themeInfo.accentBaseColorHsv, themeInfo.accentColorHsv, num.intValue());
                    if (changeColorAccent != num.intValue()) {
                        currentColors.put(str, Integer.valueOf(changeColorAccent));
                    }
                }
            }
        }
        reloadWallpaper();
        applyCommonTheme();
        applyDialogsTheme();
        applyProfileTheme();
        applyChatTheme(false);
        AndroidUtilities.runOnUIThread(-$$Lambda$Theme$JmSLMyb-373NmPAc6NmsxsfWldE.INSTANCE);
    }

    public static int changeColorAccent(ThemeInfo themeInfo, int i, int i2) {
        if (i != 0) {
            int i3 = themeInfo.accentBaseColor;
            if (!(i3 == 0 || i == i3)) {
                Color.colorToHSV(i, hsv);
                return changeColorAccent(themeInfo.accentBaseColorHsv, hsv, i2);
            }
        }
        return i2;
    }

    public static int changeColorAccent(int i) {
        ThemeInfo themeInfo = currentTheme;
        return changeColorAccent(themeInfo, themeInfo.accentColor, i);
    }

    private static int changeColorAccent(float[] fArr, float[] fArr2, int i) {
        float f = fArr[0];
        float f2 = fArr[1];
        float f3 = fArr[2];
        float f4 = fArr2[0];
        float f5 = fArr2[1];
        float f6 = fArr2[2];
        Color.colorToHSV(i, hsv);
        float[] fArr3 = hsv;
        float f7 = fArr3[0];
        float f8 = fArr3[1];
        float f9 = fArr3[2];
        float var_ = f7 - f;
        if (Math.min(Math.abs(var_), Math.abs(var_ - 360.0f)) > 30.0f) {
            return i;
        }
        var_ = Math.min((1.5f * f8) / f2, 1.0f);
        float[] fArr4 = hsv;
        fArr4[0] = (f7 + f4) - f;
        fArr4[1] = (f8 * f5) / f2;
        fArr4[2] = f9 * ((1.0f - var_) + ((var_ * f6) / f3));
        return Color.HSVToColor(Color.alpha(i), hsv);
    }

    public static void applyCurrentThemeAccent(int i) {
        currentTheme.setAccentColor(i);
        refreshThemeColors();
    }

    public static void saveThemeAccent(ThemeInfo themeInfo, int i) {
        if (themeInfo.assetName != null) {
            Editor edit = MessagesController.getGlobalMainSettings().edit();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("accent_for_");
            stringBuilder.append(themeInfo.assetName);
            edit.putInt(stringBuilder.toString(), i).commit();
            themeInfo.setAccentColor(i);
        }
    }

    private static void saveOtherThemes(boolean z) {
        int i = 0;
        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
        if (z) {
            JSONArray jSONArray = new JSONArray();
            while (i < otherThemes.size()) {
                JSONObject saveJson = ((ThemeInfo) otherThemes.get(i)).getSaveJson();
                if (saveJson != null) {
                    jSONArray.put(saveJson);
                }
                i++;
            }
            edit.putString("themes2", jSONArray.toString());
        }
        edit.putInt("remoteThemesHash", remoteThemesHash);
        edit.putInt("lastLoadingThemesTime", lastLoadingThemesTime);
        edit.putInt("lastLoadingCurrentThemeTime", lastLoadingCurrentThemeTime);
        edit.commit();
    }

    public static HashMap<String, Integer> getDefaultColors() {
        return defaultColors;
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

    private static boolean isCurrentThemeDefault() {
        return currentTheme == defaultTheme;
    }

    public static boolean isThemeDefault(ThemeInfo themeInfo) {
        return themeInfo == defaultTheme;
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

    /* JADX WARNING: Removed duplicated region for block: B:67:0x010f  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x010f  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x010f  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0117  */
    /* JADX WARNING: Missing block: B:59:0x00ff, code skipped:
            if (switchNightRunnableScheduled == false) goto L_0x0101;
     */
    /* JADX WARNING: Missing block: B:62:0x0105, code skipped:
            if (switchDayRunnableScheduled == false) goto L_0x010a;
     */
    /* JADX WARNING: Missing block: B:63:0x0108, code skipped:
            if (r1 == 0) goto L_0x010a;
     */
    public static void checkAutoNightThemeConditions(boolean r12) {
        /*
        r0 = previousTheme;
        if (r0 == 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r0 = 0;
        if (r12 == 0) goto L_0x001e;
    L_0x0008:
        r1 = switchNightRunnableScheduled;
        if (r1 == 0) goto L_0x0013;
    L_0x000c:
        switchNightRunnableScheduled = r0;
        r1 = switchNightBrightnessRunnable;
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r1);
    L_0x0013:
        r1 = switchDayRunnableScheduled;
        if (r1 == 0) goto L_0x001e;
    L_0x0017:
        switchDayRunnableScheduled = r0;
        r1 = switchDayBrightnessRunnable;
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r1);
    L_0x001e:
        r1 = selectedAutoNightType;
        r2 = 2;
        if (r1 == r2) goto L_0x0055;
    L_0x0023:
        r1 = switchNightRunnableScheduled;
        if (r1 == 0) goto L_0x002e;
    L_0x0027:
        switchNightRunnableScheduled = r0;
        r1 = switchNightBrightnessRunnable;
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r1);
    L_0x002e:
        r1 = switchDayRunnableScheduled;
        if (r1 == 0) goto L_0x0039;
    L_0x0032:
        switchDayRunnableScheduled = r0;
        r1 = switchDayBrightnessRunnable;
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r1);
    L_0x0039:
        r1 = lightSensorRegistered;
        if (r1 == 0) goto L_0x0055;
    L_0x003d:
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        lastBrightnessValue = r1;
        r1 = sensorManager;
        r3 = ambientSensorListener;
        r4 = lightSensor;
        r1.unregisterListener(r3, r4);
        lightSensorRegistered = r0;
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r1 == 0) goto L_0x0055;
    L_0x0050:
        r1 = "light sensor unregistered";
        org.telegram.messenger.FileLog.d(r1);
    L_0x0055:
        r1 = selectedAutoNightType;
        r3 = 5;
        r4 = 1;
        if (r1 != r4) goto L_0x00be;
    L_0x005b:
        r1 = java.util.Calendar.getInstance();
        r5 = java.lang.System.currentTimeMillis();
        r1.setTimeInMillis(r5);
        r5 = 11;
        r5 = r1.get(r5);
        r5 = r5 * 60;
        r6 = 12;
        r6 = r1.get(r6);
        r5 = r5 + r6;
        r6 = autoNightScheduleByLocation;
        if (r6 == 0) goto L_0x00a8;
    L_0x0079:
        r1 = r1.get(r3);
        r3 = autoNightLastSunCheckDay;
        if (r3 == r1) goto L_0x00a3;
    L_0x0081:
        r6 = autoNightLocationLatitude;
        r8 = NUM; // 0x40cNUM float:0.0 double:10000.0;
        r3 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r3 == 0) goto L_0x00a3;
    L_0x008c:
        r10 = autoNightLocationLongitude;
        r3 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1));
        if (r3 == 0) goto L_0x00a3;
    L_0x0092:
        r3 = org.telegram.messenger.time.SunDate.calculateSunriseSunset(r6, r10);
        r6 = r3[r0];
        autoNightSunriseTime = r6;
        r3 = r3[r4];
        autoNightSunsetTime = r3;
        autoNightLastSunCheckDay = r1;
        saveAutoNightThemeConfig();
    L_0x00a3:
        r1 = autoNightSunsetTime;
        r3 = autoNightSunriseTime;
        goto L_0x00ac;
    L_0x00a8:
        r1 = autoNightDayStartTime;
        r3 = autoNightDayEndTime;
    L_0x00ac:
        if (r1 >= r3) goto L_0x00b3;
    L_0x00ae:
        if (r1 > r5) goto L_0x010a;
    L_0x00b0:
        if (r5 > r3) goto L_0x010a;
    L_0x00b2:
        goto L_0x00bd;
    L_0x00b3:
        if (r1 > r5) goto L_0x00b9;
    L_0x00b5:
        r1 = 1440; // 0x5a0 float:2.018E-42 double:7.115E-321;
        if (r5 <= r1) goto L_0x0101;
    L_0x00b9:
        if (r5 < 0) goto L_0x010a;
    L_0x00bb:
        if (r5 > r3) goto L_0x010a;
    L_0x00bd:
        goto L_0x0101;
    L_0x00be:
        if (r1 != r2) goto L_0x0108;
    L_0x00c0:
        r1 = lightSensor;
        if (r1 != 0) goto L_0x00d8;
    L_0x00c4:
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r5 = "sensor";
        r1 = r1.getSystemService(r5);
        r1 = (android.hardware.SensorManager) r1;
        sensorManager = r1;
        r1 = sensorManager;
        r1 = r1.getDefaultSensor(r3);
        lightSensor = r1;
    L_0x00d8:
        r1 = lightSensorRegistered;
        if (r1 != 0) goto L_0x00f5;
    L_0x00dc:
        r1 = lightSensor;
        if (r1 == 0) goto L_0x00f5;
    L_0x00e0:
        r3 = sensorManager;
        r5 = ambientSensorListener;
        r6 = 500000; // 0x7a120 float:7.00649E-40 double:2.47033E-318;
        r3.registerListener(r5, r1, r6);
        lightSensorRegistered = r4;
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r1 == 0) goto L_0x00f5;
    L_0x00f0:
        r1 = "light sensor registered";
        org.telegram.messenger.FileLog.d(r1);
    L_0x00f5:
        r1 = lastBrightnessValue;
        r3 = autoNightBrighnessThreshold;
        r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r1 > 0) goto L_0x0103;
    L_0x00fd:
        r1 = switchNightRunnableScheduled;
        if (r1 != 0) goto L_0x010c;
    L_0x0101:
        r1 = 2;
        goto L_0x010d;
    L_0x0103:
        r1 = switchDayRunnableScheduled;
        if (r1 != 0) goto L_0x010c;
    L_0x0107:
        goto L_0x010a;
    L_0x0108:
        if (r1 != 0) goto L_0x010c;
    L_0x010a:
        r1 = 1;
        goto L_0x010d;
    L_0x010c:
        r1 = 0;
    L_0x010d:
        if (r1 == 0) goto L_0x0115;
    L_0x010f:
        if (r1 != r2) goto L_0x0112;
    L_0x0111:
        r0 = 1;
    L_0x0112:
        applyDayNightThemeMaybe(r0);
    L_0x0115:
        if (r12 == 0) goto L_0x011b;
    L_0x0117:
        r0 = 0;
        lastThemeSwitchTime = r0;
    L_0x011b:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.checkAutoNightThemeConditions(boolean):void");
    }

    private static void applyDayNightThemeMaybe(boolean z) {
        if (previousTheme == null) {
            if (z) {
                if (currentTheme != currentNightTheme) {
                    lastThemeSwitchTime = SystemClock.elapsedRealtime();
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, currentNightTheme, Boolean.valueOf(true));
                }
            } else if (currentTheme != currentDayTheme) {
                lastThemeSwitchTime = SystemClock.elapsedRealtime();
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, currentDayTheme, Boolean.valueOf(true));
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
        themeInfo.removeObservers();
        otherThemes.remove(themeInfo);
        themesDict.remove(themeInfo.name);
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
        saveCurrentTheme(themeInfo, true, true, false);
        return themeInfo;
    }

    /* JADX WARNING: Removed duplicated region for block: B:109:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0295  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x02a1 A:{SYNTHETIC, Splitter:B:99:0x02a1} */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x028a A:{SYNTHETIC, Splitter:B:92:0x028a} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0295  */
    /* JADX WARNING: Removed duplicated region for block: B:109:? A:{SYNTHETIC, RETURN} */
    public static void saveCurrentTheme(org.telegram.ui.ActionBar.Theme.ThemeInfo r19, boolean r20, boolean r21, boolean r22) {
        /*
        r1 = r19;
        r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings();
        r2 = 0;
        r3 = "overrideThemeWallpaper";
        r3 = r0.getBoolean(r3, r2);
        r4 = 0;
        r5 = "selectedBackgroundSlug";
        r5 = r0.getString(r5, r4);
        r6 = 1000001; // 0xvar_ float:1.4013E-39 double:4.94066E-318;
        r8 = "selectedBackground2";
        r8 = r0.getLong(r8, r6);
        r10 = 0;
        r12 = "selectedPattern";
        r12 = r0.getLong(r12, r10);
        r14 = 3;
        r15 = 2;
        r16 = 1;
        if (r22 == 0) goto L_0x0111;
    L_0x002b:
        r17 = android.text.TextUtils.isEmpty(r5);
        if (r17 != 0) goto L_0x010f;
    L_0x0031:
        r17 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1));
        if (r17 != 0) goto L_0x0039;
    L_0x0035:
        r6 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1));
        if (r6 == 0) goto L_0x010f;
    L_0x0039:
        if (r3 != 0) goto L_0x0041;
    L_0x003b:
        r3 = hasWallpaperFromTheme();
        if (r3 != 0) goto L_0x010f;
    L_0x0041:
        r3 = "selectedBackgroundBlurred";
        r3 = r0.getBoolean(r3, r2);
        r6 = "selectedBackgroundMotion";
        r6 = r0.getBoolean(r6, r2);
        r7 = "selectedColor";
        r7 = r0.getInt(r7, r2);
        r10 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r11 = "selectedIntensity";
        r0 = r0.getFloat(r11, r10);
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        if (r3 == 0) goto L_0x0067;
    L_0x0062:
        r3 = "blur";
        r10.append(r3);
    L_0x0067:
        if (r6 == 0) goto L_0x0079;
    L_0x0069:
        r3 = r10.length();
        if (r3 <= 0) goto L_0x0074;
    L_0x006f:
        r3 = "+";
        r10.append(r3);
    L_0x0074:
        r3 = "motion";
        r10.append(r3);
    L_0x0079:
        r17 = -1;
        r3 = "https://attheme.org?slug=";
        r6 = (r8 > r17 ? 1 : (r8 == r17 ? 0 : -1));
        if (r6 == 0) goto L_0x0099;
    L_0x0081:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r3);
        r0.append(r5);
        r3 = "&id=";
        r0.append(r3);
        r0.append(r8);
        r0 = r0.toString();
        goto L_0x00f0;
    L_0x0099:
        r6 = new java.lang.Object[r14];
        r8 = r7 >> 16;
        r8 = (byte) r8;
        r8 = r8 & 255;
        r8 = java.lang.Integer.valueOf(r8);
        r6[r2] = r8;
        r8 = r7 >> 8;
        r8 = (byte) r8;
        r8 = r8 & 255;
        r8 = java.lang.Integer.valueOf(r8);
        r6[r16] = r8;
        r7 = r7 & 255;
        r7 = (byte) r7;
        r7 = java.lang.Byte.valueOf(r7);
        r6[r15] = r7;
        r7 = "%02x%02x%02x";
        r6 = java.lang.String.format(r7, r6);
        r6 = r6.toLowerCase();
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r7.append(r3);
        r7.append(r5);
        r3 = "&intensity=";
        r7.append(r3);
        r3 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r0 = r0 * r3;
        r0 = (int) r0;
        r7.append(r0);
        r0 = "&bg_color=";
        r7.append(r0);
        r7.append(r6);
        r0 = "&pattern=";
        r7.append(r0);
        r7.append(r12);
        r0 = r7.toString();
    L_0x00f0:
        r3 = r10.length();
        if (r3 <= 0) goto L_0x0113;
    L_0x00f6:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r0);
        r0 = "&mode=";
        r3.append(r0);
        r0 = r10.toString();
        r3.append(r0);
        r0 = r3.toString();
        goto L_0x0113;
    L_0x010f:
        r0 = r4;
        goto L_0x0113;
    L_0x0111:
        r0 = themedWallpaperLink;
    L_0x0113:
        if (r21 == 0) goto L_0x0118;
    L_0x0115:
        r3 = wallpaper;
        goto L_0x011a;
    L_0x0118:
        r3 = themedWallpaper;
    L_0x011a:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = currentColors;
        r6 = r6.entrySet();
        r6 = r6.iterator();
    L_0x0129:
        r7 = r6.hasNext();
        r8 = "\n";
        if (r7 == 0) goto L_0x0169;
    L_0x0131:
        r7 = r6.next();
        r7 = (java.util.Map.Entry) r7;
        r9 = r7.getKey();
        r9 = (java.lang.String) r9;
        if (r21 == 0) goto L_0x0156;
    L_0x013f:
        r10 = r3 instanceof android.graphics.drawable.BitmapDrawable;
        if (r10 != 0) goto L_0x0145;
    L_0x0143:
        if (r0 == 0) goto L_0x0156;
    L_0x0145:
        r10 = "chat_wallpaper";
        r10 = r10.equals(r9);
        if (r10 != 0) goto L_0x0129;
    L_0x014d:
        r10 = "chat_wallpaper_gradient_to";
        r10 = r10.equals(r9);
        if (r10 == 0) goto L_0x0156;
    L_0x0155:
        goto L_0x0129;
    L_0x0156:
        r5.append(r9);
        r9 = "=";
        r5.append(r9);
        r7 = r7.getValue();
        r5.append(r7);
        r5.append(r8);
        goto L_0x0129;
    L_0x0169:
        r6 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x0284 }
        r7 = r1.pathToFile;	 Catch:{ Exception -> 0x0284 }
        r6.<init>(r7);	 Catch:{ Exception -> 0x0284 }
        r4 = r5.length();	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        if (r4 != 0) goto L_0x0185;
    L_0x0176:
        r4 = r3 instanceof android.graphics.drawable.BitmapDrawable;	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        if (r4 != 0) goto L_0x0185;
    L_0x017a:
        r4 = android.text.TextUtils.isEmpty(r0);	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        if (r4 == 0) goto L_0x0185;
    L_0x0180:
        r4 = 32;
        r5.append(r4);	 Catch:{ Exception -> 0x027d, all -> 0x027a }
    L_0x0185:
        r4 = r5.toString();	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r4 = org.telegram.messenger.AndroidUtilities.getStringBytes(r4);	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r6.write(r4);	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r4 = android.text.TextUtils.isEmpty(r0);	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r5 = 87;
        if (r4 != 0) goto L_0x01ec;
    L_0x0198:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r2.<init>();	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r4 = "WLS=";
        r2.append(r4);	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r2.append(r0);	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r2.append(r8);	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r2 = r2.toString();	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r2 = org.telegram.messenger.AndroidUtilities.getStringBytes(r2);	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r6.write(r2);	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        if (r21 == 0) goto L_0x022d;
    L_0x01b5:
        r3 = (android.graphics.drawable.BitmapDrawable) r3;	 Catch:{ all -> 0x01e7 }
        r2 = r3.getBitmap();	 Catch:{ all -> 0x01e7 }
        r3 = new java.io.FileOutputStream;	 Catch:{ all -> 0x01e7 }
        r4 = new java.io.File;	 Catch:{ all -> 0x01e7 }
        r7 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();	 Catch:{ all -> 0x01e7 }
        r8 = new java.lang.StringBuilder;	 Catch:{ all -> 0x01e7 }
        r8.<init>();	 Catch:{ all -> 0x01e7 }
        r0 = org.telegram.messenger.Utilities.MD5(r0);	 Catch:{ all -> 0x01e7 }
        r8.append(r0);	 Catch:{ all -> 0x01e7 }
        r0 = ".wp";
        r8.append(r0);	 Catch:{ all -> 0x01e7 }
        r0 = r8.toString();	 Catch:{ all -> 0x01e7 }
        r4.<init>(r7, r0);	 Catch:{ all -> 0x01e7 }
        r3.<init>(r4);	 Catch:{ all -> 0x01e7 }
        r0 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ all -> 0x01e7 }
        r2.compress(r0, r5, r3);	 Catch:{ all -> 0x01e7 }
        r3.close();	 Catch:{ all -> 0x01e7 }
        goto L_0x022d;
    L_0x01e7:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        goto L_0x022d;
    L_0x01ec:
        r0 = r3 instanceof android.graphics.drawable.BitmapDrawable;	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        if (r0 == 0) goto L_0x022d;
    L_0x01f0:
        r0 = r3;
        r0 = (android.graphics.drawable.BitmapDrawable) r0;	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r0 = r0.getBitmap();	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        if (r0 == 0) goto L_0x0224;
    L_0x01f9:
        r4 = 4;
        r7 = new byte[r4];	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r7[r2] = r5;	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r8 = 80;
        r7[r16] = r8;	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r9 = 83;
        r7[r15] = r9;	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r9 = 10;
        r7[r14] = r9;	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r6.write(r7);	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r7 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r0.compress(r7, r5, r6);	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r0 = 5;
        r0 = new byte[r0];	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r0[r2] = r9;	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r0[r16] = r5;	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r0[r15] = r8;	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r2 = 69;
        r0[r14] = r2;	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r0[r4] = r9;	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r6.write(r0);	 Catch:{ Exception -> 0x027d, all -> 0x027a }
    L_0x0224:
        if (r20 == 0) goto L_0x022d;
    L_0x0226:
        if (r22 != 0) goto L_0x022d;
    L_0x0228:
        wallpaper = r3;	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        calcBackgroundColor(r3, r15);	 Catch:{ Exception -> 0x027d, all -> 0x027a }
    L_0x022d:
        if (r22 != 0) goto L_0x0276;
    L_0x022f:
        r0 = themesDict;	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r2 = r19.getKey();	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r0 = r0.get(r2);	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        if (r0 != 0) goto L_0x0254;
    L_0x023b:
        r0 = themes;	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r0.add(r1);	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r0 = themesDict;	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r2 = r19.getKey();	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r0.put(r2, r1);	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r0 = otherThemes;	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r0.add(r1);	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        saveOtherThemes(r16);	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        sortThemes();	 Catch:{ Exception -> 0x027d, all -> 0x027a }
    L_0x0254:
        currentTheme = r1;	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r0 = currentTheme;	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r2 = currentNightTheme;	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        if (r0 == r2) goto L_0x0260;
    L_0x025c:
        r0 = currentTheme;	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        currentDayTheme = r0;	 Catch:{ Exception -> 0x027d, all -> 0x027a }
    L_0x0260:
        r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings();	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r0 = r0.edit();	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r2 = "theme";
        r3 = currentDayTheme;	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r3 = r3.getKey();	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r0.putString(r2, r3);	 Catch:{ Exception -> 0x027d, all -> 0x027a }
        r0.commit();	 Catch:{ Exception -> 0x027d, all -> 0x027a }
    L_0x0276:
        r6.close();	 Catch:{ Exception -> 0x028e }
        goto L_0x0293;
    L_0x027a:
        r0 = move-exception;
        r1 = r0;
        goto L_0x029f;
    L_0x027d:
        r0 = move-exception;
        r4 = r6;
        goto L_0x0285;
    L_0x0280:
        r0 = move-exception;
        r1 = r0;
        r6 = r4;
        goto L_0x029f;
    L_0x0284:
        r0 = move-exception;
    L_0x0285:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0280 }
        if (r4 == 0) goto L_0x0293;
    L_0x028a:
        r4.close();	 Catch:{ Exception -> 0x028e }
        goto L_0x0293;
    L_0x028e:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
    L_0x0293:
        if (r20 == 0) goto L_0x029e;
    L_0x0295:
        r0 = r1.account;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r0.saveThemeToServer(r1);
    L_0x029e:
        return;
    L_0x029f:
        if (r6 == 0) goto L_0x02aa;
    L_0x02a1:
        r6.close();	 Catch:{ Exception -> 0x02a5 }
        goto L_0x02aa;
    L_0x02a5:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
    L_0x02aa:
        goto L_0x02ac;
    L_0x02ab:
        throw r1;
    L_0x02ac:
        goto L_0x02ab;
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
                if (themeInfo != null) {
                    TL_theme tL_theme = themeInfo.info;
                    if (!(tL_theme == null || tL_theme.document == null || !UserConfig.getInstance(themeInfo.account).isClientActivated())) {
                        loadingCurrentTheme++;
                        TL_account_getTheme tL_account_getTheme = new TL_account_getTheme();
                        tL_account_getTheme.document_id = themeInfo.info.document.id;
                        tL_account_getTheme.format = "android";
                        TL_inputTheme tL_inputTheme = new TL_inputTheme();
                        TL_theme tL_theme2 = themeInfo.info;
                        tL_inputTheme.access_hash = tL_theme2.access_hash;
                        tL_inputTheme.id = tL_theme2.id;
                        tL_account_getTheme.theme = tL_inputTheme;
                        ConnectionsManager.getInstance(themeInfo.account).sendRequest(tL_account_getTheme, new -$$Lambda$Theme$pF9AI0vS7PvPoaowEPmtk2Zehvo(themeInfo));
                    }
                }
                i++;
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x001b  */
    static /* synthetic */ void lambda$null$2(org.telegram.tgnet.TLObject r4, org.telegram.ui.ActionBar.Theme.ThemeInfo r5) {
        /*
        r0 = loadingCurrentTheme;
        r1 = 1;
        r0 = r0 - r1;
        loadingCurrentTheme = r0;
        r0 = r4 instanceof org.telegram.tgnet.TLRPC.TL_theme;
        if (r0 == 0) goto L_0x0016;
    L_0x000a:
        r4 = (org.telegram.tgnet.TLRPC.TL_theme) r4;
        r0 = r4.document;
        if (r0 == 0) goto L_0x0016;
    L_0x0010:
        r5.info = r4;
        r5.loadThemeDocument();
        goto L_0x0017;
    L_0x0016:
        r1 = 0;
    L_0x0017:
        r4 = loadingCurrentTheme;
        if (r4 != 0) goto L_0x0028;
    L_0x001b:
        r4 = java.lang.System.currentTimeMillis();
        r2 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r4 = r4 / r2;
        r5 = (int) r4;
        lastLoadingCurrentThemeTime = r5;
        saveOtherThemes(r1);
    L_0x0028:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.lambda$null$2(org.telegram.tgnet.TLObject, org.telegram.ui.ActionBar.Theme$ThemeInfo):void");
    }

    public static void loadRemoteThemes(int i, boolean z) {
        if (!loadingRemoteThemes) {
            if ((z || Math.abs((System.currentTimeMillis() / 1000) - ((long) lastLoadingThemesTime)) >= 3600) && UserConfig.getInstance(i).isClientActivated()) {
                loadingRemoteThemes = true;
                TL_account_getThemes tL_account_getThemes = new TL_account_getThemes();
                tL_account_getThemes.format = "android";
                tL_account_getThemes.hash = remoteThemesHash;
                ConnectionsManager.getInstance(i).sendRequest(tL_account_getThemes, new -$$Lambda$Theme$W8PxHsNGN-1ClSKhdG5WIm1ySCw(i));
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:49:0x010f A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0105  */
    static /* synthetic */ void lambda$null$4(org.telegram.tgnet.TLObject r11, int r12) {
        /*
        r0 = 0;
        loadingRemoteThemes = r0;
        r1 = r11 instanceof org.telegram.tgnet.TLRPC.TL_account_themes;
        if (r1 == 0) goto L_0x0125;
    L_0x0007:
        r11 = (org.telegram.tgnet.TLRPC.TL_account_themes) r11;
        r1 = r11.hash;
        remoteThemesHash = r1;
        r1 = java.lang.System.currentTimeMillis();
        r3 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r1 = r1 / r3;
        r2 = (int) r1;
        lastLoadingThemesTime = r2;
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2 = otherThemes;
        r2 = r2.size();
        r3 = 0;
    L_0x0023:
        if (r3 >= r2) goto L_0x003b;
    L_0x0025:
        r4 = otherThemes;
        r4 = r4.get(r3);
        r4 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r4;
        r5 = r4.info;
        if (r5 == 0) goto L_0x0038;
    L_0x0031:
        r5 = r4.account;
        if (r5 != r12) goto L_0x0038;
    L_0x0035:
        r1.add(r4);
    L_0x0038:
        r3 = r3 + 1;
        goto L_0x0023;
    L_0x003b:
        r2 = r11.themes;
        r2 = r2.size();
        r3 = 0;
        r4 = 0;
    L_0x0043:
        r5 = 1;
        if (r3 >= r2) goto L_0x00ba;
    L_0x0046:
        r6 = r11.themes;
        r6 = r6.get(r3);
        r6 = (org.telegram.tgnet.TLRPC.Theme) r6;
        r7 = r6 instanceof org.telegram.tgnet.TLRPC.TL_theme;
        if (r7 != 0) goto L_0x0053;
    L_0x0052:
        goto L_0x00b7;
    L_0x0053:
        r6 = (org.telegram.tgnet.TLRPC.TL_theme) r6;
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "remote";
        r7.append(r8);
        r8 = r6.id;
        r7.append(r8);
        r7 = r7.toString();
        r8 = themesDict;
        r8 = r8.get(r7);
        r8 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r8;
        if (r8 != 0) goto L_0x00a5;
    L_0x0072:
        r8 = new org.telegram.ui.ActionBar.Theme$ThemeInfo;
        r8.<init>();
        r8.account = r12;
        r4 = new java.io.File;
        r9 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10.append(r7);
        r7 = ".attheme";
        r10.append(r7);
        r7 = r10.toString();
        r4.<init>(r9, r7);
        r4 = r4.getAbsolutePath();
        r8.pathToFile = r4;
        r4 = themes;
        r4.add(r8);
        r4 = otherThemes;
        r4.add(r8);
        r4 = 1;
        goto L_0x00a8;
    L_0x00a5:
        r1.remove(r8);
    L_0x00a8:
        r5 = r6.title;
        r8.name = r5;
        r8.info = r6;
        r5 = themesDict;
        r6 = r8.getKey();
        r5.put(r6, r8);
    L_0x00b7:
        r3 = r3 + 1;
        goto L_0x0043;
    L_0x00ba:
        r11 = r1.size();
        r12 = 0;
    L_0x00bf:
        if (r12 >= r11) goto L_0x0112;
    L_0x00c1:
        r2 = r1.get(r12);
        r2 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r2;
        r2.removeObservers();
        r3 = otherThemes;
        r3.remove(r2);
        r3 = themesDict;
        r6 = r2.name;
        r3.remove(r6);
        r3 = themes;
        r3.remove(r2);
        r3 = new java.io.File;
        r6 = r2.pathToFile;
        r3.<init>(r6);
        r3.delete();
        r3 = currentDayTheme;
        if (r3 != r2) goto L_0x00ee;
    L_0x00e9:
        r3 = defaultTheme;
        currentDayTheme = r3;
        goto L_0x0100;
    L_0x00ee:
        r3 = currentNightTheme;
        if (r3 != r2) goto L_0x0100;
    L_0x00f2:
        r3 = themesDict;
        r6 = "Dark Blue";
        r3 = r3.get(r6);
        r3 = (org.telegram.ui.ActionBar.Theme.ThemeInfo) r3;
        currentNightTheme = r3;
        r3 = 1;
        goto L_0x0101;
    L_0x0100:
        r3 = 0;
    L_0x0101:
        r6 = currentTheme;
        if (r6 != r2) goto L_0x010f;
    L_0x0105:
        if (r3 == 0) goto L_0x010a;
    L_0x0107:
        r2 = currentNightTheme;
        goto L_0x010c;
    L_0x010a:
        r2 = currentDayTheme;
    L_0x010c:
        applyTheme(r2, r5, r0, r3);
    L_0x010f:
        r12 = r12 + 1;
        goto L_0x00bf;
    L_0x0112:
        saveOtherThemes(r5);
        sortThemes();
        if (r4 == 0) goto L_0x0125;
    L_0x011a:
        r11 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r12 = org.telegram.messenger.NotificationCenter.themeListUpdated;
        r0 = new java.lang.Object[r0];
        r11.postNotificationName(r12, r0);
    L_0x0125:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.lambda$null$4(org.telegram.tgnet.TLObject, int):void");
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

    public static void setThemeUploadInfo(ThemeInfo themeInfo, TL_theme tL_theme, boolean z) {
        if (tL_theme != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("remote");
            stringBuilder.append(tL_theme.id);
            String stringBuilder2 = stringBuilder.toString();
            if (themeInfo != null) {
                themesDict.remove(themeInfo.getKey());
            } else {
                themeInfo = (ThemeInfo) themesDict.get(stringBuilder2);
            }
            if (themeInfo != null) {
                themeInfo.info = tL_theme;
                themeInfo.name = tL_theme.title;
                File file = new File(themeInfo.pathToFile);
                File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append(stringBuilder2);
                stringBuilder3.append(".attheme");
                File file2 = new File(filesDirFixed, stringBuilder3.toString());
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
                saveOtherThemes(true);
            }
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

    private static int getPreviewColor(HashMap<String, Integer> hashMap, String str) {
        Integer num = (Integer) hashMap.get(str);
        if (num == null) {
            num = (Integer) defaultColors.get(str);
        }
        return num.intValue();
    }

    /* JADX WARNING: Removed duplicated region for block: B:61:0x0269 A:{SYNTHETIC, Splitter:B:61:0x0269} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x02db A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x02a1 A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x02f8 A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0315 A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x033d A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x035f A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x036d A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x03ae A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x03cf A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0286 A:{SYNTHETIC, Splitter:B:77:0x0286} */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x02a1 A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x02db A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x02f8 A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0315 A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x033d A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x035f A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x036d A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x03ae A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x03cf A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0286 A:{SYNTHETIC, Splitter:B:77:0x0286} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x02db A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x02a1 A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x02f8 A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0315 A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x033d A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x035f A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x036d A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x03ae A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x03cf A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0286 A:{SYNTHETIC, Splitter:B:77:0x0286} */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x02a1 A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x02db A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x02f8 A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0315 A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x033d A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x035f A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x036d A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x03ae A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x03cf A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0286 A:{SYNTHETIC, Splitter:B:77:0x0286} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x02db A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x02a1 A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x02f8 A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0315 A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x033d A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x035f A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x036d A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x03ae A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x03cf A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0286 A:{SYNTHETIC, Splitter:B:77:0x0286} */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x02a1 A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x02db A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x02f8 A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0315 A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x033d A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x035f A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x036d A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x03ae A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x03cf A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x02db A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x02a1 A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x02f8 A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0315 A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x033d A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x035f A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x036d A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x03ae A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x03cf A:{Catch:{ all -> 0x028d, all -> 0x0437 }} */
    public static java.lang.String createThemePreviewImage(org.telegram.ui.ActionBar.Theme.ThemeInfo r24) {
        /*
        r0 = r24;
        r1 = 1;
        r2 = 0;
        r3 = new java.lang.String[r1];	 Catch:{ all -> 0x0437 }
        r4 = new java.io.File;	 Catch:{ all -> 0x0437 }
        r5 = r0.pathToFile;	 Catch:{ all -> 0x0437 }
        r4.<init>(r5);	 Catch:{ all -> 0x0437 }
        r4 = getThemeFileValues(r4, r2, r3);	 Catch:{ all -> 0x0437 }
        r5 = "wallpaperFileOffset";
        r5 = r4.get(r5);	 Catch:{ all -> 0x0437 }
        r5 = (java.lang.Integer) r5;	 Catch:{ all -> 0x0437 }
        r6 = 560; // 0x230 float:7.85E-43 double:2.767E-321;
        r7 = 678; // 0x2a6 float:9.5E-43 double:3.35E-321;
        r8 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ all -> 0x0437 }
        r6 = org.telegram.messenger.Bitmaps.createBitmap(r6, r7, r8);	 Catch:{ all -> 0x0437 }
        r13 = new android.graphics.Canvas;	 Catch:{ all -> 0x0437 }
        r13.<init>(r6);	 Catch:{ all -> 0x0437 }
        r14 = new android.graphics.Paint;	 Catch:{ all -> 0x0437 }
        r14.<init>();	 Catch:{ all -> 0x0437 }
        r7 = "actionBarDefault";
        r7 = getPreviewColor(r4, r7);	 Catch:{ all -> 0x0437 }
        r8 = "actionBarDefaultIcon";
        r8 = getPreviewColor(r4, r8);	 Catch:{ all -> 0x0437 }
        r9 = "chat_messagePanelBackground";
        r15 = getPreviewColor(r4, r9);	 Catch:{ all -> 0x0437 }
        r9 = "chat_messagePanelIcons";
        r9 = getPreviewColor(r4, r9);	 Catch:{ all -> 0x0437 }
        r10 = "chat_inBubble";
        r10 = getPreviewColor(r4, r10);	 Catch:{ all -> 0x0437 }
        r11 = "chat_outBubble";
        r11 = getPreviewColor(r4, r11);	 Catch:{ all -> 0x0437 }
        r12 = "chat_wallpaper";
        r12 = r4.get(r12);	 Catch:{ all -> 0x0437 }
        r12 = (java.lang.Integer) r12;	 Catch:{ all -> 0x0437 }
        r2 = "chat_serviceBackground";
        r2 = r4.get(r2);	 Catch:{ all -> 0x0437 }
        r2 = (java.lang.Integer) r2;	 Catch:{ all -> 0x0437 }
        r1 = "chat_wallpaper_gradient_to";
        r1 = r4.get(r1);	 Catch:{ all -> 0x0437 }
        r1 = (java.lang.Integer) r1;	 Catch:{ all -> 0x0437 }
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0437 }
        r4 = r4.getResources();	 Catch:{ all -> 0x0437 }
        r17 = r15;
        r15 = NUM; // 0x7var_ float:1.7945657E38 double:1.052935766E-314;
        r4 = r4.getDrawable(r15);	 Catch:{ all -> 0x0437 }
        r4 = r4.mutate();	 Catch:{ all -> 0x0437 }
        setDrawableColor(r4, r8);	 Catch:{ all -> 0x0437 }
        r15 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0437 }
        r15 = r15.getResources();	 Catch:{ all -> 0x0437 }
        r18 = r4;
        r4 = NUM; // 0x7var_ float:1.7945661E38 double:1.052935767E-314;
        r4 = r15.getDrawable(r4);	 Catch:{ all -> 0x0437 }
        r4 = r4.mutate();	 Catch:{ all -> 0x0437 }
        setDrawableColor(r4, r8);	 Catch:{ all -> 0x0437 }
        r8 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0437 }
        r8 = r8.getResources();	 Catch:{ all -> 0x0437 }
        r15 = NUM; // 0x7var_b float:1.7945671E38 double:1.0529357693E-314;
        r8 = r8.getDrawable(r15);	 Catch:{ all -> 0x0437 }
        r15 = r8.mutate();	 Catch:{ all -> 0x0437 }
        setDrawableColor(r15, r9);	 Catch:{ all -> 0x0437 }
        r8 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0437 }
        r8 = r8.getResources();	 Catch:{ all -> 0x0437 }
        r19 = r15;
        r15 = NUM; // 0x7var_ float:1.7945663E38 double:1.0529357674E-314;
        r8 = r8.getDrawable(r15);	 Catch:{ all -> 0x0437 }
        r15 = r8.mutate();	 Catch:{ all -> 0x0437 }
        setDrawableColor(r15, r9);	 Catch:{ all -> 0x0437 }
        r8 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0437 }
        r8 = r8.getResources();	 Catch:{ all -> 0x0437 }
        r9 = NUM; // 0x7var_ float:1.7945665E38 double:1.052935768E-314;
        r8 = r8.getDrawable(r9);	 Catch:{ all -> 0x0437 }
        r9 = r8.mutate();	 Catch:{ all -> 0x0437 }
        setDrawableColor(r9, r10);	 Catch:{ all -> 0x0437 }
        r8 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0437 }
        r8 = r8.getResources();	 Catch:{ all -> 0x0437 }
        r10 = NUM; // 0x7var_ float:1.7945667E38 double:1.0529357683E-314;
        r8 = r8.getDrawable(r10);	 Catch:{ all -> 0x0437 }
        r10 = r8.mutate();	 Catch:{ all -> 0x0437 }
        setDrawableColor(r10, r11);	 Catch:{ all -> 0x0437 }
        r11 = new android.graphics.RectF;	 Catch:{ all -> 0x0437 }
        r11.<init>();	 Catch:{ all -> 0x0437 }
        r8 = 2;
        r20 = r9;
        r9 = 0;
        if (r12 == 0) goto L_0x0141;
    L_0x00f2:
        if (r1 != 0) goto L_0x00fe;
    L_0x00f4:
        r0 = new android.graphics.drawable.ColorDrawable;	 Catch:{ all -> 0x0437 }
        r1 = r12.intValue();	 Catch:{ all -> 0x0437 }
        r0.<init>(r1);	 Catch:{ all -> 0x0437 }
        goto L_0x0115;
    L_0x00fe:
        r0 = new org.telegram.ui.Components.BackgroundGradientDrawable;	 Catch:{ all -> 0x0437 }
        r3 = android.graphics.drawable.GradientDrawable.Orientation.BL_TR;	 Catch:{ all -> 0x0437 }
        r5 = new int[r8];	 Catch:{ all -> 0x0437 }
        r21 = r12.intValue();	 Catch:{ all -> 0x0437 }
        r5[r9] = r21;	 Catch:{ all -> 0x0437 }
        r1 = r1.intValue();	 Catch:{ all -> 0x0437 }
        r16 = 1;
        r5[r16] = r1;	 Catch:{ all -> 0x0437 }
        r0.<init>(r3, r5);	 Catch:{ all -> 0x0437 }
    L_0x0115:
        r1 = r6.getWidth();	 Catch:{ all -> 0x0437 }
        r3 = r6.getHeight();	 Catch:{ all -> 0x0437 }
        r5 = 120; // 0x78 float:1.68E-43 double:5.93E-322;
        r3 = r3 - r5;
        r0.setBounds(r9, r5, r1, r3);	 Catch:{ all -> 0x0437 }
        r0.draw(r13);	 Catch:{ all -> 0x0437 }
        if (r2 != 0) goto L_0x013b;
    L_0x0128:
        r0 = new android.graphics.drawable.ColorDrawable;	 Catch:{ all -> 0x0437 }
        r1 = r12.intValue();	 Catch:{ all -> 0x0437 }
        r0.<init>(r1);	 Catch:{ all -> 0x0437 }
        r0 = org.telegram.messenger.AndroidUtilities.calcDrawableColor(r0);	 Catch:{ all -> 0x0437 }
        r0 = r0[r9];	 Catch:{ all -> 0x0437 }
        r2 = java.lang.Integer.valueOf(r0);	 Catch:{ all -> 0x0437 }
    L_0x013b:
        r12 = r10;
        r1 = 1;
        r21 = 2;
        goto L_0x029f;
    L_0x0141:
        if (r5 == 0) goto L_0x0149;
    L_0x0143:
        r1 = r5.intValue();	 Catch:{ all -> 0x0437 }
        if (r1 >= 0) goto L_0x0151;
    L_0x0149:
        r1 = r3[r9];	 Catch:{ all -> 0x0437 }
        r1 = android.text.TextUtils.isEmpty(r1);	 Catch:{ all -> 0x0437 }
        if (r1 != 0) goto L_0x029b;
    L_0x0151:
        r1 = new android.graphics.BitmapFactory$Options;	 Catch:{ all -> 0x027b }
        r1.<init>();	 Catch:{ all -> 0x027b }
        r12 = 1;
        r1.inJustDecodeBounds = r12;	 Catch:{ all -> 0x027b }
        r12 = r3[r9];	 Catch:{ all -> 0x027b }
        r12 = android.text.TextUtils.isEmpty(r12);	 Catch:{ all -> 0x027b }
        if (r12 != 0) goto L_0x0193;
    L_0x0161:
        r0 = new java.io.File;	 Catch:{ all -> 0x018b }
        r12 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();	 Catch:{ all -> 0x018b }
        r8 = new java.lang.StringBuilder;	 Catch:{ all -> 0x018b }
        r8.<init>();	 Catch:{ all -> 0x018b }
        r3 = r3[r9];	 Catch:{ all -> 0x018b }
        r3 = org.telegram.messenger.Utilities.MD5(r3);	 Catch:{ all -> 0x018b }
        r8.append(r3);	 Catch:{ all -> 0x018b }
        r3 = ".wp";
        r8.append(r3);	 Catch:{ all -> 0x018b }
        r3 = r8.toString();	 Catch:{ all -> 0x018b }
        r0.<init>(r12, r3);	 Catch:{ all -> 0x018b }
        r3 = r0.getAbsolutePath();	 Catch:{ all -> 0x018b }
        android.graphics.BitmapFactory.decodeFile(r3, r1);	 Catch:{ all -> 0x018b }
        r12 = r10;
        r3 = 0;
        goto L_0x01ac;
    L_0x018b:
        r0 = move-exception;
        r12 = r10;
        r1 = 0;
        r3 = 0;
        r21 = 2;
        goto L_0x0281;
    L_0x0193:
        r3 = new java.io.FileInputStream;	 Catch:{ all -> 0x027b }
        r0 = r0.pathToFile;	 Catch:{ all -> 0x027b }
        r3.<init>(r0);	 Catch:{ all -> 0x027b }
        r0 = r3.getChannel();	 Catch:{ all -> 0x0275 }
        r8 = r5.intValue();	 Catch:{ all -> 0x0275 }
        r12 = r10;
        r9 = (long) r8;
        r0.position(r9);	 Catch:{ all -> 0x0273 }
        r8 = 0;
        android.graphics.BitmapFactory.decodeStream(r3, r8, r1);	 Catch:{ all -> 0x0273 }
        r0 = 0;
    L_0x01ac:
        r8 = r1.outWidth;	 Catch:{ all -> 0x0273 }
        if (r8 <= 0) goto L_0x0264;
    L_0x01b0:
        r8 = r1.outHeight;	 Catch:{ all -> 0x0273 }
        if (r8 <= 0) goto L_0x0264;
    L_0x01b4:
        r8 = r1.outWidth;	 Catch:{ all -> 0x0273 }
        r8 = (float) r8;	 Catch:{ all -> 0x0273 }
        r9 = NUM; // 0x440CLASSNAME float:560.0 double:5.64043681E-315;
        r8 = r8 / r9;
        r10 = r1.outHeight;	 Catch:{ all -> 0x0273 }
        r10 = (float) r10;	 Catch:{ all -> 0x0273 }
        r10 = r10 / r9;
        r8 = java.lang.Math.min(r8, r10);	 Catch:{ all -> 0x0273 }
        r10 = 1;
        r1.inSampleSize = r10;	 Catch:{ all -> 0x0273 }
        r10 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r10 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r10 <= 0) goto L_0x01db;
    L_0x01cb:
        r10 = r1.inSampleSize;	 Catch:{ all -> 0x0273 }
        r21 = 2;
        r10 = r10 * 2;
        r1.inSampleSize = r10;	 Catch:{ all -> 0x0262 }
        r10 = r1.inSampleSize;	 Catch:{ all -> 0x0262 }
        r10 = (float) r10;	 Catch:{ all -> 0x0262 }
        r10 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1));
        if (r10 < 0) goto L_0x01cb;
    L_0x01da:
        goto L_0x01dd;
    L_0x01db:
        r21 = 2;
    L_0x01dd:
        r8 = 0;
        r1.inJustDecodeBounds = r8;	 Catch:{ all -> 0x0262 }
        if (r0 == 0) goto L_0x01eb;
    L_0x01e2:
        r0 = r0.getAbsolutePath();	 Catch:{ all -> 0x0262 }
        r0 = android.graphics.BitmapFactory.decodeFile(r0, r1);	 Catch:{ all -> 0x0262 }
        goto L_0x01fc;
    L_0x01eb:
        r0 = r3.getChannel();	 Catch:{ all -> 0x0262 }
        r5 = r5.intValue();	 Catch:{ all -> 0x0262 }
        r9 = (long) r5;	 Catch:{ all -> 0x0262 }
        r0.position(r9);	 Catch:{ all -> 0x0262 }
        r5 = 0;
        r0 = android.graphics.BitmapFactory.decodeStream(r3, r5, r1);	 Catch:{ all -> 0x0262 }
    L_0x01fc:
        if (r0 == 0) goto L_0x0266;
    L_0x01fe:
        r1 = new android.graphics.Paint;	 Catch:{ all -> 0x0262 }
        r1.<init>();	 Catch:{ all -> 0x0262 }
        r5 = 1;
        r1.setFilterBitmap(r5);	 Catch:{ all -> 0x0262 }
        r8 = r0.getWidth();	 Catch:{ all -> 0x0262 }
        r8 = (float) r8;	 Catch:{ all -> 0x0262 }
        r9 = NUM; // 0x440CLASSNAME float:560.0 double:5.64043681E-315;
        r8 = r8 / r9;
        r10 = r0.getHeight();	 Catch:{ all -> 0x0262 }
        r10 = (float) r10;	 Catch:{ all -> 0x0262 }
        r10 = r10 / r9;
        r8 = java.lang.Math.min(r8, r10);	 Catch:{ all -> 0x0262 }
        r9 = r0.getWidth();	 Catch:{ all -> 0x0262 }
        r9 = (float) r9;	 Catch:{ all -> 0x0262 }
        r9 = r9 / r8;
        r10 = r0.getHeight();	 Catch:{ all -> 0x0262 }
        r10 = (float) r10;	 Catch:{ all -> 0x0262 }
        r10 = r10 / r8;
        r8 = 0;
        r11.set(r8, r8, r9, r10);	 Catch:{ all -> 0x0262 }
        r8 = r6.getWidth();	 Catch:{ all -> 0x0262 }
        r8 = (float) r8;	 Catch:{ all -> 0x0262 }
        r9 = r11.width();	 Catch:{ all -> 0x0262 }
        r8 = r8 - r9;
        r9 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r8 = r8 / r9;
        r10 = r6.getHeight();	 Catch:{ all -> 0x0262 }
        r10 = (float) r10;	 Catch:{ all -> 0x0262 }
        r16 = r11.height();	 Catch:{ all -> 0x0262 }
        r10 = r10 - r16;
        r10 = r10 / r9;
        r11.offset(r8, r10);	 Catch:{ all -> 0x0262 }
        r8 = 0;
        r13.drawBitmap(r0, r8, r11, r1);	 Catch:{ all -> 0x0262 }
        if (r2 != 0) goto L_0x0260;
    L_0x024b:
        r1 = new android.graphics.drawable.BitmapDrawable;	 Catch:{ all -> 0x025d }
        r1.<init>(r0);	 Catch:{ all -> 0x025d }
        r0 = org.telegram.messenger.AndroidUtilities.calcDrawableColor(r1);	 Catch:{ all -> 0x025d }
        r1 = 0;
        r0 = r0[r1];	 Catch:{ all -> 0x025d }
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ all -> 0x025d }
        r2 = r0;
        goto L_0x0260;
    L_0x025d:
        r0 = move-exception;
        r1 = 1;
        goto L_0x0281;
    L_0x0260:
        r1 = 1;
        goto L_0x0267;
    L_0x0262:
        r0 = move-exception;
        goto L_0x0279;
    L_0x0264:
        r21 = 2;
    L_0x0266:
        r1 = 0;
    L_0x0267:
        if (r3 == 0) goto L_0x029f;
    L_0x0269:
        r3.close();	 Catch:{ Exception -> 0x026d }
        goto L_0x029f;
    L_0x026d:
        r0 = move-exception;
        r3 = r0;
    L_0x026f:
        org.telegram.messenger.FileLog.e(r3);	 Catch:{ all -> 0x0437 }
        goto L_0x029f;
    L_0x0273:
        r0 = move-exception;
        goto L_0x0277;
    L_0x0275:
        r0 = move-exception;
        r12 = r10;
    L_0x0277:
        r21 = 2;
    L_0x0279:
        r1 = 0;
        goto L_0x0281;
    L_0x027b:
        r0 = move-exception;
        r12 = r10;
        r21 = 2;
        r1 = 0;
        r3 = 0;
    L_0x0281:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x028d }
        if (r3 == 0) goto L_0x029f;
    L_0x0286:
        r3.close();	 Catch:{ Exception -> 0x028a }
        goto L_0x029f;
    L_0x028a:
        r0 = move-exception;
        r3 = r0;
        goto L_0x026f;
    L_0x028d:
        r0 = move-exception;
        r1 = r0;
        if (r3 == 0) goto L_0x029a;
    L_0x0291:
        r3.close();	 Catch:{ Exception -> 0x0295 }
        goto L_0x029a;
    L_0x0295:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ all -> 0x0437 }
    L_0x029a:
        throw r1;	 Catch:{ all -> 0x0437 }
    L_0x029b:
        r12 = r10;
        r21 = 2;
        r1 = 0;
    L_0x029f:
        if (r1 != 0) goto L_0x02db;
    L_0x02a1:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0437 }
        r0 = r0.getResources();	 Catch:{ all -> 0x0437 }
        r1 = NUM; // 0x7var_ float:1.7944718E38 double:1.052935537E-314;
        r0 = r0.getDrawable(r1);	 Catch:{ all -> 0x0437 }
        r0 = r0.mutate();	 Catch:{ all -> 0x0437 }
        r0 = (android.graphics.drawable.BitmapDrawable) r0;	 Catch:{ all -> 0x0437 }
        if (r2 != 0) goto L_0x02c1;
    L_0x02b6:
        r1 = org.telegram.messenger.AndroidUtilities.calcDrawableColor(r0);	 Catch:{ all -> 0x0437 }
        r2 = 0;
        r1 = r1[r2];	 Catch:{ all -> 0x0437 }
        r2 = java.lang.Integer.valueOf(r1);	 Catch:{ all -> 0x0437 }
    L_0x02c1:
        r1 = android.graphics.Shader.TileMode.REPEAT;	 Catch:{ all -> 0x0437 }
        r3 = android.graphics.Shader.TileMode.REPEAT;	 Catch:{ all -> 0x0437 }
        r0.setTileModeXY(r1, r3);	 Catch:{ all -> 0x0437 }
        r1 = r6.getWidth();	 Catch:{ all -> 0x0437 }
        r3 = r6.getHeight();	 Catch:{ all -> 0x0437 }
        r5 = 120; // 0x78 float:1.68E-43 double:5.93E-322;
        r3 = r3 - r5;
        r8 = 0;
        r0.setBounds(r8, r5, r1, r3);	 Catch:{ all -> 0x0437 }
        r0.draw(r13);	 Catch:{ all -> 0x0437 }
        goto L_0x02dd;
    L_0x02db:
        r5 = 120; // 0x78 float:1.68E-43 double:5.93E-322;
    L_0x02dd:
        r14.setColor(r7);	 Catch:{ all -> 0x0437 }
        r8 = 0;
        r9 = 0;
        r0 = r6.getWidth();	 Catch:{ all -> 0x0437 }
        r10 = (float) r0;	 Catch:{ all -> 0x0437 }
        r0 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
        r7 = r13;
        r1 = 2;
        r3 = r20;
        r22 = r11;
        r11 = r0;
        r23 = r12;
        r12 = r14;
        r7.drawRect(r8, r9, r10, r11, r12);	 Catch:{ all -> 0x0437 }
        if (r18 == 0) goto L_0x0313;
    L_0x02f8:
        r0 = 13;
        r7 = r18.getIntrinsicHeight();	 Catch:{ all -> 0x0437 }
        r9 = 120 - r7;
        r9 = r9 / r1;
        r7 = r18.getIntrinsicWidth();	 Catch:{ all -> 0x0437 }
        r7 = r7 + r0;
        r8 = r18.getIntrinsicHeight();	 Catch:{ all -> 0x0437 }
        r8 = r8 + r9;
        r10 = r18;
        r10.setBounds(r0, r9, r7, r8);	 Catch:{ all -> 0x0437 }
        r10.draw(r13);	 Catch:{ all -> 0x0437 }
    L_0x0313:
        if (r4 == 0) goto L_0x0337;
    L_0x0315:
        r0 = r6.getWidth();	 Catch:{ all -> 0x0437 }
        r7 = r4.getIntrinsicWidth();	 Catch:{ all -> 0x0437 }
        r0 = r0 - r7;
        r0 = r0 + -10;
        r7 = r4.getIntrinsicHeight();	 Catch:{ all -> 0x0437 }
        r9 = 120 - r7;
        r9 = r9 / r1;
        r7 = r4.getIntrinsicWidth();	 Catch:{ all -> 0x0437 }
        r7 = r7 + r0;
        r8 = r4.getIntrinsicHeight();	 Catch:{ all -> 0x0437 }
        r8 = r8 + r9;
        r4.setBounds(r0, r9, r7, r8);	 Catch:{ all -> 0x0437 }
        r4.draw(r13);	 Catch:{ all -> 0x0437 }
    L_0x0337:
        r0 = 20;
        r4 = r23;
        if (r4 == 0) goto L_0x035d;
    L_0x033d:
        r7 = 216; // 0xd8 float:3.03E-43 double:1.067E-321;
        r8 = r6.getWidth();	 Catch:{ all -> 0x0437 }
        r8 = r8 - r0;
        r9 = 308; // 0x134 float:4.32E-43 double:1.52E-321;
        r10 = 161; // 0xa1 float:2.26E-43 double:7.95E-322;
        r4.setBounds(r10, r7, r8, r9);	 Catch:{ all -> 0x0437 }
        r4.draw(r13);	 Catch:{ all -> 0x0437 }
        r7 = 430; // 0x1ae float:6.03E-43 double:2.124E-321;
        r8 = r6.getWidth();	 Catch:{ all -> 0x0437 }
        r8 = r8 - r0;
        r9 = 522; // 0x20a float:7.31E-43 double:2.58E-321;
        r4.setBounds(r10, r7, r8, r9);	 Catch:{ all -> 0x0437 }
        r4.draw(r13);	 Catch:{ all -> 0x0437 }
    L_0x035d:
        if (r3 == 0) goto L_0x036b;
    L_0x035f:
        r4 = 323; // 0x143 float:4.53E-43 double:1.596E-321;
        r7 = 399; // 0x18f float:5.59E-43 double:1.97E-321;
        r8 = 415; // 0x19f float:5.82E-43 double:2.05E-321;
        r3.setBounds(r0, r4, r7, r8);	 Catch:{ all -> 0x0437 }
        r3.draw(r13);	 Catch:{ all -> 0x0437 }
    L_0x036b:
        if (r2 == 0) goto L_0x038f;
    L_0x036d:
        r0 = r6.getWidth();	 Catch:{ all -> 0x0437 }
        r0 = r0 + -126;
        r0 = r0 / r1;
        r3 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
        r4 = (float) r0;	 Catch:{ all -> 0x0437 }
        r3 = (float) r3;	 Catch:{ all -> 0x0437 }
        r0 = r0 + 126;
        r0 = (float) r0;	 Catch:{ all -> 0x0437 }
        r7 = 192; // 0xc0 float:2.69E-43 double:9.5E-322;
        r7 = (float) r7;	 Catch:{ all -> 0x0437 }
        r8 = r22;
        r8.set(r4, r3, r0, r7);	 Catch:{ all -> 0x0437 }
        r0 = r2.intValue();	 Catch:{ all -> 0x0437 }
        r14.setColor(r0);	 Catch:{ all -> 0x0437 }
        r0 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r13.drawRoundRect(r8, r0, r0, r14);	 Catch:{ all -> 0x0437 }
    L_0x038f:
        r2 = r17;
        r14.setColor(r2);	 Catch:{ all -> 0x0437 }
        r8 = 0;
        r0 = r6.getHeight();	 Catch:{ all -> 0x0437 }
        r0 = r0 - r5;
        r9 = (float) r0;	 Catch:{ all -> 0x0437 }
        r0 = r6.getWidth();	 Catch:{ all -> 0x0437 }
        r10 = (float) r0;	 Catch:{ all -> 0x0437 }
        r0 = r6.getHeight();	 Catch:{ all -> 0x0437 }
        r11 = (float) r0;	 Catch:{ all -> 0x0437 }
        r7 = r13;
        r12 = r14;
        r7.drawRect(r8, r9, r10, r11, r12);	 Catch:{ all -> 0x0437 }
        r0 = 22;
        if (r19 == 0) goto L_0x03cd;
    L_0x03ae:
        r2 = r6.getHeight();	 Catch:{ all -> 0x0437 }
        r2 = r2 - r5;
        r3 = r19.getIntrinsicHeight();	 Catch:{ all -> 0x0437 }
        r9 = 120 - r3;
        r9 = r9 / r1;
        r2 = r2 + r9;
        r3 = r19.getIntrinsicWidth();	 Catch:{ all -> 0x0437 }
        r3 = r3 + r0;
        r4 = r19.getIntrinsicHeight();	 Catch:{ all -> 0x0437 }
        r4 = r4 + r2;
        r7 = r19;
        r7.setBounds(r0, r2, r3, r4);	 Catch:{ all -> 0x0437 }
        r7.draw(r13);	 Catch:{ all -> 0x0437 }
    L_0x03cd:
        if (r15 == 0) goto L_0x03f6;
    L_0x03cf:
        r2 = r6.getWidth();	 Catch:{ all -> 0x0437 }
        r3 = r15.getIntrinsicWidth();	 Catch:{ all -> 0x0437 }
        r2 = r2 - r3;
        r2 = r2 - r0;
        r0 = r6.getHeight();	 Catch:{ all -> 0x0437 }
        r0 = r0 - r5;
        r3 = r15.getIntrinsicHeight();	 Catch:{ all -> 0x0437 }
        r9 = 120 - r3;
        r9 = r9 / r1;
        r0 = r0 + r9;
        r1 = r15.getIntrinsicWidth();	 Catch:{ all -> 0x0437 }
        r1 = r1 + r2;
        r3 = r15.getIntrinsicHeight();	 Catch:{ all -> 0x0437 }
        r3 = r3 + r0;
        r15.setBounds(r2, r0, r1, r3);	 Catch:{ all -> 0x0437 }
        r15.draw(r13);	 Catch:{ all -> 0x0437 }
    L_0x03f6:
        r1 = 0;
        r13.setBitmap(r1);	 Catch:{ all -> 0x0437 }
        r0 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0437 }
        r0.<init>();	 Catch:{ all -> 0x0437 }
        r1 = "-2147483648_";
        r0.append(r1);	 Catch:{ all -> 0x0437 }
        r1 = org.telegram.messenger.SharedConfig.getLastLocalId();	 Catch:{ all -> 0x0437 }
        r0.append(r1);	 Catch:{ all -> 0x0437 }
        r1 = ".jpg";
        r0.append(r1);	 Catch:{ all -> 0x0437 }
        r0 = r0.toString();	 Catch:{ all -> 0x0437 }
        r1 = new java.io.File;	 Catch:{ all -> 0x0437 }
        r2 = 4;
        r2 = org.telegram.messenger.FileLoader.getDirectory(r2);	 Catch:{ all -> 0x0437 }
        r1.<init>(r2, r0);	 Catch:{ all -> 0x0437 }
        r0 = new java.io.FileOutputStream;	 Catch:{ all -> 0x0432 }
        r0.<init>(r1);	 Catch:{ all -> 0x0432 }
        r2 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ all -> 0x0432 }
        r3 = 80;
        r6.compress(r2, r3, r0);	 Catch:{ all -> 0x0432 }
        org.telegram.messenger.SharedConfig.saveConfig();	 Catch:{ all -> 0x0432 }
        r0 = r1.getAbsolutePath();	 Catch:{ all -> 0x0432 }
        return r0;
    L_0x0432:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0437 }
        goto L_0x043b;
    L_0x0437:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x043b:
        r1 = 0;
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.createThemePreviewImage(org.telegram.ui.ActionBar.Theme$ThemeInfo):java.lang.String");
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:34:0x0081 */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00c9 A:{SYNTHETIC, Splitter:B:55:0x00c9} */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Can't wrap try/catch for region: R(4:32|33|34|35) */
    /* JADX WARNING: Missing block: B:49:0x00c0, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:56:?, code skipped:
            r5.close();
     */
    private static java.util.HashMap<java.lang.String, java.lang.Integer> getThemeFileValues(java.io.File r16, java.lang.String r17, java.lang.String[] r18) {
        /*
        r0 = r18;
        r1 = new java.util.HashMap;
        r1.<init>();
        r2 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r3 = 0;
        r2 = new byte[r2];	 Catch:{ all -> 0x00c2 }
        if (r17 == 0) goto L_0x0013;
    L_0x000e:
        r4 = getAssetFile(r17);	 Catch:{ all -> 0x00c2 }
        goto L_0x0015;
    L_0x0013:
        r4 = r16;
    L_0x0015:
        r5 = new java.io.FileInputStream;	 Catch:{ all -> 0x00c2 }
        r5.<init>(r4);	 Catch:{ all -> 0x00c2 }
        r3 = -1;
        r4 = 0;
        r6 = 0;
        r7 = -1;
        r8 = 0;
    L_0x001f:
        r9 = r5.read(r2);	 Catch:{ all -> 0x00c0 }
        if (r9 == r3) goto L_0x00b3;
    L_0x0025:
        r12 = r6;
        r10 = 0;
        r11 = 0;
    L_0x0028:
        r13 = 1;
        if (r10 >= r9) goto L_0x00a0;
    L_0x002b:
        r14 = r2[r10];	 Catch:{ all -> 0x00c0 }
        r15 = 10;
        if (r14 != r15) goto L_0x009b;
    L_0x0031:
        r14 = r10 - r11;
        r14 = r14 + r13;
        r15 = new java.lang.String;	 Catch:{ all -> 0x00c0 }
        r13 = r14 + -1;
        r15.<init>(r2, r11, r13);	 Catch:{ all -> 0x00c0 }
        r13 = "WLS=";
        r13 = r15.startsWith(r13);	 Catch:{ all -> 0x00c0 }
        if (r13 == 0) goto L_0x0050;
    L_0x0043:
        if (r0 == 0) goto L_0x0099;
    L_0x0045:
        r13 = r0.length;	 Catch:{ all -> 0x00c0 }
        if (r13 <= 0) goto L_0x0099;
    L_0x0048:
        r13 = 4;
        r13 = r15.substring(r13);	 Catch:{ all -> 0x00c0 }
        r0[r4] = r13;	 Catch:{ all -> 0x00c0 }
        goto L_0x0099;
    L_0x0050:
        r13 = "WPS";
        r13 = r15.startsWith(r13);	 Catch:{ all -> 0x00c0 }
        if (r13 == 0) goto L_0x005c;
    L_0x0058:
        r14 = r14 + r12;
        r7 = r14;
        r8 = 1;
        goto L_0x00a0;
    L_0x005c:
        r13 = 61;
        r13 = r15.indexOf(r13);	 Catch:{ all -> 0x00c0 }
        if (r13 == r3) goto L_0x0099;
    L_0x0064:
        r3 = r15.substring(r4, r13);	 Catch:{ all -> 0x00c0 }
        r13 = r13 + 1;
        r13 = r15.substring(r13);	 Catch:{ all -> 0x00c0 }
        r15 = r13.length();	 Catch:{ all -> 0x00c0 }
        if (r15 <= 0) goto L_0x008a;
    L_0x0074:
        r15 = r13.charAt(r4);	 Catch:{ all -> 0x00c0 }
        r4 = 35;
        if (r15 != r4) goto L_0x008a;
    L_0x007c:
        r4 = android.graphics.Color.parseColor(r13);	 Catch:{ Exception -> 0x0081 }
        goto L_0x0092;
    L_0x0081:
        r4 = org.telegram.messenger.Utilities.parseInt(r13);	 Catch:{ all -> 0x00c0 }
        r4 = r4.intValue();	 Catch:{ all -> 0x00c0 }
        goto L_0x0092;
    L_0x008a:
        r4 = org.telegram.messenger.Utilities.parseInt(r13);	 Catch:{ all -> 0x00c0 }
        r4 = r4.intValue();	 Catch:{ all -> 0x00c0 }
    L_0x0092:
        r4 = java.lang.Integer.valueOf(r4);	 Catch:{ all -> 0x00c0 }
        r1.put(r3, r4);	 Catch:{ all -> 0x00c0 }
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
        r3 = r5.getChannel();	 Catch:{ all -> 0x00c0 }
        r9 = (long) r12;	 Catch:{ all -> 0x00c0 }
        r3.position(r9);	 Catch:{ all -> 0x00c0 }
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
        r2 = java.lang.Integer.valueOf(r7);	 Catch:{ all -> 0x00c0 }
        r1.put(r0, r2);	 Catch:{ all -> 0x00c0 }
        r5.close();	 Catch:{ Exception -> 0x00cd }
        goto L_0x00d2;
    L_0x00c0:
        r0 = move-exception;
        goto L_0x00c4;
    L_0x00c2:
        r0 = move-exception;
        r5 = r3;
    L_0x00c4:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x00d3 }
        if (r5 == 0) goto L_0x00d2;
    L_0x00c9:
        r5.close();	 Catch:{ Exception -> 0x00cd }
        goto L_0x00d2;
    L_0x00cd:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
    L_0x00d2:
        return r1;
    L_0x00d3:
        r0 = move-exception;
        r1 = r0;
        if (r5 == 0) goto L_0x00e0;
    L_0x00d7:
        r5.close();	 Catch:{ Exception -> 0x00db }
        goto L_0x00e0;
    L_0x00db:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
    L_0x00e0:
        goto L_0x00e2;
    L_0x00e1:
        throw r1;
    L_0x00e2:
        goto L_0x00e1;
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
            dialogs_archiveAvatarDrawable = new RLottieDrawable(NUM, "chats_archiveavatar", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f), false);
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
            Resources resources = context.getResources();
            dialogs_namePaint = new TextPaint(1);
            String str = "fonts/rmedium.ttf";
            dialogs_namePaint.setTypeface(AndroidUtilities.getTypeface(str));
            dialogs_nameEncryptedPaint = new TextPaint(1);
            dialogs_nameEncryptedPaint.setTypeface(AndroidUtilities.getTypeface(str));
            dialogs_searchNamePaint = new TextPaint(1);
            dialogs_searchNamePaint.setTypeface(AndroidUtilities.getTypeface(str));
            dialogs_searchNameEncryptedPaint = new TextPaint(1);
            dialogs_searchNameEncryptedPaint.setTypeface(AndroidUtilities.getTypeface(str));
            dialogs_messagePaint = new TextPaint(1);
            dialogs_messageNamePaint = new TextPaint(1);
            dialogs_messageNamePaint.setTypeface(AndroidUtilities.getTypeface(str));
            dialogs_messagePrintingPaint = new TextPaint(1);
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
        TextPaint textPaint = dialogs_namePaint;
        if (textPaint != null) {
            String str = "chats_name";
            textPaint.setColor(getColor(str));
            String str2 = "chats_secretName";
            dialogs_nameEncryptedPaint.setColor(getColor(str2));
            dialogs_searchNamePaint.setColor(getColor(str));
            dialogs_searchNameEncryptedPaint.setColor(getColor(str2));
            textPaint = dialogs_messagePaint;
            int color = getColor("chats_message");
            textPaint.linkColor = color;
            textPaint.setColor(color);
            textPaint = dialogs_messageNamePaint;
            color = getColor("chats_nameMessage_threeLines");
            textPaint.linkColor = color;
            textPaint.setColor(color);
            dialogs_tabletSeletedPaint.setColor(getColor("chats_tabletSelectedOverlay"));
            dialogs_pinnedPaint.setColor(getColor("chats_pinnedOverlay"));
            dialogs_timePaint.setColor(getColor("chats_date"));
            dialogs_countTextPaint.setColor(getColor("chats_unreadCounterText"));
            dialogs_archiveTextPaint.setColor(getColor("chats_archiveText"));
            dialogs_messagePrintingPaint.setColor(getColor("chats_actionMessage"));
            dialogs_countPaint.setColor(getColor("chats_unreadCounter"));
            dialogs_countGrayPaint.setColor(getColor("chats_unreadCounterMuted"));
            dialogs_errorPaint.setColor(getColor("chats_sentError"));
            dialogs_onlinePaint.setColor(getColor("windowBackgroundWhiteBlueText3"));
            dialogs_offlinePaint.setColor(getColor("windowBackgroundWhiteGrayText3"));
            setDrawableColorByKey(dialogs_lockDrawable, "chats_secretIcon");
            setDrawableColorByKey(dialogs_checkDrawable, "chats_sentCheck");
            str = "chats_sentReadCheck";
            setDrawableColorByKey(dialogs_checkReadDrawable, str);
            setDrawableColorByKey(dialogs_halfCheckDrawable, str);
            setDrawableColorByKey(dialogs_clockDrawable, "chats_sentClock");
            setDrawableColorByKey(dialogs_errorDrawable, "chats_sentErrorIcon");
            str = "chats_nameIcon";
            setDrawableColorByKey(dialogs_groupDrawable, str);
            setDrawableColorByKey(dialogs_broadcastDrawable, str);
            setDrawableColorByKey(dialogs_botDrawable, str);
            str = "chats_pinnedIcon";
            setDrawableColorByKey(dialogs_pinnedDrawable, str);
            setDrawableColorByKey(dialogs_reorderDrawable, str);
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

    /* JADX WARNING: Missing exception handler attribute for start block: B:24:0x0a6d */
    /* JADX WARNING: Can't wrap try/catch for region: R(9:12|13|(4:15|(1:17)(1:18)|19|20)|37|21|22|23|24|25) */
    public static void createChatResources(android.content.Context r14, boolean r15) {
        /*
        r0 = sync;
        monitor-enter(r0);
        r1 = chat_msgTextPaint;	 Catch:{ all -> 0x0CLASSNAME }
        r2 = 1;
        if (r1 != 0) goto L_0x003d;
    L_0x0008:
        r1 = new android.text.TextPaint;	 Catch:{ all -> 0x0CLASSNAME }
        r1.<init>(r2);	 Catch:{ all -> 0x0CLASSNAME }
        chat_msgTextPaint = r1;	 Catch:{ all -> 0x0CLASSNAME }
        r1 = new android.text.TextPaint;	 Catch:{ all -> 0x0CLASSNAME }
        r1.<init>(r2);	 Catch:{ all -> 0x0CLASSNAME }
        chat_msgGameTextPaint = r1;	 Catch:{ all -> 0x0CLASSNAME }
        r1 = new android.text.TextPaint;	 Catch:{ all -> 0x0CLASSNAME }
        r1.<init>(r2);	 Catch:{ all -> 0x0CLASSNAME }
        chat_msgTextPaintOneEmoji = r1;	 Catch:{ all -> 0x0CLASSNAME }
        r1 = new android.text.TextPaint;	 Catch:{ all -> 0x0CLASSNAME }
        r1.<init>(r2);	 Catch:{ all -> 0x0CLASSNAME }
        chat_msgTextPaintTwoEmoji = r1;	 Catch:{ all -> 0x0CLASSNAME }
        r1 = new android.text.TextPaint;	 Catch:{ all -> 0x0CLASSNAME }
        r1.<init>(r2);	 Catch:{ all -> 0x0CLASSNAME }
        chat_msgTextPaintThreeEmoji = r1;	 Catch:{ all -> 0x0CLASSNAME }
        r1 = new android.text.TextPaint;	 Catch:{ all -> 0x0CLASSNAME }
        r1.<init>(r2);	 Catch:{ all -> 0x0CLASSNAME }
        chat_msgBotButtonPaint = r1;	 Catch:{ all -> 0x0CLASSNAME }
        r1 = chat_msgBotButtonPaint;	 Catch:{ all -> 0x0CLASSNAME }
        r3 = "fonts/rmedium.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);	 Catch:{ all -> 0x0CLASSNAME }
        r1.setTypeface(r3);	 Catch:{ all -> 0x0CLASSNAME }
    L_0x003d:
        monitor-exit(r0);	 Catch:{ all -> 0x0CLASSNAME }
        r0 = 2;
        if (r15 != 0) goto L_0x0a77;
    L_0x0041:
        r1 = chat_msgInDrawable;
        if (r1 != 0) goto L_0x0a77;
    L_0x0045:
        r1 = new android.text.TextPaint;
        r1.<init>(r2);
        chat_infoPaint = r1;
        r1 = new android.text.TextPaint;
        r1.<init>(r2);
        chat_docNamePaint = r1;
        r1 = chat_docNamePaint;
        r3 = "fonts/rmedium.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r1.setTypeface(r3);
        r1 = new android.graphics.Paint;
        r1.<init>(r2);
        chat_docBackPaint = r1;
        r1 = new android.graphics.Paint;
        r1.<init>(r2);
        chat_deleteProgressPaint = r1;
        r1 = new android.graphics.Paint;
        r1.<init>(r2);
        chat_botProgressPaint = r1;
        r1 = chat_botProgressPaint;
        r3 = android.graphics.Paint.Cap.ROUND;
        r1.setStrokeCap(r3);
        r1 = chat_botProgressPaint;
        r3 = android.graphics.Paint.Style.STROKE;
        r1.setStyle(r3);
        r1 = new android.text.TextPaint;
        r1.<init>(r2);
        chat_locationTitlePaint = r1;
        r1 = chat_locationTitlePaint;
        r3 = "fonts/rmedium.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r1.setTypeface(r3);
        r1 = new android.text.TextPaint;
        r1.<init>(r2);
        chat_locationAddressPaint = r1;
        r1 = new android.graphics.Paint;
        r1.<init>();
        chat_urlPaint = r1;
        r1 = new android.graphics.Paint;
        r1.<init>();
        chat_textSearchSelectionPaint = r1;
        r1 = new android.graphics.Paint;
        r1.<init>(r2);
        chat_radialProgressPaint = r1;
        r1 = chat_radialProgressPaint;
        r3 = android.graphics.Paint.Cap.ROUND;
        r1.setStrokeCap(r3);
        r1 = chat_radialProgressPaint;
        r3 = android.graphics.Paint.Style.STROKE;
        r1.setStyle(r3);
        r1 = chat_radialProgressPaint;
        r3 = -NUM; // 0xffffffff9fffffff float:-1.0842021E-19 double:NaN;
        r1.setColor(r3);
        r1 = new android.graphics.Paint;
        r1.<init>(r2);
        chat_radialProgress2Paint = r1;
        r1 = chat_radialProgress2Paint;
        r3 = android.graphics.Paint.Cap.ROUND;
        r1.setStrokeCap(r3);
        r1 = chat_radialProgress2Paint;
        r3 = android.graphics.Paint.Style.STROKE;
        r1.setStyle(r3);
        r1 = new android.text.TextPaint;
        r1.<init>(r2);
        chat_audioTimePaint = r1;
        r1 = new android.text.TextPaint;
        r1.<init>(r2);
        chat_livePaint = r1;
        r1 = chat_livePaint;
        r3 = "fonts/rmedium.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r1.setTypeface(r3);
        r1 = new android.text.TextPaint;
        r1.<init>(r2);
        chat_audioTitlePaint = r1;
        r1 = chat_audioTitlePaint;
        r3 = "fonts/rmedium.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r1.setTypeface(r3);
        r1 = new android.text.TextPaint;
        r1.<init>(r2);
        chat_audioPerformerPaint = r1;
        r1 = new android.text.TextPaint;
        r1.<init>(r2);
        chat_botButtonPaint = r1;
        r1 = chat_botButtonPaint;
        r3 = "fonts/rmedium.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r1.setTypeface(r3);
        r1 = new android.text.TextPaint;
        r1.<init>(r2);
        chat_contactNamePaint = r1;
        r1 = chat_contactNamePaint;
        r3 = "fonts/rmedium.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r1.setTypeface(r3);
        r1 = new android.text.TextPaint;
        r1.<init>(r2);
        chat_contactPhonePaint = r1;
        r1 = new android.text.TextPaint;
        r1.<init>(r2);
        chat_durationPaint = r1;
        r1 = new android.text.TextPaint;
        r1.<init>(r2);
        chat_gamePaint = r1;
        r1 = chat_gamePaint;
        r3 = "fonts/rmedium.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r1.setTypeface(r3);
        r1 = new android.text.TextPaint;
        r1.<init>(r2);
        chat_shipmentPaint = r1;
        r1 = new android.text.TextPaint;
        r1.<init>(r2);
        chat_timePaint = r1;
        r1 = new android.text.TextPaint;
        r1.<init>(r2);
        chat_adminPaint = r1;
        r1 = new android.text.TextPaint;
        r1.<init>(r2);
        chat_namePaint = r1;
        r1 = chat_namePaint;
        r3 = "fonts/rmedium.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r1.setTypeface(r3);
        r1 = new android.text.TextPaint;
        r1.<init>(r2);
        chat_forwardNamePaint = r1;
        r1 = new android.text.TextPaint;
        r1.<init>(r2);
        chat_replyNamePaint = r1;
        r1 = chat_replyNamePaint;
        r3 = "fonts/rmedium.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r1.setTypeface(r3);
        r1 = new android.text.TextPaint;
        r1.<init>(r2);
        chat_replyTextPaint = r1;
        r1 = new android.text.TextPaint;
        r1.<init>(r2);
        chat_instantViewPaint = r1;
        r1 = chat_instantViewPaint;
        r3 = "fonts/rmedium.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r1.setTypeface(r3);
        r1 = new android.graphics.Paint;
        r1.<init>(r2);
        chat_instantViewRectPaint = r1;
        r1 = chat_instantViewRectPaint;
        r3 = android.graphics.Paint.Style.STROKE;
        r1.setStyle(r3);
        r1 = new android.graphics.Paint;
        r1.<init>(r2);
        chat_replyLinePaint = r1;
        r1 = new android.graphics.Paint;
        r1.<init>(r2);
        chat_msgErrorPaint = r1;
        r1 = new android.graphics.Paint;
        r1.<init>(r2);
        chat_statusPaint = r1;
        r1 = new android.graphics.Paint;
        r1.<init>(r2);
        chat_statusRecordPaint = r1;
        r1 = chat_statusRecordPaint;
        r3 = android.graphics.Paint.Style.STROKE;
        r1.setStyle(r3);
        r1 = chat_statusRecordPaint;
        r3 = android.graphics.Paint.Cap.ROUND;
        r1.setStrokeCap(r3);
        r1 = new android.text.TextPaint;
        r1.<init>(r2);
        chat_actionTextPaint = r1;
        r1 = chat_actionTextPaint;
        r3 = "fonts/rmedium.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r1.setTypeface(r3);
        r1 = new android.graphics.Paint;
        r1.<init>(r2);
        chat_actionBackgroundPaint = r1;
        r1 = new android.graphics.Paint;
        r1.<init>(r2);
        chat_timeBackgroundPaint = r1;
        r1 = new android.text.TextPaint;
        r1.<init>(r2);
        chat_contextResult_titleTextPaint = r1;
        r1 = chat_contextResult_titleTextPaint;
        r3 = "fonts/rmedium.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r1.setTypeface(r3);
        r1 = new android.text.TextPaint;
        r1.<init>(r2);
        chat_contextResult_descriptionTextPaint = r1;
        r1 = new android.graphics.Paint;
        r1.<init>();
        chat_composeBackgroundPaint = r1;
        r1 = r14.getResources();
        r3 = NUM; // 0x7var_ float:1.7945365E38 double:1.0529356947E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgInDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.7945365E38 double:1.0529356947E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgInSelectedDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.794592E38 double:1.05293583E-314;
        r3 = r1.getDrawable(r3);
        chat_msgNoSoundDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.7945395E38 double:1.052935702E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgOutDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.7945395E38 double:1.052935702E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgOutSelectedDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.7945405E38 double:1.0529357046E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgInMediaDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.7945405E38 double:1.0529357046E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgInMediaSelectedDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.7945405E38 double:1.0529357046E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgOutMediaDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.7945405E38 double:1.0529357046E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgOutMediaSelectedDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.794533E38 double:1.0529356863E-314;
        r4 = r1.getDrawable(r3);
        r4 = r4.mutate();
        chat_msgOutCheckDrawable = r4;
        r4 = r1.getDrawable(r3);
        r4 = r4.mutate();
        chat_msgOutCheckSelectedDrawable = r4;
        r4 = r1.getDrawable(r3);
        r4 = r4.mutate();
        chat_msgOutCheckReadDrawable = r4;
        r4 = r1.getDrawable(r3);
        r4 = r4.mutate();
        chat_msgOutCheckReadSelectedDrawable = r4;
        r4 = r1.getDrawable(r3);
        r4 = r4.mutate();
        chat_msgMediaCheckDrawable = r4;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgStickerCheckDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.7945359E38 double:1.052935693E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgOutHalfCheckDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.7945359E38 double:1.052935693E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgOutHalfCheckSelectedDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.7945359E38 double:1.052935693E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgMediaHalfCheckDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.7945359E38 double:1.052935693E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgStickerHalfCheckDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.7945334E38 double:1.0529356873E-314;
        r4 = r1.getDrawable(r3);
        r4 = r4.mutate();
        chat_msgOutClockDrawable = r4;
        r4 = r1.getDrawable(r3);
        r4 = r4.mutate();
        chat_msgOutSelectedClockDrawable = r4;
        r4 = r1.getDrawable(r3);
        r4 = r4.mutate();
        chat_msgInClockDrawable = r4;
        r4 = r1.getDrawable(r3);
        r4 = r4.mutate();
        chat_msgInSelectedClockDrawable = r4;
        r4 = r1.getDrawable(r3);
        r4 = r4.mutate();
        chat_msgMediaClockDrawable = r4;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgStickerClockDrawable = r3;
        r3 = NUM; // 0x7var_b4 float:1.7945462E38 double:1.0529357184E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgInViewsDrawable = r3;
        r3 = NUM; // 0x7var_b4 float:1.7945462E38 double:1.0529357184E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgInViewsSelectedDrawable = r3;
        r3 = NUM; // 0x7var_b4 float:1.7945462E38 double:1.0529357184E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgOutViewsDrawable = r3;
        r3 = NUM; // 0x7var_b4 float:1.7945462E38 double:1.0529357184E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgOutViewsSelectedDrawable = r3;
        r3 = NUM; // 0x7var_b4 float:1.7945462E38 double:1.0529357184E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgMediaViewsDrawable = r3;
        r3 = NUM; // 0x7var_b4 float:1.7945462E38 double:1.0529357184E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgStickerViewsDrawable = r3;
        r3 = NUM; // 0x7var_b float:1.7945314E38 double:1.0529356824E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgInMenuDrawable = r3;
        r3 = NUM; // 0x7var_b float:1.7945314E38 double:1.0529356824E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgInMenuSelectedDrawable = r3;
        r3 = NUM; // 0x7var_b float:1.7945314E38 double:1.0529356824E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgOutMenuDrawable = r3;
        r3 = NUM; // 0x7var_b float:1.7945314E38 double:1.0529356824E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgOutMenuSelectedDrawable = r3;
        r3 = NUM; // 0x7var_f float:1.7945906E38 double:1.0529358266E-314;
        r3 = r1.getDrawable(r3);
        chat_msgMediaMenuDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.7945369E38 double:1.0529356957E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgInInstantDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.7945369E38 double:1.0529356957E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgOutInstantDrawable = r3;
        r3 = NUM; // 0x7var_b5 float:1.7945464E38 double:1.052935719E-314;
        r3 = r1.getDrawable(r3);
        chat_msgErrorDrawable = r3;
        r3 = NUM; // 0x7var_b float:1.7945152E38 double:1.052935643E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_muteIconDrawable = r3;
        r3 = NUM; // 0x7var_cb float:1.794499E38 double:1.0529356033E-314;
        r3 = r1.getDrawable(r3);
        chat_lockIconDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.7944694E38 double:1.052935531E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgBroadcastDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.7944694E38 double:1.052935531E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgBroadcastMediaDrawable = r3;
        r3 = NUM; // 0x7var_b9 float:1.7944953E38 double:1.0529355944E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgInCallDrawable = r3;
        r3 = NUM; // 0x7var_b9 float:1.7944953E38 double:1.0529355944E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgInCallSelectedDrawable = r3;
        r3 = NUM; // 0x7var_b9 float:1.7944953E38 double:1.0529355944E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgOutCallDrawable = r3;
        r3 = NUM; // 0x7var_b9 float:1.7944953E38 double:1.0529355944E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgOutCallSelectedDrawable = r3;
        r3 = NUM; // 0x7var_bc float:1.794496E38 double:1.052935596E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgCallUpGreenDrawable = r3;
        r3 = NUM; // 0x7var_bf float:1.7944965E38 double:1.0529355974E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgCallDownRedDrawable = r3;
        r3 = NUM; // 0x7var_bf float:1.7944965E38 double:1.0529355974E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgCallDownGreenDrawable = r3;
        r3 = NUM; // 0x7var_bc float:1.794496E38 double:1.052935596E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        calllog_msgCallUpRedDrawable = r3;
        r3 = NUM; // 0x7var_bc float:1.794496E38 double:1.052935596E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        calllog_msgCallUpGreenDrawable = r3;
        r3 = NUM; // 0x7var_bf float:1.7944965E38 double:1.0529355974E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        calllog_msgCallDownRedDrawable = r3;
        r3 = NUM; // 0x7var_bf float:1.7944965E38 double:1.0529355974E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        calllog_msgCallDownGreenDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.7945168E38 double:1.052935647E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_msgAvatarLiveLocationDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.7944675E38 double:1.0529355267E-314;
        r3 = r1.getDrawable(r3);
        chat_inlineResultFile = r3;
        r3 = NUM; // 0x7var_ float:1.7944683E38 double:1.0529355287E-314;
        r3 = r1.getDrawable(r3);
        chat_inlineResultAudio = r3;
        r3 = NUM; // 0x7var_ float:1.7944681E38 double:1.052935528E-314;
        r3 = r1.getDrawable(r3);
        chat_inlineResultLocation = r3;
        r3 = NUM; // 0x7var_ float:1.7945199E38 double:1.052935654E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_redLocationIcon = r3;
        r3 = NUM; // 0x7var_ float:1.7945367E38 double:1.052935695E-314;
        r3 = r1.getDrawable(r3);
        chat_msgInShadowDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.7945397E38 double:1.0529357026E-314;
        r3 = r1.getDrawable(r3);
        chat_msgOutShadowDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.7945407E38 double:1.052935705E-314;
        r3 = r1.getDrawable(r3);
        chat_msgInMediaShadowDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.7945407E38 double:1.052935705E-314;
        r3 = r1.getDrawable(r3);
        chat_msgOutMediaShadowDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.794468E38 double:1.0529355277E-314;
        r3 = r1.getDrawable(r3);
        chat_botLinkDrawalbe = r3;
        r3 = NUM; // 0x7var_ float:1.7944677E38 double:1.052935527E-314;
        r3 = r1.getDrawable(r3);
        chat_botInlineDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.7945852E38 double:1.0529358133E-314;
        r3 = r1.getDrawable(r3);
        chat_systemDrawable = r3;
        r3 = NUM; // 0x7var_a2 float:1.7944907E38 double:1.052935583E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_contextResult_shadowUnderSwitchDrawable = r3;
        r3 = chat_attachButtonDrawables;
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = NUM; // 0x7var_ float:1.7944623E38 double:1.052935514E-314;
        r4 = createCircleDrawableWithIcon(r4, r5);
        r5 = 0;
        r3[r5] = r4;
        r3 = chat_attachButtonDrawables;
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r6 = NUM; // 0x7var_ float:1.7944614E38 double:1.052935512E-314;
        r4 = createCircleDrawableWithIcon(r4, r6);
        r3[r2] = r4;
        r3 = chat_attachButtonDrawables;
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r6 = NUM; // 0x7var_ float:1.794462E38 double:1.0529355134E-314;
        r4 = createCircleDrawableWithIcon(r4, r6);
        r3[r0] = r4;
        r3 = chat_attachButtonDrawables;
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r6 = NUM; // 0x7var_ float:1.7944619E38 double:1.052935513E-314;
        r4 = createCircleDrawableWithIcon(r4, r6);
        r6 = 3;
        r3[r6] = r4;
        r3 = chat_attachButtonDrawables;
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r7 = NUM; // 0x7var_ float:1.7944625E38 double:1.0529355144E-314;
        r4 = createCircleDrawableWithIcon(r4, r7);
        r7 = 4;
        r3[r7] = r4;
        r3 = chat_attachButtonDrawables;
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r8 = NUM; // 0x7var_ float:1.7944627E38 double:1.052935515E-314;
        r4 = createCircleDrawableWithIcon(r4, r8);
        r8 = 5;
        r3[r8] = r4;
        r3 = NUM; // 0x7var_c4 float:1.7945495E38 double:1.0529357263E-314;
        r3 = r1.getDrawable(r3);
        chat_attachEmptyDrawable = r3;
        r3 = chat_cornerOuter;
        r4 = NUM; // 0x7var_ float:1.794482E38 double:1.052935562E-314;
        r4 = r1.getDrawable(r4);
        r3[r5] = r4;
        r3 = chat_cornerOuter;
        r4 = NUM; // 0x7var_ float:1.7944821E38 double:1.0529355623E-314;
        r4 = r1.getDrawable(r4);
        r3[r2] = r4;
        r3 = chat_cornerOuter;
        r4 = NUM; // 0x7var_ float:1.7944817E38 double:1.0529355613E-314;
        r4 = r1.getDrawable(r4);
        r3[r0] = r4;
        r3 = chat_cornerOuter;
        r4 = NUM; // 0x7var_ float:1.7944815E38 double:1.052935561E-314;
        r4 = r1.getDrawable(r4);
        r3[r6] = r4;
        r3 = chat_cornerInner;
        r4 = NUM; // 0x7var_ float:1.7944813E38 double:1.0529355603E-314;
        r4 = r1.getDrawable(r4);
        r3[r5] = r4;
        r3 = chat_cornerInner;
        r4 = NUM; // 0x7var_ float:1.7944811E38 double:1.05293556E-314;
        r4 = r1.getDrawable(r4);
        r3[r2] = r4;
        r3 = chat_cornerInner;
        r4 = NUM; // 0x7var_ float:1.794481E38 double:1.0529355594E-314;
        r4 = r1.getDrawable(r4);
        r3[r0] = r4;
        r3 = chat_cornerInner;
        r4 = NUM; // 0x7var_ float:1.7944807E38 double:1.052935559E-314;
        r4 = r1.getDrawable(r4);
        r3[r6] = r4;
        r3 = NUM; // 0x7var_ float:1.7945748E38 double:1.052935788E-314;
        r3 = r1.getDrawable(r3);
        chat_shareDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.7945746E38 double:1.0529357876E-314;
        r3 = r1.getDrawable(r3);
        chat_shareIconDrawable = r3;
        r3 = NUM; // 0x7var_e float:1.7944834E38 double:1.0529355653E-314;
        r3 = r1.getDrawable(r3);
        chat_replyIconDrawable = r3;
        r3 = NUM; // 0x7var_ float:1.79453E38 double:1.052935679E-314;
        r3 = r1.getDrawable(r3);
        chat_goIconDrawable = r3;
        r3 = chat_fileMiniStatesDrawable;
        r3 = r3[r5];
        r4 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r10 = NUM; // 0x7var_b float:1.7944633E38 double:1.0529355164E-314;
        r9 = createCircleDrawableWithIcon(r9, r10);
        r3[r5] = r9;
        r3 = chat_fileMiniStatesDrawable;
        r3 = r3[r5];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r9 = createCircleDrawableWithIcon(r9, r10);
        r3[r2] = r9;
        r3 = chat_fileMiniStatesDrawable;
        r3 = r3[r2];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r10 = NUM; // 0x7var_c float:1.7944635E38 double:1.052935517E-314;
        r9 = createCircleDrawableWithIcon(r9, r10);
        r3[r5] = r9;
        r3 = chat_fileMiniStatesDrawable;
        r3 = r3[r2];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r9 = createCircleDrawableWithIcon(r9, r10);
        r3[r2] = r9;
        r3 = chat_fileMiniStatesDrawable;
        r3 = r3[r0];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r10 = NUM; // 0x7var_b float:1.7944633E38 double:1.0529355164E-314;
        r9 = createCircleDrawableWithIcon(r9, r10);
        r3[r5] = r9;
        r3 = chat_fileMiniStatesDrawable;
        r3 = r3[r0];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r9 = createCircleDrawableWithIcon(r9, r10);
        r3[r2] = r9;
        r3 = chat_fileMiniStatesDrawable;
        r3 = r3[r6];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r10 = NUM; // 0x7var_c float:1.7944635E38 double:1.052935517E-314;
        r9 = createCircleDrawableWithIcon(r9, r10);
        r3[r5] = r9;
        r3 = chat_fileMiniStatesDrawable;
        r3 = r3[r6];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r9 = createCircleDrawableWithIcon(r9, r10);
        r3[r2] = r9;
        r3 = chat_fileMiniStatesDrawable;
        r3 = r3[r7];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r10 = NUM; // 0x7var_ float:1.7945915E38 double:1.0529358286E-314;
        r9 = createCircleDrawableWithIcon(r9, r10);
        r3[r5] = r9;
        r3 = chat_fileMiniStatesDrawable;
        r3 = r3[r7];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r9 = createCircleDrawableWithIcon(r9, r10);
        r3[r2] = r9;
        r3 = chat_fileMiniStatesDrawable;
        r3 = r3[r8];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r10 = NUM; // 0x7var_ float:1.7945917E38 double:1.052935829E-314;
        r9 = createCircleDrawableWithIcon(r9, r10);
        r3[r5] = r9;
        r3 = chat_fileMiniStatesDrawable;
        r3 = r3[r8];
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r9 = NUM; // 0x7var_ float:1.7945917E38 double:1.052935829E-314;
        r4 = createCircleDrawableWithIcon(r4, r9);
        r3[r2] = r4;
        r3 = NUM; // 0x7var_a0 float:1.7945422E38 double:1.0529357086E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_fileIcon = r3;
        r3 = NUM; // 0x7var_c float:1.79447E38 double:1.0529355327E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_flameIcon = r3;
        r3 = NUM; // 0x7var_a1 float:1.7945424E38 double:1.052935709E-314;
        r3 = r1.getDrawable(r3);
        r3 = r3.mutate();
        chat_gifIcon = r3;
        r3 = chat_fileStatesDrawable;
        r3 = r3[r5];
        r4 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r10 = NUM; // 0x7var_a4 float:1.794543E38 double:1.0529357105E-314;
        r9 = createCircleDrawableWithIcon(r9, r10);
        r3[r5] = r9;
        r3 = chat_fileStatesDrawable;
        r3 = r3[r5];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r9 = createCircleDrawableWithIcon(r9, r10);
        r3[r2] = r9;
        r3 = chat_fileStatesDrawable;
        r3 = r3[r2];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r10 = NUM; // 0x7var_a3 float:1.7945428E38 double:1.05293571E-314;
        r9 = createCircleDrawableWithIcon(r9, r10);
        r3[r5] = r9;
        r3 = chat_fileStatesDrawable;
        r3 = r3[r2];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r9 = createCircleDrawableWithIcon(r9, r10);
        r3[r2] = r9;
        r3 = chat_fileStatesDrawable;
        r3 = r3[r0];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r10 = NUM; // 0x7var_a2 float:1.7945426E38 double:1.0529357095E-314;
        r9 = createCircleDrawableWithIcon(r9, r10);
        r3[r5] = r9;
        r3 = chat_fileStatesDrawable;
        r3 = r3[r0];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r9 = createCircleDrawableWithIcon(r9, r10);
        r3[r2] = r9;
        r3 = chat_fileStatesDrawable;
        r3 = r3[r6];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r11 = NUM; // 0x7var_a0 float:1.7945422E38 double:1.0529357086E-314;
        r9 = createCircleDrawableWithIcon(r9, r11);
        r3[r5] = r9;
        r3 = chat_fileStatesDrawable;
        r3 = r3[r6];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r9 = createCircleDrawableWithIcon(r9, r11);
        r3[r2] = r9;
        r3 = chat_fileStatesDrawable;
        r3 = r3[r7];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r11 = NUM; // 0x7var_f float:1.794542E38 double:1.052935708E-314;
        r9 = createCircleDrawableWithIcon(r9, r11);
        r3[r5] = r9;
        r3 = chat_fileStatesDrawable;
        r3 = r3[r7];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r9 = createCircleDrawableWithIcon(r9, r11);
        r3[r2] = r9;
        r3 = chat_fileStatesDrawable;
        r3 = r3[r8];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r12 = NUM; // 0x7var_a4 float:1.794543E38 double:1.0529357105E-314;
        r9 = createCircleDrawableWithIcon(r9, r12);
        r3[r5] = r9;
        r3 = chat_fileStatesDrawable;
        r3 = r3[r8];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r9 = createCircleDrawableWithIcon(r9, r12);
        r3[r2] = r9;
        r3 = chat_fileStatesDrawable;
        r9 = 6;
        r3 = r3[r9];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r12 = NUM; // 0x7var_a3 float:1.7945428E38 double:1.05293571E-314;
        r9 = createCircleDrawableWithIcon(r9, r12);
        r3[r5] = r9;
        r3 = chat_fileStatesDrawable;
        r9 = 6;
        r3 = r3[r9];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r9 = createCircleDrawableWithIcon(r9, r12);
        r3[r2] = r9;
        r3 = chat_fileStatesDrawable;
        r9 = 7;
        r3 = r3[r9];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r9 = createCircleDrawableWithIcon(r9, r10);
        r3[r5] = r9;
        r3 = chat_fileStatesDrawable;
        r9 = 7;
        r3 = r3[r9];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r9 = createCircleDrawableWithIcon(r9, r10);
        r3[r2] = r9;
        r3 = chat_fileStatesDrawable;
        r9 = 8;
        r3 = r3[r9];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r12 = NUM; // 0x7var_a0 float:1.7945422E38 double:1.0529357086E-314;
        r9 = createCircleDrawableWithIcon(r9, r12);
        r3[r5] = r9;
        r3 = chat_fileStatesDrawable;
        r9 = 8;
        r3 = r3[r9];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r9 = createCircleDrawableWithIcon(r9, r12);
        r3[r2] = r9;
        r3 = chat_fileStatesDrawable;
        r9 = 9;
        r3 = r3[r9];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r9 = createCircleDrawableWithIcon(r9, r11);
        r3[r5] = r9;
        r3 = chat_fileStatesDrawable;
        r9 = 9;
        r3 = r3[r9];
        r9 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r9 = createCircleDrawableWithIcon(r9, r11);
        r3[r2] = r9;
        r3 = chat_photoStatesDrawables;
        r3 = r3[r5];
        r9 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r12 = createCircleDrawableWithIcon(r12, r10);
        r3[r5] = r12;
        r3 = chat_photoStatesDrawables;
        r3 = r3[r5];
        r12 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r12 = createCircleDrawableWithIcon(r12, r10);
        r3[r2] = r12;
        r3 = chat_photoStatesDrawables;
        r3 = r3[r2];
        r12 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r12 = createCircleDrawableWithIcon(r12, r11);
        r3[r5] = r12;
        r3 = chat_photoStatesDrawables;
        r3 = r3[r2];
        r12 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r12 = createCircleDrawableWithIcon(r12, r11);
        r3[r2] = r12;
        r3 = chat_photoStatesDrawables;
        r3 = r3[r0];
        r12 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r13 = NUM; // 0x7var_a1 float:1.7945424E38 double:1.052935709E-314;
        r12 = createCircleDrawableWithIcon(r12, r13);
        r3[r5] = r12;
        r3 = chat_photoStatesDrawables;
        r3 = r3[r0];
        r12 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r12 = createCircleDrawableWithIcon(r12, r13);
        r3[r2] = r12;
        r3 = chat_photoStatesDrawables;
        r3 = r3[r6];
        r12 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r13 = NUM; // 0x7var_a4 float:1.794543E38 double:1.0529357105E-314;
        r12 = createCircleDrawableWithIcon(r12, r13);
        r3[r5] = r12;
        r3 = chat_photoStatesDrawables;
        r3 = r3[r6];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r12 = NUM; // 0x7var_a4 float:1.794543E38 double:1.0529357105E-314;
        r6 = createCircleDrawableWithIcon(r6, r12);
        r3[r2] = r6;
        r3 = chat_photoStatesDrawables;
        r6 = r3[r7];
        r3 = r3[r7];
        r7 = NUM; // 0x7var_c float:1.79447E38 double:1.0529355327E-314;
        r7 = r1.getDrawable(r7);
        r3[r2] = r7;
        r6[r5] = r7;
        r3 = chat_photoStatesDrawables;
        r6 = r3[r8];
        r3 = r3[r8];
        r7 = NUM; // 0x7var_ float:1.7944752E38 double:1.0529355455E-314;
        r7 = r1.getDrawable(r7);
        r3[r2] = r7;
        r6[r5] = r7;
        r3 = chat_photoStatesDrawables;
        r6 = 6;
        r6 = r3[r6];
        r7 = 6;
        r3 = r3[r7];
        r7 = NUM; // 0x7var_fc float:1.7945608E38 double:1.052935754E-314;
        r7 = r1.getDrawable(r7);
        r3[r2] = r7;
        r6[r5] = r7;
        r3 = chat_photoStatesDrawables;
        r6 = 7;
        r3 = r3[r6];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r6 = createCircleDrawableWithIcon(r6, r10);
        r3[r5] = r6;
        r3 = chat_photoStatesDrawables;
        r6 = 7;
        r3 = r3[r6];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r6 = createCircleDrawableWithIcon(r6, r10);
        r3[r2] = r6;
        r3 = chat_photoStatesDrawables;
        r6 = 8;
        r3 = r3[r6];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r6 = createCircleDrawableWithIcon(r6, r11);
        r3[r5] = r6;
        r3 = chat_photoStatesDrawables;
        r6 = 8;
        r3 = r3[r6];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r6 = createCircleDrawableWithIcon(r6, r11);
        r3[r2] = r6;
        r3 = chat_photoStatesDrawables;
        r6 = 9;
        r3 = r3[r6];
        r6 = NUM; // 0x7var_b float:1.7944827E38 double:1.052935564E-314;
        r6 = r1.getDrawable(r6);
        r6 = r6.mutate();
        r3[r5] = r6;
        r3 = chat_photoStatesDrawables;
        r6 = 9;
        r3 = r3[r6];
        r6 = NUM; // 0x7var_b float:1.7944827E38 double:1.052935564E-314;
        r6 = r1.getDrawable(r6);
        r6 = r6.mutate();
        r3[r2] = r6;
        r3 = chat_photoStatesDrawables;
        r6 = 10;
        r3 = r3[r6];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r6 = createCircleDrawableWithIcon(r6, r10);
        r3[r5] = r6;
        r3 = chat_photoStatesDrawables;
        r6 = 10;
        r3 = r3[r6];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r6 = createCircleDrawableWithIcon(r6, r10);
        r3[r2] = r6;
        r3 = chat_photoStatesDrawables;
        r6 = 11;
        r3 = r3[r6];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r6 = createCircleDrawableWithIcon(r6, r11);
        r3[r5] = r6;
        r3 = chat_photoStatesDrawables;
        r6 = 11;
        r3 = r3[r6];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r6 = createCircleDrawableWithIcon(r6, r11);
        r3[r2] = r6;
        r3 = chat_photoStatesDrawables;
        r6 = 12;
        r3 = r3[r6];
        r6 = NUM; // 0x7var_b float:1.7944827E38 double:1.052935564E-314;
        r6 = r1.getDrawable(r6);
        r6 = r6.mutate();
        r3[r5] = r6;
        r3 = chat_photoStatesDrawables;
        r6 = 12;
        r3 = r3[r6];
        r6 = NUM; // 0x7var_b float:1.7944827E38 double:1.052935564E-314;
        r1 = r1.getDrawable(r6);
        r1 = r1.mutate();
        r3[r2] = r1;
        r1 = chat_contactDrawable;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r6 = NUM; // 0x7var_ float:1.7945337E38 double:1.052935688E-314;
        r3 = createCircleDrawableWithIcon(r3, r6);
        r1[r5] = r3;
        r1 = chat_contactDrawable;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = NUM; // 0x7var_ float:1.7945337E38 double:1.052935688E-314;
        r3 = createCircleDrawableWithIcon(r3, r4);
        r1[r2] = r3;
        r1 = chat_locationDrawable;
        r3 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = NUM; // 0x7var_a float:1.7945377E38 double:1.0529356977E-314;
        r3 = createRoundRectDrawableWithIcon(r3, r4);
        r1[r5] = r3;
        r1 = chat_locationDrawable;
        r3 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = createRoundRectDrawableWithIcon(r3, r4);
        r1[r2] = r3;
        r14 = r14.getResources();
        r1 = NUM; // 0x7var_e float:1.7944801E38 double:1.0529355574E-314;
        r14 = r14.getDrawable(r1);
        chat_composeShadowDrawable = r14;
        r14 = org.telegram.messenger.AndroidUtilities.roundMessageSize;	 Catch:{ all -> 0x0a74 }
        r1 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);	 Catch:{ all -> 0x0a74 }
        r14 = r14 + r1;
        r1 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ all -> 0x0a74 }
        r1 = android.graphics.Bitmap.createBitmap(r14, r14, r1);	 Catch:{ all -> 0x0a74 }
        r3 = new android.graphics.Canvas;	 Catch:{ all -> 0x0a74 }
        r3.<init>(r1);	 Catch:{ all -> 0x0a74 }
        r4 = new android.graphics.Paint;	 Catch:{ all -> 0x0a74 }
        r4.<init>(r2);	 Catch:{ all -> 0x0a74 }
        r4.setColor(r5);	 Catch:{ all -> 0x0a74 }
        r6 = android.graphics.Paint.Style.FILL;	 Catch:{ all -> 0x0a74 }
        r4.setStyle(r6);	 Catch:{ all -> 0x0a74 }
        r6 = new android.graphics.PorterDuffXfermode;	 Catch:{ all -> 0x0a74 }
        r7 = android.graphics.PorterDuff.Mode.CLEAR;	 Catch:{ all -> 0x0a74 }
        r6.<init>(r7);	 Catch:{ all -> 0x0a74 }
        r4.setXfermode(r6);	 Catch:{ all -> 0x0a74 }
        r6 = new android.graphics.Paint;	 Catch:{ all -> 0x0a74 }
        r6.<init>(r2);	 Catch:{ all -> 0x0a74 }
        r2 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);	 Catch:{ all -> 0x0a74 }
        r2 = (float) r2;	 Catch:{ all -> 0x0a74 }
        r7 = 0;
        r8 = 0;
        r9 = NUM; // 0x5var_ float:9.223372E18 double:7.874593756E-315;
        r6.setShadowLayer(r2, r7, r8, r9);	 Catch:{ all -> 0x0a74 }
    L_0x0a4b:
        if (r5 >= r0) goto L_0x0a69;
    L_0x0a4d:
        r2 = r14 / 2;
        r2 = (float) r2;	 Catch:{ all -> 0x0a74 }
        r7 = r14 / 2;
        r7 = (float) r7;	 Catch:{ all -> 0x0a74 }
        r8 = org.telegram.messenger.AndroidUtilities.roundMessageSize;	 Catch:{ all -> 0x0a74 }
        r8 = r8 / r0;
        r9 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);	 Catch:{ all -> 0x0a74 }
        r8 = r8 - r9;
        r8 = (float) r8;	 Catch:{ all -> 0x0a74 }
        if (r5 != 0) goto L_0x0a62;
    L_0x0a60:
        r9 = r6;
        goto L_0x0a63;
    L_0x0a62:
        r9 = r4;
    L_0x0a63:
        r3.drawCircle(r2, r7, r8, r9);	 Catch:{ all -> 0x0a74 }
        r5 = r5 + 1;
        goto L_0x0a4b;
    L_0x0a69:
        r14 = 0;
        r3.setBitmap(r14);	 Catch:{ Exception -> 0x0a6d }
    L_0x0a6d:
        r14 = new android.graphics.drawable.BitmapDrawable;	 Catch:{ all -> 0x0a74 }
        r14.<init>(r1);	 Catch:{ all -> 0x0a74 }
        chat_roundVideoShadow = r14;	 Catch:{ all -> 0x0a74 }
    L_0x0a74:
        applyChatTheme(r15);
    L_0x0a77:
        r14 = chat_msgTextPaintOneEmoji;
        r1 = NUM; // 0x41e00000 float:28.0 double:5.46040909E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = (float) r1;
        r14.setTextSize(r1);
        r14 = chat_msgTextPaintTwoEmoji;
        r1 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = (float) r1;
        r14.setTextSize(r1);
        r14 = chat_msgTextPaintThreeEmoji;
        r1 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = (float) r1;
        r14.setTextSize(r1);
        r14 = chat_msgTextPaint;
        r1 = org.telegram.messenger.SharedConfig.fontSize;
        r1 = (float) r1;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = (float) r1;
        r14.setTextSize(r1);
        r14 = chat_msgGameTextPaint;
        r1 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = (float) r1;
        r14.setTextSize(r1);
        r14 = chat_msgBotButtonPaint;
        r1 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r2 = (float) r2;
        r14.setTextSize(r2);
        if (r15 != 0) goto L_0x0CLASSNAME;
    L_0x0ac2:
        r14 = chat_botProgressPaint;
        if (r14 == 0) goto L_0x0CLASSNAME;
    L_0x0ac6:
        r15 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r15 = (float) r15;
        r14.setStrokeWidth(r15);
        r14 = chat_infoPaint;
        r15 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r15 = (float) r15;
        r14.setTextSize(r15);
        r14 = chat_docNamePaint;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r15 = (float) r15;
        r14.setTextSize(r15);
        r14 = chat_locationTitlePaint;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r15 = (float) r15;
        r14.setTextSize(r15);
        r14 = chat_locationAddressPaint;
        r15 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r2 = (float) r2;
        r14.setTextSize(r2);
        r14 = chat_audioTimePaint;
        r2 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r14.setTextSize(r2);
        r14 = chat_livePaint;
        r2 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r14.setTextSize(r2);
        r14 = chat_audioTitlePaint;
        r2 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r14.setTextSize(r2);
        r14 = chat_audioPerformerPaint;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r2 = (float) r2;
        r14.setTextSize(r2);
        r14 = chat_botButtonPaint;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r2 = (float) r2;
        r14.setTextSize(r2);
        r14 = chat_contactNamePaint;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r2 = (float) r2;
        r14.setTextSize(r2);
        r14 = chat_contactPhonePaint;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r2 = (float) r2;
        r14.setTextSize(r2);
        r14 = chat_durationPaint;
        r2 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r14.setTextSize(r2);
        r14 = chat_timePaint;
        r2 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r14.setTextSize(r2);
        r14 = chat_adminPaint;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r2 = (float) r2;
        r14.setTextSize(r2);
        r14 = chat_namePaint;
        r2 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r14.setTextSize(r2);
        r14 = chat_forwardNamePaint;
        r2 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r14.setTextSize(r2);
        r14 = chat_replyNamePaint;
        r2 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r14.setTextSize(r2);
        r14 = chat_replyTextPaint;
        r2 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r14.setTextSize(r2);
        r14 = chat_gamePaint;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r2 = (float) r2;
        r14.setTextSize(r2);
        r14 = chat_shipmentPaint;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r2 = (float) r2;
        r14.setTextSize(r2);
        r14 = chat_instantViewPaint;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r2 = (float) r2;
        r14.setTextSize(r2);
        r14 = chat_instantViewRectPaint;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r14.setStrokeWidth(r2);
        r14 = chat_statusRecordPaint;
        r2 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r14.setStrokeWidth(r2);
        r14 = chat_actionTextPaint;
        r2 = 16;
        r3 = org.telegram.messenger.SharedConfig.fontSize;
        r2 = java.lang.Math.max(r2, r3);
        r2 = r2 - r0;
        r0 = (float) r2;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r0 = (float) r0;
        r14.setTextSize(r0);
        r14 = chat_contextResult_titleTextPaint;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r0 = (float) r0;
        r14.setTextSize(r0);
        r14 = chat_contextResult_descriptionTextPaint;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r15 = (float) r15;
        r14.setTextSize(r15);
        r14 = chat_radialProgressPaint;
        r15 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r15 = (float) r15;
        r14.setStrokeWidth(r15);
        r14 = chat_radialProgress2Paint;
        r15 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r15 = (float) r15;
        r14.setStrokeWidth(r15);
    L_0x0CLASSNAME:
        return;
    L_0x0CLASSNAME:
        r14 = move-exception;
        monitor-exit(r0);	 Catch:{ all -> 0x0CLASSNAME }
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        throw r14;
    L_0x0CLASSNAME:
        goto L_0x0CLASSNAME;
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
            String str5 = "chat_outBubble";
            setDrawableColorByKey(chat_msgOutDrawable, str5);
            String str6 = "chat_outBubbleSelected";
            setDrawableColorByKey(chat_msgOutSelectedDrawable, str6);
            String str7 = "chat_outBubbleShadow";
            setDrawableColorByKey(chat_msgOutShadowDrawable, str7);
            setDrawableColorByKey(chat_msgInMediaDrawable, str2);
            setDrawableColorByKey(chat_msgInMediaSelectedDrawable, str3);
            setDrawableColorByKey(chat_msgInMediaShadowDrawable, str4);
            setDrawableColorByKey(chat_msgOutMediaDrawable, str5);
            setDrawableColorByKey(chat_msgOutMediaSelectedDrawable, str6);
            setDrawableColorByKey(chat_msgOutMediaShadowDrawable, str7);
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
            return (str.equals("chats_menuTopShadow") || str.equals("chats_menuTopBackground")) ? 0 : -65536;
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

    public static int getColor(String str) {
        return getColor(str, null);
    }

    public static int getColor(String str, boolean[] zArr) {
        Integer num;
        HashMap hashMap = animatingColors;
        if (hashMap != null) {
            num = (Integer) hashMap.get(str);
            if (num != null) {
                return num.intValue();
            }
        }
        String str2 = "chat_serviceBackgroundSelected";
        String str3 = "chat_serviceBackground";
        if (!isCurrentThemeDefault()) {
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
        } else if (str.equals(str3)) {
            return serviceMessageColor;
        } else {
            if (str.equals(str2)) {
                return serviceSelectedMessageColor;
            }
            return getDefaultColor(str);
        }
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
        if (str.equals("chat_serviceBackground") || str.equals("chat_serviceBackgroundSelected")) {
            applyChatServiceMessageColor();
        } else if (str.equals(str2) || str.equals(str3)) {
            reloadWallpaper();
        }
    }

    public static void setThemeWallpaper(ThemeInfo themeInfo, Bitmap bitmap, File file) {
        currentColors.remove("chat_wallpaper");
        currentColors.remove("chat_wallpaper_gradient_to");
        themedWallpaperLink = null;
        MessagesController.getGlobalMainSettings().edit().remove("overrideThemeWallpaper").commit();
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
        return currentColors.containsKey("chat_wallpaper") || themedWallpaperFileOffset > 0 || !TextUtils.isEmpty(themedWallpaperLink);
    }

    public static boolean isCustomTheme() {
        return isCustomTheme;
    }

    public static int getSelectedColor() {
        return selectedColor;
    }

    public static void reloadWallpaper() {
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
            Utilities.searchQueue.postRunnable(-$$Lambda$Theme$mtcROGDmdvjCywBvR0cdvd_2Uoc.INSTANCE);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:68:0x011f A:{SYNTHETIC, Splitter:B:68:0x011f} */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0109 A:{SYNTHETIC, Splitter:B:55:0x0109} */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x011f A:{SYNTHETIC, Splitter:B:68:0x011f} */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x019b A:{Catch:{ all -> 0x010f }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:90:0x0197 */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x011f A:{SYNTHETIC, Splitter:B:68:0x011f} */
    /* JADX WARNING: Can't wrap try/catch for region: R(7:68|69|70|(2:72|73)(2:74|(1:(2:84|(1:86)(1:87))(1:83)))|90|91|(2:(1:94)|95)) */
    static /* synthetic */ void lambda$loadWallpaper$7() {
        /*
        r0 = wallpaperSync;
        monitor-enter(r0);
        r1 = org.telegram.messenger.MessagesController.getGlobalMainSettings();	 Catch:{ all -> 0x01b3 }
        r2 = previousTheme;	 Catch:{ all -> 0x01b3 }
        r3 = 1;
        r4 = 0;
        if (r2 != 0) goto L_0x0017;
    L_0x000d:
        r2 = "overrideThemeWallpaper";
        r2 = r1.getBoolean(r2, r4);	 Catch:{ all -> 0x01b3 }
        if (r2 == 0) goto L_0x0017;
    L_0x0015:
        r2 = 1;
        goto L_0x0018;
    L_0x0017:
        r2 = 0;
    L_0x0018:
        r5 = "selectedBackgroundMotion";
        r5 = r1.getBoolean(r5, r4);	 Catch:{ all -> 0x01b3 }
        isWallpaperMotion = r5;	 Catch:{ all -> 0x01b3 }
        r5 = "selectedPattern";
        r6 = 0;
        r8 = r1.getLong(r5, r6);	 Catch:{ all -> 0x01b3 }
        r5 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1));
        if (r5 == 0) goto L_0x002e;
    L_0x002c:
        r5 = 1;
        goto L_0x002f;
    L_0x002e:
        r5 = 0;
    L_0x002f:
        isPatternWallpaper = r5;	 Catch:{ all -> 0x01b3 }
        if (r2 != 0) goto L_0x011b;
    L_0x0033:
        r2 = currentColors;	 Catch:{ all -> 0x01b3 }
        r5 = "chat_wallpaper";
        r2 = r2.get(r5);	 Catch:{ all -> 0x01b3 }
        r2 = (java.lang.Integer) r2;	 Catch:{ all -> 0x01b3 }
        if (r2 == 0) goto L_0x0073;
    L_0x003f:
        r5 = currentColors;	 Catch:{ all -> 0x01b3 }
        r8 = "chat_wallpaper_gradient_to";
        r5 = r5.get(r8);	 Catch:{ all -> 0x01b3 }
        r5 = (java.lang.Integer) r5;	 Catch:{ all -> 0x01b3 }
        if (r5 != 0) goto L_0x0057;
    L_0x004b:
        r5 = new android.graphics.drawable.ColorDrawable;	 Catch:{ all -> 0x01b3 }
        r2 = r2.intValue();	 Catch:{ all -> 0x01b3 }
        r5.<init>(r2);	 Catch:{ all -> 0x01b3 }
        wallpaper = r5;	 Catch:{ all -> 0x01b3 }
        goto L_0x006f;
    L_0x0057:
        r8 = new org.telegram.ui.Components.BackgroundGradientDrawable;	 Catch:{ all -> 0x01b3 }
        r9 = android.graphics.drawable.GradientDrawable.Orientation.BL_TR;	 Catch:{ all -> 0x01b3 }
        r10 = 2;
        r10 = new int[r10];	 Catch:{ all -> 0x01b3 }
        r2 = r2.intValue();	 Catch:{ all -> 0x01b3 }
        r10[r4] = r2;	 Catch:{ all -> 0x01b3 }
        r2 = r5.intValue();	 Catch:{ all -> 0x01b3 }
        r10[r3] = r2;	 Catch:{ all -> 0x01b3 }
        r8.<init>(r9, r10);	 Catch:{ all -> 0x01b3 }
        wallpaper = r8;	 Catch:{ all -> 0x01b3 }
    L_0x006f:
        isCustomTheme = r3;	 Catch:{ all -> 0x01b3 }
        goto L_0x011b;
    L_0x0073:
        r2 = themedWallpaperLink;	 Catch:{ all -> 0x01b3 }
        if (r2 == 0) goto L_0x00ae;
    L_0x0077:
        r2 = new java.io.File;	 Catch:{ all -> 0x01b3 }
        r5 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();	 Catch:{ all -> 0x01b3 }
        r8 = new java.lang.StringBuilder;	 Catch:{ all -> 0x01b3 }
        r8.<init>();	 Catch:{ all -> 0x01b3 }
        r9 = themedWallpaperLink;	 Catch:{ all -> 0x01b3 }
        r9 = org.telegram.messenger.Utilities.MD5(r9);	 Catch:{ all -> 0x01b3 }
        r8.append(r9);	 Catch:{ all -> 0x01b3 }
        r9 = ".wp";
        r8.append(r9);	 Catch:{ all -> 0x01b3 }
        r8 = r8.toString();	 Catch:{ all -> 0x01b3 }
        r2.<init>(r5, r8);	 Catch:{ all -> 0x01b3 }
        r2 = r2.getAbsolutePath();	 Catch:{ all -> 0x01b3 }
        r2 = android.graphics.BitmapFactory.decodeFile(r2);	 Catch:{ all -> 0x01b3 }
        if (r2 == 0) goto L_0x011b;
    L_0x00a1:
        r5 = new android.graphics.drawable.BitmapDrawable;	 Catch:{ all -> 0x01b3 }
        r5.<init>(r2);	 Catch:{ all -> 0x01b3 }
        wallpaper = r5;	 Catch:{ all -> 0x01b3 }
        themedWallpaper = r5;	 Catch:{ all -> 0x01b3 }
        isCustomTheme = r3;	 Catch:{ all -> 0x01b3 }
        goto L_0x011b;
    L_0x00ae:
        r2 = themedWallpaperFileOffset;	 Catch:{ all -> 0x01b3 }
        if (r2 <= 0) goto L_0x011b;
    L_0x00b2:
        r2 = currentTheme;	 Catch:{ all -> 0x01b3 }
        r2 = r2.pathToFile;	 Catch:{ all -> 0x01b3 }
        if (r2 != 0) goto L_0x00be;
    L_0x00b8:
        r2 = currentTheme;	 Catch:{ all -> 0x01b3 }
        r2 = r2.assetName;	 Catch:{ all -> 0x01b3 }
        if (r2 == 0) goto L_0x011b;
    L_0x00be:
        r2 = 0;
        r5 = currentTheme;	 Catch:{ all -> 0x0103 }
        r5 = r5.assetName;	 Catch:{ all -> 0x0103 }
        if (r5 == 0) goto L_0x00ce;
    L_0x00c5:
        r5 = currentTheme;	 Catch:{ all -> 0x0103 }
        r5 = r5.assetName;	 Catch:{ all -> 0x0103 }
        r5 = getAssetFile(r5);	 Catch:{ all -> 0x0103 }
        goto L_0x00d7;
    L_0x00ce:
        r5 = new java.io.File;	 Catch:{ all -> 0x0103 }
        r8 = currentTheme;	 Catch:{ all -> 0x0103 }
        r8 = r8.pathToFile;	 Catch:{ all -> 0x0103 }
        r5.<init>(r8);	 Catch:{ all -> 0x0103 }
    L_0x00d7:
        r8 = new java.io.FileInputStream;	 Catch:{ all -> 0x0103 }
        r8.<init>(r5);	 Catch:{ all -> 0x0103 }
        r2 = r8.getChannel();	 Catch:{ all -> 0x0100 }
        r5 = themedWallpaperFileOffset;	 Catch:{ all -> 0x0100 }
        r9 = (long) r5;	 Catch:{ all -> 0x0100 }
        r2.position(r9);	 Catch:{ all -> 0x0100 }
        r2 = android.graphics.BitmapFactory.decodeStream(r8);	 Catch:{ all -> 0x0100 }
        if (r2 == 0) goto L_0x00f7;
    L_0x00ec:
        r5 = new android.graphics.drawable.BitmapDrawable;	 Catch:{ all -> 0x0100 }
        r5.<init>(r2);	 Catch:{ all -> 0x0100 }
        wallpaper = r5;	 Catch:{ all -> 0x0100 }
        themedWallpaper = r5;	 Catch:{ all -> 0x0100 }
        isCustomTheme = r3;	 Catch:{ all -> 0x0100 }
    L_0x00f7:
        r8.close();	 Catch:{ Exception -> 0x00fb }
        goto L_0x011b;
    L_0x00fb:
        r2 = move-exception;
    L_0x00fc:
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ all -> 0x01b3 }
        goto L_0x011b;
    L_0x0100:
        r5 = move-exception;
        r2 = r8;
        goto L_0x0104;
    L_0x0103:
        r5 = move-exception;
    L_0x0104:
        org.telegram.messenger.FileLog.e(r5);	 Catch:{ all -> 0x010f }
        if (r2 == 0) goto L_0x011b;
    L_0x0109:
        r2.close();	 Catch:{ Exception -> 0x010d }
        goto L_0x011b;
    L_0x010d:
        r2 = move-exception;
        goto L_0x00fc;
    L_0x010f:
        r1 = move-exception;
        if (r2 == 0) goto L_0x011a;
    L_0x0112:
        r2.close();	 Catch:{ Exception -> 0x0116 }
        goto L_0x011a;
    L_0x0116:
        r2 = move-exception;
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ all -> 0x01b3 }
    L_0x011a:
        throw r1;	 Catch:{ all -> 0x01b3 }
    L_0x011b:
        r2 = wallpaper;	 Catch:{ all -> 0x01b3 }
        if (r2 != 0) goto L_0x01a7;
    L_0x011f:
        r8 = getSelectedBackgroundId();	 Catch:{ all -> 0x0196 }
        r2 = "selectedPattern";
        r10 = r1.getLong(r2, r6);	 Catch:{ all -> 0x0196 }
        r2 = "selectedColor";
        r1 = r1.getInt(r2, r4);	 Catch:{ all -> 0x0196 }
        r12 = 1000001; // 0xvar_ float:1.4013E-39 double:4.94066E-318;
        r2 = NUM; // 0x7var_ float:1.7944643E38 double:1.052935519E-314;
        r5 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1));
        if (r5 != 0) goto L_0x0148;
    L_0x0139:
        r5 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0197 }
        r5 = r5.getResources();	 Catch:{ all -> 0x0197 }
        r2 = r5.getDrawable(r2);	 Catch:{ all -> 0x0197 }
        wallpaper = r2;	 Catch:{ all -> 0x0197 }
        isCustomTheme = r4;	 Catch:{ all -> 0x0197 }
        goto L_0x0197;
    L_0x0148:
        r12 = -1;
        r5 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1));
        if (r5 == 0) goto L_0x0158;
    L_0x014e:
        r12 = -100;
        r5 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1));
        if (r5 < 0) goto L_0x0158;
    L_0x0154:
        r5 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1));
        if (r5 <= 0) goto L_0x0197;
    L_0x0158:
        if (r1 == 0) goto L_0x0166;
    L_0x015a:
        r5 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1));
        if (r5 != 0) goto L_0x0166;
    L_0x015e:
        r2 = new android.graphics.drawable.ColorDrawable;	 Catch:{ all -> 0x0197 }
        r2.<init>(r1);	 Catch:{ all -> 0x0197 }
        wallpaper = r2;	 Catch:{ all -> 0x0197 }
        goto L_0x0197;
    L_0x0166:
        r5 = new java.io.File;	 Catch:{ all -> 0x0197 }
        r6 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();	 Catch:{ all -> 0x0197 }
        r7 = "wallpaper.jpg";
        r5.<init>(r6, r7);	 Catch:{ all -> 0x0197 }
        r5.length();	 Catch:{ all -> 0x0197 }
        r6 = r5.exists();	 Catch:{ all -> 0x0197 }
        if (r6 == 0) goto L_0x0187;
    L_0x017a:
        r2 = r5.getAbsolutePath();	 Catch:{ all -> 0x0197 }
        r2 = android.graphics.drawable.Drawable.createFromPath(r2);	 Catch:{ all -> 0x0197 }
        wallpaper = r2;	 Catch:{ all -> 0x0197 }
        isCustomTheme = r3;	 Catch:{ all -> 0x0197 }
        goto L_0x0197;
    L_0x0187:
        r5 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0197 }
        r5 = r5.getResources();	 Catch:{ all -> 0x0197 }
        r2 = r5.getDrawable(r2);	 Catch:{ all -> 0x0197 }
        wallpaper = r2;	 Catch:{ all -> 0x0197 }
        isCustomTheme = r4;	 Catch:{ all -> 0x0197 }
        goto L_0x0197;
    L_0x0196:
        r1 = 0;
    L_0x0197:
        r2 = wallpaper;	 Catch:{ all -> 0x01b3 }
        if (r2 != 0) goto L_0x01a7;
    L_0x019b:
        if (r1 != 0) goto L_0x01a0;
    L_0x019d:
        r1 = -2693905; // 0xffffffffffd6e4ef float:NaN double:NaN;
    L_0x01a0:
        r2 = new android.graphics.drawable.ColorDrawable;	 Catch:{ all -> 0x01b3 }
        r2.<init>(r1);	 Catch:{ all -> 0x01b3 }
        wallpaper = r2;	 Catch:{ all -> 0x01b3 }
    L_0x01a7:
        r1 = wallpaper;	 Catch:{ all -> 0x01b3 }
        calcBackgroundColor(r1, r3);	 Catch:{ all -> 0x01b3 }
        r1 = org.telegram.ui.ActionBar.-$$Lambda$Theme$DY2qfmfPrWuHZxqvtvAPoFIux_Y.INSTANCE;	 Catch:{ all -> 0x01b3 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);	 Catch:{ all -> 0x01b3 }
        monitor-exit(r0);	 Catch:{ all -> 0x01b3 }
        return;
    L_0x01b3:
        r1 = move-exception;
        monitor-exit(r0);	 Catch:{ all -> 0x01b3 }
        goto L_0x01b7;
    L_0x01b6:
        throw r1;
    L_0x01b7:
        goto L_0x01b6;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.lambda$loadWallpaper$7():void");
    }

    static /* synthetic */ void lambda$null$6() {
        applyChatServiceMessageColor();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewWallpapper, new Object[0]);
    }

    /* JADX WARNING: Removed duplicated region for block: B:45:0x00bb A:{SYNTHETIC, Splitter:B:45:0x00bb} */
    public static android.graphics.drawable.Drawable getThemedWallpaper(boolean r9) {
        /*
        r0 = currentColors;
        r1 = "chat_wallpaper";
        r0 = r0.get(r1);
        r0 = (java.lang.Integer) r0;
        r1 = 0;
        r2 = 1;
        if (r0 == 0) goto L_0x003b;
    L_0x000e:
        r9 = currentColors;
        r3 = "chat_wallpaper_gradient_to";
        r9 = r9.get(r3);
        r9 = (java.lang.Integer) r9;
        if (r9 != 0) goto L_0x0024;
    L_0x001a:
        r9 = new android.graphics.drawable.ColorDrawable;
        r0 = r0.intValue();
        r9.<init>(r0);
        return r9;
    L_0x0024:
        r3 = new org.telegram.ui.Components.BackgroundGradientDrawable;
        r4 = android.graphics.drawable.GradientDrawable.Orientation.BL_TR;
        r5 = 2;
        r5 = new int[r5];
        r0 = r0.intValue();
        r5[r1] = r0;
        r9 = r9.intValue();
        r5[r2] = r9;
        r3.<init>(r4, r5);
        return r3;
    L_0x003b:
        r0 = themedWallpaperFileOffset;
        r3 = 0;
        if (r0 <= 0) goto L_0x00d0;
    L_0x0040:
        r0 = currentTheme;
        r4 = r0.pathToFile;
        if (r4 != 0) goto L_0x004a;
    L_0x0046:
        r0 = r0.assetName;
        if (r0 == 0) goto L_0x00d0;
    L_0x004a:
        r0 = currentTheme;	 Catch:{ all -> 0x00b4 }
        r0 = r0.assetName;	 Catch:{ all -> 0x00b4 }
        if (r0 == 0) goto L_0x0059;
    L_0x0050:
        r0 = currentTheme;	 Catch:{ all -> 0x00b4 }
        r0 = r0.assetName;	 Catch:{ all -> 0x00b4 }
        r0 = getAssetFile(r0);	 Catch:{ all -> 0x00b4 }
        goto L_0x0062;
    L_0x0059:
        r0 = new java.io.File;	 Catch:{ all -> 0x00b4 }
        r4 = currentTheme;	 Catch:{ all -> 0x00b4 }
        r4 = r4.pathToFile;	 Catch:{ all -> 0x00b4 }
        r0.<init>(r4);	 Catch:{ all -> 0x00b4 }
    L_0x0062:
        r4 = new java.io.FileInputStream;	 Catch:{ all -> 0x00b4 }
        r4.<init>(r0);	 Catch:{ all -> 0x00b4 }
        r0 = r4.getChannel();	 Catch:{ all -> 0x00b2 }
        r5 = themedWallpaperFileOffset;	 Catch:{ all -> 0x00b2 }
        r5 = (long) r5;	 Catch:{ all -> 0x00b2 }
        r0.position(r5);	 Catch:{ all -> 0x00b2 }
        r0 = new android.graphics.BitmapFactory$Options;	 Catch:{ all -> 0x00b2 }
        r0.<init>();	 Catch:{ all -> 0x00b2 }
        if (r9 == 0) goto L_0x0096;
    L_0x0078:
        r0.inJustDecodeBounds = r2;	 Catch:{ all -> 0x00b2 }
        r9 = r0.outWidth;	 Catch:{ all -> 0x00b2 }
        r9 = (float) r9;	 Catch:{ all -> 0x00b2 }
        r5 = r0.outHeight;	 Catch:{ all -> 0x00b2 }
        r5 = (float) r5;	 Catch:{ all -> 0x00b2 }
        r6 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ all -> 0x00b2 }
    L_0x0086:
        r7 = (float) r6;	 Catch:{ all -> 0x00b2 }
        r8 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1));
        if (r8 > 0) goto L_0x008f;
    L_0x008b:
        r7 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r7 <= 0) goto L_0x0096;
    L_0x008f:
        r2 = r2 * 2;
        r7 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r9 = r9 / r7;
        r5 = r5 / r7;
        goto L_0x0086;
    L_0x0096:
        r0.inJustDecodeBounds = r1;	 Catch:{ all -> 0x00b2 }
        r0.inSampleSize = r2;	 Catch:{ all -> 0x00b2 }
        r9 = android.graphics.BitmapFactory.decodeStream(r4, r3, r0);	 Catch:{ all -> 0x00b2 }
        if (r9 == 0) goto L_0x00ae;
    L_0x00a0:
        r0 = new android.graphics.drawable.BitmapDrawable;	 Catch:{ all -> 0x00b2 }
        r0.<init>(r9);	 Catch:{ all -> 0x00b2 }
        r4.close();	 Catch:{ Exception -> 0x00a9 }
        goto L_0x00ad;
    L_0x00a9:
        r9 = move-exception;
        org.telegram.messenger.FileLog.e(r9);
    L_0x00ad:
        return r0;
    L_0x00ae:
        r4.close();	 Catch:{ Exception -> 0x00bf }
        goto L_0x00d0;
    L_0x00b2:
        r9 = move-exception;
        goto L_0x00b6;
    L_0x00b4:
        r9 = move-exception;
        r4 = r3;
    L_0x00b6:
        org.telegram.messenger.FileLog.e(r9);	 Catch:{ all -> 0x00c4 }
        if (r4 == 0) goto L_0x00d0;
    L_0x00bb:
        r4.close();	 Catch:{ Exception -> 0x00bf }
        goto L_0x00d0;
    L_0x00bf:
        r9 = move-exception;
        org.telegram.messenger.FileLog.e(r9);
        goto L_0x00d0;
    L_0x00c4:
        r9 = move-exception;
        if (r4 == 0) goto L_0x00cf;
    L_0x00c7:
        r4.close();	 Catch:{ Exception -> 0x00cb }
        goto L_0x00cf;
    L_0x00cb:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x00cf:
        throw r9;
    L_0x00d0:
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.getThemedWallpaper(boolean):android.graphics.drawable.Drawable");
    }

    public static long getSelectedBackgroundId() {
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        String str = "selectedBackground";
        long j = (long) globalMainSettings.getInt(str, 1000001);
        String str2 = "selectedBackground2";
        if (j != 1000001) {
            globalMainSettings.edit().putLong(str2, j).remove(str).commit();
        }
        long j2 = globalMainSettings.getLong(str2, 1000001);
        if (!hasWallpaperFromTheme() || globalMainSettings.getBoolean("overrideThemeWallpaper", false)) {
            if (j2 == -2) {
                return 1000001;
            }
            return j2;
        } else if (TextUtils.isEmpty(themedWallpaperLink)) {
            return -2;
        } else {
            return j2;
        }
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
