package net.hockeyapp.android.metrics.model;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Map;
import net.hockeyapp.android.metrics.JsonHelper;

public class Device
  implements IJsonSerializable, Serializable
{
  private String id;
  private String ip;
  private String language;
  private String locale;
  private String machineName;
  private String model;
  private String network;
  private String networkName;
  private String oemName;
  private String os;
  private String osVersion;
  private String roleInstance;
  private String roleName;
  private String screenResolution;
  private String type;
  private String vmName;
  
  public Device()
  {
    InitializeFields();
  }
  
  protected void InitializeFields() {}
  
  public void addToHashMap(Map<String, String> paramMap)
  {
    if (this.id != null) {
      paramMap.put("ai.device.id", this.id);
    }
    if (this.ip != null) {
      paramMap.put("ai.device.ip", this.ip);
    }
    if (this.language != null) {
      paramMap.put("ai.device.language", this.language);
    }
    if (this.locale != null) {
      paramMap.put("ai.device.locale", this.locale);
    }
    if (this.model != null) {
      paramMap.put("ai.device.model", this.model);
    }
    if (this.network != null) {
      paramMap.put("ai.device.network", this.network);
    }
    if (this.networkName != null) {
      paramMap.put("ai.device.networkName", this.networkName);
    }
    if (this.oemName != null) {
      paramMap.put("ai.device.oemName", this.oemName);
    }
    if (this.os != null) {
      paramMap.put("ai.device.os", this.os);
    }
    if (this.osVersion != null) {
      paramMap.put("ai.device.osVersion", this.osVersion);
    }
    if (this.roleInstance != null) {
      paramMap.put("ai.device.roleInstance", this.roleInstance);
    }
    if (this.roleName != null) {
      paramMap.put("ai.device.roleName", this.roleName);
    }
    if (this.screenResolution != null) {
      paramMap.put("ai.device.screenResolution", this.screenResolution);
    }
    if (this.type != null) {
      paramMap.put("ai.device.type", this.type);
    }
    if (this.machineName != null) {
      paramMap.put("ai.device.machineName", this.machineName);
    }
    if (this.vmName != null) {
      paramMap.put("ai.device.vmName", this.vmName);
    }
  }
  
  public String getId()
  {
    return this.id;
  }
  
  public String getIp()
  {
    return this.ip;
  }
  
  public String getLanguage()
  {
    return this.language;
  }
  
  public String getLocale()
  {
    return this.locale;
  }
  
  public String getMachineName()
  {
    return this.machineName;
  }
  
  public String getModel()
  {
    return this.model;
  }
  
  public String getNetwork()
  {
    return this.network;
  }
  
  public String getNetworkName()
  {
    return this.networkName;
  }
  
  public String getOemName()
  {
    return this.oemName;
  }
  
  public String getOs()
  {
    return this.os;
  }
  
  public String getOsVersion()
  {
    return this.osVersion;
  }
  
  public String getRoleInstance()
  {
    return this.roleInstance;
  }
  
  public String getRoleName()
  {
    return this.roleName;
  }
  
  public String getScreenResolution()
  {
    return this.screenResolution;
  }
  
  public String getType()
  {
    return this.type;
  }
  
  public String getVmName()
  {
    return this.vmName;
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
      paramWriter.write("" + "\"ai.device.id\":");
      paramWriter.write(JsonHelper.convert(this.id));
      localObject2 = ",";
    }
    Object localObject1 = localObject2;
    if (this.ip != null)
    {
      paramWriter.write((String)localObject2 + "\"ai.device.ip\":");
      paramWriter.write(JsonHelper.convert(this.ip));
      localObject1 = ",";
    }
    localObject2 = localObject1;
    if (this.language != null)
    {
      paramWriter.write((String)localObject1 + "\"ai.device.language\":");
      paramWriter.write(JsonHelper.convert(this.language));
      localObject2 = ",";
    }
    localObject1 = localObject2;
    if (this.locale != null)
    {
      paramWriter.write((String)localObject2 + "\"ai.device.locale\":");
      paramWriter.write(JsonHelper.convert(this.locale));
      localObject1 = ",";
    }
    localObject2 = localObject1;
    if (this.model != null)
    {
      paramWriter.write((String)localObject1 + "\"ai.device.model\":");
      paramWriter.write(JsonHelper.convert(this.model));
      localObject2 = ",";
    }
    localObject1 = localObject2;
    if (this.network != null)
    {
      paramWriter.write((String)localObject2 + "\"ai.device.network\":");
      paramWriter.write(JsonHelper.convert(this.network));
      localObject1 = ",";
    }
    localObject2 = localObject1;
    if (this.networkName != null)
    {
      paramWriter.write((String)localObject1 + "\"ai.device.networkName\":");
      paramWriter.write(JsonHelper.convert(this.networkName));
      localObject2 = ",";
    }
    localObject1 = localObject2;
    if (this.oemName != null)
    {
      paramWriter.write((String)localObject2 + "\"ai.device.oemName\":");
      paramWriter.write(JsonHelper.convert(this.oemName));
      localObject1 = ",";
    }
    localObject2 = localObject1;
    if (this.os != null)
    {
      paramWriter.write((String)localObject1 + "\"ai.device.os\":");
      paramWriter.write(JsonHelper.convert(this.os));
      localObject2 = ",";
    }
    localObject1 = localObject2;
    if (this.osVersion != null)
    {
      paramWriter.write((String)localObject2 + "\"ai.device.osVersion\":");
      paramWriter.write(JsonHelper.convert(this.osVersion));
      localObject1 = ",";
    }
    localObject2 = localObject1;
    if (this.roleInstance != null)
    {
      paramWriter.write((String)localObject1 + "\"ai.device.roleInstance\":");
      paramWriter.write(JsonHelper.convert(this.roleInstance));
      localObject2 = ",";
    }
    localObject1 = localObject2;
    if (this.roleName != null)
    {
      paramWriter.write((String)localObject2 + "\"ai.device.roleName\":");
      paramWriter.write(JsonHelper.convert(this.roleName));
      localObject1 = ",";
    }
    localObject2 = localObject1;
    if (this.screenResolution != null)
    {
      paramWriter.write((String)localObject1 + "\"ai.device.screenResolution\":");
      paramWriter.write(JsonHelper.convert(this.screenResolution));
      localObject2 = ",";
    }
    localObject1 = localObject2;
    if (this.type != null)
    {
      paramWriter.write((String)localObject2 + "\"ai.device.type\":");
      paramWriter.write(JsonHelper.convert(this.type));
      localObject1 = ",";
    }
    localObject2 = localObject1;
    if (this.machineName != null)
    {
      paramWriter.write((String)localObject1 + "\"ai.device.machineName\":");
      paramWriter.write(JsonHelper.convert(this.machineName));
      localObject2 = ",";
    }
    localObject1 = localObject2;
    if (this.vmName != null)
    {
      paramWriter.write((String)localObject2 + "\"ai.device.vmName\":");
      paramWriter.write(JsonHelper.convert(this.vmName));
      localObject1 = ",";
    }
    return (String)localObject1;
  }
  
  public void setId(String paramString)
  {
    this.id = paramString;
  }
  
  public void setIp(String paramString)
  {
    this.ip = paramString;
  }
  
  public void setLanguage(String paramString)
  {
    this.language = paramString;
  }
  
  public void setLocale(String paramString)
  {
    this.locale = paramString;
  }
  
  public void setMachineName(String paramString)
  {
    this.machineName = paramString;
  }
  
  public void setModel(String paramString)
  {
    this.model = paramString;
  }
  
  public void setNetwork(String paramString)
  {
    this.network = paramString;
  }
  
  public void setNetworkName(String paramString)
  {
    this.networkName = paramString;
  }
  
  public void setOemName(String paramString)
  {
    this.oemName = paramString;
  }
  
  public void setOs(String paramString)
  {
    this.os = paramString;
  }
  
  public void setOsVersion(String paramString)
  {
    this.osVersion = paramString;
  }
  
  public void setRoleInstance(String paramString)
  {
    this.roleInstance = paramString;
  }
  
  public void setRoleName(String paramString)
  {
    this.roleName = paramString;
  }
  
  public void setScreenResolution(String paramString)
  {
    this.screenResolution = paramString;
  }
  
  public void setType(String paramString)
  {
    this.type = paramString;
  }
  
  public void setVmName(String paramString)
  {
    this.vmName = paramString;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/metrics/model/Device.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */