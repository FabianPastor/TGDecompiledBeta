package org.telegram.messenger.exoplayer;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;

public final class AspectRatioFrameLayout
  extends FrameLayout
{
  private static final float MAX_ASPECT_RATIO_DEFORMATION_FRACTION = 0.01F;
  private Matrix matrix = new Matrix();
  private int rotation;
  private float videoAspectRatio;
  
  public AspectRatioFrameLayout(Context paramContext)
  {
    super(paramContext);
  }
  
  public AspectRatioFrameLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    if (this.videoAspectRatio == 0.0F) {}
    label224:
    for (;;)
    {
      return;
      paramInt2 = getMeasuredWidth();
      paramInt1 = getMeasuredHeight();
      float f = paramInt2 / paramInt1;
      f = this.videoAspectRatio / f - 1.0F;
      if (Math.abs(f) > 0.01F)
      {
        if (f > 0.0F)
        {
          paramInt1 = (int)(paramInt2 / this.videoAspectRatio);
          super.onMeasure(View.MeasureSpec.makeMeasureSpec(paramInt2, NUM), View.MeasureSpec.makeMeasureSpec(paramInt1, NUM));
          paramInt2 = getChildCount();
          paramInt1 = 0;
        }
        for (;;)
        {
          if (paramInt1 >= paramInt2) {
            break label224;
          }
          View localView = getChildAt(paramInt1);
          if ((localView instanceof TextureView))
          {
            this.matrix.reset();
            paramInt1 = getWidth() / 2;
            paramInt2 = getHeight() / 2;
            this.matrix.postRotate(this.rotation, paramInt1, paramInt2);
            if ((this.rotation == 90) || (this.rotation == 270))
            {
              f = getHeight() / getWidth();
              this.matrix.postScale(1.0F / f, f, paramInt1, paramInt2);
            }
            ((TextureView)localView).setTransform(this.matrix);
            return;
            paramInt2 = (int)(paramInt1 * this.videoAspectRatio);
            break;
          }
          paramInt1 += 1;
        }
      }
    }
  }
  
  public void setAspectRatio(float paramFloat, int paramInt)
  {
    if ((this.videoAspectRatio != paramFloat) || (this.rotation != paramInt))
    {
      this.videoAspectRatio = paramFloat;
      this.rotation = paramInt;
      requestLayout();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/AspectRatioFrameLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */