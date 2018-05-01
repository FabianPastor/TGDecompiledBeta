package com.mp4parser.iso14496.part15;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitWriterBuffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AvcDecoderConfigurationRecord
{
  public int avcLevelIndication;
  public int avcProfileIndication;
  public int bitDepthChromaMinus8 = 0;
  public int bitDepthChromaMinus8PaddingBits = 31;
  public int bitDepthLumaMinus8 = 0;
  public int bitDepthLumaMinus8PaddingBits = 31;
  public int chromaFormat = 1;
  public int chromaFormatPaddingBits = 31;
  public int configurationVersion;
  public boolean hasExts = true;
  public int lengthSizeMinusOne;
  public int lengthSizeMinusOnePaddingBits = 63;
  public int numberOfSequenceParameterSetsPaddingBits = 7;
  public List<byte[]> pictureParameterSets = new ArrayList();
  public int profileCompatibility;
  public List<byte[]> sequenceParameterSetExts = new ArrayList();
  public List<byte[]> sequenceParameterSets = new ArrayList();
  
  public AvcDecoderConfigurationRecord() {}
  
  public AvcDecoderConfigurationRecord(ByteBuffer paramByteBuffer)
  {
    this.configurationVersion = IsoTypeReader.readUInt8(paramByteBuffer);
    this.avcProfileIndication = IsoTypeReader.readUInt8(paramByteBuffer);
    this.profileCompatibility = IsoTypeReader.readUInt8(paramByteBuffer);
    this.avcLevelIndication = IsoTypeReader.readUInt8(paramByteBuffer);
    Object localObject = new BitReaderBuffer(paramByteBuffer);
    this.lengthSizeMinusOnePaddingBits = ((BitReaderBuffer)localObject).readBits(6);
    this.lengthSizeMinusOne = ((BitReaderBuffer)localObject).readBits(2);
    this.numberOfSequenceParameterSetsPaddingBits = ((BitReaderBuffer)localObject).readBits(3);
    int i = ((BitReaderBuffer)localObject).readBits(5);
    int j = 0;
    if (j >= i)
    {
      long l = IsoTypeReader.readUInt8(paramByteBuffer);
      j = 0;
      label181:
      if (j < l) {
        break label361;
      }
      if (paramByteBuffer.remaining() < 4) {
        this.hasExts = false;
      }
      if ((!this.hasExts) || ((this.avcProfileIndication != 100) && (this.avcProfileIndication != 110) && (this.avcProfileIndication != 122) && (this.avcProfileIndication != 144))) {
        break label421;
      }
      localObject = new BitReaderBuffer(paramByteBuffer);
      this.chromaFormatPaddingBits = ((BitReaderBuffer)localObject).readBits(6);
      this.chromaFormat = ((BitReaderBuffer)localObject).readBits(2);
      this.bitDepthLumaMinus8PaddingBits = ((BitReaderBuffer)localObject).readBits(5);
      this.bitDepthLumaMinus8 = ((BitReaderBuffer)localObject).readBits(3);
      this.bitDepthChromaMinus8PaddingBits = ((BitReaderBuffer)localObject).readBits(5);
      this.bitDepthChromaMinus8 = ((BitReaderBuffer)localObject).readBits(3);
      l = IsoTypeReader.readUInt8(paramByteBuffer);
      j = 0;
      label321:
      if (j < l) {
        break label391;
      }
    }
    for (;;)
    {
      return;
      localObject = new byte[IsoTypeReader.readUInt16(paramByteBuffer)];
      paramByteBuffer.get((byte[])localObject);
      this.sequenceParameterSets.add(localObject);
      j++;
      break;
      label361:
      localObject = new byte[IsoTypeReader.readUInt16(paramByteBuffer)];
      paramByteBuffer.get((byte[])localObject);
      this.pictureParameterSets.add(localObject);
      j++;
      break label181;
      label391:
      localObject = new byte[IsoTypeReader.readUInt16(paramByteBuffer)];
      paramByteBuffer.get((byte[])localObject);
      this.sequenceParameterSetExts.add(localObject);
      j++;
      break label321;
      label421:
      this.chromaFormat = -1;
      this.bitDepthLumaMinus8 = -1;
      this.bitDepthChromaMinus8 = -1;
    }
  }
  
  public void getContent(ByteBuffer paramByteBuffer)
  {
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.configurationVersion);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.avcProfileIndication);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.profileCompatibility);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.avcLevelIndication);
    Object localObject1 = new BitWriterBuffer(paramByteBuffer);
    ((BitWriterBuffer)localObject1).writeBits(this.lengthSizeMinusOnePaddingBits, 6);
    ((BitWriterBuffer)localObject1).writeBits(this.lengthSizeMinusOne, 2);
    ((BitWriterBuffer)localObject1).writeBits(this.numberOfSequenceParameterSetsPaddingBits, 3);
    ((BitWriterBuffer)localObject1).writeBits(this.pictureParameterSets.size(), 5);
    Object localObject2 = this.sequenceParameterSets.iterator();
    if (!((Iterator)localObject2).hasNext())
    {
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.pictureParameterSets.size());
      localObject1 = this.pictureParameterSets.iterator();
      label125:
      if (((Iterator)localObject1).hasNext()) {
        break label287;
      }
      if ((this.hasExts) && ((this.avcProfileIndication == 100) || (this.avcProfileIndication == 110) || (this.avcProfileIndication == 122) || (this.avcProfileIndication == 144)))
      {
        localObject1 = new BitWriterBuffer(paramByteBuffer);
        ((BitWriterBuffer)localObject1).writeBits(this.chromaFormatPaddingBits, 6);
        ((BitWriterBuffer)localObject1).writeBits(this.chromaFormat, 2);
        ((BitWriterBuffer)localObject1).writeBits(this.bitDepthLumaMinus8PaddingBits, 5);
        ((BitWriterBuffer)localObject1).writeBits(this.bitDepthLumaMinus8, 3);
        ((BitWriterBuffer)localObject1).writeBits(this.bitDepthChromaMinus8PaddingBits, 5);
        ((BitWriterBuffer)localObject1).writeBits(this.bitDepthChromaMinus8, 3);
        localObject2 = this.sequenceParameterSetExts.iterator();
      }
    }
    for (;;)
    {
      if (!((Iterator)localObject2).hasNext())
      {
        return;
        localObject1 = (byte[])((Iterator)localObject2).next();
        IsoTypeWriter.writeUInt16(paramByteBuffer, localObject1.length);
        paramByteBuffer.put((byte[])localObject1);
        break;
        label287:
        localObject2 = (byte[])((Iterator)localObject1).next();
        IsoTypeWriter.writeUInt16(paramByteBuffer, localObject2.length);
        paramByteBuffer.put((byte[])localObject2);
        break label125;
      }
      localObject1 = (byte[])((Iterator)localObject2).next();
      IsoTypeWriter.writeUInt16(paramByteBuffer, localObject1.length);
      paramByteBuffer.put((byte[])localObject1);
    }
  }
  
  public long getContentSize()
  {
    long l1 = 5L + 1L;
    Iterator localIterator = this.sequenceParameterSets.iterator();
    label39:
    long l2;
    if (!localIterator.hasNext())
    {
      l1 += 1L;
      localIterator = this.pictureParameterSets.iterator();
      if (localIterator.hasNext()) {
        break label150;
      }
      l2 = l1;
      if (this.hasExts) {
        if ((this.avcProfileIndication != 100) && (this.avcProfileIndication != 110) && (this.avcProfileIndication != 122))
        {
          l2 = l1;
          if (this.avcProfileIndication != 144) {}
        }
        else
        {
          l1 += 4L;
          localIterator = this.sequenceParameterSetExts.iterator();
        }
      }
    }
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        l2 = l1;
        return l2;
        l1 = l1 + 2L + ((byte[])localIterator.next()).length;
        break;
        label150:
        l1 = l1 + 2L + ((byte[])localIterator.next()).length;
        break label39;
      }
      l1 = l1 + 2L + ((byte[])localIterator.next()).length;
    }
  }
  
  public String toString()
  {
    return "AvcDecoderConfigurationRecord{configurationVersion=" + this.configurationVersion + ", avcProfileIndication=" + this.avcProfileIndication + ", profileCompatibility=" + this.profileCompatibility + ", avcLevelIndication=" + this.avcLevelIndication + ", lengthSizeMinusOne=" + this.lengthSizeMinusOne + ", hasExts=" + this.hasExts + ", chromaFormat=" + this.chromaFormat + ", bitDepthLumaMinus8=" + this.bitDepthLumaMinus8 + ", bitDepthChromaMinus8=" + this.bitDepthChromaMinus8 + ", lengthSizeMinusOnePaddingBits=" + this.lengthSizeMinusOnePaddingBits + ", numberOfSequenceParameterSetsPaddingBits=" + this.numberOfSequenceParameterSetsPaddingBits + ", chromaFormatPaddingBits=" + this.chromaFormatPaddingBits + ", bitDepthLumaMinus8PaddingBits=" + this.bitDepthLumaMinus8PaddingBits + ", bitDepthChromaMinus8PaddingBits=" + this.bitDepthChromaMinus8PaddingBits + '}';
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/mp4parser/iso14496/part15/AvcDecoderConfigurationRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */