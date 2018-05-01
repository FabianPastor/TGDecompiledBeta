package org.telegram.messenger.exoplayer2.extractor.mp4;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

final class Sniffer
{
  private static final int[] COMPATIBLE_BRANDS = { Util.getIntegerCodeForString("isom"), Util.getIntegerCodeForString("iso2"), Util.getIntegerCodeForString("iso3"), Util.getIntegerCodeForString("iso4"), Util.getIntegerCodeForString("iso5"), Util.getIntegerCodeForString("iso6"), Util.getIntegerCodeForString("avc1"), Util.getIntegerCodeForString("hvc1"), Util.getIntegerCodeForString("hev1"), Util.getIntegerCodeForString("mp41"), Util.getIntegerCodeForString("mp42"), Util.getIntegerCodeForString("3g2a"), Util.getIntegerCodeForString("3g2b"), Util.getIntegerCodeForString("3gr6"), Util.getIntegerCodeForString("3gs6"), Util.getIntegerCodeForString("3ge6"), Util.getIntegerCodeForString("3gg6"), Util.getIntegerCodeForString("M4V "), Util.getIntegerCodeForString("M4A "), Util.getIntegerCodeForString("f4v "), Util.getIntegerCodeForString("kddi"), Util.getIntegerCodeForString("M4VP"), Util.getIntegerCodeForString("qt  "), Util.getIntegerCodeForString("MSNV") };
  private static final int SEARCH_LENGTH = 4096;
  
  private static boolean isCompatibleBrand(int paramInt)
  {
    boolean bool1 = true;
    boolean bool2;
    if (paramInt >>> 8 == Util.getIntegerCodeForString("3gp")) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      int[] arrayOfInt = COMPATIBLE_BRANDS;
      int i = arrayOfInt.length;
      for (int j = 0;; j++)
      {
        if (j >= i) {
          break label52;
        }
        bool2 = bool1;
        if (arrayOfInt[j] == paramInt) {
          break;
        }
      }
      label52:
      bool2 = false;
    }
  }
  
  public static boolean sniffFragmented(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    return sniffInternal(paramExtractorInput, true);
  }
  
  private static boolean sniffInternal(ExtractorInput paramExtractorInput, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    long l1 = paramExtractorInput.getLength();
    long l2;
    if (l1 != -1L)
    {
      l2 = l1;
      if (l1 <= 4096L) {}
    }
    else
    {
      l2 = 4096L;
    }
    int i = (int)l2;
    ParsableByteArray localParsableByteArray = new ParsableByteArray(64);
    int j = 0;
    int k = 0;
    boolean bool1 = false;
    boolean bool2 = bool1;
    int m;
    int n;
    int i1;
    if (j < i)
    {
      m = 8;
      localParsableByteArray.reset(8);
      paramExtractorInput.peekFully(localParsableByteArray.data, 0, 8);
      l1 = localParsableByteArray.readUnsignedInt();
      n = localParsableByteArray.readInt();
      if (l1 == 1L)
      {
        i1 = 16;
        paramExtractorInput.peekFully(localParsableByteArray.data, 8, 8);
        localParsableByteArray.setLimit(16);
        l2 = localParsableByteArray.readUnsignedLongToLong();
        label144:
        if (l2 >= i1) {
          break label216;
        }
        paramBoolean = false;
      }
    }
    for (;;)
    {
      label155:
      return paramBoolean;
      l2 = l1;
      i1 = m;
      if (l1 != 0L) {
        break label144;
      }
      long l3 = paramExtractorInput.getLength();
      l2 = l1;
      i1 = m;
      if (l3 == -1L) {
        break label144;
      }
      l2 = l3 - paramExtractorInput.getPosition() + 8;
      i1 = m;
      break label144;
      label216:
      m = j + i1;
      j = m;
      if (n == Atom.TYPE_moov) {
        break;
      }
      if ((n == Atom.TYPE_moof) || (n == Atom.TYPE_mvex)) {
        bool2 = true;
      }
      for (;;)
      {
        if ((k != 0) && (paramBoolean == bool2))
        {
          paramBoolean = true;
          break label155;
          bool2 = bool1;
          if (m + l2 - i1 < i)
          {
            i1 = (int)(l2 - i1);
            m += i1;
            if (n == Atom.TYPE_ftyp)
            {
              if (i1 < 8)
              {
                paramBoolean = false;
                break label155;
              }
              localParsableByteArray.reset(i1);
              paramExtractorInput.peekFully(localParsableByteArray.data, 0, i1);
              n = i1 / 4;
              j = 0;
              i1 = k;
              if (j < n)
              {
                if (j == 1) {
                  localParsableByteArray.skipBytes(4);
                }
                while (!isCompatibleBrand(localParsableByteArray.readInt()))
                {
                  j++;
                  break;
                }
                i1 = 1;
              }
              j = m;
              k = i1;
              if (i1 != 0) {
                break;
              }
              paramBoolean = false;
              break label155;
            }
            j = m;
            if (i1 == 0) {
              break;
            }
            paramExtractorInput.advancePeekPosition(i1);
            j = m;
            break;
          }
        }
      }
      paramBoolean = false;
    }
  }
  
  public static boolean sniffUnfragmented(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    return sniffInternal(paramExtractorInput, false);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/mp4/Sniffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */