package com.google.firebase;

import android.content.Context;
import android.text.TextUtils;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Objects.ToStringHelper;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.StringResourceValueReader;
import com.google.android.gms.common.util.Strings;

public final class FirebaseOptions
{
  private final String zzr;
  private final String zzs;
  private final String zzt;
  private final String zzu;
  private final String zzv;
  private final String zzw;
  private final String zzx;
  
  private FirebaseOptions(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7)
  {
    if (!Strings.isEmptyOrWhitespace(paramString1)) {}
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkState(bool, "ApplicationId must be set.");
      this.zzs = paramString1;
      this.zzr = paramString2;
      this.zzt = paramString3;
      this.zzu = paramString4;
      this.zzv = paramString5;
      this.zzw = paramString6;
      this.zzx = paramString7;
      return;
    }
  }
  
  public static FirebaseOptions fromResource(Context paramContext)
  {
    paramContext = new StringResourceValueReader(paramContext);
    String str = paramContext.getString("google_app_id");
    if (TextUtils.isEmpty(str)) {}
    for (paramContext = null;; paramContext = new FirebaseOptions(str, paramContext.getString("google_api_key"), paramContext.getString("firebase_database_url"), paramContext.getString("ga_trackingId"), paramContext.getString("gcm_defaultSenderId"), paramContext.getString("google_storage_bucket"), paramContext.getString("project_id"))) {
      return paramContext;
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    boolean bool1 = false;
    boolean bool2;
    if (!(paramObject instanceof FirebaseOptions)) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      paramObject = (FirebaseOptions)paramObject;
      bool2 = bool1;
      if (Objects.equal(this.zzs, ((FirebaseOptions)paramObject).zzs))
      {
        bool2 = bool1;
        if (Objects.equal(this.zzr, ((FirebaseOptions)paramObject).zzr))
        {
          bool2 = bool1;
          if (Objects.equal(this.zzt, ((FirebaseOptions)paramObject).zzt))
          {
            bool2 = bool1;
            if (Objects.equal(this.zzu, ((FirebaseOptions)paramObject).zzu))
            {
              bool2 = bool1;
              if (Objects.equal(this.zzv, ((FirebaseOptions)paramObject).zzv))
              {
                bool2 = bool1;
                if (Objects.equal(this.zzw, ((FirebaseOptions)paramObject).zzw))
                {
                  bool2 = bool1;
                  if (Objects.equal(this.zzx, ((FirebaseOptions)paramObject).zzx)) {
                    bool2 = true;
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
  public final String getApplicationId()
  {
    return this.zzs;
  }
  
  public final String getGcmSenderId()
  {
    return this.zzv;
  }
  
  public final int hashCode()
  {
    return Objects.hashCode(new Object[] { this.zzs, this.zzr, this.zzt, this.zzu, this.zzv, this.zzw, this.zzx });
  }
  
  public final String toString()
  {
    return Objects.toStringHelper(this).add("applicationId", this.zzs).add("apiKey", this.zzr).add("databaseUrl", this.zzt).add("gcmSenderId", this.zzv).add("storageBucket", this.zzw).add("projectId", this.zzx).toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/FirebaseOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */