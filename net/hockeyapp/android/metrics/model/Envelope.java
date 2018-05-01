package net.hockeyapp.android.metrics.model;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import net.hockeyapp.android.metrics.JsonHelper;

public class Envelope
  implements IJsonSerializable
{
  private String appId;
  private String appVer;
  private String cV;
  private Base data;
  private String epoch;
  private Map<String, Extension> ext;
  private long flags;
  private String iKey;
  private String name;
  private String os;
  private String osVer;
  private int sampleRate = 100;
  private long seqNum;
  private Map<String, String> tags;
  private String time;
  private int ver = 1;
  
  public Envelope()
  {
    InitializeFields();
  }
  
  protected void InitializeFields() {}
  
  public String getAppId()
  {
    return this.appId;
  }
  
  public String getAppVer()
  {
    return this.appVer;
  }
  
  public String getCV()
  {
    return this.cV;
  }
  
  public Base getData()
  {
    return this.data;
  }
  
  public String getEpoch()
  {
    return this.epoch;
  }
  
  public Map<String, Extension> getExt()
  {
    if (this.ext == null) {
      this.ext = new LinkedHashMap();
    }
    return this.ext;
  }
  
  public long getFlags()
  {
    return this.flags;
  }
  
  public String getIKey()
  {
    return this.iKey;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String getOs()
  {
    return this.os;
  }
  
  public String getOsVer()
  {
    return this.osVer;
  }
  
  public int getSampleRate()
  {
    return this.sampleRate;
  }
  
  public long getSeqNum()
  {
    return this.seqNum;
  }
  
  public Map<String, String> getTags()
  {
    if (this.tags == null) {
      this.tags = new LinkedHashMap();
    }
    return this.tags;
  }
  
  public String getTime()
  {
    return this.time;
  }
  
  public int getVer()
  {
    return this.ver;
  }
  
  public void serialize(Writer paramWriter)
    throws IOException
  {
    if (paramWriter == null) {
      throw new IllegalArgumentException("writer");
    }
    paramWriter.write(123);
    serializeContent(paramWriter);
    paramWriter.write(125);
  }
  
  protected String serializeContent(Writer paramWriter)
    throws IOException
  {
    paramWriter.write("" + "\"ver\":");
    paramWriter.write(JsonHelper.convert(Integer.valueOf(this.ver)));
    paramWriter.write("," + "\"name\":");
    paramWriter.write(JsonHelper.convert(this.name));
    paramWriter.write("," + "\"time\":");
    paramWriter.write(JsonHelper.convert(this.time));
    if (this.sampleRate > 0.0D)
    {
      paramWriter.write("," + "\"sampleRate\":");
      paramWriter.write(JsonHelper.convert(Integer.valueOf(this.sampleRate)));
    }
    if (this.epoch != null)
    {
      paramWriter.write("," + "\"epoch\":");
      paramWriter.write(JsonHelper.convert(this.epoch));
    }
    if (this.seqNum != 0L)
    {
      paramWriter.write("," + "\"seqNum\":");
      paramWriter.write(JsonHelper.convert(Long.valueOf(this.seqNum)));
    }
    if (this.iKey != null)
    {
      paramWriter.write("," + "\"iKey\":");
      paramWriter.write(JsonHelper.convert(this.iKey));
    }
    if (this.flags != 0L)
    {
      paramWriter.write("," + "\"flags\":");
      paramWriter.write(JsonHelper.convert(Long.valueOf(this.flags)));
    }
    if (this.os != null)
    {
      paramWriter.write("," + "\"os\":");
      paramWriter.write(JsonHelper.convert(this.os));
    }
    if (this.osVer != null)
    {
      paramWriter.write("," + "\"osVer\":");
      paramWriter.write(JsonHelper.convert(this.osVer));
    }
    if (this.appId != null)
    {
      paramWriter.write("," + "\"appId\":");
      paramWriter.write(JsonHelper.convert(this.appId));
    }
    if (this.appVer != null)
    {
      paramWriter.write("," + "\"appVer\":");
      paramWriter.write(JsonHelper.convert(this.appVer));
    }
    if (this.cV != null)
    {
      paramWriter.write("," + "\"cV\":");
      paramWriter.write(JsonHelper.convert(this.cV));
    }
    if (this.tags != null)
    {
      paramWriter.write("," + "\"tags\":");
      JsonHelper.writeDictionary(paramWriter, this.tags);
    }
    if (this.ext != null)
    {
      paramWriter.write("," + "\"ext\":");
      JsonHelper.writeDictionary(paramWriter, this.ext);
    }
    if (this.data != null)
    {
      paramWriter.write("," + "\"data\":");
      JsonHelper.writeJsonSerializable(paramWriter, this.data);
    }
    return ",";
  }
  
  public void setAppId(String paramString)
  {
    this.appId = paramString;
  }
  
  public void setAppVer(String paramString)
  {
    this.appVer = paramString;
  }
  
  public void setCV(String paramString)
  {
    this.cV = paramString;
  }
  
  public void setData(Base paramBase)
  {
    this.data = paramBase;
  }
  
  public void setEpoch(String paramString)
  {
    this.epoch = paramString;
  }
  
  public void setExt(Map<String, Extension> paramMap)
  {
    this.ext = paramMap;
  }
  
  public void setFlags(long paramLong)
  {
    this.flags = paramLong;
  }
  
  public void setIKey(String paramString)
  {
    this.iKey = paramString;
  }
  
  public void setName(String paramString)
  {
    this.name = paramString;
  }
  
  public void setOs(String paramString)
  {
    this.os = paramString;
  }
  
  public void setOsVer(String paramString)
  {
    this.osVer = paramString;
  }
  
  public void setSampleRate(int paramInt)
  {
    this.sampleRate = paramInt;
  }
  
  public void setSeqNum(long paramLong)
  {
    this.seqNum = paramLong;
  }
  
  public void setTags(Map<String, String> paramMap)
  {
    this.tags = paramMap;
  }
  
  public void setTime(String paramString)
  {
    this.time = paramString;
  }
  
  public void setVer(int paramInt)
  {
    this.ver = paramInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/metrics/model/Envelope.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */