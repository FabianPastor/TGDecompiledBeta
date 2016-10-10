package org.telegram.ui.Components;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.view.ViewParent;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.FileDownloadProgressListener;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.ui.Cells.BaseCell;

public class PopupAudioView
  extends BaseCell
  implements SeekBar.SeekBarDelegate, MediaController.FileDownloadProgressListener
{
  private static Drawable backgroundMediaDrawableIn;
  private static Drawable[][] statesDrawable = (Drawable[][])Array.newInstance(Drawable.class, new int[] { 8, 2 });
  private static TextPaint timePaint;
  private int TAG;
  private int buttonPressed = 0;
  private int buttonState = 0;
  private int buttonX;
  private int buttonY;
  protected MessageObject currentMessageObject;
  private String lastTimeString = null;
  private ProgressView progressView;
  private SeekBar seekBar;
  private int seekBarX;
  private int seekBarY;
  private StaticLayout timeLayout;
  int timeWidth = 0;
  private int timeX;
  private boolean wasLayout = false;
  
  public PopupAudioView(Context paramContext)
  {
    super(paramContext);
    if (backgroundMediaDrawableIn == null)
    {
      backgroundMediaDrawableIn = getResources().getDrawable(2130837841);
      statesDrawable[0][0] = getResources().getDrawable(2130837934);
      statesDrawable[0][1] = getResources().getDrawable(2130837935);
      statesDrawable[1][0] = getResources().getDrawable(2130837875);
      statesDrawable[1][1] = getResources().getDrawable(2130837876);
      statesDrawable[2][0] = getResources().getDrawable(2130837672);
      statesDrawable[2][1] = getResources().getDrawable(2130837673);
      statesDrawable[3][0] = getResources().getDrawable(2130837670);
      statesDrawable[3][1] = getResources().getDrawable(2130837671);
      statesDrawable[4][0] = getResources().getDrawable(2130837931);
      statesDrawable[4][1] = getResources().getDrawable(2130837932);
      statesDrawable[5][0] = getResources().getDrawable(2130837873);
      statesDrawable[5][1] = getResources().getDrawable(2130837874);
      statesDrawable[6][0] = getResources().getDrawable(2130837666);
      statesDrawable[6][1] = getResources().getDrawable(2130837667);
      statesDrawable[7][0] = getResources().getDrawable(2130837664);
      statesDrawable[7][1] = getResources().getDrawable(2130837665);
      timePaint = new TextPaint(1);
    }
    timePaint.setTextSize(AndroidUtilities.dp(16.0F));
    this.TAG = MediaController.getInstance().generateObserverTag();
    this.seekBar = new SeekBar(getContext());
    this.seekBar.setDelegate(this);
    this.progressView = new ProgressView();
  }
  
  private void didPressedButton()
  {
    if (this.buttonState == 0)
    {
      boolean bool = MediaController.getInstance().playAudio(this.currentMessageObject);
      if ((!this.currentMessageObject.isOut()) && (this.currentMessageObject.isContentUnread()) && (this.currentMessageObject.messageOwner.to_id.channel_id == 0))
      {
        MessagesController.getInstance().markMessageContentAsRead(this.currentMessageObject);
        this.currentMessageObject.setContentIsRead();
      }
      if (bool)
      {
        this.buttonState = 1;
        invalidate();
      }
    }
    do
    {
      do
      {
        return;
        if (this.buttonState != 1) {
          break;
        }
      } while (!MediaController.getInstance().pauseAudio(this.currentMessageObject));
      this.buttonState = 0;
      invalidate();
      return;
      if (this.buttonState == 2)
      {
        FileLoader.getInstance().loadFile(this.currentMessageObject.getDocument(), true, false);
        this.buttonState = 3;
        invalidate();
        return;
      }
    } while (this.buttonState != 3);
    FileLoader.getInstance().cancelLoadFile(this.currentMessageObject.getDocument());
    this.buttonState = 2;
    invalidate();
  }
  
  public void downloadAudioIfNeed()
  {
    if (this.buttonState == 2)
    {
      FileLoader.getInstance().loadFile(this.currentMessageObject.getDocument(), true, false);
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
    MediaController.getInstance().removeLoadingFileObserver(this);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.currentMessageObject == null) {}
    do
    {
      return;
      if (!this.wasLayout)
      {
        requestLayout();
        return;
      }
      setDrawableBounds(backgroundMediaDrawableIn, 0, 0, getMeasuredWidth(), getMeasuredHeight());
      backgroundMediaDrawableIn.draw(paramCanvas);
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
      timePaint.setColor(-6182221);
      Drawable localDrawable = statesDrawable[(i + 4)][this.buttonPressed];
      int j = AndroidUtilities.dp(36.0F);
      i = (j - localDrawable.getIntrinsicWidth()) / 2;
      j = (j - localDrawable.getIntrinsicHeight()) / 2;
      setDrawableBounds(localDrawable, this.buttonX + i, this.buttonY + j);
      localDrawable.draw(paramCanvas);
      paramCanvas.save();
      paramCanvas.translate(this.timeX, AndroidUtilities.dp(18.0F));
      this.timeLayout.draw(paramCanvas);
      paramCanvas.restore();
      return;
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
    if (this.currentMessageObject == null) {
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    do
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
    } while ((!paramBoolean) && (this.wasLayout));
    this.wasLayout = true;
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
    if (this.currentMessageObject == null) {
      return;
    }
    this.currentMessageObject.audioProgress = paramFloat;
    MediaController.getInstance().seekToProgress(this.currentMessageObject, paramFloat);
  }
  
  public void onSuccessDownload(String paramString)
  {
    updateButtonState();
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    boolean bool2 = this.seekBar.onTouch(paramMotionEvent.getAction(), paramMotionEvent.getX() - this.seekBarX, paramMotionEvent.getY() - this.seekBarY);
    if (bool2)
    {
      if (paramMotionEvent.getAction() == 0) {
        getParent().requestDisallowInterceptTouchEvent(true);
      }
      invalidate();
      return bool2;
    }
    int i = AndroidUtilities.dp(36.0F);
    boolean bool1;
    if (paramMotionEvent.getAction() == 0)
    {
      bool1 = bool2;
      if (f1 >= this.buttonX)
      {
        bool1 = bool2;
        if (f1 <= this.buttonX + i)
        {
          bool1 = bool2;
          if (f2 >= this.buttonY)
          {
            bool1 = bool2;
            if (f2 <= this.buttonY + i)
            {
              this.buttonPressed = 1;
              invalidate();
              bool1 = true;
            }
          }
        }
      }
    }
    for (;;)
    {
      bool2 = bool1;
      if (bool1) {
        break;
      }
      return super.onTouchEvent(paramMotionEvent);
      bool1 = bool2;
      if (this.buttonPressed == 1) {
        if (paramMotionEvent.getAction() == 1)
        {
          this.buttonPressed = 0;
          playSoundEffect(0);
          didPressedButton();
          invalidate();
          bool1 = bool2;
        }
        else if (paramMotionEvent.getAction() == 3)
        {
          this.buttonPressed = 0;
          invalidate();
          bool1 = bool2;
        }
        else
        {
          bool1 = bool2;
          if (paramMotionEvent.getAction() == 2) {
            if ((f1 >= this.buttonX) && (f1 <= this.buttonX + i) && (f2 >= this.buttonY))
            {
              bool1 = bool2;
              if (f2 <= this.buttonY + i) {}
            }
            else
            {
              this.buttonPressed = 0;
              invalidate();
              bool1 = bool2;
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
      this.seekBar.setColors(-1774864, -9259544, -4399384);
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
      MediaController.getInstance().removeLoadingFileObserver(this);
      boolean bool = MediaController.getInstance().isPlayingAudio(this.currentMessageObject);
      if ((!bool) || ((bool) && (MediaController.getInstance().isAudioPaused())))
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
      MediaController.getInstance().addLoadingFileObserver((String)localObject, this);
      if (!FileLoader.getInstance().isLoadingFile((String)localObject))
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
    int k = 0;
    int j;
    Object localObject;
    if (!MediaController.getInstance().isPlayingAudio(this.currentMessageObject))
    {
      j = 0;
      i = k;
      if (j < this.currentMessageObject.getDocument().attributes.size())
      {
        localObject = (TLRPC.DocumentAttribute)this.currentMessageObject.getDocument().attributes.get(j);
        if (!(localObject instanceof TLRPC.TL_documentAttributeAudio)) {
          break label207;
        }
      }
    }
    for (int i = ((TLRPC.DocumentAttribute)localObject).duration;; i = this.currentMessageObject.audioProgressSec)
    {
      localObject = String.format("%02d:%02d", new Object[] { Integer.valueOf(i / 60), Integer.valueOf(i % 60) });
      if ((this.lastTimeString == null) || ((this.lastTimeString != null) && (!this.lastTimeString.equals(localObject))))
      {
        this.timeWidth = ((int)Math.ceil(timePaint.measureText((String)localObject)));
        this.timeLayout = new StaticLayout((CharSequence)localObject, timePaint, this.timeWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      }
      invalidate();
      return;
      label207:
      j += 1;
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/PopupAudioView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */