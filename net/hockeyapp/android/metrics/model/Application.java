package net.hockeyapp.android.metrics.model;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Map;
import net.hockeyapp.android.metrics.JsonHelper;

public class Application
  implements IJsonSerializable, Serializable
{
  private String build;
  private String typeId;
  private String ver;
  
  public Application()
  {
    InitializeFields();
  }
  
  protected void InitializeFields() {}
  
  public void addToHashMap(Map<String, String> paramMap)
  {
    if (this.ver != null) {
      paramMap.put("ai.application.ver", this.ver);
    }
    if (this.build != null) {
      paramMap.put("ai.application.build", this.build);
    }
    if (this.typeId != null) {
      paramMap.put("ai.application.typeId", this.typeId);
    }
  }
  
  public String getBuild()
  {
    return this.build;
  }
  
  public String getTypeId()
  {
    return this.typeId;
  }
  
  public String getVer()
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
    Object localObject2 = "";
    if (this.ver != null)
    {
      paramWriter.write("" + "\"ai.application.ver\":");
      paramWriter.write(JsonHelper.convert(this.ver));
      localObject2 = ",";
    }
    Object localObject1 = localObject2;
    if (this.build != null)
    {
      paramWriter.write((String)localObject2 + "\"ai.application.build\":");
      paramWriter.write(JsonHelper.convert(this.build));
      localObject1 = ",";
    }
    localObject2 = localObject1;
    if (this.typeId != null)
    {
      paramWriter.write((String)localObject1 + "\"ai.application.typeId\":");
      paramWriter.write(JsonHelper.convert(this.typeId));
      localObject2 = ",";
    }
    return (String)localObject2;
  }
  
  public void setBuild(String paramString)
  {
    this.build = paramString;
  }
  
  public void setTypeId(String paramString)
  {
    this.typeId = paramString;
  }
  
  public void setVer(String paramString)
  {
    this.ver = paramString;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/metrics/model/Application.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */