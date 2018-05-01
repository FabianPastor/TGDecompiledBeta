package org.telegram.messenger.exoplayer2.util;

import android.util.Pair;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer2.ParserException;

public final class CodecSpecificDataUtil
{
  private static final int AUDIO_OBJECT_TYPE_AAC_LC = 2;
  private static final int AUDIO_OBJECT_TYPE_ER_BSAC = 22;
  private static final int AUDIO_OBJECT_TYPE_ESCAPE = 31;
  private static final int AUDIO_OBJECT_TYPE_PS = 29;
  private static final int AUDIO_OBJECT_TYPE_SBR = 5;
  private static final int AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID = -1;
  private static final int[] AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE = { 0, 1, 2, 3, 4, 5, 6, 8, -1, -1, -1, 7, 8, -1, 8, -1 };
  private static final int AUDIO_SPECIFIC_CONFIG_FREQUENCY_INDEX_ARBITRARY = 15;
  private static final int[] AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE;
  private static final byte[] NAL_START_CODE = { 0, 0, 0, 1 };
  
  static
  {
    AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE = new int[] { 96000, 88200, 64000, 48000, 44100, 32000, 24000, 22050, 16000, 12000, 11025, 8000, 7350 };
  }
  
  public static byte[] buildAacAudioSpecificConfig(int paramInt1, int paramInt2, int paramInt3)
  {
    return new byte[] { (byte)(paramInt1 << 3 & 0xF8 | paramInt2 >> 1 & 0x7), (byte)(paramInt2 << 7 & 0x80 | paramInt3 << 3 & 0x78) };
  }
  
  public static byte[] buildAacLcAudioSpecificConfig(int paramInt1, int paramInt2)
  {
    int i = -1;
    for (int j = 0; j < AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE.length; j++) {
      if (paramInt1 == AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE[j]) {
        i = j;
      }
    }
    int k = -1;
    for (j = 0; j < AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE.length; j++) {
      if (paramInt2 == AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE[j]) {
        k = j;
      }
    }
    if ((paramInt1 == -1) || (k == -1)) {
      throw new IllegalArgumentException("Invalid sample rate or number of channels: " + paramInt1 + ", " + paramInt2);
    }
    return buildAacAudioSpecificConfig(2, i, k);
  }
  
  public static byte[] buildNalUnit(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    byte[] arrayOfByte = new byte[NAL_START_CODE.length + paramInt2];
    System.arraycopy(NAL_START_CODE, 0, arrayOfByte, 0, NAL_START_CODE.length);
    System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, NAL_START_CODE.length, paramInt2);
    return arrayOfByte;
  }
  
  private static int findNalStartCode(byte[] paramArrayOfByte, int paramInt)
  {
    int i = paramArrayOfByte.length;
    int j = NAL_START_CODE.length;
    if (paramInt <= i - j) {
      if (!isNalStartCode(paramArrayOfByte, paramInt)) {}
    }
    for (;;)
    {
      return paramInt;
      paramInt++;
      break;
      paramInt = -1;
    }
  }
  
  private static int getAacAudioObjectType(ParsableBitArray paramParsableBitArray)
  {
    int i = paramParsableBitArray.readBits(5);
    int j = i;
    if (i == 31) {
      j = paramParsableBitArray.readBits(6) + 32;
    }
    return j;
  }
  
  private static int getAacSamplingFrequency(ParsableBitArray paramParsableBitArray)
  {
    int i = paramParsableBitArray.readBits(4);
    if (i == 15)
    {
      i = paramParsableBitArray.readBits(24);
      return i;
    }
    if (i < 13) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      i = AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE[i];
      break;
    }
  }
  
  private static boolean isNalStartCode(byte[] paramArrayOfByte, int paramInt)
  {
    boolean bool1 = false;
    boolean bool2;
    if (paramArrayOfByte.length - paramInt <= NAL_START_CODE.length) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      for (int i = 0;; i++)
      {
        if (i >= NAL_START_CODE.length) {
          break label52;
        }
        bool2 = bool1;
        if (paramArrayOfByte[(paramInt + i)] != NAL_START_CODE[i]) {
          break;
        }
      }
      label52:
      bool2 = true;
    }
  }
  
  public static Pair<Integer, Integer> parseAacAudioSpecificConfig(ParsableBitArray paramParsableBitArray, boolean paramBoolean)
    throws ParserException
  {
    int i = getAacAudioObjectType(paramParsableBitArray);
    int j = getAacSamplingFrequency(paramParsableBitArray);
    int k = paramParsableBitArray.readBits(4);
    int m;
    int n;
    if (i != 5)
    {
      m = i;
      n = k;
      if (i != 29) {}
    }
    else
    {
      i = getAacSamplingFrequency(paramParsableBitArray);
      int i1 = getAacAudioObjectType(paramParsableBitArray);
      m = i1;
      n = k;
      j = i;
      if (i1 == 22)
      {
        n = paramParsableBitArray.readBits(4);
        j = i;
        m = i1;
      }
    }
    if (paramBoolean)
    {
      switch (m)
      {
      case 5: 
      case 8: 
      case 9: 
      case 10: 
      case 11: 
      case 12: 
      case 13: 
      case 14: 
      case 15: 
      case 16: 
      case 18: 
      default: 
        throw new ParserException("Unsupported audio object type: " + m);
      }
      parseGaSpecificConfig(paramParsableBitArray, m, n);
    }
    switch (m)
    {
    case 18: 
    default: 
      n = AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE[n];
      if (n == -1) {
        break;
      }
    }
    for (paramBoolean = true;; paramBoolean = false)
    {
      Assertions.checkArgument(paramBoolean);
      return Pair.create(Integer.valueOf(j), Integer.valueOf(n));
      m = paramParsableBitArray.readBits(2);
      if ((m != 2) && (m != 3)) {
        break;
      }
      throw new ParserException("Unsupported epConfig: " + m);
    }
  }
  
  public static Pair<Integer, Integer> parseAacAudioSpecificConfig(byte[] paramArrayOfByte)
    throws ParserException
  {
    return parseAacAudioSpecificConfig(new ParsableBitArray(paramArrayOfByte), false);
  }
  
  private static void parseGaSpecificConfig(ParsableBitArray paramParsableBitArray, int paramInt1, int paramInt2)
  {
    paramParsableBitArray.skipBits(1);
    if (paramParsableBitArray.readBit()) {
      paramParsableBitArray.skipBits(14);
    }
    boolean bool = paramParsableBitArray.readBit();
    if (paramInt2 == 0) {
      throw new UnsupportedOperationException();
    }
    if ((paramInt1 == 6) || (paramInt1 == 20)) {
      paramParsableBitArray.skipBits(3);
    }
    if (bool)
    {
      if (paramInt1 == 22) {
        paramParsableBitArray.skipBits(16);
      }
      if ((paramInt1 == 17) || (paramInt1 == 19) || (paramInt1 == 20) || (paramInt1 == 23)) {
        paramParsableBitArray.skipBits(3);
      }
      paramParsableBitArray.skipBits(1);
    }
  }
  
  public static byte[][] splitNalUnits(byte[] paramArrayOfByte)
  {
    if (!isNalStartCode(paramArrayOfByte, 0)) {}
    byte[][] arrayOfByte;
    for (paramArrayOfByte = (byte[][])null;; paramArrayOfByte = arrayOfByte)
    {
      return paramArrayOfByte;
      ArrayList localArrayList = new ArrayList();
      int i = 0;
      int j;
      do
      {
        localArrayList.add(Integer.valueOf(i));
        j = findNalStartCode(paramArrayOfByte, NAL_START_CODE.length + i);
        i = j;
      } while (j != -1);
      arrayOfByte = new byte[localArrayList.size()][];
      i = 0;
      if (i < localArrayList.size())
      {
        int k = ((Integer)localArrayList.get(i)).intValue();
        if (i < localArrayList.size() - 1) {}
        for (j = ((Integer)localArrayList.get(i + 1)).intValue();; j = paramArrayOfByte.length)
        {
          byte[] arrayOfByte1 = new byte[j - k];
          System.arraycopy(paramArrayOfByte, k, arrayOfByte1, 0, arrayOfByte1.length);
          arrayOfByte[i] = arrayOfByte1;
          i++;
          break;
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/util/CodecSpecificDataUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */