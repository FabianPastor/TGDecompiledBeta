package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.FileDownloadProgressListener;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LineProgressView;

public class AudioPlayerActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate, MediaController.FileDownloadProgressListener
{
  private int TAG;
  private ImageView[] buttons = new ImageView[5];
  private TextView durationTextView;
  private MessageObject lastMessageObject;
  private String lastTimeString;
  private ImageView nextButton;
  private ImageView placeholder;
  private ImageView playButton;
  private ImageView prevButton;
  private LineProgressView progressView;
  private ImageView repeatButton;
  private SeekBarView seekBarView;
  private ImageView shuffleButton;
  private TextView timeTextView;
  
  private void checkIfMusicDownloaded(MessageObject paramMessageObject)
  {
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (paramMessageObject.messageOwner.attachPath != null)
    {
      localObject1 = localObject2;
      if (paramMessageObject.messageOwner.attachPath.length() > 0)
      {
        localObject2 = new File(paramMessageObject.messageOwner.attachPath);
        localObject1 = localObject2;
        if (!((File)localObject2).exists()) {
          localObject1 = null;
        }
      }
    }
    localObject2 = localObject1;
    if (localObject1 == null) {
      localObject2 = FileLoader.getPathToMessage(paramMessageObject.messageOwner);
    }
    if (!((File)localObject2).exists())
    {
      paramMessageObject = paramMessageObject.getFileName();
      MediaController.getInstance().addLoadingFileObserver(paramMessageObject, this);
      paramMessageObject = ImageLoader.getInstance().getFileProgress(paramMessageObject);
      localObject1 = this.progressView;
      if (paramMessageObject != null) {}
      for (float f = paramMessageObject.floatValue();; f = 0.0F)
      {
        ((LineProgressView)localObject1).setProgress(f, false);
        this.progressView.setVisibility(0);
        this.seekBarView.setVisibility(4);
        this.playButton.setEnabled(false);
        return;
      }
    }
    MediaController.getInstance().removeLoadingFileObserver(this);
    this.progressView.setVisibility(4);
    this.seekBarView.setVisibility(0);
    this.playButton.setEnabled(true);
  }
  
  private void onSeekBarDrag(float paramFloat)
  {
    MediaController.getInstance().seekToProgress(MediaController.getInstance().getPlayingMessageObject(), paramFloat);
  }
  
  private void updateProgress(MessageObject paramMessageObject)
  {
    if (this.seekBarView != null)
    {
      if (!this.seekBarView.isDragging()) {
        this.seekBarView.setProgress(paramMessageObject.audioProgress);
      }
      paramMessageObject = String.format("%d:%02d", new Object[] { Integer.valueOf(paramMessageObject.audioProgressSec / 60), Integer.valueOf(paramMessageObject.audioProgressSec % 60) });
      if ((this.lastTimeString == null) || ((this.lastTimeString != null) && (!this.lastTimeString.equals(paramMessageObject))))
      {
        this.lastTimeString = paramMessageObject;
        this.timeTextView.setText(paramMessageObject);
      }
    }
  }
  
  private void updateRepeatButton()
  {
    int i = MediaController.getInstance().getRepeatMode();
    if (i == 0) {
      this.repeatButton.setImageResource(2130837925);
    }
    do
    {
      return;
      if (i == 1)
      {
        this.repeatButton.setImageResource(2130837927);
        return;
      }
    } while (i != 2);
    this.repeatButton.setImageResource(2130837926);
  }
  
  private void updateShuffleButton()
  {
    if (MediaController.getInstance().isShuffleMusic())
    {
      this.shuffleButton.setImageResource(2130837929);
      return;
    }
    this.shuffleButton.setImageResource(2130837928);
  }
  
  private void updateTitle(boolean paramBoolean)
  {
    Object localObject1 = MediaController.getInstance().getPlayingMessageObject();
    if (((localObject1 == null) && (paramBoolean)) || ((localObject1 != null) && (!((MessageObject)localObject1).isMusic()))) {
      if ((this.parentLayout != null) && (!this.parentLayout.fragmentsStack.isEmpty()) && (this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1) == this)) {
        finishFragment();
      }
    }
    while (localObject1 == null)
    {
      return;
      removeSelfFromStack();
      return;
    }
    checkIfMusicDownloaded((MessageObject)localObject1);
    updateProgress((MessageObject)localObject1);
    label122:
    Object localObject2;
    label233:
    int j;
    int i;
    if (MediaController.getInstance().isAudioPaused())
    {
      this.playButton.setImageResource(2130837939);
      if (this.actionBar != null)
      {
        this.actionBar.setTitle(((MessageObject)localObject1).getMusicTitle());
        this.actionBar.getTitleTextView().setTextColor(-14606047);
        this.actionBar.setSubtitle(((MessageObject)localObject1).getMusicAuthor());
        this.actionBar.getSubtitleTextView().setTextColor(-7697782);
      }
      localObject2 = MediaController.getInstance().getAudioInfo();
      if ((localObject2 == null) || (((AudioInfo)localObject2).getCover() == null)) {
        break label365;
      }
      this.placeholder.setImageBitmap(((AudioInfo)localObject2).getCover());
      this.placeholder.setPadding(0, 0, 0, 0);
      this.placeholder.setScaleType(ImageView.ScaleType.CENTER_CROP);
      if (this.durationTextView == null) {
        break label402;
      }
      int k = 0;
      localObject1 = ((MessageObject)localObject1).getDocument();
      j = k;
      if (localObject1 != null)
      {
        i = 0;
        label260:
        j = k;
        if (i < ((TLRPC.Document)localObject1).attributes.size())
        {
          localObject2 = (TLRPC.DocumentAttribute)((TLRPC.Document)localObject1).attributes.get(i);
          if (!(localObject2 instanceof TLRPC.TL_documentAttributeAudio)) {
            break label404;
          }
          j = ((TLRPC.DocumentAttribute)localObject2).duration;
        }
      }
      localObject2 = this.durationTextView;
      if (j == 0) {
        break label411;
      }
    }
    label365:
    label402:
    label404:
    label411:
    for (localObject1 = String.format("%d:%02d", new Object[] { Integer.valueOf(j / 60), Integer.valueOf(j % 60) });; localObject1 = "-:--")
    {
      ((TextView)localObject2).setText((CharSequence)localObject1);
      return;
      this.playButton.setImageResource(2130837938);
      break label122;
      this.placeholder.setImageResource(2130837855);
      this.placeholder.setPadding(0, 0, 0, AndroidUtilities.dp(30.0F));
      this.placeholder.setScaleType(ImageView.ScaleType.CENTER);
      break label233;
      break;
      i += 1;
      break label260;
    }
  }
  
  public View createView(Context paramContext)
  {
    FrameLayout localFrameLayout = new FrameLayout(paramContext);
    localFrameLayout.setBackgroundColor(-986896);
    localFrameLayout.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    this.fragmentView = localFrameLayout;
    this.actionBar.setBackgroundColor(-1);
    this.actionBar.setBackButtonImage(2130837916);
    this.actionBar.setItemsBackgroundColor(788529152);
    if (!AndroidUtilities.isTablet()) {
      this.actionBar.showActionModeTop();
    }
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          AudioPlayerActivity.this.finishFragment();
        }
      }
    });
    this.placeholder = new ImageView(paramContext);
    localFrameLayout.addView(this.placeholder, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 0.0F, 0.0F, 66.0F));
    Object localObject = new View(paramContext);
    ((View)localObject).setBackgroundResource(2130837694);
    localFrameLayout.addView((View)localObject, LayoutHelper.createFrame(-1, 3.0F, 83, 0.0F, 0.0F, 0.0F, 96.0F));
    localObject = new FrameLayout(paramContext);
    ((FrameLayout)localObject).setBackgroundColor(-436207617);
    localFrameLayout.addView((View)localObject, LayoutHelper.createFrame(-1, 30.0F, 83, 0.0F, 0.0F, 0.0F, 66.0F));
    this.timeTextView = new TextView(paramContext);
    this.timeTextView.setTextSize(1, 12.0F);
    this.timeTextView.setTextColor(-15095832);
    this.timeTextView.setGravity(17);
    this.timeTextView.setText("0:00");
    ((FrameLayout)localObject).addView(this.timeTextView, LayoutHelper.createFrame(44, -1, 51));
    this.durationTextView = new TextView(paramContext);
    this.durationTextView.setTextSize(1, 12.0F);
    this.durationTextView.setTextColor(-7697782);
    this.durationTextView.setGravity(17);
    this.durationTextView.setText("3:00");
    ((FrameLayout)localObject).addView(this.durationTextView, LayoutHelper.createFrame(44, -1, 53));
    this.seekBarView = new SeekBarView(paramContext);
    ((FrameLayout)localObject).addView(this.seekBarView, LayoutHelper.createFrame(-1, -1.0F, 51, 32.0F, 0.0F, 32.0F, 0.0F));
    this.progressView = new LineProgressView(paramContext);
    this.progressView.setVisibility(4);
    this.progressView.setBackgroundColor(419430400);
    this.progressView.setProgressColor(-14438417);
    ((FrameLayout)localObject).addView(this.progressView, LayoutHelper.createFrame(-1, 2.0F, 19, 44.0F, 0.0F, 44.0F, 0.0F));
    localObject = new FrameLayout(paramContext)
    {
      protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
      {
        paramAnonymousInt2 = (paramAnonymousInt3 - paramAnonymousInt1 - AndroidUtilities.dp(270.0F)) / 4;
        paramAnonymousInt1 = 0;
        while (paramAnonymousInt1 < 5)
        {
          paramAnonymousInt3 = AndroidUtilities.dp(paramAnonymousInt1 * 48 + 15) + paramAnonymousInt2 * paramAnonymousInt1;
          paramAnonymousInt4 = AndroidUtilities.dp(9.0F);
          AudioPlayerActivity.this.buttons[paramAnonymousInt1].layout(paramAnonymousInt3, paramAnonymousInt4, AudioPlayerActivity.this.buttons[paramAnonymousInt1].getMeasuredWidth() + paramAnonymousInt3, AudioPlayerActivity.this.buttons[paramAnonymousInt1].getMeasuredHeight() + paramAnonymousInt4);
          paramAnonymousInt1 += 1;
        }
      }
    };
    ((FrameLayout)localObject).setBackgroundColor(-1);
    localFrameLayout.addView((View)localObject, LayoutHelper.createFrame(-1, 66, 83));
    ImageView[] arrayOfImageView = this.buttons;
    ImageView localImageView = new ImageView(paramContext);
    this.repeatButton = localImageView;
    arrayOfImageView[0] = localImageView;
    this.repeatButton.setScaleType(ImageView.ScaleType.CENTER);
    ((FrameLayout)localObject).addView(this.repeatButton, LayoutHelper.createFrame(48, 48, 51));
    this.repeatButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        MediaController.getInstance().toggleRepeatMode();
        AudioPlayerActivity.this.updateRepeatButton();
      }
    });
    arrayOfImageView = this.buttons;
    localImageView = new ImageView(paramContext);
    this.prevButton = localImageView;
    arrayOfImageView[1] = localImageView;
    this.prevButton.setScaleType(ImageView.ScaleType.CENTER);
    this.prevButton.setImageResource(2130837940);
    ((FrameLayout)localObject).addView(this.prevButton, LayoutHelper.createFrame(48, 48, 51));
    this.prevButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        MediaController.getInstance().playPreviousMessage();
      }
    });
    arrayOfImageView = this.buttons;
    localImageView = new ImageView(paramContext);
    this.playButton = localImageView;
    arrayOfImageView[2] = localImageView;
    this.playButton.setScaleType(ImageView.ScaleType.CENTER);
    this.playButton.setImageResource(2130837939);
    ((FrameLayout)localObject).addView(this.playButton, LayoutHelper.createFrame(48, 48, 51));
    this.playButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (MediaController.getInstance().isDownloadingCurrentMessage()) {
          return;
        }
        if (MediaController.getInstance().isAudioPaused())
        {
          MediaController.getInstance().playAudio(MediaController.getInstance().getPlayingMessageObject());
          return;
        }
        MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingMessageObject());
      }
    });
    arrayOfImageView = this.buttons;
    localImageView = new ImageView(paramContext);
    this.nextButton = localImageView;
    arrayOfImageView[3] = localImageView;
    this.nextButton.setScaleType(ImageView.ScaleType.CENTER);
    this.nextButton.setImageResource(2130837937);
    ((FrameLayout)localObject).addView(this.nextButton, LayoutHelper.createFrame(48, 48, 51));
    this.nextButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        MediaController.getInstance().playNextMessage();
      }
    });
    arrayOfImageView = this.buttons;
    paramContext = new ImageView(paramContext);
    this.shuffleButton = paramContext;
    arrayOfImageView[4] = paramContext;
    this.shuffleButton.setScaleType(ImageView.ScaleType.CENTER);
    ((FrameLayout)localObject).addView(this.shuffleButton, LayoutHelper.createFrame(48, 48, 51));
    this.shuffleButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        MediaController.getInstance().toggleShuffleMusic();
        AudioPlayerActivity.this.updateShuffleButton();
      }
    });
    updateTitle(false);
    updateRepeatButton();
    updateShuffleButton();
    return localFrameLayout;
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    boolean bool;
    if ((paramInt == NotificationCenter.audioDidStarted) || (paramInt == NotificationCenter.audioPlayStateChanged) || (paramInt == NotificationCenter.audioDidReset)) {
      if ((paramInt == NotificationCenter.audioDidReset) && (((Boolean)paramVarArgs[1]).booleanValue()))
      {
        bool = true;
        updateTitle(bool);
      }
    }
    do
    {
      do
      {
        return;
        bool = false;
        break;
      } while (paramInt != NotificationCenter.audioProgressDidChanged);
      paramVarArgs = MediaController.getInstance().getPlayingMessageObject();
    } while ((paramVarArgs == null) || (!paramVarArgs.isMusic()));
    updateProgress(paramVarArgs);
  }
  
  public int getObserverTag()
  {
    return this.TAG;
  }
  
  public void onFailedDownload(String paramString) {}
  
  public boolean onFragmentCreate()
  {
    this.TAG = MediaController.getInstance().generateObserverTag();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioPlayStateChanged);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidStarted);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioProgressDidChanged);
    return super.onFragmentCreate();
  }
  
  public void onFragmentDestroy()
  {
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioPlayStateChanged);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidStarted);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioProgressDidChanged);
    MediaController.getInstance().removeLoadingFileObserver(this);
    super.onFragmentDestroy();
  }
  
  public void onProgressDownload(String paramString, float paramFloat)
  {
    this.progressView.setProgress(paramFloat, true);
  }
  
  public void onProgressUpload(String paramString, float paramFloat, boolean paramBoolean) {}
  
  public void onSuccessDownload(String paramString) {}
  
  private class SeekBarView
    extends FrameLayout
  {
    private Paint innerPaint1;
    private Paint outerPaint1;
    private boolean pressed = false;
    public int thumbDX = 0;
    private int thumbHeight;
    private int thumbWidth;
    public int thumbX = 0;
    
    public SeekBarView(Context paramContext)
    {
      super();
      setWillNotDraw(false);
      this.innerPaint1 = new Paint(1);
      this.innerPaint1.setColor(419430400);
      this.outerPaint1 = new Paint(1);
      this.outerPaint1.setColor(-14438417);
      this.thumbWidth = AndroidUtilities.dp(24.0F);
      this.thumbHeight = AndroidUtilities.dp(24.0F);
    }
    
    public boolean isDragging()
    {
      return this.pressed;
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      int i = (getMeasuredHeight() - this.thumbHeight) / 2;
      paramCanvas.drawRect(this.thumbWidth / 2, getMeasuredHeight() / 2 - AndroidUtilities.dp(1.0F), getMeasuredWidth() - this.thumbWidth / 2, getMeasuredHeight() / 2 + AndroidUtilities.dp(1.0F), this.innerPaint1);
      paramCanvas.drawRect(this.thumbWidth / 2, getMeasuredHeight() / 2 - AndroidUtilities.dp(1.0F), this.thumbWidth / 2 + this.thumbX, getMeasuredHeight() / 2 + AndroidUtilities.dp(1.0F), this.outerPaint1);
      float f2 = this.thumbX + this.thumbWidth / 2;
      float f3 = this.thumbHeight / 2 + i;
      if (this.pressed) {}
      for (float f1 = 8.0F;; f1 = 6.0F)
      {
        paramCanvas.drawCircle(f2, f3, AndroidUtilities.dp(f1), this.outerPaint1);
        return;
      }
    }
    
    public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
    {
      return onTouch(paramMotionEvent);
    }
    
    boolean onTouch(MotionEvent paramMotionEvent)
    {
      if (paramMotionEvent.getAction() == 0)
      {
        getParent().requestDisallowInterceptTouchEvent(true);
        int i = (getMeasuredHeight() - this.thumbWidth) / 2;
        if ((this.thumbX - i <= paramMotionEvent.getX()) && (paramMotionEvent.getX() <= this.thumbX + this.thumbWidth + i) && (paramMotionEvent.getY() >= 0.0F) && (paramMotionEvent.getY() <= getMeasuredHeight()))
        {
          this.pressed = true;
          this.thumbDX = ((int)(paramMotionEvent.getX() - this.thumbX));
          invalidate();
          return true;
        }
      }
      else if ((paramMotionEvent.getAction() == 1) || (paramMotionEvent.getAction() == 3))
      {
        if (this.pressed)
        {
          if (paramMotionEvent.getAction() == 1) {
            AudioPlayerActivity.this.onSeekBarDrag(this.thumbX / (getMeasuredWidth() - this.thumbWidth));
          }
          this.pressed = false;
          invalidate();
          return true;
        }
      }
      else if ((paramMotionEvent.getAction() == 2) && (this.pressed))
      {
        this.thumbX = ((int)(paramMotionEvent.getX() - this.thumbDX));
        if (this.thumbX < 0) {
          this.thumbX = 0;
        }
        for (;;)
        {
          invalidate();
          return true;
          if (this.thumbX > getMeasuredWidth() - this.thumbWidth) {
            this.thumbX = (getMeasuredWidth() - this.thumbWidth);
          }
        }
      }
      return false;
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      return onTouch(paramMotionEvent);
    }
    
    public void setProgress(float paramFloat)
    {
      int i = (int)Math.ceil((getMeasuredWidth() - this.thumbWidth) * paramFloat);
      if (this.thumbX != i)
      {
        this.thumbX = i;
        if (this.thumbX >= 0) {
          break label48;
        }
        this.thumbX = 0;
      }
      for (;;)
      {
        invalidate();
        return;
        label48:
        if (this.thumbX > getMeasuredWidth() - this.thumbWidth) {
          this.thumbX = (getMeasuredWidth() - this.thumbWidth);
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/AudioPlayerActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */