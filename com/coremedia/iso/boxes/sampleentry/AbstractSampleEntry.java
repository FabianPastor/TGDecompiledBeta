package com.coremedia.iso.boxes.sampleentry;

import com.coremedia.iso.BoxParser;
import com.googlecode.mp4parser.AbstractContainerBox;
import com.googlecode.mp4parser.DataSource;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public abstract class AbstractSampleEntry
  extends AbstractContainerBox
  implements SampleEntry
{
  protected int dataReferenceIndex = 1;
  
  protected AbstractSampleEntry(String paramString)
  {
    super(paramString);
  }
  
  public abstract void getBox(WritableByteChannel paramWritableByteChannel)
    throws IOException;
  
  public int getDataReferenceIndex()
  {
    return this.dataReferenceIndex;
  }
  
  public abstract void parse(DataSource paramDataSource, ByteBuffer paramByteBuffer, long paramLong, BoxParser paramBoxParser)
    throws IOException;
  
  public void setDataReferenceIndex(int paramInt)
  {
    this.dataReferenceIndex = paramInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/sampleentry/AbstractSampleEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */