package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.view.ViewParent;
import android.view.ViewStructure;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.FileDownloadProgressListener;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessageObject.TextLayoutBlock;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.GeoPoint;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonGame;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRequestGeoLocation;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonSwitchInline;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonUrl;
import org.telegram.tgnet.TLRPC.TL_messageFwdHeader;
import org.telegram.tgnet.TLRPC.TL_messageMediaContact;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaGame;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.RadialProgress;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SeekBar.SeekBarDelegate;
import org.telegram.ui.Components.SeekBarWaveform;
import org.telegram.ui.Components.StaticLayoutEx;
import org.telegram.ui.Components.URLSpanBotCommand;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.PhotoViewer;

public class ChatMessageCell
  extends BaseCell
  implements SeekBar.SeekBarDelegate, ImageReceiver.ImageReceiverDelegate, MediaController.FileDownloadProgressListener
{
  private static final int DOCUMENT_ATTACH_TYPE_AUDIO = 3;
  private static final int DOCUMENT_ATTACH_TYPE_DOCUMENT = 1;
  private static final int DOCUMENT_ATTACH_TYPE_GIF = 2;
  private static final int DOCUMENT_ATTACH_TYPE_MUSIC = 5;
  private static final int DOCUMENT_ATTACH_TYPE_NONE = 0;
  private static final int DOCUMENT_ATTACH_TYPE_STICKER = 6;
  private static final int DOCUMENT_ATTACH_TYPE_VIDEO = 4;
  private static TextPaint audioPerformerPaint;
  private static TextPaint audioTimePaint;
  private static TextPaint audioTitlePaint;
  private static TextPaint botButtonPaint;
  private static Paint botProgressPaint;
  private static TextPaint contactNamePaint;
  private static TextPaint contactPhonePaint;
  private static Paint deleteProgressPaint;
  private static Paint docBackPaint;
  private static TextPaint docNamePaint;
  private static TextPaint durationPaint;
  private static TextPaint forwardNamePaint;
  private static TextPaint gamePaint;
  private static TextPaint infoPaint;
  private static TextPaint locationAddressPaint;
  private static TextPaint locationTitlePaint;
  private static TextPaint namePaint;
  private static Paint replyLinePaint;
  private static TextPaint replyNamePaint;
  private static TextPaint replyTextPaint;
  private static TextPaint timePaint;
  private static Paint urlPaint;
  private static Paint urlSelectionPaint;
  private int TAG;
  private boolean allowAssistant;
  private StaticLayout authorLayout;
  private int authorX;
  private int availableTimeWidth;
  private AvatarDrawable avatarDrawable;
  private ImageReceiver avatarImage;
  private boolean avatarPressed;
  private int backgroundDrawableLeft;
  private int backgroundWidth = 100;
  private ArrayList<BotButton> botButtons = new ArrayList();
  private HashMap<String, BotButton> botButtonsByData = new HashMap();
  private int buttonPressed;
  private int buttonState;
  private int buttonX;
  private int buttonY;
  private boolean cancelLoading;
  private int captionHeight;
  private StaticLayout captionLayout;
  private int captionX;
  private int captionY;
  private AvatarDrawable contactAvatarDrawable;
  private Drawable currentBackgroundDrawable;
  private TLRPC.Chat currentChat;
  private TLRPC.Chat currentForwardChannel;
  private String currentForwardNameString;
  private TLRPC.User currentForwardUser;
  private MessageObject currentMessageObject;
  private String currentNameString;
  private TLRPC.FileLocation currentPhoto;
  private String currentPhotoFilter;
  private String currentPhotoFilterThumb;
  private TLRPC.PhotoSize currentPhotoObject;
  private TLRPC.PhotoSize currentPhotoObjectThumb;
  private TLRPC.FileLocation currentReplyPhoto;
  private String currentTimeString;
  private String currentUrl;
  private TLRPC.User currentUser;
  private TLRPC.User currentViaBotUser;
  private String currentViewsString;
  private ChatMessageCellDelegate delegate;
  private RectF deleteProgressRect = new RectF();
  private StaticLayout descriptionLayout;
  private int descriptionX;
  private int descriptionY;
  private boolean disallowLongPress;
  private StaticLayout docTitleLayout;
  private int docTitleOffsetX;
  private TLRPC.Document documentAttach;
  private int documentAttachType;
  private boolean drawBackground = true;
  private boolean drawForwardedName;
  private boolean drawImageButton;
  private boolean drawName;
  private boolean drawNameLayout;
  private boolean drawPhotoImage;
  private boolean drawShareButton;
  private boolean drawTime = true;
  private StaticLayout durationLayout;
  private int durationWidth;
  private int firstVisibleBlockNum;
  private boolean forwardBotPressed;
  private boolean forwardName;
  private float[] forwardNameOffsetX = new float[2];
  private boolean forwardNamePressed;
  private int forwardNameX;
  private int forwardNameY;
  private StaticLayout[] forwardedNameLayout = new StaticLayout[2];
  private int forwardedNameWidth;
  private boolean gamePreviewPressed;
  private boolean hasGamePreview;
  private boolean hasLinkPreview;
  private boolean imagePressed;
  private boolean inLayout;
  private StaticLayout infoLayout;
  private int infoWidth;
  private boolean isAvatarVisible;
  public boolean isChat;
  private boolean isCheckPressed = true;
  private boolean isHighlighted;
  private boolean isPressed;
  private boolean isSmallImage;
  private int keyboardHeight;
  private int lastDeleteDate;
  private int lastSendState;
  private String lastTimeString;
  private int lastViewsCount;
  private int lastVisibleBlockNum;
  private int layoutHeight;
  private int layoutWidth;
  private int linkBlockNum;
  private int linkPreviewHeight;
  private boolean linkPreviewPressed;
  private int linkSelectionBlockNum;
  private boolean mediaBackground;
  private int mediaOffsetY;
  private StaticLayout nameLayout;
  private float nameOffsetX;
  private int nameWidth;
  private float nameX;
  private float nameY;
  private int namesOffset;
  private boolean needNewVisiblePart;
  private boolean needReplyImage;
  private boolean otherPressed;
  private int otherX;
  private int otherY;
  private StaticLayout performerLayout;
  private int performerX;
  private ImageReceiver photoImage;
  private boolean photoNotSet;
  private int pressedBotButton;
  private ClickableSpan pressedLink;
  private int pressedLinkType;
  private RadialProgress radialProgress;
  private RectF rect = new RectF();
  private ImageReceiver replyImageReceiver;
  private StaticLayout replyNameLayout;
  private float replyNameOffset;
  private int replyNameWidth;
  private boolean replyPressed;
  private int replyStartX;
  private int replyStartY;
  private StaticLayout replyTextLayout;
  private float replyTextOffset;
  private int replyTextWidth;
  private Rect scrollRect = new Rect();
  private SeekBar seekBar;
  private SeekBarWaveform seekBarWaveform;
  private int seekBarX;
  private int seekBarY;
  private boolean sharePressed;
  private int shareStartX;
  private int shareStartY;
  private StaticLayout siteNameLayout;
  private StaticLayout songLayout;
  private int songX;
  private int substractBackgroundHeight;
  private int textX;
  private int textY;
  private int timeAudioX;
  private StaticLayout timeLayout;
  private int timeTextWidth;
  private int timeWidth;
  private int timeWidthAudio;
  private int timeX;
  private StaticLayout titleLayout;
  private int titleX;
  private int totalHeight;
  private int totalVisibleBlocksCount;
  private ArrayList<LinkPath> urlPath = new ArrayList();
  private ArrayList<LinkPath> urlPathCache = new ArrayList();
  private ArrayList<LinkPath> urlPathSelection = new ArrayList();
  private boolean useSeekBarWaweform;
  private int viaNameWidth;
  private int viaWidth;
  private StaticLayout videoInfoLayout;
  private StaticLayout viewsLayout;
  private int viewsTextWidth;
  private boolean wasLayout;
  private int widthForButtons;
  
  public ChatMessageCell(Context paramContext)
  {
    super(paramContext);
    if (infoPaint == null)
    {
      infoPaint = new TextPaint(1);
      docNamePaint = new TextPaint(1);
      docNamePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      docBackPaint = new Paint(1);
      deleteProgressPaint = new Paint(1);
      deleteProgressPaint.setColor(-1776928);
      botProgressPaint = new Paint(1);
      botProgressPaint.setColor(-1);
      botProgressPaint.setStrokeCap(Paint.Cap.ROUND);
      botProgressPaint.setStyle(Paint.Style.STROKE);
      locationTitlePaint = new TextPaint(1);
      locationTitlePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      locationAddressPaint = new TextPaint(1);
      urlPaint = new Paint();
      urlPaint.setColor(862104035);
      urlSelectionPaint = new Paint();
      urlSelectionPaint.setColor(1717742051);
      audioTimePaint = new TextPaint(1);
      audioTitlePaint = new TextPaint(1);
      audioTitlePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      audioPerformerPaint = new TextPaint(1);
      botButtonPaint = new TextPaint(1);
      botButtonPaint.setColor(-1);
      botButtonPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      contactNamePaint = new TextPaint(1);
      contactNamePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      contactPhonePaint = new TextPaint(1);
      durationPaint = new TextPaint(1);
      durationPaint.setColor(-1);
      gamePaint = new TextPaint(1);
      gamePaint.setColor(-1);
      gamePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      timePaint = new TextPaint(1);
      namePaint = new TextPaint(1);
      namePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      forwardNamePaint = new TextPaint(1);
      replyNamePaint = new TextPaint(1);
      replyNamePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      replyTextPaint = new TextPaint(1);
      replyTextPaint.linkColor = -14255946;
      replyLinePaint = new Paint();
    }
    botProgressPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
    infoPaint.setTextSize(AndroidUtilities.dp(12.0F));
    docNamePaint.setTextSize(AndroidUtilities.dp(15.0F));
    locationTitlePaint.setTextSize(AndroidUtilities.dp(15.0F));
    locationAddressPaint.setTextSize(AndroidUtilities.dp(13.0F));
    audioTimePaint.setTextSize(AndroidUtilities.dp(12.0F));
    audioTitlePaint.setTextSize(AndroidUtilities.dp(16.0F));
    audioPerformerPaint.setTextSize(AndroidUtilities.dp(15.0F));
    botButtonPaint.setTextSize(AndroidUtilities.dp(15.0F));
    contactNamePaint.setTextSize(AndroidUtilities.dp(15.0F));
    contactPhonePaint.setTextSize(AndroidUtilities.dp(13.0F));
    durationPaint.setTextSize(AndroidUtilities.dp(12.0F));
    timePaint.setTextSize(AndroidUtilities.dp(12.0F));
    namePaint.setTextSize(AndroidUtilities.dp(14.0F));
    forwardNamePaint.setTextSize(AndroidUtilities.dp(14.0F));
    replyNamePaint.setTextSize(AndroidUtilities.dp(14.0F));
    replyTextPaint.setTextSize(AndroidUtilities.dp(14.0F));
    gamePaint.setTextSize(AndroidUtilities.dp(13.0F));
    this.avatarImage = new ImageReceiver(this);
    this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0F));
    this.avatarDrawable = new AvatarDrawable();
    this.replyImageReceiver = new ImageReceiver(this);
    this.TAG = MediaController.getInstance().generateObserverTag();
    this.contactAvatarDrawable = new AvatarDrawable();
    this.photoImage = new ImageReceiver(this);
    this.photoImage.setDelegate(this);
    this.radialProgress = new RadialProgress(this);
    this.seekBar = new SeekBar(paramContext);
    this.seekBar.setDelegate(this);
    this.seekBarWaveform = new SeekBarWaveform(paramContext);
    this.seekBarWaveform.setDelegate(this);
    this.seekBarWaveform.setParentView(this);
    this.radialProgress = new RadialProgress(this);
  }
  
  private void calcBackgroundWidth(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((this.hasLinkPreview) || (this.hasGamePreview) || (paramInt1 - this.currentMessageObject.lastLineWidth < paramInt2))
    {
      this.totalHeight += AndroidUtilities.dp(14.0F);
      this.backgroundWidth = (Math.max(paramInt3, this.currentMessageObject.lastLineWidth) + AndroidUtilities.dp(31.0F));
      this.backgroundWidth = Math.max(this.backgroundWidth, this.timeWidth + AndroidUtilities.dp(31.0F));
      return;
    }
    paramInt1 = paramInt3 - this.currentMessageObject.lastLineWidth;
    if ((paramInt1 >= 0) && (paramInt1 <= paramInt2))
    {
      this.backgroundWidth = (paramInt3 + paramInt2 - paramInt1 + AndroidUtilities.dp(31.0F));
      return;
    }
    this.backgroundWidth = (Math.max(paramInt3, this.currentMessageObject.lastLineWidth + paramInt2) + AndroidUtilities.dp(31.0F));
  }
  
  private boolean checkAudioMotionEvent(MotionEvent paramMotionEvent)
  {
    boolean bool2;
    if ((this.documentAttachType != 3) && (this.documentAttachType != 5)) {
      bool2 = false;
    }
    int i;
    boolean bool1;
    label159:
    label192:
    do
    {
      return bool2;
      i = (int)paramMotionEvent.getX();
      int j = (int)paramMotionEvent.getY();
      if (this.useSeekBarWaweform)
      {
        bool1 = this.seekBarWaveform.onTouch(paramMotionEvent.getAction(), paramMotionEvent.getX() - this.seekBarX - AndroidUtilities.dp(13.0F), paramMotionEvent.getY() - this.seekBarY);
        if (!bool1) {
          break label192;
        }
        if ((this.useSeekBarWaweform) || (paramMotionEvent.getAction() != 0)) {
          break label159;
        }
        getParent().requestDisallowInterceptTouchEvent(true);
      }
      for (;;)
      {
        this.disallowLongPress = true;
        invalidate();
        return bool1;
        bool1 = this.seekBar.onTouch(paramMotionEvent.getAction(), paramMotionEvent.getX() - this.seekBarX, paramMotionEvent.getY() - this.seekBarY);
        break;
        if ((this.useSeekBarWaweform) && (!this.seekBarWaveform.isStartDraging()) && (paramMotionEvent.getAction() == 1)) {
          didPressedButton(true);
        }
      }
      int k = AndroidUtilities.dp(36.0F);
      if ((this.buttonState == 0) || (this.buttonState == 1) || (this.buttonState == 2)) {
        if ((i >= this.buttonX - AndroidUtilities.dp(12.0F)) && (i <= this.buttonX - AndroidUtilities.dp(12.0F) + this.backgroundWidth) && (j >= this.namesOffset + this.mediaOffsetY) && (j <= this.layoutHeight)) {
          i = 1;
        }
      }
      for (;;)
      {
        if (paramMotionEvent.getAction() == 0)
        {
          bool2 = bool1;
          if (i == 0) {
            break;
          }
          this.buttonPressed = 1;
          invalidate();
          this.radialProgress.swapBackground(getDrawableForCurrentState());
          return true;
          i = 0;
          continue;
          if ((i >= this.buttonX) && (i <= this.buttonX + k) && (j >= this.buttonY) && (j <= this.buttonY + k)) {}
          for (i = 1;; i = 0) {
            break;
          }
        }
      }
      bool2 = bool1;
    } while (this.buttonPressed == 0);
    if (paramMotionEvent.getAction() == 1)
    {
      this.buttonPressed = 0;
      playSoundEffect(0);
      didPressedButton(true);
      invalidate();
    }
    for (;;)
    {
      this.radialProgress.swapBackground(getDrawableForCurrentState());
      return bool1;
      if (paramMotionEvent.getAction() == 3)
      {
        this.buttonPressed = 0;
        invalidate();
      }
      else if ((paramMotionEvent.getAction() == 2) && (i == 0))
      {
        this.buttonPressed = 0;
        invalidate();
      }
    }
  }
  
  private boolean checkBotButtonMotionEvent(MotionEvent paramMotionEvent)
  {
    if (this.botButtons.isEmpty()) {}
    label200:
    do
    {
      for (;;)
      {
        return false;
        int k = (int)paramMotionEvent.getX();
        int m = (int)paramMotionEvent.getY();
        if (paramMotionEvent.getAction() != 0) {
          break;
        }
        int i;
        int j;
        if (this.currentMessageObject.isOutOwner())
        {
          i = getMeasuredWidth() - this.widthForButtons - AndroidUtilities.dp(10.0F);
          j = 0;
        }
        for (;;)
        {
          if (j >= this.botButtons.size()) {
            break label200;
          }
          paramMotionEvent = (BotButton)this.botButtons.get(j);
          int n = paramMotionEvent.y + this.layoutHeight - AndroidUtilities.dp(2.0F);
          if ((k >= paramMotionEvent.x + i) && (k <= paramMotionEvent.x + i + paramMotionEvent.width) && (m >= n) && (m <= paramMotionEvent.height + n))
          {
            this.pressedBotButton = j;
            invalidate();
            return true;
            i = this.backgroundDrawableLeft;
            if (this.mediaBackground) {}
            for (float f = 1.0F;; f = 7.0F)
            {
              i += AndroidUtilities.dp(f);
              break;
            }
          }
          j += 1;
        }
      }
    } while ((paramMotionEvent.getAction() != 1) || (this.pressedBotButton == -1));
    playSoundEffect(0);
    this.delegate.didPressedBotButton(this, ((BotButton)this.botButtons.get(this.pressedBotButton)).button);
    this.pressedBotButton = -1;
    invalidate();
    return false;
  }
  
  private boolean checkCaptionMotionEvent(MotionEvent paramMotionEvent)
  {
    if ((!(this.currentMessageObject.caption instanceof Spannable)) || (this.captionLayout == null)) {
      return false;
    }
    int i;
    int j;
    if ((paramMotionEvent.getAction() == 0) || (((this.linkPreviewPressed) || (this.pressedLink != null)) && (paramMotionEvent.getAction() == 1)))
    {
      i = (int)paramMotionEvent.getX();
      j = (int)paramMotionEvent.getY();
      if ((i < this.captionX) || (i > this.captionX + this.backgroundWidth) || (j < this.captionY) || (j > this.captionY + this.captionHeight)) {
        break label392;
      }
      if (paramMotionEvent.getAction() != 0) {
        break label359;
      }
    }
    for (;;)
    {
      try
      {
        i -= this.captionX;
        int k = this.captionY;
        j = this.captionLayout.getLineForVertical(j - k);
        k = this.captionLayout.getOffsetForHorizontal(j, i);
        float f = this.captionLayout.getLineLeft(j);
        if ((f <= i) && (this.captionLayout.getLineWidth(j) + f >= i))
        {
          paramMotionEvent = (Spannable)this.currentMessageObject.caption;
          Object localObject = (ClickableSpan[])paramMotionEvent.getSpans(k, k, ClickableSpan.class);
          j = 0;
          if (localObject.length == 0) {
            break label400;
          }
          i = j;
          if (localObject.length != 0)
          {
            i = j;
            if ((localObject[0] instanceof URLSpanBotCommand))
            {
              i = j;
              if (!URLSpanBotCommand.enabled) {
                break label400;
              }
            }
          }
          if (i == 0)
          {
            this.pressedLink = localObject[0];
            this.pressedLinkType = 3;
            resetUrlPaths(false);
            try
            {
              localObject = obtainNewUrlPath(false);
              i = paramMotionEvent.getSpanStart(this.pressedLink);
              ((LinkPath)localObject).setCurrentLayout(this.captionLayout, i, 0.0F);
              this.captionLayout.getSelectionPath(i, paramMotionEvent.getSpanEnd(this.pressedLink), (Path)localObject);
              invalidate();
              return true;
            }
            catch (Exception paramMotionEvent)
            {
              FileLog.e("tmessages", paramMotionEvent);
              continue;
            }
          }
        }
        return false;
      }
      catch (Exception paramMotionEvent)
      {
        FileLog.e("tmessages", paramMotionEvent);
      }
      for (;;)
      {
        label359:
        if (this.pressedLinkType == 3)
        {
          this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, false);
          resetPressedLink(3);
          return true;
          label392:
          resetPressedLink(3);
        }
      }
      label400:
      i = 1;
    }
  }
  
  private boolean checkGameMotionEvent(MotionEvent paramMotionEvent)
  {
    if (!this.hasGamePreview) {
      return false;
    }
    int i = (int)paramMotionEvent.getX();
    int j = (int)paramMotionEvent.getY();
    if (paramMotionEvent.getAction() == 0)
    {
      if ((this.drawPhotoImage) && (this.photoImage.isInsideImage(i, j)))
      {
        this.gamePreviewPressed = true;
        return true;
      }
      if ((this.descriptionLayout == null) || (j < this.descriptionY)) {}
    }
    for (;;)
    {
      try
      {
        i -= this.textX + AndroidUtilities.dp(10.0F) + this.descriptionX;
        int k = this.descriptionY;
        j = this.descriptionLayout.getLineForVertical(j - k);
        k = this.descriptionLayout.getOffsetForHorizontal(j, i);
        float f = this.descriptionLayout.getLineLeft(j);
        if ((f <= i) && (this.descriptionLayout.getLineWidth(j) + f >= i))
        {
          paramMotionEvent = (Spannable)this.currentMessageObject.linkDescription;
          Object localObject = (ClickableSpan[])paramMotionEvent.getSpans(k, k, ClickableSpan.class);
          j = 0;
          if (localObject.length == 0) {
            break label497;
          }
          i = j;
          if (localObject.length != 0)
          {
            i = j;
            if ((localObject[0] instanceof URLSpanBotCommand))
            {
              i = j;
              if (!URLSpanBotCommand.enabled) {
                break label497;
              }
            }
          }
          if (i == 0)
          {
            this.pressedLink = localObject[0];
            this.linkBlockNum = -10;
            this.pressedLinkType = 2;
            resetUrlPaths(false);
            try
            {
              localObject = obtainNewUrlPath(false);
              i = paramMotionEvent.getSpanStart(this.pressedLink);
              ((LinkPath)localObject).setCurrentLayout(this.descriptionLayout, i, 0.0F);
              this.descriptionLayout.getSelectionPath(i, paramMotionEvent.getSpanEnd(this.pressedLink), (Path)localObject);
              invalidate();
              return true;
            }
            catch (Exception paramMotionEvent)
            {
              FileLog.e("tmessages", paramMotionEvent);
              continue;
            }
          }
        }
        return false;
      }
      catch (Exception paramMotionEvent)
      {
        FileLog.e("tmessages", paramMotionEvent);
      }
      for (;;)
      {
        if (paramMotionEvent.getAction() == 1)
        {
          if ((this.pressedLinkType == 2) || (this.gamePreviewPressed))
          {
            if (this.pressedLink != null)
            {
              if ((this.pressedLink instanceof URLSpan)) {
                Browser.openUrl(getContext(), ((URLSpan)this.pressedLink).getURL());
              }
              for (;;)
              {
                resetPressedLink(2);
                break;
                this.pressedLink.onClick(this);
              }
            }
            this.gamePreviewPressed = false;
            i = 0;
            for (;;)
            {
              if (i < this.botButtons.size())
              {
                paramMotionEvent = (BotButton)this.botButtons.get(i);
                if ((paramMotionEvent.button instanceof TLRPC.TL_keyboardButtonGame))
                {
                  playSoundEffect(0);
                  this.delegate.didPressedBotButton(this, paramMotionEvent.button);
                  invalidate();
                }
              }
              else
              {
                resetPressedLink(2);
                return true;
              }
              i += 1;
            }
          }
          resetPressedLink(2);
        }
      }
      label497:
      i = 1;
    }
  }
  
  private boolean checkLinkPreviewMotionEvent(MotionEvent paramMotionEvent)
  {
    if ((this.currentMessageObject.type != 0) || (!this.hasLinkPreview)) {
      return false;
    }
    int i = (int)paramMotionEvent.getX();
    int j = (int)paramMotionEvent.getY();
    if ((i >= this.textX) && (i <= this.textX + this.backgroundWidth) && (j >= this.textY + this.currentMessageObject.textHeight) && (j <= this.textY + this.currentMessageObject.textHeight + this.linkPreviewHeight + AndroidUtilities.dp(8.0F)))
    {
      if (paramMotionEvent.getAction() != 0) {
        break label557;
      }
      if ((this.documentAttachType != 1) && (this.drawPhotoImage) && (this.photoImage.isInsideImage(i, j)))
      {
        if ((this.drawImageButton) && (this.buttonState != -1) && (i >= this.buttonX) && (i <= this.buttonX + AndroidUtilities.dp(48.0F)) && (j >= this.buttonY) && (j <= this.buttonY + AndroidUtilities.dp(48.0F)))
        {
          this.buttonPressed = 1;
          return true;
        }
        this.linkPreviewPressed = true;
        paramMotionEvent = this.currentMessageObject.messageOwner.media.webpage;
        if ((this.documentAttachType == 2) && (this.buttonState == -1) && (MediaController.getInstance().canAutoplayGifs()) && ((this.photoImage.getAnimation() == null) || (!TextUtils.isEmpty(paramMotionEvent.embed_url))))
        {
          this.linkPreviewPressed = false;
          return false;
        }
        return true;
      }
      if ((this.descriptionLayout == null) || (j < this.descriptionY)) {}
    }
    for (;;)
    {
      try
      {
        i -= this.textX + AndroidUtilities.dp(10.0F) + this.descriptionX;
        int k = this.descriptionY;
        j = this.descriptionLayout.getLineForVertical(j - k);
        k = this.descriptionLayout.getOffsetForHorizontal(j, i);
        float f = this.descriptionLayout.getLineLeft(j);
        if ((f <= i) && (this.descriptionLayout.getLineWidth(j) + f >= i))
        {
          paramMotionEvent = (Spannable)this.currentMessageObject.linkDescription;
          Object localObject = (ClickableSpan[])paramMotionEvent.getSpans(k, k, ClickableSpan.class);
          j = 0;
          if (localObject.length == 0) {
            break label931;
          }
          i = j;
          if (localObject.length != 0)
          {
            i = j;
            if ((localObject[0] instanceof URLSpanBotCommand))
            {
              i = j;
              if (!URLSpanBotCommand.enabled) {
                break label931;
              }
            }
          }
          if (i == 0)
          {
            this.pressedLink = localObject[0];
            this.linkBlockNum = -10;
            this.pressedLinkType = 2;
            resetUrlPaths(false);
            try
            {
              localObject = obtainNewUrlPath(false);
              i = paramMotionEvent.getSpanStart(this.pressedLink);
              ((LinkPath)localObject).setCurrentLayout(this.descriptionLayout, i, 0.0F);
              this.descriptionLayout.getSelectionPath(i, paramMotionEvent.getSpanEnd(this.pressedLink), (Path)localObject);
              invalidate();
              return true;
            }
            catch (Exception paramMotionEvent)
            {
              FileLog.e("tmessages", paramMotionEvent);
              continue;
            }
          }
        }
        return false;
      }
      catch (Exception paramMotionEvent)
      {
        FileLog.e("tmessages", paramMotionEvent);
      }
      for (;;)
      {
        label557:
        if (paramMotionEvent.getAction() == 1) {
          if ((this.pressedLinkType == 2) || (this.buttonPressed != 0) || (this.linkPreviewPressed))
          {
            if (this.buttonPressed != 0)
            {
              if (paramMotionEvent.getAction() == 1)
              {
                this.buttonPressed = 0;
                playSoundEffect(0);
                didPressedButton(false);
                invalidate();
              }
            }
            else
            {
              if (this.pressedLink != null)
              {
                if ((this.pressedLink instanceof URLSpan)) {
                  Browser.openUrl(getContext(), ((URLSpan)this.pressedLink).getURL());
                }
                for (;;)
                {
                  resetPressedLink(2);
                  break;
                  this.pressedLink.onClick(this);
                }
              }
              if ((this.documentAttachType == 2) && (this.drawImageButton)) {
                if (this.buttonState == -1) {
                  if (MediaController.getInstance().canAutoplayGifs()) {
                    this.delegate.didPressedImage(this);
                  }
                }
              }
              for (;;)
              {
                resetPressedLink(2);
                return true;
                this.buttonState = 2;
                this.currentMessageObject.audioProgress = 1.0F;
                this.photoImage.setAllowStartAnimation(false);
                this.photoImage.stopAnimation();
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                invalidate();
                playSoundEffect(0);
                continue;
                if ((this.buttonState == 2) || (this.buttonState == 0))
                {
                  didPressedButton(false);
                  playSoundEffect(0);
                  continue;
                  paramMotionEvent = this.currentMessageObject.messageOwner.media.webpage;
                  if ((paramMotionEvent != null) && (Build.VERSION.SDK_INT >= 16) && (!TextUtils.isEmpty(paramMotionEvent.embed_url)))
                  {
                    this.delegate.needOpenWebView(paramMotionEvent.embed_url, paramMotionEvent.site_name, paramMotionEvent.description, paramMotionEvent.url, paramMotionEvent.embed_width, paramMotionEvent.embed_height);
                  }
                  else if (this.buttonState == -1)
                  {
                    this.delegate.didPressedImage(this);
                    playSoundEffect(0);
                  }
                  else if (paramMotionEvent != null)
                  {
                    Browser.openUrl(getContext(), paramMotionEvent.url);
                  }
                }
              }
            }
          }
          else {
            resetPressedLink(2);
          }
        }
      }
      label931:
      i = 1;
    }
  }
  
  private boolean checkNeedDrawShareButton(MessageObject paramMessageObject)
  {
    boolean bool2 = true;
    boolean bool1;
    if (paramMessageObject.type == 13) {
      bool1 = false;
    }
    label230:
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              return bool1;
              if ((paramMessageObject.messageOwner.fwd_from == null) || (paramMessageObject.messageOwner.fwd_from.channel_id == 0)) {
                break;
              }
              bool1 = bool2;
            } while (!paramMessageObject.isOut());
            if (!paramMessageObject.isFromUser()) {
              break label230;
            }
            if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaEmpty)) || (paramMessageObject.messageOwner.media == null) || (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) && (!(paramMessageObject.messageOwner.media.webpage instanceof TLRPC.TL_webPage)))) {
              return false;
            }
            localObject = MessagesController.getInstance().getUser(Integer.valueOf(paramMessageObject.messageOwner.from_id));
            if (localObject == null) {
              break;
            }
            bool1 = bool2;
          } while (((TLRPC.User)localObject).bot);
          if ((!paramMessageObject.isMegagroup()) || (paramMessageObject.isOut())) {
            break label296;
          }
          Object localObject = MessagesController.getInstance().getChat(Integer.valueOf(paramMessageObject.messageOwner.to_id.channel_id));
          if ((localObject == null) || (((TLRPC.Chat)localObject).username == null) || (((TLRPC.Chat)localObject).username.length() <= 0) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact))) {
            break;
          }
          bool1 = bool2;
        } while (!(paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo));
        return false;
        if (((paramMessageObject.messageOwner.from_id >= 0) && (!paramMessageObject.messageOwner.post)) || (paramMessageObject.messageOwner.to_id.channel_id == 0)) {
          break label296;
        }
        if (paramMessageObject.messageOwner.via_bot_id != 0) {
          break;
        }
        bool1 = bool2;
      } while (paramMessageObject.messageOwner.reply_to_msg_id == 0);
      bool1 = bool2;
    } while (paramMessageObject.type != 13);
    label296:
    return false;
  }
  
  private boolean checkOtherButtonMotionEvent(MotionEvent paramMotionEvent)
  {
    if (((this.documentAttachType != 1) && (this.currentMessageObject.type != 12) && (this.documentAttachType != 5) && (this.documentAttachType != 4) && (this.documentAttachType != 2) && (this.currentMessageObject.type != 8)) || (this.hasGamePreview)) {}
    do
    {
      int i;
      int j;
      do
      {
        return false;
        i = (int)paramMotionEvent.getX();
        j = (int)paramMotionEvent.getY();
        if (paramMotionEvent.getAction() != 0) {
          break;
        }
      } while ((i < this.otherX - AndroidUtilities.dp(20.0F)) || (i > this.otherX + AndroidUtilities.dp(20.0F)) || (j < this.otherY - AndroidUtilities.dp(4.0F)) || (j > this.otherY + AndroidUtilities.dp(30.0F)));
      this.otherPressed = true;
      return true;
    } while ((paramMotionEvent.getAction() != 1) || (!this.otherPressed));
    this.otherPressed = false;
    playSoundEffect(0);
    this.delegate.didPressedOther(this);
    return false;
  }
  
  private boolean checkPhotoImageMotionEvent(MotionEvent paramMotionEvent)
  {
    boolean bool2;
    if ((!this.drawPhotoImage) && (this.documentAttachType != 1)) {
      bool2 = false;
    }
    label406:
    do
    {
      boolean bool1;
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                return bool2;
                int i = (int)paramMotionEvent.getX();
                int j = (int)paramMotionEvent.getY();
                bool2 = false;
                boolean bool3 = false;
                bool1 = false;
                if (paramMotionEvent.getAction() != 0) {
                  break;
                }
                if ((this.buttonState != -1) && (i >= this.buttonX) && (i <= this.buttonX + AndroidUtilities.dp(48.0F)) && (j >= this.buttonY) && (j <= this.buttonY + AndroidUtilities.dp(48.0F)))
                {
                  this.buttonPressed = 1;
                  invalidate();
                  bool1 = true;
                }
                for (;;)
                {
                  bool2 = bool1;
                  if (!this.imagePressed) {
                    break;
                  }
                  if (!this.currentMessageObject.isSecretPhoto()) {
                    break label406;
                  }
                  this.imagePressed = false;
                  return bool1;
                  if (this.documentAttachType == 1)
                  {
                    bool1 = bool2;
                    if (i >= this.photoImage.getImageX())
                    {
                      bool1 = bool2;
                      if (i <= this.photoImage.getImageX() + this.backgroundWidth - AndroidUtilities.dp(50.0F))
                      {
                        bool1 = bool2;
                        if (j >= this.photoImage.getImageY())
                        {
                          bool1 = bool2;
                          if (j <= this.photoImage.getImageY() + this.photoImage.getImageHeight())
                          {
                            this.imagePressed = true;
                            bool1 = true;
                          }
                        }
                      }
                    }
                  }
                  else if (this.currentMessageObject.type == 13)
                  {
                    bool1 = bool2;
                    if (this.currentMessageObject.getInputStickerSet() == null) {}
                  }
                  else
                  {
                    bool2 = bool3;
                    if (i >= this.photoImage.getImageX())
                    {
                      bool2 = bool3;
                      if (i <= this.photoImage.getImageX() + this.backgroundWidth)
                      {
                        bool2 = bool3;
                        if (j >= this.photoImage.getImageY())
                        {
                          bool2 = bool3;
                          if (j <= this.photoImage.getImageY() + this.photoImage.getImageHeight())
                          {
                            this.imagePressed = true;
                            bool2 = true;
                          }
                        }
                      }
                    }
                    bool1 = bool2;
                    if (this.currentMessageObject.type == 12)
                    {
                      bool1 = bool2;
                      if (MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.media.user_id)) == null)
                      {
                        this.imagePressed = false;
                        bool1 = false;
                      }
                    }
                  }
                }
                if (this.currentMessageObject.isSendError())
                {
                  this.imagePressed = false;
                  return false;
                }
                bool2 = bool1;
              } while (this.currentMessageObject.type != 8);
              bool2 = bool1;
            } while (this.buttonState != -1);
            bool2 = bool1;
          } while (!MediaController.getInstance().canAutoplayGifs());
          bool2 = bool1;
        } while (this.photoImage.getAnimation() != null);
        this.imagePressed = false;
        return false;
        bool2 = bool1;
      } while (paramMotionEvent.getAction() != 1);
      if (this.buttonPressed == 1)
      {
        this.buttonPressed = 0;
        playSoundEffect(0);
        didPressedButton(false);
        this.radialProgress.swapBackground(getDrawableForCurrentState());
        invalidate();
        return false;
      }
      bool2 = bool1;
    } while (!this.imagePressed);
    this.imagePressed = false;
    if ((this.buttonState == -1) || (this.buttonState == 2) || (this.buttonState == 3))
    {
      playSoundEffect(0);
      didClickedImage();
    }
    for (;;)
    {
      invalidate();
      return false;
      if ((this.buttonState == 0) && (this.documentAttachType == 1))
      {
        playSoundEffect(0);
        didPressedButton(false);
      }
    }
  }
  
  private boolean checkTextBlockMotionEvent(MotionEvent paramMotionEvent)
  {
    if ((this.currentMessageObject.type != 0) || (this.currentMessageObject.textLayoutBlocks == null) || (this.currentMessageObject.textLayoutBlocks.isEmpty()) || (!(this.currentMessageObject.messageText instanceof Spannable))) {
      return false;
    }
    int k;
    int i;
    int m;
    int j;
    if ((paramMotionEvent.getAction() == 0) || ((paramMotionEvent.getAction() == 1) && (this.pressedLinkType == 1)))
    {
      k = (int)paramMotionEvent.getX();
      i = (int)paramMotionEvent.getY();
      if ((k < this.textX) || (i < this.textY) || (k > this.textX + this.currentMessageObject.textWidth) || (i > this.textY + this.currentMessageObject.textHeight)) {
        break label912;
      }
      m = i - this.textY;
      j = 0;
      i = 0;
      if ((i < this.currentMessageObject.textLayoutBlocks.size()) && (((MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(i)).textYOffset <= m)) {}
    }
    for (;;)
    {
      Object localObject2;
      try
      {
        Object localObject1 = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(j);
        i = k - (this.textX - (int)Math.ceil(((MessageObject.TextLayoutBlock)localObject1).textXOffset));
        k = (int)(m - ((MessageObject.TextLayoutBlock)localObject1).textYOffset);
        k = ((MessageObject.TextLayoutBlock)localObject1).textLayout.getLineForVertical(k);
        m = ((MessageObject.TextLayoutBlock)localObject1).textLayout.getOffsetForHorizontal(k, i) + ((MessageObject.TextLayoutBlock)localObject1).charactersOffset;
        float f = ((MessageObject.TextLayoutBlock)localObject1).textLayout.getLineLeft(k);
        if ((f <= i) && (((MessageObject.TextLayoutBlock)localObject1).textLayout.getLineWidth(k) + f >= i))
        {
          Spannable localSpannable = (Spannable)this.currentMessageObject.messageText;
          localObject2 = (ClickableSpan[])localSpannable.getSpans(m, m, ClickableSpan.class);
          k = 0;
          if (localObject2.length == 0) {
            break label920;
          }
          i = k;
          if (localObject2.length != 0)
          {
            i = k;
            if ((localObject2[0] instanceof URLSpanBotCommand))
            {
              i = k;
              if (!URLSpanBotCommand.enabled) {
                break label920;
              }
            }
          }
          if (i == 0)
          {
            if (paramMotionEvent.getAction() != 0) {
              break label876;
            }
            this.pressedLink = localObject2[0];
            this.linkBlockNum = j;
            this.pressedLinkType = 1;
            resetUrlPaths(false);
            try
            {
              paramMotionEvent = obtainNewUrlPath(false);
              k = localSpannable.getSpanStart(this.pressedLink) - ((MessageObject.TextLayoutBlock)localObject1).charactersOffset;
              m = localSpannable.getSpanEnd(this.pressedLink);
              i = ((MessageObject.TextLayoutBlock)localObject1).textLayout.getText().length();
              paramMotionEvent.setCurrentLayout(((MessageObject.TextLayoutBlock)localObject1).textLayout, k, 0.0F);
              ((MessageObject.TextLayoutBlock)localObject1).textLayout.getSelectionPath(k, m - ((MessageObject.TextLayoutBlock)localObject1).charactersOffset, paramMotionEvent);
              if (m < ((MessageObject.TextLayoutBlock)localObject1).charactersOffset + i) {
                break label925;
              }
              i = j + 1;
              if (i >= this.currentMessageObject.textLayoutBlocks.size()) {
                break label925;
              }
              paramMotionEvent = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(i);
              n = paramMotionEvent.textLayout.getText().length();
              localObject2 = (ClickableSpan[])localSpannable.getSpans(paramMotionEvent.charactersOffset, paramMotionEvent.charactersOffset, ClickableSpan.class);
              if ((localObject2 == null) || (localObject2.length == 0)) {
                break label925;
              }
              if (localObject2[0] == this.pressedLink) {
                continue;
              }
            }
            catch (Exception paramMotionEvent)
            {
              int n;
              FileLog.e("tmessages", paramMotionEvent);
              continue;
            }
            if (i >= 0)
            {
              paramMotionEvent = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(i);
              j = paramMotionEvent.textLayout.getText().length();
              localObject1 = (ClickableSpan[])localSpannable.getSpans(paramMotionEvent.charactersOffset + j - 1, paramMotionEvent.charactersOffset + j - 1, ClickableSpan.class);
              if ((localObject1 != null) && (localObject1.length != 0))
              {
                localObject1 = localObject1[0];
                localObject2 = this.pressedLink;
                if (localObject1 == localObject2) {
                  continue;
                }
              }
            }
            invalidate();
            return true;
            j = i;
            i += 1;
            break;
            localObject2 = obtainNewUrlPath(false);
            ((LinkPath)localObject2).setCurrentLayout(paramMotionEvent.textLayout, 0, paramMotionEvent.height);
            paramMotionEvent.textLayout.getSelectionPath(0, m - paramMotionEvent.charactersOffset, (Path)localObject2);
            if (m < ((MessageObject.TextLayoutBlock)localObject1).charactersOffset + n - 1) {
              break label925;
            }
            i += 1;
            continue;
            localObject1 = obtainNewUrlPath(false);
            j = localSpannable.getSpanStart(this.pressedLink) - paramMotionEvent.charactersOffset;
            ((LinkPath)localObject1).setCurrentLayout(paramMotionEvent.textLayout, j, -paramMotionEvent.height);
            paramMotionEvent.textLayout.getSelectionPath(j, localSpannable.getSpanEnd(this.pressedLink) - paramMotionEvent.charactersOffset, (Path)localObject1);
            if (j >= 0) {
              continue;
            }
            i -= 1;
            continue;
          }
        }
        return false;
      }
      catch (Exception paramMotionEvent)
      {
        FileLog.e("tmessages", paramMotionEvent);
      }
      for (;;)
      {
        label876:
        if (localObject2[0] == this.pressedLink)
        {
          this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, false);
          resetPressedLink(1);
          return true;
          label912:
          resetPressedLink(1);
        }
      }
      label920:
      i = 1;
      continue;
      label925:
      if (k < 0) {
        i = j - 1;
      }
    }
  }
  
  private int createDocumentLayout(int paramInt, MessageObject paramMessageObject)
  {
    if (paramMessageObject.type == 0) {}
    for (this.documentAttach = paramMessageObject.messageOwner.media.webpage.document; this.documentAttach == null; this.documentAttach = paramMessageObject.messageOwner.media.document) {
      return 0;
    }
    int j;
    int i;
    Object localObject1;
    if (MessageObject.isVoiceDocument(this.documentAttach))
    {
      this.documentAttachType = 3;
      int k = 0;
      j = 0;
      i = k;
      if (j < this.documentAttach.attributes.size())
      {
        localObject1 = (TLRPC.DocumentAttribute)this.documentAttach.attributes.get(j);
        if ((localObject1 instanceof TLRPC.TL_documentAttributeAudio)) {
          i = ((TLRPC.DocumentAttribute)localObject1).duration;
        }
      }
      else
      {
        this.availableTimeWidth = (paramInt - AndroidUtilities.dp(94.0F) - (int)Math.ceil(audioTimePaint.measureText("00:00")));
        measureTime(paramMessageObject);
        j = AndroidUtilities.dp(174.0F);
        k = this.timeWidth;
        if (!this.hasLinkPreview) {
          this.backgroundWidth = Math.min(paramInt, AndroidUtilities.dp(10.0F) * i + (j + k));
        }
        if (!paramMessageObject.isOutOwner()) {
          break label253;
        }
        this.seekBarWaveform.setColors(-4463700, -8863118, -5644906);
        this.seekBar.setColors(-4463700, -8863118, -5644906);
      }
      for (;;)
      {
        this.seekBarWaveform.setMessageObject(paramMessageObject);
        return 0;
        j += 1;
        break;
        label253:
        this.seekBarWaveform.setColors(-2169365, -9259544, -4399384);
        this.seekBar.setColors(-1774864, -9259544, -4399384);
      }
    }
    if (MessageObject.isMusicDocument(this.documentAttach))
    {
      this.documentAttachType = 5;
      if (paramMessageObject.isOutOwner())
      {
        this.seekBar.setColors(-4463700, -8863118, -5644906);
        paramInt -= AndroidUtilities.dp(86.0F);
        this.songLayout = new StaticLayout(TextUtils.ellipsize(paramMessageObject.getMusicTitle().replace('\n', ' '), audioTitlePaint, paramInt - AndroidUtilities.dp(12.0F), TextUtils.TruncateAt.END), audioTitlePaint, paramInt, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        if (this.songLayout.getLineCount() > 0) {
          this.songX = (-(int)Math.ceil(this.songLayout.getLineLeft(0)));
        }
        this.performerLayout = new StaticLayout(TextUtils.ellipsize(paramMessageObject.getMusicAuthor().replace('\n', ' '), audioPerformerPaint, paramInt, TextUtils.TruncateAt.END), audioPerformerPaint, paramInt, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        if (this.performerLayout.getLineCount() > 0) {
          this.performerX = (-(int)Math.ceil(this.performerLayout.getLineLeft(0)));
        }
        j = 0;
        paramInt = 0;
      }
      for (;;)
      {
        i = j;
        if (paramInt < this.documentAttach.attributes.size())
        {
          paramMessageObject = (TLRPC.DocumentAttribute)this.documentAttach.attributes.get(paramInt);
          if ((paramMessageObject instanceof TLRPC.TL_documentAttributeAudio)) {
            i = paramMessageObject.duration;
          }
        }
        else
        {
          paramInt = (int)Math.ceil(audioTimePaint.measureText(String.format("%d:%02d / %d:%02d", new Object[] { Integer.valueOf(i / 60), Integer.valueOf(i % 60), Integer.valueOf(i / 60), Integer.valueOf(i % 60) })));
          this.availableTimeWidth = (this.backgroundWidth - AndroidUtilities.dp(94.0F) - paramInt);
          return paramInt;
          this.seekBar.setColors(-1774864, -9259544, -4399384);
          break;
        }
        paramInt += 1;
      }
    }
    if (MessageObject.isVideoDocument(this.documentAttach))
    {
      this.documentAttachType = 4;
      j = 0;
      i = 0;
      for (;;)
      {
        paramInt = j;
        if (i < this.documentAttach.attributes.size())
        {
          paramMessageObject = (TLRPC.DocumentAttribute)this.documentAttach.attributes.get(i);
          if ((paramMessageObject instanceof TLRPC.TL_documentAttributeVideo)) {
            paramInt = paramMessageObject.duration;
          }
        }
        else
        {
          i = paramInt / 60;
          paramMessageObject = String.format("%d:%02d, %s", new Object[] { Integer.valueOf(i), Integer.valueOf(paramInt - i * 60), AndroidUtilities.formatFileSize(this.documentAttach.size) });
          this.infoWidth = ((int)Math.ceil(infoPaint.measureText(paramMessageObject)));
          this.infoLayout = new StaticLayout(paramMessageObject, infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
          return 0;
        }
        i += 1;
      }
    }
    boolean bool;
    Object localObject2;
    Layout.Alignment localAlignment;
    TextUtils.TruncateAt localTruncateAt;
    if (((this.documentAttach.mime_type != null) && (this.documentAttach.mime_type.toLowerCase().startsWith("image/"))) || (((this.documentAttach.thumb instanceof TLRPC.TL_photoSize)) && (!(this.documentAttach.thumb.location instanceof TLRPC.TL_fileLocationUnavailable))))
    {
      bool = true;
      this.drawPhotoImage = bool;
      i = paramInt;
      if (!this.drawPhotoImage) {
        i = paramInt + AndroidUtilities.dp(30.0F);
      }
      this.documentAttachType = 1;
      localObject2 = FileLoader.getDocumentFileName(this.documentAttach);
      if (localObject2 != null)
      {
        localObject1 = localObject2;
        if (((String)localObject2).length() != 0) {}
      }
      else
      {
        localObject1 = LocaleController.getString("AttachDocument", 2131165338);
      }
      localObject2 = docNamePaint;
      localAlignment = Layout.Alignment.ALIGN_NORMAL;
      localTruncateAt = TextUtils.TruncateAt.MIDDLE;
      if (!this.drawPhotoImage) {
        break label1073;
      }
    }
    label1073:
    for (paramInt = 2;; paramInt = 1)
    {
      this.docTitleLayout = StaticLayoutEx.createStaticLayout((CharSequence)localObject1, (TextPaint)localObject2, i, localAlignment, 1.0F, 0.0F, false, localTruncateAt, i, paramInt);
      this.docTitleOffsetX = Integer.MIN_VALUE;
      if ((this.docTitleLayout == null) || (this.docTitleLayout.getLineCount() <= 0)) {
        break label1302;
      }
      j = 0;
      paramInt = 0;
      while (paramInt < this.docTitleLayout.getLineCount())
      {
        j = Math.max(j, (int)Math.ceil(this.docTitleLayout.getLineWidth(paramInt)));
        this.docTitleOffsetX = Math.max(this.docTitleOffsetX, (int)Math.ceil(-this.docTitleLayout.getLineLeft(paramInt)));
        paramInt += 1;
      }
      bool = false;
      break;
    }
    paramInt = Math.min(i, j);
    for (;;)
    {
      localObject1 = AndroidUtilities.formatFileSize(this.documentAttach.size) + " " + FileLoader.getDocumentExtension(this.documentAttach);
      this.infoWidth = Math.min(i - AndroidUtilities.dp(30.0F), (int)Math.ceil(infoPaint.measureText((String)localObject1)));
      localObject1 = TextUtils.ellipsize((CharSequence)localObject1, infoPaint, this.infoWidth, TextUtils.TruncateAt.END);
      try
      {
        if (this.infoWidth < 0) {
          this.infoWidth = AndroidUtilities.dp(10.0F);
        }
        this.infoLayout = new StaticLayout((CharSequence)localObject1, infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      }
      catch (Exception localException)
      {
        for (;;)
        {
          label1302:
          FileLog.e("tmessages", localException);
          continue;
          this.photoImage.setImageBitmap((BitmapDrawable)null);
        }
      }
      if (this.drawPhotoImage)
      {
        this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(paramMessageObject.photoThumbs, AndroidUtilities.getPhotoSize());
        this.photoImage.setNeedsQualityThumb(true);
        this.photoImage.setShouldGenerateQualityThumb(true);
        this.photoImage.setParentMessageObject(paramMessageObject);
        if (this.currentPhotoObject == null) {
          break;
        }
        this.currentPhotoFilter = "86_86_b";
        this.photoImage.setImage(null, null, null, null, this.currentPhotoObject.location, this.currentPhotoFilter, 0, null, true);
      }
      return paramInt;
      paramInt = i;
      this.docTitleOffsetX = 0;
    }
  }
  
  private void didClickedImage()
  {
    if ((this.currentMessageObject.type == 1) || (this.currentMessageObject.type == 13)) {
      if (this.buttonState == -1) {
        this.delegate.didPressedImage(this);
      }
    }
    label41:
    Object localObject;
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              break label41;
              break label41;
              break label41;
              do
              {
                return;
              } while (this.buttonState != 0);
              didPressedButton(false);
              return;
              if (this.currentMessageObject.type == 12)
              {
                localObject = MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.media.user_id));
                this.delegate.didPressedUserAvatar(this, (TLRPC.User)localObject);
                return;
              }
              if (this.currentMessageObject.type != 8) {
                break;
              }
              if (this.buttonState == -1)
              {
                if (MediaController.getInstance().canAutoplayGifs())
                {
                  this.delegate.didPressedImage(this);
                  return;
                }
                this.buttonState = 2;
                this.currentMessageObject.audioProgress = 1.0F;
                this.photoImage.setAllowStartAnimation(false);
                this.photoImage.stopAnimation();
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                invalidate();
                return;
              }
            } while ((this.buttonState != 2) && (this.buttonState != 0));
            didPressedButton(false);
            return;
            if (this.documentAttachType != 4) {
              break;
            }
          } while ((this.buttonState != 0) && (this.buttonState != 3));
          didPressedButton(false);
          return;
          if (this.currentMessageObject.type == 4)
          {
            this.delegate.didPressedImage(this);
            return;
          }
          if (this.documentAttachType != 1) {
            break;
          }
        } while (this.buttonState != -1);
        this.delegate.didPressedImage(this);
        return;
      } while ((this.documentAttachType != 2) || (this.buttonState != -1));
      localObject = this.currentMessageObject.messageOwner.media.webpage;
    } while (localObject == null);
    if ((Build.VERSION.SDK_INT >= 16) && (((TLRPC.WebPage)localObject).embed_url != null) && (((TLRPC.WebPage)localObject).embed_url.length() != 0))
    {
      this.delegate.needOpenWebView(((TLRPC.WebPage)localObject).embed_url, ((TLRPC.WebPage)localObject).site_name, ((TLRPC.WebPage)localObject).description, ((TLRPC.WebPage)localObject).url, ((TLRPC.WebPage)localObject).embed_width, ((TLRPC.WebPage)localObject).embed_height);
      return;
    }
    Browser.openUrl(getContext(), ((TLRPC.WebPage)localObject).url);
  }
  
  private void didPressedButton(boolean paramBoolean)
  {
    if (this.buttonState == 0) {
      if ((this.documentAttachType == 3) || (this.documentAttachType == 5)) {
        if (this.delegate.needPlayAudio(this.currentMessageObject))
        {
          this.buttonState = 1;
          this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
          invalidate();
        }
      }
    }
    label451:
    label732:
    do
    {
      do
      {
        do
        {
          return;
          this.cancelLoading = false;
          this.radialProgress.setProgress(0.0F, false);
          if (this.currentMessageObject.type == 1)
          {
            localImageReceiver = this.photoImage;
            localObject = this.currentPhotoObject.location;
            str = this.currentPhotoFilter;
            if (this.currentPhotoObjectThumb != null)
            {
              localFileLocation = this.currentPhotoObjectThumb.location;
              localImageReceiver.setImage((TLObject)localObject, str, localFileLocation, this.currentPhotoFilter, this.currentPhotoObject.size, null, false);
            }
          }
          for (;;)
          {
            this.buttonState = 1;
            this.radialProgress.setBackground(getDrawableForCurrentState(), true, paramBoolean);
            invalidate();
            return;
            localFileLocation = null;
            break;
            if (this.currentMessageObject.type == 8)
            {
              this.currentMessageObject.audioProgress = 2.0F;
              localImageReceiver = this.photoImage;
              localObject = this.currentMessageObject.messageOwner.media.document;
              if (this.currentPhotoObject != null) {}
              for (localFileLocation = this.currentPhotoObject.location;; localFileLocation = null)
              {
                localImageReceiver.setImage((TLObject)localObject, null, localFileLocation, this.currentPhotoFilter, this.currentMessageObject.messageOwner.media.document.size, null, false);
                break;
              }
            }
            if (this.currentMessageObject.type == 9)
            {
              FileLoader.getInstance().loadFile(this.currentMessageObject.messageOwner.media.document, false, false);
            }
            else if (this.documentAttachType == 4)
            {
              FileLoader.getInstance().loadFile(this.documentAttach, true, false);
            }
            else
            {
              if ((this.currentMessageObject.type != 0) || (this.documentAttachType == 0)) {
                break label451;
              }
              if (this.documentAttachType == 2)
              {
                this.photoImage.setImage(this.currentMessageObject.messageOwner.media.webpage.document, null, this.currentPhotoObject.location, this.currentPhotoFilter, this.currentMessageObject.messageOwner.media.webpage.document.size, null, false);
                this.currentMessageObject.audioProgress = 2.0F;
              }
              else if (this.documentAttachType == 1)
              {
                FileLoader.getInstance().loadFile(this.currentMessageObject.messageOwner.media.webpage.document, false, false);
              }
            }
          }
          ImageReceiver localImageReceiver = this.photoImage;
          Object localObject = this.currentPhotoObject.location;
          String str = this.currentPhotoFilter;
          if (this.currentPhotoObjectThumb != null) {}
          for (TLRPC.FileLocation localFileLocation = this.currentPhotoObjectThumb.location;; localFileLocation = null)
          {
            localImageReceiver.setImage((TLObject)localObject, str, localFileLocation, this.currentPhotoFilterThumb, 0, null, false);
            break;
          }
          if (this.buttonState != 1) {
            break label732;
          }
          if ((this.documentAttachType != 3) && (this.documentAttachType != 5)) {
            break;
          }
        } while (!MediaController.getInstance().pauseAudio(this.currentMessageObject));
        this.buttonState = 0;
        this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
        invalidate();
        return;
        if ((this.currentMessageObject.isOut()) && (this.currentMessageObject.isSending()))
        {
          this.delegate.didPressedCancelSendButton(this);
          return;
        }
        this.cancelLoading = true;
        if ((this.documentAttachType == 4) || (this.documentAttachType == 1)) {
          FileLoader.getInstance().cancelLoadFile(this.documentAttach);
        }
        for (;;)
        {
          this.buttonState = 0;
          this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
          invalidate();
          return;
          if ((this.currentMessageObject.type == 0) || (this.currentMessageObject.type == 1) || (this.currentMessageObject.type == 8)) {
            this.photoImage.cancelLoadImage();
          } else if (this.currentMessageObject.type == 9) {
            FileLoader.getInstance().cancelLoadFile(this.currentMessageObject.messageOwner.media.document);
          }
        }
        if (this.buttonState == 2)
        {
          if ((this.documentAttachType == 3) || (this.documentAttachType == 5))
          {
            this.radialProgress.setProgress(0.0F, false);
            FileLoader.getInstance().loadFile(this.documentAttach, true, false);
            this.buttonState = 4;
            this.radialProgress.setBackground(getDrawableForCurrentState(), true, false);
            invalidate();
            return;
          }
          this.photoImage.setAllowStartAnimation(true);
          this.photoImage.startAnimation();
          this.currentMessageObject.audioProgress = 0.0F;
          this.buttonState = -1;
          this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
          return;
        }
        if (this.buttonState == 3)
        {
          this.delegate.didPressedImage(this);
          return;
        }
      } while ((this.buttonState != 4) || ((this.documentAttachType != 3) && (this.documentAttachType != 5)));
      if (((!this.currentMessageObject.isOut()) || (!this.currentMessageObject.isSending())) && (!this.currentMessageObject.isSendError())) {
        break;
      }
    } while (this.delegate == null);
    this.delegate.didPressedCancelSendButton(this);
    return;
    FileLoader.getInstance().cancelLoadFile(this.documentAttach);
    this.buttonState = 2;
    this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
    invalidate();
  }
  
  private void drawContent(Canvas paramCanvas)
  {
    if ((this.needNewVisiblePart) && (this.currentMessageObject.type == 0))
    {
      getLocalVisibleRect(this.scrollRect);
      setVisiblePart(this.scrollRect.top, this.scrollRect.bottom - this.scrollRect.top);
      this.needNewVisiblePart = false;
    }
    this.photoImage.setPressed(isDrawSelectedBackground());
    Object localObject1 = this.photoImage;
    boolean bool2;
    label161:
    label230:
    int n;
    label337:
    int k;
    int i2;
    if (!PhotoViewer.getInstance().isShowingImage(this.currentMessageObject))
    {
      bool1 = true;
      ((ImageReceiver)localObject1).setVisible(bool1, false);
      this.radialProgress.setHideCurrentDrawable(false);
      this.radialProgress.setProgressColor(-1);
      bool1 = false;
      bool2 = false;
      if (this.currentMessageObject.type != 0) {
        break label2601;
      }
      if (!this.currentMessageObject.isOutOwner()) {
        break label974;
      }
      this.textX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0F));
      if (!this.hasGamePreview) {
        break label998;
      }
      this.textX += AndroidUtilities.dp(11.0F);
      this.textY = (AndroidUtilities.dp(14.0F) + this.namesOffset);
      if (this.siteNameLayout != null) {
        this.textY += this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
      }
      if ((this.currentMessageObject.textLayoutBlocks != null) && (!this.currentMessageObject.textLayoutBlocks.isEmpty()) && (this.firstVisibleBlockNum >= 0))
      {
        i = this.firstVisibleBlockNum;
        if ((i <= this.lastVisibleBlockNum) && (i < this.currentMessageObject.textLayoutBlocks.size())) {
          break label1016;
        }
      }
      if (!this.hasLinkPreview)
      {
        bool1 = bool2;
        if (!this.hasGamePreview) {
          break label1523;
        }
      }
      if (!this.hasGamePreview) {
        break label1220;
      }
      i = AndroidUtilities.dp(14.0F) + this.namesOffset;
      n = this.textX - AndroidUtilities.dp(10.0F);
      k = i;
      i2 = 0;
      localObject1 = replyLinePaint;
      if (!this.currentMessageObject.isOutOwner()) {
        break label1254;
      }
      j = -7812741;
      label363:
      ((Paint)localObject1).setColor(j);
      paramCanvas.drawRect(n, k - AndroidUtilities.dp(3.0F), AndroidUtilities.dp(2.0F) + n, this.linkPreviewHeight + k + AndroidUtilities.dp(3.0F), replyLinePaint);
      j = k;
      if (this.siteNameLayout != null)
      {
        localObject1 = replyNamePaint;
        if (!this.currentMessageObject.isOutOwner()) {
          break label1262;
        }
      }
    }
    int i1;
    label974:
    label998:
    label1016:
    label1220:
    label1254:
    label1262:
    for (int j = -11162801;; j = -12940081)
    {
      ((TextPaint)localObject1).setColor(j);
      paramCanvas.save();
      paramCanvas.translate(AndroidUtilities.dp(10.0F) + n, k - AndroidUtilities.dp(3.0F));
      this.siteNameLayout.draw(paramCanvas);
      paramCanvas.restore();
      j = k + this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
      k = j;
      i1 = i;
      if (this.hasGamePreview)
      {
        k = j;
        i1 = i;
        if (this.currentMessageObject.textHeight != 0)
        {
          i1 = i + (this.currentMessageObject.textHeight + AndroidUtilities.dp(4.0F));
          k = j + (this.currentMessageObject.textHeight + AndroidUtilities.dp(4.0F));
        }
      }
      replyNamePaint.setColor(-16777216);
      replyTextPaint.setColor(-16777216);
      m = k;
      j = i2;
      if (this.titleLayout != null)
      {
        i = k;
        if (k != i1) {
          i = k + AndroidUtilities.dp(2.0F);
        }
        j = i - AndroidUtilities.dp(1.0F);
        paramCanvas.save();
        paramCanvas.translate(AndroidUtilities.dp(10.0F) + n + this.titleX, i - AndroidUtilities.dp(3.0F));
        this.titleLayout.draw(paramCanvas);
        paramCanvas.restore();
        m = i + this.titleLayout.getLineBottom(this.titleLayout.getLineCount() - 1);
      }
      k = m;
      i = j;
      if (this.authorLayout != null)
      {
        k = m;
        if (m != i1) {
          k = m + AndroidUtilities.dp(2.0F);
        }
        i = j;
        if (j == 0) {
          i = k - AndroidUtilities.dp(1.0F);
        }
        paramCanvas.save();
        paramCanvas.translate(AndroidUtilities.dp(10.0F) + n + this.authorX, k - AndroidUtilities.dp(3.0F));
        this.authorLayout.draw(paramCanvas);
        paramCanvas.restore();
        k += this.authorLayout.getLineBottom(this.authorLayout.getLineCount() - 1);
      }
      j = k;
      m = i;
      if (this.descriptionLayout == null) {
        break label1309;
      }
      m = k;
      if (k != i1) {
        m = k + AndroidUtilities.dp(2.0F);
      }
      j = i;
      if (i == 0) {
        j = m - AndroidUtilities.dp(1.0F);
      }
      this.descriptionY = (m - AndroidUtilities.dp(3.0F));
      paramCanvas.save();
      paramCanvas.translate(AndroidUtilities.dp(10.0F) + n + this.descriptionX, this.descriptionY);
      if ((this.pressedLink == null) || (this.linkBlockNum != -10)) {
        break label1270;
      }
      i = 0;
      while (i < this.urlPath.size())
      {
        paramCanvas.drawPath((Path)this.urlPath.get(i), urlPaint);
        i += 1;
      }
      bool1 = false;
      break;
      this.textX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(17.0F));
      break label161;
      this.textY = (AndroidUtilities.dp(10.0F) + this.namesOffset);
      break label230;
      localObject1 = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(i);
      paramCanvas.save();
      paramCanvas.translate(this.textX - (int)Math.ceil(((MessageObject.TextLayoutBlock)localObject1).textXOffset), this.textY + ((MessageObject.TextLayoutBlock)localObject1).textYOffset);
      if ((this.pressedLink != null) && (i == this.linkBlockNum))
      {
        j = 0;
        while (j < this.urlPath.size())
        {
          paramCanvas.drawPath((Path)this.urlPath.get(j), urlPaint);
          j += 1;
        }
      }
      if ((i == this.linkSelectionBlockNum) && (!this.urlPathSelection.isEmpty()))
      {
        j = 0;
        while (j < this.urlPathSelection.size())
        {
          paramCanvas.drawPath((Path)this.urlPathSelection.get(j), urlSelectionPaint);
          j += 1;
        }
      }
      try
      {
        ((MessageObject.TextLayoutBlock)localObject1).textLayout.draw(paramCanvas);
        paramCanvas.restore();
        i += 1;
      }
      catch (Exception localException1)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException1);
        }
      }
      i = this.textY + this.currentMessageObject.textHeight + AndroidUtilities.dp(8.0F);
      n = this.textX + AndroidUtilities.dp(1.0F);
      break label337;
      j = -9390872;
      break label363;
    }
    label1270:
    this.descriptionLayout.draw(paramCanvas);
    paramCanvas.restore();
    int i = m + this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1);
    int m = j;
    j = i;
    label1309:
    boolean bool1 = bool2;
    if (this.drawPhotoImage)
    {
      i = j;
      if (j != i1) {
        i = j + AndroidUtilities.dp(2.0F);
      }
      if (!this.isSmallImage) {
        break label2357;
      }
      this.photoImage.setImageCoords(this.backgroundWidth + n - AndroidUtilities.dp(81.0F), m, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
      bool2 = this.photoImage.draw(paramCanvas);
      bool1 = bool2;
      if (this.videoInfoLayout != null)
      {
        if (!this.hasGamePreview) {
          break label2494;
        }
        i = this.photoImage.getImageX() + AndroidUtilities.dp(8.5F);
        j = this.photoImage.getImageY() + AndroidUtilities.dp(6.0F);
        Theme.timeBackgroundDrawable.setBounds(i - AndroidUtilities.dp(4.0F), j - AndroidUtilities.dp(1.5F), this.durationWidth + i + AndroidUtilities.dp(4.0F), AndroidUtilities.dp(16.5F) + j);
        Theme.timeBackgroundDrawable.draw(paramCanvas);
        label1493:
        paramCanvas.save();
        paramCanvas.translate(i, j);
        this.videoInfoLayout.draw(paramCanvas);
        paramCanvas.restore();
        bool1 = bool2;
      }
    }
    label1523:
    this.drawTime = true;
    label1528:
    label1574:
    long l1;
    long l2;
    float f;
    if ((this.buttonState == -1) && (this.currentMessageObject.isSecretPhoto()))
    {
      i = 4;
      if (this.currentMessageObject.messageOwner.destroyTime != 0)
      {
        if (!this.currentMessageObject.isOutOwner()) {
          break label2632;
        }
        i = 6;
      }
      setDrawableBounds(Theme.photoStatesDrawables[i][this.buttonPressed], this.buttonX, this.buttonY);
      Theme.photoStatesDrawables[i][this.buttonPressed].setAlpha((int)(255.0F * (1.0F - this.radialProgress.getAlpha())));
      Theme.photoStatesDrawables[i][this.buttonPressed].draw(paramCanvas);
      if ((!this.currentMessageObject.isOutOwner()) && (this.currentMessageObject.messageOwner.destroyTime != 0))
      {
        l1 = System.currentTimeMillis();
        l2 = ConnectionsManager.getInstance().getTimeDifference() * 1000;
        f = (float)Math.max(0L, this.currentMessageObject.messageOwner.destroyTime * 1000L - (l1 + l2)) / (this.currentMessageObject.messageOwner.ttl * 1000.0F);
        paramCanvas.drawArc(this.deleteProgressRect, -90.0F, -360.0F * f, true, deleteProgressPaint);
        if (f != 0.0F)
        {
          i = AndroidUtilities.dp(2.0F);
          invalidate((int)this.deleteProgressRect.left - i, (int)this.deleteProgressRect.top - i, (int)this.deleteProgressRect.right + i * 2, (int)this.deleteProgressRect.bottom + i * 2);
        }
        updateSecretTimeText(this.currentMessageObject);
      }
    }
    Object localObject2;
    if ((this.documentAttachType == 2) || (this.currentMessageObject.type == 8))
    {
      if ((this.photoImage.getVisible()) && (!this.hasGamePreview))
      {
        localObject2 = Theme.docMenuDrawable[3];
        i = this.photoImage.getImageX() + this.photoImage.getImageWidth() - AndroidUtilities.dp(14.0F);
        this.otherX = i;
        j = this.photoImage.getImageY() + AndroidUtilities.dp(8.1F);
        this.otherY = j;
        setDrawableBounds((Drawable)localObject2, i, j);
        Theme.docMenuDrawable[3].draw(paramCanvas);
      }
      label1920:
      if ((this.currentMessageObject.type != 1) && (this.documentAttachType != 4)) {
        break label3483;
      }
      if (this.photoImage.getVisible())
      {
        if (this.documentAttachType == 4)
        {
          localObject2 = Theme.docMenuDrawable[3];
          i = this.photoImage.getImageX() + this.photoImage.getImageWidth() - AndroidUtilities.dp(14.0F);
          this.otherX = i;
          j = this.photoImage.getImageY() + AndroidUtilities.dp(8.1F);
          this.otherY = j;
          setDrawableBounds((Drawable)localObject2, i, j);
          Theme.docMenuDrawable[3].draw(paramCanvas);
        }
        if ((this.infoLayout != null) && ((this.buttonState == 1) || (this.buttonState == 0) || (this.buttonState == 3) || (this.currentMessageObject.isSecretPhoto())))
        {
          infoPaint.setColor(-1);
          localObject2 = Theme.timeBackgroundDrawable;
          i = this.photoImage.getImageX();
          j = AndroidUtilities.dp(4.0F);
          k = this.photoImage.getImageY();
          m = AndroidUtilities.dp(4.0F);
          n = this.infoWidth;
          setDrawableBounds((Drawable)localObject2, j + i, m + k, AndroidUtilities.dp(8.0F) + n, AndroidUtilities.dp(16.5F));
          Theme.timeBackgroundDrawable.draw(paramCanvas);
          paramCanvas.save();
          paramCanvas.translate(this.photoImage.getImageX() + AndroidUtilities.dp(8.0F), this.photoImage.getImageY() + AndroidUtilities.dp(5.5F));
          this.infoLayout.draw(paramCanvas);
          paramCanvas.restore();
        }
      }
    }
    for (;;)
    {
      label2211:
      if (this.captionLayout != null)
      {
        paramCanvas.save();
        if ((this.currentMessageObject.type == 1) || (this.documentAttachType == 4) || (this.currentMessageObject.type == 8))
        {
          i = this.photoImage.getImageX() + AndroidUtilities.dp(5.0F);
          this.captionX = i;
          f = i;
          i = this.photoImage.getImageY() + this.photoImage.getImageHeight() + AndroidUtilities.dp(6.0F);
          this.captionY = i;
          paramCanvas.translate(f, i);
          if (this.pressedLink != null) {
            i = 0;
          }
        }
        else
        {
          for (;;)
          {
            if (i < this.urlPath.size())
            {
              paramCanvas.drawPath((Path)this.urlPath.get(i), urlPaint);
              i += 1;
              continue;
              label2357:
              this.photoImage.setImageCoords(AndroidUtilities.dp(10.0F) + n, i, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
              if (!this.drawImageButton) {
                break;
              }
              i = AndroidUtilities.dp(48.0F);
              this.buttonX = ((int)(this.photoImage.getImageX() + (this.photoImage.getImageWidth() - i) / 2.0F));
              this.buttonY = ((int)(this.photoImage.getImageY() + (this.photoImage.getImageHeight() - i) / 2.0F));
              this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(48.0F), this.buttonY + AndroidUtilities.dp(48.0F));
              break;
              label2494:
              i = this.photoImage.getImageX() + this.photoImage.getImageWidth() - AndroidUtilities.dp(8.0F) - this.durationWidth;
              j = this.photoImage.getImageY() + this.photoImage.getImageHeight() - AndroidUtilities.dp(19.0F);
              Theme.timeBackgroundDrawable.setBounds(i - AndroidUtilities.dp(4.0F), j - AndroidUtilities.dp(1.5F), this.durationWidth + i + AndroidUtilities.dp(4.0F), AndroidUtilities.dp(14.5F) + j);
              Theme.timeBackgroundDrawable.draw(paramCanvas);
              break label1493;
              label2601:
              if (!this.drawPhotoImage) {
                break label1528;
              }
              bool1 = this.photoImage.draw(paramCanvas);
              this.drawTime = this.photoImage.getVisible();
              break label1528;
              label2632:
              i = 5;
              break label1574;
              if (this.documentAttachType == 5)
              {
                if (this.currentMessageObject.isOutOwner())
                {
                  audioTitlePaint.setColor(-11162801);
                  audioPerformerPaint.setColor(-13286860);
                  audioTimePaint.setColor(-10112933);
                  localObject2 = this.radialProgress;
                  if ((isDrawSelectedBackground()) || (this.buttonPressed != 0))
                  {
                    i = -2820676;
                    label2706:
                    ((RadialProgress)localObject2).setProgressColor(i);
                    this.radialProgress.draw(paramCanvas);
                    paramCanvas.save();
                    paramCanvas.translate(this.timeAudioX + this.songX, AndroidUtilities.dp(13.0F) + this.namesOffset + this.mediaOffsetY);
                    this.songLayout.draw(paramCanvas);
                    paramCanvas.restore();
                    paramCanvas.save();
                    if (!MediaController.getInstance().isPlayingAudio(this.currentMessageObject)) {
                      break label3021;
                    }
                    paramCanvas.translate(this.seekBarX, this.seekBarY);
                    this.seekBar.draw(paramCanvas);
                    label2808:
                    paramCanvas.restore();
                    paramCanvas.save();
                    paramCanvas.translate(this.timeAudioX, AndroidUtilities.dp(57.0F) + this.namesOffset + this.mediaOffsetY);
                    this.durationLayout.draw(paramCanvas);
                    paramCanvas.restore();
                    if (!this.currentMessageObject.isOutOwner()) {
                      break label3063;
                    }
                    localObject2 = Theme.docMenuDrawable[1];
                    i = this.buttonX;
                    j = this.backgroundWidth;
                    if (this.currentMessageObject.type != 0) {
                      break label3091;
                    }
                  }
                }
                label3021:
                label3063:
                label3091:
                for (f = 58.0F;; f = 48.0F)
                {
                  i = j + i - AndroidUtilities.dp(f);
                  this.otherX = i;
                  j = this.buttonY - AndroidUtilities.dp(5.0F);
                  this.otherY = j;
                  setDrawableBounds((Drawable)localObject2, i, j);
                  ((Drawable)localObject2).draw(paramCanvas);
                  break;
                  i = -1048610;
                  break label2706;
                  audioTitlePaint.setColor(-11625772);
                  audioPerformerPaint.setColor(-13683656);
                  audioTimePaint.setColor(-6182221);
                  localObject2 = this.radialProgress;
                  if ((isDrawSelectedBackground()) || (this.buttonPressed != 0)) {}
                  for (i = -1902337;; i = -1)
                  {
                    ((RadialProgress)localObject2).setProgressColor(i);
                    break;
                  }
                  paramCanvas.translate(this.timeAudioX + this.performerX, AndroidUtilities.dp(35.0F) + this.namesOffset + this.mediaOffsetY);
                  this.performerLayout.draw(paramCanvas);
                  break label2808;
                  localObject2 = Theme.docMenuDrawable;
                  if (isDrawSelectedBackground()) {}
                  for (i = 2;; i = 0)
                  {
                    localObject2 = localObject2[i];
                    break;
                  }
                }
              }
              if (this.documentAttachType != 3) {
                break label1920;
              }
              if (this.currentMessageObject.isOutOwner())
              {
                localObject2 = audioTimePaint;
                if (isDrawSelectedBackground())
                {
                  label3128:
                  ((TextPaint)localObject2).setColor(-10112933);
                  localObject2 = this.radialProgress;
                  if ((!isDrawSelectedBackground()) && (this.buttonPressed == 0)) {
                    break label3377;
                  }
                  i = -2820676;
                  label3160:
                  ((RadialProgress)localObject2).setProgressColor(i);
                  this.radialProgress.draw(paramCanvas);
                  paramCanvas.save();
                  if (!this.useSeekBarWaweform) {
                    break label3451;
                  }
                  paramCanvas.translate(this.seekBarX + AndroidUtilities.dp(13.0F), this.seekBarY);
                  this.seekBarWaveform.draw(paramCanvas);
                  label3215:
                  paramCanvas.restore();
                  paramCanvas.save();
                  paramCanvas.translate(this.timeAudioX, AndroidUtilities.dp(44.0F) + this.namesOffset + this.mediaOffsetY);
                  this.durationLayout.draw(paramCanvas);
                  paramCanvas.restore();
                  if ((this.currentMessageObject.type == 0) || (this.currentMessageObject.messageOwner.to_id.channel_id != 0) || (!this.currentMessageObject.isContentUnread())) {
                    break label1920;
                  }
                  localObject2 = docBackPaint;
                  if (!this.currentMessageObject.isOutOwner()) {
                    break label3476;
                  }
                }
              }
              label3377:
              label3400:
              label3446:
              label3451:
              label3476:
              for (i = -8863118;; i = -9259544)
              {
                ((Paint)localObject2).setColor(i);
                paramCanvas.drawCircle(this.timeAudioX + this.timeWidthAudio + AndroidUtilities.dp(6.0F), AndroidUtilities.dp(51.0F) + this.namesOffset + this.mediaOffsetY, AndroidUtilities.dp(3.0F), docBackPaint);
                break;
                break label3128;
                i = -1048610;
                break label3160;
                localObject2 = audioTimePaint;
                if (isDrawSelectedBackground())
                {
                  i = -7752511;
                  ((TextPaint)localObject2).setColor(i);
                  localObject2 = this.radialProgress;
                  if ((!isDrawSelectedBackground()) && (this.buttonPressed == 0)) {
                    break label3446;
                  }
                }
                for (i = -1902337;; i = -1)
                {
                  ((RadialProgress)localObject2).setProgressColor(i);
                  break;
                  i = -6182221;
                  break label3400;
                }
                paramCanvas.translate(this.seekBarX, this.seekBarY);
                this.seekBar.draw(paramCanvas);
                break label3215;
              }
              label3483:
              if (this.currentMessageObject.type == 4)
              {
                if (this.docTitleLayout == null) {
                  break label2211;
                }
                if (this.currentMessageObject.isOutOwner())
                {
                  locationTitlePaint.setColor(-11162801);
                  localObject2 = locationAddressPaint;
                  if (isDrawSelectedBackground()) {}
                  for (;;)
                  {
                    ((TextPaint)localObject2).setColor(-10112933);
                    paramCanvas.save();
                    paramCanvas.translate(this.docTitleOffsetX + this.photoImage.getImageX() + this.photoImage.getImageWidth() + AndroidUtilities.dp(10.0F), this.photoImage.getImageY() + AndroidUtilities.dp(8.0F));
                    this.docTitleLayout.draw(paramCanvas);
                    paramCanvas.restore();
                    if (this.infoLayout == null) {
                      break;
                    }
                    paramCanvas.save();
                    paramCanvas.translate(this.photoImage.getImageX() + this.photoImage.getImageWidth() + AndroidUtilities.dp(10.0F), this.photoImage.getImageY() + this.docTitleLayout.getLineBottom(this.docTitleLayout.getLineCount() - 1) + AndroidUtilities.dp(13.0F));
                    this.infoLayout.draw(paramCanvas);
                    paramCanvas.restore();
                    break;
                  }
                }
                locationTitlePaint.setColor(-11625772);
                localObject2 = locationAddressPaint;
                if (isDrawSelectedBackground()) {}
                for (i = -7752511;; i = -6182221)
                {
                  ((TextPaint)localObject2).setColor(i);
                  break;
                }
              }
              if (this.currentMessageObject.type != 12) {
                break label2211;
              }
              localObject2 = contactNamePaint;
              if (this.currentMessageObject.isOutOwner())
              {
                i = -11162801;
                label3765:
                ((TextPaint)localObject2).setColor(i);
                localObject2 = contactPhonePaint;
                if (!this.currentMessageObject.isOutOwner()) {
                  break label4011;
                }
              }
              label4011:
              for (i = -13286860;; i = -13683656)
              {
                ((TextPaint)localObject2).setColor(i);
                if (this.titleLayout != null)
                {
                  paramCanvas.save();
                  paramCanvas.translate(this.photoImage.getImageX() + this.photoImage.getImageWidth() + AndroidUtilities.dp(9.0F), AndroidUtilities.dp(16.0F) + this.namesOffset);
                  this.titleLayout.draw(paramCanvas);
                  paramCanvas.restore();
                }
                if (this.docTitleLayout != null)
                {
                  paramCanvas.save();
                  paramCanvas.translate(this.photoImage.getImageX() + this.photoImage.getImageWidth() + AndroidUtilities.dp(9.0F), AndroidUtilities.dp(39.0F) + this.namesOffset);
                  this.docTitleLayout.draw(paramCanvas);
                  paramCanvas.restore();
                }
                if (!this.currentMessageObject.isOutOwner()) {
                  break label4018;
                }
                localObject2 = Theme.docMenuDrawable[1];
                i = this.photoImage.getImageX() + this.backgroundWidth - AndroidUtilities.dp(48.0F);
                this.otherX = i;
                j = this.photoImage.getImageY() - AndroidUtilities.dp(5.0F);
                this.otherY = j;
                setDrawableBounds((Drawable)localObject2, i, j);
                ((Drawable)localObject2).draw(paramCanvas);
                break;
                i = -11625772;
                break label3765;
              }
              label4018:
              localObject2 = Theme.docMenuDrawable;
              if (isDrawSelectedBackground()) {}
              for (i = 2;; i = 0)
              {
                localObject2 = localObject2[i];
                break;
              }
              i = this.backgroundDrawableLeft;
              if (this.currentMessageObject.isOutOwner()) {}
              for (f = 11.0F;; f = 17.0F)
              {
                i = AndroidUtilities.dp(f) + i;
                this.captionX = i;
                f = i;
                i = this.totalHeight - this.captionHeight - AndroidUtilities.dp(10.0F);
                this.captionY = i;
                paramCanvas.translate(f, i);
                break;
              }
            }
          }
        }
      }
    }
    try
    {
      this.captionLayout.draw(paramCanvas);
      paramCanvas.restore();
      if (this.documentAttachType == 1)
      {
        if (!this.currentMessageObject.isOutOwner()) {
          break label4988;
        }
        docNamePaint.setColor(-11162801);
        localObject2 = infoPaint;
        if (!isDrawSelectedBackground()) {
          break label4978;
        }
        ((TextPaint)localObject2).setColor(-10112933);
        localObject2 = docBackPaint;
        if (!isDrawSelectedBackground()) {
          break label4981;
        }
        i = -3806041;
        ((Paint)localObject2).setColor(i);
        localObject2 = Theme.docMenuDrawable[1];
        if (!this.drawPhotoImage) {
          break label5278;
        }
        if (this.currentMessageObject.type != 0) {
          break label5083;
        }
        i = this.photoImage.getImageX() + this.backgroundWidth - AndroidUtilities.dp(56.0F);
        this.otherX = i;
        j = this.photoImage.getImageY() + AndroidUtilities.dp(1.0F);
        this.otherY = j;
        setDrawableBounds((Drawable)localObject2, i, j);
        k = this.photoImage.getImageX() + this.photoImage.getImageWidth() + AndroidUtilities.dp(10.0F);
        j = this.photoImage.getImageY() + AndroidUtilities.dp(8.0F);
        m = this.photoImage.getImageY() + this.docTitleLayout.getLineBottom(this.docTitleLayout.getLineCount() - 1) + AndroidUtilities.dp(13.0F);
        if ((this.buttonState >= 0) && (this.buttonState < 4))
        {
          if (bool1) {
            break label5181;
          }
          i = this.buttonState;
          if (this.buttonState != 0) {
            break label5146;
          }
          if (!this.currentMessageObject.isOutOwner()) {
            break label5140;
          }
          i = 7;
          localObject4 = this.radialProgress;
          localObject5 = Theme.photoStatesDrawables[i];
          if ((!isDrawSelectedBackground()) && (this.buttonPressed == 0)) {
            break label5176;
          }
          i = 1;
          ((RadialProgress)localObject4).swapBackground(localObject5[i]);
        }
        if (bool1) {
          break label5248;
        }
        this.rect.set(this.photoImage.getImageX(), this.photoImage.getImageY(), this.photoImage.getImageX() + this.photoImage.getImageWidth(), this.photoImage.getImageY() + this.photoImage.getImageHeight());
        paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(3.0F), AndroidUtilities.dp(3.0F), docBackPaint);
        if (!this.currentMessageObject.isOutOwner()) {
          break label5212;
        }
        localObject4 = this.radialProgress;
        if (!isDrawSelectedBackground()) {
          break label5205;
        }
        i = -3806041;
        ((RadialProgress)localObject4).setProgressColor(i);
        i = m;
        ((Drawable)localObject2).draw(paramCanvas);
      }
    }
    catch (Exception localException3)
    {
      try
      {
        if (this.docTitleLayout != null)
        {
          paramCanvas.save();
          paramCanvas.translate(this.docTitleOffsetX + k, j);
          this.docTitleLayout.draw(paramCanvas);
          paramCanvas.restore();
        }
      }
      catch (Exception localException3)
      {
        try
        {
          for (;;)
          {
            Object localObject5;
            if (this.infoLayout != null)
            {
              paramCanvas.save();
              paramCanvas.translate(k, i);
              this.infoLayout.draw(paramCanvas);
              paramCanvas.restore();
            }
            if ((this.drawImageButton) && (this.photoImage.getVisible())) {
              this.radialProgress.draw(paramCanvas);
            }
            if (this.botButtons.isEmpty()) {
              return;
            }
            if (!this.currentMessageObject.isOutOwner()) {
              break;
            }
            i = getMeasuredWidth() - this.widthForButtons - AndroidUtilities.dp(10.0F);
            j = 0;
            for (;;)
            {
              if (j >= this.botButtons.size()) {
                return;
              }
              localObject4 = (BotButton)this.botButtons.get(j);
              m = ((BotButton)localObject4).y + this.layoutHeight - AndroidUtilities.dp(2.0F);
              localObject5 = Theme.systemDrawable;
              if (j != this.pressedBotButton) {
                break;
              }
              localObject2 = Theme.colorPressedFilter;
              ((Drawable)localObject5).setColorFilter((ColorFilter)localObject2);
              Theme.systemDrawable.setBounds(((BotButton)localObject4).x + i, m, ((BotButton)localObject4).x + i + ((BotButton)localObject4).width, ((BotButton)localObject4).height + m);
              Theme.systemDrawable.draw(paramCanvas);
              paramCanvas.save();
              paramCanvas.translate(((BotButton)localObject4).x + i + AndroidUtilities.dp(5.0F), (AndroidUtilities.dp(44.0F) - ((BotButton)localObject4).title.getLineBottom(((BotButton)localObject4).title.getLineCount() - 1)) / 2 + m);
              ((BotButton)localObject4).title.draw(paramCanvas);
              paramCanvas.restore();
              if (!(((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonUrl)) {
                break label5551;
              }
              k = ((BotButton)localObject4).x;
              n = ((BotButton)localObject4).width;
              i1 = AndroidUtilities.dp(3.0F);
              i2 = Theme.botLink.getIntrinsicWidth();
              setDrawableBounds(Theme.botLink, k + n - i1 - i2 + i, AndroidUtilities.dp(3.0F) + m);
              Theme.botLink.draw(paramCanvas);
              j += 1;
            }
            localException2 = localException2;
            FileLog.e("tmessages", localException2);
            continue;
            label4978:
            continue;
            label4981:
            i = -2427453;
            continue;
            label4988:
            docNamePaint.setColor(-11625772);
            Object localObject3 = infoPaint;
            if (isDrawSelectedBackground())
            {
              i = -7752511;
              label5013:
              ((TextPaint)localObject3).setColor(i);
              localObject3 = docBackPaint;
              if (!isDrawSelectedBackground()) {
                break label5071;
              }
              i = -3413258;
              label5035:
              ((Paint)localObject3).setColor(i);
              localObject3 = Theme.docMenuDrawable;
              if (!isDrawSelectedBackground()) {
                break label5078;
              }
            }
            label5071:
            label5078:
            for (i = 2;; i = 0)
            {
              localObject3 = localObject3[i];
              break;
              i = -6182221;
              break label5013;
              i = -1314571;
              break label5035;
            }
            label5083:
            i = this.photoImage.getImageX() + this.backgroundWidth - AndroidUtilities.dp(40.0F);
            this.otherX = i;
            j = this.photoImage.getImageY() + AndroidUtilities.dp(1.0F);
            this.otherY = j;
            setDrawableBounds((Drawable)localObject3, i, j);
            continue;
            label5140:
            i = 10;
            continue;
            label5146:
            if (this.buttonState == 1)
            {
              if (this.currentMessageObject.isOutOwner()) {}
              for (i = 8;; i = 11) {
                break;
              }
              label5176:
              i = 0;
              continue;
              label5181:
              this.radialProgress.swapBackground(Theme.photoStatesDrawables[this.buttonState][this.buttonPressed]);
              continue;
              label5205:
              i = -2427453;
              continue;
              label5212:
              localObject4 = this.radialProgress;
              if (isDrawSelectedBackground()) {}
              for (i = -3413258;; i = -1314571)
              {
                ((RadialProgress)localObject4).setProgressColor(i);
                i = m;
                break;
              }
              label5248:
              if (this.buttonState == -1) {
                this.radialProgress.setHideCurrentDrawable(true);
              }
              this.radialProgress.setProgressColor(-1);
              i = m;
              continue;
              label5278:
              i = this.buttonX;
              j = this.backgroundWidth;
              if (this.currentMessageObject.type == 0)
              {
                f = 58.0F;
                label5303:
                i = j + i - AndroidUtilities.dp(f);
                this.otherX = i;
                j = this.buttonY - AndroidUtilities.dp(5.0F);
                this.otherY = j;
                setDrawableBounds((Drawable)localObject3, i, j);
                k = this.buttonX + AndroidUtilities.dp(53.0F);
                j = this.buttonY + AndroidUtilities.dp(4.0F);
                m = this.buttonY + AndroidUtilities.dp(27.0F);
                if (!this.currentMessageObject.isOutOwner()) {
                  break label5445;
                }
                localObject4 = this.radialProgress;
                if ((!isDrawSelectedBackground()) && (this.buttonPressed == 0)) {
                  break label5438;
                }
              }
              label5438:
              for (i = -2820676;; i = -1048610)
              {
                ((RadialProgress)localObject4).setProgressColor(i);
                i = m;
                break;
                f = 48.0F;
                break label5303;
              }
              label5445:
              localObject4 = this.radialProgress;
              if ((isDrawSelectedBackground()) || (this.buttonPressed != 0)) {}
              for (i = -1902337;; i = -1)
              {
                ((RadialProgress)localObject4).setProgressColor(i);
                i = m;
                break;
              }
              localException3 = localException3;
              FileLog.e("tmessages", localException3);
            }
          }
        }
        catch (Exception localException4)
        {
          Object localObject4;
          label5551:
          label5736:
          do
          {
            do
            {
              for (;;)
              {
                FileLog.e("tmessages", localException4);
                continue;
                i = this.backgroundDrawableLeft;
                if (this.mediaBackground) {}
                for (f = 1.0F;; f = 7.0F)
                {
                  i += AndroidUtilities.dp(f);
                  break;
                }
                PorterDuffColorFilter localPorterDuffColorFilter = Theme.colorFilter;
                continue;
                if (!(((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonSwitchInline)) {
                  break;
                }
                k = ((BotButton)localObject4).x;
                n = ((BotButton)localObject4).width;
                i1 = AndroidUtilities.dp(3.0F);
                i2 = Theme.botInline.getIntrinsicWidth();
                setDrawableBounds(Theme.botInline, k + n - i1 - i2 + i, AndroidUtilities.dp(3.0F) + m);
                Theme.botInline.draw(paramCanvas);
              }
            } while ((!(((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonCallback)) && (!(((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonRequestGeoLocation)) && (!(((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonGame)));
            if ((((((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonCallback)) || ((((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonGame))) && ((!SendMessagesHelper.getInstance().isSendingCallback(this.currentMessageObject, ((BotButton)localObject4).button)) && ((!(((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonRequestGeoLocation)) || (!SendMessagesHelper.getInstance().isSendingCurrentLocation(this.currentMessageObject, ((BotButton)localObject4).button))))) {
              break;
            }
            k = 1;
          } while ((k == 0) && ((k != 0) || (((BotButton)localObject4).progressAlpha == 0.0F)));
          botProgressPaint.setAlpha(Math.min(255, (int)(((BotButton)localObject4).progressAlpha * 255.0F)));
          n = ((BotButton)localObject4).x + ((BotButton)localObject4).width - AndroidUtilities.dp(12.0F) + i;
          this.rect.set(n, AndroidUtilities.dp(4.0F) + m, AndroidUtilities.dp(8.0F) + n, AndroidUtilities.dp(12.0F) + m);
          paramCanvas.drawArc(this.rect, ((BotButton)localObject4).angle, 220.0F, false, botProgressPaint);
          invalidate((int)this.rect.left - AndroidUtilities.dp(2.0F), (int)this.rect.top - AndroidUtilities.dp(2.0F), (int)this.rect.right + AndroidUtilities.dp(2.0F), (int)this.rect.bottom + AndroidUtilities.dp(2.0F));
          l1 = System.currentTimeMillis();
          if (Math.abs(((BotButton)localObject4).lastUpdateTime - System.currentTimeMillis()) < 1000L)
          {
            l2 = l1 - ((BotButton)localObject4).lastUpdateTime;
            f = (float)(360L * l2) / 2000.0F;
            BotButton.access$702((BotButton)localObject4, (int)(((BotButton)localObject4).angle + f));
            BotButton.access$702((BotButton)localObject4, ((BotButton)localObject4).angle - ((BotButton)localObject4).angle / 360 * 360);
            if (k == 0) {
              break label6071;
            }
            if (((BotButton)localObject4).progressAlpha < 1.0F)
            {
              BotButton.access$602((BotButton)localObject4, ((BotButton)localObject4).progressAlpha + (float)l2 / 200.0F);
              if (((BotButton)localObject4).progressAlpha > 1.0F) {
                BotButton.access$602((BotButton)localObject4, 1.0F);
              }
            }
          }
          for (;;)
          {
            BotButton.access$802((BotButton)localObject4, l1);
            break;
            k = 0;
            break label5736;
            label6071:
            if (((BotButton)localObject4).progressAlpha > 0.0F)
            {
              BotButton.access$602((BotButton)localObject4, ((BotButton)localObject4).progressAlpha - (float)l2 / 200.0F);
              if (((BotButton)localObject4).progressAlpha < 0.0F) {
                BotButton.access$602((BotButton)localObject4, 0.0F);
              }
            }
          }
        }
      }
    }
  }
  
  public static StaticLayout generateStaticLayout(CharSequence paramCharSequence, TextPaint paramTextPaint, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder(paramCharSequence);
    int j = 0;
    StaticLayout localStaticLayout = new StaticLayout(paramCharSequence, paramTextPaint, paramInt2, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
    int i = 0;
    int m = paramInt1;
    int k;
    if (i < paramInt3)
    {
      localStaticLayout.getLineDirections(i);
      if ((localStaticLayout.getLineLeft(i) != 0.0F) || (localStaticLayout.isRtlCharAt(localStaticLayout.getLineStart(i))) || (localStaticLayout.isRtlCharAt(localStaticLayout.getLineEnd(i)))) {
        paramInt1 = paramInt2;
      }
      k = localStaticLayout.getLineEnd(i);
      if (k == paramCharSequence.length()) {
        m = paramInt1;
      }
    }
    else
    {
      label119:
      return StaticLayoutEx.createStaticLayout(localSpannableStringBuilder, paramTextPaint, m, Layout.Alignment.ALIGN_NORMAL, 1.0F, AndroidUtilities.dp(1.0F), false, TextUtils.TruncateAt.END, m, paramInt4);
    }
    m = k - 1;
    if (localSpannableStringBuilder.charAt(m + j) == ' ')
    {
      localSpannableStringBuilder.replace(m + j, m + j + 1, "\n");
      k = j;
    }
    for (;;)
    {
      m = paramInt1;
      if (i == localStaticLayout.getLineCount() - 1) {
        break label119;
      }
      m = paramInt1;
      if (i == paramInt4 - 1) {
        break label119;
      }
      i += 1;
      j = k;
      break;
      k = j;
      if (localSpannableStringBuilder.charAt(m + j) != '\n')
      {
        localSpannableStringBuilder.insert(m + j, "\n");
        k = j + 1;
      }
    }
  }
  
  private Drawable getDrawableForCurrentState()
  {
    int i = 3;
    int j = 0;
    int k = 1;
    int m = 1;
    int n = 1;
    int i1 = 1;
    Object localObject;
    if ((this.documentAttachType == 3) || (this.documentAttachType == 5))
    {
      if (this.buttonState == -1) {
        return null;
      }
      this.radialProgress.setAlphaForPrevious(false);
      localObject = Theme.fileStatesDrawable;
      if (this.currentMessageObject.isOutOwner())
      {
        i = this.buttonState;
        localObject = localObject[i];
        if ((!isDrawSelectedBackground()) && (this.buttonPressed == 0)) {
          break label106;
        }
      }
      label106:
      for (i = 1;; i = 0)
      {
        return localObject[i];
        i = this.buttonState + 5;
        break;
      }
    }
    if ((this.documentAttachType == 1) && (!this.drawPhotoImage))
    {
      this.radialProgress.setAlphaForPrevious(false);
      if (this.buttonState == -1)
      {
        localObject = Theme.fileStatesDrawable;
        if (this.currentMessageObject.isOutOwner())
        {
          localObject = localObject[i];
          if (!isDrawSelectedBackground()) {
            break label184;
          }
        }
        label184:
        for (i = i1;; i = 0)
        {
          return localObject[i];
          i = 8;
          break;
        }
      }
      if (this.buttonState == 0)
      {
        localObject = Theme.fileStatesDrawable;
        if (this.currentMessageObject.isOutOwner())
        {
          i = 2;
          localObject = localObject[i];
          if (!isDrawSelectedBackground()) {
            break label239;
          }
        }
        label239:
        for (i = k;; i = 0)
        {
          return localObject[i];
          i = 7;
          break;
        }
      }
      if (this.buttonState == 1)
      {
        localObject = Theme.fileStatesDrawable;
        if (this.currentMessageObject.isOutOwner())
        {
          i = 4;
          localObject = localObject[i];
          if (!isDrawSelectedBackground()) {
            break label296;
          }
        }
        label296:
        for (i = m;; i = 0)
        {
          return localObject[i];
          i = 9;
          break;
        }
      }
    }
    else
    {
      this.radialProgress.setAlphaForPrevious(true);
      if ((this.buttonState >= 0) && (this.buttonState < 4))
      {
        if (this.documentAttachType == 1)
        {
          i = this.buttonState;
          if (this.buttonState == 0) {
            if (this.currentMessageObject.isOutOwner()) {
              i = 7;
            }
          }
          while (this.buttonState != 1) {
            for (;;)
            {
              localObject = Theme.photoStatesDrawables[i];
              if (!isDrawSelectedBackground())
              {
                i = j;
                if (this.buttonPressed == 0) {}
              }
              else
              {
                i = 1;
              }
              return localObject[i];
              i = 10;
            }
          }
          if (this.currentMessageObject.isOutOwner()) {}
          for (i = 8;; i = 11) {
            break;
          }
        }
        return Theme.photoStatesDrawables[this.buttonState][this.buttonPressed];
      }
      if ((this.buttonState == -1) && (this.documentAttachType == 1))
      {
        localObject = Theme.photoStatesDrawables;
        if (this.currentMessageObject.isOutOwner())
        {
          i = 9;
          localObject = localObject[i];
          if (!isDrawSelectedBackground()) {
            break label498;
          }
        }
        label498:
        for (i = n;; i = 0)
        {
          return localObject[i];
          i = 12;
          break;
        }
      }
    }
    return null;
  }
  
  private int getMaxNameWidth()
  {
    if (this.documentAttachType == 6)
    {
      if (AndroidUtilities.isTablet()) {
        if ((this.isChat) && (!this.currentMessageObject.isOutOwner()) && (this.currentMessageObject.isFromUser())) {
          i = AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(42.0F);
        }
      }
      for (;;)
      {
        return i - this.backgroundWidth - AndroidUtilities.dp(57.0F);
        i = AndroidUtilities.getMinTabletSide();
        continue;
        if ((this.isChat) && (!this.currentMessageObject.isOutOwner()) && (this.currentMessageObject.isFromUser())) {
          i = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(42.0F);
        } else {
          i = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
        }
      }
    }
    int i = this.backgroundWidth;
    if (this.mediaBackground) {}
    for (float f = 22.0F;; f = 31.0F) {
      return i - AndroidUtilities.dp(f);
    }
  }
  
  private boolean intersect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    if (paramFloat1 <= paramFloat3) {
      if (paramFloat2 < paramFloat3) {}
    }
    while (paramFloat1 <= paramFloat4)
    {
      return true;
      return false;
    }
    return false;
  }
  
  private boolean isDrawSelectedBackground()
  {
    return ((isPressed()) && (this.isCheckPressed)) || ((!this.isCheckPressed) && (this.isPressed)) || (this.isHighlighted);
  }
  
  private boolean isPhotoDataChanged(MessageObject paramMessageObject)
  {
    if ((paramMessageObject.type == 0) || (paramMessageObject.type == 14)) {
      return false;
    }
    if (paramMessageObject.type == 4)
    {
      if (this.currentUrl == null) {
        return true;
      }
      double d1 = paramMessageObject.messageOwner.media.geo.lat;
      double d2 = paramMessageObject.messageOwner.media.geo._long;
      if (!String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=100x100&maptype=roadmap&scale=%d&markers=color:red|size:mid|%f,%f&sensor=false", new Object[] { Double.valueOf(d1), Double.valueOf(d2), Integer.valueOf(Math.min(2, (int)Math.ceil(AndroidUtilities.density))), Double.valueOf(d1), Double.valueOf(d2) }).equals(this.currentUrl)) {
        return true;
      }
    }
    else
    {
      if ((this.currentPhotoObject == null) || ((this.currentPhotoObject.location instanceof TLRPC.TL_fileLocationUnavailable))) {
        return true;
      }
      if ((this.currentMessageObject != null) && (this.photoNotSet) && (FileLoader.getPathToMessage(this.currentMessageObject.messageOwner).exists())) {
        return true;
      }
    }
    return false;
  }
  
  private boolean isUserDataChanged()
  {
    boolean bool2 = false;
    if ((this.currentMessageObject != null) && (!this.hasLinkPreview) && (this.currentMessageObject.messageOwner.media != null) && ((this.currentMessageObject.messageOwner.media.webpage instanceof TLRPC.TL_webPage))) {}
    label160:
    label197:
    label618:
    label647:
    label649:
    label664:
    for (;;)
    {
      return true;
      if ((this.currentMessageObject == null) || ((this.currentUser == null) && (this.currentChat == null))) {
        return false;
      }
      if ((this.lastSendState == this.currentMessageObject.messageOwner.send_state) && (this.lastDeleteDate == this.currentMessageObject.messageOwner.destroyTime) && (this.lastViewsCount == this.currentMessageObject.messageOwner.views))
      {
        Object localObject2 = null;
        Object localObject1 = null;
        Object localObject3;
        Object localObject4;
        if (this.currentMessageObject.isFromUser())
        {
          localObject3 = MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id));
          localObject4 = null;
          localObject2 = localObject4;
          if (this.isAvatarVisible)
          {
            if ((localObject3 == null) || (((TLRPC.User)localObject3).photo == null)) {
              break label618;
            }
            localObject2 = ((TLRPC.User)localObject3).photo.photo_small;
          }
          if (((this.replyTextLayout == null) && (this.currentMessageObject.replyMessageObject != null)) || ((this.currentPhoto == null) && (localObject2 != null)) || ((this.currentPhoto != null) && (localObject2 == null)) || ((this.currentPhoto != null) && (localObject2 != null) && ((this.currentPhoto.local_id != ((TLRPC.FileLocation)localObject2).local_id) || (this.currentPhoto.volume_id != ((TLRPC.FileLocation)localObject2).volume_id)))) {
            break label647;
          }
          localObject4 = null;
          localObject2 = localObject4;
          if (this.currentMessageObject.replyMessageObject != null)
          {
            TLRPC.PhotoSize localPhotoSize = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.replyMessageObject.photoThumbs, 80);
            localObject2 = localObject4;
            if (localPhotoSize != null)
            {
              localObject2 = localObject4;
              if (this.currentMessageObject.replyMessageObject.type != 13) {
                localObject2 = localPhotoSize.location;
              }
            }
          }
          if ((this.currentReplyPhoto == null) && (localObject2 != null)) {
            continue;
          }
          localObject4 = null;
          localObject2 = localObject4;
          if (this.drawName)
          {
            localObject2 = localObject4;
            if (this.isChat)
            {
              localObject2 = localObject4;
              if (!this.currentMessageObject.isOutOwner())
              {
                if (localObject3 == null) {
                  break label649;
                }
                localObject2 = UserObject.getUserName((TLRPC.User)localObject3);
              }
            }
          }
        }
        for (;;)
        {
          if (((this.currentNameString == null) && (localObject2 != null)) || ((this.currentNameString != null) && (localObject2 == null)) || ((this.currentNameString != null) && (localObject2 != null) && (!this.currentNameString.equals(localObject2)))) {
            break label664;
          }
          if (!this.drawForwardedName) {
            break label666;
          }
          localObject1 = this.currentMessageObject.getForwardedName();
          boolean bool1;
          if (((this.currentForwardNameString != null) || (localObject1 == null)) && ((this.currentForwardNameString == null) || (localObject1 != null)))
          {
            bool1 = bool2;
            if (this.currentForwardNameString != null)
            {
              bool1 = bool2;
              if (localObject1 != null)
              {
                bool1 = bool2;
                if (this.currentForwardNameString.equals(localObject1)) {}
              }
            }
          }
          else
          {
            bool1 = true;
          }
          return bool1;
          if (this.currentMessageObject.messageOwner.from_id < 0)
          {
            localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(-this.currentMessageObject.messageOwner.from_id));
            localObject3 = localObject2;
            break label160;
          }
          localObject3 = localObject2;
          if (!this.currentMessageObject.messageOwner.post) {
            break label160;
          }
          localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(this.currentMessageObject.messageOwner.to_id.channel_id));
          localObject3 = localObject2;
          break label160;
          localObject2 = localObject4;
          if (localObject1 == null) {
            break label197;
          }
          localObject2 = localObject4;
          if (((TLRPC.Chat)localObject1).photo == null) {
            break label197;
          }
          localObject2 = ((TLRPC.Chat)localObject1).photo.photo_small;
          break label197;
          break;
          localObject2 = localObject4;
          if (localObject1 != null) {
            localObject2 = ((TLRPC.Chat)localObject1).title;
          }
        }
      }
    }
    label666:
    return false;
  }
  
  private void measureTime(MessageObject paramMessageObject)
  {
    int i;
    TLRPC.User localUser;
    int j;
    Object localObject;
    if ((!paramMessageObject.isOutOwner()) && (paramMessageObject.messageOwner.from_id > 0) && (paramMessageObject.messageOwner.post))
    {
      i = 1;
      localUser = MessagesController.getInstance().getUser(Integer.valueOf(paramMessageObject.messageOwner.from_id));
      j = i;
      if (i != 0)
      {
        j = i;
        if (localUser == null) {
          j = 0;
        }
      }
      localObject = null;
      if (this.currentMessageObject.isFromUser()) {
        localObject = MessagesController.getInstance().getUser(Integer.valueOf(paramMessageObject.messageOwner.from_id));
      }
      if ((paramMessageObject.messageOwner.via_bot_id != 0) || (paramMessageObject.messageOwner.via_bot_name != null) || ((localObject != null) && (((TLRPC.User)localObject).bot)) || ((paramMessageObject.messageOwner.flags & 0x8000) == 0)) {
        break label498;
      }
      localObject = LocaleController.getString("EditedMessage", 2131165598) + " " + LocaleController.getInstance().formatterDay.format(paramMessageObject.messageOwner.date * 1000L);
      label194:
      if (j == 0) {
        break label524;
      }
    }
    label498:
    label524:
    for (this.currentTimeString = (", " + (String)localObject);; this.currentTimeString = ((String)localObject))
    {
      i = (int)Math.ceil(timePaint.measureText(this.currentTimeString));
      this.timeWidth = i;
      this.timeTextWidth = i;
      if ((paramMessageObject.messageOwner.flags & 0x400) != 0)
      {
        this.currentViewsString = String.format("%s", new Object[] { LocaleController.formatShortNumber(Math.max(1, paramMessageObject.messageOwner.views), null) });
        this.viewsTextWidth = ((int)Math.ceil(timePaint.measureText(this.currentViewsString)));
        this.timeWidth += this.viewsTextWidth + Theme.viewsCountDrawable[0].getIntrinsicWidth() + AndroidUtilities.dp(10.0F);
      }
      if (j != 0)
      {
        if (this.availableTimeWidth == 0) {
          this.availableTimeWidth = AndroidUtilities.dp(1000.0F);
        }
        localObject = ContactsController.formatName(localUser.first_name, localUser.last_name).replace('\n', ' ');
        j = this.availableTimeWidth - this.timeWidth;
        int k = (int)Math.ceil(timePaint.measureText((CharSequence)localObject, 0, ((CharSequence)localObject).length()));
        paramMessageObject = (MessageObject)localObject;
        i = k;
        if (k > j)
        {
          paramMessageObject = TextUtils.ellipsize((CharSequence)localObject, timePaint, j, TextUtils.TruncateAt.END);
          i = j;
        }
        this.currentTimeString = (paramMessageObject + this.currentTimeString);
        this.timeTextWidth += i;
        this.timeWidth += i;
      }
      return;
      i = 0;
      break;
      localObject = LocaleController.getInstance().formatterDay.format(paramMessageObject.messageOwner.date * 1000L);
      break label194;
    }
  }
  
  private LinkPath obtainNewUrlPath(boolean paramBoolean)
  {
    LinkPath localLinkPath;
    if (!this.urlPathCache.isEmpty())
    {
      localLinkPath = (LinkPath)this.urlPathCache.get(0);
      this.urlPathCache.remove(0);
    }
    while (paramBoolean)
    {
      this.urlPathSelection.add(localLinkPath);
      return localLinkPath;
      localLinkPath = new LinkPath();
    }
    this.urlPath.add(localLinkPath);
    return localLinkPath;
  }
  
  private void resetPressedLink(int paramInt)
  {
    if ((this.pressedLink == null) || ((this.pressedLinkType != paramInt) && (paramInt != -1))) {
      return;
    }
    resetUrlPaths(false);
    this.pressedLink = null;
    this.pressedLinkType = -1;
    invalidate();
  }
  
  private void resetUrlPaths(boolean paramBoolean)
  {
    if (paramBoolean) {
      if (!this.urlPathSelection.isEmpty()) {}
    }
    while (this.urlPath.isEmpty())
    {
      return;
      this.urlPathCache.addAll(this.urlPathSelection);
      this.urlPathSelection.clear();
      return;
    }
    this.urlPathCache.addAll(this.urlPath);
    this.urlPath.clear();
  }
  
  /* Error */
  private void setMessageObjectInternal(MessageObject paramMessageObject)
  {
    // Byte code:
    //   0: aload_1
    //   1: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4: getfield 1821	org/telegram/tgnet/TLRPC$Message:flags	I
    //   7: sipush 1024
    //   10: iand
    //   11: ifeq +44 -> 55
    //   14: aload_0
    //   15: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   18: invokevirtual 1516	org/telegram/messenger/MessageObject:isContentUnread	()Z
    //   21: ifeq +1883 -> 1904
    //   24: aload_0
    //   25: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   28: invokevirtual 854	org/telegram/messenger/MessageObject:isOut	()Z
    //   31: ifne +1873 -> 1904
    //   34: invokestatic 868	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   37: aload_0
    //   38: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   41: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   44: iconst_0
    //   45: invokevirtual 1895	org/telegram/messenger/MessagesController:addToViewsQueue	(Lorg/telegram/tgnet/TLRPC$Message;Z)V
    //   48: aload_0
    //   49: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   52: invokevirtual 1898	org/telegram/messenger/MessageObject:setContentIsRead	()V
    //   55: aload_0
    //   56: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   59: invokevirtual 857	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   62: ifeq +1877 -> 1939
    //   65: aload_0
    //   66: invokestatic 868	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   69: aload_0
    //   70: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   73: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   76: getfield 871	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   79: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   82: invokevirtual 881	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   85: putfield 1746	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   88: aload_0
    //   89: getfield 1668	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   92: ifeq +84 -> 176
    //   95: aload_1
    //   96: invokevirtual 575	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   99: ifne +77 -> 176
    //   102: aload_1
    //   103: invokevirtual 857	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   106: ifeq +70 -> 176
    //   109: aload_0
    //   110: iconst_1
    //   111: putfield 1762	org/telegram/ui/Cells/ChatMessageCell:isAvatarVisible	Z
    //   114: aload_0
    //   115: getfield 1746	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   118: ifnull +1911 -> 2029
    //   121: aload_0
    //   122: getfield 1746	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   125: getfield 1766	org/telegram/tgnet/TLRPC$User:photo	Lorg/telegram/tgnet/TLRPC$UserProfilePhoto;
    //   128: ifnull +1893 -> 2021
    //   131: aload_0
    //   132: aload_0
    //   133: getfield 1746	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   136: getfield 1766	org/telegram/tgnet/TLRPC$User:photo	Lorg/telegram/tgnet/TLRPC$UserProfilePhoto;
    //   139: getfield 1771	org/telegram/tgnet/TLRPC$UserProfilePhoto:photo_small	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   142: putfield 1778	org/telegram/ui/Cells/ChatMessageCell:currentPhoto	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   145: aload_0
    //   146: getfield 424	org/telegram/ui/Cells/ChatMessageCell:avatarDrawable	Lorg/telegram/ui/Components/AvatarDrawable;
    //   149: aload_0
    //   150: getfield 1746	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   153: invokevirtual 1902	org/telegram/ui/Components/AvatarDrawable:setInfo	(Lorg/telegram/tgnet/TLRPC$User;)V
    //   156: aload_0
    //   157: getfield 415	org/telegram/ui/Cells/ChatMessageCell:avatarImage	Lorg/telegram/messenger/ImageReceiver;
    //   160: aload_0
    //   161: getfield 1778	org/telegram/ui/Cells/ChatMessageCell:currentPhoto	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   164: ldc_w 1904
    //   167: aload_0
    //   168: getfield 424	org/telegram/ui/Cells/ChatMessageCell:avatarDrawable	Lorg/telegram/ui/Components/AvatarDrawable;
    //   171: aconst_null
    //   172: iconst_0
    //   173: invokevirtual 1907	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Ljava/lang/String;Z)V
    //   176: aload_0
    //   177: aload_1
    //   178: invokespecial 1040	org/telegram/ui/Cells/ChatMessageCell:measureTime	(Lorg/telegram/messenger/MessageObject;)V
    //   181: aload_0
    //   182: iconst_0
    //   183: putfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   186: aconst_null
    //   187: astore 7
    //   189: aconst_null
    //   190: astore 8
    //   192: aload_1
    //   193: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   196: getfield 920	org/telegram/tgnet/TLRPC$Message:via_bot_id	I
    //   199: ifeq +1908 -> 2107
    //   202: invokestatic 868	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   205: aload_1
    //   206: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   209: getfield 920	org/telegram/tgnet/TLRPC$Message:via_bot_id	I
    //   212: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   215: invokevirtual 881	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   218: astore 9
    //   220: aload 8
    //   222: astore 5
    //   224: aload 7
    //   226: astore 6
    //   228: aload 9
    //   230: ifnull +115 -> 345
    //   233: aload 8
    //   235: astore 5
    //   237: aload 7
    //   239: astore 6
    //   241: aload 9
    //   243: getfield 1908	org/telegram/tgnet/TLRPC$User:username	Ljava/lang/String;
    //   246: ifnull +99 -> 345
    //   249: aload 8
    //   251: astore 5
    //   253: aload 7
    //   255: astore 6
    //   257: aload 9
    //   259: getfield 1908	org/telegram/tgnet/TLRPC$User:username	Ljava/lang/String;
    //   262: invokevirtual 910	java/lang/String:length	()I
    //   265: ifle +80 -> 345
    //   268: new 1182	java/lang/StringBuilder
    //   271: dup
    //   272: invokespecial 1183	java/lang/StringBuilder:<init>	()V
    //   275: ldc_w 1910
    //   278: invokevirtual 1187	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   281: aload 9
    //   283: getfield 1908	org/telegram/tgnet/TLRPC$User:username	Ljava/lang/String;
    //   286: invokevirtual 1187	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   289: invokevirtual 1195	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   292: astore 6
    //   294: ldc_w 1912
    //   297: iconst_1
    //   298: anewarray 1106	java/lang/Object
    //   301: dup
    //   302: iconst_0
    //   303: aload 6
    //   305: aastore
    //   306: invokestatic 1110	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   309: invokestatic 1916	org/telegram/messenger/AndroidUtilities:replaceTags	(Ljava/lang/String;)Landroid/text/SpannableStringBuilder;
    //   312: astore 5
    //   314: aload_0
    //   315: getstatic 384	org/telegram/ui/Cells/ChatMessageCell:replyNamePaint	Landroid/text/TextPaint;
    //   318: aload 5
    //   320: iconst_0
    //   321: aload 5
    //   323: invokeinterface 1000 1 0
    //   328: invokevirtual 1873	android/text/TextPaint:measureText	(Ljava/lang/CharSequence;II)F
    //   331: f2d
    //   332: invokestatic 987	java/lang/Math:ceil	(D)D
    //   335: d2i
    //   336: putfield 1918	org/telegram/ui/Cells/ChatMessageCell:viaWidth	I
    //   339: aload_0
    //   340: aload 9
    //   342: putfield 1920	org/telegram/ui/Cells/ChatMessageCell:currentViaBotUser	Lorg/telegram/tgnet/TLRPC$User;
    //   345: aload_0
    //   346: getfield 1791	org/telegram/ui/Cells/ChatMessageCell:drawName	Z
    //   349: ifeq +1873 -> 2222
    //   352: aload_0
    //   353: getfield 1668	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   356: ifeq +1866 -> 2222
    //   359: aload_0
    //   360: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   363: invokevirtual 575	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   366: ifne +1856 -> 2222
    //   369: iconst_1
    //   370: istore_3
    //   371: aload_1
    //   372: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   375: getfield 846	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$TL_messageFwdHeader;
    //   378: ifnull +12 -> 390
    //   381: aload_1
    //   382: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   385: bipush 14
    //   387: if_icmpne +1840 -> 2227
    //   390: aload 6
    //   392: ifnull +1835 -> 2227
    //   395: iconst_1
    //   396: istore_2
    //   397: iload_3
    //   398: ifne +7 -> 405
    //   401: iload_2
    //   402: ifeq +1997 -> 2399
    //   405: aload_0
    //   406: iconst_1
    //   407: putfield 1922	org/telegram/ui/Cells/ChatMessageCell:drawNameLayout	Z
    //   410: aload_0
    //   411: aload_0
    //   412: invokespecial 1924	org/telegram/ui/Cells/ChatMessageCell:getMaxNameWidth	()I
    //   415: putfield 1926	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   418: aload_0
    //   419: getfield 1926	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   422: ifge +13 -> 435
    //   425: aload_0
    //   426: ldc_w 1927
    //   429: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   432: putfield 1926	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   435: iload_3
    //   436: ifeq +1827 -> 2263
    //   439: aload_0
    //   440: getfield 1746	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   443: ifnull +1789 -> 2232
    //   446: aload_0
    //   447: aload_0
    //   448: getfield 1746	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   451: invokestatic 1797	org/telegram/messenger/UserObject:getUserName	(Lorg/telegram/tgnet/TLRPC$User;)Ljava/lang/String;
    //   454: putfield 1799	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   457: aload_0
    //   458: getfield 1799	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   461: bipush 10
    //   463: bipush 32
    //   465: invokevirtual 1069	java/lang/String:replace	(CC)Ljava/lang/String;
    //   468: astore 7
    //   470: getstatic 380	org/telegram/ui/Cells/ChatMessageCell:namePaint	Landroid/text/TextPaint;
    //   473: astore 8
    //   475: aload_0
    //   476: getfield 1926	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   479: istore 4
    //   481: iload_2
    //   482: ifeq +1791 -> 2273
    //   485: aload_0
    //   486: getfield 1918	org/telegram/ui/Cells/ChatMessageCell:viaWidth	I
    //   489: istore_3
    //   490: aload 7
    //   492: aload 8
    //   494: iload 4
    //   496: iload_3
    //   497: isub
    //   498: i2f
    //   499: getstatic 1075	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   502: invokestatic 1079	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   505: astore 8
    //   507: aload 8
    //   509: astore 7
    //   511: iload_2
    //   512: ifeq +194 -> 706
    //   515: aload_0
    //   516: getstatic 380	org/telegram/ui/Cells/ChatMessageCell:namePaint	Landroid/text/TextPaint;
    //   519: aload 8
    //   521: iconst_0
    //   522: aload 8
    //   524: invokeinterface 1000 1 0
    //   529: invokevirtual 1873	android/text/TextPaint:measureText	(Ljava/lang/CharSequence;II)F
    //   532: f2d
    //   533: invokestatic 987	java/lang/Math:ceil	(D)D
    //   536: d2i
    //   537: putfield 1929	org/telegram/ui/Cells/ChatMessageCell:viaNameWidth	I
    //   540: aload_0
    //   541: getfield 1929	org/telegram/ui/Cells/ChatMessageCell:viaNameWidth	I
    //   544: ifeq +18 -> 562
    //   547: aload_0
    //   548: aload_0
    //   549: getfield 1929	org/telegram/ui/Cells/ChatMessageCell:viaNameWidth	I
    //   552: ldc_w 930
    //   555: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   558: iadd
    //   559: putfield 1929	org/telegram/ui/Cells/ChatMessageCell:viaNameWidth	I
    //   562: aload_0
    //   563: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   566: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   569: bipush 13
    //   571: if_icmpne +1707 -> 2278
    //   574: iconst_m1
    //   575: istore_2
    //   576: aload_0
    //   577: getfield 1799	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   580: invokevirtual 910	java/lang/String:length	()I
    //   583: ifle +1719 -> 2302
    //   586: new 1626	android/text/SpannableStringBuilder
    //   589: dup
    //   590: ldc_w 1931
    //   593: iconst_2
    //   594: anewarray 1106	java/lang/Object
    //   597: dup
    //   598: iconst_0
    //   599: aload 8
    //   601: aastore
    //   602: dup
    //   603: iconst_1
    //   604: aload 6
    //   606: aastore
    //   607: invokestatic 1110	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   610: invokespecial 1629	android/text/SpannableStringBuilder:<init>	(Ljava/lang/CharSequence;)V
    //   613: astore 7
    //   615: aload 7
    //   617: new 1933	org/telegram/ui/Components/TypefaceSpan
    //   620: dup
    //   621: getstatic 1939	android/graphics/Typeface:DEFAULT	Landroid/graphics/Typeface;
    //   624: iconst_0
    //   625: iload_2
    //   626: invokespecial 1942	org/telegram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;II)V
    //   629: aload 8
    //   631: invokeinterface 1000 1 0
    //   636: iconst_1
    //   637: iadd
    //   638: aload 8
    //   640: invokeinterface 1000 1 0
    //   645: iconst_4
    //   646: iadd
    //   647: bipush 33
    //   649: invokevirtual 1946	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   652: aload 7
    //   654: new 1933	org/telegram/ui/Components/TypefaceSpan
    //   657: dup
    //   658: ldc_w 305
    //   661: invokestatic 311	org/telegram/messenger/AndroidUtilities:getTypeface	(Ljava/lang/String;)Landroid/graphics/Typeface;
    //   664: iconst_0
    //   665: iload_2
    //   666: invokespecial 1942	org/telegram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;II)V
    //   669: aload 8
    //   671: invokeinterface 1000 1 0
    //   676: iconst_5
    //   677: iadd
    //   678: aload 7
    //   680: invokevirtual 1947	android/text/SpannableStringBuilder:length	()I
    //   683: bipush 33
    //   685: invokevirtual 1946	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   688: aload 7
    //   690: getstatic 380	org/telegram/ui/Cells/ChatMessageCell:namePaint	Landroid/text/TextPaint;
    //   693: aload_0
    //   694: getfield 1926	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   697: i2f
    //   698: getstatic 1075	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   701: invokestatic 1079	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   704: astore 7
    //   706: aload_0
    //   707: new 288	android/text/StaticLayout
    //   710: dup
    //   711: aload 7
    //   713: getstatic 380	org/telegram/ui/Cells/ChatMessageCell:namePaint	Landroid/text/TextPaint;
    //   716: aload_0
    //   717: getfield 1926	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   720: fconst_2
    //   721: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   724: iadd
    //   725: getstatic 1085	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   728: fconst_1
    //   729: fconst_0
    //   730: iconst_0
    //   731: invokespecial 1088	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   734: putfield 1949	org/telegram/ui/Cells/ChatMessageCell:nameLayout	Landroid/text/StaticLayout;
    //   737: aload_0
    //   738: getfield 1949	org/telegram/ui/Cells/ChatMessageCell:nameLayout	Landroid/text/StaticLayout;
    //   741: ifnull +1637 -> 2378
    //   744: aload_0
    //   745: getfield 1949	org/telegram/ui/Cells/ChatMessageCell:nameLayout	Landroid/text/StaticLayout;
    //   748: invokevirtual 1093	android/text/StaticLayout:getLineCount	()I
    //   751: ifle +1627 -> 2378
    //   754: aload_0
    //   755: aload_0
    //   756: getfield 1949	org/telegram/ui/Cells/ChatMessageCell:nameLayout	Landroid/text/StaticLayout;
    //   759: iconst_0
    //   760: invokevirtual 654	android/text/StaticLayout:getLineWidth	(I)F
    //   763: f2d
    //   764: invokestatic 987	java/lang/Math:ceil	(D)D
    //   767: d2i
    //   768: putfield 1926	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   771: aload_1
    //   772: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   775: bipush 13
    //   777: if_icmpeq +18 -> 795
    //   780: aload_0
    //   781: aload_0
    //   782: getfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   785: ldc_w 1490
    //   788: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   791: iadd
    //   792: putfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   795: aload_0
    //   796: aload_0
    //   797: getfield 1949	org/telegram/ui/Cells/ChatMessageCell:nameLayout	Landroid/text/StaticLayout;
    //   800: iconst_0
    //   801: invokevirtual 651	android/text/StaticLayout:getLineLeft	(I)F
    //   804: putfield 1951	org/telegram/ui/Cells/ChatMessageCell:nameOffsetX	F
    //   807: aload_0
    //   808: getfield 1799	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   811: invokevirtual 910	java/lang/String:length	()I
    //   814: ifne +8 -> 822
    //   817: aload_0
    //   818: aconst_null
    //   819: putfield 1799	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   822: aload_0
    //   823: aconst_null
    //   824: putfield 1953	org/telegram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/telegram/tgnet/TLRPC$User;
    //   827: aload_0
    //   828: aconst_null
    //   829: putfield 1806	org/telegram/ui/Cells/ChatMessageCell:currentForwardNameString	Ljava/lang/String;
    //   832: aload_0
    //   833: aconst_null
    //   834: putfield 1955	org/telegram/ui/Cells/ChatMessageCell:currentForwardChannel	Lorg/telegram/tgnet/TLRPC$Chat;
    //   837: aload_0
    //   838: getfield 290	org/telegram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   841: iconst_0
    //   842: aconst_null
    //   843: aastore
    //   844: aload_0
    //   845: getfield 290	org/telegram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   848: iconst_1
    //   849: aconst_null
    //   850: aastore
    //   851: aload_0
    //   852: iconst_0
    //   853: putfield 1957	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   856: aload_0
    //   857: getfield 1801	org/telegram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   860: ifeq +512 -> 1372
    //   863: aload_1
    //   864: invokevirtual 1960	org/telegram/messenger/MessageObject:isForwarded	()Z
    //   867: ifeq +505 -> 1372
    //   870: aload_1
    //   871: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   874: getfield 846	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$TL_messageFwdHeader;
    //   877: getfield 851	org/telegram/tgnet/TLRPC$TL_messageFwdHeader:channel_id	I
    //   880: ifeq +26 -> 906
    //   883: aload_0
    //   884: invokestatic 868	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   887: aload_1
    //   888: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   891: getfield 846	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$TL_messageFwdHeader;
    //   894: getfield 851	org/telegram/tgnet/TLRPC$TL_messageFwdHeader:channel_id	I
    //   897: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   900: invokevirtual 900	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   903: putfield 1955	org/telegram/ui/Cells/ChatMessageCell:currentForwardChannel	Lorg/telegram/tgnet/TLRPC$Chat;
    //   906: aload_1
    //   907: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   910: getfield 846	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$TL_messageFwdHeader;
    //   913: getfield 1961	org/telegram/tgnet/TLRPC$TL_messageFwdHeader:from_id	I
    //   916: ifeq +26 -> 942
    //   919: aload_0
    //   920: invokestatic 868	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   923: aload_1
    //   924: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   927: getfield 846	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$TL_messageFwdHeader;
    //   930: getfield 1961	org/telegram/tgnet/TLRPC$TL_messageFwdHeader:from_id	I
    //   933: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   936: invokevirtual 881	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   939: putfield 1953	org/telegram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/telegram/tgnet/TLRPC$User;
    //   942: aload_0
    //   943: getfield 1953	org/telegram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/telegram/tgnet/TLRPC$User;
    //   946: ifnonnull +10 -> 956
    //   949: aload_0
    //   950: getfield 1955	org/telegram/ui/Cells/ChatMessageCell:currentForwardChannel	Lorg/telegram/tgnet/TLRPC$Chat;
    //   953: ifnull +419 -> 1372
    //   956: aload_0
    //   957: getfield 1955	org/telegram/ui/Cells/ChatMessageCell:currentForwardChannel	Lorg/telegram/tgnet/TLRPC$Chat;
    //   960: ifnull +1471 -> 2431
    //   963: aload_0
    //   964: getfield 1953	org/telegram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/telegram/tgnet/TLRPC$User;
    //   967: ifnull +1450 -> 2417
    //   970: aload_0
    //   971: ldc_w 1963
    //   974: iconst_2
    //   975: anewarray 1106	java/lang/Object
    //   978: dup
    //   979: iconst_0
    //   980: aload_0
    //   981: getfield 1955	org/telegram/ui/Cells/ChatMessageCell:currentForwardChannel	Lorg/telegram/tgnet/TLRPC$Chat;
    //   984: getfield 1815	org/telegram/tgnet/TLRPC$Chat:title	Ljava/lang/String;
    //   987: aastore
    //   988: dup
    //   989: iconst_1
    //   990: aload_0
    //   991: getfield 1953	org/telegram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/telegram/tgnet/TLRPC$User;
    //   994: invokestatic 1797	org/telegram/messenger/UserObject:getUserName	(Lorg/telegram/tgnet/TLRPC$User;)Ljava/lang/String;
    //   997: aastore
    //   998: invokestatic 1110	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   1001: putfield 1806	org/telegram/ui/Cells/ChatMessageCell:currentForwardNameString	Ljava/lang/String;
    //   1004: aload_0
    //   1005: aload_0
    //   1006: invokespecial 1924	org/telegram/ui/Cells/ChatMessageCell:getMaxNameWidth	()I
    //   1009: putfield 1957	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1012: getstatic 382	org/telegram/ui/Cells/ChatMessageCell:forwardNamePaint	Landroid/text/TextPaint;
    //   1015: new 1182	java/lang/StringBuilder
    //   1018: dup
    //   1019: invokespecial 1183	java/lang/StringBuilder:<init>	()V
    //   1022: ldc_w 1965
    //   1025: ldc_w 1966
    //   1028: invokestatic 1166	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1031: invokevirtual 1187	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1034: ldc_w 1189
    //   1037: invokevirtual 1187	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1040: invokevirtual 1195	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1043: invokevirtual 1034	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   1046: f2d
    //   1047: invokestatic 987	java/lang/Math:ceil	(D)D
    //   1050: d2i
    //   1051: istore_2
    //   1052: aload_0
    //   1053: getfield 1806	org/telegram/ui/Cells/ChatMessageCell:currentForwardNameString	Ljava/lang/String;
    //   1056: bipush 10
    //   1058: bipush 32
    //   1060: invokevirtual 1069	java/lang/String:replace	(CC)Ljava/lang/String;
    //   1063: getstatic 384	org/telegram/ui/Cells/ChatMessageCell:replyNamePaint	Landroid/text/TextPaint;
    //   1066: aload_0
    //   1067: getfield 1957	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1070: iload_2
    //   1071: isub
    //   1072: aload_0
    //   1073: getfield 1918	org/telegram/ui/Cells/ChatMessageCell:viaWidth	I
    //   1076: isub
    //   1077: i2f
    //   1078: getstatic 1075	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   1081: invokestatic 1079	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   1084: astore 7
    //   1086: aload 5
    //   1088: ifnull +1364 -> 2452
    //   1091: aload_0
    //   1092: getstatic 382	org/telegram/ui/Cells/ChatMessageCell:forwardNamePaint	Landroid/text/TextPaint;
    //   1095: new 1182	java/lang/StringBuilder
    //   1098: dup
    //   1099: invokespecial 1183	java/lang/StringBuilder:<init>	()V
    //   1102: ldc_w 1965
    //   1105: ldc_w 1966
    //   1108: invokestatic 1166	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1111: invokevirtual 1187	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1114: ldc_w 1189
    //   1117: invokevirtual 1187	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1120: aload 7
    //   1122: invokevirtual 1876	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1125: invokevirtual 1195	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1128: invokevirtual 1034	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   1131: f2d
    //   1132: invokestatic 987	java/lang/Math:ceil	(D)D
    //   1135: d2i
    //   1136: putfield 1929	org/telegram/ui/Cells/ChatMessageCell:viaNameWidth	I
    //   1139: ldc_w 1968
    //   1142: iconst_3
    //   1143: anewarray 1106	java/lang/Object
    //   1146: dup
    //   1147: iconst_0
    //   1148: ldc_w 1965
    //   1151: ldc_w 1966
    //   1154: invokestatic 1166	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1157: aastore
    //   1158: dup
    //   1159: iconst_1
    //   1160: aload 7
    //   1162: aastore
    //   1163: dup
    //   1164: iconst_2
    //   1165: aload 6
    //   1167: aastore
    //   1168: invokestatic 1110	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   1171: invokestatic 1916	org/telegram/messenger/AndroidUtilities:replaceTags	(Ljava/lang/String;)Landroid/text/SpannableStringBuilder;
    //   1174: astore 5
    //   1176: aload 5
    //   1178: getstatic 382	org/telegram/ui/Cells/ChatMessageCell:forwardNamePaint	Landroid/text/TextPaint;
    //   1181: aload_0
    //   1182: getfield 1957	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1185: i2f
    //   1186: getstatic 1075	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   1189: invokestatic 1079	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   1192: astore 5
    //   1194: aload_0
    //   1195: getfield 290	org/telegram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   1198: iconst_1
    //   1199: new 288	android/text/StaticLayout
    //   1202: dup
    //   1203: aload 5
    //   1205: getstatic 382	org/telegram/ui/Cells/ChatMessageCell:forwardNamePaint	Landroid/text/TextPaint;
    //   1208: aload_0
    //   1209: getfield 1957	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1212: fconst_2
    //   1213: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1216: iadd
    //   1217: getstatic 1085	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1220: fconst_1
    //   1221: fconst_0
    //   1222: iconst_0
    //   1223: invokespecial 1088	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   1226: aastore
    //   1227: ldc_w 1970
    //   1230: ldc_w 1971
    //   1233: invokestatic 1166	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1236: invokestatic 1916	org/telegram/messenger/AndroidUtilities:replaceTags	(Ljava/lang/String;)Landroid/text/SpannableStringBuilder;
    //   1239: getstatic 382	org/telegram/ui/Cells/ChatMessageCell:forwardNamePaint	Landroid/text/TextPaint;
    //   1242: aload_0
    //   1243: getfield 1957	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1246: i2f
    //   1247: getstatic 1075	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   1250: invokestatic 1079	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   1253: astore 5
    //   1255: aload_0
    //   1256: getfield 290	org/telegram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   1259: iconst_0
    //   1260: new 288	android/text/StaticLayout
    //   1263: dup
    //   1264: aload 5
    //   1266: getstatic 382	org/telegram/ui/Cells/ChatMessageCell:forwardNamePaint	Landroid/text/TextPaint;
    //   1269: aload_0
    //   1270: getfield 1957	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1273: fconst_2
    //   1274: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1277: iadd
    //   1278: getstatic 1085	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1281: fconst_1
    //   1282: fconst_0
    //   1283: iconst_0
    //   1284: invokespecial 1088	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   1287: aastore
    //   1288: aload_0
    //   1289: aload_0
    //   1290: getfield 290	org/telegram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   1293: iconst_0
    //   1294: aaload
    //   1295: iconst_0
    //   1296: invokevirtual 654	android/text/StaticLayout:getLineWidth	(I)F
    //   1299: f2d
    //   1300: invokestatic 987	java/lang/Math:ceil	(D)D
    //   1303: d2i
    //   1304: aload_0
    //   1305: getfield 290	org/telegram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   1308: iconst_1
    //   1309: aaload
    //   1310: iconst_0
    //   1311: invokevirtual 654	android/text/StaticLayout:getLineWidth	(I)F
    //   1314: f2d
    //   1315: invokestatic 987	java/lang/Math:ceil	(D)D
    //   1318: d2i
    //   1319: invokestatic 490	java/lang/Math:max	(II)I
    //   1322: putfield 1957	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1325: aload_0
    //   1326: getfield 292	org/telegram/ui/Cells/ChatMessageCell:forwardNameOffsetX	[F
    //   1329: iconst_0
    //   1330: aload_0
    //   1331: getfield 290	org/telegram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   1334: iconst_0
    //   1335: aaload
    //   1336: iconst_0
    //   1337: invokevirtual 651	android/text/StaticLayout:getLineLeft	(I)F
    //   1340: fastore
    //   1341: aload_0
    //   1342: getfield 292	org/telegram/ui/Cells/ChatMessageCell:forwardNameOffsetX	[F
    //   1345: iconst_1
    //   1346: aload_0
    //   1347: getfield 290	org/telegram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   1350: iconst_1
    //   1351: aaload
    //   1352: iconst_0
    //   1353: invokevirtual 651	android/text/StaticLayout:getLineLeft	(I)F
    //   1356: fastore
    //   1357: aload_0
    //   1358: aload_0
    //   1359: getfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1362: ldc_w 543
    //   1365: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1368: iadd
    //   1369: putfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1372: aload_1
    //   1373: invokevirtual 1974	org/telegram/messenger/MessageObject:isReply	()Z
    //   1376: ifeq +523 -> 1899
    //   1379: aload_0
    //   1380: aload_0
    //   1381: getfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1384: ldc_w 1672
    //   1387: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1390: iadd
    //   1391: putfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1394: aload_1
    //   1395: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   1398: ifeq +27 -> 1425
    //   1401: aload_1
    //   1402: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   1405: bipush 13
    //   1407: if_icmpne +1093 -> 2500
    //   1410: aload_0
    //   1411: aload_0
    //   1412: getfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1415: ldc_w 1672
    //   1418: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1421: isub
    //   1422: putfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1425: aload_0
    //   1426: invokespecial 1924	org/telegram/ui/Cells/ChatMessageCell:getMaxNameWidth	()I
    //   1429: istore_3
    //   1430: iload_3
    //   1431: istore_2
    //   1432: aload_1
    //   1433: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   1436: bipush 13
    //   1438: if_icmpeq +12 -> 1450
    //   1441: iload_3
    //   1442: ldc_w 581
    //   1445: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1448: isub
    //   1449: istore_2
    //   1450: aconst_null
    //   1451: astore 8
    //   1453: aload_1
    //   1454: getfield 1776	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1457: ifnull +1361 -> 2818
    //   1460: aload_1
    //   1461: getfield 1776	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1464: getfield 1977	org/telegram/messenger/MessageObject:photoThumbs2	Ljava/util/ArrayList;
    //   1467: bipush 80
    //   1469: invokestatic 1205	org/telegram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;I)Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   1472: astore 6
    //   1474: aload 6
    //   1476: astore 5
    //   1478: aload 6
    //   1480: ifnonnull +17 -> 1497
    //   1483: aload_1
    //   1484: getfield 1776	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1487: getfield 1198	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   1490: bipush 80
    //   1492: invokestatic 1205	org/telegram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;I)Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   1495: astore 5
    //   1497: aload 5
    //   1499: ifnull +40 -> 1539
    //   1502: aload_1
    //   1503: getfield 1776	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1506: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   1509: bipush 13
    //   1511: if_icmpeq +28 -> 1539
    //   1514: aload_1
    //   1515: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   1518: bipush 13
    //   1520: if_icmpne +9 -> 1529
    //   1523: invokestatic 1666	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   1526: ifeq +13 -> 1539
    //   1529: aload_1
    //   1530: getfield 1776	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1533: invokevirtual 1980	org/telegram/messenger/MessageObject:isSecretMedia	()Z
    //   1536: ifeq +982 -> 2518
    //   1539: aload_0
    //   1540: getfield 426	org/telegram/ui/Cells/ChatMessageCell:replyImageReceiver	Lorg/telegram/messenger/ImageReceiver;
    //   1543: aconst_null
    //   1544: checkcast 1323	android/graphics/drawable/Drawable
    //   1547: invokevirtual 1230	org/telegram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   1550: aload_0
    //   1551: iconst_0
    //   1552: putfield 1982	org/telegram/ui/Cells/ChatMessageCell:needReplyImage	Z
    //   1555: aconst_null
    //   1556: astore 5
    //   1558: aload_1
    //   1559: getfield 1776	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1562: invokevirtual 857	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   1565: ifeq +997 -> 2562
    //   1568: invokestatic 868	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   1571: aload_1
    //   1572: getfield 1776	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1575: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1578: getfield 871	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   1581: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1584: invokevirtual 881	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   1587: astore 6
    //   1589: aload 6
    //   1591: ifnull +10 -> 1601
    //   1594: aload 6
    //   1596: invokestatic 1797	org/telegram/messenger/UserObject:getUserName	(Lorg/telegram/tgnet/TLRPC$User;)Ljava/lang/String;
    //   1599: astore 5
    //   1601: aload 5
    //   1603: ifnull +1209 -> 2812
    //   1606: aload 5
    //   1608: bipush 10
    //   1610: bipush 32
    //   1612: invokevirtual 1069	java/lang/String:replace	(CC)Ljava/lang/String;
    //   1615: getstatic 384	org/telegram/ui/Cells/ChatMessageCell:replyNamePaint	Landroid/text/TextPaint;
    //   1618: iload_2
    //   1619: i2f
    //   1620: getstatic 1075	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   1623: invokestatic 1079	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   1626: astore 5
    //   1628: aload_1
    //   1629: getfield 1776	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1632: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1635: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1638: instanceof 1984
    //   1641: ifeq +1010 -> 2651
    //   1644: aload_1
    //   1645: getfield 1776	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1648: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1651: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1654: getfield 1988	org/telegram/tgnet/TLRPC$MessageMedia:game	Lorg/telegram/tgnet/TLRPC$TL_game;
    //   1657: getfield 1991	org/telegram/tgnet/TLRPC$TL_game:title	Ljava/lang/String;
    //   1660: getstatic 386	org/telegram/ui/Cells/ChatMessageCell:replyTextPaint	Landroid/text/TextPaint;
    //   1663: invokevirtual 1995	android/text/TextPaint:getFontMetricsInt	()Landroid/graphics/Paint$FontMetricsInt;
    //   1666: ldc_w 408
    //   1669: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1672: iconst_0
    //   1673: invokestatic 2001	org/telegram/messenger/Emoji:replaceEmoji	(Ljava/lang/CharSequence;Landroid/graphics/Paint$FontMetricsInt;IZ)Ljava/lang/CharSequence;
    //   1676: getstatic 386	org/telegram/ui/Cells/ChatMessageCell:replyTextPaint	Landroid/text/TextPaint;
    //   1679: iload_2
    //   1680: i2f
    //   1681: getstatic 1075	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   1684: invokestatic 1079	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   1687: astore 6
    //   1689: iload_2
    //   1690: istore_3
    //   1691: aload 5
    //   1693: astore 7
    //   1695: aload 7
    //   1697: astore_1
    //   1698: aload 7
    //   1700: ifnonnull +13 -> 1713
    //   1703: ldc_w 2003
    //   1706: ldc_w 2004
    //   1709: invokestatic 1166	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1712: astore_1
    //   1713: aload_0
    //   1714: new 288	android/text/StaticLayout
    //   1717: dup
    //   1718: aload_1
    //   1719: getstatic 384	org/telegram/ui/Cells/ChatMessageCell:replyNamePaint	Landroid/text/TextPaint;
    //   1722: iload_3
    //   1723: ldc_w 1399
    //   1726: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1729: iadd
    //   1730: getstatic 1085	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1733: fconst_1
    //   1734: fconst_0
    //   1735: iconst_0
    //   1736: invokespecial 1088	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   1739: putfield 2006	org/telegram/ui/Cells/ChatMessageCell:replyNameLayout	Landroid/text/StaticLayout;
    //   1742: aload_0
    //   1743: getfield 2006	org/telegram/ui/Cells/ChatMessageCell:replyNameLayout	Landroid/text/StaticLayout;
    //   1746: invokevirtual 1093	android/text/StaticLayout:getLineCount	()I
    //   1749: ifle +55 -> 1804
    //   1752: aload_0
    //   1753: getfield 2006	org/telegram/ui/Cells/ChatMessageCell:replyNameLayout	Landroid/text/StaticLayout;
    //   1756: iconst_0
    //   1757: invokevirtual 654	android/text/StaticLayout:getLineWidth	(I)F
    //   1760: f2d
    //   1761: invokestatic 987	java/lang/Math:ceil	(D)D
    //   1764: d2i
    //   1765: istore 4
    //   1767: aload_0
    //   1768: getfield 1982	org/telegram/ui/Cells/ChatMessageCell:needReplyImage	Z
    //   1771: ifeq +1009 -> 2780
    //   1774: bipush 44
    //   1776: istore_2
    //   1777: aload_0
    //   1778: iload_2
    //   1779: bipush 12
    //   1781: iadd
    //   1782: i2f
    //   1783: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1786: iload 4
    //   1788: iadd
    //   1789: putfield 2008	org/telegram/ui/Cells/ChatMessageCell:replyNameWidth	I
    //   1792: aload_0
    //   1793: aload_0
    //   1794: getfield 2006	org/telegram/ui/Cells/ChatMessageCell:replyNameLayout	Landroid/text/StaticLayout;
    //   1797: iconst_0
    //   1798: invokevirtual 651	android/text/StaticLayout:getLineLeft	(I)F
    //   1801: putfield 2010	org/telegram/ui/Cells/ChatMessageCell:replyNameOffset	F
    //   1804: aload 6
    //   1806: ifnull +93 -> 1899
    //   1809: aload_0
    //   1810: new 288	android/text/StaticLayout
    //   1813: dup
    //   1814: aload 6
    //   1816: getstatic 386	org/telegram/ui/Cells/ChatMessageCell:replyTextPaint	Landroid/text/TextPaint;
    //   1819: iload_3
    //   1820: ldc_w 1399
    //   1823: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1826: iadd
    //   1827: getstatic 1085	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1830: fconst_1
    //   1831: fconst_0
    //   1832: iconst_0
    //   1833: invokespecial 1088	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   1836: putfield 1773	org/telegram/ui/Cells/ChatMessageCell:replyTextLayout	Landroid/text/StaticLayout;
    //   1839: aload_0
    //   1840: getfield 1773	org/telegram/ui/Cells/ChatMessageCell:replyTextLayout	Landroid/text/StaticLayout;
    //   1843: invokevirtual 1093	android/text/StaticLayout:getLineCount	()I
    //   1846: ifle +53 -> 1899
    //   1849: aload_0
    //   1850: getfield 1773	org/telegram/ui/Cells/ChatMessageCell:replyTextLayout	Landroid/text/StaticLayout;
    //   1853: iconst_0
    //   1854: invokevirtual 654	android/text/StaticLayout:getLineWidth	(I)F
    //   1857: f2d
    //   1858: invokestatic 987	java/lang/Math:ceil	(D)D
    //   1861: d2i
    //   1862: istore_3
    //   1863: aload_0
    //   1864: getfield 1982	org/telegram/ui/Cells/ChatMessageCell:needReplyImage	Z
    //   1867: ifeq +929 -> 2796
    //   1870: bipush 44
    //   1872: istore_2
    //   1873: aload_0
    //   1874: iload_2
    //   1875: bipush 12
    //   1877: iadd
    //   1878: i2f
    //   1879: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1882: iload_3
    //   1883: iadd
    //   1884: putfield 2012	org/telegram/ui/Cells/ChatMessageCell:replyTextWidth	I
    //   1887: aload_0
    //   1888: aload_0
    //   1889: getfield 1773	org/telegram/ui/Cells/ChatMessageCell:replyTextLayout	Landroid/text/StaticLayout;
    //   1892: iconst_0
    //   1893: invokevirtual 651	android/text/StaticLayout:getLineLeft	(I)F
    //   1896: putfield 2014	org/telegram/ui/Cells/ChatMessageCell:replyTextOffset	F
    //   1899: aload_0
    //   1900: invokevirtual 2017	org/telegram/ui/Cells/ChatMessageCell:requestLayout	()V
    //   1903: return
    //   1904: aload_0
    //   1905: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1908: getfield 2020	org/telegram/messenger/MessageObject:viewsReloaded	Z
    //   1911: ifne -1856 -> 55
    //   1914: invokestatic 868	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   1917: aload_0
    //   1918: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1921: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1924: iconst_1
    //   1925: invokevirtual 1895	org/telegram/messenger/MessagesController:addToViewsQueue	(Lorg/telegram/tgnet/TLRPC$Message;Z)V
    //   1928: aload_0
    //   1929: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1932: iconst_1
    //   1933: putfield 2020	org/telegram/messenger/MessageObject:viewsReloaded	Z
    //   1936: goto -1881 -> 55
    //   1939: aload_0
    //   1940: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1943: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1946: getfield 871	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   1949: ifge +30 -> 1979
    //   1952: aload_0
    //   1953: invokestatic 868	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   1956: aload_0
    //   1957: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1960: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1963: getfield 871	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   1966: ineg
    //   1967: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1970: invokevirtual 900	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   1973: putfield 1748	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   1976: goto -1888 -> 88
    //   1979: aload_0
    //   1980: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1983: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1986: getfield 917	org/telegram/tgnet/TLRPC$Message:post	Z
    //   1989: ifeq -1901 -> 88
    //   1992: aload_0
    //   1993: invokestatic 868	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   1996: aload_0
    //   1997: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2000: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2003: getfield 893	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   2006: getfield 896	org/telegram/tgnet/TLRPC$Peer:channel_id	I
    //   2009: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2012: invokevirtual 900	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   2015: putfield 1748	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2018: goto -1930 -> 88
    //   2021: aload_0
    //   2022: aconst_null
    //   2023: putfield 1778	org/telegram/ui/Cells/ChatMessageCell:currentPhoto	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2026: goto -1881 -> 145
    //   2029: aload_0
    //   2030: getfield 1748	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2033: ifnull +49 -> 2082
    //   2036: aload_0
    //   2037: getfield 1748	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2040: getfield 1809	org/telegram/tgnet/TLRPC$Chat:photo	Lorg/telegram/tgnet/TLRPC$ChatPhoto;
    //   2043: ifnull +31 -> 2074
    //   2046: aload_0
    //   2047: aload_0
    //   2048: getfield 1748	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2051: getfield 1809	org/telegram/tgnet/TLRPC$Chat:photo	Lorg/telegram/tgnet/TLRPC$ChatPhoto;
    //   2054: getfield 1812	org/telegram/tgnet/TLRPC$ChatPhoto:photo_small	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2057: putfield 1778	org/telegram/ui/Cells/ChatMessageCell:currentPhoto	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2060: aload_0
    //   2061: getfield 424	org/telegram/ui/Cells/ChatMessageCell:avatarDrawable	Lorg/telegram/ui/Components/AvatarDrawable;
    //   2064: aload_0
    //   2065: getfield 1748	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2068: invokevirtual 2023	org/telegram/ui/Components/AvatarDrawable:setInfo	(Lorg/telegram/tgnet/TLRPC$Chat;)V
    //   2071: goto -1915 -> 156
    //   2074: aload_0
    //   2075: aconst_null
    //   2076: putfield 1778	org/telegram/ui/Cells/ChatMessageCell:currentPhoto	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2079: goto -19 -> 2060
    //   2082: aload_0
    //   2083: aconst_null
    //   2084: putfield 1778	org/telegram/ui/Cells/ChatMessageCell:currentPhoto	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2087: aload_0
    //   2088: getfield 424	org/telegram/ui/Cells/ChatMessageCell:avatarDrawable	Lorg/telegram/ui/Components/AvatarDrawable;
    //   2091: aload_1
    //   2092: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2095: getfield 871	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   2098: aconst_null
    //   2099: aconst_null
    //   2100: iconst_0
    //   2101: invokevirtual 2026	org/telegram/ui/Components/AvatarDrawable:setInfo	(ILjava/lang/String;Ljava/lang/String;Z)V
    //   2104: goto -1948 -> 156
    //   2107: aload 8
    //   2109: astore 5
    //   2111: aload 7
    //   2113: astore 6
    //   2115: aload_1
    //   2116: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2119: getfield 1818	org/telegram/tgnet/TLRPC$Message:via_bot_name	Ljava/lang/String;
    //   2122: ifnull -1777 -> 345
    //   2125: aload 8
    //   2127: astore 5
    //   2129: aload 7
    //   2131: astore 6
    //   2133: aload_1
    //   2134: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2137: getfield 1818	org/telegram/tgnet/TLRPC$Message:via_bot_name	Ljava/lang/String;
    //   2140: invokevirtual 910	java/lang/String:length	()I
    //   2143: ifle -1798 -> 345
    //   2146: new 1182	java/lang/StringBuilder
    //   2149: dup
    //   2150: invokespecial 1183	java/lang/StringBuilder:<init>	()V
    //   2153: ldc_w 1910
    //   2156: invokevirtual 1187	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2159: aload_1
    //   2160: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2163: getfield 1818	org/telegram/tgnet/TLRPC$Message:via_bot_name	Ljava/lang/String;
    //   2166: invokevirtual 1187	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2169: invokevirtual 1195	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2172: astore 6
    //   2174: ldc_w 1912
    //   2177: iconst_1
    //   2178: anewarray 1106	java/lang/Object
    //   2181: dup
    //   2182: iconst_0
    //   2183: aload 6
    //   2185: aastore
    //   2186: invokestatic 1110	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   2189: invokestatic 1916	org/telegram/messenger/AndroidUtilities:replaceTags	(Ljava/lang/String;)Landroid/text/SpannableStringBuilder;
    //   2192: astore 5
    //   2194: aload_0
    //   2195: getstatic 384	org/telegram/ui/Cells/ChatMessageCell:replyNamePaint	Landroid/text/TextPaint;
    //   2198: aload 5
    //   2200: iconst_0
    //   2201: aload 5
    //   2203: invokeinterface 1000 1 0
    //   2208: invokevirtual 1873	android/text/TextPaint:measureText	(Ljava/lang/CharSequence;II)F
    //   2211: f2d
    //   2212: invokestatic 987	java/lang/Math:ceil	(D)D
    //   2215: d2i
    //   2216: putfield 1918	org/telegram/ui/Cells/ChatMessageCell:viaWidth	I
    //   2219: goto -1874 -> 345
    //   2222: iconst_0
    //   2223: istore_3
    //   2224: goto -1853 -> 371
    //   2227: iconst_0
    //   2228: istore_2
    //   2229: goto -1832 -> 397
    //   2232: aload_0
    //   2233: getfield 1748	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2236: ifnull +17 -> 2253
    //   2239: aload_0
    //   2240: aload_0
    //   2241: getfield 1748	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2244: getfield 1815	org/telegram/tgnet/TLRPC$Chat:title	Ljava/lang/String;
    //   2247: putfield 1799	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   2250: goto -1793 -> 457
    //   2253: aload_0
    //   2254: ldc_w 2028
    //   2257: putfield 1799	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   2260: goto -1803 -> 457
    //   2263: aload_0
    //   2264: ldc_w 2030
    //   2267: putfield 1799	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   2270: goto -1813 -> 457
    //   2273: iconst_0
    //   2274: istore_3
    //   2275: goto -1785 -> 490
    //   2278: aload_0
    //   2279: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2282: invokevirtual 575	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   2285: ifeq +10 -> 2295
    //   2288: ldc_w 1349
    //   2291: istore_2
    //   2292: goto -1716 -> 576
    //   2295: ldc_w 1382
    //   2298: istore_2
    //   2299: goto -7 -> 2292
    //   2302: new 1626	android/text/SpannableStringBuilder
    //   2305: dup
    //   2306: ldc_w 2032
    //   2309: iconst_1
    //   2310: anewarray 1106	java/lang/Object
    //   2313: dup
    //   2314: iconst_0
    //   2315: aload 6
    //   2317: aastore
    //   2318: invokestatic 1110	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   2321: invokespecial 1629	android/text/SpannableStringBuilder:<init>	(Ljava/lang/CharSequence;)V
    //   2324: astore 7
    //   2326: aload 7
    //   2328: new 1933	org/telegram/ui/Components/TypefaceSpan
    //   2331: dup
    //   2332: getstatic 1939	android/graphics/Typeface:DEFAULT	Landroid/graphics/Typeface;
    //   2335: iconst_0
    //   2336: iload_2
    //   2337: invokespecial 1942	org/telegram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;II)V
    //   2340: iconst_0
    //   2341: iconst_4
    //   2342: bipush 33
    //   2344: invokevirtual 1946	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   2347: aload 7
    //   2349: new 1933	org/telegram/ui/Components/TypefaceSpan
    //   2352: dup
    //   2353: ldc_w 305
    //   2356: invokestatic 311	org/telegram/messenger/AndroidUtilities:getTypeface	(Ljava/lang/String;)Landroid/graphics/Typeface;
    //   2359: iconst_0
    //   2360: iload_2
    //   2361: invokespecial 1942	org/telegram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;II)V
    //   2364: iconst_4
    //   2365: aload 7
    //   2367: invokevirtual 1947	android/text/SpannableStringBuilder:length	()I
    //   2370: bipush 33
    //   2372: invokevirtual 1946	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   2375: goto -1687 -> 688
    //   2378: aload_0
    //   2379: iconst_0
    //   2380: putfield 1926	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   2383: goto -1576 -> 807
    //   2386: astore 7
    //   2388: ldc_w 695
    //   2391: aload 7
    //   2393: invokestatic 701	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2396: goto -1589 -> 807
    //   2399: aload_0
    //   2400: aconst_null
    //   2401: putfield 1799	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   2404: aload_0
    //   2405: aconst_null
    //   2406: putfield 1949	org/telegram/ui/Cells/ChatMessageCell:nameLayout	Landroid/text/StaticLayout;
    //   2409: aload_0
    //   2410: iconst_0
    //   2411: putfield 1926	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   2414: goto -1592 -> 822
    //   2417: aload_0
    //   2418: aload_0
    //   2419: getfield 1955	org/telegram/ui/Cells/ChatMessageCell:currentForwardChannel	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2422: getfield 1815	org/telegram/tgnet/TLRPC$Chat:title	Ljava/lang/String;
    //   2425: putfield 1806	org/telegram/ui/Cells/ChatMessageCell:currentForwardNameString	Ljava/lang/String;
    //   2428: goto -1424 -> 1004
    //   2431: aload_0
    //   2432: getfield 1953	org/telegram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/telegram/tgnet/TLRPC$User;
    //   2435: ifnull -1431 -> 1004
    //   2438: aload_0
    //   2439: aload_0
    //   2440: getfield 1953	org/telegram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/telegram/tgnet/TLRPC$User;
    //   2443: invokestatic 1797	org/telegram/messenger/UserObject:getUserName	(Lorg/telegram/tgnet/TLRPC$User;)Ljava/lang/String;
    //   2446: putfield 1806	org/telegram/ui/Cells/ChatMessageCell:currentForwardNameString	Ljava/lang/String;
    //   2449: goto -1445 -> 1004
    //   2452: ldc_w 2034
    //   2455: iconst_2
    //   2456: anewarray 1106	java/lang/Object
    //   2459: dup
    //   2460: iconst_0
    //   2461: ldc_w 1965
    //   2464: ldc_w 1966
    //   2467: invokestatic 1166	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   2470: aastore
    //   2471: dup
    //   2472: iconst_1
    //   2473: aload 7
    //   2475: aastore
    //   2476: invokestatic 1110	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   2479: invokestatic 1916	org/telegram/messenger/AndroidUtilities:replaceTags	(Ljava/lang/String;)Landroid/text/SpannableStringBuilder;
    //   2482: astore 5
    //   2484: goto -1308 -> 1176
    //   2487: astore 5
    //   2489: ldc_w 695
    //   2492: aload 5
    //   2494: invokestatic 701	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2497: goto -1125 -> 1372
    //   2500: aload_0
    //   2501: aload_0
    //   2502: getfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   2505: ldc_w 1486
    //   2508: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2511: iadd
    //   2512: putfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   2515: goto -1090 -> 1425
    //   2518: aload_0
    //   2519: aload 5
    //   2521: getfield 1149	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2524: putfield 1789	org/telegram/ui/Cells/ChatMessageCell:currentReplyPhoto	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2527: aload_0
    //   2528: getfield 426	org/telegram/ui/Cells/ChatMessageCell:replyImageReceiver	Lorg/telegram/messenger/ImageReceiver;
    //   2531: aload 5
    //   2533: getfield 1149	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2536: ldc_w 1904
    //   2539: aconst_null
    //   2540: aconst_null
    //   2541: iconst_1
    //   2542: invokevirtual 1907	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Ljava/lang/String;Z)V
    //   2545: aload_0
    //   2546: iconst_1
    //   2547: putfield 1982	org/telegram/ui/Cells/ChatMessageCell:needReplyImage	Z
    //   2550: iload_2
    //   2551: ldc_w 1513
    //   2554: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2557: isub
    //   2558: istore_2
    //   2559: goto -1004 -> 1555
    //   2562: aload_1
    //   2563: getfield 1776	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2566: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2569: getfield 871	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   2572: ifge +40 -> 2612
    //   2575: invokestatic 868	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   2578: aload_1
    //   2579: getfield 1776	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2582: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2585: getfield 871	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   2588: ineg
    //   2589: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2592: invokevirtual 900	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   2595: astore 6
    //   2597: aload 6
    //   2599: ifnull -998 -> 1601
    //   2602: aload 6
    //   2604: getfield 1815	org/telegram/tgnet/TLRPC$Chat:title	Ljava/lang/String;
    //   2607: astore 5
    //   2609: goto -1008 -> 1601
    //   2612: invokestatic 868	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   2615: aload_1
    //   2616: getfield 1776	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2619: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2622: getfield 893	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   2625: getfield 896	org/telegram/tgnet/TLRPC$Peer:channel_id	I
    //   2628: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2631: invokevirtual 900	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   2634: astore 6
    //   2636: aload 6
    //   2638: ifnull -1037 -> 1601
    //   2641: aload 6
    //   2643: getfield 1815	org/telegram/tgnet/TLRPC$Chat:title	Ljava/lang/String;
    //   2646: astore 5
    //   2648: goto -1047 -> 1601
    //   2651: aload 5
    //   2653: astore 7
    //   2655: iload_2
    //   2656: istore_3
    //   2657: aload 8
    //   2659: astore 6
    //   2661: aload_1
    //   2662: getfield 1776	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2665: getfield 972	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   2668: ifnull -973 -> 1695
    //   2671: aload 5
    //   2673: astore 7
    //   2675: iload_2
    //   2676: istore_3
    //   2677: aload 8
    //   2679: astore 6
    //   2681: aload_1
    //   2682: getfield 1776	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2685: getfield 972	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   2688: invokeinterface 1000 1 0
    //   2693: ifle -998 -> 1695
    //   2696: aload_1
    //   2697: getfield 1776	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2700: getfield 972	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   2703: invokeinterface 2035 1 0
    //   2708: astore 6
    //   2710: aload 6
    //   2712: astore_1
    //   2713: aload 6
    //   2715: invokevirtual 910	java/lang/String:length	()I
    //   2718: sipush 150
    //   2721: if_icmple +13 -> 2734
    //   2724: aload 6
    //   2726: iconst_0
    //   2727: sipush 150
    //   2730: invokevirtual 2039	java/lang/String:substring	(II)Ljava/lang/String;
    //   2733: astore_1
    //   2734: aload_1
    //   2735: bipush 10
    //   2737: bipush 32
    //   2739: invokevirtual 1069	java/lang/String:replace	(CC)Ljava/lang/String;
    //   2742: getstatic 386	org/telegram/ui/Cells/ChatMessageCell:replyTextPaint	Landroid/text/TextPaint;
    //   2745: invokevirtual 1995	android/text/TextPaint:getFontMetricsInt	()Landroid/graphics/Paint$FontMetricsInt;
    //   2748: ldc_w 408
    //   2751: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2754: iconst_0
    //   2755: invokestatic 2001	org/telegram/messenger/Emoji:replaceEmoji	(Ljava/lang/CharSequence;Landroid/graphics/Paint$FontMetricsInt;IZ)Ljava/lang/CharSequence;
    //   2758: getstatic 386	org/telegram/ui/Cells/ChatMessageCell:replyTextPaint	Landroid/text/TextPaint;
    //   2761: iload_2
    //   2762: i2f
    //   2763: getstatic 1075	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   2766: invokestatic 1079	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   2769: astore 6
    //   2771: aload 5
    //   2773: astore 7
    //   2775: iload_2
    //   2776: istore_3
    //   2777: goto -1082 -> 1695
    //   2780: iconst_0
    //   2781: istore_2
    //   2782: goto -1005 -> 1777
    //   2785: astore_1
    //   2786: ldc_w 695
    //   2789: aload_1
    //   2790: invokestatic 701	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2793: goto -989 -> 1804
    //   2796: iconst_0
    //   2797: istore_2
    //   2798: goto -925 -> 1873
    //   2801: astore_1
    //   2802: ldc_w 695
    //   2805: aload_1
    //   2806: invokestatic 701	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2809: goto -910 -> 1899
    //   2812: aconst_null
    //   2813: astore 5
    //   2815: goto -1187 -> 1628
    //   2818: aconst_null
    //   2819: astore 7
    //   2821: iload_2
    //   2822: istore_3
    //   2823: aload 8
    //   2825: astore 6
    //   2827: goto -1132 -> 1695
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	2830	0	this	ChatMessageCell
    //   0	2830	1	paramMessageObject	MessageObject
    //   396	2426	2	i	int
    //   370	2453	3	j	int
    //   479	1310	4	k	int
    //   222	2261	5	localObject1	Object
    //   2487	45	5	localException1	Exception
    //   2607	207	5	str	String
    //   226	2600	6	localObject2	Object
    //   187	2179	7	localObject3	Object
    //   2386	88	7	localException2	Exception
    //   2653	167	7	localObject4	Object
    //   190	2634	8	localObject5	Object
    //   218	123	9	localUser	TLRPC.User
    // Exception table:
    //   from	to	target	type
    //   706	795	2386	java/lang/Exception
    //   795	807	2386	java/lang/Exception
    //   2378	2383	2386	java/lang/Exception
    //   1194	1372	2487	java/lang/Exception
    //   1713	1774	2785	java/lang/Exception
    //   1777	1804	2785	java/lang/Exception
    //   1809	1870	2801	java/lang/Exception
    //   1873	1899	2801	java/lang/Exception
  }
  
  private void updateSecretTimeText(MessageObject paramMessageObject)
  {
    if ((paramMessageObject == null) || (paramMessageObject.isOut())) {}
    do
    {
      return;
      paramMessageObject = paramMessageObject.getSecretTimeString();
    } while (paramMessageObject == null);
    this.infoWidth = ((int)Math.ceil(infoPaint.measureText(paramMessageObject)));
    this.infoLayout = new StaticLayout(TextUtils.ellipsize(paramMessageObject, infoPaint, this.infoWidth, TextUtils.TruncateAt.END), infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
    invalidate();
  }
  
  private void updateWaveform()
  {
    if ((this.currentMessageObject == null) || (this.documentAttachType != 3)) {}
    for (;;)
    {
      return;
      int i = 0;
      while (i < this.documentAttach.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)this.documentAttach.attributes.get(i);
        if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeAudio))
        {
          if ((localDocumentAttribute.waveform == null) || (localDocumentAttribute.waveform.length == 0)) {
            MediaController.getInstance().generateWaveform(this.currentMessageObject);
          }
          if (localDocumentAttribute.waveform != null) {}
          for (boolean bool = true;; bool = false)
          {
            this.useSeekBarWaweform = bool;
            this.seekBarWaveform.setWaveform(localDocumentAttribute.waveform);
            return;
          }
        }
        i += 1;
      }
    }
  }
  
  public void didSetImage(ImageReceiver paramImageReceiver, boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((this.currentMessageObject != null) && (paramBoolean1) && (!paramBoolean2) && (!this.currentMessageObject.mediaExists) && (!this.currentMessageObject.attachPathExists))
    {
      this.currentMessageObject.mediaExists = true;
      updateButtonState(true);
    }
  }
  
  public void downloadAudioIfNeed()
  {
    if ((this.documentAttachType != 3) || (this.documentAttach.size >= 1048576)) {}
    while (this.buttonState != 2) {
      return;
    }
    FileLoader.getInstance().loadFile(this.documentAttach, true, false);
    this.buttonState = 4;
    this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
  }
  
  public MessageObject getMessageObject()
  {
    return this.currentMessageObject;
  }
  
  public int getObserverTag()
  {
    return this.TAG;
  }
  
  public ImageReceiver getPhotoImage()
  {
    return this.photoImage;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.avatarImage.onAttachedToWindow();
    this.replyImageReceiver.onAttachedToWindow();
    if (this.drawPhotoImage)
    {
      if (this.photoImage.onAttachedToWindow()) {
        updateButtonState(false);
      }
      return;
    }
    updateButtonState(false);
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.avatarImage.onDetachedFromWindow();
    this.replyImageReceiver.onDetachedFromWindow();
    this.photoImage.onDetachedFromWindow();
    MediaController.getInstance().removeLoadingFileObserver(this);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.currentMessageObject == null) {}
    label81:
    label109:
    label147:
    label222:
    label267:
    label409:
    label560:
    label754:
    label764:
    label791:
    label802:
    label813:
    label834:
    label867:
    label970:
    label977:
    label988:
    label1009:
    label1075:
    label1086:
    label1094:
    label1118:
    label1143:
    label1187:
    label1254:
    label1293:
    label1299:
    label1363:
    label1426:
    label1487:
    label1693:
    label1762:
    label1842:
    label1890:
    label2038:
    label2087:
    label2127:
    label2149:
    label2211:
    label2346:
    label2370:
    label2376:
    label2476:
    label2496:
    label2694:
    label2718:
    label2724:
    label2730:
    label2736:
    label2750:
    label2756:
    label3023:
    label3124:
    label3126:
    label3132:
    label3206:
    label3231:
    label3278:
    label3371:
    label3417:
    label3423:
    label3489:
    label3542:
    label3595:
    label3662:
    label3727:
    label3773:
    label3829:
    label3877:
    label3928:
    for (;;)
    {
      return;
      if (!this.wasLayout)
      {
        requestLayout();
        return;
      }
      if (this.isAvatarVisible) {
        this.avatarImage.draw(paramCanvas);
      }
      Object localObject;
      int j;
      int k;
      int i;
      int m;
      if (this.mediaBackground)
      {
        timePaint.setColor(-1);
        if (!this.currentMessageObject.isOutOwner()) {
          break label813;
        }
        if (!isDrawSelectedBackground()) {
          break label764;
        }
        if (this.mediaBackground) {
          break label754;
        }
        this.currentBackgroundDrawable = Theme.backgroundDrawableOutSelected;
        localObject = this.currentBackgroundDrawable;
        j = this.layoutWidth;
        k = this.backgroundWidth;
        if (this.mediaBackground) {
          break label791;
        }
        i = 0;
        j = j - k - i;
        this.backgroundDrawableLeft = j;
        k = AndroidUtilities.dp(1.0F);
        m = this.backgroundWidth;
        if (!this.mediaBackground) {
          break label802;
        }
        i = 0;
        setDrawableBounds((Drawable)localObject, j, k, m - i, this.layoutHeight - AndroidUtilities.dp(2.0F));
        if ((this.drawBackground) && (this.currentBackgroundDrawable != null)) {
          this.currentBackgroundDrawable.draw(paramCanvas);
        }
        drawContent(paramCanvas);
        if (this.drawShareButton)
        {
          Drawable localDrawable = Theme.shareDrawable;
          if (!this.sharePressed) {
            break label1086;
          }
          localObject = Theme.colorPressedFilter;
          localDrawable.setColorFilter((ColorFilter)localObject);
          if (!this.currentMessageObject.isOutOwner()) {
            break label1094;
          }
          this.shareStartX = (this.currentBackgroundDrawable.getBounds().left - AndroidUtilities.dp(8.0F) - Theme.shareDrawable.getIntrinsicWidth());
          localObject = Theme.shareDrawable;
          i = this.shareStartX;
          j = this.layoutHeight - AndroidUtilities.dp(41.0F);
          this.shareStartY = j;
          setDrawableBounds((Drawable)localObject, i, j);
          Theme.shareDrawable.draw(paramCanvas);
          setDrawableBounds(Theme.shareIconDrawable, this.shareStartX + AndroidUtilities.dp(9.0F), this.shareStartY + AndroidUtilities.dp(9.0F));
          Theme.shareIconDrawable.draw(paramCanvas);
        }
        if ((this.drawNameLayout) && (this.nameLayout != null))
        {
          paramCanvas.save();
          if (this.currentMessageObject.type != 13) {
            break label1143;
          }
          namePaint.setColor(-1);
          if (!this.currentMessageObject.isOutOwner()) {
            break label1118;
          }
          this.nameX = AndroidUtilities.dp(28.0F);
          this.nameY = (this.layoutHeight - AndroidUtilities.dp(38.0F));
          Theme.systemDrawable.setColorFilter(Theme.colorFilter);
          Theme.systemDrawable.setBounds((int)this.nameX - AndroidUtilities.dp(12.0F), (int)this.nameY - AndroidUtilities.dp(5.0F), (int)this.nameX + AndroidUtilities.dp(12.0F) + this.nameWidth, (int)this.nameY + AndroidUtilities.dp(22.0F));
          Theme.systemDrawable.draw(paramCanvas);
          paramCanvas.translate(this.nameX, this.nameY);
          this.nameLayout.draw(paramCanvas);
          paramCanvas.restore();
        }
        if ((!this.drawForwardedName) || (this.forwardedNameLayout[0] == null) || (this.forwardedNameLayout[1] == null)) {
          break label1363;
        }
        if (!this.drawNameLayout) {
          break label1293;
        }
        i = 19;
        this.forwardNameY = AndroidUtilities.dp(i + 10);
        if (!this.currentMessageObject.isOutOwner()) {
          break label1299;
        }
        forwardNamePaint.setColor(-11162801);
        this.forwardNameX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0F));
      }
      for (;;)
      {
        i = 0;
        while (i < 2)
        {
          paramCanvas.save();
          paramCanvas.translate(this.forwardNameX - this.forwardNameOffsetX[i], this.forwardNameY + AndroidUtilities.dp(16.0F) * i);
          this.forwardedNameLayout[i].draw(paramCanvas);
          paramCanvas.restore();
          i += 1;
        }
        if (this.currentMessageObject.isOutOwner())
        {
          localObject = timePaint;
          if (isDrawSelectedBackground()) {}
          for (;;)
          {
            ((TextPaint)localObject).setColor(-9391780);
            break;
          }
        }
        localObject = timePaint;
        if (isDrawSelectedBackground()) {}
        for (i = -7752511;; i = -6182221)
        {
          ((TextPaint)localObject).setColor(i);
          break;
        }
        this.currentBackgroundDrawable = Theme.backgroundMediaDrawableOutSelected;
        break label81;
        if (!this.mediaBackground)
        {
          this.currentBackgroundDrawable = Theme.backgroundDrawableOut;
          break label81;
        }
        this.currentBackgroundDrawable = Theme.backgroundMediaDrawableOut;
        break label81;
        i = AndroidUtilities.dp(9.0F);
        break label109;
        i = AndroidUtilities.dp(3.0F);
        break label147;
        if (isDrawSelectedBackground()) {
          if (!this.mediaBackground)
          {
            this.currentBackgroundDrawable = Theme.backgroundDrawableInSelected;
            if ((!this.isChat) || (!this.currentMessageObject.isFromUser())) {
              break label988;
            }
            localObject = this.currentBackgroundDrawable;
            if (this.mediaBackground) {
              break label970;
            }
            i = 3;
            j = AndroidUtilities.dp(i + 48);
            this.backgroundDrawableLeft = j;
            k = AndroidUtilities.dp(1.0F);
            m = this.backgroundWidth;
            if (!this.mediaBackground) {
              break label977;
            }
          }
        }
        for (i = 0;; i = AndroidUtilities.dp(3.0F))
        {
          setDrawableBounds((Drawable)localObject, j, k, m - i, this.layoutHeight - AndroidUtilities.dp(2.0F));
          break;
          this.currentBackgroundDrawable = Theme.backgroundMediaDrawableInSelected;
          break label834;
          if (!this.mediaBackground)
          {
            this.currentBackgroundDrawable = Theme.backgroundDrawableIn;
            break label834;
          }
          this.currentBackgroundDrawable = Theme.backgroundMediaDrawableIn;
          break label834;
          i = 9;
          break label867;
        }
        localObject = this.currentBackgroundDrawable;
        if (!this.mediaBackground)
        {
          i = AndroidUtilities.dp(3.0F);
          this.backgroundDrawableLeft = i;
          k = AndroidUtilities.dp(1.0F);
          m = this.backgroundWidth;
          if (!this.mediaBackground) {
            break label1075;
          }
        }
        for (j = 0;; j = AndroidUtilities.dp(3.0F))
        {
          setDrawableBounds((Drawable)localObject, i, k, m - j, this.layoutHeight - AndroidUtilities.dp(2.0F));
          break;
          i = AndroidUtilities.dp(9.0F);
          break label1009;
        }
        localObject = Theme.colorFilter;
        break label222;
        this.shareStartX = (this.currentBackgroundDrawable.getBounds().right + AndroidUtilities.dp(8.0F));
        break label267;
        this.nameX = (this.currentBackgroundDrawable.getBounds().right + AndroidUtilities.dp(22.0F));
        break label409;
        if ((this.mediaBackground) || (this.currentMessageObject.isOutOwner()))
        {
          this.nameX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0F) - this.nameOffsetX);
          if (this.currentUser == null) {
            break label1254;
          }
          namePaint.setColor(AvatarDrawable.getNameColorForId(this.currentUser.id));
        }
        for (;;)
        {
          this.nameY = AndroidUtilities.dp(10.0F);
          break;
          this.nameX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(17.0F) - this.nameOffsetX);
          break label1187;
          if (this.currentChat != null) {
            namePaint.setColor(AvatarDrawable.getNameColorForId(this.currentChat.id));
          } else {
            namePaint.setColor(AvatarDrawable.getNameColorForId(0));
          }
        }
        i = 0;
        break label560;
        forwardNamePaint.setColor(-13072697);
        if (this.mediaBackground) {
          this.forwardNameX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0F));
        } else {
          this.forwardNameX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(17.0F));
        }
      }
      if (this.currentMessageObject.isReply())
      {
        if (this.currentMessageObject.type != 13) {
          break label2376;
        }
        replyLinePaint.setColor(-1);
        replyNamePaint.setColor(-1);
        replyTextPaint.setColor(-1);
        if (!this.currentMessageObject.isOutOwner()) {
          break label2346;
        }
        this.replyStartX = AndroidUtilities.dp(23.0F);
        this.replyStartY = (this.layoutHeight - AndroidUtilities.dp(58.0F));
        if (this.nameLayout != null) {
          this.replyStartY -= AndroidUtilities.dp(31.0F);
        }
        j = Math.max(this.replyNameWidth, this.replyTextWidth);
        if (!this.needReplyImage) {
          break label2370;
        }
        i = 44;
        i = AndroidUtilities.dp(i + 14);
        Theme.systemDrawable.setColorFilter(Theme.colorFilter);
        Theme.systemDrawable.setBounds(this.replyStartX - AndroidUtilities.dp(7.0F), this.replyStartY - AndroidUtilities.dp(6.0F), this.replyStartX - AndroidUtilities.dp(7.0F) + (j + i), this.replyStartY + AndroidUtilities.dp(41.0F));
        Theme.systemDrawable.draw(paramCanvas);
        paramCanvas.drawRect(this.replyStartX, this.replyStartY, this.replyStartX + AndroidUtilities.dp(2.0F), this.replyStartY + AndroidUtilities.dp(35.0F), replyLinePaint);
        if (this.needReplyImage)
        {
          this.replyImageReceiver.setImageCoords(this.replyStartX + AndroidUtilities.dp(10.0F), this.replyStartY, AndroidUtilities.dp(35.0F), AndroidUtilities.dp(35.0F));
          this.replyImageReceiver.draw(paramCanvas);
        }
        float f1;
        float f2;
        if (this.replyNameLayout != null)
        {
          paramCanvas.save();
          f1 = this.replyStartX;
          f2 = this.replyNameOffset;
          if (!this.needReplyImage) {
            break label2730;
          }
          i = 44;
          paramCanvas.translate(AndroidUtilities.dp(i + 10) + (f1 - f2), this.replyStartY);
          this.replyNameLayout.draw(paramCanvas);
          paramCanvas.restore();
        }
        if (this.replyTextLayout != null)
        {
          paramCanvas.save();
          f1 = this.replyStartX;
          f2 = this.replyTextOffset;
          if (!this.needReplyImage) {
            break label2736;
          }
          i = 44;
          paramCanvas.translate(AndroidUtilities.dp(i + 10) + (f1 - f2), this.replyStartY + AndroidUtilities.dp(19.0F));
          this.replyTextLayout.draw(paramCanvas);
          paramCanvas.restore();
        }
      }
      if ((this.drawTime) || (!this.mediaBackground))
      {
        int n;
        if (this.mediaBackground) {
          if (this.currentMessageObject.type == 13)
          {
            localObject = Theme.timeStickerBackgroundDrawable;
            j = this.timeX;
            k = AndroidUtilities.dp(4.0F);
            m = this.layoutHeight;
            n = AndroidUtilities.dp(27.0F);
            int i1 = this.timeWidth;
            if (!this.currentMessageObject.isOutOwner()) {
              break label2750;
            }
            i = 20;
            setDrawableBounds((Drawable)localObject, j - k, m - n, i1 + AndroidUtilities.dp(i + 8), AndroidUtilities.dp(17.0F));
            ((Drawable)localObject).draw(paramCanvas);
            i = 0;
            if ((this.currentMessageObject.messageOwner.flags & 0x400) != 0)
            {
              j = (int)(this.timeWidth - this.timeLayout.getLineWidth(0));
              if (!this.currentMessageObject.isSending()) {
                break label2756;
              }
              i = j;
              if (!this.currentMessageObject.isOutOwner())
              {
                setDrawableBounds(Theme.clockMediaDrawable, this.timeX + AndroidUtilities.dp(11.0F), this.layoutHeight - AndroidUtilities.dp(13.0F) - Theme.clockMediaDrawable.getIntrinsicHeight());
                Theme.clockMediaDrawable.draw(paramCanvas);
                i = j;
              }
            }
            paramCanvas.save();
            paramCanvas.translate(this.timeX + i, this.layoutHeight - AndroidUtilities.dp(11.3F) - this.timeLayout.getHeight());
            this.timeLayout.draw(paramCanvas);
            paramCanvas.restore();
            if (!this.currentMessageObject.isOutOwner()) {
              break label3124;
            }
            i = 0;
            j = 0;
            k = 0;
            m = 0;
            if ((int)(this.currentMessageObject.getDialogId() >> 32) != 1) {
              break label3417;
            }
            n = 1;
            if (!this.currentMessageObject.isSending()) {
              break label3423;
            }
            i = 0;
            j = 0;
            k = 1;
            m = 0;
            if (k != 0)
            {
              if (this.mediaBackground) {
                break label3489;
              }
              setDrawableBounds(Theme.clockDrawable, this.layoutWidth - AndroidUtilities.dp(18.5F) - Theme.clockDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(8.5F) - Theme.clockDrawable.getIntrinsicHeight());
              Theme.clockDrawable.draw(paramCanvas);
            }
            if (n == 0) {
              break label3595;
            }
            if ((i != 0) || (j != 0))
            {
              if (this.mediaBackground) {
                break label3542;
              }
              setDrawableBounds(Theme.broadcastDrawable, this.layoutWidth - AndroidUtilities.dp(20.5F) - Theme.broadcastDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(8.0F) - Theme.broadcastDrawable.getIntrinsicHeight());
              Theme.broadcastDrawable.draw(paramCanvas);
            }
          }
        }
        for (;;)
        {
          if (m == 0) {
            break label3928;
          }
          if (this.mediaBackground) {
            break label3930;
          }
          setDrawableBounds(Theme.errorDrawable, this.layoutWidth - AndroidUtilities.dp(18.0F) - Theme.errorDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(7.0F) - Theme.errorDrawable.getIntrinsicHeight());
          Theme.errorDrawable.draw(paramCanvas);
          return;
          this.replyStartX = (this.currentBackgroundDrawable.getBounds().right + AndroidUtilities.dp(17.0F));
          break label1426;
          i = 0;
          break label1487;
          if (this.currentMessageObject.isOutOwner())
          {
            replyLinePaint.setColor(-7812741);
            replyNamePaint.setColor(-11162801);
            if ((this.currentMessageObject.replyMessageObject != null) && (this.currentMessageObject.replyMessageObject.type == 0) && (!(this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame)))
            {
              replyTextPaint.setColor(-16777216);
              this.replyStartX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(12.0F));
              if ((!this.drawForwardedName) || (this.forwardedNameLayout[0] == null)) {
                break label2718;
              }
              i = 36;
              if ((!this.drawNameLayout) || (this.nameLayout == null)) {
                break label2724;
              }
            }
          }
          for (j = 20;; j = 0)
          {
            this.replyStartY = AndroidUtilities.dp(j + (i + 12));
            break;
            localObject = replyTextPaint;
            if (isDrawSelectedBackground()) {}
            for (;;)
            {
              ((TextPaint)localObject).setColor(-10112933);
              break;
            }
            replyLinePaint.setColor(-9390872);
            replyNamePaint.setColor(-12940081);
            if ((this.currentMessageObject.replyMessageObject != null) && (this.currentMessageObject.replyMessageObject.type == 0) && (!(this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame)))
            {
              replyTextPaint.setColor(-16777216);
              if (!this.mediaBackground) {
                break label2694;
              }
              this.replyStartX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(12.0F));
              break label2476;
            }
            localObject = replyTextPaint;
            if (isDrawSelectedBackground()) {}
            for (i = -7752511;; i = -6182221)
            {
              ((TextPaint)localObject).setColor(i);
              break;
            }
            this.replyStartX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(18.0F));
            break label2476;
            i = 0;
            break label2496;
          }
          i = 0;
          break label1693;
          i = 0;
          break label1762;
          localObject = Theme.timeBackgroundDrawable;
          break label1842;
          i = 0;
          break label1890;
          if (this.currentMessageObject.isSendError())
          {
            i = j;
            if (this.currentMessageObject.isOutOwner()) {
              break label2038;
            }
            setDrawableBounds(Theme.errorDrawable, this.timeX + AndroidUtilities.dp(11.0F), this.layoutHeight - AndroidUtilities.dp(12.5F) - Theme.errorDrawable.getIntrinsicHeight());
            Theme.errorDrawable.draw(paramCanvas);
            i = j;
            break label2038;
          }
          localObject = Theme.viewsMediaCountDrawable;
          setDrawableBounds((Drawable)localObject, this.timeX, this.layoutHeight - AndroidUtilities.dp(9.5F) - this.timeLayout.getHeight());
          ((Drawable)localObject).draw(paramCanvas);
          i = j;
          if (this.viewsLayout == null) {
            break label2038;
          }
          paramCanvas.save();
          paramCanvas.translate(this.timeX + ((Drawable)localObject).getIntrinsicWidth() + AndroidUtilities.dp(3.0F), this.layoutHeight - AndroidUtilities.dp(11.3F) - this.timeLayout.getHeight());
          this.viewsLayout.draw(paramCanvas);
          paramCanvas.restore();
          i = j;
          break label2038;
          i = 0;
          if ((this.currentMessageObject.messageOwner.flags & 0x400) != 0)
          {
            j = (int)(this.timeWidth - this.timeLayout.getLineWidth(0));
            if (!this.currentMessageObject.isSending()) {
              break label3132;
            }
            i = j;
            if (!this.currentMessageObject.isOutOwner())
            {
              localObject = Theme.clockChannelDrawable;
              if (!isDrawSelectedBackground()) {
                break label3126;
              }
              i = 1;
              localObject = localObject[i];
              setDrawableBounds((Drawable)localObject, this.timeX + AndroidUtilities.dp(11.0F), this.layoutHeight - AndroidUtilities.dp(8.5F) - ((Drawable)localObject).getIntrinsicHeight());
              ((Drawable)localObject).draw(paramCanvas);
              i = j;
            }
          }
          for (;;)
          {
            paramCanvas.save();
            paramCanvas.translate(this.timeX + i, this.layoutHeight - AndroidUtilities.dp(6.5F) - this.timeLayout.getHeight());
            this.timeLayout.draw(paramCanvas);
            paramCanvas.restore();
            break label2087;
            break;
            i = 0;
            break label3023;
            if (!this.currentMessageObject.isSendError()) {
              break label3206;
            }
            i = j;
            if (!this.currentMessageObject.isOutOwner())
            {
              setDrawableBounds(Theme.errorDrawable, this.timeX + AndroidUtilities.dp(11.0F), this.layoutHeight - AndroidUtilities.dp(6.5F) - Theme.errorDrawable.getIntrinsicHeight());
              Theme.errorDrawable.draw(paramCanvas);
              i = j;
            }
          }
          if (!this.currentMessageObject.isOutOwner())
          {
            localObject = Theme.viewsCountDrawable;
            if (isDrawSelectedBackground())
            {
              i = 1;
              setDrawableBounds(localObject[i], this.timeX, this.layoutHeight - AndroidUtilities.dp(4.5F) - this.timeLayout.getHeight());
              localObject = Theme.viewsCountDrawable;
              if (!isDrawSelectedBackground()) {
                break label3371;
              }
              i = 1;
              localObject[i].draw(paramCanvas);
            }
          }
          for (;;)
          {
            i = j;
            if (this.viewsLayout == null) {
              break;
            }
            paramCanvas.save();
            paramCanvas.translate(this.timeX + Theme.viewsOutCountDrawable.getIntrinsicWidth() + AndroidUtilities.dp(3.0F), this.layoutHeight - AndroidUtilities.dp(6.5F) - this.timeLayout.getHeight());
            this.viewsLayout.draw(paramCanvas);
            paramCanvas.restore();
            i = j;
            break;
            i = 0;
            break label3231;
            i = 0;
            break label3278;
            setDrawableBounds(Theme.viewsOutCountDrawable, this.timeX, this.layoutHeight - AndroidUtilities.dp(4.5F) - this.timeLayout.getHeight());
            Theme.viewsOutCountDrawable.draw(paramCanvas);
          }
          n = 0;
          break label2127;
          if (this.currentMessageObject.isSendError())
          {
            i = 0;
            j = 0;
            k = 0;
            m = 1;
            break label2149;
          }
          if (!this.currentMessageObject.isSent()) {
            break label2149;
          }
          if (!this.currentMessageObject.isUnread()) {}
          for (i = 1;; i = 0)
          {
            j = 1;
            k = 0;
            m = 0;
            break;
          }
          setDrawableBounds(Theme.clockMediaDrawable, this.layoutWidth - AndroidUtilities.dp(22.0F) - Theme.clockMediaDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(12.5F) - Theme.clockMediaDrawable.getIntrinsicHeight());
          Theme.clockMediaDrawable.draw(paramCanvas);
          break label2211;
          setDrawableBounds(Theme.broadcastMediaDrawable, this.layoutWidth - AndroidUtilities.dp(24.0F) - Theme.broadcastMediaDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(13.0F) - Theme.broadcastMediaDrawable.getIntrinsicHeight());
          Theme.broadcastMediaDrawable.draw(paramCanvas);
          continue;
          if (j != 0)
          {
            if (this.mediaBackground) {
              break label3773;
            }
            if (i == 0) {
              break label3727;
            }
            setDrawableBounds(Theme.checkDrawable, this.layoutWidth - AndroidUtilities.dp(22.5F) - Theme.checkDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(8.0F) - Theme.checkDrawable.getIntrinsicHeight());
          }
          for (;;)
          {
            Theme.checkDrawable.draw(paramCanvas);
            if (i == 0) {
              break label3829;
            }
            if (this.mediaBackground) {
              break label3877;
            }
            setDrawableBounds(Theme.halfCheckDrawable, this.layoutWidth - AndroidUtilities.dp(18.0F) - Theme.halfCheckDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(8.0F) - Theme.halfCheckDrawable.getIntrinsicHeight());
            Theme.halfCheckDrawable.draw(paramCanvas);
            break;
            setDrawableBounds(Theme.checkDrawable, this.layoutWidth - AndroidUtilities.dp(18.5F) - Theme.checkDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(8.0F) - Theme.checkDrawable.getIntrinsicHeight());
          }
          if (i != 0) {
            setDrawableBounds(Theme.checkMediaDrawable, this.layoutWidth - AndroidUtilities.dp(26.3F) - Theme.checkMediaDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(12.5F) - Theme.checkMediaDrawable.getIntrinsicHeight());
          }
          for (;;)
          {
            Theme.checkMediaDrawable.draw(paramCanvas);
            break label3662;
            break;
            setDrawableBounds(Theme.checkMediaDrawable, this.layoutWidth - AndroidUtilities.dp(21.5F) - Theme.checkMediaDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(12.5F) - Theme.checkMediaDrawable.getIntrinsicHeight());
          }
          setDrawableBounds(Theme.halfCheckMediaDrawable, this.layoutWidth - AndroidUtilities.dp(21.5F) - Theme.halfCheckMediaDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(12.5F) - Theme.halfCheckMediaDrawable.getIntrinsicHeight());
          Theme.halfCheckMediaDrawable.draw(paramCanvas);
        }
      }
    }
    label3930:
    setDrawableBounds(Theme.errorDrawable, this.layoutWidth - AndroidUtilities.dp(20.5F) - Theme.errorDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(11.5F) - Theme.errorDrawable.getIntrinsicHeight());
    Theme.errorDrawable.draw(paramCanvas);
  }
  
  public void onFailedDownload(String paramString)
  {
    if ((this.documentAttachType == 3) || (this.documentAttachType == 5)) {}
    for (boolean bool = true;; bool = false)
    {
      updateButtonState(bool);
      return;
    }
  }
  
  @SuppressLint({"DrawAllocation"})
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.currentMessageObject == null)
    {
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    label176:
    label221:
    label372:
    Object localObject;
    if ((paramBoolean) || (!this.wasLayout))
    {
      this.layoutWidth = getMeasuredWidth();
      this.layoutHeight = (getMeasuredHeight() - this.substractBackgroundHeight);
      if (this.timeTextWidth < 0) {
        this.timeTextWidth = AndroidUtilities.dp(10.0F);
      }
      this.timeLayout = new StaticLayout(this.currentTimeString, timePaint, this.timeTextWidth + AndroidUtilities.dp(6.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      if (this.mediaBackground) {
        break label618;
      }
      if (this.currentMessageObject.isOutOwner()) {
        break label595;
      }
      paramInt2 = this.backgroundWidth;
      paramInt3 = AndroidUtilities.dp(9.0F);
      paramInt4 = this.timeWidth;
      if ((this.isChat) && (this.currentMessageObject.isFromUser()))
      {
        paramInt1 = AndroidUtilities.dp(48.0F);
        this.timeX = (paramInt1 + (paramInt2 - paramInt3 - paramInt4));
        if ((this.currentMessageObject.messageOwner.flags & 0x400) == 0) {
          break label715;
        }
        this.viewsLayout = new StaticLayout(this.currentViewsString, timePaint, this.viewsTextWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        if (this.isAvatarVisible) {
          this.avatarImage.setImageCoords(AndroidUtilities.dp(6.0F), this.layoutHeight - AndroidUtilities.dp(44.0F), AndroidUtilities.dp(42.0F), AndroidUtilities.dp(42.0F));
        }
        this.wasLayout = true;
      }
    }
    else
    {
      if (this.currentMessageObject.type == 0) {
        this.textY = (AndroidUtilities.dp(10.0F) + this.namesOffset);
      }
      if (this.documentAttachType != 3) {
        break label816;
      }
      if (!this.currentMessageObject.isOutOwner()) {
        break label723;
      }
      this.seekBarX = (this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(57.0F));
      this.buttonX = (this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(14.0F));
      this.timeAudioX = (this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(67.0F));
      if (this.hasLinkPreview)
      {
        this.seekBarX += AndroidUtilities.dp(10.0F);
        this.buttonX += AndroidUtilities.dp(10.0F);
        this.timeAudioX += AndroidUtilities.dp(10.0F);
      }
      localObject = this.seekBarWaveform;
      paramInt2 = this.backgroundWidth;
      if (!this.hasLinkPreview) {
        break label806;
      }
      paramInt1 = 10;
      label445:
      ((SeekBarWaveform)localObject).setSize(paramInt2 - AndroidUtilities.dp(paramInt1 + 92), AndroidUtilities.dp(30.0F));
      localObject = this.seekBar;
      paramInt2 = this.backgroundWidth;
      if (!this.hasLinkPreview) {
        break label811;
      }
    }
    label595:
    label618:
    label715:
    label723:
    label806:
    label811:
    for (paramInt1 = 10;; paramInt1 = 0)
    {
      ((SeekBar)localObject).setSize(paramInt2 - AndroidUtilities.dp(paramInt1 + 72), AndroidUtilities.dp(30.0F));
      this.seekBarY = (AndroidUtilities.dp(13.0F) + this.namesOffset + this.mediaOffsetY);
      this.buttonY = (AndroidUtilities.dp(13.0F) + this.namesOffset + this.mediaOffsetY);
      this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(44.0F), this.buttonY + AndroidUtilities.dp(44.0F));
      updateAudioProgress();
      return;
      paramInt1 = 0;
      break;
      this.timeX = (this.layoutWidth - this.timeWidth - AndroidUtilities.dp(38.5F));
      break label176;
      if (!this.currentMessageObject.isOutOwner())
      {
        paramInt2 = this.backgroundWidth;
        paramInt3 = AndroidUtilities.dp(4.0F);
        paramInt4 = this.timeWidth;
        if ((this.isChat) && (this.currentMessageObject.isFromUser())) {}
        for (paramInt1 = AndroidUtilities.dp(48.0F);; paramInt1 = 0)
        {
          this.timeX = (paramInt1 + (paramInt2 - paramInt3 - paramInt4));
          break;
        }
      }
      this.timeX = (this.layoutWidth - this.timeWidth - AndroidUtilities.dp(42.0F));
      break label176;
      this.viewsLayout = null;
      break label221;
      if ((this.isChat) && (this.currentMessageObject.isFromUser()))
      {
        this.seekBarX = AndroidUtilities.dp(114.0F);
        this.buttonX = AndroidUtilities.dp(71.0F);
        this.timeAudioX = AndroidUtilities.dp(124.0F);
        break label372;
      }
      this.seekBarX = AndroidUtilities.dp(66.0F);
      this.buttonX = AndroidUtilities.dp(23.0F);
      this.timeAudioX = AndroidUtilities.dp(76.0F);
      break label372;
      paramInt1 = 0;
      break label445;
    }
    label816:
    if (this.documentAttachType == 5)
    {
      if (this.currentMessageObject.isOutOwner())
      {
        this.seekBarX = (this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(56.0F));
        this.buttonX = (this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(14.0F));
        this.timeAudioX = (this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(67.0F));
        if (this.hasLinkPreview)
        {
          this.seekBarX += AndroidUtilities.dp(10.0F);
          this.buttonX += AndroidUtilities.dp(10.0F);
          this.timeAudioX += AndroidUtilities.dp(10.0F);
        }
        localObject = this.seekBar;
        paramInt2 = this.backgroundWidth;
        if (!this.hasLinkPreview) {
          break label1153;
        }
      }
      label1153:
      for (paramInt1 = 10;; paramInt1 = 0)
      {
        ((SeekBar)localObject).setSize(paramInt2 - AndroidUtilities.dp(paramInt1 + 65), AndroidUtilities.dp(30.0F));
        this.seekBarY = (AndroidUtilities.dp(29.0F) + this.namesOffset + this.mediaOffsetY);
        this.buttonY = (AndroidUtilities.dp(13.0F) + this.namesOffset + this.mediaOffsetY);
        this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(44.0F), this.buttonY + AndroidUtilities.dp(44.0F));
        updateAudioProgress();
        return;
        if ((this.isChat) && (this.currentMessageObject.isFromUser()))
        {
          this.seekBarX = AndroidUtilities.dp(113.0F);
          this.buttonX = AndroidUtilities.dp(71.0F);
          this.timeAudioX = AndroidUtilities.dp(124.0F);
          break;
        }
        this.seekBarX = AndroidUtilities.dp(65.0F);
        this.buttonX = AndroidUtilities.dp(23.0F);
        this.timeAudioX = AndroidUtilities.dp(76.0F);
        break;
      }
    }
    if ((this.documentAttachType == 1) && (!this.drawPhotoImage))
    {
      if (this.currentMessageObject.isOutOwner()) {
        this.buttonX = (this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(14.0F));
      }
      for (;;)
      {
        if (this.hasLinkPreview) {
          this.buttonX += AndroidUtilities.dp(10.0F);
        }
        this.buttonY = (AndroidUtilities.dp(13.0F) + this.namesOffset + this.mediaOffsetY);
        this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(44.0F), this.buttonY + AndroidUtilities.dp(44.0F));
        this.photoImage.setImageCoords(this.buttonX - AndroidUtilities.dp(10.0F), this.buttonY - AndroidUtilities.dp(10.0F), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
        return;
        if ((this.isChat) && (this.currentMessageObject.isFromUser())) {
          this.buttonX = AndroidUtilities.dp(71.0F);
        } else {
          this.buttonX = AndroidUtilities.dp(23.0F);
        }
      }
    }
    if (this.currentMessageObject.type == 12)
    {
      if (this.currentMessageObject.isOutOwner()) {
        paramInt1 = this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(14.0F);
      }
      for (;;)
      {
        this.photoImage.setImageCoords(paramInt1, AndroidUtilities.dp(13.0F) + this.namesOffset, AndroidUtilities.dp(44.0F), AndroidUtilities.dp(44.0F));
        return;
        if ((this.isChat) && (this.currentMessageObject.isFromUser())) {
          paramInt1 = AndroidUtilities.dp(72.0F);
        } else {
          paramInt1 = AndroidUtilities.dp(23.0F);
        }
      }
    }
    if (this.currentMessageObject.isOutOwner()) {
      if (this.mediaBackground) {
        paramInt1 = this.layoutWidth - this.backgroundWidth - AndroidUtilities.dp(3.0F);
      }
    }
    for (;;)
    {
      this.photoImage.setImageCoords(paramInt1, this.photoImage.getImageY(), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
      this.buttonX = ((int)(paramInt1 + (this.photoImage.getImageWidth() - AndroidUtilities.dp(48.0F)) / 2.0F));
      this.buttonY = ((int)(AndroidUtilities.dp(7.0F) + (this.photoImage.getImageHeight() - AndroidUtilities.dp(48.0F)) / 2.0F) + this.namesOffset);
      this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(48.0F), this.buttonY + AndroidUtilities.dp(48.0F));
      this.deleteProgressRect.set(this.buttonX + AndroidUtilities.dp(3.0F), this.buttonY + AndroidUtilities.dp(3.0F), this.buttonX + AndroidUtilities.dp(45.0F), this.buttonY + AndroidUtilities.dp(45.0F));
      return;
      paramInt1 = this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(6.0F);
      continue;
      if ((this.isChat) && (this.currentMessageObject.isFromUser())) {
        paramInt1 = AndroidUtilities.dp(63.0F);
      } else {
        paramInt1 = AndroidUtilities.dp(15.0F);
      }
    }
  }
  
  protected void onLongPress()
  {
    if ((this.pressedLink instanceof URLSpanNoUnderline))
    {
      if (!((URLSpanNoUnderline)this.pressedLink).getURL().startsWith("/")) {
        break label77;
      }
      this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, true);
    }
    label77:
    do
    {
      return;
      if ((this.pressedLink instanceof URLSpan))
      {
        this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, true);
        return;
      }
      resetPressedLink(-1);
      if ((this.buttonPressed != 0) || (this.pressedBotButton != -1))
      {
        this.buttonPressed = 0;
        this.pressedBotButton = -1;
        invalidate();
      }
    } while (this.delegate == null);
    this.delegate.didLongPressed(this);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if ((this.currentMessageObject != null) && (this.currentMessageObject.checkLayout()))
    {
      this.inLayout = true;
      MessageObject localMessageObject = this.currentMessageObject;
      this.currentMessageObject = null;
      setMessageObject(localMessageObject);
      this.inLayout = false;
    }
    setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), this.totalHeight + this.keyboardHeight);
  }
  
  public void onProgressDownload(String paramString, float paramFloat)
  {
    this.radialProgress.setProgress(paramFloat, true);
    if ((this.documentAttachType == 3) || (this.documentAttachType == 5)) {
      if (this.buttonState != 4) {
        updateButtonState(false);
      }
    }
    while (this.buttonState == 1) {
      return;
    }
    updateButtonState(false);
  }
  
  public void onProgressUpload(String paramString, float paramFloat, boolean paramBoolean)
  {
    this.radialProgress.setProgress(paramFloat, true);
  }
  
  public void onProvideStructure(ViewStructure paramViewStructure)
  {
    super.onProvideStructure(paramViewStructure);
    if ((this.allowAssistant) && (Build.VERSION.SDK_INT >= 23))
    {
      if ((this.currentMessageObject.messageText == null) || (this.currentMessageObject.messageText.length() <= 0)) {
        break label57;
      }
      paramViewStructure.setText(this.currentMessageObject.messageText);
    }
    label57:
    while ((this.currentMessageObject.caption == null) || (this.currentMessageObject.caption.length() <= 0)) {
      return;
    }
    paramViewStructure.setText(this.currentMessageObject.caption);
  }
  
  public void onSeekBarDrag(float paramFloat)
  {
    if (this.currentMessageObject == null) {
      return;
    }
    this.currentMessageObject.audioProgress = paramFloat;
    MediaController.getInstance().seekToProgress(this.currentMessageObject, paramFloat);
  }
  
  public void onSuccessDownload(String paramString)
  {
    if ((this.documentAttachType == 3) || (this.documentAttachType == 5))
    {
      updateButtonState(true);
      updateWaveform();
    }
    for (;;)
    {
      return;
      this.radialProgress.setProgress(1.0F, true);
      if (this.currentMessageObject.type == 0)
      {
        if ((this.documentAttachType == 2) && (this.currentMessageObject.audioProgress != 1.0F))
        {
          this.buttonState = 2;
          didPressedButton(true);
          return;
        }
        if (!this.photoNotSet)
        {
          updateButtonState(true);
          return;
        }
        setMessageObject(this.currentMessageObject);
        return;
      }
      if ((!this.photoNotSet) || ((this.currentMessageObject.type == 8) && (this.currentMessageObject.audioProgress != 1.0F)))
      {
        if ((this.currentMessageObject.type != 8) || (this.currentMessageObject.audioProgress == 1.0F)) {
          break label184;
        }
        this.photoNotSet = false;
        this.buttonState = 2;
        didPressedButton(true);
      }
      while (this.photoNotSet)
      {
        setMessageObject(this.currentMessageObject);
        return;
        label184:
        updateButtonState(true);
      }
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool3;
    if ((this.currentMessageObject == null) || (!this.delegate.canPerformActions())) {
      bool3 = super.onTouchEvent(paramMotionEvent);
    }
    boolean bool2;
    float f1;
    float f2;
    label726:
    label890:
    label1083:
    label1274:
    label1356:
    label1514:
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  do
                  {
                    do
                    {
                      do
                      {
                        do
                        {
                          do
                          {
                            do
                            {
                              do
                              {
                                do
                                {
                                  do
                                  {
                                    do
                                    {
                                      boolean bool1;
                                      do
                                      {
                                        do
                                        {
                                          return bool3;
                                          this.disallowLongPress = false;
                                          bool2 = checkTextBlockMotionEvent(paramMotionEvent);
                                          bool1 = bool2;
                                          if (!bool2) {
                                            bool1 = checkOtherButtonMotionEvent(paramMotionEvent);
                                          }
                                          bool2 = bool1;
                                          if (!bool1) {
                                            bool2 = checkLinkPreviewMotionEvent(paramMotionEvent);
                                          }
                                          bool1 = bool2;
                                          if (!bool2) {
                                            bool1 = checkGameMotionEvent(paramMotionEvent);
                                          }
                                          bool2 = bool1;
                                          if (!bool1) {
                                            bool2 = checkCaptionMotionEvent(paramMotionEvent);
                                          }
                                          bool1 = bool2;
                                          if (!bool2) {
                                            bool1 = checkAudioMotionEvent(paramMotionEvent);
                                          }
                                          bool2 = bool1;
                                          if (!bool1) {
                                            bool2 = checkPhotoImageMotionEvent(paramMotionEvent);
                                          }
                                          bool1 = bool2;
                                          if (!bool2) {
                                            bool1 = checkBotButtonMotionEvent(paramMotionEvent);
                                          }
                                          bool2 = bool1;
                                          if (paramMotionEvent.getAction() == 3)
                                          {
                                            this.buttonPressed = 0;
                                            this.pressedBotButton = -1;
                                            this.linkPreviewPressed = false;
                                            this.otherPressed = false;
                                            this.imagePressed = false;
                                            bool2 = false;
                                            resetPressedLink(-1);
                                          }
                                          if ((!this.disallowLongPress) && (bool2) && (paramMotionEvent.getAction() == 0)) {
                                            startCheckLongPress();
                                          }
                                          if ((paramMotionEvent.getAction() != 0) && (paramMotionEvent.getAction() != 2)) {
                                            cancelCheckLongPress();
                                          }
                                          bool3 = bool2;
                                        } while (bool2);
                                        f1 = paramMotionEvent.getX();
                                        f2 = paramMotionEvent.getY();
                                        if (paramMotionEvent.getAction() != 0) {
                                          break label726;
                                        }
                                        if (this.delegate == null) {
                                          break;
                                        }
                                        bool3 = bool2;
                                      } while (!this.delegate.canPerformActions());
                                      if ((this.isAvatarVisible) && (this.avatarImage.isInsideImage(f1, f2)))
                                      {
                                        this.avatarPressed = true;
                                        bool1 = true;
                                      }
                                      for (;;)
                                      {
                                        bool3 = bool1;
                                        if (!bool1) {
                                          break;
                                        }
                                        startCheckLongPress();
                                        return bool1;
                                        if ((this.drawForwardedName) && (this.forwardedNameLayout[0] != null) && (f1 >= this.forwardNameX) && (f1 <= this.forwardNameX + this.forwardedNameWidth) && (f2 >= this.forwardNameY) && (f2 <= this.forwardNameY + AndroidUtilities.dp(32.0F)))
                                        {
                                          if ((this.viaWidth != 0) && (f1 >= this.forwardNameX + this.viaNameWidth + AndroidUtilities.dp(4.0F))) {
                                            this.forwardBotPressed = true;
                                          }
                                          for (;;)
                                          {
                                            bool1 = true;
                                            break;
                                            this.forwardNamePressed = true;
                                          }
                                        }
                                        if ((this.drawNameLayout) && (this.nameLayout != null) && (this.viaWidth != 0) && (f1 >= this.nameX + this.viaNameWidth) && (f1 <= this.nameX + this.viaNameWidth + this.viaWidth) && (f2 >= this.nameY - AndroidUtilities.dp(4.0F)) && (f2 <= this.nameY + AndroidUtilities.dp(20.0F)))
                                        {
                                          this.forwardBotPressed = true;
                                          bool1 = true;
                                        }
                                        else if ((this.currentMessageObject.isReply()) && (f1 >= this.replyStartX) && (f1 <= this.replyStartX + Math.max(this.replyNameWidth, this.replyTextWidth)) && (f2 >= this.replyStartY) && (f2 <= this.replyStartY + AndroidUtilities.dp(35.0F)))
                                        {
                                          this.replyPressed = true;
                                          bool1 = true;
                                        }
                                        else
                                        {
                                          bool1 = bool2;
                                          if (this.drawShareButton)
                                          {
                                            bool1 = bool2;
                                            if (f1 >= this.shareStartX)
                                            {
                                              bool1 = bool2;
                                              if (f1 <= this.shareStartX + AndroidUtilities.dp(40.0F))
                                              {
                                                bool1 = bool2;
                                                if (f2 >= this.shareStartY)
                                                {
                                                  bool1 = bool2;
                                                  if (f2 <= this.shareStartY + AndroidUtilities.dp(32.0F))
                                                  {
                                                    this.sharePressed = true;
                                                    bool1 = true;
                                                    invalidate();
                                                  }
                                                }
                                              }
                                            }
                                          }
                                        }
                                      }
                                      if (paramMotionEvent.getAction() != 2) {
                                        cancelCheckLongPress();
                                      }
                                      if (!this.avatarPressed) {
                                        break label890;
                                      }
                                      if (paramMotionEvent.getAction() != 1) {
                                        break;
                                      }
                                      this.avatarPressed = false;
                                      playSoundEffect(0);
                                      bool3 = bool2;
                                    } while (this.delegate == null);
                                    if (this.currentUser != null)
                                    {
                                      this.delegate.didPressedUserAvatar(this, this.currentUser);
                                      return bool2;
                                    }
                                    bool3 = bool2;
                                  } while (this.currentChat == null);
                                  this.delegate.didPressedChannelAvatar(this, this.currentChat, 0);
                                  return bool2;
                                  if (paramMotionEvent.getAction() == 3)
                                  {
                                    this.avatarPressed = false;
                                    return bool2;
                                  }
                                  bool3 = bool2;
                                } while (paramMotionEvent.getAction() != 2);
                                bool3 = bool2;
                              } while (!this.isAvatarVisible);
                              bool3 = bool2;
                            } while (this.avatarImage.isInsideImage(f1, f2));
                            this.avatarPressed = false;
                            return bool2;
                            if (!this.forwardNamePressed) {
                              break label1083;
                            }
                            if (paramMotionEvent.getAction() != 1) {
                              break;
                            }
                            this.forwardNamePressed = false;
                            playSoundEffect(0);
                            bool3 = bool2;
                          } while (this.delegate == null);
                          if (this.currentForwardChannel != null)
                          {
                            this.delegate.didPressedChannelAvatar(this, this.currentForwardChannel, this.currentMessageObject.messageOwner.fwd_from.channel_post);
                            return bool2;
                          }
                          bool3 = bool2;
                        } while (this.currentForwardUser == null);
                        this.delegate.didPressedUserAvatar(this, this.currentForwardUser);
                        return bool2;
                        if (paramMotionEvent.getAction() == 3)
                        {
                          this.forwardNamePressed = false;
                          return bool2;
                        }
                        bool3 = bool2;
                      } while (paramMotionEvent.getAction() != 2);
                      if ((f1 < this.forwardNameX) || (f1 > this.forwardNameX + this.forwardedNameWidth) || (f2 < this.forwardNameY)) {
                        break;
                      }
                      bool3 = bool2;
                    } while (f2 <= this.forwardNameY + AndroidUtilities.dp(32.0F));
                    this.forwardNamePressed = false;
                    return bool2;
                    if (!this.forwardBotPressed) {
                      break label1356;
                    }
                    if (paramMotionEvent.getAction() != 1) {
                      break;
                    }
                    this.forwardBotPressed = false;
                    playSoundEffect(0);
                    bool3 = bool2;
                  } while (this.delegate == null);
                  ChatMessageCellDelegate localChatMessageCellDelegate = this.delegate;
                  if (this.currentViaBotUser != null) {}
                  for (paramMotionEvent = this.currentViaBotUser.username;; paramMotionEvent = this.currentMessageObject.messageOwner.via_bot_name)
                  {
                    localChatMessageCellDelegate.didPressedViaBot(this, paramMotionEvent);
                    return bool2;
                  }
                  if (paramMotionEvent.getAction() == 3)
                  {
                    this.forwardBotPressed = false;
                    return bool2;
                  }
                  bool3 = bool2;
                } while (paramMotionEvent.getAction() != 2);
                if ((!this.drawForwardedName) || (this.forwardedNameLayout[0] == null)) {
                  break label1274;
                }
                if ((f1 < this.forwardNameX) || (f1 > this.forwardNameX + this.forwardedNameWidth) || (f2 < this.forwardNameY)) {
                  break;
                }
                bool3 = bool2;
              } while (f2 <= this.forwardNameY + AndroidUtilities.dp(32.0F));
              this.forwardBotPressed = false;
              return bool2;
              if ((f1 < this.nameX + this.viaNameWidth) || (f1 > this.nameX + this.viaNameWidth + this.viaWidth) || (f2 < this.nameY - AndroidUtilities.dp(4.0F))) {
                break;
              }
              bool3 = bool2;
            } while (f2 <= this.nameY + AndroidUtilities.dp(20.0F));
            this.forwardBotPressed = false;
            return bool2;
            if (!this.replyPressed) {
              break label1514;
            }
            if (paramMotionEvent.getAction() != 1) {
              break;
            }
            this.replyPressed = false;
            playSoundEffect(0);
            bool3 = bool2;
          } while (this.delegate == null);
          this.delegate.didPressedReplyMessage(this, this.currentMessageObject.messageOwner.reply_to_msg_id);
          return bool2;
          if (paramMotionEvent.getAction() == 3)
          {
            this.replyPressed = false;
            return bool2;
          }
          bool3 = bool2;
        } while (paramMotionEvent.getAction() != 2);
        if ((f1 < this.replyStartX) || (f1 > this.replyStartX + Math.max(this.replyNameWidth, this.replyTextWidth)) || (f2 < this.replyStartY)) {
          break;
        }
        bool3 = bool2;
      } while (f2 <= this.replyStartY + AndroidUtilities.dp(35.0F));
      this.replyPressed = false;
      return bool2;
      bool3 = bool2;
    } while (!this.sharePressed);
    if (paramMotionEvent.getAction() == 1)
    {
      this.sharePressed = false;
      playSoundEffect(0);
      if (this.delegate != null) {
        this.delegate.didPressedShare(this);
      }
    }
    for (;;)
    {
      invalidate();
      return bool2;
      if (paramMotionEvent.getAction() == 3) {
        this.sharePressed = false;
      } else if ((paramMotionEvent.getAction() == 2) && ((f1 < this.shareStartX) || (f1 > this.shareStartX + AndroidUtilities.dp(40.0F)) || (f2 < this.shareStartY) || (f2 > this.shareStartY + AndroidUtilities.dp(32.0F)))) {
        this.sharePressed = false;
      }
    }
  }
  
  public void requestLayout()
  {
    if (this.inLayout) {
      return;
    }
    super.requestLayout();
  }
  
  public void setAllowAssistant(boolean paramBoolean)
  {
    this.allowAssistant = paramBoolean;
  }
  
  public void setCheckPressed(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.isCheckPressed = paramBoolean1;
    this.isPressed = paramBoolean2;
    this.radialProgress.swapBackground(getDrawableForCurrentState());
    if (this.useSeekBarWaweform) {
      this.seekBarWaveform.setSelected(isDrawSelectedBackground());
    }
    for (;;)
    {
      invalidate();
      return;
      this.seekBar.setSelected(isDrawSelectedBackground());
    }
  }
  
  public void setDelegate(ChatMessageCellDelegate paramChatMessageCellDelegate)
  {
    this.delegate = paramChatMessageCellDelegate;
  }
  
  public void setHighlighted(boolean paramBoolean)
  {
    if (this.isHighlighted == paramBoolean) {
      return;
    }
    this.isHighlighted = paramBoolean;
    this.radialProgress.swapBackground(getDrawableForCurrentState());
    if (this.useSeekBarWaweform) {
      this.seekBarWaveform.setSelected(isDrawSelectedBackground());
    }
    for (;;)
    {
      invalidate();
      return;
      this.seekBar.setSelected(isDrawSelectedBackground());
    }
  }
  
  public void setHighlightedText(String paramString)
  {
    if ((this.currentMessageObject.messageOwner.message == null) || (this.currentMessageObject == null) || (this.currentMessageObject.type != 0) || (TextUtils.isEmpty(this.currentMessageObject.messageText)) || (paramString == null)) {
      if (!this.urlPathSelection.isEmpty())
      {
        this.linkSelectionBlockNum = -1;
        resetUrlPaths(true);
        invalidate();
      }
    }
    for (;;)
    {
      return;
      int k = TextUtils.indexOf(this.currentMessageObject.messageOwner.message.toLowerCase(), paramString.toLowerCase());
      if (k == -1)
      {
        if (!this.urlPathSelection.isEmpty())
        {
          this.linkSelectionBlockNum = -1;
          resetUrlPaths(true);
          invalidate();
        }
      }
      else
      {
        int j = k + paramString.length();
        int i = 0;
        while (i < this.currentMessageObject.textLayoutBlocks.size())
        {
          paramString = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(i);
          if ((k >= paramString.charactersOffset) && (k < paramString.charactersOffset + paramString.textLayout.getText().length()))
          {
            this.linkSelectionBlockNum = i;
            resetUrlPaths(true);
            for (;;)
            {
              try
              {
                Object localObject = obtainNewUrlPath(true);
                int m = paramString.textLayout.getText().length();
                ((LinkPath)localObject).setCurrentLayout(paramString.textLayout, k, 0.0F);
                paramString.textLayout.getSelectionPath(k, j - paramString.charactersOffset, (Path)localObject);
                if (j >= paramString.charactersOffset + m)
                {
                  i += 1;
                  if (i < this.currentMessageObject.textLayoutBlocks.size())
                  {
                    localObject = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(i);
                    k = ((MessageObject.TextLayoutBlock)localObject).textLayout.getText().length();
                    LinkPath localLinkPath = obtainNewUrlPath(true);
                    localLinkPath.setCurrentLayout(((MessageObject.TextLayoutBlock)localObject).textLayout, 0, ((MessageObject.TextLayoutBlock)localObject).height);
                    ((MessageObject.TextLayoutBlock)localObject).textLayout.getSelectionPath(0, j - ((MessageObject.TextLayoutBlock)localObject).charactersOffset, localLinkPath);
                    m = paramString.charactersOffset;
                    if (j >= m + k - 1) {
                      continue;
                    }
                  }
                }
              }
              catch (Exception paramString)
              {
                FileLog.e("tmessages", paramString);
                continue;
              }
              invalidate();
              return;
              i += 1;
            }
          }
          i += 1;
        }
      }
    }
  }
  
  /* Error */
  public void setMessageObject(MessageObject paramMessageObject)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 2278	org/telegram/messenger/MessageObject:checkLayout	()Z
    //   4: ifeq +8 -> 12
    //   7: aload_0
    //   8: aconst_null
    //   9: putfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   12: aload_0
    //   13: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   16: ifnull +17 -> 33
    //   19: aload_0
    //   20: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   23: invokevirtual 2389	org/telegram/messenger/MessageObject:getId	()I
    //   26: aload_1
    //   27: invokevirtual 2389	org/telegram/messenger/MessageObject:getId	()I
    //   30: if_icmpeq +1826 -> 1856
    //   33: iconst_1
    //   34: istore 19
    //   36: aload_0
    //   37: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   40: aload_1
    //   41: if_acmpne +10 -> 51
    //   44: aload_1
    //   45: getfield 2392	org/telegram/messenger/MessageObject:forceUpdate	Z
    //   48: ifeq +1814 -> 1862
    //   51: iconst_1
    //   52: istore 8
    //   54: aload_0
    //   55: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   58: aload_1
    //   59: if_acmpne +1809 -> 1868
    //   62: aload_0
    //   63: invokespecial 2394	org/telegram/ui/Cells/ChatMessageCell:isUserDataChanged	()Z
    //   66: ifne +10 -> 76
    //   69: aload_0
    //   70: getfield 1734	org/telegram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   73: ifeq +1795 -> 1868
    //   76: iconst_1
    //   77: istore 28
    //   79: iload 8
    //   81: ifne +16 -> 97
    //   84: iload 28
    //   86: ifne +11 -> 97
    //   89: aload_0
    //   90: aload_1
    //   91: invokespecial 2396	org/telegram/ui/Cells/ChatMessageCell:isPhotoDataChanged	(Lorg/telegram/messenger/MessageObject;)Z
    //   94: ifeq +12102 -> 12196
    //   97: aload_0
    //   98: aload_1
    //   99: putfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   102: aload_0
    //   103: aload_1
    //   104: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   107: getfield 1753	org/telegram/tgnet/TLRPC$Message:send_state	I
    //   110: putfield 1750	org/telegram/ui/Cells/ChatMessageCell:lastSendState	I
    //   113: aload_0
    //   114: aload_1
    //   115: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   118: getfield 1415	org/telegram/tgnet/TLRPC$Message:destroyTime	I
    //   121: putfield 1755	org/telegram/ui/Cells/ChatMessageCell:lastDeleteDate	I
    //   124: aload_0
    //   125: aload_1
    //   126: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   129: getfield 1760	org/telegram/tgnet/TLRPC$Message:views	I
    //   132: putfield 1757	org/telegram/ui/Cells/ChatMessageCell:lastViewsCount	I
    //   135: aload_0
    //   136: iconst_0
    //   137: putfield 1691	org/telegram/ui/Cells/ChatMessageCell:isPressed	Z
    //   140: aload_0
    //   141: iconst_1
    //   142: putfield 282	org/telegram/ui/Cells/ChatMessageCell:isCheckPressed	Z
    //   145: aload_0
    //   146: iconst_0
    //   147: putfield 1762	org/telegram/ui/Cells/ChatMessageCell:isAvatarVisible	Z
    //   150: aload_0
    //   151: iconst_0
    //   152: putfield 2088	org/telegram/ui/Cells/ChatMessageCell:wasLayout	Z
    //   155: aload_0
    //   156: aload_0
    //   157: aload_1
    //   158: invokespecial 2398	org/telegram/ui/Cells/ChatMessageCell:checkNeedDrawShareButton	(Lorg/telegram/messenger/MessageObject;)Z
    //   161: putfield 2097	org/telegram/ui/Cells/ChatMessageCell:drawShareButton	Z
    //   164: aload_0
    //   165: aconst_null
    //   166: putfield 2006	org/telegram/ui/Cells/ChatMessageCell:replyNameLayout	Landroid/text/StaticLayout;
    //   169: aload_0
    //   170: aconst_null
    //   171: putfield 1773	org/telegram/ui/Cells/ChatMessageCell:replyTextLayout	Landroid/text/StaticLayout;
    //   174: aload_0
    //   175: iconst_0
    //   176: putfield 2008	org/telegram/ui/Cells/ChatMessageCell:replyNameWidth	I
    //   179: aload_0
    //   180: iconst_0
    //   181: putfield 2012	org/telegram/ui/Cells/ChatMessageCell:replyTextWidth	I
    //   184: aload_0
    //   185: iconst_0
    //   186: putfield 1918	org/telegram/ui/Cells/ChatMessageCell:viaWidth	I
    //   189: aload_0
    //   190: iconst_0
    //   191: putfield 1929	org/telegram/ui/Cells/ChatMessageCell:viaNameWidth	I
    //   194: aload_0
    //   195: aconst_null
    //   196: putfield 1789	org/telegram/ui/Cells/ChatMessageCell:currentReplyPhoto	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   199: aload_0
    //   200: aconst_null
    //   201: putfield 1746	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   204: aload_0
    //   205: aconst_null
    //   206: putfield 1748	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   209: aload_0
    //   210: aconst_null
    //   211: putfield 1920	org/telegram/ui/Cells/ChatMessageCell:currentViaBotUser	Lorg/telegram/tgnet/TLRPC$User;
    //   214: aload_0
    //   215: iconst_0
    //   216: putfield 1922	org/telegram/ui/Cells/ChatMessageCell:drawNameLayout	Z
    //   219: aload_0
    //   220: iconst_m1
    //   221: invokespecial 708	org/telegram/ui/Cells/ChatMessageCell:resetPressedLink	(I)V
    //   224: aload_1
    //   225: iconst_0
    //   226: putfield 2392	org/telegram/messenger/MessageObject:forceUpdate	Z
    //   229: aload_0
    //   230: iconst_0
    //   231: putfield 711	org/telegram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   234: aload_0
    //   235: iconst_0
    //   236: putfield 473	org/telegram/ui/Cells/ChatMessageCell:hasLinkPreview	Z
    //   239: aload_0
    //   240: iconst_0
    //   241: putfield 475	org/telegram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   244: aload_0
    //   245: iconst_0
    //   246: putfield 631	org/telegram/ui/Cells/ChatMessageCell:linkPreviewPressed	Z
    //   249: aload_0
    //   250: iconst_0
    //   251: putfield 555	org/telegram/ui/Cells/ChatMessageCell:buttonPressed	I
    //   254: aload_0
    //   255: iconst_m1
    //   256: putfield 603	org/telegram/ui/Cells/ChatMessageCell:pressedBotButton	I
    //   259: aload_0
    //   260: iconst_0
    //   261: putfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   264: aload_0
    //   265: iconst_0
    //   266: putfield 551	org/telegram/ui/Cells/ChatMessageCell:mediaOffsetY	I
    //   269: aload_0
    //   270: iconst_0
    //   271: putfield 497	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   274: aload_0
    //   275: aconst_null
    //   276: putfield 1010	org/telegram/ui/Cells/ChatMessageCell:documentAttach	Lorg/telegram/tgnet/TLRPC$Document;
    //   279: aload_0
    //   280: aconst_null
    //   281: putfield 719	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   284: aload_0
    //   285: aconst_null
    //   286: putfield 1365	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   289: aload_0
    //   290: aconst_null
    //   291: putfield 1397	org/telegram/ui/Cells/ChatMessageCell:videoInfoLayout	Landroid/text/StaticLayout;
    //   294: aload_0
    //   295: aconst_null
    //   296: putfield 1333	org/telegram/ui/Cells/ChatMessageCell:siteNameLayout	Landroid/text/StaticLayout;
    //   299: aload_0
    //   300: aconst_null
    //   301: putfield 1369	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   304: aload_0
    //   305: aconst_null
    //   306: putfield 629	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   309: aload_0
    //   310: aconst_null
    //   311: putfield 1177	org/telegram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   314: aload_0
    //   315: iconst_0
    //   316: putfield 765	org/telegram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   319: aload_0
    //   320: aconst_null
    //   321: putfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   324: aload_0
    //   325: aconst_null
    //   326: putfield 1245	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   329: aload_0
    //   330: aconst_null
    //   331: putfield 1220	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   334: aload_0
    //   335: aconst_null
    //   336: putfield 1127	org/telegram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   339: aload_0
    //   340: iconst_0
    //   341: putfield 1239	org/telegram/ui/Cells/ChatMessageCell:cancelLoading	Z
    //   344: aload_0
    //   345: iconst_m1
    //   346: putfield 545	org/telegram/ui/Cells/ChatMessageCell:buttonState	I
    //   349: aload_0
    //   350: aconst_null
    //   351: putfield 1696	org/telegram/ui/Cells/ChatMessageCell:currentUrl	Ljava/lang/String;
    //   354: aload_0
    //   355: iconst_0
    //   356: putfield 1734	org/telegram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   359: aload_0
    //   360: iconst_1
    //   361: putfield 284	org/telegram/ui/Cells/ChatMessageCell:drawBackground	Z
    //   364: aload_0
    //   365: iconst_0
    //   366: putfield 1791	org/telegram/ui/Cells/ChatMessageCell:drawName	Z
    //   369: aload_0
    //   370: iconst_0
    //   371: putfield 508	org/telegram/ui/Cells/ChatMessageCell:useSeekBarWaweform	Z
    //   374: aload_0
    //   375: iconst_0
    //   376: putfield 1801	org/telegram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   379: aload_0
    //   380: iconst_0
    //   381: putfield 607	org/telegram/ui/Cells/ChatMessageCell:mediaBackground	Z
    //   384: aload_0
    //   385: iconst_0
    //   386: putfield 1036	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   389: aload_0
    //   390: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   393: iconst_0
    //   394: invokevirtual 1210	org/telegram/messenger/ImageReceiver:setNeedsQualityThumb	(Z)V
    //   397: aload_0
    //   398: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   401: iconst_0
    //   402: invokevirtual 1213	org/telegram/messenger/ImageReceiver:setShouldGenerateQualityThumb	(Z)V
    //   405: aload_0
    //   406: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   409: aconst_null
    //   410: invokevirtual 1216	org/telegram/messenger/ImageReceiver:setParentMessageObject	(Lorg/telegram/messenger/MessageObject;)V
    //   413: aload_0
    //   414: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   417: ldc_w 1342
    //   420: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   423: invokevirtual 419	org/telegram/messenger/ImageReceiver:setRoundRadius	(I)V
    //   426: iload 8
    //   428: ifeq +18 -> 446
    //   431: aload_0
    //   432: iconst_0
    //   433: putfield 1338	org/telegram/ui/Cells/ChatMessageCell:firstVisibleBlockNum	I
    //   436: aload_0
    //   437: iconst_0
    //   438: putfield 1340	org/telegram/ui/Cells/ChatMessageCell:lastVisibleBlockNum	I
    //   441: aload_0
    //   442: iconst_1
    //   443: putfield 1281	org/telegram/ui/Cells/ChatMessageCell:needNewVisiblePart	Z
    //   446: aload_1
    //   447: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   450: ifne +6271 -> 6721
    //   453: aload_0
    //   454: iconst_1
    //   455: putfield 1801	org/telegram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   458: invokestatic 1666	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   461: ifeq +1463 -> 1924
    //   464: aload_0
    //   465: getfield 1668	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   468: ifeq +1406 -> 1874
    //   471: aload_1
    //   472: invokevirtual 575	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   475: ifne +1399 -> 1874
    //   478: aload_1
    //   479: invokevirtual 857	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   482: ifeq +1392 -> 1874
    //   485: invokestatic 1671	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   488: ldc_w 2399
    //   491: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   494: isub
    //   495: istore 8
    //   497: aload_0
    //   498: iconst_1
    //   499: putfield 1791	org/telegram/ui/Cells/ChatMessageCell:drawName	Z
    //   502: aload_0
    //   503: iload 8
    //   505: putfield 1036	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   508: aload_0
    //   509: aload_1
    //   510: invokespecial 1040	org/telegram/ui/Cells/ChatMessageCell:measureTime	(Lorg/telegram/messenger/MessageObject;)V
    //   513: aload_0
    //   514: getfield 493	org/telegram/ui/Cells/ChatMessageCell:timeWidth	I
    //   517: ldc_w 1399
    //   520: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   523: iadd
    //   524: istore 9
    //   526: iload 9
    //   528: istore 20
    //   530: aload_1
    //   531: invokevirtual 575	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   534: ifeq +14 -> 548
    //   537: iload 9
    //   539: ldc_w 2185
    //   542: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   545: iadd
    //   546: istore 20
    //   548: aload_1
    //   549: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   552: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   555: instanceof 1984
    //   558: ifeq +1481 -> 2039
    //   561: aload_1
    //   562: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   565: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   568: getfield 1988	org/telegram/tgnet/TLRPC$MessageMedia:game	Lorg/telegram/tgnet/TLRPC$TL_game;
    //   571: instanceof 1990
    //   574: ifeq +1465 -> 2039
    //   577: iconst_1
    //   578: istore 29
    //   580: aload_0
    //   581: iload 29
    //   583: putfield 475	org/telegram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   586: aload_1
    //   587: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   590: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   593: instanceof 861
    //   596: ifeq +1449 -> 2045
    //   599: aload_1
    //   600: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   603: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   606: getfield 782	org/telegram/tgnet/TLRPC$MessageMedia:webpage	Lorg/telegram/tgnet/TLRPC$WebPage;
    //   609: instanceof 863
    //   612: ifeq +1433 -> 2045
    //   615: iconst_1
    //   616: istore 29
    //   618: aload_0
    //   619: iload 29
    //   621: putfield 473	org/telegram/ui/Cells/ChatMessageCell:hasLinkPreview	Z
    //   624: aload_0
    //   625: iload 8
    //   627: putfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   630: aload_0
    //   631: getfield 473	org/telegram/ui/Cells/ChatMessageCell:hasLinkPreview	Z
    //   634: ifne +22 -> 656
    //   637: aload_0
    //   638: getfield 475	org/telegram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   641: ifne +15 -> 656
    //   644: iload 8
    //   646: aload_1
    //   647: getfield 482	org/telegram/messenger/MessageObject:lastLineWidth	I
    //   650: isub
    //   651: iload 20
    //   653: if_icmpge +1398 -> 2051
    //   656: aload_0
    //   657: aload_0
    //   658: getfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   661: aload_1
    //   662: getfield 482	org/telegram/messenger/MessageObject:lastLineWidth	I
    //   665: invokestatic 490	java/lang/Math:max	(II)I
    //   668: ldc_w 491
    //   671: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   674: iadd
    //   675: putfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   678: aload_0
    //   679: aload_0
    //   680: getfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   683: aload_0
    //   684: getfield 493	org/telegram/ui/Cells/ChatMessageCell:timeWidth	I
    //   687: ldc_w 491
    //   690: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   693: iadd
    //   694: invokestatic 490	java/lang/Math:max	(II)I
    //   697: putfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   700: aload_0
    //   701: aload_0
    //   702: getfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   705: ldc_w 491
    //   708: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   711: isub
    //   712: putfield 1036	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   715: aload_0
    //   716: aload_1
    //   717: invokespecial 2401	org/telegram/ui/Cells/ChatMessageCell:setMessageObjectInternal	(Lorg/telegram/messenger/MessageObject;)V
    //   720: aload_1
    //   721: getfield 975	org/telegram/messenger/MessageObject:textWidth	I
    //   724: istore 10
    //   726: aload_0
    //   727: getfield 475	org/telegram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   730: ifeq +1396 -> 2126
    //   733: ldc_w 581
    //   736: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   739: istore 9
    //   741: aload_0
    //   742: iload 9
    //   744: iload 10
    //   746: iadd
    //   747: putfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   750: aload_0
    //   751: aload_1
    //   752: getfield 760	org/telegram/messenger/MessageObject:textHeight	I
    //   755: ldc_w 2402
    //   758: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   761: iadd
    //   762: aload_0
    //   763: getfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   766: iadd
    //   767: putfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   770: aload_0
    //   771: getfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   774: aload_0
    //   775: getfield 1926	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   778: invokestatic 490	java/lang/Math:max	(II)I
    //   781: aload_0
    //   782: getfield 1957	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   785: invokestatic 490	java/lang/Math:max	(II)I
    //   788: aload_0
    //   789: getfield 2008	org/telegram/ui/Cells/ChatMessageCell:replyNameWidth	I
    //   792: invokestatic 490	java/lang/Math:max	(II)I
    //   795: aload_0
    //   796: getfield 2012	org/telegram/ui/Cells/ChatMessageCell:replyTextWidth	I
    //   799: invokestatic 490	java/lang/Math:max	(II)I
    //   802: istore 12
    //   804: iconst_0
    //   805: istore 13
    //   807: aload_0
    //   808: getfield 473	org/telegram/ui/Cells/ChatMessageCell:hasLinkPreview	Z
    //   811: ifne +10 -> 821
    //   814: aload_0
    //   815: getfield 475	org/telegram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   818: ifeq +5879 -> 6697
    //   821: invokestatic 1666	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   824: ifeq +1323 -> 2147
    //   827: aload_1
    //   828: invokevirtual 857	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   831: ifeq +1301 -> 2132
    //   834: aload_0
    //   835: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   838: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   841: getfield 893	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   844: getfield 896	org/telegram/tgnet/TLRPC$Peer:channel_id	I
    //   847: ifne +19 -> 866
    //   850: aload_0
    //   851: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   854: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   857: getfield 893	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   860: getfield 2405	org/telegram/tgnet/TLRPC$Peer:chat_id	I
    //   863: ifeq +1269 -> 2132
    //   866: aload_0
    //   867: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   870: invokevirtual 854	org/telegram/messenger/MessageObject:isOut	()Z
    //   873: ifne +1259 -> 2132
    //   876: invokestatic 1671	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   879: ldc_w 2399
    //   882: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   885: isub
    //   886: istore 9
    //   888: iload 9
    //   890: istore 10
    //   892: aload_0
    //   893: getfield 2097	org/telegram/ui/Cells/ChatMessageCell:drawShareButton	Z
    //   896: ifeq +14 -> 910
    //   899: iload 9
    //   901: ldc_w 927
    //   904: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   907: isub
    //   908: istore 10
    //   910: aload_1
    //   911: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   914: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   917: getfield 782	org/telegram/tgnet/TLRPC$MessageMedia:webpage	Lorg/telegram/tgnet/TLRPC$WebPage;
    //   920: ifnull +1342 -> 2262
    //   923: aload_1
    //   924: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   927: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   930: getfield 782	org/telegram/tgnet/TLRPC$MessageMedia:webpage	Lorg/telegram/tgnet/TLRPC$WebPage;
    //   933: checkcast 863	org/telegram/tgnet/TLRPC$TL_webPage
    //   936: astore 37
    //   938: aload 37
    //   940: getfield 2406	org/telegram/tgnet/TLRPC$TL_webPage:site_name	Ljava/lang/String;
    //   943: astore 32
    //   945: aload 37
    //   947: getfield 2407	org/telegram/tgnet/TLRPC$TL_webPage:title	Ljava/lang/String;
    //   950: astore 36
    //   952: aload 37
    //   954: getfield 2410	org/telegram/tgnet/TLRPC$TL_webPage:author	Ljava/lang/String;
    //   957: astore 35
    //   959: aload 37
    //   961: getfield 2411	org/telegram/tgnet/TLRPC$TL_webPage:description	Ljava/lang/String;
    //   964: astore 34
    //   966: aload 37
    //   968: getfield 2414	org/telegram/tgnet/TLRPC$TL_webPage:photo	Lorg/telegram/tgnet/TLRPC$Photo;
    //   971: astore 33
    //   973: aload 37
    //   975: getfield 2415	org/telegram/tgnet/TLRPC$TL_webPage:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   978: astore 31
    //   980: aload 37
    //   982: getfield 2417	org/telegram/tgnet/TLRPC$TL_webPage:type	Ljava/lang/String;
    //   985: astore 30
    //   987: aload 37
    //   989: getfield 2418	org/telegram/tgnet/TLRPC$TL_webPage:duration	I
    //   992: istore 21
    //   994: iload 10
    //   996: istore 9
    //   998: aload 32
    //   1000: ifnull +50 -> 1050
    //   1003: iload 10
    //   1005: istore 9
    //   1007: aload 33
    //   1009: ifnull +41 -> 1050
    //   1012: iload 10
    //   1014: istore 9
    //   1016: aload 32
    //   1018: invokevirtual 1133	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   1021: ldc_w 2420
    //   1024: invokevirtual 1732	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1027: ifeq +23 -> 1050
    //   1030: getstatic 1676	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   1033: getfield 1684	android/graphics/Point:y	I
    //   1036: iconst_3
    //   1037: idiv
    //   1038: aload_0
    //   1039: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1042: getfield 975	org/telegram/messenger/MessageObject:textWidth	I
    //   1045: invokestatic 490	java/lang/Math:max	(II)I
    //   1048: istore 9
    //   1050: aload 30
    //   1052: ifnull +1198 -> 2250
    //   1055: aload 30
    //   1057: ldc_w 2422
    //   1060: invokevirtual 1732	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1063: ifne +25 -> 1088
    //   1066: aload 30
    //   1068: ldc_w 2424
    //   1071: invokevirtual 1732	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1074: ifne +14 -> 1088
    //   1077: aload 30
    //   1079: ldc_w 2426
    //   1082: invokevirtual 1732	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1085: ifeq +1165 -> 2250
    //   1088: iconst_1
    //   1089: istore 16
    //   1091: aload 34
    //   1093: ifnull +1163 -> 2256
    //   1096: aload 30
    //   1098: ifnull +1158 -> 2256
    //   1101: aload 30
    //   1103: ldc_w 2422
    //   1106: invokevirtual 1732	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1109: ifne +25 -> 1134
    //   1112: aload 30
    //   1114: ldc_w 2424
    //   1117: invokevirtual 1732	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1120: ifne +14 -> 1134
    //   1123: aload 30
    //   1125: ldc_w 2426
    //   1128: invokevirtual 1732	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1131: ifeq +1125 -> 2256
    //   1134: aload_0
    //   1135: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1138: getfield 1198	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   1141: ifnull +1115 -> 2256
    //   1144: iconst_1
    //   1145: istore 29
    //   1147: aload_0
    //   1148: iload 29
    //   1150: putfield 1384	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   1153: ldc_w 581
    //   1156: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1159: istore 26
    //   1161: iconst_3
    //   1162: istore 14
    //   1164: iconst_0
    //   1165: istore 10
    //   1167: iconst_0
    //   1168: istore 15
    //   1170: iload 9
    //   1172: iload 26
    //   1174: isub
    //   1175: istore 25
    //   1177: aload_0
    //   1178: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1181: getfield 1198	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   1184: ifnonnull +16 -> 1200
    //   1187: aload 33
    //   1189: ifnull +11 -> 1200
    //   1192: aload_0
    //   1193: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1196: iconst_1
    //   1197: invokevirtual 2429	org/telegram/messenger/MessageObject:generateThumbs	(Z)V
    //   1200: iload 12
    //   1202: istore 9
    //   1204: iload 13
    //   1206: istore 11
    //   1208: aload 32
    //   1210: ifnull +197 -> 1407
    //   1213: iload 10
    //   1215: istore 15
    //   1217: iload 12
    //   1219: istore 9
    //   1221: getstatic 384	org/telegram/ui/Cells/ChatMessageCell:replyNamePaint	Landroid/text/TextPaint;
    //   1224: aload 32
    //   1226: invokevirtual 1034	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   1229: f2d
    //   1230: invokestatic 987	java/lang/Math:ceil	(D)D
    //   1233: d2i
    //   1234: istore 11
    //   1236: iload 10
    //   1238: istore 15
    //   1240: iload 12
    //   1242: istore 9
    //   1244: aload_0
    //   1245: new 288	android/text/StaticLayout
    //   1248: dup
    //   1249: aload 32
    //   1251: getstatic 384	org/telegram/ui/Cells/ChatMessageCell:replyNamePaint	Landroid/text/TextPaint;
    //   1254: iload 11
    //   1256: iload 25
    //   1258: invokestatic 1044	java/lang/Math:min	(II)I
    //   1261: getstatic 1085	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1264: fconst_1
    //   1265: fconst_0
    //   1266: iconst_0
    //   1267: invokespecial 1088	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   1270: putfield 1333	org/telegram/ui/Cells/ChatMessageCell:siteNameLayout	Landroid/text/StaticLayout;
    //   1273: iload 10
    //   1275: istore 15
    //   1277: iload 12
    //   1279: istore 9
    //   1281: aload_0
    //   1282: getfield 1333	org/telegram/ui/Cells/ChatMessageCell:siteNameLayout	Landroid/text/StaticLayout;
    //   1285: aload_0
    //   1286: getfield 1333	org/telegram/ui/Cells/ChatMessageCell:siteNameLayout	Landroid/text/StaticLayout;
    //   1289: invokevirtual 1093	android/text/StaticLayout:getLineCount	()I
    //   1292: iconst_1
    //   1293: isub
    //   1294: invokevirtual 1336	android/text/StaticLayout:getLineBottom	(I)I
    //   1297: istore 11
    //   1299: iload 10
    //   1301: istore 15
    //   1303: iload 12
    //   1305: istore 9
    //   1307: aload_0
    //   1308: aload_0
    //   1309: getfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   1312: iload 11
    //   1314: iadd
    //   1315: putfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   1318: iload 10
    //   1320: istore 15
    //   1322: iload 12
    //   1324: istore 9
    //   1326: aload_0
    //   1327: aload_0
    //   1328: getfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   1331: iload 11
    //   1333: iadd
    //   1334: putfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   1337: iconst_0
    //   1338: iload 11
    //   1340: iadd
    //   1341: istore 10
    //   1343: iload 10
    //   1345: istore 15
    //   1347: iload 12
    //   1349: istore 9
    //   1351: aload_0
    //   1352: getfield 1333	org/telegram/ui/Cells/ChatMessageCell:siteNameLayout	Landroid/text/StaticLayout;
    //   1355: invokevirtual 2432	android/text/StaticLayout:getWidth	()I
    //   1358: istore 11
    //   1360: iload 10
    //   1362: istore 15
    //   1364: iload 12
    //   1366: istore 9
    //   1368: iload 12
    //   1370: iload 11
    //   1372: iload 26
    //   1374: iadd
    //   1375: invokestatic 490	java/lang/Math:max	(II)I
    //   1378: istore 12
    //   1380: iload 10
    //   1382: istore 15
    //   1384: iload 12
    //   1386: istore 9
    //   1388: iconst_0
    //   1389: iload 11
    //   1391: iload 26
    //   1393: iadd
    //   1394: invokestatic 490	java/lang/Math:max	(II)I
    //   1397: istore 11
    //   1399: iload 12
    //   1401: istore 9
    //   1403: iload 10
    //   1405: istore 15
    //   1407: iconst_0
    //   1408: istore 23
    //   1410: iconst_0
    //   1411: istore 17
    //   1413: iconst_0
    //   1414: istore 22
    //   1416: iconst_0
    //   1417: istore 18
    //   1419: iload 9
    //   1421: istore 10
    //   1423: iload 11
    //   1425: istore 13
    //   1427: iload 14
    //   1429: istore 12
    //   1431: iload 22
    //   1433: istore 14
    //   1435: aload 36
    //   1437: ifnull +1044 -> 2481
    //   1440: aload_0
    //   1441: ldc_w 2433
    //   1444: putfield 1367	org/telegram/ui/Cells/ChatMessageCell:titleX	I
    //   1447: aload_0
    //   1448: getfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   1451: ifeq +29 -> 1480
    //   1454: aload_0
    //   1455: aload_0
    //   1456: getfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   1459: fconst_2
    //   1460: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1463: iadd
    //   1464: putfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   1467: aload_0
    //   1468: aload_0
    //   1469: getfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   1472: fconst_2
    //   1473: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1476: iadd
    //   1477: putfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   1480: iconst_0
    //   1481: istore 22
    //   1483: aload_0
    //   1484: getfield 1384	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   1487: ifeq +8 -> 1495
    //   1490: aload 34
    //   1492: ifnonnull +880 -> 2372
    //   1495: aload_0
    //   1496: aload 36
    //   1498: getstatic 384	org/telegram/ui/Cells/ChatMessageCell:replyNamePaint	Landroid/text/TextPaint;
    //   1501: iload 25
    //   1503: getstatic 1085	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1506: fconst_1
    //   1507: fconst_1
    //   1508: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1511: i2f
    //   1512: iconst_0
    //   1513: getstatic 1075	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   1516: iload 25
    //   1518: iconst_4
    //   1519: invokestatic 1175	org/telegram/ui/Components/StaticLayoutEx:createStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZLandroid/text/TextUtils$TruncateAt;II)Landroid/text/StaticLayout;
    //   1522: putfield 1365	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   1525: iconst_3
    //   1526: istore 10
    //   1528: iload 9
    //   1530: istore 12
    //   1532: iload 11
    //   1534: istore 17
    //   1536: iload 23
    //   1538: istore 13
    //   1540: aload_0
    //   1541: getfield 1365	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   1544: aload_0
    //   1545: getfield 1365	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   1548: invokevirtual 1093	android/text/StaticLayout:getLineCount	()I
    //   1551: iconst_1
    //   1552: isub
    //   1553: invokevirtual 1336	android/text/StaticLayout:getLineBottom	(I)I
    //   1556: istore 14
    //   1558: iload 9
    //   1560: istore 12
    //   1562: iload 11
    //   1564: istore 17
    //   1566: iload 23
    //   1568: istore 13
    //   1570: aload_0
    //   1571: aload_0
    //   1572: getfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   1575: iload 14
    //   1577: iadd
    //   1578: putfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   1581: iload 9
    //   1583: istore 12
    //   1585: iload 11
    //   1587: istore 17
    //   1589: iload 23
    //   1591: istore 13
    //   1593: aload_0
    //   1594: aload_0
    //   1595: getfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   1598: iload 14
    //   1600: iadd
    //   1601: putfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   1604: iconst_0
    //   1605: istore 23
    //   1607: iload 18
    //   1609: istore 14
    //   1611: iload 9
    //   1613: istore 12
    //   1615: iload 11
    //   1617: istore 17
    //   1619: iload 14
    //   1621: istore 13
    //   1623: iload 23
    //   1625: aload_0
    //   1626: getfield 1365	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   1629: invokevirtual 1093	android/text/StaticLayout:getLineCount	()I
    //   1632: if_icmpge +1424 -> 3056
    //   1635: iload 9
    //   1637: istore 12
    //   1639: iload 11
    //   1641: istore 17
    //   1643: iload 14
    //   1645: istore 13
    //   1647: aload_0
    //   1648: getfield 1365	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   1651: iload 23
    //   1653: invokevirtual 651	android/text/StaticLayout:getLineLeft	(I)F
    //   1656: f2i
    //   1657: istore 27
    //   1659: iload 27
    //   1661: ifeq +6 -> 1667
    //   1664: iconst_1
    //   1665: istore 14
    //   1667: iload 9
    //   1669: istore 12
    //   1671: iload 11
    //   1673: istore 17
    //   1675: iload 14
    //   1677: istore 13
    //   1679: aload_0
    //   1680: getfield 1367	org/telegram/ui/Cells/ChatMessageCell:titleX	I
    //   1683: ldc_w 2433
    //   1686: if_icmpne +732 -> 2418
    //   1689: iload 9
    //   1691: istore 12
    //   1693: iload 11
    //   1695: istore 17
    //   1697: iload 14
    //   1699: istore 13
    //   1701: aload_0
    //   1702: iload 27
    //   1704: ineg
    //   1705: putfield 1367	org/telegram/ui/Cells/ChatMessageCell:titleX	I
    //   1708: iload 27
    //   1710: ifeq +1313 -> 3023
    //   1713: iload 9
    //   1715: istore 12
    //   1717: iload 11
    //   1719: istore 17
    //   1721: iload 14
    //   1723: istore 13
    //   1725: aload_0
    //   1726: getfield 1365	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   1729: invokevirtual 2432	android/text/StaticLayout:getWidth	()I
    //   1732: iload 27
    //   1734: isub
    //   1735: istore 18
    //   1737: iload 23
    //   1739: iload 22
    //   1741: if_icmplt +35 -> 1776
    //   1744: iload 18
    //   1746: istore 24
    //   1748: iload 27
    //   1750: ifeq +49 -> 1799
    //   1753: iload 18
    //   1755: istore 24
    //   1757: iload 9
    //   1759: istore 12
    //   1761: iload 11
    //   1763: istore 17
    //   1765: iload 14
    //   1767: istore 13
    //   1769: aload_0
    //   1770: getfield 1384	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   1773: ifeq +26 -> 1799
    //   1776: iload 9
    //   1778: istore 12
    //   1780: iload 11
    //   1782: istore 17
    //   1784: iload 14
    //   1786: istore 13
    //   1788: iload 18
    //   1790: ldc_w 2434
    //   1793: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1796: iadd
    //   1797: istore 24
    //   1799: iload 9
    //   1801: istore 12
    //   1803: iload 11
    //   1805: istore 17
    //   1807: iload 14
    //   1809: istore 13
    //   1811: iload 9
    //   1813: iload 24
    //   1815: iload 26
    //   1817: iadd
    //   1818: invokestatic 490	java/lang/Math:max	(II)I
    //   1821: istore 9
    //   1823: iload 9
    //   1825: istore 12
    //   1827: iload 11
    //   1829: istore 17
    //   1831: iload 14
    //   1833: istore 13
    //   1835: iload 11
    //   1837: iload 24
    //   1839: iload 26
    //   1841: iadd
    //   1842: invokestatic 490	java/lang/Math:max	(II)I
    //   1845: istore 11
    //   1847: iload 23
    //   1849: iconst_1
    //   1850: iadd
    //   1851: istore 23
    //   1853: goto -242 -> 1611
    //   1856: iconst_0
    //   1857: istore 19
    //   1859: goto -1823 -> 36
    //   1862: iconst_0
    //   1863: istore 8
    //   1865: goto -1811 -> 54
    //   1868: iconst_0
    //   1869: istore 28
    //   1871: goto -1792 -> 79
    //   1874: aload_1
    //   1875: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1878: getfield 893	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   1881: getfield 896	org/telegram/tgnet/TLRPC$Peer:channel_id	I
    //   1884: ifeq +34 -> 1918
    //   1887: aload_1
    //   1888: invokevirtual 575	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   1891: ifne +27 -> 1918
    //   1894: iconst_1
    //   1895: istore 29
    //   1897: aload_0
    //   1898: iload 29
    //   1900: putfield 1791	org/telegram/ui/Cells/ChatMessageCell:drawName	Z
    //   1903: invokestatic 1671	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   1906: ldc_w 2435
    //   1909: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1912: isub
    //   1913: istore 8
    //   1915: goto -1413 -> 502
    //   1918: iconst_0
    //   1919: istore 29
    //   1921: goto -24 -> 1897
    //   1924: aload_0
    //   1925: getfield 1668	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   1928: ifeq +49 -> 1977
    //   1931: aload_1
    //   1932: invokevirtual 575	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   1935: ifne +42 -> 1977
    //   1938: aload_1
    //   1939: invokevirtual 857	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   1942: ifeq +35 -> 1977
    //   1945: getstatic 1676	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   1948: getfield 1681	android/graphics/Point:x	I
    //   1951: getstatic 1676	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   1954: getfield 1684	android/graphics/Point:y	I
    //   1957: invokestatic 1044	java/lang/Math:min	(II)I
    //   1960: ldc_w 2399
    //   1963: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1966: isub
    //   1967: istore 8
    //   1969: aload_0
    //   1970: iconst_1
    //   1971: putfield 1791	org/telegram/ui/Cells/ChatMessageCell:drawName	Z
    //   1974: goto -1472 -> 502
    //   1977: getstatic 1676	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   1980: getfield 1681	android/graphics/Point:x	I
    //   1983: getstatic 1676	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   1986: getfield 1684	android/graphics/Point:y	I
    //   1989: invokestatic 1044	java/lang/Math:min	(II)I
    //   1992: ldc_w 2435
    //   1995: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1998: isub
    //   1999: istore 8
    //   2001: aload_1
    //   2002: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2005: getfield 893	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   2008: getfield 896	org/telegram/tgnet/TLRPC$Peer:channel_id	I
    //   2011: ifeq +22 -> 2033
    //   2014: aload_1
    //   2015: invokevirtual 575	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   2018: ifne +15 -> 2033
    //   2021: iconst_1
    //   2022: istore 29
    //   2024: aload_0
    //   2025: iload 29
    //   2027: putfield 1791	org/telegram/ui/Cells/ChatMessageCell:drawName	Z
    //   2030: goto -1528 -> 502
    //   2033: iconst_0
    //   2034: istore 29
    //   2036: goto -12 -> 2024
    //   2039: iconst_0
    //   2040: istore 29
    //   2042: goto -1462 -> 580
    //   2045: iconst_0
    //   2046: istore 29
    //   2048: goto -1430 -> 618
    //   2051: aload_0
    //   2052: getfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   2055: aload_1
    //   2056: getfield 482	org/telegram/messenger/MessageObject:lastLineWidth	I
    //   2059: isub
    //   2060: istore 9
    //   2062: iload 9
    //   2064: iflt +34 -> 2098
    //   2067: iload 9
    //   2069: iload 20
    //   2071: if_icmpgt +27 -> 2098
    //   2074: aload_0
    //   2075: aload_0
    //   2076: getfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   2079: iload 20
    //   2081: iadd
    //   2082: iload 9
    //   2084: isub
    //   2085: ldc_w 491
    //   2088: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2091: iadd
    //   2092: putfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   2095: goto -1395 -> 700
    //   2098: aload_0
    //   2099: aload_0
    //   2100: getfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   2103: aload_1
    //   2104: getfield 482	org/telegram/messenger/MessageObject:lastLineWidth	I
    //   2107: iload 20
    //   2109: iadd
    //   2110: invokestatic 490	java/lang/Math:max	(II)I
    //   2113: ldc_w 491
    //   2116: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2119: iadd
    //   2120: putfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   2123: goto -1423 -> 700
    //   2126: iconst_0
    //   2127: istore 9
    //   2129: goto -1388 -> 741
    //   2132: invokestatic 1671	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   2135: ldc_w 2435
    //   2138: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2141: isub
    //   2142: istore 9
    //   2144: goto -1256 -> 888
    //   2147: aload_1
    //   2148: invokevirtual 857	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   2151: ifeq +72 -> 2223
    //   2154: aload_0
    //   2155: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2158: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2161: getfield 893	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   2164: getfield 896	org/telegram/tgnet/TLRPC$Peer:channel_id	I
    //   2167: ifne +19 -> 2186
    //   2170: aload_0
    //   2171: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2174: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2177: getfield 893	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   2180: getfield 2405	org/telegram/tgnet/TLRPC$Peer:chat_id	I
    //   2183: ifeq +40 -> 2223
    //   2186: aload_0
    //   2187: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2190: invokevirtual 575	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   2193: ifne +30 -> 2223
    //   2196: getstatic 1676	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   2199: getfield 1681	android/graphics/Point:x	I
    //   2202: getstatic 1676	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   2205: getfield 1684	android/graphics/Point:y	I
    //   2208: invokestatic 1044	java/lang/Math:min	(II)I
    //   2211: ldc_w 2399
    //   2214: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2217: isub
    //   2218: istore 9
    //   2220: goto -1332 -> 888
    //   2223: getstatic 1676	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   2226: getfield 1681	android/graphics/Point:x	I
    //   2229: getstatic 1676	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   2232: getfield 1684	android/graphics/Point:y	I
    //   2235: invokestatic 1044	java/lang/Math:min	(II)I
    //   2238: ldc_w 2435
    //   2241: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2244: isub
    //   2245: istore 9
    //   2247: goto -1359 -> 888
    //   2250: iconst_0
    //   2251: istore 16
    //   2253: goto -1162 -> 1091
    //   2256: iconst_0
    //   2257: istore 29
    //   2259: goto -1112 -> 1147
    //   2262: aload_1
    //   2263: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2266: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2269: getfield 1988	org/telegram/tgnet/TLRPC$MessageMedia:game	Lorg/telegram/tgnet/TLRPC$TL_game;
    //   2272: astore 31
    //   2274: aload 31
    //   2276: getfield 1991	org/telegram/tgnet/TLRPC$TL_game:title	Ljava/lang/String;
    //   2279: astore 32
    //   2281: aconst_null
    //   2282: astore 36
    //   2284: aload_1
    //   2285: getfield 972	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   2288: invokestatic 799	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   2291: ifeq +58 -> 2349
    //   2294: aload 31
    //   2296: getfield 2436	org/telegram/tgnet/TLRPC$TL_game:description	Ljava/lang/String;
    //   2299: astore 30
    //   2301: aload 31
    //   2303: getfield 2437	org/telegram/tgnet/TLRPC$TL_game:photo	Lorg/telegram/tgnet/TLRPC$Photo;
    //   2306: astore 33
    //   2308: aconst_null
    //   2309: astore 35
    //   2311: aload 31
    //   2313: getfield 2438	org/telegram/tgnet/TLRPC$TL_game:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   2316: astore 31
    //   2318: iconst_0
    //   2319: istore 21
    //   2321: ldc_w 2439
    //   2324: astore 37
    //   2326: aload_0
    //   2327: iconst_0
    //   2328: putfield 1384	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   2331: iconst_0
    //   2332: istore 16
    //   2334: aload 30
    //   2336: astore 34
    //   2338: iload 10
    //   2340: istore 9
    //   2342: aload 37
    //   2344: astore 30
    //   2346: goto -1193 -> 1153
    //   2349: aconst_null
    //   2350: astore 30
    //   2352: goto -51 -> 2301
    //   2355: astore 37
    //   2357: ldc_w 695
    //   2360: aload 37
    //   2362: invokestatic 701	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2365: iload 13
    //   2367: istore 11
    //   2369: goto -962 -> 1407
    //   2372: iconst_3
    //   2373: istore 22
    //   2375: aload_0
    //   2376: aload 36
    //   2378: getstatic 384	org/telegram/ui/Cells/ChatMessageCell:replyNamePaint	Landroid/text/TextPaint;
    //   2381: iload 25
    //   2383: iload 25
    //   2385: ldc_w 2434
    //   2388: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2391: isub
    //   2392: iconst_3
    //   2393: iconst_4
    //   2394: invokestatic 2441	org/telegram/ui/Cells/ChatMessageCell:generateStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;IIII)Landroid/text/StaticLayout;
    //   2397: putfield 1365	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   2400: aload_0
    //   2401: getfield 1365	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   2404: invokevirtual 1093	android/text/StaticLayout:getLineCount	()I
    //   2407: istore 10
    //   2409: iconst_3
    //   2410: iload 10
    //   2412: isub
    //   2413: istore 10
    //   2415: goto -887 -> 1528
    //   2418: iload 9
    //   2420: istore 12
    //   2422: iload 11
    //   2424: istore 17
    //   2426: iload 14
    //   2428: istore 13
    //   2430: aload_0
    //   2431: aload_0
    //   2432: getfield 1367	org/telegram/ui/Cells/ChatMessageCell:titleX	I
    //   2435: iload 27
    //   2437: ineg
    //   2438: invokestatic 490	java/lang/Math:max	(II)I
    //   2441: putfield 1367	org/telegram/ui/Cells/ChatMessageCell:titleX	I
    //   2444: goto -736 -> 1708
    //   2447: astore 36
    //   2449: iload 17
    //   2451: istore 11
    //   2453: iload 12
    //   2455: istore 9
    //   2457: ldc_w 695
    //   2460: aload 36
    //   2462: invokestatic 701	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2465: iload 13
    //   2467: istore 14
    //   2469: iload 10
    //   2471: istore 12
    //   2473: iload 11
    //   2475: istore 13
    //   2477: iload 9
    //   2479: istore 10
    //   2481: iconst_0
    //   2482: istore 9
    //   2484: iconst_0
    //   2485: istore 17
    //   2487: iconst_0
    //   2488: istore 18
    //   2490: iconst_0
    //   2491: istore 11
    //   2493: aload 35
    //   2495: ifnull +9749 -> 12244
    //   2498: aload_0
    //   2499: getfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2502: ifeq +29 -> 2531
    //   2505: aload_0
    //   2506: aload_0
    //   2507: getfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2510: fconst_2
    //   2511: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2514: iadd
    //   2515: putfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2518: aload_0
    //   2519: aload_0
    //   2520: getfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2523: fconst_2
    //   2524: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2527: iadd
    //   2528: putfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2531: iload 12
    //   2533: iconst_3
    //   2534: if_icmpne +537 -> 3071
    //   2537: aload_0
    //   2538: getfield 1384	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   2541: ifeq +8 -> 2549
    //   2544: aload 34
    //   2546: ifnonnull +525 -> 3071
    //   2549: aload_0
    //   2550: new 288	android/text/StaticLayout
    //   2553: dup
    //   2554: aload 35
    //   2556: getstatic 384	org/telegram/ui/Cells/ChatMessageCell:replyNamePaint	Landroid/text/TextPaint;
    //   2559: iload 25
    //   2561: getstatic 1085	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   2564: fconst_1
    //   2565: fconst_0
    //   2566: iconst_0
    //   2567: invokespecial 1088	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   2570: putfield 1369	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   2573: iload 12
    //   2575: istore 9
    //   2577: iload 18
    //   2579: istore 17
    //   2581: iload 10
    //   2583: istore 12
    //   2585: aload_0
    //   2586: getfield 1369	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   2589: aload_0
    //   2590: getfield 1369	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   2593: invokevirtual 1093	android/text/StaticLayout:getLineCount	()I
    //   2596: iconst_1
    //   2597: isub
    //   2598: invokevirtual 1336	android/text/StaticLayout:getLineBottom	(I)I
    //   2601: istore 22
    //   2603: iload 18
    //   2605: istore 17
    //   2607: iload 10
    //   2609: istore 12
    //   2611: aload_0
    //   2612: aload_0
    //   2613: getfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2616: iload 22
    //   2618: iadd
    //   2619: putfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2622: iload 18
    //   2624: istore 17
    //   2626: iload 10
    //   2628: istore 12
    //   2630: aload_0
    //   2631: aload_0
    //   2632: getfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2635: iload 22
    //   2637: iadd
    //   2638: putfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2641: iload 18
    //   2643: istore 17
    //   2645: iload 10
    //   2647: istore 12
    //   2649: aload_0
    //   2650: getfield 1369	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   2653: iconst_0
    //   2654: invokevirtual 651	android/text/StaticLayout:getLineLeft	(I)F
    //   2657: f2i
    //   2658: istore 22
    //   2660: iload 18
    //   2662: istore 17
    //   2664: iload 10
    //   2666: istore 12
    //   2668: aload_0
    //   2669: iload 22
    //   2671: ineg
    //   2672: putfield 1371	org/telegram/ui/Cells/ChatMessageCell:authorX	I
    //   2675: iload 22
    //   2677: ifeq +439 -> 3116
    //   2680: iload 18
    //   2682: istore 17
    //   2684: iload 10
    //   2686: istore 12
    //   2688: aload_0
    //   2689: getfield 1369	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   2692: invokevirtual 2432	android/text/StaticLayout:getWidth	()I
    //   2695: iload 22
    //   2697: isub
    //   2698: istore 18
    //   2700: iconst_1
    //   2701: istore 11
    //   2703: iload 11
    //   2705: istore 17
    //   2707: iload 10
    //   2709: istore 12
    //   2711: iload 10
    //   2713: iload 18
    //   2715: iload 26
    //   2717: iadd
    //   2718: invokestatic 490	java/lang/Math:max	(II)I
    //   2721: istore 10
    //   2723: iload 11
    //   2725: istore 17
    //   2727: iload 10
    //   2729: istore 12
    //   2731: iload 13
    //   2733: iload 18
    //   2735: iload 26
    //   2737: iadd
    //   2738: invokestatic 490	java/lang/Math:max	(II)I
    //   2741: istore 18
    //   2743: iload 18
    //   2745: istore 17
    //   2747: iload 11
    //   2749: istore 12
    //   2751: iload 9
    //   2753: istore 11
    //   2755: iload 10
    //   2757: istore 9
    //   2759: aload 34
    //   2761: ifnull +470 -> 3231
    //   2764: iload 10
    //   2766: istore 13
    //   2768: aload_0
    //   2769: iconst_0
    //   2770: putfield 725	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   2773: iload 10
    //   2775: istore 13
    //   2777: aload_0
    //   2778: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2781: invokevirtual 2444	org/telegram/messenger/MessageObject:generateLinkDescription	()V
    //   2784: iload 10
    //   2786: istore 13
    //   2788: aload_0
    //   2789: getfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2792: ifeq +37 -> 2829
    //   2795: iload 10
    //   2797: istore 13
    //   2799: aload_0
    //   2800: aload_0
    //   2801: getfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2804: fconst_2
    //   2805: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2808: iadd
    //   2809: putfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2812: iload 10
    //   2814: istore 13
    //   2816: aload_0
    //   2817: aload_0
    //   2818: getfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2821: fconst_2
    //   2822: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2825: iadd
    //   2826: putfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2829: iconst_0
    //   2830: istore 9
    //   2832: iload 11
    //   2834: iconst_3
    //   2835: if_icmpne +338 -> 3173
    //   2838: iload 10
    //   2840: istore 13
    //   2842: aload_0
    //   2843: getfield 1384	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   2846: ifne +327 -> 3173
    //   2849: iload 10
    //   2851: istore 13
    //   2853: aload_0
    //   2854: aload_1
    //   2855: getfield 728	org/telegram/messenger/MessageObject:linkDescription	Ljava/lang/CharSequence;
    //   2858: getstatic 386	org/telegram/ui/Cells/ChatMessageCell:replyTextPaint	Landroid/text/TextPaint;
    //   2861: iload 25
    //   2863: getstatic 1085	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   2866: fconst_1
    //   2867: fconst_1
    //   2868: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2871: i2f
    //   2872: iconst_0
    //   2873: getstatic 1075	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   2876: iload 25
    //   2878: bipush 6
    //   2880: invokestatic 1175	org/telegram/ui/Components/StaticLayoutEx:createStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZLandroid/text/TextUtils$TruncateAt;II)Landroid/text/StaticLayout;
    //   2883: putfield 719	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   2886: iload 9
    //   2888: istore 11
    //   2890: iload 10
    //   2892: istore 13
    //   2894: aload_0
    //   2895: getfield 719	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   2898: aload_0
    //   2899: getfield 719	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   2902: invokevirtual 1093	android/text/StaticLayout:getLineCount	()I
    //   2905: iconst_1
    //   2906: isub
    //   2907: invokevirtual 1336	android/text/StaticLayout:getLineBottom	(I)I
    //   2910: istore 9
    //   2912: iload 10
    //   2914: istore 13
    //   2916: aload_0
    //   2917: aload_0
    //   2918: getfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2921: iload 9
    //   2923: iadd
    //   2924: putfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2927: iload 10
    //   2929: istore 13
    //   2931: aload_0
    //   2932: aload_0
    //   2933: getfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2936: iload 9
    //   2938: iadd
    //   2939: putfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2942: iconst_0
    //   2943: istore 18
    //   2945: iconst_0
    //   2946: istore 9
    //   2948: iload 10
    //   2950: istore 13
    //   2952: iload 9
    //   2954: aload_0
    //   2955: getfield 719	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   2958: invokevirtual 1093	android/text/StaticLayout:getLineCount	()I
    //   2961: if_icmpge +9298 -> 12259
    //   2964: iload 10
    //   2966: istore 13
    //   2968: aload_0
    //   2969: getfield 719	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   2972: iload 9
    //   2974: invokevirtual 651	android/text/StaticLayout:getLineLeft	(I)F
    //   2977: f2d
    //   2978: invokestatic 987	java/lang/Math:ceil	(D)D
    //   2981: d2i
    //   2982: istore 22
    //   2984: iload 22
    //   2986: ifeq +28 -> 3014
    //   2989: iconst_1
    //   2990: istore 18
    //   2992: iload 10
    //   2994: istore 13
    //   2996: aload_0
    //   2997: getfield 725	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   3000: ifne +1585 -> 4585
    //   3003: iload 10
    //   3005: istore 13
    //   3007: aload_0
    //   3008: iload 22
    //   3010: ineg
    //   3011: putfield 725	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   3014: iload 9
    //   3016: iconst_1
    //   3017: iadd
    //   3018: istore 9
    //   3020: goto -72 -> 2948
    //   3023: iload 9
    //   3025: istore 12
    //   3027: iload 11
    //   3029: istore 17
    //   3031: iload 14
    //   3033: istore 13
    //   3035: aload_0
    //   3036: getfield 1365	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   3039: iload 23
    //   3041: invokevirtual 654	android/text/StaticLayout:getLineWidth	(I)F
    //   3044: f2d
    //   3045: invokestatic 987	java/lang/Math:ceil	(D)D
    //   3048: dstore_2
    //   3049: dload_2
    //   3050: d2i
    //   3051: istore 18
    //   3053: goto -1316 -> 1737
    //   3056: iload 10
    //   3058: istore 12
    //   3060: iload 9
    //   3062: istore 10
    //   3064: iload 11
    //   3066: istore 13
    //   3068: goto -587 -> 2481
    //   3071: aload_0
    //   3072: aload 35
    //   3074: getstatic 384	org/telegram/ui/Cells/ChatMessageCell:replyNamePaint	Landroid/text/TextPaint;
    //   3077: iload 25
    //   3079: iload 25
    //   3081: ldc_w 2434
    //   3084: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3087: isub
    //   3088: iload 12
    //   3090: iconst_1
    //   3091: invokestatic 2441	org/telegram/ui/Cells/ChatMessageCell:generateStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;IIII)Landroid/text/StaticLayout;
    //   3094: putfield 1369	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   3097: aload_0
    //   3098: getfield 1369	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   3101: invokevirtual 1093	android/text/StaticLayout:getLineCount	()I
    //   3104: istore 9
    //   3106: iload 12
    //   3108: iload 9
    //   3110: isub
    //   3111: istore 9
    //   3113: goto -536 -> 2577
    //   3116: iload 18
    //   3118: istore 17
    //   3120: iload 10
    //   3122: istore 12
    //   3124: aload_0
    //   3125: getfield 1369	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   3128: iconst_0
    //   3129: invokevirtual 654	android/text/StaticLayout:getLineWidth	(I)F
    //   3132: f2d
    //   3133: invokestatic 987	java/lang/Math:ceil	(D)D
    //   3136: dstore_2
    //   3137: dload_2
    //   3138: d2i
    //   3139: istore 18
    //   3141: goto -438 -> 2703
    //   3144: astore 35
    //   3146: iload 12
    //   3148: istore 9
    //   3150: ldc_w 695
    //   3153: aload 35
    //   3155: invokestatic 701	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   3158: iload 9
    //   3160: istore 11
    //   3162: iload 17
    //   3164: istore 12
    //   3166: iload 13
    //   3168: istore 17
    //   3170: goto -415 -> 2755
    //   3173: iload 11
    //   3175: istore 9
    //   3177: iload 10
    //   3179: istore 13
    //   3181: aload_0
    //   3182: aload_1
    //   3183: getfield 728	org/telegram/messenger/MessageObject:linkDescription	Ljava/lang/CharSequence;
    //   3186: getstatic 386	org/telegram/ui/Cells/ChatMessageCell:replyTextPaint	Landroid/text/TextPaint;
    //   3189: iload 25
    //   3191: iload 25
    //   3193: ldc_w 2434
    //   3196: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3199: isub
    //   3200: iload 11
    //   3202: bipush 6
    //   3204: invokestatic 2441	org/telegram/ui/Cells/ChatMessageCell:generateStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;IIII)Landroid/text/StaticLayout;
    //   3207: putfield 719	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   3210: iload 9
    //   3212: istore 11
    //   3214: goto -324 -> 2890
    //   3217: astore 34
    //   3219: ldc_w 695
    //   3222: aload 34
    //   3224: invokestatic 701	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   3227: iload 13
    //   3229: istore 9
    //   3231: iload 16
    //   3233: istore 12
    //   3235: iload 16
    //   3237: ifeq +44 -> 3281
    //   3240: aload_0
    //   3241: getfield 719	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   3244: ifnull +29 -> 3273
    //   3247: iload 16
    //   3249: istore 12
    //   3251: aload_0
    //   3252: getfield 719	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   3255: ifnull +26 -> 3281
    //   3258: iload 16
    //   3260: istore 12
    //   3262: aload_0
    //   3263: getfield 719	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   3266: invokevirtual 1093	android/text/StaticLayout:getLineCount	()I
    //   3269: iconst_1
    //   3270: if_icmpne +11 -> 3281
    //   3273: iconst_0
    //   3274: istore 12
    //   3276: aload_0
    //   3277: iconst_0
    //   3278: putfield 1384	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   3281: iload 12
    //   3283: ifeq +1608 -> 4891
    //   3286: ldc_w 766
    //   3289: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3292: istore 10
    //   3294: aload 31
    //   3296: ifnull +2557 -> 5853
    //   3299: aload 31
    //   3301: invokestatic 2447	org/telegram/messenger/MessageObject:isGifDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   3304: ifeq +1609 -> 4913
    //   3307: invokestatic 432	org/telegram/messenger/MediaController:getInstance	()Lorg/telegram/messenger/MediaController;
    //   3310: invokevirtual 785	org/telegram/messenger/MediaController:canAutoplayGifs	()Z
    //   3313: ifne +8 -> 3321
    //   3316: aload_1
    //   3317: fconst_1
    //   3318: putfield 806	org/telegram/messenger/MessageObject:audioProgress	F
    //   3321: aload_0
    //   3322: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   3325: astore 33
    //   3327: aload_1
    //   3328: getfield 806	org/telegram/messenger/MessageObject:audioProgress	F
    //   3331: fconst_1
    //   3332: fcmpl
    //   3333: ifeq +1565 -> 4898
    //   3336: iconst_1
    //   3337: istore 29
    //   3339: aload 33
    //   3341: iload 29
    //   3343: invokevirtual 809	org/telegram/messenger/ImageReceiver:setAllowStartAnimation	(Z)V
    //   3346: aload_0
    //   3347: aload 31
    //   3349: getfield 1142	org/telegram/tgnet/TLRPC$Document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3352: putfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3355: aload_0
    //   3356: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3359: ifnull +148 -> 3507
    //   3362: aload_0
    //   3363: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3366: getfield 2450	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   3369: ifeq +13 -> 3382
    //   3372: aload_0
    //   3373: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3376: getfield 2453	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   3379: ifne +128 -> 3507
    //   3382: iconst_0
    //   3383: istore 11
    //   3385: iload 11
    //   3387: aload 31
    //   3389: getfield 1020	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   3392: invokevirtual 584	java/util/ArrayList:size	()I
    //   3395: if_icmpge +58 -> 3453
    //   3398: aload 31
    //   3400: getfield 1020	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   3403: iload 11
    //   3405: invokevirtual 588	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   3408: checkcast 1022	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   3411: astore 33
    //   3413: aload 33
    //   3415: instanceof 2455
    //   3418: ifne +11 -> 3429
    //   3421: aload 33
    //   3423: instanceof 1115
    //   3426: ifeq +1478 -> 4904
    //   3429: aload_0
    //   3430: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3433: aload 33
    //   3435: getfield 2456	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   3438: putfield 2450	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   3441: aload_0
    //   3442: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3445: aload 33
    //   3447: getfield 2457	org/telegram/tgnet/TLRPC$DocumentAttribute:h	I
    //   3450: putfield 2453	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   3453: aload_0
    //   3454: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3457: getfield 2450	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   3460: ifeq +13 -> 3473
    //   3463: aload_0
    //   3464: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3467: getfield 2453	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   3470: ifne +37 -> 3507
    //   3473: aload_0
    //   3474: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3477: astore 33
    //   3479: aload_0
    //   3480: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3483: astore 34
    //   3485: ldc_w 2458
    //   3488: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3491: istore 11
    //   3493: aload 34
    //   3495: iload 11
    //   3497: putfield 2453	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   3500: aload 33
    //   3502: iload 11
    //   3504: putfield 2450	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   3507: aload_0
    //   3508: iconst_2
    //   3509: putfield 497	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   3512: iload 9
    //   3514: istore 11
    //   3516: aload_0
    //   3517: getfield 497	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   3520: iconst_5
    //   3521: if_icmpeq +608 -> 4129
    //   3524: aload_0
    //   3525: getfield 497	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   3528: iconst_3
    //   3529: if_icmpeq +600 -> 4129
    //   3532: aload_0
    //   3533: getfield 497	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   3536: iconst_1
    //   3537: if_icmpeq +592 -> 4129
    //   3540: aload_0
    //   3541: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3544: ifnull +3109 -> 6653
    //   3547: aload 30
    //   3549: ifnull +2430 -> 5979
    //   3552: aload 30
    //   3554: ldc_w 2459
    //   3557: invokevirtual 1732	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3560: ifne +42 -> 3602
    //   3563: aload 30
    //   3565: ldc_w 2460
    //   3568: invokevirtual 1732	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3571: ifeq +12 -> 3583
    //   3574: aload_0
    //   3575: getfield 497	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   3578: bipush 6
    //   3580: if_icmpne +22 -> 3602
    //   3583: aload 30
    //   3585: ldc_w 2462
    //   3588: invokevirtual 1732	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3591: ifne +11 -> 3602
    //   3594: aload_0
    //   3595: getfield 497	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   3598: iconst_4
    //   3599: if_icmpne +2380 -> 5979
    //   3602: iconst_1
    //   3603: istore 29
    //   3605: aload_0
    //   3606: iload 29
    //   3608: putfield 765	org/telegram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   3611: aload_0
    //   3612: getfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   3615: ifeq +29 -> 3644
    //   3618: aload_0
    //   3619: aload_0
    //   3620: getfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   3623: fconst_2
    //   3624: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3627: iadd
    //   3628: putfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   3631: aload_0
    //   3632: aload_0
    //   3633: getfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   3636: fconst_2
    //   3637: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3640: iadd
    //   3641: putfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   3644: iload 10
    //   3646: istore 9
    //   3648: aload_0
    //   3649: getfield 497	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   3652: bipush 6
    //   3654: if_icmpne +20 -> 3674
    //   3657: invokestatic 1666	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   3660: ifeq +2325 -> 5985
    //   3663: invokestatic 1671	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   3666: i2f
    //   3667: ldc_w 2463
    //   3670: fmul
    //   3671: f2i
    //   3672: istore 9
    //   3674: iload 11
    //   3676: iload 9
    //   3678: iload 26
    //   3680: iadd
    //   3681: invokestatic 490	java/lang/Math:max	(II)I
    //   3684: istore 13
    //   3686: aload_0
    //   3687: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3690: iconst_m1
    //   3691: putfield 1246	org/telegram/tgnet/TLRPC$PhotoSize:size	I
    //   3694: aload_0
    //   3695: getfield 1245	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3698: ifnull +11 -> 3709
    //   3701: aload_0
    //   3702: getfield 1245	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3705: iconst_m1
    //   3706: putfield 1246	org/telegram/tgnet/TLRPC$PhotoSize:size	I
    //   3709: iload 12
    //   3711: ifeq +2291 -> 6002
    //   3714: iload 9
    //   3716: istore 11
    //   3718: iload 9
    //   3720: istore 10
    //   3722: iload 11
    //   3724: istore 9
    //   3726: aload_0
    //   3727: getfield 1384	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   3730: ifeq +2460 -> 6190
    //   3733: ldc_w 946
    //   3736: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3739: iload 15
    //   3741: iadd
    //   3742: aload_0
    //   3743: getfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   3746: if_icmple +46 -> 3792
    //   3749: aload_0
    //   3750: aload_0
    //   3751: getfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   3754: ldc_w 946
    //   3757: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3760: iload 15
    //   3762: iadd
    //   3763: aload_0
    //   3764: getfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   3767: isub
    //   3768: ldc_w 763
    //   3771: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3774: iadd
    //   3775: iadd
    //   3776: putfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   3779: aload_0
    //   3780: ldc_w 946
    //   3783: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3786: iload 15
    //   3788: iadd
    //   3789: putfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   3792: aload_0
    //   3793: aload_0
    //   3794: getfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   3797: ldc_w 763
    //   3800: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3803: isub
    //   3804: putfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   3807: aload_0
    //   3808: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   3811: iconst_0
    //   3812: iconst_0
    //   3813: iload 10
    //   3815: iload 9
    //   3817: invokevirtual 1392	org/telegram/messenger/ImageReceiver:setImageCoords	(IIII)V
    //   3820: aload_0
    //   3821: getstatic 1715	java/util/Locale:US	Ljava/util/Locale;
    //   3824: ldc_w 2465
    //   3827: iconst_2
    //   3828: anewarray 1106	java/lang/Object
    //   3831: dup
    //   3832: iconst_0
    //   3833: iload 10
    //   3835: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3838: aastore
    //   3839: dup
    //   3840: iconst_1
    //   3841: iload 9
    //   3843: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3846: aastore
    //   3847: invokestatic 1728	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   3850: putfield 1220	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   3853: aload_0
    //   3854: getstatic 1715	java/util/Locale:US	Ljava/util/Locale;
    //   3857: ldc_w 2467
    //   3860: iconst_2
    //   3861: anewarray 1106	java/lang/Object
    //   3864: dup
    //   3865: iconst_0
    //   3866: iload 10
    //   3868: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3871: aastore
    //   3872: dup
    //   3873: iconst_1
    //   3874: iload 9
    //   3876: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3879: aastore
    //   3880: invokestatic 1728	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   3883: putfield 1258	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilterThumb	Ljava/lang/String;
    //   3886: aload_0
    //   3887: getfield 497	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   3890: bipush 6
    //   3892: if_icmpne +2336 -> 6228
    //   3895: aload_0
    //   3896: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   3899: astore 32
    //   3901: aload_0
    //   3902: getfield 1010	org/telegram/ui/Cells/ChatMessageCell:documentAttach	Lorg/telegram/tgnet/TLRPC$Document;
    //   3905: astore 33
    //   3907: aload_0
    //   3908: getfield 1220	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   3911: astore 34
    //   3913: aload_0
    //   3914: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3917: ifnull +2305 -> 6222
    //   3920: aload_0
    //   3921: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   3924: getfield 1149	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   3927: astore 31
    //   3929: aload 32
    //   3931: aload 33
    //   3933: aconst_null
    //   3934: aload 34
    //   3936: aconst_null
    //   3937: aload 31
    //   3939: ldc_w 2469
    //   3942: aload_0
    //   3943: getfield 1010	org/telegram/ui/Cells/ChatMessageCell:documentAttach	Lorg/telegram/tgnet/TLRPC$Document;
    //   3946: getfield 1119	org/telegram/tgnet/TLRPC$Document:size	I
    //   3949: ldc_w 2471
    //   3952: iconst_1
    //   3953: invokevirtual 1224	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   3956: aload_0
    //   3957: iconst_1
    //   3958: putfield 711	org/telegram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   3961: aload 30
    //   3963: ifnull +2615 -> 6578
    //   3966: aload 30
    //   3968: ldc_w 2473
    //   3971: invokevirtual 1732	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3974: ifeq +2604 -> 6578
    //   3977: iload 21
    //   3979: ifeq +2599 -> 6578
    //   3982: iload 21
    //   3984: bipush 60
    //   3986: idiv
    //   3987: istore 9
    //   3989: ldc_w 2475
    //   3992: iconst_2
    //   3993: anewarray 1106	java/lang/Object
    //   3996: dup
    //   3997: iconst_0
    //   3998: iload 9
    //   4000: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   4003: aastore
    //   4004: dup
    //   4005: iconst_1
    //   4006: iload 21
    //   4008: iload 9
    //   4010: bipush 60
    //   4012: imul
    //   4013: isub
    //   4014: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   4017: aastore
    //   4018: invokestatic 1110	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   4021: astore 30
    //   4023: aload_0
    //   4024: getstatic 374	org/telegram/ui/Cells/ChatMessageCell:durationPaint	Landroid/text/TextPaint;
    //   4027: aload 30
    //   4029: invokevirtual 1034	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   4032: f2d
    //   4033: invokestatic 987	java/lang/Math:ceil	(D)D
    //   4036: d2i
    //   4037: putfield 1407	org/telegram/ui/Cells/ChatMessageCell:durationWidth	I
    //   4040: aload_0
    //   4041: new 288	android/text/StaticLayout
    //   4044: dup
    //   4045: aload 30
    //   4047: getstatic 374	org/telegram/ui/Cells/ChatMessageCell:durationPaint	Landroid/text/TextPaint;
    //   4050: aload_0
    //   4051: getfield 1407	org/telegram/ui/Cells/ChatMessageCell:durationWidth	I
    //   4054: getstatic 1085	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   4057: fconst_1
    //   4058: fconst_0
    //   4059: iconst_0
    //   4060: invokespecial 1088	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   4063: putfield 1397	org/telegram/ui/Cells/ChatMessageCell:videoInfoLayout	Landroid/text/StaticLayout;
    //   4066: iload 13
    //   4068: istore 11
    //   4070: aload_0
    //   4071: getfield 475	org/telegram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   4074: ifeq +45 -> 4119
    //   4077: aload_1
    //   4078: getfield 760	org/telegram/messenger/MessageObject:textHeight	I
    //   4081: ifeq +38 -> 4119
    //   4084: aload_0
    //   4085: aload_0
    //   4086: getfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   4089: aload_1
    //   4090: getfield 760	org/telegram/messenger/MessageObject:textHeight	I
    //   4093: ldc_w 1399
    //   4096: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4099: iadd
    //   4100: iadd
    //   4101: putfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   4104: aload_0
    //   4105: aload_0
    //   4106: getfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4109: ldc_w 930
    //   4112: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4115: iadd
    //   4116: putfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4119: aload_0
    //   4120: iload 8
    //   4122: iload 20
    //   4124: iload 11
    //   4126: invokespecial 2477	org/telegram/ui/Cells/ChatMessageCell:calcBackgroundWidth	(III)V
    //   4129: aload_0
    //   4130: getfield 629	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   4133: ifnonnull +219 -> 4352
    //   4136: aload_1
    //   4137: getfield 625	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   4140: ifnull +212 -> 4352
    //   4143: aload_1
    //   4144: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   4147: bipush 13
    //   4149: if_icmpeq +203 -> 4352
    //   4152: aload_0
    //   4153: getfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   4156: ldc_w 491
    //   4159: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4162: isub
    //   4163: istore 9
    //   4165: aload_0
    //   4166: new 288	android/text/StaticLayout
    //   4169: dup
    //   4170: aload_1
    //   4171: getfield 625	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   4174: invokestatic 2481	org/telegram/messenger/MessageObject:getTextPaint	()Landroid/text/TextPaint;
    //   4177: iload 9
    //   4179: ldc_w 581
    //   4182: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4185: isub
    //   4186: getstatic 1085	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   4189: fconst_1
    //   4190: fconst_0
    //   4191: iconst_0
    //   4192: invokespecial 1088	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   4195: putfield 629	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   4198: aload_0
    //   4199: getfield 629	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   4202: invokevirtual 1093	android/text/StaticLayout:getLineCount	()I
    //   4205: ifle +147 -> 4352
    //   4208: aload_0
    //   4209: getfield 493	org/telegram/ui/Cells/ChatMessageCell:timeWidth	I
    //   4212: istore 10
    //   4214: aload_1
    //   4215: invokevirtual 575	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   4218: ifeq +7526 -> 11744
    //   4221: ldc_w 927
    //   4224: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4227: istore 8
    //   4229: aload_0
    //   4230: aload_0
    //   4231: getfield 629	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   4234: invokevirtual 2174	android/text/StaticLayout:getHeight	()I
    //   4237: putfield 639	org/telegram/ui/Cells/ChatMessageCell:captionHeight	I
    //   4240: aload_0
    //   4241: aload_0
    //   4242: getfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4245: aload_0
    //   4246: getfield 639	org/telegram/ui/Cells/ChatMessageCell:captionHeight	I
    //   4249: ldc_w 1525
    //   4252: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4255: iadd
    //   4256: iadd
    //   4257: putfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4260: aload_0
    //   4261: getfield 629	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   4264: aload_0
    //   4265: getfield 629	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   4268: invokevirtual 1093	android/text/StaticLayout:getLineCount	()I
    //   4271: iconst_1
    //   4272: isub
    //   4273: invokevirtual 654	android/text/StaticLayout:getLineWidth	(I)F
    //   4276: fstore 6
    //   4278: aload_0
    //   4279: getfield 629	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   4282: aload_0
    //   4283: getfield 629	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   4286: invokevirtual 1093	android/text/StaticLayout:getLineCount	()I
    //   4289: iconst_1
    //   4290: isub
    //   4291: invokevirtual 651	android/text/StaticLayout:getLineLeft	(I)F
    //   4294: fstore 7
    //   4296: iload 9
    //   4298: ldc_w 763
    //   4301: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4304: isub
    //   4305: i2f
    //   4306: fload 6
    //   4308: fload 7
    //   4310: fadd
    //   4311: fsub
    //   4312: iload 10
    //   4314: iload 8
    //   4316: iadd
    //   4317: i2f
    //   4318: fcmpg
    //   4319: ifge +33 -> 4352
    //   4322: aload_0
    //   4323: aload_0
    //   4324: getfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4327: ldc_w 408
    //   4330: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4333: iadd
    //   4334: putfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4337: aload_0
    //   4338: aload_0
    //   4339: getfield 639	org/telegram/ui/Cells/ChatMessageCell:captionHeight	I
    //   4342: ldc_w 408
    //   4345: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4348: iadd
    //   4349: putfield 639	org/telegram/ui/Cells/ChatMessageCell:captionHeight	I
    //   4352: aload_0
    //   4353: getfield 275	org/telegram/ui/Cells/ChatMessageCell:botButtons	Ljava/util/ArrayList;
    //   4356: invokevirtual 1890	java/util/ArrayList:clear	()V
    //   4359: iload 19
    //   4361: ifeq +10 -> 4371
    //   4364: aload_0
    //   4365: getfield 280	org/telegram/ui/Cells/ChatMessageCell:botButtonsByData	Ljava/util/HashMap;
    //   4368: invokevirtual 2482	java/util/HashMap:clear	()V
    //   4371: aload_1
    //   4372: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4375: getfield 2486	org/telegram/tgnet/TLRPC$Message:reply_markup	Lorg/telegram/tgnet/TLRPC$ReplyMarkup;
    //   4378: instanceof 2488
    //   4381: ifeq +7826 -> 12207
    //   4384: aload_1
    //   4385: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4388: getfield 2486	org/telegram/tgnet/TLRPC$Message:reply_markup	Lorg/telegram/tgnet/TLRPC$ReplyMarkup;
    //   4391: getfield 2493	org/telegram/tgnet/TLRPC$ReplyMarkup:rows	Ljava/util/ArrayList;
    //   4394: invokevirtual 584	java/util/ArrayList:size	()I
    //   4397: istore 13
    //   4399: ldc_w 766
    //   4402: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4405: iload 13
    //   4407: imul
    //   4408: fconst_1
    //   4409: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4412: iadd
    //   4413: istore 8
    //   4415: aload_0
    //   4416: iload 8
    //   4418: putfield 2288	org/telegram/ui/Cells/ChatMessageCell:keyboardHeight	I
    //   4421: aload_0
    //   4422: iload 8
    //   4424: putfield 2244	org/telegram/ui/Cells/ChatMessageCell:substractBackgroundHeight	I
    //   4427: aload_0
    //   4428: aload_0
    //   4429: getfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   4432: putfield 580	org/telegram/ui/Cells/ChatMessageCell:widthForButtons	I
    //   4435: iconst_0
    //   4436: istore 9
    //   4438: aload_1
    //   4439: getfield 2496	org/telegram/messenger/MessageObject:wantedBotKeyboardWidth	I
    //   4442: aload_0
    //   4443: getfield 580	org/telegram/ui/Cells/ChatMessageCell:widthForButtons	I
    //   4446: if_icmple +74 -> 4520
    //   4449: aload_0
    //   4450: getfield 1668	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   4453: ifeq +7310 -> 11763
    //   4456: aload_1
    //   4457: invokevirtual 857	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   4460: ifeq +7303 -> 11763
    //   4463: aload_1
    //   4464: invokevirtual 575	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   4467: ifne +7296 -> 11763
    //   4470: ldc_w 2497
    //   4473: fstore 6
    //   4475: fload 6
    //   4477: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4480: ineg
    //   4481: istore 8
    //   4483: invokestatic 1666	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   4486: ifeq +7285 -> 11771
    //   4489: iload 8
    //   4491: invokestatic 1671	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   4494: iadd
    //   4495: istore 8
    //   4497: aload_0
    //   4498: aload_0
    //   4499: getfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   4502: aload_1
    //   4503: getfield 2496	org/telegram/messenger/MessageObject:wantedBotKeyboardWidth	I
    //   4506: iload 8
    //   4508: invokestatic 1044	java/lang/Math:min	(II)I
    //   4511: invokestatic 490	java/lang/Math:max	(II)I
    //   4514: putfield 580	org/telegram/ui/Cells/ChatMessageCell:widthForButtons	I
    //   4517: iconst_1
    //   4518: istore 9
    //   4520: iconst_0
    //   4521: istore 8
    //   4523: iconst_0
    //   4524: istore 10
    //   4526: iload 10
    //   4528: iload 13
    //   4530: if_icmpge +7660 -> 12190
    //   4533: aload_1
    //   4534: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4537: getfield 2486	org/telegram/tgnet/TLRPC$Message:reply_markup	Lorg/telegram/tgnet/TLRPC$ReplyMarkup;
    //   4540: getfield 2493	org/telegram/tgnet/TLRPC$ReplyMarkup:rows	Ljava/util/ArrayList;
    //   4543: iload 10
    //   4545: invokevirtual 588	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   4548: checkcast 2499	org/telegram/tgnet/TLRPC$TL_keyboardButtonRow
    //   4551: astore 30
    //   4553: aload 30
    //   4555: getfield 2502	org/telegram/tgnet/TLRPC$TL_keyboardButtonRow:buttons	Ljava/util/ArrayList;
    //   4558: invokevirtual 584	java/util/ArrayList:size	()I
    //   4561: istore 11
    //   4563: iload 11
    //   4565: ifne +7229 -> 11794
    //   4568: iload 8
    //   4570: istore 12
    //   4572: iload 10
    //   4574: iconst_1
    //   4575: iadd
    //   4576: istore 10
    //   4578: iload 12
    //   4580: istore 8
    //   4582: goto -56 -> 4526
    //   4585: iload 10
    //   4587: istore 13
    //   4589: aload_0
    //   4590: aload_0
    //   4591: getfield 725	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   4594: iload 22
    //   4596: ineg
    //   4597: invokestatic 490	java/lang/Math:max	(II)I
    //   4600: putfield 725	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   4603: goto -1589 -> 3014
    //   4606: iload 10
    //   4608: istore 13
    //   4610: iload 10
    //   4612: istore 9
    //   4614: iload 22
    //   4616: aload_0
    //   4617: getfield 719	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   4620: invokevirtual 1093	android/text/StaticLayout:getLineCount	()I
    //   4623: if_icmpge -1392 -> 3231
    //   4626: iload 10
    //   4628: istore 13
    //   4630: aload_0
    //   4631: getfield 719	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   4634: iload 22
    //   4636: invokevirtual 651	android/text/StaticLayout:getLineLeft	(I)F
    //   4639: f2d
    //   4640: invokestatic 987	java/lang/Math:ceil	(D)D
    //   4643: d2i
    //   4644: istore 24
    //   4646: iload 24
    //   4648: ifne +23 -> 4671
    //   4651: iload 10
    //   4653: istore 13
    //   4655: aload_0
    //   4656: getfield 725	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   4659: ifeq +12 -> 4671
    //   4662: iload 10
    //   4664: istore 13
    //   4666: aload_0
    //   4667: iconst_0
    //   4668: putfield 725	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   4671: iload 24
    //   4673: ifeq +172 -> 4845
    //   4676: iload 10
    //   4678: istore 13
    //   4680: aload_0
    //   4681: getfield 719	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   4684: invokevirtual 2432	android/text/StaticLayout:getWidth	()I
    //   4687: iload 24
    //   4689: isub
    //   4690: istore 9
    //   4692: iload 22
    //   4694: iload 11
    //   4696: if_icmplt +36 -> 4732
    //   4699: iload 9
    //   4701: istore 23
    //   4703: iload 11
    //   4705: ifeq +42 -> 4747
    //   4708: iload 9
    //   4710: istore 23
    //   4712: iload 24
    //   4714: ifeq +33 -> 4747
    //   4717: iload 10
    //   4719: istore 13
    //   4721: iload 9
    //   4723: istore 23
    //   4725: aload_0
    //   4726: getfield 1384	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   4729: ifeq +18 -> 4747
    //   4732: iload 10
    //   4734: istore 13
    //   4736: iload 9
    //   4738: ldc_w 2434
    //   4741: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4744: iadd
    //   4745: istore 23
    //   4747: iload 17
    //   4749: istore 9
    //   4751: iload 17
    //   4753: iload 23
    //   4755: iload 26
    //   4757: iadd
    //   4758: if_icmpge +58 -> 4816
    //   4761: iload 14
    //   4763: ifeq +24 -> 4787
    //   4766: iload 10
    //   4768: istore 13
    //   4770: aload_0
    //   4771: aload_0
    //   4772: getfield 1367	org/telegram/ui/Cells/ChatMessageCell:titleX	I
    //   4775: iload 23
    //   4777: iload 26
    //   4779: iadd
    //   4780: iload 17
    //   4782: isub
    //   4783: iadd
    //   4784: putfield 1367	org/telegram/ui/Cells/ChatMessageCell:titleX	I
    //   4787: iload 12
    //   4789: ifeq +7476 -> 12265
    //   4792: iload 10
    //   4794: istore 13
    //   4796: aload_0
    //   4797: aload_0
    //   4798: getfield 1371	org/telegram/ui/Cells/ChatMessageCell:authorX	I
    //   4801: iload 23
    //   4803: iload 26
    //   4805: iadd
    //   4806: iload 17
    //   4808: isub
    //   4809: iadd
    //   4810: putfield 1371	org/telegram/ui/Cells/ChatMessageCell:authorX	I
    //   4813: goto +7452 -> 12265
    //   4816: iload 10
    //   4818: istore 13
    //   4820: iload 10
    //   4822: iload 23
    //   4824: iload 26
    //   4826: iadd
    //   4827: invokestatic 490	java/lang/Math:max	(II)I
    //   4830: istore 10
    //   4832: iload 22
    //   4834: iconst_1
    //   4835: iadd
    //   4836: istore 22
    //   4838: iload 9
    //   4840: istore 17
    //   4842: goto -236 -> 4606
    //   4845: iload 18
    //   4847: ifeq +19 -> 4866
    //   4850: iload 10
    //   4852: istore 13
    //   4854: aload_0
    //   4855: getfield 719	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   4858: invokevirtual 2432	android/text/StaticLayout:getWidth	()I
    //   4861: istore 9
    //   4863: goto +7412 -> 12275
    //   4866: iload 10
    //   4868: istore 13
    //   4870: aload_0
    //   4871: getfield 719	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   4874: iload 22
    //   4876: invokevirtual 654	android/text/StaticLayout:getLineWidth	(I)F
    //   4879: f2d
    //   4880: invokestatic 987	java/lang/Math:ceil	(D)D
    //   4883: dstore_2
    //   4884: dload_2
    //   4885: d2i
    //   4886: istore 9
    //   4888: goto +7387 -> 12275
    //   4891: iload 25
    //   4893: istore 10
    //   4895: goto -1601 -> 3294
    //   4898: iconst_0
    //   4899: istore 29
    //   4901: goto -1562 -> 3339
    //   4904: iload 11
    //   4906: iconst_1
    //   4907: iadd
    //   4908: istore 11
    //   4910: goto -1525 -> 3385
    //   4913: aload 31
    //   4915: invokestatic 1113	org/telegram/messenger/MessageObject:isVideoDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   4918: ifeq +179 -> 5097
    //   4921: aload_0
    //   4922: aload 31
    //   4924: getfield 1142	org/telegram/tgnet/TLRPC$Document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   4927: putfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   4930: aload_0
    //   4931: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   4934: ifnull +140 -> 5074
    //   4937: aload_0
    //   4938: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   4941: getfield 2450	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   4944: ifeq +13 -> 4957
    //   4947: aload_0
    //   4948: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   4951: getfield 2453	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   4954: ifne +120 -> 5074
    //   4957: iconst_0
    //   4958: istore 11
    //   4960: iload 11
    //   4962: aload 31
    //   4964: getfield 1020	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   4967: invokevirtual 584	java/util/ArrayList:size	()I
    //   4970: if_icmpge +50 -> 5020
    //   4973: aload 31
    //   4975: getfield 1020	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   4978: iload 11
    //   4980: invokevirtual 588	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   4983: checkcast 1022	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   4986: astore 33
    //   4988: aload 33
    //   4990: instanceof 1115
    //   4993: ifeq +95 -> 5088
    //   4996: aload_0
    //   4997: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5000: aload 33
    //   5002: getfield 2456	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   5005: putfield 2450	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   5008: aload_0
    //   5009: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5012: aload 33
    //   5014: getfield 2457	org/telegram/tgnet/TLRPC$DocumentAttribute:h	I
    //   5017: putfield 2453	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   5020: aload_0
    //   5021: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5024: getfield 2450	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   5027: ifeq +13 -> 5040
    //   5030: aload_0
    //   5031: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5034: getfield 2453	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   5037: ifne +37 -> 5074
    //   5040: aload_0
    //   5041: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5044: astore 33
    //   5046: aload_0
    //   5047: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5050: astore 34
    //   5052: ldc_w 2458
    //   5055: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5058: istore 11
    //   5060: aload 34
    //   5062: iload 11
    //   5064: putfield 2453	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   5067: aload 33
    //   5069: iload 11
    //   5071: putfield 2450	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   5074: aload_0
    //   5075: iconst_0
    //   5076: aload_1
    //   5077: invokespecial 2504	org/telegram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/telegram/messenger/MessageObject;)I
    //   5080: pop
    //   5081: iload 9
    //   5083: istore 11
    //   5085: goto -1569 -> 3516
    //   5088: iload 11
    //   5090: iconst_1
    //   5091: iadd
    //   5092: istore 11
    //   5094: goto -134 -> 4960
    //   5097: aload 31
    //   5099: invokestatic 2507	org/telegram/messenger/MessageObject:isStickerDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   5102: ifeq +184 -> 5286
    //   5105: aload_0
    //   5106: aload 31
    //   5108: getfield 1142	org/telegram/tgnet/TLRPC$Document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5111: putfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5114: aload_0
    //   5115: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5118: ifnull +140 -> 5258
    //   5121: aload_0
    //   5122: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5125: getfield 2450	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   5128: ifeq +13 -> 5141
    //   5131: aload_0
    //   5132: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5135: getfield 2453	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   5138: ifne +120 -> 5258
    //   5141: iconst_0
    //   5142: istore 11
    //   5144: iload 11
    //   5146: aload 31
    //   5148: getfield 1020	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   5151: invokevirtual 584	java/util/ArrayList:size	()I
    //   5154: if_icmpge +50 -> 5204
    //   5157: aload 31
    //   5159: getfield 1020	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   5162: iload 11
    //   5164: invokevirtual 588	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   5167: checkcast 1022	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   5170: astore 33
    //   5172: aload 33
    //   5174: instanceof 2455
    //   5177: ifeq +100 -> 5277
    //   5180: aload_0
    //   5181: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5184: aload 33
    //   5186: getfield 2456	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   5189: putfield 2450	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   5192: aload_0
    //   5193: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5196: aload 33
    //   5198: getfield 2457	org/telegram/tgnet/TLRPC$DocumentAttribute:h	I
    //   5201: putfield 2453	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   5204: aload_0
    //   5205: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5208: getfield 2450	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   5211: ifeq +13 -> 5224
    //   5214: aload_0
    //   5215: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5218: getfield 2453	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   5221: ifne +37 -> 5258
    //   5224: aload_0
    //   5225: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5228: astore 33
    //   5230: aload_0
    //   5231: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5234: astore 34
    //   5236: ldc_w 2458
    //   5239: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5242: istore 11
    //   5244: aload 34
    //   5246: iload 11
    //   5248: putfield 2453	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   5251: aload 33
    //   5253: iload 11
    //   5255: putfield 2450	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   5258: aload_0
    //   5259: aload 31
    //   5261: putfield 1010	org/telegram/ui/Cells/ChatMessageCell:documentAttach	Lorg/telegram/tgnet/TLRPC$Document;
    //   5264: aload_0
    //   5265: bipush 6
    //   5267: putfield 497	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   5270: iload 9
    //   5272: istore 11
    //   5274: goto -1758 -> 3516
    //   5277: iload 11
    //   5279: iconst_1
    //   5280: iadd
    //   5281: istore 11
    //   5283: goto -139 -> 5144
    //   5286: aload_0
    //   5287: iload 8
    //   5289: iload 20
    //   5291: iload 9
    //   5293: invokespecial 2477	org/telegram/ui/Cells/ChatMessageCell:calcBackgroundWidth	(III)V
    //   5296: aload 31
    //   5298: invokestatic 2507	org/telegram/messenger/MessageObject:isStickerDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   5301: ifne +652 -> 5953
    //   5304: aload_0
    //   5305: getfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   5308: ldc_w 927
    //   5311: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5314: iload 8
    //   5316: iadd
    //   5317: if_icmpge +16 -> 5333
    //   5320: aload_0
    //   5321: ldc_w 927
    //   5324: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5327: iload 8
    //   5329: iadd
    //   5330: putfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   5333: aload 31
    //   5335: invokestatic 1015	org/telegram/messenger/MessageObject:isVoiceDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   5338: ifeq +90 -> 5428
    //   5341: aload_0
    //   5342: aload_0
    //   5343: getfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   5346: ldc_w 581
    //   5349: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5352: isub
    //   5353: aload_1
    //   5354: invokespecial 2504	org/telegram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/telegram/messenger/MessageObject;)I
    //   5357: pop
    //   5358: aload_0
    //   5359: aload_0
    //   5360: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   5363: getfield 760	org/telegram/messenger/MessageObject:textHeight	I
    //   5366: ldc_w 763
    //   5369: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5372: iadd
    //   5373: aload_0
    //   5374: getfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5377: iadd
    //   5378: putfield 551	org/telegram/ui/Cells/ChatMessageCell:mediaOffsetY	I
    //   5381: aload_0
    //   5382: aload_0
    //   5383: getfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5386: ldc_w 1513
    //   5389: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5392: iadd
    //   5393: putfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5396: aload_0
    //   5397: aload_0
    //   5398: getfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5401: ldc_w 1513
    //   5404: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5407: iadd
    //   5408: putfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5411: aload_0
    //   5412: iload 8
    //   5414: iload 20
    //   5416: iload 9
    //   5418: invokespecial 2477	org/telegram/ui/Cells/ChatMessageCell:calcBackgroundWidth	(III)V
    //   5421: iload 9
    //   5423: istore 11
    //   5425: goto -1909 -> 3516
    //   5428: aload 31
    //   5430: invokestatic 1061	org/telegram/messenger/MessageObject:isMusicDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   5433: ifeq +229 -> 5662
    //   5436: aload_0
    //   5437: aload_0
    //   5438: getfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   5441: ldc_w 581
    //   5444: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5447: isub
    //   5448: aload_1
    //   5449: invokespecial 2504	org/telegram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/telegram/messenger/MessageObject;)I
    //   5452: istore 11
    //   5454: aload_0
    //   5455: aload_0
    //   5456: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   5459: getfield 760	org/telegram/messenger/MessageObject:textHeight	I
    //   5462: ldc_w 763
    //   5465: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5468: iadd
    //   5469: aload_0
    //   5470: getfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5473: iadd
    //   5474: putfield 551	org/telegram/ui/Cells/ChatMessageCell:mediaOffsetY	I
    //   5477: aload_0
    //   5478: aload_0
    //   5479: getfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5482: ldc_w 1528
    //   5485: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5488: iadd
    //   5489: putfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5492: aload_0
    //   5493: aload_0
    //   5494: getfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5497: ldc_w 1528
    //   5500: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5503: iadd
    //   5504: putfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5507: iload 8
    //   5509: ldc_w 1062
    //   5512: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5515: isub
    //   5516: istore 13
    //   5518: iload 9
    //   5520: iload 11
    //   5522: iload 26
    //   5524: iadd
    //   5525: ldc_w 1028
    //   5528: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5531: iadd
    //   5532: invokestatic 490	java/lang/Math:max	(II)I
    //   5535: istore 9
    //   5537: iload 9
    //   5539: istore 8
    //   5541: aload_0
    //   5542: getfield 1090	org/telegram/ui/Cells/ChatMessageCell:songLayout	Landroid/text/StaticLayout;
    //   5545: ifnull +46 -> 5591
    //   5548: iload 9
    //   5550: istore 8
    //   5552: aload_0
    //   5553: getfield 1090	org/telegram/ui/Cells/ChatMessageCell:songLayout	Landroid/text/StaticLayout;
    //   5556: invokevirtual 1093	android/text/StaticLayout:getLineCount	()I
    //   5559: ifle +32 -> 5591
    //   5562: iload 9
    //   5564: i2f
    //   5565: aload_0
    //   5566: getfield 1090	org/telegram/ui/Cells/ChatMessageCell:songLayout	Landroid/text/StaticLayout;
    //   5569: iconst_0
    //   5570: invokevirtual 654	android/text/StaticLayout:getLineWidth	(I)F
    //   5573: iload 26
    //   5575: i2f
    //   5576: fadd
    //   5577: ldc_w 1062
    //   5580: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5583: i2f
    //   5584: fadd
    //   5585: invokestatic 2510	java/lang/Math:max	(FF)F
    //   5588: f2i
    //   5589: istore 8
    //   5591: iload 8
    //   5593: istore 11
    //   5595: aload_0
    //   5596: getfield 1100	org/telegram/ui/Cells/ChatMessageCell:performerLayout	Landroid/text/StaticLayout;
    //   5599: ifnull +46 -> 5645
    //   5602: iload 8
    //   5604: istore 11
    //   5606: aload_0
    //   5607: getfield 1100	org/telegram/ui/Cells/ChatMessageCell:performerLayout	Landroid/text/StaticLayout;
    //   5610: invokevirtual 1093	android/text/StaticLayout:getLineCount	()I
    //   5613: ifle +32 -> 5645
    //   5616: iload 8
    //   5618: i2f
    //   5619: aload_0
    //   5620: getfield 1100	org/telegram/ui/Cells/ChatMessageCell:performerLayout	Landroid/text/StaticLayout;
    //   5623: iconst_0
    //   5624: invokevirtual 654	android/text/StaticLayout:getLineWidth	(I)F
    //   5627: iload 26
    //   5629: i2f
    //   5630: fadd
    //   5631: ldc_w 1062
    //   5634: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5637: i2f
    //   5638: fadd
    //   5639: invokestatic 2510	java/lang/Math:max	(FF)F
    //   5642: f2i
    //   5643: istore 11
    //   5645: aload_0
    //   5646: iload 13
    //   5648: iload 20
    //   5650: iload 11
    //   5652: invokespecial 2477	org/telegram/ui/Cells/ChatMessageCell:calcBackgroundWidth	(III)V
    //   5655: iload 13
    //   5657: istore 8
    //   5659: goto -2143 -> 3516
    //   5662: aload_0
    //   5663: aload_0
    //   5664: getfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   5667: ldc_w 2511
    //   5670: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5673: isub
    //   5674: aload_1
    //   5675: invokespecial 2504	org/telegram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/telegram/messenger/MessageObject;)I
    //   5678: pop
    //   5679: aload_0
    //   5680: iconst_1
    //   5681: putfield 765	org/telegram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   5684: aload_0
    //   5685: getfield 711	org/telegram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   5688: ifeq +69 -> 5757
    //   5691: aload_0
    //   5692: aload_0
    //   5693: getfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5696: ldc_w 1927
    //   5699: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5702: iadd
    //   5703: putfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5706: aload_0
    //   5707: aload_0
    //   5708: getfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5711: ldc_w 1062
    //   5714: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5717: iadd
    //   5718: putfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5721: aload_0
    //   5722: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   5725: iconst_0
    //   5726: aload_0
    //   5727: getfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5730: aload_0
    //   5731: getfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   5734: iadd
    //   5735: ldc_w 1062
    //   5738: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5741: ldc_w 1062
    //   5744: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5747: invokevirtual 1392	org/telegram/messenger/ImageReceiver:setImageCoords	(IIII)V
    //   5750: iload 9
    //   5752: istore 11
    //   5754: goto -2238 -> 3516
    //   5757: aload_0
    //   5758: aload_0
    //   5759: getfield 477	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   5762: getfield 760	org/telegram/messenger/MessageObject:textHeight	I
    //   5765: ldc_w 763
    //   5768: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5771: iadd
    //   5772: aload_0
    //   5773: getfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5776: iadd
    //   5777: putfield 551	org/telegram/ui/Cells/ChatMessageCell:mediaOffsetY	I
    //   5780: aload_0
    //   5781: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   5784: iconst_0
    //   5785: aload_0
    //   5786: getfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5789: aload_0
    //   5790: getfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   5793: iadd
    //   5794: ldc_w 408
    //   5797: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5800: isub
    //   5801: ldc_w 1528
    //   5804: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5807: ldc_w 1528
    //   5810: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5813: invokevirtual 1392	org/telegram/messenger/ImageReceiver:setImageCoords	(IIII)V
    //   5816: aload_0
    //   5817: aload_0
    //   5818: getfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5821: ldc_w 2512
    //   5824: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5827: iadd
    //   5828: putfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5831: aload_0
    //   5832: aload_0
    //   5833: getfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5836: ldc_w 946
    //   5839: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5842: iadd
    //   5843: putfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5846: iload 9
    //   5848: istore 11
    //   5850: goto -2334 -> 3516
    //   5853: aload 33
    //   5855: ifnull +98 -> 5953
    //   5858: aload 30
    //   5860: ifnull +100 -> 5960
    //   5863: aload 30
    //   5865: ldc_w 2459
    //   5868: invokevirtual 1732	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   5871: ifeq +89 -> 5960
    //   5874: iconst_1
    //   5875: istore 29
    //   5877: aload_0
    //   5878: iload 29
    //   5880: putfield 765	org/telegram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   5883: aload_1
    //   5884: getfield 1198	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   5887: astore 33
    //   5889: aload_0
    //   5890: getfield 765	org/telegram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   5893: ifeq +73 -> 5966
    //   5896: invokestatic 1201	org/telegram/messenger/AndroidUtilities:getPhotoSize	()I
    //   5899: istore 11
    //   5901: aload_0
    //   5902: getfield 765	org/telegram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   5905: ifne +68 -> 5973
    //   5908: iconst_1
    //   5909: istore 29
    //   5911: aload_0
    //   5912: aload 33
    //   5914: iload 11
    //   5916: iload 29
    //   5918: invokestatic 2515	org/telegram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;IZ)Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5921: putfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5924: aload_0
    //   5925: aload_1
    //   5926: getfield 1198	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   5929: bipush 80
    //   5931: invokestatic 1205	org/telegram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;I)Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5934: putfield 1245	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5937: aload_0
    //   5938: getfield 1245	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5941: aload_0
    //   5942: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5945: if_acmpne +8 -> 5953
    //   5948: aload_0
    //   5949: aconst_null
    //   5950: putfield 1245	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5953: iload 9
    //   5955: istore 11
    //   5957: goto -2441 -> 3516
    //   5960: iconst_0
    //   5961: istore 29
    //   5963: goto -86 -> 5877
    //   5966: iload 10
    //   5968: istore 11
    //   5970: goto -69 -> 5901
    //   5973: iconst_0
    //   5974: istore 29
    //   5976: goto -65 -> 5911
    //   5979: iconst_0
    //   5980: istore 29
    //   5982: goto -2377 -> 3605
    //   5985: getstatic 1676	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   5988: getfield 1681	android/graphics/Point:x	I
    //   5991: i2f
    //   5992: ldc_w 2463
    //   5995: fmul
    //   5996: f2i
    //   5997: istore 9
    //   5999: goto -2325 -> 3674
    //   6002: aload_0
    //   6003: getfield 475	org/telegram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   6006: ifeq +41 -> 6047
    //   6009: sipush 640
    //   6012: i2f
    //   6013: iload 9
    //   6015: fconst_2
    //   6016: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6019: isub
    //   6020: i2f
    //   6021: fdiv
    //   6022: fstore 6
    //   6024: sipush 640
    //   6027: i2f
    //   6028: fload 6
    //   6030: fdiv
    //   6031: f2i
    //   6032: istore 10
    //   6034: sipush 360
    //   6037: i2f
    //   6038: fload 6
    //   6040: fdiv
    //   6041: f2i
    //   6042: istore 9
    //   6044: goto -2318 -> 3726
    //   6047: aload_0
    //   6048: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6051: getfield 2450	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   6054: istore 11
    //   6056: aload_0
    //   6057: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6060: getfield 2453	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   6063: istore 10
    //   6065: iload 11
    //   6067: i2f
    //   6068: iload 9
    //   6070: fconst_2
    //   6071: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6074: isub
    //   6075: i2f
    //   6076: fdiv
    //   6077: fstore 6
    //   6079: iload 11
    //   6081: i2f
    //   6082: fload 6
    //   6084: fdiv
    //   6085: f2i
    //   6086: istore 11
    //   6088: iload 10
    //   6090: i2f
    //   6091: fload 6
    //   6093: fdiv
    //   6094: f2i
    //   6095: istore 12
    //   6097: aload 32
    //   6099: ifnull +53 -> 6152
    //   6102: iload 12
    //   6104: istore 9
    //   6106: iload 11
    //   6108: istore 10
    //   6110: aload 32
    //   6112: ifnull -2386 -> 3726
    //   6115: iload 12
    //   6117: istore 9
    //   6119: iload 11
    //   6121: istore 10
    //   6123: aload 32
    //   6125: invokevirtual 1133	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   6128: ldc_w 2420
    //   6131: invokevirtual 1732	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   6134: ifne -2408 -> 3726
    //   6137: iload 12
    //   6139: istore 9
    //   6141: iload 11
    //   6143: istore 10
    //   6145: aload_0
    //   6146: getfield 497	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   6149: ifne -2423 -> 3726
    //   6152: iload 12
    //   6154: istore 9
    //   6156: iload 11
    //   6158: istore 10
    //   6160: iload 12
    //   6162: getstatic 1676	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   6165: getfield 1684	android/graphics/Point:y	I
    //   6168: iconst_3
    //   6169: idiv
    //   6170: if_icmple -2444 -> 3726
    //   6173: getstatic 1676	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   6176: getfield 1684	android/graphics/Point:y	I
    //   6179: iconst_3
    //   6180: idiv
    //   6181: istore 9
    //   6183: iload 11
    //   6185: istore 10
    //   6187: goto -2461 -> 3726
    //   6190: aload_0
    //   6191: aload_0
    //   6192: getfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6195: ldc_w 401
    //   6198: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6201: iload 9
    //   6203: iadd
    //   6204: iadd
    //   6205: putfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6208: aload_0
    //   6209: aload_0
    //   6210: getfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   6213: iload 9
    //   6215: iadd
    //   6216: putfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   6219: goto -2412 -> 3807
    //   6222: aconst_null
    //   6223: astore 31
    //   6225: goto -2296 -> 3929
    //   6228: aload_0
    //   6229: getfield 497	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   6232: iconst_4
    //   6233: if_icmpne +29 -> 6262
    //   6236: aload_0
    //   6237: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   6240: aconst_null
    //   6241: aconst_null
    //   6242: aload_0
    //   6243: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6246: getfield 1149	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6249: aload_0
    //   6250: getfield 1220	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   6253: iconst_0
    //   6254: aconst_null
    //   6255: iconst_0
    //   6256: invokevirtual 1249	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   6259: goto -2303 -> 3956
    //   6262: aload_0
    //   6263: getfield 497	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   6266: iconst_2
    //   6267: if_icmpne +117 -> 6384
    //   6270: aload_1
    //   6271: getfield 2059	org/telegram/messenger/MessageObject:mediaExists	Z
    //   6274: istore 29
    //   6276: aload 31
    //   6278: invokestatic 2519	org/telegram/messenger/FileLoader:getAttachFileName	(Lorg/telegram/tgnet/TLObject;)Ljava/lang/String;
    //   6281: astore 32
    //   6283: aload_0
    //   6284: getfield 475	org/telegram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   6287: ifne +30 -> 6317
    //   6290: iload 29
    //   6292: ifne +25 -> 6317
    //   6295: invokestatic 432	org/telegram/messenger/MediaController:getInstance	()Lorg/telegram/messenger/MediaController;
    //   6298: bipush 32
    //   6300: invokevirtual 2522	org/telegram/messenger/MediaController:canDownloadMedia	(I)Z
    //   6303: ifne +14 -> 6317
    //   6306: invokestatic 1252	org/telegram/messenger/FileLoader:getInstance	()Lorg/telegram/messenger/FileLoader;
    //   6309: aload 32
    //   6311: invokevirtual 2525	org/telegram/messenger/FileLoader:isLoadingFile	(Ljava/lang/String;)Z
    //   6314: ifeq +39 -> 6353
    //   6317: aload_0
    //   6318: iconst_0
    //   6319: putfield 1734	org/telegram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   6322: aload_0
    //   6323: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   6326: aload 31
    //   6328: aconst_null
    //   6329: aload_0
    //   6330: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6333: getfield 1149	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6336: aload_0
    //   6337: getfield 1220	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   6340: aload 31
    //   6342: getfield 1119	org/telegram/tgnet/TLRPC$Document:size	I
    //   6345: aconst_null
    //   6346: iconst_0
    //   6347: invokevirtual 1249	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   6350: goto -2394 -> 3956
    //   6353: aload_0
    //   6354: iconst_1
    //   6355: putfield 1734	org/telegram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   6358: aload_0
    //   6359: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   6362: aconst_null
    //   6363: aconst_null
    //   6364: aload_0
    //   6365: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6368: getfield 1149	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6371: aload_0
    //   6372: getfield 1220	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   6375: iconst_0
    //   6376: aconst_null
    //   6377: iconst_0
    //   6378: invokevirtual 1249	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   6381: goto -2425 -> 3956
    //   6384: aload_1
    //   6385: getfield 2059	org/telegram/messenger/MessageObject:mediaExists	Z
    //   6388: istore 29
    //   6390: aload_0
    //   6391: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6394: invokestatic 2519	org/telegram/messenger/FileLoader:getAttachFileName	(Lorg/telegram/tgnet/TLObject;)Ljava/lang/String;
    //   6397: astore 31
    //   6399: aload_0
    //   6400: getfield 475	org/telegram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   6403: ifne +29 -> 6432
    //   6406: iload 29
    //   6408: ifne +24 -> 6432
    //   6411: invokestatic 432	org/telegram/messenger/MediaController:getInstance	()Lorg/telegram/messenger/MediaController;
    //   6414: iconst_1
    //   6415: invokevirtual 2522	org/telegram/messenger/MediaController:canDownloadMedia	(I)Z
    //   6418: ifne +14 -> 6432
    //   6421: invokestatic 1252	org/telegram/messenger/FileLoader:getInstance	()Lorg/telegram/messenger/FileLoader;
    //   6424: aload 31
    //   6426: invokevirtual 2525	org/telegram/messenger/FileLoader:isLoadingFile	(Ljava/lang/String;)Z
    //   6429: ifeq +72 -> 6501
    //   6432: aload_0
    //   6433: iconst_0
    //   6434: putfield 1734	org/telegram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   6437: aload_0
    //   6438: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   6441: astore 32
    //   6443: aload_0
    //   6444: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6447: getfield 1149	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6450: astore 33
    //   6452: aload_0
    //   6453: getfield 1220	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   6456: astore 34
    //   6458: aload_0
    //   6459: getfield 1245	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6462: ifnull +33 -> 6495
    //   6465: aload_0
    //   6466: getfield 1245	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6469: getfield 1149	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6472: astore 31
    //   6474: aload 32
    //   6476: aload 33
    //   6478: aload 34
    //   6480: aload 31
    //   6482: aload_0
    //   6483: getfield 1258	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilterThumb	Ljava/lang/String;
    //   6486: iconst_0
    //   6487: aconst_null
    //   6488: iconst_0
    //   6489: invokevirtual 1249	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   6492: goto -2536 -> 3956
    //   6495: aconst_null
    //   6496: astore 31
    //   6498: goto -24 -> 6474
    //   6501: aload_0
    //   6502: iconst_1
    //   6503: putfield 1734	org/telegram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   6506: aload_0
    //   6507: getfield 1245	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6510: ifnull +54 -> 6564
    //   6513: aload_0
    //   6514: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   6517: aconst_null
    //   6518: aconst_null
    //   6519: aload_0
    //   6520: getfield 1245	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6523: getfield 1149	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6526: getstatic 1715	java/util/Locale:US	Ljava/util/Locale;
    //   6529: ldc_w 2467
    //   6532: iconst_2
    //   6533: anewarray 1106	java/lang/Object
    //   6536: dup
    //   6537: iconst_0
    //   6538: iload 10
    //   6540: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   6543: aastore
    //   6544: dup
    //   6545: iconst_1
    //   6546: iload 9
    //   6548: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   6551: aastore
    //   6552: invokestatic 1728	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   6555: iconst_0
    //   6556: aconst_null
    //   6557: iconst_0
    //   6558: invokevirtual 1249	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   6561: goto -2605 -> 3956
    //   6564: aload_0
    //   6565: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   6568: aconst_null
    //   6569: checkcast 1323	android/graphics/drawable/Drawable
    //   6572: invokevirtual 1230	org/telegram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   6575: goto -2619 -> 3956
    //   6578: iload 13
    //   6580: istore 11
    //   6582: aload_0
    //   6583: getfield 475	org/telegram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   6586: ifeq -2516 -> 4070
    //   6589: ldc_w 2527
    //   6592: ldc_w 2528
    //   6595: invokestatic 1166	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   6598: invokevirtual 2531	java/lang/String:toUpperCase	()Ljava/lang/String;
    //   6601: astore 30
    //   6603: aload_0
    //   6604: getstatic 376	org/telegram/ui/Cells/ChatMessageCell:gamePaint	Landroid/text/TextPaint;
    //   6607: aload 30
    //   6609: invokevirtual 1034	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   6612: f2d
    //   6613: invokestatic 987	java/lang/Math:ceil	(D)D
    //   6616: d2i
    //   6617: putfield 1407	org/telegram/ui/Cells/ChatMessageCell:durationWidth	I
    //   6620: aload_0
    //   6621: new 288	android/text/StaticLayout
    //   6624: dup
    //   6625: aload 30
    //   6627: getstatic 376	org/telegram/ui/Cells/ChatMessageCell:gamePaint	Landroid/text/TextPaint;
    //   6630: aload_0
    //   6631: getfield 1407	org/telegram/ui/Cells/ChatMessageCell:durationWidth	I
    //   6634: getstatic 1085	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   6637: fconst_1
    //   6638: fconst_0
    //   6639: iconst_0
    //   6640: invokespecial 1088	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   6643: putfield 1397	org/telegram/ui/Cells/ChatMessageCell:videoInfoLayout	Landroid/text/StaticLayout;
    //   6646: iload 13
    //   6648: istore 11
    //   6650: goto -2580 -> 4070
    //   6653: aload_0
    //   6654: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   6657: aconst_null
    //   6658: checkcast 1323	android/graphics/drawable/Drawable
    //   6661: invokevirtual 1230	org/telegram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   6664: aload_0
    //   6665: aload_0
    //   6666: getfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   6669: ldc_w 1399
    //   6672: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6675: isub
    //   6676: putfield 762	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   6679: aload_0
    //   6680: aload_0
    //   6681: getfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6684: ldc_w 930
    //   6687: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6690: iadd
    //   6691: putfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6694: goto -2624 -> 4070
    //   6697: aload_0
    //   6698: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   6701: aconst_null
    //   6702: checkcast 1323	android/graphics/drawable/Drawable
    //   6705: invokevirtual 1230	org/telegram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   6708: aload_0
    //   6709: iload 8
    //   6711: iload 20
    //   6713: iload 12
    //   6715: invokespecial 2477	org/telegram/ui/Cells/ChatMessageCell:calcBackgroundWidth	(III)V
    //   6718: goto -2589 -> 4129
    //   6721: aload_1
    //   6722: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   6725: bipush 12
    //   6727: if_icmpne +665 -> 7392
    //   6730: aload_0
    //   6731: iconst_0
    //   6732: putfield 1791	org/telegram/ui/Cells/ChatMessageCell:drawName	Z
    //   6735: aload_0
    //   6736: iconst_1
    //   6737: putfield 1801	org/telegram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   6740: aload_0
    //   6741: iconst_1
    //   6742: putfield 711	org/telegram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   6745: aload_0
    //   6746: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   6749: ldc_w 1685
    //   6752: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6755: invokevirtual 419	org/telegram/messenger/ImageReceiver:setRoundRadius	(I)V
    //   6758: invokestatic 1666	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   6761: ifeq +485 -> 7246
    //   6764: invokestatic 1671	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   6767: istore 8
    //   6769: aload_0
    //   6770: getfield 1668	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   6773: ifeq +465 -> 7238
    //   6776: aload_1
    //   6777: invokevirtual 857	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   6780: ifeq +458 -> 7238
    //   6783: aload_1
    //   6784: invokevirtual 575	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   6787: ifne +451 -> 7238
    //   6790: ldc_w 2532
    //   6793: fstore 6
    //   6795: aload_0
    //   6796: iload 8
    //   6798: fload 6
    //   6800: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6803: isub
    //   6804: ldc_w 2533
    //   6807: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6810: invokestatic 1044	java/lang/Math:min	(II)I
    //   6813: putfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   6816: aload_0
    //   6817: aload_0
    //   6818: getfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   6821: ldc_w 491
    //   6824: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6827: isub
    //   6828: putfield 1036	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   6831: aload_1
    //   6832: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   6835: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   6838: getfield 959	org/telegram/tgnet/TLRPC$MessageMedia:user_id	I
    //   6841: istore 8
    //   6843: invokestatic 868	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   6846: iload 8
    //   6848: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   6851: invokevirtual 881	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   6854: astore 32
    //   6856: aload_0
    //   6857: invokespecial 1924	org/telegram/ui/Cells/ChatMessageCell:getMaxNameWidth	()I
    //   6860: ldc_w 2534
    //   6863: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6866: isub
    //   6867: istore 8
    //   6869: iload 8
    //   6871: ifge +5370 -> 12241
    //   6874: ldc_w 581
    //   6877: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6880: istore 8
    //   6882: aconst_null
    //   6883: astore 30
    //   6885: aconst_null
    //   6886: astore 31
    //   6888: aload 32
    //   6890: ifnull +34 -> 6924
    //   6893: aload 31
    //   6895: astore 30
    //   6897: aload 32
    //   6899: getfield 1766	org/telegram/tgnet/TLRPC$User:photo	Lorg/telegram/tgnet/TLRPC$UserProfilePhoto;
    //   6902: ifnull +13 -> 6915
    //   6905: aload 32
    //   6907: getfield 1766	org/telegram/tgnet/TLRPC$User:photo	Lorg/telegram/tgnet/TLRPC$UserProfilePhoto;
    //   6910: getfield 1771	org/telegram/tgnet/TLRPC$UserProfilePhoto:photo_small	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   6913: astore 30
    //   6915: aload_0
    //   6916: getfield 440	org/telegram/ui/Cells/ChatMessageCell:contactAvatarDrawable	Lorg/telegram/ui/Components/AvatarDrawable;
    //   6919: aload 32
    //   6921: invokevirtual 1902	org/telegram/ui/Components/AvatarDrawable:setInfo	(Lorg/telegram/tgnet/TLRPC$User;)V
    //   6924: aload_0
    //   6925: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   6928: astore 33
    //   6930: aload 32
    //   6932: ifnull +380 -> 7312
    //   6935: aload_0
    //   6936: getfield 440	org/telegram/ui/Cells/ChatMessageCell:contactAvatarDrawable	Lorg/telegram/ui/Components/AvatarDrawable;
    //   6939: astore 31
    //   6941: aload 33
    //   6943: aload 30
    //   6945: ldc_w 1904
    //   6948: aload 31
    //   6950: aconst_null
    //   6951: iconst_0
    //   6952: invokevirtual 1907	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Ljava/lang/String;Z)V
    //   6955: aload_1
    //   6956: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   6959: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   6962: getfield 2537	org/telegram/tgnet/TLRPC$MessageMedia:phone_number	Ljava/lang/String;
    //   6965: astore 30
    //   6967: aload 30
    //   6969: ifnull +374 -> 7343
    //   6972: aload 30
    //   6974: invokevirtual 910	java/lang/String:length	()I
    //   6977: ifeq +366 -> 7343
    //   6980: invokestatic 2542	org/telegram/PhoneFormat/PhoneFormat:getInstance	()Lorg/telegram/PhoneFormat/PhoneFormat;
    //   6983: aload 30
    //   6985: invokevirtual 2545	org/telegram/PhoneFormat/PhoneFormat:format	(Ljava/lang/String;)Ljava/lang/String;
    //   6988: astore 30
    //   6990: aload_1
    //   6991: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   6994: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   6997: getfield 2546	org/telegram/tgnet/TLRPC$MessageMedia:first_name	Ljava/lang/String;
    //   7000: aload_1
    //   7001: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   7004: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   7007: getfield 2547	org/telegram/tgnet/TLRPC$MessageMedia:last_name	Ljava/lang/String;
    //   7010: invokestatic 1870	org/telegram/messenger/ContactsController:formatName	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   7013: bipush 10
    //   7015: bipush 32
    //   7017: invokevirtual 1069	java/lang/String:replace	(CC)Ljava/lang/String;
    //   7020: astore 32
    //   7022: aload 32
    //   7024: astore 31
    //   7026: aload 32
    //   7028: invokeinterface 1000 1 0
    //   7033: ifne +7 -> 7040
    //   7036: aload 30
    //   7038: astore 31
    //   7040: aload_0
    //   7041: new 288	android/text/StaticLayout
    //   7044: dup
    //   7045: aload 31
    //   7047: getstatic 370	org/telegram/ui/Cells/ChatMessageCell:contactNamePaint	Landroid/text/TextPaint;
    //   7050: iload 8
    //   7052: i2f
    //   7053: getstatic 1075	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   7056: invokestatic 1079	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   7059: getstatic 370	org/telegram/ui/Cells/ChatMessageCell:contactNamePaint	Landroid/text/TextPaint;
    //   7062: iload 8
    //   7064: fconst_2
    //   7065: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7068: iadd
    //   7069: getstatic 1085	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   7072: fconst_1
    //   7073: fconst_0
    //   7074: iconst_0
    //   7075: invokespecial 1088	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   7078: putfield 1365	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   7081: aload_0
    //   7082: new 288	android/text/StaticLayout
    //   7085: dup
    //   7086: aload 30
    //   7088: bipush 10
    //   7090: bipush 32
    //   7092: invokevirtual 1069	java/lang/String:replace	(CC)Ljava/lang/String;
    //   7095: getstatic 372	org/telegram/ui/Cells/ChatMessageCell:contactPhonePaint	Landroid/text/TextPaint;
    //   7098: iload 8
    //   7100: i2f
    //   7101: getstatic 1075	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   7104: invokestatic 1079	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   7107: getstatic 372	org/telegram/ui/Cells/ChatMessageCell:contactPhonePaint	Landroid/text/TextPaint;
    //   7110: iload 8
    //   7112: fconst_2
    //   7113: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7116: iadd
    //   7117: getstatic 1085	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   7120: fconst_1
    //   7121: fconst_0
    //   7122: iconst_0
    //   7123: invokespecial 1088	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   7126: putfield 1177	org/telegram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   7129: aload_0
    //   7130: aload_1
    //   7131: invokespecial 2401	org/telegram/ui/Cells/ChatMessageCell:setMessageObjectInternal	(Lorg/telegram/messenger/MessageObject;)V
    //   7134: aload_0
    //   7135: getfield 1801	org/telegram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   7138: ifeq +219 -> 7357
    //   7141: aload_1
    //   7142: invokevirtual 1960	org/telegram/messenger/MessageObject:isForwarded	()Z
    //   7145: ifeq +212 -> 7357
    //   7148: aload_0
    //   7149: aload_0
    //   7150: getfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   7153: ldc_w 1486
    //   7156: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7159: iadd
    //   7160: putfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   7163: aload_0
    //   7164: ldc_w 2548
    //   7167: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7170: aload_0
    //   7171: getfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   7174: iadd
    //   7175: putfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   7178: aload_0
    //   7179: getfield 1177	org/telegram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   7182: invokevirtual 1093	android/text/StaticLayout:getLineCount	()I
    //   7185: ifle -3056 -> 4129
    //   7188: aload_0
    //   7189: getfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7192: ldc_w 2534
    //   7195: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7198: isub
    //   7199: aload_0
    //   7200: getfield 1177	org/telegram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   7203: iconst_0
    //   7204: invokevirtual 654	android/text/StaticLayout:getLineWidth	(I)F
    //   7207: f2d
    //   7208: invokestatic 987	java/lang/Math:ceil	(D)D
    //   7211: d2i
    //   7212: isub
    //   7213: aload_0
    //   7214: getfield 493	org/telegram/ui/Cells/ChatMessageCell:timeWidth	I
    //   7217: if_icmpge -3088 -> 4129
    //   7220: aload_0
    //   7221: aload_0
    //   7222: getfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   7225: ldc_w 763
    //   7228: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7231: iadd
    //   7232: putfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   7235: goto -3106 -> 4129
    //   7238: ldc_w 946
    //   7241: fstore 6
    //   7243: goto -448 -> 6795
    //   7246: getstatic 1676	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   7249: getfield 1681	android/graphics/Point:x	I
    //   7252: istore 8
    //   7254: aload_0
    //   7255: getfield 1668	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   7258: ifeq +46 -> 7304
    //   7261: aload_1
    //   7262: invokevirtual 857	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   7265: ifeq +39 -> 7304
    //   7268: aload_1
    //   7269: invokevirtual 575	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   7272: ifne +32 -> 7304
    //   7275: ldc_w 2532
    //   7278: fstore 6
    //   7280: aload_0
    //   7281: iload 8
    //   7283: fload 6
    //   7285: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7288: isub
    //   7289: ldc_w 2533
    //   7292: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7295: invokestatic 1044	java/lang/Math:min	(II)I
    //   7298: putfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7301: goto -485 -> 6816
    //   7304: ldc_w 946
    //   7307: fstore 6
    //   7309: goto -29 -> 7280
    //   7312: getstatic 2551	org/telegram/ui/ActionBar/Theme:contactDrawable	[Landroid/graphics/drawable/Drawable;
    //   7315: astore 31
    //   7317: aload_1
    //   7318: invokevirtual 575	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   7321: ifeq +16 -> 7337
    //   7324: iconst_1
    //   7325: istore 9
    //   7327: aload 31
    //   7329: iload 9
    //   7331: aaload
    //   7332: astore 31
    //   7334: goto -393 -> 6941
    //   7337: iconst_0
    //   7338: istore 9
    //   7340: goto -13 -> 7327
    //   7343: ldc_w 2553
    //   7346: ldc_w 2554
    //   7349: invokestatic 1166	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   7352: astore 30
    //   7354: goto -364 -> 6990
    //   7357: aload_0
    //   7358: getfield 1922	org/telegram/ui/Cells/ChatMessageCell:drawNameLayout	Z
    //   7361: ifeq -198 -> 7163
    //   7364: aload_1
    //   7365: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   7368: getfield 923	org/telegram/tgnet/TLRPC$Message:reply_to_msg_id	I
    //   7371: ifne -208 -> 7163
    //   7374: aload_0
    //   7375: aload_0
    //   7376: getfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   7379: ldc_w 608
    //   7382: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7385: iadd
    //   7386: putfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   7389: goto -226 -> 7163
    //   7392: aload_1
    //   7393: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   7396: iconst_2
    //   7397: if_icmpne +173 -> 7570
    //   7400: aload_0
    //   7401: iconst_1
    //   7402: putfield 1801	org/telegram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   7405: invokestatic 1666	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   7408: ifeq +96 -> 7504
    //   7411: invokestatic 1671	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   7414: istore 8
    //   7416: aload_0
    //   7417: getfield 1668	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   7420: ifeq +76 -> 7496
    //   7423: aload_1
    //   7424: invokevirtual 857	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   7427: ifeq +69 -> 7496
    //   7430: aload_1
    //   7431: invokevirtual 575	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   7434: ifne +62 -> 7496
    //   7437: ldc_w 2532
    //   7440: fstore 6
    //   7442: aload_0
    //   7443: iload 8
    //   7445: fload 6
    //   7447: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7450: isub
    //   7451: ldc_w 2533
    //   7454: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7457: invokestatic 1044	java/lang/Math:min	(II)I
    //   7460: putfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7463: aload_0
    //   7464: aload_0
    //   7465: getfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7468: aload_1
    //   7469: invokespecial 2504	org/telegram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/telegram/messenger/MessageObject;)I
    //   7472: pop
    //   7473: aload_0
    //   7474: aload_1
    //   7475: invokespecial 2401	org/telegram/ui/Cells/ChatMessageCell:setMessageObjectInternal	(Lorg/telegram/messenger/MessageObject;)V
    //   7478: aload_0
    //   7479: ldc_w 2548
    //   7482: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7485: aload_0
    //   7486: getfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   7489: iadd
    //   7490: putfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   7493: goto -3364 -> 4129
    //   7496: ldc_w 946
    //   7499: fstore 6
    //   7501: goto -59 -> 7442
    //   7504: getstatic 1676	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   7507: getfield 1681	android/graphics/Point:x	I
    //   7510: istore 8
    //   7512: aload_0
    //   7513: getfield 1668	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   7516: ifeq +46 -> 7562
    //   7519: aload_1
    //   7520: invokevirtual 857	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   7523: ifeq +39 -> 7562
    //   7526: aload_1
    //   7527: invokevirtual 575	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   7530: ifne +32 -> 7562
    //   7533: ldc_w 2532
    //   7536: fstore 6
    //   7538: aload_0
    //   7539: iload 8
    //   7541: fload 6
    //   7543: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7546: isub
    //   7547: ldc_w 2533
    //   7550: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7553: invokestatic 1044	java/lang/Math:min	(II)I
    //   7556: putfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7559: goto -96 -> 7463
    //   7562: ldc_w 946
    //   7565: fstore 6
    //   7567: goto -29 -> 7538
    //   7570: aload_1
    //   7571: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   7574: bipush 14
    //   7576: if_icmpne +168 -> 7744
    //   7579: invokestatic 1666	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   7582: ifeq +96 -> 7678
    //   7585: invokestatic 1671	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   7588: istore 8
    //   7590: aload_0
    //   7591: getfield 1668	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   7594: ifeq +76 -> 7670
    //   7597: aload_1
    //   7598: invokevirtual 857	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   7601: ifeq +69 -> 7670
    //   7604: aload_1
    //   7605: invokevirtual 575	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   7608: ifne +62 -> 7670
    //   7611: ldc_w 2532
    //   7614: fstore 6
    //   7616: aload_0
    //   7617: iload 8
    //   7619: fload 6
    //   7621: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7624: isub
    //   7625: ldc_w 2533
    //   7628: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7631: invokestatic 1044	java/lang/Math:min	(II)I
    //   7634: putfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7637: aload_0
    //   7638: aload_0
    //   7639: getfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7642: aload_1
    //   7643: invokespecial 2504	org/telegram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/telegram/messenger/MessageObject;)I
    //   7646: pop
    //   7647: aload_0
    //   7648: aload_1
    //   7649: invokespecial 2401	org/telegram/ui/Cells/ChatMessageCell:setMessageObjectInternal	(Lorg/telegram/messenger/MessageObject;)V
    //   7652: aload_0
    //   7653: ldc_w 2555
    //   7656: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7659: aload_0
    //   7660: getfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   7663: iadd
    //   7664: putfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   7667: goto -3538 -> 4129
    //   7670: ldc_w 946
    //   7673: fstore 6
    //   7675: goto -59 -> 7616
    //   7678: getstatic 1676	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   7681: getfield 1681	android/graphics/Point:x	I
    //   7684: istore 8
    //   7686: aload_0
    //   7687: getfield 1668	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   7690: ifeq +46 -> 7736
    //   7693: aload_1
    //   7694: invokevirtual 857	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   7697: ifeq +39 -> 7736
    //   7700: aload_1
    //   7701: invokevirtual 575	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   7704: ifne +32 -> 7736
    //   7707: ldc_w 2532
    //   7710: fstore 6
    //   7712: aload_0
    //   7713: iload 8
    //   7715: fload 6
    //   7717: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7720: isub
    //   7721: ldc_w 2533
    //   7724: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7727: invokestatic 1044	java/lang/Math:min	(II)I
    //   7730: putfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7733: goto -96 -> 7637
    //   7736: ldc_w 946
    //   7739: fstore 6
    //   7741: goto -29 -> 7712
    //   7744: aload_1
    //   7745: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   7748: getfield 846	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$TL_messageFwdHeader;
    //   7751: ifnull +506 -> 8257
    //   7754: aload_1
    //   7755: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   7758: bipush 13
    //   7760: if_icmpeq +497 -> 8257
    //   7763: iconst_1
    //   7764: istore 29
    //   7766: aload_0
    //   7767: iload 29
    //   7769: putfield 1801	org/telegram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   7772: aload_1
    //   7773: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   7776: bipush 9
    //   7778: if_icmpeq +485 -> 8263
    //   7781: iconst_1
    //   7782: istore 29
    //   7784: aload_0
    //   7785: iload 29
    //   7787: putfield 607	org/telegram/ui/Cells/ChatMessageCell:mediaBackground	Z
    //   7790: aload_0
    //   7791: iconst_1
    //   7792: putfield 765	org/telegram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   7795: aload_0
    //   7796: iconst_1
    //   7797: putfield 711	org/telegram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   7800: iconst_0
    //   7801: istore 11
    //   7803: iconst_0
    //   7804: istore 13
    //   7806: iconst_0
    //   7807: istore 16
    //   7809: iconst_0
    //   7810: istore 17
    //   7812: iconst_0
    //   7813: istore 12
    //   7815: aload_1
    //   7816: getfield 806	org/telegram/messenger/MessageObject:audioProgress	F
    //   7819: fconst_2
    //   7820: fcmpl
    //   7821: ifeq +26 -> 7847
    //   7824: invokestatic 432	org/telegram/messenger/MediaController:getInstance	()Lorg/telegram/messenger/MediaController;
    //   7827: invokevirtual 785	org/telegram/messenger/MediaController:canAutoplayGifs	()Z
    //   7830: ifne +17 -> 7847
    //   7833: aload_1
    //   7834: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   7837: bipush 8
    //   7839: if_icmpne +8 -> 7847
    //   7842: aload_1
    //   7843: fconst_1
    //   7844: putfield 806	org/telegram/messenger/MessageObject:audioProgress	F
    //   7847: aload_0
    //   7848: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   7851: astore 30
    //   7853: aload_1
    //   7854: getfield 806	org/telegram/messenger/MessageObject:audioProgress	F
    //   7857: fconst_0
    //   7858: fcmpl
    //   7859: ifne +410 -> 8269
    //   7862: iconst_1
    //   7863: istore 29
    //   7865: aload 30
    //   7867: iload 29
    //   7869: invokevirtual 809	org/telegram/messenger/ImageReceiver:setAllowStartAnimation	(Z)V
    //   7872: aload_0
    //   7873: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   7876: aload_1
    //   7877: invokevirtual 942	org/telegram/messenger/MessageObject:isSecretPhoto	()Z
    //   7880: invokevirtual 2558	org/telegram/messenger/ImageReceiver:setForcePreview	(Z)V
    //   7883: aload_1
    //   7884: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   7887: bipush 9
    //   7889: if_icmpne +512 -> 8401
    //   7892: invokestatic 1666	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   7895: ifeq +388 -> 8283
    //   7898: invokestatic 1671	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   7901: istore 8
    //   7903: aload_0
    //   7904: getfield 1668	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   7907: ifeq +368 -> 8275
    //   7910: aload_1
    //   7911: invokevirtual 857	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   7914: ifeq +361 -> 8275
    //   7917: aload_1
    //   7918: invokevirtual 575	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   7921: ifne +354 -> 8275
    //   7924: ldc_w 2532
    //   7927: fstore 6
    //   7929: aload_0
    //   7930: iload 8
    //   7932: fload 6
    //   7934: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7937: isub
    //   7938: ldc_w 2533
    //   7941: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7944: invokestatic 1044	java/lang/Math:min	(II)I
    //   7947: putfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7950: aload_0
    //   7951: aload_1
    //   7952: invokespecial 2398	org/telegram/ui/Cells/ChatMessageCell:checkNeedDrawShareButton	(Lorg/telegram/messenger/MessageObject;)Z
    //   7955: ifeq +18 -> 7973
    //   7958: aload_0
    //   7959: aload_0
    //   7960: getfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7963: ldc_w 927
    //   7966: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7969: isub
    //   7970: putfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7973: aload_0
    //   7974: getfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7977: ldc_w 2559
    //   7980: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7983: isub
    //   7984: istore 9
    //   7986: aload_0
    //   7987: iload 9
    //   7989: aload_1
    //   7990: invokespecial 2504	org/telegram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/telegram/messenger/MessageObject;)I
    //   7993: pop
    //   7994: iload 9
    //   7996: istore 8
    //   7998: aload_1
    //   7999: getfield 625	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   8002: invokestatic 799	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   8005: ifne +14 -> 8019
    //   8008: iload 9
    //   8010: ldc_w 1062
    //   8013: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8016: iadd
    //   8017: istore 8
    //   8019: aload_0
    //   8020: getfield 711	org/telegram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   8023: ifeq +326 -> 8349
    //   8026: ldc_w 1062
    //   8029: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8032: istore 10
    //   8034: ldc_w 1062
    //   8037: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8040: istore 11
    //   8042: aload_0
    //   8043: iload 8
    //   8045: putfield 1036	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   8048: iload 12
    //   8050: istore 14
    //   8052: iload 11
    //   8054: istore 8
    //   8056: iload 10
    //   8058: istore 9
    //   8060: aload_0
    //   8061: getfield 711	org/telegram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   8064: ifne +115 -> 8179
    //   8067: iload 12
    //   8069: istore 14
    //   8071: iload 11
    //   8073: istore 8
    //   8075: iload 10
    //   8077: istore 9
    //   8079: aload_1
    //   8080: getfield 625	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   8083: invokestatic 799	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   8086: ifeq +93 -> 8179
    //   8089: iload 12
    //   8091: istore 14
    //   8093: iload 11
    //   8095: istore 8
    //   8097: iload 10
    //   8099: istore 9
    //   8101: aload_0
    //   8102: getfield 1127	org/telegram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   8105: invokevirtual 1093	android/text/StaticLayout:getLineCount	()I
    //   8108: ifle +71 -> 8179
    //   8111: aload_0
    //   8112: aload_1
    //   8113: invokespecial 1040	org/telegram/ui/Cells/ChatMessageCell:measureTime	(Lorg/telegram/messenger/MessageObject;)V
    //   8116: iload 12
    //   8118: istore 14
    //   8120: iload 11
    //   8122: istore 8
    //   8124: iload 10
    //   8126: istore 9
    //   8128: aload_0
    //   8129: getfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8132: ldc_w 2399
    //   8135: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8138: isub
    //   8139: aload_0
    //   8140: getfield 1127	org/telegram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   8143: iconst_0
    //   8144: invokevirtual 654	android/text/StaticLayout:getLineWidth	(I)F
    //   8147: f2d
    //   8148: invokestatic 987	java/lang/Math:ceil	(D)D
    //   8151: d2i
    //   8152: isub
    //   8153: aload_0
    //   8154: getfield 493	org/telegram/ui/Cells/ChatMessageCell:timeWidth	I
    //   8157: if_icmpge +22 -> 8179
    //   8160: iload 11
    //   8162: ldc_w 763
    //   8165: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8168: iadd
    //   8169: istore 8
    //   8171: iload 10
    //   8173: istore 9
    //   8175: iload 12
    //   8177: istore 14
    //   8179: aload_0
    //   8180: aload_1
    //   8181: invokespecial 2401	org/telegram/ui/Cells/ChatMessageCell:setMessageObjectInternal	(Lorg/telegram/messenger/MessageObject;)V
    //   8184: aload_0
    //   8185: getfield 1801	org/telegram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   8188: ifeq +3521 -> 11709
    //   8191: aload_0
    //   8192: aload_0
    //   8193: getfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   8196: ldc_w 1486
    //   8199: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8202: iadd
    //   8203: putfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   8206: aload_0
    //   8207: invokevirtual 534	org/telegram/ui/Cells/ChatMessageCell:invalidate	()V
    //   8210: aload_0
    //   8211: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   8214: iconst_0
    //   8215: ldc_w 608
    //   8218: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8221: aload_0
    //   8222: getfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   8225: iadd
    //   8226: iload 9
    //   8228: iload 8
    //   8230: invokevirtual 1392	org/telegram/messenger/ImageReceiver:setImageCoords	(IIII)V
    //   8233: aload_0
    //   8234: ldc_w 408
    //   8237: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8240: iload 8
    //   8242: iadd
    //   8243: aload_0
    //   8244: getfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   8247: iadd
    //   8248: iload 14
    //   8250: iadd
    //   8251: putfield 484	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   8254: goto -4125 -> 4129
    //   8257: iconst_0
    //   8258: istore 29
    //   8260: goto -494 -> 7766
    //   8263: iconst_0
    //   8264: istore 29
    //   8266: goto -482 -> 7784
    //   8269: iconst_0
    //   8270: istore 29
    //   8272: goto -407 -> 7865
    //   8275: ldc_w 946
    //   8278: fstore 6
    //   8280: goto -351 -> 7929
    //   8283: getstatic 1676	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   8286: getfield 1681	android/graphics/Point:x	I
    //   8289: istore 8
    //   8291: aload_0
    //   8292: getfield 1668	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   8295: ifeq +46 -> 8341
    //   8298: aload_1
    //   8299: invokevirtual 857	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   8302: ifeq +39 -> 8341
    //   8305: aload_1
    //   8306: invokevirtual 575	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   8309: ifne +32 -> 8341
    //   8312: ldc_w 2532
    //   8315: fstore 6
    //   8317: aload_0
    //   8318: iload 8
    //   8320: fload 6
    //   8322: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8325: isub
    //   8326: ldc_w 2533
    //   8329: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8332: invokestatic 1044	java/lang/Math:min	(II)I
    //   8335: putfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8338: goto -388 -> 7950
    //   8341: ldc_w 946
    //   8344: fstore 6
    //   8346: goto -29 -> 8317
    //   8349: ldc_w 1528
    //   8352: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8355: istore 10
    //   8357: ldc_w 1528
    //   8360: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8363: istore 11
    //   8365: aload_1
    //   8366: getfield 625	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   8369: invokestatic 799	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   8372: ifeq +21 -> 8393
    //   8375: ldc_w 1519
    //   8378: fstore 6
    //   8380: iload 8
    //   8382: fload 6
    //   8384: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8387: iadd
    //   8388: istore 8
    //   8390: goto -348 -> 8042
    //   8393: ldc_w 416
    //   8396: fstore 6
    //   8398: goto -18 -> 8380
    //   8401: aload_1
    //   8402: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   8405: iconst_4
    //   8406: if_icmpne +600 -> 9006
    //   8409: aload_1
    //   8410: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8413: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   8416: getfield 1700	org/telegram/tgnet/TLRPC$MessageMedia:geo	Lorg/telegram/tgnet/TLRPC$GeoPoint;
    //   8419: getfield 1706	org/telegram/tgnet/TLRPC$GeoPoint:lat	D
    //   8422: dstore_2
    //   8423: aload_1
    //   8424: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8427: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   8430: getfield 1700	org/telegram/tgnet/TLRPC$MessageMedia:geo	Lorg/telegram/tgnet/TLRPC$GeoPoint;
    //   8433: getfield 1709	org/telegram/tgnet/TLRPC$GeoPoint:_long	D
    //   8436: dstore 4
    //   8438: aload_1
    //   8439: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8442: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   8445: getfield 2560	org/telegram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   8448: ifnull +443 -> 8891
    //   8451: aload_1
    //   8452: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8455: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   8458: getfield 2560	org/telegram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   8461: invokevirtual 910	java/lang/String:length	()I
    //   8464: ifle +427 -> 8891
    //   8467: invokestatic 1666	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   8470: ifeq +347 -> 8817
    //   8473: invokestatic 1671	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   8476: istore 8
    //   8478: aload_0
    //   8479: getfield 1668	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   8482: ifeq +327 -> 8809
    //   8485: aload_1
    //   8486: invokevirtual 857	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   8489: ifeq +320 -> 8809
    //   8492: aload_1
    //   8493: invokevirtual 575	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   8496: ifne +313 -> 8809
    //   8499: ldc_w 2532
    //   8502: fstore 6
    //   8504: aload_0
    //   8505: iload 8
    //   8507: fload 6
    //   8509: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8512: isub
    //   8513: ldc_w 2533
    //   8516: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8519: invokestatic 1044	java/lang/Math:min	(II)I
    //   8522: putfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8525: aload_0
    //   8526: aload_1
    //   8527: invokespecial 2398	org/telegram/ui/Cells/ChatMessageCell:checkNeedDrawShareButton	(Lorg/telegram/messenger/MessageObject;)Z
    //   8530: ifeq +18 -> 8548
    //   8533: aload_0
    //   8534: aload_0
    //   8535: getfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8538: ldc_w 927
    //   8541: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8544: isub
    //   8545: putfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8548: aload_0
    //   8549: getfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8552: ldc_w 2561
    //   8555: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8558: isub
    //   8559: istore 8
    //   8561: aload_0
    //   8562: aload_1
    //   8563: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8566: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   8569: getfield 2560	org/telegram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   8572: getstatic 350	org/telegram/ui/Cells/ChatMessageCell:locationTitlePaint	Landroid/text/TextPaint;
    //   8575: iload 8
    //   8577: getstatic 1085	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   8580: fconst_1
    //   8581: fconst_0
    //   8582: iconst_0
    //   8583: getstatic 1075	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   8586: iload 8
    //   8588: iconst_2
    //   8589: invokestatic 1175	org/telegram/ui/Components/StaticLayoutEx:createStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZLandroid/text/TextUtils$TruncateAt;II)Landroid/text/StaticLayout;
    //   8592: putfield 1177	org/telegram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   8595: aload_0
    //   8596: getfield 1177	org/telegram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   8599: invokevirtual 1093	android/text/StaticLayout:getLineCount	()I
    //   8602: istore 9
    //   8604: aload_1
    //   8605: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8608: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   8611: getfield 2564	org/telegram/tgnet/TLRPC$MessageMedia:address	Ljava/lang/String;
    //   8614: ifnull +269 -> 8883
    //   8617: aload_1
    //   8618: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8621: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   8624: getfield 2564	org/telegram/tgnet/TLRPC$MessageMedia:address	Ljava/lang/String;
    //   8627: invokevirtual 910	java/lang/String:length	()I
    //   8630: ifle +253 -> 8883
    //   8633: aload_0
    //   8634: aload_1
    //   8635: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   8638: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   8641: getfield 2564	org/telegram/tgnet/TLRPC$MessageMedia:address	Ljava/lang/String;
    //   8644: getstatic 352	org/telegram/ui/Cells/ChatMessageCell:locationAddressPaint	Landroid/text/TextPaint;
    //   8647: iload 8
    //   8649: getstatic 1085	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   8652: fconst_1
    //   8653: fconst_0
    //   8654: iconst_0
    //   8655: getstatic 1075	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   8658: iload 8
    //   8660: iconst_3
    //   8661: iconst_3
    //   8662: iload 9
    //   8664: isub
    //   8665: invokestatic 1044	java/lang/Math:min	(II)I
    //   8668: invokestatic 1175	org/telegram/ui/Components/StaticLayoutEx:createStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZLandroid/text/TextUtils$TruncateAt;II)Landroid/text/StaticLayout;
    //   8671: putfield 1127	org/telegram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   8674: aload_0
    //   8675: iconst_0
    //   8676: putfield 607	org/telegram/ui/Cells/ChatMessageCell:mediaBackground	Z
    //   8679: aload_0
    //   8680: iload 8
    //   8682: putfield 1036	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   8685: ldc_w 1062
    //   8688: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8691: istore 9
    //   8693: ldc_w 1062
    //   8696: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8699: istore 8
    //   8701: aload_0
    //   8702: getstatic 1715	java/util/Locale:US	Ljava/util/Locale;
    //   8705: ldc_w 2566
    //   8708: iconst_5
    //   8709: anewarray 1106	java/lang/Object
    //   8712: dup
    //   8713: iconst_0
    //   8714: dload_2
    //   8715: invokestatic 1722	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   8718: aastore
    //   8719: dup
    //   8720: iconst_1
    //   8721: dload 4
    //   8723: invokestatic 1722	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   8726: aastore
    //   8727: dup
    //   8728: iconst_2
    //   8729: iconst_2
    //   8730: getstatic 1725	org/telegram/messenger/AndroidUtilities:density	F
    //   8733: f2d
    //   8734: invokestatic 987	java/lang/Math:ceil	(D)D
    //   8737: d2i
    //   8738: invokestatic 1044	java/lang/Math:min	(II)I
    //   8741: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   8744: aastore
    //   8745: dup
    //   8746: iconst_3
    //   8747: dload_2
    //   8748: invokestatic 1722	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   8751: aastore
    //   8752: dup
    //   8753: iconst_4
    //   8754: dload 4
    //   8756: invokestatic 1722	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   8759: aastore
    //   8760: invokestatic 1728	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   8763: putfield 1696	org/telegram/ui/Cells/ChatMessageCell:currentUrl	Ljava/lang/String;
    //   8766: aload_0
    //   8767: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   8770: astore 31
    //   8772: aload_0
    //   8773: getfield 1696	org/telegram/ui/Cells/ChatMessageCell:currentUrl	Ljava/lang/String;
    //   8776: astore 32
    //   8778: aload_1
    //   8779: invokevirtual 575	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   8782: ifeq +216 -> 8998
    //   8785: getstatic 2569	org/telegram/ui/ActionBar/Theme:geoOutDrawable	Landroid/graphics/drawable/Drawable;
    //   8788: astore 30
    //   8790: aload 31
    //   8792: aload 32
    //   8794: aconst_null
    //   8795: aload 30
    //   8797: aconst_null
    //   8798: iconst_0
    //   8799: invokevirtual 2572	org/telegram/messenger/ImageReceiver:setImage	(Ljava/lang/String;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Ljava/lang/String;I)V
    //   8802: iload 12
    //   8804: istore 14
    //   8806: goto -627 -> 8179
    //   8809: ldc_w 946
    //   8812: fstore 6
    //   8814: goto -310 -> 8504
    //   8817: getstatic 1676	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   8820: getfield 1681	android/graphics/Point:x	I
    //   8823: istore 8
    //   8825: aload_0
    //   8826: getfield 1668	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   8829: ifeq +46 -> 8875
    //   8832: aload_1
    //   8833: invokevirtual 857	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   8836: ifeq +39 -> 8875
    //   8839: aload_1
    //   8840: invokevirtual 575	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   8843: ifne +32 -> 8875
    //   8846: ldc_w 2532
    //   8849: fstore 6
    //   8851: aload_0
    //   8852: iload 8
    //   8854: fload 6
    //   8856: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8859: isub
    //   8860: ldc_w 2533
    //   8863: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8866: invokestatic 1044	java/lang/Math:min	(II)I
    //   8869: putfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8872: goto -347 -> 8525
    //   8875: ldc_w 946
    //   8878: fstore 6
    //   8880: goto -29 -> 8851
    //   8883: aload_0
    //   8884: aconst_null
    //   8885: putfield 1127	org/telegram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   8888: goto -214 -> 8674
    //   8891: aload_0
    //   8892: ldc_w 2573
    //   8895: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8898: putfield 1036	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   8901: ldc_w 1614
    //   8904: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8907: istore 9
    //   8909: ldc_w 1927
    //   8912: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8915: istore 8
    //   8917: aload_0
    //   8918: ldc_w 401
    //   8921: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8924: iload 9
    //   8926: iadd
    //   8927: putfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8930: aload_0
    //   8931: getstatic 1715	java/util/Locale:US	Ljava/util/Locale;
    //   8934: ldc_w 2575
    //   8937: iconst_5
    //   8938: anewarray 1106	java/lang/Object
    //   8941: dup
    //   8942: iconst_0
    //   8943: dload_2
    //   8944: invokestatic 1722	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   8947: aastore
    //   8948: dup
    //   8949: iconst_1
    //   8950: dload 4
    //   8952: invokestatic 1722	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   8955: aastore
    //   8956: dup
    //   8957: iconst_2
    //   8958: iconst_2
    //   8959: getstatic 1725	org/telegram/messenger/AndroidUtilities:density	F
    //   8962: f2d
    //   8963: invokestatic 987	java/lang/Math:ceil	(D)D
    //   8966: d2i
    //   8967: invokestatic 1044	java/lang/Math:min	(II)I
    //   8970: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   8973: aastore
    //   8974: dup
    //   8975: iconst_3
    //   8976: dload_2
    //   8977: invokestatic 1722	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   8980: aastore
    //   8981: dup
    //   8982: iconst_4
    //   8983: dload 4
    //   8985: invokestatic 1722	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   8988: aastore
    //   8989: invokestatic 1728	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   8992: putfield 1696	org/telegram/ui/Cells/ChatMessageCell:currentUrl	Ljava/lang/String;
    //   8995: goto -229 -> 8766
    //   8998: getstatic 2578	org/telegram/ui/ActionBar/Theme:geoInDrawable	Landroid/graphics/drawable/Drawable;
    //   9001: astore 30
    //   9003: goto -213 -> 8790
    //   9006: aload_1
    //   9007: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   9010: bipush 13
    //   9012: if_icmpne +542 -> 9554
    //   9015: aload_0
    //   9016: iconst_0
    //   9017: putfield 284	org/telegram/ui/Cells/ChatMessageCell:drawBackground	Z
    //   9020: iconst_0
    //   9021: istore 10
    //   9023: iload 13
    //   9025: istore 9
    //   9027: iload 11
    //   9029: istore 8
    //   9031: iload 10
    //   9033: aload_1
    //   9034: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   9037: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   9040: getfield 1011	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   9043: getfield 1020	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   9046: invokevirtual 584	java/util/ArrayList:size	()I
    //   9049: if_icmpge +48 -> 9097
    //   9052: aload_1
    //   9053: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   9056: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   9059: getfield 1011	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   9062: getfield 1020	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   9065: iload 10
    //   9067: invokevirtual 588	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   9070: checkcast 1022	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   9073: astore 30
    //   9075: aload 30
    //   9077: instanceof 2455
    //   9080: ifeq +281 -> 9361
    //   9083: aload 30
    //   9085: getfield 2456	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   9088: istore 8
    //   9090: aload 30
    //   9092: getfield 2457	org/telegram/tgnet/TLRPC$DocumentAttribute:h	I
    //   9095: istore 9
    //   9097: invokestatic 1666	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   9100: ifeq +270 -> 9370
    //   9103: invokestatic 1671	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   9106: i2f
    //   9107: ldc_w 2579
    //   9110: fmul
    //   9111: fstore 6
    //   9113: fload 6
    //   9115: fstore 7
    //   9117: iload 9
    //   9119: istore 10
    //   9121: iload 8
    //   9123: istore 9
    //   9125: iload 8
    //   9127: ifne +19 -> 9146
    //   9130: fload 7
    //   9132: f2i
    //   9133: istore 10
    //   9135: iload 10
    //   9137: ldc_w 1927
    //   9140: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9143: iadd
    //   9144: istore 9
    //   9146: iload 10
    //   9148: i2f
    //   9149: fload 6
    //   9151: iload 9
    //   9153: i2f
    //   9154: fdiv
    //   9155: fmul
    //   9156: f2i
    //   9157: istore 8
    //   9159: fload 6
    //   9161: f2i
    //   9162: istore 9
    //   9164: iload 8
    //   9166: istore 11
    //   9168: iload 9
    //   9170: istore 10
    //   9172: iload 8
    //   9174: i2f
    //   9175: fload 7
    //   9177: fcmpl
    //   9178: ifle +21 -> 9199
    //   9181: iload 9
    //   9183: i2f
    //   9184: fload 7
    //   9186: iload 8
    //   9188: i2f
    //   9189: fdiv
    //   9190: fmul
    //   9191: f2i
    //   9192: istore 10
    //   9194: fload 7
    //   9196: f2i
    //   9197: istore 11
    //   9199: aload_0
    //   9200: bipush 6
    //   9202: putfield 497	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   9205: aload_0
    //   9206: iload 10
    //   9208: ldc_w 408
    //   9211: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9214: isub
    //   9215: putfield 1036	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   9218: aload_0
    //   9219: ldc_w 401
    //   9222: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9225: iload 10
    //   9227: iadd
    //   9228: putfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   9231: aload_0
    //   9232: aload_1
    //   9233: getfield 1198	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   9236: bipush 80
    //   9238: invokestatic 1205	org/telegram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;I)Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9241: putfield 1245	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9244: aload_1
    //   9245: getfield 2062	org/telegram/messenger/MessageObject:attachPathExists	Z
    //   9248: ifeq +157 -> 9405
    //   9251: aload_0
    //   9252: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   9255: astore 31
    //   9257: aload_1
    //   9258: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   9261: getfield 2582	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   9264: astore 32
    //   9266: getstatic 1715	java/util/Locale:US	Ljava/util/Locale;
    //   9269: ldc_w 2465
    //   9272: iconst_2
    //   9273: anewarray 1106	java/lang/Object
    //   9276: dup
    //   9277: iconst_0
    //   9278: iload 10
    //   9280: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   9283: aastore
    //   9284: dup
    //   9285: iconst_1
    //   9286: iload 11
    //   9288: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   9291: aastore
    //   9292: invokestatic 1728	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   9295: astore 33
    //   9297: aload_0
    //   9298: getfield 1245	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9301: ifnull +98 -> 9399
    //   9304: aload_0
    //   9305: getfield 1245	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9308: getfield 1149	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   9311: astore 30
    //   9313: aload 31
    //   9315: aconst_null
    //   9316: aload 32
    //   9318: aload 33
    //   9320: aconst_null
    //   9321: aload 30
    //   9323: ldc_w 2469
    //   9326: aload_1
    //   9327: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   9330: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   9333: getfield 1011	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   9336: getfield 1119	org/telegram/tgnet/TLRPC$Document:size	I
    //   9339: ldc_w 2471
    //   9342: iconst_1
    //   9343: invokevirtual 1224	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   9346: iload 12
    //   9348: istore 14
    //   9350: iload 11
    //   9352: istore 8
    //   9354: iload 10
    //   9356: istore 9
    //   9358: goto -1179 -> 8179
    //   9361: iload 10
    //   9363: iconst_1
    //   9364: iadd
    //   9365: istore 10
    //   9367: goto -344 -> 9023
    //   9370: getstatic 1676	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   9373: getfield 1681	android/graphics/Point:x	I
    //   9376: getstatic 1676	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   9379: getfield 1684	android/graphics/Point:y	I
    //   9382: invokestatic 1044	java/lang/Math:min	(II)I
    //   9385: i2f
    //   9386: ldc_w 2463
    //   9389: fmul
    //   9390: fstore 6
    //   9392: fload 6
    //   9394: fstore 7
    //   9396: goto -279 -> 9117
    //   9399: aconst_null
    //   9400: astore 30
    //   9402: goto -89 -> 9313
    //   9405: iload 12
    //   9407: istore 14
    //   9409: iload 11
    //   9411: istore 8
    //   9413: iload 10
    //   9415: istore 9
    //   9417: aload_1
    //   9418: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   9421: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   9424: getfield 1011	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   9427: getfield 2584	org/telegram/tgnet/TLRPC$Document:id	J
    //   9430: lconst_0
    //   9431: lcmp
    //   9432: ifeq -1253 -> 8179
    //   9435: aload_0
    //   9436: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   9439: astore 31
    //   9441: aload_1
    //   9442: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   9445: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   9448: getfield 1011	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   9451: astore 32
    //   9453: getstatic 1715	java/util/Locale:US	Ljava/util/Locale;
    //   9456: ldc_w 2465
    //   9459: iconst_2
    //   9460: anewarray 1106	java/lang/Object
    //   9463: dup
    //   9464: iconst_0
    //   9465: iload 10
    //   9467: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   9470: aastore
    //   9471: dup
    //   9472: iconst_1
    //   9473: iload 11
    //   9475: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   9478: aastore
    //   9479: invokestatic 1728	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   9482: astore 33
    //   9484: aload_0
    //   9485: getfield 1245	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9488: ifnull +60 -> 9548
    //   9491: aload_0
    //   9492: getfield 1245	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9495: getfield 1149	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   9498: astore 30
    //   9500: aload 31
    //   9502: aload 32
    //   9504: aconst_null
    //   9505: aload 33
    //   9507: aconst_null
    //   9508: aload 30
    //   9510: ldc_w 2469
    //   9513: aload_1
    //   9514: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   9517: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   9520: getfield 1011	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   9523: getfield 1119	org/telegram/tgnet/TLRPC$Document:size	I
    //   9526: ldc_w 2471
    //   9529: iconst_1
    //   9530: invokevirtual 1224	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   9533: iload 12
    //   9535: istore 14
    //   9537: iload 11
    //   9539: istore 8
    //   9541: iload 10
    //   9543: istore 9
    //   9545: goto -1366 -> 8179
    //   9548: aconst_null
    //   9549: astore 30
    //   9551: goto -51 -> 9500
    //   9554: invokestatic 1666	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   9557: ifeq +1190 -> 10747
    //   9560: invokestatic 1671	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   9563: i2f
    //   9564: ldc_w 2585
    //   9567: fmul
    //   9568: f2i
    //   9569: istore 8
    //   9571: iload 8
    //   9573: istore 10
    //   9575: iload 8
    //   9577: ldc_w 1927
    //   9580: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9583: iadd
    //   9584: istore 11
    //   9586: iload 10
    //   9588: istore 13
    //   9590: iload 8
    //   9592: istore 9
    //   9594: aload_0
    //   9595: aload_1
    //   9596: invokespecial 2398	org/telegram/ui/Cells/ChatMessageCell:checkNeedDrawShareButton	(Lorg/telegram/messenger/MessageObject;)Z
    //   9599: ifeq +25 -> 9624
    //   9602: iload 10
    //   9604: ldc_w 927
    //   9607: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9610: isub
    //   9611: istore 13
    //   9613: iload 8
    //   9615: ldc_w 927
    //   9618: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9621: isub
    //   9622: istore 9
    //   9624: iload 9
    //   9626: istore 14
    //   9628: iload 9
    //   9630: invokestatic 1201	org/telegram/messenger/AndroidUtilities:getPhotoSize	()I
    //   9633: if_icmple +8 -> 9641
    //   9636: invokestatic 1201	org/telegram/messenger/AndroidUtilities:getPhotoSize	()I
    //   9639: istore 14
    //   9641: iload 11
    //   9643: istore 12
    //   9645: iload 11
    //   9647: invokestatic 1201	org/telegram/messenger/AndroidUtilities:getPhotoSize	()I
    //   9650: if_icmple +8 -> 9658
    //   9653: invokestatic 1201	org/telegram/messenger/AndroidUtilities:getPhotoSize	()I
    //   9656: istore 12
    //   9658: aload_1
    //   9659: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   9662: iconst_1
    //   9663: if_icmpne +1114 -> 10777
    //   9666: aload_0
    //   9667: aload_1
    //   9668: invokespecial 1473	org/telegram/ui/Cells/ChatMessageCell:updateSecretTimeText	(Lorg/telegram/messenger/MessageObject;)V
    //   9671: aload_0
    //   9672: aload_1
    //   9673: getfield 1198	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   9676: bipush 80
    //   9678: invokestatic 1205	org/telegram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;I)Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9681: putfield 1245	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9684: aload_1
    //   9685: getfield 625	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   9688: ifnull +8 -> 9696
    //   9691: aload_0
    //   9692: iconst_0
    //   9693: putfield 607	org/telegram/ui/Cells/ChatMessageCell:mediaBackground	Z
    //   9696: aload_0
    //   9697: aload_1
    //   9698: getfield 1198	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   9701: invokestatic 1201	org/telegram/messenger/AndroidUtilities:getPhotoSize	()I
    //   9704: invokestatic 1205	org/telegram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;I)Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9707: putfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9710: iconst_0
    //   9711: istore 9
    //   9713: iconst_0
    //   9714: istore 8
    //   9716: aload_0
    //   9717: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9720: ifnull +19 -> 9739
    //   9723: aload_0
    //   9724: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9727: aload_0
    //   9728: getfield 1245	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9731: if_acmpne +8 -> 9739
    //   9734: aload_0
    //   9735: aconst_null
    //   9736: putfield 1245	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9739: aload_0
    //   9740: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9743: ifnull +112 -> 9855
    //   9746: aload_0
    //   9747: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9750: getfield 2450	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   9753: i2f
    //   9754: iload 14
    //   9756: i2f
    //   9757: fdiv
    //   9758: fstore 6
    //   9760: aload_0
    //   9761: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9764: getfield 2450	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   9767: i2f
    //   9768: fload 6
    //   9770: fdiv
    //   9771: f2i
    //   9772: istore 9
    //   9774: aload_0
    //   9775: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9778: getfield 2453	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   9781: i2f
    //   9782: fload 6
    //   9784: fdiv
    //   9785: f2i
    //   9786: istore 8
    //   9788: iload 9
    //   9790: istore 10
    //   9792: iload 9
    //   9794: ifne +11 -> 9805
    //   9797: ldc_w 2458
    //   9800: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9803: istore 10
    //   9805: iload 8
    //   9807: istore 11
    //   9809: iload 8
    //   9811: ifne +11 -> 9822
    //   9814: ldc_w 2458
    //   9817: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9820: istore 11
    //   9822: iload 11
    //   9824: iload 12
    //   9826: if_icmple +1091 -> 10917
    //   9829: iload 11
    //   9831: i2f
    //   9832: fstore 6
    //   9834: iload 12
    //   9836: istore 8
    //   9838: fload 6
    //   9840: iload 8
    //   9842: i2f
    //   9843: fdiv
    //   9844: fstore 6
    //   9846: iload 10
    //   9848: i2f
    //   9849: fload 6
    //   9851: fdiv
    //   9852: f2i
    //   9853: istore 9
    //   9855: iload 9
    //   9857: ifeq +16 -> 9873
    //   9860: iload 8
    //   9862: istore 10
    //   9864: iload 9
    //   9866: istore 11
    //   9868: iload 8
    //   9870: ifne +160 -> 10030
    //   9873: iload 8
    //   9875: istore 10
    //   9877: iload 9
    //   9879: istore 11
    //   9881: aload_1
    //   9882: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   9885: bipush 8
    //   9887: if_icmpne +143 -> 10030
    //   9890: iconst_0
    //   9891: istore 15
    //   9893: iload 8
    //   9895: istore 10
    //   9897: iload 9
    //   9899: istore 11
    //   9901: iload 15
    //   9903: aload_1
    //   9904: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   9907: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   9910: getfield 1011	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   9913: getfield 1020	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   9916: invokevirtual 584	java/util/ArrayList:size	()I
    //   9919: if_icmpge +111 -> 10030
    //   9922: aload_1
    //   9923: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   9926: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   9929: getfield 1011	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   9932: getfield 1020	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   9935: iload 15
    //   9937: invokevirtual 588	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   9940: checkcast 1022	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   9943: astore 30
    //   9945: aload 30
    //   9947: instanceof 2455
    //   9950: ifne +11 -> 9961
    //   9953: aload 30
    //   9955: instanceof 1115
    //   9958: ifeq +1129 -> 11087
    //   9961: aload 30
    //   9963: getfield 2456	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   9966: i2f
    //   9967: iload 14
    //   9969: i2f
    //   9970: fdiv
    //   9971: fstore 6
    //   9973: aload 30
    //   9975: getfield 2456	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   9978: i2f
    //   9979: fload 6
    //   9981: fdiv
    //   9982: f2i
    //   9983: istore 8
    //   9985: aload 30
    //   9987: getfield 2457	org/telegram/tgnet/TLRPC$DocumentAttribute:h	I
    //   9990: i2f
    //   9991: fload 6
    //   9993: fdiv
    //   9994: f2i
    //   9995: istore 9
    //   9997: iload 9
    //   9999: iload 12
    //   10001: if_icmple +1004 -> 11005
    //   10004: iload 9
    //   10006: i2f
    //   10007: fstore 6
    //   10009: iload 12
    //   10011: istore 10
    //   10013: fload 6
    //   10015: iload 10
    //   10017: i2f
    //   10018: fdiv
    //   10019: fstore 6
    //   10021: iload 8
    //   10023: i2f
    //   10024: fload 6
    //   10026: fdiv
    //   10027: f2i
    //   10028: istore 11
    //   10030: iload 11
    //   10032: ifeq +12 -> 10044
    //   10035: iload 10
    //   10037: istore 9
    //   10039: iload 10
    //   10041: ifne +15 -> 10056
    //   10044: ldc_w 2458
    //   10047: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10050: istore 9
    //   10052: iload 9
    //   10054: istore 11
    //   10056: iload 11
    //   10058: istore 8
    //   10060: aload_1
    //   10061: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   10064: iconst_3
    //   10065: if_icmpne +36 -> 10101
    //   10068: iload 11
    //   10070: istore 8
    //   10072: iload 11
    //   10074: aload_0
    //   10075: getfield 1125	org/telegram/ui/Cells/ChatMessageCell:infoWidth	I
    //   10078: ldc_w 1563
    //   10081: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10084: iadd
    //   10085: if_icmpge +16 -> 10101
    //   10088: aload_0
    //   10089: getfield 1125	org/telegram/ui/Cells/ChatMessageCell:infoWidth	I
    //   10092: ldc_w 1563
    //   10095: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10098: iadd
    //   10099: istore 8
    //   10101: aload_0
    //   10102: iload 13
    //   10104: ldc_w 408
    //   10107: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10110: isub
    //   10111: putfield 1036	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   10114: aload_0
    //   10115: aload_1
    //   10116: invokespecial 1040	org/telegram/ui/Cells/ChatMessageCell:measureTime	(Lorg/telegram/messenger/MessageObject;)V
    //   10119: aload_0
    //   10120: getfield 493	org/telegram/ui/Cells/ChatMessageCell:timeWidth	I
    //   10123: istore 11
    //   10125: aload_1
    //   10126: invokevirtual 575	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   10129: ifeq +967 -> 11096
    //   10132: bipush 20
    //   10134: istore 10
    //   10136: iload 11
    //   10138: iload 10
    //   10140: bipush 14
    //   10142: iadd
    //   10143: i2f
    //   10144: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10147: iadd
    //   10148: istore 15
    //   10150: iload 8
    //   10152: istore 10
    //   10154: iload 8
    //   10156: iload 15
    //   10158: if_icmpge +7 -> 10165
    //   10161: iload 15
    //   10163: istore 10
    //   10165: aload_1
    //   10166: invokevirtual 942	org/telegram/messenger/MessageObject:isSecretPhoto	()Z
    //   10169: ifeq +24 -> 10193
    //   10172: invokestatic 1666	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   10175: ifeq +927 -> 11102
    //   10178: invokestatic 1671	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   10181: i2f
    //   10182: ldc_w 2463
    //   10185: fmul
    //   10186: f2i
    //   10187: istore 9
    //   10189: iload 9
    //   10191: istore 10
    //   10193: iload 10
    //   10195: istore 12
    //   10197: iload 9
    //   10199: istore 13
    //   10201: aload_0
    //   10202: ldc_w 401
    //   10205: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10208: iload 10
    //   10210: iadd
    //   10211: putfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   10214: aload_0
    //   10215: getfield 607	org/telegram/ui/Cells/ChatMessageCell:mediaBackground	Z
    //   10218: ifne +18 -> 10236
    //   10221: aload_0
    //   10222: aload_0
    //   10223: getfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   10226: ldc_w 1525
    //   10229: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10232: iadd
    //   10233: putfield 286	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   10236: iload 16
    //   10238: istore 8
    //   10240: aload_1
    //   10241: getfield 625	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   10244: ifnull +186 -> 10430
    //   10247: iload 17
    //   10249: istore 11
    //   10251: aload_0
    //   10252: new 288	android/text/StaticLayout
    //   10255: dup
    //   10256: aload_1
    //   10257: getfield 625	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   10260: invokestatic 2481	org/telegram/messenger/MessageObject:getTextPaint	()Landroid/text/TextPaint;
    //   10263: iload 12
    //   10265: ldc_w 581
    //   10268: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10271: isub
    //   10272: getstatic 1085	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   10275: fconst_1
    //   10276: fconst_0
    //   10277: iconst_0
    //   10278: invokespecial 1088	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   10281: putfield 629	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   10284: iload 16
    //   10286: istore 8
    //   10288: iload 17
    //   10290: istore 11
    //   10292: aload_0
    //   10293: getfield 629	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   10296: invokevirtual 1093	android/text/StaticLayout:getLineCount	()I
    //   10299: ifle +131 -> 10430
    //   10302: iload 17
    //   10304: istore 11
    //   10306: aload_0
    //   10307: aload_0
    //   10308: getfield 629	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   10311: invokevirtual 2174	android/text/StaticLayout:getHeight	()I
    //   10314: putfield 639	org/telegram/ui/Cells/ChatMessageCell:captionHeight	I
    //   10317: iload 17
    //   10319: istore 11
    //   10321: iconst_0
    //   10322: aload_0
    //   10323: getfield 639	org/telegram/ui/Cells/ChatMessageCell:captionHeight	I
    //   10326: ldc_w 1525
    //   10329: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10332: iadd
    //   10333: iadd
    //   10334: istore 14
    //   10336: iload 14
    //   10338: istore 11
    //   10340: aload_0
    //   10341: getfield 629	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   10344: aload_0
    //   10345: getfield 629	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   10348: invokevirtual 1093	android/text/StaticLayout:getLineCount	()I
    //   10351: iconst_1
    //   10352: isub
    //   10353: invokevirtual 654	android/text/StaticLayout:getLineWidth	(I)F
    //   10356: fstore 6
    //   10358: iload 14
    //   10360: istore 11
    //   10362: aload_0
    //   10363: getfield 629	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   10366: aload_0
    //   10367: getfield 629	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   10370: invokevirtual 1093	android/text/StaticLayout:getLineCount	()I
    //   10373: iconst_1
    //   10374: isub
    //   10375: invokevirtual 651	android/text/StaticLayout:getLineLeft	(I)F
    //   10378: fstore 7
    //   10380: iload 14
    //   10382: istore 8
    //   10384: iload 14
    //   10386: istore 11
    //   10388: iload 12
    //   10390: ldc_w 763
    //   10393: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10396: isub
    //   10397: i2f
    //   10398: fload 6
    //   10400: fload 7
    //   10402: fadd
    //   10403: fsub
    //   10404: iload 15
    //   10406: i2f
    //   10407: fcmpg
    //   10408: ifge +22 -> 10430
    //   10411: iload 14
    //   10413: istore 11
    //   10415: ldc_w 408
    //   10418: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10421: istore 8
    //   10423: iload 14
    //   10425: iload 8
    //   10427: iadd
    //   10428: istore 8
    //   10430: aload_0
    //   10431: getstatic 1715	java/util/Locale:US	Ljava/util/Locale;
    //   10434: ldc_w 2465
    //   10437: iconst_2
    //   10438: anewarray 1106	java/lang/Object
    //   10441: dup
    //   10442: iconst_0
    //   10443: iload 10
    //   10445: i2f
    //   10446: getstatic 1725	org/telegram/messenger/AndroidUtilities:density	F
    //   10449: fdiv
    //   10450: f2i
    //   10451: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   10454: aastore
    //   10455: dup
    //   10456: iconst_1
    //   10457: iload 9
    //   10459: i2f
    //   10460: getstatic 1725	org/telegram/messenger/AndroidUtilities:density	F
    //   10463: fdiv
    //   10464: f2i
    //   10465: invokestatic 877	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   10468: aastore
    //   10469: invokestatic 1728	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   10472: putfield 1220	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   10475: aload_1
    //   10476: getfield 1198	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   10479: ifnull +14 -> 10493
    //   10482: aload_1
    //   10483: getfield 1198	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   10486: invokevirtual 584	java/util/ArrayList:size	()I
    //   10489: iconst_1
    //   10490: if_icmpgt +20 -> 10510
    //   10493: aload_1
    //   10494: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   10497: iconst_3
    //   10498: if_icmpeq +12 -> 10510
    //   10501: aload_1
    //   10502: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   10505: bipush 8
    //   10507: if_icmpne +37 -> 10544
    //   10510: aload_1
    //   10511: invokevirtual 942	org/telegram/messenger/MessageObject:isSecretPhoto	()Z
    //   10514: ifeq +635 -> 11149
    //   10517: aload_0
    //   10518: new 1182	java/lang/StringBuilder
    //   10521: dup
    //   10522: invokespecial 1183	java/lang/StringBuilder:<init>	()V
    //   10525: aload_0
    //   10526: getfield 1220	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   10529: invokevirtual 1187	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   10532: ldc_w 2587
    //   10535: invokevirtual 1187	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   10538: invokevirtual 1195	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   10541: putfield 1220	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   10544: iconst_0
    //   10545: istore 9
    //   10547: aload_1
    //   10548: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   10551: iconst_3
    //   10552: if_icmpeq +12 -> 10564
    //   10555: aload_1
    //   10556: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   10559: bipush 8
    //   10561: if_icmpne +6 -> 10567
    //   10564: iconst_1
    //   10565: istore 9
    //   10567: aload_0
    //   10568: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   10571: ifnull +26 -> 10597
    //   10574: iload 9
    //   10576: ifne +21 -> 10597
    //   10579: aload_0
    //   10580: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   10583: getfield 1246	org/telegram/tgnet/TLRPC$PhotoSize:size	I
    //   10586: ifne +11 -> 10597
    //   10589: aload_0
    //   10590: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   10593: iconst_m1
    //   10594: putfield 1246	org/telegram/tgnet/TLRPC$PhotoSize:size	I
    //   10597: aload_1
    //   10598: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   10601: iconst_1
    //   10602: if_icmpne +703 -> 11305
    //   10605: aload_0
    //   10606: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   10609: ifnull +670 -> 11279
    //   10612: iconst_1
    //   10613: istore 10
    //   10615: aload_0
    //   10616: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   10619: invokestatic 2519	org/telegram/messenger/FileLoader:getAttachFileName	(Lorg/telegram/tgnet/TLObject;)Ljava/lang/String;
    //   10622: astore 30
    //   10624: aload_1
    //   10625: getfield 2059	org/telegram/messenger/MessageObject:mediaExists	Z
    //   10628: ifeq +551 -> 11179
    //   10631: invokestatic 432	org/telegram/messenger/MediaController:getInstance	()Lorg/telegram/messenger/MediaController;
    //   10634: aload_0
    //   10635: invokevirtual 2085	org/telegram/messenger/MediaController:removeLoadingFileObserver	(Lorg/telegram/messenger/MediaController$FileDownloadProgressListener;)V
    //   10638: iload 10
    //   10640: ifne +24 -> 10664
    //   10643: invokestatic 432	org/telegram/messenger/MediaController:getInstance	()Lorg/telegram/messenger/MediaController;
    //   10646: iconst_1
    //   10647: invokevirtual 2522	org/telegram/messenger/MediaController:canDownloadMedia	(I)Z
    //   10650: ifne +14 -> 10664
    //   10653: invokestatic 1252	org/telegram/messenger/FileLoader:getInstance	()Lorg/telegram/messenger/FileLoader;
    //   10656: aload 30
    //   10658: invokevirtual 2525	org/telegram/messenger/FileLoader:isLoadingFile	(Ljava/lang/String;)Z
    //   10661: ifeq +542 -> 11203
    //   10664: aload_0
    //   10665: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   10668: astore 31
    //   10670: aload_0
    //   10671: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   10674: getfield 1149	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   10677: astore 32
    //   10679: aload_0
    //   10680: getfield 1220	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   10683: astore 33
    //   10685: aload_0
    //   10686: getfield 1245	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   10689: ifnull +496 -> 11185
    //   10692: aload_0
    //   10693: getfield 1245	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   10696: getfield 1149	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   10699: astore 30
    //   10701: aload_0
    //   10702: getfield 1220	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   10705: astore 34
    //   10707: iload 9
    //   10709: ifeq +482 -> 11191
    //   10712: iconst_0
    //   10713: istore 9
    //   10715: aload 31
    //   10717: aload 32
    //   10719: aload 33
    //   10721: aload 30
    //   10723: aload 34
    //   10725: iload 9
    //   10727: aconst_null
    //   10728: iconst_0
    //   10729: invokevirtual 1249	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   10732: iload 8
    //   10734: istore 14
    //   10736: iload 13
    //   10738: istore 8
    //   10740: iload 12
    //   10742: istore 9
    //   10744: goto -2565 -> 8179
    //   10747: getstatic 1676	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   10750: getfield 1681	android/graphics/Point:x	I
    //   10753: getstatic 1676	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   10756: getfield 1684	android/graphics/Point:y	I
    //   10759: invokestatic 1044	java/lang/Math:min	(II)I
    //   10762: i2f
    //   10763: ldc_w 2585
    //   10766: fmul
    //   10767: f2i
    //   10768: istore 8
    //   10770: iload 8
    //   10772: istore 10
    //   10774: goto -1199 -> 9575
    //   10777: aload_1
    //   10778: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   10781: iconst_3
    //   10782: if_icmpne +37 -> 10819
    //   10785: aload_0
    //   10786: iconst_0
    //   10787: aload_1
    //   10788: invokespecial 2504	org/telegram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/telegram/messenger/MessageObject;)I
    //   10791: pop
    //   10792: aload_0
    //   10793: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   10796: iconst_1
    //   10797: invokevirtual 1210	org/telegram/messenger/ImageReceiver:setNeedsQualityThumb	(Z)V
    //   10800: aload_0
    //   10801: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   10804: iconst_1
    //   10805: invokevirtual 1213	org/telegram/messenger/ImageReceiver:setShouldGenerateQualityThumb	(Z)V
    //   10808: aload_0
    //   10809: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   10812: aload_1
    //   10813: invokevirtual 1216	org/telegram/messenger/ImageReceiver:setParentMessageObject	(Lorg/telegram/messenger/MessageObject;)V
    //   10816: goto -1132 -> 9684
    //   10819: aload_1
    //   10820: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   10823: bipush 8
    //   10825: if_icmpne -1141 -> 9684
    //   10828: aload_1
    //   10829: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   10832: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   10835: getfield 1011	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   10838: getfield 1119	org/telegram/tgnet/TLRPC$Document:size	I
    //   10841: i2l
    //   10842: invokestatic 1123	org/telegram/messenger/AndroidUtilities:formatFileSize	(J)Ljava/lang/String;
    //   10845: astore 30
    //   10847: aload_0
    //   10848: getstatic 296	org/telegram/ui/Cells/ChatMessageCell:infoPaint	Landroid/text/TextPaint;
    //   10851: aload 30
    //   10853: invokevirtual 1034	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   10856: f2d
    //   10857: invokestatic 987	java/lang/Math:ceil	(D)D
    //   10860: d2i
    //   10861: putfield 1125	org/telegram/ui/Cells/ChatMessageCell:infoWidth	I
    //   10864: aload_0
    //   10865: new 288	android/text/StaticLayout
    //   10868: dup
    //   10869: aload 30
    //   10871: getstatic 296	org/telegram/ui/Cells/ChatMessageCell:infoPaint	Landroid/text/TextPaint;
    //   10874: aload_0
    //   10875: getfield 1125	org/telegram/ui/Cells/ChatMessageCell:infoWidth	I
    //   10878: getstatic 1085	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   10881: fconst_1
    //   10882: fconst_0
    //   10883: iconst_0
    //   10884: invokespecial 1088	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   10887: putfield 1127	org/telegram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   10890: aload_0
    //   10891: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   10894: iconst_1
    //   10895: invokevirtual 1210	org/telegram/messenger/ImageReceiver:setNeedsQualityThumb	(Z)V
    //   10898: aload_0
    //   10899: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   10902: iconst_1
    //   10903: invokevirtual 1213	org/telegram/messenger/ImageReceiver:setShouldGenerateQualityThumb	(Z)V
    //   10906: aload_0
    //   10907: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   10910: aload_1
    //   10911: invokevirtual 1216	org/telegram/messenger/ImageReceiver:setParentMessageObject	(Lorg/telegram/messenger/MessageObject;)V
    //   10914: goto -1230 -> 9684
    //   10917: iload 11
    //   10919: istore 8
    //   10921: iload 10
    //   10923: istore 9
    //   10925: iload 11
    //   10927: ldc_w 2588
    //   10930: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10933: if_icmpge -1078 -> 9855
    //   10936: ldc_w 2588
    //   10939: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10942: istore 11
    //   10944: aload_0
    //   10945: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   10948: getfield 2453	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   10951: i2f
    //   10952: iload 11
    //   10954: i2f
    //   10955: fdiv
    //   10956: fstore 6
    //   10958: iload 11
    //   10960: istore 8
    //   10962: iload 10
    //   10964: istore 9
    //   10966: aload_0
    //   10967: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   10970: getfield 2450	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   10973: i2f
    //   10974: fload 6
    //   10976: fdiv
    //   10977: iload 14
    //   10979: i2f
    //   10980: fcmpg
    //   10981: ifge -1126 -> 9855
    //   10984: aload_0
    //   10985: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   10988: getfield 2450	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   10991: i2f
    //   10992: fload 6
    //   10994: fdiv
    //   10995: f2i
    //   10996: istore 9
    //   10998: iload 11
    //   11000: istore 8
    //   11002: goto -1147 -> 9855
    //   11005: iload 9
    //   11007: istore 10
    //   11009: iload 8
    //   11011: istore 11
    //   11013: iload 9
    //   11015: ldc_w 2588
    //   11018: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11021: if_icmpge -991 -> 10030
    //   11024: ldc_w 2588
    //   11027: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11030: istore 9
    //   11032: aload 30
    //   11034: getfield 2457	org/telegram/tgnet/TLRPC$DocumentAttribute:h	I
    //   11037: i2f
    //   11038: iload 9
    //   11040: i2f
    //   11041: fdiv
    //   11042: fstore 6
    //   11044: iload 9
    //   11046: istore 10
    //   11048: iload 8
    //   11050: istore 11
    //   11052: aload 30
    //   11054: getfield 2456	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   11057: i2f
    //   11058: fload 6
    //   11060: fdiv
    //   11061: iload 14
    //   11063: i2f
    //   11064: fcmpg
    //   11065: ifge -1035 -> 10030
    //   11068: aload 30
    //   11070: getfield 2456	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   11073: i2f
    //   11074: fload 6
    //   11076: fdiv
    //   11077: f2i
    //   11078: istore 11
    //   11080: iload 9
    //   11082: istore 10
    //   11084: goto -1054 -> 10030
    //   11087: iload 15
    //   11089: iconst_1
    //   11090: iadd
    //   11091: istore 15
    //   11093: goto -1200 -> 9893
    //   11096: iconst_0
    //   11097: istore 10
    //   11099: goto -963 -> 10136
    //   11102: getstatic 1676	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   11105: getfield 1681	android/graphics/Point:x	I
    //   11108: getstatic 1676	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   11111: getfield 1684	android/graphics/Point:y	I
    //   11114: invokestatic 1044	java/lang/Math:min	(II)I
    //   11117: i2f
    //   11118: ldc_w 2463
    //   11121: fmul
    //   11122: f2i
    //   11123: istore 9
    //   11125: iload 9
    //   11127: istore 10
    //   11129: goto -936 -> 10193
    //   11132: astore 30
    //   11134: ldc_w 695
    //   11137: aload 30
    //   11139: invokestatic 701	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   11142: iload 11
    //   11144: istore 8
    //   11146: goto -716 -> 10430
    //   11149: aload_0
    //   11150: new 1182	java/lang/StringBuilder
    //   11153: dup
    //   11154: invokespecial 1183	java/lang/StringBuilder:<init>	()V
    //   11157: aload_0
    //   11158: getfield 1220	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   11161: invokevirtual 1187	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   11164: ldc_w 2590
    //   11167: invokevirtual 1187	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   11170: invokevirtual 1195	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   11173: putfield 1220	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   11176: goto -632 -> 10544
    //   11179: iconst_0
    //   11180: istore 10
    //   11182: goto -544 -> 10638
    //   11185: aconst_null
    //   11186: astore 30
    //   11188: goto -487 -> 10701
    //   11191: aload_0
    //   11192: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   11195: getfield 1246	org/telegram/tgnet/TLRPC$PhotoSize:size	I
    //   11198: istore 9
    //   11200: goto -485 -> 10715
    //   11203: aload_0
    //   11204: iconst_1
    //   11205: putfield 1734	org/telegram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   11208: aload_0
    //   11209: getfield 1245	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   11212: ifnull +41 -> 11253
    //   11215: aload_0
    //   11216: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   11219: aconst_null
    //   11220: aconst_null
    //   11221: aload_0
    //   11222: getfield 1245	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   11225: getfield 1149	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   11228: aload_0
    //   11229: getfield 1220	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   11232: iconst_0
    //   11233: aconst_null
    //   11234: iconst_0
    //   11235: invokevirtual 1249	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   11238: iload 8
    //   11240: istore 14
    //   11242: iload 13
    //   11244: istore 8
    //   11246: iload 12
    //   11248: istore 9
    //   11250: goto -3071 -> 8179
    //   11253: aload_0
    //   11254: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   11257: aconst_null
    //   11258: checkcast 1323	android/graphics/drawable/Drawable
    //   11261: invokevirtual 1230	org/telegram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   11264: iload 8
    //   11266: istore 14
    //   11268: iload 13
    //   11270: istore 8
    //   11272: iload 12
    //   11274: istore 9
    //   11276: goto -3097 -> 8179
    //   11279: aload_0
    //   11280: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   11283: aconst_null
    //   11284: checkcast 1226	android/graphics/drawable/BitmapDrawable
    //   11287: invokevirtual 1230	org/telegram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   11290: iload 8
    //   11292: istore 14
    //   11294: iload 13
    //   11296: istore 8
    //   11298: iload 12
    //   11300: istore 9
    //   11302: goto -3123 -> 8179
    //   11305: aload_1
    //   11306: getfield 755	org/telegram/messenger/MessageObject:type	I
    //   11309: bipush 8
    //   11311: if_icmpne +339 -> 11650
    //   11314: aload_1
    //   11315: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   11318: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   11321: getfield 1011	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   11324: invokestatic 2519	org/telegram/messenger/FileLoader:getAttachFileName	(Lorg/telegram/tgnet/TLObject;)Ljava/lang/String;
    //   11327: astore 30
    //   11329: iconst_0
    //   11330: istore 9
    //   11332: aload_1
    //   11333: getfield 2062	org/telegram/messenger/MessageObject:attachPathExists	Z
    //   11336: ifeq +135 -> 11471
    //   11339: invokestatic 432	org/telegram/messenger/MediaController:getInstance	()Lorg/telegram/messenger/MediaController;
    //   11342: aload_0
    //   11343: invokevirtual 2085	org/telegram/messenger/MediaController:removeLoadingFileObserver	(Lorg/telegram/messenger/MediaController$FileDownloadProgressListener;)V
    //   11346: iconst_1
    //   11347: istore 9
    //   11349: aload_1
    //   11350: invokevirtual 1264	org/telegram/messenger/MessageObject:isSending	()Z
    //   11353: ifne +233 -> 11586
    //   11356: iload 9
    //   11358: ifne +41 -> 11399
    //   11361: invokestatic 432	org/telegram/messenger/MediaController:getInstance	()Lorg/telegram/messenger/MediaController;
    //   11364: bipush 32
    //   11366: invokevirtual 2522	org/telegram/messenger/MediaController:canDownloadMedia	(I)Z
    //   11369: ifeq +19 -> 11388
    //   11372: aload_1
    //   11373: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   11376: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   11379: getfield 1011	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   11382: invokestatic 2593	org/telegram/messenger/MessageObject:isNewGifDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   11385: ifne +14 -> 11399
    //   11388: invokestatic 1252	org/telegram/messenger/FileLoader:getInstance	()Lorg/telegram/messenger/FileLoader;
    //   11391: aload 30
    //   11393: invokevirtual 2525	org/telegram/messenger/FileLoader:isLoadingFile	(Ljava/lang/String;)Z
    //   11396: ifeq +190 -> 11586
    //   11399: iload 9
    //   11401: iconst_1
    //   11402: if_icmpne +100 -> 11502
    //   11405: aload_0
    //   11406: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   11409: astore 32
    //   11411: aload_1
    //   11412: invokevirtual 962	org/telegram/messenger/MessageObject:isSendError	()Z
    //   11415: ifeq +69 -> 11484
    //   11418: aconst_null
    //   11419: astore 30
    //   11421: aload_0
    //   11422: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   11425: ifnull +71 -> 11496
    //   11428: aload_0
    //   11429: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   11432: getfield 1149	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   11435: astore 31
    //   11437: aload 32
    //   11439: aconst_null
    //   11440: aload 30
    //   11442: aconst_null
    //   11443: aconst_null
    //   11444: aload 31
    //   11446: aload_0
    //   11447: getfield 1220	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   11450: iconst_0
    //   11451: aconst_null
    //   11452: iconst_0
    //   11453: invokevirtual 1224	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   11456: iload 8
    //   11458: istore 14
    //   11460: iload 13
    //   11462: istore 8
    //   11464: iload 12
    //   11466: istore 9
    //   11468: goto -3289 -> 8179
    //   11471: aload_1
    //   11472: getfield 2059	org/telegram/messenger/MessageObject:mediaExists	Z
    //   11475: ifeq -126 -> 11349
    //   11478: iconst_2
    //   11479: istore 9
    //   11481: goto -132 -> 11349
    //   11484: aload_1
    //   11485: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   11488: getfield 2582	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   11491: astore 30
    //   11493: goto -72 -> 11421
    //   11496: aconst_null
    //   11497: astore 31
    //   11499: goto -62 -> 11437
    //   11502: aload_0
    //   11503: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   11506: astore 31
    //   11508: aload_1
    //   11509: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   11512: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   11515: getfield 1011	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   11518: astore 32
    //   11520: aload_0
    //   11521: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   11524: ifnull +56 -> 11580
    //   11527: aload_0
    //   11528: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   11531: getfield 1149	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   11534: astore 30
    //   11536: aload 31
    //   11538: aload 32
    //   11540: aconst_null
    //   11541: aload 30
    //   11543: aload_0
    //   11544: getfield 1220	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   11547: aload_1
    //   11548: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   11551: getfield 776	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   11554: getfield 1011	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   11557: getfield 1119	org/telegram/tgnet/TLRPC$Document:size	I
    //   11560: aconst_null
    //   11561: iconst_0
    //   11562: invokevirtual 1249	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   11565: iload 8
    //   11567: istore 14
    //   11569: iload 13
    //   11571: istore 8
    //   11573: iload 12
    //   11575: istore 9
    //   11577: goto -3398 -> 8179
    //   11580: aconst_null
    //   11581: astore 30
    //   11583: goto -47 -> 11536
    //   11586: aload_0
    //   11587: iconst_1
    //   11588: putfield 1734	org/telegram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   11591: aload_0
    //   11592: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   11595: astore 31
    //   11597: aload_0
    //   11598: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   11601: ifnull +43 -> 11644
    //   11604: aload_0
    //   11605: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   11608: getfield 1149	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   11611: astore 30
    //   11613: aload 31
    //   11615: aconst_null
    //   11616: aconst_null
    //   11617: aload 30
    //   11619: aload_0
    //   11620: getfield 1220	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   11623: iconst_0
    //   11624: aconst_null
    //   11625: iconst_0
    //   11626: invokevirtual 1249	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   11629: iload 8
    //   11631: istore 14
    //   11633: iload 13
    //   11635: istore 8
    //   11637: iload 12
    //   11639: istore 9
    //   11641: goto -3462 -> 8179
    //   11644: aconst_null
    //   11645: astore 30
    //   11647: goto -34 -> 11613
    //   11650: aload_0
    //   11651: getfield 442	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   11654: astore 31
    //   11656: aload_0
    //   11657: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   11660: ifnull +43 -> 11703
    //   11663: aload_0
    //   11664: getfield 1207	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   11667: getfield 1149	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   11670: astore 30
    //   11672: aload 31
    //   11674: aconst_null
    //   11675: aconst_null
    //   11676: aload 30
    //   11678: aload_0
    //   11679: getfield 1220	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   11682: iconst_0
    //   11683: aconst_null
    //   11684: iconst_0
    //   11685: invokevirtual 1249	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   11688: iload 8
    //   11690: istore 14
    //   11692: iload 13
    //   11694: istore 8
    //   11696: iload 12
    //   11698: istore 9
    //   11700: goto -3521 -> 8179
    //   11703: aconst_null
    //   11704: astore 30
    //   11706: goto -34 -> 11672
    //   11709: aload_0
    //   11710: getfield 1922	org/telegram/ui/Cells/ChatMessageCell:drawNameLayout	Z
    //   11713: ifeq -3507 -> 8206
    //   11716: aload_1
    //   11717: getfield 770	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   11720: getfield 923	org/telegram/tgnet/TLRPC$Message:reply_to_msg_id	I
    //   11723: ifne -3517 -> 8206
    //   11726: aload_0
    //   11727: aload_0
    //   11728: getfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   11731: ldc_w 608
    //   11734: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11737: iadd
    //   11738: putfield 549	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   11741: goto -3535 -> 8206
    //   11744: iconst_0
    //   11745: istore 8
    //   11747: goto -7518 -> 4229
    //   11750: astore 30
    //   11752: ldc_w 695
    //   11755: aload 30
    //   11757: invokestatic 701	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   11760: goto -7408 -> 4352
    //   11763: ldc_w 581
    //   11766: fstore 6
    //   11768: goto -7293 -> 4475
    //   11771: iload 8
    //   11773: getstatic 1676	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   11776: getfield 1681	android/graphics/Point:x	I
    //   11779: getstatic 1676	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   11782: getfield 1684	android/graphics/Point:y	I
    //   11785: invokestatic 1044	java/lang/Math:min	(II)I
    //   11788: iadd
    //   11789: istore 8
    //   11791: goto -7294 -> 4497
    //   11794: aload_0
    //   11795: getfield 580	org/telegram/ui/Cells/ChatMessageCell:widthForButtons	I
    //   11798: istore 12
    //   11800: ldc_w 1486
    //   11803: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11806: istore 14
    //   11808: iload 9
    //   11810: ifne +360 -> 12170
    //   11813: aload_0
    //   11814: getfield 607	org/telegram/ui/Cells/ChatMessageCell:mediaBackground	Z
    //   11817: ifeq +353 -> 12170
    //   11820: fconst_0
    //   11821: fstore 6
    //   11823: iload 12
    //   11825: iload 14
    //   11827: iload 11
    //   11829: iconst_1
    //   11830: isub
    //   11831: imul
    //   11832: isub
    //   11833: fload 6
    //   11835: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11838: isub
    //   11839: fconst_2
    //   11840: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11843: isub
    //   11844: iload 11
    //   11846: idiv
    //   11847: istore 14
    //   11849: iconst_0
    //   11850: istore 11
    //   11852: iload 8
    //   11854: istore 12
    //   11856: iload 11
    //   11858: aload 30
    //   11860: getfield 2502	org/telegram/tgnet/TLRPC$TL_keyboardButtonRow:buttons	Ljava/util/ArrayList;
    //   11863: invokevirtual 584	java/util/ArrayList:size	()I
    //   11866: if_icmpge -7294 -> 4572
    //   11869: new 14	org/telegram/ui/Cells/ChatMessageCell$BotButton
    //   11872: dup
    //   11873: aload_0
    //   11874: aconst_null
    //   11875: invokespecial 2596	org/telegram/ui/Cells/ChatMessageCell$BotButton:<init>	(Lorg/telegram/ui/Cells/ChatMessageCell;Lorg/telegram/ui/Cells/ChatMessageCell$1;)V
    //   11878: astore 31
    //   11880: aload 31
    //   11882: aload 30
    //   11884: getfield 2502	org/telegram/tgnet/TLRPC$TL_keyboardButtonRow:buttons	Ljava/util/ArrayList;
    //   11887: iload 11
    //   11889: invokevirtual 588	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   11892: checkcast 2598	org/telegram/tgnet/TLRPC$KeyboardButton
    //   11895: invokestatic 2602	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$002	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;Lorg/telegram/tgnet/TLRPC$KeyboardButton;)Lorg/telegram/tgnet/TLRPC$KeyboardButton;
    //   11898: pop
    //   11899: aload 31
    //   11901: invokestatic 614	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$000	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;)Lorg/telegram/tgnet/TLRPC$KeyboardButton;
    //   11904: getfield 2605	org/telegram/tgnet/TLRPC$KeyboardButton:data	[B
    //   11907: invokestatic 2611	org/telegram/messenger/Utilities:bytesToHex	([B)Ljava/lang/String;
    //   11910: astore 32
    //   11912: aload_0
    //   11913: getfield 280	org/telegram/ui/Cells/ChatMessageCell:botButtonsByData	Ljava/util/HashMap;
    //   11916: aload 32
    //   11918: invokevirtual 2614	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   11921: checkcast 14	org/telegram/ui/Cells/ChatMessageCell$BotButton
    //   11924: astore 33
    //   11926: aload 33
    //   11928: ifnull +250 -> 12178
    //   11931: aload 31
    //   11933: aload 33
    //   11935: invokestatic 1593	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$600	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;)F
    //   11938: invokestatic 1618	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$602	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;F)F
    //   11941: pop
    //   11942: aload 31
    //   11944: aload 33
    //   11946: invokestatic 1597	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$700	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;)I
    //   11949: invokestatic 1613	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$702	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;I)I
    //   11952: pop
    //   11953: aload 31
    //   11955: aload 33
    //   11957: invokestatic 1602	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$800	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;)J
    //   11960: invokestatic 1622	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$802	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;J)J
    //   11963: pop2
    //   11964: aload_0
    //   11965: getfield 280	org/telegram/ui/Cells/ChatMessageCell:botButtonsByData	Ljava/util/HashMap;
    //   11968: aload 32
    //   11970: aload 31
    //   11972: invokevirtual 2618	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   11975: pop
    //   11976: aload 31
    //   11978: ldc_w 1486
    //   11981: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11984: iload 14
    //   11986: iadd
    //   11987: iload 11
    //   11989: imul
    //   11990: invokestatic 2621	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$202	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;I)I
    //   11993: pop
    //   11994: aload 31
    //   11996: ldc_w 766
    //   11999: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   12002: iload 10
    //   12004: imul
    //   12005: ldc_w 1486
    //   12008: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   12011: iadd
    //   12012: invokestatic 2624	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$102	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;I)I
    //   12015: pop
    //   12016: aload 31
    //   12018: iload 14
    //   12020: invokestatic 2627	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$302	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;I)I
    //   12023: pop
    //   12024: aload 31
    //   12026: ldc_w 1513
    //   12029: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   12032: invokestatic 2630	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$402	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;I)I
    //   12035: pop
    //   12036: aload 31
    //   12038: new 288	android/text/StaticLayout
    //   12041: dup
    //   12042: aload 31
    //   12044: invokestatic 614	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$000	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;)Lorg/telegram/tgnet/TLRPC$KeyboardButton;
    //   12047: getfield 2633	org/telegram/tgnet/TLRPC$KeyboardButton:text	Ljava/lang/String;
    //   12050: getstatic 367	org/telegram/ui/Cells/ChatMessageCell:botButtonPaint	Landroid/text/TextPaint;
    //   12053: invokevirtual 1995	android/text/TextPaint:getFontMetricsInt	()Landroid/graphics/Paint$FontMetricsInt;
    //   12056: ldc_w 405
    //   12059: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   12062: iconst_0
    //   12063: invokestatic 2001	org/telegram/messenger/Emoji:replaceEmoji	(Ljava/lang/CharSequence;Landroid/graphics/Paint$FontMetricsInt;IZ)Ljava/lang/CharSequence;
    //   12066: getstatic 367	org/telegram/ui/Cells/ChatMessageCell:botButtonPaint	Landroid/text/TextPaint;
    //   12069: iload 14
    //   12071: ldc_w 581
    //   12074: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   12077: isub
    //   12078: i2f
    //   12079: getstatic 1075	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   12082: invokestatic 1079	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   12085: getstatic 367	org/telegram/ui/Cells/ChatMessageCell:botButtonPaint	Landroid/text/TextPaint;
    //   12088: iload 14
    //   12090: ldc_w 581
    //   12093: invokestatic 396	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   12096: isub
    //   12097: getstatic 2636	android/text/Layout$Alignment:ALIGN_CENTER	Landroid/text/Layout$Alignment;
    //   12100: fconst_1
    //   12101: fconst_0
    //   12102: iconst_0
    //   12103: invokespecial 1088	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   12106: invokestatic 2640	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$902	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;Landroid/text/StaticLayout;)Landroid/text/StaticLayout;
    //   12109: pop
    //   12110: aload_0
    //   12111: getfield 275	org/telegram/ui/Cells/ChatMessageCell:botButtons	Ljava/util/ArrayList;
    //   12114: aload 31
    //   12116: invokevirtual 1882	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   12119: pop
    //   12120: iload 8
    //   12122: istore 12
    //   12124: iload 11
    //   12126: aload 30
    //   12128: getfield 2502	org/telegram/tgnet/TLRPC$TL_keyboardButtonRow:buttons	Ljava/util/ArrayList;
    //   12131: invokevirtual 584	java/util/ArrayList:size	()I
    //   12134: iconst_1
    //   12135: isub
    //   12136: if_icmpne +21 -> 12157
    //   12139: iload 8
    //   12141: aload 31
    //   12143: invokestatic 595	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$200	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;)I
    //   12146: aload 31
    //   12148: invokestatic 598	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$300	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;)I
    //   12151: iadd
    //   12152: invokestatic 490	java/lang/Math:max	(II)I
    //   12155: istore 12
    //   12157: iload 11
    //   12159: iconst_1
    //   12160: iadd
    //   12161: istore 11
    //   12163: iload 12
    //   12165: istore 8
    //   12167: goto -315 -> 11852
    //   12170: ldc_w 1525
    //   12173: fstore 6
    //   12175: goto -352 -> 11823
    //   12178: aload 31
    //   12180: invokestatic 1436	java/lang/System:currentTimeMillis	()J
    //   12183: invokestatic 1622	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$802	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;J)J
    //   12186: pop2
    //   12187: goto -223 -> 11964
    //   12190: aload_0
    //   12191: iload 8
    //   12193: putfield 580	org/telegram/ui/Cells/ChatMessageCell:widthForButtons	I
    //   12196: aload_0
    //   12197: invokespecial 2314	org/telegram/ui/Cells/ChatMessageCell:updateWaveform	()V
    //   12200: aload_0
    //   12201: iload 28
    //   12203: invokevirtual 2065	org/telegram/ui/Cells/ChatMessageCell:updateButtonState	(Z)V
    //   12206: return
    //   12207: aload_0
    //   12208: iconst_0
    //   12209: putfield 2244	org/telegram/ui/Cells/ChatMessageCell:substractBackgroundHeight	I
    //   12212: aload_0
    //   12213: iconst_0
    //   12214: putfield 2288	org/telegram/ui/Cells/ChatMessageCell:keyboardHeight	I
    //   12217: goto -21 -> 12196
    //   12220: astore 35
    //   12222: iload 12
    //   12224: istore 10
    //   12226: goto -9076 -> 3150
    //   12229: astore 36
    //   12231: iconst_3
    //   12232: istore 10
    //   12234: iload 17
    //   12236: istore 13
    //   12238: goto -9781 -> 2457
    //   12241: goto -5359 -> 6882
    //   12244: iload 12
    //   12246: istore 11
    //   12248: iload 9
    //   12250: istore 12
    //   12252: iload 13
    //   12254: istore 17
    //   12256: goto -9501 -> 2755
    //   12259: iconst_0
    //   12260: istore 22
    //   12262: goto -7656 -> 4606
    //   12265: iload 23
    //   12267: iload 26
    //   12269: iadd
    //   12270: istore 9
    //   12272: goto -7456 -> 4816
    //   12275: goto -7583 -> 4692
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	12278	0	this	ChatMessageCell
    //   0	12278	1	paramMessageObject	MessageObject
    //   3048	5929	2	d1	double
    //   8436	548	4	d2	double
    //   4276	7898	6	f1	float
    //   4294	6107	7	f2	float
    //   52	12140	8	i	int
    //   524	11747	9	j	int
    //   724	11509	10	k	int
    //   1206	11041	11	m	int
    //   802	11449	12	n	int
    //   805	11448	13	i1	int
    //   1162	10935	14	i2	int
    //   1168	9924	15	i3	int
    //   1089	9196	16	i4	int
    //   1411	10844	17	i5	int
    //   1417	3429	18	i6	int
    //   34	4326	19	i7	int
    //   528	6184	20	i8	int
    //   992	3022	21	i9	int
    //   1414	10847	22	i10	int
    //   1408	10862	23	i11	int
    //   1746	2967	24	i12	int
    //   1175	3717	25	i13	int
    //   1159	11111	26	i14	int
    //   1657	779	27	i15	int
    //   77	12125	28	bool1	boolean
    //   578	7693	29	bool2	boolean
    //   985	10084	30	localObject1	Object
    //   11132	6	30	localException1	Exception
    //   11186	519	30	localObject2	Object
    //   11750	377	30	localException2	Exception
    //   978	11201	31	localObject3	Object
    //   943	11026	32	localObject4	Object
    //   971	10985	33	localObject5	Object
    //   964	1796	34	localObject6	Object
    //   3217	6	34	localException3	Exception
    //   3483	7241	34	localObject7	Object
    //   957	2116	35	str1	String
    //   3144	10	35	localException4	Exception
    //   12220	1	35	localException5	Exception
    //   950	1427	36	str2	String
    //   2447	14	36	localException6	Exception
    //   12229	1	36	localException7	Exception
    //   936	1407	37	localObject8	Object
    //   2355	6	37	localException8	Exception
    // Exception table:
    //   from	to	target	type
    //   1221	1236	2355	java/lang/Exception
    //   1244	1273	2355	java/lang/Exception
    //   1281	1299	2355	java/lang/Exception
    //   1307	1318	2355	java/lang/Exception
    //   1326	1337	2355	java/lang/Exception
    //   1351	1360	2355	java/lang/Exception
    //   1368	1380	2355	java/lang/Exception
    //   1388	1399	2355	java/lang/Exception
    //   1540	1558	2447	java/lang/Exception
    //   1570	1581	2447	java/lang/Exception
    //   1593	1604	2447	java/lang/Exception
    //   1623	1635	2447	java/lang/Exception
    //   1647	1659	2447	java/lang/Exception
    //   1679	1689	2447	java/lang/Exception
    //   1701	1708	2447	java/lang/Exception
    //   1725	1737	2447	java/lang/Exception
    //   1769	1776	2447	java/lang/Exception
    //   1788	1799	2447	java/lang/Exception
    //   1811	1823	2447	java/lang/Exception
    //   1835	1847	2447	java/lang/Exception
    //   2430	2444	2447	java/lang/Exception
    //   3035	3049	2447	java/lang/Exception
    //   2498	2531	3144	java/lang/Exception
    //   2537	2544	3144	java/lang/Exception
    //   2549	2573	3144	java/lang/Exception
    //   3071	3106	3144	java/lang/Exception
    //   2768	2773	3217	java/lang/Exception
    //   2777	2784	3217	java/lang/Exception
    //   2788	2795	3217	java/lang/Exception
    //   2799	2812	3217	java/lang/Exception
    //   2816	2829	3217	java/lang/Exception
    //   2842	2849	3217	java/lang/Exception
    //   2853	2886	3217	java/lang/Exception
    //   2894	2912	3217	java/lang/Exception
    //   2916	2927	3217	java/lang/Exception
    //   2931	2942	3217	java/lang/Exception
    //   2952	2964	3217	java/lang/Exception
    //   2968	2984	3217	java/lang/Exception
    //   2996	3003	3217	java/lang/Exception
    //   3007	3014	3217	java/lang/Exception
    //   3181	3210	3217	java/lang/Exception
    //   4589	4603	3217	java/lang/Exception
    //   4614	4626	3217	java/lang/Exception
    //   4630	4646	3217	java/lang/Exception
    //   4655	4662	3217	java/lang/Exception
    //   4666	4671	3217	java/lang/Exception
    //   4680	4692	3217	java/lang/Exception
    //   4725	4732	3217	java/lang/Exception
    //   4736	4747	3217	java/lang/Exception
    //   4770	4787	3217	java/lang/Exception
    //   4796	4813	3217	java/lang/Exception
    //   4820	4832	3217	java/lang/Exception
    //   4854	4863	3217	java/lang/Exception
    //   4870	4884	3217	java/lang/Exception
    //   10251	10284	11132	java/lang/Exception
    //   10292	10302	11132	java/lang/Exception
    //   10306	10317	11132	java/lang/Exception
    //   10321	10336	11132	java/lang/Exception
    //   10340	10358	11132	java/lang/Exception
    //   10362	10380	11132	java/lang/Exception
    //   10388	10411	11132	java/lang/Exception
    //   10415	10423	11132	java/lang/Exception
    //   4152	4229	11750	java/lang/Exception
    //   4229	4352	11750	java/lang/Exception
    //   2585	2603	12220	java/lang/Exception
    //   2611	2622	12220	java/lang/Exception
    //   2630	2641	12220	java/lang/Exception
    //   2649	2660	12220	java/lang/Exception
    //   2668	2675	12220	java/lang/Exception
    //   2688	2700	12220	java/lang/Exception
    //   2711	2723	12220	java/lang/Exception
    //   2731	2743	12220	java/lang/Exception
    //   3124	3137	12220	java/lang/Exception
    //   1440	1480	12229	java/lang/Exception
    //   1483	1490	12229	java/lang/Exception
    //   1495	1525	12229	java/lang/Exception
    //   2375	2409	12229	java/lang/Exception
  }
  
  public void setPressed(boolean paramBoolean)
  {
    super.setPressed(paramBoolean);
    this.radialProgress.swapBackground(getDrawableForCurrentState());
    if (this.useSeekBarWaweform) {
      this.seekBarWaveform.setSelected(isDrawSelectedBackground());
    }
    for (;;)
    {
      invalidate();
      return;
      this.seekBar.setSelected(isDrawSelectedBackground());
    }
  }
  
  public void setVisiblePart(int paramInt1, int paramInt2)
  {
    if ((this.currentMessageObject == null) || (this.currentMessageObject.textLayoutBlocks == null)) {}
    int j;
    int k;
    int i;
    label85:
    label200:
    do
    {
      return;
      int i2 = paramInt1 - this.textY;
      int m = -1;
      j = -1;
      k = 0;
      i = 0;
      paramInt1 = 0;
      float f;
      int i1;
      int n;
      if ((paramInt1 >= this.currentMessageObject.textLayoutBlocks.size()) || (((MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(paramInt1)).textYOffset > i2))
      {
        paramInt1 = i;
        i = m;
        if (paramInt1 >= this.currentMessageObject.textLayoutBlocks.size()) {
          continue;
        }
        MessageObject.TextLayoutBlock localTextLayoutBlock = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(paramInt1);
        f = localTextLayoutBlock.textYOffset;
        if (!intersect(f, localTextLayoutBlock.height + f, i2, i2 + paramInt2)) {
          break label200;
        }
        j = i;
        if (i == -1) {
          j = paramInt1;
        }
        m = paramInt1;
        i1 = k + 1;
        n = j;
      }
      do
      {
        paramInt1 += 1;
        k = i1;
        i = n;
        j = m;
        break label85;
        i = paramInt1;
        paramInt1 += 1;
        break;
        i1 = k;
        n = i;
        m = j;
      } while (f <= i2);
    } while ((this.lastVisibleBlockNum == j) && (this.firstVisibleBlockNum == i) && (this.totalVisibleBlocksCount == k));
    this.lastVisibleBlockNum = j;
    this.firstVisibleBlockNum = i;
    this.totalVisibleBlocksCount = k;
    invalidate();
  }
  
  public void updateAudioProgress()
  {
    if ((this.currentMessageObject == null) || (this.documentAttach == null)) {
      return;
    }
    int m;
    label74:
    Object localObject;
    if (this.useSeekBarWaweform)
    {
      if (!this.seekBarWaveform.isDragging()) {
        this.seekBarWaveform.setProgress(this.currentMessageObject.audioProgress);
      }
      m = 0;
      k = 0;
      if (this.documentAttachType != 3) {
        break label277;
      }
      if (MediaController.getInstance().isPlayingAudio(this.currentMessageObject)) {
        break label266;
      }
      j = 0;
      i = k;
      if (j < this.documentAttach.attributes.size())
      {
        localObject = (TLRPC.DocumentAttribute)this.documentAttach.attributes.get(j);
        if (!(localObject instanceof TLRPC.TL_documentAttributeAudio)) {
          break label259;
        }
      }
    }
    label259:
    label266:
    for (int i = ((TLRPC.DocumentAttribute)localObject).duration;; i = this.currentMessageObject.audioProgressSec)
    {
      localObject = String.format("%02d:%02d", new Object[] { Integer.valueOf(i / 60), Integer.valueOf(i % 60) });
      if ((this.lastTimeString == null) || ((this.lastTimeString != null) && (!this.lastTimeString.equals(localObject))))
      {
        this.lastTimeString = ((String)localObject);
        this.timeWidthAudio = ((int)Math.ceil(audioTimePaint.measureText((String)localObject)));
        this.durationLayout = new StaticLayout((CharSequence)localObject, audioTimePaint, this.timeWidthAudio, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      }
      invalidate();
      return;
      if (this.seekBar.isDragging()) {
        break;
      }
      this.seekBar.setProgress(this.currentMessageObject.audioProgress);
      break;
      j += 1;
      break label74;
    }
    label277:
    int k = 0;
    int j = 0;
    for (;;)
    {
      i = m;
      if (j < this.documentAttach.attributes.size())
      {
        localObject = (TLRPC.DocumentAttribute)this.documentAttach.attributes.get(j);
        if ((localObject instanceof TLRPC.TL_documentAttributeAudio)) {
          i = ((TLRPC.DocumentAttribute)localObject).duration;
        }
      }
      else
      {
        j = k;
        if (MediaController.getInstance().isPlayingAudio(this.currentMessageObject)) {
          j = this.currentMessageObject.audioProgressSec;
        }
        localObject = String.format("%d:%02d / %d:%02d", new Object[] { Integer.valueOf(j / 60), Integer.valueOf(j % 60), Integer.valueOf(i / 60), Integer.valueOf(i % 60) });
        if ((this.lastTimeString != null) && ((this.lastTimeString == null) || (this.lastTimeString.equals(localObject)))) {
          break;
        }
        this.lastTimeString = ((String)localObject);
        i = (int)Math.ceil(audioTimePaint.measureText((String)localObject));
        this.durationLayout = new StaticLayout((CharSequence)localObject, audioTimePaint, i, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        break;
      }
      j += 1;
    }
  }
  
  public void updateButtonState(boolean paramBoolean)
  {
    Object localObject1 = null;
    boolean bool = false;
    if (this.currentMessageObject.type == 1) {
      if (this.currentPhotoObject != null) {}
    }
    Object localObject2;
    label370:
    float f;
    label454:
    label471:
    label477:
    label482:
    label494:
    label620:
    label681:
    do
    {
      do
      {
        return;
        localObject1 = FileLoader.getAttachFileName(this.currentPhotoObject);
        bool = this.currentMessageObject.mediaExists;
        while (TextUtils.isEmpty((CharSequence)localObject1))
        {
          this.radialProgress.setBackground(null, false, false);
          return;
          if ((this.currentMessageObject.type == 8) || (this.documentAttachType == 4) || (this.currentMessageObject.type == 9) || (this.documentAttachType == 3) || (this.documentAttachType == 5))
          {
            if (this.currentMessageObject.attachPathExists)
            {
              localObject1 = this.currentMessageObject.messageOwner.attachPath;
              bool = true;
            }
            else if ((!this.currentMessageObject.isSendError()) || (this.documentAttachType == 3) || (this.documentAttachType == 5))
            {
              localObject1 = this.currentMessageObject.getFileName();
              bool = this.currentMessageObject.mediaExists;
            }
          }
          else if (this.documentAttachType != 0)
          {
            localObject1 = FileLoader.getAttachFileName(this.documentAttach);
            bool = this.currentMessageObject.mediaExists;
          }
          else if (this.currentPhotoObject != null)
          {
            localObject1 = FileLoader.getAttachFileName(this.currentPhotoObject);
            bool = this.currentMessageObject.mediaExists;
          }
        }
        int i;
        if ((this.currentMessageObject.messageOwner.params != null) && (this.currentMessageObject.messageOwner.params.containsKey("query_id")))
        {
          i = 1;
          if ((this.documentAttachType != 3) && (this.documentAttachType != 5)) {
            break label681;
          }
          if (((!this.currentMessageObject.isOut()) || (!this.currentMessageObject.isSending())) && ((!this.currentMessageObject.isSendError()) || (i == 0))) {
            break label494;
          }
          MediaController.getInstance().addLoadingFileObserver(this.currentMessageObject.messageOwner.attachPath, this.currentMessageObject, this);
          this.buttonState = 4;
          localObject1 = this.radialProgress;
          localObject2 = getDrawableForCurrentState();
          if (i != 0) {
            break label471;
          }
          bool = true;
          ((RadialProgress)localObject1).setBackground((Drawable)localObject2, bool, paramBoolean);
          if (i != 0) {
            break label482;
          }
          localObject2 = ImageLoader.getInstance().getFileProgress(this.currentMessageObject.messageOwner.attachPath);
          localObject1 = localObject2;
          if (localObject2 == null)
          {
            localObject1 = localObject2;
            if (SendMessagesHelper.getInstance().isSendingMessage(this.currentMessageObject.getId())) {
              localObject1 = Float.valueOf(1.0F);
            }
          }
          localObject2 = this.radialProgress;
          if (localObject1 == null) {
            break label477;
          }
          f = ((Float)localObject1).floatValue();
          ((RadialProgress)localObject2).setProgress(f, false);
        }
        for (;;)
        {
          updateAudioProgress();
          return;
          i = 0;
          break;
          bool = false;
          break label370;
          f = 0.0F;
          break label454;
          this.radialProgress.setProgress(0.0F, false);
          continue;
          if (bool)
          {
            MediaController.getInstance().removeLoadingFileObserver(this);
            bool = MediaController.getInstance().isPlayingAudio(this.currentMessageObject);
            if ((!bool) || ((bool) && (MediaController.getInstance().isAudioPaused()))) {}
            for (this.buttonState = 0;; this.buttonState = 1)
            {
              this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
              break;
            }
          }
          MediaController.getInstance().addLoadingFileObserver((String)localObject1, this.currentMessageObject, this);
          if (FileLoader.getInstance().isLoadingFile((String)localObject1)) {
            break label620;
          }
          this.buttonState = 2;
          this.radialProgress.setProgress(0.0F, paramBoolean);
          this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
        }
        this.buttonState = 4;
        localObject1 = ImageLoader.getInstance().getFileProgress((String)localObject1);
        if (localObject1 != null) {
          this.radialProgress.setProgress(((Float)localObject1).floatValue(), paramBoolean);
        }
        for (;;)
        {
          this.radialProgress.setBackground(getDrawableForCurrentState(), true, paramBoolean);
          break;
          this.radialProgress.setProgress(0.0F, paramBoolean);
        }
        if ((this.currentMessageObject.type != 0) || (this.documentAttachType == 1) || (this.documentAttachType == 4)) {
          break;
        }
      } while ((this.currentPhotoObject == null) || (!this.drawImageButton));
      if (!bool)
      {
        MediaController.getInstance().addLoadingFileObserver((String)localObject1, this.currentMessageObject, this);
        f = 0.0F;
        bool = false;
        if (!FileLoader.getInstance().isLoadingFile((String)localObject1))
        {
          if ((!this.cancelLoading) && (((this.documentAttachType == 0) && (MediaController.getInstance().canDownloadMedia(1))) || ((this.documentAttachType == 2) && (MediaController.getInstance().canDownloadMedia(32))))) {
            bool = true;
          }
          for (this.buttonState = 1;; this.buttonState = 0)
          {
            this.radialProgress.setProgress(f, false);
            this.radialProgress.setBackground(getDrawableForCurrentState(), bool, paramBoolean);
            invalidate();
            return;
          }
        }
        bool = true;
        this.buttonState = 1;
        localObject1 = ImageLoader.getInstance().getFileProgress((String)localObject1);
        if (localObject1 != null) {}
        for (f = ((Float)localObject1).floatValue();; f = 0.0F) {
          break;
        }
      }
      MediaController.getInstance().removeLoadingFileObserver(this);
      if ((this.documentAttachType == 2) && (!this.photoImage.isAllowStartAnimation())) {}
      for (this.buttonState = 2;; this.buttonState = -1)
      {
        this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
        invalidate();
        return;
      }
      if ((!this.currentMessageObject.isOut()) || (!this.currentMessageObject.isSending())) {
        break;
      }
    } while ((this.currentMessageObject.messageOwner.attachPath == null) || (this.currentMessageObject.messageOwner.attachPath.length() <= 0));
    MediaController.getInstance().addLoadingFileObserver(this.currentMessageObject.messageOwner.attachPath, this.currentMessageObject, this);
    if ((this.currentMessageObject.messageOwner.attachPath == null) || (!this.currentMessageObject.messageOwner.attachPath.startsWith("http")))
    {
      bool = true;
      localObject1 = this.currentMessageObject.messageOwner.params;
      if ((this.currentMessageObject.messageOwner.message == null) || (localObject1 == null) || ((!((HashMap)localObject1).containsKey("url")) && (!((HashMap)localObject1).containsKey("bot")))) {
        break label1207;
      }
      bool = false;
      this.buttonState = -1;
      label1100:
      this.radialProgress.setBackground(getDrawableForCurrentState(), bool, paramBoolean);
      if (!bool) {
        break label1220;
      }
      localObject2 = ImageLoader.getInstance().getFileProgress(this.currentMessageObject.messageOwner.attachPath);
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = localObject2;
        if (SendMessagesHelper.getInstance().isSendingMessage(this.currentMessageObject.getId())) {
          localObject1 = Float.valueOf(1.0F);
        }
      }
      localObject2 = this.radialProgress;
      if (localObject1 == null) {
        break label1215;
      }
      f = ((Float)localObject1).floatValue();
      label1189:
      ((RadialProgress)localObject2).setProgress(f, false);
    }
    for (;;)
    {
      invalidate();
      return;
      bool = false;
      break;
      label1207:
      this.buttonState = 1;
      break label1100;
      label1215:
      f = 0.0F;
      break label1189;
      label1220:
      this.radialProgress.setProgress(0.0F, false);
    }
    if ((this.currentMessageObject.messageOwner.attachPath != null) && (this.currentMessageObject.messageOwner.attachPath.length() != 0)) {
      MediaController.getInstance().removeLoadingFileObserver(this);
    }
    if (!bool)
    {
      MediaController.getInstance().addLoadingFileObserver((String)localObject1, this.currentMessageObject, this);
      f = 0.0F;
      bool = false;
      if (!FileLoader.getInstance().isLoadingFile((String)localObject1))
      {
        if ((!this.cancelLoading) && (((this.currentMessageObject.type == 1) && (MediaController.getInstance().canDownloadMedia(1))) || ((this.currentMessageObject.type == 8) && (MediaController.getInstance().canDownloadMedia(32)) && (MessageObject.isNewGifDocument(this.currentMessageObject.messageOwner.media.document))))) {
          bool = true;
        }
        for (this.buttonState = 1;; this.buttonState = 0)
        {
          this.radialProgress.setBackground(getDrawableForCurrentState(), bool, paramBoolean);
          this.radialProgress.setProgress(f, false);
          invalidate();
          return;
        }
      }
      bool = true;
      this.buttonState = 1;
      localObject1 = ImageLoader.getInstance().getFileProgress((String)localObject1);
      if (localObject1 != null) {}
      for (f = ((Float)localObject1).floatValue();; f = 0.0F) {
        break;
      }
    }
    MediaController.getInstance().removeLoadingFileObserver(this);
    if ((this.currentMessageObject.type == 8) && (!this.photoImage.isAllowStartAnimation())) {
      this.buttonState = 2;
    }
    for (;;)
    {
      this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
      if (this.photoNotSet) {
        setMessageObject(this.currentMessageObject);
      }
      invalidate();
      return;
      if (this.documentAttachType == 4) {
        this.buttonState = 3;
      } else {
        this.buttonState = -1;
      }
    }
  }
  
  private class BotButton
  {
    private int angle;
    private TLRPC.KeyboardButton button;
    private int height;
    private long lastUpdateTime;
    private float progressAlpha;
    private StaticLayout title;
    private int width;
    private int x;
    private int y;
    
    private BotButton() {}
  }
  
  public static abstract interface ChatMessageCellDelegate
  {
    public abstract boolean canPerformActions();
    
    public abstract void didLongPressed(ChatMessageCell paramChatMessageCell);
    
    public abstract void didPressedBotButton(ChatMessageCell paramChatMessageCell, TLRPC.KeyboardButton paramKeyboardButton);
    
    public abstract void didPressedCancelSendButton(ChatMessageCell paramChatMessageCell);
    
    public abstract void didPressedChannelAvatar(ChatMessageCell paramChatMessageCell, TLRPC.Chat paramChat, int paramInt);
    
    public abstract void didPressedImage(ChatMessageCell paramChatMessageCell);
    
    public abstract void didPressedOther(ChatMessageCell paramChatMessageCell);
    
    public abstract void didPressedReplyMessage(ChatMessageCell paramChatMessageCell, int paramInt);
    
    public abstract void didPressedShare(ChatMessageCell paramChatMessageCell);
    
    public abstract void didPressedUrl(MessageObject paramMessageObject, ClickableSpan paramClickableSpan, boolean paramBoolean);
    
    public abstract void didPressedUserAvatar(ChatMessageCell paramChatMessageCell, TLRPC.User paramUser);
    
    public abstract void didPressedViaBot(ChatMessageCell paramChatMessageCell, String paramString);
    
    public abstract void needOpenWebView(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2);
    
    public abstract boolean needPlayAudio(MessageObject paramMessageObject);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/ChatMessageCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */