package com.googlecode.mp4parser.boxes.dece;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class BaseLocationBox
  extends AbstractFullBox
{
  public static final String TYPE = "bloc";
  String baseLocation = "";
  String purchaseLocation = "";
  
  static {}
  
  public BaseLocationBox()
  {
    super("bloc");
  }
  
  public BaseLocationBox(String paramString1, String paramString2)
  {
    super("bloc");
    this.baseLocation = paramString1;
    this.purchaseLocation = paramString2;
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.baseLocation = IsoTypeReader.readString(paramByteBuffer);
    paramByteBuffer.get(new byte[256 - Utf8.utf8StringLengthInBytes(this.baseLocation) - 1]);
    this.purchaseLocation = IsoTypeReader.readString(paramByteBuffer);
    paramByteBuffer.get(new byte[256 - Utf8.utf8StringLengthInBytes(this.purchaseLocation) - 1]);
    paramByteBuffer.get(new byte['Ȁ']);
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
      paramObject = (BaseLocationBox)paramObject;
      if (this.baseLocation != null)
      {
        if (this.baseLocation.equals(((BaseLocationBox)paramObject).baseLocation)) {}
      }
      else {
        while (((BaseLocationBox)paramObject).baseLocation != null) {
          return false;
        }
      }
      if (this.purchaseLocation == null) {
        break;
      }
    } while (this.purchaseLocation.equals(((BaseLocationBox)paramObject).purchaseLocation));
    for (;;)
    {
      return false;
      if (((BaseLocationBox)paramObject).purchaseLocation == null) {
        break;
      }
    }
  }
  
  public String getBaseLocation()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.baseLocation;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    paramByteBuffer.put(Utf8.convert(this.baseLocation));
    paramByteBuffer.put(new byte[256 - Utf8.utf8StringLengthInBytes(this.baseLocation)]);
    paramByteBuffer.put(Utf8.convert(this.purchaseLocation));
    paramByteBuffer.put(new byte[256 - Utf8.utf8StringLengthInBytes(this.purchaseLocation)]);
    paramByteBuffer.put(new byte['Ȁ']);
  }
  
  protected long getContentSize()
  {
    return 1028L;
  }
  
  public String getPurchaseLocation()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.purchaseLocation;
  }
  
  public int hashCode()
  {
    int j = 0;
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (this.baseLocation != null) {}
    for (int i = this.baseLocation.hashCode();; i = 0)
    {
      if (this.purchaseLocation != null) {
        j = this.purchaseLocation.hashCode();
      }
      return i * 31 + j;
    }
  }
  
  public void setBaseLocation(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.baseLocation = paramString;
  }
  
  public void setPurchaseLocation(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.purchaseLocation = paramString;
  }
  
  public String toString()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return "BaseLocationBox{baseLocation='" + this.baseLocation + '\'' + ", purchaseLocation='" + this.purchaseLocation + '\'' + '}';
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/dece/BaseLocationBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */