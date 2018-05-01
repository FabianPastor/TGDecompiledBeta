package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.PhotoViewer;

public class PhotoAttachPhotoCell
  extends FrameLayout
{
  private static Rect rect = new Rect();
  private AnimatorSet animatorSet;
  private CheckBox checkBox;
  private FrameLayout checkFrame;
  private PhotoAttachPhotoCellDelegate delegate;
  private BackupImageView imageView;
  private boolean isLast;
  private boolean isVertical;
  private boolean needCheckShow;
  private MediaController.PhotoEntry photoEntry;
  private boolean pressed;
  private FrameLayout videoInfoContainer;
  private TextView videoTextView;
  
  public PhotoAttachPhotoCell(Context paramContext)
  {
    super(paramContext);
    this.imageView = new BackupImageView(paramContext);
    addView(this.imageView, LayoutHelper.createFrame(80, 80.0F));
    this.checkFrame = new FrameLayout(paramContext);
    addView(this.checkFrame, LayoutHelper.createFrame(42, 42.0F, 51, 38.0F, 0.0F, 0.0F, 0.0F));
    this.videoInfoContainer = new FrameLayout(paramContext);
    this.videoInfoContainer.setBackgroundResource(NUM);
    this.videoInfoContainer.setPadding(AndroidUtilities.dp(3.0F), 0, AndroidUtilities.dp(3.0F), 0);
    addView(this.videoInfoContainer, LayoutHelper.createFrame(80, 16, 83));
    ImageView localImageView = new ImageView(paramContext);
    localImageView.setImageResource(NUM);
    this.videoInfoContainer.addView(localImageView, LayoutHelper.createFrame(-2, -2, 19));
    this.videoTextView = new TextView(paramContext);
    this.videoTextView.setTextColor(-1);
    this.videoTextView.setTextSize(1, 12.0F);
    this.videoInfoContainer.addView(this.videoTextView, LayoutHelper.createFrame(-2, -2.0F, 19, 18.0F, -0.7F, 0.0F, 0.0F));
    this.checkBox = new CheckBox(paramContext, NUM);
    this.checkBox.setSize(30);
    this.checkBox.setCheckOffset(AndroidUtilities.dp(1.0F));
    this.checkBox.setDrawBackground(true);
    this.checkBox.setColor(-12793105, -1);
    addView(this.checkBox, LayoutHelper.createFrame(30, 30.0F, 51, 46.0F, 4.0F, 0.0F, 0.0F));
    this.checkBox.setVisibility(0);
  }
  
  public void callDelegate()
  {
    this.delegate.onCheckClick(this);
  }
  
  public CheckBox getCheckBox()
  {
    return this.checkBox;
  }
  
  public FrameLayout getCheckFrame()
  {
    return this.checkFrame;
  }
  
  public BackupImageView getImageView()
  {
    return this.imageView;
  }
  
  public MediaController.PhotoEntry getPhotoEntry()
  {
    return this.photoEntry;
  }
  
  public View getVideoInfoContainer()
  {
    return this.videoInfoContainer;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = 0;
    paramInt1 = 0;
    if (this.isVertical)
    {
      paramInt2 = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0F), NUM);
      if (this.isLast) {}
      for (;;)
      {
        super.onMeasure(paramInt2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(paramInt1 + 80), NUM));
        return;
        paramInt1 = 6;
      }
    }
    if (this.isLast) {}
    for (paramInt1 = paramInt2;; paramInt1 = 6)
    {
      super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(paramInt1 + 80), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0F), NUM));
      break;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool1 = false;
    this.checkFrame.getHitRect(rect);
    boolean bool2;
    if (paramMotionEvent.getAction() == 0)
    {
      bool2 = bool1;
      if (rect.contains((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY()))
      {
        this.pressed = true;
        invalidate();
        bool2 = true;
      }
    }
    for (;;)
    {
      bool1 = bool2;
      if (!bool2) {
        bool1 = super.onTouchEvent(paramMotionEvent);
      }
      return bool1;
      bool2 = bool1;
      if (this.pressed) {
        if (paramMotionEvent.getAction() == 1)
        {
          getParent().requestDisallowInterceptTouchEvent(true);
          this.pressed = false;
          playSoundEffect(0);
          this.delegate.onCheckClick(this);
          invalidate();
          bool2 = bool1;
        }
        else if (paramMotionEvent.getAction() == 3)
        {
          this.pressed = false;
          invalidate();
          bool2 = bool1;
        }
        else
        {
          bool2 = bool1;
          if (paramMotionEvent.getAction() == 2)
          {
            bool2 = bool1;
            if (!rect.contains((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY()))
            {
              this.pressed = false;
              invalidate();
              bool2 = bool1;
            }
          }
        }
      }
    }
  }
  
  public void setChecked(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.checkBox.setChecked(paramInt, paramBoolean1, paramBoolean2);
  }
  
  public void setDelegate(PhotoAttachPhotoCellDelegate paramPhotoAttachPhotoCellDelegate)
  {
    this.delegate = paramPhotoAttachPhotoCellDelegate;
  }
  
  public void setIsVertical(boolean paramBoolean)
  {
    this.isVertical = paramBoolean;
  }
  
  public void setNum(int paramInt)
  {
    this.checkBox.setNum(paramInt);
  }
  
  public void setOnCheckClickLisnener(View.OnClickListener paramOnClickListener)
  {
    this.checkFrame.setOnClickListener(paramOnClickListener);
  }
  
  public void setPhotoEntry(MediaController.PhotoEntry paramPhotoEntry, boolean paramBoolean1, boolean paramBoolean2)
  {
    float f1 = 0.0F;
    boolean bool = false;
    this.pressed = false;
    this.photoEntry = paramPhotoEntry;
    this.isLast = paramBoolean2;
    int i;
    if (this.photoEntry.isVideo)
    {
      this.imageView.setOrientation(0, true);
      this.videoInfoContainer.setVisibility(0);
      i = this.photoEntry.duration / 60;
      int j = this.photoEntry.duration;
      this.videoTextView.setText(String.format("%d:%02d", new Object[] { Integer.valueOf(i), Integer.valueOf(j - i * 60) }));
      if (this.photoEntry.thumbPath == null) {
        break label241;
      }
      this.imageView.setImage(this.photoEntry.thumbPath, null, getResources().getDrawable(NUM));
      label142:
      if ((!paramBoolean1) || (!PhotoViewer.isShowingImage(this.photoEntry.path))) {
        break label415;
      }
      i = 1;
      label162:
      paramPhotoEntry = this.imageView.getImageReceiver();
      paramBoolean1 = bool;
      if (i == 0) {
        paramBoolean1 = true;
      }
      paramPhotoEntry.setVisible(paramBoolean1, true);
      paramPhotoEntry = this.checkBox;
      if (i == 0) {
        break label421;
      }
      f2 = 0.0F;
      label199:
      paramPhotoEntry.setAlpha(f2);
      paramPhotoEntry = this.videoInfoContainer;
      if (i == 0) {
        break label427;
      }
    }
    label241:
    label415:
    label421:
    label427:
    for (float f2 = f1;; f2 = 1.0F)
    {
      paramPhotoEntry.setAlpha(f2);
      requestLayout();
      return;
      this.videoInfoContainer.setVisibility(4);
      break;
      if (this.photoEntry.path != null)
      {
        if (this.photoEntry.isVideo)
        {
          this.imageView.setImage("vthumb://" + this.photoEntry.imageId + ":" + this.photoEntry.path, null, getResources().getDrawable(NUM));
          break label142;
        }
        this.imageView.setOrientation(this.photoEntry.orientation, true);
        this.imageView.setImage("thumb://" + this.photoEntry.imageId + ":" + this.photoEntry.path, null, getResources().getDrawable(NUM));
        break label142;
      }
      this.imageView.setImageResource(NUM);
      break label142;
      i = 0;
      break label162;
      f2 = 1.0F;
      break label199;
    }
  }
  
  public void showCheck(boolean paramBoolean)
  {
    float f1 = 1.0F;
    if (((paramBoolean) && (this.checkBox.getAlpha() == 1.0F)) || ((!paramBoolean) && (this.checkBox.getAlpha() == 0.0F))) {
      return;
    }
    if (this.animatorSet != null)
    {
      this.animatorSet.cancel();
      this.animatorSet = null;
    }
    this.animatorSet = new AnimatorSet();
    this.animatorSet.setInterpolator(new DecelerateInterpolator());
    this.animatorSet.setDuration(180L);
    AnimatorSet localAnimatorSet = this.animatorSet;
    Object localObject = this.videoInfoContainer;
    label108:
    CheckBox localCheckBox;
    if (paramBoolean)
    {
      f2 = 1.0F;
      localObject = ObjectAnimator.ofFloat(localObject, "alpha", new float[] { f2 });
      localCheckBox = this.checkBox;
      if (!paramBoolean) {
        break label202;
      }
    }
    label202:
    for (float f2 = f1;; f2 = 0.0F)
    {
      localAnimatorSet.playTogether(new Animator[] { localObject, ObjectAnimator.ofFloat(localCheckBox, "alpha", new float[] { f2 }) });
      this.animatorSet.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if (paramAnonymousAnimator.equals(PhotoAttachPhotoCell.this.animatorSet)) {
            PhotoAttachPhotoCell.access$002(PhotoAttachPhotoCell.this, null);
          }
        }
      });
      this.animatorSet.start();
      break;
      f2 = 0.0F;
      break label108;
    }
  }
  
  public void showImage()
  {
    this.imageView.getImageReceiver().setVisible(true, true);
  }
  
  public static abstract interface PhotoAttachPhotoCellDelegate
  {
    public abstract void onCheckClick(PhotoAttachPhotoCell paramPhotoAttachPhotoCell);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/PhotoAttachPhotoCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */