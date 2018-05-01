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
    boolean bool = true;
    if (this == paramObject) {}
    do
    {
      for (;;)
      {
        return bool;
        if ((paramObject == null) || (getClass() != paramObject.getClass()))
        {
          bool = false;
        }
        else
        {
          paramObject = (ESDescriptor)paramObject;
          if (this.URLFlag != ((ESDescriptor)paramObject).URLFlag)
          {
            bool = false;
          }
          else if (this.URLLength != ((ESDescriptor)paramObject).URLLength)
          {
            bool = false;
          }
          else if (this.dependsOnEsId != ((ESDescriptor)paramObject).dependsOnEsId)
          {
            bool = false;
          }
          else if (this.esId != ((ESDescriptor)paramObject).esId)
          {
            bool = false;
          }
          else if (this.oCREsId != ((ESDescriptor)paramObject).oCREsId)
          {
            bool = false;
          }
          else if (this.oCRstreamFlag != ((ESDescriptor)paramObject).oCRstreamFlag)
          {
            bool = false;
          }
          else if (this.remoteODFlag != ((ESDescriptor)paramObject).remoteODFlag)
          {
            bool = false;
          }
          else if (this.streamDependenceFlag != ((ESDescriptor)paramObject).streamDependenceFlag)
          {
            bool = false;
          }
          else
          {
            if (this.streamPriority == ((ESDescriptor)paramObject).streamPriority) {
              break;
            }
            bool = false;
          }
        }
      }
      if (this.URLString != null)
      {
        if (this.URLString.equals(((ESDescriptor)paramObject).URLString)) {}
      }
      else {
        while (((ESDescriptor)paramObject).URLString != null)
        {
          bool = false;
          break;
        }
      }
      if (this.decoderConfigDescriptor != null)
      {
        if (this.decoderConfigDescriptor.equals(((ESDescriptor)paramObject).decoderConfigDescriptor)) {}
      }
      else {
        while (((ESDescriptor)paramObject).decoderConfigDescriptor != null)
        {
          bool = false;
          break;
        }
      }
      if (this.otherDescriptors != null)
      {
        if (this.otherDescriptors.equals(((ESDescriptor)paramObject).otherDescriptors)) {}
      }
      else {
        while (((ESDescriptor)paramObject).otherDescriptors != null)
        {
          bool = false;
          break;
        }
      }
      if (this.slConfigDescriptor == null) {
        break;
      }
    } while (this.slConfigDescriptor.equals(((ESDescriptor)paramObject).slConfigDescriptor));
    for (;;)
    {
      bool = false;
      break;
      if (((ESDescriptor)paramObject).slConfigDescriptor == null) {
        break;
      }
    }
  }
  
  public int hashCode()
  {
    int i = 0;
    int j = this.esId;
    int k = this.streamDependenceFlag;
    int m = this.URLFlag;
    int n = this.oCRstreamFlag;
    int i1 = this.streamPriority;
    int i2 = this.URLLength;
    int i3;
    int i4;
    int i5;
    int i6;
    int i7;
    if (this.URLString != null)
    {
      i3 = this.URLString.hashCode();
      i4 = this.remoteODFlag;
      i5 = this.dependsOnEsId;
      i6 = this.oCREsId;
      if (this.decoderConfigDescriptor == null) {
        break label197;
      }
      i7 = this.decoderConfigDescriptor.hashCode();
      label86:
      if (this.slConfigDescriptor == null) {
        break label203;
      }
    }
    label197:
    label203:
    for (int i8 = this.slConfigDescriptor.hashCode();; i8 = 0)
    {
      if (this.otherDescriptors != null) {
        i = this.otherDescriptors.hashCode();
      }
      return (((((((((((j * 31 + k) * 31 + m) * 31 + n) * 31 + i1) * 31 + i2) * 31 + i3) * 31 + i4) * 31 + i5) * 31 + i6) * 31 + i7) * 31 + i8) * 31 + i;
      i3 = 0;
      break;
      i7 = 0;
      break label86;
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
    int j = getSizeBytes();
    label139:
    int m;
    label150:
    Integer localInteger;
    if (this.streamDependenceFlag == 1)
    {
      i = 2;
      if (this.URLFlag != 1) {
        break label481;
      }
      k = this.URLLength + 1;
      if (this.oCRstreamFlag != 1) {
        break label487;
      }
      m = 2;
      k = j + 1 + 2 + 1 + i + k + m;
      m = paramByteBuffer.position();
      i = k;
      if (getSize() > k + 2)
      {
        localObject1 = ObjectDescriptorFactory.createFrom(-1, paramByteBuffer);
        l = paramByteBuffer.position() - m;
        localObject2 = log;
        localObject3 = new StringBuilder().append(localObject1).append(" - ESDescriptor1 read: ").append(l).append(", size: ");
        if (localObject1 == null) {
          break label493;
        }
        localInteger = Integer.valueOf(((BaseDescriptor)localObject1).getSize());
        label253:
        ((Logger)localObject2).finer(localInteger);
        if (localObject1 == null) {
          break label499;
        }
        i = ((BaseDescriptor)localObject1).getSize();
        paramByteBuffer.position(m + i);
        k += i;
        label294:
        i = k;
        if ((localObject1 instanceof DecoderConfigDescriptor))
        {
          this.decoderConfigDescriptor = ((DecoderConfigDescriptor)localObject1);
          i = k;
        }
      }
      m = paramByteBuffer.position();
      if (getSize() <= i + 2) {
        break label528;
      }
      localObject2 = ObjectDescriptorFactory.createFrom(-1, paramByteBuffer);
      l = paramByteBuffer.position() - m;
      localObject1 = log;
      localObject3 = new StringBuilder().append(localObject2).append(" - ESDescriptor2 read: ").append(l).append(", size: ");
      if (localObject2 == null) {
        break label511;
      }
      localInteger = Integer.valueOf(((BaseDescriptor)localObject2).getSize());
      label399:
      ((Logger)localObject1).finer(localInteger);
      if (localObject2 == null) {
        break label517;
      }
      k = ((BaseDescriptor)localObject2).getSize();
      paramByteBuffer.position(m + k);
      k = i + k;
      label442:
      i = k;
      if ((localObject2 instanceof SLConfigDescriptor))
      {
        this.slConfigDescriptor = ((SLConfigDescriptor)localObject2);
        i = k;
      }
    }
    for (;;)
    {
      if (getSize() - i > 2) {
        break label539;
      }
      return;
      i = 0;
      break;
      label481:
      k = 0;
      break label139;
      label487:
      m = 0;
      break label150;
      label493:
      localInteger = null;
      break label253;
      label499:
      k = (int)(k + l);
      break label294;
      label511:
      localInteger = null;
      break label399;
      label517:
      k = (int)(i + l);
      break label442;
      label528:
      log.warning("SLConfigDescriptor is missing!");
    }
    label539:
    int k = paramByteBuffer.position();
    Object localObject2 = ObjectDescriptorFactory.createFrom(-1, paramByteBuffer);
    long l = paramByteBuffer.position() - k;
    Object localObject3 = log;
    Object localObject1 = new StringBuilder().append(localObject2).append(" - ESDescriptor3 read: ").append(l).append(", size: ");
    if (localObject2 != null)
    {
      localInteger = Integer.valueOf(((BaseDescriptor)localObject2).getSize());
      label611:
      ((Logger)localObject3).finer(localInteger);
      if (localObject2 == null) {
        break label674;
      }
      m = ((BaseDescriptor)localObject2).getSize();
      paramByteBuffer.position(k + m);
      i += m;
    }
    for (;;)
    {
      this.otherDescriptors.add(localObject2);
      break;
      localInteger = null;
      break label611;
      label674:
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
    int i = 5;
    if (this.streamDependenceFlag > 0) {
      i = 5 + 2;
    }
    int j = i;
    if (this.URLFlag > 0) {
      j = i + (this.URLLength + 1);
    }
    i = j;
    if (this.oCRstreamFlag > 0) {
      i = j + 2;
    }
    return i + this.decoderConfigDescriptor.serializedSize() + this.slConfigDescriptor.serializedSize();
  }
  
  public void setDecoderConfigDescriptor(DecoderConfigDescriptor paramDecoderConfigDescriptor)
  {
    this.decoderConfigDescriptor = paramDecoderConfigDescriptor;
  }
  
  public void setEsId(int paramInt)
  {
    this.esId = paramInt;
  }
  
  public void setSlConfigDescriptor(SLConfigDescriptor paramSLConfigDescriptor)
  {
    this.slConfigDescriptor = paramSLConfigDescriptor;
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