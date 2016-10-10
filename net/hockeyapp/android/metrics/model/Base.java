package net.hockeyapp.android.metrics.model;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import net.hockeyapp.android.metrics.JsonHelper;

public class Base
  implements IJsonSerializable
{
  public LinkedHashMap<String, String> Attributes;
  public String QualifiedName;
  private String baseType;
  
  public Base()
  {
    InitializeFields();
    this.Attributes = new LinkedHashMap();
  }
  
  protected void InitializeFields() {}
  
  public String getBaseType()
  {
    return this.baseType;
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
    String str = "";
    if (this.baseType != null)
    {
      paramWriter.write("" + "\"baseType\":");
      paramWriter.write(JsonHelper.convert(this.baseType));
      str = ",";
    }
    return str;
  }
  
  public void setBaseType(String paramString)
  {
    this.baseType = paramString;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/metrics/model/Base.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */