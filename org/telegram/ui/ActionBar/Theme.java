package org.telegram.ui.ActionBar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.time.SunDate;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.ThemeEditorView;

public class Theme
{
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
  private static Field BitmapDrawable_mColorFilter;
  private static final int LIGHT_SENSOR_THEME_SWITCH_DELAY = 1800;
  private static final int LIGHT_SENSOR_THEME_SWITCH_NEAR_DELAY = 12000;
  private static final int LIGHT_SENSOR_THEME_SWITCH_NEAR_THRESHOLD = 12000;
  private static final float MAXIMUM_LUX_BREAKPOINT = 500.0F;
  private static Method StateListDrawable_getStateDrawableMethod;
  private static SensorEventListener ambientSensorListener;
  public static float autoNightBrighnessThreshold = 0.0F;
  public static String autoNightCityName;
  public static int autoNightDayEndTime = 0;
  public static int autoNightDayStartTime = 0;
  public static int autoNightLastSunCheckDay = 0;
  public static double autoNightLocationLatitude = 0.0D;
  public static double autoNightLocationLongitude = 0.0D;
  public static boolean autoNightScheduleByLocation = false;
  public static int autoNightSunriseTime = 0;
  public static int autoNightSunsetTime = 0;
  public static Paint avatar_backgroundPaint;
  public static Drawable avatar_broadcastDrawable;
  public static Drawable avatar_photoDrawable;
  public static Drawable avatar_savedDrawable;
  private static boolean canStartHolidayAnimation = false;
  public static Paint chat_actionBackgroundPaint;
  public static TextPaint chat_actionTextPaint;
  public static TextPaint chat_adminPaint;
  public static Drawable[] chat_attachButtonDrawables;
  public static TextPaint chat_audioPerformerPaint;
  public static TextPaint chat_audioTimePaint;
  public static TextPaint chat_audioTitlePaint;
  public static TextPaint chat_botButtonPaint;
  public static Drawable chat_botInlineDrawable;
  public static Drawable chat_botLinkDrawalbe;
  public static Paint chat_botProgressPaint;
  public static Paint chat_composeBackgroundPaint;
  public static Drawable chat_composeShadowDrawable;
  public static Drawable[] chat_contactDrawable;
  public static TextPaint chat_contactNamePaint;
  public static TextPaint chat_contactPhonePaint;
  public static TextPaint chat_contextResult_descriptionTextPaint;
  public static Drawable chat_contextResult_shadowUnderSwitchDrawable;
  public static TextPaint chat_contextResult_titleTextPaint;
  public static Drawable[] chat_cornerInner;
  public static Drawable[] chat_cornerOuter;
  public static Paint chat_deleteProgressPaint;
  public static Paint chat_docBackPaint;
  public static TextPaint chat_docNamePaint;
  public static TextPaint chat_durationPaint;
  public static CombinedDrawable[][] chat_fileMiniStatesDrawable;
  public static Drawable[][] chat_fileStatesDrawable;
  public static TextPaint chat_forwardNamePaint;
  public static TextPaint chat_gamePaint;
  public static Drawable chat_goIconDrawable;
  public static TextPaint chat_infoPaint;
  public static Drawable chat_inlineResultAudio;
  public static Drawable chat_inlineResultFile;
  public static Drawable chat_inlineResultLocation;
  public static TextPaint chat_instantViewPaint;
  public static Paint chat_instantViewRectPaint;
  public static Drawable[][] chat_ivStatesDrawable;
  public static TextPaint chat_livePaint;
  public static TextPaint chat_locationAddressPaint;
  public static Drawable[] chat_locationDrawable;
  public static TextPaint chat_locationTitlePaint;
  public static Drawable chat_lockIconDrawable;
  public static Drawable chat_msgAvatarLiveLocationDrawable;
  public static TextPaint chat_msgBotButtonPaint;
  public static Drawable chat_msgBroadcastDrawable;
  public static Drawable chat_msgBroadcastMediaDrawable;
  public static Drawable chat_msgCallDownGreenDrawable;
  public static Drawable chat_msgCallDownRedDrawable;
  public static Drawable chat_msgCallUpGreenDrawable;
  public static Drawable chat_msgCallUpRedDrawable;
  public static Drawable chat_msgErrorDrawable;
  public static Paint chat_msgErrorPaint;
  public static TextPaint chat_msgGameTextPaint;
  public static Drawable chat_msgInCallDrawable;
  public static Drawable chat_msgInCallSelectedDrawable;
  public static Drawable chat_msgInClockDrawable;
  public static Drawable chat_msgInDrawable;
  public static Drawable chat_msgInInstantDrawable;
  public static Drawable chat_msgInMediaDrawable;
  public static Drawable chat_msgInMediaSelectedDrawable;
  public static Drawable chat_msgInMediaShadowDrawable;
  public static Drawable chat_msgInMenuDrawable;
  public static Drawable chat_msgInMenuSelectedDrawable;
  public static Drawable chat_msgInSelectedClockDrawable;
  public static Drawable chat_msgInSelectedDrawable;
  public static Drawable chat_msgInShadowDrawable;
  public static Drawable chat_msgInViewsDrawable;
  public static Drawable chat_msgInViewsSelectedDrawable;
  public static Drawable chat_msgMediaBroadcastDrawable;
  public static Drawable chat_msgMediaCheckDrawable;
  public static Drawable chat_msgMediaClockDrawable;
  public static Drawable chat_msgMediaHalfCheckDrawable;
  public static Drawable chat_msgMediaMenuDrawable;
  public static Drawable chat_msgMediaViewsDrawable;
  public static Drawable chat_msgOutBroadcastDrawable;
  public static Drawable chat_msgOutCallDrawable;
  public static Drawable chat_msgOutCallSelectedDrawable;
  public static Drawable chat_msgOutCheckDrawable;
  public static Drawable chat_msgOutCheckSelectedDrawable;
  public static Drawable chat_msgOutClockDrawable;
  public static Drawable chat_msgOutDrawable;
  public static Drawable chat_msgOutHalfCheckDrawable;
  public static Drawable chat_msgOutHalfCheckSelectedDrawable;
  public static Drawable chat_msgOutInstantDrawable;
  public static Drawable chat_msgOutLocationDrawable;
  public static Drawable chat_msgOutMediaDrawable;
  public static Drawable chat_msgOutMediaSelectedDrawable;
  public static Drawable chat_msgOutMediaShadowDrawable;
  public static Drawable chat_msgOutMenuDrawable;
  public static Drawable chat_msgOutMenuSelectedDrawable;
  public static Drawable chat_msgOutSelectedClockDrawable;
  public static Drawable chat_msgOutSelectedDrawable;
  public static Drawable chat_msgOutShadowDrawable;
  public static Drawable chat_msgOutViewsDrawable;
  public static Drawable chat_msgOutViewsSelectedDrawable;
  public static Drawable chat_msgStickerCheckDrawable;
  public static Drawable chat_msgStickerClockDrawable;
  public static Drawable chat_msgStickerHalfCheckDrawable;
  public static Drawable chat_msgStickerViewsDrawable;
  public static TextPaint chat_msgTextPaint;
  public static TextPaint chat_msgTextPaintOneEmoji;
  public static TextPaint chat_msgTextPaintThreeEmoji;
  public static TextPaint chat_msgTextPaintTwoEmoji;
  public static Drawable chat_muteIconDrawable;
  public static TextPaint chat_namePaint;
  public static Drawable[][] chat_photoStatesDrawables;
  public static Paint chat_radialProgress2Paint;
  public static Paint chat_radialProgressPaint;
  public static Drawable chat_replyIconDrawable;
  public static Paint chat_replyLinePaint;
  public static TextPaint chat_replyNamePaint;
  public static TextPaint chat_replyTextPaint;
  public static Drawable chat_roundVideoShadow;
  public static Drawable chat_shareDrawable;
  public static Drawable chat_shareIconDrawable;
  public static TextPaint chat_shipmentPaint;
  public static Paint chat_statusPaint;
  public static Paint chat_statusRecordPaint;
  public static Drawable chat_systemDrawable;
  public static Paint chat_textSearchSelectionPaint;
  public static Paint chat_timeBackgroundPaint;
  public static TextPaint chat_timePaint;
  public static Paint chat_urlPaint;
  public static Paint checkboxSquare_backgroundPaint;
  public static Paint checkboxSquare_checkPaint;
  public static Paint checkboxSquare_eraserPaint;
  public static PorterDuffColorFilter colorFilter;
  public static PorterDuffColorFilter colorPressedFilter;
  private static int currentColor = 0;
  private static HashMap<String, Integer> currentColors;
  private static ThemeInfo currentDayTheme;
  private static ThemeInfo currentNightTheme;
  private static int currentSelectedColor = 0;
  private static ThemeInfo currentTheme;
  private static HashMap<String, Integer> defaultColors;
  private static ThemeInfo defaultTheme;
  public static Drawable dialogs_botDrawable;
  public static Drawable dialogs_broadcastDrawable;
  public static Drawable dialogs_checkDrawable;
  public static Drawable dialogs_clockDrawable;
  public static Paint dialogs_countGrayPaint;
  public static Paint dialogs_countPaint;
  public static TextPaint dialogs_countTextPaint;
  public static Drawable dialogs_errorDrawable;
  public static Paint dialogs_errorPaint;
  public static Drawable dialogs_groupDrawable;
  public static Drawable dialogs_halfCheckDrawable;
  private static Drawable dialogs_holidayDrawable;
  private static int dialogs_holidayDrawableOffsetX = 0;
  private static int dialogs_holidayDrawableOffsetY = 0;
  public static Drawable dialogs_lockDrawable;
  public static Drawable dialogs_mentionDrawable;
  public static TextPaint dialogs_messagePaint;
  public static TextPaint dialogs_messagePrintingPaint;
  public static Drawable dialogs_muteDrawable;
  public static TextPaint dialogs_nameEncryptedPaint;
  public static TextPaint dialogs_namePaint;
  public static TextPaint dialogs_offlinePaint;
  public static TextPaint dialogs_onlinePaint;
  public static Drawable dialogs_pinnedDrawable;
  public static Paint dialogs_pinnedPaint;
  public static Paint dialogs_tabletSeletedPaint;
  public static TextPaint dialogs_timePaint;
  public static Drawable dialogs_verifiedCheckDrawable;
  public static Drawable dialogs_verifiedDrawable;
  public static Paint dividerPaint;
  private static HashMap<String, String> fallbackKeys;
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
  public static String[] keys_avatar_actionBarIcon;
  public static String[] keys_avatar_actionBarSelector;
  public static String[] keys_avatar_background;
  public static String[] keys_avatar_backgroundActionBar;
  public static String[] keys_avatar_backgroundInProfile;
  public static String[] keys_avatar_nameInMessage;
  public static String[] keys_avatar_subtitleInProfile;
  private static float lastBrightnessValue;
  private static long lastHolidayCheckTime;
  private static long lastThemeSwitchTime;
  private static Sensor lightSensor;
  private static boolean lightSensorRegistered;
  public static Paint linkSelectionPaint;
  public static Drawable listSelector;
  private static Paint maskPaint;
  private static ArrayList<ThemeInfo> otherThemes;
  private static ThemeInfo previousTheme;
  public static TextPaint profile_aboutTextPaint;
  public static Drawable profile_verifiedCheckDrawable;
  public static Drawable profile_verifiedDrawable;
  public static int selectedAutoNightType;
  private static int selectedColor;
  private static SensorManager sensorManager;
  private static int serviceMessageColor;
  private static int serviceSelectedMessageColor;
  private static Runnable switchDayBrightnessRunnable;
  private static boolean switchDayRunnableScheduled;
  private static Runnable switchNightBrightnessRunnable;
  private static boolean switchNightRunnableScheduled;
  private static final Object sync = new Object();
  private static Drawable themedWallpaper;
  private static int themedWallpaperFileOffset;
  public static ArrayList<ThemeInfo> themes;
  private static HashMap<String, ThemeInfo> themesDict;
  private static Drawable wallpaper;
  private static final Object wallpaperSync = new Object();
  
  static
  {
    lastBrightnessValue = 1.0F;
    switchDayBrightnessRunnable = new Runnable()
    {
      public void run()
      {
        Theme.access$002(false);
        Theme.applyDayNightThemeMaybe(false);
      }
    };
    switchNightBrightnessRunnable = new Runnable()
    {
      public void run()
      {
        Theme.access$202(false);
        Theme.applyDayNightThemeMaybe(true);
      }
    };
    selectedAutoNightType = 0;
    autoNightBrighnessThreshold = 0.25F;
    autoNightDayStartTime = 1320;
    autoNightDayEndTime = 480;
    autoNightSunsetTime = 1320;
    autoNightLastSunCheckDay = -1;
    autoNightSunriseTime = 480;
    autoNightCityName = "";
    autoNightLocationLatitude = 10000.0D;
    autoNightLocationLongitude = 10000.0D;
    maskPaint = new Paint(1);
    chat_attachButtonDrawables = new Drawable[8];
    chat_locationDrawable = new Drawable[2];
    chat_contactDrawable = new Drawable[2];
    chat_cornerOuter = new Drawable[4];
    chat_cornerInner = new Drawable[4];
    chat_fileStatesDrawable = new Drawable[10][2];
    chat_fileMiniStatesDrawable = new CombinedDrawable[6][2];
    chat_ivStatesDrawable = new Drawable[4][2];
    chat_photoStatesDrawables = new Drawable[13][2];
    keys_avatar_background = new String[] { "avatar_backgroundRed", "avatar_backgroundOrange", "avatar_backgroundViolet", "avatar_backgroundGreen", "avatar_backgroundCyan", "avatar_backgroundBlue", "avatar_backgroundPink" };
    keys_avatar_backgroundInProfile = new String[] { "avatar_backgroundInProfileRed", "avatar_backgroundInProfileOrange", "avatar_backgroundInProfileViolet", "avatar_backgroundInProfileGreen", "avatar_backgroundInProfileCyan", "avatar_backgroundInProfileBlue", "avatar_backgroundInProfilePink" };
    keys_avatar_backgroundActionBar = new String[] { "avatar_backgroundActionBarRed", "avatar_backgroundActionBarOrange", "avatar_backgroundActionBarViolet", "avatar_backgroundActionBarGreen", "avatar_backgroundActionBarCyan", "avatar_backgroundActionBarBlue", "avatar_backgroundActionBarPink" };
    keys_avatar_subtitleInProfile = new String[] { "avatar_subtitleInProfileRed", "avatar_subtitleInProfileOrange", "avatar_subtitleInProfileViolet", "avatar_subtitleInProfileGreen", "avatar_subtitleInProfileCyan", "avatar_subtitleInProfileBlue", "avatar_subtitleInProfilePink" };
    keys_avatar_nameInMessage = new String[] { "avatar_nameInMessageRed", "avatar_nameInMessageOrange", "avatar_nameInMessageViolet", "avatar_nameInMessageGreen", "avatar_nameInMessageCyan", "avatar_nameInMessageBlue", "avatar_nameInMessagePink" };
    keys_avatar_actionBarSelector = new String[] { "avatar_actionBarSelectorRed", "avatar_actionBarSelectorOrange", "avatar_actionBarSelectorViolet", "avatar_actionBarSelectorGreen", "avatar_actionBarSelectorCyan", "avatar_actionBarSelectorBlue", "avatar_actionBarSelectorPink" };
    keys_avatar_actionBarIcon = new String[] { "avatar_actionBarIconRed", "avatar_actionBarIconOrange", "avatar_actionBarIconViolet", "avatar_actionBarIconGreen", "avatar_actionBarIconCyan", "avatar_actionBarIconBlue", "avatar_actionBarIconPink" };
    defaultColors = new HashMap();
    fallbackKeys = new HashMap();
    defaultColors.put("dialogBackground", Integer.valueOf(-1));
    defaultColors.put("dialogBackgroundGray", Integer.valueOf(-986896));
    defaultColors.put("dialogTextBlack", Integer.valueOf(-14606047));
    defaultColors.put("dialogTextLink", Integer.valueOf(-14255946));
    defaultColors.put("dialogLinkSelection", Integer.valueOf(862104035));
    defaultColors.put("dialogTextRed", Integer.valueOf(-3319206));
    defaultColors.put("dialogTextBlue", Integer.valueOf(-13660983));
    defaultColors.put("dialogTextBlue2", Integer.valueOf(-12940081));
    defaultColors.put("dialogTextBlue3", Integer.valueOf(-12664327));
    defaultColors.put("dialogTextBlue4", Integer.valueOf(-15095832));
    defaultColors.put("dialogTextGray", Integer.valueOf(-13333567));
    defaultColors.put("dialogTextGray2", Integer.valueOf(-9079435));
    defaultColors.put("dialogTextGray3", Integer.valueOf(-6710887));
    defaultColors.put("dialogTextGray4", Integer.valueOf(-5000269));
    defaultColors.put("dialogTextHint", Integer.valueOf(-6842473));
    defaultColors.put("dialogIcon", Integer.valueOf(-7697782));
    defaultColors.put("dialogGrayLine", Integer.valueOf(-2960686));
    defaultColors.put("dialogTopBackground", Integer.valueOf(-9456923));
    defaultColors.put("dialogInputField", Integer.valueOf(-2368549));
    defaultColors.put("dialogInputFieldActivated", Integer.valueOf(-13129232));
    defaultColors.put("dialogCheckboxSquareBackground", Integer.valueOf(-12345121));
    defaultColors.put("dialogCheckboxSquareCheck", Integer.valueOf(-1));
    defaultColors.put("dialogCheckboxSquareUnchecked", Integer.valueOf(-9211021));
    defaultColors.put("dialogCheckboxSquareDisabled", Integer.valueOf(-5197648));
    defaultColors.put("dialogRadioBackground", Integer.valueOf(-5000269));
    defaultColors.put("dialogRadioBackgroundChecked", Integer.valueOf(-13129232));
    defaultColors.put("dialogProgressCircle", Integer.valueOf(-11371101));
    defaultColors.put("dialogLineProgress", Integer.valueOf(-11371101));
    defaultColors.put("dialogLineProgressBackground", Integer.valueOf(-2368549));
    defaultColors.put("dialogButton", Integer.valueOf(-11955764));
    defaultColors.put("dialogButtonSelector", Integer.valueOf(251658240));
    defaultColors.put("dialogScrollGlow", Integer.valueOf(-657673));
    defaultColors.put("dialogRoundCheckBox", Integer.valueOf(-12664327));
    defaultColors.put("dialogRoundCheckBoxCheck", Integer.valueOf(-1));
    defaultColors.put("dialogBadgeBackground", Integer.valueOf(-12664327));
    defaultColors.put("dialogBadgeText", Integer.valueOf(-1));
    defaultColors.put("windowBackgroundWhite", Integer.valueOf(-1));
    defaultColors.put("progressCircle", Integer.valueOf(-11371101));
    defaultColors.put("windowBackgroundWhiteGrayIcon", Integer.valueOf(-9211021));
    defaultColors.put("windowBackgroundWhiteBlueText", Integer.valueOf(-12876608));
    defaultColors.put("windowBackgroundWhiteBlueText2", Integer.valueOf(-13333567));
    defaultColors.put("windowBackgroundWhiteBlueText3", Integer.valueOf(-14255946));
    defaultColors.put("windowBackgroundWhiteBlueText4", Integer.valueOf(-11697229));
    defaultColors.put("windowBackgroundWhiteBlueText5", Integer.valueOf(-11759926));
    defaultColors.put("windowBackgroundWhiteBlueText6", Integer.valueOf(-12940081));
    defaultColors.put("windowBackgroundWhiteBlueText7", Integer.valueOf(-13141330));
    defaultColors.put("windowBackgroundWhiteGreenText", Integer.valueOf(-14248148));
    defaultColors.put("windowBackgroundWhiteGreenText2", Integer.valueOf(-13129447));
    defaultColors.put("windowBackgroundWhiteRedText", Integer.valueOf(-3319206));
    defaultColors.put("windowBackgroundWhiteRedText2", Integer.valueOf(-2404015));
    defaultColors.put("windowBackgroundWhiteRedText3", Integer.valueOf(-2995895));
    defaultColors.put("windowBackgroundWhiteRedText4", Integer.valueOf(-3198928));
    defaultColors.put("windowBackgroundWhiteRedText5", Integer.valueOf(-1229511));
    defaultColors.put("windowBackgroundWhiteRedText6", Integer.valueOf(-39322));
    defaultColors.put("windowBackgroundWhiteGrayText", Integer.valueOf(-5723992));
    defaultColors.put("windowBackgroundWhiteGrayText2", Integer.valueOf(-7697782));
    defaultColors.put("windowBackgroundWhiteGrayText3", Integer.valueOf(-6710887));
    defaultColors.put("windowBackgroundWhiteGrayText4", Integer.valueOf(-8355712));
    defaultColors.put("windowBackgroundWhiteGrayText5", Integer.valueOf(-6052957));
    defaultColors.put("windowBackgroundWhiteGrayText6", Integer.valueOf(-9079435));
    defaultColors.put("windowBackgroundWhiteGrayText7", Integer.valueOf(-3750202));
    defaultColors.put("windowBackgroundWhiteGrayText8", Integer.valueOf(-9605774));
    defaultColors.put("windowBackgroundWhiteGrayLine", Integer.valueOf(-2368549));
    defaultColors.put("windowBackgroundWhiteBlackText", Integer.valueOf(-14606047));
    defaultColors.put("windowBackgroundWhiteHintText", Integer.valueOf(-6842473));
    defaultColors.put("windowBackgroundWhiteValueText", Integer.valueOf(-13660983));
    defaultColors.put("windowBackgroundWhiteLinkText", Integer.valueOf(-14255946));
    defaultColors.put("windowBackgroundWhiteLinkSelection", Integer.valueOf(862104035));
    defaultColors.put("windowBackgroundWhiteBlueHeader", Integer.valueOf(-12676913));
    defaultColors.put("windowBackgroundWhiteInputField", Integer.valueOf(-2368549));
    defaultColors.put("windowBackgroundWhiteInputFieldActivated", Integer.valueOf(-13129232));
    defaultColors.put("switchThumb", Integer.valueOf(-1184275));
    defaultColors.put("switchTrack", Integer.valueOf(-3684409));
    defaultColors.put("switchThumbChecked", Integer.valueOf(-12211217));
    defaultColors.put("switchTrackChecked", Integer.valueOf(-6236422));
    defaultColors.put("checkboxSquareBackground", Integer.valueOf(-12345121));
    defaultColors.put("checkboxSquareCheck", Integer.valueOf(-1));
    defaultColors.put("checkboxSquareUnchecked", Integer.valueOf(-9211021));
    defaultColors.put("checkboxSquareDisabled", Integer.valueOf(-5197648));
    defaultColors.put("listSelectorSDK21", Integer.valueOf(251658240));
    defaultColors.put("radioBackground", Integer.valueOf(-5000269));
    defaultColors.put("radioBackgroundChecked", Integer.valueOf(-13129232));
    defaultColors.put("windowBackgroundGray", Integer.valueOf(-986896));
    defaultColors.put("windowBackgroundGrayShadow", Integer.valueOf(-16777216));
    defaultColors.put("emptyListPlaceholder", Integer.valueOf(-6974059));
    defaultColors.put("divider", Integer.valueOf(-2500135));
    defaultColors.put("graySection", Integer.valueOf(-855310));
    defaultColors.put("contextProgressInner1", Integer.valueOf(-4202506));
    defaultColors.put("contextProgressOuter1", Integer.valueOf(-13920542));
    defaultColors.put("contextProgressInner2", Integer.valueOf(-4202506));
    defaultColors.put("contextProgressOuter2", Integer.valueOf(-1));
    defaultColors.put("contextProgressInner3", Integer.valueOf(-5000269));
    defaultColors.put("contextProgressOuter3", Integer.valueOf(-1));
    defaultColors.put("fastScrollActive", Integer.valueOf(-11361317));
    defaultColors.put("fastScrollInactive", Integer.valueOf(-10263709));
    defaultColors.put("fastScrollText", Integer.valueOf(-1));
    defaultColors.put("avatar_text", Integer.valueOf(-1));
    defaultColors.put("avatar_backgroundSaved", Integer.valueOf(-10043398));
    defaultColors.put("avatar_backgroundRed", Integer.valueOf(-1743531));
    defaultColors.put("avatar_backgroundOrange", Integer.valueOf(-881592));
    defaultColors.put("avatar_backgroundViolet", Integer.valueOf(-7436818));
    defaultColors.put("avatar_backgroundGreen", Integer.valueOf(-8992691));
    defaultColors.put("avatar_backgroundCyan", Integer.valueOf(-10502443));
    defaultColors.put("avatar_backgroundBlue", Integer.valueOf(-11232035));
    defaultColors.put("avatar_backgroundPink", Integer.valueOf(-887654));
    defaultColors.put("avatar_backgroundGroupCreateSpanBlue", Integer.valueOf(-4204822));
    defaultColors.put("avatar_backgroundInProfileRed", Integer.valueOf(-2592923));
    defaultColors.put("avatar_backgroundInProfileOrange", Integer.valueOf(-615071));
    defaultColors.put("avatar_backgroundInProfileViolet", Integer.valueOf(-7570990));
    defaultColors.put("avatar_backgroundInProfileGreen", Integer.valueOf(-9981091));
    defaultColors.put("avatar_backgroundInProfileCyan", Integer.valueOf(-11099461));
    defaultColors.put("avatar_backgroundInProfileBlue", Integer.valueOf(-11500111));
    defaultColors.put("avatar_backgroundInProfilePink", Integer.valueOf(-819290));
    defaultColors.put("avatar_backgroundActionBarRed", Integer.valueOf(-3514282));
    defaultColors.put("avatar_backgroundActionBarOrange", Integer.valueOf(-947900));
    defaultColors.put("avatar_backgroundActionBarViolet", Integer.valueOf(-8557884));
    defaultColors.put("avatar_backgroundActionBarGreen", Integer.valueOf(-11099828));
    defaultColors.put("avatar_backgroundActionBarCyan", Integer.valueOf(-12283220));
    defaultColors.put("avatar_backgroundActionBarBlue", Integer.valueOf(-10907718));
    defaultColors.put("avatar_backgroundActionBarPink", Integer.valueOf(-10907718));
    defaultColors.put("avatar_subtitleInProfileRed", Integer.valueOf(-406587));
    defaultColors.put("avatar_subtitleInProfileOrange", Integer.valueOf(-139832));
    defaultColors.put("avatar_subtitleInProfileViolet", Integer.valueOf(-3291923));
    defaultColors.put("avatar_subtitleInProfileGreen", Integer.valueOf(-4133446));
    defaultColors.put("avatar_subtitleInProfileCyan", Integer.valueOf(-4660496));
    defaultColors.put("avatar_subtitleInProfileBlue", Integer.valueOf(-2626822));
    defaultColors.put("avatar_subtitleInProfilePink", Integer.valueOf(-2626822));
    defaultColors.put("avatar_nameInMessageRed", Integer.valueOf(-3516848));
    defaultColors.put("avatar_nameInMessageOrange", Integer.valueOf(-2589911));
    defaultColors.put("avatar_nameInMessageViolet", Integer.valueOf(-11627828));
    defaultColors.put("avatar_nameInMessageGreen", Integer.valueOf(-11488718));
    defaultColors.put("avatar_nameInMessageCyan", Integer.valueOf(-12406360));
    defaultColors.put("avatar_nameInMessageBlue", Integer.valueOf(-11627828));
    defaultColors.put("avatar_nameInMessagePink", Integer.valueOf(-11627828));
    defaultColors.put("avatar_actionBarSelectorRed", Integer.valueOf(-4437183));
    defaultColors.put("avatar_actionBarSelectorOrange", Integer.valueOf(-1674199));
    defaultColors.put("avatar_actionBarSelectorViolet", Integer.valueOf(-9216066));
    defaultColors.put("avatar_actionBarSelectorGreen", Integer.valueOf(-12020419));
    defaultColors.put("avatar_actionBarSelectorCyan", Integer.valueOf(-13007715));
    defaultColors.put("avatar_actionBarSelectorBlue", Integer.valueOf(-11959891));
    defaultColors.put("avatar_actionBarSelectorPink", Integer.valueOf(-11959891));
    defaultColors.put("avatar_actionBarIconRed", Integer.valueOf(-1));
    defaultColors.put("avatar_actionBarIconOrange", Integer.valueOf(-1));
    defaultColors.put("avatar_actionBarIconViolet", Integer.valueOf(-1));
    defaultColors.put("avatar_actionBarIconGreen", Integer.valueOf(-1));
    defaultColors.put("avatar_actionBarIconCyan", Integer.valueOf(-1));
    defaultColors.put("avatar_actionBarIconBlue", Integer.valueOf(-1));
    defaultColors.put("avatar_actionBarIconPink", Integer.valueOf(-1));
    defaultColors.put("actionBarDefault", Integer.valueOf(-11371101));
    defaultColors.put("actionBarDefaultIcon", Integer.valueOf(-1));
    defaultColors.put("actionBarActionModeDefault", Integer.valueOf(-1));
    defaultColors.put("actionBarActionModeDefaultTop", Integer.valueOf(-NUM));
    defaultColors.put("actionBarActionModeDefaultIcon", Integer.valueOf(-9211021));
    defaultColors.put("actionBarDefaultTitle", Integer.valueOf(-1));
    defaultColors.put("actionBarDefaultSubtitle", Integer.valueOf(-2758409));
    defaultColors.put("actionBarDefaultSelector", Integer.valueOf(-12554860));
    defaultColors.put("actionBarWhiteSelector", Integer.valueOf(788529152));
    defaultColors.put("actionBarDefaultSearch", Integer.valueOf(-1));
    defaultColors.put("actionBarDefaultSearchPlaceholder", Integer.valueOf(-NUM));
    defaultColors.put("actionBarDefaultSubmenuItem", Integer.valueOf(-14606047));
    defaultColors.put("actionBarDefaultSubmenuBackground", Integer.valueOf(-1));
    defaultColors.put("actionBarActionModeDefaultSelector", Integer.valueOf(-986896));
    defaultColors.put("chats_unreadCounter", Integer.valueOf(-11613090));
    defaultColors.put("chats_unreadCounterMuted", Integer.valueOf(-3684409));
    defaultColors.put("chats_unreadCounterText", Integer.valueOf(-1));
    defaultColors.put("chats_name", Integer.valueOf(-14606047));
    defaultColors.put("chats_secretName", Integer.valueOf(-16734706));
    defaultColors.put("chats_secretIcon", Integer.valueOf(-15093466));
    defaultColors.put("chats_nameIcon", Integer.valueOf(-14408668));
    defaultColors.put("chats_pinnedIcon", Integer.valueOf(-5723992));
    defaultColors.put("chats_message", Integer.valueOf(-7368817));
    defaultColors.put("chats_draft", Integer.valueOf(-2274503));
    defaultColors.put("chats_nameMessage", Integer.valueOf(-11697229));
    defaultColors.put("chats_attachMessage", Integer.valueOf(-11697229));
    defaultColors.put("chats_actionMessage", Integer.valueOf(-11697229));
    defaultColors.put("chats_date", Integer.valueOf(-6710887));
    defaultColors.put("chats_pinnedOverlay", Integer.valueOf(134217728));
    defaultColors.put("chats_tabletSelectedOverlay", Integer.valueOf(251658240));
    defaultColors.put("chats_sentCheck", Integer.valueOf(-12146122));
    defaultColors.put("chats_sentClock", Integer.valueOf(-9061026));
    defaultColors.put("chats_sentError", Integer.valueOf(-2796974));
    defaultColors.put("chats_sentErrorIcon", Integer.valueOf(-1));
    defaultColors.put("chats_verifiedBackground", Integer.valueOf(-13391642));
    defaultColors.put("chats_verifiedCheck", Integer.valueOf(-1));
    defaultColors.put("chats_muteIcon", Integer.valueOf(-5723992));
    defaultColors.put("chats_menuBackground", Integer.valueOf(-1));
    defaultColors.put("chats_menuItemText", Integer.valueOf(-12303292));
    defaultColors.put("chats_menuItemCheck", Integer.valueOf(-10907718));
    defaultColors.put("chats_menuItemIcon", Integer.valueOf(-9211021));
    defaultColors.put("chats_menuName", Integer.valueOf(-1));
    defaultColors.put("chats_menuPhone", Integer.valueOf(-1));
    defaultColors.put("chats_menuPhoneCats", Integer.valueOf(-4004353));
    defaultColors.put("chats_menuCloud", Integer.valueOf(-1));
    defaultColors.put("chats_menuCloudBackgroundCats", Integer.valueOf(-12420183));
    defaultColors.put("chats_actionIcon", Integer.valueOf(-1));
    defaultColors.put("chats_actionBackground", Integer.valueOf(-9788978));
    defaultColors.put("chats_actionPressedBackground", Integer.valueOf(-11038014));
    defaultColors.put("chat_lockIcon", Integer.valueOf(-1));
    defaultColors.put("chat_muteIcon", Integer.valueOf(-5124893));
    defaultColors.put("chat_inBubble", Integer.valueOf(-1));
    defaultColors.put("chat_inBubbleSelected", Integer.valueOf(-1902337));
    defaultColors.put("chat_inBubbleShadow", Integer.valueOf(-14862509));
    defaultColors.put("chat_outBubble", Integer.valueOf(-1048610));
    defaultColors.put("chat_outBubbleSelected", Integer.valueOf(-2820676));
    defaultColors.put("chat_outBubbleShadow", Integer.valueOf(-14781172));
    defaultColors.put("chat_messageTextIn", Integer.valueOf(-16777216));
    defaultColors.put("chat_messageTextOut", Integer.valueOf(-16777216));
    defaultColors.put("chat_messageLinkIn", Integer.valueOf(-14255946));
    defaultColors.put("chat_messageLinkOut", Integer.valueOf(-14255946));
    defaultColors.put("chat_serviceText", Integer.valueOf(-1));
    defaultColors.put("chat_serviceLink", Integer.valueOf(-1));
    defaultColors.put("chat_serviceIcon", Integer.valueOf(-1));
    defaultColors.put("chat_mediaTimeBackground", Integer.valueOf(NUM));
    defaultColors.put("chat_outSentCheck", Integer.valueOf(-10637232));
    defaultColors.put("chat_outSentCheckSelected", Integer.valueOf(-10637232));
    defaultColors.put("chat_outSentClock", Integer.valueOf(-9061026));
    defaultColors.put("chat_outSentClockSelected", Integer.valueOf(-9061026));
    defaultColors.put("chat_inSentClock", Integer.valueOf(-6182221));
    defaultColors.put("chat_inSentClockSelected", Integer.valueOf(-7094838));
    defaultColors.put("chat_mediaSentCheck", Integer.valueOf(-1));
    defaultColors.put("chat_mediaSentClock", Integer.valueOf(-1));
    defaultColors.put("chat_inViews", Integer.valueOf(-6182221));
    defaultColors.put("chat_inViewsSelected", Integer.valueOf(-7094838));
    defaultColors.put("chat_outViews", Integer.valueOf(-9522601));
    defaultColors.put("chat_outViewsSelected", Integer.valueOf(-9522601));
    defaultColors.put("chat_mediaViews", Integer.valueOf(-1));
    defaultColors.put("chat_inMenu", Integer.valueOf(-4801083));
    defaultColors.put("chat_inMenuSelected", Integer.valueOf(-6766130));
    defaultColors.put("chat_outMenu", Integer.valueOf(-7221634));
    defaultColors.put("chat_outMenuSelected", Integer.valueOf(-7221634));
    defaultColors.put("chat_mediaMenu", Integer.valueOf(-1));
    defaultColors.put("chat_outInstant", Integer.valueOf(-11162801));
    defaultColors.put("chat_outInstantSelected", Integer.valueOf(-12019389));
    defaultColors.put("chat_inInstant", Integer.valueOf(-12940081));
    defaultColors.put("chat_inInstantSelected", Integer.valueOf(-13600331));
    defaultColors.put("chat_sentError", Integer.valueOf(-2411211));
    defaultColors.put("chat_sentErrorIcon", Integer.valueOf(-1));
    defaultColors.put("chat_selectedBackground", Integer.valueOf(NUM));
    defaultColors.put("chat_previewDurationText", Integer.valueOf(-1));
    defaultColors.put("chat_previewGameText", Integer.valueOf(-1));
    defaultColors.put("chat_inPreviewInstantText", Integer.valueOf(-12940081));
    defaultColors.put("chat_outPreviewInstantText", Integer.valueOf(-11162801));
    defaultColors.put("chat_inPreviewInstantSelectedText", Integer.valueOf(-13600331));
    defaultColors.put("chat_outPreviewInstantSelectedText", Integer.valueOf(-12019389));
    defaultColors.put("chat_secretTimeText", Integer.valueOf(-1776928));
    defaultColors.put("chat_stickerNameText", Integer.valueOf(-1));
    defaultColors.put("chat_botButtonText", Integer.valueOf(-1));
    defaultColors.put("chat_botProgress", Integer.valueOf(-1));
    defaultColors.put("chat_inForwardedNameText", Integer.valueOf(-13072697));
    defaultColors.put("chat_outForwardedNameText", Integer.valueOf(-11162801));
    defaultColors.put("chat_inViaBotNameText", Integer.valueOf(-12940081));
    defaultColors.put("chat_outViaBotNameText", Integer.valueOf(-11162801));
    defaultColors.put("chat_stickerViaBotNameText", Integer.valueOf(-1));
    defaultColors.put("chat_inReplyLine", Integer.valueOf(-10903592));
    defaultColors.put("chat_outReplyLine", Integer.valueOf(-9520791));
    defaultColors.put("chat_stickerReplyLine", Integer.valueOf(-1));
    defaultColors.put("chat_inReplyNameText", Integer.valueOf(-12940081));
    defaultColors.put("chat_outReplyNameText", Integer.valueOf(-11162801));
    defaultColors.put("chat_stickerReplyNameText", Integer.valueOf(-1));
    defaultColors.put("chat_inReplyMessageText", Integer.valueOf(-16777216));
    defaultColors.put("chat_outReplyMessageText", Integer.valueOf(-16777216));
    defaultColors.put("chat_inReplyMediaMessageText", Integer.valueOf(-6182221));
    defaultColors.put("chat_outReplyMediaMessageText", Integer.valueOf(-10112933));
    defaultColors.put("chat_inReplyMediaMessageSelectedText", Integer.valueOf(-7752511));
    defaultColors.put("chat_outReplyMediaMessageSelectedText", Integer.valueOf(-10112933));
    defaultColors.put("chat_stickerReplyMessageText", Integer.valueOf(-1));
    defaultColors.put("chat_inPreviewLine", Integer.valueOf(-9390872));
    defaultColors.put("chat_outPreviewLine", Integer.valueOf(-7812741));
    defaultColors.put("chat_inSiteNameText", Integer.valueOf(-12940081));
    defaultColors.put("chat_outSiteNameText", Integer.valueOf(-11162801));
    defaultColors.put("chat_inContactNameText", Integer.valueOf(-11625772));
    defaultColors.put("chat_outContactNameText", Integer.valueOf(-11162801));
    defaultColors.put("chat_inContactPhoneText", Integer.valueOf(-13683656));
    defaultColors.put("chat_outContactPhoneText", Integer.valueOf(-13286860));
    defaultColors.put("chat_mediaProgress", Integer.valueOf(-1));
    defaultColors.put("chat_inAudioProgress", Integer.valueOf(-1));
    defaultColors.put("chat_outAudioProgress", Integer.valueOf(-1048610));
    defaultColors.put("chat_inAudioSelectedProgress", Integer.valueOf(-1902337));
    defaultColors.put("chat_outAudioSelectedProgress", Integer.valueOf(-2820676));
    defaultColors.put("chat_mediaTimeText", Integer.valueOf(-1));
    defaultColors.put("chat_inTimeText", Integer.valueOf(-6182221));
    defaultColors.put("chat_outTimeText", Integer.valueOf(-9391780));
    defaultColors.put("chat_adminText", Integer.valueOf(-4143413));
    defaultColors.put("chat_adminSelectedText", Integer.valueOf(-7752511));
    defaultColors.put("chat_inTimeSelectedText", Integer.valueOf(-7752511));
    defaultColors.put("chat_outTimeSelectedText", Integer.valueOf(-9391780));
    defaultColors.put("chat_inAudioPerfomerText", Integer.valueOf(-13683656));
    defaultColors.put("chat_outAudioPerfomerText", Integer.valueOf(-13286860));
    defaultColors.put("chat_inAudioTitleText", Integer.valueOf(-11625772));
    defaultColors.put("chat_outAudioTitleText", Integer.valueOf(-11162801));
    defaultColors.put("chat_inAudioDurationText", Integer.valueOf(-6182221));
    defaultColors.put("chat_outAudioDurationText", Integer.valueOf(-10112933));
    defaultColors.put("chat_inAudioDurationSelectedText", Integer.valueOf(-7752511));
    defaultColors.put("chat_outAudioDurationSelectedText", Integer.valueOf(-10112933));
    defaultColors.put("chat_inAudioSeekbar", Integer.valueOf(-1774864));
    defaultColors.put("chat_inAudioCacheSeekbar", Integer.valueOf(NUM));
    defaultColors.put("chat_outAudioSeekbar", Integer.valueOf(-4463700));
    defaultColors.put("chat_outAudioCacheSeekbar", Integer.valueOf(NUM));
    defaultColors.put("chat_inAudioSeekbarSelected", Integer.valueOf(-4399384));
    defaultColors.put("chat_outAudioSeekbarSelected", Integer.valueOf(-5644906));
    defaultColors.put("chat_inAudioSeekbarFill", Integer.valueOf(-9259544));
    defaultColors.put("chat_outAudioSeekbarFill", Integer.valueOf(-8863118));
    defaultColors.put("chat_inVoiceSeekbar", Integer.valueOf(-2169365));
    defaultColors.put("chat_outVoiceSeekbar", Integer.valueOf(-4463700));
    defaultColors.put("chat_inVoiceSeekbarSelected", Integer.valueOf(-4399384));
    defaultColors.put("chat_outVoiceSeekbarSelected", Integer.valueOf(-5644906));
    defaultColors.put("chat_inVoiceSeekbarFill", Integer.valueOf(-9259544));
    defaultColors.put("chat_outVoiceSeekbarFill", Integer.valueOf(-8863118));
    defaultColors.put("chat_inFileProgress", Integer.valueOf(-1314571));
    defaultColors.put("chat_outFileProgress", Integer.valueOf(-2427453));
    defaultColors.put("chat_inFileProgressSelected", Integer.valueOf(-3413258));
    defaultColors.put("chat_outFileProgressSelected", Integer.valueOf(-3806041));
    defaultColors.put("chat_inFileNameText", Integer.valueOf(-11625772));
    defaultColors.put("chat_outFileNameText", Integer.valueOf(-11162801));
    defaultColors.put("chat_inFileInfoText", Integer.valueOf(-6182221));
    defaultColors.put("chat_outFileInfoText", Integer.valueOf(-10112933));
    defaultColors.put("chat_inFileInfoSelectedText", Integer.valueOf(-7752511));
    defaultColors.put("chat_outFileInfoSelectedText", Integer.valueOf(-10112933));
    defaultColors.put("chat_inFileBackground", Integer.valueOf(-1314571));
    defaultColors.put("chat_outFileBackground", Integer.valueOf(-2427453));
    defaultColors.put("chat_inFileBackgroundSelected", Integer.valueOf(-3413258));
    defaultColors.put("chat_outFileBackgroundSelected", Integer.valueOf(-3806041));
    defaultColors.put("chat_inVenueNameText", Integer.valueOf(-11625772));
    defaultColors.put("chat_outVenueNameText", Integer.valueOf(-11162801));
    defaultColors.put("chat_inVenueInfoText", Integer.valueOf(-6182221));
    defaultColors.put("chat_outVenueInfoText", Integer.valueOf(-10112933));
    defaultColors.put("chat_inVenueInfoSelectedText", Integer.valueOf(-7752511));
    defaultColors.put("chat_outVenueInfoSelectedText", Integer.valueOf(-10112933));
    defaultColors.put("chat_mediaInfoText", Integer.valueOf(-1));
    defaultColors.put("chat_linkSelectBackground", Integer.valueOf(862104035));
    defaultColors.put("chat_textSelectBackground", Integer.valueOf(NUM));
    defaultColors.put("chat_emojiPanelBackground", Integer.valueOf(-657673));
    defaultColors.put("chat_emojiSearchBackground", Integer.valueOf(-1578003));
    defaultColors.put("chat_emojiPanelShadowLine", Integer.valueOf(-1907225));
    defaultColors.put("chat_emojiPanelEmptyText", Integer.valueOf(-5723992));
    defaultColors.put("chat_emojiPanelIcon", Integer.valueOf(-5723992));
    defaultColors.put("chat_emojiPanelIconSelected", Integer.valueOf(-13920542));
    defaultColors.put("chat_emojiPanelStickerPackSelector", Integer.valueOf(-1907225));
    defaultColors.put("chat_emojiPanelIconSelector", Integer.valueOf(-13920542));
    defaultColors.put("chat_emojiPanelBackspace", Integer.valueOf(-5723992));
    defaultColors.put("chat_emojiPanelMasksIcon", Integer.valueOf(-1));
    defaultColors.put("chat_emojiPanelMasksIconSelected", Integer.valueOf(-10305560));
    defaultColors.put("chat_emojiPanelTrendingTitle", Integer.valueOf(-14606047));
    defaultColors.put("chat_emojiPanelStickerSetName", Integer.valueOf(-8156010));
    defaultColors.put("chat_emojiPanelStickerSetNameIcon", Integer.valueOf(-5130564));
    defaultColors.put("chat_emojiPanelTrendingDescription", Integer.valueOf(-7697782));
    defaultColors.put("chat_botKeyboardButtonText", Integer.valueOf(-13220017));
    defaultColors.put("chat_botKeyboardButtonBackground", Integer.valueOf(-1775639));
    defaultColors.put("chat_botKeyboardButtonBackgroundPressed", Integer.valueOf(-3354156));
    defaultColors.put("chat_unreadMessagesStartArrowIcon", Integer.valueOf(-6113849));
    defaultColors.put("chat_unreadMessagesStartText", Integer.valueOf(-11102772));
    defaultColors.put("chat_unreadMessagesStartBackground", Integer.valueOf(-1));
    defaultColors.put("chat_editDoneIcon", Integer.valueOf(-11420173));
    defaultColors.put("chat_inFileIcon", Integer.valueOf(-6113849));
    defaultColors.put("chat_inFileSelectedIcon", Integer.valueOf(-7883067));
    defaultColors.put("chat_outFileIcon", Integer.valueOf(-8011912));
    defaultColors.put("chat_outFileSelectedIcon", Integer.valueOf(-8011912));
    defaultColors.put("chat_inLocationBackground", Integer.valueOf(-1314571));
    defaultColors.put("chat_inLocationIcon", Integer.valueOf(-6113849));
    defaultColors.put("chat_outLocationBackground", Integer.valueOf(-2427453));
    defaultColors.put("chat_outLocationIcon", Integer.valueOf(-7880840));
    defaultColors.put("chat_inContactBackground", Integer.valueOf(-9259544));
    defaultColors.put("chat_inContactIcon", Integer.valueOf(-1));
    defaultColors.put("chat_outContactBackground", Integer.valueOf(-8863118));
    defaultColors.put("chat_outContactIcon", Integer.valueOf(-1048610));
    defaultColors.put("chat_outBroadcast", Integer.valueOf(-12146122));
    defaultColors.put("chat_mediaBroadcast", Integer.valueOf(-1));
    defaultColors.put("chat_searchPanelIcons", Integer.valueOf(-10639908));
    defaultColors.put("chat_searchPanelText", Integer.valueOf(-11625772));
    defaultColors.put("chat_secretChatStatusText", Integer.valueOf(-8421505));
    defaultColors.put("chat_fieldOverlayText", Integer.valueOf(-12940081));
    defaultColors.put("chat_stickersHintPanel", Integer.valueOf(-1));
    defaultColors.put("chat_replyPanelIcons", Integer.valueOf(-11032346));
    defaultColors.put("chat_replyPanelClose", Integer.valueOf(-5723992));
    defaultColors.put("chat_replyPanelName", Integer.valueOf(-12940081));
    defaultColors.put("chat_replyPanelMessage", Integer.valueOf(-14540254));
    defaultColors.put("chat_replyPanelLine", Integer.valueOf(-1513240));
    defaultColors.put("chat_messagePanelBackground", Integer.valueOf(-1));
    defaultColors.put("chat_messagePanelText", Integer.valueOf(-16777216));
    defaultColors.put("chat_messagePanelHint", Integer.valueOf(-5066062));
    defaultColors.put("chat_messagePanelShadow", Integer.valueOf(-16777216));
    defaultColors.put("chat_messagePanelIcons", Integer.valueOf(-5723992));
    defaultColors.put("chat_recordedVoicePlayPause", Integer.valueOf(-1));
    defaultColors.put("chat_recordedVoicePlayPausePressed", Integer.valueOf(-2495749));
    defaultColors.put("chat_recordedVoiceDot", Integer.valueOf(-2468275));
    defaultColors.put("chat_recordedVoiceBackground", Integer.valueOf(-11165981));
    defaultColors.put("chat_recordedVoiceProgress", Integer.valueOf(-6107400));
    defaultColors.put("chat_recordedVoiceProgressInner", Integer.valueOf(-1));
    defaultColors.put("chat_recordVoiceCancel", Integer.valueOf(-6710887));
    defaultColors.put("chat_messagePanelSend", Integer.valueOf(-10309397));
    defaultColors.put("key_chat_messagePanelVoiceLock", Integer.valueOf(-5987164));
    defaultColors.put("key_chat_messagePanelVoiceLockBackground", Integer.valueOf(-1));
    defaultColors.put("key_chat_messagePanelVoiceLockShadow", Integer.valueOf(-16777216));
    defaultColors.put("chat_recordTime", Integer.valueOf(-11711413));
    defaultColors.put("chat_emojiPanelNewTrending", Integer.valueOf(-11688214));
    defaultColors.put("chat_gifSaveHintText", Integer.valueOf(-1));
    defaultColors.put("chat_gifSaveHintBackground", Integer.valueOf(-871296751));
    defaultColors.put("chat_goDownButton", Integer.valueOf(-1));
    defaultColors.put("chat_goDownButtonShadow", Integer.valueOf(-16777216));
    defaultColors.put("chat_goDownButtonIcon", Integer.valueOf(-5723992));
    defaultColors.put("chat_goDownButtonCounter", Integer.valueOf(-1));
    defaultColors.put("chat_goDownButtonCounterBackground", Integer.valueOf(-11689240));
    defaultColors.put("chat_messagePanelCancelInlineBot", Integer.valueOf(-5395027));
    defaultColors.put("chat_messagePanelVoicePressed", Integer.valueOf(-1));
    defaultColors.put("chat_messagePanelVoiceBackground", Integer.valueOf(-11037236));
    defaultColors.put("chat_messagePanelVoiceShadow", Integer.valueOf(218103808));
    defaultColors.put("chat_messagePanelVoiceDelete", Integer.valueOf(-9211021));
    defaultColors.put("chat_messagePanelVoiceDuration", Integer.valueOf(-1));
    defaultColors.put("chat_inlineResultIcon", Integer.valueOf(-11037236));
    defaultColors.put("chat_topPanelBackground", Integer.valueOf(-1));
    defaultColors.put("chat_topPanelClose", Integer.valueOf(-5723992));
    defaultColors.put("chat_topPanelLine", Integer.valueOf(-9658414));
    defaultColors.put("chat_topPanelTitle", Integer.valueOf(-12940081));
    defaultColors.put("chat_topPanelMessage", Integer.valueOf(-6710887));
    defaultColors.put("chat_reportSpam", Integer.valueOf(-3188393));
    defaultColors.put("chat_addContact", Integer.valueOf(-11894091));
    defaultColors.put("chat_inLoader", Integer.valueOf(-9259544));
    defaultColors.put("chat_inLoaderSelected", Integer.valueOf(-10114080));
    defaultColors.put("chat_outLoader", Integer.valueOf(-8863118));
    defaultColors.put("chat_outLoaderSelected", Integer.valueOf(-9783964));
    defaultColors.put("chat_inLoaderPhoto", Integer.valueOf(-6113080));
    defaultColors.put("chat_inLoaderPhotoSelected", Integer.valueOf(-6113849));
    defaultColors.put("chat_inLoaderPhotoIcon", Integer.valueOf(-197380));
    defaultColors.put("chat_inLoaderPhotoIconSelected", Integer.valueOf(-1314571));
    defaultColors.put("chat_outLoaderPhoto", Integer.valueOf(-8011912));
    defaultColors.put("chat_outLoaderPhotoSelected", Integer.valueOf(-8538000));
    defaultColors.put("chat_outLoaderPhotoIcon", Integer.valueOf(-2427453));
    defaultColors.put("chat_outLoaderPhotoIconSelected", Integer.valueOf(-4134748));
    defaultColors.put("chat_mediaLoaderPhoto", Integer.valueOf(NUM));
    defaultColors.put("chat_mediaLoaderPhotoSelected", Integer.valueOf(NUM));
    defaultColors.put("chat_mediaLoaderPhotoIcon", Integer.valueOf(-1));
    defaultColors.put("chat_mediaLoaderPhotoIconSelected", Integer.valueOf(-2500135));
    defaultColors.put("chat_secretTimerBackground", Integer.valueOf(-868326258));
    defaultColors.put("chat_secretTimerText", Integer.valueOf(-1));
    defaultColors.put("profile_creatorIcon", Integer.valueOf(-11888682));
    defaultColors.put("profile_adminIcon", Integer.valueOf(-8026747));
    defaultColors.put("profile_actionIcon", Integer.valueOf(-9211021));
    defaultColors.put("profile_actionBackground", Integer.valueOf(-1));
    defaultColors.put("profile_actionPressedBackground", Integer.valueOf(-855310));
    defaultColors.put("profile_verifiedBackground", Integer.valueOf(-5056776));
    defaultColors.put("profile_verifiedCheck", Integer.valueOf(-11959368));
    defaultColors.put("profile_title", Integer.valueOf(-1));
    defaultColors.put("player_actionBar", Integer.valueOf(-1));
    defaultColors.put("player_actionBarSelector", Integer.valueOf(788529152));
    defaultColors.put("player_actionBarTitle", Integer.valueOf(-13683656));
    defaultColors.put("player_actionBarTop", Integer.valueOf(-NUM));
    defaultColors.put("player_actionBarSubtitle", Integer.valueOf(-7697782));
    defaultColors.put("player_actionBarItems", Integer.valueOf(-7697782));
    defaultColors.put("player_background", Integer.valueOf(-1));
    defaultColors.put("player_time", Integer.valueOf(-7564650));
    defaultColors.put("player_progressBackground", Integer.valueOf(419430400));
    defaultColors.put("key_player_progressCachedBackground", Integer.valueOf(419430400));
    defaultColors.put("player_progress", Integer.valueOf(-14438417));
    defaultColors.put("player_placeholder", Integer.valueOf(-5723992));
    defaultColors.put("player_placeholderBackground", Integer.valueOf(-986896));
    defaultColors.put("player_button", Integer.valueOf(-13421773));
    defaultColors.put("player_buttonActive", Integer.valueOf(-11753238));
    defaultColors.put("files_folderIcon", Integer.valueOf(-6710887));
    defaultColors.put("files_folderIconBackground", Integer.valueOf(-986896));
    defaultColors.put("files_iconText", Integer.valueOf(-1));
    defaultColors.put("sessions_devicesImage", Integer.valueOf(-6908266));
    defaultColors.put("location_markerX", Integer.valueOf(-8355712));
    defaultColors.put("location_sendLocationBackground", Integer.valueOf(-9592620));
    defaultColors.put("location_sendLiveLocationBackground", Integer.valueOf(-39836));
    defaultColors.put("location_sendLocationIcon", Integer.valueOf(-1));
    defaultColors.put("location_liveLocationProgress", Integer.valueOf(-13262875));
    defaultColors.put("location_placeLocationBackground", Integer.valueOf(-11753238));
    defaultColors.put("location_liveLocationProgress", Integer.valueOf(-13262875));
    defaultColors.put("calls_callReceivedGreenIcon", Integer.valueOf(-16725933));
    defaultColors.put("calls_callReceivedRedIcon", Integer.valueOf(-47032));
    defaultColors.put("featuredStickers_addedIcon", Integer.valueOf(-11491093));
    defaultColors.put("featuredStickers_buttonProgress", Integer.valueOf(-1));
    defaultColors.put("featuredStickers_addButton", Integer.valueOf(-11491093));
    defaultColors.put("featuredStickers_addButtonPressed", Integer.valueOf(-12346402));
    defaultColors.put("featuredStickers_delButton", Integer.valueOf(-2533545));
    defaultColors.put("featuredStickers_delButtonPressed", Integer.valueOf(-3782327));
    defaultColors.put("featuredStickers_buttonText", Integer.valueOf(-1));
    defaultColors.put("featuredStickers_unread", Integer.valueOf(-11688214));
    defaultColors.put("inappPlayerPerformer", Integer.valueOf(-13683656));
    defaultColors.put("inappPlayerTitle", Integer.valueOf(-13683656));
    defaultColors.put("inappPlayerBackground", Integer.valueOf(-1));
    defaultColors.put("inappPlayerPlayPause", Integer.valueOf(-10309397));
    defaultColors.put("inappPlayerClose", Integer.valueOf(-5723992));
    defaultColors.put("returnToCallBackground", Integer.valueOf(-12279325));
    defaultColors.put("returnToCallText", Integer.valueOf(-1));
    defaultColors.put("sharedMedia_startStopLoadIcon", Integer.valueOf(-13196562));
    defaultColors.put("sharedMedia_linkPlaceholder", Integer.valueOf(-986896));
    defaultColors.put("sharedMedia_linkPlaceholderText", Integer.valueOf(-1));
    defaultColors.put("checkbox", Integer.valueOf(-10567099));
    defaultColors.put("checkboxCheck", Integer.valueOf(-1));
    defaultColors.put("stickers_menu", Integer.valueOf(-4801083));
    defaultColors.put("stickers_menuSelector", Integer.valueOf(788529152));
    defaultColors.put("changephoneinfo_image", Integer.valueOf(-5723992));
    defaultColors.put("groupcreate_hintText", Integer.valueOf(-6182221));
    defaultColors.put("groupcreate_cursor", Integer.valueOf(-11361317));
    defaultColors.put("groupcreate_sectionShadow", Integer.valueOf(-16777216));
    defaultColors.put("groupcreate_sectionText", Integer.valueOf(-8617336));
    defaultColors.put("groupcreate_onlineText", Integer.valueOf(-12545331));
    defaultColors.put("groupcreate_offlineText", Integer.valueOf(-8156010));
    defaultColors.put("groupcreate_checkbox", Integer.valueOf(-10567099));
    defaultColors.put("groupcreate_checkboxCheck", Integer.valueOf(-1));
    defaultColors.put("groupcreate_spanText", Integer.valueOf(-14606047));
    defaultColors.put("groupcreate_spanBackground", Integer.valueOf(-855310));
    defaultColors.put("contacts_inviteBackground", Integer.valueOf(-11157919));
    defaultColors.put("contacts_inviteText", Integer.valueOf(-1));
    defaultColors.put("login_progressInner", Integer.valueOf(-1971470));
    defaultColors.put("login_progressOuter", Integer.valueOf(-10313520));
    defaultColors.put("musicPicker_checkbox", Integer.valueOf(-14043401));
    defaultColors.put("musicPicker_checkboxCheck", Integer.valueOf(-1));
    defaultColors.put("musicPicker_buttonBackground", Integer.valueOf(-10702870));
    defaultColors.put("musicPicker_buttonIcon", Integer.valueOf(-1));
    defaultColors.put("picker_enabledButton", Integer.valueOf(-15095832));
    defaultColors.put("picker_disabledButton", Integer.valueOf(-6710887));
    defaultColors.put("picker_badge", Integer.valueOf(-14043401));
    defaultColors.put("picker_badgeText", Integer.valueOf(-1));
    defaultColors.put("chat_botSwitchToInlineText", Integer.valueOf(-12348980));
    defaultColors.put("calls_ratingStar", Integer.valueOf(Integer.MIN_VALUE));
    defaultColors.put("calls_ratingStarSelected", Integer.valueOf(-11888682));
    fallbackKeys.put("chat_adminText", "chat_inTimeText");
    fallbackKeys.put("chat_adminSelectedText", "chat_inTimeSelectedText");
    fallbackKeys.put("key_player_progressCachedBackground", "player_progressBackground");
    fallbackKeys.put("chat_inAudioCacheSeekbar", "chat_inAudioSeekbar");
    fallbackKeys.put("chat_outAudioCacheSeekbar", "chat_outAudioSeekbar");
    fallbackKeys.put("chat_emojiSearchBackground", "chat_emojiPanelStickerPackSelector");
    themes = new ArrayList();
    otherThemes = new ArrayList();
    themesDict = new HashMap();
    currentColors = new HashMap();
    Object localObject1 = new ThemeInfo();
    ((ThemeInfo)localObject1).name = "Default";
    localObject3 = themes;
    defaultTheme = (ThemeInfo)localObject1;
    currentTheme = (ThemeInfo)localObject1;
    currentDayTheme = (ThemeInfo)localObject1;
    ((ArrayList)localObject3).add(localObject1);
    themesDict.put("Default", defaultTheme);
    localObject1 = new ThemeInfo();
    ((ThemeInfo)localObject1).name = "Dark";
    ((ThemeInfo)localObject1).assetName = "dark.attheme";
    localObject3 = themes;
    currentNightTheme = (ThemeInfo)localObject1;
    ((ArrayList)localObject3).add(localObject1);
    themesDict.put("Dark", localObject1);
    localObject1 = new ThemeInfo();
    ((ThemeInfo)localObject1).name = "Blue";
    ((ThemeInfo)localObject1).assetName = "bluebubbles.attheme";
    themes.add(localObject1);
    themesDict.put("Blue", localObject1);
    localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
    localObject3 = ((SharedPreferences)localObject1).getString("themes2", null);
    int i;
    Object localObject4;
    ThemeInfo localThemeInfo;
    if (!TextUtils.isEmpty((CharSequence)localObject3))
    {
      try
      {
        localObject1 = new org/json/JSONArray;
        ((JSONArray)localObject1).<init>((String)localObject3);
        for (i = 0; i < ((JSONArray)localObject1).length(); i++)
        {
          localObject3 = ThemeInfo.createWithJson(((JSONArray)localObject1).getJSONObject(i));
          if (localObject3 != null)
          {
            otherThemes.add(localObject3);
            themes.add(localObject3);
            themesDict.put(((ThemeInfo)localObject3).name, localObject3);
          }
        }
        sortThemes();
      }
      catch (Exception localException1)
      {
        FileLog.e(localException1);
      }
      localObject4 = null;
      localThemeInfo = null;
      localObject3 = localObject4;
    }
    for (;;)
    {
      try
      {
        localSharedPreferences = MessagesController.getGlobalMainSettings();
        localObject3 = localObject4;
        String str = localSharedPreferences.getString("theme", null);
        if (str != null)
        {
          localObject3 = localObject4;
          localThemeInfo = (ThemeInfo)themesDict.get(str);
        }
        localObject3 = localThemeInfo;
        localObject4 = localSharedPreferences.getString("nighttheme", null);
        if (localObject4 != null)
        {
          localObject3 = localThemeInfo;
          localObject4 = (ThemeInfo)themesDict.get(localObject4);
          if (localObject4 != null)
          {
            localObject3 = localThemeInfo;
            currentNightTheme = (ThemeInfo)localObject4;
          }
        }
        localObject3 = localThemeInfo;
        selectedAutoNightType = localSharedPreferences.getInt("selectedAutoNightType", 0);
        localObject3 = localThemeInfo;
        autoNightScheduleByLocation = localSharedPreferences.getBoolean("autoNightScheduleByLocation", false);
        localObject3 = localThemeInfo;
        autoNightBrighnessThreshold = localSharedPreferences.getFloat("autoNightBrighnessThreshold", 0.25F);
        localObject3 = localThemeInfo;
        autoNightDayStartTime = localSharedPreferences.getInt("autoNightDayStartTime", 1320);
        localObject3 = localThemeInfo;
        autoNightDayEndTime = localSharedPreferences.getInt("autoNightDayEndTime", 480);
        localObject3 = localThemeInfo;
        autoNightSunsetTime = localSharedPreferences.getInt("autoNightSunsetTime", 1320);
        localObject3 = localThemeInfo;
        autoNightSunriseTime = localSharedPreferences.getInt("autoNightSunriseTime", 480);
        localObject3 = localThemeInfo;
        autoNightCityName = localSharedPreferences.getString("autoNightCityName", "");
        localObject3 = localThemeInfo;
        long l = localSharedPreferences.getLong("autoNightLocationLatitude3", 10000L);
        if (l == 10000L) {
          continue;
        }
        localObject3 = localThemeInfo;
        autoNightLocationLatitude = Double.longBitsToDouble(l);
        localObject3 = localThemeInfo;
        l = localSharedPreferences.getLong("autoNightLocationLongitude3", 10000L);
        if (l == 10000L) {
          continue;
        }
        localObject3 = localThemeInfo;
        autoNightLocationLongitude = Double.longBitsToDouble(l);
      }
      catch (Exception localException2)
      {
        SharedPreferences localSharedPreferences;
        FileLog.e(localException2);
        Object localObject2 = localObject3;
        continue;
        localObject3 = localObject2;
        autoNightLocationLongitude = 10000.0D;
        continue;
        currentDayTheme = (ThemeInfo)localObject2;
        continue;
      }
      localObject3 = localThemeInfo;
      autoNightLastSunCheckDay = localSharedPreferences.getInt("autoNightLastSunCheckDay", -1);
      if (localThemeInfo != null) {
        continue;
      }
      localThemeInfo = defaultTheme;
      applyTheme(localThemeInfo, false, false, false);
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run() {}
      });
      ambientSensorListener = new SensorEventListener()
      {
        public void onAccuracyChanged(Sensor paramAnonymousSensor, int paramAnonymousInt) {}
        
        public void onSensorChanged(SensorEvent paramAnonymousSensorEvent)
        {
          float f1 = paramAnonymousSensorEvent.values[0];
          float f2 = f1;
          if (f1 <= 0.0F) {
            f2 = 0.1F;
          }
          if ((ApplicationLoader.mainInterfacePaused) || (!ApplicationLoader.isScreenOn)) {}
          for (;;)
          {
            return;
            if (f2 > 500.0F) {
              Theme.access$502(1.0F);
            }
            for (;;)
            {
              if (Theme.lastBrightnessValue > Theme.autoNightBrighnessThreshold) {
                break label129;
              }
              if (MediaController.getInstance().isRecordingOrListeningByProximity()) {
                break;
              }
              if (Theme.switchDayRunnableScheduled)
              {
                Theme.access$002(false);
                AndroidUtilities.cancelRunOnUIThread(Theme.switchDayBrightnessRunnable);
              }
              if (Theme.switchNightRunnableScheduled) {
                break;
              }
              Theme.access$202(true);
              AndroidUtilities.runOnUIThread(Theme.switchNightBrightnessRunnable, Theme.access$800());
              break;
              Theme.access$502((float)Math.ceil(9.932299613952637D * Math.log(f2) + 27.05900001525879D) / 100.0F);
            }
            label129:
            if (Theme.switchNightRunnableScheduled)
            {
              Theme.access$202(false);
              AndroidUtilities.cancelRunOnUIThread(Theme.switchNightBrightnessRunnable);
            }
            if (!Theme.switchDayRunnableScheduled)
            {
              Theme.access$002(true);
              AndroidUtilities.runOnUIThread(Theme.switchDayBrightnessRunnable, Theme.access$800());
            }
          }
        }
      };
      return;
      localObject3 = localThemeInfo.getString("themes", null);
      if (!TextUtils.isEmpty((CharSequence)localObject3))
      {
        localObject4 = ((String)localObject3).split("&");
        i = 0;
        if (i < localObject4.length)
        {
          localObject3 = ThemeInfo.createWithString(localObject4[i]);
          if (localObject3 != null)
          {
            otherThemes.add(localObject3);
            themes.add(localObject3);
            themesDict.put(((ThemeInfo)localObject3).name, localObject3);
          }
          i++;
          continue;
        }
      }
      saveOtherThemes();
      localThemeInfo.edit().remove("themes").commit();
      break;
      localObject3 = localThemeInfo;
      autoNightLocationLatitude = 10000.0D;
    }
  }
  
  public static void applyChatServiceMessageColor()
  {
    if (chat_actionBackgroundPaint == null) {}
    for (;;)
    {
      return;
      Object localObject1 = (Integer)currentColors.get("chat_serviceBackground");
      Integer localInteger = (Integer)currentColors.get("chat_serviceBackgroundSelected");
      Object localObject2 = localObject1;
      if (localObject1 == null) {
        localObject2 = Integer.valueOf(serviceMessageColor);
      }
      localObject1 = localInteger;
      if (localInteger == null) {
        localObject1 = Integer.valueOf(serviceSelectedMessageColor);
      }
      if (currentColor != ((Integer)localObject2).intValue())
      {
        chat_actionBackgroundPaint.setColor(((Integer)localObject2).intValue());
        colorFilter = new PorterDuffColorFilter(((Integer)localObject2).intValue(), PorterDuff.Mode.MULTIPLY);
        currentColor = ((Integer)localObject2).intValue();
        if (chat_cornerOuter[0] != null) {
          for (int i = 0; i < 4; i++)
          {
            chat_cornerOuter[i].setColorFilter(colorFilter);
            chat_cornerInner[i].setColorFilter(colorFilter);
          }
        }
      }
      if (currentSelectedColor != ((Integer)localObject1).intValue())
      {
        currentSelectedColor = ((Integer)localObject1).intValue();
        colorPressedFilter = new PorterDuffColorFilter(((Integer)localObject1).intValue(), PorterDuff.Mode.MULTIPLY);
      }
    }
  }
  
  public static void applyChatTheme(boolean paramBoolean)
  {
    if (chat_msgTextPaint == null) {}
    for (;;)
    {
      return;
      if ((chat_msgInDrawable != null) && (!paramBoolean))
      {
        chat_gamePaint.setColor(getColor("chat_previewGameText"));
        chat_durationPaint.setColor(getColor("chat_previewDurationText"));
        chat_botButtonPaint.setColor(getColor("chat_botButtonText"));
        chat_urlPaint.setColor(getColor("chat_linkSelectBackground"));
        chat_botProgressPaint.setColor(getColor("chat_botProgress"));
        chat_deleteProgressPaint.setColor(getColor("chat_secretTimeText"));
        chat_textSearchSelectionPaint.setColor(getColor("chat_textSelectBackground"));
        chat_msgErrorPaint.setColor(getColor("chat_sentError"));
        chat_statusPaint.setColor(getColor("actionBarDefaultSubtitle"));
        chat_statusRecordPaint.setColor(getColor("actionBarDefaultSubtitle"));
        chat_actionTextPaint.setColor(getColor("chat_serviceText"));
        chat_actionTextPaint.linkColor = getColor("chat_serviceLink");
        chat_contextResult_titleTextPaint.setColor(getColor("windowBackgroundWhiteBlackText"));
        chat_composeBackgroundPaint.setColor(getColor("chat_messagePanelBackground"));
        chat_timeBackgroundPaint.setColor(getColor("chat_mediaTimeBackground"));
        setDrawableColorByKey(chat_msgInDrawable, "chat_inBubble");
        setDrawableColorByKey(chat_msgInSelectedDrawable, "chat_inBubbleSelected");
        setDrawableColorByKey(chat_msgInShadowDrawable, "chat_inBubbleShadow");
        setDrawableColorByKey(chat_msgOutDrawable, "chat_outBubble");
        setDrawableColorByKey(chat_msgOutSelectedDrawable, "chat_outBubbleSelected");
        setDrawableColorByKey(chat_msgOutShadowDrawable, "chat_outBubbleShadow");
        setDrawableColorByKey(chat_msgInMediaDrawable, "chat_inBubble");
        setDrawableColorByKey(chat_msgInMediaSelectedDrawable, "chat_inBubbleSelected");
        setDrawableColorByKey(chat_msgInMediaShadowDrawable, "chat_inBubbleShadow");
        setDrawableColorByKey(chat_msgOutMediaDrawable, "chat_outBubble");
        setDrawableColorByKey(chat_msgOutMediaSelectedDrawable, "chat_outBubbleSelected");
        setDrawableColorByKey(chat_msgOutMediaShadowDrawable, "chat_outBubbleShadow");
        setDrawableColorByKey(chat_msgOutCheckDrawable, "chat_outSentCheck");
        setDrawableColorByKey(chat_msgOutCheckSelectedDrawable, "chat_outSentCheckSelected");
        setDrawableColorByKey(chat_msgOutHalfCheckDrawable, "chat_outSentCheck");
        setDrawableColorByKey(chat_msgOutHalfCheckSelectedDrawable, "chat_outSentCheckSelected");
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
        setDrawableColorByKey(chat_msgCallUpRedDrawable, "calls_callReceivedRedIcon");
        setDrawableColorByKey(chat_msgCallUpGreenDrawable, "calls_callReceivedGreenIcon");
        setDrawableColorByKey(chat_msgCallDownRedDrawable, "calls_callReceivedRedIcon");
        setDrawableColorByKey(chat_msgCallDownGreenDrawable, "calls_callReceivedGreenIcon");
        for (int i = 0; i < 2; i++)
        {
          setCombinedDrawableColor(chat_fileMiniStatesDrawable[i][0], getColor("chat_outLoader"), false);
          setCombinedDrawableColor(chat_fileMiniStatesDrawable[i][0], getColor("chat_outBubble"), true);
          setCombinedDrawableColor(chat_fileMiniStatesDrawable[i][1], getColor("chat_outLoaderSelected"), false);
          setCombinedDrawableColor(chat_fileMiniStatesDrawable[i][1], getColor("chat_outBubbleSelected"), true);
          setCombinedDrawableColor(chat_fileMiniStatesDrawable[(i + 2)][0], getColor("chat_inLoader"), false);
          setCombinedDrawableColor(chat_fileMiniStatesDrawable[(i + 2)][0], getColor("chat_inBubble"), true);
          setCombinedDrawableColor(chat_fileMiniStatesDrawable[(i + 2)][1], getColor("chat_inLoaderSelected"), false);
          setCombinedDrawableColor(chat_fileMiniStatesDrawable[(i + 2)][1], getColor("chat_inBubbleSelected"), true);
          setCombinedDrawableColor(chat_fileMiniStatesDrawable[(i + 4)][0], getColor("chat_mediaLoaderPhoto"), false);
          setCombinedDrawableColor(chat_fileMiniStatesDrawable[(i + 4)][0], getColor("chat_mediaLoaderPhotoIcon"), true);
          setCombinedDrawableColor(chat_fileMiniStatesDrawable[(i + 4)][1], getColor("chat_mediaLoaderPhotoSelected"), false);
          setCombinedDrawableColor(chat_fileMiniStatesDrawable[(i + 4)][1], getColor("chat_mediaLoaderPhotoIconSelected"), true);
        }
        for (i = 0; i < 5; i++)
        {
          setCombinedDrawableColor(chat_fileStatesDrawable[i][0], getColor("chat_outLoader"), false);
          setCombinedDrawableColor(chat_fileStatesDrawable[i][0], getColor("chat_outBubble"), true);
          setCombinedDrawableColor(chat_fileStatesDrawable[i][1], getColor("chat_outLoaderSelected"), false);
          setCombinedDrawableColor(chat_fileStatesDrawable[i][1], getColor("chat_outBubbleSelected"), true);
          setCombinedDrawableColor(chat_fileStatesDrawable[(i + 5)][0], getColor("chat_inLoader"), false);
          setCombinedDrawableColor(chat_fileStatesDrawable[(i + 5)][0], getColor("chat_inBubble"), true);
          setCombinedDrawableColor(chat_fileStatesDrawable[(i + 5)][1], getColor("chat_inLoaderSelected"), false);
          setCombinedDrawableColor(chat_fileStatesDrawable[(i + 5)][1], getColor("chat_inBubbleSelected"), true);
        }
        for (i = 0; i < 4; i++)
        {
          setCombinedDrawableColor(chat_photoStatesDrawables[i][0], getColor("chat_mediaLoaderPhoto"), false);
          setCombinedDrawableColor(chat_photoStatesDrawables[i][0], getColor("chat_mediaLoaderPhotoIcon"), true);
          setCombinedDrawableColor(chat_photoStatesDrawables[i][1], getColor("chat_mediaLoaderPhotoSelected"), false);
          setCombinedDrawableColor(chat_photoStatesDrawables[i][1], getColor("chat_mediaLoaderPhotoIconSelected"), true);
        }
        for (i = 0; i < 2; i++)
        {
          setCombinedDrawableColor(chat_photoStatesDrawables[(i + 7)][0], getColor("chat_outLoaderPhoto"), false);
          setCombinedDrawableColor(chat_photoStatesDrawables[(i + 7)][0], getColor("chat_outLoaderPhotoIcon"), true);
          setCombinedDrawableColor(chat_photoStatesDrawables[(i + 7)][1], getColor("chat_outLoaderPhotoSelected"), false);
          setCombinedDrawableColor(chat_photoStatesDrawables[(i + 7)][1], getColor("chat_outLoaderPhotoIconSelected"), true);
          setCombinedDrawableColor(chat_photoStatesDrawables[(i + 10)][0], getColor("chat_inLoaderPhoto"), false);
          setCombinedDrawableColor(chat_photoStatesDrawables[(i + 10)][0], getColor("chat_inLoaderPhotoIcon"), true);
          setCombinedDrawableColor(chat_photoStatesDrawables[(i + 10)][1], getColor("chat_inLoaderPhotoSelected"), false);
          setCombinedDrawableColor(chat_photoStatesDrawables[(i + 10)][1], getColor("chat_inLoaderPhotoIconSelected"), true);
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
        applyChatServiceMessageColor();
      }
    }
  }
  
  public static void applyCommonTheme()
  {
    if (dividerPaint == null) {}
    for (;;)
    {
      return;
      dividerPaint.setColor(getColor("divider"));
      linkSelectionPaint.setColor(getColor("windowBackgroundWhiteLinkSelection"));
      setDrawableColorByKey(avatar_broadcastDrawable, "avatar_text");
      setDrawableColorByKey(avatar_savedDrawable, "avatar_text");
      setDrawableColorByKey(avatar_photoDrawable, "avatar_text");
    }
  }
  
  private static void applyDayNightThemeMaybe(boolean paramBoolean)
  {
    if (paramBoolean) {
      if (currentTheme != currentNightTheme)
      {
        lastThemeSwitchTime = SystemClock.uptimeMillis();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, new Object[] { currentNightTheme });
      }
    }
    for (;;)
    {
      return;
      if (currentTheme != currentDayTheme)
      {
        lastThemeSwitchTime = SystemClock.uptimeMillis();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, new Object[] { currentDayTheme });
      }
    }
  }
  
  public static void applyDialogsTheme()
  {
    if (dialogs_namePaint == null) {}
    for (;;)
    {
      return;
      dialogs_namePaint.setColor(getColor("chats_name"));
      dialogs_nameEncryptedPaint.setColor(getColor("chats_secretName"));
      TextPaint localTextPaint1 = dialogs_messagePaint;
      TextPaint localTextPaint2 = dialogs_messagePaint;
      int i = getColor("chats_message");
      localTextPaint2.linkColor = i;
      localTextPaint1.setColor(i);
      dialogs_tabletSeletedPaint.setColor(getColor("chats_tabletSelectedOverlay"));
      dialogs_pinnedPaint.setColor(getColor("chats_pinnedOverlay"));
      dialogs_timePaint.setColor(getColor("chats_date"));
      dialogs_countTextPaint.setColor(getColor("chats_unreadCounterText"));
      dialogs_messagePrintingPaint.setColor(getColor("chats_actionMessage"));
      dialogs_countPaint.setColor(getColor("chats_unreadCounter"));
      dialogs_countGrayPaint.setColor(getColor("chats_unreadCounterMuted"));
      dialogs_errorPaint.setColor(getColor("chats_sentError"));
      dialogs_onlinePaint.setColor(getColor("windowBackgroundWhiteBlueText3"));
      dialogs_offlinePaint.setColor(getColor("windowBackgroundWhiteGrayText3"));
      setDrawableColorByKey(dialogs_lockDrawable, "chats_secretIcon");
      setDrawableColorByKey(dialogs_checkDrawable, "chats_sentCheck");
      setDrawableColorByKey(dialogs_halfCheckDrawable, "chats_sentCheck");
      setDrawableColorByKey(dialogs_clockDrawable, "chats_sentClock");
      setDrawableColorByKey(dialogs_errorDrawable, "chats_sentErrorIcon");
      setDrawableColorByKey(dialogs_groupDrawable, "chats_nameIcon");
      setDrawableColorByKey(dialogs_broadcastDrawable, "chats_nameIcon");
      setDrawableColorByKey(dialogs_botDrawable, "chats_nameIcon");
      setDrawableColorByKey(dialogs_pinnedDrawable, "chats_pinnedIcon");
      setDrawableColorByKey(dialogs_muteDrawable, "chats_muteIcon");
      setDrawableColorByKey(dialogs_verifiedDrawable, "chats_verifiedBackground");
      setDrawableColorByKey(dialogs_verifiedCheckDrawable, "chats_verifiedCheck");
    }
  }
  
  public static void applyPreviousTheme()
  {
    if (previousTheme == null) {}
    for (;;)
    {
      return;
      applyTheme(previousTheme, true, false, false);
      previousTheme = null;
      checkAutoNightThemeConditions();
    }
  }
  
  public static void applyProfileTheme()
  {
    if (profile_verifiedDrawable == null) {}
    for (;;)
    {
      return;
      profile_aboutTextPaint.setColor(getColor("windowBackgroundWhiteBlackText"));
      profile_aboutTextPaint.linkColor = getColor("windowBackgroundWhiteLinkText");
      setDrawableColorByKey(profile_verifiedDrawable, "profile_verifiedBackground");
      setDrawableColorByKey(profile_verifiedCheckDrawable, "profile_verifiedCheck");
    }
  }
  
  public static void applyTheme(ThemeInfo paramThemeInfo)
  {
    applyTheme(paramThemeInfo, true, true, false);
  }
  
  public static void applyTheme(ThemeInfo paramThemeInfo, boolean paramBoolean)
  {
    applyTheme(paramThemeInfo, true, true, paramBoolean);
  }
  
  public static void applyTheme(ThemeInfo paramThemeInfo, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if (paramThemeInfo == null) {
      return;
    }
    Object localObject = ThemeEditorView.getInstance();
    if (localObject != null) {
      ((ThemeEditorView)localObject).destroy();
    }
    for (;;)
    {
      try
      {
        if ((paramThemeInfo.pathToFile == null) && (paramThemeInfo.assetName == null)) {
          break label188;
        }
        if ((!paramBoolean3) && (paramBoolean1))
        {
          localObject = MessagesController.getGlobalMainSettings().edit();
          ((SharedPreferences.Editor)localObject).putString("theme", paramThemeInfo.name);
          if (paramBoolean2) {
            ((SharedPreferences.Editor)localObject).remove("overrideThemeWallpaper");
          }
          ((SharedPreferences.Editor)localObject).commit();
        }
        if (paramThemeInfo.assetName == null) {
          break label162;
        }
        currentColors = getThemeFileValues(null, paramThemeInfo.assetName);
        currentTheme = paramThemeInfo;
        if (!paramBoolean3) {
          currentDayTheme = currentTheme;
        }
        reloadWallpaper();
        applyCommonTheme();
        applyDialogsTheme();
        applyProfileTheme();
        applyChatTheme(false);
        paramThemeInfo = new org/telegram/ui/ActionBar/Theme$9;
        paramThemeInfo.<init>(paramBoolean3);
        AndroidUtilities.runOnUIThread(paramThemeInfo);
      }
      catch (Exception paramThemeInfo)
      {
        FileLog.e(paramThemeInfo);
      }
      break;
      label162:
      localObject = new java/io/File;
      ((File)localObject).<init>(paramThemeInfo.pathToFile);
      currentColors = getThemeFileValues((File)localObject, null);
      continue;
      label188:
      if ((!paramBoolean3) && (paramBoolean1))
      {
        localObject = MessagesController.getGlobalMainSettings().edit();
        ((SharedPreferences.Editor)localObject).remove("theme");
        if (paramBoolean2) {
          ((SharedPreferences.Editor)localObject).remove("overrideThemeWallpaper");
        }
        ((SharedPreferences.Editor)localObject).commit();
      }
      currentColors.clear();
      wallpaper = null;
      themedWallpaper = null;
    }
  }
  
  public static ThemeInfo applyThemeFile(File paramFile, String paramString, boolean paramBoolean)
  {
    boolean bool = true;
    for (;;)
    {
      try
      {
        if ((!paramString.equals("Default")) && (!paramString.equals("Dark")) && (!paramString.equals("Blue"))) {
          continue;
        }
        paramFile = null;
      }
      catch (Exception paramFile)
      {
        File localFile;
        int i;
        ThemeInfo localThemeInfo;
        FileLog.e(paramFile);
        paramFile = null;
        continue;
        previousTheme = currentTheme;
        continue;
        paramBoolean = false;
        continue;
      }
      return paramFile;
      localFile = new java/io/File;
      localFile.<init>(ApplicationLoader.getFilesDirFixed(), paramString);
      if (!AndroidUtilities.copyFile(paramFile, localFile))
      {
        paramFile = null;
      }
      else
      {
        i = 0;
        localThemeInfo = (ThemeInfo)themesDict.get(paramString);
        paramFile = localThemeInfo;
        if (localThemeInfo == null)
        {
          i = 1;
          paramFile = new org/telegram/ui/ActionBar/Theme$ThemeInfo;
          paramFile.<init>();
          paramFile.name = paramString;
          paramFile.pathToFile = localFile.getAbsolutePath();
        }
        if (paramBoolean) {
          continue;
        }
        previousTheme = null;
        if (i != 0)
        {
          themes.add(paramFile);
          themesDict.put(paramFile.name, paramFile);
          otherThemes.add(paramFile);
          sortThemes();
          saveOtherThemes();
        }
        if (paramBoolean) {
          continue;
        }
        paramBoolean = bool;
        applyTheme(paramFile, paramBoolean, true, false);
      }
    }
  }
  
  private static void calcBackgroundColor(Drawable paramDrawable, int paramInt)
  {
    if (paramInt != 2)
    {
      paramDrawable = AndroidUtilities.calcDrawableColor(paramDrawable);
      serviceMessageColor = paramDrawable[0];
      serviceSelectedMessageColor = paramDrawable[1];
    }
  }
  
  public static boolean canStartHolidayAnimation()
  {
    return canStartHolidayAnimation;
  }
  
  public static void checkAutoNightThemeConditions()
  {
    checkAutoNightThemeConditions(false);
  }
  
  public static void checkAutoNightThemeConditions(boolean paramBoolean)
  {
    if (previousTheme != null) {
      return;
    }
    if (paramBoolean)
    {
      if (switchNightRunnableScheduled)
      {
        switchNightRunnableScheduled = false;
        AndroidUtilities.cancelRunOnUIThread(switchNightBrightnessRunnable);
      }
      if (switchDayRunnableScheduled)
      {
        switchDayRunnableScheduled = false;
        AndroidUtilities.cancelRunOnUIThread(switchDayBrightnessRunnable);
      }
    }
    if (selectedAutoNightType != 2)
    {
      if (switchNightRunnableScheduled)
      {
        switchNightRunnableScheduled = false;
        AndroidUtilities.cancelRunOnUIThread(switchNightBrightnessRunnable);
      }
      if (switchDayRunnableScheduled)
      {
        switchDayRunnableScheduled = false;
        AndroidUtilities.cancelRunOnUIThread(switchDayBrightnessRunnable);
      }
      if (lightSensorRegistered)
      {
        lastBrightnessValue = 1.0F;
        sensorManager.unregisterListener(ambientSensorListener, lightSensor);
        lightSensorRegistered = false;
        if (BuildVars.LOGS_ENABLED) {
          FileLog.d("light sensor unregistered");
        }
      }
    }
    int i = 0;
    int j;
    int k;
    if (selectedAutoNightType == 1)
    {
      Object localObject = Calendar.getInstance();
      ((Calendar)localObject).setTimeInMillis(System.currentTimeMillis());
      j = ((Calendar)localObject).get(11) * 60 + ((Calendar)localObject).get(12);
      if (autoNightScheduleByLocation)
      {
        i = ((Calendar)localObject).get(5);
        if ((autoNightLastSunCheckDay != i) && (autoNightLocationLatitude != 10000.0D) && (autoNightLocationLongitude != 10000.0D))
        {
          localObject = SunDate.calculateSunriseSunset(autoNightLocationLatitude, autoNightLocationLongitude);
          autoNightSunriseTime = localObject[0];
          autoNightSunsetTime = localObject[1];
          autoNightLastSunCheckDay = i;
          saveAutoNightThemeConfig();
        }
        k = autoNightSunsetTime;
        i = autoNightSunriseTime;
        label234:
        if (k >= i) {
          break label298;
        }
        if ((k > j) || (j > i)) {
          break label293;
        }
        i = 2;
        label253:
        if (i != 0) {
          if (i != 2) {
            break label455;
          }
        }
      }
    }
    label293:
    label298:
    label455:
    for (boolean bool = true;; bool = false)
    {
      applyDayNightThemeMaybe(bool);
      if (!paramBoolean) {
        break;
      }
      lastThemeSwitchTime = 0L;
      break;
      k = autoNightDayStartTime;
      i = autoNightDayEndTime;
      break label234;
      i = 1;
      break label253;
      if (((k <= j) && (j <= 1440)) || ((j >= 0) && (j <= i)))
      {
        i = 2;
        break label253;
      }
      i = 1;
      break label253;
      if (selectedAutoNightType == 2)
      {
        if (lightSensor == null)
        {
          sensorManager = (SensorManager)ApplicationLoader.applicationContext.getSystemService("sensor");
          lightSensor = sensorManager.getDefaultSensor(5);
        }
        if ((!lightSensorRegistered) && (lightSensor != null))
        {
          sensorManager.registerListener(ambientSensorListener, lightSensor, 500000);
          lightSensorRegistered = true;
          if (BuildVars.LOGS_ENABLED) {
            FileLog.d("light sensor registered");
          }
        }
        if (lastBrightnessValue <= autoNightBrighnessThreshold)
        {
          if (switchNightRunnableScheduled) {
            break label253;
          }
          i = 2;
          break label253;
        }
        if (switchDayRunnableScheduled) {
          break label253;
        }
        i = 1;
        break label253;
      }
      if (selectedAutoNightType != 0) {
        break label253;
      }
      i = 1;
      break label253;
    }
  }
  
  public static void createChatResources(Context paramContext, boolean paramBoolean)
  {
    Object localObject2;
    synchronized (sync)
    {
      if (chat_msgTextPaint == null)
      {
        localObject2 = new android/text/TextPaint;
        ((TextPaint)localObject2).<init>(1);
        chat_msgTextPaint = (TextPaint)localObject2;
        localObject2 = new android/text/TextPaint;
        ((TextPaint)localObject2).<init>(1);
        chat_msgGameTextPaint = (TextPaint)localObject2;
        localObject2 = new android/text/TextPaint;
        ((TextPaint)localObject2).<init>(1);
        chat_msgTextPaintOneEmoji = (TextPaint)localObject2;
        localObject2 = new android/text/TextPaint;
        ((TextPaint)localObject2).<init>(1);
        chat_msgTextPaintTwoEmoji = (TextPaint)localObject2;
        localObject2 = new android/text/TextPaint;
        ((TextPaint)localObject2).<init>(1);
        chat_msgTextPaintThreeEmoji = (TextPaint)localObject2;
        localObject2 = new android/text/TextPaint;
        ((TextPaint)localObject2).<init>(1);
        chat_msgBotButtonPaint = (TextPaint)localObject2;
        chat_msgBotButtonPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      }
      if ((!paramBoolean) && (chat_msgInDrawable == null))
      {
        chat_infoPaint = new TextPaint(1);
        chat_docNamePaint = new TextPaint(1);
        chat_docNamePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        chat_docBackPaint = new Paint(1);
        chat_deleteProgressPaint = new Paint(1);
        chat_botProgressPaint = new Paint(1);
        chat_botProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        chat_botProgressPaint.setStyle(Paint.Style.STROKE);
        chat_locationTitlePaint = new TextPaint(1);
        chat_locationTitlePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        chat_locationAddressPaint = new TextPaint(1);
        chat_urlPaint = new Paint();
        chat_textSearchSelectionPaint = new Paint();
        chat_radialProgressPaint = new Paint(1);
        chat_radialProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        chat_radialProgressPaint.setStyle(Paint.Style.STROKE);
        chat_radialProgressPaint.setColor(-NUM);
        chat_radialProgress2Paint = new Paint(1);
        chat_radialProgress2Paint.setStrokeCap(Paint.Cap.ROUND);
        chat_radialProgress2Paint.setStyle(Paint.Style.STROKE);
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
        chat_instantViewRectPaint.setStyle(Paint.Style.STROKE);
        chat_replyLinePaint = new Paint();
        chat_msgErrorPaint = new Paint(1);
        chat_statusPaint = new Paint(1);
        chat_statusRecordPaint = new Paint(1);
        chat_statusRecordPaint.setStyle(Paint.Style.STROKE);
        chat_statusRecordPaint.setStrokeCap(Paint.Cap.ROUND);
        chat_actionTextPaint = new TextPaint(1);
        chat_actionTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        chat_actionBackgroundPaint = new Paint(1);
        chat_timeBackgroundPaint = new Paint(1);
        chat_contextResult_titleTextPaint = new TextPaint(1);
        chat_contextResult_titleTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        chat_contextResult_descriptionTextPaint = new TextPaint(1);
        chat_composeBackgroundPaint = new Paint();
        ??? = paramContext.getResources();
        chat_msgInDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgInSelectedDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgOutDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgOutSelectedDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgInMediaDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgInMediaSelectedDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgOutMediaDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgOutMediaSelectedDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgOutCheckDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgOutCheckSelectedDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgMediaCheckDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgStickerCheckDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgOutHalfCheckDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgOutHalfCheckSelectedDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgMediaHalfCheckDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgStickerHalfCheckDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgOutClockDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgOutSelectedClockDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgInClockDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgInSelectedClockDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgMediaClockDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgStickerClockDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgInViewsDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgInViewsSelectedDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgOutViewsDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgOutViewsSelectedDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgMediaViewsDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgStickerViewsDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgInMenuDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgInMenuSelectedDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgOutMenuDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgOutMenuSelectedDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgMediaMenuDrawable = ((Resources)???).getDrawable(NUM);
        chat_msgInInstantDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgOutInstantDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgErrorDrawable = ((Resources)???).getDrawable(NUM);
        chat_muteIconDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_lockIconDrawable = ((Resources)???).getDrawable(NUM);
        chat_msgBroadcastDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgBroadcastMediaDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgInCallDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgInCallSelectedDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgOutCallDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgOutCallSelectedDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgCallUpRedDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgCallUpGreenDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgCallDownRedDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgCallDownGreenDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_msgAvatarLiveLocationDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_inlineResultFile = ((Resources)???).getDrawable(NUM);
        chat_inlineResultAudio = ((Resources)???).getDrawable(NUM);
        chat_inlineResultLocation = ((Resources)???).getDrawable(NUM);
        chat_msgInShadowDrawable = ((Resources)???).getDrawable(NUM);
        chat_msgOutShadowDrawable = ((Resources)???).getDrawable(NUM);
        chat_msgInMediaShadowDrawable = ((Resources)???).getDrawable(NUM);
        chat_msgOutMediaShadowDrawable = ((Resources)???).getDrawable(NUM);
        chat_botLinkDrawalbe = ((Resources)???).getDrawable(NUM);
        chat_botInlineDrawable = ((Resources)???).getDrawable(NUM);
        chat_systemDrawable = ((Resources)???).getDrawable(NUM);
        chat_contextResult_shadowUnderSwitchDrawable = ((Resources)???).getDrawable(NUM).mutate();
        chat_attachButtonDrawables[0] = ((Resources)???).getDrawable(NUM);
        chat_attachButtonDrawables[1] = ((Resources)???).getDrawable(NUM);
        chat_attachButtonDrawables[2] = ((Resources)???).getDrawable(NUM);
        chat_attachButtonDrawables[3] = ((Resources)???).getDrawable(NUM);
        chat_attachButtonDrawables[4] = ((Resources)???).getDrawable(NUM);
        chat_attachButtonDrawables[5] = ((Resources)???).getDrawable(NUM);
        chat_attachButtonDrawables[6] = ((Resources)???).getDrawable(NUM);
        chat_attachButtonDrawables[7] = ((Resources)???).getDrawable(NUM);
        chat_cornerOuter[0] = ((Resources)???).getDrawable(NUM);
        chat_cornerOuter[1] = ((Resources)???).getDrawable(NUM);
        chat_cornerOuter[2] = ((Resources)???).getDrawable(NUM);
        chat_cornerOuter[3] = ((Resources)???).getDrawable(NUM);
        chat_cornerInner[0] = ((Resources)???).getDrawable(NUM);
        chat_cornerInner[1] = ((Resources)???).getDrawable(NUM);
        chat_cornerInner[2] = ((Resources)???).getDrawable(NUM);
        chat_cornerInner[3] = ((Resources)???).getDrawable(NUM);
        chat_shareDrawable = ((Resources)???).getDrawable(NUM);
        chat_shareIconDrawable = ((Resources)???).getDrawable(NUM);
        chat_replyIconDrawable = ((Resources)???).getDrawable(NUM);
        chat_goIconDrawable = ((Resources)???).getDrawable(NUM);
        chat_ivStatesDrawable[0][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(40.0F), NUM, 1);
        chat_ivStatesDrawable[0][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(40.0F), NUM, 1);
        chat_ivStatesDrawable[1][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(40.0F), NUM, 1);
        chat_ivStatesDrawable[1][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(40.0F), NUM, 1);
        chat_ivStatesDrawable[2][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(40.0F), NUM, 1);
        chat_ivStatesDrawable[2][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(40.0F), NUM, 1);
        chat_ivStatesDrawable[3][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(40.0F), NUM, 2);
        chat_ivStatesDrawable[3][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(40.0F), NUM, 2);
        chat_fileMiniStatesDrawable[0][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(22.0F), NUM);
        chat_fileMiniStatesDrawable[0][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(22.0F), NUM);
        chat_fileMiniStatesDrawable[1][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(22.0F), NUM);
        chat_fileMiniStatesDrawable[1][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(22.0F), NUM);
        chat_fileMiniStatesDrawable[2][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(22.0F), NUM);
        chat_fileMiniStatesDrawable[2][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(22.0F), NUM);
        chat_fileMiniStatesDrawable[3][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(22.0F), NUM);
        chat_fileMiniStatesDrawable[3][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(22.0F), NUM);
        chat_fileMiniStatesDrawable[4][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(22.0F), NUM);
        chat_fileMiniStatesDrawable[4][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(22.0F), NUM);
        chat_fileMiniStatesDrawable[5][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(22.0F), NUM);
        chat_fileMiniStatesDrawable[5][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(22.0F), NUM);
        chat_fileStatesDrawable[0][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), NUM);
        chat_fileStatesDrawable[0][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), NUM);
        chat_fileStatesDrawable[1][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), NUM);
        chat_fileStatesDrawable[1][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), NUM);
        chat_fileStatesDrawable[2][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), NUM);
        chat_fileStatesDrawable[2][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), NUM);
        chat_fileStatesDrawable[3][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), NUM);
        chat_fileStatesDrawable[3][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), NUM);
        chat_fileStatesDrawable[4][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), NUM);
        chat_fileStatesDrawable[4][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), NUM);
        chat_fileStatesDrawable[5][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), NUM);
        chat_fileStatesDrawable[5][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), NUM);
        chat_fileStatesDrawable[6][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), NUM);
        chat_fileStatesDrawable[6][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), NUM);
        chat_fileStatesDrawable[7][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), NUM);
        chat_fileStatesDrawable[7][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), NUM);
        chat_fileStatesDrawable[8][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), NUM);
        chat_fileStatesDrawable[8][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), NUM);
        chat_fileStatesDrawable[9][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), NUM);
        chat_fileStatesDrawable[9][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), NUM);
        chat_photoStatesDrawables[0][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), NUM);
        chat_photoStatesDrawables[0][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), NUM);
        chat_photoStatesDrawables[1][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), NUM);
        chat_photoStatesDrawables[1][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), NUM);
        chat_photoStatesDrawables[2][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), NUM);
        chat_photoStatesDrawables[2][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), NUM);
        chat_photoStatesDrawables[3][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), NUM);
        chat_photoStatesDrawables[3][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), NUM);
        localObject2 = chat_photoStatesDrawables[4];
        Drawable[] arrayOfDrawable = chat_photoStatesDrawables[4];
        Object localObject3 = ((Resources)???).getDrawable(NUM);
        arrayOfDrawable[1] = localObject3;
        localObject2[0] = localObject3;
        localObject3 = chat_photoStatesDrawables[5];
        arrayOfDrawable = chat_photoStatesDrawables[5];
        localObject2 = ((Resources)???).getDrawable(NUM);
        arrayOfDrawable[1] = localObject2;
        localObject3[0] = localObject2;
        localObject3 = chat_photoStatesDrawables[6];
        arrayOfDrawable = chat_photoStatesDrawables[6];
        localObject2 = ((Resources)???).getDrawable(NUM);
        arrayOfDrawable[1] = localObject2;
        localObject3[0] = localObject2;
        chat_photoStatesDrawables[7][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), NUM);
        chat_photoStatesDrawables[7][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), NUM);
        chat_photoStatesDrawables[8][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), NUM);
        chat_photoStatesDrawables[8][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), NUM);
        chat_photoStatesDrawables[9][0] = ((Resources)???).getDrawable(NUM).mutate();
        chat_photoStatesDrawables[9][1] = ((Resources)???).getDrawable(NUM).mutate();
        chat_photoStatesDrawables[10][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), NUM);
        chat_photoStatesDrawables[10][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), NUM);
        chat_photoStatesDrawables[11][0] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), NUM);
        chat_photoStatesDrawables[11][1] = createCircleDrawableWithIcon(AndroidUtilities.dp(48.0F), NUM);
        chat_photoStatesDrawables[12][0] = ((Resources)???).getDrawable(NUM).mutate();
        chat_photoStatesDrawables[12][1] = ((Resources)???).getDrawable(NUM).mutate();
        chat_contactDrawable[0] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), NUM);
        chat_contactDrawable[1] = createCircleDrawableWithIcon(AndroidUtilities.dp(44.0F), NUM);
        chat_locationDrawable[0] = createRoundRectDrawableWithIcon(AndroidUtilities.dp(2.0F), NUM);
        chat_locationDrawable[1] = createRoundRectDrawableWithIcon(AndroidUtilities.dp(2.0F), NUM);
        chat_composeShadowDrawable = paramContext.getResources().getDrawable(NUM);
      }
    }
    for (;;)
    {
      try
      {
        int i = AndroidUtilities.roundMessageSize + AndroidUtilities.dp(6.0F);
        paramContext = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
        ??? = new android/graphics/Canvas;
        ((Canvas)???).<init>(paramContext);
        localObject2 = new android/graphics/Paint;
        ((Paint)localObject2).<init>(1);
        ((Paint)localObject2).setShadowLayer(AndroidUtilities.dp(4.0F), 0.0F, 0.0F, NUM);
        ((Canvas)???).drawCircle(i / 2, i / 2, AndroidUtilities.roundMessageSize / 2 - AndroidUtilities.dp(1.0F), (Paint)localObject2);
      }
      catch (Throwable paramContext)
      {
        continue;
      }
      try
      {
        ((Canvas)???).setBitmap(null);
        ??? = new android/graphics/drawable/BitmapDrawable;
        ((BitmapDrawable)???).<init>(paramContext);
        chat_roundVideoShadow = (Drawable)???;
        applyChatTheme(paramBoolean);
        chat_msgTextPaintOneEmoji.setTextSize(AndroidUtilities.dp(28.0F));
        chat_msgTextPaintTwoEmoji.setTextSize(AndroidUtilities.dp(24.0F));
        chat_msgTextPaintThreeEmoji.setTextSize(AndroidUtilities.dp(20.0F));
        chat_msgTextPaint.setTextSize(AndroidUtilities.dp(SharedConfig.fontSize));
        chat_msgGameTextPaint.setTextSize(AndroidUtilities.dp(14.0F));
        chat_msgBotButtonPaint.setTextSize(AndroidUtilities.dp(15.0F));
        if ((!paramBoolean) && (chat_botProgressPaint != null))
        {
          chat_botProgressPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
          chat_infoPaint.setTextSize(AndroidUtilities.dp(12.0F));
          chat_docNamePaint.setTextSize(AndroidUtilities.dp(15.0F));
          chat_locationTitlePaint.setTextSize(AndroidUtilities.dp(15.0F));
          chat_locationAddressPaint.setTextSize(AndroidUtilities.dp(13.0F));
          chat_audioTimePaint.setTextSize(AndroidUtilities.dp(12.0F));
          chat_livePaint.setTextSize(AndroidUtilities.dp(12.0F));
          chat_audioTitlePaint.setTextSize(AndroidUtilities.dp(16.0F));
          chat_audioPerformerPaint.setTextSize(AndroidUtilities.dp(15.0F));
          chat_botButtonPaint.setTextSize(AndroidUtilities.dp(15.0F));
          chat_contactNamePaint.setTextSize(AndroidUtilities.dp(15.0F));
          chat_contactPhonePaint.setTextSize(AndroidUtilities.dp(13.0F));
          chat_durationPaint.setTextSize(AndroidUtilities.dp(12.0F));
          chat_timePaint.setTextSize(AndroidUtilities.dp(12.0F));
          chat_adminPaint.setTextSize(AndroidUtilities.dp(13.0F));
          chat_namePaint.setTextSize(AndroidUtilities.dp(14.0F));
          chat_forwardNamePaint.setTextSize(AndroidUtilities.dp(14.0F));
          chat_replyNamePaint.setTextSize(AndroidUtilities.dp(14.0F));
          chat_replyTextPaint.setTextSize(AndroidUtilities.dp(14.0F));
          chat_gamePaint.setTextSize(AndroidUtilities.dp(13.0F));
          chat_shipmentPaint.setTextSize(AndroidUtilities.dp(13.0F));
          chat_instantViewPaint.setTextSize(AndroidUtilities.dp(13.0F));
          chat_instantViewRectPaint.setStrokeWidth(AndroidUtilities.dp(1.0F));
          chat_statusRecordPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
          chat_actionTextPaint.setTextSize(AndroidUtilities.dp(Math.max(16, SharedConfig.fontSize) - 2));
          chat_contextResult_titleTextPaint.setTextSize(AndroidUtilities.dp(15.0F));
          chat_contextResult_descriptionTextPaint.setTextSize(AndroidUtilities.dp(13.0F));
          chat_radialProgressPaint.setStrokeWidth(AndroidUtilities.dp(3.0F));
          chat_radialProgress2Paint.setStrokeWidth(AndroidUtilities.dp(2.0F));
        }
        return;
        paramContext = finally;
        throw paramContext;
      }
      catch (Exception localException) {}
    }
  }
  
  public static Drawable createCircleDrawable(int paramInt1, int paramInt2)
  {
    Object localObject = new OvalShape();
    ((OvalShape)localObject).resize(paramInt1, paramInt1);
    localObject = new ShapeDrawable((Shape)localObject);
    ((ShapeDrawable)localObject).getPaint().setColor(paramInt2);
    return (Drawable)localObject;
  }
  
  public static CombinedDrawable createCircleDrawableWithIcon(int paramInt1, int paramInt2)
  {
    return createCircleDrawableWithIcon(paramInt1, paramInt2, 0);
  }
  
  public static CombinedDrawable createCircleDrawableWithIcon(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt2 != 0) {}
    for (Drawable localDrawable = ApplicationLoader.applicationContext.getResources().getDrawable(paramInt2).mutate();; localDrawable = null) {
      return createCircleDrawableWithIcon(paramInt1, localDrawable, paramInt3);
    }
  }
  
  public static CombinedDrawable createCircleDrawableWithIcon(int paramInt1, Drawable paramDrawable, int paramInt2)
  {
    Object localObject = new OvalShape();
    ((OvalShape)localObject).resize(paramInt1, paramInt1);
    localObject = new ShapeDrawable((Shape)localObject);
    Paint localPaint = ((ShapeDrawable)localObject).getPaint();
    localPaint.setColor(-1);
    if (paramInt2 == 1)
    {
      localPaint.setStyle(Paint.Style.STROKE);
      localPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
    }
    for (;;)
    {
      paramDrawable = new CombinedDrawable((Drawable)localObject, paramDrawable);
      paramDrawable.setCustomSize(paramInt1, paramInt1);
      return paramDrawable;
      if (paramInt2 == 2) {
        localPaint.setAlpha(0);
      }
    }
  }
  
  public static void createCommonResources(Context paramContext)
  {
    if (dividerPaint == null)
    {
      dividerPaint = new Paint();
      dividerPaint.setStrokeWidth(1.0F);
      avatar_backgroundPaint = new Paint(1);
      checkboxSquare_checkPaint = new Paint(1);
      checkboxSquare_checkPaint.setStyle(Paint.Style.STROKE);
      checkboxSquare_checkPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
      checkboxSquare_eraserPaint = new Paint(1);
      checkboxSquare_eraserPaint.setColor(0);
      checkboxSquare_eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
      checkboxSquare_backgroundPaint = new Paint(1);
      linkSelectionPaint = new Paint();
      paramContext = paramContext.getResources();
      avatar_broadcastDrawable = paramContext.getDrawable(NUM);
      avatar_savedDrawable = paramContext.getDrawable(NUM);
      avatar_photoDrawable = paramContext.getDrawable(NUM);
      applyCommonTheme();
    }
  }
  
  public static void createDialogsResources(Context paramContext)
  {
    createCommonResources(paramContext);
    if (dialogs_namePaint == null)
    {
      paramContext = paramContext.getResources();
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
      dialogs_lockDrawable = paramContext.getDrawable(NUM);
      dialogs_checkDrawable = paramContext.getDrawable(NUM);
      dialogs_halfCheckDrawable = paramContext.getDrawable(NUM);
      dialogs_clockDrawable = paramContext.getDrawable(NUM).mutate();
      dialogs_errorDrawable = paramContext.getDrawable(NUM);
      dialogs_groupDrawable = paramContext.getDrawable(NUM);
      dialogs_broadcastDrawable = paramContext.getDrawable(NUM);
      dialogs_muteDrawable = paramContext.getDrawable(NUM).mutate();
      dialogs_verifiedDrawable = paramContext.getDrawable(NUM);
      dialogs_verifiedCheckDrawable = paramContext.getDrawable(NUM);
      dialogs_mentionDrawable = paramContext.getDrawable(NUM);
      dialogs_botDrawable = paramContext.getDrawable(NUM);
      dialogs_pinnedDrawable = paramContext.getDrawable(NUM);
      applyDialogsTheme();
    }
    dialogs_namePaint.setTextSize(AndroidUtilities.dp(17.0F));
    dialogs_nameEncryptedPaint.setTextSize(AndroidUtilities.dp(17.0F));
    dialogs_messagePaint.setTextSize(AndroidUtilities.dp(16.0F));
    dialogs_messagePrintingPaint.setTextSize(AndroidUtilities.dp(16.0F));
    dialogs_timePaint.setTextSize(AndroidUtilities.dp(13.0F));
    dialogs_countTextPaint.setTextSize(AndroidUtilities.dp(13.0F));
    dialogs_onlinePaint.setTextSize(AndroidUtilities.dp(16.0F));
    dialogs_offlinePaint.setTextSize(AndroidUtilities.dp(16.0F));
  }
  
  public static Drawable createEditTextDrawable(Context paramContext, boolean paramBoolean)
  {
    Object localObject = paramContext.getResources();
    Drawable localDrawable = ((Resources)localObject).getDrawable(NUM).mutate();
    if (paramBoolean)
    {
      paramContext = "dialogInputField";
      localDrawable.setColorFilter(new PorterDuffColorFilter(getColor(paramContext), PorterDuff.Mode.MULTIPLY));
      localObject = ((Resources)localObject).getDrawable(NUM).mutate();
      if (!paramBoolean) {
        break label138;
      }
    }
    label138:
    for (paramContext = "dialogInputFieldActivated";; paramContext = "windowBackgroundWhiteInputFieldActivated")
    {
      ((Drawable)localObject).setColorFilter(new PorterDuffColorFilter(getColor(paramContext), PorterDuff.Mode.MULTIPLY));
      paramContext = new StateListDrawable()
      {
        public boolean selectDrawable(int paramAnonymousInt)
        {
          Drawable localDrawable;
          ColorFilter localColorFilter;
          boolean bool1;
          if (Build.VERSION.SDK_INT < 21)
          {
            localDrawable = Theme.getStateDrawable(this, paramAnonymousInt);
            localColorFilter = null;
            if ((localDrawable instanceof BitmapDrawable))
            {
              localColorFilter = ((BitmapDrawable)localDrawable).getPaint().getColorFilter();
              bool1 = super.selectDrawable(paramAnonymousInt);
              bool2 = bool1;
              if (localColorFilter != null) {
                localDrawable.setColorFilter(localColorFilter);
              }
            }
          }
          for (boolean bool2 = bool1;; bool2 = super.selectDrawable(paramAnonymousInt))
          {
            return bool2;
            if (!(localDrawable instanceof NinePatchDrawable)) {
              break;
            }
            localColorFilter = ((NinePatchDrawable)localDrawable).getPaint().getColorFilter();
            break;
          }
        }
      };
      paramContext.addState(new int[] { 16842910, 16842908 }, (Drawable)localObject);
      paramContext.addState(new int[] { 16842908 }, (Drawable)localObject);
      paramContext.addState(StateSet.WILD_CARD, localDrawable);
      return paramContext;
      paramContext = "windowBackgroundWhiteInputField";
      break;
    }
  }
  
  public static Drawable createEmojiIconSelectorDrawable(Context paramContext, int paramInt1, int paramInt2, int paramInt3)
  {
    Object localObject = paramContext.getResources();
    paramContext = ((Resources)localObject).getDrawable(paramInt1).mutate();
    if (paramInt2 != 0) {
      paramContext.setColorFilter(new PorterDuffColorFilter(paramInt2, PorterDuff.Mode.MULTIPLY));
    }
    Drawable localDrawable = ((Resources)localObject).getDrawable(paramInt1).mutate();
    if (paramInt3 != 0) {
      localDrawable.setColorFilter(new PorterDuffColorFilter(paramInt3, PorterDuff.Mode.MULTIPLY));
    }
    localObject = new StateListDrawable()
    {
      public boolean selectDrawable(int paramAnonymousInt)
      {
        Drawable localDrawable;
        ColorFilter localColorFilter;
        boolean bool1;
        if (Build.VERSION.SDK_INT < 21)
        {
          localDrawable = Theme.getStateDrawable(this, paramAnonymousInt);
          localColorFilter = null;
          if ((localDrawable instanceof BitmapDrawable))
          {
            localColorFilter = ((BitmapDrawable)localDrawable).getPaint().getColorFilter();
            bool1 = super.selectDrawable(paramAnonymousInt);
            bool2 = bool1;
            if (localColorFilter != null) {
              localDrawable.setColorFilter(localColorFilter);
            }
          }
        }
        for (boolean bool2 = bool1;; bool2 = super.selectDrawable(paramAnonymousInt))
        {
          return bool2;
          if (!(localDrawable instanceof NinePatchDrawable)) {
            break;
          }
          localColorFilter = ((NinePatchDrawable)localDrawable).getPaint().getColorFilter();
          break;
        }
      }
    };
    ((StateListDrawable)localObject).setEnterFadeDuration(1);
    ((StateListDrawable)localObject).setExitFadeDuration(200);
    ((StateListDrawable)localObject).addState(new int[] { 16842913 }, localDrawable);
    ((StateListDrawable)localObject).addState(new int[0], paramContext);
    return (Drawable)localObject;
  }
  
  public static void createProfileResources(Context paramContext)
  {
    if (profile_verifiedDrawable == null)
    {
      profile_aboutTextPaint = new TextPaint(1);
      paramContext = paramContext.getResources();
      profile_verifiedDrawable = paramContext.getDrawable(NUM).mutate();
      profile_verifiedCheckDrawable = paramContext.getDrawable(NUM).mutate();
      applyProfileTheme();
    }
    profile_aboutTextPaint.setTextSize(AndroidUtilities.dp(16.0F));
  }
  
  public static Drawable createRoundRectDrawable(int paramInt1, int paramInt2)
  {
    ShapeDrawable localShapeDrawable = new ShapeDrawable(new RoundRectShape(new float[] { paramInt1, paramInt1, paramInt1, paramInt1, paramInt1, paramInt1, paramInt1, paramInt1 }, null, null));
    localShapeDrawable.getPaint().setColor(paramInt2);
    return localShapeDrawable;
  }
  
  public static Drawable createRoundRectDrawableWithIcon(int paramInt1, int paramInt2)
  {
    ShapeDrawable localShapeDrawable = new ShapeDrawable(new RoundRectShape(new float[] { paramInt1, paramInt1, paramInt1, paramInt1, paramInt1, paramInt1, paramInt1, paramInt1 }, null, null));
    localShapeDrawable.getPaint().setColor(-1);
    return new CombinedDrawable(localShapeDrawable, ApplicationLoader.applicationContext.getResources().getDrawable(paramInt2).mutate());
  }
  
  public static Drawable createSelectorDrawable(int paramInt)
  {
    return createSelectorDrawable(paramInt, 1);
  }
  
  public static Drawable createSelectorDrawable(int paramInt1, int paramInt2)
  {
    Object localObject;
    if (Build.VERSION.SDK_INT >= 21)
    {
      localObject = null;
      if (paramInt2 == 1)
      {
        maskPaint.setColor(-1);
        localObject = new Drawable()
        {
          public void draw(Canvas paramAnonymousCanvas)
          {
            Rect localRect = getBounds();
            paramAnonymousCanvas.drawCircle(localRect.centerX(), localRect.centerY(), AndroidUtilities.dp(18.0F), Theme.maskPaint);
          }
          
          public int getOpacity()
          {
            return 0;
          }
          
          public void setAlpha(int paramAnonymousInt) {}
          
          public void setColorFilter(ColorFilter paramAnonymousColorFilter) {}
        };
        localObject = new RippleDrawable(new ColorStateList(new int[][] { StateSet.WILD_CARD }, new int[] { paramInt1 }), null, (Drawable)localObject);
      }
    }
    for (;;)
    {
      return (Drawable)localObject;
      if (paramInt2 != 2) {
        break;
      }
      localObject = new ColorDrawable(-1);
      break;
      localObject = new StateListDrawable();
      ColorDrawable localColorDrawable = new ColorDrawable(paramInt1);
      ((StateListDrawable)localObject).addState(new int[] { 16842919 }, localColorDrawable);
      localColorDrawable = new ColorDrawable(paramInt1);
      ((StateListDrawable)localObject).addState(new int[] { 16842913 }, localColorDrawable);
      ((StateListDrawable)localObject).addState(StateSet.WILD_CARD, new ColorDrawable(0));
    }
  }
  
  public static Drawable createSimpleSelectorCircleDrawable(int paramInt1, int paramInt2, int paramInt3)
  {
    Object localObject = new OvalShape();
    ((OvalShape)localObject).resize(paramInt1, paramInt1);
    ShapeDrawable localShapeDrawable1 = new ShapeDrawable((Shape)localObject);
    localShapeDrawable1.getPaint().setColor(paramInt2);
    ShapeDrawable localShapeDrawable2 = new ShapeDrawable((Shape)localObject);
    if (Build.VERSION.SDK_INT >= 21)
    {
      localShapeDrawable2.getPaint().setColor(-1);
      localObject = new RippleDrawable(new ColorStateList(new int[][] { StateSet.WILD_CARD }, new int[] { paramInt3 }), localShapeDrawable1, localShapeDrawable2);
    }
    for (;;)
    {
      return (Drawable)localObject;
      localShapeDrawable2.getPaint().setColor(paramInt3);
      localObject = new StateListDrawable();
      ((StateListDrawable)localObject).addState(new int[] { 16842919 }, localShapeDrawable2);
      ((StateListDrawable)localObject).addState(new int[] { 16842908 }, localShapeDrawable2);
      ((StateListDrawable)localObject).addState(StateSet.WILD_CARD, localShapeDrawable1);
    }
  }
  
  public static Drawable createSimpleSelectorDrawable(Context paramContext, int paramInt1, int paramInt2, int paramInt3)
  {
    Object localObject = paramContext.getResources();
    paramContext = ((Resources)localObject).getDrawable(paramInt1).mutate();
    if (paramInt2 != 0) {
      paramContext.setColorFilter(new PorterDuffColorFilter(paramInt2, PorterDuff.Mode.MULTIPLY));
    }
    Drawable localDrawable = ((Resources)localObject).getDrawable(paramInt1).mutate();
    if (paramInt3 != 0) {
      localDrawable.setColorFilter(new PorterDuffColorFilter(paramInt3, PorterDuff.Mode.MULTIPLY));
    }
    localObject = new StateListDrawable()
    {
      public boolean selectDrawable(int paramAnonymousInt)
      {
        Drawable localDrawable;
        ColorFilter localColorFilter;
        boolean bool1;
        if (Build.VERSION.SDK_INT < 21)
        {
          localDrawable = Theme.getStateDrawable(this, paramAnonymousInt);
          localColorFilter = null;
          if ((localDrawable instanceof BitmapDrawable))
          {
            localColorFilter = ((BitmapDrawable)localDrawable).getPaint().getColorFilter();
            bool1 = super.selectDrawable(paramAnonymousInt);
            bool2 = bool1;
            if (localColorFilter != null) {
              localDrawable.setColorFilter(localColorFilter);
            }
          }
        }
        for (boolean bool2 = bool1;; bool2 = super.selectDrawable(paramAnonymousInt))
        {
          return bool2;
          if (!(localDrawable instanceof NinePatchDrawable)) {
            break;
          }
          localColorFilter = ((NinePatchDrawable)localDrawable).getPaint().getColorFilter();
          break;
        }
      }
    };
    ((StateListDrawable)localObject).addState(new int[] { 16842919 }, localDrawable);
    ((StateListDrawable)localObject).addState(new int[] { 16842913 }, localDrawable);
    ((StateListDrawable)localObject).addState(StateSet.WILD_CARD, paramContext);
    return (Drawable)localObject;
  }
  
  public static Drawable createSimpleSelectorRoundRectDrawable(int paramInt1, int paramInt2, int paramInt3)
  {
    ShapeDrawable localShapeDrawable1 = new ShapeDrawable(new RoundRectShape(new float[] { paramInt1, paramInt1, paramInt1, paramInt1, paramInt1, paramInt1, paramInt1, paramInt1 }, null, null));
    localShapeDrawable1.getPaint().setColor(paramInt2);
    ShapeDrawable localShapeDrawable2 = new ShapeDrawable(new RoundRectShape(new float[] { paramInt1, paramInt1, paramInt1, paramInt1, paramInt1, paramInt1, paramInt1, paramInt1 }, null, null));
    localShapeDrawable2.getPaint().setColor(paramInt3);
    StateListDrawable localStateListDrawable = new StateListDrawable();
    localStateListDrawable.addState(new int[] { 16842919 }, localShapeDrawable2);
    localStateListDrawable.addState(new int[] { 16842913 }, localShapeDrawable2);
    localStateListDrawable.addState(StateSet.WILD_CARD, localShapeDrawable1);
    return localStateListDrawable;
  }
  
  public static boolean deleteTheme(ThemeInfo paramThemeInfo)
  {
    boolean bool;
    if (paramThemeInfo.pathToFile == null) {
      bool = false;
    }
    for (;;)
    {
      return bool;
      bool = false;
      if (currentTheme == paramThemeInfo)
      {
        applyTheme(defaultTheme, true, false, false);
        bool = true;
      }
      otherThemes.remove(paramThemeInfo);
      themesDict.remove(paramThemeInfo.name);
      themes.remove(paramThemeInfo);
      new File(paramThemeInfo.pathToFile).delete();
      saveOtherThemes();
    }
  }
  
  public static void destroyResources()
  {
    for (int i = 0; i < chat_attachButtonDrawables.length; i++) {
      if (chat_attachButtonDrawables[i] != null) {
        chat_attachButtonDrawables[i].setCallback(null);
      }
    }
  }
  
  /* Error */
  public static File getAssetFile(String paramString)
  {
    // Byte code:
    //   0: new 2802	java/io/File
    //   3: dup
    //   4: invokestatic 2815	org/telegram/messenger/ApplicationLoader:getFilesDirFixed	()Ljava/io/File;
    //   7: aload_0
    //   8: invokespecial 2818	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   11: astore_1
    //   12: getstatic 2256	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   15: invokevirtual 3327	android/content/Context:getAssets	()Landroid/content/res/AssetManager;
    //   18: aload_0
    //   19: invokevirtual 3333	android/content/res/AssetManager:open	(Ljava/lang/String;)Ljava/io/InputStream;
    //   22: astore_2
    //   23: aload_2
    //   24: invokevirtual 3338	java/io/InputStream:available	()I
    //   27: i2l
    //   28: lstore_3
    //   29: aload_2
    //   30: invokevirtual 3341	java/io/InputStream:close	()V
    //   33: aload_1
    //   34: invokevirtual 3344	java/io/File:exists	()Z
    //   37: ifeq +18 -> 55
    //   40: lload_3
    //   41: lconst_0
    //   42: lcmp
    //   43: ifeq +47 -> 90
    //   46: aload_1
    //   47: invokevirtual 3346	java/io/File:length	()J
    //   50: lload_3
    //   51: lcmp
    //   52: ifeq +38 -> 90
    //   55: aconst_null
    //   56: astore 5
    //   58: aconst_null
    //   59: astore_2
    //   60: getstatic 2256	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   63: invokevirtual 3327	android/content/Context:getAssets	()Landroid/content/res/AssetManager;
    //   66: aload_0
    //   67: invokevirtual 3333	android/content/res/AssetManager:open	(Ljava/lang/String;)Ljava/io/InputStream;
    //   70: astore_0
    //   71: aload_0
    //   72: astore_2
    //   73: aload_0
    //   74: astore 5
    //   76: aload_0
    //   77: aload_1
    //   78: invokestatic 3349	org/telegram/messenger/AndroidUtilities:copyFile	(Ljava/io/InputStream;Ljava/io/File;)Z
    //   81: pop
    //   82: aload_0
    //   83: ifnull +7 -> 90
    //   86: aload_0
    //   87: invokevirtual 3341	java/io/InputStream:close	()V
    //   90: aload_1
    //   91: areturn
    //   92: astore_2
    //   93: lconst_0
    //   94: lstore_3
    //   95: aload_2
    //   96: invokestatic 2301	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   99: goto -66 -> 33
    //   102: astore_0
    //   103: aload_2
    //   104: astore 5
    //   106: aload_0
    //   107: invokestatic 2301	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   110: aload_2
    //   111: ifnull -21 -> 90
    //   114: aload_2
    //   115: invokevirtual 3341	java/io/InputStream:close	()V
    //   118: goto -28 -> 90
    //   121: astore_0
    //   122: goto -32 -> 90
    //   125: astore_0
    //   126: aload 5
    //   128: ifnull +8 -> 136
    //   131: aload 5
    //   133: invokevirtual 3341	java/io/InputStream:close	()V
    //   136: aload_0
    //   137: athrow
    //   138: astore_0
    //   139: goto -49 -> 90
    //   142: astore_2
    //   143: goto -7 -> 136
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	146	0	paramString	String
    //   11	80	1	localFile	File
    //   22	51	2	localObject1	Object
    //   92	23	2	localException1	Exception
    //   142	1	2	localException2	Exception
    //   28	67	3	l	long
    //   56	76	5	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   12	33	92	java/lang/Exception
    //   60	71	102	java/lang/Exception
    //   76	82	102	java/lang/Exception
    //   114	118	121	java/lang/Exception
    //   60	71	125	finally
    //   76	82	125	finally
    //   106	110	125	finally
    //   86	90	138	java/lang/Exception
    //   131	136	142	java/lang/Exception
  }
  
  private static long getAutoNightSwitchThemeDelay()
  {
    long l1 = 12000L;
    long l2 = SystemClock.uptimeMillis();
    if (Math.abs(lastThemeSwitchTime - l2) >= 12000L) {
      l1 = 1800L;
    }
    return l1;
  }
  
  public static Drawable getCachedWallpaper()
  {
    synchronized (wallpaperSync)
    {
      if (themedWallpaper != null)
      {
        localDrawable = themedWallpaper;
        return localDrawable;
      }
      Drawable localDrawable = wallpaper;
    }
  }
  
  public static int getColor(String paramString)
  {
    return getColor(paramString, null);
  }
  
  public static int getColor(String paramString, boolean[] paramArrayOfBoolean)
  {
    Integer localInteger = (Integer)currentColors.get(paramString);
    Object localObject = localInteger;
    int i;
    if (localInteger == null)
    {
      localObject = (String)fallbackKeys.get(paramString);
      if (localObject != null) {
        localInteger = (Integer)currentColors.get(localObject);
      }
      localObject = localInteger;
      if (localInteger == null)
      {
        if (paramArrayOfBoolean != null) {
          paramArrayOfBoolean[0] = true;
        }
        if (paramString.equals("chat_serviceBackground")) {
          i = serviceMessageColor;
        }
      }
    }
    for (;;)
    {
      return i;
      if (paramString.equals("chat_serviceBackgroundSelected"))
      {
        i = serviceSelectedMessageColor;
      }
      else
      {
        i = getDefaultColor(paramString);
        continue;
        i = ((Integer)localObject).intValue();
      }
    }
  }
  
  public static Integer getColorOrNull(String paramString)
  {
    Integer localInteger1 = (Integer)currentColors.get(paramString);
    Integer localInteger2 = localInteger1;
    if (localInteger1 == null)
    {
      if ((String)fallbackKeys.get(paramString) != null) {
        localInteger1 = (Integer)currentColors.get(paramString);
      }
      localInteger2 = localInteger1;
      if (localInteger1 == null) {
        localInteger2 = (Integer)defaultColors.get(paramString);
      }
    }
    return localInteger2;
  }
  
  public static Drawable getCurrentHolidayDrawable()
  {
    int i;
    int j;
    if (System.currentTimeMillis() - lastHolidayCheckTime >= 60000L)
    {
      lastHolidayCheckTime = System.currentTimeMillis();
      Calendar localCalendar = Calendar.getInstance();
      localCalendar.setTimeInMillis(System.currentTimeMillis());
      i = localCalendar.get(2);
      j = localCalendar.get(5);
      int k = localCalendar.get(12);
      m = localCalendar.get(11);
      if ((i != 0) || (j != 1) || (k > 10) || (m != 0)) {
        break label158;
      }
      canStartHolidayAnimation = true;
      if (dialogs_holidayDrawable == null) {
        if (i == 11) {
          if (!BuildVars.DEBUG_PRIVATE_VERSION) {
            break label165;
          }
        }
      }
    }
    label158:
    label165:
    for (int m = 29;; m = 31)
    {
      if ((j < m) || (j > 31))
      {
        if ((i != 0) || (j != 1)) {}
      }
      else
      {
        dialogs_holidayDrawable = ApplicationLoader.applicationContext.getResources().getDrawable(NUM);
        dialogs_holidayDrawableOffsetX = -AndroidUtilities.dp(3.0F);
        dialogs_holidayDrawableOffsetY = 0;
      }
      return dialogs_holidayDrawable;
      canStartHolidayAnimation = false;
      break;
    }
  }
  
  public static int getCurrentHolidayDrawableXOffset()
  {
    return dialogs_holidayDrawableOffsetX;
  }
  
  public static int getCurrentHolidayDrawableYOffset()
  {
    return dialogs_holidayDrawableOffsetY;
  }
  
  public static ThemeInfo getCurrentNightTheme()
  {
    return currentNightTheme;
  }
  
  public static String getCurrentNightThemeName()
  {
    Object localObject;
    if (currentNightTheme == null) {
      localObject = "";
    }
    for (;;)
    {
      return (String)localObject;
      String str = currentNightTheme.getName();
      localObject = str;
      if (str.toLowerCase().endsWith(".attheme")) {
        localObject = str.substring(0, str.lastIndexOf('.'));
      }
    }
  }
  
  public static ThemeInfo getCurrentTheme()
  {
    if (currentDayTheme != null) {}
    for (ThemeInfo localThemeInfo = currentDayTheme;; localThemeInfo = defaultTheme) {
      return localThemeInfo;
    }
  }
  
  public static String getCurrentThemeName()
  {
    String str1 = currentDayTheme.getName();
    String str2 = str1;
    if (str1.toLowerCase().endsWith(".attheme")) {
      str2 = str1.substring(0, str1.lastIndexOf('.'));
    }
    return str2;
  }
  
  public static int getDefaultColor(String paramString)
  {
    Integer localInteger = (Integer)defaultColors.get(paramString);
    int i;
    if (localInteger == null) {
      if (paramString.equals("chats_menuTopShadow")) {
        i = 0;
      }
    }
    for (;;)
    {
      return i;
      i = -65536;
      continue;
      i = localInteger.intValue();
    }
  }
  
  public static HashMap<String, Integer> getDefaultColors()
  {
    return defaultColors;
  }
  
  public static Drawable getRoundRectSelectorDrawable()
  {
    Drawable localDrawable;
    Object localObject;
    if (Build.VERSION.SDK_INT >= 21)
    {
      localDrawable = createRoundRectDrawable(AndroidUtilities.dp(3.0F), -1);
      localObject = StateSet.WILD_CARD;
      int i = getColor("dialogButtonSelector");
      localObject = new RippleDrawable(new ColorStateList(new int[][] { localObject }, new int[] { i }), null, localDrawable);
    }
    for (;;)
    {
      return (Drawable)localObject;
      localObject = new StateListDrawable();
      localDrawable = createRoundRectDrawable(AndroidUtilities.dp(3.0F), getColor("dialogButtonSelector"));
      ((StateListDrawable)localObject).addState(new int[] { 16842919 }, localDrawable);
      localDrawable = createRoundRectDrawable(AndroidUtilities.dp(3.0F), getColor("dialogButtonSelector"));
      ((StateListDrawable)localObject).addState(new int[] { 16842913 }, localDrawable);
      ((StateListDrawable)localObject).addState(StateSet.WILD_CARD, new ColorDrawable(0));
    }
  }
  
  public static int getSelectedColor()
  {
    return selectedColor;
  }
  
  public static Drawable getSelectorDrawable(boolean paramBoolean)
  {
    ColorDrawable localColorDrawable;
    Object localObject;
    int i;
    if (paramBoolean) {
      if (Build.VERSION.SDK_INT >= 21)
      {
        localColorDrawable = new ColorDrawable(-1);
        localObject = StateSet.WILD_CARD;
        i = getColor("listSelectorSDK21");
        localObject = new RippleDrawable(new ColorStateList(new int[][] { localObject }, new int[] { i }), new ColorDrawable(getColor("windowBackgroundWhite")), localColorDrawable);
      }
    }
    for (;;)
    {
      return (Drawable)localObject;
      i = getColor("listSelectorSDK21");
      localObject = new StateListDrawable();
      localColorDrawable = new ColorDrawable(i);
      ((StateListDrawable)localObject).addState(new int[] { 16842919 }, localColorDrawable);
      localColorDrawable = new ColorDrawable(i);
      ((StateListDrawable)localObject).addState(new int[] { 16842913 }, localColorDrawable);
      ((StateListDrawable)localObject).addState(StateSet.WILD_CARD, new ColorDrawable(getColor("windowBackgroundWhite")));
      continue;
      localObject = createSelectorDrawable(getColor("listSelectorSDK21"), 2);
    }
  }
  
  public static int getServiceMessageColor()
  {
    Integer localInteger = (Integer)currentColors.get("chat_serviceBackground");
    if (localInteger == null) {}
    for (int i = serviceMessageColor;; i = localInteger.intValue()) {
      return i;
    }
  }
  
  @SuppressLint({"PrivateApi"})
  private static Drawable getStateDrawable(Drawable paramDrawable, int paramInt)
  {
    if (StateListDrawable_getStateDrawableMethod == null) {}
    try
    {
      StateListDrawable_getStateDrawableMethod = StateListDrawable.class.getDeclaredMethod("getStateDrawable", new Class[] { Integer.TYPE });
      if (StateListDrawable_getStateDrawableMethod == null) {
        paramDrawable = null;
      }
      for (;;)
      {
        return paramDrawable;
        try
        {
          paramDrawable = (Drawable)StateListDrawable_getStateDrawableMethod.invoke(paramDrawable, new Object[] { Integer.valueOf(paramInt) });
        }
        catch (Exception paramDrawable)
        {
          paramDrawable = null;
        }
      }
    }
    catch (Throwable localThrowable)
    {
      for (;;) {}
    }
  }
  
  /* Error */
  private static HashMap<String, Integer> getThemeFileValues(File paramFile, String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: aconst_null
    //   3: astore_3
    //   4: new 1963	java/util/HashMap
    //   7: dup
    //   8: invokespecial 1964	java/util/HashMap:<init>	()V
    //   11: astore 4
    //   13: aload_2
    //   14: astore 5
    //   16: sipush 1024
    //   19: newarray <illegal type>
    //   21: astore 6
    //   23: iconst_0
    //   24: istore 7
    //   26: aload_0
    //   27: astore 8
    //   29: aload_1
    //   30: ifnull +12 -> 42
    //   33: aload_2
    //   34: astore 5
    //   36: aload_1
    //   37: invokestatic 3444	org/telegram/ui/ActionBar/Theme:getAssetFile	(Ljava/lang/String;)Ljava/io/File;
    //   40: astore 8
    //   42: aload_2
    //   43: astore 5
    //   45: new 3446	java/io/FileInputStream
    //   48: astore_0
    //   49: aload_2
    //   50: astore 5
    //   52: aload_0
    //   53: aload 8
    //   55: invokespecial 3449	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   58: iconst_0
    //   59: istore 9
    //   61: iconst_m1
    //   62: putstatic 2424	org/telegram/ui/ActionBar/Theme:themedWallpaperFileOffset	I
    //   65: iload 7
    //   67: istore 10
    //   69: aload_0
    //   70: aload 6
    //   72: invokevirtual 3453	java/io/FileInputStream:read	([B)I
    //   75: istore 11
    //   77: iload 11
    //   79: iconst_m1
    //   80: if_icmpeq +101 -> 181
    //   83: iconst_0
    //   84: istore 12
    //   86: iconst_0
    //   87: istore 13
    //   89: iload 10
    //   91: istore 7
    //   93: iload 9
    //   95: istore 14
    //   97: iload 13
    //   99: iload 11
    //   101: if_icmpge +73 -> 174
    //   104: iload 7
    //   106: istore 14
    //   108: iload 12
    //   110: istore 15
    //   112: aload 6
    //   114: iload 13
    //   116: baload
    //   117: bipush 10
    //   119: if_icmpne +164 -> 283
    //   122: iload 13
    //   124: iload 12
    //   126: isub
    //   127: iconst_1
    //   128: iadd
    //   129: istore 16
    //   131: new 1947	java/lang/String
    //   134: astore 5
    //   136: aload 5
    //   138: aload 6
    //   140: iload 12
    //   142: iload 16
    //   144: iconst_1
    //   145: isub
    //   146: ldc_w 3455
    //   149: invokespecial 3458	java/lang/String:<init>	([BIILjava/lang/String;)V
    //   152: aload 5
    //   154: ldc_w 3460
    //   157: invokevirtual 3463	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   160: ifeq +32 -> 192
    //   163: iload 7
    //   165: iload 16
    //   167: iadd
    //   168: putstatic 2424	org/telegram/ui/ActionBar/Theme:themedWallpaperFileOffset	I
    //   171: iconst_1
    //   172: istore 14
    //   174: iload 10
    //   176: iload 7
    //   178: if_icmpne +147 -> 325
    //   181: aload_0
    //   182: ifnull +7 -> 189
    //   185: aload_0
    //   186: invokevirtual 3464	java/io/FileInputStream:close	()V
    //   189: aload 4
    //   191: areturn
    //   192: aload 5
    //   194: bipush 61
    //   196: invokevirtual 3467	java/lang/String:indexOf	(I)I
    //   199: istore 14
    //   201: iload 14
    //   203: iconst_m1
    //   204: if_icmpeq +65 -> 269
    //   207: aload 5
    //   209: iconst_0
    //   210: iload 14
    //   212: invokevirtual 3404	java/lang/String:substring	(II)Ljava/lang/String;
    //   215: astore_1
    //   216: aload 5
    //   218: iload 14
    //   220: iconst_1
    //   221: iadd
    //   222: invokevirtual 3470	java/lang/String:substring	(I)Ljava/lang/String;
    //   225: astore 8
    //   227: aload 8
    //   229: invokevirtual 3471	java/lang/String:length	()I
    //   232: ifle +80 -> 312
    //   235: aload 8
    //   237: iconst_0
    //   238: invokevirtual 3475	java/lang/String:charAt	(I)C
    //   241: istore 14
    //   243: iload 14
    //   245: bipush 35
    //   247: if_icmpne +65 -> 312
    //   250: aload 8
    //   252: invokestatic 3480	android/graphics/Color:parseColor	(Ljava/lang/String;)I
    //   255: istore 14
    //   257: aload 4
    //   259: aload_1
    //   260: iload 14
    //   262: invokestatic 1974	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   265: invokevirtual 1978	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   268: pop
    //   269: iload 12
    //   271: iload 16
    //   273: iadd
    //   274: istore 15
    //   276: iload 7
    //   278: iload 16
    //   280: iadd
    //   281: istore 14
    //   283: iinc 13 1
    //   286: iload 14
    //   288: istore 7
    //   290: iload 15
    //   292: istore 12
    //   294: goto -201 -> 93
    //   297: astore 5
    //   299: aload 8
    //   301: invokestatic 3485	org/telegram/messenger/Utilities:parseInt	(Ljava/lang/String;)Ljava/lang/Integer;
    //   304: invokevirtual 2471	java/lang/Integer:intValue	()I
    //   307: istore 14
    //   309: goto -52 -> 257
    //   312: aload 8
    //   314: invokestatic 3485	org/telegram/messenger/Utilities:parseInt	(Ljava/lang/String;)Ljava/lang/Integer;
    //   317: invokevirtual 2471	java/lang/Integer:intValue	()I
    //   320: istore 14
    //   322: goto -65 -> 257
    //   325: aload_0
    //   326: invokevirtual 3489	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   329: iload 7
    //   331: i2l
    //   332: invokevirtual 3495	java/nio/channels/FileChannel:position	(J)Ljava/nio/channels/FileChannel;
    //   335: pop
    //   336: iload 14
    //   338: istore 9
    //   340: iload 14
    //   342: ifeq -277 -> 65
    //   345: goto -164 -> 181
    //   348: astore_0
    //   349: aload_0
    //   350: invokestatic 2301	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   353: goto -164 -> 189
    //   356: astore_1
    //   357: aload_3
    //   358: astore_0
    //   359: aload_0
    //   360: astore 5
    //   362: aload_1
    //   363: invokestatic 2301	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   366: aload_0
    //   367: ifnull -178 -> 189
    //   370: aload_0
    //   371: invokevirtual 3464	java/io/FileInputStream:close	()V
    //   374: goto -185 -> 189
    //   377: astore_0
    //   378: aload_0
    //   379: invokestatic 2301	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   382: goto -193 -> 189
    //   385: astore_1
    //   386: aload 5
    //   388: ifnull +8 -> 396
    //   391: aload 5
    //   393: invokevirtual 3464	java/io/FileInputStream:close	()V
    //   396: aload_1
    //   397: athrow
    //   398: astore_0
    //   399: aload_0
    //   400: invokestatic 2301	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   403: goto -7 -> 396
    //   406: astore_1
    //   407: aload_0
    //   408: astore 5
    //   410: goto -24 -> 386
    //   413: astore_1
    //   414: goto -55 -> 359
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	417	0	paramFile	File
    //   0	417	1	paramString	String
    //   1	49	2	localObject1	Object
    //   3	355	3	localObject2	Object
    //   11	247	4	localHashMap	HashMap
    //   14	203	5	localObject3	Object
    //   297	1	5	localException	Exception
    //   360	49	5	localFile	File
    //   21	118	6	arrayOfByte	byte[]
    //   24	306	7	i	int
    //   27	286	8	localObject4	Object
    //   59	280	9	j	int
    //   67	112	10	k	int
    //   75	27	11	m	int
    //   84	209	12	n	int
    //   87	197	13	i1	int
    //   95	246	14	i2	int
    //   110	181	15	i3	int
    //   129	152	16	i4	int
    // Exception table:
    //   from	to	target	type
    //   250	257	297	java/lang/Exception
    //   185	189	348	java/lang/Exception
    //   16	23	356	java/lang/Throwable
    //   36	42	356	java/lang/Throwable
    //   45	49	356	java/lang/Throwable
    //   52	58	356	java/lang/Throwable
    //   370	374	377	java/lang/Exception
    //   16	23	385	finally
    //   36	42	385	finally
    //   45	49	385	finally
    //   52	58	385	finally
    //   362	366	385	finally
    //   391	396	398	java/lang/Exception
    //   61	65	406	finally
    //   69	77	406	finally
    //   131	171	406	finally
    //   192	201	406	finally
    //   207	243	406	finally
    //   250	257	406	finally
    //   257	269	406	finally
    //   299	309	406	finally
    //   312	322	406	finally
    //   325	336	406	finally
    //   61	65	413	java/lang/Throwable
    //   69	77	413	java/lang/Throwable
    //   131	171	413	java/lang/Throwable
    //   192	201	413	java/lang/Throwable
    //   207	243	413	java/lang/Throwable
    //   250	257	413	java/lang/Throwable
    //   257	269	413	java/lang/Throwable
    //   299	309	413	java/lang/Throwable
    //   312	322	413	java/lang/Throwable
    //   325	336	413	java/lang/Throwable
  }
  
  public static Drawable getThemedDrawable(Context paramContext, int paramInt, String paramString)
  {
    paramContext = paramContext.getResources().getDrawable(paramInt).mutate();
    paramContext.setColorFilter(new PorterDuffColorFilter(getColor(paramString), PorterDuff.Mode.MULTIPLY));
    return paramContext;
  }
  
  /* Error */
  public static Drawable getThemedWallpaper(boolean paramBoolean)
  {
    // Byte code:
    //   0: getstatic 2221	org/telegram/ui/ActionBar/Theme:currentColors	Ljava/util/HashMap;
    //   3: ldc_w 1200
    //   6: invokevirtual 2316	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   9: checkcast 1970	java/lang/Integer
    //   12: astore_1
    //   13: aload_1
    //   14: ifnull +17 -> 31
    //   17: new 3299	android/graphics/drawable/ColorDrawable
    //   20: dup
    //   21: aload_1
    //   22: invokevirtual 2471	java/lang/Integer:intValue	()I
    //   25: invokespecial 3300	android/graphics/drawable/ColorDrawable:<init>	(I)V
    //   28: astore_1
    //   29: aload_1
    //   30: areturn
    //   31: getstatic 2424	org/telegram/ui/ActionBar/Theme:themedWallpaperFileOffset	I
    //   34: ifle +260 -> 294
    //   37: getstatic 2231	org/telegram/ui/ActionBar/Theme:currentTheme	Lorg/telegram/ui/ActionBar/Theme$ThemeInfo;
    //   40: getfield 2777	org/telegram/ui/ActionBar/Theme$ThemeInfo:pathToFile	Ljava/lang/String;
    //   43: ifnonnull +12 -> 55
    //   46: getstatic 2231	org/telegram/ui/ActionBar/Theme:currentTheme	Lorg/telegram/ui/ActionBar/Theme$ThemeInfo;
    //   49: getfield 2244	org/telegram/ui/ActionBar/Theme$ThemeInfo:assetName	Ljava/lang/String;
    //   52: ifnull +242 -> 294
    //   55: aconst_null
    //   56: astore_2
    //   57: aconst_null
    //   58: astore_3
    //   59: aload_2
    //   60: astore_1
    //   61: getstatic 2231	org/telegram/ui/ActionBar/Theme:currentTheme	Lorg/telegram/ui/ActionBar/Theme$ThemeInfo;
    //   64: getfield 2244	org/telegram/ui/ActionBar/Theme$ThemeInfo:assetName	Ljava/lang/String;
    //   67: ifnull +133 -> 200
    //   70: aload_2
    //   71: astore_1
    //   72: getstatic 2231	org/telegram/ui/ActionBar/Theme:currentTheme	Lorg/telegram/ui/ActionBar/Theme$ThemeInfo;
    //   75: getfield 2244	org/telegram/ui/ActionBar/Theme$ThemeInfo:assetName	Ljava/lang/String;
    //   78: invokestatic 3444	org/telegram/ui/ActionBar/Theme:getAssetFile	(Ljava/lang/String;)Ljava/io/File;
    //   81: astore 4
    //   83: aload_2
    //   84: astore_1
    //   85: new 3446	java/io/FileInputStream
    //   88: astore 5
    //   90: aload_2
    //   91: astore_1
    //   92: aload 5
    //   94: aload 4
    //   96: invokespecial 3449	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   99: aload 5
    //   101: invokevirtual 3489	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   104: getstatic 2424	org/telegram/ui/ActionBar/Theme:themedWallpaperFileOffset	I
    //   107: i2l
    //   108: invokevirtual 3495	java/nio/channels/FileChannel:position	(J)Ljava/nio/channels/FileChannel;
    //   111: pop
    //   112: new 3501	android/graphics/BitmapFactory$Options
    //   115: astore_1
    //   116: aload_1
    //   117: invokespecial 3502	android/graphics/BitmapFactory$Options:<init>	()V
    //   120: iconst_1
    //   121: istore 6
    //   123: iconst_1
    //   124: istore 7
    //   126: iload_0
    //   127: ifeq +93 -> 220
    //   130: aload_1
    //   131: iconst_1
    //   132: putfield 3505	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   135: aload_1
    //   136: getfield 3508	android/graphics/BitmapFactory$Options:outWidth	I
    //   139: i2f
    //   140: fstore 8
    //   142: aload_1
    //   143: getfield 3511	android/graphics/BitmapFactory$Options:outHeight	I
    //   146: i2f
    //   147: fstore 9
    //   149: ldc_w 3512
    //   152: invokestatic 3071	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   155: istore 10
    //   157: fload 8
    //   159: iload 10
    //   161: i2f
    //   162: fcmpl
    //   163: ifgt +16 -> 179
    //   166: iload 7
    //   168: istore 6
    //   170: fload 9
    //   172: iload 10
    //   174: i2f
    //   175: fcmpl
    //   176: ifle +44 -> 220
    //   179: iload 7
    //   181: iconst_2
    //   182: imul
    //   183: istore 7
    //   185: fload 8
    //   187: fconst_2
    //   188: fdiv
    //   189: fstore 8
    //   191: fload 9
    //   193: fconst_2
    //   194: fdiv
    //   195: fstore 9
    //   197: goto -40 -> 157
    //   200: aload_2
    //   201: astore_1
    //   202: new 2802	java/io/File
    //   205: dup
    //   206: getstatic 2231	org/telegram/ui/ActionBar/Theme:currentTheme	Lorg/telegram/ui/ActionBar/Theme$ThemeInfo;
    //   209: getfield 2777	org/telegram/ui/ActionBar/Theme$ThemeInfo:pathToFile	Ljava/lang/String;
    //   212: invokespecial 2803	java/io/File:<init>	(Ljava/lang/String;)V
    //   215: astore 4
    //   217: goto -134 -> 83
    //   220: aload_1
    //   221: iconst_0
    //   222: putfield 3505	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   225: aload_1
    //   226: iload 6
    //   228: putfield 3515	android/graphics/BitmapFactory$Options:inSampleSize	I
    //   231: aload 5
    //   233: aconst_null
    //   234: aload_1
    //   235: invokestatic 3521	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   238: astore_1
    //   239: aload_1
    //   240: ifnull +44 -> 284
    //   243: new 3138	android/graphics/drawable/BitmapDrawable
    //   246: astore 4
    //   248: aload 4
    //   250: aload_1
    //   251: invokespecial 3139	android/graphics/drawable/BitmapDrawable:<init>	(Landroid/graphics/Bitmap;)V
    //   254: aload 4
    //   256: astore_1
    //   257: aload 5
    //   259: ifnull -230 -> 29
    //   262: aload 5
    //   264: invokevirtual 3464	java/io/FileInputStream:close	()V
    //   267: aload 4
    //   269: astore_1
    //   270: goto -241 -> 29
    //   273: astore_1
    //   274: aload_1
    //   275: invokestatic 2301	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   278: aload 4
    //   280: astore_1
    //   281: goto -252 -> 29
    //   284: aload 5
    //   286: ifnull +8 -> 294
    //   289: aload 5
    //   291: invokevirtual 3464	java/io/FileInputStream:close	()V
    //   294: aconst_null
    //   295: astore_1
    //   296: goto -267 -> 29
    //   299: astore_1
    //   300: aload_1
    //   301: invokestatic 2301	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   304: goto -10 -> 294
    //   307: astore 4
    //   309: aload_3
    //   310: astore 5
    //   312: aload 5
    //   314: astore_1
    //   315: aload 4
    //   317: invokestatic 2301	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   320: aload 5
    //   322: ifnull -28 -> 294
    //   325: aload 5
    //   327: invokevirtual 3464	java/io/FileInputStream:close	()V
    //   330: goto -36 -> 294
    //   333: astore_1
    //   334: aload_1
    //   335: invokestatic 2301	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   338: goto -44 -> 294
    //   341: astore 5
    //   343: aload_1
    //   344: astore 4
    //   346: aload 4
    //   348: ifnull +8 -> 356
    //   351: aload 4
    //   353: invokevirtual 3464	java/io/FileInputStream:close	()V
    //   356: aload 5
    //   358: athrow
    //   359: astore_1
    //   360: aload_1
    //   361: invokestatic 2301	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   364: goto -8 -> 356
    //   367: astore_1
    //   368: aload 5
    //   370: astore 4
    //   372: aload_1
    //   373: astore 5
    //   375: goto -29 -> 346
    //   378: astore 4
    //   380: goto -68 -> 312
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	383	0	paramBoolean	boolean
    //   12	258	1	localObject1	Object
    //   273	2	1	localException1	Exception
    //   280	16	1	localObject2	Object
    //   299	2	1	localException2	Exception
    //   314	1	1	localObject3	Object
    //   333	11	1	localException3	Exception
    //   359	2	1	localException4	Exception
    //   367	6	1	localObject4	Object
    //   56	145	2	localObject5	Object
    //   58	252	3	localObject6	Object
    //   81	198	4	localObject7	Object
    //   307	9	4	localThrowable1	Throwable
    //   344	27	4	localObject8	Object
    //   378	1	4	localThrowable2	Throwable
    //   88	238	5	localObject9	Object
    //   341	28	5	localObject10	Object
    //   373	1	5	localObject11	Object
    //   121	106	6	i	int
    //   124	60	7	j	int
    //   140	50	8	f1	float
    //   147	49	9	f2	float
    //   155	18	10	k	int
    // Exception table:
    //   from	to	target	type
    //   262	267	273	java/lang/Exception
    //   289	294	299	java/lang/Exception
    //   61	70	307	java/lang/Throwable
    //   72	83	307	java/lang/Throwable
    //   85	90	307	java/lang/Throwable
    //   92	99	307	java/lang/Throwable
    //   202	217	307	java/lang/Throwable
    //   325	330	333	java/lang/Exception
    //   61	70	341	finally
    //   72	83	341	finally
    //   85	90	341	finally
    //   92	99	341	finally
    //   202	217	341	finally
    //   315	320	341	finally
    //   351	356	359	java/lang/Exception
    //   99	120	367	finally
    //   130	157	367	finally
    //   220	239	367	finally
    //   243	254	367	finally
    //   99	120	378	java/lang/Throwable
    //   130	157	378	java/lang/Throwable
    //   220	239	378	java/lang/Throwable
    //   243	254	378	java/lang/Throwable
  }
  
  public static boolean hasThemeKey(String paramString)
  {
    return currentColors.containsKey(paramString);
  }
  
  public static boolean hasWallpaperFromTheme()
  {
    if ((currentColors.containsKey("chat_wallpaper")) || (themedWallpaperFileOffset > 0)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isCurrentThemeNight()
  {
    if (currentTheme == currentNightTheme) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isCustomTheme()
  {
    return isCustomTheme;
  }
  
  public static void loadWallpaper()
  {
    if (wallpaper != null) {}
    for (;;)
    {
      return;
      Utilities.searchQueue.postRunnable(new Runnable()
      {
        /* Error */
        public void run()
        {
          // Byte code:
          //   0: invokestatic 26	org/telegram/ui/ActionBar/Theme:access$900	()Ljava/lang/Object;
          //   3: astore_1
          //   4: aload_1
          //   5: monitorenter
          //   6: invokestatic 32	org/telegram/messenger/MessagesController:getGlobalMainSettings	()Landroid/content/SharedPreferences;
          //   9: ldc 34
          //   11: iconst_0
          //   12: invokeinterface 40 3 0
          //   17: ifne +41 -> 58
          //   20: invokestatic 44	org/telegram/ui/ActionBar/Theme:access$1000	()Ljava/util/HashMap;
          //   23: ldc 46
          //   25: invokevirtual 52	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
          //   28: checkcast 54	java/lang/Integer
          //   31: astore_2
          //   32: aload_2
          //   33: ifnull +180 -> 213
          //   36: new 56	android/graphics/drawable/ColorDrawable
          //   39: astore_3
          //   40: aload_3
          //   41: aload_2
          //   42: invokevirtual 60	java/lang/Integer:intValue	()I
          //   45: invokespecial 63	android/graphics/drawable/ColorDrawable:<init>	(I)V
          //   48: aload_3
          //   49: invokestatic 67	org/telegram/ui/ActionBar/Theme:access$1102	(Landroid/graphics/drawable/Drawable;)Landroid/graphics/drawable/Drawable;
          //   52: pop
          //   53: iconst_1
          //   54: invokestatic 71	org/telegram/ui/ActionBar/Theme:access$1202	(Z)Z
          //   57: pop
          //   58: invokestatic 75	org/telegram/ui/ActionBar/Theme:access$1100	()Landroid/graphics/drawable/Drawable;
          //   61: astore_2
          //   62: aload_2
          //   63: ifnonnull +127 -> 190
          //   66: iconst_0
          //   67: istore 4
          //   69: iload 4
          //   71: istore 5
          //   73: invokestatic 32	org/telegram/messenger/MessagesController:getGlobalMainSettings	()Landroid/content/SharedPreferences;
          //   76: astore_2
          //   77: iload 4
          //   79: istore 5
          //   81: aload_2
          //   82: ldc 77
          //   84: ldc 78
          //   86: invokeinterface 82 3 0
          //   91: istore 6
          //   93: iload 4
          //   95: istore 5
          //   97: aload_2
          //   98: ldc 84
          //   100: iconst_0
          //   101: invokeinterface 82 3 0
          //   106: istore 4
          //   108: iload 4
          //   110: istore 5
          //   112: iload 4
          //   114: ifne +42 -> 156
          //   117: iload 6
          //   119: ldc 78
          //   121: if_icmpne +312 -> 433
          //   124: iload 4
          //   126: istore 5
          //   128: getstatic 90	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
          //   131: invokevirtual 96	android/content/Context:getResources	()Landroid/content/res/Resources;
          //   134: ldc 97
          //   136: invokevirtual 103	android/content/res/Resources:getDrawable	(I)Landroid/graphics/drawable/Drawable;
          //   139: invokestatic 67	org/telegram/ui/ActionBar/Theme:access$1102	(Landroid/graphics/drawable/Drawable;)Landroid/graphics/drawable/Drawable;
          //   142: pop
          //   143: iload 4
          //   145: istore 5
          //   147: iconst_0
          //   148: invokestatic 71	org/telegram/ui/ActionBar/Theme:access$1202	(Z)Z
          //   151: pop
          //   152: iload 4
          //   154: istore 5
          //   156: invokestatic 75	org/telegram/ui/ActionBar/Theme:access$1100	()Landroid/graphics/drawable/Drawable;
          //   159: ifnonnull +31 -> 190
          //   162: iload 5
          //   164: istore 4
          //   166: iload 5
          //   168: ifne +7 -> 175
          //   171: ldc 104
          //   173: istore 4
          //   175: new 56	android/graphics/drawable/ColorDrawable
          //   178: astore_2
          //   179: aload_2
          //   180: iload 4
          //   182: invokespecial 63	android/graphics/drawable/ColorDrawable:<init>	(I)V
          //   185: aload_2
          //   186: invokestatic 67	org/telegram/ui/ActionBar/Theme:access$1102	(Landroid/graphics/drawable/Drawable;)Landroid/graphics/drawable/Drawable;
          //   189: pop
          //   190: invokestatic 75	org/telegram/ui/ActionBar/Theme:access$1100	()Landroid/graphics/drawable/Drawable;
          //   193: iconst_1
          //   194: invokestatic 108	org/telegram/ui/ActionBar/Theme:access$1600	(Landroid/graphics/drawable/Drawable;I)V
          //   197: new 13	org/telegram/ui/ActionBar/Theme$11$1
          //   200: astore_2
          //   201: aload_2
          //   202: aload_0
          //   203: invokespecial 111	org/telegram/ui/ActionBar/Theme$11$1:<init>	(Lorg/telegram/ui/ActionBar/Theme$11;)V
          //   206: aload_2
          //   207: invokestatic 117	org/telegram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
          //   210: aload_1
          //   211: monitorexit
          //   212: return
          //   213: invokestatic 120	org/telegram/ui/ActionBar/Theme:access$1300	()I
          //   216: ifle -158 -> 58
          //   219: invokestatic 124	org/telegram/ui/ActionBar/Theme:access$1400	()Lorg/telegram/ui/ActionBar/Theme$ThemeInfo;
          //   222: getfield 130	org/telegram/ui/ActionBar/Theme$ThemeInfo:pathToFile	Ljava/lang/String;
          //   225: ifnonnull +14 -> 239
          //   228: invokestatic 124	org/telegram/ui/ActionBar/Theme:access$1400	()Lorg/telegram/ui/ActionBar/Theme$ThemeInfo;
          //   231: getfield 133	org/telegram/ui/ActionBar/Theme$ThemeInfo:assetName	Ljava/lang/String;
          //   234: astore_2
          //   235: aload_2
          //   236: ifnull -178 -> 58
          //   239: aconst_null
          //   240: astore 7
          //   242: aconst_null
          //   243: astore 8
          //   245: aload 7
          //   247: astore_2
          //   248: invokestatic 124	org/telegram/ui/ActionBar/Theme:access$1400	()Lorg/telegram/ui/ActionBar/Theme$ThemeInfo;
          //   251: getfield 133	org/telegram/ui/ActionBar/Theme$ThemeInfo:assetName	Ljava/lang/String;
          //   254: ifnull +103 -> 357
          //   257: aload 7
          //   259: astore_2
          //   260: invokestatic 124	org/telegram/ui/ActionBar/Theme:access$1400	()Lorg/telegram/ui/ActionBar/Theme$ThemeInfo;
          //   263: getfield 133	org/telegram/ui/ActionBar/Theme$ThemeInfo:assetName	Ljava/lang/String;
          //   266: invokestatic 137	org/telegram/ui/ActionBar/Theme:getAssetFile	(Ljava/lang/String;)Ljava/io/File;
          //   269: astore 9
          //   271: aload 7
          //   273: astore_2
          //   274: new 139	java/io/FileInputStream
          //   277: astore_3
          //   278: aload 7
          //   280: astore_2
          //   281: aload_3
          //   282: aload 9
          //   284: invokespecial 142	java/io/FileInputStream:<init>	(Ljava/io/File;)V
          //   287: aload_3
          //   288: invokevirtual 146	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
          //   291: invokestatic 120	org/telegram/ui/ActionBar/Theme:access$1300	()I
          //   294: i2l
          //   295: invokevirtual 152	java/nio/channels/FileChannel:position	(J)Ljava/nio/channels/FileChannel;
          //   298: pop
          //   299: aload_3
          //   300: invokestatic 158	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;)Landroid/graphics/Bitmap;
          //   303: astore 9
          //   305: aload 9
          //   307: ifnull +26 -> 333
          //   310: new 160	android/graphics/drawable/BitmapDrawable
          //   313: astore_2
          //   314: aload_2
          //   315: aload 9
          //   317: invokespecial 163	android/graphics/drawable/BitmapDrawable:<init>	(Landroid/graphics/Bitmap;)V
          //   320: aload_2
          //   321: invokestatic 67	org/telegram/ui/ActionBar/Theme:access$1102	(Landroid/graphics/drawable/Drawable;)Landroid/graphics/drawable/Drawable;
          //   324: invokestatic 166	org/telegram/ui/ActionBar/Theme:access$1502	(Landroid/graphics/drawable/Drawable;)Landroid/graphics/drawable/Drawable;
          //   327: pop
          //   328: iconst_1
          //   329: invokestatic 71	org/telegram/ui/ActionBar/Theme:access$1202	(Z)Z
          //   332: pop
          //   333: aload_3
          //   334: ifnull -276 -> 58
          //   337: aload_3
          //   338: invokevirtual 169	java/io/FileInputStream:close	()V
          //   341: goto -283 -> 58
          //   344: astore_2
          //   345: aload_2
          //   346: invokestatic 175	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
          //   349: goto -291 -> 58
          //   352: astore_2
          //   353: aload_1
          //   354: monitorexit
          //   355: aload_2
          //   356: athrow
          //   357: aload 7
          //   359: astore_2
          //   360: new 177	java/io/File
          //   363: dup
          //   364: invokestatic 124	org/telegram/ui/ActionBar/Theme:access$1400	()Lorg/telegram/ui/ActionBar/Theme$ThemeInfo;
          //   367: getfield 130	org/telegram/ui/ActionBar/Theme$ThemeInfo:pathToFile	Ljava/lang/String;
          //   370: invokespecial 180	java/io/File:<init>	(Ljava/lang/String;)V
          //   373: astore 9
          //   375: goto -104 -> 271
          //   378: astore 9
          //   380: aload 8
          //   382: astore_3
          //   383: aload_3
          //   384: astore_2
          //   385: aload 9
          //   387: invokestatic 175	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
          //   390: aload_3
          //   391: ifnull -333 -> 58
          //   394: aload_3
          //   395: invokevirtual 169	java/io/FileInputStream:close	()V
          //   398: goto -340 -> 58
          //   401: astore_2
          //   402: aload_2
          //   403: invokestatic 175	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
          //   406: goto -348 -> 58
          //   409: astore_3
          //   410: aload_2
          //   411: astore 9
          //   413: aload 9
          //   415: ifnull +8 -> 423
          //   418: aload 9
          //   420: invokevirtual 169	java/io/FileInputStream:close	()V
          //   423: aload_3
          //   424: athrow
          //   425: astore_2
          //   426: aload_2
          //   427: invokestatic 175	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
          //   430: goto -7 -> 423
          //   433: iload 4
          //   435: istore 5
          //   437: new 177	java/io/File
          //   440: astore_2
          //   441: iload 4
          //   443: istore 5
          //   445: aload_2
          //   446: invokestatic 184	org/telegram/messenger/ApplicationLoader:getFilesDirFixed	()Ljava/io/File;
          //   449: ldc -70
          //   451: invokespecial 189	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
          //   454: iload 4
          //   456: istore 5
          //   458: aload_2
          //   459: invokevirtual 193	java/io/File:exists	()Z
          //   462: ifeq +38 -> 500
          //   465: iload 4
          //   467: istore 5
          //   469: aload_2
          //   470: invokevirtual 197	java/io/File:getAbsolutePath	()Ljava/lang/String;
          //   473: invokestatic 203	android/graphics/drawable/Drawable:createFromPath	(Ljava/lang/String;)Landroid/graphics/drawable/Drawable;
          //   476: invokestatic 67	org/telegram/ui/ActionBar/Theme:access$1102	(Landroid/graphics/drawable/Drawable;)Landroid/graphics/drawable/Drawable;
          //   479: pop
          //   480: iload 4
          //   482: istore 5
          //   484: iconst_1
          //   485: invokestatic 71	org/telegram/ui/ActionBar/Theme:access$1202	(Z)Z
          //   488: pop
          //   489: iload 4
          //   491: istore 5
          //   493: goto -337 -> 156
          //   496: astore_2
          //   497: goto -341 -> 156
          //   500: iload 4
          //   502: istore 5
          //   504: getstatic 90	org/telegram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
          //   507: invokevirtual 96	android/content/Context:getResources	()Landroid/content/res/Resources;
          //   510: ldc 97
          //   512: invokevirtual 103	android/content/res/Resources:getDrawable	(I)Landroid/graphics/drawable/Drawable;
          //   515: invokestatic 67	org/telegram/ui/ActionBar/Theme:access$1102	(Landroid/graphics/drawable/Drawable;)Landroid/graphics/drawable/Drawable;
          //   518: pop
          //   519: iload 4
          //   521: istore 5
          //   523: iconst_0
          //   524: invokestatic 71	org/telegram/ui/ActionBar/Theme:access$1202	(Z)Z
          //   527: pop
          //   528: iload 4
          //   530: istore 5
          //   532: goto -376 -> 156
          //   535: astore_2
          //   536: aload_3
          //   537: astore 9
          //   539: aload_2
          //   540: astore_3
          //   541: goto -128 -> 413
          //   544: astore 9
          //   546: goto -163 -> 383
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	549	0	this	11
          //   3	351	1	localObject1	Object
          //   31	290	2	localObject2	Object
          //   344	2	2	localException1	Exception
          //   352	4	2	localObject3	Object
          //   359	26	2	localObject4	Object
          //   401	10	2	localException2	Exception
          //   425	2	2	localException3	Exception
          //   440	30	2	localFile	File
          //   496	1	2	localThrowable1	Throwable
          //   535	5	2	localObject5	Object
          //   39	356	3	localObject6	Object
          //   409	128	3	localObject7	Object
          //   540	1	3	localObject8	Object
          //   67	462	4	i	int
          //   71	460	5	j	int
          //   91	31	6	k	int
          //   240	118	7	localObject9	Object
          //   243	138	8	localObject10	Object
          //   269	105	9	localObject11	Object
          //   378	8	9	localThrowable2	Throwable
          //   411	127	9	localObject12	Object
          //   544	1	9	localThrowable3	Throwable
          // Exception table:
          //   from	to	target	type
          //   337	341	344	java/lang/Exception
          //   6	32	352	finally
          //   36	58	352	finally
          //   58	62	352	finally
          //   73	77	352	finally
          //   81	93	352	finally
          //   97	108	352	finally
          //   128	143	352	finally
          //   147	152	352	finally
          //   156	162	352	finally
          //   175	190	352	finally
          //   190	212	352	finally
          //   213	235	352	finally
          //   337	341	352	finally
          //   345	349	352	finally
          //   353	355	352	finally
          //   394	398	352	finally
          //   402	406	352	finally
          //   418	423	352	finally
          //   423	425	352	finally
          //   426	430	352	finally
          //   437	441	352	finally
          //   445	454	352	finally
          //   458	465	352	finally
          //   469	480	352	finally
          //   484	489	352	finally
          //   504	519	352	finally
          //   523	528	352	finally
          //   248	257	378	java/lang/Throwable
          //   260	271	378	java/lang/Throwable
          //   274	278	378	java/lang/Throwable
          //   281	287	378	java/lang/Throwable
          //   360	375	378	java/lang/Throwable
          //   394	398	401	java/lang/Exception
          //   248	257	409	finally
          //   260	271	409	finally
          //   274	278	409	finally
          //   281	287	409	finally
          //   360	375	409	finally
          //   385	390	409	finally
          //   418	423	425	java/lang/Exception
          //   73	77	496	java/lang/Throwable
          //   81	93	496	java/lang/Throwable
          //   97	108	496	java/lang/Throwable
          //   128	143	496	java/lang/Throwable
          //   147	152	496	java/lang/Throwable
          //   437	441	496	java/lang/Throwable
          //   445	454	496	java/lang/Throwable
          //   458	465	496	java/lang/Throwable
          //   469	480	496	java/lang/Throwable
          //   484	489	496	java/lang/Throwable
          //   504	519	496	java/lang/Throwable
          //   523	528	496	java/lang/Throwable
          //   287	305	535	finally
          //   310	333	535	finally
          //   287	305	544	java/lang/Throwable
          //   310	333	544	java/lang/Throwable
        }
      });
    }
  }
  
  public static void reloadWallpaper()
  {
    wallpaper = null;
    themedWallpaper = null;
    loadWallpaper();
  }
  
  public static void saveAutoNightThemeConfig()
  {
    SharedPreferences.Editor localEditor = MessagesController.getGlobalMainSettings().edit();
    localEditor.putInt("selectedAutoNightType", selectedAutoNightType);
    localEditor.putBoolean("autoNightScheduleByLocation", autoNightScheduleByLocation);
    localEditor.putFloat("autoNightBrighnessThreshold", autoNightBrighnessThreshold);
    localEditor.putInt("autoNightDayStartTime", autoNightDayStartTime);
    localEditor.putInt("autoNightDayEndTime", autoNightDayEndTime);
    localEditor.putInt("autoNightSunriseTime", autoNightSunriseTime);
    localEditor.putString("autoNightCityName", autoNightCityName);
    localEditor.putInt("autoNightSunsetTime", autoNightSunsetTime);
    localEditor.putLong("autoNightLocationLatitude3", Double.doubleToRawLongBits(autoNightLocationLatitude));
    localEditor.putLong("autoNightLocationLongitude3", Double.doubleToRawLongBits(autoNightLocationLongitude));
    localEditor.putInt("autoNightLastSunCheckDay", autoNightLastSunCheckDay);
    if (currentNightTheme != null) {
      localEditor.putString("nighttheme", currentNightTheme.name);
    }
    for (;;)
    {
      localEditor.commit();
      return;
      localEditor.remove("nighttheme");
    }
  }
  
  /* Error */
  public static void saveCurrentTheme(String paramString, boolean paramBoolean)
  {
    // Byte code:
    //   0: new 3564	java/lang/StringBuilder
    //   3: dup
    //   4: invokespecial 3565	java/lang/StringBuilder:<init>	()V
    //   7: astore_2
    //   8: getstatic 2221	org/telegram/ui/ActionBar/Theme:currentColors	Ljava/util/HashMap;
    //   11: invokevirtual 3569	java/util/HashMap:entrySet	()Ljava/util/Set;
    //   14: invokeinterface 3575 1 0
    //   19: astore_3
    //   20: aload_3
    //   21: invokeinterface 3580 1 0
    //   26: ifeq +54 -> 80
    //   29: aload_3
    //   30: invokeinterface 3583 1 0
    //   35: checkcast 3585	java/util/Map$Entry
    //   38: astore 4
    //   40: aload_2
    //   41: aload 4
    //   43: invokeinterface 3588 1 0
    //   48: checkcast 1947	java/lang/String
    //   51: invokevirtual 3592	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   54: ldc_w 3594
    //   57: invokevirtual 3592	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   60: aload 4
    //   62: invokeinterface 3597 1 0
    //   67: invokevirtual 3600	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   70: ldc_w 3602
    //   73: invokevirtual 3592	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   76: pop
    //   77: goto -57 -> 20
    //   80: new 2802	java/io/File
    //   83: dup
    //   84: invokestatic 2815	org/telegram/messenger/ApplicationLoader:getFilesDirFixed	()Ljava/io/File;
    //   87: aload_0
    //   88: invokespecial 2818	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   91: astore 5
    //   93: aconst_null
    //   94: astore 6
    //   96: aconst_null
    //   97: astore 7
    //   99: aload 6
    //   101: astore_3
    //   102: new 3604	java/io/FileOutputStream
    //   105: astore 4
    //   107: aload 6
    //   109: astore_3
    //   110: aload 4
    //   112: aload 5
    //   114: invokespecial 3605	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   117: aload 4
    //   119: aload_2
    //   120: invokevirtual 3608	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   123: invokevirtual 3612	java/lang/String:getBytes	()[B
    //   126: invokevirtual 3616	java/io/FileOutputStream:write	([B)V
    //   129: getstatic 2429	org/telegram/ui/ActionBar/Theme:themedWallpaper	Landroid/graphics/drawable/Drawable;
    //   132: instanceof 3138
    //   135: ifeq +116 -> 251
    //   138: getstatic 2429	org/telegram/ui/ActionBar/Theme:themedWallpaper	Landroid/graphics/drawable/Drawable;
    //   141: checkcast 3138	android/graphics/drawable/BitmapDrawable
    //   144: invokevirtual 3620	android/graphics/drawable/BitmapDrawable:getBitmap	()Landroid/graphics/Bitmap;
    //   147: astore_3
    //   148: aload_3
    //   149: ifnull +85 -> 234
    //   152: aload 4
    //   154: iconst_4
    //   155: newarray <illegal type>
    //   157: dup
    //   158: iconst_0
    //   159: ldc_w 3621
    //   162: bastore
    //   163: dup
    //   164: iconst_1
    //   165: ldc_w 3622
    //   168: bastore
    //   169: dup
    //   170: iconst_2
    //   171: ldc_w 3623
    //   174: bastore
    //   175: dup
    //   176: iconst_3
    //   177: ldc_w 3624
    //   180: bastore
    //   181: invokevirtual 3616	java/io/FileOutputStream:write	([B)V
    //   184: aload_3
    //   185: getstatic 3630	android/graphics/Bitmap$CompressFormat:JPEG	Landroid/graphics/Bitmap$CompressFormat;
    //   188: bipush 87
    //   190: aload 4
    //   192: invokevirtual 3634	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   195: pop
    //   196: aload 4
    //   198: iconst_5
    //   199: newarray <illegal type>
    //   201: dup
    //   202: iconst_0
    //   203: ldc_w 3624
    //   206: bastore
    //   207: dup
    //   208: iconst_1
    //   209: ldc_w 3621
    //   212: bastore
    //   213: dup
    //   214: iconst_2
    //   215: ldc_w 3622
    //   218: bastore
    //   219: dup
    //   220: iconst_3
    //   221: ldc_w 3635
    //   224: bastore
    //   225: dup
    //   226: iconst_4
    //   227: ldc_w 3624
    //   230: bastore
    //   231: invokevirtual 3616	java/io/FileOutputStream:write	([B)V
    //   234: iload_1
    //   235: ifeq +16 -> 251
    //   238: getstatic 2429	org/telegram/ui/ActionBar/Theme:themedWallpaper	Landroid/graphics/drawable/Drawable;
    //   241: putstatic 2416	org/telegram/ui/ActionBar/Theme:wallpaper	Landroid/graphics/drawable/Drawable;
    //   244: getstatic 2416	org/telegram/ui/ActionBar/Theme:wallpaper	Landroid/graphics/drawable/Drawable;
    //   247: iconst_2
    //   248: invokestatic 2434	org/telegram/ui/ActionBar/Theme:calcBackgroundColor	(Landroid/graphics/drawable/Drawable;I)V
    //   251: getstatic 2219	org/telegram/ui/ActionBar/Theme:themesDict	Ljava/util/HashMap;
    //   254: aload_0
    //   255: invokevirtual 2316	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   258: checkcast 30	org/telegram/ui/ActionBar/Theme$ThemeInfo
    //   261: astore 7
    //   263: aload 7
    //   265: astore_3
    //   266: aload 7
    //   268: ifnonnull +59 -> 327
    //   271: new 30	org/telegram/ui/ActionBar/Theme$ThemeInfo
    //   274: astore_3
    //   275: aload_3
    //   276: invokespecial 2222	org/telegram/ui/ActionBar/Theme$ThemeInfo:<init>	()V
    //   279: aload_3
    //   280: aload 5
    //   282: invokevirtual 2826	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   285: putfield 2777	org/telegram/ui/ActionBar/Theme$ThemeInfo:pathToFile	Ljava/lang/String;
    //   288: aload_3
    //   289: aload_0
    //   290: putfield 2227	org/telegram/ui/ActionBar/Theme$ThemeInfo:name	Ljava/lang/String;
    //   293: getstatic 2215	org/telegram/ui/ActionBar/Theme:themes	Ljava/util/ArrayList;
    //   296: aload_3
    //   297: invokevirtual 2237	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   300: pop
    //   301: getstatic 2219	org/telegram/ui/ActionBar/Theme:themesDict	Ljava/util/HashMap;
    //   304: aload_3
    //   305: getfield 2227	org/telegram/ui/ActionBar/Theme$ThemeInfo:name	Ljava/lang/String;
    //   308: aload_3
    //   309: invokevirtual 1978	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   312: pop
    //   313: getstatic 2217	org/telegram/ui/ActionBar/Theme:otherThemes	Ljava/util/ArrayList;
    //   316: aload_3
    //   317: invokevirtual 2237	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   320: pop
    //   321: invokestatic 2385	org/telegram/ui/ActionBar/Theme:saveOtherThemes	()V
    //   324: invokestatic 2304	org/telegram/ui/ActionBar/Theme:sortThemes	()V
    //   327: aload_3
    //   328: putstatic 2231	org/telegram/ui/ActionBar/Theme:currentTheme	Lorg/telegram/ui/ActionBar/Theme$ThemeInfo;
    //   331: getstatic 2231	org/telegram/ui/ActionBar/Theme:currentTheme	Lorg/telegram/ui/ActionBar/Theme$ThemeInfo;
    //   334: getstatic 2246	org/telegram/ui/ActionBar/Theme:currentNightTheme	Lorg/telegram/ui/ActionBar/Theme$ThemeInfo;
    //   337: if_acmpeq +9 -> 346
    //   340: getstatic 2231	org/telegram/ui/ActionBar/Theme:currentTheme	Lorg/telegram/ui/ActionBar/Theme$ThemeInfo;
    //   343: putstatic 2233	org/telegram/ui/ActionBar/Theme:currentDayTheme	Lorg/telegram/ui/ActionBar/Theme$ThemeInfo;
    //   346: invokestatic 2310	org/telegram/messenger/MessagesController:getGlobalMainSettings	()Landroid/content/SharedPreferences;
    //   349: invokeinterface 2389 1 0
    //   354: astore_0
    //   355: aload_0
    //   356: ldc_w 2312
    //   359: getstatic 2233	org/telegram/ui/ActionBar/Theme:currentDayTheme	Lorg/telegram/ui/ActionBar/Theme$ThemeInfo;
    //   362: getfield 2227	org/telegram/ui/ActionBar/Theme$ThemeInfo:name	Ljava/lang/String;
    //   365: invokeinterface 2781 3 0
    //   370: pop
    //   371: aload_0
    //   372: invokeinterface 2399 1 0
    //   377: pop
    //   378: aload 4
    //   380: ifnull +8 -> 388
    //   383: aload 4
    //   385: invokevirtual 3636	java/io/FileOutputStream:close	()V
    //   388: return
    //   389: astore_0
    //   390: aload_0
    //   391: invokestatic 2301	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   394: goto -6 -> 388
    //   397: astore 4
    //   399: aload 7
    //   401: astore_0
    //   402: aload_0
    //   403: astore_3
    //   404: aload 4
    //   406: invokestatic 2301	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   409: aload_0
    //   410: ifnull -22 -> 388
    //   413: aload_0
    //   414: invokevirtual 3636	java/io/FileOutputStream:close	()V
    //   417: goto -29 -> 388
    //   420: astore_0
    //   421: aload_0
    //   422: invokestatic 2301	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   425: goto -37 -> 388
    //   428: astore_0
    //   429: aload_3
    //   430: ifnull +7 -> 437
    //   433: aload_3
    //   434: invokevirtual 3636	java/io/FileOutputStream:close	()V
    //   437: aload_0
    //   438: athrow
    //   439: astore_3
    //   440: aload_3
    //   441: invokestatic 2301	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   444: goto -7 -> 437
    //   447: astore_0
    //   448: aload 4
    //   450: astore_3
    //   451: goto -22 -> 429
    //   454: astore_3
    //   455: aload 4
    //   457: astore_0
    //   458: aload_3
    //   459: astore 4
    //   461: goto -59 -> 402
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	464	0	paramString	String
    //   0	464	1	paramBoolean	boolean
    //   7	113	2	localStringBuilder	StringBuilder
    //   19	415	3	localObject1	Object
    //   439	2	3	localException1	Exception
    //   450	1	3	localObject2	Object
    //   454	5	3	localException2	Exception
    //   38	346	4	localObject3	Object
    //   397	59	4	localException3	Exception
    //   459	1	4	localException4	Exception
    //   91	190	5	localFile	File
    //   94	14	6	localObject4	Object
    //   97	303	7	localThemeInfo	ThemeInfo
    // Exception table:
    //   from	to	target	type
    //   383	388	389	java/lang/Exception
    //   102	107	397	java/lang/Exception
    //   110	117	397	java/lang/Exception
    //   413	417	420	java/lang/Exception
    //   102	107	428	finally
    //   110	117	428	finally
    //   404	409	428	finally
    //   433	437	439	java/lang/Exception
    //   117	148	447	finally
    //   152	234	447	finally
    //   238	251	447	finally
    //   251	263	447	finally
    //   271	327	447	finally
    //   327	346	447	finally
    //   346	378	447	finally
    //   117	148	454	java/lang/Exception
    //   152	234	454	java/lang/Exception
    //   238	251	454	java/lang/Exception
    //   251	263	454	java/lang/Exception
    //   271	327	454	java/lang/Exception
    //   327	346	454	java/lang/Exception
    //   346	378	454	java/lang/Exception
  }
  
  private static void saveOtherThemes()
  {
    SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
    JSONArray localJSONArray = new JSONArray();
    for (int i = 0; i < otherThemes.size(); i++)
    {
      JSONObject localJSONObject = ((ThemeInfo)otherThemes.get(i)).getSaveJson();
      if (localJSONObject != null) {
        localJSONArray.put(localJSONObject);
      }
    }
    localEditor.putString("themes2", localJSONArray.toString());
    localEditor.commit();
  }
  
  public static void setColor(String paramString, int paramInt, boolean paramBoolean)
  {
    int i = paramInt;
    if (paramString.equals("chat_wallpaper")) {
      i = paramInt | 0xFF000000;
    }
    if (paramBoolean)
    {
      currentColors.remove(paramString);
      if ((!paramString.equals("chat_serviceBackground")) && (!paramString.equals("chat_serviceBackgroundSelected"))) {
        break label68;
      }
      applyChatServiceMessageColor();
    }
    for (;;)
    {
      return;
      currentColors.put(paramString, Integer.valueOf(i));
      break;
      label68:
      if (paramString.equals("chat_wallpaper")) {
        reloadWallpaper();
      }
    }
  }
  
  public static void setCombinedDrawableColor(Drawable paramDrawable, int paramInt, boolean paramBoolean)
  {
    if (!(paramDrawable instanceof CombinedDrawable)) {
      return;
    }
    if (paramBoolean) {}
    for (paramDrawable = ((CombinedDrawable)paramDrawable).getIcon();; paramDrawable = ((CombinedDrawable)paramDrawable).getBackground())
    {
      paramDrawable.setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
      break;
    }
  }
  
  public static void setCurrentNightTheme(ThemeInfo paramThemeInfo)
  {
    if (currentTheme == currentNightTheme) {}
    for (int i = 1;; i = 0)
    {
      currentNightTheme = paramThemeInfo;
      if (i != 0) {
        applyDayNightThemeMaybe(true);
      }
      return;
    }
  }
  
  public static void setDrawableColor(Drawable paramDrawable, int paramInt)
  {
    if (paramDrawable == null) {}
    for (;;)
    {
      return;
      if ((paramDrawable instanceof ShapeDrawable)) {
        ((ShapeDrawable)paramDrawable).getPaint().setColor(paramInt);
      } else {
        paramDrawable.setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
      }
    }
  }
  
  public static void setDrawableColorByKey(Drawable paramDrawable, String paramString)
  {
    if (paramString == null) {}
    for (;;)
    {
      return;
      setDrawableColor(paramDrawable, getColor(paramString));
    }
  }
  
  public static void setEmojiDrawableColor(Drawable paramDrawable, int paramInt, boolean paramBoolean)
  {
    if ((!(paramDrawable instanceof StateListDrawable)) || (paramBoolean)) {}
    for (;;)
    {
      PorterDuffColorFilter localPorterDuffColorFilter;
      try
      {
        paramDrawable = getStateDrawable(paramDrawable, 0);
        if ((paramDrawable instanceof ShapeDrawable))
        {
          ((ShapeDrawable)paramDrawable).getPaint().setColor(paramInt);
          return;
        }
        localPorterDuffColorFilter = new android/graphics/PorterDuffColorFilter;
        localPorterDuffColorFilter.<init>(paramInt, PorterDuff.Mode.MULTIPLY);
        paramDrawable.setColorFilter(localPorterDuffColorFilter);
        continue;
      }
      catch (Throwable paramDrawable)
      {
        continue;
      }
      paramDrawable = getStateDrawable(paramDrawable, 1);
      if ((paramDrawable instanceof ShapeDrawable))
      {
        ((ShapeDrawable)paramDrawable).getPaint().setColor(paramInt);
      }
      else
      {
        localPorterDuffColorFilter = new android/graphics/PorterDuffColorFilter;
        localPorterDuffColorFilter.<init>(paramInt, PorterDuff.Mode.MULTIPLY);
        paramDrawable.setColorFilter(localPorterDuffColorFilter);
      }
    }
  }
  
  public static void setSelectorDrawableColor(Drawable paramDrawable, int paramInt, boolean paramBoolean)
  {
    if ((paramDrawable instanceof StateListDrawable)) {
      if (!paramBoolean) {}
    }
    for (;;)
    {
      try
      {
        localDrawable = getStateDrawable(paramDrawable, 0);
        if ((localDrawable instanceof ShapeDrawable))
        {
          ((ShapeDrawable)localDrawable).getPaint().setColor(paramInt);
          paramDrawable = getStateDrawable(paramDrawable, 1);
          if (!(paramDrawable instanceof ShapeDrawable)) {
            continue;
          }
          ((ShapeDrawable)paramDrawable).getPaint().setColor(paramInt);
          return;
        }
      }
      catch (Throwable paramDrawable)
      {
        Drawable localDrawable;
        continue;
        localPorterDuffColorFilter = new android/graphics/PorterDuffColorFilter;
        localPorterDuffColorFilter.<init>(paramInt, PorterDuff.Mode.MULTIPLY);
        paramDrawable.setColorFilter(localPorterDuffColorFilter);
        continue;
      }
      PorterDuffColorFilter localPorterDuffColorFilter = new android/graphics/PorterDuffColorFilter;
      localPorterDuffColorFilter.<init>(paramInt, PorterDuff.Mode.MULTIPLY);
      localDrawable.setColorFilter(localPorterDuffColorFilter);
      continue;
      paramDrawable = getStateDrawable(paramDrawable, 2);
      if ((paramDrawable instanceof ShapeDrawable))
      {
        ((ShapeDrawable)paramDrawable).getPaint().setColor(paramInt);
      }
      else
      {
        localPorterDuffColorFilter = new android/graphics/PorterDuffColorFilter;
        localPorterDuffColorFilter.<init>(paramInt, PorterDuff.Mode.MULTIPLY);
        paramDrawable.setColorFilter(localPorterDuffColorFilter);
        continue;
        if ((Build.VERSION.SDK_INT >= 21) && ((paramDrawable instanceof RippleDrawable)))
        {
          paramDrawable = (RippleDrawable)paramDrawable;
          if (paramBoolean)
          {
            paramDrawable.setColor(new ColorStateList(new int[][] { StateSet.WILD_CARD }, new int[] { paramInt }));
          }
          else if (paramDrawable.getNumberOfLayers() > 0)
          {
            paramDrawable = paramDrawable.getDrawable(0);
            if ((paramDrawable instanceof ShapeDrawable)) {
              ((ShapeDrawable)paramDrawable).getPaint().setColor(paramInt);
            } else {
              paramDrawable.setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
            }
          }
        }
      }
    }
  }
  
  public static void setThemeWallpaper(String paramString, Bitmap paramBitmap, File paramFile)
  {
    currentColors.remove("chat_wallpaper");
    MessagesController.getGlobalMainSettings().edit().remove("overrideThemeWallpaper").commit();
    if (paramBitmap != null)
    {
      themedWallpaper = new BitmapDrawable(paramBitmap);
      saveCurrentTheme(paramString, false);
      calcBackgroundColor(themedWallpaper, 0);
      applyChatServiceMessageColor();
      NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewWallpapper, new Object[0]);
    }
    for (;;)
    {
      return;
      themedWallpaper = null;
      wallpaper = null;
      saveCurrentTheme(paramString, false);
      reloadWallpaper();
    }
  }
  
  private static void sortThemes()
  {
    Collections.sort(themes, new Comparator()
    {
      public int compare(Theme.ThemeInfo paramAnonymousThemeInfo1, Theme.ThemeInfo paramAnonymousThemeInfo2)
      {
        int i;
        if ((paramAnonymousThemeInfo1.pathToFile == null) && (paramAnonymousThemeInfo1.assetName == null)) {
          i = -1;
        }
        for (;;)
        {
          return i;
          if ((paramAnonymousThemeInfo2.pathToFile == null) && (paramAnonymousThemeInfo2.assetName == null)) {
            i = 1;
          } else {
            i = paramAnonymousThemeInfo1.name.compareTo(paramAnonymousThemeInfo2.name);
          }
        }
      }
    });
  }
  
  public static class ThemeInfo
  {
    public String assetName;
    public String name;
    public String pathToFile;
    
    public static ThemeInfo createWithJson(JSONObject paramJSONObject)
    {
      if (paramJSONObject == null) {
        paramJSONObject = null;
      }
      for (;;)
      {
        return paramJSONObject;
        try
        {
          ThemeInfo localThemeInfo = new org/telegram/ui/ActionBar/Theme$ThemeInfo;
          localThemeInfo.<init>();
          localThemeInfo.name = paramJSONObject.getString("name");
          localThemeInfo.pathToFile = paramJSONObject.getString("path");
          paramJSONObject = localThemeInfo;
        }
        catch (Exception paramJSONObject)
        {
          FileLog.e(paramJSONObject);
          paramJSONObject = null;
        }
      }
    }
    
    public static ThemeInfo createWithString(String paramString)
    {
      Object localObject = null;
      if (TextUtils.isEmpty(paramString)) {
        paramString = (String)localObject;
      }
      for (;;)
      {
        return paramString;
        String[] arrayOfString = paramString.split("\\|");
        paramString = (String)localObject;
        if (arrayOfString.length == 2)
        {
          paramString = new ThemeInfo();
          paramString.name = arrayOfString[0];
          paramString.pathToFile = arrayOfString[1];
        }
      }
    }
    
    public String getName()
    {
      String str;
      if ("Default".equals(this.name)) {
        str = LocaleController.getString("Default", NUM);
      }
      for (;;)
      {
        return str;
        if ("Blue".equals(this.name)) {
          str = LocaleController.getString("ThemeBlue", NUM);
        } else if ("Dark".equals(this.name)) {
          str = LocaleController.getString("ThemeDark", NUM);
        } else {
          str = this.name;
        }
      }
    }
    
    public JSONObject getSaveJson()
    {
      try
      {
        JSONObject localJSONObject = new org/json/JSONObject;
        localJSONObject.<init>();
        localJSONObject.put("name", this.name);
        localJSONObject.put("path", this.pathToFile);
        return localJSONObject;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
          Object localObject = null;
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ActionBar/Theme.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */