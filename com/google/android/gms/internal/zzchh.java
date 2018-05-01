package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.text.TextUtils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.zzbz;
import com.google.firebase.iid.FirebaseInstanceId;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Locale;

public final class zzchh
  extends zzcjl
{
  private String mAppId;
  private String zzcwz;
  private String zzdqz;
  private String zzdra;
  private String zzixc;
  private long zzixg;
  private int zzjbk;
  private long zzjbl;
  private int zzjbm;
  
  zzchh(zzcim paramzzcim)
  {
    super(paramzzcim);
  }
  
  private final String zzaxd()
  {
    zzve();
    try
    {
      String str = FirebaseInstanceId.getInstance().getId();
      return str;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      zzawy().zzazf().log("Failed to retrieve Firebase Instance Id");
    }
    return null;
  }
  
  final String getAppId()
  {
    zzxf();
    return this.mAppId;
  }
  
  final String getGmpAppId()
  {
    zzxf();
    return this.zzcwz;
  }
  
  protected final boolean zzaxz()
  {
    return true;
  }
  
  protected final void zzayy()
  {
    int j = 1;
    Object localObject3 = "unknown";
    String str3 = "Unknown";
    int k = Integer.MIN_VALUE;
    String str1 = "Unknown";
    String str4 = getContext().getPackageName();
    Object localObject6 = getContext().getPackageManager();
    Object localObject5;
    String str2;
    int i;
    Object localObject1;
    if (localObject6 == null)
    {
      zzawy().zzazd().zzj("PackageManager is null, app identity information might be inaccurate. appId", zzchm.zzjk(str4));
      localObject5 = localObject3;
      str2 = str3;
      i = k;
      localObject3 = str1;
      this.mAppId = str4;
      this.zzixc = ((String)localObject5);
      this.zzdra = str2;
      this.zzjbk = i;
      this.zzdqz = ((String)localObject3);
      this.zzjbl = 0L;
      localObject1 = zzbz.zzck(getContext());
      if ((localObject1 == null) || (!((Status)localObject1).isSuccess())) {
        break label519;
      }
      i = 1;
      label129:
      if (i == 0)
      {
        if (localObject1 != null) {
          break label524;
        }
        zzawy().zzazd().log("GoogleService failed to initialize (no status)");
      }
      label150:
      if (i == 0) {
        break label667;
      }
      localObject1 = zzaxa().zziy("firebase_analytics_collection_enabled");
      if (!zzaxa().zzaya()) {
        break label553;
      }
      zzawy().zzazh().log("Collection disabled with firebase_analytics_collection_deactivated=1");
      i = 0;
    }
    for (;;)
    {
      this.zzcwz = "";
      this.zzixg = 0L;
      try
      {
        localObject3 = zzbz.zzajh();
        localObject1 = localObject3;
        if (TextUtils.isEmpty((CharSequence)localObject3)) {
          localObject1 = "";
        }
        this.zzcwz = ((String)localObject1);
        if (i != 0) {
          zzawy().zzazj().zze("App package, google app id", this.mAppId, this.zzcwz);
        }
      }
      catch (IllegalStateException localIllegalStateException)
      {
        for (;;)
        {
          Object localObject2;
          label519:
          label524:
          label553:
          zzawy().zzazd().zze("getGoogleAppId or isMeasurementEnabled failed with exception. appId", zzchm.zzjk(str4), localIllegalStateException);
          continue;
          i = 0;
        }
        this.zzjbm = 0;
        return;
      }
      if (Build.VERSION.SDK_INT >= 16) {
        if (zzbhd.zzcz(getContext()))
        {
          i = j;
          this.zzjbm = i;
          return;
          try
          {
            localObject1 = ((PackageManager)localObject6).getInstallerPackageName(str4);
            localObject3 = localObject1;
            if (localObject3 == null)
            {
              localObject1 = "manual_install";
              localObject5 = str1;
              str2 = str3;
            }
          }
          catch (IllegalArgumentException localIllegalArgumentException)
          {
            for (;;)
            {
              try
              {
                PackageInfo localPackageInfo = ((PackageManager)localObject6).getPackageInfo(getContext().getPackageName(), 0);
                localObject3 = str1;
                i = k;
                str2 = str3;
                localObject5 = localObject1;
                if (localPackageInfo == null) {
                  break;
                }
                localObject5 = str1;
                str2 = str3;
                localObject6 = ((PackageManager)localObject6).getApplicationLabel(localPackageInfo.applicationInfo);
                localObject3 = str1;
                localObject5 = str1;
                str2 = str3;
                if (!TextUtils.isEmpty((CharSequence)localObject6))
                {
                  localObject5 = str1;
                  str2 = str3;
                  localObject3 = ((CharSequence)localObject6).toString();
                }
                localObject5 = localObject3;
                str2 = str3;
                str1 = localPackageInfo.versionName;
                localObject5 = localObject3;
                str2 = str1;
                i = localPackageInfo.versionCode;
                str2 = str1;
                localObject5 = localObject1;
              }
              catch (PackageManager.NameNotFoundException localNameNotFoundException)
              {
                zzawy().zzazd().zze("Error retrieving package info. appId, appName", zzchm.zzjk(str4), localObject5);
                Object localObject4 = localObject5;
                i = k;
                localObject5 = localObject2;
              }
              localIllegalArgumentException = localIllegalArgumentException;
              zzawy().zzazd().zzj("Error retrieving app installer package name. appId", zzchm.zzjk(str4));
              continue;
              localObject2 = localObject3;
              if ("com.android.vending".equals(localObject3)) {
                localObject2 = "";
              }
            }
          }
          break;
          i = 0;
          break label129;
          zzawy().zzazd().zze("GoogleService failed to initialize, status", Integer.valueOf(((Status)localObject2).getStatusCode()), ((Status)localObject2).getStatusMessage());
          break label150;
          if ((localObject2 != null) && (!((Boolean)localObject2).booleanValue()))
          {
            zzawy().zzazh().log("Collection disabled with firebase_analytics_collection_enabled=0");
            i = 0;
            continue;
          }
          if ((localObject2 == null) && (zzbz.zzaji()))
          {
            zzawy().zzazh().log("Collection disabled with google_app_measurement_enable=0");
            i = 0;
            continue;
          }
          zzawy().zzazj().log("Collection enabled");
          i = 1;
          continue;
        }
      }
      label667:
      i = 0;
    }
  }
  
  final String zzayz()
  {
    byte[] arrayOfByte = new byte[16];
    zzawu().zzbaz().nextBytes(arrayOfByte);
    return String.format(Locale.US, "%032x", new Object[] { new BigInteger(1, arrayOfByte) });
  }
  
  final int zzaza()
  {
    zzxf();
    return this.zzjbk;
  }
  
  final int zzazb()
  {
    zzxf();
    return this.zzjbm;
  }
  
  final zzcgi zzjg(String paramString)
  {
    zzve();
    String str1 = getAppId();
    String str2 = getGmpAppId();
    zzxf();
    String str3 = this.zzdra;
    long l1 = zzaza();
    zzxf();
    String str4 = this.zzixc;
    zzxf();
    zzve();
    if (this.zzjbl == 0L) {
      this.zzjbl = this.zziwf.zzawu().zzaf(getContext(), getContext().getPackageName());
    }
    long l2 = this.zzjbl;
    boolean bool3 = this.zziwf.isEnabled();
    boolean bool1;
    String str5;
    long l3;
    int i;
    if (!zzawz().zzjdj)
    {
      bool1 = true;
      str5 = zzaxd();
      zzxf();
      l3 = this.zziwf.zzbaf();
      i = zzazb();
      Boolean localBoolean = zzaxa().zziy("google_analytics_adid_collection_enabled");
      if ((localBoolean != null) && (!localBoolean.booleanValue())) {
        break label211;
      }
    }
    label211:
    for (boolean bool2 = true;; bool2 = false)
    {
      return new zzcgi(str1, str2, str3, l1, str4, 11910L, l2, paramString, bool3, bool1, str5, 0L, l3, i, Boolean.valueOf(bool2).booleanValue());
      bool1 = false;
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzchh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */