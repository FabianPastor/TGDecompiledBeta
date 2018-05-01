package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.AccelerateInterpolator;
import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInlineMessage;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.GeoPoint;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_webDocument;
import org.telegram.tgnet.TLRPC.WebDocument;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LetterDrawable;
import org.telegram.ui.Components.RadialProgress;
import org.telegram.ui.PhotoViewer;

public class ContextLinkCell
  extends View
  implements DownloadController.FileDownloadProgressListener
{
  private static final int DOCUMENT_ATTACH_TYPE_AUDIO = 3;
  private static final int DOCUMENT_ATTACH_TYPE_DOCUMENT = 1;
  private static final int DOCUMENT_ATTACH_TYPE_GEO = 8;
  private static final int DOCUMENT_ATTACH_TYPE_GIF = 2;
  private static final int DOCUMENT_ATTACH_TYPE_MUSIC = 5;
  private static final int DOCUMENT_ATTACH_TYPE_NONE = 0;
  private static final int DOCUMENT_ATTACH_TYPE_PHOTO = 7;
  private static final int DOCUMENT_ATTACH_TYPE_STICKER = 6;
  private static final int DOCUMENT_ATTACH_TYPE_VIDEO = 4;
  private static AccelerateInterpolator interpolator = new AccelerateInterpolator(0.5F);
  private int TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
  private boolean buttonPressed;
  private int buttonState;
  private int currentAccount = UserConfig.selectedAccount;
  private MessageObject currentMessageObject;
  private TLRPC.PhotoSize currentPhotoObject;
  private ContextLinkCellDelegate delegate;
  private StaticLayout descriptionLayout;
  private int descriptionY = AndroidUtilities.dp(27.0F);
  private TLRPC.Document documentAttach;
  private int documentAttachType;
  private boolean drawLinkImageView;
  private TLRPC.BotInlineResult inlineResult;
  private long lastUpdateTime;
  private LetterDrawable letterDrawable = new LetterDrawable();
  private ImageReceiver linkImageView = new ImageReceiver(this);
  private StaticLayout linkLayout;
  private int linkY;
  private boolean mediaWebpage;
  private boolean needDivider;
  private boolean needShadow;
  private RadialProgress radialProgress = new RadialProgress(this);
  private float scale;
  private boolean scaled;
  private StaticLayout titleLayout;
  private int titleY = AndroidUtilities.dp(7.0F);
  
  public ContextLinkCell(Context paramContext)
  {
    super(paramContext);
  }
  
  private void didPressedButton()
  {
    if ((this.documentAttachType == 3) || (this.documentAttachType == 5))
    {
      if (this.buttonState != 0) {
        break label59;
      }
      if (MediaController.getInstance().playMessage(this.currentMessageObject))
      {
        this.buttonState = 1;
        this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
        invalidate();
      }
    }
    label59:
    do
    {
      for (;;)
      {
        return;
        if (this.buttonState != 1) {
          break;
        }
        if (MediaController.getInstance().pauseMessage(this.currentMessageObject))
        {
          this.buttonState = 0;
          this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
          invalidate();
        }
      }
      if (this.buttonState == 2)
      {
        this.radialProgress.setProgress(0.0F, false);
        if (this.documentAttach != null) {
          FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, true, 0);
        }
        for (;;)
        {
          this.buttonState = 4;
          this.radialProgress.setBackground(getDrawableForCurrentState(), true, false);
          invalidate();
          break;
          if ((this.inlineResult.content instanceof TLRPC.TL_webDocument)) {
            FileLoader.getInstance(this.currentAccount).loadFile((TLRPC.TL_webDocument)this.inlineResult.content, true, 1);
          }
        }
      }
    } while (this.buttonState != 4);
    if (this.documentAttach != null) {
      FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
    }
    for (;;)
    {
      this.buttonState = 2;
      this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
      invalidate();
      break;
      if ((this.inlineResult.content instanceof TLRPC.TL_webDocument)) {
        FileLoader.getInstance(this.currentAccount).cancelLoadFile((TLRPC.TL_webDocument)this.inlineResult.content);
      }
    }
  }
  
  private Drawable getDrawableForCurrentState()
  {
    int i = 1;
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
      localObject = Theme.chat_fileStatesDrawable[(this.buttonState + 5)];
      if (this.buttonPressed) {}
      for (;;)
      {
        localObject = localObject[i];
        break;
        i = 0;
      }
      if (this.buttonState == 1) {
        localObject = Theme.chat_photoStatesDrawables[5][0];
      } else {
        localObject = null;
      }
    }
  }
  
  private void setAttachType()
  {
    this.currentMessageObject = null;
    this.documentAttachType = 0;
    if (this.documentAttach != null) {
      if (MessageObject.isGifDocument(this.documentAttach)) {
        this.documentAttachType = 2;
      }
    }
    TLRPC.TL_message localTL_message;
    for (;;)
    {
      if ((this.documentAttachType == 3) || (this.documentAttachType == 5))
      {
        localTL_message = new TLRPC.TL_message();
        localTL_message.out = true;
        localTL_message.id = (-Utilities.random.nextInt());
        localTL_message.to_id = new TLRPC.TL_peerUser();
        localObject1 = localTL_message.to_id;
        int i = UserConfig.getInstance(this.currentAccount).getClientUserId();
        localTL_message.from_id = i;
        ((TLRPC.Peer)localObject1).user_id = i;
        localTL_message.date = ((int)(System.currentTimeMillis() / 1000L));
        localTL_message.message = "";
        localTL_message.media = new TLRPC.TL_messageMediaDocument();
        localObject1 = localTL_message.media;
        ((TLRPC.MessageMedia)localObject1).flags |= 0x3;
        localTL_message.media.document = new TLRPC.TL_document();
        localTL_message.flags |= 0x300;
        if (this.documentAttach == null) {
          break;
        }
        localTL_message.media.document = this.documentAttach;
        localTL_message.attachPath = "";
        this.currentMessageObject = new MessageObject(this.currentAccount, localTL_message, false);
      }
      return;
      if (MessageObject.isStickerDocument(this.documentAttach))
      {
        this.documentAttachType = 6;
      }
      else if (MessageObject.isMusicDocument(this.documentAttach))
      {
        this.documentAttachType = 5;
      }
      else if (MessageObject.isVoiceDocument(this.documentAttach))
      {
        this.documentAttachType = 3;
        continue;
        if (this.inlineResult != null) {
          if (this.inlineResult.photo != null) {
            this.documentAttachType = 7;
          } else if (this.inlineResult.type.equals("audio")) {
            this.documentAttachType = 5;
          } else if (this.inlineResult.type.equals("voice")) {
            this.documentAttachType = 3;
          }
        }
      }
    }
    Object localObject2 = this.inlineResult.content.url;
    label376:
    label543:
    label567:
    Object localObject3;
    Object localObject4;
    if (this.documentAttachType == 5)
    {
      localObject1 = "mp3";
      localObject1 = ImageLoader.getHttpUrlExtension((String)localObject2, (String)localObject1);
      localTL_message.media.document.id = 0L;
      localTL_message.media.document.access_hash = 0L;
      localTL_message.media.document.date = localTL_message.date;
      localTL_message.media.document.mime_type = ("audio/" + (String)localObject1);
      localTL_message.media.document.size = 0;
      localTL_message.media.document.thumb = new TLRPC.TL_photoSizeEmpty();
      localTL_message.media.document.thumb.type = "s";
      localTL_message.media.document.dc_id = 0;
      localObject2 = new TLRPC.TL_documentAttributeAudio();
      ((TLRPC.TL_documentAttributeAudio)localObject2).duration = MessageObject.getInlineResultDuration(this.inlineResult);
      if (this.inlineResult.title == null) {
        break label815;
      }
      localObject1 = this.inlineResult.title;
      ((TLRPC.TL_documentAttributeAudio)localObject2).title = ((String)localObject1);
      if (this.inlineResult.description == null) {
        break label822;
      }
      localObject1 = this.inlineResult.description;
      ((TLRPC.TL_documentAttributeAudio)localObject2).performer = ((String)localObject1);
      ((TLRPC.TL_documentAttributeAudio)localObject2).flags |= 0x3;
      if (this.documentAttachType == 3) {
        ((TLRPC.TL_documentAttributeAudio)localObject2).voice = true;
      }
      localTL_message.media.document.attributes.add(localObject2);
      localObject2 = new TLRPC.TL_documentAttributeFilename();
      localObject3 = new StringBuilder().append(Utilities.MD5(this.inlineResult.content.url)).append(".");
      localObject4 = this.inlineResult.content.url;
      if (this.documentAttachType != 5) {
        break label829;
      }
      localObject1 = "mp3";
      label679:
      ((TLRPC.TL_documentAttributeFilename)localObject2).file_name = ImageLoader.getHttpUrlExtension((String)localObject4, (String)localObject1);
      localTL_message.media.document.attributes.add(localObject2);
      localObject3 = FileLoader.getDirectory(4);
      localObject4 = new StringBuilder().append(Utilities.MD5(this.inlineResult.content.url)).append(".");
      localObject2 = this.inlineResult.content.url;
      if (this.documentAttachType != 5) {
        break label836;
      }
    }
    label815:
    label822:
    label829:
    label836:
    for (Object localObject1 = "mp3";; localObject1 = "ogg")
    {
      localTL_message.attachPath = new File((File)localObject3, ImageLoader.getHttpUrlExtension((String)localObject2, (String)localObject1)).getAbsolutePath();
      break;
      localObject1 = "ogg";
      break label376;
      localObject1 = "";
      break label543;
      localObject1 = "";
      break label567;
      localObject1 = "ogg";
      break label679;
    }
  }
  
  public TLRPC.Document getDocument()
  {
    return this.documentAttach;
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
    return this.linkImageView;
  }
  
  public TLRPC.BotInlineResult getResult()
  {
    return this.inlineResult;
  }
  
  public boolean isSticker()
  {
    if (this.documentAttachType == 6) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if ((this.drawLinkImageView) && (this.linkImageView.onAttachedToWindow())) {
      updateButtonState(false);
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (this.drawLinkImageView) {
      this.linkImageView.onDetachedFromWindow();
    }
    DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    float f;
    label82:
    label142:
    Object localObject;
    label208:
    label225:
    boolean bool;
    label258:
    long l2;
    if (this.titleLayout != null)
    {
      paramCanvas.save();
      if (LocaleController.isRTL)
      {
        f = 8.0F;
        paramCanvas.translate(AndroidUtilities.dp(f), this.titleY);
        this.titleLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
    }
    else
    {
      if (this.descriptionLayout != null)
      {
        Theme.chat_contextResult_descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        paramCanvas.save();
        if (!LocaleController.isRTL) {
          break label548;
        }
        f = 8.0F;
        paramCanvas.translate(AndroidUtilities.dp(f), this.descriptionY);
        this.descriptionLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      if (this.linkLayout != null)
      {
        Theme.chat_contextResult_descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteLinkText"));
        paramCanvas.save();
        if (!LocaleController.isRTL) {
          break label556;
        }
        f = 8.0F;
        paramCanvas.translate(AndroidUtilities.dp(f), this.linkY);
        this.linkLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      if (this.mediaWebpage) {
        break label1107;
      }
      if ((this.documentAttachType != 3) && (this.documentAttachType != 5)) {
        break label572;
      }
      RadialProgress localRadialProgress = this.radialProgress;
      if (!this.buttonPressed) {
        break label564;
      }
      localObject = "chat_inAudioSelectedProgress";
      localRadialProgress.setProgressColor(Theme.getColor((String)localObject));
      this.radialProgress.draw(paramCanvas);
      if (this.drawLinkImageView)
      {
        if (this.inlineResult != null)
        {
          localObject = this.linkImageView;
          if (PhotoViewer.isShowingImage(this.inlineResult)) {
            break label1285;
          }
          bool = true;
          ((ImageReceiver)localObject).setVisible(bool, false);
        }
        paramCanvas.save();
        if (((this.scaled) && (this.scale != 0.8F)) || ((!this.scaled) && (this.scale != 1.0F)))
        {
          long l1 = System.currentTimeMillis();
          l2 = l1 - this.lastUpdateTime;
          this.lastUpdateTime = l1;
          if ((!this.scaled) || (this.scale == 0.8F)) {
            break label1291;
          }
          this.scale -= (float)l2 / 400.0F;
          if (this.scale < 0.8F) {
            this.scale = 0.8F;
          }
          label377:
          invalidate();
        }
        paramCanvas.scale(this.scale, this.scale, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        this.linkImageView.draw(paramCanvas);
        paramCanvas.restore();
      }
      if ((this.mediaWebpage) && ((this.documentAttachType == 7) || (this.documentAttachType == 2))) {
        this.radialProgress.draw(paramCanvas);
      }
      if ((this.needDivider) && (!this.mediaWebpage))
      {
        if (!LocaleController.isRTL) {
          break label1324;
        }
        paramCanvas.drawLine(0.0F, getMeasuredHeight() - 1, getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, Theme.dividerPaint);
      }
    }
    for (;;)
    {
      if (this.needShadow)
      {
        Theme.chat_contextResult_shadowUnderSwitchDrawable.setBounds(0, 0, getMeasuredWidth(), AndroidUtilities.dp(3.0F));
        Theme.chat_contextResult_shadowUnderSwitchDrawable.draw(paramCanvas);
      }
      return;
      f = AndroidUtilities.leftBaseline;
      break;
      label548:
      f = AndroidUtilities.leftBaseline;
      break label82;
      label556:
      f = AndroidUtilities.leftBaseline;
      break label142;
      label564:
      localObject = "chat_inAudioProgress";
      break label208;
      label572:
      if ((this.inlineResult != null) && (this.inlineResult.type.equals("file")))
      {
        i = Theme.chat_inlineResultFile.getIntrinsicWidth();
        j = Theme.chat_inlineResultFile.getIntrinsicHeight();
        k = this.linkImageView.getImageX() + (AndroidUtilities.dp(52.0F) - i) / 2;
        m = this.linkImageView.getImageY() + (AndroidUtilities.dp(52.0F) - j) / 2;
        paramCanvas.drawRect(this.linkImageView.getImageX(), this.linkImageView.getImageY(), this.linkImageView.getImageX() + AndroidUtilities.dp(52.0F), this.linkImageView.getImageY() + AndroidUtilities.dp(52.0F), LetterDrawable.paint);
        Theme.chat_inlineResultFile.setBounds(k, m, k + i, m + j);
        Theme.chat_inlineResultFile.draw(paramCanvas);
        break label225;
      }
      if ((this.inlineResult != null) && ((this.inlineResult.type.equals("audio")) || (this.inlineResult.type.equals("voice"))))
      {
        m = Theme.chat_inlineResultAudio.getIntrinsicWidth();
        j = Theme.chat_inlineResultAudio.getIntrinsicHeight();
        i = this.linkImageView.getImageX() + (AndroidUtilities.dp(52.0F) - m) / 2;
        k = this.linkImageView.getImageY() + (AndroidUtilities.dp(52.0F) - j) / 2;
        paramCanvas.drawRect(this.linkImageView.getImageX(), this.linkImageView.getImageY(), this.linkImageView.getImageX() + AndroidUtilities.dp(52.0F), this.linkImageView.getImageY() + AndroidUtilities.dp(52.0F), LetterDrawable.paint);
        Theme.chat_inlineResultAudio.setBounds(i, k, i + m, k + j);
        Theme.chat_inlineResultAudio.draw(paramCanvas);
        break label225;
      }
      if ((this.inlineResult != null) && ((this.inlineResult.type.equals("venue")) || (this.inlineResult.type.equals("geo"))))
      {
        j = Theme.chat_inlineResultLocation.getIntrinsicWidth();
        m = Theme.chat_inlineResultLocation.getIntrinsicHeight();
        k = this.linkImageView.getImageX() + (AndroidUtilities.dp(52.0F) - j) / 2;
        i = this.linkImageView.getImageY() + (AndroidUtilities.dp(52.0F) - m) / 2;
        paramCanvas.drawRect(this.linkImageView.getImageX(), this.linkImageView.getImageY(), this.linkImageView.getImageX() + AndroidUtilities.dp(52.0F), this.linkImageView.getImageY() + AndroidUtilities.dp(52.0F), LetterDrawable.paint);
        Theme.chat_inlineResultLocation.setBounds(k, i, k + j, i + m);
        Theme.chat_inlineResultLocation.draw(paramCanvas);
        break label225;
      }
      this.letterDrawable.draw(paramCanvas);
      break label225;
      label1107:
      if ((this.inlineResult == null) || ((!(this.inlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaGeo)) && (!(this.inlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaVenue)))) {
        break label225;
      }
      int k = Theme.chat_inlineResultLocation.getIntrinsicWidth();
      int j = Theme.chat_inlineResultLocation.getIntrinsicHeight();
      int m = this.linkImageView.getImageX() + (this.linkImageView.getImageWidth() - k) / 2;
      int i = this.linkImageView.getImageY() + (this.linkImageView.getImageHeight() - j) / 2;
      paramCanvas.drawRect(this.linkImageView.getImageX(), this.linkImageView.getImageY(), this.linkImageView.getImageX() + this.linkImageView.getImageWidth(), this.linkImageView.getImageY() + this.linkImageView.getImageHeight(), LetterDrawable.paint);
      Theme.chat_inlineResultLocation.setBounds(m, i, m + k, i + j);
      Theme.chat_inlineResultLocation.draw(paramCanvas);
      break label225;
      label1285:
      bool = false;
      break label258;
      label1291:
      this.scale += (float)l2 / 400.0F;
      if (this.scale <= 1.0F) {
        break label377;
      }
      this.scale = 1.0F;
      break label377;
      label1324:
      paramCanvas.drawLine(AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
    }
  }
  
  public void onFailedDownload(String paramString)
  {
    updateButtonState(false);
  }
  
  @SuppressLint({"DrawAllocation"})
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    this.drawLinkImageView = false;
    this.descriptionLayout = null;
    this.titleLayout = null;
    this.linkLayout = null;
    this.currentPhotoObject = null;
    this.linkY = AndroidUtilities.dp(27.0F);
    if ((this.inlineResult == null) && (this.documentAttach == null)) {
      setMeasuredDimension(AndroidUtilities.dp(100.0F), AndroidUtilities.dp(100.0F));
    }
    int j;
    label132:
    int k;
    for (;;)
    {
      return;
      int i = View.MeasureSpec.getSize(paramInt1);
      j = i - AndroidUtilities.dp(AndroidUtilities.leftBaseline) - AndroidUtilities.dp(8.0F);
      Object localObject1 = null;
      Object localObject2 = null;
      Object localObject4 = null;
      Object localObject5 = null;
      ImageReceiver localImageReceiver = null;
      Object localObject6;
      if (this.documentAttach != null)
      {
        localObject6 = new ArrayList();
        ((ArrayList)localObject6).add(this.documentAttach.thumb);
        if ((!this.mediaWebpage) && (this.inlineResult != null) && (this.inlineResult.title == null)) {}
      }
      try
      {
        k = (int)Math.ceil(Theme.chat_contextResult_titleTextPaint.measureText(this.inlineResult.title));
        localObject7 = TextUtils.ellipsize(Emoji.replaceEmoji(this.inlineResult.title.replace('\n', ' '), Theme.chat_contextResult_titleTextPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0F), false), Theme.chat_contextResult_titleTextPaint, Math.min(k, j), TextUtils.TruncateAt.END);
        localObject2 = new android/text/StaticLayout;
        ((StaticLayout)localObject2).<init>((CharSequence)localObject7, Theme.chat_contextResult_titleTextPaint, j + AndroidUtilities.dp(4.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        this.titleLayout = ((StaticLayout)localObject2);
        this.letterDrawable.setTitle(this.inlineResult.title);
        if (this.inlineResult.description == null) {}
      }
      catch (Exception localException2)
      {
        try
        {
          this.descriptionLayout = ChatMessageCell.generateStaticLayout(Emoji.replaceEmoji(this.inlineResult.description, Theme.chat_contextResult_descriptionTextPaint.getFontMetricsInt(), AndroidUtilities.dp(13.0F), false), Theme.chat_contextResult_descriptionTextPaint, j, j, 0, 3);
          if (this.descriptionLayout.getLineCount() > 0) {
            this.linkY = (this.descriptionY + this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1) + AndroidUtilities.dp(1.0F));
          }
          if (this.inlineResult.url == null) {}
        }
        catch (Exception localException2)
        {
          try
          {
            for (;;)
            {
              k = (int)Math.ceil(Theme.chat_contextResult_descriptionTextPaint.measureText(this.inlineResult.url));
              localObject7 = TextUtils.ellipsize(this.inlineResult.url.replace('\n', ' '), Theme.chat_contextResult_descriptionTextPaint, Math.min(k, j), TextUtils.TruncateAt.MIDDLE);
              localObject2 = new android/text/StaticLayout;
              ((StaticLayout)localObject2).<init>((CharSequence)localObject7, Theme.chat_contextResult_descriptionTextPaint, j, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
              this.linkLayout = ((StaticLayout)localObject2);
              localObject8 = null;
              if (this.documentAttach == null) {
                break label1425;
              }
              if (!MessageObject.isGifDocument(this.documentAttach)) {
                break label1338;
              }
              this.currentPhotoObject = this.documentAttach.thumb;
              localObject2 = localObject1;
              localObject7 = localObject8;
              localObject1 = localImageReceiver;
              if (this.inlineResult != null)
              {
                localObject6 = localObject5;
                if ((this.inlineResult.content instanceof TLRPC.TL_webDocument))
                {
                  localObject6 = localObject5;
                  if (this.inlineResult.type != null)
                  {
                    if (!this.inlineResult.type.startsWith("gif")) {
                      break label1507;
                    }
                    localObject6 = localObject5;
                    if (this.documentAttachType != 2)
                    {
                      localObject6 = (TLRPC.TL_webDocument)this.inlineResult.content;
                      this.documentAttachType = 2;
                    }
                  }
                }
                localObject5 = localObject6;
                if (localObject6 == null)
                {
                  localObject5 = localObject6;
                  if ((this.inlineResult.thumb instanceof TLRPC.TL_webDocument)) {
                    localObject5 = (TLRPC.TL_webDocument)this.inlineResult.thumb;
                  }
                }
                localObject4 = localObject5;
                localObject1 = localImageReceiver;
                if (localObject5 == null)
                {
                  localObject4 = localObject5;
                  localObject1 = localImageReceiver;
                  if (this.currentPhotoObject == null)
                  {
                    localObject4 = localObject5;
                    localObject1 = localImageReceiver;
                    if (localObject2 == null) {
                      if (!(this.inlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaVenue))
                      {
                        localObject4 = localObject5;
                        localObject1 = localImageReceiver;
                        if (!(this.inlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaGeo)) {}
                      }
                      else
                      {
                        double d1 = this.inlineResult.send_message.geo.lat;
                        double d2 = this.inlineResult.send_message.geo._long;
                        localObject1 = String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=72x72&maptype=roadmap&scale=%d&markers=color:red|size:small|%f,%f&sensor=false", new Object[] { Double.valueOf(d1), Double.valueOf(d2), Integer.valueOf(Math.min(2, (int)Math.ceil(AndroidUtilities.density))), Double.valueOf(d1), Double.valueOf(d2) });
                        localObject4 = localObject5;
                      }
                    }
                  }
                }
              }
              k = 0;
              int m = 0;
              n = m;
              i1 = k;
              if (this.documentAttach != null)
              {
                j = 0;
                n = m;
                i1 = k;
                if (j < this.documentAttach.attributes.size())
                {
                  localObject6 = (TLRPC.DocumentAttribute)this.documentAttach.attributes.get(j);
                  if ((!(localObject6 instanceof TLRPC.TL_documentAttributeImageSize)) && (!(localObject6 instanceof TLRPC.TL_documentAttributeVideo))) {
                    break label1570;
                  }
                  i1 = ((TLRPC.DocumentAttribute)localObject6).w;
                  n = ((TLRPC.DocumentAttribute)localObject6).h;
                }
              }
              if (i1 != 0)
              {
                j = n;
                k = i1;
                if (n != 0) {}
              }
              else
              {
                if (this.currentPhotoObject == null) {
                  break label1576;
                }
                if (localObject2 != null) {
                  ((TLRPC.PhotoSize)localObject2).size = -1;
                }
                k = this.currentPhotoObject.w;
                j = this.currentPhotoObject.h;
              }
              if (k != 0)
              {
                i1 = j;
                if (j != 0) {}
              }
              else
              {
                i1 = AndroidUtilities.dp(80.0F);
                k = i1;
              }
              if ((this.documentAttach != null) || (this.currentPhotoObject != null) || (localObject4 != null) || (localObject1 != null))
              {
                localObject5 = "52_52_b";
                if (!this.mediaWebpage) {
                  break label1678;
                }
                j = (int)(k / (i1 / AndroidUtilities.dp(80.0F)));
                if (this.documentAttachType != 2) {
                  break label1615;
                }
                localObject6 = String.format(Locale.US, "%d_%d_b", new Object[] { Integer.valueOf((int)(j / AndroidUtilities.density)), Integer.valueOf(80) });
                localObject5 = localObject6;
                localImageReceiver = this.linkImageView;
                if (this.documentAttachType != 6) {
                  break label1686;
                }
                bool = true;
                localImageReceiver.setAspectFit(bool);
                if (this.documentAttachType != 2) {
                  break label1748;
                }
                if (this.documentAttach == null) {
                  break label1698;
                }
                localObject5 = this.linkImageView;
                localObject1 = this.documentAttach;
                if (this.currentPhotoObject == null) {
                  break label1692;
                }
                localObject2 = this.currentPhotoObject.location;
                ((ImageReceiver)localObject5).setImage((TLObject)localObject1, null, (TLRPC.FileLocation)localObject2, (String)localObject6, this.documentAttach.size, (String)localObject7, 0);
                this.drawLinkImageView = true;
              }
              if (!this.mediaWebpage) {
                break label1861;
              }
              paramInt2 = View.MeasureSpec.getSize(paramInt2);
              paramInt1 = paramInt2;
              if (paramInt2 == 0) {
                paramInt1 = AndroidUtilities.dp(100.0F);
              }
              setMeasuredDimension(i, paramInt1);
              j = (i - AndroidUtilities.dp(24.0F)) / 2;
              paramInt2 = (paramInt1 - AndroidUtilities.dp(24.0F)) / 2;
              this.radialProgress.setProgressRect(j, paramInt2, AndroidUtilities.dp(24.0F) + j, AndroidUtilities.dp(24.0F) + paramInt2);
              this.linkImageView.setImageCoords(0, 0, i, paramInt1);
              break;
              localObject6 = localObject2;
              if (this.inlineResult == null) {
                break label132;
              }
              localObject6 = localObject2;
              if (this.inlineResult.photo == null) {
                break label132;
              }
              localObject6 = new ArrayList(this.inlineResult.photo.sizes);
              break label132;
              localException1 = localException1;
              FileLog.e(localException1);
            }
            localException2 = localException2;
            FileLog.e(localException2);
          }
          catch (Exception localException3)
          {
            Object localObject7;
            for (;;)
            {
              Object localObject8;
              int n;
              int i1;
              boolean bool;
              FileLog.e(localException3);
              continue;
              label1338:
              if (MessageObject.isStickerDocument(this.documentAttach))
              {
                this.currentPhotoObject = this.documentAttach.thumb;
                localObject7 = "webp";
                localObject3 = localObject1;
              }
              else
              {
                localObject7 = localObject8;
                localObject3 = localObject1;
                if (this.documentAttachType != 5)
                {
                  localObject7 = localObject8;
                  localObject3 = localObject1;
                  if (this.documentAttachType != 3)
                  {
                    this.currentPhotoObject = this.documentAttach.thumb;
                    localObject7 = localObject8;
                    localObject3 = localObject1;
                    continue;
                    label1425:
                    localObject7 = localObject8;
                    localObject3 = localObject1;
                    if (this.inlineResult != null)
                    {
                      localObject7 = localObject8;
                      localObject3 = localObject1;
                      if (this.inlineResult.photo != null)
                      {
                        this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize((ArrayList)localObject6, AndroidUtilities.getPhotoSize(), true);
                        localObject6 = FileLoader.getClosestPhotoSizeWithSize((ArrayList)localObject6, 80);
                        localObject7 = localObject8;
                        localObject3 = localObject6;
                        if (localObject6 == this.currentPhotoObject)
                        {
                          localObject3 = null;
                          localObject7 = localObject8;
                          continue;
                          label1507:
                          localObject6 = localObject5;
                          if (this.inlineResult.type.equals("photo")) {
                            if ((this.inlineResult.thumb instanceof TLRPC.TL_webDocument))
                            {
                              localObject6 = (TLRPC.TL_webDocument)this.inlineResult.thumb;
                            }
                            else
                            {
                              localObject6 = (TLRPC.TL_webDocument)this.inlineResult.content;
                              continue;
                              label1570:
                              j++;
                              continue;
                              label1576:
                              j = n;
                              k = i1;
                              if (this.inlineResult != null)
                              {
                                localObject6 = MessageObject.getInlineResultWidthAndHeight(this.inlineResult);
                                k = localObject6[0];
                                j = localObject6[1];
                                continue;
                                label1615:
                                localObject6 = String.format(Locale.US, "%d_%d", new Object[] { Integer.valueOf((int)(j / AndroidUtilities.density)), Integer.valueOf(80) });
                                localObject5 = (String)localObject6 + "_b";
                                continue;
                                label1678:
                                localObject6 = "52_52";
                                continue;
                                label1686:
                                bool = false;
                                continue;
                                label1692:
                                localObject3 = null;
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
            label1698:
            localObject5 = this.linkImageView;
            if (this.currentPhotoObject != null) {}
            for (Object localObject3 = this.currentPhotoObject.location;; localObject3 = null)
            {
              ((ImageReceiver)localObject5).setImage((TLObject)localObject4, (String)localObject1, null, null, (TLRPC.FileLocation)localObject3, (String)localObject6, -1, (String)localObject7, 1);
              break;
            }
            label1748:
            if (this.currentPhotoObject != null)
            {
              localObject4 = this.linkImageView;
              localObject1 = this.currentPhotoObject.location;
              if (localObject3 != null) {}
              for (localObject3 = ((TLRPC.PhotoSize)localObject3).location;; localObject3 = null)
              {
                ((ImageReceiver)localObject4).setImage((TLObject)localObject1, (String)localObject6, (TLRPC.FileLocation)localObject3, (String)localObject5, this.currentPhotoObject.size, (String)localObject7, 0);
                break;
              }
            }
            localImageReceiver = this.linkImageView;
            if (localObject3 != null) {}
            for (localObject3 = ((TLRPC.PhotoSize)localObject3).location;; localObject3 = null)
            {
              localImageReceiver.setImage((TLObject)localObject4, (String)localObject1, (String)localObject6, null, (TLRPC.FileLocation)localObject3, (String)localObject5, -1, (String)localObject7, 1);
              break;
            }
            label1861:
            j = 0;
            paramInt2 = j;
            if (this.titleLayout != null)
            {
              paramInt2 = j;
              if (this.titleLayout.getLineCount() != 0) {
                paramInt2 = 0 + this.titleLayout.getLineBottom(this.titleLayout.getLineCount() - 1);
              }
            }
            j = paramInt2;
            if (this.descriptionLayout != null)
            {
              j = paramInt2;
              if (this.descriptionLayout.getLineCount() != 0) {
                j = paramInt2 + this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1);
              }
            }
            paramInt2 = j;
            if (this.linkLayout != null)
            {
              paramInt2 = j;
              if (this.linkLayout.getLineCount() > 0) {
                paramInt2 = j + this.linkLayout.getLineBottom(this.linkLayout.getLineCount() - 1);
              }
            }
            paramInt2 = Math.max(AndroidUtilities.dp(52.0F), paramInt2);
            j = View.MeasureSpec.getSize(paramInt1);
            k = Math.max(AndroidUtilities.dp(68.0F), AndroidUtilities.dp(16.0F) + paramInt2);
            if (!this.needDivider) {
              break label2167;
            }
          }
        }
      }
    }
    paramInt2 = 1;
    label2037:
    setMeasuredDimension(j, paramInt2 + k);
    paramInt2 = AndroidUtilities.dp(52.0F);
    if (LocaleController.isRTL) {}
    for (paramInt1 = View.MeasureSpec.getSize(paramInt1) - AndroidUtilities.dp(8.0F) - paramInt2;; paramInt1 = AndroidUtilities.dp(8.0F))
    {
      this.letterDrawable.setBounds(paramInt1, AndroidUtilities.dp(8.0F), paramInt1 + paramInt2, AndroidUtilities.dp(60.0F));
      this.linkImageView.setImageCoords(paramInt1, AndroidUtilities.dp(8.0F), paramInt2, paramInt2);
      if ((this.documentAttachType != 3) && (this.documentAttachType != 5)) {
        break;
      }
      this.radialProgress.setProgressRect(AndroidUtilities.dp(4.0F) + paramInt1, AndroidUtilities.dp(12.0F), AndroidUtilities.dp(48.0F) + paramInt1, AndroidUtilities.dp(56.0F));
      break;
      label2167:
      paramInt2 = 0;
      break label2037;
    }
  }
  
  public void onProgressDownload(String paramString, float paramFloat)
  {
    this.radialProgress.setProgress(paramFloat, true);
    if ((this.documentAttachType == 3) || (this.documentAttachType == 5)) {
      if (this.buttonState != 4) {
        updateButtonState(false);
      }
    }
    for (;;)
    {
      return;
      if (this.buttonState != 1) {
        updateButtonState(false);
      }
    }
  }
  
  public void onProgressUpload(String paramString, float paramFloat, boolean paramBoolean) {}
  
  public void onSuccessDownload(String paramString)
  {
    this.radialProgress.setProgress(1.0F, true);
    updateButtonState(true);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((this.mediaWebpage) || (this.delegate == null) || (this.inlineResult == null))
    {
      bool1 = super.onTouchEvent(paramMotionEvent);
      return bool1;
    }
    int i = (int)paramMotionEvent.getX();
    int j = (int)paramMotionEvent.getY();
    boolean bool1 = false;
    AndroidUtilities.dp(48.0F);
    boolean bool2;
    boolean bool3;
    if ((this.documentAttachType == 3) || (this.documentAttachType == 5))
    {
      bool2 = this.letterDrawable.getBounds().contains(i, j);
      if (paramMotionEvent.getAction() == 0)
      {
        bool3 = bool1;
        if (bool2)
        {
          this.buttonPressed = true;
          invalidate();
          bool3 = true;
          this.radialProgress.swapBackground(getDrawableForCurrentState());
        }
      }
    }
    for (;;)
    {
      bool1 = bool3;
      if (bool3) {
        break;
      }
      bool1 = super.onTouchEvent(paramMotionEvent);
      break;
      bool3 = bool1;
      if (this.buttonPressed)
      {
        if (paramMotionEvent.getAction() == 1)
        {
          this.buttonPressed = false;
          playSoundEffect(0);
          didPressedButton();
          invalidate();
        }
        for (;;)
        {
          this.radialProgress.swapBackground(getDrawableForCurrentState());
          bool3 = bool1;
          break;
          if (paramMotionEvent.getAction() == 3)
          {
            this.buttonPressed = false;
            invalidate();
          }
          else if ((paramMotionEvent.getAction() == 2) && (!bool2))
          {
            this.buttonPressed = false;
            invalidate();
          }
        }
        bool3 = bool1;
        if (this.inlineResult != null)
        {
          bool3 = bool1;
          if (this.inlineResult.content != null)
          {
            bool3 = bool1;
            if (!TextUtils.isEmpty(this.inlineResult.content.url)) {
              if (paramMotionEvent.getAction() == 0)
              {
                bool3 = bool1;
                if (this.letterDrawable.getBounds().contains(i, j))
                {
                  this.buttonPressed = true;
                  bool3 = true;
                }
              }
              else
              {
                bool3 = bool1;
                if (this.buttonPressed) {
                  if (paramMotionEvent.getAction() == 1)
                  {
                    this.buttonPressed = false;
                    playSoundEffect(0);
                    this.delegate.didPressedImage(this);
                    bool3 = bool1;
                  }
                  else if (paramMotionEvent.getAction() == 3)
                  {
                    this.buttonPressed = false;
                    bool3 = bool1;
                  }
                  else
                  {
                    bool3 = bool1;
                    if (paramMotionEvent.getAction() == 2)
                    {
                      bool3 = bool1;
                      if (!this.letterDrawable.getBounds().contains(i, j))
                      {
                        this.buttonPressed = false;
                        bool3 = bool1;
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
  public void setDelegate(ContextLinkCellDelegate paramContextLinkCellDelegate)
  {
    this.delegate = paramContextLinkCellDelegate;
  }
  
  public void setGif(TLRPC.Document paramDocument, boolean paramBoolean)
  {
    this.needDivider = paramBoolean;
    this.needShadow = false;
    this.inlineResult = null;
    this.documentAttach = paramDocument;
    this.mediaWebpage = true;
    setAttachType();
    requestLayout();
    updateButtonState(false);
  }
  
  public void setLink(TLRPC.BotInlineResult paramBotInlineResult, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    this.needDivider = paramBoolean2;
    this.needShadow = paramBoolean3;
    this.inlineResult = paramBotInlineResult;
    if ((this.inlineResult != null) && (this.inlineResult.document != null)) {}
    for (this.documentAttach = this.inlineResult.document;; this.documentAttach = null)
    {
      this.mediaWebpage = paramBoolean1;
      setAttachType();
      requestLayout();
      updateButtonState(false);
      return;
    }
  }
  
  public void setScaled(boolean paramBoolean)
  {
    this.scaled = paramBoolean;
    this.lastUpdateTime = System.currentTimeMillis();
    invalidate();
  }
  
  public boolean showingBitmap()
  {
    if (this.linkImageView.getBitmap() != null) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void updateButtonState(boolean paramBoolean)
  {
    float f = 0.0F;
    Object localObject1 = null;
    StringBuilder localStringBuilder = null;
    Object localObject2;
    Object localObject3;
    if ((this.documentAttachType == 5) || (this.documentAttachType == 3)) {
      if (this.documentAttach != null)
      {
        localObject2 = FileLoader.getAttachFileName(this.documentAttach);
        localObject3 = FileLoader.getPathToAttach(this.documentAttach);
      }
    }
    while (TextUtils.isEmpty((CharSequence)localObject2))
    {
      this.radialProgress.setBackground(null, false, false);
      return;
      localObject3 = localStringBuilder;
      localObject2 = localObject1;
      if ((this.inlineResult.content instanceof TLRPC.TL_webDocument))
      {
        localStringBuilder = new StringBuilder().append(Utilities.MD5(this.inlineResult.content.url)).append(".");
        localObject2 = this.inlineResult.content.url;
        if (this.documentAttachType == 5) {}
        for (localObject3 = "mp3";; localObject3 = "ogg")
        {
          localObject2 = ImageLoader.getHttpUrlExtension((String)localObject2, (String)localObject3);
          localObject3 = new File(FileLoader.getDirectory(4), (String)localObject2);
          break;
        }
        localObject3 = localStringBuilder;
        localObject2 = localObject1;
        if (this.mediaWebpage) {
          if (this.inlineResult != null)
          {
            if ((this.inlineResult.document instanceof TLRPC.TL_document))
            {
              localObject2 = FileLoader.getAttachFileName(this.inlineResult.document);
              localObject3 = FileLoader.getPathToAttach(this.inlineResult.document);
            }
            else if ((this.inlineResult.photo instanceof TLRPC.TL_photo))
            {
              this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(this.inlineResult.photo.sizes, AndroidUtilities.getPhotoSize(), true);
              localObject2 = FileLoader.getAttachFileName(this.currentPhotoObject);
              localObject3 = FileLoader.getPathToAttach(this.currentPhotoObject);
            }
            else if ((this.inlineResult.content instanceof TLRPC.TL_webDocument))
            {
              localObject2 = Utilities.MD5(this.inlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, "jpg");
              localObject3 = new File(FileLoader.getDirectory(4), (String)localObject2);
            }
            else
            {
              localObject3 = localStringBuilder;
              localObject2 = localObject1;
              if ((this.inlineResult.thumb instanceof TLRPC.TL_webDocument))
              {
                localObject2 = Utilities.MD5(this.inlineResult.thumb.url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.thumb.url, "jpg");
                localObject3 = new File(FileLoader.getDirectory(4), (String)localObject2);
              }
            }
          }
          else
          {
            localObject3 = localStringBuilder;
            localObject2 = localObject1;
            if (this.documentAttach != null)
            {
              localObject2 = FileLoader.getAttachFileName(this.documentAttach);
              localObject3 = FileLoader.getPathToAttach(this.documentAttach);
            }
          }
        }
      }
    }
    boolean bool;
    if (!((File)localObject3).exists())
    {
      DownloadController.getInstance(this.currentAccount).addLoadingFileObserver((String)localObject2, this);
      if ((this.documentAttachType == 5) || (this.documentAttachType == 3)) {
        if (this.documentAttach != null)
        {
          bool = FileLoader.getInstance(this.currentAccount).isLoadingFile((String)localObject2);
          label570:
          if (bool) {
            break label622;
          }
          this.buttonState = 2;
          this.radialProgress.setProgress(0.0F, paramBoolean);
          this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
        }
      }
      for (;;)
      {
        invalidate();
        break;
        bool = ImageLoader.getInstance().isLoadingHttpFile((String)localObject2);
        break label570;
        label622:
        this.buttonState = 4;
        localObject3 = ImageLoader.getInstance().getFileProgress((String)localObject2);
        if (localObject3 != null) {
          this.radialProgress.setProgress(((Float)localObject3).floatValue(), paramBoolean);
        }
        for (;;)
        {
          this.radialProgress.setBackground(getDrawableForCurrentState(), true, paramBoolean);
          break;
          this.radialProgress.setProgress(0.0F, paramBoolean);
        }
        this.buttonState = 1;
        localObject3 = ImageLoader.getInstance().getFileProgress((String)localObject2);
        if (localObject3 != null) {
          f = ((Float)localObject3).floatValue();
        }
        this.radialProgress.setProgress(f, false);
        this.radialProgress.setBackground(getDrawableForCurrentState(), true, paramBoolean);
      }
    }
    DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    if ((this.documentAttachType == 5) || (this.documentAttachType == 3))
    {
      bool = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
      if ((!bool) || ((bool) && (MediaController.getInstance().isMessagePaused()))) {
        this.buttonState = 0;
      }
    }
    for (;;)
    {
      this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
      invalidate();
      break;
      this.buttonState = 1;
      continue;
      this.buttonState = -1;
    }
  }
  
  public static abstract interface ContextLinkCellDelegate
  {
    public abstract void didPressedImage(ContextLinkCell paramContextLinkCell);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/ContextLinkCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */