package org.telegram.ui.ActionBar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.StateSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.messenger.time.SunDate;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.ThemeEditorView;

public class Theme {
    public static final int ACTION_BAR_AUDIO_SELECTOR_COLOR = 788529152;
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
    private static final int LIGHT_SENSOR_THEME_SWITCH_DELAY = 1800;
    private static final int LIGHT_SENSOR_THEME_SWITCH_NEAR_DELAY = 12000;
    private static final int LIGHT_SENSOR_THEME_SWITCH_NEAR_THRESHOLD = 12000;
    private static final float MAXIMUM_LUX_BREAKPOINT = 500.0f;
    private static Method StateListDrawable_getStateDrawableMethod = null;
    private static SensorEventListener ambientSensorListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        public void onSensorChanged(SensorEvent sensorEvent) {
            sensorEvent = sensorEvent.values[0];
            if (sensorEvent <= 0.0f) {
                sensorEvent = NUM;
            }
            if (!ApplicationLoader.mainInterfacePaused) {
                if (ApplicationLoader.isScreenOn) {
                    if (sensorEvent > Theme.MAXIMUM_LUX_BREAKPOINT) {
                        Theme.lastBrightnessValue = 1.0f;
                    } else {
                        Theme.lastBrightnessValue = ((float) Math.ceil((9.932299613952637d * Math.log((double) sensorEvent)) + 27.05900001525879d)) / 100.0f;
                    }
                    if (Theme.lastBrightnessValue > Theme.autoNightBrighnessThreshold) {
                        if (Theme.switchNightRunnableScheduled != null) {
                            Theme.switchNightRunnableScheduled = false;
                            AndroidUtilities.cancelRunOnUIThread(Theme.switchNightBrightnessRunnable);
                        }
                        if (Theme.switchDayRunnableScheduled == null) {
                            Theme.switchDayRunnableScheduled = true;
                            AndroidUtilities.runOnUIThread(Theme.switchDayBrightnessRunnable, Theme.getAutoNightSwitchThemeDelay());
                        }
                    } else if (MediaController.getInstance().isRecordingOrListeningByProximity() == null) {
                        if (Theme.switchDayRunnableScheduled != null) {
                            Theme.switchDayRunnableScheduled = false;
                            AndroidUtilities.cancelRunOnUIThread(Theme.switchDayBrightnessRunnable);
                        }
                        if (Theme.switchNightRunnableScheduled == null) {
                            Theme.switchNightRunnableScheduled = true;
                            AndroidUtilities.runOnUIThread(Theme.switchNightBrightnessRunnable, Theme.getAutoNightSwitchThemeDelay());
                        }
                    }
                }
            }
        }
    };
    public static float autoNightBrighnessThreshold = 0.25f;
    public static String autoNightCityName = "";
    public static int autoNightDayEndTime = 480;
    public static int autoNightDayStartTime = 1320;
    public static int autoNightLastSunCheckDay = -1;
    public static double autoNightLocationLatitude = 10000.0d;
    public static double autoNightLocationLongitude = 10000.0d;
    public static boolean autoNightScheduleByLocation = false;
    public static int autoNightSunriseTime = 480;
    public static int autoNightSunsetTime = 1320;
    public static Paint avatar_backgroundPaint = null;
    public static Drawable avatar_broadcastDrawable = null;
    public static Drawable avatar_photoDrawable = null;
    public static Drawable avatar_savedDrawable = null;
    private static boolean canStartHolidayAnimation = false;
    public static Paint chat_actionBackgroundPaint = null;
    public static TextPaint chat_actionTextPaint = null;
    public static TextPaint chat_adminPaint = null;
    public static Drawable[] chat_attachButtonDrawables = new Drawable[8];
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
    public static Drawable[][] chat_fileStatesDrawable = ((Drawable[][]) Array.newInstance(Drawable.class, new int[]{10, 2}));
    public static TextPaint chat_forwardNamePaint = null;
    public static TextPaint chat_gamePaint = null;
    public static Drawable chat_goIconDrawable = null;
    public static TextPaint chat_infoPaint = null;
    public static Drawable chat_inlineResultAudio = null;
    public static Drawable chat_inlineResultFile = null;
    public static Drawable chat_inlineResultLocation = null;
    public static TextPaint chat_instantViewPaint = null;
    public static Paint chat_instantViewRectPaint = null;
    public static Drawable[][] chat_ivStatesDrawable = ((Drawable[][]) Array.newInstance(Drawable.class, new int[]{4, 2}));
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
    public static Drawable chat_msgCallUpRedDrawable = null;
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
    public static Drawable chat_msgOutBroadcastDrawable = null;
    public static Drawable chat_msgOutCallDrawable = null;
    public static Drawable chat_msgOutCallSelectedDrawable = null;
    public static Drawable chat_msgOutCheckDrawable = null;
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
    public static PorterDuffColorFilter colorPressedFilter = null;
    private static int currentColor = 0;
    private static HashMap<String, Integer> currentColors = new HashMap();
    private static ThemeInfo currentDayTheme = null;
    private static ThemeInfo currentNightTheme = null;
    private static int currentSelectedColor = 0;
    private static ThemeInfo currentTheme = null;
    private static HashMap<String, Integer> defaultColors = new HashMap();
    private static ThemeInfo defaultTheme = null;
    public static Drawable dialogs_botDrawable = null;
    public static Drawable dialogs_broadcastDrawable = null;
    public static Drawable dialogs_checkDrawable = null;
    public static Drawable dialogs_clockDrawable = null;
    public static Paint dialogs_countGrayPaint = null;
    public static Paint dialogs_countPaint = null;
    public static TextPaint dialogs_countTextPaint = null;
    public static Drawable dialogs_errorDrawable = null;
    public static Paint dialogs_errorPaint = null;
    public static Drawable dialogs_groupDrawable = null;
    public static Drawable dialogs_halfCheckDrawable = null;
    private static Drawable dialogs_holidayDrawable = null;
    private static int dialogs_holidayDrawableOffsetX = 0;
    private static int dialogs_holidayDrawableOffsetY = 0;
    public static Drawable dialogs_lockDrawable = null;
    public static Drawable dialogs_mentionDrawable = null;
    public static TextPaint dialogs_messagePaint = null;
    public static TextPaint dialogs_messagePrintingPaint = null;
    public static Drawable dialogs_muteDrawable = null;
    public static TextPaint dialogs_nameEncryptedPaint = null;
    public static TextPaint dialogs_namePaint = null;
    public static TextPaint dialogs_offlinePaint = null;
    public static TextPaint dialogs_onlinePaint = null;
    public static Drawable dialogs_pinnedDrawable = null;
    public static Paint dialogs_pinnedPaint = null;
    public static Paint dialogs_tabletSeletedPaint = null;
    public static TextPaint dialogs_timePaint = null;
    public static Drawable dialogs_verifiedCheckDrawable = null;
    public static Drawable dialogs_verifiedDrawable = null;
    public static Paint dividerPaint = null;
    private static HashMap<String, String> fallbackKeys = new HashMap();
    private static boolean isCustomTheme = false;
    public static final String key_actionBarActionModeDefault = "actionBarActionModeDefault";
    public static final String key_actionBarActionModeDefaultIcon = "actionBarActionModeDefaultIcon";
    public static final String key_actionBarActionModeDefaultSelector = "actionBarActionModeDefaultSelector";
    public static final String key_actionBarActionModeDefaultTop = "actionBarActionModeDefaultTop";
    public static final String key_actionBarDefault = "actionBarDefault";
    public static final String key_actionBarDefaultIcon = "actionBarDefaultIcon";
    public static final String key_actionBarDefaultSearch = "actionBarDefaultSearch";
    public static final String key_actionBarDefaultSearchPlaceholder = "actionBarDefaultSearchPlaceholder";
    public static final String key_actionBarDefaultSelector = "actionBarDefaultSelector";
    public static final String key_actionBarDefaultSubmenuBackground = "actionBarDefaultSubmenuBackground";
    public static final String key_actionBarDefaultSubmenuItem = "actionBarDefaultSubmenuItem";
    public static final String key_actionBarDefaultSubtitle = "actionBarDefaultSubtitle";
    public static final String key_actionBarDefaultTitle = "actionBarDefaultTitle";
    public static final String key_actionBarWhiteSelector = "actionBarWhiteSelector";
    public static final String key_avatar_actionBarIconBlue = "avatar_actionBarIconBlue";
    public static final String key_avatar_actionBarIconCyan = "avatar_actionBarIconCyan";
    public static final String key_avatar_actionBarIconGreen = "avatar_actionBarIconGreen";
    public static final String key_avatar_actionBarIconOrange = "avatar_actionBarIconOrange";
    public static final String key_avatar_actionBarIconPink = "avatar_actionBarIconPink";
    public static final String key_avatar_actionBarIconRed = "avatar_actionBarIconRed";
    public static final String key_avatar_actionBarIconViolet = "avatar_actionBarIconViolet";
    public static final String key_avatar_actionBarSelectorBlue = "avatar_actionBarSelectorBlue";
    public static final String key_avatar_actionBarSelectorCyan = "avatar_actionBarSelectorCyan";
    public static final String key_avatar_actionBarSelectorGreen = "avatar_actionBarSelectorGreen";
    public static final String key_avatar_actionBarSelectorOrange = "avatar_actionBarSelectorOrange";
    public static final String key_avatar_actionBarSelectorPink = "avatar_actionBarSelectorPink";
    public static final String key_avatar_actionBarSelectorRed = "avatar_actionBarSelectorRed";
    public static final String key_avatar_actionBarSelectorViolet = "avatar_actionBarSelectorViolet";
    public static final String key_avatar_backgroundActionBarBlue = "avatar_backgroundActionBarBlue";
    public static final String key_avatar_backgroundActionBarCyan = "avatar_backgroundActionBarCyan";
    public static final String key_avatar_backgroundActionBarGreen = "avatar_backgroundActionBarGreen";
    public static final String key_avatar_backgroundActionBarOrange = "avatar_backgroundActionBarOrange";
    public static final String key_avatar_backgroundActionBarPink = "avatar_backgroundActionBarPink";
    public static final String key_avatar_backgroundActionBarRed = "avatar_backgroundActionBarRed";
    public static final String key_avatar_backgroundActionBarViolet = "avatar_backgroundActionBarViolet";
    public static final String key_avatar_backgroundBlue = "avatar_backgroundBlue";
    public static final String key_avatar_backgroundCyan = "avatar_backgroundCyan";
    public static final String key_avatar_backgroundGreen = "avatar_backgroundGreen";
    public static final String key_avatar_backgroundGroupCreateSpanBlue = "avatar_backgroundGroupCreateSpanBlue";
    public static final String key_avatar_backgroundInProfileBlue = "avatar_backgroundInProfileBlue";
    public static final String key_avatar_backgroundInProfileCyan = "avatar_backgroundInProfileCyan";
    public static final String key_avatar_backgroundInProfileGreen = "avatar_backgroundInProfileGreen";
    public static final String key_avatar_backgroundInProfileOrange = "avatar_backgroundInProfileOrange";
    public static final String key_avatar_backgroundInProfilePink = "avatar_backgroundInProfilePink";
    public static final String key_avatar_backgroundInProfileRed = "avatar_backgroundInProfileRed";
    public static final String key_avatar_backgroundInProfileViolet = "avatar_backgroundInProfileViolet";
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
    public static final String key_avatar_subtitleInProfileCyan = "avatar_subtitleInProfileCyan";
    public static final String key_avatar_subtitleInProfileGreen = "avatar_subtitleInProfileGreen";
    public static final String key_avatar_subtitleInProfileOrange = "avatar_subtitleInProfileOrange";
    public static final String key_avatar_subtitleInProfilePink = "avatar_subtitleInProfilePink";
    public static final String key_avatar_subtitleInProfileRed = "avatar_subtitleInProfileRed";
    public static final String key_avatar_subtitleInProfileViolet = "avatar_subtitleInProfileViolet";
    public static final String key_avatar_text = "avatar_text";
    public static final String key_calls_callReceivedGreenIcon = "calls_callReceivedGreenIcon";
    public static final String key_calls_callReceivedRedIcon = "calls_callReceivedRedIcon";
    public static final String key_calls_ratingStar = "calls_ratingStar";
    public static final String key_calls_ratingStarSelected = "calls_ratingStarSelected";
    public static final String key_changephoneinfo_image = "changephoneinfo_image";
    public static final String key_chat_addContact = "chat_addContact";
    public static final String key_chat_adminSelectedText = "chat_adminSelectedText";
    public static final String key_chat_adminText = "chat_adminText";
    public static final String key_chat_botButtonText = "chat_botButtonText";
    public static final String key_chat_botKeyboardButtonBackground = "chat_botKeyboardButtonBackground";
    public static final String key_chat_botKeyboardButtonBackgroundPressed = "chat_botKeyboardButtonBackgroundPressed";
    public static final String key_chat_botKeyboardButtonText = "chat_botKeyboardButtonText";
    public static final String key_chat_botProgress = "chat_botProgress";
    public static final String key_chat_botSwitchToInlineText = "chat_botSwitchToInlineText";
    public static final String key_chat_editDoneIcon = "chat_editDoneIcon";
    public static final String key_chat_emojiPanelBackground = "chat_emojiPanelBackground";
    public static final String key_chat_emojiPanelBackspace = "chat_emojiPanelBackspace";
    public static final String key_chat_emojiPanelEmptyText = "chat_emojiPanelEmptyText";
    public static final String key_chat_emojiPanelIcon = "chat_emojiPanelIcon";
    public static final String key_chat_emojiPanelIconSelected = "chat_emojiPanelIconSelected";
    public static final String key_chat_emojiPanelIconSelector = "chat_emojiPanelIconSelector";
    public static final String key_chat_emojiPanelMasksIcon = "chat_emojiPanelMasksIcon";
    public static final String key_chat_emojiPanelMasksIconSelected = "chat_emojiPanelMasksIconSelected";
    public static final String key_chat_emojiPanelNewTrending = "chat_emojiPanelNewTrending";
    public static final String key_chat_emojiPanelShadowLine = "chat_emojiPanelShadowLine";
    public static final String key_chat_emojiPanelStickerPackSelector = "chat_emojiPanelStickerPackSelector";
    public static final String key_chat_emojiPanelStickerSetName = "chat_emojiPanelStickerSetName";
    public static final String key_chat_emojiPanelStickerSetNameIcon = "chat_emojiPanelStickerSetNameIcon";
    public static final String key_chat_emojiPanelTrendingDescription = "chat_emojiPanelTrendingDescription";
    public static final String key_chat_emojiPanelTrendingTitle = "chat_emojiPanelTrendingTitle";
    public static final String key_chat_emojiSearchBackground = "chat_emojiSearchBackground";
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
    public static final String key_chat_inAudioPerfomerText = "chat_inAudioPerfomerText";
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
    public static final String key_chat_inMenu = "chat_inMenu";
    public static final String key_chat_inMenuSelected = "chat_inMenuSelected";
    public static final String key_chat_inPreviewInstantSelectedText = "chat_inPreviewInstantSelectedText";
    public static final String key_chat_inPreviewInstantText = "chat_inPreviewInstantText";
    public static final String key_chat_inPreviewLine = "chat_inPreviewLine";
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
    public static final String key_chat_inVenueNameText = "chat_inVenueNameText";
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
    public static final String key_chat_messagePanelHint = "chat_messagePanelHint";
    public static final String key_chat_messagePanelIcons = "chat_messagePanelIcons";
    public static final String key_chat_messagePanelSend = "chat_messagePanelSend";
    public static final String key_chat_messagePanelShadow = "chat_messagePanelShadow";
    public static final String key_chat_messagePanelText = "chat_messagePanelText";
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
    public static final String key_chat_outAudioPerfomerText = "chat_outAudioPerfomerText";
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
    public static final String key_chat_outSentCheckSelected = "chat_outSentCheckSelected";
    public static final String key_chat_outSentClock = "chat_outSentClock";
    public static final String key_chat_outSentClockSelected = "chat_outSentClockSelected";
    public static final String key_chat_outSiteNameText = "chat_outSiteNameText";
    public static final String key_chat_outTimeSelectedText = "chat_outTimeSelectedText";
    public static final String key_chat_outTimeText = "chat_outTimeText";
    public static final String key_chat_outVenueInfoSelectedText = "chat_outVenueInfoSelectedText";
    public static final String key_chat_outVenueInfoText = "chat_outVenueInfoText";
    public static final String key_chat_outVenueNameText = "chat_outVenueNameText";
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
    public static final String key_chats_actionBackground = "chats_actionBackground";
    public static final String key_chats_actionIcon = "chats_actionIcon";
    public static final String key_chats_actionMessage = "chats_actionMessage";
    public static final String key_chats_actionPressedBackground = "chats_actionPressedBackground";
    public static final String key_chats_attachMessage = "chats_attachMessage";
    public static final String key_chats_date = "chats_date";
    public static final String key_chats_draft = "chats_draft";
    public static final String key_chats_menuBackground = "chats_menuBackground";
    public static final String key_chats_menuCloud = "chats_menuCloud";
    public static final String key_chats_menuCloudBackgroundCats = "chats_menuCloudBackgroundCats";
    public static final String key_chats_menuItemCheck = "chats_menuItemCheck";
    public static final String key_chats_menuItemIcon = "chats_menuItemIcon";
    public static final String key_chats_menuItemText = "chats_menuItemText";
    public static final String key_chats_menuName = "chats_menuName";
    public static final String key_chats_menuPhone = "chats_menuPhone";
    public static final String key_chats_menuPhoneCats = "chats_menuPhoneCats";
    public static final String key_chats_menuTopShadow = "chats_menuTopShadow";
    public static final String key_chats_message = "chats_message";
    public static final String key_chats_muteIcon = "chats_muteIcon";
    public static final String key_chats_name = "chats_name";
    public static final String key_chats_nameIcon = "chats_nameIcon";
    public static final String key_chats_nameMessage = "chats_nameMessage";
    public static final String key_chats_pinnedIcon = "chats_pinnedIcon";
    public static final String key_chats_pinnedOverlay = "chats_pinnedOverlay";
    public static final String key_chats_secretIcon = "chats_secretIcon";
    public static final String key_chats_secretName = "chats_secretName";
    public static final String key_chats_sentCheck = "chats_sentCheck";
    public static final String key_chats_sentClock = "chats_sentClock";
    public static final String key_chats_sentError = "chats_sentError";
    public static final String key_chats_sentErrorIcon = "chats_sentErrorIcon";
    public static final String key_chats_tabletSelectedOverlay = "chats_tabletSelectedOverlay";
    public static final String key_chats_unreadCounter = "chats_unreadCounter";
    public static final String key_chats_unreadCounterMuted = "chats_unreadCounterMuted";
    public static final String key_chats_unreadCounterText = "chats_unreadCounterText";
    public static final String key_chats_verifiedBackground = "chats_verifiedBackground";
    public static final String key_chats_verifiedCheck = "chats_verifiedCheck";
    public static final String key_checkbox = "checkbox";
    public static final String key_checkboxCheck = "checkboxCheck";
    public static final String key_checkboxSquareBackground = "checkboxSquareBackground";
    public static final String key_checkboxSquareCheck = "checkboxSquareCheck";
    public static final String key_checkboxSquareDisabled = "checkboxSquareDisabled";
    public static final String key_checkboxSquareUnchecked = "checkboxSquareUnchecked";
    public static final String key_contacts_inviteBackground = "contacts_inviteBackground";
    public static final String key_contacts_inviteText = "contacts_inviteText";
    public static final String key_contextProgressInner1 = "contextProgressInner1";
    public static final String key_contextProgressInner2 = "contextProgressInner2";
    public static final String key_contextProgressInner3 = "contextProgressInner3";
    public static final String key_contextProgressOuter1 = "contextProgressOuter1";
    public static final String key_contextProgressOuter2 = "contextProgressOuter2";
    public static final String key_contextProgressOuter3 = "contextProgressOuter3";
    public static final String key_dialogBackground = "dialogBackground";
    public static final String key_dialogBackgroundGray = "dialogBackgroundGray";
    public static final String key_dialogBadgeBackground = "dialogBadgeBackground";
    public static final String key_dialogBadgeText = "dialogBadgeText";
    public static final String key_dialogButton = "dialogButton";
    public static final String key_dialogButtonSelector = "dialogButtonSelector";
    public static final String key_dialogCheckboxSquareBackground = "dialogCheckboxSquareBackground";
    public static final String key_dialogCheckboxSquareCheck = "dialogCheckboxSquareCheck";
    public static final String key_dialogCheckboxSquareDisabled = "dialogCheckboxSquareDisabled";
    public static final String key_dialogCheckboxSquareUnchecked = "dialogCheckboxSquareUnchecked";
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
    public static final String key_dialogRoundCheckBox = "dialogRoundCheckBox";
    public static final String key_dialogRoundCheckBoxCheck = "dialogRoundCheckBoxCheck";
    public static final String key_dialogScrollGlow = "dialogScrollGlow";
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
    public static final String key_dialogTopBackground = "dialogTopBackground";
    public static final String key_dialog_liveLocationProgress = "location_liveLocationProgress";
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
    public static final String key_groupcreate_checkbox = "groupcreate_checkbox";
    public static final String key_groupcreate_checkboxCheck = "groupcreate_checkboxCheck";
    public static final String key_groupcreate_cursor = "groupcreate_cursor";
    public static final String key_groupcreate_hintText = "groupcreate_hintText";
    public static final String key_groupcreate_offlineText = "groupcreate_offlineText";
    public static final String key_groupcreate_onlineText = "groupcreate_onlineText";
    public static final String key_groupcreate_sectionShadow = "groupcreate_sectionShadow";
    public static final String key_groupcreate_sectionText = "groupcreate_sectionText";
    public static final String key_groupcreate_spanBackground = "groupcreate_spanBackground";
    public static final String key_groupcreate_spanText = "groupcreate_spanText";
    public static final String key_inappPlayerBackground = "inappPlayerBackground";
    public static final String key_inappPlayerClose = "inappPlayerClose";
    public static final String key_inappPlayerPerformer = "inappPlayerPerformer";
    public static final String key_inappPlayerPlayPause = "inappPlayerPlayPause";
    public static final String key_inappPlayerTitle = "inappPlayerTitle";
    public static final String key_listSelector = "listSelectorSDK21";
    public static final String key_location_liveLocationProgress = "location_liveLocationProgress";
    public static final String key_location_markerX = "location_markerX";
    public static final String key_location_placeLocationBackground = "location_placeLocationBackground";
    public static final String key_location_sendLiveLocationBackground = "location_sendLiveLocationBackground";
    public static final String key_location_sendLocationBackground = "location_sendLocationBackground";
    public static final String key_location_sendLocationIcon = "location_sendLocationIcon";
    public static final String key_login_progressInner = "login_progressInner";
    public static final String key_login_progressOuter = "login_progressOuter";
    public static final String key_musicPicker_buttonBackground = "musicPicker_buttonBackground";
    public static final String key_musicPicker_buttonIcon = "musicPicker_buttonIcon";
    public static final String key_musicPicker_checkbox = "musicPicker_checkbox";
    public static final String key_musicPicker_checkboxCheck = "musicPicker_checkboxCheck";
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
    public static final String key_profile_adminIcon = "profile_adminIcon";
    public static final String key_profile_creatorIcon = "profile_creatorIcon";
    public static final String key_profile_title = "profile_title";
    public static final String key_profile_verifiedBackground = "profile_verifiedBackground";
    public static final String key_profile_verifiedCheck = "profile_verifiedCheck";
    public static final String key_progressCircle = "progressCircle";
    public static final String key_radioBackground = "radioBackground";
    public static final String key_radioBackgroundChecked = "radioBackgroundChecked";
    public static final String key_returnToCallBackground = "returnToCallBackground";
    public static final String key_returnToCallText = "returnToCallText";
    public static final String key_sessions_devicesImage = "sessions_devicesImage";
    public static final String key_sharedMedia_linkPlaceholder = "sharedMedia_linkPlaceholder";
    public static final String key_sharedMedia_linkPlaceholderText = "sharedMedia_linkPlaceholderText";
    public static final String key_sharedMedia_startStopLoadIcon = "sharedMedia_startStopLoadIcon";
    public static final String key_stickers_menu = "stickers_menu";
    public static final String key_stickers_menuSelector = "stickers_menuSelector";
    public static final String key_switchThumb = "switchThumb";
    public static final String key_switchThumbChecked = "switchThumbChecked";
    public static final String key_switchTrack = "switchTrack";
    public static final String key_switchTrackChecked = "switchTrackChecked";
    public static final String key_windowBackgroundGray = "windowBackgroundGray";
    public static final String key_windowBackgroundGrayShadow = "windowBackgroundGrayShadow";
    public static final String key_windowBackgroundWhite = "windowBackgroundWhite";
    public static final String key_windowBackgroundWhiteBlackText = "windowBackgroundWhiteBlackText";
    public static final String key_windowBackgroundWhiteBlueHeader = "windowBackgroundWhiteBlueHeader";
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
    public static String[] keys_avatar_actionBarIcon = new String[]{key_avatar_actionBarIconRed, key_avatar_actionBarIconOrange, key_avatar_actionBarIconViolet, key_avatar_actionBarIconGreen, key_avatar_actionBarIconCyan, key_avatar_actionBarIconBlue, key_avatar_actionBarIconPink};
    public static String[] keys_avatar_actionBarSelector = new String[]{key_avatar_actionBarSelectorRed, key_avatar_actionBarSelectorOrange, key_avatar_actionBarSelectorViolet, key_avatar_actionBarSelectorGreen, key_avatar_actionBarSelectorCyan, key_avatar_actionBarSelectorBlue, key_avatar_actionBarSelectorPink};
    public static String[] keys_avatar_background = new String[]{key_avatar_backgroundRed, key_avatar_backgroundOrange, key_avatar_backgroundViolet, key_avatar_backgroundGreen, key_avatar_backgroundCyan, key_avatar_backgroundBlue, key_avatar_backgroundPink};
    public static String[] keys_avatar_backgroundActionBar = new String[]{key_avatar_backgroundActionBarRed, key_avatar_backgroundActionBarOrange, key_avatar_backgroundActionBarViolet, key_avatar_backgroundActionBarGreen, key_avatar_backgroundActionBarCyan, key_avatar_backgroundActionBarBlue, key_avatar_backgroundActionBarPink};
    public static String[] keys_avatar_backgroundInProfile = new String[]{key_avatar_backgroundInProfileRed, key_avatar_backgroundInProfileOrange, key_avatar_backgroundInProfileViolet, key_avatar_backgroundInProfileGreen, key_avatar_backgroundInProfileCyan, key_avatar_backgroundInProfileBlue, key_avatar_backgroundInProfilePink};
    public static String[] keys_avatar_nameInMessage = new String[]{key_avatar_nameInMessageRed, key_avatar_nameInMessageOrange, key_avatar_nameInMessageViolet, key_avatar_nameInMessageGreen, key_avatar_nameInMessageCyan, key_avatar_nameInMessageBlue, key_avatar_nameInMessagePink};
    public static String[] keys_avatar_subtitleInProfile = new String[]{key_avatar_subtitleInProfileRed, key_avatar_subtitleInProfileOrange, key_avatar_subtitleInProfileViolet, key_avatar_subtitleInProfileGreen, key_avatar_subtitleInProfileCyan, key_avatar_subtitleInProfileBlue, key_avatar_subtitleInProfilePink};
    private static float lastBrightnessValue = 1.0f;
    private static long lastHolidayCheckTime;
    private static long lastThemeSwitchTime;
    private static Sensor lightSensor;
    private static boolean lightSensorRegistered;
    public static Paint linkSelectionPaint;
    public static Drawable listSelector;
    private static Paint maskPaint = new Paint(1);
    private static ArrayList<ThemeInfo> otherThemes = new ArrayList();
    private static ThemeInfo previousTheme;
    public static TextPaint profile_aboutTextPaint;
    public static Drawable profile_verifiedCheckDrawable;
    public static Drawable profile_verifiedDrawable;
    public static int selectedAutoNightType;
    private static int selectedColor;
    private static SensorManager sensorManager;
    private static int serviceMessageColor;
    private static int serviceSelectedMessageColor;
    private static Runnable switchDayBrightnessRunnable = new C07591();
    private static boolean switchDayRunnableScheduled;
    private static Runnable switchNightBrightnessRunnable = new C07602();
    private static boolean switchNightRunnableScheduled;
    private static final Object sync = new Object();
    private static Drawable themedWallpaper;
    private static int themedWallpaperFileOffset;
    public static ArrayList<ThemeInfo> themes = new ArrayList();
    private static HashMap<String, ThemeInfo> themesDict = new HashMap();
    private static Drawable wallpaper;
    private static final Object wallpaperSync = new Object();

    /* renamed from: org.telegram.ui.ActionBar.Theme$1 */
    static class C07591 implements Runnable {
        C07591() {
        }

        public void run() {
            Theme.switchDayRunnableScheduled = false;
            Theme.applyDayNightThemeMaybe(false);
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.Theme$2 */
    static class C07602 implements Runnable {
        C07602() {
        }

        public void run() {
            Theme.switchNightRunnableScheduled = false;
            Theme.applyDayNightThemeMaybe(true);
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.Theme$3 */
    static class C07613 implements Runnable {
        C07613() {
        }

        public void run() {
            Theme.checkAutoNightThemeConditions();
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.Theme$4 */
    static class C07624 extends StateListDrawable {
        C07624() {
        }

        public boolean selectDrawable(int i) {
            if (VERSION.SDK_INT >= 21) {
                return super.selectDrawable(i);
            }
            Drawable access$300 = Theme.getStateDrawable(this, i);
            ColorFilter colorFilter = null;
            if (access$300 instanceof BitmapDrawable) {
                colorFilter = ((BitmapDrawable) access$300).getPaint().getColorFilter();
            } else if (access$300 instanceof NinePatchDrawable) {
                colorFilter = ((NinePatchDrawable) access$300).getPaint().getColorFilter();
            }
            i = super.selectDrawable(i);
            if (colorFilter != null) {
                access$300.setColorFilter(colorFilter);
            }
            return i;
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.Theme$5 */
    static class C07635 extends StateListDrawable {
        C07635() {
        }

        public boolean selectDrawable(int i) {
            if (VERSION.SDK_INT >= 21) {
                return super.selectDrawable(i);
            }
            Drawable access$300 = Theme.getStateDrawable(this, i);
            ColorFilter colorFilter = null;
            if (access$300 instanceof BitmapDrawable) {
                colorFilter = ((BitmapDrawable) access$300).getPaint().getColorFilter();
            } else if (access$300 instanceof NinePatchDrawable) {
                colorFilter = ((NinePatchDrawable) access$300).getPaint().getColorFilter();
            }
            i = super.selectDrawable(i);
            if (colorFilter != null) {
                access$300.setColorFilter(colorFilter);
            }
            return i;
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.Theme$6 */
    static class C07646 extends StateListDrawable {
        C07646() {
        }

        public boolean selectDrawable(int i) {
            if (VERSION.SDK_INT >= 21) {
                return super.selectDrawable(i);
            }
            Drawable access$300 = Theme.getStateDrawable(this, i);
            ColorFilter colorFilter = null;
            if (access$300 instanceof BitmapDrawable) {
                colorFilter = ((BitmapDrawable) access$300).getPaint().getColorFilter();
            } else if (access$300 instanceof NinePatchDrawable) {
                colorFilter = ((NinePatchDrawable) access$300).getPaint().getColorFilter();
            }
            i = super.selectDrawable(i);
            if (colorFilter != null) {
                access$300.setColorFilter(colorFilter);
            }
            return i;
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.Theme$7 */
    static class C07657 extends Drawable {
        public int getOpacity() {
            return 0;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        C07657() {
        }

        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            canvas.drawCircle((float) bounds.centerX(), (float) bounds.centerY(), (float) AndroidUtilities.dp(18.0f), Theme.maskPaint);
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.Theme$8 */
    static class C07668 implements Comparator<ThemeInfo> {
        C07668() {
        }

        public int compare(ThemeInfo themeInfo, ThemeInfo themeInfo2) {
            if (themeInfo.pathToFile == null && themeInfo.assetName == null) {
                return -1;
            }
            if (themeInfo2.pathToFile == null && themeInfo2.assetName == null) {
                return 1;
            }
            return themeInfo.name.compareTo(themeInfo2.name);
        }
    }

    public static class ThemeInfo {
        public String assetName;
        public String name;
        public String pathToFile;

        public JSONObject getSaveJson() {
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("name", this.name);
                jSONObject.put("path", this.pathToFile);
                return jSONObject;
            } catch (Throwable e) {
                FileLog.m3e(e);
                return null;
            }
        }

        public String getName() {
            if ("Default".equals(this.name)) {
                return LocaleController.getString("Default", C0446R.string.Default);
            }
            if ("Blue".equals(this.name)) {
                return LocaleController.getString("ThemeBlue", C0446R.string.ThemeBlue);
            }
            if ("Dark".equals(this.name)) {
                return LocaleController.getString("ThemeDark", C0446R.string.ThemeDark);
            }
            return this.name;
        }

        public static ThemeInfo createWithJson(JSONObject jSONObject) {
            if (jSONObject == null) {
                return null;
            }
            try {
                ThemeInfo themeInfo = new ThemeInfo();
                themeInfo.name = jSONObject.getString("name");
                themeInfo.pathToFile = jSONObject.getString("path");
                return themeInfo;
            } catch (Throwable e) {
                FileLog.m3e(e);
                return null;
            }
        }

        public static ThemeInfo createWithString(String str) {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            str = str.split("\\|");
            if (str.length != 2) {
                return null;
            }
            ThemeInfo themeInfo = new ThemeInfo();
            themeInfo.name = str[0];
            themeInfo.pathToFile = str[1];
            return themeInfo;
        }
    }

    static {
        ThemeInfo createWithJson;
        defaultColors.put(key_dialogBackground, Integer.valueOf(-1));
        defaultColors.put(key_dialogBackgroundGray, Integer.valueOf(-986896));
        defaultColors.put(key_dialogTextBlack, Integer.valueOf(-14606047));
        defaultColors.put(key_dialogTextLink, Integer.valueOf(-14255946));
        defaultColors.put(key_dialogLinkSelection, Integer.valueOf(862104035));
        defaultColors.put(key_dialogTextRed, Integer.valueOf(-3319206));
        defaultColors.put(key_dialogTextBlue, Integer.valueOf(-13660983));
        defaultColors.put(key_dialogTextBlue2, Integer.valueOf(-12940081));
        defaultColors.put(key_dialogTextBlue3, Integer.valueOf(-12664327));
        defaultColors.put(key_dialogTextBlue4, Integer.valueOf(-15095832));
        defaultColors.put(key_dialogTextGray, Integer.valueOf(-13333567));
        defaultColors.put(key_dialogTextGray2, Integer.valueOf(-9079435));
        defaultColors.put(key_dialogTextGray3, Integer.valueOf(-6710887));
        defaultColors.put(key_dialogTextGray4, Integer.valueOf(-5000269));
        defaultColors.put(key_dialogTextHint, Integer.valueOf(-6842473));
        defaultColors.put(key_dialogIcon, Integer.valueOf(-7697782));
        defaultColors.put(key_dialogGrayLine, Integer.valueOf(-2960686));
        defaultColors.put(key_dialogTopBackground, Integer.valueOf(-9456923));
        defaultColors.put(key_dialogInputField, Integer.valueOf(-2368549));
        defaultColors.put(key_dialogInputFieldActivated, Integer.valueOf(-13129232));
        defaultColors.put(key_dialogCheckboxSquareBackground, Integer.valueOf(-12345121));
        defaultColors.put(key_dialogCheckboxSquareCheck, Integer.valueOf(-1));
        defaultColors.put(key_dialogCheckboxSquareUnchecked, Integer.valueOf(-9211021));
        defaultColors.put(key_dialogCheckboxSquareDisabled, Integer.valueOf(-5197648));
        defaultColors.put(key_dialogRadioBackground, Integer.valueOf(-5000269));
        defaultColors.put(key_dialogRadioBackgroundChecked, Integer.valueOf(-13129232));
        defaultColors.put(key_dialogProgressCircle, Integer.valueOf(-11371101));
        defaultColors.put(key_dialogLineProgress, Integer.valueOf(-11371101));
        defaultColors.put(key_dialogLineProgressBackground, Integer.valueOf(-2368549));
        defaultColors.put(key_dialogButton, Integer.valueOf(-11955764));
        defaultColors.put(key_dialogButtonSelector, Integer.valueOf(251658240));
        defaultColors.put(key_dialogScrollGlow, Integer.valueOf(-657673));
        defaultColors.put(key_dialogRoundCheckBox, Integer.valueOf(-12664327));
        defaultColors.put(key_dialogRoundCheckBoxCheck, Integer.valueOf(-1));
        defaultColors.put(key_dialogBadgeBackground, Integer.valueOf(-12664327));
        defaultColors.put(key_dialogBadgeText, Integer.valueOf(-1));
        defaultColors.put(key_windowBackgroundWhite, Integer.valueOf(-1));
        defaultColors.put(key_progressCircle, Integer.valueOf(-11371101));
        defaultColors.put(key_windowBackgroundWhiteGrayIcon, Integer.valueOf(-9211021));
        defaultColors.put(key_windowBackgroundWhiteBlueText, Integer.valueOf(-12876608));
        defaultColors.put(key_windowBackgroundWhiteBlueText2, Integer.valueOf(-13333567));
        defaultColors.put(key_windowBackgroundWhiteBlueText3, Integer.valueOf(-14255946));
        defaultColors.put(key_windowBackgroundWhiteBlueText4, Integer.valueOf(-11697229));
        defaultColors.put(key_windowBackgroundWhiteBlueText5, Integer.valueOf(-11759926));
        defaultColors.put(key_windowBackgroundWhiteBlueText6, Integer.valueOf(-12940081));
        defaultColors.put(key_windowBackgroundWhiteBlueText7, Integer.valueOf(-13141330));
        defaultColors.put(key_windowBackgroundWhiteGreenText, Integer.valueOf(-14248148));
        defaultColors.put(key_windowBackgroundWhiteGreenText2, Integer.valueOf(-13129447));
        defaultColors.put(key_windowBackgroundWhiteRedText, Integer.valueOf(-3319206));
        defaultColors.put(key_windowBackgroundWhiteRedText2, Integer.valueOf(-2404015));
        defaultColors.put(key_windowBackgroundWhiteRedText3, Integer.valueOf(-2995895));
        defaultColors.put(key_windowBackgroundWhiteRedText4, Integer.valueOf(-3198928));
        defaultColors.put(key_windowBackgroundWhiteRedText5, Integer.valueOf(-1229511));
        defaultColors.put(key_windowBackgroundWhiteRedText6, Integer.valueOf(-39322));
        defaultColors.put(key_windowBackgroundWhiteGrayText, Integer.valueOf(-5723992));
        defaultColors.put(key_windowBackgroundWhiteGrayText2, Integer.valueOf(-7697782));
        defaultColors.put(key_windowBackgroundWhiteGrayText3, Integer.valueOf(-6710887));
        defaultColors.put(key_windowBackgroundWhiteGrayText4, Integer.valueOf(-8355712));
        defaultColors.put(key_windowBackgroundWhiteGrayText5, Integer.valueOf(-6052957));
        defaultColors.put(key_windowBackgroundWhiteGrayText6, Integer.valueOf(-9079435));
        defaultColors.put(key_windowBackgroundWhiteGrayText7, Integer.valueOf(-3750202));
        defaultColors.put(key_windowBackgroundWhiteGrayText8, Integer.valueOf(-9605774));
        defaultColors.put(key_windowBackgroundWhiteGrayLine, Integer.valueOf(-2368549));
        defaultColors.put(key_windowBackgroundWhiteBlackText, Integer.valueOf(-14606047));
        defaultColors.put(key_windowBackgroundWhiteHintText, Integer.valueOf(-6842473));
        defaultColors.put(key_windowBackgroundWhiteValueText, Integer.valueOf(-13660983));
        defaultColors.put(key_windowBackgroundWhiteLinkText, Integer.valueOf(-14255946));
        defaultColors.put(key_windowBackgroundWhiteLinkSelection, Integer.valueOf(862104035));
        defaultColors.put(key_windowBackgroundWhiteBlueHeader, Integer.valueOf(-12676913));
        defaultColors.put(key_windowBackgroundWhiteInputField, Integer.valueOf(-2368549));
        defaultColors.put(key_windowBackgroundWhiteInputFieldActivated, Integer.valueOf(-13129232));
        defaultColors.put(key_switchThumb, Integer.valueOf(-1184275));
        defaultColors.put(key_switchTrack, Integer.valueOf(-3684409));
        defaultColors.put(key_switchThumbChecked, Integer.valueOf(-12211217));
        defaultColors.put(key_switchTrackChecked, Integer.valueOf(-6236422));
        defaultColors.put(key_checkboxSquareBackground, Integer.valueOf(-12345121));
        defaultColors.put(key_checkboxSquareCheck, Integer.valueOf(-1));
        defaultColors.put(key_checkboxSquareUnchecked, Integer.valueOf(-9211021));
        defaultColors.put(key_checkboxSquareDisabled, Integer.valueOf(-5197648));
        defaultColors.put(key_listSelector, Integer.valueOf(251658240));
        defaultColors.put(key_radioBackground, Integer.valueOf(-5000269));
        defaultColors.put(key_radioBackgroundChecked, Integer.valueOf(-13129232));
        defaultColors.put(key_windowBackgroundGray, Integer.valueOf(-986896));
        defaultColors.put(key_windowBackgroundGrayShadow, Integer.valueOf(ACTION_BAR_VIDEO_EDIT_COLOR));
        defaultColors.put(key_emptyListPlaceholder, Integer.valueOf(-6974059));
        defaultColors.put(key_divider, Integer.valueOf(-2500135));
        defaultColors.put(key_graySection, Integer.valueOf(-855310));
        defaultColors.put(key_contextProgressInner1, Integer.valueOf(-4202506));
        defaultColors.put(key_contextProgressOuter1, Integer.valueOf(-13920542));
        defaultColors.put(key_contextProgressInner2, Integer.valueOf(-4202506));
        defaultColors.put(key_contextProgressOuter2, Integer.valueOf(-1));
        defaultColors.put(key_contextProgressInner3, Integer.valueOf(-5000269));
        defaultColors.put(key_contextProgressOuter3, Integer.valueOf(-1));
        defaultColors.put(key_fastScrollActive, Integer.valueOf(-11361317));
        defaultColors.put(key_fastScrollInactive, Integer.valueOf(-10263709));
        defaultColors.put(key_fastScrollText, Integer.valueOf(-1));
        defaultColors.put(key_avatar_text, Integer.valueOf(-1));
        defaultColors.put(key_avatar_backgroundSaved, Integer.valueOf(-10043398));
        defaultColors.put(key_avatar_backgroundRed, Integer.valueOf(-1743531));
        defaultColors.put(key_avatar_backgroundOrange, Integer.valueOf(-881592));
        defaultColors.put(key_avatar_backgroundViolet, Integer.valueOf(-7436818));
        defaultColors.put(key_avatar_backgroundGreen, Integer.valueOf(-8992691));
        defaultColors.put(key_avatar_backgroundCyan, Integer.valueOf(-10502443));
        defaultColors.put(key_avatar_backgroundBlue, Integer.valueOf(-11232035));
        defaultColors.put(key_avatar_backgroundPink, Integer.valueOf(-887654));
        defaultColors.put(key_avatar_backgroundGroupCreateSpanBlue, Integer.valueOf(-4204822));
        defaultColors.put(key_avatar_backgroundInProfileRed, Integer.valueOf(-2592923));
        defaultColors.put(key_avatar_backgroundInProfileOrange, Integer.valueOf(-615071));
        defaultColors.put(key_avatar_backgroundInProfileViolet, Integer.valueOf(-7570990));
        defaultColors.put(key_avatar_backgroundInProfileGreen, Integer.valueOf(-9981091));
        defaultColors.put(key_avatar_backgroundInProfileCyan, Integer.valueOf(-11099461));
        defaultColors.put(key_avatar_backgroundInProfileBlue, Integer.valueOf(-11500111));
        defaultColors.put(key_avatar_backgroundInProfilePink, Integer.valueOf(-819290));
        defaultColors.put(key_avatar_backgroundActionBarRed, Integer.valueOf(-3514282));
        defaultColors.put(key_avatar_backgroundActionBarOrange, Integer.valueOf(-947900));
        defaultColors.put(key_avatar_backgroundActionBarViolet, Integer.valueOf(-8557884));
        defaultColors.put(key_avatar_backgroundActionBarGreen, Integer.valueOf(-11099828));
        defaultColors.put(key_avatar_backgroundActionBarCyan, Integer.valueOf(-12283220));
        defaultColors.put(key_avatar_backgroundActionBarBlue, Integer.valueOf(-10907718));
        defaultColors.put(key_avatar_backgroundActionBarPink, Integer.valueOf(-10907718));
        defaultColors.put(key_avatar_subtitleInProfileRed, Integer.valueOf(-406587));
        defaultColors.put(key_avatar_subtitleInProfileOrange, Integer.valueOf(-139832));
        defaultColors.put(key_avatar_subtitleInProfileViolet, Integer.valueOf(-3291923));
        defaultColors.put(key_avatar_subtitleInProfileGreen, Integer.valueOf(-4133446));
        defaultColors.put(key_avatar_subtitleInProfileCyan, Integer.valueOf(-4660496));
        defaultColors.put(key_avatar_subtitleInProfileBlue, Integer.valueOf(-2626822));
        defaultColors.put(key_avatar_subtitleInProfilePink, Integer.valueOf(-2626822));
        defaultColors.put(key_avatar_nameInMessageRed, Integer.valueOf(-3516848));
        defaultColors.put(key_avatar_nameInMessageOrange, Integer.valueOf(-2589911));
        defaultColors.put(key_avatar_nameInMessageViolet, Integer.valueOf(-11627828));
        defaultColors.put(key_avatar_nameInMessageGreen, Integer.valueOf(-11488718));
        defaultColors.put(key_avatar_nameInMessageCyan, Integer.valueOf(-12406360));
        defaultColors.put(key_avatar_nameInMessageBlue, Integer.valueOf(-11627828));
        defaultColors.put(key_avatar_nameInMessagePink, Integer.valueOf(-11627828));
        defaultColors.put(key_avatar_actionBarSelectorRed, Integer.valueOf(-4437183));
        defaultColors.put(key_avatar_actionBarSelectorOrange, Integer.valueOf(-1674199));
        defaultColors.put(key_avatar_actionBarSelectorViolet, Integer.valueOf(-9216066));
        defaultColors.put(key_avatar_actionBarSelectorGreen, Integer.valueOf(-12020419));
        defaultColors.put(key_avatar_actionBarSelectorCyan, Integer.valueOf(-13007715));
        defaultColors.put(key_avatar_actionBarSelectorBlue, Integer.valueOf(-11959891));
        defaultColors.put(key_avatar_actionBarSelectorPink, Integer.valueOf(-11959891));
        defaultColors.put(key_avatar_actionBarIconRed, Integer.valueOf(-1));
        defaultColors.put(key_avatar_actionBarIconOrange, Integer.valueOf(-1));
        defaultColors.put(key_avatar_actionBarIconViolet, Integer.valueOf(-1));
        defaultColors.put(key_avatar_actionBarIconGreen, Integer.valueOf(-1));
        defaultColors.put(key_avatar_actionBarIconCyan, Integer.valueOf(-1));
        defaultColors.put(key_avatar_actionBarIconBlue, Integer.valueOf(-1));
        defaultColors.put(key_avatar_actionBarIconPink, Integer.valueOf(-1));
        defaultColors.put(key_actionBarDefault, Integer.valueOf(-11371101));
        defaultColors.put(key_actionBarDefaultIcon, Integer.valueOf(-1));
        defaultColors.put(key_actionBarActionModeDefault, Integer.valueOf(-1));
        defaultColors.put(key_actionBarActionModeDefaultTop, Integer.valueOf(-NUM));
        defaultColors.put(key_actionBarActionModeDefaultIcon, Integer.valueOf(-9211021));
        defaultColors.put(key_actionBarDefaultTitle, Integer.valueOf(-1));
        defaultColors.put(key_actionBarDefaultSubtitle, Integer.valueOf(-2758409));
        defaultColors.put(key_actionBarDefaultSelector, Integer.valueOf(-12554860));
        defaultColors.put(key_actionBarWhiteSelector, Integer.valueOf(ACTION_BAR_AUDIO_SELECTOR_COLOR));
        defaultColors.put(key_actionBarDefaultSearch, Integer.valueOf(-1));
        defaultColors.put(key_actionBarDefaultSearchPlaceholder, Integer.valueOf(-NUM));
        defaultColors.put(key_actionBarDefaultSubmenuItem, Integer.valueOf(-14606047));
        defaultColors.put(key_actionBarDefaultSubmenuBackground, Integer.valueOf(-1));
        defaultColors.put(key_actionBarActionModeDefaultSelector, Integer.valueOf(-986896));
        defaultColors.put(key_chats_unreadCounter, Integer.valueOf(-11613090));
        defaultColors.put(key_chats_unreadCounterMuted, Integer.valueOf(-3684409));
        defaultColors.put(key_chats_unreadCounterText, Integer.valueOf(-1));
        defaultColors.put(key_chats_name, Integer.valueOf(-14606047));
        defaultColors.put(key_chats_secretName, Integer.valueOf(-16734706));
        defaultColors.put(key_chats_secretIcon, Integer.valueOf(-15093466));
        defaultColors.put(key_chats_nameIcon, Integer.valueOf(-14408668));
        defaultColors.put(key_chats_pinnedIcon, Integer.valueOf(-5723992));
        defaultColors.put(key_chats_message, Integer.valueOf(-7368817));
        defaultColors.put(key_chats_draft, Integer.valueOf(-2274503));
        defaultColors.put(key_chats_nameMessage, Integer.valueOf(-11697229));
        defaultColors.put(key_chats_attachMessage, Integer.valueOf(-11697229));
        defaultColors.put(key_chats_actionMessage, Integer.valueOf(-11697229));
        defaultColors.put(key_chats_date, Integer.valueOf(-6710887));
        defaultColors.put(key_chats_pinnedOverlay, Integer.valueOf(134217728));
        defaultColors.put(key_chats_tabletSelectedOverlay, Integer.valueOf(251658240));
        defaultColors.put(key_chats_sentCheck, Integer.valueOf(-12146122));
        defaultColors.put(key_chats_sentClock, Integer.valueOf(-9061026));
        defaultColors.put(key_chats_sentError, Integer.valueOf(-2796974));
        defaultColors.put(key_chats_sentErrorIcon, Integer.valueOf(-1));
        defaultColors.put(key_chats_verifiedBackground, Integer.valueOf(-13391642));
        defaultColors.put(key_chats_verifiedCheck, Integer.valueOf(-1));
        defaultColors.put(key_chats_muteIcon, Integer.valueOf(-5723992));
        defaultColors.put(key_chats_menuBackground, Integer.valueOf(-1));
        defaultColors.put(key_chats_menuItemText, Integer.valueOf(-12303292));
        defaultColors.put(key_chats_menuItemCheck, Integer.valueOf(-10907718));
        defaultColors.put(key_chats_menuItemIcon, Integer.valueOf(-9211021));
        defaultColors.put(key_chats_menuName, Integer.valueOf(-1));
        defaultColors.put(key_chats_menuPhone, Integer.valueOf(-1));
        defaultColors.put(key_chats_menuPhoneCats, Integer.valueOf(-4004353));
        defaultColors.put(key_chats_menuCloud, Integer.valueOf(-1));
        defaultColors.put(key_chats_menuCloudBackgroundCats, Integer.valueOf(-12420183));
        defaultColors.put(key_chats_actionIcon, Integer.valueOf(-1));
        defaultColors.put(key_chats_actionBackground, Integer.valueOf(-9788978));
        defaultColors.put(key_chats_actionPressedBackground, Integer.valueOf(-11038014));
        defaultColors.put(key_chat_lockIcon, Integer.valueOf(-1));
        defaultColors.put(key_chat_muteIcon, Integer.valueOf(-5124893));
        defaultColors.put(key_chat_inBubble, Integer.valueOf(-1));
        defaultColors.put(key_chat_inBubbleSelected, Integer.valueOf(-1902337));
        defaultColors.put(key_chat_inBubbleShadow, Integer.valueOf(-14862509));
        defaultColors.put(key_chat_outBubble, Integer.valueOf(-1048610));
        defaultColors.put(key_chat_outBubbleSelected, Integer.valueOf(-2820676));
        defaultColors.put(key_chat_outBubbleShadow, Integer.valueOf(-14781172));
        defaultColors.put(key_chat_messageTextIn, Integer.valueOf(ACTION_BAR_VIDEO_EDIT_COLOR));
        defaultColors.put(key_chat_messageTextOut, Integer.valueOf(ACTION_BAR_VIDEO_EDIT_COLOR));
        defaultColors.put(key_chat_messageLinkIn, Integer.valueOf(-14255946));
        defaultColors.put(key_chat_messageLinkOut, Integer.valueOf(-14255946));
        defaultColors.put(key_chat_serviceText, Integer.valueOf(-1));
        defaultColors.put(key_chat_serviceLink, Integer.valueOf(-1));
        defaultColors.put(key_chat_serviceIcon, Integer.valueOf(-1));
        defaultColors.put(key_chat_mediaTimeBackground, Integer.valueOf(NUM));
        defaultColors.put(key_chat_outSentCheck, Integer.valueOf(-10637232));
        defaultColors.put(key_chat_outSentCheckSelected, Integer.valueOf(-10637232));
        defaultColors.put(key_chat_outSentClock, Integer.valueOf(-9061026));
        defaultColors.put(key_chat_outSentClockSelected, Integer.valueOf(-9061026));
        defaultColors.put(key_chat_inSentClock, Integer.valueOf(-6182221));
        defaultColors.put(key_chat_inSentClockSelected, Integer.valueOf(-7094838));
        defaultColors.put(key_chat_mediaSentCheck, Integer.valueOf(-1));
        defaultColors.put(key_chat_mediaSentClock, Integer.valueOf(-1));
        defaultColors.put(key_chat_inViews, Integer.valueOf(-6182221));
        defaultColors.put(key_chat_inViewsSelected, Integer.valueOf(-7094838));
        defaultColors.put(key_chat_outViews, Integer.valueOf(-9522601));
        defaultColors.put(key_chat_outViewsSelected, Integer.valueOf(-9522601));
        defaultColors.put(key_chat_mediaViews, Integer.valueOf(-1));
        defaultColors.put(key_chat_inMenu, Integer.valueOf(-4801083));
        defaultColors.put(key_chat_inMenuSelected, Integer.valueOf(-6766130));
        defaultColors.put(key_chat_outMenu, Integer.valueOf(-7221634));
        defaultColors.put(key_chat_outMenuSelected, Integer.valueOf(-7221634));
        defaultColors.put(key_chat_mediaMenu, Integer.valueOf(-1));
        defaultColors.put(key_chat_outInstant, Integer.valueOf(-11162801));
        defaultColors.put(key_chat_outInstantSelected, Integer.valueOf(-12019389));
        defaultColors.put(key_chat_inInstant, Integer.valueOf(-12940081));
        defaultColors.put(key_chat_inInstantSelected, Integer.valueOf(-13600331));
        defaultColors.put(key_chat_sentError, Integer.valueOf(-2411211));
        defaultColors.put(key_chat_sentErrorIcon, Integer.valueOf(-1));
        defaultColors.put(key_chat_selectedBackground, Integer.valueOf(NUM));
        defaultColors.put(key_chat_previewDurationText, Integer.valueOf(-1));
        defaultColors.put(key_chat_previewGameText, Integer.valueOf(-1));
        defaultColors.put(key_chat_inPreviewInstantText, Integer.valueOf(-12940081));
        defaultColors.put(key_chat_outPreviewInstantText, Integer.valueOf(-11162801));
        defaultColors.put(key_chat_inPreviewInstantSelectedText, Integer.valueOf(-13600331));
        defaultColors.put(key_chat_outPreviewInstantSelectedText, Integer.valueOf(-12019389));
        defaultColors.put(key_chat_secretTimeText, Integer.valueOf(-1776928));
        defaultColors.put(key_chat_stickerNameText, Integer.valueOf(-1));
        defaultColors.put(key_chat_botButtonText, Integer.valueOf(-1));
        defaultColors.put(key_chat_botProgress, Integer.valueOf(-1));
        defaultColors.put(key_chat_inForwardedNameText, Integer.valueOf(-13072697));
        defaultColors.put(key_chat_outForwardedNameText, Integer.valueOf(-11162801));
        defaultColors.put(key_chat_inViaBotNameText, Integer.valueOf(-12940081));
        defaultColors.put(key_chat_outViaBotNameText, Integer.valueOf(-11162801));
        defaultColors.put(key_chat_stickerViaBotNameText, Integer.valueOf(-1));
        defaultColors.put(key_chat_inReplyLine, Integer.valueOf(-10903592));
        defaultColors.put(key_chat_outReplyLine, Integer.valueOf(-9520791));
        defaultColors.put(key_chat_stickerReplyLine, Integer.valueOf(-1));
        defaultColors.put(key_chat_inReplyNameText, Integer.valueOf(-12940081));
        defaultColors.put(key_chat_outReplyNameText, Integer.valueOf(-11162801));
        defaultColors.put(key_chat_stickerReplyNameText, Integer.valueOf(-1));
        defaultColors.put(key_chat_inReplyMessageText, Integer.valueOf(ACTION_BAR_VIDEO_EDIT_COLOR));
        defaultColors.put(key_chat_outReplyMessageText, Integer.valueOf(ACTION_BAR_VIDEO_EDIT_COLOR));
        defaultColors.put(key_chat_inReplyMediaMessageText, Integer.valueOf(-6182221));
        defaultColors.put(key_chat_outReplyMediaMessageText, Integer.valueOf(-10112933));
        defaultColors.put(key_chat_inReplyMediaMessageSelectedText, Integer.valueOf(-7752511));
        defaultColors.put(key_chat_outReplyMediaMessageSelectedText, Integer.valueOf(-10112933));
        defaultColors.put(key_chat_stickerReplyMessageText, Integer.valueOf(-1));
        defaultColors.put(key_chat_inPreviewLine, Integer.valueOf(-9390872));
        defaultColors.put(key_chat_outPreviewLine, Integer.valueOf(-7812741));
        defaultColors.put(key_chat_inSiteNameText, Integer.valueOf(-12940081));
        defaultColors.put(key_chat_outSiteNameText, Integer.valueOf(-11162801));
        defaultColors.put(key_chat_inContactNameText, Integer.valueOf(-11625772));
        defaultColors.put(key_chat_outContactNameText, Integer.valueOf(-11162801));
        defaultColors.put(key_chat_inContactPhoneText, Integer.valueOf(-13683656));
        defaultColors.put(key_chat_outContactPhoneText, Integer.valueOf(-13286860));
        defaultColors.put(key_chat_mediaProgress, Integer.valueOf(-1));
        defaultColors.put(key_chat_inAudioProgress, Integer.valueOf(-1));
        defaultColors.put(key_chat_outAudioProgress, Integer.valueOf(-1048610));
        defaultColors.put(key_chat_inAudioSelectedProgress, Integer.valueOf(-1902337));
        defaultColors.put(key_chat_outAudioSelectedProgress, Integer.valueOf(-2820676));
        defaultColors.put(key_chat_mediaTimeText, Integer.valueOf(-1));
        defaultColors.put(key_chat_inTimeText, Integer.valueOf(-6182221));
        defaultColors.put(key_chat_outTimeText, Integer.valueOf(-9391780));
        defaultColors.put(key_chat_adminText, Integer.valueOf(-4143413));
        defaultColors.put(key_chat_adminSelectedText, Integer.valueOf(-7752511));
        defaultColors.put(key_chat_inTimeSelectedText, Integer.valueOf(-7752511));
        defaultColors.put(key_chat_outTimeSelectedText, Integer.valueOf(-9391780));
        defaultColors.put(key_chat_inAudioPerfomerText, Integer.valueOf(-13683656));
        defaultColors.put(key_chat_outAudioPerfomerText, Integer.valueOf(-13286860));
        defaultColors.put(key_chat_inAudioTitleText, Integer.valueOf(-11625772));
        defaultColors.put(key_chat_outAudioTitleText, Integer.valueOf(-11162801));
        defaultColors.put(key_chat_inAudioDurationText, Integer.valueOf(-6182221));
        defaultColors.put(key_chat_outAudioDurationText, Integer.valueOf(-10112933));
        defaultColors.put(key_chat_inAudioDurationSelectedText, Integer.valueOf(-7752511));
        defaultColors.put(key_chat_outAudioDurationSelectedText, Integer.valueOf(-10112933));
        defaultColors.put(key_chat_inAudioSeekbar, Integer.valueOf(-1774864));
        defaultColors.put(key_chat_inAudioCacheSeekbar, Integer.valueOf(NUM));
        defaultColors.put(key_chat_outAudioSeekbar, Integer.valueOf(-4463700));
        defaultColors.put(key_chat_outAudioCacheSeekbar, Integer.valueOf(NUM));
        defaultColors.put(key_chat_inAudioSeekbarSelected, Integer.valueOf(-4399384));
        defaultColors.put(key_chat_outAudioSeekbarSelected, Integer.valueOf(-5644906));
        defaultColors.put(key_chat_inAudioSeekbarFill, Integer.valueOf(-9259544));
        defaultColors.put(key_chat_outAudioSeekbarFill, Integer.valueOf(-8863118));
        defaultColors.put(key_chat_inVoiceSeekbar, Integer.valueOf(-2169365));
        defaultColors.put(key_chat_outVoiceSeekbar, Integer.valueOf(-4463700));
        defaultColors.put(key_chat_inVoiceSeekbarSelected, Integer.valueOf(-4399384));
        defaultColors.put(key_chat_outVoiceSeekbarSelected, Integer.valueOf(-5644906));
        defaultColors.put(key_chat_inVoiceSeekbarFill, Integer.valueOf(-9259544));
        defaultColors.put(key_chat_outVoiceSeekbarFill, Integer.valueOf(-8863118));
        defaultColors.put(key_chat_inFileProgress, Integer.valueOf(-1314571));
        defaultColors.put(key_chat_outFileProgress, Integer.valueOf(-2427453));
        defaultColors.put(key_chat_inFileProgressSelected, Integer.valueOf(-3413258));
        defaultColors.put(key_chat_outFileProgressSelected, Integer.valueOf(-3806041));
        defaultColors.put(key_chat_inFileNameText, Integer.valueOf(-11625772));
        defaultColors.put(key_chat_outFileNameText, Integer.valueOf(-11162801));
        defaultColors.put(key_chat_inFileInfoText, Integer.valueOf(-6182221));
        defaultColors.put(key_chat_outFileInfoText, Integer.valueOf(-10112933));
        defaultColors.put(key_chat_inFileInfoSelectedText, Integer.valueOf(-7752511));
        defaultColors.put(key_chat_outFileInfoSelectedText, Integer.valueOf(-10112933));
        defaultColors.put(key_chat_inFileBackground, Integer.valueOf(-1314571));
        defaultColors.put(key_chat_outFileBackground, Integer.valueOf(-2427453));
        defaultColors.put(key_chat_inFileBackgroundSelected, Integer.valueOf(-3413258));
        defaultColors.put(key_chat_outFileBackgroundSelected, Integer.valueOf(-3806041));
        defaultColors.put(key_chat_inVenueNameText, Integer.valueOf(-11625772));
        defaultColors.put(key_chat_outVenueNameText, Integer.valueOf(-11162801));
        defaultColors.put(key_chat_inVenueInfoText, Integer.valueOf(-6182221));
        defaultColors.put(key_chat_outVenueInfoText, Integer.valueOf(-10112933));
        defaultColors.put(key_chat_inVenueInfoSelectedText, Integer.valueOf(-7752511));
        defaultColors.put(key_chat_outVenueInfoSelectedText, Integer.valueOf(-10112933));
        defaultColors.put(key_chat_mediaInfoText, Integer.valueOf(-1));
        defaultColors.put(key_chat_linkSelectBackground, Integer.valueOf(862104035));
        defaultColors.put(key_chat_textSelectBackground, Integer.valueOf(NUM));
        defaultColors.put(key_chat_emojiPanelBackground, Integer.valueOf(-657673));
        defaultColors.put(key_chat_emojiSearchBackground, Integer.valueOf(-1578003));
        defaultColors.put(key_chat_emojiPanelShadowLine, Integer.valueOf(-1907225));
        defaultColors.put(key_chat_emojiPanelEmptyText, Integer.valueOf(-5723992));
        defaultColors.put(key_chat_emojiPanelIcon, Integer.valueOf(-5723992));
        defaultColors.put(key_chat_emojiPanelIconSelected, Integer.valueOf(-13920542));
        defaultColors.put(key_chat_emojiPanelStickerPackSelector, Integer.valueOf(-1907225));
        defaultColors.put(key_chat_emojiPanelIconSelector, Integer.valueOf(-13920542));
        defaultColors.put(key_chat_emojiPanelBackspace, Integer.valueOf(-5723992));
        defaultColors.put(key_chat_emojiPanelMasksIcon, Integer.valueOf(-1));
        defaultColors.put(key_chat_emojiPanelMasksIconSelected, Integer.valueOf(-10305560));
        defaultColors.put(key_chat_emojiPanelTrendingTitle, Integer.valueOf(-14606047));
        defaultColors.put(key_chat_emojiPanelStickerSetName, Integer.valueOf(-8156010));
        defaultColors.put(key_chat_emojiPanelStickerSetNameIcon, Integer.valueOf(-5130564));
        defaultColors.put(key_chat_emojiPanelTrendingDescription, Integer.valueOf(-7697782));
        defaultColors.put(key_chat_botKeyboardButtonText, Integer.valueOf(-13220017));
        defaultColors.put(key_chat_botKeyboardButtonBackground, Integer.valueOf(-1775639));
        defaultColors.put(key_chat_botKeyboardButtonBackgroundPressed, Integer.valueOf(-3354156));
        defaultColors.put(key_chat_unreadMessagesStartArrowIcon, Integer.valueOf(-6113849));
        defaultColors.put(key_chat_unreadMessagesStartText, Integer.valueOf(-11102772));
        defaultColors.put(key_chat_unreadMessagesStartBackground, Integer.valueOf(-1));
        defaultColors.put(key_chat_editDoneIcon, Integer.valueOf(-11420173));
        defaultColors.put(key_chat_inFileIcon, Integer.valueOf(-6113849));
        defaultColors.put(key_chat_inFileSelectedIcon, Integer.valueOf(-7883067));
        defaultColors.put(key_chat_outFileIcon, Integer.valueOf(-8011912));
        defaultColors.put(key_chat_outFileSelectedIcon, Integer.valueOf(-8011912));
        defaultColors.put(key_chat_inLocationBackground, Integer.valueOf(-1314571));
        defaultColors.put(key_chat_inLocationIcon, Integer.valueOf(-6113849));
        defaultColors.put(key_chat_outLocationBackground, Integer.valueOf(-2427453));
        defaultColors.put(key_chat_outLocationIcon, Integer.valueOf(-7880840));
        defaultColors.put(key_chat_inContactBackground, Integer.valueOf(-9259544));
        defaultColors.put(key_chat_inContactIcon, Integer.valueOf(-1));
        defaultColors.put(key_chat_outContactBackground, Integer.valueOf(-8863118));
        defaultColors.put(key_chat_outContactIcon, Integer.valueOf(-1048610));
        defaultColors.put(key_chat_outBroadcast, Integer.valueOf(-12146122));
        defaultColors.put(key_chat_mediaBroadcast, Integer.valueOf(-1));
        defaultColors.put(key_chat_searchPanelIcons, Integer.valueOf(-10639908));
        defaultColors.put(key_chat_searchPanelText, Integer.valueOf(-11625772));
        defaultColors.put(key_chat_secretChatStatusText, Integer.valueOf(-8421505));
        defaultColors.put(key_chat_fieldOverlayText, Integer.valueOf(-12940081));
        defaultColors.put(key_chat_stickersHintPanel, Integer.valueOf(-1));
        defaultColors.put(key_chat_replyPanelIcons, Integer.valueOf(-11032346));
        defaultColors.put(key_chat_replyPanelClose, Integer.valueOf(-5723992));
        defaultColors.put(key_chat_replyPanelName, Integer.valueOf(-12940081));
        defaultColors.put(key_chat_replyPanelMessage, Integer.valueOf(-14540254));
        defaultColors.put(key_chat_replyPanelLine, Integer.valueOf(-1513240));
        defaultColors.put(key_chat_messagePanelBackground, Integer.valueOf(-1));
        defaultColors.put(key_chat_messagePanelText, Integer.valueOf(ACTION_BAR_VIDEO_EDIT_COLOR));
        defaultColors.put(key_chat_messagePanelHint, Integer.valueOf(-5066062));
        defaultColors.put(key_chat_messagePanelShadow, Integer.valueOf(ACTION_BAR_VIDEO_EDIT_COLOR));
        defaultColors.put(key_chat_messagePanelIcons, Integer.valueOf(-5723992));
        defaultColors.put(key_chat_recordedVoicePlayPause, Integer.valueOf(-1));
        defaultColors.put(key_chat_recordedVoicePlayPausePressed, Integer.valueOf(-2495749));
        defaultColors.put(key_chat_recordedVoiceDot, Integer.valueOf(-2468275));
        defaultColors.put(key_chat_recordedVoiceBackground, Integer.valueOf(-11165981));
        defaultColors.put(key_chat_recordedVoiceProgress, Integer.valueOf(-6107400));
        defaultColors.put(key_chat_recordedVoiceProgressInner, Integer.valueOf(-1));
        defaultColors.put(key_chat_recordVoiceCancel, Integer.valueOf(-6710887));
        defaultColors.put(key_chat_messagePanelSend, Integer.valueOf(-10309397));
        defaultColors.put(key_chat_messagePanelVoiceLock, Integer.valueOf(-5987164));
        defaultColors.put(key_chat_messagePanelVoiceLockBackground, Integer.valueOf(-1));
        defaultColors.put(key_chat_messagePanelVoiceLockShadow, Integer.valueOf(ACTION_BAR_VIDEO_EDIT_COLOR));
        defaultColors.put(key_chat_recordTime, Integer.valueOf(-11711413));
        defaultColors.put(key_chat_emojiPanelNewTrending, Integer.valueOf(-11688214));
        defaultColors.put(key_chat_gifSaveHintText, Integer.valueOf(-1));
        defaultColors.put(key_chat_gifSaveHintBackground, Integer.valueOf(-871296751));
        defaultColors.put(key_chat_goDownButton, Integer.valueOf(-1));
        defaultColors.put(key_chat_goDownButtonShadow, Integer.valueOf(ACTION_BAR_VIDEO_EDIT_COLOR));
        defaultColors.put(key_chat_goDownButtonIcon, Integer.valueOf(-5723992));
        defaultColors.put(key_chat_goDownButtonCounter, Integer.valueOf(-1));
        defaultColors.put(key_chat_goDownButtonCounterBackground, Integer.valueOf(-11689240));
        defaultColors.put(key_chat_messagePanelCancelInlineBot, Integer.valueOf(-5395027));
        defaultColors.put(key_chat_messagePanelVoicePressed, Integer.valueOf(-1));
        defaultColors.put(key_chat_messagePanelVoiceBackground, Integer.valueOf(-11037236));
        defaultColors.put(key_chat_messagePanelVoiceShadow, Integer.valueOf(218103808));
        defaultColors.put(key_chat_messagePanelVoiceDelete, Integer.valueOf(-9211021));
        defaultColors.put(key_chat_messagePanelVoiceDuration, Integer.valueOf(-1));
        defaultColors.put(key_chat_inlineResultIcon, Integer.valueOf(-11037236));
        defaultColors.put(key_chat_topPanelBackground, Integer.valueOf(-1));
        defaultColors.put(key_chat_topPanelClose, Integer.valueOf(-5723992));
        defaultColors.put(key_chat_topPanelLine, Integer.valueOf(-9658414));
        defaultColors.put(key_chat_topPanelTitle, Integer.valueOf(-12940081));
        defaultColors.put(key_chat_topPanelMessage, Integer.valueOf(-6710887));
        defaultColors.put(key_chat_reportSpam, Integer.valueOf(-3188393));
        defaultColors.put(key_chat_addContact, Integer.valueOf(-11894091));
        defaultColors.put(key_chat_inLoader, Integer.valueOf(-9259544));
        defaultColors.put(key_chat_inLoaderSelected, Integer.valueOf(-10114080));
        defaultColors.put(key_chat_outLoader, Integer.valueOf(-8863118));
        defaultColors.put(key_chat_outLoaderSelected, Integer.valueOf(-9783964));
        defaultColors.put(key_chat_inLoaderPhoto, Integer.valueOf(-6113080));
        defaultColors.put(key_chat_inLoaderPhotoSelected, Integer.valueOf(-6113849));
        defaultColors.put(key_chat_inLoaderPhotoIcon, Integer.valueOf(-197380));
        defaultColors.put(key_chat_inLoaderPhotoIconSelected, Integer.valueOf(-1314571));
        defaultColors.put(key_chat_outLoaderPhoto, Integer.valueOf(-8011912));
        defaultColors.put(key_chat_outLoaderPhotoSelected, Integer.valueOf(-8538000));
        defaultColors.put(key_chat_outLoaderPhotoIcon, Integer.valueOf(-2427453));
        defaultColors.put(key_chat_outLoaderPhotoIconSelected, Integer.valueOf(-4134748));
        defaultColors.put(key_chat_mediaLoaderPhoto, Integer.valueOf(NUM));
        defaultColors.put(key_chat_mediaLoaderPhotoSelected, Integer.valueOf(ACTION_BAR_PHOTO_VIEWER_COLOR));
        defaultColors.put(key_chat_mediaLoaderPhotoIcon, Integer.valueOf(-1));
        defaultColors.put(key_chat_mediaLoaderPhotoIconSelected, Integer.valueOf(-2500135));
        defaultColors.put(key_chat_secretTimerBackground, Integer.valueOf(-868326258));
        defaultColors.put(key_chat_secretTimerText, Integer.valueOf(-1));
        defaultColors.put(key_profile_creatorIcon, Integer.valueOf(-11888682));
        defaultColors.put(key_profile_adminIcon, Integer.valueOf(-8026747));
        defaultColors.put(key_profile_actionIcon, Integer.valueOf(-9211021));
        defaultColors.put(key_profile_actionBackground, Integer.valueOf(-1));
        defaultColors.put(key_profile_actionPressedBackground, Integer.valueOf(-855310));
        defaultColors.put(key_profile_verifiedBackground, Integer.valueOf(-5056776));
        defaultColors.put(key_profile_verifiedCheck, Integer.valueOf(-11959368));
        defaultColors.put(key_profile_title, Integer.valueOf(-1));
        defaultColors.put(key_player_actionBar, Integer.valueOf(-1));
        defaultColors.put(key_player_actionBarSelector, Integer.valueOf(ACTION_BAR_AUDIO_SELECTOR_COLOR));
        defaultColors.put(key_player_actionBarTitle, Integer.valueOf(-13683656));
        defaultColors.put(key_player_actionBarTop, Integer.valueOf(-NUM));
        defaultColors.put(key_player_actionBarSubtitle, Integer.valueOf(-7697782));
        defaultColors.put(key_player_actionBarItems, Integer.valueOf(-7697782));
        defaultColors.put(key_player_background, Integer.valueOf(-1));
        defaultColors.put(key_player_time, Integer.valueOf(-7564650));
        defaultColors.put(key_player_progressBackground, Integer.valueOf(419430400));
        defaultColors.put(key_player_progressCachedBackground, Integer.valueOf(419430400));
        defaultColors.put(key_player_progress, Integer.valueOf(-14438417));
        defaultColors.put(key_player_placeholder, Integer.valueOf(-5723992));
        defaultColors.put(key_player_placeholderBackground, Integer.valueOf(-986896));
        defaultColors.put(key_player_button, Integer.valueOf(ACTION_BAR_MEDIA_PICKER_COLOR));
        defaultColors.put(key_player_buttonActive, Integer.valueOf(-11753238));
        defaultColors.put(key_files_folderIcon, Integer.valueOf(-6710887));
        defaultColors.put(key_files_folderIconBackground, Integer.valueOf(-986896));
        defaultColors.put(key_files_iconText, Integer.valueOf(-1));
        defaultColors.put(key_sessions_devicesImage, Integer.valueOf(-6908266));
        defaultColors.put(key_location_markerX, Integer.valueOf(-8355712));
        defaultColors.put(key_location_sendLocationBackground, Integer.valueOf(-9592620));
        defaultColors.put(key_location_sendLiveLocationBackground, Integer.valueOf(-39836));
        defaultColors.put(key_location_sendLocationIcon, Integer.valueOf(-1));
        defaultColors.put("location_liveLocationProgress", Integer.valueOf(-13262875));
        defaultColors.put(key_location_placeLocationBackground, Integer.valueOf(-11753238));
        defaultColors.put("location_liveLocationProgress", Integer.valueOf(-13262875));
        defaultColors.put(key_calls_callReceivedGreenIcon, Integer.valueOf(-16725933));
        defaultColors.put(key_calls_callReceivedRedIcon, Integer.valueOf(-47032));
        defaultColors.put(key_featuredStickers_addedIcon, Integer.valueOf(-11491093));
        defaultColors.put(key_featuredStickers_buttonProgress, Integer.valueOf(-1));
        defaultColors.put(key_featuredStickers_addButton, Integer.valueOf(-11491093));
        defaultColors.put(key_featuredStickers_addButtonPressed, Integer.valueOf(-12346402));
        defaultColors.put(key_featuredStickers_delButton, Integer.valueOf(-2533545));
        defaultColors.put(key_featuredStickers_delButtonPressed, Integer.valueOf(-3782327));
        defaultColors.put(key_featuredStickers_buttonText, Integer.valueOf(-1));
        defaultColors.put(key_featuredStickers_unread, Integer.valueOf(-11688214));
        defaultColors.put(key_inappPlayerPerformer, Integer.valueOf(-13683656));
        defaultColors.put(key_inappPlayerTitle, Integer.valueOf(-13683656));
        defaultColors.put(key_inappPlayerBackground, Integer.valueOf(-1));
        defaultColors.put(key_inappPlayerPlayPause, Integer.valueOf(-10309397));
        defaultColors.put(key_inappPlayerClose, Integer.valueOf(-5723992));
        defaultColors.put(key_returnToCallBackground, Integer.valueOf(-12279325));
        defaultColors.put(key_returnToCallText, Integer.valueOf(-1));
        defaultColors.put(key_sharedMedia_startStopLoadIcon, Integer.valueOf(-13196562));
        defaultColors.put(key_sharedMedia_linkPlaceholder, Integer.valueOf(-986896));
        defaultColors.put(key_sharedMedia_linkPlaceholderText, Integer.valueOf(-1));
        defaultColors.put(key_checkbox, Integer.valueOf(-10567099));
        defaultColors.put(key_checkboxCheck, Integer.valueOf(-1));
        defaultColors.put(key_stickers_menu, Integer.valueOf(-4801083));
        defaultColors.put(key_stickers_menuSelector, Integer.valueOf(ACTION_BAR_AUDIO_SELECTOR_COLOR));
        defaultColors.put(key_changephoneinfo_image, Integer.valueOf(-5723992));
        defaultColors.put(key_groupcreate_hintText, Integer.valueOf(-6182221));
        defaultColors.put(key_groupcreate_cursor, Integer.valueOf(-11361317));
        defaultColors.put(key_groupcreate_sectionShadow, Integer.valueOf(ACTION_BAR_VIDEO_EDIT_COLOR));
        defaultColors.put(key_groupcreate_sectionText, Integer.valueOf(-8617336));
        defaultColors.put(key_groupcreate_onlineText, Integer.valueOf(-12545331));
        defaultColors.put(key_groupcreate_offlineText, Integer.valueOf(-8156010));
        defaultColors.put(key_groupcreate_checkbox, Integer.valueOf(-10567099));
        defaultColors.put(key_groupcreate_checkboxCheck, Integer.valueOf(-1));
        defaultColors.put(key_groupcreate_spanText, Integer.valueOf(-14606047));
        defaultColors.put(key_groupcreate_spanBackground, Integer.valueOf(-855310));
        defaultColors.put(key_contacts_inviteBackground, Integer.valueOf(-11157919));
        defaultColors.put(key_contacts_inviteText, Integer.valueOf(-1));
        defaultColors.put(key_login_progressInner, Integer.valueOf(-1971470));
        defaultColors.put(key_login_progressOuter, Integer.valueOf(-10313520));
        defaultColors.put(key_musicPicker_checkbox, Integer.valueOf(-14043401));
        defaultColors.put(key_musicPicker_checkboxCheck, Integer.valueOf(-1));
        defaultColors.put(key_musicPicker_buttonBackground, Integer.valueOf(-10702870));
        defaultColors.put(key_musicPicker_buttonIcon, Integer.valueOf(-1));
        defaultColors.put(key_picker_enabledButton, Integer.valueOf(-15095832));
        defaultColors.put(key_picker_disabledButton, Integer.valueOf(-6710887));
        defaultColors.put(key_picker_badge, Integer.valueOf(-14043401));
        defaultColors.put(key_picker_badgeText, Integer.valueOf(-1));
        defaultColors.put(key_chat_botSwitchToInlineText, Integer.valueOf(-12348980));
        defaultColors.put(key_calls_ratingStar, Integer.valueOf(Integer.MIN_VALUE));
        defaultColors.put(key_calls_ratingStarSelected, Integer.valueOf(-11888682));
        fallbackKeys.put(key_chat_adminText, key_chat_inTimeText);
        fallbackKeys.put(key_chat_adminSelectedText, key_chat_inTimeSelectedText);
        fallbackKeys.put(key_player_progressCachedBackground, key_player_progressBackground);
        fallbackKeys.put(key_chat_inAudioCacheSeekbar, key_chat_inAudioSeekbar);
        fallbackKeys.put(key_chat_outAudioCacheSeekbar, key_chat_outAudioSeekbar);
        fallbackKeys.put(key_chat_emojiSearchBackground, key_chat_emojiPanelStickerPackSelector);
        ThemeInfo themeInfo = new ThemeInfo();
        themeInfo.name = "Default";
        ArrayList arrayList = themes;
        defaultTheme = themeInfo;
        currentTheme = themeInfo;
        currentDayTheme = themeInfo;
        arrayList.add(themeInfo);
        themesDict.put("Default", defaultTheme);
        themeInfo = new ThemeInfo();
        themeInfo.name = "Dark";
        themeInfo.assetName = "dark.attheme";
        arrayList = themes;
        currentNightTheme = themeInfo;
        arrayList.add(themeInfo);
        themesDict.put("Dark", themeInfo);
        themeInfo = new ThemeInfo();
        themeInfo.name = "Blue";
        themeInfo.assetName = "bluebubbles.attheme";
        themes.add(themeInfo);
        themesDict.put("Blue", themeInfo);
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
        Object string = sharedPreferences.getString("themes2", null);
        if (TextUtils.isEmpty(string)) {
            string = sharedPreferences.getString("themes", null);
            if (!TextUtils.isEmpty(string)) {
                String[] split = string.split("&");
                for (String createWithString : split) {
                    ThemeInfo createWithString2 = ThemeInfo.createWithString(createWithString);
                    if (createWithString2 != null) {
                        otherThemes.add(createWithString2);
                        themes.add(createWithString2);
                        themesDict.put(createWithString2.name, createWithString2);
                    }
                }
            }
            saveOtherThemes();
            sharedPreferences.edit().remove("themes").commit();
        } else {
            try {
                JSONArray jSONArray = new JSONArray(string);
                for (int i = 0; i < jSONArray.length(); i++) {
                    createWithJson = ThemeInfo.createWithJson(jSONArray.getJSONObject(i));
                    if (createWithJson != null) {
                        otherThemes.add(createWithJson);
                        themes.add(createWithJson);
                        themesDict.put(createWithJson.name, createWithJson);
                    }
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
        sortThemes();
        themeInfo = null;
        try {
            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
            String string2 = globalMainSettings.getString("theme", null);
            if (string2 != null) {
                themeInfo = (ThemeInfo) themesDict.get(string2);
            }
            string2 = globalMainSettings.getString("nighttheme", null);
            if (string2 != null) {
                createWithJson = (ThemeInfo) themesDict.get(string2);
                if (createWithJson != null) {
                    currentNightTheme = createWithJson;
                }
            }
            selectedAutoNightType = globalMainSettings.getInt("selectedAutoNightType", 0);
            autoNightScheduleByLocation = globalMainSettings.getBoolean("autoNightScheduleByLocation", false);
            autoNightBrighnessThreshold = globalMainSettings.getFloat("autoNightBrighnessThreshold", 0.25f);
            autoNightDayStartTime = globalMainSettings.getInt("autoNightDayStartTime", 1320);
            autoNightDayEndTime = globalMainSettings.getInt("autoNightDayEndTime", 480);
            autoNightSunsetTime = globalMainSettings.getInt("autoNightSunsetTime", 1320);
            autoNightSunriseTime = globalMainSettings.getInt("autoNightSunriseTime", 480);
            autoNightCityName = globalMainSettings.getString("autoNightCityName", TtmlNode.ANONYMOUS_REGION_ID);
            long j = globalMainSettings.getLong("autoNightLocationLatitude3", 10000);
            if (j != 10000) {
                autoNightLocationLatitude = Double.longBitsToDouble(j);
            } else {
                autoNightLocationLatitude = 10000.0d;
            }
            j = globalMainSettings.getLong("autoNightLocationLongitude3", 10000);
            if (j != 10000) {
                autoNightLocationLongitude = Double.longBitsToDouble(j);
            } else {
                autoNightLocationLongitude = 10000.0d;
            }
            autoNightLastSunCheckDay = globalMainSettings.getInt("autoNightLastSunCheckDay", -1);
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
        if (themeInfo == null) {
            themeInfo = defaultTheme;
        } else {
            currentDayTheme = themeInfo;
        }
        applyTheme(themeInfo, false, false, false);
        AndroidUtilities.runOnUIThread(new C07613());
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
        if (currentNightTheme != null) {
            edit.putString("nighttheme", currentNightTheme.name);
        } else {
            edit.remove("nighttheme");
        }
        edit.commit();
    }

    @android.annotation.SuppressLint({"PrivateApi"})
    private static android.graphics.drawable.Drawable getStateDrawable(android.graphics.drawable.Drawable r6, int r7) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r0 = StateListDrawable_getStateDrawableMethod;
        r1 = 0;
        r2 = 1;
        if (r0 != 0) goto L_0x0016;
    L_0x0006:
        r0 = android.graphics.drawable.StateListDrawable.class;	 Catch:{ Throwable -> 0x0016 }
        r3 = "getStateDrawable";	 Catch:{ Throwable -> 0x0016 }
        r4 = new java.lang.Class[r2];	 Catch:{ Throwable -> 0x0016 }
        r5 = java.lang.Integer.TYPE;	 Catch:{ Throwable -> 0x0016 }
        r4[r1] = r5;	 Catch:{ Throwable -> 0x0016 }
        r0 = r0.getDeclaredMethod(r3, r4);	 Catch:{ Throwable -> 0x0016 }
        StateListDrawable_getStateDrawableMethod = r0;	 Catch:{ Throwable -> 0x0016 }
    L_0x0016:
        r0 = StateListDrawable_getStateDrawableMethod;
        r3 = 0;
        if (r0 != 0) goto L_0x001c;
    L_0x001b:
        return r3;
    L_0x001c:
        r0 = StateListDrawable_getStateDrawableMethod;	 Catch:{ Exception -> 0x002d }
        r2 = new java.lang.Object[r2];	 Catch:{ Exception -> 0x002d }
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x002d }
        r2[r1] = r7;	 Catch:{ Exception -> 0x002d }
        r6 = r0.invoke(r6, r2);	 Catch:{ Exception -> 0x002d }
        r6 = (android.graphics.drawable.Drawable) r6;	 Catch:{ Exception -> 0x002d }
        return r6;
    L_0x002d:
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.getStateDrawable(android.graphics.drawable.Drawable, int):android.graphics.drawable.Drawable");
    }

    public static Drawable createEmojiIconSelectorDrawable(Context context, int i, int i2, int i3) {
        context = context.getResources();
        Drawable mutate = context.getDrawable(i).mutate();
        if (i2 != 0) {
            mutate.setColorFilter(new PorterDuffColorFilter(i2, Mode.MULTIPLY));
        }
        context = context.getDrawable(i).mutate();
        if (i3 != 0) {
            context.setColorFilter(new PorterDuffColorFilter(i3, Mode.MULTIPLY));
        }
        i = new C07624();
        i.setEnterFadeDuration(1);
        i.setExitFadeDuration(Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        i.addState(new int[]{16842913}, context);
        i.addState(new int[0], mutate);
        return i;
    }

    public static Drawable createEditTextDrawable(Context context, boolean z) {
        context = context.getResources();
        Drawable mutate = context.getDrawable(C0446R.drawable.search_dark).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(getColor(z ? key_dialogInputField : key_windowBackgroundWhiteInputField), Mode.MULTIPLY));
        context = context.getDrawable(C0446R.drawable.search_dark_activated).mutate();
        context.setColorFilter(new PorterDuffColorFilter(getColor(z ? key_dialogInputFieldActivated : key_windowBackgroundWhiteInputFieldActivated), Mode.MULTIPLY));
        z = new C07635();
        z.addState(new int[]{16842910, 16842908}, context);
        z.addState(new int[]{16842908}, context);
        z.addState(StateSet.WILD_CARD, mutate);
        return z;
    }

    public static boolean canStartHolidayAnimation() {
        return canStartHolidayAnimation;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static Drawable getCurrentHolidayDrawable() {
        if (System.currentTimeMillis() - lastHolidayCheckTime >= ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS) {
            lastHolidayCheckTime = System.currentTimeMillis();
            Calendar instance = Calendar.getInstance();
            instance.setTimeInMillis(System.currentTimeMillis());
            int i = instance.get(2);
            int i2 = instance.get(5);
            int i3 = instance.get(12);
            int i4 = instance.get(11);
            if (i == 0 && i2 == 1 && i3 <= 10 && i4 == 0) {
                canStartHolidayAnimation = true;
            } else {
                canStartHolidayAnimation = false;
            }
            if (dialogs_holidayDrawable == null) {
                if (i == 11) {
                    if (i2 >= (BuildVars.DEBUG_PRIVATE_VERSION ? 29 : 31)) {
                    }
                }
                if (i == 0) {
                }
            }
        }
        return dialogs_holidayDrawable;
    }

    public static int getCurrentHolidayDrawableXOffset() {
        return dialogs_holidayDrawableOffsetX;
    }

    public static int getCurrentHolidayDrawableYOffset() {
        return dialogs_holidayDrawableOffsetY;
    }

    public static Drawable createSimpleSelectorDrawable(Context context, int i, int i2, int i3) {
        context = context.getResources();
        Drawable mutate = context.getDrawable(i).mutate();
        if (i2 != 0) {
            mutate.setColorFilter(new PorterDuffColorFilter(i2, Mode.MULTIPLY));
        }
        context = context.getDrawable(i).mutate();
        if (i3 != 0) {
            context.setColorFilter(new PorterDuffColorFilter(i3, Mode.MULTIPLY));
        }
        i = new C07646();
        i.addState(new int[]{16842919}, context);
        i.addState(new int[]{16842913}, context);
        i.addState(StateSet.WILD_CARD, mutate);
        return i;
    }

    public static Drawable createCircleDrawable(int i, int i2) {
        Shape ovalShape = new OvalShape();
        i = (float) i;
        ovalShape.resize(i, i);
        i = new ShapeDrawable(ovalShape);
        i.getPaint().setColor(i2);
        return i;
    }

    public static CombinedDrawable createCircleDrawableWithIcon(int i, int i2) {
        return createCircleDrawableWithIcon(i, i2, 0);
    }

    public static CombinedDrawable createCircleDrawableWithIcon(int i, int i2, int i3) {
        return createCircleDrawableWithIcon(i, i2 != 0 ? ApplicationLoader.applicationContext.getResources().getDrawable(i2).mutate() : 0, i3);
    }

    public static CombinedDrawable createCircleDrawableWithIcon(int i, Drawable drawable, int i2) {
        Shape ovalShape = new OvalShape();
        float f = (float) i;
        ovalShape.resize(f, f);
        Drawable shapeDrawable = new ShapeDrawable(ovalShape);
        Paint paint = shapeDrawable.getPaint();
        paint.setColor(-1);
        if (i2 == 1) {
            paint.setStyle(Style.STROKE);
            paint.setStrokeWidth((float) AndroidUtilities.dp(NUM));
        } else if (i2 == 2) {
            paint.setAlpha(0);
        }
        i2 = new CombinedDrawable(shapeDrawable, drawable);
        i2.setCustomSize(i, i);
        return i2;
    }

    public static Drawable createRoundRectDrawableWithIcon(int i, int i2) {
        r2 = new float[8];
        i = (float) i;
        r2[0] = i;
        r2[1] = i;
        r2[2] = i;
        r2[3] = i;
        r2[4] = i;
        r2[5] = i;
        r2[6] = i;
        r2[7] = i;
        Drawable shapeDrawable = new ShapeDrawable(new RoundRectShape(r2, null, null));
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
            drawable.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
        }
    }

    public static Drawable createSimpleSelectorCircleDrawable(int i, int i2, int i3) {
        Shape ovalShape = new OvalShape();
        i = (float) i;
        ovalShape.resize(i, i);
        i = new ShapeDrawable(ovalShape);
        i.getPaint().setColor(i2);
        i2 = new ShapeDrawable(ovalShape);
        if (VERSION.SDK_INT >= 21) {
            i2.getPaint().setColor(-1);
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{i3}), i, i2);
        }
        i2.getPaint().setColor(i3);
        i3 = new StateListDrawable();
        i3.addState(new int[]{16842919}, i2);
        i3.addState(new int[]{16842908}, i2);
        i3.addState(StateSet.WILD_CARD, i);
        return i3;
    }

    public static Drawable createRoundRectDrawable(int i, int i2) {
        r2 = new float[8];
        i = (float) i;
        r2[0] = i;
        r2[1] = i;
        r2[2] = i;
        r2[3] = i;
        r2[4] = i;
        r2[5] = i;
        r2[6] = i;
        r2[7] = i;
        Drawable shapeDrawable = new ShapeDrawable(new RoundRectShape(r2, null, null));
        shapeDrawable.getPaint().setColor(i2);
        return shapeDrawable;
    }

    public static Drawable createSimpleSelectorRoundRectDrawable(int i, int i2, int i3) {
        r3 = new float[8];
        i = (float) i;
        r3[0] = i;
        r3[1] = i;
        r3[2] = i;
        r3[3] = i;
        r3[4] = i;
        r3[5] = i;
        r3[6] = i;
        r3[7] = i;
        Drawable shapeDrawable = new ShapeDrawable(new RoundRectShape(r3, null, null));
        shapeDrawable.getPaint().setColor(i2);
        i2 = new ShapeDrawable(new RoundRectShape(new float[]{i, i, i, i, i, i, i, i}, null, null));
        i2.getPaint().setColor(i3);
        i = new StateListDrawable();
        i.addState(new int[]{16842919}, i2);
        i.addState(new int[]{16842913}, i2);
        i.addState(StateSet.WILD_CARD, shapeDrawable);
        return i;
    }

    public static Drawable getRoundRectSelectorDrawable() {
        if (VERSION.SDK_INT >= 21) {
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{getColor(key_dialogButtonSelector)}), null, createRoundRectDrawable(AndroidUtilities.dp(3.0f), -1));
        }
        Drawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, createRoundRectDrawable(AndroidUtilities.dp(3.0f), getColor(key_dialogButtonSelector)));
        stateListDrawable.addState(new int[]{16842913}, createRoundRectDrawable(AndroidUtilities.dp(3.0f), getColor(key_dialogButtonSelector)));
        stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(0));
        return stateListDrawable;
    }

    public static Drawable getSelectorDrawable(boolean z) {
        if (!z) {
            return createSelectorDrawable(getColor(key_listSelector), 2);
        }
        if (VERSION.SDK_INT >= true) {
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{getColor(key_listSelector)}), new ColorDrawable(getColor(key_windowBackgroundWhite)), new ColorDrawable(-1));
        }
        z = getColor(key_listSelector);
        Drawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, new ColorDrawable(z));
        stateListDrawable.addState(new int[]{16842913}, new ColorDrawable(z));
        stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(getColor(key_windowBackgroundWhite)));
        return stateListDrawable;
    }

    public static Drawable createSelectorDrawable(int i) {
        return createSelectorDrawable(i, 1);
    }

    public static Drawable createSelectorDrawable(int i, int i2) {
        if (VERSION.SDK_INT >= 21) {
            if (i2 == 1) {
                maskPaint.setColor(-1);
                i2 = new C07657();
            } else {
                i2 = i2 == 2 ? new ColorDrawable(-1) : 0;
            }
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{i}), null, i2);
        }
        i2 = new StateListDrawable();
        i2.addState(new int[]{16842919}, new ColorDrawable(i));
        i2.addState(new int[]{16842913}, new ColorDrawable(i));
        i2.addState(StateSet.WILD_CARD, new ColorDrawable(0));
        return i2;
    }

    public static void applyPreviousTheme() {
        if (previousTheme != null) {
            applyTheme(previousTheme, true, false, false);
            previousTheme = null;
            checkAutoNightThemeConditions();
        }
    }

    private static void sortThemes() {
        Collections.sort(themes, new C07668());
    }

    public static ThemeInfo applyThemeFile(File file, String str, boolean z) {
        try {
            if (!(str.equals("Default") || str.equals("Dark"))) {
                if (!str.equals("Blue")) {
                    File file2 = new File(ApplicationLoader.getFilesDirFixed(), str);
                    if (AndroidUtilities.copyFile(file, file2) == null) {
                        return null;
                    }
                    ThemeInfo themeInfo = (ThemeInfo) themesDict.get(str);
                    if (themeInfo == null) {
                        file = new ThemeInfo();
                        file.name = str;
                        file.pathToFile = file2.getAbsolutePath();
                        str = file;
                        file = 1;
                    } else {
                        str = themeInfo;
                        file = null;
                    }
                    if (z) {
                        previousTheme = currentTheme;
                    } else {
                        previousTheme = null;
                        if (file != null) {
                            themes.add(str);
                            themesDict.put(str.name, str);
                            otherThemes.add(str);
                            sortThemes();
                            saveOtherThemes();
                        }
                    }
                    applyTheme(str, z ^ 1, true, false);
                    return str;
                }
            }
            return null;
        } catch (Throwable e) {
            FileLog.m3e(e);
            return null;
        }
    }

    public static void applyTheme(ThemeInfo themeInfo) {
        applyTheme(themeInfo, true, true, false);
    }

    public static void applyTheme(ThemeInfo themeInfo, boolean z) {
        applyTheme(themeInfo, true, true, z);
    }

    public static void applyTheme(ThemeInfo themeInfo, boolean z, boolean z2, final boolean z3) {
        if (themeInfo != null) {
            ThemeEditorView instance = ThemeEditorView.getInstance();
            if (instance != null) {
                instance.destroy();
            }
            try {
                if (themeInfo.pathToFile == null) {
                    if (themeInfo.assetName == null) {
                        if (!z3 && z) {
                            z = MessagesController.getGlobalMainSettings().edit();
                            z.remove("theme");
                            if (z2) {
                                z.remove("overrideThemeWallpaper");
                            }
                            z.commit();
                        }
                        currentColors.clear();
                        wallpaper = null;
                        themedWallpaper = null;
                        currentTheme = themeInfo;
                        if (!z3) {
                            currentDayTheme = currentTheme;
                        }
                        reloadWallpaper();
                        applyCommonTheme();
                        applyDialogsTheme();
                        applyProfileTheme();
                        applyChatTheme(null);
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewTheme, Boolean.valueOf(z3));
                            }
                        });
                    }
                }
                if (!z3 && z) {
                    z = MessagesController.getGlobalMainSettings().edit();
                    z.putString("theme", themeInfo.name);
                    if (z2) {
                        z.remove("overrideThemeWallpaper");
                    }
                    z.commit();
                }
                if (themeInfo.assetName) {
                    currentColors = getThemeFileValues(null, themeInfo.assetName);
                } else {
                    currentColors = getThemeFileValues(new File(themeInfo.pathToFile), null);
                }
                currentTheme = themeInfo;
                if (z3) {
                    currentDayTheme = currentTheme;
                }
                reloadWallpaper();
                applyCommonTheme();
                applyDialogsTheme();
                applyProfileTheme();
                applyChatTheme(null);
                AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    private static void saveOtherThemes() {
        int i = 0;
        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
        JSONArray jSONArray = new JSONArray();
        while (i < otherThemes.size()) {
            JSONObject saveJson = ((ThemeInfo) otherThemes.get(i)).getSaveJson();
            if (saveJson != null) {
                jSONArray.put(saveJson);
            }
            i++;
        }
        edit.putString("themes2", jSONArray.toString());
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
        if (currentNightTheme == null) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        String name = currentNightTheme.getName();
        if (name.toLowerCase().endsWith(".attheme")) {
            name = name.substring(0, name.lastIndexOf(46));
        }
        return name;
    }

    public static ThemeInfo getCurrentTheme() {
        return currentDayTheme != null ? currentDayTheme : defaultTheme;
    }

    public static ThemeInfo getCurrentNightTheme() {
        return currentNightTheme;
    }

    public static boolean isCurrentThemeNight() {
        return currentTheme == currentNightTheme;
    }

    private static long getAutoNightSwitchThemeDelay() {
        return Math.abs(lastThemeSwitchTime - SystemClock.uptimeMillis()) >= 12000 ? 1800 : 12000;
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

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void checkAutoNightThemeConditions(boolean z) {
        if (previousTheme == null) {
            boolean z2;
            boolean z3 = false;
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
                        FileLog.m0d("light sensor unregistered");
                    }
                }
            }
            if (selectedAutoNightType == 1) {
                int i;
                int i2;
                Calendar instance = Calendar.getInstance();
                instance.setTimeInMillis(System.currentTimeMillis());
                int i3 = (instance.get(11) * 60) + instance.get(12);
                if (autoNightScheduleByLocation) {
                    i = instance.get(5);
                    if (!(autoNightLastSunCheckDay == i || autoNightLocationLatitude == 10000.0d || autoNightLocationLongitude == 10000.0d)) {
                        int[] calculateSunriseSunset = SunDate.calculateSunriseSunset(autoNightLocationLatitude, autoNightLocationLongitude);
                        autoNightSunriseTime = calculateSunriseSunset[0];
                        autoNightSunsetTime = calculateSunriseSunset[1];
                        autoNightLastSunCheckDay = i;
                        saveAutoNightThemeConfig();
                    }
                    i = autoNightSunsetTime;
                    i2 = autoNightSunriseTime;
                } else {
                    i = autoNightDayStartTime;
                    i2 = autoNightDayEndTime;
                }
                if (i < i2) {
                    if (i <= i3 && i3 <= i2) {
                    }
                    z2 = true;
                    if (z2) {
                        if (z2) {
                            z3 = true;
                        }
                        applyDayNightThemeMaybe(z3);
                    }
                    if (z) {
                        lastThemeSwitchTime = 0;
                    }
                } else if (i > i3 || i3 > 1440) {
                    if (i3 >= 0 && i3 <= i2) {
                    }
                    z2 = true;
                    if (z2) {
                        if (z2) {
                            z3 = true;
                        }
                        applyDayNightThemeMaybe(z3);
                    }
                    if (z) {
                        lastThemeSwitchTime = 0;
                    }
                }
            }
            if (selectedAutoNightType == 2) {
                if (lightSensor == null) {
                    sensorManager = (SensorManager) ApplicationLoader.applicationContext.getSystemService("sensor");
                    lightSensor = sensorManager.getDefaultSensor(5);
                }
                if (!(lightSensorRegistered || lightSensor == null)) {
                    sensorManager.registerListener(ambientSensorListener, lightSensor, 500000);
                    lightSensorRegistered = true;
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("light sensor registered");
                    }
                }
                if (lastBrightnessValue > autoNightBrighnessThreshold) {
                    if (!switchDayRunnableScheduled) {
                        z2 = true;
                        if (z2) {
                            if (z2) {
                                z3 = true;
                            }
                            applyDayNightThemeMaybe(z3);
                        }
                        if (z) {
                            lastThemeSwitchTime = 0;
                        }
                    }
                }
            }
            z2 = false;
            if (z2) {
                if (z2) {
                    z3 = true;
                }
                applyDayNightThemeMaybe(z3);
            }
            if (z) {
                lastThemeSwitchTime = 0;
            }
            z2 = true;
            if (z2) {
                if (z2) {
                    z3 = true;
                }
                applyDayNightThemeMaybe(z3);
            }
            if (z) {
                lastThemeSwitchTime = 0;
            }
        }
    }

    private static void applyDayNightThemeMaybe(boolean z) {
        if (z) {
            if (currentTheme != currentNightTheme) {
                lastThemeSwitchTime = SystemClock.uptimeMillis();
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, currentNightTheme);
            }
        } else if (currentTheme != currentDayTheme) {
            lastThemeSwitchTime = SystemClock.uptimeMillis();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, currentDayTheme);
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
        otherThemes.remove(themeInfo);
        themesDict.remove(themeInfo.name);
        themes.remove(themeInfo);
        new File(themeInfo.pathToFile).delete();
        saveOtherThemes();
        return z;
    }

    public static void saveCurrentTheme(String str, boolean z) {
        Throwable e;
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry entry : currentColors.entrySet()) {
            stringBuilder.append((String) entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(entry.getValue());
            stringBuilder.append("\n");
        }
        File file = new File(ApplicationLoader.getFilesDirFixed(), str);
        FileOutputStream fileOutputStream = null;
        try {
            FileOutputStream fileOutputStream2 = new FileOutputStream(file);
            try {
                fileOutputStream2.write(stringBuilder.toString().getBytes());
                if (themedWallpaper instanceof BitmapDrawable) {
                    Bitmap bitmap = ((BitmapDrawable) themedWallpaper).getBitmap();
                    if (bitmap != null) {
                        fileOutputStream2.write(new byte[]{(byte) 87, (byte) 80, (byte) 83, (byte) 10});
                        bitmap.compress(CompressFormat.JPEG, 87, fileOutputStream2);
                        fileOutputStream2.write(new byte[]{(byte) 10, (byte) 87, (byte) 80, (byte) 69, (byte) 10});
                    }
                    if (z) {
                        wallpaper = themedWallpaper;
                        calcBackgroundColor(wallpaper, 2);
                    }
                }
                z = (ThemeInfo) themesDict.get(str);
                if (z == null) {
                    z = new ThemeInfo();
                    z.pathToFile = file.getAbsolutePath();
                    z.name = str;
                    themes.add(z);
                    themesDict.put(z.name, z);
                    otherThemes.add(z);
                    saveOtherThemes();
                    sortThemes();
                }
                currentTheme = z;
                if (currentTheme != currentNightTheme) {
                    currentDayTheme = currentTheme;
                }
                str = MessagesController.getGlobalMainSettings().edit();
                str.putString("theme", currentDayTheme.name);
                str.commit();
                if (fileOutputStream2 != null) {
                    try {
                        fileOutputStream2.close();
                    } catch (Throwable e2) {
                        FileLog.m3e(e2);
                    }
                }
            } catch (Exception e3) {
                e2 = e3;
                fileOutputStream = fileOutputStream2;
                try {
                    FileLog.m3e(e2);
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (Throwable th) {
                    str = th;
                    fileOutputStream2 = fileOutputStream;
                    if (fileOutputStream2 != null) {
                        try {
                            fileOutputStream2.close();
                        } catch (Throwable e4) {
                            FileLog.m3e(e4);
                        }
                    }
                    throw str;
                }
            } catch (Throwable th2) {
                str = th2;
                if (fileOutputStream2 != null) {
                    fileOutputStream2.close();
                }
                throw str;
            }
        } catch (Exception e5) {
            e2 = e5;
            FileLog.m3e(e2);
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }

    public static java.io.File getAssetFile(java.lang.String r7) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r0 = new java.io.File;
        r1 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();
        r0.<init>(r1, r7);
        r1 = 0;
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x001e }
        r3 = r3.getAssets();	 Catch:{ Exception -> 0x001e }
        r3 = r3.open(r7);	 Catch:{ Exception -> 0x001e }
        r4 = r3.available();	 Catch:{ Exception -> 0x001e }
        r4 = (long) r4;	 Catch:{ Exception -> 0x001e }
        r3.close();	 Catch:{ Exception -> 0x001e }
        goto L_0x0023;
    L_0x001e:
        r3 = move-exception;
        org.telegram.messenger.FileLog.m3e(r3);
        r4 = r1;
    L_0x0023:
        r3 = r0.exists();
        if (r3 == 0) goto L_0x0035;
    L_0x0029:
        r3 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1));
        if (r3 == 0) goto L_0x005c;
    L_0x002d:
        r1 = r0.length();
        r3 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1));
        if (r3 == 0) goto L_0x005c;
    L_0x0035:
        r1 = 0;
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0053 }
        r2 = r2.getAssets();	 Catch:{ Exception -> 0x0053 }
        r7 = r2.open(r7);	 Catch:{ Exception -> 0x0053 }
        org.telegram.messenger.AndroidUtilities.copyFile(r7, r0);	 Catch:{ Exception -> 0x004c, all -> 0x0049 }
        if (r7 == 0) goto L_0x005c;
    L_0x0045:
        r7.close();	 Catch:{ Exception -> 0x005c }
        goto L_0x005c;
    L_0x0049:
        r0 = move-exception;
        r1 = r7;
        goto L_0x005d;
    L_0x004c:
        r1 = move-exception;
        r6 = r1;
        r1 = r7;
        r7 = r6;
        goto L_0x0054;
    L_0x0051:
        r0 = move-exception;
        goto L_0x005d;
    L_0x0053:
        r7 = move-exception;
    L_0x0054:
        org.telegram.messenger.FileLog.m3e(r7);	 Catch:{ all -> 0x0051 }
        if (r1 == 0) goto L_0x005c;
    L_0x0059:
        r1.close();	 Catch:{ Exception -> 0x005c }
    L_0x005c:
        return r0;
    L_0x005d:
        if (r1 == 0) goto L_0x0062;
    L_0x005f:
        r1.close();	 Catch:{ Exception -> 0x0062 }
    L_0x0062:
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.getAssetFile(java.lang.String):java.io.File");
    }

    private static java.util.HashMap<java.lang.String, java.lang.Integer> getThemeFileValues(java.io.File r14, java.lang.String r15) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r0 = new java.util.HashMap;
        r0.<init>();
        r1 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r2 = 0;
        r1 = new byte[r1];	 Catch:{ Throwable -> 0x00a6 }
        if (r15 == 0) goto L_0x0010;	 Catch:{ Throwable -> 0x00a6 }
    L_0x000c:
        r14 = getAssetFile(r15);	 Catch:{ Throwable -> 0x00a6 }
    L_0x0010:
        r15 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x00a6 }
        r15.<init>(r14);	 Catch:{ Throwable -> 0x00a6 }
        r14 = -1;
        themedWallpaperFileOffset = r14;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        r2 = 0;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        r3 = r2;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        r4 = r3;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
    L_0x001b:
        r5 = r15.read(r1);	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        if (r5 == r14) goto L_0x0098;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
    L_0x0021:
        r6 = r2;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        r7 = r6;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        r8 = r3;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
    L_0x0024:
        r9 = 1;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        if (r6 >= r5) goto L_0x0088;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
    L_0x0027:
        r10 = r1[r6];	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        r11 = 10;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        if (r10 != r11) goto L_0x0085;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
    L_0x002d:
        r10 = r6 - r7;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        r10 = r10 + r9;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        r11 = new java.lang.String;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        r12 = r10 + -1;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        r13 = "UTF-8";	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        r11.<init>(r1, r7, r12, r13);	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        r12 = "WPS";	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        r12 = r11.startsWith(r12);	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        if (r12 == 0) goto L_0x0046;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
    L_0x0041:
        r10 = r10 + r8;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        themedWallpaperFileOffset = r10;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        r4 = r9;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        goto L_0x0088;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
    L_0x0046:
        r9 = 61;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        r9 = r11.indexOf(r9);	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        if (r9 == r14) goto L_0x0083;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
    L_0x004e:
        r12 = r11.substring(r2, r9);	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        r9 = r9 + 1;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        r9 = r11.substring(r9);	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        r11 = r9.length();	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        if (r11 <= 0) goto L_0x0074;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
    L_0x005e:
        r11 = r9.charAt(r2);	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        r13 = 35;
        if (r11 != r13) goto L_0x0074;
    L_0x0066:
        r11 = android.graphics.Color.parseColor(r9);	 Catch:{ Exception -> 0x006b }
        goto L_0x007c;
    L_0x006b:
        r9 = org.telegram.messenger.Utilities.parseInt(r9);	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        r11 = r9.intValue();	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        goto L_0x007c;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
    L_0x0074:
        r9 = org.telegram.messenger.Utilities.parseInt(r9);	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        r11 = r9.intValue();	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
    L_0x007c:
        r9 = java.lang.Integer.valueOf(r11);	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        r0.put(r12, r9);	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
    L_0x0083:
        r7 = r7 + r10;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        r8 = r8 + r10;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
    L_0x0085:
        r6 = r6 + 1;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        goto L_0x0024;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
    L_0x0088:
        if (r3 != r8) goto L_0x008b;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
    L_0x008a:
        goto L_0x0098;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
    L_0x008b:
        r3 = r15.getChannel();	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        r5 = (long) r8;	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        r3.position(r5);	 Catch:{ Throwable -> 0x00a0, all -> 0x009e }
        if (r4 == 0) goto L_0x0096;
    L_0x0095:
        goto L_0x0098;
    L_0x0096:
        r3 = r8;
        goto L_0x001b;
    L_0x0098:
        if (r15 == 0) goto L_0x00b4;
    L_0x009a:
        r15.close();	 Catch:{ Exception -> 0x00b0 }
        goto L_0x00b4;
    L_0x009e:
        r14 = move-exception;
        goto L_0x00b5;
    L_0x00a0:
        r14 = move-exception;
        r2 = r15;
        goto L_0x00a7;
    L_0x00a3:
        r14 = move-exception;
        r15 = r2;
        goto L_0x00b5;
    L_0x00a6:
        r14 = move-exception;
    L_0x00a7:
        org.telegram.messenger.FileLog.m3e(r14);	 Catch:{ all -> 0x00a3 }
        if (r2 == 0) goto L_0x00b4;
    L_0x00ac:
        r2.close();	 Catch:{ Exception -> 0x00b0 }
        goto L_0x00b4;
    L_0x00b0:
        r14 = move-exception;
        org.telegram.messenger.FileLog.m3e(r14);
    L_0x00b4:
        return r0;
    L_0x00b5:
        if (r15 == 0) goto L_0x00bf;
    L_0x00b7:
        r15.close();	 Catch:{ Exception -> 0x00bb }
        goto L_0x00bf;
    L_0x00bb:
        r15 = move-exception;
        org.telegram.messenger.FileLog.m3e(r15);
    L_0x00bf:
        throw r14;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.getThemeFileValues(java.io.File, java.lang.String):java.util.HashMap<java.lang.String, java.lang.Integer>");
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
            context = context.getResources();
            avatar_broadcastDrawable = context.getDrawable(C0446R.drawable.broadcast_w);
            avatar_savedDrawable = context.getDrawable(C0446R.drawable.bookmark_large);
            avatar_photoDrawable = context.getDrawable(C0446R.drawable.photo_w);
            applyCommonTheme();
        }
    }

    public static void applyCommonTheme() {
        if (dividerPaint != null) {
            dividerPaint.setColor(getColor(key_divider));
            linkSelectionPaint.setColor(getColor(key_windowBackgroundWhiteLinkSelection));
            setDrawableColorByKey(avatar_broadcastDrawable, key_avatar_text);
            setDrawableColorByKey(avatar_savedDrawable, key_avatar_text);
            setDrawableColorByKey(avatar_photoDrawable, key_avatar_text);
        }
    }

    public static void createDialogsResources(Context context) {
        createCommonResources(context);
        if (dialogs_namePaint == null) {
            context = context.getResources();
            dialogs_namePaint = new TextPaint(1);
            dialogs_namePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            dialogs_nameEncryptedPaint = new TextPaint(1);
            dialogs_nameEncryptedPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            dialogs_messagePaint = new TextPaint(1);
            dialogs_messagePrintingPaint = new TextPaint(1);
            dialogs_timePaint = new TextPaint(1);
            dialogs_countTextPaint = new TextPaint(1);
            dialogs_countTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            dialogs_onlinePaint = new TextPaint(1);
            dialogs_offlinePaint = new TextPaint(1);
            dialogs_tabletSeletedPaint = new Paint();
            dialogs_pinnedPaint = new Paint();
            dialogs_countPaint = new Paint(1);
            dialogs_countGrayPaint = new Paint(1);
            dialogs_errorPaint = new Paint(1);
            dialogs_lockDrawable = context.getDrawable(C0446R.drawable.list_secret);
            dialogs_checkDrawable = context.getDrawable(C0446R.drawable.list_check);
            dialogs_halfCheckDrawable = context.getDrawable(C0446R.drawable.list_halfcheck);
            dialogs_clockDrawable = context.getDrawable(C0446R.drawable.msg_clock).mutate();
            dialogs_errorDrawable = context.getDrawable(C0446R.drawable.list_warning_sign);
            dialogs_groupDrawable = context.getDrawable(C0446R.drawable.list_group);
            dialogs_broadcastDrawable = context.getDrawable(C0446R.drawable.list_broadcast);
            dialogs_muteDrawable = context.getDrawable(C0446R.drawable.list_mute).mutate();
            dialogs_verifiedDrawable = context.getDrawable(C0446R.drawable.verified_area);
            dialogs_verifiedCheckDrawable = context.getDrawable(C0446R.drawable.verified_check);
            dialogs_mentionDrawable = context.getDrawable(C0446R.drawable.mentionchatslist);
            dialogs_botDrawable = context.getDrawable(C0446R.drawable.list_bot);
            dialogs_pinnedDrawable = context.getDrawable(C0446R.drawable.list_pin);
            applyDialogsTheme();
        }
        dialogs_namePaint.setTextSize((float) AndroidUtilities.dp(17.0f));
        dialogs_nameEncryptedPaint.setTextSize((float) AndroidUtilities.dp(17.0f));
        dialogs_messagePaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        dialogs_messagePrintingPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        dialogs_timePaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        dialogs_countTextPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        dialogs_onlinePaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        dialogs_offlinePaint.setTextSize((float) AndroidUtilities.dp(16.0f));
    }

    public static void applyDialogsTheme() {
        if (dialogs_namePaint != null) {
            dialogs_namePaint.setColor(getColor(key_chats_name));
            dialogs_nameEncryptedPaint.setColor(getColor(key_chats_secretName));
            TextPaint textPaint = dialogs_messagePaint;
            TextPaint textPaint2 = dialogs_messagePaint;
            int color = getColor(key_chats_message);
            textPaint2.linkColor = color;
            textPaint.setColor(color);
            dialogs_tabletSeletedPaint.setColor(getColor(key_chats_tabletSelectedOverlay));
            dialogs_pinnedPaint.setColor(getColor(key_chats_pinnedOverlay));
            dialogs_timePaint.setColor(getColor(key_chats_date));
            dialogs_countTextPaint.setColor(getColor(key_chats_unreadCounterText));
            dialogs_messagePrintingPaint.setColor(getColor(key_chats_actionMessage));
            dialogs_countPaint.setColor(getColor(key_chats_unreadCounter));
            dialogs_countGrayPaint.setColor(getColor(key_chats_unreadCounterMuted));
            dialogs_errorPaint.setColor(getColor(key_chats_sentError));
            dialogs_onlinePaint.setColor(getColor(key_windowBackgroundWhiteBlueText3));
            dialogs_offlinePaint.setColor(getColor(key_windowBackgroundWhiteGrayText3));
            setDrawableColorByKey(dialogs_lockDrawable, key_chats_secretIcon);
            setDrawableColorByKey(dialogs_checkDrawable, key_chats_sentCheck);
            setDrawableColorByKey(dialogs_halfCheckDrawable, key_chats_sentCheck);
            setDrawableColorByKey(dialogs_clockDrawable, key_chats_sentClock);
            setDrawableColorByKey(dialogs_errorDrawable, key_chats_sentErrorIcon);
            setDrawableColorByKey(dialogs_groupDrawable, key_chats_nameIcon);
            setDrawableColorByKey(dialogs_broadcastDrawable, key_chats_nameIcon);
            setDrawableColorByKey(dialogs_botDrawable, key_chats_nameIcon);
            setDrawableColorByKey(dialogs_pinnedDrawable, key_chats_pinnedIcon);
            setDrawableColorByKey(dialogs_muteDrawable, key_chats_muteIcon);
            setDrawableColorByKey(dialogs_verifiedDrawable, key_chats_verifiedBackground);
            setDrawableColorByKey(dialogs_verifiedCheckDrawable, key_chats_verifiedCheck);
        }
    }

    public static void destroyResources() {
        for (int i = 0; i < chat_attachButtonDrawables.length; i++) {
            if (chat_attachButtonDrawables[i] != null) {
                chat_attachButtonDrawables[i].setCallback(null);
            }
        }
    }

    public static void createChatResources(android.content.Context r17, boolean r18) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r2 = sync;
        monitor-enter(r2);
        r3 = chat_msgTextPaint;	 Catch:{ all -> 0x0bf3 }
        r4 = 1;	 Catch:{ all -> 0x0bf3 }
        if (r3 != 0) goto L_0x003d;	 Catch:{ all -> 0x0bf3 }
    L_0x0008:
        r3 = new android.text.TextPaint;	 Catch:{ all -> 0x0bf3 }
        r3.<init>(r4);	 Catch:{ all -> 0x0bf3 }
        chat_msgTextPaint = r3;	 Catch:{ all -> 0x0bf3 }
        r3 = new android.text.TextPaint;	 Catch:{ all -> 0x0bf3 }
        r3.<init>(r4);	 Catch:{ all -> 0x0bf3 }
        chat_msgGameTextPaint = r3;	 Catch:{ all -> 0x0bf3 }
        r3 = new android.text.TextPaint;	 Catch:{ all -> 0x0bf3 }
        r3.<init>(r4);	 Catch:{ all -> 0x0bf3 }
        chat_msgTextPaintOneEmoji = r3;	 Catch:{ all -> 0x0bf3 }
        r3 = new android.text.TextPaint;	 Catch:{ all -> 0x0bf3 }
        r3.<init>(r4);	 Catch:{ all -> 0x0bf3 }
        chat_msgTextPaintTwoEmoji = r3;	 Catch:{ all -> 0x0bf3 }
        r3 = new android.text.TextPaint;	 Catch:{ all -> 0x0bf3 }
        r3.<init>(r4);	 Catch:{ all -> 0x0bf3 }
        chat_msgTextPaintThreeEmoji = r3;	 Catch:{ all -> 0x0bf3 }
        r3 = new android.text.TextPaint;	 Catch:{ all -> 0x0bf3 }
        r3.<init>(r4);	 Catch:{ all -> 0x0bf3 }
        chat_msgBotButtonPaint = r3;	 Catch:{ all -> 0x0bf3 }
        r3 = chat_msgBotButtonPaint;	 Catch:{ all -> 0x0bf3 }
        r5 = "fonts/rmedium.ttf";	 Catch:{ all -> 0x0bf3 }
        r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5);	 Catch:{ all -> 0x0bf3 }
        r3.setTypeface(r5);	 Catch:{ all -> 0x0bf3 }
    L_0x003d:
        monitor-exit(r2);	 Catch:{ all -> 0x0bf3 }
        r2 = 2;
        if (r18 != 0) goto L_0x0a57;
    L_0x0041:
        r3 = chat_msgInDrawable;
        if (r3 != 0) goto L_0x0a57;
    L_0x0045:
        r3 = new android.text.TextPaint;
        r3.<init>(r4);
        chat_infoPaint = r3;
        r3 = new android.text.TextPaint;
        r3.<init>(r4);
        chat_docNamePaint = r3;
        r3 = chat_docNamePaint;
        r5 = "fonts/rmedium.ttf";
        r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5);
        r3.setTypeface(r5);
        r3 = new android.graphics.Paint;
        r3.<init>(r4);
        chat_docBackPaint = r3;
        r3 = new android.graphics.Paint;
        r3.<init>(r4);
        chat_deleteProgressPaint = r3;
        r3 = new android.graphics.Paint;
        r3.<init>(r4);
        chat_botProgressPaint = r3;
        r3 = chat_botProgressPaint;
        r5 = android.graphics.Paint.Cap.ROUND;
        r3.setStrokeCap(r5);
        r3 = chat_botProgressPaint;
        r5 = android.graphics.Paint.Style.STROKE;
        r3.setStyle(r5);
        r3 = new android.text.TextPaint;
        r3.<init>(r4);
        chat_locationTitlePaint = r3;
        r3 = chat_locationTitlePaint;
        r5 = "fonts/rmedium.ttf";
        r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5);
        r3.setTypeface(r5);
        r3 = new android.text.TextPaint;
        r3.<init>(r4);
        chat_locationAddressPaint = r3;
        r3 = new android.graphics.Paint;
        r3.<init>();
        chat_urlPaint = r3;
        r3 = new android.graphics.Paint;
        r3.<init>();
        chat_textSearchSelectionPaint = r3;
        r3 = new android.graphics.Paint;
        r3.<init>(r4);
        chat_radialProgressPaint = r3;
        r3 = chat_radialProgressPaint;
        r5 = android.graphics.Paint.Cap.ROUND;
        r3.setStrokeCap(r5);
        r3 = chat_radialProgressPaint;
        r5 = android.graphics.Paint.Style.STROKE;
        r3.setStyle(r5);
        r3 = chat_radialProgressPaint;
        r5 = -NUM; // 0xffffffff9fffffff float:-1.0842021E-19 double:NaN;
        r3.setColor(r5);
        r3 = new android.graphics.Paint;
        r3.<init>(r4);
        chat_radialProgress2Paint = r3;
        r3 = chat_radialProgress2Paint;
        r5 = android.graphics.Paint.Cap.ROUND;
        r3.setStrokeCap(r5);
        r3 = chat_radialProgress2Paint;
        r5 = android.graphics.Paint.Style.STROKE;
        r3.setStyle(r5);
        r3 = new android.text.TextPaint;
        r3.<init>(r4);
        chat_audioTimePaint = r3;
        r3 = new android.text.TextPaint;
        r3.<init>(r4);
        chat_livePaint = r3;
        r3 = chat_livePaint;
        r5 = "fonts/rmedium.ttf";
        r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5);
        r3.setTypeface(r5);
        r3 = new android.text.TextPaint;
        r3.<init>(r4);
        chat_audioTitlePaint = r3;
        r3 = chat_audioTitlePaint;
        r5 = "fonts/rmedium.ttf";
        r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5);
        r3.setTypeface(r5);
        r3 = new android.text.TextPaint;
        r3.<init>(r4);
        chat_audioPerformerPaint = r3;
        r3 = new android.text.TextPaint;
        r3.<init>(r4);
        chat_botButtonPaint = r3;
        r3 = chat_botButtonPaint;
        r5 = "fonts/rmedium.ttf";
        r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5);
        r3.setTypeface(r5);
        r3 = new android.text.TextPaint;
        r3.<init>(r4);
        chat_contactNamePaint = r3;
        r3 = chat_contactNamePaint;
        r5 = "fonts/rmedium.ttf";
        r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5);
        r3.setTypeface(r5);
        r3 = new android.text.TextPaint;
        r3.<init>(r4);
        chat_contactPhonePaint = r3;
        r3 = new android.text.TextPaint;
        r3.<init>(r4);
        chat_durationPaint = r3;
        r3 = new android.text.TextPaint;
        r3.<init>(r4);
        chat_gamePaint = r3;
        r3 = chat_gamePaint;
        r5 = "fonts/rmedium.ttf";
        r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5);
        r3.setTypeface(r5);
        r3 = new android.text.TextPaint;
        r3.<init>(r4);
        chat_shipmentPaint = r3;
        r3 = new android.text.TextPaint;
        r3.<init>(r4);
        chat_timePaint = r3;
        r3 = new android.text.TextPaint;
        r3.<init>(r4);
        chat_adminPaint = r3;
        r3 = new android.text.TextPaint;
        r3.<init>(r4);
        chat_namePaint = r3;
        r3 = chat_namePaint;
        r5 = "fonts/rmedium.ttf";
        r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5);
        r3.setTypeface(r5);
        r3 = new android.text.TextPaint;
        r3.<init>(r4);
        chat_forwardNamePaint = r3;
        r3 = new android.text.TextPaint;
        r3.<init>(r4);
        chat_replyNamePaint = r3;
        r3 = chat_replyNamePaint;
        r5 = "fonts/rmedium.ttf";
        r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5);
        r3.setTypeface(r5);
        r3 = new android.text.TextPaint;
        r3.<init>(r4);
        chat_replyTextPaint = r3;
        r3 = new android.text.TextPaint;
        r3.<init>(r4);
        chat_instantViewPaint = r3;
        r3 = chat_instantViewPaint;
        r5 = "fonts/rmedium.ttf";
        r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5);
        r3.setTypeface(r5);
        r3 = new android.graphics.Paint;
        r3.<init>(r4);
        chat_instantViewRectPaint = r3;
        r3 = chat_instantViewRectPaint;
        r5 = android.graphics.Paint.Style.STROKE;
        r3.setStyle(r5);
        r3 = new android.graphics.Paint;
        r3.<init>();
        chat_replyLinePaint = r3;
        r3 = new android.graphics.Paint;
        r3.<init>(r4);
        chat_msgErrorPaint = r3;
        r3 = new android.graphics.Paint;
        r3.<init>(r4);
        chat_statusPaint = r3;
        r3 = new android.graphics.Paint;
        r3.<init>(r4);
        chat_statusRecordPaint = r3;
        r3 = chat_statusRecordPaint;
        r5 = android.graphics.Paint.Style.STROKE;
        r3.setStyle(r5);
        r3 = chat_statusRecordPaint;
        r5 = android.graphics.Paint.Cap.ROUND;
        r3.setStrokeCap(r5);
        r3 = new android.text.TextPaint;
        r3.<init>(r4);
        chat_actionTextPaint = r3;
        r3 = chat_actionTextPaint;
        r5 = "fonts/rmedium.ttf";
        r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5);
        r3.setTypeface(r5);
        r3 = new android.graphics.Paint;
        r3.<init>(r4);
        chat_actionBackgroundPaint = r3;
        r3 = new android.graphics.Paint;
        r3.<init>(r4);
        chat_timeBackgroundPaint = r3;
        r3 = new android.text.TextPaint;
        r3.<init>(r4);
        chat_contextResult_titleTextPaint = r3;
        r3 = chat_contextResult_titleTextPaint;
        r5 = "fonts/rmedium.ttf";
        r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5);
        r3.setTypeface(r5);
        r3 = new android.text.TextPaint;
        r3.<init>(r4);
        chat_contextResult_descriptionTextPaint = r3;
        r3 = new android.graphics.Paint;
        r3.<init>();
        chat_composeBackgroundPaint = r3;
        r3 = r17.getResources();
        r5 = NUM; // 0x7f070149 float:1.7945245E38 double:1.0529356656E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgInDrawable = r5;
        r5 = NUM; // 0x7f070149 float:1.7945245E38 double:1.0529356656E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgInSelectedDrawable = r5;
        r5 = NUM; // 0x7f07014d float:1.7945253E38 double:1.0529356676E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgOutDrawable = r5;
        r5 = NUM; // 0x7f07014d float:1.7945253E38 double:1.0529356676E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgOutSelectedDrawable = r5;
        r5 = NUM; // 0x7f070153 float:1.7945266E38 double:1.0529356705E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgInMediaDrawable = r5;
        r5 = NUM; // 0x7f070153 float:1.7945266E38 double:1.0529356705E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgInMediaSelectedDrawable = r5;
        r5 = NUM; // 0x7f070153 float:1.7945266E38 double:1.0529356705E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgOutMediaDrawable = r5;
        r5 = NUM; // 0x7f070153 float:1.7945266E38 double:1.0529356705E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgOutMediaSelectedDrawable = r5;
        r5 = NUM; // 0x7f070145 float:1.7945237E38 double:1.0529356636E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgOutCheckDrawable = r5;
        r5 = NUM; // 0x7f070145 float:1.7945237E38 double:1.0529356636E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgOutCheckSelectedDrawable = r5;
        r5 = NUM; // 0x7f070145 float:1.7945237E38 double:1.0529356636E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgMediaCheckDrawable = r5;
        r5 = NUM; // 0x7f070145 float:1.7945237E38 double:1.0529356636E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgStickerCheckDrawable = r5;
        r5 = NUM; // 0x7f070148 float:1.7945243E38 double:1.052935665E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgOutHalfCheckDrawable = r5;
        r5 = NUM; // 0x7f070148 float:1.7945243E38 double:1.052935665E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgOutHalfCheckSelectedDrawable = r5;
        r5 = NUM; // 0x7f070148 float:1.7945243E38 double:1.052935665E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgMediaHalfCheckDrawable = r5;
        r5 = NUM; // 0x7f070148 float:1.7945243E38 double:1.052935665E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgStickerHalfCheckDrawable = r5;
        r5 = NUM; // 0x7f070146 float:1.794524E38 double:1.052935664E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgOutClockDrawable = r5;
        r5 = NUM; // 0x7f070146 float:1.794524E38 double:1.052935664E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgOutSelectedClockDrawable = r5;
        r5 = NUM; // 0x7f070146 float:1.794524E38 double:1.052935664E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgInClockDrawable = r5;
        r5 = NUM; // 0x7f070146 float:1.794524E38 double:1.052935664E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgInSelectedClockDrawable = r5;
        r5 = NUM; // 0x7f070146 float:1.794524E38 double:1.052935664E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgMediaClockDrawable = r5;
        r5 = NUM; // 0x7f070146 float:1.794524E38 double:1.052935664E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgStickerClockDrawable = r5;
        r5 = NUM; // 0x7f07015b float:1.7945282E38 double:1.0529356745E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgInViewsDrawable = r5;
        r5 = NUM; // 0x7f07015b float:1.7945282E38 double:1.0529356745E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgInViewsSelectedDrawable = r5;
        r5 = NUM; // 0x7f07015b float:1.7945282E38 double:1.0529356745E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgOutViewsDrawable = r5;
        r5 = NUM; // 0x7f07015b float:1.7945282E38 double:1.0529356745E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgOutViewsSelectedDrawable = r5;
        r5 = NUM; // 0x7f07015b float:1.7945282E38 double:1.0529356745E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgMediaViewsDrawable = r5;
        r5 = NUM; // 0x7f07015b float:1.7945282E38 double:1.0529356745E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgStickerViewsDrawable = r5;
        r5 = NUM; // 0x7f070144 float:1.7945235E38 double:1.052935663E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgInMenuDrawable = r5;
        r5 = NUM; // 0x7f070144 float:1.7945235E38 double:1.052935663E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgInMenuSelectedDrawable = r5;
        r5 = NUM; // 0x7f070144 float:1.7945235E38 double:1.052935663E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgOutMenuDrawable = r5;
        r5 = NUM; // 0x7f070144 float:1.7945235E38 double:1.052935663E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgOutMenuSelectedDrawable = r5;
        r5 = NUM; // 0x7f0701f9 float:1.7945602E38 double:1.0529357525E-314;
        r5 = r3.getDrawable(r5);
        chat_msgMediaMenuDrawable = r5;
        r5 = NUM; // 0x7f07014b float:1.794525E38 double:1.0529356666E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgInInstantDrawable = r5;
        r5 = NUM; // 0x7f07014b float:1.794525E38 double:1.0529356666E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgOutInstantDrawable = r5;
        r5 = NUM; // 0x7f07015c float:1.7945284E38 double:1.052935675E-314;
        r5 = r3.getDrawable(r5);
        chat_msgErrorDrawable = r5;
        r5 = NUM; // 0x7f070115 float:1.794514E38 double:1.05293564E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_muteIconDrawable = r5;
        r5 = NUM; // 0x7f0700c9 float:1.7944986E38 double:1.0529356023E-314;
        r5 = r3.getDrawable(r5);
        chat_lockIconDrawable = r5;
        r5 = NUM; // 0x7f070046 float:1.794472E38 double:1.0529355376E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgBroadcastDrawable = r5;
        r5 = NUM; // 0x7f070046 float:1.794472E38 double:1.0529355376E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgBroadcastMediaDrawable = r5;
        r5 = NUM; // 0x7f0700bc float:1.794496E38 double:1.052935596E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgInCallDrawable = r5;
        r5 = NUM; // 0x7f0700bc float:1.794496E38 double:1.052935596E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgInCallSelectedDrawable = r5;
        r5 = NUM; // 0x7f0700bc float:1.794496E38 double:1.052935596E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgOutCallDrawable = r5;
        r5 = NUM; // 0x7f0700bc float:1.794496E38 double:1.052935596E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgOutCallSelectedDrawable = r5;
        r5 = NUM; // 0x7f0700b8 float:1.7944951E38 double:1.052935594E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgCallUpRedDrawable = r5;
        r5 = NUM; // 0x7f0700b8 float:1.7944951E38 double:1.052935594E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgCallUpGreenDrawable = r5;
        r5 = NUM; // 0x7f0700bb float:1.7944957E38 double:1.0529355954E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgCallDownRedDrawable = r5;
        r5 = NUM; // 0x7f0700bb float:1.7944957E38 double:1.0529355954E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgCallDownGreenDrawable = r5;
        r5 = NUM; // 0x7f07011c float:1.7945154E38 double:1.0529356433E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_msgAvatarLiveLocationDrawable = r5;
        r5 = NUM; // 0x7f07003b float:1.7944698E38 double:1.052935532E-314;
        r5 = r3.getDrawable(r5);
        chat_inlineResultFile = r5;
        r5 = NUM; // 0x7f070041 float:1.794471E38 double:1.052935535E-314;
        r5 = r3.getDrawable(r5);
        chat_inlineResultAudio = r5;
        r5 = NUM; // 0x7f070040 float:1.7944708E38 double:1.0529355346E-314;
        r5 = r3.getDrawable(r5);
        chat_inlineResultLocation = r5;
        r5 = NUM; // 0x7f07014a float:1.7945247E38 double:1.052935666E-314;
        r5 = r3.getDrawable(r5);
        chat_msgInShadowDrawable = r5;
        r5 = NUM; // 0x7f07014e float:1.7945255E38 double:1.052935668E-314;
        r5 = r3.getDrawable(r5);
        chat_msgOutShadowDrawable = r5;
        r5 = NUM; // 0x7f070154 float:1.7945268E38 double:1.052935671E-314;
        r5 = r3.getDrawable(r5);
        chat_msgInMediaShadowDrawable = r5;
        r5 = NUM; // 0x7f070154 float:1.7945268E38 double:1.052935671E-314;
        r5 = r3.getDrawable(r5);
        chat_msgOutMediaShadowDrawable = r5;
        r5 = NUM; // 0x7f07003f float:1.7944706E38 double:1.052935534E-314;
        r5 = r3.getDrawable(r5);
        chat_botLinkDrawalbe = r5;
        r5 = NUM; // 0x7f07003e float:1.7944704E38 double:1.0529355337E-314;
        r5 = r3.getDrawable(r5);
        chat_botInlineDrawable = r5;
        r5 = NUM; // 0x7f0701e4 float:1.794556E38 double:1.052935742E-314;
        r5 = r3.getDrawable(r5);
        chat_systemDrawable = r5;
        r5 = NUM; // 0x7f07009e float:1.7944898E38 double:1.052935581E-314;
        r5 = r3.getDrawable(r5);
        r5 = r5.mutate();
        chat_contextResult_shadowUnderSwitchDrawable = r5;
        r5 = chat_attachButtonDrawables;
        r6 = NUM; // 0x7f07000e float:1.7944606E38 double:1.05293551E-314;
        r6 = r3.getDrawable(r6);
        r7 = 0;
        r5[r7] = r6;
        r5 = chat_attachButtonDrawables;
        r6 = NUM; // 0x7f070017 float:1.7944625E38 double:1.0529355144E-314;
        r6 = r3.getDrawable(r6);
        r5[r4] = r6;
        r5 = chat_attachButtonDrawables;
        r6 = NUM; // 0x7f070025 float:1.7944653E38 double:1.0529355213E-314;
        r6 = r3.getDrawable(r6);
        r5[r2] = r6;
        r5 = chat_attachButtonDrawables;
        r6 = NUM; // 0x7f07000b float:1.79446E38 double:1.0529355085E-314;
        r6 = r3.getDrawable(r6);
        r8 = 3;
        r5[r8] = r6;
        r5 = chat_attachButtonDrawables;
        r6 = NUM; // 0x7f070014 float:1.7944619E38 double:1.052935513E-314;
        r6 = r3.getDrawable(r6);
        r9 = 4;
        r5[r9] = r6;
        r5 = chat_attachButtonDrawables;
        r6 = NUM; // 0x7f070011 float:1.7944612E38 double:1.0529355114E-314;
        r6 = r3.getDrawable(r6);
        r10 = 5;
        r5[r10] = r6;
        r5 = chat_attachButtonDrawables;
        r6 = 6;
        r11 = NUM; // 0x7f07001e float:1.7944639E38 double:1.052935518E-314;
        r11 = r3.getDrawable(r11);
        r5[r6] = r11;
        r5 = chat_attachButtonDrawables;
        r6 = 7;
        r11 = NUM; // 0x7f07001b float:1.7944633E38 double:1.0529355164E-314;
        r11 = r3.getDrawable(r11);
        r5[r6] = r11;
        r5 = chat_cornerOuter;
        r6 = NUM; // 0x7f070079 float:1.7944823E38 double:1.052935563E-314;
        r6 = r3.getDrawable(r6);
        r5[r7] = r6;
        r5 = chat_cornerOuter;
        r6 = NUM; // 0x7f07007a float:1.7944825E38 double:1.0529355633E-314;
        r6 = r3.getDrawable(r6);
        r5[r4] = r6;
        r5 = chat_cornerOuter;
        r6 = NUM; // 0x7f070078 float:1.7944821E38 double:1.0529355623E-314;
        r6 = r3.getDrawable(r6);
        r5[r2] = r6;
        r5 = chat_cornerOuter;
        r6 = NUM; // 0x7f070077 float:1.794482E38 double:1.052935562E-314;
        r6 = r3.getDrawable(r6);
        r5[r8] = r6;
        r5 = chat_cornerInner;
        r6 = NUM; // 0x7f070076 float:1.7944817E38 double:1.0529355613E-314;
        r6 = r3.getDrawable(r6);
        r5[r7] = r6;
        r5 = chat_cornerInner;
        r6 = NUM; // 0x7f070075 float:1.7944815E38 double:1.052935561E-314;
        r6 = r3.getDrawable(r6);
        r5[r4] = r6;
        r5 = chat_cornerInner;
        r6 = NUM; // 0x7f070074 float:1.7944813E38 double:1.0529355603E-314;
        r6 = r3.getDrawable(r6);
        r5[r2] = r6;
        r5 = chat_cornerInner;
        r6 = NUM; // 0x7f070073 float:1.7944811E38 double:1.05293556E-314;
        r6 = r3.getDrawable(r6);
        r5[r8] = r6;
        r5 = NUM; // 0x7f0701c4 float:1.7945495E38 double:1.0529357263E-314;
        r5 = r3.getDrawable(r5);
        chat_shareDrawable = r5;
        r5 = NUM; // 0x7f0701c3 float:1.7945493E38 double:1.052935726E-314;
        r5 = r3.getDrawable(r5);
        chat_shareIconDrawable = r5;
        r5 = NUM; // 0x7f070081 float:1.794484E38 double:1.052935567E-314;
        r5 = r3.getDrawable(r5);
        chat_replyIconDrawable = r5;
        r5 = NUM; // 0x7f07013e float:1.7945223E38 double:1.05293566E-314;
        r5 = r3.getDrawable(r5);
        chat_goIconDrawable = r5;
        r5 = chat_ivStatesDrawable;
        r5 = r5[r7];
        r6 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r12 = NUM; // 0x7f07015a float:1.794528E38 double:1.052935674E-314;
        r11 = createCircleDrawableWithIcon(r11, r12, r4);
        r5[r7] = r11;
        r5 = chat_ivStatesDrawable;
        r5 = r5[r7];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = createCircleDrawableWithIcon(r11, r12, r4);
        r5[r4] = r11;
        r5 = chat_ivStatesDrawable;
        r5 = r5[r4];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r13 = NUM; // 0x7f070159 float:1.7945278E38 double:1.0529356735E-314;
        r11 = createCircleDrawableWithIcon(r11, r13, r4);
        r5[r7] = r11;
        r5 = chat_ivStatesDrawable;
        r5 = r5[r4];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = createCircleDrawableWithIcon(r11, r13, r4);
        r5[r4] = r11;
        r5 = chat_ivStatesDrawable;
        r5 = r5[r2];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r13 = NUM; // 0x7f070158 float:1.7945276E38 double:1.052935673E-314;
        r11 = createCircleDrawableWithIcon(r11, r13, r4);
        r5[r7] = r11;
        r5 = chat_ivStatesDrawable;
        r5 = r5[r2];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = createCircleDrawableWithIcon(r11, r13, r4);
        r5[r4] = r11;
        r5 = chat_ivStatesDrawable;
        r5 = r5[r8];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r14 = NUM; // 0x7f070155 float:1.794527E38 double:1.0529356715E-314;
        r11 = createCircleDrawableWithIcon(r11, r14, r2);
        r5[r7] = r11;
        r5 = chat_ivStatesDrawable;
        r5 = r5[r8];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = createCircleDrawableWithIcon(r6, r14, r2);
        r5[r4] = r6;
        r5 = chat_fileMiniStatesDrawable;
        r5 = r5[r7];
        r6 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r15 = NUM; // 0x7f070026 float:1.7944655E38 double:1.052935522E-314;
        r11 = createCircleDrawableWithIcon(r11, r15);
        r5[r7] = r11;
        r5 = chat_fileMiniStatesDrawable;
        r5 = r5[r7];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = createCircleDrawableWithIcon(r11, r15);
        r5[r4] = r11;
        r5 = chat_fileMiniStatesDrawable;
        r5 = r5[r4];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r15 = NUM; // 0x7f070027 float:1.7944657E38 double:1.0529355223E-314;
        r11 = createCircleDrawableWithIcon(r11, r15);
        r5[r7] = r11;
        r5 = chat_fileMiniStatesDrawable;
        r5 = r5[r4];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = createCircleDrawableWithIcon(r11, r15);
        r5[r4] = r11;
        r5 = chat_fileMiniStatesDrawable;
        r5 = r5[r2];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r15 = NUM; // 0x7f070026 float:1.7944655E38 double:1.052935522E-314;
        r11 = createCircleDrawableWithIcon(r11, r15);
        r5[r7] = r11;
        r5 = chat_fileMiniStatesDrawable;
        r5 = r5[r2];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = createCircleDrawableWithIcon(r11, r15);
        r5[r4] = r11;
        r5 = chat_fileMiniStatesDrawable;
        r5 = r5[r8];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r15 = NUM; // 0x7f070027 float:1.7944657E38 double:1.0529355223E-314;
        r11 = createCircleDrawableWithIcon(r11, r15);
        r5[r7] = r11;
        r5 = chat_fileMiniStatesDrawable;
        r5 = r5[r8];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = createCircleDrawableWithIcon(r11, r15);
        r5[r4] = r11;
        r5 = chat_fileMiniStatesDrawable;
        r5 = r5[r9];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r15 = NUM; // 0x7f0701fd float:1.794561E38 double:1.0529357545E-314;
        r11 = createCircleDrawableWithIcon(r11, r15);
        r5[r7] = r11;
        r5 = chat_fileMiniStatesDrawable;
        r5 = r5[r9];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = createCircleDrawableWithIcon(r11, r15);
        r5[r4] = r11;
        r5 = chat_fileMiniStatesDrawable;
        r5 = r5[r10];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r15 = NUM; // 0x7f0701fe float:1.7945612E38 double:1.052935755E-314;
        r11 = createCircleDrawableWithIcon(r11, r15);
        r5[r7] = r11;
        r5 = chat_fileMiniStatesDrawable;
        r5 = r5[r10];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = NUM; // 0x7f0701fe float:1.7945612E38 double:1.052935755E-314;
        r6 = createCircleDrawableWithIcon(r6, r11);
        r5[r4] = r6;
        r5 = chat_fileStatesDrawable;
        r5 = r5[r7];
        r6 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = createCircleDrawableWithIcon(r11, r12);
        r5[r7] = r11;
        r5 = chat_fileStatesDrawable;
        r5 = r5[r7];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = createCircleDrawableWithIcon(r11, r12);
        r5[r4] = r11;
        r5 = chat_fileStatesDrawable;
        r5 = r5[r4];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r15 = NUM; // 0x7f070159 float:1.7945278E38 double:1.0529356735E-314;
        r11 = createCircleDrawableWithIcon(r11, r15);
        r5[r7] = r11;
        r5 = chat_fileStatesDrawable;
        r5 = r5[r4];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = createCircleDrawableWithIcon(r11, r15);
        r5[r4] = r11;
        r5 = chat_fileStatesDrawable;
        r5 = r5[r2];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = createCircleDrawableWithIcon(r11, r13);
        r5[r7] = r11;
        r5 = chat_fileStatesDrawable;
        r5 = r5[r2];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = createCircleDrawableWithIcon(r11, r13);
        r5[r4] = r11;
        r5 = chat_fileStatesDrawable;
        r5 = r5[r8];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r15 = NUM; // 0x7f070156 float:1.7945272E38 double:1.052935672E-314;
        r11 = createCircleDrawableWithIcon(r11, r15);
        r5[r7] = r11;
        r5 = chat_fileStatesDrawable;
        r5 = r5[r8];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = createCircleDrawableWithIcon(r11, r15);
        r5[r4] = r11;
        r5 = chat_fileStatesDrawable;
        r5 = r5[r9];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = createCircleDrawableWithIcon(r11, r14);
        r5[r7] = r11;
        r5 = chat_fileStatesDrawable;
        r5 = r5[r9];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = createCircleDrawableWithIcon(r11, r14);
        r5[r4] = r11;
        r5 = chat_fileStatesDrawable;
        r5 = r5[r10];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = createCircleDrawableWithIcon(r11, r12);
        r5[r7] = r11;
        r5 = chat_fileStatesDrawable;
        r5 = r5[r10];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = createCircleDrawableWithIcon(r11, r12);
        r5[r4] = r11;
        r5 = chat_fileStatesDrawable;
        r11 = 6;
        r5 = r5[r11];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r15 = NUM; // 0x7f070159 float:1.7945278E38 double:1.0529356735E-314;
        r11 = createCircleDrawableWithIcon(r11, r15);
        r5[r7] = r11;
        r5 = chat_fileStatesDrawable;
        r11 = 6;
        r5 = r5[r11];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = createCircleDrawableWithIcon(r11, r15);
        r5[r4] = r11;
        r5 = chat_fileStatesDrawable;
        r11 = 7;
        r5 = r5[r11];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = createCircleDrawableWithIcon(r11, r13);
        r5[r7] = r11;
        r5 = chat_fileStatesDrawable;
        r11 = 7;
        r5 = r5[r11];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = createCircleDrawableWithIcon(r11, r13);
        r5[r4] = r11;
        r5 = chat_fileStatesDrawable;
        r11 = 8;
        r5 = r5[r11];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r15 = NUM; // 0x7f070156 float:1.7945272E38 double:1.052935672E-314;
        r11 = createCircleDrawableWithIcon(r11, r15);
        r5[r7] = r11;
        r5 = chat_fileStatesDrawable;
        r11 = 8;
        r5 = r5[r11];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = createCircleDrawableWithIcon(r11, r15);
        r5[r4] = r11;
        r5 = chat_fileStatesDrawable;
        r11 = 9;
        r5 = r5[r11];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = createCircleDrawableWithIcon(r11, r14);
        r5[r7] = r11;
        r5 = chat_fileStatesDrawable;
        r11 = 9;
        r5 = r5[r11];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r11 = createCircleDrawableWithIcon(r11, r14);
        r5[r4] = r11;
        r5 = chat_photoStatesDrawables;
        r5 = r5[r7];
        r11 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r15 = createCircleDrawableWithIcon(r15, r13);
        r5[r7] = r15;
        r5 = chat_photoStatesDrawables;
        r5 = r5[r7];
        r15 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r15 = createCircleDrawableWithIcon(r15, r13);
        r5[r4] = r15;
        r5 = chat_photoStatesDrawables;
        r5 = r5[r4];
        r15 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r15 = createCircleDrawableWithIcon(r15, r14);
        r5[r7] = r15;
        r5 = chat_photoStatesDrawables;
        r5 = r5[r4];
        r15 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r15 = createCircleDrawableWithIcon(r15, r14);
        r5[r4] = r15;
        r5 = chat_photoStatesDrawables;
        r5 = r5[r2];
        r15 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r6 = NUM; // 0x7f070157 float:1.7945274E38 double:1.0529356725E-314;
        r6 = createCircleDrawableWithIcon(r15, r6);
        r5[r7] = r6;
        r5 = chat_photoStatesDrawables;
        r5 = r5[r2];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r15 = NUM; // 0x7f070157 float:1.7945274E38 double:1.0529356725E-314;
        r6 = createCircleDrawableWithIcon(r6, r15);
        r5[r4] = r6;
        r5 = chat_photoStatesDrawables;
        r5 = r5[r8];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r6 = createCircleDrawableWithIcon(r6, r12);
        r5[r7] = r6;
        r5 = chat_photoStatesDrawables;
        r5 = r5[r8];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r6 = createCircleDrawableWithIcon(r6, r12);
        r5[r4] = r6;
        r5 = chat_photoStatesDrawables;
        r5 = r5[r9];
        r6 = chat_photoStatesDrawables;
        r6 = r6[r9];
        r8 = NUM; // 0x7f070049 float:1.7944726E38 double:1.052935539E-314;
        r8 = r3.getDrawable(r8);
        r6[r4] = r8;
        r5[r7] = r8;
        r5 = chat_photoStatesDrawables;
        r5 = r5[r10];
        r6 = chat_photoStatesDrawables;
        r6 = r6[r10];
        r8 = NUM; // 0x7f07005a float:1.794476E38 double:1.0529355475E-314;
        r8 = r3.getDrawable(r8);
        r6[r4] = r8;
        r5[r7] = r8;
        r5 = chat_photoStatesDrawables;
        r6 = 6;
        r5 = r5[r6];
        r6 = chat_photoStatesDrawables;
        r8 = 6;
        r6 = r6[r8];
        r8 = NUM; // 0x7f070197 float:1.7945403E38 double:1.052935704E-314;
        r8 = r3.getDrawable(r8);
        r6[r4] = r8;
        r5[r7] = r8;
        r5 = chat_photoStatesDrawables;
        r6 = 7;
        r5 = r5[r6];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r6 = createCircleDrawableWithIcon(r6, r13);
        r5[r7] = r6;
        r5 = chat_photoStatesDrawables;
        r6 = 7;
        r5 = r5[r6];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r6 = createCircleDrawableWithIcon(r6, r13);
        r5[r4] = r6;
        r5 = chat_photoStatesDrawables;
        r6 = 8;
        r5 = r5[r6];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r6 = createCircleDrawableWithIcon(r6, r14);
        r5[r7] = r6;
        r5 = chat_photoStatesDrawables;
        r6 = 8;
        r5 = r5[r6];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r6 = createCircleDrawableWithIcon(r6, r14);
        r5[r4] = r6;
        r5 = chat_photoStatesDrawables;
        r6 = 9;
        r5 = r5[r6];
        r6 = NUM; // 0x7f07007d float:1.7944831E38 double:1.052935565E-314;
        r6 = r3.getDrawable(r6);
        r6 = r6.mutate();
        r5[r7] = r6;
        r5 = chat_photoStatesDrawables;
        r6 = 9;
        r5 = r5[r6];
        r6 = NUM; // 0x7f07007d float:1.7944831E38 double:1.052935565E-314;
        r6 = r3.getDrawable(r6);
        r6 = r6.mutate();
        r5[r4] = r6;
        r5 = chat_photoStatesDrawables;
        r6 = 10;
        r5 = r5[r6];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r6 = createCircleDrawableWithIcon(r6, r13);
        r5[r7] = r6;
        r5 = chat_photoStatesDrawables;
        r6 = 10;
        r5 = r5[r6];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r6 = createCircleDrawableWithIcon(r6, r13);
        r5[r4] = r6;
        r5 = chat_photoStatesDrawables;
        r6 = 11;
        r5 = r5[r6];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r6 = createCircleDrawableWithIcon(r6, r14);
        r5[r7] = r6;
        r5 = chat_photoStatesDrawables;
        r6 = 11;
        r5 = r5[r6];
        r6 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r6 = createCircleDrawableWithIcon(r6, r14);
        r5[r4] = r6;
        r5 = chat_photoStatesDrawables;
        r6 = 12;
        r5 = r5[r6];
        r6 = NUM; // 0x7f07007d float:1.7944831E38 double:1.052935565E-314;
        r6 = r3.getDrawable(r6);
        r6 = r6.mutate();
        r5[r7] = r6;
        r5 = chat_photoStatesDrawables;
        r6 = 12;
        r5 = r5[r6];
        r6 = NUM; // 0x7f07007d float:1.7944831E38 double:1.052935565E-314;
        r3 = r3.getDrawable(r6);
        r3 = r3.mutate();
        r5[r4] = r3;
        r3 = chat_contactDrawable;
        r5 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r8 = NUM; // 0x7f070147 float:1.7945241E38 double:1.0529356646E-314;
        r6 = createCircleDrawableWithIcon(r6, r8);
        r3[r7] = r6;
        r3 = chat_contactDrawable;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = NUM; // 0x7f070147 float:1.7945241E38 double:1.0529356646E-314;
        r5 = createCircleDrawableWithIcon(r5, r6);
        r3[r4] = r5;
        r3 = chat_locationDrawable;
        r5 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = NUM; // 0x7f07014c float:1.7945251E38 double:1.052935667E-314;
        r5 = createRoundRectDrawableWithIcon(r5, r6);
        r3[r7] = r5;
        r3 = chat_locationDrawable;
        r5 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = createRoundRectDrawableWithIcon(r5, r6);
        r3[r4] = r5;
        r3 = r17.getResources();
        r5 = NUM; // 0x7f070072 float:1.794481E38 double:1.0529355594E-314;
        r3 = r3.getDrawable(r5);
        chat_composeShadowDrawable = r3;
        r3 = org.telegram.messenger.AndroidUtilities.roundMessageSize;	 Catch:{ Throwable -> 0x0a54 }
        r5 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;	 Catch:{ Throwable -> 0x0a54 }
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);	 Catch:{ Throwable -> 0x0a54 }
        r3 = r3 + r5;	 Catch:{ Throwable -> 0x0a54 }
        r5 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0a54 }
        r5 = android.graphics.Bitmap.createBitmap(r3, r3, r5);	 Catch:{ Throwable -> 0x0a54 }
        r6 = new android.graphics.Canvas;	 Catch:{ Throwable -> 0x0a54 }
        r6.<init>(r5);	 Catch:{ Throwable -> 0x0a54 }
        r7 = new android.graphics.Paint;	 Catch:{ Throwable -> 0x0a54 }
        r7.<init>(r4);	 Catch:{ Throwable -> 0x0a54 }
        r4 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;	 Catch:{ Throwable -> 0x0a54 }
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Throwable -> 0x0a54 }
        r4 = (float) r4;	 Catch:{ Throwable -> 0x0a54 }
        r8 = 0;	 Catch:{ Throwable -> 0x0a54 }
        r9 = 0;	 Catch:{ Throwable -> 0x0a54 }
        r10 = NUM; // 0x5f000000 float:9.223372E18 double:7.874593756E-315;	 Catch:{ Throwable -> 0x0a54 }
        r7.setShadowLayer(r4, r8, r9, r10);	 Catch:{ Throwable -> 0x0a54 }
        r4 = r3 / 2;	 Catch:{ Throwable -> 0x0a54 }
        r4 = (float) r4;	 Catch:{ Throwable -> 0x0a54 }
        r3 = r3 / r2;	 Catch:{ Throwable -> 0x0a54 }
        r3 = (float) r3;	 Catch:{ Throwable -> 0x0a54 }
        r8 = org.telegram.messenger.AndroidUtilities.roundMessageSize;	 Catch:{ Throwable -> 0x0a54 }
        r8 = r8 / r2;	 Catch:{ Throwable -> 0x0a54 }
        r9 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Throwable -> 0x0a54 }
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);	 Catch:{ Throwable -> 0x0a54 }
        r8 = r8 - r9;	 Catch:{ Throwable -> 0x0a54 }
        r8 = (float) r8;	 Catch:{ Throwable -> 0x0a54 }
        r6.drawCircle(r4, r3, r8, r7);	 Catch:{ Throwable -> 0x0a54 }
        r3 = 0;
        r6.setBitmap(r3);	 Catch:{ Exception -> 0x0a4d }
    L_0x0a4d:
        r3 = new android.graphics.drawable.BitmapDrawable;	 Catch:{ Throwable -> 0x0a54 }
        r3.<init>(r5);	 Catch:{ Throwable -> 0x0a54 }
        chat_roundVideoShadow = r3;	 Catch:{ Throwable -> 0x0a54 }
    L_0x0a54:
        applyChatTheme(r18);
    L_0x0a57:
        r3 = chat_msgTextPaintOneEmoji;
        r4 = NUM; // 0x41e00000 float:28.0 double:5.46040909E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r3.setTextSize(r4);
        r3 = chat_msgTextPaintTwoEmoji;
        r4 = NUM; // 0x41c00000 float:24.0 double:5.450047783E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r3.setTextSize(r4);
        r3 = chat_msgTextPaintThreeEmoji;
        r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r3.setTextSize(r4);
        r3 = chat_msgTextPaint;
        r4 = org.telegram.messenger.SharedConfig.fontSize;
        r4 = (float) r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r3.setTextSize(r4);
        r3 = chat_msgGameTextPaint;
        r4 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r3.setTextSize(r4);
        r3 = chat_msgBotButtonPaint;
        r4 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = (float) r5;
        r3.setTextSize(r5);
        if (r18 != 0) goto L_0x0bf2;
    L_0x0aa2:
        r1 = chat_botProgressPaint;
        if (r1 == 0) goto L_0x0bf2;
    L_0x0aa6:
        r1 = chat_botProgressPaint;
        r3 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r1.setStrokeWidth(r3);
        r1 = chat_infoPaint;
        r3 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r1.setTextSize(r3);
        r1 = chat_docNamePaint;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = (float) r3;
        r1.setTextSize(r3);
        r1 = chat_locationTitlePaint;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = (float) r3;
        r1.setTextSize(r3);
        r1 = chat_locationAddressPaint;
        r3 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r5 = (float) r5;
        r1.setTextSize(r5);
        r1 = chat_audioTimePaint;
        r5 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r1.setTextSize(r5);
        r1 = chat_livePaint;
        r5 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r1.setTextSize(r5);
        r1 = chat_audioTitlePaint;
        r5 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r1.setTextSize(r5);
        r1 = chat_audioPerformerPaint;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = (float) r5;
        r1.setTextSize(r5);
        r1 = chat_botButtonPaint;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = (float) r5;
        r1.setTextSize(r5);
        r1 = chat_contactNamePaint;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = (float) r5;
        r1.setTextSize(r5);
        r1 = chat_contactPhonePaint;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r5 = (float) r5;
        r1.setTextSize(r5);
        r1 = chat_durationPaint;
        r5 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r1.setTextSize(r5);
        r1 = chat_timePaint;
        r5 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r1.setTextSize(r5);
        r1 = chat_adminPaint;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r5 = (float) r5;
        r1.setTextSize(r5);
        r1 = chat_namePaint;
        r5 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r1.setTextSize(r5);
        r1 = chat_forwardNamePaint;
        r5 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r1.setTextSize(r5);
        r1 = chat_replyNamePaint;
        r5 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r1.setTextSize(r5);
        r1 = chat_replyTextPaint;
        r5 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r1.setTextSize(r5);
        r1 = chat_gamePaint;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r5 = (float) r5;
        r1.setTextSize(r5);
        r1 = chat_shipmentPaint;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r5 = (float) r5;
        r1.setTextSize(r5);
        r1 = chat_instantViewPaint;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r5 = (float) r5;
        r1.setTextSize(r5);
        r1 = chat_instantViewRectPaint;
        r5 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r1.setStrokeWidth(r5);
        r1 = chat_statusRecordPaint;
        r5 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r1.setStrokeWidth(r5);
        r1 = chat_actionTextPaint;
        r5 = 16;
        r6 = org.telegram.messenger.SharedConfig.fontSize;
        r5 = java.lang.Math.max(r5, r6);
        r5 = r5 - r2;
        r2 = (float) r5;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r1.setTextSize(r2);
        r1 = chat_contextResult_titleTextPaint;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = (float) r2;
        r1.setTextSize(r2);
        r1 = chat_contextResult_descriptionTextPaint;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = (float) r2;
        r1.setTextSize(r2);
        r1 = chat_radialProgressPaint;
        r2 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r1.setStrokeWidth(r2);
        r1 = chat_radialProgress2Paint;
        r2 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r1.setStrokeWidth(r2);
    L_0x0bf2:
        return;
    L_0x0bf3:
        r0 = move-exception;
        r1 = r0;
        monitor-exit(r2);	 Catch:{ all -> 0x0bf3 }
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.createChatResources(android.content.Context, boolean):void");
    }

    public static void applyChatTheme(boolean z) {
        if (!(chat_msgTextPaint == null || chat_msgInDrawable == null || z)) {
            int i;
            int i2;
            chat_gamePaint.setColor(getColor(key_chat_previewGameText));
            chat_durationPaint.setColor(getColor(key_chat_previewDurationText));
            chat_botButtonPaint.setColor(getColor(key_chat_botButtonText));
            chat_urlPaint.setColor(getColor(key_chat_linkSelectBackground));
            chat_botProgressPaint.setColor(getColor(key_chat_botProgress));
            chat_deleteProgressPaint.setColor(getColor(key_chat_secretTimeText));
            chat_textSearchSelectionPaint.setColor(getColor(key_chat_textSelectBackground));
            chat_msgErrorPaint.setColor(getColor(key_chat_sentError));
            chat_statusPaint.setColor(getColor(key_actionBarDefaultSubtitle));
            chat_statusRecordPaint.setColor(getColor(key_actionBarDefaultSubtitle));
            chat_actionTextPaint.setColor(getColor(key_chat_serviceText));
            chat_actionTextPaint.linkColor = getColor(key_chat_serviceLink);
            chat_contextResult_titleTextPaint.setColor(getColor(key_windowBackgroundWhiteBlackText));
            chat_composeBackgroundPaint.setColor(getColor(key_chat_messagePanelBackground));
            chat_timeBackgroundPaint.setColor(getColor(key_chat_mediaTimeBackground));
            setDrawableColorByKey(chat_msgInDrawable, key_chat_inBubble);
            setDrawableColorByKey(chat_msgInSelectedDrawable, key_chat_inBubbleSelected);
            setDrawableColorByKey(chat_msgInShadowDrawable, key_chat_inBubbleShadow);
            setDrawableColorByKey(chat_msgOutDrawable, key_chat_outBubble);
            setDrawableColorByKey(chat_msgOutSelectedDrawable, key_chat_outBubbleSelected);
            setDrawableColorByKey(chat_msgOutShadowDrawable, key_chat_outBubbleShadow);
            setDrawableColorByKey(chat_msgInMediaDrawable, key_chat_inBubble);
            setDrawableColorByKey(chat_msgInMediaSelectedDrawable, key_chat_inBubbleSelected);
            setDrawableColorByKey(chat_msgInMediaShadowDrawable, key_chat_inBubbleShadow);
            setDrawableColorByKey(chat_msgOutMediaDrawable, key_chat_outBubble);
            setDrawableColorByKey(chat_msgOutMediaSelectedDrawable, key_chat_outBubbleSelected);
            setDrawableColorByKey(chat_msgOutMediaShadowDrawable, key_chat_outBubbleShadow);
            setDrawableColorByKey(chat_msgOutCheckDrawable, key_chat_outSentCheck);
            setDrawableColorByKey(chat_msgOutCheckSelectedDrawable, key_chat_outSentCheckSelected);
            setDrawableColorByKey(chat_msgOutHalfCheckDrawable, key_chat_outSentCheck);
            setDrawableColorByKey(chat_msgOutHalfCheckSelectedDrawable, key_chat_outSentCheckSelected);
            setDrawableColorByKey(chat_msgOutClockDrawable, key_chat_outSentClock);
            setDrawableColorByKey(chat_msgOutSelectedClockDrawable, key_chat_outSentClockSelected);
            setDrawableColorByKey(chat_msgInClockDrawable, key_chat_inSentClock);
            setDrawableColorByKey(chat_msgInSelectedClockDrawable, key_chat_inSentClockSelected);
            setDrawableColorByKey(chat_msgMediaCheckDrawable, key_chat_mediaSentCheck);
            setDrawableColorByKey(chat_msgMediaHalfCheckDrawable, key_chat_mediaSentCheck);
            setDrawableColorByKey(chat_msgMediaClockDrawable, key_chat_mediaSentClock);
            setDrawableColorByKey(chat_msgStickerCheckDrawable, key_chat_serviceText);
            setDrawableColorByKey(chat_msgStickerHalfCheckDrawable, key_chat_serviceText);
            setDrawableColorByKey(chat_msgStickerClockDrawable, key_chat_serviceText);
            setDrawableColorByKey(chat_msgStickerViewsDrawable, key_chat_serviceText);
            setDrawableColorByKey(chat_shareIconDrawable, key_chat_serviceIcon);
            setDrawableColorByKey(chat_replyIconDrawable, key_chat_serviceIcon);
            setDrawableColorByKey(chat_goIconDrawable, key_chat_serviceIcon);
            setDrawableColorByKey(chat_botInlineDrawable, key_chat_serviceIcon);
            setDrawableColorByKey(chat_botLinkDrawalbe, key_chat_serviceIcon);
            setDrawableColorByKey(chat_msgInViewsDrawable, key_chat_inViews);
            setDrawableColorByKey(chat_msgInViewsSelectedDrawable, key_chat_inViewsSelected);
            setDrawableColorByKey(chat_msgOutViewsDrawable, key_chat_outViews);
            setDrawableColorByKey(chat_msgOutViewsSelectedDrawable, key_chat_outViewsSelected);
            setDrawableColorByKey(chat_msgMediaViewsDrawable, key_chat_mediaViews);
            setDrawableColorByKey(chat_msgInMenuDrawable, key_chat_inMenu);
            setDrawableColorByKey(chat_msgInMenuSelectedDrawable, key_chat_inMenuSelected);
            setDrawableColorByKey(chat_msgOutMenuDrawable, key_chat_outMenu);
            setDrawableColorByKey(chat_msgOutMenuSelectedDrawable, key_chat_outMenuSelected);
            setDrawableColorByKey(chat_msgMediaMenuDrawable, key_chat_mediaMenu);
            setDrawableColorByKey(chat_msgOutInstantDrawable, key_chat_outInstant);
            setDrawableColorByKey(chat_msgInInstantDrawable, key_chat_inInstant);
            setDrawableColorByKey(chat_msgErrorDrawable, key_chat_sentErrorIcon);
            setDrawableColorByKey(chat_muteIconDrawable, key_chat_muteIcon);
            setDrawableColorByKey(chat_lockIconDrawable, key_chat_lockIcon);
            setDrawableColorByKey(chat_msgBroadcastDrawable, key_chat_outBroadcast);
            setDrawableColorByKey(chat_msgBroadcastMediaDrawable, key_chat_mediaBroadcast);
            setDrawableColorByKey(chat_inlineResultFile, key_chat_inlineResultIcon);
            setDrawableColorByKey(chat_inlineResultAudio, key_chat_inlineResultIcon);
            setDrawableColorByKey(chat_inlineResultLocation, key_chat_inlineResultIcon);
            setDrawableColorByKey(chat_msgInCallDrawable, key_chat_inInstant);
            setDrawableColorByKey(chat_msgInCallSelectedDrawable, key_chat_inInstantSelected);
            setDrawableColorByKey(chat_msgOutCallDrawable, key_chat_outInstant);
            setDrawableColorByKey(chat_msgOutCallSelectedDrawable, key_chat_outInstantSelected);
            setDrawableColorByKey(chat_msgCallUpRedDrawable, key_calls_callReceivedRedIcon);
            setDrawableColorByKey(chat_msgCallUpGreenDrawable, key_calls_callReceivedGreenIcon);
            setDrawableColorByKey(chat_msgCallDownRedDrawable, key_calls_callReceivedRedIcon);
            setDrawableColorByKey(chat_msgCallDownGreenDrawable, key_calls_callReceivedGreenIcon);
            for (i = 0; i < 2; i++) {
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i][0], getColor(key_chat_outLoader), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i][0], getColor(key_chat_outBubble), true);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i][1], getColor(key_chat_outLoaderSelected), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i][1], getColor(key_chat_outBubbleSelected), true);
                int i3 = 2 + i;
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i3][0], getColor(key_chat_inLoader), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i3][0], getColor(key_chat_inBubble), true);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i3][1], getColor(key_chat_inLoaderSelected), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i3][1], getColor(key_chat_inBubbleSelected), true);
                int i4 = 4 + i;
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i4][0], getColor(key_chat_mediaLoaderPhoto), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i4][0], getColor(key_chat_mediaLoaderPhotoIcon), true);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i4][1], getColor(key_chat_mediaLoaderPhotoSelected), false);
                setCombinedDrawableColor(chat_fileMiniStatesDrawable[i4][1], getColor(key_chat_mediaLoaderPhotoIconSelected), true);
            }
            for (i = 0; i < 5; i++) {
                setCombinedDrawableColor(chat_fileStatesDrawable[i][0], getColor(key_chat_outLoader), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[i][0], getColor(key_chat_outBubble), true);
                setCombinedDrawableColor(chat_fileStatesDrawable[i][1], getColor(key_chat_outLoaderSelected), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[i][1], getColor(key_chat_outBubbleSelected), true);
                i2 = 5 + i;
                setCombinedDrawableColor(chat_fileStatesDrawable[i2][0], getColor(key_chat_inLoader), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[i2][0], getColor(key_chat_inBubble), true);
                setCombinedDrawableColor(chat_fileStatesDrawable[i2][1], getColor(key_chat_inLoaderSelected), false);
                setCombinedDrawableColor(chat_fileStatesDrawable[i2][1], getColor(key_chat_inBubbleSelected), true);
            }
            for (i = 0; i < 4; i++) {
                setCombinedDrawableColor(chat_photoStatesDrawables[i][0], getColor(key_chat_mediaLoaderPhoto), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i][0], getColor(key_chat_mediaLoaderPhotoIcon), true);
                setCombinedDrawableColor(chat_photoStatesDrawables[i][1], getColor(key_chat_mediaLoaderPhotoSelected), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i][1], getColor(key_chat_mediaLoaderPhotoIconSelected), true);
            }
            for (i = 0; i < 2; i++) {
                i2 = 7 + i;
                setCombinedDrawableColor(chat_photoStatesDrawables[i2][0], getColor(key_chat_outLoaderPhoto), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i2][0], getColor(key_chat_outLoaderPhotoIcon), true);
                setCombinedDrawableColor(chat_photoStatesDrawables[i2][1], getColor(key_chat_outLoaderPhotoSelected), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i2][1], getColor(key_chat_outLoaderPhotoIconSelected), true);
                i2 = 10 + i;
                setCombinedDrawableColor(chat_photoStatesDrawables[i2][0], getColor(key_chat_inLoaderPhoto), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i2][0], getColor(key_chat_inLoaderPhotoIcon), true);
                setCombinedDrawableColor(chat_photoStatesDrawables[i2][1], getColor(key_chat_inLoaderPhotoSelected), false);
                setCombinedDrawableColor(chat_photoStatesDrawables[i2][1], getColor(key_chat_inLoaderPhotoIconSelected), true);
            }
            setDrawableColorByKey(chat_photoStatesDrawables[9][0], key_chat_outFileIcon);
            setDrawableColorByKey(chat_photoStatesDrawables[9][1], key_chat_outFileSelectedIcon);
            setDrawableColorByKey(chat_photoStatesDrawables[12][0], key_chat_inFileIcon);
            setDrawableColorByKey(chat_photoStatesDrawables[12][1], key_chat_inFileSelectedIcon);
            setCombinedDrawableColor(chat_contactDrawable[0], getColor(key_chat_inContactBackground), false);
            setCombinedDrawableColor(chat_contactDrawable[0], getColor(key_chat_inContactIcon), true);
            setCombinedDrawableColor(chat_contactDrawable[1], getColor(key_chat_outContactBackground), false);
            setCombinedDrawableColor(chat_contactDrawable[1], getColor(key_chat_outContactIcon), true);
            setCombinedDrawableColor(chat_locationDrawable[0], getColor(key_chat_inLocationBackground), false);
            setCombinedDrawableColor(chat_locationDrawable[0], getColor(key_chat_inLocationIcon), true);
            setCombinedDrawableColor(chat_locationDrawable[1], getColor(key_chat_outLocationBackground), false);
            setCombinedDrawableColor(chat_locationDrawable[1], getColor(key_chat_outLocationIcon), true);
            setDrawableColorByKey(chat_composeShadowDrawable, key_chat_messagePanelShadow);
            applyChatServiceMessageColor();
        }
    }

    public static void applyChatServiceMessageColor() {
        if (chat_actionBackgroundPaint != null) {
            Integer num = (Integer) currentColors.get(key_chat_serviceBackground);
            Integer num2 = (Integer) currentColors.get(key_chat_serviceBackgroundSelected);
            if (num == null) {
                num = Integer.valueOf(serviceMessageColor);
            }
            if (num2 == null) {
                num2 = Integer.valueOf(serviceSelectedMessageColor);
            }
            if (currentColor != num.intValue()) {
                chat_actionBackgroundPaint.setColor(num.intValue());
                colorFilter = new PorterDuffColorFilter(num.intValue(), Mode.MULTIPLY);
                currentColor = num.intValue();
                int i = 0;
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
            }
        }
    }

    public static void createProfileResources(Context context) {
        if (profile_verifiedDrawable == null) {
            profile_aboutTextPaint = new TextPaint(1);
            context = context.getResources();
            profile_verifiedDrawable = context.getDrawable(C0446R.drawable.verified_area).mutate();
            profile_verifiedCheckDrawable = context.getDrawable(C0446R.drawable.verified_check).mutate();
            applyProfileTheme();
        }
        profile_aboutTextPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
    }

    public static void applyProfileTheme() {
        if (profile_verifiedDrawable != null) {
            profile_aboutTextPaint.setColor(getColor(key_windowBackgroundWhiteBlackText));
            profile_aboutTextPaint.linkColor = getColor(key_windowBackgroundWhiteLinkText);
            setDrawableColorByKey(profile_verifiedDrawable, key_profile_verifiedBackground);
            setDrawableColorByKey(profile_verifiedCheckDrawable, key_profile_verifiedCheck);
        }
    }

    public static Drawable getThemedDrawable(Context context, int i, String str) {
        context = context.getResources().getDrawable(i).mutate();
        context.setColorFilter(new PorterDuffColorFilter(getColor(str), Mode.MULTIPLY));
        return context;
    }

    public static int getDefaultColor(String str) {
        Integer num = (Integer) defaultColors.get(str);
        if (num == null) {
            return str.equals(key_chats_menuTopShadow) != null ? null : -65536;
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

    public static int getColor(String str) {
        return getColor(str, null);
    }

    public static int getColor(String str, boolean[] zArr) {
        Integer num = (Integer) currentColors.get(str);
        if (num == null) {
            String str2 = (String) fallbackKeys.get(str);
            if (str2 != null) {
                num = (Integer) currentColors.get(str2);
            }
            if (num == null) {
                if (zArr != null) {
                    zArr[0] = true;
                }
                if (str.equals(key_chat_serviceBackground) != null) {
                    return serviceMessageColor;
                }
                if (str.equals(key_chat_serviceBackgroundSelected) != null) {
                    return serviceSelectedMessageColor;
                }
                return getDefaultColor(str);
            }
        }
        return num.intValue();
    }

    public static void setColor(String str, int i, boolean z) {
        if (str.equals(key_chat_wallpaper)) {
            i |= ACTION_BAR_VIDEO_EDIT_COLOR;
        }
        if (z) {
            currentColors.remove(str);
        } else {
            currentColors.put(str, Integer.valueOf(i));
        }
        if (str.equals(key_chat_serviceBackground) == 0) {
            if (str.equals(key_chat_serviceBackgroundSelected) == 0) {
                if (str.equals(key_chat_wallpaper) != null) {
                    reloadWallpaper();
                    return;
                }
                return;
            }
        }
        applyChatServiceMessageColor();
    }

    public static void setThemeWallpaper(String str, Bitmap bitmap, File file) {
        currentColors.remove(key_chat_wallpaper);
        MessagesController.getGlobalMainSettings().edit().remove("overrideThemeWallpaper").commit();
        if (bitmap != null) {
            themedWallpaper = new BitmapDrawable(bitmap);
            saveCurrentTheme(str, false);
            calcBackgroundColor(themedWallpaper, 0);
            applyChatServiceMessageColor();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewWallpapper, new Object[0]);
            return;
        }
        themedWallpaper = null;
        wallpaper = null;
        saveCurrentTheme(str, false);
        reloadWallpaper();
    }

    public static void setDrawableColor(Drawable drawable, int i) {
        if (drawable != null) {
            if (drawable instanceof ShapeDrawable) {
                ((ShapeDrawable) drawable).getPaint().setColor(i);
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

    public static void setEmojiDrawableColor(android.graphics.drawable.Drawable r1, int r2, boolean r3) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r0 = r1 instanceof android.graphics.drawable.StateListDrawable;
        if (r0 == 0) goto L_0x0041;
    L_0x0004:
        if (r3 == 0) goto L_0x0024;
    L_0x0006:
        r3 = 0;
        r1 = getStateDrawable(r1, r3);	 Catch:{ Throwable -> 0x0041 }
        r3 = r1 instanceof android.graphics.drawable.ShapeDrawable;	 Catch:{ Throwable -> 0x0041 }
        if (r3 == 0) goto L_0x0019;	 Catch:{ Throwable -> 0x0041 }
    L_0x000f:
        r1 = (android.graphics.drawable.ShapeDrawable) r1;	 Catch:{ Throwable -> 0x0041 }
        r1 = r1.getPaint();	 Catch:{ Throwable -> 0x0041 }
        r1.setColor(r2);	 Catch:{ Throwable -> 0x0041 }
        goto L_0x0041;	 Catch:{ Throwable -> 0x0041 }
    L_0x0019:
        r3 = new android.graphics.PorterDuffColorFilter;	 Catch:{ Throwable -> 0x0041 }
        r0 = android.graphics.PorterDuff.Mode.MULTIPLY;	 Catch:{ Throwable -> 0x0041 }
        r3.<init>(r2, r0);	 Catch:{ Throwable -> 0x0041 }
        r1.setColorFilter(r3);	 Catch:{ Throwable -> 0x0041 }
        goto L_0x0041;	 Catch:{ Throwable -> 0x0041 }
    L_0x0024:
        r3 = 1;	 Catch:{ Throwable -> 0x0041 }
        r1 = getStateDrawable(r1, r3);	 Catch:{ Throwable -> 0x0041 }
        r3 = r1 instanceof android.graphics.drawable.ShapeDrawable;	 Catch:{ Throwable -> 0x0041 }
        if (r3 == 0) goto L_0x0037;	 Catch:{ Throwable -> 0x0041 }
    L_0x002d:
        r1 = (android.graphics.drawable.ShapeDrawable) r1;	 Catch:{ Throwable -> 0x0041 }
        r1 = r1.getPaint();	 Catch:{ Throwable -> 0x0041 }
        r1.setColor(r2);	 Catch:{ Throwable -> 0x0041 }
        goto L_0x0041;	 Catch:{ Throwable -> 0x0041 }
    L_0x0037:
        r3 = new android.graphics.PorterDuffColorFilter;	 Catch:{ Throwable -> 0x0041 }
        r0 = android.graphics.PorterDuff.Mode.MULTIPLY;	 Catch:{ Throwable -> 0x0041 }
        r3.<init>(r2, r0);	 Catch:{ Throwable -> 0x0041 }
        r1.setColorFilter(r3);	 Catch:{ Throwable -> 0x0041 }
    L_0x0041:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.setEmojiDrawableColor(android.graphics.drawable.Drawable, int, boolean):void");
    }

    public static void setSelectorDrawableColor(android.graphics.drawable.Drawable r4, int r5, boolean r6) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r0 = r4 instanceof android.graphics.drawable.StateListDrawable;
        r1 = 1;
        r2 = 0;
        if (r0 == 0) goto L_0x005f;
    L_0x0006:
        if (r6 == 0) goto L_0x0041;
    L_0x0008:
        r6 = getStateDrawable(r4, r2);	 Catch:{ Throwable -> 0x00a2 }
        r0 = r6 instanceof android.graphics.drawable.ShapeDrawable;	 Catch:{ Throwable -> 0x00a2 }
        if (r0 == 0) goto L_0x001a;	 Catch:{ Throwable -> 0x00a2 }
    L_0x0010:
        r6 = (android.graphics.drawable.ShapeDrawable) r6;	 Catch:{ Throwable -> 0x00a2 }
        r6 = r6.getPaint();	 Catch:{ Throwable -> 0x00a2 }
        r6.setColor(r5);	 Catch:{ Throwable -> 0x00a2 }
        goto L_0x0024;	 Catch:{ Throwable -> 0x00a2 }
    L_0x001a:
        r0 = new android.graphics.PorterDuffColorFilter;	 Catch:{ Throwable -> 0x00a2 }
        r2 = android.graphics.PorterDuff.Mode.MULTIPLY;	 Catch:{ Throwable -> 0x00a2 }
        r0.<init>(r5, r2);	 Catch:{ Throwable -> 0x00a2 }
        r6.setColorFilter(r0);	 Catch:{ Throwable -> 0x00a2 }
    L_0x0024:
        r4 = getStateDrawable(r4, r1);	 Catch:{ Throwable -> 0x00a2 }
        r6 = r4 instanceof android.graphics.drawable.ShapeDrawable;	 Catch:{ Throwable -> 0x00a2 }
        if (r6 == 0) goto L_0x0036;	 Catch:{ Throwable -> 0x00a2 }
    L_0x002c:
        r4 = (android.graphics.drawable.ShapeDrawable) r4;	 Catch:{ Throwable -> 0x00a2 }
        r4 = r4.getPaint();	 Catch:{ Throwable -> 0x00a2 }
        r4.setColor(r5);	 Catch:{ Throwable -> 0x00a2 }
        goto L_0x00a2;	 Catch:{ Throwable -> 0x00a2 }
    L_0x0036:
        r6 = new android.graphics.PorterDuffColorFilter;	 Catch:{ Throwable -> 0x00a2 }
        r0 = android.graphics.PorterDuff.Mode.MULTIPLY;	 Catch:{ Throwable -> 0x00a2 }
        r6.<init>(r5, r0);	 Catch:{ Throwable -> 0x00a2 }
        r4.setColorFilter(r6);	 Catch:{ Throwable -> 0x00a2 }
        goto L_0x00a2;	 Catch:{ Throwable -> 0x00a2 }
    L_0x0041:
        r6 = 2;	 Catch:{ Throwable -> 0x00a2 }
        r4 = getStateDrawable(r4, r6);	 Catch:{ Throwable -> 0x00a2 }
        r6 = r4 instanceof android.graphics.drawable.ShapeDrawable;	 Catch:{ Throwable -> 0x00a2 }
        if (r6 == 0) goto L_0x0054;	 Catch:{ Throwable -> 0x00a2 }
    L_0x004a:
        r4 = (android.graphics.drawable.ShapeDrawable) r4;	 Catch:{ Throwable -> 0x00a2 }
        r4 = r4.getPaint();	 Catch:{ Throwable -> 0x00a2 }
        r4.setColor(r5);	 Catch:{ Throwable -> 0x00a2 }
        goto L_0x00a2;	 Catch:{ Throwable -> 0x00a2 }
    L_0x0054:
        r6 = new android.graphics.PorterDuffColorFilter;	 Catch:{ Throwable -> 0x00a2 }
        r0 = android.graphics.PorterDuff.Mode.MULTIPLY;	 Catch:{ Throwable -> 0x00a2 }
        r6.<init>(r5, r0);	 Catch:{ Throwable -> 0x00a2 }
        r4.setColorFilter(r6);	 Catch:{ Throwable -> 0x00a2 }
        goto L_0x00a2;
    L_0x005f:
        r0 = android.os.Build.VERSION.SDK_INT;
        r3 = 21;
        if (r0 < r3) goto L_0x00a2;
    L_0x0065:
        r0 = r4 instanceof android.graphics.drawable.RippleDrawable;
        if (r0 == 0) goto L_0x00a2;
    L_0x0069:
        r4 = (android.graphics.drawable.RippleDrawable) r4;
        if (r6 == 0) goto L_0x0080;
    L_0x006d:
        r6 = new android.content.res.ColorStateList;
        r0 = new int[r1][];
        r3 = android.util.StateSet.WILD_CARD;
        r0[r2] = r3;
        r1 = new int[r1];
        r1[r2] = r5;
        r6.<init>(r0, r1);
        r4.setColor(r6);
        goto L_0x00a2;
    L_0x0080:
        r6 = r4.getNumberOfLayers();
        if (r6 <= 0) goto L_0x00a2;
    L_0x0086:
        r4 = r4.getDrawable(r2);
        r6 = r4 instanceof android.graphics.drawable.ShapeDrawable;
        if (r6 == 0) goto L_0x0098;
    L_0x008e:
        r4 = (android.graphics.drawable.ShapeDrawable) r4;
        r4 = r4.getPaint();
        r4.setColor(r5);
        goto L_0x00a2;
    L_0x0098:
        r6 = new android.graphics.PorterDuffColorFilter;
        r0 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r6.<init>(r5, r0);
        r4.setColorFilter(r6);
    L_0x00a2:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.setSelectorDrawableColor(android.graphics.drawable.Drawable, int, boolean):void");
    }

    public static boolean hasWallpaperFromTheme() {
        if (!currentColors.containsKey(key_chat_wallpaper)) {
            if (themedWallpaperFileOffset <= 0) {
                return false;
            }
        }
        return true;
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
            drawable = AndroidUtilities.calcDrawableColor(drawable);
            serviceMessageColor = drawable[0];
            serviceSelectedMessageColor = drawable[1];
        }
    }

    public static int getServiceMessageColor() {
        Integer num = (Integer) currentColors.get(key_chat_serviceBackground);
        return num == null ? serviceMessageColor : num.intValue();
    }

    public static void loadWallpaper() {
        if (wallpaper == null) {
            Utilities.searchQueue.postRunnable(new Runnable() {

                /* renamed from: org.telegram.ui.ActionBar.Theme$11$1 */
                class C07581 implements Runnable {
                    C07581() {
                    }

                    public void run() {
                        Theme.applyChatServiceMessageColor();
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewWallpapper, new Object[0]);
                    }
                }

                public void run() {
                    /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
                    /*
                    r8 = this;
                    r0 = org.telegram.ui.ActionBar.Theme.wallpaperSync;
                    monitor-enter(r0);
                    r1 = org.telegram.messenger.MessagesController.getGlobalMainSettings();	 Catch:{ all -> 0x013e }
                    r2 = "overrideThemeWallpaper";	 Catch:{ all -> 0x013e }
                    r3 = 0;	 Catch:{ all -> 0x013e }
                    r1 = r1.getBoolean(r2, r3);	 Catch:{ all -> 0x013e }
                    r2 = 1;	 Catch:{ all -> 0x013e }
                    if (r1 != 0) goto L_0x00b7;	 Catch:{ all -> 0x013e }
                L_0x0013:
                    r1 = org.telegram.ui.ActionBar.Theme.currentColors;	 Catch:{ all -> 0x013e }
                    r4 = "chat_wallpaper";	 Catch:{ all -> 0x013e }
                    r1 = r1.get(r4);	 Catch:{ all -> 0x013e }
                    r1 = (java.lang.Integer) r1;	 Catch:{ all -> 0x013e }
                    if (r1 == 0) goto L_0x0032;	 Catch:{ all -> 0x013e }
                L_0x0021:
                    r4 = new android.graphics.drawable.ColorDrawable;	 Catch:{ all -> 0x013e }
                    r1 = r1.intValue();	 Catch:{ all -> 0x013e }
                    r4.<init>(r1);	 Catch:{ all -> 0x013e }
                    org.telegram.ui.ActionBar.Theme.wallpaper = r4;	 Catch:{ all -> 0x013e }
                    org.telegram.ui.ActionBar.Theme.isCustomTheme = r2;	 Catch:{ all -> 0x013e }
                    goto L_0x00b7;	 Catch:{ all -> 0x013e }
                L_0x0032:
                    r1 = org.telegram.ui.ActionBar.Theme.themedWallpaperFileOffset;	 Catch:{ all -> 0x013e }
                    if (r1 <= 0) goto L_0x00b7;	 Catch:{ all -> 0x013e }
                L_0x0038:
                    r1 = org.telegram.ui.ActionBar.Theme.currentTheme;	 Catch:{ all -> 0x013e }
                    r1 = r1.pathToFile;	 Catch:{ all -> 0x013e }
                    if (r1 != 0) goto L_0x0048;	 Catch:{ all -> 0x013e }
                L_0x0040:
                    r1 = org.telegram.ui.ActionBar.Theme.currentTheme;	 Catch:{ all -> 0x013e }
                    r1 = r1.assetName;	 Catch:{ all -> 0x013e }
                    if (r1 == 0) goto L_0x00b7;
                L_0x0048:
                    r1 = 0;
                    r4 = org.telegram.ui.ActionBar.Theme.currentTheme;	 Catch:{ Throwable -> 0x00a0 }
                    r4 = r4.assetName;	 Catch:{ Throwable -> 0x00a0 }
                    if (r4 == 0) goto L_0x005c;	 Catch:{ Throwable -> 0x00a0 }
                L_0x0051:
                    r4 = org.telegram.ui.ActionBar.Theme.currentTheme;	 Catch:{ Throwable -> 0x00a0 }
                    r4 = r4.assetName;	 Catch:{ Throwable -> 0x00a0 }
                    r4 = org.telegram.ui.ActionBar.Theme.getAssetFile(r4);	 Catch:{ Throwable -> 0x00a0 }
                    goto L_0x0067;	 Catch:{ Throwable -> 0x00a0 }
                L_0x005c:
                    r4 = new java.io.File;	 Catch:{ Throwable -> 0x00a0 }
                    r5 = org.telegram.ui.ActionBar.Theme.currentTheme;	 Catch:{ Throwable -> 0x00a0 }
                    r5 = r5.pathToFile;	 Catch:{ Throwable -> 0x00a0 }
                    r4.<init>(r5);	 Catch:{ Throwable -> 0x00a0 }
                L_0x0067:
                    r5 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x00a0 }
                    r5.<init>(r4);	 Catch:{ Throwable -> 0x00a0 }
                    r1 = r5.getChannel();	 Catch:{ Throwable -> 0x009b, all -> 0x0098 }
                    r4 = org.telegram.ui.ActionBar.Theme.themedWallpaperFileOffset;	 Catch:{ Throwable -> 0x009b, all -> 0x0098 }
                    r6 = (long) r4;	 Catch:{ Throwable -> 0x009b, all -> 0x0098 }
                    r1.position(r6);	 Catch:{ Throwable -> 0x009b, all -> 0x0098 }
                    r1 = android.graphics.BitmapFactory.decodeStream(r5);	 Catch:{ Throwable -> 0x009b, all -> 0x0098 }
                    if (r1 == 0) goto L_0x008d;	 Catch:{ Throwable -> 0x009b, all -> 0x0098 }
                L_0x007e:
                    r4 = new android.graphics.drawable.BitmapDrawable;	 Catch:{ Throwable -> 0x009b, all -> 0x0098 }
                    r4.<init>(r1);	 Catch:{ Throwable -> 0x009b, all -> 0x0098 }
                    r1 = org.telegram.ui.ActionBar.Theme.wallpaper = r4;	 Catch:{ Throwable -> 0x009b, all -> 0x0098 }
                    org.telegram.ui.ActionBar.Theme.themedWallpaper = r1;	 Catch:{ Throwable -> 0x009b, all -> 0x0098 }
                    org.telegram.ui.ActionBar.Theme.isCustomTheme = r2;	 Catch:{ Throwable -> 0x009b, all -> 0x0098 }
                L_0x008d:
                    if (r5 == 0) goto L_0x00b7;
                L_0x008f:
                    r5.close();	 Catch:{ Exception -> 0x0093 }
                    goto L_0x00b7;
                L_0x0093:
                    r1 = move-exception;
                L_0x0094:
                    org.telegram.messenger.FileLog.m3e(r1);	 Catch:{ all -> 0x013e }
                    goto L_0x00b7;
                L_0x0098:
                    r2 = move-exception;
                    r1 = r5;
                    goto L_0x00ac;
                L_0x009b:
                    r4 = move-exception;
                    r1 = r5;
                    goto L_0x00a1;
                L_0x009e:
                    r2 = move-exception;
                    goto L_0x00ac;
                L_0x00a0:
                    r4 = move-exception;
                L_0x00a1:
                    org.telegram.messenger.FileLog.m3e(r4);	 Catch:{ all -> 0x009e }
                    if (r1 == 0) goto L_0x00b7;
                L_0x00a6:
                    r1.close();	 Catch:{ Exception -> 0x00aa }
                    goto L_0x00b7;
                L_0x00aa:
                    r1 = move-exception;
                    goto L_0x0094;
                L_0x00ac:
                    if (r1 == 0) goto L_0x00b6;
                L_0x00ae:
                    r1.close();	 Catch:{ Exception -> 0x00b2 }
                    goto L_0x00b6;
                L_0x00b2:
                    r1 = move-exception;
                    org.telegram.messenger.FileLog.m3e(r1);	 Catch:{ all -> 0x013e }
                L_0x00b6:
                    throw r2;	 Catch:{ all -> 0x013e }
                L_0x00b7:
                    r1 = org.telegram.ui.ActionBar.Theme.wallpaper;	 Catch:{ all -> 0x013e }
                    if (r1 != 0) goto L_0x012d;
                L_0x00bd:
                    r1 = org.telegram.messenger.MessagesController.getGlobalMainSettings();	 Catch:{ Throwable -> 0x0119 }
                    r4 = "selectedBackground";	 Catch:{ Throwable -> 0x0119 }
                    r5 = 1000001; // 0xf4241 float:1.4013E-39 double:4.94066E-318;	 Catch:{ Throwable -> 0x0119 }
                    r4 = r1.getInt(r4, r5);	 Catch:{ Throwable -> 0x0119 }
                    r6 = "selectedColor";	 Catch:{ Throwable -> 0x0119 }
                    r1 = r1.getInt(r6, r3);	 Catch:{ Throwable -> 0x0119 }
                    if (r1 != 0) goto L_0x011a;
                L_0x00d2:
                    r6 = NUM; // 0x7f07002a float:1.7944663E38 double:1.052935524E-314;
                    if (r4 != r5) goto L_0x00e8;
                L_0x00d7:
                    r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x011a }
                    r4 = r4.getResources();	 Catch:{ Throwable -> 0x011a }
                    r4 = r4.getDrawable(r6);	 Catch:{ Throwable -> 0x011a }
                    org.telegram.ui.ActionBar.Theme.wallpaper = r4;	 Catch:{ Throwable -> 0x011a }
                    org.telegram.ui.ActionBar.Theme.isCustomTheme = r3;	 Catch:{ Throwable -> 0x011a }
                    goto L_0x011a;	 Catch:{ Throwable -> 0x011a }
                L_0x00e8:
                    r4 = new java.io.File;	 Catch:{ Throwable -> 0x011a }
                    r5 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();	 Catch:{ Throwable -> 0x011a }
                    r7 = "wallpaper.jpg";	 Catch:{ Throwable -> 0x011a }
                    r4.<init>(r5, r7);	 Catch:{ Throwable -> 0x011a }
                    r5 = r4.exists();	 Catch:{ Throwable -> 0x011a }
                    if (r5 == 0) goto L_0x0108;	 Catch:{ Throwable -> 0x011a }
                L_0x00f9:
                    r3 = r4.getAbsolutePath();	 Catch:{ Throwable -> 0x011a }
                    r3 = android.graphics.drawable.Drawable.createFromPath(r3);	 Catch:{ Throwable -> 0x011a }
                    org.telegram.ui.ActionBar.Theme.wallpaper = r3;	 Catch:{ Throwable -> 0x011a }
                    org.telegram.ui.ActionBar.Theme.isCustomTheme = r2;	 Catch:{ Throwable -> 0x011a }
                    goto L_0x011a;	 Catch:{ Throwable -> 0x011a }
                L_0x0108:
                    r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x011a }
                    r4 = r4.getResources();	 Catch:{ Throwable -> 0x011a }
                    r4 = r4.getDrawable(r6);	 Catch:{ Throwable -> 0x011a }
                    org.telegram.ui.ActionBar.Theme.wallpaper = r4;	 Catch:{ Throwable -> 0x011a }
                    org.telegram.ui.ActionBar.Theme.isCustomTheme = r3;	 Catch:{ Throwable -> 0x011a }
                    goto L_0x011a;
                L_0x0119:
                    r1 = r3;
                L_0x011a:
                    r3 = org.telegram.ui.ActionBar.Theme.wallpaper;	 Catch:{ all -> 0x013e }
                    if (r3 != 0) goto L_0x012d;	 Catch:{ all -> 0x013e }
                L_0x0120:
                    if (r1 != 0) goto L_0x0125;	 Catch:{ all -> 0x013e }
                L_0x0122:
                    r1 = -2693905; // 0xffffffffffd6e4ef float:NaN double:NaN;	 Catch:{ all -> 0x013e }
                L_0x0125:
                    r3 = new android.graphics.drawable.ColorDrawable;	 Catch:{ all -> 0x013e }
                    r3.<init>(r1);	 Catch:{ all -> 0x013e }
                    org.telegram.ui.ActionBar.Theme.wallpaper = r3;	 Catch:{ all -> 0x013e }
                L_0x012d:
                    r1 = org.telegram.ui.ActionBar.Theme.wallpaper;	 Catch:{ all -> 0x013e }
                    org.telegram.ui.ActionBar.Theme.calcBackgroundColor(r1, r2);	 Catch:{ all -> 0x013e }
                    r1 = new org.telegram.ui.ActionBar.Theme$11$1;	 Catch:{ all -> 0x013e }
                    r1.<init>();	 Catch:{ all -> 0x013e }
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);	 Catch:{ all -> 0x013e }
                    monitor-exit(r0);	 Catch:{ all -> 0x013e }
                    return;	 Catch:{ all -> 0x013e }
                L_0x013e:
                    r1 = move-exception;	 Catch:{ all -> 0x013e }
                    monitor-exit(r0);	 Catch:{ all -> 0x013e }
                    throw r1;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.11.run():void");
                }
            });
        }
    }

    public static Drawable getThemedWallpaper(boolean z) {
        Throwable e;
        Integer num = (Integer) currentColors.get(key_chat_wallpaper);
        if (num != null) {
            return new ColorDrawable(num.intValue());
        }
        if (themedWallpaperFileOffset > 0 && !(currentTheme.pathToFile == null && currentTheme.assetName == null)) {
            FileInputStream fileInputStream;
            try {
                File assetFile;
                if (currentTheme.assetName != null) {
                    assetFile = getAssetFile(currentTheme.assetName);
                } else {
                    assetFile = new File(currentTheme.pathToFile);
                }
                fileInputStream = new FileInputStream(assetFile);
                try {
                    fileInputStream.getChannel().position((long) themedWallpaperFileOffset);
                    Options options = new Options();
                    int i = 1;
                    if (z) {
                        options.inJustDecodeBounds = true;
                        z = (float) options.outWidth;
                        float f = (float) options.outHeight;
                        int dp = AndroidUtilities.dp(100.0f);
                        while (true) {
                            float f2 = (float) dp;
                            if (z <= f2 && f <= f2) {
                                break;
                            }
                            i *= 2;
                            z /= true;
                            f /= 2.0f;
                        }
                    }
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = i;
                    z = BitmapFactory.decodeStream(fileInputStream, null, options);
                    if (z) {
                        Drawable bitmapDrawable = new BitmapDrawable(z);
                        if (fileInputStream != null) {
                            try {
                                fileInputStream.close();
                            } catch (Throwable e2) {
                                FileLog.m3e(e2);
                            }
                        }
                        return bitmapDrawable;
                    } else if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable e22) {
                            FileLog.m3e(e22);
                        }
                    }
                } catch (Throwable th) {
                    e22 = th;
                    try {
                        FileLog.m3e(e22);
                        if (fileInputStream != null) {
                            fileInputStream.close();
                        }
                        return null;
                    } catch (Throwable th2) {
                        z = th2;
                        if (fileInputStream != null) {
                            try {
                                fileInputStream.close();
                            } catch (Throwable e3) {
                                FileLog.m3e(e3);
                            }
                        }
                        throw z;
                    }
                }
            } catch (Throwable th3) {
                z = th3;
                fileInputStream = null;
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                throw z;
            }
        }
        return null;
    }

    public static Drawable getCachedWallpaper() {
        synchronized (wallpaperSync) {
            if (themedWallpaper != null) {
                Drawable drawable = themedWallpaper;
                return drawable;
            }
            drawable = wallpaper;
            return drawable;
        }
    }
}
