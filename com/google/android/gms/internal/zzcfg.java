package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.api.Status;
import com.google.firebase.iid.FirebaseInstanceId;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Locale;

public final class zzcfg
  extends zzchj
{
  private String mAppId;
  private String zzXB;
  private String zzaeH;
  private String zzaeI;
  private String zzboB;
  private long zzboF;
  private int zzbqC;
  private long zzbqD;
  private int zzbqE;
  
  zzcfg(zzcgl paramzzcgl)
  {
    super(paramzzcgl);
  }
  
  @WorkerThread
  private final String zzwK()
  {
    super.zzjC();
    try
    {
      String str = FirebaseInstanceId.getInstance().getId();
      return str;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      super.zzwF().zzyz().log("Failed to retrieve Firebase Instance Id");
    }
    return null;
  }
  
  final String getGmpAppId()
  {
    zzkD();
    return this.zzXB;
  }
  
  @WorkerThread
  final zzceh zzdV(String paramString)
  {
    super.zzjC();
    String str1 = zzhl();
    String str2 = getGmpAppId();
    zzkD();
    String str3 = this.zzaeI;
    long l1 = zzyv();
    zzkD();
    String str4 = this.zzboB;
    long l2 = zzcem.zzwP();
    zzkD();
    super.zzjC();
    if (this.zzbqD == 0L) {
      this.zzbqD = this.zzboe.zzwB().zzI(super.getContext(), super.getContext().getPackageName());
    }
    long l3 = this.zzbqD;
    boolean bool2 = this.zzboe.isEnabled();
    if (!super.zzwG().zzbrC) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      String str5 = zzwK();
      zzkD();
      long l4 = this.zzboe.zzyY();
      zzkD();
      return new zzceh(str1, str2, str3, l1, str4, l2, l3, paramString, bool2, bool1, str5, 0L, l4, this.zzbqE);
    }
  }
  
  final String zzhl()
  {
    zzkD();
    return this.mAppId;
  }
  
  protected final void zzjD()
  {
    int j = 1;
    Object localObject3 = "unknown";
    String str3 = "Unknown";
    int k = Integer.MIN_VALUE;
    String str1 = "Unknown";
    String str4 = super.getContext().getPackageName();
    Object localObject6 = super.getContext().getPackageManager();
    Object localObject5;
    String str2;
    int i;
    Object localObject1;
    if (localObject6 == null)
    {
      super.zzwF().zzyx().zzj("PackageManager is null, app identity information might be inaccurate. appId", zzcfl.zzdZ(str4));
      localObject5 = localObject3;
      str2 = str3;
      i = k;
      localObject3 = str1;
      this.mAppId = str4;
      this.zzboB = ((String)localObject5);
      this.zzaeI = str2;
      this.zzbqC = i;
      this.zzaeH = ((String)localObject3);
      this.zzbqD = 0L;
      zzcem.zzxE();
      localObject1 = zzbdm.zzaz(super.getContext());
      if ((localObject1 == null) || (!((Status)localObject1).isSuccess())) {
        break label526;
      }
      i = 1;
      label133:
      if (i == 0)
      {
        if (localObject1 != null) {
          break label531;
        }
        super.zzwF().zzyx().log("GoogleService failed to initialize (no status)");
      }
      label154:
      if (i == 0) {
        break label674;
      }
      localObject1 = super.zzwH().zzdN("firebase_analytics_collection_enabled");
      if (!super.zzwH().zzxF()) {
        break label560;
      }
      super.zzwF().zzyB().log("Collection disabled with firebase_analytics_collection_deactivated=1");
      i = 0;
    }
    for (;;)
    {
      this.zzXB = "";
      this.zzboF = 0L;
      zzcem.zzxE();
      try
      {
        localObject3 = zzbdm.zzqA();
        localObject1 = localObject3;
        if (TextUtils.isEmpty((CharSequence)localObject3)) {
          localObject1 = "";
        }
        this.zzXB = ((String)localObject1);
        if (i != 0) {
          super.zzwF().zzyD().zze("App package, google app id", this.mAppId, this.zzXB);
        }
      }
      catch (IllegalStateException localIllegalStateException)
      {
        for (;;)
        {
          Object localObject2;
          label526:
          label531:
          label560:
          super.zzwF().zzyx().zze("getGoogleAppId or isMeasurementEnabled failed with exception. appId", zzcfl.zzdZ(str4), localIllegalStateException);
          continue;
          i = 0;
        }
        this.zzbqE = 0;
        return;
      }
      if (Build.VERSION.SDK_INT >= 16) {
        if (zzbgy.zzaN(super.getContext()))
        {
          i = j;
          this.zzbqE = i;
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
                PackageInfo localPackageInfo = ((PackageManager)localObject6).getPackageInfo(super.getContext().getPackageName(), 0);
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
                super.zzwF().zzyx().zze("Error retrieving package info. appId, appName", zzcfl.zzdZ(str4), localObject5);
                Object localObject4 = localObject5;
                i = k;
                localObject5 = localObject2;
              }
              localIllegalArgumentException = localIllegalArgumentException;
              super.zzwF().zzyx().zzj("Error retrieving app installer package name. appId", zzcfl.zzdZ(str4));
              continue;
              localObject2 = localObject3;
              if ("com.android.vending".equals(localObject3)) {
                localObject2 = "";
              }
            }
          }
          break;
          i = 0;
          break label133;
          super.zzwF().zzyx().zze("GoogleService failed to initialize, status", Integer.valueOf(((Status)localObject2).getStatusCode()), ((Status)localObject2).getStatusMessage());
          break label154;
          if ((localObject2 != null) && (!((Boolean)localObject2).booleanValue()))
          {
            super.zzwF().zzyB().log("Collection disabled with firebase_analytics_collection_enabled=0");
            i = 0;
            continue;
          }
          if ((localObject2 == null) && (zzcem.zzqB()))
          {
            super.zzwF().zzyB().log("Collection disabled with google_app_measurement_enable=0");
            i = 0;
            continue;
          }
          super.zzwF().zzyD().log("Collection enabled");
          i = 1;
          continue;
        }
      }
      label674:
      i = 0;
    }
  }
  
  @WorkerThread
  final String zzyu()
  {
    byte[] arrayOfByte = new byte[16];
    super.zzwB().zzzt().nextBytes(arrayOfByte);
    return String.format(Locale.US, "%032x", new Object[] { new BigInteger(1, arrayOfByte) });
  }
  
  final int zzyv()
  {
    zzkD();
    return this.zzbqC;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcfg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */