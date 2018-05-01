package com.googlecode.mp4parser.authoring.builder;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
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
import com.coremedia.iso.boxes.SampleDependencyTypeBox.Entry;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.SampleToChunkBox;
import com.coremedia.iso.boxes.SchemeTypeBox;
import com.coremedia.iso.boxes.SoundMediaHeaderBox;
import com.coremedia.iso.boxes.StaticChunkOffsetBox;
import com.coremedia.iso.boxes.SubtitleMediaHeaderBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.VideoMediaHeaderBox;
import com.coremedia.iso.boxes.fragment.MovieExtendsBox;
import com.coremedia.iso.boxes.fragment.MovieExtendsHeaderBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentHeaderBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentRandomAccessBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentRandomAccessOffsetBox;
import com.coremedia.iso.boxes.fragment.SampleFlags;
import com.coremedia.iso.boxes.fragment.TrackExtendsBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentBaseMediaDecodeTimeBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentHeaderBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentRandomAccessBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentRandomAccessBox.Entry;
import com.coremedia.iso.boxes.fragment.TrackRunBox;
import com.coremedia.iso.boxes.fragment.TrackRunBox.Entry;
import com.googlecode.mp4parser.AbstractContainerBox;
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
import com.mp4parser.iso23001.part7.TrackEncryptionBox;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

public class FragmentedMp4Builder
  implements Mp4Builder
{
  private static final Logger LOG;
  protected FragmentIntersectionFinder intersectionFinder;
  
  static
  {
    if (!FragmentedMp4Builder.class.desiredAssertionStatus()) {}
    for (boolean bool = true;; bool = false)
    {
      $assertionsDisabled = bool;
      LOG = Logger.getLogger(FragmentedMp4Builder.class.getName());
      return;
    }
  }
  
  private long getTrackDuration(Movie paramMovie, Track paramTrack)
  {
    return paramTrack.getDuration() * paramMovie.getTimescale() / paramTrack.getTrackMetaData().getTimescale();
  }
  
  public Container build(Movie paramMovie)
  {
    LOG.fine("Creating movie " + paramMovie);
    Object localObject1;
    Iterator localIterator;
    label52:
    Object localObject2;
    if (this.intersectionFinder == null)
    {
      localObject1 = null;
      localIterator = paramMovie.getTracks().iterator();
      if (!localIterator.hasNext()) {
        this.intersectionFinder = new SyncSampleIntersectFinderImpl(paramMovie, (Track)localObject1, -1);
      }
    }
    else
    {
      localObject1 = new BasicContainer();
      ((BasicContainer)localObject1).addBox(createFtyp(paramMovie));
      ((BasicContainer)localObject1).addBox(createMoov(paramMovie));
      localObject2 = createMoofMdat(paramMovie).iterator();
    }
    for (;;)
    {
      if (!((Iterator)localObject2).hasNext())
      {
        ((BasicContainer)localObject1).addBox(createMfra(paramMovie, (Container)localObject1));
        return (Container)localObject1;
        localObject2 = (Track)localIterator.next();
        if (!((Track)localObject2).getHandler().equals("vide")) {
          break;
        }
        localObject1 = localObject2;
        break label52;
      }
      ((BasicContainer)localObject1).addBox((Box)((Iterator)localObject2).next());
    }
  }
  
  protected DataInformationBox createDinf(Movie paramMovie, Track paramTrack)
  {
    paramMovie = new DataInformationBox();
    paramTrack = new DataReferenceBox();
    paramMovie.addBox(paramTrack);
    DataEntryUrlBox localDataEntryUrlBox = new DataEntryUrlBox();
    localDataEntryUrlBox.setFlags(1);
    paramTrack.addBox(localDataEntryUrlBox);
    return paramMovie;
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
  
  protected int createFragment(List<Box> paramList, Track paramTrack, long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    int i = paramInt2;
    long l2;
    if (paramInt1 < paramArrayOfLong.length)
    {
      l2 = paramArrayOfLong[paramInt1];
      if (paramInt1 + 1 >= paramArrayOfLong.length) {
        break label91;
      }
    }
    label91:
    for (long l1 = paramArrayOfLong[(paramInt1 + 1)];; l1 = paramTrack.getSamples().size() + 1)
    {
      i = paramInt2;
      if (l2 != l1)
      {
        paramList.add(createMoof(l2, l1, paramTrack, paramInt2));
        paramList.add(createMdat(l2, l1, paramTrack, paramInt2));
        i = paramInt2 + 1;
      }
      return i;
    }
  }
  
  public Box createFtyp(Movie paramMovie)
  {
    paramMovie = new LinkedList();
    paramMovie.add("isom");
    paramMovie.add("iso2");
    paramMovie.add("avc1");
    return new FileTypeBox("isom", 0L, paramMovie);
  }
  
  protected Box createMdat(final long paramLong1, long paramLong2, final Track paramTrack, int paramInt)
  {
    new Box()
    {
      Container parent;
      long size_ = -1L;
      
      public void getBox(WritableByteChannel paramAnonymousWritableByteChannel)
        throws IOException
      {
        Object localObject = ByteBuffer.allocate(8);
        IsoTypeWriter.writeUInt32((ByteBuffer)localObject, CastUtils.l2i(getSize()));
        ((ByteBuffer)localObject).put(IsoFile.fourCCtoBytes(getType()));
        ((ByteBuffer)localObject).rewind();
        paramAnonymousWritableByteChannel.write((ByteBuffer)localObject);
        localObject = FragmentedMp4Builder.this.getSamples(paramLong1, paramTrack, this.val$track, this.val$i).iterator();
        for (;;)
        {
          if (!((Iterator)localObject).hasNext()) {
            return;
          }
          ((Sample)((Iterator)localObject).next()).writeTo(paramAnonymousWritableByteChannel);
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
        if (this.size_ != -1L) {
          return this.size_;
        }
        long l = 8L;
        Iterator localIterator = FragmentedMp4Builder.this.getSamples(paramLong1, paramTrack, this.val$track, this.val$i).iterator();
        for (;;)
        {
          if (!localIterator.hasNext())
          {
            this.size_ = l;
            return l;
          }
          l += ((Sample)localIterator.next()).getSize();
        }
      }
      
      public String getType()
      {
        return "mdat";
      }
      
      public void parse(DataSource paramAnonymousDataSource, ByteBuffer paramAnonymousByteBuffer, long paramAnonymousLong, BoxParser paramAnonymousBoxParser)
        throws IOException
      {}
      
      public void setParent(Container paramAnonymousContainer)
      {
        this.parent = paramAnonymousContainer;
      }
    };
  }
  
  protected Box createMdhd(Movie paramMovie, Track paramTrack)
  {
    paramMovie = new MediaHeaderBox();
    paramMovie.setCreationTime(paramTrack.getTrackMetaData().getCreationTime());
    paramMovie.setModificationTime(getDate());
    paramMovie.setDuration(0L);
    paramMovie.setTimescale(paramTrack.getTrackMetaData().getTimescale());
    paramMovie.setLanguage(paramTrack.getTrackMetaData().getLanguage());
    return paramMovie;
  }
  
  protected Box createMdia(Track paramTrack, Movie paramMovie)
  {
    MediaBox localMediaBox = new MediaBox();
    localMediaBox.addBox(createMdhd(paramMovie, paramTrack));
    localMediaBox.addBox(createMdiaHdlr(paramTrack, paramMovie));
    localMediaBox.addBox(createMinf(paramTrack, paramMovie));
    return localMediaBox;
  }
  
  protected Box createMdiaHdlr(Track paramTrack, Movie paramMovie)
  {
    paramMovie = new HandlerBox();
    paramMovie.setHandlerType(paramTrack.getHandler());
    return paramMovie;
  }
  
  protected void createMfhd(long paramLong1, long paramLong2, Track paramTrack, int paramInt, MovieFragmentBox paramMovieFragmentBox)
  {
    paramTrack = new MovieFragmentHeaderBox();
    paramTrack.setSequenceNumber(paramInt);
    paramMovieFragmentBox.addBox(paramTrack);
  }
  
  protected Box createMfra(Movie paramMovie, Container paramContainer)
  {
    MovieFragmentRandomAccessBox localMovieFragmentRandomAccessBox = new MovieFragmentRandomAccessBox();
    paramMovie = paramMovie.getTracks().iterator();
    for (;;)
    {
      if (!paramMovie.hasNext())
      {
        paramMovie = new MovieFragmentRandomAccessOffsetBox();
        localMovieFragmentRandomAccessBox.addBox(paramMovie);
        paramMovie.setMfraSize(localMovieFragmentRandomAccessBox.getSize());
        return localMovieFragmentRandomAccessBox;
      }
      localMovieFragmentRandomAccessBox.addBox(createTfra((Track)paramMovie.next(), paramContainer));
    }
  }
  
  protected Box createMinf(Track paramTrack, Movie paramMovie)
  {
    MediaInformationBox localMediaInformationBox = new MediaInformationBox();
    if (paramTrack.getHandler().equals("vide")) {
      localMediaInformationBox.addBox(new VideoMediaHeaderBox());
    }
    for (;;)
    {
      localMediaInformationBox.addBox(createDinf(paramMovie, paramTrack));
      localMediaInformationBox.addBox(createStbl(paramMovie, paramTrack));
      return localMediaInformationBox;
      if (paramTrack.getHandler().equals("soun")) {
        localMediaInformationBox.addBox(new SoundMediaHeaderBox());
      } else if (paramTrack.getHandler().equals("text")) {
        localMediaInformationBox.addBox(new NullMediaHeaderBox());
      } else if (paramTrack.getHandler().equals("subt")) {
        localMediaInformationBox.addBox(new SubtitleMediaHeaderBox());
      } else if (paramTrack.getHandler().equals("hint")) {
        localMediaInformationBox.addBox(new HintMediaHeaderBox());
      } else if (paramTrack.getHandler().equals("sbtl")) {
        localMediaInformationBox.addBox(new NullMediaHeaderBox());
      }
    }
  }
  
  protected Box createMoof(long paramLong1, long paramLong2, Track paramTrack, int paramInt)
  {
    MovieFragmentBox localMovieFragmentBox = new MovieFragmentBox();
    createMfhd(paramLong1, paramLong2, paramTrack, paramInt, localMovieFragmentBox);
    createTraf(paramLong1, paramLong2, paramTrack, paramInt, localMovieFragmentBox);
    paramTrack = (TrackRunBox)localMovieFragmentBox.getTrackRunBoxes().get(0);
    paramTrack.setDataOffset(1);
    paramTrack.setDataOffset((int)(8L + localMovieFragmentBox.getSize()));
    return localMovieFragmentBox;
  }
  
  protected List<Box> createMoofMdat(Movie paramMovie)
  {
    LinkedList localLinkedList = new LinkedList();
    HashMap localHashMap = new HashMap();
    int i = 0;
    Iterator localIterator = paramMovie.getTracks().iterator();
    int k;
    int j;
    Track localTrack;
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        k = 1;
        j = 0;
        if (j < i) {
          break;
        }
        return localLinkedList;
      }
      localTrack = (Track)localIterator.next();
      long[] arrayOfLong = this.intersectionFinder.sampleNumbers(localTrack);
      localHashMap.put(localTrack, arrayOfLong);
      i = Math.max(i, arrayOfLong.length);
    }
    localIterator = sortTracksInSequence(paramMovie.getTracks(), j, localHashMap).iterator();
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        j += 1;
        break;
      }
      localTrack = (Track)localIterator.next();
      k = createFragment(localLinkedList, localTrack, (long[])localHashMap.get(localTrack), j, k);
    }
  }
  
  protected Box createMoov(Movie paramMovie)
  {
    MovieBox localMovieBox = new MovieBox();
    localMovieBox.addBox(createMvhd(paramMovie));
    Iterator localIterator = paramMovie.getTracks().iterator();
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        localMovieBox.addBox(createMvex(paramMovie));
        return localMovieBox;
      }
      localMovieBox.addBox(createTrak((Track)localIterator.next(), paramMovie));
    }
  }
  
  protected Box createMvex(Movie paramMovie)
  {
    MovieExtendsBox localMovieExtendsBox = new MovieExtendsBox();
    Object localObject = new MovieExtendsHeaderBox();
    ((MovieExtendsHeaderBox)localObject).setVersion(1);
    Iterator localIterator = paramMovie.getTracks().iterator();
    if (!localIterator.hasNext())
    {
      localMovieExtendsBox.addBox((Box)localObject);
      localObject = paramMovie.getTracks().iterator();
    }
    for (;;)
    {
      if (!((Iterator)localObject).hasNext())
      {
        return localMovieExtendsBox;
        long l = getTrackDuration(paramMovie, (Track)localIterator.next());
        if (((MovieExtendsHeaderBox)localObject).getFragmentDuration() >= l) {
          break;
        }
        ((MovieExtendsHeaderBox)localObject).setFragmentDuration(l);
        break;
      }
      localMovieExtendsBox.addBox(createTrex(paramMovie, (Track)((Iterator)localObject).next()));
    }
  }
  
  protected Box createMvhd(Movie paramMovie)
  {
    MovieHeaderBox localMovieHeaderBox = new MovieHeaderBox();
    localMovieHeaderBox.setVersion(1);
    localMovieHeaderBox.setCreationTime(getDate());
    localMovieHeaderBox.setModificationTime(getDate());
    localMovieHeaderBox.setDuration(0L);
    localMovieHeaderBox.setTimescale(paramMovie.getTimescale());
    long l1 = 0L;
    paramMovie = paramMovie.getTracks().iterator();
    for (;;)
    {
      if (!paramMovie.hasNext())
      {
        localMovieHeaderBox.setNextTrackId(l1 + 1L);
        return localMovieHeaderBox;
      }
      Track localTrack = (Track)paramMovie.next();
      long l2 = l1;
      if (l1 < localTrack.getTrackMetaData().getTrackId()) {
        l2 = localTrack.getTrackMetaData().getTrackId();
      }
      l1 = l2;
    }
  }
  
  protected void createSaio(long paramLong1, long paramLong2, CencEncryptedTrack paramCencEncryptedTrack, int paramInt, TrackFragmentBox paramTrackFragmentBox)
  {
    paramCencEncryptedTrack = (SchemeTypeBox)Path.getPath(paramCencEncryptedTrack.getSampleDescriptionBox(), "enc.[0]/sinf[0]/schm[0]");
    paramCencEncryptedTrack = new SampleAuxiliaryInformationOffsetsBox();
    paramTrackFragmentBox.addBox(paramCencEncryptedTrack);
    assert (paramTrackFragmentBox.getBoxes(TrackRunBox.class).size() == 1) : "Don't know how to deal with multiple Track Run Boxes when encrypting";
    paramCencEncryptedTrack.setAuxInfoType("cenc");
    paramCencEncryptedTrack.setFlags(1);
    paramLong1 = 0L + 8L;
    Object localObject = paramTrackFragmentBox.getBoxes().iterator();
    if (!((Iterator)localObject).hasNext())
    {
      label110:
      localObject = (MovieFragmentBox)paramTrackFragmentBox.getParent();
      paramLong1 += 16L;
      localObject = ((MovieFragmentBox)localObject).getBoxes().iterator();
    }
    for (;;)
    {
      if (!((Iterator)localObject).hasNext()) {}
      Box localBox;
      do
      {
        paramCencEncryptedTrack.setOffsets(new long[] { paramLong1 });
        return;
        localBox = (Box)((Iterator)localObject).next();
        if ((localBox instanceof SampleEncryptionBox))
        {
          paramLong1 += ((SampleEncryptionBox)localBox).getOffsetToFirstIV();
          break label110;
        }
        paramLong1 += localBox.getSize();
        break;
        localBox = (Box)((Iterator)localObject).next();
      } while (localBox == paramTrackFragmentBox);
      paramLong1 += localBox.getSize();
    }
  }
  
  protected void createSaiz(long paramLong1, long paramLong2, CencEncryptedTrack paramCencEncryptedTrack, int paramInt, TrackFragmentBox paramTrackFragmentBox)
  {
    Object localObject1 = paramCencEncryptedTrack.getSampleDescriptionBox();
    Object localObject2 = (SchemeTypeBox)Path.getPath((AbstractContainerBox)localObject1, "enc.[0]/sinf[0]/schm[0]");
    localObject2 = (TrackEncryptionBox)Path.getPath((AbstractContainerBox)localObject1, "enc.[0]/sinf[0]/schi[0]/tenc[0]");
    localObject1 = new SampleAuxiliaryInformationSizesBox();
    ((SampleAuxiliaryInformationSizesBox)localObject1).setAuxInfoType("cenc");
    ((SampleAuxiliaryInformationSizesBox)localObject1).setFlags(1);
    if (paramCencEncryptedTrack.hasSubSampleEncryption())
    {
      localObject2 = new short[CastUtils.l2i(paramLong2 - paramLong1)];
      paramCencEncryptedTrack = paramCencEncryptedTrack.getSampleEncryptionEntries().subList(CastUtils.l2i(paramLong1 - 1L), CastUtils.l2i(paramLong2 - 1L));
      paramInt = 0;
      if (paramInt >= localObject2.length) {
        ((SampleAuxiliaryInformationSizesBox)localObject1).setSampleInfoSizes((short[])localObject2);
      }
    }
    for (;;)
    {
      paramTrackFragmentBox.addBox((Box)localObject1);
      return;
      localObject2[paramInt] = ((short)((CencSampleAuxiliaryDataFormat)paramCencEncryptedTrack.get(paramInt)).getSize());
      paramInt += 1;
      break;
      ((SampleAuxiliaryInformationSizesBox)localObject1).setDefaultSampleInfoSize(((TrackEncryptionBox)localObject2).getDefaultIvSize());
      ((SampleAuxiliaryInformationSizesBox)localObject1).setSampleCount(CastUtils.l2i(paramLong2 - paramLong1));
    }
  }
  
  protected void createSenc(long paramLong1, long paramLong2, CencEncryptedTrack paramCencEncryptedTrack, int paramInt, TrackFragmentBox paramTrackFragmentBox)
  {
    SampleEncryptionBox localSampleEncryptionBox = new SampleEncryptionBox();
    localSampleEncryptionBox.setSubSampleEncryption(paramCencEncryptedTrack.hasSubSampleEncryption());
    localSampleEncryptionBox.setEntries(paramCencEncryptedTrack.getSampleEncryptionEntries().subList(CastUtils.l2i(paramLong1 - 1L), CastUtils.l2i(paramLong2 - 1L)));
    paramTrackFragmentBox.addBox(localSampleEncryptionBox);
  }
  
  protected Box createStbl(Movie paramMovie, Track paramTrack)
  {
    paramMovie = new SampleTableBox();
    createStsd(paramTrack, paramMovie);
    paramMovie.addBox(new TimeToSampleBox());
    paramMovie.addBox(new SampleToChunkBox());
    paramMovie.addBox(new SampleSizeBox());
    paramMovie.addBox(new StaticChunkOffsetBox());
    return paramMovie;
  }
  
  protected void createStsd(Track paramTrack, SampleTableBox paramSampleTableBox)
  {
    paramSampleTableBox.addBox(paramTrack.getSampleDescriptionBox());
  }
  
  protected void createTfdt(long paramLong, Track paramTrack, TrackFragmentBox paramTrackFragmentBox)
  {
    TrackFragmentBaseMediaDecodeTimeBox localTrackFragmentBaseMediaDecodeTimeBox = new TrackFragmentBaseMediaDecodeTimeBox();
    localTrackFragmentBaseMediaDecodeTimeBox.setVersion(1);
    long l = 0L;
    paramTrack = paramTrack.getSampleDurations();
    int i = 1;
    for (;;)
    {
      if (i >= paramLong)
      {
        localTrackFragmentBaseMediaDecodeTimeBox.setBaseMediaDecodeTime(l);
        paramTrackFragmentBox.addBox(localTrackFragmentBaseMediaDecodeTimeBox);
        return;
      }
      l += paramTrack[(i - 1)];
      i += 1;
    }
  }
  
  protected void createTfhd(long paramLong1, long paramLong2, Track paramTrack, int paramInt, TrackFragmentBox paramTrackFragmentBox)
  {
    TrackFragmentHeaderBox localTrackFragmentHeaderBox = new TrackFragmentHeaderBox();
    localTrackFragmentHeaderBox.setDefaultSampleFlags(new SampleFlags());
    localTrackFragmentHeaderBox.setBaseDataOffset(-1L);
    localTrackFragmentHeaderBox.setTrackId(paramTrack.getTrackMetaData().getTrackId());
    localTrackFragmentHeaderBox.setDefaultBaseIsMoof(true);
    paramTrackFragmentBox.addBox(localTrackFragmentHeaderBox);
  }
  
  protected Box createTfra(Track paramTrack, Container paramContainer)
  {
    TrackFragmentRandomAccessBox localTrackFragmentRandomAccessBox = new TrackFragmentRandomAccessBox();
    localTrackFragmentRandomAccessBox.setVersion(1);
    LinkedList localLinkedList1 = new LinkedList();
    Object localObject1 = null;
    Object localObject3 = Path.getPaths(paramContainer, "moov/mvex/trex").iterator();
    long l2;
    long l1;
    Object localObject2;
    for (;;)
    {
      if (!((Iterator)localObject3).hasNext())
      {
        l2 = 0L;
        l1 = 0L;
        localObject2 = paramContainer.getBoxes().iterator();
        if (((Iterator)localObject2).hasNext()) {
          break;
        }
        localTrackFragmentRandomAccessBox.setEntries(localLinkedList1);
        localTrackFragmentRandomAccessBox.setTrackId(paramTrack.getTrackMetaData().getTrackId());
        return localTrackFragmentRandomAccessBox;
      }
      localObject2 = (TrackExtendsBox)((Iterator)localObject3).next();
      if (((TrackExtendsBox)localObject2).getTrackId() == paramTrack.getTrackMetaData().getTrackId()) {
        localObject1 = localObject2;
      }
    }
    localObject3 = (Box)((Iterator)localObject2).next();
    long l3 = l1;
    List localList1;
    int i;
    if ((localObject3 instanceof MovieFragmentBox))
    {
      localList1 = ((MovieFragmentBox)localObject3).getBoxes(TrackFragmentBox.class);
      i = 0;
    }
    List localList2;
    int j;
    for (;;)
    {
      if (i >= localList1.size())
      {
        l3 = l1;
        l2 += ((Box)localObject3).getSize();
        l1 = l3;
        break;
      }
      paramContainer = (TrackFragmentBox)localList1.get(i);
      l3 = l1;
      if (paramContainer.getTrackFragmentHeaderBox().getTrackId() == paramTrack.getTrackMetaData().getTrackId())
      {
        localList2 = paramContainer.getBoxes(TrackRunBox.class);
        j = 0;
        if (j < localList2.size()) {
          break label289;
        }
        l3 = l1;
      }
      i += 1;
      l1 = l3;
    }
    label289:
    LinkedList localLinkedList2 = new LinkedList();
    TrackRunBox localTrackRunBox = (TrackRunBox)localList2.get(j);
    int k = 0;
    label315:
    if (k >= localTrackRunBox.getEntries().size())
    {
      if ((localLinkedList2.size() != localTrackRunBox.getEntries().size()) || (localTrackRunBox.getEntries().size() <= 0)) {
        break label546;
      }
      localLinkedList1.add((TrackFragmentRandomAccessBox.Entry)localLinkedList2.get(0));
    }
    for (;;)
    {
      j += 1;
      break;
      TrackRunBox.Entry localEntry = (TrackRunBox.Entry)localTrackRunBox.getEntries().get(k);
      if ((k == 0) && (localTrackRunBox.isFirstSampleFlagsPresent())) {
        paramContainer = localTrackRunBox.getFirstSampleFlags();
      }
      while ((paramContainer == null) && (paramTrack.getHandler().equals("vide")))
      {
        throw new RuntimeException("Cannot find SampleFlags for video track but it's required to build tfra");
        if (localTrackRunBox.isSampleFlagsPresent()) {
          paramContainer = localEntry.getSampleFlags();
        } else {
          paramContainer = ((TrackExtendsBox)localObject1).getDefaultSampleFlags();
        }
      }
      if ((paramContainer == null) || (paramContainer.getSampleDependsOn() == 2)) {
        localLinkedList2.add(new TrackFragmentRandomAccessBox.Entry(l1, l2, i + 1, j + 1, k + 1));
      }
      l1 += localEntry.getSampleDuration();
      k += 1;
      break label315;
      label546:
      localLinkedList1.addAll(localLinkedList2);
    }
  }
  
  protected Box createTkhd(Movie paramMovie, Track paramTrack)
  {
    paramMovie = new TrackHeaderBox();
    paramMovie.setVersion(1);
    paramMovie.setFlags(7);
    paramMovie.setAlternateGroup(paramTrack.getTrackMetaData().getGroup());
    paramMovie.setCreationTime(paramTrack.getTrackMetaData().getCreationTime());
    paramMovie.setDuration(0L);
    paramMovie.setHeight(paramTrack.getTrackMetaData().getHeight());
    paramMovie.setWidth(paramTrack.getTrackMetaData().getWidth());
    paramMovie.setLayer(paramTrack.getTrackMetaData().getLayer());
    paramMovie.setModificationTime(getDate());
    paramMovie.setTrackId(paramTrack.getTrackMetaData().getTrackId());
    paramMovie.setVolume(paramTrack.getTrackMetaData().getVolume());
    return paramMovie;
  }
  
  protected void createTraf(long paramLong1, long paramLong2, Track paramTrack, int paramInt, MovieFragmentBox paramMovieFragmentBox)
  {
    TrackFragmentBox localTrackFragmentBox = new TrackFragmentBox();
    paramMovieFragmentBox.addBox(localTrackFragmentBox);
    createTfhd(paramLong1, paramLong2, paramTrack, paramInt, localTrackFragmentBox);
    createTfdt(paramLong1, paramTrack, localTrackFragmentBox);
    createTrun(paramLong1, paramLong2, paramTrack, paramInt, localTrackFragmentBox);
    if ((paramTrack instanceof CencEncryptedTrack))
    {
      createSaiz(paramLong1, paramLong2, (CencEncryptedTrack)paramTrack, paramInt, localTrackFragmentBox);
      createSenc(paramLong1, paramLong2, (CencEncryptedTrack)paramTrack, paramInt, localTrackFragmentBox);
      createSaio(paramLong1, paramLong2, (CencEncryptedTrack)paramTrack, paramInt, localTrackFragmentBox);
    }
    Object localObject2 = new HashMap();
    Object localObject3 = paramTrack.getSampleGroups().entrySet().iterator();
    Object localObject1;
    if (!((Iterator)localObject3).hasNext()) {
      localObject1 = ((Map)localObject2).entrySet().iterator();
    }
    Object localObject4;
    Object localObject5;
    for (;;)
    {
      if (!((Iterator)localObject1).hasNext())
      {
        return;
        localObject4 = (Map.Entry)((Iterator)localObject3).next();
        localObject5 = ((GroupEntry)((Map.Entry)localObject4).getKey()).getType();
        localObject1 = (List)((Map)localObject2).get(localObject5);
        paramMovieFragmentBox = (MovieFragmentBox)localObject1;
        if (localObject1 == null)
        {
          paramMovieFragmentBox = new ArrayList();
          ((Map)localObject2).put(localObject5, paramMovieFragmentBox);
        }
        paramMovieFragmentBox.add((GroupEntry)((Map.Entry)localObject4).getKey());
        break;
      }
      localObject2 = (Map.Entry)((Iterator)localObject1).next();
      localObject3 = new SampleGroupDescriptionBox();
      paramMovieFragmentBox = (String)((Map.Entry)localObject2).getKey();
      ((SampleGroupDescriptionBox)localObject3).setGroupEntries((List)((Map.Entry)localObject2).getValue());
      localObject4 = new SampleToGroupBox();
      ((SampleToGroupBox)localObject4).setGroupingType(paramMovieFragmentBox);
      paramMovieFragmentBox = null;
      paramInt = CastUtils.l2i(paramLong1 - 1L);
      if (paramInt < CastUtils.l2i(paramLong2 - 1L)) {
        break label360;
      }
      localTrackFragmentBox.addBox((Box)localObject3);
      localTrackFragmentBox.addBox((Box)localObject4);
    }
    label360:
    int j = 0;
    int i = 0;
    label366:
    if (i >= ((List)((Map.Entry)localObject2).getValue()).size())
    {
      if ((paramMovieFragmentBox != null) && (paramMovieFragmentBox.getGroupDescriptionIndex() == j)) {
        break label498;
      }
      paramMovieFragmentBox = new SampleToGroupBox.Entry(1L, j);
      ((SampleToGroupBox)localObject4).getEntries().add(paramMovieFragmentBox);
    }
    for (;;)
    {
      paramInt += 1;
      break;
      localObject5 = (GroupEntry)((List)((Map.Entry)localObject2).getValue()).get(i);
      if (Arrays.binarySearch((long[])paramTrack.getSampleGroups().get(localObject5), paramInt) >= 0) {
        j = i + 1;
      }
      i += 1;
      break label366;
      label498:
      paramMovieFragmentBox.setSampleCount(paramMovieFragmentBox.getSampleCount() + 1L);
    }
  }
  
  protected Box createTrak(Track paramTrack, Movie paramMovie)
  {
    LOG.fine("Creating Track " + paramTrack);
    TrackBox localTrackBox = new TrackBox();
    localTrackBox.addBox(createTkhd(paramMovie, paramTrack));
    Box localBox = createEdts(paramTrack, paramMovie);
    if (localBox != null) {
      localTrackBox.addBox(localBox);
    }
    localTrackBox.addBox(createMdia(paramTrack, paramMovie));
    return localTrackBox;
  }
  
  protected Box createTrex(Movie paramMovie, Track paramTrack)
  {
    paramMovie = new TrackExtendsBox();
    paramMovie.setTrackId(paramTrack.getTrackMetaData().getTrackId());
    paramMovie.setDefaultSampleDescriptionIndex(1L);
    paramMovie.setDefaultSampleDuration(0L);
    paramMovie.setDefaultSampleSize(0L);
    SampleFlags localSampleFlags = new SampleFlags();
    if (("soun".equals(paramTrack.getHandler())) || ("subt".equals(paramTrack.getHandler())))
    {
      localSampleFlags.setSampleDependsOn(2);
      localSampleFlags.setSampleIsDependedOn(2);
    }
    paramMovie.setDefaultSampleFlags(localSampleFlags);
    return paramMovie;
  }
  
  protected void createTrun(long paramLong1, long paramLong2, Track paramTrack, int paramInt, TrackFragmentBox paramTrackFragmentBox)
  {
    TrackRunBox localTrackRunBox = new TrackRunBox();
    localTrackRunBox.setVersion(1);
    long[] arrayOfLong = getSampleSizes(paramLong1, paramLong2, paramTrack, paramInt);
    localTrackRunBox.setSampleDurationPresent(true);
    localTrackRunBox.setSampleSizePresent(true);
    ArrayList localArrayList = new ArrayList(CastUtils.l2i(paramLong2 - paramLong1));
    Object localObject = paramTrack.getCompositionTimeEntries();
    int i = 0;
    label117:
    long l1;
    label132:
    long l2;
    if ((localObject != null) && (((List)localObject).size() > 0))
    {
      localObject = (CompositionTimeToSample.Entry[])((List)localObject).toArray(new CompositionTimeToSample.Entry[((List)localObject).size()]);
      if (localObject == null) {
        break label241;
      }
      paramInt = localObject[0].getCount();
      l1 = paramInt;
      if (l1 <= 0L) {
        break label247;
      }
      bool = true;
      localTrackRunBox.setSampleCompositionTimeOffsetPresent(bool);
      l2 = 1L;
      paramInt = i;
      label146:
      if (l2 < paramLong1) {
        break label253;
      }
      if (((paramTrack.getSampleDependencies() != null) && (!paramTrack.getSampleDependencies().isEmpty())) || ((paramTrack.getSyncSamples() != null) && (paramTrack.getSyncSamples().length != 0))) {
        break label334;
      }
    }
    label241:
    label247:
    label253:
    label334:
    for (boolean bool = false;; bool = true)
    {
      localTrackRunBox.setSampleFlagsPresent(bool);
      i = 0;
      if (i < arrayOfLong.length) {
        break label340;
      }
      localTrackRunBox.setEntries(localArrayList);
      paramTrackFragmentBox.addBox(localTrackRunBox);
      return;
      localObject = null;
      break;
      paramInt = -1;
      break label117;
      bool = false;
      break label132;
      paramLong2 = l1;
      i = paramInt;
      if (localObject != null)
      {
        l1 -= 1L;
        paramLong2 = l1;
        i = paramInt;
        if (l1 == 0L)
        {
          paramLong2 = l1;
          i = paramInt;
          if (localObject.length - paramInt > 1)
          {
            i = paramInt + 1;
            paramLong2 = localObject[i].getCount();
          }
        }
      }
      l2 += 1L;
      l1 = paramLong2;
      paramInt = i;
      break label146;
    }
    label340:
    TrackRunBox.Entry localEntry = new TrackRunBox.Entry();
    localEntry.setSampleSize(arrayOfLong[i]);
    SampleFlags localSampleFlags;
    if (bool)
    {
      localSampleFlags = new SampleFlags();
      if ((paramTrack.getSampleDependencies() != null) && (!paramTrack.getSampleDependencies().isEmpty()))
      {
        SampleDependencyTypeBox.Entry localEntry1 = (SampleDependencyTypeBox.Entry)paramTrack.getSampleDependencies().get(i);
        localSampleFlags.setSampleDependsOn(localEntry1.getSampleDependsOn());
        localSampleFlags.setSampleIsDependedOn(localEntry1.getSampleIsDependentOn());
        localSampleFlags.setSampleHasRedundancy(localEntry1.getSampleHasRedundancy());
      }
      if ((paramTrack.getSyncSamples() != null) && (paramTrack.getSyncSamples().length > 0))
      {
        if (Arrays.binarySearch(paramTrack.getSyncSamples(), i + paramLong1) < 0) {
          break label632;
        }
        localSampleFlags.setSampleIsDifferenceSample(false);
        localSampleFlags.setSampleDependsOn(2);
      }
    }
    for (;;)
    {
      localEntry.setSampleFlags(localSampleFlags);
      localEntry.setSampleDuration(paramTrack.getSampleDurations()[CastUtils.l2i(i + paramLong1 - 1L)]);
      paramLong2 = l1;
      int j = paramInt;
      if (localObject != null)
      {
        localEntry.setSampleCompositionTimeOffset(localObject[paramInt].getOffset());
        l1 -= 1L;
        paramLong2 = l1;
        j = paramInt;
        if (l1 == 0L)
        {
          paramLong2 = l1;
          j = paramInt;
          if (localObject.length - paramInt > 1)
          {
            j = paramInt + 1;
            paramLong2 = localObject[j].getCount();
          }
        }
      }
      localArrayList.add(localEntry);
      i += 1;
      l1 = paramLong2;
      paramInt = j;
      break;
      label632:
      localSampleFlags.setSampleIsDifferenceSample(true);
      localSampleFlags.setSampleDependsOn(1);
    }
  }
  
  public Date getDate()
  {
    return new Date();
  }
  
  public FragmentIntersectionFinder getFragmentIntersectionFinder()
  {
    return this.intersectionFinder;
  }
  
  protected long[] getSampleSizes(long paramLong1, long paramLong2, Track paramTrack, int paramInt)
  {
    paramTrack = getSamples(paramLong1, paramLong2, paramTrack, paramInt);
    long[] arrayOfLong = new long[paramTrack.size()];
    paramInt = 0;
    for (;;)
    {
      if (paramInt >= arrayOfLong.length) {
        return arrayOfLong;
      }
      arrayOfLong[paramInt] = ((Sample)paramTrack.get(paramInt)).getSize();
      paramInt += 1;
    }
  }
  
  protected List<Sample> getSamples(long paramLong1, long paramLong2, Track paramTrack, int paramInt)
  {
    return paramTrack.getSamples().subList(CastUtils.l2i(paramLong1) - 1, CastUtils.l2i(paramLong2) - 1);
  }
  
  public void setIntersectionFinder(FragmentIntersectionFinder paramFragmentIntersectionFinder)
  {
    this.intersectionFinder = paramFragmentIntersectionFinder;
  }
  
  protected List<Track> sortTracksInSequence(List<Track> paramList, final int paramInt, final Map<Track, long[]> paramMap)
  {
    paramList = new LinkedList(paramList);
    Collections.sort(paramList, new Comparator()
    {
      public int compare(Track paramAnonymousTrack1, Track paramAnonymousTrack2)
      {
        long l4 = ((long[])paramMap.get(paramAnonymousTrack1))[paramInt];
        long l3 = ((long[])paramMap.get(paramAnonymousTrack2))[paramInt];
        long[] arrayOfLong1 = paramAnonymousTrack1.getSampleDurations();
        long[] arrayOfLong2 = paramAnonymousTrack2.getSampleDurations();
        long l1 = 0L;
        long l2 = 0L;
        int i = 1;
        if (i >= l4) {
          i = 1;
        }
        for (;;)
        {
          if (i >= l3)
          {
            return (int)((l1 / paramAnonymousTrack1.getTrackMetaData().getTimescale() - l2 / paramAnonymousTrack2.getTrackMetaData().getTimescale()) * 100.0D);
            l1 += arrayOfLong1[(i - 1)];
            i += 1;
            break;
          }
          l2 += arrayOfLong2[(i - 1)];
          i += 1;
        }
      }
    });
    return paramList;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/builder/FragmentedMp4Builder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */