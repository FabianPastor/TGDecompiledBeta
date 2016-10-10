package org.telegram.ui.ActionBar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build.VERSION;
import java.lang.reflect.Array;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;

public class Theme
{
  public static final int ACTION_BAR_ACTION_MODE_TEXT_COLOR = -9211021;
  public static final int ACTION_BAR_AUDIO_SELECTOR_COLOR = 788529152;
  public static final int ACTION_BAR_BLUE_SELECTOR_COLOR = -11959891;
  public static final int ACTION_BAR_CHANNEL_INTRO_COLOR = -1;
  public static final int ACTION_BAR_CHANNEL_INTRO_SELECTOR_COLOR = 788529152;
  public static final int ACTION_BAR_COLOR = -11371101;
  public static final int ACTION_BAR_CYAN_SELECTOR_COLOR = -13007715;
  public static final int ACTION_BAR_GREEN_SELECTOR_COLOR = -12020419;
  public static final int ACTION_BAR_MAIN_AVATAR_COLOR = -11500111;
  public static final int ACTION_BAR_MEDIA_PICKER_COLOR = -13421773;
  public static final int ACTION_BAR_MODE_SELECTOR_COLOR = -986896;
  public static final int ACTION_BAR_ORANGE_SELECTOR_COLOR = -1674199;
  public static final int ACTION_BAR_PHOTO_VIEWER_COLOR = 2130706432;
  public static final int ACTION_BAR_PICKER_SELECTOR_COLOR = -12763843;
  public static final int ACTION_BAR_PINK_SELECTOR_COLOR = -2863493;
  public static final int ACTION_BAR_PLAYER_COLOR = -1;
  public static final int ACTION_BAR_PROFILE_COLOR = -10907718;
  public static final int ACTION_BAR_PROFILE_SUBTITLE_COLOR = -2626822;
  public static final int ACTION_BAR_RED_SELECTOR_COLOR = -4437183;
  public static final int ACTION_BAR_SELECTOR_COLOR = -12554860;
  public static final int ACTION_BAR_SUBTITLE_COLOR = -2758409;
  public static final int ACTION_BAR_TITLE_COLOR = -1;
  public static final int ACTION_BAR_VIDEO_EDIT_COLOR = -16777216;
  public static final int ACTION_BAR_VIOLET_SELECTOR_COLOR = -9216066;
  public static final int ACTION_BAR_WHITE_SELECTOR_COLOR = 1090519039;
  public static final int ACTION_BAR_YELLOW_SELECTOR_COLOR = -1073399;
  public static final int ALERT_PANEL_MESSAGE_TEXT_COLOR = -6710887;
  public static final int ALERT_PANEL_NAME_TEXT_COLOR = -12940081;
  public static final int ATTACH_SHEET_TEXT_COLOR = -9079435;
  public static final int AUTODOWNLOAD_SHEET_SAVE_TEXT_COLOR = -12940081;
  public static final int CHAT_ADD_CONTACT_TEXT_COLOR = -11894091;
  public static final int CHAT_BOTTOM_CHAT_OVERLAY_TEXT_COLOR = -12940081;
  public static final int CHAT_BOTTOM_OVERLAY_TEXT_COLOR = -8421505;
  public static final int CHAT_EMPTY_VIEW_TEXT_COLOR = -1;
  public static final int CHAT_GIF_HINT_TEXT_COLOR = -1;
  public static final int CHAT_REPORT_SPAM_TEXT_COLOR = -3188393;
  public static final int CHAT_SEARCH_COUNT_TEXT_COLOR = -11625772;
  public static final int CHAT_UNREAD_TEXT_COLOR = -11102772;
  public static final int DIALOGS_ATTACH_TEXT_COLOR = -11697229;
  public static final int DIALOGS_DRAFT_TEXT_COLOR = -2274503;
  public static final int DIALOGS_MESSAGE_TEXT_COLOR = -7368817;
  public static final int DIALOGS_NAME_TEXT_COLOR = -11697229;
  public static final int DIALOGS_PRINTING_TEXT_COLOR = -11697229;
  public static final int INAPP_PLAYER_BACKGROUND_COLOR = -1;
  public static final int INAPP_PLAYER_PERFORMER_TEXT_COLOR = -13683656;
  public static final int INAPP_PLAYER_TITLE_TEXT_COLOR = -13683656;
  public static final int INPUT_FIELD_SELECTOR_COLOR = -2697514;
  public static final int JOIN_SHEET_COUNT_TEXT_COLOR = -6710887;
  public static final int JOIN_SHEET_NAME_TEXT_COLOR = -14606047;
  public static final int MSG_BOT_BUTTON_TEXT_COLOR = -1;
  public static final int MSG_BOT_PROGRESS_COLOR = -1;
  public static final int MSG_IN_AUDIO_DURATION_SELECTED_TEXT_COLOR = -7752511;
  public static final int MSG_IN_AUDIO_DURATION_TEXT_COLOR = -6182221;
  public static final int MSG_IN_AUDIO_PERFORMER_TEXT_COLOR = -13683656;
  public static final int MSG_IN_AUDIO_PROGRESS_COLOR = -1;
  public static final int MSG_IN_AUDIO_SEEKBAR_COLOR = -1774864;
  public static final int MSG_IN_AUDIO_SEEKBAR_FILL_COLOR = -9259544;
  public static final int MSG_IN_AUDIO_SEEKBAR_SELECTED_COLOR = -4399384;
  public static final int MSG_IN_AUDIO_SELECTED_PROGRESS_COLOR = -1902337;
  public static final int MSG_IN_AUDIO_TITLE_TEXT_COLOR = -11625772;
  public static final int MSG_IN_CONTACT_NAME_TEXT_COLOR = -11625772;
  public static final int MSG_IN_CONTACT_PHONE_TEXT_COLOR = -13683656;
  public static final int MSG_IN_FILE_BACKGROUND_COLOR = -1314571;
  public static final int MSG_IN_FILE_BACKGROUND_SELECTED_COLOR = -3413258;
  public static final int MSG_IN_FILE_INFO_SELECTED_TEXT_COLOR = -7752511;
  public static final int MSG_IN_FILE_INFO_TEXT_COLOR = -6182221;
  public static final int MSG_IN_FILE_NAME_TEXT_COLOR = -11625772;
  public static final int MSG_IN_FILE_PROGRESS_COLOR = -1314571;
  public static final int MSG_IN_FILE_PROGRESS_SELECTED_COLOR = -3413258;
  public static final int MSG_IN_FORDWARDED_NAME_TEXT_COLOR = -13072697;
  public static final int MSG_IN_REPLY_LINE_COLOR = -9390872;
  public static final int MSG_IN_REPLY_MEDIA_MESSAGE_SELETED_TEXT_COLOR = -7752511;
  public static final int MSG_IN_REPLY_MEDIA_MESSAGE_TEXT_COLOR = -6182221;
  public static final int MSG_IN_REPLY_MESSAGE_TEXT_COLOR = -16777216;
  public static final int MSG_IN_REPLY_NAME_TEXT_COLOR = -12940081;
  public static final int MSG_IN_SITE_NAME_TEXT_COLOR = -12940081;
  public static final int MSG_IN_TIME_SELECTED_TEXT_COLOR = -7752511;
  public static final int MSG_IN_TIME_TEXT_COLOR = -6182221;
  public static final int MSG_IN_VENUE_INFO_SELECTED_TEXT_COLOR = -7752511;
  public static final int MSG_IN_VENUE_INFO_TEXT_COLOR = -6182221;
  public static final int MSG_IN_VENUE_NAME_TEXT_COLOR = -11625772;
  public static final int MSG_IN_VIA_BOT_NAME_TEXT_COLOR = -12940081;
  public static final int MSG_IN_VOICE_SEEKBAR_COLOR = -2169365;
  public static final int MSG_IN_VOICE_SEEKBAR_FILL_COLOR = -9259544;
  public static final int MSG_IN_VOICE_SEEKBAR_SELECTED_COLOR = -4399384;
  public static final int MSG_IN_WEB_PREVIEW_LINE_COLOR = -9390872;
  public static final int MSG_LINK_SELECT_BACKGROUND_COLOR = 862104035;
  public static final int MSG_LINK_TEXT_COLOR = -14255946;
  public static final int MSG_MEDIA_INFO_TEXT_COLOR = -1;
  public static final int MSG_MEDIA_PROGRESS_COLOR = -1;
  public static final int MSG_MEDIA_TIME_TEXT_COLOR = -1;
  public static final int MSG_OUT_AUDIO_DURATION_SELECTED_TEXT_COLOR = -10112933;
  public static final int MSG_OUT_AUDIO_DURATION_TEXT_COLOR = -10112933;
  public static final int MSG_OUT_AUDIO_PERFORMER_TEXT_COLOR = -13286860;
  public static final int MSG_OUT_AUDIO_PROGRESS_COLOR = -1048610;
  public static final int MSG_OUT_AUDIO_SEEKBAR_COLOR = -4463700;
  public static final int MSG_OUT_AUDIO_SEEKBAR_FILL_COLOR = -8863118;
  public static final int MSG_OUT_AUDIO_SEEKBAR_SELECTED_COLOR = -5644906;
  public static final int MSG_OUT_AUDIO_SELECTED_PROGRESS_COLOR = -2820676;
  public static final int MSG_OUT_AUDIO_TITLE_TEXT_COLOR = -11162801;
  public static final int MSG_OUT_CONTACT_NAME_TEXT_COLOR = -11162801;
  public static final int MSG_OUT_CONTACT_PHONE_TEXT_COLOR = -13286860;
  public static final int MSG_OUT_FILE_BACKGROUND_COLOR = -2427453;
  public static final int MSG_OUT_FILE_BACKGROUND_SELECTED_COLOR = -3806041;
  public static final int MSG_OUT_FILE_INFO_SELECTED_TEXT_COLOR = -10112933;
  public static final int MSG_OUT_FILE_INFO_TEXT_COLOR = -10112933;
  public static final int MSG_OUT_FILE_NAME_TEXT_COLOR = -11162801;
  public static final int MSG_OUT_FILE_PROGRESS_COLOR = -2427453;
  public static final int MSG_OUT_FILE_PROGRESS_SELECTED_COLOR = -3806041;
  public static final int MSG_OUT_FORDWARDED_NAME_TEXT_COLOR = -11162801;
  public static final int MSG_OUT_REPLY_LINE_COLOR = -7812741;
  public static final int MSG_OUT_REPLY_MEDIA_MESSAGE_SELETED_TEXT_COLOR = -10112933;
  public static final int MSG_OUT_REPLY_MEDIA_MESSAGE_TEXT_COLOR = -10112933;
  public static final int MSG_OUT_REPLY_MESSAGE_TEXT_COLOR = -16777216;
  public static final int MSG_OUT_REPLY_NAME_TEXT_COLOR = -11162801;
  public static final int MSG_OUT_SITE_NAME_TEXT_COLOR = -11162801;
  public static final int MSG_OUT_TIME_SELECTED_TEXT_COLOR = -9391780;
  public static final int MSG_OUT_TIME_TEXT_COLOR = -9391780;
  public static final int MSG_OUT_VENUE_INFO_SELECTED_TEXT_COLOR = -10112933;
  public static final int MSG_OUT_VENUE_INFO_TEXT_COLOR = -10112933;
  public static final int MSG_OUT_VENUE_NAME_TEXT_COLOR = -11162801;
  public static final int MSG_OUT_VIA_BOT_NAME_TEXT_COLOR = -11162801;
  public static final int MSG_OUT_VOICE_SEEKBAR_COLOR = -4463700;
  public static final int MSG_OUT_VOICE_SEEKBAR_FILL_COLOR = -8863118;
  public static final int MSG_OUT_VOICE_SEEKBAR_SELECTED_COLOR = -5644906;
  public static final int MSG_OUT_WEB_PREVIEW_LINE_COLOR = -7812741;
  public static final int MSG_SECRET_TIME_TEXT_COLOR = -1776928;
  public static final int MSG_SELECTED_BACKGROUND_COLOR = 1714664933;
  public static final int MSG_STICKER_NAME_TEXT_COLOR = -1;
  public static final int MSG_STICKER_REPLY_LINE_COLOR = -1;
  public static final int MSG_STICKER_REPLY_MESSAGE_TEXT_COLOR = -1;
  public static final int MSG_STICKER_REPLY_NAME_TEXT_COLOR = -1;
  public static final int MSG_STICKER_VIA_BOT_NAME_TEXT_COLOR = -1;
  public static final int MSG_TEXT_COLOR = -16777216;
  public static final int MSG_TEXT_SELECT_BACKGROUND_COLOR = 1717742051;
  public static final int MSG_WEB_PREVIEW_DURATION_TEXT_COLOR = -1;
  public static final int MSG_WEB_PREVIEW_GAME_TEXT_COLOR = -1;
  public static final int PINNED_PANEL_MESSAGE_TEXT_COLOR = -6710887;
  public static final int PINNED_PANEL_NAME_TEXT_COLOR = -12940081;
  public static final int REPLY_PANEL_MESSAGE_TEXT_COLOR = -14540254;
  public static final int REPLY_PANEL_NAME_TEXT_COLOR = -12940081;
  public static final int SECRET_CHAT_INFO_TEXT_COLOR = -1;
  public static final int SHARE_SHEET_BADGE_TEXT_COLOR = -1;
  public static final int SHARE_SHEET_COPY_TEXT_COLOR = -12940081;
  public static final int SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR = -6842473;
  public static final int SHARE_SHEET_EDIT_TEXT_COLOR = -14606047;
  public static final int SHARE_SHEET_SEND_DISABLED_TEXT_COLOR = -5000269;
  public static final int SHARE_SHEET_SEND_TEXT_COLOR = -12664327;
  public static final int STICKERS_SHEET_ADD_TEXT_COLOR = -12940081;
  public static final int STICKERS_SHEET_CLOSE_TEXT_COLOR = -12940081;
  public static final int STICKERS_SHEET_REMOVE_TEXT_COLOR = -3319206;
  public static final int STICKERS_SHEET_SEND_TEXT_COLOR = -12940081;
  public static final int STICKERS_SHEET_TITLE_TEXT_COLOR = -14606047;
  public static Drawable[] attachButtonDrawables = new Drawable[8];
  public static Drawable backgroundBluePressed;
  public static Drawable backgroundDrawableIn;
  public static Drawable backgroundDrawableInSelected;
  public static Drawable backgroundDrawableOut;
  public static Drawable backgroundDrawableOutSelected;
  public static Drawable backgroundMediaDrawableIn;
  public static Drawable backgroundMediaDrawableInSelected;
  public static Drawable backgroundMediaDrawableOut;
  public static Drawable backgroundMediaDrawableOutSelected;
  public static Drawable botInline;
  public static Drawable botLink;
  public static Drawable broadcastDrawable;
  public static Drawable broadcastMediaDrawable;
  public static Drawable checkDrawable;
  public static Drawable checkMediaDrawable;
  public static Drawable[] clockChannelDrawable = new Drawable[2];
  public static Drawable clockDrawable;
  public static Drawable clockMediaDrawable;
  public static PorterDuffColorFilter colorFilter;
  public static PorterDuffColorFilter colorPressedFilter;
  public static Drawable[] contactDrawable;
  public static Drawable[] cornerInner;
  public static Drawable[] cornerOuter = new Drawable[4];
  private static int currentColor;
  public static Drawable[] docMenuDrawable;
  public static Drawable errorDrawable;
  public static Drawable[][] fileStatesDrawable;
  public static Drawable geoInDrawable;
  public static Drawable geoOutDrawable;
  public static Drawable halfCheckDrawable;
  public static Drawable halfCheckMediaDrawable;
  public static Drawable inlineAudioDrawable;
  public static Drawable inlineDocDrawable;
  public static Drawable inlineLocationDrawable;
  private static Paint maskPaint = new Paint(1);
  public static Drawable[][] photoStatesDrawables;
  public static Drawable shareDrawable;
  public static Drawable shareIconDrawable;
  public static Drawable systemDrawable;
  public static Drawable timeBackgroundDrawable;
  public static Drawable timeStickerBackgroundDrawable;
  public static Drawable[] viewsCountDrawable;
  public static Drawable viewsMediaCountDrawable;
  public static Drawable viewsOutCountDrawable;
  
  static
  {
    cornerInner = new Drawable[4];
    viewsCountDrawable = new Drawable[2];
    contactDrawable = new Drawable[2];
    fileStatesDrawable = (Drawable[][])Array.newInstance(Drawable.class, new int[] { 10, 2 });
    photoStatesDrawables = (Drawable[][])Array.newInstance(Drawable.class, new int[] { 13, 2 });
    docMenuDrawable = new Drawable[4];
  }
  
  public static Drawable createBarSelectorDrawable(int paramInt)
  {
    return createBarSelectorDrawable(paramInt, true);
  }
  
  public static Drawable createBarSelectorDrawable(int paramInt, boolean paramBoolean)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      localObject = null;
      if (paramBoolean)
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
      }
      return new RippleDrawable(new ColorStateList(new int[][] { new int[0] }, new int[] { paramInt }), null, (Drawable)localObject);
    }
    Object localObject = new StateListDrawable();
    ColorDrawable localColorDrawable = new ColorDrawable(paramInt);
    ((StateListDrawable)localObject).addState(new int[] { 16842919 }, localColorDrawable);
    localColorDrawable = new ColorDrawable(paramInt);
    ((StateListDrawable)localObject).addState(new int[] { 16842908 }, localColorDrawable);
    localColorDrawable = new ColorDrawable(paramInt);
    ((StateListDrawable)localObject).addState(new int[] { 16842913 }, localColorDrawable);
    localColorDrawable = new ColorDrawable(paramInt);
    ((StateListDrawable)localObject).addState(new int[] { 16843518 }, localColorDrawable);
    localColorDrawable = new ColorDrawable(0);
    ((StateListDrawable)localObject).addState(new int[0], localColorDrawable);
    return (Drawable)localObject;
  }
  
  public static void loadChatResources(Context paramContext)
  {
    if (attachButtonDrawables[0] == null)
    {
      attachButtonDrawables[0] = paramContext.getResources().getDrawable(2130837519);
      attachButtonDrawables[1] = paramContext.getResources().getDrawable(2130837528);
      attachButtonDrawables[2] = paramContext.getResources().getDrawable(2130837542);
      attachButtonDrawables[3] = paramContext.getResources().getDrawable(2130837516);
      attachButtonDrawables[4] = paramContext.getResources().getDrawable(2130837525);
      attachButtonDrawables[5] = paramContext.getResources().getDrawable(2130837522);
      attachButtonDrawables[6] = paramContext.getResources().getDrawable(2130837535);
      attachButtonDrawables[7] = paramContext.getResources().getDrawable(2130837532);
    }
  }
  
  public static void loadRecources(Context paramContext)
  {
    if (backgroundDrawableIn == null)
    {
      backgroundDrawableIn = paramContext.getResources().getDrawable(2130837840);
      backgroundDrawableInSelected = paramContext.getResources().getDrawable(2130837843);
      backgroundDrawableOut = paramContext.getResources().getDrawable(2130837844);
      backgroundDrawableOutSelected = paramContext.getResources().getDrawable(2130837847);
      backgroundMediaDrawableIn = paramContext.getResources().getDrawable(2130837841);
      backgroundMediaDrawableInSelected = paramContext.getResources().getDrawable(2130837842);
      backgroundMediaDrawableOut = paramContext.getResources().getDrawable(2130837845);
      backgroundMediaDrawableOutSelected = paramContext.getResources().getDrawable(2130837846);
      checkDrawable = paramContext.getResources().getDrawable(2130837832);
      halfCheckDrawable = paramContext.getResources().getDrawable(2130837838);
      clockDrawable = paramContext.getResources().getDrawable(2130837834);
      checkMediaDrawable = paramContext.getResources().getDrawable(2130837833);
      halfCheckMediaDrawable = paramContext.getResources().getDrawable(2130837839);
      clockMediaDrawable = paramContext.getResources().getDrawable(2130837837);
      clockChannelDrawable[0] = paramContext.getResources().getDrawable(2130837835);
      clockChannelDrawable[1] = paramContext.getResources().getDrawable(2130837836);
      errorDrawable = paramContext.getResources().getDrawable(2130837848);
      timeBackgroundDrawable = paramContext.getResources().getDrawable(2130837913);
      timeStickerBackgroundDrawable = paramContext.getResources().getDrawable(2130837912);
      broadcastDrawable = paramContext.getResources().getDrawable(2130837579);
      broadcastMediaDrawable = paramContext.getResources().getDrawable(2130837580);
      systemDrawable = paramContext.getResources().getDrawable(2130838000);
      botLink = paramContext.getResources().getDrawable(2130837572);
      botInline = paramContext.getResources().getDrawable(2130837571);
      viewsCountDrawable[0] = paramContext.getResources().getDrawable(2130837945);
      viewsCountDrawable[1] = paramContext.getResources().getDrawable(2130837946);
      viewsOutCountDrawable = paramContext.getResources().getDrawable(2130837948);
      viewsMediaCountDrawable = paramContext.getResources().getDrawable(2130837947);
      fileStatesDrawable[0][0] = paramContext.getResources().getDrawable(2130837934);
      fileStatesDrawable[0][1] = paramContext.getResources().getDrawable(2130837935);
      fileStatesDrawable[1][0] = paramContext.getResources().getDrawable(2130837875);
      fileStatesDrawable[1][1] = paramContext.getResources().getDrawable(2130837876);
      fileStatesDrawable[2][0] = paramContext.getResources().getDrawable(2130837672);
      fileStatesDrawable[2][1] = paramContext.getResources().getDrawable(2130837673);
      fileStatesDrawable[3][0] = paramContext.getResources().getDrawable(2130837669);
      fileStatesDrawable[3][1] = paramContext.getResources().getDrawable(2130837674);
      fileStatesDrawable[4][0] = paramContext.getResources().getDrawable(2130837670);
      fileStatesDrawable[4][1] = paramContext.getResources().getDrawable(2130837671);
      fileStatesDrawable[5][0] = paramContext.getResources().getDrawable(2130837931);
      fileStatesDrawable[5][1] = paramContext.getResources().getDrawable(2130837932);
      fileStatesDrawable[6][0] = paramContext.getResources().getDrawable(2130837873);
      fileStatesDrawable[6][1] = paramContext.getResources().getDrawable(2130837874);
      fileStatesDrawable[7][0] = paramContext.getResources().getDrawable(2130837666);
      fileStatesDrawable[7][1] = paramContext.getResources().getDrawable(2130837667);
      fileStatesDrawable[8][0] = paramContext.getResources().getDrawable(2130837663);
      fileStatesDrawable[8][1] = paramContext.getResources().getDrawable(2130837668);
      fileStatesDrawable[9][0] = paramContext.getResources().getDrawable(2130837664);
      fileStatesDrawable[9][1] = paramContext.getResources().getDrawable(2130837665);
      photoStatesDrawables[0][0] = paramContext.getResources().getDrawable(2130837905);
      photoStatesDrawables[0][1] = paramContext.getResources().getDrawable(2130837910);
      photoStatesDrawables[1][0] = paramContext.getResources().getDrawable(2130837896);
      photoStatesDrawables[1][1] = paramContext.getResources().getDrawable(2130837901);
      photoStatesDrawables[2][0] = paramContext.getResources().getDrawable(2130837903);
      photoStatesDrawables[2][1] = paramContext.getResources().getDrawable(2130837904);
      photoStatesDrawables[3][0] = paramContext.getResources().getDrawable(2130837941);
      photoStatesDrawables[3][1] = paramContext.getResources().getDrawable(2130837942);
      Drawable[] arrayOfDrawable1 = photoStatesDrawables[4];
      Drawable[] arrayOfDrawable2 = photoStatesDrawables[4];
      Drawable localDrawable = paramContext.getResources().getDrawable(2130837585);
      arrayOfDrawable2[1] = localDrawable;
      arrayOfDrawable1[0] = localDrawable;
      arrayOfDrawable1 = photoStatesDrawables[5];
      arrayOfDrawable2 = photoStatesDrawables[5];
      localDrawable = paramContext.getResources().getDrawable(2130837596);
      arrayOfDrawable2[1] = localDrawable;
      arrayOfDrawable1[0] = localDrawable;
      arrayOfDrawable1 = photoStatesDrawables[6];
      arrayOfDrawable2 = photoStatesDrawables[6];
      localDrawable = paramContext.getResources().getDrawable(2130837902);
      arrayOfDrawable2[1] = localDrawable;
      arrayOfDrawable1[0] = localDrawable;
      photoStatesDrawables[7][0] = paramContext.getResources().getDrawable(2130837908);
      photoStatesDrawables[7][1] = paramContext.getResources().getDrawable(2130837909);
      photoStatesDrawables[8][0] = paramContext.getResources().getDrawable(2130837899);
      photoStatesDrawables[8][1] = paramContext.getResources().getDrawable(2130837900);
      photoStatesDrawables[9][0] = paramContext.getResources().getDrawable(2130837649);
      photoStatesDrawables[9][1] = paramContext.getResources().getDrawable(2130837649);
      photoStatesDrawables[10][0] = paramContext.getResources().getDrawable(2130837906);
      photoStatesDrawables[10][1] = paramContext.getResources().getDrawable(2130837907);
      photoStatesDrawables[11][0] = paramContext.getResources().getDrawable(2130837897);
      photoStatesDrawables[11][1] = paramContext.getResources().getDrawable(2130837898);
      photoStatesDrawables[12][0] = paramContext.getResources().getDrawable(2130837647);
      photoStatesDrawables[12][1] = paramContext.getResources().getDrawable(2130837648);
      docMenuDrawable[0] = paramContext.getResources().getDrawable(2130837644);
      docMenuDrawable[1] = paramContext.getResources().getDrawable(2130837646);
      docMenuDrawable[2] = paramContext.getResources().getDrawable(2130837645);
      docMenuDrawable[3] = paramContext.getResources().getDrawable(2130838024);
      contactDrawable[0] = paramContext.getResources().getDrawable(2130837624);
      contactDrawable[1] = paramContext.getResources().getDrawable(2130837625);
      shareDrawable = paramContext.getResources().getDrawable(2130837981);
      shareIconDrawable = paramContext.getResources().getDrawable(2130837980);
      geoInDrawable = paramContext.getResources().getDrawable(2130837803);
      geoOutDrawable = paramContext.getResources().getDrawable(2130837804);
      cornerOuter[0] = paramContext.getResources().getDrawable(2130837632);
      cornerOuter[1] = paramContext.getResources().getDrawable(2130837633);
      cornerOuter[2] = paramContext.getResources().getDrawable(2130837631);
      cornerOuter[3] = paramContext.getResources().getDrawable(2130837630);
      cornerInner[0] = paramContext.getResources().getDrawable(2130837629);
      cornerInner[1] = paramContext.getResources().getDrawable(2130837628);
      cornerInner[2] = paramContext.getResources().getDrawable(2130837627);
      cornerInner[3] = paramContext.getResources().getDrawable(2130837626);
      inlineDocDrawable = paramContext.getResources().getDrawable(2130837564);
      inlineAudioDrawable = paramContext.getResources().getDrawable(2130837575);
      inlineLocationDrawable = paramContext.getResources().getDrawable(2130837574);
    }
    int i = ApplicationLoader.getServiceMessageColor();
    if (currentColor != i)
    {
      colorFilter = new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY);
      colorPressedFilter = new PorterDuffColorFilter(ApplicationLoader.getServiceSelectedMessageColor(), PorterDuff.Mode.MULTIPLY);
      currentColor = i;
      i = 0;
      while (i < 4)
      {
        cornerOuter[i].setColorFilter(colorFilter);
        cornerInner[i].setColorFilter(colorFilter);
        i += 1;
      }
      timeStickerBackgroundDrawable.setColorFilter(colorFilter);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ActionBar/Theme.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */