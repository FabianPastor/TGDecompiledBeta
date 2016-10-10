package com.google.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzab.zza;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzaj;
import com.google.android.gms.common.util.zzw;

public final class FirebaseOptions
{
  private final String aSU;
  private final String aSV;
  private final String jM;
  private final String yQ;
  private final String yT;
  private final String yU;
  
  private FirebaseOptions(@NonNull String paramString1, @NonNull String paramString2, @Nullable String paramString3, @Nullable String paramString4, @Nullable String paramString5, @Nullable String paramString6)
  {
    if (!zzw.zzij(paramString1)) {}
    for (boolean bool = true;; bool = false)
    {
      zzac.zza(bool, "ApplicationId must be set.");
      this.jM = paramString1;
      this.yQ = paramString2;
      this.aSU = paramString3;
      this.aSV = paramString4;
      this.yT = paramString5;
      this.yU = paramString6;
      return;
    }
  }
  
  public static FirebaseOptions fromResource(Context paramContext)
  {
    paramContext = new zzaj(paramContext);
    String str = paramContext.getString("google_app_id");
    if (TextUtils.isEmpty(str)) {
      return null;
    }
    return new FirebaseOptions(str, paramContext.getString("google_api_key"), paramContext.getString("firebase_database_url"), paramContext.getString("ga_trackingId"), paramContext.getString("gcm_defaultSenderId"), paramContext.getString("google_storage_bucket"));
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof FirebaseOptions)) {}
    do
    {
      return false;
      paramObject = (FirebaseOptions)paramObject;
    } while ((!zzab.equal(this.jM, ((FirebaseOptions)paramObject).jM)) || (!zzab.equal(this.yQ, ((FirebaseOptions)paramObject).yQ)) || (!zzab.equal(this.aSU, ((FirebaseOptions)paramObject).aSU)) || (!zzab.equal(this.aSV, ((FirebaseOptions)paramObject).aSV)) || (!zzab.equal(this.yT, ((FirebaseOptions)paramObject).yT)) || (!zzab.equal(this.yU, ((FirebaseOptions)paramObject).yU)));
    return true;
  }
  
  public String getApiKey()
  {
    return this.yQ;
  }
  
  public String getApplicationId()
  {
    return this.jM;
  }
  
  public String getDatabaseUrl()
  {
    return this.aSU;
  }
  
  public String getGcmSenderId()
  {
    return this.yT;
  }
  
  public String getStorageBucket()
  {
    return this.yU;
  }
  
  public int hashCode()
  {
    return zzab.hashCode(new Object[] { this.jM, this.yQ, this.aSU, this.aSV, this.yT, this.yU });
  }
  
  public String toString()
  {
    return zzab.zzx(this).zzg("applicationId", this.jM).zzg("apiKey", this.yQ).zzg("databaseUrl", this.aSU).zzg("gcmSenderId", this.yT).zzg("storageBucket", this.yU).toString();
  }
  
  public static final class Builder
  {
    private String aSU;
    private String aSV;
    private String jM;
    private String yQ;
    private String yT;
    private String yU;
    
    public Builder() {}
    
    public Builder(FirebaseOptions paramFirebaseOptions)
    {
      this.jM = FirebaseOptions.zza(paramFirebaseOptions);
      this.yQ = FirebaseOptions.zzb(paramFirebaseOptions);
      this.aSU = FirebaseOptions.zzc(paramFirebaseOptions);
      this.aSV = FirebaseOptions.zzd(paramFirebaseOptions);
      this.yT = FirebaseOptions.zze(paramFirebaseOptions);
      this.yU = FirebaseOptions.zzf(paramFirebaseOptions);
    }
    
    public FirebaseOptions build()
    {
      return new FirebaseOptions(this.jM, this.yQ, this.aSU, this.aSV, this.yT, this.yU, null);
    }
    
    public Builder setApiKey(@NonNull String paramString)
    {
      this.yQ = zzac.zzh(paramString, "ApiKey must be set.");
      return this;
    }
    
    public Builder setApplicationId(@NonNull String paramString)
    {
      this.jM = zzac.zzh(paramString, "ApplicationId must be set.");
      return this;
    }
    
    public Builder setDatabaseUrl(@Nullable String paramString)
    {
      this.aSU = paramString;
      return this;
    }
    
    public Builder setGcmSenderId(@Nullable String paramString)
    {
      this.yT = paramString;
      return this;
    }
    
    public Builder setStorageBucket(@Nullable String paramString)
    {
      this.yU = paramString;
      return this;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/FirebaseOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */