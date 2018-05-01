package net.hockeyapp.android.metrics.model;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import net.hockeyapp.android.metrics.JsonHelper;

public class EventData
  extends TelemetryData
{
  private Map<String, Double> measurements;
  private String name;
  private Map<String, String> properties;
  private int ver = 2;
  
  public EventData()
  {
    InitializeFields();
    SetupAttributes();
  }
  
  protected void InitializeFields()
  {
    this.QualifiedName = "com.microsoft.applicationinsights.contracts.EventData";
  }
  
  public void SetupAttributes() {}
  
  public String getBaseType()
  {
    return "EventData";
  }
  
  public String getEnvelopeName()
  {
    return "Microsoft.ApplicationInsights.Event";
  }
  
  public Map<String, Double> getMeasurements()
  {
    if (this.measurements == null) {
      this.measurements = new LinkedHashMap();
    }
    return this.measurements;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public Map<String, String> getProperties()
  {
    if (this.properties == null) {
      this.properties = new LinkedHashMap();
    }
    return this.properties;
  }
  
  public int getVer()
  {
    return this.ver;
  }
  
  protected String serializeContent(Writer paramWriter)
    throws IOException
  {
    String str = super.serializeContent(paramWriter);
    paramWriter.write(str + "\"ver\":");
    paramWriter.write(JsonHelper.convert(Integer.valueOf(this.ver)));
    paramWriter.write("," + "\"name\":");
    paramWriter.write(JsonHelper.convert(this.name));
    if (this.properties != null)
    {
      paramWriter.write("," + "\"properties\":");
      JsonHelper.writeDictionary(paramWriter, this.properties);
    }
    if (this.measurements != null)
    {
      paramWriter.write("," + "\"measurements\":");
      JsonHelper.writeDictionary(paramWriter, this.measurements);
    }
    return ",";
  }
  
  public void setMeasurements(Map<String, Double> paramMap)
  {
    this.measurements = paramMap;
  }
  
  public void setName(String paramString)
  {
    this.name = paramString;
  }
  
  public void setProperties(Map<String, String> paramMap)
  {
    this.properties = paramMap;
  }
  
  public void setVer(int paramInt)
  {
    this.ver = paramInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/metrics/model/EventData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */