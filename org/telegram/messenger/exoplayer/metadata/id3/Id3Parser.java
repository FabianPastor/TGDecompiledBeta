package org.telegram.messenger.exoplayer.metadata.id3;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.metadata.MetadataParser;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

public final class Id3Parser
  implements MetadataParser<List<Id3Frame>>
{
  private static final int ID3_TEXT_ENCODING_ISO_8859_1 = 0;
  private static final int ID3_TEXT_ENCODING_UTF_16 = 1;
  private static final int ID3_TEXT_ENCODING_UTF_16BE = 2;
  private static final int ID3_TEXT_ENCODING_UTF_8 = 3;
  
  private static int delimiterLength(int paramInt)
  {
    if ((paramInt == 0) || (paramInt == 3)) {
      return 1;
    }
    return 2;
  }
  
  private static String getCharsetName(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "ISO-8859-1";
    case 0: 
      return "ISO-8859-1";
    case 1: 
      return "UTF-16";
    case 2: 
      return "UTF-16BE";
    }
    return "UTF-8";
  }
  
  private static int indexOfEos(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = indexOfZeroByte(paramArrayOfByte, paramInt1);
    if (paramInt2 != 0)
    {
      paramInt1 = i;
      if (paramInt2 != 3) {}
    }
    else
    {
      return i;
    }
    do
    {
      paramInt1 = indexOfZeroByte(paramArrayOfByte, paramInt1 + 1);
      if (paramInt1 >= paramArrayOfByte.length - 1) {
        break;
      }
    } while (paramArrayOfByte[(paramInt1 + 1)] != 0);
    return paramInt1;
    return paramArrayOfByte.length;
  }
  
  private static int indexOfZeroByte(byte[] paramArrayOfByte, int paramInt)
  {
    while (paramInt < paramArrayOfByte.length)
    {
      if (paramArrayOfByte[paramInt] == 0) {
        return paramInt;
      }
      paramInt += 1;
    }
    return paramArrayOfByte.length;
  }
  
  private static ApicFrame parseApicFrame(ParsableByteArray paramParsableByteArray, int paramInt)
    throws UnsupportedEncodingException
  {
    int i = paramParsableByteArray.readUnsignedByte();
    String str = getCharsetName(i);
    byte[] arrayOfByte = new byte[paramInt - 1];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt - 1);
    int j = indexOfZeroByte(arrayOfByte, 0);
    paramParsableByteArray = new String(arrayOfByte, 0, j, "ISO-8859-1");
    paramInt = arrayOfByte[(j + 1)];
    j += 2;
    int k = indexOfEos(arrayOfByte, j, i);
    return new ApicFrame(paramParsableByteArray, new String(arrayOfByte, j, k - j, str), paramInt & 0xFF, Arrays.copyOfRange(arrayOfByte, k + delimiterLength(i), arrayOfByte.length));
  }
  
  private static BinaryFrame parseBinaryFrame(ParsableByteArray paramParsableByteArray, int paramInt, String paramString)
  {
    byte[] arrayOfByte = new byte[paramInt];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt);
    return new BinaryFrame(paramString, arrayOfByte);
  }
  
  private static GeobFrame parseGeobFrame(ParsableByteArray paramParsableByteArray, int paramInt)
    throws UnsupportedEncodingException
  {
    int i = paramParsableByteArray.readUnsignedByte();
    String str1 = getCharsetName(i);
    byte[] arrayOfByte = new byte[paramInt - 1];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt - 1);
    paramInt = indexOfZeroByte(arrayOfByte, 0);
    paramParsableByteArray = new String(arrayOfByte, 0, paramInt, "ISO-8859-1");
    paramInt += 1;
    int j = indexOfEos(arrayOfByte, paramInt, i);
    String str2 = new String(arrayOfByte, paramInt, j - paramInt, str1);
    paramInt = j + delimiterLength(i);
    j = indexOfEos(arrayOfByte, paramInt, i);
    return new GeobFrame(paramParsableByteArray, str2, new String(arrayOfByte, paramInt, j - paramInt, str1), Arrays.copyOfRange(arrayOfByte, j + delimiterLength(i), arrayOfByte.length));
  }
  
  private static int parseId3Header(ParsableByteArray paramParsableByteArray)
    throws ParserException
  {
    int i = paramParsableByteArray.readUnsignedByte();
    int j = paramParsableByteArray.readUnsignedByte();
    int k = paramParsableByteArray.readUnsignedByte();
    if ((i != 73) || (j != 68) || (k != 51)) {
      throw new ParserException(String.format(Locale.US, "Unexpected ID3 file identifier, expected \"ID3\", actual \"%c%c%c\".", new Object[] { Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k) }));
    }
    paramParsableByteArray.skipBytes(2);
    k = paramParsableByteArray.readUnsignedByte();
    j = paramParsableByteArray.readSynchSafeInt();
    i = j;
    if ((k & 0x2) != 0)
    {
      i = paramParsableByteArray.readSynchSafeInt();
      if (i > 4) {
        paramParsableByteArray.skipBytes(i - 4);
      }
      i = j - i;
    }
    j = i;
    if ((k & 0x8) != 0) {
      j = i - 10;
    }
    return j;
  }
  
  private static PrivFrame parsePrivFrame(ParsableByteArray paramParsableByteArray, int paramInt)
    throws UnsupportedEncodingException
  {
    byte[] arrayOfByte = new byte[paramInt];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt);
    paramInt = indexOfZeroByte(arrayOfByte, 0);
    return new PrivFrame(new String(arrayOfByte, 0, paramInt, "ISO-8859-1"), Arrays.copyOfRange(arrayOfByte, paramInt + 1, arrayOfByte.length));
  }
  
  private static TextInformationFrame parseTextInformationFrame(ParsableByteArray paramParsableByteArray, int paramInt, String paramString)
    throws UnsupportedEncodingException
  {
    int i = paramParsableByteArray.readUnsignedByte();
    String str = getCharsetName(i);
    byte[] arrayOfByte = new byte[paramInt - 1];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt - 1);
    return new TextInformationFrame(paramString, new String(arrayOfByte, 0, indexOfEos(arrayOfByte, 0, i), str));
  }
  
  private static TxxxFrame parseTxxxFrame(ParsableByteArray paramParsableByteArray, int paramInt)
    throws UnsupportedEncodingException
  {
    int i = paramParsableByteArray.readUnsignedByte();
    String str = getCharsetName(i);
    byte[] arrayOfByte = new byte[paramInt - 1];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt - 1);
    paramInt = indexOfEos(arrayOfByte, 0, i);
    paramParsableByteArray = new String(arrayOfByte, 0, paramInt, str);
    paramInt += delimiterLength(i);
    return new TxxxFrame(paramParsableByteArray, new String(arrayOfByte, paramInt, indexOfEos(arrayOfByte, paramInt, i) - paramInt, str));
  }
  
  public boolean canParse(String paramString)
  {
    return paramString.equals("application/id3");
  }
  
  public List<Id3Frame> parse(byte[] paramArrayOfByte, int paramInt)
    throws ParserException
  {
    ArrayList localArrayList = new ArrayList();
    ParsableByteArray localParsableByteArray = new ParsableByteArray(paramArrayOfByte, paramInt);
    paramInt = parseId3Header(localParsableByteArray);
    int i;
    int j;
    int k;
    int m;
    int n;
    if (paramInt > 0)
    {
      i = localParsableByteArray.readUnsignedByte();
      j = localParsableByteArray.readUnsignedByte();
      k = localParsableByteArray.readUnsignedByte();
      m = localParsableByteArray.readUnsignedByte();
      n = localParsableByteArray.readSynchSafeInt();
      if (n > 1) {}
    }
    else
    {
      return Collections.unmodifiableList(localArrayList);
    }
    localParsableByteArray.skipBytes(2);
    if ((i == 84) && (j == 88) && (k == 88) && (m == 88)) {}
    for (;;)
    {
      try
      {
        paramArrayOfByte = parseTxxxFrame(localParsableByteArray, n);
        localArrayList.add(paramArrayOfByte);
        paramInt -= n + 10;
      }
      catch (UnsupportedEncodingException paramArrayOfByte)
      {
        throw new ParserException(paramArrayOfByte);
      }
      if ((i == 80) && (j == 82) && (k == 73) && (m == 86)) {
        paramArrayOfByte = parsePrivFrame(localParsableByteArray, n);
      } else if ((i == 71) && (j == 69) && (k == 79) && (m == 66)) {
        paramArrayOfByte = parseGeobFrame(localParsableByteArray, n);
      } else if ((i == 65) && (j == 80) && (k == 73) && (m == 67)) {
        paramArrayOfByte = parseApicFrame(localParsableByteArray, n);
      } else if (i == 84) {
        paramArrayOfByte = parseTextInformationFrame(localParsableByteArray, n, String.format(Locale.US, "%c%c%c%c", new Object[] { Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(m) }));
      } else {
        paramArrayOfByte = parseBinaryFrame(localParsableByteArray, n, String.format(Locale.US, "%c%c%c%c", new Object[] { Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(m) }));
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/metadata/id3/Id3Parser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */