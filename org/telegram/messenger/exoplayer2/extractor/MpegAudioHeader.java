package org.telegram.messenger.exoplayer2.extractor;

public final class MpegAudioHeader
{
  private static final int[] BITRATE_V1_L1;
  private static final int[] BITRATE_V1_L2 = { 32, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320, 384 };
  private static final int[] BITRATE_V1_L3 = { 32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320 };
  private static final int[] BITRATE_V2 = { 8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112, 128, 144, 160 };
  private static final int[] BITRATE_V2_L1;
  public static final int MAX_FRAME_SIZE_BYTES = 4096;
  private static final String[] MIME_TYPE_BY_LAYER = { "audio/mpeg-L1", "audio/mpeg-L2", "audio/mpeg" };
  private static final int[] SAMPLING_RATE_V1 = { 44100, 48000, 32000 };
  public int bitrate;
  public int channels;
  public int frameSize;
  public String mimeType;
  public int sampleRate;
  public int samplesPerFrame;
  public int version;
  
  static
  {
    BITRATE_V1_L1 = new int[] { 32, 64, 96, 128, 160, 192, 224, 256, 288, 320, 352, 384, 416, 448 };
    BITRATE_V2_L1 = new int[] { 32, 48, 56, 64, 80, 96, 112, 128, 144, 160, 176, 192, 224, 256 };
  }
  
  public static int getFrameSize(int paramInt)
  {
    int i = -1;
    int j;
    if ((paramInt & 0xFFE00000) != -2097152) {
      j = i;
    }
    int k;
    int m;
    int n;
    int i1;
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              return j;
              k = paramInt >>> 19 & 0x3;
              j = i;
            } while (k == 1);
            m = paramInt >>> 17 & 0x3;
            j = i;
          } while (m == 0);
          n = paramInt >>> 12 & 0xF;
          j = i;
        } while (n == 0);
        j = i;
      } while (n == 15);
      i1 = paramInt >>> 10 & 0x3;
      j = i;
    } while (i1 == 3);
    i = SAMPLING_RATE_V1[i1];
    if (k == 2)
    {
      j = i / 2;
      label101:
      i1 = paramInt >>> 9 & 0x1;
      if (m != 3) {
        break label170;
      }
      if (k != 3) {
        break label158;
      }
    }
    label158:
    for (paramInt = BITRATE_V1_L1[(n - 1)];; paramInt = BITRATE_V2_L1[(n - 1)])
    {
      j = (paramInt * 12000 / j + i1) * 4;
      break;
      j = i;
      if (k != 0) {
        break label101;
      }
      j = i / 4;
      break label101;
    }
    label170:
    if (k == 3) {
      if (m == 2) {
        paramInt = BITRATE_V1_L2[(n - 1)];
      }
    }
    for (;;)
    {
      if (k != 3) {
        break label232;
      }
      j = 144000 * paramInt / j + i1;
      break;
      paramInt = BITRATE_V1_L3[(n - 1)];
      continue;
      paramInt = BITRATE_V2[(n - 1)];
    }
    label232:
    if (m == 1) {}
    for (i = 72000;; i = 144000)
    {
      j = i * paramInt / j + i1;
      break;
    }
  }
  
  public static boolean populateHeader(int paramInt, MpegAudioHeader paramMpegAudioHeader)
  {
    boolean bool;
    if ((0xFFE00000 & paramInt) != -2097152) {
      bool = false;
    }
    int i;
    int j;
    int k;
    int m;
    for (;;)
    {
      return bool;
      i = paramInt >>> 19 & 0x3;
      if (i == 1)
      {
        bool = false;
      }
      else
      {
        j = paramInt >>> 17 & 0x3;
        if (j == 0)
        {
          bool = false;
        }
        else
        {
          k = paramInt >>> 12 & 0xF;
          if ((k == 0) || (k == 15))
          {
            bool = false;
          }
          else
          {
            m = paramInt >>> 10 & 0x3;
            if (m != 3) {
              break;
            }
            bool = false;
          }
        }
      }
    }
    int n = SAMPLING_RATE_V1[m];
    label112:
    int i1;
    int i2;
    label141:
    String str;
    if (i == 2)
    {
      m = n / 2;
      i1 = paramInt >>> 9 & 0x1;
      if (j != 3) {
        break label239;
      }
      if (i != 3) {
        break label226;
      }
      i2 = BITRATE_V1_L1[(k - 1)];
      n = (i2 * 12000 / m + i1) * 4;
      k = 384;
      str = MIME_TYPE_BY_LAYER[(3 - j)];
      if ((paramInt >> 6 & 0x3) != 3) {
        break label356;
      }
    }
    label226:
    label239:
    label315:
    label349:
    label356:
    for (paramInt = 1;; paramInt = 2)
    {
      paramMpegAudioHeader.setValues(i, str, n, m, paramInt, i2 * 1000, k);
      bool = true;
      break;
      m = n;
      if (i != 0) {
        break label112;
      }
      m = n / 4;
      break label112;
      i2 = BITRATE_V2_L1[(k - 1)];
      break label141;
      if (i == 3)
      {
        if (j == 2) {}
        for (i2 = BITRATE_V1_L2[(k - 1)];; i2 = BITRATE_V1_L3[(k - 1)])
        {
          k = 1152;
          n = 144000 * i2 / m + i1;
          break;
        }
      }
      i2 = BITRATE_V2[(k - 1)];
      if (j == 1)
      {
        k = 576;
        if (j != 1) {
          break label349;
        }
      }
      for (n = 72000;; n = 144000)
      {
        n = n * i2 / m + i1;
        break;
        k = 1152;
        break label315;
      }
    }
  }
  
  private void setValues(int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    this.version = paramInt1;
    this.mimeType = paramString;
    this.frameSize = paramInt2;
    this.sampleRate = paramInt3;
    this.channels = paramInt4;
    this.bitrate = paramInt5;
    this.samplesPerFrame = paramInt6;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/MpegAudioHeader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */