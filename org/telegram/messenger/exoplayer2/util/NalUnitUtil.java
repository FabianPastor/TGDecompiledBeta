package org.telegram.messenger.exoplayer2.util;

import android.util.Log;
import java.nio.ByteBuffer;

public final class NalUnitUtil
{
  public static final float[] ASPECT_RATIO_IDC_VALUES;
  public static final int EXTENDED_SAR = 255;
  private static final int H264_NAL_UNIT_TYPE_SEI = 6;
  private static final int H264_NAL_UNIT_TYPE_SPS = 7;
  private static final int H265_NAL_UNIT_TYPE_PREFIX_SEI = 39;
  public static final byte[] NAL_START_CODE = { 0, 0, 0, 1 };
  private static final String TAG = "NalUnitUtil";
  private static int[] scratchEscapePositions = new int[10];
  private static final Object scratchEscapePositionsLock;
  
  static
  {
    ASPECT_RATIO_IDC_VALUES = new float[] { 1.0F, 1.0F, 1.0909091F, 0.90909094F, 1.4545455F, 1.2121212F, 2.1818182F, 1.8181819F, 2.909091F, 2.4242425F, 1.6363636F, 1.3636364F, 1.939394F, 1.6161616F, 1.3333334F, 1.5F, 2.0F };
    scratchEscapePositionsLock = new Object();
  }
  
  public static void clearPrefixFlags(boolean[] paramArrayOfBoolean)
  {
    paramArrayOfBoolean[0] = false;
    paramArrayOfBoolean[1] = false;
    paramArrayOfBoolean[2] = false;
  }
  
  public static void discardToSps(ByteBuffer paramByteBuffer)
  {
    int i = paramByteBuffer.position();
    int j = 0;
    int k = 0;
    int m;
    int n;
    if (k + 1 < i)
    {
      m = paramByteBuffer.get(k) & 0xFF;
      if (j == 3)
      {
        n = j;
        if (m != 1) {
          break label108;
        }
        n = j;
        if ((paramByteBuffer.get(k + 1) & 0x1F) != 7) {
          break label108;
        }
        ByteBuffer localByteBuffer = paramByteBuffer.duplicate();
        localByteBuffer.position(k - 3);
        localByteBuffer.limit(i);
        paramByteBuffer.position(0);
        paramByteBuffer.put(localByteBuffer);
      }
    }
    for (;;)
    {
      return;
      n = j;
      if (m == 0) {
        n = j + 1;
      }
      label108:
      j = n;
      if (m != 0) {
        j = 0;
      }
      k++;
      break;
      paramByteBuffer.clear();
    }
  }
  
  public static int findNalUnit(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean[] paramArrayOfBoolean)
  {
    boolean bool1 = true;
    int i = paramInt2 - paramInt1;
    if (i >= 0)
    {
      bool2 = true;
      Assertions.checkState(bool2);
      if (i != 0) {
        break label36;
      }
      paramInt1 = paramInt2;
    }
    label36:
    label122:
    label125:
    do
    {
      for (;;)
      {
        return paramInt1;
        bool2 = false;
        break;
        if (paramArrayOfBoolean == null) {
          break label122;
        }
        if (paramArrayOfBoolean[0] != 0)
        {
          clearPrefixFlags(paramArrayOfBoolean);
          paramInt1 -= 3;
        }
        else if ((i > 1) && (paramArrayOfBoolean[1] != 0) && (paramArrayOfByte[paramInt1] == 1))
        {
          clearPrefixFlags(paramArrayOfBoolean);
          paramInt1 -= 2;
        }
        else
        {
          if ((i <= 2) || (paramArrayOfBoolean[2] == 0) || (paramArrayOfByte[paramInt1] != 0) || (paramArrayOfByte[(paramInt1 + 1)] != 1)) {
            break label122;
          }
          clearPrefixFlags(paramArrayOfBoolean);
          paramInt1--;
        }
      }
      paramInt1 += 2;
      if (paramInt1 < paramInt2 - 1)
      {
        if ((paramArrayOfByte[paramInt1] & 0xFE) != 0) {}
        for (;;)
        {
          paramInt1 += 3;
          break label125;
          if ((paramArrayOfByte[(paramInt1 - 2)] == 0) && (paramArrayOfByte[(paramInt1 - 1)] == 0) && (paramArrayOfByte[paramInt1] == 1))
          {
            if (paramArrayOfBoolean != null) {
              clearPrefixFlags(paramArrayOfBoolean);
            }
            paramInt1 -= 2;
            break;
          }
          paramInt1 -= 2;
        }
      }
      paramInt1 = paramInt2;
    } while (paramArrayOfBoolean == null);
    if (i > 2) {
      if ((paramArrayOfByte[(paramInt2 - 3)] == 0) && (paramArrayOfByte[(paramInt2 - 2)] == 0) && (paramArrayOfByte[(paramInt2 - 1)] == 1))
      {
        bool2 = true;
        label231:
        paramArrayOfBoolean[0] = bool2;
        if (i <= 1) {
          break label368;
        }
        if ((paramArrayOfByte[(paramInt2 - 2)] != 0) || (paramArrayOfByte[(paramInt2 - 1)] != 0)) {
          break label362;
        }
        bool2 = true;
        label261:
        paramArrayOfBoolean[1] = bool2;
        if (paramArrayOfByte[(paramInt2 - 1)] != 0) {
          break label394;
        }
      }
    }
    label362:
    label368:
    label394:
    for (boolean bool2 = bool1;; bool2 = false)
    {
      paramArrayOfBoolean[2] = bool2;
      paramInt1 = paramInt2;
      break;
      bool2 = false;
      break label231;
      if (i == 2)
      {
        if ((paramArrayOfBoolean[2] != 0) && (paramArrayOfByte[(paramInt2 - 2)] == 0) && (paramArrayOfByte[(paramInt2 - 1)] == 1))
        {
          bool2 = true;
          break label231;
        }
        bool2 = false;
        break label231;
      }
      if ((paramArrayOfBoolean[1] != 0) && (paramArrayOfByte[(paramInt2 - 1)] == 1))
      {
        bool2 = true;
        break label231;
      }
      bool2 = false;
      break label231;
      bool2 = false;
      break label261;
      if ((paramArrayOfBoolean[2] != 0) && (paramArrayOfByte[(paramInt2 - 1)] == 0))
      {
        bool2 = true;
        break label261;
      }
      bool2 = false;
      break label261;
    }
  }
  
  private static int findNextUnescapeIndex(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramInt1 < paramInt2 - 2) {
      if ((paramArrayOfByte[paramInt1] != 0) || (paramArrayOfByte[(paramInt1 + 1)] != 0) || (paramArrayOfByte[(paramInt1 + 2)] != 3)) {}
    }
    for (;;)
    {
      return paramInt1;
      paramInt1++;
      break;
      paramInt1 = paramInt2;
    }
  }
  
  public static int getH265NalUnitType(byte[] paramArrayOfByte, int paramInt)
  {
    return (paramArrayOfByte[(paramInt + 3)] & 0x7E) >> 1;
  }
  
  public static int getNalUnitType(byte[] paramArrayOfByte, int paramInt)
  {
    return paramArrayOfByte[(paramInt + 3)] & 0x1F;
  }
  
  public static boolean isNalUnitSei(String paramString, byte paramByte)
  {
    if ((("video/avc".equals(paramString)) && ((paramByte & 0x1F) == 6)) || (("video/hevc".equals(paramString)) && ((paramByte & 0x7E) >> 1 == 39))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static PpsData parsePpsNalUnit(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    paramArrayOfByte = new ParsableNalUnitBitArray(paramArrayOfByte, paramInt1, paramInt2);
    paramArrayOfByte.skipBits(8);
    paramInt2 = paramArrayOfByte.readUnsignedExpGolombCodedInt();
    paramInt1 = paramArrayOfByte.readUnsignedExpGolombCodedInt();
    paramArrayOfByte.skipBit();
    return new PpsData(paramInt2, paramInt1, paramArrayOfByte.readBit());
  }
  
  public static SpsData parseSpsNalUnit(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    paramArrayOfByte = new ParsableNalUnitBitArray(paramArrayOfByte, paramInt1, paramInt2);
    paramArrayOfByte.skipBits(8);
    paramInt1 = paramArrayOfByte.readBits(8);
    paramArrayOfByte.skipBits(16);
    int i = paramArrayOfByte.readUnsignedExpGolombCodedInt();
    int j = 1;
    boolean bool1 = false;
    boolean bool2 = false;
    if ((paramInt1 == 100) || (paramInt1 == 110) || (paramInt1 == 122) || (paramInt1 == 244) || (paramInt1 == 44) || (paramInt1 == 83) || (paramInt1 == 86) || (paramInt1 == 118) || (paramInt1 == 128) || (paramInt1 == 138))
    {
      k = paramArrayOfByte.readUnsignedExpGolombCodedInt();
      if (k == 3) {
        bool2 = paramArrayOfByte.readBit();
      }
      paramArrayOfByte.readUnsignedExpGolombCodedInt();
      paramArrayOfByte.readUnsignedExpGolombCodedInt();
      paramArrayOfByte.skipBit();
      bool1 = bool2;
      j = k;
      if (paramArrayOfByte.readBit())
      {
        if (k != 3)
        {
          paramInt1 = 8;
          paramInt2 = 0;
          label165:
          bool1 = bool2;
          j = k;
          if (paramInt2 >= paramInt1) {
            break label220;
          }
          if (paramArrayOfByte.readBit()) {
            if (paramInt2 >= 6) {
              break label213;
            }
          }
        }
        label213:
        for (j = 16;; j = 64)
        {
          skipScalingList(paramArrayOfByte, j);
          paramInt2++;
          break label165;
          paramInt1 = 12;
          break;
        }
      }
    }
    label220:
    int m = paramArrayOfByte.readUnsignedExpGolombCodedInt();
    int n = paramArrayOfByte.readUnsignedExpGolombCodedInt();
    int k = 0;
    bool2 = false;
    boolean bool3;
    label284:
    label372:
    float f1;
    float f2;
    if (n == 0)
    {
      paramInt2 = paramArrayOfByte.readUnsignedExpGolombCodedInt() + 4;
      paramArrayOfByte.readUnsignedExpGolombCodedInt();
      paramArrayOfByte.skipBit();
      int i1 = paramArrayOfByte.readUnsignedExpGolombCodedInt();
      k = paramArrayOfByte.readUnsignedExpGolombCodedInt();
      bool3 = paramArrayOfByte.readBit();
      if (!bool3) {
        break label571;
      }
      paramInt1 = 1;
      if (!bool3) {
        paramArrayOfByte.skipBit();
      }
      paramArrayOfByte.skipBit();
      i1 = (i1 + 1) * 16;
      int i2 = (2 - paramInt1) * (k + 1) * 16;
      k = i1;
      paramInt1 = i2;
      if (paramArrayOfByte.readBit())
      {
        int i3 = paramArrayOfByte.readUnsignedExpGolombCodedInt();
        int i4 = paramArrayOfByte.readUnsignedExpGolombCodedInt();
        int i5 = paramArrayOfByte.readUnsignedExpGolombCodedInt();
        int i6 = paramArrayOfByte.readUnsignedExpGolombCodedInt();
        if (j != 0) {
          break label581;
        }
        j = 1;
        if (!bool3) {
          break label576;
        }
        paramInt1 = 1;
        paramInt1 = 2 - paramInt1;
        k = i1 - (i3 + i4) * j;
        paramInt1 = i2 - (i5 + i6) * paramInt1;
      }
      f1 = 1.0F;
      f2 = f1;
      if (paramArrayOfByte.readBit())
      {
        f2 = f1;
        if (paramArrayOfByte.readBit())
        {
          j = paramArrayOfByte.readBits(8);
          if (j != 255) {
            break label641;
          }
          j = paramArrayOfByte.readBits(16);
          i1 = paramArrayOfByte.readBits(16);
          f2 = f1;
          if (j != 0)
          {
            f2 = f1;
            if (i1 != 0) {
              f2 = j / i1;
            }
          }
        }
      }
    }
    for (;;)
    {
      return new SpsData(i, k, paramInt1, f2, bool1, bool3, m + 4, n, paramInt2, bool2);
      paramInt2 = k;
      if (n != 1) {
        break;
      }
      bool3 = paramArrayOfByte.readBit();
      paramArrayOfByte.readSignedExpGolombCodedInt();
      paramArrayOfByte.readSignedExpGolombCodedInt();
      long l = paramArrayOfByte.readUnsignedExpGolombCodedInt();
      for (paramInt1 = 0;; paramInt1++)
      {
        paramInt2 = k;
        bool2 = bool3;
        if (paramInt1 >= l) {
          break;
        }
        paramArrayOfByte.readUnsignedExpGolombCodedInt();
      }
      label571:
      paramInt1 = 0;
      break label284;
      label576:
      paramInt1 = 0;
      break label372;
      label581:
      if (j == 3)
      {
        paramInt1 = 1;
        label589:
        if (j != 1) {
          break label629;
        }
        j = 2;
        label598:
        if (!bool3) {
          break label635;
        }
      }
      label629:
      label635:
      for (k = 1;; k = 0)
      {
        k = j * (2 - k);
        j = paramInt1;
        paramInt1 = k;
        break;
        paramInt1 = 2;
        break label589;
        j = 1;
        break label598;
      }
      label641:
      if (j < ASPECT_RATIO_IDC_VALUES.length)
      {
        f2 = ASPECT_RATIO_IDC_VALUES[j];
      }
      else
      {
        Log.w("NalUnitUtil", "Unexpected aspect_ratio_idc value: " + j);
        f2 = f1;
      }
    }
  }
  
  private static void skipScalingList(ParsableNalUnitBitArray paramParsableNalUnitBitArray, int paramInt)
  {
    int i = 8;
    int j = 8;
    int k = 0;
    if (k < paramInt)
    {
      int m = j;
      if (j != 0) {
        m = (i + paramParsableNalUnitBitArray.readSignedExpGolombCodedInt() + 256) % 256;
      }
      if (m == 0) {}
      for (;;)
      {
        k++;
        j = m;
        break;
        i = m;
      }
    }
  }
  
  /* Error */
  public static int unescapeStream(byte[] paramArrayOfByte, int paramInt)
  {
    // Byte code:
    //   0: getstatic 58	org/telegram/messenger/exoplayer2/util/NalUnitUtil:scratchEscapePositionsLock	Ljava/lang/Object;
    //   3: astore_2
    //   4: aload_2
    //   5: monitorenter
    //   6: iconst_0
    //   7: istore_3
    //   8: iconst_0
    //   9: istore 4
    //   11: iload_3
    //   12: iload_1
    //   13: if_icmpge +67 -> 80
    //   16: aload_0
    //   17: iload_3
    //   18: iload_1
    //   19: invokestatic 185	org/telegram/messenger/exoplayer2/util/NalUnitUtil:findNextUnescapeIndex	([BII)I
    //   22: istore 5
    //   24: iload 5
    //   26: istore_3
    //   27: iload 5
    //   29: iload_1
    //   30: if_icmpge -19 -> 11
    //   33: getstatic 60	org/telegram/messenger/exoplayer2/util/NalUnitUtil:scratchEscapePositions	[I
    //   36: arraylength
    //   37: iload 4
    //   39: if_icmpgt +18 -> 57
    //   42: getstatic 60	org/telegram/messenger/exoplayer2/util/NalUnitUtil:scratchEscapePositions	[I
    //   45: getstatic 60	org/telegram/messenger/exoplayer2/util/NalUnitUtil:scratchEscapePositions	[I
    //   48: arraylength
    //   49: iconst_2
    //   50: imul
    //   51: invokestatic 191	java/util/Arrays:copyOf	([II)[I
    //   54: putstatic 60	org/telegram/messenger/exoplayer2/util/NalUnitUtil:scratchEscapePositions	[I
    //   57: getstatic 60	org/telegram/messenger/exoplayer2/util/NalUnitUtil:scratchEscapePositions	[I
    //   60: astore 6
    //   62: aload 6
    //   64: iload 4
    //   66: iload 5
    //   68: iastore
    //   69: iload 5
    //   71: iconst_3
    //   72: iadd
    //   73: istore_3
    //   74: iinc 4 1
    //   77: goto -66 -> 11
    //   80: iload_1
    //   81: iload 4
    //   83: isub
    //   84: istore 7
    //   86: iconst_0
    //   87: istore_3
    //   88: iconst_0
    //   89: istore 5
    //   91: iconst_0
    //   92: istore_1
    //   93: iload_1
    //   94: iload 4
    //   96: if_icmpge +66 -> 162
    //   99: getstatic 60	org/telegram/messenger/exoplayer2/util/NalUnitUtil:scratchEscapePositions	[I
    //   102: iload_1
    //   103: iaload
    //   104: iload_3
    //   105: isub
    //   106: istore 8
    //   108: aload_0
    //   109: iload_3
    //   110: aload_0
    //   111: iload 5
    //   113: iload 8
    //   115: invokestatic 197	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
    //   118: iload 5
    //   120: iload 8
    //   122: iadd
    //   123: istore 5
    //   125: iload 5
    //   127: iconst_1
    //   128: iadd
    //   129: istore 9
    //   131: aload_0
    //   132: iload 5
    //   134: iconst_0
    //   135: i2b
    //   136: bastore
    //   137: iload 9
    //   139: iconst_1
    //   140: iadd
    //   141: istore 5
    //   143: aload_0
    //   144: iload 9
    //   146: iconst_0
    //   147: i2b
    //   148: bastore
    //   149: iload_3
    //   150: iload 8
    //   152: iconst_3
    //   153: iadd
    //   154: iadd
    //   155: istore_3
    //   156: iinc 1 1
    //   159: goto -66 -> 93
    //   162: aload_0
    //   163: iload_3
    //   164: aload_0
    //   165: iload 5
    //   167: iload 7
    //   169: iload 5
    //   171: isub
    //   172: invokestatic 197	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
    //   175: aload_2
    //   176: monitorexit
    //   177: iload 7
    //   179: ireturn
    //   180: astore_0
    //   181: aload_2
    //   182: monitorexit
    //   183: aload_0
    //   184: athrow
    //   185: astore_0
    //   186: goto -5 -> 181
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	189	0	paramArrayOfByte	byte[]
    //   0	189	1	paramInt	int
    //   3	179	2	localObject	Object
    //   7	157	3	i	int
    //   9	88	4	j	int
    //   22	150	5	k	int
    //   60	3	6	arrayOfInt	int[]
    //   84	94	7	m	int
    //   106	48	8	n	int
    //   129	16	9	i1	int
    // Exception table:
    //   from	to	target	type
    //   16	24	180	finally
    //   33	57	180	finally
    //   57	62	180	finally
    //   99	118	180	finally
    //   162	177	180	finally
    //   181	183	185	finally
  }
  
  public static final class PpsData
  {
    public final boolean bottomFieldPicOrderInFramePresentFlag;
    public final int picParameterSetId;
    public final int seqParameterSetId;
    
    public PpsData(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      this.picParameterSetId = paramInt1;
      this.seqParameterSetId = paramInt2;
      this.bottomFieldPicOrderInFramePresentFlag = paramBoolean;
    }
  }
  
  public static final class SpsData
  {
    public final boolean deltaPicOrderAlwaysZeroFlag;
    public final boolean frameMbsOnlyFlag;
    public final int frameNumLength;
    public final int height;
    public final int picOrderCntLsbLength;
    public final int picOrderCountType;
    public final float pixelWidthAspectRatio;
    public final boolean separateColorPlaneFlag;
    public final int seqParameterSetId;
    public final int width;
    
    public SpsData(int paramInt1, int paramInt2, int paramInt3, float paramFloat, boolean paramBoolean1, boolean paramBoolean2, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean3)
    {
      this.seqParameterSetId = paramInt1;
      this.width = paramInt2;
      this.height = paramInt3;
      this.pixelWidthAspectRatio = paramFloat;
      this.separateColorPlaneFlag = paramBoolean1;
      this.frameMbsOnlyFlag = paramBoolean2;
      this.frameNumLength = paramInt4;
      this.picOrderCountType = paramInt5;
      this.picOrderCntLsbLength = paramInt6;
      this.deltaPicOrderAlwaysZeroFlag = paramBoolean3;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/util/NalUnitUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */