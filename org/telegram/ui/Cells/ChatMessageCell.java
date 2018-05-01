package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
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
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStructure;
import android.view.animation.DecelerateInterpolator;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessageObject.GroupedMessagePosition;
import org.telegram.messenger.MessageObject.GroupedMessages;
import org.telegram.messenger.MessageObject.TextLayoutBlock;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
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
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageFwdHeader;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonGame;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRequestGeoLocation;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonSwitchInline;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonUrl;
import org.telegram.tgnet.TLRPC.TL_messageMediaContact;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaGame;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.RadialProgress;
import org.telegram.ui.Components.RoundVideoPlayingDrawable;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SeekBar.SeekBarDelegate;
import org.telegram.ui.Components.SeekBarWaveform;
import org.telegram.ui.Components.StaticLayoutEx;
import org.telegram.ui.Components.URLSpanBotCommand;
import org.telegram.ui.Components.URLSpanMono;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.SecretMediaViewer;

public class ChatMessageCell
  extends BaseCell
  implements DownloadController.FileDownloadProgressListener, ImageReceiver.ImageReceiverDelegate, SeekBar.SeekBarDelegate
{
  private static final int DOCUMENT_ATTACH_TYPE_AUDIO = 3;
  private static final int DOCUMENT_ATTACH_TYPE_DOCUMENT = 1;
  private static final int DOCUMENT_ATTACH_TYPE_GIF = 2;
  private static final int DOCUMENT_ATTACH_TYPE_MUSIC = 5;
  private static final int DOCUMENT_ATTACH_TYPE_NONE = 0;
  private static final int DOCUMENT_ATTACH_TYPE_ROUND = 7;
  private static final int DOCUMENT_ATTACH_TYPE_STICKER = 6;
  private static final int DOCUMENT_ATTACH_TYPE_VIDEO = 4;
  private int TAG;
  private int addedCaptionHeight;
  private StaticLayout adminLayout;
  private boolean allowAssistant;
  private StaticLayout authorLayout;
  private int authorX;
  private int availableTimeWidth;
  private AvatarDrawable avatarDrawable;
  private ImageReceiver avatarImage = new ImageReceiver();
  private boolean avatarPressed;
  private int backgroundDrawableLeft;
  private int backgroundDrawableRight;
  private int backgroundWidth = 100;
  private ArrayList<BotButton> botButtons = new ArrayList();
  private HashMap<String, BotButton> botButtonsByData = new HashMap();
  private HashMap<String, BotButton> botButtonsByPosition = new HashMap();
  private String botButtonsLayout;
  private int buttonPressed;
  private int buttonState;
  private int buttonX;
  private int buttonY;
  private boolean cancelLoading;
  private int captionHeight;
  private StaticLayout captionLayout;
  private int captionOffsetX;
  private int captionWidth;
  private int captionX;
  private int captionY;
  private AvatarDrawable contactAvatarDrawable;
  private float controlsAlpha = 1.0F;
  private int currentAccount = UserConfig.selectedAccount;
  private Drawable currentBackgroundDrawable;
  private CharSequence currentCaption;
  private TLRPC.Chat currentChat;
  private TLRPC.Chat currentForwardChannel;
  private String currentForwardNameString;
  private TLRPC.User currentForwardUser;
  private MessageObject currentMessageObject;
  private MessageObject.GroupedMessages currentMessagesGroup;
  private String currentNameString;
  private TLRPC.FileLocation currentPhoto;
  private String currentPhotoFilter;
  private String currentPhotoFilterThumb;
  private TLRPC.PhotoSize currentPhotoObject;
  private TLRPC.PhotoSize currentPhotoObjectThumb;
  private MessageObject.GroupedMessagePosition currentPosition;
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
  private boolean drawInstantView;
  private int drawInstantViewType;
  private boolean drawJoinChannelView;
  private boolean drawJoinGroupView;
  private boolean drawName;
  private boolean drawNameLayout;
  private boolean drawPhotoImage;
  private boolean drawPinnedBottom;
  private boolean drawPinnedTop;
  private boolean drawRadialCheckBackground;
  private boolean drawShareButton;
  private boolean drawTime = true;
  private boolean drwaShareGoIcon;
  private StaticLayout durationLayout;
  private int durationWidth;
  private int firstVisibleBlockNum;
  private boolean forceNotDrawTime;
  private boolean forwardBotPressed;
  private boolean forwardName;
  private float[] forwardNameOffsetX = new float[2];
  private boolean forwardNamePressed;
  private int forwardNameX;
  private int forwardNameY;
  private StaticLayout[] forwardedNameLayout = new StaticLayout[2];
  private int forwardedNameWidth;
  private boolean fullyDraw;
  private boolean gamePreviewPressed;
  private boolean groupPhotoInvisible;
  private boolean hasGamePreview;
  private boolean hasInvoicePreview;
  private boolean hasLinkPreview;
  private int hasMiniProgress;
  private boolean hasNewLineForTime;
  private boolean hasOldCaptionPreview;
  private int highlightProgress;
  private boolean imagePressed;
  private boolean inLayout;
  private StaticLayout infoLayout;
  private int infoWidth;
  private boolean instantButtonPressed;
  private boolean instantPressed;
  private int instantTextLeftX;
  private int instantTextX;
  private StaticLayout instantViewLayout;
  private Drawable instantViewSelectorDrawable;
  private int instantWidth;
  private Runnable invalidateRunnable = new Runnable()
  {
    public void run()
    {
      ChatMessageCell.this.checkLocationExpired();
      if (ChatMessageCell.this.locationExpired)
      {
        ChatMessageCell.this.invalidate();
        ChatMessageCell.access$202(ChatMessageCell.this, false);
      }
      for (;;)
      {
        return;
        ChatMessageCell.this.invalidate((int)ChatMessageCell.this.rect.left - 5, (int)ChatMessageCell.this.rect.top - 5, (int)ChatMessageCell.this.rect.right + 5, (int)ChatMessageCell.this.rect.bottom + 5);
        if (ChatMessageCell.this.scheduledInvalidate) {
          AndroidUtilities.runOnUIThread(ChatMessageCell.this.invalidateRunnable, 1000L);
        }
      }
    }
  };
  private boolean isAvatarVisible;
  public boolean isChat;
  private boolean isCheckPressed = true;
  private boolean isHighlighted;
  private boolean isHighlightedAnimated;
  private boolean isPressed;
  private boolean isSmallImage;
  private int keyboardHeight;
  private long lastControlsAlphaChangeTime;
  private int lastDeleteDate;
  private int lastHeight;
  private long lastHighlightProgressTime;
  private int lastSendState;
  private int lastTime;
  private int lastViewsCount;
  private int lastVisibleBlockNum;
  private int layoutHeight;
  private int layoutWidth;
  private int linkBlockNum;
  private int linkPreviewHeight;
  private boolean linkPreviewPressed;
  private int linkSelectionBlockNum;
  private boolean locationExpired;
  private ImageReceiver locationImageReceiver;
  private boolean mediaBackground;
  private int mediaOffsetY;
  private boolean mediaWasInvisible;
  private int miniButtonPressed;
  private int miniButtonState;
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
  private StaticLayout photosCountLayout;
  private int photosCountWidth;
  private boolean pinnedBottom;
  private boolean pinnedTop;
  private int pressedBotButton;
  private CharacterStyle pressedLink;
  private int pressedLinkType;
  private int[] pressedState = { 16842910, 16842919 };
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
  private RoundVideoPlayingDrawable roundVideoPlayingDrawable;
  private boolean scheduledInvalidate;
  private Rect scrollRect = new Rect();
  private SeekBar seekBar;
  private SeekBarWaveform seekBarWaveform;
  private int seekBarX;
  private int seekBarY;
  private boolean sharePressed;
  private int shareStartX;
  private int shareStartY;
  private StaticLayout siteNameLayout;
  private boolean siteNameRtl;
  private int siteNameWidth;
  private StaticLayout songLayout;
  private int songX;
  private int substractBackgroundHeight;
  private int textX;
  private int textY;
  private float timeAlpha = 1.0F;
  private int timeAudioX;
  private StaticLayout timeLayout;
  private int timeTextWidth;
  private boolean timeWasInvisible;
  private int timeWidth;
  private int timeWidthAudio;
  private int timeX;
  private StaticLayout titleLayout;
  private int titleX;
  private long totalChangeTime;
  private int totalHeight;
  private int totalVisibleBlocksCount;
  private int unmovedTextX;
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
  private int widthBeforeNewTimeLine;
  private int widthForButtons;
  
  public ChatMessageCell(Context paramContext)
  {
    super(paramContext);
    this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0F));
    this.avatarDrawable = new AvatarDrawable();
    this.replyImageReceiver = new ImageReceiver(this);
    this.locationImageReceiver = new ImageReceiver(this);
    this.locationImageReceiver.setRoundRadius(AndroidUtilities.dp(26.1F));
    this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
    this.contactAvatarDrawable = new AvatarDrawable();
    this.photoImage = new ImageReceiver(this);
    this.photoImage.setDelegate(this);
    this.radialProgress = new RadialProgress(this);
    this.seekBar = new SeekBar(paramContext);
    this.seekBar.setDelegate(this);
    this.seekBarWaveform = new SeekBarWaveform(paramContext);
    this.seekBarWaveform.setDelegate(this);
    this.seekBarWaveform.setParentView(this);
    this.roundVideoPlayingDrawable = new RoundVideoPlayingDrawable(this);
  }
  
  private void calcBackgroundWidth(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((this.hasLinkPreview) || (this.hasOldCaptionPreview) || (this.hasGamePreview) || (this.hasInvoicePreview) || (paramInt1 - this.currentMessageObject.lastLineWidth < paramInt2) || (this.currentMessageObject.hasRtl))
    {
      this.totalHeight += AndroidUtilities.dp(14.0F);
      this.hasNewLineForTime = true;
      this.backgroundWidth = (Math.max(paramInt3, this.currentMessageObject.lastLineWidth) + AndroidUtilities.dp(31.0F));
      paramInt2 = this.backgroundWidth;
      if (this.currentMessageObject.isOutOwner())
      {
        paramInt1 = this.timeWidth + AndroidUtilities.dp(17.0F);
        this.backgroundWidth = Math.max(paramInt2, paramInt1 + AndroidUtilities.dp(31.0F));
      }
    }
    for (;;)
    {
      return;
      paramInt1 = this.timeWidth;
      break;
      paramInt1 = paramInt3 - this.currentMessageObject.lastLineWidth;
      if ((paramInt1 >= 0) && (paramInt1 <= paramInt2)) {
        this.backgroundWidth = (paramInt3 + paramInt2 - paramInt1 + AndroidUtilities.dp(31.0F));
      } else {
        this.backgroundWidth = (Math.max(paramInt3, this.currentMessageObject.lastLineWidth + paramInt2) + AndroidUtilities.dp(31.0F));
      }
    }
  }
  
  private boolean checkAudioMotionEvent(MotionEvent paramMotionEvent)
  {
    boolean bool1;
    if ((this.documentAttachType != 3) && (this.documentAttachType != 5)) {
      bool1 = false;
    }
    boolean bool2;
    label81:
    label161:
    label194:
    int n;
    label278:
    label354:
    label377:
    label387:
    label430:
    label436:
    label450:
    label471:
    label477:
    label537:
    do
    {
      int i;
      int j;
      int k;
      int m;
      do
      {
        return bool1;
        i = (int)paramMotionEvent.getX();
        j = (int)paramMotionEvent.getY();
        if (this.useSeekBarWaweform)
        {
          bool2 = this.seekBarWaveform.onTouch(paramMotionEvent.getAction(), paramMotionEvent.getX() - this.seekBarX - AndroidUtilities.dp(13.0F), paramMotionEvent.getY() - this.seekBarY);
          if (!bool2) {
            break label194;
          }
          if ((this.useSeekBarWaweform) || (paramMotionEvent.getAction() != 0)) {
            break label161;
          }
          getParent().requestDisallowInterceptTouchEvent(true);
        }
        for (;;)
        {
          this.disallowLongPress = true;
          invalidate();
          bool1 = bool2;
          break;
          bool2 = this.seekBar.onTouch(paramMotionEvent.getAction(), paramMotionEvent.getX() - this.seekBarX, paramMotionEvent.getY() - this.seekBarY);
          break label81;
          if ((this.useSeekBarWaweform) && (!this.seekBarWaveform.isStartDraging()) && (paramMotionEvent.getAction() == 1)) {
            didPressedButton(true);
          }
        }
        k = AndroidUtilities.dp(36.0F);
        m = 0;
        n = 0;
        if (this.miniButtonState >= 0)
        {
          n = AndroidUtilities.dp(27.0F);
          if ((i < this.buttonX + n) || (i > this.buttonX + n + k) || (j < this.buttonY + n) || (j > this.buttonY + n + k)) {
            break label430;
          }
          n = 1;
        }
        if (n == 0)
        {
          if ((this.buttonState != 0) && (this.buttonState != 1) && (this.buttonState != 2)) {
            break label477;
          }
          if ((i < this.buttonX - AndroidUtilities.dp(12.0F)) || (i > this.buttonX - AndroidUtilities.dp(12.0F) + this.backgroundWidth)) {
            break label471;
          }
          if (!this.drawInstantView) {
            break label436;
          }
          m = this.buttonY;
          if (j < m) {
            break label471;
          }
          if (!this.drawInstantView) {
            break label450;
          }
          m = this.buttonY + k;
          if (j > m) {
            break label471;
          }
          m = 1;
        }
        if (paramMotionEvent.getAction() != 0) {
          break label537;
        }
        if (m != 0) {
          break;
        }
        bool1 = bool2;
      } while (n == 0);
      if (m != 0) {
        this.buttonPressed = 1;
      }
      for (;;)
      {
        invalidate();
        bool1 = true;
        updateRadialProgressBackground();
        break;
        n = 0;
        break label278;
        m = this.namesOffset + this.mediaOffsetY;
        break label354;
        m = this.namesOffset + this.mediaOffsetY + AndroidUtilities.dp(82.0F);
        break label377;
        m = 0;
        break label387;
        if ((i >= this.buttonX) && (i <= this.buttonX + k) && (j >= this.buttonY) && (j <= this.buttonY + k)) {}
        for (m = 1;; m = 0) {
          break;
        }
        this.miniButtonPressed = 1;
      }
      if (this.buttonPressed != 0)
      {
        if (paramMotionEvent.getAction() == 1)
        {
          this.buttonPressed = 0;
          playSoundEffect(0);
          didPressedButton(true);
          invalidate();
        }
        for (;;)
        {
          updateRadialProgressBackground();
          bool1 = bool2;
          break;
          if (paramMotionEvent.getAction() == 3)
          {
            this.buttonPressed = 0;
            invalidate();
          }
          else if ((paramMotionEvent.getAction() == 2) && (m == 0))
          {
            this.buttonPressed = 0;
            invalidate();
          }
        }
      }
      bool1 = bool2;
    } while (this.miniButtonPressed == 0);
    if (paramMotionEvent.getAction() == 1)
    {
      this.miniButtonPressed = 0;
      playSoundEffect(0);
      didPressedMiniButton(true);
      invalidate();
    }
    for (;;)
    {
      updateRadialProgressBackground();
      bool1 = bool2;
      break;
      if (paramMotionEvent.getAction() == 3)
      {
        this.miniButtonPressed = 0;
        invalidate();
      }
      else if ((paramMotionEvent.getAction() == 2) && (n == 0))
      {
        this.miniButtonPressed = 0;
        invalidate();
      }
    }
  }
  
  private boolean checkBotButtonMotionEvent(MotionEvent paramMotionEvent)
  {
    boolean bool1;
    if ((this.botButtons.isEmpty()) || (this.currentMessageObject.eventId != 0L)) {
      bool1 = false;
    }
    for (;;)
    {
      return bool1;
      int i = (int)paramMotionEvent.getX();
      int j = (int)paramMotionEvent.getY();
      boolean bool2 = false;
      if (paramMotionEvent.getAction() == 0)
      {
        int k;
        if (this.currentMessageObject.isOutOwner()) {
          k = getMeasuredWidth() - this.widthForButtons - AndroidUtilities.dp(10.0F);
        }
        for (int m = 0;; m++)
        {
          bool1 = bool2;
          if (m >= this.botButtons.size()) {
            break;
          }
          paramMotionEvent = (BotButton)this.botButtons.get(m);
          int n = paramMotionEvent.y + this.layoutHeight - AndroidUtilities.dp(2.0F);
          if ((i >= paramMotionEvent.x + k) && (i <= paramMotionEvent.x + k + paramMotionEvent.width) && (j >= n) && (j <= paramMotionEvent.height + n))
          {
            this.pressedBotButton = m;
            invalidate();
            bool1 = true;
            break;
            k = this.backgroundDrawableLeft;
            if (this.mediaBackground) {}
            for (float f = 1.0F;; f = 7.0F)
            {
              k += AndroidUtilities.dp(f);
              break;
            }
          }
        }
      }
      bool1 = bool2;
      if (paramMotionEvent.getAction() == 1)
      {
        bool1 = bool2;
        if (this.pressedBotButton != -1)
        {
          playSoundEffect(0);
          this.delegate.didPressedBotButton(this, ((BotButton)this.botButtons.get(this.pressedBotButton)).button);
          this.pressedBotButton = -1;
          invalidate();
          bool1 = bool2;
        }
      }
    }
  }
  
  private boolean checkCaptionMotionEvent(MotionEvent paramMotionEvent)
  {
    boolean bool;
    if ((!(this.currentCaption instanceof Spannable)) || (this.captionLayout == null)) {
      bool = false;
    }
    for (;;)
    {
      return bool;
      if ((paramMotionEvent.getAction() == 0) || (((this.linkPreviewPressed) || (this.pressedLink != null)) && (paramMotionEvent.getAction() == 1)))
      {
        int i = (int)paramMotionEvent.getX();
        int j = (int)paramMotionEvent.getY();
        if ((i < this.captionX) || (i > this.captionX + this.captionWidth) || (j < this.captionY) || (j > this.captionY + this.captionHeight)) {
          break label446;
        }
        if (paramMotionEvent.getAction() != 0) {
          break label410;
        }
        try
        {
          i -= this.captionX;
          int k = this.captionY;
          k = this.captionLayout.getLineForVertical(j - k);
          j = this.captionLayout.getOffsetForHorizontal(k, i);
          float f = this.captionLayout.getLineLeft(k);
          if ((f <= i) && (this.captionLayout.getLineWidth(k) + f >= i))
          {
            Spannable localSpannable = (Spannable)this.currentCaption;
            CharacterStyle[] arrayOfCharacterStyle = (CharacterStyle[])localSpannable.getSpans(j, j, ClickableSpan.class);
            if (arrayOfCharacterStyle != null)
            {
              paramMotionEvent = arrayOfCharacterStyle;
              if (arrayOfCharacterStyle.length != 0) {}
            }
            else
            {
              paramMotionEvent = (CharacterStyle[])localSpannable.getSpans(j, j, URLSpanMono.class);
            }
            j = 0;
            if (paramMotionEvent.length != 0)
            {
              i = j;
              if (paramMotionEvent.length != 0)
              {
                i = j;
                if ((paramMotionEvent[0] instanceof URLSpanBotCommand))
                {
                  i = j;
                  if (URLSpanBotCommand.enabled) {}
                }
              }
            }
            else
            {
              i = 1;
            }
            if (i == 0)
            {
              this.pressedLink = paramMotionEvent[0];
              this.pressedLinkType = 3;
              resetUrlPaths(false);
              try
              {
                paramMotionEvent = obtainNewUrlPath(false);
                i = localSpannable.getSpanStart(this.pressedLink);
                paramMotionEvent.setCurrentLayout(this.captionLayout, i, 0.0F);
                this.captionLayout.getSelectionPath(i, localSpannable.getSpanEnd(this.pressedLink), paramMotionEvent);
                if ((this.currentMessagesGroup != null) && (getParent() != null)) {
                  ((ViewGroup)getParent()).invalidate();
                }
                invalidate();
                bool = true;
              }
              catch (Exception paramMotionEvent)
              {
                for (;;)
                {
                  FileLog.e(paramMotionEvent);
                }
              }
            }
          }
          bool = false;
        }
        catch (Exception paramMotionEvent)
        {
          FileLog.e(paramMotionEvent);
        }
      }
    }
    for (;;)
    {
      break;
      label410:
      if (this.pressedLinkType == 3)
      {
        this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, false);
        resetPressedLink(3);
        bool = true;
        break;
        label446:
        resetPressedLink(3);
      }
    }
  }
  
  private boolean checkGameMotionEvent(MotionEvent paramMotionEvent)
  {
    boolean bool;
    if (!this.hasGamePreview) {
      bool = false;
    }
    int i;
    for (;;)
    {
      return bool;
      i = (int)paramMotionEvent.getX();
      int j = (int)paramMotionEvent.getY();
      if (paramMotionEvent.getAction() == 0)
      {
        if ((this.drawPhotoImage) && (this.photoImage.isInsideImage(i, j)))
        {
          this.gamePreviewPressed = true;
          bool = true;
          continue;
        }
        if ((this.descriptionLayout != null) && (j >= this.descriptionY)) {
          try
          {
            i -= this.unmovedTextX + AndroidUtilities.dp(10.0F) + this.descriptionX;
            int k = this.descriptionY;
            k = this.descriptionLayout.getLineForVertical(j - k);
            j = this.descriptionLayout.getOffsetForHorizontal(k, i);
            float f = this.descriptionLayout.getLineLeft(k);
            if ((f <= i) && (this.descriptionLayout.getLineWidth(k) + f >= i))
            {
              paramMotionEvent = (Spannable)this.currentMessageObject.linkDescription;
              Object localObject = (ClickableSpan[])paramMotionEvent.getSpans(j, j, ClickableSpan.class);
              j = 0;
              if (localObject.length != 0)
              {
                i = j;
                if (localObject.length != 0)
                {
                  i = j;
                  if ((localObject[0] instanceof URLSpanBotCommand))
                  {
                    i = j;
                    if (URLSpanBotCommand.enabled) {}
                  }
                }
              }
              else
              {
                i = 1;
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
                  bool = true;
                }
                catch (Exception paramMotionEvent)
                {
                  for (;;)
                  {
                    FileLog.e(paramMotionEvent);
                  }
                }
              }
            }
            bool = false;
          }
          catch (Exception paramMotionEvent)
          {
            FileLog.e(paramMotionEvent);
          }
        }
      }
    }
    for (;;)
    {
      break;
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
              if ((this.pressedLink instanceof ClickableSpan)) {
                ((ClickableSpan)this.pressedLink).onClick(this);
              }
            }
          }
          this.gamePreviewPressed = false;
          for (i = 0;; i++) {
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
              bool = true;
              break;
            }
          }
        }
        resetPressedLink(2);
      }
    }
  }
  
  private boolean checkLinkPreviewMotionEvent(MotionEvent paramMotionEvent)
  {
    boolean bool;
    if ((this.currentMessageObject.type != 0) || (!this.hasLinkPreview)) {
      bool = false;
    }
    int i;
    int j;
    for (;;)
    {
      return bool;
      i = (int)paramMotionEvent.getX();
      j = (int)paramMotionEvent.getY();
      if ((i < this.unmovedTextX) || (i > this.unmovedTextX + this.backgroundWidth) || (j < this.textY + this.currentMessageObject.textHeight)) {
        break label900;
      }
      int k = this.textY;
      int m = this.currentMessageObject.textHeight;
      int n = this.linkPreviewHeight;
      if (this.drawInstantView)
      {
        i1 = 46;
        if (j > AndroidUtilities.dp(i1 + 8) + (n + (k + m))) {
          break label900;
        }
        if (paramMotionEvent.getAction() != 0) {
          break;
        }
        if ((this.descriptionLayout == null) || (j < this.descriptionY)) {}
      }
      else
      {
        try
        {
          i1 = i - (this.unmovedTextX + AndroidUtilities.dp(10.0F) + this.descriptionX);
          m = j - this.descriptionY;
          if (m <= this.descriptionLayout.getHeight())
          {
            m = this.descriptionLayout.getLineForVertical(m);
            n = this.descriptionLayout.getOffsetForHorizontal(m, i1);
            float f = this.descriptionLayout.getLineLeft(m);
            if ((f <= i1) && (this.descriptionLayout.getLineWidth(m) + f >= i1))
            {
              paramMotionEvent = (Spannable)this.currentMessageObject.linkDescription;
              Object localObject = (ClickableSpan[])paramMotionEvent.getSpans(n, n, ClickableSpan.class);
              m = 0;
              if (localObject.length != 0)
              {
                i1 = m;
                if (localObject.length != 0)
                {
                  i1 = m;
                  if ((localObject[0] instanceof URLSpanBotCommand))
                  {
                    i1 = m;
                    if (URLSpanBotCommand.enabled) {}
                  }
                }
              }
              else
              {
                i1 = 1;
              }
              if (i1 == 0)
              {
                this.pressedLink = localObject[0];
                this.linkBlockNum = -10;
                this.pressedLinkType = 2;
                resetUrlPaths(false);
                try
                {
                  localObject = obtainNewUrlPath(false);
                  i1 = paramMotionEvent.getSpanStart(this.pressedLink);
                  ((LinkPath)localObject).setCurrentLayout(this.descriptionLayout, i1, 0.0F);
                  this.descriptionLayout.getSelectionPath(i1, paramMotionEvent.getSpanEnd(this.pressedLink), (Path)localObject);
                  invalidate();
                  bool = true;
                  continue;
                  i1 = 0;
                }
                catch (Exception paramMotionEvent)
                {
                  for (;;)
                  {
                    FileLog.e(paramMotionEvent);
                  }
                }
              }
            }
          }
          if (this.pressedLink != null) {
            break label900;
          }
        }
        catch (Exception paramMotionEvent)
        {
          FileLog.e(paramMotionEvent);
        }
      }
      m = AndroidUtilities.dp(48.0F);
      int i1 = 0;
      if (this.miniButtonState >= 0)
      {
        i1 = AndroidUtilities.dp(27.0F);
        if ((i < this.buttonX + i1) || (i > this.buttonX + i1 + m) || (j < this.buttonY + i1) || (j > this.buttonY + i1 + m)) {
          break label546;
        }
      }
      label546:
      for (i1 = 1;; i1 = 0)
      {
        if (i1 == 0) {
          break label552;
        }
        this.miniButtonPressed = 1;
        invalidate();
        bool = true;
        break;
      }
      label552:
      if ((this.drawPhotoImage) && (this.drawImageButton) && (this.buttonState != -1) && (i >= this.buttonX) && (i <= this.buttonX + AndroidUtilities.dp(48.0F)) && (j >= this.buttonY) && (j <= this.buttonY + AndroidUtilities.dp(48.0F)))
      {
        this.buttonPressed = 1;
        bool = true;
      }
      else if (this.drawInstantView)
      {
        this.instantPressed = true;
        if ((Build.VERSION.SDK_INT >= 21) && (this.instantViewSelectorDrawable != null) && (this.instantViewSelectorDrawable.getBounds().contains(i, j)))
        {
          this.instantViewSelectorDrawable.setState(this.pressedState);
          this.instantViewSelectorDrawable.setHotspot(i, j);
          this.instantButtonPressed = true;
        }
        invalidate();
        bool = true;
      }
      else
      {
        if ((this.documentAttachType == 1) || (!this.drawPhotoImage) || (!this.photoImage.isInsideImage(i, j))) {
          break label900;
        }
        this.linkPreviewPressed = true;
        paramMotionEvent = this.currentMessageObject.messageOwner.media.webpage;
        if ((this.documentAttachType == 2) && (this.buttonState == -1) && (SharedConfig.autoplayGifs) && ((this.photoImage.getAnimation() == null) || (!TextUtils.isEmpty(paramMotionEvent.embed_url))))
        {
          this.linkPreviewPressed = false;
          bool = false;
        }
        else
        {
          bool = true;
        }
      }
    }
    if (paramMotionEvent.getAction() == 1) {
      if (this.instantPressed)
      {
        if (this.delegate != null) {
          this.delegate.didPressedInstantButton(this, this.drawInstantViewType);
        }
        playSoundEffect(0);
        if ((Build.VERSION.SDK_INT >= 21) && (this.instantViewSelectorDrawable != null)) {
          this.instantViewSelectorDrawable.setState(StateSet.NOTHING);
        }
        this.instantButtonPressed = false;
        this.instantPressed = false;
        invalidate();
      }
    }
    for (;;)
    {
      label900:
      bool = false;
      break;
      if ((this.pressedLinkType == 2) || (this.buttonPressed != 0) || (this.miniButtonPressed != 0) || (this.linkPreviewPressed))
      {
        if (this.buttonPressed != 0)
        {
          this.buttonPressed = 0;
          playSoundEffect(0);
          didPressedButton(false);
          invalidate();
        }
        else if (this.miniButtonPressed != 0)
        {
          this.miniButtonPressed = 0;
          playSoundEffect(0);
          didPressedMiniButton(false);
          invalidate();
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
              if ((this.pressedLink instanceof ClickableSpan)) {
                ((ClickableSpan)this.pressedLink).onClick(this);
              }
            }
          }
          if (this.documentAttachType == 7) {
            if ((!MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) || (MediaController.getInstance().isMessagePaused())) {
              this.delegate.needPlayMessage(this.currentMessageObject);
            }
          }
          for (;;)
          {
            resetPressedLink(2);
            bool = true;
            break;
            MediaController.getInstance().pauseMessage(this.currentMessageObject);
            continue;
            if ((this.documentAttachType == 2) && (this.drawImageButton))
            {
              if (this.buttonState == -1)
              {
                if (SharedConfig.autoplayGifs)
                {
                  this.delegate.didPressedImage(this);
                }
                else
                {
                  this.buttonState = 2;
                  this.currentMessageObject.gifState = 1.0F;
                  this.photoImage.setAllowStartAnimation(false);
                  this.photoImage.stopAnimation();
                  this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                  invalidate();
                  playSoundEffect(0);
                }
              }
              else if ((this.buttonState == 2) || (this.buttonState == 0))
              {
                didPressedButton(false);
                playSoundEffect(0);
              }
            }
            else
            {
              paramMotionEvent = this.currentMessageObject.messageOwner.media.webpage;
              if ((paramMotionEvent != null) && (!TextUtils.isEmpty(paramMotionEvent.embed_url)))
              {
                this.delegate.needOpenWebView(paramMotionEvent.embed_url, paramMotionEvent.site_name, paramMotionEvent.title, paramMotionEvent.url, paramMotionEvent.embed_width, paramMotionEvent.embed_height);
              }
              else if ((this.buttonState == -1) || (this.buttonState == 3))
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
      else
      {
        resetPressedLink(2);
        continue;
        if ((paramMotionEvent.getAction() == 2) && (this.instantButtonPressed) && (Build.VERSION.SDK_INT >= 21) && (this.instantViewSelectorDrawable != null)) {
          this.instantViewSelectorDrawable.setHotspot(i, j);
        }
      }
    }
  }
  
  private void checkLocationExpired()
  {
    if (this.currentMessageObject == null) {}
    for (;;)
    {
      return;
      boolean bool = isCurrentLocationTimeExpired(this.currentMessageObject);
      if (bool != this.locationExpired)
      {
        this.locationExpired = bool;
        if (!this.locationExpired)
        {
          AndroidUtilities.runOnUIThread(this.invalidateRunnable, 1000L);
          this.scheduledInvalidate = true;
          int i = this.backgroundWidth;
          int j = AndroidUtilities.dp(91.0F);
          this.docTitleLayout = new StaticLayout(LocaleController.getString("AttachLiveLocation", NUM), Theme.chat_locationTitlePaint, i - j, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        }
        else
        {
          MessageObject localMessageObject = this.currentMessageObject;
          this.currentMessageObject = null;
          setMessageObject(localMessageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
        }
      }
    }
  }
  
  private boolean checkNeedDrawShareButton(MessageObject paramMessageObject)
  {
    boolean bool1 = true;
    boolean bool2;
    if ((this.currentPosition != null) && (!this.currentPosition.last)) {
      bool2 = false;
    }
    for (;;)
    {
      return bool2;
      if (paramMessageObject.eventId != 0L)
      {
        bool2 = false;
      }
      else if ((paramMessageObject.messageOwner.fwd_from != null) && (!paramMessageObject.isOutOwner()) && (paramMessageObject.messageOwner.fwd_from.saved_from_peer != null) && (paramMessageObject.getDialogId() == UserConfig.getInstance(this.currentAccount).getClientUserId()))
      {
        this.drwaShareGoIcon = true;
        bool2 = bool1;
      }
      else if (paramMessageObject.type == 13)
      {
        bool2 = false;
      }
      else if ((paramMessageObject.messageOwner.fwd_from != null) && (paramMessageObject.messageOwner.fwd_from.channel_id != 0))
      {
        bool2 = bool1;
        if (!paramMessageObject.isOutOwner()) {}
      }
      else
      {
        if (paramMessageObject.isFromUser())
        {
          if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaEmpty)) || (paramMessageObject.messageOwner.media == null) || (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) && (!(paramMessageObject.messageOwner.media.webpage instanceof TLRPC.TL_webPage))))
          {
            bool2 = false;
            continue;
          }
          Object localObject = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(paramMessageObject.messageOwner.from_id));
          if (localObject != null)
          {
            bool2 = bool1;
            if (((TLRPC.User)localObject).bot) {
              continue;
            }
          }
          if (!paramMessageObject.isOut())
          {
            bool2 = bool1;
            if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame)) {
              continue;
            }
            bool2 = bool1;
            if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice)) {
              continue;
            }
            if (paramMessageObject.isMegagroup())
            {
              localObject = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(paramMessageObject.messageOwner.to_id.channel_id));
              if ((localObject != null) && (((TLRPC.Chat)localObject).username != null) && (((TLRPC.Chat)localObject).username.length() > 0) && (!(paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact)))
              {
                bool2 = bool1;
                if (!(paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) {
                  continue;
                }
              }
              bool2 = false;
            }
          }
        }
        else if (((paramMessageObject.messageOwner.from_id < 0) || (paramMessageObject.messageOwner.post)) && (paramMessageObject.messageOwner.to_id.channel_id != 0))
        {
          if (paramMessageObject.messageOwner.via_bot_id == 0)
          {
            bool2 = bool1;
            if (paramMessageObject.messageOwner.reply_to_msg_id == 0) {
              continue;
            }
          }
          bool2 = bool1;
          if (paramMessageObject.type != 13) {
            continue;
          }
        }
        bool2 = false;
      }
    }
  }
  
  private boolean checkOtherButtonMotionEvent(MotionEvent paramMotionEvent)
  {
    boolean bool1 = false;
    if (this.currentMessageObject.type == 16)
    {
      i = 1;
      j = i;
      if (i == 0) {
        if (((this.documentAttachType != 1) && (this.currentMessageObject.type != 12) && (this.documentAttachType != 5) && (this.documentAttachType != 4) && (this.documentAttachType != 2) && (this.currentMessageObject.type != 8)) || (this.hasGamePreview) || (this.hasInvoicePreview)) {
          break label108;
        }
      }
    }
    label108:
    for (int j = 1;; j = 0)
    {
      if (j != 0) {
        break label114;
      }
      return bool1;
      i = 0;
      break;
    }
    label114:
    int i = (int)paramMotionEvent.getX();
    j = (int)paramMotionEvent.getY();
    boolean bool2 = false;
    if (paramMotionEvent.getAction() == 0) {
      if (this.currentMessageObject.type == 16)
      {
        bool1 = bool2;
        if (i >= this.otherX)
        {
          bool1 = bool2;
          if (i <= this.otherX + AndroidUtilities.dp(235.0F))
          {
            bool1 = bool2;
            if (j >= this.otherY - AndroidUtilities.dp(14.0F))
            {
              bool1 = bool2;
              if (j <= this.otherY + AndroidUtilities.dp(50.0F))
              {
                this.otherPressed = true;
                bool1 = true;
                invalidate();
              }
            }
          }
        }
      }
    }
    for (;;)
    {
      break;
      bool1 = bool2;
      if (i >= this.otherX - AndroidUtilities.dp(20.0F))
      {
        bool1 = bool2;
        if (i <= this.otherX + AndroidUtilities.dp(20.0F))
        {
          bool1 = bool2;
          if (j >= this.otherY - AndroidUtilities.dp(4.0F))
          {
            bool1 = bool2;
            if (j <= this.otherY + AndroidUtilities.dp(30.0F))
            {
              this.otherPressed = true;
              bool1 = true;
              invalidate();
              continue;
              bool1 = bool2;
              if (paramMotionEvent.getAction() == 1)
              {
                bool1 = bool2;
                if (this.otherPressed)
                {
                  this.otherPressed = false;
                  playSoundEffect(0);
                  this.delegate.didPressedOther(this);
                  invalidate();
                  bool1 = true;
                }
              }
            }
          }
        }
      }
    }
  }
  
  private boolean checkPhotoImageMotionEvent(MotionEvent paramMotionEvent)
  {
    boolean bool1 = false;
    if ((!this.drawPhotoImage) && (this.documentAttachType != 1)) {
      return bool1;
    }
    int i = (int)paramMotionEvent.getX();
    int j = (int)paramMotionEvent.getY();
    bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    int k;
    int m;
    if (paramMotionEvent.getAction() == 0)
    {
      k = 0;
      m = AndroidUtilities.dp(48.0F);
      if (this.miniButtonState >= 0)
      {
        k = AndroidUtilities.dp(27.0F);
        if ((i >= this.buttonX + k) && (i <= this.buttonX + k + m) && (j >= this.buttonY + k) && (j <= this.buttonY + k + m)) {
          k = 1;
        }
      }
      else
      {
        label128:
        if (k == 0) {
          break label184;
        }
        this.miniButtonPressed = 1;
        invalidate();
        bool1 = true;
        label144:
        bool3 = bool1;
        if (this.imagePressed)
        {
          if (!this.currentMessageObject.isSendError()) {
            break label507;
          }
          this.imagePressed = false;
          bool3 = false;
        }
      }
    }
    label184:
    label507:
    label671:
    do
    {
      for (;;)
      {
        bool1 = bool3;
        break;
        k = 0;
        break label128;
        if ((this.buttonState != -1) && (i >= this.buttonX) && (i <= this.buttonX + m) && (j >= this.buttonY) && (j <= this.buttonY + m))
        {
          this.buttonPressed = 1;
          invalidate();
          bool1 = true;
          break label144;
        }
        if (this.documentAttachType == 1)
        {
          bool1 = bool3;
          if (i < this.photoImage.getImageX()) {
            break label144;
          }
          bool1 = bool3;
          if (i > this.photoImage.getImageX() + this.backgroundWidth - AndroidUtilities.dp(50.0F)) {
            break label144;
          }
          bool1 = bool3;
          if (j < this.photoImage.getImageY()) {
            break label144;
          }
          bool1 = bool3;
          if (j > this.photoImage.getImageY() + this.photoImage.getImageHeight()) {
            break label144;
          }
          this.imagePressed = true;
          bool1 = true;
          break label144;
        }
        if (this.currentMessageObject.type == 13)
        {
          bool1 = bool3;
          if (this.currentMessageObject.getInputStickerSet() == null) {
            break label144;
          }
        }
        bool3 = bool2;
        if (i >= this.photoImage.getImageX())
        {
          bool3 = bool2;
          if (i <= this.photoImage.getImageX() + this.backgroundWidth)
          {
            bool3 = bool2;
            if (j >= this.photoImage.getImageY())
            {
              bool3 = bool2;
              if (j <= this.photoImage.getImageY() + this.photoImage.getImageHeight())
              {
                this.imagePressed = true;
                bool3 = true;
              }
            }
          }
        }
        bool1 = bool3;
        if (this.currentMessageObject.type != 12) {
          break label144;
        }
        bool1 = bool3;
        if (MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.currentMessageObject.messageOwner.media.user_id)) != null) {
          break label144;
        }
        this.imagePressed = false;
        bool1 = false;
        break label144;
        if ((this.currentMessageObject.type == 8) && (this.buttonState == -1) && (SharedConfig.autoplayGifs) && (this.photoImage.getAnimation() == null))
        {
          this.imagePressed = false;
          bool3 = false;
        }
        else
        {
          bool3 = bool1;
          if (this.currentMessageObject.type == 5)
          {
            bool3 = bool1;
            if (this.buttonState != -1)
            {
              this.imagePressed = false;
              bool3 = false;
              continue;
              bool3 = bool1;
              if (paramMotionEvent.getAction() == 1) {
                if (this.buttonPressed == 1)
                {
                  this.buttonPressed = 0;
                  playSoundEffect(0);
                  didPressedButton(false);
                  updateRadialProgressBackground();
                  invalidate();
                  bool3 = bool1;
                }
                else
                {
                  if (this.miniButtonPressed != 1) {
                    break label671;
                  }
                  this.miniButtonPressed = 0;
                  playSoundEffect(0);
                  didPressedMiniButton(false);
                  invalidate();
                  bool3 = bool1;
                }
              }
            }
          }
        }
      }
      bool3 = bool1;
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
      bool3 = bool1;
      break;
      if ((this.buttonState == 0) && (this.documentAttachType == 1))
      {
        playSoundEffect(0);
        didPressedButton(false);
      }
    }
  }
  
  private boolean checkTextBlockMotionEvent(MotionEvent paramMotionEvent)
  {
    boolean bool;
    if ((this.currentMessageObject.type != 0) || (this.currentMessageObject.textLayoutBlocks == null) || (this.currentMessageObject.textLayoutBlocks.isEmpty()) || (!(this.currentMessageObject.messageText instanceof Spannable))) {
      bool = false;
    }
    label151:
    label236:
    Object localObject;
    for (;;)
    {
      return bool;
      if ((paramMotionEvent.getAction() == 0) || ((paramMotionEvent.getAction() == 1) && (this.pressedLinkType == 1)))
      {
        int i = (int)paramMotionEvent.getX();
        int j = (int)paramMotionEvent.getY();
        if ((i < this.textX) || (j < this.textY) || (i > this.textX + this.currentMessageObject.textWidth) || (j > this.textY + this.currentMessageObject.textHeight)) {
          break label968;
        }
        int k = j - this.textY;
        int m = 0;
        j = 0;
        if ((j >= this.currentMessageObject.textLayoutBlocks.size()) || (((MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(j)).textYOffset > k)) {}
        try
        {
          MessageObject.TextLayoutBlock localTextLayoutBlock = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(m);
          float f1 = i;
          float f2 = this.textX;
          float f3;
          Spannable localSpannable;
          CharacterStyle[] arrayOfCharacterStyle;
          if (localTextLayoutBlock.isRtl())
          {
            f3 = this.currentMessageObject.textXOffset;
            j = (int)(f1 - (f2 - f3));
            i = (int)(k - localTextLayoutBlock.textYOffset);
            k = localTextLayoutBlock.textLayout.getLineForVertical(i);
            i = localTextLayoutBlock.textLayout.getOffsetForHorizontal(k, j);
            f3 = localTextLayoutBlock.textLayout.getLineLeft(k);
            if ((f3 <= j) && (localTextLayoutBlock.textLayout.getLineWidth(k) + f3 >= j))
            {
              localSpannable = (Spannable)this.currentMessageObject.messageText;
              arrayOfCharacterStyle = (CharacterStyle[])localSpannable.getSpans(i, i, ClickableSpan.class);
              j = 0;
              if (arrayOfCharacterStyle != null)
              {
                localObject = arrayOfCharacterStyle;
                if (arrayOfCharacterStyle.length != 0) {}
              }
              else
              {
                localObject = (CharacterStyle[])localSpannable.getSpans(i, i, URLSpanMono.class);
                j = 1;
              }
              k = 0;
              if (localObject.length != 0)
              {
                i = k;
                if (localObject.length != 0)
                {
                  i = k;
                  if ((localObject[0] instanceof URLSpanBotCommand))
                  {
                    i = k;
                    if (URLSpanBotCommand.enabled) {}
                  }
                }
              }
              else
              {
                i = 1;
              }
              if (i == 0)
              {
                if (paramMotionEvent.getAction() != 0) {
                  break label929;
                }
                this.pressedLink = localObject[0];
                this.linkBlockNum = m;
                this.pressedLinkType = 1;
                resetUrlPaths(false);
              }
            }
          }
          else
          {
            for (;;)
            {
              try
              {
                paramMotionEvent = obtainNewUrlPath(false);
                n = localSpannable.getSpanStart(this.pressedLink);
                k = localSpannable.getSpanEnd(this.pressedLink);
                paramMotionEvent.setCurrentLayout(localTextLayoutBlock.textLayout, n, 0.0F);
                localTextLayoutBlock.textLayout.getSelectionPath(n, k, paramMotionEvent);
                if (k >= localTextLayoutBlock.charactersEnd)
                {
                  i = m + 1;
                  if (i < this.currentMessageObject.textLayoutBlocks.size())
                  {
                    localObject = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(i);
                    int i1 = ((MessageObject.TextLayoutBlock)localObject).charactersOffset;
                    int i2 = ((MessageObject.TextLayoutBlock)localObject).charactersOffset;
                    if (j == 0) {
                      continue;
                    }
                    paramMotionEvent = URLSpanMono.class;
                    paramMotionEvent = (CharacterStyle[])localSpannable.getSpans(i1, i2, paramMotionEvent);
                    if ((paramMotionEvent != null) && (paramMotionEvent.length != 0) && (paramMotionEvent[0] == this.pressedLink)) {
                      continue;
                    }
                  }
                }
                if (n <= localTextLayoutBlock.charactersOffset)
                {
                  i = 0;
                  m--;
                  if (m >= 0)
                  {
                    localObject = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(m);
                    k = ((MessageObject.TextLayoutBlock)localObject).charactersEnd;
                    n = ((MessageObject.TextLayoutBlock)localObject).charactersEnd;
                    if (j == 0) {
                      continue;
                    }
                    paramMotionEvent = URLSpanMono.class;
                    paramMotionEvent = (CharacterStyle[])localSpannable.getSpans(k - 1, n - 1, paramMotionEvent);
                    if ((paramMotionEvent != null) && (paramMotionEvent.length != 0))
                    {
                      arrayOfCharacterStyle = paramMotionEvent[0];
                      paramMotionEvent = this.pressedLink;
                      if (arrayOfCharacterStyle == paramMotionEvent) {
                        continue;
                      }
                    }
                  }
                }
              }
              catch (Exception paramMotionEvent)
              {
                int n;
                FileLog.e(paramMotionEvent);
                continue;
              }
              invalidate();
              bool = true;
              break;
              m = j;
              j++;
              break label151;
              f3 = 0.0F;
              break label236;
              paramMotionEvent = ClickableSpan.class;
              continue;
              paramMotionEvent = obtainNewUrlPath(false);
              paramMotionEvent.setCurrentLayout(((MessageObject.TextLayoutBlock)localObject).textLayout, 0, ((MessageObject.TextLayoutBlock)localObject).textYOffset - localTextLayoutBlock.textYOffset);
              ((MessageObject.TextLayoutBlock)localObject).textLayout.getSelectionPath(0, k, paramMotionEvent);
              if (k >= ((MessageObject.TextLayoutBlock)localObject).charactersEnd - 1)
              {
                i++;
                continue;
                paramMotionEvent = ClickableSpan.class;
                continue;
                paramMotionEvent = obtainNewUrlPath(false);
                k = localSpannable.getSpanStart(this.pressedLink);
                i -= ((MessageObject.TextLayoutBlock)localObject).height;
                paramMotionEvent.setCurrentLayout(((MessageObject.TextLayoutBlock)localObject).textLayout, k, i);
                ((MessageObject.TextLayoutBlock)localObject).textLayout.getSelectionPath(k, localSpannable.getSpanEnd(this.pressedLink), paramMotionEvent);
                n = ((MessageObject.TextLayoutBlock)localObject).charactersOffset;
                if (k <= n) {
                  m--;
                }
              }
            }
          }
          bool = false;
        }
        catch (Exception paramMotionEvent)
        {
          FileLog.e(paramMotionEvent);
        }
      }
    }
    for (;;)
    {
      break;
      label929:
      if (localObject[0] == this.pressedLink)
      {
        this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, false);
        resetPressedLink(1);
        bool = true;
        break;
        label968:
        resetPressedLink(1);
      }
    }
  }
  
  private int createDocumentLayout(int paramInt, MessageObject paramMessageObject)
  {
    if (paramMessageObject.type == 0) {}
    for (this.documentAttach = paramMessageObject.messageOwner.media.webpage.document; this.documentAttach == null; this.documentAttach = paramMessageObject.messageOwner.media.document)
    {
      paramInt = 0;
      return paramInt;
    }
    int j;
    int k;
    Object localObject1;
    if (MessageObject.isVoiceDocument(this.documentAttach))
    {
      this.documentAttachType = 3;
      int i = 0;
      for (j = 0;; j++)
      {
        k = i;
        if (j < this.documentAttach.attributes.size())
        {
          localObject1 = (TLRPC.DocumentAttribute)this.documentAttach.attributes.get(j);
          if ((localObject1 instanceof TLRPC.TL_documentAttributeAudio)) {
            k = ((TLRPC.DocumentAttribute)localObject1).duration;
          }
        }
        else
        {
          this.widthBeforeNewTimeLine = (paramInt - AndroidUtilities.dp(94.0F) - (int)Math.ceil(Theme.chat_audioTimePaint.measureText("00:00")));
          this.availableTimeWidth = (paramInt - AndroidUtilities.dp(18.0F));
          measureTime(paramMessageObject);
          j = AndroidUtilities.dp(174.0F);
          i = this.timeWidth;
          if (!this.hasLinkPreview) {
            this.backgroundWidth = Math.min(paramInt, AndroidUtilities.dp(10.0F) * k + (j + i));
          }
          this.seekBarWaveform.setMessageObject(paramMessageObject);
          paramInt = 0;
          break;
        }
      }
    }
    if (MessageObject.isMusicDocument(this.documentAttach))
    {
      this.documentAttachType = 5;
      paramInt -= AndroidUtilities.dp(86.0F);
      this.songLayout = new StaticLayout(TextUtils.ellipsize(paramMessageObject.getMusicTitle().replace('\n', ' '), Theme.chat_audioTitlePaint, paramInt - AndroidUtilities.dp(12.0F), TextUtils.TruncateAt.END), Theme.chat_audioTitlePaint, paramInt, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      if (this.songLayout.getLineCount() > 0) {
        this.songX = (-(int)Math.ceil(this.songLayout.getLineLeft(0)));
      }
      this.performerLayout = new StaticLayout(TextUtils.ellipsize(paramMessageObject.getMusicAuthor().replace('\n', ' '), Theme.chat_audioPerformerPaint, paramInt, TextUtils.TruncateAt.END), Theme.chat_audioPerformerPaint, paramInt, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      if (this.performerLayout.getLineCount() > 0) {
        this.performerX = (-(int)Math.ceil(this.performerLayout.getLineLeft(0)));
      }
      j = 0;
      for (paramInt = 0;; paramInt++)
      {
        k = j;
        if (paramInt < this.documentAttach.attributes.size())
        {
          paramMessageObject = (TLRPC.DocumentAttribute)this.documentAttach.attributes.get(paramInt);
          if ((paramMessageObject instanceof TLRPC.TL_documentAttributeAudio)) {
            k = paramMessageObject.duration;
          }
        }
        else
        {
          paramInt = (int)Math.ceil(Theme.chat_audioTimePaint.measureText(String.format("%d:%02d / %d:%02d", new Object[] { Integer.valueOf(k / 60), Integer.valueOf(k % 60), Integer.valueOf(k / 60), Integer.valueOf(k % 60) })));
          this.widthBeforeNewTimeLine = (this.backgroundWidth - AndroidUtilities.dp(86.0F) - paramInt);
          this.availableTimeWidth = (this.backgroundWidth - AndroidUtilities.dp(28.0F));
          break;
        }
      }
    }
    if (MessageObject.isVideoDocument(this.documentAttach))
    {
      this.documentAttachType = 4;
      if (!paramMessageObject.needDrawBluredPreview()) {
        j = 0;
      }
      for (k = 0;; k++)
      {
        paramInt = j;
        if (k < this.documentAttach.attributes.size())
        {
          paramMessageObject = (TLRPC.DocumentAttribute)this.documentAttach.attributes.get(k);
          if ((paramMessageObject instanceof TLRPC.TL_documentAttributeVideo)) {
            paramInt = paramMessageObject.duration;
          }
        }
        else
        {
          k = paramInt / 60;
          paramMessageObject = String.format("%d:%02d, %s", new Object[] { Integer.valueOf(k), Integer.valueOf(paramInt - k * 60), AndroidUtilities.formatFileSize(this.documentAttach.size) });
          this.infoWidth = ((int)Math.ceil(Theme.chat_infoPaint.measureText(paramMessageObject)));
          this.infoLayout = new StaticLayout(paramMessageObject, Theme.chat_infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
          paramInt = 0;
          break;
        }
      }
    }
    boolean bool;
    Object localObject2;
    TextPaint localTextPaint;
    TextUtils.TruncateAt localTruncateAt;
    if (((this.documentAttach.mime_type != null) && (this.documentAttach.mime_type.toLowerCase().startsWith("image/"))) || ((this.documentAttach.thumb != null) && (!(this.documentAttach.thumb instanceof TLRPC.TL_photoSizeEmpty)) && (!(this.documentAttach.thumb.location instanceof TLRPC.TL_fileLocationUnavailable))))
    {
      bool = true;
      this.drawPhotoImage = bool;
      k = paramInt;
      if (!this.drawPhotoImage) {
        k = paramInt + AndroidUtilities.dp(30.0F);
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
        localObject1 = LocaleController.getString("AttachDocument", NUM);
      }
      localTextPaint = Theme.chat_docNamePaint;
      localObject2 = Layout.Alignment.ALIGN_NORMAL;
      localTruncateAt = TextUtils.TruncateAt.MIDDLE;
      if (!this.drawPhotoImage) {
        break label1020;
      }
    }
    label1020:
    for (paramInt = 2;; paramInt = 1)
    {
      this.docTitleLayout = StaticLayoutEx.createStaticLayout((CharSequence)localObject1, localTextPaint, k, (Layout.Alignment)localObject2, 1.0F, 0.0F, false, localTruncateAt, k, paramInt);
      this.docTitleOffsetX = Integer.MIN_VALUE;
      if ((this.docTitleLayout == null) || (this.docTitleLayout.getLineCount() <= 0)) {
        break label1256;
      }
      paramInt = 0;
      for (j = 0; j < this.docTitleLayout.getLineCount(); j++)
      {
        paramInt = Math.max(paramInt, (int)Math.ceil(this.docTitleLayout.getLineWidth(j)));
        this.docTitleOffsetX = Math.max(this.docTitleOffsetX, (int)Math.ceil(-this.docTitleLayout.getLineLeft(j)));
      }
      bool = false;
      break;
    }
    paramInt = Math.min(k, paramInt);
    for (;;)
    {
      localObject1 = AndroidUtilities.formatFileSize(this.documentAttach.size) + " " + FileLoader.getDocumentExtension(this.documentAttach);
      this.infoWidth = Math.min(k - AndroidUtilities.dp(30.0F), (int)Math.ceil(Theme.chat_infoPaint.measureText((String)localObject1)));
      localObject1 = TextUtils.ellipsize((CharSequence)localObject1, Theme.chat_infoPaint, this.infoWidth, TextUtils.TruncateAt.END);
      try
      {
        if (this.infoWidth < 0) {
          this.infoWidth = AndroidUtilities.dp(10.0F);
        }
        localObject2 = new android/text/StaticLayout;
        ((StaticLayout)localObject2).<init>((CharSequence)localObject1, Theme.chat_infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        this.infoLayout = ((StaticLayout)localObject2);
      }
      catch (Exception localException)
      {
        for (;;)
        {
          label1256:
          FileLog.e(localException);
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
          break label1277;
        }
        this.currentPhotoFilter = "86_86_b";
        this.photoImage.setImage(null, null, null, null, this.currentPhotoObject.location, this.currentPhotoFilter, 0, null, 1);
      }
      break;
      paramInt = k;
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
    for (;;)
    {
      return;
      if (this.buttonState == 0)
      {
        didPressedButton(false);
        continue;
        Object localObject;
        if (this.currentMessageObject.type == 12)
        {
          localObject = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.currentMessageObject.messageOwner.media.user_id));
          this.delegate.didPressedUserAvatar(this, (TLRPC.User)localObject);
        }
        else if (this.currentMessageObject.type == 5)
        {
          if ((!MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) || (MediaController.getInstance().isMessagePaused())) {
            this.delegate.needPlayMessage(this.currentMessageObject);
          } else {
            MediaController.getInstance().pauseMessage(this.currentMessageObject);
          }
        }
        else if (this.currentMessageObject.type == 8)
        {
          if (this.buttonState == -1)
          {
            if (SharedConfig.autoplayGifs)
            {
              this.delegate.didPressedImage(this);
            }
            else
            {
              this.buttonState = 2;
              this.currentMessageObject.gifState = 1.0F;
              this.photoImage.setAllowStartAnimation(false);
              this.photoImage.stopAnimation();
              this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
              invalidate();
            }
          }
          else if ((this.buttonState == 2) || (this.buttonState == 0)) {
            didPressedButton(false);
          }
        }
        else if (this.documentAttachType == 4)
        {
          if (this.buttonState == -1) {
            this.delegate.didPressedImage(this);
          } else if ((this.buttonState == 0) || (this.buttonState == 3)) {
            didPressedButton(false);
          }
        }
        else if (this.currentMessageObject.type == 4)
        {
          this.delegate.didPressedImage(this);
        }
        else if (this.documentAttachType == 1)
        {
          if (this.buttonState == -1) {
            this.delegate.didPressedImage(this);
          }
        }
        else if (this.documentAttachType == 2)
        {
          if (this.buttonState == -1)
          {
            localObject = this.currentMessageObject.messageOwner.media.webpage;
            if (localObject != null) {
              if ((((TLRPC.WebPage)localObject).embed_url != null) && (((TLRPC.WebPage)localObject).embed_url.length() != 0)) {
                this.delegate.needOpenWebView(((TLRPC.WebPage)localObject).embed_url, ((TLRPC.WebPage)localObject).site_name, ((TLRPC.WebPage)localObject).description, ((TLRPC.WebPage)localObject).url, ((TLRPC.WebPage)localObject).embed_width, ((TLRPC.WebPage)localObject).embed_height);
              } else {
                Browser.openUrl(getContext(), ((TLRPC.WebPage)localObject).url);
              }
            }
          }
        }
        else if ((this.hasInvoicePreview) && (this.buttonState == -1))
        {
          this.delegate.didPressedImage(this);
        }
      }
    }
  }
  
  private void didPressedButton(boolean paramBoolean)
  {
    if (this.buttonState == 0) {
      if ((this.documentAttachType == 3) || (this.documentAttachType == 5))
      {
        if (this.miniButtonState == 0) {
          FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, true, 0);
        }
        if (this.delegate.needPlayMessage(this.currentMessageObject))
        {
          if ((this.hasMiniProgress == 2) && (this.miniButtonState != 1))
          {
            this.miniButtonState = 1;
            this.radialProgress.setProgress(0.0F, false);
            this.radialProgress.setMiniBackground(getMiniDrawableForCurrentState(), true, false);
          }
          updatePlayingMessageProgress();
          this.buttonState = 1;
          this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
          invalidate();
        }
      }
    }
    for (;;)
    {
      return;
      this.cancelLoading = false;
      this.radialProgress.setProgress(0.0F, false);
      label200:
      int j;
      if (this.currentMessageObject.type == 1)
      {
        this.photoImage.setForceLoading(true);
        localObject1 = this.photoImage;
        localObject2 = this.currentPhotoObject.location;
        localObject3 = this.currentPhotoFilter;
        if (this.currentPhotoObjectThumb != null)
        {
          localObject4 = this.currentPhotoObjectThumb.location;
          String str = this.currentPhotoFilterThumb;
          int i = this.currentPhotoObject.size;
          if (!this.currentMessageObject.shouldEncryptPhotoOrVideo()) {
            break label275;
          }
          j = 2;
          label228:
          ((ImageReceiver)localObject1).setImage((TLObject)localObject2, (String)localObject3, (TLRPC.FileLocation)localObject4, str, i, null, j);
        }
      }
      for (;;)
      {
        this.buttonState = 1;
        this.radialProgress.setBackground(getDrawableForCurrentState(), true, paramBoolean);
        invalidate();
        break;
        localObject4 = null;
        break label200;
        label275:
        j = 0;
        break label228;
        if (this.currentMessageObject.type == 8)
        {
          this.currentMessageObject.gifState = 2.0F;
          this.photoImage.setForceLoading(true);
          localObject1 = this.photoImage;
          localObject2 = this.currentMessageObject.messageOwner.media.document;
          if (this.currentPhotoObject != null) {}
          for (localObject4 = this.currentPhotoObject.location;; localObject4 = null)
          {
            ((ImageReceiver)localObject1).setImage((TLObject)localObject2, null, (TLRPC.FileLocation)localObject4, this.currentPhotoFilterThumb, this.currentMessageObject.messageOwner.media.document.size, null, 0);
            break;
          }
        }
        if (this.currentMessageObject.isRoundVideo())
        {
          if (this.currentMessageObject.isSecretMedia())
          {
            FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), true, 1);
          }
          else
          {
            this.currentMessageObject.gifState = 2.0F;
            localObject1 = this.currentMessageObject.getDocument();
            this.photoImage.setForceLoading(true);
            localObject2 = this.photoImage;
            if (this.currentPhotoObject != null) {}
            for (localObject4 = this.currentPhotoObject.location;; localObject4 = null)
            {
              ((ImageReceiver)localObject2).setImage((TLObject)localObject1, null, (TLRPC.FileLocation)localObject4, this.currentPhotoFilterThumb, ((TLRPC.Document)localObject1).size, null, 0);
              break;
            }
          }
        }
        else if (this.currentMessageObject.type == 9)
        {
          FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.messageOwner.media.document, false, 0);
        }
        else
        {
          if (this.documentAttachType == 4)
          {
            localObject4 = FileLoader.getInstance(this.currentAccount);
            localObject2 = this.documentAttach;
            if (this.currentMessageObject.shouldEncryptPhotoOrVideo()) {}
            for (j = 2;; j = 0)
            {
              ((FileLoader)localObject4).loadFile((TLRPC.Document)localObject2, true, j);
              break;
            }
          }
          if ((this.currentMessageObject.type != 0) || (this.documentAttachType == 0)) {
            break label729;
          }
          if (this.documentAttachType == 2)
          {
            this.photoImage.setForceLoading(true);
            this.photoImage.setImage(this.currentMessageObject.messageOwner.media.webpage.document, null, this.currentPhotoObject.location, this.currentPhotoFilterThumb, this.currentMessageObject.messageOwner.media.webpage.document.size, null, 0);
            this.currentMessageObject.gifState = 2.0F;
          }
          else if (this.documentAttachType == 1)
          {
            FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.messageOwner.media.webpage.document, false, 0);
          }
        }
      }
      label729:
      this.photoImage.setForceLoading(true);
      Object localObject2 = this.photoImage;
      Object localObject3 = this.currentPhotoObject.location;
      Object localObject1 = this.currentPhotoFilter;
      if (this.currentPhotoObjectThumb != null) {}
      for (Object localObject4 = this.currentPhotoObjectThumb.location;; localObject4 = null)
      {
        ((ImageReceiver)localObject2).setImage((TLObject)localObject3, (String)localObject1, (TLRPC.FileLocation)localObject4, this.currentPhotoFilterThumb, 0, null, 0);
        break;
      }
      if (this.buttonState == 1)
      {
        if ((this.documentAttachType == 3) || (this.documentAttachType == 5))
        {
          if (MediaController.getInstance().pauseMessage(this.currentMessageObject))
          {
            this.buttonState = 0;
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
            invalidate();
          }
        }
        else if ((this.currentMessageObject.isOut()) && (this.currentMessageObject.isSending()))
        {
          if (!this.radialProgress.isDrawCheckDrawable()) {
            this.delegate.didPressedCancelSendButton(this);
          }
        }
        else
        {
          this.cancelLoading = true;
          if ((this.documentAttachType == 4) || (this.documentAttachType == 1)) {
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
          }
          for (;;)
          {
            this.buttonState = 0;
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
            invalidate();
            break;
            if ((this.currentMessageObject.type == 0) || (this.currentMessageObject.type == 1) || (this.currentMessageObject.type == 8) || (this.currentMessageObject.type == 5))
            {
              ImageLoader.getInstance().cancelForceLoadingForImageReceiver(this.photoImage);
              this.photoImage.cancelLoadImage();
            }
            else if (this.currentMessageObject.type == 9)
            {
              FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.messageOwner.media.document);
            }
          }
        }
      }
      else if (this.buttonState == 2)
      {
        if ((this.documentAttachType == 3) || (this.documentAttachType == 5))
        {
          this.radialProgress.setProgress(0.0F, false);
          FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, true, 0);
          this.buttonState = 4;
          this.radialProgress.setBackground(getDrawableForCurrentState(), true, false);
          invalidate();
        }
        else
        {
          this.photoImage.setAllowStartAnimation(true);
          this.photoImage.startAnimation();
          this.currentMessageObject.gifState = 0.0F;
          this.buttonState = -1;
          this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
        }
      }
      else if (this.buttonState == 3)
      {
        if ((this.hasMiniProgress == 2) && (this.miniButtonState != 1))
        {
          this.miniButtonState = 1;
          this.radialProgress.setProgress(0.0F, false);
          this.radialProgress.setMiniBackground(getMiniDrawableForCurrentState(), true, false);
        }
        this.delegate.didPressedImage(this);
      }
      else if ((this.buttonState == 4) && ((this.documentAttachType == 3) || (this.documentAttachType == 5)))
      {
        if (((this.currentMessageObject.isOut()) && (this.currentMessageObject.isSending())) || (this.currentMessageObject.isSendError()))
        {
          if (this.delegate != null) {
            this.delegate.didPressedCancelSendButton(this);
          }
        }
        else
        {
          FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
          this.buttonState = 2;
          this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
          invalidate();
        }
      }
    }
  }
  
  private void didPressedMiniButton(boolean paramBoolean)
  {
    if (this.miniButtonState == 0)
    {
      this.miniButtonState = 1;
      this.radialProgress.setProgress(0.0F, false);
      if ((this.documentAttachType == 3) || (this.documentAttachType == 5)) {
        FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, true, 0);
      }
    }
    label204:
    for (;;)
    {
      this.radialProgress.setMiniBackground(getMiniDrawableForCurrentState(), true, false);
      invalidate();
      for (;;)
      {
        return;
        if (this.documentAttachType != 4) {
          break label204;
        }
        FileLoader localFileLoader = FileLoader.getInstance(this.currentAccount);
        TLRPC.Document localDocument = this.documentAttach;
        if (this.currentMessageObject.shouldEncryptPhotoOrVideo()) {}
        for (int i = 2;; i = 0)
        {
          localFileLoader.loadFile(localDocument, true, i);
          break;
        }
        if (this.miniButtonState == 1)
        {
          if (((this.documentAttachType == 3) || (this.documentAttachType == 5)) && (MediaController.getInstance().isPlayingMessage(this.currentMessageObject))) {
            MediaController.getInstance().cleanupPlayer(true, true);
          }
          this.miniButtonState = 0;
          FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
          this.radialProgress.setMiniBackground(getMiniDrawableForCurrentState(), true, false);
          invalidate();
        }
      }
    }
  }
  
  private void drawContent(Canvas paramCanvas)
  {
    if ((this.needNewVisiblePart) && (this.currentMessageObject.type == 0))
    {
      getLocalVisibleRect(this.scrollRect);
      setVisiblePart(this.scrollRect.top, this.scrollRect.bottom - this.scrollRect.top);
      this.needNewVisiblePart = false;
    }
    Object localObject1;
    label93:
    label129:
    label155:
    boolean bool2;
    boolean bool3;
    label294:
    int j;
    label373:
    label545:
    int m;
    int n;
    Object localObject3;
    label578:
    label660:
    int i2;
    if (this.currentMessagesGroup != null)
    {
      bool1 = true;
      this.forceNotDrawTime = bool1;
      localObject1 = this.photoImage;
      if (!isDrawSelectedBackground()) {
        break label1474;
      }
      if (this.currentPosition == null) {
        break label1468;
      }
      i = 2;
      ((ImageReceiver)localObject1).setPressed(i);
      localObject1 = this.photoImage;
      if ((PhotoViewer.isShowingImage(this.currentMessageObject)) || (SecretMediaViewer.getInstance().isShowingImage(this.currentMessageObject))) {
        break label1480;
      }
      bool1 = true;
      ((ImageReceiver)localObject1).setVisible(bool1, false);
      if (this.photoImage.getVisible()) {
        break label1485;
      }
      this.mediaWasInvisible = true;
      this.timeWasInvisible = true;
      this.radialProgress.setHideCurrentDrawable(false);
      this.radialProgress.setProgressColor(Theme.getColor("chat_mediaProgress"));
      bool2 = false;
      bool3 = false;
      bool1 = false;
      if (this.currentMessageObject.type != 0) {
        break label5615;
      }
      if (!this.currentMessageObject.isOutOwner()) {
        break label1563;
      }
      this.textX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0F));
      if (!this.hasGamePreview) {
        break label1617;
      }
      this.textX += AndroidUtilities.dp(11.0F);
      this.textY = (AndroidUtilities.dp(14.0F) + this.namesOffset);
      if (this.siteNameLayout != null) {
        this.textY += this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
      }
      this.unmovedTextX = this.textX;
      if ((this.currentMessageObject.textXOffset != 0.0F) && (this.replyNameLayout != null))
      {
        j = this.backgroundWidth - AndroidUtilities.dp(31.0F) - this.currentMessageObject.textWidth;
        i = j;
        if (!this.hasNewLineForTime)
        {
          k = this.timeWidth;
          if (!this.currentMessageObject.isOutOwner()) {
            break label1692;
          }
          i = 20;
          i = j - (AndroidUtilities.dp(i + 4) + k);
        }
        if (i > 0) {
          this.textX += i;
        }
      }
      if ((this.currentMessageObject.textLayoutBlocks != null) && (!this.currentMessageObject.textLayoutBlocks.isEmpty()))
      {
        if (this.fullyDraw)
        {
          this.firstVisibleBlockNum = 0;
          this.lastVisibleBlockNum = this.currentMessageObject.textLayoutBlocks.size();
        }
        if (this.firstVisibleBlockNum >= 0)
        {
          i = this.firstVisibleBlockNum;
          if ((i <= this.lastVisibleBlockNum) && (i < this.currentMessageObject.textLayoutBlocks.size())) {
            break label1698;
          }
        }
      }
      if ((!this.hasLinkPreview) && (!this.hasGamePreview) && (!this.hasInvoicePreview)) {
        break label2940;
      }
      if (!this.hasGamePreview) {
        break label1913;
      }
      i = AndroidUtilities.dp(14.0F) + this.namesOffset;
      j = this.unmovedTextX - AndroidUtilities.dp(10.0F);
      m = i;
      n = 0;
      if (!this.hasInvoicePreview)
      {
        localObject3 = Theme.chat_replyLinePaint;
        if (!this.currentMessageObject.isOutOwner()) {
          break label1982;
        }
        localObject1 = "chat_outPreviewLine";
        ((Paint)localObject3).setColor(Theme.getColor((String)localObject1));
        paramCanvas.drawRect(j, m - AndroidUtilities.dp(3.0F), AndroidUtilities.dp(2.0F) + j, this.linkPreviewHeight + m + AndroidUtilities.dp(3.0F), Theme.chat_replyLinePaint);
      }
      i1 = m;
      if (this.siteNameLayout != null)
      {
        localObject3 = Theme.chat_replyNamePaint;
        if (!this.currentMessageObject.isOutOwner()) {
          break label1989;
        }
        localObject1 = "chat_outSiteNameText";
        ((TextPaint)localObject3).setColor(Theme.getColor((String)localObject1));
        paramCanvas.save();
        if (!this.siteNameRtl) {
          break label1996;
        }
        k = this.backgroundWidth - this.siteNameWidth - AndroidUtilities.dp(32.0F);
        paramCanvas.translate(j + k, m - AndroidUtilities.dp(3.0F));
        this.siteNameLayout.draw(paramCanvas);
        paramCanvas.restore();
        i1 = m + this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
      }
      if (!this.hasGamePreview)
      {
        k = i1;
        i2 = i;
        if (!this.hasInvoicePreview) {}
      }
      else
      {
        k = i1;
        i2 = i;
        if (this.currentMessageObject.textHeight != 0)
        {
          i2 = i + (this.currentMessageObject.textHeight + AndroidUtilities.dp(4.0F));
          k = i1 + (this.currentMessageObject.textHeight + AndroidUtilities.dp(4.0F));
        }
      }
      bool2 = bool1;
      i = k;
      if (this.drawPhotoImage)
      {
        bool2 = bool1;
        i = k;
        if (this.drawInstantView)
        {
          i = k;
          if (k != i2) {
            i = k + AndroidUtilities.dp(2.0F);
          }
          this.photoImage.setImageCoords(AndroidUtilities.dp(10.0F) + j, i, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
          if (this.drawImageButton)
          {
            k = AndroidUtilities.dp(48.0F);
            this.buttonX = ((int)(this.photoImage.getImageX() + (this.photoImage.getImageWidth() - k) / 2.0F));
            this.buttonY = ((int)(this.photoImage.getImageY() + (this.photoImage.getImageHeight() - k) / 2.0F));
            this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + k, this.buttonY + k);
          }
          bool2 = this.photoImage.draw(paramCanvas);
          i += this.photoImage.getImageHeight() + AndroidUtilities.dp(6.0F);
        }
      }
      if (!this.currentMessageObject.isOutOwner()) {
        break label2020;
      }
      Theme.chat_replyNamePaint.setColor(Theme.getColor("chat_messageTextOut"));
      Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_messageTextOut"));
      label1071:
      i1 = i;
      k = n;
      if (this.titleLayout != null)
      {
        k = i;
        if (i != i2) {
          k = i + AndroidUtilities.dp(2.0F);
        }
        i = k - AndroidUtilities.dp(1.0F);
        paramCanvas.save();
        paramCanvas.translate(AndroidUtilities.dp(10.0F) + j + this.titleX, k - AndroidUtilities.dp(3.0F));
        this.titleLayout.draw(paramCanvas);
        paramCanvas.restore();
        i1 = k + this.titleLayout.getLineBottom(this.titleLayout.getLineCount() - 1);
        k = i;
      }
      m = i1;
      i = k;
      if (this.authorLayout != null)
      {
        m = i1;
        if (i1 != i2) {
          m = i1 + AndroidUtilities.dp(2.0F);
        }
        i = k;
        if (k == 0) {
          i = m - AndroidUtilities.dp(1.0F);
        }
        paramCanvas.save();
        paramCanvas.translate(AndroidUtilities.dp(10.0F) + j + this.authorX, m - AndroidUtilities.dp(3.0F));
        this.authorLayout.draw(paramCanvas);
        paramCanvas.restore();
        m += this.authorLayout.getLineBottom(this.authorLayout.getLineCount() - 1);
      }
      k = m;
      i1 = i;
      if (this.descriptionLayout == null) {
        break label2099;
      }
      i1 = m;
      if (m != i2) {
        i1 = m + AndroidUtilities.dp(2.0F);
      }
      k = i;
      if (i == 0) {
        k = i1 - AndroidUtilities.dp(1.0F);
      }
      this.descriptionY = (i1 - AndroidUtilities.dp(3.0F));
      paramCanvas.save();
      if (!this.hasInvoicePreview) {
        break label2047;
      }
    }
    label1468:
    label1474:
    label1480:
    label1485:
    label1563:
    float f;
    label1617:
    label1692:
    label1698:
    label1913:
    label1982:
    Object localObject2;
    label1989:
    label1996:
    label2020:
    label2047:
    for (int i = 0;; i = AndroidUtilities.dp(10.0F))
    {
      paramCanvas.translate(i + j + this.descriptionX, this.descriptionY);
      if ((this.pressedLink == null) || (this.linkBlockNum != -10)) {
        break label2058;
      }
      for (i = 0; i < this.urlPath.size(); i++) {
        paramCanvas.drawPath((Path)this.urlPath.get(i), Theme.chat_urlPaint);
      }
      bool1 = false;
      break;
      i = 1;
      break label93;
      i = 0;
      break label93;
      bool1 = false;
      break label129;
      if (this.groupPhotoInvisible)
      {
        this.timeWasInvisible = true;
        break label155;
      }
      if ((!this.mediaWasInvisible) && (!this.timeWasInvisible)) {
        break label155;
      }
      if (this.mediaWasInvisible)
      {
        this.controlsAlpha = 0.0F;
        this.mediaWasInvisible = false;
      }
      if (this.timeWasInvisible)
      {
        this.timeAlpha = 0.0F;
        this.timeWasInvisible = false;
      }
      this.lastControlsAlphaChangeTime = System.currentTimeMillis();
      this.totalChangeTime = 0L;
      break label155;
      i = this.currentBackgroundDrawable.getBounds().left;
      if ((!this.mediaBackground) && (this.drawPinnedBottom)) {}
      for (f = 11.0F;; f = 17.0F)
      {
        this.textX = (AndroidUtilities.dp(f) + i);
        break;
      }
      if (this.hasInvoicePreview)
      {
        this.textY = (AndroidUtilities.dp(14.0F) + this.namesOffset);
        if (this.siteNameLayout == null) {
          break label294;
        }
        this.textY += this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
        break label294;
      }
      this.textY = (AndroidUtilities.dp(10.0F) + this.namesOffset);
      break label294;
      i = 0;
      break label373;
      localObject1 = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(i);
      paramCanvas.save();
      k = this.textX;
      if (((MessageObject.TextLayoutBlock)localObject1).isRtl()) {}
      for (j = (int)Math.ceil(this.currentMessageObject.textXOffset);; j = 0)
      {
        paramCanvas.translate(k - j, this.textY + ((MessageObject.TextLayoutBlock)localObject1).textYOffset);
        if ((this.pressedLink == null) || (i != this.linkBlockNum)) {
          break;
        }
        for (j = 0; j < this.urlPath.size(); j++) {
          paramCanvas.drawPath((Path)this.urlPath.get(j), Theme.chat_urlPaint);
        }
      }
      if ((i == this.linkSelectionBlockNum) && (!this.urlPathSelection.isEmpty())) {
        for (j = 0; j < this.urlPathSelection.size(); j++) {
          paramCanvas.drawPath((Path)this.urlPathSelection.get(j), Theme.chat_textSearchSelectionPaint);
        }
      }
      try
      {
        ((MessageObject.TextLayoutBlock)localObject1).textLayout.draw(paramCanvas);
        paramCanvas.restore();
        i++;
      }
      catch (Exception localException1)
      {
        for (;;)
        {
          FileLog.e(localException1);
        }
      }
      if (this.hasInvoicePreview)
      {
        i = AndroidUtilities.dp(14.0F) + this.namesOffset;
        j = this.unmovedTextX + AndroidUtilities.dp(1.0F);
        break label545;
      }
      i = this.textY + this.currentMessageObject.textHeight + AndroidUtilities.dp(8.0F);
      j = this.unmovedTextX + AndroidUtilities.dp(1.0F);
      break label545;
      localObject2 = "chat_inPreviewLine";
      break label578;
      localObject2 = "chat_inSiteNameText";
      break label660;
      if (this.hasInvoicePreview) {}
      for (k = 0;; k = AndroidUtilities.dp(10.0F)) {
        break;
      }
      Theme.chat_replyNamePaint.setColor(Theme.getColor("chat_messageTextIn"));
      Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_messageTextIn"));
      break label1071;
    }
    label2058:
    this.descriptionLayout.draw(paramCanvas);
    paramCanvas.restore();
    i = i1 + this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1);
    int i1 = k;
    int k = i;
    label2099:
    boolean bool1 = bool2;
    i = k;
    if (this.drawPhotoImage)
    {
      bool1 = bool2;
      i = k;
      if (!this.drawInstantView)
      {
        i = k;
        if (k != i2) {
          i = k + AndroidUtilities.dp(2.0F);
        }
        if (!this.isSmallImage) {
          break label5227;
        }
        this.photoImage.setImageCoords(this.backgroundWidth + j - AndroidUtilities.dp(81.0F), i1, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
      }
    }
    for (;;)
    {
      label2230:
      label2613:
      label2654:
      label2734:
      label2940:
      label2948:
      label3019:
      long l1;
      long l2;
      label3442:
      label3857:
      label4029:
      label4074:
      label4149:
      label4263:
      label4395:
      label4420:
      label4440:
      label4512:
      label4631:
      Object localObject4;
      if ((this.currentMessageObject.isRoundVideo()) && (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) && (MediaController.getInstance().isRoundVideoDrawingReady()))
      {
        bool1 = true;
        this.drawTime = true;
        if ((this.photosCountLayout != null) && (this.photoImage.getVisible()))
        {
          i1 = this.photoImage.getImageX() + this.photoImage.getImageWidth() - AndroidUtilities.dp(8.0F) - this.photosCountWidth;
          m = this.photoImage.getImageY() + this.photoImage.getImageHeight() - AndroidUtilities.dp(19.0F);
          this.rect.set(i1 - AndroidUtilities.dp(4.0F), m - AndroidUtilities.dp(1.5F), this.photosCountWidth + i1 + AndroidUtilities.dp(4.0F), AndroidUtilities.dp(14.5F) + m);
          k = Theme.chat_timeBackgroundPaint.getAlpha();
          Theme.chat_timeBackgroundPaint.setAlpha((int)(k * this.controlsAlpha));
          Theme.chat_durationPaint.setAlpha((int)(255.0F * this.controlsAlpha));
          paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0F), AndroidUtilities.dp(4.0F), Theme.chat_timeBackgroundPaint);
          Theme.chat_timeBackgroundPaint.setAlpha(k);
          paramCanvas.save();
          paramCanvas.translate(i1, m);
          this.photosCountLayout.draw(paramCanvas);
          paramCanvas.restore();
          Theme.chat_durationPaint.setAlpha(255);
        }
        if ((this.videoInfoLayout != null) && ((!this.drawPhotoImage) || (this.photoImage.getVisible())))
        {
          if ((!this.hasGamePreview) && (!this.hasInvoicePreview)) {
            break label5412;
          }
          if (!this.drawPhotoImage) {
            break label5397;
          }
          i = this.photoImage.getImageX() + AndroidUtilities.dp(8.5F);
          k = this.photoImage.getImageY() + AndroidUtilities.dp(6.0F);
          this.rect.set(i - AndroidUtilities.dp(4.0F), k - AndroidUtilities.dp(1.5F), this.durationWidth + i + AndroidUtilities.dp(4.0F), AndroidUtilities.dp(16.5F) + k);
          paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0F), AndroidUtilities.dp(4.0F), Theme.chat_timeBackgroundPaint);
          paramCanvas.save();
          paramCanvas.translate(i, k);
          if (this.hasInvoicePreview)
          {
            if (!this.drawPhotoImage) {
              break label5545;
            }
            Theme.chat_shipmentPaint.setColor(Theme.getColor("chat_previewGameText"));
          }
          this.videoInfoLayout.draw(paramCanvas);
          paramCanvas.restore();
        }
        bool2 = bool1;
        if (this.drawInstantView)
        {
          i = this.linkPreviewHeight + i2 + AndroidUtilities.dp(10.0F);
          localObject3 = Theme.chat_instantViewRectPaint;
          if (!this.currentMessageObject.isOutOwner()) {
            break label5585;
          }
          localObject2 = Theme.chat_msgOutInstantDrawable;
          Theme.chat_instantViewPaint.setColor(Theme.getColor("chat_outPreviewInstantText"));
          ((Paint)localObject3).setColor(Theme.getColor("chat_outPreviewInstantText"));
          if (Build.VERSION.SDK_INT >= 21)
          {
            this.instantViewSelectorDrawable.setBounds(j, i, this.instantWidth + j, AndroidUtilities.dp(36.0F) + i);
            this.instantViewSelectorDrawable.draw(paramCanvas);
          }
          this.rect.set(j, i, this.instantWidth + j, AndroidUtilities.dp(36.0F) + i);
          paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(6.0F), AndroidUtilities.dp(6.0F), (Paint)localObject3);
          if (this.drawInstantViewType == 0)
          {
            setDrawableBounds((Drawable)localObject2, this.instantTextLeftX + this.instantTextX + j - AndroidUtilities.dp(15.0F), AndroidUtilities.dp(11.5F) + i, AndroidUtilities.dp(9.0F), AndroidUtilities.dp(13.0F));
            ((Drawable)localObject2).draw(paramCanvas);
          }
          bool2 = bool1;
          if (this.instantViewLayout != null)
          {
            paramCanvas.save();
            paramCanvas.translate(this.instantTextX + j, AndroidUtilities.dp(10.5F) + i);
            this.instantViewLayout.draw(paramCanvas);
            paramCanvas.restore();
            bool2 = bool1;
          }
        }
        this.drawTime = true;
        bool1 = bool2;
        if ((this.buttonState == -1) && (this.currentMessageObject.needDrawBluredPreview()) && (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) && (this.photoImage.getVisible()))
        {
          i = 4;
          if (this.currentMessageObject.messageOwner.destroyTime != 0)
          {
            if (!this.currentMessageObject.isOutOwner()) {
              break label6012;
            }
            i = 6;
          }
          setDrawableBounds(Theme.chat_photoStatesDrawables[i][this.buttonPressed], this.buttonX, this.buttonY);
          Theme.chat_photoStatesDrawables[i][this.buttonPressed].setAlpha((int)(255.0F * (1.0F - this.radialProgress.getAlpha()) * this.controlsAlpha));
          Theme.chat_photoStatesDrawables[i][this.buttonPressed].draw(paramCanvas);
          if (this.currentMessageObject.messageOwner.destroyTime != 0)
          {
            if (!this.currentMessageObject.isOutOwner())
            {
              l1 = System.currentTimeMillis();
              l2 = ConnectionsManager.getInstance(this.currentAccount).getTimeDifference() * 1000;
              f = (float)Math.max(0L, this.currentMessageObject.messageOwner.destroyTime * 1000L - (l1 + l2)) / (this.currentMessageObject.messageOwner.ttl * 1000.0F);
              Theme.chat_deleteProgressPaint.setAlpha((int)(255.0F * this.controlsAlpha));
              paramCanvas.drawArc(this.deleteProgressRect, -90.0F, -360.0F * f, true, Theme.chat_deleteProgressPaint);
              if (f != 0.0F)
              {
                i = AndroidUtilities.dp(2.0F);
                invalidate((int)this.deleteProgressRect.left - i, (int)this.deleteProgressRect.top - i, (int)this.deleteProgressRect.right + i * 2, (int)this.deleteProgressRect.bottom + i * 2);
              }
            }
            updateSecretTimeText(this.currentMessageObject);
          }
        }
        if ((this.documentAttachType != 2) && (this.currentMessageObject.type != 8)) {
          break label6018;
        }
        if ((this.photoImage.getVisible()) && (!this.hasGamePreview) && (!this.currentMessageObject.needDrawBluredPreview()))
        {
          k = ((BitmapDrawable)Theme.chat_msgMediaMenuDrawable).getPaint().getAlpha();
          Theme.chat_msgMediaMenuDrawable.setAlpha((int)(k * this.controlsAlpha));
          localObject2 = Theme.chat_msgMediaMenuDrawable;
          j = this.photoImage.getImageX() + this.photoImage.getImageWidth() - AndroidUtilities.dp(14.0F);
          this.otherX = j;
          i = this.photoImage.getImageY() + AndroidUtilities.dp(8.1F);
          this.otherY = i;
          setDrawableBounds((Drawable)localObject2, j, i);
          Theme.chat_msgMediaMenuDrawable.draw(paramCanvas);
          Theme.chat_msgMediaMenuDrawable.setAlpha(k);
        }
        if ((this.currentMessageObject.type != 1) && (this.documentAttachType != 4)) {
          break label7371;
        }
        if (this.photoImage.getVisible())
        {
          if ((!this.currentMessageObject.needDrawBluredPreview()) && (this.documentAttachType == 4))
          {
            i = ((BitmapDrawable)Theme.chat_msgMediaMenuDrawable).getPaint().getAlpha();
            Theme.chat_msgMediaMenuDrawable.setAlpha((int)(i * this.controlsAlpha));
            localObject2 = Theme.chat_msgMediaMenuDrawable;
            k = this.photoImage.getImageX() + this.photoImage.getImageWidth() - AndroidUtilities.dp(14.0F);
            this.otherX = k;
            j = this.photoImage.getImageY() + AndroidUtilities.dp(8.1F);
            this.otherY = j;
            setDrawableBounds((Drawable)localObject2, k, j);
            Theme.chat_msgMediaMenuDrawable.draw(paramCanvas);
            Theme.chat_msgMediaMenuDrawable.setAlpha(i);
          }
          if ((!this.forceNotDrawTime) && (this.infoLayout != null) && ((this.buttonState == 1) || (this.buttonState == 0) || (this.buttonState == 3) || (this.currentMessageObject.needDrawBluredPreview())))
          {
            Theme.chat_infoPaint.setColor(Theme.getColor("chat_mediaInfoText"));
            j = this.photoImage.getImageX() + AndroidUtilities.dp(4.0F);
            i = this.photoImage.getImageY() + AndroidUtilities.dp(4.0F);
            this.rect.set(j, i, this.infoWidth + j + AndroidUtilities.dp(8.0F), AndroidUtilities.dp(16.5F) + i);
            i = Theme.chat_timeBackgroundPaint.getAlpha();
            Theme.chat_timeBackgroundPaint.setAlpha((int)(i * this.controlsAlpha));
            paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0F), AndroidUtilities.dp(4.0F), Theme.chat_timeBackgroundPaint);
            Theme.chat_timeBackgroundPaint.setAlpha(i);
            paramCanvas.save();
            paramCanvas.translate(this.photoImage.getImageX() + AndroidUtilities.dp(8.0F), this.photoImage.getImageY() + AndroidUtilities.dp(5.5F));
            Theme.chat_infoPaint.setAlpha((int)(255.0F * this.controlsAlpha));
            this.infoLayout.draw(paramCanvas);
            paramCanvas.restore();
            Theme.chat_infoPaint.setAlpha(255);
          }
        }
        if (this.captionLayout != null)
        {
          if ((this.currentMessageObject.type != 1) && (this.documentAttachType != 4) && (this.currentMessageObject.type != 8)) {
            break label9037;
          }
          this.captionX = (this.photoImage.getImageX() + AndroidUtilities.dp(5.0F) + this.captionOffsetX);
          this.captionY = (this.photoImage.getImageY() + this.photoImage.getImageHeight() + AndroidUtilities.dp(6.0F));
        }
        if (this.currentPosition == null) {
          drawCaptionLayout(paramCanvas, false);
        }
        if (this.hasOldCaptionPreview)
        {
          if ((this.currentMessageObject.type != 1) && (this.documentAttachType != 4) && (this.currentMessageObject.type != 8)) {
            break label9248;
          }
          j = this.photoImage.getImageX() + AndroidUtilities.dp(5.0F);
          i = this.totalHeight;
          if (!this.drawPinnedTop) {
            break label9290;
          }
          f = 9.0F;
          i1 = i - AndroidUtilities.dp(f) - this.linkPreviewHeight - AndroidUtilities.dp(8.0F);
          k = i1;
          localObject3 = Theme.chat_replyLinePaint;
          if (!this.currentMessageObject.isOutOwner()) {
            break label9298;
          }
          localObject2 = "chat_outPreviewLine";
          ((Paint)localObject3).setColor(Theme.getColor((String)localObject2));
          paramCanvas.drawRect(j, k - AndroidUtilities.dp(3.0F), AndroidUtilities.dp(2.0F) + j, this.linkPreviewHeight + k, Theme.chat_replyLinePaint);
          i = k;
          if (this.siteNameLayout != null)
          {
            localObject3 = Theme.chat_replyNamePaint;
            if (!this.currentMessageObject.isOutOwner()) {
              break label9305;
            }
            localObject2 = "chat_outSiteNameText";
            ((TextPaint)localObject3).setColor(Theme.getColor((String)localObject2));
            paramCanvas.save();
            if (!this.siteNameRtl) {
              break label9312;
            }
            i = this.backgroundWidth - this.siteNameWidth - AndroidUtilities.dp(32.0F);
            paramCanvas.translate(j + i, k - AndroidUtilities.dp(3.0F));
            this.siteNameLayout.draw(paramCanvas);
            paramCanvas.restore();
            i = k + this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
          }
          if (!this.currentMessageObject.isOutOwner()) {
            break label9336;
          }
          Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_messageTextOut"));
          if (this.descriptionLayout != null)
          {
            k = i;
            if (i != i1) {
              k = i + AndroidUtilities.dp(2.0F);
            }
            this.descriptionY = (k - AndroidUtilities.dp(3.0F));
            paramCanvas.save();
            paramCanvas.translate(AndroidUtilities.dp(10.0F) + j + this.descriptionX, this.descriptionY);
            this.descriptionLayout.draw(paramCanvas);
            paramCanvas.restore();
          }
          this.drawTime = true;
        }
        if (this.documentAttachType == 1)
        {
          if (!this.currentMessageObject.isOutOwner()) {
            break label9372;
          }
          Theme.chat_docNamePaint.setColor(Theme.getColor("chat_outFileNameText"));
          localObject3 = Theme.chat_infoPaint;
          if (!isDrawSelectedBackground()) {
            break label9351;
          }
          localObject2 = "chat_outFileInfoSelectedText";
          ((TextPaint)localObject3).setColor(Theme.getColor((String)localObject2));
          localObject3 = Theme.chat_docBackPaint;
          if (!isDrawSelectedBackground()) {
            break label9358;
          }
          localObject2 = "chat_outFileBackgroundSelected";
          ((Paint)localObject3).setColor(Theme.getColor((String)localObject2));
          if (!isDrawSelectedBackground()) {
            break label9365;
          }
          localObject2 = Theme.chat_msgOutMenuSelectedDrawable;
          if (!this.drawPhotoImage) {
            break label9683;
          }
          if (this.currentMessageObject.type != 0) {
            break label9469;
          }
          j = this.photoImage.getImageX() + this.backgroundWidth - AndroidUtilities.dp(56.0F);
          this.otherX = j;
          i = this.photoImage.getImageY() + AndroidUtilities.dp(1.0F);
          this.otherY = i;
          setDrawableBounds((Drawable)localObject2, j, i);
          k = this.photoImage.getImageX() + this.photoImage.getImageWidth() + AndroidUtilities.dp(10.0F);
          j = this.photoImage.getImageY() + AndroidUtilities.dp(8.0F);
          i1 = this.photoImage.getImageY() + this.docTitleLayout.getLineBottom(this.docTitleLayout.getLineCount() - 1) + AndroidUtilities.dp(13.0F);
          if ((this.buttonState >= 0) && (this.buttonState < 4))
          {
            if (bool1) {
              break label9572;
            }
            i = this.buttonState;
            if (this.buttonState != 0) {
              break label9534;
            }
            if (!this.currentMessageObject.isOutOwner()) {
              break label9527;
            }
            i = 7;
            localObject4 = this.radialProgress;
            localObject3 = Theme.chat_photoStatesDrawables[i];
            if ((!isDrawSelectedBackground()) && (this.buttonPressed == 0)) {
              break label9566;
            }
            i = 1;
            label4662:
            ((RadialProgress)localObject4).swapBackground(localObject3[i]);
          }
          label4673:
          if (bool1) {
            break label9647;
          }
          this.rect.set(this.photoImage.getImageX(), this.photoImage.getImageY(), this.photoImage.getImageX() + this.photoImage.getImageWidth(), this.photoImage.getImageY() + this.photoImage.getImageHeight());
          paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(3.0F), AndroidUtilities.dp(3.0F), Theme.chat_docBackPaint);
          if (!this.currentMessageObject.isOutOwner()) {
            break label9604;
          }
          localObject4 = this.radialProgress;
          if (!isDrawSelectedBackground()) {
            break label9596;
          }
          localObject3 = "chat_outFileProgressSelected";
          label4785:
          ((RadialProgress)localObject4).setProgressColor(Theme.getColor((String)localObject3));
          i = i1;
          label4799:
          ((Drawable)localObject2).draw(paramCanvas);
        }
      }
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
      catch (Exception localException2)
      {
        try
        {
          for (;;)
          {
            if (this.infoLayout != null)
            {
              paramCanvas.save();
              paramCanvas.translate(k, i);
              this.infoLayout.draw(paramCanvas);
              paramCanvas.restore();
            }
            if ((this.drawImageButton) && (this.photoImage.getVisible()))
            {
              if (this.controlsAlpha != 1.0F) {
                this.radialProgress.setOverrideAlpha(this.controlsAlpha);
              }
              this.radialProgress.draw(paramCanvas);
            }
            if (this.botButtons.isEmpty()) {
              return;
            }
            if (!this.currentMessageObject.isOutOwner()) {
              break label9921;
            }
            i = getMeasuredWidth() - this.widthForButtons - AndroidUtilities.dp(10.0F);
            for (j = 0;; j++)
            {
              if (j >= this.botButtons.size()) {
                return;
              }
              localObject4 = (BotButton)this.botButtons.get(j);
              i1 = ((BotButton)localObject4).y + this.layoutHeight - AndroidUtilities.dp(2.0F);
              localObject3 = Theme.chat_systemDrawable;
              if (j != this.pressedBotButton) {
                break;
              }
              localObject2 = Theme.colorPressedFilter;
              ((Drawable)localObject3).setColorFilter((ColorFilter)localObject2);
              Theme.chat_systemDrawable.setBounds(((BotButton)localObject4).x + i, i1, ((BotButton)localObject4).x + i + ((BotButton)localObject4).width, ((BotButton)localObject4).height + i1);
              Theme.chat_systemDrawable.draw(paramCanvas);
              paramCanvas.save();
              paramCanvas.translate(((BotButton)localObject4).x + i + AndroidUtilities.dp(5.0F), (AndroidUtilities.dp(44.0F) - ((BotButton)localObject4).title.getLineBottom(((BotButton)localObject4).title.getLineCount() - 1)) / 2 + i1);
              ((BotButton)localObject4).title.draw(paramCanvas);
              paramCanvas.restore();
              if (!(((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonUrl)) {
                break label9965;
              }
              k = ((BotButton)localObject4).x;
              n = ((BotButton)localObject4).width;
              i2 = AndroidUtilities.dp(3.0F);
              m = Theme.chat_botLinkDrawalbe.getIntrinsicWidth();
              setDrawableBounds(Theme.chat_botLinkDrawalbe, k + n - i2 - m + i, AndroidUtilities.dp(3.0F) + i1);
              Theme.chat_botLinkDrawalbe.draw(paramCanvas);
            }
            label5227:
            localObject2 = this.photoImage;
            if (this.hasInvoicePreview) {}
            for (k = -AndroidUtilities.dp(6.3F);; k = AndroidUtilities.dp(10.0F))
            {
              ((ImageReceiver)localObject2).setImageCoords(k + j, i, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
              if (!this.drawImageButton) {
                break;
              }
              k = AndroidUtilities.dp(48.0F);
              this.buttonX = ((int)(this.photoImage.getImageX() + (this.photoImage.getImageWidth() - k) / 2.0F));
              this.buttonY = ((int)(this.photoImage.getImageY() + (this.photoImage.getImageHeight() - k) / 2.0F));
              this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + k, this.buttonY + k);
              break;
            }
            bool1 = this.photoImage.draw(paramCanvas);
            break label2230;
            label5397:
            i1 = j;
            k = i;
            i = i1;
            break label2613;
            label5412:
            i = this.photoImage.getImageX() + this.photoImage.getImageWidth() - AndroidUtilities.dp(8.0F) - this.durationWidth;
            k = this.photoImage.getImageY() + this.photoImage.getImageHeight() - AndroidUtilities.dp(19.0F);
            this.rect.set(i - AndroidUtilities.dp(4.0F), k - AndroidUtilities.dp(1.5F), this.durationWidth + i + AndroidUtilities.dp(4.0F), AndroidUtilities.dp(14.5F) + k);
            paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0F), AndroidUtilities.dp(4.0F), Theme.chat_timeBackgroundPaint);
            break label2613;
            label5545:
            if (this.currentMessageObject.isOutOwner())
            {
              Theme.chat_shipmentPaint.setColor(Theme.getColor("chat_messageTextOut"));
              break label2654;
            }
            Theme.chat_shipmentPaint.setColor(Theme.getColor("chat_messageTextIn"));
            break label2654;
            label5585:
            localObject2 = Theme.chat_msgInInstantDrawable;
            Theme.chat_instantViewPaint.setColor(Theme.getColor("chat_inPreviewInstantText"));
            ((Paint)localObject3).setColor(Theme.getColor("chat_inPreviewInstantText"));
            break label2734;
            label5615:
            bool1 = bool3;
            if (!this.drawPhotoImage) {
              break label2948;
            }
            if ((this.currentMessageObject.isRoundVideo()) && (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) && (MediaController.getInstance().isRoundVideoDrawingReady()))
            {
              bool1 = true;
              this.drawTime = true;
              break label2948;
            }
            if ((this.currentMessageObject.type == 5) && (Theme.chat_roundVideoShadow != null))
            {
              j = this.photoImage.getImageX() - AndroidUtilities.dp(3.0F);
              i = this.photoImage.getImageY() - AndroidUtilities.dp(2.0F);
              Theme.chat_roundVideoShadow.setAlpha((int)(this.photoImage.getCurrentAlpha() * 255.0F));
              Theme.chat_roundVideoShadow.setBounds(j, i, AndroidUtilities.roundMessageSize + j + AndroidUtilities.dp(6.0F), AndroidUtilities.roundMessageSize + i + AndroidUtilities.dp(6.0F));
              Theme.chat_roundVideoShadow.draw(paramCanvas);
            }
            bool2 = this.photoImage.draw(paramCanvas);
            bool3 = this.drawTime;
            this.drawTime = this.photoImage.getVisible();
            bool1 = bool2;
            if (this.currentPosition == null) {
              break label2948;
            }
            bool1 = bool2;
            if (bool3 == this.drawTime) {
              break label2948;
            }
            localObject2 = (ViewGroup)getParent();
            bool1 = bool2;
            if (localObject2 == null) {
              break label2948;
            }
            if (!this.currentPosition.last)
            {
              j = ((ViewGroup)localObject2).getChildCount();
              i = 0;
              bool1 = bool2;
              if (i >= j) {
                break label2948;
              }
              localObject3 = ((ViewGroup)localObject2).getChildAt(i);
              if ((localObject3 == this) || (!(localObject3 instanceof ChatMessageCell))) {}
              do
              {
                do
                {
                  i++;
                  break;
                  localObject3 = (ChatMessageCell)localObject3;
                } while (((ChatMessageCell)localObject3).getCurrentMessagesGroup() != this.currentMessagesGroup);
                localObject4 = ((ChatMessageCell)localObject3).getCurrentPosition();
              } while ((!((MessageObject.GroupedMessagePosition)localObject4).last) || (((MessageObject.GroupedMessagePosition)localObject4).maxY != this.currentPosition.maxY) || (((ChatMessageCell)localObject3).timeX - AndroidUtilities.dp(4.0F) + ((ChatMessageCell)localObject3).getLeft() >= getRight()));
              if (!this.drawTime) {}
              for (bool1 = true;; bool1 = false)
              {
                ((ChatMessageCell)localObject3).groupPhotoInvisible = bool1;
                ((ChatMessageCell)localObject3).invalidate();
                ((ViewGroup)localObject2).invalidate();
                break;
              }
            }
            ((ViewGroup)localObject2).invalidate();
            bool1 = bool2;
            break label2948;
            label6012:
            i = 5;
            break label3019;
            label6018:
            if ((this.documentAttachType == 7) || (this.currentMessageObject.type == 5))
            {
              if (this.durationLayout == null) {
                break label3442;
              }
              bool2 = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
              if ((bool2) && (this.currentMessageObject.type == 5)) {
                drawRoundProgress(paramCanvas);
              }
              if (this.documentAttachType == 7)
              {
                i = this.backgroundDrawableLeft;
                if ((this.currentMessageObject.isOutOwner()) || (this.drawPinnedBottom))
                {
                  f = 12.0F;
                  label6115:
                  j = i + AndroidUtilities.dp(f);
                  k = this.layoutHeight;
                  if (!this.drawPinnedBottom) {
                    break label6202;
                  }
                }
                label6202:
                for (i = 2;; i = 0)
                {
                  i = k - AndroidUtilities.dp(6.3F - i) - this.timeLayout.getHeight();
                  paramCanvas.save();
                  paramCanvas.translate(j, i);
                  this.durationLayout.draw(paramCanvas);
                  paramCanvas.restore();
                  break;
                  f = 18.0F;
                  break label6115;
                }
              }
              j = this.backgroundDrawableLeft + AndroidUtilities.dp(8.0F);
              k = this.layoutHeight;
              if (this.drawPinnedBottom) {}
              for (i = 2;; i = 0)
              {
                i = k - AndroidUtilities.dp(28 - i);
                this.rect.set(j, i, this.timeWidthAudio + j + AndroidUtilities.dp(22.0F), AndroidUtilities.dp(17.0F) + i);
                paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0F), AndroidUtilities.dp(4.0F), Theme.chat_actionBackgroundPaint);
                if ((bool2) || (!this.currentMessageObject.isContentUnread())) {
                  break label6411;
                }
                Theme.chat_docBackPaint.setColor(Theme.getColor("chat_mediaTimeText"));
                paramCanvas.drawCircle(this.timeWidthAudio + j + AndroidUtilities.dp(12.0F), AndroidUtilities.dp(8.3F) + i, AndroidUtilities.dp(3.0F), Theme.chat_docBackPaint);
                j += AndroidUtilities.dp(4.0F);
                i += AndroidUtilities.dp(1.7F);
                break;
              }
              label6411:
              if ((bool2) && (!MediaController.getInstance().isMessagePaused())) {
                this.roundVideoPlayingDrawable.start();
              }
              for (;;)
              {
                setDrawableBounds(this.roundVideoPlayingDrawable, this.timeWidthAudio + j + AndroidUtilities.dp(6.0F), AndroidUtilities.dp(2.3F) + i);
                this.roundVideoPlayingDrawable.draw(paramCanvas);
                break;
                this.roundVideoPlayingDrawable.stop();
              }
            }
            if (this.documentAttachType == 5)
            {
              if (this.currentMessageObject.isOutOwner())
              {
                Theme.chat_audioTitlePaint.setColor(Theme.getColor("chat_outAudioTitleText"));
                Theme.chat_audioPerformerPaint.setColor(Theme.getColor("chat_outAudioPerfomerText"));
                Theme.chat_audioTimePaint.setColor(Theme.getColor("chat_outAudioDurationText"));
                localObject3 = this.radialProgress;
                if ((isDrawSelectedBackground()) || (this.buttonPressed != 0))
                {
                  localObject2 = "chat_outAudioSelectedProgress";
                  label6561:
                  ((RadialProgress)localObject3).setProgressColor(Theme.getColor((String)localObject2));
                  this.radialProgress.draw(paramCanvas);
                  paramCanvas.save();
                  paramCanvas.translate(this.timeAudioX + this.songX, AndroidUtilities.dp(13.0F) + this.namesOffset + this.mediaOffsetY);
                  this.songLayout.draw(paramCanvas);
                  paramCanvas.restore();
                  paramCanvas.save();
                  if (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                    break label6901;
                  }
                  paramCanvas.translate(this.seekBarX, this.seekBarY);
                  this.seekBar.draw(paramCanvas);
                  label6666:
                  paramCanvas.restore();
                  paramCanvas.save();
                  paramCanvas.translate(this.timeAudioX, AndroidUtilities.dp(57.0F) + this.namesOffset + this.mediaOffsetY);
                  this.durationLayout.draw(paramCanvas);
                  paramCanvas.restore();
                  if (!this.currentMessageObject.isOutOwner()) {
                    break label6950;
                  }
                  if (!isDrawSelectedBackground()) {
                    break label6943;
                  }
                  localObject2 = Theme.chat_msgOutMenuSelectedDrawable;
                  label6734:
                  j = this.buttonX;
                  i = this.backgroundWidth;
                  if (this.currentMessageObject.type != 0) {
                    break label6971;
                  }
                }
              }
              label6901:
              label6943:
              label6950:
              label6971:
              for (f = 58.0F;; f = 48.0F)
              {
                j = i + j - AndroidUtilities.dp(f);
                this.otherX = j;
                i = this.buttonY - AndroidUtilities.dp(5.0F);
                this.otherY = i;
                setDrawableBounds((Drawable)localObject2, j, i);
                ((Drawable)localObject2).draw(paramCanvas);
                break;
                localObject2 = "chat_outAudioProgress";
                break label6561;
                Theme.chat_audioTitlePaint.setColor(Theme.getColor("chat_inAudioTitleText"));
                Theme.chat_audioPerformerPaint.setColor(Theme.getColor("chat_inAudioPerfomerText"));
                Theme.chat_audioTimePaint.setColor(Theme.getColor("chat_inAudioDurationText"));
                localObject3 = this.radialProgress;
                if ((isDrawSelectedBackground()) || (this.buttonPressed != 0)) {}
                for (localObject2 = "chat_inAudioSelectedProgress";; localObject2 = "chat_inAudioProgress")
                {
                  ((RadialProgress)localObject3).setProgressColor(Theme.getColor((String)localObject2));
                  break;
                }
                paramCanvas.translate(this.timeAudioX + this.performerX, AndroidUtilities.dp(35.0F) + this.namesOffset + this.mediaOffsetY);
                this.performerLayout.draw(paramCanvas);
                break label6666;
                localObject2 = Theme.chat_msgOutMenuDrawable;
                break label6734;
                if (isDrawSelectedBackground()) {}
                for (localObject2 = Theme.chat_msgInMenuSelectedDrawable;; localObject2 = Theme.chat_msgInMenuDrawable) {
                  break;
                }
              }
            }
            if (this.documentAttachType != 3) {
              break label3442;
            }
            if (this.currentMessageObject.isOutOwner())
            {
              localObject3 = Theme.chat_audioTimePaint;
              if (isDrawSelectedBackground())
              {
                localObject2 = "chat_outAudioDurationSelectedText";
                label7013:
                ((TextPaint)localObject3).setColor(Theme.getColor((String)localObject2));
                localObject3 = this.radialProgress;
                if ((!isDrawSelectedBackground()) && (this.buttonPressed == 0)) {
                  break label7257;
                }
                localObject2 = "chat_outAudioSelectedProgress";
                label7046:
                ((RadialProgress)localObject3).setProgressColor(Theme.getColor((String)localObject2));
                this.radialProgress.draw(paramCanvas);
                paramCanvas.save();
                if (!this.useSeekBarWaweform) {
                  break label7339;
                }
                paramCanvas.translate(this.seekBarX + AndroidUtilities.dp(13.0F), this.seekBarY);
                this.seekBarWaveform.draw(paramCanvas);
                label7104:
                paramCanvas.restore();
                paramCanvas.save();
                paramCanvas.translate(this.timeAudioX, AndroidUtilities.dp(44.0F) + this.namesOffset + this.mediaOffsetY);
                this.durationLayout.draw(paramCanvas);
                paramCanvas.restore();
                if ((this.currentMessageObject.type == 0) || (!this.currentMessageObject.isContentUnread())) {
                  break label3442;
                }
                localObject3 = Theme.chat_docBackPaint;
                if (!this.currentMessageObject.isOutOwner()) {
                  break label7364;
                }
              }
            }
            label7257:
            label7280:
            label7332:
            label7339:
            label7364:
            for (localObject2 = "chat_outVoiceSeekbarFill";; localObject2 = "chat_inVoiceSeekbarFill")
            {
              ((Paint)localObject3).setColor(Theme.getColor((String)localObject2));
              paramCanvas.drawCircle(this.timeAudioX + this.timeWidthAudio + AndroidUtilities.dp(6.0F), AndroidUtilities.dp(51.0F) + this.namesOffset + this.mediaOffsetY, AndroidUtilities.dp(3.0F), Theme.chat_docBackPaint);
              break;
              localObject2 = "chat_outAudioDurationText";
              break label7013;
              localObject2 = "chat_outAudioProgress";
              break label7046;
              localObject3 = Theme.chat_audioTimePaint;
              if (isDrawSelectedBackground())
              {
                localObject2 = "chat_inAudioDurationSelectedText";
                ((TextPaint)localObject3).setColor(Theme.getColor((String)localObject2));
                localObject3 = this.radialProgress;
                if ((!isDrawSelectedBackground()) && (this.buttonPressed == 0)) {
                  break label7332;
                }
              }
              for (localObject2 = "chat_inAudioSelectedProgress";; localObject2 = "chat_inAudioProgress")
              {
                ((RadialProgress)localObject3).setProgressColor(Theme.getColor((String)localObject2));
                break;
                localObject2 = "chat_inAudioDurationText";
                break label7280;
              }
              paramCanvas.translate(this.seekBarX, this.seekBarY);
              this.seekBar.draw(paramCanvas);
              break label7104;
            }
            label7371:
            if (this.currentMessageObject.type == 4)
            {
              if (this.docTitleLayout == null) {
                break label3857;
              }
              if (this.currentMessageObject.isOutOwner()) {
                if ((this.currentMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeoLive))
                {
                  Theme.chat_locationTitlePaint.setColor(Theme.getColor("chat_messageTextOut"));
                  label7427:
                  localObject3 = Theme.chat_locationAddressPaint;
                  if (!isDrawSelectedBackground()) {
                    break label7982;
                  }
                  localObject2 = "chat_outVenueInfoSelectedText";
                  label7443:
                  ((TextPaint)localObject3).setColor(Theme.getColor((String)localObject2));
                  if (!(this.currentMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeoLive)) {
                    break label8094;
                  }
                  i = this.photoImage.getImageY2() + AndroidUtilities.dp(30.0F);
                  if (!this.locationExpired)
                  {
                    this.forceNotDrawTime = true;
                    f = Math.abs(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - this.currentMessageObject.messageOwner.date) / this.currentMessageObject.messageOwner.media.period;
                    this.rect.set(this.photoImage.getImageX2() - AndroidUtilities.dp(43.0F), i - AndroidUtilities.dp(15.0F), this.photoImage.getImageX2() - AndroidUtilities.dp(13.0F), AndroidUtilities.dp(15.0F) + i);
                    if (!this.currentMessageObject.isOutOwner()) {
                      break label8067;
                    }
                    Theme.chat_radialProgress2Paint.setColor(Theme.getColor("chat_outInstant"));
                    Theme.chat_livePaint.setColor(Theme.getColor("chat_outInstant"));
                  }
                }
              }
              for (;;)
              {
                Theme.chat_radialProgress2Paint.setAlpha(50);
                paramCanvas.drawCircle(this.rect.centerX(), this.rect.centerY(), AndroidUtilities.dp(15.0F), Theme.chat_radialProgress2Paint);
                Theme.chat_radialProgress2Paint.setAlpha(255);
                paramCanvas.drawArc(this.rect, -90.0F, -360.0F * (1.0F - f), false, Theme.chat_radialProgress2Paint);
                localObject2 = LocaleController.formatLocationLeftTime(Math.abs(this.currentMessageObject.messageOwner.media.period - (ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - this.currentMessageObject.messageOwner.date)));
                f = Theme.chat_livePaint.measureText((String)localObject2);
                paramCanvas.drawText((String)localObject2, this.rect.centerX() - f / 2.0F, AndroidUtilities.dp(4.0F) + i, Theme.chat_livePaint);
                paramCanvas.save();
                paramCanvas.translate(this.photoImage.getImageX() + AndroidUtilities.dp(10.0F), this.photoImage.getImageY2() + AndroidUtilities.dp(10.0F));
                this.docTitleLayout.draw(paramCanvas);
                paramCanvas.translate(0.0F, AndroidUtilities.dp(23.0F));
                this.infoLayout.draw(paramCanvas);
                paramCanvas.restore();
                j = this.photoImage.getImageX() + this.photoImage.getImageWidth() / 2 - AndroidUtilities.dp(31.0F);
                i = this.photoImage.getImageY() + this.photoImage.getImageHeight() / 2 - AndroidUtilities.dp(38.0F);
                setDrawableBounds(Theme.chat_msgAvatarLiveLocationDrawable, j, i);
                Theme.chat_msgAvatarLiveLocationDrawable.draw(paramCanvas);
                this.locationImageReceiver.setImageCoords(AndroidUtilities.dp(5.0F) + j, AndroidUtilities.dp(5.0F) + i, AndroidUtilities.dp(52.0F), AndroidUtilities.dp(52.0F));
                this.locationImageReceiver.draw(paramCanvas);
                break;
                Theme.chat_locationTitlePaint.setColor(Theme.getColor("chat_outVenueNameText"));
                break label7427;
                label7982:
                localObject2 = "chat_outVenueInfoText";
                break label7443;
                if ((this.currentMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeoLive))
                {
                  Theme.chat_locationTitlePaint.setColor(Theme.getColor("chat_messageTextIn"));
                  label8017:
                  localObject3 = Theme.chat_locationAddressPaint;
                  if (!isDrawSelectedBackground()) {
                    break label8060;
                  }
                }
                label8060:
                for (localObject2 = "chat_inVenueInfoSelectedText";; localObject2 = "chat_inVenueInfoText")
                {
                  ((TextPaint)localObject3).setColor(Theme.getColor((String)localObject2));
                  break;
                  Theme.chat_locationTitlePaint.setColor(Theme.getColor("chat_inVenueNameText"));
                  break label8017;
                }
                label8067:
                Theme.chat_radialProgress2Paint.setColor(Theme.getColor("chat_inInstant"));
                Theme.chat_livePaint.setColor(Theme.getColor("chat_inInstant"));
              }
              label8094:
              paramCanvas.save();
              paramCanvas.translate(this.docTitleOffsetX + this.photoImage.getImageX() + this.photoImage.getImageWidth() + AndroidUtilities.dp(10.0F), this.photoImage.getImageY() + AndroidUtilities.dp(8.0F));
              this.docTitleLayout.draw(paramCanvas);
              paramCanvas.restore();
              if (this.infoLayout == null) {
                break label3857;
              }
              paramCanvas.save();
              paramCanvas.translate(this.photoImage.getImageX() + this.photoImage.getImageWidth() + AndroidUtilities.dp(10.0F), this.photoImage.getImageY() + this.docTitleLayout.getLineBottom(this.docTitleLayout.getLineCount() - 1) + AndroidUtilities.dp(13.0F));
              this.infoLayout.draw(paramCanvas);
              paramCanvas.restore();
              break label3857;
            }
            if (this.currentMessageObject.type == 16)
            {
              if (this.currentMessageObject.isOutOwner())
              {
                Theme.chat_audioTitlePaint.setColor(Theme.getColor("chat_messageTextOut"));
                localObject3 = Theme.chat_contactPhonePaint;
                if (isDrawSelectedBackground())
                {
                  localObject2 = "chat_outTimeSelectedText";
                  label8294:
                  ((TextPaint)localObject3).setColor(Theme.getColor((String)localObject2));
                  this.forceNotDrawTime = true;
                  if (!this.currentMessageObject.isOutOwner()) {
                    break label8594;
                  }
                  i = this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(16.0F);
                  label8336:
                  this.otherX = i;
                  if (this.titleLayout != null)
                  {
                    paramCanvas.save();
                    paramCanvas.translate(i, AndroidUtilities.dp(12.0F) + this.namesOffset);
                    this.titleLayout.draw(paramCanvas);
                    paramCanvas.restore();
                  }
                  if (this.docTitleLayout != null)
                  {
                    paramCanvas.save();
                    paramCanvas.translate(AndroidUtilities.dp(19.0F) + i, AndroidUtilities.dp(37.0F) + this.namesOffset);
                    this.docTitleLayout.draw(paramCanvas);
                    paramCanvas.restore();
                  }
                  if (!this.currentMessageObject.isOutOwner()) {
                    break label8640;
                  }
                  localObject4 = Theme.chat_msgCallUpGreenDrawable;
                  if ((!isDrawSelectedBackground()) && (!this.otherPressed)) {
                    break label8633;
                  }
                }
              }
              label8594:
              label8633:
              for (localObject2 = Theme.chat_msgOutCallSelectedDrawable;; localObject2 = Theme.chat_msgOutCallDrawable)
              {
                setDrawableBounds((Drawable)localObject4, i - AndroidUtilities.dp(3.0F), AndroidUtilities.dp(36.0F) + this.namesOffset);
                ((Drawable)localObject4).draw(paramCanvas);
                k = AndroidUtilities.dp(205.0F);
                j = AndroidUtilities.dp(22.0F);
                this.otherY = j;
                setDrawableBounds((Drawable)localObject2, k + i, j);
                ((Drawable)localObject2).draw(paramCanvas);
                break;
                localObject2 = "chat_outTimeText";
                break label8294;
                Theme.chat_audioTitlePaint.setColor(Theme.getColor("chat_messageTextIn"));
                localObject3 = Theme.chat_contactPhonePaint;
                if (isDrawSelectedBackground()) {}
                for (localObject2 = "chat_inTimeSelectedText";; localObject2 = "chat_inTimeText")
                {
                  ((TextPaint)localObject3).setColor(Theme.getColor((String)localObject2));
                  break;
                }
                if ((this.isChat) && (this.currentMessageObject.needDrawAvatar()))
                {
                  i = AndroidUtilities.dp(74.0F);
                  break label8336;
                }
                i = AndroidUtilities.dp(25.0F);
                break label8336;
              }
              label8640:
              localObject2 = this.currentMessageObject.messageOwner.action.reason;
              if (((localObject2 instanceof TLRPC.TL_phoneCallDiscardReasonMissed)) || ((localObject2 instanceof TLRPC.TL_phoneCallDiscardReasonBusy)))
              {
                localObject2 = Theme.chat_msgCallDownRedDrawable;
                label8672:
                if ((!isDrawSelectedBackground()) && (!this.otherPressed)) {
                  break label8707;
                }
              }
              label8707:
              for (localObject3 = Theme.chat_msgInCallSelectedDrawable;; localObject3 = Theme.chat_msgInCallDrawable)
              {
                localObject4 = localObject2;
                localObject2 = localObject3;
                break;
                localObject2 = Theme.chat_msgCallDownGreenDrawable;
                break label8672;
              }
            }
            if (this.currentMessageObject.type != 12) {
              break label3857;
            }
            localObject3 = Theme.chat_contactNamePaint;
            if (this.currentMessageObject.isOutOwner())
            {
              localObject2 = "chat_outContactNameText";
              label8746:
              ((TextPaint)localObject3).setColor(Theme.getColor((String)localObject2));
              localObject3 = Theme.chat_contactPhonePaint;
              if (!this.currentMessageObject.isOutOwner()) {
                break label9002;
              }
              localObject2 = "chat_outContactPhoneText";
              label8774:
              ((TextPaint)localObject3).setColor(Theme.getColor((String)localObject2));
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
                break label9016;
              }
              if (!isDrawSelectedBackground()) {
                break label9009;
              }
            }
            label9002:
            label9009:
            for (localObject2 = Theme.chat_msgOutMenuSelectedDrawable;; localObject2 = Theme.chat_msgOutMenuDrawable)
            {
              i = this.photoImage.getImageX() + this.backgroundWidth - AndroidUtilities.dp(48.0F);
              this.otherX = i;
              j = this.photoImage.getImageY() - AndroidUtilities.dp(5.0F);
              this.otherY = j;
              setDrawableBounds((Drawable)localObject2, i, j);
              ((Drawable)localObject2).draw(paramCanvas);
              break;
              localObject2 = "chat_inContactNameText";
              break label8746;
              localObject2 = "chat_inContactPhoneText";
              break label8774;
            }
            label9016:
            if (isDrawSelectedBackground()) {}
            for (localObject2 = Theme.chat_msgInMenuSelectedDrawable;; localObject2 = Theme.chat_msgInMenuDrawable) {
              break;
            }
            label9037:
            if (this.hasOldCaptionPreview)
            {
              i = this.backgroundDrawableLeft;
              if (this.currentMessageObject.isOutOwner())
              {
                f = 11.0F;
                label9065:
                this.captionX = (AndroidUtilities.dp(f) + i + this.captionOffsetX);
                j = this.totalHeight;
                i = this.captionHeight;
                if (!this.drawPinnedTop) {
                  break label9144;
                }
              }
              label9144:
              for (f = 9.0F;; f = 10.0F)
              {
                this.captionY = (j - i - AndroidUtilities.dp(f) - this.linkPreviewHeight - AndroidUtilities.dp(17.0F));
                break;
                f = 17.0F;
                break label9065;
              }
            }
            i = this.backgroundDrawableLeft;
            if (this.currentMessageObject.isOutOwner())
            {
              f = 11.0F;
              label9173:
              this.captionX = (AndroidUtilities.dp(f) + i + this.captionOffsetX);
              j = this.totalHeight;
              i = this.captionHeight;
              if (!this.drawPinnedTop) {
                break label9240;
              }
            }
            label9240:
            for (f = 9.0F;; f = 10.0F)
            {
              this.captionY = (j - i - AndroidUtilities.dp(f));
              break;
              f = 17.0F;
              break label9173;
            }
            label9248:
            i = this.backgroundDrawableLeft;
            if (this.currentMessageObject.isOutOwner()) {}
            for (f = 11.0F;; f = 17.0F)
            {
              j = i + AndroidUtilities.dp(f);
              break;
            }
            label9290:
            f = 10.0F;
            break label4029;
            label9298:
            localObject2 = "chat_inPreviewLine";
            break label4074;
            label9305:
            localObject2 = "chat_inSiteNameText";
            break label4149;
            label9312:
            if (this.hasInvoicePreview) {}
            for (i = 0;; i = AndroidUtilities.dp(10.0F)) {
              break;
            }
            label9336:
            Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_messageTextIn"));
            break label4263;
            label9351:
            localObject2 = "chat_outFileInfoText";
            break label4395;
            label9358:
            localObject2 = "chat_outFileBackground";
            break label4420;
            label9365:
            localObject2 = Theme.chat_msgOutMenuDrawable;
            break label4440;
            label9372:
            Theme.chat_docNamePaint.setColor(Theme.getColor("chat_inFileNameText"));
            localObject3 = Theme.chat_infoPaint;
            if (isDrawSelectedBackground())
            {
              localObject2 = "chat_inFileInfoSelectedText";
              label9400:
              ((TextPaint)localObject3).setColor(Theme.getColor((String)localObject2));
              localObject3 = Theme.chat_docBackPaint;
              if (!isDrawSelectedBackground()) {
                break label9455;
              }
              localObject2 = "chat_inFileBackgroundSelected";
              label9425:
              ((Paint)localObject3).setColor(Theme.getColor((String)localObject2));
              if (!isDrawSelectedBackground()) {
                break label9462;
              }
            }
            label9455:
            label9462:
            for (localObject2 = Theme.chat_msgInMenuSelectedDrawable;; localObject2 = Theme.chat_msgInMenuDrawable)
            {
              break;
              localObject2 = "chat_inFileInfoText";
              break label9400;
              localObject2 = "chat_inFileBackground";
              break label9425;
            }
            label9469:
            j = this.photoImage.getImageX() + this.backgroundWidth - AndroidUtilities.dp(40.0F);
            this.otherX = j;
            i = this.photoImage.getImageY() + AndroidUtilities.dp(1.0F);
            this.otherY = i;
            setDrawableBounds((Drawable)localObject2, j, i);
            break label4512;
            label9527:
            i = 10;
            break label4631;
            label9534:
            if (this.buttonState != 1) {
              break label4631;
            }
            if (this.currentMessageObject.isOutOwner()) {}
            for (i = 8;; i = 11) {
              break;
            }
            label9566:
            i = 0;
            break label4662;
            label9572:
            this.radialProgress.swapBackground(Theme.chat_photoStatesDrawables[this.buttonState][this.buttonPressed]);
            break label4673;
            label9596:
            localObject3 = "chat_outFileProgress";
            break label4785;
            label9604:
            localObject4 = this.radialProgress;
            if (isDrawSelectedBackground()) {}
            for (localObject3 = "chat_inFileProgressSelected";; localObject3 = "chat_inFileProgress")
            {
              ((RadialProgress)localObject4).setProgressColor(Theme.getColor((String)localObject3));
              i = i1;
              break;
            }
            label9647:
            if (this.buttonState == -1) {
              this.radialProgress.setHideCurrentDrawable(true);
            }
            this.radialProgress.setProgressColor(Theme.getColor("chat_mediaProgress"));
            i = i1;
            break label4799;
            label9683:
            j = this.buttonX;
            i = this.backgroundWidth;
            if (this.currentMessageObject.type == 0)
            {
              f = 58.0F;
              label9710:
              i = i + j - AndroidUtilities.dp(f);
              this.otherX = i;
              j = this.buttonY - AndroidUtilities.dp(5.0F);
              this.otherY = j;
              setDrawableBounds((Drawable)localObject2, i, j);
              k = this.buttonX + AndroidUtilities.dp(53.0F);
              j = this.buttonY + AndroidUtilities.dp(4.0F);
              i = this.buttonY + AndroidUtilities.dp(27.0F);
              if (!this.currentMessageObject.isOutOwner()) {
                break label9859;
              }
              localObject4 = this.radialProgress;
              if ((!isDrawSelectedBackground()) && (this.buttonPressed == 0)) {
                break label9851;
              }
            }
            label9851:
            for (localObject3 = "chat_outAudioSelectedProgress";; localObject3 = "chat_outAudioProgress")
            {
              ((RadialProgress)localObject4).setProgressColor(Theme.getColor((String)localObject3));
              break;
              f = 48.0F;
              break label9710;
            }
            label9859:
            localObject4 = this.radialProgress;
            if ((isDrawSelectedBackground()) || (this.buttonPressed != 0)) {}
            for (localObject3 = "chat_inAudioSelectedProgress";; localObject3 = "chat_inAudioProgress")
            {
              ((RadialProgress)localObject4).setProgressColor(Theme.getColor((String)localObject3));
              break;
            }
            localException2 = localException2;
            FileLog.e(localException2);
          }
        }
        catch (Exception localException3)
        {
          label9921:
          label9965:
          label10180:
          do
          {
            do
            {
              for (;;)
              {
                FileLog.e(localException3);
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
                n = ((BotButton)localObject4).x;
                i2 = ((BotButton)localObject4).width;
                m = AndroidUtilities.dp(3.0F);
                k = Theme.chat_botInlineDrawable.getIntrinsicWidth();
                setDrawableBounds(Theme.chat_botInlineDrawable, n + i2 - m - k + i, AndroidUtilities.dp(3.0F) + i1);
                Theme.chat_botInlineDrawable.draw(paramCanvas);
              }
            } while ((!(((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonCallback)) && (!(((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonRequestGeoLocation)) && (!(((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonGame)) && (!(((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonBuy)));
            if ((((((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonCallback)) || ((((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonGame)) || ((((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonBuy))) && ((!SendMessagesHelper.getInstance(this.currentAccount).isSendingCallback(this.currentMessageObject, ((BotButton)localObject4).button)) && ((!(((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonRequestGeoLocation)) || (!SendMessagesHelper.getInstance(this.currentAccount).isSendingCurrentLocation(this.currentMessageObject, ((BotButton)localObject4).button))))) {
              break;
            }
            k = 1;
          } while ((k == 0) && ((k != 0) || (((BotButton)localObject4).progressAlpha == 0.0F)));
          Theme.chat_botProgressPaint.setAlpha(Math.min(255, (int)(((BotButton)localObject4).progressAlpha * 255.0F)));
          m = ((BotButton)localObject4).x + ((BotButton)localObject4).width - AndroidUtilities.dp(12.0F) + i;
          this.rect.set(m, AndroidUtilities.dp(4.0F) + i1, AndroidUtilities.dp(8.0F) + m, AndroidUtilities.dp(12.0F) + i1);
          paramCanvas.drawArc(this.rect, ((BotButton)localObject4).angle, 220.0F, false, Theme.chat_botProgressPaint);
          invalidate((int)this.rect.left - AndroidUtilities.dp(2.0F), (int)this.rect.top - AndroidUtilities.dp(2.0F), (int)this.rect.right + AndroidUtilities.dp(2.0F), (int)this.rect.bottom + AndroidUtilities.dp(2.0F));
          l2 = System.currentTimeMillis();
          if (Math.abs(((BotButton)localObject4).lastUpdateTime - System.currentTimeMillis()) < 1000L)
          {
            l1 = l2 - ((BotButton)localObject4).lastUpdateTime;
            f = (float)(360L * l1) / 2000.0F;
            BotButton.access$1202((BotButton)localObject4, (int)(((BotButton)localObject4).angle + f));
            BotButton.access$1202((BotButton)localObject4, ((BotButton)localObject4).angle - ((BotButton)localObject4).angle / 360 * 360);
            if (k == 0) {
              break label10518;
            }
            if (((BotButton)localObject4).progressAlpha < 1.0F)
            {
              BotButton.access$1102((BotButton)localObject4, ((BotButton)localObject4).progressAlpha + (float)l1 / 200.0F);
              if (((BotButton)localObject4).progressAlpha > 1.0F) {
                BotButton.access$1102((BotButton)localObject4, 1.0F);
              }
            }
          }
          for (;;)
          {
            BotButton.access$1302((BotButton)localObject4, l2);
            break;
            k = 0;
            break label10180;
            label10518:
            if (((BotButton)localObject4).progressAlpha > 0.0F)
            {
              BotButton.access$1102((BotButton)localObject4, ((BotButton)localObject4).progressAlpha - (float)l1 / 200.0F);
              if (((BotButton)localObject4).progressAlpha < 0.0F) {
                BotButton.access$1102((BotButton)localObject4, 0.0F);
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
    int i = 0;
    StaticLayout localStaticLayout = new StaticLayout(paramCharSequence, paramTextPaint, paramInt2, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
    int j = 0;
    int k = paramInt1;
    int m;
    if (j < paramInt3)
    {
      localStaticLayout.getLineDirections(j);
      if ((localStaticLayout.getLineLeft(j) != 0.0F) || (localStaticLayout.isRtlCharAt(localStaticLayout.getLineStart(j))) || (localStaticLayout.isRtlCharAt(localStaticLayout.getLineEnd(j)))) {
        paramInt1 = paramInt2;
      }
      m = localStaticLayout.getLineEnd(j);
      if (m == paramCharSequence.length()) {
        k = paramInt1;
      }
    }
    else
    {
      label119:
      return StaticLayoutEx.createStaticLayout(localSpannableStringBuilder, paramTextPaint, k, Layout.Alignment.ALIGN_NORMAL, 1.0F, AndroidUtilities.dp(1.0F), false, TextUtils.TruncateAt.END, k, paramInt4);
    }
    k = m - 1;
    if (localSpannableStringBuilder.charAt(k + i) == ' ')
    {
      localSpannableStringBuilder.replace(k + i, k + i + 1, "\n");
      m = i;
    }
    for (;;)
    {
      k = paramInt1;
      if (j == localStaticLayout.getLineCount() - 1) {
        break label119;
      }
      k = paramInt1;
      if (j == paramInt4 - 1) {
        break label119;
      }
      j++;
      i = m;
      break;
      m = i;
      if (localSpannableStringBuilder.charAt(k + i) != '\n')
      {
        localSpannableStringBuilder.insert(k + i, "\n");
        m = i + 1;
      }
    }
  }
  
  private int getAdditionalWidthForPosition(MessageObject.GroupedMessagePosition paramGroupedMessagePosition)
  {
    int i = 0;
    int j = 0;
    if (paramGroupedMessagePosition != null)
    {
      if ((paramGroupedMessagePosition.flags & 0x2) == 0) {
        j = 0 + AndroidUtilities.dp(4.0F);
      }
      i = j;
      if ((paramGroupedMessagePosition.flags & 0x1) == 0) {
        i = j + AndroidUtilities.dp(4.0F);
      }
    }
    return i;
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
    if ((this.documentAttachType == 3) || (this.documentAttachType == 5)) {
      if (this.buttonState == -1) {
        localObject = null;
      }
    }
    for (;;)
    {
      return (Drawable)localObject;
      this.radialProgress.setAlphaForPrevious(false);
      this.radialProgress.setAlphaForMiniPrevious(true);
      localObject = Theme.chat_fileStatesDrawable;
      if (this.currentMessageObject.isOutOwner())
      {
        i = this.buttonState;
        label81:
        localObject = localObject[i];
        if ((!isDrawSelectedBackground()) && (this.buttonPressed == 0)) {
          break label122;
        }
      }
      label122:
      for (i = 1;; i = 0)
      {
        localObject = localObject[i];
        break;
        i = this.buttonState + 5;
        break label81;
      }
      if ((this.documentAttachType == 1) && (!this.drawPhotoImage))
      {
        this.radialProgress.setAlphaForPrevious(false);
        if (this.buttonState == -1)
        {
          localObject = Theme.chat_fileStatesDrawable;
          if (this.currentMessageObject.isOutOwner())
          {
            label173:
            localObject = localObject[i];
            if (!isDrawSelectedBackground()) {
              break label204;
            }
          }
          label204:
          for (i = i1;; i = 0)
          {
            localObject = localObject[i];
            break;
            i = 8;
            break label173;
          }
        }
        if (this.buttonState == 0)
        {
          localObject = Theme.chat_fileStatesDrawable;
          if (this.currentMessageObject.isOutOwner())
          {
            i = 2;
            label233:
            localObject = localObject[i];
            if (!isDrawSelectedBackground()) {
              break label263;
            }
          }
          label263:
          for (i = k;; i = 0)
          {
            localObject = localObject[i];
            break;
            i = 7;
            break label233;
          }
        }
        if (this.buttonState == 1)
        {
          localObject = Theme.chat_fileStatesDrawable;
          if (this.currentMessageObject.isOutOwner())
          {
            i = 4;
            label293:
            localObject = localObject[i];
            if (!isDrawSelectedBackground()) {
              break label324;
            }
          }
          label324:
          for (i = m;; i = 0)
          {
            localObject = localObject[i];
            break;
            i = 9;
            break label293;
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
                localObject = Theme.chat_photoStatesDrawables[i];
                if (!isDrawSelectedBackground())
                {
                  i = j;
                  if (this.buttonPressed == 0) {}
                }
                else
                {
                  i = 1;
                }
                localObject = localObject[i];
                break;
                i = 10;
              }
            }
            if (this.currentMessageObject.isOutOwner()) {}
            for (i = 8;; i = 11) {
              break;
            }
          }
          localObject = Theme.chat_photoStatesDrawables[this.buttonState][this.buttonPressed];
          continue;
        }
        if ((this.buttonState == -1) && (this.documentAttachType == 1))
        {
          localObject = Theme.chat_photoStatesDrawables;
          if (this.currentMessageObject.isOutOwner())
          {
            i = 9;
            label507:
            localObject = localObject[i];
            if (!isDrawSelectedBackground()) {
              break label538;
            }
          }
          label538:
          for (i = n;; i = 0)
          {
            localObject = localObject[i];
            break;
            i = 12;
            break label507;
          }
        }
      }
      localObject = null;
    }
  }
  
  private int getGroupPhotosWidth()
  {
    if ((!AndroidUtilities.isInMultiwindow) && (AndroidUtilities.isTablet()) && ((!AndroidUtilities.isSmallTablet()) || (getResources().getConfiguration().orientation == 2)))
    {
      int i = AndroidUtilities.displaySize.x / 100 * 35;
      j = i;
      if (i < AndroidUtilities.dp(320.0F)) {
        j = AndroidUtilities.dp(320.0F);
      }
    }
    for (int j = AndroidUtilities.displaySize.x - j;; j = AndroidUtilities.displaySize.x) {
      return j;
    }
  }
  
  private int getMaxNameWidth()
  {
    if ((this.documentAttachType == 6) || (this.currentMessageObject.type == 5))
    {
      if (AndroidUtilities.isTablet()) {
        if ((this.isChat) && (!this.currentMessageObject.isOutOwner()) && (this.currentMessageObject.needDrawAvatar())) {
          i = AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(42.0F);
        }
      }
      for (;;)
      {
        i = i - this.backgroundWidth - AndroidUtilities.dp(57.0F);
        return i;
        i = AndroidUtilities.getMinTabletSide();
        continue;
        if ((this.isChat) && (!this.currentMessageObject.isOutOwner()) && (this.currentMessageObject.needDrawAvatar())) {
          i = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(42.0F);
        } else {
          i = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
        }
      }
    }
    if (this.currentMessagesGroup != null)
    {
      if (AndroidUtilities.isTablet()) {}
      for (int j = AndroidUtilities.getMinTabletSide();; j = AndroidUtilities.displaySize.x)
      {
        i = 0;
        for (int k = 0; k < this.currentMessagesGroup.posArray.size(); k++)
        {
          MessageObject.GroupedMessagePosition localGroupedMessagePosition = (MessageObject.GroupedMessagePosition)this.currentMessagesGroup.posArray.get(k);
          if (localGroupedMessagePosition.minY != 0) {
            break;
          }
          i = (int)(i + Math.ceil((localGroupedMessagePosition.pw + localGroupedMessagePosition.leftSpanOffset) / 1000.0F * j));
        }
      }
      if (this.isAvatarVisible) {}
      for (j = 48;; j = 0)
      {
        i -= AndroidUtilities.dp(j + 31);
        break;
      }
    }
    int i = this.backgroundWidth;
    if (this.mediaBackground) {}
    for (float f = 22.0F;; f = 31.0F)
    {
      i -= AndroidUtilities.dp(f);
      break;
    }
  }
  
  private Drawable getMiniDrawableForCurrentState()
  {
    Object localObject = null;
    int i = 1;
    if (this.miniButtonState < 0) {}
    label56:
    label93:
    do
    {
      return (Drawable)localObject;
      if ((this.documentAttachType == 3) || (this.documentAttachType == 5))
      {
        this.radialProgress.setAlphaForPrevious(false);
        localObject = Theme.chat_fileMiniStatesDrawable;
        if (this.currentMessageObject.isOutOwner())
        {
          i = this.miniButtonState;
          localObject = localObject[i];
          if ((!isDrawSelectedBackground()) && (this.miniButtonPressed == 0)) {
            break label93;
          }
        }
        for (i = 1;; i = 0)
        {
          localObject = localObject[i];
          break;
          i = this.miniButtonState + 2;
          break label56;
        }
      }
    } while (this.documentAttachType != 4);
    localObject = Theme.chat_fileMiniStatesDrawable[(this.miniButtonState + 4)];
    if (this.miniButtonPressed != 0) {}
    for (;;)
    {
      localObject = localObject[i];
      break;
      i = 0;
    }
  }
  
  private boolean intersect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    boolean bool = true;
    if (paramFloat1 <= paramFloat3) {
      if (paramFloat2 < paramFloat3) {}
    }
    for (;;)
    {
      return bool;
      bool = false;
      continue;
      if (paramFloat1 > paramFloat4) {
        bool = false;
      }
    }
  }
  
  private boolean isCurrentLocationTimeExpired(MessageObject paramMessageObject)
  {
    boolean bool = true;
    if (this.currentMessageObject.messageOwner.media.period % 60 == 0) {
      if (Math.abs(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - paramMessageObject.messageOwner.date) <= paramMessageObject.messageOwner.media.period) {}
    }
    for (;;)
    {
      return bool;
      bool = false;
      continue;
      if (Math.abs(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - paramMessageObject.messageOwner.date) <= paramMessageObject.messageOwner.media.period - 5) {
        bool = false;
      }
    }
  }
  
  private boolean isDrawSelectedBackground()
  {
    if (((isPressed()) && (this.isCheckPressed)) || ((!this.isCheckPressed) && (this.isPressed)) || (this.isHighlighted)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private boolean isOpenChatByShare(MessageObject paramMessageObject)
  {
    if ((paramMessageObject.messageOwner.fwd_from != null) && (paramMessageObject.messageOwner.fwd_from.saved_from_peer != null)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private boolean isPhotoDataChanged(MessageObject paramMessageObject)
  {
    boolean bool;
    if ((paramMessageObject.type == 0) || (paramMessageObject.type == 14)) {
      bool = false;
    }
    for (;;)
    {
      return bool;
      if (paramMessageObject.type == 4)
      {
        if (this.currentUrl == null)
        {
          bool = true;
        }
        else
        {
          double d1 = paramMessageObject.messageOwner.media.geo.lat;
          double d2 = paramMessageObject.messageOwner.media.geo._long;
          if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeoLive))
          {
            int i = this.backgroundWidth;
            int j = AndroidUtilities.dp(21.0F);
            int k = AndroidUtilities.dp(195.0F);
            double d3 = 268435456 / 3.141592653589793D;
            d1 = (1.5707963267948966D - 2.0D * Math.atan(Math.exp((Math.round(268435456 - Math.log((1.0D + Math.sin(3.141592653589793D * d1 / 180.0D)) / (1.0D - Math.sin(3.141592653589793D * d1 / 180.0D))) * d3 / 2.0D) - (AndroidUtilities.dp(10.3F) << 6) - 268435456) / d3))) * 180.0D / 3.141592653589793D;
            paramMessageObject = String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=%dx%d&maptype=roadmap&scale=%d&sensor=false", new Object[] { Double.valueOf(d1), Double.valueOf(d2), Integer.valueOf((int)((i - j) / AndroidUtilities.density)), Integer.valueOf((int)(k / AndroidUtilities.density)), Integer.valueOf(Math.min(2, (int)Math.ceil(AndroidUtilities.density))) });
          }
          for (;;)
          {
            if (paramMessageObject.equals(this.currentUrl)) {
              break label546;
            }
            bool = true;
            break;
            if (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.title)) {
              paramMessageObject = String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=72x72&maptype=roadmap&scale=%d&markers=color:red|size:mid|%f,%f&sensor=false", new Object[] { Double.valueOf(d1), Double.valueOf(d2), Integer.valueOf(Math.min(2, (int)Math.ceil(AndroidUtilities.density))), Double.valueOf(d1), Double.valueOf(d2) });
            } else {
              paramMessageObject = String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=200x100&maptype=roadmap&scale=%d&markers=color:red|size:mid|%f,%f&sensor=false", new Object[] { Double.valueOf(d1), Double.valueOf(d2), Integer.valueOf(Math.min(2, (int)Math.ceil(AndroidUtilities.density))), Double.valueOf(d1), Double.valueOf(d2) });
            }
          }
        }
      }
      else
      {
        if ((this.currentPhotoObject == null) || ((this.currentPhotoObject.location instanceof TLRPC.TL_fileLocationUnavailable)))
        {
          if ((paramMessageObject.type == 1) || (paramMessageObject.type == 5) || (paramMessageObject.type == 3) || (paramMessageObject.type == 8) || (paramMessageObject.type == 13)) {
            bool = true;
          }
        }
        else if ((this.currentMessageObject != null) && (this.photoNotSet) && (FileLoader.getPathToMessage(this.currentMessageObject.messageOwner).exists()))
        {
          bool = true;
          continue;
        }
        label546:
        bool = false;
      }
    }
  }
  
  private boolean isUserDataChanged()
  {
    boolean bool1 = false;
    boolean bool2 = true;
    boolean bool3;
    if ((this.currentMessageObject != null) && (!this.hasLinkPreview) && (this.currentMessageObject.messageOwner.media != null) && ((this.currentMessageObject.messageOwner.media.webpage instanceof TLRPC.TL_webPage))) {
      bool3 = bool2;
    }
    for (;;)
    {
      return bool3;
      if ((this.currentMessageObject == null) || ((this.currentUser == null) && (this.currentChat == null)))
      {
        bool3 = false;
      }
      else
      {
        bool3 = bool2;
        if (this.lastSendState == this.currentMessageObject.messageOwner.send_state)
        {
          bool3 = bool2;
          if (this.lastDeleteDate == this.currentMessageObject.messageOwner.destroyTime)
          {
            bool3 = bool2;
            if (this.lastViewsCount == this.currentMessageObject.messageOwner.views)
            {
              updateCurrentUserAndChat();
              Object localObject1 = null;
              Object localObject2 = localObject1;
              if (this.isAvatarVisible)
              {
                if ((this.currentUser != null) && (this.currentUser.photo != null)) {
                  localObject2 = this.currentUser.photo.photo_small;
                }
              }
              else
              {
                label184:
                if (this.replyTextLayout == null)
                {
                  bool3 = bool2;
                  if (this.currentMessageObject.replyMessageObject != null) {
                    continue;
                  }
                }
                if (this.currentPhoto == null)
                {
                  bool3 = bool2;
                  if (localObject2 != null) {
                    continue;
                  }
                }
                if (this.currentPhoto != null)
                {
                  bool3 = bool2;
                  if (localObject2 == null) {
                    continue;
                  }
                }
                if ((this.currentPhoto != null) && (localObject2 != null))
                {
                  bool3 = bool2;
                  if (this.currentPhoto.local_id != ((TLRPC.FileLocation)localObject2).local_id) {
                    continue;
                  }
                  bool3 = bool2;
                  if (this.currentPhoto.volume_id != ((TLRPC.FileLocation)localObject2).volume_id) {
                    continue;
                  }
                }
                localObject1 = null;
                localObject2 = localObject1;
                if (this.replyNameLayout != null)
                {
                  TLRPC.PhotoSize localPhotoSize = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.replyMessageObject.photoThumbs, 80);
                  localObject2 = localObject1;
                  if (localPhotoSize != null)
                  {
                    localObject2 = localObject1;
                    if (this.currentMessageObject.replyMessageObject.type != 13) {
                      localObject2 = localPhotoSize.location;
                    }
                  }
                }
                if (this.currentReplyPhoto == null)
                {
                  bool3 = bool2;
                  if (localObject2 != null) {
                    continue;
                  }
                }
                localObject1 = null;
                localObject2 = localObject1;
                if (this.drawName)
                {
                  localObject2 = localObject1;
                  if (this.isChat)
                  {
                    localObject2 = localObject1;
                    if (!this.currentMessageObject.isOutOwner())
                    {
                      if (this.currentUser == null) {
                        break label582;
                      }
                      localObject2 = UserObject.getUserName(this.currentUser);
                    }
                  }
                }
              }
              for (;;)
              {
                if (this.currentNameString == null)
                {
                  bool3 = bool2;
                  if (localObject2 != null) {
                    break;
                  }
                }
                if (this.currentNameString != null)
                {
                  bool3 = bool2;
                  if (localObject2 == null) {
                    break;
                  }
                }
                if ((this.currentNameString != null) && (localObject2 != null))
                {
                  bool3 = bool2;
                  if (!this.currentNameString.equals(localObject2)) {
                    break;
                  }
                }
                if (!this.drawForwardedName) {
                  break label605;
                }
                localObject2 = this.currentMessageObject.getForwardedName();
                if (((this.currentForwardNameString != null) || (localObject2 == null)) && ((this.currentForwardNameString == null) || (localObject2 != null)))
                {
                  bool3 = bool1;
                  if (this.currentForwardNameString != null)
                  {
                    bool3 = bool1;
                    if (localObject2 != null)
                    {
                      bool3 = bool1;
                      if (this.currentForwardNameString.equals(localObject2)) {}
                    }
                  }
                }
                else
                {
                  bool3 = true;
                }
                break;
                localObject2 = localObject1;
                if (this.currentChat == null) {
                  break label184;
                }
                localObject2 = localObject1;
                if (this.currentChat.photo == null) {
                  break label184;
                }
                localObject2 = this.currentChat.photo.photo_small;
                break label184;
                label582:
                localObject2 = localObject1;
                if (this.currentChat != null) {
                  localObject2 = this.currentChat.title;
                }
              }
              label605:
              bool3 = false;
            }
          }
        }
      }
    }
  }
  
  private void measureTime(MessageObject paramMessageObject)
  {
    Object localObject1;
    Object localObject2;
    label183:
    label211:
    int i;
    if (paramMessageObject.messageOwner.post_author != null)
    {
      localObject1 = paramMessageObject.messageOwner.post_author.replace("\n", "");
      localObject2 = null;
      if (this.currentMessageObject.isFromUser()) {
        localObject2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(paramMessageObject.messageOwner.from_id));
      }
      if ((paramMessageObject.isLiveLocation()) || (paramMessageObject.getDialogId() == 777000L) || (paramMessageObject.messageOwner.via_bot_id != 0) || (paramMessageObject.messageOwner.via_bot_name != null) || ((localObject2 != null) && (((TLRPC.User)localObject2).bot)) || ((paramMessageObject.messageOwner.flags & 0x8000) == 0) || (this.currentPosition != null)) {
        break label618;
      }
      localObject2 = LocaleController.getString("EditedMessage", NUM) + " " + LocaleController.getInstance().formatterDay.format(paramMessageObject.messageOwner.date * 1000L);
      if (localObject1 == null) {
        break label643;
      }
      this.currentTimeString = (", " + (String)localObject2);
      i = (int)Math.ceil(Theme.chat_timePaint.measureText(this.currentTimeString));
      this.timeWidth = i;
      this.timeTextWidth = i;
      if ((paramMessageObject.messageOwner.flags & 0x400) != 0)
      {
        this.currentViewsString = String.format("%s", new Object[] { LocaleController.formatShortNumber(Math.max(1, paramMessageObject.messageOwner.views), null) });
        this.viewsTextWidth = ((int)Math.ceil(Theme.chat_timePaint.measureText(this.currentViewsString)));
        this.timeWidth += this.viewsTextWidth + Theme.chat_msgInViewsDrawable.getIntrinsicWidth() + AndroidUtilities.dp(10.0F);
      }
      if (localObject1 != null)
      {
        if (this.availableTimeWidth == 0) {
          this.availableTimeWidth = AndroidUtilities.dp(1000.0F);
        }
        j = this.availableTimeWidth - this.timeWidth;
        i = j;
        if (paramMessageObject.isOutOwner())
        {
          if (paramMessageObject.type != 5) {
            break label651;
          }
          i = j - AndroidUtilities.dp(20.0F);
        }
        label394:
        int k = (int)Math.ceil(Theme.chat_timePaint.measureText((CharSequence)localObject1, 0, ((CharSequence)localObject1).length()));
        paramMessageObject = (MessageObject)localObject1;
        j = k;
        if (k > i)
        {
          if (i > 0) {
            break label665;
          }
          paramMessageObject = "";
        }
      }
    }
    for (int j = 0;; j = i)
    {
      this.currentTimeString = (paramMessageObject + this.currentTimeString);
      this.timeTextWidth += j;
      this.timeWidth += j;
      return;
      if ((paramMessageObject.messageOwner.fwd_from != null) && (paramMessageObject.messageOwner.fwd_from.post_author != null))
      {
        localObject1 = paramMessageObject.messageOwner.fwd_from.post_author.replace("\n", "");
        break;
      }
      if ((!paramMessageObject.isOutOwner()) && (paramMessageObject.messageOwner.from_id > 0) && (paramMessageObject.messageOwner.post))
      {
        localObject1 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(paramMessageObject.messageOwner.from_id));
        if (localObject1 != null)
        {
          localObject1 = ContactsController.formatName(((TLRPC.User)localObject1).first_name, ((TLRPC.User)localObject1).last_name).replace('\n', ' ');
          break;
        }
        localObject1 = null;
        break;
      }
      localObject1 = null;
      break;
      label618:
      localObject2 = LocaleController.getInstance().formatterDay.format(paramMessageObject.messageOwner.date * 1000L);
      break label183;
      label643:
      this.currentTimeString = ((String)localObject2);
      break label211;
      label651:
      i = j - AndroidUtilities.dp(96.0F);
      break label394;
      label665:
      paramMessageObject = TextUtils.ellipsize((CharSequence)localObject1, Theme.chat_timePaint, i, TextUtils.TruncateAt.END);
    }
  }
  
  private LinkPath obtainNewUrlPath(boolean paramBoolean)
  {
    LinkPath localLinkPath;
    if (!this.urlPathCache.isEmpty())
    {
      localLinkPath = (LinkPath)this.urlPathCache.get(0);
      this.urlPathCache.remove(0);
      if (!paramBoolean) {
        break label57;
      }
      this.urlPathSelection.add(localLinkPath);
    }
    for (;;)
    {
      return localLinkPath;
      localLinkPath = new LinkPath();
      break;
      label57:
      this.urlPath.add(localLinkPath);
    }
  }
  
  private void resetPressedLink(int paramInt)
  {
    if ((this.pressedLink == null) || ((this.pressedLinkType != paramInt) && (paramInt != -1))) {}
    for (;;)
    {
      return;
      resetUrlPaths(false);
      this.pressedLink = null;
      this.pressedLinkType = -1;
      invalidate();
    }
  }
  
  private void resetUrlPaths(boolean paramBoolean)
  {
    if (paramBoolean) {
      if (!this.urlPathSelection.isEmpty()) {}
    }
    for (;;)
    {
      return;
      this.urlPathCache.addAll(this.urlPathSelection);
      this.urlPathSelection.clear();
      continue;
      if (!this.urlPath.isEmpty())
      {
        this.urlPathCache.addAll(this.urlPath);
        this.urlPath.clear();
      }
    }
  }
  
  /* Error */
  private void setMessageObjectInternal(MessageObject paramMessageObject)
  {
    // Byte code:
    //   0: aload_1
    //   1: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4: getfield 2460	org/telegram/tgnet/TLRPC$Message:flags	I
    //   7: sipush 1024
    //   10: iand
    //   11: ifeq +38 -> 49
    //   14: aload_0
    //   15: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   18: getfield 2535	org/telegram/messenger/MessageObject:viewsReloaded	Z
    //   21: ifne +28 -> 49
    //   24: aload_0
    //   25: getfield 342	org/telegram/ui/Cells/ChatMessageCell:currentAccount	I
    //   28: invokestatic 1006	org/telegram/messenger/MessagesController:getInstance	(I)Lorg/telegram/messenger/MessagesController;
    //   31: aload_0
    //   32: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   35: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   38: invokevirtual 2539	org/telegram/messenger/MessagesController:addToViewsQueue	(Lorg/telegram/tgnet/TLRPC$Message;)V
    //   41: aload_0
    //   42: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   45: iconst_1
    //   46: putfield 2535	org/telegram/messenger/MessageObject:viewsReloaded	Z
    //   49: aload_0
    //   50: invokespecial 2393	org/telegram/ui/Cells/ChatMessageCell:updateCurrentUserAndChat	()V
    //   53: aload_0
    //   54: getfield 2278	org/telegram/ui/Cells/ChatMessageCell:isAvatarVisible	Z
    //   57: ifeq +65 -> 122
    //   60: aload_0
    //   61: getfield 2376	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   64: ifnull +2125 -> 2189
    //   67: aload_0
    //   68: getfield 2376	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   71: getfield 2397	org/telegram/tgnet/TLRPC$User:photo	Lorg/telegram/tgnet/TLRPC$UserProfilePhoto;
    //   74: ifnull +2107 -> 2181
    //   77: aload_0
    //   78: aload_0
    //   79: getfield 2376	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   82: getfield 2397	org/telegram/tgnet/TLRPC$User:photo	Lorg/telegram/tgnet/TLRPC$UserProfilePhoto;
    //   85: getfield 2402	org/telegram/tgnet/TLRPC$UserProfilePhoto:photo_small	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   88: putfield 2409	org/telegram/ui/Cells/ChatMessageCell:currentPhoto	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   91: aload_0
    //   92: getfield 382	org/telegram/ui/Cells/ChatMessageCell:avatarDrawable	Lorg/telegram/ui/Components/AvatarDrawable;
    //   95: aload_0
    //   96: getfield 2376	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   99: invokevirtual 2543	org/telegram/ui/Components/AvatarDrawable:setInfo	(Lorg/telegram/tgnet/TLRPC$User;)V
    //   102: aload_0
    //   103: getfield 366	org/telegram/ui/Cells/ChatMessageCell:avatarImage	Lorg/telegram/messenger/ImageReceiver;
    //   106: aload_0
    //   107: getfield 2409	org/telegram/ui/Cells/ChatMessageCell:currentPhoto	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   110: ldc_w 2545
    //   113: aload_0
    //   114: getfield 382	org/telegram/ui/Cells/ChatMessageCell:avatarDrawable	Lorg/telegram/ui/Components/AvatarDrawable;
    //   117: aconst_null
    //   118: iconst_0
    //   119: invokevirtual 2548	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Ljava/lang/String;I)V
    //   122: aload_0
    //   123: aload_1
    //   124: invokespecial 1191	org/telegram/ui/Cells/ChatMessageCell:measureTime	(Lorg/telegram/messenger/MessageObject;)V
    //   127: aload_0
    //   128: iconst_0
    //   129: putfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   132: aconst_null
    //   133: astore_2
    //   134: aconst_null
    //   135: astore_3
    //   136: aload_1
    //   137: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   140: getfield 1064	org/telegram/tgnet/TLRPC$Message:via_bot_id	I
    //   143: ifeq +2124 -> 2267
    //   146: aload_0
    //   147: getfield 342	org/telegram/ui/Cells/ChatMessageCell:currentAccount	I
    //   150: invokestatic 1006	org/telegram/messenger/MessagesController:getInstance	(I)Lorg/telegram/messenger/MessagesController;
    //   153: aload_1
    //   154: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   157: getfield 1064	org/telegram/tgnet/TLRPC$Message:via_bot_id	I
    //   160: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   163: invokevirtual 1019	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   166: astore 4
    //   168: aload_3
    //   169: astore 5
    //   171: aload_2
    //   172: astore 6
    //   174: aload 4
    //   176: ifnull +111 -> 287
    //   179: aload_3
    //   180: astore 5
    //   182: aload_2
    //   183: astore 6
    //   185: aload 4
    //   187: getfield 2549	org/telegram/tgnet/TLRPC$User:username	Ljava/lang/String;
    //   190: ifnull +97 -> 287
    //   193: aload_3
    //   194: astore 5
    //   196: aload_2
    //   197: astore 6
    //   199: aload 4
    //   201: getfield 2549	org/telegram/tgnet/TLRPC$User:username	Ljava/lang/String;
    //   204: invokevirtual 1054	java/lang/String:length	()I
    //   207: ifle +80 -> 287
    //   210: new 1320	java/lang/StringBuilder
    //   213: dup
    //   214: invokespecial 1321	java/lang/StringBuilder:<init>	()V
    //   217: ldc_w 2551
    //   220: invokevirtual 1325	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   223: aload 4
    //   225: getfield 2549	org/telegram/tgnet/TLRPC$User:username	Ljava/lang/String;
    //   228: invokevirtual 1325	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   231: invokevirtual 1333	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   234: astore 6
    //   236: ldc_w 2553
    //   239: iconst_1
    //   240: anewarray 1242	java/lang/Object
    //   243: dup
    //   244: iconst_0
    //   245: aload 6
    //   247: aastore
    //   248: invokestatic 1246	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   251: invokestatic 2557	org/telegram/messenger/AndroidUtilities:replaceTags	(Ljava/lang/String;)Landroid/text/SpannableStringBuilder;
    //   254: astore 5
    //   256: aload_0
    //   257: getstatic 1554	org/telegram/ui/ActionBar/Theme:chat_replyNamePaint	Landroid/text/TextPaint;
    //   260: aload 5
    //   262: iconst_0
    //   263: aload 5
    //   265: invokeinterface 2192 1 0
    //   270: invokevirtual 2500	android/text/TextPaint:measureText	(Ljava/lang/CharSequence;II)F
    //   273: f2d
    //   274: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   277: d2i
    //   278: putfield 2559	org/telegram/ui/Cells/ChatMessageCell:viaWidth	I
    //   281: aload_0
    //   282: aload 4
    //   284: putfield 2561	org/telegram/ui/Cells/ChatMessageCell:currentViaBotUser	Lorg/telegram/tgnet/TLRPC$User;
    //   287: aload_0
    //   288: getfield 2421	org/telegram/ui/Cells/ChatMessageCell:drawName	Z
    //   291: ifeq +2087 -> 2378
    //   294: aload_0
    //   295: getfield 2040	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   298: ifeq +2080 -> 2378
    //   301: aload_0
    //   302: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   305: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   308: ifne +2070 -> 2378
    //   311: iconst_1
    //   312: istore 7
    //   314: aload_1
    //   315: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   318: getfield 971	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$MessageFwdHeader;
    //   321: ifnull +12 -> 333
    //   324: aload_1
    //   325: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   328: bipush 14
    //   330: if_icmpne +2054 -> 2384
    //   333: aload 6
    //   335: ifnull +2049 -> 2384
    //   338: iconst_1
    //   339: istore 8
    //   341: iload 7
    //   343: ifne +8 -> 351
    //   346: iload 8
    //   348: ifeq +2225 -> 2573
    //   351: aload_0
    //   352: iconst_1
    //   353: putfield 2563	org/telegram/ui/Cells/ChatMessageCell:drawNameLayout	Z
    //   356: aload_0
    //   357: aload_0
    //   358: invokespecial 2565	org/telegram/ui/Cells/ChatMessageCell:getMaxNameWidth	()I
    //   361: putfield 2567	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   364: aload_0
    //   365: getfield 2567	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   368: ifge +13 -> 381
    //   371: aload_0
    //   372: ldc_w 2568
    //   375: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   378: putfield 2567	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   381: aload_0
    //   382: getfield 2376	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   385: ifnull +2005 -> 2390
    //   388: aload_0
    //   389: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   392: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   395: ifne +1995 -> 2390
    //   398: aload_0
    //   399: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   402: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   405: bipush 13
    //   407: if_icmpeq +1983 -> 2390
    //   410: aload_0
    //   411: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   414: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   417: iconst_5
    //   418: if_icmpeq +1972 -> 2390
    //   421: aload_0
    //   422: getfield 618	org/telegram/ui/Cells/ChatMessageCell:delegate	Lorg/telegram/ui/Cells/ChatMessageCell$ChatMessageCellDelegate;
    //   425: aload_0
    //   426: getfield 2376	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   429: getfield 2571	org/telegram/tgnet/TLRPC$User:id	I
    //   432: invokeinterface 2574 2 0
    //   437: ifeq +1953 -> 2390
    //   440: ldc_w 2576
    //   443: ldc_w 2577
    //   446: invokestatic 934	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   449: astore_2
    //   450: getstatic 2580	org/telegram/ui/ActionBar/Theme:chat_adminPaint	Landroid/text/TextPaint;
    //   453: aload_2
    //   454: invokevirtual 1178	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   457: f2d
    //   458: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   461: d2i
    //   462: istore 9
    //   464: aload_0
    //   465: aload_0
    //   466: getfield 2567	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   469: iload 9
    //   471: isub
    //   472: putfield 2567	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   475: iload 7
    //   477: ifeq +1952 -> 2429
    //   480: aload_0
    //   481: getfield 2376	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   484: ifnull +1914 -> 2398
    //   487: aload_0
    //   488: aload_0
    //   489: getfield 2376	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   492: invokestatic 2427	org/telegram/messenger/UserObject:getUserName	(Lorg/telegram/tgnet/TLRPC$User;)Ljava/lang/String;
    //   495: putfield 2429	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   498: aload_0
    //   499: getfield 2429	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   502: bipush 10
    //   504: bipush 32
    //   506: invokevirtual 1208	java/lang/String:replace	(CC)Ljava/lang/String;
    //   509: astore_3
    //   510: getstatic 2583	org/telegram/ui/ActionBar/Theme:chat_namePaint	Landroid/text/TextPaint;
    //   513: astore 4
    //   515: aload_0
    //   516: getfield 2567	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   519: istore 10
    //   521: iload 8
    //   523: ifeq +1916 -> 2439
    //   526: aload_0
    //   527: getfield 2559	org/telegram/ui/Cells/ChatMessageCell:viaWidth	I
    //   530: istore 7
    //   532: aload_3
    //   533: aload 4
    //   535: iload 10
    //   537: iload 7
    //   539: isub
    //   540: i2f
    //   541: getstatic 1217	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   544: invokestatic 1221	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   547: astore 4
    //   549: aload 4
    //   551: astore_3
    //   552: iload 8
    //   554: ifeq +207 -> 761
    //   557: aload_0
    //   558: getstatic 2583	org/telegram/ui/ActionBar/Theme:chat_namePaint	Landroid/text/TextPaint;
    //   561: aload 4
    //   563: iconst_0
    //   564: aload 4
    //   566: invokeinterface 2192 1 0
    //   571: invokevirtual 2500	android/text/TextPaint:measureText	(Ljava/lang/CharSequence;II)F
    //   574: f2d
    //   575: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   578: d2i
    //   579: putfield 2585	org/telegram/ui/Cells/ChatMessageCell:viaNameWidth	I
    //   582: aload_0
    //   583: getfield 2585	org/telegram/ui/Cells/ChatMessageCell:viaNameWidth	I
    //   586: ifeq +18 -> 604
    //   589: aload_0
    //   590: aload_0
    //   591: getfield 2585	org/telegram/ui/Cells/ChatMessageCell:viaNameWidth	I
    //   594: ldc_w 1078
    //   597: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   600: iadd
    //   601: putfield 2585	org/telegram/ui/Cells/ChatMessageCell:viaNameWidth	I
    //   604: aload_0
    //   605: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   608: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   611: bipush 13
    //   613: if_icmpeq +14 -> 627
    //   616: aload_0
    //   617: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   620: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   623: iconst_5
    //   624: if_icmpne +1821 -> 2445
    //   627: ldc_w 2587
    //   630: invokestatic 1511	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   633: istore 8
    //   635: aload_0
    //   636: getfield 2429	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   639: invokevirtual 1054	java/lang/String:length	()I
    //   642: ifle +1833 -> 2475
    //   645: new 2172	android/text/SpannableStringBuilder
    //   648: dup
    //   649: ldc_w 2589
    //   652: iconst_2
    //   653: anewarray 1242	java/lang/Object
    //   656: dup
    //   657: iconst_0
    //   658: aload 4
    //   660: aastore
    //   661: dup
    //   662: iconst_1
    //   663: aload 6
    //   665: aastore
    //   666: invokestatic 1246	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   669: invokespecial 2175	android/text/SpannableStringBuilder:<init>	(Ljava/lang/CharSequence;)V
    //   672: astore_3
    //   673: aload_3
    //   674: new 2591	org/telegram/ui/Components/TypefaceSpan
    //   677: dup
    //   678: getstatic 2597	android/graphics/Typeface:DEFAULT	Landroid/graphics/Typeface;
    //   681: iconst_0
    //   682: iload 8
    //   684: invokespecial 2600	org/telegram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;II)V
    //   687: aload 4
    //   689: invokeinterface 2192 1 0
    //   694: iconst_1
    //   695: iadd
    //   696: aload 4
    //   698: invokeinterface 2192 1 0
    //   703: iconst_4
    //   704: iadd
    //   705: bipush 33
    //   707: invokevirtual 2604	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   710: aload_3
    //   711: new 2591	org/telegram/ui/Components/TypefaceSpan
    //   714: dup
    //   715: ldc_w 2606
    //   718: invokestatic 2610	org/telegram/messenger/AndroidUtilities:getTypeface	(Ljava/lang/String;)Landroid/graphics/Typeface;
    //   721: iconst_0
    //   722: iload 8
    //   724: invokespecial 2600	org/telegram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;II)V
    //   727: aload 4
    //   729: invokeinterface 2192 1 0
    //   734: iconst_5
    //   735: iadd
    //   736: aload_3
    //   737: invokevirtual 2611	android/text/SpannableStringBuilder:length	()I
    //   740: bipush 33
    //   742: invokevirtual 2604	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   745: aload_3
    //   746: getstatic 2583	org/telegram/ui/ActionBar/Theme:chat_namePaint	Landroid/text/TextPaint;
    //   749: aload_0
    //   750: getfield 2567	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   753: i2f
    //   754: getstatic 1217	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   757: invokestatic 1221	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   760: astore_3
    //   761: new 350	android/text/StaticLayout
    //   764: astore 4
    //   766: aload 4
    //   768: aload_3
    //   769: getstatic 2583	org/telegram/ui/ActionBar/Theme:chat_namePaint	Landroid/text/TextPaint;
    //   772: aload_0
    //   773: getfield 2567	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   776: fconst_2
    //   777: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   780: iadd
    //   781: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   784: fconst_1
    //   785: fconst_0
    //   786: iconst_0
    //   787: invokespecial 949	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   790: aload_0
    //   791: aload 4
    //   793: putfield 2613	org/telegram/ui/Cells/ChatMessageCell:nameLayout	Landroid/text/StaticLayout;
    //   796: aload_0
    //   797: getfield 2613	org/telegram/ui/Cells/ChatMessageCell:nameLayout	Landroid/text/StaticLayout;
    //   800: ifnull +1749 -> 2549
    //   803: aload_0
    //   804: getfield 2613	org/telegram/ui/Cells/ChatMessageCell:nameLayout	Landroid/text/StaticLayout;
    //   807: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   810: ifle +1739 -> 2549
    //   813: aload_0
    //   814: aload_0
    //   815: getfield 2613	org/telegram/ui/Cells/ChatMessageCell:nameLayout	Landroid/text/StaticLayout;
    //   818: iconst_0
    //   819: invokevirtual 662	android/text/StaticLayout:getLineWidth	(I)F
    //   822: f2d
    //   823: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   826: d2i
    //   827: putfield 2567	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   830: aload_1
    //   831: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   834: bipush 13
    //   836: if_icmpeq +18 -> 854
    //   839: aload_0
    //   840: aload_0
    //   841: getfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   844: ldc_w 1646
    //   847: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   850: iadd
    //   851: putfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   854: aload_0
    //   855: aload_0
    //   856: getfield 2613	org/telegram/ui/Cells/ChatMessageCell:nameLayout	Landroid/text/StaticLayout;
    //   859: iconst_0
    //   860: invokevirtual 659	android/text/StaticLayout:getLineLeft	(I)F
    //   863: putfield 2615	org/telegram/ui/Cells/ChatMessageCell:nameOffsetX	F
    //   866: aload_2
    //   867: ifnull +1698 -> 2565
    //   870: new 350	android/text/StaticLayout
    //   873: astore_3
    //   874: aload_3
    //   875: aload_2
    //   876: getstatic 2580	org/telegram/ui/ActionBar/Theme:chat_adminPaint	Landroid/text/TextPaint;
    //   879: iload 9
    //   881: fconst_2
    //   882: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   885: iadd
    //   886: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   889: fconst_1
    //   890: fconst_0
    //   891: iconst_0
    //   892: invokespecial 949	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   895: aload_0
    //   896: aload_3
    //   897: putfield 2617	org/telegram/ui/Cells/ChatMessageCell:adminLayout	Landroid/text/StaticLayout;
    //   900: aload_0
    //   901: aload_0
    //   902: getfield 2567	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   905: i2f
    //   906: aload_0
    //   907: getfield 2617	org/telegram/ui/Cells/ChatMessageCell:adminLayout	Landroid/text/StaticLayout;
    //   910: iconst_0
    //   911: invokevirtual 662	android/text/StaticLayout:getLineWidth	(I)F
    //   914: ldc_w 1629
    //   917: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   920: i2f
    //   921: fadd
    //   922: fadd
    //   923: f2i
    //   924: putfield 2567	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   927: aload_0
    //   928: getfield 2429	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   931: invokevirtual 1054	java/lang/String:length	()I
    //   934: ifne +8 -> 942
    //   937: aload_0
    //   938: aconst_null
    //   939: putfield 2429	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   942: aload_0
    //   943: aconst_null
    //   944: putfield 2619	org/telegram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/telegram/tgnet/TLRPC$User;
    //   947: aload_0
    //   948: aconst_null
    //   949: putfield 2436	org/telegram/ui/Cells/ChatMessageCell:currentForwardNameString	Ljava/lang/String;
    //   952: aload_0
    //   953: aconst_null
    //   954: putfield 2621	org/telegram/ui/Cells/ChatMessageCell:currentForwardChannel	Lorg/telegram/tgnet/TLRPC$Chat;
    //   957: aload_0
    //   958: getfield 352	org/telegram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   961: iconst_0
    //   962: aconst_null
    //   963: aastore
    //   964: aload_0
    //   965: getfield 352	org/telegram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   968: iconst_1
    //   969: aconst_null
    //   970: aastore
    //   971: aload_0
    //   972: iconst_0
    //   973: putfield 2623	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   976: aload_0
    //   977: getfield 2431	org/telegram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   980: ifeq +634 -> 1614
    //   983: aload_1
    //   984: invokevirtual 2626	org/telegram/messenger/MessageObject:needDrawForwarded	()Z
    //   987: ifeq +627 -> 1614
    //   990: aload_0
    //   991: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   994: ifnull +13 -> 1007
    //   997: aload_0
    //   998: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   1001: getfield 2270	org/telegram/messenger/MessageObject$GroupedMessagePosition:minY	B
    //   1004: ifne +610 -> 1614
    //   1007: aload_1
    //   1008: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1011: getfield 971	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$MessageFwdHeader;
    //   1014: getfield 992	org/telegram/tgnet/TLRPC$MessageFwdHeader:channel_id	I
    //   1017: ifeq +30 -> 1047
    //   1020: aload_0
    //   1021: aload_0
    //   1022: getfield 342	org/telegram/ui/Cells/ChatMessageCell:currentAccount	I
    //   1025: invokestatic 1006	org/telegram/messenger/MessagesController:getInstance	(I)Lorg/telegram/messenger/MessagesController;
    //   1028: aload_1
    //   1029: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1032: getfield 971	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$MessageFwdHeader;
    //   1035: getfield 992	org/telegram/tgnet/TLRPC$MessageFwdHeader:channel_id	I
    //   1038: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1041: invokevirtual 1044	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   1044: putfield 2621	org/telegram/ui/Cells/ChatMessageCell:currentForwardChannel	Lorg/telegram/tgnet/TLRPC$Chat;
    //   1047: aload_1
    //   1048: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1051: getfield 971	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$MessageFwdHeader;
    //   1054: getfield 2627	org/telegram/tgnet/TLRPC$MessageFwdHeader:from_id	I
    //   1057: ifeq +30 -> 1087
    //   1060: aload_0
    //   1061: aload_0
    //   1062: getfield 342	org/telegram/ui/Cells/ChatMessageCell:currentAccount	I
    //   1065: invokestatic 1006	org/telegram/messenger/MessagesController:getInstance	(I)Lorg/telegram/messenger/MessagesController;
    //   1068: aload_1
    //   1069: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1072: getfield 971	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$MessageFwdHeader;
    //   1075: getfield 2627	org/telegram/tgnet/TLRPC$MessageFwdHeader:from_id	I
    //   1078: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1081: invokevirtual 1019	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   1084: putfield 2619	org/telegram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/telegram/tgnet/TLRPC$User;
    //   1087: aload_0
    //   1088: getfield 2619	org/telegram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/telegram/tgnet/TLRPC$User;
    //   1091: ifnonnull +10 -> 1101
    //   1094: aload_0
    //   1095: getfield 2621	org/telegram/ui/Cells/ChatMessageCell:currentForwardChannel	Lorg/telegram/tgnet/TLRPC$Chat;
    //   1098: ifnull +516 -> 1614
    //   1101: aload_0
    //   1102: getfield 2621	org/telegram/ui/Cells/ChatMessageCell:currentForwardChannel	Lorg/telegram/tgnet/TLRPC$Chat;
    //   1105: ifnull +1500 -> 2605
    //   1108: aload_0
    //   1109: getfield 2619	org/telegram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/telegram/tgnet/TLRPC$User;
    //   1112: ifnull +1479 -> 2591
    //   1115: aload_0
    //   1116: ldc_w 2629
    //   1119: iconst_2
    //   1120: anewarray 1242	java/lang/Object
    //   1123: dup
    //   1124: iconst_0
    //   1125: aload_0
    //   1126: getfield 2621	org/telegram/ui/Cells/ChatMessageCell:currentForwardChannel	Lorg/telegram/tgnet/TLRPC$Chat;
    //   1129: getfield 2443	org/telegram/tgnet/TLRPC$Chat:title	Ljava/lang/String;
    //   1132: aastore
    //   1133: dup
    //   1134: iconst_1
    //   1135: aload_0
    //   1136: getfield 2619	org/telegram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/telegram/tgnet/TLRPC$User;
    //   1139: invokestatic 2427	org/telegram/messenger/UserObject:getUserName	(Lorg/telegram/tgnet/TLRPC$User;)Ljava/lang/String;
    //   1142: aastore
    //   1143: invokestatic 1246	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   1146: putfield 2436	org/telegram/ui/Cells/ChatMessageCell:currentForwardNameString	Ljava/lang/String;
    //   1149: aload_0
    //   1150: aload_0
    //   1151: invokespecial 2565	org/telegram/ui/Cells/ChatMessageCell:getMaxNameWidth	()I
    //   1154: putfield 2623	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1157: ldc_w 2631
    //   1160: ldc_w 2632
    //   1163: invokestatic 934	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1166: astore_2
    //   1167: ldc_w 2634
    //   1170: ldc_w 2635
    //   1173: invokestatic 934	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1176: astore 4
    //   1178: aload 4
    //   1180: ldc_w 2637
    //   1183: invokevirtual 2640	java/lang/String:indexOf	(Ljava/lang/String;)I
    //   1186: istore 9
    //   1188: getstatic 2643	org/telegram/ui/ActionBar/Theme:chat_forwardNamePaint	Landroid/text/TextPaint;
    //   1191: new 1320	java/lang/StringBuilder
    //   1194: dup
    //   1195: invokespecial 1321	java/lang/StringBuilder:<init>	()V
    //   1198: aload_2
    //   1199: invokevirtual 1325	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1202: ldc_w 1327
    //   1205: invokevirtual 1325	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1208: invokevirtual 1333	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1211: invokevirtual 1178	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   1214: f2d
    //   1215: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   1218: d2i
    //   1219: istore 8
    //   1221: aload_0
    //   1222: getfield 2436	org/telegram/ui/Cells/ChatMessageCell:currentForwardNameString	Ljava/lang/String;
    //   1225: bipush 10
    //   1227: bipush 32
    //   1229: invokevirtual 1208	java/lang/String:replace	(CC)Ljava/lang/String;
    //   1232: getstatic 1554	org/telegram/ui/ActionBar/Theme:chat_replyNamePaint	Landroid/text/TextPaint;
    //   1235: aload_0
    //   1236: getfield 2623	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1239: iload 8
    //   1241: isub
    //   1242: aload_0
    //   1243: getfield 2559	org/telegram/ui/Cells/ChatMessageCell:viaWidth	I
    //   1246: isub
    //   1247: i2f
    //   1248: getstatic 1217	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   1251: invokestatic 1221	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   1254: astore_3
    //   1255: aload 4
    //   1257: iconst_1
    //   1258: anewarray 1242	java/lang/Object
    //   1261: dup
    //   1262: iconst_0
    //   1263: aload_3
    //   1264: aastore
    //   1265: invokestatic 1246	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   1268: astore_2
    //   1269: aload 5
    //   1271: ifnull +1366 -> 2637
    //   1274: new 2172	android/text/SpannableStringBuilder
    //   1277: dup
    //   1278: ldc_w 2589
    //   1281: iconst_2
    //   1282: anewarray 1242	java/lang/Object
    //   1285: dup
    //   1286: iconst_0
    //   1287: aload_2
    //   1288: aastore
    //   1289: dup
    //   1290: iconst_1
    //   1291: aload 6
    //   1293: aastore
    //   1294: invokestatic 1246	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   1297: invokespecial 2175	android/text/SpannableStringBuilder:<init>	(Ljava/lang/CharSequence;)V
    //   1300: astore 5
    //   1302: aload_0
    //   1303: getstatic 2643	org/telegram/ui/ActionBar/Theme:chat_forwardNamePaint	Landroid/text/TextPaint;
    //   1306: aload_2
    //   1307: invokevirtual 1178	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   1310: f2d
    //   1311: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   1314: d2i
    //   1315: putfield 2585	org/telegram/ui/Cells/ChatMessageCell:viaNameWidth	I
    //   1318: aload 5
    //   1320: new 2591	org/telegram/ui/Components/TypefaceSpan
    //   1323: dup
    //   1324: ldc_w 2606
    //   1327: invokestatic 2610	org/telegram/messenger/AndroidUtilities:getTypeface	(Ljava/lang/String;)Landroid/graphics/Typeface;
    //   1330: invokespecial 2646	org/telegram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;)V
    //   1333: aload 5
    //   1335: invokevirtual 2611	android/text/SpannableStringBuilder:length	()I
    //   1338: aload 6
    //   1340: invokevirtual 1054	java/lang/String:length	()I
    //   1343: isub
    //   1344: iconst_1
    //   1345: isub
    //   1346: aload 5
    //   1348: invokevirtual 2611	android/text/SpannableStringBuilder:length	()I
    //   1351: bipush 33
    //   1353: invokevirtual 2604	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   1356: aload 5
    //   1358: astore 6
    //   1360: iload 9
    //   1362: iflt +34 -> 1396
    //   1365: aload 6
    //   1367: new 2591	org/telegram/ui/Components/TypefaceSpan
    //   1370: dup
    //   1371: ldc_w 2606
    //   1374: invokestatic 2610	org/telegram/messenger/AndroidUtilities:getTypeface	(Ljava/lang/String;)Landroid/graphics/Typeface;
    //   1377: invokespecial 2646	org/telegram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;)V
    //   1380: iload 9
    //   1382: aload_3
    //   1383: invokeinterface 2192 1 0
    //   1388: iload 9
    //   1390: iadd
    //   1391: bipush 33
    //   1393: invokevirtual 2604	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   1396: aload 6
    //   1398: getstatic 2643	org/telegram/ui/ActionBar/Theme:chat_forwardNamePaint	Landroid/text/TextPaint;
    //   1401: aload_0
    //   1402: getfield 2623	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1405: i2f
    //   1406: getstatic 1217	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   1409: invokestatic 1221	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   1412: astore 5
    //   1414: aload_0
    //   1415: getfield 352	org/telegram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   1418: astore_2
    //   1419: new 350	android/text/StaticLayout
    //   1422: astore 6
    //   1424: aload 6
    //   1426: aload 5
    //   1428: getstatic 2643	org/telegram/ui/ActionBar/Theme:chat_forwardNamePaint	Landroid/text/TextPaint;
    //   1431: aload_0
    //   1432: getfield 2623	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1435: fconst_2
    //   1436: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1439: iadd
    //   1440: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1443: fconst_1
    //   1444: fconst_0
    //   1445: iconst_0
    //   1446: invokespecial 949	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   1449: aload_2
    //   1450: iconst_1
    //   1451: aload 6
    //   1453: aastore
    //   1454: ldc_w 2648
    //   1457: ldc_w 2649
    //   1460: invokestatic 934	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1463: invokestatic 2557	org/telegram/messenger/AndroidUtilities:replaceTags	(Ljava/lang/String;)Landroid/text/SpannableStringBuilder;
    //   1466: getstatic 2643	org/telegram/ui/ActionBar/Theme:chat_forwardNamePaint	Landroid/text/TextPaint;
    //   1469: aload_0
    //   1470: getfield 2623	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1473: i2f
    //   1474: getstatic 1217	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   1477: invokestatic 1221	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   1480: astore 5
    //   1482: aload_0
    //   1483: getfield 352	org/telegram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   1486: astore_2
    //   1487: new 350	android/text/StaticLayout
    //   1490: astore 6
    //   1492: aload 6
    //   1494: aload 5
    //   1496: getstatic 2643	org/telegram/ui/ActionBar/Theme:chat_forwardNamePaint	Landroid/text/TextPaint;
    //   1499: aload_0
    //   1500: getfield 2623	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1503: fconst_2
    //   1504: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1507: iadd
    //   1508: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1511: fconst_1
    //   1512: fconst_0
    //   1513: iconst_0
    //   1514: invokespecial 949	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   1517: aload_2
    //   1518: iconst_0
    //   1519: aload 6
    //   1521: aastore
    //   1522: aload_0
    //   1523: aload_0
    //   1524: getfield 352	org/telegram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   1527: iconst_0
    //   1528: aaload
    //   1529: iconst_0
    //   1530: invokevirtual 662	android/text/StaticLayout:getLineWidth	(I)F
    //   1533: f2d
    //   1534: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   1537: d2i
    //   1538: aload_0
    //   1539: getfield 352	org/telegram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   1542: iconst_1
    //   1543: aaload
    //   1544: iconst_0
    //   1545: invokevirtual 662	android/text/StaticLayout:getLineWidth	(I)F
    //   1548: f2d
    //   1549: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   1552: d2i
    //   1553: invokestatic 486	java/lang/Math:max	(II)I
    //   1556: putfield 2623	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1559: aload_0
    //   1560: getfield 354	org/telegram/ui/Cells/ChatMessageCell:forwardNameOffsetX	[F
    //   1563: iconst_0
    //   1564: aload_0
    //   1565: getfield 352	org/telegram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   1568: iconst_0
    //   1569: aaload
    //   1570: iconst_0
    //   1571: invokevirtual 659	android/text/StaticLayout:getLineLeft	(I)F
    //   1574: fastore
    //   1575: aload_0
    //   1576: getfield 354	org/telegram/ui/Cells/ChatMessageCell:forwardNameOffsetX	[F
    //   1579: iconst_1
    //   1580: aload_0
    //   1581: getfield 352	org/telegram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   1584: iconst_1
    //   1585: aaload
    //   1586: iconst_0
    //   1587: invokevirtual 659	android/text/StaticLayout:getLineLeft	(I)F
    //   1590: fastore
    //   1591: aload_1
    //   1592: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   1595: iconst_5
    //   1596: if_icmpeq +18 -> 1614
    //   1599: aload_0
    //   1600: aload_0
    //   1601: getfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1604: ldc_w 544
    //   1607: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1610: iadd
    //   1611: putfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1614: aload_1
    //   1615: invokevirtual 2652	org/telegram/messenger/MessageObject:hasValidReplyMessageObject	()Z
    //   1618: ifeq +558 -> 2176
    //   1621: aload_0
    //   1622: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   1625: ifnull +13 -> 1638
    //   1628: aload_0
    //   1629: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   1632: getfield 2270	org/telegram/messenger/MessageObject$GroupedMessagePosition:minY	B
    //   1635: ifne +541 -> 2176
    //   1638: aload_1
    //   1639: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   1642: bipush 13
    //   1644: if_icmpeq +48 -> 1692
    //   1647: aload_1
    //   1648: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   1651: iconst_5
    //   1652: if_icmpeq +40 -> 1692
    //   1655: aload_0
    //   1656: aload_0
    //   1657: getfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1660: ldc_w 2259
    //   1663: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1666: iadd
    //   1667: putfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1670: aload_1
    //   1671: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   1674: ifeq +18 -> 1692
    //   1677: aload_0
    //   1678: aload_0
    //   1679: getfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1682: ldc_w 1775
    //   1685: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1688: iadd
    //   1689: putfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1692: aload_0
    //   1693: invokespecial 2565	org/telegram/ui/Cells/ChatMessageCell:getMaxNameWidth	()I
    //   1696: istore 8
    //   1698: iload 8
    //   1700: istore 9
    //   1702: aload_1
    //   1703: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   1706: bipush 13
    //   1708: if_icmpeq +26 -> 1734
    //   1711: iload 8
    //   1713: istore 9
    //   1715: aload_1
    //   1716: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   1719: iconst_5
    //   1720: if_icmpeq +14 -> 1734
    //   1723: iload 8
    //   1725: ldc_w 587
    //   1728: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1731: isub
    //   1732: istore 9
    //   1734: aconst_null
    //   1735: astore_2
    //   1736: aload_1
    //   1737: getfield 2407	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1740: getfield 2655	org/telegram/messenger/MessageObject:photoThumbs2	Ljava/util/ArrayList;
    //   1743: bipush 80
    //   1745: invokestatic 1343	org/telegram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;I)Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   1748: astore 5
    //   1750: aload 5
    //   1752: astore 6
    //   1754: aload 5
    //   1756: ifnonnull +17 -> 1773
    //   1759: aload_1
    //   1760: getfield 2407	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1763: getfield 1336	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   1766: bipush 80
    //   1768: invokestatic 1343	org/telegram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;I)Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   1771: astore 6
    //   1773: aload 6
    //   1775: ifnull +40 -> 1815
    //   1778: aload_1
    //   1779: getfield 2407	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1782: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   1785: bipush 13
    //   1787: if_icmpeq +28 -> 1815
    //   1790: aload_1
    //   1791: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   1794: bipush 13
    //   1796: if_icmpne +9 -> 1805
    //   1799: invokestatic 2226	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   1802: ifeq +13 -> 1815
    //   1805: aload_1
    //   1806: getfield 2407	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1809: invokevirtual 1419	org/telegram/messenger/MessageObject:isSecretMedia	()Z
    //   1812: ifeq +860 -> 2672
    //   1815: aload_0
    //   1816: getfield 387	org/telegram/ui/Cells/ChatMessageCell:replyImageReceiver	Lorg/telegram/messenger/ImageReceiver;
    //   1819: aconst_null
    //   1820: checkcast 794	android/graphics/drawable/Drawable
    //   1823: invokevirtual 1368	org/telegram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   1826: aload_0
    //   1827: iconst_0
    //   1828: putfield 2657	org/telegram/ui/Cells/ChatMessageCell:needReplyImage	Z
    //   1831: aconst_null
    //   1832: astore 6
    //   1834: aload_1
    //   1835: getfield 2660	org/telegram/messenger/MessageObject:customReplyName	Ljava/lang/String;
    //   1838: ifnull +914 -> 2752
    //   1841: aload_1
    //   1842: getfield 2660	org/telegram/messenger/MessageObject:customReplyName	Ljava/lang/String;
    //   1845: astore 6
    //   1847: aload 6
    //   1849: astore 5
    //   1851: aload 6
    //   1853: ifnonnull +14 -> 1867
    //   1856: ldc_w 2662
    //   1859: ldc_w 2663
    //   1862: invokestatic 934	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1865: astore 5
    //   1867: aload 5
    //   1869: bipush 10
    //   1871: bipush 32
    //   1873: invokevirtual 1208	java/lang/String:replace	(CC)Ljava/lang/String;
    //   1876: getstatic 1554	org/telegram/ui/ActionBar/Theme:chat_replyNamePaint	Landroid/text/TextPaint;
    //   1879: iload 9
    //   1881: i2f
    //   1882: getstatic 1217	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   1885: invokestatic 1221	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   1888: astore 5
    //   1890: aload_1
    //   1891: getfield 2407	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1894: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1897: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1900: instanceof 1029
    //   1903: ifeq +996 -> 2899
    //   1906: aload_1
    //   1907: getfield 2407	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1910: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1913: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1916: getfield 2667	org/telegram/tgnet/TLRPC$MessageMedia:game	Lorg/telegram/tgnet/TLRPC$TL_game;
    //   1919: getfield 2670	org/telegram/tgnet/TLRPC$TL_game:title	Ljava/lang/String;
    //   1922: getstatic 1593	org/telegram/ui/ActionBar/Theme:chat_replyTextPaint	Landroid/text/TextPaint;
    //   1925: invokevirtual 2674	android/text/TextPaint:getFontMetricsInt	()Landroid/graphics/Paint$FontMetricsInt;
    //   1928: ldc_w 478
    //   1931: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1934: iconst_0
    //   1935: invokestatic 2680	org/telegram/messenger/Emoji:replaceEmoji	(Ljava/lang/CharSequence;Landroid/graphics/Paint$FontMetricsInt;IZ)Ljava/lang/CharSequence;
    //   1938: getstatic 1593	org/telegram/ui/ActionBar/Theme:chat_replyTextPaint	Landroid/text/TextPaint;
    //   1941: iload 9
    //   1943: i2f
    //   1944: getstatic 1217	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   1947: invokestatic 1221	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   1950: astore 6
    //   1952: aload_0
    //   1953: getfield 2657	org/telegram/ui/Cells/ChatMessageCell:needReplyImage	Z
    //   1956: ifeq +1115 -> 3071
    //   1959: bipush 44
    //   1961: istore 8
    //   1963: aload_0
    //   1964: iload 8
    //   1966: iconst_4
    //   1967: iadd
    //   1968: i2f
    //   1969: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1972: putfield 2682	org/telegram/ui/Cells/ChatMessageCell:replyNameWidth	I
    //   1975: aload 5
    //   1977: ifnull +87 -> 2064
    //   1980: new 350	android/text/StaticLayout
    //   1983: astore_1
    //   1984: aload_1
    //   1985: aload 5
    //   1987: getstatic 1554	org/telegram/ui/ActionBar/Theme:chat_replyNamePaint	Landroid/text/TextPaint;
    //   1990: iload 9
    //   1992: ldc_w 1588
    //   1995: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1998: iadd
    //   1999: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   2002: fconst_1
    //   2003: fconst_0
    //   2004: iconst_0
    //   2005: invokespecial 949	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   2008: aload_0
    //   2009: aload_1
    //   2010: putfield 1527	org/telegram/ui/Cells/ChatMessageCell:replyNameLayout	Landroid/text/StaticLayout;
    //   2013: aload_0
    //   2014: getfield 1527	org/telegram/ui/Cells/ChatMessageCell:replyNameLayout	Landroid/text/StaticLayout;
    //   2017: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   2020: ifle +44 -> 2064
    //   2023: aload_0
    //   2024: aload_0
    //   2025: getfield 2682	org/telegram/ui/Cells/ChatMessageCell:replyNameWidth	I
    //   2028: aload_0
    //   2029: getfield 1527	org/telegram/ui/Cells/ChatMessageCell:replyNameLayout	Landroid/text/StaticLayout;
    //   2032: iconst_0
    //   2033: invokevirtual 662	android/text/StaticLayout:getLineWidth	(I)F
    //   2036: f2d
    //   2037: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   2040: d2i
    //   2041: ldc_w 1629
    //   2044: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2047: iadd
    //   2048: iadd
    //   2049: putfield 2682	org/telegram/ui/Cells/ChatMessageCell:replyNameWidth	I
    //   2052: aload_0
    //   2053: aload_0
    //   2054: getfield 1527	org/telegram/ui/Cells/ChatMessageCell:replyNameLayout	Landroid/text/StaticLayout;
    //   2057: iconst_0
    //   2058: invokevirtual 659	android/text/StaticLayout:getLineLeft	(I)F
    //   2061: putfield 2684	org/telegram/ui/Cells/ChatMessageCell:replyNameOffset	F
    //   2064: aload_0
    //   2065: getfield 2657	org/telegram/ui/Cells/ChatMessageCell:needReplyImage	Z
    //   2068: ifeq +1017 -> 3085
    //   2071: bipush 44
    //   2073: istore 8
    //   2075: aload_0
    //   2076: iload 8
    //   2078: iconst_4
    //   2079: iadd
    //   2080: i2f
    //   2081: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2084: putfield 2686	org/telegram/ui/Cells/ChatMessageCell:replyTextWidth	I
    //   2087: aload 6
    //   2089: ifnull +87 -> 2176
    //   2092: new 350	android/text/StaticLayout
    //   2095: astore_1
    //   2096: aload_1
    //   2097: aload 6
    //   2099: getstatic 1593	org/telegram/ui/ActionBar/Theme:chat_replyTextPaint	Landroid/text/TextPaint;
    //   2102: iload 9
    //   2104: ldc_w 1588
    //   2107: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2110: iadd
    //   2111: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   2114: fconst_1
    //   2115: fconst_0
    //   2116: iconst_0
    //   2117: invokespecial 949	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   2120: aload_0
    //   2121: aload_1
    //   2122: putfield 2404	org/telegram/ui/Cells/ChatMessageCell:replyTextLayout	Landroid/text/StaticLayout;
    //   2125: aload_0
    //   2126: getfield 2404	org/telegram/ui/Cells/ChatMessageCell:replyTextLayout	Landroid/text/StaticLayout;
    //   2129: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   2132: ifle +44 -> 2176
    //   2135: aload_0
    //   2136: aload_0
    //   2137: getfield 2686	org/telegram/ui/Cells/ChatMessageCell:replyTextWidth	I
    //   2140: aload_0
    //   2141: getfield 2404	org/telegram/ui/Cells/ChatMessageCell:replyTextLayout	Landroid/text/StaticLayout;
    //   2144: iconst_0
    //   2145: invokevirtual 662	android/text/StaticLayout:getLineWidth	(I)F
    //   2148: f2d
    //   2149: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   2152: d2i
    //   2153: ldc_w 1629
    //   2156: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2159: iadd
    //   2160: iadd
    //   2161: putfield 2686	org/telegram/ui/Cells/ChatMessageCell:replyTextWidth	I
    //   2164: aload_0
    //   2165: aload_0
    //   2166: getfield 2404	org/telegram/ui/Cells/ChatMessageCell:replyTextLayout	Landroid/text/StaticLayout;
    //   2169: iconst_0
    //   2170: invokevirtual 659	android/text/StaticLayout:getLineLeft	(I)F
    //   2173: putfield 2688	org/telegram/ui/Cells/ChatMessageCell:replyTextOffset	F
    //   2176: aload_0
    //   2177: invokevirtual 2691	org/telegram/ui/Cells/ChatMessageCell:requestLayout	()V
    //   2180: return
    //   2181: aload_0
    //   2182: aconst_null
    //   2183: putfield 2409	org/telegram/ui/Cells/ChatMessageCell:currentPhoto	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2186: goto -2095 -> 91
    //   2189: aload_0
    //   2190: getfield 2378	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2193: ifnull +49 -> 2242
    //   2196: aload_0
    //   2197: getfield 2378	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2200: getfield 2439	org/telegram/tgnet/TLRPC$Chat:photo	Lorg/telegram/tgnet/TLRPC$ChatPhoto;
    //   2203: ifnull +31 -> 2234
    //   2206: aload_0
    //   2207: aload_0
    //   2208: getfield 2378	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2211: getfield 2439	org/telegram/tgnet/TLRPC$Chat:photo	Lorg/telegram/tgnet/TLRPC$ChatPhoto;
    //   2214: getfield 2442	org/telegram/tgnet/TLRPC$ChatPhoto:photo_small	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2217: putfield 2409	org/telegram/ui/Cells/ChatMessageCell:currentPhoto	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2220: aload_0
    //   2221: getfield 382	org/telegram/ui/Cells/ChatMessageCell:avatarDrawable	Lorg/telegram/ui/Components/AvatarDrawable;
    //   2224: aload_0
    //   2225: getfield 2378	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2228: invokevirtual 2694	org/telegram/ui/Components/AvatarDrawable:setInfo	(Lorg/telegram/tgnet/TLRPC$Chat;)V
    //   2231: goto -2129 -> 102
    //   2234: aload_0
    //   2235: aconst_null
    //   2236: putfield 2409	org/telegram/ui/Cells/ChatMessageCell:currentPhoto	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2239: goto -19 -> 2220
    //   2242: aload_0
    //   2243: aconst_null
    //   2244: putfield 2409	org/telegram/ui/Cells/ChatMessageCell:currentPhoto	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2247: aload_0
    //   2248: getfield 382	org/telegram/ui/Cells/ChatMessageCell:avatarDrawable	Lorg/telegram/ui/Components/AvatarDrawable;
    //   2251: aload_1
    //   2252: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2255: getfield 1009	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   2258: aconst_null
    //   2259: aconst_null
    //   2260: iconst_0
    //   2261: invokevirtual 2697	org/telegram/ui/Components/AvatarDrawable:setInfo	(ILjava/lang/String;Ljava/lang/String;Z)V
    //   2264: goto -2162 -> 102
    //   2267: aload_3
    //   2268: astore 5
    //   2270: aload_2
    //   2271: astore 6
    //   2273: aload_1
    //   2274: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2277: getfield 2459	org/telegram/tgnet/TLRPC$Message:via_bot_name	Ljava/lang/String;
    //   2280: ifnull -1993 -> 287
    //   2283: aload_3
    //   2284: astore 5
    //   2286: aload_2
    //   2287: astore 6
    //   2289: aload_1
    //   2290: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2293: getfield 2459	org/telegram/tgnet/TLRPC$Message:via_bot_name	Ljava/lang/String;
    //   2296: invokevirtual 1054	java/lang/String:length	()I
    //   2299: ifle -2012 -> 287
    //   2302: new 1320	java/lang/StringBuilder
    //   2305: dup
    //   2306: invokespecial 1321	java/lang/StringBuilder:<init>	()V
    //   2309: ldc_w 2551
    //   2312: invokevirtual 1325	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2315: aload_1
    //   2316: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2319: getfield 2459	org/telegram/tgnet/TLRPC$Message:via_bot_name	Ljava/lang/String;
    //   2322: invokevirtual 1325	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2325: invokevirtual 1333	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2328: astore 6
    //   2330: ldc_w 2553
    //   2333: iconst_1
    //   2334: anewarray 1242	java/lang/Object
    //   2337: dup
    //   2338: iconst_0
    //   2339: aload 6
    //   2341: aastore
    //   2342: invokestatic 1246	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   2345: invokestatic 2557	org/telegram/messenger/AndroidUtilities:replaceTags	(Ljava/lang/String;)Landroid/text/SpannableStringBuilder;
    //   2348: astore 5
    //   2350: aload_0
    //   2351: getstatic 1554	org/telegram/ui/ActionBar/Theme:chat_replyNamePaint	Landroid/text/TextPaint;
    //   2354: aload 5
    //   2356: iconst_0
    //   2357: aload 5
    //   2359: invokeinterface 2192 1 0
    //   2364: invokevirtual 2500	android/text/TextPaint:measureText	(Ljava/lang/CharSequence;II)F
    //   2367: f2d
    //   2368: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   2371: d2i
    //   2372: putfield 2559	org/telegram/ui/Cells/ChatMessageCell:viaWidth	I
    //   2375: goto -2088 -> 287
    //   2378: iconst_0
    //   2379: istore 7
    //   2381: goto -2067 -> 314
    //   2384: iconst_0
    //   2385: istore 8
    //   2387: goto -2046 -> 341
    //   2390: aconst_null
    //   2391: astore_2
    //   2392: iconst_0
    //   2393: istore 9
    //   2395: goto -1920 -> 475
    //   2398: aload_0
    //   2399: getfield 2378	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2402: ifnull +17 -> 2419
    //   2405: aload_0
    //   2406: aload_0
    //   2407: getfield 2378	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2410: getfield 2443	org/telegram/tgnet/TLRPC$Chat:title	Ljava/lang/String;
    //   2413: putfield 2429	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   2416: goto -1918 -> 498
    //   2419: aload_0
    //   2420: ldc_w 2699
    //   2423: putfield 2429	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   2426: goto -1928 -> 498
    //   2429: aload_0
    //   2430: ldc_w 2448
    //   2433: putfield 2429	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   2436: goto -1938 -> 498
    //   2439: iconst_0
    //   2440: istore 7
    //   2442: goto -1910 -> 532
    //   2445: aload_0
    //   2446: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2449: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   2452: ifeq +16 -> 2468
    //   2455: ldc_w 2701
    //   2458: astore_3
    //   2459: aload_3
    //   2460: invokestatic 1511	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   2463: istore 8
    //   2465: goto -1830 -> 635
    //   2468: ldc_w 2703
    //   2471: astore_3
    //   2472: goto -13 -> 2459
    //   2475: new 2172	android/text/SpannableStringBuilder
    //   2478: dup
    //   2479: ldc_w 2705
    //   2482: iconst_1
    //   2483: anewarray 1242	java/lang/Object
    //   2486: dup
    //   2487: iconst_0
    //   2488: aload 6
    //   2490: aastore
    //   2491: invokestatic 1246	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   2494: invokespecial 2175	android/text/SpannableStringBuilder:<init>	(Ljava/lang/CharSequence;)V
    //   2497: astore_3
    //   2498: aload_3
    //   2499: new 2591	org/telegram/ui/Components/TypefaceSpan
    //   2502: dup
    //   2503: getstatic 2597	android/graphics/Typeface:DEFAULT	Landroid/graphics/Typeface;
    //   2506: iconst_0
    //   2507: iload 8
    //   2509: invokespecial 2600	org/telegram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;II)V
    //   2512: iconst_0
    //   2513: iconst_4
    //   2514: bipush 33
    //   2516: invokevirtual 2604	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   2519: aload_3
    //   2520: new 2591	org/telegram/ui/Components/TypefaceSpan
    //   2523: dup
    //   2524: ldc_w 2606
    //   2527: invokestatic 2610	org/telegram/messenger/AndroidUtilities:getTypeface	(Ljava/lang/String;)Landroid/graphics/Typeface;
    //   2530: iconst_0
    //   2531: iload 8
    //   2533: invokespecial 2600	org/telegram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;II)V
    //   2536: iconst_4
    //   2537: aload_3
    //   2538: invokevirtual 2611	android/text/SpannableStringBuilder:length	()I
    //   2541: bipush 33
    //   2543: invokevirtual 2604	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   2546: goto -1801 -> 745
    //   2549: aload_0
    //   2550: iconst_0
    //   2551: putfield 2567	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   2554: goto -1688 -> 866
    //   2557: astore_2
    //   2558: aload_2
    //   2559: invokestatic 714	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   2562: goto -1635 -> 927
    //   2565: aload_0
    //   2566: aconst_null
    //   2567: putfield 2617	org/telegram/ui/Cells/ChatMessageCell:adminLayout	Landroid/text/StaticLayout;
    //   2570: goto -1643 -> 927
    //   2573: aload_0
    //   2574: aconst_null
    //   2575: putfield 2429	org/telegram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   2578: aload_0
    //   2579: aconst_null
    //   2580: putfield 2613	org/telegram/ui/Cells/ChatMessageCell:nameLayout	Landroid/text/StaticLayout;
    //   2583: aload_0
    //   2584: iconst_0
    //   2585: putfield 2567	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   2588: goto -1646 -> 942
    //   2591: aload_0
    //   2592: aload_0
    //   2593: getfield 2621	org/telegram/ui/Cells/ChatMessageCell:currentForwardChannel	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2596: getfield 2443	org/telegram/tgnet/TLRPC$Chat:title	Ljava/lang/String;
    //   2599: putfield 2436	org/telegram/ui/Cells/ChatMessageCell:currentForwardNameString	Ljava/lang/String;
    //   2602: goto -1453 -> 1149
    //   2605: aload_0
    //   2606: getfield 2619	org/telegram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/telegram/tgnet/TLRPC$User;
    //   2609: ifnull -1460 -> 1149
    //   2612: aload_0
    //   2613: aload_0
    //   2614: getfield 2619	org/telegram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/telegram/tgnet/TLRPC$User;
    //   2617: invokestatic 2427	org/telegram/messenger/UserObject:getUserName	(Lorg/telegram/tgnet/TLRPC$User;)Ljava/lang/String;
    //   2620: putfield 2436	org/telegram/ui/Cells/ChatMessageCell:currentForwardNameString	Ljava/lang/String;
    //   2623: goto -1474 -> 1149
    //   2626: astore_2
    //   2627: aload_3
    //   2628: invokeinterface 2706 1 0
    //   2633: astore_2
    //   2634: goto -1365 -> 1269
    //   2637: new 2172	android/text/SpannableStringBuilder
    //   2640: dup
    //   2641: aload 4
    //   2643: iconst_1
    //   2644: anewarray 1242	java/lang/Object
    //   2647: dup
    //   2648: iconst_0
    //   2649: aload_3
    //   2650: aastore
    //   2651: invokestatic 1246	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   2654: invokespecial 2175	android/text/SpannableStringBuilder:<init>	(Ljava/lang/CharSequence;)V
    //   2657: astore 6
    //   2659: goto -1299 -> 1360
    //   2662: astore 6
    //   2664: aload 6
    //   2666: invokestatic 714	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   2669: goto -1055 -> 1614
    //   2672: aload_1
    //   2673: getfield 2407	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2676: invokevirtual 1416	org/telegram/messenger/MessageObject:isRoundVideo	()Z
    //   2679: ifeq +62 -> 2741
    //   2682: aload_0
    //   2683: getfield 387	org/telegram/ui/Cells/ChatMessageCell:replyImageReceiver	Lorg/telegram/messenger/ImageReceiver;
    //   2686: ldc_w 1883
    //   2689: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2692: invokevirtual 377	org/telegram/messenger/ImageReceiver:setRoundRadius	(I)V
    //   2695: aload_0
    //   2696: aload 6
    //   2698: getfield 1292	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2701: putfield 2419	org/telegram/ui/Cells/ChatMessageCell:currentReplyPhoto	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2704: aload_0
    //   2705: getfield 387	org/telegram/ui/Cells/ChatMessageCell:replyImageReceiver	Lorg/telegram/messenger/ImageReceiver;
    //   2708: aload 6
    //   2710: getfield 1292	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   2713: ldc_w 2545
    //   2716: aconst_null
    //   2717: aconst_null
    //   2718: iconst_1
    //   2719: invokevirtual 2548	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Ljava/lang/String;I)V
    //   2722: aload_0
    //   2723: iconst_1
    //   2724: putfield 2657	org/telegram/ui/Cells/ChatMessageCell:needReplyImage	Z
    //   2727: iload 9
    //   2729: ldc_w 1819
    //   2732: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2735: isub
    //   2736: istore 9
    //   2738: goto -907 -> 1831
    //   2741: aload_0
    //   2742: getfield 387	org/telegram/ui/Cells/ChatMessageCell:replyImageReceiver	Lorg/telegram/messenger/ImageReceiver;
    //   2745: iconst_0
    //   2746: invokevirtual 377	org/telegram/messenger/ImageReceiver:setRoundRadius	(I)V
    //   2749: goto -54 -> 2695
    //   2752: aload_1
    //   2753: getfield 2407	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2756: invokevirtual 995	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   2759: ifeq +43 -> 2802
    //   2762: aload_0
    //   2763: getfield 342	org/telegram/ui/Cells/ChatMessageCell:currentAccount	I
    //   2766: invokestatic 1006	org/telegram/messenger/MessagesController:getInstance	(I)Lorg/telegram/messenger/MessagesController;
    //   2769: aload_1
    //   2770: getfield 2407	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2773: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2776: getfield 1009	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   2779: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2782: invokevirtual 1019	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   2785: astore 5
    //   2787: aload 5
    //   2789: ifnull -942 -> 1847
    //   2792: aload 5
    //   2794: invokestatic 2427	org/telegram/messenger/UserObject:getUserName	(Lorg/telegram/tgnet/TLRPC$User;)Ljava/lang/String;
    //   2797: astore 6
    //   2799: goto -952 -> 1847
    //   2802: aload_1
    //   2803: getfield 2407	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2806: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2809: getfield 1009	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   2812: ifge +44 -> 2856
    //   2815: aload_0
    //   2816: getfield 342	org/telegram/ui/Cells/ChatMessageCell:currentAccount	I
    //   2819: invokestatic 1006	org/telegram/messenger/MessagesController:getInstance	(I)Lorg/telegram/messenger/MessagesController;
    //   2822: aload_1
    //   2823: getfield 2407	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2826: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2829: getfield 1009	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   2832: ineg
    //   2833: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2836: invokevirtual 1044	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   2839: astore 5
    //   2841: aload 5
    //   2843: ifnull -996 -> 1847
    //   2846: aload 5
    //   2848: getfield 2443	org/telegram/tgnet/TLRPC$Chat:title	Ljava/lang/String;
    //   2851: astore 6
    //   2853: goto -1006 -> 1847
    //   2856: aload_0
    //   2857: getfield 342	org/telegram/ui/Cells/ChatMessageCell:currentAccount	I
    //   2860: invokestatic 1006	org/telegram/messenger/MessagesController:getInstance	(I)Lorg/telegram/messenger/MessagesController;
    //   2863: aload_1
    //   2864: getfield 2407	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2867: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2870: getfield 1037	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   2873: getfield 1040	org/telegram/tgnet/TLRPC$Peer:channel_id	I
    //   2876: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2879: invokevirtual 1044	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   2882: astore 5
    //   2884: aload 5
    //   2886: ifnull -1039 -> 1847
    //   2889: aload 5
    //   2891: getfield 2443	org/telegram/tgnet/TLRPC$Chat:title	Ljava/lang/String;
    //   2894: astore 6
    //   2896: goto -1049 -> 1847
    //   2899: aload_1
    //   2900: getfield 2407	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2903: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2906: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2909: instanceof 1031
    //   2912: ifeq +49 -> 2961
    //   2915: aload_1
    //   2916: getfield 2407	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2919: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2922: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   2925: getfield 2358	org/telegram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   2928: getstatic 1593	org/telegram/ui/ActionBar/Theme:chat_replyTextPaint	Landroid/text/TextPaint;
    //   2931: invokevirtual 2674	android/text/TextPaint:getFontMetricsInt	()Landroid/graphics/Paint$FontMetricsInt;
    //   2934: ldc_w 478
    //   2937: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2940: iconst_0
    //   2941: invokestatic 2680	org/telegram/messenger/Emoji:replaceEmoji	(Ljava/lang/CharSequence;Landroid/graphics/Paint$FontMetricsInt;IZ)Ljava/lang/CharSequence;
    //   2944: getstatic 1593	org/telegram/ui/ActionBar/Theme:chat_replyTextPaint	Landroid/text/TextPaint;
    //   2947: iload 9
    //   2949: i2f
    //   2950: getstatic 1217	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   2953: invokestatic 1221	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   2956: astore 6
    //   2958: goto -1006 -> 1952
    //   2961: aload_2
    //   2962: astore 6
    //   2964: aload_1
    //   2965: getfield 2407	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2968: getfield 1114	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   2971: ifnull -1019 -> 1952
    //   2974: aload_2
    //   2975: astore 6
    //   2977: aload_1
    //   2978: getfield 2407	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2981: getfield 1114	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   2984: invokeinterface 2192 1 0
    //   2989: ifle -1037 -> 1952
    //   2992: aload_1
    //   2993: getfield 2407	org/telegram/messenger/MessageObject:replyMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2996: getfield 1114	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   2999: invokeinterface 2706 1 0
    //   3004: astore 6
    //   3006: aload 6
    //   3008: astore_1
    //   3009: aload 6
    //   3011: invokevirtual 1054	java/lang/String:length	()I
    //   3014: sipush 150
    //   3017: if_icmple +13 -> 3030
    //   3020: aload 6
    //   3022: iconst_0
    //   3023: sipush 150
    //   3026: invokevirtual 2710	java/lang/String:substring	(II)Ljava/lang/String;
    //   3029: astore_1
    //   3030: aload_1
    //   3031: bipush 10
    //   3033: bipush 32
    //   3035: invokevirtual 1208	java/lang/String:replace	(CC)Ljava/lang/String;
    //   3038: getstatic 1593	org/telegram/ui/ActionBar/Theme:chat_replyTextPaint	Landroid/text/TextPaint;
    //   3041: invokevirtual 2674	android/text/TextPaint:getFontMetricsInt	()Landroid/graphics/Paint$FontMetricsInt;
    //   3044: ldc_w 478
    //   3047: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3050: iconst_0
    //   3051: invokestatic 2680	org/telegram/messenger/Emoji:replaceEmoji	(Ljava/lang/CharSequence;Landroid/graphics/Paint$FontMetricsInt;IZ)Ljava/lang/CharSequence;
    //   3054: getstatic 1593	org/telegram/ui/ActionBar/Theme:chat_replyTextPaint	Landroid/text/TextPaint;
    //   3057: iload 9
    //   3059: i2f
    //   3060: getstatic 1217	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   3063: invokestatic 1221	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   3066: astore 6
    //   3068: goto -1116 -> 1952
    //   3071: iconst_0
    //   3072: istore 8
    //   3074: goto -1111 -> 1963
    //   3077: astore_1
    //   3078: aload_1
    //   3079: invokestatic 714	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   3082: goto -1018 -> 2064
    //   3085: iconst_0
    //   3086: istore 8
    //   3088: goto -1013 -> 2075
    //   3091: astore_1
    //   3092: aload_1
    //   3093: invokestatic 714	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   3096: goto -920 -> 2176
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	3099	0	this	ChatMessageCell
    //   0	3099	1	paramMessageObject	MessageObject
    //   133	2259	2	localObject1	Object
    //   2557	2	2	localException1	Exception
    //   2626	1	2	localException2	Exception
    //   2633	342	2	str	String
    //   135	2515	3	localObject2	Object
    //   166	2476	4	localObject3	Object
    //   169	2721	5	localObject4	Object
    //   172	2486	6	localObject5	Object
    //   2662	47	6	localException3	Exception
    //   2797	270	6	localObject6	Object
    //   312	2129	7	i	int
    //   339	2748	8	j	int
    //   462	2596	9	k	int
    //   519	21	10	m	int
    // Exception table:
    //   from	to	target	type
    //   761	854	2557	java/lang/Exception
    //   854	866	2557	java/lang/Exception
    //   870	927	2557	java/lang/Exception
    //   2549	2554	2557	java/lang/Exception
    //   2565	2570	2557	java/lang/Exception
    //   1255	1269	2626	java/lang/Exception
    //   1414	1449	2662	java/lang/Exception
    //   1454	1517	2662	java/lang/Exception
    //   1522	1614	2662	java/lang/Exception
    //   1952	1959	3077	java/lang/Exception
    //   1963	1975	3077	java/lang/Exception
    //   1980	2064	3077	java/lang/Exception
    //   2064	2071	3091	java/lang/Exception
    //   2075	2087	3091	java/lang/Exception
    //   2092	2176	3091	java/lang/Exception
  }
  
  private void updateCurrentUserAndChat()
  {
    MessagesController localMessagesController = MessagesController.getInstance(this.currentAccount);
    TLRPC.MessageFwdHeader localMessageFwdHeader = this.currentMessageObject.messageOwner.fwd_from;
    int i = UserConfig.getInstance(this.currentAccount).getClientUserId();
    if ((localMessageFwdHeader != null) && (localMessageFwdHeader.channel_id != 0) && (this.currentMessageObject.getDialogId() == i)) {
      this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(localMessageFwdHeader.channel_id));
    }
    for (;;)
    {
      return;
      if ((localMessageFwdHeader != null) && (localMessageFwdHeader.saved_from_peer != null))
      {
        if (localMessageFwdHeader.saved_from_peer.user_id != 0)
        {
          if (localMessageFwdHeader.from_id != 0) {
            this.currentUser = localMessagesController.getUser(Integer.valueOf(localMessageFwdHeader.from_id));
          } else {
            this.currentUser = localMessagesController.getUser(Integer.valueOf(localMessageFwdHeader.saved_from_peer.user_id));
          }
        }
        else if (localMessageFwdHeader.saved_from_peer.channel_id != 0)
        {
          if ((this.currentMessageObject.isSavedFromMegagroup()) && (localMessageFwdHeader.from_id != 0)) {
            this.currentUser = localMessagesController.getUser(Integer.valueOf(localMessageFwdHeader.from_id));
          } else {
            this.currentChat = localMessagesController.getChat(Integer.valueOf(localMessageFwdHeader.saved_from_peer.channel_id));
          }
        }
        else if (localMessageFwdHeader.saved_from_peer.chat_id != 0) {
          if (localMessageFwdHeader.from_id != 0) {
            this.currentUser = localMessagesController.getUser(Integer.valueOf(localMessageFwdHeader.from_id));
          } else {
            this.currentChat = localMessagesController.getChat(Integer.valueOf(localMessageFwdHeader.saved_from_peer.chat_id));
          }
        }
      }
      else if ((localMessageFwdHeader != null) && (localMessageFwdHeader.from_id != 0) && (localMessageFwdHeader.channel_id == 0) && (this.currentMessageObject.getDialogId() == i)) {
        this.currentUser = localMessagesController.getUser(Integer.valueOf(localMessageFwdHeader.from_id));
      } else if (this.currentMessageObject.isFromUser()) {
        this.currentUser = localMessagesController.getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id));
      } else if (this.currentMessageObject.messageOwner.from_id < 0) {
        this.currentChat = localMessagesController.getChat(Integer.valueOf(-this.currentMessageObject.messageOwner.from_id));
      } else if (this.currentMessageObject.messageOwner.post) {
        this.currentChat = localMessagesController.getChat(Integer.valueOf(this.currentMessageObject.messageOwner.to_id.channel_id));
      }
    }
  }
  
  private void updateRadialProgressBackground()
  {
    if (this.drawRadialCheckBackground) {}
    for (;;)
    {
      return;
      this.radialProgress.swapBackground(getDrawableForCurrentState());
      if (this.hasMiniProgress != 0) {
        this.radialProgress.swapMiniBackground(getMiniDrawableForCurrentState());
      }
    }
  }
  
  private void updateSecretTimeText(MessageObject paramMessageObject)
  {
    if ((paramMessageObject == null) || (!paramMessageObject.needDrawBluredPreview())) {}
    for (;;)
    {
      return;
      paramMessageObject = paramMessageObject.getSecretTimeString();
      if (paramMessageObject != null)
      {
        this.infoWidth = ((int)Math.ceil(Theme.chat_infoPaint.measureText(paramMessageObject)));
        this.infoLayout = new StaticLayout(TextUtils.ellipsize(paramMessageObject, Theme.chat_infoPaint, this.infoWidth, TextUtils.TruncateAt.END), Theme.chat_infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        invalidate();
      }
    }
  }
  
  private void updateWaveform()
  {
    if ((this.currentMessageObject == null) || (this.documentAttachType != 3)) {}
    label116:
    for (;;)
    {
      return;
      for (int i = 0;; i++)
      {
        if (i >= this.documentAttach.attributes.size()) {
          break label116;
        }
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
            break;
          }
        }
      }
    }
  }
  
  public void checkRoundVideoPlayback(boolean paramBoolean)
  {
    boolean bool = paramBoolean;
    if (paramBoolean)
    {
      if (MediaController.getInstance().getPlayingMessageObject() == null) {
        bool = true;
      }
    }
    else
    {
      this.photoImage.setAllowStartAnimation(bool);
      if (!bool) {
        break label42;
      }
      this.photoImage.startAnimation();
    }
    for (;;)
    {
      return;
      bool = false;
      break;
      label42:
      this.photoImage.stopAnimation();
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
    if (this.documentAttachType != 3) {}
    for (;;)
    {
      return;
      if (this.buttonState == 2)
      {
        FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, true, 0);
        this.buttonState = 4;
        this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
      }
    }
  }
  
  public void drawCaptionLayout(Canvas paramCanvas, boolean paramBoolean)
  {
    if ((this.captionLayout == null) || ((paramBoolean) && (this.pressedLink == null))) {}
    for (;;)
    {
      return;
      paramCanvas.save();
      paramCanvas.translate(this.captionX, this.captionY);
      if (this.pressedLink != null) {
        for (int i = 0; i < this.urlPath.size(); i++) {
          paramCanvas.drawPath((Path)this.urlPath.get(i), Theme.chat_urlPaint);
        }
      }
      if (!paramBoolean) {}
      try
      {
        this.captionLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
        }
      }
    }
  }
  
  public void drawNamesLayout(Canvas paramCanvas)
  {
    float f1 = 11.0F;
    int i = 0;
    TextPaint localTextPaint;
    String str;
    if ((this.drawNameLayout) && (this.nameLayout != null))
    {
      paramCanvas.save();
      if ((this.currentMessageObject.type != 13) && (this.currentMessageObject.type != 5)) {
        break label571;
      }
      Theme.chat_namePaint.setColor(Theme.getColor("chat_stickerNameText"));
      if (!this.currentMessageObject.isOutOwner()) {
        break label547;
      }
      this.nameX = AndroidUtilities.dp(28.0F);
      this.nameY = (this.layoutHeight - AndroidUtilities.dp(38.0F));
      Theme.chat_systemDrawable.setColorFilter(Theme.colorFilter);
      Theme.chat_systemDrawable.setBounds((int)this.nameX - AndroidUtilities.dp(12.0F), (int)this.nameY - AndroidUtilities.dp(5.0F), (int)this.nameX + AndroidUtilities.dp(12.0F) + this.nameWidth, (int)this.nameY + AndroidUtilities.dp(22.0F));
      Theme.chat_systemDrawable.draw(paramCanvas);
      paramCanvas.translate(this.nameX, this.nameY);
      this.nameLayout.draw(paramCanvas);
      paramCanvas.restore();
      if (this.adminLayout != null)
      {
        localTextPaint = Theme.chat_adminPaint;
        if (!isDrawSelectedBackground()) {
          break label791;
        }
        str = "chat_adminSelectedText";
        label220:
        localTextPaint.setColor(Theme.getColor(str));
        paramCanvas.save();
        paramCanvas.translate(this.backgroundDrawableLeft + this.backgroundDrawableRight - AndroidUtilities.dp(11.0F) - this.adminLayout.getLineWidth(0), this.nameY + AndroidUtilities.dp(0.5F));
        this.adminLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
    }
    label384:
    int k;
    label547:
    label571:
    label632:
    float f2;
    if ((this.drawForwardedName) && (this.forwardedNameLayout[0] != null) && (this.forwardedNameLayout[1] != null) && ((this.currentPosition == null) || ((this.currentPosition.minY == 0) && (this.currentPosition.minX == 0))))
    {
      if (this.currentMessageObject.type == 5)
      {
        Theme.chat_forwardNamePaint.setColor(Theme.getColor("chat_stickerReplyNameText"));
        if (this.currentMessageObject.isOutOwner())
        {
          this.forwardNameX = AndroidUtilities.dp(23.0F);
          this.forwardNameY = AndroidUtilities.dp(12.0F);
          j = this.forwardedNameWidth;
          k = AndroidUtilities.dp(14.0F);
          Theme.chat_systemDrawable.setColorFilter(Theme.colorFilter);
          Theme.chat_systemDrawable.setBounds(this.forwardNameX - AndroidUtilities.dp(7.0F), this.forwardNameY - AndroidUtilities.dp(6.0F), this.forwardNameX - AndroidUtilities.dp(7.0F) + (j + k), this.forwardNameY + AndroidUtilities.dp(38.0F));
          Theme.chat_systemDrawable.draw(paramCanvas);
        }
      }
      for (;;)
      {
        for (j = 0; j < 2; j++)
        {
          paramCanvas.save();
          paramCanvas.translate(this.forwardNameX - this.forwardNameOffsetX[j], this.forwardNameY + AndroidUtilities.dp(16.0F) * j);
          this.forwardedNameLayout[j].draw(paramCanvas);
          paramCanvas.restore();
        }
        this.nameX = (this.backgroundDrawableLeft + this.backgroundDrawableRight + AndroidUtilities.dp(22.0F));
        break;
        if ((this.mediaBackground) || (this.currentMessageObject.isOutOwner()))
        {
          this.nameX = (this.backgroundDrawableLeft + AndroidUtilities.dp(11.0F) - this.nameOffsetX);
          if (this.currentUser == null) {
            break label711;
          }
          Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(this.currentUser.id));
          if (!this.drawPinnedTop) {
            break label783;
          }
        }
        label711:
        label783:
        for (f2 = 9.0F;; f2 = 10.0F)
        {
          this.nameY = AndroidUtilities.dp(f2);
          break;
          j = this.backgroundDrawableLeft;
          if ((!this.mediaBackground) && (this.drawPinnedBottom)) {}
          for (f2 = 11.0F;; f2 = 17.0F)
          {
            this.nameX = (AndroidUtilities.dp(f2) + j - this.nameOffsetX);
            break;
          }
          if (this.currentChat != null)
          {
            if ((ChatObject.isChannel(this.currentChat)) && (!this.currentChat.megagroup))
            {
              Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(5));
              break label632;
            }
            Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(this.currentChat.id));
            break label632;
          }
          Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(0));
          break label632;
        }
        label791:
        str = "chat_adminText";
        break label220;
        this.forwardNameX = (this.backgroundDrawableLeft + this.backgroundDrawableRight + AndroidUtilities.dp(17.0F));
        break label384;
        if (this.drawNameLayout) {}
        for (j = 19;; j = 0)
        {
          this.forwardNameY = AndroidUtilities.dp(j + 10);
          if (!this.currentMessageObject.isOutOwner()) {
            break label892;
          }
          Theme.chat_forwardNamePaint.setColor(Theme.getColor("chat_outForwardedNameText"));
          this.forwardNameX = (this.backgroundDrawableLeft + AndroidUtilities.dp(11.0F));
          break;
        }
        label892:
        Theme.chat_forwardNamePaint.setColor(Theme.getColor("chat_inForwardedNameText"));
        if (!this.mediaBackground) {
          break label929;
        }
        this.forwardNameX = (this.backgroundDrawableLeft + AndroidUtilities.dp(11.0F));
      }
      label929:
      j = this.backgroundDrawableLeft;
      if ((!this.mediaBackground) && (this.drawPinnedBottom)) {}
      for (f2 = f1;; f2 = 17.0F)
      {
        this.forwardNameX = (j + AndroidUtilities.dp(f2));
        break;
      }
    }
    if (this.replyNameLayout != null)
    {
      if ((this.currentMessageObject.type != 13) && (this.currentMessageObject.type != 5)) {
        break label1456;
      }
      Theme.chat_replyLinePaint.setColor(Theme.getColor("chat_stickerReplyLine"));
      Theme.chat_replyNamePaint.setColor(Theme.getColor("chat_stickerReplyNameText"));
      Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_stickerReplyMessageText"));
      if (!this.currentMessageObject.isOutOwner()) {
        break label1433;
      }
      this.replyStartX = AndroidUtilities.dp(23.0F);
      this.replyStartY = AndroidUtilities.dp(12.0F);
      j = Math.max(this.replyNameWidth, this.replyTextWidth);
      k = AndroidUtilities.dp(14.0F);
      Theme.chat_systemDrawable.setColorFilter(Theme.colorFilter);
      Theme.chat_systemDrawable.setBounds(this.replyStartX - AndroidUtilities.dp(7.0F), this.replyStartY - AndroidUtilities.dp(6.0F), this.replyStartX - AndroidUtilities.dp(7.0F) + (j + k), this.replyStartY + AndroidUtilities.dp(41.0F));
      Theme.chat_systemDrawable.draw(paramCanvas);
      if ((this.currentPosition == null) || ((this.currentPosition.minY == 0) && (this.currentPosition.minX == 0)))
      {
        paramCanvas.drawRect(this.replyStartX, this.replyStartY, this.replyStartX + AndroidUtilities.dp(2.0F), this.replyStartY + AndroidUtilities.dp(35.0F), Theme.chat_replyLinePaint);
        if (this.needReplyImage)
        {
          this.replyImageReceiver.setImageCoords(this.replyStartX + AndroidUtilities.dp(10.0F), this.replyStartY, AndroidUtilities.dp(35.0F), AndroidUtilities.dp(35.0F));
          this.replyImageReceiver.draw(paramCanvas);
        }
        if (this.replyNameLayout != null)
        {
          paramCanvas.save();
          f1 = this.replyStartX;
          f2 = this.replyNameOffset;
          if (!this.needReplyImage) {
            break label1893;
          }
        }
      }
    }
    label1433:
    label1456:
    label1578:
    label1598:
    label1833:
    label1881:
    label1887:
    label1893:
    for (int j = 44;; j = 0)
    {
      paramCanvas.translate(AndroidUtilities.dp(j + 10) + (f1 - f2), this.replyStartY);
      this.replyNameLayout.draw(paramCanvas);
      paramCanvas.restore();
      if (this.replyTextLayout != null)
      {
        paramCanvas.save();
        f2 = this.replyStartX;
        f1 = this.replyTextOffset;
        j = i;
        if (this.needReplyImage) {
          j = 44;
        }
        paramCanvas.translate(f2 - f1 + AndroidUtilities.dp(j + 10), this.replyStartY + AndroidUtilities.dp(19.0F));
        this.replyTextLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      return;
      this.replyStartX = (this.backgroundDrawableLeft + this.backgroundDrawableRight + AndroidUtilities.dp(17.0F));
      break;
      if (this.currentMessageObject.isOutOwner())
      {
        Theme.chat_replyLinePaint.setColor(Theme.getColor("chat_outReplyLine"));
        Theme.chat_replyNamePaint.setColor(Theme.getColor("chat_outReplyNameText"));
        if ((this.currentMessageObject.hasValidReplyMessageObject()) && (this.currentMessageObject.replyMessageObject.type == 0) && (!(this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame)) && (!(this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice)))
        {
          Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_outReplyMessageText"));
          this.replyStartX = (this.backgroundDrawableLeft + AndroidUtilities.dp(12.0F));
          if ((!this.drawForwardedName) || (this.forwardedNameLayout[0] == null)) {
            break label1881;
          }
          j = 36;
          if ((!this.drawNameLayout) || (this.nameLayout == null)) {
            break label1887;
          }
        }
      }
      for (k = 20;; k = 0)
      {
        this.replyStartY = AndroidUtilities.dp(k + (j + 12));
        break;
        localTextPaint = Theme.chat_replyTextPaint;
        if (isDrawSelectedBackground()) {}
        for (str = "chat_outReplyMediaMessageSelectedText";; str = "chat_outReplyMediaMessageText")
        {
          localTextPaint.setColor(Theme.getColor(str));
          break;
        }
        Theme.chat_replyLinePaint.setColor(Theme.getColor("chat_inReplyLine"));
        Theme.chat_replyNamePaint.setColor(Theme.getColor("chat_inReplyNameText"));
        if ((this.currentMessageObject.hasValidReplyMessageObject()) && (this.currentMessageObject.replyMessageObject.type == 0) && (!(this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame)) && (!(this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice)))
        {
          Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_inReplyMessageText"));
          if (!this.mediaBackground) {
            break label1833;
          }
          this.replyStartX = (this.backgroundDrawableLeft + AndroidUtilities.dp(12.0F));
          break label1578;
        }
        localTextPaint = Theme.chat_replyTextPaint;
        if (isDrawSelectedBackground()) {}
        for (str = "chat_inReplyMediaMessageSelectedText";; str = "chat_inReplyMediaMessageText")
        {
          localTextPaint.setColor(Theme.getColor(str));
          break;
        }
        j = this.backgroundDrawableLeft;
        if ((!this.mediaBackground) && (this.drawPinnedBottom)) {}
        for (f2 = 12.0F;; f2 = 18.0F)
        {
          this.replyStartX = (AndroidUtilities.dp(f2) + j);
          break;
        }
        j = 0;
        break label1598;
      }
    }
  }
  
  public void drawRoundProgress(Canvas paramCanvas)
  {
    this.rect.set(this.photoImage.getImageX() + AndroidUtilities.dpf2(1.5F), this.photoImage.getImageY() + AndroidUtilities.dpf2(1.5F), this.photoImage.getImageX2() - AndroidUtilities.dpf2(1.5F), this.photoImage.getImageY2() - AndroidUtilities.dpf2(1.5F));
    paramCanvas.drawArc(this.rect, -90.0F, this.currentMessageObject.audioProgress * 360.0F, false, Theme.chat_radialProgressPaint);
  }
  
  public void drawTimeLayout(Canvas paramCanvas)
  {
    if (((this.drawTime) && (!this.groupPhotoInvisible)) || (((this.mediaBackground) && (this.captionLayout == null)) || (this.timeLayout == null))) {
      return;
    }
    label59:
    Object localObject1;
    label117:
    int j;
    int k;
    Object localObject2;
    int m;
    int n;
    if (this.currentMessageObject.type == 5)
    {
      Theme.chat_timePaint.setColor(Theme.getColor("chat_mediaTimeText"));
      if (this.drawPinnedBottom) {
        paramCanvas.translate(0.0F, AndroidUtilities.dp(2.0F));
      }
      if ((!this.mediaBackground) || (this.captionLayout != null)) {
        break label1273;
      }
      if ((this.currentMessageObject.type != 13) && (this.currentMessageObject.type != 5)) {
        break label951;
      }
      localObject1 = Theme.chat_actionBackgroundPaint;
      i = ((Paint)localObject1).getAlpha();
      ((Paint)localObject1).setAlpha((int)(i * this.timeAlpha));
      Theme.chat_timePaint.setAlpha((int)(255.0F * this.timeAlpha));
      j = this.timeX - AndroidUtilities.dp(4.0F);
      k = this.layoutHeight - AndroidUtilities.dp(28.0F);
      localObject2 = this.rect;
      float f1 = j;
      float f2 = k;
      m = this.timeWidth;
      if (!this.currentMessageObject.isOutOwner()) {
        break label958;
      }
      n = 20;
      label211:
      ((RectF)localObject2).set(f1, f2, AndroidUtilities.dp(n + 8) + (j + m), AndroidUtilities.dp(17.0F) + k);
      paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0F), AndroidUtilities.dp(4.0F), (Paint)localObject1);
      ((Paint)localObject1).setAlpha(i);
      i = (int)-this.timeLayout.getLineLeft(0);
      n = i;
      if ((this.currentMessageObject.messageOwner.flags & 0x400) != 0)
      {
        i += (int)(this.timeWidth - this.timeLayout.getLineWidth(0));
        if (!this.currentMessageObject.isSending()) {
          break label964;
        }
        n = i;
        if (!this.currentMessageObject.isOutOwner())
        {
          setDrawableBounds(Theme.chat_msgMediaClockDrawable, this.timeX + AndroidUtilities.dp(11.0F), this.layoutHeight - AndroidUtilities.dp(14.0F) - Theme.chat_msgMediaClockDrawable.getIntrinsicHeight());
          Theme.chat_msgMediaClockDrawable.draw(paramCanvas);
          n = i;
        }
      }
      label391:
      paramCanvas.save();
      paramCanvas.translate(this.timeX + n, this.layoutHeight - AndroidUtilities.dp(12.3F) - this.timeLayout.getHeight());
      this.timeLayout.draw(paramCanvas);
      paramCanvas.restore();
      Theme.chat_timePaint.setAlpha(255);
      label449:
      if (!this.currentMessageObject.isOutOwner()) {
        break label1445;
      }
      n = 0;
      i = 0;
      j = 0;
      k = 0;
      if ((int)(this.currentMessageObject.getDialogId() >> 32) != 1) {
        break label1781;
      }
      m = 1;
      label488:
      if (!this.currentMessageObject.isSending()) {
        break label1787;
      }
      n = 0;
      i = 0;
      j = 1;
      k = 0;
      label509:
      if (j != 0)
      {
        if ((!this.mediaBackground) || (this.captionLayout != null)) {
          break label1903;
        }
        if ((this.currentMessageObject.type != 13) && (this.currentMessageObject.type != 5)) {
          break label1851;
        }
        setDrawableBounds(Theme.chat_msgStickerClockDrawable, this.layoutWidth - AndroidUtilities.dp(22.0F) - Theme.chat_msgStickerClockDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(13.5F) - Theme.chat_msgStickerClockDrawable.getIntrinsicHeight());
        Theme.chat_msgStickerClockDrawable.draw(paramCanvas);
      }
      label600:
      if (m == 0) {
        break label2007;
      }
      if ((n != 0) || (i != 0))
      {
        if ((!this.mediaBackground) || (this.captionLayout != null)) {
          break label1955;
        }
        setDrawableBounds(Theme.chat_msgBroadcastMediaDrawable, this.layoutWidth - AndroidUtilities.dp(24.0F) - Theme.chat_msgBroadcastMediaDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(14.0F) - Theme.chat_msgBroadcastMediaDrawable.getIntrinsicHeight());
        Theme.chat_msgBroadcastMediaDrawable.draw(paramCanvas);
      }
      label677:
      if (k == 0) {
        break label2602;
      }
      if ((!this.mediaBackground) || (this.captionLayout != null)) {
        break label2611;
      }
      n = this.layoutWidth - AndroidUtilities.dp(34.5F);
    }
    for (int i = this.layoutHeight - AndroidUtilities.dp(26.5F);; i = this.layoutHeight - AndroidUtilities.dp(21.0F))
    {
      this.rect.set(n, i, AndroidUtilities.dp(14.0F) + n, AndroidUtilities.dp(14.0F) + i);
      paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(1.0F), AndroidUtilities.dp(1.0F), Theme.chat_msgErrorPaint);
      setDrawableBounds(Theme.chat_msgErrorDrawable, AndroidUtilities.dp(6.0F) + n, AndroidUtilities.dp(2.0F) + i);
      Theme.chat_msgErrorDrawable.draw(paramCanvas);
      break;
      if ((this.mediaBackground) && (this.captionLayout == null))
      {
        if ((this.currentMessageObject.type == 13) || (this.currentMessageObject.type == 5))
        {
          Theme.chat_timePaint.setColor(Theme.getColor("chat_serviceText"));
          break label59;
        }
        Theme.chat_timePaint.setColor(Theme.getColor("chat_mediaTimeText"));
        break label59;
      }
      if (this.currentMessageObject.isOutOwner())
      {
        localObject2 = Theme.chat_timePaint;
        if (isDrawSelectedBackground()) {}
        for (localObject1 = "chat_outTimeSelectedText";; localObject1 = "chat_outTimeText")
        {
          ((TextPaint)localObject2).setColor(Theme.getColor((String)localObject1));
          break;
        }
      }
      localObject2 = Theme.chat_timePaint;
      if (isDrawSelectedBackground()) {}
      for (localObject1 = "chat_inTimeSelectedText";; localObject1 = "chat_inTimeText")
      {
        ((TextPaint)localObject2).setColor(Theme.getColor((String)localObject1));
        break;
      }
      label951:
      localObject1 = Theme.chat_timeBackgroundPaint;
      break label117;
      label958:
      n = 0;
      break label211;
      label964:
      if (this.currentMessageObject.isSendError())
      {
        n = i;
        if (this.currentMessageObject.isOutOwner()) {
          break label391;
        }
        j = this.timeX + AndroidUtilities.dp(11.0F);
        n = this.layoutHeight - AndroidUtilities.dp(27.5F);
        this.rect.set(j, n, AndroidUtilities.dp(14.0F) + j, AndroidUtilities.dp(14.0F) + n);
        paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(1.0F), AndroidUtilities.dp(1.0F), Theme.chat_msgErrorPaint);
        setDrawableBounds(Theme.chat_msgErrorDrawable, AndroidUtilities.dp(6.0F) + j, AndroidUtilities.dp(2.0F) + n);
        Theme.chat_msgErrorDrawable.draw(paramCanvas);
        n = i;
        break label391;
      }
      if ((this.currentMessageObject.type == 13) || (this.currentMessageObject.type == 5)) {}
      for (localObject1 = Theme.chat_msgStickerViewsDrawable;; localObject1 = Theme.chat_msgMediaViewsDrawable)
      {
        n = ((BitmapDrawable)localObject1).getPaint().getAlpha();
        ((Drawable)localObject1).setAlpha((int)(this.timeAlpha * n));
        setDrawableBounds((Drawable)localObject1, this.timeX, this.layoutHeight - AndroidUtilities.dp(10.5F) - this.timeLayout.getHeight());
        ((Drawable)localObject1).draw(paramCanvas);
        ((Drawable)localObject1).setAlpha(n);
        n = i;
        if (this.viewsLayout == null) {
          break;
        }
        paramCanvas.save();
        paramCanvas.translate(this.timeX + ((Drawable)localObject1).getIntrinsicWidth() + AndroidUtilities.dp(3.0F), this.layoutHeight - AndroidUtilities.dp(12.3F) - this.timeLayout.getHeight());
        this.viewsLayout.draw(paramCanvas);
        paramCanvas.restore();
        n = i;
        break;
      }
      label1273:
      i = (int)-this.timeLayout.getLineLeft(0);
      n = i;
      if ((this.currentMessageObject.messageOwner.flags & 0x400) != 0)
      {
        i += (int)(this.timeWidth - this.timeLayout.getLineWidth(0));
        if (!this.currentMessageObject.isSending()) {
          break label1454;
        }
        n = i;
        if (!this.currentMessageObject.isOutOwner())
        {
          if (!isDrawSelectedBackground()) {
            break label1447;
          }
          localObject1 = Theme.chat_msgInSelectedClockDrawable;
          label1356:
          setDrawableBounds((Drawable)localObject1, this.timeX + AndroidUtilities.dp(11.0F), this.layoutHeight - AndroidUtilities.dp(8.5F) - ((Drawable)localObject1).getIntrinsicHeight());
          ((Drawable)localObject1).draw(paramCanvas);
          n = i;
        }
      }
      for (;;)
      {
        paramCanvas.save();
        paramCanvas.translate(this.timeX + n, this.layoutHeight - AndroidUtilities.dp(6.5F) - this.timeLayout.getHeight());
        this.timeLayout.draw(paramCanvas);
        paramCanvas.restore();
        break label449;
        label1445:
        break;
        label1447:
        localObject1 = Theme.chat_msgInClockDrawable;
        break label1356;
        label1454:
        if (!this.currentMessageObject.isSendError()) {
          break label1592;
        }
        n = i;
        if (!this.currentMessageObject.isOutOwner())
        {
          j = this.timeX + AndroidUtilities.dp(11.0F);
          n = this.layoutHeight - AndroidUtilities.dp(20.5F);
          this.rect.set(j, n, AndroidUtilities.dp(14.0F) + j, AndroidUtilities.dp(14.0F) + n);
          paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(1.0F), AndroidUtilities.dp(1.0F), Theme.chat_msgErrorPaint);
          setDrawableBounds(Theme.chat_msgErrorDrawable, AndroidUtilities.dp(6.0F) + j, AndroidUtilities.dp(2.0F) + n);
          Theme.chat_msgErrorDrawable.draw(paramCanvas);
          n = i;
        }
      }
      label1592:
      if (!this.currentMessageObject.isOutOwner())
      {
        if (isDrawSelectedBackground()) {}
        for (localObject1 = Theme.chat_msgInViewsSelectedDrawable;; localObject1 = Theme.chat_msgInViewsDrawable)
        {
          setDrawableBounds((Drawable)localObject1, this.timeX, this.layoutHeight - AndroidUtilities.dp(4.5F) - this.timeLayout.getHeight());
          ((Drawable)localObject1).draw(paramCanvas);
          n = i;
          if (this.viewsLayout == null) {
            break;
          }
          paramCanvas.save();
          paramCanvas.translate(this.timeX + Theme.chat_msgInViewsDrawable.getIntrinsicWidth() + AndroidUtilities.dp(3.0F), this.layoutHeight - AndroidUtilities.dp(6.5F) - this.timeLayout.getHeight());
          this.viewsLayout.draw(paramCanvas);
          paramCanvas.restore();
          n = i;
          break;
        }
      }
      if (isDrawSelectedBackground()) {}
      for (localObject1 = Theme.chat_msgOutViewsSelectedDrawable;; localObject1 = Theme.chat_msgOutViewsDrawable)
      {
        setDrawableBounds((Drawable)localObject1, this.timeX, this.layoutHeight - AndroidUtilities.dp(4.5F) - this.timeLayout.getHeight());
        ((Drawable)localObject1).draw(paramCanvas);
        break;
      }
      label1781:
      m = 0;
      break label488;
      label1787:
      if (this.currentMessageObject.isSendError())
      {
        n = 0;
        i = 0;
        j = 0;
        k = 1;
        break label509;
      }
      if (!this.currentMessageObject.isSent()) {
        break label509;
      }
      if (!this.currentMessageObject.isUnread()) {}
      for (n = 1;; n = 0)
      {
        i = 1;
        j = 0;
        k = 0;
        break;
      }
      label1851:
      setDrawableBounds(Theme.chat_msgMediaClockDrawable, this.layoutWidth - AndroidUtilities.dp(22.0F) - Theme.chat_msgMediaClockDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(13.5F) - Theme.chat_msgMediaClockDrawable.getIntrinsicHeight());
      Theme.chat_msgMediaClockDrawable.draw(paramCanvas);
      break label600;
      label1903:
      setDrawableBounds(Theme.chat_msgOutClockDrawable, this.layoutWidth - AndroidUtilities.dp(18.5F) - Theme.chat_msgOutClockDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(8.5F) - Theme.chat_msgOutClockDrawable.getIntrinsicHeight());
      Theme.chat_msgOutClockDrawable.draw(paramCanvas);
      break label600;
      label1955:
      setDrawableBounds(Theme.chat_msgBroadcastDrawable, this.layoutWidth - AndroidUtilities.dp(20.5F) - Theme.chat_msgBroadcastDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(8.0F) - Theme.chat_msgBroadcastDrawable.getIntrinsicHeight());
      Theme.chat_msgBroadcastDrawable.draw(paramCanvas);
      break label677;
      label2007:
      if (i != 0)
      {
        if ((!this.mediaBackground) || (this.captionLayout != null)) {
          break label2367;
        }
        if ((this.currentMessageObject.type != 13) && (this.currentMessageObject.type != 5)) {
          break label2241;
        }
        if (n == 0) {
          break label2196;
        }
        setDrawableBounds(Theme.chat_msgStickerCheckDrawable, this.layoutWidth - AndroidUtilities.dp(26.3F) - Theme.chat_msgStickerCheckDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(13.5F) - Theme.chat_msgStickerCheckDrawable.getIntrinsicHeight());
      }
      for (;;)
      {
        Theme.chat_msgStickerCheckDrawable.draw(paramCanvas);
        label2102:
        if (n == 0) {
          break label2425;
        }
        if ((!this.mediaBackground) || (this.captionLayout != null)) {
          break label2549;
        }
        if ((this.currentMessageObject.type != 13) && (this.currentMessageObject.type != 5)) {
          break label2473;
        }
        setDrawableBounds(Theme.chat_msgStickerHalfCheckDrawable, this.layoutWidth - AndroidUtilities.dp(21.5F) - Theme.chat_msgStickerHalfCheckDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(13.5F) - Theme.chat_msgStickerHalfCheckDrawable.getIntrinsicHeight());
        Theme.chat_msgStickerHalfCheckDrawable.draw(paramCanvas);
        break;
        label2196:
        setDrawableBounds(Theme.chat_msgStickerCheckDrawable, this.layoutWidth - AndroidUtilities.dp(21.5F) - Theme.chat_msgStickerCheckDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(13.5F) - Theme.chat_msgStickerCheckDrawable.getIntrinsicHeight());
      }
      label2241:
      if (n != 0) {
        setDrawableBounds(Theme.chat_msgMediaCheckDrawable, this.layoutWidth - AndroidUtilities.dp(26.3F) - Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(13.5F) - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight());
      }
      for (;;)
      {
        Theme.chat_msgMediaCheckDrawable.setAlpha((int)(255.0F * this.timeAlpha));
        Theme.chat_msgMediaCheckDrawable.draw(paramCanvas);
        Theme.chat_msgMediaCheckDrawable.setAlpha(255);
        break;
        setDrawableBounds(Theme.chat_msgMediaCheckDrawable, this.layoutWidth - AndroidUtilities.dp(21.5F) - Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(13.5F) - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight());
      }
      label2367:
      if (isDrawSelectedBackground())
      {
        localObject1 = Theme.chat_msgOutCheckSelectedDrawable;
        label2378:
        if (n == 0) {
          break label2434;
        }
        setDrawableBounds((Drawable)localObject1, this.layoutWidth - AndroidUtilities.dp(22.5F) - ((Drawable)localObject1).getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(8.0F) - ((Drawable)localObject1).getIntrinsicHeight());
      }
      for (;;)
      {
        ((Drawable)localObject1).draw(paramCanvas);
        break label2102;
        label2425:
        break;
        localObject1 = Theme.chat_msgOutCheckDrawable;
        break label2378;
        label2434:
        setDrawableBounds((Drawable)localObject1, this.layoutWidth - AndroidUtilities.dp(18.5F) - ((Drawable)localObject1).getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(8.0F) - ((Drawable)localObject1).getIntrinsicHeight());
      }
      label2473:
      setDrawableBounds(Theme.chat_msgMediaHalfCheckDrawable, this.layoutWidth - AndroidUtilities.dp(21.5F) - Theme.chat_msgMediaHalfCheckDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(13.5F) - Theme.chat_msgMediaHalfCheckDrawable.getIntrinsicHeight());
      Theme.chat_msgMediaHalfCheckDrawable.setAlpha((int)(255.0F * this.timeAlpha));
      Theme.chat_msgMediaHalfCheckDrawable.draw(paramCanvas);
      Theme.chat_msgMediaHalfCheckDrawable.setAlpha(255);
      break label677;
      label2549:
      if (isDrawSelectedBackground()) {}
      for (localObject1 = Theme.chat_msgOutHalfCheckSelectedDrawable;; localObject1 = Theme.chat_msgOutHalfCheckDrawable)
      {
        setDrawableBounds((Drawable)localObject1, this.layoutWidth - AndroidUtilities.dp(18.0F) - ((Drawable)localObject1).getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(8.0F) - ((Drawable)localObject1).getIntrinsicHeight());
        ((Drawable)localObject1).draw(paramCanvas);
        break label677;
        label2602:
        break;
      }
      label2611:
      n = this.layoutWidth - AndroidUtilities.dp(32.0F);
    }
  }
  
  public ImageReceiver getAvatarImage()
  {
    if (this.isAvatarVisible) {}
    for (ImageReceiver localImageReceiver = this.avatarImage;; localImageReceiver = null) {
      return localImageReceiver;
    }
  }
  
  public int getBackgroundDrawableLeft()
  {
    int i = 0;
    int j = 0;
    if (this.currentMessageObject.isOutOwner())
    {
      i = this.layoutWidth;
      int k = this.backgroundWidth;
      if (!this.mediaBackground) {}
      for (;;)
      {
        j = i - k - j;
        return j;
        j = AndroidUtilities.dp(9.0F);
      }
    }
    j = i;
    if (this.isChat)
    {
      j = i;
      if (this.isAvatarVisible) {
        j = 48;
      }
    }
    if (!this.mediaBackground) {}
    for (i = 3;; i = 9)
    {
      j = AndroidUtilities.dp(j + i);
      break;
    }
  }
  
  public int getCaptionHeight()
  {
    return this.addedCaptionHeight;
  }
  
  public MessageObject.GroupedMessages getCurrentMessagesGroup()
  {
    return this.currentMessagesGroup;
  }
  
  public MessageObject.GroupedMessagePosition getCurrentPosition()
  {
    return this.currentPosition;
  }
  
  public int getLayoutHeight()
  {
    return this.layoutHeight;
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
  
  public boolean hasCaptionLayout()
  {
    if (this.captionLayout != null) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean hasNameLayout()
  {
    boolean bool = false;
    if (((this.drawNameLayout) && (this.nameLayout != null)) || ((this.drawForwardedName) && (this.forwardedNameLayout[0] != null) && (this.forwardedNameLayout[1] != null) && ((this.currentPosition == null) || ((this.currentPosition.minY == 0) && (this.currentPosition.minX == 0)))) || (this.replyNameLayout != null)) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isInsideBackground(float paramFloat1, float paramFloat2)
  {
    if ((this.currentBackgroundDrawable != null) && (paramFloat1 >= getLeft() + this.backgroundDrawableLeft) && (paramFloat1 <= getLeft() + this.backgroundDrawableLeft + this.backgroundDrawableRight)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isPinnedBottom()
  {
    return this.pinnedBottom;
  }
  
  public boolean isPinnedTop()
  {
    return this.pinnedTop;
  }
  
  public boolean needDelayRoundProgressDraw()
  {
    if ((this.documentAttachType == 7) && (this.currentMessageObject.type != 5) && (MediaController.getInstance().isPlayingMessage(this.currentMessageObject))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    setTranslationX(0.0F);
    this.avatarImage.onAttachedToWindow();
    this.avatarImage.setParentView((View)getParent());
    this.replyImageReceiver.onAttachedToWindow();
    this.locationImageReceiver.onAttachedToWindow();
    if (this.drawPhotoImage) {
      if (this.photoImage.onAttachedToWindow()) {
        updateButtonState(false);
      }
    }
    for (;;)
    {
      if ((this.currentMessageObject != null) && (this.currentMessageObject.isRoundVideo())) {
        checkRoundVideoPlayback(true);
      }
      return;
      updateButtonState(false);
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.avatarImage.onDetachedFromWindow();
    this.replyImageReceiver.onDetachedFromWindow();
    this.locationImageReceiver.onDetachedFromWindow();
    this.photoImage.onDetachedFromWindow();
    DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.currentMessageObject == null) {}
    for (;;)
    {
      return;
      if (this.wasLayout) {
        break;
      }
      requestLayout();
    }
    label92:
    label179:
    label202:
    int j;
    int k;
    int n;
    int i1;
    int i2;
    int i3;
    Object localObject1;
    Object localObject2;
    label265:
    int i4;
    label286:
    label313:
    label565:
    label589:
    Object localObject3;
    float f;
    label713:
    long l1;
    long l2;
    long l3;
    if (this.currentMessageObject.isOutOwner())
    {
      Theme.chat_msgTextPaint.setColor(Theme.getColor("chat_messageTextOut"));
      Theme.chat_msgTextPaint.linkColor = Theme.getColor("chat_messageLinkOut");
      Theme.chat_msgGameTextPaint.setColor(Theme.getColor("chat_messageTextOut"));
      Theme.chat_msgGameTextPaint.linkColor = Theme.getColor("chat_messageLinkOut");
      Theme.chat_replyTextPaint.linkColor = Theme.getColor("chat_messageLinkOut");
      if (this.documentAttach != null)
      {
        if (this.documentAttachType != 3) {
          break label1356;
        }
        if (!this.currentMessageObject.isOutOwner()) {
          break label1291;
        }
        this.seekBarWaveform.setColors(Theme.getColor("chat_outVoiceSeekbar"), Theme.getColor("chat_outVoiceSeekbarFill"), Theme.getColor("chat_outVoiceSeekbarSelected"));
        this.seekBar.setColors(Theme.getColor("chat_outAudioSeekbar"), Theme.getColor("chat_outAudioCacheSeekbar"), Theme.getColor("chat_outAudioSeekbarFill"), Theme.getColor("chat_outAudioSeekbarFill"), Theme.getColor("chat_outAudioSeekbarSelected"));
      }
      if (this.currentMessageObject.type != 5) {
        break label1459;
      }
      Theme.chat_timePaint.setColor(Theme.getColor("chat_mediaTimeText"));
      int i = 0;
      j = 0;
      k = 0;
      int m = 0;
      n = 0;
      i1 = 0;
      i2 = 0;
      i3 = 0;
      if (!this.currentMessageObject.isOutOwner()) {
        break label1681;
      }
      if ((this.mediaBackground) || (this.drawPinnedBottom)) {
        break label1605;
      }
      this.currentBackgroundDrawable = Theme.chat_msgOutDrawable;
      localObject1 = Theme.chat_msgOutSelectedDrawable;
      localObject2 = Theme.chat_msgOutShadowDrawable;
      i1 = this.layoutWidth;
      j = this.backgroundWidth;
      if (this.mediaBackground) {
        break label1625;
      }
      i4 = 0;
      this.backgroundDrawableLeft = (i1 - j - i4);
      i1 = this.backgroundWidth;
      if (!this.mediaBackground) {
        break label1636;
      }
      i4 = 0;
      this.backgroundDrawableRight = (i1 - i4);
      if ((this.currentMessagesGroup != null) && (!this.currentPosition.edge)) {
        this.backgroundDrawableRight += AndroidUtilities.dp(10.0F);
      }
      i1 = this.backgroundDrawableLeft;
      if ((!this.mediaBackground) && (this.drawPinnedBottom)) {
        this.backgroundDrawableRight -= AndroidUtilities.dp(6.0F);
      }
      i4 = n;
      j = i1;
      if (this.currentPosition != null)
      {
        if ((this.currentPosition.flags & 0x2) == 0) {
          this.backgroundDrawableRight += AndroidUtilities.dp(8.0F);
        }
        n = i1;
        if ((this.currentPosition.flags & 0x1) == 0)
        {
          n = i1 - AndroidUtilities.dp(8.0F);
          this.backgroundDrawableRight += AndroidUtilities.dp(8.0F);
        }
        i1 = m;
        if ((this.currentPosition.flags & 0x4) == 0)
        {
          i1 = 0 - AndroidUtilities.dp(9.0F);
          i3 = 0 + AndroidUtilities.dp(9.0F);
        }
        i4 = i3;
        i = i1;
        j = n;
        if ((this.currentPosition.flags & 0x8) == 0)
        {
          i4 = i3 + AndroidUtilities.dp(9.0F);
          j = n;
          i = i1;
        }
      }
      if ((!this.drawPinnedBottom) || (!this.drawPinnedTop)) {
        break label1647;
      }
      n = 0;
      if ((!this.drawPinnedTop) && ((!this.drawPinnedTop) || (!this.drawPinnedBottom))) {
        break label1672;
      }
      i1 = 0;
      i1 = i + i1;
      setDrawableBounds(this.currentBackgroundDrawable, j, i1, this.backgroundDrawableRight, this.layoutHeight - n + i4);
      setDrawableBounds((Drawable)localObject1, j, i1, this.backgroundDrawableRight, this.layoutHeight - n + i4);
      setDrawableBounds((Drawable)localObject2, j, i1, this.backgroundDrawableRight, this.layoutHeight - n + i4);
      localObject3 = localObject2;
      localObject2 = localObject1;
      if ((this.drawBackground) && (this.currentBackgroundDrawable != null))
      {
        if (!this.isHighlightedAnimated) {
          break label2290;
        }
        this.currentBackgroundDrawable.draw(paramCanvas);
        if (this.highlightProgress < 300) {
          break label2276;
        }
        f = 1.0F;
        if (this.currentPosition == null)
        {
          ((Drawable)localObject2).setAlpha((int)(255.0F * f));
          ((Drawable)localObject2).draw(paramCanvas);
        }
        l1 = System.currentTimeMillis();
        l2 = Math.abs(l1 - this.lastHighlightProgressTime);
        l3 = l2;
        if (l2 > 17L) {
          l3 = 17L;
        }
        this.highlightProgress = ((int)(this.highlightProgress - l3));
        this.lastHighlightProgressTime = l1;
        if (this.highlightProgress <= 0)
        {
          this.highlightProgress = 0;
          this.isHighlightedAnimated = false;
        }
        invalidate();
        label813:
        if ((this.currentPosition == null) || (this.currentPosition.flags != 0)) {
          ((Drawable)localObject3).draw(paramCanvas);
        }
      }
      drawContent(paramCanvas);
      if (this.drawShareButton)
      {
        localObject2 = Theme.chat_shareDrawable;
        if (!this.sharePressed) {
          break label2339;
        }
        localObject1 = Theme.colorPressedFilter;
        label865:
        ((Drawable)localObject2).setColorFilter((ColorFilter)localObject1);
        if (!this.currentMessageObject.isOutOwner()) {
          break label2347;
        }
        this.shareStartX = (this.currentBackgroundDrawable.getBounds().left - AndroidUtilities.dp(8.0F) - Theme.chat_shareDrawable.getIntrinsicWidth());
        label910:
        localObject1 = Theme.chat_shareDrawable;
        n = this.shareStartX;
        i4 = this.layoutHeight - AndroidUtilities.dp(41.0F);
        this.shareStartY = i4;
        setDrawableBounds((Drawable)localObject1, n, i4);
        Theme.chat_shareDrawable.draw(paramCanvas);
        if (!this.drwaShareGoIcon) {
          break label2371;
        }
        setDrawableBounds(Theme.chat_goIconDrawable, this.shareStartX + AndroidUtilities.dp(12.0F), this.shareStartY + AndroidUtilities.dp(9.0F));
        Theme.chat_goIconDrawable.draw(paramCanvas);
      }
    }
    for (;;)
    {
      if (this.currentPosition == null) {
        drawNamesLayout(paramCanvas);
      }
      if (((this.drawTime) || (!this.mediaBackground)) && (!this.forceNotDrawTime)) {
        drawTimeLayout(paramCanvas);
      }
      if ((this.controlsAlpha == 1.0F) && (this.timeAlpha == 1.0F)) {
        break;
      }
      l1 = System.currentTimeMillis();
      l2 = Math.abs(this.lastControlsAlphaChangeTime - l1);
      l3 = l2;
      if (l2 > 17L) {
        l3 = 17L;
      }
      this.totalChangeTime += l3;
      if (this.totalChangeTime > 100L) {
        this.totalChangeTime = 100L;
      }
      this.lastControlsAlphaChangeTime = l1;
      if (this.controlsAlpha != 1.0F) {
        this.controlsAlpha = AndroidUtilities.decelerateInterpolator.getInterpolation((float)this.totalChangeTime / 100.0F);
      }
      if (this.timeAlpha != 1.0F) {
        this.timeAlpha = AndroidUtilities.decelerateInterpolator.getInterpolation((float)this.totalChangeTime / 100.0F);
      }
      invalidate();
      if ((!this.forceNotDrawTime) || (this.currentPosition == null) || (!this.currentPosition.last) || (getParent() == null)) {
        break;
      }
      ((View)getParent()).invalidate();
      break;
      Theme.chat_msgTextPaint.setColor(Theme.getColor("chat_messageTextIn"));
      Theme.chat_msgTextPaint.linkColor = Theme.getColor("chat_messageLinkIn");
      Theme.chat_msgGameTextPaint.setColor(Theme.getColor("chat_messageTextIn"));
      Theme.chat_msgGameTextPaint.linkColor = Theme.getColor("chat_messageLinkIn");
      Theme.chat_replyTextPaint.linkColor = Theme.getColor("chat_messageLinkIn");
      break label92;
      label1291:
      this.seekBarWaveform.setColors(Theme.getColor("chat_inVoiceSeekbar"), Theme.getColor("chat_inVoiceSeekbarFill"), Theme.getColor("chat_inVoiceSeekbarSelected"));
      this.seekBar.setColors(Theme.getColor("chat_inAudioSeekbar"), Theme.getColor("chat_inAudioCacheSeekbar"), Theme.getColor("chat_inAudioSeekbarFill"), Theme.getColor("chat_inAudioSeekbarFill"), Theme.getColor("chat_inAudioSeekbarSelected"));
      break label179;
      label1356:
      if (this.documentAttachType != 5) {
        break label179;
      }
      this.documentAttachType = 5;
      if (this.currentMessageObject.isOutOwner())
      {
        this.seekBar.setColors(Theme.getColor("chat_outAudioSeekbar"), Theme.getColor("chat_outAudioCacheSeekbar"), Theme.getColor("chat_outAudioSeekbarFill"), Theme.getColor("chat_outAudioSeekbarFill"), Theme.getColor("chat_outAudioSeekbarSelected"));
        break label179;
      }
      this.seekBar.setColors(Theme.getColor("chat_inAudioSeekbar"), Theme.getColor("chat_inAudioCacheSeekbar"), Theme.getColor("chat_inAudioSeekbarFill"), Theme.getColor("chat_inAudioSeekbarFill"), Theme.getColor("chat_inAudioSeekbarSelected"));
      break label179;
      label1459:
      if (this.mediaBackground)
      {
        if ((this.currentMessageObject.type == 13) || (this.currentMessageObject.type == 5))
        {
          Theme.chat_timePaint.setColor(Theme.getColor("chat_serviceText"));
          break label202;
        }
        Theme.chat_timePaint.setColor(Theme.getColor("chat_mediaTimeText"));
        break label202;
      }
      if (this.currentMessageObject.isOutOwner())
      {
        localObject2 = Theme.chat_timePaint;
        if (isDrawSelectedBackground()) {}
        for (localObject1 = "chat_outTimeSelectedText";; localObject1 = "chat_outTimeText")
        {
          ((TextPaint)localObject2).setColor(Theme.getColor((String)localObject1));
          break;
        }
      }
      localObject2 = Theme.chat_timePaint;
      if (isDrawSelectedBackground()) {}
      for (localObject1 = "chat_inTimeSelectedText";; localObject1 = "chat_inTimeText")
      {
        ((TextPaint)localObject2).setColor(Theme.getColor((String)localObject1));
        break;
      }
      label1605:
      this.currentBackgroundDrawable = Theme.chat_msgOutMediaDrawable;
      localObject1 = Theme.chat_msgOutMediaSelectedDrawable;
      localObject2 = Theme.chat_msgOutMediaShadowDrawable;
      break label265;
      label1625:
      i4 = AndroidUtilities.dp(9.0F);
      break label286;
      label1636:
      i4 = AndroidUtilities.dp(3.0F);
      break label313;
      label1647:
      if (this.drawPinnedBottom)
      {
        n = AndroidUtilities.dp(1.0F);
        break label565;
      }
      n = AndroidUtilities.dp(2.0F);
      break label565;
      label1672:
      i1 = AndroidUtilities.dp(1.0F);
      break label589;
      label1681:
      if ((!this.mediaBackground) && (!this.drawPinnedBottom))
      {
        this.currentBackgroundDrawable = Theme.chat_msgInDrawable;
        localObject2 = Theme.chat_msgInSelectedDrawable;
        localObject1 = Theme.chat_msgInShadowDrawable;
        label1712:
        if ((!this.isChat) || (!this.isAvatarVisible)) {
          break label2218;
        }
        i4 = 48;
        label1730:
        if (this.mediaBackground) {
          break label2224;
        }
        n = 3;
        label1740:
        this.backgroundDrawableLeft = AndroidUtilities.dp(i4 + n);
        n = this.backgroundWidth;
        if (!this.mediaBackground) {
          break label2231;
        }
        i4 = 0;
        label1769:
        this.backgroundDrawableRight = (n - i4);
        if (this.currentMessagesGroup != null)
        {
          if (!this.currentPosition.edge)
          {
            this.backgroundDrawableLeft -= AndroidUtilities.dp(10.0F);
            this.backgroundDrawableRight += AndroidUtilities.dp(10.0F);
          }
          if (this.currentPosition.leftSpanOffset != 0) {
            this.backgroundDrawableLeft += (int)Math.ceil(this.currentPosition.leftSpanOffset / 1000.0F * getGroupPhotosWidth());
          }
        }
        if ((!this.mediaBackground) && (this.drawPinnedBottom))
        {
          this.backgroundDrawableRight -= AndroidUtilities.dp(6.0F);
          this.backgroundDrawableLeft += AndroidUtilities.dp(6.0F);
        }
        i4 = i2;
        i3 = k;
        if (this.currentPosition != null)
        {
          if ((this.currentPosition.flags & 0x2) == 0) {
            this.backgroundDrawableRight += AndroidUtilities.dp(8.0F);
          }
          if ((this.currentPosition.flags & 0x1) == 0)
          {
            this.backgroundDrawableLeft -= AndroidUtilities.dp(8.0F);
            this.backgroundDrawableRight += AndroidUtilities.dp(8.0F);
          }
          n = j;
          if ((this.currentPosition.flags & 0x4) == 0)
          {
            n = 0 - AndroidUtilities.dp(9.0F);
            i1 = 0 + AndroidUtilities.dp(9.0F);
          }
          i4 = i1;
          i3 = n;
          if ((this.currentPosition.flags & 0x8) == 0)
          {
            i4 = i1 + AndroidUtilities.dp(10.0F);
            i3 = n;
          }
        }
        if ((!this.drawPinnedBottom) || (!this.drawPinnedTop)) {
          break label2242;
        }
        n = 0;
        label2083:
        if ((!this.drawPinnedTop) && ((!this.drawPinnedTop) || (!this.drawPinnedBottom))) {
          break label2267;
        }
      }
      label2218:
      label2224:
      label2231:
      label2242:
      label2267:
      for (i1 = 0;; i1 = AndroidUtilities.dp(1.0F))
      {
        i1 = i3 + i1;
        setDrawableBounds(this.currentBackgroundDrawable, this.backgroundDrawableLeft, i1, this.backgroundDrawableRight, this.layoutHeight - n + i4);
        setDrawableBounds((Drawable)localObject2, this.backgroundDrawableLeft, i1, this.backgroundDrawableRight, this.layoutHeight - n + i4);
        setDrawableBounds((Drawable)localObject1, this.backgroundDrawableLeft, i1, this.backgroundDrawableRight, this.layoutHeight - n + i4);
        localObject3 = localObject1;
        break;
        this.currentBackgroundDrawable = Theme.chat_msgInMediaDrawable;
        localObject2 = Theme.chat_msgInMediaSelectedDrawable;
        localObject1 = Theme.chat_msgInMediaShadowDrawable;
        break label1712;
        i4 = 0;
        break label1730;
        n = 9;
        break label1740;
        i4 = AndroidUtilities.dp(3.0F);
        break label1769;
        if (this.drawPinnedBottom)
        {
          n = AndroidUtilities.dp(1.0F);
          break label2083;
        }
        n = AndroidUtilities.dp(2.0F);
        break label2083;
      }
      label2276:
      f = this.highlightProgress / 300.0F;
      break label713;
      label2290:
      if ((isDrawSelectedBackground()) && ((this.currentPosition == null) || (getBackground() != null)))
      {
        ((Drawable)localObject2).setAlpha(255);
        ((Drawable)localObject2).draw(paramCanvas);
        break label813;
      }
      this.currentBackgroundDrawable.draw(paramCanvas);
      break label813;
      label2339:
      localObject1 = Theme.colorFilter;
      break label865;
      label2347:
      this.shareStartX = (this.currentBackgroundDrawable.getBounds().right + AndroidUtilities.dp(8.0F));
      break label910;
      label2371:
      setDrawableBounds(Theme.chat_shareIconDrawable, this.shareStartX + AndroidUtilities.dp(9.0F), this.shareStartY + AndroidUtilities.dp(9.0F));
      Theme.chat_shareIconDrawable.draw(paramCanvas);
    }
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
    if (this.currentMessageObject == null) {
      return;
    }
    label142:
    label155:
    label200:
    label361:
    Object localObject;
    if ((paramBoolean) || (!this.wasLayout))
    {
      this.layoutWidth = getMeasuredWidth();
      this.layoutHeight = (getMeasuredHeight() - this.substractBackgroundHeight);
      if (this.timeTextWidth < 0) {
        this.timeTextWidth = AndroidUtilities.dp(10.0F);
      }
      this.timeLayout = new StaticLayout(this.currentTimeString, Theme.chat_timePaint, this.timeTextWidth + AndroidUtilities.dp(100.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      if (this.mediaBackground) {
        break label609;
      }
      if (this.currentMessageObject.isOutOwner()) {
        break label586;
      }
      paramInt4 = this.backgroundWidth;
      paramInt3 = AndroidUtilities.dp(9.0F);
      paramInt2 = this.timeWidth;
      if (this.isAvatarVisible)
      {
        paramInt1 = AndroidUtilities.dp(48.0F);
        this.timeX = (paramInt1 + (paramInt4 - paramInt3 - paramInt2));
        if ((this.currentMessageObject.messageOwner.flags & 0x400) == 0) {
          break label745;
        }
        this.viewsLayout = new StaticLayout(this.currentViewsString, Theme.chat_timePaint, this.viewsTextWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        if (this.isAvatarVisible) {
          this.avatarImage.setImageCoords(AndroidUtilities.dp(6.0F), this.avatarImage.getImageY(), AndroidUtilities.dp(42.0F), AndroidUtilities.dp(42.0F));
        }
        this.wasLayout = true;
      }
    }
    else
    {
      if (this.currentMessageObject.type == 0) {
        this.textY = (AndroidUtilities.dp(10.0F) + this.namesOffset);
      }
      if (this.currentMessageObject.isRoundVideo()) {
        updatePlayingMessageProgress();
      }
      if (this.documentAttachType != 3) {
        break label846;
      }
      if (!this.currentMessageObject.isOutOwner()) {
        break label753;
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
        break label836;
      }
      paramInt1 = 10;
      label434:
      ((SeekBarWaveform)localObject).setSize(paramInt2 - AndroidUtilities.dp(paramInt1 + 92), AndroidUtilities.dp(30.0F));
      localObject = this.seekBar;
      paramInt2 = this.backgroundWidth;
      if (!this.hasLinkPreview) {
        break label841;
      }
    }
    label586:
    label609:
    label745:
    label753:
    label836:
    label841:
    for (paramInt1 = 10;; paramInt1 = 0)
    {
      ((SeekBar)localObject).setSize(paramInt2 - AndroidUtilities.dp(paramInt1 + 72), AndroidUtilities.dp(30.0F));
      this.seekBarY = (AndroidUtilities.dp(13.0F) + this.namesOffset + this.mediaOffsetY);
      this.buttonY = (AndroidUtilities.dp(13.0F) + this.namesOffset + this.mediaOffsetY);
      this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(44.0F), this.buttonY + AndroidUtilities.dp(44.0F));
      updatePlayingMessageProgress();
      break;
      paramInt1 = 0;
      break label142;
      this.timeX = (this.layoutWidth - this.timeWidth - AndroidUtilities.dp(38.5F));
      break label155;
      if (!this.currentMessageObject.isOutOwner())
      {
        paramInt3 = this.backgroundWidth;
        paramInt4 = AndroidUtilities.dp(4.0F);
        paramInt2 = this.timeWidth;
        if (this.isAvatarVisible) {}
        for (paramInt1 = AndroidUtilities.dp(48.0F);; paramInt1 = 0)
        {
          this.timeX = (paramInt1 + (paramInt3 - paramInt4 - paramInt2));
          if ((this.currentPosition == null) || (this.currentPosition.leftSpanOffset == 0)) {
            break;
          }
          this.timeX += (int)Math.ceil(this.currentPosition.leftSpanOffset / 1000.0F * getGroupPhotosWidth());
          break;
        }
      }
      this.timeX = (this.layoutWidth - this.timeWidth - AndroidUtilities.dp(42.0F));
      break label155;
      this.viewsLayout = null;
      break label200;
      if ((this.isChat) && (this.currentMessageObject.needDrawAvatar()))
      {
        this.seekBarX = AndroidUtilities.dp(114.0F);
        this.buttonX = AndroidUtilities.dp(71.0F);
        this.timeAudioX = AndroidUtilities.dp(124.0F);
        break label361;
      }
      this.seekBarX = AndroidUtilities.dp(66.0F);
      this.buttonX = AndroidUtilities.dp(23.0F);
      this.timeAudioX = AndroidUtilities.dp(76.0F);
      break label361;
      paramInt1 = 0;
      break label434;
    }
    label846:
    if (this.documentAttachType == 5)
    {
      if (this.currentMessageObject.isOutOwner())
      {
        this.seekBarX = (this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(56.0F));
        this.buttonX = (this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(14.0F));
        this.timeAudioX = (this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(67.0F));
        label924:
        if (this.hasLinkPreview)
        {
          this.seekBarX += AndroidUtilities.dp(10.0F);
          this.buttonX += AndroidUtilities.dp(10.0F);
          this.timeAudioX += AndroidUtilities.dp(10.0F);
        }
        localObject = this.seekBar;
        paramInt2 = this.backgroundWidth;
        if (!this.hasLinkPreview) {
          break label1185;
        }
      }
      label1185:
      for (paramInt1 = 10;; paramInt1 = 0)
      {
        ((SeekBar)localObject).setSize(paramInt2 - AndroidUtilities.dp(paramInt1 + 65), AndroidUtilities.dp(30.0F));
        this.seekBarY = (AndroidUtilities.dp(29.0F) + this.namesOffset + this.mediaOffsetY);
        this.buttonY = (AndroidUtilities.dp(13.0F) + this.namesOffset + this.mediaOffsetY);
        this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(44.0F), this.buttonY + AndroidUtilities.dp(44.0F));
        updatePlayingMessageProgress();
        break;
        if ((this.isChat) && (this.currentMessageObject.needDrawAvatar()))
        {
          this.seekBarX = AndroidUtilities.dp(113.0F);
          this.buttonX = AndroidUtilities.dp(71.0F);
          this.timeAudioX = AndroidUtilities.dp(124.0F);
          break label924;
        }
        this.seekBarX = AndroidUtilities.dp(65.0F);
        this.buttonX = AndroidUtilities.dp(23.0F);
        this.timeAudioX = AndroidUtilities.dp(76.0F);
        break label924;
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
        break;
        if ((this.isChat) && (this.currentMessageObject.needDrawAvatar())) {
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
        break;
        if ((this.isChat) && (this.currentMessageObject.needDrawAvatar())) {
          paramInt1 = AndroidUtilities.dp(72.0F);
        } else {
          paramInt1 = AndroidUtilities.dp(23.0F);
        }
      }
    }
    if ((this.currentMessageObject.type == 0) && ((this.hasLinkPreview) || (this.hasGamePreview) || (this.hasInvoicePreview))) {
      if (this.hasGamePreview)
      {
        paramInt1 = this.unmovedTextX - AndroidUtilities.dp(10.0F);
        label1563:
        if (!this.isSmallImage) {
          break label1868;
        }
        paramInt1 = this.backgroundWidth + paramInt1 - AndroidUtilities.dp(81.0F);
      }
    }
    for (;;)
    {
      paramInt2 = paramInt1;
      if (this.currentPosition != null)
      {
        paramInt3 = paramInt1;
        if ((this.currentPosition.flags & 0x1) == 0) {
          paramInt3 = paramInt1 - AndroidUtilities.dp(4.0F);
        }
        paramInt2 = paramInt3;
        if (this.currentPosition.leftSpanOffset != 0) {
          paramInt2 = paramInt3 + (int)Math.ceil(this.currentPosition.leftSpanOffset / 1000.0F * getGroupPhotosWidth());
        }
      }
      this.photoImage.setImageCoords(paramInt2, this.photoImage.getImageY(), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
      this.buttonX = ((int)(paramInt2 + (this.photoImage.getImageWidth() - AndroidUtilities.dp(48.0F)) / 2.0F));
      this.buttonY = (this.photoImage.getImageY() + (this.photoImage.getImageHeight() - AndroidUtilities.dp(48.0F)) / 2);
      this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(48.0F), this.buttonY + AndroidUtilities.dp(48.0F));
      this.deleteProgressRect.set(this.buttonX + AndroidUtilities.dp(3.0F), this.buttonY + AndroidUtilities.dp(3.0F), this.buttonX + AndroidUtilities.dp(45.0F), this.buttonY + AndroidUtilities.dp(45.0F));
      break;
      if (this.hasInvoicePreview)
      {
        paramInt1 = this.unmovedTextX + AndroidUtilities.dp(1.0F);
        break label1563;
      }
      paramInt1 = this.unmovedTextX + AndroidUtilities.dp(1.0F);
      break label1563;
      label1868:
      if (this.hasInvoicePreview) {}
      for (paramInt2 = -AndroidUtilities.dp(6.3F);; paramInt2 = AndroidUtilities.dp(10.0F))
      {
        paramInt1 += paramInt2;
        break;
      }
      if (!this.currentMessageObject.isOutOwner()) {
        break label1957;
      }
      if (this.mediaBackground) {
        paramInt1 = this.layoutWidth - this.backgroundWidth - AndroidUtilities.dp(3.0F);
      } else {
        paramInt1 = this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(6.0F);
      }
    }
    label1957:
    if ((this.isChat) && (this.isAvatarVisible)) {}
    for (paramInt2 = AndroidUtilities.dp(63.0F);; paramInt2 = AndroidUtilities.dp(15.0F))
    {
      paramInt1 = paramInt2;
      if (this.currentPosition == null) {
        break;
      }
      paramInt1 = paramInt2;
      if (this.currentPosition.edge) {
        break;
      }
      paramInt1 = paramInt2 - AndroidUtilities.dp(10.0F);
      break;
    }
  }
  
  protected void onLongPress()
  {
    if ((this.pressedLink instanceof URLSpanMono)) {
      this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, true);
    }
    for (;;)
    {
      return;
      if ((this.pressedLink instanceof URLSpanNoUnderline))
      {
        if (((URLSpanNoUnderline)this.pressedLink).getURL().startsWith("/")) {
          this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, true);
        }
      }
      else if ((this.pressedLink instanceof URLSpan))
      {
        this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, true);
        continue;
      }
      resetPressedLink(-1);
      if ((this.buttonPressed != 0) || (this.miniButtonPressed != 0) || (this.pressedBotButton != -1))
      {
        this.buttonPressed = 0;
        this.miniButtonState = 0;
        this.pressedBotButton = -1;
        invalidate();
      }
      if (this.instantPressed)
      {
        this.instantButtonPressed = false;
        this.instantPressed = false;
        if ((Build.VERSION.SDK_INT >= 21) && (this.instantViewSelectorDrawable != null)) {
          this.instantViewSelectorDrawable.setState(StateSet.NOTHING);
        }
        invalidate();
      }
      if (this.delegate != null) {
        this.delegate.didLongPressed(this);
      }
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if ((this.currentMessageObject != null) && ((this.currentMessageObject.checkLayout()) || ((this.currentPosition != null) && (this.lastHeight != AndroidUtilities.displaySize.y))))
    {
      this.inLayout = true;
      MessageObject localMessageObject = this.currentMessageObject;
      this.currentMessageObject = null;
      setMessageObject(localMessageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
      this.inLayout = false;
    }
    setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), this.totalHeight + this.keyboardHeight);
    this.lastHeight = AndroidUtilities.displaySize.y;
  }
  
  public void onProgressDownload(String paramString, float paramFloat)
  {
    this.radialProgress.setProgress(paramFloat, true);
    if ((this.documentAttachType == 3) || (this.documentAttachType == 5)) {
      if (this.hasMiniProgress != 0) {
        if (this.miniButtonState != 1) {
          updateButtonState(false);
        }
      }
    }
    for (;;)
    {
      return;
      if (this.buttonState != 4)
      {
        updateButtonState(false);
        continue;
        if (this.hasMiniProgress != 0)
        {
          if (this.miniButtonState != 1) {
            updateButtonState(false);
          }
        }
        else if (this.buttonState != 1) {
          updateButtonState(false);
        }
      }
    }
  }
  
  public void onProgressUpload(String paramString, float paramFloat, boolean paramBoolean)
  {
    this.radialProgress.setProgress(paramFloat, true);
    if ((paramFloat == 1.0F) && (this.currentPosition != null) && (SendMessagesHelper.getInstance(this.currentAccount).isSendingMessage(this.currentMessageObject.getId())) && (this.buttonState == 1))
    {
      this.drawRadialCheckBackground = true;
      this.radialProgress.setCheckBackground(false, true);
    }
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
    for (;;)
    {
      return;
      label57:
      if ((this.currentMessageObject.caption != null) && (this.currentMessageObject.caption.length() > 0)) {
        paramViewStructure.setText(this.currentMessageObject.caption);
      }
    }
  }
  
  public void onSeekBarDrag(float paramFloat)
  {
    if (this.currentMessageObject == null) {}
    for (;;)
    {
      return;
      this.currentMessageObject.audioProgress = paramFloat;
      MediaController.getInstance().seekToProgress(this.currentMessageObject, paramFloat);
    }
  }
  
  public void onSuccessDownload(String paramString)
  {
    if ((this.documentAttachType == 3) || (this.documentAttachType == 5))
    {
      updateButtonState(true);
      updateWaveform();
    }
    label238:
    label244:
    for (;;)
    {
      return;
      this.radialProgress.setProgress(1.0F, true);
      if (this.currentMessageObject.type == 0)
      {
        if ((this.documentAttachType == 2) && (this.currentMessageObject.gifState != 1.0F))
        {
          this.buttonState = 2;
          didPressedButton(true);
        }
        else if (!this.photoNotSet)
        {
          updateButtonState(true);
        }
        else
        {
          setMessageObject(this.currentMessageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
        }
      }
      else
      {
        if ((!this.photoNotSet) || (((this.currentMessageObject.type == 8) || (this.currentMessageObject.type == 5)) && (this.currentMessageObject.gifState != 1.0F)))
        {
          if (((this.currentMessageObject.type != 8) && (this.currentMessageObject.type != 5)) || (this.currentMessageObject.gifState == 1.0F)) {
            break label238;
          }
          this.photoNotSet = false;
          this.buttonState = 2;
          didPressedButton(true);
        }
        for (;;)
        {
          if (!this.photoNotSet) {
            break label244;
          }
          setMessageObject(this.currentMessageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
          break;
          updateButtonState(true);
        }
      }
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool1;
    if ((this.currentMessageObject == null) || (!this.delegate.canPerformActions())) {
      bool1 = super.onTouchEvent(paramMotionEvent);
    }
    boolean bool3;
    float f1;
    float f2;
    label667:
    label1685:
    do
    {
      do
      {
        for (;;)
        {
          return bool1;
          this.disallowLongPress = false;
          boolean bool2 = checkTextBlockMotionEvent(paramMotionEvent);
          bool3 = bool2;
          if (!bool2) {
            bool3 = checkOtherButtonMotionEvent(paramMotionEvent);
          }
          bool2 = bool3;
          if (!bool3) {
            bool2 = checkCaptionMotionEvent(paramMotionEvent);
          }
          bool3 = bool2;
          if (!bool2) {
            bool3 = checkAudioMotionEvent(paramMotionEvent);
          }
          bool2 = bool3;
          if (!bool3) {
            bool2 = checkLinkPreviewMotionEvent(paramMotionEvent);
          }
          bool3 = bool2;
          if (!bool2) {
            bool3 = checkGameMotionEvent(paramMotionEvent);
          }
          bool2 = bool3;
          if (!bool3) {
            bool2 = checkPhotoImageMotionEvent(paramMotionEvent);
          }
          bool3 = bool2;
          if (!bool2) {
            bool3 = checkBotButtonMotionEvent(paramMotionEvent);
          }
          if (paramMotionEvent.getAction() == 3)
          {
            this.buttonPressed = 0;
            this.miniButtonPressed = 0;
            this.pressedBotButton = -1;
            this.linkPreviewPressed = false;
            this.otherPressed = false;
            this.imagePressed = false;
            this.gamePreviewPressed = false;
            this.instantButtonPressed = false;
            this.instantPressed = false;
            if ((Build.VERSION.SDK_INT >= 21) && (this.instantViewSelectorDrawable != null)) {
              this.instantViewSelectorDrawable.setState(StateSet.NOTHING);
            }
            bool3 = false;
            resetPressedLink(-1);
          }
          if ((!this.disallowLongPress) && (bool3) && (paramMotionEvent.getAction() == 0)) {
            startCheckLongPress();
          }
          if ((paramMotionEvent.getAction() != 0) && (paramMotionEvent.getAction() != 2)) {
            cancelCheckLongPress();
          }
          bool1 = bool3;
          if (!bool3)
          {
            f1 = paramMotionEvent.getX();
            f2 = paramMotionEvent.getY();
            if (paramMotionEvent.getAction() == 0)
            {
              if (this.delegate != null)
              {
                bool1 = bool3;
                if (!this.delegate.canPerformActions()) {}
              }
              else
              {
                if ((this.isAvatarVisible) && (this.avatarImage.isInsideImage(f1, getTop() + f2)))
                {
                  this.avatarPressed = true;
                  bool2 = true;
                }
                do
                {
                  for (;;)
                  {
                    bool1 = bool2;
                    if (!bool2) {
                      break;
                    }
                    startCheckLongPress();
                    bool1 = bool2;
                    break;
                    if ((this.drawForwardedName) && (this.forwardedNameLayout[0] != null) && (f1 >= this.forwardNameX) && (f1 <= this.forwardNameX + this.forwardedNameWidth) && (f2 >= this.forwardNameY) && (f2 <= this.forwardNameY + AndroidUtilities.dp(32.0F)))
                    {
                      if ((this.viaWidth != 0) && (f1 >= this.forwardNameX + this.viaNameWidth + AndroidUtilities.dp(4.0F))) {
                        this.forwardBotPressed = true;
                      }
                      for (;;)
                      {
                        bool2 = true;
                        break;
                        this.forwardNamePressed = true;
                      }
                    }
                    if ((this.drawNameLayout) && (this.nameLayout != null) && (this.viaWidth != 0) && (f1 >= this.nameX + this.viaNameWidth) && (f1 <= this.nameX + this.viaNameWidth + this.viaWidth) && (f2 >= this.nameY - AndroidUtilities.dp(4.0F)) && (f2 <= this.nameY + AndroidUtilities.dp(20.0F)))
                    {
                      this.forwardBotPressed = true;
                      bool2 = true;
                    }
                    else
                    {
                      if ((!this.drawShareButton) || (f1 < this.shareStartX) || (f1 > this.shareStartX + AndroidUtilities.dp(40.0F)) || (f2 < this.shareStartY) || (f2 > this.shareStartY + AndroidUtilities.dp(32.0F))) {
                        break label667;
                      }
                      this.sharePressed = true;
                      bool2 = true;
                      invalidate();
                    }
                  }
                  bool2 = bool3;
                } while (this.replyNameLayout == null);
                if ((this.currentMessageObject.type == 13) || (this.currentMessageObject.type == 5)) {}
                for (i = this.replyStartX + Math.max(this.replyNameWidth, this.replyTextWidth);; i = this.replyStartX + this.backgroundDrawableRight)
                {
                  bool2 = bool3;
                  if (f1 < this.replyStartX) {
                    break;
                  }
                  bool2 = bool3;
                  if (f1 > i) {
                    break;
                  }
                  bool2 = bool3;
                  if (f2 < this.replyStartY) {
                    break;
                  }
                  bool2 = bool3;
                  if (f2 > this.replyStartY + AndroidUtilities.dp(35.0F)) {
                    break;
                  }
                  this.replyPressed = true;
                  bool2 = true;
                  break;
                }
              }
            }
            else
            {
              if (paramMotionEvent.getAction() != 2) {
                cancelCheckLongPress();
              }
              if (this.avatarPressed)
              {
                if (paramMotionEvent.getAction() == 1)
                {
                  this.avatarPressed = false;
                  playSoundEffect(0);
                  bool1 = bool3;
                  if (this.delegate != null) {
                    if (this.currentUser != null)
                    {
                      this.delegate.didPressedUserAvatar(this, this.currentUser);
                      bool1 = bool3;
                    }
                    else
                    {
                      bool1 = bool3;
                      if (this.currentChat != null)
                      {
                        this.delegate.didPressedChannelAvatar(this, this.currentChat, 0);
                        bool1 = bool3;
                      }
                    }
                  }
                }
                else if (paramMotionEvent.getAction() == 3)
                {
                  this.avatarPressed = false;
                  bool1 = bool3;
                }
                else
                {
                  bool1 = bool3;
                  if (paramMotionEvent.getAction() == 2)
                  {
                    bool1 = bool3;
                    if (this.isAvatarVisible)
                    {
                      bool1 = bool3;
                      if (!this.avatarImage.isInsideImage(f1, getTop() + f2))
                      {
                        this.avatarPressed = false;
                        bool1 = bool3;
                      }
                    }
                  }
                }
              }
              else if (this.forwardNamePressed)
              {
                if (paramMotionEvent.getAction() == 1)
                {
                  this.forwardNamePressed = false;
                  playSoundEffect(0);
                  bool1 = bool3;
                  if (this.delegate != null) {
                    if (this.currentForwardChannel != null)
                    {
                      this.delegate.didPressedChannelAvatar(this, this.currentForwardChannel, this.currentMessageObject.messageOwner.fwd_from.channel_post);
                      bool1 = bool3;
                    }
                    else
                    {
                      bool1 = bool3;
                      if (this.currentForwardUser != null)
                      {
                        this.delegate.didPressedUserAvatar(this, this.currentForwardUser);
                        bool1 = bool3;
                      }
                    }
                  }
                }
                else if (paramMotionEvent.getAction() == 3)
                {
                  this.forwardNamePressed = false;
                  bool1 = bool3;
                }
                else
                {
                  bool1 = bool3;
                  if (paramMotionEvent.getAction() == 2) {
                    if ((f1 >= this.forwardNameX) && (f1 <= this.forwardNameX + this.forwardedNameWidth) && (f2 >= this.forwardNameY))
                    {
                      bool1 = bool3;
                      if (f2 <= this.forwardNameY + AndroidUtilities.dp(32.0F)) {}
                    }
                    else
                    {
                      this.forwardNamePressed = false;
                      bool1 = bool3;
                    }
                  }
                }
              }
              else if (this.forwardBotPressed)
              {
                if (paramMotionEvent.getAction() == 1)
                {
                  this.forwardBotPressed = false;
                  playSoundEffect(0);
                  bool1 = bool3;
                  if (this.delegate != null)
                  {
                    ChatMessageCellDelegate localChatMessageCellDelegate = this.delegate;
                    if (this.currentViaBotUser != null) {}
                    for (paramMotionEvent = this.currentViaBotUser.username;; paramMotionEvent = this.currentMessageObject.messageOwner.via_bot_name)
                    {
                      localChatMessageCellDelegate.didPressedViaBot(this, paramMotionEvent);
                      bool1 = bool3;
                      break;
                    }
                  }
                }
                else if (paramMotionEvent.getAction() == 3)
                {
                  this.forwardBotPressed = false;
                  bool1 = bool3;
                }
                else
                {
                  bool1 = bool3;
                  if (paramMotionEvent.getAction() == 2) {
                    if ((this.drawForwardedName) && (this.forwardedNameLayout[0] != null))
                    {
                      if ((f1 >= this.forwardNameX) && (f1 <= this.forwardNameX + this.forwardedNameWidth) && (f2 >= this.forwardNameY))
                      {
                        bool1 = bool3;
                        if (f2 <= this.forwardNameY + AndroidUtilities.dp(32.0F)) {}
                      }
                      else
                      {
                        this.forwardBotPressed = false;
                        bool1 = bool3;
                      }
                    }
                    else if ((f1 >= this.nameX + this.viaNameWidth) && (f1 <= this.nameX + this.viaNameWidth + this.viaWidth) && (f2 >= this.nameY - AndroidUtilities.dp(4.0F)))
                    {
                      bool1 = bool3;
                      if (f2 <= this.nameY + AndroidUtilities.dp(20.0F)) {}
                    }
                    else
                    {
                      this.forwardBotPressed = false;
                      bool1 = bool3;
                    }
                  }
                }
              }
              else
              {
                if (!this.replyPressed) {
                  break label1685;
                }
                if (paramMotionEvent.getAction() == 1)
                {
                  this.replyPressed = false;
                  playSoundEffect(0);
                  bool1 = bool3;
                  if (this.delegate != null)
                  {
                    this.delegate.didPressedReplyMessage(this, this.currentMessageObject.messageOwner.reply_to_msg_id);
                    bool1 = bool3;
                  }
                }
                else
                {
                  if (paramMotionEvent.getAction() != 3) {
                    break;
                  }
                  this.replyPressed = false;
                  bool1 = bool3;
                }
              }
            }
          }
        }
        bool1 = bool3;
      } while (paramMotionEvent.getAction() != 2);
      if ((this.currentMessageObject.type == 13) || (this.currentMessageObject.type == 5)) {}
      for (int i = this.replyStartX + Math.max(this.replyNameWidth, this.replyTextWidth);; i = this.replyStartX + this.backgroundDrawableRight)
      {
        if ((f1 >= this.replyStartX) && (f1 <= i) && (f2 >= this.replyStartY))
        {
          bool1 = bool3;
          if (f2 <= this.replyStartY + AndroidUtilities.dp(35.0F)) {
            break;
          }
        }
        this.replyPressed = false;
        bool1 = bool3;
        break;
      }
      bool1 = bool3;
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
      bool1 = bool3;
      break;
      if (paramMotionEvent.getAction() == 3) {
        this.sharePressed = false;
      } else if ((paramMotionEvent.getAction() == 2) && ((f1 < this.shareStartX) || (f1 > this.shareStartX + AndroidUtilities.dp(40.0F)) || (f2 < this.shareStartY) || (f2 > this.shareStartY + AndroidUtilities.dp(32.0F)))) {
        this.sharePressed = false;
      }
    }
  }
  
  public void requestLayout()
  {
    if (this.inLayout) {}
    for (;;)
    {
      return;
      super.requestLayout();
    }
  }
  
  public void setAllowAssistant(boolean paramBoolean)
  {
    this.allowAssistant = paramBoolean;
  }
  
  public void setCheckPressed(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.isCheckPressed = paramBoolean1;
    this.isPressed = paramBoolean2;
    updateRadialProgressBackground();
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
  
  public void setFullyDraw(boolean paramBoolean)
  {
    this.fullyDraw = paramBoolean;
  }
  
  public void setHighlighted(boolean paramBoolean)
  {
    if (this.isHighlighted == paramBoolean) {
      return;
    }
    this.isHighlighted = paramBoolean;
    if (!this.isHighlighted)
    {
      this.lastHighlightProgressTime = System.currentTimeMillis();
      this.isHighlightedAnimated = true;
      this.highlightProgress = 300;
      label40:
      updateRadialProgressBackground();
      if (!this.useSeekBarWaweform) {
        break label82;
      }
      this.seekBarWaveform.setSelected(isDrawSelectedBackground());
    }
    for (;;)
    {
      invalidate();
      break;
      this.isHighlightedAnimated = false;
      this.highlightProgress = 0;
      break label40;
      label82:
      this.seekBar.setSelected(isDrawSelectedBackground());
    }
  }
  
  public void setHighlightedAnimated()
  {
    this.isHighlightedAnimated = true;
    this.highlightProgress = 1000;
    this.lastHighlightProgressTime = System.currentTimeMillis();
    invalidate();
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
    label397:
    for (;;)
    {
      return;
      int i = TextUtils.indexOf(this.currentMessageObject.messageOwner.message.toLowerCase(), paramString.toLowerCase());
      if (i == -1)
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
        int j = i + paramString.length();
        for (int k = 0;; k++)
        {
          if (k >= this.currentMessageObject.textLayoutBlocks.size()) {
            break label397;
          }
          paramString = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(k);
          if ((i >= paramString.charactersOffset) && (i < paramString.charactersOffset + paramString.textLayout.getText().length()))
          {
            this.linkSelectionBlockNum = k;
            resetUrlPaths(true);
            for (;;)
            {
              try
              {
                LinkPath localLinkPath = obtainNewUrlPath(true);
                int m = paramString.textLayout.getText().length();
                localLinkPath.setCurrentLayout(paramString.textLayout, i, 0.0F);
                paramString.textLayout.getSelectionPath(i, j - paramString.charactersOffset, localLinkPath);
                if (j >= paramString.charactersOffset + m)
                {
                  k++;
                  if (k < this.currentMessageObject.textLayoutBlocks.size())
                  {
                    MessageObject.TextLayoutBlock localTextLayoutBlock = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(k);
                    m = localTextLayoutBlock.textLayout.getText().length();
                    localLinkPath = obtainNewUrlPath(true);
                    localLinkPath.setCurrentLayout(localTextLayoutBlock.textLayout, 0, localTextLayoutBlock.height);
                    localTextLayoutBlock.textLayout.getSelectionPath(0, j - localTextLayoutBlock.charactersOffset, localLinkPath);
                    i = paramString.charactersOffset;
                    if (j >= i + m - 1) {
                      continue;
                    }
                  }
                }
              }
              catch (Exception paramString)
              {
                FileLog.e(paramString);
                continue;
              }
              invalidate();
              break;
              k++;
            }
          }
        }
      }
    }
  }
  
  /* Error */
  public void setMessageObject(MessageObject paramMessageObject, final MessageObject.GroupedMessages paramGroupedMessages, boolean paramBoolean1, boolean paramBoolean2)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 3143	org/telegram/messenger/MessageObject:checkLayout	()Z
    //   4: ifne +23 -> 27
    //   7: aload_0
    //   8: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   11: ifnull +21 -> 32
    //   14: aload_0
    //   15: getfield 3145	org/telegram/ui/Cells/ChatMessageCell:lastHeight	I
    //   18: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   21: getfield 2262	android/graphics/Point:y	I
    //   24: if_icmpeq +8 -> 32
    //   27: aload_0
    //   28: aconst_null
    //   29: putfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   32: aload_0
    //   33: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   36: ifnull +17 -> 53
    //   39: aload_0
    //   40: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   43: invokevirtual 3164	org/telegram/messenger/MessageObject:getId	()I
    //   46: aload_1
    //   47: invokevirtual 3164	org/telegram/messenger/MessageObject:getId	()I
    //   50: if_icmpeq +2752 -> 2802
    //   53: iconst_1
    //   54: istore 5
    //   56: aload_0
    //   57: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   60: aload_1
    //   61: if_acmpne +10 -> 71
    //   64: aload_1
    //   65: getfield 3273	org/telegram/messenger/MessageObject:forceUpdate	Z
    //   68: ifeq +2740 -> 2808
    //   71: iconst_1
    //   72: istore 6
    //   74: aload_0
    //   75: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   78: aload_1
    //   79: if_acmpne +2735 -> 2814
    //   82: aload_0
    //   83: invokespecial 3275	org/telegram/ui/Cells/ChatMessageCell:isUserDataChanged	()Z
    //   86: ifne +10 -> 96
    //   89: aload_0
    //   90: getfield 2364	org/telegram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   93: ifeq +2721 -> 2814
    //   96: iconst_1
    //   97: istore 7
    //   99: aload_2
    //   100: aload_0
    //   101: getfield 705	org/telegram/ui/Cells/ChatMessageCell:currentMessagesGroup	Lorg/telegram/messenger/MessageObject$GroupedMessages;
    //   104: if_acmpeq +2716 -> 2820
    //   107: iconst_1
    //   108: istore 8
    //   110: iload 8
    //   112: istore 9
    //   114: iload 8
    //   116: ifne +53 -> 169
    //   119: iload 8
    //   121: istore 9
    //   123: aload_2
    //   124: ifnull +45 -> 169
    //   127: aload_2
    //   128: getfield 3278	org/telegram/messenger/MessageObject$GroupedMessages:messages	Ljava/util/ArrayList;
    //   131: invokevirtual 590	java/util/ArrayList:size	()I
    //   134: iconst_1
    //   135: if_icmple +2691 -> 2826
    //   138: aload_0
    //   139: getfield 705	org/telegram/ui/Cells/ChatMessageCell:currentMessagesGroup	Lorg/telegram/messenger/MessageObject$GroupedMessages;
    //   142: getfield 3281	org/telegram/messenger/MessageObject$GroupedMessages:positions	Ljava/util/HashMap;
    //   145: aload_0
    //   146: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   149: invokevirtual 3284	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   152: checkcast 964	org/telegram/messenger/MessageObject$GroupedMessagePosition
    //   155: astore 10
    //   157: aload 10
    //   159: aload_0
    //   160: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   163: if_acmpeq +2669 -> 2832
    //   166: iconst_1
    //   167: istore 9
    //   169: iload 6
    //   171: ifne +38 -> 209
    //   174: iload 7
    //   176: ifne +33 -> 209
    //   179: iload 9
    //   181: ifne +28 -> 209
    //   184: aload_0
    //   185: aload_1
    //   186: invokespecial 3286	org/telegram/ui/Cells/ChatMessageCell:isPhotoDataChanged	(Lorg/telegram/messenger/MessageObject;)Z
    //   189: ifne +20 -> 209
    //   192: aload_0
    //   193: getfield 953	org/telegram/ui/Cells/ChatMessageCell:pinnedBottom	Z
    //   196: iload_3
    //   197: if_icmpne +12 -> 209
    //   200: aload_0
    //   201: getfield 955	org/telegram/ui/Cells/ChatMessageCell:pinnedTop	Z
    //   204: iload 4
    //   206: if_icmpeq +19058 -> 19264
    //   209: aload_0
    //   210: iload_3
    //   211: putfield 953	org/telegram/ui/Cells/ChatMessageCell:pinnedBottom	Z
    //   214: aload_0
    //   215: iload 4
    //   217: putfield 955	org/telegram/ui/Cells/ChatMessageCell:pinnedTop	Z
    //   220: aload_0
    //   221: bipush -2
    //   223: putfield 3288	org/telegram/ui/Cells/ChatMessageCell:lastTime	I
    //   226: aload_0
    //   227: iconst_0
    //   228: putfield 3010	org/telegram/ui/Cells/ChatMessageCell:isHighlightedAnimated	Z
    //   231: aload_0
    //   232: iconst_m1
    //   233: putfield 1184	org/telegram/ui/Cells/ChatMessageCell:widthBeforeNewTimeLine	I
    //   236: aload_0
    //   237: aload_1
    //   238: putfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   241: aload_0
    //   242: aload_2
    //   243: putfield 705	org/telegram/ui/Cells/ChatMessageCell:currentMessagesGroup	Lorg/telegram/messenger/MessageObject$GroupedMessages;
    //   246: aload_0
    //   247: getfield 705	org/telegram/ui/Cells/ChatMessageCell:currentMessagesGroup	Lorg/telegram/messenger/MessageObject$GroupedMessages;
    //   250: ifnull +2588 -> 2838
    //   253: aload_0
    //   254: getfield 705	org/telegram/ui/Cells/ChatMessageCell:currentMessagesGroup	Lorg/telegram/messenger/MessageObject$GroupedMessages;
    //   257: getfield 2267	org/telegram/messenger/MessageObject$GroupedMessages:posArray	Ljava/util/ArrayList;
    //   260: invokevirtual 590	java/util/ArrayList:size	()I
    //   263: iconst_1
    //   264: if_icmple +2574 -> 2838
    //   267: aload_0
    //   268: aload_0
    //   269: getfield 705	org/telegram/ui/Cells/ChatMessageCell:currentMessagesGroup	Lorg/telegram/messenger/MessageObject$GroupedMessages;
    //   272: getfield 3281	org/telegram/messenger/MessageObject$GroupedMessages:positions	Ljava/util/HashMap;
    //   275: aload_0
    //   276: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   279: invokevirtual 3284	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   282: checkcast 964	org/telegram/messenger/MessageObject$GroupedMessagePosition
    //   285: putfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   288: aload_0
    //   289: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   292: ifnonnull +8 -> 300
    //   295: aload_0
    //   296: aconst_null
    //   297: putfield 705	org/telegram/ui/Cells/ChatMessageCell:currentMessagesGroup	Lorg/telegram/messenger/MessageObject$GroupedMessages;
    //   300: aload_0
    //   301: getfield 955	org/telegram/ui/Cells/ChatMessageCell:pinnedTop	Z
    //   304: ifeq +2547 -> 2851
    //   307: aload_0
    //   308: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   311: ifnull +15 -> 326
    //   314: aload_0
    //   315: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   318: getfield 2210	org/telegram/messenger/MessageObject$GroupedMessagePosition:flags	I
    //   321: iconst_4
    //   322: iand
    //   323: ifeq +2528 -> 2851
    //   326: iconst_1
    //   327: istore_3
    //   328: aload_0
    //   329: iload_3
    //   330: putfield 1783	org/telegram/ui/Cells/ChatMessageCell:drawPinnedTop	Z
    //   333: aload_0
    //   334: getfield 953	org/telegram/ui/Cells/ChatMessageCell:pinnedBottom	Z
    //   337: ifeq +2519 -> 2856
    //   340: aload_0
    //   341: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   344: ifnull +16 -> 360
    //   347: aload_0
    //   348: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   351: getfield 2210	org/telegram/messenger/MessageObject$GroupedMessagePosition:flags	I
    //   354: bipush 8
    //   356: iand
    //   357: ifeq +2499 -> 2856
    //   360: iconst_1
    //   361: istore_3
    //   362: aload_0
    //   363: iload_3
    //   364: putfield 1623	org/telegram/ui/Cells/ChatMessageCell:drawPinnedBottom	Z
    //   367: aload_0
    //   368: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   371: iconst_0
    //   372: invokevirtual 3291	org/telegram/messenger/ImageReceiver:setCrossfadeWithOldImage	(Z)V
    //   375: aload_0
    //   376: aload_1
    //   377: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   380: getfield 2383	org/telegram/tgnet/TLRPC$Message:send_state	I
    //   383: putfield 2380	org/telegram/ui/Cells/ChatMessageCell:lastSendState	I
    //   386: aload_0
    //   387: aload_1
    //   388: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   391: getfield 1715	org/telegram/tgnet/TLRPC$Message:destroyTime	I
    //   394: putfield 2385	org/telegram/ui/Cells/ChatMessageCell:lastDeleteDate	I
    //   397: aload_0
    //   398: aload_1
    //   399: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   402: getfield 2390	org/telegram/tgnet/TLRPC$Message:views	I
    //   405: putfield 2387	org/telegram/ui/Cells/ChatMessageCell:lastViewsCount	I
    //   408: aload_0
    //   409: iconst_0
    //   410: putfield 2288	org/telegram/ui/Cells/ChatMessageCell:isPressed	Z
    //   413: aload_0
    //   414: iconst_0
    //   415: putfield 730	org/telegram/ui/Cells/ChatMessageCell:gamePreviewPressed	Z
    //   418: aload_0
    //   419: iconst_1
    //   420: putfield 344	org/telegram/ui/Cells/ChatMessageCell:isCheckPressed	Z
    //   423: aload_0
    //   424: iconst_0
    //   425: putfield 480	org/telegram/ui/Cells/ChatMessageCell:hasNewLineForTime	Z
    //   428: aload_0
    //   429: getfield 2040	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   432: ifeq +2429 -> 2861
    //   435: aload_1
    //   436: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   439: ifne +2422 -> 2861
    //   442: aload_1
    //   443: invokevirtual 2043	org/telegram/messenger/MessageObject:needDrawAvatar	()Z
    //   446: ifeq +2415 -> 2861
    //   449: aload_0
    //   450: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   453: ifnull +13 -> 466
    //   456: aload_0
    //   457: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   460: getfield 3008	org/telegram/messenger/MessageObject$GroupedMessagePosition:edge	Z
    //   463: ifeq +2398 -> 2861
    //   466: iconst_1
    //   467: istore_3
    //   468: aload_0
    //   469: iload_3
    //   470: putfield 2278	org/telegram/ui/Cells/ChatMessageCell:isAvatarVisible	Z
    //   473: aload_0
    //   474: iconst_0
    //   475: putfield 2967	org/telegram/ui/Cells/ChatMessageCell:wasLayout	Z
    //   478: aload_0
    //   479: iconst_0
    //   480: putfield 989	org/telegram/ui/Cells/ChatMessageCell:drwaShareGoIcon	Z
    //   483: aload_0
    //   484: iconst_0
    //   485: putfield 1612	org/telegram/ui/Cells/ChatMessageCell:groupPhotoInvisible	Z
    //   488: aload_0
    //   489: aload_0
    //   490: aload_1
    //   491: invokespecial 3293	org/telegram/ui/Cells/ChatMessageCell:checkNeedDrawShareButton	(Lorg/telegram/messenger/MessageObject;)Z
    //   494: putfield 3020	org/telegram/ui/Cells/ChatMessageCell:drawShareButton	Z
    //   497: aload_0
    //   498: aconst_null
    //   499: putfield 1527	org/telegram/ui/Cells/ChatMessageCell:replyNameLayout	Landroid/text/StaticLayout;
    //   502: aload_0
    //   503: aconst_null
    //   504: putfield 2617	org/telegram/ui/Cells/ChatMessageCell:adminLayout	Landroid/text/StaticLayout;
    //   507: aload_0
    //   508: aconst_null
    //   509: putfield 2404	org/telegram/ui/Cells/ChatMessageCell:replyTextLayout	Landroid/text/StaticLayout;
    //   512: aload_0
    //   513: iconst_0
    //   514: putfield 2682	org/telegram/ui/Cells/ChatMessageCell:replyNameWidth	I
    //   517: aload_0
    //   518: iconst_0
    //   519: putfield 2686	org/telegram/ui/Cells/ChatMessageCell:replyTextWidth	I
    //   522: aload_0
    //   523: iconst_0
    //   524: putfield 2559	org/telegram/ui/Cells/ChatMessageCell:viaWidth	I
    //   527: aload_0
    //   528: iconst_0
    //   529: putfield 2585	org/telegram/ui/Cells/ChatMessageCell:viaNameWidth	I
    //   532: aload_0
    //   533: iconst_0
    //   534: putfield 2933	org/telegram/ui/Cells/ChatMessageCell:addedCaptionHeight	I
    //   537: aload_0
    //   538: aconst_null
    //   539: putfield 2419	org/telegram/ui/Cells/ChatMessageCell:currentReplyPhoto	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   542: aload_0
    //   543: aconst_null
    //   544: putfield 2376	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   547: aload_0
    //   548: aconst_null
    //   549: putfield 2378	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   552: aload_0
    //   553: aconst_null
    //   554: putfield 2561	org/telegram/ui/Cells/ChatMessageCell:currentViaBotUser	Lorg/telegram/tgnet/TLRPC$User;
    //   557: aload_0
    //   558: iconst_0
    //   559: putfield 2563	org/telegram/ui/Cells/ChatMessageCell:drawNameLayout	Z
    //   562: aload_0
    //   563: getfield 449	org/telegram/ui/Cells/ChatMessageCell:scheduledInvalidate	Z
    //   566: ifeq +15 -> 581
    //   569: aload_0
    //   570: getfield 361	org/telegram/ui/Cells/ChatMessageCell:invalidateRunnable	Ljava/lang/Runnable;
    //   573: invokestatic 3297	org/telegram/messenger/AndroidUtilities:cancelRunOnUIThread	(Ljava/lang/Runnable;)V
    //   576: aload_0
    //   577: iconst_0
    //   578: putfield 449	org/telegram/ui/Cells/ChatMessageCell:scheduledInvalidate	Z
    //   581: aload_0
    //   582: iconst_m1
    //   583: invokespecial 721	org/telegram/ui/Cells/ChatMessageCell:resetPressedLink	(I)V
    //   586: aload_1
    //   587: iconst_0
    //   588: putfield 3273	org/telegram/messenger/MessageObject:forceUpdate	Z
    //   591: aload_0
    //   592: iconst_0
    //   593: putfield 724	org/telegram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   596: aload_0
    //   597: iconst_0
    //   598: putfield 459	org/telegram/ui/Cells/ChatMessageCell:hasLinkPreview	Z
    //   601: aload_0
    //   602: iconst_0
    //   603: putfield 461	org/telegram/ui/Cells/ChatMessageCell:hasOldCaptionPreview	Z
    //   606: aload_0
    //   607: iconst_0
    //   608: putfield 463	org/telegram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   611: aload_0
    //   612: iconst_0
    //   613: putfield 465	org/telegram/ui/Cells/ChatMessageCell:hasInvoicePreview	Z
    //   616: aload_0
    //   617: iconst_0
    //   618: putfield 812	org/telegram/ui/Cells/ChatMessageCell:instantButtonPressed	Z
    //   621: aload_0
    //   622: iconst_0
    //   623: putfield 785	org/telegram/ui/Cells/ChatMessageCell:instantPressed	Z
    //   626: getstatic 790	android/os/Build$VERSION:SDK_INT	I
    //   629: bipush 21
    //   631: if_icmplt +31 -> 662
    //   634: aload_0
    //   635: getfield 792	org/telegram/ui/Cells/ChatMessageCell:instantViewSelectorDrawable	Landroid/graphics/drawable/Drawable;
    //   638: ifnull +24 -> 662
    //   641: aload_0
    //   642: getfield 792	org/telegram/ui/Cells/ChatMessageCell:instantViewSelectorDrawable	Landroid/graphics/drawable/Drawable;
    //   645: iconst_0
    //   646: iconst_0
    //   647: invokevirtual 3300	android/graphics/drawable/Drawable:setVisible	(ZZ)Z
    //   650: pop
    //   651: aload_0
    //   652: getfield 792	org/telegram/ui/Cells/ChatMessageCell:instantViewSelectorDrawable	Landroid/graphics/drawable/Drawable;
    //   655: getstatic 858	android/util/StateSet:NOTHING	[I
    //   658: invokevirtual 806	android/graphics/drawable/Drawable:setState	([I)Z
    //   661: pop
    //   662: aload_0
    //   663: iconst_0
    //   664: putfield 637	org/telegram/ui/Cells/ChatMessageCell:linkPreviewPressed	Z
    //   667: aload_0
    //   668: iconst_0
    //   669: putfield 558	org/telegram/ui/Cells/ChatMessageCell:buttonPressed	I
    //   672: aload_0
    //   673: iconst_0
    //   674: putfield 568	org/telegram/ui/Cells/ChatMessageCell:miniButtonPressed	I
    //   677: aload_0
    //   678: iconst_m1
    //   679: putfield 611	org/telegram/ui/Cells/ChatMessageCell:pressedBotButton	I
    //   682: aload_0
    //   683: iconst_0
    //   684: putfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   687: aload_0
    //   688: iconst_0
    //   689: putfield 565	org/telegram/ui/Cells/ChatMessageCell:mediaOffsetY	I
    //   692: aload_0
    //   693: iconst_0
    //   694: putfield 498	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   697: aload_0
    //   698: aconst_null
    //   699: putfield 1149	org/telegram/ui/Cells/ChatMessageCell:documentAttach	Lorg/telegram/tgnet/TLRPC$Document;
    //   702: aload_0
    //   703: aconst_null
    //   704: putfield 732	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   707: aload_0
    //   708: aconst_null
    //   709: putfield 1595	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   712: aload_0
    //   713: aconst_null
    //   714: putfield 1672	org/telegram/ui/Cells/ChatMessageCell:videoInfoLayout	Landroid/text/StaticLayout;
    //   717: aload_0
    //   718: aconst_null
    //   719: putfield 1643	org/telegram/ui/Cells/ChatMessageCell:photosCountLayout	Landroid/text/StaticLayout;
    //   722: aload_0
    //   723: aconst_null
    //   724: putfield 1522	org/telegram/ui/Cells/ChatMessageCell:siteNameLayout	Landroid/text/StaticLayout;
    //   727: aload_0
    //   728: aconst_null
    //   729: putfield 1599	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   732: aload_0
    //   733: aconst_null
    //   734: putfield 635	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   737: aload_0
    //   738: iconst_0
    //   739: putfield 1777	org/telegram/ui/Cells/ChatMessageCell:captionOffsetX	I
    //   742: aload_0
    //   743: aconst_null
    //   744: putfield 631	org/telegram/ui/Cells/ChatMessageCell:currentCaption	Ljava/lang/CharSequence;
    //   747: aload_0
    //   748: aconst_null
    //   749: putfield 951	org/telegram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   752: aload_0
    //   753: iconst_0
    //   754: putfield 783	org/telegram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   757: aload_0
    //   758: aconst_null
    //   759: putfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   762: aload_0
    //   763: aconst_null
    //   764: putfield 1404	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   767: aload_0
    //   768: aconst_null
    //   769: putfield 1358	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   772: aload_0
    //   773: aconst_null
    //   774: putfield 1270	org/telegram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   777: aload_0
    //   778: iconst_0
    //   779: putfield 1399	org/telegram/ui/Cells/ChatMessageCell:cancelLoading	Z
    //   782: aload_0
    //   783: iconst_m1
    //   784: putfield 553	org/telegram/ui/Cells/ChatMessageCell:buttonState	I
    //   787: aload_0
    //   788: iconst_m1
    //   789: putfield 546	org/telegram/ui/Cells/ChatMessageCell:miniButtonState	I
    //   792: aload_0
    //   793: iconst_0
    //   794: putfield 1384	org/telegram/ui/Cells/ChatMessageCell:hasMiniProgress	I
    //   797: aload_0
    //   798: aconst_null
    //   799: putfield 2294	org/telegram/ui/Cells/ChatMessageCell:currentUrl	Ljava/lang/String;
    //   802: aload_0
    //   803: iconst_0
    //   804: putfield 2364	org/telegram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   807: aload_0
    //   808: iconst_1
    //   809: putfield 346	org/telegram/ui/Cells/ChatMessageCell:drawBackground	Z
    //   812: aload_0
    //   813: iconst_0
    //   814: putfield 2421	org/telegram/ui/Cells/ChatMessageCell:drawName	Z
    //   817: aload_0
    //   818: iconst_0
    //   819: putfield 509	org/telegram/ui/Cells/ChatMessageCell:useSeekBarWaweform	Z
    //   822: aload_0
    //   823: iconst_0
    //   824: putfield 556	org/telegram/ui/Cells/ChatMessageCell:drawInstantView	Z
    //   827: aload_0
    //   828: iconst_0
    //   829: putfield 849	org/telegram/ui/Cells/ChatMessageCell:drawInstantViewType	I
    //   832: aload_0
    //   833: iconst_0
    //   834: putfield 2431	org/telegram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   837: aload_0
    //   838: iconst_0
    //   839: putfield 615	org/telegram/ui/Cells/ChatMessageCell:mediaBackground	Z
    //   842: iconst_0
    //   843: istore 11
    //   845: iconst_0
    //   846: istore 12
    //   848: iconst_0
    //   849: istore 13
    //   851: aload_0
    //   852: iconst_0
    //   853: putfield 1187	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   856: aload_0
    //   857: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   860: iconst_0
    //   861: invokevirtual 1402	org/telegram/messenger/ImageReceiver:setForceLoading	(Z)V
    //   864: aload_0
    //   865: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   868: iconst_0
    //   869: invokevirtual 1348	org/telegram/messenger/ImageReceiver:setNeedsQualityThumb	(Z)V
    //   872: aload_0
    //   873: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   876: iconst_0
    //   877: invokevirtual 1351	org/telegram/messenger/ImageReceiver:setShouldGenerateQualityThumb	(Z)V
    //   880: aload_0
    //   881: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   884: iconst_0
    //   885: invokevirtual 3303	org/telegram/messenger/ImageReceiver:setAllowDecodeSingleFrame	(Z)V
    //   888: aload_0
    //   889: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   892: aconst_null
    //   893: invokevirtual 1354	org/telegram/messenger/ImageReceiver:setParentMessageObject	(Lorg/telegram/messenger/MessageObject;)V
    //   896: aload_0
    //   897: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   900: ldc_w 1545
    //   903: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   906: invokevirtual 377	org/telegram/messenger/ImageReceiver:setRoundRadius	(I)V
    //   909: iload 6
    //   911: ifeq +18 -> 929
    //   914: aload_0
    //   915: iconst_0
    //   916: putfield 1531	org/telegram/ui/Cells/ChatMessageCell:firstVisibleBlockNum	I
    //   919: aload_0
    //   920: iconst_0
    //   921: putfield 1533	org/telegram/ui/Cells/ChatMessageCell:lastVisibleBlockNum	I
    //   924: aload_0
    //   925: iconst_1
    //   926: putfield 1459	org/telegram/ui/Cells/ChatMessageCell:needNewVisiblePart	Z
    //   929: aload_1
    //   930: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   933: ifne +8537 -> 9470
    //   936: aload_0
    //   937: iconst_1
    //   938: putfield 2431	org/telegram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   941: invokestatic 2226	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   944: ifeq +1969 -> 2913
    //   947: aload_0
    //   948: getfield 2040	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   951: ifeq +1915 -> 2866
    //   954: aload_1
    //   955: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   958: ifne +1908 -> 2866
    //   961: aload_1
    //   962: invokevirtual 2043	org/telegram/messenger/MessageObject:needDrawAvatar	()Z
    //   965: ifeq +1901 -> 2866
    //   968: invokestatic 2258	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   971: ldc_w 3304
    //   974: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   977: isub
    //   978: istore 14
    //   980: aload_0
    //   981: iconst_1
    //   982: putfield 2421	org/telegram/ui/Cells/ChatMessageCell:drawName	Z
    //   985: aload_0
    //   986: iload 14
    //   988: putfield 1187	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   991: aload_1
    //   992: invokevirtual 1416	org/telegram/messenger/MessageObject:isRoundVideo	()Z
    //   995: ifeq +49 -> 1044
    //   998: aload_0
    //   999: getfield 1187	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   1002: i2d
    //   1003: dstore 15
    //   1005: getstatic 1170	org/telegram/ui/ActionBar/Theme:chat_audioTimePaint	Landroid/text/TextPaint;
    //   1008: ldc_w 1172
    //   1011: invokevirtual 1178	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   1014: f2d
    //   1015: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   1018: dstore 17
    //   1020: aload_1
    //   1021: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   1024: ifeq +2001 -> 3025
    //   1027: iconst_0
    //   1028: istore 9
    //   1030: aload_0
    //   1031: dload 15
    //   1033: dload 17
    //   1035: iload 9
    //   1037: i2d
    //   1038: dadd
    //   1039: dsub
    //   1040: d2i
    //   1041: putfield 1187	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   1044: aload_0
    //   1045: aload_1
    //   1046: invokespecial 1191	org/telegram/ui/Cells/ChatMessageCell:measureTime	(Lorg/telegram/messenger/MessageObject;)V
    //   1049: aload_0
    //   1050: getfield 493	org/telegram/ui/Cells/ChatMessageCell:timeWidth	I
    //   1053: ldc_w 1588
    //   1056: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1059: iadd
    //   1060: istore 9
    //   1062: iload 9
    //   1064: istore 19
    //   1066: aload_1
    //   1067: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   1070: ifeq +14 -> 1084
    //   1073: iload 9
    //   1075: ldc_w 2877
    //   1078: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1081: iadd
    //   1082: istore 19
    //   1084: aload_1
    //   1085: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1088: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1091: instanceof 1029
    //   1094: ifeq +1942 -> 3036
    //   1097: aload_1
    //   1098: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1101: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1104: getfield 2667	org/telegram/tgnet/TLRPC$MessageMedia:game	Lorg/telegram/tgnet/TLRPC$TL_game;
    //   1107: instanceof 2669
    //   1110: ifeq +1926 -> 3036
    //   1113: iconst_1
    //   1114: istore_3
    //   1115: aload_0
    //   1116: iload_3
    //   1117: putfield 463	org/telegram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   1120: aload_0
    //   1121: aload_1
    //   1122: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1125: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1128: instanceof 1031
    //   1131: putfield 465	org/telegram/ui/Cells/ChatMessageCell:hasInvoicePreview	Z
    //   1134: aload_1
    //   1135: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1138: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1141: instanceof 999
    //   1144: ifeq +1897 -> 3041
    //   1147: aload_1
    //   1148: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1151: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1154: getfield 828	org/telegram/tgnet/TLRPC$MessageMedia:webpage	Lorg/telegram/tgnet/TLRPC$WebPage;
    //   1157: instanceof 1001
    //   1160: ifeq +1881 -> 3041
    //   1163: iconst_1
    //   1164: istore_3
    //   1165: aload_0
    //   1166: iload_3
    //   1167: putfield 459	org/telegram/ui/Cells/ChatMessageCell:hasLinkPreview	Z
    //   1170: aload_0
    //   1171: getfield 459	org/telegram/ui/Cells/ChatMessageCell:hasLinkPreview	Z
    //   1174: ifeq +1872 -> 3046
    //   1177: aload_1
    //   1178: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1181: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1184: getfield 828	org/telegram/tgnet/TLRPC$MessageMedia:webpage	Lorg/telegram/tgnet/TLRPC$WebPage;
    //   1187: getfield 3308	org/telegram/tgnet/TLRPC$WebPage:cached_page	Lorg/telegram/tgnet/TLRPC$Page;
    //   1190: ifnull +1856 -> 3046
    //   1193: iconst_1
    //   1194: istore_3
    //   1195: aload_0
    //   1196: iload_3
    //   1197: putfield 556	org/telegram/ui/Cells/ChatMessageCell:drawInstantView	Z
    //   1200: iconst_0
    //   1201: istore 9
    //   1203: aload_0
    //   1204: getfield 459	org/telegram/ui/Cells/ChatMessageCell:hasLinkPreview	Z
    //   1207: ifeq +1844 -> 3051
    //   1210: aload_1
    //   1211: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1214: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1217: getfield 828	org/telegram/tgnet/TLRPC$MessageMedia:webpage	Lorg/telegram/tgnet/TLRPC$WebPage;
    //   1220: getfield 899	org/telegram/tgnet/TLRPC$WebPage:site_name	Ljava/lang/String;
    //   1223: astore_2
    //   1224: aload_0
    //   1225: getfield 459	org/telegram/ui/Cells/ChatMessageCell:hasLinkPreview	Z
    //   1228: ifeq +1828 -> 3056
    //   1231: aload_1
    //   1232: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1235: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1238: getfield 828	org/telegram/tgnet/TLRPC$MessageMedia:webpage	Lorg/telegram/tgnet/TLRPC$WebPage;
    //   1241: getfield 3310	org/telegram/tgnet/TLRPC$WebPage:type	Ljava/lang/String;
    //   1244: astore 10
    //   1246: aload_0
    //   1247: getfield 556	org/telegram/ui/Cells/ChatMessageCell:drawInstantView	Z
    //   1250: ifne +1872 -> 3122
    //   1253: ldc_w 3312
    //   1256: aload 10
    //   1258: invokevirtual 2357	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1261: ifeq +1801 -> 3062
    //   1264: aload_0
    //   1265: iconst_1
    //   1266: putfield 556	org/telegram/ui/Cells/ChatMessageCell:drawInstantView	Z
    //   1269: aload_0
    //   1270: iconst_1
    //   1271: putfield 849	org/telegram/ui/Cells/ChatMessageCell:drawInstantViewType	I
    //   1274: iload 9
    //   1276: istore 6
    //   1278: getstatic 790	android/os/Build$VERSION:SDK_INT	I
    //   1281: bipush 21
    //   1283: if_icmplt +127 -> 1410
    //   1286: aload_0
    //   1287: getfield 556	org/telegram/ui/Cells/ChatMessageCell:drawInstantView	Z
    //   1290: ifeq +120 -> 1410
    //   1293: aload_0
    //   1294: getfield 792	org/telegram/ui/Cells/ChatMessageCell:instantViewSelectorDrawable	Landroid/graphics/drawable/Drawable;
    //   1297: ifnonnull +2120 -> 3417
    //   1300: new 1541	android/graphics/Paint
    //   1303: dup
    //   1304: iconst_1
    //   1305: invokespecial 3314	android/graphics/Paint:<init>	(I)V
    //   1308: astore_2
    //   1309: aload_2
    //   1310: iconst_m1
    //   1311: invokevirtual 1544	android/graphics/Paint:setColor	(I)V
    //   1314: new 14	org/telegram/ui/Cells/ChatMessageCell$2
    //   1317: dup
    //   1318: aload_0
    //   1319: aload_2
    //   1320: invokespecial 3317	org/telegram/ui/Cells/ChatMessageCell$2:<init>	(Lorg/telegram/ui/Cells/ChatMessageCell;Landroid/graphics/Paint;)V
    //   1323: astore 10
    //   1325: getstatic 3320	android/util/StateSet:WILD_CARD	[I
    //   1328: astore 20
    //   1330: aload_0
    //   1331: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1334: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   1337: ifeq +2073 -> 3410
    //   1340: ldc_w 1692
    //   1343: astore_2
    //   1344: aload_2
    //   1345: invokestatic 1511	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   1348: istore 9
    //   1350: aload_0
    //   1351: new 3322	android/graphics/drawable/RippleDrawable
    //   1354: dup
    //   1355: new 3324	android/content/res/ColorStateList
    //   1358: dup
    //   1359: iconst_1
    //   1360: anewarray 3325	[I
    //   1363: dup
    //   1364: iconst_0
    //   1365: aload 20
    //   1367: aastore
    //   1368: iconst_1
    //   1369: newarray <illegal type>
    //   1371: dup
    //   1372: iconst_0
    //   1373: iload 9
    //   1375: ldc_w 3326
    //   1378: iand
    //   1379: iastore
    //   1380: invokespecial 3329	android/content/res/ColorStateList:<init>	([[I[I)V
    //   1383: aconst_null
    //   1384: aload 10
    //   1386: invokespecial 3332	android/graphics/drawable/RippleDrawable:<init>	(Landroid/content/res/ColorStateList;Landroid/graphics/drawable/Drawable;Landroid/graphics/drawable/Drawable;)V
    //   1389: putfield 792	org/telegram/ui/Cells/ChatMessageCell:instantViewSelectorDrawable	Landroid/graphics/drawable/Drawable;
    //   1392: aload_0
    //   1393: getfield 792	org/telegram/ui/Cells/ChatMessageCell:instantViewSelectorDrawable	Landroid/graphics/drawable/Drawable;
    //   1396: aload_0
    //   1397: invokevirtual 3336	android/graphics/drawable/Drawable:setCallback	(Landroid/graphics/drawable/Drawable$Callback;)V
    //   1400: aload_0
    //   1401: getfield 792	org/telegram/ui/Cells/ChatMessageCell:instantViewSelectorDrawable	Landroid/graphics/drawable/Drawable;
    //   1404: iconst_1
    //   1405: iconst_0
    //   1406: invokevirtual 3300	android/graphics/drawable/Drawable:setVisible	(ZZ)Z
    //   1409: pop
    //   1410: aload_0
    //   1411: iload 14
    //   1413: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   1416: aload_0
    //   1417: getfield 459	org/telegram/ui/Cells/ChatMessageCell:hasLinkPreview	Z
    //   1420: ifne +29 -> 1449
    //   1423: aload_0
    //   1424: getfield 463	org/telegram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   1427: ifne +22 -> 1449
    //   1430: aload_0
    //   1431: getfield 465	org/telegram/ui/Cells/ChatMessageCell:hasInvoicePreview	Z
    //   1434: ifne +15 -> 1449
    //   1437: iload 14
    //   1439: aload_1
    //   1440: getfield 472	org/telegram/messenger/MessageObject:lastLineWidth	I
    //   1443: isub
    //   1444: iload 19
    //   1446: if_icmpge +2015 -> 3461
    //   1449: aload_0
    //   1450: aload_0
    //   1451: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   1454: aload_1
    //   1455: getfield 472	org/telegram/messenger/MessageObject:lastLineWidth	I
    //   1458: invokestatic 486	java/lang/Math:max	(II)I
    //   1461: ldc_w 487
    //   1464: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1467: iadd
    //   1468: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   1471: aload_0
    //   1472: aload_0
    //   1473: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   1476: aload_0
    //   1477: getfield 493	org/telegram/ui/Cells/ChatMessageCell:timeWidth	I
    //   1480: ldc_w 487
    //   1483: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1486: iadd
    //   1487: invokestatic 486	java/lang/Math:max	(II)I
    //   1490: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   1493: aload_0
    //   1494: aload_0
    //   1495: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   1498: ldc_w 487
    //   1501: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1504: isub
    //   1505: putfield 1187	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   1508: aload_1
    //   1509: invokevirtual 1416	org/telegram/messenger/MessageObject:isRoundVideo	()Z
    //   1512: ifeq +49 -> 1561
    //   1515: aload_0
    //   1516: getfield 1187	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   1519: i2d
    //   1520: dstore 15
    //   1522: getstatic 1170	org/telegram/ui/ActionBar/Theme:chat_audioTimePaint	Landroid/text/TextPaint;
    //   1525: ldc_w 1172
    //   1528: invokevirtual 1178	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   1531: f2d
    //   1532: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   1535: dstore 17
    //   1537: aload_1
    //   1538: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   1541: ifeq +1995 -> 3536
    //   1544: iconst_0
    //   1545: istore 9
    //   1547: aload_0
    //   1548: dload 15
    //   1550: dload 17
    //   1552: iload 9
    //   1554: i2d
    //   1555: dadd
    //   1556: dsub
    //   1557: d2i
    //   1558: putfield 1187	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   1561: aload_0
    //   1562: aload_1
    //   1563: invokespecial 3338	org/telegram/ui/Cells/ChatMessageCell:setMessageObjectInternal	(Lorg/telegram/messenger/MessageObject;)V
    //   1566: aload_1
    //   1567: getfield 1119	org/telegram/messenger/MessageObject:textWidth	I
    //   1570: istore 8
    //   1572: aload_0
    //   1573: getfield 463	org/telegram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   1576: ifne +10 -> 1586
    //   1579: aload_0
    //   1580: getfield 465	org/telegram/ui/Cells/ChatMessageCell:hasInvoicePreview	Z
    //   1583: ifeq +1964 -> 3547
    //   1586: ldc_w 587
    //   1589: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1592: istore 9
    //   1594: aload_0
    //   1595: iload 9
    //   1597: iload 8
    //   1599: iadd
    //   1600: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   1603: aload_0
    //   1604: aload_1
    //   1605: getfield 775	org/telegram/messenger/MessageObject:textHeight	I
    //   1608: ldc_w 3339
    //   1611: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1614: iadd
    //   1615: aload_0
    //   1616: getfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1619: iadd
    //   1620: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   1623: aload_0
    //   1624: getfield 1783	org/telegram/ui/Cells/ChatMessageCell:drawPinnedTop	Z
    //   1627: ifeq +16 -> 1643
    //   1630: aload_0
    //   1631: aload_0
    //   1632: getfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1635: fconst_1
    //   1636: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1639: isub
    //   1640: putfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1643: aload_0
    //   1644: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   1647: aload_0
    //   1648: getfield 2567	org/telegram/ui/Cells/ChatMessageCell:nameWidth	I
    //   1651: invokestatic 486	java/lang/Math:max	(II)I
    //   1654: aload_0
    //   1655: getfield 2623	org/telegram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1658: invokestatic 486	java/lang/Math:max	(II)I
    //   1661: aload_0
    //   1662: getfield 2682	org/telegram/ui/Cells/ChatMessageCell:replyNameWidth	I
    //   1665: invokestatic 486	java/lang/Math:max	(II)I
    //   1668: aload_0
    //   1669: getfield 2686	org/telegram/ui/Cells/ChatMessageCell:replyTextWidth	I
    //   1672: invokestatic 486	java/lang/Math:max	(II)I
    //   1675: istore 21
    //   1677: iconst_0
    //   1678: istore 22
    //   1680: aload_0
    //   1681: getfield 459	org/telegram/ui/Cells/ChatMessageCell:hasLinkPreview	Z
    //   1684: ifne +17 -> 1701
    //   1687: aload_0
    //   1688: getfield 463	org/telegram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   1691: ifne +10 -> 1701
    //   1694: aload_0
    //   1695: getfield 465	org/telegram/ui/Cells/ChatMessageCell:hasInvoicePreview	Z
    //   1698: ifeq +7744 -> 9442
    //   1701: invokestatic 2226	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   1704: ifeq +1864 -> 3568
    //   1707: aload_0
    //   1708: getfield 2040	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   1711: ifeq +1842 -> 3553
    //   1714: aload_1
    //   1715: invokevirtual 2043	org/telegram/messenger/MessageObject:needDrawAvatar	()Z
    //   1718: ifeq +1835 -> 3553
    //   1721: aload_0
    //   1722: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1725: invokevirtual 1027	org/telegram/messenger/MessageObject:isOut	()Z
    //   1728: ifne +1825 -> 3553
    //   1731: invokestatic 2258	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   1734: ldc_w 3340
    //   1737: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1740: isub
    //   1741: istore 9
    //   1743: iload 9
    //   1745: istore 8
    //   1747: aload_0
    //   1748: getfield 3020	org/telegram/ui/Cells/ChatMessageCell:drawShareButton	Z
    //   1751: ifeq +14 -> 1765
    //   1754: iload 9
    //   1756: ldc_w 1077
    //   1759: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1762: isub
    //   1763: istore 8
    //   1765: aload_0
    //   1766: getfield 459	org/telegram/ui/Cells/ChatMessageCell:hasLinkPreview	Z
    //   1769: ifeq +1888 -> 3657
    //   1772: aload_1
    //   1773: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1776: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1779: getfield 828	org/telegram/tgnet/TLRPC$MessageMedia:webpage	Lorg/telegram/tgnet/TLRPC$WebPage;
    //   1782: checkcast 1001	org/telegram/tgnet/TLRPC$TL_webPage
    //   1785: astore_2
    //   1786: aload_2
    //   1787: getfield 3341	org/telegram/tgnet/TLRPC$TL_webPage:site_name	Ljava/lang/String;
    //   1790: astore 23
    //   1792: aload_2
    //   1793: getfield 3342	org/telegram/tgnet/TLRPC$TL_webPage:title	Ljava/lang/String;
    //   1796: astore 24
    //   1798: aload_2
    //   1799: getfield 3345	org/telegram/tgnet/TLRPC$TL_webPage:author	Ljava/lang/String;
    //   1802: astore 25
    //   1804: aload_2
    //   1805: getfield 3346	org/telegram/tgnet/TLRPC$TL_webPage:description	Ljava/lang/String;
    //   1808: astore 26
    //   1810: aload_2
    //   1811: getfield 3349	org/telegram/tgnet/TLRPC$TL_webPage:photo	Lorg/telegram/tgnet/TLRPC$Photo;
    //   1814: astore 27
    //   1816: aload_2
    //   1817: getfield 3350	org/telegram/tgnet/TLRPC$TL_webPage:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   1820: astore 20
    //   1822: aload_2
    //   1823: getfield 3351	org/telegram/tgnet/TLRPC$TL_webPage:type	Ljava/lang/String;
    //   1826: astore 10
    //   1828: aload_2
    //   1829: getfield 3352	org/telegram/tgnet/TLRPC$TL_webPage:duration	I
    //   1832: istore 28
    //   1834: iload 8
    //   1836: istore 9
    //   1838: aload 23
    //   1840: ifnull +50 -> 1890
    //   1843: iload 8
    //   1845: istore 9
    //   1847: aload 27
    //   1849: ifnull +41 -> 1890
    //   1852: iload 8
    //   1854: istore 9
    //   1856: aload 23
    //   1858: invokevirtual 1276	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   1861: ldc_w 3354
    //   1864: invokevirtual 2357	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1867: ifeq +23 -> 1890
    //   1870: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   1873: getfield 2262	android/graphics/Point:y	I
    //   1876: iconst_3
    //   1877: idiv
    //   1878: aload_0
    //   1879: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   1882: getfield 1119	org/telegram/messenger/MessageObject:textWidth	I
    //   1885: invokestatic 486	java/lang/Math:max	(II)I
    //   1888: istore 9
    //   1890: iload 6
    //   1892: ifne +1754 -> 3646
    //   1895: aload_0
    //   1896: getfield 556	org/telegram/ui/Cells/ChatMessageCell:drawInstantView	Z
    //   1899: ifne +1747 -> 3646
    //   1902: aload 20
    //   1904: ifnonnull +1742 -> 3646
    //   1907: aload 10
    //   1909: ifnull +1737 -> 3646
    //   1912: aload 10
    //   1914: ldc_w 3356
    //   1917: invokevirtual 2357	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1920: ifne +25 -> 1945
    //   1923: aload 10
    //   1925: ldc_w 3358
    //   1928: invokevirtual 2357	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1931: ifne +14 -> 1945
    //   1934: aload 10
    //   1936: ldc_w 3360
    //   1939: invokevirtual 2357	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1942: ifeq +1704 -> 3646
    //   1945: iconst_1
    //   1946: istore 11
    //   1948: iload 6
    //   1950: ifne +1702 -> 3652
    //   1953: aload_0
    //   1954: getfield 556	org/telegram/ui/Cells/ChatMessageCell:drawInstantView	Z
    //   1957: ifne +1695 -> 3652
    //   1960: aload 20
    //   1962: ifnonnull +1690 -> 3652
    //   1965: aload 26
    //   1967: ifnull +1685 -> 3652
    //   1970: aload 10
    //   1972: ifnull +1680 -> 3652
    //   1975: aload 10
    //   1977: ldc_w 3356
    //   1980: invokevirtual 2357	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1983: ifne +25 -> 2008
    //   1986: aload 10
    //   1988: ldc_w 3358
    //   1991: invokevirtual 2357	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1994: ifne +14 -> 2008
    //   1997: aload 10
    //   1999: ldc_w 3360
    //   2002: invokevirtual 2357	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   2005: ifeq +1647 -> 3652
    //   2008: aload_0
    //   2009: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2012: getfield 1336	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   2015: ifnull +1637 -> 3652
    //   2018: iconst_1
    //   2019: istore_3
    //   2020: aload_0
    //   2021: iload_3
    //   2022: putfield 1637	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   2025: aconst_null
    //   2026: astore_2
    //   2027: aload_0
    //   2028: getfield 465	org/telegram/ui/Cells/ChatMessageCell:hasInvoicePreview	Z
    //   2031: ifeq +1799 -> 3830
    //   2034: iconst_0
    //   2035: istore 29
    //   2037: iconst_3
    //   2038: istore 30
    //   2040: iconst_0
    //   2041: istore 8
    //   2043: iconst_0
    //   2044: istore 31
    //   2046: iload 9
    //   2048: iload 29
    //   2050: isub
    //   2051: istore 32
    //   2053: aload_0
    //   2054: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2057: getfield 1336	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   2060: ifnonnull +16 -> 2076
    //   2063: aload 27
    //   2065: ifnull +11 -> 2076
    //   2068: aload_0
    //   2069: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   2072: iconst_1
    //   2073: invokevirtual 3363	org/telegram/messenger/MessageObject:generateThumbs	(Z)V
    //   2076: iload 21
    //   2078: istore 9
    //   2080: iload 22
    //   2082: istore 6
    //   2084: aload 23
    //   2086: ifnull +270 -> 2356
    //   2089: iload 8
    //   2091: istore 31
    //   2093: iload 21
    //   2095: istore 9
    //   2097: getstatic 1554	org/telegram/ui/ActionBar/Theme:chat_replyNamePaint	Landroid/text/TextPaint;
    //   2100: aload 23
    //   2102: invokevirtual 1178	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   2105: fconst_1
    //   2106: fadd
    //   2107: f2d
    //   2108: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   2111: d2i
    //   2112: istore 6
    //   2114: iload 8
    //   2116: istore 31
    //   2118: iload 21
    //   2120: istore 9
    //   2122: new 350	android/text/StaticLayout
    //   2125: astore 33
    //   2127: iload 8
    //   2129: istore 31
    //   2131: iload 21
    //   2133: istore 9
    //   2135: aload 33
    //   2137: aload 23
    //   2139: getstatic 1554	org/telegram/ui/ActionBar/Theme:chat_replyNamePaint	Landroid/text/TextPaint;
    //   2142: iload 6
    //   2144: iload 32
    //   2146: invokestatic 1195	java/lang/Math:min	(II)I
    //   2149: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   2152: fconst_1
    //   2153: fconst_0
    //   2154: iconst_0
    //   2155: invokespecial 949	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   2158: iload 8
    //   2160: istore 31
    //   2162: iload 21
    //   2164: istore 9
    //   2166: aload_0
    //   2167: aload 33
    //   2169: putfield 1522	org/telegram/ui/Cells/ChatMessageCell:siteNameLayout	Landroid/text/StaticLayout;
    //   2172: iload 8
    //   2174: istore 31
    //   2176: iload 21
    //   2178: istore 9
    //   2180: aload_0
    //   2181: getfield 1522	org/telegram/ui/Cells/ChatMessageCell:siteNameLayout	Landroid/text/StaticLayout;
    //   2184: iconst_0
    //   2185: invokevirtual 659	android/text/StaticLayout:getLineLeft	(I)F
    //   2188: fconst_0
    //   2189: fcmpl
    //   2190: ifeq +1651 -> 3841
    //   2193: iconst_1
    //   2194: istore_3
    //   2195: iload 8
    //   2197: istore 31
    //   2199: iload 21
    //   2201: istore 9
    //   2203: aload_0
    //   2204: iload_3
    //   2205: putfield 1562	org/telegram/ui/Cells/ChatMessageCell:siteNameRtl	Z
    //   2208: iload 8
    //   2210: istore 31
    //   2212: iload 21
    //   2214: istore 9
    //   2216: aload_0
    //   2217: getfield 1522	org/telegram/ui/Cells/ChatMessageCell:siteNameLayout	Landroid/text/StaticLayout;
    //   2220: aload_0
    //   2221: getfield 1522	org/telegram/ui/Cells/ChatMessageCell:siteNameLayout	Landroid/text/StaticLayout;
    //   2224: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   2227: iconst_1
    //   2228: isub
    //   2229: invokevirtual 1525	android/text/StaticLayout:getLineBottom	(I)I
    //   2232: istore 6
    //   2234: iload 8
    //   2236: istore 31
    //   2238: iload 21
    //   2240: istore 9
    //   2242: aload_0
    //   2243: aload_0
    //   2244: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2247: iload 6
    //   2249: iadd
    //   2250: putfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2253: iload 8
    //   2255: istore 31
    //   2257: iload 21
    //   2259: istore 9
    //   2261: aload_0
    //   2262: aload_0
    //   2263: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2266: iload 6
    //   2268: iadd
    //   2269: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2272: iconst_0
    //   2273: iload 6
    //   2275: iadd
    //   2276: istore 8
    //   2278: iload 8
    //   2280: istore 31
    //   2282: iload 21
    //   2284: istore 9
    //   2286: aload_0
    //   2287: getfield 1522	org/telegram/ui/Cells/ChatMessageCell:siteNameLayout	Landroid/text/StaticLayout;
    //   2290: invokevirtual 3366	android/text/StaticLayout:getWidth	()I
    //   2293: istore 6
    //   2295: iload 8
    //   2297: istore 31
    //   2299: iload 21
    //   2301: istore 9
    //   2303: aload_0
    //   2304: iload 6
    //   2306: putfield 1564	org/telegram/ui/Cells/ChatMessageCell:siteNameWidth	I
    //   2309: iload 8
    //   2311: istore 31
    //   2313: iload 21
    //   2315: istore 9
    //   2317: iload 21
    //   2319: iload 6
    //   2321: iload 29
    //   2323: iadd
    //   2324: invokestatic 486	java/lang/Math:max	(II)I
    //   2327: istore 21
    //   2329: iload 8
    //   2331: istore 31
    //   2333: iload 21
    //   2335: istore 9
    //   2337: iconst_0
    //   2338: iload 6
    //   2340: iload 29
    //   2342: iadd
    //   2343: invokestatic 486	java/lang/Math:max	(II)I
    //   2346: istore 6
    //   2348: iload 21
    //   2350: istore 9
    //   2352: iload 8
    //   2354: istore 31
    //   2356: iconst_0
    //   2357: istore 34
    //   2359: iconst_0
    //   2360: istore 35
    //   2362: iconst_0
    //   2363: istore 36
    //   2365: iconst_0
    //   2366: istore 12
    //   2368: iload 9
    //   2370: istore 8
    //   2372: iload 6
    //   2374: istore 22
    //   2376: iload 30
    //   2378: istore 21
    //   2380: iload 36
    //   2382: istore 30
    //   2384: aload 24
    //   2386: ifnull +1580 -> 3966
    //   2389: aload_0
    //   2390: ldc_w 3367
    //   2393: putfield 1597	org/telegram/ui/Cells/ChatMessageCell:titleX	I
    //   2396: aload_0
    //   2397: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2400: ifeq +29 -> 2429
    //   2403: aload_0
    //   2404: aload_0
    //   2405: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2408: fconst_2
    //   2409: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2412: iadd
    //   2413: putfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2416: aload_0
    //   2417: aload_0
    //   2418: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2421: fconst_2
    //   2422: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2425: iadd
    //   2426: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2429: iconst_0
    //   2430: istore 36
    //   2432: aload_0
    //   2433: getfield 1637	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   2436: ifeq +8 -> 2444
    //   2439: aload 26
    //   2441: ifnonnull +1419 -> 3860
    //   2444: aload_0
    //   2445: aload 24
    //   2447: getstatic 1554	org/telegram/ui/ActionBar/Theme:chat_replyNamePaint	Landroid/text/TextPaint;
    //   2450: iload 32
    //   2452: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   2455: fconst_1
    //   2456: fconst_1
    //   2457: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2460: i2f
    //   2461: iconst_0
    //   2462: getstatic 1217	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   2465: iload 32
    //   2467: iconst_4
    //   2468: invokestatic 1315	org/telegram/ui/Components/StaticLayoutEx:createStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZLandroid/text/TextUtils$TruncateAt;II)Landroid/text/StaticLayout;
    //   2471: putfield 1595	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   2474: iconst_3
    //   2475: istore 8
    //   2477: iload 9
    //   2479: istore 21
    //   2481: iload 6
    //   2483: istore 35
    //   2485: iload 34
    //   2487: istore 22
    //   2489: aload_0
    //   2490: getfield 1595	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   2493: aload_0
    //   2494: getfield 1595	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   2497: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   2500: iconst_1
    //   2501: isub
    //   2502: invokevirtual 1525	android/text/StaticLayout:getLineBottom	(I)I
    //   2505: istore 30
    //   2507: iload 9
    //   2509: istore 21
    //   2511: iload 6
    //   2513: istore 35
    //   2515: iload 34
    //   2517: istore 22
    //   2519: aload_0
    //   2520: aload_0
    //   2521: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2524: iload 30
    //   2526: iadd
    //   2527: putfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2530: iload 9
    //   2532: istore 21
    //   2534: iload 6
    //   2536: istore 35
    //   2538: iload 34
    //   2540: istore 22
    //   2542: aload_0
    //   2543: aload_0
    //   2544: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2547: iload 30
    //   2549: iadd
    //   2550: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2553: iconst_0
    //   2554: istore 34
    //   2556: iload 12
    //   2558: istore 30
    //   2560: iload 9
    //   2562: istore 21
    //   2564: iload 6
    //   2566: istore 35
    //   2568: iload 30
    //   2570: istore 22
    //   2572: iload 34
    //   2574: aload_0
    //   2575: getfield 1595	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   2578: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   2581: if_icmpge +1969 -> 4550
    //   2584: iload 9
    //   2586: istore 21
    //   2588: iload 6
    //   2590: istore 35
    //   2592: iload 30
    //   2594: istore 22
    //   2596: aload_0
    //   2597: getfield 1595	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   2600: iload 34
    //   2602: invokevirtual 659	android/text/StaticLayout:getLineLeft	(I)F
    //   2605: f2i
    //   2606: istore 37
    //   2608: iload 37
    //   2610: ifeq +6 -> 2616
    //   2613: iconst_1
    //   2614: istore 30
    //   2616: iload 9
    //   2618: istore 21
    //   2620: iload 6
    //   2622: istore 35
    //   2624: iload 30
    //   2626: istore 22
    //   2628: aload_0
    //   2629: getfield 1597	org/telegram/ui/Cells/ChatMessageCell:titleX	I
    //   2632: ldc_w 3367
    //   2635: if_icmpne +1271 -> 3906
    //   2638: iload 9
    //   2640: istore 21
    //   2642: iload 6
    //   2644: istore 35
    //   2646: iload 30
    //   2648: istore 22
    //   2650: aload_0
    //   2651: iload 37
    //   2653: ineg
    //   2654: putfield 1597	org/telegram/ui/Cells/ChatMessageCell:titleX	I
    //   2657: iload 37
    //   2659: ifeq +1856 -> 4515
    //   2662: iload 9
    //   2664: istore 21
    //   2666: iload 6
    //   2668: istore 35
    //   2670: iload 30
    //   2672: istore 22
    //   2674: aload_0
    //   2675: getfield 1595	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   2678: invokevirtual 3366	android/text/StaticLayout:getWidth	()I
    //   2681: iload 37
    //   2683: isub
    //   2684: istore 12
    //   2686: iload 34
    //   2688: iload 36
    //   2690: if_icmplt +35 -> 2725
    //   2693: iload 12
    //   2695: istore 38
    //   2697: iload 37
    //   2699: ifeq +49 -> 2748
    //   2702: iload 12
    //   2704: istore 38
    //   2706: iload 9
    //   2708: istore 21
    //   2710: iload 6
    //   2712: istore 35
    //   2714: iload 30
    //   2716: istore 22
    //   2718: aload_0
    //   2719: getfield 1637	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   2722: ifeq +26 -> 2748
    //   2725: iload 9
    //   2727: istore 21
    //   2729: iload 6
    //   2731: istore 35
    //   2733: iload 30
    //   2735: istore 22
    //   2737: iload 12
    //   2739: ldc_w 2004
    //   2742: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2745: iadd
    //   2746: istore 38
    //   2748: iload 9
    //   2750: istore 21
    //   2752: iload 6
    //   2754: istore 35
    //   2756: iload 30
    //   2758: istore 22
    //   2760: iload 9
    //   2762: iload 38
    //   2764: iload 29
    //   2766: iadd
    //   2767: invokestatic 486	java/lang/Math:max	(II)I
    //   2770: istore 9
    //   2772: iload 9
    //   2774: istore 21
    //   2776: iload 6
    //   2778: istore 35
    //   2780: iload 30
    //   2782: istore 22
    //   2784: iload 6
    //   2786: iload 38
    //   2788: iload 29
    //   2790: iadd
    //   2791: invokestatic 486	java/lang/Math:max	(II)I
    //   2794: istore 6
    //   2796: iinc 34 1
    //   2799: goto -239 -> 2560
    //   2802: iconst_0
    //   2803: istore 5
    //   2805: goto -2749 -> 56
    //   2808: iconst_0
    //   2809: istore 6
    //   2811: goto -2737 -> 74
    //   2814: iconst_0
    //   2815: istore 7
    //   2817: goto -2718 -> 99
    //   2820: iconst_0
    //   2821: istore 8
    //   2823: goto -2713 -> 110
    //   2826: aconst_null
    //   2827: astore 10
    //   2829: goto -2672 -> 157
    //   2832: iconst_0
    //   2833: istore 9
    //   2835: goto -2666 -> 169
    //   2838: aload_0
    //   2839: aconst_null
    //   2840: putfield 705	org/telegram/ui/Cells/ChatMessageCell:currentMessagesGroup	Lorg/telegram/messenger/MessageObject$GroupedMessages;
    //   2843: aload_0
    //   2844: aconst_null
    //   2845: putfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   2848: goto -2548 -> 300
    //   2851: iconst_0
    //   2852: istore_3
    //   2853: goto -2525 -> 328
    //   2856: iconst_0
    //   2857: istore_3
    //   2858: goto -2496 -> 362
    //   2861: iconst_0
    //   2862: istore_3
    //   2863: goto -2395 -> 468
    //   2866: aload_1
    //   2867: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2870: getfield 1037	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   2873: getfield 1040	org/telegram/tgnet/TLRPC$Peer:channel_id	I
    //   2876: ifeq +32 -> 2908
    //   2879: aload_1
    //   2880: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   2883: ifne +25 -> 2908
    //   2886: iconst_1
    //   2887: istore_3
    //   2888: aload_0
    //   2889: iload_3
    //   2890: putfield 2421	org/telegram/ui/Cells/ChatMessageCell:drawName	Z
    //   2893: invokestatic 2258	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   2896: ldc_w 3368
    //   2899: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2902: isub
    //   2903: istore 14
    //   2905: goto -1920 -> 985
    //   2908: iconst_0
    //   2909: istore_3
    //   2910: goto -22 -> 2888
    //   2913: aload_0
    //   2914: getfield 2040	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   2917: ifeq +49 -> 2966
    //   2920: aload_1
    //   2921: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   2924: ifne +42 -> 2966
    //   2927: aload_1
    //   2928: invokevirtual 2043	org/telegram/messenger/MessageObject:needDrawAvatar	()Z
    //   2931: ifeq +35 -> 2966
    //   2934: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   2937: getfield 2253	android/graphics/Point:x	I
    //   2940: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   2943: getfield 2262	android/graphics/Point:y	I
    //   2946: invokestatic 1195	java/lang/Math:min	(II)I
    //   2949: ldc_w 3304
    //   2952: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2955: isub
    //   2956: istore 14
    //   2958: aload_0
    //   2959: iconst_1
    //   2960: putfield 2421	org/telegram/ui/Cells/ChatMessageCell:drawName	Z
    //   2963: goto -1978 -> 985
    //   2966: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   2969: getfield 2253	android/graphics/Point:x	I
    //   2972: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   2975: getfield 2262	android/graphics/Point:y	I
    //   2978: invokestatic 1195	java/lang/Math:min	(II)I
    //   2981: ldc_w 3368
    //   2984: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2987: isub
    //   2988: istore 14
    //   2990: aload_1
    //   2991: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   2994: getfield 1037	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   2997: getfield 1040	org/telegram/tgnet/TLRPC$Peer:channel_id	I
    //   3000: ifeq +20 -> 3020
    //   3003: aload_1
    //   3004: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   3007: ifne +13 -> 3020
    //   3010: iconst_1
    //   3011: istore_3
    //   3012: aload_0
    //   3013: iload_3
    //   3014: putfield 2421	org/telegram/ui/Cells/ChatMessageCell:drawName	Z
    //   3017: goto -2032 -> 985
    //   3020: iconst_0
    //   3021: istore_3
    //   3022: goto -10 -> 3012
    //   3025: ldc_w 3369
    //   3028: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3031: istore 9
    //   3033: goto -2003 -> 1030
    //   3036: iconst_0
    //   3037: istore_3
    //   3038: goto -1923 -> 1115
    //   3041: iconst_0
    //   3042: istore_3
    //   3043: goto -1878 -> 1165
    //   3046: iconst_0
    //   3047: istore_3
    //   3048: goto -1853 -> 1195
    //   3051: aconst_null
    //   3052: astore_2
    //   3053: goto -1829 -> 1224
    //   3056: aconst_null
    //   3057: astore 10
    //   3059: goto -1813 -> 1246
    //   3062: ldc_w 3371
    //   3065: aload 10
    //   3067: invokevirtual 2357	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3070: ifeq +20 -> 3090
    //   3073: aload_0
    //   3074: iconst_1
    //   3075: putfield 556	org/telegram/ui/Cells/ChatMessageCell:drawInstantView	Z
    //   3078: aload_0
    //   3079: iconst_2
    //   3080: putfield 849	org/telegram/ui/Cells/ChatMessageCell:drawInstantViewType	I
    //   3083: iload 9
    //   3085: istore 6
    //   3087: goto -1809 -> 1278
    //   3090: iload 9
    //   3092: istore 6
    //   3094: ldc_w 3373
    //   3097: aload 10
    //   3099: invokevirtual 2357	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3102: ifeq -1824 -> 1278
    //   3105: aload_0
    //   3106: iconst_1
    //   3107: putfield 556	org/telegram/ui/Cells/ChatMessageCell:drawInstantView	Z
    //   3110: aload_0
    //   3111: iconst_3
    //   3112: putfield 849	org/telegram/ui/Cells/ChatMessageCell:drawInstantViewType	I
    //   3115: iload 9
    //   3117: istore 6
    //   3119: goto -1841 -> 1278
    //   3122: iload 9
    //   3124: istore 6
    //   3126: aload_2
    //   3127: ifnull -1849 -> 1278
    //   3130: aload_2
    //   3131: invokevirtual 1276	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   3134: astore_2
    //   3135: aload_2
    //   3136: ldc_w 3354
    //   3139: invokevirtual 2357	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3142: ifne +28 -> 3170
    //   3145: aload_2
    //   3146: ldc_w 3375
    //   3149: invokevirtual 2357	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3152: ifne +18 -> 3170
    //   3155: iload 9
    //   3157: istore 6
    //   3159: ldc_w 3377
    //   3162: aload 10
    //   3164: invokevirtual 2357	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3167: ifeq -1889 -> 1278
    //   3170: iload 9
    //   3172: istore 6
    //   3174: aload_1
    //   3175: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   3178: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   3181: getfield 828	org/telegram/tgnet/TLRPC$MessageMedia:webpage	Lorg/telegram/tgnet/TLRPC$WebPage;
    //   3184: getfield 3308	org/telegram/tgnet/TLRPC$WebPage:cached_page	Lorg/telegram/tgnet/TLRPC$Page;
    //   3187: instanceof 3379
    //   3190: ifeq -1912 -> 1278
    //   3193: aload_1
    //   3194: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   3197: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   3200: getfield 828	org/telegram/tgnet/TLRPC$MessageMedia:webpage	Lorg/telegram/tgnet/TLRPC$WebPage;
    //   3203: getfield 3380	org/telegram/tgnet/TLRPC$WebPage:photo	Lorg/telegram/tgnet/TLRPC$Photo;
    //   3206: instanceof 3382
    //   3209: ifne +26 -> 3235
    //   3212: iload 9
    //   3214: istore 6
    //   3216: aload_1
    //   3217: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   3220: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   3223: getfield 828	org/telegram/tgnet/TLRPC$MessageMedia:webpage	Lorg/telegram/tgnet/TLRPC$WebPage;
    //   3226: getfield 1147	org/telegram/tgnet/TLRPC$WebPage:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   3229: invokestatic 1250	org/telegram/messenger/MessageObject:isVideoDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   3232: ifeq -1954 -> 1278
    //   3235: aload_0
    //   3236: iconst_0
    //   3237: putfield 556	org/telegram/ui/Cells/ChatMessageCell:drawInstantView	Z
    //   3240: iconst_1
    //   3241: istore 6
    //   3243: aload_1
    //   3244: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   3247: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   3250: getfield 828	org/telegram/tgnet/TLRPC$MessageMedia:webpage	Lorg/telegram/tgnet/TLRPC$WebPage;
    //   3253: getfield 3308	org/telegram/tgnet/TLRPC$WebPage:cached_page	Lorg/telegram/tgnet/TLRPC$Page;
    //   3256: getfield 3387	org/telegram/tgnet/TLRPC$Page:blocks	Ljava/util/ArrayList;
    //   3259: astore_2
    //   3260: iconst_1
    //   3261: istore 9
    //   3263: iconst_0
    //   3264: istore 8
    //   3266: iload 8
    //   3268: aload_2
    //   3269: invokevirtual 590	java/util/ArrayList:size	()I
    //   3272: if_icmpge +65 -> 3337
    //   3275: aload_2
    //   3276: iload 8
    //   3278: invokevirtual 594	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   3281: checkcast 3389	org/telegram/tgnet/TLRPC$PageBlock
    //   3284: astore 10
    //   3286: aload 10
    //   3288: instanceof 3391
    //   3291: ifeq +22 -> 3313
    //   3294: aload 10
    //   3296: checkcast 3391	org/telegram/tgnet/TLRPC$TL_pageBlockSlideshow
    //   3299: getfield 3394	org/telegram/tgnet/TLRPC$TL_pageBlockSlideshow:items	Ljava/util/ArrayList;
    //   3302: invokevirtual 590	java/util/ArrayList:size	()I
    //   3305: istore 9
    //   3307: iinc 8 1
    //   3310: goto -44 -> 3266
    //   3313: aload 10
    //   3315: instanceof 3396
    //   3318: ifeq -11 -> 3307
    //   3321: aload 10
    //   3323: checkcast 3396	org/telegram/tgnet/TLRPC$TL_pageBlockCollage
    //   3326: getfield 3397	org/telegram/tgnet/TLRPC$TL_pageBlockCollage:items	Ljava/util/ArrayList;
    //   3329: invokevirtual 590	java/util/ArrayList:size	()I
    //   3332: istore 9
    //   3334: goto -27 -> 3307
    //   3337: ldc_w 3399
    //   3340: ldc_w 3400
    //   3343: iconst_2
    //   3344: anewarray 1242	java/lang/Object
    //   3347: dup
    //   3348: iconst_0
    //   3349: iconst_1
    //   3350: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3353: aastore
    //   3354: dup
    //   3355: iconst_1
    //   3356: iload 9
    //   3358: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3361: aastore
    //   3362: invokestatic 3404	org/telegram/messenger/LocaleController:formatString	(Ljava/lang/String;I[Ljava/lang/Object;)Ljava/lang/String;
    //   3365: astore_2
    //   3366: aload_0
    //   3367: getstatic 1664	org/telegram/ui/ActionBar/Theme:chat_durationPaint	Landroid/text/TextPaint;
    //   3370: aload_2
    //   3371: invokevirtual 1178	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   3374: f2d
    //   3375: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   3378: d2i
    //   3379: putfield 1645	org/telegram/ui/Cells/ChatMessageCell:photosCountWidth	I
    //   3382: aload_0
    //   3383: new 350	android/text/StaticLayout
    //   3386: dup
    //   3387: aload_2
    //   3388: getstatic 1664	org/telegram/ui/ActionBar/Theme:chat_durationPaint	Landroid/text/TextPaint;
    //   3391: aload_0
    //   3392: getfield 1645	org/telegram/ui/Cells/ChatMessageCell:photosCountWidth	I
    //   3395: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   3398: fconst_1
    //   3399: fconst_0
    //   3400: iconst_0
    //   3401: invokespecial 949	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   3404: putfield 1643	org/telegram/ui/Cells/ChatMessageCell:photosCountLayout	Landroid/text/StaticLayout;
    //   3407: goto -2129 -> 1278
    //   3410: ldc_w 1837
    //   3413: astore_2
    //   3414: goto -2070 -> 1344
    //   3417: aload_0
    //   3418: getfield 792	org/telegram/ui/Cells/ChatMessageCell:instantViewSelectorDrawable	Landroid/graphics/drawable/Drawable;
    //   3421: astore 10
    //   3423: aload_0
    //   3424: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   3427: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   3430: ifeq +24 -> 3454
    //   3433: ldc_w 1692
    //   3436: astore_2
    //   3437: aload 10
    //   3439: aload_2
    //   3440: invokestatic 1511	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   3443: ldc_w 3326
    //   3446: iand
    //   3447: iconst_1
    //   3448: invokestatic 3408	org/telegram/ui/ActionBar/Theme:setSelectorDrawableColor	(Landroid/graphics/drawable/Drawable;IZ)V
    //   3451: goto -2051 -> 1400
    //   3454: ldc_w 1837
    //   3457: astore_2
    //   3458: goto -21 -> 3437
    //   3461: aload_0
    //   3462: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   3465: aload_1
    //   3466: getfield 472	org/telegram/messenger/MessageObject:lastLineWidth	I
    //   3469: isub
    //   3470: istore 9
    //   3472: iload 9
    //   3474: iflt +34 -> 3508
    //   3477: iload 9
    //   3479: iload 19
    //   3481: if_icmpgt +27 -> 3508
    //   3484: aload_0
    //   3485: aload_0
    //   3486: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   3489: iload 19
    //   3491: iadd
    //   3492: iload 9
    //   3494: isub
    //   3495: ldc_w 487
    //   3498: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3501: iadd
    //   3502: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   3505: goto -2012 -> 1493
    //   3508: aload_0
    //   3509: aload_0
    //   3510: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   3513: aload_1
    //   3514: getfield 472	org/telegram/messenger/MessageObject:lastLineWidth	I
    //   3517: iload 19
    //   3519: iadd
    //   3520: invokestatic 486	java/lang/Math:max	(II)I
    //   3523: ldc_w 487
    //   3526: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3529: iadd
    //   3530: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   3533: goto -2040 -> 1493
    //   3536: ldc_w 3369
    //   3539: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3542: istore 9
    //   3544: goto -1997 -> 1547
    //   3547: iconst_0
    //   3548: istore 9
    //   3550: goto -1956 -> 1594
    //   3553: invokestatic 2258	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   3556: ldc_w 3368
    //   3559: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3562: isub
    //   3563: istore 9
    //   3565: goto -1822 -> 1743
    //   3568: aload_0
    //   3569: getfield 2040	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   3572: ifeq +47 -> 3619
    //   3575: aload_1
    //   3576: invokevirtual 2043	org/telegram/messenger/MessageObject:needDrawAvatar	()Z
    //   3579: ifeq +40 -> 3619
    //   3582: aload_0
    //   3583: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   3586: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   3589: ifne +30 -> 3619
    //   3592: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   3595: getfield 2253	android/graphics/Point:x	I
    //   3598: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   3601: getfield 2262	android/graphics/Point:y	I
    //   3604: invokestatic 1195	java/lang/Math:min	(II)I
    //   3607: ldc_w 3340
    //   3610: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3613: isub
    //   3614: istore 9
    //   3616: goto -1873 -> 1743
    //   3619: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   3622: getfield 2253	android/graphics/Point:x	I
    //   3625: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   3628: getfield 2262	android/graphics/Point:y	I
    //   3631: invokestatic 1195	java/lang/Math:min	(II)I
    //   3634: ldc_w 3368
    //   3637: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3640: isub
    //   3641: istore 9
    //   3643: goto -1900 -> 1743
    //   3646: iconst_0
    //   3647: istore 11
    //   3649: goto -1701 -> 1948
    //   3652: iconst_0
    //   3653: istore_3
    //   3654: goto -1634 -> 2020
    //   3657: aload_0
    //   3658: getfield 465	org/telegram/ui/Cells/ChatMessageCell:hasInvoicePreview	Z
    //   3661: ifeq +87 -> 3748
    //   3664: aload_1
    //   3665: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   3668: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   3671: checkcast 1031	org/telegram/tgnet/TLRPC$TL_messageMediaInvoice
    //   3674: astore_2
    //   3675: aload_1
    //   3676: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   3679: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   3682: getfield 2358	org/telegram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   3685: astore 23
    //   3687: aconst_null
    //   3688: astore 24
    //   3690: aconst_null
    //   3691: astore 26
    //   3693: aconst_null
    //   3694: astore 27
    //   3696: aconst_null
    //   3697: astore 25
    //   3699: aconst_null
    //   3700: astore 20
    //   3702: aload_2
    //   3703: getfield 3411	org/telegram/tgnet/TLRPC$TL_messageMediaInvoice:photo	Lorg/telegram/tgnet/TLRPC$WebDocument;
    //   3706: instanceof 3413
    //   3709: ifeq +34 -> 3743
    //   3712: aload_2
    //   3713: getfield 3411	org/telegram/tgnet/TLRPC$TL_messageMediaInvoice:photo	Lorg/telegram/tgnet/TLRPC$WebDocument;
    //   3716: checkcast 3413	org/telegram/tgnet/TLRPC$TL_webDocument
    //   3719: astore_2
    //   3720: iconst_0
    //   3721: istore 28
    //   3723: ldc_w 3415
    //   3726: astore 10
    //   3728: aload_0
    //   3729: iconst_0
    //   3730: putfield 1637	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   3733: iconst_0
    //   3734: istore 11
    //   3736: iload 8
    //   3738: istore 9
    //   3740: goto -1713 -> 2027
    //   3743: aconst_null
    //   3744: astore_2
    //   3745: goto -25 -> 3720
    //   3748: aload_1
    //   3749: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   3752: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   3755: getfield 2667	org/telegram/tgnet/TLRPC$MessageMedia:game	Lorg/telegram/tgnet/TLRPC$TL_game;
    //   3758: astore_2
    //   3759: aload_2
    //   3760: getfield 2670	org/telegram/tgnet/TLRPC$TL_game:title	Ljava/lang/String;
    //   3763: astore 23
    //   3765: aconst_null
    //   3766: astore 24
    //   3768: aload_1
    //   3769: getfield 1114	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   3772: invokestatic 847	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   3775: ifeq +49 -> 3824
    //   3778: aload_2
    //   3779: getfield 3416	org/telegram/tgnet/TLRPC$TL_game:description	Ljava/lang/String;
    //   3782: astore 26
    //   3784: aload_2
    //   3785: getfield 3417	org/telegram/tgnet/TLRPC$TL_game:photo	Lorg/telegram/tgnet/TLRPC$Photo;
    //   3788: astore 27
    //   3790: aconst_null
    //   3791: astore 25
    //   3793: aload_2
    //   3794: getfield 3418	org/telegram/tgnet/TLRPC$TL_game:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   3797: astore 20
    //   3799: iconst_0
    //   3800: istore 28
    //   3802: ldc_w 3419
    //   3805: astore 10
    //   3807: aload_0
    //   3808: iconst_0
    //   3809: putfield 1637	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   3812: iconst_0
    //   3813: istore 11
    //   3815: aconst_null
    //   3816: astore_2
    //   3817: iload 8
    //   3819: istore 9
    //   3821: goto -1794 -> 2027
    //   3824: aconst_null
    //   3825: astore 26
    //   3827: goto -43 -> 3784
    //   3830: ldc_w 587
    //   3833: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3836: istore 29
    //   3838: goto -1801 -> 2037
    //   3841: iconst_0
    //   3842: istore_3
    //   3843: goto -1648 -> 2195
    //   3846: astore 33
    //   3848: aload 33
    //   3850: invokestatic 714	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   3853: iload 22
    //   3855: istore 6
    //   3857: goto -1501 -> 2356
    //   3860: iconst_3
    //   3861: istore 36
    //   3863: aload_0
    //   3864: aload 24
    //   3866: getstatic 1554	org/telegram/ui/ActionBar/Theme:chat_replyNamePaint	Landroid/text/TextPaint;
    //   3869: iload 32
    //   3871: iload 32
    //   3873: ldc_w 2004
    //   3876: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3879: isub
    //   3880: iconst_3
    //   3881: iconst_4
    //   3882: invokestatic 3421	org/telegram/ui/Cells/ChatMessageCell:generateStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;IIII)Landroid/text/StaticLayout;
    //   3885: putfield 1595	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   3888: aload_0
    //   3889: getfield 1595	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   3892: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   3895: istore 8
    //   3897: iconst_3
    //   3898: iload 8
    //   3900: isub
    //   3901: istore 8
    //   3903: goto -1426 -> 2477
    //   3906: iload 9
    //   3908: istore 21
    //   3910: iload 6
    //   3912: istore 35
    //   3914: iload 30
    //   3916: istore 22
    //   3918: aload_0
    //   3919: aload_0
    //   3920: getfield 1597	org/telegram/ui/Cells/ChatMessageCell:titleX	I
    //   3923: iload 37
    //   3925: ineg
    //   3926: invokestatic 486	java/lang/Math:max	(II)I
    //   3929: putfield 1597	org/telegram/ui/Cells/ChatMessageCell:titleX	I
    //   3932: goto -1275 -> 2657
    //   3935: astore 33
    //   3937: iload 35
    //   3939: istore 6
    //   3941: iload 21
    //   3943: istore 9
    //   3945: aload 33
    //   3947: invokestatic 714	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   3950: iload 22
    //   3952: istore 30
    //   3954: iload 8
    //   3956: istore 21
    //   3958: iload 6
    //   3960: istore 22
    //   3962: iload 9
    //   3964: istore 8
    //   3966: iconst_0
    //   3967: istore 9
    //   3969: iconst_0
    //   3970: istore 35
    //   3972: iconst_0
    //   3973: istore 12
    //   3975: iconst_0
    //   3976: istore 6
    //   3978: aload 25
    //   3980: ifnull +15415 -> 19395
    //   3983: aload 24
    //   3985: ifnonnull +15410 -> 19395
    //   3988: aload_0
    //   3989: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   3992: ifeq +29 -> 4021
    //   3995: aload_0
    //   3996: aload_0
    //   3997: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   4000: fconst_2
    //   4001: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4004: iadd
    //   4005: putfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   4008: aload_0
    //   4009: aload_0
    //   4010: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4013: fconst_2
    //   4014: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4017: iadd
    //   4018: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4021: iload 21
    //   4023: iconst_3
    //   4024: if_icmpne +541 -> 4565
    //   4027: aload_0
    //   4028: getfield 1637	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   4031: ifeq +8 -> 4039
    //   4034: aload 26
    //   4036: ifnonnull +529 -> 4565
    //   4039: new 350	android/text/StaticLayout
    //   4042: astore 24
    //   4044: aload 24
    //   4046: aload 25
    //   4048: getstatic 1554	org/telegram/ui/ActionBar/Theme:chat_replyNamePaint	Landroid/text/TextPaint;
    //   4051: iload 32
    //   4053: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   4056: fconst_1
    //   4057: fconst_0
    //   4058: iconst_0
    //   4059: invokespecial 949	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   4062: aload_0
    //   4063: aload 24
    //   4065: putfield 1599	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   4068: iload 21
    //   4070: istore 9
    //   4072: iload 12
    //   4074: istore 35
    //   4076: iload 8
    //   4078: istore 21
    //   4080: aload_0
    //   4081: getfield 1599	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   4084: aload_0
    //   4085: getfield 1599	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   4088: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   4091: iconst_1
    //   4092: isub
    //   4093: invokevirtual 1525	android/text/StaticLayout:getLineBottom	(I)I
    //   4096: istore 36
    //   4098: iload 12
    //   4100: istore 35
    //   4102: iload 8
    //   4104: istore 21
    //   4106: aload_0
    //   4107: aload_0
    //   4108: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   4111: iload 36
    //   4113: iadd
    //   4114: putfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   4117: iload 12
    //   4119: istore 35
    //   4121: iload 8
    //   4123: istore 21
    //   4125: aload_0
    //   4126: aload_0
    //   4127: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4130: iload 36
    //   4132: iadd
    //   4133: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4136: iload 12
    //   4138: istore 35
    //   4140: iload 8
    //   4142: istore 21
    //   4144: aload_0
    //   4145: getfield 1599	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   4148: iconst_0
    //   4149: invokevirtual 659	android/text/StaticLayout:getLineLeft	(I)F
    //   4152: f2i
    //   4153: istore 36
    //   4155: iload 12
    //   4157: istore 35
    //   4159: iload 8
    //   4161: istore 21
    //   4163: aload_0
    //   4164: iload 36
    //   4166: ineg
    //   4167: putfield 1601	org/telegram/ui/Cells/ChatMessageCell:authorX	I
    //   4170: iload 36
    //   4172: ifeq +438 -> 4610
    //   4175: iload 12
    //   4177: istore 35
    //   4179: iload 8
    //   4181: istore 21
    //   4183: aload_0
    //   4184: getfield 1599	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   4187: invokevirtual 3366	android/text/StaticLayout:getWidth	()I
    //   4190: iload 36
    //   4192: isub
    //   4193: istore 12
    //   4195: iconst_1
    //   4196: istore 6
    //   4198: iload 6
    //   4200: istore 35
    //   4202: iload 8
    //   4204: istore 21
    //   4206: iload 8
    //   4208: iload 12
    //   4210: iload 29
    //   4212: iadd
    //   4213: invokestatic 486	java/lang/Math:max	(II)I
    //   4216: istore 8
    //   4218: iload 6
    //   4220: istore 35
    //   4222: iload 8
    //   4224: istore 21
    //   4226: iload 22
    //   4228: iload 12
    //   4230: iload 29
    //   4232: iadd
    //   4233: invokestatic 486	java/lang/Math:max	(II)I
    //   4236: istore 12
    //   4238: iload 12
    //   4240: istore 35
    //   4242: iload 6
    //   4244: istore 21
    //   4246: iload 9
    //   4248: istore 6
    //   4250: iload 8
    //   4252: istore 9
    //   4254: aload 26
    //   4256: ifnull +465 -> 4721
    //   4259: iload 8
    //   4261: istore 22
    //   4263: aload_0
    //   4264: iconst_0
    //   4265: putfield 738	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   4268: iload 8
    //   4270: istore 22
    //   4272: aload_0
    //   4273: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   4276: invokevirtual 3424	org/telegram/messenger/MessageObject:generateLinkDescription	()V
    //   4279: iload 8
    //   4281: istore 22
    //   4283: aload_0
    //   4284: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   4287: ifeq +37 -> 4324
    //   4290: iload 8
    //   4292: istore 22
    //   4294: aload_0
    //   4295: aload_0
    //   4296: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   4299: fconst_2
    //   4300: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4303: iadd
    //   4304: putfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   4307: iload 8
    //   4309: istore 22
    //   4311: aload_0
    //   4312: aload_0
    //   4313: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4316: fconst_2
    //   4317: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4320: iadd
    //   4321: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4324: iconst_0
    //   4325: istore 9
    //   4327: iload 6
    //   4329: iconst_3
    //   4330: if_icmpne +336 -> 4666
    //   4333: iload 8
    //   4335: istore 22
    //   4337: aload_0
    //   4338: getfield 1637	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   4341: ifne +325 -> 4666
    //   4344: iload 8
    //   4346: istore 22
    //   4348: aload_0
    //   4349: aload_1
    //   4350: getfield 741	org/telegram/messenger/MessageObject:linkDescription	Ljava/lang/CharSequence;
    //   4353: getstatic 1593	org/telegram/ui/ActionBar/Theme:chat_replyTextPaint	Landroid/text/TextPaint;
    //   4356: iload 32
    //   4358: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   4361: fconst_1
    //   4362: fconst_1
    //   4363: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4366: i2f
    //   4367: iconst_0
    //   4368: getstatic 1217	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   4371: iload 32
    //   4373: bipush 6
    //   4375: invokestatic 1315	org/telegram/ui/Components/StaticLayoutEx:createStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZLandroid/text/TextUtils$TruncateAt;II)Landroid/text/StaticLayout;
    //   4378: putfield 732	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   4381: iload 9
    //   4383: istore 6
    //   4385: iload 8
    //   4387: istore 22
    //   4389: aload_0
    //   4390: getfield 732	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   4393: aload_0
    //   4394: getfield 732	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   4397: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   4400: iconst_1
    //   4401: isub
    //   4402: invokevirtual 1525	android/text/StaticLayout:getLineBottom	(I)I
    //   4405: istore 9
    //   4407: iload 8
    //   4409: istore 22
    //   4411: aload_0
    //   4412: aload_0
    //   4413: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   4416: iload 9
    //   4418: iadd
    //   4419: putfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   4422: iload 8
    //   4424: istore 22
    //   4426: aload_0
    //   4427: aload_0
    //   4428: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4431: iload 9
    //   4433: iadd
    //   4434: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4437: iconst_0
    //   4438: istore 12
    //   4440: iconst_0
    //   4441: istore 9
    //   4443: iload 8
    //   4445: istore 22
    //   4447: iload 9
    //   4449: aload_0
    //   4450: getfield 732	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   4453: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   4456: if_icmpge +2114 -> 6570
    //   4459: iload 8
    //   4461: istore 22
    //   4463: aload_0
    //   4464: getfield 732	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   4467: iload 9
    //   4469: invokevirtual 659	android/text/StaticLayout:getLineLeft	(I)F
    //   4472: f2d
    //   4473: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   4476: d2i
    //   4477: istore 36
    //   4479: iload 36
    //   4481: ifeq +28 -> 4509
    //   4484: iconst_1
    //   4485: istore 12
    //   4487: iload 8
    //   4489: istore 22
    //   4491: aload_0
    //   4492: getfield 738	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   4495: ifne +2054 -> 6549
    //   4498: iload 8
    //   4500: istore 22
    //   4502: aload_0
    //   4503: iload 36
    //   4505: ineg
    //   4506: putfield 738	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   4509: iinc 9 1
    //   4512: goto -69 -> 4443
    //   4515: iload 9
    //   4517: istore 21
    //   4519: iload 6
    //   4521: istore 35
    //   4523: iload 30
    //   4525: istore 22
    //   4527: aload_0
    //   4528: getfield 1595	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   4531: iload 34
    //   4533: invokevirtual 662	android/text/StaticLayout:getLineWidth	(I)F
    //   4536: f2d
    //   4537: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   4540: dstore 17
    //   4542: dload 17
    //   4544: d2i
    //   4545: istore 12
    //   4547: goto -1861 -> 2686
    //   4550: iload 8
    //   4552: istore 21
    //   4554: iload 9
    //   4556: istore 8
    //   4558: iload 6
    //   4560: istore 22
    //   4562: goto -596 -> 3966
    //   4565: aload_0
    //   4566: aload 25
    //   4568: getstatic 1554	org/telegram/ui/ActionBar/Theme:chat_replyNamePaint	Landroid/text/TextPaint;
    //   4571: iload 32
    //   4573: iload 32
    //   4575: ldc_w 2004
    //   4578: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4581: isub
    //   4582: iload 21
    //   4584: iconst_1
    //   4585: invokestatic 3421	org/telegram/ui/Cells/ChatMessageCell:generateStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;IIII)Landroid/text/StaticLayout;
    //   4588: putfield 1599	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   4591: aload_0
    //   4592: getfield 1599	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   4595: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   4598: istore 9
    //   4600: iload 21
    //   4602: iload 9
    //   4604: isub
    //   4605: istore 9
    //   4607: goto -535 -> 4072
    //   4610: iload 12
    //   4612: istore 35
    //   4614: iload 8
    //   4616: istore 21
    //   4618: aload_0
    //   4619: getfield 1599	org/telegram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   4622: iconst_0
    //   4623: invokevirtual 662	android/text/StaticLayout:getLineWidth	(I)F
    //   4626: f2d
    //   4627: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   4630: dstore 17
    //   4632: dload 17
    //   4634: d2i
    //   4635: istore 12
    //   4637: goto -439 -> 4198
    //   4640: astore 25
    //   4642: iload 21
    //   4644: istore 9
    //   4646: aload 25
    //   4648: invokestatic 714	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   4651: iload 9
    //   4653: istore 6
    //   4655: iload 35
    //   4657: istore 21
    //   4659: iload 22
    //   4661: istore 35
    //   4663: goto -413 -> 4250
    //   4666: iload 6
    //   4668: istore 9
    //   4670: iload 8
    //   4672: istore 22
    //   4674: aload_0
    //   4675: aload_1
    //   4676: getfield 741	org/telegram/messenger/MessageObject:linkDescription	Ljava/lang/CharSequence;
    //   4679: getstatic 1593	org/telegram/ui/ActionBar/Theme:chat_replyTextPaint	Landroid/text/TextPaint;
    //   4682: iload 32
    //   4684: iload 32
    //   4686: ldc_w 2004
    //   4689: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4692: isub
    //   4693: iload 6
    //   4695: bipush 6
    //   4697: invokestatic 3421	org/telegram/ui/Cells/ChatMessageCell:generateStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;IIII)Landroid/text/StaticLayout;
    //   4700: putfield 732	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   4703: iload 9
    //   4705: istore 6
    //   4707: goto -322 -> 4385
    //   4710: astore 26
    //   4712: aload 26
    //   4714: invokestatic 714	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   4717: iload 22
    //   4719: istore 9
    //   4721: iload 11
    //   4723: istore 21
    //   4725: iload 11
    //   4727: ifeq +44 -> 4771
    //   4730: aload_0
    //   4731: getfield 732	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   4734: ifnull +29 -> 4763
    //   4737: iload 11
    //   4739: istore 21
    //   4741: aload_0
    //   4742: getfield 732	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   4745: ifnull +26 -> 4771
    //   4748: iload 11
    //   4750: istore 21
    //   4752: aload_0
    //   4753: getfield 732	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   4756: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   4759: iconst_1
    //   4760: if_icmpne +11 -> 4771
    //   4763: iconst_0
    //   4764: istore 21
    //   4766: aload_0
    //   4767: iconst_0
    //   4768: putfield 1637	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   4771: iload 21
    //   4773: ifeq +2084 -> 6857
    //   4776: ldc_w 781
    //   4779: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4782: istore 8
    //   4784: aload 20
    //   4786: ifnull +3401 -> 8187
    //   4789: aload 20
    //   4791: invokestatic 3427	org/telegram/messenger/MessageObject:isRoundVideoDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   4794: ifeq +2070 -> 6864
    //   4797: aload_0
    //   4798: aload 20
    //   4800: getfield 1285	org/telegram/tgnet/TLRPC$Document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   4803: putfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   4806: aload_0
    //   4807: aload 20
    //   4809: putfield 1149	org/telegram/ui/Cells/ChatMessageCell:documentAttach	Lorg/telegram/tgnet/TLRPC$Document;
    //   4812: aload_0
    //   4813: bipush 7
    //   4815: putfield 498	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   4818: iload 9
    //   4820: istore 6
    //   4822: aload_0
    //   4823: getfield 498	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   4826: iconst_5
    //   4827: if_icmpeq +868 -> 5695
    //   4830: aload_0
    //   4831: getfield 498	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   4834: iconst_3
    //   4835: if_icmpeq +860 -> 5695
    //   4838: aload_0
    //   4839: getfield 498	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   4842: iconst_1
    //   4843: if_icmpeq +852 -> 5695
    //   4846: aload_0
    //   4847: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   4850: ifnonnull +7 -> 4857
    //   4853: aload_2
    //   4854: ifnull +4415 -> 9269
    //   4857: aload 10
    //   4859: ifnull +3479 -> 8338
    //   4862: aload 10
    //   4864: ldc_w 3428
    //   4867: invokevirtual 2357	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   4870: ifne +42 -> 4912
    //   4873: aload 10
    //   4875: ldc_w 3429
    //   4878: invokevirtual 2357	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   4881: ifeq +12 -> 4893
    //   4884: aload_0
    //   4885: getfield 498	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   4888: bipush 6
    //   4890: if_icmpne +22 -> 4912
    //   4893: aload 10
    //   4895: ldc_w 3431
    //   4898: invokevirtual 2357	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   4901: ifne +11 -> 4912
    //   4904: aload_0
    //   4905: getfield 498	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   4908: iconst_4
    //   4909: if_icmpne +3429 -> 8338
    //   4912: iconst_1
    //   4913: istore_3
    //   4914: aload_0
    //   4915: iload_3
    //   4916: putfield 783	org/telegram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   4919: aload_0
    //   4920: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   4923: ifeq +29 -> 4952
    //   4926: aload_0
    //   4927: aload_0
    //   4928: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   4931: fconst_2
    //   4932: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4935: iadd
    //   4936: putfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   4939: aload_0
    //   4940: aload_0
    //   4941: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4944: fconst_2
    //   4945: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4948: iadd
    //   4949: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4952: aload_0
    //   4953: getfield 498	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   4956: bipush 6
    //   4958: if_icmpne +3402 -> 8360
    //   4961: invokestatic 2226	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   4964: ifeq +3379 -> 8343
    //   4967: invokestatic 2258	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   4970: i2f
    //   4971: ldc_w 2766
    //   4974: fmul
    //   4975: f2i
    //   4976: istore 9
    //   4978: aload_0
    //   4979: getfield 465	org/telegram/ui/Cells/ChatMessageCell:hasInvoicePreview	Z
    //   4982: ifeq +3407 -> 8389
    //   4985: ldc_w 554
    //   4988: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   4991: istore 8
    //   4993: iload 6
    //   4995: iload 9
    //   4997: iload 8
    //   4999: isub
    //   5000: iload 29
    //   5002: iadd
    //   5003: invokestatic 486	java/lang/Math:max	(II)I
    //   5006: istore 11
    //   5008: aload_0
    //   5009: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5012: ifnull +3383 -> 8395
    //   5015: aload_0
    //   5016: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5019: iconst_m1
    //   5020: putfield 1407	org/telegram/tgnet/TLRPC$PhotoSize:size	I
    //   5023: aload_0
    //   5024: getfield 1404	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5027: ifnull +11 -> 5038
    //   5030: aload_0
    //   5031: getfield 1404	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   5034: iconst_m1
    //   5035: putfield 1407	org/telegram/tgnet/TLRPC$PhotoSize:size	I
    //   5038: iload 21
    //   5040: ifne +12 -> 5052
    //   5043: aload_0
    //   5044: getfield 498	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   5047: bipush 7
    //   5049: if_icmpne +3354 -> 8403
    //   5052: iload 9
    //   5054: istore 6
    //   5056: iload 9
    //   5058: istore 8
    //   5060: iload 6
    //   5062: istore 9
    //   5064: aload_0
    //   5065: getfield 1637	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   5068: ifeq +3544 -> 8612
    //   5071: ldc_w 1074
    //   5074: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5077: iload 31
    //   5079: iadd
    //   5080: aload_0
    //   5081: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5084: if_icmple +46 -> 5130
    //   5087: aload_0
    //   5088: aload_0
    //   5089: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5092: ldc_w 1074
    //   5095: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5098: iload 31
    //   5100: iadd
    //   5101: aload_0
    //   5102: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5105: isub
    //   5106: ldc_w 1629
    //   5109: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5112: iadd
    //   5113: iadd
    //   5114: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5117: aload_0
    //   5118: ldc_w 1074
    //   5121: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5124: iload 31
    //   5126: iadd
    //   5127: putfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5130: aload_0
    //   5131: aload_0
    //   5132: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5135: ldc_w 1629
    //   5138: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5141: isub
    //   5142: putfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5145: aload_0
    //   5146: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   5149: iconst_0
    //   5150: iconst_0
    //   5151: iload 8
    //   5153: iload 9
    //   5155: invokevirtual 1581	org/telegram/messenger/ImageReceiver:setImageCoords	(IIII)V
    //   5158: aload_0
    //   5159: getstatic 2340	java/util/Locale:US	Ljava/util/Locale;
    //   5162: ldc_w 3433
    //   5165: iconst_2
    //   5166: anewarray 1242	java/lang/Object
    //   5169: dup
    //   5170: iconst_0
    //   5171: iload 8
    //   5173: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   5176: aastore
    //   5177: dup
    //   5178: iconst_1
    //   5179: iload 9
    //   5181: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   5184: aastore
    //   5185: invokestatic 2353	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   5188: putfield 1358	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   5191: aload_0
    //   5192: getstatic 2340	java/util/Locale:US	Ljava/util/Locale;
    //   5195: ldc_w 3435
    //   5198: iconst_2
    //   5199: anewarray 1242	java/lang/Object
    //   5202: dup
    //   5203: iconst_0
    //   5204: iload 8
    //   5206: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   5209: aastore
    //   5210: dup
    //   5211: iconst_1
    //   5212: iload 9
    //   5214: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   5217: aastore
    //   5218: invokestatic 2353	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   5221: putfield 1406	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilterThumb	Ljava/lang/String;
    //   5224: aload_2
    //   5225: ifnull +3419 -> 8644
    //   5228: aload_0
    //   5229: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   5232: aload_2
    //   5233: aconst_null
    //   5234: aload_0
    //   5235: getfield 1358	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   5238: aconst_null
    //   5239: aconst_null
    //   5240: ldc_w 3437
    //   5243: aload_2
    //   5244: getfield 3438	org/telegram/tgnet/TLRPC$TL_webDocument:size	I
    //   5247: aconst_null
    //   5248: iconst_1
    //   5249: invokevirtual 1362	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;I)V
    //   5252: aload_0
    //   5253: iconst_1
    //   5254: putfield 724	org/telegram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   5257: aload 10
    //   5259: ifnull +3938 -> 9197
    //   5262: aload 10
    //   5264: ldc_w 3440
    //   5267: invokevirtual 2357	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   5270: ifeq +3927 -> 9197
    //   5273: iload 28
    //   5275: ifeq +3922 -> 9197
    //   5278: iload 28
    //   5280: bipush 60
    //   5282: idiv
    //   5283: istore 9
    //   5285: ldc_w 3442
    //   5288: iconst_2
    //   5289: anewarray 1242	java/lang/Object
    //   5292: dup
    //   5293: iconst_0
    //   5294: iload 9
    //   5296: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   5299: aastore
    //   5300: dup
    //   5301: iconst_1
    //   5302: iload 28
    //   5304: iload 9
    //   5306: bipush 60
    //   5308: imul
    //   5309: isub
    //   5310: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   5313: aastore
    //   5314: invokestatic 1246	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   5317: astore_2
    //   5318: aload_0
    //   5319: getstatic 1664	org/telegram/ui/ActionBar/Theme:chat_durationPaint	Landroid/text/TextPaint;
    //   5322: aload_2
    //   5323: invokevirtual 1178	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   5326: f2d
    //   5327: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   5330: d2i
    //   5331: putfield 1675	org/telegram/ui/Cells/ChatMessageCell:durationWidth	I
    //   5334: aload_0
    //   5335: new 350	android/text/StaticLayout
    //   5338: dup
    //   5339: aload_2
    //   5340: getstatic 1664	org/telegram/ui/ActionBar/Theme:chat_durationPaint	Landroid/text/TextPaint;
    //   5343: aload_0
    //   5344: getfield 1675	org/telegram/ui/Cells/ChatMessageCell:durationWidth	I
    //   5347: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   5350: fconst_1
    //   5351: fconst_0
    //   5352: iconst_0
    //   5353: invokespecial 949	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   5356: putfield 1672	org/telegram/ui/Cells/ChatMessageCell:videoInfoLayout	Landroid/text/StaticLayout;
    //   5359: iload 11
    //   5361: istore 6
    //   5363: iload 6
    //   5365: istore 9
    //   5367: aload_0
    //   5368: getfield 465	org/telegram/ui/Cells/ChatMessageCell:hasInvoicePreview	Z
    //   5371: ifeq +265 -> 5636
    //   5374: aload_1
    //   5375: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   5378: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   5381: getfield 3443	org/telegram/tgnet/TLRPC$MessageMedia:flags	I
    //   5384: iconst_4
    //   5385: iand
    //   5386: ifeq +3927 -> 9313
    //   5389: ldc_w 3445
    //   5392: ldc_w 3446
    //   5395: invokestatic 934	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   5398: invokevirtual 3449	java/lang/String:toUpperCase	()Ljava/lang/String;
    //   5401: astore_2
    //   5402: invokestatic 2467	org/telegram/messenger/LocaleController:getInstance	()Lorg/telegram/messenger/LocaleController;
    //   5405: aload_1
    //   5406: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   5409: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   5412: getfield 3452	org/telegram/tgnet/TLRPC$MessageMedia:total_amount	J
    //   5415: aload_1
    //   5416: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   5419: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   5422: getfield 3455	org/telegram/tgnet/TLRPC$MessageMedia:currency	Ljava/lang/String;
    //   5425: invokevirtual 3459	org/telegram/messenger/LocaleController:formatCurrencyString	(JLjava/lang/String;)Ljava/lang/String;
    //   5428: astore 10
    //   5430: new 2172	android/text/SpannableStringBuilder
    //   5433: dup
    //   5434: new 1320	java/lang/StringBuilder
    //   5437: dup
    //   5438: invokespecial 1321	java/lang/StringBuilder:<init>	()V
    //   5441: aload 10
    //   5443: invokevirtual 1325	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5446: ldc_w 1327
    //   5449: invokevirtual 1325	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5452: aload_2
    //   5453: invokevirtual 2503	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   5456: invokevirtual 1333	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   5459: invokespecial 2175	android/text/SpannableStringBuilder:<init>	(Ljava/lang/CharSequence;)V
    //   5462: astore_2
    //   5463: aload_2
    //   5464: new 2591	org/telegram/ui/Components/TypefaceSpan
    //   5467: dup
    //   5468: ldc_w 2606
    //   5471: invokestatic 2610	org/telegram/messenger/AndroidUtilities:getTypeface	(Ljava/lang/String;)Landroid/graphics/Typeface;
    //   5474: invokespecial 2646	org/telegram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;)V
    //   5477: iconst_0
    //   5478: aload 10
    //   5480: invokevirtual 1054	java/lang/String:length	()I
    //   5483: bipush 33
    //   5485: invokevirtual 2604	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   5488: aload_0
    //   5489: getstatic 1679	org/telegram/ui/ActionBar/Theme:chat_shipmentPaint	Landroid/text/TextPaint;
    //   5492: aload_2
    //   5493: iconst_0
    //   5494: aload_2
    //   5495: invokevirtual 2611	android/text/SpannableStringBuilder:length	()I
    //   5498: invokevirtual 2500	android/text/TextPaint:measureText	(Ljava/lang/CharSequence;II)F
    //   5501: f2d
    //   5502: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   5505: d2i
    //   5506: putfield 1675	org/telegram/ui/Cells/ChatMessageCell:durationWidth	I
    //   5509: aload_0
    //   5510: new 350	android/text/StaticLayout
    //   5513: dup
    //   5514: aload_2
    //   5515: getstatic 1679	org/telegram/ui/ActionBar/Theme:chat_shipmentPaint	Landroid/text/TextPaint;
    //   5518: aload_0
    //   5519: getfield 1675	org/telegram/ui/Cells/ChatMessageCell:durationWidth	I
    //   5522: ldc_w 587
    //   5525: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5528: iadd
    //   5529: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   5532: fconst_1
    //   5533: fconst_0
    //   5534: iconst_0
    //   5535: invokespecial 949	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   5538: putfield 1672	org/telegram/ui/Cells/ChatMessageCell:videoInfoLayout	Landroid/text/StaticLayout;
    //   5541: iload 6
    //   5543: istore 9
    //   5545: aload_0
    //   5546: getfield 724	org/telegram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   5549: ifne +87 -> 5636
    //   5552: aload_0
    //   5553: aload_0
    //   5554: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5557: ldc_w 1588
    //   5560: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5563: iadd
    //   5564: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5567: aload_0
    //   5568: getfield 493	org/telegram/ui/Cells/ChatMessageCell:timeWidth	I
    //   5571: istore 8
    //   5573: aload_1
    //   5574: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   5577: ifeq +3781 -> 9358
    //   5580: bipush 20
    //   5582: istore 9
    //   5584: iload 8
    //   5586: iload 9
    //   5588: bipush 14
    //   5590: iadd
    //   5591: i2f
    //   5592: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5595: iadd
    //   5596: istore 9
    //   5598: aload_0
    //   5599: getfield 1675	org/telegram/ui/Cells/ChatMessageCell:durationWidth	I
    //   5602: iload 9
    //   5604: iadd
    //   5605: iload 14
    //   5607: if_icmple +3757 -> 9364
    //   5610: aload_0
    //   5611: getfield 1675	org/telegram/ui/Cells/ChatMessageCell:durationWidth	I
    //   5614: iload 6
    //   5616: invokestatic 486	java/lang/Math:max	(II)I
    //   5619: istore 9
    //   5621: aload_0
    //   5622: aload_0
    //   5623: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5626: ldc_w 554
    //   5629: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5632: iadd
    //   5633: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5636: aload_0
    //   5637: getfield 463	org/telegram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   5640: ifeq +45 -> 5685
    //   5643: aload_1
    //   5644: getfield 775	org/telegram/messenger/MessageObject:textHeight	I
    //   5647: ifeq +38 -> 5685
    //   5650: aload_0
    //   5651: aload_0
    //   5652: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5655: aload_1
    //   5656: getfield 775	org/telegram/messenger/MessageObject:textHeight	I
    //   5659: ldc_w 1588
    //   5662: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5665: iadd
    //   5666: iadd
    //   5667: putfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5670: aload_0
    //   5671: aload_0
    //   5672: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5675: ldc_w 1078
    //   5678: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5681: iadd
    //   5682: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5685: aload_0
    //   5686: iload 14
    //   5688: iload 19
    //   5690: iload 9
    //   5692: invokespecial 3461	org/telegram/ui/Cells/ChatMessageCell:calcBackgroundWidth	(III)V
    //   5695: iload 13
    //   5697: istore 22
    //   5699: aload_0
    //   5700: getfield 556	org/telegram/ui/Cells/ChatMessageCell:drawInstantView	Z
    //   5703: ifeq +212 -> 5915
    //   5706: aload_0
    //   5707: ldc_w 3462
    //   5710: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5713: putfield 1694	org/telegram/ui/Cells/ChatMessageCell:instantWidth	I
    //   5716: aload_0
    //   5717: getfield 849	org/telegram/ui/Cells/ChatMessageCell:drawInstantViewType	I
    //   5720: iconst_1
    //   5721: if_icmpne +3660 -> 9381
    //   5724: ldc_w 3464
    //   5727: ldc_w 3465
    //   5730: invokestatic 934	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   5733: astore_2
    //   5734: aload_0
    //   5735: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   5738: ldc_w 3466
    //   5741: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5744: isub
    //   5745: istore 9
    //   5747: aload_0
    //   5748: new 350	android/text/StaticLayout
    //   5751: dup
    //   5752: aload_2
    //   5753: getstatic 1690	org/telegram/ui/ActionBar/Theme:chat_instantViewPaint	Landroid/text/TextPaint;
    //   5756: iload 9
    //   5758: i2f
    //   5759: getstatic 1217	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   5762: invokestatic 1221	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   5765: getstatic 1690	org/telegram/ui/ActionBar/Theme:chat_instantViewPaint	Landroid/text/TextPaint;
    //   5768: iload 9
    //   5770: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   5773: fconst_1
    //   5774: fconst_0
    //   5775: iconst_0
    //   5776: invokespecial 949	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   5779: putfield 1711	org/telegram/ui/Cells/ChatMessageCell:instantViewLayout	Landroid/text/StaticLayout;
    //   5782: aload_0
    //   5783: aload_0
    //   5784: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   5787: ldc_w 3467
    //   5790: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5793: isub
    //   5794: putfield 1694	org/telegram/ui/Cells/ChatMessageCell:instantWidth	I
    //   5797: aload_0
    //   5798: aload_0
    //   5799: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5802: ldc_w 3468
    //   5805: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5808: iadd
    //   5809: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5812: iload 13
    //   5814: istore 22
    //   5816: aload_0
    //   5817: getfield 1711	org/telegram/ui/Cells/ChatMessageCell:instantViewLayout	Landroid/text/StaticLayout;
    //   5820: ifnull +95 -> 5915
    //   5823: iload 13
    //   5825: istore 22
    //   5827: aload_0
    //   5828: getfield 1711	org/telegram/ui/Cells/ChatMessageCell:instantViewLayout	Landroid/text/StaticLayout;
    //   5831: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   5834: ifle +81 -> 5915
    //   5837: aload_0
    //   5838: getfield 1694	org/telegram/ui/Cells/ChatMessageCell:instantWidth	I
    //   5841: i2d
    //   5842: aload_0
    //   5843: getfield 1711	org/telegram/ui/Cells/ChatMessageCell:instantViewLayout	Landroid/text/StaticLayout;
    //   5846: iconst_0
    //   5847: invokevirtual 662	android/text/StaticLayout:getLineWidth	(I)F
    //   5850: f2d
    //   5851: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   5854: dsub
    //   5855: d2i
    //   5856: iconst_2
    //   5857: idiv
    //   5858: istore 8
    //   5860: aload_0
    //   5861: getfield 849	org/telegram/ui/Cells/ChatMessageCell:drawInstantViewType	I
    //   5864: ifne +3572 -> 9436
    //   5867: ldc_w 1629
    //   5870: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5873: istore 9
    //   5875: aload_0
    //   5876: iload 9
    //   5878: iload 8
    //   5880: iadd
    //   5881: putfield 1702	org/telegram/ui/Cells/ChatMessageCell:instantTextX	I
    //   5884: aload_0
    //   5885: aload_0
    //   5886: getfield 1711	org/telegram/ui/Cells/ChatMessageCell:instantViewLayout	Landroid/text/StaticLayout;
    //   5889: iconst_0
    //   5890: invokevirtual 659	android/text/StaticLayout:getLineLeft	(I)F
    //   5893: f2i
    //   5894: putfield 1700	org/telegram/ui/Cells/ChatMessageCell:instantTextLeftX	I
    //   5897: aload_0
    //   5898: aload_0
    //   5899: getfield 1702	org/telegram/ui/Cells/ChatMessageCell:instantTextX	I
    //   5902: aload_0
    //   5903: getfield 1700	org/telegram/ui/Cells/ChatMessageCell:instantTextLeftX	I
    //   5906: ineg
    //   5907: iadd
    //   5908: putfield 1702	org/telegram/ui/Cells/ChatMessageCell:instantTextX	I
    //   5911: iload 13
    //   5913: istore 22
    //   5915: aload_0
    //   5916: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   5919: ifnonnull +12274 -> 18193
    //   5922: aload_0
    //   5923: getfield 635	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   5926: ifnonnull +12267 -> 18193
    //   5929: aload_1
    //   5930: getfield 3184	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   5933: ifnull +12260 -> 18193
    //   5936: aload_1
    //   5937: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   5940: bipush 13
    //   5942: if_icmpeq +12251 -> 18193
    //   5945: aload_0
    //   5946: aload_1
    //   5947: getfield 3184	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   5950: putfield 631	org/telegram/ui/Cells/ChatMessageCell:currentCaption	Ljava/lang/CharSequence;
    //   5953: aload_0
    //   5954: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   5957: ldc_w 487
    //   5960: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5963: isub
    //   5964: istore 6
    //   5966: iload 6
    //   5968: ldc_w 587
    //   5971: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5974: isub
    //   5975: istore 8
    //   5977: getstatic 790	android/os/Build$VERSION:SDK_INT	I
    //   5980: bipush 24
    //   5982: if_icmplt +12162 -> 18144
    //   5985: aload_0
    //   5986: aload_1
    //   5987: getfield 3184	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   5990: iconst_0
    //   5991: aload_1
    //   5992: getfield 3184	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   5995: invokeinterface 2192 1 0
    //   6000: getstatic 2970	org/telegram/ui/ActionBar/Theme:chat_msgTextPaint	Landroid/text/TextPaint;
    //   6003: iload 8
    //   6005: invokestatic 3474	android/text/StaticLayout$Builder:obtain	(Ljava/lang/CharSequence;IILandroid/text/TextPaint;I)Landroid/text/StaticLayout$Builder;
    //   6008: iconst_1
    //   6009: invokevirtual 3478	android/text/StaticLayout$Builder:setBreakStrategy	(I)Landroid/text/StaticLayout$Builder;
    //   6012: iconst_0
    //   6013: invokevirtual 3481	android/text/StaticLayout$Builder:setHyphenationFrequency	(I)Landroid/text/StaticLayout$Builder;
    //   6016: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   6019: invokevirtual 3485	android/text/StaticLayout$Builder:setAlignment	(Landroid/text/Layout$Alignment;)Landroid/text/StaticLayout$Builder;
    //   6022: invokevirtual 3489	android/text/StaticLayout$Builder:build	()Landroid/text/StaticLayout;
    //   6025: putfield 635	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   6028: iload 22
    //   6030: istore 9
    //   6032: aload_0
    //   6033: getfield 635	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   6036: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   6039: ifle +160 -> 6199
    //   6042: aload_0
    //   6043: iload 8
    //   6045: putfield 643	org/telegram/ui/Cells/ChatMessageCell:captionWidth	I
    //   6048: aload_0
    //   6049: getfield 493	org/telegram/ui/Cells/ChatMessageCell:timeWidth	I
    //   6052: istore 21
    //   6054: aload_1
    //   6055: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   6058: ifeq +12129 -> 18187
    //   6061: ldc_w 1077
    //   6064: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6067: istore 8
    //   6069: aload_0
    //   6070: aload_0
    //   6071: getfield 635	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   6074: invokevirtual 780	android/text/StaticLayout:getHeight	()I
    //   6077: putfield 647	org/telegram/ui/Cells/ChatMessageCell:captionHeight	I
    //   6080: aload_0
    //   6081: aload_0
    //   6082: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6085: aload_0
    //   6086: getfield 647	org/telegram/ui/Cells/ChatMessageCell:captionHeight	I
    //   6089: ldc_w 1705
    //   6092: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6095: iadd
    //   6096: iadd
    //   6097: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6100: aload_0
    //   6101: getfield 635	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   6104: aload_0
    //   6105: getfield 635	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   6108: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   6111: iconst_1
    //   6112: isub
    //   6113: invokevirtual 662	android/text/StaticLayout:getLineWidth	(I)F
    //   6116: fstore 39
    //   6118: aload_0
    //   6119: getfield 635	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   6122: aload_0
    //   6123: getfield 635	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   6126: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   6129: iconst_1
    //   6130: isub
    //   6131: invokevirtual 659	android/text/StaticLayout:getLineLeft	(I)F
    //   6134: fstore 40
    //   6136: iload 22
    //   6138: istore 9
    //   6140: iload 6
    //   6142: ldc_w 1629
    //   6145: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6148: isub
    //   6149: i2f
    //   6150: fload 39
    //   6152: fload 40
    //   6154: fadd
    //   6155: fsub
    //   6156: iload 21
    //   6158: iload 8
    //   6160: iadd
    //   6161: i2f
    //   6162: fcmpg
    //   6163: ifge +36 -> 6199
    //   6166: aload_0
    //   6167: aload_0
    //   6168: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6171: ldc_w 478
    //   6174: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6177: iadd
    //   6178: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6181: aload_0
    //   6182: aload_0
    //   6183: getfield 647	org/telegram/ui/Cells/ChatMessageCell:captionHeight	I
    //   6186: ldc_w 478
    //   6189: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6192: iadd
    //   6193: putfield 647	org/telegram/ui/Cells/ChatMessageCell:captionHeight	I
    //   6196: iconst_2
    //   6197: istore 9
    //   6199: aload_0
    //   6200: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   6203: getfield 581	org/telegram/messenger/MessageObject:eventId	J
    //   6206: lconst_0
    //   6207: lcmp
    //   6208: ifeq +12132 -> 18340
    //   6211: aload_0
    //   6212: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   6215: invokevirtual 3492	org/telegram/messenger/MessageObject:isMediaEmpty	()Z
    //   6218: ifne +12122 -> 18340
    //   6221: aload_0
    //   6222: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   6225: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   6228: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   6231: getfield 828	org/telegram/tgnet/TLRPC$MessageMedia:webpage	Lorg/telegram/tgnet/TLRPC$WebPage;
    //   6234: ifnull +12106 -> 18340
    //   6237: aload_0
    //   6238: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   6241: ldc_w 2803
    //   6244: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6247: isub
    //   6248: istore 8
    //   6250: aload_0
    //   6251: iconst_1
    //   6252: putfield 461	org/telegram/ui/Cells/ChatMessageCell:hasOldCaptionPreview	Z
    //   6255: aload_0
    //   6256: iconst_0
    //   6257: putfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   6260: aload_0
    //   6261: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   6264: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   6267: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   6270: getfield 828	org/telegram/tgnet/TLRPC$MessageMedia:webpage	Lorg/telegram/tgnet/TLRPC$WebPage;
    //   6273: astore_2
    //   6274: getstatic 1554	org/telegram/ui/ActionBar/Theme:chat_replyNamePaint	Landroid/text/TextPaint;
    //   6277: aload_2
    //   6278: getfield 899	org/telegram/tgnet/TLRPC$WebPage:site_name	Ljava/lang/String;
    //   6281: invokevirtual 1178	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   6284: fconst_1
    //   6285: fadd
    //   6286: f2d
    //   6287: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   6290: d2i
    //   6291: istore 6
    //   6293: aload_0
    //   6294: iload 6
    //   6296: putfield 1564	org/telegram/ui/Cells/ChatMessageCell:siteNameWidth	I
    //   6299: new 350	android/text/StaticLayout
    //   6302: astore 10
    //   6304: aload 10
    //   6306: aload_2
    //   6307: getfield 899	org/telegram/tgnet/TLRPC$WebPage:site_name	Ljava/lang/String;
    //   6310: getstatic 1554	org/telegram/ui/ActionBar/Theme:chat_replyNamePaint	Landroid/text/TextPaint;
    //   6313: iload 6
    //   6315: iload 8
    //   6317: invokestatic 1195	java/lang/Math:min	(II)I
    //   6320: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   6323: fconst_1
    //   6324: fconst_0
    //   6325: iconst_0
    //   6326: invokespecial 949	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   6329: aload_0
    //   6330: aload 10
    //   6332: putfield 1522	org/telegram/ui/Cells/ChatMessageCell:siteNameLayout	Landroid/text/StaticLayout;
    //   6335: aload_0
    //   6336: getfield 1522	org/telegram/ui/Cells/ChatMessageCell:siteNameLayout	Landroid/text/StaticLayout;
    //   6339: iconst_0
    //   6340: invokevirtual 659	android/text/StaticLayout:getLineLeft	(I)F
    //   6343: fconst_0
    //   6344: fcmpl
    //   6345: ifeq +11902 -> 18247
    //   6348: iconst_1
    //   6349: istore_3
    //   6350: aload_0
    //   6351: iload_3
    //   6352: putfield 1562	org/telegram/ui/Cells/ChatMessageCell:siteNameRtl	Z
    //   6355: aload_0
    //   6356: getfield 1522	org/telegram/ui/Cells/ChatMessageCell:siteNameLayout	Landroid/text/StaticLayout;
    //   6359: aload_0
    //   6360: getfield 1522	org/telegram/ui/Cells/ChatMessageCell:siteNameLayout	Landroid/text/StaticLayout;
    //   6363: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   6366: iconst_1
    //   6367: isub
    //   6368: invokevirtual 1525	android/text/StaticLayout:getLineBottom	(I)I
    //   6371: istore 6
    //   6373: aload_0
    //   6374: aload_0
    //   6375: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   6378: iload 6
    //   6380: iadd
    //   6381: putfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   6384: aload_0
    //   6385: aload_0
    //   6386: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6389: iload 6
    //   6391: iadd
    //   6392: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6395: aload_0
    //   6396: iconst_0
    //   6397: putfield 738	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   6400: aload_0
    //   6401: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   6404: ifeq +16 -> 6420
    //   6407: aload_0
    //   6408: aload_0
    //   6409: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6412: fconst_2
    //   6413: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6416: iadd
    //   6417: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6420: aload_0
    //   6421: aload_2
    //   6422: getfield 1375	org/telegram/tgnet/TLRPC$WebPage:description	Ljava/lang/String;
    //   6425: getstatic 1593	org/telegram/ui/ActionBar/Theme:chat_replyTextPaint	Landroid/text/TextPaint;
    //   6428: iload 8
    //   6430: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   6433: fconst_1
    //   6434: fconst_1
    //   6435: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6438: i2f
    //   6439: iconst_0
    //   6440: getstatic 1217	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   6443: iload 8
    //   6445: bipush 6
    //   6447: invokestatic 1315	org/telegram/ui/Components/StaticLayoutEx:createStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZLandroid/text/TextUtils$TruncateAt;II)Landroid/text/StaticLayout;
    //   6450: putfield 732	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   6453: aload_0
    //   6454: getfield 732	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   6457: aload_0
    //   6458: getfield 732	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   6461: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   6464: iconst_1
    //   6465: isub
    //   6466: invokevirtual 1525	android/text/StaticLayout:getLineBottom	(I)I
    //   6469: istore 8
    //   6471: aload_0
    //   6472: aload_0
    //   6473: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   6476: iload 8
    //   6478: iadd
    //   6479: putfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   6482: aload_0
    //   6483: aload_0
    //   6484: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6487: iload 8
    //   6489: iadd
    //   6490: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6493: iconst_0
    //   6494: istore 8
    //   6496: iload 8
    //   6498: aload_0
    //   6499: getfield 732	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   6502: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   6505: if_icmpge +11779 -> 18284
    //   6508: aload_0
    //   6509: getfield 732	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   6512: iload 8
    //   6514: invokevirtual 659	android/text/StaticLayout:getLineLeft	(I)F
    //   6517: f2d
    //   6518: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   6521: d2i
    //   6522: istore 6
    //   6524: iload 6
    //   6526: ifeq +17 -> 6543
    //   6529: aload_0
    //   6530: getfield 738	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   6533: ifne +11729 -> 18262
    //   6536: aload_0
    //   6537: iload 6
    //   6539: ineg
    //   6540: putfield 738	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   6543: iinc 8 1
    //   6546: goto -50 -> 6496
    //   6549: iload 8
    //   6551: istore 22
    //   6553: aload_0
    //   6554: aload_0
    //   6555: getfield 738	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   6558: iload 36
    //   6560: ineg
    //   6561: invokestatic 486	java/lang/Math:max	(II)I
    //   6564: putfield 738	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   6567: goto -2058 -> 4509
    //   6570: iload 8
    //   6572: istore 22
    //   6574: aload_0
    //   6575: getfield 732	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   6578: invokevirtual 3366	android/text/StaticLayout:getWidth	()I
    //   6581: istore 38
    //   6583: iconst_0
    //   6584: istore 36
    //   6586: iload 8
    //   6588: istore 22
    //   6590: iload 8
    //   6592: istore 9
    //   6594: iload 36
    //   6596: aload_0
    //   6597: getfield 732	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   6600: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   6603: if_icmpge -1882 -> 4721
    //   6606: iload 8
    //   6608: istore 22
    //   6610: aload_0
    //   6611: getfield 732	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   6614: iload 36
    //   6616: invokevirtual 659	android/text/StaticLayout:getLineLeft	(I)F
    //   6619: f2d
    //   6620: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   6623: d2i
    //   6624: istore 37
    //   6626: iload 37
    //   6628: ifne +23 -> 6651
    //   6631: iload 8
    //   6633: istore 22
    //   6635: aload_0
    //   6636: getfield 738	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   6639: ifeq +12 -> 6651
    //   6642: iload 8
    //   6644: istore 22
    //   6646: aload_0
    //   6647: iconst_0
    //   6648: putfield 738	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   6651: iload 37
    //   6653: ifeq +164 -> 6817
    //   6656: iload 38
    //   6658: iload 37
    //   6660: isub
    //   6661: istore 9
    //   6663: iload 36
    //   6665: iload 6
    //   6667: if_icmplt +36 -> 6703
    //   6670: iload 9
    //   6672: istore 34
    //   6674: iload 6
    //   6676: ifeq +42 -> 6718
    //   6679: iload 9
    //   6681: istore 34
    //   6683: iload 37
    //   6685: ifeq +33 -> 6718
    //   6688: iload 8
    //   6690: istore 22
    //   6692: iload 9
    //   6694: istore 34
    //   6696: aload_0
    //   6697: getfield 1637	org/telegram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   6700: ifeq +18 -> 6718
    //   6703: iload 8
    //   6705: istore 22
    //   6707: iload 9
    //   6709: ldc_w 2004
    //   6712: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6715: iadd
    //   6716: istore 34
    //   6718: iload 35
    //   6720: istore 9
    //   6722: iload 35
    //   6724: iload 34
    //   6726: iload 29
    //   6728: iadd
    //   6729: if_icmpge +62 -> 6791
    //   6732: iload 30
    //   6734: ifeq +24 -> 6758
    //   6737: iload 8
    //   6739: istore 22
    //   6741: aload_0
    //   6742: aload_0
    //   6743: getfield 1597	org/telegram/ui/Cells/ChatMessageCell:titleX	I
    //   6746: iload 34
    //   6748: iload 29
    //   6750: iadd
    //   6751: iload 35
    //   6753: isub
    //   6754: iadd
    //   6755: putfield 1597	org/telegram/ui/Cells/ChatMessageCell:titleX	I
    //   6758: iload 21
    //   6760: ifeq +24 -> 6784
    //   6763: iload 8
    //   6765: istore 22
    //   6767: aload_0
    //   6768: aload_0
    //   6769: getfield 1601	org/telegram/ui/Cells/ChatMessageCell:authorX	I
    //   6772: iload 34
    //   6774: iload 29
    //   6776: iadd
    //   6777: iload 35
    //   6779: isub
    //   6780: iadd
    //   6781: putfield 1601	org/telegram/ui/Cells/ChatMessageCell:authorX	I
    //   6784: iload 34
    //   6786: iload 29
    //   6788: iadd
    //   6789: istore 9
    //   6791: iload 8
    //   6793: istore 22
    //   6795: iload 8
    //   6797: iload 34
    //   6799: iload 29
    //   6801: iadd
    //   6802: invokestatic 486	java/lang/Math:max	(II)I
    //   6805: istore 8
    //   6807: iinc 36 1
    //   6810: iload 9
    //   6812: istore 35
    //   6814: goto -228 -> 6586
    //   6817: iload 12
    //   6819: ifeq +10 -> 6829
    //   6822: iload 38
    //   6824: istore 9
    //   6826: goto -163 -> 6663
    //   6829: iload 8
    //   6831: istore 22
    //   6833: aload_0
    //   6834: getfield 732	org/telegram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   6837: iload 36
    //   6839: invokevirtual 662	android/text/StaticLayout:getLineWidth	(I)F
    //   6842: f2d
    //   6843: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   6846: d2i
    //   6847: iload 38
    //   6849: invokestatic 1195	java/lang/Math:min	(II)I
    //   6852: istore 9
    //   6854: goto -191 -> 6663
    //   6857: iload 32
    //   6859: istore 8
    //   6861: goto -2077 -> 4784
    //   6864: aload 20
    //   6866: invokestatic 3495	org/telegram/messenger/MessageObject:isGifDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   6869: ifeq +227 -> 7096
    //   6872: getstatic 833	org/telegram/messenger/SharedConfig:autoplayGifs	Z
    //   6875: ifne +8 -> 6883
    //   6878: aload_1
    //   6879: fconst_1
    //   6880: putfield 882	org/telegram/messenger/MessageObject:gifState	F
    //   6883: aload_0
    //   6884: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   6887: astore 26
    //   6889: aload_1
    //   6890: getfield 882	org/telegram/messenger/MessageObject:gifState	F
    //   6893: fconst_1
    //   6894: fcmpl
    //   6895: ifeq +190 -> 7085
    //   6898: iconst_1
    //   6899: istore_3
    //   6900: aload 26
    //   6902: iload_3
    //   6903: invokevirtual 885	org/telegram/messenger/ImageReceiver:setAllowStartAnimation	(Z)V
    //   6906: aload_0
    //   6907: aload 20
    //   6909: getfield 1285	org/telegram/tgnet/TLRPC$Document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6912: putfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6915: aload_0
    //   6916: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6919: ifnull +148 -> 7067
    //   6922: aload_0
    //   6923: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6926: getfield 3498	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   6929: ifeq +13 -> 6942
    //   6932: aload_0
    //   6933: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6936: getfield 3501	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   6939: ifne +128 -> 7067
    //   6942: iconst_0
    //   6943: istore 6
    //   6945: iload 6
    //   6947: aload 20
    //   6949: getfield 1159	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   6952: invokevirtual 590	java/util/ArrayList:size	()I
    //   6955: if_icmpge +58 -> 7013
    //   6958: aload 20
    //   6960: getfield 1159	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   6963: iload 6
    //   6965: invokevirtual 594	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   6968: checkcast 1161	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   6971: astore 26
    //   6973: aload 26
    //   6975: instanceof 3503
    //   6978: ifne +11 -> 6989
    //   6981: aload 26
    //   6983: instanceof 1255
    //   6986: ifeq +104 -> 7090
    //   6989: aload_0
    //   6990: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   6993: aload 26
    //   6995: getfield 3504	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   6998: putfield 3498	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   7001: aload_0
    //   7002: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7005: aload 26
    //   7007: getfield 3505	org/telegram/tgnet/TLRPC$DocumentAttribute:h	I
    //   7010: putfield 3501	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   7013: aload_0
    //   7014: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7017: getfield 3498	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   7020: ifeq +13 -> 7033
    //   7023: aload_0
    //   7024: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7027: getfield 3501	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   7030: ifne +37 -> 7067
    //   7033: aload_0
    //   7034: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7037: astore 27
    //   7039: aload_0
    //   7040: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7043: astore 26
    //   7045: ldc_w 3506
    //   7048: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7051: istore 6
    //   7053: aload 26
    //   7055: iload 6
    //   7057: putfield 3501	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   7060: aload 27
    //   7062: iload 6
    //   7064: putfield 3498	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   7067: aload_0
    //   7068: aload 20
    //   7070: putfield 1149	org/telegram/ui/Cells/ChatMessageCell:documentAttach	Lorg/telegram/tgnet/TLRPC$Document;
    //   7073: aload_0
    //   7074: iconst_2
    //   7075: putfield 498	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   7078: iload 9
    //   7080: istore 6
    //   7082: goto -2260 -> 4822
    //   7085: iconst_0
    //   7086: istore_3
    //   7087: goto -187 -> 6900
    //   7090: iinc 6 1
    //   7093: goto -148 -> 6945
    //   7096: aload 20
    //   7098: invokestatic 1250	org/telegram/messenger/MessageObject:isVideoDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   7101: ifeq +176 -> 7277
    //   7104: aload_0
    //   7105: aload 20
    //   7107: getfield 1285	org/telegram/tgnet/TLRPC$Document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7110: putfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7113: aload_0
    //   7114: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7117: ifnull +140 -> 7257
    //   7120: aload_0
    //   7121: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7124: getfield 3498	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   7127: ifeq +13 -> 7140
    //   7130: aload_0
    //   7131: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7134: getfield 3501	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   7137: ifne +120 -> 7257
    //   7140: iconst_0
    //   7141: istore 6
    //   7143: iload 6
    //   7145: aload 20
    //   7147: getfield 1159	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   7150: invokevirtual 590	java/util/ArrayList:size	()I
    //   7153: if_icmpge +50 -> 7203
    //   7156: aload 20
    //   7158: getfield 1159	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   7161: iload 6
    //   7163: invokevirtual 594	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   7166: checkcast 1161	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   7169: astore 26
    //   7171: aload 26
    //   7173: instanceof 1255
    //   7176: ifeq +95 -> 7271
    //   7179: aload_0
    //   7180: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7183: aload 26
    //   7185: getfield 3504	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   7188: putfield 3498	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   7191: aload_0
    //   7192: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7195: aload 26
    //   7197: getfield 3505	org/telegram/tgnet/TLRPC$DocumentAttribute:h	I
    //   7200: putfield 3501	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   7203: aload_0
    //   7204: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7207: getfield 3498	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   7210: ifeq +13 -> 7223
    //   7213: aload_0
    //   7214: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7217: getfield 3501	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   7220: ifne +37 -> 7257
    //   7223: aload_0
    //   7224: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7227: astore 26
    //   7229: aload_0
    //   7230: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7233: astore 27
    //   7235: ldc_w 3506
    //   7238: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7241: istore 6
    //   7243: aload 27
    //   7245: iload 6
    //   7247: putfield 3501	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   7250: aload 26
    //   7252: iload 6
    //   7254: putfield 3498	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   7257: aload_0
    //   7258: iconst_0
    //   7259: aload_1
    //   7260: invokespecial 3508	org/telegram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/telegram/messenger/MessageObject;)I
    //   7263: pop
    //   7264: iload 9
    //   7266: istore 6
    //   7268: goto -2446 -> 4822
    //   7271: iinc 6 1
    //   7274: goto -131 -> 7143
    //   7277: aload 20
    //   7279: invokestatic 3511	org/telegram/messenger/MessageObject:isStickerDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   7282: ifeq +181 -> 7463
    //   7285: aload_0
    //   7286: aload 20
    //   7288: getfield 1285	org/telegram/tgnet/TLRPC$Document:thumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7291: putfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7294: aload_0
    //   7295: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7298: ifnull +140 -> 7438
    //   7301: aload_0
    //   7302: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7305: getfield 3498	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   7308: ifeq +13 -> 7321
    //   7311: aload_0
    //   7312: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7315: getfield 3501	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   7318: ifne +120 -> 7438
    //   7321: iconst_0
    //   7322: istore 6
    //   7324: iload 6
    //   7326: aload 20
    //   7328: getfield 1159	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   7331: invokevirtual 590	java/util/ArrayList:size	()I
    //   7334: if_icmpge +50 -> 7384
    //   7337: aload 20
    //   7339: getfield 1159	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   7342: iload 6
    //   7344: invokevirtual 594	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   7347: checkcast 1161	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   7350: astore 26
    //   7352: aload 26
    //   7354: instanceof 3503
    //   7357: ifeq +100 -> 7457
    //   7360: aload_0
    //   7361: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7364: aload 26
    //   7366: getfield 3504	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   7369: putfield 3498	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   7372: aload_0
    //   7373: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7376: aload 26
    //   7378: getfield 3505	org/telegram/tgnet/TLRPC$DocumentAttribute:h	I
    //   7381: putfield 3501	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   7384: aload_0
    //   7385: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7388: getfield 3498	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   7391: ifeq +13 -> 7404
    //   7394: aload_0
    //   7395: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7398: getfield 3501	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   7401: ifne +37 -> 7438
    //   7404: aload_0
    //   7405: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7408: astore 26
    //   7410: aload_0
    //   7411: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   7414: astore 27
    //   7416: ldc_w 3506
    //   7419: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7422: istore 6
    //   7424: aload 27
    //   7426: iload 6
    //   7428: putfield 3501	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   7431: aload 26
    //   7433: iload 6
    //   7435: putfield 3498	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   7438: aload_0
    //   7439: aload 20
    //   7441: putfield 1149	org/telegram/ui/Cells/ChatMessageCell:documentAttach	Lorg/telegram/tgnet/TLRPC$Document;
    //   7444: aload_0
    //   7445: bipush 6
    //   7447: putfield 498	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   7450: iload 9
    //   7452: istore 6
    //   7454: goto -2632 -> 4822
    //   7457: iinc 6 1
    //   7460: goto -136 -> 7324
    //   7463: aload_0
    //   7464: iload 14
    //   7466: iload 19
    //   7468: iload 9
    //   7470: invokespecial 3461	org/telegram/ui/Cells/ChatMessageCell:calcBackgroundWidth	(III)V
    //   7473: aload 20
    //   7475: invokestatic 3511	org/telegram/messenger/MessageObject:isStickerDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   7478: ifne +11910 -> 19388
    //   7481: aload_0
    //   7482: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7485: ldc_w 1077
    //   7488: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7491: iload 14
    //   7493: iadd
    //   7494: if_icmpge +16 -> 7510
    //   7497: aload_0
    //   7498: ldc_w 1077
    //   7501: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7504: iload 14
    //   7506: iadd
    //   7507: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7510: aload 20
    //   7512: invokestatic 1154	org/telegram/messenger/MessageObject:isVoiceDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   7515: ifeq +251 -> 7766
    //   7518: aload_0
    //   7519: aload_0
    //   7520: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7523: ldc_w 587
    //   7526: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7529: isub
    //   7530: aload_1
    //   7531: invokespecial 3508	org/telegram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/telegram/messenger/MessageObject;)I
    //   7534: pop
    //   7535: aload_0
    //   7536: aload_0
    //   7537: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   7540: getfield 775	org/telegram/messenger/MessageObject:textHeight	I
    //   7543: ldc_w 1629
    //   7546: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7549: iadd
    //   7550: aload_0
    //   7551: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   7554: iadd
    //   7555: putfield 565	org/telegram/ui/Cells/ChatMessageCell:mediaOffsetY	I
    //   7558: aload_0
    //   7559: aload_0
    //   7560: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   7563: ldc_w 1819
    //   7566: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7569: iadd
    //   7570: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   7573: aload_0
    //   7574: aload_0
    //   7575: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   7578: ldc_w 1819
    //   7581: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7584: iadd
    //   7585: putfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   7588: iload 14
    //   7590: ldc_w 1201
    //   7593: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7596: isub
    //   7597: istore 14
    //   7599: invokestatic 2226	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   7602: ifeq +87 -> 7689
    //   7605: invokestatic 2258	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   7608: istore 6
    //   7610: aload_0
    //   7611: getfield 2040	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   7614: ifeq +69 -> 7683
    //   7617: aload_1
    //   7618: invokevirtual 2043	org/telegram/messenger/MessageObject:needDrawAvatar	()Z
    //   7621: ifeq +62 -> 7683
    //   7624: aload_1
    //   7625: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   7628: ifne +55 -> 7683
    //   7631: ldc_w 2004
    //   7634: fstore 40
    //   7636: iload 9
    //   7638: iload 6
    //   7640: fload 40
    //   7642: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7645: isub
    //   7646: ldc_w 2145
    //   7649: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7652: invokestatic 1195	java/lang/Math:min	(II)I
    //   7655: ldc_w 1079
    //   7658: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7661: isub
    //   7662: iload 29
    //   7664: iadd
    //   7665: invokestatic 486	java/lang/Math:max	(II)I
    //   7668: istore 6
    //   7670: aload_0
    //   7671: iload 14
    //   7673: iload 19
    //   7675: iload 6
    //   7677: invokespecial 3461	org/telegram/ui/Cells/ChatMessageCell:calcBackgroundWidth	(III)V
    //   7680: goto -2858 -> 4822
    //   7683: fconst_0
    //   7684: fstore 40
    //   7686: goto -50 -> 7636
    //   7689: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   7692: getfield 2253	android/graphics/Point:x	I
    //   7695: istore 6
    //   7697: aload_0
    //   7698: getfield 2040	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   7701: ifeq +59 -> 7760
    //   7704: aload_1
    //   7705: invokevirtual 2043	org/telegram/messenger/MessageObject:needDrawAvatar	()Z
    //   7708: ifeq +52 -> 7760
    //   7711: aload_1
    //   7712: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   7715: ifne +45 -> 7760
    //   7718: ldc_w 2004
    //   7721: fstore 40
    //   7723: iload 9
    //   7725: iload 6
    //   7727: fload 40
    //   7729: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7732: isub
    //   7733: ldc_w 2145
    //   7736: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7739: invokestatic 1195	java/lang/Math:min	(II)I
    //   7742: ldc_w 1079
    //   7745: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7748: isub
    //   7749: iload 29
    //   7751: iadd
    //   7752: invokestatic 486	java/lang/Math:max	(II)I
    //   7755: istore 6
    //   7757: goto -87 -> 7670
    //   7760: fconst_0
    //   7761: fstore 40
    //   7763: goto -40 -> 7723
    //   7766: aload 20
    //   7768: invokestatic 1200	org/telegram/messenger/MessageObject:isMusicDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   7771: ifeq +225 -> 7996
    //   7774: aload_0
    //   7775: aload_0
    //   7776: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7779: ldc_w 587
    //   7782: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7785: isub
    //   7786: aload_1
    //   7787: invokespecial 3508	org/telegram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/telegram/messenger/MessageObject;)I
    //   7790: istore 6
    //   7792: aload_0
    //   7793: aload_0
    //   7794: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   7797: getfield 775	org/telegram/messenger/MessageObject:textHeight	I
    //   7800: ldc_w 1629
    //   7803: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7806: iadd
    //   7807: aload_0
    //   7808: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   7811: iadd
    //   7812: putfield 565	org/telegram/ui/Cells/ChatMessageCell:mediaOffsetY	I
    //   7815: aload_0
    //   7816: aload_0
    //   7817: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   7820: ldc_w 1796
    //   7823: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7826: iadd
    //   7827: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   7830: aload_0
    //   7831: aload_0
    //   7832: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   7835: ldc_w 1796
    //   7838: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7841: iadd
    //   7842: putfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   7845: iload 14
    //   7847: ldc_w 1201
    //   7850: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7853: isub
    //   7854: istore 14
    //   7856: iload 9
    //   7858: iload 6
    //   7860: iload 29
    //   7862: iadd
    //   7863: ldc_w 1167
    //   7866: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7869: iadd
    //   7870: invokestatic 486	java/lang/Math:max	(II)I
    //   7873: istore 6
    //   7875: iload 6
    //   7877: istore 9
    //   7879: aload_0
    //   7880: getfield 1223	org/telegram/ui/Cells/ChatMessageCell:songLayout	Landroid/text/StaticLayout;
    //   7883: ifnull +46 -> 7929
    //   7886: iload 6
    //   7888: istore 9
    //   7890: aload_0
    //   7891: getfield 1223	org/telegram/ui/Cells/ChatMessageCell:songLayout	Landroid/text/StaticLayout;
    //   7894: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   7897: ifle +32 -> 7929
    //   7900: iload 6
    //   7902: i2f
    //   7903: aload_0
    //   7904: getfield 1223	org/telegram/ui/Cells/ChatMessageCell:songLayout	Landroid/text/StaticLayout;
    //   7907: iconst_0
    //   7908: invokevirtual 662	android/text/StaticLayout:getLineWidth	(I)F
    //   7911: iload 29
    //   7913: i2f
    //   7914: fadd
    //   7915: ldc_w 1201
    //   7918: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7921: i2f
    //   7922: fadd
    //   7923: invokestatic 3514	java/lang/Math:max	(FF)F
    //   7926: f2i
    //   7927: istore 9
    //   7929: iload 9
    //   7931: istore 6
    //   7933: aload_0
    //   7934: getfield 1236	org/telegram/ui/Cells/ChatMessageCell:performerLayout	Landroid/text/StaticLayout;
    //   7937: ifnull +46 -> 7983
    //   7940: iload 9
    //   7942: istore 6
    //   7944: aload_0
    //   7945: getfield 1236	org/telegram/ui/Cells/ChatMessageCell:performerLayout	Landroid/text/StaticLayout;
    //   7948: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   7951: ifle +32 -> 7983
    //   7954: iload 9
    //   7956: i2f
    //   7957: aload_0
    //   7958: getfield 1236	org/telegram/ui/Cells/ChatMessageCell:performerLayout	Landroid/text/StaticLayout;
    //   7961: iconst_0
    //   7962: invokevirtual 662	android/text/StaticLayout:getLineWidth	(I)F
    //   7965: iload 29
    //   7967: i2f
    //   7968: fadd
    //   7969: ldc_w 1201
    //   7972: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   7975: i2f
    //   7976: fadd
    //   7977: invokestatic 3514	java/lang/Math:max	(FF)F
    //   7980: f2i
    //   7981: istore 6
    //   7983: aload_0
    //   7984: iload 14
    //   7986: iload 19
    //   7988: iload 6
    //   7990: invokespecial 3461	org/telegram/ui/Cells/ChatMessageCell:calcBackgroundWidth	(III)V
    //   7993: goto -3171 -> 4822
    //   7996: aload_0
    //   7997: aload_0
    //   7998: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8001: ldc_w 3515
    //   8004: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8007: isub
    //   8008: aload_1
    //   8009: invokespecial 3508	org/telegram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/telegram/messenger/MessageObject;)I
    //   8012: pop
    //   8013: aload_0
    //   8014: iconst_1
    //   8015: putfield 783	org/telegram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   8018: aload_0
    //   8019: getfield 724	org/telegram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   8022: ifeq +69 -> 8091
    //   8025: aload_0
    //   8026: aload_0
    //   8027: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   8030: ldc_w 2568
    //   8033: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8036: iadd
    //   8037: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   8040: aload_0
    //   8041: aload_0
    //   8042: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   8045: ldc_w 1201
    //   8048: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8051: iadd
    //   8052: putfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   8055: aload_0
    //   8056: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   8059: iconst_0
    //   8060: aload_0
    //   8061: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   8064: aload_0
    //   8065: getfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   8068: iadd
    //   8069: ldc_w 1201
    //   8072: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8075: ldc_w 1201
    //   8078: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8081: invokevirtual 1581	org/telegram/messenger/ImageReceiver:setImageCoords	(IIII)V
    //   8084: iload 9
    //   8086: istore 6
    //   8088: goto -3266 -> 4822
    //   8091: aload_0
    //   8092: aload_0
    //   8093: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   8096: getfield 775	org/telegram/messenger/MessageObject:textHeight	I
    //   8099: ldc_w 1629
    //   8102: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8105: iadd
    //   8106: aload_0
    //   8107: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   8110: iadd
    //   8111: putfield 565	org/telegram/ui/Cells/ChatMessageCell:mediaOffsetY	I
    //   8114: aload_0
    //   8115: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   8118: iconst_0
    //   8119: aload_0
    //   8120: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   8123: aload_0
    //   8124: getfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   8127: iadd
    //   8128: ldc_w 478
    //   8131: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8134: isub
    //   8135: ldc_w 1796
    //   8138: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8141: ldc_w 1796
    //   8144: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8147: invokevirtual 1581	org/telegram/messenger/ImageReceiver:setImageCoords	(IIII)V
    //   8150: aload_0
    //   8151: aload_0
    //   8152: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   8155: ldc_w 3369
    //   8158: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8161: iadd
    //   8162: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   8165: aload_0
    //   8166: aload_0
    //   8167: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   8170: ldc_w 1074
    //   8173: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8176: iadd
    //   8177: putfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   8180: iload 9
    //   8182: istore 6
    //   8184: goto -3362 -> 4822
    //   8187: aload 27
    //   8189: ifnull +118 -> 8307
    //   8192: aload 10
    //   8194: ifnull +96 -> 8290
    //   8197: aload 10
    //   8199: ldc_w 3428
    //   8202: invokevirtual 2357	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   8205: ifeq +85 -> 8290
    //   8208: iconst_1
    //   8209: istore_3
    //   8210: aload_0
    //   8211: iload_3
    //   8212: putfield 783	org/telegram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   8215: aload_1
    //   8216: getfield 1336	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   8219: astore 26
    //   8221: aload_0
    //   8222: getfield 783	org/telegram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   8225: ifeq +70 -> 8295
    //   8228: invokestatic 1339	org/telegram/messenger/AndroidUtilities:getPhotoSize	()I
    //   8231: istore 6
    //   8233: aload_0
    //   8234: getfield 783	org/telegram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   8237: ifne +65 -> 8302
    //   8240: iconst_1
    //   8241: istore_3
    //   8242: aload_0
    //   8243: aload 26
    //   8245: iload 6
    //   8247: iload_3
    //   8248: invokestatic 3518	org/telegram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;IZ)Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8251: putfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8254: aload_0
    //   8255: aload_1
    //   8256: getfield 1336	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   8259: bipush 80
    //   8261: invokestatic 1343	org/telegram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;I)Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8264: putfield 1404	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8267: aload_0
    //   8268: getfield 1404	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8271: aload_0
    //   8272: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8275: if_acmpne +11113 -> 19388
    //   8278: aload_0
    //   8279: aconst_null
    //   8280: putfield 1404	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8283: iload 9
    //   8285: istore 6
    //   8287: goto -3465 -> 4822
    //   8290: iconst_0
    //   8291: istore_3
    //   8292: goto -82 -> 8210
    //   8295: iload 8
    //   8297: istore 6
    //   8299: goto -66 -> 8233
    //   8302: iconst_0
    //   8303: istore_3
    //   8304: goto -62 -> 8242
    //   8307: aload_2
    //   8308: ifnull +11080 -> 19388
    //   8311: aload_2
    //   8312: getfield 3519	org/telegram/tgnet/TLRPC$TL_webDocument:mime_type	Ljava/lang/String;
    //   8315: ldc_w 1278
    //   8318: invokevirtual 1282	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   8321: ifne +11064 -> 19385
    //   8324: aconst_null
    //   8325: astore_2
    //   8326: aload_0
    //   8327: iconst_0
    //   8328: putfield 783	org/telegram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   8331: iload 9
    //   8333: istore 6
    //   8335: goto -3513 -> 4822
    //   8338: iconst_0
    //   8339: istore_3
    //   8340: goto -3426 -> 4914
    //   8343: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   8346: getfield 2253	android/graphics/Point:x	I
    //   8349: i2f
    //   8350: ldc_w 2766
    //   8353: fmul
    //   8354: f2i
    //   8355: istore 9
    //   8357: goto -3379 -> 4978
    //   8360: iload 8
    //   8362: istore 9
    //   8364: aload_0
    //   8365: getfield 498	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   8368: bipush 7
    //   8370: if_icmpne -3392 -> 4978
    //   8373: getstatic 1846	org/telegram/messenger/AndroidUtilities:roundMessageSize	I
    //   8376: istore 9
    //   8378: aload_0
    //   8379: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   8382: iconst_1
    //   8383: invokevirtual 3303	org/telegram/messenger/ImageReceiver:setAllowDecodeSingleFrame	(Z)V
    //   8386: goto -3408 -> 4978
    //   8389: iconst_0
    //   8390: istore 8
    //   8392: goto -3399 -> 4993
    //   8395: aload_2
    //   8396: iconst_m1
    //   8397: putfield 3438	org/telegram/tgnet/TLRPC$TL_webDocument:size	I
    //   8400: goto -3362 -> 5038
    //   8403: aload_0
    //   8404: getfield 463	org/telegram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   8407: ifne +10 -> 8417
    //   8410: aload_0
    //   8411: getfield 465	org/telegram/ui/Cells/ChatMessageCell:hasInvoicePreview	Z
    //   8414: ifeq +41 -> 8455
    //   8417: sipush 640
    //   8420: i2f
    //   8421: iload 9
    //   8423: fconst_2
    //   8424: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8427: isub
    //   8428: i2f
    //   8429: fdiv
    //   8430: fstore 40
    //   8432: sipush 640
    //   8435: i2f
    //   8436: fload 40
    //   8438: fdiv
    //   8439: f2i
    //   8440: istore 8
    //   8442: sipush 360
    //   8445: i2f
    //   8446: fload 40
    //   8448: fdiv
    //   8449: f2i
    //   8450: istore 9
    //   8452: goto -3388 -> 5064
    //   8455: aload_0
    //   8456: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8459: getfield 3498	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   8462: istore 6
    //   8464: aload_0
    //   8465: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8468: getfield 3501	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   8471: istore 8
    //   8473: iload 6
    //   8475: i2f
    //   8476: iload 9
    //   8478: fconst_2
    //   8479: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8482: isub
    //   8483: i2f
    //   8484: fdiv
    //   8485: fstore 40
    //   8487: iload 6
    //   8489: i2f
    //   8490: fload 40
    //   8492: fdiv
    //   8493: f2i
    //   8494: istore 6
    //   8496: iload 8
    //   8498: i2f
    //   8499: fload 40
    //   8501: fdiv
    //   8502: f2i
    //   8503: istore 21
    //   8505: aload 23
    //   8507: ifnull +29 -> 8536
    //   8510: aload 23
    //   8512: ifnull +62 -> 8574
    //   8515: aload 23
    //   8517: invokevirtual 1276	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   8520: ldc_w 3354
    //   8523: invokevirtual 2357	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   8526: ifne +48 -> 8574
    //   8529: aload_0
    //   8530: getfield 498	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   8533: ifne +41 -> 8574
    //   8536: iload 21
    //   8538: istore 9
    //   8540: iload 6
    //   8542: istore 8
    //   8544: iload 21
    //   8546: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   8549: getfield 2262	android/graphics/Point:y	I
    //   8552: iconst_3
    //   8553: idiv
    //   8554: if_icmple -3490 -> 5064
    //   8557: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   8560: getfield 2262	android/graphics/Point:y	I
    //   8563: iconst_3
    //   8564: idiv
    //   8565: istore 9
    //   8567: iload 6
    //   8569: istore 8
    //   8571: goto -3507 -> 5064
    //   8574: iload 21
    //   8576: istore 9
    //   8578: iload 6
    //   8580: istore 8
    //   8582: iload 21
    //   8584: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   8587: getfield 2262	android/graphics/Point:y	I
    //   8590: iconst_2
    //   8591: idiv
    //   8592: if_icmple -3528 -> 5064
    //   8595: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   8598: getfield 2262	android/graphics/Point:y	I
    //   8601: iconst_2
    //   8602: idiv
    //   8603: istore 9
    //   8605: iload 6
    //   8607: istore 8
    //   8609: goto -3545 -> 5064
    //   8612: aload_0
    //   8613: aload_0
    //   8614: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   8617: ldc_w 554
    //   8620: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   8623: iload 9
    //   8625: iadd
    //   8626: iadd
    //   8627: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   8630: aload_0
    //   8631: aload_0
    //   8632: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   8635: iload 9
    //   8637: iadd
    //   8638: putfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   8641: goto -3496 -> 5145
    //   8644: aload_0
    //   8645: getfield 498	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   8648: bipush 6
    //   8650: if_icmpne +70 -> 8720
    //   8653: aload_0
    //   8654: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   8657: astore 23
    //   8659: aload_0
    //   8660: getfield 1149	org/telegram/ui/Cells/ChatMessageCell:documentAttach	Lorg/telegram/tgnet/TLRPC$Document;
    //   8663: astore 20
    //   8665: aload_0
    //   8666: getfield 1358	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   8669: astore 26
    //   8671: aload_0
    //   8672: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8675: ifnull +40 -> 8715
    //   8678: aload_0
    //   8679: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8682: getfield 1292	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   8685: astore_2
    //   8686: aload 23
    //   8688: aload 20
    //   8690: aconst_null
    //   8691: aload 26
    //   8693: aconst_null
    //   8694: aload_2
    //   8695: ldc_w 3437
    //   8698: aload_0
    //   8699: getfield 1149	org/telegram/ui/Cells/ChatMessageCell:documentAttach	Lorg/telegram/tgnet/TLRPC$Document;
    //   8702: getfield 1259	org/telegram/tgnet/TLRPC$Document:size	I
    //   8705: ldc_w 3521
    //   8708: iconst_1
    //   8709: invokevirtual 1362	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;I)V
    //   8712: goto -3460 -> 5252
    //   8715: aconst_null
    //   8716: astore_2
    //   8717: goto -31 -> 8686
    //   8720: aload_0
    //   8721: getfield 498	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   8724: iconst_4
    //   8725: if_icmpne +53 -> 8778
    //   8728: aload_0
    //   8729: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   8732: iconst_1
    //   8733: invokevirtual 1348	org/telegram/messenger/ImageReceiver:setNeedsQualityThumb	(Z)V
    //   8736: aload_0
    //   8737: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   8740: iconst_1
    //   8741: invokevirtual 1351	org/telegram/messenger/ImageReceiver:setShouldGenerateQualityThumb	(Z)V
    //   8744: aload_0
    //   8745: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   8748: aload_1
    //   8749: invokevirtual 1354	org/telegram/messenger/ImageReceiver:setParentMessageObject	(Lorg/telegram/messenger/MessageObject;)V
    //   8752: aload_0
    //   8753: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   8756: aconst_null
    //   8757: aconst_null
    //   8758: aload_0
    //   8759: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8762: getfield 1292	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   8765: aload_0
    //   8766: getfield 1358	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   8769: iconst_0
    //   8770: aconst_null
    //   8771: iconst_0
    //   8772: invokevirtual 1413	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;I)V
    //   8775: goto -3523 -> 5252
    //   8778: aload_0
    //   8779: getfield 498	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   8782: iconst_2
    //   8783: if_icmpeq +12 -> 8795
    //   8786: aload_0
    //   8787: getfield 498	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   8790: bipush 7
    //   8792: if_icmpne +207 -> 8999
    //   8795: aload 20
    //   8797: invokestatic 3525	org/telegram/messenger/FileLoader:getAttachFileName	(Lorg/telegram/tgnet/TLObject;)Ljava/lang/String;
    //   8800: astore_2
    //   8801: iconst_0
    //   8802: istore_3
    //   8803: aload 20
    //   8805: invokestatic 3427	org/telegram/messenger/MessageObject:isRoundVideoDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   8808: ifeq +111 -> 8919
    //   8811: aload_0
    //   8812: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   8815: getstatic 1846	org/telegram/messenger/AndroidUtilities:roundMessageSize	I
    //   8818: iconst_2
    //   8819: idiv
    //   8820: invokevirtual 377	org/telegram/messenger/ImageReceiver:setRoundRadius	(I)V
    //   8823: aload_0
    //   8824: getfield 342	org/telegram/ui/Cells/ChatMessageCell:currentAccount	I
    //   8827: invokestatic 396	org/telegram/messenger/DownloadController:getInstance	(I)Lorg/telegram/messenger/DownloadController;
    //   8830: aload_0
    //   8831: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   8834: invokevirtual 3528	org/telegram/messenger/DownloadController:canDownloadMedia	(Lorg/telegram/messenger/MessageObject;)Z
    //   8837: istore_3
    //   8838: aload_1
    //   8839: invokevirtual 1426	org/telegram/messenger/MessageObject:isSending	()Z
    //   8842: ifne +108 -> 8950
    //   8845: aload_1
    //   8846: getfield 2747	org/telegram/messenger/MessageObject:mediaExists	Z
    //   8849: ifne +21 -> 8870
    //   8852: aload_0
    //   8853: getfield 342	org/telegram/ui/Cells/ChatMessageCell:currentAccount	I
    //   8856: invokestatic 1378	org/telegram/messenger/FileLoader:getInstance	(I)Lorg/telegram/messenger/FileLoader;
    //   8859: aload_2
    //   8860: invokevirtual 3531	org/telegram/messenger/FileLoader:isLoadingFile	(Ljava/lang/String;)Z
    //   8863: ifne +7 -> 8870
    //   8866: iload_3
    //   8867: ifeq +83 -> 8950
    //   8870: aload_0
    //   8871: iconst_0
    //   8872: putfield 2364	org/telegram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   8875: aload_0
    //   8876: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   8879: astore 23
    //   8881: aload_0
    //   8882: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8885: ifnull +60 -> 8945
    //   8888: aload_0
    //   8889: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8892: getfield 1292	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   8895: astore_2
    //   8896: aload 23
    //   8898: aload 20
    //   8900: aconst_null
    //   8901: aload_2
    //   8902: aload_0
    //   8903: getfield 1406	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilterThumb	Ljava/lang/String;
    //   8906: aload 20
    //   8908: getfield 1259	org/telegram/tgnet/TLRPC$Document:size	I
    //   8911: aconst_null
    //   8912: iconst_0
    //   8913: invokevirtual 1413	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;I)V
    //   8916: goto -3664 -> 5252
    //   8919: aload 20
    //   8921: invokestatic 3534	org/telegram/messenger/MessageObject:isNewGifDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   8924: ifeq -86 -> 8838
    //   8927: aload_0
    //   8928: getfield 342	org/telegram/ui/Cells/ChatMessageCell:currentAccount	I
    //   8931: invokestatic 396	org/telegram/messenger/DownloadController:getInstance	(I)Lorg/telegram/messenger/DownloadController;
    //   8934: aload_0
    //   8935: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   8938: invokevirtual 3528	org/telegram/messenger/DownloadController:canDownloadMedia	(Lorg/telegram/messenger/MessageObject;)Z
    //   8941: istore_3
    //   8942: goto -104 -> 8838
    //   8945: aconst_null
    //   8946: astore_2
    //   8947: goto -51 -> 8896
    //   8950: aload_0
    //   8951: iconst_1
    //   8952: putfield 2364	org/telegram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   8955: aload_0
    //   8956: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   8959: astore 20
    //   8961: aload_0
    //   8962: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8965: ifnull +29 -> 8994
    //   8968: aload_0
    //   8969: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   8972: getfield 1292	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   8975: astore_2
    //   8976: aload 20
    //   8978: aconst_null
    //   8979: aconst_null
    //   8980: aload_2
    //   8981: aload_0
    //   8982: getfield 1406	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilterThumb	Ljava/lang/String;
    //   8985: iconst_0
    //   8986: aconst_null
    //   8987: iconst_0
    //   8988: invokevirtual 1413	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;I)V
    //   8991: goto -3739 -> 5252
    //   8994: aconst_null
    //   8995: astore_2
    //   8996: goto -20 -> 8976
    //   8999: aload_1
    //   9000: getfield 2747	org/telegram/messenger/MessageObject:mediaExists	Z
    //   9003: istore_3
    //   9004: aload_0
    //   9005: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9008: invokestatic 3525	org/telegram/messenger/FileLoader:getAttachFileName	(Lorg/telegram/tgnet/TLObject;)Ljava/lang/String;
    //   9011: astore_2
    //   9012: aload_0
    //   9013: getfield 463	org/telegram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   9016: ifne +38 -> 9054
    //   9019: iload_3
    //   9020: ifne +34 -> 9054
    //   9023: aload_0
    //   9024: getfield 342	org/telegram/ui/Cells/ChatMessageCell:currentAccount	I
    //   9027: invokestatic 396	org/telegram/messenger/DownloadController:getInstance	(I)Lorg/telegram/messenger/DownloadController;
    //   9030: aload_0
    //   9031: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   9034: invokevirtual 3528	org/telegram/messenger/DownloadController:canDownloadMedia	(Lorg/telegram/messenger/MessageObject;)Z
    //   9037: ifne +17 -> 9054
    //   9040: aload_0
    //   9041: getfield 342	org/telegram/ui/Cells/ChatMessageCell:currentAccount	I
    //   9044: invokestatic 1378	org/telegram/messenger/FileLoader:getInstance	(I)Lorg/telegram/messenger/FileLoader;
    //   9047: aload_2
    //   9048: invokevirtual 3531	org/telegram/messenger/FileLoader:isLoadingFile	(Ljava/lang/String;)Z
    //   9051: ifeq +69 -> 9120
    //   9054: aload_0
    //   9055: iconst_0
    //   9056: putfield 2364	org/telegram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   9059: aload_0
    //   9060: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   9063: astore 26
    //   9065: aload_0
    //   9066: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9069: getfield 1292	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   9072: astore 20
    //   9074: aload_0
    //   9075: getfield 1358	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   9078: astore 23
    //   9080: aload_0
    //   9081: getfield 1404	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9084: ifnull +31 -> 9115
    //   9087: aload_0
    //   9088: getfield 1404	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9091: getfield 1292	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   9094: astore_2
    //   9095: aload 26
    //   9097: aload 20
    //   9099: aload 23
    //   9101: aload_2
    //   9102: aload_0
    //   9103: getfield 1406	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilterThumb	Ljava/lang/String;
    //   9106: iconst_0
    //   9107: aconst_null
    //   9108: iconst_0
    //   9109: invokevirtual 1413	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;I)V
    //   9112: goto -3860 -> 5252
    //   9115: aconst_null
    //   9116: astore_2
    //   9117: goto -22 -> 9095
    //   9120: aload_0
    //   9121: iconst_1
    //   9122: putfield 2364	org/telegram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   9125: aload_0
    //   9126: getfield 1404	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9129: ifnull +54 -> 9183
    //   9132: aload_0
    //   9133: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   9136: aconst_null
    //   9137: aconst_null
    //   9138: aload_0
    //   9139: getfield 1404	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   9142: getfield 1292	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   9145: getstatic 2340	java/util/Locale:US	Ljava/util/Locale;
    //   9148: ldc_w 3435
    //   9151: iconst_2
    //   9152: anewarray 1242	java/lang/Object
    //   9155: dup
    //   9156: iconst_0
    //   9157: iload 8
    //   9159: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   9162: aastore
    //   9163: dup
    //   9164: iconst_1
    //   9165: iload 9
    //   9167: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   9170: aastore
    //   9171: invokestatic 2353	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   9174: iconst_0
    //   9175: aconst_null
    //   9176: iconst_0
    //   9177: invokevirtual 1413	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;I)V
    //   9180: goto -3928 -> 5252
    //   9183: aload_0
    //   9184: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   9187: aconst_null
    //   9188: checkcast 794	android/graphics/drawable/Drawable
    //   9191: invokevirtual 1368	org/telegram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   9194: goto -3942 -> 5252
    //   9197: iload 11
    //   9199: istore 6
    //   9201: aload_0
    //   9202: getfield 463	org/telegram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   9205: ifeq -3842 -> 5363
    //   9208: ldc_w 3536
    //   9211: ldc_w 3537
    //   9214: invokestatic 934	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   9217: invokevirtual 3449	java/lang/String:toUpperCase	()Ljava/lang/String;
    //   9220: astore_2
    //   9221: aload_0
    //   9222: getstatic 3540	org/telegram/ui/ActionBar/Theme:chat_gamePaint	Landroid/text/TextPaint;
    //   9225: aload_2
    //   9226: invokevirtual 1178	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   9229: f2d
    //   9230: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   9233: d2i
    //   9234: putfield 1675	org/telegram/ui/Cells/ChatMessageCell:durationWidth	I
    //   9237: aload_0
    //   9238: new 350	android/text/StaticLayout
    //   9241: dup
    //   9242: aload_2
    //   9243: getstatic 3540	org/telegram/ui/ActionBar/Theme:chat_gamePaint	Landroid/text/TextPaint;
    //   9246: aload_0
    //   9247: getfield 1675	org/telegram/ui/Cells/ChatMessageCell:durationWidth	I
    //   9250: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   9253: fconst_1
    //   9254: fconst_0
    //   9255: iconst_0
    //   9256: invokespecial 949	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   9259: putfield 1672	org/telegram/ui/Cells/ChatMessageCell:videoInfoLayout	Landroid/text/StaticLayout;
    //   9262: iload 11
    //   9264: istore 6
    //   9266: goto -3903 -> 5363
    //   9269: aload_0
    //   9270: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   9273: aconst_null
    //   9274: checkcast 794	android/graphics/drawable/Drawable
    //   9277: invokevirtual 1368	org/telegram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   9280: aload_0
    //   9281: aload_0
    //   9282: getfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   9285: ldc_w 1588
    //   9288: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9291: isub
    //   9292: putfield 777	org/telegram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   9295: aload_0
    //   9296: aload_0
    //   9297: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   9300: ldc_w 1078
    //   9303: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9306: iadd
    //   9307: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   9310: goto -3947 -> 5363
    //   9313: aload_1
    //   9314: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   9317: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   9320: getfield 3543	org/telegram/tgnet/TLRPC$MessageMedia:test	Z
    //   9323: ifeq +19 -> 9342
    //   9326: ldc_w 3545
    //   9329: ldc_w 3546
    //   9332: invokestatic 934	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   9335: invokevirtual 3449	java/lang/String:toUpperCase	()Ljava/lang/String;
    //   9338: astore_2
    //   9339: goto -3937 -> 5402
    //   9342: ldc_w 3548
    //   9345: ldc_w 3549
    //   9348: invokestatic 934	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   9351: invokevirtual 3449	java/lang/String:toUpperCase	()Ljava/lang/String;
    //   9354: astore_2
    //   9355: goto -3953 -> 5402
    //   9358: iconst_0
    //   9359: istore 9
    //   9361: goto -3777 -> 5584
    //   9364: aload_0
    //   9365: getfield 1675	org/telegram/ui/Cells/ChatMessageCell:durationWidth	I
    //   9368: iload 9
    //   9370: iadd
    //   9371: iload 6
    //   9373: invokestatic 486	java/lang/Math:max	(II)I
    //   9376: istore 9
    //   9378: goto -3742 -> 5636
    //   9381: aload_0
    //   9382: getfield 849	org/telegram/ui/Cells/ChatMessageCell:drawInstantViewType	I
    //   9385: iconst_2
    //   9386: if_icmpne +16 -> 9402
    //   9389: ldc_w 3551
    //   9392: ldc_w 3552
    //   9395: invokestatic 934	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   9398: astore_2
    //   9399: goto -3665 -> 5734
    //   9402: aload_0
    //   9403: getfield 849	org/telegram/ui/Cells/ChatMessageCell:drawInstantViewType	I
    //   9406: iconst_3
    //   9407: if_icmpne +16 -> 9423
    //   9410: ldc_w 3554
    //   9413: ldc_w 3555
    //   9416: invokestatic 934	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   9419: astore_2
    //   9420: goto -3686 -> 5734
    //   9423: ldc_w 3557
    //   9426: ldc_w 3558
    //   9429: invokestatic 934	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   9432: astore_2
    //   9433: goto -3699 -> 5734
    //   9436: iconst_0
    //   9437: istore 9
    //   9439: goto -3564 -> 5875
    //   9442: aload_0
    //   9443: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   9446: aconst_null
    //   9447: checkcast 794	android/graphics/drawable/Drawable
    //   9450: invokevirtual 1368	org/telegram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   9453: aload_0
    //   9454: iload 14
    //   9456: iload 19
    //   9458: iload 21
    //   9460: invokespecial 3461	org/telegram/ui/Cells/ChatMessageCell:calcBackgroundWidth	(III)V
    //   9463: iload 13
    //   9465: istore 22
    //   9467: goto -3552 -> 5915
    //   9470: aload_1
    //   9471: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   9474: bipush 16
    //   9476: if_icmpne +501 -> 9977
    //   9479: aload_0
    //   9480: iconst_0
    //   9481: putfield 2421	org/telegram/ui/Cells/ChatMessageCell:drawName	Z
    //   9484: aload_0
    //   9485: iconst_0
    //   9486: putfield 2431	org/telegram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   9489: aload_0
    //   9490: iconst_0
    //   9491: putfield 724	org/telegram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   9494: invokestatic 2226	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   9497: ifeq +347 -> 9844
    //   9500: invokestatic 2258	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   9503: istore 9
    //   9505: aload_0
    //   9506: getfield 2040	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   9509: ifeq +327 -> 9836
    //   9512: aload_1
    //   9513: invokevirtual 2043	org/telegram/messenger/MessageObject:needDrawAvatar	()Z
    //   9516: ifeq +320 -> 9836
    //   9519: aload_1
    //   9520: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   9523: ifne +313 -> 9836
    //   9526: ldc_w 3559
    //   9529: fstore 40
    //   9531: aload_0
    //   9532: iload 9
    //   9534: fload 40
    //   9536: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9539: isub
    //   9540: ldc_w 3560
    //   9543: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9546: invokestatic 1195	java/lang/Math:min	(II)I
    //   9549: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   9552: aload_0
    //   9553: aload_0
    //   9554: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   9557: ldc_w 487
    //   9560: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9563: isub
    //   9564: putfield 1187	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   9567: aload_0
    //   9568: invokespecial 2565	org/telegram/ui/Cells/ChatMessageCell:getMaxNameWidth	()I
    //   9571: ldc_w 1074
    //   9574: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9577: isub
    //   9578: istore 9
    //   9580: iload 9
    //   9582: ifge +9800 -> 19382
    //   9585: ldc_w 587
    //   9588: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9591: istore 9
    //   9593: invokestatic 2467	org/telegram/messenger/LocaleController:getInstance	()Lorg/telegram/messenger/LocaleController;
    //   9596: getfield 2471	org/telegram/messenger/LocaleController:formatterDay	Lorg/telegram/messenger/time/FastDateFormat;
    //   9599: aload_1
    //   9600: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   9603: getfield 1966	org/telegram/tgnet/TLRPC$Message:date	I
    //   9606: i2l
    //   9607: ldc2_w 919
    //   9610: lmul
    //   9611: invokevirtual 2475	org/telegram/messenger/time/FastDateFormat:format	(J)Ljava/lang/String;
    //   9614: astore 20
    //   9616: aload_1
    //   9617: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   9620: getfield 2052	org/telegram/tgnet/TLRPC$Message:action	Lorg/telegram/tgnet/TLRPC$MessageAction;
    //   9623: checkcast 3562	org/telegram/tgnet/TLRPC$TL_messageActionPhoneCall
    //   9626: astore 23
    //   9628: aload 23
    //   9630: getfield 3563	org/telegram/tgnet/TLRPC$TL_messageActionPhoneCall:reason	Lorg/telegram/tgnet/TLRPC$PhoneCallDiscardReason;
    //   9633: instanceof 2060
    //   9636: istore_3
    //   9637: aload_1
    //   9638: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   9641: ifeq +282 -> 9923
    //   9644: iload_3
    //   9645: ifeq +265 -> 9910
    //   9648: ldc_w 3565
    //   9651: ldc_w 3566
    //   9654: invokestatic 934	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   9657: astore_2
    //   9658: aload 20
    //   9660: astore 10
    //   9662: aload 23
    //   9664: getfield 3567	org/telegram/tgnet/TLRPC$TL_messageActionPhoneCall:duration	I
    //   9667: ifle +37 -> 9704
    //   9670: new 1320	java/lang/StringBuilder
    //   9673: dup
    //   9674: invokespecial 1321	java/lang/StringBuilder:<init>	()V
    //   9677: aload 20
    //   9679: invokevirtual 1325	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   9682: ldc_w 2477
    //   9685: invokevirtual 1325	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   9688: aload 23
    //   9690: getfield 3567	org/telegram/tgnet/TLRPC$TL_messageActionPhoneCall:duration	I
    //   9693: invokestatic 3570	org/telegram/messenger/LocaleController:formatCallDuration	(I)Ljava/lang/String;
    //   9696: invokevirtual 1325	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   9699: invokevirtual 1333	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   9702: astore 10
    //   9704: aload_0
    //   9705: new 350	android/text/StaticLayout
    //   9708: dup
    //   9709: aload_2
    //   9710: getstatic 1211	org/telegram/ui/ActionBar/Theme:chat_audioTitlePaint	Landroid/text/TextPaint;
    //   9713: iload 9
    //   9715: i2f
    //   9716: getstatic 1217	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   9719: invokestatic 1221	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   9722: getstatic 1211	org/telegram/ui/ActionBar/Theme:chat_audioTitlePaint	Landroid/text/TextPaint;
    //   9725: iload 9
    //   9727: fconst_2
    //   9728: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9731: iadd
    //   9732: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   9735: fconst_1
    //   9736: fconst_0
    //   9737: iconst_0
    //   9738: invokespecial 949	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   9741: putfield 1595	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   9744: aload_0
    //   9745: new 350	android/text/StaticLayout
    //   9748: dup
    //   9749: aload 10
    //   9751: getstatic 2019	org/telegram/ui/ActionBar/Theme:chat_contactPhonePaint	Landroid/text/TextPaint;
    //   9754: iload 9
    //   9756: i2f
    //   9757: getstatic 1217	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   9760: invokestatic 1221	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   9763: getstatic 2019	org/telegram/ui/ActionBar/Theme:chat_contactPhonePaint	Landroid/text/TextPaint;
    //   9766: iload 9
    //   9768: fconst_2
    //   9769: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9772: iadd
    //   9773: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   9776: fconst_1
    //   9777: fconst_0
    //   9778: iconst_0
    //   9779: invokespecial 949	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   9782: putfield 951	org/telegram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   9785: aload_0
    //   9786: aload_1
    //   9787: invokespecial 3338	org/telegram/ui/Cells/ChatMessageCell:setMessageObjectInternal	(Lorg/telegram/messenger/MessageObject;)V
    //   9790: aload_0
    //   9791: ldc_w 3126
    //   9794: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9797: aload_0
    //   9798: getfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   9801: iadd
    //   9802: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   9805: iload 13
    //   9807: istore 22
    //   9809: aload_0
    //   9810: getfield 1783	org/telegram/ui/Cells/ChatMessageCell:drawPinnedTop	Z
    //   9813: ifeq -3898 -> 5915
    //   9816: aload_0
    //   9817: aload_0
    //   9818: getfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   9821: fconst_1
    //   9822: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9825: isub
    //   9826: putfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   9829: iload 13
    //   9831: istore 22
    //   9833: goto -3918 -> 5915
    //   9836: ldc_w 1074
    //   9839: fstore 40
    //   9841: goto -310 -> 9531
    //   9844: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   9847: getfield 2253	android/graphics/Point:x	I
    //   9850: istore 9
    //   9852: aload_0
    //   9853: getfield 2040	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   9856: ifeq +46 -> 9902
    //   9859: aload_1
    //   9860: invokevirtual 2043	org/telegram/messenger/MessageObject:needDrawAvatar	()Z
    //   9863: ifeq +39 -> 9902
    //   9866: aload_1
    //   9867: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   9870: ifne +32 -> 9902
    //   9873: ldc_w 3559
    //   9876: fstore 40
    //   9878: aload_0
    //   9879: iload 9
    //   9881: fload 40
    //   9883: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9886: isub
    //   9887: ldc_w 3560
    //   9890: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   9893: invokestatic 1195	java/lang/Math:min	(II)I
    //   9896: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   9899: goto -347 -> 9552
    //   9902: ldc_w 1074
    //   9905: fstore 40
    //   9907: goto -29 -> 9878
    //   9910: ldc_w 3572
    //   9913: ldc_w 3573
    //   9916: invokestatic 934	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   9919: astore_2
    //   9920: goto -262 -> 9658
    //   9923: iload_3
    //   9924: ifeq +16 -> 9940
    //   9927: ldc_w 3575
    //   9930: ldc_w 3576
    //   9933: invokestatic 934	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   9936: astore_2
    //   9937: goto -279 -> 9658
    //   9940: aload 23
    //   9942: getfield 3563	org/telegram/tgnet/TLRPC$TL_messageActionPhoneCall:reason	Lorg/telegram/tgnet/TLRPC$PhoneCallDiscardReason;
    //   9945: instanceof 2062
    //   9948: ifeq +16 -> 9964
    //   9951: ldc_w 3578
    //   9954: ldc_w 3579
    //   9957: invokestatic 934	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   9960: astore_2
    //   9961: goto -303 -> 9658
    //   9964: ldc_w 3581
    //   9967: ldc_w 3582
    //   9970: invokestatic 934	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   9973: astore_2
    //   9974: goto -316 -> 9658
    //   9977: aload_1
    //   9978: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   9981: bipush 12
    //   9983: if_icmpne +706 -> 10689
    //   9986: aload_0
    //   9987: iconst_0
    //   9988: putfield 2421	org/telegram/ui/Cells/ChatMessageCell:drawName	Z
    //   9991: aload_0
    //   9992: iconst_1
    //   9993: putfield 2431	org/telegram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   9996: aload_0
    //   9997: iconst_1
    //   9998: putfield 724	org/telegram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   10001: aload_0
    //   10002: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   10005: ldc_w 1883
    //   10008: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10011: invokevirtual 377	org/telegram/messenger/ImageReceiver:setRoundRadius	(I)V
    //   10014: invokestatic 2226	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   10017: ifeq +527 -> 10544
    //   10020: invokestatic 2258	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   10023: istore 9
    //   10025: aload_0
    //   10026: getfield 2040	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   10029: ifeq +507 -> 10536
    //   10032: aload_1
    //   10033: invokevirtual 2043	org/telegram/messenger/MessageObject:needDrawAvatar	()Z
    //   10036: ifeq +500 -> 10536
    //   10039: aload_1
    //   10040: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   10043: ifne +493 -> 10536
    //   10046: ldc_w 3559
    //   10049: fstore 40
    //   10051: aload_0
    //   10052: iload 9
    //   10054: fload 40
    //   10056: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10059: isub
    //   10060: ldc_w 3560
    //   10063: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10066: invokestatic 1195	java/lang/Math:min	(II)I
    //   10069: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   10072: aload_0
    //   10073: aload_0
    //   10074: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   10077: ldc_w 487
    //   10080: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10083: isub
    //   10084: putfield 1187	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   10087: aload_1
    //   10088: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   10091: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   10094: getfield 1104	org/telegram/tgnet/TLRPC$MessageMedia:user_id	I
    //   10097: istore 9
    //   10099: aload_0
    //   10100: getfield 342	org/telegram/ui/Cells/ChatMessageCell:currentAccount	I
    //   10103: invokestatic 1006	org/telegram/messenger/MessagesController:getInstance	(I)Lorg/telegram/messenger/MessagesController;
    //   10106: iload 9
    //   10108: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   10111: invokevirtual 1019	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   10114: astore 23
    //   10116: aload_0
    //   10117: invokespecial 2565	org/telegram/ui/Cells/ChatMessageCell:getMaxNameWidth	()I
    //   10120: ldc_w 3583
    //   10123: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10126: isub
    //   10127: istore 9
    //   10129: iload 9
    //   10131: ifge +9248 -> 19379
    //   10134: ldc_w 587
    //   10137: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10140: istore 9
    //   10142: aconst_null
    //   10143: astore_2
    //   10144: aconst_null
    //   10145: astore 10
    //   10147: aload 23
    //   10149: ifnull +32 -> 10181
    //   10152: aload 10
    //   10154: astore_2
    //   10155: aload 23
    //   10157: getfield 2397	org/telegram/tgnet/TLRPC$User:photo	Lorg/telegram/tgnet/TLRPC$UserProfilePhoto;
    //   10160: ifnull +12 -> 10172
    //   10163: aload 23
    //   10165: getfield 2397	org/telegram/tgnet/TLRPC$User:photo	Lorg/telegram/tgnet/TLRPC$UserProfilePhoto;
    //   10168: getfield 2402	org/telegram/tgnet/TLRPC$UserProfilePhoto:photo_small	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   10171: astore_2
    //   10172: aload_0
    //   10173: getfield 404	org/telegram/ui/Cells/ChatMessageCell:contactAvatarDrawable	Lorg/telegram/ui/Components/AvatarDrawable;
    //   10176: aload 23
    //   10178: invokevirtual 2543	org/telegram/ui/Components/AvatarDrawable:setInfo	(Lorg/telegram/tgnet/TLRPC$User;)V
    //   10181: aload_0
    //   10182: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   10185: astore 20
    //   10187: aload 23
    //   10189: ifnull +421 -> 10610
    //   10192: aload_0
    //   10193: getfield 404	org/telegram/ui/Cells/ChatMessageCell:contactAvatarDrawable	Lorg/telegram/ui/Components/AvatarDrawable;
    //   10196: astore 10
    //   10198: aload 20
    //   10200: aload_2
    //   10201: ldc_w 2545
    //   10204: aload 10
    //   10206: aconst_null
    //   10207: iconst_0
    //   10208: invokevirtual 2548	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Ljava/lang/String;I)V
    //   10211: aload_1
    //   10212: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   10215: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   10218: getfield 3586	org/telegram/tgnet/TLRPC$MessageMedia:phone_number	Ljava/lang/String;
    //   10221: astore_2
    //   10222: aload_2
    //   10223: ifnull +418 -> 10641
    //   10226: aload_2
    //   10227: invokevirtual 1054	java/lang/String:length	()I
    //   10230: ifeq +411 -> 10641
    //   10233: invokestatic 3591	org/telegram/PhoneFormat/PhoneFormat:getInstance	()Lorg/telegram/PhoneFormat/PhoneFormat;
    //   10236: aload_2
    //   10237: invokevirtual 3594	org/telegram/PhoneFormat/PhoneFormat:format	(Ljava/lang/String;)Ljava/lang/String;
    //   10240: astore_2
    //   10241: aload_1
    //   10242: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   10245: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   10248: getfield 3595	org/telegram/tgnet/TLRPC$MessageMedia:first_name	Ljava/lang/String;
    //   10251: aload_1
    //   10252: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   10255: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   10258: getfield 3596	org/telegram/tgnet/TLRPC$MessageMedia:last_name	Ljava/lang/String;
    //   10261: invokestatic 2516	org/telegram/messenger/ContactsController:formatName	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   10264: bipush 10
    //   10266: bipush 32
    //   10268: invokevirtual 1208	java/lang/String:replace	(CC)Ljava/lang/String;
    //   10271: astore 20
    //   10273: aload 20
    //   10275: astore 10
    //   10277: aload 20
    //   10279: invokeinterface 2192 1 0
    //   10284: ifne +6 -> 10290
    //   10287: aload_2
    //   10288: astore 10
    //   10290: aload_0
    //   10291: new 350	android/text/StaticLayout
    //   10294: dup
    //   10295: aload 10
    //   10297: getstatic 2077	org/telegram/ui/ActionBar/Theme:chat_contactNamePaint	Landroid/text/TextPaint;
    //   10300: iload 9
    //   10302: i2f
    //   10303: getstatic 1217	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   10306: invokestatic 1221	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   10309: getstatic 2077	org/telegram/ui/ActionBar/Theme:chat_contactNamePaint	Landroid/text/TextPaint;
    //   10312: iload 9
    //   10314: fconst_2
    //   10315: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10318: iadd
    //   10319: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   10322: fconst_1
    //   10323: fconst_0
    //   10324: iconst_0
    //   10325: invokespecial 949	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   10328: putfield 1595	org/telegram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   10331: aload_0
    //   10332: new 350	android/text/StaticLayout
    //   10335: dup
    //   10336: aload_2
    //   10337: bipush 10
    //   10339: bipush 32
    //   10341: invokevirtual 1208	java/lang/String:replace	(CC)Ljava/lang/String;
    //   10344: getstatic 2019	org/telegram/ui/ActionBar/Theme:chat_contactPhonePaint	Landroid/text/TextPaint;
    //   10347: iload 9
    //   10349: i2f
    //   10350: getstatic 1217	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   10353: invokestatic 1221	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   10356: getstatic 2019	org/telegram/ui/ActionBar/Theme:chat_contactPhonePaint	Landroid/text/TextPaint;
    //   10359: iload 9
    //   10361: fconst_2
    //   10362: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10365: iadd
    //   10366: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   10369: fconst_1
    //   10370: fconst_0
    //   10371: iconst_0
    //   10372: invokespecial 949	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   10375: putfield 951	org/telegram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   10378: aload_0
    //   10379: aload_1
    //   10380: invokespecial 3338	org/telegram/ui/Cells/ChatMessageCell:setMessageObjectInternal	(Lorg/telegram/messenger/MessageObject;)V
    //   10383: aload_0
    //   10384: getfield 2431	org/telegram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   10387: ifeq +267 -> 10654
    //   10390: aload_1
    //   10391: invokevirtual 2626	org/telegram/messenger/MessageObject:needDrawForwarded	()Z
    //   10394: ifeq +260 -> 10654
    //   10397: aload_0
    //   10398: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   10401: ifnull +13 -> 10414
    //   10404: aload_0
    //   10405: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   10408: getfield 2270	org/telegram/messenger/MessageObject$GroupedMessagePosition:minY	B
    //   10411: ifne +243 -> 10654
    //   10414: aload_0
    //   10415: aload_0
    //   10416: getfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   10419: ldc_w 1775
    //   10422: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10425: iadd
    //   10426: putfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   10429: aload_0
    //   10430: ldc_w 3597
    //   10433: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10436: aload_0
    //   10437: getfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   10440: iadd
    //   10441: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   10444: aload_0
    //   10445: getfield 1783	org/telegram/ui/Cells/ChatMessageCell:drawPinnedTop	Z
    //   10448: ifeq +16 -> 10464
    //   10451: aload_0
    //   10452: aload_0
    //   10453: getfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   10456: fconst_1
    //   10457: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10460: isub
    //   10461: putfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   10464: iload 13
    //   10466: istore 22
    //   10468: aload_0
    //   10469: getfield 951	org/telegram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   10472: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   10475: ifle -4560 -> 5915
    //   10478: iload 13
    //   10480: istore 22
    //   10482: aload_0
    //   10483: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   10486: ldc_w 3583
    //   10489: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10492: isub
    //   10493: aload_0
    //   10494: getfield 951	org/telegram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   10497: iconst_0
    //   10498: invokevirtual 662	android/text/StaticLayout:getLineWidth	(I)F
    //   10501: f2d
    //   10502: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   10505: d2i
    //   10506: isub
    //   10507: aload_0
    //   10508: getfield 493	org/telegram/ui/Cells/ChatMessageCell:timeWidth	I
    //   10511: if_icmpge -4596 -> 5915
    //   10514: aload_0
    //   10515: aload_0
    //   10516: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   10519: ldc_w 1629
    //   10522: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10525: iadd
    //   10526: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   10529: iload 13
    //   10531: istore 22
    //   10533: goto -4618 -> 5915
    //   10536: ldc_w 1074
    //   10539: fstore 40
    //   10541: goto -490 -> 10051
    //   10544: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   10547: getfield 2253	android/graphics/Point:x	I
    //   10550: istore 9
    //   10552: aload_0
    //   10553: getfield 2040	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   10556: ifeq +46 -> 10602
    //   10559: aload_1
    //   10560: invokevirtual 2043	org/telegram/messenger/MessageObject:needDrawAvatar	()Z
    //   10563: ifeq +39 -> 10602
    //   10566: aload_1
    //   10567: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   10570: ifne +32 -> 10602
    //   10573: ldc_w 3559
    //   10576: fstore 40
    //   10578: aload_0
    //   10579: iload 9
    //   10581: fload 40
    //   10583: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10586: isub
    //   10587: ldc_w 3560
    //   10590: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10593: invokestatic 1195	java/lang/Math:min	(II)I
    //   10596: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   10599: goto -527 -> 10072
    //   10602: ldc_w 1074
    //   10605: fstore 40
    //   10607: goto -29 -> 10578
    //   10610: getstatic 3601	org/telegram/ui/ActionBar/Theme:chat_contactDrawable	[Landroid/graphics/drawable/Drawable;
    //   10613: astore 10
    //   10615: aload_1
    //   10616: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   10619: ifeq +16 -> 10635
    //   10622: iconst_1
    //   10623: istore 8
    //   10625: aload 10
    //   10627: iload 8
    //   10629: aaload
    //   10630: astore 10
    //   10632: goto -434 -> 10198
    //   10635: iconst_0
    //   10636: istore 8
    //   10638: goto -13 -> 10625
    //   10641: ldc_w 3603
    //   10644: ldc_w 3604
    //   10647: invokestatic 934	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   10650: astore_2
    //   10651: goto -410 -> 10241
    //   10654: aload_0
    //   10655: getfield 2563	org/telegram/ui/Cells/ChatMessageCell:drawNameLayout	Z
    //   10658: ifeq -229 -> 10429
    //   10661: aload_1
    //   10662: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   10665: getfield 1067	org/telegram/tgnet/TLRPC$Message:reply_to_msg_id	I
    //   10668: ifne -239 -> 10429
    //   10671: aload_0
    //   10672: aload_0
    //   10673: getfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   10676: ldc_w 616
    //   10679: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10682: iadd
    //   10683: putfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   10686: goto -257 -> 10429
    //   10689: aload_1
    //   10690: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   10693: iconst_2
    //   10694: if_icmpne +201 -> 10895
    //   10697: aload_0
    //   10698: iconst_1
    //   10699: putfield 2431	org/telegram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   10702: invokestatic 2226	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   10705: ifeq +124 -> 10829
    //   10708: invokestatic 2258	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   10711: istore 9
    //   10713: aload_0
    //   10714: getfield 2040	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   10717: ifeq +104 -> 10821
    //   10720: aload_1
    //   10721: invokevirtual 2043	org/telegram/messenger/MessageObject:needDrawAvatar	()Z
    //   10724: ifeq +97 -> 10821
    //   10727: aload_1
    //   10728: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   10731: ifne +90 -> 10821
    //   10734: ldc_w 3559
    //   10737: fstore 40
    //   10739: aload_0
    //   10740: iload 9
    //   10742: fload 40
    //   10744: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10747: isub
    //   10748: ldc_w 3560
    //   10751: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10754: invokestatic 1195	java/lang/Math:min	(II)I
    //   10757: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   10760: aload_0
    //   10761: aload_0
    //   10762: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   10765: aload_1
    //   10766: invokespecial 3508	org/telegram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/telegram/messenger/MessageObject;)I
    //   10769: pop
    //   10770: aload_0
    //   10771: aload_1
    //   10772: invokespecial 3338	org/telegram/ui/Cells/ChatMessageCell:setMessageObjectInternal	(Lorg/telegram/messenger/MessageObject;)V
    //   10775: aload_0
    //   10776: ldc_w 3597
    //   10779: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10782: aload_0
    //   10783: getfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   10786: iadd
    //   10787: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   10790: iload 13
    //   10792: istore 22
    //   10794: aload_0
    //   10795: getfield 1783	org/telegram/ui/Cells/ChatMessageCell:drawPinnedTop	Z
    //   10798: ifeq -4883 -> 5915
    //   10801: aload_0
    //   10802: aload_0
    //   10803: getfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   10806: fconst_1
    //   10807: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10810: isub
    //   10811: putfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   10814: iload 13
    //   10816: istore 22
    //   10818: goto -4903 -> 5915
    //   10821: ldc_w 1074
    //   10824: fstore 40
    //   10826: goto -87 -> 10739
    //   10829: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   10832: getfield 2253	android/graphics/Point:x	I
    //   10835: istore 9
    //   10837: aload_0
    //   10838: getfield 2040	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   10841: ifeq +46 -> 10887
    //   10844: aload_1
    //   10845: invokevirtual 2043	org/telegram/messenger/MessageObject:needDrawAvatar	()Z
    //   10848: ifeq +39 -> 10887
    //   10851: aload_1
    //   10852: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   10855: ifne +32 -> 10887
    //   10858: ldc_w 3559
    //   10861: fstore 40
    //   10863: aload_0
    //   10864: iload 9
    //   10866: fload 40
    //   10868: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10871: isub
    //   10872: ldc_w 3560
    //   10875: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10878: invokestatic 1195	java/lang/Math:min	(II)I
    //   10881: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   10884: goto -124 -> 10760
    //   10887: ldc_w 1074
    //   10890: fstore 40
    //   10892: goto -29 -> 10863
    //   10895: aload_1
    //   10896: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   10899: bipush 14
    //   10901: if_icmpne +196 -> 11097
    //   10904: invokestatic 2226	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   10907: ifeq +124 -> 11031
    //   10910: invokestatic 2258	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   10913: istore 9
    //   10915: aload_0
    //   10916: getfield 2040	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   10919: ifeq +104 -> 11023
    //   10922: aload_1
    //   10923: invokevirtual 2043	org/telegram/messenger/MessageObject:needDrawAvatar	()Z
    //   10926: ifeq +97 -> 11023
    //   10929: aload_1
    //   10930: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   10933: ifne +90 -> 11023
    //   10936: ldc_w 3559
    //   10939: fstore 40
    //   10941: aload_0
    //   10942: iload 9
    //   10944: fload 40
    //   10946: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10949: isub
    //   10950: ldc_w 3560
    //   10953: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10956: invokestatic 1195	java/lang/Math:min	(II)I
    //   10959: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   10962: aload_0
    //   10963: aload_0
    //   10964: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   10967: aload_1
    //   10968: invokespecial 3508	org/telegram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/telegram/messenger/MessageObject;)I
    //   10971: pop
    //   10972: aload_0
    //   10973: aload_1
    //   10974: invokespecial 3338	org/telegram/ui/Cells/ChatMessageCell:setMessageObjectInternal	(Lorg/telegram/messenger/MessageObject;)V
    //   10977: aload_0
    //   10978: ldc_w 566
    //   10981: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   10984: aload_0
    //   10985: getfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   10988: iadd
    //   10989: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   10992: iload 13
    //   10994: istore 22
    //   10996: aload_0
    //   10997: getfield 1783	org/telegram/ui/Cells/ChatMessageCell:drawPinnedTop	Z
    //   11000: ifeq -5085 -> 5915
    //   11003: aload_0
    //   11004: aload_0
    //   11005: getfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   11008: fconst_1
    //   11009: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11012: isub
    //   11013: putfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   11016: iload 13
    //   11018: istore 22
    //   11020: goto -5105 -> 5915
    //   11023: ldc_w 1074
    //   11026: fstore 40
    //   11028: goto -87 -> 10941
    //   11031: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   11034: getfield 2253	android/graphics/Point:x	I
    //   11037: istore 9
    //   11039: aload_0
    //   11040: getfield 2040	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   11043: ifeq +46 -> 11089
    //   11046: aload_1
    //   11047: invokevirtual 2043	org/telegram/messenger/MessageObject:needDrawAvatar	()Z
    //   11050: ifeq +39 -> 11089
    //   11053: aload_1
    //   11054: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   11057: ifne +32 -> 11089
    //   11060: ldc_w 3559
    //   11063: fstore 40
    //   11065: aload_0
    //   11066: iload 9
    //   11068: fload 40
    //   11070: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11073: isub
    //   11074: ldc_w 3560
    //   11077: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11080: invokestatic 1195	java/lang/Math:min	(II)I
    //   11083: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   11086: goto -124 -> 10962
    //   11089: ldc_w 1074
    //   11092: fstore 40
    //   11094: goto -29 -> 11065
    //   11097: aload_1
    //   11098: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   11101: getfield 971	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$MessageFwdHeader;
    //   11104: ifnull +753 -> 11857
    //   11107: aload_1
    //   11108: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   11111: bipush 13
    //   11113: if_icmpeq +744 -> 11857
    //   11116: iconst_1
    //   11117: istore_3
    //   11118: aload_0
    //   11119: iload_3
    //   11120: putfield 2431	org/telegram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   11123: aload_1
    //   11124: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   11127: bipush 9
    //   11129: if_icmpeq +733 -> 11862
    //   11132: iconst_1
    //   11133: istore_3
    //   11134: aload_0
    //   11135: iload_3
    //   11136: putfield 615	org/telegram/ui/Cells/ChatMessageCell:mediaBackground	Z
    //   11139: aload_0
    //   11140: iconst_1
    //   11141: putfield 783	org/telegram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   11144: aload_0
    //   11145: iconst_1
    //   11146: putfield 724	org/telegram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   11149: iconst_0
    //   11150: istore 22
    //   11152: iconst_0
    //   11153: istore 21
    //   11155: iconst_0
    //   11156: istore 8
    //   11158: iconst_0
    //   11159: istore 9
    //   11161: iconst_0
    //   11162: istore 19
    //   11164: iconst_0
    //   11165: istore 29
    //   11167: iconst_0
    //   11168: istore 14
    //   11170: aload_1
    //   11171: getfield 882	org/telegram/messenger/MessageObject:gifState	F
    //   11174: fconst_2
    //   11175: fcmpl
    //   11176: ifeq +31 -> 11207
    //   11179: getstatic 833	org/telegram/messenger/SharedConfig:autoplayGifs	Z
    //   11182: ifne +25 -> 11207
    //   11185: aload_1
    //   11186: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   11189: bipush 8
    //   11191: if_icmpeq +11 -> 11202
    //   11194: aload_1
    //   11195: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   11198: iconst_5
    //   11199: if_icmpne +8 -> 11207
    //   11202: aload_1
    //   11203: fconst_1
    //   11204: putfield 882	org/telegram/messenger/MessageObject:gifState	F
    //   11207: aload_1
    //   11208: invokevirtual 1416	org/telegram/messenger/MessageObject:isRoundVideo	()Z
    //   11211: ifeq +661 -> 11872
    //   11214: aload_0
    //   11215: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   11218: iconst_1
    //   11219: invokevirtual 3303	org/telegram/messenger/ImageReceiver:setAllowDecodeSingleFrame	(Z)V
    //   11222: aload_0
    //   11223: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   11226: astore_2
    //   11227: invokestatic 863	org/telegram/messenger/MediaController:getInstance	()Lorg/telegram/messenger/MediaController;
    //   11230: invokevirtual 2742	org/telegram/messenger/MediaController:getPlayingMessageObject	()Lorg/telegram/messenger/MessageObject;
    //   11233: ifnonnull +634 -> 11867
    //   11236: iconst_1
    //   11237: istore_3
    //   11238: aload_2
    //   11239: iload_3
    //   11240: invokevirtual 885	org/telegram/messenger/ImageReceiver:setAllowStartAnimation	(Z)V
    //   11243: aload_0
    //   11244: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   11247: aload_1
    //   11248: invokevirtual 1253	org/telegram/messenger/MessageObject:needDrawBluredPreview	()Z
    //   11251: invokevirtual 3607	org/telegram/messenger/ImageReceiver:setForcePreview	(Z)V
    //   11254: aload_1
    //   11255: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   11258: bipush 9
    //   11260: if_icmpne +767 -> 12027
    //   11263: invokestatic 2226	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   11266: ifeq +643 -> 11909
    //   11269: invokestatic 2258	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   11272: istore 9
    //   11274: aload_0
    //   11275: getfield 2040	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   11278: ifeq +623 -> 11901
    //   11281: aload_1
    //   11282: invokevirtual 2043	org/telegram/messenger/MessageObject:needDrawAvatar	()Z
    //   11285: ifeq +616 -> 11901
    //   11288: aload_1
    //   11289: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   11292: ifne +609 -> 11901
    //   11295: ldc_w 3559
    //   11298: fstore 40
    //   11300: aload_0
    //   11301: iload 9
    //   11303: fload 40
    //   11305: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11308: isub
    //   11309: ldc_w 3560
    //   11312: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11315: invokestatic 1195	java/lang/Math:min	(II)I
    //   11318: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   11321: aload_0
    //   11322: aload_1
    //   11323: invokespecial 3293	org/telegram/ui/Cells/ChatMessageCell:checkNeedDrawShareButton	(Lorg/telegram/messenger/MessageObject;)Z
    //   11326: ifeq +18 -> 11344
    //   11329: aload_0
    //   11330: aload_0
    //   11331: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   11334: ldc_w 1077
    //   11337: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11340: isub
    //   11341: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   11344: aload_0
    //   11345: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   11348: ldc_w 3608
    //   11351: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11354: isub
    //   11355: istore 8
    //   11357: aload_0
    //   11358: iload 8
    //   11360: aload_1
    //   11361: invokespecial 3508	org/telegram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/telegram/messenger/MessageObject;)I
    //   11364: pop
    //   11365: iload 8
    //   11367: istore 9
    //   11369: aload_1
    //   11370: getfield 3184	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   11373: invokestatic 847	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   11376: ifne +14 -> 11390
    //   11379: iload 8
    //   11381: ldc_w 1201
    //   11384: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11387: iadd
    //   11388: istore 9
    //   11390: aload_0
    //   11391: getfield 724	org/telegram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   11394: ifeq +581 -> 11975
    //   11397: ldc_w 1201
    //   11400: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11403: istore 6
    //   11405: ldc_w 1201
    //   11408: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11411: istore 21
    //   11413: aload_0
    //   11414: iload 9
    //   11416: putfield 1187	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   11419: iload 14
    //   11421: istore 30
    //   11423: iload 11
    //   11425: istore 22
    //   11427: iload 21
    //   11429: istore 9
    //   11431: iload 6
    //   11433: istore 8
    //   11435: aload_0
    //   11436: getfield 724	org/telegram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   11439: ifne +131 -> 11570
    //   11442: iload 14
    //   11444: istore 30
    //   11446: iload 11
    //   11448: istore 22
    //   11450: iload 21
    //   11452: istore 9
    //   11454: iload 6
    //   11456: istore 8
    //   11458: aload_1
    //   11459: getfield 3184	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   11462: invokestatic 847	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   11465: ifeq +105 -> 11570
    //   11468: iload 14
    //   11470: istore 30
    //   11472: iload 11
    //   11474: istore 22
    //   11476: iload 21
    //   11478: istore 9
    //   11480: iload 6
    //   11482: istore 8
    //   11484: aload_0
    //   11485: getfield 1270	org/telegram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   11488: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   11491: ifle +79 -> 11570
    //   11494: aload_0
    //   11495: aload_1
    //   11496: invokespecial 1191	org/telegram/ui/Cells/ChatMessageCell:measureTime	(Lorg/telegram/messenger/MessageObject;)V
    //   11499: iload 14
    //   11501: istore 30
    //   11503: iload 11
    //   11505: istore 22
    //   11507: iload 21
    //   11509: istore 9
    //   11511: iload 6
    //   11513: istore 8
    //   11515: aload_0
    //   11516: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   11519: ldc_w 3304
    //   11522: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11525: isub
    //   11526: aload_0
    //   11527: getfield 1270	org/telegram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   11530: iconst_0
    //   11531: invokevirtual 662	android/text/StaticLayout:getLineWidth	(I)F
    //   11534: f2d
    //   11535: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   11538: d2i
    //   11539: isub
    //   11540: aload_0
    //   11541: getfield 493	org/telegram/ui/Cells/ChatMessageCell:timeWidth	I
    //   11544: if_icmpge +26 -> 11570
    //   11547: iload 21
    //   11549: ldc_w 1629
    //   11552: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11555: iadd
    //   11556: istore 9
    //   11558: iload 6
    //   11560: istore 8
    //   11562: iload 11
    //   11564: istore 22
    //   11566: iload 14
    //   11568: istore 30
    //   11570: aload_0
    //   11571: aload_1
    //   11572: invokespecial 3338	org/telegram/ui/Cells/ChatMessageCell:setMessageObjectInternal	(Lorg/telegram/messenger/MessageObject;)V
    //   11575: aload_0
    //   11576: getfield 2431	org/telegram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   11579: ifeq +6530 -> 18109
    //   11582: aload_1
    //   11583: invokevirtual 2626	org/telegram/messenger/MessageObject:needDrawForwarded	()Z
    //   11586: ifeq +6523 -> 18109
    //   11589: aload_0
    //   11590: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   11593: ifnull +13 -> 11606
    //   11596: aload_0
    //   11597: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   11600: getfield 2270	org/telegram/messenger/MessageObject$GroupedMessagePosition:minY	B
    //   11603: ifne +6506 -> 18109
    //   11606: aload_1
    //   11607: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   11610: iconst_5
    //   11611: if_icmpeq +18 -> 11629
    //   11614: aload_0
    //   11615: aload_0
    //   11616: getfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   11619: ldc_w 1775
    //   11622: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11625: iadd
    //   11626: putfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   11629: aload_0
    //   11630: ldc_w 478
    //   11633: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11636: iload 9
    //   11638: iadd
    //   11639: aload_0
    //   11640: getfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   11643: iadd
    //   11644: iload 30
    //   11646: iadd
    //   11647: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   11650: aload_0
    //   11651: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   11654: ifnull +31 -> 11685
    //   11657: aload_0
    //   11658: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   11661: getfield 2210	org/telegram/messenger/MessageObject$GroupedMessagePosition:flags	I
    //   11664: bipush 8
    //   11666: iand
    //   11667: ifne +18 -> 11685
    //   11670: aload_0
    //   11671: aload_0
    //   11672: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   11675: ldc_w 1545
    //   11678: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11681: isub
    //   11682: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   11685: iconst_0
    //   11686: istore 11
    //   11688: iconst_0
    //   11689: istore 21
    //   11691: iload 9
    //   11693: istore 6
    //   11695: iload 8
    //   11697: istore 14
    //   11699: aload_0
    //   11700: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   11703: ifnull +101 -> 11804
    //   11706: iload 8
    //   11708: aload_0
    //   11709: aload_0
    //   11710: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   11713: invokespecial 3610	org/telegram/ui/Cells/ChatMessageCell:getAdditionalWidthForPosition	(Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;)I
    //   11716: iadd
    //   11717: istore 30
    //   11719: iload 21
    //   11721: istore 8
    //   11723: iload 9
    //   11725: istore 21
    //   11727: aload_0
    //   11728: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   11731: getfield 2210	org/telegram/messenger/MessageObject$GroupedMessagePosition:flags	I
    //   11734: iconst_4
    //   11735: iand
    //   11736: ifne +24 -> 11760
    //   11739: iload 9
    //   11741: ldc_w 1078
    //   11744: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11747: iadd
    //   11748: istore 21
    //   11750: iconst_0
    //   11751: ldc_w 1078
    //   11754: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11757: isub
    //   11758: istore 8
    //   11760: iload 8
    //   11762: istore 11
    //   11764: iload 21
    //   11766: istore 6
    //   11768: iload 30
    //   11770: istore 14
    //   11772: aload_0
    //   11773: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   11776: getfield 2210	org/telegram/messenger/MessageObject$GroupedMessagePosition:flags	I
    //   11779: bipush 8
    //   11781: iand
    //   11782: ifne +22 -> 11804
    //   11785: iload 21
    //   11787: ldc_w 1078
    //   11790: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11793: iadd
    //   11794: istore 6
    //   11796: iload 30
    //   11798: istore 14
    //   11800: iload 8
    //   11802: istore 11
    //   11804: aload_0
    //   11805: getfield 1783	org/telegram/ui/Cells/ChatMessageCell:drawPinnedTop	Z
    //   11808: ifeq +16 -> 11824
    //   11811: aload_0
    //   11812: aload_0
    //   11813: getfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   11816: fconst_1
    //   11817: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11820: isub
    //   11821: putfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   11824: aload_0
    //   11825: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   11828: iconst_0
    //   11829: ldc_w 616
    //   11832: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11835: aload_0
    //   11836: getfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   11839: iadd
    //   11840: iload 11
    //   11842: iadd
    //   11843: iload 14
    //   11845: iload 6
    //   11847: invokevirtual 1581	org/telegram/messenger/ImageReceiver:setImageCoords	(IIII)V
    //   11850: aload_0
    //   11851: invokevirtual 536	org/telegram/ui/Cells/ChatMessageCell:invalidate	()V
    //   11854: goto -5939 -> 5915
    //   11857: iconst_0
    //   11858: istore_3
    //   11859: goto -741 -> 11118
    //   11862: iconst_0
    //   11863: istore_3
    //   11864: goto -730 -> 11134
    //   11867: iconst_0
    //   11868: istore_3
    //   11869: goto -631 -> 11238
    //   11872: aload_0
    //   11873: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   11876: astore_2
    //   11877: aload_1
    //   11878: getfield 882	org/telegram/messenger/MessageObject:gifState	F
    //   11881: fconst_0
    //   11882: fcmpl
    //   11883: ifne +13 -> 11896
    //   11886: iconst_1
    //   11887: istore_3
    //   11888: aload_2
    //   11889: iload_3
    //   11890: invokevirtual 885	org/telegram/messenger/ImageReceiver:setAllowStartAnimation	(Z)V
    //   11893: goto -650 -> 11243
    //   11896: iconst_0
    //   11897: istore_3
    //   11898: goto -10 -> 11888
    //   11901: ldc_w 1074
    //   11904: fstore 40
    //   11906: goto -606 -> 11300
    //   11909: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   11912: getfield 2253	android/graphics/Point:x	I
    //   11915: istore 9
    //   11917: aload_0
    //   11918: getfield 2040	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   11921: ifeq +46 -> 11967
    //   11924: aload_1
    //   11925: invokevirtual 2043	org/telegram/messenger/MessageObject:needDrawAvatar	()Z
    //   11928: ifeq +39 -> 11967
    //   11931: aload_1
    //   11932: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   11935: ifne +32 -> 11967
    //   11938: ldc_w 3559
    //   11941: fstore 40
    //   11943: aload_0
    //   11944: iload 9
    //   11946: fload 40
    //   11948: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11951: isub
    //   11952: ldc_w 3560
    //   11955: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11958: invokestatic 1195	java/lang/Math:min	(II)I
    //   11961: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   11964: goto -643 -> 11321
    //   11967: ldc_w 1074
    //   11970: fstore 40
    //   11972: goto -29 -> 11943
    //   11975: ldc_w 1796
    //   11978: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11981: istore 6
    //   11983: ldc_w 1796
    //   11986: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   11989: istore 21
    //   11991: aload_1
    //   11992: getfield 3184	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   11995: invokestatic 847	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   11998: ifeq +21 -> 12019
    //   12001: ldc_w 1946
    //   12004: fstore 40
    //   12006: iload 9
    //   12008: fload 40
    //   12010: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   12013: iadd
    //   12014: istore 9
    //   12016: goto -603 -> 11413
    //   12019: ldc_w 367
    //   12022: fstore 40
    //   12024: goto -18 -> 12006
    //   12027: aload_1
    //   12028: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   12031: iconst_4
    //   12032: if_icmpne +1321 -> 13353
    //   12035: aload_1
    //   12036: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   12039: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   12042: getfield 2298	org/telegram/tgnet/TLRPC$MessageMedia:geo	Lorg/telegram/tgnet/TLRPC$GeoPoint;
    //   12045: getfield 2304	org/telegram/tgnet/TLRPC$GeoPoint:lat	D
    //   12048: dstore 41
    //   12050: aload_1
    //   12051: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   12054: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   12057: getfield 2298	org/telegram/tgnet/TLRPC$MessageMedia:geo	Lorg/telegram/tgnet/TLRPC$GeoPoint;
    //   12060: getfield 2307	org/telegram/tgnet/TLRPC$GeoPoint:_long	D
    //   12063: dstore 17
    //   12065: aload_1
    //   12066: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   12069: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   12072: instanceof 1952
    //   12075: ifeq +753 -> 12828
    //   12078: invokestatic 2226	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   12081: ifeq +598 -> 12679
    //   12084: invokestatic 2258	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   12087: istore 9
    //   12089: aload_0
    //   12090: getfield 2040	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   12093: ifeq +578 -> 12671
    //   12096: aload_1
    //   12097: invokevirtual 2043	org/telegram/messenger/MessageObject:needDrawAvatar	()Z
    //   12100: ifeq +571 -> 12671
    //   12103: aload_1
    //   12104: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   12107: ifne +564 -> 12671
    //   12110: ldc_w 3559
    //   12113: fstore 40
    //   12115: aload_0
    //   12116: iload 9
    //   12118: fload 40
    //   12120: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   12123: isub
    //   12124: ldc_w 3611
    //   12127: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   12130: invokestatic 1195	java/lang/Math:min	(II)I
    //   12133: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   12136: aload_0
    //   12137: aload_1
    //   12138: invokespecial 3293	org/telegram/ui/Cells/ChatMessageCell:checkNeedDrawShareButton	(Lorg/telegram/messenger/MessageObject;)Z
    //   12141: ifeq +18 -> 12159
    //   12144: aload_0
    //   12145: aload_0
    //   12146: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   12149: ldc_w 1077
    //   12152: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   12155: isub
    //   12156: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   12159: aload_0
    //   12160: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   12163: ldc_w 2025
    //   12166: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   12169: isub
    //   12170: istore 9
    //   12172: aload_0
    //   12173: iload 9
    //   12175: putfield 1187	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   12178: iload 9
    //   12180: ldc_w 3612
    //   12183: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   12186: isub
    //   12187: istore 9
    //   12189: aload_0
    //   12190: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   12193: ldc_w 367
    //   12196: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   12199: isub
    //   12200: istore 21
    //   12202: ldc_w 2308
    //   12205: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   12208: istore 6
    //   12210: ldc_w 2309
    //   12213: i2d
    //   12214: ldc2_w 2310
    //   12217: ddiv
    //   12218: dstore 15
    //   12220: ldc2_w 2312
    //   12223: ldc2_w 2314
    //   12226: ldc_w 2309
    //   12229: i2d
    //   12230: dconst_1
    //   12231: ldc2_w 2310
    //   12234: dload 41
    //   12236: dmul
    //   12237: ldc2_w 2316
    //   12240: ddiv
    //   12241: invokestatic 2320	java/lang/Math:sin	(D)D
    //   12244: dadd
    //   12245: dconst_1
    //   12246: ldc2_w 2310
    //   12249: dload 41
    //   12251: dmul
    //   12252: ldc2_w 2316
    //   12255: ddiv
    //   12256: invokestatic 2320	java/lang/Math:sin	(D)D
    //   12259: dsub
    //   12260: ddiv
    //   12261: invokestatic 2323	java/lang/Math:log	(D)D
    //   12264: dload 15
    //   12266: dmul
    //   12267: ldc2_w 2314
    //   12270: ddiv
    //   12271: dsub
    //   12272: invokestatic 2327	java/lang/Math:round	(D)J
    //   12275: ldc_w 2328
    //   12278: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   12281: bipush 6
    //   12283: ishl
    //   12284: i2l
    //   12285: lsub
    //   12286: l2d
    //   12287: ldc_w 2309
    //   12290: i2d
    //   12291: dsub
    //   12292: dload 15
    //   12294: ddiv
    //   12295: invokestatic 2331	java/lang/Math:exp	(D)D
    //   12298: invokestatic 2334	java/lang/Math:atan	(D)D
    //   12301: dmul
    //   12302: dsub
    //   12303: ldc2_w 2316
    //   12306: dmul
    //   12307: ldc2_w 2310
    //   12310: ddiv
    //   12311: dstore 15
    //   12313: aload_0
    //   12314: getstatic 2340	java/util/Locale:US	Ljava/util/Locale;
    //   12317: ldc_w 2342
    //   12320: iconst_5
    //   12321: anewarray 1242	java/lang/Object
    //   12324: dup
    //   12325: iconst_0
    //   12326: dload 15
    //   12328: invokestatic 2347	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   12331: aastore
    //   12332: dup
    //   12333: iconst_1
    //   12334: dload 17
    //   12336: invokestatic 2347	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   12339: aastore
    //   12340: dup
    //   12341: iconst_2
    //   12342: iload 21
    //   12344: i2f
    //   12345: getstatic 2350	org/telegram/messenger/AndroidUtilities:density	F
    //   12348: fdiv
    //   12349: f2i
    //   12350: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   12353: aastore
    //   12354: dup
    //   12355: iconst_3
    //   12356: iload 6
    //   12358: i2f
    //   12359: getstatic 2350	org/telegram/messenger/AndroidUtilities:density	F
    //   12362: fdiv
    //   12363: f2i
    //   12364: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   12367: aastore
    //   12368: dup
    //   12369: iconst_4
    //   12370: iconst_2
    //   12371: getstatic 2350	org/telegram/messenger/AndroidUtilities:density	F
    //   12374: f2d
    //   12375: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   12378: d2i
    //   12379: invokestatic 1195	java/lang/Math:min	(II)I
    //   12382: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   12385: aastore
    //   12386: invokestatic 2353	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   12389: putfield 2294	org/telegram/ui/Cells/ChatMessageCell:currentUrl	Ljava/lang/String;
    //   12392: aload_0
    //   12393: aload_1
    //   12394: invokespecial 918	org/telegram/ui/Cells/ChatMessageCell:isCurrentLocationTimeExpired	(Lorg/telegram/messenger/MessageObject;)Z
    //   12397: istore_3
    //   12398: aload_0
    //   12399: iload_3
    //   12400: putfield 446	org/telegram/ui/Cells/ChatMessageCell:locationExpired	Z
    //   12403: iload_3
    //   12404: ifne +341 -> 12745
    //   12407: aload_0
    //   12408: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   12411: iconst_1
    //   12412: invokevirtual 3291	org/telegram/messenger/ImageReceiver:setCrossfadeWithOldImage	(Z)V
    //   12415: aload_0
    //   12416: iconst_0
    //   12417: putfield 615	org/telegram/ui/Cells/ChatMessageCell:mediaBackground	Z
    //   12420: ldc_w 1796
    //   12423: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   12426: istore 14
    //   12428: aload_0
    //   12429: getfield 361	org/telegram/ui/Cells/ChatMessageCell:invalidateRunnable	Ljava/lang/Runnable;
    //   12432: ldc2_w 919
    //   12435: invokestatic 924	org/telegram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;J)V
    //   12438: aload_0
    //   12439: iconst_1
    //   12440: putfield 449	org/telegram/ui/Cells/ChatMessageCell:scheduledInvalidate	Z
    //   12443: aload_0
    //   12444: new 350	android/text/StaticLayout
    //   12447: dup
    //   12448: ldc_w 927
    //   12451: ldc_w 928
    //   12454: invokestatic 934	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   12457: getstatic 940	org/telegram/ui/ActionBar/Theme:chat_locationTitlePaint	Landroid/text/TextPaint;
    //   12460: iload 9
    //   12462: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   12465: fconst_1
    //   12466: fconst_0
    //   12467: iconst_0
    //   12468: invokespecial 949	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   12471: putfield 951	org/telegram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   12474: aconst_null
    //   12475: astore 20
    //   12477: aconst_null
    //   12478: astore 10
    //   12480: aconst_null
    //   12481: astore_2
    //   12482: aload_0
    //   12483: invokespecial 2393	org/telegram/ui/Cells/ChatMessageCell:updateCurrentUserAndChat	()V
    //   12486: aload_0
    //   12487: getfield 2376	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   12490: ifnull +277 -> 12767
    //   12493: aload_0
    //   12494: getfield 2376	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   12497: getfield 2397	org/telegram/tgnet/TLRPC$User:photo	Lorg/telegram/tgnet/TLRPC$UserProfilePhoto;
    //   12500: ifnull +14 -> 12514
    //   12503: aload_0
    //   12504: getfield 2376	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   12507: getfield 2397	org/telegram/tgnet/TLRPC$User:photo	Lorg/telegram/tgnet/TLRPC$UserProfilePhoto;
    //   12510: getfield 2402	org/telegram/tgnet/TLRPC$UserProfilePhoto:photo_small	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   12513: astore_2
    //   12514: aload_0
    //   12515: getfield 404	org/telegram/ui/Cells/ChatMessageCell:contactAvatarDrawable	Lorg/telegram/ui/Components/AvatarDrawable;
    //   12518: aload_0
    //   12519: getfield 2376	org/telegram/ui/Cells/ChatMessageCell:currentUser	Lorg/telegram/tgnet/TLRPC$User;
    //   12522: invokevirtual 2543	org/telegram/ui/Components/AvatarDrawable:setInfo	(Lorg/telegram/tgnet/TLRPC$User;)V
    //   12525: aload_0
    //   12526: getfield 389	org/telegram/ui/Cells/ChatMessageCell:locationImageReceiver	Lorg/telegram/messenger/ImageReceiver;
    //   12529: aload_2
    //   12530: ldc_w 2545
    //   12533: aload_0
    //   12534: getfield 404	org/telegram/ui/Cells/ChatMessageCell:contactAvatarDrawable	Lorg/telegram/ui/Components/AvatarDrawable;
    //   12537: aconst_null
    //   12538: iconst_0
    //   12539: invokevirtual 2548	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Ljava/lang/String;I)V
    //   12542: aload_1
    //   12543: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   12546: getfield 3615	org/telegram/tgnet/TLRPC$Message:edit_date	I
    //   12549: ifeq +266 -> 12815
    //   12552: aload_1
    //   12553: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   12556: getfield 3615	org/telegram/tgnet/TLRPC$Message:edit_date	I
    //   12559: i2l
    //   12560: lstore 43
    //   12562: aload_0
    //   12563: new 350	android/text/StaticLayout
    //   12566: dup
    //   12567: lload 43
    //   12569: invokestatic 3618	org/telegram/messenger/LocaleController:formatLocationUpdateDate	(J)Ljava/lang/String;
    //   12572: getstatic 1955	org/telegram/ui/ActionBar/Theme:chat_locationAddressPaint	Landroid/text/TextPaint;
    //   12575: iload 9
    //   12577: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   12580: fconst_1
    //   12581: fconst_0
    //   12582: iconst_0
    //   12583: invokespecial 949	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   12586: putfield 1270	org/telegram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   12589: iload 14
    //   12591: istore 30
    //   12593: iload 11
    //   12595: istore 22
    //   12597: iload 6
    //   12599: istore 9
    //   12601: iload 21
    //   12603: istore 8
    //   12605: aload_0
    //   12606: getfield 2294	org/telegram/ui/Cells/ChatMessageCell:currentUrl	Ljava/lang/String;
    //   12609: ifnull -1039 -> 11570
    //   12612: aload_0
    //   12613: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   12616: astore_2
    //   12617: aload_0
    //   12618: getfield 2294	org/telegram/ui/Cells/ChatMessageCell:currentUrl	Ljava/lang/String;
    //   12621: astore 10
    //   12623: getstatic 3621	org/telegram/ui/ActionBar/Theme:chat_locationDrawable	[Landroid/graphics/drawable/Drawable;
    //   12626: astore 20
    //   12628: aload_1
    //   12629: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   12632: ifeq +715 -> 13347
    //   12635: iconst_1
    //   12636: istore 9
    //   12638: aload_2
    //   12639: aload 10
    //   12641: aconst_null
    //   12642: aload 20
    //   12644: iload 9
    //   12646: aaload
    //   12647: aconst_null
    //   12648: iconst_0
    //   12649: invokevirtual 3624	org/telegram/messenger/ImageReceiver:setImage	(Ljava/lang/String;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Ljava/lang/String;I)V
    //   12652: iload 14
    //   12654: istore 30
    //   12656: iload 11
    //   12658: istore 22
    //   12660: iload 6
    //   12662: istore 9
    //   12664: iload 21
    //   12666: istore 8
    //   12668: goto -1098 -> 11570
    //   12671: ldc_w 1074
    //   12674: fstore 40
    //   12676: goto -561 -> 12115
    //   12679: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   12682: getfield 2253	android/graphics/Point:x	I
    //   12685: istore 9
    //   12687: aload_0
    //   12688: getfield 2040	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   12691: ifeq +46 -> 12737
    //   12694: aload_1
    //   12695: invokevirtual 2043	org/telegram/messenger/MessageObject:needDrawAvatar	()Z
    //   12698: ifeq +39 -> 12737
    //   12701: aload_1
    //   12702: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   12705: ifne +32 -> 12737
    //   12708: ldc_w 3559
    //   12711: fstore 40
    //   12713: aload_0
    //   12714: iload 9
    //   12716: fload 40
    //   12718: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   12721: isub
    //   12722: ldc_w 3611
    //   12725: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   12728: invokestatic 1195	java/lang/Math:min	(II)I
    //   12731: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   12734: goto -598 -> 12136
    //   12737: ldc_w 1074
    //   12740: fstore 40
    //   12742: goto -29 -> 12713
    //   12745: aload_0
    //   12746: aload_0
    //   12747: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   12750: ldc_w 1705
    //   12753: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   12756: isub
    //   12757: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   12760: iload 8
    //   12762: istore 14
    //   12764: goto -321 -> 12443
    //   12767: aload 20
    //   12769: astore_2
    //   12770: aload_0
    //   12771: getfield 2378	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   12774: ifnull -249 -> 12525
    //   12777: aload 10
    //   12779: astore_2
    //   12780: aload_0
    //   12781: getfield 2378	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   12784: getfield 2439	org/telegram/tgnet/TLRPC$Chat:photo	Lorg/telegram/tgnet/TLRPC$ChatPhoto;
    //   12787: ifnull +14 -> 12801
    //   12790: aload_0
    //   12791: getfield 2378	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   12794: getfield 2439	org/telegram/tgnet/TLRPC$Chat:photo	Lorg/telegram/tgnet/TLRPC$ChatPhoto;
    //   12797: getfield 2442	org/telegram/tgnet/TLRPC$ChatPhoto:photo_small	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   12800: astore_2
    //   12801: aload_0
    //   12802: getfield 404	org/telegram/ui/Cells/ChatMessageCell:contactAvatarDrawable	Lorg/telegram/ui/Components/AvatarDrawable;
    //   12805: aload_0
    //   12806: getfield 2378	org/telegram/ui/Cells/ChatMessageCell:currentChat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   12809: invokevirtual 2694	org/telegram/ui/Components/AvatarDrawable:setInfo	(Lorg/telegram/tgnet/TLRPC$Chat;)V
    //   12812: goto -287 -> 12525
    //   12815: aload_1
    //   12816: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   12819: getfield 1966	org/telegram/tgnet/TLRPC$Message:date	I
    //   12822: i2l
    //   12823: lstore 43
    //   12825: goto -263 -> 12562
    //   12828: aload_1
    //   12829: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   12832: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   12835: getfield 2358	org/telegram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   12838: invokestatic 847	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   12841: ifne +393 -> 13234
    //   12844: invokestatic 2226	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   12847: ifeq +313 -> 13160
    //   12850: invokestatic 2258	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   12853: istore 8
    //   12855: aload_0
    //   12856: getfield 2040	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   12859: ifeq +293 -> 13152
    //   12862: aload_1
    //   12863: invokevirtual 2043	org/telegram/messenger/MessageObject:needDrawAvatar	()Z
    //   12866: ifeq +286 -> 13152
    //   12869: aload_1
    //   12870: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   12873: ifne +279 -> 13152
    //   12876: ldc_w 3559
    //   12879: fstore 40
    //   12881: aload_0
    //   12882: iload 8
    //   12884: fload 40
    //   12886: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   12889: isub
    //   12890: ldc_w 3560
    //   12893: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   12896: invokestatic 1195	java/lang/Math:min	(II)I
    //   12899: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   12902: aload_0
    //   12903: aload_1
    //   12904: invokespecial 3293	org/telegram/ui/Cells/ChatMessageCell:checkNeedDrawShareButton	(Lorg/telegram/messenger/MessageObject;)Z
    //   12907: ifeq +18 -> 12925
    //   12910: aload_0
    //   12911: aload_0
    //   12912: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   12915: ldc_w 1077
    //   12918: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   12921: isub
    //   12922: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   12925: aload_0
    //   12926: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   12929: ldc_w 3625
    //   12932: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   12935: isub
    //   12936: istore 8
    //   12938: aload_0
    //   12939: aload_1
    //   12940: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   12943: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   12946: getfield 2358	org/telegram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   12949: getstatic 940	org/telegram/ui/ActionBar/Theme:chat_locationTitlePaint	Landroid/text/TextPaint;
    //   12952: iload 8
    //   12954: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   12957: fconst_1
    //   12958: fconst_0
    //   12959: iconst_0
    //   12960: getstatic 1217	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   12963: iload 8
    //   12965: iconst_2
    //   12966: invokestatic 1315	org/telegram/ui/Components/StaticLayoutEx:createStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZLandroid/text/TextUtils$TruncateAt;II)Landroid/text/StaticLayout;
    //   12969: putfield 951	org/telegram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   12972: aload_0
    //   12973: getfield 951	org/telegram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   12976: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   12979: istore 6
    //   12981: aload_1
    //   12982: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   12985: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   12988: getfield 3628	org/telegram/tgnet/TLRPC$MessageMedia:address	Ljava/lang/String;
    //   12991: ifnull +235 -> 13226
    //   12994: aload_1
    //   12995: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   12998: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   13001: getfield 3628	org/telegram/tgnet/TLRPC$MessageMedia:address	Ljava/lang/String;
    //   13004: invokevirtual 1054	java/lang/String:length	()I
    //   13007: ifle +219 -> 13226
    //   13010: aload_0
    //   13011: aload_1
    //   13012: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   13015: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   13018: getfield 3628	org/telegram/tgnet/TLRPC$MessageMedia:address	Ljava/lang/String;
    //   13021: getstatic 1955	org/telegram/ui/ActionBar/Theme:chat_locationAddressPaint	Landroid/text/TextPaint;
    //   13024: iload 8
    //   13026: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   13029: fconst_1
    //   13030: fconst_0
    //   13031: iconst_0
    //   13032: getstatic 1217	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   13035: iload 8
    //   13037: iconst_3
    //   13038: iconst_3
    //   13039: iload 6
    //   13041: isub
    //   13042: invokestatic 1195	java/lang/Math:min	(II)I
    //   13045: invokestatic 1315	org/telegram/ui/Components/StaticLayoutEx:createStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZLandroid/text/TextUtils$TruncateAt;II)Landroid/text/StaticLayout;
    //   13048: putfield 1270	org/telegram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   13051: aload_0
    //   13052: iconst_0
    //   13053: putfield 615	org/telegram/ui/Cells/ChatMessageCell:mediaBackground	Z
    //   13056: aload_0
    //   13057: iload 8
    //   13059: putfield 1187	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   13062: ldc_w 1201
    //   13065: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   13068: istore 21
    //   13070: ldc_w 1201
    //   13073: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   13076: istore 6
    //   13078: aload_0
    //   13079: getstatic 2340	java/util/Locale:US	Ljava/util/Locale;
    //   13082: ldc_w 2360
    //   13085: iconst_5
    //   13086: anewarray 1242	java/lang/Object
    //   13089: dup
    //   13090: iconst_0
    //   13091: dload 41
    //   13093: invokestatic 2347	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   13096: aastore
    //   13097: dup
    //   13098: iconst_1
    //   13099: dload 17
    //   13101: invokestatic 2347	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   13104: aastore
    //   13105: dup
    //   13106: iconst_2
    //   13107: iconst_2
    //   13108: getstatic 2350	org/telegram/messenger/AndroidUtilities:density	F
    //   13111: f2d
    //   13112: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   13115: d2i
    //   13116: invokestatic 1195	java/lang/Math:min	(II)I
    //   13119: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   13122: aastore
    //   13123: dup
    //   13124: iconst_3
    //   13125: dload 41
    //   13127: invokestatic 2347	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   13130: aastore
    //   13131: dup
    //   13132: iconst_4
    //   13133: dload 17
    //   13135: invokestatic 2347	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   13138: aastore
    //   13139: invokestatic 2353	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   13142: putfield 2294	org/telegram/ui/Cells/ChatMessageCell:currentUrl	Ljava/lang/String;
    //   13145: iload 9
    //   13147: istore 14
    //   13149: goto -560 -> 12589
    //   13152: ldc_w 1074
    //   13155: fstore 40
    //   13157: goto -276 -> 12881
    //   13160: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   13163: getfield 2253	android/graphics/Point:x	I
    //   13166: istore 8
    //   13168: aload_0
    //   13169: getfield 2040	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   13172: ifeq +46 -> 13218
    //   13175: aload_1
    //   13176: invokevirtual 2043	org/telegram/messenger/MessageObject:needDrawAvatar	()Z
    //   13179: ifeq +39 -> 13218
    //   13182: aload_1
    //   13183: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   13186: ifne +32 -> 13218
    //   13189: ldc_w 3559
    //   13192: fstore 40
    //   13194: aload_0
    //   13195: iload 8
    //   13197: fload 40
    //   13199: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   13202: isub
    //   13203: ldc_w 3560
    //   13206: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   13209: invokestatic 1195	java/lang/Math:min	(II)I
    //   13212: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   13215: goto -313 -> 12902
    //   13218: ldc_w 1074
    //   13221: fstore 40
    //   13223: goto -29 -> 13194
    //   13226: aload_0
    //   13227: aconst_null
    //   13228: putfield 1270	org/telegram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   13231: goto -180 -> 13051
    //   13234: aload_0
    //   13235: ldc_w 3629
    //   13238: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   13241: putfield 1187	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   13244: ldc_w 2160
    //   13247: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   13250: istore 21
    //   13252: ldc_w 2568
    //   13255: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   13258: istore 6
    //   13260: aload_0
    //   13261: ldc_w 554
    //   13264: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   13267: iload 21
    //   13269: iadd
    //   13270: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   13273: aload_0
    //   13274: getstatic 2340	java/util/Locale:US	Ljava/util/Locale;
    //   13277: ldc_w 2362
    //   13280: iconst_5
    //   13281: anewarray 1242	java/lang/Object
    //   13284: dup
    //   13285: iconst_0
    //   13286: dload 41
    //   13288: invokestatic 2347	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   13291: aastore
    //   13292: dup
    //   13293: iconst_1
    //   13294: dload 17
    //   13296: invokestatic 2347	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   13299: aastore
    //   13300: dup
    //   13301: iconst_2
    //   13302: iconst_2
    //   13303: getstatic 2350	org/telegram/messenger/AndroidUtilities:density	F
    //   13306: f2d
    //   13307: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   13310: d2i
    //   13311: invokestatic 1195	java/lang/Math:min	(II)I
    //   13314: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   13317: aastore
    //   13318: dup
    //   13319: iconst_3
    //   13320: dload 41
    //   13322: invokestatic 2347	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   13325: aastore
    //   13326: dup
    //   13327: iconst_4
    //   13328: dload 17
    //   13330: invokestatic 2347	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   13333: aastore
    //   13334: invokestatic 2353	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   13337: putfield 2294	org/telegram/ui/Cells/ChatMessageCell:currentUrl	Ljava/lang/String;
    //   13340: iload 9
    //   13342: istore 14
    //   13344: goto -755 -> 12589
    //   13347: iconst_0
    //   13348: istore 9
    //   13350: goto -712 -> 12638
    //   13353: aload_1
    //   13354: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   13357: bipush 13
    //   13359: if_icmpne +541 -> 13900
    //   13362: aload_0
    //   13363: iconst_0
    //   13364: putfield 346	org/telegram/ui/Cells/ChatMessageCell:drawBackground	Z
    //   13367: iconst_0
    //   13368: istore 6
    //   13370: iload 21
    //   13372: istore 8
    //   13374: iload 22
    //   13376: istore 9
    //   13378: iload 6
    //   13380: aload_1
    //   13381: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   13384: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   13387: getfield 1150	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   13390: getfield 1159	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   13393: invokevirtual 590	java/util/ArrayList:size	()I
    //   13396: if_icmpge +44 -> 13440
    //   13399: aload_1
    //   13400: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   13403: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   13406: getfield 1150	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   13409: getfield 1159	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   13412: iload 6
    //   13414: invokevirtual 594	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   13417: checkcast 1161	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   13420: astore_2
    //   13421: aload_2
    //   13422: instanceof 3503
    //   13425: ifeq +281 -> 13706
    //   13428: aload_2
    //   13429: getfield 3504	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   13432: istore 9
    //   13434: aload_2
    //   13435: getfield 3505	org/telegram/tgnet/TLRPC$DocumentAttribute:h	I
    //   13438: istore 8
    //   13440: invokestatic 2226	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   13443: ifeq +269 -> 13712
    //   13446: invokestatic 2258	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   13449: i2f
    //   13450: ldc_w 3630
    //   13453: fmul
    //   13454: fstore 39
    //   13456: fload 39
    //   13458: fstore 40
    //   13460: iload 8
    //   13462: istore 6
    //   13464: iload 9
    //   13466: istore 8
    //   13468: iload 9
    //   13470: ifne +19 -> 13489
    //   13473: fload 40
    //   13475: f2i
    //   13476: istore 6
    //   13478: iload 6
    //   13480: ldc_w 2568
    //   13483: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   13486: iadd
    //   13487: istore 8
    //   13489: iload 6
    //   13491: i2f
    //   13492: fload 39
    //   13494: iload 8
    //   13496: i2f
    //   13497: fdiv
    //   13498: fmul
    //   13499: f2i
    //   13500: istore 8
    //   13502: fload 39
    //   13504: f2i
    //   13505: istore 9
    //   13507: iload 8
    //   13509: istore 21
    //   13511: iload 9
    //   13513: istore 6
    //   13515: iload 8
    //   13517: i2f
    //   13518: fload 40
    //   13520: fcmpl
    //   13521: ifle +21 -> 13542
    //   13524: iload 9
    //   13526: i2f
    //   13527: fload 40
    //   13529: iload 8
    //   13531: i2f
    //   13532: fdiv
    //   13533: fmul
    //   13534: f2i
    //   13535: istore 6
    //   13537: fload 40
    //   13539: f2i
    //   13540: istore 21
    //   13542: aload_0
    //   13543: bipush 6
    //   13545: putfield 498	org/telegram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   13548: aload_0
    //   13549: iload 6
    //   13551: ldc_w 478
    //   13554: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   13557: isub
    //   13558: putfield 1187	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   13561: aload_0
    //   13562: ldc_w 554
    //   13565: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   13568: iload 6
    //   13570: iadd
    //   13571: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   13574: aload_0
    //   13575: aload_1
    //   13576: getfield 1336	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   13579: bipush 80
    //   13581: invokestatic 1343	org/telegram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;I)Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   13584: putfield 1404	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   13587: aload_1
    //   13588: getfield 2750	org/telegram/messenger/MessageObject:attachPathExists	Z
    //   13591: ifeq +155 -> 13746
    //   13594: aload_0
    //   13595: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   13598: astore 20
    //   13600: aload_1
    //   13601: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   13604: getfield 3633	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   13607: astore 23
    //   13609: getstatic 2340	java/util/Locale:US	Ljava/util/Locale;
    //   13612: ldc_w 3433
    //   13615: iconst_2
    //   13616: anewarray 1242	java/lang/Object
    //   13619: dup
    //   13620: iconst_0
    //   13621: iload 6
    //   13623: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   13626: aastore
    //   13627: dup
    //   13628: iconst_1
    //   13629: iload 21
    //   13631: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   13634: aastore
    //   13635: invokestatic 2353	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   13638: astore 10
    //   13640: aload_0
    //   13641: getfield 1404	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   13644: ifnull +97 -> 13741
    //   13647: aload_0
    //   13648: getfield 1404	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   13651: getfield 1292	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   13654: astore_2
    //   13655: aload 20
    //   13657: aconst_null
    //   13658: aload 23
    //   13660: aload 10
    //   13662: aconst_null
    //   13663: aload_2
    //   13664: ldc_w 3437
    //   13667: aload_1
    //   13668: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   13671: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   13674: getfield 1150	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   13677: getfield 1259	org/telegram/tgnet/TLRPC$Document:size	I
    //   13680: ldc_w 3521
    //   13683: iconst_1
    //   13684: invokevirtual 1362	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;I)V
    //   13687: iload 14
    //   13689: istore 30
    //   13691: iload 11
    //   13693: istore 22
    //   13695: iload 21
    //   13697: istore 9
    //   13699: iload 6
    //   13701: istore 8
    //   13703: goto -2133 -> 11570
    //   13706: iinc 6 1
    //   13709: goto -339 -> 13370
    //   13712: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   13715: getfield 2253	android/graphics/Point:x	I
    //   13718: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   13721: getfield 2262	android/graphics/Point:y	I
    //   13724: invokestatic 1195	java/lang/Math:min	(II)I
    //   13727: i2f
    //   13728: ldc_w 2766
    //   13731: fmul
    //   13732: fstore 39
    //   13734: fload 39
    //   13736: fstore 40
    //   13738: goto -278 -> 13460
    //   13741: aconst_null
    //   13742: astore_2
    //   13743: goto -88 -> 13655
    //   13746: iload 14
    //   13748: istore 30
    //   13750: iload 11
    //   13752: istore 22
    //   13754: iload 21
    //   13756: istore 9
    //   13758: iload 6
    //   13760: istore 8
    //   13762: aload_1
    //   13763: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   13766: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   13769: getfield 1150	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   13772: getfield 3635	org/telegram/tgnet/TLRPC$Document:id	J
    //   13775: lconst_0
    //   13776: lcmp
    //   13777: ifeq -2207 -> 11570
    //   13780: aload_0
    //   13781: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   13784: astore 10
    //   13786: aload_1
    //   13787: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   13790: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   13793: getfield 1150	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   13796: astore 20
    //   13798: getstatic 2340	java/util/Locale:US	Ljava/util/Locale;
    //   13801: ldc_w 3433
    //   13804: iconst_2
    //   13805: anewarray 1242	java/lang/Object
    //   13808: dup
    //   13809: iconst_0
    //   13810: iload 6
    //   13812: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   13815: aastore
    //   13816: dup
    //   13817: iconst_1
    //   13818: iload 21
    //   13820: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   13823: aastore
    //   13824: invokestatic 2353	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   13827: astore 23
    //   13829: aload_0
    //   13830: getfield 1404	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   13833: ifnull +62 -> 13895
    //   13836: aload_0
    //   13837: getfield 1404	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   13840: getfield 1292	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   13843: astore_2
    //   13844: aload 10
    //   13846: aload 20
    //   13848: aconst_null
    //   13849: aload 23
    //   13851: aconst_null
    //   13852: aload_2
    //   13853: ldc_w 3437
    //   13856: aload_1
    //   13857: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   13860: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   13863: getfield 1150	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   13866: getfield 1259	org/telegram/tgnet/TLRPC$Document:size	I
    //   13869: ldc_w 3521
    //   13872: iconst_1
    //   13873: invokevirtual 1362	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;I)V
    //   13876: iload 14
    //   13878: istore 30
    //   13880: iload 11
    //   13882: istore 22
    //   13884: iload 21
    //   13886: istore 9
    //   13888: iload 6
    //   13890: istore 8
    //   13892: goto -2322 -> 11570
    //   13895: aconst_null
    //   13896: astore_2
    //   13897: goto -53 -> 13844
    //   13900: aload_1
    //   13901: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   13904: iconst_5
    //   13905: if_icmpne +672 -> 14577
    //   13908: getstatic 1846	org/telegram/messenger/AndroidUtilities:roundMessageSize	I
    //   13911: istore 8
    //   13913: iload 8
    //   13915: istore 9
    //   13917: iload 8
    //   13919: ldc_w 2568
    //   13922: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   13925: iadd
    //   13926: istore 21
    //   13928: iload 9
    //   13930: istore 11
    //   13932: iload 8
    //   13934: istore 6
    //   13936: aload_1
    //   13937: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   13940: iconst_5
    //   13941: if_icmpeq +41 -> 13982
    //   13944: iload 9
    //   13946: istore 11
    //   13948: iload 8
    //   13950: istore 6
    //   13952: aload_0
    //   13953: aload_1
    //   13954: invokespecial 3293	org/telegram/ui/Cells/ChatMessageCell:checkNeedDrawShareButton	(Lorg/telegram/messenger/MessageObject;)Z
    //   13957: ifeq +25 -> 13982
    //   13960: iload 9
    //   13962: ldc_w 1077
    //   13965: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   13968: isub
    //   13969: istore 11
    //   13971: iload 8
    //   13973: ldc_w 1077
    //   13976: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   13979: isub
    //   13980: istore 6
    //   13982: iload 6
    //   13984: istore 22
    //   13986: iload 6
    //   13988: invokestatic 1339	org/telegram/messenger/AndroidUtilities:getPhotoSize	()I
    //   13991: if_icmple +8 -> 13999
    //   13994: invokestatic 1339	org/telegram/messenger/AndroidUtilities:getPhotoSize	()I
    //   13997: istore 22
    //   13999: iload 21
    //   14001: istore 6
    //   14003: iload 21
    //   14005: invokestatic 1339	org/telegram/messenger/AndroidUtilities:getPhotoSize	()I
    //   14008: if_icmple +8 -> 14016
    //   14011: invokestatic 1339	org/telegram/messenger/AndroidUtilities:getPhotoSize	()I
    //   14014: istore 6
    //   14016: aload_1
    //   14017: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   14020: iconst_1
    //   14021: if_icmpne +610 -> 14631
    //   14024: aload_0
    //   14025: aload_1
    //   14026: invokespecial 1763	org/telegram/ui/Cells/ChatMessageCell:updateSecretTimeText	(Lorg/telegram/messenger/MessageObject;)V
    //   14029: aload_0
    //   14030: aload_1
    //   14031: getfield 1336	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   14034: bipush 80
    //   14036: invokestatic 1343	org/telegram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;I)Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   14039: putfield 1404	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   14042: aload_0
    //   14043: getfield 705	org/telegram/ui/Cells/ChatMessageCell:currentMessagesGroup	Lorg/telegram/messenger/MessageObject$GroupedMessages;
    //   14046: ifnonnull +15 -> 14061
    //   14049: aload_1
    //   14050: getfield 3184	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   14053: ifnull +8 -> 14061
    //   14056: aload_0
    //   14057: iconst_0
    //   14058: putfield 615	org/telegram/ui/Cells/ChatMessageCell:mediaBackground	Z
    //   14061: aload_0
    //   14062: aload_1
    //   14063: getfield 1336	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   14066: invokestatic 1339	org/telegram/messenger/AndroidUtilities:getPhotoSize	()I
    //   14069: invokestatic 1343	org/telegram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;I)Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   14072: putfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   14075: iconst_0
    //   14076: istore 8
    //   14078: iconst_0
    //   14079: istore 9
    //   14081: aload_0
    //   14082: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   14085: ifnull +19 -> 14104
    //   14088: aload_0
    //   14089: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   14092: aload_0
    //   14093: getfield 1404	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   14096: if_acmpne +8 -> 14104
    //   14099: aload_0
    //   14100: aconst_null
    //   14101: putfield 1404	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   14104: aload_0
    //   14105: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   14108: ifnull +112 -> 14220
    //   14111: aload_0
    //   14112: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   14115: getfield 3498	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   14118: i2f
    //   14119: iload 22
    //   14121: i2f
    //   14122: fdiv
    //   14123: fstore 40
    //   14125: aload_0
    //   14126: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   14129: getfield 3498	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   14132: i2f
    //   14133: fload 40
    //   14135: fdiv
    //   14136: f2i
    //   14137: istore 8
    //   14139: aload_0
    //   14140: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   14143: getfield 3501	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   14146: i2f
    //   14147: fload 40
    //   14149: fdiv
    //   14150: f2i
    //   14151: istore 9
    //   14153: iload 8
    //   14155: istore 21
    //   14157: iload 8
    //   14159: ifne +11 -> 14170
    //   14162: ldc_w 3506
    //   14165: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   14168: istore 21
    //   14170: iload 9
    //   14172: istore 14
    //   14174: iload 9
    //   14176: ifne +11 -> 14187
    //   14179: ldc_w 3506
    //   14182: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   14185: istore 14
    //   14187: iload 14
    //   14189: iload 6
    //   14191: if_icmple +638 -> 14829
    //   14194: iload 14
    //   14196: i2f
    //   14197: fstore 40
    //   14199: iload 6
    //   14201: istore 9
    //   14203: fload 40
    //   14205: iload 9
    //   14207: i2f
    //   14208: fdiv
    //   14209: fstore 40
    //   14211: iload 21
    //   14213: i2f
    //   14214: fload 40
    //   14216: fdiv
    //   14217: f2i
    //   14218: istore 8
    //   14220: iload 9
    //   14222: istore 14
    //   14224: iload 8
    //   14226: istore 21
    //   14228: aload_1
    //   14229: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   14232: iconst_5
    //   14233: if_icmpne +12 -> 14245
    //   14236: getstatic 1846	org/telegram/messenger/AndroidUtilities:roundMessageSize	I
    //   14239: istore 14
    //   14241: iload 14
    //   14243: istore 21
    //   14245: iload 21
    //   14247: ifeq +16 -> 14263
    //   14250: iload 14
    //   14252: istore 9
    //   14254: iload 21
    //   14256: istore 8
    //   14258: iload 14
    //   14260: ifne +154 -> 14414
    //   14263: iload 14
    //   14265: istore 9
    //   14267: iload 21
    //   14269: istore 8
    //   14271: aload_1
    //   14272: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   14275: bipush 8
    //   14277: if_icmpne +137 -> 14414
    //   14280: iconst_0
    //   14281: istore 30
    //   14283: iload 14
    //   14285: istore 9
    //   14287: iload 21
    //   14289: istore 8
    //   14291: iload 30
    //   14293: aload_1
    //   14294: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   14297: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   14300: getfield 1150	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   14303: getfield 1159	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   14306: invokevirtual 590	java/util/ArrayList:size	()I
    //   14309: if_icmpge +105 -> 14414
    //   14312: aload_1
    //   14313: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   14316: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   14319: getfield 1150	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   14322: getfield 1159	org/telegram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   14325: iload 30
    //   14327: invokevirtual 594	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   14330: checkcast 1161	org/telegram/tgnet/TLRPC$DocumentAttribute
    //   14333: astore_2
    //   14334: aload_2
    //   14335: instanceof 3503
    //   14338: ifne +10 -> 14348
    //   14341: aload_2
    //   14342: instanceof 1255
    //   14345: ifeq +651 -> 14996
    //   14348: aload_2
    //   14349: getfield 3504	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   14352: i2f
    //   14353: iload 22
    //   14355: i2f
    //   14356: fdiv
    //   14357: fstore 40
    //   14359: aload_2
    //   14360: getfield 3504	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   14363: i2f
    //   14364: fload 40
    //   14366: fdiv
    //   14367: f2i
    //   14368: istore 21
    //   14370: aload_2
    //   14371: getfield 3505	org/telegram/tgnet/TLRPC$DocumentAttribute:h	I
    //   14374: i2f
    //   14375: fload 40
    //   14377: fdiv
    //   14378: f2i
    //   14379: istore 14
    //   14381: iload 14
    //   14383: iload 6
    //   14385: if_icmple +532 -> 14917
    //   14388: iload 14
    //   14390: i2f
    //   14391: fstore 40
    //   14393: iload 6
    //   14395: istore 9
    //   14397: fload 40
    //   14399: iload 9
    //   14401: i2f
    //   14402: fdiv
    //   14403: fstore 40
    //   14405: iload 21
    //   14407: i2f
    //   14408: fload 40
    //   14410: fdiv
    //   14411: f2i
    //   14412: istore 8
    //   14414: iload 8
    //   14416: ifeq +12 -> 14428
    //   14419: iload 9
    //   14421: istore 6
    //   14423: iload 9
    //   14425: ifne +15 -> 14440
    //   14428: ldc_w 3506
    //   14431: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   14434: istore 6
    //   14436: iload 6
    //   14438: istore 8
    //   14440: iload 8
    //   14442: istore 9
    //   14444: aload_1
    //   14445: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   14448: iconst_3
    //   14449: if_icmpne +36 -> 14485
    //   14452: iload 8
    //   14454: istore 9
    //   14456: iload 8
    //   14458: aload_0
    //   14459: getfield 1268	org/telegram/ui/Cells/ChatMessageCell:infoWidth	I
    //   14462: ldc_w 2101
    //   14465: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   14468: iadd
    //   14469: if_icmpge +16 -> 14485
    //   14472: aload_0
    //   14473: getfield 1268	org/telegram/ui/Cells/ChatMessageCell:infoWidth	I
    //   14476: ldc_w 2101
    //   14479: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   14482: iadd
    //   14483: istore 9
    //   14485: aload_0
    //   14486: getfield 705	org/telegram/ui/Cells/ChatMessageCell:currentMessagesGroup	Lorg/telegram/messenger/MessageObject$GroupedMessages;
    //   14489: ifnull +897 -> 15386
    //   14492: iconst_0
    //   14493: istore 8
    //   14495: aload_0
    //   14496: invokespecial 3082	org/telegram/ui/Cells/ChatMessageCell:getGroupPhotosWidth	()I
    //   14499: istore 14
    //   14501: iconst_0
    //   14502: istore 21
    //   14504: iload 21
    //   14506: aload_0
    //   14507: getfield 705	org/telegram/ui/Cells/ChatMessageCell:currentMessagesGroup	Lorg/telegram/messenger/MessageObject$GroupedMessages;
    //   14510: getfield 2267	org/telegram/messenger/MessageObject$GroupedMessages:posArray	Ljava/util/ArrayList;
    //   14513: invokevirtual 590	java/util/ArrayList:size	()I
    //   14516: if_icmpge +486 -> 15002
    //   14519: aload_0
    //   14520: getfield 705	org/telegram/ui/Cells/ChatMessageCell:currentMessagesGroup	Lorg/telegram/messenger/MessageObject$GroupedMessages;
    //   14523: getfield 2267	org/telegram/messenger/MessageObject$GroupedMessages:posArray	Ljava/util/ArrayList;
    //   14526: iload 21
    //   14528: invokevirtual 594	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   14531: checkcast 964	org/telegram/messenger/MessageObject$GroupedMessagePosition
    //   14534: astore_2
    //   14535: aload_2
    //   14536: getfield 2270	org/telegram/messenger/MessageObject$GroupedMessagePosition:minY	B
    //   14539: ifne +463 -> 15002
    //   14542: iload 8
    //   14544: i2d
    //   14545: aload_2
    //   14546: getfield 2273	org/telegram/messenger/MessageObject$GroupedMessagePosition:pw	I
    //   14549: aload_2
    //   14550: getfield 2276	org/telegram/messenger/MessageObject$GroupedMessagePosition:leftSpanOffset	I
    //   14553: iadd
    //   14554: i2f
    //   14555: ldc_w 1740
    //   14558: fdiv
    //   14559: iload 14
    //   14561: i2f
    //   14562: fmul
    //   14563: f2d
    //   14564: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   14567: dadd
    //   14568: d2i
    //   14569: istore 8
    //   14571: iinc 21 1
    //   14574: goto -70 -> 14504
    //   14577: invokestatic 2226	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   14580: ifeq +21 -> 14601
    //   14583: invokestatic 2258	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   14586: i2f
    //   14587: ldc_w 3636
    //   14590: fmul
    //   14591: f2i
    //   14592: istore 8
    //   14594: iload 8
    //   14596: istore 9
    //   14598: goto -681 -> 13917
    //   14601: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   14604: getfield 2253	android/graphics/Point:x	I
    //   14607: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   14610: getfield 2262	android/graphics/Point:y	I
    //   14613: invokestatic 1195	java/lang/Math:min	(II)I
    //   14616: i2f
    //   14617: ldc_w 3636
    //   14620: fmul
    //   14621: f2i
    //   14622: istore 8
    //   14624: iload 8
    //   14626: istore 9
    //   14628: goto -711 -> 13917
    //   14631: aload_1
    //   14632: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   14635: iconst_3
    //   14636: if_icmpne +49 -> 14685
    //   14639: aload_0
    //   14640: iconst_0
    //   14641: aload_1
    //   14642: invokespecial 3508	org/telegram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/telegram/messenger/MessageObject;)I
    //   14645: pop
    //   14646: aload_0
    //   14647: aload_1
    //   14648: invokespecial 1763	org/telegram/ui/Cells/ChatMessageCell:updateSecretTimeText	(Lorg/telegram/messenger/MessageObject;)V
    //   14651: aload_1
    //   14652: invokevirtual 1253	org/telegram/messenger/MessageObject:needDrawBluredPreview	()Z
    //   14655: ifne +19 -> 14674
    //   14658: aload_0
    //   14659: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   14662: iconst_1
    //   14663: invokevirtual 1348	org/telegram/messenger/ImageReceiver:setNeedsQualityThumb	(Z)V
    //   14666: aload_0
    //   14667: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   14670: iconst_1
    //   14671: invokevirtual 1351	org/telegram/messenger/ImageReceiver:setShouldGenerateQualityThumb	(Z)V
    //   14674: aload_0
    //   14675: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   14678: aload_1
    //   14679: invokevirtual 1354	org/telegram/messenger/ImageReceiver:setParentMessageObject	(Lorg/telegram/messenger/MessageObject;)V
    //   14682: goto -640 -> 14042
    //   14685: aload_1
    //   14686: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   14689: iconst_5
    //   14690: if_icmpne +37 -> 14727
    //   14693: aload_1
    //   14694: invokevirtual 1253	org/telegram/messenger/MessageObject:needDrawBluredPreview	()Z
    //   14697: ifne +19 -> 14716
    //   14700: aload_0
    //   14701: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   14704: iconst_1
    //   14705: invokevirtual 1348	org/telegram/messenger/ImageReceiver:setNeedsQualityThumb	(Z)V
    //   14708: aload_0
    //   14709: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   14712: iconst_1
    //   14713: invokevirtual 1351	org/telegram/messenger/ImageReceiver:setShouldGenerateQualityThumb	(Z)V
    //   14716: aload_0
    //   14717: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   14720: aload_1
    //   14721: invokevirtual 1354	org/telegram/messenger/ImageReceiver:setParentMessageObject	(Lorg/telegram/messenger/MessageObject;)V
    //   14724: goto -682 -> 14042
    //   14727: aload_1
    //   14728: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   14731: bipush 8
    //   14733: if_icmpne -691 -> 14042
    //   14736: aload_1
    //   14737: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   14740: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   14743: getfield 1150	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   14746: getfield 1259	org/telegram/tgnet/TLRPC$Document:size	I
    //   14749: i2l
    //   14750: invokestatic 1263	org/telegram/messenger/AndroidUtilities:formatFileSize	(J)Ljava/lang/String;
    //   14753: astore_2
    //   14754: aload_0
    //   14755: getstatic 1266	org/telegram/ui/ActionBar/Theme:chat_infoPaint	Landroid/text/TextPaint;
    //   14758: aload_2
    //   14759: invokevirtual 1178	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   14762: f2d
    //   14763: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   14766: d2i
    //   14767: putfield 1268	org/telegram/ui/Cells/ChatMessageCell:infoWidth	I
    //   14770: aload_0
    //   14771: new 350	android/text/StaticLayout
    //   14774: dup
    //   14775: aload_2
    //   14776: getstatic 1266	org/telegram/ui/ActionBar/Theme:chat_infoPaint	Landroid/text/TextPaint;
    //   14779: aload_0
    //   14780: getfield 1268	org/telegram/ui/Cells/ChatMessageCell:infoWidth	I
    //   14783: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   14786: fconst_1
    //   14787: fconst_0
    //   14788: iconst_0
    //   14789: invokespecial 949	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   14792: putfield 1270	org/telegram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   14795: aload_1
    //   14796: invokevirtual 1253	org/telegram/messenger/MessageObject:needDrawBluredPreview	()Z
    //   14799: ifne +19 -> 14818
    //   14802: aload_0
    //   14803: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   14806: iconst_1
    //   14807: invokevirtual 1348	org/telegram/messenger/ImageReceiver:setNeedsQualityThumb	(Z)V
    //   14810: aload_0
    //   14811: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   14814: iconst_1
    //   14815: invokevirtual 1351	org/telegram/messenger/ImageReceiver:setShouldGenerateQualityThumb	(Z)V
    //   14818: aload_0
    //   14819: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   14822: aload_1
    //   14823: invokevirtual 1354	org/telegram/messenger/ImageReceiver:setParentMessageObject	(Lorg/telegram/messenger/MessageObject;)V
    //   14826: goto -784 -> 14042
    //   14829: iload 14
    //   14831: istore 9
    //   14833: iload 21
    //   14835: istore 8
    //   14837: iload 14
    //   14839: ldc_w 3637
    //   14842: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   14845: if_icmpge -625 -> 14220
    //   14848: ldc_w 3637
    //   14851: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   14854: istore 14
    //   14856: aload_0
    //   14857: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   14860: getfield 3501	org/telegram/tgnet/TLRPC$PhotoSize:h	I
    //   14863: i2f
    //   14864: iload 14
    //   14866: i2f
    //   14867: fdiv
    //   14868: fstore 40
    //   14870: iload 14
    //   14872: istore 9
    //   14874: iload 21
    //   14876: istore 8
    //   14878: aload_0
    //   14879: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   14882: getfield 3498	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   14885: i2f
    //   14886: fload 40
    //   14888: fdiv
    //   14889: iload 22
    //   14891: i2f
    //   14892: fcmpg
    //   14893: ifge -673 -> 14220
    //   14896: aload_0
    //   14897: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   14900: getfield 3498	org/telegram/tgnet/TLRPC$PhotoSize:w	I
    //   14903: i2f
    //   14904: fload 40
    //   14906: fdiv
    //   14907: f2i
    //   14908: istore 8
    //   14910: iload 14
    //   14912: istore 9
    //   14914: goto -694 -> 14220
    //   14917: iload 14
    //   14919: istore 9
    //   14921: iload 21
    //   14923: istore 8
    //   14925: iload 14
    //   14927: ldc_w 3637
    //   14930: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   14933: if_icmpge -519 -> 14414
    //   14936: ldc_w 3637
    //   14939: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   14942: istore 6
    //   14944: aload_2
    //   14945: getfield 3505	org/telegram/tgnet/TLRPC$DocumentAttribute:h	I
    //   14948: i2f
    //   14949: iload 6
    //   14951: i2f
    //   14952: fdiv
    //   14953: fstore 40
    //   14955: iload 6
    //   14957: istore 9
    //   14959: iload 21
    //   14961: istore 8
    //   14963: aload_2
    //   14964: getfield 3504	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   14967: i2f
    //   14968: fload 40
    //   14970: fdiv
    //   14971: iload 22
    //   14973: i2f
    //   14974: fcmpg
    //   14975: ifge -561 -> 14414
    //   14978: aload_2
    //   14979: getfield 3504	org/telegram/tgnet/TLRPC$DocumentAttribute:w	I
    //   14982: i2f
    //   14983: fload 40
    //   14985: fdiv
    //   14986: f2i
    //   14987: istore 8
    //   14989: iload 6
    //   14991: istore 9
    //   14993: goto -579 -> 14414
    //   14996: iinc 30 1
    //   14999: goto -716 -> 14283
    //   15002: aload_0
    //   15003: iload 8
    //   15005: ldc_w 1931
    //   15008: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   15011: isub
    //   15012: putfield 1187	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   15015: aload_1
    //   15016: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   15019: iconst_5
    //   15020: if_icmpne +35 -> 15055
    //   15023: aload_0
    //   15024: aload_0
    //   15025: getfield 1187	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   15028: i2d
    //   15029: getstatic 1170	org/telegram/ui/ActionBar/Theme:chat_audioTimePaint	Landroid/text/TextPaint;
    //   15032: ldc_w 1172
    //   15035: invokevirtual 1178	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   15038: f2d
    //   15039: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   15042: ldc_w 3638
    //   15045: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   15048: i2d
    //   15049: dadd
    //   15050: dsub
    //   15051: d2i
    //   15052: putfield 1187	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   15055: aload_0
    //   15056: aload_1
    //   15057: invokespecial 1191	org/telegram/ui/Cells/ChatMessageCell:measureTime	(Lorg/telegram/messenger/MessageObject;)V
    //   15060: aload_0
    //   15061: getfield 493	org/telegram/ui/Cells/ChatMessageCell:timeWidth	I
    //   15064: istore 21
    //   15066: aload_1
    //   15067: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   15070: ifeq +332 -> 15402
    //   15073: bipush 20
    //   15075: istore 8
    //   15077: iload 21
    //   15079: iload 8
    //   15081: bipush 14
    //   15083: iadd
    //   15084: i2f
    //   15085: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   15088: iadd
    //   15089: istore 36
    //   15091: iload 9
    //   15093: istore 21
    //   15095: iload 9
    //   15097: iload 36
    //   15099: if_icmpge +7 -> 15106
    //   15102: iload 36
    //   15104: istore 21
    //   15106: aload_1
    //   15107: invokevirtual 1416	org/telegram/messenger/MessageObject:isRoundVideo	()Z
    //   15110: ifeq +298 -> 15408
    //   15113: iload 21
    //   15115: iload 6
    //   15117: invokestatic 1195	java/lang/Math:min	(II)I
    //   15120: istore 8
    //   15122: iload 8
    //   15124: istore 9
    //   15126: aload_0
    //   15127: iconst_0
    //   15128: putfield 346	org/telegram/ui/Cells/ChatMessageCell:drawBackground	Z
    //   15131: aload_0
    //   15132: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   15135: iload 9
    //   15137: iconst_2
    //   15138: idiv
    //   15139: invokevirtual 377	org/telegram/messenger/ImageReceiver:setRoundRadius	(I)V
    //   15142: aload_0
    //   15143: getfield 705	org/telegram/ui/Cells/ChatMessageCell:currentMessagesGroup	Lorg/telegram/messenger/MessageObject$GroupedMessages;
    //   15146: ifnull +1987 -> 17133
    //   15149: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   15152: getfield 2253	android/graphics/Point:x	I
    //   15155: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   15158: getfield 2262	android/graphics/Point:y	I
    //   15161: invokestatic 486	java/lang/Math:max	(II)I
    //   15164: i2f
    //   15165: ldc_w 2766
    //   15168: fmul
    //   15169: fstore 40
    //   15171: aload_0
    //   15172: invokespecial 3082	org/telegram/ui/Cells/ChatMessageCell:getGroupPhotosWidth	()I
    //   15175: istore 34
    //   15177: aload_0
    //   15178: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   15181: getfield 2273	org/telegram/messenger/MessageObject$GroupedMessagePosition:pw	I
    //   15184: i2f
    //   15185: ldc_w 1740
    //   15188: fdiv
    //   15189: iload 34
    //   15191: i2f
    //   15192: fmul
    //   15193: f2d
    //   15194: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   15197: d2i
    //   15198: istore 11
    //   15200: iload 11
    //   15202: istore 9
    //   15204: aload_0
    //   15205: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   15208: getfield 2270	org/telegram/messenger/MessageObject$GroupedMessagePosition:minY	B
    //   15211: ifeq +392 -> 15603
    //   15214: aload_1
    //   15215: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   15218: ifeq +15 -> 15233
    //   15221: aload_0
    //   15222: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   15225: getfield 2210	org/telegram/messenger/MessageObject$GroupedMessagePosition:flags	I
    //   15228: iconst_1
    //   15229: iand
    //   15230: ifne +30 -> 15260
    //   15233: iload 11
    //   15235: istore 9
    //   15237: aload_1
    //   15238: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   15241: ifne +362 -> 15603
    //   15244: iload 11
    //   15246: istore 9
    //   15248: aload_0
    //   15249: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   15252: getfield 2210	org/telegram/messenger/MessageObject$GroupedMessagePosition:flags	I
    //   15255: iconst_2
    //   15256: iand
    //   15257: ifeq +346 -> 15603
    //   15260: iconst_0
    //   15261: istore 9
    //   15263: iconst_0
    //   15264: istore 8
    //   15266: iconst_0
    //   15267: istore 6
    //   15269: iload 6
    //   15271: aload_0
    //   15272: getfield 705	org/telegram/ui/Cells/ChatMessageCell:currentMessagesGroup	Lorg/telegram/messenger/MessageObject$GroupedMessages;
    //   15275: getfield 2267	org/telegram/messenger/MessageObject$GroupedMessages:posArray	Ljava/util/ArrayList;
    //   15278: invokevirtual 590	java/util/ArrayList:size	()I
    //   15281: if_icmpge +312 -> 15593
    //   15284: aload_0
    //   15285: getfield 705	org/telegram/ui/Cells/ChatMessageCell:currentMessagesGroup	Lorg/telegram/messenger/MessageObject$GroupedMessages;
    //   15288: getfield 2267	org/telegram/messenger/MessageObject$GroupedMessages:posArray	Ljava/util/ArrayList;
    //   15291: iload 6
    //   15293: invokevirtual 594	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   15296: checkcast 964	org/telegram/messenger/MessageObject$GroupedMessagePosition
    //   15299: astore_2
    //   15300: aload_2
    //   15301: getfield 2270	org/telegram/messenger/MessageObject$GroupedMessagePosition:minY	B
    //   15304: ifne +179 -> 15483
    //   15307: iload 9
    //   15309: i2d
    //   15310: dstore 15
    //   15312: aload_2
    //   15313: getfield 2273	org/telegram/messenger/MessageObject$GroupedMessagePosition:pw	I
    //   15316: i2f
    //   15317: ldc_w 1740
    //   15320: fdiv
    //   15321: iload 34
    //   15323: i2f
    //   15324: fmul
    //   15325: f2d
    //   15326: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   15329: dstore 41
    //   15331: aload_2
    //   15332: getfield 2276	org/telegram/messenger/MessageObject$GroupedMessagePosition:leftSpanOffset	I
    //   15335: ifeq +142 -> 15477
    //   15338: aload_2
    //   15339: getfield 2276	org/telegram/messenger/MessageObject$GroupedMessagePosition:leftSpanOffset	I
    //   15342: i2f
    //   15343: ldc_w 1740
    //   15346: fdiv
    //   15347: iload 34
    //   15349: i2f
    //   15350: fmul
    //   15351: f2d
    //   15352: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   15355: dstore 17
    //   15357: dload 17
    //   15359: dload 41
    //   15361: dadd
    //   15362: dload 15
    //   15364: dadd
    //   15365: d2i
    //   15366: istore 21
    //   15368: iload 8
    //   15370: istore 14
    //   15372: iinc 6 1
    //   15375: iload 14
    //   15377: istore 8
    //   15379: iload 21
    //   15381: istore 9
    //   15383: goto -114 -> 15269
    //   15386: aload_0
    //   15387: iload 11
    //   15389: ldc_w 478
    //   15392: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   15395: isub
    //   15396: putfield 1187	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   15399: goto -384 -> 15015
    //   15402: iconst_0
    //   15403: istore 8
    //   15405: goto -328 -> 15077
    //   15408: iload 6
    //   15410: istore 8
    //   15412: iload 21
    //   15414: istore 9
    //   15416: aload_1
    //   15417: invokevirtual 1253	org/telegram/messenger/MessageObject:needDrawBluredPreview	()Z
    //   15420: ifeq -278 -> 15142
    //   15423: invokestatic 2226	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   15426: ifeq +21 -> 15447
    //   15429: invokestatic 2258	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   15432: i2f
    //   15433: ldc_w 2766
    //   15436: fmul
    //   15437: f2i
    //   15438: istore 8
    //   15440: iload 8
    //   15442: istore 9
    //   15444: goto -302 -> 15142
    //   15447: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   15450: getfield 2253	android/graphics/Point:x	I
    //   15453: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   15456: getfield 2262	android/graphics/Point:y	I
    //   15459: invokestatic 1195	java/lang/Math:min	(II)I
    //   15462: i2f
    //   15463: ldc_w 2766
    //   15466: fmul
    //   15467: f2i
    //   15468: istore 8
    //   15470: iload 8
    //   15472: istore 9
    //   15474: goto -332 -> 15142
    //   15477: dconst_0
    //   15478: dstore 17
    //   15480: goto -123 -> 15357
    //   15483: aload_2
    //   15484: getfield 2270	org/telegram/messenger/MessageObject$GroupedMessagePosition:minY	B
    //   15487: aload_0
    //   15488: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   15491: getfield 2270	org/telegram/messenger/MessageObject$GroupedMessagePosition:minY	B
    //   15494: if_icmpne +77 -> 15571
    //   15497: iload 8
    //   15499: i2d
    //   15500: dstore 41
    //   15502: aload_2
    //   15503: getfield 2273	org/telegram/messenger/MessageObject$GroupedMessagePosition:pw	I
    //   15506: i2f
    //   15507: ldc_w 1740
    //   15510: fdiv
    //   15511: iload 34
    //   15513: i2f
    //   15514: fmul
    //   15515: f2d
    //   15516: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   15519: dstore 15
    //   15521: aload_2
    //   15522: getfield 2276	org/telegram/messenger/MessageObject$GroupedMessagePosition:leftSpanOffset	I
    //   15525: ifeq +40 -> 15565
    //   15528: aload_2
    //   15529: getfield 2276	org/telegram/messenger/MessageObject$GroupedMessagePosition:leftSpanOffset	I
    //   15532: i2f
    //   15533: ldc_w 1740
    //   15536: fdiv
    //   15537: iload 34
    //   15539: i2f
    //   15540: fmul
    //   15541: f2d
    //   15542: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   15545: dstore 17
    //   15547: dload 17
    //   15549: dload 15
    //   15551: dadd
    //   15552: dload 41
    //   15554: dadd
    //   15555: d2i
    //   15556: istore 14
    //   15558: iload 9
    //   15560: istore 21
    //   15562: goto -190 -> 15372
    //   15565: dconst_0
    //   15566: dstore 17
    //   15568: goto -21 -> 15547
    //   15571: iload 8
    //   15573: istore 14
    //   15575: iload 9
    //   15577: istore 21
    //   15579: aload_2
    //   15580: getfield 2270	org/telegram/messenger/MessageObject$GroupedMessagePosition:minY	B
    //   15583: aload_0
    //   15584: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   15587: getfield 2270	org/telegram/messenger/MessageObject$GroupedMessagePosition:minY	B
    //   15590: if_icmple -218 -> 15372
    //   15593: iload 11
    //   15595: iload 9
    //   15597: iload 8
    //   15599: isub
    //   15600: iadd
    //   15601: istore 9
    //   15603: iload 9
    //   15605: ldc_w 1705
    //   15608: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   15611: isub
    //   15612: istore 9
    //   15614: iload 9
    //   15616: istore 8
    //   15618: aload_0
    //   15619: getfield 2278	org/telegram/ui/Cells/ChatMessageCell:isAvatarVisible	Z
    //   15622: ifeq +14 -> 15636
    //   15625: iload 9
    //   15627: ldc_w 781
    //   15630: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   15633: isub
    //   15634: istore 8
    //   15636: aload_0
    //   15637: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   15640: getfield 3641	org/telegram/messenger/MessageObject$GroupedMessagePosition:siblingHeights	[F
    //   15643: ifnull +546 -> 16189
    //   15646: iconst_0
    //   15647: istore 6
    //   15649: iconst_0
    //   15650: istore 9
    //   15652: iload 9
    //   15654: aload_0
    //   15655: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   15658: getfield 3641	org/telegram/messenger/MessageObject$GroupedMessagePosition:siblingHeights	[F
    //   15661: arraylength
    //   15662: if_icmpge +32 -> 15694
    //   15665: iload 6
    //   15667: aload_0
    //   15668: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   15671: getfield 3641	org/telegram/messenger/MessageObject$GroupedMessagePosition:siblingHeights	[F
    //   15674: iload 9
    //   15676: faload
    //   15677: fload 40
    //   15679: fmul
    //   15680: f2d
    //   15681: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   15684: d2i
    //   15685: iadd
    //   15686: istore 6
    //   15688: iinc 9 1
    //   15691: goto -39 -> 15652
    //   15694: iload 6
    //   15696: aload_0
    //   15697: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   15700: getfield 1865	org/telegram/messenger/MessageObject$GroupedMessagePosition:maxY	B
    //   15703: aload_0
    //   15704: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   15707: getfield 2270	org/telegram/messenger/MessageObject$GroupedMessagePosition:minY	B
    //   15710: isub
    //   15711: ldc_w 1520
    //   15714: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   15717: imul
    //   15718: iadd
    //   15719: istore 9
    //   15721: aload_0
    //   15722: iload 8
    //   15724: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   15727: iload 8
    //   15729: ldc_w 554
    //   15732: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   15735: isub
    //   15736: istore 30
    //   15738: iload 30
    //   15740: istore 8
    //   15742: iload 8
    //   15744: istore 21
    //   15746: aload_0
    //   15747: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   15750: getfield 3008	org/telegram/messenger/MessageObject$GroupedMessagePosition:edge	Z
    //   15753: ifne +14 -> 15767
    //   15756: iload 8
    //   15758: ldc_w 587
    //   15761: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   15764: iadd
    //   15765: istore 21
    //   15767: iload 9
    //   15769: istore 28
    //   15771: iconst_0
    //   15772: iload 21
    //   15774: ldc_w 587
    //   15777: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   15780: isub
    //   15781: iadd
    //   15782: istore 22
    //   15784: aload_0
    //   15785: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   15788: getfield 2210	org/telegram/messenger/MessageObject$GroupedMessagePosition:flags	I
    //   15791: bipush 8
    //   15793: iand
    //   15794: ifne +65 -> 15859
    //   15797: iload 22
    //   15799: istore 6
    //   15801: iload 9
    //   15803: istore 35
    //   15805: iload 28
    //   15807: istore 11
    //   15809: iload 21
    //   15811: istore 14
    //   15813: iload 30
    //   15815: istore 8
    //   15817: aload_0
    //   15818: getfield 705	org/telegram/ui/Cells/ChatMessageCell:currentMessagesGroup	Lorg/telegram/messenger/MessageObject$GroupedMessages;
    //   15821: getfield 3644	org/telegram/messenger/MessageObject$GroupedMessages:hasSibling	Z
    //   15824: ifeq +729 -> 16553
    //   15827: iload 22
    //   15829: istore 6
    //   15831: iload 9
    //   15833: istore 35
    //   15835: iload 28
    //   15837: istore 11
    //   15839: iload 21
    //   15841: istore 14
    //   15843: iload 30
    //   15845: istore 8
    //   15847: aload_0
    //   15848: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   15851: getfield 2210	org/telegram/messenger/MessageObject$GroupedMessagePosition:flags	I
    //   15854: iconst_4
    //   15855: iand
    //   15856: ifne +697 -> 16553
    //   15859: iload 22
    //   15861: aload_0
    //   15862: aload_0
    //   15863: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   15866: invokespecial 3610	org/telegram/ui/Cells/ChatMessageCell:getAdditionalWidthForPosition	(Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;)I
    //   15869: iadd
    //   15870: istore 22
    //   15872: aload_0
    //   15873: getfield 705	org/telegram/ui/Cells/ChatMessageCell:currentMessagesGroup	Lorg/telegram/messenger/MessageObject$GroupedMessages;
    //   15876: getfield 3278	org/telegram/messenger/MessageObject$GroupedMessages:messages	Ljava/util/ArrayList;
    //   15879: invokevirtual 590	java/util/ArrayList:size	()I
    //   15882: istore 38
    //   15884: iconst_0
    //   15885: istore 31
    //   15887: iload 22
    //   15889: istore 6
    //   15891: iload 9
    //   15893: istore 35
    //   15895: iload 28
    //   15897: istore 11
    //   15899: iload 21
    //   15901: istore 14
    //   15903: iload 30
    //   15905: istore 8
    //   15907: iload 31
    //   15909: iload 38
    //   15911: if_icmpge +642 -> 16553
    //   15914: aload_0
    //   15915: getfield 705	org/telegram/ui/Cells/ChatMessageCell:currentMessagesGroup	Lorg/telegram/messenger/MessageObject$GroupedMessages;
    //   15918: getfield 3278	org/telegram/messenger/MessageObject$GroupedMessages:messages	Ljava/util/ArrayList;
    //   15921: iload 31
    //   15923: invokevirtual 594	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   15926: checkcast 469	org/telegram/messenger/MessageObject
    //   15929: astore_2
    //   15930: aload_0
    //   15931: getfield 705	org/telegram/ui/Cells/ChatMessageCell:currentMessagesGroup	Lorg/telegram/messenger/MessageObject$GroupedMessages;
    //   15934: getfield 2267	org/telegram/messenger/MessageObject$GroupedMessages:posArray	Ljava/util/ArrayList;
    //   15937: iload 31
    //   15939: invokevirtual 594	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   15942: checkcast 964	org/telegram/messenger/MessageObject$GroupedMessagePosition
    //   15945: astore 20
    //   15947: iload 22
    //   15949: istore 6
    //   15951: iload 30
    //   15953: istore 8
    //   15955: aload 20
    //   15957: aload_0
    //   15958: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   15961: if_acmpeq +561 -> 16522
    //   15964: iload 22
    //   15966: istore 6
    //   15968: iload 30
    //   15970: istore 8
    //   15972: aload 20
    //   15974: getfield 2210	org/telegram/messenger/MessageObject$GroupedMessagePosition:flags	I
    //   15977: bipush 8
    //   15979: iand
    //   15980: ifeq +542 -> 16522
    //   15983: aload 20
    //   15985: getfield 2273	org/telegram/messenger/MessageObject$GroupedMessagePosition:pw	I
    //   15988: i2f
    //   15989: ldc_w 1740
    //   15992: fdiv
    //   15993: iload 34
    //   15995: i2f
    //   15996: fmul
    //   15997: f2d
    //   15998: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   16001: d2i
    //   16002: istore 35
    //   16004: iload 35
    //   16006: istore 8
    //   16008: aload 20
    //   16010: getfield 2270	org/telegram/messenger/MessageObject$GroupedMessagePosition:minY	B
    //   16013: ifeq +323 -> 16336
    //   16016: aload_1
    //   16017: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   16020: ifeq +13 -> 16033
    //   16023: aload 20
    //   16025: getfield 2210	org/telegram/messenger/MessageObject$GroupedMessagePosition:flags	I
    //   16028: iconst_1
    //   16029: iand
    //   16030: ifne +28 -> 16058
    //   16033: iload 35
    //   16035: istore 8
    //   16037: aload_1
    //   16038: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   16041: ifne +295 -> 16336
    //   16044: iload 35
    //   16046: istore 8
    //   16048: aload 20
    //   16050: getfield 2210	org/telegram/messenger/MessageObject$GroupedMessagePosition:flags	I
    //   16053: iconst_2
    //   16054: iand
    //   16055: ifeq +281 -> 16336
    //   16058: iconst_0
    //   16059: istore 8
    //   16061: iconst_0
    //   16062: istore 14
    //   16064: iconst_0
    //   16065: istore 6
    //   16067: iload 6
    //   16069: aload_0
    //   16070: getfield 705	org/telegram/ui/Cells/ChatMessageCell:currentMessagesGroup	Lorg/telegram/messenger/MessageObject$GroupedMessages;
    //   16073: getfield 2267	org/telegram/messenger/MessageObject$GroupedMessages:posArray	Ljava/util/ArrayList;
    //   16076: invokevirtual 590	java/util/ArrayList:size	()I
    //   16079: if_icmpge +247 -> 16326
    //   16082: aload_0
    //   16083: getfield 705	org/telegram/ui/Cells/ChatMessageCell:currentMessagesGroup	Lorg/telegram/messenger/MessageObject$GroupedMessages;
    //   16086: getfield 2267	org/telegram/messenger/MessageObject$GroupedMessages:posArray	Ljava/util/ArrayList;
    //   16089: iload 6
    //   16091: invokevirtual 594	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   16094: checkcast 964	org/telegram/messenger/MessageObject$GroupedMessagePosition
    //   16097: astore 10
    //   16099: aload 10
    //   16101: getfield 2270	org/telegram/messenger/MessageObject$GroupedMessagePosition:minY	B
    //   16104: ifne +111 -> 16215
    //   16107: iload 8
    //   16109: i2d
    //   16110: dstore 41
    //   16112: aload 10
    //   16114: getfield 2273	org/telegram/messenger/MessageObject$GroupedMessagePosition:pw	I
    //   16117: i2f
    //   16118: ldc_w 1740
    //   16121: fdiv
    //   16122: iload 34
    //   16124: i2f
    //   16125: fmul
    //   16126: f2d
    //   16127: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   16130: dstore 15
    //   16132: aload 10
    //   16134: getfield 2276	org/telegram/messenger/MessageObject$GroupedMessagePosition:leftSpanOffset	I
    //   16137: ifeq +72 -> 16209
    //   16140: aload 10
    //   16142: getfield 2276	org/telegram/messenger/MessageObject$GroupedMessagePosition:leftSpanOffset	I
    //   16145: i2f
    //   16146: ldc_w 1740
    //   16149: fdiv
    //   16150: iload 34
    //   16152: i2f
    //   16153: fmul
    //   16154: f2d
    //   16155: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   16158: dstore 17
    //   16160: dload 17
    //   16162: dload 15
    //   16164: dadd
    //   16165: dload 41
    //   16167: dadd
    //   16168: d2i
    //   16169: istore 30
    //   16171: iload 14
    //   16173: istore 11
    //   16175: iinc 6 1
    //   16178: iload 11
    //   16180: istore 14
    //   16182: iload 30
    //   16184: istore 8
    //   16186: goto -119 -> 16067
    //   16189: aload_0
    //   16190: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   16193: getfield 3647	org/telegram/messenger/MessageObject$GroupedMessagePosition:ph	F
    //   16196: fload 40
    //   16198: fmul
    //   16199: f2d
    //   16200: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   16203: d2i
    //   16204: istore 9
    //   16206: goto -485 -> 15721
    //   16209: dconst_0
    //   16210: dstore 17
    //   16212: goto -52 -> 16160
    //   16215: aload 10
    //   16217: getfield 2270	org/telegram/messenger/MessageObject$GroupedMessagePosition:minY	B
    //   16220: aload 20
    //   16222: getfield 2270	org/telegram/messenger/MessageObject$GroupedMessagePosition:minY	B
    //   16225: if_icmpne +80 -> 16305
    //   16228: iload 14
    //   16230: i2d
    //   16231: dstore 41
    //   16233: aload 10
    //   16235: getfield 2273	org/telegram/messenger/MessageObject$GroupedMessagePosition:pw	I
    //   16238: i2f
    //   16239: ldc_w 1740
    //   16242: fdiv
    //   16243: iload 34
    //   16245: i2f
    //   16246: fmul
    //   16247: f2d
    //   16248: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   16251: dstore 15
    //   16253: aload 10
    //   16255: getfield 2276	org/telegram/messenger/MessageObject$GroupedMessagePosition:leftSpanOffset	I
    //   16258: ifeq +41 -> 16299
    //   16261: aload 10
    //   16263: getfield 2276	org/telegram/messenger/MessageObject$GroupedMessagePosition:leftSpanOffset	I
    //   16266: i2f
    //   16267: ldc_w 1740
    //   16270: fdiv
    //   16271: iload 34
    //   16273: i2f
    //   16274: fmul
    //   16275: f2d
    //   16276: invokestatic 1182	java/lang/Math:ceil	(D)D
    //   16279: dstore 17
    //   16281: dload 17
    //   16283: dload 15
    //   16285: dadd
    //   16286: dload 41
    //   16288: dadd
    //   16289: d2i
    //   16290: istore 11
    //   16292: iload 8
    //   16294: istore 30
    //   16296: goto -121 -> 16175
    //   16299: dconst_0
    //   16300: dstore 17
    //   16302: goto -21 -> 16281
    //   16305: iload 14
    //   16307: istore 11
    //   16309: iload 8
    //   16311: istore 30
    //   16313: aload 10
    //   16315: getfield 2270	org/telegram/messenger/MessageObject$GroupedMessagePosition:minY	B
    //   16318: aload 20
    //   16320: getfield 2270	org/telegram/messenger/MessageObject$GroupedMessagePosition:minY	B
    //   16323: if_icmple -148 -> 16175
    //   16326: iload 35
    //   16328: iload 8
    //   16330: iload 14
    //   16332: isub
    //   16333: iadd
    //   16334: istore 8
    //   16336: iload 8
    //   16338: ldc_w 1185
    //   16341: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   16344: isub
    //   16345: istore 6
    //   16347: iload 6
    //   16349: istore 8
    //   16351: aload_0
    //   16352: getfield 2040	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   16355: ifeq +53 -> 16408
    //   16358: iload 6
    //   16360: istore 8
    //   16362: aload_2
    //   16363: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   16366: ifne +42 -> 16408
    //   16369: iload 6
    //   16371: istore 8
    //   16373: aload_2
    //   16374: invokevirtual 2043	org/telegram/messenger/MessageObject:needDrawAvatar	()Z
    //   16377: ifeq +31 -> 16408
    //   16380: aload 20
    //   16382: ifnull +15 -> 16397
    //   16385: iload 6
    //   16387: istore 8
    //   16389: aload 20
    //   16391: getfield 3008	org/telegram/messenger/MessageObject$GroupedMessagePosition:edge	Z
    //   16394: ifeq +14 -> 16408
    //   16397: iload 6
    //   16399: ldc_w 781
    //   16402: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   16405: isub
    //   16406: istore 8
    //   16408: iload 8
    //   16410: aload_0
    //   16411: aload 20
    //   16413: invokespecial 3610	org/telegram/ui/Cells/ChatMessageCell:getAdditionalWidthForPosition	(Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;)I
    //   16416: iadd
    //   16417: istore 8
    //   16419: iload 8
    //   16421: istore 14
    //   16423: aload 20
    //   16425: getfield 3008	org/telegram/messenger/MessageObject$GroupedMessagePosition:edge	Z
    //   16428: ifne +14 -> 16442
    //   16431: iload 8
    //   16433: ldc_w 587
    //   16436: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   16439: iadd
    //   16440: istore 14
    //   16442: iload 22
    //   16444: iload 14
    //   16446: iadd
    //   16447: istore 11
    //   16449: aload 20
    //   16451: getfield 2769	org/telegram/messenger/MessageObject$GroupedMessagePosition:minX	B
    //   16454: aload_0
    //   16455: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   16458: getfield 2769	org/telegram/messenger/MessageObject$GroupedMessagePosition:minX	B
    //   16461: if_icmplt +42 -> 16503
    //   16464: iload 11
    //   16466: istore 6
    //   16468: iload 14
    //   16470: istore 8
    //   16472: aload_0
    //   16473: getfield 705	org/telegram/ui/Cells/ChatMessageCell:currentMessagesGroup	Lorg/telegram/messenger/MessageObject$GroupedMessages;
    //   16476: getfield 3644	org/telegram/messenger/MessageObject$GroupedMessages:hasSibling	Z
    //   16479: ifeq +43 -> 16522
    //   16482: iload 11
    //   16484: istore 6
    //   16486: iload 14
    //   16488: istore 8
    //   16490: aload 20
    //   16492: getfield 2270	org/telegram/messenger/MessageObject$GroupedMessagePosition:minY	B
    //   16495: aload 20
    //   16497: getfield 1865	org/telegram/messenger/MessageObject$GroupedMessagePosition:maxY	B
    //   16500: if_icmpeq +22 -> 16522
    //   16503: aload_0
    //   16504: aload_0
    //   16505: getfield 1777	org/telegram/ui/Cells/ChatMessageCell:captionOffsetX	I
    //   16508: iload 14
    //   16510: isub
    //   16511: putfield 1777	org/telegram/ui/Cells/ChatMessageCell:captionOffsetX	I
    //   16514: iload 14
    //   16516: istore 8
    //   16518: iload 11
    //   16520: istore 6
    //   16522: aload_2
    //   16523: getfield 3184	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   16526: ifnull +593 -> 17119
    //   16529: aload_0
    //   16530: getfield 631	org/telegram/ui/Cells/ChatMessageCell:currentCaption	Ljava/lang/CharSequence;
    //   16533: ifnull +578 -> 17111
    //   16536: aload_0
    //   16537: aconst_null
    //   16538: putfield 631	org/telegram/ui/Cells/ChatMessageCell:currentCaption	Ljava/lang/CharSequence;
    //   16541: iload 21
    //   16543: istore 14
    //   16545: iload 28
    //   16547: istore 11
    //   16549: iload 9
    //   16551: istore 35
    //   16553: iload 19
    //   16555: istore 9
    //   16557: iload 12
    //   16559: istore 22
    //   16561: aload_0
    //   16562: getfield 631	org/telegram/ui/Cells/ChatMessageCell:currentCaption	Ljava/lang/CharSequence;
    //   16565: ifnull +282 -> 16847
    //   16568: iload 29
    //   16570: istore 21
    //   16572: getstatic 790	android/os/Build$VERSION:SDK_INT	I
    //   16575: bipush 24
    //   16577: if_icmplt +629 -> 17206
    //   16580: iload 29
    //   16582: istore 21
    //   16584: aload_0
    //   16585: aload_0
    //   16586: getfield 631	org/telegram/ui/Cells/ChatMessageCell:currentCaption	Ljava/lang/CharSequence;
    //   16589: iconst_0
    //   16590: aload_0
    //   16591: getfield 631	org/telegram/ui/Cells/ChatMessageCell:currentCaption	Ljava/lang/CharSequence;
    //   16594: invokeinterface 2192 1 0
    //   16599: getstatic 2970	org/telegram/ui/ActionBar/Theme:chat_msgTextPaint	Landroid/text/TextPaint;
    //   16602: iload 6
    //   16604: invokestatic 3474	android/text/StaticLayout$Builder:obtain	(Ljava/lang/CharSequence;IILandroid/text/TextPaint;I)Landroid/text/StaticLayout$Builder;
    //   16607: iconst_1
    //   16608: invokevirtual 3478	android/text/StaticLayout$Builder:setBreakStrategy	(I)Landroid/text/StaticLayout$Builder;
    //   16611: iconst_0
    //   16612: invokevirtual 3481	android/text/StaticLayout$Builder:setHyphenationFrequency	(I)Landroid/text/StaticLayout$Builder;
    //   16615: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   16618: invokevirtual 3485	android/text/StaticLayout$Builder:setAlignment	(Landroid/text/Layout$Alignment;)Landroid/text/StaticLayout$Builder;
    //   16621: invokevirtual 3489	android/text/StaticLayout$Builder:build	()Landroid/text/StaticLayout;
    //   16624: putfield 635	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   16627: iload 19
    //   16629: istore 9
    //   16631: iload 12
    //   16633: istore 22
    //   16635: iload 29
    //   16637: istore 21
    //   16639: aload_0
    //   16640: getfield 635	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   16643: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   16646: ifle +201 -> 16847
    //   16649: iload 29
    //   16651: istore 21
    //   16653: aload_0
    //   16654: iload 6
    //   16656: putfield 643	org/telegram/ui/Cells/ChatMessageCell:captionWidth	I
    //   16659: iload 29
    //   16661: istore 21
    //   16663: aload_0
    //   16664: aload_0
    //   16665: getfield 635	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   16668: invokevirtual 780	android/text/StaticLayout:getHeight	()I
    //   16671: putfield 647	org/telegram/ui/Cells/ChatMessageCell:captionHeight	I
    //   16674: iload 29
    //   16676: istore 21
    //   16678: aload_0
    //   16679: aload_0
    //   16680: getfield 647	org/telegram/ui/Cells/ChatMessageCell:captionHeight	I
    //   16683: ldc_w 1705
    //   16686: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   16689: iadd
    //   16690: putfield 2933	org/telegram/ui/Cells/ChatMessageCell:addedCaptionHeight	I
    //   16693: iload 29
    //   16695: istore 21
    //   16697: aload_0
    //   16698: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   16701: ifnull +20 -> 16721
    //   16704: iload 29
    //   16706: istore 21
    //   16708: aload_0
    //   16709: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   16712: getfield 2210	org/telegram/messenger/MessageObject$GroupedMessagePosition:flags	I
    //   16715: bipush 8
    //   16717: iand
    //   16718: ifeq +547 -> 17265
    //   16721: iload 29
    //   16723: istore 21
    //   16725: iconst_0
    //   16726: aload_0
    //   16727: getfield 2933	org/telegram/ui/Cells/ChatMessageCell:addedCaptionHeight	I
    //   16730: iadd
    //   16731: istore 30
    //   16733: iload 30
    //   16735: istore 21
    //   16737: aload_0
    //   16738: getfield 635	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   16741: aload_0
    //   16742: getfield 635	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   16745: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   16748: iconst_1
    //   16749: isub
    //   16750: invokevirtual 662	android/text/StaticLayout:getLineWidth	(I)F
    //   16753: fstore 39
    //   16755: iload 30
    //   16757: istore 21
    //   16759: aload_0
    //   16760: getfield 635	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   16763: aload_0
    //   16764: getfield 635	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   16767: invokevirtual 1226	android/text/StaticLayout:getLineCount	()I
    //   16770: iconst_1
    //   16771: isub
    //   16772: invokevirtual 659	android/text/StaticLayout:getLineLeft	(I)F
    //   16775: fstore 40
    //   16777: iload 30
    //   16779: istore 9
    //   16781: iload 12
    //   16783: istore 22
    //   16785: iload 30
    //   16787: istore 21
    //   16789: fconst_2
    //   16790: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   16793: iload 6
    //   16795: iadd
    //   16796: i2f
    //   16797: fload 39
    //   16799: fload 40
    //   16801: fadd
    //   16802: fsub
    //   16803: iload 36
    //   16805: i2f
    //   16806: fcmpg
    //   16807: ifge +40 -> 16847
    //   16810: iload 30
    //   16812: istore 21
    //   16814: iload 30
    //   16816: ldc_w 478
    //   16819: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   16822: iadd
    //   16823: istore 9
    //   16825: iload 9
    //   16827: istore 21
    //   16829: aload_0
    //   16830: aload_0
    //   16831: getfield 2933	org/telegram/ui/Cells/ChatMessageCell:addedCaptionHeight	I
    //   16834: ldc_w 478
    //   16837: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   16840: iadd
    //   16841: putfield 2933	org/telegram/ui/Cells/ChatMessageCell:addedCaptionHeight	I
    //   16844: iconst_1
    //   16845: istore 22
    //   16847: getstatic 2340	java/util/Locale:US	Ljava/util/Locale;
    //   16850: ldc_w 3433
    //   16853: iconst_2
    //   16854: anewarray 1242	java/lang/Object
    //   16857: dup
    //   16858: iconst_0
    //   16859: iload 8
    //   16861: i2f
    //   16862: getstatic 2350	org/telegram/messenger/AndroidUtilities:density	F
    //   16865: fdiv
    //   16866: f2i
    //   16867: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   16870: aastore
    //   16871: dup
    //   16872: iconst_1
    //   16873: iload 35
    //   16875: i2f
    //   16876: getstatic 2350	org/telegram/messenger/AndroidUtilities:density	F
    //   16879: fdiv
    //   16880: f2i
    //   16881: invokestatic 1015	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   16884: aastore
    //   16885: invokestatic 2353	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   16888: astore_2
    //   16889: aload_0
    //   16890: aload_2
    //   16891: putfield 1406	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilterThumb	Ljava/lang/String;
    //   16894: aload_0
    //   16895: aload_2
    //   16896: putfield 1358	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   16899: aload_1
    //   16900: getfield 1336	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   16903: ifnull +14 -> 16917
    //   16906: aload_1
    //   16907: getfield 1336	org/telegram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   16910: invokevirtual 590	java/util/ArrayList:size	()I
    //   16913: iconst_1
    //   16914: if_icmpgt +28 -> 16942
    //   16917: aload_1
    //   16918: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   16921: iconst_3
    //   16922: if_icmpeq +20 -> 16942
    //   16925: aload_1
    //   16926: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   16929: bipush 8
    //   16931: if_icmpeq +11 -> 16942
    //   16934: aload_1
    //   16935: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   16938: iconst_5
    //   16939: if_icmpne +64 -> 17003
    //   16942: aload_1
    //   16943: invokevirtual 1253	org/telegram/messenger/MessageObject:needDrawBluredPreview	()Z
    //   16946: ifeq +339 -> 17285
    //   16949: aload_0
    //   16950: new 1320	java/lang/StringBuilder
    //   16953: dup
    //   16954: invokespecial 1321	java/lang/StringBuilder:<init>	()V
    //   16957: aload_0
    //   16958: getfield 1358	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   16961: invokevirtual 1325	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   16964: ldc_w 3649
    //   16967: invokevirtual 1325	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   16970: invokevirtual 1333	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   16973: putfield 1358	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   16976: aload_0
    //   16977: new 1320	java/lang/StringBuilder
    //   16980: dup
    //   16981: invokespecial 1321	java/lang/StringBuilder:<init>	()V
    //   16984: aload_0
    //   16985: getfield 1406	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilterThumb	Ljava/lang/String;
    //   16988: invokevirtual 1325	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   16991: ldc_w 3649
    //   16994: invokevirtual 1325	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   16997: invokevirtual 1333	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   17000: putfield 1406	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilterThumb	Ljava/lang/String;
    //   17003: iconst_0
    //   17004: istore 8
    //   17006: aload_1
    //   17007: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   17010: iconst_3
    //   17011: if_icmpeq +20 -> 17031
    //   17014: aload_1
    //   17015: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   17018: bipush 8
    //   17020: if_icmpeq +11 -> 17031
    //   17023: aload_1
    //   17024: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   17027: iconst_5
    //   17028: if_icmpne +6 -> 17034
    //   17031: iconst_1
    //   17032: istore 8
    //   17034: aload_0
    //   17035: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   17038: ifnull +26 -> 17064
    //   17041: iload 8
    //   17043: ifne +21 -> 17064
    //   17046: aload_0
    //   17047: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   17050: getfield 1407	org/telegram/tgnet/TLRPC$PhotoSize:size	I
    //   17053: ifne +11 -> 17064
    //   17056: aload_0
    //   17057: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   17060: iconst_m1
    //   17061: putfield 1407	org/telegram/tgnet/TLRPC$PhotoSize:size	I
    //   17064: aload_1
    //   17065: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   17068: iconst_1
    //   17069: if_icmpne +574 -> 17643
    //   17072: aload_1
    //   17073: getfield 3652	org/telegram/messenger/MessageObject:useCustomPhoto	Z
    //   17076: ifeq +239 -> 17315
    //   17079: aload_0
    //   17080: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   17083: aload_0
    //   17084: invokevirtual 2233	org/telegram/ui/Cells/ChatMessageCell:getResources	()Landroid/content/res/Resources;
    //   17087: ldc_w 3653
    //   17090: invokevirtual 3657	android/content/res/Resources:getDrawable	(I)Landroid/graphics/drawable/Drawable;
    //   17093: invokevirtual 1368	org/telegram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   17096: iload 9
    //   17098: istore 30
    //   17100: iload 11
    //   17102: istore 9
    //   17104: iload 14
    //   17106: istore 8
    //   17108: goto -5538 -> 11570
    //   17111: aload_0
    //   17112: aload_2
    //   17113: getfield 3184	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   17116: putfield 631	org/telegram/ui/Cells/ChatMessageCell:currentCaption	Ljava/lang/CharSequence;
    //   17119: iinc 31 1
    //   17122: iload 6
    //   17124: istore 22
    //   17126: iload 8
    //   17128: istore 30
    //   17130: goto -1243 -> 15887
    //   17133: iload 9
    //   17135: istore 14
    //   17137: iload 8
    //   17139: istore 11
    //   17141: aload_0
    //   17142: ldc_w 554
    //   17145: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   17148: iload 9
    //   17150: iadd
    //   17151: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   17154: aload_0
    //   17155: getfield 615	org/telegram/ui/Cells/ChatMessageCell:mediaBackground	Z
    //   17158: ifne +18 -> 17176
    //   17161: aload_0
    //   17162: aload_0
    //   17163: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   17166: ldc_w 1705
    //   17169: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   17172: iadd
    //   17173: putfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   17176: aload_0
    //   17177: aload_1
    //   17178: getfield 3184	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   17181: putfield 631	org/telegram/ui/Cells/ChatMessageCell:currentCaption	Ljava/lang/CharSequence;
    //   17184: iload 14
    //   17186: ldc_w 587
    //   17189: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   17192: isub
    //   17193: istore 6
    //   17195: iload 8
    //   17197: istore 35
    //   17199: iload 9
    //   17201: istore 8
    //   17203: goto -650 -> 16553
    //   17206: iload 29
    //   17208: istore 21
    //   17210: new 350	android/text/StaticLayout
    //   17213: astore_2
    //   17214: iload 29
    //   17216: istore 21
    //   17218: aload_2
    //   17219: aload_0
    //   17220: getfield 631	org/telegram/ui/Cells/ChatMessageCell:currentCaption	Ljava/lang/CharSequence;
    //   17223: getstatic 2970	org/telegram/ui/ActionBar/Theme:chat_msgTextPaint	Landroid/text/TextPaint;
    //   17226: iload 6
    //   17228: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   17231: fconst_1
    //   17232: fconst_0
    //   17233: iconst_0
    //   17234: invokespecial 949	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   17237: iload 29
    //   17239: istore 21
    //   17241: aload_0
    //   17242: aload_2
    //   17243: putfield 635	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   17246: goto -619 -> 16627
    //   17249: astore_2
    //   17250: aload_2
    //   17251: invokestatic 714	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   17254: iload 21
    //   17256: istore 9
    //   17258: iload 12
    //   17260: istore 22
    //   17262: goto -415 -> 16847
    //   17265: iload 29
    //   17267: istore 21
    //   17269: aload_0
    //   17270: aconst_null
    //   17271: putfield 635	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   17274: iload 19
    //   17276: istore 9
    //   17278: iload 12
    //   17280: istore 22
    //   17282: goto -435 -> 16847
    //   17285: aload_0
    //   17286: new 1320	java/lang/StringBuilder
    //   17289: dup
    //   17290: invokespecial 1321	java/lang/StringBuilder:<init>	()V
    //   17293: aload_0
    //   17294: getfield 1406	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilterThumb	Ljava/lang/String;
    //   17297: invokevirtual 1325	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17300: ldc_w 3659
    //   17303: invokevirtual 1325	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17306: invokevirtual 1333	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   17309: putfield 1406	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilterThumb	Ljava/lang/String;
    //   17312: goto -309 -> 17003
    //   17315: aload_0
    //   17316: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   17319: ifnull +298 -> 17617
    //   17322: iconst_1
    //   17323: istore 6
    //   17325: aload_0
    //   17326: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   17329: invokestatic 3525	org/telegram/messenger/FileLoader:getAttachFileName	(Lorg/telegram/tgnet/TLObject;)Ljava/lang/String;
    //   17332: astore_2
    //   17333: aload_1
    //   17334: getfield 2747	org/telegram/messenger/MessageObject:mediaExists	Z
    //   17337: ifeq +145 -> 17482
    //   17340: aload_0
    //   17341: getfield 342	org/telegram/ui/Cells/ChatMessageCell:currentAccount	I
    //   17344: invokestatic 396	org/telegram/messenger/DownloadController:getInstance	(I)Lorg/telegram/messenger/DownloadController;
    //   17347: aload_0
    //   17348: invokevirtual 2964	org/telegram/messenger/DownloadController:removeLoadingFileObserver	(Lorg/telegram/messenger/DownloadController$FileDownloadProgressListener;)V
    //   17351: iload 6
    //   17353: ifne +34 -> 17387
    //   17356: aload_0
    //   17357: getfield 342	org/telegram/ui/Cells/ChatMessageCell:currentAccount	I
    //   17360: invokestatic 396	org/telegram/messenger/DownloadController:getInstance	(I)Lorg/telegram/messenger/DownloadController;
    //   17363: aload_0
    //   17364: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   17367: invokevirtual 3528	org/telegram/messenger/DownloadController:canDownloadMedia	(Lorg/telegram/messenger/MessageObject;)Z
    //   17370: ifne +17 -> 17387
    //   17373: aload_0
    //   17374: getfield 342	org/telegram/ui/Cells/ChatMessageCell:currentAccount	I
    //   17377: invokestatic 1378	org/telegram/messenger/FileLoader:getInstance	(I)Lorg/telegram/messenger/FileLoader;
    //   17380: aload_2
    //   17381: invokevirtual 3531	org/telegram/messenger/FileLoader:isLoadingFile	(Ljava/lang/String;)Z
    //   17384: ifeq +127 -> 17511
    //   17387: aload_0
    //   17388: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   17391: astore 23
    //   17393: aload_0
    //   17394: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   17397: getfield 1292	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   17400: astore 10
    //   17402: aload_0
    //   17403: getfield 1358	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   17406: astore 20
    //   17408: aload_0
    //   17409: getfield 1404	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   17412: ifnull +76 -> 17488
    //   17415: aload_0
    //   17416: getfield 1404	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   17419: getfield 1292	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   17422: astore_2
    //   17423: aload_0
    //   17424: getfield 1406	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilterThumb	Ljava/lang/String;
    //   17427: astore 26
    //   17429: iload 8
    //   17431: ifeq +62 -> 17493
    //   17434: iconst_0
    //   17435: istore 8
    //   17437: aload_0
    //   17438: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   17441: invokevirtual 1410	org/telegram/messenger/MessageObject:shouldEncryptPhotoOrVideo	()Z
    //   17444: ifeq +61 -> 17505
    //   17447: iconst_2
    //   17448: istore 6
    //   17450: aload 23
    //   17452: aload 10
    //   17454: aload 20
    //   17456: aload_2
    //   17457: aload 26
    //   17459: iload 8
    //   17461: aconst_null
    //   17462: iload 6
    //   17464: invokevirtual 1413	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;I)V
    //   17467: iload 9
    //   17469: istore 30
    //   17471: iload 11
    //   17473: istore 9
    //   17475: iload 14
    //   17477: istore 8
    //   17479: goto -5909 -> 11570
    //   17482: iconst_0
    //   17483: istore 6
    //   17485: goto -134 -> 17351
    //   17488: aconst_null
    //   17489: astore_2
    //   17490: goto -67 -> 17423
    //   17493: aload_0
    //   17494: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   17497: getfield 1407	org/telegram/tgnet/TLRPC$PhotoSize:size	I
    //   17500: istore 8
    //   17502: goto -65 -> 17437
    //   17505: iconst_0
    //   17506: istore 6
    //   17508: goto -58 -> 17450
    //   17511: aload_0
    //   17512: iconst_1
    //   17513: putfield 2364	org/telegram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   17516: aload_0
    //   17517: getfield 1404	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   17520: ifnull +71 -> 17591
    //   17523: aload_0
    //   17524: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   17527: astore_2
    //   17528: aload_0
    //   17529: getfield 1404	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   17532: getfield 1292	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   17535: astore 10
    //   17537: aload_0
    //   17538: getfield 1406	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilterThumb	Ljava/lang/String;
    //   17541: astore 20
    //   17543: aload_0
    //   17544: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   17547: invokevirtual 1410	org/telegram/messenger/MessageObject:shouldEncryptPhotoOrVideo	()Z
    //   17550: ifeq +35 -> 17585
    //   17553: iconst_2
    //   17554: istore 8
    //   17556: aload_2
    //   17557: aconst_null
    //   17558: aconst_null
    //   17559: aload 10
    //   17561: aload 20
    //   17563: iconst_0
    //   17564: aconst_null
    //   17565: iload 8
    //   17567: invokevirtual 1413	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;I)V
    //   17570: iload 9
    //   17572: istore 30
    //   17574: iload 11
    //   17576: istore 9
    //   17578: iload 14
    //   17580: istore 8
    //   17582: goto -6012 -> 11570
    //   17585: iconst_0
    //   17586: istore 8
    //   17588: goto -32 -> 17556
    //   17591: aload_0
    //   17592: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   17595: aconst_null
    //   17596: checkcast 794	android/graphics/drawable/Drawable
    //   17599: invokevirtual 1368	org/telegram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   17602: iload 9
    //   17604: istore 30
    //   17606: iload 11
    //   17608: istore 9
    //   17610: iload 14
    //   17612: istore 8
    //   17614: goto -6044 -> 11570
    //   17617: aload_0
    //   17618: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   17621: aconst_null
    //   17622: checkcast 1364	android/graphics/drawable/BitmapDrawable
    //   17625: invokevirtual 1368	org/telegram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   17628: iload 9
    //   17630: istore 30
    //   17632: iload 11
    //   17634: istore 9
    //   17636: iload 14
    //   17638: istore 8
    //   17640: goto -6070 -> 11570
    //   17643: aload_1
    //   17644: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   17647: bipush 8
    //   17649: if_icmpeq +11 -> 17660
    //   17652: aload_1
    //   17653: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   17656: iconst_5
    //   17657: if_icmpne +372 -> 18029
    //   17660: aload_1
    //   17661: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   17664: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   17667: getfield 1150	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   17670: invokestatic 3525	org/telegram/messenger/FileLoader:getAttachFileName	(Lorg/telegram/tgnet/TLObject;)Ljava/lang/String;
    //   17673: astore_2
    //   17674: iconst_0
    //   17675: istore 8
    //   17677: aload_1
    //   17678: getfield 2750	org/telegram/messenger/MessageObject:attachPathExists	Z
    //   17681: ifeq +150 -> 17831
    //   17684: aload_0
    //   17685: getfield 342	org/telegram/ui/Cells/ChatMessageCell:currentAccount	I
    //   17688: invokestatic 396	org/telegram/messenger/DownloadController:getInstance	(I)Lorg/telegram/messenger/DownloadController;
    //   17691: aload_0
    //   17692: invokevirtual 2964	org/telegram/messenger/DownloadController:removeLoadingFileObserver	(Lorg/telegram/messenger/DownloadController$FileDownloadProgressListener;)V
    //   17695: iconst_1
    //   17696: istore 8
    //   17698: iconst_0
    //   17699: istore_3
    //   17700: aload_1
    //   17701: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   17704: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   17707: getfield 1150	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   17710: invokestatic 3534	org/telegram/messenger/MessageObject:isNewGifDocument	(Lorg/telegram/tgnet/TLRPC$Document;)Z
    //   17713: ifeq +131 -> 17844
    //   17716: aload_0
    //   17717: getfield 342	org/telegram/ui/Cells/ChatMessageCell:currentAccount	I
    //   17720: invokestatic 396	org/telegram/messenger/DownloadController:getInstance	(I)Lorg/telegram/messenger/DownloadController;
    //   17723: aload_0
    //   17724: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   17727: invokevirtual 3528	org/telegram/messenger/DownloadController:canDownloadMedia	(Lorg/telegram/messenger/MessageObject;)Z
    //   17730: istore_3
    //   17731: aload_1
    //   17732: invokevirtual 1426	org/telegram/messenger/MessageObject:isSending	()Z
    //   17735: ifne +233 -> 17968
    //   17738: iload 8
    //   17740: ifne +21 -> 17761
    //   17743: aload_0
    //   17744: getfield 342	org/telegram/ui/Cells/ChatMessageCell:currentAccount	I
    //   17747: invokestatic 1378	org/telegram/messenger/FileLoader:getInstance	(I)Lorg/telegram/messenger/FileLoader;
    //   17750: aload_2
    //   17751: invokevirtual 3531	org/telegram/messenger/FileLoader:isLoadingFile	(Ljava/lang/String;)Z
    //   17754: ifne +7 -> 17761
    //   17757: iload_3
    //   17758: ifeq +210 -> 17968
    //   17761: iload 8
    //   17763: iconst_1
    //   17764: if_icmpne +123 -> 17887
    //   17767: aload_0
    //   17768: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   17771: astore 20
    //   17773: aload_1
    //   17774: invokevirtual 1088	org/telegram/messenger/MessageObject:isSendError	()Z
    //   17777: ifeq +93 -> 17870
    //   17780: aconst_null
    //   17781: astore_2
    //   17782: aload_0
    //   17783: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   17786: ifnull +95 -> 17881
    //   17789: aload_0
    //   17790: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   17793: getfield 1292	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   17796: astore 10
    //   17798: aload 20
    //   17800: aconst_null
    //   17801: aload_2
    //   17802: aconst_null
    //   17803: aconst_null
    //   17804: aload 10
    //   17806: aload_0
    //   17807: getfield 1406	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilterThumb	Ljava/lang/String;
    //   17810: iconst_0
    //   17811: aconst_null
    //   17812: iconst_0
    //   17813: invokevirtual 1362	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;I)V
    //   17816: iload 9
    //   17818: istore 30
    //   17820: iload 11
    //   17822: istore 9
    //   17824: iload 14
    //   17826: istore 8
    //   17828: goto -6258 -> 11570
    //   17831: aload_1
    //   17832: getfield 2747	org/telegram/messenger/MessageObject:mediaExists	Z
    //   17835: ifeq -137 -> 17698
    //   17838: iconst_2
    //   17839: istore 8
    //   17841: goto -143 -> 17698
    //   17844: aload_1
    //   17845: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   17848: iconst_5
    //   17849: if_icmpne -118 -> 17731
    //   17852: aload_0
    //   17853: getfield 342	org/telegram/ui/Cells/ChatMessageCell:currentAccount	I
    //   17856: invokestatic 396	org/telegram/messenger/DownloadController:getInstance	(I)Lorg/telegram/messenger/DownloadController;
    //   17859: aload_0
    //   17860: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   17863: invokevirtual 3528	org/telegram/messenger/DownloadController:canDownloadMedia	(Lorg/telegram/messenger/MessageObject;)Z
    //   17866: istore_3
    //   17867: goto -136 -> 17731
    //   17870: aload_1
    //   17871: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   17874: getfield 3633	org/telegram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   17877: astore_2
    //   17878: goto -96 -> 17782
    //   17881: aconst_null
    //   17882: astore 10
    //   17884: goto -86 -> 17798
    //   17887: aload_0
    //   17888: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   17891: astore 20
    //   17893: aload_1
    //   17894: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   17897: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   17900: getfield 1150	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   17903: astore 10
    //   17905: aload_0
    //   17906: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   17909: ifnull +54 -> 17963
    //   17912: aload_0
    //   17913: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   17916: getfield 1292	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   17919: astore_2
    //   17920: aload 20
    //   17922: aload 10
    //   17924: aconst_null
    //   17925: aload_2
    //   17926: aload_0
    //   17927: getfield 1406	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilterThumb	Ljava/lang/String;
    //   17930: aload_1
    //   17931: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   17934: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   17937: getfield 1150	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   17940: getfield 1259	org/telegram/tgnet/TLRPC$Document:size	I
    //   17943: aconst_null
    //   17944: iconst_0
    //   17945: invokevirtual 1413	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;I)V
    //   17948: iload 9
    //   17950: istore 30
    //   17952: iload 11
    //   17954: istore 9
    //   17956: iload 14
    //   17958: istore 8
    //   17960: goto -6390 -> 11570
    //   17963: aconst_null
    //   17964: astore_2
    //   17965: goto -45 -> 17920
    //   17968: aload_0
    //   17969: iconst_1
    //   17970: putfield 2364	org/telegram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   17973: aload_0
    //   17974: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   17977: astore 10
    //   17979: aload_0
    //   17980: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   17983: ifnull +41 -> 18024
    //   17986: aload_0
    //   17987: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   17990: getfield 1292	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   17993: astore_2
    //   17994: aload 10
    //   17996: aconst_null
    //   17997: aconst_null
    //   17998: aload_2
    //   17999: aload_0
    //   18000: getfield 1406	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilterThumb	Ljava/lang/String;
    //   18003: iconst_0
    //   18004: aconst_null
    //   18005: iconst_0
    //   18006: invokevirtual 1413	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;I)V
    //   18009: iload 9
    //   18011: istore 30
    //   18013: iload 11
    //   18015: istore 9
    //   18017: iload 14
    //   18019: istore 8
    //   18021: goto -6451 -> 11570
    //   18024: aconst_null
    //   18025: astore_2
    //   18026: goto -32 -> 17994
    //   18029: aload_0
    //   18030: getfield 406	org/telegram/ui/Cells/ChatMessageCell:photoImage	Lorg/telegram/messenger/ImageReceiver;
    //   18033: astore 10
    //   18035: aload_0
    //   18036: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   18039: ifnull +59 -> 18098
    //   18042: aload_0
    //   18043: getfield 1345	org/telegram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/telegram/tgnet/TLRPC$PhotoSize;
    //   18046: getfield 1292	org/telegram/tgnet/TLRPC$PhotoSize:location	Lorg/telegram/tgnet/TLRPC$FileLocation;
    //   18049: astore_2
    //   18050: aload_0
    //   18051: getfield 1406	org/telegram/ui/Cells/ChatMessageCell:currentPhotoFilterThumb	Ljava/lang/String;
    //   18054: astore 20
    //   18056: aload_0
    //   18057: getfield 467	org/telegram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/telegram/messenger/MessageObject;
    //   18060: invokevirtual 1410	org/telegram/messenger/MessageObject:shouldEncryptPhotoOrVideo	()Z
    //   18063: ifeq +40 -> 18103
    //   18066: iconst_2
    //   18067: istore 8
    //   18069: aload 10
    //   18071: aconst_null
    //   18072: aconst_null
    //   18073: aload_2
    //   18074: aload 20
    //   18076: iconst_0
    //   18077: aconst_null
    //   18078: iload 8
    //   18080: invokevirtual 1413	org/telegram/messenger/ImageReceiver:setImage	(Lorg/telegram/tgnet/TLObject;Ljava/lang/String;Lorg/telegram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;I)V
    //   18083: iload 9
    //   18085: istore 30
    //   18087: iload 11
    //   18089: istore 9
    //   18091: iload 14
    //   18093: istore 8
    //   18095: goto -6525 -> 11570
    //   18098: aconst_null
    //   18099: astore_2
    //   18100: goto -50 -> 18050
    //   18103: iconst_0
    //   18104: istore 8
    //   18106: goto -37 -> 18069
    //   18109: aload_0
    //   18110: getfield 2563	org/telegram/ui/Cells/ChatMessageCell:drawNameLayout	Z
    //   18113: ifeq -6484 -> 11629
    //   18116: aload_1
    //   18117: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   18120: getfield 1067	org/telegram/tgnet/TLRPC$Message:reply_to_msg_id	I
    //   18123: ifne -6494 -> 11629
    //   18126: aload_0
    //   18127: aload_0
    //   18128: getfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   18131: ldc_w 616
    //   18134: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   18137: iadd
    //   18138: putfield 563	org/telegram/ui/Cells/ChatMessageCell:namesOffset	I
    //   18141: goto -6512 -> 11629
    //   18144: new 350	android/text/StaticLayout
    //   18147: astore_2
    //   18148: aload_2
    //   18149: aload_1
    //   18150: getfield 3184	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   18153: getstatic 2970	org/telegram/ui/ActionBar/Theme:chat_msgTextPaint	Landroid/text/TextPaint;
    //   18156: iload 8
    //   18158: getstatic 946	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   18161: fconst_1
    //   18162: fconst_0
    //   18163: iconst_0
    //   18164: invokespecial 949	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   18167: aload_0
    //   18168: aload_2
    //   18169: putfield 635	org/telegram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   18172: goto -12144 -> 6028
    //   18175: astore_2
    //   18176: aload_2
    //   18177: invokestatic 714	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   18180: iload 22
    //   18182: istore 9
    //   18184: goto -11985 -> 6199
    //   18187: iconst_0
    //   18188: istore 8
    //   18190: goto -12121 -> 6069
    //   18193: iload 22
    //   18195: istore 9
    //   18197: aload_0
    //   18198: getfield 1184	org/telegram/ui/Cells/ChatMessageCell:widthBeforeNewTimeLine	I
    //   18201: iconst_m1
    //   18202: if_icmpeq -12003 -> 6199
    //   18205: iload 22
    //   18207: istore 9
    //   18209: aload_0
    //   18210: getfield 1187	org/telegram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   18213: aload_0
    //   18214: getfield 1184	org/telegram/ui/Cells/ChatMessageCell:widthBeforeNewTimeLine	I
    //   18217: isub
    //   18218: aload_0
    //   18219: getfield 493	org/telegram/ui/Cells/ChatMessageCell:timeWidth	I
    //   18222: if_icmpge -12023 -> 6199
    //   18225: aload_0
    //   18226: aload_0
    //   18227: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   18230: ldc_w 478
    //   18233: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   18236: iadd
    //   18237: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   18240: iload 22
    //   18242: istore 9
    //   18244: goto -12045 -> 6199
    //   18247: iconst_0
    //   18248: istore_3
    //   18249: goto -11899 -> 6350
    //   18252: astore 10
    //   18254: aload 10
    //   18256: invokestatic 714	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   18259: goto -11864 -> 6395
    //   18262: aload_0
    //   18263: aload_0
    //   18264: getfield 738	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   18267: iload 6
    //   18269: ineg
    //   18270: invokestatic 486	java/lang/Math:max	(II)I
    //   18273: putfield 738	org/telegram/ui/Cells/ChatMessageCell:descriptionX	I
    //   18276: goto -11733 -> 6543
    //   18279: astore_2
    //   18280: aload_2
    //   18281: invokestatic 714	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   18284: aload_0
    //   18285: aload_0
    //   18286: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   18289: ldc_w 494
    //   18292: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   18295: iadd
    //   18296: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   18299: iload 9
    //   18301: ifeq +39 -> 18340
    //   18304: aload_0
    //   18305: aload_0
    //   18306: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   18309: ldc_w 478
    //   18312: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   18315: isub
    //   18316: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   18319: iload 9
    //   18321: iconst_2
    //   18322: if_icmpne +18 -> 18340
    //   18325: aload_0
    //   18326: aload_0
    //   18327: getfield 647	org/telegram/ui/Cells/ChatMessageCell:captionHeight	I
    //   18330: ldc_w 478
    //   18333: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   18336: isub
    //   18337: putfield 647	org/telegram/ui/Cells/ChatMessageCell:captionHeight	I
    //   18340: aload_0
    //   18341: getfield 328	org/telegram/ui/Cells/ChatMessageCell:botButtons	Ljava/util/ArrayList;
    //   18344: invokevirtual 2531	java/util/ArrayList:clear	()V
    //   18347: iload 5
    //   18349: ifeq +22 -> 18371
    //   18352: aload_0
    //   18353: getfield 333	org/telegram/ui/Cells/ChatMessageCell:botButtonsByData	Ljava/util/HashMap;
    //   18356: invokevirtual 3660	java/util/HashMap:clear	()V
    //   18359: aload_0
    //   18360: getfield 335	org/telegram/ui/Cells/ChatMessageCell:botButtonsByPosition	Ljava/util/HashMap;
    //   18363: invokevirtual 3660	java/util/HashMap:clear	()V
    //   18366: aload_0
    //   18367: aconst_null
    //   18368: putfield 3662	org/telegram/ui/Cells/ChatMessageCell:botButtonsLayout	Ljava/lang/String;
    //   18371: aload_0
    //   18372: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   18375: ifnonnull +900 -> 19275
    //   18378: aload_1
    //   18379: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   18382: getfield 3666	org/telegram/tgnet/TLRPC$Message:reply_markup	Lorg/telegram/tgnet/TLRPC$ReplyMarkup;
    //   18385: instanceof 3668
    //   18388: ifeq +887 -> 19275
    //   18391: aload_1
    //   18392: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   18395: getfield 3666	org/telegram/tgnet/TLRPC$Message:reply_markup	Lorg/telegram/tgnet/TLRPC$ReplyMarkup;
    //   18398: getfield 3673	org/telegram/tgnet/TLRPC$ReplyMarkup:rows	Ljava/util/ArrayList;
    //   18401: invokevirtual 590	java/util/ArrayList:size	()I
    //   18404: istore 11
    //   18406: ldc_w 781
    //   18409: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   18412: iload 11
    //   18414: imul
    //   18415: fconst_1
    //   18416: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   18419: iadd
    //   18420: istore 9
    //   18422: aload_0
    //   18423: iload 9
    //   18425: putfield 3154	org/telegram/ui/Cells/ChatMessageCell:keyboardHeight	I
    //   18428: aload_0
    //   18429: iload 9
    //   18431: putfield 3112	org/telegram/ui/Cells/ChatMessageCell:substractBackgroundHeight	I
    //   18434: aload_0
    //   18435: aload_0
    //   18436: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   18439: putfield 586	org/telegram/ui/Cells/ChatMessageCell:widthForButtons	I
    //   18442: iconst_0
    //   18443: istore 8
    //   18445: aload_1
    //   18446: getfield 3676	org/telegram/messenger/MessageObject:wantedBotKeyboardWidth	I
    //   18449: aload_0
    //   18450: getfield 586	org/telegram/ui/Cells/ChatMessageCell:widthForButtons	I
    //   18453: if_icmple +74 -> 18527
    //   18456: aload_0
    //   18457: getfield 2040	org/telegram/ui/Cells/ChatMessageCell:isChat	Z
    //   18460: ifeq +192 -> 18652
    //   18463: aload_1
    //   18464: invokevirtual 2043	org/telegram/messenger/MessageObject:needDrawAvatar	()Z
    //   18467: ifeq +185 -> 18652
    //   18470: aload_1
    //   18471: invokevirtual 491	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   18474: ifne +178 -> 18652
    //   18477: ldc_w 3677
    //   18480: fstore 40
    //   18482: fload 40
    //   18484: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   18487: ineg
    //   18488: istore 9
    //   18490: invokestatic 2226	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   18493: ifeq +167 -> 18660
    //   18496: iload 9
    //   18498: invokestatic 2258	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   18501: iadd
    //   18502: istore 9
    //   18504: aload_0
    //   18505: aload_0
    //   18506: getfield 348	org/telegram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   18509: aload_1
    //   18510: getfield 3676	org/telegram/messenger/MessageObject:wantedBotKeyboardWidth	I
    //   18513: iload 9
    //   18515: invokestatic 1195	java/lang/Math:min	(II)I
    //   18518: invokestatic 486	java/lang/Math:max	(II)I
    //   18521: putfield 586	org/telegram/ui/Cells/ChatMessageCell:widthForButtons	I
    //   18524: iconst_1
    //   18525: istore 8
    //   18527: iconst_0
    //   18528: istore 9
    //   18530: new 330	java/util/HashMap
    //   18533: dup
    //   18534: aload_0
    //   18535: getfield 333	org/telegram/ui/Cells/ChatMessageCell:botButtonsByData	Ljava/util/HashMap;
    //   18538: invokespecial 3680	java/util/HashMap:<init>	(Ljava/util/Map;)V
    //   18541: astore 20
    //   18543: aload_1
    //   18544: getfield 3683	org/telegram/messenger/MessageObject:botButtonsLayout	Ljava/lang/StringBuilder;
    //   18547: ifnull +136 -> 18683
    //   18550: aload_0
    //   18551: getfield 3662	org/telegram/ui/Cells/ChatMessageCell:botButtonsLayout	Ljava/lang/String;
    //   18554: ifnull +129 -> 18683
    //   18557: aload_0
    //   18558: getfield 3662	org/telegram/ui/Cells/ChatMessageCell:botButtonsLayout	Ljava/lang/String;
    //   18561: aload_1
    //   18562: getfield 3683	org/telegram/messenger/MessageObject:botButtonsLayout	Ljava/lang/StringBuilder;
    //   18565: invokevirtual 1333	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   18568: invokevirtual 2357	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   18571: ifeq +112 -> 18683
    //   18574: new 330	java/util/HashMap
    //   18577: dup
    //   18578: aload_0
    //   18579: getfield 335	org/telegram/ui/Cells/ChatMessageCell:botButtonsByPosition	Ljava/util/HashMap;
    //   18582: invokespecial 3680	java/util/HashMap:<init>	(Ljava/util/Map;)V
    //   18585: astore_2
    //   18586: aload_0
    //   18587: getfield 333	org/telegram/ui/Cells/ChatMessageCell:botButtonsByData	Ljava/util/HashMap;
    //   18590: invokevirtual 3660	java/util/HashMap:clear	()V
    //   18593: iconst_0
    //   18594: istore 6
    //   18596: iload 6
    //   18598: iload 11
    //   18600: if_icmpge +599 -> 19199
    //   18603: aload_1
    //   18604: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   18607: getfield 3666	org/telegram/tgnet/TLRPC$Message:reply_markup	Lorg/telegram/tgnet/TLRPC$ReplyMarkup;
    //   18610: getfield 3673	org/telegram/tgnet/TLRPC$ReplyMarkup:rows	Ljava/util/ArrayList;
    //   18613: iload 6
    //   18615: invokevirtual 594	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   18618: checkcast 3685	org/telegram/tgnet/TLRPC$TL_keyboardButtonRow
    //   18621: astore 26
    //   18623: aload 26
    //   18625: getfield 3688	org/telegram/tgnet/TLRPC$TL_keyboardButtonRow:buttons	Ljava/util/ArrayList;
    //   18628: invokevirtual 590	java/util/ArrayList:size	()I
    //   18631: istore 21
    //   18633: iload 21
    //   18635: ifne +71 -> 18706
    //   18638: iload 9
    //   18640: istore 14
    //   18642: iinc 6 1
    //   18645: iload 14
    //   18647: istore 9
    //   18649: goto -53 -> 18596
    //   18652: ldc_w 587
    //   18655: fstore 40
    //   18657: goto -175 -> 18482
    //   18660: iload 9
    //   18662: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   18665: getfield 2253	android/graphics/Point:x	I
    //   18668: getstatic 2248	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   18671: getfield 2262	android/graphics/Point:y	I
    //   18674: invokestatic 1195	java/lang/Math:min	(II)I
    //   18677: iadd
    //   18678: istore 9
    //   18680: goto -176 -> 18504
    //   18683: aload_1
    //   18684: getfield 3683	org/telegram/messenger/MessageObject:botButtonsLayout	Ljava/lang/StringBuilder;
    //   18687: ifnull +14 -> 18701
    //   18690: aload_0
    //   18691: aload_1
    //   18692: getfield 3683	org/telegram/messenger/MessageObject:botButtonsLayout	Ljava/lang/StringBuilder;
    //   18695: invokevirtual 1333	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   18698: putfield 3662	org/telegram/ui/Cells/ChatMessageCell:botButtonsLayout	Ljava/lang/String;
    //   18701: aconst_null
    //   18702: astore_2
    //   18703: goto -117 -> 18586
    //   18706: aload_0
    //   18707: getfield 586	org/telegram/ui/Cells/ChatMessageCell:widthForButtons	I
    //   18710: istore 14
    //   18712: ldc_w 1775
    //   18715: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   18718: istore 22
    //   18720: iload 8
    //   18722: ifne +394 -> 19116
    //   18725: aload_0
    //   18726: getfield 615	org/telegram/ui/Cells/ChatMessageCell:mediaBackground	Z
    //   18729: ifeq +387 -> 19116
    //   18732: fconst_0
    //   18733: fstore 40
    //   18735: iload 14
    //   18737: iload 22
    //   18739: iload 21
    //   18741: iconst_1
    //   18742: isub
    //   18743: imul
    //   18744: isub
    //   18745: fload 40
    //   18747: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   18750: isub
    //   18751: fconst_2
    //   18752: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   18755: isub
    //   18756: iload 21
    //   18758: idiv
    //   18759: istore 22
    //   18761: iconst_0
    //   18762: istore 21
    //   18764: iload 9
    //   18766: istore 14
    //   18768: iload 21
    //   18770: aload 26
    //   18772: getfield 3688	org/telegram/tgnet/TLRPC$TL_keyboardButtonRow:buttons	Ljava/util/ArrayList;
    //   18775: invokevirtual 590	java/util/ArrayList:size	()I
    //   18778: if_icmpge -136 -> 18642
    //   18781: new 16	org/telegram/ui/Cells/ChatMessageCell$BotButton
    //   18784: dup
    //   18785: aload_0
    //   18786: aconst_null
    //   18787: invokespecial 3691	org/telegram/ui/Cells/ChatMessageCell$BotButton:<init>	(Lorg/telegram/ui/Cells/ChatMessageCell;Lorg/telegram/ui/Cells/ChatMessageCell$1;)V
    //   18790: astore 23
    //   18792: aload 23
    //   18794: aload 26
    //   18796: getfield 3688	org/telegram/tgnet/TLRPC$TL_keyboardButtonRow:buttons	Ljava/util/ArrayList;
    //   18799: iload 21
    //   18801: invokevirtual 594	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   18804: checkcast 3693	org/telegram/tgnet/TLRPC$KeyboardButton
    //   18807: invokestatic 3697	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$502	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;Lorg/telegram/tgnet/TLRPC$KeyboardButton;)Lorg/telegram/tgnet/TLRPC$KeyboardButton;
    //   18810: pop
    //   18811: aload 23
    //   18813: invokestatic 622	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$500	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;)Lorg/telegram/tgnet/TLRPC$KeyboardButton;
    //   18816: getfield 3700	org/telegram/tgnet/TLRPC$KeyboardButton:data	[B
    //   18819: invokestatic 3706	org/telegram/messenger/Utilities:bytesToHex	([B)Ljava/lang/String;
    //   18822: astore 25
    //   18824: new 1320	java/lang/StringBuilder
    //   18827: dup
    //   18828: invokespecial 1321	java/lang/StringBuilder:<init>	()V
    //   18831: iload 6
    //   18833: invokevirtual 3709	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   18836: ldc_w 2448
    //   18839: invokevirtual 1325	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   18842: iload 21
    //   18844: invokevirtual 3709	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   18847: invokevirtual 1333	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   18850: astore 27
    //   18852: aload_2
    //   18853: ifnull +271 -> 19124
    //   18856: aload_2
    //   18857: aload 27
    //   18859: invokevirtual 3284	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   18862: checkcast 16	org/telegram/ui/Cells/ChatMessageCell$BotButton
    //   18865: astore 10
    //   18867: aload 10
    //   18869: ifnull +270 -> 19139
    //   18872: aload 23
    //   18874: aload 10
    //   18876: invokestatic 2138	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$1100	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;)F
    //   18879: invokestatic 2164	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$1102	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;F)F
    //   18882: pop
    //   18883: aload 23
    //   18885: aload 10
    //   18887: invokestatic 2144	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$1200	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;)I
    //   18890: invokestatic 2159	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$1202	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;I)I
    //   18893: pop
    //   18894: aload 23
    //   18896: aload 10
    //   18898: invokestatic 2149	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$1300	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;)J
    //   18901: invokestatic 2168	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$1302	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;J)J
    //   18904: pop2
    //   18905: aload_0
    //   18906: getfield 333	org/telegram/ui/Cells/ChatMessageCell:botButtonsByData	Ljava/util/HashMap;
    //   18909: aload 25
    //   18911: aload 23
    //   18913: invokevirtual 3713	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   18916: pop
    //   18917: aload_0
    //   18918: getfield 335	org/telegram/ui/Cells/ChatMessageCell:botButtonsByPosition	Ljava/util/HashMap;
    //   18921: aload 27
    //   18923: aload 23
    //   18925: invokevirtual 3713	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   18928: pop
    //   18929: aload 23
    //   18931: ldc_w 1775
    //   18934: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   18937: iload 22
    //   18939: iadd
    //   18940: iload 21
    //   18942: imul
    //   18943: invokestatic 3716	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$702	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;I)I
    //   18946: pop
    //   18947: aload 23
    //   18949: ldc_w 781
    //   18952: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   18955: iload 6
    //   18957: imul
    //   18958: ldc_w 1775
    //   18961: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   18964: iadd
    //   18965: invokestatic 3719	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$602	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;I)I
    //   18968: pop
    //   18969: aload 23
    //   18971: iload 22
    //   18973: invokestatic 3722	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$802	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;I)I
    //   18976: pop
    //   18977: aload 23
    //   18979: ldc_w 1819
    //   18982: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   18985: invokestatic 3725	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$902	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;I)I
    //   18988: pop
    //   18989: aload 23
    //   18991: invokestatic 622	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$500	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;)Lorg/telegram/tgnet/TLRPC$KeyboardButton;
    //   18994: instanceof 2122
    //   18997: ifeq +154 -> 19151
    //   19000: aload_1
    //   19001: getfield 816	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   19004: getfield 822	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   19007: getfield 3443	org/telegram/tgnet/TLRPC$MessageMedia:flags	I
    //   19010: iconst_4
    //   19011: iand
    //   19012: ifeq +139 -> 19151
    //   19015: ldc_w 3445
    //   19018: ldc_w 3446
    //   19021: invokestatic 934	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   19024: astore 10
    //   19026: aload 23
    //   19028: new 350	android/text/StaticLayout
    //   19031: dup
    //   19032: aload 10
    //   19034: getstatic 3728	org/telegram/ui/ActionBar/Theme:chat_botButtonPaint	Landroid/text/TextPaint;
    //   19037: iload 22
    //   19039: ldc_w 587
    //   19042: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   19045: isub
    //   19046: getstatic 3731	android/text/Layout$Alignment:ALIGN_CENTER	Landroid/text/Layout$Alignment;
    //   19049: fconst_1
    //   19050: fconst_0
    //   19051: iconst_0
    //   19052: invokespecial 949	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   19055: invokestatic 3735	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$1402	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;Landroid/text/StaticLayout;)Landroid/text/StaticLayout;
    //   19058: pop
    //   19059: aload_0
    //   19060: getfield 328	org/telegram/ui/Cells/ChatMessageCell:botButtons	Ljava/util/ArrayList;
    //   19063: aload 23
    //   19065: invokevirtual 2523	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   19068: pop
    //   19069: iload 9
    //   19071: istore 14
    //   19073: iload 21
    //   19075: aload 26
    //   19077: getfield 3688	org/telegram/tgnet/TLRPC$TL_keyboardButtonRow:buttons	Ljava/util/ArrayList;
    //   19080: invokevirtual 590	java/util/ArrayList:size	()I
    //   19083: iconst_1
    //   19084: isub
    //   19085: if_icmpne +21 -> 19106
    //   19088: iload 9
    //   19090: aload 23
    //   19092: invokestatic 603	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$700	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;)I
    //   19095: aload 23
    //   19097: invokestatic 606	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$800	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;)I
    //   19100: iadd
    //   19101: invokestatic 486	java/lang/Math:max	(II)I
    //   19104: istore 14
    //   19106: iinc 21 1
    //   19109: iload 14
    //   19111: istore 9
    //   19113: goto -349 -> 18764
    //   19116: ldc_w 1705
    //   19119: fstore 40
    //   19121: goto -386 -> 18735
    //   19124: aload 20
    //   19126: aload 25
    //   19128: invokevirtual 3284	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   19131: checkcast 16	org/telegram/ui/Cells/ChatMessageCell$BotButton
    //   19134: astore 10
    //   19136: goto -269 -> 18867
    //   19139: aload 23
    //   19141: invokestatic 1617	java/lang/System:currentTimeMillis	()J
    //   19144: invokestatic 2168	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$1302	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;J)J
    //   19147: pop2
    //   19148: goto -243 -> 18905
    //   19151: aload 23
    //   19153: invokestatic 622	org/telegram/ui/Cells/ChatMessageCell$BotButton:access$500	(Lorg/telegram/ui/Cells/ChatMessageCell$BotButton;)Lorg/telegram/tgnet/TLRPC$KeyboardButton;
    //   19156: getfield 3738	org/telegram/tgnet/TLRPC$KeyboardButton:text	Ljava/lang/String;
    //   19159: getstatic 3728	org/telegram/ui/ActionBar/Theme:chat_botButtonPaint	Landroid/text/TextPaint;
    //   19162: invokevirtual 2674	android/text/TextPaint:getFontMetricsInt	()Landroid/graphics/Paint$FontMetricsInt;
    //   19165: ldc_w 1703
    //   19168: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   19171: iconst_0
    //   19172: invokestatic 2680	org/telegram/messenger/Emoji:replaceEmoji	(Ljava/lang/CharSequence;Landroid/graphics/Paint$FontMetricsInt;IZ)Ljava/lang/CharSequence;
    //   19175: getstatic 3728	org/telegram/ui/ActionBar/Theme:chat_botButtonPaint	Landroid/text/TextPaint;
    //   19178: iload 22
    //   19180: ldc_w 587
    //   19183: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   19186: isub
    //   19187: i2f
    //   19188: getstatic 1217	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   19191: invokestatic 1221	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   19194: astore 10
    //   19196: goto -170 -> 19026
    //   19199: aload_0
    //   19200: iload 9
    //   19202: putfield 586	org/telegram/ui/Cells/ChatMessageCell:widthForButtons	I
    //   19205: aload_0
    //   19206: getfield 1623	org/telegram/ui/Cells/ChatMessageCell:drawPinnedBottom	Z
    //   19209: ifeq +79 -> 19288
    //   19212: aload_0
    //   19213: getfield 1783	org/telegram/ui/Cells/ChatMessageCell:drawPinnedTop	Z
    //   19216: ifeq +72 -> 19288
    //   19219: aload_0
    //   19220: aload_0
    //   19221: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   19224: fconst_2
    //   19225: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   19228: isub
    //   19229: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   19232: aload_1
    //   19233: getfield 770	org/telegram/messenger/MessageObject:type	I
    //   19236: bipush 13
    //   19238: if_icmpne +26 -> 19264
    //   19241: aload_0
    //   19242: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   19245: ldc_w 3597
    //   19248: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   19251: if_icmpge +13 -> 19264
    //   19254: aload_0
    //   19255: ldc_w 3597
    //   19258: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   19261: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   19264: aload_0
    //   19265: invokespecial 3192	org/telegram/ui/Cells/ChatMessageCell:updateWaveform	()V
    //   19268: aload_0
    //   19269: iload 7
    //   19271: invokevirtual 2753	org/telegram/ui/Cells/ChatMessageCell:updateButtonState	(Z)V
    //   19274: return
    //   19275: aload_0
    //   19276: iconst_0
    //   19277: putfield 3112	org/telegram/ui/Cells/ChatMessageCell:substractBackgroundHeight	I
    //   19280: aload_0
    //   19281: iconst_0
    //   19282: putfield 3154	org/telegram/ui/Cells/ChatMessageCell:keyboardHeight	I
    //   19285: goto -80 -> 19205
    //   19288: aload_0
    //   19289: getfield 1623	org/telegram/ui/Cells/ChatMessageCell:drawPinnedBottom	Z
    //   19292: ifeq +19 -> 19311
    //   19295: aload_0
    //   19296: aload_0
    //   19297: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   19300: fconst_1
    //   19301: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   19304: isub
    //   19305: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   19308: goto -76 -> 19232
    //   19311: aload_0
    //   19312: getfield 1783	org/telegram/ui/Cells/ChatMessageCell:drawPinnedTop	Z
    //   19315: ifeq -83 -> 19232
    //   19318: aload_0
    //   19319: getfield 953	org/telegram/ui/Cells/ChatMessageCell:pinnedBottom	Z
    //   19322: ifeq -90 -> 19232
    //   19325: aload_0
    //   19326: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   19329: ifnull -97 -> 19232
    //   19332: aload_0
    //   19333: getfield 962	org/telegram/ui/Cells/ChatMessageCell:currentPosition	Lorg/telegram/messenger/MessageObject$GroupedMessagePosition;
    //   19336: getfield 3641	org/telegram/messenger/MessageObject$GroupedMessagePosition:siblingHeights	[F
    //   19339: ifnonnull -107 -> 19232
    //   19342: aload_0
    //   19343: aload_0
    //   19344: getfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   19347: fconst_1
    //   19348: invokestatic 373	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   19351: isub
    //   19352: putfield 477	org/telegram/ui/Cells/ChatMessageCell:totalHeight	I
    //   19355: goto -123 -> 19232
    //   19358: astore 25
    //   19360: iload 21
    //   19362: istore 8
    //   19364: goto -14718 -> 4646
    //   19367: astore 33
    //   19369: iconst_3
    //   19370: istore 8
    //   19372: iload 35
    //   19374: istore 22
    //   19376: goto -15431 -> 3945
    //   19379: goto -9237 -> 10142
    //   19382: goto -9789 -> 9593
    //   19385: goto -11059 -> 8326
    //   19388: iload 9
    //   19390: istore 6
    //   19392: goto -14570 -> 4822
    //   19395: iload 21
    //   19397: istore 6
    //   19399: iload 9
    //   19401: istore 21
    //   19403: iload 22
    //   19405: istore 35
    //   19407: goto -15157 -> 4250
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19410	0	this	ChatMessageCell
    //   0	19410	1	paramMessageObject	MessageObject
    //   0	19410	2	paramGroupedMessages	MessageObject.GroupedMessages
    //   0	19410	3	paramBoolean1	boolean
    //   0	19410	4	paramBoolean2	boolean
    //   54	18294	5	i	int
    //   72	19326	6	j	int
    //   97	19173	7	bool	boolean
    //   108	19263	8	k	int
    //   112	19288	9	m	int
    //   155	17915	10	localObject1	Object
    //   18252	3	10	localException1	Exception
    //   18865	330	10	localObject2	Object
    //   843	17758	11	n	int
    //   846	16433	12	i1	int
    //   849	10168	13	i2	int
    //   978	18132	14	i3	int
    //   1003	15281	15	d1	double
    //   1018	15283	17	d2	double
    //   1064	16211	19	i4	int
    //   1328	17797	20	localObject3	Object
    //   1675	17727	21	i5	int
    //   1678	17726	22	i6	int
    //   1790	17362	23	localObject4	Object
    //   1796	2268	24	localObject5	Object
    //   1802	2765	25	str1	String
    //   4640	7	25	localException2	Exception
    //   18822	305	25	str2	String
    //   19358	1	25	localException3	Exception
    //   1808	2447	26	str3	String
    //   4710	3	26	localException4	Exception
    //   6887	12189	26	localObject6	Object
    //   1814	17108	27	localObject7	Object
    //   1832	14714	28	i7	int
    //   2035	15231	29	i8	int
    //   2038	16048	30	i9	int
    //   2044	15076	31	i10	int
    //   2051	4807	32	i11	int
    //   2125	43	33	localStaticLayout	StaticLayout
    //   3846	3	33	localException5	Exception
    //   3935	11	33	localException6	Exception
    //   19367	1	33	localException7	Exception
    //   2357	13915	34	i12	int
    //   2360	17046	35	i13	int
    //   2363	14441	36	i14	int
    //   2606	4078	37	i15	int
    //   2695	13217	38	i16	int
    //   6116	10682	39	f1	float
    //   6134	12986	40	f2	float
    //   12048	4239	41	d3	double
    //   12560	264	43	l	long
    // Exception table:
    //   from	to	target	type
    //   2097	2114	3846	java/lang/Exception
    //   2122	2127	3846	java/lang/Exception
    //   2135	2158	3846	java/lang/Exception
    //   2166	2172	3846	java/lang/Exception
    //   2180	2193	3846	java/lang/Exception
    //   2203	2208	3846	java/lang/Exception
    //   2216	2234	3846	java/lang/Exception
    //   2242	2253	3846	java/lang/Exception
    //   2261	2272	3846	java/lang/Exception
    //   2286	2295	3846	java/lang/Exception
    //   2303	2309	3846	java/lang/Exception
    //   2317	2329	3846	java/lang/Exception
    //   2337	2348	3846	java/lang/Exception
    //   2489	2507	3935	java/lang/Exception
    //   2519	2530	3935	java/lang/Exception
    //   2542	2553	3935	java/lang/Exception
    //   2572	2584	3935	java/lang/Exception
    //   2596	2608	3935	java/lang/Exception
    //   2628	2638	3935	java/lang/Exception
    //   2650	2657	3935	java/lang/Exception
    //   2674	2686	3935	java/lang/Exception
    //   2718	2725	3935	java/lang/Exception
    //   2737	2748	3935	java/lang/Exception
    //   2760	2772	3935	java/lang/Exception
    //   2784	2796	3935	java/lang/Exception
    //   3918	3932	3935	java/lang/Exception
    //   4527	4542	3935	java/lang/Exception
    //   3988	4021	4640	java/lang/Exception
    //   4027	4034	4640	java/lang/Exception
    //   4039	4068	4640	java/lang/Exception
    //   4565	4600	4640	java/lang/Exception
    //   4263	4268	4710	java/lang/Exception
    //   4272	4279	4710	java/lang/Exception
    //   4283	4290	4710	java/lang/Exception
    //   4294	4307	4710	java/lang/Exception
    //   4311	4324	4710	java/lang/Exception
    //   4337	4344	4710	java/lang/Exception
    //   4348	4381	4710	java/lang/Exception
    //   4389	4407	4710	java/lang/Exception
    //   4411	4422	4710	java/lang/Exception
    //   4426	4437	4710	java/lang/Exception
    //   4447	4459	4710	java/lang/Exception
    //   4463	4479	4710	java/lang/Exception
    //   4491	4498	4710	java/lang/Exception
    //   4502	4509	4710	java/lang/Exception
    //   4674	4703	4710	java/lang/Exception
    //   6553	6567	4710	java/lang/Exception
    //   6574	6583	4710	java/lang/Exception
    //   6594	6606	4710	java/lang/Exception
    //   6610	6626	4710	java/lang/Exception
    //   6635	6642	4710	java/lang/Exception
    //   6646	6651	4710	java/lang/Exception
    //   6696	6703	4710	java/lang/Exception
    //   6707	6718	4710	java/lang/Exception
    //   6741	6758	4710	java/lang/Exception
    //   6767	6784	4710	java/lang/Exception
    //   6795	6807	4710	java/lang/Exception
    //   6833	6854	4710	java/lang/Exception
    //   16572	16580	17249	java/lang/Exception
    //   16584	16627	17249	java/lang/Exception
    //   16639	16649	17249	java/lang/Exception
    //   16653	16659	17249	java/lang/Exception
    //   16663	16674	17249	java/lang/Exception
    //   16678	16693	17249	java/lang/Exception
    //   16697	16704	17249	java/lang/Exception
    //   16708	16721	17249	java/lang/Exception
    //   16725	16733	17249	java/lang/Exception
    //   16737	16755	17249	java/lang/Exception
    //   16759	16777	17249	java/lang/Exception
    //   16789	16810	17249	java/lang/Exception
    //   16814	16825	17249	java/lang/Exception
    //   16829	16844	17249	java/lang/Exception
    //   17210	17214	17249	java/lang/Exception
    //   17218	17237	17249	java/lang/Exception
    //   17241	17246	17249	java/lang/Exception
    //   17269	17274	17249	java/lang/Exception
    //   5945	6028	18175	java/lang/Exception
    //   6032	6069	18175	java/lang/Exception
    //   6069	6136	18175	java/lang/Exception
    //   6140	6196	18175	java/lang/Exception
    //   18144	18172	18175	java/lang/Exception
    //   6274	6348	18252	java/lang/Exception
    //   6350	6395	18252	java/lang/Exception
    //   6395	6420	18279	java/lang/Exception
    //   6420	6493	18279	java/lang/Exception
    //   6496	6524	18279	java/lang/Exception
    //   6529	6543	18279	java/lang/Exception
    //   18262	18276	18279	java/lang/Exception
    //   4080	4098	19358	java/lang/Exception
    //   4106	4117	19358	java/lang/Exception
    //   4125	4136	19358	java/lang/Exception
    //   4144	4155	19358	java/lang/Exception
    //   4163	4170	19358	java/lang/Exception
    //   4183	4195	19358	java/lang/Exception
    //   4206	4218	19358	java/lang/Exception
    //   4226	4238	19358	java/lang/Exception
    //   4618	4632	19358	java/lang/Exception
    //   2389	2429	19367	java/lang/Exception
    //   2432	2439	19367	java/lang/Exception
    //   2444	2474	19367	java/lang/Exception
    //   3863	3897	19367	java/lang/Exception
  }
  
  public void setPressed(boolean paramBoolean)
  {
    super.setPressed(paramBoolean);
    updateRadialProgressBackground();
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
    for (;;)
    {
      return;
      int i = paramInt1 - this.textY;
      int j = -1;
      int k = -1;
      int m = 0;
      int n = 0;
      paramInt1 = 0;
      label83:
      float f;
      int i1;
      int i2;
      if ((paramInt1 >= this.currentMessageObject.textLayoutBlocks.size()) || (((MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(paramInt1)).textYOffset > i))
      {
        paramInt1 = n;
        n = j;
        if (paramInt1 >= this.currentMessageObject.textLayoutBlocks.size()) {
          break label217;
        }
        MessageObject.TextLayoutBlock localTextLayoutBlock = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(paramInt1);
        f = localTextLayoutBlock.textYOffset;
        if (!intersect(f, localTextLayoutBlock.height + f, i, i + paramInt2)) {
          break label197;
        }
        k = n;
        if (n == -1) {
          k = paramInt1;
        }
        j = paramInt1;
        i1 = m + 1;
        i2 = k;
      }
      label197:
      do
      {
        paramInt1++;
        m = i1;
        n = i2;
        k = j;
        break label83;
        n = paramInt1;
        paramInt1++;
        break;
        i1 = m;
        i2 = n;
        j = k;
      } while (f <= i);
      label217:
      if ((this.lastVisibleBlockNum != k) || (this.firstVisibleBlockNum != n) || (this.totalVisibleBlocksCount != m))
      {
        this.lastVisibleBlockNum = k;
        this.firstVisibleBlockNum = n;
        this.totalVisibleBlocksCount = m;
        invalidate();
      }
    }
  }
  
  public void updateButtonState(boolean paramBoolean)
  {
    this.drawRadialCheckBackground = false;
    Object localObject1 = null;
    boolean bool1 = false;
    label44:
    boolean bool2;
    if (this.currentMessageObject.type == 1)
    {
      if (this.currentPhotoObject == null) {
        return;
      }
      localObject1 = FileLoader.getAttachFileName(this.currentPhotoObject);
      bool1 = this.currentMessageObject.mediaExists;
      bool2 = bool1;
      if (SharedConfig.streamMedia)
      {
        bool2 = bool1;
        if ((int)this.currentMessageObject.getDialogId() != 0)
        {
          bool2 = bool1;
          if (!this.currentMessageObject.isSecretMedia()) {
            if (this.documentAttachType != 5)
            {
              bool2 = bool1;
              if (this.documentAttachType == 4)
              {
                bool2 = bool1;
                if (!this.currentMessageObject.canStreamVideo()) {}
              }
            }
            else
            {
              if (!bool1) {
                break label380;
              }
            }
          }
        }
      }
    }
    label380:
    for (int i = 1;; i = 2)
    {
      this.hasMiniProgress = i;
      bool2 = true;
      if (!TextUtils.isEmpty((CharSequence)localObject1)) {
        break label386;
      }
      this.radialProgress.setBackground(null, false, false);
      this.radialProgress.setMiniBackground(null, false, false);
      break;
      if ((this.currentMessageObject.type == 8) || (this.currentMessageObject.type == 5) || (this.documentAttachType == 7) || (this.documentAttachType == 4) || (this.currentMessageObject.type == 9) || (this.documentAttachType == 3) || (this.documentAttachType == 5))
      {
        if (this.currentMessageObject.useCustomPhoto)
        {
          this.buttonState = 1;
          this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
          break;
        }
        if (this.currentMessageObject.attachPathExists)
        {
          localObject1 = this.currentMessageObject.messageOwner.attachPath;
          bool1 = true;
          break label44;
        }
        if ((this.currentMessageObject.isSendError()) && (this.documentAttachType != 3) && (this.documentAttachType != 5)) {
          break label44;
        }
        localObject1 = this.currentMessageObject.getFileName();
        bool1 = this.currentMessageObject.mediaExists;
        break label44;
      }
      if (this.documentAttachType != 0)
      {
        localObject1 = FileLoader.getAttachFileName(this.documentAttach);
        bool1 = this.currentMessageObject.mediaExists;
        break label44;
      }
      if (this.currentPhotoObject == null) {
        break label44;
      }
      localObject1 = FileLoader.getAttachFileName(this.currentPhotoObject);
      bool1 = this.currentMessageObject.mediaExists;
      break label44;
    }
    label386:
    label421:
    Object localObject2;
    if ((this.currentMessageObject.messageOwner.params != null) && (this.currentMessageObject.messageOwner.params.containsKey("query_id")))
    {
      i = 1;
      if ((this.documentAttachType != 3) && (this.documentAttachType != 5)) {
        break label1106;
      }
      if (((!this.currentMessageObject.isOut()) || (!this.currentMessageObject.isSending())) && ((!this.currentMessageObject.isSendError()) || (i == 0))) {
        break label664;
      }
      DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(this.currentMessageObject.messageOwner.attachPath, this.currentMessageObject, this);
      this.buttonState = 4;
      localObject2 = this.radialProgress;
      localObject1 = getDrawableForCurrentState();
      if (i != 0) {
        break label641;
      }
      bool1 = true;
      label520:
      ((RadialProgress)localObject2).setBackground((Drawable)localObject1, bool1, paramBoolean);
      if (i != 0) {
        break label652;
      }
      localObject2 = ImageLoader.getInstance().getFileProgress(this.currentMessageObject.messageOwner.attachPath);
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = localObject2;
        if (SendMessagesHelper.getInstance(this.currentAccount).isSendingMessage(this.currentMessageObject.getId())) {
          localObject1 = Float.valueOf(1.0F);
        }
      }
      localObject2 = this.radialProgress;
      if (localObject1 == null) {
        break label646;
      }
      f = ((Float)localObject1).floatValue();
      label603:
      ((RadialProgress)localObject2).setProgress(f, false);
      label611:
      updatePlayingMessageProgress();
    }
    for (;;)
    {
      label615:
      if (this.hasMiniProgress == 0)
      {
        this.radialProgress.setMiniBackground(null, false, paramBoolean);
        break;
        i = 0;
        break label421;
        label641:
        bool1 = false;
        break label520;
        label646:
        f = 0.0F;
        break label603;
        label652:
        this.radialProgress.setProgress(0.0F, false);
        break label611;
        label664:
        if (this.hasMiniProgress != 0)
        {
          RadialProgress localRadialProgress = this.radialProgress;
          if (this.currentMessageObject.isOutOwner())
          {
            localObject2 = "chat_outLoader";
            label692:
            localRadialProgress.setMiniProgressBackgroundColor(Theme.getColor((String)localObject2));
            bool1 = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
            if ((bool1) && ((!bool1) || (!MediaController.getInstance().isMessagePaused()))) {
              break label812;
            }
            this.buttonState = 0;
            label735:
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
            if (this.hasMiniProgress != 1) {
              break label820;
            }
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            this.miniButtonState = -1;
            label772:
            localObject2 = this.radialProgress;
            localObject1 = getMiniDrawableForCurrentState();
            if (this.miniButtonState != 1) {
              break label911;
            }
          }
          label812:
          label820:
          label911:
          for (bool1 = true;; bool1 = false)
          {
            ((RadialProgress)localObject2).setMiniBackground((Drawable)localObject1, bool1, paramBoolean);
            break;
            localObject2 = "chat_inLoader";
            break label692;
            this.buttonState = 1;
            break label735;
            DownloadController.getInstance(this.currentAccount).addLoadingFileObserver((String)localObject1, this.currentMessageObject, this);
            if (!FileLoader.getInstance(this.currentAccount).isLoadingFile((String)localObject1))
            {
              this.radialProgress.setProgress(0.0F, paramBoolean);
              this.miniButtonState = 0;
              break label772;
            }
            this.miniButtonState = 1;
            localObject1 = ImageLoader.getInstance().getFileProgress((String)localObject1);
            if (localObject1 != null)
            {
              this.radialProgress.setProgress(((Float)localObject1).floatValue(), paramBoolean);
              break label772;
            }
            this.radialProgress.setProgress(0.0F, paramBoolean);
            break label772;
          }
        }
        if (bool2)
        {
          DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
          bool1 = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
          if ((!bool1) || ((bool1) && (MediaController.getInstance().isMessagePaused()))) {}
          for (this.buttonState = 0;; this.buttonState = 1)
          {
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
            break;
          }
        }
        DownloadController.getInstance(this.currentAccount).addLoadingFileObserver((String)localObject1, this.currentMessageObject, this);
        if (!FileLoader.getInstance(this.currentAccount).isLoadingFile((String)localObject1))
        {
          this.buttonState = 2;
          this.radialProgress.setProgress(0.0F, paramBoolean);
          this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
          break label611;
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
        label1106:
        if ((this.currentMessageObject.type == 0) && (this.documentAttachType != 1) && (this.documentAttachType != 4))
        {
          if ((this.currentPhotoObject == null) || (!this.drawImageButton)) {
            break;
          }
          if (!bool2)
          {
            DownloadController.getInstance(this.currentAccount).addLoadingFileObserver((String)localObject1, this.currentMessageObject, this);
            f = 0.0F;
            bool1 = false;
            if (!FileLoader.getInstance(this.currentAccount).isLoadingFile((String)localObject1))
            {
              if ((!this.cancelLoading) && (((this.documentAttachType == 0) && (DownloadController.getInstance(this.currentAccount).canDownloadMedia(this.currentMessageObject))) || ((this.documentAttachType == 2) && (MessageObject.isNewGifDocument(this.documentAttach)) && (DownloadController.getInstance(this.currentAccount).canDownloadMedia(this.currentMessageObject))))) {
                bool1 = true;
              }
              for (this.buttonState = 1;; this.buttonState = 0)
              {
                this.radialProgress.setProgress(f, false);
                this.radialProgress.setBackground(getDrawableForCurrentState(), bool1, paramBoolean);
                invalidate();
                break;
              }
            }
            bool1 = true;
            this.buttonState = 1;
            localObject1 = ImageLoader.getInstance().getFileProgress((String)localObject1);
            if (localObject1 != null) {}
            for (f = ((Float)localObject1).floatValue();; f = 0.0F) {
              break;
            }
          }
          DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
          if ((this.documentAttachType == 2) && (!this.photoImage.isAllowStartAnimation())) {}
          for (this.buttonState = 2;; this.buttonState = -1)
          {
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
            invalidate();
            break;
          }
        }
        if ((this.currentMessageObject.isOut()) && (this.currentMessageObject.isSending()))
        {
          if ((this.currentMessageObject.messageOwner.attachPath != null) && (this.currentMessageObject.messageOwner.attachPath.length() > 0))
          {
            DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(this.currentMessageObject.messageOwner.attachPath, this.currentMessageObject, this);
            if ((this.currentMessageObject.messageOwner.attachPath == null) || (!this.currentMessageObject.messageOwner.attachPath.startsWith("http")))
            {
              bool1 = true;
              label1501:
              localObject1 = this.currentMessageObject.messageOwner.params;
              if ((this.currentMessageObject.messageOwner.message == null) || (localObject1 == null) || ((!((HashMap)localObject1).containsKey("url")) && (!((HashMap)localObject1).containsKey("bot")))) {
                break label1688;
              }
              bool1 = false;
              this.buttonState = -1;
              label1556:
              bool2 = SendMessagesHelper.getInstance(this.currentAccount).isSendingMessage(this.currentMessageObject.getId());
              if ((this.currentPosition == null) || (!bool2) || (this.buttonState != 1)) {
                break label1696;
              }
              this.drawRadialCheckBackground = true;
              this.radialProgress.setCheckBackground(false, paramBoolean);
              label1609:
              if (!bool1) {
                break label1718;
              }
              localObject2 = ImageLoader.getInstance().getFileProgress(this.currentMessageObject.messageOwner.attachPath);
              localObject1 = localObject2;
              if (localObject2 == null)
              {
                localObject1 = localObject2;
                if (bool2) {
                  localObject1 = Float.valueOf(1.0F);
                }
              }
              localObject2 = this.radialProgress;
              if (localObject1 == null) {
                break label1712;
              }
              f = ((Float)localObject1).floatValue();
              label1668:
              ((RadialProgress)localObject2).setProgress(f, false);
            }
            for (;;)
            {
              invalidate();
              break;
              bool1 = false;
              break label1501;
              label1688:
              this.buttonState = 1;
              break label1556;
              label1696:
              this.radialProgress.setBackground(getDrawableForCurrentState(), bool1, paramBoolean);
              break label1609;
              label1712:
              f = 0.0F;
              break label1668;
              label1718:
              this.radialProgress.setProgress(0.0F, false);
            }
          }
        }
        else
        {
          if ((this.currentMessageObject.messageOwner.attachPath != null) && (this.currentMessageObject.messageOwner.attachPath.length() != 0)) {
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
          }
          if (this.hasMiniProgress != 0)
          {
            this.radialProgress.setMiniProgressBackgroundColor(Theme.getColor("chat_inLoaderPhoto"));
            this.buttonState = 3;
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
            if (this.hasMiniProgress == 1)
            {
              DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
              this.miniButtonState = -1;
              label1832:
              localObject1 = this.radialProgress;
              localObject2 = getMiniDrawableForCurrentState();
              if (this.miniButtonState != 1) {
                break label1955;
              }
            }
            label1955:
            for (bool1 = true;; bool1 = false)
            {
              ((RadialProgress)localObject1).setMiniBackground((Drawable)localObject2, bool1, paramBoolean);
              break;
              DownloadController.getInstance(this.currentAccount).addLoadingFileObserver((String)localObject1, this.currentMessageObject, this);
              if (!FileLoader.getInstance(this.currentAccount).isLoadingFile((String)localObject1))
              {
                this.radialProgress.setProgress(0.0F, paramBoolean);
                this.miniButtonState = 0;
                break label1832;
              }
              this.miniButtonState = 1;
              localObject1 = ImageLoader.getInstance().getFileProgress((String)localObject1);
              if (localObject1 != null)
              {
                this.radialProgress.setProgress(((Float)localObject1).floatValue(), paramBoolean);
                break label1832;
              }
              this.radialProgress.setProgress(0.0F, paramBoolean);
              break label1832;
            }
          }
          if (bool2)
          {
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            if (this.currentMessageObject.needDrawBluredPreview()) {
              this.buttonState = -1;
            }
            for (;;)
            {
              this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
              if (this.photoNotSet) {
                setMessageObject(this.currentMessageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
              }
              invalidate();
              break;
              if ((this.currentMessageObject.type == 8) && (!this.photoImage.isAllowStartAnimation())) {
                this.buttonState = 2;
              } else if (this.documentAttachType == 4) {
                this.buttonState = 3;
              } else {
                this.buttonState = -1;
              }
            }
          }
          DownloadController.getInstance(this.currentAccount).addLoadingFileObserver((String)localObject1, this.currentMessageObject, this);
          f = 0.0F;
          bool2 = false;
          if (FileLoader.getInstance(this.currentAccount).isLoadingFile((String)localObject1)) {
            break label2293;
          }
          bool1 = false;
          if (this.currentMessageObject.type != 1) {
            break label2204;
          }
          bool1 = DownloadController.getInstance(this.currentAccount).canDownloadMedia(this.currentMessageObject);
          label2156:
          if ((this.cancelLoading) || (!bool1)) {
            break label2282;
          }
          bool1 = true;
          this.buttonState = 1;
        }
      }
    }
    for (;;)
    {
      this.radialProgress.setBackground(getDrawableForCurrentState(), bool1, paramBoolean);
      this.radialProgress.setProgress(f, false);
      invalidate();
      break label615;
      break;
      label2204:
      if ((this.currentMessageObject.type == 8) && (MessageObject.isNewGifDocument(this.currentMessageObject.messageOwner.media.document)))
      {
        bool1 = DownloadController.getInstance(this.currentAccount).canDownloadMedia(this.currentMessageObject);
        break label2156;
      }
      if (this.currentMessageObject.type != 5) {
        break label2156;
      }
      bool1 = DownloadController.getInstance(this.currentAccount).canDownloadMedia(this.currentMessageObject);
      break label2156;
      label2282:
      this.buttonState = 0;
      bool1 = bool2;
    }
    label2293:
    bool1 = true;
    this.buttonState = 1;
    localObject1 = ImageLoader.getInstance().getFileProgress((String)localObject1);
    if (localObject1 != null) {}
    for (float f = ((Float)localObject1).floatValue();; f = 0.0F) {
      break;
    }
  }
  
  public void updatePlayingMessageProgress()
  {
    if (this.currentMessageObject == null) {}
    int i;
    int j;
    int k;
    do
    {
      return;
      if (this.currentMessageObject.isRoundVideo())
      {
        i = 0;
        localObject = this.currentMessageObject.getDocument();
        for (j = 0;; j++)
        {
          k = i;
          if (j < ((TLRPC.Document)localObject).attributes.size())
          {
            TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)((TLRPC.Document)localObject).attributes.get(j);
            if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeVideo)) {
              k = localDocumentAttribute.duration;
            }
          }
          else
          {
            j = k;
            if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
              j = Math.max(0, k - this.currentMessageObject.audioProgressSec);
            }
            if (this.lastTime == j) {
              break;
            }
            this.lastTime = j;
            localObject = String.format("%02d:%02d", new Object[] { Integer.valueOf(j / 60), Integer.valueOf(j % 60) });
            this.timeWidthAudio = ((int)Math.ceil(Theme.chat_timePaint.measureText((String)localObject)));
            this.durationLayout = new StaticLayout((CharSequence)localObject, Theme.chat_timePaint, this.timeWidthAudio, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
            invalidate();
            break;
          }
        }
      }
    } while (this.documentAttach == null);
    if (this.useSeekBarWaweform)
    {
      if (!this.seekBarWaveform.isDragging()) {
        this.seekBarWaveform.setProgress(this.currentMessageObject.audioProgress);
      }
      label239:
      i = 0;
      if (this.documentAttachType != 3) {
        break label464;
      }
      if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
        break label452;
      }
      j = 0;
      label264:
      k = i;
      if (j < this.documentAttach.attributes.size())
      {
        localObject = (TLRPC.DocumentAttribute)this.documentAttach.attributes.get(j);
        if (!(localObject instanceof TLRPC.TL_documentAttributeAudio)) {
          break label446;
        }
        k = ((TLRPC.DocumentAttribute)localObject).duration;
      }
      label309:
      if (this.lastTime != k)
      {
        this.lastTime = k;
        localObject = String.format("%02d:%02d", new Object[] { Integer.valueOf(k / 60), Integer.valueOf(k % 60) });
        this.timeWidthAudio = ((int)Math.ceil(Theme.chat_audioTimePaint.measureText((String)localObject)));
        this.durationLayout = new StaticLayout((CharSequence)localObject, Theme.chat_audioTimePaint, this.timeWidthAudio, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      }
    }
    label446:
    label452:
    label464:
    do
    {
      invalidate();
      break;
      if (this.seekBar.isDragging()) {
        break label239;
      }
      this.seekBar.setProgress(this.currentMessageObject.audioProgress);
      this.seekBar.setBufferedProgress(this.currentMessageObject.bufferedProgress);
      break label239;
      j++;
      break label264;
      k = this.currentMessageObject.audioProgressSec;
      break label309;
      k = 0;
      j = this.currentMessageObject.getDuration();
      if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
        k = this.currentMessageObject.audioProgressSec;
      }
    } while (this.lastTime == k);
    this.lastTime = k;
    if (j == 0) {}
    for (Object localObject = String.format("%d:%02d / -:--", new Object[] { Integer.valueOf(k / 60), Integer.valueOf(k % 60) });; localObject = String.format("%d:%02d / %d:%02d", new Object[] { Integer.valueOf(k / 60), Integer.valueOf(k % 60), Integer.valueOf(j / 60), Integer.valueOf(j % 60) }))
    {
      k = (int)Math.ceil(Theme.chat_audioTimePaint.measureText((String)localObject));
      this.durationLayout = new StaticLayout((CharSequence)localObject, Theme.chat_audioTimePaint, k, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      break;
    }
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    if ((super.verifyDrawable(paramDrawable)) || (paramDrawable == this.instantViewSelectorDrawable)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
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
    
    public abstract void didPressedInstantButton(ChatMessageCell paramChatMessageCell, int paramInt);
    
    public abstract void didPressedOther(ChatMessageCell paramChatMessageCell);
    
    public abstract void didPressedReplyMessage(ChatMessageCell paramChatMessageCell, int paramInt);
    
    public abstract void didPressedShare(ChatMessageCell paramChatMessageCell);
    
    public abstract void didPressedUrl(MessageObject paramMessageObject, CharacterStyle paramCharacterStyle, boolean paramBoolean);
    
    public abstract void didPressedUserAvatar(ChatMessageCell paramChatMessageCell, TLRPC.User paramUser);
    
    public abstract void didPressedViaBot(ChatMessageCell paramChatMessageCell, String paramString);
    
    public abstract boolean isChatAdminCell(int paramInt);
    
    public abstract void needOpenWebView(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2);
    
    public abstract boolean needPlayMessage(MessageObject paramMessageObject);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/ChatMessageCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */