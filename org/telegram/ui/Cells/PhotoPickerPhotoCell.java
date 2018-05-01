package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View.MeasureSpec;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;

public class PhotoPickerPhotoCell
  extends FrameLayout
{
  private AnimatorSet animator;
  private AnimatorSet animatorSet;
  public CheckBox checkBox;
  public FrameLayout checkFrame;
  public int itemWidth;
  public BackupImageView photoImage;
  public FrameLayout videoInfoContainer;
  public TextView videoTextView;
  private boolean zoomOnSelect;
  
  public PhotoPickerPhotoCell(Context paramContext, boolean paramBoolean)
  {
    super(paramContext);
    this.zoomOnSelect = paramBoolean;
    this.photoImage = new BackupImageView(paramContext);
    addView(this.photoImage, LayoutHelper.createFrame(-1, -1.0F));
    this.checkFrame = new FrameLayout(paramContext);
    addView(this.checkFrame, LayoutHelper.createFrame(42, 42, 53));
    this.videoInfoContainer = new FrameLayout(paramContext);
    this.videoInfoContainer.setBackgroundResource(NUM);
    this.videoInfoContainer.setPadding(AndroidUtilities.dp(3.0F), 0, AndroidUtilities.dp(3.0F), 0);
    addView(this.videoInfoContainer, LayoutHelper.createFrame(-1, 16, 83));
    ImageView localImageView = new ImageView(paramContext);
    localImageView.setImageResource(NUM);
    this.videoInfoContainer.addView(localImageView, LayoutHelper.createFrame(-2, -2, 19));
    this.videoTextView = new TextView(paramContext);
    this.videoTextView.setTextColor(-1);
    this.videoTextView.setTextSize(1, 12.0F);
    this.videoInfoContainer.addView(this.videoTextView, LayoutHelper.createFrame(-2, -2.0F, 19, 18.0F, -0.7F, 0.0F, 0.0F));
    this.checkBox = new CheckBox(paramContext, NUM);
    paramContext = this.checkBox;
    int i;
    if (paramBoolean)
    {
      i = 30;
      paramContext.setSize(i);
      this.checkBox.setCheckOffset(AndroidUtilities.dp(1.0F));
      this.checkBox.setDrawBackground(true);
      this.checkBox.setColor(-10043398, -1);
      paramContext = this.checkBox;
      if (!paramBoolean) {
        break label320;
      }
      i = 30;
      label284:
      if (!paramBoolean) {
        break label327;
      }
    }
    label320:
    label327:
    for (float f = 30.0F;; f = 26.0F)
    {
      addView(paramContext, LayoutHelper.createFrame(i, f, 53, 0.0F, 4.0F, 4.0F, 0.0F));
      return;
      i = 26;
      break;
      i = 26;
      break label284;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(this.itemWidth, NUM), View.MeasureSpec.makeMeasureSpec(this.itemWidth, NUM));
  }
  
  public void setChecked(int paramInt, final boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = -16119286;
    float f1 = 0.85F;
    this.checkBox.setChecked(paramInt, paramBoolean1, paramBoolean2);
    if (this.animator != null)
    {
      this.animator.cancel();
      this.animator = null;
    }
    Object localObject1;
    Object localObject2;
    float f2;
    BackupImageView localBackupImageView;
    if (this.zoomOnSelect)
    {
      if (!paramBoolean2) {
        break label195;
      }
      if (paramBoolean1) {
        setBackgroundColor(-16119286);
      }
      this.animator = new AnimatorSet();
      localObject1 = this.animator;
      localObject2 = this.photoImage;
      if (!paramBoolean1) {
        break label183;
      }
      f2 = 0.85F;
      localObject2 = ObjectAnimator.ofFloat(localObject2, "scaleX", new float[] { f2 });
      localBackupImageView = this.photoImage;
      if (!paramBoolean1) {
        break label189;
      }
    }
    for (;;)
    {
      ((AnimatorSet)localObject1).playTogether(new Animator[] { localObject2, ObjectAnimator.ofFloat(localBackupImageView, "scaleY", new float[] { f1 }) });
      this.animator.setDuration(200L);
      this.animator.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          if ((PhotoPickerPhotoCell.this.animator != null) && (PhotoPickerPhotoCell.this.animator.equals(paramAnonymousAnimator))) {
            PhotoPickerPhotoCell.access$102(PhotoPickerPhotoCell.this, null);
          }
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if ((PhotoPickerPhotoCell.this.animator != null) && (PhotoPickerPhotoCell.this.animator.equals(paramAnonymousAnimator)))
          {
            PhotoPickerPhotoCell.access$102(PhotoPickerPhotoCell.this, null);
            if (!paramBoolean1) {
              PhotoPickerPhotoCell.this.setBackgroundColor(0);
            }
          }
        }
      });
      this.animator.start();
      return;
      label183:
      f2 = 1.0F;
      break;
      label189:
      f1 = 1.0F;
    }
    label195:
    if (paramBoolean1)
    {
      paramInt = i;
      label202:
      setBackgroundColor(paramInt);
      localObject1 = this.photoImage;
      if (!paramBoolean1) {
        break label253;
      }
      f2 = 0.85F;
      label221:
      ((BackupImageView)localObject1).setScaleX(f2);
      localObject1 = this.photoImage;
      if (!paramBoolean1) {
        break label259;
      }
    }
    for (;;)
    {
      ((BackupImageView)localObject1).setScaleY(f1);
      break;
      paramInt = 0;
      break label202;
      label253:
      f2 = 1.0F;
      break label221;
      label259:
      f1 = 1.0F;
    }
  }
  
  public void setNum(int paramInt)
  {
    this.checkBox.setNum(paramInt);
  }
  
  public void showCheck(boolean paramBoolean)
  {
    float f1 = 1.0F;
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
    ObjectAnimator localObjectAnimator;
    if (paramBoolean)
    {
      f2 = 1.0F;
      localObjectAnimator = ObjectAnimator.ofFloat(localObject, "alpha", new float[] { f2 });
      localObject = this.checkBox;
      if (!paramBoolean) {
        break label165;
      }
    }
    label165:
    for (float f2 = f1;; f2 = 0.0F)
    {
      localAnimatorSet.playTogether(new Animator[] { localObjectAnimator, ObjectAnimator.ofFloat(localObject, "alpha", new float[] { f2 }) });
      this.animatorSet.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if (paramAnonymousAnimator.equals(PhotoPickerPhotoCell.this.animatorSet)) {
            PhotoPickerPhotoCell.access$002(PhotoPickerPhotoCell.this, null);
          }
        }
      });
      this.animatorSet.start();
      return;
      f2 = 0.0F;
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/PhotoPickerPhotoCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */