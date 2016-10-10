package org.telegram.messenger.exoplayer.extractor.mp4;

import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.drm.DrmInitData;
import org.telegram.messenger.exoplayer.drm.DrmInitData.Mapped;
import org.telegram.messenger.exoplayer.drm.DrmInitData.SchemeInitData;
import org.telegram.messenger.exoplayer.extractor.ChunkIndex;
import org.telegram.messenger.exoplayer.extractor.Extractor;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer.extractor.PositionHolder;
import org.telegram.messenger.exoplayer.extractor.SeekMap;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.exoplayer.util.Util;

public class FragmentedMp4Extractor
  implements Extractor
{
  private static final int FLAG_SIDELOADED = 4;
  public static final int FLAG_WORKAROUND_EVERY_VIDEO_FRAME_IS_SYNC_FRAME = 1;
  public static final int FLAG_WORKAROUND_IGNORE_TFDT_BOX = 2;
  private static final byte[] PIFF_SAMPLE_ENCRYPTION_BOX_EXTENDED_TYPE = { -94, 57, 79, 82, 90, -101, 79, 20, -94, 68, 108, 66, 124, 100, -115, -12 };
  private static final int SAMPLE_GROUP_TYPE_seig = Util.getIntegerCodeForString("seig");
  private static final int STATE_READING_ATOM_HEADER = 0;
  private static final int STATE_READING_ATOM_PAYLOAD = 1;
  private static final int STATE_READING_ENCRYPTION_DATA = 2;
  private static final int STATE_READING_SAMPLE_CONTINUE = 4;
  private static final int STATE_READING_SAMPLE_START = 3;
  private static final String TAG = "FragmentedMp4Extractor";
  private ParsableByteArray atomData;
  private final ParsableByteArray atomHeader;
  private int atomHeaderBytesRead;
  private long atomSize;
  private int atomType;
  private final Stack<Atom.ContainerAtom> containerAtoms;
  private TrackBundle currentTrackBundle;
  private final ParsableByteArray encryptionSignalByte;
  private long endOfMdatPosition;
  private final byte[] extendedTypeScratch;
  private ExtractorOutput extractorOutput;
  private final int flags;
  private boolean haveOutputSeekMap;
  private final ParsableByteArray nalLength;
  private final ParsableByteArray nalStartCode;
  private int parserState;
  private int sampleBytesWritten;
  private int sampleCurrentNalBytesRemaining;
  private int sampleSize;
  private final Track sideloadedTrack;
  private final SparseArray<TrackBundle> trackBundles;
  
  public FragmentedMp4Extractor()
  {
    this(0);
  }
  
  public FragmentedMp4Extractor(int paramInt)
  {
    this(paramInt, null);
  }
  
  public FragmentedMp4Extractor(int paramInt, Track paramTrack)
  {
    this.sideloadedTrack = paramTrack;
    if (paramTrack != null) {}
    for (int i = 4;; i = 0)
    {
      this.flags = (i | paramInt);
      this.atomHeader = new ParsableByteArray(16);
      this.nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
      this.nalLength = new ParsableByteArray(4);
      this.encryptionSignalByte = new ParsableByteArray(1);
      this.extendedTypeScratch = new byte[16];
      this.containerAtoms = new Stack();
      this.trackBundles = new SparseArray();
      enterReadingAtomHeaderState();
      return;
    }
  }
  
  private int appendSampleEncryptionData(TrackBundle paramTrackBundle)
  {
    TrackFragment localTrackFragment = paramTrackBundle.fragment;
    ParsableByteArray localParsableByteArray = localTrackFragment.sampleEncryptionData;
    int i = localTrackFragment.header.sampleDescriptionIndex;
    Object localObject;
    int j;
    int k;
    if (localTrackFragment.trackEncryptionBox != null)
    {
      localObject = localTrackFragment.trackEncryptionBox;
      j = ((TrackEncryptionBox)localObject).initializationVectorSize;
      k = localTrackFragment.sampleHasSubsampleEncryptionTable[paramTrackBundle.currentSampleIndex];
      localObject = this.encryptionSignalByte.data;
      if (k == 0) {
        break label137;
      }
    }
    label137:
    for (i = 128;; i = 0)
    {
      localObject[0] = ((byte)(i | j));
      this.encryptionSignalByte.setPosition(0);
      paramTrackBundle = paramTrackBundle.output;
      paramTrackBundle.sampleData(this.encryptionSignalByte, 1);
      paramTrackBundle.sampleData(localParsableByteArray, j);
      if (k != 0) {
        break label142;
      }
      return j + 1;
      localObject = paramTrackBundle.track.sampleDescriptionEncryptionBoxes[i];
      break;
    }
    label142:
    i = localParsableByteArray.readUnsignedShort();
    localParsableByteArray.skipBytes(-2);
    i = i * 6 + 2;
    paramTrackBundle.sampleData(localParsableByteArray, i);
    return j + 1 + i;
  }
  
  private void enterReadingAtomHeaderState()
  {
    this.parserState = 0;
    this.atomHeaderBytesRead = 0;
  }
  
  private static TrackBundle getNextFragmentRun(SparseArray<TrackBundle> paramSparseArray)
  {
    Object localObject = null;
    long l1 = Long.MAX_VALUE;
    int j = paramSparseArray.size();
    int i = 0;
    if (i < j)
    {
      TrackBundle localTrackBundle = (TrackBundle)paramSparseArray.valueAt(i);
      long l2;
      if (localTrackBundle.currentSampleIndex == localTrackBundle.fragment.length) {
        l2 = l1;
      }
      for (;;)
      {
        i += 1;
        l1 = l2;
        break;
        long l3 = localTrackBundle.fragment.dataPosition;
        l2 = l1;
        if (l3 < l1)
        {
          localObject = localTrackBundle;
          l2 = l3;
        }
      }
    }
    return (TrackBundle)localObject;
  }
  
  private void onContainerAtomRead(Atom.ContainerAtom paramContainerAtom)
    throws ParserException
  {
    if (paramContainerAtom.type == Atom.TYPE_moov) {
      onMoovContainerAtomRead(paramContainerAtom);
    }
    do
    {
      return;
      if (paramContainerAtom.type == Atom.TYPE_moof)
      {
        onMoofContainerAtomRead(paramContainerAtom);
        return;
      }
    } while (this.containerAtoms.isEmpty());
    ((Atom.ContainerAtom)this.containerAtoms.peek()).add(paramContainerAtom);
  }
  
  private void onLeafAtomRead(Atom.LeafAtom paramLeafAtom, long paramLong)
    throws ParserException
  {
    if (!this.containerAtoms.isEmpty()) {
      ((Atom.ContainerAtom)this.containerAtoms.peek()).add(paramLeafAtom);
    }
    do
    {
      return;
      if (paramLeafAtom.type == Atom.TYPE_sidx)
      {
        paramLeafAtom = parseSidx(paramLeafAtom.data, paramLong);
        this.extractorOutput.seekMap(paramLeafAtom);
        this.haveOutputSeekMap = true;
        return;
      }
    } while (paramLeafAtom.type != Atom.TYPE_emsg);
    parseEmsg(paramLeafAtom.data, paramLong);
  }
  
  private void onMoofContainerAtomRead(Atom.ContainerAtom paramContainerAtom)
    throws ParserException
  {
    parseMoof(paramContainerAtom, this.trackBundles, this.flags, this.extendedTypeScratch);
  }
  
  private void onMoovContainerAtomRead(Atom.ContainerAtom paramContainerAtom)
  {
    Object localObject3;
    if (this.sideloadedTrack == null)
    {
      bool = true;
      Assertions.checkState(bool, "Unexpected moov box.");
      localObject3 = paramContainerAtom.leafChildren;
      j = ((List)localObject3).size();
      localObject1 = null;
      i = 0;
      label37:
      if (i >= j) {
        break label157;
      }
      Atom.LeafAtom localLeafAtom = (Atom.LeafAtom)((List)localObject3).get(i);
      localObject2 = localObject1;
      if (localLeafAtom.type == Atom.TYPE_pssh)
      {
        localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = new DrmInitData.Mapped();
        }
        localObject1 = localLeafAtom.data.data;
        if (PsshAtomUtil.parseUuid((byte[])localObject1) != null) {
          break label132;
        }
        Log.w("FragmentedMp4Extractor", "Skipped pssh atom (failed to extract uuid)");
      }
    }
    for (;;)
    {
      i += 1;
      localObject1 = localObject2;
      break label37;
      bool = false;
      break;
      label132:
      ((DrmInitData.Mapped)localObject2).put(PsshAtomUtil.parseUuid((byte[])localObject1), new DrmInitData.SchemeInitData("video/mp4", (byte[])localObject1));
    }
    label157:
    if (localObject1 != null) {
      this.extractorOutput.drmInitData((DrmInitData)localObject1);
    }
    Object localObject2 = paramContainerAtom.getContainerAtomOfType(Atom.TYPE_mvex);
    Object localObject1 = new SparseArray();
    long l = -1L;
    int j = ((Atom.ContainerAtom)localObject2).leafChildren.size();
    int i = 0;
    if (i < j)
    {
      localObject3 = (Atom.LeafAtom)((Atom.ContainerAtom)localObject2).leafChildren.get(i);
      if (((Atom.LeafAtom)localObject3).type == Atom.TYPE_trex)
      {
        localObject3 = parseTrex(((Atom.LeafAtom)localObject3).data);
        ((SparseArray)localObject1).put(((Integer)((Pair)localObject3).first).intValue(), ((Pair)localObject3).second);
      }
      for (;;)
      {
        i += 1;
        break;
        if (((Atom.LeafAtom)localObject3).type == Atom.TYPE_mehd) {
          l = parseMehd(((Atom.LeafAtom)localObject3).data);
        }
      }
    }
    localObject2 = new SparseArray();
    j = paramContainerAtom.containerChildren.size();
    i = 0;
    while (i < j)
    {
      localObject3 = (Atom.ContainerAtom)paramContainerAtom.containerChildren.get(i);
      if (((Atom.ContainerAtom)localObject3).type == Atom.TYPE_trak)
      {
        localObject3 = AtomParsers.parseTrak((Atom.ContainerAtom)localObject3, paramContainerAtom.getLeafAtomOfType(Atom.TYPE_mvhd), l, false);
        if (localObject3 != null) {
          ((SparseArray)localObject2).put(((Track)localObject3).id, localObject3);
        }
      }
      i += 1;
    }
    j = ((SparseArray)localObject2).size();
    if (this.trackBundles.size() == 0)
    {
      i = 0;
      while (i < j)
      {
        this.trackBundles.put(((Track)((SparseArray)localObject2).valueAt(i)).id, new TrackBundle(this.extractorOutput.track(i)));
        i += 1;
      }
      this.extractorOutput.endTracks();
      i = 0;
      while (i < j)
      {
        paramContainerAtom = (Track)((SparseArray)localObject2).valueAt(i);
        ((TrackBundle)this.trackBundles.get(paramContainerAtom.id)).init(paramContainerAtom, (DefaultSampleValues)((SparseArray)localObject1).get(paramContainerAtom.id));
        i += 1;
      }
    }
    if (this.trackBundles.size() == j) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      break;
    }
  }
  
  private static long parseMehd(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.setPosition(8);
    if (Atom.parseFullAtomVersion(paramParsableByteArray.readInt()) == 0) {
      return paramParsableByteArray.readUnsignedInt();
    }
    return paramParsableByteArray.readUnsignedLongToLong();
  }
  
  private static void parseMoof(Atom.ContainerAtom paramContainerAtom, SparseArray<TrackBundle> paramSparseArray, int paramInt, byte[] paramArrayOfByte)
    throws ParserException
  {
    int j = paramContainerAtom.containerChildren.size();
    int i = 0;
    while (i < j)
    {
      Atom.ContainerAtom localContainerAtom = (Atom.ContainerAtom)paramContainerAtom.containerChildren.get(i);
      if (localContainerAtom.type == Atom.TYPE_traf) {
        parseTraf(localContainerAtom, paramSparseArray, paramInt, paramArrayOfByte);
      }
      i += 1;
    }
  }
  
  private static void parseSaio(ParsableByteArray paramParsableByteArray, TrackFragment paramTrackFragment)
    throws ParserException
  {
    paramParsableByteArray.setPosition(8);
    int i = paramParsableByteArray.readInt();
    if ((Atom.parseFullAtomFlags(i) & 0x1) == 1) {
      paramParsableByteArray.skipBytes(8);
    }
    int j = paramParsableByteArray.readUnsignedIntToInt();
    if (j != 1) {
      throw new ParserException("Unexpected saio entry count: " + j);
    }
    i = Atom.parseFullAtomVersion(i);
    long l2 = paramTrackFragment.auxiliaryDataPosition;
    if (i == 0) {}
    for (long l1 = paramParsableByteArray.readUnsignedInt();; l1 = paramParsableByteArray.readUnsignedLongToLong())
    {
      paramTrackFragment.auxiliaryDataPosition = (l1 + l2);
      return;
    }
  }
  
  private static void parseSaiz(TrackEncryptionBox paramTrackEncryptionBox, ParsableByteArray paramParsableByteArray, TrackFragment paramTrackFragment)
    throws ParserException
  {
    int n = paramTrackEncryptionBox.initializationVectorSize;
    paramParsableByteArray.setPosition(8);
    if ((Atom.parseFullAtomFlags(paramParsableByteArray.readInt()) & 0x1) == 1) {
      paramParsableByteArray.skipBytes(8);
    }
    int j = paramParsableByteArray.readUnsignedByte();
    int m = paramParsableByteArray.readUnsignedIntToInt();
    if (m != paramTrackFragment.length) {
      throw new ParserException("Length mismatch: " + m + ", " + paramTrackFragment.length);
    }
    int i = 0;
    int k;
    if (j == 0)
    {
      paramTrackEncryptionBox = paramTrackFragment.sampleHasSubsampleEncryptionTable;
      j = 0;
      k = i;
      if (j < m)
      {
        k = paramParsableByteArray.readUnsignedByte();
        i += k;
        if (k > n) {}
        for (bool = true;; bool = false)
        {
          paramTrackEncryptionBox[j] = bool;
          j += 1;
          break;
        }
      }
    }
    else
    {
      if (j <= n) {
        break label199;
      }
    }
    label199:
    for (boolean bool = true;; bool = false)
    {
      k = 0 + j * m;
      Arrays.fill(paramTrackFragment.sampleHasSubsampleEncryptionTable, 0, m, bool);
      paramTrackFragment.initEncryptionData(k);
      return;
    }
  }
  
  private static void parseSenc(ParsableByteArray paramParsableByteArray, int paramInt, TrackFragment paramTrackFragment)
    throws ParserException
  {
    paramParsableByteArray.setPosition(paramInt + 8);
    paramInt = Atom.parseFullAtomFlags(paramParsableByteArray.readInt());
    if ((paramInt & 0x1) != 0) {
      throw new ParserException("Overriding TrackEncryptionBox parameters is unsupported.");
    }
    if ((paramInt & 0x2) != 0) {}
    for (boolean bool = true;; bool = false)
    {
      paramInt = paramParsableByteArray.readUnsignedIntToInt();
      if (paramInt == paramTrackFragment.length) {
        break;
      }
      throw new ParserException("Length mismatch: " + paramInt + ", " + paramTrackFragment.length);
    }
    Arrays.fill(paramTrackFragment.sampleHasSubsampleEncryptionTable, 0, paramInt, bool);
    paramTrackFragment.initEncryptionData(paramParsableByteArray.bytesLeft());
    paramTrackFragment.fillEncryptionData(paramParsableByteArray);
  }
  
  private static void parseSenc(ParsableByteArray paramParsableByteArray, TrackFragment paramTrackFragment)
    throws ParserException
  {
    parseSenc(paramParsableByteArray, 0, paramTrackFragment);
  }
  
  private static void parseSgpd(ParsableByteArray paramParsableByteArray1, ParsableByteArray paramParsableByteArray2, TrackFragment paramTrackFragment)
    throws ParserException
  {
    paramParsableByteArray1.setPosition(8);
    int i = paramParsableByteArray1.readInt();
    if (paramParsableByteArray1.readInt() != SAMPLE_GROUP_TYPE_seig) {}
    for (;;)
    {
      return;
      if (Atom.parseFullAtomVersion(i) == 1) {
        paramParsableByteArray1.skipBytes(4);
      }
      if (paramParsableByteArray1.readInt() != 1) {
        throw new ParserException("Entry count in sbgp != 1 (unsupported).");
      }
      paramParsableByteArray2.setPosition(8);
      i = paramParsableByteArray2.readInt();
      if (paramParsableByteArray2.readInt() == SAMPLE_GROUP_TYPE_seig)
      {
        i = Atom.parseFullAtomVersion(i);
        if (i == 1)
        {
          if (paramParsableByteArray2.readUnsignedInt() == 0L) {
            throw new ParserException("Variable length decription in sgpd found (unsupported)");
          }
        }
        else if (i >= 2) {
          paramParsableByteArray2.skipBytes(4);
        }
        if (paramParsableByteArray2.readUnsignedInt() != 1L) {
          throw new ParserException("Entry count in sgpd != 1 (unsupported).");
        }
        paramParsableByteArray2.skipBytes(2);
        if (paramParsableByteArray2.readUnsignedByte() == 1) {}
        for (boolean bool = true; bool; bool = false)
        {
          i = paramParsableByteArray2.readUnsignedByte();
          paramParsableByteArray1 = new byte[16];
          paramParsableByteArray2.readBytes(paramParsableByteArray1, 0, paramParsableByteArray1.length);
          paramTrackFragment.definesEncryptionData = true;
          paramTrackFragment.trackEncryptionBox = new TrackEncryptionBox(bool, i, paramParsableByteArray1);
          return;
        }
      }
    }
  }
  
  private static ChunkIndex parseSidx(ParsableByteArray paramParsableByteArray, long paramLong)
    throws ParserException
  {
    paramParsableByteArray.setPosition(8);
    int i = Atom.parseFullAtomVersion(paramParsableByteArray.readInt());
    paramParsableByteArray.skipBytes(4);
    long l3 = paramParsableByteArray.readUnsignedInt();
    long l1;
    int j;
    int[] arrayOfInt;
    long[] arrayOfLong1;
    long[] arrayOfLong2;
    long[] arrayOfLong3;
    long l2;
    if (i == 0)
    {
      l1 = paramParsableByteArray.readUnsignedInt();
      paramLong += paramParsableByteArray.readUnsignedInt();
      paramParsableByteArray.skipBytes(2);
      j = paramParsableByteArray.readUnsignedShort();
      arrayOfInt = new int[j];
      arrayOfLong1 = new long[j];
      arrayOfLong2 = new long[j];
      arrayOfLong3 = new long[j];
      l2 = Util.scaleLargeTimestamp(l1, 1000000L, l3);
      i = 0;
    }
    for (;;)
    {
      if (i >= j) {
        break label216;
      }
      int k = paramParsableByteArray.readInt();
      if ((0x80000000 & k) != 0)
      {
        throw new ParserException("Unhandled indirect reference");
        l1 = paramParsableByteArray.readUnsignedLongToLong();
        paramLong += paramParsableByteArray.readUnsignedLongToLong();
        break;
      }
      long l4 = paramParsableByteArray.readUnsignedInt();
      arrayOfInt[i] = (0x7FFFFFFF & k);
      arrayOfLong1[i] = paramLong;
      arrayOfLong3[i] = l2;
      l1 += l4;
      l2 = Util.scaleLargeTimestamp(l1, 1000000L, l3);
      arrayOfLong2[i] = (l2 - arrayOfLong3[i]);
      paramParsableByteArray.skipBytes(4);
      paramLong += arrayOfInt[i];
      i += 1;
    }
    label216:
    return new ChunkIndex(arrayOfInt, arrayOfLong1, arrayOfLong2, arrayOfLong3);
  }
  
  private static long parseTfdt(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.setPosition(8);
    if (Atom.parseFullAtomVersion(paramParsableByteArray.readInt()) == 1) {
      return paramParsableByteArray.readUnsignedLongToLong();
    }
    return paramParsableByteArray.readUnsignedInt();
  }
  
  private static TrackBundle parseTfhd(ParsableByteArray paramParsableByteArray, SparseArray<TrackBundle> paramSparseArray, int paramInt)
  {
    paramParsableByteArray.setPosition(8);
    int k = Atom.parseFullAtomFlags(paramParsableByteArray.readInt());
    int i = paramParsableByteArray.readInt();
    if ((paramInt & 0x4) == 0) {}
    for (paramInt = i;; paramInt = 0)
    {
      paramSparseArray = (TrackBundle)paramSparseArray.get(paramInt);
      if (paramSparseArray != null) {
        break;
      }
      return null;
    }
    if ((k & 0x1) != 0)
    {
      long l = paramParsableByteArray.readUnsignedLongToLong();
      paramSparseArray.fragment.dataPosition = l;
      paramSparseArray.fragment.auxiliaryDataPosition = l;
    }
    DefaultSampleValues localDefaultSampleValues = paramSparseArray.defaultSampleValues;
    label112:
    int j;
    if ((k & 0x2) != 0)
    {
      paramInt = paramParsableByteArray.readUnsignedIntToInt() - 1;
      if ((k & 0x8) == 0) {
        break label171;
      }
      i = paramParsableByteArray.readUnsignedIntToInt();
      if ((k & 0x10) == 0) {
        break label180;
      }
      j = paramParsableByteArray.readUnsignedIntToInt();
      label126:
      if ((k & 0x20) == 0) {
        break label190;
      }
    }
    label171:
    label180:
    label190:
    for (k = paramParsableByteArray.readUnsignedIntToInt();; k = localDefaultSampleValues.flags)
    {
      paramSparseArray.fragment.header = new DefaultSampleValues(paramInt, i, j, k);
      return paramSparseArray;
      paramInt = localDefaultSampleValues.sampleDescriptionIndex;
      break;
      i = localDefaultSampleValues.duration;
      break label112;
      j = localDefaultSampleValues.size;
      break label126;
    }
  }
  
  private static void parseTraf(Atom.ContainerAtom paramContainerAtom, SparseArray<TrackBundle> paramSparseArray, int paramInt, byte[] paramArrayOfByte)
    throws ParserException
  {
    if (paramContainerAtom.getChildAtomOfTypeCount(Atom.TYPE_trun) != 1) {
      throw new ParserException("Trun count in traf != 1 (unsupported).");
    }
    Object localObject = parseTfhd(paramContainerAtom.getLeafAtomOfType(Atom.TYPE_tfhd).data, paramSparseArray, paramInt);
    if (localObject == null) {}
    for (;;)
    {
      return;
      paramSparseArray = ((TrackBundle)localObject).fragment;
      long l2 = paramSparseArray.nextFragmentDecodeTime;
      ((TrackBundle)localObject).reset();
      long l1 = l2;
      if (paramContainerAtom.getLeafAtomOfType(Atom.TYPE_tfdt) != null)
      {
        l1 = l2;
        if ((paramInt & 0x2) == 0) {
          l1 = parseTfdt(paramContainerAtom.getLeafAtomOfType(Atom.TYPE_tfdt).data);
        }
      }
      parseTrun((TrackBundle)localObject, l1, paramInt, paramContainerAtom.getLeafAtomOfType(Atom.TYPE_trun).data);
      Atom.LeafAtom localLeafAtom = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_saiz);
      if (localLeafAtom != null) {
        parseSaiz(localObject.track.sampleDescriptionEncryptionBoxes[paramSparseArray.header.sampleDescriptionIndex], localLeafAtom.data, paramSparseArray);
      }
      localObject = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_saio);
      if (localObject != null) {
        parseSaio(((Atom.LeafAtom)localObject).data, paramSparseArray);
      }
      localObject = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_senc);
      if (localObject != null) {
        parseSenc(((Atom.LeafAtom)localObject).data, paramSparseArray);
      }
      localObject = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_sbgp);
      localLeafAtom = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_sgpd);
      if ((localObject != null) && (localLeafAtom != null)) {
        parseSgpd(((Atom.LeafAtom)localObject).data, localLeafAtom.data, paramSparseArray);
      }
      int i = paramContainerAtom.leafChildren.size();
      paramInt = 0;
      while (paramInt < i)
      {
        localObject = (Atom.LeafAtom)paramContainerAtom.leafChildren.get(paramInt);
        if (((Atom.LeafAtom)localObject).type == Atom.TYPE_uuid) {
          parseUuid(((Atom.LeafAtom)localObject).data, paramSparseArray, paramArrayOfByte);
        }
        paramInt += 1;
      }
    }
  }
  
  private static Pair<Integer, DefaultSampleValues> parseTrex(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.setPosition(12);
    return Pair.create(Integer.valueOf(paramParsableByteArray.readInt()), new DefaultSampleValues(paramParsableByteArray.readUnsignedIntToInt() - 1, paramParsableByteArray.readUnsignedIntToInt(), paramParsableByteArray.readUnsignedIntToInt(), paramParsableByteArray.readInt()));
  }
  
  private static void parseTrun(TrackBundle paramTrackBundle, long paramLong, int paramInt, ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.setPosition(8);
    int i1 = Atom.parseFullAtomFlags(paramParsableByteArray.readInt());
    Track localTrack = paramTrackBundle.track;
    paramTrackBundle = paramTrackBundle.fragment;
    DefaultSampleValues localDefaultSampleValues = paramTrackBundle.header;
    int i6 = paramParsableByteArray.readUnsignedIntToInt();
    if ((i1 & 0x1) != 0) {
      paramTrackBundle.dataPosition += paramParsableByteArray.readInt();
    }
    int j;
    int k;
    label104:
    int m;
    label116:
    int n;
    label128:
    label140:
    int[] arrayOfInt2;
    boolean[] arrayOfBoolean;
    int i2;
    label262:
    int i3;
    label265:
    int i4;
    label284:
    int i5;
    if ((i1 & 0x4) != 0)
    {
      j = 1;
      int i = localDefaultSampleValues.flags;
      if (j != 0) {
        i = paramParsableByteArray.readUnsignedIntToInt();
      }
      if ((i1 & 0x100) == 0) {
        break label407;
      }
      k = 1;
      if ((i1 & 0x200) == 0) {
        break label413;
      }
      m = 1;
      if ((i1 & 0x400) == 0) {
        break label419;
      }
      n = 1;
      if ((i1 & 0x800) == 0) {
        break label425;
      }
      i1 = 1;
      long l2 = 0L;
      long l1 = l2;
      if (localTrack.editListDurations != null)
      {
        l1 = l2;
        if (localTrack.editListDurations.length == 1)
        {
          l1 = l2;
          if (localTrack.editListDurations[0] == 0L) {
            l1 = Util.scaleLargeTimestamp(localTrack.editListMediaTimes[0], 1000L, localTrack.timescale);
          }
        }
      }
      paramTrackBundle.initTables(i6);
      int[] arrayOfInt1 = paramTrackBundle.sampleSizeTable;
      arrayOfInt2 = paramTrackBundle.sampleCompositionTimeOffsetTable;
      long[] arrayOfLong = paramTrackBundle.sampleDecodingTimeTable;
      arrayOfBoolean = paramTrackBundle.sampleIsSyncFrameTable;
      l2 = localTrack.timescale;
      if ((localTrack.type != Track.TYPE_vide) || ((paramInt & 0x1) == 0)) {
        break label431;
      }
      i2 = 1;
      i3 = 0;
      if (i3 >= i6) {
        break label495;
      }
      if (k == 0) {
        break label437;
      }
      i4 = paramParsableByteArray.readUnsignedIntToInt();
      if (m == 0) {
        break label447;
      }
      i5 = paramParsableByteArray.readUnsignedIntToInt();
      label296:
      if ((i3 != 0) || (j == 0)) {
        break label457;
      }
      paramInt = i;
      label309:
      if (i1 == 0) {
        break label480;
      }
      arrayOfInt2[i3] = ((int)(paramParsableByteArray.readInt() * 1000 / l2));
      label333:
      arrayOfLong[i3] = (Util.scaleLargeTimestamp(paramLong, 1000L, l2) - l1);
      arrayOfInt1[i3] = i5;
      if (((paramInt >> 16 & 0x1) != 0) || ((i2 != 0) && (i3 != 0))) {
        break label489;
      }
    }
    label407:
    label413:
    label419:
    label425:
    label431:
    label437:
    label447:
    label457:
    label480:
    label489:
    for (int i7 = 1;; i7 = 0)
    {
      arrayOfBoolean[i3] = i7;
      paramLong += i4;
      i3 += 1;
      break label265;
      j = 0;
      break;
      k = 0;
      break label104;
      m = 0;
      break label116;
      n = 0;
      break label128;
      i1 = 0;
      break label140;
      i2 = 0;
      break label262;
      i4 = localDefaultSampleValues.duration;
      break label284;
      i5 = localDefaultSampleValues.size;
      break label296;
      if (n != 0)
      {
        paramInt = paramParsableByteArray.readInt();
        break label309;
      }
      paramInt = localDefaultSampleValues.flags;
      break label309;
      arrayOfInt2[i3] = 0;
      break label333;
    }
    label495:
    paramTrackBundle.nextFragmentDecodeTime = paramLong;
  }
  
  private static void parseUuid(ParsableByteArray paramParsableByteArray, TrackFragment paramTrackFragment, byte[] paramArrayOfByte)
    throws ParserException
  {
    paramParsableByteArray.setPosition(8);
    paramParsableByteArray.readBytes(paramArrayOfByte, 0, 16);
    if (!Arrays.equals(paramArrayOfByte, PIFF_SAMPLE_ENCRYPTION_BOX_EXTENDED_TYPE)) {
      return;
    }
    parseSenc(paramParsableByteArray, 16, paramTrackFragment);
  }
  
  private void processAtomEnded(long paramLong)
    throws ParserException
  {
    while ((!this.containerAtoms.isEmpty()) && (((Atom.ContainerAtom)this.containerAtoms.peek()).endPosition == paramLong)) {
      onContainerAtomRead((Atom.ContainerAtom)this.containerAtoms.pop());
    }
    enterReadingAtomHeaderState();
  }
  
  private boolean readAtomHeader(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    if (this.atomHeaderBytesRead == 0)
    {
      if (!paramExtractorInput.readFully(this.atomHeader.data, 0, 8, true)) {
        return false;
      }
      this.atomHeaderBytesRead = 8;
      this.atomHeader.setPosition(0);
      this.atomSize = this.atomHeader.readUnsignedInt();
      this.atomType = this.atomHeader.readInt();
    }
    if (this.atomSize == 1L)
    {
      paramExtractorInput.readFully(this.atomHeader.data, 8, 8);
      this.atomHeaderBytesRead += 8;
      this.atomSize = this.atomHeader.readUnsignedLongToLong();
    }
    long l = paramExtractorInput.getPosition() - this.atomHeaderBytesRead;
    if (this.atomType == Atom.TYPE_moof)
    {
      int j = this.trackBundles.size();
      int i = 0;
      while (i < j)
      {
        TrackFragment localTrackFragment = ((TrackBundle)this.trackBundles.valueAt(i)).fragment;
        localTrackFragment.auxiliaryDataPosition = l;
        localTrackFragment.dataPosition = l;
        i += 1;
      }
    }
    if (this.atomType == Atom.TYPE_mdat)
    {
      this.currentTrackBundle = null;
      this.endOfMdatPosition = (this.atomSize + l);
      if (!this.haveOutputSeekMap)
      {
        this.extractorOutput.seekMap(SeekMap.UNSEEKABLE);
        this.haveOutputSeekMap = true;
      }
      this.parserState = 2;
      return true;
    }
    if (shouldParseContainerAtom(this.atomType))
    {
      l = paramExtractorInput.getPosition() + this.atomSize - 8L;
      this.containerAtoms.add(new Atom.ContainerAtom(this.atomType, l));
      if (this.atomSize == this.atomHeaderBytesRead) {
        processAtomEnded(l);
      }
    }
    for (;;)
    {
      return true;
      enterReadingAtomHeaderState();
      continue;
      if (shouldParseLeafAtom(this.atomType))
      {
        if (this.atomHeaderBytesRead != 8) {
          throw new ParserException("Leaf atom defines extended atom size (unsupported).");
        }
        if (this.atomSize > 2147483647L) {
          throw new ParserException("Leaf atom with length > 2147483647 (unsupported).");
        }
        this.atomData = new ParsableByteArray((int)this.atomSize);
        System.arraycopy(this.atomHeader.data, 0, this.atomData.data, 0, 8);
        this.parserState = 1;
      }
      else
      {
        if (this.atomSize > 2147483647L) {
          throw new ParserException("Skipping atom with length > 2147483647 (unsupported).");
        }
        this.atomData = null;
        this.parserState = 1;
      }
    }
  }
  
  private void readAtomPayload(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    int i = (int)this.atomSize - this.atomHeaderBytesRead;
    if (this.atomData != null)
    {
      paramExtractorInput.readFully(this.atomData.data, 8, i);
      onLeafAtomRead(new Atom.LeafAtom(this.atomType, this.atomData), paramExtractorInput.getPosition());
    }
    for (;;)
    {
      processAtomEnded(paramExtractorInput.getPosition());
      return;
      paramExtractorInput.skipFully(i);
    }
  }
  
  private void readEncryptionData(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    Object localObject1 = null;
    long l1 = Long.MAX_VALUE;
    int j = this.trackBundles.size();
    int i = 0;
    while (i < j)
    {
      TrackFragment localTrackFragment = ((TrackBundle)this.trackBundles.valueAt(i)).fragment;
      long l2 = l1;
      Object localObject2 = localObject1;
      if (localTrackFragment.sampleEncryptionDataNeedsFill)
      {
        l2 = l1;
        localObject2 = localObject1;
        if (localTrackFragment.auxiliaryDataPosition < l1)
        {
          l2 = localTrackFragment.auxiliaryDataPosition;
          localObject2 = (TrackBundle)this.trackBundles.valueAt(i);
        }
      }
      i += 1;
      l1 = l2;
      localObject1 = localObject2;
    }
    if (localObject1 == null)
    {
      this.parserState = 3;
      return;
    }
    i = (int)(l1 - paramExtractorInput.getPosition());
    if (i < 0) {
      throw new ParserException("Offset to encryption data was negative.");
    }
    paramExtractorInput.skipFully(i);
    ((TrackBundle)localObject1).fragment.fillEncryptionData(paramExtractorInput);
  }
  
  private boolean readSample(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    int i;
    TrackFragment localTrackFragment;
    Track localTrack;
    TrackOutput localTrackOutput;
    int j;
    int k;
    if (this.parserState == 3)
    {
      if (this.currentTrackBundle == null)
      {
        this.currentTrackBundle = getNextFragmentRun(this.trackBundles);
        if (this.currentTrackBundle == null)
        {
          i = (int)(this.endOfMdatPosition - paramExtractorInput.getPosition());
          if (i < 0) {
            throw new ParserException("Offset to end of mdat was negative.");
          }
          paramExtractorInput.skipFully(i);
          enterReadingAtomHeaderState();
          return false;
        }
        i = (int)(this.currentTrackBundle.fragment.dataPosition - paramExtractorInput.getPosition());
        if (i < 0) {
          throw new ParserException("Offset to sample data was negative.");
        }
        paramExtractorInput.skipFully(i);
      }
      this.sampleSize = this.currentTrackBundle.fragment.sampleSizeTable[this.currentTrackBundle.currentSampleIndex];
      if (this.currentTrackBundle.fragment.definesEncryptionData)
      {
        this.sampleBytesWritten = appendSampleEncryptionData(this.currentTrackBundle);
        this.sampleSize += this.sampleBytesWritten;
        this.parserState = 4;
        this.sampleCurrentNalBytesRemaining = 0;
      }
    }
    else
    {
      localTrackFragment = this.currentTrackBundle.fragment;
      localTrack = this.currentTrackBundle.track;
      localTrackOutput = this.currentTrackBundle.output;
      j = this.currentTrackBundle.currentSampleIndex;
      if (localTrack.nalUnitLengthFieldLength == -1) {
        break label413;
      }
      byte[] arrayOfByte = this.nalLength.data;
      arrayOfByte[0] = 0;
      arrayOfByte[1] = 0;
      arrayOfByte[2] = 0;
      i = localTrack.nalUnitLengthFieldLength;
      k = 4 - localTrack.nalUnitLengthFieldLength;
    }
    for (;;)
    {
      if (this.sampleBytesWritten >= this.sampleSize) {
        break label456;
      }
      if (this.sampleCurrentNalBytesRemaining == 0)
      {
        paramExtractorInput.readFully(this.nalLength.data, k, i);
        this.nalLength.setPosition(0);
        this.sampleCurrentNalBytesRemaining = this.nalLength.readUnsignedIntToInt();
        this.nalStartCode.setPosition(0);
        localTrackOutput.sampleData(this.nalStartCode, 4);
        this.sampleBytesWritten += 4;
        this.sampleSize += k;
        continue;
        this.sampleBytesWritten = 0;
        break;
      }
      int m = localTrackOutput.sampleData(paramExtractorInput, this.sampleCurrentNalBytesRemaining, false);
      this.sampleBytesWritten += m;
      this.sampleCurrentNalBytesRemaining -= m;
    }
    label413:
    while (this.sampleBytesWritten < this.sampleSize)
    {
      i = localTrackOutput.sampleData(paramExtractorInput, this.sampleSize - this.sampleBytesWritten, false);
      this.sampleBytesWritten += i;
    }
    label456:
    long l = localTrackFragment.getSamplePresentationTime(j);
    if (localTrackFragment.definesEncryptionData)
    {
      i = 2;
      if (localTrackFragment.sampleIsSyncFrameTable[j] == 0) {
        break label592;
      }
      j = 1;
      label486:
      k = localTrackFragment.header.sampleDescriptionIndex;
      paramExtractorInput = null;
      if (localTrackFragment.definesEncryptionData) {
        if (localTrackFragment.trackEncryptionBox == null) {
          break label597;
        }
      }
    }
    label592:
    label597:
    for (paramExtractorInput = localTrackFragment.trackEncryptionBox.keyId;; paramExtractorInput = localTrack.sampleDescriptionEncryptionBoxes[k].keyId)
    {
      localTrackOutput.sampleMetadata(l * 1000L, i | j, this.sampleSize, 0, paramExtractorInput);
      paramExtractorInput = this.currentTrackBundle;
      paramExtractorInput.currentSampleIndex += 1;
      if (this.currentTrackBundle.currentSampleIndex == localTrackFragment.length) {
        this.currentTrackBundle = null;
      }
      this.parserState = 3;
      return true;
      i = 0;
      break;
      j = 0;
      break label486;
    }
  }
  
  private static boolean shouldParseContainerAtom(int paramInt)
  {
    return (paramInt == Atom.TYPE_moov) || (paramInt == Atom.TYPE_trak) || (paramInt == Atom.TYPE_mdia) || (paramInt == Atom.TYPE_minf) || (paramInt == Atom.TYPE_stbl) || (paramInt == Atom.TYPE_moof) || (paramInt == Atom.TYPE_traf) || (paramInt == Atom.TYPE_mvex) || (paramInt == Atom.TYPE_edts);
  }
  
  private static boolean shouldParseLeafAtom(int paramInt)
  {
    return (paramInt == Atom.TYPE_hdlr) || (paramInt == Atom.TYPE_mdhd) || (paramInt == Atom.TYPE_mvhd) || (paramInt == Atom.TYPE_sidx) || (paramInt == Atom.TYPE_stsd) || (paramInt == Atom.TYPE_tfdt) || (paramInt == Atom.TYPE_tfhd) || (paramInt == Atom.TYPE_tkhd) || (paramInt == Atom.TYPE_trex) || (paramInt == Atom.TYPE_trun) || (paramInt == Atom.TYPE_pssh) || (paramInt == Atom.TYPE_saiz) || (paramInt == Atom.TYPE_saio) || (paramInt == Atom.TYPE_senc) || (paramInt == Atom.TYPE_sbgp) || (paramInt == Atom.TYPE_sgpd) || (paramInt == Atom.TYPE_uuid) || (paramInt == Atom.TYPE_elst) || (paramInt == Atom.TYPE_mehd) || (paramInt == Atom.TYPE_emsg);
  }
  
  public final void init(ExtractorOutput paramExtractorOutput)
  {
    this.extractorOutput = paramExtractorOutput;
    if (this.sideloadedTrack != null)
    {
      paramExtractorOutput = new TrackBundle(paramExtractorOutput.track(0));
      paramExtractorOutput.init(this.sideloadedTrack, new DefaultSampleValues(0, 0, 0, 0));
      this.trackBundles.put(0, paramExtractorOutput);
      this.extractorOutput.endTracks();
    }
  }
  
  protected void parseEmsg(ParsableByteArray paramParsableByteArray, long paramLong)
    throws ParserException
  {}
  
  public final int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    for (;;)
    {
      switch (this.parserState)
      {
      default: 
        if (readSample(paramExtractorInput)) {
          return 0;
        }
        break;
      case 0: 
        if (!readAtomHeader(paramExtractorInput)) {
          return -1;
        }
        break;
      case 1: 
        readAtomPayload(paramExtractorInput);
        break;
      case 2: 
        readEncryptionData(paramExtractorInput);
      }
    }
  }
  
  public final void release() {}
  
  public final void seek()
  {
    int j = this.trackBundles.size();
    int i = 0;
    while (i < j)
    {
      ((TrackBundle)this.trackBundles.valueAt(i)).reset();
      i += 1;
    }
    this.containerAtoms.clear();
    enterReadingAtomHeaderState();
  }
  
  public final boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    return Sniffer.sniffFragmented(paramExtractorInput);
  }
  
  private static final class TrackBundle
  {
    public int currentSampleIndex;
    public DefaultSampleValues defaultSampleValues;
    public final TrackFragment fragment = new TrackFragment();
    public final TrackOutput output;
    public Track track;
    
    public TrackBundle(TrackOutput paramTrackOutput)
    {
      this.output = paramTrackOutput;
    }
    
    public void init(Track paramTrack, DefaultSampleValues paramDefaultSampleValues)
    {
      this.track = ((Track)Assertions.checkNotNull(paramTrack));
      this.defaultSampleValues = ((DefaultSampleValues)Assertions.checkNotNull(paramDefaultSampleValues));
      this.output.format(paramTrack.mediaFormat);
      reset();
    }
    
    public void reset()
    {
      this.fragment.reset();
      this.currentSampleIndex = 0;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/mp4/FragmentedMp4Extractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */