package net.hockeyapp.android.metrics.model;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Map;
import net.hockeyapp.android.metrics.JsonHelper;

public class Session
  implements IJsonSerializable, Serializable
{
  private String id;
  private String isFirst;
  private String isNew;
  
  public Session()
  {
    InitializeFields();
  }
  
  protected void InitializeFields() {}
  
  public void addToHashMap(Map<String, String> paramMap)
  {
    if (this.id != null) {
      paramMap.put("ai.session.id", this.id);
    }
    if (this.isFirst != null) {
      paramMap.put("ai.session.isFirst", this.isFirst);
    }
    if (this.isNew != null) {
      paramMap.put("ai.session.isNew", this.isNew);
    }
  }
  
  public String getId()
  {
    return this.id;
  }
  
  public String getIsFirst()
  {
    return this.isFirst;
  }
  
  public String getIsNew()
  {
    return this.isNew;
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
    if (this.id != null)
    {
      paramWriter.write("" + "\"ai.session.id\":");
      paramWriter.write(JsonHelper.convert(this.id));
      localObject2 = ",";
    }
    Object localObject1 = localObject2;
    if (this.isFirst != null)
    {
      paramWriter.write((String)localObject2 + "\"ai.session.isFirst\":");
      paramWriter.write(JsonHelper.convert(this.isFirst));
      localObject1 = ",";
    }
    localObject2 = localObject1;
    if (this.isNew != null)
    {
      paramWriter.write((String)localObject1 + "\"ai.session.isNew\":");
      paramWriter.write(JsonHelper.convert(this.isNew));
      localObject2 = ",";
    }
    return (String)localObject2;
  }
  
  public void setId(String paramString)
  {
    this.id = paramString;
  }
  
  public void setIsFirst(String paramString)
  {
    this.isFirst = paramString;
  }
  
  public void setIsNew(String paramString)
  {
    this.isNew = paramString;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/metrics/model/Session.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */