package org.telegram.messenger.exoplayer2.audio;

import java.nio.ByteBuffer;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.util.ParsableBitArray;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class Ac3Util
{
  private static final int AC3_SYNCFRAME_AUDIO_SAMPLE_COUNT = 1536;
  private static final int AUDIO_SAMPLES_PER_AUDIO_BLOCK = 256;
  private static final int[] BITRATE_BY_HALF_FRMSIZECOD = { 32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320, 384, 448, 512, 576, 640 };
  private static final int[] BLOCKS_PER_SYNCFRAME_BY_NUMBLKSCOD = { 1, 2, 3, 6 };
  private static final int[] CHANNEL_COUNT_BY_ACMOD;
  private static final int[] SAMPLE_RATE_BY_FSCOD = { 48000, 44100, 32000 };
  private static final int[] SAMPLE_RATE_BY_FSCOD2 = { 24000, 22050, 16000 };
  private static final int[] SYNCFRAME_SIZE_WORDS_BY_HALF_FRMSIZECOD_44_1 = { 69, 87, 104, 121, 139, 174, 208, 243, 278, 348, 417, 487, 557, 696, 835, 975, 1114, 1253, 1393 };
  public static final int TRUEHD_RECHUNK_SAMPLE_COUNT = 8;
  public static final int TRUEHD_SYNCFRAME_PREFIX_LENGTH = 12;
  
  static
  {
    CHANNEL_COUNT_BY_ACMOD = new int[] { 2, 1, 2, 3, 3, 4, 4, 5 };
  }
  
  public static int getAc3SyncframeAudioSampleCount()
  {
    return 1536;
  }
  
  private static int getAc3SyncframeSize(int paramInt1, int paramInt2)
  {
    int i = paramInt2 / 2;
    if ((paramInt1 < 0) || (paramInt1 >= SAMPLE_RATE_BY_FSCOD.length) || (paramInt2 < 0) || (i >= SYNCFRAME_SIZE_WORDS_BY_HALF_FRMSIZECOD_44_1.length)) {
      paramInt1 = -1;
    }
    for (;;)
    {
      return paramInt1;
      paramInt1 = SAMPLE_RATE_BY_FSCOD[paramInt1];
      if (paramInt1 == 44100)
      {
        paramInt1 = (SYNCFRAME_SIZE_WORDS_BY_HALF_FRMSIZECOD_44_1[i] + paramInt2 % 2) * 2;
      }
      else
      {
        paramInt2 = BITRATE_BY_HALF_FRMSIZECOD[i];
        if (paramInt1 == 32000) {
          paramInt1 = paramInt2 * 6;
        } else {
          paramInt1 = paramInt2 * 4;
        }
      }
    }
  }
  
  public static Format parseAc3AnnexFFormat(ParsableByteArray paramParsableByteArray, String paramString1, String paramString2, DrmInitData paramDrmInitData)
  {
    int i = paramParsableByteArray.readUnsignedByte();
    int j = SAMPLE_RATE_BY_FSCOD[((i & 0xC0) >> 6)];
    int k = paramParsableByteArray.readUnsignedByte();
    int m = CHANNEL_COUNT_BY_ACMOD[((k & 0x38) >> 3)];
    i = m;
    if ((k & 0x4) != 0) {
      i = m + 1;
    }
    return Format.createAudioSampleFormat(paramString1, "audio/ac3", null, -1, -1, i, j, null, paramDrmInitData, 0, paramString2);
  }
  
  public static Ac3SyncFrameInfo parseAc3SyncframeInfo(ParsableBitArray paramParsableBitArray)
  {
    int i = paramParsableBitArray.getPosition();
    paramParsableBitArray.skipBits(40);
    int k;
    int m;
    label92:
    int i2;
    int i4;
    if (paramParsableBitArray.readBits(5) == 16)
    {
      j = 1;
      paramParsableBitArray.setPosition(i);
      i = -1;
      if (j == 0) {
        break label904;
      }
      paramParsableBitArray.skipBits(16);
      k = paramParsableBitArray.readBits(2);
      paramParsableBitArray.skipBits(3);
      m = (paramParsableBitArray.readBits(11) + 1) * 2;
      n = paramParsableBitArray.readBits(2);
      if (n != 3) {
        break label636;
      }
      i1 = 3;
      j = SAMPLE_RATE_BY_FSCOD2[paramParsableBitArray.readBits(2)];
      i = 6;
      i2 = i * 256;
      i3 = paramParsableBitArray.readBits(3);
      bool = paramParsableBitArray.readBit();
      i4 = CHANNEL_COUNT_BY_ACMOD[i3];
      if (!bool) {
        break label660;
      }
      i5 = 1;
      label128:
      i4 += i5;
      paramParsableBitArray.skipBits(10);
      if (paramParsableBitArray.readBit()) {
        paramParsableBitArray.skipBits(8);
      }
      if (i3 == 0)
      {
        paramParsableBitArray.skipBits(5);
        if (paramParsableBitArray.readBit()) {
          paramParsableBitArray.skipBits(8);
        }
      }
      if ((k == 1) && (paramParsableBitArray.readBit())) {
        paramParsableBitArray.skipBits(16);
      }
      if (paramParsableBitArray.readBit())
      {
        if (i3 > 2) {
          paramParsableBitArray.skipBits(2);
        }
        if (((i3 & 0x1) != 0) && (i3 > 2)) {
          paramParsableBitArray.skipBits(6);
        }
        if ((i3 & 0x4) != 0) {
          paramParsableBitArray.skipBits(6);
        }
        if ((bool) && (paramParsableBitArray.readBit())) {
          paramParsableBitArray.skipBits(5);
        }
        if (k == 0)
        {
          if (paramParsableBitArray.readBit()) {
            paramParsableBitArray.skipBits(6);
          }
          if ((i3 == 0) && (paramParsableBitArray.readBit())) {
            paramParsableBitArray.skipBits(6);
          }
          if (paramParsableBitArray.readBit()) {
            paramParsableBitArray.skipBits(6);
          }
          i5 = paramParsableBitArray.readBits(2);
          if (i5 != 1) {
            break label666;
          }
          paramParsableBitArray.skipBits(5);
          label328:
          if (i3 < 2)
          {
            if (paramParsableBitArray.readBit()) {
              paramParsableBitArray.skipBits(14);
            }
            if ((i3 == 0) && (paramParsableBitArray.readBit())) {
              paramParsableBitArray.skipBits(14);
            }
          }
          if (paramParsableBitArray.readBit())
          {
            if (i1 != 0) {
              break label877;
            }
            paramParsableBitArray.skipBits(5);
          }
        }
      }
    }
    for (;;)
    {
      if (paramParsableBitArray.readBit())
      {
        paramParsableBitArray.skipBits(5);
        if (i3 == 2) {
          paramParsableBitArray.skipBits(4);
        }
        if (i3 >= 6) {
          paramParsableBitArray.skipBits(2);
        }
        if (paramParsableBitArray.readBit()) {
          paramParsableBitArray.skipBits(8);
        }
        if ((i3 == 0) && (paramParsableBitArray.readBit())) {
          paramParsableBitArray.skipBits(8);
        }
        if (n < 3) {
          paramParsableBitArray.skipBit();
        }
      }
      if ((k == 0) && (i1 != 3)) {
        paramParsableBitArray.skipBit();
      }
      if ((k == 2) && ((i1 == 3) || (paramParsableBitArray.readBit()))) {
        paramParsableBitArray.skipBits(6);
      }
      String str1 = "audio/eac3";
      str2 = str1;
      i = k;
      i1 = i4;
      i5 = j;
      n = m;
      i3 = i2;
      if (paramParsableBitArray.readBit())
      {
        str2 = str1;
        i = k;
        i1 = i4;
        i5 = j;
        n = m;
        i3 = i2;
        if (paramParsableBitArray.readBits(6) == 1)
        {
          str2 = str1;
          i = k;
          i1 = i4;
          i5 = j;
          n = m;
          i3 = i2;
          if (paramParsableBitArray.readBits(8) == 1)
          {
            str2 = "audio/eac3-joc";
            i3 = i2;
            n = m;
            i5 = j;
            i1 = i4;
            i = k;
          }
        }
      }
      return new Ac3SyncFrameInfo(str2, i, i1, i5, n, i3, null);
      j = 0;
      break;
      label636:
      i1 = paramParsableBitArray.readBits(2);
      i = BLOCKS_PER_SYNCFRAME_BY_NUMBLKSCOD[i1];
      j = SAMPLE_RATE_BY_FSCOD[n];
      break label92;
      label660:
      i5 = 0;
      break label128;
      label666:
      if (i5 == 2)
      {
        paramParsableBitArray.skipBits(12);
        break label328;
      }
      if (i5 != 3) {
        break label328;
      }
      i5 = paramParsableBitArray.readBits(5);
      if (paramParsableBitArray.readBit())
      {
        paramParsableBitArray.skipBits(5);
        if (paramParsableBitArray.readBit()) {
          paramParsableBitArray.skipBits(4);
        }
        if (paramParsableBitArray.readBit()) {
          paramParsableBitArray.skipBits(4);
        }
        if (paramParsableBitArray.readBit()) {
          paramParsableBitArray.skipBits(4);
        }
        if (paramParsableBitArray.readBit()) {
          paramParsableBitArray.skipBits(4);
        }
        if (paramParsableBitArray.readBit()) {
          paramParsableBitArray.skipBits(4);
        }
        if (paramParsableBitArray.readBit()) {
          paramParsableBitArray.skipBits(4);
        }
        if (paramParsableBitArray.readBit()) {
          paramParsableBitArray.skipBits(4);
        }
        if (paramParsableBitArray.readBit())
        {
          if (paramParsableBitArray.readBit()) {
            paramParsableBitArray.skipBits(4);
          }
          if (paramParsableBitArray.readBit()) {
            paramParsableBitArray.skipBits(4);
          }
        }
      }
      if (paramParsableBitArray.readBit())
      {
        paramParsableBitArray.skipBits(5);
        if (paramParsableBitArray.readBit())
        {
          paramParsableBitArray.skipBits(7);
          if (paramParsableBitArray.readBit()) {
            paramParsableBitArray.skipBits(8);
          }
        }
      }
      paramParsableBitArray.skipBits((i5 + 2) * 8);
      paramParsableBitArray.byteAlign();
      break label328;
      label877:
      for (i5 = 0; i5 < i; i5++) {
        if (paramParsableBitArray.readBit()) {
          paramParsableBitArray.skipBits(5);
        }
      }
    }
    label904:
    String str2 = "audio/ac3";
    paramParsableBitArray.skipBits(32);
    int i1 = paramParsableBitArray.readBits(2);
    int n = getAc3SyncframeSize(i1, paramParsableBitArray.readBits(6));
    paramParsableBitArray.skipBits(8);
    int j = paramParsableBitArray.readBits(3);
    if (((j & 0x1) != 0) && (j != 1)) {
      paramParsableBitArray.skipBits(2);
    }
    if ((j & 0x4) != 0) {
      paramParsableBitArray.skipBits(2);
    }
    if (j == 2) {
      paramParsableBitArray.skipBits(2);
    }
    int i5 = SAMPLE_RATE_BY_FSCOD[i1];
    int i3 = 1536;
    boolean bool = paramParsableBitArray.readBit();
    i1 = CHANNEL_COUNT_BY_ACMOD[j];
    if (bool) {}
    for (j = 1;; j = 0)
    {
      i1 += j;
      break;
    }
  }
  
  public static int parseAc3SyncframeSize(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte.length < 5) {}
    for (int i = -1;; i = getAc3SyncframeSize((paramArrayOfByte[4] & 0xC0) >> 6, paramArrayOfByte[4] & 0x3F)) {
      return i;
    }
  }
  
  public static Format parseEAc3AnnexFFormat(ParsableByteArray paramParsableByteArray, String paramString1, String paramString2, DrmInitData paramDrmInitData)
  {
    paramParsableByteArray.skipBytes(2);
    int i = paramParsableByteArray.readUnsignedByte();
    int j = SAMPLE_RATE_BY_FSCOD[((i & 0xC0) >> 6)];
    int k = paramParsableByteArray.readUnsignedByte();
    int m = CHANNEL_COUNT_BY_ACMOD[((k & 0xE) >> 1)];
    i = m;
    if ((k & 0x1) != 0) {
      i = m + 1;
    }
    m = i;
    if ((paramParsableByteArray.readUnsignedByte() & 0x1E) >> 1 > 0)
    {
      m = i;
      if ((paramParsableByteArray.readUnsignedByte() & 0x2) != 0) {
        m = i + 2;
      }
    }
    String str1 = "audio/eac3";
    String str2 = str1;
    if (paramParsableByteArray.bytesLeft() > 0)
    {
      str2 = str1;
      if ((paramParsableByteArray.readUnsignedByte() & 0x1) != 0) {
        str2 = "audio/eac3-joc";
      }
    }
    return Format.createAudioSampleFormat(paramString1, str2, null, -1, -1, m, j, null, paramDrmInitData, 0, paramString2);
  }
  
  public static int parseEAc3SyncframeAudioSampleCount(ByteBuffer paramByteBuffer)
  {
    if ((paramByteBuffer.get(paramByteBuffer.position() + 4) & 0xC0) >> 6 == 3) {}
    for (int i = 6;; i = BLOCKS_PER_SYNCFRAME_BY_NUMBLKSCOD[((paramByteBuffer.get(paramByteBuffer.position() + 4) & 0x30) >> 4)]) {
      return i * 256;
    }
  }
  
  public static int parseTrueHdSyncframeAudioSampleCount(ByteBuffer paramByteBuffer)
  {
    if (paramByteBuffer.getInt(paramByteBuffer.position() + 4) != -NUM) {}
    for (int i = 0;; i = 40 << (paramByteBuffer.get(paramByteBuffer.position() + 8) & 0x7)) {
      return i;
    }
  }
  
  public static int parseTrueHdSyncframeAudioSampleCount(byte[] paramArrayOfByte)
  {
    if ((paramArrayOfByte[4] != -8) || (paramArrayOfByte[5] != 114) || (paramArrayOfByte[6] != 111) || (paramArrayOfByte[7] != -70)) {}
    for (int i = 0;; i = 40 << (paramArrayOfByte[8] & 0x7)) {
      return i;
    }
  }
  
  public static final class Ac3SyncFrameInfo
  {
    public static final int STREAM_TYPE_TYPE0 = 0;
    public static final int STREAM_TYPE_TYPE1 = 1;
    public static final int STREAM_TYPE_TYPE2 = 2;
    public static final int STREAM_TYPE_UNDEFINED = -1;
    public final int channelCount;
    public final int frameSize;
    public final String mimeType;
    public final int sampleCount;
    public final int sampleRate;
    public final int streamType;
    
    private Ac3SyncFrameInfo(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      this.mimeType = paramString;
      this.streamType = paramInt1;
      this.channelCount = paramInt2;
      this.sampleRate = paramInt3;
      this.frameSize = paramInt4;
      this.sampleCount = paramInt5;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/audio/Ac3Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */