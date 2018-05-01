package org.telegram.messenger.exoplayer2.extractor.mp4;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.telegram.messenger.exoplayer2.extractor.GaplessInfoHolder;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.SeekPoints;
import org.telegram.messenger.exoplayer2.extractor.SeekPoint;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.NalUnitUtil;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class Mp4Extractor
  implements Extractor, SeekMap
{
  private static final int BRAND_QUICKTIME = Util.getIntegerCodeForString("qt  ");
  public static final ExtractorsFactory FACTORY = new ExtractorsFactory()
  {
    public Extractor[] createExtractors()
    {
      return new Extractor[] { new Mp4Extractor() };
    }
  };
  public static final int FLAG_WORKAROUND_IGNORE_EDIT_LISTS = 1;
  private static final long MAXIMUM_READ_AHEAD_BYTES_STREAM = 10485760L;
  private static final long RELOAD_MINIMUM_SEEK_DISTANCE = 262144L;
  private static final int STATE_READING_ATOM_HEADER = 0;
  private static final int STATE_READING_ATOM_PAYLOAD = 1;
  private static final int STATE_READING_SAMPLE = 2;
  private long[][] accumulatedSampleSizes;
  private ParsableByteArray atomData;
  private final ParsableByteArray atomHeader;
  private int atomHeaderBytesRead;
  private long atomSize;
  private int atomType;
  private final Stack<Atom.ContainerAtom> containerAtoms;
  private long durationUs;
  private ExtractorOutput extractorOutput;
  private int firstVideoTrackIndex;
  private final int flags;
  private boolean isQuickTime;
  private final ParsableByteArray nalLength;
  private final ParsableByteArray nalStartCode;
  private int parserState;
  private int sampleBytesWritten;
  private int sampleCurrentNalBytesRemaining;
  private int sampleTrackIndex;
  private Mp4Track[] tracks;
  
  public Mp4Extractor()
  {
    this(0);
  }
  
  public Mp4Extractor(int paramInt)
  {
    this.flags = paramInt;
    this.atomHeader = new ParsableByteArray(16);
    this.containerAtoms = new Stack();
    this.nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
    this.nalLength = new ParsableByteArray(4);
    this.sampleTrackIndex = -1;
  }
  
  private static long[][] calculateAccumulatedSampleSizes(Mp4Track[] paramArrayOfMp4Track)
  {
    long[][] arrayOfLong = new long[paramArrayOfMp4Track.length][];
    int[] arrayOfInt = new int[paramArrayOfMp4Track.length];
    long[] arrayOfLong1 = new long[paramArrayOfMp4Track.length];
    boolean[] arrayOfBoolean = new boolean[paramArrayOfMp4Track.length];
    for (int i = 0; i < paramArrayOfMp4Track.length; i++)
    {
      arrayOfLong[i] = new long[paramArrayOfMp4Track[i].sampleTable.sampleCount];
      arrayOfLong1[i] = paramArrayOfMp4Track[i].sampleTable.timestampsUs[0];
    }
    long l1 = 0L;
    int j = 0;
    while (j < paramArrayOfMp4Track.length)
    {
      long l2 = Long.MAX_VALUE;
      int k = -1;
      i = 0;
      while (i < paramArrayOfMp4Track.length)
      {
        int m = k;
        long l3 = l2;
        if (arrayOfBoolean[i] == 0)
        {
          m = k;
          l3 = l2;
          if (arrayOfLong1[i] <= l2)
          {
            m = i;
            l3 = arrayOfLong1[i];
          }
        }
        i++;
        k = m;
        l2 = l3;
      }
      i = arrayOfInt[k];
      arrayOfLong[k][i] = l1;
      l1 += paramArrayOfMp4Track[k].sampleTable.sizes[i];
      i++;
      arrayOfInt[k] = i;
      if (i < arrayOfLong[k].length)
      {
        arrayOfLong1[k] = paramArrayOfMp4Track[k].sampleTable.timestampsUs[i];
      }
      else
      {
        arrayOfBoolean[k] = true;
        j++;
      }
    }
    return arrayOfLong;
  }
  
  private void enterReadingAtomHeaderState()
  {
    this.parserState = 0;
    this.atomHeaderBytesRead = 0;
  }
  
  private static int getSynchronizationSampleIndex(TrackSampleTable paramTrackSampleTable, long paramLong)
  {
    int i = paramTrackSampleTable.getIndexOfEarlierOrEqualSynchronizationSample(paramLong);
    int j = i;
    if (i == -1) {
      j = paramTrackSampleTable.getIndexOfLaterOrEqualSynchronizationSample(paramLong);
    }
    return j;
  }
  
  private int getTrackIndexOfNextReadSample(long paramLong)
  {
    long l1 = Long.MAX_VALUE;
    int i = 1;
    int j = -1;
    long l2 = Long.MAX_VALUE;
    long l3 = Long.MAX_VALUE;
    int k = 1;
    int m = -1;
    int n = 0;
    while (n < this.tracks.length)
    {
      Mp4Track localMp4Track = this.tracks[n];
      int i1 = localMp4Track.sampleIndex;
      long l4;
      if (i1 == localMp4Track.sampleTable.sampleCount)
      {
        l4 = l3;
        n++;
        l3 = l4;
      }
      else
      {
        long l5 = localMp4Track.sampleTable.offsets[i1];
        long l6 = this.accumulatedSampleSizes[n][i1];
        l4 = l5 - paramLong;
        if ((l4 < 0L) || (l4 >= 262144L)) {}
        for (i1 = 1;; i1 = 0)
        {
          long l7;
          int i2;
          int i3;
          if ((i1 != 0) || (i == 0))
          {
            l7 = l2;
            i2 = i;
            i3 = j;
            l5 = l1;
            if (i1 == i)
            {
              l7 = l2;
              i2 = i;
              i3 = j;
              l5 = l1;
              if (l4 >= l1) {}
            }
          }
          else
          {
            i2 = i1;
            l5 = l4;
            i3 = n;
            l7 = l6;
          }
          l4 = l3;
          l2 = l7;
          i = i2;
          j = i3;
          l1 = l5;
          if (l6 >= l3) {
            break;
          }
          m = n;
          l4 = l6;
          k = i1;
          l2 = l7;
          i = i2;
          j = i3;
          l1 = l5;
          break;
        }
      }
    }
    if ((l3 == Long.MAX_VALUE) || (k == 0) || (l2 < 10485760L + l3)) {
      m = j;
    }
    return m;
  }
  
  private static long maybeAdjustSeekOffset(TrackSampleTable paramTrackSampleTable, long paramLong1, long paramLong2)
  {
    int i = getSynchronizationSampleIndex(paramTrackSampleTable, paramLong1);
    if (i == -1) {}
    for (;;)
    {
      return paramLong2;
      paramLong2 = Math.min(paramTrackSampleTable.offsets[i], paramLong2);
    }
  }
  
  private void processAtomEnded(long paramLong)
    throws ParserException
  {
    while ((!this.containerAtoms.isEmpty()) && (((Atom.ContainerAtom)this.containerAtoms.peek()).endPosition == paramLong))
    {
      Atom.ContainerAtom localContainerAtom = (Atom.ContainerAtom)this.containerAtoms.pop();
      if (localContainerAtom.type == Atom.TYPE_moov)
      {
        processMoovAtom(localContainerAtom);
        this.containerAtoms.clear();
        this.parserState = 2;
      }
      else if (!this.containerAtoms.isEmpty())
      {
        ((Atom.ContainerAtom)this.containerAtoms.peek()).add(localContainerAtom);
      }
    }
    if (this.parserState != 2) {
      enterReadingAtomHeaderState();
    }
  }
  
  private static boolean processFtypAtom(ParsableByteArray paramParsableByteArray)
  {
    boolean bool = true;
    paramParsableByteArray.setPosition(8);
    if (paramParsableByteArray.readInt() == BRAND_QUICKTIME) {}
    for (;;)
    {
      return bool;
      paramParsableByteArray.skipBytes(4);
      for (;;)
      {
        if (paramParsableByteArray.bytesLeft() > 0) {
          if (paramParsableByteArray.readInt() == BRAND_QUICKTIME) {
            break;
          }
        }
      }
      bool = false;
    }
  }
  
  private void processMoovAtom(Atom.ContainerAtom paramContainerAtom)
    throws ParserException
  {
    int i = -1;
    long l1 = -9223372036854775807L;
    ArrayList localArrayList = new ArrayList();
    Object localObject1 = null;
    GaplessInfoHolder localGaplessInfoHolder = new GaplessInfoHolder();
    Object localObject2 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_udta);
    if (localObject2 != null)
    {
      localObject2 = AtomParsers.parseUdta((Atom.LeafAtom)localObject2, this.isQuickTime);
      localObject1 = localObject2;
      if (localObject2 != null)
      {
        localGaplessInfoHolder.setFromMetadata((Metadata)localObject2);
        localObject1 = localObject2;
      }
    }
    int j = 0;
    while (j < paramContainerAtom.containerChildren.size())
    {
      localObject2 = (Atom.ContainerAtom)paramContainerAtom.containerChildren.get(j);
      long l2;
      int k;
      if (((Atom.ContainerAtom)localObject2).type != Atom.TYPE_trak)
      {
        l2 = l1;
        k = i;
        j++;
        i = k;
        l1 = l2;
      }
      else
      {
        Object localObject3 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_mvhd);
        if ((this.flags & 0x1) != 0) {}
        for (boolean bool = true;; bool = false)
        {
          Track localTrack = AtomParsers.parseTrak((Atom.ContainerAtom)localObject2, (Atom.LeafAtom)localObject3, -9223372036854775807L, null, bool, this.isQuickTime);
          k = i;
          l2 = l1;
          if (localTrack == null) {
            break;
          }
          localObject2 = AtomParsers.parseStbl(localTrack, ((Atom.ContainerAtom)localObject2).getContainerAtomOfType(Atom.TYPE_mdia).getContainerAtomOfType(Atom.TYPE_minf).getContainerAtomOfType(Atom.TYPE_stbl), localGaplessInfoHolder);
          k = i;
          l2 = l1;
          if (((TrackSampleTable)localObject2).sampleCount == 0) {
            break;
          }
          Mp4Track localMp4Track = new Mp4Track(localTrack, (TrackSampleTable)localObject2, this.extractorOutput.track(j, localTrack.type));
          k = ((TrackSampleTable)localObject2).maximumSize;
          Format localFormat = localTrack.format.copyWithMaxInputSize(k + 30);
          localObject2 = localFormat;
          if (localTrack.type == 1)
          {
            localObject3 = localFormat;
            if (localGaplessInfoHolder.hasGaplessInfo()) {
              localObject3 = localFormat.copyWithGaplessInfo(localGaplessInfoHolder.encoderDelay, localGaplessInfoHolder.encoderPadding);
            }
            localObject2 = localObject3;
            if (localObject1 != null) {
              localObject2 = ((Format)localObject3).copyWithMetadata((Metadata)localObject1);
            }
          }
          localMp4Track.trackOutput.format((Format)localObject2);
          l2 = Math.max(l1, localTrack.durationUs);
          k = i;
          if (localTrack.type == 2)
          {
            k = i;
            if (i == -1) {
              k = localArrayList.size();
            }
          }
          localArrayList.add(localMp4Track);
          break;
        }
      }
    }
    this.firstVideoTrackIndex = i;
    this.durationUs = l1;
    this.tracks = ((Mp4Track[])localArrayList.toArray(new Mp4Track[localArrayList.size()]));
    this.accumulatedSampleSizes = calculateAccumulatedSampleSizes(this.tracks);
    this.extractorOutput.endTracks();
    this.extractorOutput.seekMap(this);
  }
  
  private boolean readAtomHeader(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    boolean bool = false;
    if (this.atomHeaderBytesRead == 0)
    {
      if (!paramExtractorInput.readFully(this.atomHeader.data, 0, 8, true)) {
        return bool;
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
    long l2;
    while (this.atomSize < this.atomHeaderBytesRead)
    {
      throw new ParserException("Atom size less than header length (unsupported).");
      if (this.atomSize == 0L)
      {
        long l1 = paramExtractorInput.getLength();
        l2 = l1;
        if (l1 == -1L)
        {
          l2 = l1;
          if (!this.containerAtoms.isEmpty()) {
            l2 = ((Atom.ContainerAtom)this.containerAtoms.peek()).endPosition;
          }
        }
        if (l2 != -1L) {
          this.atomSize = (l2 - paramExtractorInput.getPosition() + this.atomHeaderBytesRead);
        }
      }
    }
    if (shouldParseContainerAtom(this.atomType))
    {
      l2 = paramExtractorInput.getPosition() + this.atomSize - this.atomHeaderBytesRead;
      this.containerAtoms.add(new Atom.ContainerAtom(this.atomType, l2));
      if (this.atomSize == this.atomHeaderBytesRead) {
        processAtomEnded(l2);
      }
    }
    for (;;)
    {
      bool = true;
      break;
      enterReadingAtomHeaderState();
      continue;
      if (shouldParseLeafAtom(this.atomType))
      {
        if (this.atomHeaderBytesRead == 8)
        {
          bool = true;
          label327:
          Assertions.checkState(bool);
          if (this.atomSize > 2147483647L) {
            break label398;
          }
        }
        label398:
        for (bool = true;; bool = false)
        {
          Assertions.checkState(bool);
          this.atomData = new ParsableByteArray((int)this.atomSize);
          System.arraycopy(this.atomHeader.data, 0, this.atomData.data, 0, 8);
          this.parserState = 1;
          break;
          bool = false;
          break label327;
        }
      }
      this.atomData = null;
      this.parserState = 1;
    }
  }
  
  private boolean readAtomPayload(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    long l1 = this.atomSize - this.atomHeaderBytesRead;
    long l2 = paramExtractorInput.getPosition();
    int i = 0;
    int j;
    if (this.atomData != null)
    {
      paramExtractorInput.readFully(this.atomData.data, this.atomHeaderBytesRead, (int)l1);
      if (this.atomType == Atom.TYPE_ftyp)
      {
        this.isQuickTime = processFtypAtom(this.atomData);
        j = i;
        processAtomEnded(l2 + l1);
        if ((j == 0) || (this.parserState == 2)) {
          break label190;
        }
      }
    }
    label190:
    for (boolean bool = true;; bool = false)
    {
      return bool;
      j = i;
      if (this.containerAtoms.isEmpty()) {
        break;
      }
      ((Atom.ContainerAtom)this.containerAtoms.peek()).add(new Atom.LeafAtom(this.atomType, this.atomData));
      j = i;
      break;
      if (l1 < 262144L)
      {
        paramExtractorInput.skipFully((int)l1);
        j = i;
        break;
      }
      paramPositionHolder.position = (paramExtractorInput.getPosition() + l1);
      j = 1;
      break;
    }
  }
  
  private int readSample(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    long l1 = paramExtractorInput.getPosition();
    int i;
    if (this.sampleTrackIndex == -1)
    {
      this.sampleTrackIndex = getTrackIndexOfNextReadSample(l1);
      if (this.sampleTrackIndex == -1) {
        i = -1;
      }
    }
    for (;;)
    {
      return i;
      Mp4Track localMp4Track = this.tracks[this.sampleTrackIndex];
      TrackOutput localTrackOutput = localMp4Track.trackOutput;
      int j = localMp4Track.sampleIndex;
      long l2 = localMp4Track.sampleTable.offsets[j];
      int k = localMp4Track.sampleTable.sizes[j];
      l1 = l2 - l1 + this.sampleBytesWritten;
      if ((l1 < 0L) || (l1 >= 262144L))
      {
        paramPositionHolder.position = l2;
        i = 1;
      }
      else
      {
        i = k;
        l2 = l1;
        if (localMp4Track.track.sampleTransformation == 1)
        {
          l2 = l1 + 8L;
          i = k - 8;
        }
        paramExtractorInput.skipFully((int)l2);
        if (localMp4Track.track.nalUnitLengthFieldLength != 0)
        {
          paramPositionHolder = this.nalLength.data;
          paramPositionHolder[0] = ((byte)0);
          paramPositionHolder[1] = ((byte)0);
          paramPositionHolder[2] = ((byte)0);
          int m = localMp4Track.track.nalUnitLengthFieldLength;
          int n = 4 - localMp4Track.track.nalUnitLengthFieldLength;
          for (;;)
          {
            k = i;
            if (this.sampleBytesWritten >= i) {
              break;
            }
            if (this.sampleCurrentNalBytesRemaining == 0)
            {
              paramExtractorInput.readFully(this.nalLength.data, n, m);
              this.nalLength.setPosition(0);
              this.sampleCurrentNalBytesRemaining = this.nalLength.readUnsignedIntToInt();
              this.nalStartCode.setPosition(0);
              localTrackOutput.sampleData(this.nalStartCode, 4);
              this.sampleBytesWritten += 4;
              i += n;
            }
            else
            {
              k = localTrackOutput.sampleData(paramExtractorInput, this.sampleCurrentNalBytesRemaining, false);
              this.sampleBytesWritten += k;
              this.sampleCurrentNalBytesRemaining -= k;
            }
          }
        }
        for (;;)
        {
          k = i;
          if (this.sampleBytesWritten >= i) {
            break;
          }
          k = localTrackOutput.sampleData(paramExtractorInput, i - this.sampleBytesWritten, false);
          this.sampleBytesWritten += k;
          this.sampleCurrentNalBytesRemaining -= k;
        }
        localTrackOutput.sampleMetadata(localMp4Track.sampleTable.timestampsUs[j], localMp4Track.sampleTable.flags[j], k, 0, null);
        localMp4Track.sampleIndex += 1;
        this.sampleTrackIndex = -1;
        this.sampleBytesWritten = 0;
        this.sampleCurrentNalBytesRemaining = 0;
        i = 0;
      }
    }
  }
  
  private static boolean shouldParseContainerAtom(int paramInt)
  {
    if ((paramInt == Atom.TYPE_moov) || (paramInt == Atom.TYPE_trak) || (paramInt == Atom.TYPE_mdia) || (paramInt == Atom.TYPE_minf) || (paramInt == Atom.TYPE_stbl) || (paramInt == Atom.TYPE_edts)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private static boolean shouldParseLeafAtom(int paramInt)
  {
    if ((paramInt == Atom.TYPE_mdhd) || (paramInt == Atom.TYPE_mvhd) || (paramInt == Atom.TYPE_hdlr) || (paramInt == Atom.TYPE_stsd) || (paramInt == Atom.TYPE_stts) || (paramInt == Atom.TYPE_stss) || (paramInt == Atom.TYPE_ctts) || (paramInt == Atom.TYPE_elst) || (paramInt == Atom.TYPE_stsc) || (paramInt == Atom.TYPE_stsz) || (paramInt == Atom.TYPE_stz2) || (paramInt == Atom.TYPE_stco) || (paramInt == Atom.TYPE_co64) || (paramInt == Atom.TYPE_tkhd) || (paramInt == Atom.TYPE_ftyp) || (paramInt == Atom.TYPE_udta)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void updateSampleIndices(long paramLong)
  {
    for (Mp4Track localMp4Track : this.tracks)
    {
      TrackSampleTable localTrackSampleTable = localMp4Track.sampleTable;
      int k = localTrackSampleTable.getIndexOfEarlierOrEqualSynchronizationSample(paramLong);
      int m = k;
      if (k == -1) {
        m = localTrackSampleTable.getIndexOfLaterOrEqualSynchronizationSample(paramLong);
      }
      localMp4Track.sampleIndex = m;
    }
  }
  
  public long getDurationUs()
  {
    return this.durationUs;
  }
  
  public SeekMap.SeekPoints getSeekPoints(long paramLong)
  {
    Object localObject;
    if (this.tracks.length == 0) {
      localObject = new SeekMap.SeekPoints(SeekPoint.START);
    }
    for (;;)
    {
      return (SeekMap.SeekPoints)localObject;
      long l1 = -9223372036854775807L;
      long l2 = -1L;
      long l4;
      long l6;
      long l7;
      long l8;
      long l9;
      int j;
      if (this.firstVideoTrackIndex != -1)
      {
        localObject = this.tracks[this.firstVideoTrackIndex].sampleTable;
        int i = getSynchronizationSampleIndex((TrackSampleTable)localObject, paramLong);
        if (i == -1)
        {
          localObject = new SeekMap.SeekPoints(SeekPoint.START);
          continue;
        }
        long l3 = localObject.timestampsUs[i];
        l4 = l3;
        long l5 = localObject.offsets[i];
        l6 = l5;
        l7 = l4;
        l8 = l2;
        l9 = l1;
        if (l3 < paramLong)
        {
          l6 = l5;
          l7 = l4;
          l8 = l2;
          l9 = l1;
          if (i < ((TrackSampleTable)localObject).sampleCount - 1)
          {
            j = ((TrackSampleTable)localObject).getIndexOfLaterOrEqualSynchronizationSample(paramLong);
            l6 = l5;
            l7 = l4;
            l8 = l2;
            l9 = l1;
            if (j != -1)
            {
              l6 = l5;
              l7 = l4;
              l8 = l2;
              l9 = l1;
              if (j != i)
              {
                l9 = localObject.timestampsUs[j];
                l8 = localObject.offsets[j];
                l7 = l4;
                l6 = l5;
              }
            }
          }
        }
      }
      for (;;)
      {
        j = 0;
        l4 = l6;
        while (j < this.tracks.length)
        {
          paramLong = l4;
          l6 = l8;
          if (j != this.firstVideoTrackIndex)
          {
            localObject = this.tracks[j].sampleTable;
            l4 = maybeAdjustSeekOffset((TrackSampleTable)localObject, l7, l4);
            paramLong = l4;
            l6 = l8;
            if (l9 != -9223372036854775807L)
            {
              l6 = maybeAdjustSeekOffset((TrackSampleTable)localObject, l9, l8);
              paramLong = l4;
            }
          }
          j++;
          l4 = paramLong;
          l8 = l6;
        }
        l6 = Long.MAX_VALUE;
        l7 = paramLong;
        l8 = l2;
        l9 = l1;
      }
      localObject = new SeekPoint(l7, l4);
      if (l9 == -9223372036854775807L) {
        localObject = new SeekMap.SeekPoints((SeekPoint)localObject);
      } else {
        localObject = new SeekMap.SeekPoints((SeekPoint)localObject, new SeekPoint(l9, l8));
      }
    }
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    this.extractorOutput = paramExtractorOutput;
  }
  
  public boolean isSeekable()
  {
    return true;
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    do
    {
      switch (this.parserState)
      {
      default: 
        throw new IllegalStateException();
      }
    } while (readAtomHeader(paramExtractorInput));
    int i = -1;
    for (;;)
    {
      return i;
      if (!readAtomPayload(paramExtractorInput, paramPositionHolder)) {
        break;
      }
      i = 1;
      continue;
      i = readSample(paramExtractorInput, paramPositionHolder);
    }
  }
  
  public void release() {}
  
  public void seek(long paramLong1, long paramLong2)
  {
    this.containerAtoms.clear();
    this.atomHeaderBytesRead = 0;
    this.sampleTrackIndex = -1;
    this.sampleBytesWritten = 0;
    this.sampleCurrentNalBytesRemaining = 0;
    if (paramLong1 == 0L) {
      enterReadingAtomHeaderState();
    }
    for (;;)
    {
      return;
      if (this.tracks != null) {
        updateSampleIndices(paramLong2);
      }
    }
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    return Sniffer.sniffUnfragmented(paramExtractorInput);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Flags {}
  
  private static final class Mp4Track
  {
    public int sampleIndex;
    public final TrackSampleTable sampleTable;
    public final Track track;
    public final TrackOutput trackOutput;
    
    public Mp4Track(Track paramTrack, TrackSampleTable paramTrackSampleTable, TrackOutput paramTrackOutput)
    {
      this.track = paramTrack;
      this.sampleTable = paramTrackSampleTable;
      this.trackOutput = paramTrackOutput;
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface State {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/mp4/Mp4Extractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */