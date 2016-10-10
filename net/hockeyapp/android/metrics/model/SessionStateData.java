package net.hockeyapp.android.metrics.model;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import net.hockeyapp.android.metrics.JsonHelper;

public class SessionStateData
  extends TelemetryData
{
  private SessionState state = SessionState.START;
  private int ver = 2;
  
  public SessionStateData()
  {
    InitializeFields();
    SetupAttributes();
  }
  
  protected void InitializeFields()
  {
    this.QualifiedName = "com.microsoft.applicationinsights.contracts.SessionStateData";
  }
  
  public void SetupAttributes() {}
  
  public String getBaseType()
  {
    return "SessionStateData";
  }
  
  public String getEnvelopeName()
  {
    return "Microsoft.ApplicationInsights.SessionState";
  }
  
  public Map<String, String> getProperties()
  {
    return null;
  }
  
  public SessionState getState()
  {
    return this.state;
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
    paramWriter.write("," + "\"state\":");
    paramWriter.write(JsonHelper.convert(Integer.valueOf(this.state.getValue())));
    return ",";
  }
  
  public void setProperties(Map<String, String> paramMap) {}
  
  public void setState(SessionState paramSessionState)
  {
    this.state = paramSessionState;
  }
  
  public void setVer(int paramInt)
  {
    this.ver = paramInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/metrics/model/SessionStateData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */