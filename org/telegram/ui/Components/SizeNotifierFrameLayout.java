package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.ActionBar;

public class SizeNotifierFrameLayout
  extends FrameLayout
{
  private Drawable backgroundDrawable;
  private int bottomClip;
  private SizeNotifierFrameLayoutDelegate delegate;
  private int keyboardHeight;
  private boolean occupyStatusBar = true;
  private Rect rect = new Rect();
  
  public SizeNotifierFrameLayout(Context paramContext)
  {
    super(paramContext);
    setWillNotDraw(false);
  }
  
  public Drawable getBackgroundImage()
  {
    return this.backgroundDrawable;
  }
  
  public int getKeyboardHeight()
  {
    View localView = getRootView();
    getWindowVisibleDisplayFrame(this.rect);
    int i = localView.getHeight();
    if (this.rect.top != 0) {}
    for (int j = AndroidUtilities.statusBarHeight;; j = 0) {
      return i - j - AndroidUtilities.getViewInset(localView) - (this.rect.bottom - this.rect.top);
    }
  }
  
  protected boolean isActionBarVisible()
  {
    return true;
  }
  
  public void notifyHeightChanged()
  {
    if (this.delegate != null)
    {
      this.keyboardHeight = getKeyboardHeight();
      if (AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y) {
        break label47;
      }
    }
    label47:
    for (final boolean bool = true;; bool = false)
    {
      post(new Runnable()
      {
        public void run()
        {
          if (SizeNotifierFrameLayout.this.delegate != null) {
            SizeNotifierFrameLayout.this.delegate.onSizeChanged(SizeNotifierFrameLayout.this.keyboardHeight, bool);
          }
        }
      });
      return;
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.backgroundDrawable != null) {
      if ((this.backgroundDrawable instanceof ColorDrawable))
      {
        if (this.bottomClip != 0)
        {
          paramCanvas.save();
          paramCanvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight() - this.bottomClip);
        }
        this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        this.backgroundDrawable.draw(paramCanvas);
        if (this.bottomClip != 0) {
          paramCanvas.restore();
        }
      }
    }
    for (;;)
    {
      return;
      if ((this.backgroundDrawable instanceof BitmapDrawable))
      {
        float f1;
        if (((BitmapDrawable)this.backgroundDrawable).getTileModeX() == Shader.TileMode.REPEAT)
        {
          paramCanvas.save();
          f1 = 2.0F / AndroidUtilities.density;
          paramCanvas.scale(f1, f1);
          this.backgroundDrawable.setBounds(0, 0, (int)Math.ceil(getMeasuredWidth() / f1), (int)Math.ceil(getMeasuredHeight() / f1));
          this.backgroundDrawable.draw(paramCanvas);
          paramCanvas.restore();
        }
        else
        {
          int i;
          label188:
          int j;
          label208:
          int k;
          int m;
          float f2;
          if (isActionBarVisible())
          {
            i = ActionBar.getCurrentActionBarHeight();
            if ((Build.VERSION.SDK_INT < 21) || (!this.occupyStatusBar)) {
              break label388;
            }
            j = AndroidUtilities.statusBarHeight;
            k = i + j;
            m = getMeasuredHeight() - k;
            f2 = getMeasuredWidth() / this.backgroundDrawable.getIntrinsicWidth();
            f1 = (this.keyboardHeight + m) / this.backgroundDrawable.getIntrinsicHeight();
            if (f2 >= f1) {
              break label394;
            }
          }
          for (;;)
          {
            int n = (int)Math.ceil(this.backgroundDrawable.getIntrinsicWidth() * f1);
            j = (int)Math.ceil(this.backgroundDrawable.getIntrinsicHeight() * f1);
            i = (getMeasuredWidth() - n) / 2;
            m = (m - j + this.keyboardHeight) / 2 + k;
            paramCanvas.save();
            paramCanvas.clipRect(0, k, n, getMeasuredHeight() - this.bottomClip);
            this.backgroundDrawable.setBounds(i, m, i + n, m + j);
            this.backgroundDrawable.draw(paramCanvas);
            paramCanvas.restore();
            break;
            i = 0;
            break label188;
            label388:
            j = 0;
            break label208;
            label394:
            f1 = f2;
          }
          super.onDraw(paramCanvas);
        }
      }
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    notifyHeightChanged();
  }
  
  public void setBackgroundImage(Drawable paramDrawable)
  {
    this.backgroundDrawable = paramDrawable;
    invalidate();
  }
  
  public void setBottomClip(int paramInt)
  {
    this.bottomClip = paramInt;
  }
  
  public void setDelegate(SizeNotifierFrameLayoutDelegate paramSizeNotifierFrameLayoutDelegate)
  {
    this.delegate = paramSizeNotifierFrameLayoutDelegate;
  }
  
  public void setOccupyStatusBar(boolean paramBoolean)
  {
    this.occupyStatusBar = paramBoolean;
  }
  
  public static abstract interface SizeNotifierFrameLayoutDelegate
  {
    public abstract void onSizeChanged(int paramInt, boolean paramBoolean);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/SizeNotifierFrameLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */