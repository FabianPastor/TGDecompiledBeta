package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.Hex;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Descriptor(tags={4})
public class DecoderConfigDescriptor
  extends BaseDescriptor
{
  private static Logger log = Logger.getLogger(DecoderConfigDescriptor.class.getName());
  AudioSpecificConfig audioSpecificInfo;
  long avgBitRate;
  int bufferSizeDB;
  byte[] configDescriptorDeadBytes;
  DecoderSpecificInfo decoderSpecificInfo;
  long maxBitRate;
  int objectTypeIndication;
  List<ProfileLevelIndicationDescriptor> profileLevelIndicationDescriptors = new ArrayList();
  int streamType;
  int upStream;
  
  public void parseDetail(ByteBuffer paramByteBuffer)
    throws IOException
  {
    this.objectTypeIndication = IsoTypeReader.readUInt8(paramByteBuffer);
    int i = IsoTypeReader.readUInt8(paramByteBuffer);
    this.streamType = (i >>> 2);
    this.upStream = (i >> 1 & 0x1);
    this.bufferSizeDB = IsoTypeReader.readUInt24(paramByteBuffer);
    this.maxBitRate = IsoTypeReader.readUInt32(paramByteBuffer);
    this.avgBitRate = IsoTypeReader.readUInt32(paramByteBuffer);
    if (paramByteBuffer.remaining() > 2)
    {
      i = paramByteBuffer.position();
      localBaseDescriptor = ObjectDescriptorFactory.createFrom(this.objectTypeIndication, paramByteBuffer);
      i = paramByteBuffer.position() - i;
      localLogger = log;
      localStringBuilder = new StringBuilder().append(localBaseDescriptor).append(" - DecoderConfigDescr1 read: ").append(i).append(", size: ");
      if (localBaseDescriptor == null) {
        break label216;
      }
    }
    label216:
    for (Integer localInteger = Integer.valueOf(localBaseDescriptor.getSize());; localInteger = null)
    {
      localLogger.finer(localInteger);
      if (localBaseDescriptor != null)
      {
        int j = localBaseDescriptor.getSize();
        if (i < j)
        {
          this.configDescriptorDeadBytes = new byte[j - i];
          paramByteBuffer.get(this.configDescriptorDeadBytes);
        }
      }
      if ((localBaseDescriptor instanceof DecoderSpecificInfo)) {
        this.decoderSpecificInfo = ((DecoderSpecificInfo)localBaseDescriptor);
      }
      if ((localBaseDescriptor instanceof AudioSpecificConfig)) {
        this.audioSpecificInfo = ((AudioSpecificConfig)localBaseDescriptor);
      }
      if (paramByteBuffer.remaining() > 2) {
        break;
      }
      return;
    }
    long l1 = paramByteBuffer.position();
    BaseDescriptor localBaseDescriptor = ObjectDescriptorFactory.createFrom(this.objectTypeIndication, paramByteBuffer);
    long l2 = paramByteBuffer.position();
    Logger localLogger = log;
    StringBuilder localStringBuilder = new StringBuilder().append(localBaseDescriptor).append(" - DecoderConfigDescr2 read: ").append(l2 - l1).append(", size: ");
    if (localBaseDescriptor != null) {}
    for (localInteger = Integer.valueOf(localBaseDescriptor.getSize());; localInteger = null)
    {
      localLogger.finer(localInteger);
      if (!(localBaseDescriptor instanceof ProfileLevelIndicationDescriptor)) {
        break;
      }
      this.profileLevelIndicationDescriptors.add((ProfileLevelIndicationDescriptor)localBaseDescriptor);
      break;
    }
  }
  
  public ByteBuffer serialize()
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(serializedSize());
    IsoTypeWriter.writeUInt8(localByteBuffer, 4);
    IsoTypeWriter.writeUInt8(localByteBuffer, serializedSize() - 2);
    IsoTypeWriter.writeUInt8(localByteBuffer, this.objectTypeIndication);
    IsoTypeWriter.writeUInt8(localByteBuffer, this.streamType << 2 | this.upStream << 1 | 0x1);
    IsoTypeWriter.writeUInt24(localByteBuffer, this.bufferSizeDB);
    IsoTypeWriter.writeUInt32(localByteBuffer, this.maxBitRate);
    IsoTypeWriter.writeUInt32(localByteBuffer, this.avgBitRate);
    if (this.audioSpecificInfo != null) {
      localByteBuffer.put(this.audioSpecificInfo.serialize().array());
    }
    return localByteBuffer;
  }
  
  public int serializedSize()
  {
    if (this.audioSpecificInfo == null) {}
    for (int i = 0;; i = this.audioSpecificInfo.serializedSize()) {
      return i + 15;
    }
  }
  
  public void setAudioSpecificInfo(AudioSpecificConfig paramAudioSpecificConfig)
  {
    this.audioSpecificInfo = paramAudioSpecificConfig;
  }
  
  public void setAvgBitRate(long paramLong)
  {
    this.avgBitRate = paramLong;
  }
  
  public void setBufferSizeDB(int paramInt)
  {
    this.bufferSizeDB = paramInt;
  }
  
  public void setMaxBitRate(long paramLong)
  {
    this.maxBitRate = paramLong;
  }
  
  public void setObjectTypeIndication(int paramInt)
  {
    this.objectTypeIndication = paramInt;
  }
  
  public void setStreamType(int paramInt)
  {
    this.streamType = paramInt;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder1 = new StringBuilder();
    localStringBuilder1.append("DecoderConfigDescriptor");
    localStringBuilder1.append("{objectTypeIndication=").append(this.objectTypeIndication);
    localStringBuilder1.append(", streamType=").append(this.streamType);
    localStringBuilder1.append(", upStream=").append(this.upStream);
    localStringBuilder1.append(", bufferSizeDB=").append(this.bufferSizeDB);
    localStringBuilder1.append(", maxBitRate=").append(this.maxBitRate);
    localStringBuilder1.append(", avgBitRate=").append(this.avgBitRate);
    localStringBuilder1.append(", decoderSpecificInfo=").append(this.decoderSpecificInfo);
    localStringBuilder1.append(", audioSpecificInfo=").append(this.audioSpecificInfo);
    StringBuilder localStringBuilder2 = localStringBuilder1.append(", configDescriptorDeadBytes=");
    if (this.configDescriptorDeadBytes != null)
    {
      localObject = this.configDescriptorDeadBytes;
      localStringBuilder2.append(Hex.encodeHex((byte[])localObject));
      localStringBuilder2 = localStringBuilder1.append(", profileLevelIndicationDescriptors=");
      if (this.profileLevelIndicationDescriptors != null) {
        break label197;
      }
    }
    label197:
    for (Object localObject = "null";; localObject = Arrays.asList(new List[] { this.profileLevelIndicationDescriptors }).toString())
    {
      localStringBuilder2.append((String)localObject);
      localStringBuilder1.append('}');
      return localStringBuilder1.toString();
      localObject = new byte[0];
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/mp4/objectdescriptors/DecoderConfigDescriptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */