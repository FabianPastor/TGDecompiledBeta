package org.telegram.ui.Components.Paint;

import android.graphics.PointF;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class RenderState
{
  private static final int DEFAULT_STATE_SIZE = 256;
  private int allocatedCount;
  public float alpha;
  public float angle;
  public float baseWeight;
  private ByteBuffer buffer;
  private int count;
  public double remainder;
  public float scale;
  public float spacing;
  
  public boolean addPoint(PointF paramPointF, float paramFloat1, float paramFloat2, float paramFloat3, int paramInt)
  {
    if (((paramInt != -1) && (paramInt >= this.allocatedCount)) || (this.buffer.position() == this.buffer.limit()))
    {
      resizeBuffer();
      return false;
    }
    if (paramInt != -1) {
      this.buffer.position(paramInt * 5 * 4);
    }
    this.buffer.putFloat(paramPointF.x);
    this.buffer.putFloat(paramPointF.y);
    this.buffer.putFloat(paramFloat1);
    this.buffer.putFloat(paramFloat2);
    this.buffer.putFloat(paramFloat3);
    return true;
  }
  
  public void appendValuesCount(int paramInt)
  {
    paramInt = this.count + paramInt;
    if ((paramInt > this.allocatedCount) || (this.buffer == null)) {
      resizeBuffer();
    }
    this.count = paramInt;
  }
  
  public int getCount()
  {
    return this.count;
  }
  
  public void prepare()
  {
    this.count = 0;
    if (this.buffer != null) {
      return;
    }
    this.allocatedCount = 256;
    this.buffer = ByteBuffer.allocateDirect(this.allocatedCount * 5 * 4);
    this.buffer.order(ByteOrder.nativeOrder());
    this.buffer.position(0);
  }
  
  public float read()
  {
    return this.buffer.getFloat();
  }
  
  public void reset()
  {
    this.count = 0;
    this.remainder = 0.0D;
    if (this.buffer != null) {
      this.buffer.position(0);
    }
  }
  
  public void resizeBuffer()
  {
    if (this.buffer != null) {
      this.buffer = null;
    }
    this.allocatedCount = Math.max(this.allocatedCount * 2, 256);
    this.buffer = ByteBuffer.allocateDirect(this.allocatedCount * 5 * 4);
    this.buffer.order(ByteOrder.nativeOrder());
    this.buffer.position(0);
  }
  
  public void setPosition(int paramInt)
  {
    if ((this.buffer == null) || (paramInt < 0) || (paramInt >= this.allocatedCount)) {
      return;
    }
    this.buffer.position(paramInt * 5 * 4);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Paint/RenderState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */