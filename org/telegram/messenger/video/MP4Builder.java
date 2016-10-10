package org.telegram.messenger.video;

import android.annotation.TargetApi;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaFormat;
import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.DataEntryUrlBox;
import com.coremedia.iso.boxes.DataInformationBox;
import com.coremedia.iso.boxes.DataReferenceBox;
import com.coremedia.iso.boxes.FileTypeBox;
import com.coremedia.iso.boxes.HandlerBox;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MediaInformationBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.SampleToChunkBox;
import com.coremedia.iso.boxes.SampleToChunkBox.Entry;
import com.coremedia.iso.boxes.StaticChunkOffsetBox;
import com.coremedia.iso.boxes.SyncSampleBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TimeToSampleBox.Entry;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.util.Matrix;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@TargetApi(16)
public class MP4Builder
{
  private Mp4Movie currentMp4Movie = null;
  private long dataOffset = 0L;
  private FileChannel fc = null;
  private FileOutputStream fos = null;
  private InterleaveChunkMdat mdat = null;
  private ByteBuffer sizeBuffer = null;
  private HashMap<Track, long[]> track2SampleSizes = new HashMap();
  private boolean writeNewMdat = true;
  private long writedSinceLastMdat = 0L;
  
  private void flushCurrentMdat()
    throws Exception
  {
    long l = this.fc.position();
    this.fc.position(this.mdat.getOffset());
    this.mdat.getBox(this.fc);
    this.fc.position(l);
    this.mdat.setDataOffset(0L);
    this.mdat.setContentSize(0L);
    this.fos.flush();
  }
  
  public static long gcd(long paramLong1, long paramLong2)
  {
    if (paramLong2 == 0L) {
      return paramLong1;
    }
    return gcd(paramLong2, paramLong1 % paramLong2);
  }
  
  public int addTrack(MediaFormat paramMediaFormat, boolean paramBoolean)
    throws Exception
  {
    return this.currentMp4Movie.addTrack(paramMediaFormat, paramBoolean);
  }
  
  protected FileTypeBox createFileTypeBox()
  {
    LinkedList localLinkedList = new LinkedList();
    localLinkedList.add("isom");
    localLinkedList.add("3gp4");
    return new FileTypeBox("isom", 0L, localLinkedList);
  }
  
  public MP4Builder createMovie(Mp4Movie paramMp4Movie)
    throws Exception
  {
    this.currentMp4Movie = paramMp4Movie;
    this.fos = new FileOutputStream(paramMp4Movie.getCacheFile());
    this.fc = this.fos.getChannel();
    paramMp4Movie = createFileTypeBox();
    paramMp4Movie.getBox(this.fc);
    this.dataOffset += paramMp4Movie.getSize();
    this.writedSinceLastMdat += this.dataOffset;
    this.mdat = new InterleaveChunkMdat(null);
    this.sizeBuffer = ByteBuffer.allocateDirect(4);
    return this;
  }
  
  protected MovieBox createMovieBox(Mp4Movie paramMp4Movie)
  {
    MovieBox localMovieBox = new MovieBox();
    Object localObject = new MovieHeaderBox();
    ((MovieHeaderBox)localObject).setCreationTime(new Date());
    ((MovieHeaderBox)localObject).setModificationTime(new Date());
    ((MovieHeaderBox)localObject).setMatrix(Matrix.ROTATE_0);
    long l3 = getTimescale(paramMp4Movie);
    long l1 = 0L;
    Iterator localIterator = paramMp4Movie.getTracks().iterator();
    while (localIterator.hasNext())
    {
      Track localTrack = (Track)localIterator.next();
      long l2 = localTrack.getDuration() * l3 / localTrack.getTimeScale();
      if (l2 > l1) {
        l1 = l2;
      }
    }
    ((MovieHeaderBox)localObject).setDuration(l1);
    ((MovieHeaderBox)localObject).setTimescale(l3);
    ((MovieHeaderBox)localObject).setNextTrackId(paramMp4Movie.getTracks().size() + 1);
    localMovieBox.addBox((Box)localObject);
    localObject = paramMp4Movie.getTracks().iterator();
    while (((Iterator)localObject).hasNext()) {
      localMovieBox.addBox(createTrackBox((Track)((Iterator)localObject).next(), paramMp4Movie));
    }
    return localMovieBox;
  }
  
  protected Box createStbl(Track paramTrack)
  {
    SampleTableBox localSampleTableBox = new SampleTableBox();
    createStsd(paramTrack, localSampleTableBox);
    createStts(paramTrack, localSampleTableBox);
    createStss(paramTrack, localSampleTableBox);
    createStsc(paramTrack, localSampleTableBox);
    createStsz(paramTrack, localSampleTableBox);
    createStco(paramTrack, localSampleTableBox);
    return localSampleTableBox;
  }
  
  protected void createStco(Track paramTrack, SampleTableBox paramSampleTableBox)
  {
    Object localObject = new ArrayList();
    long l1 = -1L;
    paramTrack = paramTrack.getSamples().iterator();
    while (paramTrack.hasNext())
    {
      Sample localSample = (Sample)paramTrack.next();
      long l3 = localSample.getOffset();
      long l2 = l1;
      if (l1 != -1L)
      {
        l2 = l1;
        if (l1 != l3) {
          l2 = -1L;
        }
      }
      if (l2 == -1L) {
        ((ArrayList)localObject).add(Long.valueOf(l3));
      }
      l1 = l3 + localSample.getSize();
    }
    paramTrack = new long[((ArrayList)localObject).size()];
    int i = 0;
    while (i < ((ArrayList)localObject).size())
    {
      paramTrack[i] = ((Long)((ArrayList)localObject).get(i)).longValue();
      i += 1;
    }
    localObject = new StaticChunkOffsetBox();
    ((StaticChunkOffsetBox)localObject).setChunkOffsets(paramTrack);
    paramSampleTableBox.addBox((Box)localObject);
  }
  
  protected void createStsc(Track paramTrack, SampleTableBox paramSampleTableBox)
  {
    SampleToChunkBox localSampleToChunkBox = new SampleToChunkBox();
    localSampleToChunkBox.setEntries(new LinkedList());
    int n = 1;
    int j = 0;
    int i = -1;
    int i4 = paramTrack.getSamples().size();
    int m = 0;
    if (m < i4)
    {
      Sample localSample = (Sample)paramTrack.getSamples().get(m);
      long l1 = localSample.getOffset();
      long l2 = localSample.getSize();
      int i1 = j + 1;
      int k = 0;
      if (m != i4 - 1) {
        if (l1 + l2 == ((Sample)paramTrack.getSamples().get(m + 1)).getOffset()) {}
      }
      for (k = 1;; k = 1)
      {
        int i3 = n;
        j = i1;
        int i2 = i;
        if (k != 0)
        {
          j = i;
          if (i != i1)
          {
            localSampleToChunkBox.getEntries().add(new SampleToChunkBox.Entry(n, i1, 1L));
            j = i1;
          }
          i = 0;
          i3 = n + 1;
          i2 = j;
          j = i;
        }
        m += 1;
        n = i3;
        i = i2;
        break;
      }
    }
    paramSampleTableBox.addBox(localSampleToChunkBox);
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
    Iterator localIterator = paramTrack.getSampleDurations().iterator();
    paramTrack = (Track)localObject;
    while (localIterator.hasNext())
    {
      long l = ((Long)localIterator.next()).longValue();
      if ((paramTrack != null) && (paramTrack.getDelta() == l))
      {
        paramTrack.setCount(paramTrack.getCount() + 1L);
      }
      else
      {
        paramTrack = new TimeToSampleBox.Entry(1L, l);
        localArrayList.add(paramTrack);
      }
    }
    paramTrack = new TimeToSampleBox();
    paramTrack.setEntries(localArrayList);
    paramSampleTableBox.addBox(paramTrack);
  }
  
  protected TrackBox createTrackBox(Track paramTrack, Mp4Movie paramMp4Movie)
  {
    TrackBox localTrackBox = new TrackBox();
    Object localObject1 = new TrackHeaderBox();
    ((TrackHeaderBox)localObject1).setEnabled(true);
    ((TrackHeaderBox)localObject1).setInMovie(true);
    ((TrackHeaderBox)localObject1).setInPreview(true);
    Object localObject2;
    if (paramTrack.isAudio())
    {
      ((TrackHeaderBox)localObject1).setMatrix(Matrix.ROTATE_0);
      ((TrackHeaderBox)localObject1).setAlternateGroup(0);
      ((TrackHeaderBox)localObject1).setCreationTime(paramTrack.getCreationTime());
      ((TrackHeaderBox)localObject1).setDuration(paramTrack.getDuration() * getTimescale(paramMp4Movie) / paramTrack.getTimeScale());
      ((TrackHeaderBox)localObject1).setHeight(paramTrack.getHeight());
      ((TrackHeaderBox)localObject1).setWidth(paramTrack.getWidth());
      ((TrackHeaderBox)localObject1).setLayer(0);
      ((TrackHeaderBox)localObject1).setModificationTime(new Date());
      ((TrackHeaderBox)localObject1).setTrackId(paramTrack.getTrackId() + 1L);
      ((TrackHeaderBox)localObject1).setVolume(paramTrack.getVolume());
      localTrackBox.addBox((Box)localObject1);
      localObject1 = new MediaBox();
      localTrackBox.addBox((Box)localObject1);
      paramMp4Movie = new MediaHeaderBox();
      paramMp4Movie.setCreationTime(paramTrack.getCreationTime());
      paramMp4Movie.setDuration(paramTrack.getDuration());
      paramMp4Movie.setTimescale(paramTrack.getTimeScale());
      paramMp4Movie.setLanguage("eng");
      ((MediaBox)localObject1).addBox(paramMp4Movie);
      localObject2 = new HandlerBox();
      if (!paramTrack.isAudio()) {
        break label351;
      }
    }
    label351:
    for (paramMp4Movie = "SoundHandle";; paramMp4Movie = "VideoHandle")
    {
      ((HandlerBox)localObject2).setName(paramMp4Movie);
      ((HandlerBox)localObject2).setHandlerType(paramTrack.getHandler());
      ((MediaBox)localObject1).addBox((Box)localObject2);
      paramMp4Movie = new MediaInformationBox();
      paramMp4Movie.addBox(paramTrack.getMediaHeaderBox());
      localObject2 = new DataInformationBox();
      DataReferenceBox localDataReferenceBox = new DataReferenceBox();
      ((DataInformationBox)localObject2).addBox(localDataReferenceBox);
      DataEntryUrlBox localDataEntryUrlBox = new DataEntryUrlBox();
      localDataEntryUrlBox.setFlags(1);
      localDataReferenceBox.addBox(localDataEntryUrlBox);
      paramMp4Movie.addBox((Box)localObject2);
      paramMp4Movie.addBox(createStbl(paramTrack));
      ((MediaBox)localObject1).addBox(paramMp4Movie);
      return localTrackBox;
      ((TrackHeaderBox)localObject1).setMatrix(paramMp4Movie.getMatrix());
      break;
    }
  }
  
  public void finishMovie(boolean paramBoolean)
    throws Exception
  {
    if (this.mdat.getContentSize() != 0L) {
      flushCurrentMdat();
    }
    Iterator localIterator = this.currentMp4Movie.getTracks().iterator();
    while (localIterator.hasNext())
    {
      Track localTrack = (Track)localIterator.next();
      ArrayList localArrayList = localTrack.getSamples();
      long[] arrayOfLong = new long[localArrayList.size()];
      int i = 0;
      while (i < arrayOfLong.length)
      {
        arrayOfLong[i] = ((Sample)localArrayList.get(i)).getSize();
        i += 1;
      }
      this.track2SampleSizes.put(localTrack, arrayOfLong);
    }
    createMovieBox(this.currentMp4Movie).getBox(this.fc);
    this.fos.flush();
    this.fc.close();
    this.fos.close();
  }
  
  public long getTimescale(Mp4Movie paramMp4Movie)
  {
    long l = 0L;
    if (!paramMp4Movie.getTracks().isEmpty()) {
      l = ((Track)paramMp4Movie.getTracks().iterator().next()).getTimeScale();
    }
    paramMp4Movie = paramMp4Movie.getTracks().iterator();
    while (paramMp4Movie.hasNext()) {
      l = gcd(((Track)paramMp4Movie.next()).getTimeScale(), l);
    }
    return l;
  }
  
  public boolean writeSampleData(int paramInt, ByteBuffer paramByteBuffer, MediaCodec.BufferInfo paramBufferInfo, boolean paramBoolean)
    throws Exception
  {
    if (this.writeNewMdat)
    {
      this.mdat.setContentSize(0L);
      this.mdat.getBox(this.fc);
      this.mdat.setDataOffset(this.dataOffset);
      this.dataOffset += 16L;
      this.writedSinceLastMdat += 16L;
      this.writeNewMdat = false;
    }
    this.mdat.setContentSize(this.mdat.getContentSize() + paramBufferInfo.size);
    this.writedSinceLastMdat += paramBufferInfo.size;
    boolean bool = false;
    if (this.writedSinceLastMdat >= 32768L)
    {
      flushCurrentMdat();
      this.writeNewMdat = true;
      bool = true;
      this.writedSinceLastMdat -= 32768L;
    }
    this.currentMp4Movie.addSample(paramInt, this.dataOffset, paramBufferInfo);
    int i = paramBufferInfo.offset;
    if (paramBoolean) {}
    for (paramInt = 0;; paramInt = 4)
    {
      paramByteBuffer.position(paramInt + i);
      paramByteBuffer.limit(paramBufferInfo.offset + paramBufferInfo.size);
      if (!paramBoolean)
      {
        this.sizeBuffer.position(0);
        this.sizeBuffer.putInt(paramBufferInfo.size - 4);
        this.sizeBuffer.position(0);
        this.fc.write(this.sizeBuffer);
      }
      this.fc.write(paramByteBuffer);
      this.dataOffset += paramBufferInfo.size;
      if (bool) {
        this.fos.flush();
      }
      return bool;
    }
  }
  
  private class InterleaveChunkMdat
    implements Box
  {
    private long contentSize = 1073741824L;
    private long dataOffset = 0L;
    private Container parent;
    
    private InterleaveChunkMdat() {}
    
    private boolean isSmallBox(long paramLong)
    {
      return 8L + paramLong < 4294967296L;
    }
    
    public void getBox(WritableByteChannel paramWritableByteChannel)
      throws IOException
    {
      ByteBuffer localByteBuffer = ByteBuffer.allocate(16);
      long l = getSize();
      if (isSmallBox(l))
      {
        IsoTypeWriter.writeUInt32(localByteBuffer, l);
        localByteBuffer.put(IsoFile.fourCCtoBytes("mdat"));
        if (!isSmallBox(l)) {
          break label80;
        }
        localByteBuffer.put(new byte[8]);
      }
      for (;;)
      {
        localByteBuffer.rewind();
        paramWritableByteChannel.write(localByteBuffer);
        return;
        IsoTypeWriter.writeUInt32(localByteBuffer, 1L);
        break;
        label80:
        IsoTypeWriter.writeUInt64(localByteBuffer, l);
      }
    }
    
    public long getContentSize()
    {
      return this.contentSize;
    }
    
    public long getOffset()
    {
      return this.dataOffset;
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
    
    public void setContentSize(long paramLong)
    {
      this.contentSize = paramLong;
    }
    
    public void setDataOffset(long paramLong)
    {
      this.dataOffset = paramLong;
    }
    
    public void setParent(Container paramContainer)
    {
      this.parent = paramContainer;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/video/MP4Builder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */