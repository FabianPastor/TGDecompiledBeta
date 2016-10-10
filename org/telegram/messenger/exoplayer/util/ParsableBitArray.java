package org.telegram.messenger.exoplayer.util;

public final class ParsableBitArray
{
  private int bitOffset;
  private int byteLimit;
  private int byteOffset;
  public byte[] data;
  
  public ParsableBitArray() {}
  
  public ParsableBitArray(byte[] paramArrayOfByte)
  {
    this(paramArrayOfByte, paramArrayOfByte.length);
  }
  
  public ParsableBitArray(byte[] paramArrayOfByte, int paramInt)
  {
    this.data = paramArrayOfByte;
    this.byteLimit = paramInt;
  }
  
  private void assertValidOffset()
  {
    if ((this.byteOffset >= 0) && (this.bitOffset >= 0) && (this.bitOffset < 8) && ((this.byteOffset < this.byteLimit) || ((this.byteOffset == this.byteLimit) && (this.bitOffset == 0)))) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      return;
    }
  }
  
  private int readExpGolombCodeNum()
  {
    int i = 0;
    while (!readBit()) {
      i += 1;
    }
    if (i > 0) {}
    for (int j = readBits(i);; j = 0) {
      return j + ((1 << i) - 1);
    }
  }
  
  public int bitsLeft()
  {
    return (this.byteLimit - this.byteOffset) * 8 - this.bitOffset;
  }
  
  public boolean canReadExpGolombCodedNum()
  {
    int k = this.byteOffset;
    int m = this.bitOffset;
    int i = 0;
    while ((this.byteOffset < this.byteLimit) && (!readBit())) {
      i += 1;
    }
    if (this.byteOffset == this.byteLimit) {}
    for (int j = 1;; j = 0)
    {
      this.byteOffset = k;
      this.bitOffset = m;
      if ((j != 0) || (bitsLeft() < i * 2 + 1)) {
        break;
      }
      return true;
    }
    return false;
  }
  
  public int getPosition()
  {
    return this.byteOffset * 8 + this.bitOffset;
  }
  
  public boolean readBit()
  {
    return readBits(1) == 1;
  }
  
  public int readBits(int paramInt)
  {
    if (paramInt == 0) {
      return 0;
    }
    int k = 0;
    int m = paramInt / 8;
    int j = 0;
    int i = paramInt;
    paramInt = k;
    if (j < m)
    {
      if (this.bitOffset != 0) {}
      for (k = (this.data[this.byteOffset] & 0xFF) << this.bitOffset | (this.data[(this.byteOffset + 1)] & 0xFF) >>> 8 - this.bitOffset;; k = this.data[this.byteOffset])
      {
        i -= 8;
        paramInt |= (k & 0xFF) << i;
        this.byteOffset += 1;
        j += 1;
        break;
      }
    }
    j = paramInt;
    if (i > 0)
    {
      j = this.bitOffset + i;
      i = (byte)(255 >> 8 - i);
      if (j <= 8) {
        break label225;
      }
      paramInt |= ((this.data[this.byteOffset] & 0xFF) << j - 8 | (this.data[(this.byteOffset + 1)] & 0xFF) >> 16 - j) & i;
      this.byteOffset += 1;
    }
    for (;;)
    {
      this.bitOffset = (j % 8);
      j = paramInt;
      assertValidOffset();
      return j;
      label225:
      i = paramInt | (this.data[this.byteOffset] & 0xFF) >> 8 - j & i;
      paramInt = i;
      if (j == 8)
      {
        this.byteOffset += 1;
        paramInt = i;
      }
    }
  }
  
  public int readSignedExpGolombCodedInt()
  {
    int j = readExpGolombCodeNum();
    if (j % 2 == 0) {}
    for (int i = -1;; i = 1) {
      return i * ((j + 1) / 2);
    }
  }
  
  public int readUnsignedExpGolombCodedInt()
  {
    return readExpGolombCodeNum();
  }
  
  public void reset(byte[] paramArrayOfByte)
  {
    reset(paramArrayOfByte, paramArrayOfByte.length);
  }
  
  public void reset(byte[] paramArrayOfByte, int paramInt)
  {
    this.data = paramArrayOfByte;
    this.byteOffset = 0;
    this.bitOffset = 0;
    this.byteLimit = paramInt;
  }
  
  public void setPosition(int paramInt)
  {
    this.byteOffset = (paramInt / 8);
    this.bitOffset = (paramInt - this.byteOffset * 8);
    assertValidOffset();
  }
  
  public void skipBits(int paramInt)
  {
    this.byteOffset += paramInt / 8;
    this.bitOffset += paramInt % 8;
    if (this.bitOffset > 7)
    {
      this.byteOffset += 1;
      this.bitOffset -= 8;
    }
    assertValidOffset();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/util/ParsableBitArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */