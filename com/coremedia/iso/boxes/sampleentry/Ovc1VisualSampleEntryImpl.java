package com.coremedia.iso.boxes.sampleentry;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.util.CastUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class Ovc1VisualSampleEntryImpl
  extends AbstractSampleEntry
{
  public static final String TYPE = "ovc1";
  private byte[] vc1Content = new byte[0];
  
  public Ovc1VisualSampleEntryImpl()
  {
    super("ovc1");
  }
  
  public void getBox(WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    paramWritableByteChannel.write(getHeader());
    ByteBuffer localByteBuffer = ByteBuffer.allocate(8);
    localByteBuffer.position(6);
    IsoTypeWriter.writeUInt16(localByteBuffer, this.dataReferenceIndex);
    paramWritableByteChannel.write((ByteBuffer)localByteBuffer.rewind());
    paramWritableByteChannel.write(ByteBuffer.wrap(this.vc1Content));
  }
  
  public long getSize()
  {
    if ((this.largeBox) || (this.vc1Content.length + 16 >= 4294967296L)) {}
    for (int i = 16;; i = 8)
    {
      long l = i;
      return this.vc1Content.length + l + 8L;
    }
  }
  
  public byte[] getVc1Content()
  {
    return this.vc1Content;
  }
  
  public void parse(DataSource paramDataSource, ByteBuffer paramByteBuffer, long paramLong, BoxParser paramBoxParser)
    throws IOException
  {
    paramByteBuffer = ByteBuffer.allocate(CastUtils.l2i(paramLong));
    paramDataSource.read(paramByteBuffer);
    paramByteBuffer.position(6);
    this.dataReferenceIndex = IsoTypeReader.readUInt16(paramByteBuffer);
    this.vc1Content = new byte[paramByteBuffer.remaining()];
    paramByteBuffer.get(this.vc1Content);
  }
  
  public void setVc1Content(byte[] paramArrayOfByte)
  {
    this.vc1Content = paramArrayOfByte;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/sampleentry/Ovc1VisualSampleEntryImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */