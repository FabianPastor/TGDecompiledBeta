package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.SampleDependencyTypeBox.Entry;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.SampleImpl;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.AbstractList;
import java.util.List;

public class ReplaceSampleTrack
  extends AbstractTrack
{
  Track origTrack;
  private Sample sampleContent;
  private long sampleNumber;
  private List<Sample> samples;
  
  public ReplaceSampleTrack(Track paramTrack, long paramLong, ByteBuffer paramByteBuffer)
  {
    super("replace(" + paramTrack.getName() + ")");
    this.origTrack = paramTrack;
    this.sampleNumber = paramLong;
    this.sampleContent = new SampleImpl(paramByteBuffer);
    this.samples = new ReplaceASingleEntryList(null);
  }
  
  public void close()
    throws IOException
  {
    this.origTrack.close();
  }
  
  public List<CompositionTimeToSample.Entry> getCompositionTimeEntries()
  {
    return this.origTrack.getCompositionTimeEntries();
  }
  
  public String getHandler()
  {
    return this.origTrack.getHandler();
  }
  
  public List<SampleDependencyTypeBox.Entry> getSampleDependencies()
  {
    return this.origTrack.getSampleDependencies();
  }
  
  public SampleDescriptionBox getSampleDescriptionBox()
  {
    return this.origTrack.getSampleDescriptionBox();
  }
  
  public long[] getSampleDurations()
  {
    try
    {
      long[] arrayOfLong = this.origTrack.getSampleDurations();
      return arrayOfLong;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public List<Sample> getSamples()
  {
    return this.samples;
  }
  
  public SubSampleInformationBox getSubsampleInformationBox()
  {
    return this.origTrack.getSubsampleInformationBox();
  }
  
  public long[] getSyncSamples()
  {
    try
    {
      long[] arrayOfLong = this.origTrack.getSyncSamples();
      return arrayOfLong;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public TrackMetaData getTrackMetaData()
  {
    return this.origTrack.getTrackMetaData();
  }
  
  private class ReplaceASingleEntryList
    extends AbstractList<Sample>
  {
    private ReplaceASingleEntryList() {}
    
    public Sample get(int paramInt)
    {
      if (ReplaceSampleTrack.this.sampleNumber == paramInt) {
        return ReplaceSampleTrack.this.sampleContent;
      }
      return (Sample)ReplaceSampleTrack.this.origTrack.getSamples().get(paramInt);
    }
    
    public int size()
    {
      return ReplaceSampleTrack.this.origTrack.getSamples().size();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/tracks/ReplaceSampleTrack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */