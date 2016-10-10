package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewParent;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.Peer;

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
    int i4 = this.waveformBytes.length * 8 / 5;
    float f3 = i4 / f1;
    float f1 = 0.0F;
    int j = 0;
    Paint localPaint = paintInner;
    int i;
    label112:
    int i5;
    int m;
    label149:
    int i3;
    float f2;
    if ((this.messageObject != null) && (!this.messageObject.isOutOwner()) && (this.messageObject.isContentUnread()) && (this.messageObject.messageOwner.to_id.channel_id == 0))
    {
      i = this.outerColor;
      localPaint.setColor(i);
      paintOuter.setColor(this.outerColor);
      i5 = (this.height - AndroidUtilities.dp(14.0F)) / 2;
      i = 0;
      m = 0;
      if (m < i4)
      {
        if (m == j) {
          break label217;
        }
        i1 = j;
        i3 = i;
        f2 = f1;
      }
    }
    label217:
    int n;
    int i2;
    do
    {
      m += 1;
      f1 = f2;
      i = i3;
      j = i1;
      break label149;
      break;
      if (this.selected)
      {
        i = this.selectedColor;
        break label112;
      }
      i = this.innerColor;
      break label112;
      n = 0;
      int k = j;
      while (j == k)
      {
        f1 += f3;
        k = (int)f1;
        n += 1;
      }
      j = m * 5;
      i2 = j / 8;
      j -= i2 * 8;
      i1 = 8 - j;
      i3 = 5 - i1;
      i1 = (byte)(this.waveformBytes[i2] >> j & (2 << Math.min(5, i1) - 1) - 1);
      j = i1;
      if (i3 > 0)
      {
        j = (byte)(i1 << i3);
        j = (byte)(this.waveformBytes[(i2 + 1)] & (2 << i3 - 1) - 1 | j);
      }
      i2 = 0;
      f2 = f1;
      i3 = i;
      i1 = k;
    } while (i2 >= n);
    int i1 = i * AndroidUtilities.dp(3.0F);
    if ((i1 < this.thumbX) && (AndroidUtilities.dp(2.0F) + i1 < this.thumbX)) {
      paramCanvas.drawRect(i1, AndroidUtilities.dp(14.0F - Math.max(1.0F, 14.0F * j / 31.0F)) + i5, AndroidUtilities.dp(2.0F) + i1, AndroidUtilities.dp(14.0F) + i5, paintOuter);
    }
    for (;;)
    {
      i += 1;
      i2 += 1;
      break;
      paramCanvas.drawRect(i1, AndroidUtilities.dp(14.0F - Math.max(1.0F, 14.0F * j / 31.0F)) + i5, AndroidUtilities.dp(2.0F) + i1, AndroidUtilities.dp(14.0F) + i5, paintInner);
      if (i1 < this.thumbX) {
        paramCanvas.drawRect(i1, AndroidUtilities.dp(14.0F - Math.max(1.0F, 14.0F * j / 31.0F)) + i5, this.thumbX, AndroidUtilities.dp(14.0F) + i5, paintOuter);
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
    if (paramInt == 0)
    {
      if ((0.0F <= paramFloat1) && (paramFloat1 <= this.width) && (paramFloat2 >= 0.0F) && (paramFloat2 <= this.height))
      {
        this.startX = paramFloat1;
        this.pressed = true;
        this.thumbDX = ((int)(paramFloat1 - this.thumbX));
        this.startDraging = false;
      }
    }
    else {
      for (;;)
      {
        return true;
        if ((paramInt == 1) || (paramInt == 3))
        {
          if (this.pressed)
          {
            if ((paramInt == 1) && (this.delegate != null)) {
              this.delegate.onSeekBarDrag(this.thumbX / this.width);
            }
            this.pressed = false;
            return true;
          }
        }
        else if ((paramInt == 2) && (this.pressed))
        {
          if (this.startDraging)
          {
            this.thumbX = ((int)(paramFloat1 - this.thumbDX));
            if (this.thumbX >= 0) {
              break label236;
            }
            this.thumbX = 0;
          }
          while ((this.startX != -1.0F) && (Math.abs(paramFloat1 - this.startX) > AndroidUtilities.getPixelsInCM(0.2F, true)))
          {
            if ((this.parentView != null) && (this.parentView.getParent() != null)) {
              this.parentView.getParent().requestDisallowInterceptTouchEvent(true);
            }
            this.startDraging = true;
            this.startX = -1.0F;
            return true;
            label236:
            if (this.thumbX > this.width) {
              this.thumbX = this.width;
            }
          }
        }
      }
    }
    return false;
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
    while (this.thumbX <= this.width) {
      return;
    }
    this.thumbX = this.width;
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