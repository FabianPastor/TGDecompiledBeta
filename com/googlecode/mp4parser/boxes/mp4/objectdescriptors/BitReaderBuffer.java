package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import java.nio.ByteBuffer;

public class BitReaderBuffer
{
  private ByteBuffer buffer;
  int initialPos;
  int position;
  
  public BitReaderBuffer(ByteBuffer paramByteBuffer)
  {
    this.buffer = paramByteBuffer;
    this.initialPos = paramByteBuffer.position();
  }
  
  public int byteSync()
  {
    int j = 8 - this.position % 8;
    int i = j;
    if (j == 8) {
      i = 0;
    }
    readBits(i);
    return i;
  }
  
  public int getPosition()
  {
    return this.position;
  }
  
  public int readBits(int paramInt)
  {
    int i = this.buffer.get(this.initialPos + this.position / 8);
    int j;
    if (i < 0)
    {
      i += 256;
      j = 8 - this.position % 8;
      if (paramInt > j) {
        break label115;
      }
      i = (i << this.position % 8 & 0xFF) >> this.position % 8 + (j - paramInt);
      this.position += paramInt;
    }
    for (paramInt = i;; paramInt = (readBits(j) << paramInt) + readBits(paramInt))
    {
      this.buffer.position(this.initialPos + (int)Math.ceil(this.position / 8.0D));
      return paramInt;
      break;
      label115:
      paramInt -= j;
    }
  }
  
  public boolean readBool()
  {
    return readBits(1) == 1;
  }
  
  public int remainingBits()
  {
    return this.buffer.limit() * 8 - this.position;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/mp4/objectdescriptors/BitReaderBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */