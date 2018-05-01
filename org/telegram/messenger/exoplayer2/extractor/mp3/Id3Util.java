package org.telegram.messenger.exoplayer2.extractor.mp3;

import android.util.Pair;
import java.io.IOException;
import java.nio.charset.Charset;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.GaplessInfoHolder;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

final class Id3Util
{
  private static final Charset[] CHARSET_BY_ENCODING = { Charset.forName("ISO-8859-1"), Charset.forName("UTF-16LE"), Charset.forName("UTF-16BE"), Charset.forName("UTF-8") };
  private static final int ID3_TAG = Util.getIntegerCodeForString("ID3");
  private static final int MAXIMUM_METADATA_SIZE = 3145728;
  
  private static boolean canParseMetadata(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return (paramInt2 != 255) && (paramInt1 >= 2) && (paramInt1 <= 4) && (paramInt4 <= 3145728) && ((paramInt1 != 2) || (((paramInt3 & 0x3F) == 0) && ((paramInt3 & 0x40) == 0))) && ((paramInt1 != 3) || ((paramInt3 & 0x1F) == 0)) && ((paramInt1 != 4) || ((paramInt3 & 0xF) == 0));
  }
  
  private static boolean canUnescapeVersion4(ParsableByteArray paramParsableByteArray, boolean paramBoolean)
  {
    paramParsableByteArray.setPosition(0);
    for (;;)
    {
      if ((paramParsableByteArray.bytesLeft() < 10) || (paramParsableByteArray.readInt() == 0)) {
        return true;
      }
      long l2 = paramParsableByteArray.readUnsignedInt();
      long l1 = l2;
      if (!paramBoolean)
      {
        if ((0x808080 & l2) != 0L) {
          return false;
        }
        l1 = l2 & 0x7F | (l2 >> 8 & 0x7F) << 7 | (l2 >> 16 & 0x7F) << 14 | (l2 >> 24 & 0x7F) << 21;
      }
      if (l1 > paramParsableByteArray.bytesLeft() - 2) {
        return false;
      }
      if (((paramParsableByteArray.readUnsignedShort() & 0x1) != 0) && (paramParsableByteArray.bytesLeft() < 4)) {
        return false;
      }
      paramParsableByteArray.skipBytes((int)l1);
    }
  }
  
  private static Pair<String, String> findNextComment(int paramInt, ParsableByteArray paramParsableByteArray)
  {
    if (paramInt == 2) {
      if (paramParsableByteArray.bytesLeft() >= 6) {}
    }
    label14:
    String str;
    int i;
    int j;
    label69:
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
              do
              {
                return null;
                str = paramParsableByteArray.readString(3, Charset.forName("US-ASCII"));
              } while (str.equals("\000\000\000"));
              i = paramParsableByteArray.readUnsignedInt24();
            } while ((i == 0) || (i > paramParsableByteArray.bytesLeft()));
            j = i;
            if (!str.equals("COM")) {
              break;
            }
            j = i;
            paramInt = paramParsableByteArray.readUnsignedByte();
          } while ((paramInt < 0) || (paramInt >= CHARSET_BY_ENCODING.length));
          paramParsableByteArray = paramParsableByteArray.readString(j - 1, CHARSET_BY_ENCODING[paramInt]).split("\000");
        } while (paramParsableByteArray.length != 2);
        return Pair.create(paramParsableByteArray[0], paramParsableByteArray[1]);
      } while (paramParsableByteArray.bytesLeft() < 10);
      str = paramParsableByteArray.readString(4, Charset.forName("US-ASCII"));
    } while (str.equals("\000\000\000\000"));
    if (paramInt == 4)
    {
      i = paramParsableByteArray.readSynchSafeInt();
      label161:
      if ((i == 0) || (i > paramParsableByteArray.bytesLeft() - 2)) {
        break label243;
      }
      j = paramParsableByteArray.readUnsignedShort();
      if (((paramInt != 4) || ((j & 0xC) == 0)) && ((paramInt != 3) || ((j & 0xC0) == 0))) {
        break label245;
      }
    }
    label243:
    label245:
    for (int k = 1;; k = 0)
    {
      j = i;
      if (k == 0)
      {
        j = i;
        if (str.equals("COMM")) {
          break label69;
        }
        j = i;
      }
      paramParsableByteArray.skipBytes(j);
      break;
      i = paramParsableByteArray.readUnsignedIntToInt();
      break label161;
      break label14;
    }
  }
  
  private static void parseGaplessInfo(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, GaplessInfoHolder paramGaplessInfoHolder)
  {
    unescape(paramParsableByteArray, paramInt1, paramInt2);
    paramParsableByteArray.setPosition(0);
    if ((paramInt1 == 3) && ((paramInt2 & 0x40) != 0))
    {
      if (paramParsableByteArray.bytesLeft() < 4) {}
      do
      {
        do
        {
          return;
          paramInt2 = paramParsableByteArray.readUnsignedIntToInt();
        } while (paramInt2 > paramParsableByteArray.bytesLeft());
        if (paramInt2 < 6) {
          break;
        }
        paramParsableByteArray.skipBytes(2);
        int i = paramParsableByteArray.readUnsignedIntToInt();
        paramParsableByteArray.setPosition(4);
        paramParsableByteArray.setLimit(paramParsableByteArray.limit() - i);
      } while (paramParsableByteArray.bytesLeft() < paramInt2);
      paramParsableByteArray.skipBytes(paramInt2);
    }
    for (;;)
    {
      Pair localPair = findNextComment(paramInt1, paramParsableByteArray);
      if (localPair == null) {
        break;
      }
      if ((((String)localPair.first).length() > 3) && (paramGaplessInfoHolder.setFromComment(((String)localPair.first).substring(3), (String)localPair.second)))
      {
        return;
        if ((paramInt1 == 4) && ((paramInt2 & 0x40) != 0))
        {
          if (paramParsableByteArray.bytesLeft() < 4) {
            break;
          }
          paramInt2 = paramParsableByteArray.readSynchSafeInt();
          if ((paramInt2 < 6) || (paramInt2 > paramParsableByteArray.bytesLeft() + 4)) {
            break;
          }
          paramParsableByteArray.setPosition(paramInt2);
        }
      }
    }
  }
  
  public static void parseId3(ExtractorInput paramExtractorInput, GaplessInfoHolder paramGaplessInfoHolder)
    throws IOException, InterruptedException
  {
    ParsableByteArray localParsableByteArray = new ParsableByteArray(10);
    int i = 0;
    paramExtractorInput.peekFully(localParsableByteArray.data, 0, 10);
    localParsableByteArray.setPosition(0);
    if (localParsableByteArray.readUnsignedInt24() != ID3_TAG)
    {
      paramExtractorInput.resetPeekPosition();
      paramExtractorInput.advancePeekPosition(i);
      return;
    }
    int j = localParsableByteArray.readUnsignedByte();
    int k = localParsableByteArray.readUnsignedByte();
    int m = localParsableByteArray.readUnsignedByte();
    int n = localParsableByteArray.readSynchSafeInt();
    if ((!paramGaplessInfoHolder.hasGaplessInfo()) && (canParseMetadata(j, k, m, n)))
    {
      byte[] arrayOfByte = new byte[n];
      paramExtractorInput.peekFully(arrayOfByte, 0, n);
      parseGaplessInfo(new ParsableByteArray(arrayOfByte), j, m, paramGaplessInfoHolder);
    }
    for (;;)
    {
      i += n + 10;
      break;
      paramExtractorInput.advancePeekPosition(n);
    }
  }
  
  private static boolean unescape(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2)
  {
    boolean bool = false;
    if (paramInt1 != 4) {
      if ((paramInt2 & 0x80) != 0)
      {
        byte[] arrayOfByte = paramParsableByteArray.data;
        paramInt2 = arrayOfByte.length;
        paramInt1 = 0;
        while (paramInt1 + 1 < paramInt2)
        {
          int i = paramInt2;
          if ((arrayOfByte[paramInt1] & 0xFF) == 255)
          {
            i = paramInt2;
            if (arrayOfByte[(paramInt1 + 1)] == 0)
            {
              System.arraycopy(arrayOfByte, paramInt1 + 2, arrayOfByte, paramInt1 + 1, paramInt2 - paramInt1 - 2);
              i = paramInt2 - 1;
            }
          }
          paramInt1 += 1;
          paramInt2 = i;
        }
        paramParsableByteArray.setLimit(paramInt2);
      }
    }
    for (;;)
    {
      bool = true;
      do
      {
        return bool;
        if (canUnescapeVersion4(paramParsableByteArray, false))
        {
          unescapeVersion4(paramParsableByteArray, false);
          break;
        }
      } while (!canUnescapeVersion4(paramParsableByteArray, true));
      unescapeVersion4(paramParsableByteArray, true);
    }
  }
  
  private static void unescapeVersion4(ParsableByteArray paramParsableByteArray, boolean paramBoolean)
  {
    paramParsableByteArray.setPosition(0);
    byte[] arrayOfByte = paramParsableByteArray.data;
    for (;;)
    {
      if ((paramParsableByteArray.bytesLeft() < 10) || (paramParsableByteArray.readInt() == 0)) {
        return;
      }
      if (paramBoolean) {}
      int i3;
      int i;
      int k;
      for (int j = paramParsableByteArray.readUnsignedIntToInt();; j = paramParsableByteArray.readSynchSafeInt())
      {
        i3 = paramParsableByteArray.readUnsignedShort();
        i = j;
        k = i3;
        if ((i3 & 0x1) != 0)
        {
          i = paramParsableByteArray.getPosition();
          System.arraycopy(arrayOfByte, i + 4, arrayOfByte, i, paramParsableByteArray.bytesLeft() - 4);
          i = j - 4;
          k = i3 & 0xFFFFFFFE;
          paramParsableByteArray.setLimit(paramParsableByteArray.limit() - 4);
        }
        m = i;
        j = k;
        if ((k & 0x2) == 0) {
          break label257;
        }
        j = paramParsableByteArray.getPosition() + 1;
        int n = 0;
        m = j;
        while (n + 1 < i)
        {
          int i1 = i;
          int i2 = j;
          if ((arrayOfByte[(j - 1)] & 0xFF) == 255)
          {
            i1 = i;
            i2 = j;
            if (arrayOfByte[j] == 0)
            {
              i2 = j + 1;
              i1 = i - 1;
            }
          }
          arrayOfByte[m] = arrayOfByte[i2];
          n += 1;
          m += 1;
          j = i2 + 1;
          i = i1;
        }
      }
      paramParsableByteArray.setLimit(paramParsableByteArray.limit() - (j - m));
      System.arraycopy(arrayOfByte, j, arrayOfByte, m, paramParsableByteArray.bytesLeft() - j);
      j = k & 0xFFFFFFFD;
      int m = i;
      label257:
      if ((j != i3) || (paramBoolean))
      {
        i = paramParsableByteArray.getPosition() - 6;
        writeSyncSafeInteger(arrayOfByte, i, m);
        arrayOfByte[(i + 4)] = ((byte)(j >> 8));
        arrayOfByte[(i + 5)] = ((byte)(j & 0xFF));
      }
      paramParsableByteArray.skipBytes(m);
    }
  }
  
  private static void writeSyncSafeInteger(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    paramArrayOfByte[paramInt1] = ((byte)(paramInt2 >> 21 & 0x7F));
    paramArrayOfByte[(paramInt1 + 1)] = ((byte)(paramInt2 >> 14 & 0x7F));
    paramArrayOfByte[(paramInt1 + 2)] = ((byte)(paramInt2 >> 7 & 0x7F));
    paramArrayOfByte[(paramInt1 + 3)] = ((byte)(paramInt2 & 0x7F));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/mp3/Id3Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */