package com.googlecode.mp4parser.boxes.microsoft;

import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class XtraBox
  extends AbstractBox
{
  private static final long FILETIME_EPOCH_DIFF = 11644473600000L;
  private static final long FILETIME_ONE_MILLISECOND = 10000L;
  public static final int MP4_XTRA_BT_FILETIME = 21;
  public static final int MP4_XTRA_BT_GUID = 72;
  public static final int MP4_XTRA_BT_INT64 = 19;
  public static final int MP4_XTRA_BT_UNICODE = 8;
  public static final String TYPE = "Xtra";
  ByteBuffer data;
  private boolean successfulParse = false;
  Vector<XtraTag> tags = new Vector();
  
  static {}
  
  public XtraBox()
  {
    super("Xtra");
  }
  
  public XtraBox(String paramString)
  {
    super(paramString);
  }
  
  private int detailSize()
  {
    int j = 0;
    int i = 0;
    for (;;)
    {
      if (i >= this.tags.size()) {
        return j;
      }
      j += ((XtraTag)this.tags.elementAt(i)).getContentSize();
      i += 1;
    }
  }
  
  private static long filetimeToMillis(long paramLong)
  {
    return paramLong / 10000L - 11644473600000L;
  }
  
  private XtraTag getTagByName(String paramString)
  {
    Iterator localIterator = this.tags.iterator();
    XtraTag localXtraTag;
    do
    {
      if (!localIterator.hasNext()) {
        return null;
      }
      localXtraTag = (XtraTag)localIterator.next();
    } while (!localXtraTag.tagName.equals(paramString));
    return localXtraTag;
  }
  
  private static long millisToFiletime(long paramLong)
  {
    return (11644473600000L + paramLong) * 10000L;
  }
  
  private static String readAsciiString(ByteBuffer paramByteBuffer, int paramInt)
  {
    byte[] arrayOfByte = new byte[paramInt];
    paramByteBuffer.get(arrayOfByte);
    try
    {
      paramByteBuffer = new String(arrayOfByte, "US-ASCII");
      return paramByteBuffer;
    }
    catch (UnsupportedEncodingException paramByteBuffer)
    {
      throw new RuntimeException("Shouldn't happen", paramByteBuffer);
    }
  }
  
  private static String readUtf16String(ByteBuffer paramByteBuffer, int paramInt)
  {
    char[] arrayOfChar = new char[paramInt / 2 - 1];
    int i = 0;
    for (;;)
    {
      if (i >= paramInt / 2 - 1)
      {
        paramByteBuffer.getChar();
        return new String(arrayOfChar);
      }
      arrayOfChar[i] = paramByteBuffer.getChar();
      i += 1;
    }
  }
  
  private static void writeAsciiString(ByteBuffer paramByteBuffer, String paramString)
  {
    try
    {
      paramByteBuffer.put(paramString.getBytes("US-ASCII"));
      return;
    }
    catch (UnsupportedEncodingException paramByteBuffer)
    {
      throw new RuntimeException("Shouldn't happen", paramByteBuffer);
    }
  }
  
  private static void writeUtf16String(ByteBuffer paramByteBuffer, String paramString)
  {
    paramString = paramString.toCharArray();
    int i = 0;
    for (;;)
    {
      if (i >= paramString.length)
      {
        paramByteBuffer.putChar('\000');
        return;
      }
      paramByteBuffer.putChar(paramString[i]);
      i += 1;
    }
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    int i = paramByteBuffer.remaining();
    this.data = paramByteBuffer.slice();
    this.successfulParse = false;
    try
    {
      this.tags.clear();
      if (paramByteBuffer.remaining() <= 0)
      {
        int j = detailSize();
        if (i == j) {
          break label186;
        }
        throw new RuntimeException("Improperly handled Xtra tag: Calculated sizes don't match ( " + i + "/" + j + ")");
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        this.successfulParse = false;
        System.err.println("Malformed Xtra Tag detected: " + localException.toString());
        localException.printStackTrace();
        paramByteBuffer.position(paramByteBuffer.position() + paramByteBuffer.remaining());
        return;
        XtraTag localXtraTag = new XtraTag(null);
        localXtraTag.parse(paramByteBuffer);
        this.tags.addElement(localXtraTag);
      }
    }
    finally
    {
      paramByteBuffer.order(ByteOrder.BIG_ENDIAN);
    }
    label186:
    this.successfulParse = true;
    paramByteBuffer.order(ByteOrder.BIG_ENDIAN);
  }
  
  public String[] getAllTagNames()
  {
    Object localObject = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    localObject = new String[this.tags.size()];
    int i = 0;
    for (;;)
    {
      if (i >= this.tags.size()) {
        return (String[])localObject;
      }
      localObject[i] = ((XtraTag)this.tags.elementAt(i)).tagName;
      i += 1;
    }
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    if (this.successfulParse)
    {
      int i = 0;
      for (;;)
      {
        if (i >= this.tags.size()) {
          return;
        }
        ((XtraTag)this.tags.elementAt(i)).getContent(paramByteBuffer);
        i += 1;
      }
    }
    this.data.rewind();
    paramByteBuffer.put(this.data);
  }
  
  protected long getContentSize()
  {
    if (this.successfulParse) {
      return detailSize();
    }
    return this.data.limit();
  }
  
  public Date getFirstDateValue(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    paramString = getValues(paramString);
    int j = paramString.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return null;
      }
      localJoinPoint = paramString[i];
      if ((localJoinPoint instanceof Date)) {
        return (Date)localJoinPoint;
      }
      i += 1;
    }
  }
  
  public Long getFirstLongValue(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    paramString = getValues(paramString);
    int j = paramString.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return null;
      }
      localJoinPoint = paramString[i];
      if ((localJoinPoint instanceof Long)) {
        return (Long)localJoinPoint;
      }
      i += 1;
    }
  }
  
  public String getFirstStringValue(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    paramString = getValues(paramString);
    int j = paramString.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return null;
      }
      localJoinPoint = paramString[i];
      if ((localJoinPoint instanceof String)) {
        return (String)localJoinPoint;
      }
      i += 1;
    }
  }
  
  public Object[] getValues(String paramString)
  {
    Object localObject = Factory.makeJP(ajc$tjp_5, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    paramString = getTagByName(paramString);
    if (paramString != null)
    {
      localObject = new Object[paramString.values.size()];
      int i = 0;
      for (;;)
      {
        if (i >= paramString.values.size()) {
          return (Object[])localObject;
        }
        localObject[i] = ((XtraValue)paramString.values.elementAt(i)).getValueAsObject();
        i += 1;
      }
    }
    return new Object[0];
  }
  
  public void removeTag(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    paramString = getTagByName(paramString);
    if (paramString != null) {
      this.tags.remove(paramString);
    }
  }
  
  public void setTagValue(String paramString, long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_10, this, this, paramString, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    removeTag(paramString);
    paramString = new XtraTag(paramString, null);
    paramString.values.addElement(new XtraValue(paramLong, null));
    this.tags.addElement(paramString);
  }
  
  public void setTagValue(String paramString1, String paramString2)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_8, this, this, paramString1, paramString2);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    setTagValues(paramString1, new String[] { paramString2 });
  }
  
  public void setTagValue(String paramString, Date paramDate)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_9, this, this, paramString, paramDate);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    removeTag(paramString);
    paramString = new XtraTag(paramString, null);
    paramString.values.addElement(new XtraValue(paramDate, null));
    this.tags.addElement(paramString);
  }
  
  public void setTagValues(String paramString, String[] paramArrayOfString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this, paramString, paramArrayOfString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    removeTag(paramString);
    paramString = new XtraTag(paramString, null);
    int i = 0;
    for (;;)
    {
      if (i >= paramArrayOfString.length)
      {
        this.tags.addElement(paramString);
        return;
      }
      paramString.values.addElement(new XtraValue(paramArrayOfString[i], null));
      i += 1;
    }
  }
  
  public String toString()
  {
    Object localObject = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    if (!isParsed()) {
      parseDetails();
    }
    localObject = new StringBuffer();
    ((StringBuffer)localObject).append("XtraBox[");
    Iterator localIterator1 = this.tags.iterator();
    for (;;)
    {
      if (!localIterator1.hasNext())
      {
        ((StringBuffer)localObject).append("]");
        return ((StringBuffer)localObject).toString();
      }
      XtraTag localXtraTag = (XtraTag)localIterator1.next();
      Iterator localIterator2 = localXtraTag.values.iterator();
      while (localIterator2.hasNext())
      {
        XtraValue localXtraValue = (XtraValue)localIterator2.next();
        ((StringBuffer)localObject).append(localXtraTag.tagName);
        ((StringBuffer)localObject).append("=");
        ((StringBuffer)localObject).append(localXtraValue.toString());
        ((StringBuffer)localObject).append(";");
      }
    }
  }
  
  private static class XtraTag
  {
    private int inputSize;
    private String tagName;
    private Vector<XtraBox.XtraValue> values = new Vector();
    
    private XtraTag() {}
    
    private XtraTag(String paramString)
    {
      this();
      this.tagName = paramString;
    }
    
    private void getContent(ByteBuffer paramByteBuffer)
    {
      paramByteBuffer.putInt(getContentSize());
      paramByteBuffer.putInt(this.tagName.length());
      XtraBox.writeAsciiString(paramByteBuffer, this.tagName);
      paramByteBuffer.putInt(this.values.size());
      int i = 0;
      for (;;)
      {
        if (i >= this.values.size()) {
          return;
        }
        XtraBox.XtraValue.access$2((XtraBox.XtraValue)this.values.elementAt(i), paramByteBuffer);
        i += 1;
      }
    }
    
    private int getContentSize()
    {
      int j = this.tagName.length() + 12;
      int i = 0;
      for (;;)
      {
        if (i >= this.values.size()) {
          return j;
        }
        j += XtraBox.XtraValue.access$3((XtraBox.XtraValue)this.values.elementAt(i));
        i += 1;
      }
    }
    
    private void parse(ByteBuffer paramByteBuffer)
    {
      this.inputSize = paramByteBuffer.getInt();
      this.tagName = XtraBox.readAsciiString(paramByteBuffer, paramByteBuffer.getInt());
      int j = paramByteBuffer.getInt();
      int i = 0;
      for (;;)
      {
        if (i >= j)
        {
          if (this.inputSize == getContentSize()) {
            break;
          }
          throw new RuntimeException("Improperly handled Xtra tag: Sizes don't match ( " + this.inputSize + "/" + getContentSize() + ") on " + this.tagName);
        }
        XtraBox.XtraValue localXtraValue = new XtraBox.XtraValue(null);
        XtraBox.XtraValue.access$1(localXtraValue, paramByteBuffer);
        this.values.addElement(localXtraValue);
        i += 1;
      }
    }
    
    public String toString()
    {
      StringBuffer localStringBuffer = new StringBuffer();
      localStringBuffer.append(this.tagName);
      localStringBuffer.append(" [");
      localStringBuffer.append(this.inputSize);
      localStringBuffer.append("/");
      localStringBuffer.append(this.values.size());
      localStringBuffer.append("]:\n");
      int i = 0;
      for (;;)
      {
        if (i >= this.values.size()) {
          return localStringBuffer.toString();
        }
        localStringBuffer.append("  ");
        localStringBuffer.append(((XtraBox.XtraValue)this.values.elementAt(i)).toString());
        localStringBuffer.append("\n");
        i += 1;
      }
    }
  }
  
  private static class XtraValue
  {
    public Date fileTimeValue;
    public long longValue;
    public byte[] nonParsedValue;
    public String stringValue;
    public int type;
    
    private XtraValue() {}
    
    private XtraValue(long paramLong)
    {
      this.type = 19;
      this.longValue = paramLong;
    }
    
    private XtraValue(String paramString)
    {
      this.type = 8;
      this.stringValue = paramString;
    }
    
    private XtraValue(Date paramDate)
    {
      this.type = 21;
      this.fileTimeValue = paramDate;
    }
    
    private void getContent(ByteBuffer paramByteBuffer)
    {
      for (;;)
      {
        try
        {
          paramByteBuffer.putInt(getContentSize());
          paramByteBuffer.putShort((short)this.type);
          paramByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
          switch (this.type)
          {
          case 8: 
            paramByteBuffer.put(this.nonParsedValue);
            return;
          }
        }
        finally
        {
          paramByteBuffer.order(ByteOrder.BIG_ENDIAN);
        }
        XtraBox.writeUtf16String(paramByteBuffer, this.stringValue);
        continue;
        paramByteBuffer.putLong(this.longValue);
        continue;
        paramByteBuffer.putLong(XtraBox.millisToFiletime(this.fileTimeValue.getTime()));
      }
    }
    
    private int getContentSize()
    {
      switch (this.type)
      {
      default: 
        return 6 + this.nonParsedValue.length;
      case 8: 
        return 6 + (this.stringValue.length() * 2 + 2);
      }
      return 6 + 8;
    }
    
    private Object getValueAsObject()
    {
      switch (this.type)
      {
      default: 
        return this.nonParsedValue;
      case 8: 
        return this.stringValue;
      case 19: 
        return new Long(this.longValue);
      }
      return this.fileTimeValue;
    }
    
    private void parse(ByteBuffer paramByteBuffer)
    {
      int i = paramByteBuffer.getInt() - 6;
      this.type = paramByteBuffer.getShort();
      paramByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
      switch (this.type)
      {
      default: 
        this.nonParsedValue = new byte[i];
        paramByteBuffer.get(this.nonParsedValue);
      }
      for (;;)
      {
        paramByteBuffer.order(ByteOrder.BIG_ENDIAN);
        return;
        this.stringValue = XtraBox.readUtf16String(paramByteBuffer, i);
        continue;
        this.longValue = paramByteBuffer.getLong();
        continue;
        this.fileTimeValue = new Date(XtraBox.filetimeToMillis(paramByteBuffer.getLong()));
      }
    }
    
    public String toString()
    {
      switch (this.type)
      {
      default: 
        return "[GUID](nonParsed)";
      case 8: 
        return "[string]" + this.stringValue;
      case 19: 
        return "[long]" + String.valueOf(this.longValue);
      }
      return "[filetime]" + this.fileTimeValue.toString();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/microsoft/XtraBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */