package org.telegram.messenger.audioinfo.mp3;

public class MP3Frame
{
  private final byte[] bytes;
  private final Header header;
  
  MP3Frame(Header paramHeader, byte[] paramArrayOfByte)
  {
    this.header = paramHeader;
    this.bytes = paramArrayOfByte;
  }
  
  public Header getHeader()
  {
    return this.header;
  }
  
  public int getNumberOfFrames()
  {
    int i;
    if (isXingFrame())
    {
      i = this.header.getXingOffset();
      if ((this.bytes[(i + 7)] & 0x1) != 0) {
        return (this.bytes[(i + 8)] & 0xFF) << 24 | (this.bytes[(i + 9)] & 0xFF) << 16 | (this.bytes[(i + 10)] & 0xFF) << 8 | this.bytes[(i + 11)] & 0xFF;
      }
    }
    else if (isVBRIFrame())
    {
      i = this.header.getVBRIOffset();
      return (this.bytes[(i + 14)] & 0xFF) << 24 | (this.bytes[(i + 15)] & 0xFF) << 16 | (this.bytes[(i + 16)] & 0xFF) << 8 | this.bytes[(i + 17)] & 0xFF;
    }
    return -1;
  }
  
  public int getSize()
  {
    return this.bytes.length;
  }
  
  boolean isChecksumError()
  {
    if ((this.header.getProtection() == 0) && (this.header.getLayer() == 1))
    {
      CRC16 localCRC16 = new CRC16();
      localCRC16.update(this.bytes[2]);
      localCRC16.update(this.bytes[3]);
      int j = this.header.getSideInfoSize();
      int i = 0;
      while (i < j)
      {
        localCRC16.update(this.bytes[(i + 6)]);
        i += 1;
      }
      return ((this.bytes[4] & 0xFF) << 8 | this.bytes[5] & 0xFF) != localCRC16.getValue();
    }
    return false;
  }
  
  boolean isVBRIFrame()
  {
    int i = this.header.getVBRIOffset();
    if (this.bytes.length < i + 26) {}
    while ((this.bytes[i] != 86) || (this.bytes[(i + 1)] != 66) || (this.bytes[(i + 2)] != 82) || (this.bytes[(i + 3)] != 73)) {
      return false;
    }
    return true;
  }
  
  boolean isXingFrame()
  {
    int i = this.header.getXingOffset();
    if (this.bytes.length < i + 12) {}
    do
    {
      do
      {
        return false;
      } while ((i < 0) || (this.bytes.length < i + 8));
      if ((this.bytes[i] == 88) && (this.bytes[(i + 1)] == 105) && (this.bytes[(i + 2)] == 110) && (this.bytes[(i + 3)] == 103)) {
        return true;
      }
    } while ((this.bytes[i] != 73) || (this.bytes[(i + 1)] != 110) || (this.bytes[(i + 2)] != 102) || (this.bytes[(i + 3)] != 111));
    return true;
  }
  
  static final class CRC16
  {
    private short crc = -1;
    
    public short getValue()
    {
      return this.crc;
    }
    
    public void reset()
    {
      this.crc = -1;
    }
    
    public void update(byte paramByte)
    {
      update(paramByte, 8);
    }
    
    public void update(int paramInt1, int paramInt2)
    {
      paramInt2 = 1 << paramInt2 - 1;
      int i;
      label18:
      int j;
      if ((this.crc & 0x8000) == 0)
      {
        i = 1;
        if ((paramInt1 & paramInt2) != 0) {
          break label73;
        }
        j = 1;
        label27:
        if ((j ^ i) == 0) {
          break label79;
        }
        this.crc = ((short)(this.crc << 1));
      }
      label73:
      label79:
      for (this.crc = ((short)(this.crc ^ 0x8005));; this.crc = ((short)(this.crc << 1)))
      {
        i = paramInt2 >>> 1;
        paramInt2 = i;
        if (i != 0) {
          break;
        }
        return;
        i = 0;
        break label18;
        j = 0;
        break label27;
      }
    }
  }
  
  public static class Header
  {
    private static final int[][] BITRATES;
    private static final int[][] BITRATES_COLUMN;
    private static final int[][] FREQUENCIES;
    private static final int MPEG_BITRATE_FREE = 0;
    private static final int MPEG_BITRATE_RESERVED = 15;
    public static final int MPEG_CHANNEL_MODE_MONO = 3;
    private static final int MPEG_FRQUENCY_RESERVED = 3;
    public static final int MPEG_LAYER_1 = 3;
    public static final int MPEG_LAYER_2 = 2;
    public static final int MPEG_LAYER_3 = 1;
    private static final int MPEG_LAYER_RESERVED = 0;
    public static final int MPEG_PROTECTION_CRC = 0;
    public static final int MPEG_VERSION_1 = 3;
    public static final int MPEG_VERSION_2 = 2;
    public static final int MPEG_VERSION_2_5 = 0;
    private static final int MPEG_VERSION_RESERVED = 1;
    private static final int[][] SIDE_INFO_SIZES;
    private static final int[][] SIZE_COEFFICIENTS;
    private static final int[] SLOT_SIZES;
    private final int bitrate;
    private final int channelMode;
    private final int frequency;
    private final int layer;
    private final int padding;
    private final int protection;
    private final int version;
    
    static
    {
      int[] arrayOfInt1 = { 11025, -1, 22050, 44100 };
      int[] arrayOfInt2 = { 8000, -1, 16000, 32000 };
      FREQUENCIES = new int[][] { arrayOfInt1, { 12000, -1, 24000, 48000 }, arrayOfInt2, { -1, -1, -1, -1 } };
      arrayOfInt1 = new int[] { 256000, 128000, 112000, 128000, 64000 };
      arrayOfInt2 = new int[] { 384000, 256000, 224000, 192000, 128000 };
      int[] arrayOfInt3 = { 416000, 320000, 256000, 224000, 144000 };
      BITRATES = new int[][] { { 0, 0, 0, 0, 0 }, { 32000, 32000, 32000, 32000, 8000 }, { 64000, 48000, 40000, 48000, 16000 }, { 96000, 56000, 48000, 56000, 24000 }, { 128000, 64000, 56000, 64000, 32000 }, { 160000, 80000, 64000, 80000, 40000 }, { 192000, 96000, 80000, 96000, 48000 }, { 224000, 112000, 96000, 112000, 56000 }, arrayOfInt1, { 288000, 160000, 128000, 144000, 80000 }, { 320000, 192000, 160000, 160000, 96000 }, { 352000, 224000, 192000, 176000, 112000 }, arrayOfInt2, arrayOfInt3, { 448000, 384000, 320000, 256000, 160000 }, { -1, -1, -1, -1, -1 } };
      BITRATES_COLUMN = new int[][] { { -1, 4, 4, 3 }, { -1, -1, -1, -1 }, { -1, 4, 4, 3 }, { -1, 2, 1, 0 } };
      arrayOfInt1 = new int[] { -1, 72, 144, 12 };
      SIZE_COEFFICIENTS = new int[][] { { -1, 72, 144, 12 }, { -1, -1, -1, -1 }, arrayOfInt1, { -1, 144, 144, 12 } };
      SLOT_SIZES = new int[] { -1, 1, 1, 4 };
      arrayOfInt1 = new int[] { 17, -1, 17, 32 };
      arrayOfInt2 = new int[] { 17, -1, 17, 32 };
      arrayOfInt3 = new int[] { 9, -1, 9, 17 };
      SIDE_INFO_SIZES = new int[][] { { 17, -1, 17, 32 }, arrayOfInt1, arrayOfInt2, arrayOfInt3 };
    }
    
    public Header(int paramInt1, int paramInt2, int paramInt3)
      throws MP3Exception
    {
      this.version = (paramInt1 >> 3 & 0x3);
      if (this.version == 1) {
        throw new MP3Exception("Reserved version");
      }
      this.layer = (paramInt1 >> 1 & 0x3);
      if (this.layer == 0) {
        throw new MP3Exception("Reserved layer");
      }
      this.bitrate = (paramInt2 >> 4 & 0xF);
      if (this.bitrate == 15) {
        throw new MP3Exception("Reserved bitrate");
      }
      if (this.bitrate == 0) {
        throw new MP3Exception("Free bitrate");
      }
      this.frequency = (paramInt2 >> 2 & 0x3);
      if (this.frequency == 3) {
        throw new MP3Exception("Reserved frequency");
      }
      this.channelMode = (paramInt3 >> 6 & 0x3);
      this.padding = (paramInt2 >> 1 & 0x1);
      this.protection = (paramInt1 & 0x1);
      paramInt1 = 4;
      if (this.protection == 0) {
        paramInt1 = 4 + 2;
      }
      paramInt2 = paramInt1;
      if (this.layer == 1) {
        paramInt2 = paramInt1 + getSideInfoSize();
      }
      if (getFrameSize() < paramInt2) {
        throw new MP3Exception("Frame size must be at least " + paramInt2);
      }
    }
    
    public int getBitrate()
    {
      return BITRATES[this.bitrate][BITRATES_COLUMN[this.version][this.layer]];
    }
    
    public int getChannelMode()
    {
      return this.channelMode;
    }
    
    public int getDuration()
    {
      return (int)getTotalDuration(getFrameSize());
    }
    
    public int getFrameSize()
    {
      return (SIZE_COEFFICIENTS[this.version][this.layer] * getBitrate() / getFrequency() + this.padding) * SLOT_SIZES[this.layer];
    }
    
    public int getFrequency()
    {
      return FREQUENCIES[this.frequency][this.version];
    }
    
    public int getLayer()
    {
      return this.layer;
    }
    
    public int getProtection()
    {
      return this.protection;
    }
    
    public int getSampleCount()
    {
      if (this.layer == 3) {
        return 384;
      }
      return 1152;
    }
    
    public int getSideInfoSize()
    {
      return SIDE_INFO_SIZES[this.channelMode][this.version];
    }
    
    public long getTotalDuration(long paramLong)
    {
      long l = 1000L * (getSampleCount() * paramLong) / (getFrameSize() * getFrequency());
      paramLong = l;
      if (getVersion() != 3)
      {
        paramLong = l;
        if (getChannelMode() == 3) {
          paramLong = l / 2L;
        }
      }
      return paramLong;
    }
    
    public int getVBRIOffset()
    {
      return 36;
    }
    
    public int getVersion()
    {
      return this.version;
    }
    
    public int getXingOffset()
    {
      return getSideInfoSize() + 4;
    }
    
    public boolean isCompatible(Header paramHeader)
    {
      return (this.layer == paramHeader.layer) && (this.version == paramHeader.version) && (this.frequency == paramHeader.frequency) && (this.channelMode == paramHeader.channelMode);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/audioinfo/mp3/MP3Frame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */