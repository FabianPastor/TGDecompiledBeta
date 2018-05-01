package org.telegram.messenger.exoplayer2.extractor.ogg;

import android.util.Log;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

final class VorbisUtil
{
  private static final String TAG = "VorbisUtil";
  
  public static int iLog(int paramInt)
  {
    int i = 0;
    while (paramInt > 0)
    {
      i++;
      paramInt >>>= 1;
    }
    return i;
  }
  
  private static long mapType1QuantValues(long paramLong1, long paramLong2)
  {
    return Math.floor(Math.pow(paramLong1, 1.0D / paramLong2));
  }
  
  private static CodeBook readBook(VorbisBitArray paramVorbisBitArray)
    throws ParserException
  {
    if (paramVorbisBitArray.readBits(24) != 5653314) {
      throw new ParserException("expected code book to start with [0x56, 0x43, 0x42] at " + paramVorbisBitArray.getPosition());
    }
    int i = paramVorbisBitArray.readBits(16);
    int j = paramVorbisBitArray.readBits(24);
    long[] arrayOfLong = new long[j];
    boolean bool1 = paramVorbisBitArray.readBit();
    int k;
    if (!bool1)
    {
      boolean bool2 = paramVorbisBitArray.readBit();
      k = 0;
      if (k < arrayOfLong.length)
      {
        if (bool2) {
          if (paramVorbisBitArray.readBit()) {
            arrayOfLong[k] = (paramVorbisBitArray.readBits(5) + 1);
          }
        }
        for (;;)
        {
          k++;
          break;
          arrayOfLong[k] = 0L;
          continue;
          arrayOfLong[k] = (paramVorbisBitArray.readBits(5) + 1);
        }
      }
    }
    else
    {
      k = paramVorbisBitArray.readBits(5) + 1;
      int m = 0;
      while (m < arrayOfLong.length)
      {
        int n = paramVorbisBitArray.readBits(iLog(j - m));
        for (i1 = 0; (i1 < n) && (m < arrayOfLong.length); i1++)
        {
          arrayOfLong[m] = k;
          m++;
        }
        k++;
      }
    }
    int i1 = paramVorbisBitArray.readBits(4);
    if (i1 > 2) {
      throw new ParserException("lookup type greater than 2 not decodable: " + i1);
    }
    long l;
    if ((i1 == 1) || (i1 == 2))
    {
      paramVorbisBitArray.skipBits(32);
      paramVorbisBitArray.skipBits(32);
      k = paramVorbisBitArray.readBits(4);
      paramVorbisBitArray.skipBits(1);
      if (i1 != 1) {
        break label340;
      }
      if (i == 0) {
        break label334;
      }
      l = mapType1QuantValues(j, i);
    }
    for (;;)
    {
      paramVorbisBitArray.skipBits((int)((k + 1) * l));
      return new CodeBook(i, j, arrayOfLong, i1, bool1);
      label334:
      l = 0L;
      continue;
      label340:
      l = j * i;
    }
  }
  
  private static void readFloors(VorbisBitArray paramVorbisBitArray)
    throws ParserException
  {
    int i = paramVorbisBitArray.readBits(6);
    for (int j = 0; j < i + 1; j++)
    {
      int k = paramVorbisBitArray.readBits(16);
      int m;
      switch (k)
      {
      default: 
        throw new ParserException("floor type greater than 1 not decodable: " + k);
      case 0: 
        paramVorbisBitArray.skipBits(8);
        paramVorbisBitArray.skipBits(16);
        paramVorbisBitArray.skipBits(16);
        paramVorbisBitArray.skipBits(6);
        paramVorbisBitArray.skipBits(8);
        m = paramVorbisBitArray.readBits(4);
        k = 0;
      }
      while (k < m + 1)
      {
        paramVorbisBitArray.skipBits(8);
        k++;
        continue;
        int n = paramVorbisBitArray.readBits(5);
        k = -1;
        int[] arrayOfInt1 = new int[n];
        m = 0;
        while (m < n)
        {
          arrayOfInt1[m] = paramVorbisBitArray.readBits(4);
          i1 = k;
          if (arrayOfInt1[m] > k) {
            i1 = arrayOfInt1[m];
          }
          m++;
          k = i1;
        }
        int[] arrayOfInt2 = new int[k + 1];
        for (k = 0; k < arrayOfInt2.length; k++)
        {
          arrayOfInt2[k] = (paramVorbisBitArray.readBits(3) + 1);
          i1 = paramVorbisBitArray.readBits(2);
          if (i1 > 0) {
            paramVorbisBitArray.skipBits(8);
          }
          for (m = 0; m < 1 << i1; m++) {
            paramVorbisBitArray.skipBits(8);
          }
        }
        paramVorbisBitArray.skipBits(2);
        int i2 = paramVorbisBitArray.readBits(4);
        int i1 = 0;
        k = 0;
        m = 0;
        while (k < n)
        {
          i1 += arrayOfInt2[arrayOfInt1[k]];
          while (m < i1)
          {
            paramVorbisBitArray.skipBits(i2);
            m++;
          }
          k++;
        }
      }
    }
  }
  
  private static void readMappings(int paramInt, VorbisBitArray paramVorbisBitArray)
    throws ParserException
  {
    int i = paramVorbisBitArray.readBits(6);
    int j = 0;
    if (j < i + 1)
    {
      int k = paramVorbisBitArray.readBits(16);
      switch (k)
      {
      default: 
        Log.e("VorbisUtil", "mapping type other than 0 not supported: " + k);
      }
      for (;;)
      {
        j++;
        break;
        if (paramVorbisBitArray.readBit()) {}
        for (k = paramVorbisBitArray.readBits(4) + 1; paramVorbisBitArray.readBit(); k = 1)
        {
          int m = paramVorbisBitArray.readBits(8);
          for (n = 0; n < m + 1; n++)
          {
            paramVorbisBitArray.skipBits(iLog(paramInt - 1));
            paramVorbisBitArray.skipBits(iLog(paramInt - 1));
          }
        }
        if (paramVorbisBitArray.readBits(2) != 0) {
          throw new ParserException("to reserved bits must be zero after mapping coupling steps");
        }
        if (k > 1) {
          for (n = 0; n < paramInt; n++) {
            paramVorbisBitArray.skipBits(4);
          }
        }
        for (int n = 0; n < k; n++)
        {
          paramVorbisBitArray.skipBits(8);
          paramVorbisBitArray.skipBits(8);
          paramVorbisBitArray.skipBits(8);
        }
      }
    }
  }
  
  private static Mode[] readModes(VorbisBitArray paramVorbisBitArray)
  {
    int i = paramVorbisBitArray.readBits(6) + 1;
    Mode[] arrayOfMode = new Mode[i];
    for (int j = 0; j < i; j++) {
      arrayOfMode[j] = new Mode(paramVorbisBitArray.readBit(), paramVorbisBitArray.readBits(16), paramVorbisBitArray.readBits(16), paramVorbisBitArray.readBits(8));
    }
    return arrayOfMode;
  }
  
  private static void readResidues(VorbisBitArray paramVorbisBitArray)
    throws ParserException
  {
    int i = paramVorbisBitArray.readBits(6);
    for (int j = 0; j < i + 1; j++)
    {
      if (paramVorbisBitArray.readBits(16) > 2) {
        throw new ParserException("residueType greater than 2 is not decodable");
      }
      paramVorbisBitArray.skipBits(24);
      paramVorbisBitArray.skipBits(24);
      paramVorbisBitArray.skipBits(24);
      int k = paramVorbisBitArray.readBits(6) + 1;
      paramVorbisBitArray.skipBits(8);
      int[] arrayOfInt = new int[k];
      int n;
      for (int m = 0; m < k; m++)
      {
        n = 0;
        int i1 = paramVorbisBitArray.readBits(3);
        if (paramVorbisBitArray.readBit()) {
          n = paramVorbisBitArray.readBits(5);
        }
        arrayOfInt[m] = (n * 8 + i1);
      }
      for (m = 0; m < k; m++) {
        for (n = 0; n < 8; n++) {
          if ((arrayOfInt[m] & 1 << n) != 0) {
            paramVorbisBitArray.skipBits(8);
          }
        }
      }
    }
  }
  
  public static CommentHeader readVorbisCommentHeader(ParsableByteArray paramParsableByteArray)
    throws ParserException
  {
    verifyVorbisHeaderCapturePattern(3, paramParsableByteArray, false);
    String str = paramParsableByteArray.readString((int)paramParsableByteArray.readLittleEndianUnsignedInt());
    int i = str.length();
    long l = paramParsableByteArray.readLittleEndianUnsignedInt();
    String[] arrayOfString = new String[(int)l];
    int j = i + 11 + 4;
    for (i = 0; i < l; i++)
    {
      arrayOfString[i] = paramParsableByteArray.readString((int)paramParsableByteArray.readLittleEndianUnsignedInt());
      j = j + 4 + arrayOfString[i].length();
    }
    if ((paramParsableByteArray.readUnsignedByte() & 0x1) == 0) {
      throw new ParserException("framing bit expected to be set");
    }
    return new CommentHeader(str, arrayOfString, j + 1);
  }
  
  public static VorbisIdHeader readVorbisIdentificationHeader(ParsableByteArray paramParsableByteArray)
    throws ParserException
  {
    verifyVorbisHeaderCapturePattern(1, paramParsableByteArray, false);
    long l1 = paramParsableByteArray.readLittleEndianUnsignedInt();
    int i = paramParsableByteArray.readUnsignedByte();
    long l2 = paramParsableByteArray.readLittleEndianUnsignedInt();
    int j = paramParsableByteArray.readLittleEndianInt();
    int k = paramParsableByteArray.readLittleEndianInt();
    int m = paramParsableByteArray.readLittleEndianInt();
    int n = paramParsableByteArray.readUnsignedByte();
    int i1 = (int)Math.pow(2.0D, n & 0xF);
    n = (int)Math.pow(2.0D, (n & 0xF0) >> 4);
    if ((paramParsableByteArray.readUnsignedByte() & 0x1) > 0) {}
    for (boolean bool = true;; bool = false) {
      return new VorbisIdHeader(l1, i, l2, j, k, m, i1, n, bool, Arrays.copyOf(paramParsableByteArray.data, paramParsableByteArray.limit()));
    }
  }
  
  public static Mode[] readVorbisModes(ParsableByteArray paramParsableByteArray, int paramInt)
    throws ParserException
  {
    verifyVorbisHeaderCapturePattern(5, paramParsableByteArray, false);
    int i = paramParsableByteArray.readUnsignedByte();
    VorbisBitArray localVorbisBitArray = new VorbisBitArray(paramParsableByteArray.data);
    localVorbisBitArray.skipBits(paramParsableByteArray.getPosition() * 8);
    for (int j = 0; j < i + 1; j++) {
      readBook(localVorbisBitArray);
    }
    i = localVorbisBitArray.readBits(6);
    for (j = 0; j < i + 1; j++) {
      if (localVorbisBitArray.readBits(16) != 0) {
        throw new ParserException("placeholder of time domain transforms not zeroed out");
      }
    }
    readFloors(localVorbisBitArray);
    readResidues(localVorbisBitArray);
    readMappings(paramInt, localVorbisBitArray);
    paramParsableByteArray = readModes(localVorbisBitArray);
    if (!localVorbisBitArray.readBit()) {
      throw new ParserException("framing bit after modes not set as expected");
    }
    return paramParsableByteArray;
  }
  
  public static boolean verifyVorbisHeaderCapturePattern(int paramInt, ParsableByteArray paramParsableByteArray, boolean paramBoolean)
    throws ParserException
  {
    boolean bool = false;
    if (paramParsableByteArray.bytesLeft() < 7) {
      if (!paramBoolean) {}
    }
    for (;;)
    {
      return bool;
      throw new ParserException("too short header: " + paramParsableByteArray.bytesLeft());
      if (paramParsableByteArray.readUnsignedByte() != paramInt)
      {
        if (!paramBoolean) {
          throw new ParserException("expected header type " + Integer.toHexString(paramInt));
        }
      }
      else if ((paramParsableByteArray.readUnsignedByte() != 118) || (paramParsableByteArray.readUnsignedByte() != 111) || (paramParsableByteArray.readUnsignedByte() != 114) || (paramParsableByteArray.readUnsignedByte() != 98) || (paramParsableByteArray.readUnsignedByte() != 105) || (paramParsableByteArray.readUnsignedByte() != 115))
      {
        if (!paramBoolean) {
          throw new ParserException("expected characters 'vorbis'");
        }
      }
      else {
        bool = true;
      }
    }
  }
  
  public static final class CodeBook
  {
    public final int dimensions;
    public final int entries;
    public final boolean isOrdered;
    public final long[] lengthMap;
    public final int lookupType;
    
    public CodeBook(int paramInt1, int paramInt2, long[] paramArrayOfLong, int paramInt3, boolean paramBoolean)
    {
      this.dimensions = paramInt1;
      this.entries = paramInt2;
      this.lengthMap = paramArrayOfLong;
      this.lookupType = paramInt3;
      this.isOrdered = paramBoolean;
    }
  }
  
  public static final class CommentHeader
  {
    public final String[] comments;
    public final int length;
    public final String vendor;
    
    public CommentHeader(String paramString, String[] paramArrayOfString, int paramInt)
    {
      this.vendor = paramString;
      this.comments = paramArrayOfString;
      this.length = paramInt;
    }
  }
  
  public static final class Mode
  {
    public final boolean blockFlag;
    public final int mapping;
    public final int transformType;
    public final int windowType;
    
    public Mode(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3)
    {
      this.blockFlag = paramBoolean;
      this.windowType = paramInt1;
      this.transformType = paramInt2;
      this.mapping = paramInt3;
    }
  }
  
  public static final class VorbisIdHeader
  {
    public final int bitrateMax;
    public final int bitrateMin;
    public final int bitrateNominal;
    public final int blockSize0;
    public final int blockSize1;
    public final int channels;
    public final byte[] data;
    public final boolean framingFlag;
    public final long sampleRate;
    public final long version;
    
    public VorbisIdHeader(long paramLong1, int paramInt1, long paramLong2, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean, byte[] paramArrayOfByte)
    {
      this.version = paramLong1;
      this.channels = paramInt1;
      this.sampleRate = paramLong2;
      this.bitrateMax = paramInt2;
      this.bitrateNominal = paramInt3;
      this.bitrateMin = paramInt4;
      this.blockSize0 = paramInt5;
      this.blockSize1 = paramInt6;
      this.framingFlag = paramBoolean;
      this.data = paramArrayOfByte;
    }
    
    public int getApproximateBitrate()
    {
      if (this.bitrateNominal == 0) {}
      for (int i = (this.bitrateMin + this.bitrateMax) / 2;; i = this.bitrateNominal) {
        return i;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ogg/VorbisUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */