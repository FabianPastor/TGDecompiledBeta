package org.telegram.messenger.exoplayer.util;

import java.nio.ByteBuffer;
import org.telegram.messenger.exoplayer.MediaFormat;

public final class DtsUtil
{
  private static final int[] CHANNELS_BY_AMODE = { 1, 2, 2, 2, 2, 3, 3, 4, 4, 5, 6, 6, 6, 7, 8, 8 };
  private static final int[] SAMPLE_RATE_BY_SFREQ = { -1, 8000, 16000, 32000, -1, -1, 11025, 22050, 44100, -1, -1, 12000, 24000, 48000, -1, -1 };
  private static final ParsableBitArray SCRATCH_BITS = new ParsableBitArray();
  private static final int[] TWICE_BITRATE_KBPS_BY_RATE = { 64, 112, 128, 192, 224, 256, 384, 448, 512, 640, 768, 896, 1024, 1152, 1280, 1536, 1920, 2048, 2304, 2560, 2688, 2816, 2823, 2944, 3072, 3840, 4096, 6144, 7680 };
  
  public static int getDtsFrameSize(byte[] paramArrayOfByte)
  {
    return ((paramArrayOfByte[5] & 0x2) << 12 | (paramArrayOfByte[6] & 0xFF) << 4 | (paramArrayOfByte[7] & 0xF0) >> 4) + 1;
  }
  
  public static int parseDtsAudioSampleCount(ByteBuffer paramByteBuffer)
  {
    int i = paramByteBuffer.position();
    return (((paramByteBuffer.get(i + 4) & 0x1) << 6 | (paramByteBuffer.get(i + 5) & 0xFC) >> 2) + 1) * 32;
  }
  
  public static int parseDtsAudioSampleCount(byte[] paramArrayOfByte)
  {
    return (((paramArrayOfByte[4] & 0x1) << 6 | (paramArrayOfByte[5] & 0xFC) >> 2) + 1) * 32;
  }
  
  public static MediaFormat parseDtsFormat(byte[] paramArrayOfByte, String paramString1, long paramLong, String paramString2)
  {
    ParsableBitArray localParsableBitArray = SCRATCH_BITS;
    localParsableBitArray.reset(paramArrayOfByte);
    localParsableBitArray.skipBits(60);
    int i = localParsableBitArray.readBits(6);
    int k = CHANNELS_BY_AMODE[i];
    i = localParsableBitArray.readBits(4);
    int m = SAMPLE_RATE_BY_SFREQ[i];
    i = localParsableBitArray.readBits(5);
    if (i >= TWICE_BITRATE_KBPS_BY_RATE.length)
    {
      i = -1;
      localParsableBitArray.skipBits(10);
      if (localParsableBitArray.readBits(2) <= 0) {
        break label128;
      }
    }
    label128:
    for (int j = 1;; j = 0)
    {
      return MediaFormat.createAudioFormat(paramString1, "audio/vnd.dts", i, -1, paramLong, k + j, m, null, paramString2);
      i = TWICE_BITRATE_KBPS_BY_RATE[i] * 1000 / 2;
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/util/DtsUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */