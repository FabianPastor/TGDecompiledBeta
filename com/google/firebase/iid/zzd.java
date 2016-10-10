package com.google.firebase.iid;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import java.io.IOException;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

public class zzd
{
  static Map<String, zzd> afS = new HashMap();
  static String afY;
  private static zzg bhA;
  private static zzf bhB;
  KeyPair afV;
  String afW = "";
  Context mContext;
  
  protected zzd(Context paramContext, String paramString, Bundle paramBundle)
  {
    this.mContext = paramContext.getApplicationContext();
    this.afW = paramString;
  }
  
  public static zzd zzb(Context paramContext, Bundle paramBundle)
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
        if (bhA == null)
        {
          bhA = new zzg(localContext);
          bhB = new zzf(localContext);
        }
        afY = Integer.toString(FirebaseInstanceId.zzdg(localContext));
        zzd localzzd = (zzd)afS.get(str);
        paramContext = localzzd;
        if (localzzd == null)
        {
          paramContext = new zzd(localContext, str, paramBundle);
          afS.put(str, paramContext);
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
  
  public zzg H()
  {
    return bhA;
  }
  
  public zzf I()
  {
    return bhB;
  }
  
  public long getCreationTime()
  {
    return bhA.zztw(this.afW);
  }
  
  public String getToken(String paramString1, String paramString2, Bundle paramBundle)
    throws IOException
  {
    if (Looper.getMainLooper() == Looper.myLooper()) {
      throw new IOException("MAIN_THREAD");
    }
    Bundle localBundle = paramBundle;
    if (paramBundle == null) {
      localBundle = new Bundle();
    }
    int j = 1;
    int i;
    if ((localBundle.getString("ttl") != null) || ("jwt".equals(localBundle.getString("type")))) {
      i = 0;
    }
    do
    {
      do
      {
        paramBundle = zzc(paramString1, paramString2, localBundle);
        if ((paramBundle != null) && (i != 0)) {
          bhA.zza(this.afW, paramString1, paramString2, paramBundle, afY);
        }
        return paramBundle;
        paramBundle = bhA.zzq(this.afW, paramString1, paramString2);
        i = j;
      } while (paramBundle == null);
      i = j;
    } while (paramBundle.zztz(afY));
    return paramBundle.auj;
  }
  
  public void zzb(String paramString1, String paramString2, Bundle paramBundle)
    throws IOException
  {
    if (Looper.getMainLooper() == Looper.myLooper()) {
      throw new IOException("MAIN_THREAD");
    }
    bhA.zzi(this.afW, paramString1, paramString2);
    Bundle localBundle = paramBundle;
    if (paramBundle == null) {
      localBundle = new Bundle();
    }
    localBundle.putString("sender", paramString1);
    if (paramString2 != null) {
      localBundle.putString("scope", paramString2);
    }
    localBundle.putString("subscription", paramString1);
    localBundle.putString("delete", "1");
    localBundle.putString("X-delete", "1");
    if ("".equals(this.afW))
    {
      paramString2 = paramString1;
      localBundle.putString("subtype", paramString2);
      if (!"".equals(this.afW)) {
        break label165;
      }
    }
    for (;;)
    {
      localBundle.putString("X-subtype", paramString1);
      paramString1 = bhB.zza(localBundle, zzbop());
      bhB.zzt(paramString1);
      return;
      paramString2 = this.afW;
      break;
      label165:
      paramString1 = this.afW;
    }
  }
  
  KeyPair zzbop()
  {
    if (this.afV == null) {
      this.afV = bhA.zzks(this.afW);
    }
    if (this.afV == null) {
      this.afV = bhA.zztx(this.afW);
    }
    return this.afV;
  }
  
  public void zzboq()
  {
    bhA.zzkt(this.afW);
    this.afV = null;
  }
  
  public String zzc(String paramString1, String paramString2, Bundle paramBundle)
    throws IOException
  {
    if (paramString2 != null) {
      paramBundle.putString("scope", paramString2);
    }
    paramBundle.putString("sender", paramString1);
    if ("".equals(this.afW)) {}
    for (paramString2 = paramString1;; paramString2 = this.afW)
    {
      if (!paramBundle.containsKey("legacy.register"))
      {
        paramBundle.putString("subscription", paramString1);
        paramBundle.putString("subtype", paramString2);
        paramBundle.putString("X-subscription", paramString1);
        paramBundle.putString("X-subtype", paramString2);
      }
      paramString1 = bhB.zza(paramBundle, zzbop());
      return bhB.zzt(paramString1);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */