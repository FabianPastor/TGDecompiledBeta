package net.hockeyapp.android.metrics;

import java.util.Map;
import net.hockeyapp.android.metrics.model.Domain;

public abstract class ITelemetry
  extends Domain
{
  public abstract String getBaseType();
  
  public abstract String getEnvelopeName();
  
  public abstract Map<String, String> getProperties();
  
  public abstract void setProperties(Map<String, String> paramMap);
  
  public abstract void setVer(int paramInt);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/metrics/ITelemetry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */