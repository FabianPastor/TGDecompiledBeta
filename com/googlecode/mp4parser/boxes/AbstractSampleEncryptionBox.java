package com.googlecode.mp4parser.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.annotations.DoNotParseDetail;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public abstract class AbstractSampleEncryptionBox
  extends AbstractFullBox
{
  protected int algorithmId = -1;
  List<CencSampleAuxiliaryDataFormat> entries = Collections.emptyList();
  protected int ivSize = -1;
  protected byte[] kid = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
  
  static {}
  
  protected AbstractSampleEncryptionBox(String paramString)
  {
    super(paramString);
  }
  
  private List<CencSampleAuxiliaryDataFormat> parseEntries(ByteBuffer paramByteBuffer, long paramLong, int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    for (;;)
    {
      if (paramLong <= 0L) {
        return localArrayList;
      }
      try
      {
        CencSampleAuxiliaryDataFormat localCencSampleAuxiliaryDataFormat = new CencSampleAuxiliaryDataFormat();
        localCencSampleAuxiliaryDataFormat.iv = new byte[paramInt];
        paramByteBuffer.get(localCencSampleAuxiliaryDataFormat.iv);
        int i;
        if ((getFlags() & 0x2) > 0)
        {
          localCencSampleAuxiliaryDataFormat.pairs = new CencSampleAuxiliaryDataFormat.Pair[IsoTypeReader.readUInt16(paramByteBuffer)];
          i = 0;
        }
        for (;;)
        {
          if (i >= localCencSampleAuxiliaryDataFormat.pairs.length)
          {
            localArrayList.add(localCencSampleAuxiliaryDataFormat);
            paramLong -= 1L;
            break;
          }
          localCencSampleAuxiliaryDataFormat.pairs[i] = localCencSampleAuxiliaryDataFormat.createPair(IsoTypeReader.readUInt16(paramByteBuffer), IsoTypeReader.readUInt32(paramByteBuffer));
          i += 1;
        }
        return null;
      }
      catch (BufferUnderflowException paramByteBuffer) {}
    }
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    if ((getFlags() & 0x1) > 0)
    {
      this.algorithmId = IsoTypeReader.readUInt24(paramByteBuffer);
      this.ivSize = IsoTypeReader.readUInt8(paramByteBuffer);
      this.kid = new byte[16];
      paramByteBuffer.get(this.kid);
    }
    long l = IsoTypeReader.readUInt32(paramByteBuffer);
    ByteBuffer localByteBuffer1 = paramByteBuffer.duplicate();
    ByteBuffer localByteBuffer2 = paramByteBuffer.duplicate();
    this.entries = parseEntries(localByteBuffer1, l, 8);
    if (this.entries == null)
    {
      this.entries = parseEntries(localByteBuffer2, l, 16);
      paramByteBuffer.position(paramByteBuffer.position() + paramByteBuffer.remaining() - localByteBuffer2.remaining());
    }
    while (this.entries == null)
    {
      throw new RuntimeException("Cannot parse SampleEncryptionBox");
      paramByteBuffer.position(paramByteBuffer.position() + paramByteBuffer.remaining() - localByteBuffer1.remaining());
    }
  }
  
  public boolean equals(Object paramObject)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, paramObject);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (this == paramObject) {}
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (AbstractSampleEncryptionBox)paramObject;
      if (this.algorithmId != ((AbstractSampleEncryptionBox)paramObject).algorithmId) {
        return false;
      }
      if (this.ivSize != ((AbstractSampleEncryptionBox)paramObject).ivSize) {
        return false;
      }
      if (this.entries != null)
      {
        if (this.entries.equals(((AbstractSampleEncryptionBox)paramObject).entries)) {}
      }
      else {
        while (((AbstractSampleEncryptionBox)paramObject).entries != null) {
          return false;
        }
      }
    } while (Arrays.equals(this.kid, ((AbstractSampleEncryptionBox)paramObject).kid));
    return false;
  }
  
  public void getBox(WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    super.getBox(paramWritableByteChannel);
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    if (isOverrideTrackEncryptionBoxParameters())
    {
      IsoTypeWriter.writeUInt24(paramByteBuffer, this.algorithmId);
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.ivSize);
      paramByteBuffer.put(this.kid);
    }
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.entries.size());
    Iterator localIterator = this.entries.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      Object localObject1 = (CencSampleAuxiliaryDataFormat)localIterator.next();
      if (((CencSampleAuxiliaryDataFormat)localObject1).getSize() > 0)
      {
        if ((((CencSampleAuxiliaryDataFormat)localObject1).iv.length != 8) && (((CencSampleAuxiliaryDataFormat)localObject1).iv.length != 16)) {
          throw new RuntimeException("IV must be either 8 or 16 bytes");
        }
        paramByteBuffer.put(((CencSampleAuxiliaryDataFormat)localObject1).iv);
        if (isSubSampleEncryption())
        {
          IsoTypeWriter.writeUInt16(paramByteBuffer, ((CencSampleAuxiliaryDataFormat)localObject1).pairs.length);
          localObject1 = ((CencSampleAuxiliaryDataFormat)localObject1).pairs;
          int j = localObject1.length;
          int i = 0;
          while (i < j)
          {
            Object localObject2 = localObject1[i];
            IsoTypeWriter.writeUInt16(paramByteBuffer, ((CencSampleAuxiliaryDataFormat.Pair)localObject2).clear());
            IsoTypeWriter.writeUInt32(paramByteBuffer, ((CencSampleAuxiliaryDataFormat.Pair)localObject2).encrypted());
            i += 1;
          }
        }
      }
    }
  }
  
  protected long getContentSize()
  {
    long l = 4L;
    if (isOverrideTrackEncryptionBoxParameters()) {
      l = 4L + 4L + this.kid.length;
    }
    l += 4L;
    Iterator localIterator = this.entries.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return l;
      }
      l += ((CencSampleAuxiliaryDataFormat)localIterator.next()).getSize();
    }
  }
  
  public List<CencSampleAuxiliaryDataFormat> getEntries()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.entries;
  }
  
  public List<Short> getEntrySizes()
  {
    Object localObject = Factory.makeJP(ajc$tjp_5, this, this);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    localObject = new ArrayList(this.entries.size());
    Iterator localIterator = this.entries.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return (List<Short>)localObject;
      }
      CencSampleAuxiliaryDataFormat localCencSampleAuxiliaryDataFormat = (CencSampleAuxiliaryDataFormat)localIterator.next();
      short s2 = (short)localCencSampleAuxiliaryDataFormat.iv.length;
      short s1 = s2;
      if (isSubSampleEncryption())
      {
        int i = (short)(s2 + 2);
        s1 = (short)(localCencSampleAuxiliaryDataFormat.pairs.length * 6 + i);
      }
      ((List)localObject).add(Short.valueOf(s1));
    }
  }
  
  public int getOffsetToFirstIV()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    int i;
    if (getSize() > 4294967296L)
    {
      i = 16;
      if (!isOverrideTrackEncryptionBoxParameters()) {
        break label57;
      }
    }
    label57:
    for (int j = this.kid.length + 4;; j = 0)
    {
      return i + j + 4;
      i = 8;
      break;
    }
  }
  
  public int hashCode()
  {
    int j = 0;
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    int k = this.algorithmId;
    int m = this.ivSize;
    if (this.kid != null) {}
    for (int i = Arrays.hashCode(this.kid);; i = 0)
    {
      if (this.entries != null) {
        j = this.entries.hashCode();
      }
      return ((k * 31 + m) * 31 + i) * 31 + j;
    }
  }
  
  @DoNotParseDetail
  protected boolean isOverrideTrackEncryptionBoxParameters()
  {
    return (getFlags() & 0x1) > 0;
  }
  
  @DoNotParseDetail
  public boolean isSubSampleEncryption()
  {
    return (getFlags() & 0x2) > 0;
  }
  
  public void setEntries(List<CencSampleAuxiliaryDataFormat> paramList)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this, paramList);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.entries = paramList;
  }
  
  @DoNotParseDetail
  public void setSubSampleEncryption(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      setFlags(getFlags() | 0x2);
      return;
    }
    setFlags(getFlags() & 0xFFFFFD);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/AbstractSampleEncryptionBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */