package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;

public class PhotoPickerPhotoCell
  extends FrameLayout
{
  private AnimatorSet animator;
  public CheckBox checkBox;
  public FrameLayout checkFrame;
  public int itemWidth;
  public BackupImageView photoImage;
  
  public PhotoPickerPhotoCell(Context paramContext)
  {
    super(paramContext);
    this.photoImage = new BackupImageView(paramContext);
    addView(this.photoImage, LayoutHelper.createFrame(-1, -1.0F));
    this.checkFrame = new FrameLayout(paramContext);
    addView(this.checkFrame, LayoutHelper.createFrame(42, 42, 53));
    this.checkBox = new CheckBox(paramContext, 2130837595);
    this.checkBox.setSize(30);
    this.checkBox.setCheckOffset(AndroidUtilities.dp(1.0F));
    this.checkBox.setDrawBackground(true);
    this.checkBox.setColor(-12793105);
    addView(this.checkBox, LayoutHelper.createFrame(30, 30.0F, 53, 0.0F, 4.0F, 4.0F, 0.0F));
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(this.itemWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(this.itemWidth, 1073741824));
  }
  
  public void setChecked(final boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = -16119286;
    float f1 = 0.85F;
    this.checkBox.setChecked(paramBoolean1, paramBoolean2);
    if (this.animator != null)
    {
      this.animator.cancel();
      this.animator = null;
    }
    Object localObject1;
    float f2;
    if (paramBoolean2)
    {
      if (paramBoolean1) {
        setBackgroundColor(-16119286);
      }
      this.animator = new AnimatorSet();
      localObject1 = this.animator;
      Object localObject2 = this.photoImage;
      BackupImageView localBackupImageView;
      if (paramBoolean1)
      {
        f2 = 0.85F;
        localObject2 = ObjectAnimator.ofFloat(localObject2, "scaleX", new float[] { f2 });
        localBackupImageView = this.photoImage;
        if (!paramBoolean1) {
          break label179;
        }
      }
      for (;;)
      {
        ((AnimatorSet)localObject1).playTogether(new Animator[] { localObject2, ObjectAnimator.ofFloat(localBackupImageView, "scaleY", new float[] { f1 }) });
        this.animator.setDuration(200L);
        this.animator.addListener(new AnimatorListenerAdapterProxy()
        {
          public void onAnimationCancel(Animator paramAnonymousAnimator)
          {
            if ((PhotoPickerPhotoCell.this.animator != null) && (PhotoPickerPhotoCell.this.animator.equals(paramAnonymousAnimator))) {
              PhotoPickerPhotoCell.access$002(PhotoPickerPhotoCell.this, null);
            }
          }
          
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            if ((PhotoPickerPhotoCell.this.animator != null) && (PhotoPickerPhotoCell.this.animator.equals(paramAnonymousAnimator)))
            {
              PhotoPickerPhotoCell.access$002(PhotoPickerPhotoCell.this, null);
              if (!paramBoolean1) {
                PhotoPickerPhotoCell.this.setBackgroundColor(0);
              }
            }
          }
        });
        this.animator.start();
        return;
        f2 = 1.0F;
        break;
        label179:
        f1 = 1.0F;
      }
    }
    if (paramBoolean1)
    {
      setBackgroundColor(i);
      localObject1 = this.photoImage;
      if (!paramBoolean1) {
        break label238;
      }
      f2 = 0.85F;
      label208:
      ((BackupImageView)localObject1).setScaleX(f2);
      localObject1 = this.photoImage;
      if (!paramBoolean1) {
        break label244;
      }
    }
    for (;;)
    {
      ((BackupImageView)localObject1).setScaleY(f1);
      return;
      i = 0;
      break;
      label238:
      f2 = 1.0F;
      break label208;
      label244:
      f1 = 1.0F;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/PhotoPickerPhotoCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */