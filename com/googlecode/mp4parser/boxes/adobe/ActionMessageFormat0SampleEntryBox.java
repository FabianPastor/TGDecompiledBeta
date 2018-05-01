package com.googlecode.mp4parser.boxes.adobe;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.sampleentry.AbstractSampleEntry;
import com.googlecode.mp4parser.DataSource;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class ActionMessageFormat0SampleEntryBox
  extends AbstractSampleEntry
{
  public static final String TYPE = "amf0";
  
  public ActionMessageFormat0SampleEntryBox()
  {
    super("amf0");
  }
  
  public void getBox(WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    paramWritableByteChannel.write(getHeader());
    ByteBuffer localByteBuffer = ByteBuffer.allocate(8);
    localByteBuffer.position(6);
    IsoTypeWriter.writeUInt16(localByteBuffer, this.dataReferenceIndex);
    paramWritableByteChannel.write((ByteBuffer)localByteBuffer.rewind());
    writeContainer(paramWritableByteChannel);
  }
  
  public long getSize()
  {
    long l = getContainerSize();
    if ((this.largeBox) || (l + 8L >= 4294967296L)) {}
    for (int i = 16;; i = 8) {
      return i + (l + 8L);
    }
  }
  
  public void parse(DataSource paramDataSource, ByteBuffer paramByteBuffer, long paramLong, BoxParser paramBoxParser)
    throws IOException
  {
    paramByteBuffer = ByteBuffer.allocate(8);
    paramDataSource.read(paramByteBuffer);
    paramByteBuffer.position(6);
    this.dataReferenceIndex = IsoTypeReader.readUInt16(paramByteBuffer);
    initContainer(paramDataSource, paramLong - 8L, paramBoxParser);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/adobe/ActionMessageFormat0SampleEntryBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */