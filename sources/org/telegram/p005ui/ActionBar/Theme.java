package org.telegram.p005ui.ActionBar;

import android.annotation.SuppressLint;
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
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
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
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.StateSet;
import com.google.android.exoplayer2.CLASSNAMEC;
import com.google.android.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.CLASSNAMER;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.messenger.time.SunDate;
import org.telegram.p005ui.Components.CombinedDrawable;
import org.telegram.p005ui.Components.ThemeEditorView;

/* renamed from: org.telegram.ui.ActionBar.Theme */
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
    private static final int LIGHT_SENSOR_THEME_SWITCH_DELAY = 1800;
    private static final int LIGHT_SENSOR_THEME_SWITCH_NEAR_DELAY = 12000;
    private static final int LIGHT_SENSOR_THEME_SWITCH_NEAR_THRESHOLD = 12000;
    private static final float MAXIMUM_LUX_BREAKPOINT = 500.0f;
    private static Method StateListDrawable_getStateDrawableMethod = null;
    private static SensorEventListener ambientSensorListener = new CLASSNAME();
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
    public static Drawable avatar_broadcastDrawable = null;
    public static Drawable avatar_savedDrawable = null;
    private static boolean canStartHolidayAnimation = false;
    public static Paint chat_actionBackgroundPaint = null;
    public static TextPaint chat_actionTextPaint = null;
    public static TextPaint chat_adminPaint = null;
    public static Drawable[] chat_attachButtonDrawables = new Drawable[10];
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
    public static Paint dialogs_onlineCirclePaint = null;
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
    public static final String key_avatar_actionBarSelectorBlue = "avatar_actionBarSelectorBlue";
    public static final String key_avatar_backgroundActionBarBlue = "avatar_backgroundActionBarBlue";
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
    public static final String key_calls_ratingStar = "calls_ratingStar";
    public static final String key_calls_ratingStarSelected = "calls_ratingStarSelected";
    public static final String key_changephoneinfo_changeText = "key_changephoneinfo_changeText";
    public static final String key_changephoneinfo_image = "changephoneinfo_image";
    public static final String key_chat_addContact = "chat_addContact";
    public static final String key_chat_adminSelectedText = "chat_adminSelectedText";
    public static final String key_chat_adminText = "chat_adminText";
    public static final String key_chat_attachAudioBackground = "chat_attachAudioBackground";
    public static final String key_chat_attachAudioIcon = "chat_attachAudioIcon";
    public static final String key_chat_attachCameraIcon1 = "chat_attachCameraIcon1";
    public static final String key_chat_attachCameraIcon2 = "chat_attachCameraIcon2";
    public static final String key_chat_attachCameraIcon3 = "chat_attachCameraIcon3";
    public static final String key_chat_attachCameraIcon4 = "chat_attachCameraIcon4";
    public static final String key_chat_attachCameraIcon5 = "chat_attachCameraIcon5";
    public static final String key_chat_attachCameraIcon6 = "chat_attachCameraIcon6";
    public static final String key_chat_attachContactBackground = "chat_attachContactBackground";
    public static final String key_chat_attachContactIcon = "chat_attachContactIcon";
    public static final String key_chat_attachFileBackground = "chat_attachFileBackground";
    public static final String key_chat_attachFileIcon = "chat_attachFileIcon";
    public static final String key_chat_attachGalleryBackground = "chat_attachGalleryBackground";
    public static final String key_chat_attachGalleryIcon = "chat_attachGalleryIcon";
    public static final String key_chat_attachHideBackground = "chat_attachHideBackground";
    public static final String key_chat_attachHideIcon = "chat_attachHideIcon";
    public static final String key_chat_attachLocationBackground = "chat_attachLocationBackground";
    public static final String key_chat_attachLocationIcon = "chat_attachLocationIcon";
    public static final String key_chat_attachPollBackground = "chat_attachPollBackground";
    public static final String key_chat_attachPollIcon = "chat_attachPollIcon";
    public static final String key_chat_attachSendBackground = "chat_attachSendBackground";
    public static final String key_chat_attachSendIcon = "chat_attachSendIcon";
    public static final String key_chat_attachVideoBackground = "chat_attachVideoBackground";
    public static final String key_chat_attachVideoIcon = "chat_attachVideoIcon";
    public static final String key_chat_botButtonText = "chat_botButtonText";
    public static final String key_chat_botKeyboardButtonBackground = "chat_botKeyboardButtonBackground";
    public static final String key_chat_botKeyboardButtonBackgroundPressed = "chat_botKeyboardButtonBackgroundPressed";
    public static final String key_chat_botKeyboardButtonText = "chat_botKeyboardButtonText";
    public static final String key_chat_botProgress = "chat_botProgress";
    public static final String key_chat_botSwitchToInlineText = "chat_botSwitchToInlineText";
    public static final String key_chat_editDoneIcon = "chat_editDoneIcon";
    public static final String key_chat_emojiPanelBackground = "chat_emojiPanelBackground";
    public static final String key_chat_emojiPanelBackspace = "chat_emojiPanelBackspace";
    public static final String key_chat_emojiPanelBadgeBackground = "chat_emojiPanelBadgeBackground";
    public static final String key_chat_emojiPanelBadgeText = "chat_emojiPanelBadgeText";
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
    public static final String key_chat_inAudioPerfomerSelectedText = "chat_inAudioPerfomerSelectedText";
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
    public static final String key_chat_outAudioPerfomerSelectedText = "chat_outAudioPerfomerSelectedText";
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
    public static final String key_chats_actionUnreadBackground = "chats_actionUnreadBackground";
    public static final String key_chats_actionUnreadIcon = "chats_actionUnreadIcon";
    public static final String key_chats_actionUnreadPressedBackground = "chats_actionUnreadPressedBackground";
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
    public static final String key_chats_menuTopShadow = "chats_menuTopShadow";
    public static final String key_chats_message = "chats_message";
    public static final String key_chats_muteIcon = "chats_muteIcon";
    public static final String key_chats_name = "chats_name";
    public static final String key_chats_nameIcon = "chats_nameIcon";
    public static final String key_chats_nameMessage = "chats_nameMessage";
    public static final String key_chats_onlineCircle = "chats_onlineCircle";
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
    public static final String key_dialogCameraIcon = "dialogCameraIcon";
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
    public static final String key_dialogRedIcon = "dialogRedIcon";
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
    public static final String key_groupcreate_checkbox = "groupcreate_checkbox";
    public static final String key_groupcreate_checkboxCheck = "groupcreate_checkboxCheck";
    public static final String key_groupcreate_cursor = "groupcreate_cursor";
    public static final String key_groupcreate_hintText = "groupcreate_hintText";
    public static final String key_groupcreate_offlineText = "groupcreate_offlineText";
    public static final String key_groupcreate_onlineText = "groupcreate_onlineText";
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
    public static final String key_location_markerX = "location_markerX";
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
    public static final String key_sharedMedia_photoPlaceholder = "sharedMedia_photoPlaceholder";
    public static final String key_sharedMedia_startStopLoadIcon = "sharedMedia_startStopLoadIcon";
    public static final String key_stickers_menu = "stickers_menu";
    public static final String key_stickers_menuSelector = "stickers_menuSelector";
    public static final String key_switch2Track = "switch2Track";
    public static final String key_switch2TrackChecked = "switch2TrackChecked";
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
    public static String[] keys_avatar_background = new String[]{key_avatar_backgroundRed, key_avatar_backgroundOrange, key_avatar_backgroundViolet, key_avatar_backgroundGreen, key_avatar_backgroundCyan, key_avatar_backgroundBlue, key_avatar_backgroundPink};
    public static String[] keys_avatar_nameInMessage = new String[]{key_avatar_nameInMessageRed, key_avatar_nameInMessageOrange, key_avatar_nameInMessageViolet, key_avatar_nameInMessageGreen, key_avatar_nameInMessageCyan, key_avatar_nameInMessageBlue, key_avatar_nameInMessagePink};
    private static float lastBrightnessValue = 1.0f;
    private static long lastHolidayCheckTime;
    private static long lastThemeSwitchTime;
    private static Sensor lightSensor;
    private static boolean lightSensorRegistered;
    public static Paint linkSelectionPaint;
    public static Drawable listSelector;
    private static Paint maskPaint = new Paint(1);
    public static Drawable moveUpDrawable;
    private static ArrayList<ThemeInfo> otherThemes = new ArrayList();
    private static ThemeInfo previousTheme;
    public static TextPaint profile_aboutTextPaint;
    public static Drawable profile_verifiedCheckDrawable;
    public static Drawable profile_verifiedDrawable;
    public static int selectedAutoNightType;
    private static int selectedColor;
    private static SensorManager sensorManager;
    private static int serviceMessage2Color;
    private static int serviceMessageColor;
    private static int serviceSelectedMessage2Color;
    private static int serviceSelectedMessageColor;
    private static Runnable switchDayBrightnessRunnable = new CLASSNAME();
    private static boolean switchDayRunnableScheduled;
    private static Runnable switchNightBrightnessRunnable = new CLASSNAME();
    private static boolean switchNightRunnableScheduled;
    private static final Object sync = new Object();
    private static Drawable themedWallpaper;
    private static int themedWallpaperFileOffset;
    public static ArrayList<ThemeInfo> themes = new ArrayList();
    private static HashMap<String, ThemeInfo> themesDict = new HashMap();
    private static Drawable wallpaper;
    private static final Object wallpaperSync = new Object();

    /* renamed from: org.telegram.ui.ActionBar.Theme$1 */
    static class CLASSNAME implements Runnable {
        CLASSNAME() {
        }

        public void run() {
            Theme.switchDayRunnableScheduled = false;
            Theme.applyDayNightThemeMaybe(false);
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.Theme$2 */
    static class CLASSNAME implements Runnable {
        CLASSNAME() {
        }

        public void run() {
            Theme.switchNightRunnableScheduled = false;
            Theme.applyDayNightThemeMaybe(true);
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.Theme$3 */
    static class CLASSNAME extends StateListDrawable {
        CLASSNAME() {
        }

        public boolean selectDrawable(int index) {
            if (VERSION.SDK_INT >= 21) {
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
            if (colorFilter == null) {
                return result;
            }
            drawable.setColorFilter(colorFilter);
            return result;
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.Theme$4 */
    static class CLASSNAME extends StateListDrawable {
        CLASSNAME() {
        }

        public boolean selectDrawable(int index) {
            if (VERSION.SDK_INT >= 21) {
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
            if (colorFilter == null) {
                return result;
            }
            drawable.setColorFilter(colorFilter);
            return result;
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.Theme$5 */
    static class CLASSNAME extends StateListDrawable {
        CLASSNAME() {
        }

        public boolean selectDrawable(int index) {
            if (VERSION.SDK_INT >= 21) {
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
            if (colorFilter == null) {
                return result;
            }
            drawable.setColorFilter(colorFilter);
            return result;
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.Theme$6 */
    static class CLASSNAME extends Drawable {
        CLASSNAME() {
        }

        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            canvas.drawCircle((float) bounds.centerX(), (float) bounds.centerY(), (float) AndroidUtilities.m9dp(18.0f), Theme.maskPaint);
        }

        public void setAlpha(int alpha) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public int getOpacity() {
            return 0;
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.Theme$7 */
    static class CLASSNAME implements SensorEventListener {
        CLASSNAME() {
        }

        public void onSensorChanged(SensorEvent event) {
            float lux = event.values[0];
            if (lux <= 0.0f) {
                lux = 0.1f;
            }
            if (!ApplicationLoader.mainInterfacePaused && ApplicationLoader.isScreenOn) {
                if (lux > Theme.MAXIMUM_LUX_BREAKPOINT) {
                    Theme.lastBrightnessValue = 1.0f;
                } else {
                    Theme.lastBrightnessValue = ((float) Math.ceil((9.932299613952637d * Math.log((double) lux)) + 27.05900001525879d)) / 100.0f;
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

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.Theme$AttachCameraDrawable */
    private static class AttachCameraDrawable extends Drawable {
        private Paint paint = new Paint(1);
        private Path segment;

        public AttachCameraDrawable() {
            int size = AndroidUtilities.m9dp(54.0f);
            RectF rect = new RectF(0.0f, 0.0f, (float) size, (float) size);
            this.segment = new Path();
            this.segment.moveTo((float) AndroidUtilities.m9dp(23.0f), (float) AndroidUtilities.m9dp(20.0f));
            this.segment.lineTo((float) AndroidUtilities.m9dp(23.0f), 0.0f);
            this.segment.arcTo(rect, -98.0f, 50.0f, false);
            this.segment.close();
        }

        public void draw(Canvas canvas) {
            canvas.save();
            int cx = AndroidUtilities.m9dp(27.0f);
            canvas.rotate(-90.0f, (float) cx, (float) cx);
            for (int a = 0; a < 6; a++) {
                switch (a) {
                    case 0:
                        this.paint.setColor(Theme.getColor(Theme.key_chat_attachCameraIcon1));
                        break;
                    case 1:
                        this.paint.setColor(Theme.getColor(Theme.key_chat_attachCameraIcon2));
                        break;
                    case 2:
                        this.paint.setColor(Theme.getColor(Theme.key_chat_attachCameraIcon3));
                        break;
                    case 3:
                        this.paint.setColor(Theme.getColor(Theme.key_chat_attachCameraIcon4));
                        break;
                    case 4:
                        this.paint.setColor(Theme.getColor(Theme.key_chat_attachCameraIcon5));
                        break;
                    case 5:
                        this.paint.setColor(Theme.getColor(Theme.key_chat_attachCameraIcon6));
                        break;
                    default:
                        break;
                }
                canvas.rotate(60.0f, (float) cx, (float) cx);
                canvas.drawPath(this.segment, this.paint);
            }
            canvas.restore();
        }

        public void setAlpha(int alpha) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
            invalidateSelf();
        }

        public int getOpacity() {
            return -2;
        }

        public int getIntrinsicWidth() {
            return AndroidUtilities.m9dp(54.0f);
        }

        public int getIntrinsicHeight() {
            return AndroidUtilities.m9dp(54.0f);
        }

        public int getMinimumWidth() {
            return AndroidUtilities.m9dp(54.0f);
        }

        public int getMinimumHeight() {
            return AndroidUtilities.m9dp(54.0f);
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.Theme$ThemeInfo */
    public static class ThemeInfo {
        public String assetName;
        public String name;
        public String pathToFile;

        public JSONObject getSaveJson() {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", this.name);
                jsonObject.put("path", this.pathToFile);
                return jsonObject;
            } catch (Throwable e) {
                FileLog.m13e(e);
                return null;
            }
        }

        public String getName() {
            if ("Default".equals(this.name)) {
                return LocaleController.getString("Default", CLASSNAMER.string.Default);
            }
            if ("Blue".equals(this.name)) {
                return LocaleController.getString("ThemeBlue", CLASSNAMER.string.ThemeBlue);
            }
            if ("Dark".equals(this.name)) {
                return LocaleController.getString("ThemeDark", CLASSNAMER.string.ThemeDark);
            }
            if ("Dark Blue".equals(this.name)) {
                return LocaleController.getString("ThemeDarkBlue", CLASSNAMER.string.ThemeDarkBlue);
            }
            return this.name;
        }

        public static ThemeInfo createWithJson(JSONObject object) {
            if (object == null) {
                return null;
            }
            try {
                ThemeInfo themeInfo = new ThemeInfo();
                themeInfo.name = object.getString("name");
                themeInfo.pathToFile = object.getString("path");
                return themeInfo;
            } catch (Throwable e) {
                FileLog.m13e(e);
                return null;
            }
        }

        public static ThemeInfo createWithString(String string) {
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
    }

    static {
        selectedAutoNightType = 0;
        autoNightBrighnessThreshold = 0.25f;
        autoNightDayStartTime = 1320;
        autoNightDayEndTime = 480;
        autoNightSunsetTime = 1320;
        autoNightLastSunCheckDay = -1;
        autoNightSunriseTime = 480;
        autoNightCityName = TtmlNode.ANONYMOUS_REGION_ID;
        autoNightLocationLatitude = 10000.0d;
        autoNightLocationLongitude = 10000.0d;
        defaultColors.put(key_dialogBackground, Integer.valueOf(-1));
        defaultColors.put(key_dialogBackgroundGray, Integer.valueOf(-986896));
        defaultColors.put(key_dialogTextBlack, Integer.valueOf(-14540254));
        defaultColors.put(key_dialogTextLink, Integer.valueOf(-14255946));
        defaultColors.put(key_dialogLinkSelection, Integer.valueOf(NUM));
        defaultColors.put(key_dialogTextRed, Integer.valueOf(-3319206));
        defaultColors.put(key_dialogTextRed2, Integer.valueOf(-1434339));
        defaultColors.put(key_dialogTextBlue, Integer.valueOf(-13660983));
        defaultColors.put(key_dialogTextBlue2, Integer.valueOf(-12937771));
        defaultColors.put(key_dialogTextBlue3, Integer.valueOf(-12664327));
        defaultColors.put(key_dialogTextBlue4, Integer.valueOf(-15095832));
        defaultColors.put(key_dialogTextGray, Integer.valueOf(-13333567));
        defaultColors.put(key_dialogTextGray2, Integer.valueOf(-9079435));
        defaultColors.put(key_dialogTextGray3, Integer.valueOf(-6710887));
        defaultColors.put(key_dialogTextGray4, Integer.valueOf(-5000269));
        defaultColors.put(key_dialogTextHint, Integer.valueOf(-6842473));
        defaultColors.put(key_dialogIcon, Integer.valueOf(-7697782));
        defaultColors.put(key_dialogRedIcon, Integer.valueOf(-2539970));
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
        defaultColors.put(key_dialogButtonSelector, Integer.valueOf(NUM));
        defaultColors.put(key_dialogScrollGlow, Integer.valueOf(-657673));
        defaultColors.put(key_dialogRoundCheckBox, Integer.valueOf(-12664327));
        defaultColors.put(key_dialogRoundCheckBoxCheck, Integer.valueOf(-1));
        defaultColors.put(key_dialogBadgeBackground, Integer.valueOf(-12664327));
        defaultColors.put(key_dialogBadgeText, Integer.valueOf(-1));
        defaultColors.put(key_dialogCameraIcon, Integer.valueOf(-1));
        defaultColors.put(key_dialog_inlineProgressBackground, Integer.valueOf(-NUM));
        defaultColors.put(key_dialog_inlineProgress, Integer.valueOf(-9735304));
        defaultColors.put(key_windowBackgroundWhite, Integer.valueOf(-1));
        defaultColors.put(key_progressCircle, Integer.valueOf(-11371101));
        defaultColors.put(key_windowBackgroundWhiteGrayIcon, Integer.valueOf(-8288629));
        defaultColors.put(key_windowBackgroundWhiteBlueText, Integer.valueOf(-12876608));
        defaultColors.put(key_windowBackgroundWhiteBlueText2, Integer.valueOf(-12937771));
        defaultColors.put(key_windowBackgroundWhiteBlueText3, Integer.valueOf(-14255946));
        defaultColors.put(key_windowBackgroundWhiteBlueText4, Integer.valueOf(-11697229));
        defaultColors.put(key_windowBackgroundWhiteBlueText5, Integer.valueOf(-11759926));
        defaultColors.put(key_windowBackgroundWhiteBlueText6, Integer.valueOf(-12940081));
        defaultColors.put(key_windowBackgroundWhiteBlueText7, Integer.valueOf(-13141330));
        defaultColors.put(key_windowBackgroundWhiteGreenText, Integer.valueOf(-14248148));
        defaultColors.put(key_windowBackgroundWhiteGreenText2, Integer.valueOf(-13129704));
        defaultColors.put(key_windowBackgroundWhiteRedText, Integer.valueOf(-3319206));
        defaultColors.put(key_windowBackgroundWhiteRedText2, Integer.valueOf(-2404015));
        defaultColors.put(key_windowBackgroundWhiteRedText3, Integer.valueOf(-2995895));
        defaultColors.put(key_windowBackgroundWhiteRedText4, Integer.valueOf(-3198928));
        defaultColors.put(key_windowBackgroundWhiteRedText5, Integer.valueOf(-1230535));
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
        defaultColors.put(key_windowBackgroundWhiteBlackText, Integer.valueOf(-14540254));
        defaultColors.put(key_windowBackgroundWhiteHintText, Integer.valueOf(-5723992));
        defaultColors.put(key_windowBackgroundWhiteValueText, Integer.valueOf(-12937771));
        defaultColors.put(key_windowBackgroundWhiteLinkText, Integer.valueOf(-14255946));
        defaultColors.put(key_windowBackgroundWhiteLinkSelection, Integer.valueOf(NUM));
        defaultColors.put(key_windowBackgroundWhiteBlueHeader, Integer.valueOf(-12937771));
        defaultColors.put(key_windowBackgroundWhiteInputField, Integer.valueOf(-2368549));
        defaultColors.put(key_windowBackgroundWhiteInputFieldActivated, Integer.valueOf(-13129232));
        defaultColors.put(key_switchTrack, Integer.valueOf(-5196358));
        defaultColors.put(key_switchTrackChecked, Integer.valueOf(-11358743));
        defaultColors.put(key_switch2Track, Integer.valueOf(-688514));
        defaultColors.put(key_switch2TrackChecked, Integer.valueOf(-11358743));
        defaultColors.put(key_checkboxSquareBackground, Integer.valueOf(-12345121));
        defaultColors.put(key_checkboxSquareCheck, Integer.valueOf(-1));
        defaultColors.put(key_checkboxSquareUnchecked, Integer.valueOf(-9211021));
        defaultColors.put(key_checkboxSquareDisabled, Integer.valueOf(-5197648));
        defaultColors.put(key_listSelector, Integer.valueOf(NUM));
        defaultColors.put(key_radioBackground, Integer.valueOf(-5000269));
        defaultColors.put(key_radioBackgroundChecked, Integer.valueOf(-13129232));
        defaultColors.put(key_windowBackgroundGray, Integer.valueOf(-986896));
        defaultColors.put(key_windowBackgroundGrayShadow, Integer.valueOf(ACTION_BAR_VIDEO_EDIT_COLOR));
        defaultColors.put(key_emptyListPlaceholder, Integer.valueOf(-6974059));
        defaultColors.put(key_divider, Integer.valueOf(-2500135));
        defaultColors.put(key_graySection, Integer.valueOf(-1117195));
        defaultColors.put(key_graySectionText, Integer.valueOf(-8418927));
        defaultColors.put(key_contextProgressInner1, Integer.valueOf(-4202506));
        defaultColors.put(key_contextProgressOuter1, Integer.valueOf(-13920542));
        defaultColors.put(key_contextProgressInner2, Integer.valueOf(-4202506));
        defaultColors.put(key_contextProgressOuter2, Integer.valueOf(-1));
        defaultColors.put(key_contextProgressInner3, Integer.valueOf(-5000269));
        defaultColors.put(key_contextProgressOuter3, Integer.valueOf(-1));
        defaultColors.put(key_fastScrollActive, Integer.valueOf(-11361317));
        defaultColors.put(key_fastScrollInactive, Integer.valueOf(-3551791));
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
        defaultColors.put(key_avatar_backgroundGroupCreateSpanBlue, Integer.valueOf(-1642505));
        defaultColors.put(key_avatar_backgroundInProfileBlue, Integer.valueOf(-11500111));
        defaultColors.put(key_avatar_backgroundActionBarBlue, Integer.valueOf(-10907718));
        defaultColors.put(key_avatar_subtitleInProfileBlue, Integer.valueOf(-2626822));
        defaultColors.put(key_avatar_actionBarSelectorBlue, Integer.valueOf(-11959891));
        defaultColors.put(key_avatar_actionBarIconBlue, Integer.valueOf(-1));
        defaultColors.put(key_avatar_nameInMessageRed, Integer.valueOf(-3516848));
        defaultColors.put(key_avatar_nameInMessageOrange, Integer.valueOf(-2589911));
        defaultColors.put(key_avatar_nameInMessageViolet, Integer.valueOf(-11627828));
        defaultColors.put(key_avatar_nameInMessageGreen, Integer.valueOf(-11488718));
        defaultColors.put(key_avatar_nameInMessageCyan, Integer.valueOf(-12406360));
        defaultColors.put(key_avatar_nameInMessageBlue, Integer.valueOf(-11627828));
        defaultColors.put(key_avatar_nameInMessagePink, Integer.valueOf(-11627828));
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
        defaultColors.put(key_actionBarDefaultSubmenuItem, Integer.valueOf(-14540254));
        defaultColors.put(key_actionBarDefaultSubmenuBackground, Integer.valueOf(-1));
        defaultColors.put(key_actionBarActionModeDefaultSelector, Integer.valueOf(-986896));
        defaultColors.put(key_chats_onlineCircle, Integer.valueOf(-13456922));
        defaultColors.put(key_chats_unreadCounter, Integer.valueOf(-11613090));
        defaultColors.put(key_chats_unreadCounterMuted, Integer.valueOf(-3684409));
        defaultColors.put(key_chats_unreadCounterText, Integer.valueOf(-1));
        defaultColors.put(key_chats_name, Integer.valueOf(-14540254));
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
        defaultColors.put(key_chats_pinnedOverlay, Integer.valueOf(NUM));
        defaultColors.put(key_chats_tabletSelectedOverlay, Integer.valueOf(NUM));
        defaultColors.put(key_chats_sentCheck, Integer.valueOf(-12146122));
        defaultColors.put(key_chats_sentClock, Integer.valueOf(-9061026));
        defaultColors.put(key_chats_sentError, Integer.valueOf(-2796974));
        defaultColors.put(key_chats_sentErrorIcon, Integer.valueOf(-1));
        defaultColors.put(key_chats_verifiedBackground, Integer.valueOf(-13391642));
        defaultColors.put(key_chats_verifiedCheck, Integer.valueOf(-1));
        defaultColors.put(key_chats_muteIcon, Integer.valueOf(-5723992));
        defaultColors.put(key_chats_mentionIcon, Integer.valueOf(-1));
        defaultColors.put(key_chats_menuBackground, Integer.valueOf(-1));
        defaultColors.put(key_chats_menuItemText, Integer.valueOf(-12303292));
        defaultColors.put(key_chats_menuItemCheck, Integer.valueOf(-10907718));
        defaultColors.put(key_chats_menuItemIcon, Integer.valueOf(-7827048));
        defaultColors.put(key_chats_menuName, Integer.valueOf(-1));
        defaultColors.put(key_chats_menuPhone, Integer.valueOf(-1));
        defaultColors.put(key_chats_menuPhoneCats, Integer.valueOf(-4004353));
        defaultColors.put(key_chats_menuCloud, Integer.valueOf(-1));
        defaultColors.put(key_chats_menuCloudBackgroundCats, Integer.valueOf(-12420183));
        defaultColors.put(key_chats_actionIcon, Integer.valueOf(-1));
        defaultColors.put(key_chats_actionBackground, Integer.valueOf(-9788978));
        defaultColors.put(key_chats_actionPressedBackground, Integer.valueOf(-11038014));
        defaultColors.put(key_chats_actionUnreadIcon, Integer.valueOf(-9211021));
        defaultColors.put(key_chats_actionUnreadBackground, Integer.valueOf(-1));
        defaultColors.put(key_chats_actionUnreadPressedBackground, Integer.valueOf(-855310));
        defaultColors.put(key_chat_attachCameraIcon1, Integer.valueOf(-33488));
        defaultColors.put(key_chat_attachCameraIcon2, Integer.valueOf(-1353648));
        defaultColors.put(key_chat_attachCameraIcon3, Integer.valueOf(-12342798));
        defaultColors.put(key_chat_attachCameraIcon4, Integer.valueOf(-4958752));
        defaultColors.put(key_chat_attachCameraIcon5, Integer.valueOf(-10366879));
        defaultColors.put(key_chat_attachCameraIcon6, Integer.valueOf(-81627));
        defaultColors.put(key_chat_attachGalleryBackground, Integer.valueOf(-5997863));
        defaultColors.put(key_chat_attachGalleryIcon, Integer.valueOf(-1));
        defaultColors.put(key_chat_attachVideoBackground, Integer.valueOf(-1871495));
        defaultColors.put(key_chat_attachVideoIcon, Integer.valueOf(-1));
        defaultColors.put(key_chat_attachAudioBackground, Integer.valueOf(-620719));
        defaultColors.put(key_chat_attachAudioIcon, Integer.valueOf(-1));
        defaultColors.put(key_chat_attachFileBackground, Integer.valueOf(-13328140));
        defaultColors.put(key_chat_attachFileIcon, Integer.valueOf(-1));
        defaultColors.put(key_chat_attachContactBackground, Integer.valueOf(-12664838));
        defaultColors.put(key_chat_attachContactIcon, Integer.valueOf(-1));
        defaultColors.put(key_chat_attachLocationBackground, Integer.valueOf(-12597126));
        defaultColors.put(key_chat_attachLocationIcon, Integer.valueOf(-1));
        defaultColors.put(key_chat_attachHideBackground, Integer.valueOf(-5330248));
        defaultColors.put(key_chat_attachHideIcon, Integer.valueOf(-1));
        defaultColors.put(key_chat_attachSendBackground, Integer.valueOf(-12664838));
        defaultColors.put(key_chat_attachPollBackground, Integer.valueOf(-670899));
        defaultColors.put(key_chat_attachPollIcon, Integer.valueOf(-1));
        defaultColors.put(key_chat_attachSendIcon, Integer.valueOf(-1));
        defaultColors.put(key_chat_shareBackground, Integer.valueOf(NUM));
        defaultColors.put(key_chat_shareBackgroundSelected, Integer.valueOf(-NUM));
        defaultColors.put(key_chat_lockIcon, Integer.valueOf(-1));
        defaultColors.put(key_chat_muteIcon, Integer.valueOf(-5124893));
        defaultColors.put(key_chat_inBubble, Integer.valueOf(-1));
        defaultColors.put(key_chat_inBubbleSelected, Integer.valueOf(-1902337));
        defaultColors.put(key_chat_inBubbleShadow, Integer.valueOf(-14862509));
        defaultColors.put(key_chat_outBubble, Integer.valueOf(-1048610));
        defaultColors.put(key_chat_outBubbleSelected, Integer.valueOf(-2820676));
        defaultColors.put(key_chat_outBubbleShadow, Integer.valueOf(-14781172));
        defaultColors.put(key_chat_inMediaIcon, Integer.valueOf(-1));
        defaultColors.put(key_chat_inMediaIconSelected, Integer.valueOf(-1902337));
        defaultColors.put(key_chat_outMediaIcon, Integer.valueOf(-1048610));
        defaultColors.put(key_chat_outMediaIconSelected, Integer.valueOf(-2820676));
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
        defaultColors.put(key_chat_inContactPhoneSelectedText, Integer.valueOf(-13683656));
        defaultColors.put(key_chat_outContactPhoneText, Integer.valueOf(-13286860));
        defaultColors.put(key_chat_outContactPhoneSelectedText, Integer.valueOf(-13286860));
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
        defaultColors.put(key_chat_inAudioPerfomerSelectedText, Integer.valueOf(-13683656));
        defaultColors.put(key_chat_outAudioPerfomerText, Integer.valueOf(-13286860));
        defaultColors.put(key_chat_outAudioPerfomerSelectedText, Integer.valueOf(-13286860));
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
        defaultColors.put(key_chat_inVenueInfoText, Integer.valueOf(-6182221));
        defaultColors.put(key_chat_outVenueInfoText, Integer.valueOf(-10112933));
        defaultColors.put(key_chat_inVenueInfoSelectedText, Integer.valueOf(-7752511));
        defaultColors.put(key_chat_outVenueInfoSelectedText, Integer.valueOf(-10112933));
        defaultColors.put(key_chat_mediaInfoText, Integer.valueOf(-1));
        defaultColors.put(key_chat_linkSelectBackground, Integer.valueOf(NUM));
        defaultColors.put(key_chat_textSelectBackground, Integer.valueOf(NUM));
        defaultColors.put(key_chat_emojiPanelBackground, Integer.valueOf(-657673));
        defaultColors.put(key_chat_emojiPanelBadgeBackground, Integer.valueOf(-11688214));
        defaultColors.put(key_chat_emojiPanelBadgeText, Integer.valueOf(-1));
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
        defaultColors.put(key_chat_emojiPanelTrendingTitle, Integer.valueOf(-14540254));
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
        defaultColors.put(key_chat_gifSaveHintBackground, Integer.valueOf(-NUM));
        defaultColors.put(key_chat_goDownButton, Integer.valueOf(-1));
        defaultColors.put(key_chat_goDownButtonShadow, Integer.valueOf(ACTION_BAR_VIDEO_EDIT_COLOR));
        defaultColors.put(key_chat_goDownButtonIcon, Integer.valueOf(-5723992));
        defaultColors.put(key_chat_goDownButtonCounter, Integer.valueOf(-1));
        defaultColors.put(key_chat_goDownButtonCounterBackground, Integer.valueOf(-11689240));
        defaultColors.put(key_chat_messagePanelCancelInlineBot, Integer.valueOf(-5395027));
        defaultColors.put(key_chat_messagePanelVoicePressed, Integer.valueOf(-1));
        defaultColors.put(key_chat_messagePanelVoiceBackground, Integer.valueOf(-11037236));
        defaultColors.put(key_chat_messagePanelVoiceShadow, Integer.valueOf(NUM));
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
        defaultColors.put(key_chat_secretTimerBackground, Integer.valueOf(-NUM));
        defaultColors.put(key_chat_secretTimerText, Integer.valueOf(-1));
        defaultColors.put(key_profile_creatorIcon, Integer.valueOf(-12937771));
        defaultColors.put(key_profile_adminIcon, Integer.valueOf(-8026747));
        defaultColors.put(key_profile_actionIcon, Integer.valueOf(-8288630));
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
        defaultColors.put(key_player_progressBackground, Integer.valueOf(NUM));
        defaultColors.put(key_player_progressCachedBackground, Integer.valueOf(NUM));
        defaultColors.put(key_player_progress, Integer.valueOf(-14438417));
        defaultColors.put(key_player_placeholder, Integer.valueOf(-5723992));
        defaultColors.put(key_player_placeholderBackground, Integer.valueOf(-986896));
        defaultColors.put(key_player_button, Integer.valueOf(ACTION_BAR_MEDIA_PICKER_COLOR));
        defaultColors.put(key_player_buttonActive, Integer.valueOf(-11753238));
        defaultColors.put(key_files_folderIcon, Integer.valueOf(-6710887));
        defaultColors.put(key_files_folderIconBackground, Integer.valueOf(-986896));
        defaultColors.put(key_files_iconText, Integer.valueOf(-1));
        defaultColors.put(key_sessions_devicesImage, Integer.valueOf(-6908266));
        defaultColors.put(key_passport_authorizeBackground, Integer.valueOf(-12211217));
        defaultColors.put(key_passport_authorizeBackgroundSelected, Integer.valueOf(-12542501));
        defaultColors.put(key_passport_authorizeText, Integer.valueOf(-1));
        defaultColors.put(key_location_markerX, Integer.valueOf(-8355712));
        defaultColors.put(key_location_sendLocationBackground, Integer.valueOf(-9592620));
        defaultColors.put(key_location_sendLiveLocationBackground, Integer.valueOf(-39836));
        defaultColors.put(key_location_sendLocationIcon, Integer.valueOf(-1));
        defaultColors.put(key_location_sendLiveLocationIcon, Integer.valueOf(-1));
        defaultColors.put(key_location_liveLocationProgress, Integer.valueOf(-13262875));
        defaultColors.put(key_location_placeLocationBackground, Integer.valueOf(-11753238));
        defaultColors.put(key_dialog_liveLocationProgress, Integer.valueOf(-13262875));
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
        defaultColors.put(key_sharedMedia_linkPlaceholder, Integer.valueOf(-986123));
        defaultColors.put(key_sharedMedia_linkPlaceholderText, Integer.valueOf(-4735293));
        defaultColors.put(key_sharedMedia_photoPlaceholder, Integer.valueOf(-657931));
        defaultColors.put(key_checkbox, Integer.valueOf(-10567099));
        defaultColors.put(key_checkboxCheck, Integer.valueOf(-1));
        defaultColors.put(key_stickers_menu, Integer.valueOf(-4801083));
        defaultColors.put(key_stickers_menuSelector, Integer.valueOf(ACTION_BAR_AUDIO_SELECTOR_COLOR));
        defaultColors.put(key_changephoneinfo_image, Integer.valueOf(-5723992));
        defaultColors.put(key_changephoneinfo_changeText, Integer.valueOf(-11697229));
        defaultColors.put(key_groupcreate_hintText, Integer.valueOf(-6182221));
        defaultColors.put(key_groupcreate_cursor, Integer.valueOf(-11361317));
        defaultColors.put(key_groupcreate_sectionShadow, Integer.valueOf(ACTION_BAR_VIDEO_EDIT_COLOR));
        defaultColors.put(key_groupcreate_sectionText, Integer.valueOf(-8617336));
        defaultColors.put(key_groupcreate_onlineText, Integer.valueOf(-12545331));
        defaultColors.put(key_groupcreate_offlineText, Integer.valueOf(-8156010));
        defaultColors.put(key_groupcreate_checkbox, Integer.valueOf(-10567099));
        defaultColors.put(key_groupcreate_checkboxCheck, Integer.valueOf(-1));
        defaultColors.put(key_groupcreate_spanText, Integer.valueOf(-14540254));
        defaultColors.put(key_groupcreate_spanBackground, Integer.valueOf(-855310));
        defaultColors.put(key_groupcreate_spanDelete, Integer.valueOf(-1));
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
        fallbackKeys.put(key_location_sendLiveLocationIcon, key_location_sendLocationIcon);
        fallbackKeys.put(key_changephoneinfo_changeText, key_windowBackgroundWhiteBlueText4);
        fallbackKeys.put(key_graySectionText, key_windowBackgroundWhiteGrayText2);
        fallbackKeys.put(key_chat_inMediaIcon, key_chat_inBubble);
        fallbackKeys.put(key_chat_outMediaIcon, key_chat_outBubble);
        fallbackKeys.put(key_chat_inMediaIconSelected, key_chat_inBubbleSelected);
        fallbackKeys.put(key_chat_outMediaIconSelected, key_chat_outBubbleSelected);
        fallbackKeys.put(key_chats_actionUnreadIcon, key_profile_actionIcon);
        fallbackKeys.put(key_chats_actionUnreadBackground, key_profile_actionBackground);
        fallbackKeys.put(key_chats_actionUnreadPressedBackground, key_profile_actionPressedBackground);
        fallbackKeys.put(key_dialog_inlineProgressBackground, key_windowBackgroundGray);
        fallbackKeys.put(key_dialog_inlineProgress, key_chats_menuItemIcon);
        fallbackKeys.put(key_groupcreate_spanDelete, key_chats_actionIcon);
        fallbackKeys.put(key_sharedMedia_photoPlaceholder, key_windowBackgroundGray);
        fallbackKeys.put(key_chat_attachPollBackground, key_chat_attachAudioBackground);
        fallbackKeys.put(key_chat_attachPollIcon, key_chat_attachAudioIcon);
        fallbackKeys.put(key_chats_onlineCircle, key_windowBackgroundWhiteBlueText);
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
        themeInfo = new ThemeInfo();
        themeInfo.name = "Dark Blue";
        themeInfo.assetName = "darkblue.attheme";
        themes.add(themeInfo);
        themesDict.put("Dark Blue", themeInfo);
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
        String themesString = preferences.getString("themes2", null);
        int a;
        if (TextUtils.isEmpty(themesString)) {
            themesString = preferences.getString("themes", null);
            if (!TextUtils.isEmpty(themesString)) {
                String[] themesArr = themesString.split("&");
                for (String createWithString : themesArr) {
                    themeInfo = ThemeInfo.createWithString(createWithString);
                    if (themeInfo != null) {
                        otherThemes.add(themeInfo);
                        themes.add(themeInfo);
                        themesDict.put(themeInfo.name, themeInfo);
                    }
                }
            }
            Theme.saveOtherThemes();
            preferences.edit().remove("themes").commit();
        } else {
            try {
                JSONArray jsonArray = new JSONArray(themesString);
                for (a = 0; a < jsonArray.length(); a++) {
                    themeInfo = ThemeInfo.createWithJson(jsonArray.getJSONObject(a));
                    if (themeInfo != null) {
                        otherThemes.add(themeInfo);
                        themes.add(themeInfo);
                        themesDict.put(themeInfo.name, themeInfo);
                    }
                }
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }
        Theme.sortThemes();
        ThemeInfo applyingTheme = null;
        try {
            preferences = MessagesController.getGlobalMainSettings();
            String theme = preferences.getString("theme", null);
            if (theme != null) {
                applyingTheme = (ThemeInfo) themesDict.get(theme);
            }
            theme = preferences.getString("nighttheme", null);
            if (theme != null) {
                ThemeInfo t = (ThemeInfo) themesDict.get(theme);
                if (t != null) {
                    currentNightTheme = t;
                }
            }
            selectedAutoNightType = preferences.getInt("selectedAutoNightType", 0);
            autoNightScheduleByLocation = preferences.getBoolean("autoNightScheduleByLocation", false);
            autoNightBrighnessThreshold = preferences.getFloat("autoNightBrighnessThreshold", 0.25f);
            autoNightDayStartTime = preferences.getInt("autoNightDayStartTime", 1320);
            autoNightDayEndTime = preferences.getInt("autoNightDayEndTime", 480);
            autoNightSunsetTime = preferences.getInt("autoNightSunsetTime", 1320);
            autoNightSunriseTime = preferences.getInt("autoNightSunriseTime", 480);
            autoNightCityName = preferences.getString("autoNightCityName", TtmlNode.ANONYMOUS_REGION_ID);
            long val = preferences.getLong("autoNightLocationLatitude3", 10000);
            if (val != 10000) {
                autoNightLocationLatitude = Double.longBitsToDouble(val);
            } else {
                autoNightLocationLatitude = 10000.0d;
            }
            val = preferences.getLong("autoNightLocationLongitude3", 10000);
            if (val != 10000) {
                autoNightLocationLongitude = Double.longBitsToDouble(val);
            } else {
                autoNightLocationLongitude = 10000.0d;
            }
            autoNightLastSunCheckDay = preferences.getInt("autoNightLastSunCheckDay", -1);
        } catch (Throwable e2) {
            FileLog.m13e(e2);
        }
        if (applyingTheme == null) {
            applyingTheme = defaultTheme;
        } else {
            currentDayTheme = applyingTheme;
        }
        Theme.applyTheme(applyingTheme, false, false, false);
        AndroidUtilities.runOnUIThread(Theme$$Lambda$4.$instance);
    }

    public static void saveAutoNightThemeConfig() {
        Editor editor = MessagesController.getGlobalMainSettings().edit();
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
        if (currentNightTheme != null) {
            editor.putString("nighttheme", currentNightTheme.name);
        } else {
            editor.remove("nighttheme");
        }
        editor.commit();
    }

    @SuppressLint({"PrivateApi"})
    private static Drawable getStateDrawable(Drawable drawable, int index) {
        if (StateListDrawable_getStateDrawableMethod == null) {
            try {
                StateListDrawable_getStateDrawableMethod = StateListDrawable.class.getDeclaredMethod("getStateDrawable", new Class[]{Integer.TYPE});
            } catch (Throwable th) {
            }
        }
        if (StateListDrawable_getStateDrawableMethod == null) {
            return null;
        }
        try {
            return (Drawable) StateListDrawable_getStateDrawableMethod.invoke(drawable, new Object[]{Integer.valueOf(index)});
        } catch (Exception e) {
            return null;
        }
    }

    public static Drawable createEmojiIconSelectorDrawable(Context context, int resource, int defaultColor, int pressedColor) {
        Resources resources = context.getResources();
        Drawable defaultDrawable = resources.getDrawable(resource).mutate();
        if (defaultColor != 0) {
            defaultDrawable.setColorFilter(new PorterDuffColorFilter(defaultColor, Mode.MULTIPLY));
        }
        Drawable pressedDrawable = resources.getDrawable(resource).mutate();
        if (pressedColor != 0) {
            pressedDrawable.setColorFilter(new PorterDuffColorFilter(pressedColor, Mode.MULTIPLY));
        }
        StateListDrawable stateListDrawable = new CLASSNAME();
        stateListDrawable.setEnterFadeDuration(1);
        stateListDrawable.setExitFadeDuration(Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        stateListDrawable.addState(new int[]{16842913}, pressedDrawable);
        stateListDrawable.addState(new int[0], defaultDrawable);
        return stateListDrawable;
    }

    public static Drawable createEditTextDrawable(Context context, boolean alert) {
        Resources resources = context.getResources();
        Drawable defaultDrawable = resources.getDrawable(CLASSNAMER.drawable.search_dark).mutate();
        defaultDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(alert ? key_dialogInputField : key_windowBackgroundWhiteInputField), Mode.MULTIPLY));
        Drawable pressedDrawable = resources.getDrawable(CLASSNAMER.drawable.search_dark_activated).mutate();
        pressedDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(alert ? key_dialogInputFieldActivated : key_windowBackgroundWhiteInputFieldActivated), Mode.MULTIPLY));
        StateListDrawable stateListDrawable = new CLASSNAME();
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
        int minutes = calendar.get(12);
        int hour = calendar.get(11);
        if ((monthOfYear != 11 || dayOfMonth < 24 || dayOfMonth > 31) && (monthOfYear != 0 || dayOfMonth != 1)) {
            return -1;
        }
        return 0;
    }

    /* JADX WARNING: Missing block: B:16:0x0053, code:
            if (r1 <= 31) goto L_0x0059;
     */
    /* JADX WARNING: Missing block: B:18:0x0057, code:
            if (r1 == 1) goto L_0x0059;
     */
    /* JADX WARNING: Missing block: B:19:0x0059, code:
            dialogs_holidayDrawable = org.telegram.messenger.ApplicationLoader.applicationContext.getResources().getDrawable(org.telegram.messenger.CLASSNAMER.drawable.newyear);
            dialogs_holidayDrawableOffsetX = -org.telegram.messenger.AndroidUtilities.m9dp(3.0f);
            dialogs_holidayDrawableOffsetY = 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static Drawable getCurrentHolidayDrawable() {
        if (System.currentTimeMillis() - lastHolidayCheckTime >= ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS) {
            lastHolidayCheckTime = System.currentTimeMillis();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int monthOfYear = calendar.get(2);
            int dayOfMonth = calendar.get(5);
            int minutes = calendar.get(12);
            int hour = calendar.get(11);
            if (monthOfYear == 0 && dayOfMonth == 1 && minutes <= 10 && hour == 0) {
                canStartHolidayAnimation = true;
            } else {
                canStartHolidayAnimation = false;
            }
            if (dialogs_holidayDrawable == null) {
                if (monthOfYear == 11) {
                    int i;
                    if (BuildVars.DEBUG_PRIVATE_VERSION) {
                        i = 29;
                    } else {
                        i = 31;
                    }
                    if (dayOfMonth >= i) {
                    }
                }
                if (monthOfYear == 0) {
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

    public static Drawable createSimpleSelectorDrawable(Context context, int resource, int defaultColor, int pressedColor) {
        Resources resources = context.getResources();
        Drawable defaultDrawable = resources.getDrawable(resource).mutate();
        if (defaultColor != 0) {
            defaultDrawable.setColorFilter(new PorterDuffColorFilter(defaultColor, Mode.MULTIPLY));
        }
        Drawable pressedDrawable = resources.getDrawable(resource).mutate();
        if (pressedColor != 0) {
            pressedDrawable.setColorFilter(new PorterDuffColorFilter(pressedColor, Mode.MULTIPLY));
        }
        StateListDrawable stateListDrawable = new CLASSNAME();
        stateListDrawable.addState(new int[]{16842919}, pressedDrawable);
        stateListDrawable.addState(new int[]{16842913}, pressedDrawable);
        stateListDrawable.addState(StateSet.WILD_CARD, defaultDrawable);
        return stateListDrawable;
    }

    public static Drawable createCircleDrawable(int size, int color) {
        OvalShape ovalShape = new OvalShape();
        ovalShape.resize((float) size, (float) size);
        ShapeDrawable defaultDrawable = new ShapeDrawable(ovalShape);
        defaultDrawable.getPaint().setColor(color);
        return defaultDrawable;
    }

    public static CombinedDrawable createCircleDrawableWithIcon(int size, int iconRes) {
        return Theme.createCircleDrawableWithIcon(size, iconRes, 0);
    }

    public static CombinedDrawable createCircleDrawableWithIcon(int size, int iconRes, int stroke) {
        Drawable drawable;
        if (iconRes != 0) {
            drawable = ApplicationLoader.applicationContext.getResources().getDrawable(iconRes).mutate();
        } else {
            drawable = null;
        }
        return Theme.createCircleDrawableWithIcon(size, drawable, stroke);
    }

    public static CombinedDrawable createCircleDrawableWithIcon(int size, Drawable drawable, int stroke) {
        OvalShape ovalShape = new OvalShape();
        ovalShape.resize((float) size, (float) size);
        ShapeDrawable defaultDrawable = new ShapeDrawable(ovalShape);
        Paint paint = defaultDrawable.getPaint();
        paint.setColor(-1);
        if (stroke == 1) {
            paint.setStyle(Style.STROKE);
            paint.setStrokeWidth((float) AndroidUtilities.m9dp(2.0f));
        } else if (stroke == 2) {
            paint.setAlpha(0);
        }
        CombinedDrawable combinedDrawable = new CombinedDrawable(defaultDrawable, drawable);
        combinedDrawable.setCustomSize(size, size);
        return combinedDrawable;
    }

    public static Drawable createRoundRectDrawableWithIcon(int rad, int iconRes) {
        ShapeDrawable defaultDrawable = new ShapeDrawable(new RoundRectShape(new float[]{(float) rad, (float) rad, (float) rad, (float) rad, (float) rad, (float) rad, (float) rad, (float) rad}, null, null));
        defaultDrawable.getPaint().setColor(-1);
        return new CombinedDrawable(defaultDrawable, ApplicationLoader.applicationContext.getResources().getDrawable(iconRes).mutate());
    }

    public static void setCombinedDrawableColor(Drawable combinedDrawable, int color, boolean isIcon) {
        if (combinedDrawable instanceof CombinedDrawable) {
            Drawable drawable;
            if (isIcon) {
                drawable = ((CombinedDrawable) combinedDrawable).getIcon();
            } else {
                drawable = ((CombinedDrawable) combinedDrawable).getBackground();
            }
            if (drawable instanceof ColorDrawable) {
                ((ColorDrawable) drawable).setColor(color);
            } else {
                drawable.setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
            }
        }
    }

    public static Drawable createSimpleSelectorCircleDrawable(int size, int defaultColor, int pressedColor) {
        OvalShape ovalShape = new OvalShape();
        ovalShape.resize((float) size, (float) size);
        ShapeDrawable defaultDrawable = new ShapeDrawable(ovalShape);
        defaultDrawable.getPaint().setColor(defaultColor);
        ShapeDrawable pressedDrawable = new ShapeDrawable(ovalShape);
        if (VERSION.SDK_INT >= 21) {
            pressedDrawable.getPaint().setColor(-1);
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{pressedColor}), defaultDrawable, pressedDrawable);
        }
        pressedDrawable.getPaint().setColor(pressedColor);
        Drawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, pressedDrawable);
        stateListDrawable.addState(new int[]{16842908}, pressedDrawable);
        stateListDrawable.addState(StateSet.WILD_CARD, defaultDrawable);
        return stateListDrawable;
    }

    public static Drawable createRoundRectDrawable(int rad, int defaultColor) {
        ShapeDrawable defaultDrawable = new ShapeDrawable(new RoundRectShape(new float[]{(float) rad, (float) rad, (float) rad, (float) rad, (float) rad, (float) rad, (float) rad, (float) rad}, null, null));
        defaultDrawable.getPaint().setColor(defaultColor);
        return defaultDrawable;
    }

    public static Drawable createSimpleSelectorRoundRectDrawable(int rad, int defaultColor, int pressedColor) {
        ShapeDrawable defaultDrawable = new ShapeDrawable(new RoundRectShape(new float[]{(float) rad, (float) rad, (float) rad, (float) rad, (float) rad, (float) rad, (float) rad, (float) rad}, null, null));
        defaultDrawable.getPaint().setColor(defaultColor);
        ShapeDrawable pressedDrawable = new ShapeDrawable(new RoundRectShape(new float[]{(float) rad, (float) rad, (float) rad, (float) rad, (float) rad, (float) rad, (float) rad, (float) rad}, null, null));
        pressedDrawable.getPaint().setColor(pressedColor);
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, pressedDrawable);
        stateListDrawable.addState(new int[]{16842913}, pressedDrawable);
        stateListDrawable.addState(StateSet.WILD_CARD, defaultDrawable);
        return stateListDrawable;
    }

    public static Drawable getRoundRectSelectorDrawable() {
        if (VERSION.SDK_INT >= 21) {
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{Theme.getColor(key_dialogButtonSelector)}), null, Theme.createRoundRectDrawable(AndroidUtilities.m9dp(3.0f), -1));
        }
        Drawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, Theme.createRoundRectDrawable(AndroidUtilities.m9dp(3.0f), Theme.getColor(key_dialogButtonSelector)));
        stateListDrawable.addState(new int[]{16842913}, Theme.createRoundRectDrawable(AndroidUtilities.m9dp(3.0f), Theme.getColor(key_dialogButtonSelector)));
        stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(0));
        return stateListDrawable;
    }

    public static Drawable createSelectorWithBackgroundDrawable(int backgroundColor, int color) {
        if (VERSION.SDK_INT >= 21) {
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{color}), new ColorDrawable(backgroundColor), new ColorDrawable(backgroundColor));
        }
        Drawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, new ColorDrawable(color));
        stateListDrawable.addState(new int[]{16842913}, new ColorDrawable(color));
        stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(backgroundColor));
        return stateListDrawable;
    }

    public static Drawable getSelectorDrawable(boolean whiteBackground) {
        if (!whiteBackground) {
            return Theme.createSelectorDrawable(Theme.getColor(key_listSelector), 2);
        }
        if (VERSION.SDK_INT >= 21) {
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{Theme.getColor(key_listSelector)}), new ColorDrawable(Theme.getColor(key_windowBackgroundWhite)), new ColorDrawable(-1));
        }
        int color = Theme.getColor(key_listSelector);
        Drawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, new ColorDrawable(color));
        stateListDrawable.addState(new int[]{16842913}, new ColorDrawable(color));
        stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(Theme.getColor(key_windowBackgroundWhite)));
        return stateListDrawable;
    }

    public static Drawable createSelectorDrawable(int color) {
        return Theme.createSelectorDrawable(color, 1);
    }

    public static Drawable createSelectorDrawable(int color, int maskType) {
        if (VERSION.SDK_INT >= 21) {
            Drawable maskDrawable = null;
            if (maskType == 1) {
                maskPaint.setColor(-1);
                maskDrawable = new CLASSNAME();
            } else if (maskType == 2) {
                maskDrawable = new ColorDrawable(-1);
            }
            return new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{color}), null, maskDrawable);
        }
        Drawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, new ColorDrawable(color));
        stateListDrawable.addState(new int[]{16842913}, new ColorDrawable(color));
        stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(0));
        return stateListDrawable;
    }

    public static void applyPreviousTheme() {
        if (previousTheme != null) {
            Theme.applyTheme(previousTheme, true, false, false);
            previousTheme = null;
            Theme.checkAutoNightThemeConditions();
        }
    }

    private static void sortThemes() {
        Collections.sort(themes, Theme$$Lambda$0.$instance);
    }

    static final /* synthetic */ int lambda$sortThemes$0$Theme(ThemeInfo o1, ThemeInfo o2) {
        if (o1.pathToFile == null && o1.assetName == null) {
            return -1;
        }
        if (o2.pathToFile == null && o2.assetName == null) {
            return 1;
        }
        return o1.name.compareTo(o2.name);
    }

    public static ThemeInfo applyThemeFile(File file, String themeName, boolean temporary) {
        boolean z = true;
        try {
            if (themeName.equals("Default") || themeName.equals("Dark") || themeName.equals("Blue") || themeName.equals("Dark Blue")) {
                return null;
            }
            File finalFile = new File(ApplicationLoader.getFilesDirFixed(), themeName);
            if (!AndroidUtilities.copyFile(file, finalFile)) {
                return null;
            }
            boolean newTheme = false;
            ThemeInfo themeInfo = (ThemeInfo) themesDict.get(themeName);
            if (themeInfo == null) {
                newTheme = true;
                themeInfo = new ThemeInfo();
                themeInfo.name = themeName;
                themeInfo.pathToFile = finalFile.getAbsolutePath();
            }
            if (temporary) {
                previousTheme = currentTheme;
            } else {
                previousTheme = null;
                if (newTheme) {
                    themes.add(themeInfo);
                    themesDict.put(themeInfo.name, themeInfo);
                    otherThemes.add(themeInfo);
                    Theme.sortThemes();
                    Theme.saveOtherThemes();
                }
            }
            if (temporary) {
                z = false;
            }
            Theme.applyTheme(themeInfo, z, true, false);
            return themeInfo;
        } catch (Throwable e) {
            FileLog.m13e(e);
            return null;
        }
    }

    public static void applyTheme(ThemeInfo themeInfo) {
        Theme.applyTheme(themeInfo, true, true, false);
    }

    public static void applyTheme(ThemeInfo themeInfo, boolean animated) {
        Theme.applyTheme(themeInfo, true, true, animated);
    }

    public static void applyTheme(ThemeInfo themeInfo, boolean save, boolean removeWallpaperOverride, boolean nightTheme) {
        if (themeInfo != null) {
            ThemeEditorView editorView = ThemeEditorView.getInstance();
            if (editorView != null) {
                editorView.destroy();
            }
            try {
                Editor editor;
                if (themeInfo.pathToFile == null && themeInfo.assetName == null) {
                    if (!nightTheme && save) {
                        editor = MessagesController.getGlobalMainSettings().edit();
                        editor.remove("theme");
                        if (removeWallpaperOverride) {
                            editor.remove("overrideThemeWallpaper");
                        }
                        editor.commit();
                    }
                    currentColors.clear();
                    wallpaper = null;
                    themedWallpaper = null;
                } else {
                    if (!nightTheme && save) {
                        editor = MessagesController.getGlobalMainSettings().edit();
                        editor.putString("theme", themeInfo.name);
                        if (removeWallpaperOverride) {
                            editor.remove("overrideThemeWallpaper");
                        }
                        editor.commit();
                    }
                    if (themeInfo.assetName != null) {
                        currentColors = Theme.getThemeFileValues(null, themeInfo.assetName);
                    } else {
                        currentColors = Theme.getThemeFileValues(new File(themeInfo.pathToFile), null);
                    }
                }
                currentTheme = themeInfo;
                if (!nightTheme) {
                    currentDayTheme = currentTheme;
                }
                Theme.reloadWallpaper();
                Theme.applyCommonTheme();
                Theme.applyDialogsTheme();
                Theme.applyProfileTheme();
                Theme.applyChatTheme(false);
                AndroidUtilities.runOnUIThread(new Theme$$Lambda$1(nightTheme));
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }
    }

    private static void saveOtherThemes() {
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
        JSONArray array = new JSONArray();
        for (int a = 0; a < otherThemes.size(); a++) {
            JSONObject jsonObject = ((ThemeInfo) otherThemes.get(a)).getSaveJson();
            if (jsonObject != null) {
                array.put(jsonObject);
            }
        }
        editor.putString("themes2", array.toString());
        editor.commit();
    }

    public static HashMap<String, Integer> getDefaultColors() {
        return defaultColors;
    }

    public static String getCurrentThemeName() {
        String text = currentDayTheme.getName();
        if (text.toLowerCase().endsWith(".attheme")) {
            return text.substring(0, text.lastIndexOf(46));
        }
        return text;
    }

    public static String getCurrentNightThemeName() {
        if (currentNightTheme == null) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        String text = currentNightTheme.getName();
        if (text.toLowerCase().endsWith(".attheme")) {
            return text.substring(0, text.lastIndexOf(46));
        }
        return text;
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

    public static boolean isCurrentThemeDefault() {
        return currentTheme == defaultTheme;
    }

    private static long getAutoNightSwitchThemeDelay() {
        if (Math.abs(lastThemeSwitchTime - SystemClock.elapsedRealtime()) >= 12000) {
            return 1800;
        }
        return 12000;
    }

    public static void setCurrentNightTheme(ThemeInfo theme) {
        boolean apply = currentTheme == currentNightTheme;
        currentNightTheme = theme;
        if (apply) {
            Theme.applyDayNightThemeMaybe(true);
        }
    }

    public static void checkAutoNightThemeConditions() {
        Theme.checkAutoNightThemeConditions(false);
    }

    public static void checkAutoNightThemeConditions(boolean force) {
        if (previousTheme == null) {
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
                        FileLog.m10d("light sensor unregistered");
                    }
                }
            }
            int switchToTheme = 0;
            if (selectedAutoNightType == 1) {
                int timeStart;
                int timeEnd;
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                int time = (calendar.get(11) * 60) + calendar.get(12);
                if (autoNightScheduleByLocation) {
                    int day = calendar.get(5);
                    if (!(autoNightLastSunCheckDay == day || autoNightLocationLatitude == 10000.0d || autoNightLocationLongitude == 10000.0d)) {
                        int[] t = SunDate.calculateSunriseSunset(autoNightLocationLatitude, autoNightLocationLongitude);
                        autoNightSunriseTime = t[0];
                        autoNightSunsetTime = t[1];
                        autoNightLastSunCheckDay = day;
                        Theme.saveAutoNightThemeConfig();
                    }
                    timeStart = autoNightSunsetTime;
                    timeEnd = autoNightSunriseTime;
                } else {
                    timeStart = autoNightDayStartTime;
                    timeEnd = autoNightDayEndTime;
                }
                switchToTheme = timeStart < timeEnd ? (timeStart > time || time > timeEnd) ? 1 : 2 : ((timeStart > time || time > 1440) && (time < 0 || time > timeEnd)) ? 1 : 2;
            } else if (selectedAutoNightType == 2) {
                if (lightSensor == null) {
                    sensorManager = (SensorManager) ApplicationLoader.applicationContext.getSystemService("sensor");
                    lightSensor = sensorManager.getDefaultSensor(5);
                }
                if (!(lightSensorRegistered || lightSensor == null)) {
                    sensorManager.registerListener(ambientSensorListener, lightSensor, 500000);
                    lightSensorRegistered = true;
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m10d("light sensor registered");
                    }
                }
                if (lastBrightnessValue <= autoNightBrighnessThreshold) {
                    if (!switchNightRunnableScheduled) {
                        switchToTheme = 2;
                    }
                } else if (!switchDayRunnableScheduled) {
                    switchToTheme = 1;
                }
            } else if (selectedAutoNightType == 0) {
                switchToTheme = 1;
            }
            if (switchToTheme != 0) {
                Theme.applyDayNightThemeMaybe(switchToTheme == 2);
            }
            if (force) {
                lastThemeSwitchTime = 0;
            }
        }
    }

    private static void applyDayNightThemeMaybe(boolean night) {
        if (night) {
            if (currentTheme != currentNightTheme) {
                lastThemeSwitchTime = SystemClock.elapsedRealtime();
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, currentNightTheme);
            }
        } else if (currentTheme != currentDayTheme) {
            lastThemeSwitchTime = SystemClock.elapsedRealtime();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, currentDayTheme);
        }
    }

    public static boolean deleteTheme(ThemeInfo themeInfo) {
        if (themeInfo.pathToFile == null) {
            return false;
        }
        boolean currentThemeDeleted = false;
        if (currentTheme == themeInfo) {
            Theme.applyTheme(defaultTheme, true, false, false);
            currentThemeDeleted = true;
        }
        otherThemes.remove(themeInfo);
        themesDict.remove(themeInfo.name);
        themes.remove(themeInfo);
        new File(themeInfo.pathToFile).delete();
        Theme.saveOtherThemes();
        return currentThemeDeleted;
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:0x00f8 A:{SYNTHETIC, Splitter: B:38:0x00f8} */
    /* JADX WARNING: Removed duplicated region for block: B:50:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00ec A:{SYNTHETIC, Splitter: B:32:0x00ec} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void saveCurrentTheme(String name, boolean finalSave) {
        Throwable e;
        Throwable th;
        StringBuilder result = new StringBuilder();
        for (Entry<String, Integer> entry : currentColors.entrySet()) {
            result.append((String) entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        File file = new File(ApplicationLoader.getFilesDirFixed(), name);
        FileOutputStream stream = null;
        try {
            FileOutputStream stream2 = new FileOutputStream(file);
            try {
                stream2.write(AndroidUtilities.getStringBytes(result.toString()));
                if (themedWallpaper instanceof BitmapDrawable) {
                    Bitmap bitmap = ((BitmapDrawable) themedWallpaper).getBitmap();
                    if (bitmap != null) {
                        stream2.write(new byte[]{(byte) 87, (byte) 80, (byte) 83, (byte) 10});
                        bitmap.compress(CompressFormat.JPEG, 87, stream2);
                        stream2.write(new byte[]{(byte) 10, (byte) 87, (byte) 80, (byte) 69, (byte) 10});
                    }
                    if (finalSave) {
                        wallpaper = themedWallpaper;
                        Theme.calcBackgroundColor(wallpaper, 2);
                    }
                }
                ThemeInfo newTheme = (ThemeInfo) themesDict.get(name);
                if (newTheme == null) {
                    newTheme = new ThemeInfo();
                    newTheme.pathToFile = file.getAbsolutePath();
                    newTheme.name = name;
                    themes.add(newTheme);
                    themesDict.put(newTheme.name, newTheme);
                    otherThemes.add(newTheme);
                    Theme.saveOtherThemes();
                    Theme.sortThemes();
                }
                currentTheme = newTheme;
                if (currentTheme != currentNightTheme) {
                    currentDayTheme = currentTheme;
                }
                Editor editor = MessagesController.getGlobalMainSettings().edit();
                editor.putString("theme", currentDayTheme.name);
                editor.commit();
                if (stream2 != null) {
                    try {
                        stream2.close();
                    } catch (Throwable e2) {
                        FileLog.m13e(e2);
                        stream = stream2;
                        return;
                    }
                }
                stream = stream2;
            } catch (Exception e3) {
                e2 = e3;
                stream = stream2;
                try {
                    FileLog.m13e(e2);
                    if (stream == null) {
                        try {
                            stream.close();
                        } catch (Throwable e22) {
                            FileLog.m13e(e22);
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Throwable e222) {
                            FileLog.m13e(e222);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                stream = stream2;
                if (stream != null) {
                }
                throw th;
            }
        } catch (Exception e4) {
            e222 = e4;
            FileLog.m13e(e222);
            if (stream == null) {
            }
        }
    }

    public static File getAssetFile(String assetName) {
        File file = new File(ApplicationLoader.getFilesDirFixed(), assetName);
        long size;
        try {
            InputStream stream = ApplicationLoader.applicationContext.getAssets().open(assetName);
            size = (long) stream.available();
            stream.close();
        } catch (Throwable e) {
            size = 0;
            FileLog.m13e(e);
        }
        if (!(file.exists() && (size == 0 || file.length() == size))) {
            InputStream in = null;
            try {
                in = ApplicationLoader.applicationContext.getAssets().open(assetName);
                AndroidUtilities.copyFile(in, file);
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception e2) {
                    }
                }
            } catch (Throwable e3) {
                FileLog.m13e(e3);
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception e4) {
                    }
                }
            } catch (Throwable th) {
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception e5) {
                    }
                }
            }
        }
        return file;
    }

    /* JADX WARNING: Removed duplicated region for block: B:58:0x00f8 A:{SYNTHETIC, Splitter: B:58:0x00f8} */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00eb A:{SYNTHETIC, Splitter: B:52:0x00eb} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static HashMap<String, Integer> getThemeFileValues(File file, String assetName) {
        Throwable e;
        Throwable th;
        FileInputStream stream = null;
        HashMap<String, Integer> stringMap = new HashMap();
        try {
            byte[] bytes = new byte[1024];
            int currentPosition = 0;
            if (assetName != null) {
                file = Theme.getAssetFile(assetName);
            }
            FileInputStream fileInputStream = new FileInputStream(file);
            boolean finished = false;
            try {
                themedWallpaperFileOffset = -1;
                do {
                    int read = fileInputStream.read(bytes);
                    if (read == -1) {
                        break;
                    }
                    int previousPosition = currentPosition;
                    int start = 0;
                    for (int a = 0; a < read; a++) {
                        if (bytes[a] == (byte) 10) {
                            int len = (a - start) + 1;
                            String line = new String(bytes, start, len - 1, CLASSNAMEC.UTF8_NAME);
                            if (line.startsWith("WPS")) {
                                themedWallpaperFileOffset = currentPosition + len;
                                finished = true;
                                break;
                            }
                            int idx = line.indexOf(61);
                            if (idx != -1) {
                                int value;
                                String key = line.substring(0, idx);
                                String param = line.substring(idx + 1);
                                if (param.length() <= 0 || param.charAt(0) != '#') {
                                    value = Utilities.parseInt(param).intValue();
                                } else {
                                    try {
                                        value = Color.parseColor(param);
                                    } catch (Exception e2) {
                                        value = Utilities.parseInt(param).intValue();
                                    }
                                }
                                stringMap.put(key, Integer.valueOf(value));
                            }
                            start += len;
                            currentPosition += len;
                        }
                    }
                    if (previousPosition == currentPosition) {
                        break;
                    }
                    fileInputStream.getChannel().position((long) currentPosition);
                } while (!finished);
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (Throwable e3) {
                        FileLog.m13e(e3);
                        stream = fileInputStream;
                    }
                }
                stream = fileInputStream;
            } catch (Throwable th2) {
                th = th2;
                stream = fileInputStream;
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Throwable e32) {
                        FileLog.m13e(e32);
                    }
                }
                throw th;
            }
        } catch (Throwable th3) {
            e32 = th3;
            FileLog.m13e(e32);
            if (stream != null) {
            }
            return stringMap;
        }
        return stringMap;
    }

    public static void createCommonResources(Context context) {
        if (dividerPaint == null) {
            dividerPaint = new Paint();
            dividerPaint.setStrokeWidth(1.0f);
            avatar_backgroundPaint = new Paint(1);
            checkboxSquare_checkPaint = new Paint(1);
            checkboxSquare_checkPaint.setStyle(Style.STROKE);
            checkboxSquare_checkPaint.setStrokeWidth((float) AndroidUtilities.m9dp(2.0f));
            checkboxSquare_eraserPaint = new Paint(1);
            checkboxSquare_eraserPaint.setColor(0);
            checkboxSquare_eraserPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            checkboxSquare_backgroundPaint = new Paint(1);
            linkSelectionPaint = new Paint();
            Resources resources = context.getResources();
            avatar_broadcastDrawable = resources.getDrawable(CLASSNAMER.drawable.broadcast_w);
            avatar_savedDrawable = resources.getDrawable(CLASSNAMER.drawable.bookmark_large);
            Theme.applyCommonTheme();
        }
    }

    public static void applyCommonTheme() {
        if (dividerPaint != null) {
            dividerPaint.setColor(Theme.getColor(key_divider));
            linkSelectionPaint.setColor(Theme.getColor(key_windowBackgroundWhiteLinkSelection));
            Theme.setDrawableColorByKey(avatar_broadcastDrawable, key_avatar_text);
            Theme.setDrawableColorByKey(avatar_savedDrawable, key_avatar_text);
        }
    }

    public static void createDialogsResources(Context context) {
        Theme.createCommonResources(context);
        if (dialogs_namePaint == null) {
            Resources resources = context.getResources();
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
            dialogs_onlineCirclePaint = new Paint(1);
            dialogs_countPaint = new Paint(1);
            dialogs_countGrayPaint = new Paint(1);
            dialogs_errorPaint = new Paint(1);
            dialogs_lockDrawable = resources.getDrawable(CLASSNAMER.drawable.list_secret);
            dialogs_checkDrawable = resources.getDrawable(CLASSNAMER.drawable.list_check);
            dialogs_halfCheckDrawable = resources.getDrawable(CLASSNAMER.drawable.list_halfcheck);
            dialogs_clockDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_clock).mutate();
            dialogs_errorDrawable = resources.getDrawable(CLASSNAMER.drawable.list_warning_sign);
            dialogs_groupDrawable = resources.getDrawable(CLASSNAMER.drawable.list_group);
            dialogs_broadcastDrawable = resources.getDrawable(CLASSNAMER.drawable.list_broadcast);
            dialogs_muteDrawable = resources.getDrawable(CLASSNAMER.drawable.list_mute).mutate();
            dialogs_verifiedDrawable = resources.getDrawable(CLASSNAMER.drawable.verified_area);
            dialogs_verifiedCheckDrawable = resources.getDrawable(CLASSNAMER.drawable.verified_check);
            dialogs_mentionDrawable = resources.getDrawable(CLASSNAMER.drawable.mentionchatslist);
            dialogs_botDrawable = resources.getDrawable(CLASSNAMER.drawable.list_bot);
            dialogs_pinnedDrawable = resources.getDrawable(CLASSNAMER.drawable.list_pin);
            moveUpDrawable = resources.getDrawable(CLASSNAMER.drawable.preview_open);
            Theme.applyDialogsTheme();
        }
        dialogs_namePaint.setTextSize((float) AndroidUtilities.m9dp(17.0f));
        dialogs_nameEncryptedPaint.setTextSize((float) AndroidUtilities.m9dp(17.0f));
        dialogs_messagePaint.setTextSize((float) AndroidUtilities.m9dp(16.0f));
        dialogs_messagePrintingPaint.setTextSize((float) AndroidUtilities.m9dp(16.0f));
        dialogs_timePaint.setTextSize((float) AndroidUtilities.m9dp(13.0f));
        dialogs_countTextPaint.setTextSize((float) AndroidUtilities.m9dp(13.0f));
        dialogs_onlinePaint.setTextSize((float) AndroidUtilities.m9dp(16.0f));
        dialogs_offlinePaint.setTextSize((float) AndroidUtilities.m9dp(16.0f));
    }

    public static void applyDialogsTheme() {
        if (dialogs_namePaint != null) {
            dialogs_namePaint.setColor(Theme.getColor(key_chats_name));
            dialogs_nameEncryptedPaint.setColor(Theme.getColor(key_chats_secretName));
            TextPaint textPaint = dialogs_messagePaint;
            TextPaint textPaint2 = dialogs_messagePaint;
            int color = Theme.getColor(key_chats_message);
            textPaint2.linkColor = color;
            textPaint.setColor(color);
            dialogs_tabletSeletedPaint.setColor(Theme.getColor(key_chats_tabletSelectedOverlay));
            dialogs_pinnedPaint.setColor(Theme.getColor(key_chats_pinnedOverlay));
            dialogs_timePaint.setColor(Theme.getColor(key_chats_date));
            dialogs_countTextPaint.setColor(Theme.getColor(key_chats_unreadCounterText));
            dialogs_messagePrintingPaint.setColor(Theme.getColor(key_chats_actionMessage));
            dialogs_countPaint.setColor(Theme.getColor(key_chats_unreadCounter));
            dialogs_countGrayPaint.setColor(Theme.getColor(key_chats_unreadCounterMuted));
            dialogs_errorPaint.setColor(Theme.getColor(key_chats_sentError));
            dialogs_onlinePaint.setColor(Theme.getColor(key_windowBackgroundWhiteBlueText3));
            dialogs_offlinePaint.setColor(Theme.getColor(key_windowBackgroundWhiteGrayText3));
            Theme.setDrawableColorByKey(dialogs_lockDrawable, key_chats_secretIcon);
            Theme.setDrawableColorByKey(dialogs_checkDrawable, key_chats_sentCheck);
            Theme.setDrawableColorByKey(dialogs_halfCheckDrawable, key_chats_sentCheck);
            Theme.setDrawableColorByKey(dialogs_clockDrawable, key_chats_sentClock);
            Theme.setDrawableColorByKey(dialogs_errorDrawable, key_chats_sentErrorIcon);
            Theme.setDrawableColorByKey(dialogs_groupDrawable, key_chats_nameIcon);
            Theme.setDrawableColorByKey(dialogs_broadcastDrawable, key_chats_nameIcon);
            Theme.setDrawableColorByKey(dialogs_botDrawable, key_chats_nameIcon);
            Theme.setDrawableColorByKey(dialogs_pinnedDrawable, key_chats_pinnedIcon);
            Theme.setDrawableColorByKey(dialogs_muteDrawable, key_chats_muteIcon);
            Theme.setDrawableColorByKey(dialogs_mentionDrawable, key_chats_mentionIcon);
            Theme.setDrawableColorByKey(dialogs_verifiedDrawable, key_chats_verifiedBackground);
            Theme.setDrawableColorByKey(dialogs_verifiedCheckDrawable, key_chats_verifiedCheck);
        }
    }

    public static void destroyResources() {
        for (int a = 0; a < chat_attachButtonDrawables.length; a++) {
            if (chat_attachButtonDrawables[a] != null) {
                chat_attachButtonDrawables[a].setCallback(null);
            }
        }
    }

    public static void createChatResources(Context context, boolean fontsOnly) {
        synchronized (sync) {
            if (chat_msgTextPaint == null) {
                chat_msgTextPaint = new TextPaint(1);
                chat_msgGameTextPaint = new TextPaint(1);
                chat_msgTextPaintOneEmoji = new TextPaint(1);
                chat_msgTextPaintTwoEmoji = new TextPaint(1);
                chat_msgTextPaintThreeEmoji = new TextPaint(1);
                chat_msgBotButtonPaint = new TextPaint(1);
                chat_msgBotButtonPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            }
        }
        if (!fontsOnly && chat_msgInDrawable == null) {
            chat_infoPaint = new TextPaint(1);
            chat_docNamePaint = new TextPaint(1);
            chat_docNamePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_docBackPaint = new Paint(1);
            chat_deleteProgressPaint = new Paint(1);
            chat_botProgressPaint = new Paint(1);
            chat_botProgressPaint.setStrokeCap(Cap.ROUND);
            chat_botProgressPaint.setStyle(Style.STROKE);
            chat_locationTitlePaint = new TextPaint(1);
            chat_locationTitlePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_locationAddressPaint = new TextPaint(1);
            chat_urlPaint = new Paint();
            chat_textSearchSelectionPaint = new Paint();
            chat_radialProgressPaint = new Paint(1);
            chat_radialProgressPaint.setStrokeCap(Cap.ROUND);
            chat_radialProgressPaint.setStyle(Style.STROKE);
            chat_radialProgressPaint.setColor(-NUM);
            chat_radialProgress2Paint = new Paint(1);
            chat_radialProgress2Paint.setStrokeCap(Cap.ROUND);
            chat_radialProgress2Paint.setStyle(Style.STROKE);
            chat_audioTimePaint = new TextPaint(1);
            chat_livePaint = new TextPaint(1);
            chat_livePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_audioTitlePaint = new TextPaint(1);
            chat_audioTitlePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_audioPerformerPaint = new TextPaint(1);
            chat_botButtonPaint = new TextPaint(1);
            chat_botButtonPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_contactNamePaint = new TextPaint(1);
            chat_contactNamePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_contactPhonePaint = new TextPaint(1);
            chat_durationPaint = new TextPaint(1);
            chat_gamePaint = new TextPaint(1);
            chat_gamePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_shipmentPaint = new TextPaint(1);
            chat_timePaint = new TextPaint(1);
            chat_adminPaint = new TextPaint(1);
            chat_namePaint = new TextPaint(1);
            chat_namePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_forwardNamePaint = new TextPaint(1);
            chat_replyNamePaint = new TextPaint(1);
            chat_replyNamePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_replyTextPaint = new TextPaint(1);
            chat_instantViewPaint = new TextPaint(1);
            chat_instantViewPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_instantViewRectPaint = new Paint(1);
            chat_instantViewRectPaint.setStyle(Style.STROKE);
            chat_replyLinePaint = new Paint();
            chat_msgErrorPaint = new Paint(1);
            chat_statusPaint = new Paint(1);
            chat_statusRecordPaint = new Paint(1);
            chat_statusRecordPaint.setStyle(Style.STROKE);
            chat_statusRecordPaint.setStrokeCap(Cap.ROUND);
            chat_actionTextPaint = new TextPaint(1);
            chat_actionTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_actionBackgroundPaint = new Paint(1);
            chat_timeBackgroundPaint = new Paint(1);
            chat_contextResult_titleTextPaint = new TextPaint(1);
            chat_contextResult_titleTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_contextResult_descriptionTextPaint = new TextPaint(1);
            chat_composeBackgroundPaint = new Paint();
            Resources resources = context.getResources();
            chat_msgInDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_in).mutate();
            chat_msgInSelectedDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_in).mutate();
            chat_msgOutDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_out).mutate();
            chat_msgOutSelectedDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_out).mutate();
            chat_msgInMediaDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_photo).mutate();
            chat_msgInMediaSelectedDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_photo).mutate();
            chat_msgOutMediaDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_photo).mutate();
            chat_msgOutMediaSelectedDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_photo).mutate();
            chat_msgOutCheckDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_check).mutate();
            chat_msgOutCheckSelectedDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_check).mutate();
            chat_msgMediaCheckDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_check).mutate();
            chat_msgStickerCheckDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_check).mutate();
            chat_msgOutHalfCheckDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_halfcheck).mutate();
            chat_msgOutHalfCheckSelectedDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_halfcheck).mutate();
            chat_msgMediaHalfCheckDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_halfcheck).mutate();
            chat_msgStickerHalfCheckDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_halfcheck).mutate();
            chat_msgOutClockDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_clock).mutate();
            chat_msgOutSelectedClockDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_clock).mutate();
            chat_msgInClockDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_clock).mutate();
            chat_msgInSelectedClockDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_clock).mutate();
            chat_msgMediaClockDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_clock).mutate();
            chat_msgStickerClockDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_clock).mutate();
            chat_msgInViewsDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_views).mutate();
            chat_msgInViewsSelectedDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_views).mutate();
            chat_msgOutViewsDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_views).mutate();
            chat_msgOutViewsSelectedDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_views).mutate();
            chat_msgMediaViewsDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_views).mutate();
            chat_msgStickerViewsDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_views).mutate();
            chat_msgInMenuDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_actions).mutate();
            chat_msgInMenuSelectedDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_actions).mutate();
            chat_msgOutMenuDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_actions).mutate();
            chat_msgOutMenuSelectedDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_actions).mutate();
            chat_msgMediaMenuDrawable = resources.getDrawable(CLASSNAMER.drawable.video_actions);
            chat_msgInInstantDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_instant).mutate();
            chat_msgOutInstantDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_instant).mutate();
            chat_msgErrorDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_warning);
            chat_muteIconDrawable = resources.getDrawable(CLASSNAMER.drawable.list_mute).mutate();
            chat_lockIconDrawable = resources.getDrawable(CLASSNAMER.drawable.ic_lock_header);
            chat_msgBroadcastDrawable = resources.getDrawable(CLASSNAMER.drawable.broadcast3).mutate();
            chat_msgBroadcastMediaDrawable = resources.getDrawable(CLASSNAMER.drawable.broadcast3).mutate();
            chat_msgInCallDrawable = resources.getDrawable(CLASSNAMER.drawable.ic_call_white_24dp).mutate();
            chat_msgInCallSelectedDrawable = resources.getDrawable(CLASSNAMER.drawable.ic_call_white_24dp).mutate();
            chat_msgOutCallDrawable = resources.getDrawable(CLASSNAMER.drawable.ic_call_white_24dp).mutate();
            chat_msgOutCallSelectedDrawable = resources.getDrawable(CLASSNAMER.drawable.ic_call_white_24dp).mutate();
            chat_msgCallUpRedDrawable = resources.getDrawable(CLASSNAMER.drawable.ic_call_made_green_18dp).mutate();
            chat_msgCallUpGreenDrawable = resources.getDrawable(CLASSNAMER.drawable.ic_call_made_green_18dp).mutate();
            chat_msgCallDownRedDrawable = resources.getDrawable(CLASSNAMER.drawable.ic_call_received_green_18dp).mutate();
            chat_msgCallDownGreenDrawable = resources.getDrawable(CLASSNAMER.drawable.ic_call_received_green_18dp).mutate();
            chat_msgAvatarLiveLocationDrawable = resources.getDrawable(CLASSNAMER.drawable.livepin).mutate();
            chat_inlineResultFile = resources.getDrawable(CLASSNAMER.drawable.bot_file);
            chat_inlineResultAudio = resources.getDrawable(CLASSNAMER.drawable.bot_music);
            chat_inlineResultLocation = resources.getDrawable(CLASSNAMER.drawable.bot_location);
            chat_redLocationIcon = resources.getDrawable(CLASSNAMER.drawable.map_pin).mutate();
            chat_msgInShadowDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_in_shadow);
            chat_msgOutShadowDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_out_shadow);
            chat_msgInMediaShadowDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_photo_shadow);
            chat_msgOutMediaShadowDrawable = resources.getDrawable(CLASSNAMER.drawable.msg_photo_shadow);
            chat_botLinkDrawalbe = resources.getDrawable(CLASSNAMER.drawable.bot_link);
            chat_botInlineDrawable = resources.getDrawable(CLASSNAMER.drawable.bot_lines);
            chat_systemDrawable = resources.getDrawable(CLASSNAMER.drawable.system);
            chat_contextResult_shadowUnderSwitchDrawable = resources.getDrawable(CLASSNAMER.drawable.header_shadow).mutate();
            chat_attachButtonDrawables[0] = new AttachCameraDrawable();
            chat_attachButtonDrawables[1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(54.0f), CLASSNAMER.drawable.attach_gallery);
            chat_attachButtonDrawables[2] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(54.0f), CLASSNAMER.drawable.attach_video);
            chat_attachButtonDrawables[3] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(54.0f), CLASSNAMER.drawable.attach_audio);
            chat_attachButtonDrawables[4] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(54.0f), CLASSNAMER.drawable.attach_file);
            chat_attachButtonDrawables[5] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(54.0f), CLASSNAMER.drawable.attach_contact);
            chat_attachButtonDrawables[6] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(54.0f), CLASSNAMER.drawable.attach_location);
            chat_attachButtonDrawables[7] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(54.0f), CLASSNAMER.drawable.attach_close);
            chat_attachButtonDrawables[8] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(54.0f), CLASSNAMER.drawable.attach_send);
            chat_attachButtonDrawables[9] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(54.0f), CLASSNAMER.drawable.attach_polls);
            chat_cornerOuter[0] = resources.getDrawable(CLASSNAMER.drawable.corner_out_tl);
            chat_cornerOuter[1] = resources.getDrawable(CLASSNAMER.drawable.corner_out_tr);
            chat_cornerOuter[2] = resources.getDrawable(CLASSNAMER.drawable.corner_out_br);
            chat_cornerOuter[3] = resources.getDrawable(CLASSNAMER.drawable.corner_out_bl);
            chat_cornerInner[0] = resources.getDrawable(CLASSNAMER.drawable.corner_in_tr);
            chat_cornerInner[1] = resources.getDrawable(CLASSNAMER.drawable.corner_in_tl);
            chat_cornerInner[2] = resources.getDrawable(CLASSNAMER.drawable.corner_in_br);
            chat_cornerInner[3] = resources.getDrawable(CLASSNAMER.drawable.corner_in_bl);
            chat_shareDrawable = resources.getDrawable(CLASSNAMER.drawable.share_round);
            chat_shareIconDrawable = resources.getDrawable(CLASSNAMER.drawable.share_arrow);
            chat_replyIconDrawable = resources.getDrawable(CLASSNAMER.drawable.fast_reply);
            chat_goIconDrawable = resources.getDrawable(CLASSNAMER.drawable.message_arrow);
            chat_ivStatesDrawable[0][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(40.0f), (int) CLASSNAMER.drawable.msg_round_play_m, 1);
            chat_ivStatesDrawable[0][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(40.0f), (int) CLASSNAMER.drawable.msg_round_play_m, 1);
            chat_ivStatesDrawable[1][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(40.0f), (int) CLASSNAMER.drawable.msg_round_pause_m, 1);
            chat_ivStatesDrawable[1][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(40.0f), (int) CLASSNAMER.drawable.msg_round_pause_m, 1);
            chat_ivStatesDrawable[2][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(40.0f), (int) CLASSNAMER.drawable.msg_round_load_m, 1);
            chat_ivStatesDrawable[2][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(40.0f), (int) CLASSNAMER.drawable.msg_round_load_m, 1);
            chat_ivStatesDrawable[3][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(40.0f), (int) CLASSNAMER.drawable.msg_round_cancel_m, 2);
            chat_ivStatesDrawable[3][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(40.0f), (int) CLASSNAMER.drawable.msg_round_cancel_m, 2);
            chat_fileMiniStatesDrawable[0][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(22.0f), CLASSNAMER.drawable.audio_mini_arrow);
            chat_fileMiniStatesDrawable[0][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(22.0f), CLASSNAMER.drawable.audio_mini_arrow);
            chat_fileMiniStatesDrawable[1][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(22.0f), CLASSNAMER.drawable.audio_mini_cancel);
            chat_fileMiniStatesDrawable[1][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(22.0f), CLASSNAMER.drawable.audio_mini_cancel);
            chat_fileMiniStatesDrawable[2][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(22.0f), CLASSNAMER.drawable.audio_mini_arrow);
            chat_fileMiniStatesDrawable[2][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(22.0f), CLASSNAMER.drawable.audio_mini_arrow);
            chat_fileMiniStatesDrawable[3][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(22.0f), CLASSNAMER.drawable.audio_mini_cancel);
            chat_fileMiniStatesDrawable[3][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(22.0f), CLASSNAMER.drawable.audio_mini_cancel);
            chat_fileMiniStatesDrawable[4][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(22.0f), CLASSNAMER.drawable.video_mini_arrow);
            chat_fileMiniStatesDrawable[4][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(22.0f), CLASSNAMER.drawable.video_mini_arrow);
            chat_fileMiniStatesDrawable[5][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(22.0f), CLASSNAMER.drawable.video_mini_cancel);
            chat_fileMiniStatesDrawable[5][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(22.0f), CLASSNAMER.drawable.video_mini_cancel);
            chat_fileStatesDrawable[0][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(44.0f), CLASSNAMER.drawable.msg_round_play_m);
            chat_fileStatesDrawable[0][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(44.0f), CLASSNAMER.drawable.msg_round_play_m);
            chat_fileStatesDrawable[1][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(44.0f), CLASSNAMER.drawable.msg_round_pause_m);
            chat_fileStatesDrawable[1][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(44.0f), CLASSNAMER.drawable.msg_round_pause_m);
            chat_fileStatesDrawable[2][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(44.0f), CLASSNAMER.drawable.msg_round_load_m);
            chat_fileStatesDrawable[2][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(44.0f), CLASSNAMER.drawable.msg_round_load_m);
            chat_fileStatesDrawable[3][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(44.0f), CLASSNAMER.drawable.msg_round_file_s);
            chat_fileStatesDrawable[3][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(44.0f), CLASSNAMER.drawable.msg_round_file_s);
            chat_fileStatesDrawable[4][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(44.0f), CLASSNAMER.drawable.msg_round_cancel_m);
            chat_fileStatesDrawable[4][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(44.0f), CLASSNAMER.drawable.msg_round_cancel_m);
            chat_fileStatesDrawable[5][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(44.0f), CLASSNAMER.drawable.msg_round_play_m);
            chat_fileStatesDrawable[5][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(44.0f), CLASSNAMER.drawable.msg_round_play_m);
            chat_fileStatesDrawable[6][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(44.0f), CLASSNAMER.drawable.msg_round_pause_m);
            chat_fileStatesDrawable[6][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(44.0f), CLASSNAMER.drawable.msg_round_pause_m);
            chat_fileStatesDrawable[7][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(44.0f), CLASSNAMER.drawable.msg_round_load_m);
            chat_fileStatesDrawable[7][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(44.0f), CLASSNAMER.drawable.msg_round_load_m);
            chat_fileStatesDrawable[8][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(44.0f), CLASSNAMER.drawable.msg_round_file_s);
            chat_fileStatesDrawable[8][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(44.0f), CLASSNAMER.drawable.msg_round_file_s);
            chat_fileStatesDrawable[9][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(44.0f), CLASSNAMER.drawable.msg_round_cancel_m);
            chat_fileStatesDrawable[9][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(44.0f), CLASSNAMER.drawable.msg_round_cancel_m);
            chat_photoStatesDrawables[0][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(48.0f), CLASSNAMER.drawable.msg_round_load_m);
            chat_photoStatesDrawables[0][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(48.0f), CLASSNAMER.drawable.msg_round_load_m);
            chat_photoStatesDrawables[1][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(48.0f), CLASSNAMER.drawable.msg_round_cancel_m);
            chat_photoStatesDrawables[1][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(48.0f), CLASSNAMER.drawable.msg_round_cancel_m);
            chat_photoStatesDrawables[2][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(48.0f), CLASSNAMER.drawable.msg_round_gif_m);
            chat_photoStatesDrawables[2][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(48.0f), CLASSNAMER.drawable.msg_round_gif_m);
            chat_photoStatesDrawables[3][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(48.0f), CLASSNAMER.drawable.msg_round_play_m);
            chat_photoStatesDrawables[3][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(48.0f), CLASSNAMER.drawable.msg_round_play_m);
            Drawable[] drawableArr = chat_photoStatesDrawables[4];
            Drawable[] drawableArr2 = chat_photoStatesDrawables[4];
            Drawable drawable = resources.getDrawable(CLASSNAMER.drawable.burn);
            drawableArr2[1] = drawable;
            drawableArr[0] = drawable;
            drawableArr = chat_photoStatesDrawables[5];
            drawableArr2 = chat_photoStatesDrawables[5];
            drawable = resources.getDrawable(CLASSNAMER.drawable.circle);
            drawableArr2[1] = drawable;
            drawableArr[0] = drawable;
            drawableArr = chat_photoStatesDrawables[6];
            drawableArr2 = chat_photoStatesDrawables[6];
            drawable = resources.getDrawable(CLASSNAMER.drawable.photocheck);
            drawableArr2[1] = drawable;
            drawableArr[0] = drawable;
            chat_photoStatesDrawables[7][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(48.0f), CLASSNAMER.drawable.msg_round_load_m);
            chat_photoStatesDrawables[7][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(48.0f), CLASSNAMER.drawable.msg_round_load_m);
            chat_photoStatesDrawables[8][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(48.0f), CLASSNAMER.drawable.msg_round_cancel_m);
            chat_photoStatesDrawables[8][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(48.0f), CLASSNAMER.drawable.msg_round_cancel_m);
            chat_photoStatesDrawables[9][0] = resources.getDrawable(CLASSNAMER.drawable.doc_big).mutate();
            chat_photoStatesDrawables[9][1] = resources.getDrawable(CLASSNAMER.drawable.doc_big).mutate();
            chat_photoStatesDrawables[10][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(48.0f), CLASSNAMER.drawable.msg_round_load_m);
            chat_photoStatesDrawables[10][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(48.0f), CLASSNAMER.drawable.msg_round_load_m);
            chat_photoStatesDrawables[11][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(48.0f), CLASSNAMER.drawable.msg_round_cancel_m);
            chat_photoStatesDrawables[11][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(48.0f), CLASSNAMER.drawable.msg_round_cancel_m);
            chat_photoStatesDrawables[12][0] = resources.getDrawable(CLASSNAMER.drawable.doc_big).mutate();
            chat_photoStatesDrawables[12][1] = resources.getDrawable(CLASSNAMER.drawable.doc_big).mutate();
            chat_contactDrawable[0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(44.0f), CLASSNAMER.drawable.msg_contact);
            chat_contactDrawable[1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.m9dp(44.0f), CLASSNAMER.drawable.msg_contact);
            chat_locationDrawable[0] = Theme.createRoundRectDrawableWithIcon(AndroidUtilities.m9dp(2.0f), CLASSNAMER.drawable.msg_location);
            chat_locationDrawable[1] = Theme.createRoundRectDrawableWithIcon(AndroidUtilities.m9dp(2.0f), CLASSNAMER.drawable.msg_location);
            chat_composeShadowDrawable = context.getResources().getDrawable(CLASSNAMER.drawable.compose_panel_shadow);
            try {
                int bitmapSize = AndroidUtilities.roundMessageSize + AndroidUtilities.m9dp(6.0f);
                Bitmap bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                Paint eraserPaint = new Paint(1);
                eraserPaint.setColor(0);
                eraserPaint.setStyle(Style.FILL);
                eraserPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
                Paint paint = new Paint(1);
                paint.setShadowLayer((float) AndroidUtilities.m9dp(4.0f), 0.0f, 0.0f, NUM);
                for (int a = 0; a < 2; a++) {
                    Paint paint2;
                    float f = (float) (bitmapSize / 2);
                    float f2 = (float) (bitmapSize / 2);
                    float dp = (float) ((AndroidUtilities.roundMessageSize / 2) - AndroidUtilities.m9dp(1.0f));
                    if (a == 0) {
                        paint2 = paint;
                    } else {
                        paint2 = eraserPaint;
                    }
                    canvas.drawCircle(f, f2, dp, paint2);
                }
                try {
                    canvas.setBitmap(null);
                } catch (Exception e) {
                }
                chat_roundVideoShadow = new BitmapDrawable(bitmap);
            } catch (Throwable th) {
            }
            Theme.applyChatTheme(fontsOnly);
        }
        chat_msgTextPaintOneEmoji.setTextSize((float) AndroidUtilities.m9dp(28.0f));
        chat_msgTextPaintTwoEmoji.setTextSize((float) AndroidUtilities.m9dp(24.0f));
        chat_msgTextPaintThreeEmoji.setTextSize((float) AndroidUtilities.m9dp(20.0f));
        chat_msgTextPaint.setTextSize((float) AndroidUtilities.m9dp((float) SharedConfig.fontSize));
        chat_msgGameTextPaint.setTextSize((float) AndroidUtilities.m9dp(14.0f));
        chat_msgBotButtonPaint.setTextSize((float) AndroidUtilities.m9dp(15.0f));
        if (!fontsOnly && chat_botProgressPaint != null) {
            chat_botProgressPaint.setStrokeWidth((float) AndroidUtilities.m9dp(2.0f));
            chat_infoPaint.setTextSize((float) AndroidUtilities.m9dp(12.0f));
            chat_docNamePaint.setTextSize((float) AndroidUtilities.m9dp(15.0f));
            chat_locationTitlePaint.setTextSize((float) AndroidUtilities.m9dp(15.0f));
            chat_locationAddressPaint.setTextSize((float) AndroidUtilities.m9dp(13.0f));
            chat_audioTimePaint.setTextSize((float) AndroidUtilities.m9dp(12.0f));
            chat_livePaint.setTextSize((float) AndroidUtilities.m9dp(12.0f));
            chat_audioTitlePaint.setTextSize((float) AndroidUtilities.m9dp(16.0f));
            chat_audioPerformerPaint.setTextSize((float) AndroidUtilities.m9dp(15.0f));
            chat_botButtonPaint.setTextSize((float) AndroidUtilities.m9dp(15.0f));
            chat_contactNamePaint.setTextSize((float) AndroidUtilities.m9dp(15.0f));
            chat_contactPhonePaint.setTextSize((float) AndroidUtilities.m9dp(13.0f));
            chat_durationPaint.setTextSize((float) AndroidUtilities.m9dp(12.0f));
            chat_timePaint.setTextSize((float) AndroidUtilities.m9dp(12.0f));
            chat_adminPaint.setTextSize((float) AndroidUtilities.m9dp(13.0f));
            chat_namePaint.setTextSize((float) AndroidUtilities.m9dp(14.0f));
            chat_forwardNamePaint.setTextSize((float) AndroidUtilities.m9dp(14.0f));
            chat_replyNamePaint.setTextSize((float) AndroidUtilities.m9dp(14.0f));
            chat_replyTextPaint.setTextSize((float) AndroidUtilities.m9dp(14.0f));
            chat_gamePaint.setTextSize((float) AndroidUtilities.m9dp(13.0f));
            chat_shipmentPaint.setTextSize((float) AndroidUtilities.m9dp(13.0f));
            chat_instantViewPaint.setTextSize((float) AndroidUtilities.m9dp(13.0f));
            chat_instantViewRectPaint.setStrokeWidth((float) AndroidUtilities.m9dp(1.0f));
            chat_statusRecordPaint.setStrokeWidth((float) AndroidUtilities.m9dp(2.0f));
            chat_actionTextPaint.setTextSize((float) AndroidUtilities.m9dp((float) (Math.max(16, SharedConfig.fontSize) - 2)));
            chat_contextResult_titleTextPaint.setTextSize((float) AndroidUtilities.m9dp(15.0f));
            chat_contextResult_descriptionTextPaint.setTextSize((float) AndroidUtilities.m9dp(13.0f));
            chat_radialProgressPaint.setStrokeWidth((float) AndroidUtilities.m9dp(3.0f));
            chat_radialProgress2Paint.setStrokeWidth((float) AndroidUtilities.m9dp(2.0f));
        }
    }

    public static void applyChatTheme(boolean fontsOnly) {
        if (chat_msgTextPaint != null && chat_msgInDrawable != null && !fontsOnly) {
            int a;
            chat_gamePaint.setColor(Theme.getColor(key_chat_previewGameText));
            chat_durationPaint.setColor(Theme.getColor(key_chat_previewDurationText));
            chat_botButtonPaint.setColor(Theme.getColor(key_chat_botButtonText));
            chat_urlPaint.setColor(Theme.getColor(key_chat_linkSelectBackground));
            chat_botProgressPaint.setColor(Theme.getColor(key_chat_botProgress));
            chat_deleteProgressPaint.setColor(Theme.getColor(key_chat_secretTimeText));
            chat_textSearchSelectionPaint.setColor(Theme.getColor(key_chat_textSelectBackground));
            chat_msgErrorPaint.setColor(Theme.getColor(key_chat_sentError));
            chat_statusPaint.setColor(Theme.getColor(key_actionBarDefaultSubtitle));
            chat_statusRecordPaint.setColor(Theme.getColor(key_actionBarDefaultSubtitle));
            chat_actionTextPaint.setColor(Theme.getColor(key_chat_serviceText));
            chat_actionTextPaint.linkColor = Theme.getColor(key_chat_serviceLink);
            chat_contextResult_titleTextPaint.setColor(Theme.getColor(key_windowBackgroundWhiteBlackText));
            chat_composeBackgroundPaint.setColor(Theme.getColor(key_chat_messagePanelBackground));
            chat_timeBackgroundPaint.setColor(Theme.getColor(key_chat_mediaTimeBackground));
            Theme.setDrawableColorByKey(chat_msgInDrawable, key_chat_inBubble);
            Theme.setDrawableColorByKey(chat_msgInSelectedDrawable, key_chat_inBubbleSelected);
            Theme.setDrawableColorByKey(chat_msgInShadowDrawable, key_chat_inBubbleShadow);
            Theme.setDrawableColorByKey(chat_msgOutDrawable, key_chat_outBubble);
            Theme.setDrawableColorByKey(chat_msgOutSelectedDrawable, key_chat_outBubbleSelected);
            Theme.setDrawableColorByKey(chat_msgOutShadowDrawable, key_chat_outBubbleShadow);
            Theme.setDrawableColorByKey(chat_msgInMediaDrawable, key_chat_inBubble);
            Theme.setDrawableColorByKey(chat_msgInMediaSelectedDrawable, key_chat_inBubbleSelected);
            Theme.setDrawableColorByKey(chat_msgInMediaShadowDrawable, key_chat_inBubbleShadow);
            Theme.setDrawableColorByKey(chat_msgOutMediaDrawable, key_chat_outBubble);
            Theme.setDrawableColorByKey(chat_msgOutMediaSelectedDrawable, key_chat_outBubbleSelected);
            Theme.setDrawableColorByKey(chat_msgOutMediaShadowDrawable, key_chat_outBubbleShadow);
            Theme.setDrawableColorByKey(chat_msgOutCheckDrawable, key_chat_outSentCheck);
            Theme.setDrawableColorByKey(chat_msgOutCheckSelectedDrawable, key_chat_outSentCheckSelected);
            Theme.setDrawableColorByKey(chat_msgOutHalfCheckDrawable, key_chat_outSentCheck);
            Theme.setDrawableColorByKey(chat_msgOutHalfCheckSelectedDrawable, key_chat_outSentCheckSelected);
            Theme.setDrawableColorByKey(chat_msgOutClockDrawable, key_chat_outSentClock);
            Theme.setDrawableColorByKey(chat_msgOutSelectedClockDrawable, key_chat_outSentClockSelected);
            Theme.setDrawableColorByKey(chat_msgInClockDrawable, key_chat_inSentClock);
            Theme.setDrawableColorByKey(chat_msgInSelectedClockDrawable, key_chat_inSentClockSelected);
            Theme.setDrawableColorByKey(chat_msgMediaCheckDrawable, key_chat_mediaSentCheck);
            Theme.setDrawableColorByKey(chat_msgMediaHalfCheckDrawable, key_chat_mediaSentCheck);
            Theme.setDrawableColorByKey(chat_msgMediaClockDrawable, key_chat_mediaSentClock);
            Theme.setDrawableColorByKey(chat_msgStickerCheckDrawable, key_chat_serviceText);
            Theme.setDrawableColorByKey(chat_msgStickerHalfCheckDrawable, key_chat_serviceText);
            Theme.setDrawableColorByKey(chat_msgStickerClockDrawable, key_chat_serviceText);
            Theme.setDrawableColorByKey(chat_msgStickerViewsDrawable, key_chat_serviceText);
            Theme.setDrawableColorByKey(chat_shareIconDrawable, key_chat_serviceIcon);
            Theme.setDrawableColorByKey(chat_replyIconDrawable, key_chat_serviceIcon);
            Theme.setDrawableColorByKey(chat_goIconDrawable, key_chat_serviceIcon);
            Theme.setDrawableColorByKey(chat_botInlineDrawable, key_chat_serviceIcon);
            Theme.setDrawableColorByKey(chat_botLinkDrawalbe, key_chat_serviceIcon);
            Theme.setDrawableColorByKey(chat_msgInViewsDrawable, key_chat_inViews);
            Theme.setDrawableColorByKey(chat_msgInViewsSelectedDrawable, key_chat_inViewsSelected);
            Theme.setDrawableColorByKey(chat_msgOutViewsDrawable, key_chat_outViews);
            Theme.setDrawableColorByKey(chat_msgOutViewsSelectedDrawable, key_chat_outViewsSelected);
            Theme.setDrawableColorByKey(chat_msgMediaViewsDrawable, key_chat_mediaViews);
            Theme.setDrawableColorByKey(chat_msgInMenuDrawable, key_chat_inMenu);
            Theme.setDrawableColorByKey(chat_msgInMenuSelectedDrawable, key_chat_inMenuSelected);
            Theme.setDrawableColorByKey(chat_msgOutMenuDrawable, key_chat_outMenu);
            Theme.setDrawableColorByKey(chat_msgOutMenuSelectedDrawable, key_chat_outMenuSelected);
            Theme.setDrawableColorByKey(chat_msgMediaMenuDrawable, key_chat_mediaMenu);
            Theme.setDrawableColorByKey(chat_msgOutInstantDrawable, key_chat_outInstant);
            Theme.setDrawableColorByKey(chat_msgInInstantDrawable, key_chat_inInstant);
            Theme.setDrawableColorByKey(chat_msgErrorDrawable, key_chat_sentErrorIcon);
            Theme.setDrawableColorByKey(chat_muteIconDrawable, key_chat_muteIcon);
            Theme.setDrawableColorByKey(chat_lockIconDrawable, key_chat_lockIcon);
            Theme.setDrawableColorByKey(chat_msgBroadcastDrawable, key_chat_outBroadcast);
            Theme.setDrawableColorByKey(chat_msgBroadcastMediaDrawable, key_chat_mediaBroadcast);
            Theme.setDrawableColorByKey(chat_inlineResultFile, key_chat_inlineResultIcon);
            Theme.setDrawableColorByKey(chat_inlineResultAudio, key_chat_inlineResultIcon);
            Theme.setDrawableColorByKey(chat_inlineResultLocation, key_chat_inlineResultIcon);
            Theme.setDrawableColorByKey(chat_msgInCallDrawable, key_chat_inInstant);
            Theme.setDrawableColorByKey(chat_msgInCallSelectedDrawable, key_chat_inInstantSelected);
            Theme.setDrawableColorByKey(chat_msgOutCallDrawable, key_chat_outInstant);
            Theme.setDrawableColorByKey(chat_msgOutCallSelectedDrawable, key_chat_outInstantSelected);
            Theme.setDrawableColorByKey(chat_msgCallUpRedDrawable, key_calls_callReceivedRedIcon);
            Theme.setDrawableColorByKey(chat_msgCallUpGreenDrawable, key_calls_callReceivedGreenIcon);
            Theme.setDrawableColorByKey(chat_msgCallDownRedDrawable, key_calls_callReceivedRedIcon);
            Theme.setDrawableColorByKey(chat_msgCallDownGreenDrawable, key_calls_callReceivedGreenIcon);
            for (a = 0; a < 2; a++) {
                Theme.setCombinedDrawableColor(chat_fileMiniStatesDrawable[a][0], Theme.getColor(key_chat_outLoader), false);
                Theme.setCombinedDrawableColor(chat_fileMiniStatesDrawable[a][0], Theme.getColor(key_chat_outMediaIcon), true);
                Theme.setCombinedDrawableColor(chat_fileMiniStatesDrawable[a][1], Theme.getColor(key_chat_outLoaderSelected), false);
                Theme.setCombinedDrawableColor(chat_fileMiniStatesDrawable[a][1], Theme.getColor(key_chat_outMediaIconSelected), true);
                Theme.setCombinedDrawableColor(chat_fileMiniStatesDrawable[a + 2][0], Theme.getColor(key_chat_inLoader), false);
                Theme.setCombinedDrawableColor(chat_fileMiniStatesDrawable[a + 2][0], Theme.getColor(key_chat_inMediaIcon), true);
                Theme.setCombinedDrawableColor(chat_fileMiniStatesDrawable[a + 2][1], Theme.getColor(key_chat_inLoaderSelected), false);
                Theme.setCombinedDrawableColor(chat_fileMiniStatesDrawable[a + 2][1], Theme.getColor(key_chat_inMediaIconSelected), true);
                Theme.setCombinedDrawableColor(chat_fileMiniStatesDrawable[a + 4][0], Theme.getColor(key_chat_mediaLoaderPhoto), false);
                Theme.setCombinedDrawableColor(chat_fileMiniStatesDrawable[a + 4][0], Theme.getColor(key_chat_mediaLoaderPhotoIcon), true);
                Theme.setCombinedDrawableColor(chat_fileMiniStatesDrawable[a + 4][1], Theme.getColor(key_chat_mediaLoaderPhotoSelected), false);
                Theme.setCombinedDrawableColor(chat_fileMiniStatesDrawable[a + 4][1], Theme.getColor(key_chat_mediaLoaderPhotoIconSelected), true);
            }
            for (a = 0; a < 5; a++) {
                Theme.setCombinedDrawableColor(chat_fileStatesDrawable[a][0], Theme.getColor(key_chat_outLoader), false);
                Theme.setCombinedDrawableColor(chat_fileStatesDrawable[a][0], Theme.getColor(key_chat_outMediaIcon), true);
                Theme.setCombinedDrawableColor(chat_fileStatesDrawable[a][1], Theme.getColor(key_chat_outLoaderSelected), false);
                Theme.setCombinedDrawableColor(chat_fileStatesDrawable[a][1], Theme.getColor(key_chat_outMediaIconSelected), true);
                Theme.setCombinedDrawableColor(chat_fileStatesDrawable[a + 5][0], Theme.getColor(key_chat_inLoader), false);
                Theme.setCombinedDrawableColor(chat_fileStatesDrawable[a + 5][0], Theme.getColor(key_chat_inMediaIcon), true);
                Theme.setCombinedDrawableColor(chat_fileStatesDrawable[a + 5][1], Theme.getColor(key_chat_inLoaderSelected), false);
                Theme.setCombinedDrawableColor(chat_fileStatesDrawable[a + 5][1], Theme.getColor(key_chat_inMediaIconSelected), true);
            }
            for (a = 0; a < 4; a++) {
                Theme.setCombinedDrawableColor(chat_photoStatesDrawables[a][0], Theme.getColor(key_chat_mediaLoaderPhoto), false);
                Theme.setCombinedDrawableColor(chat_photoStatesDrawables[a][0], Theme.getColor(key_chat_mediaLoaderPhotoIcon), true);
                Theme.setCombinedDrawableColor(chat_photoStatesDrawables[a][1], Theme.getColor(key_chat_mediaLoaderPhotoSelected), false);
                Theme.setCombinedDrawableColor(chat_photoStatesDrawables[a][1], Theme.getColor(key_chat_mediaLoaderPhotoIconSelected), true);
            }
            for (a = 0; a < 2; a++) {
                Theme.setCombinedDrawableColor(chat_photoStatesDrawables[a + 7][0], Theme.getColor(key_chat_outLoaderPhoto), false);
                Theme.setCombinedDrawableColor(chat_photoStatesDrawables[a + 7][0], Theme.getColor(key_chat_outLoaderPhotoIcon), true);
                Theme.setCombinedDrawableColor(chat_photoStatesDrawables[a + 7][1], Theme.getColor(key_chat_outLoaderPhotoSelected), false);
                Theme.setCombinedDrawableColor(chat_photoStatesDrawables[a + 7][1], Theme.getColor(key_chat_outLoaderPhotoIconSelected), true);
                Theme.setCombinedDrawableColor(chat_photoStatesDrawables[a + 10][0], Theme.getColor(key_chat_inLoaderPhoto), false);
                Theme.setCombinedDrawableColor(chat_photoStatesDrawables[a + 10][0], Theme.getColor(key_chat_inLoaderPhotoIcon), true);
                Theme.setCombinedDrawableColor(chat_photoStatesDrawables[a + 10][1], Theme.getColor(key_chat_inLoaderPhotoSelected), false);
                Theme.setCombinedDrawableColor(chat_photoStatesDrawables[a + 10][1], Theme.getColor(key_chat_inLoaderPhotoIconSelected), true);
            }
            Theme.setDrawableColorByKey(chat_photoStatesDrawables[9][0], key_chat_outFileIcon);
            Theme.setDrawableColorByKey(chat_photoStatesDrawables[9][1], key_chat_outFileSelectedIcon);
            Theme.setDrawableColorByKey(chat_photoStatesDrawables[12][0], key_chat_inFileIcon);
            Theme.setDrawableColorByKey(chat_photoStatesDrawables[12][1], key_chat_inFileSelectedIcon);
            Theme.setCombinedDrawableColor(chat_contactDrawable[0], Theme.getColor(key_chat_inContactBackground), false);
            Theme.setCombinedDrawableColor(chat_contactDrawable[0], Theme.getColor(key_chat_inContactIcon), true);
            Theme.setCombinedDrawableColor(chat_contactDrawable[1], Theme.getColor(key_chat_outContactBackground), false);
            Theme.setCombinedDrawableColor(chat_contactDrawable[1], Theme.getColor(key_chat_outContactIcon), true);
            Theme.setCombinedDrawableColor(chat_locationDrawable[0], Theme.getColor(key_chat_inLocationBackground), false);
            Theme.setCombinedDrawableColor(chat_locationDrawable[0], Theme.getColor(key_chat_inLocationIcon), true);
            Theme.setCombinedDrawableColor(chat_locationDrawable[1], Theme.getColor(key_chat_outLocationBackground), false);
            Theme.setCombinedDrawableColor(chat_locationDrawable[1], Theme.getColor(key_chat_outLocationIcon), true);
            Theme.setDrawableColorByKey(chat_composeShadowDrawable, key_chat_messagePanelShadow);
            Theme.setCombinedDrawableColor(chat_attachButtonDrawables[1], Theme.getColor(key_chat_attachGalleryBackground), false);
            Theme.setCombinedDrawableColor(chat_attachButtonDrawables[1], Theme.getColor(key_chat_attachGalleryIcon), true);
            Theme.setCombinedDrawableColor(chat_attachButtonDrawables[2], Theme.getColor(key_chat_attachVideoBackground), false);
            Theme.setCombinedDrawableColor(chat_attachButtonDrawables[2], Theme.getColor(key_chat_attachVideoIcon), true);
            Theme.setCombinedDrawableColor(chat_attachButtonDrawables[3], Theme.getColor(key_chat_attachAudioBackground), false);
            Theme.setCombinedDrawableColor(chat_attachButtonDrawables[3], Theme.getColor(key_chat_attachAudioIcon), true);
            Theme.setCombinedDrawableColor(chat_attachButtonDrawables[4], Theme.getColor(key_chat_attachFileBackground), false);
            Theme.setCombinedDrawableColor(chat_attachButtonDrawables[4], Theme.getColor(key_chat_attachFileIcon), true);
            Theme.setCombinedDrawableColor(chat_attachButtonDrawables[5], Theme.getColor(key_chat_attachContactBackground), false);
            Theme.setCombinedDrawableColor(chat_attachButtonDrawables[5], Theme.getColor(key_chat_attachContactIcon), true);
            Theme.setCombinedDrawableColor(chat_attachButtonDrawables[6], Theme.getColor(key_chat_attachLocationBackground), false);
            Theme.setCombinedDrawableColor(chat_attachButtonDrawables[6], Theme.getColor(key_chat_attachLocationIcon), true);
            Theme.setCombinedDrawableColor(chat_attachButtonDrawables[7], Theme.getColor(key_chat_attachHideBackground), false);
            Theme.setCombinedDrawableColor(chat_attachButtonDrawables[7], Theme.getColor(key_chat_attachHideIcon), true);
            Theme.setCombinedDrawableColor(chat_attachButtonDrawables[8], Theme.getColor(key_chat_attachSendBackground), false);
            Theme.setCombinedDrawableColor(chat_attachButtonDrawables[8], Theme.getColor(key_chat_attachSendIcon), true);
            Theme.setCombinedDrawableColor(chat_attachButtonDrawables[9], Theme.getColor(key_chat_attachPollBackground), false);
            Theme.setCombinedDrawableColor(chat_attachButtonDrawables[9], Theme.getColor(key_chat_attachPollIcon), true);
            Theme.applyChatServiceMessageColor();
        }
    }

    public static void applyChatServiceMessageColor() {
        if (chat_actionBackgroundPaint != null) {
            Integer serviceColor = (Integer) currentColors.get(key_chat_serviceBackground);
            Integer servicePressedColor = (Integer) currentColors.get(key_chat_serviceBackgroundSelected);
            Integer serviceColor2 = serviceColor;
            Integer servicePressedColor2 = servicePressedColor;
            if (serviceColor == null) {
                serviceColor = Integer.valueOf(serviceMessageColor);
                serviceColor2 = Integer.valueOf(serviceMessage2Color);
            }
            if (servicePressedColor == null) {
                servicePressedColor = Integer.valueOf(serviceSelectedMessageColor);
                servicePressedColor2 = Integer.valueOf(serviceSelectedMessage2Color);
            }
            if (currentColor != serviceColor.intValue()) {
                chat_actionBackgroundPaint.setColor(serviceColor.intValue());
                colorFilter = new PorterDuffColorFilter(serviceColor.intValue(), Mode.MULTIPLY);
                colorFilter2 = new PorterDuffColorFilter(serviceColor2.intValue(), Mode.MULTIPLY);
                currentColor = serviceColor.intValue();
                if (chat_cornerOuter[0] != null) {
                    for (int a = 0; a < 4; a++) {
                        chat_cornerOuter[a].setColorFilter(colorFilter);
                        chat_cornerInner[a].setColorFilter(colorFilter);
                    }
                }
            }
            if (currentSelectedColor != servicePressedColor.intValue()) {
                currentSelectedColor = servicePressedColor.intValue();
                colorPressedFilter = new PorterDuffColorFilter(servicePressedColor.intValue(), Mode.MULTIPLY);
                colorPressedFilter2 = new PorterDuffColorFilter(servicePressedColor2.intValue(), Mode.MULTIPLY);
            }
        }
    }

    public static void createProfileResources(Context context) {
        if (profile_verifiedDrawable == null) {
            profile_aboutTextPaint = new TextPaint(1);
            Resources resources = context.getResources();
            profile_verifiedDrawable = resources.getDrawable(CLASSNAMER.drawable.verified_area).mutate();
            profile_verifiedCheckDrawable = resources.getDrawable(CLASSNAMER.drawable.verified_check).mutate();
            Theme.applyProfileTheme();
        }
        profile_aboutTextPaint.setTextSize((float) AndroidUtilities.m9dp(16.0f));
    }

    public static ColorFilter getShareColorFilter(int color, boolean selected) {
        if (selected) {
            if (currentShareSelectedColorFilter == null || currentShareSelectedColorFilterColor != color) {
                currentShareSelectedColorFilterColor = color;
                currentShareSelectedColorFilter = new PorterDuffColorFilter(color, Mode.MULTIPLY);
            }
            return currentShareSelectedColorFilter;
        }
        if (currentShareColorFilter == null || currentShareColorFilterColor != color) {
            currentShareColorFilterColor = color;
            currentShareColorFilter = new PorterDuffColorFilter(color, Mode.MULTIPLY);
        }
        return currentShareColorFilter;
    }

    public static void applyProfileTheme() {
        if (profile_verifiedDrawable != null) {
            profile_aboutTextPaint.setColor(Theme.getColor(key_windowBackgroundWhiteBlackText));
            profile_aboutTextPaint.linkColor = Theme.getColor(key_windowBackgroundWhiteLinkText);
            Theme.setDrawableColorByKey(profile_verifiedDrawable, key_profile_verifiedBackground);
            Theme.setDrawableColorByKey(profile_verifiedCheckDrawable, key_profile_verifiedCheck);
        }
    }

    public static Drawable getThemedDrawable(Context context, int resId, String key) {
        return Theme.getThemedDrawable(context, resId, Theme.getColor(key));
    }

    public static Drawable getThemedDrawable(Context context, int resId, int color) {
        if (context == null) {
            return null;
        }
        Drawable drawable = context.getResources().getDrawable(resId).mutate();
        drawable.setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
        return drawable;
    }

    public static int getDefaultColor(String key) {
        Integer value = (Integer) defaultColors.get(key);
        if (value != null) {
            return value.intValue();
        }
        if (key.equals(key_chats_menuTopShadow)) {
            return 0;
        }
        return -65536;
    }

    public static boolean hasThemeKey(String key) {
        return currentColors.containsKey(key);
    }

    public static Integer getColorOrNull(String key) {
        Integer color = (Integer) currentColors.get(key);
        if (color != null) {
            return color;
        }
        if (((String) fallbackKeys.get(key)) != null) {
            color = (Integer) currentColors.get(key);
        }
        if (color == null) {
            return (Integer) defaultColors.get(key);
        }
        return color;
    }

    public static int getColor(String key) {
        return Theme.getColor(key, null);
    }

    public static int getColor(String key, boolean[] isDefault) {
        if (!Theme.isCurrentThemeDefault()) {
            Integer color = (Integer) currentColors.get(key);
            if (color == null) {
                String fallbackKey = (String) fallbackKeys.get(key);
                if (fallbackKey != null) {
                    color = (Integer) currentColors.get(fallbackKey);
                }
                if (color == null) {
                    if (isDefault != null) {
                        isDefault[0] = true;
                    }
                    if (key.equals(key_chat_serviceBackground)) {
                        return serviceMessageColor;
                    }
                    if (key.equals(key_chat_serviceBackgroundSelected)) {
                        return serviceSelectedMessageColor;
                    }
                    return Theme.getDefaultColor(key);
                }
            }
            return color.intValue();
        } else if (key.equals(key_chat_serviceBackground)) {
            return serviceMessageColor;
        } else {
            if (key.equals(key_chat_serviceBackgroundSelected)) {
                return serviceSelectedMessageColor;
            }
            return Theme.getDefaultColor(key);
        }
    }

    public static void setColor(String key, int color, boolean useDefault) {
        if (key.equals(key_chat_wallpaper)) {
            color |= ACTION_BAR_VIDEO_EDIT_COLOR;
        }
        if (useDefault) {
            currentColors.remove(key);
        } else {
            currentColors.put(key, Integer.valueOf(color));
        }
        if (key.equals(key_chat_serviceBackground) || key.equals(key_chat_serviceBackgroundSelected)) {
            Theme.applyChatServiceMessageColor();
        } else if (key.equals(key_chat_wallpaper)) {
            Theme.reloadWallpaper();
        }
    }

    public static void setThemeWallpaper(String themeName, Bitmap bitmap, File path) {
        currentColors.remove(key_chat_wallpaper);
        MessagesController.getGlobalMainSettings().edit().remove("overrideThemeWallpaper").commit();
        if (bitmap != null) {
            themedWallpaper = new BitmapDrawable(bitmap);
            Theme.saveCurrentTheme(themeName, false);
            Theme.calcBackgroundColor(themedWallpaper, 0);
            Theme.applyChatServiceMessageColor();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewWallpapper, new Object[0]);
            return;
        }
        themedWallpaper = null;
        wallpaper = null;
        Theme.saveCurrentTheme(themeName, false);
        Theme.reloadWallpaper();
    }

    public static void setDrawableColor(Drawable drawable, int color) {
        if (drawable != null) {
            if (drawable instanceof ShapeDrawable) {
                ((ShapeDrawable) drawable).getPaint().setColor(color);
            } else {
                drawable.setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
            }
        }
    }

    public static void setDrawableColorByKey(Drawable drawable, String key) {
        if (key != null) {
            Theme.setDrawableColor(drawable, Theme.getColor(key));
        }
    }

    public static void setEmojiDrawableColor(Drawable drawable, int color, boolean selected) {
        if (!(drawable instanceof StateListDrawable)) {
            return;
        }
        Drawable state;
        if (selected) {
            try {
                state = Theme.getStateDrawable(drawable, 0);
                if (state instanceof ShapeDrawable) {
                    ((ShapeDrawable) state).getPaint().setColor(color);
                    return;
                } else {
                    state.setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                    return;
                }
            } catch (Throwable th) {
                return;
            }
        }
        state = Theme.getStateDrawable(drawable, 1);
        if (state instanceof ShapeDrawable) {
            ((ShapeDrawable) state).getPaint().setColor(color);
        } else {
            state.setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
        }
    }

    public static void setSelectorDrawableColor(Drawable drawable, int color, boolean selected) {
        if (drawable instanceof StateListDrawable) {
            Drawable state;
            if (selected) {
                try {
                    state = Theme.getStateDrawable(drawable, 0);
                    if (state instanceof ShapeDrawable) {
                        ((ShapeDrawable) state).getPaint().setColor(color);
                    } else {
                        state.setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                    }
                    state = Theme.getStateDrawable(drawable, 1);
                    if (state instanceof ShapeDrawable) {
                        ((ShapeDrawable) state).getPaint().setColor(color);
                        return;
                    } else {
                        state.setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                        return;
                    }
                } catch (Throwable th) {
                    return;
                }
            }
            state = Theme.getStateDrawable(drawable, 2);
            if (state instanceof ShapeDrawable) {
                ((ShapeDrawable) state).getPaint().setColor(color);
            } else {
                state.setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
            }
        } else if (VERSION.SDK_INT >= 21 && (drawable instanceof RippleDrawable)) {
            RippleDrawable rippleDrawable = (RippleDrawable) drawable;
            if (selected) {
                rippleDrawable.setColor(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{color}));
            } else if (rippleDrawable.getNumberOfLayers() > 0) {
                Drawable drawable1 = rippleDrawable.getDrawable(0);
                if (drawable1 instanceof ShapeDrawable) {
                    ((ShapeDrawable) drawable1).getPaint().setColor(color);
                } else {
                    drawable1.setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
                }
            }
        }
    }

    public static boolean hasWallpaperFromTheme() {
        return currentColors.containsKey(key_chat_wallpaper) || themedWallpaperFileOffset > 0;
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
        Theme.loadWallpaper();
    }

    private static void calcBackgroundColor(Drawable drawable, int save) {
        if (save != 2) {
            int[] result = AndroidUtilities.calcDrawableColor(drawable);
            serviceMessageColor = result[0];
            serviceSelectedMessageColor = result[1];
            serviceMessage2Color = result[2];
            serviceSelectedMessage2Color = result[3];
        }
    }

    public static int getServiceMessageColor() {
        Integer serviceColor = (Integer) currentColors.get(key_chat_serviceBackground);
        return serviceColor == null ? serviceMessageColor : serviceColor.intValue();
    }

    public static void loadWallpaper() {
        if (wallpaper == null) {
            Utilities.searchQueue.postRunnable(Theme$$Lambda$2.$instance);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0030  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x00ec A:{SYNTHETIC, Splitter: B:64:0x00ec} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static final /* synthetic */ void lambda$loadWallpaper$3$Theme() {
        Throwable e;
        Throwable th;
        synchronized (wallpaperSync) {
            if (!MessagesController.getGlobalMainSettings().getBoolean("overrideThemeWallpaper", false)) {
                Integer backgroundColor = (Integer) currentColors.get(key_chat_wallpaper);
                if (backgroundColor != null) {
                    wallpaper = new ColorDrawable(backgroundColor.intValue());
                    isCustomTheme = true;
                } else if (themedWallpaperFileOffset > 0 && !(currentTheme.pathToFile == null && currentTheme.assetName == null)) {
                    FileInputStream stream = null;
                    try {
                        File file;
                        if (currentTheme.assetName != null) {
                            file = Theme.getAssetFile(currentTheme.assetName);
                        } else {
                            file = new File(currentTheme.pathToFile);
                        }
                        FileInputStream stream2 = new FileInputStream(file);
                        try {
                            stream2.getChannel().position((long) themedWallpaperFileOffset);
                            Bitmap bitmap = BitmapFactory.decodeStream(stream2);
                            if (bitmap != null) {
                                Drawable bitmapDrawable = new BitmapDrawable(bitmap);
                                wallpaper = bitmapDrawable;
                                themedWallpaper = bitmapDrawable;
                                isCustomTheme = true;
                            }
                            if (stream2 != null) {
                                try {
                                    stream2.close();
                                } catch (Throwable e2) {
                                    FileLog.m13e(e2);
                                }
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            stream = stream2;
                            if (stream != null) {
                                try {
                                    stream.close();
                                } catch (Throwable e22) {
                                    FileLog.m13e(e22);
                                }
                            }
                            throw th;
                        }
                    } catch (Throwable th3) {
                        e22 = th3;
                    }
                }
            }
            if (wallpaper == null) {
                int selectedColor = 0;
                try {
                    SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                    int selectedBackground = preferences.getInt("selectedBackground", 1000001);
                    selectedColor = preferences.getInt("selectedColor", 0);
                    if (selectedColor == 0) {
                        if (selectedBackground == 1000001) {
                            wallpaper = ApplicationLoader.applicationContext.getResources().getDrawable(CLASSNAMER.drawable.background_hd);
                            isCustomTheme = false;
                        } else {
                            File toFile = new File(ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg");
                            if (toFile.exists()) {
                                wallpaper = Drawable.createFromPath(toFile.getAbsolutePath());
                                isCustomTheme = true;
                            } else {
                                wallpaper = ApplicationLoader.applicationContext.getResources().getDrawable(CLASSNAMER.drawable.background_hd);
                                isCustomTheme = false;
                            }
                        }
                    }
                } catch (Throwable th4) {
                }
                if (wallpaper == null) {
                    if (selectedColor == 0) {
                        selectedColor = -2693905;
                    }
                    wallpaper = new ColorDrawable(selectedColor);
                }
            }
            Theme.calcBackgroundColor(wallpaper, 1);
            AndroidUtilities.runOnUIThread(Theme$$Lambda$3.$instance);
        }
        return;
    }

    static final /* synthetic */ void lambda$null$2$Theme() {
        Theme.applyChatServiceMessageColor();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewWallpapper, new Object[0]);
    }

    /* JADX WARNING: Removed duplicated region for block: B:52:0x00b5 A:{SYNTHETIC, Splitter: B:52:0x00b5} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static Drawable getThemedWallpaper(boolean thumb) {
        Throwable e;
        Throwable th;
        Integer backgroundColor = (Integer) currentColors.get(key_chat_wallpaper);
        if (backgroundColor != null) {
            return new ColorDrawable(backgroundColor.intValue());
        }
        if (themedWallpaperFileOffset > 0 && !(currentTheme.pathToFile == null && currentTheme.assetName == null)) {
            FileInputStream stream = null;
            try {
                File file;
                if (currentTheme.assetName != null) {
                    file = Theme.getAssetFile(currentTheme.assetName);
                } else {
                    file = new File(currentTheme.pathToFile);
                }
                FileInputStream stream2 = new FileInputStream(file);
                try {
                    stream2.getChannel().position((long) themedWallpaperFileOffset);
                    Options opts = new Options();
                    int scaleFactor = 1;
                    if (thumb) {
                        opts.inJustDecodeBounds = true;
                        float photoW = (float) opts.outWidth;
                        float photoH = (float) opts.outHeight;
                        int maxWidth = AndroidUtilities.m9dp(100.0f);
                        while (true) {
                            if (photoW <= ((float) maxWidth) && photoH <= ((float) maxWidth)) {
                                break;
                            }
                            scaleFactor *= 2;
                            photoW /= 2.0f;
                            photoH /= 2.0f;
                        }
                    }
                    opts.inJustDecodeBounds = false;
                    opts.inSampleSize = scaleFactor;
                    Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, opts);
                    if (bitmap != null) {
                        Drawable bitmapDrawable = new BitmapDrawable(bitmap);
                        if (stream2 == null) {
                            return bitmapDrawable;
                        }
                        try {
                            stream2.close();
                            return bitmapDrawable;
                        } catch (Throwable e2) {
                            FileLog.m13e(e2);
                            return bitmapDrawable;
                        }
                    } else if (stream2 != null) {
                        try {
                            stream2.close();
                        } catch (Throwable e22) {
                            FileLog.m13e(e22);
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    stream = stream2;
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Throwable e222) {
                            FileLog.m13e(e222);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                e222 = th3;
            }
        }
        return null;
    }

    public static Drawable getCachedWallpaper() {
        Drawable drawable;
        synchronized (wallpaperSync) {
            if (themedWallpaper != null) {
                drawable = themedWallpaper;
            } else {
                drawable = wallpaper;
            }
        }
        return drawable;
    }
}
