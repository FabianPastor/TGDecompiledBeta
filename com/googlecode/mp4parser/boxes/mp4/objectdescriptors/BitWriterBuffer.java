package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import java.nio.ByteBuffer;

public class BitWriterBuffer
{
  private ByteBuffer buffer;
  int initialPos;
  int position = 0;
  
  static
  {
    if (!BitWriterBuffer.class.desiredAssertionStatus()) {}
    for (boolean bool = true;; bool = false)
    {
      $assertionsDisabled = bool;
      return;
    }
  }
  
  public BitWriterBuffer(ByteBuffer paramByteBuffer)
  {
    this.buffer = paramByteBuffer;
    this.initialPos = paramByteBuffer.position();
  }
  
  public void writeBits(int paramInt1, int paramInt2)
  {
    int j = 1;
    if ((!$assertionsDisabled) && (paramInt1 > (1 << paramInt2) - 1)) {
      throw new AssertionError(String.format("Trying to write a value bigger (%s) than the number bits (%s) allows. Please mask the value before writing it and make your code is really working as intended.", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf((1 << paramInt2) - 1) }));
    }
    int m = 8 - this.position % 8;
    int i;
    ByteBuffer localByteBuffer;
    if (paramInt2 <= m)
    {
      int k = this.buffer.get(this.initialPos + this.position / 8);
      i = k;
      if (k < 0) {
        i = k + 256;
      }
      i += (paramInt1 << m - paramInt2);
      localByteBuffer = this.buffer;
      k = this.initialPos;
      m = this.position / 8;
      paramInt1 = i;
      if (i > 127) {
        paramInt1 = i - 256;
      }
      localByteBuffer.put(k + m, (byte)paramInt1);
      this.position += paramInt2;
      localByteBuffer = this.buffer;
      paramInt2 = this.initialPos;
      i = this.position / 8;
      if (this.position % 8 <= 0) {
        break label247;
      }
    }
    label247:
    for (paramInt1 = j;; paramInt1 = 0)
    {
      localByteBuffer.position(paramInt1 + (paramInt2 + i));
      return;
      paramInt2 -= m;
      writeBits(paramInt1 >> paramInt2, m);
      writeBits((1 << paramInt2) - 1 & paramInt1, paramInt2);
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/mp4/objectdescriptors/BitWriterBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */