package com.googlecode.mp4parser.boxes.dece;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.annotations.DoNotParseDetail;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class AssetInformationBox
  extends AbstractFullBox
{
  public static final String TYPE = "ainf";
  String apid = "";
  String profileVersion = "0000";
  
  static
  {
    
    if (!AssetInformationBox.class.desiredAssertionStatus()) {}
    for (boolean bool = true;; bool = false)
    {
      $assertionsDisabled = bool;
      return;
    }
  }
  
  public AssetInformationBox()
  {
    super("ainf");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.profileVersion = IsoTypeReader.readString(paramByteBuffer, 4);
    this.apid = IsoTypeReader.readString(paramByteBuffer);
  }
  
  public String getApid()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.apid;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    if (getVersion() == 0)
    {
      paramByteBuffer.put(Utf8.convert(this.profileVersion), 0, 4);
      paramByteBuffer.put(Utf8.convert(this.apid));
      paramByteBuffer.put((byte)0);
      return;
    }
    throw new RuntimeException("Unknown ainf version " + getVersion());
  }
  
  protected long getContentSize()
  {
    return Utf8.utf8StringLengthInBytes(this.apid) + 9;
  }
  
  public String getProfileVersion()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.profileVersion;
  }
  
  @DoNotParseDetail
  public boolean isHidden()
  {
    return (getFlags() & 0x1) == 1;
  }
  
  public void setApid(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.apid = paramString;
  }
  
  @DoNotParseDetail
  public void setHidden(boolean paramBoolean)
  {
    int i = getFlags();
    if ((isHidden() ^ paramBoolean))
    {
      if (paramBoolean) {
        setFlags(i | 0x1);
      }
    }
    else {
      return;
    }
    setFlags(0xFFFFFE & i);
  }
  
  public void setProfileVersion(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    assert ((paramString != null) && (paramString.length() == 4));
    this.profileVersion = paramString;
  }
  
  public static class Entry
  {
    public String assetId;
    public String namespace;
    public String profileLevelIdc;
    
    public Entry(String paramString1, String paramString2, String paramString3)
    {
      this.namespace = paramString1;
      this.profileLevelIdc = paramString2;
      this.assetId = paramString3;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {}
      do
      {
        return true;
        if ((paramObject == null) || (getClass() != paramObject.getClass())) {
          return false;
        }
        paramObject = (Entry)paramObject;
        if (!this.assetId.equals(((Entry)paramObject).assetId)) {
          return false;
        }
        if (!this.namespace.equals(((Entry)paramObject).namespace)) {
          return false;
        }
      } while (this.profileLevelIdc.equals(((Entry)paramObject).profileLevelIdc));
      return false;
    }
    
    public int getSize()
    {
      return Utf8.utf8StringLengthInBytes(this.namespace) + 3 + Utf8.utf8StringLengthInBytes(this.profileLevelIdc) + Utf8.utf8StringLengthInBytes(this.assetId);
    }
    
    public int hashCode()
    {
      return (this.namespace.hashCode() * 31 + this.profileLevelIdc.hashCode()) * 31 + this.assetId.hashCode();
    }
    
    public String toString()
    {
      return "{namespace='" + this.namespace + '\'' + ", profileLevelIdc='" + this.profileLevelIdc + '\'' + ", assetId='" + this.assetId + '\'' + '}';
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/dece/AssetInformationBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */