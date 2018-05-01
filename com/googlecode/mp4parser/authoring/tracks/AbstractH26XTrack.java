package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.SampleDependencyTypeBox.Entry;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.SampleImpl;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractH26XTrack
  extends AbstractTrack
{
  static int BUFFER = 67107840;
  protected List<CompositionTimeToSample.Entry> ctts = new ArrayList();
  private DataSource dataSource;
  protected long[] decodingTimes;
  protected List<SampleDependencyTypeBox.Entry> sdtp = new ArrayList();
  protected List<Integer> stss = new ArrayList();
  TrackMetaData trackMetaData = new TrackMetaData();
  
  public AbstractH26XTrack(DataSource paramDataSource)
  {
    super(paramDataSource.toString());
    this.dataSource = paramDataSource;
  }
  
  static InputStream cleanBuffer(InputStream paramInputStream)
  {
    return new CleanInputStream(paramInputStream);
  }
  
  protected static byte[] toArray(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer = paramByteBuffer.duplicate();
    byte[] arrayOfByte = new byte[paramByteBuffer.remaining()];
    paramByteBuffer.get(arrayOfByte, 0, arrayOfByte.length);
    return arrayOfByte;
  }
  
  public void close()
    throws IOException
  {
    this.dataSource.close();
  }
  
  protected Sample createSampleObject(List<? extends ByteBuffer> paramList)
  {
    byte[] arrayOfByte = new byte[paramList.size() * 4];
    Object localObject = ByteBuffer.wrap(arrayOfByte);
    Iterator localIterator = paramList.iterator();
    int i;
    if (!localIterator.hasNext())
    {
      localObject = new ByteBuffer[paramList.size() * 2];
      i = 0;
    }
    for (;;)
    {
      if (i >= paramList.size())
      {
        return new SampleImpl((ByteBuffer[])localObject);
        ((ByteBuffer)localObject).putInt(((ByteBuffer)localIterator.next()).remaining());
        break;
      }
      localObject[(i * 2)] = ByteBuffer.wrap(arrayOfByte, i * 4, 4);
      localObject[(i * 2 + 1)] = ((ByteBuffer)paramList.get(i));
      i += 1;
    }
  }
  
  protected ByteBuffer findNextNal(LookAhead paramLookAhead)
    throws IOException
  {
    try
    {
      if (paramLookAhead.nextThreeEquals001()) {
        paramLookAhead.discardNext3AndMarkStart();
      }
      for (;;)
      {
        if (paramLookAhead.nextThreeEquals000or001orEof())
        {
          return paramLookAhead.getNal();
          paramLookAhead.discardByte();
          break;
        }
        paramLookAhead.discardByte();
      }
      return null;
    }
    catch (EOFException paramLookAhead) {}
  }
  
  public List<CompositionTimeToSample.Entry> getCompositionTimeEntries()
  {
    return this.ctts;
  }
  
  public List<SampleDependencyTypeBox.Entry> getSampleDependencies()
  {
    return this.sdtp;
  }
  
  public long[] getSampleDurations()
  {
    return this.decodingTimes;
  }
  
  public long[] getSyncSamples()
  {
    long[] arrayOfLong = new long[this.stss.size()];
    int i = 0;
    for (;;)
    {
      if (i >= this.stss.size()) {
        return arrayOfLong;
      }
      arrayOfLong[i] = ((Integer)this.stss.get(i)).intValue();
      i += 1;
    }
  }
  
  public TrackMetaData getTrackMetaData()
  {
    return this.trackMetaData;
  }
  
  public static class LookAhead
  {
    ByteBuffer buffer;
    long bufferStartPos = 0L;
    DataSource dataSource;
    int inBufferPos = 0;
    long start;
    
    public LookAhead(DataSource paramDataSource)
      throws IOException
    {
      this.dataSource = paramDataSource;
      fillBuffer();
    }
    
    public void discardByte()
    {
      this.inBufferPos += 1;
    }
    
    public void discardNext3AndMarkStart()
    {
      this.inBufferPos += 3;
      this.start = (this.bufferStartPos + this.inBufferPos);
    }
    
    public void fillBuffer()
      throws IOException
    {
      this.buffer = this.dataSource.map(this.bufferStartPos, Math.min(this.dataSource.size() - this.bufferStartPos, AbstractH26XTrack.BUFFER));
    }
    
    public ByteBuffer getNal()
    {
      if (this.start >= this.bufferStartPos)
      {
        this.buffer.position((int)(this.start - this.bufferStartPos));
        ByteBuffer localByteBuffer = this.buffer.slice();
        localByteBuffer.limit((int)(this.inBufferPos - (this.start - this.bufferStartPos)));
        return (ByteBuffer)localByteBuffer;
      }
      throw new RuntimeException("damn! NAL exceeds buffer");
    }
    
    public boolean nextThreeEquals000or001orEof()
      throws IOException
    {
      if (this.buffer.limit() - this.inBufferPos >= 3) {
        if ((this.buffer.get(this.inBufferPos) != 0) || (this.buffer.get(this.inBufferPos + 1) != 0) || ((this.buffer.get(this.inBufferPos + 2) != 0) && (this.buffer.get(this.inBufferPos + 2) != 1))) {}
      }
      do
      {
        return true;
        return false;
        if (this.bufferStartPos + this.inBufferPos + 3L <= this.dataSource.size()) {
          break;
        }
      } while (this.bufferStartPos + this.inBufferPos == this.dataSource.size());
      return false;
      this.bufferStartPos = this.start;
      this.inBufferPos = 0;
      fillBuffer();
      return nextThreeEquals000or001orEof();
    }
    
    public boolean nextThreeEquals001()
      throws IOException
    {
      if (this.buffer.limit() - this.inBufferPos >= 3) {
        return (this.buffer.get(this.inBufferPos) == 0) && (this.buffer.get(this.inBufferPos + 1) == 0) && (this.buffer.get(this.inBufferPos + 2) == 1);
      }
      if (this.bufferStartPos + this.inBufferPos + 3L >= this.dataSource.size()) {
        throw new EOFException();
      }
      return false;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/tracks/AbstractH26XTrack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */