package org.telegram.messenger.exoplayer.extractor.mp4;

import android.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.extractor.GaplessInfo;
import org.telegram.messenger.exoplayer.util.Ac3Util;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.CodecSpecificDataUtil;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.exoplayer.util.NalUnitUtil.SpsData;
import org.telegram.messenger.exoplayer.util.ParsableBitArray;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.exoplayer.util.Util;

final class AtomParsers
{
  private static int findEsdsPosition(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2)
  {
    int i = paramParsableByteArray.getPosition();
    while (i - paramInt1 < paramInt2)
    {
      paramParsableByteArray.setPosition(i);
      int j = paramParsableByteArray.readInt();
      if (j > 0) {}
      for (boolean bool = true;; bool = false)
      {
        Assertions.checkArgument(bool, "childAtomSize should be positive");
        if (paramParsableByteArray.readInt() != Atom.TYPE_esds) {
          break;
        }
        return i;
      }
      i += j;
    }
    return -1;
  }
  
  private static void parseAudioSampleEntry(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong, String paramString, boolean paramBoolean, StsdData paramStsdData, int paramInt5)
  {
    paramParsableByteArray.setPosition(paramInt2 + 8);
    int k = 0;
    int m;
    int n;
    int i;
    int j;
    label91:
    Object localObject1;
    label140:
    Object localObject2;
    label146:
    boolean bool;
    label172:
    label217:
    Object localObject3;
    Object localObject4;
    if (paramBoolean)
    {
      paramParsableByteArray.skipBytes(8);
      k = paramParsableByteArray.readUnsignedShort();
      paramParsableByteArray.skipBytes(6);
      if ((k != 0) && (k != 1)) {
        break label367;
      }
      m = paramParsableByteArray.readUnsignedShort();
      paramParsableByteArray.skipBytes(6);
      n = paramParsableByteArray.readUnsignedFixedPoint1616();
      i = m;
      j = n;
      if (k == 1)
      {
        paramParsableByteArray.skipBytes(16);
        j = n;
        i = m;
      }
      m = paramParsableByteArray.getPosition();
      k = paramInt1;
      if (paramInt1 == Atom.TYPE_enca)
      {
        k = parseSampleEntryEncryptionData(paramParsableByteArray, paramInt2, paramInt3, paramStsdData, paramInt5);
        paramParsableByteArray.setPosition(m);
      }
      localObject1 = null;
      if (k != Atom.TYPE_ac_3) {
        break label404;
      }
      localObject1 = "audio/ac3";
      localObject2 = null;
      paramInt1 = m;
      if (paramInt1 - paramInt2 >= paramInt3) {
        break label718;
      }
      paramParsableByteArray.setPosition(paramInt1);
      n = paramParsableByteArray.readInt();
      if (n <= 0) {
        break label525;
      }
      bool = true;
      Assertions.checkArgument(bool, "childAtomSize should be positive");
      m = paramParsableByteArray.readInt();
      if ((m != Atom.TYPE_esds) && ((!paramBoolean) || (m != Atom.TYPE_wave))) {
        break label543;
      }
      if (m != Atom.TYPE_esds) {
        break label531;
      }
      m = paramInt1;
      localObject3 = localObject1;
      k = i;
      paramInt5 = j;
      localObject4 = localObject2;
      if (m != -1)
      {
        localObject2 = parseEsdsFromParent(paramParsableByteArray, m);
        localObject1 = (String)((Pair)localObject2).first;
        localObject2 = (byte[])((Pair)localObject2).second;
        localObject3 = localObject1;
        k = i;
        paramInt5 = j;
        localObject4 = localObject2;
        if ("audio/mp4a-latm".equals(localObject1))
        {
          localObject3 = CodecSpecificDataUtil.parseAacAudioSpecificConfig((byte[])localObject2);
          paramInt5 = ((Integer)((Pair)localObject3).first).intValue();
          k = ((Integer)((Pair)localObject3).second).intValue();
          localObject4 = localObject2;
          localObject3 = localObject1;
        }
      }
    }
    for (;;)
    {
      paramInt1 += n;
      localObject1 = localObject3;
      i = k;
      j = paramInt5;
      localObject2 = localObject4;
      break label146;
      paramParsableByteArray.skipBytes(16);
      break;
      label367:
      if (k != 2) {
        break label781;
      }
      paramParsableByteArray.skipBytes(16);
      j = (int)Math.round(paramParsableByteArray.readDouble());
      i = paramParsableByteArray.readUnsignedIntToInt();
      paramParsableByteArray.skipBytes(20);
      break label91;
      label404:
      if (k == Atom.TYPE_ec_3)
      {
        localObject1 = "audio/eac3";
        break label140;
      }
      if (k == Atom.TYPE_dtsc)
      {
        localObject1 = "audio/vnd.dts";
        break label140;
      }
      if ((k == Atom.TYPE_dtsh) || (k == Atom.TYPE_dtsl))
      {
        localObject1 = "audio/vnd.dts.hd";
        break label140;
      }
      if (k == Atom.TYPE_dtse)
      {
        localObject1 = "audio/vnd.dts.hd;profile=lbr";
        break label140;
      }
      if (k == Atom.TYPE_samr)
      {
        localObject1 = "audio/3gpp";
        break label140;
      }
      if (k == Atom.TYPE_sawb)
      {
        localObject1 = "audio/amr-wb";
        break label140;
      }
      if ((k != Atom.TYPE_lpcm) && (k != Atom.TYPE_sowt)) {
        break label140;
      }
      localObject1 = "audio/raw";
      break label140;
      label525:
      bool = false;
      break label172;
      label531:
      m = findEsdsPosition(paramParsableByteArray, paramInt1, n);
      break label217;
      label543:
      if (m == Atom.TYPE_dac3)
      {
        paramParsableByteArray.setPosition(paramInt1 + 8);
        paramStsdData.mediaFormat = Ac3Util.parseAc3AnnexFFormat(paramParsableByteArray, Integer.toString(paramInt4), paramLong, paramString);
        localObject3 = localObject1;
        k = i;
        paramInt5 = j;
        localObject4 = localObject2;
      }
      else if (m == Atom.TYPE_dec3)
      {
        paramParsableByteArray.setPosition(paramInt1 + 8);
        paramStsdData.mediaFormat = Ac3Util.parseEAc3AnnexFFormat(paramParsableByteArray, Integer.toString(paramInt4), paramLong, paramString);
        localObject3 = localObject1;
        k = i;
        paramInt5 = j;
        localObject4 = localObject2;
      }
      else
      {
        localObject3 = localObject1;
        k = i;
        paramInt5 = j;
        localObject4 = localObject2;
        if (m == Atom.TYPE_ddts)
        {
          paramStsdData.mediaFormat = MediaFormat.createAudioFormat(Integer.toString(paramInt4), (String)localObject1, -1, -1, paramLong, i, j, null, paramString);
          localObject3 = localObject1;
          k = i;
          paramInt5 = j;
          localObject4 = localObject2;
        }
      }
    }
    label718:
    if ((paramStsdData.mediaFormat == null) && (localObject1 != null))
    {
      if (!"audio/raw".equals(localObject1)) {
        break label782;
      }
      paramInt1 = 2;
      localObject3 = Integer.toString(paramInt4);
      if (localObject2 != null) {
        break label787;
      }
    }
    label781:
    label782:
    label787:
    for (paramParsableByteArray = null;; paramParsableByteArray = Collections.singletonList(localObject2))
    {
      paramStsdData.mediaFormat = MediaFormat.createAudioFormat((String)localObject3, (String)localObject1, -1, -1, paramLong, i, j, paramParsableByteArray, paramString, paramInt1);
      return;
      paramInt1 = -1;
      break;
    }
  }
  
  private static AvcCData parseAvcCFromParent(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    paramParsableByteArray.setPosition(paramInt + 8 + 4);
    int i = (paramParsableByteArray.readUnsignedByte() & 0x3) + 1;
    if (i == 3) {
      throw new IllegalStateException();
    }
    ArrayList localArrayList = new ArrayList();
    float f = 1.0F;
    int j = paramParsableByteArray.readUnsignedByte() & 0x1F;
    paramInt = 0;
    while (paramInt < j)
    {
      localArrayList.add(NalUnitUtil.parseChildNalUnit(paramParsableByteArray));
      paramInt += 1;
    }
    int k = paramParsableByteArray.readUnsignedByte();
    paramInt = 0;
    while (paramInt < k)
    {
      localArrayList.add(NalUnitUtil.parseChildNalUnit(paramParsableByteArray));
      paramInt += 1;
    }
    if (j > 0)
    {
      paramParsableByteArray = new ParsableBitArray((byte[])localArrayList.get(0));
      paramParsableByteArray.setPosition((i + 1) * 8);
      f = NalUnitUtil.parseSpsNalUnit(paramParsableByteArray).pixelWidthAspectRatio;
    }
    return new AvcCData(localArrayList, i, f);
  }
  
  private static Pair<long[], long[]> parseEdts(Atom.ContainerAtom paramContainerAtom)
  {
    if (paramContainerAtom != null)
    {
      paramContainerAtom = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_elst);
      if (paramContainerAtom != null) {}
    }
    else
    {
      return Pair.create(null, null);
    }
    paramContainerAtom = paramContainerAtom.data;
    paramContainerAtom.setPosition(8);
    int j = Atom.parseFullAtomVersion(paramContainerAtom.readInt());
    int k = paramContainerAtom.readUnsignedIntToInt();
    long[] arrayOfLong1 = new long[k];
    long[] arrayOfLong2 = new long[k];
    int i = 0;
    while (i < k)
    {
      if (j == 1)
      {
        l = paramContainerAtom.readUnsignedLongToLong();
        arrayOfLong1[i] = l;
        if (j != 1) {
          break label125;
        }
      }
      label125:
      for (long l = paramContainerAtom.readLong();; l = paramContainerAtom.readInt())
      {
        arrayOfLong2[i] = l;
        if (paramContainerAtom.readShort() == 1) {
          break label135;
        }
        throw new IllegalArgumentException("Unsupported media rate.");
        l = paramContainerAtom.readUnsignedInt();
        break;
      }
      label135:
      paramContainerAtom.skipBytes(2);
      i += 1;
    }
    return Pair.create(arrayOfLong1, arrayOfLong2);
  }
  
  private static Pair<String, byte[]> parseEsdsFromParent(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    paramParsableByteArray.setPosition(paramInt + 8 + 4);
    paramParsableByteArray.skipBytes(1);
    parseExpandableClassSize(paramParsableByteArray);
    paramParsableByteArray.skipBytes(2);
    paramInt = paramParsableByteArray.readUnsignedByte();
    if ((paramInt & 0x80) != 0) {
      paramParsableByteArray.skipBytes(2);
    }
    if ((paramInt & 0x40) != 0) {
      paramParsableByteArray.skipBytes(paramParsableByteArray.readUnsignedShort());
    }
    if ((paramInt & 0x20) != 0) {
      paramParsableByteArray.skipBytes(2);
    }
    paramParsableByteArray.skipBytes(1);
    parseExpandableClassSize(paramParsableByteArray);
    Object localObject;
    switch (paramParsableByteArray.readUnsignedByte())
    {
    default: 
      localObject = null;
    case 107: 
    case 32: 
    case 33: 
    case 35: 
    case 64: 
    case 102: 
    case 103: 
    case 104: 
    case 165: 
    case 166: 
      for (;;)
      {
        paramParsableByteArray.skipBytes(12);
        paramParsableByteArray.skipBytes(1);
        paramInt = parseExpandableClassSize(paramParsableByteArray);
        byte[] arrayOfByte = new byte[paramInt];
        paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt);
        return Pair.create(localObject, arrayOfByte);
        return Pair.create("audio/mpeg", null);
        localObject = "video/mp4v-es";
        continue;
        localObject = "video/avc";
        continue;
        localObject = "video/hevc";
        continue;
        localObject = "audio/mp4a-latm";
        continue;
        localObject = "audio/ac3";
        continue;
        localObject = "audio/eac3";
      }
    case 169: 
    case 172: 
      return Pair.create("audio/vnd.dts", null);
    }
    return Pair.create("audio/vnd.dts.hd", null);
  }
  
  private static int parseExpandableClassSize(ParsableByteArray paramParsableByteArray)
  {
    int j = paramParsableByteArray.readUnsignedByte();
    for (int i = j & 0x7F; (j & 0x80) == 128; i = i << 7 | j & 0x7F) {
      j = paramParsableByteArray.readUnsignedByte();
    }
    return i;
  }
  
  private static int parseHdlr(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.setPosition(16);
    return paramParsableByteArray.readInt();
  }
  
  private static Pair<List<byte[]>, Integer> parseHvcCFromParent(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    paramParsableByteArray.setPosition(paramInt + 8 + 21);
    int m = paramParsableByteArray.readUnsignedByte();
    int n = paramParsableByteArray.readUnsignedByte();
    paramInt = 0;
    int k = paramParsableByteArray.getPosition();
    int i = 0;
    int i1;
    int j;
    int i2;
    while (i < n)
    {
      paramParsableByteArray.skipBytes(1);
      i1 = paramParsableByteArray.readUnsignedShort();
      j = 0;
      while (j < i1)
      {
        i2 = paramParsableByteArray.readUnsignedShort();
        paramInt += i2 + 4;
        paramParsableByteArray.skipBytes(i2);
        j += 1;
      }
      i += 1;
    }
    paramParsableByteArray.setPosition(k);
    byte[] arrayOfByte = new byte[paramInt];
    k = 0;
    i = 0;
    while (i < n)
    {
      paramParsableByteArray.skipBytes(1);
      i1 = paramParsableByteArray.readUnsignedShort();
      j = 0;
      while (j < i1)
      {
        i2 = paramParsableByteArray.readUnsignedShort();
        System.arraycopy(NalUnitUtil.NAL_START_CODE, 0, arrayOfByte, k, NalUnitUtil.NAL_START_CODE.length);
        k += NalUnitUtil.NAL_START_CODE.length;
        System.arraycopy(paramParsableByteArray.data, paramParsableByteArray.getPosition(), arrayOfByte, k, i2);
        k += i2;
        paramParsableByteArray.skipBytes(i2);
        j += 1;
      }
      i += 1;
    }
    if (paramInt == 0) {}
    for (paramParsableByteArray = null;; paramParsableByteArray = Collections.singletonList(arrayOfByte)) {
      return Pair.create(paramParsableByteArray, Integer.valueOf((m & 0x3) + 1));
    }
  }
  
  private static GaplessInfo parseIlst(ParsableByteArray paramParsableByteArray)
  {
    while (paramParsableByteArray.bytesLeft() > 0)
    {
      int i = paramParsableByteArray.getPosition() + paramParsableByteArray.readInt();
      if (paramParsableByteArray.readInt() == Atom.TYPE_DASHES)
      {
        String str2 = null;
        String str1 = null;
        String str3 = null;
        while (paramParsableByteArray.getPosition() < i)
        {
          int j = paramParsableByteArray.readInt() - 12;
          int k = paramParsableByteArray.readInt();
          paramParsableByteArray.skipBytes(4);
          if (k == Atom.TYPE_mean)
          {
            str2 = paramParsableByteArray.readString(j);
          }
          else if (k == Atom.TYPE_name)
          {
            str1 = paramParsableByteArray.readString(j);
          }
          else if (k == Atom.TYPE_data)
          {
            paramParsableByteArray.skipBytes(4);
            str3 = paramParsableByteArray.readString(j - 4);
          }
          else
          {
            paramParsableByteArray.skipBytes(j);
          }
        }
        if ((str1 != null) && (str3 != null) && ("com.apple.iTunes".equals(str2))) {
          return GaplessInfo.createFromComment(str1, str3);
        }
      }
      else
      {
        paramParsableByteArray.setPosition(i);
      }
    }
    return null;
  }
  
  private static Pair<Long, String> parseMdhd(ParsableByteArray paramParsableByteArray)
  {
    int j = 8;
    paramParsableByteArray.setPosition(8);
    int k = Atom.parseFullAtomVersion(paramParsableByteArray.readInt());
    if (k == 0) {}
    for (int i = 8;; i = 16)
    {
      paramParsableByteArray.skipBytes(i);
      long l = paramParsableByteArray.readUnsignedInt();
      i = j;
      if (k == 0) {
        i = 4;
      }
      paramParsableByteArray.skipBytes(i);
      i = paramParsableByteArray.readUnsignedShort();
      return Pair.create(Long.valueOf(l), "" + (char)((i >> 10 & 0x1F) + 96) + (char)((i >> 5 & 0x1F) + 96) + (char)((i & 0x1F) + 96));
    }
  }
  
  private static GaplessInfo parseMetaAtom(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.skipBytes(12);
    ParsableByteArray localParsableByteArray = new ParsableByteArray();
    while (paramParsableByteArray.bytesLeft() >= 8)
    {
      int i = paramParsableByteArray.readInt() - 8;
      if (paramParsableByteArray.readInt() == Atom.TYPE_ilst)
      {
        localParsableByteArray.reset(paramParsableByteArray.data, paramParsableByteArray.getPosition() + i);
        localParsableByteArray.setPosition(paramParsableByteArray.getPosition());
        GaplessInfo localGaplessInfo = parseIlst(localParsableByteArray);
        if (localGaplessInfo != null) {
          return localGaplessInfo;
        }
      }
      paramParsableByteArray.skipBytes(i);
    }
    return null;
  }
  
  private static long parseMvhd(ParsableByteArray paramParsableByteArray)
  {
    int i = 8;
    paramParsableByteArray.setPosition(8);
    if (Atom.parseFullAtomVersion(paramParsableByteArray.readInt()) == 0) {}
    for (;;)
    {
      paramParsableByteArray.skipBytes(i);
      return paramParsableByteArray.readUnsignedInt();
      i = 16;
    }
  }
  
  private static float parsePaspFromParent(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    paramParsableByteArray.setPosition(paramInt + 8);
    paramInt = paramParsableByteArray.readUnsignedIntToInt();
    int i = paramParsableByteArray.readUnsignedIntToInt();
    return paramInt / i;
  }
  
  private static int parseSampleEntryEncryptionData(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, StsdData paramStsdData, int paramInt3)
  {
    boolean bool2 = true;
    int k = 0;
    int i = paramParsableByteArray.getPosition();
    for (;;)
    {
      int j = k;
      Integer localInteger;
      if (i - paramInt1 < paramInt2)
      {
        paramParsableByteArray.setPosition(i);
        j = paramParsableByteArray.readInt();
        if (j <= 0) {
          break label120;
        }
        bool1 = true;
        Assertions.checkArgument(bool1, "childAtomSize should be positive");
        if (paramParsableByteArray.readInt() != Atom.TYPE_sinf) {
          break label132;
        }
        paramParsableByteArray = parseSinfFromParent(paramParsableByteArray, i, j);
        localInteger = (Integer)paramParsableByteArray.first;
        if (localInteger == null) {
          break label126;
        }
      }
      label120:
      label126:
      for (boolean bool1 = bool2;; bool1 = false)
      {
        Assertions.checkArgument(bool1, "frma atom is mandatory");
        paramStsdData.trackEncryptionBoxes[paramInt3] = ((TrackEncryptionBox)paramParsableByteArray.second);
        j = localInteger.intValue();
        return j;
        bool1 = false;
        break;
      }
      label132:
      i += j;
    }
  }
  
  private static TrackEncryptionBox parseSchiFromParent(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2)
  {
    boolean bool = true;
    int i = paramInt1 + 8;
    while (i - paramInt1 < paramInt2)
    {
      paramParsableByteArray.setPosition(i);
      int j = paramParsableByteArray.readInt();
      if (paramParsableByteArray.readInt() == Atom.TYPE_tenc)
      {
        paramParsableByteArray.skipBytes(6);
        if (paramParsableByteArray.readUnsignedByte() == 1) {}
        for (;;)
        {
          paramInt1 = paramParsableByteArray.readUnsignedByte();
          byte[] arrayOfByte = new byte[16];
          paramParsableByteArray.readBytes(arrayOfByte, 0, arrayOfByte.length);
          return new TrackEncryptionBox(bool, paramInt1, arrayOfByte);
          bool = false;
        }
      }
      i += j;
    }
    return null;
  }
  
  private static Pair<Integer, TrackEncryptionBox> parseSinfFromParent(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2)
  {
    int i = paramInt1 + 8;
    TrackEncryptionBox localTrackEncryptionBox = null;
    Object localObject1 = null;
    if (i - paramInt1 < paramInt2)
    {
      paramParsableByteArray.setPosition(i);
      int j = paramParsableByteArray.readInt();
      int k = paramParsableByteArray.readInt();
      Object localObject2;
      if (k == Atom.TYPE_frma) {
        localObject2 = Integer.valueOf(paramParsableByteArray.readInt());
      }
      for (;;)
      {
        i += j;
        localObject1 = localObject2;
        break;
        if (k == Atom.TYPE_schm)
        {
          paramParsableByteArray.skipBytes(4);
          paramParsableByteArray.readInt();
          paramParsableByteArray.readInt();
          localObject2 = localObject1;
        }
        else
        {
          localObject2 = localObject1;
          if (k == Atom.TYPE_schi)
          {
            localTrackEncryptionBox = parseSchiFromParent(paramParsableByteArray, i, j);
            localObject2 = localObject1;
          }
        }
      }
    }
    return Pair.create(localObject1, localTrackEncryptionBox);
  }
  
  public static TrackSampleTable parseStbl(Track paramTrack, Atom.ContainerAtom paramContainerAtom)
    throws ParserException
  {
    Object localObject1 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_stsz);
    if (localObject1 != null) {}
    int i13;
    for (Object localObject2 = new StszSampleSizeBox((Atom.LeafAtom)localObject1);; localObject2 = new Stz2SampleSizeBox((Atom.LeafAtom)localObject1))
    {
      i13 = ((SampleSizeBox)localObject2).getSampleCount();
      if (i13 != 0) {
        break;
      }
      return new TrackSampleTable(new long[0], new int[0], 0, new long[0], new int[0]);
      localObject1 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_stz2);
      if (localObject1 == null) {
        throw new ParserException("Track has no sample table size information");
      }
    }
    boolean bool = false;
    Object localObject3 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_stco);
    localObject1 = localObject3;
    if (localObject3 == null)
    {
      bool = true;
      localObject1 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_co64);
    }
    localObject3 = ((Atom.LeafAtom)localObject1).data;
    Object localObject4 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_stsc).data;
    Object localObject7 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_stts).data;
    localObject1 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_stss);
    label201:
    ChunkIterator localChunkIterator;
    int i1;
    int i4;
    int i2;
    int i3;
    if (localObject1 != null)
    {
      localObject1 = ((Atom.LeafAtom)localObject1).data;
      paramContainerAtom = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_ctts);
      if (paramContainerAtom == null) {
        break label473;
      }
      paramContainerAtom = paramContainerAtom.data;
      localChunkIterator = new ChunkIterator((ParsableByteArray)localObject4, (ParsableByteArray)localObject3, bool);
      ((ParsableByteArray)localObject7).setPosition(12);
      i1 = ((ParsableByteArray)localObject7).readUnsignedIntToInt() - 1;
      i4 = ((ParsableByteArray)localObject7).readUnsignedIntToInt();
      i2 = ((ParsableByteArray)localObject7).readUnsignedIntToInt();
      i3 = 0;
      i = 0;
      n = 0;
      if (paramContainerAtom != null)
      {
        paramContainerAtom.setPosition(12);
        i = paramContainerAtom.readUnsignedIntToInt();
      }
      m = -1;
      k = 0;
      j = m;
      localObject3 = localObject1;
      if (localObject1 != null)
      {
        ((ParsableByteArray)localObject1).setPosition(12);
        k = ((ParsableByteArray)localObject1).readUnsignedIntToInt();
        if (k <= 0) {
          break label478;
        }
        j = ((ParsableByteArray)localObject1).readUnsignedIntToInt() - 1;
        localObject3 = localObject1;
      }
      label318:
      if ((!((SampleSizeBox)localObject2).isFixedSampleSize()) || (!"audio/raw".equals(paramTrack.mediaFormat.mimeType)) || (i1 != 0) || (i != 0) || (k != 0)) {
        break label487;
      }
    }
    Object localObject5;
    Object localObject6;
    long l2;
    long l1;
    int i6;
    int i5;
    label473:
    label478:
    label487:
    for (int m = 1;; m = 0)
    {
      i7 = 0;
      if (m != 0) {
        break label963;
      }
      localObject4 = new long[i13];
      localObject1 = new int[i13];
      localObject5 = new long[i13];
      localObject6 = new int[i13];
      l2 = 0L;
      l1 = 0L;
      i6 = 0;
      i8 = 0;
      m = n;
      i5 = k;
      n = i6;
      k = i3;
      i6 = j;
      i3 = i8;
      j = i7;
      if (i3 >= i13) {
        break label785;
      }
      while (n == 0)
      {
        Assertions.checkState(localChunkIterator.moveNext());
        l1 = localChunkIterator.offset;
        n = localChunkIterator.numSamples;
      }
      localObject1 = null;
      break;
      paramContainerAtom = null;
      break label201;
      localObject3 = null;
      j = m;
      break label318;
    }
    int i9 = k;
    int i8 = i;
    int i7 = m;
    if (paramContainerAtom != null)
    {
      while ((k == 0) && (i > 0))
      {
        k = paramContainerAtom.readUnsignedIntToInt();
        m = paramContainerAtom.readInt();
        i -= 1;
      }
      i9 = k - 1;
      i7 = m;
      i8 = i;
    }
    localObject4[i3] = l1;
    localObject1[i3] = ((SampleSizeBox)localObject2).readNextSampleSize();
    int k = j;
    if (localObject1[i3] > j) {
      k = localObject1[i3];
    }
    localObject5[i3] = (i7 + l2);
    if (localObject3 == null) {}
    for (int i = 1;; i = 0)
    {
      localObject6[i3] = i;
      int i11 = i6;
      m = i5;
      if (i3 == i6)
      {
        localObject6[i3] = 1;
        i = i5 - 1;
        i11 = i6;
        m = i;
        if (i > 0)
        {
          i11 = ((ParsableByteArray)localObject3).readUnsignedIntToInt() - 1;
          m = i;
        }
      }
      l2 += i2;
      j = i4 - 1;
      i = j;
      int i12 = i1;
      int i10 = i2;
      if (j == 0)
      {
        i = j;
        i12 = i1;
        i10 = i2;
        if (i1 > 0)
        {
          i = ((ParsableByteArray)localObject7).readUnsignedIntToInt();
          i10 = ((ParsableByteArray)localObject7).readUnsignedIntToInt();
          i12 = i1 - 1;
        }
      }
      l1 += localObject1[i3];
      n -= 1;
      i3 += 1;
      j = k;
      i6 = i11;
      i4 = i;
      k = i9;
      i5 = m;
      i1 = i12;
      i = i8;
      i2 = i10;
      m = i7;
      break;
    }
    label785:
    if (k == 0)
    {
      bool = true;
      Assertions.checkArgument(bool);
      label798:
      if (i <= 0) {
        break label841;
      }
      if (paramContainerAtom.readUnsignedIntToInt() != 0) {
        break label835;
      }
    }
    label835:
    for (bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      paramContainerAtom.readInt();
      i -= 1;
      break label798;
      bool = false;
      break;
    }
    label841:
    if (i5 == 0)
    {
      bool = true;
      Assertions.checkArgument(bool);
      if (i4 != 0) {
        break label945;
      }
      bool = true;
      label862:
      Assertions.checkArgument(bool);
      if (n != 0) {
        break label951;
      }
      bool = true;
      label875:
      Assertions.checkArgument(bool);
      if (i1 != 0) {
        break label957;
      }
      bool = true;
      label888:
      Assertions.checkArgument(bool);
      localObject2 = localObject6;
      localObject3 = localObject5;
      paramContainerAtom = (Atom.ContainerAtom)localObject4;
    }
    for (;;)
    {
      if (paramTrack.editListDurations != null) {
        break label1070;
      }
      Util.scaleLargeTimestampsInPlace((long[])localObject3, 1000000L, paramTrack.timescale);
      return new TrackSampleTable(paramContainerAtom, (int[])localObject1, j, (long[])localObject3, (int[])localObject2);
      bool = false;
      break;
      label945:
      bool = false;
      break label862;
      label951:
      bool = false;
      break label875;
      label957:
      bool = false;
      break label888;
      label963:
      paramContainerAtom = new long[localChunkIterator.length];
      localObject1 = new int[localChunkIterator.length];
      while (localChunkIterator.moveNext())
      {
        paramContainerAtom[localChunkIterator.index] = localChunkIterator.offset;
        localObject1[localChunkIterator.index] = localChunkIterator.numSamples;
      }
      localObject2 = FixedSampleSizeRechunker.rechunk(((SampleSizeBox)localObject2).readNextSampleSize(), paramContainerAtom, (int[])localObject1, i2);
      paramContainerAtom = ((FixedSampleSizeRechunker.Results)localObject2).offsets;
      localObject1 = ((FixedSampleSizeRechunker.Results)localObject2).sizes;
      j = ((FixedSampleSizeRechunker.Results)localObject2).maximumSize;
      localObject3 = ((FixedSampleSizeRechunker.Results)localObject2).timestamps;
      localObject2 = ((FixedSampleSizeRechunker.Results)localObject2).flags;
    }
    label1070:
    if ((paramTrack.editListDurations.length == 1) && (paramTrack.editListDurations[0] == 0L))
    {
      i = 0;
      while (i < localObject3.length)
      {
        localObject3[i] = Util.scaleLargeTimestamp(localObject3[i] - paramTrack.editListMediaTimes[0], 1000000L, paramTrack.timescale);
        i += 1;
      }
      return new TrackSampleTable(paramContainerAtom, (int[])localObject1, j, (long[])localObject3, (int[])localObject2);
    }
    k = 0;
    int n = 0;
    i = 0;
    m = 0;
    if (m < paramTrack.editListDurations.length)
    {
      l1 = paramTrack.editListMediaTimes[m];
      i3 = i;
      i2 = k;
      i1 = n;
      if (l1 != -1L)
      {
        l2 = Util.scaleLargeTimestamp(paramTrack.editListDurations[m], paramTrack.timescale, paramTrack.movieTimescale);
        i3 = Util.binarySearchCeil((long[])localObject3, l1, true, true);
        i1 = Util.binarySearchCeil((long[])localObject3, l1 + l2, true, false);
        i2 = k + (i1 - i3);
        if (n == i3) {
          break label1288;
        }
      }
      label1288:
      for (k = 1;; k = 0)
      {
        i3 = i | k;
        m += 1;
        i = i3;
        k = i2;
        n = i1;
        break;
      }
    }
    if (k != i13)
    {
      m = 1;
      i2 = i | m;
      if (i2 == 0) {
        break label1590;
      }
      localObject4 = new long[k];
      label1321:
      if (i2 == 0) {
        break label1596;
      }
      localObject5 = new int[k];
      label1332:
      if (i2 == 0) {
        break label1603;
      }
      i = 0;
      label1339:
      if (i2 == 0) {
        break label1608;
      }
      localObject6 = new int[k];
      label1350:
      localObject7 = new long[k];
      l1 = 0L;
      j = 0;
      k = 0;
    }
    for (;;)
    {
      if (k >= paramTrack.editListDurations.length) {
        break label1637;
      }
      l2 = paramTrack.editListMediaTimes[k];
      long l3 = paramTrack.editListDurations[k];
      n = i;
      i1 = j;
      if (l2 != -1L)
      {
        long l4 = Util.scaleLargeTimestamp(l3, paramTrack.timescale, paramTrack.movieTimescale);
        m = Util.binarySearchCeil((long[])localObject3, l2, true, true);
        i3 = Util.binarySearchCeil((long[])localObject3, l2 + l4, true, false);
        if (i2 != 0)
        {
          n = i3 - m;
          System.arraycopy(paramContainerAtom, m, localObject4, j, n);
          System.arraycopy(localObject1, m, localObject5, j, n);
          System.arraycopy(localObject2, m, localObject6, j, n);
        }
        for (;;)
        {
          n = i;
          i1 = j;
          if (m >= i3) {
            break;
          }
          localObject7[j] = (Util.scaleLargeTimestamp(l1, 1000000L, paramTrack.movieTimescale) + Util.scaleLargeTimestamp(localObject3[m] - l2, 1000000L, paramTrack.timescale));
          n = i;
          if (i2 != 0)
          {
            n = i;
            if (localObject5[j] > i) {
              n = localObject1[m];
            }
          }
          j += 1;
          m += 1;
          i = n;
        }
        m = 0;
        break;
        label1590:
        localObject4 = paramContainerAtom;
        break label1321;
        label1596:
        localObject5 = localObject1;
        break label1332;
        label1603:
        i = j;
        break label1339;
        label1608:
        localObject6 = localObject2;
        break label1350;
      }
      l1 += l3;
      k += 1;
      i = n;
      j = i1;
    }
    label1637:
    k = 0;
    int j = 0;
    if ((j < localObject6.length) && (k == 0))
    {
      if ((localObject6[j] & 0x1) != 0) {}
      for (m = 1;; m = 0)
      {
        k |= m;
        j += 1;
        break;
      }
    }
    if (k == 0) {
      throw new ParserException("The edited sample sequence does not contain a sync sample.");
    }
    return new TrackSampleTable((long[])localObject4, (int[])localObject5, i, (long[])localObject7, (int[])localObject6);
  }
  
  private static StsdData parseStsd(ParsableByteArray paramParsableByteArray, int paramInt1, long paramLong, int paramInt2, String paramString, boolean paramBoolean)
  {
    paramParsableByteArray.setPosition(12);
    int j = paramParsableByteArray.readInt();
    StsdData localStsdData = new StsdData(j);
    int i = 0;
    if (i < j)
    {
      int k = paramParsableByteArray.getPosition();
      int m = paramParsableByteArray.readInt();
      boolean bool;
      label53:
      int n;
      if (m > 0)
      {
        bool = true;
        Assertions.checkArgument(bool, "childAtomSize should be positive");
        n = paramParsableByteArray.readInt();
        if ((n != Atom.TYPE_avc1) && (n != Atom.TYPE_avc3) && (n != Atom.TYPE_encv) && (n != Atom.TYPE_mp4v) && (n != Atom.TYPE_hvc1) && (n != Atom.TYPE_hev1) && (n != Atom.TYPE_s263) && (n != Atom.TYPE_vp08) && (n != Atom.TYPE_vp09)) {
          break label180;
        }
        parseVideoSampleEntry(paramParsableByteArray, n, k, m, paramInt1, paramLong, paramInt2, localStsdData, i);
      }
      for (;;)
      {
        paramParsableByteArray.setPosition(k + m);
        i += 1;
        break;
        bool = false;
        break label53;
        label180:
        if ((n == Atom.TYPE_mp4a) || (n == Atom.TYPE_enca) || (n == Atom.TYPE_ac_3) || (n == Atom.TYPE_ec_3) || (n == Atom.TYPE_dtsc) || (n == Atom.TYPE_dtse) || (n == Atom.TYPE_dtsh) || (n == Atom.TYPE_dtsl) || (n == Atom.TYPE_samr) || (n == Atom.TYPE_sawb) || (n == Atom.TYPE_lpcm) || (n == Atom.TYPE_sowt)) {
          parseAudioSampleEntry(paramParsableByteArray, n, k, m, paramInt1, paramLong, paramString, paramBoolean, localStsdData, i);
        } else if (n == Atom.TYPE_TTML) {
          localStsdData.mediaFormat = MediaFormat.createTextFormat(Integer.toString(paramInt1), "application/ttml+xml", -1, paramLong, paramString);
        } else if (n == Atom.TYPE_tx3g) {
          localStsdData.mediaFormat = MediaFormat.createTextFormat(Integer.toString(paramInt1), "application/x-quicktime-tx3g", -1, paramLong, paramString);
        } else if (n == Atom.TYPE_wvtt) {
          localStsdData.mediaFormat = MediaFormat.createTextFormat(Integer.toString(paramInt1), "application/x-mp4vtt", -1, paramLong, paramString);
        } else if (n == Atom.TYPE_stpp) {
          localStsdData.mediaFormat = MediaFormat.createTextFormat(Integer.toString(paramInt1), "application/ttml+xml", -1, paramLong, paramString, 0L);
        }
      }
    }
    return localStsdData;
  }
  
  private static TkhdData parseTkhd(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.setPosition(8);
    int i1 = Atom.parseFullAtomVersion(paramParsableByteArray.readInt());
    int i;
    int n;
    int m;
    label55:
    int j;
    label57:
    int k;
    long l1;
    if (i1 == 0)
    {
      i = 8;
      paramParsableByteArray.skipBytes(i);
      n = paramParsableByteArray.readInt();
      paramParsableByteArray.skipBytes(4);
      m = 1;
      int i2 = paramParsableByteArray.getPosition();
      if (i1 != 0) {
        break label172;
      }
      i = 4;
      j = 0;
      k = m;
      if (j < i)
      {
        if (paramParsableByteArray.data[(i2 + j)] == -1) {
          break label178;
        }
        k = 0;
      }
      if (k == 0) {
        break label185;
      }
      paramParsableByteArray.skipBytes(i);
      l1 = -1L;
      paramParsableByteArray.skipBytes(16);
      i = paramParsableByteArray.readInt();
      j = paramParsableByteArray.readInt();
      paramParsableByteArray.skipBytes(4);
      k = paramParsableByteArray.readInt();
      m = paramParsableByteArray.readInt();
      if ((i != 0) || (j != 65536) || (k != -65536) || (m != 0)) {
        break label224;
      }
      i = 90;
    }
    for (;;)
    {
      return new TkhdData(n, l1, i);
      i = 16;
      break;
      label172:
      i = 8;
      break label55;
      label178:
      j += 1;
      break label57;
      label185:
      if (i1 == 0) {}
      for (long l2 = paramParsableByteArray.readUnsignedInt();; l2 = paramParsableByteArray.readUnsignedLongToLong())
      {
        l1 = l2;
        if (l2 != 0L) {
          break;
        }
        l1 = -1L;
        break;
      }
      label224:
      if ((i == 0) && (j == -65536) && (k == 65536) && (m == 0)) {
        i = 270;
      } else if ((i == -65536) && (j == 0) && (k == 0) && (m == -65536)) {
        i = 180;
      } else {
        i = 0;
      }
    }
  }
  
  public static Track parseTrak(Atom.ContainerAtom paramContainerAtom, Atom.LeafAtom paramLeafAtom, long paramLong, boolean paramBoolean)
  {
    Object localObject = paramContainerAtom.getContainerAtomOfType(Atom.TYPE_mdia);
    int i = parseHdlr(((Atom.ContainerAtom)localObject).getLeafAtomOfType(Atom.TYPE_hdlr).data);
    if ((i != Track.TYPE_soun) && (i != Track.TYPE_vide) && (i != Track.TYPE_text) && (i != Track.TYPE_sbtl) && (i != Track.TYPE_subt)) {
      return null;
    }
    TkhdData localTkhdData = parseTkhd(paramContainerAtom.getLeafAtomOfType(Atom.TYPE_tkhd).data);
    long l1 = paramLong;
    if (paramLong == -1L) {
      l1 = localTkhdData.duration;
    }
    long l2 = parseMvhd(paramLeafAtom.data);
    if (l1 == -1L) {}
    for (paramLong = -1L;; paramLong = Util.scaleLargeTimestamp(l1, 1000000L, l2))
    {
      Atom.ContainerAtom localContainerAtom = ((Atom.ContainerAtom)localObject).getContainerAtomOfType(Atom.TYPE_minf).getContainerAtomOfType(Atom.TYPE_stbl);
      paramLeafAtom = parseMdhd(((Atom.ContainerAtom)localObject).getLeafAtomOfType(Atom.TYPE_mdhd).data);
      localObject = parseStsd(localContainerAtom.getLeafAtomOfType(Atom.TYPE_stsd).data, localTkhdData.id, paramLong, localTkhdData.rotationDegrees, (String)paramLeafAtom.second, paramBoolean);
      paramContainerAtom = parseEdts(paramContainerAtom.getContainerAtomOfType(Atom.TYPE_edts));
      if (((StsdData)localObject).mediaFormat != null) {
        break;
      }
      return null;
    }
    return new Track(localTkhdData.id, i, ((Long)paramLeafAtom.first).longValue(), l2, paramLong, ((StsdData)localObject).mediaFormat, ((StsdData)localObject).trackEncryptionBoxes, ((StsdData)localObject).nalUnitLengthFieldLength, (long[])paramContainerAtom.first, (long[])paramContainerAtom.second);
  }
  
  public static GaplessInfo parseUdta(Atom.LeafAtom paramLeafAtom, boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (;;)
    {
      return null;
      paramLeafAtom = paramLeafAtom.data;
      paramLeafAtom.setPosition(8);
      while (paramLeafAtom.bytesLeft() >= 8)
      {
        int i = paramLeafAtom.readInt();
        if (paramLeafAtom.readInt() == Atom.TYPE_meta)
        {
          paramLeafAtom.setPosition(paramLeafAtom.getPosition() - 8);
          paramLeafAtom.setLimit(paramLeafAtom.getPosition() + i);
          return parseMetaAtom(paramLeafAtom);
        }
        paramLeafAtom.skipBytes(i - 8);
      }
    }
  }
  
  private static void parseVideoSampleEntry(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong, int paramInt5, StsdData paramStsdData, int paramInt6)
  {
    paramParsableByteArray.setPosition(paramInt2 + 8);
    paramParsableByteArray.skipBytes(24);
    int k = paramParsableByteArray.readUnsignedShort();
    int m = paramParsableByteArray.readUnsignedShort();
    int i = 0;
    float f2 = 1.0F;
    paramParsableByteArray.skipBytes(50);
    int j = paramParsableByteArray.getPosition();
    if (paramInt1 == Atom.TYPE_encv)
    {
      parseSampleEntryEncryptionData(paramParsableByteArray, paramInt2, paramInt3, paramStsdData, paramInt6);
      paramParsableByteArray.setPosition(j);
    }
    Object localObject3 = null;
    Object localObject4 = null;
    paramInt6 = j;
    int n;
    if (paramInt6 - paramInt2 < paramInt3)
    {
      paramParsableByteArray.setPosition(paramInt6);
      j = paramParsableByteArray.getPosition();
      n = paramParsableByteArray.readInt();
      if ((n != 0) || (paramParsableByteArray.getPosition() - paramInt2 != paramInt3)) {}
    }
    else
    {
      if (localObject4 != null) {
        break label573;
      }
      return;
    }
    boolean bool;
    label133:
    int i1;
    label162:
    Object localObject2;
    float f1;
    if (n > 0)
    {
      bool = true;
      Assertions.checkArgument(bool, "childAtomSize should be positive");
      i1 = paramParsableByteArray.readInt();
      if (i1 != Atom.TYPE_avcC) {
        break label275;
      }
      if (localObject4 != null) {
        break label269;
      }
      bool = true;
      Assertions.checkState(bool);
      localObject3 = "video/avc";
      AvcCData localAvcCData = parseAvcCFromParent(paramParsableByteArray, j);
      localObject4 = localAvcCData.initializationData;
      paramStsdData.nalUnitLengthFieldLength = localAvcCData.nalUnitLengthFieldLength;
      localObject1 = localObject3;
      localObject2 = localObject4;
      f1 = f2;
      j = i;
      if (i == 0)
      {
        f1 = localAvcCData.pixelWidthAspectRatio;
        j = i;
        localObject2 = localObject4;
        localObject1 = localObject3;
      }
    }
    label269:
    label275:
    label495:
    do
    {
      for (;;)
      {
        paramInt6 += n;
        localObject4 = localObject1;
        localObject3 = localObject2;
        f2 = f1;
        i = j;
        break;
        bool = false;
        break label133;
        bool = false;
        break label162;
        if (i1 == Atom.TYPE_hvcC)
        {
          if (localObject4 == null) {}
          for (bool = true;; bool = false)
          {
            Assertions.checkState(bool);
            localObject1 = "video/hevc";
            localObject3 = parseHvcCFromParent(paramParsableByteArray, j);
            localObject2 = (List)((Pair)localObject3).first;
            paramStsdData.nalUnitLengthFieldLength = ((Integer)((Pair)localObject3).second).intValue();
            f1 = f2;
            j = i;
            break;
          }
        }
        if (i1 == Atom.TYPE_d263)
        {
          if (localObject4 == null) {}
          for (bool = true;; bool = false)
          {
            Assertions.checkState(bool);
            localObject1 = "video/3gpp";
            localObject2 = localObject3;
            f1 = f2;
            j = i;
            break;
          }
        }
        if (i1 == Atom.TYPE_esds)
        {
          if (localObject4 == null) {}
          for (bool = true;; bool = false)
          {
            Assertions.checkState(bool);
            localObject2 = parseEsdsFromParent(paramParsableByteArray, j);
            localObject1 = (String)((Pair)localObject2).first;
            localObject2 = Collections.singletonList(((Pair)localObject2).second);
            f1 = f2;
            j = i;
            break;
          }
        }
        if (i1 != Atom.TYPE_pasp) {
          break label495;
        }
        f1 = parsePaspFromParent(paramParsableByteArray, j);
        j = 1;
        localObject1 = localObject4;
        localObject2 = localObject3;
      }
      localObject1 = localObject4;
      localObject2 = localObject3;
      f1 = f2;
      j = i;
    } while (i1 != Atom.TYPE_vpcC);
    if (localObject4 == null)
    {
      bool = true;
      label527:
      Assertions.checkState(bool);
      if (paramInt1 != Atom.TYPE_vp08) {
        break label565;
      }
    }
    label565:
    for (Object localObject1 = "video/x-vnd.on2.vp8";; localObject1 = "video/x-vnd.on2.vp9")
    {
      localObject2 = localObject3;
      f1 = f2;
      j = i;
      break;
      bool = false;
      break label527;
    }
    label573:
    paramStsdData.mediaFormat = MediaFormat.createVideoFormat(Integer.toString(paramInt4), (String)localObject4, -1, -1, paramLong, k, m, (List)localObject3, paramInt5, f2);
  }
  
  private static final class AvcCData
  {
    public final List<byte[]> initializationData;
    public final int nalUnitLengthFieldLength;
    public final float pixelWidthAspectRatio;
    
    public AvcCData(List<byte[]> paramList, int paramInt, float paramFloat)
    {
      this.initializationData = paramList;
      this.nalUnitLengthFieldLength = paramInt;
      this.pixelWidthAspectRatio = paramFloat;
    }
  }
  
  private static final class ChunkIterator
  {
    private final ParsableByteArray chunkOffsets;
    private final boolean chunkOffsetsAreLongs;
    public int index;
    public final int length;
    private int nextSamplesPerChunkChangeIndex;
    public int numSamples;
    public long offset;
    private int remainingSamplesPerChunkChanges;
    private final ParsableByteArray stsc;
    
    public ChunkIterator(ParsableByteArray paramParsableByteArray1, ParsableByteArray paramParsableByteArray2, boolean paramBoolean)
    {
      this.stsc = paramParsableByteArray1;
      this.chunkOffsets = paramParsableByteArray2;
      this.chunkOffsetsAreLongs = paramBoolean;
      paramParsableByteArray2.setPosition(12);
      this.length = paramParsableByteArray2.readUnsignedIntToInt();
      paramParsableByteArray1.setPosition(12);
      this.remainingSamplesPerChunkChanges = paramParsableByteArray1.readUnsignedIntToInt();
      if (paramParsableByteArray1.readInt() == 1) {}
      for (paramBoolean = bool;; paramBoolean = false)
      {
        Assertions.checkState(paramBoolean, "first_chunk must be 1");
        this.index = -1;
        return;
      }
    }
    
    public boolean moveNext()
    {
      int i = this.index + 1;
      this.index = i;
      if (i == this.length) {
        return false;
      }
      long l;
      if (this.chunkOffsetsAreLongs)
      {
        l = this.chunkOffsets.readUnsignedLongToLong();
        this.offset = l;
        if (this.index == this.nextSamplesPerChunkChangeIndex)
        {
          this.numSamples = this.stsc.readUnsignedIntToInt();
          this.stsc.skipBytes(4);
          i = this.remainingSamplesPerChunkChanges - 1;
          this.remainingSamplesPerChunkChanges = i;
          if (i <= 0) {
            break label116;
          }
        }
      }
      label116:
      for (i = this.stsc.readUnsignedIntToInt() - 1;; i = -1)
      {
        this.nextSamplesPerChunkChangeIndex = i;
        return true;
        l = this.chunkOffsets.readUnsignedInt();
        break;
      }
    }
  }
  
  private static abstract interface SampleSizeBox
  {
    public abstract int getSampleCount();
    
    public abstract boolean isFixedSampleSize();
    
    public abstract int readNextSampleSize();
  }
  
  private static final class StsdData
  {
    public MediaFormat mediaFormat;
    public int nalUnitLengthFieldLength;
    public final TrackEncryptionBox[] trackEncryptionBoxes;
    
    public StsdData(int paramInt)
    {
      this.trackEncryptionBoxes = new TrackEncryptionBox[paramInt];
      this.nalUnitLengthFieldLength = -1;
    }
  }
  
  static final class StszSampleSizeBox
    implements AtomParsers.SampleSizeBox
  {
    private final ParsableByteArray data;
    private final int fixedSampleSize;
    private final int sampleCount;
    
    public StszSampleSizeBox(Atom.LeafAtom paramLeafAtom)
    {
      this.data = paramLeafAtom.data;
      this.data.setPosition(12);
      this.fixedSampleSize = this.data.readUnsignedIntToInt();
      this.sampleCount = this.data.readUnsignedIntToInt();
    }
    
    public int getSampleCount()
    {
      return this.sampleCount;
    }
    
    public boolean isFixedSampleSize()
    {
      return this.fixedSampleSize != 0;
    }
    
    public int readNextSampleSize()
    {
      if (this.fixedSampleSize == 0) {
        return this.data.readUnsignedIntToInt();
      }
      return this.fixedSampleSize;
    }
  }
  
  static final class Stz2SampleSizeBox
    implements AtomParsers.SampleSizeBox
  {
    private int currentByte;
    private final ParsableByteArray data;
    private final int fieldSize;
    private final int sampleCount;
    private int sampleIndex;
    
    public Stz2SampleSizeBox(Atom.LeafAtom paramLeafAtom)
    {
      this.data = paramLeafAtom.data;
      this.data.setPosition(12);
      this.fieldSize = (this.data.readUnsignedIntToInt() & 0xFF);
      this.sampleCount = this.data.readUnsignedIntToInt();
    }
    
    public int getSampleCount()
    {
      return this.sampleCount;
    }
    
    public boolean isFixedSampleSize()
    {
      return false;
    }
    
    public int readNextSampleSize()
    {
      if (this.fieldSize == 8) {
        return this.data.readUnsignedByte();
      }
      if (this.fieldSize == 16) {
        return this.data.readUnsignedShort();
      }
      int i = this.sampleIndex;
      this.sampleIndex = (i + 1);
      if (i % 2 == 0)
      {
        this.currentByte = this.data.readUnsignedByte();
        return (this.currentByte & 0xF0) >> 4;
      }
      return this.currentByte & 0xF;
    }
  }
  
  private static final class TkhdData
  {
    private final long duration;
    private final int id;
    private final int rotationDegrees;
    
    public TkhdData(int paramInt1, long paramLong, int paramInt2)
    {
      this.id = paramInt1;
      this.duration = paramLong;
      this.rotationDegrees = paramInt2;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/mp4/AtomParsers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */