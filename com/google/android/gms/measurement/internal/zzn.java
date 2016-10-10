package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.text.TextUtils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzqw;
import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;

public class zzn
  extends zzaa
{
  private static final X500Principal apj = new X500Principal("CN=Android Debug,O=Android,C=US");
  private String F;
  private String G;
  private String anD;
  private String anw;
  private int apk;
  private long apl;
  private String zzcpe;
  
  zzn(zzx paramzzx)
  {
    super(paramzzx);
  }
  
  String zzbsr()
  {
    zzaax();
    return this.anw;
  }
  
  String zzbsx()
  {
    zzaax();
    return this.anD;
  }
  
  long zzbsy()
  {
    return zzbvi().zzbsy();
  }
  
  long zzbsz()
  {
    zzaax();
    return this.apl;
  }
  
  int zzbwa()
  {
    zzaax();
    return this.apk;
  }
  
  boolean zzbwb()
  {
    try
    {
      Object localObject = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 64);
      if ((localObject != null) && (((PackageInfo)localObject).signatures != null) && (((PackageInfo)localObject).signatures.length > 0))
      {
        localObject = localObject.signatures[0];
        boolean bool = ((X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(((Signature)localObject).toByteArray()))).getSubjectX500Principal().equals(apj);
        return bool;
      }
    }
    catch (CertificateException localCertificateException)
    {
      zzbvg().zzbwc().zzj("Error obtaining certificate", localCertificateException);
      return true;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;)
      {
        zzbvg().zzbwc().zzj("Package name not found", localNameNotFoundException);
      }
    }
  }
  
  protected void zzdr(Status paramStatus)
  {
    if (paramStatus == null)
    {
      zzbvg().zzbwc().log("GoogleService failed to initialize (no status)");
      return;
    }
    zzbvg().zzbwc().zze("GoogleService failed to initialize, status", Integer.valueOf(paramStatus.getStatusCode()), paramStatus.getStatusMessage());
  }
  
  AppMetadata zzmi(String paramString)
  {
    String str1 = zzti();
    String str2 = zzbsr();
    String str3 = zzyt();
    long l1 = zzbwa();
    String str4 = zzbsx();
    long l2 = zzbsy();
    long l3 = zzbsz();
    boolean bool2 = this.anq.isEnabled();
    if (!zzbvh().aqe) {}
    for (boolean bool1 = true;; bool1 = false) {
      return new AppMetadata(str1, str2, str3, l1, str4, l2, l3, paramString, bool2, bool1, zzbvh().zzbst());
    }
  }
  
  String zzti()
  {
    zzaax();
    return this.zzcpe;
  }
  
  protected void zzym()
  {
    Object localObject3 = "unknown";
    String str4 = "Unknown";
    int j = Integer.MIN_VALUE;
    String str2 = "Unknown";
    String str5 = getContext().getPackageName();
    PackageManager localPackageManager = getContext().getPackageManager();
    String str3;
    int i;
    String str1;
    Object localObject1;
    if (localPackageManager == null)
    {
      zzbvg().zzbwc().log("PackageManager is null, app identity information might be inaccurate");
      str3 = str4;
      i = j;
      str1 = str2;
      this.zzcpe = str5;
      this.anD = ((String)localObject3);
      this.G = str3;
      this.apk = i;
      this.F = str1;
      this.apl = 0L;
      localObject1 = zzal.zzfi("MD5");
      if (localObject1 != null) {
        break label484;
      }
      zzbvg().zzbwc().log("Could not get MD5 instance");
      this.apl = -1L;
      label130:
      if (!zzbvi().zzact()) {
        break label570;
      }
      localObject1 = zzqw.zzb(getContext(), "-", true);
      label152:
      if ((localObject1 == null) || (!((Status)localObject1).isSuccess())) {
        break label581;
      }
      i = 1;
      label165:
      if (i == 0) {
        zzdr((Status)localObject1);
      }
      if (i == 0) {
        break label681;
      }
      localObject1 = zzbvi().zzbud();
      if (!zzbvi().zzbuc()) {
        break label586;
      }
      zzbvg().zzbwh().log("Collection disabled with firebase_analytics_collection_deactivated=1");
      i = 0;
    }
    for (;;)
    {
      this.anw = "";
      if (!zzbvi().zzact()) {}
      try
      {
        str1 = zzqw.zzasl();
        localObject1 = str1;
        if (TextUtils.isEmpty(str1)) {
          localObject1 = "";
        }
        this.anw = ((String)localObject1);
        if (i != 0) {
          zzbvg().zzbwj().zze("App package, google app id", this.zzcpe, this.anw);
        }
        return;
      }
      catch (IllegalStateException localIllegalStateException)
      {
        Object localObject2;
        Status localStatus;
        zzbvg().zzbwc().zzj("getGoogleAppId or isMeasurementEnabled failed with exception", localIllegalStateException);
        return;
      }
      str1 = localPackageManager.getInstallerPackageName(str5);
      if (str1 == null) {
        localObject1 = "manual_install";
      }
      for (;;)
      {
        localObject3 = str2;
        str3 = str4;
        try
        {
          PackageInfo localPackageInfo = localPackageManager.getPackageInfo(getContext().getPackageName(), 0);
          str1 = str2;
          i = j;
          str3 = str4;
          localObject3 = localObject1;
          if (localPackageInfo == null) {
            break;
          }
          localObject3 = str2;
          str3 = str4;
          CharSequence localCharSequence = localPackageManager.getApplicationLabel(localPackageInfo.applicationInfo);
          str1 = str2;
          localObject3 = str2;
          str3 = str4;
          if (!TextUtils.isEmpty(localCharSequence))
          {
            localObject3 = str2;
            str3 = str4;
            str1 = localCharSequence.toString();
          }
          localObject3 = str1;
          str3 = str4;
          str2 = localPackageInfo.versionName;
          localObject3 = str1;
          str3 = str2;
          i = localPackageInfo.versionCode;
          str3 = str2;
          localObject3 = localObject1;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException2)
        {
          zzbvg().zzbwc().zzj("Error retrieving package info: appName", localObject3);
          localObject2 = localObject3;
          i = j;
          localObject3 = localObject1;
        }
        localObject1 = str1;
        if ("com.android.vending".equals(str1)) {
          localObject1 = "";
        }
      }
      break;
      label484:
      if (localPackageManager == null) {
        break label130;
      }
      try
      {
        if (zzbwb()) {
          break label130;
        }
        localObject2 = localPackageManager.getPackageInfo(getContext().getPackageName(), 64);
        if ((((PackageInfo)localObject2).signatures == null) || (((PackageInfo)localObject2).signatures.length <= 0)) {
          break label130;
        }
        this.apl = zzal.zzx(((MessageDigest)localObject1).digest(localObject2.signatures[0].toByteArray()));
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException1)
      {
        zzbvg().zzbwc().zzj("Package name not found", localNameNotFoundException1);
      }
      break label130;
      label570:
      localStatus = zzqw.zzcb(getContext());
      break label152;
      label581:
      i = 0;
      break label165;
      label586:
      if ((localStatus != null) && (!localStatus.booleanValue()))
      {
        zzbvg().zzbwh().log("Collection disabled with firebase_analytics_collection_enabled=0");
        i = 0;
      }
      else if ((localStatus == null) && (zzbvi().zzasm()))
      {
        zzbvg().zzbwh().log("Collection disabled with google_app_measurement_enable=0");
        i = 0;
      }
      else
      {
        zzbvg().zzbwj().log("Collection enabled");
        i = 1;
        continue;
        label681:
        i = 0;
      }
    }
  }
  
  String zzyt()
  {
    zzaax();
    return this.G;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */