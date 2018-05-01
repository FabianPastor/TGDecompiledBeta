package com.google.android.gms.iid;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.util.Base64;
import android.util.Log;
import java.io.IOException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Map;

public class InstanceID
{
  private static Map<String, InstanceID> zzifg = new ArrayMap();
  private static zzo zzifh;
  private static zzl zzifi;
  private static String zzifm;
  private Context mContext;
  private KeyPair zzifj;
  private String zzifk = "";
  private long zzifl;
  
  private InstanceID(Context paramContext, String paramString, Bundle paramBundle)
  {
    this.mContext = paramContext.getApplicationContext();
    this.zzifk = paramString;
  }
  
  public static InstanceID getInstance(Context paramContext)
  {
    return getInstance(paramContext, null);
  }
  
  public static InstanceID getInstance(Context paramContext, Bundle paramBundle)
  {
    String str;
    if (paramBundle == null) {
      str = "";
    }
    for (;;)
    {
      try
      {
        Context localContext = paramContext.getApplicationContext();
        if (zzifh == null)
        {
          zzifh = new zzo(localContext);
          zzifi = new zzl(localContext);
        }
        zzifm = Integer.toString(zzdm(localContext));
        InstanceID localInstanceID = (InstanceID)zzifg.get(str);
        paramContext = localInstanceID;
        if (localInstanceID == null)
        {
          paramContext = new InstanceID(localContext, str, paramBundle);
          zzifg.put(str, paramContext);
        }
        return paramContext;
      }
      finally {}
      str = paramBundle.getString("subtype");
      while (str != null) {
        break;
      }
      str = "";
    }
  }
  
  static String zza(KeyPair paramKeyPair)
  {
    paramKeyPair = paramKeyPair.getPublic().getEncoded();
    try
    {
      paramKeyPair = MessageDigest.getInstance("SHA1").digest(paramKeyPair);
      paramKeyPair[0] = ((byte)((paramKeyPair[0] & 0xF) + 112));
      paramKeyPair = Base64.encodeToString(paramKeyPair, 0, 8, 11);
      return paramKeyPair;
    }
    catch (NoSuchAlgorithmException paramKeyPair)
    {
      Log.w("InstanceID", "Unexpected error, device missing required algorithms");
    }
    return null;
  }
  
  private final KeyPair zzave()
  {
    if (this.zzifj == null) {
      this.zzifj = zzifh.zzib(this.zzifk);
    }
    if (this.zzifj == null)
    {
      this.zzifl = System.currentTimeMillis();
      this.zzifj = zzifh.zzc(this.zzifk, this.zzifl);
    }
    return this.zzifj;
  }
  
  public static zzo zzavg()
  {
    return zzifh;
  }
  
  static int zzdm(Context paramContext)
  {
    try
    {
      int i = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0).versionCode;
      return i;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      paramContext = String.valueOf(paramContext);
      Log.w("InstanceID", String.valueOf(paramContext).length() + 38 + "Never happens: can't find own package " + paramContext);
    }
    return 0;
  }
  
  static String zzdn(Context paramContext)
  {
    try
    {
      paramContext = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0).versionName;
      return paramContext;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      paramContext = String.valueOf(paramContext);
      Log.w("InstanceID", String.valueOf(paramContext).length() + 38 + "Never happens: can't find own package " + paramContext);
    }
    return null;
  }
  
  static String zzo(byte[] paramArrayOfByte)
  {
    return Base64.encodeToString(paramArrayOfByte, 11);
  }
  
  public String getToken(String paramString1, String paramString2, Bundle paramBundle)
    throws IOException
  {
    int j = 0;
    int k = 1;
    if (Looper.getMainLooper() == Looper.myLooper()) {
      throw new IOException("MAIN_THREAD");
    }
    Object localObject = zzifh.get("appVersion");
    if ((localObject == null) || (!((String)localObject).equals(zzifm)))
    {
      i = 1;
      if (i == 0) {
        break label133;
      }
    }
    label133:
    for (localObject = null;; localObject = zzifh.zze(this.zzifk, paramString1, paramString2))
    {
      if (localObject == null) {
        break label150;
      }
      return (String)localObject;
      localObject = zzifh.get("lastToken");
      if (localObject == null)
      {
        i = 1;
        break;
      }
      long l = Long.parseLong((String)localObject);
      if (System.currentTimeMillis() / 1000L - Long.valueOf(l).longValue() > 604800L)
      {
        i = 1;
        break;
      }
      i = 0;
      break;
    }
    label150:
    localObject = paramBundle;
    if (paramBundle == null) {
      localObject = new Bundle();
    }
    int i = k;
    if (((Bundle)localObject).getString("ttl") != null) {
      i = 0;
    }
    if ("jwt".equals(((Bundle)localObject).getString("type"))) {
      i = j;
    }
    for (;;)
    {
      paramBundle = zzb(paramString1, paramString2, (Bundle)localObject);
      localObject = paramBundle;
      if (paramBundle == null) {
        break;
      }
      localObject = paramBundle;
      if (i == 0) {
        break;
      }
      zzifh.zza(this.zzifk, paramString1, paramString2, paramBundle, zzifm);
      return paramBundle;
    }
  }
  
  public final void zzavf()
  {
    this.zzifl = 0L;
    zzifh.zzhz(String.valueOf(this.zzifk).concat("|"));
    this.zzifj = null;
  }
  
  public final String zzb(String paramString1, String paramString2, Bundle paramBundle)
    throws IOException
  {
    if (paramString2 != null) {
      paramBundle.putString("scope", paramString2);
    }
    paramBundle.putString("sender", paramString1);
    if ("".equals(this.zzifk)) {}
    for (paramString2 = paramString1;; paramString2 = this.zzifk)
    {
      if (!paramBundle.containsKey("legacy.register"))
      {
        paramBundle.putString("subscription", paramString1);
        paramBundle.putString("subtype", paramString2);
        paramBundle.putString("X-subscription", paramString1);
        paramBundle.putString("X-subtype", paramString2);
      }
      return zzl.zzj(zzifi.zza(paramBundle, zzave()));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/iid/InstanceID.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */