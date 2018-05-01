package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class HandlerBox
  extends AbstractFullBox
{
  public static final String TYPE = "hdlr";
  public static final Map<String, String> readableTypes;
  private long a;
  private long b;
  private long c;
  private String handlerType;
  private String name = null;
  private long shouldBeZeroButAppleWritesHereSomeValue;
  private boolean zeroTerm = true;
  
  static
  {
    ajc$preClinit();
    HashMap localHashMap = new HashMap();
    localHashMap.put("odsm", "ObjectDescriptorStream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
    localHashMap.put("crsm", "ClockReferenceStream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
    localHashMap.put("sdsm", "SceneDescriptionStream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
    localHashMap.put("m7sm", "MPEG7Stream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
    localHashMap.put("ocsm", "ObjectContentInfoStream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
    localHashMap.put("ipsm", "IPMP Stream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
    localHashMap.put("mjsm", "MPEG-J Stream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
    localHashMap.put("mdir", "Apple Meta Data iTunes Reader");
    localHashMap.put("mp7b", "MPEG-7 binary XML");
    localHashMap.put("mp7t", "MPEG-7 XML");
    localHashMap.put("vide", "Video Track");
    localHashMap.put("soun", "Sound Track");
    localHashMap.put("hint", "Hint Track");
    localHashMap.put("appl", "Apple specific");
    localHashMap.put("meta", "Timed Metadata track - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
    readableTypes = Collections.unmodifiableMap(localHashMap);
  }
  
  public HandlerBox()
  {
    super("hdlr");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.shouldBeZeroButAppleWritesHereSomeValue = IsoTypeReader.readUInt32(paramByteBuffer);
    this.handlerType = IsoTypeReader.read4cc(paramByteBuffer);
    this.a = IsoTypeReader.readUInt32(paramByteBuffer);
    this.b = IsoTypeReader.readUInt32(paramByteBuffer);
    this.c = IsoTypeReader.readUInt32(paramByteBuffer);
    if (paramByteBuffer.remaining() > 0)
    {
      this.name = IsoTypeReader.readString(paramByteBuffer, paramByteBuffer.remaining());
      if (this.name.endsWith("\000"))
      {
        this.name = this.name.substring(0, this.name.length() - 1);
        this.zeroTerm = true;
      }
    }
    for (;;)
    {
      return;
      this.zeroTerm = false;
      continue;
      this.zeroTerm = false;
    }
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.shouldBeZeroButAppleWritesHereSomeValue);
    paramByteBuffer.put(IsoFile.fourCCtoBytes(this.handlerType));
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.a);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.b);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.c);
    if (this.name != null) {
      paramByteBuffer.put(Utf8.convert(this.name));
    }
    if (this.zeroTerm) {
      paramByteBuffer.put((byte)0);
    }
  }
  
  protected long getContentSize()
  {
    if (this.zeroTerm) {}
    for (long l = Utf8.utf8StringLengthInBytes(this.name) + 25;; l = Utf8.utf8StringLengthInBytes(this.name) + 24) {
      return l;
    }
  }
  
  public String getHandlerType()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.handlerType;
  }
  
  public String getHumanReadableTrackType()
  {
    Object localObject = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    if (readableTypes.get(this.handlerType) != null) {}
    for (localObject = (String)readableTypes.get(this.handlerType);; localObject = "Unknown Handler Type") {
      return (String)localObject;
    }
  }
  
  public String getName()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.name;
  }
  
  public void setHandlerType(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.handlerType = paramString;
  }
  
  public void setName(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.name = paramString;
  }
  
  public String toString()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return "HandlerBox[handlerType=" + getHandlerType() + ";name=" + getName() + "]";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/HandlerBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */