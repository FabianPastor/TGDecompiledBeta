package net.hockeyapp.android.metrics.model;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Map;
import net.hockeyapp.android.metrics.JsonHelper;

public class Internal
  implements IJsonSerializable, Serializable
{
  private String accountId;
  private String agentVersion;
  private String applicationName;
  private String applicationType;
  private String dataCollectorReceivedTime;
  private String flowType;
  private String instrumentationKey;
  private String isAudit;
  private String profileClassId;
  private String profileId;
  private String requestSource;
  private String sdkVersion;
  private String telemetryItemId;
  private String trackingSourceId;
  private String trackingType;
  
  public Internal()
  {
    InitializeFields();
  }
  
  protected void InitializeFields() {}
  
  public void addToHashMap(Map<String, String> paramMap)
  {
    if (this.sdkVersion != null) {
      paramMap.put("ai.internal.sdkVersion", this.sdkVersion);
    }
    if (this.agentVersion != null) {
      paramMap.put("ai.internal.agentVersion", this.agentVersion);
    }
    if (this.dataCollectorReceivedTime != null) {
      paramMap.put("ai.internal.dataCollectorReceivedTime", this.dataCollectorReceivedTime);
    }
    if (this.profileId != null) {
      paramMap.put("ai.internal.profileId", this.profileId);
    }
    if (this.profileClassId != null) {
      paramMap.put("ai.internal.profileClassId", this.profileClassId);
    }
    if (this.accountId != null) {
      paramMap.put("ai.internal.accountId", this.accountId);
    }
    if (this.applicationName != null) {
      paramMap.put("ai.internal.applicationName", this.applicationName);
    }
    if (this.instrumentationKey != null) {
      paramMap.put("ai.internal.instrumentationKey", this.instrumentationKey);
    }
    if (this.telemetryItemId != null) {
      paramMap.put("ai.internal.telemetryItemId", this.telemetryItemId);
    }
    if (this.applicationType != null) {
      paramMap.put("ai.internal.applicationType", this.applicationType);
    }
    if (this.requestSource != null) {
      paramMap.put("ai.internal.requestSource", this.requestSource);
    }
    if (this.flowType != null) {
      paramMap.put("ai.internal.flowType", this.flowType);
    }
    if (this.isAudit != null) {
      paramMap.put("ai.internal.isAudit", this.isAudit);
    }
    if (this.trackingSourceId != null) {
      paramMap.put("ai.internal.trackingSourceId", this.trackingSourceId);
    }
    if (this.trackingType != null) {
      paramMap.put("ai.internal.trackingType", this.trackingType);
    }
  }
  
  public String getAccountId()
  {
    return this.accountId;
  }
  
  public String getAgentVersion()
  {
    return this.agentVersion;
  }
  
  public String getApplicationName()
  {
    return this.applicationName;
  }
  
  public String getApplicationType()
  {
    return this.applicationType;
  }
  
  public String getDataCollectorReceivedTime()
  {
    return this.dataCollectorReceivedTime;
  }
  
  public String getFlowType()
  {
    return this.flowType;
  }
  
  public String getInstrumentationKey()
  {
    return this.instrumentationKey;
  }
  
  public String getIsAudit()
  {
    return this.isAudit;
  }
  
  public String getProfileClassId()
  {
    return this.profileClassId;
  }
  
  public String getProfileId()
  {
    return this.profileId;
  }
  
  public String getRequestSource()
  {
    return this.requestSource;
  }
  
  public String getSdkVersion()
  {
    return this.sdkVersion;
  }
  
  public String getTelemetryItemId()
  {
    return this.telemetryItemId;
  }
  
  public String getTrackingSourceId()
  {
    return this.trackingSourceId;
  }
  
  public String getTrackingType()
  {
    return this.trackingType;
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
    if (this.sdkVersion != null)
    {
      paramWriter.write("" + "\"ai.internal.sdkVersion\":");
      paramWriter.write(JsonHelper.convert(this.sdkVersion));
      localObject2 = ",";
    }
    Object localObject1 = localObject2;
    if (this.agentVersion != null)
    {
      paramWriter.write((String)localObject2 + "\"ai.internal.agentVersion\":");
      paramWriter.write(JsonHelper.convert(this.agentVersion));
      localObject1 = ",";
    }
    localObject2 = localObject1;
    if (this.dataCollectorReceivedTime != null)
    {
      paramWriter.write((String)localObject1 + "\"ai.internal.dataCollectorReceivedTime\":");
      paramWriter.write(JsonHelper.convert(this.dataCollectorReceivedTime));
      localObject2 = ",";
    }
    localObject1 = localObject2;
    if (this.profileId != null)
    {
      paramWriter.write((String)localObject2 + "\"ai.internal.profileId\":");
      paramWriter.write(JsonHelper.convert(this.profileId));
      localObject1 = ",";
    }
    localObject2 = localObject1;
    if (this.profileClassId != null)
    {
      paramWriter.write((String)localObject1 + "\"ai.internal.profileClassId\":");
      paramWriter.write(JsonHelper.convert(this.profileClassId));
      localObject2 = ",";
    }
    localObject1 = localObject2;
    if (this.accountId != null)
    {
      paramWriter.write((String)localObject2 + "\"ai.internal.accountId\":");
      paramWriter.write(JsonHelper.convert(this.accountId));
      localObject1 = ",";
    }
    localObject2 = localObject1;
    if (this.applicationName != null)
    {
      paramWriter.write((String)localObject1 + "\"ai.internal.applicationName\":");
      paramWriter.write(JsonHelper.convert(this.applicationName));
      localObject2 = ",";
    }
    localObject1 = localObject2;
    if (this.instrumentationKey != null)
    {
      paramWriter.write((String)localObject2 + "\"ai.internal.instrumentationKey\":");
      paramWriter.write(JsonHelper.convert(this.instrumentationKey));
      localObject1 = ",";
    }
    localObject2 = localObject1;
    if (this.telemetryItemId != null)
    {
      paramWriter.write((String)localObject1 + "\"ai.internal.telemetryItemId\":");
      paramWriter.write(JsonHelper.convert(this.telemetryItemId));
      localObject2 = ",";
    }
    localObject1 = localObject2;
    if (this.applicationType != null)
    {
      paramWriter.write((String)localObject2 + "\"ai.internal.applicationType\":");
      paramWriter.write(JsonHelper.convert(this.applicationType));
      localObject1 = ",";
    }
    localObject2 = localObject1;
    if (this.requestSource != null)
    {
      paramWriter.write((String)localObject1 + "\"ai.internal.requestSource\":");
      paramWriter.write(JsonHelper.convert(this.requestSource));
      localObject2 = ",";
    }
    localObject1 = localObject2;
    if (this.flowType != null)
    {
      paramWriter.write((String)localObject2 + "\"ai.internal.flowType\":");
      paramWriter.write(JsonHelper.convert(this.flowType));
      localObject1 = ",";
    }
    localObject2 = localObject1;
    if (this.isAudit != null)
    {
      paramWriter.write((String)localObject1 + "\"ai.internal.isAudit\":");
      paramWriter.write(JsonHelper.convert(this.isAudit));
      localObject2 = ",";
    }
    localObject1 = localObject2;
    if (this.trackingSourceId != null)
    {
      paramWriter.write((String)localObject2 + "\"ai.internal.trackingSourceId\":");
      paramWriter.write(JsonHelper.convert(this.trackingSourceId));
      localObject1 = ",";
    }
    localObject2 = localObject1;
    if (this.trackingType != null)
    {
      paramWriter.write((String)localObject1 + "\"ai.internal.trackingType\":");
      paramWriter.write(JsonHelper.convert(this.trackingType));
      localObject2 = ",";
    }
    return (String)localObject2;
  }
  
  public void setAccountId(String paramString)
  {
    this.accountId = paramString;
  }
  
  public void setAgentVersion(String paramString)
  {
    this.agentVersion = paramString;
  }
  
  public void setApplicationName(String paramString)
  {
    this.applicationName = paramString;
  }
  
  public void setApplicationType(String paramString)
  {
    this.applicationType = paramString;
  }
  
  public void setDataCollectorReceivedTime(String paramString)
  {
    this.dataCollectorReceivedTime = paramString;
  }
  
  public void setFlowType(String paramString)
  {
    this.flowType = paramString;
  }
  
  public void setInstrumentationKey(String paramString)
  {
    this.instrumentationKey = paramString;
  }
  
  public void setIsAudit(String paramString)
  {
    this.isAudit = paramString;
  }
  
  public void setProfileClassId(String paramString)
  {
    this.profileClassId = paramString;
  }
  
  public void setProfileId(String paramString)
  {
    this.profileId = paramString;
  }
  
  public void setRequestSource(String paramString)
  {
    this.requestSource = paramString;
  }
  
  public void setSdkVersion(String paramString)
  {
    this.sdkVersion = paramString;
  }
  
  public void setTelemetryItemId(String paramString)
  {
    this.telemetryItemId = paramString;
  }
  
  public void setTrackingSourceId(String paramString)
  {
    this.trackingSourceId = paramString;
  }
  
  public void setTrackingType(String paramString)
  {
    this.trackingType = paramString;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/metrics/model/Internal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */