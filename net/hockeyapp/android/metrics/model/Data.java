package net.hockeyapp.android.metrics.model;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import net.hockeyapp.android.metrics.JsonHelper;

public class Data<TDomain extends Domain>
  extends Base
  implements ITelemetryData
{
  private TDomain baseData;
  
  public Data()
  {
    InitializeFields();
    SetupAttributes();
  }
  
  protected void InitializeFields()
  {
    this.QualifiedName = "com.microsoft.telemetry.Data";
  }
  
  public void SetupAttributes()
  {
    this.Attributes.put("Description", "Data struct to contain both B and C sections.");
  }
  
  public TDomain getBaseData()
  {
    return this.baseData;
  }
  
  protected String serializeContent(Writer paramWriter)
    throws IOException
  {
    String str = super.serializeContent(paramWriter);
    paramWriter.write(str + "\"baseData\":");
    JsonHelper.writeJsonSerializable(paramWriter, this.baseData);
    return ",";
  }
  
  public void setBaseData(TDomain paramTDomain)
  {
    this.baseData = paramTDomain;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/metrics/model/Data.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */