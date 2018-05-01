package org.telegram.messenger.exoplayer2.extractor.mp4;

import android.util.Log;
import android.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.audio.Ac3Util;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.extractor.GaplessInfoHolder;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.metadata.Metadata.Entry;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.CodecSpecificDataUtil;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;
import org.telegram.messenger.exoplayer2.video.AvcConfig;
import org.telegram.messenger.exoplayer2.video.HevcConfig;

final class AtomParsers
{
  private static final String TAG = "AtomParsers";
  private static final int TYPE_cenc = Util.getIntegerCodeForString("cenc");
  private static final int TYPE_clcp;
  private static final int TYPE_meta = Util.getIntegerCodeForString("meta");
  private static final int TYPE_sbtl;
  private static final int TYPE_soun;
  private static final int TYPE_subt;
  private static final int TYPE_text;
  private static final int TYPE_vide = Util.getIntegerCodeForString("vide");
  
  static
  {
    TYPE_soun = Util.getIntegerCodeForString("soun");
    TYPE_text = Util.getIntegerCodeForString("text");
    TYPE_sbtl = Util.getIntegerCodeForString("sbtl");
    TYPE_subt = Util.getIntegerCodeForString("subt");
    TYPE_clcp = Util.getIntegerCodeForString("clcp");
  }
  
  private static int findEsdsPosition(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2)
  {
    int i = paramParsableByteArray.getPosition();
    int j;
    boolean bool;
    if (i - paramInt1 < paramInt2)
    {
      paramParsableByteArray.setPosition(i);
      j = paramParsableByteArray.readInt();
      if (j > 0)
      {
        bool = true;
        label31:
        Assertions.checkArgument(bool, "childAtomSize should be positive");
        if (paramParsableByteArray.readInt() != Atom.TYPE_esds) {
          break label56;
        }
      }
    }
    for (;;)
    {
      return i;
      bool = false;
      break label31;
      label56:
      i += j;
      break;
      i = -1;
    }
  }
  
  private static void parseAudioSampleEntry(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString, boolean paramBoolean, DrmInitData paramDrmInitData, StsdData paramStsdData, int paramInt5)
    throws ParserException
  {
    paramParsableByteArray.setPosition(paramInt2 + 8 + 8);
    int i = 0;
    int j;
    int k;
    int m;
    int n;
    label88:
    DrmInitData localDrmInitData;
    Object localObject1;
    label145:
    label185:
    Object localObject2;
    label195:
    boolean bool;
    label221:
    label266:
    Object localObject3;
    if (paramBoolean)
    {
      i = paramParsableByteArray.readUnsignedShort();
      paramParsableByteArray.skipBytes(6);
      if ((i != 0) && (i != 1)) {
        break label416;
      }
      j = paramParsableByteArray.readUnsignedShort();
      paramParsableByteArray.skipBytes(6);
      k = paramParsableByteArray.readUnsignedFixedPoint1616();
      m = j;
      n = k;
      if (i == 1)
      {
        paramParsableByteArray.skipBytes(16);
        n = k;
        m = j;
      }
      j = paramParsableByteArray.getPosition();
      i = paramInt1;
      localDrmInitData = paramDrmInitData;
      if (paramInt1 == Atom.TYPE_enca)
      {
        localObject1 = parseSampleEntryEncryptionData(paramParsableByteArray, paramInt2, paramInt3);
        localDrmInitData = paramDrmInitData;
        if (localObject1 != null)
        {
          paramInt1 = ((Integer)((Pair)localObject1).first).intValue();
          if (paramDrmInitData != null) {
            break label453;
          }
          localDrmInitData = null;
          paramStsdData.trackEncryptionBoxes[paramInt5] = ((TrackEncryptionBox)((Pair)localObject1).second);
        }
        paramParsableByteArray.setPosition(j);
        i = paramInt1;
      }
      paramDrmInitData = null;
      if (i != Atom.TYPE_ac_3) {
        break label474;
      }
      paramDrmInitData = "audio/ac3";
      localObject2 = null;
      paramInt1 = j;
      localObject1 = paramDrmInitData;
      if (paramInt1 - paramInt2 >= paramInt3) {
        break label863;
      }
      paramParsableByteArray.setPosition(paramInt1);
      k = paramParsableByteArray.readInt();
      if (k <= 0) {
        break label625;
      }
      bool = true;
      Assertions.checkArgument(bool, "childAtomSize should be positive");
      j = paramParsableByteArray.readInt();
      if ((j != Atom.TYPE_esds) && ((!paramBoolean) || (j != Atom.TYPE_wave))) {
        break label643;
      }
      if (j != Atom.TYPE_esds) {
        break label631;
      }
      j = paramInt1;
      localObject3 = localObject1;
      i = m;
      paramInt5 = n;
      paramDrmInitData = (DrmInitData)localObject2;
      if (j != -1)
      {
        paramDrmInitData = parseEsdsFromParent(paramParsableByteArray, j);
        localObject1 = (String)paramDrmInitData.first;
        localObject2 = (byte[])paramDrmInitData.second;
        localObject3 = localObject1;
        i = m;
        paramInt5 = n;
        paramDrmInitData = (DrmInitData)localObject2;
        if ("audio/mp4a-latm".equals(localObject1))
        {
          paramDrmInitData = CodecSpecificDataUtil.parseAacAudioSpecificConfig((byte[])localObject2);
          paramInt5 = ((Integer)paramDrmInitData.first).intValue();
          i = ((Integer)paramDrmInitData.second).intValue();
          paramDrmInitData = (DrmInitData)localObject2;
          localObject3 = localObject1;
        }
      }
    }
    for (;;)
    {
      paramInt1 += k;
      localObject1 = localObject3;
      m = i;
      n = paramInt5;
      localObject2 = paramDrmInitData;
      break label195;
      paramParsableByteArray.skipBytes(8);
      break;
      label416:
      if (i != 2) {
        break label928;
      }
      paramParsableByteArray.skipBytes(16);
      n = (int)Math.round(paramParsableByteArray.readDouble());
      m = paramParsableByteArray.readUnsignedIntToInt();
      paramParsableByteArray.skipBytes(20);
      break label88;
      label453:
      localDrmInitData = paramDrmInitData.copyWithSchemeType(((TrackEncryptionBox)((Pair)localObject1).second).schemeType);
      break label145;
      label474:
      if (i == Atom.TYPE_ec_3)
      {
        paramDrmInitData = "audio/eac3";
        break label185;
      }
      if (i == Atom.TYPE_dtsc)
      {
        paramDrmInitData = "audio/vnd.dts";
        break label185;
      }
      if ((i == Atom.TYPE_dtsh) || (i == Atom.TYPE_dtsl))
      {
        paramDrmInitData = "audio/vnd.dts.hd";
        break label185;
      }
      if (i == Atom.TYPE_dtse)
      {
        paramDrmInitData = "audio/vnd.dts.hd;profile=lbr";
        break label185;
      }
      if (i == Atom.TYPE_samr)
      {
        paramDrmInitData = "audio/3gpp";
        break label185;
      }
      if (i == Atom.TYPE_sawb)
      {
        paramDrmInitData = "audio/amr-wb";
        break label185;
      }
      if ((i == Atom.TYPE_lpcm) || (i == Atom.TYPE_sowt))
      {
        paramDrmInitData = "audio/raw";
        break label185;
      }
      if (i == Atom.TYPE__mp3)
      {
        paramDrmInitData = "audio/mpeg";
        break label185;
      }
      if (i != Atom.TYPE_alac) {
        break label185;
      }
      paramDrmInitData = "audio/alac";
      break label185;
      label625:
      bool = false;
      break label221;
      label631:
      j = findEsdsPosition(paramParsableByteArray, paramInt1, k);
      break label266;
      label643:
      if (j == Atom.TYPE_dac3)
      {
        paramParsableByteArray.setPosition(paramInt1 + 8);
        paramStsdData.format = Ac3Util.parseAc3AnnexFFormat(paramParsableByteArray, Integer.toString(paramInt4), paramString, localDrmInitData);
        localObject3 = localObject1;
        i = m;
        paramInt5 = n;
        paramDrmInitData = (DrmInitData)localObject2;
      }
      else if (j == Atom.TYPE_dec3)
      {
        paramParsableByteArray.setPosition(paramInt1 + 8);
        paramStsdData.format = Ac3Util.parseEAc3AnnexFFormat(paramParsableByteArray, Integer.toString(paramInt4), paramString, localDrmInitData);
        localObject3 = localObject1;
        i = m;
        paramInt5 = n;
        paramDrmInitData = (DrmInitData)localObject2;
      }
      else if (j == Atom.TYPE_ddts)
      {
        paramStsdData.format = Format.createAudioSampleFormat(Integer.toString(paramInt4), (String)localObject1, null, -1, -1, m, n, null, localDrmInitData, 0, paramString);
        localObject3 = localObject1;
        i = m;
        paramInt5 = n;
        paramDrmInitData = (DrmInitData)localObject2;
      }
      else
      {
        localObject3 = localObject1;
        i = m;
        paramInt5 = n;
        paramDrmInitData = (DrmInitData)localObject2;
        if (j == Atom.TYPE_alac)
        {
          paramDrmInitData = new byte[k];
          paramParsableByteArray.setPosition(paramInt1);
          paramParsableByteArray.readBytes(paramDrmInitData, 0, k);
          localObject3 = localObject1;
          i = m;
          paramInt5 = n;
        }
      }
    }
    label863:
    if ((paramStsdData.format == null) && (localObject1 != null))
    {
      if (!"audio/raw".equals(localObject1)) {
        break label929;
      }
      paramInt1 = 2;
      paramDrmInitData = Integer.toString(paramInt4);
      if (localObject2 != null) {
        break label934;
      }
    }
    label928:
    label929:
    label934:
    for (paramParsableByteArray = null;; paramParsableByteArray = Collections.singletonList(localObject2))
    {
      paramStsdData.format = Format.createAudioSampleFormat(paramDrmInitData, (String)localObject1, null, -1, -1, m, n, paramInt1, paramParsableByteArray, localDrmInitData, 0, paramString);
      return;
      paramInt1 = -1;
      break;
    }
  }
  
  static Pair<Integer, TrackEncryptionBox> parseCommonEncryptionSinfFromParent(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2)
  {
    boolean bool1 = true;
    int i = paramInt1 + 8;
    int j = -1;
    int k = 0;
    Object localObject1 = null;
    Object localObject2 = null;
    if (i - paramInt1 < paramInt2)
    {
      paramParsableByteArray.setPosition(i);
      int m = paramParsableByteArray.readInt();
      int n = paramParsableByteArray.readInt();
      Object localObject3;
      Object localObject4;
      if (n == Atom.TYPE_frma)
      {
        localObject3 = Integer.valueOf(paramParsableByteArray.readInt());
        localObject4 = localObject1;
      }
      for (;;)
      {
        i += m;
        localObject2 = localObject3;
        localObject1 = localObject4;
        break;
        if (n == Atom.TYPE_schm)
        {
          paramParsableByteArray.skipBytes(4);
          localObject4 = paramParsableByteArray.readString(4);
          localObject3 = localObject2;
        }
        else
        {
          localObject3 = localObject2;
          localObject4 = localObject1;
          if (n == Atom.TYPE_schi)
          {
            j = i;
            k = m;
            localObject3 = localObject2;
            localObject4 = localObject1;
          }
        }
      }
    }
    boolean bool2;
    if (("cenc".equals(localObject1)) || ("cbc1".equals(localObject1)) || ("cens".equals(localObject1)) || ("cbcs".equals(localObject1))) {
      if (localObject2 != null)
      {
        bool2 = true;
        Assertions.checkArgument(bool2, "frma atom is mandatory");
        if (j == -1) {
          break label264;
        }
        bool2 = true;
        label215:
        Assertions.checkArgument(bool2, "schi atom is mandatory");
        paramParsableByteArray = parseSchiFromParent(paramParsableByteArray, j, k, (String)localObject1);
        if (paramParsableByteArray == null) {
          break label270;
        }
        bool2 = bool1;
        label241:
        Assertions.checkArgument(bool2, "tenc atom is mandatory");
      }
    }
    for (paramParsableByteArray = Pair.create(localObject2, paramParsableByteArray);; paramParsableByteArray = null)
    {
      return paramParsableByteArray;
      bool2 = false;
      break;
      label264:
      bool2 = false;
      break label215;
      label270:
      bool2 = false;
      break label241;
    }
  }
  
  private static Pair<long[], long[]> parseEdts(Atom.ContainerAtom paramContainerAtom)
  {
    if (paramContainerAtom != null)
    {
      paramContainerAtom = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_elst);
      if (paramContainerAtom != null) {
        break label24;
      }
    }
    label24:
    long[] arrayOfLong;
    for (paramContainerAtom = Pair.create(null, null);; paramContainerAtom = Pair.create(arrayOfLong, paramContainerAtom))
    {
      return paramContainerAtom;
      ParsableByteArray localParsableByteArray = paramContainerAtom.data;
      localParsableByteArray.setPosition(8);
      int i = Atom.parseFullAtomVersion(localParsableByteArray.readInt());
      int j = localParsableByteArray.readUnsignedIntToInt();
      arrayOfLong = new long[j];
      paramContainerAtom = new long[j];
      for (int k = 0; k < j; k++)
      {
        if (i == 1)
        {
          l = localParsableByteArray.readUnsignedLongToLong();
          arrayOfLong[k] = l;
          if (i != 1) {
            break label129;
          }
        }
        label129:
        for (long l = localParsableByteArray.readLong();; l = localParsableByteArray.readInt())
        {
          paramContainerAtom[k] = l;
          if (localParsableByteArray.readShort() == 1) {
            break label139;
          }
          throw new IllegalArgumentException("Unsupported media rate.");
          l = localParsableByteArray.readUnsignedInt();
          break;
        }
        label139:
        localParsableByteArray.skipBytes(2);
      }
    }
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
      paramParsableByteArray.skipBytes(12);
      paramParsableByteArray.skipBytes(1);
      paramInt = parseExpandableClassSize(paramParsableByteArray);
      byte[] arrayOfByte = new byte[paramInt];
      paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt);
      paramParsableByteArray = Pair.create(localObject, arrayOfByte);
    }
    for (;;)
    {
      return paramParsableByteArray;
      localObject = "video/mpeg2";
      break;
      localObject = "video/mp4v-es";
      break;
      localObject = "video/avc";
      break;
      localObject = "video/hevc";
      break;
      paramParsableByteArray = Pair.create("audio/mpeg", null);
      continue;
      localObject = "audio/mp4a-latm";
      break;
      localObject = "audio/ac3";
      break;
      localObject = "audio/eac3";
      break;
      paramParsableByteArray = Pair.create("audio/vnd.dts", null);
      continue;
      paramParsableByteArray = Pair.create("audio/vnd.dts.hd", null);
    }
  }
  
  private static int parseExpandableClassSize(ParsableByteArray paramParsableByteArray)
  {
    int i = paramParsableByteArray.readUnsignedByte();
    for (int j = i & 0x7F; (i & 0x80) == 128; j = j << 7 | i & 0x7F) {
      i = paramParsableByteArray.readUnsignedByte();
    }
    return j;
  }
  
  private static int parseHdlr(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.setPosition(16);
    int i = paramParsableByteArray.readInt();
    if (i == TYPE_soun) {
      i = 1;
    }
    for (;;)
    {
      return i;
      if (i == TYPE_vide) {
        i = 2;
      } else if ((i == TYPE_text) || (i == TYPE_sbtl) || (i == TYPE_subt) || (i == TYPE_clcp)) {
        i = 3;
      } else if (i == TYPE_meta) {
        i = 4;
      } else {
        i = -1;
      }
    }
  }
  
  private static Metadata parseIlst(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    paramParsableByteArray.skipBytes(8);
    ArrayList localArrayList = new ArrayList();
    while (paramParsableByteArray.getPosition() < paramInt)
    {
      Metadata.Entry localEntry = MetadataUtil.parseIlstElement(paramParsableByteArray);
      if (localEntry != null) {
        localArrayList.add(localEntry);
      }
    }
    if (localArrayList.isEmpty()) {}
    for (paramParsableByteArray = null;; paramParsableByteArray = new Metadata(localArrayList)) {
      return paramParsableByteArray;
    }
  }
  
  private static Pair<Long, String> parseMdhd(ParsableByteArray paramParsableByteArray)
  {
    int i = 8;
    paramParsableByteArray.setPosition(8);
    int j = Atom.parseFullAtomVersion(paramParsableByteArray.readInt());
    if (j == 0) {}
    for (int k = 8;; k = 16)
    {
      paramParsableByteArray.skipBytes(k);
      long l = paramParsableByteArray.readUnsignedInt();
      k = i;
      if (j == 0) {
        k = 4;
      }
      paramParsableByteArray.skipBytes(k);
      k = paramParsableByteArray.readUnsignedShort();
      return Pair.create(Long.valueOf(l), "" + (char)((k >> 10 & 0x1F) + 96) + (char)((k >> 5 & 0x1F) + 96) + (char)((k & 0x1F) + 96));
    }
  }
  
  private static Metadata parseMetaAtom(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    paramParsableByteArray.skipBytes(12);
    int i;
    int j;
    if (paramParsableByteArray.getPosition() < paramInt)
    {
      i = paramParsableByteArray.getPosition();
      j = paramParsableByteArray.readInt();
      if (paramParsableByteArray.readInt() == Atom.TYPE_ilst) {
        paramParsableByteArray.setPosition(i);
      }
    }
    for (paramParsableByteArray = parseIlst(paramParsableByteArray, i + j);; paramParsableByteArray = null)
    {
      return paramParsableByteArray;
      paramParsableByteArray.skipBytes(j - 8);
      break;
    }
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
  
  private static byte[] parseProjFromParent(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2)
  {
    int i = paramInt1 + 8;
    int j;
    if (i - paramInt1 < paramInt2)
    {
      paramParsableByteArray.setPosition(i);
      j = paramParsableByteArray.readInt();
      if (paramParsableByteArray.readInt() != Atom.TYPE_proj) {}
    }
    for (paramParsableByteArray = Arrays.copyOfRange(paramParsableByteArray.data, i, i + j);; paramParsableByteArray = null)
    {
      return paramParsableByteArray;
      i += j;
      break;
    }
  }
  
  private static Pair<Integer, TrackEncryptionBox> parseSampleEntryEncryptionData(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2)
  {
    int i = paramParsableByteArray.getPosition();
    int j;
    boolean bool;
    label31:
    Pair localPair;
    if (i - paramInt1 < paramInt2)
    {
      paramParsableByteArray.setPosition(i);
      j = paramParsableByteArray.readInt();
      if (j > 0)
      {
        bool = true;
        Assertions.checkArgument(bool, "childAtomSize should be positive");
        if (paramParsableByteArray.readInt() != Atom.TYPE_sinf) {
          break label73;
        }
        localPair = parseCommonEncryptionSinfFromParent(paramParsableByteArray, i, j);
        if (localPair == null) {
          break label73;
        }
      }
    }
    for (paramParsableByteArray = localPair;; paramParsableByteArray = null)
    {
      return paramParsableByteArray;
      bool = false;
      break label31;
      label73:
      i += j;
      break;
    }
  }
  
  private static TrackEncryptionBox parseSchiFromParent(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, String paramString)
  {
    int i = paramInt1 + 8;
    int j;
    label64:
    boolean bool;
    label75:
    byte[] arrayOfByte;
    Object localObject2;
    if (i - paramInt1 < paramInt2)
    {
      paramParsableByteArray.setPosition(i);
      j = paramParsableByteArray.readInt();
      if (paramParsableByteArray.readInt() == Atom.TYPE_tenc)
      {
        i = Atom.parseFullAtomVersion(paramParsableByteArray.readInt());
        paramParsableByteArray.skipBytes(1);
        paramInt2 = 0;
        paramInt1 = 0;
        if (i == 0)
        {
          paramParsableByteArray.skipBytes(1);
          if (paramParsableByteArray.readUnsignedByte() != 1) {
            break label181;
          }
          bool = true;
          i = paramParsableByteArray.readUnsignedByte();
          arrayOfByte = new byte[16];
          paramParsableByteArray.readBytes(arrayOfByte, 0, arrayOfByte.length);
          Object localObject1 = null;
          localObject2 = localObject1;
          if (bool)
          {
            localObject2 = localObject1;
            if (i == 0)
            {
              j = paramParsableByteArray.readUnsignedByte();
              localObject2 = new byte[j];
              paramParsableByteArray.readBytes((byte[])localObject2, 0, j);
            }
          }
        }
      }
    }
    for (paramParsableByteArray = new TrackEncryptionBox(bool, paramString, i, arrayOfByte, paramInt2, paramInt1, (byte[])localObject2);; paramParsableByteArray = null)
    {
      return paramParsableByteArray;
      paramInt1 = paramParsableByteArray.readUnsignedByte();
      paramInt2 = (paramInt1 & 0xF0) >> 4;
      paramInt1 &= 0xF;
      break label64;
      label181:
      bool = false;
      break label75;
      i += j;
      break;
    }
  }
  
  public static TrackSampleTable parseStbl(Track paramTrack, Atom.ContainerAtom paramContainerAtom, GaplessInfoHolder paramGaplessInfoHolder)
    throws ParserException
  {
    Object localObject1 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_stsz);
    Object localObject2;
    int i;
    if (localObject1 != null)
    {
      localObject2 = new StszSampleSizeBox((Atom.LeafAtom)localObject1);
      i = ((SampleSizeBox)localObject2).getSampleCount();
      if (i != 0) {
        break label95;
      }
      paramTrack = new TrackSampleTable(new long[0], new int[0], 0, new long[0], new int[0]);
    }
    for (;;)
    {
      return paramTrack;
      localObject1 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_stz2);
      if (localObject1 == null) {
        throw new ParserException("Track has no sample table size information");
      }
      localObject2 = new Stz2SampleSizeBox((Atom.LeafAtom)localObject1);
      break;
      label95:
      boolean bool = false;
      Object localObject3 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_stco);
      localObject1 = localObject3;
      if (localObject3 == null)
      {
        bool = true;
        localObject1 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_co64);
      }
      Object localObject4 = ((Atom.LeafAtom)localObject1).data;
      localObject3 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_stsc).data;
      ParsableByteArray localParsableByteArray = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_stts).data;
      localObject1 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_stss);
      label190:
      ChunkIterator localChunkIterator;
      int j;
      int k;
      int m;
      int n;
      int i2;
      int i5;
      if (localObject1 != null)
      {
        localObject1 = ((Atom.LeafAtom)localObject1).data;
        paramContainerAtom = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_ctts);
        if (paramContainerAtom == null) {
          break label462;
        }
        paramContainerAtom = paramContainerAtom.data;
        localChunkIterator = new ChunkIterator((ParsableByteArray)localObject3, (ParsableByteArray)localObject4, bool);
        localParsableByteArray.setPosition(12);
        j = localParsableByteArray.readUnsignedIntToInt() - 1;
        k = localParsableByteArray.readUnsignedIntToInt();
        m = localParsableByteArray.readUnsignedIntToInt();
        n = 0;
        i1 = 0;
        i2 = 0;
        if (paramContainerAtom != null)
        {
          paramContainerAtom.setPosition(12);
          i1 = paramContainerAtom.readUnsignedIntToInt();
        }
        i3 = -1;
        i4 = 0;
        i5 = i3;
        localObject3 = localObject1;
        if (localObject1 != null)
        {
          ((ParsableByteArray)localObject1).setPosition(12);
          i4 = ((ParsableByteArray)localObject1).readUnsignedIntToInt();
          if (i4 <= 0) {
            break label467;
          }
          i5 = ((ParsableByteArray)localObject1).readUnsignedIntToInt() - 1;
          localObject3 = localObject1;
        }
        label305:
        if ((!((SampleSizeBox)localObject2).isFixedSampleSize()) || (!"audio/raw".equals(paramTrack.format.sampleMimeType)) || (j != 0) || (i1 != 0) || (i4 != 0)) {
          break label477;
        }
      }
      long l1;
      long[] arrayOfLong;
      int[] arrayOfInt;
      Object localObject5;
      long l2;
      int i7;
      int i9;
      label462:
      label467:
      label477:
      for (int i3 = 1;; i3 = 0)
      {
        i6 = 0;
        l1 = 0L;
        if (i3 != 0) {
          break label1028;
        }
        arrayOfLong = new long[i];
        arrayOfInt = new int[i];
        localObject5 = new long[i];
        localObject4 = new int[i];
        l2 = 0L;
        i7 = 0;
        i8 = 0;
        i3 = i2;
        i9 = i4;
        i2 = i7;
        i4 = n;
        i7 = i5;
        n = i8;
        i5 = i6;
        if (n >= i) {
          break label790;
        }
        while (i2 == 0)
        {
          Assertions.checkState(localChunkIterator.moveNext());
          l2 = localChunkIterator.offset;
          i2 = localChunkIterator.numSamples;
        }
        localObject1 = null;
        break;
        paramContainerAtom = null;
        break label190;
        localObject3 = null;
        i5 = i3;
        break label305;
      }
      int i10 = i4;
      int i8 = i1;
      int i6 = i3;
      if (paramContainerAtom != null)
      {
        while ((i4 == 0) && (i1 > 0))
        {
          i4 = paramContainerAtom.readUnsignedIntToInt();
          i3 = paramContainerAtom.readInt();
          i1--;
        }
        i10 = i4 - 1;
        i6 = i3;
        i8 = i1;
      }
      arrayOfLong[n] = l2;
      arrayOfInt[n] = ((SampleSizeBox)localObject2).readNextSampleSize();
      int i4 = i5;
      if (arrayOfInt[n] > i5) {
        i4 = arrayOfInt[n];
      }
      localObject5[n] = (i6 + l1);
      if (localObject3 == null) {}
      for (int i1 = 1;; i1 = 0)
      {
        localObject4[n] = i1;
        int i11 = i7;
        i3 = i9;
        if (n == i7)
        {
          localObject4[n] = 1;
          i1 = i9 - 1;
          i11 = i7;
          i3 = i1;
          if (i1 > 0)
          {
            i11 = ((ParsableByteArray)localObject3).readUnsignedIntToInt() - 1;
            i3 = i1;
          }
        }
        l1 += m;
        i5 = k - 1;
        i1 = i5;
        int i12 = j;
        int i13 = m;
        if (i5 == 0)
        {
          i1 = i5;
          i12 = j;
          i13 = m;
          if (j > 0)
          {
            i1 = localParsableByteArray.readUnsignedIntToInt();
            i13 = localParsableByteArray.readInt();
            i12 = j - 1;
          }
        }
        l2 += arrayOfInt[n];
        i2--;
        n++;
        i5 = i4;
        i7 = i11;
        k = i1;
        i4 = i10;
        i9 = i3;
        j = i12;
        i1 = i8;
        m = i13;
        i3 = i6;
        break;
      }
      label790:
      if (i4 == 0)
      {
        bool = true;
        Assertions.checkArgument(bool);
        label803:
        if (i1 <= 0) {
          break label846;
        }
        if (paramContainerAtom.readUnsignedIntToInt() != 0) {
          break label840;
        }
      }
      label840:
      for (bool = true;; bool = false)
      {
        Assertions.checkArgument(bool);
        paramContainerAtom.readInt();
        i1--;
        break label803;
        bool = false;
        break;
      }
      label846:
      if ((i9 == 0) && (k == 0) && (i2 == 0))
      {
        paramContainerAtom = arrayOfLong;
        localObject1 = arrayOfInt;
        i1 = i5;
        localObject3 = localObject5;
        localObject2 = localObject4;
        l2 = l1;
        if (j == 0) {}
      }
      else
      {
        Log.w("AtomParsers", "Inconsistent stbl box for track " + paramTrack.id + ": remainingSynchronizationSamples " + i9 + ", remainingSamplesAtTimestampDelta " + k + ", remainingSamplesInChunk " + i2 + ", remainingTimestampDeltaChanges " + j);
        l2 = l1;
        localObject2 = localObject4;
        localObject3 = localObject5;
        i1 = i5;
        localObject1 = arrayOfInt;
        paramContainerAtom = arrayOfLong;
      }
      for (;;)
      {
        if ((paramTrack.editListDurations != null) && (!paramGaplessInfoHolder.hasGaplessInfo())) {
          break label1136;
        }
        Util.scaleLargeTimestampsInPlace((long[])localObject3, 1000000L, paramTrack.timescale);
        paramTrack = new TrackSampleTable(paramContainerAtom, (int[])localObject1, i1, (long[])localObject3, (int[])localObject2);
        break;
        label1028:
        localObject1 = new long[localChunkIterator.length];
        paramContainerAtom = new int[localChunkIterator.length];
        while (localChunkIterator.moveNext())
        {
          localObject1[localChunkIterator.index] = localChunkIterator.offset;
          paramContainerAtom[localChunkIterator.index] = localChunkIterator.numSamples;
        }
        localObject2 = FixedSampleSizeRechunker.rechunk(((SampleSizeBox)localObject2).readNextSampleSize(), (long[])localObject1, paramContainerAtom, m);
        paramContainerAtom = ((FixedSampleSizeRechunker.Results)localObject2).offsets;
        localObject1 = ((FixedSampleSizeRechunker.Results)localObject2).sizes;
        i1 = ((FixedSampleSizeRechunker.Results)localObject2).maximumSize;
        localObject3 = ((FixedSampleSizeRechunker.Results)localObject2).timestamps;
        localObject2 = ((FixedSampleSizeRechunker.Results)localObject2).flags;
        l2 = l1;
      }
      label1136:
      long l3;
      if ((paramTrack.editListDurations.length == 1) && (paramTrack.type == 1) && (localObject3.length >= 2))
      {
        l3 = paramTrack.editListMediaTimes[0];
        l1 = l3 + Util.scaleLargeTimestamp(paramTrack.editListDurations[0], paramTrack.timescale, paramTrack.movieTimescale);
        if ((localObject3[0] <= l3) && (l3 < localObject3[1]) && (localObject3[(localObject3.length - 1)] < l1) && (l1 <= l2))
        {
          l3 = Util.scaleLargeTimestamp(l3 - localObject3[0], paramTrack.format.sampleRate, paramTrack.timescale);
          l1 = Util.scaleLargeTimestamp(l2 - l1, paramTrack.format.sampleRate, paramTrack.timescale);
          if (((l3 != 0L) || (l1 != 0L)) && (l3 <= 2147483647L) && (l1 <= 2147483647L))
          {
            paramGaplessInfoHolder.encoderDelay = ((int)l3);
            paramGaplessInfoHolder.encoderPadding = ((int)l1);
            Util.scaleLargeTimestampsInPlace((long[])localObject3, 1000000L, paramTrack.timescale);
            paramTrack = new TrackSampleTable(paramContainerAtom, (int[])localObject1, i1, (long[])localObject3, (int[])localObject2);
            continue;
          }
        }
      }
      if ((paramTrack.editListDurations.length == 1) && (paramTrack.editListDurations[0] == 0L))
      {
        for (i5 = 0; i5 < localObject3.length; i5++) {
          localObject3[i5] = Util.scaleLargeTimestamp(localObject3[i5] - paramTrack.editListMediaTimes[0], 1000000L, paramTrack.timescale);
        }
        paramTrack = new TrackSampleTable(paramContainerAtom, (int[])localObject1, i1, (long[])localObject3, (int[])localObject2);
      }
      else
      {
        if (paramTrack.type == 1)
        {
          bool = true;
          i4 = 0;
          i2 = 0;
          i5 = 0;
          i3 = 0;
          label1461:
          if (i3 >= paramTrack.editListDurations.length) {
            break label1604;
          }
          l2 = paramTrack.editListMediaTimes[i3];
          n = i5;
          m = i4;
          j = i2;
          if (l2 != -1L)
          {
            l1 = Util.scaleLargeTimestamp(paramTrack.editListDurations[i3], paramTrack.timescale, paramTrack.movieTimescale);
            n = Util.binarySearchCeil((long[])localObject3, l2, true, true);
            j = Util.binarySearchCeil((long[])localObject3, l2 + l1, bool, false);
            m = i4 + (j - n);
            if (i2 == n) {
              break label1598;
            }
          }
        }
        label1598:
        for (i4 = 1;; i4 = 0)
        {
          n = i5 | i4;
          i3++;
          i5 = n;
          i4 = m;
          i2 = j;
          break label1461;
          bool = false;
          break;
        }
        label1604:
        if (i4 != i)
        {
          i3 = 1;
          n = i5 | i3;
          if (n == 0) {
            break label1909;
          }
          paramGaplessInfoHolder = new long[i4];
          label1631:
          if (n == 0) {
            break label1914;
          }
          localObject4 = new int[i4];
          label1642:
          if (n == 0) {
            break label1920;
          }
          i5 = 0;
          label1650:
          if (n == 0) {
            break label1927;
          }
          localObject5 = new int[i4];
          label1661:
          arrayOfLong = new long[i4];
          l1 = 0L;
          i4 = 0;
          i3 = 0;
        }
        for (;;)
        {
          if (i3 >= paramTrack.editListDurations.length) {
            break label1955;
          }
          long l4 = paramTrack.editListMediaTimes[i3];
          l3 = paramTrack.editListDurations[i3];
          j = i5;
          m = i4;
          if (l4 != -1L)
          {
            l2 = Util.scaleLargeTimestamp(l3, paramTrack.timescale, paramTrack.movieTimescale);
            i2 = Util.binarySearchCeil((long[])localObject3, l4, true, true);
            i9 = Util.binarySearchCeil((long[])localObject3, l4 + l2, bool, false);
            if (n != 0)
            {
              j = i9 - i2;
              System.arraycopy(paramContainerAtom, i2, paramGaplessInfoHolder, i4, j);
              System.arraycopy(localObject1, i2, localObject4, i4, j);
              System.arraycopy(localObject2, i2, localObject5, i4, j);
            }
            for (;;)
            {
              j = i5;
              m = i4;
              if (i2 >= i9) {
                break;
              }
              arrayOfLong[i4] = (Util.scaleLargeTimestamp(l1, 1000000L, paramTrack.movieTimescale) + Util.scaleLargeTimestamp(localObject3[i2] - l4, 1000000L, paramTrack.timescale));
              j = i5;
              if (n != 0)
              {
                j = i5;
                if (localObject4[i4] > i5) {
                  j = localObject1[i2];
                }
              }
              i4++;
              i2++;
              i5 = j;
            }
            i3 = 0;
            break;
            label1909:
            paramGaplessInfoHolder = paramContainerAtom;
            break label1631;
            label1914:
            localObject4 = localObject1;
            break label1642;
            label1920:
            i5 = i1;
            break label1650;
            label1927:
            localObject5 = localObject2;
            break label1661;
          }
          l1 += l3;
          i3++;
          i5 = j;
          i4 = m;
        }
        label1955:
        i3 = 0;
        i4 = 0;
        if ((i4 < localObject5.length) && (i3 == 0))
        {
          if ((localObject5[i4] & 0x1) != 0) {}
          for (i2 = 1;; i2 = 0)
          {
            i3 |= i2;
            i4++;
            break;
          }
        }
        if (i3 == 0)
        {
          Log.w("AtomParsers", "Ignoring edit list: Edited sample sequence does not contain a sync sample.");
          Util.scaleLargeTimestampsInPlace((long[])localObject3, 1000000L, paramTrack.timescale);
          paramTrack = new TrackSampleTable(paramContainerAtom, (int[])localObject1, i1, (long[])localObject3, (int[])localObject2);
        }
        else
        {
          paramTrack = new TrackSampleTable(paramGaplessInfoHolder, (int[])localObject4, i5, arrayOfLong, (int[])localObject5);
        }
      }
    }
  }
  
  private static StsdData parseStsd(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, String paramString, DrmInitData paramDrmInitData, boolean paramBoolean)
    throws ParserException
  {
    paramParsableByteArray.setPosition(12);
    int i = paramParsableByteArray.readInt();
    StsdData localStsdData = new StsdData(i);
    int j = 0;
    if (j < i)
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
          break label177;
        }
        parseVideoSampleEntry(paramParsableByteArray, n, k, m, paramInt1, paramInt2, paramDrmInitData, localStsdData, j);
      }
      for (;;)
      {
        paramParsableByteArray.setPosition(k + m);
        j++;
        break;
        bool = false;
        break label53;
        label177:
        if ((n == Atom.TYPE_mp4a) || (n == Atom.TYPE_enca) || (n == Atom.TYPE_ac_3) || (n == Atom.TYPE_ec_3) || (n == Atom.TYPE_dtsc) || (n == Atom.TYPE_dtse) || (n == Atom.TYPE_dtsh) || (n == Atom.TYPE_dtsl) || (n == Atom.TYPE_samr) || (n == Atom.TYPE_sawb) || (n == Atom.TYPE_lpcm) || (n == Atom.TYPE_sowt) || (n == Atom.TYPE__mp3) || (n == Atom.TYPE_alac)) {
          parseAudioSampleEntry(paramParsableByteArray, n, k, m, paramInt1, paramString, paramBoolean, paramDrmInitData, localStsdData, j);
        } else if ((n == Atom.TYPE_TTML) || (n == Atom.TYPE_tx3g) || (n == Atom.TYPE_wvtt) || (n == Atom.TYPE_stpp) || (n == Atom.TYPE_c608)) {
          parseTextSampleEntry(paramParsableByteArray, n, k, m, paramInt1, paramString, localStsdData);
        } else if (n == Atom.TYPE_camm) {
          localStsdData.format = Format.createSampleFormat(Integer.toString(paramInt1), "application/x-camera-motion", null, -1, null);
        }
      }
    }
    return localStsdData;
  }
  
  private static void parseTextSampleEntry(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString, StsdData paramStsdData)
    throws ParserException
  {
    paramParsableByteArray.setPosition(paramInt2 + 8 + 8);
    Object localObject1 = null;
    long l = Long.MAX_VALUE;
    if (paramInt1 == Atom.TYPE_TTML) {
      paramParsableByteArray = "application/ttml+xml";
    }
    for (;;)
    {
      paramStsdData.format = Format.createTextSampleFormat(Integer.toString(paramInt4), paramParsableByteArray, null, -1, 0, paramString, -1, null, l, (List)localObject1);
      return;
      if (paramInt1 == Atom.TYPE_tx3g)
      {
        localObject1 = "application/x-quicktime-tx3g";
        paramInt1 = paramInt3 - 8 - 8;
        Object localObject2 = new byte[paramInt1];
        paramParsableByteArray.readBytes((byte[])localObject2, 0, paramInt1);
        localObject2 = Collections.singletonList(localObject2);
        paramParsableByteArray = (ParsableByteArray)localObject1;
        localObject1 = localObject2;
      }
      else if (paramInt1 == Atom.TYPE_wvtt)
      {
        paramParsableByteArray = "application/x-mp4-vtt";
      }
      else if (paramInt1 == Atom.TYPE_stpp)
      {
        paramParsableByteArray = "application/ttml+xml";
        l = 0L;
      }
      else
      {
        if (paramInt1 != Atom.TYPE_c608) {
          break;
        }
        paramParsableByteArray = "application/x-mp4-cea-608";
        paramStsdData.requiredSampleTransformation = 1;
      }
    }
    throw new IllegalStateException();
  }
  
  private static TkhdData parseTkhd(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.setPosition(8);
    int i = Atom.parseFullAtomVersion(paramParsableByteArray.readInt());
    int j;
    int k;
    int m;
    label51:
    int i1;
    label54:
    int i2;
    long l1;
    if (i == 0)
    {
      j = 8;
      paramParsableByteArray.skipBytes(j);
      k = paramParsableByteArray.readInt();
      paramParsableByteArray.skipBytes(4);
      m = 1;
      int n = paramParsableByteArray.getPosition();
      if (i != 0) {
        break label177;
      }
      j = 4;
      i1 = 0;
      i2 = m;
      if (i1 < j)
      {
        if (paramParsableByteArray.data[(n + i1)] == -1) {
          break label183;
        }
        i2 = 0;
      }
      if (i2 == 0) {
        break label189;
      }
      paramParsableByteArray.skipBytes(j);
      l1 = -9223372036854775807L;
      paramParsableByteArray.skipBytes(16);
      m = paramParsableByteArray.readInt();
      j = paramParsableByteArray.readInt();
      paramParsableByteArray.skipBytes(4);
      i2 = paramParsableByteArray.readInt();
      i1 = paramParsableByteArray.readInt();
      if ((m != 0) || (j != 65536) || (i2 != -65536) || (i1 != 0)) {
        break label227;
      }
      j = 90;
    }
    for (;;)
    {
      return new TkhdData(k, l1, j);
      j = 16;
      break;
      label177:
      j = 8;
      break label51;
      label183:
      i1++;
      break label54;
      label189:
      if (i == 0) {}
      for (long l2 = paramParsableByteArray.readUnsignedInt();; l2 = paramParsableByteArray.readUnsignedLongToLong())
      {
        l1 = l2;
        if (l2 != 0L) {
          break;
        }
        l1 = -9223372036854775807L;
        break;
      }
      label227:
      if ((m == 0) && (j == -65536) && (i2 == 65536) && (i1 == 0)) {
        j = 270;
      } else if ((m == -65536) && (j == 0) && (i2 == 0) && (i1 == -65536)) {
        j = 180;
      } else {
        j = 0;
      }
    }
  }
  
  public static Track parseTrak(Atom.ContainerAtom paramContainerAtom, Atom.LeafAtom paramLeafAtom, long paramLong, DrmInitData paramDrmInitData, boolean paramBoolean1, boolean paramBoolean2)
    throws ParserException
  {
    Object localObject = paramContainerAtom.getContainerAtomOfType(Atom.TYPE_mdia);
    int i = parseHdlr(((Atom.ContainerAtom)localObject).getLeafAtomOfType(Atom.TYPE_hdlr).data);
    if (i == -1) {
      paramContainerAtom = null;
    }
    for (;;)
    {
      return paramContainerAtom;
      TkhdData localTkhdData = parseTkhd(paramContainerAtom.getLeafAtomOfType(Atom.TYPE_tkhd).data);
      long l1 = paramLong;
      if (paramLong == -9223372036854775807L) {
        l1 = localTkhdData.duration;
      }
      long l2 = parseMvhd(paramLeafAtom.data);
      if (l1 == -9223372036854775807L) {}
      StsdData localStsdData;
      for (paramLong = -9223372036854775807L;; paramLong = Util.scaleLargeTimestamp(l1, 1000000L, l2))
      {
        paramLeafAtom = ((Atom.ContainerAtom)localObject).getContainerAtomOfType(Atom.TYPE_minf).getContainerAtomOfType(Atom.TYPE_stbl);
        localObject = parseMdhd(((Atom.ContainerAtom)localObject).getLeafAtomOfType(Atom.TYPE_mdhd).data);
        localStsdData = parseStsd(paramLeafAtom.getLeafAtomOfType(Atom.TYPE_stsd).data, localTkhdData.id, localTkhdData.rotationDegrees, (String)((Pair)localObject).second, paramDrmInitData, paramBoolean2);
        paramDrmInitData = null;
        paramLeafAtom = null;
        if (!paramBoolean1)
        {
          paramContainerAtom = parseEdts(paramContainerAtom.getContainerAtomOfType(Atom.TYPE_edts));
          paramDrmInitData = (long[])paramContainerAtom.first;
          paramLeafAtom = (long[])paramContainerAtom.second;
        }
        if (localStsdData.format != null) {
          break label223;
        }
        paramContainerAtom = null;
        break;
      }
      label223:
      paramContainerAtom = new Track(localTkhdData.id, i, ((Long)((Pair)localObject).first).longValue(), l2, paramLong, localStsdData.format, localStsdData.requiredSampleTransformation, localStsdData.trackEncryptionBoxes, localStsdData.nalUnitLengthFieldLength, paramDrmInitData, paramLeafAtom);
    }
  }
  
  public static Metadata parseUdta(Atom.LeafAtom paramLeafAtom, boolean paramBoolean)
  {
    Object localObject = null;
    if (paramBoolean)
    {
      paramLeafAtom = (Atom.LeafAtom)localObject;
      return paramLeafAtom;
    }
    ParsableByteArray localParsableByteArray = paramLeafAtom.data;
    localParsableByteArray.setPosition(8);
    for (;;)
    {
      paramLeafAtom = (Atom.LeafAtom)localObject;
      if (localParsableByteArray.bytesLeft() < 8) {
        break;
      }
      int i = localParsableByteArray.getPosition();
      int j = localParsableByteArray.readInt();
      if (localParsableByteArray.readInt() == Atom.TYPE_meta)
      {
        localParsableByteArray.setPosition(i);
        paramLeafAtom = parseMetaAtom(localParsableByteArray, i + j);
        break;
      }
      localParsableByteArray.skipBytes(j - 8);
    }
  }
  
  private static void parseVideoSampleEntry(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, DrmInitData paramDrmInitData, StsdData paramStsdData, int paramInt6)
    throws ParserException
  {
    paramParsableByteArray.setPosition(paramInt2 + 8 + 8);
    paramParsableByteArray.skipBytes(16);
    int i = paramParsableByteArray.readUnsignedShort();
    int j = paramParsableByteArray.readUnsignedShort();
    int k = 0;
    float f1 = 1.0F;
    paramParsableByteArray.skipBytes(50);
    int m = paramParsableByteArray.getPosition();
    int n = paramInt1;
    Object localObject1 = paramDrmInitData;
    Object localObject2;
    Object localObject3;
    Object localObject4;
    Object localObject5;
    int i1;
    if (paramInt1 == Atom.TYPE_encv)
    {
      localObject1 = parseSampleEntryEncryptionData(paramParsableByteArray, paramInt2, paramInt3);
      localObject2 = paramDrmInitData;
      if (localObject1 != null)
      {
        paramInt1 = ((Integer)((Pair)localObject1).first).intValue();
        if (paramDrmInitData == null)
        {
          localObject2 = null;
          paramStsdData.trackEncryptionBoxes[paramInt6] = ((TrackEncryptionBox)((Pair)localObject1).second);
        }
      }
      else
      {
        paramParsableByteArray.setPosition(m);
        localObject1 = localObject2;
        n = paramInt1;
      }
    }
    else
    {
      localObject3 = null;
      localObject4 = null;
      localObject5 = null;
      paramInt1 = -1;
      paramInt6 = k;
      k = paramInt1;
      if (m - paramInt2 < paramInt3)
      {
        paramParsableByteArray.setPosition(m);
        paramInt1 = paramParsableByteArray.getPosition();
        i1 = paramParsableByteArray.readInt();
        if ((i1 != 0) || (paramParsableByteArray.getPosition() - paramInt2 != paramInt3)) {
          break label212;
        }
      }
      if (localObject4 != null) {
        break label970;
      }
    }
    for (;;)
    {
      return;
      localObject2 = paramDrmInitData.copyWithSchemeType(((TrackEncryptionBox)((Pair)localObject1).second).schemeType);
      break;
      label212:
      boolean bool;
      label220:
      int i2;
      label249:
      float f2;
      Object localObject6;
      int i3;
      if (i1 > 0)
      {
        bool = true;
        Assertions.checkArgument(bool, "childAtomSize should be positive");
        i2 = paramParsableByteArray.readInt();
        if (i2 != Atom.TYPE_avcC) {
          break label389;
        }
        if (localObject4 != null) {
          break label383;
        }
        bool = true;
        Assertions.checkState(bool);
        localObject4 = "video/avc";
        paramParsableByteArray.setPosition(paramInt1 + 8);
        AvcConfig localAvcConfig = AvcConfig.parse(paramParsableByteArray);
        localObject3 = localAvcConfig.initializationData;
        paramStsdData.nalUnitLengthFieldLength = localAvcConfig.nalUnitLengthFieldLength;
        paramDrmInitData = (DrmInitData)localObject4;
        localObject2 = localObject3;
        f2 = f1;
        localObject6 = localObject5;
        paramInt1 = k;
        i3 = paramInt6;
        if (paramInt6 == 0)
        {
          f2 = localAvcConfig.pixelWidthAspectRatio;
          i3 = paramInt6;
          paramInt1 = k;
          localObject6 = localObject5;
          localObject2 = localObject3;
          paramDrmInitData = (DrmInitData)localObject4;
        }
      }
      for (;;)
      {
        m += i1;
        localObject4 = paramDrmInitData;
        localObject3 = localObject2;
        f1 = f2;
        localObject5 = localObject6;
        k = paramInt1;
        paramInt6 = i3;
        break;
        bool = false;
        break label220;
        label383:
        bool = false;
        break label249;
        label389:
        if (i2 == Atom.TYPE_hvcC)
        {
          if (localObject4 == null) {}
          for (bool = true;; bool = false)
          {
            Assertions.checkState(bool);
            paramDrmInitData = "video/hevc";
            paramParsableByteArray.setPosition(paramInt1 + 8);
            localObject3 = HevcConfig.parse(paramParsableByteArray);
            localObject2 = ((HevcConfig)localObject3).initializationData;
            paramStsdData.nalUnitLengthFieldLength = ((HevcConfig)localObject3).nalUnitLengthFieldLength;
            f2 = f1;
            localObject6 = localObject5;
            paramInt1 = k;
            i3 = paramInt6;
            break;
          }
        }
        if (i2 == Atom.TYPE_vpcC)
        {
          if (localObject4 == null)
          {
            bool = true;
            label486:
            Assertions.checkState(bool);
            if (n != Atom.TYPE_vp08) {
              break label532;
            }
          }
          label532:
          for (paramDrmInitData = "video/x-vnd.on2.vp8";; paramDrmInitData = "video/x-vnd.on2.vp9")
          {
            localObject2 = localObject3;
            f2 = f1;
            localObject6 = localObject5;
            paramInt1 = k;
            i3 = paramInt6;
            break;
            bool = false;
            break label486;
          }
        }
        if (i2 == Atom.TYPE_d263)
        {
          if (localObject4 == null) {}
          for (bool = true;; bool = false)
          {
            Assertions.checkState(bool);
            paramDrmInitData = "video/3gpp";
            localObject2 = localObject3;
            f2 = f1;
            localObject6 = localObject5;
            paramInt1 = k;
            i3 = paramInt6;
            break;
          }
        }
        if (i2 == Atom.TYPE_esds)
        {
          if (localObject4 == null) {}
          for (bool = true;; bool = false)
          {
            Assertions.checkState(bool);
            localObject2 = parseEsdsFromParent(paramParsableByteArray, paramInt1);
            paramDrmInitData = (String)((Pair)localObject2).first;
            localObject2 = Collections.singletonList(((Pair)localObject2).second);
            f2 = f1;
            localObject6 = localObject5;
            paramInt1 = k;
            i3 = paramInt6;
            break;
          }
        }
        if (i2 == Atom.TYPE_pasp)
        {
          f2 = parsePaspFromParent(paramParsableByteArray, paramInt1);
          i3 = 1;
          paramDrmInitData = (DrmInitData)localObject4;
          localObject2 = localObject3;
          localObject6 = localObject5;
          paramInt1 = k;
        }
        else if (i2 == Atom.TYPE_sv3d)
        {
          localObject6 = parseProjFromParent(paramParsableByteArray, paramInt1, i1);
          paramDrmInitData = (DrmInitData)localObject4;
          localObject2 = localObject3;
          f2 = f1;
          paramInt1 = k;
          i3 = paramInt6;
        }
        else
        {
          paramDrmInitData = (DrmInitData)localObject4;
          localObject2 = localObject3;
          f2 = f1;
          localObject6 = localObject5;
          paramInt1 = k;
          i3 = paramInt6;
          if (i2 == Atom.TYPE_st3d)
          {
            i2 = paramParsableByteArray.readUnsignedByte();
            paramParsableByteArray.skipBytes(3);
            paramDrmInitData = (DrmInitData)localObject4;
            localObject2 = localObject3;
            f2 = f1;
            localObject6 = localObject5;
            paramInt1 = k;
            i3 = paramInt6;
            if (i2 == 0) {
              switch (paramParsableByteArray.readUnsignedByte())
              {
              default: 
                paramDrmInitData = (DrmInitData)localObject4;
                localObject2 = localObject3;
                f2 = f1;
                localObject6 = localObject5;
                paramInt1 = k;
                i3 = paramInt6;
                break;
              case 0: 
                paramInt1 = 0;
                paramDrmInitData = (DrmInitData)localObject4;
                localObject2 = localObject3;
                f2 = f1;
                localObject6 = localObject5;
                i3 = paramInt6;
                break;
              case 1: 
                paramInt1 = 1;
                paramDrmInitData = (DrmInitData)localObject4;
                localObject2 = localObject3;
                f2 = f1;
                localObject6 = localObject5;
                i3 = paramInt6;
                break;
              case 2: 
                paramInt1 = 2;
                paramDrmInitData = (DrmInitData)localObject4;
                localObject2 = localObject3;
                f2 = f1;
                localObject6 = localObject5;
                i3 = paramInt6;
                break;
              case 3: 
                paramInt1 = 3;
                paramDrmInitData = (DrmInitData)localObject4;
                localObject2 = localObject3;
                f2 = f1;
                localObject6 = localObject5;
                i3 = paramInt6;
              }
            }
          }
        }
      }
      label970:
      paramStsdData.format = Format.createVideoSampleFormat(Integer.toString(paramInt4), (String)localObject4, null, -1, -1, i, j, -1.0F, (List)localObject3, paramInt5, f1, (byte[])localObject5, k, null, (DrmInitData)localObject1);
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
      boolean bool;
      if (i == this.length)
      {
        bool = false;
        return bool;
      }
      long l;
      if (this.chunkOffsetsAreLongs)
      {
        l = this.chunkOffsets.readUnsignedLongToLong();
        label39:
        this.offset = l;
        if (this.index == this.nextSamplesPerChunkChangeIndex)
        {
          this.numSamples = this.stsc.readUnsignedIntToInt();
          this.stsc.skipBytes(4);
          i = this.remainingSamplesPerChunkChanges - 1;
          this.remainingSamplesPerChunkChanges = i;
          if (i <= 0) {
            break label121;
          }
        }
      }
      label121:
      for (i = this.stsc.readUnsignedIntToInt() - 1;; i = -1)
      {
        this.nextSamplesPerChunkChangeIndex = i;
        bool = true;
        break;
        l = this.chunkOffsets.readUnsignedInt();
        break label39;
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
    public static final int STSD_HEADER_SIZE = 8;
    public Format format;
    public int nalUnitLengthFieldLength;
    public int requiredSampleTransformation;
    public final TrackEncryptionBox[] trackEncryptionBoxes;
    
    public StsdData(int paramInt)
    {
      this.trackEncryptionBoxes = new TrackEncryptionBox[paramInt];
      this.requiredSampleTransformation = 0;
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
      if (this.fixedSampleSize != 0) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public int readNextSampleSize()
    {
      if (this.fixedSampleSize == 0) {}
      for (int i = this.data.readUnsignedIntToInt();; i = this.fixedSampleSize) {
        return i;
      }
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
      int i;
      if (this.fieldSize == 8) {
        i = this.data.readUnsignedByte();
      }
      for (;;)
      {
        return i;
        if (this.fieldSize == 16)
        {
          i = this.data.readUnsignedShort();
        }
        else
        {
          i = this.sampleIndex;
          this.sampleIndex = (i + 1);
          if (i % 2 == 0)
          {
            this.currentByte = this.data.readUnsignedByte();
            i = (this.currentByte & 0xF0) >> 4;
          }
          else
          {
            i = this.currentByte & 0xF;
          }
        }
      }
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/mp4/AtomParsers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */