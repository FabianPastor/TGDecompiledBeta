package org.telegram.messenger.exoplayer2.extractor.ts;

import java.util.Arrays;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class NalUnitTargetBuffer
{
  private boolean isCompleted;
  private boolean isFilling;
  public byte[] nalData;
  public int nalLength;
  private final int targetType;
  
  public NalUnitTargetBuffer(int paramInt1, int paramInt2)
  {
    this.targetType = paramInt1;
    this.nalData = new byte[paramInt2 + 3];
    this.nalData[2] = ((byte)1);
  }
  
  public void appendToNalUnit(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (!this.isFilling) {}
    for (;;)
    {
      return;
      paramInt2 -= paramInt1;
      if (this.nalData.length < this.nalLength + paramInt2) {
        this.nalData = Arrays.copyOf(this.nalData, (this.nalLength + paramInt2) * 2);
      }
      System.arraycopy(paramArrayOfByte, paramInt1, this.nalData, this.nalLength, paramInt2);
      this.nalLength += paramInt2;
    }
  }
  
  public boolean endNalUnit(int paramInt)
  {
    boolean bool = false;
    if (!this.isFilling) {}
    for (;;)
    {
      return bool;
      this.nalLength -= paramInt;
      this.isFilling = false;
      this.isCompleted = true;
      bool = true;
    }
  }
  
  public boolean isCompleted()
  {
    return this.isCompleted;
  }
  
  public void reset()
  {
    this.isFilling = false;
    this.isCompleted = false;
  }
  
  public void startNalUnit(int paramInt)
  {
    boolean bool1 = true;
    if (!this.isFilling)
    {
      bool2 = true;
      Assertions.checkState(bool2);
      if (paramInt != this.targetType) {
        break label53;
      }
    }
    label53:
    for (boolean bool2 = bool1;; bool2 = false)
    {
      this.isFilling = bool2;
      if (this.isFilling)
      {
        this.nalLength = 3;
        this.isCompleted = false;
      }
      return;
      bool2 = false;
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ts/NalUnitTargetBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */