package net.hockeyapp.android.metrics.model;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Map;
import net.hockeyapp.android.metrics.JsonHelper;

public class User
  implements IJsonSerializable, Serializable
{
  private String accountAcquisitionDate;
  private String accountId;
  private String anonUserAcquisitionDate;
  private String authUserAcquisitionDate;
  private String authUserId;
  private String id;
  private String storeRegion;
  private String userAgent;
  
  public User()
  {
    InitializeFields();
  }
  
  protected void InitializeFields() {}
  
  public void addToHashMap(Map<String, String> paramMap)
  {
    if (this.accountAcquisitionDate != null) {
      paramMap.put("ai.user.accountAcquisitionDate", this.accountAcquisitionDate);
    }
    if (this.accountId != null) {
      paramMap.put("ai.user.accountId", this.accountId);
    }
    if (this.userAgent != null) {
      paramMap.put("ai.user.userAgent", this.userAgent);
    }
    if (this.id != null) {
      paramMap.put("ai.user.id", this.id);
    }
    if (this.storeRegion != null) {
      paramMap.put("ai.user.storeRegion", this.storeRegion);
    }
    if (this.authUserId != null) {
      paramMap.put("ai.user.authUserId", this.authUserId);
    }
    if (this.anonUserAcquisitionDate != null) {
      paramMap.put("ai.user.anonUserAcquisitionDate", this.anonUserAcquisitionDate);
    }
    if (this.authUserAcquisitionDate != null) {
      paramMap.put("ai.user.authUserAcquisitionDate", this.authUserAcquisitionDate);
    }
  }
  
  public String getAccountAcquisitionDate()
  {
    return this.accountAcquisitionDate;
  }
  
  public String getAccountId()
  {
    return this.accountId;
  }
  
  public String getAnonUserAcquisitionDate()
  {
    return this.anonUserAcquisitionDate;
  }
  
  public String getAuthUserAcquisitionDate()
  {
    return this.authUserAcquisitionDate;
  }
  
  public String getAuthUserId()
  {
    return this.authUserId;
  }
  
  public String getId()
  {
    return this.id;
  }
  
  public String getStoreRegion()
  {
    return this.storeRegion;
  }
  
  public String getUserAgent()
  {
    return this.userAgent;
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
    if (this.accountAcquisitionDate != null)
    {
      paramWriter.write("" + "\"ai.user.accountAcquisitionDate\":");
      paramWriter.write(JsonHelper.convert(this.accountAcquisitionDate));
      localObject2 = ",";
    }
    Object localObject1 = localObject2;
    if (this.accountId != null)
    {
      paramWriter.write((String)localObject2 + "\"ai.user.accountId\":");
      paramWriter.write(JsonHelper.convert(this.accountId));
      localObject1 = ",";
    }
    localObject2 = localObject1;
    if (this.userAgent != null)
    {
      paramWriter.write((String)localObject1 + "\"ai.user.userAgent\":");
      paramWriter.write(JsonHelper.convert(this.userAgent));
      localObject2 = ",";
    }
    localObject1 = localObject2;
    if (this.id != null)
    {
      paramWriter.write((String)localObject2 + "\"ai.user.id\":");
      paramWriter.write(JsonHelper.convert(this.id));
      localObject1 = ",";
    }
    localObject2 = localObject1;
    if (this.storeRegion != null)
    {
      paramWriter.write((String)localObject1 + "\"ai.user.storeRegion\":");
      paramWriter.write(JsonHelper.convert(this.storeRegion));
      localObject2 = ",";
    }
    localObject1 = localObject2;
    if (this.authUserId != null)
    {
      paramWriter.write((String)localObject2 + "\"ai.user.authUserId\":");
      paramWriter.write(JsonHelper.convert(this.authUserId));
      localObject1 = ",";
    }
    localObject2 = localObject1;
    if (this.anonUserAcquisitionDate != null)
    {
      paramWriter.write((String)localObject1 + "\"ai.user.anonUserAcquisitionDate\":");
      paramWriter.write(JsonHelper.convert(this.anonUserAcquisitionDate));
      localObject2 = ",";
    }
    localObject1 = localObject2;
    if (this.authUserAcquisitionDate != null)
    {
      paramWriter.write((String)localObject2 + "\"ai.user.authUserAcquisitionDate\":");
      paramWriter.write(JsonHelper.convert(this.authUserAcquisitionDate));
      localObject1 = ",";
    }
    return (String)localObject1;
  }
  
  public void setAccountAcquisitionDate(String paramString)
  {
    this.accountAcquisitionDate = paramString;
  }
  
  public void setAccountId(String paramString)
  {
    this.accountId = paramString;
  }
  
  public void setAnonUserAcquisitionDate(String paramString)
  {
    this.anonUserAcquisitionDate = paramString;
  }
  
  public void setAuthUserAcquisitionDate(String paramString)
  {
    this.authUserAcquisitionDate = paramString;
  }
  
  public void setAuthUserId(String paramString)
  {
    this.authUserId = paramString;
  }
  
  public void setId(String paramString)
  {
    this.id = paramString;
  }
  
  public void setStoreRegion(String paramString)
  {
    this.storeRegion = paramString;
  }
  
  public void setUserAgent(String paramString)
  {
    this.userAgent = paramString;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/metrics/model/User.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */