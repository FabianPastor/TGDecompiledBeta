package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RadialProgress;

public class AudioPlayerCell
  extends View
  implements DownloadController.FileDownloadProgressListener
{
  private int TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
  private boolean buttonPressed;
  private int buttonState;
  private int buttonX;
  private int buttonY;
  private int currentAccount = UserConfig.selectedAccount;
  private MessageObject currentMessageObject;
  private StaticLayout descriptionLayout;
  private int descriptionY = AndroidUtilities.dp(29.0F);
  private int hasMiniProgress;
  private boolean miniButtonPressed;
  private int miniButtonState;
  private RadialProgress radialProgress = new RadialProgress(this);
  private StaticLayout titleLayout;
  private int titleY = AndroidUtilities.dp(9.0F);
  
  public AudioPlayerCell(Context paramContext)
  {
    super(paramContext);
  }
  
  private boolean checkAudioMotionEvent(MotionEvent paramMotionEvent)
  {
    int i = (int)paramMotionEvent.getX();
    int j = (int)paramMotionEvent.getY();
    boolean bool1 = false;
    int k = AndroidUtilities.dp(36.0F);
    int m = 0;
    boolean bool2;
    if (this.miniButtonState >= 0)
    {
      m = AndroidUtilities.dp(27.0F);
      if ((i >= this.buttonX + m) && (i <= this.buttonX + m + k) && (j >= this.buttonY + m) && (j <= this.buttonY + m + k)) {
        m = 1;
      }
    }
    else
    {
      if (paramMotionEvent.getAction() != 0) {
        break label133;
      }
      bool2 = bool1;
      if (m != 0)
      {
        this.miniButtonPressed = true;
        invalidate();
        bool2 = true;
        updateRadialProgressBackground();
      }
    }
    label133:
    do
    {
      return bool2;
      m = 0;
      break;
      bool2 = bool1;
    } while (!this.miniButtonPressed);
    if (paramMotionEvent.getAction() == 1)
    {
      this.miniButtonPressed = false;
      playSoundEffect(0);
      didPressedMiniButton(true);
      invalidate();
    }
    for (;;)
    {
      updateRadialProgressBackground();
      bool2 = bool1;
      break;
      if (paramMotionEvent.getAction() == 3)
      {
        this.miniButtonPressed = false;
        invalidate();
      }
      else if ((paramMotionEvent.getAction() == 2) && (m == 0))
      {
        this.miniButtonPressed = false;
        invalidate();
      }
    }
  }
  
  private void didPressedMiniButton(boolean paramBoolean)
  {
    if (this.miniButtonState == 0)
    {
      this.miniButtonState = 1;
      this.radialProgress.setProgress(0.0F, false);
      FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), true, 0);
      this.radialProgress.setMiniBackground(getMiniDrawableForCurrentState(), true, false);
      invalidate();
    }
    for (;;)
    {
      return;
      if (this.miniButtonState == 1)
      {
        if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
          MediaController.getInstance().cleanupPlayer(true, true);
        }
        this.miniButtonState = 0;
        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
        this.radialProgress.setMiniBackground(getMiniDrawableForCurrentState(), true, false);
        invalidate();
      }
    }
  }
  
  private Drawable getDrawableForCurrentState()
  {
    int i = 0;
    if (this.buttonState == -1) {}
    for (Object localObject = null;; localObject = localObject[i])
    {
      return (Drawable)localObject;
      this.radialProgress.setAlphaForPrevious(false);
      localObject = Theme.chat_fileStatesDrawable[(this.buttonState + 5)];
      if (this.buttonPressed) {
        i = 1;
      }
    }
  }
  
  private Drawable getMiniDrawableForCurrentState()
  {
    int i = 0;
    if (this.miniButtonState < 0) {}
    for (Object localObject = null;; localObject = localObject[i])
    {
      return (Drawable)localObject;
      this.radialProgress.setAlphaForPrevious(false);
      localObject = Theme.chat_fileMiniStatesDrawable[(this.miniButtonState + 2)];
      if (this.miniButtonPressed) {
        i = 1;
      }
    }
  }
  
  private void updateRadialProgressBackground()
  {
    this.radialProgress.swapBackground(getDrawableForCurrentState());
    if (this.hasMiniProgress != 0) {
      this.radialProgress.swapMiniBackground(getMiniDrawableForCurrentState());
    }
  }
  
  public void didPressedButton()
  {
    if (this.buttonState == 0)
    {
      if (this.miniButtonState == 0) {
        FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), true, 0);
      }
      if (MediaController.getInstance().findMessageInPlaylistAndPlay(this.currentMessageObject))
      {
        if ((this.hasMiniProgress == 2) && (this.miniButtonState != 1))
        {
          this.miniButtonState = 1;
          this.radialProgress.setProgress(0.0F, false);
          this.radialProgress.setMiniBackground(getMiniDrawableForCurrentState(), true, false);
        }
        this.buttonState = 1;
        this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
        invalidate();
      }
    }
    for (;;)
    {
      return;
      if (this.buttonState == 1)
      {
        if (MediaController.getInstance().pauseMessage(this.currentMessageObject))
        {
          this.buttonState = 0;
          this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
          invalidate();
        }
      }
      else if (this.buttonState == 2)
      {
        this.radialProgress.setProgress(0.0F, false);
        FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), true, 0);
        this.buttonState = 4;
        this.radialProgress.setBackground(getDrawableForCurrentState(), true, false);
        invalidate();
      }
      else if (this.buttonState == 4)
      {
        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
        this.buttonState = 2;
        this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
        invalidate();
      }
    }
  }
  
  public MessageObject getMessageObject()
  {
    return this.currentMessageObject;
  }
  
  public int getObserverTag()
  {
    return this.TAG;
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    float f1 = 8.0F;
    float f2;
    label81:
    RadialProgress localRadialProgress;
    if (this.titleLayout != null)
    {
      paramCanvas.save();
      if (LocaleController.isRTL)
      {
        f2 = 8.0F;
        paramCanvas.translate(AndroidUtilities.dp(f2), this.titleY);
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
          break label151;
        }
        f2 = f1;
        paramCanvas.translate(AndroidUtilities.dp(f2), this.descriptionY);
        this.descriptionLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      localRadialProgress = this.radialProgress;
      if (!this.buttonPressed) {
        break label159;
      }
    }
    label151:
    label159:
    for (String str = "chat_inAudioSelectedProgress";; str = "chat_inAudioProgress")
    {
      localRadialProgress.setProgressColor(Theme.getColor(str));
      this.radialProgress.draw(paramCanvas);
      return;
      f2 = AndroidUtilities.leftBaseline;
      break;
      f2 = AndroidUtilities.leftBaseline;
      break label81;
    }
  }
  
  public void onFailedDownload(String paramString)
  {
    updateButtonState(false);
  }
  
  @SuppressLint({"DrawAllocation"})
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    this.descriptionLayout = null;
    this.titleLayout = null;
    paramInt2 = View.MeasureSpec.getSize(paramInt1) - AndroidUtilities.dp(AndroidUtilities.leftBaseline) - AndroidUtilities.dp(28.0F);
    try
    {
      localObject = this.currentMessageObject.getMusicTitle();
      i = (int)Math.ceil(Theme.chat_contextResult_titleTextPaint.measureText((String)localObject));
      localCharSequence = TextUtils.ellipsize(((String)localObject).replace('\n', ' '), Theme.chat_contextResult_titleTextPaint, Math.min(i, paramInt2), TextUtils.TruncateAt.END);
      localObject = new android/text/StaticLayout;
      ((StaticLayout)localObject).<init>(localCharSequence, Theme.chat_contextResult_titleTextPaint, paramInt2 + AndroidUtilities.dp(4.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      this.titleLayout = ((StaticLayout)localObject);
    }
    catch (Exception localException1)
    {
      try
      {
        for (;;)
        {
          Object localObject = this.currentMessageObject.getMusicAuthor();
          int i = (int)Math.ceil(Theme.chat_contextResult_descriptionTextPaint.measureText((String)localObject));
          CharSequence localCharSequence = TextUtils.ellipsize(((String)localObject).replace('\n', ' '), Theme.chat_contextResult_descriptionTextPaint, Math.min(i, paramInt2), TextUtils.TruncateAt.END);
          localObject = new android/text/StaticLayout;
          ((StaticLayout)localObject).<init>(localCharSequence, Theme.chat_contextResult_descriptionTextPaint, paramInt2 + AndroidUtilities.dp(4.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
          this.descriptionLayout = ((StaticLayout)localObject);
          setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), AndroidUtilities.dp(56.0F));
          paramInt2 = AndroidUtilities.dp(52.0F);
          if (!LocaleController.isRTL) {
            break;
          }
          paramInt1 = View.MeasureSpec.getSize(paramInt1) - AndroidUtilities.dp(8.0F) - paramInt2;
          localObject = this.radialProgress;
          paramInt2 = AndroidUtilities.dp(4.0F) + paramInt1;
          this.buttonX = paramInt2;
          i = AndroidUtilities.dp(6.0F);
          this.buttonY = i;
          ((RadialProgress)localObject).setProgressRect(paramInt2, i, AndroidUtilities.dp(48.0F) + paramInt1, AndroidUtilities.dp(50.0F));
          return;
          localException1 = localException1;
          FileLog.e(localException1);
        }
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          FileLog.e(localException2);
          continue;
          paramInt1 = AndroidUtilities.dp(8.0F);
        }
      }
    }
  }
  
  public void onProgressDownload(String paramString, float paramFloat)
  {
    this.radialProgress.setProgress(paramFloat, true);
    if (this.hasMiniProgress != 0) {
      if (this.miniButtonState != 1) {
        updateButtonState(false);
      }
    }
    for (;;)
    {
      return;
      if (this.buttonState != 4) {
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
    boolean bool;
    if (this.currentMessageObject == null) {
      bool = super.onTouchEvent(paramMotionEvent);
    }
    for (;;)
    {
      return bool;
      bool = checkAudioMotionEvent(paramMotionEvent);
      if (paramMotionEvent.getAction() == 3)
      {
        this.miniButtonPressed = false;
        this.buttonPressed = false;
        bool = false;
      }
    }
  }
  
  public void setMessageObject(MessageObject paramMessageObject)
  {
    this.currentMessageObject = paramMessageObject;
    requestLayout();
    updateButtonState(false);
  }
  
  public void updateButtonState(boolean paramBoolean)
  {
    String str = this.currentMessageObject.getFileName();
    Object localObject1 = null;
    if (!TextUtils.isEmpty(this.currentMessageObject.messageOwner.attachPath))
    {
      localObject2 = new File(this.currentMessageObject.messageOwner.attachPath);
      localObject1 = localObject2;
      if (!((File)localObject2).exists()) {
        localObject1 = null;
      }
    }
    Object localObject2 = localObject1;
    if (localObject1 == null) {
      localObject2 = FileLoader.getPathToAttach(this.currentMessageObject.getDocument());
    }
    if (TextUtils.isEmpty(str)) {
      this.radialProgress.setBackground(null, false, false);
    }
    for (;;)
    {
      return;
      if ((((File)localObject2).exists()) && (((File)localObject2).length() == 0L)) {
        ((File)localObject2).delete();
      }
      boolean bool1 = ((File)localObject2).exists();
      boolean bool2 = bool1;
      int i;
      if (SharedConfig.streamMedia)
      {
        bool2 = bool1;
        if ((int)this.currentMessageObject.getDialogId() != 0)
        {
          if (!bool1) {
            break label311;
          }
          i = 1;
          label159:
          this.hasMiniProgress = i;
          bool2 = true;
        }
      }
      if (this.hasMiniProgress != 0)
      {
        localObject2 = this.radialProgress;
        if (this.currentMessageObject.isOutOwner())
        {
          localObject1 = "chat_outLoader";
          label195:
          ((RadialProgress)localObject2).setMiniProgressBackgroundColor(Theme.getColor((String)localObject1));
          bool2 = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
          if ((bool2) && ((!bool2) || (!MediaController.getInstance().isMessagePaused()))) {
            break label324;
          }
          this.buttonState = 0;
          label240:
          this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
          if (this.hasMiniProgress != 1) {
            break label332;
          }
          DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
          this.miniButtonState = -1;
          label277:
          localObject2 = this.radialProgress;
          localObject1 = getMiniDrawableForCurrentState();
          if (this.miniButtonState != 1) {
            break label423;
          }
        }
        label311:
        label324:
        label332:
        label423:
        for (bool2 = true;; bool2 = false)
        {
          ((RadialProgress)localObject2).setMiniBackground((Drawable)localObject1, bool2, paramBoolean);
          break;
          i = 2;
          break label159;
          localObject1 = "chat_inLoader";
          break label195;
          this.buttonState = 1;
          break label240;
          DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(str, this.currentMessageObject, this);
          if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(str))
          {
            this.radialProgress.setProgress(0.0F, paramBoolean);
            this.miniButtonState = 0;
            break label277;
          }
          this.miniButtonState = 1;
          localObject1 = ImageLoader.getInstance().getFileProgress(str);
          if (localObject1 != null)
          {
            this.radialProgress.setProgress(((Float)localObject1).floatValue(), paramBoolean);
            break label277;
          }
          this.radialProgress.setProgress(0.0F, paramBoolean);
          break label277;
        }
      }
      if (bool2)
      {
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
        bool2 = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
        if ((!bool2) || ((bool2) && (MediaController.getInstance().isMessagePaused()))) {}
        for (this.buttonState = 0;; this.buttonState = 1)
        {
          this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
          invalidate();
          break;
        }
      }
      DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(str, this);
      if (FileLoader.getInstance(this.currentAccount).isLoadingFile(str)) {
        break;
      }
      this.buttonState = 2;
      this.radialProgress.setProgress(0.0F, paramBoolean);
      this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
      invalidate();
    }
    this.buttonState = 4;
    localObject1 = ImageLoader.getInstance().getFileProgress(str);
    if (localObject1 != null) {
      this.radialProgress.setProgress(((Float)localObject1).floatValue(), paramBoolean);
    }
    for (;;)
    {
      this.radialProgress.setBackground(getDrawableForCurrentState(), true, paramBoolean);
      break;
      this.radialProgress.setProgress(0.0F, paramBoolean);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/AudioPlayerCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */