package net.hockeyapp.android.metrics.model;

import java.io.IOException;
import java.io.Writer;
import net.hockeyapp.android.metrics.JsonHelper;

public class Extension
  implements IJsonSerializable
{
  private String ver = "1.0";
  
  public Extension()
  {
    InitializeFields();
  }
  
  protected void InitializeFields() {}
  
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
    String str = "";
    if (this.ver != null)
    {
      paramWriter.write("" + "\"ver\":");
      paramWriter.write(JsonHelper.convert(this.ver));
      str = ",";
    }
    return str;
  }
  
  public void setVer(String paramString)
  {
    this.ver = paramString;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/metrics/model/Extension.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */