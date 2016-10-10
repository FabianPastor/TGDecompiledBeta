package org.telegram.messenger.exoplayer.util;

import java.nio.ByteBuffer;
import org.telegram.messenger.exoplayer.MediaFormat;

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
    paramInt1 = SAMPLE_RATE_BY_FSCOD[paramInt1];
    if (paramInt1 == 44100) {
      return (SYNCFRAME_SIZE_WORDS_BY_HALF_FRMSIZECOD_44_1[(paramInt2 / 2)] + paramInt2 % 2) * 2;
    }
    paramInt2 = BITRATE_BY_HALF_FRMSIZECOD[(paramInt2 / 2)];
    if (paramInt1 == 32000) {
      return paramInt2 * 6;
    }
    return paramInt2 * 4;
  }
  
  public static MediaFormat parseAc3AnnexFFormat(ParsableByteArray paramParsableByteArray, String paramString1, long paramLong, String paramString2)
  {
    int i = paramParsableByteArray.readUnsignedByte();
    int k = SAMPLE_RATE_BY_FSCOD[((i & 0xC0) >> 6)];
    int m = paramParsableByteArray.readUnsignedByte();
    int j = CHANNEL_COUNT_BY_ACMOD[((m & 0x38) >> 3)];
    i = j;
    if ((m & 0x4) != 0) {
      i = j + 1;
    }
    return MediaFormat.createAudioFormat(paramString1, "audio/ac3", -1, -1, paramLong, i, k, null, paramString2);
  }
  
  public static MediaFormat parseAc3SyncframeFormat(ParsableBitArray paramParsableBitArray, String paramString1, long paramLong, String paramString2)
  {
    paramParsableBitArray.skipBits(32);
    int j = paramParsableBitArray.readBits(2);
    paramParsableBitArray.skipBits(14);
    int i = paramParsableBitArray.readBits(3);
    if (((i & 0x1) != 0) && (i != 1)) {
      paramParsableBitArray.skipBits(2);
    }
    if ((i & 0x4) != 0) {
      paramParsableBitArray.skipBits(2);
    }
    if (i == 2) {
      paramParsableBitArray.skipBits(2);
    }
    boolean bool = paramParsableBitArray.readBit();
    int k = CHANNEL_COUNT_BY_ACMOD[i];
    if (bool) {}
    for (i = 1;; i = 0) {
      return MediaFormat.createAudioFormat(paramString1, "audio/ac3", -1, -1, paramLong, k + i, SAMPLE_RATE_BY_FSCOD[j], null, paramString2);
    }
  }
  
  public static int parseAc3SyncframeSize(byte[] paramArrayOfByte)
  {
    return getAc3SyncframeSize((paramArrayOfByte[4] & 0xC0) >> 6, paramArrayOfByte[4] & 0x3F);
  }
  
  public static MediaFormat parseEAc3AnnexFFormat(ParsableByteArray paramParsableByteArray, String paramString1, long paramLong, String paramString2)
  {
    paramParsableByteArray.skipBytes(2);
    int i = paramParsableByteArray.readUnsignedByte();
    int k = SAMPLE_RATE_BY_FSCOD[((i & 0xC0) >> 6)];
    int m = paramParsableByteArray.readUnsignedByte();
    int j = CHANNEL_COUNT_BY_ACMOD[((m & 0xE) >> 1)];
    i = j;
    if ((m & 0x1) != 0) {
      i = j + 1;
    }
    return MediaFormat.createAudioFormat(paramString1, "audio/eac3", -1, -1, paramLong, i, k, null, paramString2);
  }
  
  public static int parseEAc3SyncframeAudioSampleCount(ByteBuffer paramByteBuffer)
  {
    if ((paramByteBuffer.get(paramByteBuffer.position() + 4) & 0xC0) >> 6 == 3) {}
    for (int i = 6;; i = BLOCKS_PER_SYNCFRAME_BY_NUMBLKSCOD[((paramByteBuffer.get(paramByteBuffer.position() + 4) & 0x30) >> 4)]) {
      return i * 256;
    }
  }
  
  public static int parseEAc3SyncframeAudioSampleCount(byte[] paramArrayOfByte)
  {
    if ((paramArrayOfByte[4] & 0xC0) >> 6 == 3) {}
    for (int i = 6;; i = BLOCKS_PER_SYNCFRAME_BY_NUMBLKSCOD[((paramArrayOfByte[4] & 0x30) >> 4)]) {
      return i * 256;
    }
  }
  
  public static int parseEAc3SyncframeSize(byte[] paramArrayOfByte)
  {
    return (((paramArrayOfByte[2] & 0x7) << 8) + (paramArrayOfByte[3] & 0xFF) + 1) * 2;
  }
  
  public static MediaFormat parseEac3SyncframeFormat(ParsableBitArray paramParsableBitArray, String paramString1, long paramLong, String paramString2)
  {
    paramParsableBitArray.skipBits(32);
    int i = paramParsableBitArray.readBits(2);
    int k;
    if (i == 3)
    {
      i = SAMPLE_RATE_BY_FSCOD2[paramParsableBitArray.readBits(2)];
      j = paramParsableBitArray.readBits(3);
      boolean bool = paramParsableBitArray.readBit();
      k = CHANNEL_COUNT_BY_ACMOD[j];
      if (!bool) {
        break label95;
      }
    }
    label95:
    for (int j = 1;; j = 0)
    {
      return MediaFormat.createAudioFormat(paramString1, "audio/eac3", -1, -1, paramLong, k + j, i, null, paramString2);
      paramParsableBitArray.skipBits(2);
      i = SAMPLE_RATE_BY_FSCOD[i];
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/util/Ac3Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */