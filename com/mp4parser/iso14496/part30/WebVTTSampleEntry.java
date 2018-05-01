package com.mp4parser.iso14496.part30;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.boxes.sampleentry.AbstractSampleEntry;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.util.Path;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class WebVTTSampleEntry
  extends AbstractSampleEntry
{
  public static final String TYPE = "wvtt";
  
  public WebVTTSampleEntry()
  {
    super("wvtt");
  }
  
  public void getBox(WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    paramWritableByteChannel.write(getHeader());
    writeContainer(paramWritableByteChannel);
  }
  
  public WebVTTConfigurationBox getConfig()
  {
    return (WebVTTConfigurationBox)Path.getPath(this, "vttC");
  }
  
  public WebVTTSourceLabelBox getSourceLabel()
  {
    return (WebVTTSourceLabelBox)Path.getPath(this, "vlab");
  }
  
  public void parse(DataSource paramDataSource, ByteBuffer paramByteBuffer, long paramLong, BoxParser paramBoxParser)
    throws IOException
  {
    initContainer(paramDataSource, paramLong, paramBoxParser);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/mp4parser/iso14496/part30/WebVTTSampleEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */