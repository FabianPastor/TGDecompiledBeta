package com.mp4parser.iso14496.part15;

import com.coremedia.iso.Hex;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.authoring.tracks.CleanInputStream;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitWriterBuffer;
import com.googlecode.mp4parser.h264.model.PictureParameterSet;
import com.googlecode.mp4parser.h264.model.SeqParameterSet;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
    int j = ((BitReaderBuffer)localObject).readBits(5);
    int i = 0;
    long l;
    if (i >= j)
    {
      l = IsoTypeReader.readUInt8(paramByteBuffer);
      i = 0;
      label183:
      if (i < l) {
        break label371;
      }
      if (paramByteBuffer.remaining() < 4) {
        this.hasExts = false;
      }
      if ((!this.hasExts) || ((this.avcProfileIndication != 100) && (this.avcProfileIndication != 110) && (this.avcProfileIndication != 122) && (this.avcProfileIndication != 144))) {
        break label439;
      }
      localObject = new BitReaderBuffer(paramByteBuffer);
      this.chromaFormatPaddingBits = ((BitReaderBuffer)localObject).readBits(6);
      this.chromaFormat = ((BitReaderBuffer)localObject).readBits(2);
      this.bitDepthLumaMinus8PaddingBits = ((BitReaderBuffer)localObject).readBits(5);
      this.bitDepthLumaMinus8 = ((BitReaderBuffer)localObject).readBits(3);
      this.bitDepthChromaMinus8PaddingBits = ((BitReaderBuffer)localObject).readBits(5);
      this.bitDepthChromaMinus8 = ((BitReaderBuffer)localObject).readBits(3);
      l = IsoTypeReader.readUInt8(paramByteBuffer);
      i = 0;
    }
    for (;;)
    {
      if (i >= l)
      {
        return;
        localObject = new byte[IsoTypeReader.readUInt16(paramByteBuffer)];
        paramByteBuffer.get((byte[])localObject);
        this.sequenceParameterSets.add(localObject);
        i += 1;
        break;
        label371:
        localObject = new byte[IsoTypeReader.readUInt16(paramByteBuffer)];
        paramByteBuffer.get((byte[])localObject);
        this.pictureParameterSets.add(localObject);
        i += 1;
        break label183;
      }
      localObject = new byte[IsoTypeReader.readUInt16(paramByteBuffer)];
      paramByteBuffer.get((byte[])localObject);
      this.sequenceParameterSetExts.add(localObject);
      i += 1;
    }
    label439:
    this.chromaFormat = -1;
    this.bitDepthLumaMinus8 = -1;
    this.bitDepthChromaMinus8 = -1;
  }
  
  public void getContent(ByteBuffer paramByteBuffer)
  {
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.configurationVersion);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.avcProfileIndication);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.profileCompatibility);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.avcLevelIndication);
    Object localObject = new BitWriterBuffer(paramByteBuffer);
    ((BitWriterBuffer)localObject).writeBits(this.lengthSizeMinusOnePaddingBits, 6);
    ((BitWriterBuffer)localObject).writeBits(this.lengthSizeMinusOne, 2);
    ((BitWriterBuffer)localObject).writeBits(this.numberOfSequenceParameterSetsPaddingBits, 3);
    ((BitWriterBuffer)localObject).writeBits(this.pictureParameterSets.size(), 5);
    localObject = this.sequenceParameterSets.iterator();
    if (!((Iterator)localObject).hasNext())
    {
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.pictureParameterSets.size());
      localObject = this.pictureParameterSets.iterator();
      label125:
      if (((Iterator)localObject).hasNext()) {
        break label287;
      }
      if ((this.hasExts) && ((this.avcProfileIndication == 100) || (this.avcProfileIndication == 110) || (this.avcProfileIndication == 122) || (this.avcProfileIndication == 144)))
      {
        localObject = new BitWriterBuffer(paramByteBuffer);
        ((BitWriterBuffer)localObject).writeBits(this.chromaFormatPaddingBits, 6);
        ((BitWriterBuffer)localObject).writeBits(this.chromaFormat, 2);
        ((BitWriterBuffer)localObject).writeBits(this.bitDepthLumaMinus8PaddingBits, 5);
        ((BitWriterBuffer)localObject).writeBits(this.bitDepthLumaMinus8, 3);
        ((BitWriterBuffer)localObject).writeBits(this.bitDepthChromaMinus8PaddingBits, 5);
        ((BitWriterBuffer)localObject).writeBits(this.bitDepthChromaMinus8, 3);
        localObject = this.sequenceParameterSetExts.iterator();
      }
    }
    for (;;)
    {
      if (!((Iterator)localObject).hasNext())
      {
        return;
        arrayOfByte = (byte[])((Iterator)localObject).next();
        IsoTypeWriter.writeUInt16(paramByteBuffer, arrayOfByte.length);
        paramByteBuffer.put(arrayOfByte);
        break;
        label287:
        arrayOfByte = (byte[])((Iterator)localObject).next();
        IsoTypeWriter.writeUInt16(paramByteBuffer, arrayOfByte.length);
        paramByteBuffer.put(arrayOfByte);
        break label125;
      }
      byte[] arrayOfByte = (byte[])((Iterator)localObject).next();
      IsoTypeWriter.writeUInt16(paramByteBuffer, arrayOfByte.length);
      paramByteBuffer.put(arrayOfByte);
    }
  }
  
  public long getContentSize()
  {
    long l1 = 5L + 1L;
    Iterator localIterator = this.sequenceParameterSets.iterator();
    label42:
    long l2;
    if (!localIterator.hasNext())
    {
      l1 += 1L;
      localIterator = this.pictureParameterSets.iterator();
      if (localIterator.hasNext()) {
        break label153;
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
        label153:
        l1 = l1 + 2L + ((byte[])localIterator.next()).length;
        break label42;
      }
      l1 = l1 + 2L + ((byte[])localIterator.next()).length;
    }
  }
  
  public String[] getPPS()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.pictureParameterSets.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return (String[])localArrayList.toArray(new String[localArrayList.size()]);
      }
      Object localObject = (byte[])localIterator.next();
      try
      {
        localObject = PictureParameterSet.read(new ByteArrayInputStream((byte[])localObject, 1, localObject.length - 1)).toString();
        localArrayList.add(localObject);
      }
      catch (IOException localIOException)
      {
        throw new RuntimeException(localIOException);
      }
    }
  }
  
  public List<String> getPictureParameterSetsAsStrings()
  {
    ArrayList localArrayList = new ArrayList(this.pictureParameterSets.size());
    Iterator localIterator = this.pictureParameterSets.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return localArrayList;
      }
      localArrayList.add(Hex.encodeHex((byte[])localIterator.next()));
    }
  }
  
  public String[] getSPS()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.sequenceParameterSets.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return (String[])localArrayList.toArray(new String[localArrayList.size()]);
      }
      Object localObject2 = (byte[])localIterator.next();
      Object localObject1 = "not parsable";
      try
      {
        localObject2 = SeqParameterSet.read(new CleanInputStream(new ByteArrayInputStream((byte[])localObject2, 1, localObject2.length - 1))).toString();
        localObject1 = localObject2;
      }
      catch (IOException localIOException)
      {
        for (;;) {}
      }
      localArrayList.add(localObject1);
    }
  }
  
  public List<String> getSequenceParameterSetExtsAsStrings()
  {
    ArrayList localArrayList = new ArrayList(this.sequenceParameterSetExts.size());
    Iterator localIterator = this.sequenceParameterSetExts.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return localArrayList;
      }
      localArrayList.add(Hex.encodeHex((byte[])localIterator.next()));
    }
  }
  
  public List<String> getSequenceParameterSetsAsStrings()
  {
    ArrayList localArrayList = new ArrayList(this.sequenceParameterSets.size());
    Iterator localIterator = this.sequenceParameterSets.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return localArrayList;
      }
      localArrayList.add(Hex.encodeHex((byte[])localIterator.next()));
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