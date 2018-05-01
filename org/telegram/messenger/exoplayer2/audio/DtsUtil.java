package org.telegram.messenger.exoplayer2.audio;

import java.nio.ByteBuffer;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.util.ParsableBitArray;

public final class DtsUtil
{
  private static final int[] CHANNELS_BY_AMODE = { 1, 2, 2, 2, 2, 3, 3, 4, 4, 5, 6, 6, 6, 7, 8, 8 };
  private static final int[] SAMPLE_RATE_BY_SFREQ = { -1, 8000, 16000, 32000, -1, -1, 11025, 22050, 44100, -1, -1, 12000, 24000, 48000, -1, -1 };
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
  
  public static Format parseDtsFormat(byte[] paramArrayOfByte, String paramString1, String paramString2, DrmInitData paramDrmInitData)
  {
    paramArrayOfByte = new ParsableBitArray(paramArrayOfByte);
    paramArrayOfByte.skipBits(60);
    int i = paramArrayOfByte.readBits(6);
    int j = CHANNELS_BY_AMODE[i];
    i = paramArrayOfByte.readBits(4);
    int k = SAMPLE_RATE_BY_SFREQ[i];
    i = paramArrayOfByte.readBits(5);
    if (i >= TWICE_BITRATE_KBPS_BY_RATE.length)
    {
      i = -1;
      paramArrayOfByte.skipBits(10);
      if (paramArrayOfByte.readBits(2) <= 0) {
        break label121;
      }
    }
    label121:
    for (int m = 1;; m = 0)
    {
      return Format.createAudioSampleFormat(paramString1, "audio/vnd.dts", null, i, -1, j + m, k, null, paramDrmInitData, 0, paramString2);
      i = TWICE_BITRATE_KBPS_BY_RATE[i] * 1000 / 2;
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/audio/DtsUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */