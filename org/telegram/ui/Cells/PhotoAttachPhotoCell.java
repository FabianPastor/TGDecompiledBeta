package org.telegram.ui.Cells;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.widget.FrameLayout;
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
  private CheckBox checkBox;
  private FrameLayout checkFrame;
  private PhotoAttachPhotoCellDelegate delegate;
  private BackupImageView imageView;
  private boolean isLast;
  private MediaController.PhotoEntry photoEntry;
  private boolean pressed;
  
  public PhotoAttachPhotoCell(Context paramContext)
  {
    super(paramContext);
    this.imageView = new BackupImageView(paramContext);
    addView(this.imageView, LayoutHelper.createFrame(80, 80.0F));
    this.checkFrame = new FrameLayout(paramContext);
    addView(this.checkFrame, LayoutHelper.createFrame(42, 42.0F, 51, 38.0F, 0.0F, 0.0F, 0.0F));
    this.checkBox = new CheckBox(paramContext, 2130837595);
    this.checkBox.setSize(30);
    this.checkBox.setCheckOffset(AndroidUtilities.dp(1.0F));
    this.checkBox.setDrawBackground(true);
    this.checkBox.setColor(-12793105);
    addView(this.checkBox, LayoutHelper.createFrame(30, 30.0F, 51, 46.0F, 4.0F, 0.0F, 0.0F));
    this.checkBox.setVisibility(0);
  }
  
  public CheckBox getCheckBox()
  {
    return this.checkBox;
  }
  
  public BackupImageView getImageView()
  {
    return this.imageView;
  }
  
  public MediaController.PhotoEntry getPhotoEntry()
  {
    return this.photoEntry;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (this.isLast) {}
    for (paramInt1 = 0;; paramInt1 = 6)
    {
      super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(paramInt1 + 80), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0F), 1073741824));
      return;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool2 = false;
    this.checkFrame.getHitRect(rect);
    boolean bool1;
    if (paramMotionEvent.getAction() == 0)
    {
      bool1 = bool2;
      if (rect.contains((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY()))
      {
        this.pressed = true;
        invalidate();
        bool1 = true;
      }
    }
    for (;;)
    {
      bool2 = bool1;
      if (!bool1) {
        bool2 = super.onTouchEvent(paramMotionEvent);
      }
      return bool2;
      bool1 = bool2;
      if (this.pressed) {
        if (paramMotionEvent.getAction() == 1)
        {
          getParent().requestDisallowInterceptTouchEvent(true);
          this.pressed = false;
          playSoundEffect(0);
          this.delegate.onCheckClick(this);
          invalidate();
          bool1 = bool2;
        }
        else if (paramMotionEvent.getAction() == 3)
        {
          this.pressed = false;
          invalidate();
          bool1 = bool2;
        }
        else
        {
          bool1 = bool2;
          if (paramMotionEvent.getAction() == 2)
          {
            bool1 = bool2;
            if (!rect.contains((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY()))
            {
              this.pressed = false;
              invalidate();
              bool1 = bool2;
            }
          }
        }
      }
    }
  }
  
  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.checkBox.setChecked(paramBoolean1, paramBoolean2);
  }
  
  public void setDelegate(PhotoAttachPhotoCellDelegate paramPhotoAttachPhotoCellDelegate)
  {
    this.delegate = paramPhotoAttachPhotoCellDelegate;
  }
  
  public void setOnCheckClickLisnener(View.OnClickListener paramOnClickListener)
  {
    this.checkFrame.setOnClickListener(paramOnClickListener);
  }
  
  public void setPhotoEntry(MediaController.PhotoEntry paramPhotoEntry, boolean paramBoolean)
  {
    int i = 0;
    this.pressed = false;
    this.photoEntry = paramPhotoEntry;
    this.isLast = paramBoolean;
    boolean bool;
    if (this.photoEntry.thumbPath != null)
    {
      this.imageView.setImage(this.photoEntry.thumbPath, null, getResources().getDrawable(2130837858));
      bool = PhotoViewer.getInstance().isShowingImage(this.photoEntry.path);
      paramPhotoEntry = this.imageView.getImageReceiver();
      if (bool) {
        break label206;
      }
    }
    label206:
    for (paramBoolean = true;; paramBoolean = false)
    {
      paramPhotoEntry.setVisible(paramBoolean, true);
      paramPhotoEntry = this.checkBox;
      if (bool) {
        i = 4;
      }
      paramPhotoEntry.setVisibility(i);
      requestLayout();
      return;
      if (this.photoEntry.path != null)
      {
        this.imageView.setOrientation(this.photoEntry.orientation, true);
        this.imageView.setImage("thumb://" + this.photoEntry.imageId + ":" + this.photoEntry.path, null, getResources().getDrawable(2130837858));
        break;
      }
      this.imageView.setImageResource(2130837858);
      break;
    }
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