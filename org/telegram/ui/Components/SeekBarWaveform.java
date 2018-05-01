package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewParent;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;

public class SeekBarWaveform
{
  private static Paint paintInner;
  private static Paint paintOuter;
  private SeekBar.SeekBarDelegate delegate;
  private int height;
  private int innerColor;
  private MessageObject messageObject;
  private int outerColor;
  private View parentView;
  private boolean pressed = false;
  private boolean selected;
  private int selectedColor;
  private boolean startDraging = false;
  private float startX;
  private int thumbDX = 0;
  private int thumbX = 0;
  private byte[] waveformBytes;
  private int width;
  
  public SeekBarWaveform(Context paramContext)
  {
    if (paintInner == null)
    {
      paintInner = new Paint();
      paintOuter = new Paint();
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    if ((this.waveformBytes == null) || (this.width == 0)) {}
    do
    {
      return;
      f1 = this.width / AndroidUtilities.dp(3.0F);
    } while (f1 <= 0.1F);
    int i = this.waveformBytes.length * 8 / 5;
    float f2 = i / f1;
    float f1 = 0.0F;
    int j = 0;
    Paint localPaint = paintInner;
    int k;
    label94:
    int m;
    int n;
    label131:
    int i2;
    float f3;
    if ((this.messageObject != null) && (!this.messageObject.isOutOwner()) && (this.messageObject.isContentUnread()))
    {
      k = this.outerColor;
      localPaint.setColor(k);
      paintOuter.setColor(this.outerColor);
      m = (this.height - AndroidUtilities.dp(14.0F)) / 2;
      k = 0;
      n = 0;
      if (n < i)
      {
        if (n == j) {
          break label197;
        }
        i1 = j;
        i2 = k;
        f3 = f1;
      }
    }
    label197:
    int i3;
    int i5;
    do
    {
      n++;
      f1 = f3;
      k = i2;
      j = i1;
      break label131;
      break;
      if (this.selected)
      {
        k = this.selectedColor;
        break label94;
      }
      k = this.innerColor;
      break label94;
      i3 = 0;
      int i4 = j;
      while (j == i4)
      {
        f1 += f2;
        i4 = (int)f1;
        i3++;
      }
      j = n * 5;
      i1 = j / 8;
      i5 = j - i1 * 8;
      j = 8 - i5;
      i2 = 5 - j;
      i5 = (byte)(this.waveformBytes[i1] >> i5 & (2 << Math.min(5, j) - 1) - 1);
      j = i5;
      if (i2 > 0)
      {
        j = i5;
        if (i1 + 1 < this.waveformBytes.length)
        {
          j = (byte)(i5 << i2);
          j = (byte)(this.waveformBytes[(i1 + 1)] & (2 << i2 - 1) - 1 | j);
        }
      }
      i5 = 0;
      f3 = f1;
      i2 = k;
      i1 = i4;
    } while (i5 >= i3);
    int i1 = k * AndroidUtilities.dp(3.0F);
    if ((i1 < this.thumbX) && (AndroidUtilities.dp(2.0F) + i1 < this.thumbX)) {
      paramCanvas.drawRect(i1, AndroidUtilities.dp(14.0F - Math.max(1.0F, 14.0F * j / 31.0F)) + m, AndroidUtilities.dp(2.0F) + i1, AndroidUtilities.dp(14.0F) + m, paintOuter);
    }
    for (;;)
    {
      k++;
      i5++;
      break;
      paramCanvas.drawRect(i1, AndroidUtilities.dp(14.0F - Math.max(1.0F, 14.0F * j / 31.0F)) + m, AndroidUtilities.dp(2.0F) + i1, AndroidUtilities.dp(14.0F) + m, paintInner);
      if (i1 < this.thumbX) {
        paramCanvas.drawRect(i1, AndroidUtilities.dp(14.0F - Math.max(1.0F, 14.0F * j / 31.0F)) + m, this.thumbX, AndroidUtilities.dp(14.0F) + m, paintOuter);
      }
    }
  }
  
  public boolean isDragging()
  {
    return this.pressed;
  }
  
  public boolean isStartDraging()
  {
    return this.startDraging;
  }
  
  public boolean onTouch(int paramInt, float paramFloat1, float paramFloat2)
  {
    boolean bool1 = true;
    boolean bool2;
    if (paramInt == 0)
    {
      if ((0.0F > paramFloat1) || (paramFloat1 > this.width) || (paramFloat2 < 0.0F) || (paramFloat2 > this.height)) {
        break label284;
      }
      this.startX = paramFloat1;
      this.pressed = true;
      this.thumbDX = ((int)(paramFloat1 - this.thumbX));
      this.startDraging = false;
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      if ((paramInt == 1) || (paramInt == 3))
      {
        if (this.pressed)
        {
          if ((paramInt == 1) && (this.delegate != null)) {
            this.delegate.onSeekBarDrag(this.thumbX / this.width);
          }
          this.pressed = false;
          bool2 = bool1;
        }
      }
      else if ((paramInt == 2) && (this.pressed))
      {
        if (this.startDraging)
        {
          this.thumbX = ((int)(paramFloat1 - this.thumbDX));
          if (this.thumbX >= 0) {
            break label262;
          }
          this.thumbX = 0;
        }
        for (;;)
        {
          bool2 = bool1;
          if (this.startX == -1.0F) {
            break;
          }
          bool2 = bool1;
          if (Math.abs(paramFloat1 - this.startX) <= AndroidUtilities.getPixelsInCM(0.2F, true)) {
            break;
          }
          if ((this.parentView != null) && (this.parentView.getParent() != null)) {
            this.parentView.getParent().requestDisallowInterceptTouchEvent(true);
          }
          this.startDraging = true;
          this.startX = -1.0F;
          bool2 = bool1;
          break;
          label262:
          if (this.thumbX > this.width) {
            this.thumbX = this.width;
          }
        }
      }
      label284:
      bool2 = false;
    }
  }
  
  public void setColors(int paramInt1, int paramInt2, int paramInt3)
  {
    this.innerColor = paramInt1;
    this.outerColor = paramInt2;
    this.selectedColor = paramInt3;
  }
  
  public void setDelegate(SeekBar.SeekBarDelegate paramSeekBarDelegate)
  {
    this.delegate = paramSeekBarDelegate;
  }
  
  public void setMessageObject(MessageObject paramMessageObject)
  {
    this.messageObject = paramMessageObject;
  }
  
  public void setParentView(View paramView)
  {
    this.parentView = paramView;
  }
  
  public void setProgress(float paramFloat)
  {
    this.thumbX = ((int)Math.ceil(this.width * paramFloat));
    if (this.thumbX < 0) {
      this.thumbX = 0;
    }
    for (;;)
    {
      return;
      if (this.thumbX > this.width) {
        this.thumbX = this.width;
      }
    }
  }
  
  public void setSelected(boolean paramBoolean)
  {
    this.selected = paramBoolean;
  }
  
  public void setSize(int paramInt1, int paramInt2)
  {
    this.width = paramInt1;
    this.height = paramInt2;
  }
  
  public void setWaveform(byte[] paramArrayOfByte)
  {
    this.waveformBytes = paramArrayOfByte;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/SeekBarWaveform.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */