package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.CastUtils;
import com.mp4parser.iso14496.part15.StepwiseTemporalLayerEntry;
import com.mp4parser.iso14496.part15.SyncSampleEntry;
import com.mp4parser.iso14496.part15.TemporalLayerSampleGroup;
import com.mp4parser.iso14496.part15.TemporalSubLayerSampleGroup;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class SampleGroupDescriptionBox
  extends AbstractFullBox
{
  public static final String TYPE = "sgpd";
  private int defaultLength;
  private List<GroupEntry> groupEntries = new LinkedList();
  
  static {}
  
  public SampleGroupDescriptionBox()
  {
    super("sgpd");
    setVersion(1);
  }
  
  private GroupEntry parseGroupEntry(ByteBuffer paramByteBuffer, String paramString)
  {
    if ("roll".equals(paramString)) {
      paramString = new RollRecoveryEntry();
    }
    for (;;)
    {
      paramString.parse(paramByteBuffer);
      return paramString;
      if ("rash".equals(paramString)) {
        paramString = new RateShareEntry();
      } else if ("seig".equals(paramString)) {
        paramString = new CencSampleEncryptionInformationGroupEntry();
      } else if ("rap ".equals(paramString)) {
        paramString = new VisualRandomAccessEntry();
      } else if ("tele".equals(paramString)) {
        paramString = new TemporalLevelEntry();
      } else if ("sync".equals(paramString)) {
        paramString = new SyncSampleEntry();
      } else if ("tscl".equals(paramString)) {
        paramString = new TemporalLayerSampleGroup();
      } else if ("tsas".equals(paramString)) {
        paramString = new TemporalSubLayerSampleGroup();
      } else if ("stsa".equals(paramString)) {
        paramString = new StepwiseTemporalLayerEntry();
      } else {
        paramString = new UnknownEntry(paramString);
      }
    }
  }
  
  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    if (getVersion() != 1) {
      throw new RuntimeException("SampleGroupDescriptionBox are only supported in version 1");
    }
    String str = IsoTypeReader.read4cc(paramByteBuffer);
    if (getVersion() == 1) {
      this.defaultLength = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
    }
    for (long l = IsoTypeReader.readUInt32(paramByteBuffer);; l -= 1L)
    {
      if (l <= 0L) {
        return;
      }
      int i = this.defaultLength;
      if (getVersion() != 1) {
        break;
      }
      if (this.defaultLength == 0) {
        i = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
      }
      int j = paramByteBuffer.position();
      ByteBuffer localByteBuffer = paramByteBuffer.slice();
      localByteBuffer.limit(i);
      this.groupEntries.add(parseGroupEntry(localByteBuffer, str));
      paramByteBuffer.position(j + i);
    }
    throw new RuntimeException("This should be implemented");
  }
  
  public boolean equals(Object paramObject)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this, paramObject);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (this == paramObject) {}
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (SampleGroupDescriptionBox)paramObject;
      if (this.defaultLength != ((SampleGroupDescriptionBox)paramObject).defaultLength) {
        return false;
      }
      if (this.groupEntries == null) {
        break;
      }
    } while (this.groupEntries.equals(((SampleGroupDescriptionBox)paramObject).groupEntries));
    for (;;)
    {
      return false;
      if (((SampleGroupDescriptionBox)paramObject).groupEntries == null) {
        break;
      }
    }
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    paramByteBuffer.put(IsoFile.fourCCtoBytes(((GroupEntry)this.groupEntries.get(0)).getType()));
    if (getVersion() == 1) {
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.defaultLength);
    }
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.groupEntries.size());
    Iterator localIterator = this.groupEntries.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      GroupEntry localGroupEntry = (GroupEntry)localIterator.next();
      if ((getVersion() == 1) && (this.defaultLength == 0)) {
        IsoTypeWriter.writeUInt32(paramByteBuffer, localGroupEntry.get().limit());
      }
      paramByteBuffer.put(localGroupEntry.get());
    }
  }
  
  protected long getContentSize()
  {
    long l1 = 8L;
    if (getVersion() == 1) {
      l1 = 8L + 4L;
    }
    l1 += 4L;
    Iterator localIterator = this.groupEntries.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return l1;
      }
      GroupEntry localGroupEntry = (GroupEntry)localIterator.next();
      long l2 = l1;
      if (getVersion() == 1)
      {
        l2 = l1;
        if (this.defaultLength == 0) {
          l2 = l1 + 4L;
        }
      }
      l1 = l2 + localGroupEntry.size();
    }
  }
  
  public int getDefaultLength()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.defaultLength;
  }
  
  public List<GroupEntry> getGroupEntries()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.groupEntries;
  }
  
  public int hashCode()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    int j = this.defaultLength;
    if (this.groupEntries != null) {}
    for (int i = this.groupEntries.hashCode();; i = 0) {
      return (j + 0) * 31 + i;
    }
  }
  
  public void setDefaultLength(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.defaultLength = paramInt;
  }
  
  public void setGroupEntries(List<GroupEntry> paramList)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, paramList);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.groupEntries = paramList;
  }
  
  public String toString()
  {
    Object localObject = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    StringBuilder localStringBuilder = new StringBuilder("SampleGroupDescriptionBox{groupingType='");
    if (this.groupEntries.size() > 0) {}
    for (localObject = ((GroupEntry)this.groupEntries.get(0)).getType();; localObject = "????") {
      return (String)localObject + '\'' + ", defaultLength=" + this.defaultLength + ", groupEntries=" + this.groupEntries + '}';
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/mp4/samplegrouping/SampleGroupDescriptionBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */