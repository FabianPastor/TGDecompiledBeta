package com.google.firebase.iid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Base64;
import android.util.Log;
import com.google.android.gms.common.util.PlatformVersion;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.List;
import javax.annotation.concurrent.GuardedBy;

public final class zzw
{
  @GuardedBy("this")
  private String zzbrk;
  @GuardedBy("this")
  private String zzbrl;
  @GuardedBy("this")
  private int zzbrm;
  @GuardedBy("this")
  private int zzbrn = 0;
  private final Context zzqs;
  
  public zzw(Context paramContext)
  {
    this.zzqs = paramContext;
  }
  
  public static String zza(FirebaseApp paramFirebaseApp)
  {
    String str = paramFirebaseApp.getOptions().getGcmSenderId();
    if (str != null) {
      paramFirebaseApp = str;
    }
    for (;;)
    {
      return paramFirebaseApp;
      str = paramFirebaseApp.getOptions().getApplicationId();
      paramFirebaseApp = str;
      if (str.startsWith("1:"))
      {
        paramFirebaseApp = str.split(":");
        if (paramFirebaseApp.length < 2)
        {
          paramFirebaseApp = null;
        }
        else
        {
          str = paramFirebaseApp[1];
          paramFirebaseApp = str;
          if (str.isEmpty()) {
            paramFirebaseApp = null;
          }
        }
      }
    }
  }
  
  public static String zza(KeyPair paramKeyPair)
  {
    paramKeyPair = paramKeyPair.getPublic().getEncoded();
    try
    {
      paramKeyPair = MessageDigest.getInstance("SHA1").digest(paramKeyPair);
      paramKeyPair[0] = ((byte)(byte)((paramKeyPair[0] & 0xF) + 112));
      paramKeyPair = Base64.encodeToString(paramKeyPair, 0, 8, 11);
    }
    catch (NoSuchAlgorithmException paramKeyPair)
    {
      for (;;)
      {
        Log.w("FirebaseInstanceId", "Unexpected error, device missing required algorithms");
        paramKeyPair = null;
      }
    }
    return paramKeyPair;
  }
  
  private final PackageInfo zzey(String paramString)
  {
    try
    {
      paramString = this.zzqs.getPackageManager().getPackageInfo(paramString, 0);
      return paramString;
    }
    catch (PackageManager.NameNotFoundException paramString)
    {
      for (;;)
      {
        paramString = String.valueOf(paramString);
        Log.w("FirebaseInstanceId", String.valueOf(paramString).length() + 23 + "Failed to find package " + paramString);
        paramString = null;
      }
    }
  }
  
  private final void zzsy()
  {
    try
    {
      PackageInfo localPackageInfo = zzey(this.zzqs.getPackageName());
      if (localPackageInfo != null)
      {
        this.zzbrk = Integer.toString(localPackageInfo.versionCode);
        this.zzbrl = localPackageInfo.versionName;
      }
      return;
    }
    finally {}
  }
  
  public final int zzsu()
  {
    int i = 0;
    for (;;)
    {
      try
      {
        if (this.zzbrn != 0)
        {
          i = this.zzbrn;
          return i;
        }
        PackageManager localPackageManager = this.zzqs.getPackageManager();
        if (localPackageManager.checkPermission("com.google.android.c2dm.permission.SEND", "com.google.android.gms") == -1)
        {
          Log.e("FirebaseInstanceId", "Google Play services missing or without correct permission.");
          continue;
        }
        if (PlatformVersion.isAtLeastO()) {
          break label112;
        }
      }
      finally {}
      Object localObject2 = new android/content/Intent;
      ((Intent)localObject2).<init>("com.google.android.c2dm.intent.REGISTER");
      ((Intent)localObject2).setPackage("com.google.android.gms");
      localObject2 = ((PackageManager)localObject1).queryIntentServices((Intent)localObject2, 0);
      if ((localObject2 != null) && (((List)localObject2).size() > 0))
      {
        this.zzbrn = 1;
        i = this.zzbrn;
      }
      else
      {
        label112:
        localObject2 = new android/content/Intent;
        ((Intent)localObject2).<init>("com.google.iid.TOKEN_REQUEST");
        ((Intent)localObject2).setPackage("com.google.android.gms");
        List localList = ((PackageManager)localObject1).queryBroadcastReceivers((Intent)localObject2, 0);
        if ((localList == null) || (localList.size() <= 0)) {
          break;
        }
        this.zzbrn = 2;
        i = this.zzbrn;
      }
    }
    Log.w("FirebaseInstanceId", "Failed to resolve IID implementation package, falling back");
    if (PlatformVersion.isAtLeastO()) {}
    for (this.zzbrn = 2;; this.zzbrn = 1)
    {
      i = this.zzbrn;
      break;
    }
  }
  
  public final String zzsv()
  {
    try
    {
      if (this.zzbrk == null) {
        zzsy();
      }
      String str = this.zzbrk;
      return str;
    }
    finally {}
  }
  
  public final String zzsw()
  {
    try
    {
      if (this.zzbrl == null) {
        zzsy();
      }
      String str = this.zzbrl;
      return str;
    }
    finally {}
  }
  
  public final int zzsx()
  {
    try
    {
      if (this.zzbrm == 0)
      {
        PackageInfo localPackageInfo = zzey("com.google.android.gms");
        if (localPackageInfo != null) {
          this.zzbrm = localPackageInfo.versionCode;
        }
      }
      int i = this.zzbrm;
      return i;
    }
    finally {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/zzw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */