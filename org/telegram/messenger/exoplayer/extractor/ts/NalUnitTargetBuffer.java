package org.telegram.messenger.exoplayer.extractor.ts;

import java.util.Arrays;
import org.telegram.messenger.exoplayer.util.Assertions;

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
    this.nalData[2] = 1;
  }
  
  public void appendToNalUnit(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (!this.isFilling) {
      return;
    }
    paramInt2 -= paramInt1;
    if (this.nalData.length < this.nalLength + paramInt2) {
      this.nalData = Arrays.copyOf(this.nalData, (this.nalLength + paramInt2) * 2);
    }
    System.arraycopy(paramArrayOfByte, paramInt1, this.nalData, this.nalLength, paramInt2);
    this.nalLength += paramInt2;
  }
  
  public boolean endNalUnit(int paramInt)
  {
    if (!this.isFilling) {
      return false;
    }
    this.nalLength -= paramInt;
    this.isFilling = false;
    this.isCompleted = true;
    return true;
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
    boolean bool2 = true;
    if (!this.isFilling)
    {
      bool1 = true;
      Assertions.checkState(bool1);
      if (paramInt != this.targetType) {
        break label53;
      }
    }
    label53:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.isFilling = bool1;
      if (this.isFilling)
      {
        this.nalLength = 3;
        this.isCompleted = false;
      }
      return;
      bool1 = false;
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/ts/NalUnitTargetBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */