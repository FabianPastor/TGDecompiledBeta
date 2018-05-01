package org.telegram.messenger.exoplayer2.util;

public final class ParsableNalUnitBitArray
{
  private int bitOffset;
  private int byteLimit;
  private int byteOffset;
  private byte[] data;
  
  public ParsableNalUnitBitArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    reset(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  private void assertValidOffset()
  {
    if ((this.byteOffset >= 0) && ((this.byteOffset < this.byteLimit) || ((this.byteOffset == this.byteLimit) && (this.bitOffset == 0)))) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      return;
    }
  }
  
  private int readExpGolombCodeNum()
  {
    for (int i = 0; !readBit(); i++) {}
    if (i > 0) {}
    for (int j = readBits(i);; j = 0) {
      return j + ((1 << i) - 1);
    }
  }
  
  private boolean shouldSkipByte(int paramInt)
  {
    if ((2 <= paramInt) && (paramInt < this.byteLimit) && (this.data[paramInt] == 3) && (this.data[(paramInt - 2)] == 0) && (this.data[(paramInt - 1)] == 0)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean canReadBits(int paramInt)
  {
    int i = this.byteOffset;
    int j = paramInt / 8;
    int k = this.byteOffset + j;
    int m = this.bitOffset + paramInt - j * 8;
    j = m;
    paramInt = k;
    if (m > 7)
    {
      paramInt = k + 1;
      j = m - 8;
    }
    k = i + 1;
    m = paramInt;
    paramInt = k;
    while ((paramInt <= m) && (m < this.byteLimit))
    {
      i = paramInt;
      k = m;
      if (shouldSkipByte(paramInt))
      {
        k = m + 1;
        i = paramInt + 2;
      }
      paramInt = i + 1;
      m = k;
    }
    if ((m < this.byteLimit) || ((m == this.byteLimit) && (j == 0))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean canReadExpGolombCodedNum()
  {
    boolean bool = true;
    int i = this.byteOffset;
    int j = this.bitOffset;
    for (int k = 0; (this.byteOffset < this.byteLimit) && (!readBit()); k++) {}
    int m;
    if (this.byteOffset == this.byteLimit)
    {
      m = 1;
      this.byteOffset = i;
      this.bitOffset = j;
      if ((m != 0) || (!canReadBits(k * 2 + 1))) {
        break label89;
      }
    }
    for (;;)
    {
      return bool;
      m = 0;
      break;
      label89:
      bool = false;
    }
  }
  
  public boolean readBit()
  {
    if ((this.data[this.byteOffset] & 128 >> this.bitOffset) != 0) {}
    for (boolean bool = true;; bool = false)
    {
      skipBit();
      return bool;
    }
  }
  
  public int readBits(int paramInt)
  {
    int i = 2;
    int j = 0;
    this.bitOffset += paramInt;
    int m;
    if (this.bitOffset > 8)
    {
      this.bitOffset -= 8;
      k = j | (this.data[this.byteOffset] & 0xFF) << this.bitOffset;
      m = this.byteOffset;
      if (shouldSkipByte(this.byteOffset + 1)) {}
      for (j = 2;; j = 1)
      {
        this.byteOffset = (j + m);
        j = k;
        break;
      }
    }
    int k = this.data[this.byteOffset];
    int n = this.bitOffset;
    if (this.bitOffset == 8)
    {
      this.bitOffset = 0;
      m = this.byteOffset;
      if (!shouldSkipByte(this.byteOffset + 1)) {
        break label180;
      }
    }
    for (;;)
    {
      this.byteOffset = (m + i);
      assertValidOffset();
      return (j | (k & 0xFF) >> 8 - n) & -1 >>> 32 - paramInt;
      label180:
      i = 1;
    }
  }
  
  public int readSignedExpGolombCodedInt()
  {
    int i = readExpGolombCodeNum();
    if (i % 2 == 0) {}
    for (int j = -1;; j = 1) {
      return j * ((i + 1) / 2);
    }
  }
  
  public int readUnsignedExpGolombCodedInt()
  {
    return readExpGolombCodeNum();
  }
  
  public void reset(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.data = paramArrayOfByte;
    this.byteOffset = paramInt1;
    this.byteLimit = paramInt2;
    this.bitOffset = 0;
    assertValidOffset();
  }
  
  public void skipBit()
  {
    int i = this.bitOffset + 1;
    this.bitOffset = i;
    int j;
    if (i == 8)
    {
      this.bitOffset = 0;
      j = this.byteOffset;
      if (!shouldSkipByte(this.byteOffset + 1)) {
        break label55;
      }
    }
    label55:
    for (i = 2;; i = 1)
    {
      this.byteOffset = (i + j);
      assertValidOffset();
      return;
    }
  }
  
  public void skipBits(int paramInt)
  {
    int i = this.byteOffset;
    int j = paramInt / 8;
    this.byteOffset += j;
    this.bitOffset += paramInt - j * 8;
    if (this.bitOffset > 7)
    {
      this.byteOffset += 1;
      this.bitOffset -= 8;
    }
    for (paramInt = i + 1; paramInt <= this.byteOffset; paramInt = j + 1)
    {
      j = paramInt;
      if (shouldSkipByte(paramInt))
      {
        this.byteOffset += 1;
        j = paramInt + 2;
      }
    }
    assertValidOffset();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/util/ParsableNalUnitBitArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */