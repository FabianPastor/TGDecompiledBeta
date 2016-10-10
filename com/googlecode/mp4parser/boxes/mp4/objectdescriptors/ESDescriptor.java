package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Descriptor(tags={3})
public class ESDescriptor
  extends BaseDescriptor
{
  private static Logger log = Logger.getLogger(ESDescriptor.class.getName());
  int URLFlag;
  int URLLength = 0;
  String URLString;
  DecoderConfigDescriptor decoderConfigDescriptor;
  int dependsOnEsId;
  int esId;
  int oCREsId;
  int oCRstreamFlag;
  List<BaseDescriptor> otherDescriptors = new ArrayList();
  int remoteODFlag;
  SLConfigDescriptor slConfigDescriptor;
  int streamDependenceFlag;
  int streamPriority;
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (ESDescriptor)paramObject;
      if (this.URLFlag != ((ESDescriptor)paramObject).URLFlag) {
        return false;
      }
      if (this.URLLength != ((ESDescriptor)paramObject).URLLength) {
        return false;
      }
      if (this.dependsOnEsId != ((ESDescriptor)paramObject).dependsOnEsId) {
        return false;
      }
      if (this.esId != ((ESDescriptor)paramObject).esId) {
        return false;
      }
      if (this.oCREsId != ((ESDescriptor)paramObject).oCREsId) {
        return false;
      }
      if (this.oCRstreamFlag != ((ESDescriptor)paramObject).oCRstreamFlag) {
        return false;
      }
      if (this.remoteODFlag != ((ESDescriptor)paramObject).remoteODFlag) {
        return false;
      }
      if (this.streamDependenceFlag != ((ESDescriptor)paramObject).streamDependenceFlag) {
        return false;
      }
      if (this.streamPriority != ((ESDescriptor)paramObject).streamPriority) {
        return false;
      }
      if (this.URLString != null)
      {
        if (this.URLString.equals(((ESDescriptor)paramObject).URLString)) {}
      }
      else {
        while (((ESDescriptor)paramObject).URLString != null) {
          return false;
        }
      }
      if (this.decoderConfigDescriptor != null)
      {
        if (this.decoderConfigDescriptor.equals(((ESDescriptor)paramObject).decoderConfigDescriptor)) {}
      }
      else {
        while (((ESDescriptor)paramObject).decoderConfigDescriptor != null) {
          return false;
        }
      }
      if (this.otherDescriptors != null)
      {
        if (this.otherDescriptors.equals(((ESDescriptor)paramObject).otherDescriptors)) {}
      }
      else {
        while (((ESDescriptor)paramObject).otherDescriptors != null) {
          return false;
        }
      }
      if (this.slConfigDescriptor == null) {
        break;
      }
    } while (this.slConfigDescriptor.equals(((ESDescriptor)paramObject).slConfigDescriptor));
    for (;;)
    {
      return false;
      if (((ESDescriptor)paramObject).slConfigDescriptor == null) {
        break;
      }
    }
  }
  
  public DecoderConfigDescriptor getDecoderConfigDescriptor()
  {
    return this.decoderConfigDescriptor;
  }
  
  public int getDependsOnEsId()
  {
    return this.dependsOnEsId;
  }
  
  public int getEsId()
  {
    return this.esId;
  }
  
  public List<BaseDescriptor> getOtherDescriptors()
  {
    return this.otherDescriptors;
  }
  
  public int getRemoteODFlag()
  {
    return this.remoteODFlag;
  }
  
  public SLConfigDescriptor getSlConfigDescriptor()
  {
    return this.slConfigDescriptor;
  }
  
  public int getStreamDependenceFlag()
  {
    return this.streamDependenceFlag;
  }
  
  public int getStreamPriority()
  {
    return this.streamPriority;
  }
  
  public int getURLFlag()
  {
    return this.URLFlag;
  }
  
  public int getURLLength()
  {
    return this.URLLength;
  }
  
  public String getURLString()
  {
    return this.URLString;
  }
  
  public int getoCREsId()
  {
    return this.oCREsId;
  }
  
  public int getoCRstreamFlag()
  {
    return this.oCRstreamFlag;
  }
  
  public int hashCode()
  {
    int m = 0;
    int n = this.esId;
    int i1 = this.streamDependenceFlag;
    int i2 = this.URLFlag;
    int i3 = this.oCRstreamFlag;
    int i4 = this.streamPriority;
    int i5 = this.URLLength;
    int i;
    int i6;
    int i7;
    int i8;
    int j;
    if (this.URLString != null)
    {
      i = this.URLString.hashCode();
      i6 = this.remoteODFlag;
      i7 = this.dependsOnEsId;
      i8 = this.oCREsId;
      if (this.decoderConfigDescriptor == null) {
        break label197;
      }
      j = this.decoderConfigDescriptor.hashCode();
      label87:
      if (this.slConfigDescriptor == null) {
        break label202;
      }
    }
    label197:
    label202:
    for (int k = this.slConfigDescriptor.hashCode();; k = 0)
    {
      if (this.otherDescriptors != null) {
        m = this.otherDescriptors.hashCode();
      }
      return (((((((((((n * 31 + i1) * 31 + i2) * 31 + i3) * 31 + i4) * 31 + i5) * 31 + i) * 31 + i6) * 31 + i7) * 31 + i8) * 31 + j) * 31 + k) * 31 + m;
      i = 0;
      break;
      j = 0;
      break label87;
    }
  }
  
  public void parseDetail(ByteBuffer paramByteBuffer)
    throws IOException
  {
    this.esId = IsoTypeReader.readUInt16(paramByteBuffer);
    int i = IsoTypeReader.readUInt8(paramByteBuffer);
    this.streamDependenceFlag = (i >>> 7);
    this.URLFlag = (i >>> 6 & 0x1);
    this.oCRstreamFlag = (i >>> 5 & 0x1);
    this.streamPriority = (i & 0x1F);
    if (this.streamDependenceFlag == 1) {
      this.dependsOnEsId = IsoTypeReader.readUInt16(paramByteBuffer);
    }
    if (this.URLFlag == 1)
    {
      this.URLLength = IsoTypeReader.readUInt8(paramByteBuffer);
      this.URLString = IsoTypeReader.readString(paramByteBuffer, this.URLLength);
    }
    if (this.oCRstreamFlag == 1) {
      this.oCREsId = IsoTypeReader.readUInt16(paramByteBuffer);
    }
    int m = getSizeBytes();
    label139:
    int k;
    label150:
    Integer localInteger;
    if (this.streamDependenceFlag == 1)
    {
      i = 2;
      if (this.URLFlag != 1) {
        break label468;
      }
      j = this.URLLength + 1;
      if (this.oCRstreamFlag != 1) {
        break label473;
      }
      k = 2;
      j = m + 1 + 2 + 1 + i + j + k;
      k = paramByteBuffer.position();
      i = j;
      if (getSize() > j + 2)
      {
        localBaseDescriptor = ObjectDescriptorFactory.createFrom(-1, paramByteBuffer);
        l = paramByteBuffer.position() - k;
        localLogger = log;
        localStringBuilder = new StringBuilder().append(localBaseDescriptor).append(" - ESDescriptor1 read: ").append(l).append(", size: ");
        if (localBaseDescriptor == null) {
          break label479;
        }
        localInteger = Integer.valueOf(localBaseDescriptor.getSize());
        label250:
        localLogger.finer(localInteger);
        if (localBaseDescriptor == null) {
          break label485;
        }
        i = localBaseDescriptor.getSize();
        paramByteBuffer.position(k + i);
        j += i;
        label289:
        i = j;
        if ((localBaseDescriptor instanceof DecoderConfigDescriptor))
        {
          this.decoderConfigDescriptor = ((DecoderConfigDescriptor)localBaseDescriptor);
          i = j;
        }
      }
      j = paramByteBuffer.position();
      if (getSize() <= i + 2) {
        break label511;
      }
      localBaseDescriptor = ObjectDescriptorFactory.createFrom(-1, paramByteBuffer);
      l = paramByteBuffer.position() - j;
      localLogger = log;
      localStringBuilder = new StringBuilder().append(localBaseDescriptor).append(" - ESDescriptor2 read: ").append(l).append(", size: ");
      if (localBaseDescriptor == null) {
        break label495;
      }
      localInteger = Integer.valueOf(localBaseDescriptor.getSize());
      label390:
      localLogger.finer(localInteger);
      if (localBaseDescriptor == null) {
        break label501;
      }
      k = localBaseDescriptor.getSize();
      paramByteBuffer.position(j + k);
      j = i + k;
      label431:
      i = j;
      if ((localBaseDescriptor instanceof SLConfigDescriptor))
      {
        this.slConfigDescriptor = ((SLConfigDescriptor)localBaseDescriptor);
        i = j;
      }
    }
    for (;;)
    {
      if (getSize() - i > 2) {
        break label522;
      }
      return;
      i = 0;
      break;
      label468:
      j = 0;
      break label139;
      label473:
      k = 0;
      break label150;
      label479:
      localInteger = null;
      break label250;
      label485:
      j = (int)(j + l);
      break label289;
      label495:
      localInteger = null;
      break label390;
      label501:
      j = (int)(i + l);
      break label431;
      label511:
      log.warning("SLConfigDescriptor is missing!");
    }
    label522:
    int j = paramByteBuffer.position();
    BaseDescriptor localBaseDescriptor = ObjectDescriptorFactory.createFrom(-1, paramByteBuffer);
    long l = paramByteBuffer.position() - j;
    Logger localLogger = log;
    StringBuilder localStringBuilder = new StringBuilder().append(localBaseDescriptor).append(" - ESDescriptor3 read: ").append(l).append(", size: ");
    if (localBaseDescriptor != null)
    {
      localInteger = Integer.valueOf(localBaseDescriptor.getSize());
      label592:
      localLogger.finer(localInteger);
      if (localBaseDescriptor == null) {
        break label654;
      }
      k = localBaseDescriptor.getSize();
      paramByteBuffer.position(j + k);
      i += k;
    }
    for (;;)
    {
      this.otherDescriptors.add(localBaseDescriptor);
      break;
      localInteger = null;
      break label592;
      label654:
      i = (int)(i + l);
    }
  }
  
  public ByteBuffer serialize()
  {
    ByteBuffer localByteBuffer1 = ByteBuffer.allocate(serializedSize());
    IsoTypeWriter.writeUInt8(localByteBuffer1, 3);
    IsoTypeWriter.writeUInt8(localByteBuffer1, serializedSize() - 2);
    IsoTypeWriter.writeUInt16(localByteBuffer1, this.esId);
    IsoTypeWriter.writeUInt8(localByteBuffer1, this.streamDependenceFlag << 7 | this.URLFlag << 6 | this.oCRstreamFlag << 5 | this.streamPriority & 0x1F);
    if (this.streamDependenceFlag > 0) {
      IsoTypeWriter.writeUInt16(localByteBuffer1, this.dependsOnEsId);
    }
    if (this.URLFlag > 0)
    {
      IsoTypeWriter.writeUInt8(localByteBuffer1, this.URLLength);
      IsoTypeWriter.writeUtf8String(localByteBuffer1, this.URLString);
    }
    if (this.oCRstreamFlag > 0) {
      IsoTypeWriter.writeUInt16(localByteBuffer1, this.oCREsId);
    }
    ByteBuffer localByteBuffer2 = this.decoderConfigDescriptor.serialize();
    ByteBuffer localByteBuffer3 = this.slConfigDescriptor.serialize();
    localByteBuffer1.put(localByteBuffer2.array());
    localByteBuffer1.put(localByteBuffer3.array());
    return localByteBuffer1;
  }
  
  public int serializedSize()
  {
    int j = 5;
    if (this.streamDependenceFlag > 0) {
      j = 5 + 2;
    }
    int i = j;
    if (this.URLFlag > 0) {
      i = j + (this.URLLength + 1);
    }
    j = i;
    if (this.oCRstreamFlag > 0) {
      j = i + 2;
    }
    return j + this.decoderConfigDescriptor.serializedSize() + this.slConfigDescriptor.serializedSize();
  }
  
  public void setDecoderConfigDescriptor(DecoderConfigDescriptor paramDecoderConfigDescriptor)
  {
    this.decoderConfigDescriptor = paramDecoderConfigDescriptor;
  }
  
  public void setDependsOnEsId(int paramInt)
  {
    this.dependsOnEsId = paramInt;
  }
  
  public void setEsId(int paramInt)
  {
    this.esId = paramInt;
  }
  
  public void setRemoteODFlag(int paramInt)
  {
    this.remoteODFlag = paramInt;
  }
  
  public void setSlConfigDescriptor(SLConfigDescriptor paramSLConfigDescriptor)
  {
    this.slConfigDescriptor = paramSLConfigDescriptor;
  }
  
  public void setStreamDependenceFlag(int paramInt)
  {
    this.streamDependenceFlag = paramInt;
  }
  
  public void setStreamPriority(int paramInt)
  {
    this.streamPriority = paramInt;
  }
  
  public void setURLFlag(int paramInt)
  {
    this.URLFlag = paramInt;
  }
  
  public void setURLLength(int paramInt)
  {
    this.URLLength = paramInt;
  }
  
  public void setURLString(String paramString)
  {
    this.URLString = paramString;
  }
  
  public void setoCREsId(int paramInt)
  {
    this.oCREsId = paramInt;
  }
  
  public void setoCRstreamFlag(int paramInt)
  {
    this.oCRstreamFlag = paramInt;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("ESDescriptor");
    localStringBuilder.append("{esId=").append(this.esId);
    localStringBuilder.append(", streamDependenceFlag=").append(this.streamDependenceFlag);
    localStringBuilder.append(", URLFlag=").append(this.URLFlag);
    localStringBuilder.append(", oCRstreamFlag=").append(this.oCRstreamFlag);
    localStringBuilder.append(", streamPriority=").append(this.streamPriority);
    localStringBuilder.append(", URLLength=").append(this.URLLength);
    localStringBuilder.append(", URLString='").append(this.URLString).append('\'');
    localStringBuilder.append(", remoteODFlag=").append(this.remoteODFlag);
    localStringBuilder.append(", dependsOnEsId=").append(this.dependsOnEsId);
    localStringBuilder.append(", oCREsId=").append(this.oCREsId);
    localStringBuilder.append(", decoderConfigDescriptor=").append(this.decoderConfigDescriptor);
    localStringBuilder.append(", slConfigDescriptor=").append(this.slConfigDescriptor);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/mp4/objectdescriptors/ESDescriptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */