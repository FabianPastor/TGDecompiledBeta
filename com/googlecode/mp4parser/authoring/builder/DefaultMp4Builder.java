package com.googlecode.mp4parser.authoring.builder;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.DataEntryUrlBox;
import com.coremedia.iso.boxes.DataInformationBox;
import com.coremedia.iso.boxes.DataReferenceBox;
import com.coremedia.iso.boxes.EditBox;
import com.coremedia.iso.boxes.EditListBox;
import com.coremedia.iso.boxes.EditListBox.Entry;
import com.coremedia.iso.boxes.FileTypeBox;
import com.coremedia.iso.boxes.HandlerBox;
import com.coremedia.iso.boxes.HintMediaHeaderBox;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MediaInformationBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.coremedia.iso.boxes.NullMediaHeaderBox;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.SampleToChunkBox;
import com.coremedia.iso.boxes.SampleToChunkBox.Entry;
import com.coremedia.iso.boxes.SoundMediaHeaderBox;
import com.coremedia.iso.boxes.StaticChunkOffsetBox;
import com.coremedia.iso.boxes.SubtitleMediaHeaderBox;
import com.coremedia.iso.boxes.SyncSampleBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TimeToSampleBox.Entry;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.VideoMediaHeaderBox;
import com.googlecode.mp4parser.BasicContainer;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.Edit;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.authoring.tracks.CencEncryptedTrack;
import com.googlecode.mp4parser.boxes.dece.SampleEncryptionBox;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleGroupDescriptionBox;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleToGroupBox;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleToGroupBox.Entry;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.Path;
import com.mp4parser.iso14496.part12.SampleAuxiliaryInformationOffsetsBox;
import com.mp4parser.iso14496.part12.SampleAuxiliaryInformationSizesBox;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultMp4Builder
  implements Mp4Builder
{
  private static Logger LOG;
  Set<StaticChunkOffsetBox> chunkOffsetBoxes = new HashSet();
  private FragmentIntersectionFinder intersectionFinder;
  Set<SampleAuxiliaryInformationOffsetsBox> sampleAuxiliaryInformationOffsetsBoxes = new HashSet();
  HashMap<Track, List<Sample>> track2Sample = new HashMap();
  HashMap<Track, long[]> track2SampleSizes = new HashMap();
  
  static
  {
    if (!DefaultMp4Builder.class.desiredAssertionStatus()) {}
    for (boolean bool = true;; bool = false)
    {
      $assertionsDisabled = bool;
      LOG = Logger.getLogger(DefaultMp4Builder.class.getName());
      return;
    }
  }
  
  public static long gcd(long paramLong1, long paramLong2)
  {
    if (paramLong2 == 0L) {
      return paramLong1;
    }
    return gcd(paramLong2, paramLong1 % paramLong2);
  }
  
  private static long sum(int[] paramArrayOfInt)
  {
    long l = 0L;
    int j = paramArrayOfInt.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return l;
      }
      l += paramArrayOfInt[i];
      i += 1;
    }
  }
  
  private static long sum(long[] paramArrayOfLong)
  {
    long l = 0L;
    int j = paramArrayOfLong.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return l;
      }
      l += paramArrayOfLong[i];
      i += 1;
    }
  }
  
  public Container build(Movie paramMovie)
  {
    if (this.intersectionFinder == null) {
      this.intersectionFinder = new TwoSecondIntersectionFinder(paramMovie, 2);
    }
    LOG.fine("Creating movie " + paramMovie);
    Object localObject1 = paramMovie.getTracks().iterator();
    Object localObject3;
    Object localObject2;
    if (!((Iterator)localObject1).hasNext())
    {
      localObject3 = new BasicContainer();
      ((BasicContainer)localObject3).addBox(createFileTypeBox(paramMovie));
      localObject1 = new HashMap();
      localObject2 = paramMovie.getTracks().iterator();
      label102:
      if (((Iterator)localObject2).hasNext()) {
        break label318;
      }
      localObject2 = createMovieBox(paramMovie, (Map)localObject1);
      ((BasicContainer)localObject3).addBox((Box)localObject2);
      localObject2 = Path.getPaths((Box)localObject2, "trak/mdia/minf/stbl/stsz");
      l = 0L;
      localObject2 = ((List)localObject2).iterator();
      label148:
      if (((Iterator)localObject2).hasNext()) {
        break label350;
      }
      paramMovie = new InterleaveChunkMdat(paramMovie, (Map)localObject1, l, null);
      ((BasicContainer)localObject3).addBox(paramMovie);
      l = paramMovie.getDataOffset();
      paramMovie = this.chunkOffsetBoxes.iterator();
    }
    Object localObject4;
    int i;
    for (;;)
    {
      if (!paramMovie.hasNext())
      {
        localObject4 = this.sampleAuxiliaryInformationOffsetsBoxes.iterator();
        if (((Iterator)localObject4).hasNext()) {
          break label412;
        }
        return (Container)localObject3;
        localObject2 = (Track)((Iterator)localObject1).next();
        localObject3 = ((Track)localObject2).getSamples();
        putSamples((Track)localObject2, (List)localObject3);
        localObject4 = new long[((List)localObject3).size()];
        i = 0;
        for (;;)
        {
          if (i >= localObject4.length)
          {
            this.track2SampleSizes.put(localObject2, localObject4);
            break;
          }
          localObject4[i] = ((Sample)((List)localObject3).get(i)).getSize();
          i += 1;
        }
        label318:
        localObject4 = (Track)((Iterator)localObject2).next();
        ((Map)localObject1).put(localObject4, getChunkSizes((Track)localObject4, paramMovie));
        break label102;
        label350:
        l += sum(((SampleSizeBox)((Iterator)localObject2).next()).getSampleSizes());
        break label148;
      }
      localObject1 = ((StaticChunkOffsetBox)paramMovie.next()).getChunkOffsets();
      i = 0;
      while (i < localObject1.length)
      {
        localObject1[i] += l;
        i += 1;
      }
    }
    label412:
    localObject1 = (SampleAuxiliaryInformationOffsetsBox)((Iterator)localObject4).next();
    long l = ((SampleAuxiliaryInformationOffsetsBox)localObject1).getSize() + 44L;
    for (paramMovie = (Movie)localObject1;; paramMovie = (Movie)localObject2)
    {
      localObject2 = ((Box)paramMovie).getParent();
      Iterator localIterator = ((Container)localObject2).getBoxes().iterator();
      label465:
      if (!localIterator.hasNext()) {
        label475:
        if (!(localObject2 instanceof Box))
        {
          paramMovie = ((SampleAuxiliaryInformationOffsetsBox)localObject1).getOffsets();
          i = 0;
        }
      } else {
        for (;;)
        {
          if (i >= paramMovie.length)
          {
            ((SampleAuxiliaryInformationOffsetsBox)localObject1).setOffsets(paramMovie);
            break;
            Box localBox = (Box)localIterator.next();
            if (localBox == paramMovie) {
              break label475;
            }
            l += localBox.getSize();
            break label465;
          }
          paramMovie[i] += l;
          i += 1;
        }
      }
    }
  }
  
  protected void createCencBoxes(CencEncryptedTrack paramCencEncryptedTrack, SampleTableBox paramSampleTableBox, int[] paramArrayOfInt)
  {
    SampleAuxiliaryInformationSizesBox localSampleAuxiliaryInformationSizesBox = new SampleAuxiliaryInformationSizesBox();
    localSampleAuxiliaryInformationSizesBox.setAuxInfoType("cenc");
    localSampleAuxiliaryInformationSizesBox.setFlags(1);
    List localList = paramCencEncryptedTrack.getSampleEncryptionEntries();
    Object localObject;
    int i;
    if (paramCencEncryptedTrack.hasSubSampleEncryption())
    {
      localObject = new short[localList.size()];
      i = 0;
      if (i >= localObject.length) {
        localSampleAuxiliaryInformationSizesBox.setSampleInfoSizes((short[])localObject);
      }
    }
    long l;
    int j;
    for (;;)
    {
      localObject = new SampleAuxiliaryInformationOffsetsBox();
      SampleEncryptionBox localSampleEncryptionBox = new SampleEncryptionBox();
      localSampleEncryptionBox.setSubSampleEncryption(paramCencEncryptedTrack.hasSubSampleEncryption());
      localSampleEncryptionBox.setEntries(localList);
      l = localSampleEncryptionBox.getOffsetToFirstIV();
      j = 0;
      paramCencEncryptedTrack = new long[paramArrayOfInt.length];
      i = 0;
      if (i < paramArrayOfInt.length) {
        break label223;
      }
      ((SampleAuxiliaryInformationOffsetsBox)localObject).setOffsets(paramCencEncryptedTrack);
      paramSampleTableBox.addBox(localSampleAuxiliaryInformationSizesBox);
      paramSampleTableBox.addBox((Box)localObject);
      paramSampleTableBox.addBox(localSampleEncryptionBox);
      this.sampleAuxiliaryInformationOffsetsBoxes.add(localObject);
      return;
      localObject[i] = ((short)((CencSampleAuxiliaryDataFormat)localList.get(i)).getSize());
      i += 1;
      break;
      localSampleAuxiliaryInformationSizesBox.setDefaultSampleInfoSize(8);
      localSampleAuxiliaryInformationSizesBox.setSampleCount(paramCencEncryptedTrack.getSamples().size());
    }
    label223:
    paramCencEncryptedTrack[i] = l;
    int k = 0;
    for (;;)
    {
      if (k >= paramArrayOfInt[i])
      {
        i += 1;
        break;
      }
      l += ((CencSampleAuxiliaryDataFormat)localList.get(j)).getSize();
      k += 1;
      j += 1;
    }
  }
  
  protected void createCtts(Track paramTrack, SampleTableBox paramSampleTableBox)
  {
    paramTrack = paramTrack.getCompositionTimeEntries();
    if ((paramTrack != null) && (!paramTrack.isEmpty()))
    {
      CompositionTimeToSample localCompositionTimeToSample = new CompositionTimeToSample();
      localCompositionTimeToSample.setEntries(paramTrack);
      paramSampleTableBox.addBox(localCompositionTimeToSample);
    }
  }
  
  protected Box createEdts(Track paramTrack, Movie paramMovie)
  {
    if ((paramTrack.getEdits() != null) && (paramTrack.getEdits().size() > 0))
    {
      EditListBox localEditListBox = new EditListBox();
      localEditListBox.setVersion(1);
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = paramTrack.getEdits().iterator();
      for (;;)
      {
        if (!localIterator.hasNext())
        {
          localEditListBox.setEntries(localArrayList);
          paramTrack = new EditBox();
          paramTrack.addBox(localEditListBox);
          return paramTrack;
        }
        Edit localEdit = (Edit)localIterator.next();
        localArrayList.add(new EditListBox.Entry(localEditListBox, Math.round(localEdit.getSegmentDuration() * paramMovie.getTimescale()), localEdit.getMediaTime() * paramTrack.getTrackMetaData().getTimescale() / localEdit.getTimeScale(), localEdit.getMediaRate()));
      }
    }
    return null;
  }
  
  protected FileTypeBox createFileTypeBox(Movie paramMovie)
  {
    paramMovie = new LinkedList();
    paramMovie.add("isom");
    paramMovie.add("iso2");
    paramMovie.add("avc1");
    return new FileTypeBox("isom", 0L, paramMovie);
  }
  
  protected MovieBox createMovieBox(Movie paramMovie, Map<Track, int[]> paramMap)
  {
    MovieBox localMovieBox = new MovieBox();
    Object localObject1 = new MovieHeaderBox();
    ((MovieHeaderBox)localObject1).setCreationTime(new Date());
    ((MovieHeaderBox)localObject1).setModificationTime(new Date());
    ((MovieHeaderBox)localObject1).setMatrix(paramMovie.getMatrix());
    long l3 = getTimescale(paramMovie);
    long l2 = 0L;
    Iterator localIterator = paramMovie.getTracks().iterator();
    long l1;
    if (!localIterator.hasNext())
    {
      ((MovieHeaderBox)localObject1).setDuration(l2);
      ((MovieHeaderBox)localObject1).setTimescale(l3);
      l1 = 0L;
      localIterator = paramMovie.getTracks().iterator();
      label109:
      if (localIterator.hasNext()) {
        break label306;
      }
      ((MovieHeaderBox)localObject1).setNextTrackId(l1 + 1L);
      localMovieBox.addBox((Box)localObject1);
      localObject1 = paramMovie.getTracks().iterator();
    }
    for (;;)
    {
      if (!((Iterator)localObject1).hasNext())
      {
        paramMovie = createUdta(paramMovie);
        if (paramMovie != null) {
          localMovieBox.addBox(paramMovie);
        }
        return localMovieBox;
        Object localObject2 = (Track)localIterator.next();
        if ((((Track)localObject2).getEdits() == null) || (((Track)localObject2).getEdits().isEmpty()))
        {
          l1 = ((Track)localObject2).getDuration() * getTimescale(paramMovie) / ((Track)localObject2).getTrackMetaData().getTimescale();
          label236:
          if (l1 <= l2) {
            break label284;
          }
          l2 = l1;
          break;
        }
        l1 = 0L;
        localObject2 = ((Track)localObject2).getEdits().iterator();
        for (;;)
        {
          if (!((Iterator)localObject2).hasNext())
          {
            l1 *= getTimescale(paramMovie);
            break label236;
            label284:
            break;
          }
          l1 += ((Edit)((Iterator)localObject2).next()).getSegmentDuration();
        }
        label306:
        localObject2 = (Track)localIterator.next();
        l2 = l1;
        if (l1 < ((Track)localObject2).getTrackMetaData().getTrackId()) {
          l2 = ((Track)localObject2).getTrackMetaData().getTrackId();
        }
        l1 = l2;
        break label109;
      }
      localMovieBox.addBox(createTrackBox((Track)((Iterator)localObject1).next(), paramMovie, paramMap));
    }
  }
  
  protected void createSdtp(Track paramTrack, SampleTableBox paramSampleTableBox)
  {
    if ((paramTrack.getSampleDependencies() != null) && (!paramTrack.getSampleDependencies().isEmpty()))
    {
      SampleDependencyTypeBox localSampleDependencyTypeBox = new SampleDependencyTypeBox();
      localSampleDependencyTypeBox.setEntries(paramTrack.getSampleDependencies());
      paramSampleTableBox.addBox(localSampleDependencyTypeBox);
    }
  }
  
  protected Box createStbl(Track paramTrack, Movie paramMovie, Map<Track, int[]> paramMap)
  {
    SampleTableBox localSampleTableBox = new SampleTableBox();
    createStsd(paramTrack, localSampleTableBox);
    createStts(paramTrack, localSampleTableBox);
    createCtts(paramTrack, localSampleTableBox);
    createStss(paramTrack, localSampleTableBox);
    createSdtp(paramTrack, localSampleTableBox);
    createStsc(paramTrack, paramMap, localSampleTableBox);
    createStsz(paramTrack, localSampleTableBox);
    createStco(paramTrack, paramMovie, paramMap, localSampleTableBox);
    Object localObject2 = new HashMap();
    Object localObject3 = paramTrack.getSampleGroups().entrySet().iterator();
    Object localObject1;
    if (!((Iterator)localObject3).hasNext()) {
      localObject1 = ((Map)localObject2).entrySet().iterator();
    }
    Object localObject4;
    Object localObject5;
    int i;
    for (;;)
    {
      if (!((Iterator)localObject1).hasNext())
      {
        if ((paramTrack instanceof CencEncryptedTrack)) {
          createCencBoxes((CencEncryptedTrack)paramTrack, localSampleTableBox, (int[])paramMap.get(paramTrack));
        }
        createSubs(paramTrack, localSampleTableBox);
        return localSampleTableBox;
        localObject4 = (Map.Entry)((Iterator)localObject3).next();
        localObject5 = ((GroupEntry)((Map.Entry)localObject4).getKey()).getType();
        localObject1 = (List)((Map)localObject2).get(localObject5);
        paramMovie = (Movie)localObject1;
        if (localObject1 == null)
        {
          paramMovie = new ArrayList();
          ((Map)localObject2).put(localObject5, paramMovie);
        }
        paramMovie.add((GroupEntry)((Map.Entry)localObject4).getKey());
        break;
      }
      localObject2 = (Map.Entry)((Iterator)localObject1).next();
      localObject3 = new SampleGroupDescriptionBox();
      paramMovie = (String)((Map.Entry)localObject2).getKey();
      ((SampleGroupDescriptionBox)localObject3).setGroupEntries((List)((Map.Entry)localObject2).getValue());
      localObject4 = new SampleToGroupBox();
      ((SampleToGroupBox)localObject4).setGroupingType(paramMovie);
      paramMovie = null;
      i = 0;
      if (i < paramTrack.getSamples().size()) {
        break label354;
      }
      localSampleTableBox.addBox((Box)localObject3);
      localSampleTableBox.addBox((Box)localObject4);
    }
    label354:
    int k = 0;
    int j = 0;
    label360:
    if (j >= ((List)((Map.Entry)localObject2).getValue()).size())
    {
      if ((paramMovie != null) && (paramMovie.getGroupDescriptionIndex() == k)) {
        break label487;
      }
      paramMovie = new SampleToGroupBox.Entry(1L, k);
      ((SampleToGroupBox)localObject4).getEntries().add(paramMovie);
    }
    for (;;)
    {
      i += 1;
      break;
      localObject5 = (GroupEntry)((List)((Map.Entry)localObject2).getValue()).get(j);
      if (Arrays.binarySearch((long[])paramTrack.getSampleGroups().get(localObject5), i) >= 0) {
        k = j + 1;
      }
      j += 1;
      break label360;
      label487:
      paramMovie.setSampleCount(paramMovie.getSampleCount() + 1L);
    }
  }
  
  protected void createStco(Track paramTrack, Movie paramMovie, Map<Track, int[]> paramMap, SampleTableBox paramSampleTableBox)
  {
    int[] arrayOfInt1 = (int[])paramMap.get(paramTrack);
    StaticChunkOffsetBox localStaticChunkOffsetBox = new StaticChunkOffsetBox();
    this.chunkOffsetBoxes.add(localStaticChunkOffsetBox);
    long l1 = 0L;
    long[] arrayOfLong = new long[arrayOfInt1.length];
    if (LOG.isLoggable(Level.FINE)) {
      LOG.fine("Calculating chunk offsets for track_" + paramTrack.getTrackMetaData().getTrackId());
    }
    int i = 0;
    Iterator localIterator;
    for (;;)
    {
      if (i >= arrayOfInt1.length)
      {
        localStaticChunkOffsetBox.setChunkOffsets(arrayOfLong);
        paramSampleTableBox.addBox(localStaticChunkOffsetBox);
        return;
      }
      if (LOG.isLoggable(Level.FINER)) {
        LOG.finer("Calculating chunk offsets for track_" + paramTrack.getTrackMetaData().getTrackId() + " chunk " + i);
      }
      localIterator = paramMovie.getTracks().iterator();
      if (localIterator.hasNext()) {
        break;
      }
      i += 1;
    }
    Track localTrack = (Track)localIterator.next();
    if (LOG.isLoggable(Level.FINEST)) {
      LOG.finest("Adding offsets of track_" + localTrack.getTrackMetaData().getTrackId());
    }
    int[] arrayOfInt2 = (int[])paramMap.get(localTrack);
    long l2 = 0L;
    int j = 0;
    for (;;)
    {
      if (j >= i)
      {
        if (localTrack == paramTrack) {
          arrayOfLong[i] = l1;
        }
        j = CastUtils.l2i(l2);
        long l3 = l1;
        for (;;)
        {
          l1 = l3;
          if (j >= arrayOfInt2[i] + l2) {
            break;
          }
          l3 += ((long[])this.track2SampleSizes.get(localTrack))[j];
          j += 1;
        }
      }
      l2 += arrayOfInt2[j];
      j += 1;
    }
  }
  
  protected void createStsc(Track paramTrack, Map<Track, int[]> paramMap, SampleTableBox paramSampleTableBox)
  {
    paramTrack = (int[])paramMap.get(paramTrack);
    paramMap = new SampleToChunkBox();
    paramMap.setEntries(new LinkedList());
    long l1 = -2147483648L;
    int i = 0;
    for (;;)
    {
      if (i >= paramTrack.length)
      {
        paramSampleTableBox.addBox(paramMap);
        return;
      }
      long l2 = l1;
      if (l1 != paramTrack[i])
      {
        paramMap.getEntries().add(new SampleToChunkBox.Entry(i + 1, paramTrack[i], 1L));
        l2 = paramTrack[i];
      }
      i += 1;
      l1 = l2;
    }
  }
  
  protected void createStsd(Track paramTrack, SampleTableBox paramSampleTableBox)
  {
    paramSampleTableBox.addBox(paramTrack.getSampleDescriptionBox());
  }
  
  protected void createStss(Track paramTrack, SampleTableBox paramSampleTableBox)
  {
    paramTrack = paramTrack.getSyncSamples();
    if ((paramTrack != null) && (paramTrack.length > 0))
    {
      SyncSampleBox localSyncSampleBox = new SyncSampleBox();
      localSyncSampleBox.setSampleNumber(paramTrack);
      paramSampleTableBox.addBox(localSyncSampleBox);
    }
  }
  
  protected void createStsz(Track paramTrack, SampleTableBox paramSampleTableBox)
  {
    SampleSizeBox localSampleSizeBox = new SampleSizeBox();
    localSampleSizeBox.setSampleSizes((long[])this.track2SampleSizes.get(paramTrack));
    paramSampleTableBox.addBox(localSampleSizeBox);
  }
  
  protected void createStts(Track paramTrack, SampleTableBox paramSampleTableBox)
  {
    Object localObject = null;
    ArrayList localArrayList = new ArrayList();
    long[] arrayOfLong = paramTrack.getSampleDurations();
    int j = arrayOfLong.length;
    int i = 0;
    paramTrack = (Track)localObject;
    if (i >= j)
    {
      paramTrack = new TimeToSampleBox();
      paramTrack.setEntries(localArrayList);
      paramSampleTableBox.addBox(paramTrack);
      return;
    }
    long l = arrayOfLong[i];
    if ((paramTrack != null) && (paramTrack.getDelta() == l)) {
      paramTrack.setCount(paramTrack.getCount() + 1L);
    }
    for (;;)
    {
      i += 1;
      break;
      paramTrack = new TimeToSampleBox.Entry(1L, l);
      localArrayList.add(paramTrack);
    }
  }
  
  protected void createSubs(Track paramTrack, SampleTableBox paramSampleTableBox)
  {
    if (paramTrack.getSubsampleInformationBox() != null) {
      paramSampleTableBox.addBox(paramTrack.getSubsampleInformationBox());
    }
  }
  
  protected TrackBox createTrackBox(Track paramTrack, Movie paramMovie, Map<Track, int[]> paramMap)
  {
    TrackBox localTrackBox = new TrackBox();
    Object localObject1 = new TrackHeaderBox();
    ((TrackHeaderBox)localObject1).setEnabled(true);
    ((TrackHeaderBox)localObject1).setInMovie(true);
    ((TrackHeaderBox)localObject1).setInPreview(true);
    ((TrackHeaderBox)localObject1).setInPoster(true);
    ((TrackHeaderBox)localObject1).setMatrix(paramTrack.getTrackMetaData().getMatrix());
    ((TrackHeaderBox)localObject1).setAlternateGroup(paramTrack.getTrackMetaData().getGroup());
    ((TrackHeaderBox)localObject1).setCreationTime(paramTrack.getTrackMetaData().getCreationTime());
    Object localObject2;
    if ((paramTrack.getEdits() == null) || (paramTrack.getEdits().isEmpty()))
    {
      ((TrackHeaderBox)localObject1).setDuration(paramTrack.getDuration() * getTimescale(paramMovie) / paramTrack.getTrackMetaData().getTimescale());
      ((TrackHeaderBox)localObject1).setHeight(paramTrack.getTrackMetaData().getHeight());
      ((TrackHeaderBox)localObject1).setWidth(paramTrack.getTrackMetaData().getWidth());
      ((TrackHeaderBox)localObject1).setLayer(paramTrack.getTrackMetaData().getLayer());
      ((TrackHeaderBox)localObject1).setModificationTime(new Date());
      ((TrackHeaderBox)localObject1).setTrackId(paramTrack.getTrackMetaData().getTrackId());
      ((TrackHeaderBox)localObject1).setVolume(paramTrack.getTrackMetaData().getVolume());
      localTrackBox.addBox((Box)localObject1);
      localTrackBox.addBox(createEdts(paramTrack, paramMovie));
      localObject1 = new MediaBox();
      localTrackBox.addBox((Box)localObject1);
      localObject2 = new MediaHeaderBox();
      ((MediaHeaderBox)localObject2).setCreationTime(paramTrack.getTrackMetaData().getCreationTime());
      ((MediaHeaderBox)localObject2).setDuration(paramTrack.getDuration());
      ((MediaHeaderBox)localObject2).setTimescale(paramTrack.getTrackMetaData().getTimescale());
      ((MediaHeaderBox)localObject2).setLanguage(paramTrack.getTrackMetaData().getLanguage());
      ((MediaBox)localObject1).addBox((Box)localObject2);
      localObject2 = new HandlerBox();
      ((MediaBox)localObject1).addBox((Box)localObject2);
      ((HandlerBox)localObject2).setHandlerType(paramTrack.getHandler());
      localObject2 = new MediaInformationBox();
      if (!paramTrack.getHandler().equals("vide")) {
        break label526;
      }
      ((MediaInformationBox)localObject2).addBox(new VideoMediaHeaderBox());
    }
    for (;;)
    {
      DataInformationBox localDataInformationBox = new DataInformationBox();
      DataReferenceBox localDataReferenceBox = new DataReferenceBox();
      localDataInformationBox.addBox(localDataReferenceBox);
      DataEntryUrlBox localDataEntryUrlBox = new DataEntryUrlBox();
      localDataEntryUrlBox.setFlags(1);
      localDataReferenceBox.addBox(localDataEntryUrlBox);
      ((MediaInformationBox)localObject2).addBox(localDataInformationBox);
      ((MediaInformationBox)localObject2).addBox(createStbl(paramTrack, paramMovie, paramMap));
      ((MediaBox)localObject1).addBox((Box)localObject2);
      return localTrackBox;
      long l = 0L;
      localObject2 = paramTrack.getEdits().iterator();
      for (;;)
      {
        if (!((Iterator)localObject2).hasNext())
        {
          ((TrackHeaderBox)localObject1).setDuration(paramTrack.getTrackMetaData().getTimescale() * l);
          break;
        }
        l += ((Edit)((Iterator)localObject2).next()).getSegmentDuration();
      }
      label526:
      if (paramTrack.getHandler().equals("soun")) {
        ((MediaInformationBox)localObject2).addBox(new SoundMediaHeaderBox());
      } else if (paramTrack.getHandler().equals("text")) {
        ((MediaInformationBox)localObject2).addBox(new NullMediaHeaderBox());
      } else if (paramTrack.getHandler().equals("subt")) {
        ((MediaInformationBox)localObject2).addBox(new SubtitleMediaHeaderBox());
      } else if (paramTrack.getHandler().equals("hint")) {
        ((MediaInformationBox)localObject2).addBox(new HintMediaHeaderBox());
      } else if (paramTrack.getHandler().equals("sbtl")) {
        ((MediaInformationBox)localObject2).addBox(new NullMediaHeaderBox());
      }
    }
  }
  
  protected Box createUdta(Movie paramMovie)
  {
    return null;
  }
  
  int[] getChunkSizes(Track paramTrack, Movie paramMovie)
  {
    paramMovie = this.intersectionFinder.sampleNumbers(paramTrack);
    int[] arrayOfInt = new int[paramMovie.length];
    int i = 0;
    if (i >= paramMovie.length)
    {
      if ((!$assertionsDisabled) && (((List)this.track2Sample.get(paramTrack)).size() != sum(arrayOfInt))) {
        throw new AssertionError("The number of samples and the sum of all chunk lengths must be equal");
      }
    }
    else
    {
      long l2 = paramMovie[i];
      if (paramMovie.length == i + 1) {}
      for (long l1 = paramTrack.getSamples().size();; l1 = paramMovie[(i + 1)] - 1L)
      {
        arrayOfInt[i] = CastUtils.l2i(l1 - (l2 - 1L));
        i += 1;
        break;
      }
    }
    return arrayOfInt;
  }
  
  public long getTimescale(Movie paramMovie)
  {
    long l = ((Track)paramMovie.getTracks().iterator().next()).getTrackMetaData().getTimescale();
    paramMovie = paramMovie.getTracks().iterator();
    for (;;)
    {
      if (!paramMovie.hasNext()) {
        return l;
      }
      l = gcd(((Track)paramMovie.next()).getTrackMetaData().getTimescale(), l);
    }
  }
  
  protected List<Sample> putSamples(Track paramTrack, List<Sample> paramList)
  {
    return (List)this.track2Sample.put(paramTrack, paramList);
  }
  
  public void setIntersectionFinder(FragmentIntersectionFinder paramFragmentIntersectionFinder)
  {
    this.intersectionFinder = paramFragmentIntersectionFinder;
  }
  
  private class InterleaveChunkMdat
    implements Box
  {
    List<List<Sample>> chunkList = new ArrayList();
    long contentSize;
    Container parent;
    List<Track> tracks;
    
    private InterleaveChunkMdat(Map<Track, int[]> paramMap, long paramLong)
    {
      Object localObject1;
      this.contentSize = localObject1;
      this.tracks = paramMap.getTracks();
      int i = 0;
      for (;;)
      {
        if (i >= ((int[])paramLong.values().iterator().next()).length) {
          return;
        }
        paramMap = this.tracks.iterator();
        if (paramMap.hasNext()) {
          break;
        }
        i += 1;
      }
      Object localObject2 = (Track)paramMap.next();
      int[] arrayOfInt = (int[])paramLong.get(localObject2);
      long l = 0L;
      int j = 0;
      for (;;)
      {
        if (j >= i)
        {
          localObject2 = ((List)DefaultMp4Builder.this.track2Sample.get(localObject2)).subList(CastUtils.l2i(l), CastUtils.l2i(arrayOfInt[i] + l));
          this.chunkList.add(localObject2);
          break;
        }
        l += arrayOfInt[j];
        j += 1;
      }
    }
    
    private boolean isSmallBox(long paramLong)
    {
      return 8L + paramLong < 4294967296L;
    }
    
    public void getBox(WritableByteChannel paramWritableByteChannel)
      throws IOException
    {
      Object localObject = ByteBuffer.allocate(16);
      long l = getSize();
      if (isSmallBox(l))
      {
        IsoTypeWriter.writeUInt32((ByteBuffer)localObject, l);
        ((ByteBuffer)localObject).put(IsoFile.fourCCtoBytes("mdat"));
        if (!isSmallBox(l)) {
          break label101;
        }
        ((ByteBuffer)localObject).put(new byte[8]);
        label55:
        ((ByteBuffer)localObject).rewind();
        paramWritableByteChannel.write((ByteBuffer)localObject);
        localObject = this.chunkList.iterator();
      }
      for (;;)
      {
        if (!((Iterator)localObject).hasNext())
        {
          return;
          IsoTypeWriter.writeUInt32((ByteBuffer)localObject, 1L);
          break;
          label101:
          IsoTypeWriter.writeUInt64((ByteBuffer)localObject, l);
          break label55;
        }
        Iterator localIterator = ((List)((Iterator)localObject).next()).iterator();
        while (localIterator.hasNext()) {
          ((Sample)localIterator.next()).writeTo(paramWritableByteChannel);
        }
      }
    }
    
    public long getDataOffset()
    {
      Object localObject = this;
      long l = 16L;
      if (!(localObject instanceof Box)) {
        return l;
      }
      Iterator localIterator = ((Box)localObject).getParent().getBoxes().iterator();
      for (;;)
      {
        if (!localIterator.hasNext()) {}
        Box localBox;
        do
        {
          localObject = ((Box)localObject).getParent();
          break;
          localBox = (Box)localIterator.next();
        } while (localObject == localBox);
        l += localBox.getSize();
      }
    }
    
    public long getOffset()
    {
      throw new RuntimeException("Doesn't have any meaning for programmatically created boxes");
    }
    
    public Container getParent()
    {
      return this.parent;
    }
    
    public long getSize()
    {
      return 16L + this.contentSize;
    }
    
    public String getType()
    {
      return "mdat";
    }
    
    public void parse(DataSource paramDataSource, ByteBuffer paramByteBuffer, long paramLong, BoxParser paramBoxParser)
      throws IOException
    {}
    
    public void setParent(Container paramContainer)
    {
      this.parent = paramContainer;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/builder/DefaultMp4Builder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */