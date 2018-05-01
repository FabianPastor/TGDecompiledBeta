package org.telegram.messenger.exoplayer2.metadata.id3;

import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.metadata.MetadataDecoder;
import org.telegram.messenger.exoplayer2.metadata.MetadataInputBuffer;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class Id3Decoder
  implements MetadataDecoder
{
  private static final int FRAME_FLAG_V3_HAS_GROUP_IDENTIFIER = 32;
  private static final int FRAME_FLAG_V3_IS_COMPRESSED = 128;
  private static final int FRAME_FLAG_V3_IS_ENCRYPTED = 64;
  private static final int FRAME_FLAG_V4_HAS_DATA_LENGTH = 1;
  private static final int FRAME_FLAG_V4_HAS_GROUP_IDENTIFIER = 64;
  private static final int FRAME_FLAG_V4_IS_COMPRESSED = 8;
  private static final int FRAME_FLAG_V4_IS_ENCRYPTED = 4;
  private static final int FRAME_FLAG_V4_IS_UNSYNCHRONIZED = 2;
  public static final int ID3_HEADER_LENGTH = 10;
  public static final int ID3_TAG = Util.getIntegerCodeForString("ID3");
  private static final int ID3_TEXT_ENCODING_ISO_8859_1 = 0;
  private static final int ID3_TEXT_ENCODING_UTF_16 = 1;
  private static final int ID3_TEXT_ENCODING_UTF_16BE = 2;
  private static final int ID3_TEXT_ENCODING_UTF_8 = 3;
  private static final String TAG = "Id3Decoder";
  private final FramePredicate framePredicate;
  
  public Id3Decoder()
  {
    this(null);
  }
  
  public Id3Decoder(FramePredicate paramFramePredicate)
  {
    this.framePredicate = paramFramePredicate;
  }
  
  private static byte[] copyOfRangeIfValid(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramInt2 <= paramInt1) {}
    for (paramArrayOfByte = new byte[0];; paramArrayOfByte = Arrays.copyOfRange(paramArrayOfByte, paramInt1, paramInt2)) {
      return paramArrayOfByte;
    }
  }
  
  private static ApicFrame decodeApicFrame(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2)
    throws UnsupportedEncodingException
  {
    int i = paramParsableByteArray.readUnsignedByte();
    String str1 = getCharsetName(i);
    byte[] arrayOfByte = new byte[paramInt1 - 1];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt1 - 1);
    String str2;
    if (paramInt2 == 2)
    {
      paramInt2 = 2;
      str2 = "image/" + Util.toLowerInvariant(new String(arrayOfByte, 0, 3, "ISO-8859-1"));
      paramParsableByteArray = str2;
      paramInt1 = paramInt2;
      if (str2.equals("image/jpg"))
      {
        paramParsableByteArray = "image/jpeg";
        paramInt1 = paramInt2;
      }
    }
    for (;;)
    {
      paramInt2 = arrayOfByte[(paramInt1 + 1)];
      int j = paramInt1 + 2;
      paramInt1 = indexOfEos(arrayOfByte, j, i);
      return new ApicFrame(paramParsableByteArray, new String(arrayOfByte, j, paramInt1 - j, str1), paramInt2 & 0xFF, copyOfRangeIfValid(arrayOfByte, paramInt1 + delimiterLength(i), arrayOfByte.length));
      paramInt2 = indexOfZeroByte(arrayOfByte, 0);
      str2 = Util.toLowerInvariant(new String(arrayOfByte, 0, paramInt2, "ISO-8859-1"));
      paramParsableByteArray = str2;
      paramInt1 = paramInt2;
      if (str2.indexOf('/') == -1)
      {
        paramParsableByteArray = "image/" + str2;
        paramInt1 = paramInt2;
      }
    }
  }
  
  private static BinaryFrame decodeBinaryFrame(ParsableByteArray paramParsableByteArray, int paramInt, String paramString)
  {
    byte[] arrayOfByte = new byte[paramInt];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt);
    return new BinaryFrame(paramString, arrayOfByte);
  }
  
  private static ChapterFrame decodeChapterFrame(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3, FramePredicate paramFramePredicate)
    throws UnsupportedEncodingException
  {
    int i = paramParsableByteArray.getPosition();
    int j = indexOfZeroByte(paramParsableByteArray.data, i);
    String str = new String(paramParsableByteArray.data, i, j - i, "ISO-8859-1");
    paramParsableByteArray.setPosition(j + 1);
    j = paramParsableByteArray.readInt();
    int k = paramParsableByteArray.readInt();
    long l1 = paramParsableByteArray.readUnsignedInt();
    long l2 = l1;
    if (l1 == 4294967295L) {
      l2 = -1L;
    }
    long l3 = paramParsableByteArray.readUnsignedInt();
    l1 = l3;
    if (l3 == 4294967295L) {
      l1 = -1L;
    }
    ArrayList localArrayList = new ArrayList();
    while (paramParsableByteArray.getPosition() < i + paramInt1)
    {
      Id3Frame localId3Frame = decodeFrame(paramInt2, paramParsableByteArray, paramBoolean, paramInt3, paramFramePredicate);
      if (localId3Frame != null) {
        localArrayList.add(localId3Frame);
      }
    }
    paramParsableByteArray = new Id3Frame[localArrayList.size()];
    localArrayList.toArray(paramParsableByteArray);
    return new ChapterFrame(str, j, k, l2, l1, paramParsableByteArray);
  }
  
  private static ChapterTocFrame decodeChapterTOCFrame(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3, FramePredicate paramFramePredicate)
    throws UnsupportedEncodingException
  {
    int i = paramParsableByteArray.getPosition();
    int j = indexOfZeroByte(paramParsableByteArray.data, i);
    String str = new String(paramParsableByteArray.data, i, j - i, "ISO-8859-1");
    paramParsableByteArray.setPosition(j + 1);
    j = paramParsableByteArray.readUnsignedByte();
    boolean bool1;
    if ((j & 0x2) != 0)
    {
      bool1 = true;
      if ((j & 0x1) == 0) {
        break label158;
      }
    }
    String[] arrayOfString;
    label158:
    for (boolean bool2 = true;; bool2 = false)
    {
      int k = paramParsableByteArray.readUnsignedByte();
      arrayOfString = new String[k];
      for (j = 0; j < k; j++)
      {
        int m = paramParsableByteArray.getPosition();
        int n = indexOfZeroByte(paramParsableByteArray.data, m);
        arrayOfString[j] = new String(paramParsableByteArray.data, m, n - m, "ISO-8859-1");
        paramParsableByteArray.setPosition(n + 1);
      }
      bool1 = false;
      break;
    }
    ArrayList localArrayList = new ArrayList();
    while (paramParsableByteArray.getPosition() < i + paramInt1)
    {
      Id3Frame localId3Frame = decodeFrame(paramInt2, paramParsableByteArray, paramBoolean, paramInt3, paramFramePredicate);
      if (localId3Frame != null) {
        localArrayList.add(localId3Frame);
      }
    }
    paramParsableByteArray = new Id3Frame[localArrayList.size()];
    localArrayList.toArray(paramParsableByteArray);
    return new ChapterTocFrame(str, bool1, bool2, arrayOfString, paramParsableByteArray);
  }
  
  private static CommentFrame decodeCommentFrame(ParsableByteArray paramParsableByteArray, int paramInt)
    throws UnsupportedEncodingException
  {
    if (paramInt < 4)
    {
      paramParsableByteArray = null;
      return paramParsableByteArray;
    }
    int i = paramParsableByteArray.readUnsignedByte();
    String str1 = getCharsetName(i);
    Object localObject = new byte[3];
    paramParsableByteArray.readBytes((byte[])localObject, 0, 3);
    localObject = new String((byte[])localObject, 0, 3);
    byte[] arrayOfByte = new byte[paramInt - 4];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt - 4);
    paramInt = indexOfEos(arrayOfByte, 0, i);
    String str2 = new String(arrayOfByte, 0, paramInt, str1);
    paramInt += delimiterLength(i);
    if (paramInt < arrayOfByte.length) {}
    for (paramParsableByteArray = new String(arrayOfByte, paramInt, indexOfEos(arrayOfByte, paramInt, i) - paramInt, str1);; paramParsableByteArray = "")
    {
      paramParsableByteArray = new CommentFrame((String)localObject, str2, paramParsableByteArray);
      break;
    }
  }
  
  private static Id3Frame decodeFrame(int paramInt1, ParsableByteArray paramParsableByteArray, boolean paramBoolean, int paramInt2, FramePredicate paramFramePredicate)
  {
    int i = paramParsableByteArray.readUnsignedByte();
    int j = paramParsableByteArray.readUnsignedByte();
    int k = paramParsableByteArray.readUnsignedByte();
    int m;
    int n;
    int i1;
    label95:
    int i2;
    if (paramInt1 >= 3)
    {
      m = paramParsableByteArray.readUnsignedByte();
      if (paramInt1 != 4) {
        break label156;
      }
      n = paramParsableByteArray.readUnsignedIntToInt();
      i1 = n;
      if (!paramBoolean) {
        i1 = n & 0xFF | (n >> 8 & 0xFF) << 7 | (n >> 16 & 0xFF) << 14 | (n >> 24 & 0xFF) << 21;
      }
      if (paramInt1 < 3) {
        break label179;
      }
      i2 = paramParsableByteArray.readUnsignedShort();
      label106:
      if ((i != 0) || (j != 0) || (k != 0) || (m != 0) || (i1 != 0) || (i2 != 0)) {
        break label185;
      }
      paramParsableByteArray.setPosition(paramParsableByteArray.limit());
      paramFramePredicate = null;
    }
    for (;;)
    {
      return paramFramePredicate;
      m = 0;
      break;
      label156:
      if (paramInt1 == 3)
      {
        i1 = paramParsableByteArray.readUnsignedIntToInt();
        break label95;
      }
      i1 = paramParsableByteArray.readUnsignedInt24();
      break label95;
      label179:
      i2 = 0;
      break label106;
      label185:
      int i3 = paramParsableByteArray.getPosition() + i1;
      if (i3 > paramParsableByteArray.limit())
      {
        Log.w("Id3Decoder", "Frame size exceeds remaining tag data");
        paramParsableByteArray.setPosition(paramParsableByteArray.limit());
        paramFramePredicate = null;
        continue;
      }
      if ((paramFramePredicate != null) && (!paramFramePredicate.evaluate(paramInt1, i, j, k, m)))
      {
        paramParsableByteArray.setPosition(i3);
        paramFramePredicate = null;
        continue;
      }
      int i4 = 0;
      int i5 = 0;
      int i6 = 0;
      n = 0;
      int i7 = 0;
      if (paramInt1 == 3) {
        if ((i2 & 0x80) != 0)
        {
          n = 1;
          label293:
          if ((i2 & 0x40) == 0) {
            break label363;
          }
          i5 = 1;
          label304:
          if ((i2 & 0x20) == 0) {
            break label369;
          }
          i7 = 1;
          label315:
          i2 = n;
          i4 = n;
          n = i2;
        }
      }
      for (;;)
      {
        if ((i4 != 0) || (i5 != 0))
        {
          Log.w("Id3Decoder", "Skipping unsupported compressed or encrypted frame");
          paramParsableByteArray.setPosition(i3);
          paramFramePredicate = null;
          break;
          n = 0;
          break label293;
          label363:
          i5 = 0;
          break label304;
          label369:
          i7 = 0;
          break label315;
          if (paramInt1 == 4)
          {
            if ((i2 & 0x40) != 0)
            {
              i7 = 1;
              label391:
              if ((i2 & 0x8) == 0) {
                break label441;
              }
              i4 = 1;
              label402:
              if ((i2 & 0x4) == 0) {
                break label447;
              }
              i5 = 1;
              label412:
              if ((i2 & 0x2) == 0) {
                break label453;
              }
              i6 = 1;
              label422:
              if ((i2 & 0x1) == 0) {
                break label459;
              }
            }
            label441:
            label447:
            label453:
            label459:
            for (n = 1;; n = 0)
            {
              break;
              i7 = 0;
              break label391;
              i4 = 0;
              break label402;
              i5 = 0;
              break label412;
              i6 = 0;
              break label422;
            }
          }
        }
      }
      i4 = i1;
      if (i7 != 0)
      {
        i4 = i1 - 1;
        paramParsableByteArray.skipBytes(1);
      }
      i1 = i4;
      if (n != 0)
      {
        i1 = i4 - 4;
        paramParsableByteArray.skipBytes(4);
      }
      n = i1;
      if (i6 != 0) {
        n = removeUnsynchronization(paramParsableByteArray, i1);
      }
      if ((i == 84) && (j == 88) && (k == 88) && ((paramInt1 == 2) || (m == 88))) {}
      try
      {
        paramFramePredicate = decodeTxxxFrame(paramParsableByteArray, n);
        for (;;)
        {
          if (paramFramePredicate == null)
          {
            StringBuilder localStringBuilder = new java/lang/StringBuilder;
            localStringBuilder.<init>();
            Log.w("Id3Decoder", "Failed to decode frame: id=" + getFrameId(paramInt1, i, j, k, m) + ", frameSize=" + n);
          }
          paramParsableByteArray.setPosition(i3);
          break;
          if (i == 84)
          {
            paramFramePredicate = decodeTextInformationFrame(paramParsableByteArray, n, getFrameId(paramInt1, i, j, k, m));
          }
          else if ((i == 87) && (j == 88) && (k == 88) && ((paramInt1 == 2) || (m == 88)))
          {
            paramFramePredicate = decodeWxxxFrame(paramParsableByteArray, n);
          }
          else if (i == 87)
          {
            paramFramePredicate = decodeUrlLinkFrame(paramParsableByteArray, n, getFrameId(paramInt1, i, j, k, m));
          }
          else if ((i == 80) && (j == 82) && (k == 73) && (m == 86))
          {
            paramFramePredicate = decodePrivFrame(paramParsableByteArray, n);
          }
          else if ((i == 71) && (j == 69) && (k == 79) && ((m == 66) || (paramInt1 == 2)))
          {
            paramFramePredicate = decodeGeobFrame(paramParsableByteArray, n);
          }
          else
          {
            if (paramInt1 == 2)
            {
              if ((i != 80) || (j != 73) || (k != 67)) {}
            }
            else {
              while ((i == 65) && (j == 80) && (k == 73) && (m == 67))
              {
                paramFramePredicate = decodeApicFrame(paramParsableByteArray, n, paramInt1);
                break;
              }
            }
            if ((i == 67) && (j == 79) && (k == 77) && ((m == 77) || (paramInt1 == 2))) {
              paramFramePredicate = decodeCommentFrame(paramParsableByteArray, n);
            } else if ((i == 67) && (j == 72) && (k == 65) && (m == 80)) {
              paramFramePredicate = decodeChapterFrame(paramParsableByteArray, n, paramInt1, paramBoolean, paramInt2, paramFramePredicate);
            } else if ((i == 67) && (j == 84) && (k == 79) && (m == 67)) {
              paramFramePredicate = decodeChapterTOCFrame(paramParsableByteArray, n, paramInt1, paramBoolean, paramInt2, paramFramePredicate);
            } else {
              paramFramePredicate = decodeBinaryFrame(paramParsableByteArray, n, getFrameId(paramInt1, i, j, k, m));
            }
          }
        }
      }
      catch (UnsupportedEncodingException paramFramePredicate)
      {
        Log.w("Id3Decoder", "Unsupported character encoding");
        paramFramePredicate = null;
        paramParsableByteArray.setPosition(i3);
      }
      finally
      {
        paramParsableByteArray.setPosition(i3);
      }
    }
  }
  
  private static GeobFrame decodeGeobFrame(ParsableByteArray paramParsableByteArray, int paramInt)
    throws UnsupportedEncodingException
  {
    int i = paramParsableByteArray.readUnsignedByte();
    String str1 = getCharsetName(i);
    byte[] arrayOfByte = new byte[paramInt - 1];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt - 1);
    paramInt = indexOfZeroByte(arrayOfByte, 0);
    String str2 = new String(arrayOfByte, 0, paramInt, "ISO-8859-1");
    int j = paramInt + 1;
    paramInt = indexOfEos(arrayOfByte, j, i);
    paramParsableByteArray = new String(arrayOfByte, j, paramInt - j, str1);
    j = paramInt + delimiterLength(i);
    paramInt = indexOfEos(arrayOfByte, j, i);
    return new GeobFrame(str2, paramParsableByteArray, new String(arrayOfByte, j, paramInt - j, str1), copyOfRangeIfValid(arrayOfByte, paramInt + delimiterLength(i), arrayOfByte.length));
  }
  
  private static Id3Header decodeHeader(ParsableByteArray paramParsableByteArray)
  {
    boolean bool = true;
    if (paramParsableByteArray.bytesLeft() < 10) {
      Log.w("Id3Decoder", "Data too short to be an ID3 tag");
    }
    int i;
    for (paramParsableByteArray = null;; paramParsableByteArray = null)
    {
      return paramParsableByteArray;
      i = paramParsableByteArray.readUnsignedInt24();
      if (i == ID3_TAG) {
        break;
      }
      Log.w("Id3Decoder", "Unexpected first three bytes of ID3 tag header: " + i);
    }
    int j = paramParsableByteArray.readUnsignedByte();
    paramParsableByteArray.skipBytes(1);
    int k = paramParsableByteArray.readUnsignedByte();
    int m = paramParsableByteArray.readSynchSafeInt();
    int n;
    if (j == 2)
    {
      if ((k & 0x40) != 0) {}
      for (n = 1;; n = 0)
      {
        i = m;
        if (n == 0) {
          break label174;
        }
        Log.w("Id3Decoder", "Skipped ID3 tag with majorVersion=2 and undefined compression scheme");
        paramParsableByteArray = null;
        break;
      }
    }
    if (j == 3) {
      if ((k & 0x40) != 0)
      {
        n = 1;
        label149:
        i = m;
        if (n != 0)
        {
          i = paramParsableByteArray.readInt();
          paramParsableByteArray.skipBytes(i);
          i = m - (i + 4);
        }
        label174:
        if ((j >= 4) || ((k & 0x80) == 0)) {
          break label319;
        }
      }
    }
    for (;;)
    {
      paramParsableByteArray = new Id3Header(j, bool, i);
      break;
      n = 0;
      break label149;
      if (j == 4)
      {
        if ((k & 0x40) != 0)
        {
          i = 1;
          label223:
          n = m;
          if (i != 0)
          {
            i = paramParsableByteArray.readSynchSafeInt();
            paramParsableByteArray.skipBytes(i - 4);
            n = m - i;
          }
          if ((k & 0x10) == 0) {
            break label282;
          }
        }
        label282:
        for (m = 1;; m = 0)
        {
          i = n;
          if (m == 0) {
            break;
          }
          i = n - 10;
          break;
          i = 0;
          break label223;
        }
      }
      Log.w("Id3Decoder", "Skipped ID3 tag with unsupported majorVersion=" + j);
      paramParsableByteArray = null;
      break;
      label319:
      bool = false;
    }
  }
  
  private static PrivFrame decodePrivFrame(ParsableByteArray paramParsableByteArray, int paramInt)
    throws UnsupportedEncodingException
  {
    byte[] arrayOfByte = new byte[paramInt];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt);
    paramInt = indexOfZeroByte(arrayOfByte, 0);
    return new PrivFrame(new String(arrayOfByte, 0, paramInt, "ISO-8859-1"), copyOfRangeIfValid(arrayOfByte, paramInt + 1, arrayOfByte.length));
  }
  
  private static TextInformationFrame decodeTextInformationFrame(ParsableByteArray paramParsableByteArray, int paramInt, String paramString)
    throws UnsupportedEncodingException
  {
    byte[] arrayOfByte = null;
    if (paramInt < 1) {}
    int i;
    String str;
    for (paramParsableByteArray = arrayOfByte;; paramParsableByteArray = new TextInformationFrame(paramString, null, new String(arrayOfByte, 0, indexOfEos(arrayOfByte, 0, i), str)))
    {
      return paramParsableByteArray;
      i = paramParsableByteArray.readUnsignedByte();
      str = getCharsetName(i);
      arrayOfByte = new byte[paramInt - 1];
      paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt - 1);
    }
  }
  
  private static TextInformationFrame decodeTxxxFrame(ParsableByteArray paramParsableByteArray, int paramInt)
    throws UnsupportedEncodingException
  {
    if (paramInt < 1)
    {
      paramParsableByteArray = null;
      return paramParsableByteArray;
    }
    int i = paramParsableByteArray.readUnsignedByte();
    String str1 = getCharsetName(i);
    byte[] arrayOfByte = new byte[paramInt - 1];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt - 1);
    paramInt = indexOfEos(arrayOfByte, 0, i);
    String str2 = new String(arrayOfByte, 0, paramInt, str1);
    paramInt += delimiterLength(i);
    if (paramInt < arrayOfByte.length) {}
    for (paramParsableByteArray = new String(arrayOfByte, paramInt, indexOfEos(arrayOfByte, paramInt, i) - paramInt, str1);; paramParsableByteArray = "")
    {
      paramParsableByteArray = new TextInformationFrame("TXXX", str2, paramParsableByteArray);
      break;
    }
  }
  
  private static UrlLinkFrame decodeUrlLinkFrame(ParsableByteArray paramParsableByteArray, int paramInt, String paramString)
    throws UnsupportedEncodingException
  {
    byte[] arrayOfByte = new byte[paramInt];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt);
    return new UrlLinkFrame(paramString, null, new String(arrayOfByte, 0, indexOfZeroByte(arrayOfByte, 0), "ISO-8859-1"));
  }
  
  private static UrlLinkFrame decodeWxxxFrame(ParsableByteArray paramParsableByteArray, int paramInt)
    throws UnsupportedEncodingException
  {
    if (paramInt < 1)
    {
      paramParsableByteArray = null;
      return paramParsableByteArray;
    }
    int i = paramParsableByteArray.readUnsignedByte();
    String str = getCharsetName(i);
    byte[] arrayOfByte = new byte[paramInt - 1];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt - 1);
    paramInt = indexOfEos(arrayOfByte, 0, i);
    str = new String(arrayOfByte, 0, paramInt, str);
    paramInt += delimiterLength(i);
    if (paramInt < arrayOfByte.length) {}
    for (paramParsableByteArray = new String(arrayOfByte, paramInt, indexOfZeroByte(arrayOfByte, paramInt) - paramInt, "ISO-8859-1");; paramParsableByteArray = "")
    {
      paramParsableByteArray = new UrlLinkFrame("WXXX", str, paramParsableByteArray);
      break;
    }
  }
  
  private static int delimiterLength(int paramInt)
  {
    if ((paramInt == 0) || (paramInt == 3)) {}
    for (paramInt = 1;; paramInt = 2) {
      return paramInt;
    }
  }
  
  private static String getCharsetName(int paramInt)
  {
    String str;
    switch (paramInt)
    {
    default: 
      str = "ISO-8859-1";
    }
    for (;;)
    {
      return str;
      str = "ISO-8859-1";
      continue;
      str = "UTF-16";
      continue;
      str = "UTF-16BE";
      continue;
      str = "UTF-8";
    }
  }
  
  private static String getFrameId(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    if (paramInt1 == 2) {}
    for (String str = String.format(Locale.US, "%c%c%c", new Object[] { Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4) });; str = String.format(Locale.US, "%c%c%c%c", new Object[] { Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4), Integer.valueOf(paramInt5) })) {
      return str;
    }
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
      paramInt1 = i;
    }
    for (;;)
    {
      return paramInt1;
      do
      {
        paramInt1 = indexOfZeroByte(paramArrayOfByte, paramInt1 + 1);
        if (paramInt1 >= paramArrayOfByte.length - 1) {
          break;
        }
      } while ((paramInt1 % 2 != 0) || (paramArrayOfByte[(paramInt1 + 1)] != 0));
      continue;
      paramInt1 = paramArrayOfByte.length;
    }
  }
  
  private static int indexOfZeroByte(byte[] paramArrayOfByte, int paramInt)
  {
    if (paramInt < paramArrayOfByte.length) {
      if (paramArrayOfByte[paramInt] != 0) {}
    }
    for (;;)
    {
      return paramInt;
      paramInt++;
      break;
      paramInt = paramArrayOfByte.length;
    }
  }
  
  private static int removeUnsynchronization(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    byte[] arrayOfByte = paramParsableByteArray.data;
    int i = paramParsableByteArray.getPosition();
    for (int j = paramInt; i + 1 < j; j = paramInt)
    {
      paramInt = j;
      if ((arrayOfByte[i] & 0xFF) == 255)
      {
        paramInt = j;
        if (arrayOfByte[(i + 1)] == 0)
        {
          System.arraycopy(arrayOfByte, i + 2, arrayOfByte, i + 1, j - i - 2);
          paramInt = j - 1;
        }
      }
      i++;
    }
    return j;
  }
  
  private static boolean validateFrames(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = paramParsableByteArray.getPosition();
    for (;;)
    {
      try
      {
        if (paramParsableByteArray.bytesLeft() >= paramInt2)
        {
          long l1;
          int k;
          if (paramInt1 >= 3)
          {
            j = paramParsableByteArray.readInt();
            l1 = paramParsableByteArray.readUnsignedInt();
            k = paramParsableByteArray.readUnsignedShort();
            if ((j == 0) && (l1 == 0L) && (k == 0))
            {
              paramBoolean = true;
              return paramBoolean;
            }
          }
          else
          {
            j = paramParsableByteArray.readUnsignedInt24();
            m = paramParsableByteArray.readUnsignedInt24();
            l1 = m;
            k = 0;
            continue;
          }
          long l2 = l1;
          if (paramInt1 == 4)
          {
            l2 = l1;
            if (!paramBoolean)
            {
              if ((0x808080 & l1) != 0L)
              {
                paramBoolean = false;
                paramParsableByteArray.setPosition(i);
                continue;
              }
              l2 = 0xFF & l1 | (l1 >> 8 & 0xFF) << 7 | (l1 >> 16 & 0xFF) << 14 | (l1 >> 24 & 0xFF) << 21;
            }
          }
          int m = 0;
          int j = 0;
          if (paramInt1 == 4)
          {
            if ((k & 0x40) != 0)
            {
              m = 1;
              if ((k & 0x1) != 0)
              {
                j = 1;
                k = 0;
                if (m != 0) {
                  k = 0 + 1;
                }
                m = k;
                if (j != 0) {
                  m = k + 4;
                }
                if (l2 >= m) {
                  continue;
                }
                paramBoolean = false;
                paramParsableByteArray.setPosition(i);
              }
            }
            else
            {
              m = 0;
              continue;
            }
            j = 0;
            continue;
          }
          else
          {
            if (paramInt1 != 3) {
              continue;
            }
            if ((k & 0x20) != 0)
            {
              m = 1;
              if ((k & 0x80) != 0) {
                j = 1;
              }
            }
            else
            {
              m = 0;
              continue;
            }
            j = 0;
            continue;
          }
          j = paramParsableByteArray.bytesLeft();
          if (j < l2)
          {
            paramBoolean = false;
            paramParsableByteArray.setPosition(i);
            continue;
          }
          j = (int)l2;
          paramParsableByteArray.skipBytes(j);
          continue;
        }
        paramBoolean = true;
      }
      finally
      {
        paramParsableByteArray.setPosition(i);
      }
      paramParsableByteArray.setPosition(i);
    }
  }
  
  public Metadata decode(MetadataInputBuffer paramMetadataInputBuffer)
  {
    paramMetadataInputBuffer = paramMetadataInputBuffer.data;
    return decode(paramMetadataInputBuffer.array(), paramMetadataInputBuffer.limit());
  }
  
  public Metadata decode(byte[] paramArrayOfByte, int paramInt)
  {
    Id3Frame localId3Frame = null;
    ArrayList localArrayList = new ArrayList();
    paramArrayOfByte = new ParsableByteArray(paramArrayOfByte, paramInt);
    Id3Header localId3Header = decodeHeader(paramArrayOfByte);
    if (localId3Header == null) {
      paramArrayOfByte = localId3Frame;
    }
    for (;;)
    {
      return paramArrayOfByte;
      int i = paramArrayOfByte.getPosition();
      if (localId3Header.majorVersion == 2) {}
      for (paramInt = 6;; paramInt = 10)
      {
        int j = localId3Header.framesSize;
        if (localId3Header.isUnsynchronized) {
          j = removeUnsynchronization(paramArrayOfByte, localId3Header.framesSize);
        }
        paramArrayOfByte.setLimit(i + j);
        boolean bool = false;
        if (!validateFrames(paramArrayOfByte, localId3Header.majorVersion, paramInt, false))
        {
          if ((localId3Header.majorVersion != 4) || (!validateFrames(paramArrayOfByte, 4, paramInt, true))) {
            break;
          }
          bool = true;
        }
        while (paramArrayOfByte.bytesLeft() >= paramInt)
        {
          localId3Frame = decodeFrame(localId3Header.majorVersion, paramArrayOfByte, bool, paramInt, this.framePredicate);
          if (localId3Frame != null) {
            localArrayList.add(localId3Frame);
          }
        }
      }
      Log.w("Id3Decoder", "Failed to validate ID3 tag with majorVersion=" + localId3Header.majorVersion);
      paramArrayOfByte = localId3Frame;
      continue;
      paramArrayOfByte = new Metadata(localArrayList);
    }
  }
  
  public static abstract interface FramePredicate
  {
    public abstract boolean evaluate(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  }
  
  private static final class Id3Header
  {
    private final int framesSize;
    private final boolean isUnsynchronized;
    private final int majorVersion;
    
    public Id3Header(int paramInt1, boolean paramBoolean, int paramInt2)
    {
      this.majorVersion = paramInt1;
      this.isUnsynchronized = paramBoolean;
      this.framesSize = paramInt2;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/metadata/id3/Id3Decoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */