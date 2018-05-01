package com.googlecode.mp4parser.authoring.tracks.mjpeg;

import com.coremedia.iso.Hex;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Edit;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.mp4.ESDescriptorBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ESDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ObjectDescriptorFactory;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.WritableByteChannel;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;

public class OneJpegPerIframe
  extends AbstractTrack
{
  File[] jpegs;
  long[] sampleDurations;
  SampleDescriptionBox stsd;
  long[] syncSamples;
  TrackMetaData trackMetaData = new TrackMetaData();
  
  public OneJpegPerIframe(String paramString, File[] paramArrayOfFile, Track paramTrack)
    throws IOException
  {
    super(paramString);
    this.jpegs = paramArrayOfFile;
    if (paramTrack.getSyncSamples().length != paramArrayOfFile.length) {
      throw new RuntimeException("Number of sync samples doesn't match the number of stills (" + paramTrack.getSyncSamples().length + " vs. " + paramArrayOfFile.length + ")");
    }
    paramString = ImageIO.read(paramArrayOfFile[0]);
    this.trackMetaData.setWidth(paramString.getWidth());
    this.trackMetaData.setHeight(paramString.getHeight());
    this.trackMetaData.setTimescale(paramTrack.getTrackMetaData().getTimescale());
    paramString = paramTrack.getSampleDurations();
    Object localObject = paramTrack.getSyncSamples();
    int j = 1;
    long l1 = 0L;
    this.sampleDurations = new long[localObject.length];
    int i = 1;
    label267:
    double d1;
    label298:
    double d2;
    if (i >= paramString.length)
    {
      this.sampleDurations[(this.sampleDurations.length - 1)] = l1;
      this.stsd = new SampleDescriptionBox();
      paramString = new VisualSampleEntry("mp4v");
      this.stsd.addBox(paramString);
      localObject = new ESDescriptorBox();
      ((ESDescriptorBox)localObject).setData(ByteBuffer.wrap(Hex.decodeHex("038080801B000100048080800D6C11000000000A1CB4000A1CB4068080800102")));
      ((ESDescriptorBox)localObject).setEsDescriptor((ESDescriptor)ObjectDescriptorFactory.createFrom(-1, ByteBuffer.wrap(Hex.decodeHex("038080801B000100048080800D6C11000000000A1CB4000A1CB4068080800102"))));
      paramString.addBox((Box)localObject);
      this.syncSamples = new long[paramArrayOfFile.length];
      i = 0;
      if (i < this.syncSamples.length) {
        break label529;
      }
      d1 = 0.0D;
      i = 1;
      j = 1;
      paramString = paramTrack.getEdits().iterator();
      if (paramString.hasNext()) {
        break label550;
      }
      d2 = d1;
      if (paramTrack.getCompositionTimeEntries() != null)
      {
        d2 = d1;
        if (paramTrack.getCompositionTimeEntries().size() > 0)
        {
          l1 = 0L;
          paramString = Arrays.copyOfRange(CompositionTimeToSample.blowupCompositionTimes(paramTrack.getCompositionTimeEntries()), 0, 50);
          i = 0;
          label360:
          if (i < paramString.length) {
            break label658;
          }
          Arrays.sort(paramString);
          d2 = d1 + paramString[0] / paramTrack.getTrackMetaData().getTimescale();
        }
      }
      if (d2 >= 0.0D) {
        break label694;
      }
      getEdits().add(new Edit((-d2 * getTrackMetaData().getTimescale()), getTrackMetaData().getTimescale(), 1.0D, getDuration() / getTrackMetaData().getTimescale()));
    }
    label529:
    label550:
    label658:
    label694:
    while (d2 <= 0.0D)
    {
      return;
      int k = j;
      long l2 = l1;
      if (j < localObject.length)
      {
        k = j;
        l2 = l1;
        if (i == localObject[j])
        {
          this.sampleDurations[(j - 1)] = l1;
          l2 = 0L;
          k = j + 1;
        }
      }
      l1 = l2 + paramString[i];
      i += 1;
      j = k;
      break;
      this.syncSamples[i] = (i + 1);
      i += 1;
      break label267;
      paramArrayOfFile = (Edit)paramString.next();
      if ((paramArrayOfFile.getMediaTime() == -1L) && (i == 0)) {
        throw new RuntimeException("Cannot accept edit list for processing (1)");
      }
      if ((paramArrayOfFile.getMediaTime() >= 0L) && (j == 0)) {
        throw new RuntimeException("Cannot accept edit list for processing (2)");
      }
      if (paramArrayOfFile.getMediaTime() == -1L)
      {
        d1 += paramArrayOfFile.getSegmentDuration();
        break label298;
      }
      d1 -= paramArrayOfFile.getMediaTime() / paramArrayOfFile.getTimeScale();
      j = 0;
      i = 0;
      break label298;
      paramString[i] = ((int)(paramString[i] + l1));
      l1 += paramTrack.getSampleDurations()[i];
      i += 1;
      break label360;
    }
    getEdits().add(new Edit(-1L, getTrackMetaData().getTimescale(), 1.0D, d2));
    getEdits().add(new Edit(0L, getTrackMetaData().getTimescale(), 1.0D, getDuration() / getTrackMetaData().getTimescale()));
  }
  
  public void close()
    throws IOException
  {}
  
  public String getHandler()
  {
    return "vide";
  }
  
  public SampleDescriptionBox getSampleDescriptionBox()
  {
    return this.stsd;
  }
  
  public long[] getSampleDurations()
  {
    return this.sampleDurations;
  }
  
  public List<Sample> getSamples()
  {
    new AbstractList()
    {
      public Sample get(final int paramAnonymousInt)
      {
        new Sample()
        {
          ByteBuffer sample = null;
          
          public ByteBuffer asByteBuffer()
          {
            if (this.sample == null) {}
            try
            {
              RandomAccessFile localRandomAccessFile = new RandomAccessFile(OneJpegPerIframe.this.jpegs[paramAnonymousInt], "r");
              this.sample = localRandomAccessFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0L, localRandomAccessFile.length());
              return this.sample;
            }
            catch (IOException localIOException)
            {
              throw new RuntimeException(localIOException);
            }
          }
          
          public long getSize()
          {
            return OneJpegPerIframe.this.jpegs[paramAnonymousInt].length();
          }
          
          public void writeTo(WritableByteChannel paramAnonymous2WritableByteChannel)
            throws IOException
          {
            RandomAccessFile localRandomAccessFile = new RandomAccessFile(OneJpegPerIframe.this.jpegs[paramAnonymousInt], "r");
            localRandomAccessFile.getChannel().transferTo(0L, localRandomAccessFile.length(), paramAnonymous2WritableByteChannel);
            localRandomAccessFile.close();
          }
        };
      }
      
      public int size()
      {
        return OneJpegPerIframe.this.jpegs.length;
      }
    };
  }
  
  public long[] getSyncSamples()
  {
    return this.syncSamples;
  }
  
  public TrackMetaData getTrackMetaData()
  {
    return this.trackMetaData;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/tracks/mjpeg/OneJpegPerIframe.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */