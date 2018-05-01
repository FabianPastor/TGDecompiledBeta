package com.coremedia.iso.boxes.apple;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class AppleDataReferenceBox
  extends AbstractFullBox
{
  public static final String TYPE = "rdrf";
  private String dataReference;
  private int dataReferenceSize;
  private String dataReferenceType;
  
  static {}
  
  public AppleDataReferenceBox()
  {
    super("rdrf");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.dataReferenceType = IsoTypeReader.read4cc(paramByteBuffer);
    this.dataReferenceSize = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
    this.dataReference = IsoTypeReader.readString(paramByteBuffer, this.dataReferenceSize);
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    paramByteBuffer.put(IsoFile.fourCCtoBytes(this.dataReferenceType));
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.dataReferenceSize);
    paramByteBuffer.put(Utf8.convert(this.dataReference));
  }
  
  protected long getContentSize()
  {
    return this.dataReferenceSize + 12;
  }
  
  public String getDataReference()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.dataReference;
  }
  
  public long getDataReferenceSize()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.dataReferenceSize;
  }
  
  public String getDataReferenceType()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.dataReferenceType;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/apple/AppleDataReferenceBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */