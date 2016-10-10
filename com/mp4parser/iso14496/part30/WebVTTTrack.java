package com.mp4parser.iso14496.part30;

import com.coremedia.iso.Utf8;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.util.CastUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class WebVTTTrack
  extends AbstractTrack
{
  WebVTTSampleEntry sampleEntry = new WebVTTSampleEntry();
  List<Sample> samples = new ArrayList();
  String[] subs;
  
  public WebVTTTrack(DataSource paramDataSource)
    throws IOException
  {
    super(paramDataSource.toString());
    this.sampleEntry.addBox(new WebVTTConfigurationBox());
    this.sampleEntry.addBox(new WebVTTSourceLabelBox());
    ByteBuffer localByteBuffer = paramDataSource.map(0L, CastUtils.l2i(paramDataSource.size()));
    paramDataSource = new byte[CastUtils.l2i(paramDataSource.size())];
    localByteBuffer.get(paramDataSource);
    this.subs = Utf8.convert(paramDataSource).split("\\r?\\n");
    paramDataSource = "";
    int i = 0;
    int j;
    if (i >= this.subs.length) {
      j = i;
    }
    for (;;)
    {
      label125:
      if (j >= this.subs.length) {}
      while (!this.subs[j].isEmpty())
      {
        return;
        paramDataSource = paramDataSource + this.subs[i] + "\n";
        if (this.subs[(i + 1)].isEmpty())
        {
          j = i;
          if (this.subs[(i + 2)].isEmpty()) {
            break label125;
          }
        }
        i += 1;
        break;
      }
      j += 1;
    }
  }
  
  public void close()
    throws IOException
  {}
  
  public String getHandler()
  {
    return null;
  }
  
  public SampleDescriptionBox getSampleDescriptionBox()
  {
    return null;
  }
  
  public long[] getSampleDurations()
  {
    return new long[0];
  }
  
  public List<Sample> getSamples()
  {
    return null;
  }
  
  public TrackMetaData getTrackMetaData()
  {
    return null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/mp4parser/iso14496/part30/WebVTTTrack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */