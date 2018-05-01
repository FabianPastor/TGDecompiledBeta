package org.telegram.messenger.exoplayer;

import org.telegram.messenger.exoplayer.util.Util;

public final class C
{
  public static final int CHANNEL_OUT_7POINT1_SURROUND;
  public static final int CRYPTO_MODE_AES_CTR = 1;
  public static final int ENCODING_AC3 = 5;
  public static final int ENCODING_DTS = 7;
  public static final int ENCODING_DTS_HD = 8;
  public static final int ENCODING_E_AC3 = 6;
  public static final int ENCODING_INVALID = 0;
  public static final int ENCODING_PCM_16BIT = 2;
  public static final int ENCODING_PCM_24BIT = Integer.MIN_VALUE;
  public static final int ENCODING_PCM_32BIT = NUM;
  public static final int ENCODING_PCM_8BIT = 3;
  public static final int LENGTH_UNBOUNDED = -1;
  public static final long MATCH_LONGEST_US = -2L;
  public static final long MICROS_PER_SECOND = 1000000L;
  public static final int RESULT_END_OF_INPUT = -1;
  public static final int RESULT_MAX_LENGTH_EXCEEDED = -2;
  public static final int SAMPLE_FLAG_DECODE_ONLY = 134217728;
  public static final int SAMPLE_FLAG_ENCRYPTED = 2;
  public static final int SAMPLE_FLAG_SYNC = 1;
  public static final long UNKNOWN_TIME_US = -1L;
  public static final String UTF8_NAME = "UTF-8";
  
  static
  {
    if (Util.SDK_INT < 23) {}
    for (int i = 1020;; i = 6396)
    {
      CHANNEL_OUT_7POINT1_SURROUND = i;
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/C.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */