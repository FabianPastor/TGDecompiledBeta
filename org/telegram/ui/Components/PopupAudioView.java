package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.view.ViewParent;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.BaseCell;

public class PopupAudioView
  extends BaseCell
  implements DownloadController.FileDownloadProgressListener, SeekBar.SeekBarDelegate
{
  private int TAG;
  private int buttonPressed = 0;
  private int buttonState = 0;
  private int buttonX;
  private int buttonY;
  private int currentAccount;
  protected MessageObject currentMessageObject;
  private String lastTimeString = null;
  private ProgressView progressView;
  private SeekBar seekBar;
  private int seekBarX;
  private int seekBarY;
  private StaticLayout timeLayout;
  private TextPaint timePaint = new TextPaint(1);
  int timeWidth = 0;
  private int timeX;
  private boolean wasLayout = false;
  
  public PopupAudioView(Context paramContext)
  {
    super(paramContext);
    this.timePaint.setTextSize(AndroidUtilities.dp(16.0F));
    this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
    this.seekBar = new SeekBar(getContext());
    this.seekBar.setDelegate(this);
    this.progressView = new ProgressView();
  }
  
  private void didPressedButton()
  {
    if (this.buttonState == 0)
    {
      boolean bool = MediaController.getInstance().playMessage(this.currentMessageObject);
      if ((!this.currentMessageObject.isOut()) && (this.currentMessageObject.isContentUnread()) && (this.currentMessageObject.messageOwner.to_id.channel_id == 0))
      {
        MessagesController.getInstance(this.currentAccount).markMessageContentAsRead(this.currentMessageObject);
        this.currentMessageObject.setContentIsRead();
      }
      if (bool)
      {
        this.buttonState = 1;
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
          invalidate();
        }
      }
      else if (this.buttonState == 2)
      {
        FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), true, 0);
        this.buttonState = 4;
        invalidate();
      }
      else if (this.buttonState == 3)
      {
        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
        this.buttonState = 2;
        invalidate();
      }
    }
  }
  
  public void downloadAudioIfNeed()
  {
    if (this.buttonState == 2)
    {
      FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), true, 0);
      this.buttonState = 3;
      invalidate();
    }
  }
  
  public final MessageObject getMessageObject()
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
    if (this.currentMessageObject == null) {}
    do
    {
      for (;;)
      {
        return;
        if (this.wasLayout) {
          break;
        }
        requestLayout();
      }
      setDrawableBounds(Theme.chat_msgInMediaDrawable, 0, 0, getMeasuredWidth(), getMeasuredHeight());
      Theme.chat_msgInMediaDrawable.draw(paramCanvas);
    } while (this.currentMessageObject == null);
    paramCanvas.save();
    if ((this.buttonState == 0) || (this.buttonState == 1))
    {
      paramCanvas.translate(this.seekBarX, this.seekBarY);
      this.seekBar.draw(paramCanvas);
    }
    for (;;)
    {
      paramCanvas.restore();
      int i = this.buttonState;
      this.timePaint.setColor(-6182221);
      Drawable localDrawable = Theme.chat_fileStatesDrawable[(i + 5)][this.buttonPressed];
      int j = AndroidUtilities.dp(36.0F);
      i = (j - localDrawable.getIntrinsicWidth()) / 2;
      j = (j - localDrawable.getIntrinsicHeight()) / 2;
      setDrawableBounds(localDrawable, this.buttonX + i, this.buttonY + j);
      localDrawable.draw(paramCanvas);
      paramCanvas.save();
      paramCanvas.translate(this.timeX, AndroidUtilities.dp(18.0F));
      this.timeLayout.draw(paramCanvas);
      paramCanvas.restore();
      break;
      paramCanvas.translate(this.seekBarX + AndroidUtilities.dp(12.0F), this.seekBarY);
      this.progressView.draw(paramCanvas);
    }
  }
  
  public void onFailedDownload(String paramString)
  {
    updateButtonState();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.currentMessageObject == null) {}
    for (;;)
    {
      return;
      this.seekBarX = AndroidUtilities.dp(54.0F);
      this.buttonX = AndroidUtilities.dp(10.0F);
      this.timeX = (getMeasuredWidth() - this.timeWidth - AndroidUtilities.dp(16.0F));
      this.seekBar.setSize(getMeasuredWidth() - AndroidUtilities.dp(70.0F) - this.timeWidth, AndroidUtilities.dp(30.0F));
      this.progressView.width = (getMeasuredWidth() - AndroidUtilities.dp(94.0F) - this.timeWidth);
      this.progressView.height = AndroidUtilities.dp(30.0F);
      this.seekBarY = AndroidUtilities.dp(13.0F);
      this.buttonY = AndroidUtilities.dp(10.0F);
      updateProgress();
      if ((paramBoolean) || (!this.wasLayout)) {
        this.wasLayout = true;
      }
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), AndroidUtilities.dp(56.0F));
  }
  
  public void onProgressDownload(String paramString, float paramFloat)
  {
    this.progressView.setProgress(paramFloat);
    if (this.buttonState != 3) {
      updateButtonState();
    }
    invalidate();
  }
  
  public void onProgressUpload(String paramString, float paramFloat, boolean paramBoolean) {}
  
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
    updateButtonState();
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    boolean bool1 = this.seekBar.onTouch(paramMotionEvent.getAction(), paramMotionEvent.getX() - this.seekBarX, paramMotionEvent.getY() - this.seekBarY);
    if (bool1)
    {
      if (paramMotionEvent.getAction() == 0) {
        getParent().requestDisallowInterceptTouchEvent(true);
      }
      invalidate();
      return bool1;
    }
    int i = AndroidUtilities.dp(36.0F);
    boolean bool2;
    if (paramMotionEvent.getAction() == 0)
    {
      bool2 = bool1;
      if (f1 >= this.buttonX)
      {
        bool2 = bool1;
        if (f1 <= this.buttonX + i)
        {
          bool2 = bool1;
          if (f2 >= this.buttonY)
          {
            bool2 = bool1;
            if (f2 <= this.buttonY + i)
            {
              this.buttonPressed = 1;
              invalidate();
              bool2 = true;
            }
          }
        }
      }
    }
    for (;;)
    {
      bool1 = bool2;
      if (bool2) {
        break;
      }
      bool1 = super.onTouchEvent(paramMotionEvent);
      break;
      bool2 = bool1;
      if (this.buttonPressed == 1) {
        if (paramMotionEvent.getAction() == 1)
        {
          this.buttonPressed = 0;
          playSoundEffect(0);
          didPressedButton();
          invalidate();
          bool2 = bool1;
        }
        else if (paramMotionEvent.getAction() == 3)
        {
          this.buttonPressed = 0;
          invalidate();
          bool2 = bool1;
        }
        else
        {
          bool2 = bool1;
          if (paramMotionEvent.getAction() == 2) {
            if ((f1 >= this.buttonX) && (f1 <= this.buttonX + i) && (f2 >= this.buttonY))
            {
              bool2 = bool1;
              if (f2 <= this.buttonY + i) {}
            }
            else
            {
              this.buttonPressed = 0;
              invalidate();
              bool2 = bool1;
            }
          }
        }
      }
    }
  }
  
  public void setMessageObject(MessageObject paramMessageObject)
  {
    if (this.currentMessageObject != paramMessageObject)
    {
      this.currentAccount = paramMessageObject.currentAccount;
      this.seekBar.setColors(Theme.getColor("chat_inAudioSeekbar"), Theme.getColor("chat_inAudioSeekbar"), Theme.getColor("chat_inAudioSeekbarFill"), Theme.getColor("chat_inAudioSeekbarFill"), Theme.getColor("chat_inAudioSeekbarSelected"));
      this.progressView.setProgressColors(-2497813, -7944712);
      this.currentMessageObject = paramMessageObject;
      this.wasLayout = false;
      requestLayout();
    }
    updateButtonState();
  }
  
  public void updateButtonState()
  {
    Object localObject = this.currentMessageObject.getFileName();
    if (FileLoader.getPathToMessage(this.currentMessageObject.messageOwner).exists())
    {
      DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
      boolean bool = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
      if ((!bool) || ((bool) && (MediaController.getInstance().isMessagePaused())))
      {
        this.buttonState = 0;
        this.progressView.setProgress(0.0F);
      }
    }
    for (;;)
    {
      updateProgress();
      return;
      this.buttonState = 1;
      break;
      DownloadController.getInstance(this.currentAccount).addLoadingFileObserver((String)localObject, this);
      if (!FileLoader.getInstance(this.currentAccount).isLoadingFile((String)localObject))
      {
        this.buttonState = 2;
        this.progressView.setProgress(0.0F);
      }
      else
      {
        this.buttonState = 3;
        localObject = ImageLoader.getInstance().getFileProgress((String)localObject);
        if (localObject != null) {
          this.progressView.setProgress(((Float)localObject).floatValue());
        } else {
          this.progressView.setProgress(0.0F);
        }
      }
    }
  }
  
  public void updateProgress()
  {
    if (this.currentMessageObject == null) {
      return;
    }
    if (!this.seekBar.isDragging()) {
      this.seekBar.setProgress(this.currentMessageObject.audioProgress);
    }
    int i = 0;
    int j;
    label49:
    Object localObject;
    if (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject))
    {
      j = 0;
      k = i;
      if (j < this.currentMessageObject.getDocument().attributes.size())
      {
        localObject = (TLRPC.DocumentAttribute)this.currentMessageObject.getDocument().attributes.get(j);
        if (!(localObject instanceof TLRPC.TL_documentAttributeAudio)) {
          break label211;
        }
      }
    }
    for (int k = ((TLRPC.DocumentAttribute)localObject).duration;; k = this.currentMessageObject.audioProgressSec)
    {
      localObject = String.format("%02d:%02d", new Object[] { Integer.valueOf(k / 60), Integer.valueOf(k % 60) });
      if ((this.lastTimeString == null) || ((this.lastTimeString != null) && (!this.lastTimeString.equals(localObject))))
      {
        this.timeWidth = ((int)Math.ceil(this.timePaint.measureText((String)localObject)));
        this.timeLayout = new StaticLayout((CharSequence)localObject, this.timePaint, this.timeWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      }
      invalidate();
      break;
      label211:
      j++;
      break label49;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/PopupAudioView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */